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

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;

import javax.swing.text.JTextComponent;

/**
 * @author Ron Hitchens
 */
public class MarkLogicPlugin extends EditPlugin
{
	public static final String NAME = "marklogic";
	public static final String OPTION_PREFIX = "options.marklogic.";

//	public void start ()
//	{
//		super.start ();
//
//		Log.log (Log.ERROR, this, "start");
//	}
//
//	public void stop ()
//	{
//		super.stop ();
//		Log.log (Log.ERROR, this, "stop");
//	}

	// ----------------------------------------------------------------

	static String getProperty (String propName, String defaultValue)
	{
		String value = jEdit.getProperty (OPTION_PREFIX + propName);

		if (value == null) {
			return (defaultValue);
		} else {
			return (value);
		}
	}

	static String getProperty (String propName)
	{
		return (getProperty (propName, null));
	}

	static void setProperty (String propName, String value)
	{
		jEdit.setProperty (OPTION_PREFIX + propName, value);
	}

	static void removeProperty (String propName)
	{
		jEdit.unsetProperty (OPTION_PREFIX + propName);
	}

	static void setTextFromProperty (JTextComponent field, String property)
	{
		String value = getProperty (property);

		if (value != null) {
			field.setText (value);
		}
	}
}
