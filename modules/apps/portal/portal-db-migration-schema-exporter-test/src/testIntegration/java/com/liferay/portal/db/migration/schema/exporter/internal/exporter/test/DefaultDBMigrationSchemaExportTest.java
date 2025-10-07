/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.migration.schema.exporter.internal.exporter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.db.migration.schema.exporter.internal.test.util.DatabaseTestUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class DefaultDBMigrationSchemaExportTest
	extends BaseDBMigrationSchemaExportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(DBManagerUtil.getDBType() == DBType.POSTGRESQL);
		Assume.assumeFalse(DBPartition.isPartitionEnabled());
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpClassBaseDBMigrationSchemaExportTestCase();

		_company = CompanyTestUtil.addCompany();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		tearDownClassBaseDBMigrationSchemaExportTestCase();

		if (_company != null) {
			_companyLocalService.deleteCompany(_company);
		}
	}

	@Test
	public void testExportImportDBSchemaDefinition() throws Exception {
		testExportImportDBSchemaDefinition(
			() -> {
				DatabaseTestUtil.createSchema(COPY_DB_SCHEMA_NAME);

				DataSource copyDataSource = null;

				try {
					copyDataSource = DatabaseTestUtil.initDataSource(
						COPY_DB_SCHEMA_NAME);

					DatabaseTestUtil.importFile(
						new File(folder, "tables.sql"), copyDataSource);

					assertTables(
						InfrastructureUtil.getDataSource(), copyDataSource);

					DatabaseTestUtil.importFile(
						new File(folder, "indexes.sql"), copyDataSource);

					assertIndexes(
						InfrastructureUtil.getDataSource(), copyDataSource);
				}
				finally {
					DatabaseTestUtil.dropSchema(COPY_DB_SCHEMA_NAME);

					if (copyDataSource != null) {
						DatabaseTestUtil.destroyDataSource(copyDataSource);
					}
				}
			});
	}

	@Test
	public void testExportImportReport() throws Exception {
		String reportContent = getReportContent();

		Assert.assertTrue(reportContent.endsWith("Portal missing tables:"));
	}

	@Test
	public void testExportImportReportWithMissingTable() throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL("create table TestTable (testColumn bigint primary key)");

		try {
			String reportContent = getReportContent();

			Assert.assertTrue(
				reportContent.contains(
					"Portal missing tables: " +
						StringUtil.toLowerCase("TestTable")));
		}
		finally {
			db.runSQL("DROP_TABLE_IF_EXISTS(TestTable)");
		}
	}

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

}