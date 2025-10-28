/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.jsp.compiler.internal;

import com.liferay.portal.kernel.zip.ZipFileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * @author Shuyang Zhou
 */
public class VfsJavaFileObject extends BaseJavaFileObject {

	public VfsJavaFileObject(String className, URL url, String entryName)
		throws MalformedURLException {

		super(Kind.CLASS, className);

		_entryName = entryName;

		String file = url.getFile();

		int index = file.indexOf(".jar");

		if (index < 0) {
			throw new MalformedURLException(
				url + " does not denote a jar file");
		}

		_file = new File(file.substring(0, index + 4));
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return ZipFileUtil.openInputStream(_file, _entryName);
	}

	@Override
	public URI toUri() {
		return _file.toURI();
	}

	private final String _entryName;
	private final File _file;

}