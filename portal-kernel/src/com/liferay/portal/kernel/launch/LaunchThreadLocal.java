/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.launch;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Alejandro Tardín
 * @author Carlos Correa
 */
public class LaunchThreadLocal {

	public static long getLaunchSetId() {
		return _launchSetId.get();
	}

	public static LaunchMode getLaunchMode() {
		return _launchMode.get();
	}

	public static boolean isLaunchModeEdit() {
		if (_launchMode.get() == LaunchMode.EDIT) {
			return true;
		}

		return false;
	}

	public static boolean isLaunchModePreview() {
		if (_launchMode.get() == LaunchMode.PREVIEW) {
			return true;
		}

		return false;
	}

	public static SafeCloseable setLaunchSetIdWithSafeCloseable(
		long launchSetId, LaunchMode launchMode) {

		SafeCloseable launchSetIdSafeCloseable =
			_launchSetId.setWithSafeCloseable(launchSetId);
		SafeCloseable launchModeSafeCloseable = _launchMode.setWithSafeCloseable(launchMode);

		return () -> {
			launchModeSafeCloseable.close();
			launchSetIdSafeCloseable.close();
		};
	}

	private LaunchThreadLocal() {
	}

	private static final CentralizedThreadLocal<Long> _launchSetId =
		new CentralizedThreadLocal<>(
			LaunchThreadLocal.class.getName() + "._launchSetId", () -> -1L);
	private static final CentralizedThreadLocal<LaunchMode> _launchMode =
		new CentralizedThreadLocal<>(
			LaunchThreadLocal.class.getName() + "._launchMode");

}