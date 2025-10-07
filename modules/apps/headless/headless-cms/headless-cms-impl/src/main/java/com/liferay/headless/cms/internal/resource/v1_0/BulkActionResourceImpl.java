/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.resource.v1_0;

import com.liferay.depot.model.DepotEntry;
import com.liferay.document.library.display.context.DLMimeTypeDisplayContext;
import com.liferay.headless.batch.engine.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.resource.v1_0.ImportTaskResource;
import com.liferay.headless.cms.dto.v1_0.BulkAction;
import com.liferay.headless.cms.dto.v1_0.BulkActionItem;
import com.liferay.headless.cms.dto.v1_0.BulkActionTask;
import com.liferay.headless.cms.dto.v1_0.DefaultPermissionBulkAction;
import com.liferay.headless.cms.dto.v1_0.DeleteBulkAction;
import com.liferay.headless.cms.dto.v1_0.KeywordBulkAction;
import com.liferay.headless.cms.dto.v1_0.PermissionBulkAction;
import com.liferay.headless.cms.dto.v1_0.TaxonomyCategoryBulkAction;
import com.liferay.headless.cms.internal.odata.entity.v1_0.BulkActionEntityModel;
import com.liferay.headless.cms.resource.v1_0.BulkActionResource;
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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
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
import com.liferay.trash.TrashHelper;

import jakarta.validation.ValidationException;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MultivaluedMap;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Crescenzo Rega
 * @author Thiago Buarque
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/bulk-action.properties",
	scope = ServiceScope.PROTOTYPE, service = BulkActionResource.class
)
public class BulkActionResourceImpl extends BaseBulkActionResourceImpl {

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public BulkActionTask postBulkAction(
			String search, Filter filter, BulkAction bulkAction)
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

	@Override
	public Page<BulkActionItem> postBulkActionItemPreviewPage(
			Boolean fetchChildren, String search, Filter filter,
			Pagination pagination, Sort[] sorts, BulkAction bulkAction)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-17564") ||
			!Objects.equals(
				bulkAction.getType(), BulkAction.Type.DELETE_BULK_ACTION)) {

			throw new UnsupportedOperationException();
		}

		if (ArrayUtil.isNotEmpty(sorts)) {
			Sort sort = sorts[0];

			String fieldName = sort.getFieldName();

			if ((!StringUtil.equalsIgnoreCase(fieldName, "name") &&
				 !StringUtil.equalsIgnoreCase(fieldName, "usages")) ||
				(sorts.length > 1)) {

				throw new BadRequestException(
					"Only the fields \"name\" and \"usages\" are sortable");
			}
		}
		else {
			sorts = new Sort[] {new Sort("usages", true)};
		}

		BulkActionItem[] bulkActionItems = bulkAction.getBulkActionItems();

		if (GetterUtil.getBoolean(fetchChildren)) {
			if (ArrayUtil.isEmpty(bulkActionItems)) {
				return Page.of(Collections.emptyList());
			}

			BulkActionItem bulkActionItem = bulkActionItems[0];

			if ((bulkActionItems.length > 1) ||
				!Objects.equals(
					bulkActionItem.getClassName(),
					ObjectEntryFolder.class.getName())) {

				throw new BadRequestException(
					"\"fetchChildren\" is only supported with a single folder");
			}

			return _getBulkActionItemPreviewPage(
				_getObjectEntryFolderBulkActionItems(
					bulkActionItem.getClassPK()),
				pagination, search, sorts[0]);
		}

		if (ArrayUtil.isEmpty(bulkActionItems) &&
			!GetterUtil.getBoolean(bulkAction.getSelectAll())) {

			return Page.of(Collections.emptyList());
		}

		if (!GetterUtil.getBoolean(bulkAction.getSelectAll())) {
			return _getBulkActionItemPreviewPage(
				ListUtil.fromArray(bulkActionItems), pagination, search,
				sorts[0]);
		}

		return _getBulkActionItemPreviewPage(
			filter, pagination, search, sorts[0]);
	}

	private BulkActionTask _addBulkActionTask(String type) throws Exception {
		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, contextUser.getUserId(), _getBulkActionTaskObjectDefinitionId(),
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

	private void _addBulkActionTaskItem(
			List<BulkActionItem> bulkActionItems, BulkActionTask bulkActionTask,
			Map.Entry<String, List<BulkActionItem>> entry,
			ImportTask importTask, String taskItemDelegateName)
		throws Exception {

		for (BulkActionItem bulkActionItem : entry.getValue()) {
			_objectEntryLocalService.addObjectEntry(
				0, contextUser.getUserId(),
				_getBulkActionTaskItemObjectDefinitionId(),
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
					"r_bulkActionTaskToBulkActionTaskItems_c_bulkActionTaskId",
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

	private ImportTaskResource _createImportTaskResource() {
		return _importTaskResourceFactory.create(
		).httpServletRequest(
			contextHttpServletRequest
		).httpServletResponse(
			contextHttpServletResponse
		).uriInfo(
			contextUriInfo
		).user(
			contextUser
		).build();
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

	private Page<BulkActionItem> _getBulkActionItemPreviewPage(
			Filter filter, Pagination pagination, String search, Sort sort)
		throws Exception {

		if (filter == null) {
			throw new ValidationException("Filter is null");
		}

		List<BulkActionItem> bulkActionItems = new ArrayList<>();

		DynamicServletRequest dynamicServletRequest = new DynamicServletRequest(
			contextHttpServletRequest);

		dynamicServletRequest.setParameter("nestedFields", "embedded");

		SearchResultResource searchResultResource =
			_searchResultResourceFactory.create(
			).httpServletRequest(
				dynamicServletRequest
			).httpServletResponse(
				contextHttpServletResponse
			).preferredLocale(
				contextUser.getLocale()
			).uriInfo(
				contextUriInfo
			).user(
				contextUser
			).build();

		if (StringUtil.equalsIgnoreCase(sort.getFieldName(), "name")) {
			sort.setFieldName(
				Field.getSortableFieldName(
					"localized_title_".concat(contextUser.getLanguageId())));
		}

		Page<SearchResult> searchPage = searchResultResource.getSearchPage(
			null, true, null, null, search, filter, pagination,
			new Sort[] {sort});

		for (SearchResult searchResult : searchPage.getItems()) {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				String.valueOf(searchResult.getEmbedded()));

			bulkActionItems.add(_toBulkActionItem(jsonObject.getLong("id")));
		}

		if (StringUtil.equalsIgnoreCase(sort.getFieldName(), "usages")) {
			bulkActionItems = _sortBulkActionItems(bulkActionItems, sort);
		}

		return Page.of(bulkActionItems, pagination, searchPage.getTotalCount());
	}

	private Page<BulkActionItem> _getBulkActionItemPreviewPage(
		List<BulkActionItem> bulkActionItems1, Pagination pagination,
		String search, Sort sort) {

		List<BulkActionItem> bulkActionItems2 = new ArrayList<>();

		long totalCount = bulkActionItems1.size();

		for (BulkActionItem bulkActionItem : bulkActionItems1) {
			bulkActionItems2.add(
				_toBulkActionItem(
					GetterUtil.getLong(bulkActionItem.getClassPK())));
		}

		if (Validator.isNotNull(search)) {
			bulkActionItems2 = ListUtil.filter(
				bulkActionItems2,
				bulkActionItem -> StringUtil.containsIgnoreCase(
					bulkActionItem.getName(), search, StringPool.BLANK));

			totalCount = bulkActionItems2.size();
		}

		return Page.of(
			_sortBulkActionItems(bulkActionItems2, sort), pagination,
			totalCount);
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

	private long _getBulkActionTaskItemObjectDefinitionId() throws Exception {
		if (_bulkActionTaskItemObjectDefinition != null) {
			return _bulkActionTaskItemObjectDefinition.getObjectDefinitionId();
		}

		_bulkActionTaskItemObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_BULK_ACTION_TASK_ITEM", contextCompany.getCompanyId());

		return _bulkActionTaskItemObjectDefinition.getObjectDefinitionId();
	}

	private long _getBulkActionTaskObjectDefinitionId() throws Exception {
		if (_bulkActionTaskObjectDefinition != null) {
			return _bulkActionTaskObjectDefinition.getObjectDefinitionId();
		}

		_bulkActionTaskObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_BULK_ACTION_TASK", contextCompany.getCompanyId());

		return _bulkActionTaskObjectDefinition.getObjectDefinitionId();
	}

	private String _getClassName(String key) {
		if (StringUtil.equals(key, DepotEntry.class.getName())) {
			return "com.liferay.headless.asset.library.dto.v1_0.AssetLibrary";
		}
		else if (StringUtil.equals(key, ObjectEntryFolder.class.getName())) {
			return "com.liferay.headless.object.dto.v1_0.ObjectEntryFolder";
		}

		return "com.liferay.object.rest.dto.v1_0.ObjectEntry";
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
				"L_BASIC_WEB_CONTENT")) {

			return "basic-web-content";
		}
		else if (Objects.equals(
					objectDefinition.getExternalReferenceCode(), "L_BLOG")) {

			return "blog";
		}
		else if (Objects.equals(
					objectDefinition.getExternalReferenceCode(),
					"L_KNOWLEDGE_BASE")) {

			return "knowledge-base";
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

	private String _getTaskItemDelegateName(String className) throws Exception {
		if (StringUtil.equals(DepotEntry.class.getName(), className) ||
			StringUtil.equals(ObjectEntryFolder.class.getName(), className)) {

			return null;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinitionByClassName(
				contextCompany.getCompanyId(), className);

		return objectDefinition.getName();
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

	private ObjectDefinition _bulkActionTaskItemObjectDefinition;
	private ObjectDefinition _bulkActionTaskObjectDefinition;

	@Reference
	private DLMimeTypeDisplayContext _dlMimeTypeDisplayContext;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private ImportTaskResource.Factory _importTaskResourceFactory;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

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

}