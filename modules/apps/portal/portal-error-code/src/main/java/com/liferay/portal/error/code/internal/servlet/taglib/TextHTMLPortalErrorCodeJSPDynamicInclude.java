/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.error.code.internal.servlet.taglib;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.MapUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@Component(property = "mime.type=text/html", service = DynamicInclude.class)
public class TextHTMLPortalErrorCodeJSPDynamicInclude
	extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		httpServletResponse.setContentType(_contentType);

		super.include(httpServletRequest, httpServletResponse, key);
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

	@Override
	protected String getJspPath() {
		return "/dynamic_include/text_html.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private static final String _CHARSET = "; charset=UTF-8";

	private static final Log _log = LogFactoryUtil.getLog(
		TextHTMLPortalErrorCodeJSPDynamicInclude.class);

	private String _contentType;
	private String _key;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.portal.error.code)")
	private ServletContext _servletContext;

}