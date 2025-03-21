/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.internal.search;

import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.service.CommerceTierPriceEntryLocalService;
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
public class CommerceTierPriceEntryIndexer
	extends BaseIndexer<CommerceTierPriceEntry> {

	public static final String CLASS_NAME =
		CommerceTierPriceEntry.class.getName();

	public static final String FIELD_COMMERCE_PRICE_ENTRY_ID =
		"commercePriceEntryId";

	public static final String FIELD_EXTERNAL_REFERENCE_CODE =
		"externalReferenceCode";

	public CommerceTierPriceEntryIndexer() {
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

		long commercePriceEntryId = GetterUtil.getLong(
			searchContext.getAttribute(FIELD_COMMERCE_PRICE_ENTRY_ID));

		if (commercePriceEntryId > 0) {
			contextBooleanFilter.addRequiredTerm(
				FIELD_COMMERCE_PRICE_ENTRY_ID, commercePriceEntryId);
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
	protected void doDelete(CommerceTierPriceEntry commerceTierPriceEntry)
		throws Exception {

		deleteDocument(
			commerceTierPriceEntry.getCompanyId(),
			commerceTierPriceEntry.getCommercePriceEntryId());
	}

	@Override
	protected Document doGetDocument(
			CommerceTierPriceEntry commerceTierPriceEntry)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Indexing commerce tier price entry " + commerceTierPriceEntry);
		}

		Document document = getBaseModelDocument(
			CLASS_NAME, commerceTierPriceEntry);

		document.addKeyword(
			FIELD_COMMERCE_PRICE_ENTRY_ID,
			commerceTierPriceEntry.getCommercePriceEntryId());
		document.addKeyword(
			FIELD_EXTERNAL_REFERENCE_CODE,
			commerceTierPriceEntry.getExternalReferenceCode());

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Commerce tier price entry " + commerceTierPriceEntry +
					" indexed successfully");
		}

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletRequest portletRequest, PortletResponse portletResponse) {

		Summary summary = createSummary(
			document, FIELD_COMMERCE_PRICE_ENTRY_ID,
			FIELD_COMMERCE_PRICE_ENTRY_ID);

		summary.setMaxContentLength(200);

		return summary;
	}

	@Override
	protected void doReindex(CommerceTierPriceEntry commerceTierPriceEntry)
		throws Exception {

		_indexWriterHelper.updateDocument(
			commerceTierPriceEntry.getCompanyId(),
			getDocument(commerceTierPriceEntry));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		doReindex(
			_commerceTierPriceEntryLocalService.getCommerceTierPriceEntry(
				classPK));
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		_reindexCommerceTierPriceEntries(companyId);
	}

	private void _reindexCommerceTierPriceEntries(long companyId)
		throws Exception {

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_commerceTierPriceEntryLocalService.
				getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(CommerceTierPriceEntry commerceTierPriceEntry) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(commerceTierPriceEntry));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index commerce tier price entry " +
								commerceTierPriceEntry,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceTierPriceEntryIndexer.class);

	@Reference
	private CommerceTierPriceEntryLocalService
		_commerceTierPriceEntryLocalService;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

}