/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2025-06
 */

package com.liferay.portal.search.solr8.internal.search.engine.adapter.search;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Rodrigo Guedes de Souza
 */
public class SearchSolrQueryAssemblerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_searchSolrQueryAssemblerImpl = new SearchSolrQueryAssemblerImpl();

		ReflectionTestUtil.setFieldValue(
			_searchSolrQueryAssemblerImpl, "_baseSolrQueryAssembler",
			Mockito.mock(BaseSolrQueryAssembler.class));
	}

	@Test
	public void testEntryClassNameField() {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setSorts(
			new Sort[] {new Sort(Field.ENTRY_CLASS_NAME, false)});

		_assertSort(
			Field.ENTRY_CLASS_NAME, SolrQuery.ORDER.asc, searchSearchRequest);
	}

	@Test
	public void testNondefaultSortableFieldAsc() {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setSorts(
			new Sort[] {new Sort(Field.ROLE_ID, Sort.LONG_TYPE, false)});

		_assertSort(
			Field.ROLE_ID + "_sortable", SolrQuery.ORDER.asc,
			searchSearchRequest);
	}

	@Test
	public void testNondefaultSortableFieldDesc() {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setSorts(
			new Sort[] {new Sort(Field.ROLE_ID, Sort.LONG_TYPE, true)});

		_assertSort(
			Field.ROLE_ID + "_sortable", SolrQuery.ORDER.desc,
			searchSearchRequest);
	}

	@Test
	public void testPriorityField() {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setSorts(
			new Sort[] {new Sort(Field.PRIORITY, false)});

		_assertSort(Field.PRIORITY, SolrQuery.ORDER.asc, searchSearchRequest);
	}

	@Test
	public void testWithoutSorts() {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		SolrQuery solrQuery = new SolrQuery();

		_searchSolrQueryAssemblerImpl.assemble(solrQuery, searchSearchRequest);

		List<SolrQuery.SortClause> sorts = solrQuery.getSorts();

		Assert.assertTrue(sorts.isEmpty());
	}

	private void _assertSort(
		String expectedFieldName, SolrQuery.ORDER expectedOrder,
		SearchSearchRequest searchSearchRequest) {

		SolrQuery solrQuery = new SolrQuery();

		_searchSolrQueryAssemblerImpl.assemble(solrQuery, searchSearchRequest);

		List<SolrQuery.SortClause> sorts = solrQuery.getSorts();

		SolrQuery.SortClause sort = sorts.get(0);

		Assert.assertEquals(expectedFieldName, sort.getItem());
		Assert.assertEquals(expectedOrder, sort.getOrder());
	}

	private static SearchSolrQueryAssemblerImpl _searchSolrQueryAssemblerImpl;

}