/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.portal.kernel.dao.db.DuplicateUniqueFinderRowsCleaner;

/**
 * @author Jorge Avalos
 */
public class DeleteDuplicateUniqueFinderRowsUpgradeProcess
	extends UpgradeProcess {

	public DeleteDuplicateUniqueFinderRowsUpgradeProcess(
		String tableName, String[] columnNames, String orderByClause) {

		_tableName = tableName;
		_columnNames = columnNames;
		_orderByClause = orderByClause;
	}

	@Override
	protected void doUpgrade() throws Exception {
		DuplicateUniqueFinderRowsCleaner duplicateUniqueFinderRowsCleaner =
			new DuplicateUniqueFinderRowsCleaner(
				connection, _tableName, _columnNames, _orderByClause);

		duplicateUniqueFinderRowsCleaner.deleteDuplicates();
	}

	private final String[] _columnNames;
	private final String _orderByClause;
	private final String _tableName;

}