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

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;

import javax.swing.JComboBox;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Aug 13, 2004
 * Time: 3:17:27 PM
 */
public class BufferHelper
{
	private BufferHelper ()
	{
		// no no no
	}

	static void reloadBufferList (JComboBox combo, String excludePath)
	{
		String curr = (String) combo.getSelectedItem();

		combo.removeAllItems();

		Buffer [] buffers = jEdit.getBuffers ();

		for (int i = 0; i < buffers.length; i++) {
			Buffer buffer = buffers [i];
			String path = buffer.getPath();

			if ((excludePath != null) && (path.equals (excludePath))) {
				continue;
			}

			combo.addItem (path);

			if (path.equals (curr)) {
				combo.setSelectedItem (path);
			}
		}
	}

	static void clearOutputBuffer (String path)
	{
		if (path == null) {
			return;
		}

		Buffer buffer = jEdit.getBuffer (path);

		clearOutputBuffer (buffer);
	}

	private static void clearOutputBuffer (Buffer buffer)
	{
		if (buffer == null) {
			return;
		}

		buffer.writeLock();

		buffer.remove (0, buffer.getLength());

		buffer.writeUnlock();
	}
}
