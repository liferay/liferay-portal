/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.set.permission;

import com.liferay.portal.kernel.security.permission.PermissionChecker;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Lourdes Fernández Besada
 */
@ProviderType
public interface LaunchSetPermission {

	public boolean contains(
		PermissionChecker permissionChecker, long launchSetId, String actionId);

}