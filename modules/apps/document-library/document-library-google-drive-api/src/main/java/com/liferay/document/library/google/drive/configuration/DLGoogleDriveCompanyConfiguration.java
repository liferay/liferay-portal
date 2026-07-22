/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.google.drive.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Adolfo Pérez
 */
@ExtendedObjectClassDefinition(
	category = "documents-and-media",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.document.library.google.drive.configuration.DLGoogleDriveCompanyConfiguration",
	localization = "content/Language", name = "google-drive-configuration-name"
)
public interface DLGoogleDriveCompanyConfiguration {

	/**
	 * Returns the client ID of the Google application to use when operating on
	 * Google Drive files. If <code>null</code> or empty, the Google Drive
	 * integration disables itself.
	 *
	 * @return the client ID
	 */
	@ExtendedAttributeDefinition(
		descriptionArguments = "https://console.developers.google.com/apis/credentials/oauthclient",
		requiredInput = true
	)
	@Meta.AD(
		description = "client-id-description", name = "client-id",
		required = false
	)
	public String clientId();

	/**
	 * Returns the client secret of the Google application to use when operating
	 * on Google Drive files. If <code>null</code> or empty, the Google Drive
	 * integration disables itself.
	 *
	 * @return the client secret
	 */
	@ExtendedAttributeDefinition(
		descriptionArguments = "https://console.developers.google.com/apis/credentials/oauthclient",
		requiredInput = true
	)
	@Meta.AD(
		description = "client-secret-description", name = "client-secret",
		required = false, type = Meta.Type.Password
	)
	public String clientSecret();

	/**
	 * Returns the API Key of the Google Drive Picker API If <code>null</code>
	 * or empty, the Google Drive Shortcut integration disables itself.
	 *
	 * @return the api key
	 */
	@ExtendedAttributeDefinition(
		descriptionArguments = "https://console.developers.google.com/flows/enableapi?apiid=picker&credential=client_key"
	)
	@Meta.AD(
		description = "picker-api-key-description", name = "picker-api-key",
		required = false, type = Meta.Type.Password
	)
	public String pickerAPIKey();

}