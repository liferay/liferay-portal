/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.list.type.internal.resource.v1_0;

import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeEntry;
import com.liferay.headless.admin.list.type.internal.dto.v1_0.util.ListTypeEntryUtil;
import com.liferay.headless.admin.list.type.internal.odata.entity.v1_0.ListTypeEntryEntityModel;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeEntryResource;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeDefinitionService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.list.type.service.ListTypeEntryService;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gabriel Albuquerque
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/list-type-entry.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = ListTypeEntryResource.class
)
public class ListTypeEntryResourceImpl extends BaseListTypeEntryResourceImpl {

	@Override
	public void deleteListTypeEntry(Long listTypeEntryId) throws Exception {
		_listTypeEntryService.deleteListTypeEntry(listTypeEntryId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Page<ListTypeEntry>
			getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
				String externalReferenceCode, String search,
				Aggregation aggregation, Filter filter, Pagination pagination,
				Sort[] sorts)
		throws Exception {

		com.liferay.list.type.model.ListTypeDefinition
			serviceBuilderlistTypeDefinition =
				_listTypeDefinitionService.
					getListTypeDefinitionByExternalReferenceCode(
						externalReferenceCode, contextCompany.getCompanyId());

		return getListTypeDefinitionListTypeEntriesPage(
			serviceBuilderlistTypeDefinition.getListTypeDefinitionId(), search,
			aggregation, filter, pagination, sorts);
	}

	@NestedField(
		parentClass = ListTypeDefinition.class, value = "listTypeEntries"
	)
	@Override
	public Page<ListTypeEntry> getListTypeDefinitionListTypeEntriesPage(
			Long listTypeDefinitionId, String search, Aggregation aggregation,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			HashMapBuilder.put(
				"create",
				addAction(
					ActionKeys.UPDATE, "postListTypeDefinitionListTypeEntry",
					com.liferay.list.type.model.ListTypeDefinition.class.
						getName(),
					listTypeDefinitionId)
			).put(
				"createBatch",
				addAction(
					ActionKeys.UPDATE,
					"postListTypeDefinitionListTypeEntryBatch",
					com.liferay.list.type.model.ListTypeDefinition.class.
						getName(),
					listTypeDefinitionId)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, "getListTypeDefinitionListTypeEntriesPage",
					com.liferay.list.type.model.ListTypeDefinition.class.
						getName(),
					listTypeDefinitionId)
			).build(),
			booleanQuery -> {
			},
			filter, com.liferay.list.type.model.ListTypeEntry.class.getName(),
			search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(Field.NAME, search);
				searchContext.setAttribute("key", search);
				searchContext.setAttribute(
					"listTypeDefinitionId", listTypeDefinitionId);
				searchContext.setCompanyId(contextCompany.getCompanyId());
			},
			sorts,
			document -> {
				com.liferay.list.type.model.ListTypeEntry listTypeEntry =
					_listTypeEntryService.getListTypeEntry(
						GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)));

				return ListTypeEntryUtil.toListTypeEntry(
					_getActions(listTypeEntry),
					contextAcceptLanguage.getPreferredLocale(), listTypeEntry);
			});
	}

	@Override
	public ListTypeEntry getListTypeEntry(Long listTypeEntryId)
		throws Exception {

		return ListTypeEntryUtil.toListTypeEntry(
			null, contextAcceptLanguage.getPreferredLocale(),
			_listTypeEntryService.getListTypeEntry(listTypeEntryId));
	}

	@Override
	public ListTypeEntry
			postListTypeDefinitionByExternalReferenceCodeListTypeEntry(
				String externalReferenceCode, ListTypeEntry listTypeEntry)
		throws Exception {

		com.liferay.list.type.model.ListTypeDefinition
			serviceBuilderlistTypeDefinition =
				_listTypeDefinitionService.
					getListTypeDefinitionByExternalReferenceCode(
						externalReferenceCode, contextCompany.getCompanyId());

		return postListTypeDefinitionListTypeEntry(
			serviceBuilderlistTypeDefinition.getListTypeDefinitionId(),
			listTypeEntry);
	}

	@Override
	public ListTypeEntry postListTypeDefinitionListTypeEntry(
			Long listTypeDefinitionId, ListTypeEntry listTypeEntry)
		throws Exception {

		com.liferay.list.type.model.ListTypeDefinition
			serviceBuilderListTypeDefinition =
				_listTypeDefinitionLocalService.getListTypeDefinition(
					listTypeDefinitionId);

		return ListTypeEntryUtil.toListTypeEntry(
			null, contextAcceptLanguage.getPreferredLocale(),
			_listTypeEntryService.addListTypeEntry(
				listTypeEntry.getExternalReferenceCode(), listTypeDefinitionId,
				listTypeEntry.getKey(),
				LocalizedMapUtil.populateLocalizedMap(
					serviceBuilderListTypeDefinition.getDefaultLanguageId(),
					listTypeEntry.getName_i18n(), listTypeEntry.getName()),
				GetterUtil.getBoolean(listTypeEntry.getSystem())));
	}

	@Override
	public ListTypeEntry putListTypeEntry(
			Long listTypeEntryId, ListTypeEntry listTypeEntry)
		throws Exception {

		com.liferay.list.type.model.ListTypeEntry serviceBuilderListTypeEntry =
			_listTypeEntryLocalService.getListTypeEntry(listTypeEntryId);

		com.liferay.list.type.model.ListTypeDefinition
			serviceBuilderListTypeDefinition =
				_listTypeDefinitionLocalService.getListTypeDefinition(
					serviceBuilderListTypeEntry.getListTypeDefinitionId());

		return ListTypeEntryUtil.toListTypeEntry(
			null, contextAcceptLanguage.getPreferredLocale(),
			_listTypeEntryService.updateListTypeEntry(
				listTypeEntry.getExternalReferenceCode(), listTypeEntryId,
				LocalizedMapUtil.populateLocalizedMap(
					serviceBuilderListTypeDefinition.getDefaultLanguageId(),
					listTypeEntry.getName_i18n(), listTypeEntry.getName())));
	}

	private Map<String, Map<String, String>> _getActions(
		com.liferay.list.type.model.ListTypeEntry serviceBuilderListTypeEntry) {

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			() -> {
				if (serviceBuilderListTypeEntry.isSystem()) {
					return null;
				}

				return addAction(
					ActionKeys.DELETE, "deleteListTypeEntry",
					com.liferay.list.type.model.ListTypeDefinition.class.
						getName(),
					serviceBuilderListTypeEntry.getListTypeDefinitionId());
			}
		).put(
			"get",
			addAction(
				ActionKeys.VIEW, "getListTypeEntry",
				com.liferay.list.type.model.ListTypeDefinition.class.getName(),
				serviceBuilderListTypeEntry.getListTypeDefinitionId())
		).put(
			"update",
			addAction(
				ActionKeys.UPDATE, "putListTypeEntry",
				com.liferay.list.type.model.ListTypeDefinition.class.getName(),
				serviceBuilderListTypeEntry.getListTypeDefinitionId())
		).build();
	}

	private static final EntityModel _entityModel =
		new ListTypeEntryEntityModel();

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Reference
	private ListTypeDefinitionService _listTypeDefinitionService;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ListTypeEntryService _listTypeEntryService;

}