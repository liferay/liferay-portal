/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.util;

import com.liferay.petra.string.StringPool;

/**
 * @author Thiago Buarque
 */
public class ConfigurationPIDUtil {

	public static String getUnscopedPid(String pid) {
		pid = pid.replaceFirst("\\.scoped.*", StringPool.BLANK);

		return pid.replaceFirst("~.*", StringPool.BLANK);
	}

}