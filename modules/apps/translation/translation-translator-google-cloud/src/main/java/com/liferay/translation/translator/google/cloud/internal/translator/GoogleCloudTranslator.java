/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.translator.google.cloud.internal.translator;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.translation.exception.TranslatorException;
import com.liferay.translation.translator.BaseTranslator;
import com.liferay.translation.translator.Translator;
import com.liferay.translation.translator.TranslatorPacket;
import com.liferay.translation.translator.google.cloud.internal.configuration.GoogleCloudTranslatorConfiguration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 * @author Roberto Díaz
 */
@Component(
	configurationPid = "com.liferay.translation.translator.google.cloud.internal.configuration.GoogleCloudTranslatorConfiguration",
	service = Translator.class
)
public class GoogleCloudTranslator extends BaseTranslator {

	@Override
	public boolean isEnabled(long companyId) throws ConfigurationException {
		GoogleCloudTranslatorConfiguration googleCloudTranslatorConfiguration =
			_configurationProvider.getCompanyConfiguration(
				GoogleCloudTranslatorConfiguration.class, companyId);

		if (googleCloudTranslatorConfiguration.enabled() &&
			!Validator.isBlank(
				googleCloudTranslatorConfiguration.
					serviceAccountPrivateKey())) {

			return true;
		}

		return false;
	}

	@Override
	public TranslatorPacket translate(TranslatorPacket translatorPacket)
		throws PortalException {

		GoogleCloudTranslatorConfiguration googleCloudTranslatorConfiguration =
			_configurationProvider.getCompanyConfiguration(
				GoogleCloudTranslatorConfiguration.class,
				translatorPacket.getCompanyId());

		if (!googleCloudTranslatorConfiguration.enabled()) {
			return translatorPacket;
		}

		String sourceLanguageCode = getLanguageCode(
			translatorPacket.getSourceLanguageId());
		String targetLanguageCode = getLanguageCode(
			translatorPacket.getTargetLanguageId());

		Translate translate = _getTranslate(googleCloudTranslatorConfiguration);

		Set<String> supportedLanguageCodes = SetUtil.fromCollection(
			TransformUtil.transform(
				translate.listSupportedLanguages(), Language::getCode));

		if (!supportedLanguageCodes.contains(sourceLanguageCode) ||
			!supportedLanguageCodes.contains(targetLanguageCode)) {

			throw new TranslatorException(
				"Translation between the selected languages is not supported");
		}

		Map<String, String> fieldsMap = translatorPacket.getFieldsMap();

		List<Translation> translations = translate.translate(
			new ArrayList<>(fieldsMap.values()),
			Translate.TranslateOption.sourceLanguage(sourceLanguageCode),
			Translate.TranslateOption.targetLanguage(targetLanguageCode));

		Map<String, String> translationFieldsMap = new HashMap<>();

		Iterator<Translation> iterator = translations.iterator();

		for (String key : fieldsMap.keySet()) {
			Translation translation = iterator.next();

			translationFieldsMap.put(key, translation.getTranslatedText());
		}

		return new TranslatorPacket() {

			@Override
			public long getCompanyId() {
				return translatorPacket.getCompanyId();
			}

			@Override
			public Map<String, String> getFieldsMap() {
				return translationFieldsMap;
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

	private Translate _getTranslate(
			GoogleCloudTranslatorConfiguration
				googleCloudTranslatorConfiguration)
		throws ConfigurationException {

		String serviceAccountPrivateKey =
			googleCloudTranslatorConfiguration.serviceAccountPrivateKey();

		GoogleCredentials googleCredentials = null;

		try (InputStream inputStream = new ByteArrayInputStream(
				serviceAccountPrivateKey.getBytes())) {

			googleCredentials = GoogleCredentials.fromStream(inputStream);
		}
		catch (IOException ioException) {
			throw new SystemException(
				"Unable to authenticate with Google Cloud", ioException);
		}

		TranslateOptions.DefaultTranslateFactory defaultTranslateFactory =
			new TranslateOptions.DefaultTranslateFactory();

		return defaultTranslateFactory.create(
			TranslateOptions.newBuilder(
			).setCredentials(
				googleCredentials
			).build());
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

}