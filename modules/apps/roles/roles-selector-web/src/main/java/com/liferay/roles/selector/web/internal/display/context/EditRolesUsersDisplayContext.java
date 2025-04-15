/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.selector.web.internal.display.context;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.search.OrganizationRoleUserChecker;
import com.liferay.site.search.UserGroupRoleUserChecker;
import com.liferay.users.admin.search.UserSearch;
import com.liferay.users.admin.search.UserSearchTerms;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author Mariano Álvaro Sáiz
 */
public class EditRolesUsersDisplayContext {

	public EditRolesUsersDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public SearchContainer<User> getSearchContainer() throws PortalException {
		if (_userSearch != null) {
			return _userSearch;
		}

		_userSearch = new UserSearch(
			_renderRequest,
			(PortletURL)_httpServletRequest.getAttribute(
				"edit_roles.jsp-portletURL"));

		UserSearchTerms searchTerms =
			(UserSearchTerms)_userSearch.getSearchTerms();

		LinkedHashMap<String, Object> userParams =
			LinkedHashMapBuilder.<String, Object>put(
				"inherit", Boolean.TRUE
			).put(
				"usersGroups", Long.valueOf(_getGroupId())
			).put(
				"userGroupRole",
				() -> {
					if (Objects.equals(
							_httpServletRequest.getAttribute(
								"edit_roles.jsp-tabs1"),
							"current")) {

						return new Long[] {
							Long.valueOf(_getGroupId()),
							Long.valueOf(_getRoleId())
						};
					}

					return null;
				}
			).build();

		if (searchTerms.isAdvancedSearch()) {
			_userSearch.setResultsAndTotal(
				() -> UserLocalServiceUtil.search(
					_themeDisplay.getCompanyId(), searchTerms.getFirstName(),
					searchTerms.getMiddleName(), searchTerms.getLastName(),
					searchTerms.getScreenName(), searchTerms.getEmailAddress(),
					searchTerms.getStatus(), userParams,
					searchTerms.isAndOperator(), _userSearch.getStart(),
					_userSearch.getEnd(), _userSearch.getOrderByComparator()),
				UserLocalServiceUtil.searchCount(
					_themeDisplay.getCompanyId(), searchTerms.getFirstName(),
					searchTerms.getMiddleName(), searchTerms.getLastName(),
					searchTerms.getScreenName(), searchTerms.getEmailAddress(),
					searchTerms.getStatus(), userParams,
					searchTerms.isAndOperator()));
		}
		else {
			_userSearch.setResultsAndTotal(
				() -> UserLocalServiceUtil.search(
					_themeDisplay.getCompanyId(), searchTerms.getKeywords(),
					searchTerms.getStatus(), userParams, _userSearch.getStart(),
					_userSearch.getEnd(), _userSearch.getOrderByComparator()),
				UserLocalServiceUtil.searchCount(
					_themeDisplay.getCompanyId(), searchTerms.getKeywords(),
					searchTerms.getStatus(), userParams));
		}

		Role role = _getRole();

		if (role.getType() == RoleConstants.TYPE_SITE) {
			_userSearch.setRowChecker(
				new UserGroupRoleUserChecker(
					_renderResponse, _getGroup(), role));
		}
		else {
			_userSearch.setRowChecker(
				new OrganizationRoleUserChecker(
					_renderResponse,
					(Organization)_httpServletRequest.getAttribute(
						"edit_roles.jsp-organization"),
					role));
		}

		return _userSearch;
	}

	private Group _getGroup() {
		if (_group != null) {
			return _group;
		}

		_group = (Group)_httpServletRequest.getAttribute(
			"edit_roles.jsp-group");

		return _group;
	}

	private long _getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		Group group = _getGroup();

		_groupId = group.getGroupId();

		return _groupId;
	}

	private Role _getRole() {
		if (_role != null) {
			return _role;
		}

		_role = (Role)_httpServletRequest.getAttribute("edit_roles.jsp-role");

		return _role;
	}

	private long _getRoleId() {
		if (_roleId != null) {
			return _roleId;
		}

		Role role = _getRole();

		_roleId = role.getRoleId();

		return _roleId;
	}

	private Group _group;
	private Long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private Role _role;
	private Long _roleId;
	private final ThemeDisplay _themeDisplay;
	private UserSearch _userSearch;

}