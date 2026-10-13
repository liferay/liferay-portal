/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.set.scheduler.util;

import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

/**
 * @author Jose Luis Navarro
 */
public class LaunchSetSchedulerUtil {

	public static String getJobName(ObjectEntry launchSetObjectEntry) {
		return StringBundler.concat(
			launchSetObjectEntry.getObjectEntryId(), StringPool.AT,
			launchSetObjectEntry.getCompanyId());
	}

}