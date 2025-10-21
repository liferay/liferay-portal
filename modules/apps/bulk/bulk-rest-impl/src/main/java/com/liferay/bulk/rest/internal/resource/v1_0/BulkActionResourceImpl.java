/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.internal.resource.v1_0;

import com.liferay.bulk.rest.dto.v1_0.BulkAction;
import com.liferay.bulk.rest.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.dto.v1_0.BulkActionTask;
import com.liferay.bulk.rest.dto.v1_0.DefaultPermissionBulkAction;
import com.liferay.bulk.rest.dto.v1_0.DeleteBulkAction;
import com.liferay.bulk.rest.dto.v1_0.KeywordBulkAction;
import com.liferay.bulk.rest.dto.v1_0.PermissionBulkAction;
import com.liferay.bulk.rest.dto.v1_0.TaxonomyCategoryBulkAction;
import com.liferay.bulk.rest.internal.odata.entity.v1_0.BulkActionEntityModel;
import com.liferay.bulk.rest.resource.v1_0.BulkActionResource;

import com.liferay.document.library.display.context.DLMimeTypeDisplayContext;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryVersionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.trash.helper.TrashHelper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.rest.dto.v1_0.SearchResult;
import com.liferay.portal.search.rest.resource.v1_0.SearchResultResource;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.permission.Permission;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.MultivaluedMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/bulk-action.properties",
	scope = ServiceScope.PROTOTYPE, service = BulkActionResource.class
)
public class BulkActionResourceImpl extends BaseBulkActionResourceImpl {

	@Override
	public BulkActionTask postBulkAction(
		String search, Filter filter,
		BulkAction bulkAction)
		throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(
			contextCompany.getCompanyId(), "LPD-17564")) {

			throw new UnsupportedOperationException();
		}

		BulkAction.Type type = bulkAction.getType();

		if (BulkAction.Type.DEFAULT_PERMISSION_BULK_ACTION.equals(type)) {
			DefaultPermissionBulkAction defaultPermissionBulkAction =
				(DefaultPermissionBulkAction)bulkAction;

			return _executeDefaultPermissionBulkAction(
				defaultPermissionBulkAction,
				_getDefaultPermissionBulkActionItemsMap(
					bulkAction.getBulkActionItems(),
					GetterUtil.getLong(
						defaultPermissionBulkAction.getDepotGroupId()),
					GetterUtil.getBoolean(bulkAction.getSelectAll()),
					GetterUtil.getString(
						defaultPermissionBulkAction.getTreePath())));
		}
		else if (BulkAction.Type.DELETE_BULK_ACTION.equals(type)) {
			return _executeDeleteBulkAction(
				bulkAction,
				_getBulkActionItemsMap(
					bulkAction.getBulkActionItems(), filter, search,
					GetterUtil.getBoolean(bulkAction.getSelectAll())));
		}
		else if (BulkAction.Type.KEYWORD_BULK_ACTION.equals(type)) {
			return _executeKeywordBulkAction(
				bulkAction,
				_getBulkActionItemsMap(
					bulkAction.getBulkActionItems(), filter, search,
					GetterUtil.getBoolean(bulkAction.getSelectAll())));
		}
		else if (BulkAction.Type.PERMISSION_BULK_ACTION.equals(type)) {
			return _executePermissionBulkAction(
				bulkAction,
				_getBulkActionItemsMap(
					bulkAction.getBulkActionItems(), filter, search,
					GetterUtil.getBoolean(bulkAction.getSelectAll())));
		}
		else if (BulkAction.Type.TAXONOMY_CATEGORY_BULK_ACTION.equals(type)) {
			return _executeTaxonomyCategoryBulkAction(
				bulkAction,
				_getBulkActionItemsMap(
					bulkAction.getBulkActionItems(), filter, search,
					GetterUtil.getBoolean(bulkAction.getSelectAll())));
		}

		throw new UnsupportedOperationException();
	}

	private Map<String, List<BulkActionItem>> _getBulkActionItemsMap(
		BulkActionItem[] bulkActionItems, Filter filter, String search,
		boolean selectAll)
		throws Exception {

		Map<String, List<BulkActionItem>> bulkActionItemsMap = new HashMap<>();

		if (selectAll && ArrayUtil.isEmpty(bulkActionItems)) {
			if (filter == null) {
				throw new ValidationException("Filter is null");
			}

			SearchResultResource searchResultResource =
				_searchResultResourceFactory.create(
				).httpServletRequest(
					contextHttpServletRequest
				).httpServletResponse(
					contextHttpServletResponse
				).preferredLocale(
					contextAcceptLanguage.getPreferredLocale()
				).uriInfo(
					contextUriInfo
				).user(
					contextUser
				).build();

			int pageSize = 500;

			for (int page = 1;; page++) {
				Page<SearchResult> searchPage =
					searchResultResource.getSearchPage(
						null, true, null, null, search, filter,
						Pagination.of(page, pageSize), null);

				for (SearchResult searchResult : searchPage.getItems()) {
					JSONObject jsonObject = _jsonFactory.createJSONObject(
						String.valueOf(searchResult.getEmbedded()));

					bulkActionItemsMap.computeIfAbsent(
						searchResult.getEntryClassName(),
						className -> new ArrayList<>()
					).add(
						new BulkActionItem() {
							{
								setClassExternalReferenceCode(
									() -> jsonObject.getString(
										"externalReferenceCode"));
								setClassPK(() -> jsonObject.getLong("id"));
							}
						}
					);
				}

				if ((page * pageSize) >= searchPage.getTotalCount()) {
					break;
				}
			}

			return bulkActionItemsMap;
		}

		if (ArrayUtil.isEmpty(bulkActionItems)) {
			return bulkActionItemsMap;
		}

		for (BulkActionItem bulkActionItem : bulkActionItems) {
			bulkActionItemsMap.computeIfAbsent(
				bulkActionItem.getClassName(), className -> new ArrayList<>()
			).add(
				bulkActionItem
			);
		}

		return bulkActionItemsMap;
	}

	private long _getCMSBulkActionTaskItemObjectDefinitionId()
		throws Exception {

		if (_cmsBulkActionTaskItemObjectDefinition != null) {
			return _cmsBulkActionTaskItemObjectDefinition.
				getObjectDefinitionId();
		}

		_cmsBulkActionTaskItemObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BULK_ACTION_TASK_ITEM",
					contextCompany.getCompanyId());

		return _cmsBulkActionTaskItemObjectDefinition.getObjectDefinitionId();
	}

	private void _addBulkActionTaskItem(
		List<BulkActionItem> bulkActionItems, BulkActionTask bulkActionTask,
		Map.Entry<String, List<BulkActionItem>> entry,
		ImportTask importTask, String taskItemDelegateName)
		throws Exception {

		for (BulkActionItem bulkActionItem : entry.getValue()) {
			_objectEntryLocalService.addObjectEntry(
				0, contextUser.getUserId(),
				_getCMSBulkActionTaskItemObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"classExternalReferenceCode",
					bulkActionItem.getClassExternalReferenceCode()
				).put(
					"classPK", bulkActionItem.getClassPK()
				).put(
					"executionStatus",
					StringUtil.toLowerCase(
						importTask.getExecuteStatusAsString())
				).put(
					"importTaskId", importTask.getId()
				).put(
					"name", bulkActionItem.getName()
				).put(
					"r_cmsBATaskToCMSBATaskItems_c_cmsBulkActionTaskId",
					bulkActionTask.getId()
				).put(
					"type",
					(taskItemDelegateName != null) ? taskItemDelegateName :
						"ObjectEntryFolder"
				).build(),
				new ServiceContext());

			bulkActionItems.add(bulkActionItem);
		}
	}

	private long _getCMSBulkActionTaskObjectDefinitionId() throws Exception {
		if (_cmsBulkActionTaskObjectDefinition != null) {
			return _cmsBulkActionTaskObjectDefinition.getObjectDefinitionId();
		}

		_cmsBulkActionTaskObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BULK_ACTION_TASK", contextCompany.getCompanyId());

		return _cmsBulkActionTaskObjectDefinition.getObjectDefinitionId();
	}

	private BulkActionTask _addBulkActionTask(String type) throws Exception {
		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, contextUser.getUserId(),
			_getCMSBulkActionTaskObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"actionName", type
			).put(
				"executionStatus", "initial"
			).put(
				"type", type
			).build(),
			new ServiceContext());

		Map<String, Serializable> values = objectEntry.getValues();

		return new BulkActionTask() {
			{
				setActionName(
					() -> GetterUtil.getString(values.get("actionName")));
				setAuthor(objectEntry::getUserName);
				setCreatedDate(objectEntry::getCreateDate);
				setExecuteStatus(
					() -> GetterUtil.getString(values.get("executionStatus")));
				setExternalReferenceCode(objectEntry::getExternalReferenceCode);
				setId(objectEntry::getObjectEntryId);
				setType(() -> GetterUtil.getString(values.get("type")));
			}
		};
	}

	private BulkActionTask _executeDefaultPermissionBulkAction(
		DefaultPermissionBulkAction defaultPermissionBulkAction,
		Map<String, List<BulkActionItem>> bulkActionItemsMap)
		throws Exception {

		String defaultPermissions =
			defaultPermissionBulkAction.getDefaultPermissions();

		if (MapUtil.isEmpty(bulkActionItemsMap) ||
			Validator.isNull(defaultPermissions)) {

			return new BulkActionTask();
		}

		BulkActionTask bulkActionTask = _addBulkActionTask(
			defaultPermissionBulkAction.getTypeAsString());

		List<BulkActionItem> bulkActionItems = new ArrayList<>();
		ImportTaskResource importTaskResource = _createImportTaskResource();

		for (Map.Entry<String, List<BulkActionItem>> entry :
			bulkActionItemsMap.entrySet()) {

			String taskItemDelegateName = _getTaskItemDelegateName(
				entry.getKey());

			ImportTask importTask = importTaskResource.putImportTaskObject(
				_getClassName(entry.getKey()), null, null,
				ImportTask.ImportStrategy.ON_ERROR_CONTINUE.getValue(),
				taskItemDelegateName, "PARTIAL_UPDATE",
				transform(
					entry.getValue(),
					bulkActionItem -> HashMapBuilder.<String, Object>put(
						"defaultPermissions", defaultPermissions
					).put(
						"id", bulkActionItem.getClassPK()
					).build()));

			_addBulkActionTaskItem(
				bulkActionItems, bulkActionTask, entry, importTask,
				taskItemDelegateName);
		}

		bulkActionTask.setNumberOfItems(bulkActionItems::size);

		return bulkActionTask;
	}

	private BulkActionTask _executeDeleteBulkAction(
		BulkAction bulkAction,
		Map<String, List<BulkActionItem>> bulkActionItemsMap)
		throws Exception {

		if (MapUtil.isEmpty(bulkActionItemsMap)) {
			return new BulkActionTask();
		}

		DeleteBulkAction deleteBulkAction = (DeleteBulkAction)bulkAction;

		BulkActionTask bulkActionTask = _addBulkActionTask(
			deleteBulkAction.getTypeAsString());

		List<BulkActionItem> bulkActionItems = new ArrayList<>();

		ImportTaskResource importTaskResource = _createImportTaskResource();

		for (Map.Entry<String, List<BulkActionItem>> entry :
			bulkActionItemsMap.entrySet()) {

			String taskItemDelegateName = _getTaskItemDelegateName(
				entry.getKey());

			ImportTask importTask = importTaskResource.deleteImportTaskObject(
				_getClassName(entry.getKey()), null, null,
				ImportTask.ImportStrategy.ON_ERROR_CONTINUE.getValue(),
				taskItemDelegateName,
				transform(
					entry.getValue(),
					bulkActionItem -> HashMapBuilder.<String, Object>put(
						"id", bulkActionItem.getClassPK()
					).build()));

			_addBulkActionTaskItem(
				bulkActionItems, bulkActionTask, entry, importTask,
				taskItemDelegateName);
		}

		bulkActionTask.setNumberOfItems(bulkActionItems::size);

		return bulkActionTask;
	}

	private BulkActionTask _executeKeywordBulkAction(
		BulkAction bulkAction,
		Map<String, List<BulkActionItem>> bulkActionItemsMap)
		throws Exception {

		KeywordBulkAction keywordBulkAction = (KeywordBulkAction)bulkAction;

		String[] keywords = keywordBulkAction.getKeywords();

		if (MapUtil.isEmpty(bulkActionItemsMap) ||
			ArrayUtil.isEmpty(keywords)) {

			return new BulkActionTask();
		}

		BulkActionTask bulkActionTask = _addBulkActionTask(
			keywordBulkAction.getTypeAsString());

		List<BulkActionItem> bulkActionItems = new ArrayList<>();
		ImportTaskResource importTaskResource = _createImportTaskResource();

		for (Map.Entry<String, List<BulkActionItem>> entry :
			bulkActionItemsMap.entrySet()) {

			if (StringUtil.equals(
				"com.liferay.object.model.ObjectEntryFolder",
				entry.getKey())) {

				continue;
			}

			String taskItemDelegateName = _getTaskItemDelegateName(
				entry.getKey());

			ImportTask importTask = importTaskResource.putImportTaskObject(
				_getClassName(entry.getKey()), null, null,
				ImportTask.ImportStrategy.ON_ERROR_CONTINUE.getValue(),
				taskItemDelegateName, "PARTIAL_UPDATE",
				transform(
					entry.getValue(),
					bulkActionItem -> HashMapBuilder.<String, Object>put(
						"id", bulkActionItem.getClassPK()
					).put(
						"keywords", keywords
					).build()));

			_addBulkActionTaskItem(
				bulkActionItems, bulkActionTask, entry, importTask,
				taskItemDelegateName);
		}

		bulkActionTask.setNumberOfItems(bulkActionItems::size);

		return bulkActionTask;
	}

	private BulkActionTask _executePermissionBulkAction(
		BulkAction bulkAction,
		Map<String, List<BulkActionItem>> bulkActionItemsMap)
		throws Exception {

		PermissionBulkAction permissionBulkAction =
			(PermissionBulkAction)bulkAction;

		String configuration = permissionBulkAction.getConfiguration();
		Permission[] permissions = permissionBulkAction.getPermissions();

		if (MapUtil.isEmpty(bulkActionItemsMap) ||
			(Validator.isNull(configuration) &&
			 ArrayUtil.isEmpty(permissions))) {

			return new BulkActionTask();
		}

		BulkActionTask bulkActionTask = _addBulkActionTask(
			permissionBulkAction.getTypeAsString());

		List<BulkActionItem> bulkActionItems = new ArrayList<>();
		JSONObject configurationJSONObject = _jsonFactory.createJSONObject(
			GetterUtil.get(configuration, "{}"));
		ImportTaskResource importTaskResource = _createImportTaskResource();
		Map<String, Role> roles = new HashMap<>();

		for (Map.Entry<String, List<BulkActionItem>> entry :
			bulkActionItemsMap.entrySet()) {

			String taskItemDelegateName = _getTaskItemDelegateName(
				entry.getKey());

			List<HashMap<String, Object>> permissionsList = _getPermissionsList(
				configurationJSONObject, entry, permissions, roles);

			if (ListUtil.isEmpty(permissionsList)) {
				continue;
			}

			ImportTask importTask = importTaskResource.putImportTaskObject(
				_getClassName(entry.getKey()), null, null,
				ImportTask.ImportStrategy.ON_ERROR_CONTINUE.getValue(),
				taskItemDelegateName, "PARTIAL_UPDATE",
				transform(
					entry.getValue(),
					bulkActionItem -> HashMapBuilder.<String, Object>put(
						"id", bulkActionItem.getClassPK()
					).put(
						"permissions", permissionsList
					).build()));

			_addBulkActionTaskItem(
				bulkActionItems, bulkActionTask, entry, importTask,
				taskItemDelegateName);
		}

		bulkActionTask.setNumberOfItems(bulkActionItems::size);

		return bulkActionTask;
	}

	private BulkActionTask _executeTaxonomyCategoryBulkAction(
		BulkAction bulkAction,
		Map<String, List<BulkActionItem>> bulkActionItemsMap)
		throws Exception {

		TaxonomyCategoryBulkAction taxonomyCategoryBulkAction =
			(TaxonomyCategoryBulkAction)bulkAction;

		Long[] taxonomyCategoryIds =
			taxonomyCategoryBulkAction.getTaxonomyCategoryIds();

		if (MapUtil.isEmpty(bulkActionItemsMap) ||
			ArrayUtil.isEmpty(taxonomyCategoryIds)) {

			return new BulkActionTask();
		}

		BulkActionTask bulkActionTask = _addBulkActionTask(
			taxonomyCategoryBulkAction.getTypeAsString());

		List<BulkActionItem> bulkActionItems = new ArrayList<>();
		ImportTaskResource importTaskResource = _createImportTaskResource();

		for (Map.Entry<String, List<BulkActionItem>> entry :
			bulkActionItemsMap.entrySet()) {

			if (StringUtil.equals(
				"com.liferay.object.model.ObjectEntryFolder",
				entry.getKey())) {

				continue;
			}

			String taskItemDelegateName = _getTaskItemDelegateName(
				entry.getKey());

			ImportTask importTask = importTaskResource.putImportTaskObject(
				_getClassName(entry.getKey()), null, null,
				ImportTask.ImportStrategy.ON_ERROR_CONTINUE.getValue(),
				taskItemDelegateName, "PARTIAL_UPDATE",
				transform(
					entry.getValue(),
					bulkActionItem -> HashMapBuilder.<String, Object>put(
						"id", bulkActionItem.getClassPK()
					).put(
						"taxonomyCategoryIds", taxonomyCategoryIds
					).build()));

			_addBulkActionTaskItem(
				bulkActionItems, bulkActionTask, entry, importTask,
				taskItemDelegateName);
		}

		bulkActionTask.setNumberOfItems(bulkActionItems::size);

		return bulkActionTask;
	}

	private Map<String, List<BulkActionItem>>
	_getDefaultPermissionBulkActionItemsMap(
		BulkActionItem[] bulkActionItems, long depotGroupId,
		boolean selectAll, String treePath)
		throws Exception {

		Map<String, List<BulkActionItem>> bulkActionItemsMap = new HashMap<>();

		if (selectAll && ArrayUtil.isEmpty(bulkActionItems)) {
			if ((depotGroupId == 0) && Validator.isNull(treePath)) {
				throw new ValidationException();
			}

			String filterString = StringBundler.concat(
				"(className eq '", ObjectEntryFolder.class.getName(),
				"') and ");

			if (Validator.isNull(treePath)) {
				filterString = StringBundler.concat(
					filterString, "(depotGroupId eq ", depotGroupId, ")");
			}
			else {
				filterString = StringBundler.concat(
					filterString, "(startswith(treePath, '", treePath, "'))");
			}

			return _populateDefaultPermissionBulkActionItemsMap(
				bulkActionItemsMap, filterString);
		}

		if (ArrayUtil.isEmpty(bulkActionItems)) {
			return bulkActionItemsMap;
		}

		BulkActionItem bulkActionItem = bulkActionItems[0];

		String filterString = StringBundler.concat(
			"(className eq '", bulkActionItem.getClassName(), "') and (",
			StringUtil.merge(
				transform(
					bulkActionItems,
					item ->
						"(classExternalReferenceCode eq '" +
						item.getClassExternalReferenceCode() + "')",
					String.class),
				" or "),
			")");

		return _populateDefaultPermissionBulkActionItemsMap(
			bulkActionItemsMap, filterString);
	}

	private String _getDeletionType(long groupId) throws PortalException {
		if (_trashHelper.isTrashEnabled(groupId)) {
			return "RECYCLE_BIN";
		}

		return "PERMANENT_DELETION";
	}

	private String _getMimeType(
		ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws Exception {

		if (Objects.equals(
			objectDefinition.getExternalReferenceCode(),
			"L_CMS_BASIC_WEB_CONTENT")) {

			return "basic-web-content";
		}
		else if (Objects.equals(
			objectDefinition.getExternalReferenceCode(),
			"L_CMS_BLOG")) {

			return "blog";
		}

		ObjectEntryVersion objectEntryVersion =
			_objectEntryVersionLocalService.getObjectEntryVersion(
				objectEntry.getObjectEntryId(), objectEntry.getVersion());

		JSONObject contentJSONObject = _jsonFactory.createJSONObject(
			objectEntryVersion.getContent());

		JSONObject propertiesJSONObject = contentJSONObject.getJSONObject(
			"properties");

		JSONObject fileJSONObject = propertiesJSONObject.getJSONObject("file");

		if (fileJSONObject != null) {
			return _dlMimeTypeDisplayContext.getIconFileMimeType(
				fileJSONObject.getString("mimeType"));
		}

		return "custom-structure";
	}

	private List<BulkActionItem> _getObjectEntryFolderBulkActionItems(
		long objectObjectEntryFolderId)
		throws Exception {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.getObjectEntryFolder(
				objectObjectEntryFolderId);

		List<BulkActionItem> bulkActionItems = transform(
			_objectEntryLocalService.getObjectEntryFolderObjectEntries(
				objectEntryFolder.getGroupId(), objectObjectEntryFolderId,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			this::_toBulkActionItem);

		bulkActionItems.addAll(
			transform(
				_objectEntryFolderLocalService.getObjectEntryFolders(
					objectEntryFolder.getGroupId(),
					objectEntryFolder.getCompanyId(), objectObjectEntryFolderId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				this::_toBulkActionItem));

		return bulkActionItems;
	}

	private List<HashMap<String, Object>> _getPermissionsList(
		JSONObject configurationJSONObject,
		Map.Entry<String, List<BulkActionItem>> entry, Permission[] permissions,
		Map<String, Role> roles) {

		if (ArrayUtil.isNotEmpty(permissions)) {
			return transformToList(
				permissions,
				permission -> HashMapBuilder.<String, Object>put(
					"actionIds", ListUtil.fromArray(permission.getActionIds())
				).put(
					"roleExternalReferenceCode",
					permission.getRoleExternalReferenceCode()
				).put(
					"roleName", permission.getRoleName()
				).put(
					"roleType", permission.getRoleType()
				).build());
		}

		JSONObject jsonObject = null;
		List<String> resourceActions = null;

		if (Objects.equals(entry.getKey(), ObjectEntryFolder.class.getName())) {
			jsonObject = configurationJSONObject.getJSONObject(
				"OBJECT_ENTRY_FOLDERS");
			resourceActions = ResourceActionsUtil.getResourceActions(
				ObjectEntryFolder.class.getName());
		}
		else {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
					contextCompany.getCompanyId(), entry.getKey());

			if (objectDefinition == null) {
				return null;
			}

			if (Objects.equals(
				objectDefinition.getObjectFolderExternalReferenceCode(),
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES)) {

				jsonObject = configurationJSONObject.getJSONObject(
					ObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_CONTENTS);
			}
			else if (Objects.equals(
				objectDefinition.getObjectFolderExternalReferenceCode(),
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_FILE_TYPES)) {

				jsonObject = configurationJSONObject.getJSONObject(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES);
			}

			resourceActions = ResourceActionsUtil.getResourceActions(
				objectDefinition.getClassName());
		}

		if (jsonObject == null) {
			return null;
		}

		List<HashMap<String, Object>> permissionsList = new ArrayList<>();

		JSONObject finalJSONObject = jsonObject;
		List<String> finalResourceActions = resourceActions;

		Iterator<String> iterator = jsonObject.keys();

		iterator.forEachRemaining(
			key -> {
				if (!roles.containsKey(key)) {
					Role role = _roleLocalService.fetchRole(
						contextCompany.getCompanyId(), key);

					if (role == null) {
						return;
					}

					roles.put(key, role);
				}

				Role role = roles.get(key);

				permissionsList.add(
					HashMapBuilder.<String, Object>put(
						"actionIds",
						ListUtil.fromArray(
							ArrayUtil.filter(
								JSONUtil.toStringArray(
									finalJSONObject.getJSONArray(key)),
								action -> finalResourceActions.contains(
									action)))
					).put(
						"roleExternalReferenceCode",
						role.getExternalReferenceCode()
					).put(
						"roleName", role.getName()
					).put(
						"roleType", role.getType()
					).build());
			});

		return permissionsList;
	}

	private Map<String, List<BulkActionItem>>
	_populateDefaultPermissionBulkActionItemsMap(
		Map<String, List<BulkActionItem>> bulkActionItemsMap,
		String filterString)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_DEFAULT_PERMISSION", contextCompany.getCompanyId());

		Predicate predicate = _filterFactory.create(
			filterString, objectDefinition);

		List<Long> primaryKeys = _objectEntryLocalService.getPrimaryKeys(
			new Long[0], contextCompany.getCompanyId(), contextUser.getUserId(),
			objectDefinition.getObjectDefinitionId(), predicate, false, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		if (ListUtil.isEmpty(primaryKeys)) {
			return bulkActionItemsMap;
		}

		for (long primaryKey : primaryKeys) {
			bulkActionItemsMap.computeIfAbsent(
				objectDefinition.getClassName(), className -> new ArrayList<>()
			).add(
				new BulkActionItem() {
					{
						setClassPK(() -> primaryKey);
					}
				}
			);
		}

		return bulkActionItemsMap;
	}

	private List<BulkActionItem> _sortBulkActionItems(
		List<BulkActionItem> bulkActionItems, Sort sort) {

		return ListUtil.sort(
			bulkActionItems,
			(bulkActionItem1, bulkActionItem2) -> {
				if (StringUtil.equalsIgnoreCase(sort.getFieldName(), "name")) {
					String name = bulkActionItem1.getName();

					int value = name.compareTo(bulkActionItem2.getName());

					if (!sort.isReverse()) {
						return value;
					}

					return -value;
				}

				Map<String, Object> attributes1 =
					bulkActionItem1.getAttributes();

				Long usages = GetterUtil.getLong(attributes1.get("usages"));

				Map<String, Object> attributes2 =
					bulkActionItem2.getAttributes();

				int value = usages.compareTo(
					GetterUtil.getLong(attributes2.get("usages")));

				if (!sort.isReverse()) {
					return value;
				}

				return -value;
			});
	}

	private BulkActionItem _toBulkActionItem(long classPK) {
		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			classPK);

		if (objectEntry != null) {
			return _toBulkActionItem(objectEntry);
		}

		return _toBulkActionItem(
			_objectEntryFolderLocalService.fetchObjectEntryFolder(classPK));
	}

	private BulkActionItem _toBulkActionItem(ObjectEntry objectEntry) {
		BulkActionItem bulkActionItem = new BulkActionItem();

		bulkActionItem.setClassPK(objectEntry::getObjectEntryId);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		bulkActionItem.setAttributes(
			() -> HashMapBuilder.<String, Object>put(
				"deletionType", () -> _getDeletionType(objectEntry.getGroupId())
			).put(
				"mimeType", _getMimeType(objectDefinition, objectEntry)
			).put(
				"type", "ASSET"
			).put(
				"usages",
				_getUsagesCount(
					objectDefinition.getClassName(),
					objectDefinition.getObjectDefinitionId(),
					objectEntry.getObjectEntryId())
			).build());

		bulkActionItem.setClassExternalReferenceCode(
			objectEntry::getExternalReferenceCode);
		bulkActionItem.setClassName(objectDefinition::getClassName);
		bulkActionItem.setName(
			() -> objectEntry.getTitleValue(
				LocaleUtil.toLanguageId(contextUser.getLocale()), true));

		return bulkActionItem;
	}

	private long _getUsagesCount(
		String className, long objectDefinitionId, long objectEntryId)
		throws Exception {

		int usagesCount =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesCount(
					_portal.getClassNameId(className), objectEntryId);

		boolean skipObjectEntryResourcePermission =
			ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission();

		try {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

			List<ObjectRelationship> objectRelationships =
				_objectRelationshipLocalService.getObjectRelationships(
					objectDefinitionId);

			for (ObjectRelationship objectRelationship : objectRelationships) {
				ObjectRelatedModelsProvider objectRelatedModelsProvider =
					_objectRelatedModelsProviderRegistry.
						getObjectRelatedModelsProvider(
							className, contextCompany.getCompanyId(),
							objectRelationship.getType());

				usagesCount +=
					objectRelatedModelsProvider.getRelatedModelsCount(
						0, objectRelationship.getObjectRelationshipId(), null,
						objectEntryId, null);
			}
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(
				skipObjectEntryResourcePermission);
		}

		return usagesCount;
	}

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	private BulkActionItem _toBulkActionItem(
		ObjectEntryFolder objectEntryFolder) {

		BulkActionItem bulkActionItem = new BulkActionItem();

		bulkActionItem.setClassPK(objectEntryFolder::getObjectEntryFolderId);

		bulkActionItem.setAttributes(
			() -> HashMapBuilder.<String, Object>put(
				"deletionType",
				() -> _getDeletionType(objectEntryFolder.getGroupId())
			).put(
				"itemsCount",
				() -> {
					long itemsCount =
						_objectEntryFolderLocalService.
							getObjectEntryFoldersCount(
								objectEntryFolder.getGroupId(),
								objectEntryFolder.getCompanyId(),
								objectEntryFolder.getObjectEntryFolderId());

					itemsCount +=
						_objectEntryLocalService.
							getObjectEntryFolderObjectEntriesCount(
								objectEntryFolder.getGroupId(),
								objectEntryFolder.getObjectEntryFolderId());

					return itemsCount;
				}
			).put(
				"type", "FOLDER"
			).build());
		bulkActionItem.setClassExternalReferenceCode(
			objectEntryFolder::getExternalReferenceCode);
		bulkActionItem.setClassName(objectEntryFolder::getModelClassName);
		bulkActionItem.setName(objectEntryFolder::getName);

		return bulkActionItem;
	}

	private static final EntityModel _entityModel = new BulkActionEntityModel();

	private ObjectDefinition _cmsBulkActionTaskItemObjectDefinition;
	private ObjectDefinition _cmsBulkActionTaskObjectDefinition;

	@Reference
	private DLMimeTypeDisplayContext _dlMimeTypeDisplayContext;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectEntryVersionLocalService _objectEntryVersionLocalService;

	@Reference
	private ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SearchResultResource.Factory _searchResultResourceFactory;

	@Reference
	private TrashHelper _trashHelper;

	@Override
	public Page<BulkActionItem> postBulkActionItemPreviewPage(
		Boolean fetchChildren, String search, Filter filter,
		Pagination pagination, Sort[] sorts, BulkAction bulkAction)
		throws Exception {
		return super.postBulkActionItemPreviewPage(
			fetchChildren, search, filter, pagination, sorts, bulkAction);
	}
}