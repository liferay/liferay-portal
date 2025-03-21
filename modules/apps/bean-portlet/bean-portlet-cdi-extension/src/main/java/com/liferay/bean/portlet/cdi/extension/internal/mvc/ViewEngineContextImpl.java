/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import com.liferay.bean.portlet.extension.ViewRenderer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.mvc.Models;
import jakarta.mvc.engine.ViewEngineContext;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;

import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Locale;

/**
 * @author Neil Griffin
 */
public class ViewEngineContextImpl implements ViewEngineContext {

	public ViewEngineContextImpl(
		Configuration configuration, Locale locale, MimeResponse mimeResponse,
		Models models, PortletRequest portletRequest) {

		_configuration = configuration;
		_locale = locale;
		_mimeResponse = mimeResponse;
		_models = models;
		_portletRequest = portletRequest;
	}

	@Override
	public Configuration getConfiguration() {
		return _configuration;
	}

	@Override
	public Locale getLocale() {
		return _locale;
	}

	@Override
	public MediaType getMediaType() {
		if (_mediaType == null) {
			String contentType = _mimeResponse.getContentType();

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
					type, subtype, _mimeResponse.getCharacterEncoding());
			}
		}

		return _mediaType;
	}

	@Override
	public Models getModels() {
		return _models;
	}

	@Override
	public OutputStream getOutputStream() {
		try {
			return _mimeResponse.getPortletOutputStream();
		}
		catch (IOException ioException) {
			_log.error(ioException);

			return null;
		}
	}

	@Override
	public <T> T getRequest(Class<T> type) {
		return type.cast(_portletRequest);
	}

	@Override
	public ResourceInfo getResourceInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T getResponse(Class<T> type) {
		return type.cast(_mimeResponse);
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
		return (String)_portletRequest.getAttribute(ViewRenderer.VIEW_NAME);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewEngineContextImpl.class);

	private final Configuration _configuration;
	private final Locale _locale;
	private MediaType _mediaType;
	private final MimeResponse _mimeResponse;
	private final Models _models;
	private final PortletRequest _portletRequest;

}