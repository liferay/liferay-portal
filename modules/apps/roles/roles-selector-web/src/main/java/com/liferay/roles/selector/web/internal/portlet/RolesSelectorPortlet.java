/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.selector.web.internal.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.UserGroupGroupRoleService;
import com.liferay.portal.kernel.service.UserGroupRoleService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.roles.selector.web.internal.constants.RolesSelectorPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-roles-selector",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.system=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Roles Selector",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + RolesSelectorPortletKeys.ROLES_SELECTOR,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class RolesSelectorPortlet extends MVCPortlet {

	public void editUserGroupGroupRoleUsers(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		long roleId = ParamUtil.getLong(actionRequest, "roleId");

		long[] addUserGroupIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "addUserGroupIds"), 0L);
		long[] removeUserGroupIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "removeUserGroupIds"), 0L);

		_userGroupGroupRoleService.addUserGroupGroupRoles(
			addUserGroupIds, groupId, roleId);
		_userGroupGroupRoleService.deleteUserGroupGroupRoles(
			removeUserGroupIds, groupId, roleId);
	}

	public void editUserGroupRoleUsers(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		long roleId = ParamUtil.getLong(actionRequest, "roleId");

		long[] addUserIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "addUserIds"), 0L);
		long[] removeUserIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "removeUserIds"), 0L);

		_userGroupRoleService.addUserGroupRoles(addUserIds, groupId, roleId);
		_userGroupRoleService.deleteUserGroupRoles(
			removeUserIds, groupId, roleId);
	}

	@Reference
	private UserGroupGroupRoleService _userGroupGroupRoleService;

	@Reference
	private UserGroupRoleService _userGroupRoleService;

}