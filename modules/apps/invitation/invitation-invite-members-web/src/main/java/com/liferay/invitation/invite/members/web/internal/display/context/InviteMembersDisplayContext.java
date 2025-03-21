/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.invitation.invite.members.web.internal.display.context;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.TeamLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.RoleNameComparator;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Map;

/**
 * @author Diego Hu
 */
public class InviteMembersDisplayContext {

	public InviteMembersDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getInviteMembersProps() throws PortalException {
		return HashMapBuilder.<String, Object>put(
			"assignRolesPermission",
			GroupPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), ActionKeys.ASSIGN_USER_ROLES)
		).put(
			"getAvailableUsersURL",
			ResourceURLBuilder.createResourceURL(
				_renderResponse
			).setResourceID(
				"getAvailableUsers"
			).buildString()
		).put(
			"manageTeamsPermission",
			GroupPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), ActionKeys.MANAGE_TEAMS)
		).put(
			"roles",
			TransformUtil.transform(
				UsersAdminUtil.filterGroupRoles(
					PermissionThreadLocal.getPermissionChecker(),
					_themeDisplay.getScopeGroupId(),
					RoleLocalServiceUtil.search(
						_themeDisplay.getCompanyId(), null, null,
						new Integer[] {RoleConstants.TYPE_SITE},
						QueryUtil.ALL_POS, QueryUtil.ALL_POS,
						RoleNameComparator.getInstance(false))),
				role -> HashMapBuilder.<String, Object>put(
					"label", role.getTitle(_themeDisplay.getLocale())
				).put(
					"value", role.getRoleId()
				).build())
		).put(
			"scopeGroupId", _themeDisplay.getScopeGroupId()
		).put(
			"sendInvitesURL",
			PortletURLBuilder.createActionURL(
				_renderResponse
			).setActionName(
				"sendInvites"
			).buildString()
		).put(
			"teams",
			TransformUtil.transform(
				TeamLocalServiceUtil.getGroupTeams(
					_themeDisplay.getScopeGroupId()),
				team -> HashMapBuilder.<String, Object>put(
					"label", team.getName()
				).put(
					"value", team.getTeamId()
				).build())
		).build();
	}

	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}