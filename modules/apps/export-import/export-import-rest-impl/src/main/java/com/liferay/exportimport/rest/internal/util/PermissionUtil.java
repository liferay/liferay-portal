/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

/**
 * @author Petteri Karttunen
 */
public class PermissionUtil {

	public static void checkExportPermission(long companyId, long groupId)
		throws PortalException {

		_checkPermission(
			companyId, groupId, PortletKeys.COMPANY_EXPORT,
			ExportImportPortletKeys.EXPORT);
	}

	public static void checkImportPermission(long companyId, long groupId)
		throws PortalException {

		_checkPermission(
			companyId, groupId, PortletKeys.COMPANY_IMPORT,
			ExportImportPortletKeys.IMPORT);
	}

	public static void checkPublishPermission(long groupId)
		throws PortalException {

		GroupPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(), groupId,
			ActionKeys.PUBLISH_STAGING);
	}

	private static void _checkPermission(
			long companyId, long groupId, String companyPortletKey,
			String portletKey)
		throws PortalException {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.isGroupAdmin(groupId)) {
			return;
		}

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if (stagingGroupHelper.isCompanyGroup(companyId, groupId)) {
			PortletPermissionUtil.check(
				permissionChecker, groupId, companyPortletKey,
				ActionKeys.ACCESS_IN_CONTROL_PANEL);
		}
		else {
			PortletPermissionUtil.check(
				permissionChecker, groupId, portletKey,
				ActionKeys.ACCESS_IN_CONTROL_PANEL);
		}
	}

}