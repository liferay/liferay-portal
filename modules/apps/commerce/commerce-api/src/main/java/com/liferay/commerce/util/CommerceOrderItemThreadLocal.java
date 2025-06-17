/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.util;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceOrderItemThreadLocal {

	public static boolean isSanitized() {
		return _sanitized.get();
	}

	public static SafeCloseable setSanitizedWithSafeCloseable(
		boolean sanitized) {

		return _sanitized.setWithSafeCloseable(sanitized);
	}

	private static final CentralizedThreadLocal<Boolean> _sanitized =
		new CentralizedThreadLocal<>(
			CommerceOrderItemThreadLocal.class + "._sanitized",
			() -> Boolean.FALSE);

}