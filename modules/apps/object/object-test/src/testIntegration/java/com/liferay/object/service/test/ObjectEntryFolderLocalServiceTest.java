/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.entry.folder.util.ObjectEntryFolderThreadLocal;
import com.liferay.object.exception.DuplicateObjectEntryFolderExternalReferenceCodeException;
import com.liferay.object.exception.NoSuchObjectEntryFolderException;
import com.liferay.object.exception.ObjectEntryFolderNameException;
import com.liferay.object.exception.ObjectEntryFolderParentObjectEntryFolderIdException;
import com.liferay.object.exception.ObjectEntryFolderScopeException;
import com.liferay.object.exception.RequiredObjectEntryFolderException;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectEntryFolderTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalService;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class ObjectEntryFolderLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_objectDefinition = _addObjectDefinition();
	}

	@After
	public void tearDown() throws Exception {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-32050")}
	)
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
				StringUtil.randomString(), _group.getGroupId(),
				TestPropsValues.getUserId(),
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

		// Model listeners

		AtomicInteger atomicInteger = new AtomicInteger(0);

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration =
			(ServiceRegistration)bundleContext.registerService(
				ModelListener.class,
				new BaseModelListener<ObjectEntryFolder>() {

					@Override
					public Class<?> getModelClass() {
						return ObjectEntryFolder.class;
					}

					@Override
					public void onAfterRemove(
						ObjectEntryFolder objectEntryFolder) {

						atomicInteger.incrementAndGet();
					}

				},
				null);

		_objectEntryFolderLocalService.deleteObjectEntryFolder(
			_addObjectEntryFolder(
				StringUtil.randomString(), _group.getGroupId(),
				StringUtil.randomString(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT));

		Assert.assertEquals(1, atomicInteger.get());

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
	@TestInfo("LPD-56833")
	public void testGetOrAddEmptyObjectEntryFolder() throws Exception {

		// Lazy referencing disabled

		String externalReferenceCode = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			NoSuchObjectEntryFolderException.class,
			String.format(
				"No ObjectEntryFolder exists with the key {" +
					"externalReferenceCode=%s, groupId=%s, companyId=%s}",
				externalReferenceCode, TestPropsValues.getGroupId(),
				TestPropsValues.getCompanyId()),
			() -> _objectEntryFolderLocalService.getOrAddEmptyObjectEntryFolder(
				externalReferenceCode, TestPropsValues.getGroupId(),
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				ServiceContextTestUtil.getServiceContext()));

		// Lazy referencing enabled

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			long exportImportConfigurationId = RandomTestUtil.randomLong();

			ExportImportThreadLocal.setExportImportConfigurationId(
				exportImportConfigurationId);

			ObjectEntryFolder objectEntryFolder =
				_objectEntryFolderLocalService.getOrAddEmptyObjectEntryFolder(
					externalReferenceCode, TestPropsValues.getGroupId(),
					TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
					ServiceContextTestUtil.getServiceContext());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, objectEntryFolder.getStatus());

			List<ExportImportReportEntry> exportImportReportEntries =
				_exportImportReportEntryLocalService.
					getExportImportReportEntries(
						TestPropsValues.getCompanyId(),
						exportImportConfigurationId);

			Assert.assertEquals(
				exportImportReportEntries.toString(), 1,
				exportImportReportEntries.size());
			Assert.assertTrue(
				ListUtil.exists(
					exportImportReportEntries,
					exportImportReportEntry ->
						Objects.equals(
							exportImportReportEntry.
								getClassExternalReferenceCode(),
							externalReferenceCode) &&
						(exportImportReportEntry.getType() ==
							ExportImportReportEntryConstants.TYPE_EMPTY)));

			objectEntryFolder =
				_objectEntryFolderLocalService.updateObjectEntryFolder(
					objectEntryFolder.getUserId(),
					objectEntryFolder.getObjectEntryFolderId(),
					objectEntryFolder.getParentObjectEntryFolderId(),
					objectEntryFolder.getDescription(),
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.randomString(),
					ServiceContextTestUtil.getServiceContext());

			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED,
				objectEntryFolder.getStatus());
		}
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-32050")}
	)
	@Test
	public void testMoveObjectEntryFolderToTrash() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ObjectEntryFolder objectEntryFolder1 =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(group.getGroupId());

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
			objectEntryFolder1.getObjectEntryFolderId(),
			Collections.emptyMap());

		ObjectEntryFolder objectEntryFolder2 =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				group.getGroupId(),
				objectEntryFolder1.getObjectEntryFolderId());

		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
			objectEntryFolder2.getObjectEntryFolderId(),
			Collections.emptyMap());

		ObjectEntryFolder objectEntryFolder3 =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				group.getGroupId(),
				objectEntryFolder2.getObjectEntryFolderId());

		ObjectEntry objectEntry3 = ObjectEntryTestUtil.addObjectEntry(
			group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
			objectEntryFolder3.getObjectEntryFolderId(),
			Collections.emptyMap());

		objectEntryFolder2 =
			_objectEntryFolderLocalService.moveObjectEntryFolderToTrash(
				TestPropsValues.getUserId(), objectEntryFolder2,
				ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			objectEntryFolder2.getParentObjectEntryFolderId());

		TrashEntry trashEntry = _trashEntryLocalService.fetchEntry(
			ObjectEntryFolder.class.getName(),
			objectEntryFolder2.getObjectEntryFolderId());

		Assert.assertEquals(
			objectEntryFolder1.getObjectEntryFolderId(),
			GetterUtil.getLong(
				trashEntry.getTypeSettingsProperty(
					"parentObjectEntryFolderId")));

		_assertObjectEntryFolderStatus(
			WorkflowConstants.STATUS_APPROVED,
			objectEntryFolder1.getObjectEntryFolderId());
		_assertObjectEntryFolderStatus(
			WorkflowConstants.STATUS_IN_TRASH,
			objectEntryFolder2.getObjectEntryFolderId());
		_assertObjectEntryFolderStatus(
			WorkflowConstants.STATUS_IN_TRASH,
			objectEntryFolder3.getObjectEntryFolderId());
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_APPROVED, objectEntry1.getObjectEntryId());
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_IN_TRASH, objectEntry2.getObjectEntryId());
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_IN_TRASH, objectEntry3.getObjectEntryId());

		_objectEntryFolderLocalService.deleteObjectEntryFolder(
			objectEntryFolder2.getObjectEntryFolderId());

		Assert.assertNull(
			_trashEntryLocalService.fetchEntry(
				ObjectEntryFolder.class.getName(),
				objectEntryFolder2.getObjectEntryFolderId()));
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-32050")}
	)
	@Test
	public void testRestoreObjectEntryFolderFromTrash() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ObjectEntryFolder objectEntryFolder1 =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(group.getGroupId());

		ObjectEntryFolder objectEntryFolder2 =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				group.getGroupId(),
				objectEntryFolder1.getObjectEntryFolderId());

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			group.getGroupId(), _objectDefinition.getObjectDefinitionId(),
			objectEntryFolder2.getObjectEntryFolderId(),
			Collections.emptyMap());

		objectEntryFolder2 =
			_objectEntryFolderLocalService.restoreObjectEntryFolderFromTrash(
				TestPropsValues.getUserId(),
				_objectEntryFolderLocalService.moveObjectEntryFolderToTrash(
					TestPropsValues.getUserId(), objectEntryFolder2,
					ServiceContextTestUtil.getServiceContext()),
				ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			objectEntryFolder1.getObjectEntryFolderId(),
			objectEntryFolder2.getParentObjectEntryFolderId());

		_assertObjectEntryFolderStatus(
			WorkflowConstants.STATUS_APPROVED,
			objectEntryFolder1.getObjectEntryFolderId());
		_assertObjectEntryFolderStatus(
			WorkflowConstants.STATUS_APPROVED,
			objectEntryFolder2.getObjectEntryFolderId());
		_assertObjectEntryStatus(
			WorkflowConstants.STATUS_APPROVED, objectEntry1.getObjectEntryId());

		objectEntryFolder2 =
			_objectEntryFolderLocalService.moveObjectEntryFolderToTrash(
				TestPropsValues.getUserId(), objectEntryFolder2,
				ServiceContextTestUtil.getServiceContext());

		_objectEntryFolderLocalService.deleteObjectEntryFolder(
			objectEntryFolder1.getObjectEntryFolderId());

		objectEntryFolder2 =
			_objectEntryFolderLocalService.restoreObjectEntryFolderFromTrash(
				TestPropsValues.getUserId(), objectEntryFolder2,
				ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			objectEntryFolder2.getParentObjectEntryFolderId());

		Assert.assertNull(
			_trashEntryLocalService.fetchEntry(
				ObjectEntryFolder.class.getName(),
				objectEntryFolder2.getObjectEntryFolderId()));
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
				null, TestPropsValues.getUserId(), 0, null, false, true, false,
				false, true, false, false, false, false, null,
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
						RandomTestUtil.randomString(), "fieldName")),
				Collections.emptyList());

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private ObjectEntry _addObjectEntry(long objectEntryFolderId)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
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
			externalReferenceCode, groupId, TestPropsValues.getUserId(),
			parentObjectEntryFolderId, RandomTestUtil.randomString(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			name, ServiceContextTestUtil.getServiceContext());
	}

	private void _assertObjectEntryFolderStatus(
		int expectedStatus, long objectEntryFolderId) {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntryFolderId);

		Assert.assertEquals(expectedStatus, objectEntryFolder.getStatus());
	}

	private void _assertObjectEntryStatus(
		int expectedStatus, long objectEntryId) {

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			objectEntryId);

		Assert.assertEquals(expectedStatus, objectEntry.getStatus());
	}

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

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

	private ServiceRegistration<ModelListener<ObjectEntryFolder>>
		_serviceRegistration;

	@Inject
	private TrashEntryLocalService _trashEntryLocalService;

}