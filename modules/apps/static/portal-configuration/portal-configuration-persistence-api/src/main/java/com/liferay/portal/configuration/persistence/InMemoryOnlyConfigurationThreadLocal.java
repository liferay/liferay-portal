/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Raymond Augé
 * @author Gregory Amerson
 */
public class InMemoryOnlyConfigurationThreadLocal {

	public static boolean isInMemoryOnly() {
		return _inMemoryOnly.get();
	}

	public static SafeCloseable setInMemoryOnlyWithSafeCloseable(
		boolean inMemoryOnly) {

		return _inMemoryOnly.setWithSafeCloseable(inMemoryOnly);
	}

	private static final CentralizedThreadLocal<Boolean> _inMemoryOnly =
		new CentralizedThreadLocal<>(
			InMemoryOnlyConfigurationThreadLocal.class + "._inMemoryOnly",
			() -> Boolean.FALSE, false);

}