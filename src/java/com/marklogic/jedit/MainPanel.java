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

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import java.awt.Dimension;
import java.awt.Insets;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * @author Ron Hitchens
 */
public class MainPanel
	implements EBComponent, RunQueryListener
{
	private static final String ML_VERSION_PROP = "plugin.com.marklogic.jedit.MarkLogicPlugin.version";

	private View view;
	private List propertyHolders = new ArrayList();
	private List bufferListeners = new ArrayList();
	private RunPanel runner;

	private JPanel rootPanel;
	private JTabbedPane tabbedPane;
	private JLabel versionLabel;
	private JButton mlLogoButton;
	private JButton xqzoneLogoButton;

	public MainPanel (View view, String position)
	{
		this.view = view;

		ConnectionPanel configPanel = new ConfigPanel (tabbedPane);

		runner = new RunPanel (view, configPanel);

		tabbedPane.add ("Run", runner.getPanel());
		tabbedPane.add ("Config", configPanel.getPanel());
		tabbedPane.add ("Help", new HelpPanel().getPanel());

		bufferListeners.add (runner);
		propertyHolders.add (runner);
		propertyHolders.add (configPanel);

		setupImages();
		setupListeners();

		if (position.equals (DockableWindowManager.FLOATING))
			rootPanel.setPreferredSize (new Dimension (450, 650));

		rootPanel.addAncestorListener (new AddRemoveListener (this));

		versionLabel.setText ("Version " + jEdit.getProperty (ML_VERSION_PROP, "x.x"));
	}

	public JPanel getPanel()
	{
		return (rootPanel);
	}

	public static JPanel newPanel (View view, String position)
	{
		MainPanel mainPanel = new MainPanel (view, position);

		registerRunQueryListener (mainPanel);

		return (mainPanel.getPanel());
	}

	// ---------------------------------------------------------------------

	private static Map registeredRunQueryListeners = new WeakHashMap();

	private static synchronized void registerRunQueryListener (MainPanel mainPanel)
	{
		registeredRunQueryListeners.put (mainPanel.getPanel(), mainPanel);
	}

	public static synchronized void notifyRunQueryListener (JPanel panel)
	{
		RunQueryListener listener = (RunQueryListener) registeredRunQueryListeners.get (panel);

		if (listener != null) {
			listener.runQuery();
		}
	}

	public void runQuery ()
	{
		runner.runQuery();
	}

	// ---------------------------------------------------------------------

	private Map bufferNames = new HashMap();

	public void handleMessage (EBMessage ebMessage)
	{
//System.err.println ("EBMessage: " + ebMessage);
		if (ebMessage instanceof BufferUpdate) {
			BufferUpdate msg = (BufferUpdate) ebMessage;
			Buffer buffer = msg.getBuffer();
			Object what = msg.getWhat();

			if (what == BufferUpdate.SAVING) {
				bufferNames.put (buffer, buffer.getPath());
			}

			if (what == BufferUpdate.CLOSED) {
				bufferNames.remove (buffer);
			}

			for (Iterator it = bufferListeners.iterator (); it.hasNext ();) {
				BufferUpdateListener listener = (BufferUpdateListener) it.next ();

				if ((what == BufferUpdate.SAVED) && (bufferNames.get (buffer) != null)) {
					listener.bufferRenamed (buffer,
						(String) bufferNames.get (buffer), buffer.getPath());
				}

				listener.bufferUpdated (msg);
			}

			if (what == BufferUpdate.SAVED) {
				bufferNames.remove (buffer);
			}
		}
	}

	// ---------------------------------------------------------------------

	private class AddRemoveListener implements AncestorListener
	{
		private EBComponent component;

		public AddRemoveListener (EBComponent component)
		{
			this.component = component;
		}

		public void ancestorAdded (AncestorEvent event)
		{
			EditBus.addToBus (component);
		}

		public void ancestorRemoved (AncestorEvent event)
		{
			for (Iterator it = propertyHolders.iterator(); it.hasNext();) {
				PropertiesPanel propertiesPanel = (PropertiesPanel) it.next ();

				propertiesPanel.saveProperties();
			}

			EditBus.removeFromBus (component);
		}

		public void ancestorMoved (AncestorEvent event)
		{
		}
	}

	// ---------------------------------------------------------------------

	private void setupImages ()
	{
		URL url = getClass().getResource ("marklogiclogo.png");

		if (url != null) {
			mlLogoButton.setText ("");
			mlLogoButton.setIcon (new ImageIcon (url));
		}

		url = getClass().getResource ("xqzonelogo.png");

		if (url != null) {
			xqzoneLogoButton.setText ("");
			xqzoneLogoButton.setIcon (new ImageIcon (url));
		}
	}

	private void setupListeners ()
	{
		xqzoneLogoButton.addActionListener (new ListenHelper.UrlLaunchListener (view, "http://xqzone.marklogic.com/"));
		mlLogoButton.addActionListener (new ListenHelper.UrlLaunchListener (view, "http://www.marklogic.com/"));
	}

	// ---------------------------------------------------------------------

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
		rootPanel.setLayout (new GridLayoutManager (1, 1, new Insets (0, 0, 0, 0), -1, -1));
		final JScrollPane scrollPane1 = new JScrollPane ();
		rootPanel.add (scrollPane1, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null));
		final JPanel panel1 = new JPanel ();
		panel1.setLayout (new GridLayoutManager (2, 1, new Insets (5, 5, 15, 5), -1, -1));
		scrollPane1.setViewportView (panel1);
		final JPanel panel2 = new JPanel ();
		panel2.setLayout (new GridLayoutManager (3, 1, new Insets (0, 2, 0, 2), -1, -1));
		panel1.add (panel2, new GridConstraints (1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null));
		panel2.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (), null));
		final JPanel panel3 = new JPanel ();
		panel3.setLayout (new GridLayoutManager (1, 3, new Insets (0, 0, 0, 0), -1, -1));
		panel2.add (panel3, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		final Spacer spacer1 = new Spacer ();
		panel3.add (spacer1, new GridConstraints (0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null));
		mlLogoButton = new JButton ();
		mlLogoButton.setIconTextGap (0);
		mlLogoButton.setMargin (new Insets (0, 0, 0, 0));
		mlLogoButton.setText ("Mark Logic");
		mlLogoButton.setToolTipText ("Built For Content");
		panel3.add (mlLogoButton, new GridConstraints (0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		xqzoneLogoButton = new JButton ();
		xqzoneLogoButton.setIconTextGap (0);
		xqzoneLogoButton.setMargin (new Insets (0, 0, 0, 0));
		xqzoneLogoButton.setText ("xq:zone");
		xqzoneLogoButton.setToolTipText ("xqzone.marklogic.com, Where XQZone gets to work");
		panel3.add (xqzoneLogoButton, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final JPanel panel4 = new JPanel ();
		panel4.setLayout (new GridLayoutManager (1, 4, new Insets (0, 0, 0, 0), -1, -1));
		panel2.add (panel4, new GridConstraints (2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		final JLabel label1 = new JLabel ();
		label1.setText ("Mark Logic jEdit Plugin");
		label1.setVerticalTextPosition (0);
		panel4.add (label1, new GridConstraints (0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		versionLabel = new JLabel ();
		versionLabel.setText ("Version X.X");
		panel4.add (versionLabel, new GridConstraints (0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final Spacer spacer2 = new Spacer ();
		panel4.add (spacer2, new GridConstraints (0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null));
		final Spacer spacer3 = new Spacer ();
		panel4.add (spacer3, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null));
		final JPanel panel5 = new JPanel ();
		panel5.setLayout (new GridLayoutManager (1, 3, new Insets (0, 0, 0, 0), -1, -1));
		panel2.add (panel5, new GridConstraints (1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		final Spacer spacer4 = new Spacer ();
		panel5.add (spacer4, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null));
		final JLabel label2 = new JLabel ();
		label2.setHorizontalAlignment (0);
		label2.setHorizontalTextPosition (0);
		label2.setText ("See xqzone.marklogic.com for updates");
		panel5.add (label2, new GridConstraints (0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final Spacer spacer5 = new Spacer ();
		panel5.add (spacer5, new GridConstraints (0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null));
		tabbedPane = new JTabbedPane ();
		panel1.add (tabbedPane, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
	}

}
