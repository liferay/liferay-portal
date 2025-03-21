/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import com.liferay.bean.portlet.extension.ViewRenderer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.mvc.engine.ViewEngineContext;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;

import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Locale;

/**
 * @author Neil Griffin
 */
public abstract class BaseViewEngineContext implements ViewEngineContext {

	@Override
	public Locale getLocale() {
		PortletRequest portletRequest = getPortletRequest();

		return portletRequest.getLocale();
	}

	@Override
	public MediaType getMediaType() {
		if (_mediaType == null) {
			MimeResponse mimeResponse = getMimeResponse();

			String contentType = mimeResponse.getContentType();

			if (contentType == null) {
				_mediaType = MediaType.TEXT_HTML_TYPE;
			}
			else {
				String type = contentType;
				String subtype = null;

				int pos = contentType.indexOf('/');

				if (pos > 0) {
					type = contentType.substring(0, pos);
					subtype = contentType.substring(pos + 1);
				}

				_mediaType = new MediaType(
					type, subtype, mimeResponse.getCharacterEncoding());
			}
		}

		return _mediaType;
	}

	@Override
	public OutputStream getOutputStream() {
		try {
			MimeResponse mimeResponse = getMimeResponse();

			return mimeResponse.getPortletOutputStream();
		}
		catch (IOException ioException) {
			_log.error(ioException);

			return null;
		}
	}

	@Override
	public <T> T getRequest(Class<T> clazz) {
		return clazz.cast(getPortletRequest());
	}

	@Override
	public ResourceInfo getResourceInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T getResponse(Class<T> clazz) {
		return clazz.cast(getMimeResponse());
	}

	@Override
	public MultivaluedMap<String, Object> getResponseHeaders() {
		throw new UnsupportedOperationException();
	}

	@Override
	public UriInfo getUriInfo() {
		return new UriInfoImpl();
	}

	@Override
	public String getView() {
		PortletRequest portletRequest = getPortletRequest();

		return (String)portletRequest.getAttribute(ViewRenderer.VIEW_NAME);
	}

	protected abstract MimeResponse getMimeResponse();

	protected abstract PortletRequest getPortletRequest();

	private static final Log _log = LogFactoryUtil.getLog(
		BaseViewEngineContext.class);

	private MediaType _mediaType;

}