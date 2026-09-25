/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.invitation.invite.members.service.impl;

import com.liferay.invitation.invite.members.service.base.MemberRequestServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.UserGroupRolePermissionUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=im",
		"json.web.service.context.path=MemberRequest"
	},
	service = AopService.class
)
public class MemberRequestServiceImpl extends MemberRequestServiceBaseImpl {

	@Override
	public void addMemberRequests(
			long groupId, long[] receiverUserIds, long invitedRoleId,
			long invitedTeamId, ServiceContext serviceContext)
		throws PortalException {

		_check(groupId, invitedRoleId, invitedTeamId);

		memberRequestLocalService.addMemberRequests(
			getUserId(), groupId, receiverUserIds, invitedRoleId, invitedTeamId,
			serviceContext);
	}

	@Override
	public void addMemberRequests(
			long groupId, String[] emailAddresses, long invitedRoleId,
			long invitedTeamId, ServiceContext serviceContext)
		throws PortalException {

		_check(groupId, invitedRoleId, invitedTeamId);

		memberRequestLocalService.addMemberRequests(
			getUserId(), groupId, emailAddresses, invitedRoleId, invitedTeamId,
			serviceContext);
	}

	private void _check(long groupId, long invitedRoleId, long invitedTeamId)
		throws PortalException {

		if (!_userLocalService.hasGroupUser(groupId, getUserId())) {
			throw new PrincipalException();
		}

		PermissionChecker permissionChecker = getPermissionChecker();

		if (invitedRoleId > 0) {
			UserGroupRolePermissionUtil.check(
				permissionChecker, groupId, invitedRoleId);
		}

		if (invitedTeamId > 0) {
			Team team = _teamLocalService.getTeam(invitedTeamId);

			if (groupId != team.getGroupId()) {
				throw new PrincipalException();
			}

			GroupPermissionUtil.check(
				permissionChecker, groupId, ActionKeys.MANAGE_TEAMS);
		}
	}

	@Reference
	private TeamLocalService _teamLocalService;

	@Reference
	private UserLocalService _userLocalService;

}