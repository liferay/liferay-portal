/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.internal.resource.v1_0;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.headless.common.spi.odata.entity.EntityFieldsUtil;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.object.dto.v1_0.ObjectEntryFolder;
import com.liferay.headless.object.internal.odata.entity.v1_0.ObjectEntryFolderEntityModel;
import com.liferay.headless.object.resource.v1_0.ObjectEntryFolderResource;
import com.liferay.object.constants.ObjectConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.exception.NoSuchObjectEntryFolderException;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryFolderService;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.expando.ExpandoBridgeIndexer;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.ActionUtil;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.sharing.configuration.SharingConfiguration;
import com.liferay.sharing.configuration.SharingConfigurationFactory;
import com.liferay.trash.TrashHelper;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alicia García
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/object-entry-folder.properties",
	scope = ServiceScope.PROTOTYPE, service = ObjectEntryFolderResource.class
)
public class ObjectEntryFolderResourceImpl
	extends BaseObjectEntryFolderResourceImpl {

	@Override
	public void deleteObjectEntryFolder(Long objectEntryFolderId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		_deleteObjectEntryFolder(
			_objectEntryFolderLocalService.getObjectEntryFolder(
				objectEntryFolderId));
	}

	@Override
	public void deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
			String scopeKey, String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		_deleteObjectEntryFolder(
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					externalReferenceCode, _getGroupId(scopeKey),
					contextCompany.getCompanyId()));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return new ObjectEntryFolderEntityModel(
			EntityFieldsUtil.getEntityFields(
				_portal.getClassNameId(
					com.liferay.object.model.ObjectEntryFolder.class.getName()),
				contextCompany.getCompanyId(), _expandoBridgeIndexer,
				_expandoColumnLocalService, _expandoTableLocalService));
	}

	@Override
	public ObjectEntryFolder
			getScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
				String scopeKey, String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _toObjectEntryFolder(
			_objectEntryFolderService.
				getObjectEntryFolderByExternalReferenceCode(
					externalReferenceCode, _getGroupId(scopeKey),
					contextCompany.getCompanyId()));
	}

	@Override
	public Page<ObjectEntryFolder> getScopeScopeKeyObjectEntryFoldersPage(
			String scopeKey, Boolean flatten, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		long groupId = _getGroupId(scopeKey);

		DepotEntry depotEntry = _depotEntryLocalService.fetchGroupDepotEntry(
			groupId);

		if (depotEntry == null) {
			throw new NoSuchObjectEntryFolderException();
		}

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.ADD_FOLDER, "postScopeScopeKeyObjectEntryFolder",
					com.liferay.object.model.ObjectEntryFolder.class.getName(),
					groupId)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getScopeScopeKeyObjectEntryFoldersPage",
					com.liferay.object.model.ObjectEntryFolder.class.getName(),
					groupId)
			).build(),
			booleanQuery -> {
				if (!GetterUtil.getBoolean(flatten)) {
					BooleanFilter booleanFilter =
						booleanQuery.getPreBooleanFilter();

					booleanFilter.add(
						new TermFilter(Field.GROUP_ID, scopeKey),
						BooleanClauseOccur.MUST);
				}
			},
			filter, com.liferay.object.model.ObjectEntryFolder.class.getName(),
			search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				if (Validator.isNotNull(search)) {
					searchContext.setKeywords(search);
				}

				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});
			},
			sorts,
			document -> _toObjectEntryFolder(
				_objectEntryFolderService.getObjectEntryFolder(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public ObjectEntryFolder
			patchScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
				String scopeKey, String externalReferenceCode,
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _updateObjectEntryFolder(
			objectEntryFolder,
			_objectEntryFolderService.
				getObjectEntryFolderByExternalReferenceCode(
					externalReferenceCode, _getGroupId(scopeKey),
					contextCompany.getCompanyId()));
	}

	@Override
	public ObjectEntryFolder postScopeScopeKeyObjectEntryFolder(
			String scopeKey, ObjectEntryFolder objectEntryFolder)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		long groupId = _getGroupId(scopeKey);

		return _addObjectEntryFolder(
			groupId,
			GetterUtil.getLong(
				_getParentObjectEntryFolderId(
					false, groupId, objectEntryFolder)),
			objectEntryFolder);
	}

	@Override
	public ObjectEntryFolder
			postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeRestore(
				String scopeKey, String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		com.liferay.object.model.ObjectEntryFolder
			serviceBuilderObjectEntryFolder =
				_objectEntryFolderService.
					getObjectEntryFolderByExternalReferenceCode(
						externalReferenceCode, _getGroupId(scopeKey),
						contextUser.getCompanyId());

		return _toObjectEntryFolder(
			_objectEntryFolderService.restoreObjectEntryFolderFromTrash(
				serviceBuilderObjectEntryFolder,
				ServiceContextBuilder.create(
					serviceBuilderObjectEntryFolder.getGroupId(),
					contextHttpServletRequest, null
				).build()));
	}

	@Override
	public void
			postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeSubscribe(
				String scopeKey, String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		long groupId = _getGroupId(scopeKey);

		com.liferay.object.model.ObjectEntryFolder
			serviceBuilderObjectEntryFolder =
				_objectEntryFolderService.
					getObjectEntryFolderByExternalReferenceCode(
						externalReferenceCode, groupId,
						contextUser.getCompanyId());

		_objectEntryFolderService.subscribeObjectEntryFolder(
			groupId, serviceBuilderObjectEntryFolder.getObjectEntryFolderId());
	}

	@Override
	public void
			postScopeScopeKeyObjectEntryFolderByExternalReferenceCodeUnsubscribe(
				String scopeKey, String externalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		long groupId = _getGroupId(scopeKey);

		com.liferay.object.model.ObjectEntryFolder
			serviceBuilderObjectEntryFolder =
				_objectEntryFolderService.
					getObjectEntryFolderByExternalReferenceCode(
						externalReferenceCode, groupId,
						contextUser.getCompanyId());

		_objectEntryFolderService.unsubscribeObjectEntryFolder(
			groupId, serviceBuilderObjectEntryFolder.getObjectEntryFolderId());
	}

	@Override
	public ObjectEntryFolder
			putScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
				String scopeKey, String externalReferenceCode,
				ObjectEntryFolder objectEntryFolder)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		long groupId = _getGroupId(scopeKey);

		com.liferay.object.model.ObjectEntryFolder
			serviceBuilderObjectEntryFolder =
				_objectEntryFolderService.
					fetchObjectEntryFolderByExternalReferenceCode(
						externalReferenceCode, groupId,
						contextUser.getCompanyId());

		if (serviceBuilderObjectEntryFolder == null) {
			return _addObjectEntryFolder(
				groupId,
				GetterUtil.getLong(
					_getParentObjectEntryFolderId(
						true, groupId, objectEntryFolder)),
				objectEntryFolder);
		}

		return _toObjectEntryFolder(
			_objectEntryFolderService.updateObjectEntryFolder(
				serviceBuilderObjectEntryFolder.getObjectEntryFolderId(),
				GetterUtil.getLong(
					_getParentObjectEntryFolderId(
						true, groupId, objectEntryFolder)),
				objectEntryFolder.getDescription(),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					objectEntryFolder.getLabel(),
					objectEntryFolder.getLabel_i18n()),
				objectEntryFolder.getTitle(),
				ServiceContextBuilder.create(
					groupId, contextHttpServletRequest, null
				).build()));
	}

	@Override
	protected ObjectEntryFolder doGetObjectEntryFolder(Long objectEntryFolderId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		return _toObjectEntryFolder(
			_objectEntryFolderService.getObjectEntryFolder(
				objectEntryFolderId));
	}

	@Override
	protected ObjectEntryFolder doPutObjectEntryFolder(
			Long objectEntryFolderId, ObjectEntryFolder objectEntryFolder)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		com.liferay.object.model.ObjectEntryFolder
			serviceBuilderObjectEntryFolder =
				_objectEntryFolderService.fetchObjectEntryFolder(
					objectEntryFolderId);

		if (serviceBuilderObjectEntryFolder == null) {
			long groupId = _getGroupId(objectEntryFolder.getScopeKey());

			return _addObjectEntryFolder(
				groupId,
				GetterUtil.getLong(
					_getParentObjectEntryFolderId(
						true, groupId, objectEntryFolder)),
				objectEntryFolder);
		}

		return _updateObjectEntryFolder(
			objectEntryFolder,
			_objectEntryFolderService.getObjectEntryFolder(
				objectEntryFolderId));
	}

	@Override
	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		com.liferay.object.model.ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderService.getObjectEntryFolder(
				GetterUtil.getLong(id));

		return objectEntryFolder.getGroupId();
	}

	@Override
	protected String getPermissionCheckerPortletName(Object id) {
		return ObjectConstants.RESOURCE_NAME;
	}

	@Override
	protected String getPermissionCheckerResourceName(Object id) {
		return com.liferay.object.model.ObjectEntryFolder.class.getName();
	}

	private ObjectEntryFolder _addObjectEntryFolder(
			long groupId, long parentObjectEntryFolderId,
			ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return _toObjectEntryFolder(
			_objectEntryFolderService.addObjectEntryFolder(
				objectEntryFolder.getExternalReferenceCode(), groupId,
				parentObjectEntryFolderId, objectEntryFolder.getDescription(),
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					objectEntryFolder.getLabel(),
					objectEntryFolder.getLabel_i18n()),
				objectEntryFolder.getTitle(),
				ServiceContextBuilder.create(
					groupId, contextHttpServletRequest,
					objectEntryFolder.getViewableByAsString()
				).build()));
	}

	private void _deleteObjectEntryFolder(
			com.liferay.object.model.ObjectEntryFolder
				serviceBuilderObjectEntryFolder)
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.fetchGroupDepotEntry(
			serviceBuilderObjectEntryFolder.getGroupId());

		if ((depotEntry != null) &&
			_trashHelper.isTrashEnabled(
				serviceBuilderObjectEntryFolder.getGroupId()) &&
			(serviceBuilderObjectEntryFolder.getStatus() !=
				WorkflowConstants.STATUS_IN_TRASH) &&
			FeatureFlagManagerUtil.isEnabled("LPD-17564")) {

			_objectEntryFolderService.moveObjectEntryFolderToTrash(
				serviceBuilderObjectEntryFolder,
				ServiceContextBuilder.create(
					serviceBuilderObjectEntryFolder.getGroupId(),
					contextHttpServletRequest, null
				).build());
		}
		else {
			_objectEntryFolderService.deleteObjectEntryFolder(
				serviceBuilderObjectEntryFolder.getObjectEntryFolderId());
		}
	}

	private long _getGroupId(String scopeKey) throws Exception {
		Long groupId = GroupUtil.getGroupId(
			contextCompany.getCompanyId(), scopeKey, _groupLocalService);

		if (groupId != null) {
			return groupId;
		}

		if (Objects.equals(scopeKey, "0")) {
			return 0;
		}

		throw new NoSuchGroupException();
	}

	private Long _getParentObjectEntryFolderId(
			boolean addObjectEntryFolder, long groupId,
			ObjectEntryFolder objectEntryFolder)
		throws Exception {

		String parentObjectEntryFolderExternalReferenceCode =
			objectEntryFolder.getParentObjectEntryFolderExternalReferenceCode();

		Long parentObjectEntryFolderId =
			objectEntryFolder.getParentObjectEntryFolderId();

		if (Validator.isNull(parentObjectEntryFolderExternalReferenceCode)) {
			if (parentObjectEntryFolderId == null) {
				return null;
			}

			if (parentObjectEntryFolderId !=
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT) {

				_objectEntryFolderService.getObjectEntryFolder(
					parentObjectEntryFolderId);
			}

			return parentObjectEntryFolderId;
		}

		com.liferay.object.model.ObjectEntryFolder
			serviceBuilderObjectEntryFolder =
				_objectEntryFolderService.
					fetchObjectEntryFolderByExternalReferenceCode(
						parentObjectEntryFolderExternalReferenceCode, groupId,
						contextUser.getCompanyId());

		if ((parentObjectEntryFolderId != null) &&
			(serviceBuilderObjectEntryFolder != null) &&
			(serviceBuilderObjectEntryFolder.getObjectEntryFolderId() !=
				parentObjectEntryFolderId)) {

			throw new NoSuchObjectEntryFolderException();
		}

		if (serviceBuilderObjectEntryFolder == null) {
			if (!addObjectEntryFolder) {
				serviceBuilderObjectEntryFolder =
					_objectEntryFolderLocalService.
						getOrAddEmptyObjectEntryFolder(
							parentObjectEntryFolderExternalReferenceCode,
							groupId, contextUser.getCompanyId(),
							contextUser.getUserId(),
							ServiceContextBuilder.create(
								groupId, contextHttpServletRequest, null
							).build());
			}
			else {
				serviceBuilderObjectEntryFolder =
					_objectEntryFolderService.addObjectEntryFolder(
						parentObjectEntryFolderExternalReferenceCode, groupId,
						ObjectEntryFolderConstants.
							PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
						null, null,
						parentObjectEntryFolderExternalReferenceCode,
						ServiceContextBuilder.create(
							groupId, contextHttpServletRequest, null
						).build());
			}
		}

		return serviceBuilderObjectEntryFolder.getObjectEntryFolderId();
	}

	private ObjectEntryFolder _toObjectEntryFolder(
			com.liferay.object.model.ObjectEntryFolder
				serviceBuilderObjectEntryFolder)
		throws Exception {

		return _objectEntryFolderDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				HashMapBuilder.put(
					"delete",
					addAction(
						ActionKeys.DELETE, serviceBuilderObjectEntryFolder,
						"deleteObjectEntryFolder")
				).put(
					"get",
					addAction(
						ActionKeys.VIEW, serviceBuilderObjectEntryFolder,
						"getObjectEntryFolder")
				).put(
					"permissions",
					addAction(
						ActionKeys.PERMISSIONS, serviceBuilderObjectEntryFolder,
						"getObjectEntryFolderPermissionsPage")
				).put(
					"restore",
					() -> {
						if (!FeatureFlagManagerUtil.isEnabled("LPD-17564") ||
							(serviceBuilderObjectEntryFolder.getStatus() !=
								WorkflowConstants.STATUS_IN_TRASH)) {

							return null;
						}

						return ActionUtil.addAction(
							ActionKeys.DELETE,
							ObjectEntryFolderResourceImpl.class,
							serviceBuilderObjectEntryFolder.
								getObjectEntryFolderId(),
							"postScopeScopeKeyObjectEntryFolderByExternal" +
								"ReferenceCodeRestore",
							null, _objectEntryFolderModelResourcePermission,
							HashMapBuilder.put(
								"externalReferenceCode",
								serviceBuilderObjectEntryFolder.
									getExternalReferenceCode()
							).put(
								"scopeKey",
								String.valueOf(
									serviceBuilderObjectEntryFolder.
										getGroupId())
							).build(),
							contextUriInfo);
					}
				).put(
					"share",
					() -> {
						Group group = _groupLocalService.fetchGroup(
							serviceBuilderObjectEntryFolder.getGroupId());

						if (group == null) {
							return null;
						}

						SharingConfiguration sharingConfiguration =
							_sharingConfigurationFactory.
								getGroupSharingConfiguration(group);

						if (!sharingConfiguration.isEnabled()) {
							return null;
						}

						return addAction(
							ActionKeys.VIEW, serviceBuilderObjectEntryFolder,
							"getObjectEntryFolder");
					}
				).put(
					"update",
					addAction(
						ActionKeys.UPDATE, serviceBuilderObjectEntryFolder,
						"patchObjectEntryFolder")
				).build(),
				_dtoConverterRegistry,
				serviceBuilderObjectEntryFolder.getObjectEntryFolderId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private ObjectEntryFolder _updateObjectEntryFolder(
			ObjectEntryFolder objectEntryFolder,
			com.liferay.object.model.ObjectEntryFolder
				serviceBuilderObjectEntryFolder)
		throws Exception {

		String description = objectEntryFolder.getDescription();

		if (description == null) {
			description = serviceBuilderObjectEntryFolder.getDescription();
		}

		Map<String, String> labelMap = objectEntryFolder.getLabel_i18n();

		if (labelMap == null) {
			labelMap = LocalizedMapUtil.getI18nMap(
				serviceBuilderObjectEntryFolder.getLabelMap());
		}

		Long parentObjectEntryFolderId = _getParentObjectEntryFolderId(
			false, serviceBuilderObjectEntryFolder.getGroupId(),
			objectEntryFolder);

		if (parentObjectEntryFolderId == null) {
			parentObjectEntryFolderId =
				serviceBuilderObjectEntryFolder.getParentObjectEntryFolderId();
		}

		return _toObjectEntryFolder(
			_objectEntryFolderService.updateObjectEntryFolder(
				serviceBuilderObjectEntryFolder.getObjectEntryFolderId(),
				parentObjectEntryFolderId, description,
				LocalizedMapUtil.getLocalizedMap(
					contextAcceptLanguage.getPreferredLocale(),
					GetterUtil.getString(
						objectEntryFolder.getLabel(),
						serviceBuilderObjectEntryFolder.getLabel()),
					labelMap),
				GetterUtil.getString(
					objectEntryFolder.getTitle(),
					serviceBuilderObjectEntryFolder.getName()),
				ServiceContextBuilder.create(
					serviceBuilderObjectEntryFolder.getGroupId(),
					contextHttpServletRequest, null
				).build()));
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ExpandoBridgeIndexer _expandoBridgeIndexer;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.object.internal.dto.v1_0.converter.ObjectEntryFolderDTOConverter)"
	)
	private DTOConverter
		<com.liferay.object.model.ObjectEntryFolder, ObjectEntryFolder>
			_objectEntryFolderDTOConverter;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectEntryFolder)"
	)
	private ModelResourcePermission<com.liferay.object.model.ObjectEntryFolder>
		_objectEntryFolderModelResourcePermission;

	@Reference
	private ObjectEntryFolderService _objectEntryFolderService;

	@Reference
	private Portal _portal;

	@Reference
	private SharingConfigurationFactory _sharingConfigurationFactory;

	@Reference
	private TrashHelper _trashHelper;

}