/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
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
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;
import com.liferay.site.cms.site.initializer.util.CMSDefaultPermissionUtil;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class ObjectEntryFolderModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CMSTestUtil.getOrAddGroup(ObjectEntryFolderModelListenerTest.class);

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		_group = depotEntry.getGroup();
	}

	@Test
	@TestInfo("LPD-92888")
	public void testOnAfterCreate() throws Exception {
		JSONObject rootJSONObject = CMSDefaultPermissionUtil.getJSONObject(
			_group.getCompanyId(), _group.getCreatorUserId(),
			_group.getExternalReferenceCode(), DepotEntry.class.getName(),
			_filterFactory);

		ObjectEntryFolder rootObjectEntryFolder = _addObjectEntryFolder(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		_assertResourcePermissions(null, rootJSONObject, rootObjectEntryFolder);

		ObjectEntryFolder childObjectEntryFolder = _addObjectEntryFolder(
			rootObjectEntryFolder.getObjectEntryFolderId());

		_assertResourcePermissions(
			null, rootJSONObject, childObjectEntryFolder);

		ObjectEntry objectEntry = _fetchObjectEntry(rootObjectEntryFolder);

		Assert.assertNotNull(objectEntry);

		String actionId = RandomTestUtil.randomString();

		rootJSONObject.put(
			"OBJECT_ENTRY_FOLDERS",
			JSONUtil.put(
				DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
				JSONUtil.putAll(
					ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER, ActionKeys.DELETE,
					ActionKeys.PERMISSIONS)
			).put(
				RoleConstants.CMS_ADMINISTRATOR,
				JSONUtil.putAll(actionId, ActionKeys.UPDATE, ActionKeys.VIEW)
			).put(
				RoleConstants.USER,
				JSONUtil.putAll(
					ActionKeys.ADD_ENTRY, ActionKeys.DELETE, ActionKeys.VIEW)
			));

		CMSDefaultPermissionUtil.addOrUpdateObjectEntry(
			objectEntry.getExternalReferenceCode(),
			rootObjectEntryFolder.getCompanyId(),
			rootObjectEntryFolder.getUserId(),
			rootObjectEntryFolder.getExternalReferenceCode(),
			rootObjectEntryFolder.getModelClassName(), rootJSONObject,
			rootObjectEntryFolder.getGroupId(),
			rootObjectEntryFolder.getTreePath());

		childObjectEntryFolder = _addObjectEntryFolder(
			rootObjectEntryFolder.getObjectEntryFolderId());

		_assertResourcePermissions(
			actionId, rootJSONObject, childObjectEntryFolder);
	}

	@Test
	public void testOnAfterRemove() throws Exception {
		ObjectEntryFolder rootObjectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					_group.getGroupId(), _group.getCompanyId());

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				RandomTestUtil.randomString(), _group.getGroupId(),
				_group.getCreatorUserId(),
				rootObjectEntryFolder.getObjectEntryFolderId(), "",
				HashMapBuilder.put(
					LocaleUtil.ENGLISH, RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		User user = UserTestUtil.addGroupAdminUser(_group);

		int sharingEntriesCount =
			_sharingEntryLocalService.getSharingEntriesCount();

		_sharingEntryLocalService.addSharingEntry(
			null, TestPropsValues.getUserId(), 0, 0, user.getUserId(),
			_portal.getClassNameId(ObjectEntryFolder.class.getName()),
			objectEntryFolder.getObjectEntryFolderId(), _group.getGroupId(),
			true, Arrays.asList(SharingEntryAction.VIEW), null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(
			sharingEntriesCount + 1,
			_sharingEntryLocalService.getSharingEntriesCount());

		_objectEntryFolderLocalService.deleteObjectEntryFolder(
			objectEntryFolder.getObjectEntryFolderId());

		Assert.assertEquals(
			sharingEntriesCount,
			_sharingEntryLocalService.getSharingEntriesCount());

		Assert.assertNull(_fetchObjectEntry(objectEntryFolder));
	}

	@Test
	@TestInfo("LPD-92888")
	public void testOnAfterUpdate() throws Exception {
		_testOnAfterUpdateWithExternalReferenceCode();
		_testOnAfterUpdateWithParentObjectEntryFolder();
	}

	private ObjectEntryFolder _addObjectEntryFolder(
			long parentObjectEntryFolderId)
		throws Exception {

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			RandomTestUtil.randomString(), _group.getGroupId(),
			_group.getCreatorUserId(), parentObjectEntryFolderId, "",
			HashMapBuilder.put(
				LocaleUtil.ENGLISH, RandomTestUtil.randomString()
			).build(),
			RandomTestUtil.randomString(), new ServiceContext());
	}

	private void _assertResourcePermissions(
			String actionId, JSONObject expectedDefaultPermissionsJSONObject,
			ObjectEntryFolder objectEntryFolder)
		throws Exception {

		JSONObject actualDefaultPermissionsJSONObject =
			CMSDefaultPermissionUtil.getJSONObject(
				objectEntryFolder.getCompanyId(), objectEntryFolder.getUserId(),
				objectEntryFolder.getExternalReferenceCode(),
				objectEntryFolder.getModelClassName(), _filterFactory);

		Assert.assertEquals(
			expectedDefaultPermissionsJSONObject.toString(),
			actualDefaultPermissionsJSONObject.toString());

		JSONObject jsonObject =
			expectedDefaultPermissionsJSONObject.getJSONObject(
				"OBJECT_ENTRY_FOLDERS");

		for (String roleName : jsonObject.keySet()) {
			Set<String> actionIds = JSONUtil.toStringSet(
				jsonObject.getJSONArray(roleName));

			ResourcePermission resourcePermission = _fetchResourcePermission(
				objectEntryFolder, roleName);

			for (ResourceAction resourceAction :
					_resourceActionLocalService.getResourceActions(
						ObjectEntryFolder.class.getName())) {

				String resourceActionActionId = resourceAction.getActionId();

				if ((objectEntryFolder.getParentObjectEntryFolderId() ==
						ObjectEntryFolderConstants.
							PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT) &&
					Objects.equals(resourceActionActionId, ActionKeys.DELETE)) {

					Assert.assertFalse(
						resourcePermission.hasActionId(resourceActionActionId));
				}
				else {
					Assert.assertEquals(
						actionIds.toString(),
						actionIds.contains(resourceActionActionId),
						resourcePermission.hasActionId(resourceActionActionId));
				}
			}

			Assert.assertFalse(resourcePermission.hasActionId(actionId));
		}
	}

	private ObjectEntry _fetchObjectEntry(ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return CMSDefaultPermissionUtil.fetchObjectEntry(
			objectEntryFolder.getCompanyId(), objectEntryFolder.getUserId(),
			objectEntryFolder.getExternalReferenceCode(),
			objectEntryFolder.getModelClassName(), _filterFactory);
	}

	private ResourcePermission _fetchResourcePermission(
			ObjectEntryFolder objectEntryFolder, String roleName)
		throws Exception {

		Role role = RoleLocalServiceUtil.getRole(
			_group.getCompanyId(), roleName);

		return _resourcePermissionLocalService.fetchResourcePermission(
			_group.getCompanyId(), ObjectEntryFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntryFolder.getObjectEntryFolderId()),
			role.getRoleId());
	}

	private void _testOnAfterUpdateWithExternalReferenceCode()
		throws Exception {

		ObjectEntryFolder rootObjectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					_group.getGroupId(), _group.getCompanyId());

		ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder(
			rootObjectEntryFolder.getObjectEntryFolderId());

		String originalExternalReferenceCode =
			objectEntryFolder.getExternalReferenceCode();

		Assert.assertNotNull(_fetchObjectEntry(objectEntryFolder));

		objectEntryFolder.setExternalReferenceCode(
			RandomTestUtil.randomString());

		objectEntryFolder =
			_objectEntryFolderLocalService.updateObjectEntryFolder(
				objectEntryFolder);

		Assert.assertNull(
			CMSDefaultPermissionUtil.fetchObjectEntry(
				objectEntryFolder.getCompanyId(), objectEntryFolder.getUserId(),
				originalExternalReferenceCode,
				objectEntryFolder.getModelClassName(), _filterFactory));

		Assert.assertNotNull(_fetchObjectEntry(objectEntryFolder));
	}

	private void _testOnAfterUpdateWithParentObjectEntryFolder()
		throws Exception {

		ObjectEntryFolder objectEntryFolder1 =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					_group.getGroupId(), _group.getCompanyId());

		ObjectEntryFolder objectEntryFolder2 = _addObjectEntryFolder(
			objectEntryFolder1.getObjectEntryFolderId());

		JSONObject jsonObject = CMSDefaultPermissionUtil.getJSONObject(
			objectEntryFolder2.getCompanyId(), objectEntryFolder2.getUserId(),
			objectEntryFolder2.getExternalReferenceCode(),
			objectEntryFolder2.getModelClassName(), _filterFactory);

		_assertResourcePermissions(null, jsonObject, objectEntryFolder2);

		ObjectEntryFolder objectEntryFolder3 = _addObjectEntryFolder(
			objectEntryFolder1.getObjectEntryFolderId());

		_assertResourcePermissions(null, jsonObject, objectEntryFolder3);

		ObjectEntry objectEntry = _fetchObjectEntry(objectEntryFolder2);

		Assert.assertNotNull(objectEntry);

		String actionId = RandomTestUtil.randomString();

		jsonObject.put(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
			JSONUtil.put(
				RoleConstants.CMS_ADMINISTRATOR,
				JSONUtil.putAll(actionId, ActionKeys.UPDATE, ActionKeys.VIEW)
			).put(
				RoleConstants.USER, JSONUtil.putAll(ActionKeys.VIEW)
			));

		CMSDefaultPermissionUtil.addOrUpdateObjectEntry(
			objectEntry.getExternalReferenceCode(),
			objectEntryFolder2.getCompanyId(), objectEntryFolder2.getUserId(),
			objectEntryFolder2.getExternalReferenceCode(),
			objectEntryFolder2.getModelClassName(), jsonObject,
			objectEntryFolder2.getGroupId(), objectEntryFolder2.getTreePath());

		objectEntryFolder3 =
			_objectEntryFolderLocalService.moveObjectEntryFolder(
				objectEntryFolder3.getUserId(),
				objectEntryFolder3.getObjectEntryFolderId(),
				objectEntryFolder2.getObjectEntryFolderId(), false,
				ServiceContextTestUtil.getServiceContext());

		_assertResourcePermissions(actionId, jsonObject, objectEntryFolder3);
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private FilterFactory<Predicate> _filterFactory;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

}