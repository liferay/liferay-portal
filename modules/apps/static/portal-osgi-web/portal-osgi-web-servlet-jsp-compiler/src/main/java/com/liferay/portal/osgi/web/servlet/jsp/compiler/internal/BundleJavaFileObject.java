/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.jsp.compiler.internal;

import com.liferay.petra.reflect.ReflectionUtil;

import java.io.IOException;
import java.io.InputStream;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Shuyang Zhou
 */
public class BundleJavaFileObject extends BaseJavaFileObject {

	public BundleJavaFileObject(String className, URL url) {
		super(Kind.CLASS, className);

		_url = url;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return _url.openStream();
	}

	@Override
	public URI toUri() {
		try {
			return _url.toURI();
		}
		catch (URISyntaxException uriSyntaxException) {
			return ReflectionUtil.throwException(uriSyntaxException);
		}
	}

	private final URL _url;

}