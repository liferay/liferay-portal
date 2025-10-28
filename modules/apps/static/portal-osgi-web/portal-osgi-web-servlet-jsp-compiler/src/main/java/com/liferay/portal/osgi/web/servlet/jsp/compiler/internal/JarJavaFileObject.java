/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.jsp.compiler.internal;

import com.liferay.portal.kernel.zip.ZipFileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URI;

/**
 * @author Shuyang Zhou
 */
public class JarJavaFileObject extends BaseJavaFileObject {

	public JarJavaFileObject(String className, File file, String entryName) {
		super(Kind.CLASS, className);

		_file = file;
		_entryName = entryName;
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