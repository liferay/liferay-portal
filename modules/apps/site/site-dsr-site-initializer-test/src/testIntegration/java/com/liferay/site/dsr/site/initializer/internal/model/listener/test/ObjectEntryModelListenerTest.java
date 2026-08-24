/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.model.listener.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.dsr.site.initializer.constants.DSRFolderConstants;
import com.liferay.site.dsr.site.initializer.test.util.DSRLayoutTestUtil;
import com.liferay.site.dsr.site.initializer.test.util.DSRTestUtil;
import com.liferay.site.dsr.site.initializer.thread.local.DSRRoomThreadLocal;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class ObjectEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, ServiceContextTestUtil.getServiceContext());
		_group = DSRTestUtil.getOrAddGroup();
		_objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", TestPropsValues.getCompanyId());

		BundleContext bundleContext = FrameworkUtil.getBundle(
			ObjectEntryModelListenerTest.class
		).getBundleContext();

		_serviceRegistrations.add(
			bundleContext.registerService(
				AnalyticsSettingsManager.class,
				new TestAnalyticsSettingsManager(_analyticsEnabled),
				HashMapDictionaryBuilder.<String, Object>put(
					"service.ranking", Integer.MAX_VALUE
				).build()));
	}

	@After
	public void tearDown() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	@Test
	public void testOnAfterCreate() throws Exception {
		_testOnAfterCreate();
		_testOnAfterCreateWithDSRRoomThreadLocal();
		_testOnAfterCreateWithDSRSellerRole();
	}

	@Test
	public void testOnAfterRemove() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry(
			"A" + RandomTestUtil.randomString());

		Assert.assertNotNull(
			_groupLocalService.fetchGroup(
				TestPropsValues.getCompanyId(),
				_classNameLocalService.getClassNameId(
					_objectDefinition.getClassName()),
				objectEntry.getObjectEntryId()));

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());

		Assert.assertNull(
			_groupLocalService.fetchGroup(
				TestPropsValues.getCompanyId(),
				_classNameLocalService.getClassNameId(
					_objectDefinition.getClassName()),
				objectEntry.getObjectEntryId()));
	}

	@Test
	@TestInfo("LPD-102253")
	public void testOnBeforeCreate() throws Exception {
		try {
			_objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId(), 0, null,
				HashMapBuilder.<String, Serializable>put(
					"expirationDate",
					new Date(System.currentTimeMillis() + Time.DAY)
				).put(
					"name", "A" + RandomTestUtil.randomString()
				).put(
					"r_accountToDSRRooms_accountEntryId",
					_accountEntry.getAccountEntryId()
				).build(),
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			Throwable throwable = modelListenerException.getCause();

			Assert.assertEquals(
				UnsupportedOperationException.class, throwable.getClass());
		}
	}

	@Test
	@TestInfo("LPD-102253")
	public void testOnBeforeUpdate() throws Exception {
		ObjectEntry objectEntry = _addObjectEntry(
			"A" + RandomTestUtil.randomString());

		try {
			_objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryFolderId(),
				HashMapBuilder.<String, Serializable>put(
					"expirationDate",
					new Date(System.currentTimeMillis() + Time.DAY)
				).build(),
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			Throwable throwable = modelListenerException.getCause();

			Assert.assertEquals(
				UnsupportedOperationException.class, throwable.getClass());
		}

		try {
			_objectEntryLocalService.expireObjectEntry(
				TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			Throwable throwable = modelListenerException.getCause();

			Assert.assertEquals(
				UnsupportedOperationException.class, throwable.getClass());
		}
	}

	private DLFileEntry _addFileEntry(long folderId, Group group)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			return _dlFileEntryLocalService.addFileEntry(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				group.getGroupId(), folderId, RandomTestUtil.randomString(),
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null, null,
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
				null, null,
				new ByteArrayInputStream(TestDataConstants.TEST_BYTE_ARRAY),
				TestDataConstants.TEST_BYTE_ARRAY.length, null, null, null,
				serviceContext);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private ObjectEntry _addObjectEntry(String name) throws Exception {
		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"name", name
			).put(
				"r_accountToDSRRooms_accountEntryId",
				_accountEntry.getAccountEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertHasResourcePermission(
			String actionId, ObjectEntry objectEntry, long roleId)
		throws Exception {

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectEntry.getCompanyId(), objectEntry.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry.getObjectEntryId()), roleId,
				actionId));
	}

	private void _testOnAfterCreate() throws Exception {
		String name = StringUtil.toLowerCase(
			"A" + RandomTestUtil.randomString());

		ObjectEntry objectEntry = _addObjectEntry(name);

		Group group = _groupLocalService.fetchGroup(
			TestPropsValues.getCompanyId(),
			_classNameLocalService.getClassNameId(
				_objectDefinition.getClassName()),
			objectEntry.getObjectEntryId());

		Assert.assertEquals("/" + name, group.getFriendlyURL());
		Assert.assertEquals(
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
			group.getMembershipRestriction());
		Assert.assertEquals(name, group.getName(LocaleUtil.getDefault()));
		Assert.assertEquals(
			GroupConstants.TYPE_SITE_RESTRICTED, group.getType());
		Assert.assertTrue(group.isManualMembership());
		Assert.assertTrue(group.isSite());

		objectEntry = _objectEntryLocalService.fetchObjectEntry(
			objectEntry.getObjectEntryId());

		Map<String, Serializable> values = objectEntry.getValues();

		Assert.assertEquals(
			group.getExternalReferenceCode(),
			values.get("siteExternalReferenceCode"));
		Assert.assertEquals(group.getGroupId(), values.get("siteId"));

		String friendlyURL = StringUtil.toLowerCase(
			"A" + RandomTestUtil.randomString());

		objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"friendlyURL", friendlyURL
			).put(
				"name", name
			).put(
				"r_accountToDSRRooms_accountEntryId",
				_accountEntry.getAccountEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(_analyticsEnabled.get());

		group = _groupLocalService.fetchGroup(
			TestPropsValues.getCompanyId(),
			_classNameLocalService.getClassNameId(
				_objectDefinition.getClassName()),
			objectEntry.getObjectEntryId());

		Assert.assertEquals("/" + friendlyURL, group.getFriendlyURL());
		Assert.assertEquals(name, group.getName(LocaleUtil.getDefault()));

		DSRLayoutTestUtil.assertFragmentEntryLink(group.getGroupId());
		DSRLayoutTestUtil.assertLayouts(
			group.getGroupId(),
			new String[] {"Documents", "Login", "Onboarding"}, false);

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		String[] actionIds = TransformUtil.transformToArray(
			_resourceActionLocalService.getResourceActions(
				objectDefinition.getClassName()),
			ResourceAction::getActionId, String.class);

		Role role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), RoleConstants.OWNER);

		for (String actionId : actionIds) {
			_assertHasResourcePermission(
				actionId, objectEntry, role.getRoleId());
		}
	}

	private void _testOnAfterCreateWithDSRRoomThreadLocal() throws Exception {
		ObjectEntry sourceObjectEntry = _addObjectEntry(
			StringUtil.toLowerCase("A" + RandomTestUtil.randomString()));

		Group sourceGroup = _groupLocalService.fetchGroup(
			TestPropsValues.getCompanyId(),
			_classNameLocalService.getClassNameId(
				_objectDefinition.getClassName()),
			sourceObjectEntry.getObjectEntryId());

		DLFolder sourceDLFolder =
			_dlFolderLocalService.getDLFolderByExternalReferenceCode(
				DSRFolderConstants.EXTERNAL_REFERENCE_CODE_DSR_DOCUMENTS,
				sourceGroup.getGroupId());

		DLFileEntry dlFileEntry1 = _addFileEntry(
			sourceDLFolder.getFolderId(), sourceGroup);
		DLFileEntry dlFileEntry2 = _addFileEntry(
			sourceDLFolder.getFolderId(), sourceGroup);

		ObjectEntry objectEntry;

		DSRRoomThreadLocal.setFileEntryIds(
			new long[] {dlFileEntry1.getFileEntryId()});
		DSRRoomThreadLocal.setObjectEntryId(
			sourceObjectEntry.getObjectEntryId());

		try {
			objectEntry = _addObjectEntry(
				StringUtil.toLowerCase("A" + RandomTestUtil.randomString()));
		}
		finally {
			DSRRoomThreadLocal.setFileEntryIds(new long[0]);
			DSRRoomThreadLocal.setObjectEntryId(0);
		}

		Group group = _groupLocalService.fetchGroup(
			TestPropsValues.getCompanyId(),
			_classNameLocalService.getClassNameId(
				_objectDefinition.getClassName()),
			objectEntry.getObjectEntryId());

		Assert.assertTrue(
			_userGroupRoleLocalService.hasUserGroupRole(
				TestPropsValues.getUserId(), group.getGroupId(),
				RoleConstants.SITE_OWNER));
		Assert.assertNotEquals(sourceGroup.getGroupId(), group.getGroupId());

		DSRLayoutTestUtil.assertLayouts(
			group.getGroupId(),
			new String[] {"Documents", "Login", "Onboarding"}, false);

		DLFolder dlFolder =
			_dlFolderLocalService.getDLFolderByExternalReferenceCode(
				DSRFolderConstants.EXTERNAL_REFERENCE_CODE_DSR_DOCUMENTS,
				group.getGroupId());

		List<DLFileEntry> dlFileEntries =
			_dlFileEntryLocalService.getFileEntries(
				group.getGroupId(), dlFolder.getFolderId());

		Assert.assertTrue(
			ListUtil.exists(
				dlFileEntries,
				dlFileEntry ->
					(dlFileEntry.getFileEntryId() !=
						dlFileEntry1.getFileEntryId()) &&
					Objects.equals(
						dlFileEntry.getTitle(), dlFileEntry1.getTitle())));
		Assert.assertFalse(
			ListUtil.exists(
				dlFileEntries,
				dlFileEntry -> Objects.equals(
					dlFileEntry.getTitle(), dlFileEntry2.getTitle())));
	}

	private void _testOnAfterCreateWithDSRSellerRole() throws Exception {
		User user = UserTestUtil.addUser();

		Role dsrSellerRole = _roleLocalService.fetchRoleByExternalReferenceCode(
			"L_DSR_SELLER", TestPropsValues.getCompanyId());

		_userLocalService.addRoleUser(dsrSellerRole.getRoleId(), user);

		String name = StringUtil.toLowerCase(
			"B" + RandomTestUtil.randomString());

		ObjectEntry objectEntry;

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			objectEntry = _objectEntryService.addObjectEntry(
				0, _objectDefinition.getObjectDefinitionId(), 0, null,
				HashMapBuilder.<String, Serializable>put(
					"name", name
				).put(
					"r_accountToDSRRooms_accountEntryId",
					_accountEntry.getAccountEntryId()
				).build(),
				ServiceContextTestUtil.getServiceContext());

			Group group = _groupLocalService.fetchGroup(
				TestPropsValues.getCompanyId(),
				_classNameLocalService.getClassNameId(
					_objectDefinition.getClassName()),
				objectEntry.getObjectEntryId());

			Assert.assertEquals("/" + name, group.getFriendlyURL());
			Assert.assertEquals(
				GroupConstants.TYPE_SITE_RESTRICTED, group.getType());
			Assert.assertTrue(group.isSite());

			LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
				group.getGroupId(), false);

			LayoutSetPrototype layoutSetPrototype =
				_layoutSetPrototypeLocalService.
					fetchLayoutSetPrototypeByUuidAndCompanyId(
						"L_DSR_LAYOUT_SET_PROTOTYPE",
						TestPropsValues.getCompanyId());

			Assert.assertEquals(
				layoutSetPrototype.getLayoutSetPrototypeId(),
				layoutSet.getLayoutSetPrototypeId());
			Assert.assertEquals(
				layoutSetPrototype.getUuid(),
				layoutSet.getLayoutSetPrototypeUuid());

			DSRLayoutTestUtil.assertLayouts(
				group.getGroupId(),
				new String[] {"Documents", "Login", "Onboarding"}, false);

			Assert.assertTrue(
				_userGroupRoleLocalService.hasUserGroupRole(
					user.getUserId(), group.getGroupId(),
					RoleConstants.SITE_OWNER));
		}
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private final AtomicBoolean _analyticsEnabled = new AtomicBoolean();

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectEntryService _objectEntryService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

	private static class TestAnalyticsSettingsManager
		implements AnalyticsSettingsManager {

		public TestAnalyticsSettingsManager(AtomicBoolean analyticsEnabled) {
			_analyticsEnabled = analyticsEnabled;
		}

		@Override
		public void deleteCompanyConfiguration(long companyId) {
		}

		@Override
		public AnalyticsConfiguration getAnalyticsConfiguration(
			long companyId) {

			return null;
		}

		@Override
		public long[] getCommerceChannelIds(long companyId, long[] groupIds) {
			return new long[0];
		}

		@Override
		public Long[] getSiteIds(String analyticsChannelId, long companyId) {
			return new Long[0];
		}

		@Override
		public boolean isAnalyticsEnabled(long companyId) {
			_analyticsEnabled.set(true);

			return false;
		}

		@Override
		public boolean isSiteIdSynced(long companyId, long groupId) {
			return false;
		}

		@Override
		public boolean syncedAccountSettingsEnabled(long companyId) {
			return false;
		}

		@Override
		public boolean syncedContactSettingsEnabled(long companyId) {
			return false;
		}

		@Override
		public void updateCompanyConfiguration(
			long companyId, Map<String, Object> properties) {
		}

		@Override
		public String[] updateSiteIds(
			String analyticsChannelId, long companyId,
			Long[] dataSourceSiteIds) {

			return new String[0];
		}

		private final AtomicBoolean _analyticsEnabled;

	}

}