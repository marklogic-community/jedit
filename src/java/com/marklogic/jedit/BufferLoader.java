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

import xmlindenter.XmlIndenterPlugin;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Buffer;

/**
 * This class is needed because changes to edit buffers
 * can only be done by the AWT thread.  An instance of
 * this class is created and handed to the Swing invokeLater()
 * method which will run it in the AWT thread as soon as
 * possible.
 */
class BufferLoader implements Runnable
{
	private View view;
	private Buffer buffer;
	private String body;
	private String mode;
	private boolean autoSave;
	private boolean autoRaise;
	private boolean autoIndent;

	public BufferLoader (View view, Buffer buffer, String body,
		String mode, boolean autoSave, boolean autoRaise, boolean autoIndent)
	{
		this.view = view;
		this.buffer = buffer;
		this.body = body;
		this.mode = mode;
		this.autoSave = autoSave;
		this.autoRaise = autoRaise;
		this.autoIndent = autoIndent;
	}

	public void run ()
	{
		buffer.writeLock ();

		if (buffer.getLength() > 0) {
			buffer.remove (0, buffer.getLength());
		}

		buffer.insert (0, body);

		buffer.writeUnlock ();

		buffer.setMode (mode);

		if (autoSave) {
			buffer.save (view, buffer.getPath());
		}

		if (autoRaise) {
			view.setBuffer (buffer);
		}

		if (autoIndent) {
			view.setBuffer (buffer);
			XmlIndenterPlugin.indentXml (view);
		}
	}
}
