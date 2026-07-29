/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.security.permission.contributor;

/**
 * @author Thiago Buarque
 */
public class DepotRolePermission {

	public DepotRolePermission(
		String roleName, String resourceName, String... actionKeys) {

		_roleName = roleName;
		_resourceName = resourceName;
		_actionKeys = actionKeys;
	}

	public String[] getActionKeys() {
		return _actionKeys;
	}

	public String getResourceName() {
		return _resourceName;
	}

	public String getRoleName() {
		return _roleName;
	}

	private final String[] _actionKeys;
	private final String _resourceName;
	private final String _roleName;

}