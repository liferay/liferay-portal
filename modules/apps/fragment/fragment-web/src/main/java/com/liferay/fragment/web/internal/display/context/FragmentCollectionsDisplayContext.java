/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionServiceUtil;
import com.liferay.fragment.web.internal.util.FragmentPortletUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class FragmentCollectionsDisplayContext {

	public FragmentCollectionsDisplayContext(
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
			_httpServletRequest, FragmentPortletKeys.FRAGMENT,
			"fragment-collections-order-by-type", "asc");

		return _orderByType;
	}

	public SearchContainer<FragmentCollection> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SearchContainer<FragmentCollection> searchContainer =
			new SearchContainer(
				_renderRequest, _getPortletURL(), null,
				"there-are-no-fragment-sets");

		searchContainer.setOrderByCol(_getOrderByCol());
		searchContainer.setOrderByComparator(
			FragmentPortletUtil.getFragmentCollectionOrderByComparator(
				_getOrderByCol(), getOrderByType()));
		searchContainer.setOrderByType(getOrderByType());

		long[] groupIds = {themeDisplay.getScopeGroupId()};

		if (_isIncludeGlobalFragmentCollections()) {
			groupIds = new long[] {
				themeDisplay.getScopeGroupId(), themeDisplay.getCompanyGroupId()
			};
		}

		Group scopeGroup = themeDisplay.getScopeGroup();

		if ((themeDisplay.getCompanyId() ==
				PortalInstancePool.getDefaultCompanyId()) &&
			scopeGroup.isCompany()) {

			groupIds = ArrayUtil.append(groupIds, CompanyConstants.SYSTEM);
		}

		long[] allGroupIds = groupIds;

		if (_isSearch()) {
			if (_isIncludeMarketplaceFragmentCollections()) {
				searchContainer.setResultsAndTotal(
					() -> FragmentCollectionServiceUtil.getFragmentCollections(
						allGroupIds, _getKeywords(), searchContainer.getStart(),
						searchContainer.getEnd(),
						searchContainer.getOrderByComparator()),
					FragmentCollectionServiceUtil.getFragmentCollectionsCount(
						allGroupIds, _getKeywords()));
			}
			else {
				searchContainer.setResultsAndTotal(
					() -> FragmentCollectionServiceUtil.getFragmentCollections(
						allGroupIds, _getKeywords(), false,
						searchContainer.getStart(), searchContainer.getEnd(),
						searchContainer.getOrderByComparator()),
					FragmentCollectionServiceUtil.getFragmentCollectionsCount(
						allGroupIds, _getKeywords(), false));
			}
		}
		else {
			if (_isIncludeMarketplaceFragmentCollections()) {
				searchContainer.setResultsAndTotal(
					() -> FragmentCollectionServiceUtil.getFragmentCollections(
						allGroupIds, searchContainer.getStart(),
						searchContainer.getEnd(),
						searchContainer.getOrderByComparator()),
					FragmentCollectionServiceUtil.getFragmentCollectionsCount(
						allGroupIds));
			}
			else {
				searchContainer.setResultsAndTotal(
					() -> FragmentCollectionServiceUtil.getFragmentCollections(
						allGroupIds, false, searchContainer.getStart(),
						searchContainer.getEnd(),
						searchContainer.getOrderByComparator()),
					FragmentCollectionServiceUtil.getFragmentCollectionsCount(
						allGroupIds, false));
			}
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
			_httpServletRequest, FragmentPortletKeys.FRAGMENT,
			"fragment-collections-order-by-col", "create-date");

		return _orderByCol;
	}

	private PortletURL _getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/fragment/view_fragment_collections"
		).setKeywords(
			() -> {
				String keywords = _getKeywords();

				if (Validator.isNotNull(keywords)) {
					return keywords;
				}

				return null;
			}
		).setParameter(
			"eventName", getEventName()
		).setParameter(
			"includeGlobalFragmentCollections",
			_isIncludeGlobalFragmentCollections()
		).setParameter(
			"includeMarketplaceFragmentCollections",
			_isIncludeMarketplaceFragmentCollections()
		).setParameter(
			"orderByCol",
			() -> {
				String orderByCol = _getOrderByCol();

				if (Validator.isNotNull(orderByCol)) {
					return orderByCol;
				}

				return null;
			}
		).setParameter(
			"orderByType",
			() -> {
				String orderByType = getOrderByType();

				if (Validator.isNotNull(orderByType)) {
					return orderByType;
				}

				return null;
			}
		).buildPortletURL();
	}

	private boolean _isIncludeGlobalFragmentCollections() {
		if (_includeGlobalFragmentCollections != null) {
			return _includeGlobalFragmentCollections;
		}

		_includeGlobalFragmentCollections = ParamUtil.getBoolean(
			_httpServletRequest, "includeGlobalFragmentCollections");

		return _includeGlobalFragmentCollections;
	}

	private boolean _isIncludeMarketplaceFragmentCollections() {
		if (_includeMarketplaceFragmentCollections != null) {
			return _includeMarketplaceFragmentCollections;
		}

		_includeMarketplaceFragmentCollections = ParamUtil.getBoolean(
			_httpServletRequest, "includeMarketplaceFragmentCollections");

		return _includeMarketplaceFragmentCollections;
	}

	private boolean _isSearch() {
		return Validator.isNotNull(_getKeywords());
	}

	private String _eventName;
	private final HttpServletRequest _httpServletRequest;
	private Boolean _includeGlobalFragmentCollections;
	private Boolean _includeMarketplaceFragmentCollections;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<FragmentCollection> _searchContainer;

}