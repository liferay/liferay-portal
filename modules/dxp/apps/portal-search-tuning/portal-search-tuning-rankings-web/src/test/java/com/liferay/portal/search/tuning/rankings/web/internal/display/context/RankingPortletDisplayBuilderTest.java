/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.tuning.rankings.index.RankingBuilderFactory;
import com.liferay.portal.search.tuning.rankings.web.internal.BaseRankingsWebTestCase;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class RankingPortletDisplayBuilderTest extends BaseRankingsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		setUpPortletPreferencesFactoryUtil();

		_rankingPortletDisplayBuilder = new RankingPortletDisplayBuilder(
			_httpServletRequest, language, portal, queries,
			_rankingBuilderFactory, rankingIndexNameBuilder, _renderRequest,
			_renderResponse, searchEngineAdapter, _searchEngineInformation,
			_sorts);
	}

	@Test
	public void testBuild() throws Exception {
		setUpHttpServletRequestParamValue(
			_httpServletRequest, "displayStyle", "displayStyle");
		setUpHttpServletRequestParamValue(
			_httpServletRequest, "orderByType", "desc");
		setUpHttpServletRequestAttribute(
			_httpServletRequest, WebKeys.THEME_DISPLAY,
			Mockito.mock(ThemeDisplay.class));

		setUpLanguageUtil("");
		setUpPortal();
		setUpPortalUtil();
		setUpQuery();
		setUpRankingIndexNameBuilder();
		setUpRenderResponse(_renderResponse);
		setUpSearchEngineAdapter(Mockito.mock(SearchHits.class));

		RankingPortletDisplayContext rankingPortletDisplayContext =
			_rankingPortletDisplayBuilder.build();

		Assert.assertEquals(
			"", rankingPortletDisplayContext.getClearResultsURL());
		Assert.assertNotNull(rankingPortletDisplayContext.getCreationMenu());
		Assert.assertEquals(
			"displayStyle", rankingPortletDisplayContext.getDisplayStyle());
		Assert.assertEquals(
			"desc", rankingPortletDisplayContext.getOrderByType());
		Assert.assertEquals(
			"", rankingPortletDisplayContext.getSearchActionURL());
		Assert.assertNotNull(rankingPortletDisplayContext.getSearchContainer());
		Assert.assertEquals("", rankingPortletDisplayContext.getSortingURL());
		Assert.assertEquals(3, rankingPortletDisplayContext.getTotalItems());
		Assert.assertFalse(
			rankingPortletDisplayContext.isDisabledManagementBar());
		Assert.assertTrue(rankingPortletDisplayContext.isShowCreationMenu());

		List<DropdownItem> dropdownItems =
			rankingPortletDisplayContext.getActionDropdownItems();

		Assert.assertEquals(dropdownItems.toString(), 3, dropdownItems.size());

		dropdownItems =
			rankingPortletDisplayContext.getFilterItemsDropdownItems();

		Assert.assertEquals(dropdownItems.toString(), 2, dropdownItems.size());
	}

	@Override
	protected HttpServletRequest setUpPortalGetHttpServletRequest() {
		Mockito.doReturn(
			_httpServletRequest
		).when(
			portal
		).getHttpServletRequest(
			Mockito.any(PortletRequest.class)
		);

		return _httpServletRequest;
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final RankingBuilderFactory _rankingBuilderFactory = Mockito.mock(
		RankingBuilderFactory.class);
	private RankingPortletDisplayBuilder _rankingPortletDisplayBuilder;
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);
	private final SearchEngineInformation _searchEngineInformation =
		Mockito.mock(SearchEngineInformation.class);
	private final Sorts _sorts = Mockito.mock(Sorts.class);

}