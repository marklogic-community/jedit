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

import infoviewer.InfoViewerPlugin;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Aug 3, 2004
 * Time: 11:57:47 AM
 */
public class ListenHelper
{
	private ListenHelper()
	{
		// private
	}

	public static class ToggleListener implements ChangeListener
	{
		private JRadioButton radio;
		private Component component;

		public ToggleListener (JRadioButton radio, Component component)
		{
			this.radio = radio;
			this.component = component;
		}

		public void stateChanged (ChangeEvent e)
		{
			component.setEnabled (radio.isSelected());
		}
	}

	public static class PropertySetListener implements ChangeListener
	{
		private AbstractButton button;
		private String propName;
		private String selectedValue;
		private String unselectedValue;

		public PropertySetListener (AbstractButton button, String propName, String selectedValue, String unselectedValue)
		{
			this.button = button;
			this.propName = propName;
			this.selectedValue = selectedValue;
			this.unselectedValue = unselectedValue;
		}

		public void stateChanged (ChangeEvent e)
		{
			if (button.isSelected()) {
				MarkLogicPlugin.setProperty (propName, selectedValue);
			} else {
				if (unselectedValue == null) {
					MarkLogicPlugin.removeProperty (propName);
				} else {
					MarkLogicPlugin.setProperty (propName, unselectedValue);
				}
			}
		}
	}

	public static class UrlLaunchListener implements ActionListener
	{
		private View view;
		private String url;

		public UrlLaunchListener (View view, String url)
		{
			this.view = view;
			this.url = url;
		}

		public void actionPerformed (ActionEvent e)
		{
			InfoViewerPlugin.openURL (view, url);
		}
	}

	public static class TabSetListener implements ChangeListener
	{
		private JTabbedPane pane;
		private JRadioButton radio;
		private int location;

		public TabSetListener (JTabbedPane pane, JRadioButton radio, int location)
		{
			this.pane = pane;
			this.radio = radio;
			this.location = location;
		}

		public void stateChanged (ChangeEvent e)
		{
			if (radio.isSelected()) {
				pane.setTabPlacement (location);
			}
		}
	}

	public static class ComboCheckboxListener implements ActionListener
	{
		private JComboBox combo;
		private AbstractButton button;
		private Set values = new HashSet();

		public ComboCheckboxListener (JComboBox combo, AbstractButton button, String value)
		{
			this.combo = combo;
			this.button = button;

			String [] valArray = value.split ("\\s*,\\s*");

			for (int i = 0; i < valArray.length; i++) {
				String s = valArray[i];

				values.add (s);
			}
		}

		public void actionPerformed (ActionEvent e)
		{
			button.setEnabled (values.contains (combo.getSelectedItem()));
		}
	}

	public static class FileModeSetListener implements ActionListener
	{
		private JComboBox modeCombo;
		private JRadioButton bufferRadio;
		private JComboBox bufferCombo;

		public FileModeSetListener (JComboBox modeCombo, JRadioButton bufferRadio, JComboBox bufferCombo)
		{
			this.modeCombo = modeCombo;
			this.bufferRadio = bufferRadio;
			this.bufferCombo = bufferCombo;
		}

		public void actionPerformed (ActionEvent e)
		{
			if (e.getID() != ActionEvent.ACTION_PERFORMED) {
				return;
			}

			if ( ! bufferRadio.isSelected()) {
				return;
			}

			Buffer buffer = jEdit.getBuffer ((String) bufferCombo.getSelectedItem());

			if (buffer != null) {
				buffer.setMode ((String) modeCombo.getSelectedItem());
			}
		}
	}
}
