/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.admin.web.internal.util.LayoutPageTemplatePortletUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionServiceUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Pavel Savinov
 */
public class LayoutPageTemplateCollectionsDisplayContext {

	public LayoutPageTemplateCollectionsDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public String getEventName() {
		if (Validator.isNotNull(_eventName)) {
			return _eventName;
		}

		_eventName = ParamUtil.getString(
			_httpServletRequest, "eventName",
			_renderResponse.getNamespace() + "selectCollections");

		return _eventName;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest,
			LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
			"layout-page-template-collections-order-by-type", "asc");

		return _orderByType;
	}

	public SearchContainer<LayoutPageTemplateCollection> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SearchContainer<LayoutPageTemplateCollection> searchContainer =
			new SearchContainer(
				_renderRequest, _renderResponse.createRenderURL(), null,
				"there-are-no-page-template-sets");

		searchContainer.setOrderByCol(_getOrderByCol());
		searchContainer.setOrderByComparator(
			LayoutPageTemplatePortletUtil.
				getLayoutPageTemplateCollectionOrderByComparator(
					_getOrderByCol(), getOrderByType()));
		searchContainer.setOrderByType(getOrderByType());

		if (_isSearch()) {
			searchContainer.setResultsAndTotal(
				() ->
					LayoutPageTemplateCollectionServiceUtil.
						getLayoutPageTemplateCollections(
							themeDisplay.getScopeGroupId(), _getKeywords(),
							LayoutPageTemplateEntryTypeConstants.BASIC,
							searchContainer.getStart(),
							searchContainer.getEnd(),
							searchContainer.getOrderByComparator()),
				LayoutPageTemplateCollectionServiceUtil.
					getLayoutPageTemplateCollectionsCount(
						themeDisplay.getScopeGroupId(), _getKeywords(),
						LayoutPageTemplateEntryTypeConstants.BASIC));
		}
		else {
			searchContainer.setResultsAndTotal(
				() ->
					LayoutPageTemplateCollectionServiceUtil.
						getLayoutPageTemplateCollections(
							themeDisplay.getScopeGroupId(),
							LayoutPageTemplateEntryTypeConstants.BASIC,
							searchContainer.getStart(),
							searchContainer.getEnd(),
							searchContainer.getOrderByComparator()),
				LayoutPageTemplateCollectionServiceUtil.
					getLayoutPageTemplateCollectionsCount(
						themeDisplay.getScopeGroupId(),
						LayoutPageTemplateEntryTypeConstants.BASIC));
		}

		searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest,
			LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
			"layout-page-template-collections-order-by-col", "create-date");

		return _orderByCol;
	}

	private boolean _isSearch() {
		return Validator.isNotNull(_getKeywords());
	}

	private String _eventName;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<LayoutPageTemplateCollection> _searchContainer;

}