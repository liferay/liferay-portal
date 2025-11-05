/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.internal.resource.v1_0;

import com.liferay.bulk.rest.dto.v1_0.BulkAction;
import com.liferay.bulk.rest.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.dto.v1_0.BulkActionTask;
import com.liferay.bulk.rest.dto.v1_0.DefaultPermissionBulkAction;
import com.liferay.bulk.rest.dto.v1_0.KeywordBulkAction;
import com.liferay.bulk.rest.dto.v1_0.PermissionBulkAction;
import com.liferay.bulk.rest.internal.odata.entity.v1_0.BulkActionEntityModel;
import com.liferay.bulk.rest.internal.selection.v1_0.BulkActionBulkSelectionFactory;
import com.liferay.bulk.rest.resource.v1_0.BulkActionResource;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.bulk.selection.BulkSelectionFactoryRegistry;
import com.liferay.bulk.selection.BulkSelectionInputParameters;
import com.liferay.bulk.selection.BulkSelectionRunner;
import com.liferay.depot.model.DepotEntry;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.permission.Permission;

import jakarta.ws.rs.core.MultivaluedMap;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
				contextCompany.getCompanyId(), "LPD-17564")) {

			throw new UnsupportedOperationException();
		}

		BulkActionBulkSelectionFactory bulkActionBulkSelectionFactory =
			_getBulkActionBulkSelectionFactory(
				blueprintExternalReferenceCode, emptySearch, entryClassNames,
				scope, search, filter, pagination, sorts, bulkAction);

		BulkSelection<Object> bulkSelection =
			bulkActionBulkSelectionFactory.create();

		if (bulkSelection.getSize() == 0) {
			return new BulkActionTask();
		}

		BulkAction.Type type = bulkAction.getType();

		BulkActionTask bulkActionTask = _addBulkActionTask(type);

		_bulkSelectionRunner.run(
			contextUser, bulkSelection, _getBulkSelectionAction(type),
			_getInputMap(bulkAction, bulkActionTask, type));

		return bulkActionTask;
	}

	@Override
	public Page<BulkActionItem> postBulkActionItemPreviewPage(
			Boolean fetchChildren, String search, Filter filter,
			Pagination pagination, Sort[] sorts, BulkAction bulkAction)
		throws Exception {

		return super.postBulkActionItemPreviewPage(
			fetchChildren, search, filter, pagination, sorts, bulkAction);
	}

	private BulkActionTask _addBulkActionTask(BulkAction.Type type)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BULK_ACTION_TASK", contextCompany.getCompanyId());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, contextUser.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			contextAcceptLanguage.getPreferredLanguageId(),
			HashMapBuilder.<String, Serializable>put(
				"actionName", type.toString()
			).put(
				"executionStatus", "initial"
			).put(
				"type", type.toString()
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

	private BulkActionBulkSelectionFactory _getBulkActionBulkSelectionFactory(
		String blueprintExternalReferenceCode, Boolean emptySearch,
		String entryClassNames, String scope, String search, Filter filter,
		Pagination pagination, Sort[] sorts, BulkAction bulkAction) {

		return new BulkActionBulkSelectionFactory.Builder(
		).bulkSelectionFactoryRegistry(
			_bulkSelectionFactoryRegistry
		).indexerRegistry(
			_indexerRegistry
		).filterFactory(
			_filterFactory
		).localization(
			_localization
		).searcher(
			_searcher
		).searchRequestBuilderFactory(
			_searchRequestBuilderFactory
		).contextAcceptLanguage(
			contextAcceptLanguage
		).contextCompany(
			contextCompany
		).blueprintExternalReferenceCode(
			blueprintExternalReferenceCode
		).emptySearch(
			emptySearch
		).entryClassNames(
			entryClassNames
		).objectDefinitionLocalService(
			_objectDefinitionLocalService
		).objectEntryLocalService(
			_objectEntryLocalService
		).scope(
			scope
		).search(
			search
		).filter(
			filter
		).pagination(
			pagination
		).sorts(
			sorts
		).bulkAction(
			bulkAction
		).contextHttpServletRequest(
			contextHttpServletRequest
		).contextUser(
			contextUser
		).build();
	}

	private BulkSelectionAction<Object> _getBulkSelectionAction(
		BulkAction.Type type) {

		if (BulkAction.Type.DEFAULT_PERMISSION_BULK_ACTION.equals(type)) {
			return _defaultPermissionObjectTagsBulkSelectionAction;
		}
		else if (BulkAction.Type.DELETE_BULK_ACTION.equals(type)) {
			return _deleteObjectBulkSelectionAction;
		}
		else if (BulkAction.Type.KEYWORD_BULK_ACTION.equals(type)) {
			return _editObjectTagsBulkSelectionAction;
		}
		else if (BulkAction.Type.PERMISSION_BULK_ACTION.equals(type)) {
			return _permissionObjectTagsBulkSelectionAction;
		}

		throw new UnsupportedOperationException();
	}

	private Map<String, Serializable> _getInputMap(
		BulkAction bulkAction, BulkActionTask bulkActionTask,
		BulkAction.Type type) {

		if (BulkAction.Type.DEFAULT_PERMISSION_BULK_ACTION.equals(type)) {
			return HashMapBuilder.<String, Serializable>put(
				"bulkActionTaskId", bulkActionTask.getId()
			).put(
				"defaultPermissions",
				() -> {
					DefaultPermissionBulkAction defaultPermissionBulkAction =
						(DefaultPermissionBulkAction)bulkAction;

					return defaultPermissionBulkAction.getDefaultPermissions();
				}
			).build();
		}
		else if (BulkAction.Type.DELETE_BULK_ACTION.equals(type)) {
			return HashMapBuilder.<String, Serializable>put(
				"bulkActionTaskId", bulkActionTask.getId()
			).build();
		}
		else if (BulkAction.Type.KEYWORD_BULK_ACTION.equals(type)) {
			return HashMapBuilder.<String, Serializable>put(
				BulkSelectionInputParameters.ASSET_ENTRY_BULK_SELECTION, true
			).put(
				"append", true
			).put(
				"bulkActionTaskId", bulkActionTask.getId()
			).put(
				"toAddTagNames",
				() -> {
					KeywordBulkAction keywordBulkAction =
						(KeywordBulkAction)bulkAction;

					return keywordBulkAction.getKeywordsToAdd();
				}
			).put(
				"toRemoveTagNames",
				() -> {
					KeywordBulkAction keywordBulkAction =
						(KeywordBulkAction)bulkAction;

					return keywordBulkAction.getKeywordsToRemove();
				}
			).build();
		}
		else if (BulkAction.Type.PERMISSION_BULK_ACTION.equals(type)) {
			return HashMapBuilder.<String, Serializable>put(
				"bulkActionTaskId", bulkActionTask.getId()
			).put(
				"companyId", contextCompany.getCompanyId()
			).put(
				"permissions",
				() -> {
					PermissionBulkAction permissionBulkAction =
						(PermissionBulkAction)bulkAction;

					return _getPermissions(
						_jsonFactory.createJSONObject(
							GetterUtil.get(
								permissionBulkAction.getConfiguration(), "{}")),
						permissionBulkAction.getPermissions());
				}
			).build();
		}

		throw new UnsupportedOperationException();
	}

	private Serializable _getPermissions(
		JSONObject configurationJSONObject, Permission[] permissions) {

		if (ArrayUtil.isNotEmpty(permissions)) {
			return HashMapBuilder.put(
				DepotEntry.class.getName(), permissions
			).build();
		}

		ObjectDefinition cmsBasicWebContentObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", contextCompany.getCompanyId());
		ObjectDefinition cmsBasicDocumentObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", contextCompany.getCompanyId());
		Map<String, Role> roles = new HashMap<>();

		return HashMapBuilder.put(
			cmsBasicDocumentObjectDefinition.getClassName(),
			() -> _getPermissions(
				cmsBasicDocumentObjectDefinition.getClassName(),
				configurationJSONObject.getJSONObject(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES),
				roles)
		).put(
			cmsBasicWebContentObjectDefinition.getClassName(),
			() -> _getPermissions(
				cmsBasicWebContentObjectDefinition.getClassName(),
				configurationJSONObject.getJSONObject(
					ObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_CONTENTS),
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

		List<String> resourceActions = ResourceActionsUtil.getResourceActions(
			className);

		List<Permission> permissionList = new ArrayList<>(jsonObject.length());

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

				permissionList.add(permission);
			});

		return permissionList.toArray(new Permission[0]);
	}

	private static final EntityModel _entityModel = new BulkActionEntityModel();

	@Reference
	private BulkSelectionFactoryRegistry _bulkSelectionFactoryRegistry;

	@Reference
	private BulkSelectionRunner _bulkSelectionRunner;

	@Reference(target = "(bulk.selection.action.key=default.permission.object)")
	private BulkSelectionAction<Object>
		_defaultPermissionObjectTagsBulkSelectionAction;

	@Reference(target = "(bulk.selection.action.key=delete.object)")
	private BulkSelectionAction<Object> _deleteObjectBulkSelectionAction;

	@Reference(target = "(bulk.selection.action.key=edit.object.tags)")
	private BulkSelectionAction<Object> _editObjectTagsBulkSelectionAction;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Localization _localization;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference(target = "(bulk.selection.action.key=permission.object)")
	private BulkSelectionAction<Object>
		_permissionObjectTagsBulkSelectionAction;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}