/*
 * Copyright 2004 Mark Logic Corporation. All Rights Reserved.
 */
package marklogicadapter;

import xquery.AdapterOptionsPanel;
import xquery.XQueryGUI;

import javax.swing.*;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: May 29, 2004
 * Time: 6:22:40 PM
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
	private JTextField passwordField = new JTextField (XQueryGUI.getProperty (PASS_PROPERTY));


	public void _init ()
	{
		addSeparator("Mark Logic CIS Server Options");

		addComponent ("Host", hostField, GridBagConstraints.HORIZONTAL);
		addComponent ("Port", portField, GridBagConstraints.HORIZONTAL);
		addComponent ("User", usernameField, GridBagConstraints.HORIZONTAL);
		addComponent ("Password", passwordField, GridBagConstraints.HORIZONTAL);
	}

	public void _save ()
	{
		XQueryGUI.setProperty (HOST_PROPERTY, hostField.getText());
		XQueryGUI.setProperty (PORT_PROPERTY, portField.getText());
		XQueryGUI.setProperty (USER_PROPERTY, usernameField.getText());
		XQueryGUI.setProperty (PASS_PROPERTY, passwordField.getText());
	}
}
