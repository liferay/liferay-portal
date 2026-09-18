/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.portal.instances.client.dto.v1_0.PortalInstanceExport;
import com.liferay.headless.portal.instances.client.problem.Problem;
import com.liferay.headless.portal.instances.client.resource.v1_0.PortalInstanceExportResource;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class PortalInstanceExportResourceTest
	extends BasePortalInstanceExportResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BasePortalInstanceExportResourceTestCase.setUpClass();

		_company = CompanyTestUtil.addCompany();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		String name = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		try {
			_companyLocalService.deleteCompany(_company.getCompanyId());
		}
		finally {
			PrincipalThreadLocal.setName(name);
		}
	}

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

			portalInstanceExportResource = PortalInstanceExportResource.builder(
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
	public void testPostPortalInstanceExport() throws Exception {

		// The generated test posts a random entity and expects it back, but an
		// export computes its own result, so it does not apply here

		DB db = DBManagerUtil.getDB();

		Assume.assumeTrue(db.isSupportsDBPartition());

		_testPostPortalInstanceExport();
		_testPostPortalInstanceExportWithNonexistentPortalInstance();
		_testPostPortalInstanceExportWithoutOmniadminPermission();
	}

	private Configuration _createScopedConfiguration(
			Dictionary<String, Object> properties)
		throws Exception {

		Configuration configuration =
			_configurationAdmin.createFactoryConfiguration(
				"com.liferay.headless.portal.instances.internal.test." +
					RandomTestUtil.randomString(),
				StringPool.QUESTION);

		configuration.update(properties);

		return configuration;
	}

	private PortalInstanceExportResource
			_createUserPortalInstanceExportResource()
		throws Exception {

		User user = UserTestUtil.addUser(testCompany, "test");

		return PortalInstanceExportResource.builder(
		).authentication(
			user.getEmailAddress(), "test"
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
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

	private List<String> _getExportedConfigurationIds(long companyId)
		throws Exception {

		List<String> configurationIds = new ArrayList<>();

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select configurationId from ",
					DBPartitionUtil.getExportedPartitionName(companyId),
					".Configuration_"));

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				configurationIds.add(resultSet.getString("configurationId"));
			}
		}

		return configurationIds;
	}

	private void _testPostPortalInstanceExport() throws Exception {
		long companyId = _company.getCompanyId();

		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			try {
				PortalInstanceExport portalInstanceExport =
					portalInstanceExportResource.postPortalInstanceExport(
						_toPortalInstanceExport(_company.getWebId()));

				Assert.assertEquals(
					DBPartitionUtil.getExportedPartitionName(companyId),
					portalInstanceExport.getExportedPartitionName());
				Assert.assertEquals(
					Long.valueOf(companyId),
					portalInstanceExport.getSourceCompanyId());
			}
			finally {
				_dropExportedSchema(companyId);
			}

			return;
		}

		List<Configuration> configurations = new ArrayList<>();

		try {
			Configuration company1Configuration = _createScopedConfiguration(
				HashMapDictionaryBuilder.<String, Object>put(
					ExtendedObjectClassDefinition.Scope.COMPANY.
						getPropertyKey(),
					companyId
				).build());

			configurations.add(company1Configuration);

			Configuration company2Configuration = _createScopedConfiguration(
				HashMapDictionaryBuilder.<String, Object>put(
					ExtendedObjectClassDefinition.Scope.COMPANY.
						getPropertyKey(),
					RandomTestUtil.randomLong()
				).build());

			configurations.add(company2Configuration);

			Group group = _groupLocalService.getCompanyGroup(companyId);

			Configuration groupConfiguration = _createScopedConfiguration(
				HashMapDictionaryBuilder.<String, Object>put(
					ExtendedObjectClassDefinition.Scope.COMPANY.
						getPropertyKey(),
					companyId
				).put(
					ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey(),
					group.getGroupId()
				).build());

			configurations.add(groupConfiguration);

			Configuration nonexistentGroupConfiguration =
				_createScopedConfiguration(
					HashMapDictionaryBuilder.<String, Object>put(
						ExtendedObjectClassDefinition.Scope.COMPANY.
							getPropertyKey(),
						companyId
					).put(
						ExtendedObjectClassDefinition.Scope.GROUP.
							getPropertyKey(),
						RandomTestUtil.randomLong()
					).build());

			configurations.add(nonexistentGroupConfiguration);

			Configuration portletInstanceConfiguration =
				_createScopedConfiguration(
					HashMapDictionaryBuilder.<String, Object>put(
						ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
							getPropertyKey(),
						RandomTestUtil.randomString()
					).build());

			configurations.add(portletInstanceConfiguration);

			PortalInstanceExport portalInstanceExport =
				portalInstanceExportResource.postPortalInstanceExport(
					_toPortalInstanceExport(_company.getWebId()));

			Assert.assertEquals(
				DBPartitionUtil.getExportedPartitionName(companyId),
				portalInstanceExport.getExportedPartitionName());
			Assert.assertEquals(
				Long.valueOf(companyId),
				portalInstanceExport.getSourceCompanyId());

			List<String> configurationIds = _getExportedConfigurationIds(
				companyId);

			Assert.assertTrue(
				configurationIds.contains(company1Configuration.getPid()));
			Assert.assertTrue(
				configurationIds.contains(groupConfiguration.getPid()));
			Assert.assertTrue(
				configurationIds.contains(
					portletInstanceConfiguration.getPid()));
			Assert.assertFalse(
				configurationIds.contains(company2Configuration.getPid()));
			Assert.assertFalse(
				configurationIds.contains(
					nonexistentGroupConfiguration.getPid()));
		}
		finally {
			_dropExportedSchema(companyId);

			for (Configuration configuration : configurations) {
				configuration.delete();
			}
		}
	}

	private void _testPostPortalInstanceExportWithNonexistentPortalInstance()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_PORTAL_INSTANCE_EXPORT_RESOURCE_IMPL,
				LoggerTestUtil.ERROR)) {

			portalInstanceExportResource.postPortalInstanceExport(
				_toPortalInstanceExport(RandomTestUtil.randomString()));

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private void _testPostPortalInstanceExportWithoutOmniadminPermission()
		throws Exception {

		PortalInstanceExportResource userPortalInstanceExportResource =
			_createUserPortalInstanceExportResource();

		try {
			userPortalInstanceExportResource.postPortalInstanceExport(
				_toPortalInstanceExport(_company.getWebId()));

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private PortalInstanceExport _toPortalInstanceExport(
		String portalInstanceId) {

		PortalInstanceExport portalInstanceExport = new PortalInstanceExport();

		portalInstanceExport.setPortalInstanceId(() -> portalInstanceId);

		return portalInstanceExport;
	}

	private static final String
		_CLASS_NAME_PORTAL_INSTANCE_EXPORT_RESOURCE_IMPL =
			"com.liferay.headless.portal.instances.internal.resource.v1_0." +
				"PortalInstanceExportResourceImpl";

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private GroupLocalService _groupLocalService;

}