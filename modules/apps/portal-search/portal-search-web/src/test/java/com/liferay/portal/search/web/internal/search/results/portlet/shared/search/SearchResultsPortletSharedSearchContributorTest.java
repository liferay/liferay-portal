/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.results.portlet.shared.search;

import com.liferay.petra.string.StringPool;
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
import com.liferay.portal.search.web.internal.search.results.portlet.SearchResultsPortletPreferences;
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

	@FeatureFlag("LPD-114635")
	@Test
	public void testContributeMaximumSearchDepth() {
		_testContributeMaximumSearchDepth(80, 10, 90, "5");
		_testContributeMaximumSearchDepth(80, 10, 90, "7");
		_testContributeMaximumSearchDepth(80, 20, 100, "5");
		_testContributeMaximumSearchDepth(80, 20, 100, "6");
	}

	@FeatureFlag(enable = false, value = "LPD-114635")
	@Test
	public void testContributeMaximumSearchDepthWhenFeatureFlagIsDisabled() {
		_testContributeMaximumSearchDepth(100, 20, 100, "6");
	}

	@FeatureFlag("LPD-114635")
	@Test
	public void testContributeMaximumSearchDepthWithFederatedSearch() {
		SearchRequestBuilder searchRequestBuilder =
			_createSearchRequestBuilder();

		_contribute(
			false, 10,
			_createPortletSharedSearchSettings("5", "4", searchRequestBuilder));

		PortletSharedSearchSettings portletSharedSearchSettings =
			_createPortletSharedSearchSettings("5", "4", searchRequestBuilder);

		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);
		String federatedSearchKey = RandomTestUtil.randomString();

		Mockito.doReturn(
			federatedSearchKey
		).when(
			portletPreferences
		).getValue(
			SearchResultsPortletPreferences.PREFERENCE_KEY_FEDERATED_SEARCH_KEY,
			StringPool.BLANK
		);

		Mockito.doReturn(
			portletPreferences
		).when(
			portletSharedSearchSettings
		).getPortletPreferences();

		_contribute(false, 20, portletSharedSearchSettings);

		SearchRequest searchRequest = searchRequestBuilder.build();

		Assert.assertEquals(Integer.valueOf(5), searchRequest.getFrom());
		Assert.assertEquals(Integer.valueOf(5), searchRequest.getSize());

		SearchRequestBuilder federatedSearchRequestBuilder =
			searchRequestBuilder.getFederatedSearchRequestBuilder(
				federatedSearchKey);

		SearchRequest federatedSearchRequest =
			federatedSearchRequestBuilder.build();

		Assert.assertEquals(
			Integer.valueOf(15), federatedSearchRequest.getFrom());
		Assert.assertEquals(
			Integer.valueOf(5), federatedSearchRequest.getSize());
	}

	@FeatureFlag("LPD-114635")
	@Test
	public void testContributeMaximumSearchDepthWithMultipleWidgets() {
		_testContributeMaximumSearchDepthWithMultipleWidgets(40, 10, 100, 50);
		_testContributeMaximumSearchDepthWithMultipleWidgets(40, 10, 50, 100);
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
		boolean limitResultCountAccuracy, int maximumSearchDepth,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		_contribute(
			limitResultCountAccuracy, maximumSearchDepth,
			portletSharedSearchSettings);

		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(null);

		return searchRequestBuilder.build();
	}

	private void _contribute(
		boolean limitResultCountAccuracy, int maximumSearchDepth,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchResultsPortletSharedSearchContributor
			searchResultsPortletSharedSearchContributor =
				new SearchResultsPortletSharedSearchContributor();

		ReflectionTestUtil.setFieldValue(
			searchResultsPortletSharedSearchContributor,
			"_configurationProvider",
			_createConfigurationProvider(
				limitResultCountAccuracy, maximumSearchDepth));

		searchResultsPortletSharedSearchContributor.contribute(
			portletSharedSearchSettings);
	}

	private ConfigurationProvider _createConfigurationProvider(
		boolean limitResultCountAccuracy, int maximumSearchDepth) {

		SearchResultsPortletInstanceConfiguration
			searchResultsPortletInstanceConfiguration = Mockito.mock(
				SearchResultsPortletInstanceConfiguration.class);

		Mockito.doReturn(
			limitResultCountAccuracy
		).when(
			searchResultsPortletInstanceConfiguration
		).limitResultCountAccuracy();

		Mockito.doReturn(
			maximumSearchDepth
		).when(
			searchResultsPortletInstanceConfiguration
		).maximumSearchDepth();

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
		String paginationDeltaParameterValue,
		String paginationStartParameterValue,
		SearchRequestBuilder searchRequestBuilder) {

		PortletSharedSearchSettings portletSharedSearchSettings = Mockito.mock(
			PortletSharedSearchSettings.class);

		Mockito.doAnswer(
			invocationOnMock ->
				searchRequestBuilder.getFederatedSearchRequestBuilder(
					invocationOnMock.getArgument(0))
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
			paginationStartParameterValue
		).when(
			portletSharedSearchSettings
		).getParameter(
			"start"
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

	private SearchRequestBuilder _createSearchRequestBuilder() {
		return new SearchRequestBuilderImpl(
			new SearchRequestBuilderFactoryImpl());
	}

	private void _testContribute(
		int expectedPaginationDelta, String paginationDeltaParameterValue) {

		PortletSharedSearchSettings portletSharedSearchSettings =
			_createPortletSharedSearchSettings(
				paginationDeltaParameterValue, null,
				_createSearchRequestBuilder());

		SearchRequest searchRequest = _buildSearchRequest(
			false, Integer.MAX_VALUE, portletSharedSearchSettings);

		Mockito.verify(
			portletSharedSearchSettings
		).setPaginationDelta(
			expectedPaginationDelta
		);

		Assert.assertEquals(
			Integer.valueOf(expectedPaginationDelta), searchRequest.getSize());
	}

	private void _testContributeMaximumSearchDepth(
		int expectedFrom, int expectedSize, int maximumSearchDepth,
		String paginationStartParameterValue) {

		PortletSharedSearchSettings portletSharedSearchSettings =
			_createPortletSharedSearchSettings(
				"20", paginationStartParameterValue,
				_createSearchRequestBuilder());

		SearchRequest searchRequest = _buildSearchRequest(
			false, maximumSearchDepth, portletSharedSearchSettings);

		Assert.assertEquals(
			Integer.valueOf(expectedFrom), searchRequest.getFrom());
		Assert.assertEquals(
			Integer.valueOf(expectedSize), searchRequest.getSize());

		Mockito.verify(
			portletSharedSearchSettings
		).setPaginationStart(
			(expectedFrom / 20) + 1
		);
	}

	private void _testContributeMaximumSearchDepthWithMultipleWidgets(
		int expectedFrom, int expectedSize, int maximumSearchDepth1,
		int maximumSearchDepth2) {

		SearchRequestBuilder searchRequestBuilder =
			_createSearchRequestBuilder();

		_contribute(
			false, maximumSearchDepth1,
			_createPortletSharedSearchSettings(
				"20", "3", searchRequestBuilder));
		_contribute(
			false, maximumSearchDepth2,
			_createPortletSharedSearchSettings(
				"20", "3", searchRequestBuilder));

		SearchRequest searchRequest = searchRequestBuilder.build();

		Assert.assertEquals(
			Integer.valueOf(expectedFrom), searchRequest.getFrom());
		Assert.assertEquals(
			Integer.valueOf(expectedSize), searchRequest.getSize());
	}

	private void _testContributeTrackTotalHitsLimit(
		boolean limitResultCountAccuracy, Integer expectedTrackTotalHitsLimit) {

		SearchRequest searchRequest = _buildSearchRequest(
			limitResultCountAccuracy, Integer.MAX_VALUE,
			_createPortletSharedSearchSettings(
				null, null, _createSearchRequestBuilder()));

		Assert.assertEquals(
			expectedTrackTotalHitsLimit,
			searchRequest.getTrackTotalHitsLimit());
	}

	private static final String _PORTLET_ID = RandomTestUtil.randomString();

	private final Layout _layout = Mockito.mock(Layout.class);
	private final ThemeDisplay _themeDisplay = new ThemeDisplay();

}