/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.util.CMSDefaultPermissionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class DepotEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);
	}

	@Test
	public void testAddDepotEntry() throws Exception {
		_assertCMSDefaultPermissions(
			_addDepotEntry(DepotConstants.TYPE_ASSET_LIBRARY));
		_assertCMSDefaultPermissions(_addDepotEntry(DepotConstants.TYPE_SPACE));
		_assertCMSDefaultPermissions(
			_addStagedDepotEntry(DepotConstants.TYPE_ASSET_LIBRARY));
		_assertCMSDefaultPermissions(
			_addStagedDepotEntry(DepotConstants.TYPE_SPACE));

		_assertObjectEntryFolders(
			_addDepotEntry(DepotConstants.TYPE_ASSET_LIBRARY), 0);
		_assertObjectEntryFolders(_addDepotEntry(DepotConstants.TYPE_SPACE), 2);
		_assertObjectEntryFolders(
			_addStagedDepotEntry(DepotConstants.TYPE_ASSET_LIBRARY), 0);
		_assertObjectEntryFolders(
			_addStagedDepotEntry(DepotConstants.TYPE_SPACE), 2);
	}

	@Test
	public void testDeleteDepotEntry() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		Group group = depotEntry.getGroup();

		_depotEntryLocalService.deleteDepotEntry(depotEntry.getDepotEntryId());

		Assert.assertNull(
			CMSDefaultPermissionUtil.fetchObjectEntry(
				depotEntry.getCompanyId(), depotEntry.getUserId(),
				group.getExternalReferenceCode(),
				depotEntry.getModelClassName(), _filterFactory));
		Assert.assertEquals(
			0,
			_objectEntryFolderLocalService.getObjectEntryFoldersCount(
				depotEntry.getGroupId(), depotEntry.getCompanyId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT));
	}

	private DepotEntry _addDepotEntry(int depotType) throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			depotType, ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private DepotEntry _addStagedDepotEntry(int depotType) throws Exception {
		Group group = GroupTestUtil.addGroup();

		group.setType(GroupConstants.TYPE_DEPOT);

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.put(
			"depotEntryType", String.valueOf(depotType));

		group.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		group = _groupLocalService.updateGroup(group);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAttribute("staging", Boolean.TRUE);

		return _depotEntryLocalService.addDepotEntry(group, serviceContext);
	}

	private void _assertCMSDefaultPermissions(DepotEntry depotEntry)
		throws Exception {

		Group group = depotEntry.getGroup();

		JSONObject jsonObject1 = CMSDefaultPermissionUtil.getJSONObject(
			depotEntry.getCompanyId(), depotEntry.getUserId(),
			group.getExternalReferenceCode(), depotEntry.getModelClassName(),
			_filterFactory);

		if (depotEntry.getType() == DepotConstants.TYPE_ASSET_LIBRARY) {
			Assert.assertTrue(
				jsonObject1.toString(), JSONUtil.isEmpty(jsonObject1));

			return;
		}

		JSONObject jsonObject2 = jsonObject1.getJSONObject(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", TestPropsValues.getCompanyId());

		String[] objectEntryActionIds = {
			ActionKeys.ADD_DISCUSSION, ActionKeys.DELETE,
			ActionKeys.DELETE_DISCUSSION, ActionKeys.DOWNLOAD,
			ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
			ActionKeys.UPDATE_DISCUSSION, ActionKeys.VIEW
		};

		Assert.assertArrayEquals(
			objectEntryActionIds,
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(
					DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR)));
		Assert.assertArrayEquals(
			objectEntryActionIds,
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(
					DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER)));

		String[] assetLibraryMemberObjectEntryActionIds = {
			ActionKeys.ADD_DISCUSSION, ActionKeys.DOWNLOAD, ActionKeys.VIEW
		};

		Assert.assertArrayEquals(
			assetLibraryMemberObjectEntryActionIds,
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(
					DepotRolesConstants.ASSET_LIBRARY_MEMBER)));

		Assert.assertArrayEquals(
			TransformUtil.transformToArray(
				_resourceActionLocalService.getResourceActions(
					objectDefinition.getClassName()),
				ResourceAction::getActionId, String.class),
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(RoleConstants.CMS_ADMINISTRATOR)));
		Assert.assertArrayEquals(
			TransformUtil.transformToArray(
				_resourceActionLocalService.getResourceActions(
					objectDefinition.getClassName()),
				ResourceAction::getActionId, String.class),
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(RoleConstants.OWNER)));
		Assert.assertArrayEquals(
			new String[] {ActionKeys.VIEW},
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(RoleConstants.USER)));

		jsonObject2 = jsonObject1.getJSONObject(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES);

		objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", TestPropsValues.getCompanyId());

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFieldsByBusinessType(
				objectDefinition.getObjectDefinitionId(),
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT);

		if (ListUtil.isNotEmpty(objectFields)) {
			ObjectField objectField = objectFields.get(0);

			assetLibraryMemberObjectEntryActionIds = ArrayUtil.append(
				assetLibraryMemberObjectEntryActionIds,
				objectField.getAttachmentDownloadActionKey());
			objectEntryActionIds = ArrayUtil.append(
				objectEntryActionIds,
				objectField.getAttachmentDownloadActionKey());
		}

		Assert.assertArrayEquals(
			objectEntryActionIds,
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(
					DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR)));
		Assert.assertArrayEquals(
			objectEntryActionIds,
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(
					DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER)));
		Assert.assertArrayEquals(
			assetLibraryMemberObjectEntryActionIds,
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(
					DepotRolesConstants.ASSET_LIBRARY_MEMBER)));
		Assert.assertArrayEquals(
			TransformUtil.transformToArray(
				_resourceActionLocalService.getResourceActions(
					objectDefinition.getClassName()),
				ResourceAction::getActionId, String.class),
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(RoleConstants.CMS_ADMINISTRATOR)));
		Assert.assertArrayEquals(
			TransformUtil.transformToArray(
				_resourceActionLocalService.getResourceActions(
					objectDefinition.getClassName()),
				ResourceAction::getActionId, String.class),
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(RoleConstants.OWNER)));
		Assert.assertArrayEquals(
			new String[] {ActionKeys.VIEW},
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(RoleConstants.USER)));

		jsonObject2 = jsonObject1.getJSONObject("OBJECT_ENTRY_FOLDERS");

		Assert.assertArrayEquals(
			new String[] {
				ActionKeys.ADD_ENTRY, ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER,
				ActionKeys.DELETE, ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
				ActionKeys.SUBSCRIBE, ActionKeys.VIEW
			},
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(
					DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR)));
		Assert.assertArrayEquals(
			new String[] {
				ActionKeys.ADD_ENTRY, ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER,
				ActionKeys.DELETE, ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
				ActionKeys.SUBSCRIBE, ActionKeys.VIEW
			},
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(
					DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER)));
		Assert.assertArrayEquals(
			new String[] {
				ActionKeys.ADD_DISCUSSION, ActionKeys.VIEW, ActionKeys.SUBSCRIBE
			},
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(
					DepotRolesConstants.ASSET_LIBRARY_MEMBER)));
		Assert.assertArrayEquals(
			TransformUtil.transformToArray(
				_resourceActionLocalService.getResourceActions(
					ObjectEntryFolder.class.getName()),
				ResourceAction::getActionId, String.class),
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(RoleConstants.CMS_ADMINISTRATOR)));
		Assert.assertArrayEquals(
			TransformUtil.transformToArray(
				_resourceActionLocalService.getResourceActions(
					ObjectEntryFolder.class.getName()),
				ResourceAction::getActionId, String.class),
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(RoleConstants.OWNER)));
		Assert.assertArrayEquals(
			new String[] {ActionKeys.VIEW, ActionKeys.SUBSCRIBE},
			JSONUtil.toStringArray(
				jsonObject2.getJSONArray(RoleConstants.USER)));
	}

	private void _assertObjectEntryFolders(
			DepotEntry depotEntry, int expectedObjectEntryFoldersCount)
		throws Exception {

		Assert.assertEquals(
			expectedObjectEntryFoldersCount,
			_objectEntryFolderLocalService.getObjectEntryFoldersCount(
				depotEntry.getGroupId(), depotEntry.getCompanyId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT));

		if (expectedObjectEntryFoldersCount > 0) {
			AssertUtils.assertEquals(
				Arrays.asList("Contents", "Files"),
				ListUtil.sort(
					ListUtil.toList(
						_objectEntryFolderLocalService.getObjectEntryFolders(
							depotEntry.getGroupId(), depotEntry.getCompanyId(),
							ObjectEntryFolderConstants.
								PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
							QueryUtil.ALL_POS, QueryUtil.ALL_POS),
						ObjectEntryFolder::getName)));

			Group group = _groupLocalService.fetchGroup(
				depotEntry.getGroupId());

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.
					getObjectDefinitionByExternalReferenceCode(
						"L_CMS_BASIC_DOCUMENT", group.getCompanyId());

			Repository repository = _portletFileRepository.getPortletRepository(
				group.getGroupId(), objectDefinition.getPortletId());

			Assert.assertNotNull(
				_dlFolderLocalService.fetchFolder(
					group.getGroupId(), repository.getDlFolderId(),
					String.valueOf(TestPropsValues.getUserId())));

			Assert.assertNotNull(
				_repositoryLocalService.fetchRepository(
					group.getGroupId(), TempFileEntryUtil.class.getName(),
					TempFileEntryUtil.class.getName()));
		}
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	@Inject(
		filter = "filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private FilterFactory<Predicate> _filterFactory;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private RepositoryLocalService _repositoryLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

}