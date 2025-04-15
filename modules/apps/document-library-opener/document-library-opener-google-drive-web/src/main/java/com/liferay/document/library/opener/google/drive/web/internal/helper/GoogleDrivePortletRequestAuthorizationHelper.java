/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.google.drive.web.internal.helper;

import com.liferay.document.library.opener.google.drive.web.internal.DLOpenerGoogleDriveManager;
import com.liferay.document.library.opener.google.drive.web.internal.oauth.OAuth2StateUtil;
import com.liferay.document.library.opener.oauth.OAuth2State;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = GoogleDrivePortletRequestAuthorizationHelper.class)
public class GoogleDrivePortletRequestAuthorizationHelper {

	public void performAuthorizationFlow(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws IOException, PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String state = PwdGenerator.getPassword(5);

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(portletRequest));

		OAuth2StateUtil.save(
			originalHttpServletRequest,
			new OAuth2State(
				themeDisplay.getUserId(), _getSuccessURL(portletRequest),
				_getFailureURL(portletRequest), state));

		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(portletResponse);

		String authorizationURL =
			_dlOpenerGoogleDriveManager.getAuthorizationURL(
				themeDisplay.getCompanyId(), state,
				OAuth2StateUtil.getRedirectURI(
					_portal.getPortalURL(portletRequest)));

		if (!_dlOpenerGoogleDriveManager.hasValidCredential(
				themeDisplay.getCompanyId(), themeDisplay.getUserId())) {

			authorizationURL = HttpComponentsUtil.setParameter(
				authorizationURL, "prompt", "select_account");
		}

		httpServletResponse.sendRedirect(authorizationURL);
	}

	private String _getFailureURL(PortletRequest portletRequest)
		throws PortalException {

		LiferayPortletURL liferayPortletURL = _portletURLFactory.create(
			portletRequest, _portal.getPortletId(portletRequest),
			_portal.getControlPanelPlid(portletRequest),
			PortletRequest.RENDER_PHASE);

		return liferayPortletURL.toString();
	}

	private String _getSuccessURL(PortletRequest portletRequest) {
		return _portal.getCurrentURL(
			_portal.getHttpServletRequest(portletRequest));
	}

	@Reference
	private DLOpenerGoogleDriveManager _dlOpenerGoogleDriveManager;

	@Reference
	private Portal _portal;

	@Reference
	private PortletURLFactory _portletURLFactory;

}