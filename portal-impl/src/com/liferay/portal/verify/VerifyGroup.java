/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.service.impl.GroupLocalServiceImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author     Brian Wing Shun Chan
 */
public class VerifyGroup extends VerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		if (FeatureFlagManagerUtil.isEnabled("LPS-157670")) {
			verifyOrganizationNames();
			verifySites();
			verifyTree();
		}

		verifyStagedGroups();
	}

	protected void verifyOrganizationNames() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			StringBundler sb = new StringBundler(5);

			sb.append("select groupId, name from Group_ where name like '%");
			sb.append(GroupLocalServiceImpl.ORGANIZATION_NAME_SUFFIX);
			sb.append("%' and name not like '%");
			sb.append(GroupLocalServiceImpl.ORGANIZATION_NAME_SUFFIX);
			sb.append("'");

			try (PreparedStatement preparedStatement1 =
					connection.prepareStatement(sb.toString());
				PreparedStatement preparedStatement2 =
					AutoBatchPreparedStatementUtil.concurrentAutoBatch(
						connection,
						"update Group_ set name = ? where groupId = ?");
				ResultSet resultSet = preparedStatement1.executeQuery()) {

				while (resultSet.next()) {
					String name = resultSet.getString("name");

					if (name.endsWith(
							GroupLocalServiceImpl.ORGANIZATION_NAME_SUFFIX) ||
						name.endsWith(
							GroupLocalServiceImpl.
								ORGANIZATION_STAGING_SUFFIX)) {

						continue;
					}

					int pos = name.indexOf(
						GroupLocalServiceImpl.ORGANIZATION_NAME_SUFFIX);

					pos = name.indexOf(" ", pos + 1);

					String newName =
						name.substring(pos + 1) +
							GroupLocalServiceImpl.ORGANIZATION_NAME_SUFFIX;

					preparedStatement2.setString(1, newName);

					long groupId = resultSet.getLong("groupId");

					preparedStatement2.setLong(2, groupId);

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

	protected void verifySites() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			long organizationClassNameId = PortalUtil.getClassNameId(
				Organization.class);

			runSQL(
				StringBundler.concat(
					"update Group_ set site = [$TRUE$] where classNameId = ",
					organizationClassNameId,
					" and site = [$FALSE$] and exists (select 1 from Layout ",
					"where Layout.groupId = Group_.groupId)"));
		}
	}

	protected void verifyStagedGroups() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			List<Group> groups = GroupLocalServiceUtil.getLiveGroups();

			for (Group group : groups) {
				if (!group.hasStagingGroup()) {
					continue;
				}

				UnicodeProperties typeSettingsUnicodeProperties =
					group.getTypeSettingsProperties();

				verifyStagingTypeSettingsProperties(
					typeSettingsUnicodeProperties);

				GroupLocalServiceUtil.updateGroup(
					group.getGroupId(),
					typeSettingsUnicodeProperties.toString());

				Group stagingGroup = group.getStagingGroup();

				if (stagingGroup.isStagedRemotely()) {
					continue;
				}

				verifyStagingGroupOrganizationMembership(stagingGroup);
				verifyStagingGroupRoleMembership(stagingGroup);
				verifyStagingGroupUserGroupMembership(stagingGroup);
				verifyStagingGroupUserMembership(stagingGroup);
				verifyStagingUserGroupRolesAssignments(stagingGroup);
				verifyStagingUserGroupGroupRolesAssignments(stagingGroup);
			}
		}
	}

	protected void verifyStagingGroupOrganizationMembership(Group stagingGroup)
		throws Exception {

		List<Organization> stagingOrganizations =
			OrganizationLocalServiceUtil.getGroupOrganizations(
				stagingGroup.getGroupId());

		if (ListUtil.isEmpty(stagingOrganizations)) {
			return;
		}

		List<Organization> liveOrganizations =
			OrganizationLocalServiceUtil.getGroupOrganizations(
				stagingGroup.getLiveGroupId());

		for (Organization stagingGroupOrganization : stagingOrganizations) {
			if (liveOrganizations.contains(stagingGroupOrganization)) {
				continue;
			}

			OrganizationLocalServiceUtil.addGroupOrganization(
				stagingGroup.getLiveGroupId(), stagingGroupOrganization);
		}

		OrganizationLocalServiceUtil.clearGroupOrganizations(
			stagingGroup.getGroupId());
	}

	protected void verifyStagingGroupRoleMembership(Group stagingGroup) {
		List<Role> stagingRoles = RoleLocalServiceUtil.getGroupRoles(
			stagingGroup.getGroupId());

		if (ListUtil.isEmpty(stagingRoles)) {
			return;
		}

		List<Role> liveRoles = RoleLocalServiceUtil.getGroupRoles(
			stagingGroup.getLiveGroupId());

		for (Role stagingRole : stagingRoles) {
			if (liveRoles.contains(stagingRole)) {
				continue;
			}

			RoleLocalServiceUtil.addGroupRole(
				stagingGroup.getLiveGroupId(), stagingRole);
		}

		RoleLocalServiceUtil.clearGroupRoles(stagingGroup.getGroupId());
	}

	protected void verifyStagingGroupUserGroupMembership(Group stagingGroup) {
		List<UserGroup> stagingUserGroups =
			UserGroupLocalServiceUtil.getGroupUserGroups(
				stagingGroup.getGroupId());

		if (ListUtil.isEmpty(stagingUserGroups)) {
			return;
		}

		List<UserGroup> liveUserGroups =
			UserGroupLocalServiceUtil.getGroupUserGroups(
				stagingGroup.getLiveGroupId());

		for (UserGroup stagingUserGroup : stagingUserGroups) {
			if (liveUserGroups.contains(stagingUserGroup)) {
				continue;
			}

			UserGroupLocalServiceUtil.addGroupUserGroup(
				stagingGroup.getLiveGroupId(), stagingUserGroup);
		}

		UserGroupLocalServiceUtil.clearGroupUserGroups(
			stagingGroup.getGroupId());
	}

	protected void verifyStagingGroupUserMembership(Group stagingGroup) {
		List<User> stagingGroupUsers = UserLocalServiceUtil.getGroupUsers(
			stagingGroup.getGroupId());

		if (ListUtil.isEmpty(stagingGroupUsers)) {
			return;
		}

		List<User> liveGroupUsers = UserLocalServiceUtil.getGroupUsers(
			stagingGroup.getLiveGroupId());

		for (User stagingGroupUser : stagingGroupUsers) {
			if (liveGroupUsers.contains(stagingGroupUser)) {
				continue;
			}

			UserLocalServiceUtil.addGroupUser(
				stagingGroup.getLiveGroupId(), stagingGroupUser);
		}

		UserLocalServiceUtil.clearGroupUsers(stagingGroup.getGroupId());
	}

	protected void verifyStagingTypeSettingsProperties(
		UnicodeProperties typeSettingsUnicodeProperties) {

		Set<String> keys = typeSettingsUnicodeProperties.keySet();

		Iterator<String> iterator = keys.iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();

			if (!ArrayUtil.contains(
					_LEGACY_STAGED_PORTLET_TYPE_SETTINGS_KEYS, key)) {

				continue;
			}

			if (_log.isInfoEnabled()) {
				_log.info("Removing type settings property " + key);
			}

			iterator.remove();
		}
	}

	protected void verifyStagingUserGroupGroupRolesAssignments(
		Group stagingGroup) {

		DynamicQuery dynamicQuery =
			UserGroupGroupRoleLocalServiceUtil.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("groupId", stagingGroup.getGroupId()));

		List<UserGroupGroupRole> stagingUserGroupGroupRoles =
			UserGroupGroupRoleLocalServiceUtil.dynamicQuery(dynamicQuery);

		if (stagingUserGroupGroupRoles.isEmpty()) {
			return;
		}

		dynamicQuery = UserGroupGroupRoleLocalServiceUtil.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"groupId", stagingGroup.getLiveGroupId()));

		List<UserGroupGroupRole> liveUserGroupGroupRoles =
			UserGroupGroupRoleLocalServiceUtil.dynamicQuery(dynamicQuery);

		for (UserGroupGroupRole userGroupGroupRole :
				stagingUserGroupGroupRoles) {

			userGroupGroupRole.setGroupId(stagingGroup.getLiveGroupId());

			if (liveUserGroupGroupRoles.contains(userGroupGroupRole)) {
				continue;
			}

			UserGroupGroupRoleLocalServiceUtil.updateUserGroupGroupRole(
				userGroupGroupRole);
		}

		UserGroupGroupRoleLocalServiceUtil.deleteUserGroupGroupRolesByGroupId(
			stagingGroup.getGroupId());
	}

	protected void verifyStagingUserGroupRolesAssignments(Group stagingGroup) {
		List<UserGroupRole> stagingUserGroupRoles =
			UserGroupRoleLocalServiceUtil.getUserGroupRolesByGroup(
				stagingGroup.getGroupId());

		if (ListUtil.isEmpty(stagingUserGroupRoles)) {
			return;
		}

		List<UserGroupRole> liveUserGroupRoles =
			UserGroupRoleLocalServiceUtil.getUserGroupRolesByGroup(
				stagingGroup.getLiveGroupId());

		for (UserGroupRole stagingUserGroupRole : stagingUserGroupRoles) {
			stagingUserGroupRole.setGroupId(stagingGroup.getLiveGroupId());

			if (liveUserGroupRoles.contains(stagingUserGroupRole)) {
				continue;
			}

			UserGroupRoleLocalServiceUtil.updateUserGroupRole(
				stagingUserGroupRole);
		}

		UserGroupRoleLocalServiceUtil.deleteUserGroupRolesByGroupId(
			stagingGroup.getGroupId());
	}

	protected void verifyTree() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			CompanyLocalServiceUtil.forEachCompanyId(
				companyId -> GroupLocalServiceUtil.rebuildTree(companyId));
		}
	}

	private static final String[] _LEGACY_STAGED_PORTLET_TYPE_SETTINGS_KEYS = {
		"staged-portlet_39", "staged-portlet_54", "staged-portlet_56",
		"staged-portlet_59", "staged-portlet_107", "staged-portlet_108",
		"staged-portlet_110", "staged-portlet_166", "staged-portlet_169"
	};

	private static final Log _log = LogFactoryUtil.getLog(VerifyGroup.class);

}