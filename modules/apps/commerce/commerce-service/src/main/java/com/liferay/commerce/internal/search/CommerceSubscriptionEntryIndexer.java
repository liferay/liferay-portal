/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.search;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.service.CommerceSubscriptionEntryLocalService;
import com.liferay.petra.string.StringPool;
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
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = Indexer.class)
public class CommerceSubscriptionEntryIndexer
	extends BaseIndexer<CommerceSubscriptionEntry> {

	public static final String CLASS_NAME =
		CommerceSubscriptionEntry.class.getName();

	public static final String FIELD_COMMERCE_ORDER_ITEM_ID =
		"commerceOrderItemId";

	public static final String FIELD_CP_INSTANCE_ID = "CPInstanceId";

	public static final String FIELD_MAX_SUBSCRIPTION_CYCLES =
		"maxSubscriptionCycles";

	public static final String FIELD_SKU = "sku";

	public static final String FIELD_SUBSCRIPTION_STATUS = "subscriptionStatus";

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		Long maxSubscriptionCycles = (Long)searchContext.getAttribute(
			FIELD_MAX_SUBSCRIPTION_CYCLES);

		if (maxSubscriptionCycles != null) {
			contextBooleanFilter.addTerm(
				FIELD_MAX_SUBSCRIPTION_CYCLES,
				String.valueOf(maxSubscriptionCycles), BooleanClauseOccur.MUST);
		}

		Integer subscriptionStatus = (Integer)searchContext.getAttribute(
			FIELD_SUBSCRIPTION_STATUS);

		if (subscriptionStatus != null) {
			contextBooleanFilter.addTerm(
				FIELD_SUBSCRIPTION_STATUS, String.valueOf(subscriptionStatus),
				BooleanClauseOccur.MUST);
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		addSearchTerm(searchQuery, searchContext, Field.ENTRY_CLASS_PK, false);
		addSearchLocalizedTerm(searchQuery, searchContext, Field.NAME, false);
		addSearchTerm(
			searchQuery, searchContext, FIELD_COMMERCE_ORDER_ITEM_ID, false);
		addSearchTerm(searchQuery, searchContext, FIELD_CP_INSTANCE_ID, false);
		addSearchTerm(searchQuery, searchContext, FIELD_SKU, false);
	}

	@Override
	protected void doDelete(CommerceSubscriptionEntry commerceSubscriptionEntry)
		throws Exception {

		deleteDocument(
			commerceSubscriptionEntry.getCompanyId(),
			commerceSubscriptionEntry.getCommerceSubscriptionEntryId());
	}

	@Override
	protected Document doGetDocument(
			CommerceSubscriptionEntry commerceSubscriptionEntry)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Indexing commerce subscription entry " +
					commerceSubscriptionEntry);
		}

		Document document = getBaseModelDocument(
			CLASS_NAME, commerceSubscriptionEntry);

		document.addNumber(
			FIELD_COMMERCE_ORDER_ITEM_ID,
			commerceSubscriptionEntry.getCommerceOrderItemId());
		document.addNumber(
			FIELD_CP_INSTANCE_ID, commerceSubscriptionEntry.getCPInstanceId());
		document.addNumber(
			FIELD_MAX_SUBSCRIPTION_CYCLES,
			commerceSubscriptionEntry.getMaxSubscriptionCycles());
		document.addKeyword(
			FIELD_SUBSCRIPTION_STATUS,
			commerceSubscriptionEntry.getSubscriptionStatus());

		String sku = StringPool.BLANK;

		CommerceOrderItem commerceOrderItem =
			commerceSubscriptionEntry.fetchCommerceOrderItem();

		if (commerceOrderItem != null) {
			sku = commerceOrderItem.getSku();
		}

		document.addText(FIELD_SKU, sku);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Commerce subscription entry " + commerceSubscriptionEntry +
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
	protected void doReindex(
			CommerceSubscriptionEntry commerceSubscriptionEntry)
		throws Exception {

		_indexWriterHelper.updateDocument(
			commerceSubscriptionEntry.getCompanyId(),
			getDocument(commerceSubscriptionEntry));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		doReindex(
			_commerceSubscriptionEntryLocalService.getCommerceSubscriptionEntry(
				classPK));
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		_reindexCommerceSubscriptionEntries(companyId);
	}

	private void _reindexCommerceSubscriptionEntries(long companyId)
		throws Exception {

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_commerceSubscriptionEntryLocalService.
				getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(CommerceSubscriptionEntry commerceSubscriptionEntry) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(commerceSubscriptionEntry));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index commerce subscription entry " +
								commerceSubscriptionEntry,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceSubscriptionEntryIndexer.class);

	@Reference
	private CommerceSubscriptionEntryLocalService
		_commerceSubscriptionEntryLocalService;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

}