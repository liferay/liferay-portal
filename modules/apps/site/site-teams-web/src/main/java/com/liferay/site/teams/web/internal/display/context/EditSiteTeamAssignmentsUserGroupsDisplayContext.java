/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.teams.web.internal.display.context;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.persistence.constants.UserGroupFinderConstants;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.site.teams.web.internal.constants.SiteTeamsPortletKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;

/**
 * @author Eudaldo Alonso
 */
public class EditSiteTeamAssignmentsUserGroupsDisplayContext
	extends EditSiteTeamAssignmentsDisplayContext {

	public EditSiteTeamAssignmentsUserGroupsDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		super(httpServletRequest, renderRequest, renderResponse);
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			httpServletRequest, SiteTeamsPortletKeys.SITE_TEAMS,
			"usergroup-display-style", "list");

		return _displayStyle;
	}

	@Override
	public PortletURL getEditTeamAssignmentsURL() {
		PortletURL portletURL = super.getEditTeamAssignmentsURL();

		String keywords = getKeywords();

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		String orderByCol = getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			portletURL.setParameter("orderByCol", orderByCol);
		}

		String orderByType = getOrderByType();

		if (Validator.isNotNull(orderByType)) {
			portletURL.setParameter("orderByType", orderByType);
		}

		return portletURL;
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(httpServletRequest, "keywords");

		return _keywords;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			httpServletRequest, SiteTeamsPortletKeys.SITE_TEAMS,
			"usergroup-order-by-col", "name");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			httpServletRequest, SiteTeamsPortletKeys.SITE_TEAMS,
			"usergroup-order-by-type", "asc");

		return _orderByType;
	}

	public SearchContainer<UserGroup> getUserGroupSearchContainer() {
		if (_userGroupSearchContainer != null) {
			return _userGroupSearchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SearchContainer<UserGroup> userGroupSearchContainer =
			new SearchContainer<>(
				renderRequest, getEditTeamAssignmentsURL(), null,
				"no-user-groups-were-found");

		userGroupSearchContainer.setOrderByCol(getOrderByCol());
		userGroupSearchContainer.setOrderByComparator(
			UsersAdminUtil.getUserGroupOrderByComparator(
				getOrderByCol(), getOrderByType()));
		userGroupSearchContainer.setOrderByType(getOrderByType());

		LinkedHashMap<String, Object> userGroupParams =
			LinkedHashMapBuilder.<String, Object>put(
				UserGroupFinderConstants.PARAM_KEY_USER_GROUPS_TEAMS,
				getTeamId()
			).build();

		userGroupSearchContainer.setResultsAndTotal(
			() -> UserGroupLocalServiceUtil.search(
				themeDisplay.getCompanyId(), getKeywords(), userGroupParams,
				userGroupSearchContainer.getStart(),
				userGroupSearchContainer.getEnd(),
				userGroupSearchContainer.getOrderByComparator()),
			UserGroupLocalServiceUtil.searchCount(
				themeDisplay.getCompanyId(), getKeywords(), userGroupParams));

		userGroupSearchContainer.setRowChecker(
			new EmptyOnClickRowChecker(renderResponse));

		_userGroupSearchContainer = userGroupSearchContainer;

		return _userGroupSearchContainer;
	}

	private String _displayStyle;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private SearchContainer<UserGroup> _userGroupSearchContainer;

}