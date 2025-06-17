/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.item.selector.web.internal.display.context;

import com.liferay.asset.display.page.item.selector.AssetDisplayPageSelectorCriterion;
import com.liferay.item.selector.criteria.AssetEntryItemSelectorReturnType;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.layout.page.template.util.comparator.LayoutPageTemplateCollectionLayoutPageTemplateEntryCreateDateComparator;
import com.liferay.layout.page.template.util.comparator.LayoutPageTemplateCollectionLayoutPageTemplateEntryNameComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryBuilder;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryListBuilder;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Yurena Cabrera
 */
public class AssetDisplayPagesItemSelectorCustomViewDisplayContext {

	public AssetDisplayPagesItemSelectorCustomViewDisplayContext(
		HttpServletRequest httpServletRequest, String itemSelectedEventName,
		AssetDisplayPageSelectorCriterion assetDisplayPageSelectorCriterion,
		PortletURL portletURL) {

		_httpServletRequest = httpServletRequest;
		_itemSelectedEventName = itemSelectedEventName;
		_assetDisplayPageSelectorCriterion = assetDisplayPageSelectorCriterion;
		_portletURL = portletURL;

		_portletRequest = (PortletRequest)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public SearchContainer<?> getAssetDisplayPageSearchContainer() {
		if (_assetDisplayPageSearchContainer != null) {
			return _assetDisplayPageSearchContainer;
		}

		SearchContainer<Object> assetDisplayPageSearchContainer =
			new SearchContainer<>(
				_portletRequest, _portletURL, null,
				"there-are-no-display-page-templates");

		assetDisplayPageSearchContainer.setId(
			"displayPages" + getLayoutPageTemplateCollectionId());
		assetDisplayPageSearchContainer.setOrderByCol(_getOrderByCol());
		assetDisplayPageSearchContainer.setOrderByComparator(
			_getLayoutPageTemplateEntryOrderByComparator(
				_getOrderByCol(), getOrderByType()));
		assetDisplayPageSearchContainer.setOrderByType(getOrderByType());
		assetDisplayPageSearchContainer.setResultsAndTotal(
			() ->
				LayoutPageTemplateEntryServiceUtil.
					getLayoutPageCollectionsAndLayoutPageTemplateEntries(
						_getGroupId(), getLayoutPageTemplateCollectionId(),
						_assetDisplayPageSelectorCriterion.getClassNameId(),
						_assetDisplayPageSelectorCriterion.getClassTypeId(),
						_getKeywords(),
						LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
						WorkflowConstants.STATUS_APPROVED,
						assetDisplayPageSearchContainer.getStart(),
						assetDisplayPageSearchContainer.getEnd(),
						assetDisplayPageSearchContainer.getOrderByComparator()),
			LayoutPageTemplateEntryServiceUtil.
				getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
					_getGroupId(), getLayoutPageTemplateCollectionId(),
					_assetDisplayPageSelectorCriterion.getClassNameId(),
					_assetDisplayPageSelectorCriterion.getClassTypeId(),
					_getKeywords(),
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE,
					WorkflowConstants.STATUS_APPROVED));

		_assetDisplayPageSearchContainer = assetDisplayPageSearchContainer;

		return _assetDisplayPageSearchContainer;
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public List<BreadcrumbEntry> getLayoutPageTemplateBreadcrumbEntries() {
		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplateCollectionLocalServiceUtil.
				fetchLayoutPageTemplateCollection(
					getLayoutPageTemplateCollectionId());

		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(
					LanguageUtil.get(_httpServletRequest, "home"));
				breadcrumbEntry.setURL(
					PortletURLBuilder.create(
						_portletURL
					).setParameter(
						"layoutPageTemplateCollectionId",
						LayoutPageTemplateConstants.
							PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT
					).buildString());
			}
		).addAll(
			() -> layoutPageTemplateCollection != null,
			() -> {
				List<LayoutPageTemplateCollection>
					layoutPageTemplateCollections =
						layoutPageTemplateCollection.getAncestors();

				Collections.reverse(layoutPageTemplateCollections);

				return TransformUtil.transform(
					layoutPageTemplateCollections,
					curLayoutPageTemplateCollection ->
						BreadcrumbEntryBuilder.setTitle(
							curLayoutPageTemplateCollection.getName()
						).setURL(
							PortletURLBuilder.create(
								_portletURL
							).setParameter(
								"layoutPageTemplateCollectionId",
								curLayoutPageTemplateCollection.
									getLayoutPageTemplateCollectionId()
							).buildString()
						).build());
			}
		).build();
	}

	public long getLayoutPageTemplateCollectionId() {
		if (_layoutPageTemplateCollectionId != null) {
			return _layoutPageTemplateCollectionId;
		}

		_layoutPageTemplateCollectionId = ParamUtil.getLong(
			_httpServletRequest, "layoutPageTemplateCollectionId",
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT);

		return _layoutPageTemplateCollectionId;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = ParamUtil.getString(
			_httpServletRequest, "orderByType", "asc");

		return _orderByType;
	}

	public String getPayload(LayoutPageTemplateEntry layoutPageTemplateEntry) {
		return JSONUtil.put(
			"id", layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
		).put(
			"name", layoutPageTemplateEntry.getName()
		).put(
			"plid", layoutPageTemplateEntry.getPlid()
		).put(
			"type", "asset-display-page"
		).put(
			"uuid", layoutPageTemplateEntry.getUuid()
		).toString();
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	public String getReturnType() {
		return AssetEntryItemSelectorReturnType.class.getName();
	}

	private long _getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		_groupId = ParamUtil.getLong(
			_httpServletRequest, "groupId", _themeDisplay.getScopeGroupId());

		return _groupId;
	}

	private String _getKeywords() {
		if (Validator.isNotNull(_keywords)) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private OrderByComparator<Object>
		_getLayoutPageTemplateEntryOrderByComparator(
			String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<Object> orderByComparator = null;

		if (orderByCol.equals("create-date")) {
			orderByComparator =
				LayoutPageTemplateCollectionLayoutPageTemplateEntryCreateDateComparator.
					getInstance(orderByAsc);
		}
		else if (orderByCol.equals("name")) {
			orderByComparator =
				LayoutPageTemplateCollectionLayoutPageTemplateEntryNameComparator.
					getInstance(orderByAsc);
		}

		return orderByComparator;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = ParamUtil.getString(
			_httpServletRequest, "orderByCol", "create-date");

		return _orderByCol;
	}

	private SearchContainer<?> _assetDisplayPageSearchContainer;
	private final AssetDisplayPageSelectorCriterion
		_assetDisplayPageSelectorCriterion;
	private Long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private final String _itemSelectedEventName;
	private String _keywords;
	private Long _layoutPageTemplateCollectionId;
	private String _orderByCol;
	private String _orderByType;
	private final PortletRequest _portletRequest;
	private final PortletURL _portletURL;
	private final ThemeDisplay _themeDisplay;

}