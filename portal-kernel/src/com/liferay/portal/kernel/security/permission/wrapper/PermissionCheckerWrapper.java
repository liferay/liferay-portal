/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.permission.wrapper;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.UserBag;

import java.util.Map;

/**
 * @author Preston Crary
 */
public class PermissionCheckerWrapper implements PermissionChecker {

	public PermissionCheckerWrapper(PermissionChecker permissionChecker) {
		this.permissionChecker = permissionChecker;
	}

	@Override
	public final PermissionChecker clone() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getCompanyId() {
		return permissionChecker.getCompanyId();
	}

	@Override
	public long[] getGuestUserRoleIds() {
		return permissionChecker.getGuestUserRoleIds();
	}

	@Override
	public long getOwnerRoleId() {
		return permissionChecker.getOwnerRoleId();
	}

	@Override
	public Map<Object, Object> getPermissionChecksMap() {
		return permissionChecker.getPermissionChecksMap();
	}

	@Override
	public long[] getRoleIds(long userId, long groupId) {
		return permissionChecker.getRoleIds(userId, groupId);
	}

	@Override
	public User getUser() {
		return permissionChecker.getUser();
	}

	@Override
	public UserBag getUserBag() throws Exception {
		return permissionChecker.getUserBag();
	}

	@Override
	public long getUserId() {
		return permissionChecker.getUserId();
	}

	@Override
	public boolean hasOwnerPermission(
		long companyId, String name, long primKey, long ownerId,
		String actionId) {

		return permissionChecker.hasOwnerPermission(
			companyId, name, primKey, ownerId, actionId);
	}

	@Override
	public boolean hasOwnerPermission(
		long companyId, String name, String primKey, long ownerId,
		String actionId) {

		return permissionChecker.hasOwnerPermission(
			companyId, name, primKey, ownerId, actionId);
	}

	@Override
	public boolean hasPermission(
		Group group, String name, long primKey, String actionId) {

		return permissionChecker.hasPermission(group, name, primKey, actionId);
	}

	@Override
	public boolean hasPermission(
		Group group, String name, String primKey, String actionId) {

		return permissionChecker.hasPermission(group, name, primKey, actionId);
	}

	@Override
	public boolean hasPermission(
		long groupId, String name, long primKey, String actionId) {

		return permissionChecker.hasPermission(
			groupId, name, primKey, actionId);
	}

	@Override
	public boolean hasPermission(
		long groupId, String name, String primKey, String actionId) {

		return permissionChecker.hasPermission(
			groupId, name, primKey, actionId);
	}

	@Override
	public final void init(User user) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCheckGuest() {
		return permissionChecker.isCheckGuest();
	}

	@Override
	public boolean isCompanyAdmin() {
		return permissionChecker.isCompanyAdmin();
	}

	@Override
	public boolean isCompanyAdmin(long companyId) {
		return permissionChecker.isCompanyAdmin(companyId);
	}

	@Override
	public boolean isContentReviewer(long companyId, long groupId) {
		return permissionChecker.isContentReviewer(companyId, groupId);
	}

	@Override
	public boolean isGroupAdmin(long groupId) {
		return permissionChecker.isGroupAdmin(groupId);
	}

	@Override
	public boolean isGroupMember(long groupId) {
		return permissionChecker.isGroupMember(groupId);
	}

	@Override
	public boolean isGroupOwner(long groupId) {
		return permissionChecker.isGroupOwner(groupId);
	}

	@Override
	public boolean isOmniadmin() {
		return permissionChecker.isOmniadmin();
	}

	@Override
	public boolean isOrganizationAdmin(long organizationId) {
		return permissionChecker.isOrganizationAdmin(organizationId);
	}

	@Override
	public boolean isOrganizationOwner(long organizationId) {
		return permissionChecker.isOrganizationOwner(organizationId);
	}

	@Override
	public boolean isSignedIn() {
		return permissionChecker.isSignedIn();
	}

	protected final PermissionChecker permissionChecker;

}