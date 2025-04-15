/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.internal.search;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListAccountRel;
import com.liferay.commerce.price.list.model.CommercePriceListChannelRel;
import com.liferay.commerce.price.list.model.CommercePriceListCommerceAccountGroupRel;
import com.liferay.commerce.price.list.model.CommercePriceListOrderTypeRel;
import com.liferay.commerce.price.list.service.CommercePriceListAccountRelLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListChannelRelLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListCommerceAccountGroupRelLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListOrderTypeRelLocalService;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.MissingFilter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.filter.FilterBuilders;
import com.liferay.portal.search.filter.TermsSetFilterBuilder;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
@Component(service = Indexer.class)
public class CommercePriceListIndexer extends BaseIndexer<CommercePriceList> {

	public static final String CLASS_NAME = CommercePriceList.class.getName();

	public static final String FIELD_EXTERNAL_REFERENCE_CODE =
		"externalReferenceCode";

	public CommercePriceListIndexer() {
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.GROUP_ID, Field.MODIFIED_DATE, Field.NAME,
			Field.SCOPE_GROUP_ID, Field.UID);
		setFilterSearch(true);
		setPermissionAware(true);
	}

	@Override
	public void delete(long companyId, String uuid) throws SearchException {
		super.delete(companyId, uuid);

		_commercePriceListLocalService.cleanPriceListCache();
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		int[] statuses = GetterUtil.getIntegerValues(
			searchContext.getAttribute(Field.STATUS), null);

		if (ArrayUtil.isEmpty(statuses)) {
			int status = GetterUtil.getInteger(
				searchContext.getAttribute(Field.STATUS),
				WorkflowConstants.STATUS_APPROVED);

			statuses = new int[] {status};
		}

		if (!ArrayUtil.contains(statuses, WorkflowConstants.STATUS_ANY)) {
			TermsFilter statusesTermsFilter = new TermsFilter(Field.STATUS);

			statusesTermsFilter.addValues(ArrayUtil.toStringArray(statuses));

			contextBooleanFilter.add(
				statusesTermsFilter, BooleanClauseOccur.MUST);
		}
		else {
			contextBooleanFilter.addTerm(
				Field.STATUS, String.valueOf(WorkflowConstants.STATUS_IN_TRASH),
				BooleanClauseOccur.MUST_NOT);
		}

		long[] commerceAccountGroupIds = GetterUtil.getLongValues(
			searchContext.getAttribute("commerceAccountGroupIds"), null);

		if (ArrayUtil.isNotEmpty(commerceAccountGroupIds)) {
			TermsSetFilterBuilder termsSetFilterBuilder =
				_filterBuilders.termsSetFilterBuilder();

			termsSetFilterBuilder.setFieldName("commerceAccountGroupIds");
			termsSetFilterBuilder.setMinimumShouldMatchField(
				"commerceAccountGroupIds_required_matches");

			List<String> values = new ArrayList<>(
				commerceAccountGroupIds.length);

			for (long commerceAccountGroupId : commerceAccountGroupIds) {
				values.add(String.valueOf(commerceAccountGroupId));
			}

			termsSetFilterBuilder.setValues(values);

			BooleanFilter fieldBooleanFilter = new BooleanFilter();

			fieldBooleanFilter.add(
				new TermFilter("commerceAccountGroupIds_required_matches", "0"),
				BooleanClauseOccur.SHOULD);
			fieldBooleanFilter.add(
				termsSetFilterBuilder.build(), BooleanClauseOccur.SHOULD);

			contextBooleanFilter.add(
				fieldBooleanFilter, BooleanClauseOccur.MUST);
		}

		long commerceAccountId = GetterUtil.getLong(
			searchContext.getAttribute("commerceAccountId"));

		if (commerceAccountId > 0) {
			BooleanFilter commerceAccountBooleanFilter = new BooleanFilter();

			commerceAccountBooleanFilter.add(
				new MissingFilter("commerceAccountId"),
				BooleanClauseOccur.SHOULD);

			commerceAccountBooleanFilter.addTerm(
				"commerceAccountId", String.valueOf(commerceAccountId),
				BooleanClauseOccur.SHOULD);

			contextBooleanFilter.add(
				commerceAccountBooleanFilter, BooleanClauseOccur.MUST);
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		addSearchTerm(searchQuery, searchContext, Field.ENTRY_CLASS_PK, false);
		addSearchTerm(searchQuery, searchContext, Field.NAME, false);
		addSearchTerm(searchQuery, searchContext, Field.USER_NAME, false);
		addSearchTerm(
			searchQuery, searchContext, FIELD_EXTERNAL_REFERENCE_CODE, false);

		LinkedHashMap<String, Object> params =
			(LinkedHashMap<String, Object>)searchContext.getAttribute("params");

		if (params != null) {
			String expandoAttributes = (String)params.get("expandoAttributes");

			if (Validator.isNotNull(expandoAttributes)) {
				addSearchExpando(searchQuery, searchContext, expandoAttributes);
			}
		}
	}

	@Override
	protected void doDelete(CommercePriceList commercePriceList)
		throws Exception {

		deleteDocument(
			commercePriceList.getCompanyId(),
			commercePriceList.getCommercePriceListId());

		_commercePriceListLocalService.cleanPriceListCache();
	}

	@Override
	protected Document doGetDocument(CommercePriceList commercePriceList)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Indexing commerce price list " + commercePriceList);
		}

		Document document = getBaseModelDocument(CLASS_NAME, commercePriceList);

		document.addNumber(
			Field.ENTRY_CLASS_PK, commercePriceList.getCommercePriceListId());
		document.addText(Field.NAME, commercePriceList.getName());
		document.addNumberSortable(
			Field.PRIORITY, commercePriceList.getPriority());
		document.addText(Field.USER_NAME, commercePriceList.getUserName());
		document.addKeyword(
			FIELD_EXTERNAL_REFERENCE_CODE,
			commercePriceList.getExternalReferenceCode());

		long commerceCatalogId = _getCatalogId(commercePriceList);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.fetchCommerceCatalog(
				commerceCatalogId);

		if (commerceCatalog != null) {
			document.addKeyword(
				"accountEntryId", commerceCatalog.getAccountEntryId());
		}

		document.addKeyword(
			"catalogBasePriceList", commercePriceList.isCatalogBasePriceList());
		document.addNumber("catalogId", commerceCatalogId);

		long[] commerceAccountGroupIds = TransformUtil.transformToLongArray(
			_commercePriceListCommerceAccountGroupRelLocalService.
				getCommercePriceListCommerceAccountGroupRels(
					commercePriceList.getCommercePriceListId()),
			CommercePriceListCommerceAccountGroupRel::
				getCommerceAccountGroupId);

		document.addNumber("commerceAccountGroupIds", commerceAccountGroupIds);
		document.addNumber(
			"commerceAccountGroupIds_required_matches",
			commerceAccountGroupIds.length);

		document.addNumber(
			"commerceAccountId",
			TransformUtil.transformToLongArray(
				_commercePriceListAccountRelLocalService.
					getCommercePriceListAccountRels(
						commercePriceList.getCommercePriceListId()),
				CommercePriceListAccountRel::getCommerceAccountId));
		document.addNumber(
			"commerceChannelId",
			TransformUtil.transformToLongArray(
				_commercePriceListChannelRelLocalService.
					getCommercePriceListChannelRels(
						commercePriceList.getCommercePriceListId()),
				CommercePriceListChannelRel::getCommerceChannelId));
		document.addNumber(
			"commerceOrderTypeId",
			TransformUtil.transformToLongArray(
				_commercePriceListOrderTypeRelLocalService.
					getCommercePriceListOrderTypeRels(
						commercePriceList.getCommercePriceListId()),
				CommercePriceListOrderTypeRel::getCommerceOrderTypeId));
		document.addText("type", commercePriceList.getType());

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Commerce price list " + commercePriceList +
					" indexed successfully");
		}

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletRequest portletRequest, PortletResponse portletResponse) {

		Summary summary = createSummary(
			document, Field.ENTRY_CLASS_PK, Field.NAME);

		summary.setMaxContentLength(200);

		return summary;
	}

	@Override
	protected void doReindex(CommercePriceList commercePriceList)
		throws Exception {

		_indexWriterHelper.updateDocument(
			commercePriceList.getCompanyId(), getDocument(commercePriceList));

		_commercePriceListLocalService.cleanPriceListCache();
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		doReindex(_commercePriceListLocalService.getCommercePriceList(classPK));
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		_reindexCommercePriceLists(companyId);

		_commercePriceListLocalService.cleanPriceListCache();
	}

	private long _getCatalogId(CommercePriceList commercePriceList)
		throws Exception {

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.fetchCommerceCatalogByGroupId(
				commercePriceList.getGroupId());

		if (commerceCatalog == null) {
			return 0L;
		}

		return commerceCatalog.getCommerceCatalogId();
	}

	private void _reindexCommercePriceLists(long companyId) throws Exception {
		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_commercePriceListLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(CommercePriceList commercePriceList) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(commercePriceList));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index commerce price list " +
								commercePriceList,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePriceListIndexer.class);

	@Reference
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Reference
	private CommercePriceListAccountRelLocalService
		_commercePriceListAccountRelLocalService;

	@Reference
	private CommercePriceListChannelRelLocalService
		_commercePriceListChannelRelLocalService;

	@Reference
	private CommercePriceListCommerceAccountGroupRelLocalService
		_commercePriceListCommerceAccountGroupRelLocalService;

	@Reference
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Reference
	private CommercePriceListOrderTypeRelLocalService
		_commercePriceListOrderTypeRelLocalService;

	@Reference
	private FilterBuilders _filterBuilders;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

}