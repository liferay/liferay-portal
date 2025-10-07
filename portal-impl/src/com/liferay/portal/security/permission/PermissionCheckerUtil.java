/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.contributor.RoleContributor;

/**
 * @author Brian Wing Shun Chan
 */
public class PermissionCheckerUtil {

	public static void setThreadValues(User user) {
		if (user == null) {
			PrincipalThreadLocal.setName(null);
			PermissionThreadLocal.setPermissionChecker(null);

			return;
		}

		PrincipalThreadLocal.setName(String.valueOf(user.getUserId()));

		try {
			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			if (permissionChecker == null) {
				permissionChecker = new AdvancedPermissionChecker();
			}

			permissionChecker.init(
				user, _roleContributors.toArray(new RoleContributor[0]));

			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PermissionCheckerUtil.class);

	private static final ServiceTrackerList<RoleContributor> _roleContributors =
		ServiceTrackerListFactory.open(
			SystemBundleUtil.getBundleContext(), RoleContributor.class);

}