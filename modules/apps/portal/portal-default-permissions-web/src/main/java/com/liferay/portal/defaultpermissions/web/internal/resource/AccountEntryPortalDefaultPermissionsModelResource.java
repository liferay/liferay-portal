/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.defaultpermissions.web.internal.resource;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.defaultpermissions.kernel.resource.PortalDefaultPermissionsModelResource;
import org.osgi.service.component.annotations.Component;

/**
 * @author Stefano Motta
 */
@Component(
	property = "portal.default.permissions.model.resource.key=" + AccountEntryPortalDefaultPermissionsModelResource.MODEL_RESOURCE_KEY,
	service = PortalDefaultPermissionsModelResource.class
)
public class AccountEntryPortalDefaultPermissionsModelResource
	implements PortalDefaultPermissionsModelResource {

	public static final String MODEL_RESOURCE_KEY =
		"com.liferay.account.model.AccountEntry";

	@Override
	public String getClassName() {
		return MODEL_RESOURCE_KEY;
	}

	@Override
	public String getLabel() {
		return "account";
	}

	@Override
	public String getScope() {
		return ExtendedObjectClassDefinition.Scope.COMPANY.toString();
	}

	@Override
	public boolean isAllowOverridePermissions() {
		return true;
	}

}