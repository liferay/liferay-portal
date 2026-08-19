/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.list.type.internal.resource.v1_0;

import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeEntry;
import com.liferay.headless.admin.list.type.dto.v1_0.Status;
import com.liferay.headless.admin.list.type.internal.dto.v1_0.util.ListTypeEntryUtil;
import com.liferay.headless.admin.list.type.internal.odata.entity.v1_0.ListTypeDefinitionEntityModel;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeDefinitionResource;
import com.liferay.list.type.constants.ListTypeActionKeys;
import com.liferay.list.type.constants.ListTypeConstants;
import com.liferay.list.type.service.ListTypeDefinitionService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.rest.dto.v1_0.util.CreatorUtil;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PermissionService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.permission.ModelPermissionsUtil;
import com.liferay.portal.vulcan.permission.Permission;
import com.liferay.portal.vulcan.permission.PermissionUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collection;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gabriel Albuquerque
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/list-type-definition.properties",
	property = "export.import.vulcan.batch.engine.task.item.delegate=true",
	scope = ServiceScope.PROTOTYPE, service = ListTypeDefinitionResource.class
)
public class ListTypeDefinitionResourceImpl
	extends BaseListTypeDefinitionResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate
		<ListTypeDefinition> {

	@Override
	public void deleteListTypeDefinition(Long listTypeDefinitionId)
		throws Exception {

		_listTypeDefinitionService.deleteListTypeDefinition(
			listTypeDefinitionId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public ExportImportDescriptor
		<com.liferay.list.type.model.ListTypeDefinition>
			getExportImportDescriptor() {

		return new ExportImportDescriptor<>() {

			@Override
			public String getKey() {
				return ListTypeDefinitionResourceImpl.class.getName();
			}

			@Override
			public String getLabelLanguageKey() {
				return "picklists";
			}

			@Override
			public Class<com.liferay.list.type.model.ListTypeDefinition>
				getModelClass() {

				return com.liferay.list.type.model.ListTypeDefinition.class;
			}

			@Override
			public String getPortletId() {
				return ObjectPortletKeys.LIST_TYPE_DEFINITIONS;
			}

			@Override
			public int getRank() {
				return 98;
			}

			@Override
			public Scope getScope() {
				return Scope.COMPANY;
			}

			@Override
			public String getSectionKey() {
				return ExportImportConstants.SECTION_KEY_CONTENT_AND_DATA;
			}

		};
	}

	@Override
	public ListTypeDefinition getListTypeDefinition(Long listTypeDefinitionId)
		throws Exception {

		return _toListTypeDefinition(
			_listTypeDefinitionService.getListTypeDefinition(
				listTypeDefinitionId));
	}

	@Override
	public ListTypeDefinition getListTypeDefinitionByExternalReferenceCode(
			String externalReferenceCode)
		throws PortalException {

		return _toListTypeDefinition(
			_listTypeDefinitionService.
				getListTypeDefinitionByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId()));
	}

	@Override
	public Page<ListTypeDefinition> getListTypeDefinitionsPage(
			String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					ListTypeActionKeys.ADD_LIST_TYPE_DEFINITION,
					"postListTypeDefinition", ListTypeConstants.RESOURCE_NAME,
					contextCompany.getCompanyId())
			).put(
				"createBatch",
				addAction(
					ListTypeActionKeys.ADD_LIST_TYPE_DEFINITION,
					"postListTypeDefinitionBatch",
					ListTypeConstants.RESOURCE_NAME,
					contextCompany.getCompanyId())
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getListTypeDefinitionsPage",
					ListTypeConstants.RESOURCE_NAME,
					contextCompany.getCompanyId())
			).build(),
			booleanQuery -> {
			},
			filter,
			com.liferay.list.type.model.ListTypeDefinition.class.getName(),
			search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(Field.NAME, search);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> _toListTypeDefinition(
				_listTypeDefinitionService.getListTypeDefinition(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public ListTypeDefinition postListTypeDefinition(
			ListTypeDefinition listTypeDefinition)
		throws Exception {

		return _toListTypeDefinition(
			_listTypeDefinitionService.addListTypeDefinition(
				listTypeDefinition.getExternalReferenceCode(),
				LocalizedMapUtil.populateLocalizedMap(
					listTypeDefinition.getDefaultLanguageId(),
					listTypeDefinition.getName_i18n(),
					listTypeDefinition.getName()),
				GetterUtil.getBoolean(listTypeDefinition.getSystem()),
				transformToList(
					listTypeDefinition.getListTypeEntries(),
					listTypeEntry -> ListTypeEntryUtil.toListTypeEntry(
						listTypeEntry, _listTypeEntryLocalService)),
				_createServiceContext(listTypeDefinition)));
	}

	@Override
	public ListTypeDefinition putListTypeDefinition(
			Long listTypeDefinitionId, ListTypeDefinition listTypeDefinition)
		throws Exception {

		return _toListTypeDefinition(
			_listTypeDefinitionService.updateListTypeDefinition(
				listTypeDefinition.getExternalReferenceCode(),
				listTypeDefinitionId,
				LocalizedMapUtil.populateLocalizedMap(
					listTypeDefinition.getDefaultLanguageId(),
					listTypeDefinition.getName_i18n(),
					listTypeDefinition.getName()),
				transformToList(
					listTypeDefinition.getListTypeEntries(),
					listTypeEntry -> ListTypeEntryUtil.toListTypeEntry(
						listTypeEntry, _listTypeEntryLocalService)),
				_createServiceContext(listTypeDefinition)));
	}

	@Override
	public ListTypeDefinition putListTypeDefinitionByExternalReferenceCode(
			String externalReferenceCode, ListTypeDefinition listTypeDefinition)
		throws Exception {

		listTypeDefinition.setExternalReferenceCode(
			() -> externalReferenceCode);

		com.liferay.list.type.model.ListTypeDefinition
			serviceBuilderListTypeDefinition =
				_listTypeDefinitionService.
					fetchListTypeDefinitionByExternalReferenceCode(
						externalReferenceCode, contextCompany.getCompanyId());

		if (serviceBuilderListTypeDefinition != null) {
			return putListTypeDefinition(
				serviceBuilderListTypeDefinition.getListTypeDefinitionId(),
				listTypeDefinition);
		}

		return postListTypeDefinition(listTypeDefinition);
	}

	@Override
	protected void preparePatch(
		ListTypeDefinition listTypeDefinition,
		ListTypeDefinition existingListTypeDefinition) {

		if (listTypeDefinition.getListTypeEntries() != null) {
			existingListTypeDefinition.setListTypeEntries(
				listTypeDefinition::getListTypeEntries);
		}
	}

	private ServiceContext _createServiceContext(
			ListTypeDefinition listTypeDefinition)
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		if (listTypeDefinition.getPermissions() == null) {
			serviceContext.setModelPermissions(null);

			return serviceContext;
		}

		serviceContext.setModelPermissions(
			ModelPermissionsUtil.toModelPermissions(
				contextCompany.getCompanyId(),
				listTypeDefinition.getPermissions(),
				GetterUtil.getLong(listTypeDefinition.getId()),
				com.liferay.list.type.model.ListTypeDefinition.class.getName(),
				_resourceActionLocalService, _resourcePermissionLocalService,
				_roleLocalService));

		return serviceContext;
	}

	private Locale _getLocale() {
		if (contextUser != null) {
			return contextUser.getLocale();
		}

		return contextAcceptLanguage.getPreferredLocale();
	}

	private ListTypeDefinition _toListTypeDefinition(
		com.liferay.list.type.model.ListTypeDefinition
			serviceBuilderListTypeDefinition) {

		if (serviceBuilderListTypeDefinition == null) {
			return null;
		}

		Locale locale = _getLocale();
		String permissionName =
			com.liferay.list.type.model.ListTypeDefinition.class.getName();
		User user = _userLocalService.fetchUser(
			serviceBuilderListTypeDefinition.getUserId());

		return new ListTypeDefinition() {
			{
				setActions(
					() -> HashMapBuilder.put(
						"delete",
						() -> {
							int count =
								_objectFieldLocalService.
									getObjectFieldsCountByListTypeDefinitionId(
										serviceBuilderListTypeDefinition.
											getListTypeDefinitionId());

							if ((count > 0) ||
								serviceBuilderListTypeDefinition.isSystem()) {

								return null;
							}

							return addAction(
								ActionKeys.DELETE, "deleteListTypeDefinition",
								permissionName,
								serviceBuilderListTypeDefinition.
									getListTypeDefinitionId());
						}
					).put(
						"get",
						addAction(
							ActionKeys.VIEW, "getListTypeDefinition",
							permissionName,
							serviceBuilderListTypeDefinition.
								getListTypeDefinitionId())
					).put(
						"permissions",
						addAction(
							ActionKeys.PERMISSIONS, "patchListTypeDefinition",
							permissionName,
							serviceBuilderListTypeDefinition.
								getListTypeDefinitionId())
					).put(
						"update",
						addAction(
							ActionKeys.UPDATE, "putListTypeDefinition",
							permissionName,
							serviceBuilderListTypeDefinition.
								getListTypeDefinitionId())
					).build());
				setCreator(
					() -> CreatorUtil.toCreator(_portal, contextUriInfo, user));
				setDateCreated(serviceBuilderListTypeDefinition::getCreateDate);
				setDateModified(
					serviceBuilderListTypeDefinition::getModifiedDate);
				setExternalReferenceCode(
					serviceBuilderListTypeDefinition::getExternalReferenceCode);
				setId(
					serviceBuilderListTypeDefinition::getListTypeDefinitionId);
				setListTypeEntries(
					() -> transformToArray(
						_listTypeEntryLocalService.getListTypeEntries(
							serviceBuilderListTypeDefinition.
								getListTypeDefinitionId(),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS),
						listTypeEntry -> ListTypeEntryUtil.toListTypeEntry(
							null, locale, _portal, listTypeEntry,
							contextUriInfo,
							_userLocalService.fetchUser(
								listTypeEntry.getUserId())),
						ListTypeEntry.class));
				setName(() -> serviceBuilderListTypeDefinition.getName(locale));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						serviceBuilderListTypeDefinition.getNameMap()));
				setPermissions(
					() -> NestedFieldsSupplier.supply(
						"permissions",
						nestedFieldNames -> {
							_permissionService.checkPermission(
								contextCompany.getGroupId(), permissionName,
								serviceBuilderListTypeDefinition.
									getListTypeDefinitionId());

							Collection<Permission> permissions =
								PermissionUtil.getPermissions(
									serviceBuilderListTypeDefinition.
										getCompanyId(),
									resourceActionLocalService.
										getResourceActions(permissionName),
									serviceBuilderListTypeDefinition.
										getListTypeDefinitionId(),
									permissionName, null);

							return permissions.toArray(new Permission[0]);
						}));
				setStatus(
					() -> new Status() {
						{
							setCode(
								serviceBuilderListTypeDefinition::getStatus);
							setLabel(
								() -> WorkflowConstants.getStatusLabel(
									serviceBuilderListTypeDefinition.
										getStatus()));
							setLabel_i18n(
								() -> LanguageUtil.get(
									LanguageResources.getResourceBundle(locale),
									WorkflowConstants.getStatusLabel(
										serviceBuilderListTypeDefinition.
											getStatus())));
						}
					});
				setSystem(serviceBuilderListTypeDefinition::isSystem);
			}
		};
	}

	private static final EntityModel _entityModel =
		new ListTypeDefinitionEntityModel();

	@Reference
	private ListTypeDefinitionService _listTypeDefinitionService;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private PermissionService _permissionService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}