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

import com.marklogic.swing.ErrorPopup;
import com.marklogic.xqrunner.XQuery;
import com.marklogic.xqrunner.XQAsyncRunner;
import com.marklogic.xqrunner.XQProgressListener;
import com.marklogic.xqrunner.XQDataSource;
import com.marklogic.xqrunner.XQException;
import com.marklogic.xqrunner.generic.AsyncRunner;
import com.marklogic.xqrunner.generic.SimpleQuery;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: Aug 13, 2004
 * Time: 2:57:04 PM
 */
public class ExecuteButtonListener implements ActionListener
{
	private XQProgressListener queryListener;
	private XQDataSource datasource = null;
	private XQAsyncRunner queryRunner = null;
	private ConnectionPanel connectionPanel;
	private SourcePanel sourcePanel;
	private DestPanel runPanel;

	public ExecuteButtonListener (SourcePanel inputPanel, DestPanel destPanel,
		ConnectionPanel connectionPanel, XQProgressListener queryListener)
	{
		this.runPanel = destPanel;
		this.connectionPanel = connectionPanel;
		this.sourcePanel = inputPanel;
		this.queryListener = queryListener;
	}

	public void actionPerformed (ActionEvent e)
	{
		XQuery query;
		XQDataSource ds;

		runPanel.clearErrors();
		runPanel.clearOutputBuffer();

		try {
			query = new SimpleQuery (sourcePanel.getQueryText());
			ds = connectionPanel.getDataSource();
		} catch (XQException e1) {
			ErrorPopup.popError (sourcePanel.getPanel(), "Cannot Get DataSource", e1.toString());
			return;
		}

		if (ds != datasource) {
			datasource = ds;
			queryRunner = new AsyncRunner (datasource);

			queryRunner.registerListener (queryListener, null);
		}

		try {
			queryRunner.startQuery (query);
		} catch (Throwable e1) {
			ErrorPopup.popError (sourcePanel.getPanel(), "Unexpected Exception", e1.toString());
		}
	}
}
