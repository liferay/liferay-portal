/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.taglib.servlet.taglib;

import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.portal.kernel.captcha.Captcha;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.RenderLayoutContentThreadLocal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.servlet.PipingServletResponseFactory;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;

/**
 * @author Brian Wing Shun Chan
 */
public class CaptchaTag extends IncludeTag {

	@Override
	public int doEndTag() throws JspException {
		callSetAttributes();

		try {
			if (RenderLayoutContentThreadLocal.isRenderLayoutContent()) {
				return EVAL_PAGE;
			}

			Captcha captcha = CaptchaUtil.getCaptcha();

			captcha.render(
				getRequest(),
				PipingServletResponseFactory.createPipingServletResponse(
					pageContext));

			return EVAL_PAGE;
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}

			throw new JspException(ioException);
		}
		finally {
			doClearTag();
		}
	}

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

	private static final Log _log = LogFactoryUtil.getLog(CaptchaTag.class);

	private static final Snapshot<ServletContext> _servletContextSnapshot =
		new Snapshot<>(
			CaptchaTag.class, ServletContext.class,
			"(osgi.web.symbolicname=com.liferay.captcha.taglib)");

	private String _errorMessage;
	private String _url;

}