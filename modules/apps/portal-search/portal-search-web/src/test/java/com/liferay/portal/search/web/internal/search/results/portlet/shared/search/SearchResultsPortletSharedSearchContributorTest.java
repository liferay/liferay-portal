/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.results.portlet.shared.search;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.search.internal.legacy.searcher.SearchRequestBuilderImpl;
import com.liferay.portal.search.internal.searcher.SearchRequestBuilderFactoryImpl;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.web.internal.search.results.portlet.SearchResultsPortletPreferences;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Olivia Yu
 */
public class SearchResultsPortletSharedSearchContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testContribute() {
		_testContribute(
			PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA,
			String.valueOf(PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA + 1));

		int paginationDelta = PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA - 1;

		_testContribute(paginationDelta, String.valueOf(paginationDelta));
	}

	@FeatureFlag("LPD-98858")
	@Test
	public void testContributeTrackTotalHitsLimit() {
		_testContributeTrackTotalHitsLimit("0", 0);
		_testContributeTrackTotalHitsLimit(null, 1000);

		int accurateCountLimit = RandomTestUtil.randomInt();

		_testContributeTrackTotalHitsLimit(
			String.valueOf(accurateCountLimit), accurateCountLimit);
	}

	@FeatureFlag(enable = false, value = "LPD-98858")
	@Test
	public void testContributeTrackTotalHitsLimitWhenFeatureFlagIsDisabled() {
		_testContributeTrackTotalHitsLimit(
			String.valueOf(RandomTestUtil.randomInt()), null);
		_testContributeTrackTotalHitsLimit(null, null);
	}

	private SearchRequest _buildSearchRequest(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchResultsPortletSharedSearchContributor
			searchResultsPortletSharedSearchContributor =
				new SearchResultsPortletSharedSearchContributor();

		searchResultsPortletSharedSearchContributor.contribute(
			portletSharedSearchSettings);

		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(null);

		return searchRequestBuilder.build();
	}

	private PortletPreferences _createPortletPreferences(
		String accurateCountLimitPreferenceValue) {

		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);

		Mockito.doReturn(
			accurateCountLimitPreferenceValue
		).when(
			portletPreferences
		).getValue(
			SearchResultsPortletPreferences.PREFERENCE_KEY_ACCURATE_COUNT_LIMIT,
			StringPool.BLANK
		);

		return portletPreferences;
	}

	private PortletSharedSearchSettings _createPortletSharedSearchSettings(
		String accurateCountLimitPreferenceValue,
		String paginationDeltaParameterValue) {

		PortletSharedSearchSettings portletSharedSearchSettings = Mockito.mock(
			PortletSharedSearchSettings.class);

		SearchRequestBuilder searchRequestBuilder =
			new SearchRequestBuilderImpl(new SearchRequestBuilderFactoryImpl());

		Mockito.doReturn(
			searchRequestBuilder
		).when(
			portletSharedSearchSettings
		).getFederatedSearchRequestBuilder(
			Mockito.any()
		);

		Mockito.doReturn(
			paginationDeltaParameterValue
		).when(
			portletSharedSearchSettings
		).getParameter(
			"delta"
		);

		Mockito.doReturn(
			_createPortletPreferences(accurateCountLimitPreferenceValue)
		).when(
			portletSharedSearchSettings
		).getPortletPreferences();

		return portletSharedSearchSettings;
	}

	private void _testContribute(
		int expectedPaginationDelta, String paginationDeltaParameterValue) {

		PortletSharedSearchSettings portletSharedSearchSettings =
			_createPortletSharedSearchSettings(
				null, paginationDeltaParameterValue);

		SearchRequest searchRequest = _buildSearchRequest(
			portletSharedSearchSettings);

		Mockito.verify(
			portletSharedSearchSettings
		).setPaginationDelta(
			expectedPaginationDelta
		);

		Assert.assertEquals(
			Integer.valueOf(expectedPaginationDelta), searchRequest.getSize());
	}

	private void _testContributeTrackTotalHitsLimit(
		String accurateCountLimitPreferenceValue,
		Integer expectedTrackTotalHitsLimit) {

		SearchRequest searchRequest = _buildSearchRequest(
			_createPortletSharedSearchSettings(
				accurateCountLimitPreferenceValue, null));

		Assert.assertEquals(
			expectedTrackTotalHitsLimit,
			searchRequest.getTrackTotalHitsLimit());
	}

}