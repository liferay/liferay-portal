/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.opennlp.internal;

import com.liferay.asset.auto.tagger.AssetAutoTagProvider;
import com.liferay.asset.auto.tagger.opennlp.internal.configuration.OpenNLPDocumentAssetAutoTaggerCompanyConfiguration;
import com.liferay.asset.auto.tagger.text.extractor.TextExtractor;
import com.liferay.asset.auto.tagger.text.extractor.TextExtractorRegistry;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import opennlp.tools.namefind.ThreadSafeNameFinderME;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "model.class.name=com.liferay.asset.kernel.model.AssetEntry",
	service = AssetAutoTagProvider.class
)
public class OpenNLPDocumentAssetAutoTagProvider
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

					AssetRenderer<?> assetRenderer =
						assetEntry.getAssetRenderer();

					return _getTagNames(
						assetEntry.getCompanyId(),
						() -> textExtractor.extract(
							assetRenderer.getAssetObject(), locale),
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

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundle = bundleContext.getBundle();
	}

	private TokenNameFinder _createTokenNameFinder(String resourceName)
		throws IOException {

		return new ThreadSafeNameFinderME(
			new TokenNameFinderModel(_bundle.getResource(resourceName)));
	}

	private String[] _getTagNames(
		List<TokenNameFinder> tokenNameFinders, String[] tokens,
		double confidenceThreshold) {

		List<Span> spans = new ArrayList<>();

		for (TokenNameFinder tokenNameFinder : tokenNameFinders) {
			try {
				spans.addAll(
					TransformUtil.transformToList(
						tokenNameFinder.find(tokens),
						nameSpan -> {
							if (nameSpan.getProb() > confidenceThreshold) {
								return nameSpan;
							}

							return null;
						}));
			}
			finally {
				tokenNameFinder.clearAdaptiveData();
			}
		}

		return Span.spansToStrings(spans.toArray(new Span[0]), tokens);
	}

	private Collection<String> _getTagNames(
			long companyId, Supplier<String> textSupplier, Locale locale,
			String mimeType)
		throws Exception {

		if ((Objects.nonNull(locale) &&
			 !Objects.equals(
				 locale.getLanguage(), LocaleUtil.ENGLISH.getLanguage())) ||
			!_supportedContentTypes.contains(mimeType)) {

			return Collections.emptyList();
		}

		SentenceDetectorME sentenceDetectorME = new SentenceDetectorME(
			_sentenceModelDCLSingleton.getSingleton(
				() -> {
					try {
						return new SentenceModel(
							_bundle.getResource(
								"org.apache.opennlp.model.en.sent.bin"));
					}
					catch (IOException ioException) {
						return ReflectionUtil.throwException(ioException);
					}
				}));

		TokenizerME tokenizerME = new TokenizerME(
			_tokenizerModelDCLSingleton.getSingleton(
				() -> {
					try {
						return new TokenizerModel(
							_bundle.getResource(
								"org.apache.opennlp.model.en.token.bin"));
					}
					catch (IOException ioException) {
						return ReflectionUtil.throwException(ioException);
					}
				}));

		List<TokenNameFinder> tokenNameFinders =
			_tokenNameFindersDCLSingleton.getSingleton(
				() -> {
					try {
						return Arrays.asList(
							_createTokenNameFinder(
								"org.apache.opennlp.model.en.ner.location.bin"),
							_createTokenNameFinder(
								"org.apache.opennlp.model.en.ner." +
									"organization.bin"),
							_createTokenNameFinder(
								"org.apache.opennlp.model.en.ner.person.bin"));
					}
					catch (IOException ioException) {
						return ReflectionUtil.throwException(ioException);
					}
				});

		OpenNLPDocumentAssetAutoTaggerCompanyConfiguration
			openNLPDocumentAssetAutoTaggerCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					OpenNLPDocumentAssetAutoTaggerCompanyConfiguration.class,
					companyId);

		Set<String> tagNames = new HashSet<>();

		for (String sentence :
				sentenceDetectorME.sentDetect(textSupplier.get())) {

			Collections.addAll(
				tagNames,
				_getTagNames(
					tokenNameFinders, tokenizerME.tokenize(sentence),
					openNLPDocumentAssetAutoTaggerCompanyConfiguration.
						confidenceThreshold()));
		}

		return tagNames;
	}

	private boolean _isEnabled(AssetEntry assetEntry)
		throws ConfigurationException {

		OpenNLPDocumentAssetAutoTaggerCompanyConfiguration
			openNLPDocumentAssetAutoTaggerCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					OpenNLPDocumentAssetAutoTaggerCompanyConfiguration.class,
					assetEntry.getCompanyId());

		return ArrayUtil.contains(
			openNLPDocumentAssetAutoTaggerCompanyConfiguration.
				enabledClassNames(),
			assetEntry.getClassName());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OpenNLPDocumentAssetAutoTagProvider.class);

	private static final Set<String> _supportedContentTypes = new HashSet<>(
		Arrays.asList(
			"application/epub+zip", "application/vnd.apple.pages.13",
			"application/vnd.google-apps.document",
			"application/vnd.oasis.opendocument.text",
			"application/vnd.openxmlformats-officedocument.wordprocessingml." +
				"document",
			ContentTypes.APPLICATION_MSWORD, ContentTypes.APPLICATION_PDF,
			ContentTypes.APPLICATION_TEXT, ContentTypes.TEXT,
			ContentTypes.TEXT_PLAIN, ContentTypes.TEXT_HTML,
			ContentTypes.TEXT_HTML_UTF8));

	private Bundle _bundle;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private final DCLSingleton<SentenceModel> _sentenceModelDCLSingleton =
		new DCLSingleton<>();

	@Reference
	private TextExtractorRegistry _textExtractorRegistry;

	private final DCLSingleton<TokenizerModel> _tokenizerModelDCLSingleton =
		new DCLSingleton<>();
	private final DCLSingleton<List<TokenNameFinder>>
		_tokenNameFindersDCLSingleton = new DCLSingleton<>();

}