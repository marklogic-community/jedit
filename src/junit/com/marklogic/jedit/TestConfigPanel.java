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

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Sep 24, 2004
 * Time: 3:16:27 PM
 */
public class TestConfigPanel extends TestCase
{
	public void testValueChanged()
	{
		Map values = new HashMap();

		assertTrue (ConfigPanel.valueChanged (values, "foo", "bar"));
		assertFalse (ConfigPanel.valueChanged (values, "foo", "bar"));
		assertTrue (ConfigPanel.valueChanged (values, "blah", "bar"));
		assertFalse (ConfigPanel.valueChanged (values, "blah", "bar"));
		assertFalse (ConfigPanel.valueChanged (values, "foo", "bar"));

		assertTrue (ConfigPanel.valueChanged (values, "foo", "blech"));
		assertFalse (ConfigPanel.valueChanged (values, "foo", "blech"));
		assertTrue (ConfigPanel.valueChanged (values, "foo", ""));
		assertTrue (ConfigPanel.valueChanged (values, "foo", null));
		assertTrue (ConfigPanel.valueChanged (values, "foo", "glurg"));
		assertFalse (ConfigPanel.valueChanged (values, "foo", "glurg"));
	}
}
