/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.launch;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Carlos Correa
 */
public class LaunchEditThreadLocal {

	public static long getLaunchSetId() {
		return _launchSetId.get();
	}

	public static SafeCloseable setLaunchSetIdWithSafeCloseable(
		long launchSetId) {

		return _launchSetId.setWithSafeCloseable(launchSetId);
	}

	private LaunchEditThreadLocal() {
	}

	private static final CentralizedThreadLocal<Long> _launchSetId =
		new CentralizedThreadLocal<>(
			LaunchEditThreadLocal.class.getName() + "._launchSetId", () -> 0L);

}