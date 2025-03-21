/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.authentication.openid.connect.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnect;
import com.liferay.portal.security.sso.openid.connect.constants.OpenIdConnectWebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = {
		"auth.token.ignore.mvc.action=true",
		"jakarta.portlet.name=" + PortletKeys.FAST_LOGIN,
		"jakarta.portlet.name=" + PortletKeys.LOGIN,
		"mvc.command.name=" + OpenIdConnectWebKeys.OPEN_ID_CONNECT_RESPONSE_ACTION_NAME
	},
	service = MVCActionCommand.class
)
public class OpenIdConnectLoginResponseMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	public void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!_openIdConnect.isEnabled(themeDisplay.getCompanyId())) {
			return;
		}

		String error = ParamUtil.getString(httpServletRequest, "error");

		if (Validator.isNotNull(error)) {
			if (ArrayUtil.contains(_ERRORS, error)) {
				SessionErrors.add(actionRequest, error);
			}
			else {
				SessionErrors.add(actionRequest, "unknownError");
			}

			actionResponse.setRenderParameter(
				"mvcRenderCommandName",
				OpenIdConnectWebKeys.OPEN_ID_CONNECT_REQUEST_ACTION_NAME);
		}
		else {
			String redirect = ParamUtil.getString(actionRequest, "redirect");

			if (!Validator.isBlank(redirect)) {
				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else {
				actionResponse.sendRedirect(themeDisplay.getPathMain());
			}
		}
	}

	private static final String[] _ERRORS = {
		UserEmailAddressException.MustNotUseCompanyMx.class.getSimpleName(),
		"StrangersNotAllowedException"
	};

	@Reference
	private OpenIdConnect _openIdConnect;

	@Reference
	private Portal _portal;

}