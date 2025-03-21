/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.beans;

import jakarta.portlet.ClientDataRequest;

import jakarta.servlet.http.Part;

import java.io.BufferedReader;
import java.io.InputStream;

import java.util.Collection;

/**
 * @author Neil Griffin
 */
public class DummyClientDataRequest
	extends DummyPortletRequest implements ClientDataRequest {

	public static final ClientDataRequest INSTANCE =
		new DummyClientDataRequest();

	@Override
	public String getCharacterEncoding() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getContentLength() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getContentLengthLong() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getContentType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getMethod() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Part getPart(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Part> getParts() {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream getPortletInputStream() {
		throw new UnsupportedOperationException();
	}

	@Override
	public BufferedReader getReader() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCharacterEncoding(String encoding) {
	}

}