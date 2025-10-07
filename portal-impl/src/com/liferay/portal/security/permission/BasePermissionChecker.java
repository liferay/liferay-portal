/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portlet.admin.util.OmniadminUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 */
public abstract class BasePermissionChecker implements PermissionChecker {

	@Override
	public abstract PermissionChecker clone();

	@Override
	public long getCompanyId() {
		return user.getCompanyId();
	}

	@Override
	public long[] getGuestUserRoleIds() {
		return PermissionChecker.DEFAULT_ROLE_IDS;
	}

	@Override
	public long getOwnerRoleId() {
		return ownerRole.getRoleId();
	}

	@Override
	public Map<Object, Object> getPermissionChecksMap() {
		return _permissionChecksMap;
	}

	@Override
	public long[] getRoleIds(long userId, long groupId) {
		return PermissionChecker.DEFAULT_ROLE_IDS;
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public long getUserId() {
		return user.getUserId();
	}

	@Override
	public boolean hasOwnerPermission(
		long companyId, String name, long primKey, long ownerId,
		String actionId) {

		return hasOwnerPermission(
			companyId, name, String.valueOf(primKey), ownerId, actionId);
	}

	@Override
	public boolean hasPermission(
		Group group, String name, long primKey, String actionId) {

		return hasPermission(group, name, String.valueOf(primKey), actionId);
	}

	@Override
	public boolean hasPermission(
		long groupId, String name, long primKey, String actionId) {

		return hasPermission(
			GroupLocalServiceUtil.fetchGroup(groupId), name,
			String.valueOf(primKey), actionId);
	}

	@Override
	public boolean hasPermission(
		long groupId, String name, String primKey, String actionId) {

		return hasPermission(
			GroupLocalServiceUtil.fetchGroup(groupId), name, primKey, actionId);
	}

	@Override
	public void init(User user) {
		this.user = user;

		if (user.isGuestUser()) {
			guestUserId = user.getUserId();
			signedIn = false;
		}
		else {
			try {
				guestUserId = UserLocalServiceUtil.getGuestUserId(
					user.getCompanyId());
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			signedIn = true;
		}

		try {
			ownerRole = RoleLocalServiceUtil.getRole(
				user.getCompanyId(), RoleConstants.OWNER);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	public boolean isCheckGuest() {
		return checkGuest;
	}

	@Override
	public boolean isOmniadmin() {
		if (omniadmin == null) {
			omniadmin = Boolean.valueOf(OmniadminUtil.isOmniadmin(getUser()));
		}

		return omniadmin.booleanValue();
	}

	@Override
	public boolean isSignedIn() {
		return signedIn;
	}

	protected boolean checkGuest = PropsValues.PERMISSIONS_CHECK_GUEST_ENABLED;
	protected long guestUserId;
	protected Boolean omniadmin;
	protected Role ownerRole;
	protected boolean signedIn;
	protected User user;

	private static final Log _log = LogFactoryUtil.getLog(
		BasePermissionChecker.class);

	private final Map<Object, Object> _permissionChecksMap =
		new ConcurrentHashMap<>();

}