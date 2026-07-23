/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.info.permission.provider;

import com.liferay.depot.model.DepotEntry;
import com.liferay.info.permission.provider.InfoPermissionProvider;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(service = InfoPermissionProvider.class)
public class DepotEntryInfoPermissionProvider
	implements InfoPermissionProvider<DepotEntry> {

	@Override
	public boolean hasAddPermission(
		long groupId, PermissionChecker permissionChecker) {

		return true;
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker) {
		return true;
	}

}