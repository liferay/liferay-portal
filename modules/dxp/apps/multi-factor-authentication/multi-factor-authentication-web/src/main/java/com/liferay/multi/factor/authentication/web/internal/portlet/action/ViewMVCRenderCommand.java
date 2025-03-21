/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.web.internal.portlet.action;

import com.liferay.multi.factor.authentication.spi.checker.browser.BrowserMFAChecker;
import com.liferay.multi.factor.authentication.web.internal.constants.MFAPortletKeys;
import com.liferay.multi.factor.authentication.web.internal.constants.MFAWebKeys;
import com.liferay.multi.factor.authentication.web.internal.policy.MFAPolicy;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Locale;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 * @author Marta Medio
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MFAPortletKeys.MFA_VERIFY,
		"mvc.command.name=/mfa_verify/view"
	},
	service = MVCRenderCommand.class
)
public class ViewMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		long mfaUserId = _getMFAUserId(renderRequest);

		if (mfaUserId == 0) {
			SessionErrors.add(renderRequest, "sessionExpired");

			return "/error.jsp";
		}

		List<BrowserMFAChecker> browserMFACheckers =
			_mfaPolicy.getAvailableBrowserMFACheckers(
				_portal.getCompanyId(renderRequest), mfaUserId);

		int mfaCheckerIndex = ParamUtil.getInteger(
			renderRequest, "mfaCheckerIndex");

		BrowserMFAChecker browserMFAChecker = null;

		if ((mfaCheckerIndex > -1) &&
			(mfaCheckerIndex < browserMFACheckers.size())) {

			browserMFAChecker = browserMFACheckers.get(mfaCheckerIndex);
		}
		else {
			browserMFAChecker = browserMFACheckers.get(0);
		}

		renderRequest.setAttribute(
			MFAWebKeys.BROWSER_MFA_CHECKER, browserMFAChecker);

		BrowserMFAChecker nextAvailableBrowserMFAChecker =
			browserMFACheckers.get(0);

		if ((mfaCheckerIndex > -1) &&
			((mfaCheckerIndex + 1) < browserMFACheckers.size())) {

			nextAvailableBrowserMFAChecker = browserMFACheckers.get(
				mfaCheckerIndex + 1);
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		renderRequest.setAttribute(
			MFAWebKeys.BROWSER_MFA_CHECKER_NAME,
			_getMFACheckerName(
				nextAvailableBrowserMFAChecker, themeDisplay.getLocale()));

		renderRequest.setAttribute(
			MFAWebKeys.BROWSER_MFA_CHECKERS, browserMFACheckers);
		renderRequest.setAttribute(MFAWebKeys.MFA_USER_ID, mfaUserId);

		return "/mfa_verify/view.jsp";
	}

	private String _getMFACheckerName(
		BrowserMFAChecker browserMFAChecker, Locale locale) {

		Class<? extends BrowserMFAChecker> clazz = browserMFAChecker.getClass();

		Bundle bundle = FrameworkUtil.getBundle(clazz);

		ResourceBundleLoader resourceBundleLoader =
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByBundleSymbolicName(
					bundle.getSymbolicName());

		if (resourceBundleLoader == null) {
			resourceBundleLoader =
				ResourceBundleLoaderUtil.getPortalResourceBundleLoader();
		}

		return GetterUtil.getString(
			ResourceBundleUtil.getString(
				resourceBundleLoader.loadResourceBundle(locale),
				clazz.getName()),
			clazz.getName());
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