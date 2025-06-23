/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.adapter.index.AnalysisIndexResponseToken;
import com.liferay.portal.search.engine.adapter.index.AnalyzeIndexRequest;
import com.liferay.portal.search.engine.adapter.index.AnalyzeIndexResponse;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.analysis.CharFilter;
import org.opensearch.client.opensearch._types.analysis.TokenFilter;
import org.opensearch.client.opensearch._types.analysis.Tokenizer;
import org.opensearch.client.opensearch.indices.AnalyzeRequest;
import org.opensearch.client.opensearch.indices.AnalyzeResponse;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;
import org.opensearch.client.opensearch.indices.analyze.AnalyzeDetail;
import org.opensearch.client.opensearch.indices.analyze.AnalyzeToken;
import org.opensearch.client.opensearch.indices.analyze.AnalyzerDetail;
import org.opensearch.client.opensearch.indices.analyze.CharFilterDetail;
import org.opensearch.client.opensearch.indices.analyze.ExplainAnalyzeToken;
import org.opensearch.client.opensearch.indices.analyze.TokenDetail;

/**
 * @author Michael C. Han
 */
public class AnalyzeIndexRequestExecutor {

	public AnalyzeIndexRequestExecutor(
		OpenSearchConnectionManager openSearchConnectionManager) {

		_openSearchConnectionManager = openSearchConnectionManager;
	}

	public AnalyzeIndexResponse execute(
		AnalyzeIndexRequest analyzeIndexRequest) {

		AnalyzeIndexResponse analyzeIndexResponse = new AnalyzeIndexResponse();

		AnalyzeResponse analyzeResponse = _getAnalyzeResponse(
			analyzeIndexRequest, createAnalyzeRequest(analyzeIndexRequest));

		if (analyzeResponse.detail() != null) {
			_processAnalyzeDetail(
				analyzeResponse.detail(), analyzeIndexResponse);
		}
		else {
			List<AnalysisIndexResponseToken> analysisIndexResponseTokens =
				_translateAnalyzeTokens(analyzeResponse.tokens());

			analyzeIndexResponse.addAnalysisIndexResponseTokens(
				analysisIndexResponseTokens);
		}

		return analyzeIndexResponse;
	}

	protected AnalyzeRequest createAnalyzeRequest(
		AnalyzeIndexRequest analyzeIndexRequest) {

		AnalyzeRequest.Builder builder = new AnalyzeRequest.Builder();

		builder.attributes(
			ListUtil.fromArray(analyzeIndexRequest.getAttributesArray()));
		builder.explain(analyzeIndexRequest.isExplain());
		builder.index(analyzeIndexRequest.getIndexName());
		builder.text(ListUtil.fromArray(analyzeIndexRequest.getTexts()));

		if (Validator.isNotNull(analyzeIndexRequest.getAnalyzer())) {
			builder.analyzer(analyzeIndexRequest.getAnalyzer());
		}
		else if (Validator.isNotNull(analyzeIndexRequest.getFieldName())) {
			builder.field(analyzeIndexRequest.getFieldName());
		}
		else if (Validator.isNotNull(analyzeIndexRequest.getNormalizer())) {
			builder.normalizer(analyzeIndexRequest.getNormalizer());
		}
		else {
			if (Validator.isNotNull(analyzeIndexRequest.getTokenizer())) {
				builder.tokenizer(
					Tokenizer.of(
						tokenizer -> tokenizer.name(
							analyzeIndexRequest.getTokenizer())));
			}

			for (String charFilter : analyzeIndexRequest.getCharFilters()) {
				builder.charFilter(
					CharFilter.of(
						openSearchCharFilter -> openSearchCharFilter.name(
							charFilter)));
			}

			for (String tokenFilter : analyzeIndexRequest.getTokenFilters()) {
				builder.filter(
					TokenFilter.of(
						openSearchTokenFilter -> openSearchTokenFilter.name(
							tokenFilter)));
			}
		}

		return builder.build();
	}

	private AnalyzeResponse _getAnalyzeResponse(
		AnalyzeIndexRequest analyzeIndexRequest,
		AnalyzeRequest analyzeRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				analyzeIndexRequest.getConnectionId(),
				analyzeIndexRequest.isPreferLocalCluster());

		OpenSearchIndicesClient openSearchIndicesClient =
			openSearchClient.indices();

		try {
			return openSearchIndicesClient.analyze(analyzeRequest);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _processAnalyzeDetail(
		AnalyzeDetail analyzeDetail,
		AnalyzeIndexResponse analyzeIndexResponse) {

		if (analyzeDetail.analyzer() != null) {
			AnalyzerDetail analyzerDetail = analyzeDetail.analyzer();

			AnalyzeIndexResponse.DetailsAnalyzer detailsAnalyzer =
				new AnalyzeIndexResponse.DetailsAnalyzer(
					analyzerDetail.name(),
					_translateExplainAnalyzeTokens(analyzerDetail.tokens()));

			analyzeIndexResponse.setDetailsAnalyzer(detailsAnalyzer);
		}
		else {
			List<AnalyzeIndexResponse.DetailsCharFilter> detailsCharFilters =
				new ArrayList<>();

			for (CharFilterDetail charFilterDetail :
					analyzeDetail.charfilters()) {

				String charFilterName = charFilterDetail.name();
				String[] charFilterTexts = ArrayUtil.toStringArray(
					charFilterDetail.filteredText());

				AnalyzeIndexResponse.DetailsCharFilter detailsCharFilter =
					new AnalyzeIndexResponse.DetailsCharFilter(
						charFilterName, charFilterTexts);

				detailsCharFilters.add(detailsCharFilter);
			}

			analyzeIndexResponse.setDetailsCharFilters(detailsCharFilters);

			List<AnalyzeIndexResponse.DetailsTokenFilter> detailsTokenFilters =
				new ArrayList<>();

			for (TokenDetail tokenDetail : analyzeDetail.tokenfilters()) {
				AnalyzeIndexResponse.DetailsTokenFilter detailsTokenFilter =
					new AnalyzeIndexResponse.DetailsTokenFilter(
						tokenDetail.name(),
						_translateExplainAnalyzeTokens(tokenDetail.tokens()));

				detailsTokenFilters.add(detailsTokenFilter);
			}

			analyzeIndexResponse.setDetailsTokenFilters(detailsTokenFilters);

			TokenDetail tokenDetail = analyzeDetail.tokenizer();

			AnalyzeIndexResponse.DetailsTokenizer detailsTokenizer =
				new AnalyzeIndexResponse.DetailsTokenizer(
					tokenDetail.name(),
					_translateExplainAnalyzeTokens(tokenDetail.tokens()));

			analyzeIndexResponse.setDetailsTokenizer(detailsTokenizer);
		}
	}

	private List<AnalysisIndexResponseToken> _translateAnalyzeTokens(
		List<AnalyzeToken> analyzeTokens) {

		return TransformUtil.transform(
			analyzeTokens,
			analyzeToken -> {
				AnalysisIndexResponseToken analysisIndexResponseToken =
					new AnalysisIndexResponseToken(analyzeToken.token());

				analysisIndexResponseToken.setEndOffset(
					Math.toIntExact(analyzeToken.endOffset()));
				analysisIndexResponseToken.setPosition(
					Math.toIntExact(analyzeToken.position()));

				if (analyzeToken.positionLength() != null) {
					analysisIndexResponseToken.setPositionLength(
						Math.toIntExact(analyzeToken.positionLength()));
				}

				analysisIndexResponseToken.setStartOffset(
					Math.toIntExact(analyzeToken.startOffset()));
				analysisIndexResponseToken.setType(analyzeToken.type());

				return analysisIndexResponseToken;
			});
	}

	private List<AnalysisIndexResponseToken> _translateExplainAnalyzeTokens(
		List<ExplainAnalyzeToken> explainAnalyzeTokens) {

		return TransformUtil.transform(
			explainAnalyzeTokens,
			explainAnalyzeToken -> {
				AnalysisIndexResponseToken analysisIndexResponseToken =
					new AnalysisIndexResponseToken(explainAnalyzeToken.token());

				analysisIndexResponseToken.setEndOffset(
					Math.toIntExact(explainAnalyzeToken.endOffset()));
				analysisIndexResponseToken.setPosition(
					Math.toIntExact(explainAnalyzeToken.position()));
				analysisIndexResponseToken.setPositionLength(
					Math.toIntExact(explainAnalyzeToken.positionlength()));
				analysisIndexResponseToken.setStartOffset(
					Math.toIntExact(explainAnalyzeToken.startOffset()));
				analysisIndexResponseToken.setType(explainAnalyzeToken.type());

				return analysisIndexResponseToken;
			});
	}

	private final OpenSearchConnectionManager _openSearchConnectionManager;

}