/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.document;

import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch7.internal.legacy.query.ElasticsearchQueryTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.engine.adapter.document.UpdateByQueryDocumentRequest;
import com.liferay.portal.search.internal.script.ScriptsImpl;
import com.liferay.portal.search.script.Scripts;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.elasticsearch.index.reindex.UpdateByQueryRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Dylan Rebelak
 */
public class UpdateByQueryDocumentRequestExecutorTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture(
			UpdateByQueryDocumentRequestExecutorTest.class.getSimpleName());

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
	public void testDocumentRequestTranslationWithRefresh() {
		doTestDocumentRequestTranslation(true);
	}

	protected void doTestDocumentRequestTranslation(boolean refresh) {
		BooleanQuery booleanQuery = new BooleanQueryImpl();

		booleanQuery.addExactTerm(_FIELD_NAME, true);

		UpdateByQueryDocumentRequest updateByQueryDocumentRequest =
			new UpdateByQueryDocumentRequest(
				booleanQuery, null, new String[] {_INDEX_NAME});

		updateByQueryDocumentRequest.setRefresh(refresh);

		UpdateByQueryDocumentRequestExecutorImpl
			updateByQueryDocumentRequestExecutorImpl =
				new UpdateByQueryDocumentRequestExecutorImpl();

		ElasticsearchQueryTranslatorFixture
			lecacyElasticsearchQueryTranslatorFixture =
				new ElasticsearchQueryTranslatorFixture();

		ReflectionTestUtil.setFieldValue(
			updateByQueryDocumentRequestExecutorImpl,
			"_elasticsearchClientResolver", _elasticsearchFixture);
		ReflectionTestUtil.setFieldValue(
			updateByQueryDocumentRequestExecutorImpl, "_legacyQueryTranslator",
			lecacyElasticsearchQueryTranslatorFixture.
				getElasticsearchQueryTranslator());

		ReflectionTestUtil.setFieldValue(
			updateByQueryDocumentRequestExecutorImpl, "_queryTranslator",
			new ElasticsearchQueryTranslator());

		ReflectionTestUtil.setFieldValue(
			updateByQueryDocumentRequestExecutorImpl, "_scripts", _scripts);

		UpdateByQueryRequest updateByQueryRequest =
			updateByQueryDocumentRequestExecutorImpl.createUpdateByQueryRequest(
				updateByQueryDocumentRequest);

		Assert.assertArrayEquals(
			new String[] {_INDEX_NAME}, updateByQueryRequest.indices());

		Assert.assertEquals(
			updateByQueryDocumentRequest.isRefresh(),
			updateByQueryRequest.isRefresh());

		String queryString = String.valueOf(
			updateByQueryRequest.getSearchRequest());

		Assert.assertTrue(queryString.contains(_FIELD_NAME));
		Assert.assertTrue(queryString.contains("\"value\":\"true\""));
	}

	private static final String _FIELD_NAME = "testField";

	private static final String _INDEX_NAME = "test_request_index";

	private static final Scripts _scripts = new ScriptsImpl();

	private ElasticsearchFixture _elasticsearchFixture;

}