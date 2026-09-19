/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.impl;

import com.liferay.commerce.payment.model.CommercePaymentEntryAudit;
import com.liferay.commerce.payment.service.base.CommercePaymentEntryAuditLocalServiceBaseImpl;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
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

import java.math.BigDecimal;

import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = "model.class.name=com.liferay.commerce.payment.model.CommercePaymentEntryAudit",
	service = AopService.class
)
public class CommercePaymentEntryAuditLocalServiceImpl
	extends CommercePaymentEntryAuditLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CommercePaymentEntryAudit addCommercePaymentEntryAudit(
			long userId, long commercePaymentEntryId, BigDecimal amount,
			String currencyCode, String logType, String logTypeSettings,
			ServiceContext serviceContext)
		throws PortalException {

		CommercePaymentEntryAudit commercePaymentEntryAudit =
			commercePaymentEntryAuditPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		commercePaymentEntryAudit.setCompanyId(user.getCompanyId());
		commercePaymentEntryAudit.setUserId(user.getUserId());
		commercePaymentEntryAudit.setUserName(user.getFullName());

		commercePaymentEntryAudit.setCommercePaymentEntryId(
			commercePaymentEntryId);
		commercePaymentEntryAudit.setAmount(amount);
		commercePaymentEntryAudit.setCurrencyCode(currencyCode);
		commercePaymentEntryAudit.setLogType(logType);
		commercePaymentEntryAudit.setLogTypeSettings(logTypeSettings);

		commercePaymentEntryAudit = commercePaymentEntryAuditPersistence.update(
			commercePaymentEntryAudit);

		_resourceLocalService.addModelResources(
			commercePaymentEntryAudit, serviceContext);

		return commercePaymentEntryAudit;
	}

	@Override
	public CommercePaymentEntryAudit deleteCommercePaymentEntryAudit(
			long commercePaymentEntryAuditId)
		throws PortalException {

		CommercePaymentEntryAudit commercePaymentEntryAudit =
			commercePaymentEntryAuditPersistence.remove(
				commercePaymentEntryAuditId);

		_resourceLocalService.deleteResource(
			commercePaymentEntryAudit.getCompanyId(),
			CommercePaymentEntryAudit.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			commercePaymentEntryAudit.getCommercePaymentEntryAuditId());

		return commercePaymentEntryAudit;
	}

	@Override
	public void deleteCommercePaymentEntryAudits(long commercePaymentEntryId)
		throws PortalException {

		List<CommercePaymentEntryAudit> commercePaymentEntryAudits =
			commercePaymentEntryAuditPersistence.findByCommercePaymentEntryId(
				commercePaymentEntryId);

		for (CommercePaymentEntryAudit commercePaymentEntryAudit :
				commercePaymentEntryAudits) {

			commercePaymentEntryAuditLocalService.
				deleteCommercePaymentEntryAudit(
					commercePaymentEntryAudit.getCommercePaymentEntryAuditId());
		}
	}

	@Override
	public List<CommercePaymentEntryAudit> getCommercePaymentEntryAudits(
		long commercePaymentEntryId, int start, int end,
		OrderByComparator<CommercePaymentEntryAudit> orderByComparator) {

		return commercePaymentEntryAuditPersistence.
			findByCommercePaymentEntryId(
				commercePaymentEntryId, start, end, orderByComparator);
	}

	@Override
	public int getCommercePaymentEntryAuditsCount(long commercePaymentEntryId) {
		return commercePaymentEntryAuditPersistence.
			countByCommercePaymentEntryId(commercePaymentEntryId);
	}

	@Override
	public BaseModelSearchResult<CommercePaymentEntryAudit>
		searchCommercePaymentEntryAudits(
			long companyId, String keywords,
			LinkedHashMap<String, Object> params, int start, int end,
			String orderByField, boolean reverse) {

		SearchResponse searchResponse = _searcher.search(
			_getSearchRequest(
				companyId, keywords, params, start, end, orderByField,
				reverse));

		SearchHits searchHits = searchResponse.getSearchHits();

		List<CommercePaymentEntryAudit> commercePaymentEntries =
			TransformUtil.transform(
				searchHits.getSearchHits(),
				searchHit -> {
					Document document = searchHit.getDocument();

					long commercePaymentEntryAuditId = document.getLong(
						Field.ENTRY_CLASS_PK);

					CommercePaymentEntryAudit commercePaymentEntryAudit =
						fetchCommercePaymentEntryAudit(
							commercePaymentEntryAuditId);

					if (commercePaymentEntryAudit == null) {
						Indexer<CommercePaymentEntryAudit>
							commercePaymentEntryAuditIndexer =
								IndexerRegistryUtil.getIndexer(
									CommercePaymentEntryAudit.class);

						commercePaymentEntryAuditIndexer.delete(
							document.getLong(Field.COMPANY_ID),
							document.getString(Field.UID));
					}

					return commercePaymentEntryAudit;
				});

		return new BaseModelSearchResult<>(
			commercePaymentEntries, searchResponse.getTotalHits());
	}

	private SearchRequest _getSearchRequest(
		long companyId, String keywords, LinkedHashMap<String, Object> params,
		int start, int end, String orderByField, boolean reverse) {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.entryClassNames(
			CommercePaymentEntryAudit.class.getName()
		).emptySearchEnabled(
			true
		).highlightEnabled(
			false
		).withSearchContext(
			searchContext -> _populateSearchContext(
				searchContext, companyId, keywords, params)
		);

		if (start != QueryUtil.ALL_POS) {
			searchRequestBuilder.from(start);
			searchRequestBuilder.size(end);
		}

		if (Validator.isNotNull(orderByField)) {
			SortOrder sortOrder = SortOrder.ASC;

			if (reverse) {
				sortOrder = SortOrder.DESC;
			}

			FieldSort fieldSort = _sorts.field(
				_sortFieldBuilder.getSortField(
					CommercePaymentEntryAudit.class, orderByField),
				sortOrder);

			searchRequestBuilder.sorts(fieldSort);
		}

		return searchRequestBuilder.build();
	}

	private void _populateSearchContext(
		SearchContext searchContext, long companyId, String keywords,
		LinkedHashMap<String, Object> params) {

		searchContext.setCompanyId(companyId);

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		if (MapUtil.isEmpty(params)) {
			return;
		}

		long[] commercePaymentEntryIds = (long[])params.get(
			"commercePaymentEntryIds");

		if (ArrayUtil.isNotEmpty(commercePaymentEntryIds)) {
			searchContext.setAttribute(
				"commercePaymentEntryIds", commercePaymentEntryIds);
		}

		String[] logTypes = (String[])params.get("logTypes");

		if (ArrayUtil.isNotEmpty(logTypes)) {
			searchContext.setAttribute("logTypes", logTypes);
		}

		long permissionUserId = GetterUtil.getLong(
			params.get("permissionUserId"));

		if (permissionUserId > 0) {
			searchContext.setUserId(permissionUserId);
		}
	}

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Searcher _searcher;

	@Reference
	private SortFieldBuilder _sortFieldBuilder;

	@Reference
	private Sorts _sorts;

	@Reference
	private UserLocalService _userLocalService;

}