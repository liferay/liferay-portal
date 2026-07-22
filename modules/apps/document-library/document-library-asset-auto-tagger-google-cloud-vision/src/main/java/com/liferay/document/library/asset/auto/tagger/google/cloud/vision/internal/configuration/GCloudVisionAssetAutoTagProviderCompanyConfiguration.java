/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.asset.auto.tagger.google.cloud.vision.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.document.library.asset.auto.tagger.google.cloud.vision.internal.constants.GCloudVisionAssetAutoTagProviderConstants;
import com.liferay.portal.configuration.metatype.annotations.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Alejandro Tardín
 */
@ExtendedObjectClassDefinition(
	category = "assets", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	description = "google-cloud-vision-asset-auto-tag-provider-description",
	id = "com.liferay.document.library.asset.auto.tagger.google.cloud.vision.internal.configuration.GCloudVisionAssetAutoTagProviderCompanyConfiguration",
	localization = "content/Language",
	name = "google-cloud-vision-asset-auto-tag-provider-configuration-name"
)
public interface GCloudVisionAssetAutoTagProviderCompanyConfiguration {

	/**
	 * Sets the API key for the G Cloud Vision API.
	 */
	@ExtendedAttributeDefinition(
		descriptionArguments = GCloudVisionAssetAutoTagProviderConstants.API_KEY_DOCS_URL,
		requiredInput = true
	)
	@Meta.AD(
		description = "set-the-api-key-for-the-google-cloud-vision-api",
		name = "api-key", required = false, type = Meta.Type.Password
	)
	public String apiKey();

	/**
	 * Enables auto tagging of images using the G Cloud Vision API.
	 */
	@Meta.AD(
		description = "enabled-description[google-cloud-vision]",
		name = "enabled", required = false
	)
	public boolean enabled();

}