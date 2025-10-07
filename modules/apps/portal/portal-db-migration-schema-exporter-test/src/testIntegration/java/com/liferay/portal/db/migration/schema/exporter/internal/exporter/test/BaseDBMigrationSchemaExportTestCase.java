/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.migration.schema.exporter.internal.exporter.test;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.db.migration.schema.exporter.internal.test.util.ConfigurationTestUtil;
import com.liferay.portal.db.migration.schema.exporter.internal.test.util.DatabaseTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.io.File;

import java.nio.file.Files;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.cm.PersistenceManager;

import org.junit.Assert;

import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Mariano Álvaro Sáiz
 */
public abstract class BaseDBMigrationSchemaExportTestCase {

	protected static void setUpClassBaseDBMigrationSchemaExportTestCase()
		throws Exception {

		folder = FileUtil.createTempFolder();

		_objectDefinition1 = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName());
		_objectDefinition2 = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName());

		_objectRelationship = ObjectRelationshipTestUtil.addObjectRelationship(
			ObjectRelationshipLocalServiceUtil.getService(), _objectDefinition1,
			_objectDefinition2);
	}

	protected static void tearDownClassBaseDBMigrationSchemaExportTestCase()
		throws Exception {

		Files.deleteIfExists(ConfigurationTestUtil.getConfigurationPath(PID));

		FileUtil.deltree(folder);

		if (_objectRelationship != null) {
			ObjectRelationshipLocalServiceUtil.deleteObjectRelationship(
				_objectRelationship.getObjectRelationshipId());
		}

		if (_objectDefinition1 != null) {
			ObjectDefinitionLocalServiceUtil.deleteObjectDefinition(
				_objectDefinition1.getObjectDefinitionId());
		}

		if (_objectDefinition2 != null) {
			ObjectDefinitionLocalServiceUtil.deleteObjectDefinition(
				_objectDefinition2.getObjectDefinitionId());
		}
	}

	protected void assertIndexes(
			DataSource dataSource, DataSource copyDataSource)
		throws Exception {

		List<String> copyIndexColumnNames =
			DatabaseTestUtil.getIndexColumnNames(copyDataSource);
		List<String> indexColumnNames = DatabaseTestUtil.getIndexColumnNames(
			dataSource);

		Assert.assertEquals(
			StringUtils.difference(
				copyIndexColumnNames.toString(), indexColumnNames.toString()),
			indexColumnNames.size(), copyIndexColumnNames.size());

		for (int i = 0; i < indexColumnNames.size(); i++) {
			Assert.assertEquals(
				indexColumnNames.get(i), copyIndexColumnNames.get(i));
		}
	}

	protected void assertTables(
			DataSource dataSource, DataSource copyDataSource)
		throws Exception {

		List<String> copyTableColumnNames =
			DatabaseTestUtil.getTableColumnNames(copyDataSource);
		List<String> tableColumnNames = DatabaseTestUtil.getTableColumnNames(
			dataSource);

		Assert.assertEquals(
			StringUtils.difference(
				copyTableColumnNames.toString(), tableColumnNames.toString()),
			tableColumnNames.size(), copyTableColumnNames.size());

		for (int i = 0; i < tableColumnNames.size(); i++) {
			Assert.assertEquals(
				tableColumnNames.get(i), copyTableColumnNames.get(i));
		}
	}

	protected String getReportContent() throws Exception {
		ConfigurationTestUtil.deployConfiguration(
			configurationAdmin, folder.getAbsolutePath(), PID);

		return FileUtil.read(
			new File(folder, "db_migration_schema_export_report.txt"));
	}

	protected void testExportImportDBSchemaDefinition(
			UnsafeRunnable<Exception> runnable)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.db.migration.schema.exporter.internal." +
					"exporter.DBMigrationSchemaExport",
				LoggerTestUtil.INFO)) {

			ConfigurationTestUtil.deployConfiguration(
				configurationAdmin, folder.getAbsolutePath(), PID);

			runnable.run();

			Assert.assertFalse(
				Files.exists(ConfigurationTestUtil.getConfigurationPath(PID)));
			Assert.assertNull(
				configurationAdmin.listConfigurations(
					"(service.pid=" + PID + ")"));
			Assert.assertNull(
				ReflectionTestUtil.invoke(
					_persistenceManager, "_getDictionary",
					new Class<?>[] {String.class}, PID));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			List<String> messages = logCapture.getMessages();

			Assert.assertEquals(
				"Start database schema definition export", messages.get(0));
			Assert.assertEquals(
				"Finished database schema definition export to " +
					folder.getAbsolutePath(),
				messages.get(1));
		}
	}

	protected static final String COPY_DB_SCHEMA_NAME = "testschema";

	protected static final String PID =
		"com.liferay.portal.db.migration.schema.exporter.internal." +
			"configuration.DBMigrationSchemaExportConfiguration";

	protected static File folder;

	@Inject
	protected ConfigurationAdmin configurationAdmin;

	private static ObjectDefinition _objectDefinition1;
	private static ObjectDefinition _objectDefinition2;
	private static ObjectRelationship _objectRelationship;

	@Inject
	private PersistenceManager _persistenceManager;

}