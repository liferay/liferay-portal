/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.results.portlet.shared.search;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.search.internal.legacy.searcher.SearchRequestBuilderImpl;
import com.liferay.portal.search.internal.searcher.SearchRequestBuilderFactoryImpl;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.web.internal.search.results.configuration.SearchResultsPortletInstanceConfiguration;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.Before;
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

	@Before
	public void setUp() {
		_themeDisplay.setLayout(_layout);
	}

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
		_testContributeTrackTotalHitsLimit(false, null);
		_testContributeTrackTotalHitsLimit(true, 10001);
	}

	@FeatureFlag(enable = false, value = "LPD-98858")
	@Test
	public void testContributeTrackTotalHitsLimitWhenFeatureFlagIsDisabled() {
		_testContributeTrackTotalHitsLimit(false, null);
		_testContributeTrackTotalHitsLimit(true, null);
	}

	private SearchRequest _buildSearchRequest(
		boolean limitResultCountAccuracy,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchResultsPortletSharedSearchContributor
			searchResultsPortletSharedSearchContributor =
				new SearchResultsPortletSharedSearchContributor();

		ReflectionTestUtil.setFieldValue(
			searchResultsPortletSharedSearchContributor,
			"_configurationProvider",
			_createConfigurationProvider(limitResultCountAccuracy));

		searchResultsPortletSharedSearchContributor.contribute(
			portletSharedSearchSettings);

		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(null);

		return searchRequestBuilder.build();
	}

	private ConfigurationProvider _createConfigurationProvider(
		boolean limitResultCountAccuracy) {

		SearchResultsPortletInstanceConfiguration
			searchResultsPortletInstanceConfiguration = Mockito.mock(
				SearchResultsPortletInstanceConfiguration.class);

		Mockito.doReturn(
			limitResultCountAccuracy
		).when(
			searchResultsPortletInstanceConfiguration
		).limitResultCountAccuracy();

		ConfigurationProvider configurationProvider = Mockito.mock(
			ConfigurationProvider.class);

		try {
			Mockito.doReturn(
				searchResultsPortletInstanceConfiguration
			).when(
				configurationProvider
			).getPortletInstanceConfiguration(
				SearchResultsPortletInstanceConfiguration.class, _layout,
				_PORTLET_ID
			);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		return configurationProvider;
	}

	private PortletSharedSearchSettings _createPortletSharedSearchSettings(
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
			_PORTLET_ID
		).when(
			portletSharedSearchSettings
		).getPortletId();

		Mockito.doReturn(
			Mockito.mock(PortletPreferences.class)
		).when(
			portletSharedSearchSettings
		).getPortletPreferences();

		Mockito.doReturn(
			_themeDisplay
		).when(
			portletSharedSearchSettings
		).getThemeDisplay();

		return portletSharedSearchSettings;
	}

	private void _testContribute(
		int expectedPaginationDelta, String paginationDeltaParameterValue) {

		PortletSharedSearchSettings portletSharedSearchSettings =
			_createPortletSharedSearchSettings(paginationDeltaParameterValue);

		SearchRequest searchRequest = _buildSearchRequest(
			false, portletSharedSearchSettings);

		Mockito.verify(
			portletSharedSearchSettings
		).setPaginationDelta(
			expectedPaginationDelta
		);

		Assert.assertEquals(
			Integer.valueOf(expectedPaginationDelta), searchRequest.getSize());
	}

	private void _testContributeTrackTotalHitsLimit(
		boolean limitResultCountAccuracy, Integer expectedTrackTotalHitsLimit) {

		SearchRequest searchRequest = _buildSearchRequest(
			limitResultCountAccuracy, _createPortletSharedSearchSettings(null));

		Assert.assertEquals(
			expectedTrackTotalHitsLimit,
			searchRequest.getTrackTotalHitsLimit());
	}

	private static final String _PORTLET_ID = RandomTestUtil.randomString();

	private final Layout _layout = Mockito.mock(Layout.class);
	private final ThemeDisplay _themeDisplay = new ThemeDisplay();

}