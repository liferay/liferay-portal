/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.display.context;

import java.util.List;
import java.util.Map;

/**
 * @author Petteri Karttunen
 */
public class SemanticSearchCompanyConfigurationDisplayContext {

	public List<String> getAvailableEmbeddingVectorDimensions() {
		return _availableEmbeddingVectorDimensions;
	}

	public Map<String, String> getAvailableLanguageDisplayNames() {
		return _availableLanguageDisplayNames;
	}

	public Map<String, String> getAvailableModelClassNames() {
		return _availableModelClassNames;
	}

	public Map<String, String> getAvailableTextEmbeddingProviders() {
		return _availableTextEmbeddingProviders;
	}

	public Map<String, String> getAvailableTextTruncationStrategies() {
		return _availableTextTruncationStrategies;
	}

	public int getTextEmbeddingCacheTimeout() {
		return _textEmbeddingCacheTimeout;
	}

	public String[] getTextEmbeddingProviderConfigurationJSONs() {
		return _textEmbeddingProviderConfigurationJSONs;
	}

	public boolean isTextEmbeddingsEnabled() {
		return _textEmbeddingsEnabled;
	}

	public void setAvailableEmbeddingVectorDimensions(
		List<String> availableEmbeddingVectorDimensions) {

		_availableEmbeddingVectorDimensions =
			availableEmbeddingVectorDimensions;
	}

	public void setAvailableLanguageDisplayNames(
		Map<String, String> availableLanguageDisplayNames) {

		_availableLanguageDisplayNames = availableLanguageDisplayNames;
	}

	public void setAvailableModelClassNames(
		Map<String, String> availableModelClassNames) {

		_availableModelClassNames = availableModelClassNames;
	}

	public void setAvailableTextEmbeddingProviders(
		Map<String, String> availableTextEmbeddingProviders) {

		_availableTextEmbeddingProviders = availableTextEmbeddingProviders;
	}

	public void setAvailableTextTruncationStrategies(
		Map<String, String> availableTextTruncationStrategies) {

		_availableTextTruncationStrategies = availableTextTruncationStrategies;
	}

	public void setTextEmbeddingCacheTimeout(int textEmbeddingCacheTimeout) {
		_textEmbeddingCacheTimeout = textEmbeddingCacheTimeout;
	}

	public void setTextEmbeddingProviderConfigurationJSONs(
		String[] textEmbeddingProviderConfigurationJSONs) {

		_textEmbeddingProviderConfigurationJSONs =
			textEmbeddingProviderConfigurationJSONs;
	}

	public void setTextEmbeddingsEnabled(boolean textEmbeddingsEnabled) {
		_textEmbeddingsEnabled = textEmbeddingsEnabled;
	}

	private List<String> _availableEmbeddingVectorDimensions;
	private Map<String, String> _availableLanguageDisplayNames;
	private Map<String, String> _availableModelClassNames;
	private Map<String, String> _availableTextEmbeddingProviders;
	private Map<String, String> _availableTextTruncationStrategies;
	private int _textEmbeddingCacheTimeout;
	private String[] _textEmbeddingProviderConfigurationJSONs;
	private boolean _textEmbeddingsEnabled;

}