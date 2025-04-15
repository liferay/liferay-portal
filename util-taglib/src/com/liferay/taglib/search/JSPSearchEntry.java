/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.search;

import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Writer;

/**
 * @author Brian Wing Shun Chan
 */
public class JSPSearchEntry extends SearchEntry {

	@Override
	public Object clone() {
		JSPSearchEntry jspSearchEntry = new JSPSearchEntry();

		BeanPropertiesUtil.copyProperties(this, jspSearchEntry);

		return jspSearchEntry;
	}

	public String getHref() {
		return _href;
	}

	public String getPath() {
		return _path;
	}

	public HttpServletRequest getRequest() {
		return _httpServletRequest;
	}

	public HttpServletResponse getResponse() {
		return _httpServletResponse;
	}

	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void print(
			Writer writer, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletRequest.setAttribute(WebKeys.SEARCH_ENTRY_HREF, getHref());

		if (_servletContext != null) {
			RequestDispatcher requestDispatcher =
				DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
					_servletContext, _path);

			requestDispatcher.include(
				_httpServletRequest,
				new PipingServletResponse(httpServletResponse, writer));
		}
		else {
			RequestDispatcher requestDispatcher =
				httpServletRequest.getRequestDispatcher(_path);

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}

		httpServletRequest.removeAttribute(WebKeys.SEARCH_ENTRY_HREF);
	}

	public void setHref(String href) {
		_href = href;
	}

	public void setPath(String path) {
		_path = path;
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

	private String _href;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private String _path;
	private ServletContext _servletContext;

}