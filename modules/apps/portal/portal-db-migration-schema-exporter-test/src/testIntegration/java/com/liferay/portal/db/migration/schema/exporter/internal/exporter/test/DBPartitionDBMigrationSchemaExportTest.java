/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.migration.schema.exporter.internal.exporter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.db.migration.schema.exporter.internal.test.util.DatabaseTestUtil;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;

import javax.sql.DataSource;

import org.apache.felix.cm.PersistenceManager;

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
public class DBPartitionDBMigrationSchemaExportTest
	extends BaseDBMigrationSchemaExportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(DBManagerUtil.getDBType() == DBType.POSTGRESQL);
		Assume.assumeTrue(DBPartition.isPartitionEnabled());
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpClassBaseDBMigrationSchemaExportTestCase();

		_company = CompanyTestUtil.addCompany();

		_companyPartitionName = DBPartitionUtil.getPartitionName(
			_company.getCompanyId());

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			User adminUser = UserTestUtil.getAdminUser(_company.getCompanyId());

			_objectDBPartitionDefinition1 =
				ObjectDefinitionTestUtil.addCustomObjectDefinition(
					ObjectDefinitionTestUtil.getRandomName(),
					adminUser.getUserId());
			_objectDBPartitionDefinition2 =
				ObjectDefinitionTestUtil.addCustomObjectDefinition(
					ObjectDefinitionTestUtil.getRandomName(),
					adminUser.getUserId());

			ObjectRelationshipTestUtil.addObjectRelationship(
				ObjectRelationshipLocalServiceUtil.getService(),
				_objectDBPartitionDefinition1, _objectDBPartitionDefinition2,
				adminUser.getUserId());
		}
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
			() -> _companyLocalService.forEachCompanyId(
				companyId -> {
					String indexesSQLName = "indexes.sql";
					String tablesSQLName = "tables.sql";

					if (companyId != PortalInstancePool.getDefaultCompanyId()) {
						indexesSQLName =
							companyId + StringPool.UNDERLINE + indexesSQLName;
						tablesSQLName =
							companyId + StringPool.UNDERLINE + tablesSQLName;
					}

					File indexesSQLFile = new File(folder, indexesSQLName);
					File tablesSQLFile = new File(folder, tablesSQLName);

					DatabaseTestUtil.createSchema(COPY_DB_SCHEMA_NAME);

					DataSource copyDataSource = null;
					DataSource dataSource = InfrastructureUtil.getDataSource();

					try {
						copyDataSource = DatabaseTestUtil.initDataSource(
							COPY_DB_SCHEMA_NAME);

						if (companyId ==
								PortalInstancePool.getDefaultCompanyId()) {

							DatabaseTestUtil.importFile(
								tablesSQLFile, copyDataSource);
						}
						else {
							DatabaseTestUtil.importSQL(
								StringUtil.replace(
									FileUtil.read(tablesSQLFile),
									DBPartitionUtil.getPartitionName(companyId),
									COPY_DB_SCHEMA_NAME),
								InfrastructureUtil.getDataSource());

							dataSource = DatabaseTestUtil.initDataSource(
								DBPartitionUtil.getPartitionName(companyId));
						}

						assertTables(dataSource, copyDataSource);
						Assert.assertEquals(
							DatabaseTestUtil.getViewNames(dataSource),
							DatabaseTestUtil.getViewNames(copyDataSource));

						if (companyId ==
								PortalInstancePool.getDefaultCompanyId()) {

							DatabaseTestUtil.importFile(
								indexesSQLFile, copyDataSource);
						}
						else {
							DatabaseTestUtil.importSQL(
								StringUtil.replace(
									FileUtil.read(indexesSQLFile),
									DBPartitionUtil.getPartitionName(companyId),
									COPY_DB_SCHEMA_NAME),
								InfrastructureUtil.getDataSource());
						}

						assertIndexes(dataSource, copyDataSource);
					}
					finally {
						DatabaseTestUtil.dropSchema(COPY_DB_SCHEMA_NAME);

						if ((dataSource != null) &&
							(dataSource !=
								InfrastructureUtil.getDataSource())) {

							DatabaseTestUtil.destroyDataSource(dataSource);
						}

						if (copyDataSource != null) {
							DatabaseTestUtil.destroyDataSource(copyDataSource);
						}
					}
				}));
	}

	@Test
	public void testExportImportReport() throws Exception {
		String reportContent = getReportContent();

		Assert.assertTrue(
			reportContent.contains(
				"Default virtual instance missing tables:\n"));
		Assert.assertTrue(
			reportContent.contains(
				"Virtual instance " + _company.getCompanyId() +
					" missing tables:\n"));
		Assert.assertTrue(
			reportContent.contains(
				"Virtual instance " + _company.getCompanyId() +
					" missing views:\n") ||
			reportContent.endsWith(
				"Virtual instance " + _company.getCompanyId() +
					" missing views:"));
		Assert.assertTrue(
			reportContent.contains(
				"Virtual instance " + TestPropsValues.getCompanyId() +
					" missing tables:\n"));
		Assert.assertTrue(
			reportContent.contains(
				"Virtual instance " + TestPropsValues.getCompanyId() +
					" missing views:\n") ||
			reportContent.endsWith(
				"Virtual instance " + TestPropsValues.getCompanyId() +
					" missing views:"));
	}

	@Test
	public void testExportImportReportWithMissingTable() throws Exception {
		DB db = DBManagerUtil.getDB();

		String defaultPartitionName = DBPartitionUtil.getPartitionName(
			PortalInstancePool.getDefaultCompanyId());

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			db.runSQL(
				"create table " + defaultPartitionName +
					".TestTable (testColumn bigint primary key)");
			db.runSQL(
				"create table " +
					DBPartitionUtil.getPartitionName(_company.getCompanyId()) +
						".TestTable2 (testColumn bigint primary key)");

			String reportContent = getReportContent();

			Assert.assertTrue(
				reportContent.contains(
					"Default virtual instance missing tables: testtable"));

			Assert.assertTrue(
				reportContent.contains(
					StringBundler.concat(
						"Virtual instance ", _company.getCompanyId(),
						" missing tables: ",
						StringUtil.toLowerCase("TestTable2"))));
		}
		finally {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						PortalInstancePool.getDefaultCompanyId())) {

				db.runSQL(
					"DROP_TABLE_IF_EXISTS(" + defaultPartitionName +
						".TestTable)");
				db.runSQL(
					"DROP_TABLE_IF_EXISTS(" + _companyPartitionName +
						".TestTable2)");
			}
		}
	}

	@Test
	public void testExportImportReportWithMissingView() throws Exception {
		DB db = DBManagerUtil.getDB();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			db.runSQL(
				"create view " +
					DBPartitionUtil.getPartitionName(_company.getCompanyId()) +
						".TestView as select * from Company");

			String reportContent = getReportContent();

			Assert.assertTrue(
				reportContent.contains(
					StringBundler.concat(
						"Virtual instance ", _company.getCompanyId(),
						" missing views: ",
						StringUtil.toLowerCase("TestView"))));
		}
		finally {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						PortalInstancePool.getDefaultCompanyId())) {

				db.runSQL(
					"drop view if exists " + _companyPartitionName +
						".TestView");
			}
		}
	}

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static String _companyPartitionName;
	private static ObjectDefinition _objectDBPartitionDefinition1;
	private static ObjectDefinition _objectDBPartitionDefinition2;

	@Inject
	private PersistenceManager _persistenceManager;

}