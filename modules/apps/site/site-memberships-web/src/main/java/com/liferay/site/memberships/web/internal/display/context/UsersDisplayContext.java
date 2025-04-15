/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.display.context;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.TeamLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.memberships.constants.SiteMembershipsPortletKeys;
import com.liferay.site.memberships.web.internal.util.GroupUtil;
import com.liferay.users.admin.search.UserSearch;
import com.liferay.users.admin.search.UserSearchTerms;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * @author Eudaldo Alonso
 */
public class UsersDisplayContext {

	public UsersDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(
			httpServletRequest);
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_httpServletRequest,
			SiteMembershipsPortletKeys.SITE_MEMBERSHIPS_ADMIN,
			"display-style-users", "icon");

		return _displayStyle;
	}

	public long getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_groupId = ParamUtil.getLong(
			_httpServletRequest, "groupId",
			themeDisplay.getSiteGroupIdOrLiveGroupId());

		return _groupId;
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_renderRequest, "keywords");

		return _keywords;
	}

	public String getNavigation() {
		if (_navigation != null) {
			return _navigation;
		}

		_navigation = ParamUtil.getString(
			_httpServletRequest, "navigation", "all");

		return _navigation;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest,
			SiteMembershipsPortletKeys.SITE_MEMBERSHIPS_ADMIN,
			"order-by-col-users", "modified-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest,
			SiteMembershipsPortletKeys.SITE_MEMBERSHIPS_ADMIN,
			"order-by-type-users", "asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view.jsp"
		).setRedirect(
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)_httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return themeDisplay.getURLCurrent();
			}
		).setKeywords(
			() -> {
				String keywords = getKeywords();

				if (Validator.isNotNull(keywords)) {
					return keywords;
				}

				return null;
			}
		).setNavigation(
			() -> {
				String navigation = getNavigation();

				if (Validator.isNotNull(navigation)) {
					return navigation;
				}

				return null;
			}
		).setTabs1(
			"users"
		).setParameter(
			"displayStyle",
			() -> {
				String displayStyle = getDisplayStyle();

				if (Validator.isNotNull(displayStyle)) {
					return displayStyle;
				}

				return null;
			}
		).setParameter(
			"groupId", getGroupId()
		).setParameter(
			"orderByCol",
			() -> {
				String orderByCol = getOrderByCol();

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
		).setParameter(
			"roleId",
			() -> {
				Role role = getRole();

				if (role != null) {
					return role.getRoleId();
				}

				return null;
			}
		).setParameter(
			"teamId",
			() -> {
				Team team = getTeam();

				if (team != null) {
					return team.getTeamId();
				}

				return null;
			}
		).buildPortletURL();
	}

	public Role getRole() {
		if (_role != null) {
			return _role;
		}

		long roleId = ParamUtil.getLong(_httpServletRequest, "roleId");

		if (roleId > 0) {
			_role = RoleLocalServiceUtil.fetchRole(roleId);
		}

		return _role;
	}

	public Team getTeam() {
		if (_team != null) {
			return _team;
		}

		long teamId = ParamUtil.getLong(_httpServletRequest, "teamId");

		if (teamId > 0) {
			_team = TeamLocalServiceUtil.fetchTeam(teamId);
		}

		return _team;
	}

	public SearchContainer<User> getUserSearchContainer()
		throws PortalException {

		if (_userSearch != null) {
			return _userSearch;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		UserSearch userSearch = new UserSearch(_renderRequest, getPortletURL());

		userSearch.setEmptyResultsMessage(
			LanguageUtil.format(
				themeDisplay.getLocale(),
				"no-user-was-found-that-is-a-direct-member-of-this-x",
				StringUtil.toLowerCase(
					GroupUtil.getGroupTypeLabel(
						_groupId, themeDisplay.getLocale())),
				false));
		userSearch.setOrderByCol(getOrderByCol());
		userSearch.setOrderByType(getOrderByType());

		UserSearchTerms searchTerms =
			(UserSearchTerms)userSearch.getSearchTerms();

		LinkedHashMap<String, Object> userParams =
			LinkedHashMapBuilder.<String, Object>put(
				"inherit", Boolean.TRUE
			).put(
				"usersGroups", Long.valueOf(getGroupId())
			).put(
				"userGroupRole",
				() -> {
					Role role = getRole();

					if (role != null) {
						return new Long[] {
							Long.valueOf(getGroupId()),
							Long.valueOf(role.getRoleId())
						};
					}

					return null;
				}
			).put(
				"usersTeams",
				() -> {
					Team team = getTeam();

					if (team != null) {
						return Long.valueOf(team.getTeamId());
					}

					return null;
				}
			).build();

		if (GroupPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), getGroupId(),
				ActionKeys.VIEW_MEMBERS)) {

			userSearch.setResultsAndTotal(
				() -> UserLocalServiceUtil.search(
					themeDisplay.getCompanyId(), searchTerms.getKeywords(),
					searchTerms.getStatus(), userParams, userSearch.getStart(),
					userSearch.getEnd(), userSearch.getOrderByComparator()),
				UserLocalServiceUtil.searchCount(
					themeDisplay.getCompanyId(), searchTerms.getKeywords(),
					searchTerms.getStatus(), userParams));
		}
		else {
			userSearch.setResultsAndTotal(Collections::emptyList, 0);
		}

		userSearch.setRowChecker(new EmptyOnClickRowChecker(_renderResponse));

		_userSearch = userSearch;

		return _userSearch;
	}

	private String _displayStyle;
	private Long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _navigation;
	private String _orderByCol;
	private String _orderByType;
	private final PortalPreferences _portalPreferences;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private Role _role;
	private Team _team;
	private UserSearch _userSearch;

}