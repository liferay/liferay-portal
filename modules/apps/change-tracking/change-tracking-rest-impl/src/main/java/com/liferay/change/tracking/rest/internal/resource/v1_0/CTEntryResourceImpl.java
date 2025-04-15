/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.internal.resource.v1_0;

import com.liferay.change.tracking.exception.NoSuchEntryException;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.rest.dto.v1_0.CTEntry;
import com.liferay.change.tracking.rest.internal.odata.entity.v1_0.CTEntryEntityModel;
import com.liferay.change.tracking.rest.resource.v1_0.CTEntryResource;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.spi.history.CTCollectionHistoryProvider;
import com.liferay.change.tracking.spi.history.CTCollectionHistoryProviderRegistry;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author David Truong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/ct-entry.properties",
	scope = ServiceScope.PROTOTYPE, service = CTEntryResource.class
)
public class CTEntryResourceImpl extends BaseCTEntryResourceImpl {

	@Override
	public Page<CTEntry> getCtCollectionCTEntriesPage(
			Long ctCollectionId, String search, Boolean showHideable,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			com.liferay.change.tracking.model.CTEntry.class.getName(), search,
			pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute("ctCollectionId", ctCollectionId);
				searchContext.setAttribute("showHideable", showHideable);
				searchContext.setCompanyId(contextCompany.getCompanyId());

				if (Validator.isNotNull(search)) {
					searchContext.setKeywords(search);
				}
			},
			sorts,
			document -> _toCTEntry(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public CTEntry
			getCtCollectionCTEntryByModelClassNameByModelClassPkModelClassPK(
				Long ctCollectionId, Long modelClassNameId, Long modelClassPK)
		throws Exception {

		CTCollectionHistoryProvider<?> ctCollectionHistoryProvider =
			_ctCollectionHistoryProviderRegistry.getCTCollectionHistoryProvider(
				modelClassNameId);

		com.liferay.change.tracking.model.CTEntry ctEntry =
			ctCollectionHistoryProvider.getCTEntry(
				ctCollectionId, modelClassNameId, modelClassPK);

		if (ctEntry == null) {
			throw new NoSuchEntryException(
				StringBundler.concat(
					"No change tracking entry exists with change tracking ",
					"collection ID ", ctCollectionId, ", model class name ID ",
					modelClassNameId, ", and model class PK ", modelClassPK));
		}

		return _ctEntryDTOConverter.toDTO(
			_getDTOConverterContext(ctEntry), ctEntry);
	}

	@Override
	public Page<CTEntry> getCTEntriesHistoryPage(
			Long classNameId, Long classPK, String search, Long siteId,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		if (ArrayUtil.isEmpty(sorts)) {
			sorts = new Sort[] {
				new Sort(Field.getSortableFieldName(Field.MODIFIED_DATE), true)
			};
		}

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			com.liferay.change.tracking.model.CTEntry.class.getName(), search,
			pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				CTCollectionHistoryProvider<?> ctCollectionHistoryProvider =
					_ctCollectionHistoryProviderRegistry.
						getCTCollectionHistoryProvider(classNameId);

				UnsafeConsumer<SearchUtil.SearchContext, Exception>
					unsafeConsumer =
						ctCollectionHistoryProvider.
							getSearchContextUnsafeConsumer(
								classNameId, GetterUtil.getLong(classPK));

				unsafeConsumer.accept(searchContext);

				if (siteId != null) {
					searchContext.setAttribute(
						Field.GROUP_ID, new Long[] {siteId});
				}

				searchContext.setAttribute("ctCollectionId", -1);
				searchContext.setCompanyId(contextCompany.getCompanyId());

				BooleanFilter booleanFilter = new BooleanFilter();

				booleanFilter.addTerm(
					"ctCollectionId",
					String.valueOf(CTCollectionThreadLocal.getCTCollectionId()),
					BooleanClauseOccur.MUST_NOT);
				booleanFilter.addTerm(
					"ctCollectionStatus",
					String.valueOf(WorkflowConstants.STATUS_EXPIRED),
					BooleanClauseOccur.MUST_NOT);

				searchContext.setBooleanClauses(
					new BooleanClause[] {
						BooleanClauseFactoryUtil.create(
							new BooleanQueryImpl() {
								{
									if (filter != null) {
										booleanFilter.add(
											filter, BooleanClauseOccur.MUST);
									}

									setPreBooleanFilter(booleanFilter);
								}
							},
							BooleanClauseOccur.MUST.getName())
					});

				if (Validator.isNotNull(search)) {
					searchContext.setKeywords(search);
				}
			},
			sorts,
			document -> _toCTEntry(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public CTEntry getCTEntry(Long ctEntryId) throws Exception {
		return _toCTEntry(ctEntryId);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	private <T extends BaseModel<T>> DefaultDTOConverterContext
			_getDTOConverterContext(
				com.liferay.change.tracking.model.CTEntry ctEntry)
		throws Exception {

		CTCollection ctCollection = _ctCollectionLocalService.getCTCollection(
			ctEntry.getCtCollectionId());

		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(),
			HashMapBuilder.put(
				"checkout",
				() -> {
					if (ctCollection.getStatus() !=
							WorkflowConstants.STATUS_DRAFT) {

						return null;
					}

					return addAction(
						ActionKeys.UPDATE, ctEntry.getCtCollectionId(),
						"getCTEntry", _ctCollectionModelResourcePermission);
				}
			).put(
				"delete",
				() -> {
					if (ctCollection.getStatus() !=
							WorkflowConstants.STATUS_DRAFT) {

						return null;
					}

					return addAction(
						ActionKeys.DELETE, ctEntry.getCtCollectionId(),
						"getCTEntry", _ctCollectionModelResourcePermission);
				}
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, ctEntry.getCtCollectionId(), "getCTEntry",
					_ctCollectionModelResourcePermission)
			).put(
				"move-changes",
				() -> {
					if ((ctCollection.getStatus() !=
							WorkflowConstants.STATUS_DRAFT) &&
						(ctCollection.getStatus() !=
							WorkflowConstants.STATUS_EXPIRED)) {

						return null;
					}

					return addAction(
						ActionKeys.UPDATE, ctEntry.getCtCollectionId(),
						"getCTEntry", _ctCollectionModelResourcePermission);
				}
			).put(
				"update",
				() -> {
					if (ctCollection.getStatus() !=
							WorkflowConstants.STATUS_DRAFT) {

						return null;
					}

					return addAction(
						ActionKeys.UPDATE, ctEntry.getCtCollectionId(),
						"getCTEntry", _ctCollectionModelResourcePermission);
				}
			).put(
				"view-discard",
				() -> {
					if (ctCollection.getStatus() !=
							WorkflowConstants.STATUS_DRAFT) {

						return null;
					}

					return addAction(
						ActionKeys.UPDATE, ctEntry.getCtCollectionId(),
						"getCTEntry", _ctCollectionModelResourcePermission);
				}
			).build(),
			null, contextHttpServletRequest, ctEntry.getCtCollectionId(),
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	private CTEntry _toCTEntry(Long ctEntryId) throws Exception {
		com.liferay.change.tracking.model.CTEntry ctEntry =
			_ctEntryLocalService.getCTEntry(ctEntryId);

		return _ctEntryDTOConverter.toDTO(
			_getDTOConverterContext(ctEntry), ctEntry);
	}

	private static final EntityModel _entityModel = new CTEntryEntityModel();

	@Reference
	private CTCollectionHistoryProviderRegistry
		_ctCollectionHistoryProviderRegistry;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.change.tracking.model.CTCollection)"
	)
	private volatile ModelResourcePermission<CTCollection>
		_ctCollectionModelResourcePermission;

	@Reference(
		target = "(component.name=com.liferay.change.tracking.rest.internal.dto.v1_0.converter.CTEntryDTOConverter)"
	)
	private DTOConverter<com.liferay.change.tracking.model.CTEntry, CTEntry>
		_ctEntryDTOConverter;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

}