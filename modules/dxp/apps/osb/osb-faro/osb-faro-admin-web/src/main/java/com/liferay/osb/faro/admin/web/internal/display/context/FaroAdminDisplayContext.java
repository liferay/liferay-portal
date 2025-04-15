/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.osb.faro.admin.web.internal.model.FaroProjectAdminDisplay;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.ActionURLBuilder;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Shinn Lok
 */
public class FaroAdminDisplayContext {

	public FaroAdminDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems(
		FaroProjectAdminDisplay faroProjectAdminDisplay) {

		if (faroProjectAdminDisplay.isOffline()) {
			return Collections.emptyList();
		}

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setDisabled(
					!faroProjectAdminDisplay.isDataSourceConnected());
				dropdownItem.setHref(
					StringBundler.concat(
						"javascript:Liferay.Util.openConfirmModal({message: '",
						LanguageUtil.get(
							_httpServletRequest,
							"are-you-sure-you-want-to-disconnect-the-data-" +
								"sources"),
						"', onConfirm: (isConfirmed) => {if (isConfirmed) {",
						"submitForm(document.hrefFm, '",
						HtmlUtil.escapeJS(
							ActionURLBuilder.createActionURL(
								_renderResponse
							).setActionName(
								"/faro_admin/disconnect_data_sources"
							).setParameter(
								"faroProjectId",
								faroProjectAdminDisplay.getFaroProjectId()
							).buildString()),
						"');} else {self.focus();}}});"));
				dropdownItem.setIcon("logout");
				dropdownItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest, "disconnect-data-sources"));
			}
		).build();
	}

	public PortletURL getPortletURL() {
		PortletURL portletURL = _renderResponse.createRenderURL();

		String keywords = _getKeywords();

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		String navigation = ParamUtil.getString(
			_httpServletRequest, "navigation");

		if (Validator.isNotNull(navigation)) {
			portletURL.setParameter(
				"navigation", HtmlUtil.escapeJS(_getNavigation()));
		}

		String orderByCol = _getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			portletURL.setParameter("orderByCol", orderByCol);
		}

		String orderByType = _getOrderByType();

		if (Validator.isNotNull(orderByType)) {
			portletURL.setParameter("orderByType", orderByType);
		}

		return portletURL;
	}

	public SearchContainer<FaroProjectAdminDisplay> getSearchContainer()
		throws Exception {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		SearchContainer<FaroProjectAdminDisplay> searchContainer =
			new SearchContainer<>(
				_renderRequest, getPortletURL(), null, "there-are-no-projects");

		searchContainer.setOrderByCol(_getOrderByCol());
		searchContainer.setOrderByType(_getOrderByType());

		Indexer<FaroProject> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			FaroProject.class);

		SearchContext searchContext = new SearchContext();

		if (_isBasic()) {
			searchContext.setAttribute("subscriptionName", "Basic");
		}
		else if (_isBusiness()) {
			searchContext.setAttribute("subscriptionName", "Business");
		}
		else if (_isEnterprise()) {
			searchContext.setAttribute("subscriptionName", "Enterprise");
		}
		else if (_isInactive()) {
			searchContext.setAttribute("inactive", Boolean.TRUE);
		}
		else if (_isNavigationUsageLimitApproaching()) {
			searchContext.setAttribute("maxUsage", 100);
			searchContext.setAttribute("minUsage", 70);
		}
		else if (_isNavigationUsageLimitExceeded()) {
			searchContext.setAttribute("maxUsage", Integer.MAX_VALUE);
			searchContext.setAttribute("minUsage", 100);
		}
		else if (_isOffline()) {
			searchContext.setAttribute("offline", Boolean.TRUE);
		}

		searchContext.setEnd(searchContainer.getEnd());
		searchContext.setKeywords(
			ParamUtil.getString(_httpServletRequest, "keywords"));
		searchContext.setStart(searchContainer.getStart());

		String orderByCol = _orderByCol;

		if (orderByCol.equals("createDate") ||
			orderByCol.equals("individualsUsage") ||
			orderByCol.equals("pageViewsUsage")) {

			orderByCol += "_sortable";
		}

		boolean reverse = true;

		if (_orderByType.equals("asc")) {
			reverse = false;
		}

		searchContext.setSorts(new Sort(orderByCol, reverse));

		Hits hits = indexer.search(searchContext);

		searchContainer.setResultsAndTotal(
			() -> TransformUtil.transform(
				hits.toList(), FaroProjectAdminDisplay::new),
			hits.getLength());

		searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		_searchContainer = searchContainer;

		return searchContainer;
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private String _getNavigation() {
		if (_navigation != null) {
			return _navigation;
		}

		_navigation = ParamUtil.getString(
			_httpServletRequest, "navigation", "all");

		return _navigation;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = ParamUtil.getString(
			_httpServletRequest, "orderByCol", "createDate");

		return _orderByCol;
	}

	private String _getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = ParamUtil.getString(
			_httpServletRequest, "orderByType", "asc");

		return _orderByType;
	}

	private boolean _isBasic() {
		return StringUtil.equals(_getNavigation(), "basic");
	}

	private boolean _isBusiness() {
		return StringUtil.equals(_getNavigation(), "business");
	}

	private boolean _isEnterprise() {
		return StringUtil.equals(_getNavigation(), "enterprise");
	}

	private boolean _isInactive() {
		return StringUtil.equals(_getNavigation(), "inactive");
	}

	private boolean _isNavigationUsageLimitApproaching() {
		return StringUtil.equals(_getNavigation(), "usage-limit-approaching");
	}

	private boolean _isNavigationUsageLimitExceeded() {
		return StringUtil.equals(_getNavigation(), "usage-limit-exceeded");
	}

	private boolean _isOffline() {
		return StringUtil.equals(_getNavigation(), "offline");
	}

	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _navigation;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<FaroProjectAdminDisplay> _searchContainer;
	private final ThemeDisplay _themeDisplay;

}