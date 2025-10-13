/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;

import java.util.Map;
import java.util.Objects;

/**
 * @author Tomas Polesovsky
 */
public class StagingPermissionChecker implements PermissionChecker {

	public StagingPermissionChecker(PermissionChecker permissionChecker) {
		_permissionChecker = permissionChecker;
	}

	@Override
	public PermissionChecker clone() {
		return new StagingPermissionChecker(_permissionChecker.clone());
	}

	@Override
	public long getCompanyId() {
		return _permissionChecker.getCompanyId();
	}

	@Override
	public long[] getGuestUserRoleIds() {
		return _permissionChecker.getGuestUserRoleIds();
	}

	@Override
	public long getOwnerRoleId() {
		return _permissionChecker.getOwnerRoleId();
	}

	@Override
	public Map<Object, Object> getPermissionChecksMap() {
		return _permissionChecker.getPermissionChecksMap();
	}

	@Override
	public long[] getRoleIds(long userId, long groupId) {
		return _permissionChecker.getRoleIds(
			userId, StagingUtil.getLiveGroupId(groupId));
	}

	@Override
	public User getUser() {
		return _permissionChecker.getUser();
	}

	@Override
	public UserBag getUserBag() throws Exception {
		return _permissionChecker.getUserBag();
	}

	@Override
	public long getUserId() {
		return _permissionChecker.getUserId();
	}

	@Override
	public boolean hasOwnerPermission(
		long companyId, String name, long primKey, long ownerId,
		String actionId) {

		return _permissionChecker.hasOwnerPermission(
			companyId, name, primKey, ownerId, actionId);
	}

	@Override
	public boolean hasOwnerPermission(
		long companyId, String name, String primKey, long ownerId,
		String actionId) {

		return _permissionChecker.hasOwnerPermission(
			companyId, name, primKey, ownerId, actionId);
	}

	@Override
	public boolean hasPermission(
		Group group, String name, long primKey, String actionId) {

		if (_isStagingFolder(name, actionId)) {
			return true;
		}

		Group liveGroup = StagingUtil.getLiveGroup(group);

		if ((liveGroup != null) &&
			(liveGroup.getGroupId() != group.getGroupId()) &&
			(primKey == group.getGroupId())) {

			primKey = liveGroup.getGroupId();
		}

		return _permissionChecker.hasPermission(
			liveGroup, name, primKey, actionId);
	}

	@Override
	public boolean hasPermission(
		Group group, String name, String primKey, String actionId) {

		if (_isStagingFolder(name, actionId)) {
			return true;
		}

		Group liveGroup = StagingUtil.getLiveGroup(group);

		if ((liveGroup != group) &&
			primKey.equals(String.valueOf(group.getGroupId()))) {

			primKey = String.valueOf(liveGroup.getGroupId());
		}

		return _permissionChecker.hasPermission(
			liveGroup, name, primKey, actionId);
	}

	@Override
	public boolean hasPermission(
		long groupId, String name, long primKey, String actionId) {

		return hasPermission(
			GroupLocalServiceUtil.fetchGroup(groupId), name, primKey, actionId);
	}

	@Override
	public boolean hasPermission(
		long groupId, String name, String primKey, String actionId) {

		return hasPermission(
			GroupLocalServiceUtil.fetchGroup(groupId), name, primKey, actionId);
	}

	@Override
	public void init(User user) {
		_permissionChecker.init(user);
	}

	@Override
	public boolean isCheckGuest() {
		return _permissionChecker.isCheckGuest();
	}

	@Override
	public boolean isCompanyAdmin() {
		return _permissionChecker.isCompanyAdmin();
	}

	@Override
	public boolean isCompanyAdmin(long companyId) {
		return _permissionChecker.isCompanyAdmin(companyId);
	}

	@Override
	public boolean isContentReviewer(long companyId, long groupId) {
		return _permissionChecker.isContentReviewer(
			companyId, StagingUtil.getLiveGroupId(groupId));
	}

	@Override
	public boolean isGroupAdmin(long groupId) {
		return _permissionChecker.isGroupAdmin(
			StagingUtil.getLiveGroupId(groupId));
	}

	@Override
	public boolean isGroupMember(long groupId) {
		return _permissionChecker.isGroupMember(
			StagingUtil.getLiveGroupId(groupId));
	}

	@Override
	public boolean isGroupOwner(long groupId) {
		return _permissionChecker.isGroupOwner(
			StagingUtil.getLiveGroupId(groupId));
	}

	@Override
	public boolean isOmniadmin() {
		return _permissionChecker.isOmniadmin();
	}

	@Override
	public boolean isOrganizationAdmin(long organizationId) {
		return _permissionChecker.isOrganizationAdmin(organizationId);
	}

	@Override
	public boolean isOrganizationOwner(long organizationId) {
		return _permissionChecker.isOrganizationOwner(organizationId);
	}

	@Override
	public boolean isSignedIn() {
		return _permissionChecker.isSignedIn();
	}

	private boolean _isStagingFolder(String name, String actionId) {
		if (ExportImportThreadLocal.isStagingInProcessOnRemoteLive() &&
			actionId.equals("VIEW") &&
			(name.equals(Folder.class.getName()) ||
			 name.equals(DLFolder.class.getName()) ||
			 Objects.equals(name, "com.liferay.document.library"))) {

			return true;
		}

		return false;
	}

	private final PermissionChecker _permissionChecker;

}