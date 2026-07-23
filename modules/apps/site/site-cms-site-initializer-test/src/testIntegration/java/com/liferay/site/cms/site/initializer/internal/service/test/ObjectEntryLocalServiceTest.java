/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.definition.setting.builder.ObjectDefinitionSettingBuilder;
import com.liferay.object.definition.util.ObjectDefinitionUtil;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.cms.site.initializer.internal.service.test.util.CMSObjectEntryTestUtil;
import com.liferay.trash.service.TrashEntryLocalService;

import java.io.Serializable;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class ObjectEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		_group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				_group.getCompanyId());

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addSystemObjectDefinition(
				null, TestPropsValues.getUserId(),
				objectFolder.getObjectFolderId(),
				ObjectDefinitionUtil.generateRandomClassName(), null, true,
				false, true, false, true, false, false, false, false, false,
				null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, "Test", null, null, null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, ObjectDefinitionConstants.SCOPE_DEPOT, null, 1,
				WorkflowConstants.STATUS_DRAFT,
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS
					).value(
						StringPool.TRUE
					).build()),
				List.of(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())),
				Collections.emptyList());

		_objectDefinition =
			_objectDefinitionLocalService.publishSystemObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		_user = UserTestUtil.addUser();
	}

	@Test
	public void testAddObjectEntry() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_depotEntry.getGroupId());

		AssertUtils.assertFailure(
			AssetCategoryException.class, null,
			() -> _testAddObjectTypeEntry(serviceContext));

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		_testAddObjectTypeEntry(serviceContext);
	}

	@Test
	public void testCheckObjectEntries() throws Exception {
		ObjectEntry objectEntry =
			_objectEntryLocalService.addOrUpdateObjectEntry(
				null, _depotEntry.getGroupId(), _user.getUserId(),
				_objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				HashMapBuilder.<String, Serializable>put(
					"reviewDate", new Date()
				).build(),
				ServiceContextTestUtil.getServiceContext(
					_depotEntry.getGroupId()));

		_objectEntryLocalService.checkObjectEntries(objectEntry.getCompanyId());

		_assertUserNotificationEvent(objectEntry.getObjectEntryId());
	}

	@Test
	public void testPublishSystemObjectDefinition() throws Exception {
		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				_getObjectFolderExternalReferenceCode(), _group.getCompanyId());

		ObjectDefinition systemObjectDefinition =
			ObjectDefinitionLocalServiceUtil.addSystemObjectDefinition(
				null, TestPropsValues.getUserId(),
				objectFolder.getObjectFolderId(),
				ObjectDefinitionUtil.generateRandomClassName(), null, true,
				false, true, false, true, false, false, false, false, false,
				null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, "Test" + StringUtil.randomString(), null, null, null,
				null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, ObjectDefinitionConstants.SCOPE_DEPOT, null, 1,
				WorkflowConstants.STATUS_DRAFT, Collections.emptyList(),
				List.of(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())),
				Collections.emptyList());

		systemObjectDefinition =
			_objectDefinitionLocalService.publishSystemObjectDefinition(
				TestPropsValues.getUserId(),
				systemObjectDefinition.getObjectDefinitionId());

		_assertHasResourcePermissionScopeCompany(systemObjectDefinition);
		_assertHasResourcePermissionScopeIndividual(systemObjectDefinition);

		_objectDefinitionLocalService.deleteObjectDefinition(
			systemObjectDefinition.getObjectDefinitionId());
	}

	@Test
	@TestInfo("LPD-89104")
	public void testRestoreObjectEntryFromTrash() throws Exception {
		_testRestoreObjectEntryFromTrashWhenParentFolderExists();
		_testRestoreObjectEntryFromTrashWhenParentFolderDeleted();
	}

	private ObjectEntry _addBasicWebContentObjectEntry(
			ObjectDefinition objectDefinition, ServiceContext serviceContext)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					"L_CONTENTS", _depotEntry.getGroupId(),
					_depotEntry.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), _depotEntry.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), "en_US",
			HashMapBuilder.<String, Serializable>put(
				"content_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build(),
			serviceContext);
	}

	private ObjectEntryFolder _addObjectEntryFolder() throws Exception {
		return _objectEntryFolderLocalService.addObjectEntryFolder(
			RandomTestUtil.randomString(), _depotEntry.getGroupId(),
			TestPropsValues.getUserId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			RandomTestUtil.randomString(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_depotEntry.getGroupId()));
	}

	private void _assertHasResourcePermissionScopeCompany(
			ObjectDefinition objectDefinition)
		throws Exception {

		Role role = _roleLocalService.getRole(
			objectDefinition.getCompanyId(), RoleConstants.CMS_ADMINISTRATOR);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()),
				role.getRoleId(), ActionKeys.DELETE));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()),
				role.getRoleId(), ActionKeys.PERMISSIONS));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()),
				role.getRoleId(), ActionKeys.UPDATE));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(objectDefinition.getCompanyId()),
				role.getRoleId(), ActionKeys.VIEW));
	}

	private void _assertHasResourcePermissionScopeIndividual(
			ObjectDefinition objectDefinition)
		throws Exception {

		Role role = _roleLocalService.getRole(
			objectDefinition.getCompanyId(), RoleConstants.GUEST);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				ObjectDefinition.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectDefinition.getObjectDefinitionId()),
				role.getRoleId(), ActionKeys.VIEW));

		role = _roleLocalService.getRole(
			objectDefinition.getCompanyId(), RoleConstants.USER);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				objectDefinition.getCompanyId(),
				ObjectDefinition.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectDefinition.getObjectDefinitionId()),
				role.getRoleId(), ActionKeys.VIEW));
	}

	private void _assertUserNotificationEvent(long objectEntryId) {
		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
				_user.getUserId());

		Assert.assertEquals(
			userNotificationEvents.toString(), 1,
			userNotificationEvents.size());

		UserNotificationEvent userNotificationEvent =
			userNotificationEvents.get(0);

		Assert.assertEquals(
			_objectDefinition.getPortletId(), userNotificationEvent.getType());

		String payload = userNotificationEvent.getPayload();

		Assert.assertTrue(payload.contains("classPK\":" + objectEntryId));
	}

	private String _getObjectFolderExternalReferenceCode() {
		if (RandomTestUtil.randomBoolean()) {
			return ObjectFolderConstants.
				EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES;
		}

		return ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES;
	}

	private void _testAddObjectTypeEntry(ServiceContext serviceContext)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", TestPropsValues.getCompanyId());

		AssetVocabulary vocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId(),
			_portal.getClassNameId(objectDefinition.getClassName()),
			AssetCategoryConstants.ALL_CLASS_TYPE_PK, true);

		AssetTestUtil.addCategory(
			_group.getGroupId(), vocabulary.getVocabularyId());

		try {
			_addBasicWebContentObjectEntry(objectDefinition, serviceContext);
		}
		finally {
			_assetVocabularyLocalService.deleteVocabulary(vocabulary);
		}
	}

	private void _testRestoreObjectEntryFromTrashWhenParentFolderDeleted()
		throws Exception {

		ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder();

		ObjectEntry objectEntry = CMSObjectEntryTestUtil.addObjectEntry(
			_depotEntry.getGroupId(), _objectDefinition,
			objectEntryFolder.getObjectEntryFolderId());

		CMSObjectEntryTestUtil.moveObjectEntryToTrash(
			_depotEntry.getGroupId(), objectEntry);

		_objectEntryFolderLocalService.deleteObjectEntryFolder(
			objectEntryFolder.getObjectEntryFolderId());

		ObjectEntry trashedObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				objectEntry.getObjectEntryId());

		ObjectEntry restoredObjectEntry =
			_objectEntryLocalService.restoreObjectEntryFromTrash(
				TestPropsValues.getUserId(), trashedObjectEntry,
				ServiceContextTestUtil.getServiceContext(
					_depotEntry.getGroupId()));

		Assert.assertEquals(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			restoredObjectEntry.getObjectEntryFolderId());
	}

	private void _testRestoreObjectEntryFromTrashWhenParentFolderExists()
		throws Exception {

		ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder();

		ObjectEntry objectEntry = CMSObjectEntryTestUtil.addObjectEntry(
			_depotEntry.getGroupId(), _objectDefinition,
			objectEntryFolder.getObjectEntryFolderId());

		CMSObjectEntryTestUtil.moveObjectEntryToTrash(
			_depotEntry.getGroupId(), objectEntry);

		ObjectEntry trashedObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				objectEntry.getObjectEntryId());

		ObjectEntry restoredObjectEntry =
			_objectEntryLocalService.restoreObjectEntryFromTrash(
				TestPropsValues.getUserId(), trashedObjectEntry,
				ServiceContextTestUtil.getServiceContext(
					_depotEntry.getGroupId()));

		Assert.assertEquals(
			objectEntryFolder.getObjectEntryFolderId(),
			restoredObjectEntry.getObjectEntryFolderId());
		Assert.assertNull(
			_trashEntryLocalService.fetchEntry(
				_objectDefinition.getClassName(),
				restoredObjectEntry.getObjectEntryId()));
	}

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private TrashEntryLocalService _trashEntryLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}