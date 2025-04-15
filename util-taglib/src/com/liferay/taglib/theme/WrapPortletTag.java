/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.theme;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.theme.ThemeUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.servlet.PipingServletResponseFactory;
import com.liferay.taglib.util.ParamAndPropertyAncestorTagImpl;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTag;

/**
 * @author Brian Wing Shun Chan
 */
public class WrapPortletTag
	extends ParamAndPropertyAncestorTagImpl implements BodyTag {

	public static String doTag(
			String wrapPage, String portletPage, ServletContext servletContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		// Portlet content

		RequestDispatcher requestDispatcher =
			DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
				servletContext, portletPage);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			httpServletResponse, unsyncStringWriter);

		requestDispatcher.include(httpServletRequest, pipingServletResponse);

		portletDisplay.setContent(unsyncStringWriter.getStringBundler());

		// Page

		String content = ThemeUtil.include(
			servletContext, httpServletRequest, httpServletResponse, wrapPage,
			themeDisplay.getTheme(), false);

		return StringBundler.concat(
			_CONTENT_WRAPPER_PRE, content, _CONTENT_WRAPPER_POST);
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			HttpServletRequest httpServletRequest = getRequest();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			// Portlet content

			portletDisplay.setContent(getBodyContentAsStringBundler());

			// Page

			ThemeUtil.include(
				getServletContext(), httpServletRequest,
				PipingServletResponseFactory.createPipingServletResponse(
					pageContext),
				getPage(), themeDisplay.getTheme());

			return EVAL_PAGE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
		finally {
			clearParams();
			clearProperties();
		}
	}

	@Override
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public void setPage(String page) {
		_page = page;
	}

	protected String getPage() {
		return _page;
	}

	private static final String _CONTENT_WRAPPER_POST = "</div>";

	private static final String _CONTENT_WRAPPER_PRE =
		"<div class=\"column-1\" id=\"main-content\" role=\"main\">";

	private String _page;

}