/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.beans;

import jakarta.portlet.ActionURL;
import jakarta.portlet.CacheControl;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderURL;
import jakarta.portlet.ResourceURL;

import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.Locale;

/**
 * @author Neil Griffin
 */
public class DummyMimeResponse
	extends DummyPortletResponse implements MimeResponse {

	public static final MimeResponse INSTANCE = new DummyMimeResponse();

	@Override
	public <T extends PortletURL & ActionURL> T createActionURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ActionURL createActionURL(Copy copy) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends PortletURL & RenderURL> T createRenderURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public RenderURL createRenderURL(Copy copy) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResourceURL createResourceURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flushBuffer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getBufferSize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CacheControl getCacheControl() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCharacterEncoding() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getContentType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException();
	}

	@Override
	public OutputStream getPortletOutputStream() {
		throw new UnsupportedOperationException();
	}

	@Override
	public PrintWriter getWriter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCommitted() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void resetBuffer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBufferSize(int size) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setContentType(String contentType) {
		throw new UnsupportedOperationException();
	}

}