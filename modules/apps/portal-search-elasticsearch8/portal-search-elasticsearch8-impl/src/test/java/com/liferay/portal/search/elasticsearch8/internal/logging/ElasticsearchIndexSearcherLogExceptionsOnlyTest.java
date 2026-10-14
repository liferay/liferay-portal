/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.logging;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.search.elasticsearch8.internal.ElasticsearchIndexSearcher;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchConnectionFixture;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch8.internal.indexing.LiferayElasticsearchIndexingFixtureFactory;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Bryan Engler
 */
public class ElasticsearchIndexSearcherLogExceptionsOnlyTest
	extends BaseIndexingTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testExceptionOnlyLoggedWhenQueryMalformedSearch() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ElasticsearchIndexSearcher.class.getName(),
				LoggerTestUtil.ERROR)) {

			search(createSearchContext(), getMalformedQuery());

			_assertLogCapture(logCapture);
		}
	}

	@Test
	public void testExceptionOnlyLoggedWhenQueryMalformedSearchCount() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ElasticsearchIndexSearcher.class.getName(),
				LoggerTestUtil.ERROR)) {

			searchCount(createSearchContext(), getMalformedQuery());

			_assertLogCapture(logCapture);
		}
	}

	protected ElasticsearchConnectionFixture
		createElasticsearchConnectionFixture() {

		return ElasticsearchConnectionFixture.builder(
		).clusterName(
			ElasticsearchIndexSearcherLogExceptionsOnlyTest.class.
				getSimpleName()
		).elasticsearchConfigurationProperties(
			Collections.singletonMap("logExceptionsOnly", true)
		).build();
	}

	@Override
	protected IndexingFixture createIndexingFixture() {
		return LiferayElasticsearchIndexingFixtureFactory.builder(
		).elasticsearchFixture(
			new ElasticsearchFixture(createElasticsearchConnectionFixture())
		).build();
	}

	protected Query getMalformedQuery() {
		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(
			new TermQuery(Field.EXPIRATION_DATE, "text"),
			BooleanClauseOccur.MUST);

		return booleanQuery;
	}

	private void _assertLogCapture(LogCapture logCapture) {
		List<LogEntry> logEntries = logCapture.getLogEntries();

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		Assert.assertEquals(LoggerTestUtil.ERROR, logEntry.getPriority());

		String expectedMessage = StringBundler.concat(
			"[es/search] failed: [search_phase_execution_exception] all ",
			"shards failed; caused by: [parse_exception] failed to parse date ",
			"field [text] with format [yyyyMMddHHmmss]: [failed to parse date ",
			"field [text] with format [yyyyMMddHHmmss]]; caused by: ",
			"[illegal_argument_exception] failed to parse date field [text] ",
			"with format [yyyyMMddHHmmss]; caused by: ",
			"[date_time_parse_exception] Text 'text' could not be parsed at ",
			"index 0; root cause: [parse_exception] failed to parse date ",
			"field [text] with format [yyyyMMddHHmmss]: [failed to parse date ",
			"field [text] with format [yyyyMMddHHmmss]]");

		Assert.assertEquals(expectedMessage, logEntry.getMessage());

		Throwable throwable = logEntry.getThrowable();

		Assert.assertEquals(expectedMessage, throwable.getMessage());
		Assert.assertSame(ElasticsearchException.class, throwable.getClass());
	}

}