/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.defaultpermissions.resource;

import com.liferay.portal.kernel.defaultpermissions.resource.PortalDefaultPermissionsModelResource;

/**
 * @author Adolfo Pérez
 */
public class ObjectEntryPortalDefaultPermissionsModelResource
	implements PortalDefaultPermissionsModelResource {

	public ObjectEntryPortalDefaultPermissionsModelResource(
		String className, String label, String scope) {

		_className = className;
		_label = label;
		_scope = scope;
	}

	@Override
	public String getClassName() {
		return _className;
	}

	@Override
	public String getLabel() {
		return _label;
	}

	@Override
	public String getScope() {
		return _scope;
	}

	@Override
	public boolean isAllowOverridePermissions() {
		return true;
	}

	private final String _className;
	private final String _label;
	private final String _scope;

}