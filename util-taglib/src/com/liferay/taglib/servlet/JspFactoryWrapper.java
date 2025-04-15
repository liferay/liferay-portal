/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.servlet;

import com.liferay.portal.kernel.servlet.DirectServletRegistryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.jsp.JspApplicationContext;
import jakarta.servlet.jsp.JspEngineInfo;
import jakarta.servlet.jsp.JspFactory;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Shuyang Zhou
 */
public class JspFactoryWrapper extends JspFactory {

	public JspFactoryWrapper(JspFactory jspFactory) {
		_jspFactory = jspFactory;
	}

	@Override
	public JspEngineInfo getEngineInfo() {
		return _jspFactory.getEngineInfo();
	}

	@Override
	public JspApplicationContext getJspApplicationContext(
		ServletContext servletContext) {

		return _jspFactory.getJspApplicationContext(servletContext);
	}

	@Override
	public PageContext getPageContext(
		Servlet servlet, ServletRequest servletRequest,
		ServletResponse servletResponse, String errorPageURL,
		boolean needsSession, int buffer, boolean autoflush) {

		if (autoflush) {
			buffer = _JSP_WRITER_BUFFER_SIZE;
		}

		PageContext pageContext = _jspFactory.getPageContext(
			servlet, servletRequest, servletResponse, errorPageURL,
			needsSession, buffer, autoflush);

		if (_DIRECT_SERVLET_CONTEXT_ENABLED) {
			String servletPath = (String)servletRequest.getAttribute(
				WebKeys.SERVLET_PATH);

			if (servletPath != null) {
				servletRequest.removeAttribute(WebKeys.SERVLET_PATH);

				ServletContext servletContext = pageContext.getServletContext();

				String contextPath = servletContext.getContextPath();

				DirectServletRegistryUtil.putServlet(
					contextPath.concat(servletPath), servlet);
			}
		}

		return new PageContextWrapper(pageContext);
	}

	@Override
	public void releasePageContext(PageContext pageContext) {
		if (pageContext instanceof PageContextWrapper) {
			PageContextWrapper pageContextWrapper =
				(PageContextWrapper)pageContext;

			pageContext = pageContextWrapper.getWrappedPageContext();
		}

		_jspFactory.releasePageContext(pageContext);
	}

	private static final boolean _DIRECT_SERVLET_CONTEXT_ENABLED =
		GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.DIRECT_SERVLET_CONTEXT_ENABLED));

	private static final int _JSP_WRITER_BUFFER_SIZE = GetterUtil.getInteger(
		PropsUtil.get(PropsKeys.JSP_WRITER_BUFFER_SIZE));

	private final JspFactory _jspFactory;

}