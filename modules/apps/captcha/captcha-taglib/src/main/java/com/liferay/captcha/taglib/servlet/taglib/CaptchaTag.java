/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.taglib.servlet.taglib;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Brian Wing Shun Chan
 */
public class CaptchaTag extends IncludeTag {

	public String getErrorMessage() {
		return _errorMessage;
	}

	public String getUrl() {
		return _url;
	}

	public void setErrorMessage(String errorMessage) {
		_errorMessage = errorMessage;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(_servletContextSnapshot.get());
	}

	public void setUrl(String url) {
		_url = url;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_errorMessage = null;
		_url = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-captcha:captcha:errorMessage", _errorMessage);
		httpServletRequest.setAttribute(
			"liferay-captcha:captcha:url", _getURL(httpServletRequest));
	}

	private String _getURL(HttpServletRequest httpServletRequest) {
		if (Validator.isNotNull(_url)) {
			return _url;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String url = themeDisplay.getPathMain() + "/portal/captcha/get_image";

		String portletId = PortalUtil.getPortletId(httpServletRequest);

		if (Validator.isNotNull(portletId)) {
			url += "?portletId=" + portletId;
		}

		return url;
	}

	private static final String _PAGE = "/captcha/page.jsp";

	private static final Snapshot<ServletContext> _servletContextSnapshot =
		new Snapshot<>(
			CaptchaTag.class, ServletContext.class,
			"(osgi.web.symbolicname=com.liferay.captcha.taglib)");

	private String _errorMessage;
	private String _url;

}