/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.repository.search.internal;

import com.liferay.document.library.repository.search.internal.util.KeywordsUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.search.RepositorySearchQueryTermBuilder;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(service = RepositorySearchQueryTermBuilder.class)
public class LuceneRepositorySearchQueryTermBuilder
	implements RepositorySearchQueryTermBuilder {

	@Override
	public void addTerm(
		BooleanQuery booleanQuery, SearchContext searchContext, String field,
		String value) {

		if (Validator.isNull(value)) {
			return;
		}

		try {
			QueryParser queryParser = new QueryParser(field, _analyzer);

			queryParser.setAllowLeadingWildcard(true);
			queryParser.setSplitOnWhitespace(true);

			Query query = null;

			try {
				query = queryParser.parse(value);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				query = queryParser.parse(KeywordsUtil.escape(value));
			}

			_translateQuery(
				booleanQuery, searchContext, query, BooleanClause.Occur.SHOULD);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Activate
	protected void activate() {
		_analyzer = new RepositoryAnalyzer();
	}

	protected BooleanClauseOccur getBooleanClauseOccur(
		BooleanClause.Occur occur) {

		if (occur.equals(BooleanClause.Occur.MUST)) {
			return BooleanClauseOccur.MUST;
		}
		else if (occur.equals(BooleanClause.Occur.MUST_NOT)) {
			return BooleanClauseOccur.MUST_NOT;
		}

		return BooleanClauseOccur.SHOULD;
	}

	protected BooleanClause.Occur getBooleanClauseOccur(
		BooleanClauseOccur occur) {

		if (occur.equals(BooleanClauseOccur.MUST)) {
			return BooleanClause.Occur.MUST;
		}
		else if (occur.equals(BooleanClauseOccur.MUST_NOT)) {
			return BooleanClause.Occur.MUST_NOT;
		}

		return BooleanClause.Occur.SHOULD;
	}

	private void _translateQuery(
			BooleanQuery booleanQuery, SearchContext searchContext, Query query,
			BooleanClause.Occur occur)
		throws Exception {

		BooleanClauseOccur booleanClauseOccur = getBooleanClauseOccur(occur);

		if (query instanceof org.apache.lucene.search.TermQuery) {
			org.apache.lucene.search.TermQuery luceneTermQuery =
				(org.apache.lucene.search.TermQuery)query;

			Term term = luceneTermQuery.getTerm();

			String termValue = term.text();

			TermQuery termQuery = new TermQuery(term.field(), termValue);

			booleanQuery.add(termQuery, getBooleanClauseOccur(occur));
		}
		else if (query instanceof org.apache.lucene.search.BooleanQuery) {
			org.apache.lucene.search.BooleanQuery curBooleanQuery =
				(org.apache.lucene.search.BooleanQuery)query;

			BooleanQuery conjunctionQuery = new BooleanQuery();

			BooleanQuery disjunctionQuery = new BooleanQuery();

			for (BooleanClause booleanClause : curBooleanQuery.clauses()) {
				BooleanClauseOccur curBooleanClauseOccur =
					getBooleanClauseOccur(booleanClause.getOccur());

				BooleanQuery subbooleanQuery = null;

				if (curBooleanClauseOccur.equals(BooleanClauseOccur.SHOULD)) {
					subbooleanQuery = disjunctionQuery;
				}
				else {
					subbooleanQuery = conjunctionQuery;
				}

				_translateQuery(
					subbooleanQuery, searchContext, booleanClause.getQuery(),
					booleanClause.getOccur());
			}

			if (conjunctionQuery.hasClauses()) {
				booleanQuery.add(conjunctionQuery, BooleanClauseOccur.MUST);
			}

			if (disjunctionQuery.hasClauses()) {
				booleanQuery.add(disjunctionQuery, BooleanClauseOccur.SHOULD);
			}
		}
		else if (query instanceof FuzzyQuery) {
			FuzzyQuery fuzzyQuery = (FuzzyQuery)query;

			Term term = fuzzyQuery.getTerm();

			String termText = term.text();

			String termValue = termText.concat(StringPool.STAR);

			WildcardQuery wildcardQuery = new WildcardQuery(
				term.field(), termValue);

			booleanQuery.add(wildcardQuery, booleanClauseOccur);
		}
		else if (query instanceof PhraseQuery) {
			PhraseQuery phraseQuery = (PhraseQuery)query;

			Term[] terms = phraseQuery.getTerms();

			StringBundler sb = new StringBundler(terms.length * 2);

			for (Term term : terms) {
				sb.append(term.text());
				sb.append(StringPool.SPACE);
			}

			TermQuery termQuery = new TermQuery(
				terms[0].field(), StringUtil.trim(sb.toString()));

			booleanQuery.add(termQuery, booleanClauseOccur);
		}
		else if (query instanceof PrefixQuery) {
			PrefixQuery prefixQuery = (PrefixQuery)query;

			Term prefixTerm = prefixQuery.getPrefix();

			String prefixTermText = prefixTerm.text();

			String termValue = prefixTermText.concat(StringPool.STAR);

			WildcardQuery wildcardQuery = new WildcardQuery(
				prefixTerm.field(), termValue);

			booleanQuery.add(wildcardQuery, booleanClauseOccur);
		}
		else if (query instanceof TermRangeQuery) {
			TermRangeQuery termRangeQuery = (TermRangeQuery)query;

			BytesRef lowerTerm = termRangeQuery.getLowerTerm();
			BytesRef upperTerm = termRangeQuery.getUpperTerm();

			booleanQuery.addRangeTerm(
				termRangeQuery.getField(), lowerTerm.utf8ToString(),
				upperTerm.utf8ToString());
		}
		else if (query instanceof org.apache.lucene.search.WildcardQuery) {
			org.apache.lucene.search.WildcardQuery luceneWildcardQuery =
				(org.apache.lucene.search.WildcardQuery)query;

			Term wildcardTerm = luceneWildcardQuery.getTerm();

			WildcardQuery wildcardQuery = new WildcardQuery(
				wildcardTerm.field(), wildcardTerm.text());

			booleanQuery.add(wildcardQuery, booleanClauseOccur);
		}
		else {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Ignoring unknown query type ", query.getClass(),
						" with query ", query));
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LuceneRepositorySearchQueryTermBuilder.class);

	private Analyzer _analyzer;

	private static class RepositoryAnalyzer extends Analyzer {

		@Override
		protected TokenStreamComponents createComponents(String fieldName) {
			return new TokenStreamComponents(new KeywordTokenizer());
		}

		@Override
		protected TokenStream normalize(
			String fieldName, TokenStream tokenStream) {

			return new LowerCaseFilter(tokenStream);
		}

	}

}