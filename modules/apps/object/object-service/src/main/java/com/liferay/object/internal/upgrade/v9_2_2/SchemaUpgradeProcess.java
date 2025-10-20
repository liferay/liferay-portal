/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v9_2_2;

import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * @author Paulo Albuquerque
 */
public class SchemaUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		long companyId = CompanyThreadLocal.getCompanyId();
		long defaultCompanyId = PortalInstancePool.getDefaultCompanyId();

		if (!PropsValues.DATABASE_PARTITION_ENABLED ||
			(companyId == defaultCompanyId)) {

			return;
		}

		DatabaseMetaData databaseMetaData = connection.getMetaData();
		DBInspector dbInspector = new DBInspector(connection);

		try (ResultSet resultSet = databaseMetaData.getTables(
				dbInspector.getCatalog(), dbInspector.getSchema(), null,
				new String[] {"VIEW"})) {

			while (resultSet.next()) {
				String tableName = resultSet.getString("TABLE_NAME");

				String tableNameLowerCase = StringUtil.toLowerCase(tableName);

				if (tableName.contains(String.valueOf(defaultCompanyId)) &&
					(tableNameLowerCase.contains("_x_") ||
					 tableNameLowerCase.startsWith("o_"))) {

					runSQL("drop view if exists " + tableName);
				}
			}
		}
	}

}