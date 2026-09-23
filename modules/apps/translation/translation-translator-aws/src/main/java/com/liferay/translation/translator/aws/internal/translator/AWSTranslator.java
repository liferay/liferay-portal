/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.translator.aws.internal.translator;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.translation.translator.BaseTranslator;
import com.liferay.translation.translator.Translator;
import com.liferay.translation.translator.TranslatorPacket;
import com.liferay.translation.translator.aws.internal.configuration.AWSTranslatorConfiguration;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

/**
 * @author Adolfo Pérez
 * @author Roberto Díaz
 */
@Component(
	configurationPid = "com.liferay.translation.translator.aws.internal.configuration.AWSTranslatorConfiguration",
	service = Translator.class
)
public class AWSTranslator extends BaseTranslator {

	@Override
	public boolean isEnabled(long companyId) throws ConfigurationException {
		AWSTranslatorConfiguration awsTranslatorConfiguration =
			_configurationProvider.getCompanyConfiguration(
				AWSTranslatorConfiguration.class, companyId);

		return awsTranslatorConfiguration.enabled();
	}

	@Override
	public TranslatorPacket translate(TranslatorPacket translatorPacket)
		throws PortalException {

		AWSTranslatorConfiguration awsTranslatorConfiguration =
			_configurationProvider.getCompanyConfiguration(
				AWSTranslatorConfiguration.class,
				translatorPacket.getCompanyId());

		if (!awsTranslatorConfiguration.enabled()) {
			return translatorPacket;
		}

		TranslateClient translateClient = TranslateClient.builder(
		).credentialsProvider(
			StaticCredentialsProvider.create(
				AwsBasicCredentials.create(
					awsTranslatorConfiguration.accessKey(),
					_secretResolver.resolve(
						translatorPacket.getCompanyId(),
						awsTranslatorConfiguration.secretKey())))
		).region(
			Region.of(awsTranslatorConfiguration.region())
		).build();

		Map<String, String> translatedFieldsMap = new HashMap<>();

		String sourceLanguageCode = getLanguageCode(
			translatorPacket.getSourceLanguageId());
		String targetLanguageCode = getLanguageCode(
			translatorPacket.getTargetLanguageId());

		Map<String, String> fieldsMap = translatorPacket.getFieldsMap();

		fieldsMap.forEach(
			(key, value) -> translatedFieldsMap.put(
				key,
				_translate(
					translateClient, value, sourceLanguageCode,
					targetLanguageCode)));

		return new TranslatorPacket() {

			@Override
			public long getCompanyId() {
				return translatorPacket.getCompanyId();
			}

			@Override
			public Map<String, String> getFieldsMap() {
				return translatedFieldsMap;
			}

			@Override
			public Map<String, Boolean> getHTMLMap() {
				return translatorPacket.getHTMLMap();
			}

			@Override
			public String getSourceLanguageId() {
				return translatorPacket.getSourceLanguageId();
			}

			@Override
			public String getTargetLanguageId() {
				return translatorPacket.getTargetLanguageId();
			}

		};
	}

	private String _translate(
		TranslateClient translateClient, String text, String sourceLanguageCode,
		String targetLanguageCode) {

		if (Validator.isBlank(text)) {
			return text;
		}

		TranslateTextResponse translateTextResponse =
			translateClient.translateText(
				builder -> builder.sourceLanguageCode(
					sourceLanguageCode
				).targetLanguageCode(
					targetLanguageCode
				).text(
					text
				).build());

		return translateTextResponse.translatedText();
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private SecretResolver _secretResolver;

}