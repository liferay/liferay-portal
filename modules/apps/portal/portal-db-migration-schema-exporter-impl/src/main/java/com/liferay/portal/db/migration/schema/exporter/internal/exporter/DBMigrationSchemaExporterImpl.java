/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.migration.schema.exporter.internal.exporter;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.db.migration.schema.exporter.DBMigrationSchemaExportResult;
import com.liferay.portal.db.migration.schema.exporter.DBMigrationSchemaExporter;
import com.liferay.portal.db.migration.schema.exporter.internal.sql.writer.SQLWriter;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.patcher.PatcherValues;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;

import java.io.File;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Ortiz
 */
@Component(service = DBMigrationSchemaExporter.class)
public class DBMigrationSchemaExporterImpl
	implements DBMigrationSchemaExporter {

	@Override
	public DBMigrationSchemaExportResult export(String exportFilesPath)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Start database schema definition export");
		}

		File directory = new File(exportFilesPath);

		SQLWriter sqlWriter = new SQLWriter();

		List<String> fileNames = sqlWriter.writeFiles(directory);

		String reportFileName = _generateReport(exportFilesPath);

		if (_log.isInfoEnabled()) {
			_log.info(
				"Finished database schema definition export to " +
					directory.getAbsolutePath());
		}

		return new DBMigrationSchemaExportResult(fileNames, reportFileName);
	}

	private String _generateReport(String dirName) throws Exception {
		String reportFileName = "db_migration_schema_export_report.txt";

		String installedPatchNames = StringUtil.merge(
			PatcherValues.INSTALLED_PATCH_NAMES, StringPool.COMMA_AND_SPACE);
		Release release = _releaseLocalService.fetchRelease(
			ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME);

		FileUtil.write(
			new File(dirName, reportFileName),
			StringUtil.merge(
				new Object[] {
					"Export date: " + _toString(new Date()),
					"Portal build date: " + _toString(release.getBuildDate()),
					"Portal build number: " + release.getBuildNumber(),
					"Portal installed patches: " + installedPatchNames,
					"Portal schema version: " + release.getSchemaVersion(),
					StringPool.NEW_LINE,
					"Database type: " + DBManagerUtil.getDBType(),
					"Export database type: " + DBType.POSTGRESQL,
					StringPool.NEW_LINE, _getTablesInfo(dirName)
				},
				StringPool.NEW_LINE));

		return reportFileName;
	}

	private Set<String> _getDBTableNames(String type) throws Exception {
		Set<String> tableNames = new HashSet<>();

		DataSource dataSource = InfrastructureUtil.getDataSource();

		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();

			try (ResultSet resultSet = databaseMetaData.getTables(
					connection.getCatalog(), connection.getSchema(), null,
					new String[] {type})) {

				while (resultSet.next()) {
					tableNames.add(
						StringUtil.toLowerCase(
							resultSet.getString("TABLE_NAME")));
				}
			}
		}

		return tableNames;
	}

	private Set<String> _getExportTableNames(
			long companyId, String dirName, String type)
		throws Exception {

		Set<String> tableNames = new HashSet<>();

		String prefix = StringPool.BLANK;

		if (companyId != PortalInstancePool.getDefaultCompanyId()) {
			prefix = companyId + StringPool.UNDERLINE;
		}

		String content = StringUtil.toLowerCase(
			FileUtil.read(new File(dirName, prefix + "tables.sql")));

		String[] lines = StringUtil.split(content, StringPool.NEW_LINE);

		for (String line : lines) {
			if (type.equals("TABLE") &&
				StringUtil.startsWith(line, "create table")) {

				String[] parts = line.split(StringPool.SPACE);

				String tableName = StringUtil.extractLast(
					parts[2], StringPool.PERIOD);

				tableNames.add((tableName == null) ? parts[2] : tableName);
			}
			else if (type.equals("VIEW") &&
					 StringUtil.startsWith(line, "create or replace view")) {

				tableNames.add(
					StringUtil.extractLast(
						line.split(StringPool.SPACE)[4], StringPool.PERIOD));
			}
		}

		return tableNames;
	}

	private String _getTablesInfo(
			long companyId, String dirName, String message, String type)
		throws Exception {

		Set<String> dbTableNames = _getDBTableNames(type);
		Set<String> exportTableNames = _getExportTableNames(
			companyId, dirName, type);

		String missingTableNames = StringUtil.merge(
			SetUtil.asymmetricDifference(dbTableNames, exportTableNames),
			StringPool.COMMA_AND_SPACE);

		return StringUtil.merge(
			new Object[] {
				StringUtil.replace(message, '?', "database") +
					dbTableNames.size(),
				StringUtil.replace(message, '?', "export") +
					exportTableNames.size(),
				StringUtil.replace(message, '?', "missing") + missingTableNames,
				StringPool.NEW_LINE
			},
			StringPool.NEW_LINE);
	}

	private String _getTablesInfo(String dirName) throws Exception {
		if (!PropsValues.DATABASE_PARTITION_ENABLED) {
			return _getTablesInfo(
				PortalInstancePool.getDefaultCompanyId(), dirName,
				"Portal ? tables: ", "TABLE");
		}

		StringBundler sb = new StringBundler(
			_getTablesInfo(
				PortalInstancePool.getDefaultCompanyId(), dirName,
				"Default virtual instance ? tables: ", "TABLE"));

		_companyLocalService.forEachCompanyId(
			companyId -> {
				if (companyId == PortalInstancePool.getDefaultCompanyId()) {
					return;
				}

				sb.append(StringPool.NEW_LINE);
				sb.append(
					_getTablesInfo(
						companyId, dirName,
						StringBundler.concat(
							"Virtual instance ", companyId, " ? tables: "),
						"TABLE"));
				sb.append(
					_getTablesInfo(
						companyId, dirName,
						StringBundler.concat(
							"Virtual instance ", companyId, " ? views: "),
						"VIEW"));
			});

		return sb.toString();
	}

	private String _toString(Date date) {
		return Time.getSimpleDate(date, DateUtil.ISO_8601_PATTERN);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DBMigrationSchemaExporterImpl.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ReleaseLocalService _releaseLocalService;

}