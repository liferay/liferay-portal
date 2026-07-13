/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.permission;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.Map;
import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 */
public class GroupPermissionUtil {

	public static void check(
			PermissionChecker permissionChecker, Group group, String actionId)
		throws PortalException {

		if (!contains(permissionChecker, group, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, Group.class.getName(), group.getGroupId(),
				actionId);
		}
	}

	public static void check(
			PermissionChecker permissionChecker, long groupId, String actionId)
		throws PortalException {

		if (!contains(permissionChecker, groupId, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, Group.class.getName(), groupId, actionId);
		}
	}

	public static void check(
			PermissionChecker permissionChecker, String actionId)
		throws PortalException {

		if (!contains(permissionChecker, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, Group.class.getName(), Long.valueOf(0),
				actionId);
		}
	}

	public static boolean contains(
			PermissionChecker permissionChecker, Group group, String actionId)
		throws PortalException {

		Map<Object, Object> permissionChecksMap =
			permissionChecker.getPermissionChecksMap();

		CacheKey cacheKey = new CacheKey(
			group.getGroupId(), group.getMvccVersion(), actionId);

		Boolean contains = (Boolean)permissionChecksMap.get(cacheKey);

		if (contains == null) {
			contains = _contains(permissionChecker, group, actionId);

			permissionChecksMap.put(cacheKey, contains);
		}

		return contains;
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long groupId, String actionId)
		throws PortalException {

		if (groupId > 0) {
			return contains(
				permissionChecker, GroupLocalServiceUtil.getGroup(groupId),
				actionId);
		}

		return false;
	}

	public static boolean contains(
		PermissionChecker permissionChecker, String actionId) {

		return permissionChecker.hasPermission(
			null, Group.class.getName(), Group.class.getName(), actionId);
	}

	private static boolean _contains(
			PermissionChecker permissionChecker, Group group, String actionId)
		throws PortalException {

		if ((actionId.equals(ActionKeys.ADD_LAYOUT) ||
			 actionId.equals(ActionKeys.MANAGE_LAYOUTS)) &&
			((group.hasLocalOrRemoteStagingGroup() &&
			  _STAGING_LIVE_GROUP_LOCKING_ENABLED &&
			  !ExportImportThreadLocal.isInitialLayoutStagingInProcess() &&
			  !ExportImportThreadLocal.isStagingInProcess()) ||
			 group.isLayoutPrototype())) {

			return false;
		}

		Group originalGroup = group;

		long groupId = group.getGroupId();

		if (group.isStagingGroup()) {
			group = group.getLiveGroup();
		}

		if (group.isUser()) {

			// An individual user would never reach this block because he would
			// be an administrator of his own layouts. However, a user who
			// manages a set of organizations may be modifying pages of a user
			// he manages.

			User user = UserLocalServiceUtil.getUserById(group.getClassPK());

			if ((permissionChecker.getUserId() != user.getUserId()) &&
				UserPermissionUtil.contains(
					permissionChecker, user.getUserId(),
					user.getOrganizationIds(), ActionKeys.UPDATE)) {

				return true;
			}
		}

		if (actionId.equals(ActionKeys.ADD_COMMUNITY) &&
			(permissionChecker.hasPermission(
				originalGroup, Group.class.getName(), groupId,
				ActionKeys.MANAGE_SUBGROUPS) ||
			 PortalPermissionUtil.contains(
				 permissionChecker, ActionKeys.ADD_COMMUNITY))) {

			return true;
		}
		else if (actionId.equals(ActionKeys.ADD_LAYOUT) &&
				 permissionChecker.hasPermission(
					 originalGroup, Group.class.getName(), groupId,
					 ActionKeys.MANAGE_LAYOUTS)) {

			return true;
		}
		else if ((actionId.equals(ActionKeys.EXPORT_IMPORT_LAYOUTS) ||
				  actionId.equals(ActionKeys.EXPORT_IMPORT_PORTLET_INFO) ||
				  actionId.equals(ActionKeys.PUBLISH_PORTLET_INFO)) &&
				 permissionChecker.hasPermission(
					 originalGroup, Group.class.getName(), groupId,
					 ActionKeys.PUBLISH_STAGING)) {

			return true;
		}
		else if (actionId.equals(ActionKeys.EXPORT_IMPORT_LAYOUTS) &&
				 group.isCompany() &&
				 (PortletPermissionUtil.contains(
					 permissionChecker, PortletKeys.COMPANY_EXPORT,
					 ActionKeys.ACCESS_IN_CONTROL_PANEL) ||
				  PortletPermissionUtil.contains(
					  permissionChecker, PortletKeys.COMPANY_IMPORT,
					  ActionKeys.ACCESS_IN_CONTROL_PANEL))) {

			return true;
		}
		else if (actionId.equals(ActionKeys.VIEW) &&
				 ((originalGroup.getType() == GroupConstants.TYPE_SITE_OPEN) ||
				  permissionChecker.hasPermission(
					  originalGroup, Group.class.getName(), groupId,
					  ActionKeys.ASSIGN_USER_ROLES) ||
				  permissionChecker.hasPermission(
					  originalGroup, Group.class.getName(), groupId,
					  ActionKeys.MANAGE_LAYOUTS) ||
				  permissionChecker.isGroupMember(
					  originalGroup.getGroupId()))) {

			return true;
		}
		else if (actionId.equals(ActionKeys.VIEW_STAGING) &&
				 (permissionChecker.hasPermission(
					 originalGroup, Group.class.getName(), groupId,
					 ActionKeys.MANAGE_LAYOUTS) ||
				  permissionChecker.hasPermission(
					  originalGroup, Group.class.getName(), groupId,
					  ActionKeys.MANAGE_STAGING) ||
				  permissionChecker.hasPermission(
					  originalGroup, Group.class.getName(), groupId,
					  ActionKeys.PUBLISH_STAGING) ||
				  permissionChecker.hasPermission(
					  originalGroup, Group.class.getName(), groupId,
					  ActionKeys.UPDATE))) {

			return true;
		}

		// Group id must be set so that users can modify their personal pages

		return permissionChecker.hasPermission(
			originalGroup, Group.class.getName(), groupId, actionId);
	}

	private static final boolean _STAGING_LIVE_GROUP_LOCKING_ENABLED =
		GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.STAGING_LIVE_GROUP_LOCKING_ENABLED));

	private static class CacheKey {

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}

			if (!(object instanceof CacheKey)) {
				return false;
			}

			CacheKey cacheKey = (CacheKey)object;

			if ((_groupId == cacheKey._groupId) &&
				(_mvccVersion == cacheKey._mvccVersion) &&
				Objects.equals(_actionId, cacheKey._actionId)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hash = HashUtil.hash(0, _groupId);

			hash = HashUtil.hash(hash, _mvccVersion);

			return HashUtil.hash(hash, _actionId);
		}

		private CacheKey(long groupId, long mvccVersion, String actionId) {
			_groupId = groupId;
			_mvccVersion = mvccVersion;
			_actionId = actionId;
		}

		private final String _actionId;
		private final long _groupId;
		private final long _mvccVersion;

	}

}