/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.error.code.internal.servlet.taglib;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.MapUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;

/**
 * @author Carlos Sierra Andrés
 */
public abstract class BasePortalErrorCodeDynamicInclude
	implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		httpServletResponse.setContentType(_contentType);

		String message = (String)httpServletRequest.getAttribute(
			JavaConstants.JAVAX_SERVLET_ERROR_MESSAGE);
		PrintWriter printWriter = httpServletResponse.getWriter();
		int statusCode = (Integer)httpServletRequest.getAttribute(
			RequestDispatcher.ERROR_STATUS_CODE);

		if (_log.isDebugEnabled()) {
			String requestURI = (String)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_SERVLET_ERROR_REQUEST_URI);
			Throwable throwable = (Throwable)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_SERVLET_ERROR_EXCEPTION);

			write(message, printWriter, requestURI, statusCode, throwable);
		}
		else {
			write(message, printWriter, statusCode);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(_key);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		String mimeType = MapUtil.getString(properties, "mime.type", null);

		if (mimeType == null) {
			throw new IllegalArgumentException("Mime type is null");
		}

		_contentType = mimeType.concat(_CHARSET);
		_key = "/errors/code.jsp#".concat(mimeType);
	}

	protected abstract void write(
		String message, PrintWriter printWriter, int statusCode);

	protected abstract void write(
		String message, PrintWriter printWriter, String requestURI,
		int statusCode, Throwable throwable);

	private static final String _CHARSET = "; charset=UTF-8";

	private static final Log _log = LogFactoryUtil.getLog(
		BasePortalErrorCodeDynamicInclude.class);

	private String _contentType;
	private String _key;

}