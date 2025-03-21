/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.web.internal.portlet.action;

import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.multi.factor.authentication.spi.checker.browser.BrowserMFAChecker;
import com.liferay.multi.factor.authentication.web.internal.constants.MFAPortletKeys;
import com.liferay.multi.factor.authentication.web.internal.constants.MFAWebKeys;
import com.liferay.multi.factor.authentication.web.internal.policy.MFAPolicy;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 * @author Marta Medio
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MFAPortletKeys.MFA_VERIFY,
		"mvc.command.name=/mfa_verify/verify"
	},
	service = MVCActionCommand.class
)
public class VerifyMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long mfaUserId = _getMFAUserId(actionRequest);

		if (mfaUserId == 0) {
			SessionErrors.add(actionRequest, "sessionExpired");

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			actionResponse.setRenderParameter("mvcRenderCommandName", "/");

			return;
		}

		List<BrowserMFAChecker> browserMFACheckers =
			_mfaPolicy.getAvailableBrowserMFACheckers(
				_portal.getCompanyId(actionRequest), mfaUserId);

		int mfaCheckerIndex = ParamUtil.getInteger(
			actionRequest, "mfaCheckerIndex");

		BrowserMFAChecker browserMFAChecker = null;

		if ((mfaCheckerIndex > -1) &&
			(mfaCheckerIndex < browserMFACheckers.size())) {

			browserMFAChecker = browserMFACheckers.get(mfaCheckerIndex);
		}
		else {
			browserMFAChecker = browserMFACheckers.get(0);
		}

		try {
			if (!browserMFAChecker.verifyBrowserRequest(
					_portal.getHttpServletRequest(actionRequest),
					_portal.getHttpServletResponse(actionResponse),
					mfaUserId)) {

				hideDefaultErrorMessage(actionRequest);

				SessionErrors.add(actionRequest, "mfaVerificationFailed");
			}
			else {
				actionRequest.setAttribute(
					WebKeys.REDIRECT,
					PortletURLBuilder.createActionURL(
						_portal.getLiferayPortletResponse(actionResponse),
						LoginPortletKeys.LOGIN
					).setActionName(
						"/login/login"
					).setRedirect(
						ParamUtil.getString(actionRequest, "redirect")
					).setParameter(
						"saveLastPath", Boolean.FALSE
					).setParameter(
						"state", ParamUtil.getString(actionRequest, "state")
					).buildString());
			}
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, exception.getClass(), exception);
		}
	}

	private long _getMFAUserId(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (themeDisplay.isSignedIn()) {
			return themeDisplay.getUserId();
		}

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(portletRequest));

		HttpSession httpSession = httpServletRequest.getSession();

		return GetterUtil.getLong(
			httpSession.getAttribute(MFAWebKeys.MFA_USER_ID));
	}

	@Reference
	private MFAPolicy _mfaPolicy;

	@Reference
	private Portal _portal;

}