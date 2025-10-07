/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.security.permission;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Crescenzo Rega
 * @generated
 */
public class LiberalPermissionChecker implements PermissionChecker {

	public LiberalPermissionChecker(User user) {
		init(user);
	}

	@Override
	public PermissionChecker clone() {
		return this;
	}

	@Override
	public long getCompanyId() {
		return _user.getCompanyId();
	}

	@Override
	public long[] getGuestUserRoleIds() {
		return PermissionChecker.DEFAULT_ROLE_IDS;
	}

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	public List<Long> getOwnerResourceBlockIds(
		long companyId, long groupId, String name, String actionId) {

		return new ArrayList<>();
	}

	@Override
	public long getOwnerRoleId() {
		return _ownerRole.getRoleId();
	}

	@Override
	public Map<Object, Object> getPermissionChecksMap() {
		return new HashMap<>();
	}

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	public List<Long> getResourceBlockIds(
		long companyId, long groupId, long userId, String name,
		String actionId) {

		return new ArrayList<>();
	}

	@Override
	public long[] getRoleIds(long userId, long groupId) {
		return PermissionChecker.DEFAULT_ROLE_IDS;
	}

	@Override
	public User getUser() {
		return _user;
	}

	@Override
	public UserBag getUserBag() {
		return null;
	}

	@Override
	public long getUserId() {
		return _user.getUserId();
	}

	@Override
	public boolean hasOwnerPermission(
		long companyId, String name, long primKey, long ownerId,
		String actionId) {

		return true;
	}

	@Override
	public boolean hasOwnerPermission(
		long companyId, String name, String primKey, long ownerId,
		String actionId) {

		return true;
	}

	@Override
	public boolean hasPermission(
		Group group, String name, long primKey, String actionId) {

		return true;
	}

	@Override
	public boolean hasPermission(
		Group group, String name, String primKey, String actionId) {

		return true;
	}

	@Override
	public boolean hasPermission(
		long groupId, String name, long primKey, String actionId) {

		return true;
	}

	@Override
	public boolean hasPermission(
		long groupId, String name, String primKey, String actionId) {

		return true;
	}

	@Override
	public void init(User user) {
		_user = user;

		try {
			_ownerRole = RoleLocalServiceUtil.getRole(
				user.getCompanyId(), RoleConstants.OWNER);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	public boolean isCheckGuest() {
		return GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.PERMISSIONS_CHECK_GUEST_ENABLED));
	}

	@Override
	public boolean isCompanyAdmin() {
		return true;
	}

	@Override
	public boolean isCompanyAdmin(long companyId) {
		return true;
	}

	@Override
	public boolean isContentReviewer(long companyId, long groupId) {
		return true;
	}

	@Override
	public boolean isGroupAdmin(long groupId) {
		return true;
	}

	@Override
	public boolean isGroupMember(long groupId) {
		return true;
	}

	@Override
	public boolean isGroupOwner(long groupId) {
		return true;
	}

	@Override
	public boolean isOmniadmin() {
		return true;
	}

	@Override
	public boolean isOrganizationAdmin(long organizationId) {
		return true;
	}

	@Override
	public boolean isOrganizationOwner(long organizationId) {
		return true;
	}

	@Override
	public boolean isSignedIn() {
		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiberalPermissionChecker.class);

	private Role _ownerRole;
	private User _user;

}