/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.util;

import com.liferay.portal.kernel.security.auth.CompanyCentralizedThreadLocal;

/**
 * @author Christian Moura
 */
public class ScimThreadLocal {

	public static boolean isResetInProcess() {
		return _resetInProcess.get();
	}

	public static void setResetInProcess(boolean resetInProcess) {
		_resetInProcess.set(resetInProcess);
	}

	private static final ThreadLocal<Boolean> _resetInProcess =
		new CompanyCentralizedThreadLocal<>(
			ScimThreadLocal.class + "._resetInProcess", () -> Boolean.FALSE);

}