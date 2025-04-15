/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.token.security.auth;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Defines the public interface for authentication token location
 * implementations.
 *
 * <p>
 * Custom <code>TokenRetriever</code> classes can override these default
 * implementations:
 * </p>
 *
 * <ul>
 * <li>
 * {@link
 * com.liferay.portal.security.sso.token.internal.security.auth.RequestTokenRetriever}
 * </li>
 * <li>
 * {@link com.liferay.portal.security.sso.token.internal.security.auth.Request
 * HeaderTokenRetriever}
 * </li>
 * <li>
 * {@link
 * com.liferay.portal.security.sso.token.internal.security.auth.CookieTokenRetriever}
 * </li>
 * <li>
 * {@link
 * com.liferay.portal.security.sso.token.internal.security.auth.SessionTokenRetriever}
 * </li>
 * </ul>
 *
 * @author Michael C. Han
 */
public interface TokenRetriever {

	public String getLoginToken(
		HttpServletRequest httpServletRequest, String userTokenName);

}