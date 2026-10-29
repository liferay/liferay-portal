/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.servlet.taglib;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alvaro Saugar
 */
@Component(
	property = {
		"dynamic.include.key=com.liferay.configuration.admin.web#/edit_configuration.jsp#com.liferay.cookies.configuration.banner.CookiesBannerConfiguration#pre",
		"dynamic.include.key=com.liferay.configuration.admin.web#/edit_configuration.jsp#com.liferay.cookies.configuration.consent.CookiesConsentConfiguration#pre",
		"dynamic.include.key=com.liferay.configuration.admin.web#/edit_configuration.jsp#com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration#pre"
	},
	service = DynamicInclude.class
)
public class CookiesConfigurationActivationWarnJSPDynamicInclude
	extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/cookies_banner_configuration_warn.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CookiesConfigurationActivationWarnJSPDynamicInclude.class);

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.cookies.banner.web)"
	)
	private ServletContext _servletContext;

}