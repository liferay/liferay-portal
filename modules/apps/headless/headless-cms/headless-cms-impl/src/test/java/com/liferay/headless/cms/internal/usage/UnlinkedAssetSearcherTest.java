/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.usage;

import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;

import java.util.Collections;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mikel Lorza
 */
public class UnlinkedAssetSearcherTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-105769")
	public void testSearch() {
		_setUpLocalizationUtil();

		SearchRequestBuilder searchRequestBuilder = Mockito.mock(
			SearchRequestBuilder.class, Mockito.RETURNS_SELF);

		SiteNavigationMenuItemLocalService siteNavigationMenuItemLocalService =
			Mockito.mock(SiteNavigationMenuItemLocalService.class);

		Mockito.when(
			siteNavigationMenuItemLocalService.dslQuery(Mockito.any())
		).thenReturn(
			Collections.emptyList()
		);

		UnlinkedAssetSearcher unlinkedAssetSearcher = new UnlinkedAssetSearcher(
			Mockito.mock(Aggregations.class, Mockito.RETURNS_MOCKS),
			Mockito.mock(AssetLinkLocalService.class),
			Mockito.mock(ObjectDefinitionLocalService.class),
			Mockito.mock(ObjectEntryLocalService.class),
			Mockito.mock(ObjectRelationshipLocalService.class), _getSearcher(),
			_getSearchRequestBuilderFactory(searchRequestBuilder),
			siteNavigationMenuItemLocalService);

		unlinkedAssetSearcher.search(
			RandomTestUtil.randomLong(),
			new Long[] {RandomTestUtil.randomLong()},
			RandomTestUtil.randomString(), Pagination.of(501, 20), null, null);

		Mockito.verify(
			searchRequestBuilder
		).from(
			10000
		);

		Mockito.verify(
			searchRequestBuilder
		).size(
			20
		);
	}

	private SearchRequestBuilderFactory _getSearchRequestBuilderFactory(
		SearchRequestBuilder searchRequestBuilder) {

		SearchRequestBuilderFactory searchRequestBuilderFactory = Mockito.mock(
			SearchRequestBuilderFactory.class);

		Mockito.when(
			searchRequestBuilderFactory.builder()
		).thenReturn(
			searchRequestBuilder
		);

		return searchRequestBuilderFactory;
	}

	private Searcher _getSearcher() {
		Searcher searcher = Mockito.mock(Searcher.class);

		Mockito.when(
			searcher.search(Mockito.any())
		).thenReturn(
			Mockito.mock(SearchResponse.class)
		);

		return searcher;
	}

	private void _setUpLocalizationUtil() {
		LocalizationUtil localizationUtil = new LocalizationUtil();

		localizationUtil.setLocalization(Mockito.mock(Localization.class));
	}

}