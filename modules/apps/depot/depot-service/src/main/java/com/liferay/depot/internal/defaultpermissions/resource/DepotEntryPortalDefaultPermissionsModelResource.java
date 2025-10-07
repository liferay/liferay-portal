/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.defaultpermissions.resource;

import com.liferay.depot.model.DepotEntry;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.defaultpermissions.resource.PortalDefaultPermissionsModelResource;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "portal.default.permissions.model.resource.key=com.liferay.depot.model.DepotEntry",
	service = PortalDefaultPermissionsModelResource.class
)
public class DepotEntryPortalDefaultPermissionsModelResource
	implements PortalDefaultPermissionsModelResource {

	@Override
	public String getClassName() {
		return DepotEntry.class.getName();
	}

	@Override
	public String getLabel() {
		return "asset-library";
	}

	@Override
	public String getScope() {
		return ExtendedObjectClassDefinition.Scope.GROUP.toString();
	}

	@Override
	public boolean isAllowOverridePermissions() {
		return true;
	}

}