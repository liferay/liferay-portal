/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.suggest;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.query.QueryTranslator;
import com.liferay.portal.kernel.search.suggest.CompletionSuggester;
import com.liferay.portal.kernel.search.suggest.PhraseSuggester;
import com.liferay.portal.kernel.search.suggest.Suggester;
import com.liferay.portal.kernel.search.suggest.SuggesterTranslator;
import com.liferay.portal.kernel.search.suggest.SuggesterVisitor;
import com.liferay.portal.kernel.search.suggest.TermSuggester;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch7.internal.legacy.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.index.IndexNameBuilder;

import java.util.Set;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.suggest.SortBy;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.phrase.DirectCandidateGeneratorBuilder;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;

/**
 * @author Michael C. Han
 */
public class ElasticsearchSuggesterTranslator
	implements SuggesterTranslator<SuggestionBuilder>,
			   SuggesterVisitor<SuggestionBuilder> {

	public ElasticsearchSuggesterTranslator(IndexNameBuilder indexNameBuilder) {
		_queryTranslator = new ElasticsearchQueryTranslator(indexNameBuilder);
	}

	@Override
	public SuggestionBuilder translate(
		Suggester suggester, SearchContext searchContext) {

		return suggester.accept(this);
	}

	@Override
	public SuggestionBuilder visit(CompletionSuggester completionSuggester) {
		CompletionSuggestionBuilder completionSuggesterBuilder =
			SuggestBuilders.completionSuggestion(
				completionSuggester.getField());

		if (Validator.isNotNull(completionSuggester.getAnalyzer())) {
			completionSuggesterBuilder.analyzer(
				completionSuggester.getAnalyzer());
		}

		if (completionSuggester.getShardSize() != null) {
			completionSuggesterBuilder.shardSize(
				completionSuggester.getShardSize());
		}

		if (completionSuggester.getSize() != null) {
			completionSuggesterBuilder.size(completionSuggester.getSize());
		}

		completionSuggesterBuilder.text(completionSuggester.getValue());

		return completionSuggesterBuilder;
	}

	@Override
	public SuggestionBuilder visit(PhraseSuggester phraseSuggester) {
		PhraseSuggestionBuilder phraseSuggestionBuilder =
			SuggestBuilders.phraseSuggestion(phraseSuggester.getField());

		if (Validator.isNotNull(phraseSuggester.getAnalyzer())) {
			phraseSuggestionBuilder.analyzer(phraseSuggester.getAnalyzer());
		}

		_translate(
			phraseSuggester.getCandidateGenerators(), phraseSuggestionBuilder);

		_translate(phraseSuggester.getCollate(), phraseSuggestionBuilder);

		if (phraseSuggester.getConfidence() != null) {
			phraseSuggestionBuilder.confidence(phraseSuggester.getConfidence());
		}

		if (phraseSuggester.isForceUnigrams() != null) {
			phraseSuggestionBuilder.forceUnigrams(
				phraseSuggester.isForceUnigrams());
		}

		if (phraseSuggester.getGramSize() != null) {
			phraseSuggestionBuilder.gramSize(phraseSuggester.getGramSize());
		}

		if (phraseSuggester.getMaxErrors() != null) {
			phraseSuggestionBuilder.maxErrors(phraseSuggester.getMaxErrors());
		}

		if (Validator.isNotNull(phraseSuggester.getPostHighlightFilter()) &&
			Validator.isNotNull(phraseSuggester.getPreHighlightFilter())) {

			phraseSuggestionBuilder.highlight(
				phraseSuggester.getPreHighlightFilter(),
				phraseSuggester.getPostHighlightFilter());
		}

		if (phraseSuggester.getRealWordErrorLikelihood() != null) {
			phraseSuggestionBuilder.realWordErrorLikelihood(
				phraseSuggester.getRealWordErrorLikelihood());
		}

		if (phraseSuggester.getSeparator() != null) {
			phraseSuggestionBuilder.separator(phraseSuggester.getSeparator());
		}

		if (phraseSuggester.getShardSize() != null) {
			phraseSuggestionBuilder.shardSize(phraseSuggester.getShardSize());
		}

		if (phraseSuggester.getSize() != null) {
			phraseSuggestionBuilder.size(phraseSuggester.getSize());
		}

		if (phraseSuggester.getTokenLimit() != null) {
			phraseSuggestionBuilder.tokenLimit(phraseSuggester.getTokenLimit());
		}

		phraseSuggestionBuilder.text(phraseSuggester.getValue());

		return phraseSuggestionBuilder;
	}

	@Override
	public SuggestionBuilder visit(TermSuggester termSuggester) {
		TermSuggestionBuilder termSuggesterBuilder =
			SuggestBuilders.termSuggestion(termSuggester.getField());

		if (Validator.isNotNull(termSuggester.getAnalyzer())) {
			termSuggesterBuilder.analyzer(termSuggester.getAnalyzer());
		}

		if (termSuggester.getAccuracy() != null) {
			termSuggesterBuilder.accuracy(termSuggester.getAccuracy());
		}

		if (termSuggester.getMaxEdits() != null) {
			termSuggesterBuilder.maxEdits(termSuggester.getMaxEdits());
		}

		if (termSuggester.getMaxInspections() != null) {
			termSuggesterBuilder.maxInspections(
				termSuggester.getMaxInspections());
		}

		if (termSuggester.getMaxTermFreq() != null) {
			termSuggesterBuilder.maxTermFreq(termSuggester.getMaxTermFreq());
		}

		if (termSuggester.getMinWordLength() != null) {
			termSuggesterBuilder.minWordLength(
				termSuggester.getMinWordLength());
		}

		if (termSuggester.getMinDocFreq() != null) {
			termSuggesterBuilder.minDocFreq(termSuggester.getMinDocFreq());
		}

		if (termSuggester.getPrefixLength() != null) {
			termSuggesterBuilder.prefixLength(termSuggester.getPrefixLength());
		}

		if (termSuggester.getShardSize() != null) {
			termSuggesterBuilder.shardSize(termSuggester.getShardSize());
		}

		if (termSuggester.getSize() != null) {
			termSuggesterBuilder.size(termSuggester.getSize());
		}

		if (termSuggester.getSort() != null) {
			termSuggesterBuilder.sort(_translateSort(termSuggester.getSort()));
		}

		if (termSuggester.getStringDistance() != null) {
			termSuggesterBuilder.stringDistance(
				_translateDistance(termSuggester.getStringDistance()));
		}

		if (termSuggester.getSuggestMode() != null) {
			termSuggesterBuilder.suggestMode(
				_translateMode(termSuggester.getSuggestMode()));
		}

		termSuggesterBuilder.text(termSuggester.getValue());

		return termSuggesterBuilder;
	}

	private void _translate(
		PhraseSuggester.Collate collate,
		PhraseSuggestionBuilder phraseSuggestionBuilder) {

		if (collate != null) {
			QueryBuilder queryBuilder = _queryTranslator.translate(
				collate.getQuery(), null);

			phraseSuggestionBuilder.collateParams(collate.getParams());

			if (collate.isPrune() != null) {
				phraseSuggestionBuilder.collatePrune(collate.isPrune());
			}

			phraseSuggestionBuilder.collateQuery(queryBuilder.toString());
		}
	}

	private void _translate(
		Set<PhraseSuggester.CandidateGenerator> candidateGenerators,
		PhraseSuggestionBuilder phraseSuggestionBuilder) {

		for (PhraseSuggester.CandidateGenerator candidateGenerator :
				candidateGenerators) {

			DirectCandidateGeneratorBuilder directCandidateGenerator =
				new DirectCandidateGeneratorBuilder(
					candidateGenerator.getField());

			if (candidateGenerator.getAccuracy() != null) {
				directCandidateGenerator.accuracy(
					candidateGenerator.getAccuracy());
			}

			if (candidateGenerator.getMaxEdits() != null) {
				directCandidateGenerator.maxEdits(
					candidateGenerator.getMaxEdits());
			}

			if (candidateGenerator.getMaxInspections() != null) {
				directCandidateGenerator.maxInspections(
					candidateGenerator.getMaxInspections());
			}

			if (candidateGenerator.getMaxTermFreq() != null) {
				directCandidateGenerator.maxTermFreq(
					candidateGenerator.getMaxTermFreq());
			}

			if (candidateGenerator.getMinWordLength() != null) {
				directCandidateGenerator.minWordLength(
					candidateGenerator.getMinWordLength());
			}

			if (candidateGenerator.getMinDocFreq() != null) {
				directCandidateGenerator.minDocFreq(
					candidateGenerator.getMinDocFreq());
			}

			if (candidateGenerator.getPrefixLength() != null) {
				directCandidateGenerator.prefixLength(
					candidateGenerator.getPrefixLength());
			}

			if (Validator.isNotNull(
					candidateGenerator.getPostFilterAnalyzer())) {

				directCandidateGenerator.postFilter(
					candidateGenerator.getPostFilterAnalyzer());
			}

			if (Validator.isNotNull(
					candidateGenerator.getPreFilterAnalyzer())) {

				directCandidateGenerator.preFilter(
					candidateGenerator.getPreFilterAnalyzer());
			}

			if (candidateGenerator.getSize() != null) {
				directCandidateGenerator.size(candidateGenerator.getSize());
			}

			if (candidateGenerator.getSort() != null) {
				directCandidateGenerator.sort(
					_translate(candidateGenerator.getSort()));
			}

			if (candidateGenerator.getStringDistance() != null) {
				directCandidateGenerator.stringDistance(
					_translate(candidateGenerator.getStringDistance()));
			}

			if (candidateGenerator.getSuggestMode() != null) {
				directCandidateGenerator.suggestMode(
					_translate(candidateGenerator.getSuggestMode()));
			}

			phraseSuggestionBuilder.addCandidateGenerator(
				directCandidateGenerator);
		}
	}

	private String _translate(Suggester.Sort sort) {
		if (sort == Suggester.Sort.FREQUENCY) {
			return "frequency";
		}

		return "score";
	}

	private String _translate(Suggester.StringDistance stringDistance) {
		if (stringDistance == Suggester.StringDistance.DAMERAU_LEVENSHTEIN) {
			return "damerau_levnshtein";
		}
		else if (stringDistance == Suggester.StringDistance.JAROWINKLER) {
			return "jarowinkler";
		}
		else if (stringDistance == Suggester.StringDistance.LEVENSTEIN) {
			return "levenstein";
		}
		else if (stringDistance == Suggester.StringDistance.NGRAM) {
			return "ngram";
		}

		return "internal";
	}

	private String _translate(Suggester.SuggestMode suggestMode) {
		if (suggestMode == Suggester.SuggestMode.ALWAYS) {
			return "always";
		}
		else if (suggestMode == Suggester.SuggestMode.POPULAR) {
			return "popular";
		}

		return "missing";
	}

	private TermSuggestionBuilder.StringDistanceImpl _translateDistance(
		Suggester.StringDistance stringDistance) {

		if (stringDistance == Suggester.StringDistance.DAMERAU_LEVENSHTEIN) {
			return TermSuggestionBuilder.StringDistanceImpl.DAMERAU_LEVENSHTEIN;
		}
		else if (stringDistance == Suggester.StringDistance.JAROWINKLER) {
			return TermSuggestionBuilder.StringDistanceImpl.JARO_WINKLER;
		}
		else if (stringDistance == Suggester.StringDistance.LEVENSTEIN) {
			return TermSuggestionBuilder.StringDistanceImpl.LEVENSHTEIN;
		}
		else if (stringDistance == Suggester.StringDistance.NGRAM) {
			return TermSuggestionBuilder.StringDistanceImpl.NGRAM;
		}

		return TermSuggestionBuilder.StringDistanceImpl.INTERNAL;
	}

	private TermSuggestionBuilder.SuggestMode _translateMode(
		Suggester.SuggestMode suggestMode) {

		if (suggestMode == Suggester.SuggestMode.ALWAYS) {
			return TermSuggestionBuilder.SuggestMode.ALWAYS;
		}
		else if (suggestMode == Suggester.SuggestMode.POPULAR) {
			return TermSuggestionBuilder.SuggestMode.POPULAR;
		}

		return TermSuggestionBuilder.SuggestMode.MISSING;
	}

	private SortBy _translateSort(Suggester.Sort sort) {
		if (sort == Suggester.Sort.FREQUENCY) {
			return SortBy.FREQUENCY;
		}

		return SortBy.SCORE;
	}

	private final QueryTranslator<QueryBuilder> _queryTranslator;

}