/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal.display.context;

import com.liferay.document.library.util.DLURLHelper;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.service.OAuth2ApplicationService;
import com.liferay.oauth2.provider.web.internal.AssignableScopes;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

/**
 * @author Tomas Polesovsky
 */
public class OAuth2ConnectedApplicationsPortletDisplayContext
	extends BaseOAuth2PortletDisplayContext {

	public OAuth2ConnectedApplicationsPortletDisplayContext(
		AssignableScopes assignableScopes, DLURLHelper dlURLHelper,
		OAuth2ApplicationService oAuth2ApplicationService,
		OAuth2Authorization oAuth2Authorization,
		PortletRequest portletRequest) {

		this(dlURLHelper, oAuth2ApplicationService, portletRequest);

		_assignableScopes = assignableScopes;
		_oAuth2Authorization = oAuth2Authorization;
	}

	public OAuth2ConnectedApplicationsPortletDisplayContext(
		DLURLHelper dlURLHelper,
		OAuth2ApplicationService oAuth2ApplicationService,
		PortletRequest portletRequest) {

		super(
			dlURLHelper, oAuth2ApplicationService, portletRequest,
			(ThemeDisplay)portletRequest.getAttribute(WebKeys.THEME_DISPLAY));
	}

	public AssignableScopes getAssignableScopes() {
		return _assignableScopes;
	}

	public OAuth2Authorization getOAuth2Authorization() {
		return _oAuth2Authorization;
	}

	private AssignableScopes _assignableScopes;
	private OAuth2Authorization _oAuth2Authorization;

}