/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.blueprint.parameter.contributor;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.configuration.SemanticSearchConfiguration;
import com.liferay.portal.search.configuration.SemanticSearchConfigurationProvider;
import com.liferay.portal.search.ml.embedding.text.TextEmbeddingRetriever;
import com.liferay.portal.search.rest.dto.v1_0.EmbeddingProviderConfiguration;
import com.liferay.search.experiences.blueprint.parameter.SXPParameter;
import com.liferay.search.experiences.blueprint.parameter.contributor.SXPParameterContributor;
import com.liferay.search.experiences.blueprint.parameter.contributor.SXPParameterContributorDefinition;
import com.liferay.search.experiences.internal.blueprint.parameter.DoubleArraySXPParameter;
import com.liferay.search.experiences.internal.blueprint.parameter.IntegerSXPParameter;

import java.beans.ExceptionListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author Petteri Karttunen
 */
public class MLSXPParameterContributor implements SXPParameterContributor {

	public MLSXPParameterContributor(
		Language language,
		SemanticSearchConfigurationProvider semanticSearchConfigurationProvider,
		TextEmbeddingRetriever textEmbeddingRetriever) {

		_language = language;
		_semanticSearchConfigurationProvider =
			semanticSearchConfigurationProvider;
		_textEmbeddingRetriever = textEmbeddingRetriever;
	}

	@Override
	public void contribute(
		ExceptionListener exceptionListener, SearchContext searchContext,
		Set<SXPParameter> sxpParameters) {

		_addTextEmbeddingParameters(
			exceptionListener, searchContext, sxpParameters);
	}

	@Override
	public String getSXPParameterCategoryNameKey() {
		return "ml";
	}

	@Override
	public List<SXPParameterContributorDefinition>
		getSXPParameterContributorDefinitions(long companyId, Locale locale) {

		return _getTextEmbeddingParameterContributorDefinitions(
			companyId, locale);
	}

	private void _addTextEmbeddingParameters(
		ExceptionListener exceptionListener, SearchContext searchContext,
		Set<SXPParameter> sxpParameters) {

		EmbeddingProviderConfiguration embeddingProviderConfiguration =
			_getEmbeddingProviderConfiguration(
				exceptionListener,
				_getSemanticSearchConfiguration(searchContext.getCompanyId()));

		if (embeddingProviderConfiguration == null) {
			return;
		}

		sxpParameters.add(
			new IntegerSXPParameter(
				"ml.text_embeddings.vector_dimensions", true,
				embeddingProviderConfiguration.getEmbeddingVectorDimensions()));

		Double[] textEmbedding = _textEmbeddingRetriever.getTextEmbedding(
			embeddingProviderConfiguration.getProviderName(),
			searchContext.getKeywords());

		if (ArrayUtil.isEmpty(textEmbedding)) {
			return;
		}

		sxpParameters.add(
			new DoubleArraySXPParameter(
				"ml.text_embeddings.keywords_embedding", true, textEmbedding));
	}

	private EmbeddingProviderConfiguration _getEmbeddingProviderConfiguration(
		ExceptionListener exceptionListener,
		SemanticSearchConfiguration semanticSearchConfiguration) {

		if (!semanticSearchConfiguration.textEmbeddingsEnabled()) {
			return null;
		}

		try {
			for (String textEmbeddingProviderConfigurationJSON :
					semanticSearchConfiguration.
						textEmbeddingProviderConfigurationJSONs()) {

				return EmbeddingProviderConfiguration.unsafeToDTO(
					textEmbeddingProviderConfigurationJSON);
			}
		}
		catch (Exception exception) {
			if (exceptionListener != null) {
				exceptionListener.exceptionThrown(exception);
			}

			return ReflectionUtil.throwException(exception);
		}

		return null;
	}

	private EmbeddingProviderConfiguration _getEmbeddingProviderConfiguration(
		SemanticSearchConfiguration semanticSearchConfiguration) {

		return _getEmbeddingProviderConfiguration(
			null, semanticSearchConfiguration);
	}

	private SemanticSearchConfiguration _getSemanticSearchConfiguration(
		long companyId) {

		return _semanticSearchConfigurationProvider.getCompanyConfiguration(
			companyId);
	}

	private List<SXPParameterContributorDefinition>
		_getTextEmbeddingParameterContributorDefinitions(
			long companyId, Locale locale) {

		EmbeddingProviderConfiguration embeddingProviderConfiguration =
			_getEmbeddingProviderConfiguration(
				_getSemanticSearchConfiguration(companyId));

		if (embeddingProviderConfiguration == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(
			new SXPParameterContributorDefinition(
				IntegerSXPParameter.class,
				StringBundler.concat(
					_language.get(locale, "text-embedding-vector-dimensions"),
					" (", embeddingProviderConfiguration.getProviderName(),
					")"),
				"ml.text_embeddings.vector_dimensions"),
			new SXPParameterContributorDefinition(
				DoubleArraySXPParameter.class,
				StringBundler.concat(
					_language.get(locale, "keywords-embedding"), " (",
					embeddingProviderConfiguration.getProviderName(), ")"),
				"ml.text_embeddings.keywords_embedding"));
	}

	private final Language _language;
	private final SemanticSearchConfigurationProvider
		_semanticSearchConfigurationProvider;
	private final TextEmbeddingRetriever _textEmbeddingRetriever;

}