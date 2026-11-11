/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.security.permission.resource;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

/**
 * @author Pei-Jung Lan
 */
public class AccountEntryPermission {

	public static boolean contains(
		PermissionChecker permissionChecker, AccountEntry accountEntry,
		String actionId) {

		try {
			ModelResourcePermission<AccountEntry> modelResourcePermission =
				_accountEntryModelResourcePermissionSnapshot.get();

			return modelResourcePermission.contains(
				permissionChecker, accountEntry, actionId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	public static boolean contains(
		PermissionChecker permissionChecker, long accountEntryId,
		String actionId) {

		try {
			ModelResourcePermission<AccountEntry> modelResourcePermission =
				_accountEntryModelResourcePermissionSnapshot.get();

			return modelResourcePermission.contains(
				permissionChecker, accountEntryId, actionId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	public static boolean hasEditOrManageOrganizationsPermission(
		PermissionChecker permissionChecker, long accountEntryId) {

		if (contains(
				permissionChecker, accountEntryId,
				AccountActionKeys.MANAGE_ORGANIZATIONS) ||
			contains(
				permissionChecker, accountEntryId,
				AccountActionKeys.UPDATE_ORGANIZATIONS)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountEntryPermission.class);

	private static final Snapshot<ModelResourcePermission<AccountEntry>>
		_accountEntryModelResourcePermissionSnapshot = new Snapshot<>(
			AccountEntryPermission.class,
			Snapshot.cast(ModelResourcePermission.class),
			"(model.class.name=com.liferay.account.model.AccountEntry)", true);

}