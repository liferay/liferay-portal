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
public class LaunchSetThreadLocal {

	public static long getLaunchSetId() {
		return _launchSetId.get();
	}

	public static LaunchSetMode getLaunchSetMode() {
		return _launchSetMode.get();
	}

	public static boolean isLaunchSetModeEdit() {
		if (_launchSetMode.get() == LaunchSetMode.EDIT) {
			return true;
		}

		return false;
	}

	public static boolean isLaunchSetModePreview() {
		if (_launchSetMode.get() == LaunchSetMode.PREVIEW) {
			return true;
		}

		return false;
	}

	public static SafeCloseable setLaunchSetIdWithSafeCloseable(
		long launchSetId, LaunchSetMode launchSetMode) {

		SafeCloseable launchSetIdSafeCloseable =
			_launchSetId.setWithSafeCloseable(launchSetId);
		SafeCloseable launchSetModeSafeCloseable =
			_launchSetMode.setWithSafeCloseable(launchSetMode);

		return () -> {
			launchSetIdSafeCloseable.close();
			launchSetModeSafeCloseable.close();
		};
	}

	private LaunchSetThreadLocal() {
	}

	private static final CentralizedThreadLocal<Long> _launchSetId =
		new CentralizedThreadLocal<>(
			LaunchSetThreadLocal.class.getName() + "._launchSetId", () -> -1L);
	private static final CentralizedThreadLocal<LaunchSetMode> _launchSetMode =
		new CentralizedThreadLocal<>(
			LaunchSetThreadLocal.class.getName() + "._launchSetMode");

}