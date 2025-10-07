/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.util.StringBundler;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DataCleanupLoggingUtil {

	public static void logDelete(
		Log log, long count, String tableName, String cause) {

		if ((count < 1) || !log.isInfoEnabled()) {
			return;
		}

		log.info(
			StringBundler.concat(
				"Table ", tableName, ", ", String.valueOf(count), " row",
				(count > 1) ? "s " : " ", "deleted because ", cause));
	}

	public static void logTruncate(Log log, String tableName) {
		if (log.isInfoEnabled()) {
			log.info(
				"Table " + tableName +
					", truncated because data is no longer needed");
		}
	}

	public static void logUpdate(
		Log log, long count, String tableName, String columnName, Object value,
		String cause) {

		if ((count < 1) || !log.isInfoEnabled()) {
			return;
		}

		log.info(
			StringBundler.concat(
				"Table ", tableName, ", ", String.valueOf(count), " row",
				(count > 1) ? "s " : " ", "updated column ", columnName,
				(value != null) ? (" to value " + String.valueOf(value)) :
					StringPool.BLANK,
				" because ", cause));
	}

}