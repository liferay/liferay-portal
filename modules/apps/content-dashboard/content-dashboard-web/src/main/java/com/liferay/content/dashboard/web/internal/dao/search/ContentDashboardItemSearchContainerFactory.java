/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.dao.search;

import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.content.dashboard.item.ContentDashboardItem;
import com.liferay.content.dashboard.item.ContentDashboardItemFactory;
import com.liferay.content.dashboard.web.internal.constants.ContentDashboardPortletKeys;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItemFactoryRegistry;
import com.liferay.content.dashboard.web.internal.item.filter.ContentDashboardItemFilterProviderRegistry;
import com.liferay.content.dashboard.web.internal.search.request.ContentDashboardSearchContextBuilder;
import com.liferay.content.dashboard.web.internal.searcher.ContentDashboardSearchRequestBuilderFactory;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Cristina González
 */
public class ContentDashboardItemSearchContainerFactory {

	public static ContentDashboardItemSearchContainerFactory getInstance(
		AssetCategoryLocalService assetCategoryLocalService,
		AssetVocabularyLocalService assetVocabularyLocalService,
		ContentDashboardItemFactoryRegistry contentDashboardItemFactoryRegistry,
		ContentDashboardItemFilterProviderRegistry
			contentDashboardItemFilterProviderRegistry,
		ContentDashboardSearchRequestBuilderFactory
			contentDashboardSearchRequestBuilderFactory,
		InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
		Portal portal, PortletRequest portletRequest,
		PortletResponse portletResponse, Searcher searcher) {

		return new ContentDashboardItemSearchContainerFactory(
			assetCategoryLocalService, assetVocabularyLocalService,
			contentDashboardItemFactoryRegistry,
			contentDashboardItemFilterProviderRegistry,
			contentDashboardSearchRequestBuilderFactory,
			infoSearchClassMapperRegistry, portal, portletRequest,
			portletResponse, searcher);
	}

	public SearchContainer<ContentDashboardItem<?>> create()
		throws PortletException {

		SearchContainer<ContentDashboardItem<?>> searchContainer =
			_getContentDashboardItemSearchContainer();

		return _create(
			searchContainer.getEnd(), searchContainer,
			searchContainer.getStart());
	}

	private ContentDashboardItemSearchContainerFactory(
		AssetCategoryLocalService assetCategoryLocalService,
		AssetVocabularyLocalService assetVocabularyLocalService,
		ContentDashboardItemFactoryRegistry contentDashboardItemFactoryRegistry,
		ContentDashboardItemFilterProviderRegistry
			contentDashboardItemFilterProviderRegistry,
		ContentDashboardSearchRequestBuilderFactory
			contentDashboardSearchRequestBuilderFactory,
		InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
		Portal portal, PortletRequest portletRequest,
		PortletResponse portletResponse, Searcher searcher) {

		_assetCategoryLocalService = assetCategoryLocalService;
		_assetVocabularyLocalService = assetVocabularyLocalService;
		_contentDashboardItemFactoryRegistry =
			contentDashboardItemFactoryRegistry;
		_contentDashboardItemFilterProviderRegistry =
			contentDashboardItemFilterProviderRegistry;
		_contentDashboardSearchRequestBuilderFactory =
			contentDashboardSearchRequestBuilderFactory;
		_infoSearchClassMapperRegistry = infoSearchClassMapperRegistry;
		_portal = portal;
		_portletRequest = portletRequest;
		_portletResponse = portletResponse;
		_searcher = searcher;

		_locale = portal.getLocale(portletRequest);
	}

	private SearchContainer<ContentDashboardItem<?>> _create(
		int end, SearchContainer<ContentDashboardItem<?>> searchContainer,
		int start) {

		SearchResponse searchResponse = _getSearchResponse(end, start);

		searchContainer.setResultsAndTotal(
			() -> TransformUtil.transform(
				searchResponse.getDocuments71(), this::_toContentDashboardItem),
			searchResponse.getTotalHits());

		return searchContainer;
	}

	private SearchContainer<ContentDashboardItem<?>>
			_getContentDashboardItemSearchContainer()
		throws PortletException {

		SearchContainer<ContentDashboardItem<?>> searchContainer =
			new SearchContainer<>(
				_portletRequest,
				PortletURLUtil.clone(
					PortletURLUtil.getCurrent(
						PortalUtil.getLiferayPortletRequest(_portletRequest),
						PortalUtil.getLiferayPortletResponse(_portletResponse)),
					PortalUtil.getLiferayPortletResponse(_portletResponse)),
				null, "there-is-no-content");

		searchContainer.setOrderByCol(_getOrderByCol());
		searchContainer.setOrderByType(_getOrderByType());

		return searchContainer;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_portletRequest,
			ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN,
			"item-search-order-by-col", "modified-date");

		return _orderByCol;
	}

	private String _getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		if (Objects.equals(_getOrderByCol(), "title")) {
			_orderByType = SearchOrderByUtil.getOrderByType(
				_portletRequest,
				ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN,
				"item-search-order-by-type", "asc");

			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_portletRequest,
			ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN,
			"item-search-order-by-type", "desc");

		return _orderByType;
	}

	private SearchResponse _getSearchResponse(int end, int start) {
		return _searcher.search(
			_contentDashboardSearchRequestBuilderFactory.builder(
				new ContentDashboardSearchContextBuilder(
					_portal.getHttpServletRequest(_portletRequest),
					_assetCategoryLocalService, _assetVocabularyLocalService,
					_contentDashboardItemFilterProviderRegistry
				).withEnd(
					end
				).withSort(
					_getSort(_getOrderByCol(), _getOrderByType())
				).withStart(
					start
				).build()
			).build());
	}

	private Sort _getSort(String orderByCol, String orderByType) {
		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		if (orderByCol.equals("title")) {
			return new Sort(
				Field.getSortableFieldName(
					"localized_title_".concat(
						LocaleUtil.toLanguageId(_locale))),
				Sort.STRING_TYPE, !orderByAsc);
		}

		return new Sort(Field.MODIFIED_DATE, Sort.LONG_TYPE, !orderByAsc);
	}

	private ContentDashboardItem<?> _toContentDashboardItem(
		ContentDashboardItemFactory<?> contentDashboardItemFactory,
		Document document) {

		try {
			long classPK = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			if (classPK == 0) {
				classPK = GetterUtil.getLong(
					document.get(Field.ROOT_ENTRY_CLASS_PK));
			}

			return contentDashboardItemFactory.create(classPK);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private ContentDashboardItem<?> _toContentDashboardItem(Document document) {
		ContentDashboardItemFactory<?> contentDashboardItemFactory =
			_contentDashboardItemFactoryRegistry.getContentDashboardItemFactory(
				_infoSearchClassMapperRegistry.getClassName(
					document.get(Field.ENTRY_CLASS_NAME)));

		if (contentDashboardItemFactory == null) {
			return null;
		}

		return _toContentDashboardItem(contentDashboardItemFactory, document);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentDashboardItemSearchContainerFactory.class);

	private final AssetCategoryLocalService _assetCategoryLocalService;
	private final AssetVocabularyLocalService _assetVocabularyLocalService;
	private final ContentDashboardItemFactoryRegistry
		_contentDashboardItemFactoryRegistry;
	private final ContentDashboardItemFilterProviderRegistry
		_contentDashboardItemFilterProviderRegistry;
	private final ContentDashboardSearchRequestBuilderFactory
		_contentDashboardSearchRequestBuilderFactory;
	private final InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;
	private final Locale _locale;
	private String _orderByCol;
	private String _orderByType;
	private final Portal _portal;
	private final PortletRequest _portletRequest;
	private final PortletResponse _portletResponse;
	private final Searcher _searcher;

}