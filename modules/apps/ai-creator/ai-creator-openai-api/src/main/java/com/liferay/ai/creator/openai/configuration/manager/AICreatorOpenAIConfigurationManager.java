/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.configuration.manager;

import com.liferay.portal.kernel.module.configuration.ConfigurationException;

/**
 * @author Lourdes Fernández Besada
 */
public interface AICreatorOpenAIConfigurationManager {

	public String getAICreatorOpenAICompanyAPIKey(long companyId)
		throws ConfigurationException;

	public String getAICreatorOpenAICompanyImageModel(long companyId)
		throws ConfigurationException;

	public String getAICreatorOpenAICompanyImageSizes(long companyId)
		throws ConfigurationException;

	public String getAICreatorOpenAIGroupAPIKey(long groupId)
		throws ConfigurationException;

	public String getAICreatorOpenAIGroupAPIKey(long companyId, long groupId)
		throws ConfigurationException;

	public String getAICreatorOpenAIGroupImageModel(long groupId)
		throws ConfigurationException;

	public String getAICreatorOpenAIGroupImageModel(
			long companyId, long groupId)
		throws ConfigurationException;

	public String getAICreatorOpenAIGroupImageSizes(long groupId)
		throws ConfigurationException;

	public String getAICreatorOpenAIGroupImageSizes(
			long companyId, long groupId)
		throws ConfigurationException;

	public boolean isAICreatorChatGPTCompanyEnabled(long companyId)
		throws ConfigurationException;

	public boolean isAICreatorChatGPTGroupEnabled(long companyId, long groupId)
		throws ConfigurationException;

	public boolean isAICreatorOpenAIImageGenerationCompanyEnabled(
			long companyId)
		throws ConfigurationException;

	public boolean isAICreatorOpenAIImageGenerationGroupEnabled(
			long companyId, long groupId)
		throws ConfigurationException;

	public void saveAICreatorOpenAICompanyConfiguration(
			String apiKey, long companyId, boolean enableChatGPT,
			boolean enableOpenAIImageGeneration, String imageModel,
			String imageSizes)
		throws ConfigurationException;

	public void saveAICreatorOpenAIGroupConfiguration(
			String apiKey, long groupId, boolean enableChatGPT,
			boolean enableOpenAIImageGeneration, String imageModel,
			String imageSizes)
		throws ConfigurationException;

}