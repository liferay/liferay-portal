/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.onedrive.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Cristina González
 */
@ExtendedObjectClassDefinition(
	category = "documents-and-media",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.document.library.opener.onedrive.web.internal.configuration.DLOneDriveCompanyConfiguration",
	localization = "content/Language", name = "onedrive-configuration-name"
)
public interface DLOneDriveCompanyConfiguration {

	/**
	 * Returns the client ID of the OneDrive application to use when operating
	 * on OneDrive files. If <code>null</code> or empty, the OneDrive
	 * integration disables itself.
	 *
	 * @return the client ID
	 * @review
	 */
	@ExtendedAttributeDefinition(
		descriptionArguments = "https://docs.microsoft.com/graph/auth-register-app-v2",
		requiredInput = true
	)
	@Meta.AD(
		description = "onedrive-client-id-description", name = "client-id",
		required = false
	)
	public String clientId();

	/**
	 * Returns the client secret of the OneDrive application to use when
	 * operating on OneDrive files. If <code>null</code> or empty, the One Drive
	 * integration disables itself.
	 *
	 * @return the client secret
	 * @review
	 */
	@ExtendedAttributeDefinition(
		descriptionArguments = "https://docs.microsoft.com/graph/auth-register-app-v2",
		requiredInput = true
	)
	@Meta.AD(
		description = "onedrive-client-secret-description",
		name = "client-secret", required = false, type = Meta.Type.Password
	)
	public String clientSecret();

	/**
	 * Returns the tenant of the OneDrive application to use when operating on
	 * OneDrive files. If <code>null</code> or empty, the One Drive integration
	 * disables itself.
	 *
	 * @return the tenant
	 * @review
	 */
	@ExtendedAttributeDefinition(
		descriptionArguments = "https://docs.microsoft.com/onedrive/find-your-office-365-tenant-id",
		requiredInput = true
	)
	@Meta.AD(
		description = "onedrive-tenant-description", name = "onedrive-tenant",
		required = false
	)
	public String tenant();

}