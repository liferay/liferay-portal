/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.bulk.rest.client.dto.v1_0.AssignStructureDefaultWorkflowBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.BulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.client.dto.v1_0.BulkActionTask;
import com.liferay.bulk.rest.client.dto.v1_0.CopyBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.DefaultPermissionBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.DeleteBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.ExpireBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.KeywordBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.PermissionBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.ResetPermissionBulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.SelectionScope;
import com.liferay.bulk.rest.client.dto.v1_0.TaxonomyCategoryBulkAction;
import com.liferay.bulk.rest.client.pagination.Page;
import com.liferay.bulk.rest.client.pagination.Pagination;
import com.liferay.bulk.rest.client.problem.Problem;
import com.liferay.bulk.selection.constants.BulkSelectionActionStatusConstants;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.definition.setting.builder.ObjectDefinitionSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
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
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectEntryFolderTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.GroupUtil;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;
import com.liferay.site.cms.site.initializer.util.CMSDefaultPermissionUtil;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-34594")}
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

		CMSTestUtil.getOrAddGroup(BulkActionResourceTest.class);

		_cmsAdministratorRole = _getOrAddCMSAdministratorRole(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId());
		_cmsBasicWebContentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", testCompany.getCompanyId());
		_cmsBulkActionTaskObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BULK_ACTION_TASK", testCompany.getCompanyId());

		_depotEntry1 = _addDepotEntry(true);
		_depotEntry2 = _addDepotEntry(false);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_depotEntry1.getGroupId(), TestPropsValues.getUserId());

		_user = UserTestUtil.addUser();
	}

	@Override
	@Test
	public void testPostBulkAction() throws Exception {
		_testPostBulkActionWithTypeAssignStructureDefaultWorkflow();
		_testPostBulkActionWithTypeCopy();
		_testPostBulkActionWithTypeDefaultPermission();
		_testPostBulkActionWithTypeDefaultPermissionSingleRole();
		_testPostBulkActionWithTypeDelete();
		_testPostBulkActionWithTypeDeleteObjectEntry();
		_testPostBulkActionWithTypeExpire();
		_testPostBulkActionWithTypeKeyword();
		_testPostBulkActionWithTypePermission();
		_testPostBulkActionWithTypePermissionSingleRole();
		_testPostBulkActionWithTypeResetPermission();
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
			nameMap, null, DepotConstants.TYPE_SPACE, serviceContext);

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
			_cmsBasicWebContentObjectDefinition.getObjectDefinitionId(),
			objectEntryFolderId, _LANGUAGE_ID, _getObjectEntryValues(),
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

	private ObjectEntry[] _getObjectEntries(
			Group group, ObjectDefinition objectDefinition)
		throws Exception {

		List<ObjectEntry> objectEntries = new ArrayList<>();

		objectEntries.add(
			ObjectEntryTestUtil.addObjectEntry(
				group.getGroupId(), objectDefinition, _getObjectEntryValues()));
		objectEntries.add(
			ObjectEntryTestUtil.addObjectEntry(
				group.getGroupId(), objectDefinition, _getObjectEntryValues()));

		return objectEntries.toArray(new ObjectEntry[0]);
	}

	private Map<String, Serializable> _getObjectEntryValues() {
		return HashMapBuilder.<String, Serializable>put(
			"title_i18n",
			HashMapBuilder.put(
				_LANGUAGE_ID, RandomTestUtil.randomString()
			).build()
		).build();
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

	private void _postBulkAction(BulkAction bulkAction) throws Exception {
		BulkActionTask bulkActionTask = bulkActionResource.postBulkAction(
			null, null, null, null, null, null, null, null, bulkAction);

		Assert.assertNotNull(bulkActionTask.getId());

		_waitForFinish(GetterUtil.getLong(bulkActionTask.getId()));

		BulkActionItem[] bulkActionItems = bulkAction.getBulkActionItems();

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			bulkActionTask.getId());

		Map<String, Serializable> values = objectEntry.getValues();

		Assert.assertEquals(
			bulkActionItems.length,
			GetterUtil.getInteger((String)values.get("numberOfItems")));
	}

	private BulkAction _testBulkDeleteFilterValidation(BulkAction.Type type)
		throws Exception {

		BulkAction bulkAction = new DeleteBulkAction();

		SelectionScope selectionScope = new SelectionScope();

		selectionScope.setSelectAll(false);

		bulkAction.setSelectionScope(selectionScope);

		bulkAction.setType(type);

		BulkActionTask bulkActionTask = bulkActionResource.postBulkAction(
			null, null, null, null, null, null, null, null, bulkAction);

		Assert.assertNull(bulkActionTask.getId());

		selectionScope.setSelectAll(true);

		bulkAction.setSelectionScope(selectionScope);

		try {
			bulkActionResource.postBulkAction(
				null, null, null, null, null, null, null, null, bulkAction);
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals("Filter is null", problem.getTitle());
		}

		return bulkAction;
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
				_cmsBasicWebContentObjectDefinition.getClassName()),
			objectEntry.getObjectEntryId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomInt(), RandomTestUtil.randomInt(),
			_serviceContext);

		_testPostBulkActionItemPreviewPageWithBulkActionItems(
			bulkAction, expectedDeletionType, 1L, objectEntry,
			objectEntryFolder1);

		ObjectEntryFolder objectEntryFolder2 = _addObjectEntryFolder(
			depotEntry.getGroupId(),
			objectEntryFolder1.getObjectEntryFolderId());

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
					_cmsBasicWebContentObjectDefinition.getClassName(),
					objectEntry),
				_toBulkActionItem(objectEntryFolder)
			});

		SelectionScope selectionScope = new SelectionScope();

		selectionScope.setSelectAll(false);

		bulkAction.setSelectionScope(selectionScope);

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

		SelectionScope selectionScope = new SelectionScope();

		selectionScope.setSelectAll(true);

		bulkAction.setSelectionScope(selectionScope);

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

	private void _testPostBulkActionWithTypeAssignStructureDefaultWorkflow()
		throws Exception {

		AssignStructureDefaultWorkflowBulkAction bulkAction =
			new AssignStructureDefaultWorkflowBulkAction();

		bulkAction.setBulkActionItems(
			_toBulkActionItems(null, _cmsBulkActionTaskObjectDefinition));
		bulkAction.setType(
			BulkAction.Type.ASSIGN_STRUCTURE_DEFAULT_WORKFLOW_BULK_ACTION);
		bulkAction.setWorkflow("Single Approver");

		_postBulkAction(bulkAction);

		List<WorkflowDefinitionLink> workflowDefinitionLinks =
			WorkflowDefinitionLinkLocalServiceUtil.getWorkflowDefinitionLinks(
				testCompany.getCompanyId(),
				_cmsBulkActionTaskObjectDefinition.getClassName());

		Assert.assertFalse(workflowDefinitionLinks.isEmpty());

		WorkflowDefinitionLink workflowDefinitionLink =
			workflowDefinitionLinks.get(0);

		Assert.assertEquals(
			"Single Approver",
			workflowDefinitionLink.getWorkflowDefinitionName());

		bulkAction.setWorkflow("");

		_postBulkAction(bulkAction);

		workflowDefinitionLinks =
			WorkflowDefinitionLinkLocalServiceUtil.getWorkflowDefinitionLinks(
				testCompany.getCompanyId(),
				_cmsBulkActionTaskObjectDefinition.getClassName());

		Assert.assertTrue(workflowDefinitionLinks.isEmpty());
	}

	private void _testPostBulkActionWithTypeCopy() throws Exception {
		CopyBulkAction copyBulkAction = new CopyBulkAction();

		copyBulkAction.setType(BulkAction.Type.COPY_BULK_ACTION);

		ObjectEntryFolder sourceObjectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				_depotEntry1.getGroupId());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_depotEntry1.getGroupId(), _cmsBasicWebContentObjectDefinition,
			sourceObjectEntryFolder.getObjectEntryFolderId(),
			_getObjectEntryValues());

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				_depotEntry1.getGroupId(),
				sourceObjectEntryFolder.getObjectEntryFolderId());

		copyBulkAction.setBulkActionItems(
			_toBulkActionItems(
				_cmsBasicWebContentObjectDefinition, objectEntry,
				objectEntryFolder));

		BulkActionTask bulkActionTask = bulkActionResource.postBulkAction(
			null, null, null, null, null, null, null, null, copyBulkAction);

		Assert.assertNotNull(bulkActionTask.getId());

		_waitForFinish(GetterUtil.getLong(bulkActionTask.getId()));

		ObjectEntry copyBulkActionObjectEntry =
			_objectEntryLocalService.getObjectEntry(bulkActionTask.getId());

		Map<String, Serializable> values =
			copyBulkActionObjectEntry.getValues();

		Assert.assertEquals(2, values.get("numberOfFailedItems"));

		ObjectEntryFolder targetObjectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				_depotEntry1.getGroupId());

		Assert.assertEquals(
			1,
			_objectEntryFolderLocalService.getObjectEntryFoldersCount(
				sourceObjectEntryFolder.getGroupId(),
				sourceObjectEntryFolder.getCompanyId(),
				sourceObjectEntryFolder.getObjectEntryFolderId()));

		Assert.assertEquals(
			1,
			_objectEntryLocalService.getObjectEntryFolderObjectEntriesCount(
				sourceObjectEntryFolder.getGroupId(),
				sourceObjectEntryFolder.getObjectEntryFolderId()));

		Assert.assertEquals(
			0,
			_objectEntryFolderLocalService.getObjectEntryFoldersCount(
				targetObjectEntryFolder.getGroupId(),
				targetObjectEntryFolder.getCompanyId(),
				targetObjectEntryFolder.getObjectEntryFolderId()));

		Assert.assertEquals(
			0,
			_objectEntryLocalService.getObjectEntryFolderObjectEntriesCount(
				targetObjectEntryFolder.getGroupId(),
				targetObjectEntryFolder.getObjectEntryFolderId()));

		copyBulkAction.setObjectEntryFolderId(
			targetObjectEntryFolder.getObjectEntryFolderId());

		_postBulkAction(copyBulkAction);

		Assert.assertEquals(
			1,
			_objectEntryFolderLocalService.getObjectEntryFoldersCount(
				sourceObjectEntryFolder.getGroupId(),
				sourceObjectEntryFolder.getCompanyId(),
				sourceObjectEntryFolder.getObjectEntryFolderId()));

		Assert.assertEquals(
			1,
			_objectEntryLocalService.getObjectEntryFolderObjectEntriesCount(
				sourceObjectEntryFolder.getGroupId(),
				sourceObjectEntryFolder.getObjectEntryFolderId()));

		Assert.assertEquals(
			1,
			_objectEntryFolderLocalService.getObjectEntryFoldersCount(
				targetObjectEntryFolder.getGroupId(),
				targetObjectEntryFolder.getCompanyId(),
				targetObjectEntryFolder.getObjectEntryFolderId()));

		Assert.assertEquals(
			1,
			_objectEntryLocalService.getObjectEntryFolderObjectEntriesCount(
				targetObjectEntryFolder.getGroupId(),
				targetObjectEntryFolder.getObjectEntryFolderId()));

		targetObjectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				_depotEntry2.getGroupId());

		Assert.assertEquals(
			0,
			_objectEntryFolderLocalService.getObjectEntryFoldersCount(
				targetObjectEntryFolder.getGroupId(),
				targetObjectEntryFolder.getCompanyId(),
				targetObjectEntryFolder.getObjectEntryFolderId()));

		Assert.assertEquals(
			0,
			_objectEntryLocalService.getObjectEntryFolderObjectEntriesCount(
				targetObjectEntryFolder.getGroupId(),
				targetObjectEntryFolder.getObjectEntryFolderId()));

		copyBulkAction.setObjectEntryFolderId(
			targetObjectEntryFolder.getObjectEntryFolderId());

		_postBulkAction(copyBulkAction);

		Assert.assertEquals(
			1,
			_objectEntryFolderLocalService.getObjectEntryFoldersCount(
				sourceObjectEntryFolder.getGroupId(),
				sourceObjectEntryFolder.getCompanyId(),
				sourceObjectEntryFolder.getObjectEntryFolderId()));

		Assert.assertEquals(
			1,
			_objectEntryLocalService.getObjectEntryFolderObjectEntriesCount(
				sourceObjectEntryFolder.getGroupId(),
				sourceObjectEntryFolder.getObjectEntryFolderId()));

		Assert.assertEquals(
			1,
			_objectEntryFolderLocalService.getObjectEntryFoldersCount(
				targetObjectEntryFolder.getGroupId(),
				targetObjectEntryFolder.getCompanyId(),
				targetObjectEntryFolder.getObjectEntryFolderId()));

		Assert.assertEquals(
			1,
			_objectEntryLocalService.getObjectEntryFolderObjectEntriesCount(
				targetObjectEntryFolder.getGroupId(),
				targetObjectEntryFolder.getObjectEntryFolderId()));
	}

	private void _testPostBulkActionWithTypeDefaultPermission()
		throws Exception {

		DefaultPermissionBulkAction defaultPermissionBulkAction =
			new DefaultPermissionBulkAction();

		defaultPermissionBulkAction.setDefaultPermissions(
			"{\"test1\": \"test1\"}");

		SelectionScope selectionScope = new SelectionScope();

		selectionScope.setSelectAll(true);

		defaultPermissionBulkAction.setSelectionScope(selectionScope);

		defaultPermissionBulkAction.setType(
			BulkAction.Type.DEFAULT_PERMISSION_BULK_ACTION);

		try {
			bulkActionResource.postBulkAction(
				null, null, null, null, null, null, null, null,
				defaultPermissionBulkAction);

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
			null, null, null, null, null, null, null, null,
			defaultPermissionBulkAction);

		_waitForFinish(GetterUtil.getLong(bulkActionTask.getId()));

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
			null, null, null, null, null, null, null, null,
			defaultPermissionBulkAction);

		_waitForFinish(GetterUtil.getLong(bulkActionTask.getId()));

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

		selectionScope.setSelectAll(false);

		defaultPermissionBulkAction.setSelectionScope(selectionScope);

		bulkActionTask = bulkActionResource.postBulkAction(
			null, null, null, null, null, null, null, null,
			defaultPermissionBulkAction);

		_waitForFinish(GetterUtil.getLong(bulkActionTask.getId()));

		jsonObject = _getDefaultPermissionsJSONObject(
			objectDefinition, objectEntryFolder2);

		Assert.assertFalse(jsonObject.has("test2"));
		Assert.assertTrue(jsonObject.has("test3"));

		jsonObject = _getDefaultPermissionsJSONObject(
			objectDefinition, objectEntryFolder4);

		Assert.assertFalse(jsonObject.has("test2"));
		Assert.assertTrue(jsonObject.has("test3"));
	}

	private void _testPostBulkActionWithTypeDefaultPermissionSingleRole()
		throws Exception {

		DefaultPermissionBulkAction defaultPermissionBulkAction =
			new DefaultPermissionBulkAction();

		Role role1 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roleLocalService.addGroupRole(
			testGroup.getGroupId(), role1.getRoleId());

		Role role2 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roleLocalService.addGroupRole(
			testGroup.getGroupId(), role2.getRoleId());

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

		JSONObject defaultPermissionsJSONObject = JSONUtil.put(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
			JSONUtil.put(
				role1.getName(), JSONUtil.putAll(ActionKeys.VIEW)
			).put(
				role2.getName(), JSONUtil.putAll(ActionKeys.VIEW)
			));

		defaultPermissionBulkAction.setDefaultPermissions(
			defaultPermissionsJSONObject.toString());

		defaultPermissionBulkAction.setDepotGroupId(_depotEntry2.getGroupId());
		defaultPermissionBulkAction.setBulkActionItems(
			new BulkActionItem[] {
				_toBulkActionItem(objectEntryFolder2),
				_toBulkActionItem(objectEntryFolder3)
			});
		defaultPermissionBulkAction.setType(
			BulkAction.Type.DEFAULT_PERMISSION_BULK_ACTION);

		_postBulkAction(defaultPermissionBulkAction);

		JSONObject singleRoleDefaultPermissionsJSONObject = JSONUtil.put(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
			JSONUtil.put(
				role1.getName(),
				JSONUtil.putAll(ActionKeys.DELETE, ActionKeys.UPDATE)
			).put(
				role2.getName(), JSONUtil.putAll(ActionKeys.ADD_ENTRY)
			));

		defaultPermissionBulkAction.setDefaultPermissions(
			singleRoleDefaultPermissionsJSONObject.toString());

		defaultPermissionBulkAction.setRoleKey(role1.getName());

		_postBulkAction(defaultPermissionBulkAction);

		JSONObject jsonObject = _getDefaultPermissionsJSONObject(
			objectDefinition, objectEntryFolder2);

		defaultPermissionsJSONObject = jsonObject.getJSONObject(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS);

		JSONArray jsonArray = defaultPermissionsJSONObject.getJSONArray(
			role1.getName());

		String role1Permissions = jsonArray.toString();

		Assert.assertTrue(role1Permissions.contains(ActionKeys.DELETE));
		Assert.assertTrue(role1Permissions.contains(ActionKeys.UPDATE));
		Assert.assertFalse(role1Permissions.contains(ActionKeys.VIEW));

		jsonArray = defaultPermissionsJSONObject.getJSONArray(role2.getName());

		String role2Permissions = jsonArray.toString();

		Assert.assertFalse(role2Permissions.contains(ActionKeys.DELETE));
		Assert.assertFalse(role2Permissions.contains(ActionKeys.UPDATE));
		Assert.assertTrue(role2Permissions.contains(ActionKeys.VIEW));

		jsonObject = _getDefaultPermissionsJSONObject(
			objectDefinition, objectEntryFolder3);

		defaultPermissionsJSONObject = jsonObject.getJSONObject(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS);

		jsonArray = defaultPermissionsJSONObject.getJSONArray(role1.getName());

		role1Permissions = jsonArray.toString();

		Assert.assertTrue(role1Permissions.contains(ActionKeys.DELETE));
		Assert.assertTrue(role1Permissions.contains(ActionKeys.UPDATE));
		Assert.assertFalse(role1Permissions.contains(ActionKeys.VIEW));

		jsonArray = defaultPermissionsJSONObject.getJSONArray(role2.getName());

		role2Permissions = jsonArray.toString();

		Assert.assertFalse(role2Permissions.contains(ActionKeys.DELETE));
		Assert.assertFalse(role2Permissions.contains(ActionKeys.UPDATE));
		Assert.assertTrue(role2Permissions.contains(ActionKeys.VIEW));
	}

	private void _testPostBulkActionWithTypeDelete() throws Exception {
		BulkAction bulkAction = _testBulkDeleteFilterValidation(
			BulkAction.Type.DELETE_BULK_ACTION);

		SelectionScope selectionScope = bulkAction.getSelectionScope();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_depotEntry2.getGroupId(), _cmsBasicWebContentObjectDefinition,
			_getObjectEntryValues());

		bulkAction.setBulkActionItems(
			_toBulkActionItems(
				_cmsBasicWebContentObjectDefinition, objectEntry));

		selectionScope.setSelectAll(false);

		bulkAction.setSelectionScope(selectionScope);

		_postBulkAction(bulkAction);

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				objectEntry.getObjectEntryId()));
	}

	private void _testPostBulkActionWithTypeDeleteObjectEntry()
		throws Exception {

		BulkAction bulkAction = _testBulkDeleteFilterValidation(
			BulkAction.Type.DELETE_OBJECT_ENTRY_BULK_ACTION);

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
						"First Name", "firstName", false),
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
						"Last Name", "lastName", false)),
				ObjectDefinitionConstants.SCOPE_SITE,
				TestPropsValues.getUserId());

		Group group = GroupUtil.create(TestPropsValues.getGroupId());

		bulkAction.setBulkActionItems(
			_toBulkActionItems(
				objectDefinition, _getObjectEntries(group, objectDefinition)));

		bulkActionResource.postBulkAction(
			null, true, null, String.valueOf(group.getGroupId()), null,
			"(objectDefinitionId eq " +
				objectDefinition.getObjectDefinitionId() + ")",
			null, null, bulkAction);

		Assert.assertEquals(
			0,
			_objectEntryLocalService.getObjectEntriesCount(
				objectDefinition.getObjectDefinitionId()));

		ObjectEntry[] objectEntries = _getObjectEntries(
			group, objectDefinition);

		SelectionScope selectionScope = bulkAction.getSelectionScope();

		selectionScope.setSelectAll(false);

		bulkAction.setBulkActionItems(
			_toBulkActionItems(objectDefinition, objectEntries[0]));

		bulkAction.setSelectionScope(selectionScope);

		bulkActionResource.postBulkAction(
			null, null, null, String.valueOf(group.getGroupId()), null, null,
			null, null, bulkAction);

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				objectEntries[0].getObjectEntryId()));

		Assert.assertNotNull(
			_objectEntryLocalService.fetchObjectEntry(
				objectEntries[1].getObjectEntryId()));
	}

	private void _testPostBulkActionWithTypeExpire() throws Exception {
		BulkAction bulkAction = new ExpireBulkAction();

		bulkAction.setType(BulkAction.Type.EXPIRE_BULK_ACTION);

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_depotEntry2.getGroupId(), _cmsBasicWebContentObjectDefinition,
			_getObjectEntryValues());

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder();

		bulkAction.setBulkActionItems(
			_toBulkActionItems(
				_cmsBasicWebContentObjectDefinition, objectEntry,
				objectEntryFolder));

		_postBulkAction(bulkAction);

		objectEntry = _objectEntryLocalService.fetchObjectEntry(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED, objectEntry.getStatus());

		objectEntryFolder =
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				objectEntryFolder.getObjectEntryFolderId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, objectEntryFolder.getStatus());
	}

	private void _testPostBulkActionWithTypeKeyword() throws Exception {
		KeywordBulkAction keywordBulkAction = new KeywordBulkAction();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_depotEntry2.getGroupId(), _cmsBasicWebContentObjectDefinition,
			_getObjectEntryValues());

		keywordBulkAction.setBulkActionItems(
			_toBulkActionItems(
				_cmsBasicWebContentObjectDefinition, objectEntry));

		String[] keywords = {
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		};

		keywordBulkAction.setKeywordsToAdd(keywords);

		keywordBulkAction.setType(BulkAction.Type.KEYWORD_BULK_ACTION);

		_postBulkAction(keywordBulkAction);

		Assert.assertArrayEquals(
			ArrayUtil.sortedUnique(keywords),
			ArrayUtil.sortedUnique(
				_assetTagLocalService.getTagNames(
					_cmsBasicWebContentObjectDefinition.getClassName(),
					objectEntry.getObjectEntryId())));
	}

	private void _testPostBulkActionWithTypePermission() throws Exception {
		PermissionBulkAction permissionBulkAction = new PermissionBulkAction();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roleLocalService.addGroupRole(
			testGroup.getGroupId(), role.getRoleId());

		ObjectEntryFolder objectEntryFolder1 =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					_depotEntry2.getGroupId(), _depotEntry2.getCompanyId());

		ObjectEntry objectEntry1 = _objectEntryLocalService.addObjectEntry(
			_depotEntry2.getGroupId(), _depotEntry2.getUserId(),
			_cmsBasicWebContentObjectDefinition.getObjectDefinitionId(),
			objectEntryFolder1.getObjectEntryFolderId(), "en_US",
			_getObjectEntryValues(),
			ServiceContextTestUtil.getServiceContext());

		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(),
				objectFolder.getObjectFolderId(), null, false, true, false,
				true, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_DEPOT,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS
					).value(
						StringPool.TRUE
					).build()),
				ListUtil.fromArray(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "text")),
				Collections.emptyList(), new ServiceContext());

		objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		ObjectEntryFolder objectEntryFolder2 =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				RandomTestUtil.randomString(), _depotEntry2.getGroupId(),
				_depotEntry2.getUserId(),
				objectEntryFolder1.getObjectEntryFolderId(), "",
				HashMapBuilder.put(
					LocaleUtil.ENGLISH, RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		ObjectEntry objectEntry2 = _objectEntryLocalService.addObjectEntry(
			_depotEntry2.getGroupId(), _depotEntry2.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder2.getObjectEntryFolderId(), "en_US",
			_getObjectEntryValues(),
			ServiceContextTestUtil.getServiceContext());

		permissionBulkAction.setBulkActionItems(
			_toBulkActionItems(
				_cmsBulkActionTaskObjectDefinition, objectEntry1,
				objectEntry2));

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		permissionBulkAction.setConfiguration(
			jsonObject.put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
				JSONUtil.put(role.getName(), JSONUtil.putAll(ActionKeys.VIEW))
			).put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
				JSONUtil.put(role.getName(), JSONUtil.putAll(ActionKeys.VIEW))
			).put(
				"OBJECT_ENTRY_FOLDERS",
				JSONUtil.put(role.getName(), JSONUtil.putAll(ActionKeys.VIEW))
			).toString());

		permissionBulkAction.setType(BulkAction.Type.PERMISSION_BULK_ACTION);

		_postBulkAction(permissionBulkAction);

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				_depotEntry2.getCompanyId(), objectEntry1.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry1.getObjectEntryId()),
				role.getRoleId());

		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.VIEW));

		resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				_depotEntry2.getCompanyId(), objectEntry2.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry2.getObjectEntryId()),
				role.getRoleId());

		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.VIEW));
	}

	private void _testPostBulkActionWithTypePermissionSingleRole()
		throws Exception {

		PermissionBulkAction permissionBulkAction = new PermissionBulkAction();

		Role role1 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		Role role2 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roleLocalService.addGroupRole(
			testGroup.getGroupId(), role1.getRoleId());
		_roleLocalService.addGroupRole(
			testGroup.getGroupId(), role2.getRoleId());

		ObjectEntryFolder objectEntryFolder1 =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					_depotEntry2.getGroupId(), _depotEntry2.getCompanyId());

		ObjectEntry objectEntry1 = _objectEntryLocalService.addObjectEntry(
			_depotEntry2.getGroupId(), _depotEntry2.getUserId(),
			_cmsBasicWebContentObjectDefinition.getObjectDefinitionId(),
			objectEntryFolder1.getObjectEntryFolderId(), "en_US",
			_getObjectEntryValues(),
			ServiceContextTestUtil.getServiceContext());

		ObjectEntryFolder objectEntryFolder2 =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				RandomTestUtil.randomString(), _depotEntry2.getGroupId(),
				_depotEntry2.getUserId(),
				objectEntryFolder1.getObjectEntryFolderId(), "",
				HashMapBuilder.put(
					LocaleUtil.ENGLISH, RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext());

		_resourcePermissionLocalService.setResourcePermissions(
			_depotEntry2.getCompanyId(), objectEntry1.getModelClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry1.getObjectEntryId()), role1.getRoleId(),
			new String[] {ActionKeys.VIEW});
		_resourcePermissionLocalService.setResourcePermissions(
			_depotEntry2.getCompanyId(), objectEntry1.getModelClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry1.getObjectEntryId()), role2.getRoleId(),
			new String[] {ActionKeys.VIEW});
		_resourcePermissionLocalService.setResourcePermissions(
			_depotEntry2.getCompanyId(), objectEntryFolder2.getModelClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntryFolder2.getObjectEntryFolderId()),
			role1.getRoleId(), new String[] {ActionKeys.VIEW});
		_resourcePermissionLocalService.setResourcePermissions(
			_depotEntry2.getCompanyId(), objectEntryFolder2.getModelClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntryFolder2.getObjectEntryFolderId()),
			role2.getRoleId(), new String[] {ActionKeys.VIEW});

		permissionBulkAction.setBulkActionItems(
			new BulkActionItem[] {
				_toBulkActionItem(
					_cmsBasicWebContentObjectDefinition.getClassName(),
					objectEntry1),
				_toBulkActionItem(objectEntryFolder2)
			});

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		permissionBulkAction.setConfiguration(
			jsonObject.put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
				JSONUtil.put(
					role1.getName(),
					JSONUtil.putAll(
						ActionKeys.DELETE, ActionKeys.UPDATE, ActionKeys.VIEW))
			).put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
				JSONUtil.put(role1.getName(), JSONUtil.putAll(ActionKeys.VIEW))
			).put(
				"OBJECT_ENTRY_FOLDERS",
				JSONUtil.put(
					role1.getName(),
					JSONUtil.putAll(
						ActionKeys.ADD_ENTRY, ActionKeys.PERMISSIONS,
						ActionKeys.SUBSCRIBE))
			).toString());

		permissionBulkAction.setRoleKey(role1.getName());
		permissionBulkAction.setType(BulkAction.Type.PERMISSION_BULK_ACTION);

		_postBulkAction(permissionBulkAction);

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				_depotEntry2.getCompanyId(), objectEntry1.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry1.getObjectEntryId()),
				role1.getRoleId());

		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.DELETE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.UPDATE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.VIEW));

		resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				_depotEntry2.getCompanyId(), objectEntry1.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry1.getObjectEntryId()),
				role2.getRoleId());

		Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.DELETE));
		Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.UPDATE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.VIEW));

		resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				_depotEntry2.getCompanyId(),
				objectEntryFolder2.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntryFolder2.getObjectEntryFolderId()),
				role1.getRoleId());

		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.ADD_ENTRY));
		Assert.assertTrue(
			resourcePermission.hasActionId(ActionKeys.PERMISSIONS));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.SUBSCRIBE));
		Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.VIEW));

		resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				_depotEntry2.getCompanyId(),
				objectEntryFolder2.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntryFolder2.getObjectEntryFolderId()),
				role2.getRoleId());

		Assert.assertFalse(
			resourcePermission.hasActionId(ActionKeys.ADD_ENTRY));
		Assert.assertFalse(
			resourcePermission.hasActionId(ActionKeys.PERMISSIONS));
		Assert.assertFalse(
			resourcePermission.hasActionId(ActionKeys.SUBSCRIBE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.VIEW));
	}

	private void _testPostBulkActionWithTypeResetPermission() throws Exception {
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
				JSONUtil.putAll(ActionKeys.ADD_ENTRY, ActionKeys.SUBSCRIBE))
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

		ObjectEntryFolder objectEntryFolder3 =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				RandomTestUtil.randomString(), group.getGroupId(),
				group.getCreatorUserId(),
				objectEntryFolder2.getObjectEntryFolderId(), "",
				HashMapBuilder.put(
					LocaleUtil.ENGLISH, RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(), serviceContext);

		_resourcePermissionLocalService.setResourcePermissions(
			objectEntryFolder3.getCompanyId(),
			objectEntryFolder3.getModelClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntryFolder3.getPrimaryKeyObj()),
			_cmsAdministratorRole.getRoleId(),
			new String[] {ActionKeys.DELETE});

		ResetPermissionBulkAction resetPermissionBulkAction =
			new ResetPermissionBulkAction();

		resetPermissionBulkAction.setBulkActionItems(
			new BulkActionItem[] {
				_toBulkActionItem(
					objectEntry2.getModelClassName(), objectEntry2),
				_toBulkActionItem(objectEntryFolder3)
			});

		resetPermissionBulkAction.setType(
			ResetPermissionBulkAction.Type.RESET_PERMISSION_BULK_ACTION);

		_postBulkAction(resetPermissionBulkAction);

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				objectEntry2.getCompanyId(), objectEntry2.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry2.getObjectEntryId()),
				_cmsAdministratorRole.getRoleId());

		Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.DELETE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.UPDATE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.VIEW));

		resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				objectEntryFolder3.getCompanyId(),
				objectEntryFolder3.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntryFolder3.getObjectEntryFolderId()),
				_cmsAdministratorRole.getRoleId());

		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.ADD_ENTRY));
		Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.DELETE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.SUBSCRIBE));

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

		ObjectEntryFolder objectEntryFolder4 =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				RandomTestUtil.randomString(), group.getGroupId(),
				group.getCreatorUserId(),
				objectEntryFolder2.getObjectEntryFolderId(), "",
				HashMapBuilder.put(
					LocaleUtil.ENGLISH, RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(), serviceContext);

		_resourcePermissionLocalService.setResourcePermissions(
			objectEntryFolder4.getCompanyId(),
			objectEntryFolder4.getModelClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntryFolder4.getObjectEntryFolderId()),
			_cmsAdministratorRole.getRoleId(), new String[] {ActionKeys.VIEW});

		resetPermissionBulkAction.setBulkActionItems(
			new BulkActionItem[] {
				_toBulkActionItem(
					objectEntry3.getModelClassName(), objectEntry3),
				_toBulkActionItem(objectEntryFolder4)
			});

		_postBulkAction(resetPermissionBulkAction);

		resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				objectEntry3.getCompanyId(), objectEntry3.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry3.getObjectEntryId()),
				_cmsAdministratorRole.getRoleId());

		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.DELETE));
		Assert.assertTrue(
			resourcePermission.hasActionId(ActionKeys.PERMISSIONS));
		Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.VIEW));

		resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				objectEntryFolder4.getCompanyId(),
				objectEntryFolder4.getModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntryFolder4.getObjectEntryFolderId()),
				_cmsAdministratorRole.getRoleId());

		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.ADD_ENTRY));
		Assert.assertFalse(resourcePermission.hasActionId(ActionKeys.DELETE));
		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.SUBSCRIBE));
	}

	private void _testPostBulkActionWithTypeTaxonomyCategory()
		throws Exception {

		TaxonomyCategoryBulkAction taxonomyCategoryBulkAction =
			new TaxonomyCategoryBulkAction();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_depotEntry2.getGroupId(), _cmsBasicWebContentObjectDefinition,
			_getObjectEntryValues());

		taxonomyCategoryBulkAction.setBulkActionItems(
			_toBulkActionItems(
				_cmsBasicWebContentObjectDefinition, objectEntry));

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

		taxonomyCategoryBulkAction.setTaxonomyCategoryIdsToAdd(
			taxonomyCategoryIds);

		taxonomyCategoryBulkAction.setType(
			BulkAction.Type.TAXONOMY_CATEGORY_BULK_ACTION);

		_postBulkAction(taxonomyCategoryBulkAction);

		Assert.assertArrayEquals(
			ArrayUtil.toArray(taxonomyCategoryIds),
			_assetCategoryLocalService.getCategoryIds(
				_cmsBasicWebContentObjectDefinition.getClassName(),
				objectEntry.getObjectEntryId()));
	}

	private BulkActionItem _toBulkActionItem(
		ObjectEntryFolder objectEntryFolder) {

		return new BulkActionItem() {
			{
				setClassExternalReferenceCode(
					objectEntryFolder.getExternalReferenceCode());
				setClassName(objectEntryFolder.getModelClassName());
				setClassPK(objectEntryFolder.getObjectEntryFolderId());
				setName(objectEntryFolder.getName());
			}
		};
	}

	private BulkActionItem _toBulkActionItem(
			String clazzName, ObjectEntry objectEntry)
		throws Exception {

		return new BulkActionItem() {
			{
				setClassExternalReferenceCode(
					objectEntry.getExternalReferenceCode());
				setClassName(clazzName);
				setClassPK(objectEntry.getObjectEntryId());
				setName(objectEntry.getTitleValue(_LANGUAGE_ID));
			}
		};
	}

	private BulkActionItem[] _toBulkActionItems(
			ObjectDefinition objectDefinition, Object... objects)
		throws Exception {

		return TransformUtil.transform(
			objects,
			object -> new BulkActionItem() {
				{
					if (object instanceof ObjectDefinition) {
						ObjectDefinition objectDefinition =
							(ObjectDefinition)object;

						setClassExternalReferenceCode(
							objectDefinition::getExternalReferenceCode);

						setClassName(ObjectDefinition.class.getName());
						setClassPK(objectDefinition::getObjectDefinitionId);
						setName(objectDefinition::getName);
					}
					else if (object instanceof ObjectEntry) {
						ObjectEntry objectEntry = (ObjectEntry)object;

						setClassExternalReferenceCode(
							objectEntry::getExternalReferenceCode);

						setClassName(objectDefinition::getClassName);
						setClassPK(objectEntry::getObjectEntryId);
						setName(objectEntry::getTitleValue);
					}
					else if (object instanceof ObjectEntryFolder) {
						ObjectEntryFolder objectEntryFolder =
							(ObjectEntryFolder)object;

						setClassExternalReferenceCode(
							objectEntryFolder::getExternalReferenceCode);
						setClassName(objectEntryFolder::getModelClassName);
						setClassPK(objectEntryFolder::getObjectEntryFolderId);
						setName(objectEntryFolder::getName);
					}
				}
			},
			BulkActionItem.class);
	}

	private void _waitForFinish(long bulkActionTaskId) throws Exception {
		while (true) {
			ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
				bulkActionTaskId);

			Map<String, Serializable> values = objectEntry.getValues();

			String executionStatus = (String)values.get("executionStatus");

			if (StringUtil.equals(
					executionStatus,
					BulkSelectionActionStatusConstants.COMPLETED) ||
				StringUtil.equals(
					executionStatus,
					BulkSelectionActionStatusConstants.FAILED)) {

				Assert.assertEquals(
					BulkSelectionActionStatusConstants.COMPLETED,
					executionStatus);

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

	private Role _cmsAdministratorRole;
	private ObjectDefinition _cmsBasicWebContentObjectDefinition;
	private ObjectDefinition _cmsBulkActionTaskObjectDefinition;
	private DepotEntry _depotEntry1;
	private DepotEntry _depotEntry2;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject(
		filter = "filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	private FilterFactory<Predicate> _filterFactory;

	@Inject
	private JSONFactory _jsonFactory;

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
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}