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

import javax.swing.filechooser.FileFilter;

import java.util.Set;
import java.util.HashSet;
import java.io.File;

/**
 * @author Ron Hitchens
 */
public class GenericFileFilter extends FileFilter
{
	private String description;
	Set suffixes = new HashSet();

	public GenericFileFilter (String description, String suffixCsv)
	{
		String [] suffixArray = suffixCsv.split ("\\s*,\\s*");

		for (int i = 0; i < suffixArray.length; i++) {
			String suffix = suffixArray [i];

			suffixes.add (suffix);
		}

		this.description = description;
	}

	public String getDescription ()
	{
		return (description);
	}

	public boolean accept (File file)
	{
		if (file.isDirectory()) {
			return (true);
		}

		return (suffixes.contains (getSuffix (file.getPath())));
	}

	private String getSuffix (String path)
	{
		int dot = path.lastIndexOf ('.');

		if ((dot < 0) || (dot == (path.length() - 1))) {
			return ("");
		}

		return (path.substring (dot + 1).toLowerCase());
	}
}
