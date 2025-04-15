/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.memberships.web.internal.display.context.SiteMembershipsDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Diego Hu
 */
public class UserGroupActionDropdownItemsProvider {

	public UserGroupActionDropdownItemsProvider(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SiteMembershipsDisplayContext siteMembershipsDisplayContext,
		UserGroup userGroup) {

		_liferayPortletResponse = liferayPortletResponse;
		_siteMembershipsDisplayContext = siteMembershipsDisplayContext;
		_userGroup = userGroup;

		_httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);
		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		return DropdownItemListBuilder.add(
			() -> GroupPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(),
				_siteMembershipsDisplayContext.getGroup(),
				ActionKeys.ASSIGN_USER_ROLES),
			dropdownItem -> {
				dropdownItem.putData("action", "assignUserGroupRole");
				dropdownItem.putData(
					"assignUserGroupRoleURL",
					PortletURLBuilder.createActionURL(
						_liferayPortletResponse
					).setMVCPath(
						"/user_groups_roles.jsp"
					).setParameter(
						"groupId", _siteMembershipsDisplayContext.getGroupId()
					).setParameter(
						"userGroupId", _userGroup.getUserGroupId()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.putData(
					"userGroupId", String.valueOf(_userGroup.getUserGroupId()));
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "assign-roles"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "unassignUserGroupRole");
				dropdownItem.putData(
					"unassignUserGroupRoleURL",
					PortletURLBuilder.createActionURL(
						_liferayPortletResponse
					).setMVCPath(
						"/user_groups_roles.jsp"
					).setParameter(
						"assignRoles", Boolean.FALSE
					).setParameter(
						"groupId", _siteMembershipsDisplayContext.getGroupId()
					).setParameter(
						"userGroupId", _userGroup.getUserGroupId()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.putData(
					"userGroupId", String.valueOf(_userGroup.getUserGroupId()));
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "unassign-roles"));
			}
		).add(
			() -> GroupPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(),
				_siteMembershipsDisplayContext.getGroup(),
				ActionKeys.ASSIGN_MEMBERS),
			dropdownItem -> {
				dropdownItem.putData("action", "deleteGroupUserGroups");
				dropdownItem.putData(
					"deleteGroupUserGroupsURL",
					PortletURLBuilder.createActionURL(
						_liferayPortletResponse
					).setActionName(
						"deleteGroupUserGroups"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"groupId", _siteMembershipsDisplayContext.getGroupId()
					).setParameter(
						"removeUserGroupId", _userGroup.getUserGroupId()
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "remove-membership"));
			}
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final SiteMembershipsDisplayContext _siteMembershipsDisplayContext;
	private final ThemeDisplay _themeDisplay;
	private final UserGroup _userGroup;

}