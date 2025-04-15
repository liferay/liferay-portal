/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.provider;

import com.liferay.portal.security.sso.openid.connect.OpenIdConnectSession;

import jakarta.servlet.http.HttpSession;

/**
 * @author     Istvan Sajtos
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public interface OpenIdConnectSessionProvider {

	public OpenIdConnectSession getOpenIdConnectSession(
		HttpSession httpSession);

}