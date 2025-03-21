/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.portletext;

import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.taglib.servlet.PipingServletResponseFactory;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;

/**
 * @author Brian Wing Shun Chan
 */
public class PreviewTag extends IncludeTag {

	public static void doTag(
			String portletName, String queryString, boolean showBorders,
			String width, ServletContext servletContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		doTag(
			_PAGE, portletName, queryString, showBorders, width, servletContext,
			httpServletRequest, httpServletResponse);
	}

	public static void doTag(
			String page, String portletName, String queryString,
			boolean showBorders, String width, ServletContext servletContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletRequest.setAttribute(
			"liferay-portlet:preview:portletName", portletName);
		httpServletRequest.setAttribute(
			"liferay-portlet:preview:queryString", queryString);
		httpServletRequest.setAttribute(
			"liferay-portlet:preview:showBorders", String.valueOf(showBorders));
		httpServletRequest.setAttribute("liferay-portlet:preview:width", width);

		RequestDispatcher requestDispatcher =
			DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
				servletContext, page);

		requestDispatcher.include(httpServletRequest, httpServletResponse);
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			doTag(
				getPage(), _portletName, _queryString, _showBorders, _width,
				getServletContext(), getRequest(),
				PipingServletResponseFactory.createPipingServletResponse(
					pageContext));

			return EVAL_PAGE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
	}

	public String getPortletName() {
		return _portletName;
	}

	public String getQueryString() {
		return _queryString;
	}

	public String getWidth() {
		return _width;
	}

	public boolean isShowBorders() {
		return _showBorders;
	}

	public void setPortletName(String portletName) {
		_portletName = portletName;
	}

	public void setQueryString(String queryString) {
		_queryString = queryString;
	}

	public void setShowBorders(boolean showBorders) {
		_showBorders = showBorders;
	}

	public void setWidth(String width) {
		_width = width;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	private static final String _PAGE = "/html/taglib/portlet/preview/page.jsp";

	private String _portletName;
	private String _queryString;
	private boolean _showBorders;
	private String _width;

}