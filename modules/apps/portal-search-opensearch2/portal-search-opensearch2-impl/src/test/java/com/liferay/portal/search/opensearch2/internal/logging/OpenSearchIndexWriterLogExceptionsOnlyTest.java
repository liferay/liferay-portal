/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.logging;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriter;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
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
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.opensearch.client.opensearch._types.OpenSearchException;

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

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		IndexWriter indexWriter = getIndexWriter();

		if (indexWriter == null) {
			return;
		}

		indexWriter.deleteDocument(createSearchContext(), _UID);
	}

	@Test
	public void testAddDocument() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			addDocument(
				DocumentCreationHelpers.singleKeyword(
					Field.EXPIRATION_DATE, _INVALID_DATE));

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
	public void testAddDocuments() throws SearchException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, _INVALID_DATE);

			IndexWriter indexWriter = getIndexWriter();

			indexWriter.addDocuments(
				createSearchContext(), Arrays.asList(document));

			_assertLogCapture(
				message -> Assert.assertEquals("Bulk add failed", message),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testAddDocumentsBulkExecutor() throws SearchException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BulkDocumentRequestExecutor.class.getName(),
				LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, _INVALID_DATE);

			IndexWriter indexWriter = getIndexWriter();

			indexWriter.addDocuments(
				createSearchContext(), Arrays.asList(document));

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
	public void testCommit() throws SearchException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(_COMPANY_ID);

			IndexWriter indexWriter = getIndexWriter();

			indexWriter.commit(searchContext);

			_assertLogCapture(
				message -> Assert.assertEquals(
					StringBundler.concat(
						"Request failed: [index_not_found_exception] no such ",
						"index [", _COMPANY_ID, "]"),
					message),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testDeleteDocument() throws SearchException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.WARN)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(_COMPANY_ID);

			IndexWriter indexWriter = getIndexWriter();

			indexWriter.deleteDocument(searchContext, _UID);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
		}
	}

	@Test
	public void testDeleteDocumentInfoLevel() throws SearchException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.INFO)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(_COMPANY_ID);

			IndexWriter indexWriter = getIndexWriter();

			indexWriter.deleteDocument(searchContext, _UID);

			String expectedMessage = StringBundler.concat(
				OpenSearchException.class.getName(), ": Request failed: ",
				"[index_not_found_exception] no such index [", _COMPANY_ID,
				"]");

			_assertLogCapture(
				message -> Assert.assertEquals(expectedMessage, message),
				logCapture, LoggerTestUtil.INFO);
		}
	}

	@Test
	public void testDeleteDocuments() throws SearchException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(_COMPANY_ID);

			IndexWriter indexWriter = getIndexWriter();

			indexWriter.deleteDocuments(searchContext, Arrays.asList(_UID));

			_assertLogCapture(
				message -> Assert.assertEquals("Bulk delete failed", message),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testDeleteDocumentsBulkExecutor() throws SearchException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BulkDocumentRequestExecutor.class.getName(),
				LoggerTestUtil.ERROR)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(_COMPANY_ID);

			IndexWriter indexWriter = getIndexWriter();

			indexWriter.deleteDocuments(searchContext, Arrays.asList(_UID));

			String expectedMessage = StringBundler.concat(
				"no such index [", _COMPANY_ID, "]");

			_assertLogCapture(
				message -> Assert.assertTrue(
					message + " does not contain " + expectedMessage,
					message.contains(expectedMessage)),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testDeleteEntityDocuments() throws SearchException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(_COMPANY_ID);

			IndexWriter indexWriter = getIndexWriter();

			indexWriter.deleteEntityDocuments(searchContext, "test");

			_assertLogCapture(
				message -> Assert.assertEquals(
					StringBundler.concat(
						"Request failed: [index_not_found_exception] no such ",
						"index [", _COMPANY_ID, "]"),
					message),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testPartiallyUpdateDocument() throws SearchException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.UID, _UID);

			IndexWriter indexWriter = getIndexWriter();

			indexWriter.partiallyUpdateDocument(
				createSearchContext(), document);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());
		}
	}

	@Test
	public void testPartiallyUpdateDocuments() throws SearchException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.UID, _UID);

			IndexWriter indexWriter = getIndexWriter();

			indexWriter.partiallyUpdateDocuments(
				createSearchContext(), Arrays.asList(document));

			_assertLogCapture(
				message -> Assert.assertEquals(
					"Bulk partial update failed", message),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testPartiallyUpdateDocumentsBulkExecutor()
		throws SearchException {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BulkDocumentRequestExecutor.class.getName(),
				LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.UID, _UID);

			IndexWriter indexWriter = getIndexWriter();

			indexWriter.partiallyUpdateDocuments(
				createSearchContext(), Arrays.asList(document));

			String expectedMessage = StringBundler.concat(
				"[", _UID, "]: document missing");

			_assertLogCapture(
				message -> Assert.assertTrue(
					message + " does not contain " + expectedMessage,
					message.contains(expectedMessage)),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testUpdateDocument() throws SearchException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, _INVALID_DATE);
			document.addKeyword(Field.UID, _UID);

			IndexWriter indexWriter = getIndexWriter();

			indexWriter.updateDocument(createSearchContext(), document);

			_assertLogCapture(
				message -> Assert.assertEquals("Update failed", message),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testUpdateDocumentBulkExecutor() throws SearchException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BulkDocumentRequestExecutor.class.getName(),
				LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, _INVALID_DATE);
			document.addKeyword(Field.UID, _UID);

			IndexWriter indexWriter = getIndexWriter();

			indexWriter.updateDocument(createSearchContext(), document);

			String expectedMessage = StringBundler.concat(
				"failed to parse field [expirationDate] of type [date] in ",
				"document with id '", _UID, "'. Preview of field's value: '",
				_INVALID_DATE, "'");

			_assertLogCapture(
				message -> Assert.assertTrue(
					message + " does not contain " + expectedMessage,
					message.contains(expectedMessage)),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testUpdateDocuments() throws SearchException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				OpenSearchIndexWriter.class.getName(), LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, _INVALID_DATE);
			document.addKeyword(Field.UID, _UID);

			IndexWriter indexWriter = getIndexWriter();

			indexWriter.updateDocuments(
				createSearchContext(), Arrays.asList(document));

			_assertLogCapture(
				message -> Assert.assertEquals("Bulk update failed", message),
				logCapture, LoggerTestUtil.ERROR);
		}
	}

	@Test
	public void testUpdateDocumentsBulkExecutor() throws SearchException {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BulkDocumentRequestExecutor.class.getName(),
				LoggerTestUtil.ERROR)) {

			Document document = new DocumentImpl();

			document.addKeyword(Field.EXPIRATION_DATE, _INVALID_DATE);
			document.addKeyword(Field.UID, _UID);

			IndexWriter indexWriter = getIndexWriter();

			indexWriter.updateDocuments(
				createSearchContext(), Arrays.asList(document));

			String expectedMessage = StringBundler.concat(
				"failed to parse field [expirationDate] of type [date] in ",
				"document with id '", _UID, "'. Preview of field's value: '",
				_INVALID_DATE, "'");

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
				Collections.singletonMap("logExceptionsOnly", true))
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

	private static final long _COMPANY_ID = 1;

	private static final String _INVALID_DATE = "text";

	private static final String _UID = "1";

}