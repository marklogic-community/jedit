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
import com.marklogic.xqrunner.XQDataSource;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xqrunner.XQFactory;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import java.awt.Dimension;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ron Hitchens
 */
public class ConfigPanel
	implements ConnectionPanel
{
	private static final String HOST_NAME_PROP = "server-hostname";
	private static final String PORT_PROP = "server-port";
	private static final String DATASOURCE_PROP = "server-datasource";
	private static final String USER_PROP = "username";
	private static final String PASSWORD_PROP = "password";
	private static final String TABS_PROP = "tab-placement";

	// These fields are managed and initialized by the IntelliJ forms code
	private JTextField hostnameText;
	private JTextField portText;
	private JTextField userText;
	private JPasswordField passwordText;
	private JTextField datasourceText;
	private JRadioButton hostRadio;
	private JRadioButton datasourceRadio;
	private JPanel rootPanel;
	private JComboBox serverProfileSelector;
	private JRadioButton tabsLeftRadio;
	private JRadioButton tabsTopRadio;
	private JRadioButton tabsBottomRadio;
	private JRadioButton tabsRightRadio;

	private JTabbedPane tabbedPane;

	// -------------------------------------------------------------

	public ConfigPanel (JTabbedPane pane)
	{
		this.tabbedPane = pane;

		setDefaults();
		groupButtons();
		setDependencies();

		setListeners (pane);
	}

	public JPanel getPanel()
	{
		return (rootPanel);
	}

	// -------------------------------------------------------------

	private XQFactory factory = null;
	private XQDataSource datasource = null;

	public XQDataSource getDataSource() throws XQException
	{
		if (datasourcePropertiesUnchanged() && (datasource != null)) {
			return (datasource);
		}

		if (factory == null) {
			factory = new XQFactory ("xdbc");
		}

		String user = userText.getText();
		String pw = new String (passwordText.getPassword());

		if (datasourceRadio.isSelected()) {
			return (factory.newDataSource (datasourceText.getText(), user, pw));
		}

		return (factory.newDataSource (hostnameText.getText(),
			Integer.parseInt (portText.getText()), user, pw));
	}

	// -------------------------------------------------------------

	private Map lastValues = new HashMap();

	private boolean datasourcePropertiesUnchanged()
	{
		boolean changed = false;

		changed |= valueChanged (lastValues, "hostname", hostnameText.getText());
		changed |= valueChanged (lastValues, "port", portText.getText());
		changed |= valueChanged (lastValues, "jndiname", datasourceText.getText());
		changed |= valueChanged (lastValues, "username", userText.getText());
		changed |= valueChanged (lastValues, "password", new String (passwordText.getPassword()));

		return ( ! changed);
	}

	static boolean valueChanged (Map map, String name, String newValue)
	{
		String oldValue = (String) map.get (name);

		if ((oldValue == null) || ( ! oldValue.equals (newValue))) {
			map.put (name, newValue);
			return (true);
		}

		return (false);
	}

	// -------------------------------------------------------------

	public void saveProperties()
	{
		MarkLogicPlugin.setProperty (HOST_NAME_PROP, hostnameText.getText());
		MarkLogicPlugin.setProperty (PORT_PROP, portText.getText());
		MarkLogicPlugin.setProperty (DATASOURCE_PROP, datasourceText.getText());
		MarkLogicPlugin.setProperty (USER_PROP, userText.getText());
		MarkLogicPlugin.setProperty (PASSWORD_PROP, new String (passwordText.getPassword()));

		MarkLogicPlugin.setProperty (TABS_PROP, "" + tabbedPane.getTabPlacement());
	}

	// -------------------------------------------------------------

	private void setDefaults ()
	{
		MarkLogicPlugin.setTextFromProperty (hostnameText, HOST_NAME_PROP);
		MarkLogicPlugin.setTextFromProperty (portText, PORT_PROP);
		MarkLogicPlugin.setTextFromProperty (datasourceText, DATASOURCE_PROP);
		MarkLogicPlugin.setTextFromProperty (userText, USER_PROP);
		MarkLogicPlugin.setTextFromProperty (passwordText, PASSWORD_PROP);

		int placement = Integer.parseInt (MarkLogicPlugin.getProperty (TABS_PROP, "" + JTabbedPane.TOP));
		tabbedPane.setTabPlacement (placement);

		tabsLeftRadio.setSelected (placement == JTabbedPane.LEFT);
		tabsTopRadio.setSelected (placement == JTabbedPane.TOP);
		tabsBottomRadio.setSelected (placement == JTabbedPane.BOTTOM);
		tabsRightRadio.setSelected (placement == JTabbedPane.RIGHT);
	}

	private void groupButtons ()
	{
		ButtonGroup group = new ButtonGroup();

		group.add (hostRadio);
		group.add (datasourceRadio);

		group = new ButtonGroup();

		group.add (tabsLeftRadio);
		group.add (tabsTopRadio);
		group.add (tabsBottomRadio);
		group.add (tabsRightRadio);
	}

	private void setDependencies ()
	{
		hostRadio.addChangeListener (new ListenHelper.ToggleListener (hostRadio, hostnameText));
		hostRadio.addChangeListener (new ListenHelper.ToggleListener (hostRadio, portText));

		datasourceRadio.addChangeListener (new ListenHelper.ToggleListener (datasourceRadio, datasourceText));
	}

	private void setListeners (JTabbedPane pane)
	{
		tabsLeftRadio.addChangeListener (new ListenHelper.TabSetListener (pane, tabsLeftRadio, JTabbedPane.LEFT));
		tabsTopRadio.addChangeListener (new ListenHelper.TabSetListener (pane, tabsTopRadio, JTabbedPane.TOP));
		tabsBottomRadio.addChangeListener (new ListenHelper.TabSetListener (pane, tabsBottomRadio, JTabbedPane.BOTTOM));
		tabsRightRadio.addChangeListener (new ListenHelper.TabSetListener (pane, tabsRightRadio, JTabbedPane.RIGHT));
	}

	// -------------------------------------------------------------

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
		rootPanel.setLayout (new GridLayoutManager (5, 1, new Insets (5, 5, 5, 5), -1, -1));
		final JPanel panel1 = new JPanel ();
		panel1.setLayout (new GridLayoutManager (2, 1, new Insets (5, 3, 3, 3), -1, -1));
		rootPanel.add (panel1, new GridConstraints (1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		panel1.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (), null));
		final JLabel label1 = new JLabel ();
		label1.setText ("Connection Credentials");
		panel1.add (label1, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final JPanel panel2 = new JPanel ();
		panel2.setLayout (new GridLayoutManager (2, 2, new Insets (0, 0, 0, 0), -1, -1));
		panel1.add (panel2, new GridConstraints (1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		final JLabel label2 = new JLabel ();
		label2.setText ("User");
		label2.setToolTipText ("The user name to connect as");
		panel2.add (label2, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final JLabel label3 = new JLabel ();
		label3.setText ("Password");
		panel2.add (label3, new GridConstraints (1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		passwordText = new JPasswordField ();
		passwordText.setToolTipText ("The password for the specified user");
		panel2.add (passwordText, new GridConstraints (1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension (150, -1), new Dimension (150, -1)));
		userText = new JTextField ();
		userText.setToolTipText ("User name to connect as");
		panel2.add (userText, new GridConstraints (0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension (150, -1), new Dimension (150, -1)));
		final Spacer spacer1 = new Spacer ();
		rootPanel.add (spacer1, new GridConstraints (4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null));
		final JPanel panel3 = new JPanel ();
		panel3.setLayout (new GridLayoutManager (1, 2, new Insets (5, 3, 3, 3), -1, -1));
		rootPanel.add (panel3, new GridConstraints (2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		panel3.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (), null));
		final JLabel label4 = new JLabel ();
		label4.setEnabled (false);
		label4.setText ("Server Profile");
		panel3.add (label4, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		serverProfileSelector = new JComboBox ();
		serverProfileSelector.setEditable (true);
		serverProfileSelector.setEnabled (false);
		serverProfileSelector.setToolTipText ("Select a server profile");
		panel3.add (serverProfileSelector, new GridConstraints (0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final JPanel panel4 = new JPanel ();
		panel4.setLayout (new GridLayoutManager (2, 1, new Insets (5, 3, 3, 3), -1, -1));
		rootPanel.add (panel4, new GridConstraints (3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		panel4.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (), null));
		final JLabel label5 = new JLabel ();
		label5.setText ("Tab Placement");
		panel4.add (label5, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final JPanel panel5 = new JPanel ();
		panel5.setLayout (new GridLayoutManager (1, 4, new Insets (0, 0, 0, 0), -1, -1));
		panel4.add (panel5, new GridConstraints (1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		tabsLeftRadio = new JRadioButton ();
		tabsLeftRadio.setText ("Left");
		panel5.add (tabsLeftRadio, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		tabsTopRadio = new JRadioButton ();
		tabsTopRadio.setSelected (true);
		tabsTopRadio.setText ("Top");
		panel5.add (tabsTopRadio, new GridConstraints (0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		tabsBottomRadio = new JRadioButton ();
		tabsBottomRadio.setText ("Bottom");
		panel5.add (tabsBottomRadio, new GridConstraints (0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		tabsRightRadio = new JRadioButton ();
		tabsRightRadio.setText ("Right");
		panel5.add (tabsRightRadio, new GridConstraints (0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final JPanel panel6 = new JPanel ();
		panel6.setLayout (new GridLayoutManager (2, 1, new Insets (0, 0, 0, 0), -1, -1));
		rootPanel.add (panel6, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		panel6.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (), null));
		final JPanel panel7 = new JPanel ();
		panel7.setLayout (new GridLayoutManager (2, 1, new Insets (5, 3, 3, 3), -1, -1));
		panel6.add (panel7, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		hostRadio = new JRadioButton ();
		hostRadio.setSelected (true);
		hostRadio.setText ("Host Name and Port of Server");
		hostRadio.setToolTipText ("Use hostname/port to specify server location ");
		panel7.add (hostRadio, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final JPanel panel8 = new JPanel ();
		panel8.setLayout (new GridLayoutManager (2, 2, new Insets (0, 0, 0, 0), -1, -1));
		panel7.add (panel8, new GridConstraints (1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		hostnameText = new JTextField ();
		hostnameText.setText ("");
		hostnameText.setToolTipText ("The host name or IP address of the server");
		panel8.add (hostnameText, new GridConstraints (0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension (150, -1), null));
		final JLabel label6 = new JLabel ();
		label6.setText ("Host");
		panel8.add (label6, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final JLabel label7 = new JLabel ();
		label7.setText ("Port");
		panel8.add (label7, new GridConstraints (1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		portText = new JTextField ();
		portText.setToolTipText ("Port number of XDBC listener on server");
		panel8.add (portText, new GridConstraints (1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension (150, -1), new Dimension (120, -1)));
		final JPanel panel9 = new JPanel ();
		panel9.setLayout (new GridLayoutManager (2, 1, new Insets (5, 3, 3, 3), -1, -1));
		panel6.add (panel9, new GridConstraints (1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		datasourceRadio = new JRadioButton ();
		datasourceRadio.setText ("JNDI DataSource URI");
		panel9.add (datasourceRadio, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		final JPanel panel10 = new JPanel ();
		panel10.setLayout (new GridLayoutManager (1, 2, new Insets (0, 0, 0, 0), -1, -1));
		panel9.add (panel10, new GridConstraints (1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));
		final JLabel label8 = new JLabel ();
		label8.setText ("Datasource");
		panel10.add (label8, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
		datasourceText = new JTextField ();
		datasourceText.setEnabled (false);
		datasourceText.setToolTipText ("JNDI lookup key of DataSource object");
		panel10.add (datasourceText, new GridConstraints (0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension (150, -1), null));
	}
}
