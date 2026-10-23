/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.util;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.IncludeExcludeClause;
import com.liferay.portal.search.aggregation.bucket.TermsAggregation;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.query.TermsQuery;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.site.cms.site.initializer.constants.CMSWorkflowConstants;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * @author Mikel Lorza
 */
public class CMSSearchUtil {

	public static void addBucketKeys(
		Function<IncludeExcludeClause, TermsAggregationResult>
			termsAggregationResultFunction,
		Collection<String> keys, String prefix) {

		String escapedPrefix = _escapeRegex(prefix);
		Set<String> excludedNextCharacters = new TreeSet<>();

		while (true) {
			TermsAggregationResult termsAggregationResult =
				termsAggregationResultFunction.apply(
					_getIncludeExcludeClause(
						_getExcludeRegex(escapedPrefix, excludedNextCharacters),
						escapedPrefix + ".*"));

			if (termsAggregationResult == null) {
				return;
			}

			Set<String> nextCharacters = _getNextCharacters(
				prefix, termsAggregationResult);

			if ((termsAggregationResult.getOtherDocCounts() == 0) ||
				nextCharacters.isEmpty()) {

				keys.addAll(
					TransformUtil.transform(
						termsAggregationResult.getBuckets(), Bucket::getKey));

				return;
			}

			for (String nextCharacter : nextCharacters) {
				addBucketKeys(
					termsAggregationResultFunction, keys,
					prefix + nextCharacter);
			}

			excludedNextCharacters.addAll(nextCharacters);
		}
	}

	public static BooleanQuery getReferrersBooleanQuery() {
		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		booleanQuery.addFilterQueryClauses(
			getTermsQuery("cms_section", "contents", "files"),
			getTermsQuery(
				Field.STATUS,
				ArrayUtil.toStringArray(CMSWorkflowConstants.STATUSES)),
			QueriesUtil.term("rootDescendantNode", false));
		booleanQuery.addMustNotQueryClauses(
			QueriesUtil.term(Field.STATUS, WorkflowConstants.STATUS_EXPIRED));

		return booleanQuery;
	}

	public static TermsAggregationResult getTermsAggregationResult(
		Aggregations aggregations, String fieldName,
		IncludeExcludeClause includeExcludeClause, Searcher searcher,
		SearchRequestBuilder searchRequestBuilder, int size) {

		TermsAggregation termsAggregation = aggregations.terms(
			fieldName, fieldName);

		termsAggregation.setIncludeExcludeClause(includeExcludeClause);
		termsAggregation.setSize(size);

		SearchResponse searchResponse = searcher.search(
			searchRequestBuilder.addAggregation(
				termsAggregation
			).size(
				0
			).build());

		return (TermsAggregationResult)searchResponse.getAggregationResult(
			fieldName);
	}

	public static TermsQuery getTermsQuery(String fieldName, String... values) {
		TermsQuery termsQuery = QueriesUtil.terms(fieldName);

		termsQuery.addValues(values);

		return termsQuery;
	}

	private static String _escapeRegex(String value) {
		StringBundler sb = new StringBundler(value.length() * 2);

		for (int i = 0; i < value.length();) {
			int codePoint = value.codePointAt(i);

			if ((codePoint > 127) || !Character.isLetterOrDigit(codePoint)) {
				sb.append(StringPool.BACK_SLASH);
			}

			sb.append(new String(Character.toChars(codePoint)));

			i += Character.charCount(codePoint);
		}

		return sb.toString();
	}

	private static String _getExcludeRegex(
		String escapedPrefix, Set<String> excludedNextCharacters) {

		if (excludedNextCharacters.isEmpty()) {
			return null;
		}

		return StringBundler.concat(
			escapedPrefix, "[",
			_escapeRegex(
				StringUtil.merge(excludedNextCharacters, StringPool.BLANK)),
			"].*");
	}

	private static IncludeExcludeClause _getIncludeExcludeClause(
		String excludeRegex, String includeRegex) {

		return new IncludeExcludeClause() {

			@Override
			public String getExcludeRegex() {
				return excludeRegex;
			}

			@Override
			public String[] getExcludedValues() {
				return null;
			}

			@Override
			public String getIncludeRegex() {
				return includeRegex;
			}

			@Override
			public String[] getIncludedValues() {
				return null;
			}

		};
	}

	private static Set<String> _getNextCharacters(
		String prefix, TermsAggregationResult termsAggregationResult) {

		Set<String> nextCharacters = new TreeSet<>();

		for (Bucket bucket : termsAggregationResult.getBuckets()) {
			String key = bucket.getKey();

			if (key.length() <= prefix.length()) {
				continue;
			}

			nextCharacters.add(
				new String(
					Character.toChars(key.codePointAt(prefix.length()))));
		}

		return nextCharacters;
	}

}