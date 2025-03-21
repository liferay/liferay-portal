/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.item.selector.web.internal.display.context;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.TeamLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.users.admin.item.selector.UserSiteTeamItemSelectorCriterion;
import com.liferay.users.admin.item.selector.web.internal.search.UserSiteTeamChecker;
import com.liferay.users.admin.search.UserSearch;
import com.liferay.users.admin.search.UserSearchTerms;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;

/**
 * @author Eudaldo Alonso
 */
public class UserSiteTeamItemSelectorViewDisplayContext {

	public UserSiteTeamItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		RenderRequest renderRequest, RenderResponse renderResponse,
		UserSiteTeamItemSelectorCriterion userSiteTeamItemSelectorCriterion) {

		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_userSiteTeamItemSelectorCriterion = userSiteTeamItemSelectorCriterion;
	}

	public SearchContainer<User> getUserSearchContainer() {
		if (_userSearchContainer != null) {
			return _userSearchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SearchContainer<User> userSearchContainer = new UserSearch(
			_renderRequest, _portletURL);

		UserSearchTerms searchTerms =
			(UserSearchTerms)userSearchContainer.getSearchTerms();

		Team team = TeamLocalServiceUtil.fetchTeam(
			_userSiteTeamItemSelectorCriterion.getTeamId());

		LinkedHashMap<String, Object> userParams =
			LinkedHashMapBuilder.<String, Object>put(
				"inherit", Boolean.TRUE
			).put(
				"usersGroups",
				() -> {
					Group group = GroupLocalServiceUtil.fetchGroup(
						team.getGroupId());

					if (group != null) {
						group = StagingUtil.getLiveGroup(group.getGroupId());
					}

					return group.getGroupId();
				}
			).build();

		userSearchContainer.setResultsAndTotal(
			() -> UserLocalServiceUtil.search(
				themeDisplay.getCompanyId(), searchTerms.getKeywords(),
				searchTerms.getStatus(), userParams,
				userSearchContainer.getStart(), userSearchContainer.getEnd(),
				userSearchContainer.getOrderByComparator()),
			UserLocalServiceUtil.searchCount(
				themeDisplay.getCompanyId(), searchTerms.getKeywords(),
				searchTerms.getStatus(), userParams));

		userSearchContainer.setRowChecker(
			new UserSiteTeamChecker(_renderResponse, team));

		_userSearchContainer = userSearchContainer;

		return _userSearchContainer;
	}

	private final HttpServletRequest _httpServletRequest;
	private final PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<User> _userSearchContainer;
	private final UserSiteTeamItemSelectorCriterion
		_userSiteTeamItemSelectorCriterion;

}