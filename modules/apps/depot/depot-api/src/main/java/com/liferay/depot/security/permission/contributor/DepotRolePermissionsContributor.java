/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.security.permission.contributor;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Thiago Buarque
 */
@ProviderType
public interface DepotRolePermissionsContributor {

	public List<DepotRolePermission> getDepotRolePermissions();

}