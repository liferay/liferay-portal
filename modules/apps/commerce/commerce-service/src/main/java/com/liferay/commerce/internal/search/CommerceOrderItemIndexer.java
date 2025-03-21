/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.search;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.generic.WildcardQueryImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.expando.ExpandoBridgeIndexer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.LinkedHashMap;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
@Component(service = Indexer.class)
public class CommerceOrderItemIndexer extends BaseIndexer<CommerceOrderItem> {

	public static final String CLASS_NAME = CommerceOrderItem.class.getName();

	public static final String FIELD_COMMERCE_ORDER_ID = "commerceOrderId";

	public static final String FIELD_CP_DEFINITION_ID = "CPDefinitionId";

	public static final String FIELD_FINAL_PRICE = "finalPrice";

	public static final String FIELD_PARENT_COMMERCE_ORDER_ITEM_ID =
		"parentCommerceOrderItemId";

	public static final String FIELD_QUANTITY = "quantity";

	public static final String FIELD_SKU = CPField.SKU;

	public static final String FIELD_UNIT_PRICE = "unitPrice";

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		Long commerceOrderId = (Long)searchContext.getAttribute(
			FIELD_COMMERCE_ORDER_ID);

		if (commerceOrderId != null) {
			contextBooleanFilter.addRequiredTerm(
				FIELD_COMMERCE_ORDER_ID, commerceOrderId);
		}

		Long parentCommerceOrderItemId = (Long)searchContext.getAttribute(
			FIELD_PARENT_COMMERCE_ORDER_ITEM_ID);

		if (parentCommerceOrderItemId != null) {
			contextBooleanFilter.addRequiredTerm(
				FIELD_PARENT_COMMERCE_ORDER_ITEM_ID, parentCommerceOrderItemId);
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		addSearchLocalizedTerm(searchQuery, searchContext, Field.NAME, true);
		addSearchTerm(searchQuery, searchContext, FIELD_SKU, false);

		LinkedHashMap<String, Object> params =
			(LinkedHashMap<String, Object>)searchContext.getAttribute("params");

		if (params != null) {
			String expandoAttributes = (String)params.get("expandoAttributes");

			if (Validator.isNotNull(expandoAttributes)) {
				addSearchExpando(searchQuery, searchContext, expandoAttributes);
			}
		}

		String keywords = searchContext.getKeywords();

		if (Validator.isNotNull(keywords)) {
			try {
				keywords = StringUtil.toLowerCase(keywords);

				searchQuery.add(
					_getTrailingWildcardQuery(FIELD_SKU, keywords),
					BooleanClauseOccur.SHOULD);
			}
			catch (ParseException parseException) {
				throw new SystemException(parseException);
			}
		}
	}

	@Override
	protected void doDelete(CommerceOrderItem commerceOrderItem)
		throws Exception {

		deleteDocument(
			commerceOrderItem.getCompanyId(),
			commerceOrderItem.getCommerceOrderItemId());
	}

	@Override
	protected Document doGetDocument(CommerceOrderItem commerceOrderItem)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Indexing commerce order item " + commerceOrderItem);
		}

		Document document = getBaseModelDocument(CLASS_NAME, commerceOrderItem);

		document.addLocalizedKeyword(
			Field.NAME, commerceOrderItem.getNameMap(), false, true);
		document.addNumber(
			FIELD_COMMERCE_ORDER_ID, commerceOrderItem.getCommerceOrderId());
		document.addKeyword(
			FIELD_CP_DEFINITION_ID, commerceOrderItem.getCPDefinitionId());
		document.addNumber(
			FIELD_FINAL_PRICE, commerceOrderItem.getFinalPrice());
		document.addNumber(
			FIELD_PARENT_COMMERCE_ORDER_ITEM_ID,
			commerceOrderItem.getParentCommerceOrderItemId());
		document.addNumber(FIELD_QUANTITY, commerceOrderItem.getQuantity());
		document.addNumberSortable(
			FIELD_QUANTITY, commerceOrderItem.getQuantity());
		document.addKeyword(FIELD_SKU, commerceOrderItem.getSku());
		document.addKeywordSortable(FIELD_SKU, commerceOrderItem.getSku());
		document.addNumber(FIELD_UNIT_PRICE, commerceOrderItem.getUnitPrice());

		String unitOfMeasureKey = commerceOrderItem.getUnitOfMeasureKey();

		if (Validator.isNotNull(unitOfMeasureKey)) {
			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
				_cpInstanceUnitOfMeasureLocalService.
					fetchCPInstanceUnitOfMeasure(
						commerceOrderItem.getCPInstanceId(), unitOfMeasureKey);

			if (cpInstanceUnitOfMeasure != null) {
				document.addLocalizedText(
					"cpInstanceUnitOfMeasure",
					cpInstanceUnitOfMeasure.getNameMap(), true);
			}
		}

		_expandoBridgeIndexer.addAttributes(
			document, commerceOrderItem.getExpandoBridge());

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Commerce order item " + commerceOrderItem +
					" indexed successfully");
		}

		return document;
	}

	@Override
	protected Summary doGetSummary(
			Document document, Locale locale, String snippet,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		return null;
	}

	@Override
	protected void doReindex(CommerceOrderItem commerceOrderItem)
		throws Exception {

		_indexWriterHelper.updateDocument(
			commerceOrderItem.getCompanyId(), getDocument(commerceOrderItem));
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		doReindex(_commerceOrderItemLocalService.getCommerceOrderItem(classPK));
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		_reindexCommerceOrderItems(companyId);
	}

	private WildcardQuery _getTrailingWildcardQuery(
		String field, String value) {

		return new WildcardQueryImpl(field, value + StringPool.STAR);
	}

	private void _reindexCommerceOrderItems(long companyId) throws Exception {
		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_commerceOrderItemLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(CommerceOrderItem commerceOrderItem) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(commerceOrderItem));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index commerce order item " +
								commerceOrderItem,
							portalException);
					}
				}
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderItemIndexer.class);

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private ExpandoBridgeIndexer _expandoBridgeIndexer;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

}