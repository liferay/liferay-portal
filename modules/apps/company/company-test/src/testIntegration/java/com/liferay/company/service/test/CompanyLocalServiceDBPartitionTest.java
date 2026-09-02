/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.company.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.company.service.test.util.CompanyLocalServiceTestUtil;
import com.liferay.counter.kernel.model.Counter;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.counter.kernel.service.persistence.CounterFinder;
import com.liferay.counter.model.CounterRegister;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
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
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
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
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.spring.orm.LastSessionRecorderHelper;
import com.liferay.portal.kernel.spring.orm.LastSessionRecorderHelperUtil;
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
import com.liferay.portal.kernel.util.ListUtil;
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

import jakarta.portlet.Portlet;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.felix.cm.PersistenceManager;

import org.junit.After;
import org.junit.AfterClass;
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

		_safeCloseable = CompanyThreadLocal.setCompanyIdWithSafeCloseable(
			_defaultCompanyId);
	}

	@AfterClass
	public static void tearDownClass() {
		if (_safeCloseable != null) {
			_safeCloseable.close();
		}
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
		long rulesCount = _getRulesCount(defaultPartitionName);

		_company1 = CompanyTestUtil.addCompany();

		Assert.assertTrue(
			ArrayUtil.contains(
				CompanyLocalServiceTestUtil.getCompanyIdsBySQL(),
				_company1.getCompanyId()));

		Assert.assertEquals(dbPartitionsCount + 1, _getDBPartitionsCount());
		Assert.assertEquals(
			rulesCount,
			_getRulesCount(
				CompanyLocalServiceTestUtil.getPartitionName(
					_company1.getCompanyId())));
	}

	@Test
	public void testAddCompanyDoesNotWriteToDefaultPartition()
		throws Exception {

		Company company = CompanyTestUtil.addCompany();

		try {
			Assert.assertEquals(
				0,
				_getResourcePermissionsCount(
					defaultPartitionName, company.getCompanyId()));
		}
		finally {
			companyLocalService.deleteCompany(company);

			_deleteResourcePermissions(
				defaultPartitionName, company.getCompanyId());
		}
	}

	@Test
	public void testAddCompanyFlushesPendingWritesBeforeApplyingNewCompany()
		throws Exception {

		List<Long> companyIds = new ArrayList<>();

		Thread currentThread = Thread.currentThread();

		LastSessionRecorderHelper lastSessionRecorderHelper =
			ReflectionTestUtil.getFieldValue(
				LastSessionRecorderHelperUtil.class,
				"_lastSessionRecorderHelper");

		ReflectionTestUtil.setFieldValue(
			LastSessionRecorderHelperUtil.class, "_lastSessionRecorderHelper",
			new LastSessionRecorderHelper() {

				@Override
				public void syncLastSessionState() {
					lastSessionRecorderHelper.syncLastSessionState();
				}

				@Override
				public void syncLastSessionState(boolean portalSessionOnly) {

					// Only a full sync flushes every session, so it is the
					// one a company scope switch depends on

					if (!portalSessionOnly &&
						(currentThread == Thread.currentThread())) {

						companyIds.add(CompanyThreadLocal.getCompanyId());
					}

					lastSessionRecorderHelper.syncLastSessionState(
						portalSessionOnly);
				}

			});

		try {
			_company1 = CompanyTestUtil.addCompany();
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				LastSessionRecorderHelperUtil.class,
				"_lastSessionRecorderHelper", lastSessionRecorderHelper);
		}

		Assert.assertFalse(companyIds.toString(), companyIds.isEmpty());

		Assert.assertEquals(
			companyIds.toString(), _defaultCompanyId, (long)companyIds.get(0));
	}

	@Test
	public void testAddCompanyUsesVirtualHostCounter() throws Exception {
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

	@Test
	public void testAddCompanyWhenCompanyLocalServiceFails() throws Exception {
		long[] companyIds = CompanyLocalServiceTestUtil.getCompanyIdsBySQL();
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
			Assert.assertArrayEquals(
				companyIds, CompanyLocalServiceTestUtil.getCompanyIdsBySQL());
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
		long[] companyIds = CompanyLocalServiceTestUtil.getCompanyIdsBySQL();
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
			Assert.assertArrayEquals(
				companyIds, CompanyLocalServiceTestUtil.getCompanyIdsBySQL());
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

		Configuration configuration =
			CompanyLocalServiceTestUtil.createFactoryConfiguration(
				_configurationAdmin, company.getCompanyId());

		String pid = configuration.getPid();

		companyLocalService.exportCompany(company.getCompanyId());

		try {
			CompanyLocalServiceTestUtil.assertConfiguration(
				_configurationAdmin, _persistenceManager, pid, true);

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
						DBPartitionUtil.getExportedPartitionName(
							company.getCompanyId())));
			}

			companyLocalService.deleteCompany(company);

			company = companyLocalService.addDBPartitionCompany(
				company.getCompanyId(), name, virtualHostName, webId);

			Assert.assertTrue(
				ArrayUtil.contains(
					CompanyLocalServiceTestUtil.getCompanyIdsBySQL(),
					company.getCompanyId()));

			Assert.assertEquals(name, company.getName());
			Assert.assertEquals(virtualHostName, company.getVirtualHostname());
			Assert.assertEquals(webId, company.getWebId());

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						company.getCompanyId())) {

				CompanyLocalServiceTestUtil.assertConfiguration(
					_configurationAdmin, _persistenceManager, pid, true);
			}
		}
		finally {
			db.runSQL(
				dbPartitionDB.getDropPartitionSQL(
					DBPartitionUtil.getExportedPartitionName(
						company.getCompanyId())));

			if (ArrayUtil.contains(
					CompanyLocalServiceTestUtil.getCompanyIdsBySQL(),
					company.getCompanyId())) {

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
			companyLocalService.exportCompany(company.getCompanyId());

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
						CompanyLocalServiceTestUtil.getCompanyIdsBySQL(),
						company.getCompanyId()));

				CompanyLocalServiceTestUtil.checkStandaloneDBPartitionTables(
					connection, dbPartitionDB,
					DBPartitionUtil.getExportedPartitionName(
						company.getCompanyId()),
					"Company", "VirtualHost");
			}
		}
		finally {
			db.runSQL(
				dbPartitionDB.getDropPartitionSQL(
					DBPartitionUtil.getExportedPartitionName(
						company.getCompanyId())));

			if (ArrayUtil.contains(
					CompanyLocalServiceTestUtil.getCompanyIdsBySQL(),
					company.getCompanyId())) {

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
			companyLocalService.exportCompany(company.getCompanyId());

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
						CompanyLocalServiceTestUtil.getCompanyIdsBySQL(),
						company.getCompanyId()));

				CompanyLocalServiceTestUtil.checkStandaloneDBPartitionTables(
					connection, dbPartitionDB,
					DBPartitionUtil.getExportedPartitionName(
						company.getCompanyId()),
					"Company", "VirtualHost");
			}
		}
		finally {
			db.runSQL(
				dbPartitionDB.getDropPartitionSQL(
					DBPartitionUtil.getExportedPartitionName(
						company.getCompanyId())));

			if (ArrayUtil.contains(
					CompanyLocalServiceTestUtil.getCompanyIdsBySQL(),
					company.getCompanyId())) {

				companyLocalService.deleteCompany(company);
			}
			else {
				removeDBPartitions(new long[] {company.getCompanyId()});
			}
		}
	}

	@Test
	public void testCopyDBPartitionCompany() throws Exception {
		long rulesCount = _getRulesCount(defaultPartitionName);

		Configuration configuration =
			CompanyLocalServiceTestUtil.createFactoryConfiguration(
				_configurationAdmin, TestPropsValues.getCompanyId());

		String name = RandomTestUtil.randomString();
		String virtualHostname = StringUtil.toLowerCase(
			RandomTestUtil.randomString());
		String webId = RandomTestUtil.randomString();

		Company copiedCompany = null;

		try (SafeCloseable safeCloseable1 =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					TestPropsValues.getCompanyId())) {

			ObjectDefinition objectDefinition =
				ObjectDefinitionTestUtil.publishObjectDefinition();

			copiedCompany = companyLocalService.copyDBPartitionCompany(
				TestPropsValues.getCompanyId(), null, name, virtualHostname,
				webId);

			_assertCopyDBPartitionCompany(
				copiedCompany, name, virtualHostname, webId);

			long copiedCompanyId = copiedCompany.getCompanyId();

			_assertCompanyConfiguration(copiedCompanyId, configuration);

			_addCopyDBPartitionCompanyCache(copiedCompanyId);

			try (SafeCloseable safeCloseable2 =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						copiedCompanyId)) {

				ObjectDefinition copiedObjectDefinition =
					_objectDefinitionLocalService.fetchObjectDefinition(
						objectDefinition.getObjectDefinitionId());

				Assert.assertNotEquals(
					copiedObjectDefinition.getClassName(),
					objectDefinition.getClassName());

				Collection<ServiceReference<Portlet>> serviceReferences =
					_bundleContext.getServiceReferences(
						Portlet.class,
						StringBundler.concat(
							"(&(com.liferay.portlet.company=",
							copiedObjectDefinition.getCompanyId(),
							")(jakarta.portlet.name=",
							copiedObjectDefinition.getPortletId(), "))"));

				Assert.assertFalse(serviceReferences.isEmpty());

				Assert.assertNotNull(
					_classNameLocalService.fetchClassName(
						copiedObjectDefinition.getClassName()));
				Assert.assertNotNull(
					_portletLocalService.getPortletById(
						copiedObjectDefinition.getPortletId()));
				Assert.assertTrue(
					ListUtil.isNotEmpty(
						_resourceActionLocalService.getResourceActions(
							copiedObjectDefinition.getClassName())));
				Assert.assertTrue(
					ListUtil.isNotEmpty(
						_resourceActionLocalService.getResourceActions(
							copiedObjectDefinition.getPortletId())));
				Assert.assertTrue(
					ListUtil.isNotEmpty(
						_resourcePermissionLocalService.getResourcePermissions(
							copiedObjectDefinition.getClassName())));
				Assert.assertTrue(
					ListUtil.isNotEmpty(
						_resourcePermissionLocalService.getResourcePermissions(
							copiedObjectDefinition.getPortletId())));
			}

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
				_getRulesCount(
					CompanyLocalServiceTestUtil.getPartitionName(
						copiedCompany.getCompanyId())));

			SafeCloseable safeCloseable2 =
				PortalInstances.setCopyInProcessCompanyIdWithSafeCloseable(
					copiedCompanyId);

			safeCloseable2.close();
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
				ArrayUtil.contains(
					CompanyLocalServiceTestUtil.getCompanyIdsBySQL(),
					toCompanyId));

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
				ArrayUtil.contains(
					CompanyLocalServiceTestUtil.getCompanyIdsBySQL(),
					toCompanyId));

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

		Configuration configuration =
			CompanyLocalServiceTestUtil.createFactoryConfiguration(
				_configurationAdmin, _company1.getCompanyId());

		String pid = configuration.getPid();

		_createRepositories(_company1);

		_assertCache(_company1.getCompanyId(), true);

		long repositoryId = RandomTestUtil.nextLong();
		String fileName1 = "/" + RandomTestUtil.randomString();

		_store.addFile(
			_company1.getCompanyId(), repositoryId, fileName1,
			Store.VERSION_DEFAULT, new UnsyncByteArrayInputStream(new byte[0]));

		int dbPartitionsCount = _getDBPartitionsCount();

		companyLocalService.deleteCompany(_company1);

		Assert.assertFalse(
			PortalInstances.isCompanyInDeletionProcess(
				_company1.getCompanyId()));

		Assert.assertFalse(
			ArrayUtil.contains(
				CompanyLocalServiceTestUtil.getCompanyIdsBySQL(),
				_company1.getCompanyId()));
		Assert.assertFalse(
			_store.hasFile(
				_company1.getCompanyId(), repositoryId, fileName1,
				Store.VERSION_DEFAULT));

		Assert.assertEquals(dbPartitionsCount - 1, _getDBPartitionsCount());

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Collection<ServiceReference<Portlet>> serviceReferences =
			bundleContext.getServiceReferences(
				Portlet.class,
				"(com.liferay.portlet.company=" + _company1.getCompanyId() +
					")");

		Assert.assertTrue(serviceReferences.isEmpty());

		_assertCache(_company1.getCompanyId(), false);

		CompanyLocalServiceTestUtil.assertConfiguration(
			_configurationAdmin, _persistenceManager, pid, false);
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
					CompanyLocalServiceTestUtil.getCompanyIdsBySQL(),
					_company1.getCompanyId()));
		}
	}

	@Test
	public void testForEachCompanyIdSkipsGoneOrInDeletionCompanies()
		throws Exception {

		// A company in the deletion process is skipped

		_company1 = CompanyTestUtil.addCompany();

		long companyId = _company1.getCompanyId();

		List<Long> companyIds = new ArrayList<>();

		try (SafeCloseable safeCloseable =
				PortalInstances.setCompanyInDeletionProcessWithSafeCloseable(
					companyId)) {

			companyLocalService.forEachCompanyId(
				companyIds::add, new long[] {companyId});
		}

		Assert.assertEquals(companyIds.toString(), 0, companyIds.size());

		// A company that no longer exists is skipped

		companyLocalService.forEachCompanyId(
			companyIds::add, new long[] {RandomTestUtil.nextLong()});

		Assert.assertEquals(companyIds.toString(), 0, companyIds.size());

		// The system scope is passed through

		companyLocalService.forEachCompanyId(
			companyIds::add, new long[] {CompanyConstants.SYSTEM});

		Assert.assertEquals(
			Collections.singletonList(CompanyConstants.SYSTEM), companyIds);

		companyIds.clear();

		// A live company is iterated

		companyLocalService.forEachCompanyId(
			companyIds::add, new long[] {companyId});

		Assert.assertEquals(Collections.singletonList(companyId), companyIds);
	}

	@Test
	public void testIsCurrentCompanyRestricted() throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_defaultCompanyId)) {

			Assert.assertFalse(DBPartition.isCurrentCompanyRestricted());
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					CompanyConstants.SYSTEM)) {

			Assert.assertFalse(DBPartition.isCurrentCompanyRestricted());
		}

		_company1 = CompanyTestUtil.addCompany();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company1.getCompanyId())) {

			Assert.assertTrue(DBPartition.isCurrentCompanyRestricted());
		}
	}

	@Test
	public void testUpdateCompanyMx() throws Exception {
		_company1 = CompanyTestUtil.addCompany();

		String mx = _company1.getMx();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company1.getCompanyId())) {

			companyLocalService.updateCompany(
				_company1.getCompanyId(), _company1.getVirtualHostname(),
				StringUtil.toLowerCase(RandomTestUtil.randomString()) + ".com",
				_company1.getMaxUsers(), _company1.isActive());
		}

		Company company = companyLocalService.getCompany(
			_company1.getCompanyId());

		Assert.assertEquals(mx, company.getMx());

		String updatedMx =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) + ".com";

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_defaultCompanyId)) {

			companyLocalService.updateCompany(
				_company1.getCompanyId(), _company1.getVirtualHostname(),
				updatedMx, _company1.getMaxUsers(), _company1.isActive());
		}

		company = companyLocalService.getCompany(_company1.getCompanyId());

		Assert.assertEquals(updatedMx, company.getMx());

		String legalName = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company1.getCompanyId())) {

			companyLocalService.updateCompany(
				_company1.getCompanyId(), _company1.getVirtualHostname(),
				StringUtil.toLowerCase(RandomTestUtil.randomString()) + ".com",
				company.getHomeURL(), false, null, name, legalName,
				company.getLegalId(), company.getLegalType(),
				company.getSicCode(), company.getTickerSymbol(),
				company.getIndustry(), company.getType(), company.getSize());
		}

		company = companyLocalService.getCompany(_company1.getCompanyId());

		Assert.assertEquals(legalName, company.getLegalName());
		Assert.assertEquals(updatedMx, company.getMx());
		Assert.assertEquals(name, company.getName());

		updatedMx =
			StringUtil.toLowerCase(RandomTestUtil.randomString()) + ".com";

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_defaultCompanyId)) {

			companyLocalService.updateCompany(
				_company1.getCompanyId(), _company1.getVirtualHostname(),
				updatedMx, company.getHomeURL(), false, null, name, legalName,
				company.getLegalId(), company.getLegalType(),
				company.getSicCode(), company.getTickerSymbol(),
				company.getIndustry(), company.getType(), company.getSize());
		}

		company = companyLocalService.getCompany(_company1.getCompanyId());

		Assert.assertEquals(updatedMx, company.getMx());
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
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select configurationId, dictionary from ",
					CompanyLocalServiceTestUtil.getPartitionName(companyId),
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

	private void _assertCopyDBPartitionCompany(
			Company company, String name, String virtualHostname, String webId)
		throws Exception {

		Assert.assertTrue(
			ArrayUtil.contains(
				CompanyLocalServiceTestUtil.getCompanyIdsBySQL(),
				company.getCompanyId()));
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

		try (ResultSet resultSet1 = databaseMetaData.getTables(
				dbPartitionDB.getCatalog(
					connection,
					CompanyLocalServiceTestUtil.getPartitionName(
						copiedCompanyId)),
				dbPartitionDB.getSchema(
					connection,
					CompanyLocalServiceTestUtil.getPartitionName(
						copiedCompanyId)),
				null, new String[] {"TABLE"})) {

			while (resultSet1.next()) {
				String tableName = resultSet1.getString("TABLE_NAME");

				if (dbInspector.isControlTable(tableName)) {
					continue;
				}

				tableNames.add(tableName);
			}
		}

		for (String tableName : tableNames) {
			try (ResultSet resultSet2 = databaseMetaData.getColumns(
					dbPartitionDB.getCatalog(
						connection,
						CompanyLocalServiceTestUtil.getPartitionName(
							copiedCompanyId)),
					dbPartitionDB.getSchema(
						connection,
						CompanyLocalServiceTestUtil.getPartitionName(
							copiedCompanyId)),
					tableName, null)) {

				while (resultSet2.next()) {
					int columnType = resultSet2.getInt("DATA_TYPE");

					if ((columnType != Types.BIGINT) &&
						(columnType != Types.LONGVARCHAR) &&
						(columnType != Types.VARCHAR)) {

						continue;
					}

					String columnName = resultSet2.getString("COLUMN_NAME");

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

					try (ResultSet resultSet3 =
							preparedStatement.executeQuery()) {

						if (resultSet3.next()) {
							Assert.fail(
								StringBundler.concat(
									"Company ID ", companyId,
									" is present in the copied database ",
									"schema in ", tableName, StringPool.PERIOD,
									columnName, StringPool.COLON,
									StringPool.SPACE,
									resultSet3.getObject(columnName)));
						}
					}
				}
			}
		}
	}

	private void _checkPartitionDoesNotExist(long companyId) throws Exception {
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
			partitionNames.contains(
				CompanyLocalServiceTestUtil.getPartitionName(companyId)));
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

	private void _deleteResourcePermissions(
			String partitionName, long companyId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"delete from ", partitionName,
					".ResourcePermission where companyId = ?"))) {

			preparedStatement.setLong(1, companyId);

			preparedStatement.executeUpdate();
		}
	}

	private int _getDBPartitionsCount() throws Exception {
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

	private int _getResourcePermissionsCount(
			String partitionName, long companyId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select count(*) COUNT_VALUE from ", partitionName,
					".ResourcePermission where companyId = ?"))) {

			preparedStatement.setLong(1, companyId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				resultSet.next();

				return resultSet.getInt("COUNT_VALUE");
			}
		}
	}

	private long _getRulesCount(String partitionName) throws Exception {
		if (db.getDBType() != DBType.POSTGRESQL) {
			return 0;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select count(pg_catalog.pg_rewrite.rulename) as count ",
					"from pg_catalog.pg_rewrite join pg_catalog.pg_class on ",
					"pg_catalog.pg_rewrite.ev_class = pg_catalog.pg_class.oid ",
					"where pg_catalog.pg_class.relnamespace = ?::",
					"regnamespace and (pg_catalog.pg_rewrite.rulename like ",
					"'update_%' or pg_catalog.pg_rewrite.rulename like ",
					"'delete_%')"))) {

			preparedStatement.setString(1, partitionName);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				resultSet.next();

				return resultSet.getLong("count");
			}
		}
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
	private static long _defaultCompanyId;
	private static SafeCloseable _safeCloseable;

	private ClassName _className1;
	private ClassName _className2;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private Company _company1;

	@DeleteAfterTestRun
	private Company _company2;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	private long _counter;

	@Inject
	private CounterFinder _counterFinder;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private PersistenceManager _persistenceManager;

	@Inject
	private Portal _portal;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private RepositoryFactory _repositoryFactory;

	@Inject
	private RepositoryLocalService _repositoryLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private ServiceRegistration<RepositoryDefiner> _serviceRegistration;

	@Inject(
		filter = "(&(objectClass=com.liferay.document.library.kernel.store.Store)(default=true))"
	)
	private Store _store;

	@Inject
	private VirtualHostLocalService _virtualHostLocalService;

}