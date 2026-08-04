/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.engine.adapter.document;

import co.elastic.clients.elasticsearch._types.Conflicts;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;

import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch8.internal.util.JsonpUtil;
import com.liferay.portal.search.engine.adapter.document.DeleteByQueryDocumentRequest;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Dylan Rebelak
 */
public class DeleteByQueryDocumentRequestExecutorTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture(
			DeleteByQueryDocumentRequestExecutorTest.class.getSimpleName());

		_elasticsearchFixture.setUp();
	}

	@After
	public void tearDown() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	@Test
	public void testDocumentRequestTranslationWithNoRefresh() {
		doTestDocumentRequestTranslation(false);
	}

	@Test
	public void testDocumentRequestTranslationWithProceedOnConflicts() {
		DeleteByQueryDocumentRequestExecutor
			deleteByQueryDocumentRequestExecutor =
				new DeleteByQueryDocumentRequestExecutor(_elasticsearchFixture);

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.addExactTerm(_FIELD_NAME, true);

		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest =
			new DeleteByQueryDocumentRequest(
				booleanQuery, new String[] {_INDEX_NAME});

		deleteByQueryDocumentRequest.setProceedOnConflicts(true);

		DeleteByQueryRequest deleteByQueryRequest =
			deleteByQueryDocumentRequestExecutor.createDeleteByQueryRequest(
				deleteByQueryDocumentRequest);

		Assert.assertEquals(
			Conflicts.Proceed, deleteByQueryRequest.conflicts());
	}

	@Test
	public void testDocumentRequestTranslationWithRefresh() {
		doTestDocumentRequestTranslation(true);
	}

	protected void doTestDocumentRequestTranslation(boolean refresh) {
		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.addExactTerm(_FIELD_NAME, true);

		DeleteByQueryDocumentRequest deleteByQueryDocumentRequest =
			new DeleteByQueryDocumentRequest(
				booleanQuery, new String[] {_INDEX_NAME});

		deleteByQueryDocumentRequest.setRefresh(refresh);

		DeleteByQueryDocumentRequestExecutor
			deleteByQueryDocumentRequestExecutor =
				new DeleteByQueryDocumentRequestExecutor(_elasticsearchFixture);

		DeleteByQueryRequest deleteByQueryRequest =
			deleteByQueryDocumentRequestExecutor.createDeleteByQueryRequest(
				deleteByQueryDocumentRequest);

		Assert.assertArrayEquals(
			new String[] {_INDEX_NAME},
			ArrayUtil.toStringArray(deleteByQueryRequest.index()));

		Assert.assertEquals(
			deleteByQueryDocumentRequest.isRefresh(),
			deleteByQueryRequest.refresh());

		String queryString = JsonpUtil.toString(deleteByQueryRequest);

		Assert.assertTrue(queryString.contains(_FIELD_NAME));
		Assert.assertTrue(queryString.contains("\"value\":\"true\""));
	}

	private static final String _FIELD_NAME = "testField";

	private static final String _INDEX_NAME = "test_request_index";

	private ElasticsearchFixture _elasticsearchFixture;

}