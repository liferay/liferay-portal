/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.internal.search;

import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.LinkedHashMap;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = Indexer.class)
public class CommercePriceEntryIndexer extends BaseIndexer<CommercePriceEntry> {

	public static final String CLASS_NAME = CommercePriceEntry.class.getName();

	public static final String FIELD_COMMERCE_PRICE_LIST_ID =
		"commercePriceListId";

	public static final String FIELD_EXTERNAL_REFERENCE_CODE =
		"externalReferenceCode";

	public CommercePriceEntryIndexer() {
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.GROUP_ID, Field.MODIFIED_DATE, Field.SCOPE_GROUP_ID,
			Field.UID);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		long commercePriceListId = GetterUtil.getLong(
			searchContext.getAttribute(FIELD_COMMERCE_PRICE_LIST_ID));

		if (commercePriceListId > 0) {
			contextBooleanFilter.addRequiredTerm(
				FIELD_COMMERCE_PRICE_LIST_ID, commercePriceListId);
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		addSearchTerm(searchQuery, searchContext, Field.ENTRY_CLASS_PK, false);
		addSearchTerm(searchQuery, searchContext, Field.USER_NAME, false);
		addSearchTerm(
			searchQuery, searchContext, FIELD_EXTERNAL_REFERENCE_CODE, false);

		addSearchLocalizedTerm(
			searchQuery, searchContext, "cpDefinitionName", false);
		addSearchTerm(searchQuery, searchContext, "sku", false);
		addSearchTerm(
			searchQuery, searchContext, "skuExternalReferenceCode", false);

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
	protected void doDelete(CommercePriceEntry commercePriceEntry)
		throws Exception {

		deleteDocument(
			commercePriceEntry.getCompanyId(),
			commercePriceEntry.getCommercePriceEntryId());
	}

	@Override
	protected Document doGetDocument(CommercePriceEntry commercePriceEntry)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Indexing commerce price entry " + commercePriceEntry);
		}

		Document document = getBaseModelDocument(
			CLASS_NAME, commercePriceEntry);

		document.addKeyword(
			FIELD_COMMERCE_PRICE_LIST_ID,
			commercePriceEntry.getCommercePriceListId());
		document.addKeyword(
			FIELD_EXTERNAL_REFERENCE_CODE,
			commercePriceEntry.getExternalReferenceCode());

		CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
			commercePriceEntry.getCProductId(),
			commercePriceEntry.getCPInstanceUuid());

		if (cpInstance != null) {
			document.addKeyword("cpInstanceId", cpInstance.getCPInstanceId());
			document.addKeyword("sku", cpInstance.getSku());
			document.addKeyword(
				"skuExternalReferenceCode",
				cpInstance.getExternalReferenceCode());

			CPDefinition cpDefinition = cpInstance.getCPDefinition();

			document.addLocalizedKeyword(
				"cpDefinitionName", cpDefinition.getNameMap());
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Commerce price entry " + commercePriceEntry +
					" indexed successfully");
		}

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletRequest portletRequest, PortletResponse portletResponse) {

		Summary summary = createSummary(
			document, FIELD_COMMERCE_PRICE_LIST_ID,
			FIELD_COMMERCE_PRICE_LIST_ID);

		summary.setMaxContentLength(200);

		return summary;
	}

	@Override
	protected void doReindex(CommercePriceEntry commercePriceEntry)
		throws Exception {

		_indexWriterHelper.updateDocument(
			commercePriceEntry.getCompanyId(), getDocument(commercePriceEntry));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		doReindex(
			_commercePriceEntryLocalService.getCommercePriceEntry(classPK));
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		_reindexCommercePriceEntries(companyId);
	}

	private void _reindexCommercePriceEntries(long companyId) throws Exception {
		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_commercePriceEntryLocalService.
				getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(CommercePriceEntry commercePriceEntry) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(commercePriceEntry));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index commerce price entry " +
								commercePriceEntry,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePriceEntryIndexer.class);

	@Reference
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

}