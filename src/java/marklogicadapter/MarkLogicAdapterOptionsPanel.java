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

package marklogicadapter;

import xquery.AdapterOptionsPanel;
import xquery.XQueryGUI;

import javax.swing.*;

import java.awt.*;

/**
 * The sub-panel of the JEdit/XQuery plugin that's used to
 * configure the Mark Logic Adapter.
 * @author Ron Hitchens, Mark Logic Corporation
 */
public class MarkLogicAdapterOptionsPanel extends AdapterOptionsPanel
{
	public static final String HOST_PROPERTY = "marklogic.datasource.host";
	public static final String PORT_PROPERTY = "marklogic.datasource.port";
	public static final String USER_PROPERTY = "marklogic.datasource.user";
	public static final String PASS_PROPERTY = "marklogic.datasource.pass";

	private JTextField hostField = new JTextField (XQueryGUI.getProperty (HOST_PROPERTY));
	private JTextField portField = new JTextField (XQueryGUI.getProperty (PORT_PROPERTY));
	private JTextField usernameField = new JTextField (XQueryGUI.getProperty (USER_PROPERTY));
	private JTextField passwordField = new JPasswordField (XQueryGUI.getProperty (PASS_PROPERTY));
//	private JCheckBox hostIsJndi;

	public void _init ()
	{
		addSeparator("Mark Logic Content Interaction Server Connection Options");

		addLabel (" ");
		addLabel ("Specify server host name and port");
//		addLabel ("Specify host name and port of server, or JNDI DataSource URI");
//
//		hostIsJndi = addBooleanComponent ("Host field is DataSource URI", "marklogic.datasource.isjndi", false);
//
//		hostIsJndi.addActionListener (new ActionListener() {
//			public void actionPerformed (ActionEvent e) {
//				portField.setEditable ( ! hostIsJndi.isSelected());
//			}});

//		addComponent ("Host/DataSource", hostField, GridBagConstraints.HORIZONTAL);
		addComponent ("Host", hostField, GridBagConstraints.HORIZONTAL);
		addComponent ("Port", portField, GridBagConstraints.HORIZONTAL);

		addLabel (" ");
		addLabel ("Specify user credentials");
		addComponent ("User", usernameField, GridBagConstraints.HORIZONTAL);
		addComponent ("Password", passwordField, GridBagConstraints.HORIZONTAL);

		addLabel (" ");
		addLabel (" ");
		addLabel ("Visit http://xqzone.marklogic.com for");
		addLabel ("updates and other XQuery tools.");
	}

	public void _save ()
	{
		XQueryGUI.setProperty (HOST_PROPERTY, hostField.getText());
		XQueryGUI.setProperty (PORT_PROPERTY, portField.getText());
		XQueryGUI.setProperty (USER_PROPERTY, usernameField.getText());
		XQueryGUI.setProperty (PASS_PROPERTY, passwordField.getText());
	}

	// --------------------------------------------------------

	public void addLabel (String label)
	{
		Box box = new Box (BoxLayout.X_AXIS);
		JLabel l = new JLabel (label);
		l.setMaximumSize (l.getPreferredSize());
		box.add (l);
		box.add (Box.createGlue());

		GridBagConstraints cons = new GridBagConstraints();
		cons.gridy = y++;
		cons.gridheight = 1;
		cons.gridwidth = GridBagConstraints.REMAINDER;
		cons.fill = GridBagConstraints.BOTH;
		cons.anchor = GridBagConstraints.WEST;
		cons.weightx = 1.0f;
		cons.insets = new Insets (1,0,1,0);

		gridBag.setConstraints (box,cons);

		add (box);
	}
}
