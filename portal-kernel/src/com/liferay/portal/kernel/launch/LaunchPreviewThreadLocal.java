/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.launch;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Alejandro Tardín
 */
public class LaunchPreviewThreadLocal {

	public static long getLaunchSetId() {
		return _launchSetId.get();
	}

	public static SafeCloseable setLaunchSetIdWithSafeCloseable(
		long launchSetId) {

		return _launchSetId.setWithSafeCloseable(launchSetId);
	}

	private LaunchPreviewThreadLocal() {
	}

	private static final CentralizedThreadLocal<Long> _launchSetId =
		new CentralizedThreadLocal<>(
			LaunchPreviewThreadLocal.class.getName() + "._launchSetId",
			() -> -1L);

}