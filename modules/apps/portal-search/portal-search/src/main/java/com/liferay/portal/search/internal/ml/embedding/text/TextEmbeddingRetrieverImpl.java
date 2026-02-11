/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.ml.embedding.text;

import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.configuration.SemanticSearchConfiguration;
import com.liferay.portal.search.configuration.SemanticSearchConfigurationProvider;
import com.liferay.portal.search.internal.ml.embedding.text.util.TextExtractionUtil;
import com.liferay.portal.search.internal.web.cache.TextEmbeddingProviderWebCacheItem;
import com.liferay.portal.search.ml.embedding.EmbeddingProviderStatus;
import com.liferay.portal.search.ml.embedding.text.TextEmbeddingRetriever;
import com.liferay.portal.search.rest.dto.v1_0.EmbeddingProviderConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(service = TextEmbeddingRetriever.class)
public class TextEmbeddingRetrieverImpl implements TextEmbeddingRetriever {

	@Override
	public List<String> getAvailableProviderNames() {
		return ListUtil.fromCollection(_textEmbeddingProviders.keySet());
	}

	@Override
	public EmbeddingProviderStatus getEmbeddingProviderStatus(
		String embeddingProviderConfigurationJSON) {

		EmbeddingProviderConfiguration embeddingProviderConfiguration = null;

		try {
			embeddingProviderConfiguration =
				EmbeddingProviderConfiguration.unsafeToDTO(
					embeddingProviderConfigurationJSON);
		}
		catch (Exception exception) {
			return new EmbeddingProviderStatus.EmbeddingProviderStatusBuilder(
			).errorMessage(
				exception.getMessage()
			).build();
		}

		String providerName = embeddingProviderConfiguration.getProviderName();

		try {
			TextEmbeddingProvider textEmbeddingProvider =
				_textEmbeddingProviders.get(providerName);

			if (textEmbeddingProvider == null) {
				return new EmbeddingProviderStatus.
					EmbeddingProviderStatusBuilder(
				).errorMessage(
					"Embedding provider " + providerName + " was not found"
				).providerName(
					providerName
				).build();
			}

			Double[] textEmbedding = textEmbeddingProvider.getEmbedding(
				embeddingProviderConfiguration, StringUtil.randomString());

			return new EmbeddingProviderStatus.EmbeddingProviderStatusBuilder(
			).embeddingVectorDimensions(
				textEmbedding.length
			).providerName(
				providerName
			).build();
		}
		catch (Exception exception) {
			return new EmbeddingProviderStatus.EmbeddingProviderStatusBuilder(
			).errorMessage(
				exception.getMessage()
			).providerName(
				providerName
			).build();
		}
	}

	@Override
	public EmbeddingProviderStatus[] getEmbeddingProviderStatuses() {
		List<EmbeddingProviderStatus> embeddingProviderStatuses =
			new ArrayList<>();

		for (String textEmbeddingProviderConfigurationJSON :
				getTextEmbeddingProviderConfigurationJSONs()) {

			embeddingProviderStatuses.add(
				getEmbeddingProviderStatus(
					textEmbeddingProviderConfigurationJSON));
		}

		return embeddingProviderStatuses.toArray(
			new EmbeddingProviderStatus[0]);
	}

	@Override
	public Double[] getTextEmbedding(String providerName, String text) {
		if (Validator.isBlank(text)) {
			return new Double[0];
		}

		TextEmbeddingProvider textEmbeddingProvider =
			_textEmbeddingProviders.get(providerName);

		if (textEmbeddingProvider == null) {
			return new Double[0];
		}

		EmbeddingProviderConfiguration embeddingProviderConfiguration =
			getEmbeddingProviderConfiguration(providerName);

		if (embeddingProviderConfiguration == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Configuration for provider " + providerName +
						" not found");
			}

			return new Double[0];
		}

		String textExcerpt = _getTextExcerpt(
			embeddingProviderConfiguration, text);

		if (Validator.isBlank(textExcerpt)) {
			return new Double[0];
		}

		return TextEmbeddingProviderWebCacheItem.get(
			embeddingProviderConfiguration,
			_semanticSearchConfigurationProvider.getCompanyConfiguration(
				CompanyThreadLocal.getCompanyId()),
			textExcerpt, textEmbeddingProvider);
	}

	@Activate
	protected void activate(
		Map<String, Object> properties, BundleContext bundleContext) {

		_disabledProviders = (String[])properties.get("disabledProviders");

		_serviceRegistration = bundleContext.registerService(
			FeatureFlagListener.class,
			(companyId, featureFlagKey, enabled) -> {
				if (enabled) {
					_addTextEmbeddingProvider(
						"huggingFaceInferenceAPI",
						_huggingFaceInterfaceApiTextProvider);
				}
				else {
					_removeTextEmbeddingProvider("huggingFaceInferenceAPI");
				}
			},
			MapUtil.singletonDictionary("feature.flag.key", "LPS-122920"));

		_serviceRegistration = bundleContext.registerService(
			FeatureFlagListener.class,
			(companyId, featureFlagKey, enabled) -> {
				if (enabled) {
					_addTextEmbeddingProvider(
						"huggingFaceInferenceEndpoint",
						_huggingFaceInferenceEndpointTextProvider);
				}
				else {
					_removeTextEmbeddingProvider(
						"huggingFaceInferenceEndpoint");
				}
			},
			MapUtil.singletonDictionary("feature.flag.key", "LPS-122920"));

		_addTextEmbeddingProvider(
			disabledProviders, "openai", _openAITextEmbeddingTextProvider);

		_serviceRegistration = bundleContext.registerService(
			FeatureFlagListener.class,
			(companyId, featureFlagKey, enabled) -> {
				if (enabled) {
					_addTextEmbeddingProvider(
						"txtai", _txtAITextEmbeddingTextProvider);
				}
				else {
					_removeTextEmbeddingProvider("txtai");
				}
			},
			MapUtil.singletonDictionary("feature.flag.key", "LPS-122920"));

		_serviceRegistration = bundleContext.registerService(
			FeatureFlagListener.class,
			(companyId, featureFlagKey, enabled) -> {
				if (enabled) {
					_addTextEmbeddingProvider(
						"vertexAI", _vertexAITextEmbeddingTextProvider);
				}
				else {
					_removeTextEmbeddingProvider("vertexAI");
				}
			},
			MapUtil.singletonDictionary("feature.flag.key", "LPD-31789"));
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	protected EmbeddingProviderConfiguration getEmbeddingProviderConfiguration(
		String providerName) {

		for (String textEmbeddingProviderConfigurationJSON :
				getTextEmbeddingProviderConfigurationJSONs()) {

			EmbeddingProviderConfiguration embeddingProviderConfiguration =
				EmbeddingProviderConfiguration.toDTO(
					textEmbeddingProviderConfigurationJSON);

			if (providerName.equals(
					embeddingProviderConfiguration.getProviderName())) {

				return embeddingProviderConfiguration;
			}
		}

		return null;
	}

	protected String[] getTextEmbeddingProviderConfigurationJSONs() {
		SemanticSearchConfiguration semanticSearchConfiguration =
			_semanticSearchConfigurationProvider.getCompanyConfiguration(
				CompanyThreadLocal.getCompanyId());

		return semanticSearchConfiguration.
			textEmbeddingProviderConfigurationJSONs();
	}

	private void _addTextEmbeddingProvider(
		String name, TextEmbeddingProvider textEmbeddingProvider) {

		if (_log.isDebugEnabled()) {
			_log.debug("Enabling " + name);
		}

		_textEmbeddingProviders.put(name, textEmbeddingProvider);
	}

	private void _addTextEmbeddingProvider(
		String[] disabledProviders, String name,
		TextEmbeddingProvider textEmbeddingProvider) {

		if (ArrayUtil.contains(disabledProviders, name)) {
			if (_log.isDebugEnabled()) {
				_log.debug("Disabling " + name);
			}

			return;
		}

		_addTextEmbeddingProvider(name, textEmbeddingProvider);
	}

	private String _getTextExcerpt(
		EmbeddingProviderConfiguration embeddingProviderConfiguration,
		String text) {

		Map<String, Object> attributes =
			(Map<String, Object>)embeddingProviderConfiguration.getAttributes();

		int defaultMaxCharacterCount = 1000;
		String defaultTextTruncationStrategy = "beginning";

		if (attributes == null) {
			return TextExtractionUtil.extractSentences(
				defaultMaxCharacterCount, text, defaultTextTruncationStrategy);
		}

		return TextExtractionUtil.extractSentences(
			MapUtil.getInteger(
				attributes, "maxCharacterCount", defaultMaxCharacterCount),
			text,
			MapUtil.getString(
				attributes, "textTruncationStrategy",
				defaultTextTruncationStrategy));
	}

	private void _removeTextEmbeddingProvider(String name) {
		if (!_textEmbeddingProviders.containsKey(name)) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Removing " + name);
		}

		_textEmbeddingProviders.remove(name);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TextEmbeddingRetrieverImpl.class);

	private String[] _disabledProviders;

	@Reference(target = "(provider.name=HuggingFaceInferenceEndpoint)")
	private TextEmbeddingProvider _huggingFaceInferenceEndpointTextProvider;

	@Reference(target = "(provider.name=HuggingFaceInferenceAPI)")
	private TextEmbeddingProvider _huggingFaceInterfaceApiTextProvider;

	@Reference(target = "(provider.name=OpenAI)")
	private TextEmbeddingProvider _openAITextEmbeddingTextProvider;

	@Reference
	private SemanticSearchConfigurationProvider
		_semanticSearchConfigurationProvider;

	private ServiceRegistration<?> _serviceRegistration;
	private final Map<String, TextEmbeddingProvider> _textEmbeddingProviders =
		new ConcurrentHashMap<>();

	@Reference(target = "(provider.name=TxtAIText)")
	private TextEmbeddingProvider _txtAITextEmbeddingTextProvider;

	@Reference(target = "(provider.name=VertexAI)")
	private TextEmbeddingProvider _vertexAITextEmbeddingTextProvider;

}