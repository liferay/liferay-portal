/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.index;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Petteri Karttunen
 */
public class MCPToolIndexCreatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_indexNameBuilder.getIndexName(_COMPANY_ID)
		).thenReturn(
			"liferay-" + _COMPANY_ID
		);

		ReflectionTestUtil.setFieldValue(
			_mcpToolIndexCreator, "_indexNameBuilder", _indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			_mcpToolIndexCreator, "_jsonFactory", _jsonFactory);
		ReflectionTestUtil.setFieldValue(
			_mcpToolIndexCreator, "_searchEngineAdapter", _searchEngineAdapter);
	}

	@Test
	public void testCreateIfNotExists() throws Exception {
		_mockIndexExists(false, false);

		_mcpToolIndexCreator.createIfNotExists(_COMPANY_ID);

		ArgumentCaptor<CreateIndexRequest> argumentCaptor =
			ArgumentCaptor.forClass(CreateIndexRequest.class);

		Mockito.verify(
			_searchEngineAdapter
		).execute(
			argumentCaptor.capture()
		);

		CreateIndexRequest createIndexRequest = argumentCaptor.getValue();

		Assert.assertEquals(_getIndexName(), createIndexRequest.getIndexName());

		JSONObject mappingsJSONObject = _jsonFactory.createJSONObject(
			createIndexRequest.getMappings());

		Assert.assertNotNull(mappingsJSONObject.getJSONObject("properties"));

		Assert.assertFalse(
			JSONUtil.isEmpty(
				JSONUtil.getValueAsJSONArray(
					_jsonFactory.createJSONObject(
						createIndexRequest.getSettings()),
					"JSONObject/analysis", "JSONObject/filter",
					"JSONObject/mcp_tool_synonym", "JSONArray/synonyms")));
	}

	@Test
	public void testCreateIfNotExistsWhenIndexExists() {
		_mockIndexExists(true, true);

		_mcpToolIndexCreator.createIfNotExists(_COMPANY_ID);

		Mockito.verify(
			_searchEngineAdapter, Mockito.never()
		).execute(
			Mockito.any(CreateIndexRequest.class)
		);
	}

	@Test(expected = RuntimeException.class)
	public void testCreateIfNotExistsWhenRequestFails() {
		_mockIndexExists(false, false);

		Mockito.when(
			_searchEngineAdapter.execute(Mockito.any(CreateIndexRequest.class))
		).thenThrow(
			new RuntimeException()
		);

		_mcpToolIndexCreator.createIfNotExists(_COMPANY_ID);
	}

	@Test
	public void testCreateIfNotExistsWhenRequestFailsAfterConcurrentCreate() {
		_mockIndexExists(false, true);

		Mockito.when(
			_searchEngineAdapter.execute(Mockito.any(CreateIndexRequest.class))
		).thenThrow(
			new RuntimeException()
		);

		_mcpToolIndexCreator.createIfNotExists(_COMPANY_ID);
	}

	@Test
	public void testDeleteIfExists() {
		_mockIndexExists(true, true);

		_mcpToolIndexCreator.deleteIfExists(_COMPANY_ID);

		ArgumentCaptor<DeleteIndexRequest> argumentCaptor =
			ArgumentCaptor.forClass(DeleteIndexRequest.class);

		Mockito.verify(
			_searchEngineAdapter
		).execute(
			argumentCaptor.capture()
		);

		DeleteIndexRequest deleteIndexRequest = argumentCaptor.getValue();

		Assert.assertArrayEquals(
			new String[] {_getIndexName()}, deleteIndexRequest.getIndexNames());
	}

	@Test
	public void testDeleteIfExistsWhenIndexDoesNotExist() {
		_mockIndexExists(false, false);

		_mcpToolIndexCreator.deleteIfExists(_COMPANY_ID);

		Mockito.verify(
			_searchEngineAdapter, Mockito.never()
		).execute(
			Mockito.any(DeleteIndexRequest.class)
		);
	}

	@Test(expected = RuntimeException.class)
	public void testDeleteIfExistsWhenRequestFails() {
		_mockIndexExists(true, true);

		Mockito.when(
			_searchEngineAdapter.execute(Mockito.any(DeleteIndexRequest.class))
		).thenThrow(
			new RuntimeException()
		);

		_mcpToolIndexCreator.deleteIfExists(_COMPANY_ID);
	}

	@Test
	public void testDeleteIfExistsWhenRequestFailsAfterConcurrentDelete() {
		_mockIndexExists(true, false);

		Mockito.when(
			_searchEngineAdapter.execute(Mockito.any(DeleteIndexRequest.class))
		).thenThrow(
			new RuntimeException()
		);

		_mcpToolIndexCreator.deleteIfExists(_COMPANY_ID);
	}

	private String _getIndexName() {
		return "liferay-" + _COMPANY_ID + "-mcp-tools";
	}

	private void _mockIndexExists(boolean exists, boolean existsAfterFailure) {
		Mockito.when(
			_searchEngineAdapter.execute(
				Mockito.any(IndicesExistsIndexRequest.class))
		).thenReturn(
			new IndicesExistsIndexResponse(exists),
			new IndicesExistsIndexResponse(existsAfterFailure)
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private final IndexNameBuilder _indexNameBuilder = Mockito.mock(
		IndexNameBuilder.class);
	private final JSONFactory _jsonFactory = new JSONFactoryImpl();
	private final MCPToolIndexCreator _mcpToolIndexCreator =
		new MCPToolIndexCreator();
	private final SearchEngineAdapter _searchEngineAdapter = Mockito.mock(
		SearchEngineAdapter.class);

}