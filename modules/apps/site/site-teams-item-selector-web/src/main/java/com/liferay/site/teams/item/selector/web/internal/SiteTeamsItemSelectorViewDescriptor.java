/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.teams.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.service.TeamLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.TeamNameComparator;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class SiteTeamsItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<Team> {

	public SiteTeamsItemSelectorViewDescriptor(
		HttpServletRequest httpServletRequest, PortletURL portletURL) {

		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getDefaultDisplayStyle() {
		return "list";
	}

	@Override
	public String[] getDisplayViews() {
		return new String[] {"list", "descriptive"};
	}

	@Override
	public ItemDescriptor getItemDescriptor(Team team) {
		return new SiteTeamsItemDescriptor(team);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new UUIDItemSelectorReturnType();
	}

	@Override
	public String[] getOrderByKeys() {
		return new String[] {"name"};
	}

	@Override
	public SearchContainer<Team> getSearchContainer() throws PortalException {
		return _getTeamSearchContainer();
	}

	@Override
	public boolean isShowBreadcrumb() {
		return false;
	}

	@Override
	public boolean isShowSearch() {
		return true;
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

		_orderByCol = ParamUtil.getString(
			_httpServletRequest, "orderByCol", "name");

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

	private SearchContainer<Team> _getTeamSearchContainer() {
		RenderRequest renderRequest =
			(RenderRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		SearchContainer<Team> teamSearchContainer = new SearchContainer<>(
			renderRequest, _portletURL, null, "no-teams-were-found");

		teamSearchContainer.setOrderByCol(_getOrderByCol());

		boolean orderByAsc = false;

		if (Objects.equals(_getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		teamSearchContainer.setOrderByComparator(
			TeamNameComparator.getInstance(orderByAsc));
		teamSearchContainer.setOrderByType(_getOrderByType());

		teamSearchContainer.setResultsAndTotal(
			() -> TeamLocalServiceUtil.search(
				_themeDisplay.getScopeGroupId(), _getKeywords(), _getKeywords(),
				new LinkedHashMap<>(), teamSearchContainer.getStart(),
				teamSearchContainer.getEnd(),
				teamSearchContainer.getOrderByComparator()),
			TeamLocalServiceUtil.searchCount(
				_themeDisplay.getScopeGroupId(), _getKeywords(), _getKeywords(),
				new LinkedHashMap<>()));

		return teamSearchContainer;
	}

	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final PortletURL _portletURL;
	private final ThemeDisplay _themeDisplay;

}