/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.internal.configuration.manager;

import com.liferay.ai.creator.openai.configuration.AICreatorOpenAICompanyConfiguration;
import com.liferay.ai.creator.openai.configuration.AICreatorOpenAIGroupConfiguration;
import com.liferay.ai.creator.openai.configuration.manager.AICreatorOpenAIConfigurationManager;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
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
	public boolean isAICreatorChatGPTCompanyEnabled(long companyId)
		throws ConfigurationException {

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
	public boolean isAICreatorDALLECompanyEnabled(long companyId)
		throws ConfigurationException {

		AICreatorOpenAICompanyConfiguration
			aiCreatorOpenAICompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					AICreatorOpenAICompanyConfiguration.class, companyId);

		return aiCreatorOpenAICompanyConfiguration.enableDALLEToCreateImages();
	}

	@Override
	public boolean isAICreatorDALLEGroupEnabled(long companyId, long groupId)
		throws ConfigurationException {

		if (!isAICreatorDALLECompanyEnabled(companyId)) {
			return false;
		}

		AICreatorOpenAIGroupConfiguration aiCreatorOpenAIGroupConfiguration =
			_configurationProvider.getGroupConfiguration(
				AICreatorOpenAIGroupConfiguration.class, companyId, groupId);

		return aiCreatorOpenAIGroupConfiguration.enableDALLEToCreateImages();
	}

	@Override
	public void saveAICreatorOpenAICompanyConfiguration(
			long companyId, String apiKey, boolean enableChatGPT,
			boolean enableDALLE)
		throws ConfigurationException {

		_configurationProvider.saveCompanyConfiguration(
			AICreatorOpenAICompanyConfiguration.class, companyId,
			HashMapDictionaryBuilder.<String, Object>put(
				"apiKey", apiKey
			).put(
				"enableChatGPTToCreateContent", enableChatGPT
			).put(
				"enableDALLEToCreateImages", enableDALLE
			).build());
	}

	@Override
	public void saveAICreatorOpenAIGroupConfiguration(
			long groupId, String apiKey, boolean enableChatGPT,
			boolean enableDALLE)
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
				"enableDALLEToCreateImages", enableDALLE
			).build());
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private SecretResolver _secretResolver;

}