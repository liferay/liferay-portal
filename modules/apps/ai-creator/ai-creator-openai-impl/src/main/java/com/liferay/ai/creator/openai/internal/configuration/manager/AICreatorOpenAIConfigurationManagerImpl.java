/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.internal.configuration.manager;

import com.liferay.ai.creator.openai.configuration.AICreatorOpenAICompanyConfiguration;
import com.liferay.ai.creator.openai.configuration.AICreatorOpenAIGroupConfiguration;
import com.liferay.ai.creator.openai.configuration.manager.AICreatorOpenAIConfigurationManager;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.secret.SecretResolver;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = AICreatorOpenAIConfigurationManager.class)
public class AICreatorOpenAIConfigurationManagerImpl
	implements AICreatorOpenAIConfigurationManager {

	@Override
	public String getAICreatorOpenAICompanyAPIKey(long companyId)
		throws ConfigurationException {

		AICreatorOpenAICompanyConfiguration
			aiCreatorOpenAICompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					AICreatorOpenAICompanyConfiguration.class, companyId);

		return _secretResolver.resolve(
			companyId, aiCreatorOpenAICompanyConfiguration.apiKey());
	}

	@Override
	public String getAICreatorOpenAICompanyImageModel(long companyId)
		throws ConfigurationException {

		AICreatorOpenAICompanyConfiguration
			aiCreatorOpenAICompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					AICreatorOpenAICompanyConfiguration.class, companyId);

		String imageModel = aiCreatorOpenAICompanyConfiguration.imageModel();

		if (Validator.isNull(imageModel)) {
			return "gpt-image-2";
		}

		return imageModel;
	}

	@Override
	public String getAICreatorOpenAICompanyImageSizes(long companyId)
		throws ConfigurationException {

		AICreatorOpenAICompanyConfiguration
			aiCreatorOpenAICompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					AICreatorOpenAICompanyConfiguration.class, companyId);

		String imageSizes = aiCreatorOpenAICompanyConfiguration.imageSizes();

		if (Validator.isNull(imageSizes)) {
			return "1024x1024,1536x1024,1024x1536";
		}

		return imageSizes;
	}

	@Override
	public String getAICreatorOpenAIGroupAPIKey(long groupId)
		throws ConfigurationException {

		Group group = _groupLocalService.fetchGroup(groupId);

		AICreatorOpenAIGroupConfiguration aiCreatorOpenAIGroupConfiguration =
			_configurationProvider.getGroupConfiguration(
				AICreatorOpenAIGroupConfiguration.class, group.getCompanyId(),
				groupId);

		return _secretResolver.resolve(
			group.getCompanyId(), aiCreatorOpenAIGroupConfiguration.apiKey());
	}

	@Override
	public String getAICreatorOpenAIGroupAPIKey(long companyId, long groupId)
		throws ConfigurationException {

		AICreatorOpenAIGroupConfiguration aiCreatorOpenAIGroupConfiguration =
			_configurationProvider.getGroupConfiguration(
				AICreatorOpenAIGroupConfiguration.class, companyId, groupId);

		String apiKey = aiCreatorOpenAIGroupConfiguration.apiKey();

		if (Validator.isNotNull(apiKey)) {
			return _secretResolver.resolve(companyId, apiKey);
		}

		AICreatorOpenAICompanyConfiguration
			aiCreatorOpenAICompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					AICreatorOpenAICompanyConfiguration.class, companyId);

		return _secretResolver.resolve(
			companyId, aiCreatorOpenAICompanyConfiguration.apiKey());
	}

	@Override
	public String getAICreatorOpenAIGroupImageModel(long groupId)
		throws ConfigurationException {

		Group group = _groupLocalService.fetchGroup(groupId);

		AICreatorOpenAIGroupConfiguration aiCreatorOpenAIGroupConfiguration =
			_configurationProvider.getGroupConfiguration(
				AICreatorOpenAIGroupConfiguration.class, group.getCompanyId(),
				groupId);

		return aiCreatorOpenAIGroupConfiguration.imageModel();
	}

	@Override
	public String getAICreatorOpenAIGroupImageModel(
			long companyId, long groupId)
		throws ConfigurationException {

		AICreatorOpenAIGroupConfiguration aiCreatorOpenAIGroupConfiguration =
			_configurationProvider.getGroupConfiguration(
				AICreatorOpenAIGroupConfiguration.class, companyId, groupId);

		String imageModel = aiCreatorOpenAIGroupConfiguration.imageModel();

		if (Validator.isNotNull(imageModel)) {
			return imageModel;
		}

		return getAICreatorOpenAICompanyImageModel(companyId);
	}

	@Override
	public String getAICreatorOpenAIGroupImageSizes(long groupId)
		throws ConfigurationException {

		Group group = _groupLocalService.fetchGroup(groupId);

		AICreatorOpenAIGroupConfiguration aiCreatorOpenAIGroupConfiguration =
			_configurationProvider.getGroupConfiguration(
				AICreatorOpenAIGroupConfiguration.class, group.getCompanyId(),
				groupId);

		return aiCreatorOpenAIGroupConfiguration.imageSizes();
	}

	@Override
	public String getAICreatorOpenAIGroupImageSizes(
			long companyId, long groupId)
		throws ConfigurationException {

		AICreatorOpenAIGroupConfiguration aiCreatorOpenAIGroupConfiguration =
			_configurationProvider.getGroupConfiguration(
				AICreatorOpenAIGroupConfiguration.class, companyId, groupId);

		String imageSizes = aiCreatorOpenAIGroupConfiguration.imageSizes();

		if (Validator.isNotNull(imageSizes)) {
			return imageSizes;
		}

		return getAICreatorOpenAICompanyImageSizes(companyId);
	}

	@Override
	public boolean isAICreatorChatGPTCompanyEnabled(long companyId)
		throws ConfigurationException {

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-93786")) {
			return false;
		}

		AICreatorOpenAICompanyConfiguration
			aiCreatorOpenAICompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					AICreatorOpenAICompanyConfiguration.class, companyId);

		return aiCreatorOpenAICompanyConfiguration.
			enableChatGPTToCreateContent();
	}

	@Override
	public boolean isAICreatorChatGPTGroupEnabled(long companyId, long groupId)
		throws ConfigurationException {

		if (!isAICreatorChatGPTCompanyEnabled(companyId)) {
			return false;
		}

		AICreatorOpenAIGroupConfiguration aiCreatorOpenAIGroupConfiguration =
			_configurationProvider.getGroupConfiguration(
				AICreatorOpenAIGroupConfiguration.class, companyId, groupId);

		return aiCreatorOpenAIGroupConfiguration.enableChatGPTToCreateContent();
	}

	@Override
	public boolean isAICreatorOpenAIImageGenerationCompanyEnabled(
			long companyId)
		throws ConfigurationException {

		AICreatorOpenAICompanyConfiguration
			aiCreatorOpenAICompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					AICreatorOpenAICompanyConfiguration.class, companyId);

		return aiCreatorOpenAICompanyConfiguration.
			enableOpenAIImageGeneration();
	}

	@Override
	public boolean isAICreatorOpenAIImageGenerationGroupEnabled(
			long companyId, long groupId)
		throws ConfigurationException {

		if (!isAICreatorOpenAIImageGenerationCompanyEnabled(companyId)) {
			return false;
		}

		AICreatorOpenAIGroupConfiguration aiCreatorOpenAIGroupConfiguration =
			_configurationProvider.getGroupConfiguration(
				AICreatorOpenAIGroupConfiguration.class, companyId, groupId);

		return aiCreatorOpenAIGroupConfiguration.enableOpenAIImageGeneration();
	}

	@Override
	public void saveAICreatorOpenAICompanyConfiguration(
			String apiKey, long companyId, boolean enableChatGPT,
			boolean enableOpenAIImageGeneration, String imageModel,
			String imageSizes)
		throws ConfigurationException {

		_configurationProvider.saveCompanyConfiguration(
			AICreatorOpenAICompanyConfiguration.class, companyId,
			HashMapDictionaryBuilder.<String, Object>put(
				"apiKey", apiKey
			).put(
				"enableChatGPTToCreateContent", enableChatGPT
			).put(
				"enableOpenAIImageGeneration", enableOpenAIImageGeneration
			).put(
				"imageModel", imageModel
			).put(
				"imageSizes", imageSizes
			).build());
	}

	@Override
	public void saveAICreatorOpenAIGroupConfiguration(
			String apiKey, long groupId, boolean enableChatGPT,
			boolean enableOpenAIImageGeneration, String imageModel,
			String imageSizes)
		throws ConfigurationException {

		Group group = _groupLocalService.fetchGroup(groupId);

		_configurationProvider.saveGroupConfiguration(
			AICreatorOpenAIGroupConfiguration.class, group.getCompanyId(),
			groupId,
			HashMapDictionaryBuilder.<String, Object>put(
				"apiKey", apiKey
			).put(
				"enableChatGPTToCreateContent", enableChatGPT
			).put(
				"enableOpenAIImageGeneration", enableOpenAIImageGeneration
			).put(
				"imageModel", imageModel
			).put(
				"imageSizes", imageSizes
			).build());
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private SecretResolver _secretResolver;

}