/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.selector.web.internal.display.context;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.roles.admin.search.RoleSearch;
import com.liferay.roles.admin.search.RoleSearchTerms;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Mariano Álvaro Sáiz
 */
public class EditRolesDisplayContext {

	public EditRolesDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public long getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		Group group = _getGroup();

		_groupId = group.getGroupId();

		return _groupId;
	}

	public SearchContainer<Role> getSearchContainer() throws PortalException {
		if (_roleSearch != null) {
			return _roleSearch;
		}

		_roleSearch = new RoleSearch(
			_renderRequest,
			(PortletURL)_httpServletRequest.getAttribute(
				"edit_roles.jsp-portletURL"));

		RoleSearchTerms searchTerms =
			(RoleSearchTerms)_roleSearch.getSearchTerms();

		_roleSearch.setResultsAndTotal(
			UsersAdminUtil.filterGroupRoles(
				_themeDisplay.getPermissionChecker(), getGroupId(),
				RoleLocalServiceUtil.search(
					_themeDisplay.getCompanyId(), searchTerms.getKeywords(),
					new Integer[] {
						(Integer)_httpServletRequest.getAttribute(
							"edit_roles.jsp-roleType")
					},
					QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					_roleSearch.getOrderByComparator())));

		return _roleSearch;
	}

	private Group _getGroup() {
		if (_group != null) {
			return _group;
		}

		_group = (Group)_httpServletRequest.getAttribute(
			"edit_roles.jsp-group");

		return _group;
	}

	private Group _group;
	private Long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private final RenderRequest _renderRequest;
	private RoleSearch _roleSearch;
	private final ThemeDisplay _themeDisplay;

}