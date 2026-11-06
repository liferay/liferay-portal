/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.backgroundtask;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

/**
 * @author Michael C. Han
 */
public class BackgroundTaskThreadLocal {

	public static long getBackgroundTaskId() {
		return _backgroundTaskId.get();
	}

	public static boolean hasBackgroundTask() {
		long backgroundTaskId = getBackgroundTaskId();

		if (backgroundTaskId > 0) {
			return true;
		}

		return false;
	}

	public static SafeCloseable setBackgroundTaskIdWithSafeCloseable(
		long backgroundTaskId) {

		if (backgroundTaskId > 0) {
			return _backgroundTaskId.setWithSafeCloseable(backgroundTaskId);
		}

		return SafeCloseable.EMPTY;
	}

	private static final CentralizedThreadLocal<Long> _backgroundTaskId =
		new CentralizedThreadLocal<>(
			BackgroundTaskThreadLocal.class + "._backgroundTaskId", () -> 0L);

}