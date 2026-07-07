/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.server.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.server.client.dto.v1_0.DatabaseSchemaExport;
import com.liferay.headless.admin.server.client.problem.Problem;
import com.liferay.headless.admin.server.client.resource.v1_0.DatabaseSchemaExportResource;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class DatabaseSchemaExportResourceTest
	extends BaseDatabaseSchemaExportResourceTestCase {

	@Override
	@Test
	public void testPostDatabaseSchemaExport() throws Exception {
		Assume.assumeFalse(PropsValues.DATABASE_PARTITION_ENABLED);

		_testPostDatabaseSchemaExport();
		_testPostDatabaseSchemaExportWithoutExportFilesPath();
		_testPostDatabaseSchemaExportWithoutOmniadminPermission();
	}

	private void _testPostDatabaseSchemaExport() throws Exception {
		Path path = Files.createTempDirectory("db-migration-schema-export");

		File directory = path.toFile();

		try {
			DatabaseSchemaExport databaseSchemaExport =
				databaseSchemaExportResource.postDatabaseSchemaExport(
					new DatabaseSchemaExport() {
						{
							exportFilesPath = directory.getAbsolutePath();
						}
					});

			Assert.assertEquals(
				directory.getAbsolutePath(),
				databaseSchemaExport.getExportFilesPath());

			List<String> fileNames = Arrays.asList(
				databaseSchemaExport.getFileNames());

			Assert.assertTrue(fileNames.contains("indexes.sql"));
			Assert.assertTrue(fileNames.contains("tables.sql"));

			String reportFileName = databaseSchemaExport.getReportFileName();

			Assert.assertEquals(
				"db_migration_schema_export_report.txt", reportFileName);

			File reportFile = new File(directory, reportFileName);

			Assert.assertTrue(reportFile.exists());
		}
		finally {
			FileUtil.deltree(directory);
		}
	}

	private void _testPostDatabaseSchemaExportWithoutExportFilesPath()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			databaseSchemaExportResource.postDatabaseSchemaExport(
				new DatabaseSchemaExport());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
		}
	}

	private void _testPostDatabaseSchemaExportWithoutOmniadminPermission()
		throws Exception {

		User user = UserTestUtil.addUser(testCompany, "test");

		DatabaseSchemaExportResource userDatabaseSchemaExportResource =
			DatabaseSchemaExportResource.builder(
			).authentication(
				user.getEmailAddress(), "test"
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			userDatabaseSchemaExportResource.postDatabaseSchemaExport(
				new DatabaseSchemaExport() {
					{
						exportFilesPath = SystemProperties.get(
							SystemProperties.TMP_DIR);
					}
				});

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("UNAUTHORIZED", problem.getStatus());
		}
	}

}