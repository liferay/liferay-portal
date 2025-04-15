/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.authentication.facebook.connect.web.internal.servlet.taglib;

import com.liferay.portal.kernel.facebook.FacebookConnect;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.sso.facebook.connect.constants.FacebookConnectWebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * When Liferay's Sign In portlet is requested, this class adds a Facebook link
 * to the portlet if Facebook Connect authentication has been enabled for the
 * portal instance being accessed.
 *
 * @author Michael C. Han
 */
@Component(service = DynamicInclude.class)
public class FacebookConnectNavigationPreJSPDynamicInclude
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

		String strutsAction = ParamUtil.getString(
			httpServletRequest, "struts_action");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (strutsAction.startsWith("/login/facebook_connect") ||
			!_facebookConnect.isEnabled(themeDisplay.getCompanyId()) ||
			LayoutConstants.TYPE_UTILITY.equals(layout.getType())) {

			return;
		}

		String facebookAuthRedirectURL = _facebookConnect.getRedirectURL(
			themeDisplay.getCompanyId());

		httpServletRequest.setAttribute(
			FacebookConnectWebKeys.FACEBOOK_AUTH_REDIRECT_URL,
			facebookAuthRedirectURL);

		String facebookAuthURL = _facebookConnect.getAuthURL(
			themeDisplay.getCompanyId());

		httpServletRequest.setAttribute(
			FacebookConnectWebKeys.FACEBOOK_AUTH_URL, facebookAuthURL);

		String facebookAppId = _facebookConnect.getAppId(
			themeDisplay.getCompanyId());

		httpServletRequest.setAttribute(
			FacebookConnectWebKeys.FACEBOOK_APP_ID, facebookAppId);

		super.include(httpServletRequest, httpServletResponse, key);
	}

	@Override
	public void register(
		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry) {

		dynamicIncludeRegistry.register(
			"com.liferay.login.web#/navigation.jsp#pre");
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/com.liferay.login.web/navigation/facebook.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FacebookConnectNavigationPreJSPDynamicInclude.class);

	@Reference
	private FacebookConnect _facebookConnect;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.login.authentication.facebook.connect.web)"
	)
	private ServletContext _servletContext;

}