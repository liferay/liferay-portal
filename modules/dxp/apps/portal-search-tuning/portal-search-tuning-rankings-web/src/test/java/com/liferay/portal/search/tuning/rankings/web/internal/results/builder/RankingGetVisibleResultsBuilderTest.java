/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.results.builder;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.search.tuning.rankings.index.Ranking;
import com.liferay.portal.search.tuning.rankings.web.internal.searcher.helper.RankingSearchRequestHelper;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class RankingGetVisibleResultsBuilderTest
	extends BaseRankingResultsBuilderTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpRankingSearchRequestHelper();

		_rankingGetVisibleResultsBuilder = new RankingGetVisibleResultsBuilder(
			complexQueryPartBuilderFactory, dlAppLocalService,
			fastDateFormatFactory, groupLocalService, rankingIndexName,
			rankingIndexReader, _rankingSearchRequestHelper, resourceActions,
			resourceRequest, resourceResponse, queries, searcher,
			searchRequestBuilderFactory);
	}

	@Test
	public void testBuild() throws Exception {
		setUpComplexQueryPartBuilderFactory(setUpComplexQueryPartBuilder());
		setUpDLAppLocalService();
		setUpFastDateFormatFactory();
		setUpPortalUtil();
		setUpQuery();

		Ranking ranking = Mockito.mock(Ranking.class);

		Mockito.doReturn(
			"defaultQueryString"
		).when(
			ranking
		).getQueryString();

		setUpRankingIndexReader(ranking);

		setUpResourceRequest();
		setUpSearchRequestBuilderFactory(setUpSearchRequestBuilder());
		setUpSearcher(setUpSearchResponse(setUpDocumentWithGetString()));

		Assert.assertEquals(
			objectMapper.readTree(_getExpectedDocumentsString()),
			objectMapper.readTree(
				_rankingGetVisibleResultsBuilder.build(
				).toJSONString()));
	}

	@Test
	public void testBuildWithRankingNotPresent() {
		setUpRankingIndexReader(null);

		Assert.assertEquals(
			JSONUtil.put(
				"documents", JSONFactoryUtil.createJSONArray()
			).put(
				"total", 0
			).toString(),
			_rankingGetVisibleResultsBuilder.build(
			).toString());
	}

	private String _getExpectedDocumentsString() {
		return JSONUtil.put(
			"documents",
			JSONUtil.put(
				JSONUtil.put(
					"author", "theAuthor"
				).put(
					"clicks", "theClicks"
				).put(
					"date", "20021209000109"
				).put(
					"deleted", false
				).put(
					"description", "undefined"
				).put(
					"hidden", false
				).put(
					"icon", "document-image"
				).put(
					"id", "theUID"
				).put(
					"pinned", false
				).put(
					"title", "theTitle"
				).put(
					"viewURL", ""
				))
		).put(
			"total", 1
		).toString();
	}

	private void _setUpRankingSearchRequestHelper() {
		Mockito.doNothing(
		).when(
			_rankingSearchRequestHelper
		).contribute(
			Mockito.any(), Mockito.any()
		);
	}

	private RankingGetVisibleResultsBuilder _rankingGetVisibleResultsBuilder;
	private final RankingSearchRequestHelper _rankingSearchRequestHelper =
		Mockito.mock(RankingSearchRequestHelper.class);

}