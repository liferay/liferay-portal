/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.PropsValues;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DataCleanupLoggingUtil {

	public static void logDelete(
		Log log, long count, boolean readOnly, String tableName, String cause) {

		if ((count < 1) || !log.isInfoEnabled()) {
			return;
		}

		log.info(
			StringBundler.concat(
				"Table ", _prependPartitionName(tableName), ", ", count, " row",
				(count > 1) ? "s " : " ", readOnly ? "should be " : "",
				"deleted because ", cause));
	}

	public static void logDelete(
		Log log, long count, String tableName, String cause) {

		logDelete(log, count, false, tableName, cause);
	}

	public static void logDrop(Log log, String tableName, String cause) {
		if (log.isInfoEnabled()) {
			log.info(
				StringBundler.concat(
					"Table ", _prependPartitionName(tableName),
					" was dropped because ", cause));
		}
	}

	public static void logRename(
		Log log, String originalValue, String renamedValue, String cause) {

		if (!log.isInfoEnabled()) {
			return;
		}

		log.info(
			StringBundler.concat(
				"Table ", _prependPartitionName(originalValue),
				" was renamed to ", renamedValue, " because ", cause));
	}

	public static void logTruncate(Log log, String tableName) {
		if (log.isInfoEnabled()) {
			log.info(
				"Table " + _prependPartitionName(tableName) +
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
				"Table ", _prependPartitionName(tableName), ", ", count, " row",
				(count > 1) ? "s " : " ", "updated column ", columnName,
				(value != null) ? (" to value " + String.valueOf(value)) :
					StringPool.BLANK,
				" because ", cause));
	}

	private static String _prependPartitionName(String tableName) {
		if (!PropsValues.DATABASE_PARTITION_ENABLED) {
			return tableName;
		}

		long companyId = CompanyThreadLocal.getNonsystemCompanyId();

		if (companyId == PortalInstancePool.getDefaultCompanyId()) {
			return tableName;
		}

		return StringBundler.concat(
			PropsValues.DATABASE_PARTITION_SCHEMA_NAME_PREFIX, companyId,
			StringPool.PERIOD, tableName);
	}

}