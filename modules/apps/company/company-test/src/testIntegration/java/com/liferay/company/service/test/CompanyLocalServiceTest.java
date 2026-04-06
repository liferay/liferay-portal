/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.company.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.company.service.test.util.CompanyLocalServiceTestUtil;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.counter.kernel.service.persistence.CounterFinder;
import com.liferay.counter.model.CounterRegister;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.db.partition.db.DBPartitionDB;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.CompanyMaxUsersException;
import com.liferay.portal.kernel.exception.CompanyMxException;
import com.liferay.portal.kernel.exception.CompanyNameException;
import com.liferay.portal.kernel.exception.CompanyVirtualHostException;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.NoSuchVirtualHostException;
import com.liferay.portal.kernel.exception.RequiredCompanyException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.PasswordPolicyTable;
import com.liferay.portal.kernel.model.PortalPreferenceValue;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.service.PortalPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.model.impl.CompanyImpl;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sites.kernel.util.Sites;

import jakarta.portlet.Portlet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sql.DataSource;

import org.apache.felix.cm.PersistenceManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
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
 * @author Mika Koivisto
 * @author Dale Shan
 */
@DataGuard(autoDelete = false, scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class CompanyLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			CompanyLocalServiceDBPartitionTest.class);

		_bundleContext = bundle.getBundleContext();

		_connection = DataAccess.getConnection();

		_db = DBManagerUtil.getDB();

		if (_db.isSupportsDBPartition()) {
			ReflectionTestUtil.invoke(
				DBPartitionUtil.class, "_initializeDBPartitionDB",
				new Class<?>[] {DB.class, DataSource.class}, _db,
				InfrastructureUtil.getDataSource());
		}

		_dbPartitionDB = ReflectionTestUtil.getFieldValue(
			DBPartitionUtil.class, "_dbPartitionDB");
		_safeCloseable = CompanyThreadLocal.setCompanyIdWithSafeCloseable(
			PortalInstancePool.getDefaultCompanyId());

		_initializeClassNames();

		_modelListeners = _registerModelListeners();

		_deletedCompany = _addCompany();

		_addCompanyUserGroupRole(_deletedCompany);

		_companyLocalService.deleteCompany(_deletedCompany);

		_cleanUpData();

		_company = _addCompany();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_companyLocalService.deleteCompany(_company);

		_cleanUpData();

		if (_safeCloseable != null) {
			_safeCloseable.close();
		}

		DataAccess.cleanUp(_connection);
	}

	@Before
	public void setUp() throws Exception {
		_initializeClassNames();
	}

	@After
	public void tearDown() throws Exception {
		_cleanUpData();
	}

	@Test
	public void testAddAndDeleteCompany() throws Exception {
		Company company = _addCompany();

		_companyLocalService.deleteCompany(company.getCompanyId());

		for (String webId : PortalInstancePool.getWebIds()) {
			Assert.assertNotEquals(company.getWebId(), webId);
		}
	}

	@Test
	public void testAddAndDeleteCompanyWithChildOrganizationSite()
		throws Exception {

		Company company = _addCompany();
		Organization companyOrganization = null;
		Group companyOrganizationGroup = null;
		Group group = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			long userId = _userLocalService.getGuestUserId(
				company.getCompanyId());

			companyOrganization = _organizationLocalService.addOrganization(
				userId, OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
				RandomTestUtil.randomString(), true);

			companyOrganizationGroup = companyOrganization.getGroup();

			group = GroupTestUtil.addGroup(
				company.getCompanyId(), userId,
				companyOrganizationGroup.getGroupId());
		}
		finally {
			_companyLocalService.deleteCompany(company);
		}

		Assert.assertNull(
			"The company organization should delete with the company",
			_organizationLocalService.fetchOrganization(
				companyOrganization.getOrganizationId()));
		Assert.assertNull(
			"The company organization group should delete with the company",
			_groupLocalService.fetchGroup(
				companyOrganizationGroup.getGroupId()));
		Assert.assertNull(
			"The company organization child group should delete with the " +
				"company",
			_groupLocalService.fetchGroup(group.getGroupId()));
	}

	@Test
	public void testAddAndDeleteCompanyWithCompanyGroupStaging()
		throws Exception {

		Assume.assumeFalse(PropsValues.DATABASE_PARTITION_ENABLED);

		Company company = _addCompany();
		Group companyGroup = null;
		Group companyStagingGroup = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId());

			// LPD-79857

			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineExportTaskExecutorImpl",
				LoggerTestUtil.ERROR)) {

			companyGroup = company.getGroup();

			_stagingLocalService.enableLocalStaging(
				_userLocalService.getGuestUserId(company.getCompanyId()),
				companyGroup, false, false, new ServiceContext());

			companyStagingGroup = companyGroup.getStagingGroup();
		}
		finally {
			_companyLocalService.deleteCompany(company.getCompanyId());
		}

		Assert.assertNull(
			_groupLocalService.fetchGroup(companyGroup.getGroupId()));
		Assert.assertNull(
			_groupLocalService.fetchGroup(companyStagingGroup.getGroupId()));
	}

	@Test
	public void testAddAndDeleteCompanyWithDLFileEntryTypes() throws Exception {
		Assume.assumeFalse(PropsValues.DATABASE_PARTITION_ENABLED);

		Company company = _addCompany();
		DDMStructure ddmStructure = null;
		DLFileEntryType dlFileEntryType = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			long userId = _userLocalService.getGuestUserId(
				company.getCompanyId());

			Group guestGroup = _groupLocalService.getGroup(
				company.getCompanyId(), GroupConstants.GUEST);

			ServiceContext serviceContext = _getServiceContext(
				company.getCompanyId());

			serviceContext.setScopeGroupId(guestGroup.getGroupId());
			serviceContext.setUserId(userId);

			ddmStructure = _ddmStructureLocalService.addStructure(
				null, userId, guestGroup.getGroupId(),
				DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
				_portal.getClassNameId(DLFileEntryMetadata.class),
				StringPool.BLANK,
				HashMapBuilder.put(
					LocaleUtil.getDefault(),
					DLFileEntryMetadata.class.getSimpleName()
				).build(),
				new HashMap<>(), StringPool.BLANK,
				StorageType.DEFAULT.toString(), serviceContext);

			dlFileEntryType = _dlFileEntryTypeLocalService.addFileEntryType(
				null, userId, guestGroup.getGroupId(),
				ddmStructure.getStructureId(),
				CompanyLocalServiceTest.class.getSimpleName(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(),
					CompanyLocalServiceTest.class.getSimpleName()
				).build(),
				new HashMap<>(),
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
				serviceContext);

			serviceContext.setAttribute(
				"fileEntryTypeId", dlFileEntryType.getFileEntryTypeId());

			_dlAppLocalService.addFileEntry(
				null, userId, guestGroup.getGroupId(), 0, "test.xml",
				"text/xml", "test.xml", "", "", "", "test".getBytes(), null,
				null, null, serviceContext);
		}
		finally {
			_companyLocalService.deleteCompany(company.getCompanyId());
		}

		Assert.assertNull(
			_ddmStructureLocalService.fetchStructure(
				ddmStructure.getStructureId()));
		Assert.assertNull(
			_dlFileEntryTypeLocalService.fetchDLFileEntryType(
				dlFileEntryType.getFileEntryTypeId()));
	}

	@Test
	public void testAddAndDeleteCompanyWithLayoutSetPrototype()
		throws Throwable {

		Assume.assumeFalse(PropsValues.DATABASE_PARTITION_ENABLED);

		Company company = _addCompany();
		LayoutSetPrototype layoutSetPrototype = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			long userId = _userLocalService.getGuestUserId(
				company.getCompanyId());

			Group group = GroupTestUtil.addGroup(
				company.getCompanyId(), userId,
				GroupConstants.DEFAULT_PARENT_GROUP_ID);

			layoutSetPrototype = _addLayoutSetPrototype(
				company.getCompanyId(), userId, RandomTestUtil.randomString());

			long layoutSetPrototypeId =
				layoutSetPrototype.getLayoutSetPrototypeId();

			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					_sites.updateLayoutSetPrototypesLinks(
						group, layoutSetPrototypeId, 0, true, false);

					return null;
				});
		}
		finally {
			_companyLocalService.deleteCompany(company.getCompanyId());
		}

		Assert.assertNull(
			_layoutSetPrototypeLocalService.fetchLayoutSetPrototype(
				layoutSetPrototype.getLayoutSetPrototypeId()));
	}

	@Test
	public void testAddAndDeleteCompanyWithLayoutSetPrototypeLinkedUserGroup()
		throws Throwable {

		Assume.assumeFalse(PropsValues.DATABASE_PARTITION_ENABLED);

		Company company = _addCompany();
		long layoutSetPrototypeId = 0;
		long userGroupId = 0;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			long userId = _userLocalService.getGuestUserId(
				company.getCompanyId());

			Group group = GroupTestUtil.addGroup(
				company.getCompanyId(), userId,
				GroupConstants.DEFAULT_PARENT_GROUP_ID);

			UserGroup userGroup = UserGroupTestUtil.addUserGroup(
				group.getGroupId());

			LayoutSetPrototype layoutSetPrototype = _addLayoutSetPrototype(
				company.getCompanyId(), userId, RandomTestUtil.randomString());

			layoutSetPrototypeId = layoutSetPrototype.getLayoutSetPrototypeId();

			userGroupId = userGroup.getUserGroupId();

			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					_sites.updateLayoutSetPrototypesLinks(
						userGroup.getGroup(),
						layoutSetPrototype.getLayoutSetPrototypeId(), 0, true,
						false);

					return null;
				});
		}
		finally {
			_companyLocalService.deleteCompany(company.getCompanyId());
		}

		Assert.assertNull(
			_layoutSetPrototypeLocalService.fetchLayoutSetPrototype(
				layoutSetPrototypeId));
		Assert.assertNull(_userGroupLocalService.fetchUserGroup(userGroupId));
	}

	@Test
	public void testAddAndDeleteCompanyWithParentGroup() throws Exception {
		Assume.assumeFalse(PropsValues.DATABASE_PARTITION_ENABLED);

		Company company = _addCompany();
		Group group = null;
		Group parentGroup = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			long userId = _userLocalService.getGuestUserId(
				company.getCompanyId());

			parentGroup = GroupTestUtil.addGroup(
				company.getCompanyId(), userId,
				GroupConstants.DEFAULT_PARENT_GROUP_ID);

			group = GroupTestUtil.addGroup(
				company.getCompanyId(), userId, parentGroup.getGroupId());
		}
		finally {
			_companyLocalService.deleteCompany(company.getCompanyId());
		}

		Assert.assertNull(
			_groupLocalService.fetchGroup(parentGroup.getGroupId()));
		Assert.assertNull(_groupLocalService.fetchGroup(group.getGroupId()));
	}

	@Test
	public void testAddAndDeleteCompanyWithPredictableCompanyIdsEnabled()
		throws Exception {

		Company company1 = null;
		Company company2 = null;
		boolean originalCompanyPredictableCompanyIdsEnabled =
			ReflectionTestUtil.getAndSetFieldValue(
				PropsValues.class, "COMPANY_PREDICTABLE_COMPANY_IDS_ENABLED",
				true);

		try {
			StartupHelperUtil.setDBNew(true);

			String webId1 = RandomTestUtil.randomString() + "test.com";

			if (!originalCompanyPredictableCompanyIdsEnabled) {
				_counterLocalService.reset(Company.class.getName());
			}

			company1 = _companyLocalService.addCompany(
				null, webId1, webId1, "test.com", 0, true, true, null, null,
				null, null, null, null);

			if (!originalCompanyPredictableCompanyIdsEnabled) {
				Assert.assertEquals(10000, company1.getCompanyId());
			}

			StartupHelperUtil.setDBNew(false);

			// Simulate a reboot

			CounterFinder counterFinder = ReflectionTestUtil.getFieldValue(
				_counterLocalService, "counterFinder");

			Map<String, CounterRegister> counterRegisterMap =
				ReflectionTestUtil.getFieldValue(
					counterFinder, "_counterRegisterMap");

			counterRegisterMap.remove(
				DBPartitionUtil.getPartitionKey(Company.class.getName()));

			String webId2 = RandomTestUtil.randomString() + "test.com";

			company2 = _companyLocalService.addCompany(
				null, webId2, webId2, "test.com", 0, true, true, null, null,
				null, null, null, null);

			Assert.assertEquals(
				company1.getCompanyId() + 1, company2.getCompanyId());
		}
		finally {
			StartupHelperUtil.setDBNew(false);

			if (company1 != null) {
				_companyLocalService.deleteCompany(company1);
			}

			if (company2 != null) {
				_companyLocalService.deleteCompany(company2);
			}

			if (!originalCompanyPredictableCompanyIdsEnabled) {
				_counterLocalService.reset(Company.class.getName());
			}

			ReflectionTestUtil.setFieldValue(
				PropsValues.class, "COMPANY_PREDICTABLE_COMPANY_IDS_ENABLED",
				originalCompanyPredictableCompanyIdsEnabled);
		}
	}

	@Test
	public void testAddAndDeleteCompanyWithStagedOrganizationSite()
		throws Exception {

		Assume.assumeFalse(PropsValues.DATABASE_PARTITION_ENABLED);

		Company company = _addCompany();
		Organization companyOrganization = null;
		Group companyOrganizationGroup = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			User companyAdminUser = UserTestUtil.addCompanyAdminUser(company);

			companyOrganization = _organizationLocalService.addOrganization(
				companyAdminUser.getUserId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
				RandomTestUtil.randomString(), true);

			companyOrganizationGroup = companyOrganization.getGroup();

			GroupTestUtil.enableLocalStaging(
				companyOrganizationGroup, companyAdminUser.getUserId());
		}
		finally {
			_companyLocalService.deleteCompany(company);
		}

		Assert.assertNull(
			_organizationLocalService.fetchOrganization(
				companyOrganization.getOrganizationId()));
		Assert.assertNull(
			_groupLocalService.fetchGroup(
				companyOrganizationGroup.getGroupId()));
	}

	@Test
	public void testAddAndDeleteCompanyWithUserGroup() throws Exception {
		Assume.assumeFalse(PropsValues.DATABASE_PARTITION_ENABLED);

		Company company = _addCompany();
		User user = null;
		UserGroup userGroup = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			long userId = _userLocalService.getGuestUserId(
				company.getCompanyId());

			Group group = GroupTestUtil.addGroup(
				company.getCompanyId(), userId,
				GroupConstants.DEFAULT_PARENT_GROUP_ID);

			userGroup = UserGroupTestUtil.addUserGroup(group.getGroupId());

			user = _addUser(
				company.getCompanyId(), userId, group.getGroupId(),
				_getServiceContext(company.getCompanyId()));

			_userGroupLocalService.addUserUserGroup(
				user.getUserId(), userGroup);
		}
		finally {
			_companyLocalService.deleteCompany(company.getCompanyId());
		}

		Assert.assertNull(
			_userGroupLocalService.fetchUserGroup(userGroup.getUserGroupId()));
		Assert.assertNull(_userLocalService.fetchUser(user.getUserId()));
	}

	@Test
	public void testAddAndDeleteCompanyWithUserGroupAndUserGroupRole()
		throws Exception {

		Assume.assumeFalse(PropsValues.DATABASE_PARTITION_ENABLED);

		Company company = _addCompany();
		Group group = null;
		Role role = null;
		User user = null;
		UserGroup userGroup = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			long userId = _userLocalService.getGuestUserId(
				company.getCompanyId());

			group = GroupTestUtil.addGroup(
				company.getCompanyId(), userId,
				GroupConstants.DEFAULT_PARENT_GROUP_ID);

			userGroup = UserGroupTestUtil.addUserGroup(group.getGroupId());

			user = _addUser(
				company.getCompanyId(), userId, group.getGroupId(),
				_getServiceContext(company.getCompanyId()));

			_userGroupLocalService.addUserUserGroup(
				user.getUserId(), userGroup);

			role = _roleLocalService.addRole(
				RandomTestUtil.randomString(), userId, Group.class.getName(),
				group.getClassPK(), StringUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.getDefault(), StringUtil.randomString()),
				Collections.emptyMap(), RoleConstants.TYPE_SITE,
				StringPool.BLANK, _getServiceContext(company.getCompanyId()));

			_userGroupRoleLocalService.addUserGroupRole(
				user.getUserId(), group.getGroupId(), role.getRoleId());
		}
		finally {
			_companyLocalService.deleteCompany(company.getCompanyId());
		}

		Assert.assertNull(_roleLocalService.fetchRole(role.getRoleId()));
		Assert.assertNull(_userLocalService.fetchUser(user.getUserId()));
		Assert.assertNull(
			_userGroupLocalService.fetchUserGroup(userGroup.getUserGroupId()));
		Assert.assertNull(
			_userGroupRoleLocalService.fetchUserGroupRole(
				user.getUserId(), group.getGroupId(), role.getRoleId()));
	}

	@Test
	public void testDeleteCompanyDeletesGroups() throws Exception {
		Assert.assertEquals(
			0,
			_groupLocalService.getGroupsCount(
				_deletedCompany.getCompanyId(),
				GroupConstants.ANY_PARENT_GROUP_ID, true));
		Assert.assertEquals(
			0,
			_groupLocalService.getGroupsCount(
				_deletedCompany.getCompanyId(),
				GroupConstants.ANY_PARENT_GROUP_ID, false));
	}

	@Test
	public void testDeleteCompanyDeletesLayoutPrototypes() throws Exception {
		Assert.assertEquals(
			0,
			_layoutPrototypeLocalService.searchCount(
				_deletedCompany.getCompanyId(), true));
		Assert.assertEquals(
			0,
			_layoutPrototypeLocalService.searchCount(
				_deletedCompany.getCompanyId(), false));
	}

	@Test
	public void testDeleteCompanyDeletesLayoutSetPrototypes() throws Exception {
		List<LayoutSetPrototype> layoutSetPrototypes =
			_layoutSetPrototypeLocalService.getLayoutSetPrototypes(
				_deletedCompany.getCompanyId());

		Assert.assertEquals(
			layoutSetPrototypes.toString(), 0, layoutSetPrototypes.size());
	}

	@Test
	public void testDeleteCompanyDeletesOrganizations() throws Exception {
		Assert.assertEquals(
			0,
			_organizationLocalService.getOrganizationsCount(
				_deletedCompany.getCompanyId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID));
	}

	@Test
	public void testDeleteCompanyDeletesPasswordPolicies() throws Throwable {
		TransactionInvokerUtil.invoke(
			_transactionConfig,
			() -> {
				Assert.assertEquals(
					0,
					_passwordPolicyLocalService.dslQueryCount(
						DSLQueryFactoryUtil.count(
						).from(
							PasswordPolicyTable.INSTANCE
						).where(
							PasswordPolicyTable.INSTANCE.companyId.eq(
								_deletedCompany.getCompanyId())
						)));

				return null;
			});
	}

	@Test
	public void testDeleteCompanyDeletesPortalInstance() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> Assert.assertNotEquals(
				"Company instance was not deleted",
				_deletedCompany.getCompanyId(), (long)companyId));
	}

	@Test
	public void testDeleteCompanyDeletesPortalPreferences() throws Throwable {
		TransactionInvokerUtil.invoke(
			_transactionConfig,
			() -> {
				Assert.assertNull(
					_portalPreferencesLocalService.fetchPortalPreferences(
						_deletedCompany.getCompanyId(),
						PortletKeys.PREFS_OWNER_TYPE_COMPANY));

				return null;
			});
	}

	@Test
	public void testDeleteCompanyDeletesPortlets() throws Throwable {
		TransactionInvokerUtil.invoke(
			_transactionConfig,
			() -> {
				Assert.assertEquals(
					0,
					_portletLocalService.getPortletsCount(
						_deletedCompany.getCompanyId()));

				return null;
			});
	}

	@Test
	public void testDeleteCompanyDeletesRoles() throws Exception {
		List<Role> roles = _roleLocalService.getRoles(
			_deletedCompany.getCompanyId());

		Assert.assertEquals(roles.toString(), 0, roles.size());
	}

	@Test
	public void testDeleteCompanyDeletesUserGroupRoleBeforeRole()
		throws Exception {

		Assume.assumeFalse(PropsValues.DATABASE_PARTITION_ENABLED);

		Assert.assertEquals(
			UserGroupRole.class.getName(), _modelListeners.get(0));
		Assert.assertEquals(Role.class.getName(), _modelListeners.get(1));
	}

	@Test
	public void testDeleteCompanyDeletesUsers() throws Exception {
		List<User> users = _userLocalService.getCompanyUsers(
			_deletedCompany.getCompanyId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		Assert.assertEquals(users.toString(), 0, users.size());
	}

	@Test(expected = NoSuchVirtualHostException.class)
	public void testDeleteCompanyDeletesVirtualHost() throws Exception {
		_virtualHostLocalService.getVirtualHost(_deletedCompany.getWebId());
	}

	@Test(expected = RequiredCompanyException.class)
	public void testDeleteDefaultCompany() throws Exception {
		_companyLocalService.deleteCompany(
			PortalInstancePool.getDefaultCompanyId());
	}

	@FeatureFlag("LPD-11342")
	@Test
	public void testExportCompany() throws Exception {
		Assume.assumeTrue(_db.isSupportsDBPartition());

		try {
			Configuration configuration =
				CompanyLocalServiceTestUtil.createFactoryConfiguration(
					_configurationAdmin, _company.getCompanyId());

			String pid = configuration.getPid();

			_companyLocalService.exportCompany(_company.getCompanyId());

			Assert.assertTrue(
				ArrayUtil.contains(
					CompanyLocalServiceTestUtil.getCompanyIdsBySQL(),
					_company.getCompanyId()));
			Assert.assertTrue(
				_dbPartitionDB.existsPartition(
					_connection,
					CompanyLocalServiceTestUtil.getExportedPartitionName(
						_company.getCompanyId())));

			CompanyLocalServiceTestUtil.checkStandaloneDBPartitionTables(
				_connection, _dbPartitionDB,
				CompanyLocalServiceTestUtil.getExportedPartitionName(
					_company.getCompanyId()),
				"Company", "VirtualHost");

			Collection<ServiceReference<Portlet>> serviceReferences =
				_bundleContext.getServiceReferences(
					Portlet.class,
					"(com.liferay.portlet.company=" + _company.getCompanyId() +
						")");

			Assert.assertFalse(serviceReferences.isEmpty());

			CompanyLocalServiceTestUtil.assertConfiguration(
				_configurationAdmin, _persistenceManager, pid, true);
		}
		finally {
			_db.runSQL(
				_dbPartitionDB.getDropPartitionSQL(
					CompanyLocalServiceTestUtil.getExportedPartitionName(
						_company.getCompanyId())));
		}
	}

	@FeatureFlag("LPD-11342")
	@Test
	public void testExportCompanyDefaultCompany() {
		Assume.assumeTrue(_db.isSupportsDBPartition());

		try {
			_companyLocalService.exportCompany(
				PortalInstancePool.getDefaultCompanyId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof RequiredCompanyException);
		}
	}

	@FeatureFlag("LPD-11342")
	@Test
	public void testExportCompanyWhenDBPartitionUtilFails() throws Exception {
		Assume.assumeTrue(_db.isSupportsDBPartition());

		int tablesCount = _getTablesCount(_company.getCompanyId());
		int viewsCount = _getViewsCount(_company.getCompanyId());

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

							return method.invoke(_dbPartitionDB, args);
						}))) {

			_companyLocalService.exportCompany(_company.getCompanyId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(
				ArrayUtil.contains(
					CompanyLocalServiceTestUtil.getCompanyIdsBySQL(),
					_company.getCompanyId()));
			Assert.assertEquals(
				tablesCount, _getTablesCount(_company.getCompanyId()));
			Assert.assertEquals(
				viewsCount, _getViewsCount(_company.getCompanyId()));
			Assert.assertFalse(
				_dbPartitionDB.existsPartition(
					_connection,
					CompanyLocalServiceTestUtil.getExportedPartitionName(
						_company.getCompanyId())));
		}
		finally {
			_db.runSQL(
				_dbPartitionDB.getDropPartitionSQL(
					CompanyLocalServiceTestUtil.getExportedPartitionName(
						_company.getCompanyId())));
		}
	}

	@Test
	public void testExportCompanyWithoutFF() {
		try {
			_companyLocalService.exportCompany(
				PortalInstancePool.getDefaultCompanyId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(
				exception instanceof UnsupportedOperationException);

			Assert.assertEquals(
				"Feature flag LPD-11342 is disabled", exception.getMessage());
		}
	}

	@Test
	public void testForEachCompanyIdWithInvalidCompanyId() {
		long invalidCompanyId = RandomTestUtil.nextLong();

		try (SafeCloseable safeCloseable = CompanyThreadLocal.lock(
				PortalInstancePool.getDefaultCompanyId())) {

			_companyLocalService.forEachCompanyId(
				null, new long[] {invalidCompanyId});

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			Assert.assertEquals(
				StringBundler.concat(
					"Unable to iterate over the following company IDs [",
					invalidCompanyId, "] because company ID ",
					PortalInstancePool.getDefaultCompanyId(), " is locked"),
				unsupportedOperationException.getMessage());
		}
	}

	@Test
	public void testForEachCompanyWithInvalidCompany() {
		long invalidCompanyId = RandomTestUtil.nextLong();

		try (SafeCloseable safeCloseable = CompanyThreadLocal.lock(
				PortalInstancePool.getDefaultCompanyId())) {

			_companyLocalService.forEachCompany(
				null,
				Collections.singletonList(
					new CompanyImpl() {
						{
							setCompanyId(invalidCompanyId);
						}
					}));

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			Assert.assertEquals(
				StringBundler.concat(
					"Unable to iterate over the following company IDs [",
					invalidCompanyId, "] because company ID ",
					PortalInstancePool.getDefaultCompanyId(), " is locked"),
				unsupportedOperationException.getMessage());
		}
	}

	@Test
	public void testGetCompanyByVirtualHost() throws Exception {
		Company company = _addCompany("::1");

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			Assert.assertEquals(
				company, _companyLocalService.getCompanyByVirtualHost("::1"));
			Assert.assertEquals(
				company,
				_companyLocalService.getCompanyByVirtualHost(
					"0:0:0:0:0:0:0:1"));
		}
		finally {
			_companyLocalService.deleteCompany(company);
		}
	}

	@Test
	public void testUpdateCompanyLocales() throws Exception {
		SafeCloseable safeCloseable =
			CompanyThreadLocal.setCompanyIdWithSafeCloseable(
				_company.getCompanyId());

		String languageId = LocaleUtil.toLanguageId(_company.getLocale());
		TimeZone timeZone = _company.getTimeZone();

		try {
			_companyLocalService.updateDisplay(
				_company.getCompanyId(), "ca_ES", timeZone.getID());

			_companyLocalService.updatePreferences(
				_company.getCompanyId(),
				UnicodePropertiesBuilder.put(
					PropsKeys.LOCALES, "ca_ES"
				).build());

			Assert.assertEquals(
				Collections.singleton(LocaleUtil.fromLanguageId("ca_ES")),
				_language.getAvailableLocales());
		}
		finally {
			_companyLocalService.updateDisplay(
				_company.getCompanyId(), languageId, timeZone.getID());

			_resetCompanyLocales(_company.getCompanyId());

			safeCloseable.close();
		}
	}

	@Test
	public void testUpdateCompanyLocalesUpdateGroupLocales() throws Exception {
		Group group = null;
		SafeCloseable safeCloseable =
			CompanyThreadLocal.setCompanyIdWithSafeCloseable(
				_company.getCompanyId());

		try {
			User user = UserTestUtil.getAdminUser(_company.getCompanyId());

			group = GroupTestUtil.addGroup(
				_company.getCompanyId(), user.getUserId(),
				GroupConstants.DEFAULT_PARENT_GROUP_ID);

			String[] companyLanguageIds = _prefsProps.getStringArray(
				_company.getCompanyId(), PropsKeys.LOCALES, StringPool.COMMA,
				PropsValues.LOCALES_ENABLED);

			group = GroupTestUtil.updateDisplaySettings(
				group.getGroupId(),
				ListUtil.fromArray(
					LocaleUtil.fromLanguageIds(companyLanguageIds)),
				LocaleUtil.getDefault());

			UnicodeProperties groupTypeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			Assert.assertEquals(
				StringUtil.merge(companyLanguageIds),
				groupTypeSettingsUnicodeProperties.getProperty(
					PropsKeys.LOCALES));

			_companyLocalService.updatePreferences(
				_company.getCompanyId(),
				UnicodePropertiesBuilder.put(
					PropsKeys.LOCALES, "en_US"
				).build());

			Assert.assertEquals(
				"en_US",
				_prefsProps.getString(
					_company.getCompanyId(), PropsKeys.LOCALES));

			group = _groupLocalService.getGroup(group.getGroupId());

			groupTypeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			Assert.assertEquals(
				"en_US",
				groupTypeSettingsUnicodeProperties.getProperty(
					PropsKeys.LOCALES));

			_companyLocalService.updatePreferences(
				_company.getCompanyId(),
				UnicodePropertiesBuilder.put(
					PropsKeys.LOCALES, "ca_ES,en_US"
				).build());

			group = _groupLocalService.getGroup(group.getGroupId());

			groupTypeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			Assert.assertEquals(
				"en_US",
				groupTypeSettingsUnicodeProperties.getProperty(
					PropsKeys.LOCALES));
		}
		finally {
			_resetCompanyLocales(_company.getCompanyId());

			GroupTestUtil.deleteGroup(group);

			safeCloseable.close();
		}
	}

	@Test
	public void testUpdateCompanyLocalesWithLayoutSetPrototype()
		throws Exception {

		LayoutSetPrototype layoutSetPrototype = null;
		SafeCloseable safeCloseable =
			CompanyThreadLocal.setCompanyIdWithSafeCloseable(
				_company.getCompanyId());

		String languageId = LocaleUtil.toLanguageId(_company.getLocale());
		TimeZone timeZone = _company.getTimeZone();

		try {
			long userId = _userLocalService.getGuestUserId(
				_company.getCompanyId());

			layoutSetPrototype = _addLayoutSetPrototype(
				_company.getCompanyId(), userId, RandomTestUtil.randomString());

			_companyLocalService.updateDisplay(
				_company.getCompanyId(), "ca_ES", timeZone.getID());

			_companyLocalService.updatePreferences(
				_company.getCompanyId(),
				UnicodePropertiesBuilder.put(
					PropsKeys.LOCALES, "ca_ES"
				).build());

			Assert.assertEquals(
				Collections.singleton(LocaleUtil.fromLanguageId("ca_ES")),
				_language.getAvailableLocales());
		}
		finally {
			_layoutSetPrototypeLocalService.deleteLayoutSetPrototype(
				layoutSetPrototype);

			_companyLocalService.updateDisplay(
				_company.getCompanyId(), languageId, timeZone.getID());

			ActionableDynamicQuery actionableDynamicQuery =
				_systemEventLocalService.getActionableDynamicQuery();

			actionableDynamicQuery.setCompanyId(_company.getCompanyId());

			actionableDynamicQuery.setPerformActionMethod(
				(SystemEvent systemEvent) ->
					_systemEventLocalService.deleteSystemEvent(systemEvent));

			actionableDynamicQuery.performActions();

			_resetCompanyLocales(_company.getCompanyId());

			safeCloseable.close();
		}
	}

	@Test
	public void testUpdateCompanyWithInvalidMaxUsers() throws Exception {
		_testValidateMaxUsers(-1);
		_testValidateMaxUsers(-100);
	}

	@Test
	public void testUpdateCompanyWithInvalidMx() throws Exception {
		_testUpdateMx("abc", false, true);
		_testUpdateMx(StringPool.BLANK, false, true);
	}

	@Test
	public void testUpdateCompanyWithInvalidVirtualHostnames()
		throws Exception {

		_testUpdateVirtualHostnames(
			new String[] {StringPool.BLANK, "localhost", ".abc"}, true);
	}

	@Test
	public void testUpdateCompanyWithValidMaxUsers() throws Exception {
		_testValidateMaxUsers(0);
		_testValidateMaxUsers(20);
		_testValidateMaxUsers(10000);
	}

	@Test
	public void testUpdateDisplay() throws Exception {
		SafeCloseable safeCloseable =
			CompanyThreadLocal.setCompanyIdWithSafeCloseable(
				_company.getCompanyId());

		String languageId = LocaleUtil.toLanguageId(_company.getLocale());
		TimeZone timeZone = _company.getTimeZone();

		try {
			User user = _userLocalService.getGuestUser(_company.getCompanyId());

			_userLocalService.updateUser(user);

			_companyLocalService.updateDisplay(
				_company.getCompanyId(),
				LocaleUtil.toLanguageId(LocaleUtil.HUNGARY), "CET");

			user = _userLocalService.getGuestUser(_company.getCompanyId());

			Assert.assertEquals(
				LocaleUtil.toLanguageId(LocaleUtil.HUNGARY),
				user.getLanguageId());
			Assert.assertEquals("CET", user.getTimeZoneId());
		}
		finally {
			_companyLocalService.updateDisplay(
				_company.getCompanyId(), languageId, timeZone.getID());

			safeCloseable.close();
		}
	}

	@Test
	public void testUpdateInvalidCompanyNames() throws Exception {
		String companyName = _company.getName();
		Group group = null;
		SafeCloseable safeCloseable =
			CompanyThreadLocal.setCompanyIdWithSafeCloseable(
				_company.getCompanyId());

		try {
			group = GroupTestUtil.addGroup(
				_company.getCompanyId(),
				_userLocalService.getGuestUserId(_company.getCompanyId()),
				GroupConstants.DEFAULT_PARENT_GROUP_ID);

			_testUpdateCompanyNames(
				_company,
				new String[] {StringPool.BLANK, group.getDescriptiveName()},
				true);
		}
		finally {
			_company = _companyLocalService.updateCompany(
				_company.getCompanyId(), _company.getVirtualHostname(),
				_company.getMx(), _company.getHomeURL(), true, null,
				companyName, _company.getLegalName(), _company.getLegalId(),
				_company.getLegalType(), _company.getSicCode(),
				_company.getTickerSymbol(), _company.getIndustry(),
				_company.getType(), _company.getSize());

			GroupTestUtil.deleteGroup(group);

			safeCloseable.close();
		}
	}

	@Test
	public void testUpdateMx() throws Exception {
		_testUpdateMx("abc.com", true, true);
		_testUpdateMx("abc.com", true, false);
		_testUpdateMx(StringPool.BLANK, false, true);
		_testUpdateMx(StringPool.BLANK, false, false);
	}

	@Test
	public void testUpdateValidCompanyNames() throws Exception {
		String companyName = _company.getName();

		try {
			_testUpdateCompanyNames(
				_company, new String[] {RandomTestUtil.randomString()}, false);
		}
		finally {
			_company = _companyLocalService.updateCompany(
				_company.getCompanyId(), _company.getVirtualHostname(),
				_company.getMx(), _company.getHomeURL(), true, null,
				companyName, _company.getLegalName(), _company.getLegalId(),
				_company.getLegalType(), _company.getSicCode(),
				_company.getTickerSymbol(), _company.getIndustry(),
				_company.getType(), _company.getSize());
		}
	}

	@Test
	public void testUpdateValidVirtualHostnames() throws Exception {
		_testUpdateVirtualHostnames(
			new String[] {
				"abc.com", "255.0.0.0", "0:0:0:0:0:0:0:1", "::1",
				"0000:0000:0000:0000:0000:0000:0000:0001"
			},
			false);
	}

	private static Company _addCompany() throws Exception {
		long counterCompanyId =
			_counterLocalService.increment(Company.class.getName()) + 1;

		Company company = _addCompany(
			RandomTestUtil.randomString() + "test.com");

		if (PropsValues.COMPANY_PREDICTABLE_COMPANY_IDS_ENABLED) {
			Assert.assertEquals(counterCompanyId, company.getCompanyId());
		}
		else {
			Assert.assertTrue(
				(company.getCompanyId() >= (long)Math.pow(10, 13)) &&
				(company.getCompanyId() < (long)Math.pow(10, 14)));
			Assert.assertNotEquals(counterCompanyId, company.getCompanyId());
		}

		return company;
	}

	private static Company _addCompany(String webId) throws Exception {
		return _companyLocalService.addCompany(
			null, webId, webId, "test.com", 0, true, true, null, null, null,
			null, null, null);
	}

	private static void _addCompanyUserGroupRole(Company company)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			long userId = _userLocalService.getGuestUserId(
				company.getCompanyId());

			Group group = GroupTestUtil.addGroup(
				company.getCompanyId(), userId,
				GroupConstants.DEFAULT_PARENT_GROUP_ID);

			User user = _addUser(
				company.getCompanyId(), userId, group.getGroupId(),
				_getServiceContext(company.getCompanyId()));

			UserGroup userGroup = UserGroupTestUtil.addUserGroup(
				group.getGroupId());

			_userGroupLocalService.addUserUserGroup(
				user.getUserId(), userGroup);

			Role role = _roleLocalService.addRole(
				RandomTestUtil.randomString(), userId, Group.class.getName(),
				group.getClassPK(), StringUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.getDefault(), StringUtil.randomString()),
				Collections.emptyMap(), RoleConstants.TYPE_SITE,
				StringPool.BLANK, _getServiceContext(company.getCompanyId()));

			_userGroupRoleLocalService.addUserGroupRole(
				user.getUserId(), group.getGroupId(), role.getRoleId());
		}
	}

	private static User _addUser(
			long companyId, long userId, long groupId,
			ServiceContext serviceContext)
		throws Exception {

		return UserTestUtil.addUser(
			companyId, userId,
			RandomTestUtil.randomString(NumericStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new long[] {groupId},
			serviceContext);
	}

	private static void _cleanUpData() throws Exception {
		List<ClassName> classNames = ListUtil.remove(
			_classNameLocalService.getClassNames(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			_classNames);

		for (ClassName className : classNames) {
			_classNameLocalService.deleteClassName(className);
		}

		_resetBackgroundTaskThreadLocal();

		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	private static ServiceContext _getServiceContext(long companyId) {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setCompanyId(companyId);

		return serviceContext;
	}

	private static void _initializeClassNames() throws Exception {
		_classNames = _classNameLocalService.getClassNames(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	private static List<String> _registerModelListeners() {
		Bundle bundle = FrameworkUtil.getBundle(CompanyLocalServiceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		List<String> modelListeners = new CopyOnWriteArrayList<>();

		_serviceRegistrations.add(
			bundleContext.registerService(
				ModelListener.class,
				new BaseModelListener<Role>() {

					@Override
					public void onBeforeRemove(Role role)
						throws ModelListenerException {

						modelListeners.add(Role.class.getName());
					}

				},
				new HashMapDictionary<>()));
		_serviceRegistrations.add(
			bundleContext.registerService(
				ModelListener.class,
				new BaseModelListener<UserGroupRole>() {

					@Override
					public void onBeforeRemove(UserGroupRole userGroupRole)
						throws ModelListenerException {

						modelListeners.add(UserGroupRole.class.getName());
					}

				},
				new HashMapDictionary<>()));

		return modelListeners;
	}

	private static void _resetBackgroundTaskThreadLocal() throws Exception {
		Field backgroundTaskIdField =
			BackgroundTaskThreadLocal.class.getDeclaredField(
				"_backgroundTaskId");

		backgroundTaskIdField.setAccessible(true);

		Method setMethod = ThreadLocal.class.getDeclaredMethod(
			"set", Object.class);

		setMethod.invoke(backgroundTaskIdField.get(null), 0L);
	}

	private LayoutSetPrototype _addLayoutSetPrototype(
			long companyId, long userId, String name)
		throws Exception {

		return _layoutSetPrototypeLocalService.addLayoutSetPrototype(
			userId, companyId,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), name
			).build(),
			new HashMap<Locale, String>(), true, true,
			_getServiceContext(companyId));
	}

	private List<String> _getTableNames(String type, long companyId)
		throws Exception {

		DatabaseMetaData databaseMetaData = _connection.getMetaData();

		List<String> objectNames = new ArrayList<>();

		String partitionName = CompanyLocalServiceTestUtil.getPartitionName(
			companyId);

		try (ResultSet resultSet = databaseMetaData.getTables(
				_dbPartitionDB.getCatalog(_connection, partitionName),
				_dbPartitionDB.getSchema(_connection, partitionName), null,
				new String[] {type})) {

			while (resultSet.next()) {
				objectNames.add(resultSet.getString("TABLE_NAME"));
			}
		}

		return objectNames;
	}

	private int _getTablesCount(long companyId) throws Exception {
		List<String> tableNames = _getTableNames("TABLE", companyId);

		return tableNames.size();
	}

	private int _getViewsCount(long companyId) throws Exception {
		List<String> tableNames = _getTableNames("VIEW", companyId);

		return tableNames.size();
	}

	private void _resetCompanyLocales(long companyId) throws Exception {
		ActionableDynamicQuery actionableDynamicQuery =
			_portalPreferenceValueLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				dynamicQuery.add(
					RestrictionsFactoryUtil.eq("companyId", companyId));
				dynamicQuery.add(
					RestrictionsFactoryUtil.eq("key", PropsKeys.LOCALES));
			});

		actionableDynamicQuery.setPerformActionMethod(
			(PortalPreferenceValue portalPreferenceValue) ->
				_portalPreferenceValueLocalService.deletePortalPreferenceValue(
					portalPreferenceValue));

		actionableDynamicQuery.performActions();

		PortalPreferences portalPreferences =
			_portalPreferenceValueLocalService.getPortalPreferences(
				_portalPreferencesLocalService.fetchPortalPreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY),
				false);

		portalPreferences.resetValues(null);

		LanguageUtil.resetAvailableLocales(companyId);
	}

	private void _testUpdateCompanyNames(
			Company company, String[] companyNames, boolean expectFailure)
		throws Exception {

		for (String companyName : companyNames) {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						company.getCompanyId())) {

				company = _companyLocalService.updateCompany(
					company.getCompanyId(), company.getVirtualHostname(),
					company.getMx(), company.getHomeURL(), true, null,
					companyName, company.getLegalName(), company.getLegalId(),
					company.getLegalType(), company.getSicCode(),
					company.getTickerSymbol(), company.getIndustry(),
					company.getType(), company.getSize());

				Assert.assertFalse(expectFailure);
			}
			catch (CompanyNameException companyNameException) {
				if (_log.isDebugEnabled()) {
					_log.debug(companyNameException);
				}

				Assert.assertTrue(expectFailure);
			}
		}
	}

	private void _testUpdateMx(String mx, boolean valid, boolean mailMxUpdate)
		throws Exception {

		String originalMx = _company.getMx();

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"COMPANY_MX_UPDATE", mailMxUpdate);
			SafeCloseable safeCloseable2 =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_companyLocalService.updateCompany(
				_company.getCompanyId(), _company.getVirtualHostname(), mx,
				_company.getMaxUsers(), _company.isActive());

			_company = _companyLocalService.getCompany(_company.getCompanyId());

			if (valid && mailMxUpdate) {
				Assert.assertNotEquals(originalMx, _company.getMx());
			}
			else {
				Assert.assertEquals(originalMx, _company.getMx());
			}
		}
		catch (CompanyMxException companyMxException) {
			if (_log.isDebugEnabled()) {
				_log.debug(companyMxException);
			}

			Assert.assertFalse(valid);
			Assert.assertTrue(mailMxUpdate);
		}
		finally {
			_company = _companyLocalService.updateCompany(
				_company.getCompanyId(), _company.getVirtualHostname(),
				originalMx, _company.getMaxUsers(), _company.isActive());
		}
	}

	private void _testUpdateVirtualHostnames(
			String[] virtualHostnames, boolean expectFailure)
		throws Exception {

		VirtualHost originalVirtualHost = null;

		try {
			originalVirtualHost =
				_virtualHostLocalService.fetchCompanyDefaultVirtualHost(
					_company.getCompanyId());

			for (String virtualHostname : virtualHostnames) {
				try (SafeCloseable safeCloseable =
						CompanyThreadLocal.setCompanyIdWithSafeCloseable(
							_company.getCompanyId())) {

					_companyLocalService.updateCompany(
						_company.getCompanyId(), virtualHostname,
						_company.getMx(), _company.getMaxUsers(),
						_company.isActive());

					Assert.assertFalse(expectFailure);
				}
				catch (CompanyVirtualHostException
							companyVirtualHostException) {

					if (_log.isDebugEnabled()) {
						_log.debug(companyVirtualHostException);
					}

					Assert.assertTrue(expectFailure);
				}
			}
		}
		finally {
			String originalVirtualHostname = originalVirtualHost.getHostname();

			VirtualHost virtualHost =
				_virtualHostLocalService.fetchCompanyDefaultVirtualHost(
					_company.getCompanyId());

			if (!originalVirtualHostname.equals(virtualHost.getHostname())) {
				_virtualHostLocalService.deleteVirtualHost(virtualHost);
			}

			_virtualHostLocalService.updateVirtualHost(originalVirtualHost);

			_company = _companyLocalService.updateCompany(
				_company.getCompanyId(), originalVirtualHostname,
				_company.getMx(), _company.getMaxUsers(), _company.isActive());
		}
	}

	private void _testValidateMaxUsers(int maxUsers) throws Exception {
		int originalMaxUsers = _company.getMaxUsers();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_company = _companyLocalService.updateCompany(
				_company.getCompanyId(), _company.getVirtualHostname(),
				_company.getMx(), maxUsers, _company.isActive());

			Assert.assertTrue(maxUsers >= 0);

			Assert.assertEquals(maxUsers, _company.getMaxUsers());
		}
		catch (CompanyMaxUsersException companyMaxUsersException) {
			Assert.assertTrue(maxUsers < 0);

			Assert.assertEquals(originalMaxUsers, _company.getMaxUsers());
		}
		finally {
			_company = _companyLocalService.updateCompany(
				_company.getCompanyId(), _company.getVirtualHostname(),
				_company.getMx(), originalMaxUsers, _company.isActive());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyLocalServiceTest.class);

	private static BundleContext _bundleContext;

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	private static List<ClassName> _classNames;
	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static Connection _connection;

	@Inject
	private static CounterLocalService _counterLocalService;

	private static DB _db;
	private static DBPartitionDB _dbPartitionDB;
	private static Company _deletedCompany;
	private static List<String> _modelListeners;

	@Inject
	private static RoleLocalService _roleLocalService;

	private static SafeCloseable _safeCloseable;
	private static final List<ServiceRegistration<?>> _serviceRegistrations =
		new CopyOnWriteArrayList<>();

	@Inject
	private static SystemEventLocalService _systemEventLocalService;

	private static final TransactionConfig _transactionConfig;

	@Inject
	private static UserGroupLocalService _userGroupLocalService;

	@Inject
	private static UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private static UserLocalService _userLocalService;

	static {
		TransactionConfig.Builder builder = new TransactionConfig.Builder();

		builder.setPropagation(Propagation.SUPPORTS);
		builder.setReadOnly(true);
		builder.setRollbackForClasses(Exception.class);

		_transactionConfig = builder.build();
	}

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Language _language;

	@Inject
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Inject
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private PasswordPolicyLocalService _passwordPolicyLocalService;

	@Inject
	private PersistenceManager _persistenceManager;

	@Inject
	private Portal _portal;

	@Inject
	private PortalPreferencesLocalService _portalPreferencesLocalService;

	@Inject
	private PortalPreferenceValueLocalService
		_portalPreferenceValueLocalService;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private PrefsProps _prefsProps;

	@Inject
	private Sites _sites;

	@Inject
	private StagingLocalService _stagingLocalService;

	@Inject
	private VirtualHostLocalService _virtualHostLocalService;

}