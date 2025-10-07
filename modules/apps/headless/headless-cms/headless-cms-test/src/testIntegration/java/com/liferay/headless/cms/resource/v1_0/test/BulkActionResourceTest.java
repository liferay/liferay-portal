/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.headless.cms.client.dto.v1_0.BulkAction;
import com.liferay.headless.cms.client.dto.v1_0.BulkActionItem;
import com.liferay.headless.cms.client.dto.v1_0.BulkActionTask;
import com.liferay.headless.cms.client.dto.v1_0.DefaultPermissionBulkAction;
import com.liferay.headless.cms.client.dto.v1_0.DeleteBulkAction;
import com.liferay.headless.cms.client.dto.v1_0.KeywordBulkAction;
import com.liferay.headless.cms.client.dto.v1_0.PermissionBulkAction;
import com.liferay.headless.cms.client.dto.v1_0.TaxonomyCategoryBulkAction;
import com.liferay.headless.cms.client.pagination.Page;
import com.liferay.headless.cms.client.pagination.Pagination;
import com.liferay.headless.cms.client.permission.Permission;
import com.liferay.headless.cms.client.problem.Problem;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;
import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
 * @author Crescenzo Rega
 */
@FeatureFlags(
	featureFlags = {
		@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-21926"),
		@FeatureFlag("LPD-31149"), @FeatureFlag("LPD-34594"),
		@FeatureFlag("LPS-179669")
	}
)
@RunWith(Arquillian.class)
public class BulkActionResourceTest extends BaseBulkActionResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		if (!_isCMSSiteInitialized()) {

			// These tests require the instance to be created with the feature
			// flag LPD-17564 enabled. On CI, feature flags are enabled on
			// demand for each test, but not during instance initialization.
			// Until the feature flag LPD-17564 is removed, run the batch
			// engine unit processor manually so that the object definitions
			// are created.

			Bundle testBundle = FrameworkUtil.getBundle(
				BulkActionResourceTest.class);

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

		_basicWebContentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_BASIC_WEB_CONTENT", testCompany.getCompanyId());
		_bulkActionTaskObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_BULK_ACTION_TASK", testCompany.getCompanyId());

		_depotEntry1 = _addDepotEntry(true);
		_depotEntry2 = _addDepotEntry(false);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_depotEntry1.getGroupId(), TestPropsValues.getUserId());

		_importTaskResource = ImportTaskResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).header(
			HttpHeaders.ACCEPT, ContentTypes.APPLICATION_JSON
		).header(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON
		).parameter(
			"siteId", String.valueOf(TestPropsValues.getGroupId())
		).build();

		_user = UserTestUtil.addUser();
	}

	@Override
	@Test
	public void testPostBulkAction() throws Exception {
		_testPostBulkActionWithTypeDefaultPermission();
		_testPostBulkActionWithTypeDelete();
		_testPostBulkActionWithTypeKeyword();
		_testPostBulkActionWithTypePermission();
		_testPostBulkActionWithTypeTaxonomyCategory();
	}

	@Override
	@Test
	public void testPostBulkActionItemPreviewPage() throws Exception {
		_testPostBulkActionItemPreviewPage(_depotEntry1, "RECYCLE_BIN");
		_testPostBulkActionItemPreviewPage(_depotEntry2, "PERMANENT_DELETION");
	}

	private DepotEntry _addDepotEntry(boolean trashEnabled) throws Exception {
		Map<Locale, String> nameMap = Collections.singletonMap(
			LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext = new ServiceContext() {
			{
				setCompanyId(testGroup.getCompanyId());
				setUserId(TestPropsValues.getUserId());
			}
		};

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			nameMap, null, DepotConstants.TYPE_ASSET_LIBRARY, serviceContext);

		return _depotEntryLocalService.updateDepotEntry(
			depotEntry.getDepotEntryId(), nameMap, null, Collections.emptyMap(),
			UnicodePropertiesBuilder.put(
				"trashEnabled", trashEnabled
			).build(),
			serviceContext);
	}

	private ObjectEntry _addObjectEntry(long groupId, long objectEntryFolderId)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			groupId, _user.getUserId(),
			_basicWebContentObjectDefinition.getObjectDefinitionId(),
			objectEntryFolderId, _LANGUAGE_ID,
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.<String, Serializable>put(
					_LANGUAGE_ID, RandomTestUtil.randomString()
				).build()
			).build(),
			_serviceContext);
	}

	private ObjectEntryFolder _addObjectEntryFolder(
			long groupId, long parentObjectEntryFolderId)
		throws Exception {

		String name = RandomTestUtil.randomString();

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			null, groupId, _user.getUserId(), parentObjectEntryFolderId, null,
			HashMapBuilder.put(
				LocaleUtil.US, name
			).build(),
			name, _serviceContext);
	}

	private void _assertBulkActionItem(
		BulkActionItem bulkActionItem, long expectedClassPK,
		String expectedDeletionType, Long expectedItemsCount,
		String expectedMimeType, String expectedName, String expectedType,
		Long expectedUsages) {

		Map<String, Object> attributes = bulkActionItem.getAttributes();

		Assert.assertEquals(
			expectedDeletionType, attributes.get("deletionType"));

		Object itemsCount = attributes.get("itemsCount");

		if (expectedItemsCount == null) {
			Assert.assertNull(expectedItemsCount);
		}
		else {
			Assert.assertEquals(
				expectedItemsCount.longValue(),
				GetterUtil.getLong(itemsCount, -1));
		}

		Assert.assertEquals(expectedMimeType, attributes.get("mimeType"));
		Assert.assertEquals(expectedType, attributes.get("type"));

		Object usages = attributes.get("usages");

		if (expectedUsages == null) {
			Assert.assertNull(usages);
		}
		else {
			Assert.assertEquals(
				expectedUsages.longValue(), GetterUtil.getLong(usages, -1));
		}

		Assert.assertEquals(expectedClassPK, (long)bulkActionItem.getClassPK());
		Assert.assertEquals(expectedName, bulkActionItem.getName());
	}

	private void _deleteFile(Bundle bundle, String fileName) {
		File file = bundle.getDataFile(
			".com.liferay.site.initializer.cms.internal.batch." + fileName +
				".batch.engine.data.json.0.processed");

		if ((file != null) && file.exists()) {
			file.delete();
		}
	}

	private JSONObject _getDefaultPermissionsJSONObject(
			ObjectDefinition objectDefinition,
			ObjectEntryFolder objectEntryFolder)
		throws Exception {

		Predicate predicate = _filterFactory.create(
			StringBundler.concat(
				"(classExternalReferenceCode eq '",
				objectEntryFolder.getExternalReferenceCode(),
				"') and (className eq '", objectEntryFolder.getModelClassName(),
				"')"),
			objectDefinition);

		List<Long> primaryKeys = _objectEntryLocalService.getPrimaryKeys(
			new Long[0], _depotEntry2.getCompanyId(),
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), predicate, false, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			primaryKeys.get(0));

		Map<String, Serializable> values = objectEntry.getValues();

		return JSONFactoryUtil.createJSONObject(
			String.valueOf(values.get("defaultPermissions")));
	}

	private Map<String, Serializable> _getImportTaskValues(
			BulkActionTask bulkActionTask)
		throws Exception {

		ObjectEntry bulkActionTaskObjectEntry =
			_objectEntryLocalService.getObjectEntry(bulkActionTask.getId());

		List<ObjectEntry> objectEntries = ListUtil.filter(
			_objectEntryLocalService.getOneToManyObjectEntries(
				bulkActionTaskObjectEntry.getGroupId(),
				_objectRelationshipLocalService.getObjectRelationship(
					_bulkActionTaskObjectDefinition.getObjectDefinitionId(),
					"bulkActionTaskToBulkActionTaskItems"
				).getObjectRelationshipId(),
				null, false, bulkActionTaskObjectEntry.getObjectEntryId(), true,
				null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
			objectEntry -> Objects.equals(
				GetterUtil.getLong(
					objectEntry.getValues(
					).get(
						"r_bulkActionTaskToBulkActionTaskItems_c_" +
							"bulkActionTaskId"
					)),
				bulkActionTaskObjectEntry.getObjectEntryId()));

		ObjectEntry objectEntry = objectEntries.get(0);

		return objectEntry.getValues();
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

	private void _postBulkAction(BulkAction bulkAction) throws Exception {
		BulkActionTask bulkActionTask = bulkActionResource.postBulkAction(
			null, null, bulkAction);

		Assert.assertEquals("initial", bulkActionTask.getExecuteStatus());
		Assert.assertNotNull(bulkActionTask.getId());

		ObjectEntry bulkActionTaskObjectEntry =
			_objectEntryLocalService.getObjectEntry(bulkActionTask.getId());

		List<ObjectEntry> objectEntries = ListUtil.filter(
			_objectEntryLocalService.getOneToManyObjectEntries(
				bulkActionTaskObjectEntry.getGroupId(),
				_objectRelationshipLocalService.getObjectRelationship(
					_bulkActionTaskObjectDefinition.getObjectDefinitionId(),
					"bulkActionTaskToBulkActionTaskItems"
				).getObjectRelationshipId(),
				null, false, bulkActionTaskObjectEntry.getObjectEntryId(), true,
				null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
			objectEntry -> Objects.equals(
				GetterUtil.getLong(
					objectEntry.getValues(
					).get(
						"r_bulkActionTaskToBulkActionTaskItems_c_" +
							"bulkActionTaskId"
					)),
				bulkActionTaskObjectEntry.getObjectEntryId()));

		ObjectEntry objectEntry = objectEntries.get(0);

		Map<String, Serializable> values = objectEntry.getValues();

		_waitForFinish(GetterUtil.getLong(values.get("importTaskId")));
	}

	private void _testPostBulkActionItemPreviewPage(
			DepotEntry depotEntry, String expectedDeletionType)
		throws Exception {

		BulkAction bulkAction = new DeleteBulkAction();

		bulkAction.setType(BulkAction.Type.DELETE_BULK_ACTION);

		ObjectEntryFolder contentObjectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					"L_CONTENTS", depotEntry.getGroupId(),
					testCompany.getCompanyId());

		ObjectEntryFolder objectEntryFolder1 = _addObjectEntryFolder(
			depotEntry.getGroupId(),
			contentObjectEntryFolder.getObjectEntryFolderId());

		_serviceContext.setAttribute(
			"friendlyUrlMap", new HashMap<String, String>());

		ObjectEntry objectEntry = _addObjectEntry(
			depotEntry.getGroupId(),
			objectEntryFolder1.getObjectEntryFolderId());

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			testGroup.getGroupId(), StringPool.BLANK,
			_portal.getClassNameId(
				_basicWebContentObjectDefinition.getClassName()),
			objectEntry.getObjectEntryId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
			_serviceContext);

		ObjectEntryFolder objectEntryFolder2 = _addObjectEntryFolder(
			depotEntry.getGroupId(),
			objectEntryFolder1.getObjectEntryFolderId());

		_testPostBulkActionItemPreviewPageWithBulkActionItems(
			bulkAction, expectedDeletionType, 2L, objectEntry,
			objectEntryFolder1);
		_testPostBulkActionItemPreviewPageWithBulkActionItems(
			bulkAction, expectedDeletionType, 0L, objectEntry,
			objectEntryFolder2);
		_testPostBulkActionItemPreviewPageWithFetchChildrenEnabled(
			bulkAction, expectedDeletionType, objectEntry, objectEntryFolder1,
			objectEntryFolder2);
		_testPostBulkActionItemPreviewPageWithSelectAllAndFilter(
			bulkAction, expectedDeletionType, objectEntryFolder1,
			objectEntryFolder2);
	}

	private void _testPostBulkActionItemPreviewPageWithBulkActionItems(
			BulkAction bulkAction, String expectedDeletionType,
			Long expectedItemsCount, ObjectEntry objectEntry,
			ObjectEntryFolder objectEntryFolder)
		throws Exception {

		bulkAction.setBulkActionItems(
			new BulkActionItem[] {
				_toBulkActionItem(
					_basicWebContentObjectDefinition.getClassName(),
					objectEntry),
				_toBulkActionItem(objectEntryFolder)
			});
		bulkAction.setSelectAll(false);

		Page<BulkActionItem> page1 =
			bulkActionResource.postBulkActionItemPreviewPage(
				false, null, null, Pagination.of(1, 10), "name:desc",
				bulkAction);

		Assert.assertEquals(2, page1.getTotalCount());

		List<BulkActionItem> items1 = ListUtil.fromCollection(page1.getItems());

		Assert.assertEquals(items1.toString(), 2, items1.size());

		String name = objectEntry.getTitleValue(_LANGUAGE_ID);

		if (name.compareTo(objectEntryFolder.getName()) < 0) {
			_assertBulkActionItem(
				items1.get(0), objectEntryFolder.getObjectEntryFolderId(),
				expectedDeletionType, expectedItemsCount, null,
				objectEntryFolder.getName(), "FOLDER", null);
			_assertBulkActionItem(
				items1.get(1), objectEntry.getObjectEntryId(),
				expectedDeletionType, null, "basic-web-content",
				objectEntry.getTitleValue(_LANGUAGE_ID), "ASSET", 1L);
		}
		else {
			_assertBulkActionItem(
				items1.get(0), objectEntry.getObjectEntryId(),
				expectedDeletionType, null, "basic-web-content",
				objectEntry.getTitleValue(_LANGUAGE_ID), "ASSET", 1L);
			_assertBulkActionItem(
				items1.get(1), objectEntryFolder.getObjectEntryFolderId(),
				expectedDeletionType, expectedItemsCount, null,
				objectEntryFolder.getName(), "FOLDER", null);
		}

		Page<BulkActionItem> page2 =
			bulkActionResource.postBulkActionItemPreviewPage(
				false, null, null, Pagination.of(1, 10), "usages:asc",
				bulkAction);

		Assert.assertEquals(2, page2.getTotalCount());

		List<BulkActionItem> items2 = ListUtil.fromCollection(page2.getItems());

		Assert.assertEquals(items2.toString(), 2, items2.size());

		_assertBulkActionItem(
			items2.get(0), objectEntryFolder.getObjectEntryFolderId(),
			expectedDeletionType, expectedItemsCount, null,
			objectEntryFolder.getName(), "FOLDER", null);
		_assertBulkActionItem(
			items2.get(1), objectEntry.getObjectEntryId(), expectedDeletionType,
			null, "basic-web-content", objectEntry.getTitleValue(_LANGUAGE_ID),
			"ASSET", 1L);
	}

	private void _testPostBulkActionItemPreviewPageWithFetchChildrenEnabled(
			BulkAction bulkAction, String expectedDeletionType,
			ObjectEntry objectEntry, ObjectEntryFolder objectEntryFolder1,
			ObjectEntryFolder objectEntryFolder2)
		throws Exception {

		bulkAction.setBulkActionItems(
			new BulkActionItem[] {_toBulkActionItem(objectEntryFolder1)});

		Page<BulkActionItem> page1 =
			bulkActionResource.postBulkActionItemPreviewPage(
				true, null, null, Pagination.of(1, 10), "name:desc",
				bulkAction);

		Assert.assertEquals(2, page1.getTotalCount());

		List<BulkActionItem> items1 = ListUtil.fromCollection(page1.getItems());

		Assert.assertEquals(items1.toString(), 2, items1.size());

		String name = objectEntry.getTitleValue(_LANGUAGE_ID);

		if (name.compareTo(objectEntryFolder2.getName()) < 0) {
			_assertBulkActionItem(
				items1.get(0), objectEntryFolder2.getObjectEntryFolderId(),
				expectedDeletionType, 0L, null, objectEntryFolder2.getName(),
				"FOLDER", null);
			_assertBulkActionItem(
				items1.get(1), objectEntry.getObjectEntryId(),
				expectedDeletionType, null, "basic-web-content",
				objectEntry.getTitleValue(_LANGUAGE_ID), "ASSET", 1L);
		}
		else {
			_assertBulkActionItem(
				items1.get(0), objectEntry.getObjectEntryId(),
				expectedDeletionType, null, "basic-web-content",
				objectEntry.getTitleValue(_LANGUAGE_ID), "ASSET", 1L);
			_assertBulkActionItem(
				items1.get(1), objectEntryFolder2.getObjectEntryFolderId(),
				expectedDeletionType, 0L, null, objectEntryFolder2.getName(),
				"FOLDER", null);
		}

		Page<BulkActionItem> page2 =
			bulkActionResource.postBulkActionItemPreviewPage(
				true, null, null, Pagination.of(1, 10), "usages:desc",
				bulkAction);

		Assert.assertEquals(2, page2.getTotalCount());

		List<BulkActionItem> items2 = ListUtil.fromCollection(page2.getItems());

		Assert.assertEquals(items2.toString(), 2, items2.size());

		_assertBulkActionItem(
			items2.get(0), objectEntry.getObjectEntryId(), expectedDeletionType,
			null, "basic-web-content", objectEntry.getTitleValue(_LANGUAGE_ID),
			"ASSET", 1L);
		_assertBulkActionItem(
			items2.get(1), objectEntryFolder2.getObjectEntryFolderId(),
			expectedDeletionType, 0L, null, objectEntryFolder2.getName(),
			"FOLDER", null);
	}

	private void _testPostBulkActionItemPreviewPageWithSelectAllAndFilter(
			BulkAction bulkAction, String expectedDeletionType,
			ObjectEntryFolder objectEntryFolder1,
			ObjectEntryFolder objectEntryFolder2)
		throws Exception {

		bulkAction.setSelectAll(true);

		Page<BulkActionItem> page =
			bulkActionResource.postBulkActionItemPreviewPage(
				false, objectEntryFolder2.getName(),
				"folderId eq " + objectEntryFolder1.getObjectEntryFolderId(),
				Pagination.of(1, 10), null, bulkAction);

		Assert.assertEquals(1, page.getTotalCount());

		List<BulkActionItem> items = ListUtil.fromCollection(page.getItems());

		Assert.assertEquals(items.toString(), 1, items.size());

		_assertBulkActionItem(
			items.get(0), objectEntryFolder2.getObjectEntryFolderId(),
			expectedDeletionType, 0L, null, objectEntryFolder2.getName(),
			"FOLDER", null);
	}

	private void _testPostBulkActionWithTypeDefaultPermission()
		throws Exception {

		DefaultPermissionBulkAction defaultPermissionBulkAction =
			new DefaultPermissionBulkAction();

		defaultPermissionBulkAction.setDefaultPermissions(
			"{\"test1\": \"test1\"}");
		defaultPermissionBulkAction.setSelectAll(true);
		defaultPermissionBulkAction.setType(
			BulkAction.Type.DEFAULT_PERMISSION_BULK_ACTION);

		try {
			bulkActionResource.postBulkAction(
				null, null, defaultPermissionBulkAction);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Assert.assertNotNull(problemException);
		}

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_DEFAULT_PERMISSION", _depotEntry2.getCompanyId());

		ObjectEntryFolder objectEntryFolder1 =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					_depotEntry2.getGroupId(), _depotEntry2.getCompanyId());

		ObjectEntryFolder objectEntryFolder2 =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				RandomTestUtil.randomString(), _depotEntry2.getGroupId(),
				TestPropsValues.getUserId(),
				objectEntryFolder1.getObjectEntryFolderId(), "",
				HashMapBuilder.put(
					LocaleUtil.ENGLISH, RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		JSONObject jsonObject = _getDefaultPermissionsJSONObject(
			objectDefinition, objectEntryFolder2);

		Assert.assertTrue(
			jsonObject.has(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS));

		ObjectEntryFolder objectEntryFolder3 =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				RandomTestUtil.randomString(), _depotEntry2.getGroupId(),
				TestPropsValues.getUserId(),
				objectEntryFolder1.getObjectEntryFolderId(), "",
				HashMapBuilder.put(
					LocaleUtil.ENGLISH, RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		jsonObject = _getDefaultPermissionsJSONObject(
			objectDefinition, objectEntryFolder2);

		Assert.assertTrue(
			jsonObject.has(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS));

		defaultPermissionBulkAction.setDepotGroupId(_depotEntry2.getGroupId());

		BulkActionTask bulkActionTask = bulkActionResource.postBulkAction(
			null, null, defaultPermissionBulkAction);

		Map<String, Serializable> values = _getImportTaskValues(bulkActionTask);

		_waitForFinish(GetterUtil.getLong(values.get("importTaskId")));

		jsonObject = _getDefaultPermissionsJSONObject(
			objectDefinition, objectEntryFolder2);

		Assert.assertFalse(
			jsonObject.has(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS));
		Assert.assertTrue(jsonObject.has("test1"));

		jsonObject = _getDefaultPermissionsJSONObject(
			objectDefinition, objectEntryFolder3);

		Assert.assertFalse(
			jsonObject.has(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS));
		Assert.assertTrue(jsonObject.has("test1"));

		ObjectEntryFolder objectEntryFolder4 =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				RandomTestUtil.randomString(), _depotEntry2.getGroupId(),
				TestPropsValues.getUserId(),
				objectEntryFolder3.getObjectEntryFolderId(), "",
				HashMapBuilder.put(
					LocaleUtil.ENGLISH, RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		jsonObject = _getDefaultPermissionsJSONObject(
			objectDefinition, objectEntryFolder4);

		Assert.assertFalse(
			jsonObject.has(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS));
		Assert.assertTrue(jsonObject.has("test1"));

		defaultPermissionBulkAction.setDefaultPermissions(
			"{\"test2\": \"test2\"}");
		defaultPermissionBulkAction.setTreePath(
			objectEntryFolder3.getTreePath());

		bulkActionTask = bulkActionResource.postBulkAction(
			null, null, defaultPermissionBulkAction);

		values = _getImportTaskValues(bulkActionTask);

		_waitForFinish(GetterUtil.getLong(values.get("importTaskId")));

		jsonObject = _getDefaultPermissionsJSONObject(
			objectDefinition, objectEntryFolder2);

		Assert.assertTrue(jsonObject.has("test1"));
		Assert.assertFalse(jsonObject.has("test2"));

		jsonObject = _getDefaultPermissionsJSONObject(
			objectDefinition, objectEntryFolder3);

		Assert.assertFalse(jsonObject.has("test1"));
		Assert.assertTrue(jsonObject.has("test2"));

		jsonObject = _getDefaultPermissionsJSONObject(
			objectDefinition, objectEntryFolder4);

		Assert.assertFalse(jsonObject.has("test1"));
		Assert.assertTrue(jsonObject.has("test2"));

		defaultPermissionBulkAction.setBulkActionItems(
			new BulkActionItem[] {
				_toBulkActionItem(objectEntryFolder2),
				_toBulkActionItem(objectEntryFolder4)
			});
		defaultPermissionBulkAction.setDefaultPermissions(
			"{\"test3\": \"test3\"}");
		defaultPermissionBulkAction.setSelectAll(false);

		bulkActionTask = bulkActionResource.postBulkAction(
			null, null, defaultPermissionBulkAction);

		values = _getImportTaskValues(bulkActionTask);

		_waitForFinish(GetterUtil.getLong(values.get("importTaskId")));

		jsonObject = _getDefaultPermissionsJSONObject(
			objectDefinition, objectEntryFolder2);

		Assert.assertFalse(jsonObject.has("test2"));
		Assert.assertTrue(jsonObject.has("test3"));

		jsonObject = _getDefaultPermissionsJSONObject(
			objectDefinition, objectEntryFolder4);

		Assert.assertFalse(jsonObject.has("test2"));
		Assert.assertTrue(jsonObject.has("test3"));
	}

	private void _testPostBulkActionWithTypeDelete() throws Exception {
		BulkAction bulkAction = new DeleteBulkAction();

		bulkAction.setType(BulkAction.Type.DELETE_BULK_ACTION);

		BulkActionTask bulkActionTask = bulkActionResource.postBulkAction(
			null, null, bulkAction);

		Assert.assertNull(bulkActionTask.getId());

		bulkAction.setSelectAll(true);

		try {
			bulkActionResource.postBulkAction(null, null, bulkAction);
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals("Filter is null", problem.getTitle());
		}

		ObjectEntry basicWebContentObjectEntry =
			ObjectEntryTestUtil.addObjectEntry(
				_depotEntry2.getGroupId(), _basicWebContentObjectDefinition,
				Collections.emptyMap());

		bulkAction.setBulkActionItems(
			_toBulkActionItems(basicWebContentObjectEntry));

		_postBulkAction(bulkAction);

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				basicWebContentObjectEntry.getObjectEntryId()));
	}

	private void _testPostBulkActionWithTypeKeyword() throws Exception {
		KeywordBulkAction keywordBulkAction = new KeywordBulkAction();

		ObjectEntry basicWebContentObjectEntry =
			ObjectEntryTestUtil.addObjectEntry(
				_depotEntry2.getGroupId(), _basicWebContentObjectDefinition,
				Collections.emptyMap());

		keywordBulkAction.setBulkActionItems(
			_toBulkActionItems(basicWebContentObjectEntry));

		String[] keywords = {
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		};

		keywordBulkAction.setKeywords(keywords);

		keywordBulkAction.setType(BulkAction.Type.KEYWORD_BULK_ACTION);

		_postBulkAction(keywordBulkAction);

		Assert.assertArrayEquals(
			keywords,
			_assetTagLocalService.getTagNames(
				_basicWebContentObjectDefinition.getClassName(),
				basicWebContentObjectEntry.getObjectEntryId()));
	}

	private void _testPostBulkActionWithTypePermission() throws Exception {
		PermissionBulkAction permissionBulkAction = new PermissionBulkAction();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roleLocalService.addGroupRole(
			testGroup.getGroupId(), role.getRoleId());

		ObjectEntry basicWebContentObjectEntry =
			ObjectEntryTestUtil.addObjectEntry(
				_depotEntry2.getGroupId(), _basicWebContentObjectDefinition,
				Collections.emptyMap());

		permissionBulkAction.setBulkActionItems(
			_toBulkActionItems(basicWebContentObjectEntry));

		permissionBulkAction.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(new String[] {"VIEW"});
						setRoleExternalReferenceCode(
							role.getExternalReferenceCode());
						setRoleName(role.getName());
						setRoleType(role.getTypeLabel());
					}
				}
			});

		permissionBulkAction.setType(BulkAction.Type.PERMISSION_BULK_ACTION);

		_postBulkAction(permissionBulkAction);

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				_depotEntry2.getCompanyId(),
				basicWebContentObjectEntry.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(basicWebContentObjectEntry.getObjectEntryId()),
				role.getRoleId());

		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.VIEW));
	}

	private void _testPostBulkActionWithTypeTaxonomyCategory()
		throws Exception {

		TaxonomyCategoryBulkAction taxonomyCategoryBulkAction =
			new TaxonomyCategoryBulkAction();

		ObjectEntry basicWebContentObjectEntry =
			ObjectEntryTestUtil.addObjectEntry(
				_depotEntry2.getGroupId(), _basicWebContentObjectDefinition,
				Collections.emptyMap());

		taxonomyCategoryBulkAction.setBulkActionItems(
			_toBulkActionItems(basicWebContentObjectEntry));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), testGroup.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), testGroup.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), testGroup.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		Long[] taxonomyCategoryIds = {
			assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
		};

		taxonomyCategoryBulkAction.setTaxonomyCategoryIds(taxonomyCategoryIds);

		taxonomyCategoryBulkAction.setType(
			BulkAction.Type.TAXONOMY_CATEGORY_BULK_ACTION);

		_postBulkAction(taxonomyCategoryBulkAction);

		Assert.assertArrayEquals(
			ArrayUtil.toArray(taxonomyCategoryIds),
			_assetCategoryLocalService.getCategoryIds(
				_basicWebContentObjectDefinition.getClassName(),
				basicWebContentObjectEntry.getObjectEntryId()));
	}

	private BulkActionItem _toBulkActionItem(
		ObjectEntryFolder objectEntryFolder) {

		BulkActionItem bulkActionItem = new BulkActionItem();

		bulkActionItem.setClassPK(objectEntryFolder.getObjectEntryFolderId());
		bulkActionItem.setClassName(objectEntryFolder.getModelClassName());
		bulkActionItem.setClassExternalReferenceCode(
			objectEntryFolder.getExternalReferenceCode());
		bulkActionItem.setName(objectEntryFolder.getName());

		return bulkActionItem;
	}

	private BulkActionItem _toBulkActionItem(
			String className, ObjectEntry objectEntry)
		throws Exception {

		BulkActionItem bulkActionItem = new BulkActionItem();

		bulkActionItem.setClassPK(objectEntry.getObjectEntryId());
		bulkActionItem.setClassName(className);
		bulkActionItem.setClassExternalReferenceCode(
			objectEntry.getExternalReferenceCode());
		bulkActionItem.setName(objectEntry.getTitleValue(_LANGUAGE_ID));

		return bulkActionItem;
	}

	private BulkActionItem[] _toBulkActionItems(
			ObjectEntry basicWebContentObjectEntry)
		throws Exception {

		return new BulkActionItem[] {
			new BulkActionItem() {
				{
					setClassExternalReferenceCode(
						basicWebContentObjectEntry.getExternalReferenceCode());
					setClassName(
						_basicWebContentObjectDefinition.getClassName());
					setClassPK(basicWebContentObjectEntry.getObjectEntryId());
					setName(basicWebContentObjectEntry.getTitleValue());
				}
			}
		};
	}

	private void _waitForFinish(long importTaskId) throws Exception {
		while (true) {
			ImportTask importTask = _importTaskResource.getImportTask(
				importTaskId);

			ImportTask.ExecuteStatus executeStatus =
				importTask.getExecuteStatus();

			if (StringUtil.equals(executeStatus.getValue(), "COMPLETED") ||
				StringUtil.equals(executeStatus.getValue(), "FAILED")) {

				Assert.assertEquals("COMPLETED", executeStatus.getValue());

				return;
			}
		}
	}

	private static final String _LANGUAGE_ID = "en_US";

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private ObjectDefinition _basicWebContentObjectDefinition;

	@Inject
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	@Inject
	private BatchEngineUnitReader _batchEngineUnitReader;

	private ObjectDefinition _bulkActionTaskObjectDefinition;
	private DepotEntry _depotEntry1;
	private DepotEntry _depotEntry2;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private FilterFactory<Predicate> _filterFactory;

	private ImportTaskResource _importTaskResource;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}