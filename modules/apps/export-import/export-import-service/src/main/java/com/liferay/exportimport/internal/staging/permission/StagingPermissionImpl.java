/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.staging.permission;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.staging.permission.StagingPermission;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(service = StagingPermission.class)
public class StagingPermissionImpl implements StagingPermission {

	@Override
	public Boolean hasPermission(
		PermissionChecker permissionChecker, Group group, String className,
		long classPK, String portletId, String actionId) {

		if (!_isLockedAction(actionId)) {
			return null;
		}

		return _hasPermission(group, portletId);
	}

	@Override
	public Boolean hasPermission(
		PermissionChecker permissionChecker, long groupId, String className,
		long classPK, String portletId, String actionId) {

		if (!_isLockedAction(actionId)) {
			return null;
		}

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return null;
		}

		return _hasPermission(group, portletId);
	}

	private Boolean _hasPermission(Group group, String portletId) {
		if (group.hasLocalOrRemoteStagingGroup() &&
			(Validator.isNull(portletId) || group.isStagedPortlet(portletId))) {

			return false;
		}

		return null;
	}

	private boolean _isLockedAction(String actionId) {
		if (!PropsValues.STAGING_LIVE_GROUP_LOCKING_ENABLED ||
			ExportImportThreadLocal.isExportInProcess() ||
			ExportImportThreadLocal.isImportInProcess() ||
			ExportImportThreadLocal.isInitialLayoutStagingInProcess() ||
			ExportImportThreadLocal.isStagingInProcess()) {

			return false;
		}

		if (!actionId.equals(ActionKeys.ACCESS) &&
			!actionId.equals(ActionKeys.ACCESS_IN_CONTROL_PANEL) &&
			!actionId.equals(ActionKeys.ADD_DISCUSSION) &&
			!actionId.equals(ActionKeys.ADD_TO_PAGE) &&
			!actionId.equals(ActionKeys.ASSIGN_MEMBERS) &&
			!actionId.equals(ActionKeys.CONFIGURATION) &&
			!actionId.equals(ActionKeys.CUSTOMIZE) &&
			!actionId.equals(ActionKeys.DELETE) &&
			!actionId.equals(ActionKeys.DELETE_DISCUSSION) &&
			!actionId.equals(ActionKeys.DOWNLOAD) &&
			!actionId.equals(ActionKeys.UPDATE_DISCUSSION) &&
			!actionId.equals(ActionKeys.VIEW)) {

			return true;
		}

		return false;
	}

	@Reference
	private GroupLocalService _groupLocalService;

}