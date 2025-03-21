/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.token.events;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Defines the public interface for logout behavior.
 *
 * <p>
 * Custom <code>LogoutProcessor</code> classes can override these default
 * implementations:
 * </p>
 *
 * <ul>
 * <li>
 * {@link
 * com.liferay.portal.security.sso.token.internal.events.CookieLogoutProcessor}
 * </li>
 * <li>
 * {@link
 * com.liferay.portal.security.sso.token.internal.events.RedirectLogoutProcessor
 * }
 * </li>
 * </ul>
 *
 * @author Michael C. Han
 */
public interface LogoutProcessor {

	public void logout(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String... parameters)
		throws IOException;

}