/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.migration.schema.exporter.internal.sql.writer;

import com.liferay.portal.db.migration.schema.exporter.internal.sql.provider.DBPartitionSQLProvider;
import com.liferay.portal.db.migration.schema.exporter.internal.sql.provider.PortalSQLProvider;
import com.liferay.portal.db.migration.schema.exporter.internal.sql.provider.SQLProvider;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.util.FileUtil;

import java.io.File;

/**
 * @author Mariano Álvaro Sáiz
 */
public class SQLWriter {

	public void writeFiles(File directory) throws Exception {
		if (DBPartition.isPartitionEnabled()) {
			_writeDBPartitionFiles(directory);
		}
		else {
			_writeFiles(directory);
		}
	}

	private void _writeDBPartitionFiles(File directory) throws Exception {
		DBPartitionSQLProvider.clearCache();

		CompanyLocalServiceUtil.forEachCompanyId(
			companyId -> {
				if (companyId == PortalInstancePool.getDefaultCompanyId()) {
					_writeFiles(directory);

					return;
				}

				SQLProvider sqlProvider = new DBPartitionSQLProvider(companyId);

				FileUtil.write(
					new File(directory, companyId + "_indexes.sql"),
					sqlProvider.getIndexesSQL());
				FileUtil.write(
					new File(directory, companyId + "_tables.sql"),
					sqlProvider.getTablesSQL());
			});
	}

	private void _writeFiles(File directory) throws Exception {
		SQLProvider sqlProvider = new PortalSQLProvider();

		FileUtil.write(
			new File(directory, "indexes.sql"), sqlProvider.getIndexesSQL());
		FileUtil.write(
			new File(directory, "tables.sql"), sqlProvider.getTablesSQL());
	}

}