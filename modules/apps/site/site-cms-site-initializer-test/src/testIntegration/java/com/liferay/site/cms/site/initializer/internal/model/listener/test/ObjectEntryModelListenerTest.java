/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.util.CMSDefaultPermissionUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

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
		_cmsAdministratorRole = _getOrAddCMSAdministratorRole(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId());
		_userRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.USER);

		if (_isCMSSiteInitialized()) {
			return;
		}

		// These tests require the instance to be created with the feature
		// flag LPD-17564 enabled. On CI, feature flags are enabled on
		// demand for each test, but not during instance initialization.
		// Until the feature flag LPD-17564 is removed, run the batch
		// engine unit processor manually so that the object definitions
		// are created.

		Bundle testBundle = FrameworkUtil.getBundle(
			GroupModelListenerTest.class);

		BundleContext bundleContext = testBundle.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			if (!Objects.equals(
					bundle.getSymbolicName(),
					"com.liferay.site.initializer.cms")) {

				continue;
			}

			_deleteFile(bundle, "00.list.type.definition");
			_deleteFile(bundle, "01.object.folder");
			_deleteFile(bundle, "02.object.definition");

			CompletableFuture<Void> completableFuture =
				_batchEngineUnitProcessor.processBatchEngineUnits(
					_batchEngineUnitReader.getBatchEngineUnits(bundle));

			completableFuture.join();
		}
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testAddObjectEntry() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

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
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		JSONObject jsonObject = CMSDefaultPermissionUtil.getJSONObject(
			objectEntryFolder2.getCompanyId(), objectEntryFolder2.getUserId(),
			objectEntryFolder2.getExternalReferenceCode(),
			objectEntryFolder2.getModelClassName(), _filterFactory);

		String randomString = RandomTestUtil.randomString();

		jsonObject.put(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
			JSONUtil.put(
				RoleConstants.CMS_ADMINISTRATOR,
				JSONUtil.putAll(
					ActionKeys.UPDATE, ActionKeys.VIEW, randomString)
			).put(
				RoleConstants.USER, JSONUtil.putAll(ActionKeys.VIEW)
			));

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

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_BASIC_WEB_CONTENT", objectEntryFolder2.getCompanyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAttribute(
			"friendlyUrlMap", new HashMap<String, String>());

		ObjectEntry objectEntry2 = _objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), depotEntry.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder2.getObjectEntryFolderId(), "en_US",
			HashMapBuilder.<String, Serializable>put(
				"text", RandomTestUtil.randomString()
			).build(),
			serviceContext);

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				objectEntry2.getCompanyId(), objectEntry2.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry2.getObjectEntryId()),
				_cmsAdministratorRole.getRoleId());

		Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.DELETE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.UPDATE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.VIEW));
		Assert.assertFalse(resourcePermission.hasActionId(randomString));

		resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				objectEntry2.getCompanyId(), objectEntry2.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry2.getObjectEntryId()),
				_userRole.getRoleId());

		Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.DELETE));
		Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.UPDATE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.VIEW));

		objectEntryFolder1 =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
					group.getGroupId(), depotEntry.getCompanyId());

		objectEntryFolder2 =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				RandomTestUtil.randomString(), group.getGroupId(),
				group.getCreatorUserId(),
				objectEntryFolder1.getObjectEntryFolderId(), "",
				HashMapBuilder.put(
					LocaleUtil.ENGLISH, RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		jsonObject = CMSDefaultPermissionUtil.getJSONObject(
			objectEntryFolder2.getCompanyId(), objectEntryFolder2.getUserId(),
			objectEntryFolder2.getExternalReferenceCode(),
			objectEntryFolder2.getModelClassName(), _filterFactory);

		jsonObject.put(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
			JSONUtil.put(
				RoleConstants.CMS_ADMINISTRATOR,
				JSONUtil.putAll(
					ActionKeys.DELETE, ActionKeys.VIEW, randomString)
			).put(
				RoleConstants.USER, JSONUtil.putAll(ActionKeys.UPDATE)
			));

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

		objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_BASIC_DOCUMENT", objectEntryFolder2.getCompanyId());

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), depotEntry.getGroupId(),
			depotEntry.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, null,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new ByteArrayInputStream(TestDataConstants.TEST_BYTE_ARRAY),
			TestDataConstants.TEST_BYTE_ARRAY.length, null, null, null,
			ServiceContextTestUtil.getServiceContext());

		objectEntry2 = _objectEntryLocalService.addObjectEntry(
			depotEntry.getGroupId(), depotEntry.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder2.getObjectEntryFolderId(), "en_US",
			HashMapBuilder.<String, Serializable>put(
				"file", dlFileEntry.getFileEntryId()
			).build(),
			serviceContext);

		resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				objectEntry2.getCompanyId(), objectEntry2.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry2.getObjectEntryId()),
				_cmsAdministratorRole.getRoleId());

		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.DELETE));
		Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.UPDATE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.VIEW));
		Assert.assertFalse(resourcePermission.hasActionId(randomString));

		resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				objectEntry2.getCompanyId(), objectEntry2.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry2.getObjectEntryId()),
				_userRole.getRoleId());

		Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.DELETE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.UPDATE));
		Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.VIEW));
	}

	private void _deleteFile(Bundle bundle, String fileName) {
		File file = bundle.getDataFile(
			".com.liferay.site.initializer.cms.internal.batch." + fileName +
				".batch.engine.data.json.0.processed");

		if ((file != null) && file.exists()) {
			file.delete();
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

	private boolean _isCMSSiteInitialized() throws Exception {
		ObjectFolder objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES,
				TestPropsValues.getCompanyId());

		if (objectFolder != null) {
			return true;
		}

		return false;
	}

	@Inject
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	@Inject
	private BatchEngineUnitReader _batchEngineUnitReader;

	private Role _cmsAdministratorRole;

	@Inject
	private CompanyLocalService _companyLocalService;

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
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private Role _userRole;

}