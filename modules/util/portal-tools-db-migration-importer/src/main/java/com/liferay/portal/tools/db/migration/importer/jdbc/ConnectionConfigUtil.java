/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.migration.importer.jdbc;

import com.liferay.portal.kernel.util.GetterUtil;

/**
 * @author Luis Ortiz
 */
public class ConnectionConfigUtil {

	public static int getBatchSize() {
		return _batchSize;
	}

	public static int getFetchSize() {
		return _fetchSize;
	}

	public static void setBatchSize(String batchSize) {
		_batchSize = GetterUtil.get(batchSize, _BATCH_SIZE);
	}

	public static void setFetchSize(String fetchSize) {
		_fetchSize = GetterUtil.get(fetchSize, _FETCH_SIZE);
	}

	private static final int _BATCH_SIZE = 2500;

	private static final int _FETCH_SIZE = 2500;

	private static int _batchSize;
	private static int _fetchSize;

}