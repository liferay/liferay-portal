/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.engine.adapter.index;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael C. Han
 */
public class AnalyzeIndexResponse implements IndexResponse {

	public void addAnalysisIndexResponseTokens(
		AnalysisIndexResponseToken analysisIndexResponseToken) {

		_analysisIndexResponseTokens.add(analysisIndexResponseToken);
	}

	public void addAnalysisIndexResponseTokens(
		List<AnalysisIndexResponseToken> analysisIndexResponseToken) {

		_analysisIndexResponseTokens.addAll(analysisIndexResponseToken);
	}

	public List<AnalysisIndexResponseToken> getAnalysisIndexResponseTokens() {
		return _analysisIndexResponseTokens;
	}

	public DetailsAnalyzer getDetailsAnalyzer() {
		return _detailsAnalyzer;
	}

	public List<DetailsCharFilter> getDetailsCharFilters() {
		return _detailsCharFilters;
	}

	public List<DetailsTokenFilter> getDetailsTokenFilters() {
		return _detailsTokenFilters;
	}

	public DetailsTokenizer getDetailsTokenizer() {
		return _detailsTokenizer;
	}

	public void setDetailsAnalyzer(DetailsAnalyzer detailsAnalyzer) {
		_detailsAnalyzer = detailsAnalyzer;
	}

	public void setDetailsCharFilters(
		List<DetailsCharFilter> detailsCharFilters) {

		_detailsCharFilters = detailsCharFilters;
	}

	public void setDetailsTokenFilters(
		List<DetailsTokenFilter> detailsTokenFilters) {

		_detailsTokenFilters = detailsTokenFilters;
	}

	public void setDetailsTokenizer(DetailsTokenizer detailsTokenizer) {
		_detailsTokenizer = detailsTokenizer;
	}

	public static class DetailsAnalyzer {

		public DetailsAnalyzer(
			String analyzerName,
			List<AnalysisIndexResponseToken> analysisIndexResponseTokens) {

			_analyzerName = analyzerName;
			_analysisIndexResponseTokens = analysisIndexResponseTokens;
		}

		public List<AnalysisIndexResponseToken>
			getAnalysisIndexResponseTokens() {

			return _analysisIndexResponseTokens;
		}

		public String getAnalyzerName() {
			return _analyzerName;
		}

		private List<AnalysisIndexResponseToken> _analysisIndexResponseTokens;
		private final String _analyzerName;

	}

	public static class DetailsCharFilter {

		public DetailsCharFilter(
			String charFilterName, String[] charFilterTexts) {

			_charFilterName = charFilterName;
			_charFilterTexts = charFilterTexts;
		}

		public String getCharFilterName() {
			return _charFilterName;
		}

		public String[] getCharFilterTexts() {
			return _charFilterTexts;
		}

		private final String _charFilterName;
		private final String[] _charFilterTexts;

	}

	public static class DetailsTokenFilter {

		public DetailsTokenFilter(
			String tokenFilterName,
			List<AnalysisIndexResponseToken> analysisIndexResponseTokens) {

			_tokenFilterName = tokenFilterName;
			_analysisIndexResponseTokens = analysisIndexResponseTokens;
		}

		public List<AnalysisIndexResponseToken>
			getAnalysisIndexResponseTokens() {

			return _analysisIndexResponseTokens;
		}

		public String getTokenFilterName() {
			return _tokenFilterName;
		}

		private List<AnalysisIndexResponseToken> _analysisIndexResponseTokens;
		private final String _tokenFilterName;

	}

	public static class DetailsTokenizer {

		public DetailsTokenizer(
			String tokenizerName,
			List<AnalysisIndexResponseToken> analysisIndexResponseTokens) {

			_tokenizerName = tokenizerName;
			_analysisIndexResponseTokens = analysisIndexResponseTokens;
		}

		public List<AnalysisIndexResponseToken>
			getAnalysisIndexResponseTokens() {

			return _analysisIndexResponseTokens;
		}

		public String getTokenizerName() {
			return _tokenizerName;
		}

		private List<AnalysisIndexResponseToken> _analysisIndexResponseTokens;
		private final String _tokenizerName;

	}

	private final List<AnalysisIndexResponseToken>
		_analysisIndexResponseTokens = new ArrayList<>();
	private DetailsAnalyzer _detailsAnalyzer;
	private List<DetailsCharFilter> _detailsCharFilters;
	private List<DetailsTokenFilter> _detailsTokenFilters;
	private DetailsTokenizer _detailsTokenizer;

}