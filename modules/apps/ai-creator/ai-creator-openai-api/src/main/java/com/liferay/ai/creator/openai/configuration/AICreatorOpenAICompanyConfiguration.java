/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Lourdes Fernández Besada
 */
@ExtendedObjectClassDefinition(
	category = "ai-creator", generateUI = false,
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.ai.creator.openai.configuration.AICreatorOpenAICompanyConfiguration",
	localization = "content/Language",
	name = "ai-creator-openai-company-configuration-name"
)
public interface AICreatorOpenAICompanyConfiguration {

	@Meta.AD(
		deflt = "", name = "api-key", required = false,
		type = Meta.Type.Password
	)
	public String apiKey();

	@Meta.AD(
		deflt = "true", name = "enable-chatgpt-to-create-content",
		required = false
	)
	public boolean enableChatGPTToCreateContent();

	@Meta.AD(
		deflt = "true", name = "enable-openai-image-generation",
		required = false
	)
	public boolean enableOpenAIImageGeneration();

	@Meta.AD(
		deflt = "gpt-image-2", description = "image-model-help",
		name = "image-model", required = false
	)
	public String imageModel();

	@Meta.AD(
		deflt = "1024x1024,1536x1024,1024x1536",
		description = "image-sizes-help", name = "image-sizes", required = false
	)
	public String imageSizes();

}