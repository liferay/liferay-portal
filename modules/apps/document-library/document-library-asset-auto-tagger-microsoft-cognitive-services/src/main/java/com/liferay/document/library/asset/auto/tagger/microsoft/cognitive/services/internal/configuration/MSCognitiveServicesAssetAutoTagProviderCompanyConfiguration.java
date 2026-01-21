/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.asset.auto.tagger.microsoft.cognitive.services.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.document.library.asset.auto.tagger.microsoft.cognitive.services.internal.constants.MSCognitiveServicesAssetAutoTagProviderConstants;
import com.liferay.portal.configuration.metatype.annotations.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Alejandro Tardín
 */
@ExtendedObjectClassDefinition(
	category = "assets", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	description = "microsoft-cognitive-services-asset-auto-tag-provider-description",
	id = "com.liferay.document.library.asset.auto.tagger.microsoft.cognitive.services.internal.configuration.MSCognitiveServicesAssetAutoTagProviderCompanyConfiguration",
	localization = "content/Language",
	name = "microsoft-cognitive-services-asset-auto-tag-provider-configuration-name"
)
public interface MSCognitiveServicesAssetAutoTagProviderCompanyConfiguration {

	/**
	 * Sets the Computer Vision API V2 endpoint.
	 */
	@ExtendedAttributeDefinition(
		descriptionArguments = {
			MSCognitiveServicesAssetAutoTagProviderConstants.
				SAMPLE_API_ENDPOINT,
			MSCognitiveServicesAssetAutoTagProviderConstants.API_KEY_DOCS_URL
		},
		requiredInput = true
	)
	@Meta.AD(
		description = "api-endpoint-description", name = "api-endpoint",
		required = false
	)
	public String apiEndpoint();

	/**
	 * Sets the API Key for the Computer Vision API V2.
	 */
	@ExtendedAttributeDefinition(
		descriptionArguments = MSCognitiveServicesAssetAutoTagProviderConstants.API_KEY_DOCS_URL,
		requiredInput = true
	)
	@Meta.AD(
		description = "set-the-api-key-for-the-computer-vision-api-v2",
		name = "api-key", required = false
	)
	public String apiKey();

	/**
	 * Enables auto tagging of images using the Microsoft Cognitive Services
	 * API.
	 */
	@Meta.AD(
		description = "enabled-description[microsoft-cognitive-services]",
		name = "enabled", required = false
	)
	public boolean enabled();

}