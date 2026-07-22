/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.internal.resource.v1_0;

import com.liferay.bulk.rest.dto.v1_0.AddObjectToProjectBulkSelectionAction;
import com.liferay.bulk.rest.dto.v1_0.AssignStructureDefaultWorkflowBulkSelectionAction;
import com.liferay.bulk.rest.dto.v1_0.AssignToObjectBulkSelectionAction;
import com.liferay.bulk.rest.dto.v1_0.BulkAction;
import com.liferay.bulk.rest.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.dto.v1_0.BulkActionTask;
import com.liferay.bulk.rest.dto.v1_0.CopyObjectBulkSelectionAction;
import com.liferay.bulk.rest.dto.v1_0.DefaultPermissionObjectBulkSelectionAction;
import com.liferay.bulk.rest.dto.v1_0.DeleteObjectAssetVersionBulkSelectionAction;
import com.liferay.bulk.rest.dto.v1_0.DeleteObjectBulkSelectionAction;
import com.liferay.bulk.rest.dto.v1_0.DueDateObjectBulkSelectionAction;
import com.liferay.bulk.rest.dto.v1_0.EditObjectCategoriesBulkSelectionAction;
import com.liferay.bulk.rest.dto.v1_0.EditObjectTagsBulkSelectionAction;
import com.liferay.bulk.rest.dto.v1_0.MoveObjectBulkSelectionAction;
import com.liferay.bulk.rest.dto.v1_0.PermissionObjectBulkSelectionAction;
import com.liferay.bulk.rest.dto.v1_0.SelectionScope;
import com.liferay.bulk.rest.dto.v1_0.StatusObjectBulkSelectionAction;
import com.liferay.bulk.rest.dto.v1_0.UpdateObjectValuesBulkSelectionAction;
import com.liferay.bulk.rest.internal.odata.entity.v1_0.BulkActionEntityModel;
import com.liferay.bulk.rest.internal.selection.v1_0.BulkActionBulkSelectionFactory;
import com.liferay.bulk.rest.resource.v1_0.BulkActionResource;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.bulk.selection.BulkSelectionFactoryRegistry;
import com.liferay.bulk.selection.BulkSelectionRunner;
import com.liferay.bulk.selection.constants.BulkSelectionActionStatusConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.document.library.display.context.DLMimeTypeDisplayContext;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.QueryTerm;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.rest.dto.v1_0.SearchResult;
import com.liferay.portal.search.rest.resource.v1_0.SearchResultResource;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
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
 * @author Alejandro Tardín
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
			String blueprintExternalReferenceCode, Boolean emptySearch,
			String entryClassNames, String scope, String search, Filter filter,
			Pagination pagination, Sort[] sorts, BulkAction bulkAction)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-17564") &&
			!BulkAction.Type.DELETE_OBJECT_ENTRY_BULK_SELECTION_ACTION.equals(
				bulkAction.getType())) {

			throw new UnsupportedOperationException();
		}

		BulkActionBulkSelectionFactory bulkActionBulkSelectionFactory =
			_getBulkActionBulkSelectionFactory(
				blueprintExternalReferenceCode, bulkAction, emptySearch,
				entryClassNames, filter, scope, search, sorts);

		BulkSelection<Object> bulkSelection =
			bulkActionBulkSelectionFactory.create();

		if (bulkSelection.getSize() == 0) {
			return new BulkActionTask();
		}

		BulkActionTask bulkActionTask = _addBulkActionTask(
			bulkAction, bulkSelection.getSize());

		BulkAction.Type type = bulkAction.getType();

		_bulkSelectionRunner.run(
			contextUser, bulkSelection, _getBulkSelectionAction(type),
			_getInputMap(bulkAction, bulkActionTask, filter, type));

		return bulkActionTask;
	}

	@Override
	public Page<BulkActionItem> postBulkActionItemPreviewPage(
			Boolean fetchChildren, String search, Filter filter,
			Pagination pagination, Sort[] sorts, BulkAction bulkAction)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-17564") ||
			!Objects.equals(
				bulkAction.getType(),
				BulkAction.Type.DELETE_OBJECT_BULK_SELECTION_ACTION)) {

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

		boolean selectAll = false;

		SelectionScope selectionScope = bulkAction.getSelectionScope();

		if (selectionScope != null) {
			selectAll = GetterUtil.getBoolean(selectionScope.getSelectAll());
		}

		if (ArrayUtil.isEmpty(bulkActionItems) && !selectAll) {
			return Page.of(Collections.emptyList());
		}

		if (!selectAll) {
			return _getBulkActionItemPreviewPage(
				ListUtil.fromArray(bulkActionItems), pagination, search,
				sorts[0]);
		}

		return _getBulkActionItemPreviewPage(
			filter, pagination, search, sorts[0]);
	}

	private BulkActionTask _addBulkActionTask(
			BulkAction bulkAction, long numberOfItems)
		throws Exception {

		if (BulkAction.Type.DELETE_OBJECT_ENTRY_BULK_SELECTION_ACTION.equals(
				bulkAction.getType())) {

			return new BulkActionTask();
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BULK_ACTION_TASK", contextCompany.getCompanyId());

		String typeString = _getTypeString(bulkAction);

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, contextUser.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			contextAcceptLanguage.getPreferredLanguageId(),
			HashMapBuilder.<String, Serializable>put(
				"actionName", typeString
			).put(
				"executionStatus", BulkSelectionActionStatusConstants.INITIAL
			).put(
				"numberOfItems", numberOfItems
			).put(
				"type", typeString
			).build(),
			new ServiceContext());

		return new BulkActionTask() {
			{
				setActionName(() -> GetterUtil.getString(typeString));
				setAuthor(objectEntry::getUserName);
				setCreatedDate(objectEntry::getCreateDate);
				setExecuteStatus(
					() -> GetterUtil.getString(
						BulkSelectionActionStatusConstants.INITIAL));
				setExternalReferenceCode(objectEntry::getExternalReferenceCode);
				setId(objectEntry::getObjectEntryId);
				setType(() -> GetterUtil.getString(typeString));
			}
		};
	}

	private BulkActionBulkSelectionFactory _getBulkActionBulkSelectionFactory(
		String blueprintExternalReferenceCode, BulkAction bulkAction,
		Boolean emptySearch, String entryClassNames, Filter filter,
		String scope, String search, Sort[] sorts) {

		return new BulkActionBulkSelectionFactory.Builder(
		).acceptLanguage(
			contextAcceptLanguage
		).blueprintExternalReferenceCode(
			blueprintExternalReferenceCode
		).bulkAction(
			bulkAction
		).bulkSelectionFactoryRegistry(
			_bulkSelectionFactoryRegistry
		).company(
			contextCompany
		).emptySearch(
			emptySearch
		).entryClassNames(
			entryClassNames
		).filter(
			filter
		).filterFactory(
			_filterFactory
		).groupLocalService(
			_groupLocalService
		).httpServletRequest(
			contextHttpServletRequest
		).localization(
			_localization
		).objectDefinitionLocalService(
			_objectDefinitionLocalService
		).objectEntryLocalService(
			_objectEntryLocalService
		).objectEntryVersionLocalService(
			_objectEntryVersionLocalService
		).scope(
			scope
		).search(
			search
		).searcher(
			_searcher
		).searchRequestBuilderFactory(
			_searchRequestBuilderFactory
		).sorts(
			sorts
		).user(
			contextUser
		).build();
	}

	private Page<BulkActionItem> _getBulkActionItemPreviewPage(
			Filter filter, Pagination pagination, String search, Sort sort)
		throws Exception {

		if (filter == null) {
			throw new ValidationException("Filter is null");
		}

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

		List<BulkActionItem> bulkActionItems = transform(
			searchPage.getItems(),
			searchResult -> {
				JSONObject jsonObject = _jsonFactory.createJSONObject(
					String.valueOf(searchResult.getEmbedded()));

				return _toBulkActionItem(jsonObject.getLong("id"));
			});

		if (StringUtil.equalsIgnoreCase(sort.getFieldName(), "usages")) {
			bulkActionItems = _sortBulkActionItems(bulkActionItems, sort);
		}

		return Page.of(bulkActionItems, pagination, searchPage.getTotalCount());
	}

	private Page<BulkActionItem> _getBulkActionItemPreviewPage(
		List<BulkActionItem> bulkActionItems1, Pagination pagination,
		String search, Sort sort) {

		List<BulkActionItem> bulkActionItems2 = transform(
			bulkActionItems1,
			bulkActionItem -> _toBulkActionItem(
				GetterUtil.getLong(bulkActionItem.getClassPK())));

		long totalCount = bulkActionItems1.size();

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

	private BulkSelectionAction<Object> _getBulkSelectionAction(
		BulkAction.Type type) {

		if (BulkAction.Type.ADD_OBJECT_TO_PROJECT_BULK_SELECTION_ACTION.equals(
				type)) {

			return _addObjectToProjectBulkSelectionAction;
		}
		else if (BulkAction.Type.
					ASSIGN_STRUCTURE_DEFAULT_WORKFLOW_BULK_SELECTION_ACTION.
						equals(type)) {

			return _assignStructureDefaultWorkflowBulkSelectionAction;
		}
		else if (BulkAction.Type.ASSIGN_TO_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			BulkSelectionAction<Object> assignToObjectBulkSelectionAction =
				_assignToObjectBulkSelectionActionSnapshot.get();

			if (assignToObjectBulkSelectionAction == null) {
				throw new UnsupportedOperationException();
			}

			return assignToObjectBulkSelectionAction;
		}
		else if (BulkAction.Type.COPY_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			return _copyObjectBulkSelectionAction;
		}
		else if (BulkAction.Type.
					DEFAULT_PERMISSION_OBJECT_BULK_SELECTION_ACTION.equals(
						type)) {

			return _defaultPermissionObjectBulkSelectionAction;
		}
		else if (BulkAction.Type.
					DELETE_OBJECT_ASSET_VERSION_BULK_SELECTION_ACTION.equals(
						type)) {

			return _deleteObjectAssetVersionBulkSelectionAction;
		}
		else if (BulkAction.Type.DELETE_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			return _deleteObjectBulkSelectionAction;
		}
		else if (BulkAction.Type.DELETE_OBJECT_ENTRY_BULK_SELECTION_ACTION.
					equals(type)) {

			return _deleteObjectEntryBulkSelectionAction;
		}
		else if (BulkAction.Type.DUE_DATE_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			return _dueDateObjectBulkSelectionAction;
		}
		else if (BulkAction.Type.DUPLICATE_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			return _duplicateObjectBulkSelectionAction;
		}
		else if (BulkAction.Type.EDIT_OBJECT_CATEGORIES_BULK_SELECTION_ACTION.
					equals(type)) {

			return _editObjectCategoriesBulkSelectionAction;
		}
		else if (BulkAction.Type.EDIT_OBJECT_TAGS_BULK_SELECTION_ACTION.equals(
					type)) {

			return _editObjectTagsBulkSelectionAction;
		}
		else if (BulkAction.Type.EXPIRE_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			return _expireObjectBulkSelectionAction;
		}
		else if (BulkAction.Type.MOVE_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			return _moveObjectBulkSelectionAction;
		}
		else if (BulkAction.Type.PERMISSION_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			return _permissionObjectBulkSelectionAction;
		}
		else if (BulkAction.Type.RESET_PERMISSION_OBJECT_BULK_SELECTION_ACTION.
					equals(type)) {

			return _resetPermissionObjectBulkSelectionAction;
		}
		else if (BulkAction.Type.RESTORE_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			return _restoreObjectBulkSelectionAction;
		}
		else if (BulkAction.Type.STATUS_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			return _statusObjectBulkSelectionAction;
		}
		else if (BulkAction.Type.UPDATE_OBJECT_VALUES_BULK_SELECTION_ACTION.
					equals(type)) {

			return _updateObjectValuesBulkSelectionAction;
		}

		throw new UnsupportedOperationException();
	}

	private String _getDeletionType(long groupId) throws PortalException {
		if (_trashHelper.isTrashEnabled(groupId)) {
			return "RECYCLE_BIN";
		}

		return "PERMANENT_DELETION";
	}

	private Map<String, Serializable> _getInputMap(
			BulkAction bulkAction, BulkActionTask bulkActionTask, Filter filter,
			BulkAction.Type type)
		throws Exception {

		HashMapBuilder.HashMapWrapper<String, Serializable> hashMapWrapper =
			HashMapBuilder.<String, Serializable>put(
				"bulkActionTaskId", bulkActionTask.getId());

		if (BulkAction.Type.ADD_OBJECT_TO_PROJECT_BULK_SELECTION_ACTION.equals(
				type)) {

			AddObjectToProjectBulkSelectionAction
				addObjectToProjectBulkSelectionAction =
					(AddObjectToProjectBulkSelectionAction)bulkAction;

			return hashMapWrapper.put(
				"projectScopeKeys",
				addObjectToProjectBulkSelectionAction.getProjectScopeKeys()
			).build();
		}
		else if (BulkAction.Type.
					ASSIGN_STRUCTURE_DEFAULT_WORKFLOW_BULK_SELECTION_ACTION.
						equals(type)) {

			AssignStructureDefaultWorkflowBulkSelectionAction
				assignStructureDefaultWorkflowBulkAction =
					(AssignStructureDefaultWorkflowBulkSelectionAction)
						bulkAction;

			return hashMapWrapper.put(
				"workflow",
				assignStructureDefaultWorkflowBulkAction::getWorkflow
			).build();
		}
		else if (BulkAction.Type.ASSIGN_TO_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			AssignToObjectBulkSelectionAction assignToBulkAction =
				(AssignToObjectBulkSelectionAction)bulkAction;

			return hashMapWrapper.put(
				"externalReferenceCode",
				assignToBulkAction::getExternalReferenceCode
			).put(
				"name", assignToBulkAction::getName
			).put(
				"type", assignToBulkAction::getClassName
			).build();
		}
		else if (BulkAction.Type.COPY_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			CopyObjectBulkSelectionAction copyBulkAction =
				(CopyObjectBulkSelectionAction)bulkAction;

			return hashMapWrapper.put(
				"objectEntryFolderId", copyBulkAction.getObjectEntryFolderId()
			).build();
		}
		else if (BulkAction.Type.
					DEFAULT_PERMISSION_OBJECT_BULK_SELECTION_ACTION.equals(
						type)) {

			DefaultPermissionObjectBulkSelectionAction
				defaultPermissionObjectBulkSelectionAction =
					(DefaultPermissionObjectBulkSelectionAction)bulkAction;

			return hashMapWrapper.put(
				"defaultPermissions",
				defaultPermissionObjectBulkSelectionAction::
					getDefaultPermissions
			).put(
				"roleKey",
				defaultPermissionObjectBulkSelectionAction.getRoleKey()
			).build();
		}
		else if (BulkAction.Type.
					DELETE_OBJECT_ASSET_VERSION_BULK_SELECTION_ACTION.equals(
						type)) {

			DeleteObjectAssetVersionBulkSelectionAction
				deleteAssetVersionBulkAction =
					(DeleteObjectAssetVersionBulkSelectionAction)bulkAction;

			return hashMapWrapper.put(
				"toRemoveVersions", deleteAssetVersionBulkAction.getVersions()
			).build();
		}
		else if (BulkAction.Type.DELETE_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			return hashMapWrapper.build();
		}
		else if (BulkAction.Type.DELETE_OBJECT_ENTRY_BULK_SELECTION_ACTION.
					equals(type)) {

			return hashMapWrapper.put(
				"objectDefinitionId", _getObjectDefinitionId(bulkAction, filter)
			).build();
		}
		else if (BulkAction.Type.DUE_DATE_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			DueDateObjectBulkSelectionAction dueDateBulkAction =
				(DueDateObjectBulkSelectionAction)bulkAction;

			return hashMapWrapper.put(
				"dueDate", dueDateBulkAction.getDueDate()
			).build();
		}
		else if (BulkAction.Type.DUPLICATE_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			return hashMapWrapper.build();
		}
		else if (BulkAction.Type.EDIT_OBJECT_CATEGORIES_BULK_SELECTION_ACTION.
					equals(type)) {

			EditObjectCategoriesBulkSelectionAction taxonomyCategoryBulkAction =
				(EditObjectCategoriesBulkSelectionAction)bulkAction;

			return hashMapWrapper.put(
				"append",
				GetterUtil.getBoolean(taxonomyCategoryBulkAction.getAppend())
			).put(
				"toAddCategoryIds",
				taxonomyCategoryBulkAction.getTaxonomyCategoryIdsToAdd()
			).put(
				"toRemoveCategoryIds",
				taxonomyCategoryBulkAction.getTaxonomyCategoryIdsToRemove()
			).build();
		}
		else if (BulkAction.Type.EDIT_OBJECT_TAGS_BULK_SELECTION_ACTION.equals(
					type)) {

			EditObjectTagsBulkSelectionAction keywordBulkAction =
				(EditObjectTagsBulkSelectionAction)bulkAction;

			return hashMapWrapper.put(
				"append", GetterUtil.getBoolean(keywordBulkAction.getAppend())
			).put(
				"toAddTagNames", keywordBulkAction.getKeywordsToAdd()
			).put(
				"toRemoveTagNames", keywordBulkAction.getKeywordsToRemove()
			).build();
		}
		else if (BulkAction.Type.EXPIRE_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			return hashMapWrapper.build();
		}
		else if (BulkAction.Type.MOVE_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			MoveObjectBulkSelectionAction moveBulkAction =
				(MoveObjectBulkSelectionAction)bulkAction;

			return hashMapWrapper.put(
				"objectEntryFolderId", moveBulkAction.getObjectEntryFolderId()
			).build();
		}
		else if (BulkAction.Type.PERMISSION_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			PermissionObjectBulkSelectionAction permissionBulkAction =
				(PermissionObjectBulkSelectionAction)bulkAction;

			return hashMapWrapper.put(
				"permissions",
				() -> _getPermissions(
					_jsonFactory.createJSONObject(
						GetterUtil.get(
							permissionBulkAction.getConfiguration(), "{}")),
					permissionBulkAction.getPermissions())
			).put(
				"roleKey", permissionBulkAction.getRoleKey()
			).build();
		}
		else if (BulkAction.Type.RESET_PERMISSION_OBJECT_BULK_SELECTION_ACTION.
					equals(type)) {

			return hashMapWrapper.build();
		}
		else if (BulkAction.Type.RESTORE_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			return hashMapWrapper.build();
		}
		else if (BulkAction.Type.STATUS_OBJECT_BULK_SELECTION_ACTION.equals(
					type)) {

			StatusObjectBulkSelectionAction statusBulkAction =
				(StatusObjectBulkSelectionAction)bulkAction;

			return hashMapWrapper.put(
				"status", statusBulkAction.getStatus()
			).build();
		}
		else if (BulkAction.Type.UPDATE_OBJECT_VALUES_BULK_SELECTION_ACTION.
					equals(type)) {

			UpdateObjectValuesBulkSelectionAction updateValuesBulkAction =
				(UpdateObjectValuesBulkSelectionAction)bulkAction;

			return hashMapWrapper.put(
				"values", (Serializable)updateValuesBulkAction.getValues()
			).build();
		}

		throw new UnsupportedOperationException();
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

	private long _getObjectDefinitionId(BulkAction bulkAction, Filter filter)
		throws Exception {

		SelectionScope selectionScope = bulkAction.getSelectionScope();

		if (selectionScope.getSelectAll()) {
			QueryFilter queryFilter = (QueryFilter)filter;

			TermQuery termQuery = (TermQuery)queryFilter.getQuery();

			QueryTerm queryTerm = termQuery.getQueryTerm();

			return Long.valueOf(queryTerm.getValue());
		}

		BulkActionItem[] bulkActionItems = bulkAction.getBulkActionItems();

		BulkActionItem bulkActionItem = bulkActionItems[0];

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			bulkActionItem.getClassPK());

		return objectEntry.getObjectDefinitionId();
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

	private Serializable _getPermissions(
		JSONObject configurationJSONObject, Permission[] permissions) {

		if (ArrayUtil.isNotEmpty(permissions)) {
			return HashMapBuilder.put(
				DepotEntry.class.getName(), permissions
			).build();
		}

		ObjectDefinition cmsBasicDocumentObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", contextCompany.getCompanyId());
		ObjectDefinition cmsBasicWebContentObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", contextCompany.getCompanyId());
		Map<String, Role> roles = new HashMap<>();

		return HashMapBuilder.put(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
			() -> _getPermissions(
				cmsBasicWebContentObjectDefinition.getClassName(),
				configurationJSONObject.getJSONObject(
					ObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_CONTENTS),
				roles)
		).put(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
			() -> _getPermissions(
				cmsBasicDocumentObjectDefinition.getClassName(),
				configurationJSONObject.getJSONObject(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES),
				roles)
		).put(
			ObjectEntryFolder.class.getName(),
			() -> _getPermissions(
				ObjectEntryFolder.class.getName(),
				configurationJSONObject.getJSONObject("OBJECT_ENTRY_FOLDERS"),
				roles)
		).build();
	}

	private Permission[] _getPermissions(
		String className, JSONObject jsonObject, Map<String, Role> roles) {

		List<Permission> permissionsList = new ArrayList<>(jsonObject.length());
		List<String> resourceActions = ResourceActionsUtil.getResourceActions(
			className);

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

				Permission permission = new Permission();

				permission.setActionIds(
					ArrayUtil.filter(
						JSONUtil.toStringArray(jsonObject.getJSONArray(key)),
						action -> resourceActions.contains(action)));
				permission.setRoleExternalReferenceCode(
					role.getExternalReferenceCode());
				permission.setRoleName(role.getName());
				permission.setRoleType(
					RoleConstants.getTypeLabel(role.getType()));

				permissionsList.add(permission);
			});

		return permissionsList.toArray(new Permission[0]);
	}

	private String _getTypeString(BulkAction bulkAction) throws Exception {
		BulkAction.Type type = bulkAction.getType();

		if (BulkAction.Type.DELETE_OBJECT_BULK_SELECTION_ACTION.equals(type)) {
			DeleteObjectBulkSelectionAction deleteBulkAction =
				(DeleteObjectBulkSelectionAction)bulkAction;

			if (!Validator.isBlank(deleteBulkAction.getClassName())) {
				ObjectDefinition cmsBulkActionTaskObjectDefinition =
					_objectDefinitionLocalService.
						getObjectDefinitionByExternalReferenceCode(
							"L_CMS_BULK_ACTION_TASK",
							contextCompany.getCompanyId());

				ObjectDefinition objectDefinition =
					_objectDefinitionLocalService.
						fetchObjectDefinitionByClassName(
							cmsBulkActionTaskObjectDefinition.getCompanyId(),
							deleteBulkAction.getClassName());

				if ((objectDefinition != null) &&
					Objects.equals(
						objectDefinition.getExternalReferenceCode(),
						"L_CMP_TASK")) {

					return "DeleteTaskBulkAction";
				}
			}
		}

		return type.toString();
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
				if (objectRelationship.isEdge()) {
					continue;
				}

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
		bulkActionItem.setClassName(
			((ClassedModel)objectEntryFolder)::getModelClassName);
		bulkActionItem.setClassPK(objectEntryFolder::getObjectEntryFolderId);
		bulkActionItem.setName(objectEntryFolder::getName);

		return bulkActionItem;
	}

	private static final Snapshot<BulkSelectionAction<Object>>
		_assignToObjectBulkSelectionActionSnapshot = new Snapshot<>(
			BulkActionResourceImpl.class,
			Snapshot.cast(BulkSelectionAction.class),
			"(bulk.selection.action.key=assign.to.object)", true);
	private static final EntityModel _entityModel = new BulkActionEntityModel();

	@Reference(target = "(bulk.selection.action.key=add.object.to.project)")
	private BulkSelectionAction<Object> _addObjectToProjectBulkSelectionAction;

	@Reference(
		target = "(bulk.selection.action.key=assign.structure.default.workflow.object.definition)"
	)
	private BulkSelectionAction<Object>
		_assignStructureDefaultWorkflowBulkSelectionAction;

	@Reference
	private BulkSelectionFactoryRegistry _bulkSelectionFactoryRegistry;

	@Reference
	private BulkSelectionRunner _bulkSelectionRunner;

	@Reference(target = "(bulk.selection.action.key=copy.object)")
	private BulkSelectionAction<Object> _copyObjectBulkSelectionAction;

	@Reference(target = "(bulk.selection.action.key=default.permission.object)")
	private BulkSelectionAction<Object>
		_defaultPermissionObjectBulkSelectionAction;

	@Reference(
		target = "(bulk.selection.action.key=delete.object.asset.version)"
	)
	private BulkSelectionAction<Object>
		_deleteObjectAssetVersionBulkSelectionAction;

	@Reference(target = "(bulk.selection.action.key=delete.object)")
	private BulkSelectionAction<Object> _deleteObjectBulkSelectionAction;

	@Reference(target = "(bulk.selection.action.key=delete.object.entry)")
	private BulkSelectionAction<Object> _deleteObjectEntryBulkSelectionAction;

	@Reference
	private DLMimeTypeDisplayContext _dlMimeTypeDisplayContext;

	@Reference(target = "(bulk.selection.action.key=due.date.object)")
	private BulkSelectionAction<Object> _dueDateObjectBulkSelectionAction;

	@Reference(target = "(bulk.selection.action.key=duplicate.object)")
	private BulkSelectionAction<Object> _duplicateObjectBulkSelectionAction;

	@Reference(target = "(bulk.selection.action.key=edit.object.categories)")
	private BulkSelectionAction<Object>
		_editObjectCategoriesBulkSelectionAction;

	@Reference(target = "(bulk.selection.action.key=edit.object.tags)")
	private BulkSelectionAction<Object> _editObjectTagsBulkSelectionAction;

	@Reference(target = "(bulk.selection.action.key=expire.object)")
	private BulkSelectionAction<Object> _expireObjectBulkSelectionAction;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Reference
	private Localization _localization;

	@Reference(target = "(bulk.selection.action.key=move.object)")
	private BulkSelectionAction<Object> _moveObjectBulkSelectionAction;

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

	@Reference(target = "(bulk.selection.action.key=permission.object)")
	private BulkSelectionAction<Object> _permissionObjectBulkSelectionAction;

	@Reference
	private Portal _portal;

	@Reference(target = "(bulk.selection.action.key=reset.permission.object)")
	private BulkSelectionAction<Object>
		_resetPermissionObjectBulkSelectionAction;

	@Reference(target = "(bulk.selection.action.key=restore.object)")
	private BulkSelectionAction<Object> _restoreObjectBulkSelectionAction;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private SearchResultResource.Factory _searchResultResourceFactory;

	@Reference(target = "(bulk.selection.action.key=status.object)")
	private BulkSelectionAction<Object> _statusObjectBulkSelectionAction;

	@Reference
	private TrashHelper _trashHelper;

	@Reference(target = "(bulk.selection.action.key=update.object.values)")
	private BulkSelectionAction<Object> _updateObjectValuesBulkSelectionAction;

}