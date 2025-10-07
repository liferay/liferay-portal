/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.util;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.TeamLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.service.permission.RolePermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.security.permission.UserBagFactoryUtil;
import com.liferay.portal.service.permission.UserGroupPermissionUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Raymond Augé
 */
public class AnnouncementsUtil {

	public static LinkedHashMap<Long, long[]> getAnnouncementScopes(User user)
		throws PortalException {

		long userId = user.getUserId();

		// General announcements

		// Personal announcements

		LinkedHashMap<Long, long[]> scopes =
			LinkedHashMapBuilder.<Long, long[]>put(
				0L, new long[] {0}
			).put(
				PortalUtil.getClassNameId(User.class.getName()),
				new long[] {userId}
			).build();

		// Organization announcements

		UserBag userBag = UserBagFactoryUtil.create(user);

		long[] organizationIds = userBag.getUserOrgIds();

		if (organizationIds.length > 0) {
			scopes.put(
				PortalUtil.getClassNameId(Organization.class.getName()),
				organizationIds);
		}

		// Site announcements

		long[] groupIds = userBag.getUserGroupIds();

		if (groupIds.length > 0) {
			scopes.put(
				PortalUtil.getClassNameId(Group.class.getName()), groupIds);
		}

		// User group announcements

		List<UserGroup> userGroups =
			UserGroupLocalServiceUtil.getUserUserGroups(userId);

		if (!userGroups.isEmpty()) {
			scopes.put(
				PortalUtil.getClassNameId(UserGroup.class.getName()),
				ListUtil.toLongArray(
					userGroups, UserGroup.USER_GROUP_ID_ACCESSOR));
		}

		// Role announcements

		Set<Long> roleIds = SetUtil.fromArray(userBag.getRoleIds());

		if ((groupIds.length > 0) || (organizationIds.length > 0)) {
			List<UserGroupRole> userGroupRoles =
				UserGroupRoleLocalServiceUtil.getUserGroupRoles(userId);

			for (UserGroupRole userGroupRole : userGroupRoles) {
				roleIds.add(userGroupRole.getRoleId());
			}
		}

		if (!userGroups.isEmpty()) {
			List<UserGroupGroupRole> userGroupGroupRoles =
				UserGroupGroupRoleLocalServiceUtil.getUserGroupGroupRolesByUser(
					userId);

			for (UserGroupGroupRole userGroupGroupRole : userGroupGroupRoles) {
				roleIds.add(userGroupGroupRole.getRoleId());
			}
		}

		List<Team> teams = TeamLocalServiceUtil.getUserTeams(userId);

		long[] teamIds = ListUtil.toLongArray(teams, Team.TEAM_ID_ACCESSOR);

		long companyId = user.getCompanyId();

		if (teamIds.length > 0) {
			List<Role> teamsRoles = RoleLocalServiceUtil.getTeamsRoles(
				companyId, teamIds);

			for (Role teamRole : teamsRoles) {
				roleIds.add(teamRole.getRoleId());
			}
		}

		if (_PERMISSIONS_CHECK_GUEST_ENABLED) {
			Role guestRole = RoleLocalServiceUtil.getRole(
				companyId, RoleConstants.GUEST);

			roleIds.add(guestRole.getRoleId());
		}

		if (!roleIds.isEmpty()) {
			scopes.put(
				PortalUtil.getClassNameId(Role.class.getName()),
				ArrayUtil.toLongArray(roleIds));
		}

		return scopes;
	}

	public static List<Group> getGroups(ThemeDisplay themeDisplay)
		throws PortalException {

		return TransformUtil.transform(
			GroupLocalServiceUtil.getUserGroups(themeDisplay.getUserId(), true),
			group -> {
				if (((group.isOrganization() && group.isSite()) ||
					 group.isRegularSite()) &&
					GroupPermissionUtil.contains(
						themeDisplay.getPermissionChecker(), group.getGroupId(),
						ActionKeys.MANAGE_ANNOUNCEMENTS)) {

					return group;
				}

				return null;
			});
	}

	public static List<Organization> getOrganizations(ThemeDisplay themeDisplay)
		throws PortalException {

		List<Organization> organizations =
			OrganizationLocalServiceUtil.getUserOrganizations(
				themeDisplay.getUserId());

		return TransformUtil.transform(
			organizations,
			organization -> {
				if (OrganizationPermissionUtil.contains(
						themeDisplay.getPermissionChecker(),
						organization.getOrganizationId(),
						ActionKeys.MANAGE_ANNOUNCEMENTS)) {

					return organization;
				}

				return null;
			});
	}

	public static List<Role> getRoles(ThemeDisplay themeDisplay)
		throws PortalException {

		return TransformUtil.transform(
			RoleLocalServiceUtil.getRoles(themeDisplay.getCompanyId()),
			role -> {
				if (hasManageAnnouncementsPermission(
						role, themeDisplay.getPermissionChecker())) {

					return role;
				}

				return null;
			});
	}

	public static List<UserGroup> getUserGroups(ThemeDisplay themeDisplay) {
		return TransformUtil.transform(
			UserGroupLocalServiceUtil.getUserGroups(
				themeDisplay.getCompanyId()),
			userGroup -> {
				if (UserGroupPermissionUtil.contains(
						themeDisplay.getPermissionChecker(),
						userGroup.getUserGroupId(),
						ActionKeys.MANAGE_ANNOUNCEMENTS)) {

					return userGroup;
				}

				return null;
			});
	}

	public static boolean hasManageAnnouncementsPermission(
			Role role, PermissionChecker permissionChecker)
		throws PortalException {

		if (role.isTeam()) {
			Team team = TeamLocalServiceUtil.getTeam(role.getClassPK());

			if (GroupPermissionUtil.contains(
					permissionChecker, team.getGroupId(),
					ActionKeys.MANAGE_ANNOUNCEMENTS) &&
				RolePermissionUtil.contains(
					permissionChecker, team.getGroupId(), role.getRoleId(),
					ActionKeys.MANAGE_ANNOUNCEMENTS)) {

				return true;
			}
		}
		else if (RolePermissionUtil.contains(
					permissionChecker, role.getRoleId(),
					ActionKeys.MANAGE_ANNOUNCEMENTS)) {

			return true;
		}

		return false;
	}

	public static String toJSON(List<String> content) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			TransformUtil.transform(content, HtmlUtil::escape));

		return jsonArray.toString();
	}

	public static List<String> toStringList(String json) throws JSONException {
		return TransformUtil.transform(
			JSONUtil.toStringList(JSONFactoryUtil.createJSONArray(json)),
			HtmlUtil::unescape);
	}

	private static final boolean _PERMISSIONS_CHECK_GUEST_ENABLED =
		GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.PERMISSIONS_CHECK_GUEST_ENABLED));

}