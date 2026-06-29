/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.migration.schema.exporter.internal.sql.writer;

import com.liferay.portal.db.migration.schema.exporter.internal.sql.provider.DBPartitionSQLProvider;
import com.liferay.portal.db.migration.schema.exporter.internal.sql.provider.PortalSQLProvider;
import com.liferay.portal.db.migration.schema.exporter.internal.sql.provider.SQLProvider;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mariano Álvaro Sáiz
 */
public class SQLWriter {

	public List<String> writeFiles(File directory) throws Exception {
		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			return _writeDBPartitionFiles(directory);
		}

		return _writeFiles(directory);
	}

	private List<String> _writeDBPartitionFiles(File directory)
		throws Exception {

		DBPartitionSQLProvider.clearCache();

		List<String> fileNames = new ArrayList<>();

		CompanyLocalServiceUtil.forEachCompanyId(
			companyId -> {
				if (companyId == PortalInstancePool.getDefaultCompanyId()) {
					fileNames.addAll(_writeFiles(directory));

					return;
				}

				SQLProvider sqlProvider = new DBPartitionSQLProvider(companyId);

				String indexesFileName = companyId + "_indexes.sql";
				String tablesFileName = companyId + "_tables.sql";

				FileUtil.write(
					new File(directory, indexesFileName),
					sqlProvider.getIndexesSQL());
				FileUtil.write(
					new File(directory, tablesFileName),
					sqlProvider.getTablesSQL());

				fileNames.add(indexesFileName);
				fileNames.add(tablesFileName);
			});

		return fileNames;
	}

	private List<String> _writeFiles(File directory) throws Exception {
		SQLProvider sqlProvider = new PortalSQLProvider();

		FileUtil.write(
			new File(directory, "indexes.sql"), sqlProvider.getIndexesSQL());
		FileUtil.write(
			new File(directory, "tables.sql"), sqlProvider.getTablesSQL());

		return ListUtil.fromArray("indexes.sql", "tables.sql");
	}

}