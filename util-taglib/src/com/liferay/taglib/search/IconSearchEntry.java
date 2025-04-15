/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.search;

import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Writer;

/**
 * @author Julio Camarero
 */
public class IconSearchEntry extends TextSearchEntry {

	public static String getPage() {
		return _PAGE;
	}

	@Override
	public Object clone() {
		IconSearchEntry iconSearchEntry = new IconSearchEntry();

		BeanPropertiesUtil.copyProperties(this, iconSearchEntry);

		return iconSearchEntry;
	}

	@Override
	public String getHref() {
		return _href;
	}

	public String getIcon() {
		return _icon;
	}

	public HttpServletRequest getRequest() {
		return _httpServletRequest;
	}

	public HttpServletResponse getResponse() {
		return _httpServletResponse;
	}

	public ServletContext getServletContext() {
		if (_servletContext == null) {
			return ServletContextPool.get(PortalUtil.getServletContextName());
		}

		return _servletContext;
	}

	public boolean isToggleRowChecker() {
		return _toggleRowChecker;
	}

	@Override
	public void print(
			Writer writer, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletRequest.setAttribute(
			"liferay-ui:search-container-column-icon:href", _href);
		httpServletRequest.setAttribute(
			"liferay-ui:search-container-column-icon:icon", _icon);
		httpServletRequest.setAttribute(
			"liferay-ui:search-container-column-icon:toggleRowChecker",
			_toggleRowChecker);

		RequestDispatcher requestDispatcher =
			DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
				getServletContext(), _PAGE);

		requestDispatcher.include(
			httpServletRequest,
			new PipingServletResponse(httpServletResponse, writer));
	}

	@Override
	public void setHref(String href) {
		_href = href;
	}

	public void setIcon(String icon) {
		_icon = icon;
	}

	public void setRequest(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public void setResponse(HttpServletResponse httpServletResponse) {
		_httpServletResponse = httpServletResponse;
	}

	public void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	public void setToggleRowChecker(boolean toggleRowChecker) {
		_toggleRowChecker = toggleRowChecker;
	}

	private static final String _PAGE =
		"/html/taglib/ui/search_container/icon.jsp";

	private String _href;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private String _icon;
	private ServletContext _servletContext;
	private boolean _toggleRowChecker;

}