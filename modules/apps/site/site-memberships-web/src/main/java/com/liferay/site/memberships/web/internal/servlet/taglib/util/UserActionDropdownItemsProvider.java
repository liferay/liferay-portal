/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.membershippolicy.SiteMembershipPolicyUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class UserActionDropdownItemsProvider {

	public UserActionDropdownItemsProvider(
		User user, RenderRequest renderRequest, RenderResponse renderResponse) {

		_user = user;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		return DropdownItemListBuilder.add(
			() -> GroupPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getSiteGroupIdOrLiveGroupId(),
				ActionKeys.ASSIGN_USER_ROLES),
			_getAssignRolesActionUnsafeConsumer()
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "unassignRoles");
				dropdownItem.putData(
					"unassignUserGroupRoleURL",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setMVCPath(
						"/users_roles.jsp"
					).setParameter(
						"assignRoles", Boolean.FALSE
					).setParameter(
						"groupId", _themeDisplay.getSiteGroupIdOrLiveGroupId()
					).setParameter(
						"p_u_i_d", _user.getUserId()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString());
				dropdownItem.putData("userId", _user.getUserId());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "unassign-roles"));
			}
		).add(
			() ->
				GroupPermissionUtil.contains(
					_themeDisplay.getPermissionChecker(),
					_themeDisplay.getSiteGroupIdOrLiveGroupId(),
					ActionKeys.ASSIGN_MEMBERS) &&
				!SiteMembershipPolicyUtil.isMembershipProtected(
					_themeDisplay.getPermissionChecker(), _user.getUserId(),
					_themeDisplay.getSiteGroupIdOrLiveGroupId()) &&
				!SiteMembershipPolicyUtil.isMembershipRequired(
					_user.getUserId(),
					_themeDisplay.getSiteGroupIdOrLiveGroupId()),
			_getDeleteGroupUsersActionUnsafeConsumer()
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getAssignRolesActionUnsafeConsumer()
		throws Exception {

		return dropdownItem -> {
			dropdownItem.putData("action", "assignRoles");
			dropdownItem.putData(
				"assignRolesURL",
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCPath(
					"/users_roles.jsp"
				).setParameter(
					"groupId", _themeDisplay.getSiteGroupIdOrLiveGroupId()
				).setParameter(
					"p_u_i_d", _user.getUserId()
				).setParameter(
					"roleType",
					() -> {
						Group group = _themeDisplay.getScopeGroup();

						if (!group.isSite() && group.isDepot()) {
							return RoleConstants.TYPE_DEPOT;
						}

						return null;
					}
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString());
			dropdownItem.putData(
				"editUserGroupRoleURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"editUserGroupRole"
				).setParameter(
					"p_u_i_d", _user.getUserId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "assign-roles"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteGroupUsersActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteGroupUsers");
			dropdownItem.putData(
				"deleteGroupUsersURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"deleteGroupUsers"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"groupId", _themeDisplay.getSiteGroupIdOrLiveGroupId()
				).setParameter(
					"removeUserId", _user.getUserId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "remove-membership"));
		};
	}

	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;
	private final User _user;

}