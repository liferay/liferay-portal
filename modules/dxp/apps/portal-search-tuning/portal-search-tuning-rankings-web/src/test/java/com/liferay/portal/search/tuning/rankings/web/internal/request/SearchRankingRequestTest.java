/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.request;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.MatchAllQuery;
import com.liferay.portal.search.query.MatchQuery;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.web.internal.BaseRankingsWebTestCase;
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
public class SearchRankingRequestTest extends BaseRankingsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpQuery();

		setUpPortletPreferencesFactoryUtil();
		setUpPropsUtil();

		HttpServletRequest httpServletRequest =
			setUpPortalGetHttpServletRequest();

		setUpHttpServletRequestGetAttribute(
			httpServletRequest, WebKeys.THEME_DISPLAY,
			Mockito.mock(ThemeDisplay.class));

		_searchRankingRequest = new SearchRankingRequest(
			httpServletRequest, queries, _rankingIndexName, _sorts,
			Mockito.mock(SearchContainer.class), searchEngineAdapter);
	}

	@Test
	public void testSearch() throws Exception {
		SearchHits searchHits = setUpSearchEngineAdapter(
			Mockito.mock(SearchHits.class));

		SearchRankingResponse searchRankingResponse =
			_searchRankingRequest.search();

		Assert.assertEquals(searchHits, searchRankingResponse.getSearchHits());
	}

	@Test
	public void testSearchWithBlankKeyword() throws Exception {
		SearchHits searchHits = setUpSearchEngineAdapter(
			Mockito.mock(SearchHits.class));

		SearchRankingResponse searchRankingResponse =
			_searchRankingRequest.search();

		Assert.assertEquals(searchHits, searchRankingResponse.getSearchHits());
	}

	private void _setUpQuery() {
		Mockito.doReturn(
			Mockito.mock(BooleanQuery.class)
		).when(
			queries
		).booleanQuery();

		Mockito.doReturn(
			Mockito.mock(MatchQuery.class)
		).when(
			queries
		).match(
			Mockito.anyString(), Mockito.anyString()
		);

		Mockito.doReturn(
			Mockito.mock(MatchAllQuery.class)
		).when(
			queries
		).matchAll();
	}

	private final RankingIndexName _rankingIndexName = Mockito.mock(
		RankingIndexName.class);
	private SearchRankingRequest _searchRankingRequest;
	private final Sorts _sorts = Mockito.mock(Sorts.class);

}