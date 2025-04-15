/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auto.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 */
public interface AutoLogin {

	/**
	 * Set a request attribute with this variable to tell the AutoLoginFilter to
	 * stop processing filters and redirect the user to a specified location.
	 */
	public static final String AUTO_LOGIN_REDIRECT = "AUTO_LOGIN_REDIRECT";

	/**
	 * Set a request attribute with this variable to tell the AutoLoginFilter to
	 * continue processing filters and then redirect the user to a specified
	 * location.
	 */
	public static final String AUTO_LOGIN_REDIRECT_AND_CONTINUE =
		"AUTO_LOGIN_REDIRECT_AND_CONTINUE";

	public String[] login(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws AutoLoginException;

}