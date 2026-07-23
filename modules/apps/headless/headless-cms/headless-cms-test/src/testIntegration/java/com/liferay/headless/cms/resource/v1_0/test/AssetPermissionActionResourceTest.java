/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.headless.cms.client.dto.v1_0.ResetAssetPermissionAction;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;
import com.liferay.site.cms.site.initializer.util.CMSDefaultPermissionUtil;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balazs Breier
 */
@RunWith(Arquillian.class)
public class AssetPermissionActionResourceTest
	extends BaseAssetPermissionActionResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		super.setUp();

		CMSTestUtil.getOrAddGroup(AssetPermissionActionResourceTest.class);

		_cmsAdministratorRole = _getOrAddCMSAdministratorRole(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId());
		_userRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);
	}

	@Override
	@Test
	public void testPostAssetPermission() throws Exception {
		_testPostAssetPermissionWithTypeResetAssetPermission();
	}

	private void _assertHasActionIds(
		ResourcePermission resourcePermission, String... actionIds) {

		for (String actionId : _ACTION_IDS) {
			if (ArrayUtil.contains(actionIds, actionId)) {
				Assert.assertTrue(resourcePermission.hasActionId(actionId));
			}
			else {
				Assert.assertFalse(resourcePermission.hasActionId(actionId));
			}
		}
	}

	private Role _getOrAddCMSAdministratorRole(long companyId, long userId)
		throws Exception {

		Role role = _roleLocalService.fetchRole(
			companyId, RoleConstants.CMS_ADMINISTRATOR);

		if (role != null) {
			return role;
		}

		return _roleLocalService.addRole(
			null, userId, null, 0, RoleConstants.CMS_ADMINISTRATOR, null, null,
			RoleConstants.TYPE_REGULAR, null, null);
	}

	private void _testPostAssetPermissionWithTypeResetAssetPermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE, serviceContext);

		Group group = depotEntry.getGroup();

		ObjectEntryFolder objectEntryFolder1 =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					group.getGroupId(), depotEntry.getCompanyId());

		ObjectEntryFolder objectEntryFolder2 =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				RandomTestUtil.randomString(), group.getGroupId(),
				group.getCreatorUserId(),
				objectEntryFolder1.getObjectEntryFolderId(), "",
				HashMapBuilder.put(
					LocaleUtil.ENGLISH, RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(), serviceContext);

		JSONObject jsonObject = CMSDefaultPermissionUtil.getJSONObject(
			objectEntryFolder2.getCompanyId(), objectEntryFolder2.getUserId(),
			objectEntryFolder2.getExternalReferenceCode(),
			objectEntryFolder2.getModelClassName(), _filterFactory);

		jsonObject.put(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
			JSONUtil.put(
				RoleConstants.CMS_ADMINISTRATOR,
				JSONUtil.putAll(ActionKeys.UPDATE, ActionKeys.VIEW))
		).put(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
			JSONUtil.put(
				RoleConstants.CMS_ADMINISTRATOR,
				JSONUtil.putAll(ActionKeys.DELETE, ActionKeys.PERMISSIONS))
		).put(
			"OBJECT_ENTRY_FOLDERS",
			JSONUtil.put(
				RoleConstants.CMS_ADMINISTRATOR,
				JSONUtil.putAll(
					ActionKeys.ADD_ENTRY,
					ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER,
					ActionKeys.SUBSCRIBE))
		);

		ObjectEntry objectEntry1 = CMSDefaultPermissionUtil.fetchObjectEntry(
			objectEntryFolder2.getCompanyId(), objectEntryFolder2.getUserId(),
			objectEntryFolder2.getExternalReferenceCode(),
			objectEntryFolder2.getModelClassName(), _filterFactory);

		CMSDefaultPermissionUtil.addOrUpdateObjectEntry(
			objectEntry1.getExternalReferenceCode(),
			objectEntryFolder2.getCompanyId(), objectEntryFolder2.getUserId(),
			objectEntryFolder2.getExternalReferenceCode(),
			objectEntryFolder2.getModelClassName(), jsonObject,
			objectEntryFolder2.getGroupId(), objectEntryFolder2.getTreePath());

		ObjectDefinition objectDefinition1 =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT",
					objectEntryFolder2.getCompanyId());

		serviceContext.setAttribute(
			"friendlyUrlMap", new HashMap<String, String>());

		ObjectEntry objectEntry2 = _objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), depotEntry.getUserId(),
			objectDefinition1.getObjectDefinitionId(),
			objectEntryFolder2.getObjectEntryFolderId(), "en_US",
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build(),
			serviceContext);

		_resourcePermissionLocalService.setResourcePermissions(
			objectEntry2.getCompanyId(), objectEntry2.getModelClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry2.getPrimaryKeyObj()),
			_cmsAdministratorRole.getRoleId(),
			new String[] {ActionKeys.DELETE});

		assetPermissionActionResource.postAssetPermission(
			new ResetAssetPermissionAction() {
				{
					setClassName(objectDefinition1.getClassName());
					setClassPK(objectEntry2.getObjectEntryId());
					setType(Type.RESET_ASSET_PERMISSION_ACTION);
				}
			});

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				objectEntry2.getCompanyId(), objectEntry2.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry2.getObjectEntryId()),
				_cmsAdministratorRole.getRoleId());

		_assertHasActionIds(
			resourcePermission, ActionKeys.UPDATE, ActionKeys.VIEW);

		ObjectDefinition objectDefinition2 =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", objectEntryFolder2.getCompanyId());

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), depotEntry.getGroupId(),
			depotEntry.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, null,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new ByteArrayInputStream(TestDataConstants.TEST_BYTE_ARRAY),
			TestDataConstants.TEST_BYTE_ARRAY.length, null, null, null,
			serviceContext);

		objectEntryFolder1 =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
					group.getGroupId(), depotEntry.getCompanyId());

		objectEntryFolder2 =
			_objectEntryFolderLocalService.updateObjectEntryFolder(
				objectEntryFolder2.getUserId(),
				objectEntryFolder2.getObjectEntryFolderId(),
				objectEntryFolder1.getObjectEntryFolderId(), "",
				HashMapBuilder.put(
					LocaleUtil.ENGLISH, RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(), serviceContext);

		objectEntry1 = CMSDefaultPermissionUtil.fetchObjectEntry(
			objectEntryFolder2.getCompanyId(), objectEntryFolder2.getUserId(),
			objectEntryFolder2.getExternalReferenceCode(),
			objectEntryFolder2.getModelClassName(), _filterFactory);

		CMSDefaultPermissionUtil.addOrUpdateObjectEntry(
			objectEntry1.getExternalReferenceCode(),
			objectEntryFolder2.getCompanyId(), objectEntryFolder2.getUserId(),
			objectEntryFolder2.getExternalReferenceCode(),
			objectEntryFolder2.getModelClassName(), jsonObject,
			objectEntryFolder2.getGroupId(), objectEntryFolder2.getTreePath());

		ObjectEntry objectEntry3 = _objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), depotEntry.getUserId(),
			objectDefinition2.getObjectDefinitionId(),
			objectEntryFolder2.getObjectEntryFolderId(), "en_US",
			HashMapBuilder.<String, Serializable>put(
				"file", dlFileEntry.getFileEntryId()
			).put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build(),
			serviceContext);

		_resourcePermissionLocalService.setResourcePermissions(
			objectEntry3.getCompanyId(), objectEntry3.getModelClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry3.getPrimaryKeyObj()),
			_cmsAdministratorRole.getRoleId(), new String[] {ActionKeys.VIEW});

		assetPermissionActionResource.postAssetPermission(
			new ResetAssetPermissionAction() {
				{
					setClassName(objectDefinition2.getClassName());
					setClassPK(objectEntry3.getObjectEntryId());
					setType(Type.RESET_ASSET_PERMISSION_ACTION);
				}
			});

		resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				objectEntry3.getCompanyId(), objectEntry3.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry3.getObjectEntryId()),
				_cmsAdministratorRole.getRoleId());

		_assertHasActionIds(
			resourcePermission, ActionKeys.DELETE, ActionKeys.PERMISSIONS);

		ObjectEntryFolder objectEntryFolder3 =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				RandomTestUtil.randomString(), group.getGroupId(),
				group.getCreatorUserId(),
				objectEntryFolder2.getObjectEntryFolderId(), "",
				HashMapBuilder.put(
					LocaleUtil.ENGLISH, RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		_resourcePermissionLocalService.setResourcePermissions(
			objectEntryFolder3.getCompanyId(),
			objectEntryFolder3.getModelClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntryFolder3.getObjectEntryFolderId()),
			_cmsAdministratorRole.getRoleId(), new String[] {ActionKeys.VIEW});

		assetPermissionActionResource.postAssetPermission(
			new ResetAssetPermissionAction() {
				{
					setClassName(objectEntryFolder3.getModelClassName());
					setClassPK(objectEntryFolder3.getObjectEntryFolderId());
					setType(Type.RESET_ASSET_PERMISSION_ACTION);
				}
			});

		resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				objectEntryFolder3.getCompanyId(),
				objectEntryFolder3.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntryFolder3.getObjectEntryFolderId()),
				_cmsAdministratorRole.getRoleId());

		_assertHasActionIds(
			resourcePermission, ActionKeys.ADD_ENTRY,
			ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER, ActionKeys.SUBSCRIBE);
	}

	private static final String[] _ACTION_IDS = {
		ActionKeys.ADD_ENTRY, ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER,
		ActionKeys.DELETE, ActionKeys.PERMISSIONS, ActionKeys.SUBSCRIBE,
		ActionKeys.UPDATE, ActionKeys.VIEW
	};

	private Role _cmsAdministratorRole;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject(
		filter = "filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private FilterFactory<Predicate> _filterFactory;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private Role _userRole;

}