/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.set.permission;

import com.liferay.launch.internal.set.util.LaunchSetUtil;
import com.liferay.launch.set.permission.LaunchSetPermission;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = LaunchSetPermission.class)
public class LaunchSetPermissionImpl implements LaunchSetPermission {

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, long launchSetId,
		String actionId) {

		ObjectEntry objectEntry = LaunchSetUtil.fetchLaunchSetObjectEntry(
			launchSetId);

		if (objectEntry == null) {
			return false;
		}

		try {
			ModelResourcePermission<ObjectEntry> modelResourcePermission =
				_objectEntryService.getModelResourcePermission(
					objectEntry.getObjectDefinitionId());

			return modelResourcePermission.contains(
				permissionChecker, launchSetId, actionId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LaunchSetPermissionImpl.class);

	@Reference
	private ObjectEntryService _objectEntryService;

}