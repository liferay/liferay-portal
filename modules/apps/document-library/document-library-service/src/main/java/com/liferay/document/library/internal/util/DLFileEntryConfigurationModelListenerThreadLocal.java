/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.util;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Jürgen Kappler
 */
public class DLFileEntryConfigurationModelListenerThreadLocal {

	public static boolean isValidationEnabled() {
		return _validationEnabled.get();
	}

	public static SafeCloseable setValidationEnabledWithSafeCloseable(
		boolean validationEnabled) {

		return _validationEnabled.setWithSafeCloseable(validationEnabled);
	}

	private static final CentralizedThreadLocal<Boolean> _validationEnabled =
		new CentralizedThreadLocal<>(
			DLFileEntryConfigurationModelListenerThreadLocal.class +
				"._validationEnabled",
			() -> Boolean.TRUE, false);

}