/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.jsp.compiler.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;

import javax.tools.JavaFileObject;

/**
 * @author Shuyang Zhou
 */
public abstract class BaseJavaFileObject implements JavaFileObject {

	public BaseJavaFileObject(Kind kind, String className) {
		this.kind = kind;
		this.className = className;
	}

	@Override
	public boolean delete() {
		return false;
	}

	@Override
	public Modifier getAccessLevel() {
		return null;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		throw new UnsupportedOperationException();
	}

	public String getClassName() {
		return className;
	}

	@Override
	public Kind getKind() {
		return kind;
	}

	@Override
	public long getLastModified() {
		return 0;
	}

	@Override
	public String getName() {
		String simpleName = null;

		int index = className.lastIndexOf('.');

		if (index >= 0) {
			simpleName = className.substring(index + 1);
		}
		else {
			simpleName = className;
		}

		return simpleName.concat(kind.extension);
	}

	@Override
	public NestingKind getNestingKind() {
		return null;
	}

	@Override
	public boolean isNameCompatible(String simpleName, Kind kind) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @throws IOException
	 */
	@Override
	public InputStream openInputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public OutputStream openOutputStream() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Reader openReader(boolean ignoreEncodingErrors) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Writer openWriter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(4);

		Class<?> clazz = getClass();

		sb.append(clazz.getName());

		sb.append(StringPool.OPEN_BRACKET);
		sb.append(toUri());
		sb.append(StringPool.CLOSE_BRACKET);

		return sb.toString();
	}

	protected final String className;
	protected final Kind kind;

}