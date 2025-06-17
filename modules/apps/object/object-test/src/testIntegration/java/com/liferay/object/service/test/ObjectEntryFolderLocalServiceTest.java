/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.entry.folder.util.ObjectEntryFolderThreadLocal;
import com.liferay.object.exception.DuplicateObjectEntryFolderExternalReferenceCodeException;
import com.liferay.object.exception.ObjectEntryFolderNameException;
import com.liferay.object.exception.ObjectEntryFolderParentObjectEntryFolderIdException;
import com.liferay.object.exception.ObjectEntryFolderScopeException;
import com.liferay.object.exception.RequiredObjectEntryFolderException;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class ObjectEntryFolderLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_objectDefinition = _addObjectDefinition();
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testAddObjectEntryFolder() throws Exception {
		String externalReferenceCode = StringUtil.randomString();

		AssertUtils.assertFailure(
			DuplicateObjectEntryFolderExternalReferenceCodeException.class,
			"Duplicate object entry folder with external reference code " +
				externalReferenceCode,
			() -> {
				_addObjectEntryFolder(
					externalReferenceCode, _group.getGroupId(),
					StringUtil.randomString(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);
				_addObjectEntryFolder(
					externalReferenceCode, _group.getGroupId(),
					StringUtil.randomString(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);
			});

		String name = StringUtil.randomString();

		AssertUtils.assertFailure(
			ObjectEntryFolderNameException.MustNotBeDuplicate.class,
			"Duplicate name " + name,
			() -> {
				_addObjectEntryFolder(
					StringUtil.randomString(), _group.getGroupId(), name,
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);
				_addObjectEntryFolder(
					StringUtil.randomString(), _group.getGroupId(), name,
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);
			});

		AssertUtils.assertFailure(
			ObjectEntryFolderNameException.MustNotBeNull.class, "Name is null",
			() -> _addObjectEntryFolder(
				StringUtil.randomString(), _group.getGroupId(), null,
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT));
		AssertUtils.assertFailure(
			ObjectEntryFolderScopeException.class,
			StringBundler.concat(
				"Group ID ", TestPropsValues.getGroupId(),
				" does not match parent object entry folder group ID ",
				_group.getGroupId()),
			() -> {
				ObjectEntryFolder parentObjectEntryFolder =
					_addObjectEntryFolder(
						StringUtil.randomString(), _group.getGroupId(),
						StringUtil.randomString(),
						ObjectEntryFolderConstants.
							PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

				_addObjectEntryFolder(
					StringUtil.randomString(), TestPropsValues.getGroupId(),
					StringUtil.randomString(),
					parentObjectEntryFolder.getObjectEntryFolderId());
			});

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				StringUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				RandomTestUtil.randomString(), null, StringUtil.randomString(),
				new ServiceContext());

		AssertUtils.assertEquals(
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), objectEntryFolder.getName()
			).build(),
			objectEntryFolder.getLabelMap());

		Role role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				ObjectEntryFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntryFolder.getObjectEntryFolderId()),
				role.getRoleId(), ActionKeys.ADD_ENTRY));

		role = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(),
			DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				ObjectEntryFolder.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntryFolder.getObjectEntryFolderId()),
				role.getRoleId(), ActionKeys.ADD_ENTRY));
	}

	@Test
	public void testDeleteObjectEntryFolder() throws Exception {

		// Object entry folder

		ObjectEntryFolder objectEntryFolder1 = _addObjectEntryFolder(
			StringUtil.randomString(), _group.getGroupId(),
			StringUtil.randomString(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		ObjectEntryFolder objectEntryFolder2 = _addObjectEntryFolder(
			StringUtil.randomString(), _group.getGroupId(),
			StringUtil.randomString(),
			objectEntryFolder1.getObjectEntryFolderId());
		ObjectEntryFolder objectEntryFolder3 = _addObjectEntryFolder(
			StringUtil.randomString(), _group.getGroupId(),
			StringUtil.randomString(),
			objectEntryFolder1.getObjectEntryFolderId());

		ObjectEntry objectEntry = _addObjectEntry(
			objectEntryFolder1.getObjectEntryFolderId());

		_objectEntryFolderLocalService.deleteObjectEntryFolder(
			objectEntryFolder1.getObjectEntryFolderId());

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				objectEntry.getObjectEntryId()));
		Assert.assertNull(
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntryFolder1.getObjectEntryFolderId()));
		Assert.assertNull(
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntryFolder2.getObjectEntryFolderId()));
		Assert.assertNull(
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntryFolder3.getObjectEntryFolderId()));

		// System object entry folder

		String externalReferenceCode =
			ObjectEntryFolderConstants.
				EXTERNAL_REFERENCE_CODE_PREFIX_SYSTEM_OBJECT_ENTRY_FOLDER +
					StringUtil.randomString();

		AssertUtils.assertFailure(
			RequiredObjectEntryFolderException.class,
			"System object entry folder " + externalReferenceCode +
				" cannot be deleted",
			() -> {
				ObjectEntryFolder systemObjectEntryFolder =
					_addObjectEntryFolder(
						externalReferenceCode, _group.getGroupId(),
						StringUtil.randomString(),
						ObjectEntryFolderConstants.
							PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

				_objectEntryFolderLocalService.deleteObjectEntryFolder(
					systemObjectEntryFolder.getObjectEntryFolderId());
			});

		ObjectEntryFolder systemObjectEntryFolder = _addObjectEntryFolder(
			ObjectEntryFolderConstants.
				EXTERNAL_REFERENCE_CODE_PREFIX_SYSTEM_OBJECT_ENTRY_FOLDER +
					StringUtil.randomString(),
			_group.getGroupId(), StringUtil.randomString(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		try (SafeCloseable safeCloseable =
				ObjectEntryFolderThreadLocal.
					setForceDeleteSystemObjectEntryFolderWithSafeCloseable(
						true)) {

			_objectEntryFolderLocalService.deleteObjectEntryFolder(
				systemObjectEntryFolder.getObjectEntryFolderId());
		}

		Assert.assertNull(
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				systemObjectEntryFolder.getObjectEntryFolderId()));
	}

	@Test
	public void testUpdateObjectEntryFolder() throws Exception {
		String name = StringUtil.randomString();

		AssertUtils.assertFailure(
			ObjectEntryFolderNameException.MustNotBeDuplicate.class,
			"Duplicate name " + name,
			() -> {
				_addObjectEntryFolder(
					StringUtil.randomString(), _group.getGroupId(), name,
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

				ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder(
					StringUtil.randomString(), _group.getGroupId(),
					StringUtil.randomString(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

				_objectEntryFolderLocalService.updateObjectEntryFolder(
					TestPropsValues.getUserId(),
					objectEntryFolder.getObjectEntryFolderId(),
					objectEntryFolder.getParentObjectEntryFolderId(),
					objectEntryFolder.getDescription(),
					objectEntryFolder.getLabelMap(), name,
					new ServiceContext());
			});

		AssertUtils.assertFailure(
			ObjectEntryFolderNameException.MustNotBeNull.class, "Name is null",
			() -> {
				ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder(
					StringUtil.randomString(), _group.getGroupId(),
					StringUtil.randomString(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

				_objectEntryFolderLocalService.updateObjectEntryFolder(
					TestPropsValues.getUserId(),
					objectEntryFolder.getObjectEntryFolderId(),
					objectEntryFolder.getParentObjectEntryFolderId(),
					objectEntryFolder.getDescription(),
					objectEntryFolder.getLabelMap(), null,
					new ServiceContext());
			});
		AssertUtils.assertFailure(
			ObjectEntryFolderScopeException.class,
			StringBundler.concat(
				"Group ID ", _group.getGroupId(),
				" does not match parent object entry folder group ID ",
				TestPropsValues.getGroupId()),
			() -> {
				ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder(
					StringUtil.randomString(), _group.getGroupId(),
					StringUtil.randomString(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

				ObjectEntryFolder parentObjectEntryFolder =
					_addObjectEntryFolder(
						StringUtil.randomString(), TestPropsValues.getGroupId(),
						StringUtil.randomString(),
						ObjectEntryFolderConstants.
							PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

				_objectEntryFolderLocalService.updateObjectEntryFolder(
					TestPropsValues.getUserId(),
					objectEntryFolder.getObjectEntryFolderId(),
					parentObjectEntryFolder.getObjectEntryFolderId(),
					objectEntryFolder.getDescription(),
					objectEntryFolder.getLabelMap(),
					objectEntryFolder.getName(), new ServiceContext());
			});

		ObjectEntryFolder objectEntryFolder1 = _addObjectEntryFolder(
			StringUtil.randomString(), _group.getGroupId(),
			StringUtil.randomString(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);

		Assert.assertNotEquals(
			objectEntryFolder1.getName(),
			objectEntryFolder1.getLabel(LocaleUtil.getSiteDefault()));

		ObjectEntryFolder objectEntryFolder2 =
			_objectEntryFolderLocalService.updateObjectEntryFolder(
				TestPropsValues.getUserId(),
				objectEntryFolder1.getObjectEntryFolderId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				objectEntryFolder1.getDescription(), null,
				objectEntryFolder1.getName(), new ServiceContext());

		AssertUtils.assertEquals(
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), objectEntryFolder2.getName()
			).build(),
			objectEntryFolder2.getLabelMap());

		AssertUtils.assertFailure(
			ObjectEntryFolderParentObjectEntryFolderIdException.class,
			StringBundler.concat(
				"Object entry folder ",
				objectEntryFolder1.getObjectEntryFolderId(),
				" cannot have one of its children or itself as a parent"),
			() -> _objectEntryFolderLocalService.updateObjectEntryFolder(
				TestPropsValues.getUserId(),
				objectEntryFolder1.getObjectEntryFolderId(),
				objectEntryFolder1.getObjectEntryFolderId(),
				objectEntryFolder1.getDescription(),
				objectEntryFolder1.getLabelMap(), objectEntryFolder1.getName(),
				new ServiceContext()));
		AssertUtils.assertFailure(
			ObjectEntryFolderParentObjectEntryFolderIdException.class,
			StringBundler.concat(
				"Object entry folder ",
				objectEntryFolder1.getObjectEntryFolderId(),
				" cannot have one of its children or itself as a parent"),
			() -> {
				ObjectEntryFolder objectEntryFolder = _addObjectEntryFolder(
					StringUtil.randomString(), _group.getGroupId(),
					StringUtil.randomString(),
					objectEntryFolder1.getObjectEntryFolderId());

				_objectEntryFolderLocalService.updateObjectEntryFolder(
					TestPropsValues.getUserId(),
					objectEntryFolder1.getObjectEntryFolderId(),
					objectEntryFolder.getObjectEntryFolderId(),
					objectEntryFolder1.getDescription(),
					objectEntryFolder1.getLabelMap(),
					objectEntryFolder1.getName(), new ServiceContext());
			});
	}

	private ObjectDefinition _addObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, false,
				false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(StringUtil.randomString()),
				"A" + StringUtil.randomString(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_SITE,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "fieldName")));

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private ObjectEntry _addObjectEntry(long objectEntryFolderId)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_objectDefinition.getObjectDefinitionId(), objectEntryFolderId,
			null,
			HashMapBuilder.<String, Serializable>put(
				"fieldName", StringUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private ObjectEntryFolder _addObjectEntryFolder(
			String externalReferenceCode, long groupId, String name,
			long parentObjectEntryFolderId)
		throws Exception {

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			externalReferenceCode, TestPropsValues.getUserId(), groupId,
			parentObjectEntryFolderId, RandomTestUtil.randomString(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			name, ServiceContextTestUtil.getServiceContext());
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

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

}