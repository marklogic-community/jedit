/*
 * Copyright (c)2004 Mark Logic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The use of the Apache License does not indicate that this project is
 * affiliated with the Apache Software Foundation.
 */
package com.marklogic.jedit;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.marklogic.swing.ErrorPopup;
import com.marklogic.xdbc.XDBCXQueryException;
import com.marklogic.xqrunner.XQProgressListener;
import com.marklogic.xqrunner.XQResult;
import com.marklogic.xqrunner.XQAsyncRunner;
import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


/**
 * @author Ron Hitchens, Mark Logic Corporation
 */
public class RunPanel
	implements SourcePanel, DestPanel, BufferUpdateListener, XQProgressListener, RunQueryListener
{
	private static final String SOURCE_THIS_BUFFER_PROP = "query-source.buffer-choice";
	private static final String DEST_CHOICE_PROP = "query-dest.choice";
	private static final String DEST_THIS_BUFFER_PROP = "query-dest.buffer-choice";
	private static final String DEST_AUTO_SAVE = "query-dest.auto-save";
	private static final String DEST_AUTO_RAISE = "query-dest.auto-raise";
	private static final String XML_INDENT_PROP = "query-dest.xml-indent-result";
	private static final String BUTTON_GO_PROP = "execute-button.go-value";
	private static final String BUTTON_STOP_PROP = "execute-button.stop-value";

	private View view;
	private ConnectionPanel connectionPanel;
	private DefaultErrorSource errorSource = null;

	private JPanel rootPanel;
	private JCheckBox indentCheckbox;
	private JRadioButton destThisBufferRadio;
	private JRadioButton destNewBufferRadio;
	private JCheckBox destBufferAutoSave;
	private JComboBox destBufferMode;
	private JCheckBox destBufferAutoRaise;
	private JComboBox destThisBufferCombo;
	private JButton goButton;
	private JComboBox sourceThisBufferCombo;
	private JTextField destNewBufferName;

	public RunPanel (View view, ConnectionPanel connectionPanel)
	{
		this.view = view;
		this.connectionPanel = connectionPanel;

		groupButtons();
		setDependencies();
		setDefaults();
		setListeners();
	}

	public JPanel getPanel()
	{
		return (rootPanel);
	}

	// ----------------------------------------------------------------------
	// Implementation of DestPanel

	public void setQueryResult (String body)
	{
		Buffer buffer = getTargetBuffer();

		SwingUtilities.invokeLater (new BufferLoader (view, buffer, body,
			(String) destBufferMode.getSelectedItem(),
			destBufferAutoSave.isEnabled() && destBufferAutoSave.isSelected(),
			destBufferAutoRaise.isEnabled() && destBufferAutoRaise.isSelected(),
			indentCheckbox.isEnabled() && indentCheckbox.isSelected()));

		destNewBufferRadio.setSelected (false);
		destThisBufferRadio.setSelected (true);
		destThisBufferCombo.setSelectedItem (buffer.getPath());
	}

	public void clearErrors()
	{
		if (errorSource != null) {
			errorSource.clear();
			ErrorSource.unregisterErrorSource (errorSource);
			errorSource = null;
		}
	}

	public void clearOutputBuffer ()
	{
		BufferHelper.clearOutputBuffer (getSelectedDestFilePath());
	}

	// ----------------------------------------------------------------------
	// implementation of BufferUpdateListener interface

	public void bufferRenamed (Buffer buffer, String oldPath, String newPath)
	{
		renameComboItem (sourceThisBufferCombo, oldPath, newPath);
		renameComboItem (destThisBufferCombo, oldPath, newPath);
	}

	private void renameComboItem (JComboBox combo, String oldPath, String newPath)
	{
		int count = combo.getItemCount();

		for (int i = 0; i < count; i++) {

			if (combo.getItemAt (i).equals (oldPath)) {
				boolean selected = combo.getSelectedIndex() == i;

				combo.removeItemAt (i);

				combo.addItem (newPath);

				if (selected) {
					combo.setSelectedItem (newPath);
				}

				break;
			}
		}
	}

	public void bufferUpdated (BufferUpdate bufferUpdate)
	{
		Object what = bufferUpdate.getWhat();

		if ((what == BufferUpdate.CREATED)
			|| (what == BufferUpdate.LOADED)
			|| (what == BufferUpdate.SAVED)
			|| (what == BufferUpdate.CLOSED))
		{
			reloadBufferComboBox();
		}
	}

	public void reloadBufferComboBox ()
	{
		BufferHelper.reloadBufferList (destThisBufferCombo, getSelectedSourceFilePath());
		BufferHelper.reloadBufferList (sourceThisBufferCombo, null);

		if (destThisBufferCombo.getItemCount() == 0) {
			destNewBufferRadio.doClick();
			destThisBufferRadio.setEnabled (false);
		} else {
			destThisBufferRadio.setEnabled (true);
		}
	}

	// ----------------------------------------------------------------------
	// Implementation of RunQueryListener interface

	public void runQuery ()
	{
		goButton.doClick();
	}

	// ----------------------------------------------------------------------

	public void saveProperties ()
	{
		MarkLogicPlugin.setProperty (SOURCE_THIS_BUFFER_PROP, (String) sourceThisBufferCombo.getSelectedItem());
		MarkLogicPlugin.setProperty (DEST_THIS_BUFFER_PROP, (String) destThisBufferCombo.getSelectedItem());

		MarkLogicPlugin.setProperty (XML_INDENT_PROP, Boolean.toString (indentCheckbox.isSelected()));
		MarkLogicPlugin.setProperty (DEST_AUTO_RAISE, Boolean.toString (destBufferAutoRaise.isSelected()));
		MarkLogicPlugin.setProperty (DEST_AUTO_SAVE, Boolean.toString (destBufferAutoSave.isSelected()));
	}

	private String getSelectedDestFilePath()
	{
		if (destThisBufferRadio.isSelected()) {
			return (String) destThisBufferCombo.getSelectedItem();
		}

		return (null);
	}

	public String getQueryText()
	{
		String bufferName = (String) sourceThisBufferCombo.getSelectedItem();

		if (bufferName == null) {
			throw new RuntimeException ("No query source is selected");
		}

		Buffer buffer = jEdit.getBuffer (bufferName);

		return (buffer.getText (0, buffer.getLength()));
	}

	public String getSelectedSourceFilePath ()
	{
		return (String) sourceThisBufferCombo.getSelectedItem();
	}

	// ----------------------------------------------------------------------

	protected void setDefaults()
	{
		sourceThisBufferCombo.addItem (MarkLogicPlugin.getProperty (SOURCE_THIS_BUFFER_PROP, "xxx"));
		sourceThisBufferCombo.setSelectedIndex (0);

		destThisBufferCombo.addItem (MarkLogicPlugin.getProperty (DEST_THIS_BUFFER_PROP, "yyy"));
		destThisBufferCombo.setSelectedIndex (0);

		reloadBufferComboBox();

		String dstChoice = MarkLogicPlugin.getProperty (DEST_CHOICE_PROP, "new");

		String inputPath = getSelectedSourceFilePath();

		if ((inputPath != null) && inputPath.equals (getSelectedDestFilePath())) {
			dstChoice = "new";
			destBufferAutoSave.setSelected (false);
		}

		if (dstChoice.equals ("this")) {
			destThisBufferRadio.doClick();
		} else {
			destNewBufferRadio.doClick();
		}

		indentCheckbox.setSelected (Boolean.valueOf (MarkLogicPlugin.getProperty (XML_INDENT_PROP, "false")).booleanValue());
		destBufferAutoRaise.setSelected (Boolean.valueOf (MarkLogicPlugin.getProperty (DEST_AUTO_RAISE, "false")).booleanValue());
		destBufferAutoSave.setSelected (Boolean.valueOf (MarkLogicPlugin.getProperty (DEST_AUTO_SAVE, "false")).booleanValue());

		resetRunButton();

		Mode [] modes = jEdit.getModes();

		for (int i = 0; i < modes.length; i++) {
			Mode mode = modes[i];

			destBufferMode.addItem (mode.getName());

			if (mode.getName().equals ("text")) {
				destBufferMode.setSelectedIndex (i);
			}
		}
	}

	private void groupButtons()
	{
		ButtonGroup destGroup = new ButtonGroup();

		destGroup.add (destThisBufferRadio);
		destGroup.add (destNewBufferRadio);
	}

	private void setDependencies()
	{
		destThisBufferRadio.addChangeListener (new ListenHelper.ToggleListener (destThisBufferRadio, destBufferAutoSave));
		destThisBufferRadio.addChangeListener (new ListenHelper.ToggleListener (destThisBufferRadio, destBufferAutoRaise));
		destThisBufferRadio.addChangeListener (new ListenHelper.ToggleListener (destThisBufferRadio, destThisBufferCombo));
		destNewBufferRadio.addChangeListener (new ListenHelper.ToggleListener (destNewBufferRadio, destNewBufferName));

		destThisBufferRadio.addChangeListener (new ListenHelper.PropertySetListener (destThisBufferRadio, DEST_CHOICE_PROP, "this", null));
		destNewBufferRadio.addChangeListener (new ListenHelper.PropertySetListener (destNewBufferRadio, DEST_CHOICE_PROP, "new", null));
		destBufferAutoRaise.addChangeListener (new ListenHelper.PropertySetListener (destBufferAutoRaise, DEST_AUTO_RAISE, "true", "false"));
		destBufferAutoSave.addChangeListener (new ListenHelper.PropertySetListener (destBufferAutoSave, DEST_AUTO_SAVE, "true", "false"));
	}

	private void setListeners()
	{
		destBufferMode.addActionListener (new ListenHelper.ComboCheckboxListener (destBufferMode, indentCheckbox, "xml,xsl,html"));
		destBufferMode.addActionListener (new ListenHelper.FileModeSetListener (destBufferMode, destThisBufferRadio, destThisBufferCombo));

		sourceThisBufferCombo.addItemListener (new SourceBufferItemListener());

		goButton.addActionListener (new ExecuteButtonListener (this, this, connectionPanel, this));
	}

	// ----------------------------------------------------------------------

	private class SourceBufferItemListener implements ItemListener
	{
		public void itemStateChanged (ItemEvent e)
		{
			if (e.getStateChange() != ItemEvent.SELECTED) {
				return;
			}

			Object item = e.getItem();
			Object destSelection = destThisBufferCombo.getSelectedItem();

			BufferHelper.reloadBufferList (destThisBufferCombo, (String) item);

			if (item.equals (destSelection)) {
				destNewBufferRadio.doClick();
			}
		}
	}

	// ----------------------------------------------------------------------
	// Implementation of XQProgressListener interface

	public void queryStarted (XQAsyncRunner context, Object attachment)
	{
		goButton.setText (MarkLogicPlugin.getProperty (BUTTON_STOP_PROP, "Cancel Query"));
	}

	public void queryFinished (XQAsyncRunner context, XQResult result, Object attachment)
	{
		try {
			setQueryResult (result.asString ("\n"));
		} catch (Throwable e) {
//e.printStackTrace();
			ErrorPopup.popError (getPanel(), "Setting Query Result", e.toString());
		}

		resetRunButton();
	}

	public void queryAborted (XQAsyncRunner context, Object attachment)
	{
		ErrorPopup.popError (getPanel(), "Query Aborted", "Closed connection");

		resetRunButton();
	}

	public void queryFailed (XQAsyncRunner context, Throwable t, Object attachment)
	{
		Throwable throwable = t;

		if ( ! (t instanceof XDBCXQueryException)) {
			if ((t.getCause() != null) && (t.getCause() instanceof XDBCXQueryException)) {
				throwable = t.getCause();
			}
		}

		String path = getSelectedSourceFilePath();
		String msg = (throwable == null) ? "[none]" : throwable.toString();
		int line = 0;

		if (throwable instanceof XDBCXQueryException) {
			XDBCXQueryException e = (XDBCXQueryException) throwable;

			msg = e.getFormatString();
			line = extractXdbcLineNumber (e);
		}

		if (path == null) {
			ErrorPopup.popError (getPanel(), "Query Failed", msg);
		} else {
			reportError (path, msg, line);
		}

		resetRunButton();
	}

	private int extractXdbcLineNumber (XDBCXQueryException exception)
	{
		XDBCXQueryException.Frame [] stack = exception.getStack();

		if (stack.length == 0) {
			return (0);
		}

		int line = stack [0].getLine();

		if (line > 0) {
			return (line - 1);
		} else {
			return (0);
		}
	}

	// ----------------------------------------------------------------------

	private void resetRunButton()
	{
		goButton.setText (MarkLogicPlugin.getProperty (BUTTON_GO_PROP, "Run Query"));
	}


	private void reportError (String path, String message, int line)
	{
		Buffer buffer = jEdit.getBuffer (path);
		int lineLength = buffer.getLineLength (line);

		DefaultErrorSource errorSource = getErrorSource();

		DefaultErrorSource.DefaultError error = new DefaultErrorSource.DefaultError (
			errorSource, ErrorSource.ERROR, path, line, 0, lineLength, message);

		errorSource.addError (error);
	}

	private DefaultErrorSource getErrorSource()
	{
		if (errorSource == null) {
			errorSource = new DefaultErrorSource ("Mark Logic Plugin");
			ErrorSource.registerErrorSource (errorSource);
		}

		return (errorSource);
	}

	// ----------------------------------------------------------------------

	private Buffer getTargetBuffer ()
	{
		if (destThisBufferRadio.isSelected()) {
			return (jEdit.getBuffer ((String) destThisBufferCombo.getSelectedItem()));
		}

		String newFileName = scrubString (destNewBufferName.getText());

		if (newFileName == null) {
			return (jEdit.newFile (view));
		}

		destNewBufferName.setText ("");

		return (jEdit.openFile (view, newFileName));
	}

	private String scrubString (String str)
	{
		if (str == null) {
			return (null);
		}

		String s = str.trim();

		if (s.length() == 0) {
			return (null);
		}

		return (s);
	}

	// ----------------------------------------------------------------------

	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// !!! IMPORTANT !!!
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$ ();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * !!! IMPORTANT !!!
	 * DO NOT edit this method OR call it in your code!
	 */
	private void $$$setupUI$$$ ()
	{
		rootPanel = new JPanel ();
		rootPanel.setLayout (new GridLayoutManager (4, 1, new Insets (3, 5, 0, 5), -1, -1));
		final JPanel panel1 = new JPanel ();
		panel1.setLayout (new GridLayoutManager (4, 1, new Insets (5, 5, 5, 5), -1, -1));
		rootPanel.add (panel1, new GridConstraints (1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		panel1.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (), null));
		final JLabel label1 = new JLabel ();
		label1.setText ("Send XQuery Result To");
		panel1.add (label1, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final JPanel panel2 = new JPanel ();
		panel2.setLayout (new GridLayoutManager (2, 1, new Insets (15, 0, 10, 0), -1, -1));
		panel1.add (panel2, new GridConstraints (3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		final JLabel label2 = new JLabel ();
		label2.setText ("File Mode (Data Type) of XQuery Result");
		label2.setToolTipText ("The file type (editing mode), affects syntax highlighting and validation");
		panel2.add (label2, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final JPanel panel3 = new JPanel ();
		panel3.setLayout (new GridLayoutManager (1, 3, new Insets (0, 0, 0, 0), -1, -1));
		panel2.add (panel3, new GridConstraints (1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		destBufferMode = new JComboBox ();
		destBufferMode.setEditable (false);
		panel3.add (destBufferMode, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final Spacer spacer1 = new Spacer ();
		panel3.add (spacer1, new GridConstraints (0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null));
		indentCheckbox = new JCheckBox ();
		indentCheckbox.setEnabled (false);
		indentCheckbox.setSelected (false);
		indentCheckbox.setText ("Indent XML/HTML");
		indentCheckbox.setToolTipText ("Apply XML indenting to the query output");
		panel3.add (indentCheckbox, new GridConstraints (0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final JPanel panel4 = new JPanel ();
		panel4.setLayout (new GridLayoutManager (3, 1, new Insets (5, 0, 5, 0), -1, -1));
		panel1.add (panel4, new GridConstraints (1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		destThisBufferCombo = new JComboBox ();
		destThisBufferCombo.setEnabled (false);
		destThisBufferCombo.setToolTipText ("The buffer that will receive the query result");
		panel4.add (destThisBufferCombo, new GridConstraints (1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		destThisBufferRadio = new JRadioButton ();
		destThisBufferRadio.setSelected (false);
		destThisBufferRadio.setText ("This Edit Buffer:");
		destThisBufferRadio.setToolTipText ("Send query output to a specific buffer");
		panel4.add (destThisBufferRadio, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final JPanel panel5 = new JPanel ();
		panel5.setLayout (new GridLayoutManager (1, 3, new Insets (0, 0, 0, 0), -1, -1));
		panel4.add (panel5, new GridConstraints (2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		final Spacer spacer2 = new Spacer ();
		panel5.add (spacer2, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null));
		destBufferAutoRaise = new JCheckBox ();
		destBufferAutoRaise.setEnabled (false);
		destBufferAutoRaise.setText ("Auto Select");
		destBufferAutoRaise.setToolTipText ("Make buffer active when updated with a new query result");
		panel5.add (destBufferAutoRaise, new GridConstraints (0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		destBufferAutoSave = new JCheckBox ();
		destBufferAutoSave.setEnabled (false);
		destBufferAutoSave.setSelected (false);
		destBufferAutoSave.setText ("Auto Save");
		destBufferAutoSave.setToolTipText ("Force save to disk on query completion");
		panel5.add (destBufferAutoSave, new GridConstraints (0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final JPanel panel6 = new JPanel ();
		panel6.setLayout (new GridLayoutManager (1, 2, new Insets (0, 0, 0, 0), -1, -1));
		panel1.add (panel6, new GridConstraints (2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		destNewBufferRadio = new JRadioButton ();
		destNewBufferRadio.setEnabled (true);
		destNewBufferRadio.setSelected (true);
		destNewBufferRadio.setText ("A New Edit Buffer");
		destNewBufferRadio.setToolTipText ("Create a new buffer to hold the query result");
		panel6.add (destNewBufferRadio, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		destNewBufferName = new JTextField ();
		destNewBufferName.setToolTipText ("File name to give the newly created edit buffer");
		panel6.add (destNewBufferName, new GridConstraints (0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension (150, -1), null));
		final JPanel panel7 = new JPanel ();
		panel7.setLayout (new GridLayoutManager (1, 3, new Insets (15, 5, 15, 5), -1, -1));
		rootPanel.add (panel7, new GridConstraints (2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		goButton = new JButton ();
		goButton.setText ("Run Query");
		goButton.setToolTipText ("Send the XQuery script to be executed");
		panel7.add (goButton, new GridConstraints (0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final Spacer spacer3 = new Spacer ();
		panel7.add (spacer3, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null));
		final Spacer spacer4 = new Spacer ();
		panel7.add (spacer4, new GridConstraints (0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null));
		final Spacer spacer5 = new Spacer ();
		rootPanel.add (spacer5, new GridConstraints (3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null));
		final JPanel panel8 = new JPanel ();
		panel8.setLayout (new GridLayoutManager (2, 1, new Insets (5, 5, 5, 5), -1, -1));
		rootPanel.add (panel8, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		panel8.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (), null));
		final JLabel label3 = new JLabel ();
		label3.setText ("Get XQuery Script From This Edit Buffer");
		panel8.add (label3, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		sourceThisBufferCombo = new JComboBox ();
		sourceThisBufferCombo.setEnabled (true);
		sourceThisBufferCombo.setToolTipText ("The buffer to use as the query source");
		panel8.add (sourceThisBufferCombo, new GridConstraints (1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
	}
}
