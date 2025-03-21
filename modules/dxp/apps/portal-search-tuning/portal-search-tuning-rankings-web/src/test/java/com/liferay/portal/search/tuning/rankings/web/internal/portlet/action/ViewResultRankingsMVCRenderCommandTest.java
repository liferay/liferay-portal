/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.portlet.action;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.tuning.rankings.index.RankingBuilderFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.RenderURL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class ViewResultRankingsMVCRenderCommandTest
	extends BaseRankingsPortletActionTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_viewResultRankingsMVCRenderCommand =
			new ViewResultRankingsMVCRenderCommand();

		setUpPortletPreferencesFactoryUtil();

		ReflectionTestUtil.setFieldValue(
			_viewResultRankingsMVCRenderCommand, "_rankingBuilderFactory",
			_rankingBuilderFactory);
		ReflectionTestUtil.setFieldValue(
			_viewResultRankingsMVCRenderCommand, "indexNameBuilder",
			_indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			_viewResultRankingsMVCRenderCommand, "language", language);
		ReflectionTestUtil.setFieldValue(
			_viewResultRankingsMVCRenderCommand, "portal", portal);
		ReflectionTestUtil.setFieldValue(
			_viewResultRankingsMVCRenderCommand, "queries", queries);
		ReflectionTestUtil.setFieldValue(
			_viewResultRankingsMVCRenderCommand, "rankingIndexNameBuilder",
			rankingIndexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			_viewResultRankingsMVCRenderCommand, "searchEngineAdapter",
			searchEngineAdapter);
		ReflectionTestUtil.setFieldValue(
			_viewResultRankingsMVCRenderCommand, "searchEngineInformation",
			_searchEngineInformation);
		ReflectionTestUtil.setFieldValue(
			_viewResultRankingsMVCRenderCommand, "searchRequestBuilderFactory",
			_searchRequestBuilderFactory);
		ReflectionTestUtil.setFieldValue(
			_viewResultRankingsMVCRenderCommand, "sorts", _sorts);
	}

	@Test
	public void testRender() throws Exception {
		_setUpRenderResponse();

		setUpLanguageUtil("");
		setUpPortal();
		setUpPortalUtil();
		setUpPropsUtil();
		setUpQuery();
		setUpRankingIndexNameBuilder();
		setUpRenderResponse(_renderResponse);
		setUpSearchEngineAdapter(Mockito.mock(SearchHits.class));

		_viewResultRankingsMVCRenderCommand.render(
			_renderRequest, _renderResponse);

		Assert.assertEquals(
			"/view.jsp",
			_viewResultRankingsMVCRenderCommand.render(
				_renderRequest, _renderResponse));
	}

	private void _setUpRenderResponse() {
		Mockito.doReturn(
			Mockito.mock(RenderURL.class)
		).when(
			_renderResponse
		).createRenderURL();
	}

	private final IndexNameBuilder _indexNameBuilder = Mockito.mock(
		IndexNameBuilder.class);
	private final RankingBuilderFactory _rankingBuilderFactory = Mockito.mock(
		RankingBuilderFactory.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);
	private final SearchEngineInformation _searchEngineInformation =
		Mockito.mock(SearchEngineInformation.class);
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory =
		Mockito.mock(SearchRequestBuilderFactory.class);
	private final Sorts _sorts = Mockito.mock(Sorts.class);
	private ViewResultRankingsMVCRenderCommand
		_viewResultRankingsMVCRenderCommand;

}