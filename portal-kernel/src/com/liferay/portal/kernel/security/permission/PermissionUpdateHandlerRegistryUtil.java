/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.permission;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gergely Mathe
 */
public class PermissionUpdateHandlerRegistryUtil {

	public static PermissionUpdateHandler getPermissionUpdateHandler(
		String modelClassName) {

		return _permissionUpdateHandlers.getService(modelClassName);
	}

	public static List<PermissionUpdateHandler> getPermissionUpdateHandlers() {
		return new ArrayList<>(_permissionUpdateHandlers.values());
	}

	private PermissionUpdateHandlerRegistryUtil() {
	}

	private static final ServiceTrackerMap<String, PermissionUpdateHandler>
		_permissionUpdateHandlers = ServiceTrackerMapFactory.openSingleValueMap(
			SystemBundleUtil.getBundleContext(), PermissionUpdateHandler.class,
			"model.class.name");

}