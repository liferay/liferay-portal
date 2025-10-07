/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.migration.schema.exporter.internal.exporter;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.db.migration.schema.exporter.internal.configuration.DBMigrationSchemaExportConfiguration;
import com.liferay.portal.db.migration.schema.exporter.internal.sql.writer.SQLWriter;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.db.partition.DBPartition;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mariano Álvaro Sáiz
 */
@Component(
	configurationPid = "com.liferay.portal.db.migration.schema.exporter.internal.configuration.DBMigrationSchemaExportConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class DBMigrationSchemaExport {

	@Activate
	protected void activate(Map<String, Object> properties) {
		_exportDBSchemaDefinition(properties);
	}

	private void _deleteConfiguration(String pid) {
		try {
			Path path = Paths.get(
				PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
				pid.concat(".config"));

			if (Files.exists(path)) {
				Files.delete(path);
			}
			else {
				Configuration[] configurations =
					_configurationAdmin.listConfigurations(
						StringBundler.concat(
							"(", Constants.SERVICE_PID, StringPool.EQUAL, pid,
							"*)"));

				if (configurations == null) {
					return;
				}

				for (Configuration configuration : configurations) {
					configuration.delete();
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _exportDBSchemaDefinition(Map<String, Object> properties) {
		if (_log.isInfoEnabled()) {
			_log.info("Start database schema definition export");
		}

		try {
			DBMigrationSchemaExportConfiguration
				dbMigrationSchemaExportConfiguration =
					ConfigurableUtil.createConfigurable(
						DBMigrationSchemaExportConfiguration.class, properties);

			SQLWriter sqlWriter = new SQLWriter();

			File file = new File(
				dbMigrationSchemaExportConfiguration.exportFilesPath());

			sqlWriter.writeFiles(file);

			if (_log.isInfoEnabled()) {
				_log.info(
					"Finished database schema definition export to " +
						file.getAbsolutePath());
			}

			_generateReport(
				dbMigrationSchemaExportConfiguration.exportFilesPath());
		}
		catch (Exception exception) {
			_log.error(
				"Unable to export database schema definition", exception);
		}
		finally {
			_deleteConfiguration((String)properties.get("service.pid"));
		}
	}

	private void _generateReport(String dirName) throws Exception {
		String installedPatchNames = StringUtil.merge(
			PatcherValues.INSTALLED_PATCH_NAMES, StringPool.COMMA_AND_SPACE);
		Release release = _releaseLocalService.fetchRelease(
			ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME);

		FileUtil.write(
			new File(dirName, "db_migration_schema_export_report.txt"),
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
		if (!DBPartition.isPartitionEnabled()) {
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
		DBMigrationSchemaExport.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ReleaseLocalService _releaseLocalService;

}