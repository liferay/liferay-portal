/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.document;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.test.util.RequestExecutorFixture;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentRequest;
import com.liferay.portal.search.internal.document.DocumentBuilderImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Adam Brandizzi
 */
public class UpdateDocumentRequestExecutorTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture();

		_elasticsearchFixture.setUp();

		_requestExecutorFixture = new RequestExecutorFixture(
			_elasticsearchFixture);

		_requestExecutorFixture.setUp();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	@Before
	public void setUp() {
		_updateDocumentRequestExecutor =
			_requestExecutorFixture.getUpdateDocumentRequestExecutor();

		_requestExecutorFixture.createIndex(_INDEX_NAME);
	}

	@After
	public void tearDown() {
		_requestExecutorFixture.deleteIndex(_INDEX_NAME);
	}

	@Test
	public void testUnsetValue() {
		String fieldName = _FIELD_NAME;

		IndexDocumentResponse indexDocumentResponse =
			_requestExecutorFixture.indexDocument(
				_INDEX_NAME, buildDocument(fieldName, "example test"));

		String uid = indexDocumentResponse.getUid();

		assertIndexedFieldEquals(uid, _FIELD_NAME, "example test");

		_updateDocumentRequestExecutor.execute(
			new UpdateDocumentRequest(
				_INDEX_NAME, uid, buildDocumentWithUnsetField(fieldName)));

		assertIndexedFieldEquals(uid, _FIELD_NAME, "example test");
	}

	@Test
	public void testUnsetValueWithArrayWithNull() {
		IndexDocumentResponse indexDocumentResponse =
			_requestExecutorFixture.indexDocument(
				_INDEX_NAME, buildDocument(_FIELD_NAME, "example test"));

		String uid = indexDocumentResponse.getUid();

		assertIndexedFieldEquals(uid, _FIELD_NAME, "example test");

		_updateDocumentRequestExecutor.execute(
			new UpdateDocumentRequest(
				_INDEX_NAME, uid,
				buildDocument(_FIELD_NAME, new String[] {null})));

		assertIndexedFieldEquals(uid, _FIELD_NAME, "example test");
	}

	@Test
	public void testUnsetValueWithEmptyArray() {
		IndexDocumentResponse indexDocumentResponse =
			_requestExecutorFixture.indexDocument(
				_INDEX_NAME, buildDocument(_FIELD_NAME, "example test"));

		String uid = indexDocumentResponse.getUid();

		assertIndexedFieldEquals(uid, _FIELD_NAME, "example test");

		_updateDocumentRequestExecutor.execute(
			new UpdateDocumentRequest(
				_INDEX_NAME, uid, buildDocument(_FIELD_NAME, new String[0])));

		assertIndexedFieldEquals(uid, _FIELD_NAME, "example test");
	}

	@Test
	public void testUnsetValueWithNull() {
		IndexDocumentResponse indexDocumentResponse =
			_requestExecutorFixture.indexDocument(
				_INDEX_NAME, buildDocument(_FIELD_NAME, "example test"));

		String uid = indexDocumentResponse.getUid();

		assertIndexedFieldEquals(uid, _FIELD_NAME, "example test");

		_updateDocumentRequestExecutor.execute(
			new UpdateDocumentRequest(
				_INDEX_NAME, uid, buildDocument(_FIELD_NAME, (String)null)));

		assertIndexedFieldEquals(uid, _FIELD_NAME, "example test");
	}

	@Test
	public void testUpdateDocumentWithNoRefresh() {
		doUpdateDocument(false);
	}

	@Test
	public void testUpdateDocumentWithRefresh() {
		doUpdateDocument(true);
	}

	protected void assertIndexedFieldEquals(
		String uid, String fieldName, String expectedFieldValue) {

		Document document = _requestExecutorFixture.getDocumentById(
			_INDEX_NAME, uid);

		Assert.assertEquals(expectedFieldValue, document.getString(fieldName));
	}

	protected Document buildDocument(String fieldName, String... fieldValue) {
		DocumentBuilder documentBuilder = new DocumentBuilderImpl();

		return documentBuilder.setStrings(
			fieldName, fieldValue
		).build();
	}

	protected Document buildDocument(
		String uid, String fieldName, String fieldValue) {

		DocumentBuilder documentBuilder = new DocumentBuilderImpl();

		return documentBuilder.setString(
			fieldName, fieldValue
		).setString(
			Field.UID, uid
		).build();
	}

	protected Document buildDocumentWithUnsetField(String fieldName) {
		DocumentBuilder documentBuilder = new DocumentBuilderImpl();

		return documentBuilder.unsetValue(
			fieldName
		).build();
	}

	protected void doUpdateDocument(boolean refresh) {
		IndexDocumentResponse indexDocumentResponse =
			_requestExecutorFixture.indexDocument(
				_INDEX_NAME, buildDocument(_FIELD_NAME, "example test"));

		String uid = indexDocumentResponse.getUid();

		assertIndexedFieldEquals(uid, _FIELD_NAME, "example test");

		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(
			_INDEX_NAME, uid, buildDocument(_FIELD_NAME, "updated value"));

		updateDocumentRequest.setRefresh(refresh);

		_updateDocumentRequestExecutor.execute(updateDocumentRequest);

		assertIndexedFieldEquals(uid, _FIELD_NAME, "updated value");
	}

	private static final String _FIELD_NAME = "testField";

	private static final String _INDEX_NAME = "test_request_index";

	private static ElasticsearchFixture _elasticsearchFixture;
	private static RequestExecutorFixture _requestExecutorFixture;

	private UpdateDocumentRequestExecutor _updateDocumentRequestExecutor;

}