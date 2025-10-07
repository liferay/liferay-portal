/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.suggest;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.query.QueryTranslator;
import com.liferay.portal.kernel.search.suggest.CompletionSuggester;
import com.liferay.portal.kernel.search.suggest.PhraseSuggester;
import com.liferay.portal.kernel.search.suggest.Suggester;
import com.liferay.portal.kernel.search.suggest.SuggesterTranslator;
import com.liferay.portal.kernel.search.suggest.SuggesterVisitor;
import com.liferay.portal.kernel.search.suggest.TermSuggester;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.opensearch2.internal.legacy.query.OpenSearchQueryTranslator;
import com.liferay.portal.search.opensearch2.internal.util.ConversionUtil;
import com.liferay.portal.search.opensearch2.internal.util.JsonpUtil;
import com.liferay.portal.search.opensearch2.internal.util.SetterUtil;

import java.util.Set;

import org.opensearch.client.opensearch._types.SuggestMode;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryVariant;
import org.opensearch.client.opensearch.core.search.DirectGenerator;
import org.opensearch.client.opensearch.core.search.FieldSuggester;
import org.opensearch.client.opensearch.core.search.FieldSuggesterBuilders;
import org.opensearch.client.opensearch.core.search.PhraseSuggestCollate;
import org.opensearch.client.opensearch.core.search.PhraseSuggestCollateQuery;
import org.opensearch.client.opensearch.core.search.PhraseSuggestHighlight;
import org.opensearch.client.opensearch.core.search.StringDistance;
import org.opensearch.client.opensearch.core.search.SuggestSort;

/**
 * @author Michael C. Han
 * @author Petteri Karttunen
 */
public class OpenSearchSuggesterTranslator
	implements SuggesterTranslator<FieldSuggester>,
			   SuggesterVisitor<FieldSuggester> {

	public OpenSearchSuggesterTranslator(IndexNameBuilder indexNameBuilder) {
		_queryTranslator = new OpenSearchQueryTranslator(indexNameBuilder);
	}

	@Override
	public FieldSuggester translate(
		Suggester suggester, SearchContext searchContext) {

		return suggester.accept(this);
	}

	@Override
	public FieldSuggester visit(CompletionSuggester completionSuggester) {
		org.opensearch.client.opensearch.core.search.CompletionSuggester.Builder
			completionSuggesterBuilder = FieldSuggesterBuilders.completion();

		SetterUtil.setNotBlankString(
			completionSuggesterBuilder::analyzer,
			completionSuggester.getAnalyzer());

		completionSuggesterBuilder.field(completionSuggester.getField());

		SetterUtil.setNotNullInteger(
			completionSuggesterBuilder::size, completionSuggester.getSize());

		FieldSuggester.Builder fieldSuggesterBuilder =
			new FieldSuggester.Builder();

		fieldSuggesterBuilder.completion(completionSuggesterBuilder.build());
		fieldSuggesterBuilder.text(completionSuggester.getValue());

		return fieldSuggesterBuilder.build();
	}

	@Override
	public FieldSuggester visit(PhraseSuggester phraseSuggester) {
		FieldSuggester.Builder fieldSuggesterBuilder =
			new FieldSuggester.Builder();

		org.opensearch.client.opensearch.core.search.PhraseSuggester.Builder
			phraseSuggesterBuilder = FieldSuggesterBuilders.phrase();

		SetterUtil.setNotBlankString(
			phraseSuggesterBuilder::analyzer, phraseSuggester.getAnalyzer());

		Set<PhraseSuggester.CandidateGenerator> candidateGenerators =
			phraseSuggester.getCandidateGenerators();

		if (SetUtil.isNotEmpty(candidateGenerators)) {
			candidateGenerators.forEach(
				candidateGenerator -> phraseSuggesterBuilder.directGenerator(
					_translateCandidateGenerator(candidateGenerator)));
		}

		if (phraseSuggester.getCollate() != null) {
			phraseSuggesterBuilder.collate(
				_translateCollate(phraseSuggester.getCollate()));
		}

		SetterUtil.setNotNullFloatAsDouble(
			phraseSuggesterBuilder::confidence,
			phraseSuggester.getConfidence());

		phraseSuggesterBuilder.field(phraseSuggester.getField());

		SetterUtil.setNotNullBoolean(
			phraseSuggesterBuilder::forceUnigrams,
			phraseSuggester.isForceUnigrams());
		SetterUtil.setNotNullInteger(
			phraseSuggesterBuilder::gramSize, phraseSuggester.getGramSize());
		SetterUtil.setNotNullFloatAsDouble(
			phraseSuggesterBuilder::maxErrors, phraseSuggester.getMaxErrors());

		if (Validator.isNotNull(phraseSuggester.getPostHighlightFilter()) &&
			Validator.isNotNull(phraseSuggester.getPreHighlightFilter())) {

			phraseSuggesterBuilder.highlight(
				PhraseSuggestHighlight.of(
					phraseSuggestHighlight -> phraseSuggestHighlight.postTag(
						phraseSuggester.getPostHighlightFilter()
					).preTag(
						phraseSuggester.getPreHighlightFilter()
					)));
		}

		SetterUtil.setNotNullFloatAsDouble(
			phraseSuggesterBuilder::realWordErrorLikelihood,
			phraseSuggester.getRealWordErrorLikelihood());
		SetterUtil.setNotBlankString(
			phraseSuggesterBuilder::separator, phraseSuggester.getSeparator());
		SetterUtil.setNotNullInteger(
			phraseSuggesterBuilder::shardSize, phraseSuggester.getShardSize());
		SetterUtil.setNotNullInteger(
			phraseSuggesterBuilder::size, phraseSuggester.getSize());
		SetterUtil.setNotNullInteger(
			phraseSuggesterBuilder::tokenLimit,
			phraseSuggester.getTokenLimit());

		fieldSuggesterBuilder.phrase(phraseSuggesterBuilder.build());
		fieldSuggesterBuilder.text(phraseSuggester.getValue());

		return fieldSuggesterBuilder.build();
	}

	@Override
	public FieldSuggester visit(TermSuggester termSuggester) {
		FieldSuggester.Builder fieldSuggesterBuilder =
			new FieldSuggester.Builder();

		org.opensearch.client.opensearch.core.search.TermSuggester.Builder
			termSuggesterBuilder = FieldSuggesterBuilders.term();

		termSuggesterBuilder.field(termSuggester.getField());

		SetterUtil.setNotBlankString(
			termSuggesterBuilder::analyzer, termSuggester.getAnalyzer());
		SetterUtil.setNotNullInteger(
			termSuggesterBuilder::maxEdits, termSuggester.getMaxEdits());
		SetterUtil.setNotNullInteger(
			termSuggesterBuilder::maxInspections,
			termSuggester.getMaxInspections());
		SetterUtil.setNotNullIntegerAsFloat(
			termSuggesterBuilder::maxTermFreq, termSuggester.getMaxTermFreq());
		SetterUtil.setNotNullIntegerAsFloat(
			termSuggesterBuilder::minDocFreq, termSuggester.getMinDocFreq());
		SetterUtil.setNotNullInteger(
			termSuggesterBuilder::minWordLength,
			termSuggester.getMinWordLength());
		SetterUtil.setNotNullInteger(
			termSuggesterBuilder::prefixLength,
			termSuggester.getPrefixLength());
		SetterUtil.setNotNullInteger(
			termSuggesterBuilder::shardSize, termSuggester.getShardSize());
		SetterUtil.setNotNullInteger(
			termSuggesterBuilder::size, termSuggester.getSize());

		if (termSuggester.getSort() != null) {
			termSuggesterBuilder.sort(_translateSort(termSuggester.getSort()));
		}

		if (termSuggester.getStringDistance() != null) {
			termSuggesterBuilder.stringDistance(
				_translateStringDistance(termSuggester.getStringDistance()));
		}

		if (termSuggester.getSuggestMode() != null) {
			termSuggesterBuilder.suggestMode(
				_translateSuggestMode(termSuggester.getSuggestMode()));
		}

		fieldSuggesterBuilder.term(termSuggesterBuilder.build());
		fieldSuggesterBuilder.text(termSuggester.getValue());

		return fieldSuggesterBuilder.build();
	}

	private DirectGenerator _translateCandidateGenerator(
		PhraseSuggester.CandidateGenerator candidateGenerator) {

		DirectGenerator.Builder builder = new DirectGenerator.Builder();

		builder.field(candidateGenerator.getField());

		SetterUtil.setNotNullInteger(
			builder::maxEdits, candidateGenerator.getMaxEdits());
		SetterUtil.setNotNullIntegerAsFloat(
			builder::maxInspections, candidateGenerator.getMaxInspections());
		SetterUtil.setNotNullIntegerAsFloat(
			builder::maxTermFreq, candidateGenerator.getMaxTermFreq());
		SetterUtil.setNotNullIntegerAsFloat(
			builder::minDocFreq, candidateGenerator.getMinDocFreq());
		SetterUtil.setNotNullInteger(
			builder::minWordLength, candidateGenerator.getMinWordLength());
		SetterUtil.setNotBlankString(
			builder::postFilter, candidateGenerator.getPostFilterAnalyzer());
		SetterUtil.setNotBlankString(
			builder::preFilter, candidateGenerator.getPreFilterAnalyzer());
		SetterUtil.setNotNullInteger(
			builder::prefixLength, candidateGenerator.getPrefixLength());
		SetterUtil.setNotNullInteger(
			builder::size, candidateGenerator.getSize());

		if (candidateGenerator.getSuggestMode() != null) {
			builder.suggestMode(
				_translateSuggestMode(candidateGenerator.getSuggestMode()));
		}

		return builder.build();
	}

	private PhraseSuggestCollate _translateCollate(
		PhraseSuggester.Collate collate) {

		PhraseSuggestCollate.Builder builder =
			new PhraseSuggestCollate.Builder();

		if (MapUtil.isNotEmpty(collate.getParams())) {
			builder.params(ConversionUtil.toJsonDataMap(collate.getParams()));
		}

		SetterUtil.setNotNullBoolean(builder::prune, collate.isPrune());

		builder.query(
			PhraseSuggestCollateQuery.of(
				phraseSuggestCollateQuery -> phraseSuggestCollateQuery.source(
					JsonpUtil.toString(
						new Query(
							_queryTranslator.translate(
								collate.getQuery(), null))))));

		return builder.build();
	}

	private SuggestSort _translateSort(Suggester.Sort sort) {
		if (sort == Suggester.Sort.FREQUENCY) {
			return SuggestSort.Frequency;
		}

		return SuggestSort.Score;
	}

	private StringDistance _translateStringDistance(
		Suggester.StringDistance stringDistance) {

		if (stringDistance == Suggester.StringDistance.DAMERAU_LEVENSHTEIN) {
			return StringDistance.DamerauLevenshtein;
		}
		else if (stringDistance == Suggester.StringDistance.JAROWINKLER) {
			return StringDistance.JaroWinkler;
		}
		else if (stringDistance == Suggester.StringDistance.LEVENSTEIN) {
			return StringDistance.Levenshtein;
		}
		else if (stringDistance == Suggester.StringDistance.NGRAM) {
			return StringDistance.Ngram;
		}

		return StringDistance.Internal;
	}

	private SuggestMode _translateSuggestMode(
		Suggester.SuggestMode suggestMode) {

		if (suggestMode == Suggester.SuggestMode.ALWAYS) {
			return SuggestMode.Always;
		}
		else if (suggestMode == Suggester.SuggestMode.POPULAR) {
			return SuggestMode.Popular;
		}

		return SuggestMode.Missing;
	}

	private final QueryTranslator<QueryVariant> _queryTranslator;

}