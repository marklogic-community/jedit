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
package com.marklogic.swing;

import javax.swing.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ron Hitchens
 */
public class GenericFileChooser
{
	static final File [] emptyFileList = new File [0];
	private JFileChooser chooser;

	public GenericFileChooser (String approveButtonText, boolean multiSelect)
	{
		chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled (multiSelect);

		if (approveButtonText != null) {
			setApproveButtonText (approveButtonText);
		}

//		addFileFilters();
	}

	public GenericFileChooser (String approveButtonText)
	{
		this (approveButtonText, false);
	}

	public GenericFileChooser ()
	{
		this ("Select", false);
	}

	// ------------------------------------------------------------

	public void setDialogTitle (String text)
	{
		chooser.setDialogTitle (text);
	}

	public void setApproveButtonText (String text)
	{
		chooser.setApproveButtonText (text);
	}

	public void setApproveButtonToolTipText (String text)
	{
		chooser.setApproveButtonToolTipText (text);
	}

	public void setMultiSelectionEnabled (boolean value)
	{
		chooser.setMultiSelectionEnabled (value);
	}

	public void addFileFilter (String description, String suffixCsv)
	{
		chooser.addChoosableFileFilter (new GenericFileFilter (description, suffixCsv));
	}

	// ------------------------------------------------------------

	public File [] chooseFiles()
	{
		File [] selectedFiles = null;

		int option = chooser.showOpenDialog (new JFrame());

		if (option == JFileChooser.APPROVE_OPTION) {
			selectedFiles = chooser.getSelectedFiles();
		}

		if ((selectedFiles == null) || (selectedFiles.length == 0)) {
			return (emptyFileList);
		}

		List list = new ArrayList();

		for (int i = 0; i < selectedFiles.length; i++) {
			File file = selectedFiles [i];

			if ( ! file.isFile()) {
				continue;
			}

			list.add (file);
		}

		if (list.size() == 0) {
			return (emptyFileList);
		}

		selectedFiles = new File [list.size ()];

		list.toArray (selectedFiles);

		return (selectedFiles);
	}

	// ------------------------------------------------------------

//	private void addFileFilters ()
//	{
//		chooser.addChoosableFileFilter (new GenericFileFilter ("XML Files (.xml,.xhtml,.xsd,.svg)", "xml,xhtml,xht,xhtm,xsd,xsl,xslt,jdo,svg"));
//		chooser.addChoosableFileFilter (new GenericFileFilter ("HTML Files (.html,.htm)", "html,htm,css"));
//		chooser.addChoosableFileFilter (new GenericFileFilter ("SGML Files (.sgml,.sgm)", "sgml,sgm"));
//		chooser.addChoosableFileFilter (new GenericFileFilter ("XML, HTML and SGML files", "xml,xhtml,xht,xhtm,xsd,html,htm,sgml,sgm"));
//		chooser.addChoosableFileFilter (new GenericFileFilter ("PDF files", "pdf"));
//		chooser.addChoosableFileFilter (new GenericFileFilter ("Images files", "jpeg,jpg,gif,png"));
//		chooser.addChoosableFileFilter (new GenericFileFilter ("MS Office Files (.doc,.dot,.rtf,.xls,.ppt)", "doc,dot,rtf,xls,ppt"));
//		chooser.addChoosableFileFilter (new GenericFileFilter ("OpenOffice Files (.swx,.sxc,.sxd,.sxi)", "swx,stw,sxc,stc,sxd,std,sxi,sti"));
//		chooser.addChoosableFileFilter (new GenericFileFilter ("MS Office and OpenOffice", "doc,dot,rtf,xls,ppt,swx,stw,sxc,stc,sxd,std,sxi,sti"));
//	}
}
