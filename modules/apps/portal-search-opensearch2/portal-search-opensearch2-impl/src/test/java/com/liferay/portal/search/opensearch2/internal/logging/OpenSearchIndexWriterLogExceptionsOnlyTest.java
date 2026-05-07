/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.logging;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.search.opensearch2.internal.OpenSearchIndexWriter;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.search.opensearch2.internal.connection.TestOpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.indexing.LiferayOpenSearchIndexingFixtureFactory;
import com.liferay.portal.search.opensearch2.internal.search.engine.adapter.document.BulkDocumentRequestExecutor;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.DocumentCreationHelpers;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Bryan Engler
 */
public class OpenSearchIndexWriterLogExceptionsOnlyTest
	extends BaseIndexingTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testAddDocument() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			addDocument(
				DocumentCreationHelpers.singleKeyword(
					Field.EXPIRATION_DATE, "text"));

			String expectedMessage =
				"failed to parse field [expirationDate] of type [date] in " +
					"document with id";

			_assertLogCapture(
				message -> Assert.assertTrue(
					message + " does not contain " + expectedMessage,
					message.contains(expectedMessage)),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testAddDocuments() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, "text");

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.addDocuments(
					createSearchContext(), Arrays.asList(document));
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(
				message -> Assert.assertEquals("Bulk add failed", message),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testAddDocumentsBulkExecutor() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BulkDocumentRequestExecutor.class.getName(),
				LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, "text");

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.addDocuments(
					createSearchContext(), Arrays.asList(document));
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			String expectedMessage =
				"failed to parse field [expirationDate] of type [date] in " +
					"document with id";

			_assertLogCapture(
				message -> Assert.assertTrue(
					message + " does not contain " + expectedMessage,
					message.contains(expectedMessage)),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testCommit() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(1);

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.commit(searchContext);
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(
				message -> Assert.assertEquals(
					"Request failed: [index_not_found_exception] no such " +
						"index [1]",
					message),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testDeleteDocument() {
		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(1);

		IndexWriter indexWriter = getIndexWriter();

		try {
			indexWriter.deleteDocument(searchContext, "1");
		}
		catch (SearchException searchException) {
			if (_log.isDebugEnabled()) {
				_log.debug(searchException);
			}
		}
	}

	@Test
	public void testDeleteDocumentInfoLevel() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.INFO)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(1);

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.deleteDocument(searchContext, "1");
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			String expectedMessage =
				"Request failed: [index_not_found_exception] no such index [1]";

			_assertLogCapture(
				message -> Assert.assertTrue(
					message + " does not contain " + expectedMessage,
					message.contains(expectedMessage)),
				logCapture, LoggerTestUtil.INFO);
		}
	}

	@Test
	public void testDeleteDocuments() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(1);

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.deleteDocuments(searchContext, Arrays.asList("1"));
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(
				message -> Assert.assertEquals("Bulk delete failed", message),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testDeleteDocumentsBulkExecutor() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BulkDocumentRequestExecutor.class.getName(),
				LoggerTestUtil.ERROR)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(1);

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.deleteDocuments(searchContext, Arrays.asList("1"));
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			String expectedMessage = "no such index [1]";

			_assertLogCapture(
				message -> Assert.assertTrue(
					message + " does not contain " + expectedMessage,
					message.contains(expectedMessage)),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testDeleteEntityDocuments() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(1);

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.deleteEntityDocuments(searchContext, "test");
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(
				message -> Assert.assertEquals(
					"Request failed: [index_not_found_exception] no such " +
						"index [1]",
					message),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testPartiallyUpdateDocument() {
		Document document = new DocumentImpl();

		document.addKeyword(Field.UID, "1");

		IndexWriter indexWriter = getIndexWriter();

		try {
			indexWriter.partiallyUpdateDocument(
				createSearchContext(), document);
		}
		catch (SearchException searchException) {
			if (_log.isDebugEnabled()) {
				_log.debug(searchException);
			}
		}
	}

	@Test
	public void testPartiallyUpdateDocuments() {
		Document document = new DocumentImpl();

		document.addKeyword(Field.UID, "1");

		IndexWriter indexWriter = getIndexWriter();

		try {
			indexWriter.partiallyUpdateDocuments(
				createSearchContext(), Arrays.asList(document));
		}
		catch (SearchException searchException) {
			if (_log.isDebugEnabled()) {
				_log.debug(searchException);
			}
		}
	}

	@Test
	public void testPartiallyUpdateDocumentsBulkExecutor() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BulkDocumentRequestExecutor.class.getName(),
				LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.UID, "1");

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.partiallyUpdateDocuments(
					createSearchContext(), Arrays.asList(document));
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			String expectedMessage = "[1]: document missing";

			_assertLogCapture(
				message -> Assert.assertTrue(
					message + " does not contain " + expectedMessage,
					message.contains(expectedMessage)),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testUpdateDocument() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, "text");
			document.addKeyword(Field.UID, "1");

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.updateDocument(createSearchContext(), document);
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(
				message -> Assert.assertEquals("Update failed", message),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testUpdateDocumentBulkExecutor() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BulkDocumentRequestExecutor.class.getName(),
				LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, "text");
			document.addKeyword(Field.UID, "1");

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.updateDocument(createSearchContext(), document);
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			String expectedMessage =
				"failed to parse field [expirationDate] of type [date] in " +
					"document with id '1'. Preview of field's value: 'text'";

			_assertLogCapture(
				message -> Assert.assertTrue(
					message + " does not contain " + expectedMessage,
					message.contains(expectedMessage)),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testUpdateDocuments() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, "text");
			document.addKeyword(Field.UID, "1");

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.updateDocuments(
					createSearchContext(), Arrays.asList(document));
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			_assertLogCapture(
				message -> Assert.assertEquals("Bulk update failed", message),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testUpdateDocumentsBulkExecutor() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BulkDocumentRequestExecutor.class.getName(),
				LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, "text");
			document.addKeyword(Field.UID, "1");

			IndexWriter indexWriter = getIndexWriter();

			try {
				indexWriter.updateDocuments(
					createSearchContext(), Arrays.asList(document));
			}
			catch (SearchException searchException) {
				if (_log.isDebugEnabled()) {
					_log.debug(searchException);
				}
			}

			String expectedMessage =
				"failed to parse field [expirationDate] of type [date] in " +
					"document with id '1'. Preview of field's value: 'text'";

			_assertLogCapture(
				message -> Assert.assertTrue(
					message + " does not contain " + expectedMessage,
					message.contains(expectedMessage)),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Override
	protected IndexingFixture createIndexingFixture() {
		return LiferayOpenSearchIndexingFixtureFactory.builder(
		).testOpenSearchConnectionManager(
			new TestOpenSearchConnectionManager(
				HashMapBuilder.<String, Object>put(
					"logExceptionsOnly", true
				).build())
		).build();
	}

	private void _assertLogCapture(
		Consumer<String> consumer, LogCapture logCapture, String logLevel) {

		List<LogEntry> logEntries = logCapture.getLogEntries();

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		Assert.assertEquals(logLevel, logEntry.getPriority());
		consumer.accept(logEntry.getMessage());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OpenSearchIndexWriterLogExceptionsOnlyTest.class);

}