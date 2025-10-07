/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.search;

import com.liferay.portal.kernel.search.suggest.Suggester;
import com.liferay.portal.kernel.search.suggest.SuggesterTranslator;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.engine.adapter.search.SuggestSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SuggestSearchResponse;
import com.liferay.portal.search.engine.adapter.search.SuggestSearchResult;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.suggest.OpenSearchSuggesterTranslator;
import com.liferay.portal.search.opensearch2.internal.util.ConversionUtil;
import com.liferay.portal.search.opensearch2.internal.util.SetterUtil;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.CompletionSuggest;
import org.opensearch.client.opensearch.core.search.CompletionSuggestOption;
import org.opensearch.client.opensearch.core.search.FieldSuggester;
import org.opensearch.client.opensearch.core.search.PhraseSuggest;
import org.opensearch.client.opensearch.core.search.PhraseSuggestOption;
import org.opensearch.client.opensearch.core.search.Suggest;
import org.opensearch.client.opensearch.core.search.Suggester.Builder;
import org.opensearch.client.opensearch.core.search.TermSuggest;
import org.opensearch.client.opensearch.core.search.TermSuggestOption;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Petteri Karttunen
 */
@Component(service = SuggestSearchRequestExecutor.class)
public class SuggestSearchRequestExecutorImpl
	implements SuggestSearchRequestExecutor {

	@Override
	public SuggestSearchResponse execute(
		SuggestSearchRequest suggestSearchRequest) {

		SearchResponse<JsonData> searchResponse = getSearchResponse(
			_createSearchRequest(suggestSearchRequest), suggestSearchRequest);

		Map<String, List<Suggest<JsonData>>> suggests =
			searchResponse.suggest();

		SuggestSearchResponse suggestSearchResponse =
			new SuggestSearchResponse();

		if (MapUtil.isEmpty(suggests)) {
			return suggestSearchResponse;
		}

		for (Map.Entry<String, List<Suggest<JsonData>>> entry :
				suggests.entrySet()) {

			suggestSearchResponse.addSuggestSearchResult(
				_translateSuggests(entry.getKey(), entry.getValue()));
		}

		return suggestSearchResponse;
	}

	@Activate
	protected void activate() {
		_suggesterTranslator = new OpenSearchSuggesterTranslator(
			_indexNameBuilder);
	}

	protected SearchResponse<JsonData> getSearchResponse(
		SearchRequest searchRequest,
		SuggestSearchRequest suggestSearchRequest) {

		OpenSearchClient openSearchClient =
			_openSearchConnectionManager.getOpenSearchClient(
				suggestSearchRequest.getConnectionId(),
				suggestSearchRequest.isPreferLocalCluster());

		try {
			return openSearchClient.search(searchRequest, JsonData.class);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private SearchRequest _createSearchRequest(
		SuggestSearchRequest suggestSearchRequest) {

		SearchRequest.Builder searchRequestBuilder =
			new SearchRequest.Builder();

		searchRequestBuilder.index(
			Arrays.asList(suggestSearchRequest.getIndexNames()));

		Map<String, Suggester> suggesters =
			suggestSearchRequest.getSuggesterMap();

		Builder suggesterBuilder = new Builder();

		for (Map.Entry<String, Suggester> entry : suggesters.entrySet()) {
			suggesterBuilder.suggesters(
				entry.getKey(),
				_suggesterTranslator.translate(entry.getValue(), null));
		}

		SetterUtil.setNotBlankString(
			suggesterBuilder::text, suggestSearchRequest.getGlobalText());

		searchRequestBuilder.suggest(suggesterBuilder.build());

		return searchRequestBuilder.build();
	}

	private SuggestSearchResult.Entry _translateCompletionSuggest(
		CompletionSuggest<JsonData> completionSuggest) {

		SuggestSearchResult.Entry entry = new SuggestSearchResult.Entry(
			completionSuggest.text());

		for (CompletionSuggestOption<JsonData> completionSuggestOption :
				completionSuggest.options()) {

			entry.addOption(
				new SuggestSearchResult.Entry.Option(
					completionSuggestOption.text(),
					ConversionUtil.toFloat(completionSuggestOption.score())));
		}

		return entry;
	}

	private SuggestSearchResult.Entry _translatePhraseSuggest(
		PhraseSuggest phraseSuggest) {

		SuggestSearchResult.Entry entry = new SuggestSearchResult.Entry(
			phraseSuggest.text());

		for (PhraseSuggestOption phraseSuggestOption :
				phraseSuggest.options()) {

			SuggestSearchResult.Entry.Option option =
				new SuggestSearchResult.Entry.Option(
					phraseSuggestOption.text(),
					ConversionUtil.toFloat(phraseSuggestOption.score()));

			SetterUtil.setNotBlankString(
				option::setHighlightedText, phraseSuggestOption.highlighted());

			entry.addOption(option);
		}

		return entry;
	}

	private SuggestSearchResult _translateSuggests(
		String name, List<Suggest<JsonData>> suggests) {

		SuggestSearchResult suggestSearchResult = new SuggestSearchResult(name);

		for (Suggest<JsonData> suggest : suggests) {
			if (suggest.isCompletion()) {
				suggestSearchResult.addEntry(
					_translateCompletionSuggest(suggest.completion()));
			}
			else if (suggest.isPhrase()) {
				suggestSearchResult.addEntry(
					_translatePhraseSuggest(suggest.phrase()));
			}
			else {
				suggestSearchResult.addEntry(
					_translateTermSuggest(suggest.term()));
			}
		}

		return suggestSearchResult;
	}

	private SuggestSearchResult.Entry _translateTermSuggest(
		TermSuggest termSuggest) {

		SuggestSearchResult.Entry entry = new SuggestSearchResult.Entry(
			termSuggest.text());

		for (TermSuggestOption termSuggestOption : termSuggest.options()) {
			SuggestSearchResult.Entry.Option option =
				new SuggestSearchResult.Entry.Option(
					termSuggestOption.text(),
					ConversionUtil.toFloat(termSuggestOption.score()));

			option.setFrequency(Math.toIntExact(termSuggestOption.freq()));

			entry.addOption(option);
		}

		return entry;
	}

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private OpenSearchConnectionManager _openSearchConnectionManager;

	private SuggesterTranslator<FieldSuggester> _suggesterTranslator;

}