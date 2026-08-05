/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.commerce.product.exception.CPTaxCategoryNameException;
import com.liferay.commerce.product.exception.DuplicateCPTaxCategoryException;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.base.CPTaxCategoryLocalServiceBaseImpl;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.FieldSort;
import com.liferay.portal.search.sort.SortFieldBuilder;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.util.PortalInstances;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPTaxCategory",
	service = AopService.class
)
public class CPTaxCategoryLocalServiceImpl
	extends CPTaxCategoryLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPTaxCategory addCPTaxCategory(
			String externalReferenceCode, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, ServiceContext serviceContext)
		throws PortalException {

		// Commerce product tax category

		User user = _userLocalService.getUser(serviceContext.getUserId());

		_validate(user.getCompanyId(), 0, externalReferenceCode, nameMap);

		long cpTaxCategoryId = counterLocalService.increment();

		CPTaxCategory cpTaxCategory = cpTaxCategoryPersistence.create(
			cpTaxCategoryId);

		cpTaxCategory.setExternalReferenceCode(externalReferenceCode);
		cpTaxCategory.setCompanyId(user.getCompanyId());
		cpTaxCategory.setUserId(user.getUserId());
		cpTaxCategory.setUserName(user.getFullName());
		cpTaxCategory.setNameMap(nameMap);
		cpTaxCategory.setDescriptionMap(descriptionMap);

		if (_emptyModelManager.isEmptyModel()) {
			cpTaxCategory.setStatus(WorkflowConstants.STATUS_EMPTY);
		}
		else {
			cpTaxCategory.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		cpTaxCategory = cpTaxCategoryPersistence.update(cpTaxCategory);

		// Resources

		_resourceLocalService.addResources(
			cpTaxCategory.getCompanyId(), 0, user.getUserId(),
			CPTaxCategory.class.getName(), cpTaxCategory.getCPTaxCategoryId(),
			false, false, false);

		return cpTaxCategory;
	}

	@Override
	public int countCPTaxCategoriesByCompanyId(long companyId, String keyword) {
		return cpTaxCategoryFinder.countCPTaxCategoriesByCompanyId(
			companyId, keyword);
	}

	@Override
	public void deleteCPTaxCategories(long companyId) {
		if (!PortalInstances.isCurrentCompanyInDeletionProcess()) {
			throw new UnsupportedOperationException(
				"Deleting tax categories by company must be called when " +
					"deleting a company");
		}

		cpTaxCategoryPersistence.removeByCompanyId(companyId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPTaxCategory deleteCPTaxCategory(CPTaxCategory cpTaxCategory)
		throws PortalException {

		// Commerce product tax category

		cpTaxCategory = cpTaxCategoryPersistence.remove(cpTaxCategory);

		// Commerce product definitions

		_cpDefinitionLocalService.updateCPDefinitionsByCPTaxCategoryId(
			cpTaxCategory.getCPTaxCategoryId());

		// Resources

		_resourceLocalService.deleteResource(
			cpTaxCategory.getCompanyId(), CPTaxCategory.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			cpTaxCategory.getCPTaxCategoryId());

		return cpTaxCategory;
	}

	@Override
	public CPTaxCategory deleteCPTaxCategory(long cpTaxCategoryId)
		throws PortalException {

		CPTaxCategory cpTaxCategory = cpTaxCategoryPersistence.findByPrimaryKey(
			cpTaxCategoryId);

		return cpTaxCategoryLocalService.deleteCPTaxCategory(cpTaxCategory);
	}

	@Override
	public List<CPTaxCategory> findCPTaxCategoriesByCompanyId(
		long companyId, String keyword, int start, int end) {

		return cpTaxCategoryFinder.findCPTaxCategoriesByCompanyId(
			companyId, keyword, start, end);
	}

	@Override
	public List<CPTaxCategory> getCPTaxCategories(long companyId) {
		return cpTaxCategoryPersistence.findByCompanyId(companyId);
	}

	@Override
	public List<CPTaxCategory> getCPTaxCategories(
		long companyId, int start, int end,
		OrderByComparator<CPTaxCategory> orderByComparator) {

		return cpTaxCategoryPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public int getCPTaxCategoriesCount(long companyId) {
		return cpTaxCategoryPersistence.countByCompanyId(companyId);
	}

	@Override
	public CPTaxCategory getOrAddEmptyCPTaxCategory(
			String externalReferenceCode, long companyId, long userId)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		return _emptyModelManager.getOrAddEmptyModel(
			CPTaxCategory.class, companyId,
			() -> cpTaxCategoryLocalService.addCPTaxCategory(
				externalReferenceCode,
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), externalReferenceCode),
				null, serviceContext),
			externalReferenceCode,
			this::fetchCPTaxCategoryByExternalReferenceCode,
			this::getCPTaxCategoryByExternalReferenceCode,
			CPTaxCategory.class.getName());
	}

	@Override
	public BaseModelSearchResult<CPTaxCategory> searchCPTaxCategories(
			long companyId, String keywords, int start, int end, Sort sort)
		throws PortalException {

		SearchResponse searchResponse = _searcher.search(
			_getSearchRequest(companyId, keywords, start, end, sort));

		SearchHits searchHits = searchResponse.getSearchHits();

		List<CPTaxCategory> cpTaxCategories = TransformUtil.transform(
			searchHits.getSearchHits(),
			searchHit -> {
				Document document = searchHit.getDocument();

				long cpTaxCategoryId = document.getLong(Field.ENTRY_CLASS_PK);

				CPTaxCategory cpTaxCategory = fetchCPTaxCategory(
					cpTaxCategoryId);

				if (cpTaxCategory == null) {
					Indexer<CPTaxCategory> indexer =
						IndexerRegistryUtil.getIndexer(CPTaxCategory.class);

					indexer.delete(
						document.getLong(Field.COMPANY_ID),
						document.getString(Field.UID));
				}

				return cpTaxCategory;
			});

		return new BaseModelSearchResult<>(
			cpTaxCategories, searchResponse.getTotalHits());
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CPTaxCategory updateCPTaxCategory(
			String externalReferenceCode, long cpTaxCategoryId,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap)
		throws PortalException {

		CPTaxCategory cpTaxCategory = cpTaxCategoryPersistence.findByPrimaryKey(
			cpTaxCategoryId);

		_validate(
			cpTaxCategory.getCompanyId(), cpTaxCategoryId,
			externalReferenceCode, nameMap);

		cpTaxCategory.setExternalReferenceCode(externalReferenceCode);
		cpTaxCategory.setNameMap(nameMap);
		cpTaxCategory.setDescriptionMap(descriptionMap);
		cpTaxCategory.setStatus(
			_emptyModelManager.solveEmptyModel(
				cpTaxCategory.getExternalReferenceCode(),
				cpTaxCategory.getModelClassName(), cpTaxCategory.getCompanyId(),
				0, cpTaxCategory.getStatus(),
				() -> WorkflowConstants.STATUS_APPROVED));

		return cpTaxCategoryPersistence.update(cpTaxCategory);
	}

	private SearchRequest _getSearchRequest(
		long companyId, String keywords, int start, int end, Sort sort) {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.entryClassNames(
			CPTaxCategory.class.getName()
		).emptySearchEnabled(
			true
		).highlightEnabled(
			false
		).withSearchContext(
			searchContext -> _populateSearchContext(
				searchContext, companyId, keywords, start, end, sort)
		);

		if (start != QueryUtil.ALL_POS) {
			searchRequestBuilder.from(start);
			searchRequestBuilder.size(end);
		}

		if (Validator.isNotNull(sort.getFieldName())) {
			SortOrder sortOrder = SortOrder.ASC;

			if (sort.isReverse()) {
				sortOrder = SortOrder.DESC;
			}

			FieldSort fieldSort = _sorts.field(
				_sortFieldBuilder.getSortField(
					CPTaxCategory.class, sort.getFieldName()),
				sortOrder);

			searchRequestBuilder.sorts(fieldSort);
		}

		return searchRequestBuilder.build();
	}

	private void _populateSearchContext(
		SearchContext searchContext, long companyId, String keywords, int start,
		int end, Sort sort) {

		searchContext.setAttributes(
			HashMapBuilder.<String, Serializable>put(
				Field.DESCRIPTION, keywords
			).put(
				Field.NAME, keywords
			).put(
				"params",
				LinkedHashMapBuilder.<String, Object>put(
					"keywords", keywords
				).build()
			).build());
		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		if (sort != null) {
			searchContext.setSorts(sort);
		}

		searchContext.setStart(start);
	}

	private void _validate(
			long companyId, long cpTaxCategoryId, String externalReferenceCode,
			Map<Locale, String> nameMap)
		throws PortalException {

		if (Validator.isNotNull(externalReferenceCode)) {
			CPTaxCategory cpTaxCategory = cpTaxCategoryPersistence.fetchByERC_C(
				externalReferenceCode, companyId);

			if ((cpTaxCategory != null) &&
				(cpTaxCategory.getCPTaxCategoryId() != cpTaxCategoryId)) {

				throw new DuplicateCPTaxCategoryException(
					"There is another commerce tax category with external " +
						"reference code " + externalReferenceCode);
			}
		}

		_validate(nameMap);
	}

	private void _validate(Map<Locale, String> nameMap) throws PortalException {
		Locale locale = LocaleUtil.getSiteDefault();

		String name = nameMap.get(locale);

		if (Validator.isNull(name)) {
			throw new CPTaxCategoryNameException();
		}
	}

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private EmptyModelManager _emptyModelManager;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private SortFieldBuilder _sortFieldBuilder;

	@Reference
	private Sorts _sorts;

	@Reference
	private UserLocalService _userLocalService;

}