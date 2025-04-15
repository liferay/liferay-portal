/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import jakarta.portlet.ClientDataRequest;
import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;

import jakarta.servlet.http.Part;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.util.Collection;

/**
 * @author Dante Wang
 */
public class MockClientDataRequest
	extends MockPortletRequest implements ClientDataRequest {

	public MockClientDataRequest() {
	}

	public MockClientDataRequest(
		PortalContext portalContext, PortletContext portletContext) {

		super(portalContext, portletContext);
	}

	public MockClientDataRequest(PortletContext portletContext) {
		super(portletContext);
	}

	public String getCharacterEncoding() {
		return _characterEncoding;
	}

	public int getContentLength() {
		if (_content != null) {
			return _content.length;
		}

		return -1;
	}

	public long getContentLengthLong() {
		return getContentLength();
	}

	public String getContentType() {
		return _contentType;
	}

	public String getMethod() {
		return _method;
	}

	public Part getPart(String name) throws IOException, PortletException {
		throw new UnsupportedOperationException();
	}

	public Collection<Part> getParts() throws IOException, PortletException {
		throw new UnsupportedOperationException();
	}

	public InputStream getPortletInputStream() throws IOException {
		if (_content == null) {
			return null;
		}

		return new ByteArrayInputStream(_content);
	}

	public BufferedReader getReader() throws UnsupportedEncodingException {
		if (_content == null) {
			return null;
		}

		InputStream inputStream = new ByteArrayInputStream(_content);

		if (_characterEncoding == null) {
			return new BufferedReader(new InputStreamReader(inputStream));
		}

		return new BufferedReader(
			new InputStreamReader(inputStream, _characterEncoding));
	}

	public void setCharacterEncoding(String characterEncoding) {
		_characterEncoding = characterEncoding;
	}

	public void setContent(byte[] content) {
		_content = content;
	}

	public void setContentType(String contentType) {
		_contentType = contentType;
	}

	public void setMethod(String method) {
		_method = method;
	}

	private String _characterEncoding;
	private byte[] _content;
	private String _contentType;
	private String _method;

}