/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.internal.scheduler;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;

import java.lang.reflect.Method;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author Aquiles Duarte
 */
@RunWith(MockitoJUnitRunner.class)
public class
	WorkflowMetricsSLADefinitionTransformerSchedulerJobConfigurationTest {

	@Before
	public void setUp() {
		_originalIndexSearchLimit = PropsUtil.get(PropsKeys.INDEX_SEARCH_LIMIT);
	}

	@After
	public void tearDown() {
		PropsUtil.set(PropsKeys.INDEX_SEARCH_LIMIT, _originalIndexSearchLimit);
	}

	@Test
	public void testTransform() throws Exception {
		Integer indexSearchLimit = RandomTestUtil.randomInt();

		PropsUtil.set(
			PropsKeys.INDEX_SEARCH_LIMIT, String.valueOf(indexSearchLimit));

		Mockito.when(
			_searchCapabilities.isWorkflowMetricsSupported()
		).thenReturn(
			true
		);

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			_indexNameBuilder.getIndexName(companyId)
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_indicesExistsIndexResponse.isExists()
		).thenReturn(
			true
		);

		Mockito.when(
			_searchEngineAdapter.execute(
				Mockito.any(IndicesExistsIndexRequest.class))
		).thenReturn(
			_indicesExistsIndexResponse
		);

		Mockito.when(
			_queries.booleanQuery()
		).thenReturn(
			Mockito.mock(BooleanQuery.class)
		);

		Mockito.when(
			_searchHits.getSearchHits()
		).thenReturn(
			Collections.emptyList()
		);

		Mockito.when(
			_searchSearchResponse.getSearchHits()
		).thenReturn(
			_searchHits
		);

		Mockito.when(
			_searchEngineAdapter.execute(Mockito.any(SearchSearchRequest.class))
		).thenReturn(
			_searchSearchResponse
		);

		Method method =
			WorkflowMetricsSLADefinitionTransformerSchedulerJobConfiguration.
				class.getDeclaredMethod("_transform", long.class);

		method.setAccessible(true);

		method.invoke(
			_workflowMetricsSLADefinitionTransformerSchedulerJobConfiguration,
			companyId);

		ArgumentCaptor<SearchSearchRequest> argumentCaptor =
			ArgumentCaptor.forClass(SearchSearchRequest.class);

		Mockito.verify(
			_searchEngineAdapter
		).execute(
			argumentCaptor.capture()
		);

		Assert.assertEquals(
			indexSearchLimit,
			argumentCaptor.getValue(
			).getSize());
	}

	private final IndexNameBuilder _indexNameBuilder = Mockito.mock(
		IndexNameBuilder.class);
	private final IndicesExistsIndexResponse _indicesExistsIndexResponse =
		Mockito.mock(IndicesExistsIndexResponse.class);
	private String _originalIndexSearchLimit;
	private final Queries _queries = Mockito.mock(Queries.class);
	private final SearchCapabilities _searchCapabilities = Mockito.mock(
		SearchCapabilities.class);
	private final SearchEngineAdapter _searchEngineAdapter = Mockito.mock(
		SearchEngineAdapter.class);
	private final SearchHits _searchHits = Mockito.mock(SearchHits.class);
	private final SearchSearchResponse _searchSearchResponse = Mockito.mock(
		SearchSearchResponse.class);

	@InjectMocks
	private WorkflowMetricsSLADefinitionTransformerSchedulerJobConfiguration
		_workflowMetricsSLADefinitionTransformerSchedulerJobConfiguration;

}