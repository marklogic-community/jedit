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

import com.marklogic.queryhelper.Query;
import com.marklogic.queryhelper.QueryExecuter;
import com.marklogic.xdbc.XDBCConnection;
import com.marklogic.xdbc.XDBCException;
import com.marklogic.xdmp.XDMPDataSource;
import xquery.Adapter;
import xquery.AdapterException;
import xquery.XQueryGUI;

import java.util.Properties;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Adapter for the Jedit/XQuery plugin to send query requests
 * to a Mark Logic Content Interation Server engine.
 * The class loading logic in the XQuery plugin requires that the
 * package name be the same as the class name, but in lower-case.
 * @author Ron Hitchens, Mark Logic Corporation
 */
public class MarkLogicAdapter implements Adapter
{
	private Properties properties = null;
	private String baseUri = null;
	private String docUri = null;
	private String performance = "";
	private XDMPDataSource dataSource = null;

	public MarkLogicAdapter (Properties propertes)
	{
		this.properties = propertes;
	}

	/*
	 * @param the adapter properties
	 */
	public void setProperties (Properties prop) throws AdapterException
	{
		properties = prop;
		dataSource = null;
	}

	/*
	 * @param the string containing the base uri
	 */
	public void setBaseUri (String uri) throws AdapterException
	{
		baseUri = uri;
	}

	/*
	 * @param the string containing the path to the context document
	 */
	public void loadContextFromFile (String path) throws AdapterException
	{
		docUri = path;		// assume path is a CIS-resident URI
	}

	/*
	 * @param the string containing the context document
	 */
	public void loadContextFromString (String context) throws AdapterException
	{
		throw new AdapterException ("loadContextFromString is not implemented");
	}

	/*
	 * @param the string containing the XQuery
	 */
	public String evaluateFromString (String xquery) throws AdapterException
	{
		try {
			QueryExecuter executer = new QueryExecuter (getXdbcConnection());
			Query query = new Query (xquery);
			Object [] result = executer.execute (query);
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < result.length; i++) {
				String s = result[i].toString();

				if (i != 0) {
					sb.append ("\n");	// FIXME: use local line sep
				}

				sb.append (s);
			}

			return (sb.toString());
		} catch (Exception e) {
			throw new AdapterException ("cannot connect to CIS: " + e, e);
//		} catch (Throwable e) {
//			return (e.toString ());
		}
	}

	/*
	 * @param the string containing the path to the file containing the XQuery
	 */
	public String evaluateFromFile (String path) throws AdapterException
	{
		try {
			BufferedReader reader = new BufferedReader (new FileReader (path));
			StringBuffer sb = new StringBuffer();
			String line;

			while ((line = reader.readLine ()) != null) {
				sb.append (line).append ("\n");
			}

			sb.append ("\n");
			reader.close();

			return (evaluateFromString (sb.toString()));
		} catch (IOException e) {
			throw new AdapterException ("I/O error reading '" + path + "': " + e, e);
		}
	}

	/*
	 * @description enables the performance monitoring
	 */
	public void setPerformanceEnabled (boolean enabled) throws AdapterException
	{
		// ignore this for now
	}

	/*
	 * @description gets the performance
	 */
	public String getPerformance() throws AdapterException
	{
		return (performance);
	}

	private XDBCConnection getXdbcConnection()
		throws AdapterException
	{
		if (dataSource == null) {
			String host = XQueryGUI.getProperty (MarkLogicAdapterOptionsPanel.HOST_PROPERTY);
			String portStr = XQueryGUI.getProperty (MarkLogicAdapterOptionsPanel.PORT_PROPERTY);
			int port = -1;

			try {
				port = Integer.parseInt (portStr);
				dataSource = new XDMPDataSource (host, port);
			} catch (NumberFormatException e) {
				throw new AdapterException ("'" + portStr + "' is not an integer", e);
			} catch (XDBCException e) {
				throw new AdapterException ("cannot obtain DataSource", e);
			}
		}

		String user = XQueryGUI.getProperty (MarkLogicAdapterOptionsPanel.USER_PROPERTY);
		String pass = XQueryGUI.getProperty (MarkLogicAdapterOptionsPanel.PASS_PROPERTY);

		try {
			if (user == null) {
				return (dataSource.getConnection());
			} else {
				return (dataSource.getConnection (user, pass));
			}
		} catch (XDBCException e) {
			throw new AdapterException ("Cannot obtain connection", e);
		}
	}
}
