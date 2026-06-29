/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.migration.schema.exporter.internal.sql.provider;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.instance.PortalInstancePool;

/**
 * @author Mariano Álvaro Sáiz
 */
public class PortalSQLProvider extends BaseSQLProvider {

	public PortalSQLProvider() throws Exception {
		_objectSQLProvider = new ObjectSQLProvider(
			PortalInstancePool.getCompanyIds(), db);
	}

	@Override
	public String getIndexesSQL() {
		return super.getIndexesSQL() + StringPool.NEW_LINE +
			_objectSQLProvider.getIndexesSQL();
	}

	@Override
	public String getTablesSQL() {
		return super.getTablesSQL() + StringPool.NEW_LINE +
			_objectSQLProvider.getTablesSQL();
	}

	private final ObjectSQLProvider _objectSQLProvider;

}