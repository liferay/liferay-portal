/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.provider;

import com.liferay.portal.security.sso.openid.connect.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.constants.OpenIdConnectWebKeys;
import com.liferay.portal.security.sso.openid.connect.provider.OpenIdConnectSessionProvider;

import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

/**
 * @author Istvan Sajtos
 */
@Component(service = OpenIdConnectSessionProvider.class)
public class OpenIdConnectSessionProviderImpl
	implements OpenIdConnectSessionProvider {

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	public static void setOpenIdConnectSession(
		HttpSession httpSession, OpenIdConnectSession openIdConnectSession) {

		httpSession.setAttribute(
			OpenIdConnectWebKeys.OPEN_ID_CONNECT_SESSION, openIdConnectSession);
	}

	@Override
	public OpenIdConnectSession getOpenIdConnectSession(
		HttpSession httpSession) {

		return (OpenIdConnectSession)httpSession.getAttribute(
			OpenIdConnectWebKeys.OPEN_ID_CONNECT_SESSION);
	}

}