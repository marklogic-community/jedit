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

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Aug 5, 2004
 * Time: 11:23:34 AM
 */
public class HelpPanel
{
	private static final String HELP_FILE_NAME = "help.html";
	private JPanel rootPanel;
	private JEditorPane helpTextPane;

	public HelpPanel()
	{
		StringBuffer sb = new StringBuffer();

		try {
			URL url = getClass().getResource (HELP_FILE_NAME);

			helpTextPane.setPage (url);
		} catch (Exception e) {
			sb.append ("<html><head><title>Help File Error</title></head><body>");
			sb.append ("<b>Help text file read error</b><br><br>");
			sb.append ("Visit http://xqzone.marklogic.com/ for help");
			sb.append ("</body></html>");

			helpTextPane.setText (sb.toString());
		}

		helpTextPane.addHyperlinkListener (new LinkHandler (helpTextPane));
		helpTextPane.setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));
	}

	public JPanel getPanel()
	{
		return (rootPanel);
	}

	class LinkHandler implements HyperlinkListener
	{
		private JEditorPane viewer;

		public LinkHandler (JEditorPane helpTextPane)
		{
			this.viewer = helpTextPane;
		}

		public void hyperlinkUpdate (HyperlinkEvent evt)
		{
			if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
			{
				if (evt instanceof HTMLFrameHyperlinkEvent)
				{
					((HTMLDocument)viewer.getDocument())
						.processHTMLFrameHyperlinkEvent(
						(HTMLFrameHyperlinkEvent)evt);
				} else {
					URL url = evt.getURL();

					if (url != null) {
						// disallow any URLs outside the page for now
						if ((url.getHost() != null) && ( ! url.getHost().equals (""))) {
							return;
						}

						try {
							viewer.setPage (url);
						} catch (IOException e) {
							// nothing for now
						}
					}
				}
			} else if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED) {
				viewer.setCursor (Cursor.getPredefinedCursor (Cursor.HAND_CURSOR));

			} else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED) {
				viewer.setCursor (Cursor.getDefaultCursor());
			}
		}
	}


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
		rootPanel.setLayout (new GridLayoutManager (1, 1, new Insets (5, 5, 5, 5), -1, -1));
		final JScrollPane scrollPane1 = new JScrollPane ();
		scrollPane1.setHorizontalScrollBarPolicy (31);
		scrollPane1.setVerticalScrollBarPolicy (20);
		rootPanel.add (scrollPane1, new GridConstraints (0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension (260, 400), null));
		helpTextPane = new JEditorPane ();
		helpTextPane.setContentType ("text/html");
		helpTextPane.setEditable (false);
		helpTextPane.setMargin (new Insets (4, 4, 4, 4));
		helpTextPane.setText ("<html>   <head>    </head>   <body>     <p>      </p>   </body> </html> ");
		scrollPane1.setViewportView (helpTextPane);
	}

}
