/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.request;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexName;
import com.liferay.portal.search.tuning.synonyms.web.internal.BaseSynonymsWebTestCase;
import com.liferay.portal.search.tuning.synonyms.web.internal.display.context.SynonymSetDisplayContext;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class SearchSynonymSetRequestTest extends BaseSynonymsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		setUpPortletPreferencesFactoryUtil();
	}

	@Test
	public void testSearchWithEmptyKeywords() {
		setUpPortal(_httpServletRequest);

		SearchHits searchHits = Mockito.mock(SearchHits.class);

		setUpSearchEngineAdapter(searchHits);

		_searchSynonymSetRequest = new SearchSynonymSetRequest(
			_synonymSetIndexName, _httpServletRequest, _queries, _sorts,
			_searchContainer, searchEngineAdapter);

		SearchSynonymSetResponse searchSynonymSetResponse =
			_searchSynonymSetRequest.search();

		Assert.assertEquals(
			searchHits, searchSynonymSetResponse.getSearchHits());

		Mockito.verify(
			_queries, Mockito.never()
		).match(
			Mockito.anyString(), Mockito.anyString()
		);

		Mockito.verify(
			_queries, Mockito.times(1)
		).matchAll();
	}

	@Test
	public void testSearchWithNotEmptyKeywords() {
		setUpHttpServletRequestParameterValue(
			_httpServletRequest, "keywords", "keywords");
		setUpPortal(_httpServletRequest);

		SearchHits searchHits = Mockito.mock(SearchHits.class);

		setUpSearchEngineAdapter(searchHits);

		_searchSynonymSetRequest = new SearchSynonymSetRequest(
			_synonymSetIndexName, _httpServletRequest, _queries, _sorts,
			_searchContainer, searchEngineAdapter);

		SearchSynonymSetResponse searchSynonymSetResponse =
			_searchSynonymSetRequest.search();

		Assert.assertEquals(
			searchHits, searchSynonymSetResponse.getSearchHits());

		Mockito.verify(
			_queries, Mockito.times(1)
		).match(
			Mockito.anyString(), Mockito.anyString()
		);

		Mockito.verify(
			_queries, Mockito.never()
		).matchAll();
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Queries _queries = Mockito.mock(Queries.class);
	private final SearchContainer<SynonymSetDisplayContext> _searchContainer =
		Mockito.mock(SearchContainer.class);
	private SearchSynonymSetRequest _searchSynonymSetRequest;
	private final Sorts _sorts = Mockito.mock(Sorts.class);
	private final SynonymSetIndexName _synonymSetIndexName = Mockito.mock(
		SynonymSetIndexName.class);

}