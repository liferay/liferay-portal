/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.query.translator;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.MatchAllQuery;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.TermQuery;
import com.liferay.portal.search.query.WildcardQuery;

import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.MatchAllDocsQuery;

/**
 * @author André de Oliveira
 * @author Petteri Karttunen
 */
public class SolrQueryTranslator {

	public org.apache.lucene.search.Query convert(Query query) {
		if (query instanceof BooleanQuery) {
			return visit((BooleanQuery)query);
		}

		if (query instanceof MatchAllQuery) {
			return visit((MatchAllQuery)query);
		}

		if (query instanceof TermQuery) {
			return visit((TermQuery)query);
		}

		if (query instanceof WildcardQuery) {
			return visit((WildcardQuery)query);
		}

		_log.error("Query translator not found for " + query.getClass());

		return null;
	}

	public String translate(Query query) {
		org.apache.lucene.search.Query luceneQuery = convert(query);

		if (luceneQuery != null) {
			return luceneQuery.toString();
		}

		return null;
	}

	public org.apache.lucene.search.Query visit(BooleanQuery booleanQuery) {
		org.apache.lucene.search.BooleanQuery.Builder builder =
			new org.apache.lucene.search.BooleanQuery.Builder();

		_processQueryClause(
			booleanQuery.getFilterQueryClauses(), this,
			query -> builder.add(query, BooleanClause.Occur.FILTER));

		_processQueryClause(
			booleanQuery.getMustQueryClauses(), this,
			query -> builder.add(query, BooleanClause.Occur.MUST));

		_processQueryClause(
			booleanQuery.getMustNotQueryClauses(), this,
			query -> builder.add(query, BooleanClause.Occur.MUST_NOT));

		org.apache.lucene.search.Query query = builder.build();

		if (booleanQuery.getBoost() != null) {
			return new BoostQuery(query, booleanQuery.getBoost());
		}

		return query;
	}

	public org.apache.lucene.search.Query visit(MatchAllQuery matchAllQuery) {
		org.apache.lucene.search.Query query = new MatchAllDocsQuery();

		if (matchAllQuery.getBoost() != null) {
			return new BoostQuery(query, matchAllQuery.getBoost());
		}

		return query;
	}

	public org.apache.lucene.search.Query visit(TermQuery termQuery) {
		org.apache.lucene.search.Query query =
			new org.apache.lucene.search.TermQuery(
				new Term(
					termQuery.getField(),
					String.valueOf(termQuery.getValue())));

		if (termQuery.getBoost() != null) {
			return new BoostQuery(query, termQuery.getBoost());
		}

		return query;
	}

	public org.apache.lucene.search.Query visit(WildcardQuery wildcardQuery) {
		WildcardQueryTranslatorImpl wildcardQueryTranslatorImpl =
			new WildcardQueryTranslatorImpl();

		return wildcardQueryTranslatorImpl.translate(wildcardQuery);
	}

	private void _processQueryClause(
		List<Query> queryClauses, SolrQueryTranslator solrQueryTranslator,
		LuceneQueryConsumer luceneQueryConsumer) {

		for (Query query : queryClauses) {
			org.apache.lucene.search.Query luceneQuery = _translate(
				query, solrQueryTranslator);

			if (luceneQuery == null) {
				continue;
			}

			luceneQueryConsumer.accept(_translate(query, solrQueryTranslator));
		}
	}

	private org.apache.lucene.search.Query _translate(
		Query query, SolrQueryTranslator solrQueryTranslator) {

		return solrQueryTranslator.convert(query);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SolrQueryTranslator.class);

	private interface LuceneQueryConsumer {

		public void accept(org.apache.lucene.search.Query query);

	}

}