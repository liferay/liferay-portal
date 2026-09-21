/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.google.cloud.natural.language.internal;

import com.liferay.asset.auto.tagger.AssetAutoTagProvider;
import com.liferay.asset.auto.tagger.google.cloud.natural.language.internal.configuration.GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration;
import com.liferay.asset.auto.tagger.google.cloud.natural.language.internal.constants.GCloudNaturalLanguageDocumentAssetAutoTaggerConstants;
import com.liferay.asset.auto.tagger.google.cloud.natural.language.internal.util.GCloudNaturalLanguageUtil;
import com.liferay.asset.auto.tagger.text.extractor.TextExtractor;
import com.liferay.asset.auto.tagger.text.extractor.TextExtractorRegistry;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.key.secret.SecretResolver;

import java.net.HttpURLConnection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "model.class.name=com.liferay.asset.kernel.model.AssetEntry",
	service = AssetAutoTagProvider.class
)
public class GCloudNaturalLanguageDocumentAssetAutoTagProvider
	implements AssetAutoTagProvider<AssetEntry> {

	@Override
	public Collection<String> getTagNames(AssetEntry assetEntry) {
		try {
			if (_isEnabled(assetEntry)) {
				TextExtractor<Object> textExtractor =
					(TextExtractor<Object>)
						_textExtractorRegistry.getTextExtractor(
							assetEntry.getClassName());

				if (textExtractor != null) {
					Locale locale = LocaleUtil.fromLanguageId(
						assetEntry.getDefaultLanguageId());

					return _getTagNames(
						assetEntry.getCompanyId(),
						() -> textExtractor.extract(
							_getAssetObject(assetEntry), locale),
						locale, assetEntry.getMimeType());
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return Collections.emptyList();
	}

	private Object _getAssetObject(AssetEntry assetEntry) {
		AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

		return assetRenderer.getAssetObject();
	}

	private Collection<String> _getClassificationTagNames(
			long companyId,
			GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration
				gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration,
			String documentPayload, Locale locale)
		throws Exception {

		if (!gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration.
				classificationEndpointEnabled() ||
			(Objects.nonNull(locale) &&
			 !Objects.equals(
				 locale.getLanguage(), LocaleUtil.ENGLISH.getLanguage()))) {

			return Collections.emptySet();
		}

		JSONObject responseJSONObject = _post(
			_getServiceURL(
				_secretResolver.resolve(
					companyId,
					gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration.
						apiKey()),
				"classifyText"),
			documentPayload);
		float confidence =
			gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration.
				confidence();

		return _toTagNames(
			responseJSONObject.getJSONArray("categories"),
			jsonObject -> jsonObject.getDouble("confidence") > confidence);
	}

	private String _getDocumentPayload(String content, String mimeType) {
		String type = GCloudNaturalLanguageUtil.getType(mimeType);

		int size =
			GCloudNaturalLanguageDocumentAssetAutoTaggerConstants.
				MAX_CHARACTERS_SERVICE - _MINIMUM_PAYLOAD_SIZE - type.length();

		String truncatedContent = GCloudNaturalLanguageUtil.truncateToSize(
			content, size);

		return GCloudNaturalLanguageUtil.getDocumentPayload(
			truncatedContent, type);
	}

	private Collection<String> _getEntitiesTagNames(
			long companyId,
			GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration
				gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration,
			String documentPayload, Locale locale)
		throws Exception {

		if (!gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration.
				entityEndpointEnabled() ||
			(Objects.nonNull(locale) &&
			 !_supportedEntityLanguages.contains(locale.getLanguage()))) {

			return Collections.emptySet();
		}

		JSONObject responseJSONObject = _post(
			_getServiceURL(
				_secretResolver.resolve(
					companyId,
					gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration.
						apiKey()),
				"analyzeEntities"),
			documentPayload);
		float salience =
			gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration.salience();

		return _toTagNames(
			responseJSONObject.getJSONArray("entities"),
			jsonObject -> jsonObject.getDouble("salience") > salience);
	}

	private String _getServiceURL(String apiKey, String endpoint) {
		return StringBundler.concat(
			"https://language.googleapis.com/v1/documents:", endpoint, "?key=",
			apiKey);
	}

	private Collection<String> _getTagNames(
			long companyId, Supplier<String> textSupplier, Locale locale,
			String mimeType)
		throws Exception {

		if (!_supportedContentTypes.contains(mimeType)) {
			return Collections.emptySet();
		}

		GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration
			gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration.
						class,
					companyId);

		if (!gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration.
				classificationEndpointEnabled() &&
			!gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration.
				entityEndpointEnabled()) {

			return Collections.emptySet();
		}

		String documentPayload = _getDocumentPayload(
			textSupplier.get(), mimeType);

		Collection<String> classificationTagNames = _getClassificationTagNames(
			companyId, gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration,
			documentPayload, locale);

		Collection<String> entitiesTagNames = _getEntitiesTagNames(
			companyId, gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration,
			documentPayload, locale);

		Set<String> tagNames = new HashSet<>();

		tagNames.addAll(classificationTagNames);
		tagNames.addAll(entitiesTagNames);

		return tagNames;
	}

	private boolean _isEnabled(AssetEntry assetEntry)
		throws ConfigurationException {

		GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration
			gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration.
						class,
					assetEntry.getCompanyId());

		return ArrayUtil.contains(
			gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration.
				enabledClassNames(),
			assetEntry.getClassName());
	}

	private JSONObject _post(String serviceURL, String body) throws Exception {
		Http.Options options = new Http.Options();

		options.addHeader("Content-Type", ContentTypes.APPLICATION_JSON);
		options.setBody(body, ContentTypes.APPLICATION_JSON, StringPool.UTF8);
		options.setLocation(serviceURL);
		options.setPost(true);

		String responseJSON = _http.URLtoString(options);

		JSONObject jsonObject = _jsonFactory.createJSONObject(responseJSON);

		Http.Response response = options.getResponse();

		if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return jsonObject;
		}

		JSONObject errorJSONObject = jsonObject.getJSONObject("error");

		String errorMessage = responseJSON;

		if (errorJSONObject != null) {
			errorMessage = errorJSONObject.getString("message");
		}

		throw new PortalException(
			StringBundler.concat(
				"Unable to generate tags with the Google Natural Language ",
				"service. Response code ", response.getResponseCode(), ": ",
				errorMessage));
	}

	private Set<String> _toTagNames(
		JSONArray jsonArray, Predicate<JSONObject> predicate) {

		if (jsonArray == null) {
			return Collections.emptySet();
		}

		Set<String> tagNames = new HashSet<>();

		for (Object object : jsonArray) {
			JSONObject jsonObject = (JSONObject)object;

			if (!predicate.test(jsonObject)) {
				continue;
			}

			String[] tagNameParts1 = StringUtil.split(
				StringUtil.removeChars(
					jsonObject.getString("name"), CharPool.APOSTROPHE,
					CharPool.DASH),
				CharPool.AMPERSAND);

			for (String tagNamePart1 : tagNameParts1) {
				String[] tagNameParts2 = StringUtil.split(
					tagNamePart1, CharPool.FORWARD_SLASH);

				for (String tagNamePart2 : tagNameParts2) {
					tagNamePart2 = tagNamePart2.trim();

					if (!tagNamePart2.isEmpty()) {
						tagNames.add(tagNamePart2);
					}
				}
			}
		}

		return tagNames;
	}

	private static final int _MINIMUM_PAYLOAD_SIZE;

	private static final Log _log = LogFactoryUtil.getLog(
		GCloudNaturalLanguageDocumentAssetAutoTagProvider.class);

	private static final Set<String> _supportedContentTypes = new HashSet<>(
		Arrays.asList(
			"application/epub+zip", "application/vnd.apple.pages.13",
			"application/vnd.google-apps.document",
			"application/vnd.oasis.opendocument.text",
			"application/vnd.openxmlformats-officedocument.wordprocessingml." +
				"document",
			ContentTypes.APPLICATION_MSWORD, ContentTypes.APPLICATION_PDF,
			ContentTypes.APPLICATION_TEXT, ContentTypes.TEXT_HTML,
			ContentTypes.TEXT_PLAIN));
	private static final Set<String> _supportedEntityLanguages = new HashSet<>(
		Arrays.asList(
			LocaleUtil.CHINESE.getLanguage(), LocaleUtil.ENGLISH.getLanguage(),
			LocaleUtil.FRENCH.getLanguage(), LocaleUtil.GERMAN.getLanguage(),
			LocaleUtil.ITALIAN.getLanguage(), LocaleUtil.JAPAN.getLanguage(),
			LocaleUtil.KOREAN.getLanguage(), LocaleUtil.PORTUGAL.getLanguage(),
			LocaleUtil.SPAIN.getLanguage()));

	static {
		String payload = GCloudNaturalLanguageUtil.getDocumentPayload(
			StringPool.BLANK, StringPool.BLANK);

		_MINIMUM_PAYLOAD_SIZE = payload.length();
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private SecretResolver _secretResolver;

	@Reference
	private TextExtractorRegistry _textExtractorRegistry;

}