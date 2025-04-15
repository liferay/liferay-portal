/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.spi.checker.headless;

import com.liferay.multi.factor.authentication.spi.checker.MFAChecker;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 * @author Marta Medio
 */
public interface HeadlessMFAChecker extends MFAChecker {

	public boolean verifyHeadlessRequest(
		HttpServletRequest httpServletRequest, long userId);

}