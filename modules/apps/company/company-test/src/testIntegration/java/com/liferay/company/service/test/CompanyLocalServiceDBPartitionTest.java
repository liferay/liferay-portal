/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.company.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.model.Counter;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.counter.kernel.service.persistence.CounterFinder;
import com.liferay.counter.model.CounterRegister;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.db.partition.db.DBPartitionDB;
import com.liferay.portal.db.partition.test.util.BaseDBPartitionTestCase;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.RepositoryFactory;
import com.liferay.portal.kernel.repository.registry.RepositoryDefiner;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.repository.liferayrepository.LiferayRepository;
import com.liferay.portal.repository.registry.RepositoryClassDefinition;
import com.liferay.portal.repository.registry.RepositoryClassDefinitionCatalogUtil;
import com.liferay.portal.service.impl.ClassNameLocalServiceImpl;
import com.liferay.portal.service.impl.CompanyLocalServiceImpl;
import com.liferay.portal.service.impl.ResourceActionLocalServiceImpl;
import com.liferay.portal.spring.aop.AopInvocationHandler;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.util.PortalInstances;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.portlet.Portlet;

import org.apache.felix.cm.PersistenceManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Mariano Álvaro Sáiz
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class CompanyLocalServiceDBPartitionTest
	extends BaseDBPartitionTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			TransactionalTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDBPartitionTestCase.setUpClass();

		Bundle bundle = FrameworkUtil.getBundle(
			CompanyLocalServiceDBPartitionTest.class);

		_bundleContext = bundle.getBundleContext();

		_defaultCompanyId = PortalInstancePool.getDefaultCompanyId();
	}

	@After
	public void tearDown() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();

			_serviceRegistration = null;
		}
	}

	@Test
	public void testAddCompany() throws Exception {
		int dbPartitionsCount = _getDBPartitionsCount();
		int rulesCount = _getRulesCount(defaultPartitionName);

		_company1 = CompanyTestUtil.addCompany();

		Assert.assertTrue(
			ArrayUtil.contains(
				_getCompanyIdsBySQL(), _company1.getCompanyId()));

		Assert.assertEquals(dbPartitionsCount + 1, _getDBPartitionsCount());
		Assert.assertEquals(
			rulesCount,
			_getRulesCount(getPartitionName(_company1.getCompanyId())));
	}

	@Test
	public void testAddCompanyUsesVirtualHostCounter() throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_defaultCompanyId)) {

			long counter = _counterLocalService.increment();

			_company1 = CompanyTestUtil.addCompany();

			VirtualHost virtualHost = _virtualHostLocalService.getVirtualHost(
				_company1.getVirtualHostname());

			Assert.assertEquals(counter + 1, virtualHost.getVirtualHostId());

			_company2 = CompanyTestUtil.addCompany();

			virtualHost = _virtualHostLocalService.getVirtualHost(
				_company2.getVirtualHostname());

			Assert.assertEquals(counter + 2, virtualHost.getVirtualHostId());
		}
	}

	@Test
	public void testAddCompanyWhenCompanyLocalServiceFails() throws Exception {
		long[] companyIds = _getCompanyIdsBySQL();
		int dbPartitionsCount = _getDBPartitionsCount();

		Company company = null;

		AopInvocationHandler aopInvocationHandler =
			ProxyUtil.fetchInvocationHandler(
				companyLocalService, AopInvocationHandler.class);

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					(CompanyLocalServiceImpl)aopInvocationHandler.getTarget(),
					"_dlFileEntryTypeLocalService", null)) {

			company = companyLocalService.addCompany(
				RandomTestUtil.randomLong(), "test.com", "test.com", "test.com",
				0, true, true, null, null, null, null, null, null);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertArrayEquals(companyIds, _getCompanyIdsBySQL());
			Assert.assertEquals(dbPartitionsCount, _getDBPartitionsCount());
		}
		finally {
			if (company != null) {
				removeDBPartitions(new long[] {company.getCompanyId()});
			}
		}
	}

	@Test
	public void testAddCompanyWhenDBPartitionUtilFails() throws Exception {
		long[] companyIds = _getCompanyIdsBySQL();
		int dbPartitionsCount = _getDBPartitionsCount();

		Company company = null;

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					DBPartitionUtil.class, "_dbPartitionDB",
					ProxyUtil.newProxyInstance(
						DBPartitionDB.class.getClassLoader(),
						new Class<?>[] {DBPartitionDB.class},
						(proxy, method, args) -> {
							if (Objects.equals(
									method.getName(), "getCreateTableSQL")) {

								throw new Exception();
							}

							return method.invoke(dbPartitionDB, args);
						}))) {

			company = CompanyTestUtil.addCompany();

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertArrayEquals(companyIds, _getCompanyIdsBySQL());
			Assert.assertEquals(dbPartitionsCount, _getDBPartitionsCount());
		}
		finally {
			if (company != null) {
				companyLocalService.deleteCompany(company);
			}
		}
	}

	@Test
	public void testAddDBPartitionCompany() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		Configuration configuration = _createFactoryConfiguration(
			company.getCompanyId());

		String pid = configuration.getPid();

		companyLocalService.extractDBPartitionCompany(company.getCompanyId());

		try {
			_assertConfiguration(pid, true);

			String name = "new" + company.getName();
			String virtualHostName = "new" + company.getVirtualHostname();
			String webId = "new" + company.getWebId();

			try {
				company = companyLocalService.addDBPartitionCompany(
					company.getCompanyId(), name, virtualHostName, webId);

				Assert.fail();
			}
			catch (Exception exception) {
				Assert.assertTrue(
					exception instanceof IllegalArgumentException);

				Assert.assertTrue(
					dbPartitionDB.existsPartition(
						connection,
						getExtractedPartitionName(company.getCompanyId())));
			}

			companyLocalService.deleteCompany(company);

			company = companyLocalService.addDBPartitionCompany(
				company.getCompanyId(), name, virtualHostName, webId);

			Assert.assertTrue(
				ArrayUtil.contains(
					_getCompanyIdsBySQL(), company.getCompanyId()));

			Assert.assertEquals(name, company.getName());
			Assert.assertEquals(virtualHostName, company.getVirtualHostname());
			Assert.assertEquals(webId, company.getWebId());

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						company.getCompanyId())) {

				_assertConfiguration(pid, true);
			}
		}
		finally {
			db.runSQL(
				dbPartitionDB.getDropPartitionSQL(
					getExtractedPartitionName(company.getCompanyId())));

			if (ArrayUtil.contains(
					_getCompanyIdsBySQL(), company.getCompanyId())) {

				companyLocalService.deleteCompany(company);
			}
			else {
				removeDBPartitions(new long[] {company.getCompanyId()});
			}
		}
	}

	@Test
	public void testAddDBPartitionCompanyWhenCompanyLocalServiceFails()
		throws Exception {

		Company company = CompanyTestUtil.addCompany();

		try {
			companyLocalService.extractDBPartitionCompany(
				company.getCompanyId());

			_companyLocalService.deleteCompany(company);

			Company defaultCompany = companyLocalService.getCompany(
				_defaultCompanyId);

			try {
				companyLocalService.addDBPartitionCompany(
					company.getCompanyId(), null, null,
					defaultCompany.getWebId());

				Assert.fail();
			}
			catch (Exception exception) {
				Assert.assertFalse(
					ArrayUtil.contains(
						_getCompanyIdsBySQL(), company.getCompanyId()));

				_checkStandaloneDBPartitionTables(
					getExtractedPartitionName(company.getCompanyId()),
					"Company", "VirtualHost");
			}
		}
		finally {
			db.runSQL(
				dbPartitionDB.getDropPartitionSQL(
					getExtractedPartitionName(company.getCompanyId())));

			if (ArrayUtil.contains(
					_getCompanyIdsBySQL(), company.getCompanyId())) {

				companyLocalService.deleteCompany(company);
			}
			else {
				removeDBPartitions(new long[] {company.getCompanyId()});
			}
		}
	}

	@Test
	public void testAddDBPartitionCompanyWhenDBPartitionUtilFails()
		throws Exception {

		Company company = CompanyTestUtil.addCompany();

		try {
			companyLocalService.extractDBPartitionCompany(
				company.getCompanyId());

			_companyLocalService.deleteCompany(company);

			try (AutoCloseable autoCloseable =
					ReflectionTestUtil.setFieldValueWithAutoCloseable(
						DBPartitionUtil.class, "_dbPartitionDB",
						ProxyUtil.newProxyInstance(
							DBPartitionDB.class.getClassLoader(),
							new Class<?>[] {DBPartitionDB.class},
							(proxy, method, args) -> {
								if (Objects.equals(
										method.getName(), "getCreateViewSQL") &&
									StringUtil.equalsIgnoreCase(
										(String)args[2], "VirtualHost")) {

									throw new Exception();
								}

								return method.invoke(dbPartitionDB, args);
							}))) {

				company = companyLocalService.addDBPartitionCompany(
					company.getCompanyId(), null, null, null);

				Assert.fail();
			}
			catch (PortalException portalException) {
				Assert.assertFalse(
					ArrayUtil.contains(
						_getCompanyIdsBySQL(), company.getCompanyId()));

				_checkStandaloneDBPartitionTables(
					getExtractedPartitionName(company.getCompanyId()),
					"Company", "VirtualHost");
			}
		}
		finally {
			db.runSQL(
				dbPartitionDB.getDropPartitionSQL(
					getExtractedPartitionName(company.getCompanyId())));

			if (ArrayUtil.contains(
					_getCompanyIdsBySQL(), company.getCompanyId())) {

				companyLocalService.deleteCompany(company);
			}
			else {
				removeDBPartitions(new long[] {company.getCompanyId()});
			}
		}
	}

	@Test
	public void testCopyDBPartitionCompany() throws Exception {
		int rulesCount = _getRulesCount(defaultPartitionName);

		Configuration configuration = _createFactoryConfiguration(
			TestPropsValues.getCompanyId());

		String name = RandomTestUtil.randomString();
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();
		String virtualHostname = StringUtil.toLowerCase(
			RandomTestUtil.randomString());
		String webId = RandomTestUtil.randomString();

		Company copiedCompany = null;

		try {
			copiedCompany = companyLocalService.copyDBPartitionCompany(
				TestPropsValues.getCompanyId(), null, name, virtualHostname,
				webId);

			_assertCopyDBPartitionCompany(
				copiedCompany, name, virtualHostname, webId);

			long copiedCompanyId = copiedCompany.getCompanyId();

			_assertCompanyConfiguration(copiedCompanyId, configuration);

			_addCopyDBPartitionCompanyCache(copiedCompanyId);

			Collection<ServiceReference<Portlet>> serviceReferences =
				_bundleContext.getServiceReferences(
					Portlet.class,
					StringBundler.concat(
						"(&(com.liferay.portlet.company=",
						copiedCompany.getCompanyId(), ")(javax.portlet.name=",
						objectDefinition.getPortletId(), "))"));

			Assert.assertFalse(serviceReferences.isEmpty());

			companyLocalService.deleteCompany(copiedCompany);

			copiedCompany = companyLocalService.copyDBPartitionCompany(
				TestPropsValues.getCompanyId(), copiedCompanyId, name,
				virtualHostname, webId);

			Assert.assertEquals(copiedCompanyId, copiedCompany.getCompanyId());

			_assertCopyDBPartitionCompanyCache(copiedCompanyId);

			_assertCopyDBPartitionCompany(
				copiedCompany, name, virtualHostname, webId);
			_assertCopyDBPartitionCompanyId(
				TestPropsValues.getCompanyId(), copiedCompany.getCompanyId());

			Assert.assertEquals(
				rulesCount,
				_getRulesCount(getPartitionName(copiedCompany.getCompanyId())));

			SafeCloseable safeCloseable =
				PortalInstances.setCopyInProcessCompanyIdWithSafeCloseable(
					copiedCompanyId);

			safeCloseable.close();
		}
		finally {
			if (_className1 != null) {
				_classNameLocalService.deleteClassName(_className1);
			}

			if (_className2 != null) {
				_classNameLocalService.deleteClassName(_className2);
			}

			if (configuration != null) {
				ConfigurationTestUtil.deleteConfiguration(configuration);
			}

			if (copiedCompany != null) {
				companyLocalService.deleteCompany(copiedCompany.getCompanyId());
			}
		}
	}

	@Test
	public void testCopyDBPartitionCompanyWhenCompanyLocalServiceFails()
		throws Exception {

		Company company = CompanyTestUtil.addCompany();

		long toCompanyId = RandomTestUtil.nextLong();

		try {
			companyLocalService.copyDBPartitionCompany(
				company.getCompanyId(), toCompanyId, company.getName(),
				company.getVirtualHostname(), company.getWebId());

			Assert.fail();
		}
		catch (PortalException portalException) {
			Assert.assertFalse(
				ArrayUtil.contains(_getCompanyIdsBySQL(), toCompanyId));

			_checkPartitionDoesNotExist(toCompanyId);

			SafeCloseable safeCloseable =
				PortalInstances.setCopyInProcessCompanyIdWithSafeCloseable(
					toCompanyId);

			safeCloseable.close();
		}
		finally {
			companyLocalService.deleteCompany(company);
		}
	}

	@Test
	public void testCopyDBPartitionCompanyWhenDBPartitionUtilFails()
		throws Exception {

		Company company = CompanyTestUtil.addCompany();

		String name = RandomTestUtil.randomString();
		long toCompanyId = RandomTestUtil.nextLong();
		String virtualHostname = StringUtil.toLowerCase(
			RandomTestUtil.randomString());
		String webId = RandomTestUtil.randomString();

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					DBPartitionUtil.class, "_dbPartitionDB",
					ProxyUtil.newProxyInstance(
						DBPartitionDB.class.getClassLoader(),
						new Class<?>[] {DBPartitionDB.class},
						(proxy, method, args) -> {
							if (Objects.equals(
									method.getName(), "getCreateViewSQL") &&
								StringUtil.equalsIgnoreCase(
									(String)args[2], "VirtualHost")) {

								throw new Exception();
							}

							return method.invoke(dbPartitionDB, args);
						}))) {

			companyLocalService.copyDBPartitionCompany(
				company.getCompanyId(), toCompanyId, name, virtualHostname,
				webId);

			Assert.fail();
		}
		catch (PortalException portalException) {
			Assert.assertFalse(
				ArrayUtil.contains(_getCompanyIdsBySQL(), toCompanyId));

			_checkPartitionDoesNotExist(toCompanyId);

			SafeCloseable safeCloseable =
				PortalInstances.setCopyInProcessCompanyIdWithSafeCloseable(
					toCompanyId);

			safeCloseable.close();
		}
		finally {
			companyLocalService.deleteCompany(company);
		}
	}

	@Test
	public void testDeleteCompany() throws Exception {
		_company1 = CompanyTestUtil.addCompany();

		Configuration configuration = _createFactoryConfiguration(
			_company1.getCompanyId());

		String pid = configuration.getPid();

		_createRepositories(_company1);

		_assertCache(_company1.getCompanyId(), true);

		int dbPartitionsCount = _getDBPartitionsCount();

		companyLocalService.deleteCompany(_company1);

		Assert.assertFalse(
			ArrayUtil.contains(
				_getCompanyIdsBySQL(), _company1.getCompanyId()));

		Assert.assertEquals(dbPartitionsCount - 1, _getDBPartitionsCount());

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Collection<ServiceReference<Portlet>> serviceReferences =
			bundleContext.getServiceReferences(
				Portlet.class,
				"(com.liferay.portlet.company=" + _company1.getCompanyId() +
					")");

		Assert.assertTrue(serviceReferences.isEmpty());

		_assertCache(_company1.getCompanyId(), false);

		_assertConfiguration(pid, false);
	}

	@Test
	public void testDeleteCompanyWhenDBPartitionUtilFails() throws Exception {
		_company1 = CompanyTestUtil.addCompany();

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					DBPartitionUtil.class, "_dbPartitionDB",
					ProxyUtil.newProxyInstance(
						DBPartitionDB.class.getClassLoader(),
						new Class<?>[] {DBPartitionDB.class},
						(proxy, method, args) -> {
							if (Objects.equals(
									method.getName(), "getDropPartitionSQL")) {

								throw new Exception();
							}

							return method.invoke(dbPartitionDB, args);
						}))) {

			companyLocalService.deleteCompany(_company1);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(
				ArrayUtil.contains(
					_getCompanyIdsBySQL(), _company1.getCompanyId()));
		}
	}

	@Test
	public void testExtractDBPartitionCompany() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		try {
			Configuration configuration = _createFactoryConfiguration(
				company.getCompanyId());

			String pid = configuration.getPid();

			companyLocalService.extractDBPartitionCompany(
				company.getCompanyId());

			Assert.assertTrue(
				ArrayUtil.contains(
					_getCompanyIdsBySQL(), company.getCompanyId()));
			Assert.assertTrue(
				dbPartitionDB.existsPartition(
					connection,
					getExtractedPartitionName(company.getCompanyId())));

			_checkStandaloneDBPartitionTables(
				getExtractedPartitionName(company.getCompanyId()), "Company",
				"VirtualHost");

			Collection<ServiceReference<Portlet>> serviceReferences =
				_bundleContext.getServiceReferences(
					Portlet.class,
					"(com.liferay.portlet.company=" + company.getCompanyId() +
						")");

			Assert.assertFalse(serviceReferences.isEmpty());

			_assertConfiguration(pid, true);
		}
		finally {
			db.runSQL(
				dbPartitionDB.getDropPartitionSQL(
					getExtractedPartitionName(company.getCompanyId())));

			companyLocalService.deleteCompany(company);
		}
	}

	@Test
	public void testExtractDBPartitionCompanyWhenDBPartitionUtilFails()
		throws Exception {

		Company company = CompanyTestUtil.addCompany();

		int tablesCount = _getTablesCount(company.getCompanyId());
		int viewsCount = _getViewsCount(company.getCompanyId());

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					DBPartitionUtil.class, "_dbPartitionDB",
					ProxyUtil.newProxyInstance(
						DBPartitionDB.class.getClassLoader(),
						new Class<?>[] {DBPartitionDB.class},
						(proxy, method, args) -> {
							if (Objects.equals(
									method.getName(), "getCreateTableSQL") &&
								StringUtil.equalsIgnoreCase(
									(String)args[3], "VirtualHost")) {

								throw new Exception();
							}

							return method.invoke(dbPartitionDB, args);
						}))) {

			companyLocalService.extractDBPartitionCompany(
				company.getCompanyId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(
				ArrayUtil.contains(
					_getCompanyIdsBySQL(), company.getCompanyId()));
			Assert.assertEquals(
				tablesCount, _getTablesCount(company.getCompanyId()));
			Assert.assertEquals(
				viewsCount, _getViewsCount(company.getCompanyId()));
			Assert.assertFalse(
				dbPartitionDB.existsPartition(
					connection,
					getExtractedPartitionName(company.getCompanyId())));
		}
		finally {
			db.runSQL(
				dbPartitionDB.getDropPartitionSQL(
					getExtractedPartitionName(company.getCompanyId())));

			companyLocalService.deleteCompany(company);
		}
	}

	private void _addCopyDBPartitionCompanyCache(long companyId) {
		_className1 = _classNameLocalService.addClassName(_CLASS_NAME_1);
		_className2 = _classNameLocalService.addClassName(_CLASS_NAME_2);

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			// Reverse order to generate different class name IDs

			_classNameLocalService.addClassName(_CLASS_NAME_2);

			_classNameLocalService.addClassName(_CLASS_NAME_1);

			_counter = _counterLocalService.increment(
				CompanyLocalServiceDBPartitionTest.class.getName());

			_counterLocalService.reset(
				CompanyLocalServiceDBPartitionTest.class.getName(), 100000);
		}
	}

	private void _assertCache(long companyId, boolean cached) throws Exception {
		Map<Long, Map<String, Long>> classNameIdsMap =
			ReflectionTestUtil.getFieldValue(
				Class.forName(
					ClassNameLocalServiceImpl.class.getName() +
						"$ClassNamePool"),
				"_classNameIdsMap");

		Assert.assertEquals(cached, classNameIdsMap.containsKey(companyId));

		Map<Long, Map<Long, ClassName>> classNamesMap =
			ReflectionTestUtil.getFieldValue(
				Class.forName(
					ClassNameLocalServiceImpl.class.getName() +
						"$ClassNamePool"),
				"_classNamesMap");

		Assert.assertEquals(cached, classNamesMap.containsKey(companyId));

		Map<String, CounterRegister> counterRegisterMap =
			ReflectionTestUtil.getFieldValue(
				_counterFinder, "_counterRegisterMap");

		Assert.assertEquals(
			cached,
			counterRegisterMap.containsKey(
				Counter.class.getName() + StringPool.AT + companyId));

		RepositoryClassDefinition repositoryClassDefinition =
			RepositoryClassDefinitionCatalogUtil.getRepositoryClassDefinition(
				companyId, CompanyLocalServiceDBPartitionTest.class.getName());

		Assert.assertEquals(
			cached,
			MapUtil.isNotEmpty(
				(Map<Long, Map<Long, LocalRepository>>)
					ReflectionTestUtil.getFieldValue(
						repositoryClassDefinition, "_localRepositoriesMap")));
		Assert.assertEquals(
			cached,
			MapUtil.isNotEmpty(
				(Map<Long, Map<Long, Repository>>)
					ReflectionTestUtil.getFieldValue(
						repositoryClassDefinition, "_repositoriesMap")));

		Assert.assertEquals(cached, _hasResourceActionsCached(companyId));
	}

	private void _assertCompanyConfiguration(
			long companyId, Configuration configuration)
		throws SQLException {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select configurationId, dictionary from ",
					getPartitionName(companyId),
					".Configuration_ where configurationId like '",
					configuration.getFactoryPid(), "%'"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertTrue(resultSet.next());

			String configurationId = resultSet.getString("configurationId");

			Assert.assertTrue(_persistenceManager.exists(configurationId));

			String dictionary = resultSet.getString("dictionary");

			Assert.assertTrue(dictionary.contains(String.valueOf(companyId)));
			Assert.assertFalse(dictionary.contains(configuration.getPid()));
		}
	}

	private void _assertConfiguration(String pid, boolean exists)
		throws Exception {

		if (exists) {
			Assert.assertNotNull(
				_configurationAdmin.listConfigurations(
					"(service.pid=" + pid + ")"));

			Assert.assertTrue(_persistenceManager.exists(pid));

			return;
		}

		Assert.assertNull(
			_configurationAdmin.listConfigurations(
				"(service.pid=" + pid + ")"));

		Assert.assertFalse(_persistenceManager.exists(pid));
	}

	private void _assertCopyDBPartitionCompany(
			Company company, String name, String virtualHostname, String webId)
		throws Exception {

		Assert.assertTrue(
			ArrayUtil.contains(_getCompanyIdsBySQL(), company.getCompanyId()));
		Assert.assertEquals(name, company.getName());
		Assert.assertEquals(virtualHostname, company.getVirtualHostname());
		Assert.assertEquals(webId, company.getWebId());

		_virtualHostLocalService.getVirtualHost(virtualHostname);
	}

	private void _assertCopyDBPartitionCompanyCache(long companyId) {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			Assert.assertEquals(
				_className1,
				_classNameLocalService.getClassName(_CLASS_NAME_1));
			Assert.assertEquals(
				_className2,
				_classNameLocalService.getClassName(_CLASS_NAME_2));

			Assert.assertEquals(
				_counter,
				_counterLocalService.increment(
					CompanyLocalServiceDBPartitionTest.class.getName()));

			Assert.assertTrue(_hasResourceActionsCached(companyId));
		}
	}

	private void _assertCopyDBPartitionCompanyId(
			long companyId, long copiedCompanyId)
		throws Exception {

		DBInspector dbInspector = new DBInspector(connection);
		List<String> tableNames = new ArrayList<>();

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		try (ResultSet resultSet = databaseMetaData.getTables(
				dbPartitionDB.getCatalog(
					connection, getPartitionName(copiedCompanyId)),
				dbPartitionDB.getSchema(
					connection, getPartitionName(copiedCompanyId)),
				null, new String[] {"TABLE"})) {

			while (resultSet.next()) {
				String tableName = resultSet.getString("TABLE_NAME");

				if (dbInspector.isControlTable(tableName)) {
					continue;
				}

				tableNames.add(tableName);
			}
		}

		for (String tableName : tableNames) {
			try (ResultSet resultSet = databaseMetaData.getColumns(
					dbPartitionDB.getCatalog(
						connection, getPartitionName(copiedCompanyId)),
					dbPartitionDB.getSchema(
						connection, getPartitionName(copiedCompanyId)),
					tableName, null)) {

				while (resultSet.next()) {
					int columnType = resultSet.getInt("DATA_TYPE");

					if ((columnType != Types.BIGINT) &&
						(columnType != Types.LONGVARCHAR) &&
						(columnType != Types.VARCHAR)) {

						continue;
					}

					String columnName = resultSet.getString("COLUMN_NAME");

					String whereClause = StringBundler.concat(
						columnName, " like '%", companyId, "%'");

					if (columnType == Types.BIGINT) {
						whereClause = StringBundler.concat(
							"CAST_LONG(", columnName, ") = ", companyId);
					}
					else if (columnType == Types.LONGVARCHAR) {
						whereClause = StringBundler.concat(
							"CAST_TEXT(", columnName, ") like '%", companyId,
							"%'");
					}

					PreparedStatement preparedStatement =
						connection.prepareStatement(
							SQLTransformer.transform(
								StringBundler.concat(
									"select ", columnName, " from ",
									DBPartitionUtil.getPartitionName(
										copiedCompanyId),
									StringPool.PERIOD, tableName, " where ",
									whereClause)));

					try (ResultSet resultSet2 =
							preparedStatement.executeQuery()) {

						if (resultSet2.next()) {
							Assert.fail(
								StringBundler.concat(
									"Company ID ", companyId,
									" is present in the copied database ",
									"schema in ", tableName, StringPool.PERIOD,
									columnName, StringPool.COLON,
									StringPool.SPACE, resultSet2.getObject(1)));
						}
					}
				}
			}
		}
	}

	private void _checkPartitionDoesNotExist(long companyId)
		throws SQLException {

		List<String> partitionNames = new ArrayList<>();

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		if (db.getDBType() == DBType.MYSQL) {
			try (ResultSet resultSet = databaseMetaData.getSchemas()) {
				while (resultSet.next()) {
					partitionNames.add(resultSet.getString("TABLE_SCHEM"));
				}
			}
		}
		else {
			try (ResultSet resultSet = databaseMetaData.getCatalogs()) {
				while (resultSet.next()) {
					partitionNames.add(resultSet.getString("TABLE_CAT"));
				}
			}
		}

		Assert.assertFalse(
			partitionNames.contains(getPartitionName(companyId)));
	}

	private void _checkStandaloneDBPartitionTables(
			String partitionName, String... expectedTableNames)
		throws Exception {

		List<String> tableNames = new ArrayList<>();

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		try (ResultSet resultSet = databaseMetaData.getTables(
				dbPartitionDB.getCatalog(connection, partitionName),
				dbPartitionDB.getSchema(connection, partitionName), null,
				new String[] {"TABLE"})) {

			while (resultSet.next()) {
				tableNames.add(
					StringUtil.toUpperCase(resultSet.getString("TABLE_NAME")));
			}
		}

		for (String expectedTableName : expectedTableNames) {
			Assert.assertTrue(
				tableNames.contains(StringUtil.toUpperCase(expectedTableName)));
		}
	}

	private Configuration _createFactoryConfiguration(long companyId)
		throws Exception {

		String pid = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			pid = ConfigurationTestUtil.createFactoryConfiguration(
				CompanyLocalServiceDBPartitionTest.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId", companyId
				).put(
					"test", RandomTestUtil.randomString()
				).build());
		}

		return _configurationAdmin.getConfiguration(pid);
	}

	private void _createRepositories(Company company) throws Exception {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			RepositoryDefiner.class,
			(RepositoryDefiner)ProxyUtil.newProxyInstance(
				RepositoryDefiner.class.getClassLoader(),
				new Class<?>[] {RepositoryDefiner.class},
				(proxy, method, args) -> {
					if (Objects.equals(method.getName(), "getClassName")) {
						return CompanyLocalServiceDBPartitionTest.class.
							getName();
					}

					if (Objects.equals(
							method.getName(), "isExternalRepository")) {

						return false;
					}

					return null;
				}),
			MapUtil.singletonDictionary(
				"companyId", String.valueOf(company.getCompanyId())));

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			User adminUser = UserTestUtil.getAdminUser(company.getCompanyId());

			Repository repository = _repositoryLocalService.addRepository(
				null, adminUser.getUserId(), company.getGroupId(),
				_portal.getClassNameId(LiferayRepository.class.getName()),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), new UnicodeProperties(), true,
				ServiceContextTestUtil.getServiceContext(company.getGroupId()));

			RepositoryClassDefinition repositoryClassDefinition =
				RepositoryClassDefinitionCatalogUtil.
					getRepositoryClassDefinition(
						company.getCompanyId(),
						CompanyLocalServiceDBPartitionTest.class.getName());

			repositoryClassDefinition.setRepositoryFactory(_repositoryFactory);

			repositoryClassDefinition.createLocalRepository(
				repository.getRepositoryId());
			repositoryClassDefinition.createRepository(
				repository.getRepositoryId());
		}
	}

	private long[] _getCompanyIdsBySQL() {
		return ReflectionTestUtil.invoke(
			PortalInstancePool.class, "_getCompanyIdsBySQL", null, null);
	}

	private int _getDBPartitionsCount() throws SQLException {
		DatabaseMetaData databaseMetaData = connection.getMetaData();

		try (ResultSet resultSet = databaseMetaData.getSchemas()) {
			if (resultSet.last()) {
				return resultSet.getRow();
			}
		}

		try (ResultSet resultSet = databaseMetaData.getCatalogs()) {
			while (resultSet.last()) {
				return resultSet.getRow();
			}
		}

		throw new SQLException("At least one database partition is required");
	}

	private List<String> _getObjectNames(String objectType, long companyId)
		throws Exception {

		List<String> objectNames = new ArrayList<>();

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		String partitionName = getPartitionName(companyId);

		try (ResultSet resultSet = databaseMetaData.getTables(
				dbPartitionDB.getCatalog(connection, partitionName),
				dbPartitionDB.getSchema(connection, partitionName), null,
				new String[] {objectType})) {

			while (resultSet.next()) {
				objectNames.add(resultSet.getString("TABLE_NAME"));
			}
		}

		return objectNames;
	}

	private int _getRulesCount(String partitionName) throws SQLException {
		if (db.getDBType() != DBType.POSTGRESQL) {
			return 0;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select count(pg_catalog.pg_rewrite.rulename) from ",
					"pg_catalog.pg_rewrite join pg_catalog.pg_class on ",
					"pg_catalog.pg_rewrite.ev_class = pg_catalog.pg_class.oid ",
					"where pg_catalog.pg_class.relnamespace = '", partitionName,
					"'::regnamespace and (pg_catalog.pg_rewrite.rulename like ",
					"'update_%' or pg_catalog.pg_rewrite.rulename like ",
					"'delete_%')"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			resultSet.next();

			return resultSet.getInt(1);
		}
	}

	private int _getTablesCount(long companyId) throws Exception {
		List<String> tableNames = _getObjectNames("TABLE", companyId);

		return tableNames.size();
	}

	private int _getViewsCount(long companyId) throws Exception {
		List<String> viewNames = _getObjectNames("VIEW", companyId);

		return viewNames.size();
	}

	private boolean _hasResourceActionsCached(long companyId) {
		AopInvocationHandler aopInvocationHandler =
			ProxyUtil.fetchInvocationHandler(
				_resourceActionLocalService, AopInvocationHandler.class);

		Map<String, ResourceAction> resourceActions =
			ReflectionTestUtil.getFieldValue(
				(ResourceActionLocalServiceImpl)
					aopInvocationHandler.getTarget(),
				"_resourceActions");

		for (String key : resourceActions.keySet()) {
			if (key.endsWith(StringPool.AT + companyId)) {
				return true;
			}
		}

		return false;
	}

	private static final String _CLASS_NAME_1 =
		CompanyLocalServiceDBPartitionTest.class.getName() + 1;

	private static final String _CLASS_NAME_2 =
		CompanyLocalServiceDBPartitionTest.class.getName() + 2;

	private static BundleContext _bundleContext;
	private static ClassName _className1;
	private static ClassName _className2;

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	private static long _counter;

	@Inject
	private static CounterLocalService _counterLocalService;

	private static long _defaultCompanyId;

	@Inject
	private static VirtualHostLocalService _virtualHostLocalService;

	@DeleteAfterTestRun
	private Company _company1;

	@DeleteAfterTestRun
	private Company _company2;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private CounterFinder _counterFinder;

	@Inject
	private PersistenceManager _persistenceManager;

	@Inject
	private Portal _portal;

	@Inject
	private RepositoryFactory _repositoryFactory;

	@Inject
	private RepositoryLocalService _repositoryLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	private ServiceRegistration<RepositoryDefiner> _serviceRegistration;

}