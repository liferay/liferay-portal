/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.teams.web.internal.display.context;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.TeamServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.teams.web.internal.constants.SiteTeamsPortletKeys;
import com.liferay.site.teams.web.internal.search.TeamSearch;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class SiteTeamsDisplayContext {

	public SiteTeamsDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest, SiteTeamsPortletKeys.SITE_TEAMS, "list");

		return _displayStyle;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, SiteTeamsPortletKeys.SITE_TEAMS, "name");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, SiteTeamsPortletKeys.SITE_TEAMS, "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setParameter(
			"displayStyle", getDisplayStyle()
		).buildPortletURL();
	}

	public SearchContainer<Team> getSearchContainer() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SearchContainer<Team> searchContainer = new TeamSearch(
			_renderRequest, getPortletURL());

		searchContainer.setEmptyResultsMessage("there-are-no-teams");
		searchContainer.setId("teams");
		searchContainer.setOrderByCol(getOrderByCol());
		searchContainer.setOrderByType(getOrderByType());
		searchContainer.setResultsAndTotal(
			() -> TeamServiceUtil.search(
				themeDisplay.getScopeGroupId(), getKeywords(), getKeywords(),
				new LinkedHashMap<>(), searchContainer.getStart(),
				searchContainer.getEnd(),
				searchContainer.getOrderByComparator()),
			TeamServiceUtil.searchCount(
				themeDisplay.getScopeGroupId(), getKeywords(), getKeywords(),
				new LinkedHashMap<>()));
		searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_renderResponse));

		return searchContainer;
	}

	public boolean isDescriptiveView() {
		return Objects.equals(getDisplayStyle(), "descriptive");
	}

	public boolean isListView() {
		return Objects.equals(getDisplayStyle(), "list");
	}

	protected String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_renderRequest, "keywords");

		return _keywords;
	}

	private String _displayStyle;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}