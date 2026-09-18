/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.document;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ErrorCause;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.bulk.OperationType;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch8.internal.util.JsonpUtil;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentRequest;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael C. Han
 */
public class BulkDocumentRequestExecutorTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ElasticsearchFixture elasticsearchFixture = new ElasticsearchFixture(
			getClass());

		_bulkDocumentRequestExecutor = new BulkDocumentRequestExecutor(
			elasticsearchFixture, 0, 0);

		_elasticsearchFixture = elasticsearchFixture;

		_documentFixture.setUp();
		_elasticsearchFixture.setUp();
	}

	@After
	public void tearDown() throws Exception {
		_documentFixture.tearDown();

		_elasticsearchFixture.tearDown();
	}

	@Test
	public void testBulkDocumentRequestTranslation() {
		String uid = "1";

		Document document = new DocumentImpl();

		document.addKeyword(Field.TYPE, _MAPPING_NAME);
		document.addKeyword(Field.UID, uid);
		document.addKeyword("staging", "true");

		IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(
			_INDEX_NAME, document);

		BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();

		bulkDocumentRequest.addBulkableDocumentRequest(indexDocumentRequest);

		DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest(
			_INDEX_NAME, uid);

		bulkDocumentRequest.addBulkableDocumentRequest(deleteDocumentRequest);

		Document updatedDocument = new DocumentImpl();

		updatedDocument.addKeyword(Field.UID, uid);
		updatedDocument.addKeyword("staging", "false");

		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(
			_INDEX_NAME, uid, updatedDocument);

		bulkDocumentRequest.addBulkableDocumentRequest(updateDocumentRequest);

		BulkRequest bulkRequest =
			_bulkDocumentRequestExecutor.createBulkRequest(bulkDocumentRequest);

		List<BulkOperation> bulkOperations = bulkRequest.operations();

		StringBundler sb = new StringBundler();

		for (BulkOperation bulkOperation : bulkOperations) {
			sb.append(JsonpUtil.toString(bulkOperation));
			sb.append("\n");
		}

		Assert.assertEquals(sb.toString(), 3, bulkOperations.size());
	}

	@Test
	public void testBulkDocumentResponseWhenNumberOfTriesIsZero()
		throws Exception {

		BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();

		bulkDocumentRequest.addBulkableDocumentRequest(
			new DeleteDocumentRequest(
				_INDEX_NAME, RandomTestUtil.randomString()));
		bulkDocumentRequest.addBulkableDocumentRequest(
			new DeleteDocumentRequest(
				_INDEX_NAME, RandomTestUtil.randomString()));

		ElasticsearchClient elasticsearchClient = Mockito.mock(
			ElasticsearchClient.class);

		IOException ioException = new IOException(
			RandomTestUtil.randomString());

		Mockito.doThrow(
			ioException
		).when(
			elasticsearchClient
		).bulk(
			Mockito.any(BulkRequest.class)
		);

		ElasticsearchClientResolver mockElasticsearchClientResolver =
			Mockito.mock(ElasticsearchClientResolver.class);

		Mockito.when(
			mockElasticsearchClientResolver.getElasticsearchClient(
				Mockito.nullable(String.class), Mockito.anyBoolean())
		).thenReturn(
			elasticsearchClient
		);

		BulkDocumentRequestExecutor bulkDocumentRequestExecutor =
			new BulkDocumentRequestExecutor(
				mockElasticsearchClientResolver, 0, 0);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BulkDocumentRequestExecutor.class.getName(),
				LoggerTestUtil.ERROR)) {

			try {
				bulkDocumentRequestExecutor.execute(bulkDocumentRequest);

				Assert.fail();
			}
			catch (RuntimeException runtimeException) {
				Assert.assertSame(ioException, runtimeException.getCause());
			}

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to get a bulk response for 2 operations",
				logEntry.getMessage());
			Assert.assertSame(ioException, logEntry.getThrowable());
		}
	}

	@Test
	public void testBulkDocumentResponseWithRejectedBulkItems() {
		try {
			_bulkDocumentRequestExecutor.createBulkDocumentResponse(
				_createBulkResponse());

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			Assert.assertEquals(
				"Unable to index 2/3 bulk items: " + _REJECTION_REASON,
				runtimeException.getMessage());
		}
	}

	private BulkResponse _createBulkResponse() {
		ErrorCause errorCause = new ErrorCause.Builder(
		).reason(
			_REJECTION_REASON
		).type(
			RandomTestUtil.randomString()
		).build();

		BulkResponseItem rejectedBulkResponseItem1 =
			new BulkResponseItem.Builder(
			).error(
				errorCause
			).index(
				_INDEX_NAME
			).operationType(
				OperationType.Index
			).status(
				429
			).build();

		BulkResponseItem rejectedBulkResponseItem2 =
			new BulkResponseItem.Builder(
			).index(
				_INDEX_NAME
			).operationType(
				OperationType.Index
			).status(
				429
			).build();
		BulkResponseItem successfulBulkResponseItem =
			new BulkResponseItem.Builder(
			).index(
				_INDEX_NAME
			).operationType(
				OperationType.Index
			).status(
				200
			).build();

		return new BulkResponse.Builder(
		).errors(
			true
		).items(
			rejectedBulkResponseItem1, rejectedBulkResponseItem2,
			successfulBulkResponseItem
		).took(
			RandomTestUtil.randomLong()
		).build();
	}

	private static final String _INDEX_NAME = "test_request_index";

	private static final String _MAPPING_NAME = "testMapping";

	private static final String _REJECTION_REASON =
		"rejected execution of coordinating operation";

	private BulkDocumentRequestExecutor _bulkDocumentRequestExecutor;
	private final DocumentFixture _documentFixture = new DocumentFixture();
	private ElasticsearchFixture _elasticsearchFixture;

}