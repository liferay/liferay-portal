/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.spi.checker.setup;

import com.liferay.multi.factor.authentication.spi.checker.MFAChecker;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Tomas Polesovsky
 * @author Marta Medio
 */
public interface SetupMFAChecker extends MFAChecker {

	public void includeSetup(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long userId)
		throws Exception;

	@Override
	public boolean isAvailable(long userId);

	public void removeExistingSetup(long userId);

	public boolean setUp(HttpServletRequest httpServletRequest, long userId);

}