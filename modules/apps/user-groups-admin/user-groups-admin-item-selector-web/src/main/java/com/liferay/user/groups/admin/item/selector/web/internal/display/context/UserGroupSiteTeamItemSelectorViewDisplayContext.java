/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.groups.admin.item.selector.web.internal.display.context;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.TeamLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.persistence.constants.UserGroupFinderConstants;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.user.groups.admin.item.selector.UserGroupSiteTeamItemSelectorCriterion;
import com.liferay.user.groups.admin.item.selector.web.internal.search.UserGroupSiteTeamChecker;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;

/**
 * @author Eudaldo Alonso
 */
public class UserGroupSiteTeamItemSelectorViewDisplayContext {

	public UserGroupSiteTeamItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		RenderRequest renderRequest, RenderResponse renderResponse,
		UserGroupSiteTeamItemSelectorCriterion
			userGroupSiteTeamItemSelectorCriterion) {

		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_userGroupSiteTeamItemSelectorCriterion =
			userGroupSiteTeamItemSelectorCriterion;
	}

	public SearchContainer<UserGroup> getUserGroupSearchContainer() {
		if (_userGroupSearchContainer != null) {
			return _userGroupSearchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SearchContainer<UserGroup> userGroupSearchContainer =
			new SearchContainer<>(
				_renderRequest, _portletURL, null, "no-user-groups-were-found");

		userGroupSearchContainer.setOrderByCol(_getOrderByCol());
		userGroupSearchContainer.setOrderByComparator(
			UsersAdminUtil.getUserGroupOrderByComparator(
				_getOrderByCol(), _getOrderByType()));
		userGroupSearchContainer.setOrderByType(_getOrderByType());

		Team team = TeamLocalServiceUtil.fetchTeam(
			_userGroupSiteTeamItemSelectorCriterion.getTeamId());

		LinkedHashMap<String, Object> userGroupParams =
			LinkedHashMapBuilder.<String, Object>put(
				UserGroupFinderConstants.PARAM_KEY_USER_GROUPS_GROUPS,
				() -> {
					Group group = GroupLocalServiceUtil.fetchGroup(
						team.getGroupId());

					if (group != null) {
						group = StagingUtil.getLiveGroup(group.getGroupId());
					}

					return group.getGroupId();
				}
			).build();

		userGroupSearchContainer.setResultsAndTotal(
			() -> UserGroupLocalServiceUtil.search(
				themeDisplay.getCompanyId(), _getKeywords(), userGroupParams,
				userGroupSearchContainer.getStart(),
				userGroupSearchContainer.getEnd(),
				userGroupSearchContainer.getOrderByComparator()),
			UserGroupLocalServiceUtil.searchCount(
				themeDisplay.getCompanyId(), _getKeywords(), userGroupParams));

		userGroupSearchContainer.setRowChecker(
			new UserGroupSiteTeamChecker(_renderResponse, team));

		_userGroupSearchContainer = userGroupSearchContainer;

		return _userGroupSearchContainer;
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private String _getOrderByCol() {
		return ParamUtil.getString(
			_renderRequest, SearchContainer.DEFAULT_ORDER_BY_COL_PARAM, "name");
	}

	private String _getOrderByType() {
		return ParamUtil.getString(
			_renderRequest, SearchContainer.DEFAULT_ORDER_BY_TYPE_PARAM, "asc");
	}

	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private final PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<UserGroup> _userGroupSearchContainer;
	private final UserGroupSiteTeamItemSelectorCriterion
		_userGroupSiteTeamItemSelectorCriterion;

}