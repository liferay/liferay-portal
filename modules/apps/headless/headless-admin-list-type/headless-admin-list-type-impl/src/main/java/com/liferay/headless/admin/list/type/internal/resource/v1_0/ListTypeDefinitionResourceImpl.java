/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.list.type.internal.resource.v1_0;

import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeEntry;
import com.liferay.headless.admin.list.type.internal.dto.v1_0.util.ListTypeEntryUtil;
import com.liferay.headless.admin.list.type.internal.odata.entity.v1_0.ListTypeDefinitionEntityModel;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeDefinitionResource;
import com.liferay.list.type.constants.ListTypeActionKeys;
import com.liferay.list.type.constants.ListTypeConstants;
import com.liferay.list.type.service.ListTypeDefinitionService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gabriel Albuquerque
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/list-type-definition.properties",
	scope = ServiceScope.PROTOTYPE, service = ListTypeDefinitionResource.class
)
public class ListTypeDefinitionResourceImpl
	extends BaseListTypeDefinitionResourceImpl {

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
				fetchListTypeDefinitionByExternalReferenceCode(
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
						listTypeEntry, _listTypeEntryLocalService))));
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
						listTypeEntry, _listTypeEntryLocalService))));
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
								com.liferay.list.type.model.ListTypeDefinition.
									class.getName(),
								serviceBuilderListTypeDefinition.
									getListTypeDefinitionId());
						}
					).put(
						"get",
						addAction(
							ActionKeys.VIEW, "getListTypeDefinition",
							com.liferay.list.type.model.ListTypeDefinition.
								class.getName(),
							serviceBuilderListTypeDefinition.
								getListTypeDefinitionId())
					).put(
						"permissions",
						addAction(
							ActionKeys.PERMISSIONS, "patchListTypeDefinition",
							com.liferay.list.type.model.ListTypeDefinition.
								class.getName(),
							serviceBuilderListTypeDefinition.
								getListTypeDefinitionId())
					).put(
						"update",
						addAction(
							ActionKeys.UPDATE, "putListTypeDefinition",
							com.liferay.list.type.model.ListTypeDefinition.
								class.getName(),
							serviceBuilderListTypeDefinition.
								getListTypeDefinitionId())
					).build());
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
							null, locale, listTypeEntry),
						ListTypeEntry.class));
				setName(() -> serviceBuilderListTypeDefinition.getName(locale));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						serviceBuilderListTypeDefinition.getNameMap()));
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

}