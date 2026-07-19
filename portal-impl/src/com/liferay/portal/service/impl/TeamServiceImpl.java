/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.TeamPermissionUtil;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.service.base.TeamServiceBaseImpl;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class TeamServiceImpl extends TeamServiceBaseImpl {

	@Override
	public Team addTeam(
			long groupId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.MANAGE_TEAMS);

		return teamLocalService.addTeam(
			getUserId(), groupId, name, description, serviceContext);
	}

	@Override
	public void deleteTeam(long teamId) throws PortalException {
		TeamPermissionUtil.check(
			getPermissionChecker(), teamId, ActionKeys.DELETE);

		teamLocalService.deleteTeam(teamId);
	}

	@Override
	public List<Team> getGroupTeams(long groupId) throws PortalException {
		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.MANAGE_TEAMS);

		return teamLocalService.getGroupTeams(groupId);
	}

	@Override
	public Team getTeam(long teamId) throws PortalException {
		Team team = teamLocalService.getTeam(teamId);

		TeamPermissionUtil.check(getPermissionChecker(), team, ActionKeys.VIEW);

		return team;
	}

	@Override
	public Team getTeam(long groupId, String name) throws PortalException {
		Team team = teamLocalService.getTeam(groupId, name);

		TeamPermissionUtil.check(getPermissionChecker(), team, ActionKeys.VIEW);

		return team;
	}

	@Override
	public List<Team> getUserTeams(long userId) throws PortalException {
		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.UPDATE);

		return teamLocalService.getUserTeams(userId);
	}

	@Override
	public List<Team> getUserTeams(long userId, long groupId)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!GroupPermissionUtil.contains(
				permissionChecker, groupId, ActionKeys.MANAGE_TEAMS) &&
			!UserPermissionUtil.contains(
				permissionChecker, userId, ActionKeys.UPDATE)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, Group.class.getName(), groupId,
				ActionKeys.MANAGE_TEAMS, ActionKeys.UPDATE);
		}

		return teamLocalService.getUserTeams(userId, groupId);
	}

	@Override
	public boolean hasUserTeam(long userId, long teamId)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		Team team = teamPersistence.findByPrimaryKey(teamId);

		if (!GroupPermissionUtil.contains(
				permissionChecker, team.getGroupId(),
				ActionKeys.MANAGE_TEAMS) &&
			!UserPermissionUtil.contains(
				permissionChecker, userId, ActionKeys.UPDATE)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, Team.class.getName(), teamId,
				ActionKeys.MANAGE_TEAMS, ActionKeys.UPDATE);
		}

		return userPersistence.containsTeam(userId, teamId);
	}

	@Override
	public List<Team> search(
		long groupId, String name, String description,
		LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<Team> orderByComparator) {

		return teamFinder.filterFindByG_N_D(
			groupId, name, description, params, start, end, orderByComparator);
	}

	@Override
	public int searchCount(
		long groupId, String name, String description,
		LinkedHashMap<String, Object> params) {

		return teamFinder.filterCountByG_N_D(
			groupId, name, description, params);
	}

	@Override
	public Team updateTeam(long teamId, String name, String description)
		throws PortalException {

		TeamPermissionUtil.check(
			getPermissionChecker(), teamId, ActionKeys.UPDATE);

		return teamLocalService.updateTeam(teamId, name, description);
	}

}