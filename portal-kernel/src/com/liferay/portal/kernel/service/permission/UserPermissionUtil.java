/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.permission;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Arrays;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class UserPermissionUtil {

	public static void check(
			PermissionChecker permissionChecker, long userId,
			long[] organizationIds, String actionId)
		throws PrincipalException {

		if (!contains(permissionChecker, userId, organizationIds, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, User.class.getName(), userId, actionId);
		}
	}

	public static void check(
			PermissionChecker permissionChecker, long userId, String actionId)
		throws PrincipalException {

		if (!contains(permissionChecker, userId, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, User.class.getName(), userId, actionId);
		}
	}

	public static boolean contains(
		PermissionChecker permissionChecker, long userId,
		long[] organizationIds, String actionId) {

		try {
			User user = null;

			if (userId != ResourceConstants.PRIMKEY_DNE) {
				if (permissionChecker.isOmniadmin()) {
					return true;
				}

				user = UserLocalServiceUtil.getUserById(userId);

				if (!actionId.equals(ActionKeys.VIEW) &&
					!permissionChecker.isOmniadmin() &&
					(PortalUtil.isOmniadmin(user) ||
					 (!permissionChecker.isCompanyAdmin() &&
					  PortalUtil.isCompanyAdmin(user)))) {

					return false;
				}

				Contact contact = user.getContact();

				if (permissionChecker.hasOwnerPermission(
						permissionChecker.getCompanyId(), User.class.getName(),
						userId, contact.getUserId(), actionId) ||
					((permissionChecker.getUserId() == userId) &&
					 !actionId.equals(ActionKeys.PERMISSIONS)) ||
					permissionChecker.hasPermission(
						null, User.class.getName(), userId, actionId)) {

					return true;
				}
			}
			else {
				if (permissionChecker.hasPermission(
						null, User.class.getName(), User.class.getName(),
						actionId)) {

					return true;
				}
			}

			if (user == null) {
				return false;
			}

			if (organizationIds == null) {
				organizationIds = user.getOrganizationIds();
			}

			if (!Arrays.equals(organizationIds, user.getOrganizationIds())) {
				List<Long> organizationIdsList = ListUtil.fromArray(
					ArrayUtil.append(
						organizationIds, user.getOrganizationIds()));

				ListUtil.distinct(organizationIdsList);

				organizationIds = ArrayUtil.toLongArray(organizationIdsList);
			}

			for (long organizationId : organizationIds) {
				Organization organization =
					OrganizationLocalServiceUtil.getOrganization(
						organizationId);

				if (!OrganizationPermissionUtil.contains(
						permissionChecker, organization,
						ActionKeys.MANAGE_USERS)) {

					continue;
				}

				if (permissionChecker.getUserId() == user.getUserId()) {
					return true;
				}

				// Organization administrators and those with "Manage
				// Users" permission can only manage normal users

				if (!UserGroupRoleLocalServiceUtil.hasUserGroupRole(
						user.getUserId(), organization.getGroupId(),
						RoleConstants.ORGANIZATION_ADMINISTRATOR, true) &&
					!UserGroupRoleLocalServiceUtil.hasUserGroupRole(
						user.getUserId(), organization.getGroupId(),
						RoleConstants.ORGANIZATION_OWNER, true)) {

					return true;
				}

				Organization curOrganization = organization;

				while (curOrganization != null) {

					// Organization owners can manage all users

					if (UserGroupRoleLocalServiceUtil.hasUserGroupRole(
							permissionChecker.getUserId(),
							curOrganization.getGroupId(),
							RoleConstants.ORGANIZATION_OWNER, true)) {

						return true;
					}

					curOrganization = curOrganization.getParentOrganization();
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	public static boolean contains(
		PermissionChecker permissionChecker, long userId, String actionId) {

		return contains(permissionChecker, userId, null, actionId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserPermissionUtil.class);

}