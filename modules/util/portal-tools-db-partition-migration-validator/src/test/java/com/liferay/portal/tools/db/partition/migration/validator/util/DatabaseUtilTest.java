/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.partition.migration.validator.util;

import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.tools.db.partition.migration.validator.Company;
import com.liferay.portal.tools.db.partition.migration.validator.LiferayDatabase;
import com.liferay.portal.tools.db.partition.migration.validator.Release;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Luis Ortiz
 */
public class DatabaseUtilTest extends BaseTestCase {

	@Before
	public void setUp() throws Exception {
		mockGetColumns(
			Arrays.asList("Table1", "Company", "Table2", "Object_x_25000"));
		mockGetCompanies(Arrays.asList(_company1, _company2));
		mockGetCompanyIds(Collections.singletonList(25000L));
		mockGetCompanyInfos(Collections.singletonList(_COMPANY_ID));
		mockGetConnection(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
		mockGetReleases(Arrays.asList(_module1Release, _module2Release));
		mockGetTables(true);
	}

	@Test
	@TestInfo("LPD-102648")
	public void testExportLiferayDatabaseWithCompanyNamesInOtherSchemas()
		throws Exception {

		mockGetCatalogs(Arrays.asList("Schema1", "Schema2"));
		mockGetCompanyNames(Collections.singletonList(_company1));
		mockGetCompanyNames(
			Collections.singletonList(_company2), "Schema2.CompanyInfo");

		LiferayDatabase liferayDatabase = DatabaseUtil.exportLiferayDatabase(
			connection, _COMPANY_ID);

		_assert(liferayDatabase, true);
	}

	@Test
	@TestInfo("LPD-6742")
	public void testExportLiferayDatabaseWithDefaultCompany() throws Exception {
		LiferayDatabase liferayDatabase = DatabaseUtil.exportLiferayDatabase(
			connection, _COMPANY_ID);

		_assert(liferayDatabase, true);
	}

	@Test
	@TestInfo("LPD-6742")
	public void testExportLiferayDatabaseWithMultipleCompanies()
		throws Exception {

		List<Long> companyIds = Arrays.asList(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong());

		mockGetCompanyInfos(companyIds);

		LiferayDatabase liferayDatabase = DatabaseUtil.exportLiferayDatabase(
			connection, companyIds.get(0));

		Long companyId = companyIds.get(0);

		Assert.assertEquals(
			companyId.longValue(), liferayDatabase.getExportedCompanyId());
	}

	@Test
	@TestInfo("LPD-6742")
	public void testExportLiferayDatabaseWithNondefaultCompany()
		throws Exception {

		mockGetTables(false);

		LiferayDatabase liferayDatabase = DatabaseUtil.exportLiferayDatabase(
			connection, _COMPANY_ID);

		_assert(liferayDatabase, false);
	}

	@Test
	@TestInfo("LPD-39640")
	public void testExportLiferayDatabaseWithNonexistentCompanyId()
		throws Exception {

		List<Long> companyIds = Arrays.asList(
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong());

		mockGetCompanyInfos(companyIds);

		long randomCompanyId = RandomTestUtil.randomLong();

		try {
			DatabaseUtil.exportLiferayDatabase(connection, randomCompanyId);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof IllegalArgumentException);
			Assert.assertEquals(
				"Company " + randomCompanyId + " does not exist",
				exception.getMessage());
		}
	}

	@Test
	@TestInfo("LPD-16309")
	public void testReplaceSchemaName() {

		// Default JDBC URL

		Assert.assertEquals(
			"jdbc:mysql://localhost:3306/lportal",
			DatabaseUtil.replaceSchemaName(
				"jdbc:mysql://localhost:3306/lportal", null));
		Assert.assertEquals(
			"jdbc:postgresql://localhost:5432/lportal",
			DatabaseUtil.replaceSchemaName(
				"jdbc:postgresql://localhost:5432/lportal", null));

		// Replace schema name

		Assert.assertEquals(
			"jdbc:mysql://localhost:3306/schemaName",
			DatabaseUtil.replaceSchemaName(
				"jdbc:mysql://localhost:3306/lportal", "schemaName"));
		Assert.assertEquals(
			"jdbc:postgresql://localhost:5432/lportal?currentSchema=schemaName",
			DatabaseUtil.replaceSchemaName(
				"jdbc:postgresql://localhost:5432/lportal", "schemaName"));

		// Replace schema name with several URL parameters

		Assert.assertEquals(
			"jdbc:mysql://localhost:3306/schemaName?parameter=value",
			DatabaseUtil.replaceSchemaName(
				"jdbc:mysql://localhost:3306/lportal?parameter=value",
				"schemaName"));
		Assert.assertEquals(
			"jdbc:postgresql://localhost:5432/lportal?parameter=value&" +
				"currentSchema=schemaName",
			DatabaseUtil.replaceSchemaName(
				"jdbc:postgresql://localhost:5432/lportal?parameter=value",
				"schemaName"));
	}

	private void _assert(LiferayDatabase liferayDatabase, boolean isDefault) {
		List<Company> companies = liferayDatabase.getCompanies();

		Assert.assertEquals(companies.toString(), 2, companies.size());
		Assert.assertEquals(_company1, companies.get(0));
		Assert.assertEquals(_company2, companies.get(1));

		Assert.assertEquals(
			_COMPANY_ID, (Long)liferayDatabase.getExportedCompanyId());

		List<Release> releases = liferayDatabase.getReleases();

		Assert.assertEquals(releases.toString(), 2, releases.size());
		Assert.assertEquals(_module1Release, releases.get(0));
		Assert.assertEquals(_module2Release, releases.get(1));

		List<String> tableNames = liferayDatabase.getTableNames();

		Assert.assertEquals(tableNames.toString(), 2, tableNames.size());
		Assert.assertFalse(tableNames.contains("Company"));
		Assert.assertFalse(tableNames.contains("Object_x_25000"));
		Assert.assertTrue(tableNames.contains("Table1"));
		Assert.assertTrue(tableNames.contains("Table2"));

		Assert.assertEquals(
			isDefault, liferayDatabase.isExportedCompanyDefault());
	}

	private static final Long _COMPANY_ID = RandomTestUtil.randomLong();

	private final Company _company1 = new Company(
		RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
		RandomTestUtil.randomString(), RandomTestUtil.randomString());
	private final Company _company2 = new Company(
		RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
		RandomTestUtil.randomString(), RandomTestUtil.randomString());
	private final Release _module1Release = new Release(
		Version.parseVersion("14.2.4"), "module1", 0, true);
	private final Release _module2Release = new Release(
		Version.parseVersion("2.0.1"), "module2", 1, false);

}