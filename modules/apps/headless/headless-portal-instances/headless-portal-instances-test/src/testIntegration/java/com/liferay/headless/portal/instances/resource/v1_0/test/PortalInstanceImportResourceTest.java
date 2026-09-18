/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.portal.instances.client.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.client.dto.v1_0.PortalInstanceImport;
import com.liferay.headless.portal.instances.client.problem.Problem;
import com.liferay.headless.portal.instances.client.resource.v1_0.PortalInstanceImportResource;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class PortalInstanceImportResourceTest
	extends BasePortalInstanceImportResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		if (!PropsValues.DATABASE_PARTITION_ENABLED) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					PortalInstancePool.getDefaultCompanyId())) {

			Company company = _companyLocalService.getCompany(
				PortalInstancePool.getDefaultCompanyId());

			User user = UserTestUtil.getAdminUser(company.getCompanyId());

			portalInstanceImportResource = PortalInstanceImportResource.builder(
			).authentication(
				user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				company.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();
		}
	}

	@Override
	@Test
	public void testPostPortalInstanceImport() throws Exception {
		super.testPostPortalInstanceImport();

		DB db = DBManagerUtil.getDB();

		Assume.assumeTrue(db.isSupportsDBPartition());

		if (!PropsValues.DATABASE_PARTITION_ENABLED) {
			_testPostPortalInstanceImportWithDBPartitionDisabled();

			return;
		}

		_testPostPortalInstanceImportExistingDBPartition();
		_testPostPortalInstanceImportInvalidSchemaName();
		_testPostPortalInstanceImportNonexistentDBPartition();
		_testPostPortalInstanceImportSuccess();
		_testPostPortalInstanceImportWithoutOmniadminPermission();
	}

	private PortalInstanceImportResource
			_createUserPortalInstanceImportResource()
		throws Exception {

		User user = UserTestUtil.addUser(testCompany, "test");

		return PortalInstanceImportResource.builder(
		).authentication(
			user.getEmailAddress(), "test"
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private void _deleteCompany(long companyId) throws Exception {
		String name = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		try {
			_companyLocalService.deleteCompany(companyId);
		}
		finally {
			PrincipalThreadLocal.setName(name);
		}
	}

	private void _dropExportedSchema(long companyId) throws Exception {
		DB db = DBManagerUtil.getDB();

		String sql =
			"drop schema if exists " +
				DBPartitionUtil.getExportedPartitionName(companyId);

		if (db.getDBType() == DBType.POSTGRESQL) {
			sql = sql + " cascade";
		}

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			preparedStatement.executeUpdate();
		}
	}

	private void _testPostPortalInstanceImportExistingDBPartition()
		throws Exception {

		Company company = CompanyTestUtil.addCompany();

		long companyId = company.getCompanyId();

		_companyLocalService.exportCompany(companyId);

		String randomId = StringUtil.toLowerCase(RandomTestUtil.randomString());

		PortalInstanceImport portalInstanceImport = new PortalInstanceImport();

		portalInstanceImport.setSchemaName(
			DBPartitionUtil.getExportedPartitionName(companyId));
		portalInstanceImport.setVirtualHost(
			randomId + "." +
				StringUtil.toLowerCase(RandomTestUtil.randomString(3)));
		portalInstanceImport.setWebId(randomId);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_PORTAL_INSTANCE_IMPORT_RESOURCE_IMPL,
				LoggerTestUtil.ERROR)) {

			portalInstanceImportResource.postPortalInstanceImport(
				portalInstanceImport);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Database partition " +
					DBPartitionUtil.getPartitionName(companyId) +
						" already exists",
				problem.getTitle());
		}
		finally {
			_deleteCompany(companyId);

			_dropExportedSchema(companyId);
		}
	}

	private void _testPostPortalInstanceImportInvalidSchemaName()
		throws Exception {

		PortalInstanceImport portalInstanceImport = new PortalInstanceImport();

		portalInstanceImport.setSchemaName(RandomTestUtil.randomString());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_PORTAL_INSTANCE_IMPORT_RESOURCE_IMPL,
				LoggerTestUtil.ERROR)) {

			portalInstanceImportResource.postPortalInstanceImport(
				portalInstanceImport);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
		}
	}

	private void _testPostPortalInstanceImportNonexistentDBPartition()
		throws Exception {

		String schemaName = DBPartitionUtil.getExportedPartitionName(
			RandomTestUtil.randomLong());

		PortalInstanceImport portalInstanceImport = new PortalInstanceImport();

		portalInstanceImport.setSchemaName(schemaName);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_PORTAL_INSTANCE_IMPORT_RESOURCE_IMPL,
				LoggerTestUtil.ERROR)) {

			portalInstanceImportResource.postPortalInstanceImport(
				portalInstanceImport);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Unable to insert the database partition " + schemaName +
					" because it does not exist",
				problem.getTitle());
		}
	}

	private void _testPostPortalInstanceImportSuccess() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		long companyId = company.getCompanyId();

		try {
			_companyLocalService.exportCompany(companyId);
		}
		finally {
			_deleteCompany(companyId);
		}

		String randomId = StringUtil.toLowerCase(RandomTestUtil.randomString());

		String virtualHost =
			randomId + "." +
				StringUtil.toLowerCase(RandomTestUtil.randomString(3));

		PortalInstanceImport portalInstanceImport = new PortalInstanceImport();

		portalInstanceImport.setSchemaName(
			DBPartitionUtil.getExportedPartitionName(companyId));
		portalInstanceImport.setVirtualHost(virtualHost);
		portalInstanceImport.setWebId(randomId);

		PortalInstance portalInstance =
			portalInstanceImportResource.postPortalInstanceImport(
				portalInstanceImport);

		try {
			Assert.assertEquals(
				Long.valueOf(companyId), portalInstance.getCompanyId());
			Assert.assertEquals(randomId, portalInstance.getPortalInstanceId());
			Assert.assertEquals(virtualHost, portalInstance.getVirtualHost());
		}
		finally {
			_deleteCompany(portalInstance.getCompanyId());

			_dropExportedSchema(companyId);
		}
	}

	private void _testPostPortalInstanceImportWithDBPartitionDisabled()
		throws Exception {

		PortalInstanceImport portalInstanceImport = new PortalInstanceImport();

		portalInstanceImport.setSchemaName(
			DBPartitionUtil.getExportedPartitionName(
				RandomTestUtil.randomLong()));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_PORTAL_INSTANCE_IMPORT_RESOURCE_IMPL,
				LoggerTestUtil.ERROR)) {

			portalInstanceImportResource.postPortalInstanceImport(
				portalInstanceImport);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Database partitioning must be enabled", problem.getTitle());
		}
	}

	private void _testPostPortalInstanceImportWithoutOmniadminPermission()
		throws Exception {

		PortalInstanceImportResource userPortalInstanceImportResource =
			_createUserPortalInstanceImportResource();

		PortalInstanceImport portalInstanceImport = new PortalInstanceImport();

		portalInstanceImport.setSchemaName(RandomTestUtil.randomString());

		try {
			userPortalInstanceImportResource.postPortalInstanceImport(
				portalInstanceImport);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private static final String
		_CLASS_NAME_PORTAL_INSTANCE_IMPORT_RESOURCE_IMPL =
			"com.liferay.headless.portal.instances.internal.resource.v1_0." +
				"PortalInstanceImportResourceImpl";

	@Inject
	private CompanyLocalService _companyLocalService;

}