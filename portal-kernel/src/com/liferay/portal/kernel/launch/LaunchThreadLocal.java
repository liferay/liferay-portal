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

	public static Mode getMode() {
		return _mode.get();
	}

	public static boolean isEdit() {
		if (_mode.get() == Mode.EDIT) {
			return true;
		}

		return false;
	}

	public static boolean isPreview() {
		if (_mode.get() == Mode.PREVIEW) {
			return true;
		}

		return false;
	}

	public static SafeCloseable setLaunchSetIdWithSafeCloseable(
		long launchSetId, Mode mode) {

		SafeCloseable launchSetIdSafeCloseable =
			_launchSetId.setWithSafeCloseable(launchSetId);
		SafeCloseable modeSafeCloseable = _mode.setWithSafeCloseable(mode);

		return () -> {
			modeSafeCloseable.close();
			launchSetIdSafeCloseable.close();
		};
	}

	public static enum Mode {

		EDIT, PREVIEW,

	}

	private LaunchThreadLocal() {
	}

	private static final CentralizedThreadLocal<Long> _launchSetId =
		new CentralizedThreadLocal<>(
			LaunchThreadLocal.class.getName() + "._launchSetId", () -> -1L);
	private static final CentralizedThreadLocal<Mode> _mode =
		new CentralizedThreadLocal<>(
			LaunchThreadLocal.class.getName() + "._mode");

}