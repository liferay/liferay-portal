/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroProjectLocalServiceUtil;
import com.liferay.osb.faro.service.FaroUserLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.url.builder.ActionURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

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
	}

	public List<DropdownItem> getActionDropdownItems(FaroProject faroProject) {
		if (faroProject.isOffline()) {
			return Collections.emptyList();
		}

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setDisabled(!faroProject.isDataSourceConnected());
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
								"faroProjectId", faroProject.getFaroProjectId()
							).buildString()),
						"');} else {self.focus();}}});"));
				dropdownItem.setIcon("logout");
				dropdownItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest, "disconnect-data-sources"));
			}
		).build();
	}

	public String getOwner(FaroProject faroProject) {
		FaroUser faroUser = FaroUserLocalServiceUtil.fetchOwnerFaroUser(
			faroProject.getGroupId());

		if (faroUser == null) {
			return null;
		}

		if (faroUser.getLiveUserId() <= 0) {
			return faroUser.getEmailAddress();
		}

		User user = UserLocalServiceUtil.fetchUser(faroUser.getLiveUserId());

		if (user != null) {
			return user.getFullName() + " " + user.getEmailAddress();
		}

		return null;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setKeywords(
			_getKeywords()
		).setNavigation(
			_getNavigation()
		).setParameter(
			"orderByCol", _getOrderByCol()
		).setParameter(
			"orderByType", _getOrderByType()
		).buildPortletURL();
	}

	public SearchContainer<FaroProject> getSearchContainer() throws Exception {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		SearchContainer<FaroProject> searchContainer = new SearchContainer<>(
			_renderRequest, getPortletURL(), null, "there-are-no-projects");

		searchContainer.setId("faro_admin");

		searchContainer.setOrderByCol(_getOrderByCol());
		searchContainer.setOrderByType(_getOrderByType());

		Indexer<FaroProject> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			FaroProject.class);

		SearchContext searchContext = new SearchContext();

		String navigation = _getNavigation();

		if (navigation.equals("basic")) {
			searchContext.setAttribute("subscriptionName", "Basic");
		}
		else if (navigation.equals("business")) {
			searchContext.setAttribute("subscriptionName", "Business");
		}
		else if (navigation.equals("enterprise")) {
			searchContext.setAttribute("subscriptionName", "Enterprise");
		}
		else if (navigation.equals("inactive")) {
			searchContext.setAttribute("inactive", Boolean.TRUE);
		}
		else if (navigation.equals("usage-limit-approaching")) {
			searchContext.setAttribute("maxUsage", 100);
			searchContext.setAttribute("minUsage", 70);
		}
		else if (navigation.equals("usage-limit-exceeded")) {
			searchContext.setAttribute("maxUsage", Integer.MAX_VALUE);
			searchContext.setAttribute("minUsage", 100);
		}
		else if (navigation.equals("offline")) {
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
				hits.toList(),
				document -> FaroProjectLocalServiceUtil.fetchFaroProject(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))),
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

	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _navigation;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<FaroProject> _searchContainer;

}