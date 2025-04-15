/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.custom.facet.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.BaseFacetDisplayContextTestCase;
import com.liferay.portal.search.web.internal.custom.facet.configuration.CustomFacetPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.custom.facet.display.context.builder.CustomFacetDisplayContextBuilder;
import com.liferay.portal.search.web.internal.facet.display.context.BucketDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.FacetDisplayContext;
import com.liferay.portal.search.web.internal.util.DateRangeFactoryUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Petteri Karttunen
 */
public class CustomFacetDisplayContextBuilderTest
	extends BaseFacetDisplayContextTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Override
	public FacetDisplayContext createFacetDisplayContext(String parameterValue)
		throws Exception {

		return createFacetDisplayContext(parameterValue, "count:desc");
	}

	@Override
	public FacetDisplayContext createFacetDisplayContext(
			String parameterValue, String order)
		throws ConfigurationException {

		configurationProviderUtilMockedStatic.when(
			() -> ConfigurationProviderUtil.getPortletInstanceConfiguration(
				Mockito.any(), Mockito.any())
		).thenReturn(
			Mockito.mock(CustomFacetPortletInstanceConfiguration.class)
		);

		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			new CustomFacetDisplayContextBuilder(
				_AGGREGATION_TYPE_TERMS, getHttpServletRequest());

		return customFacetDisplayContextBuilder.currentURL(
			"/search"
		).facet(
			facet
		).frequenciesVisible(
			true
		).order(
			order
		).parameterValue(
			parameterValue
		).build();
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		Mockito.doReturn(
			facetCollector
		).when(
			facet
		).getFacetCollector();

		Mockito.doReturn(
			_getFacetConfiguration(JSONFactoryUtil.createJSONObject())
		).when(
			facet
		).getFacetConfiguration();

		Mockito.doReturn(
			new SearchContext()
		).when(
			facet
		).getSearchContext();
	}

	@Test
	public void testAggregationTypeSet() throws Exception {
		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			_createCustomFacetDisplayContextBuilder(_AGGREGATION_TYPE_TERMS);

		CustomFacetDisplayContext customFacetDisplayContext =
			customFacetDisplayContextBuilder.build();

		Assert.assertEquals(
			_AGGREGATION_TYPE_TERMS,
			customFacetDisplayContext.getAggregationType());
	}

	@Test
	public void testCustomDateRangeHasFrequency() throws Exception {
		String from = "2018-01-01";
		String to = "2018-01-31";

		int frequency = RandomTestUtil.randomInt();

		_mockTermCollectorFrequency(
			_mockTermCollector(
				DateRangeFactoryUtil.getRangeString(
					from, to, TimeZoneUtil.GMT)),
			frequency);

		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			_createCustomFacetDisplayContextBuilder(
				_AGGREGATION_TYPE_DATE_RANGE);

		CustomFacetDisplayContext customFacetDisplayContext =
			customFacetDisplayContextBuilder.fromParameterValue(
				from
			).toParameterValue(
				to
			).build();

		BucketDisplayContext bucketDisplayContext =
			customFacetDisplayContext.getCustomRangeBucketDisplayContext();

		Assert.assertEquals(frequency, bucketDisplayContext.getFrequency());
	}

	@Test
	public void testCustomDateRangeHasTermCollectorFrequency()
		throws Exception {

		int frequency = RandomTestUtil.randomInt();

		_mockTermCollectorFrequency(_mockTermCollector(), frequency);

		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			_createCustomFacetDisplayContextBuilder(
				_AGGREGATION_TYPE_DATE_RANGE);

		CustomFacetDisplayContext customFacetDisplayContext =
			customFacetDisplayContextBuilder.fromParameterValue(
				"2018-01-01"
			).toParameterValue(
				"2018-01-31"
			).build();

		BucketDisplayContext bucketDisplayContext =
			customFacetDisplayContext.getCustomRangeBucketDisplayContext();

		Assert.assertEquals(frequency, bucketDisplayContext.getFrequency());
	}

	@Test
	public void testCustomDisplayCaption() throws Exception {
		String customDisplayCaption = "Custom Display Caption";

		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			_createCustomFacetDisplayContextBuilder(_AGGREGATION_TYPE_TERMS);

		CustomFacetDisplayContext customFacetDisplayContext =
			customFacetDisplayContextBuilder.customDisplayCaption(
				customDisplayCaption
			).build();

		Assert.assertEquals(
			customDisplayCaption,
			customFacetDisplayContext.getDisplayCaption());
	}

	@Test
	public void testDateRangeAggregationBucketDisplayContexts()
		throws Exception {

		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			_createCustomFacetDisplayContextBuilder(
				_AGGREGATION_TYPE_DATE_RANGE);

		_mockFacetConfiguration(
			"past-hour=[20180515225959 TO 20180515235959]",
			"some-time-ago=[20180508235959 TO 20180514235959]");

		CustomFacetDisplayContext customFacetDisplayContext =
			customFacetDisplayContextBuilder.build();

		List<BucketDisplayContext> bucketDisplayContexts =
			customFacetDisplayContext.getBucketDisplayContexts();

		Assert.assertEquals(
			bucketDisplayContexts.toString(), 2, bucketDisplayContexts.size());

		BucketDisplayContext bucketDisplayContext = bucketDisplayContexts.get(
			0);

		Assert.assertEquals("past-hour", bucketDisplayContext.getBucketText());

		bucketDisplayContext = bucketDisplayContexts.get(1);

		Assert.assertEquals(
			"some-time-ago", bucketDisplayContext.getBucketText());
	}

	@Override
	@Test
	public void testEmptySearchResults() throws Exception {
		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			_createCustomFacetDisplayContextBuilder(_AGGREGATION_TYPE_TERMS);

		CustomFacetDisplayContext customFacetDisplayContext =
			customFacetDisplayContextBuilder.totalHits(
				0
			).build();

		Assert.assertTrue(customFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testIsNothingSelected() throws Exception {
		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			_createCustomFacetDisplayContextBuilder(_AGGREGATION_TYPE_TERMS);

		CustomFacetDisplayContext customFacetDisplayContext =
			customFacetDisplayContextBuilder.build();

		Assert.assertTrue(customFacetDisplayContext.isNothingSelected());
	}

	@Test
	public void testIsNothingSelectedWithFromAndToAttributes()
		throws Exception {

		_mockFacetConfiguration("one=[200 TO 300]", "two=[400 TO 500]");

		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			_createCustomFacetDisplayContextBuilder(_AGGREGATION_TYPE_RANGE);

		CustomFacetDisplayContext customFacetDisplayContext =
			customFacetDisplayContextBuilder.fromParameterValue(
				"1"
			).toParameterValue(
				"100"
			).build();

		Assert.assertFalse(customFacetDisplayContext.isNothingSelected());
	}

	@Test
	public void testIsNothingSelectedWithSelectedDateRange() throws Exception {
		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			_createCustomFacetDisplayContextBuilder(
				_AGGREGATION_TYPE_DATE_RANGE);

		CustomFacetDisplayContext customFacetDisplayContext =
			customFacetDisplayContextBuilder.parameterValue(
				"past-24-hours"
			).build();

		Assert.assertFalse(customFacetDisplayContext.isNothingSelected());
	}

	@Test
	public void testIsRenderNothingFalseWithFromAndTo() throws Exception {
		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			_createCustomFacetDisplayContextBuilder(
				_AGGREGATION_TYPE_DATE_RANGE);

		CustomFacetDisplayContext customFacetDisplayContext =
			customFacetDisplayContextBuilder.aggregationField(
				RandomTestUtil.randomString()
			).fromParameterValue(
				"2018-01-01"
			).toParameterValue(
				"2018-01-31"
			).totalHits(
				0
			).build();

		Assert.assertFalse(customFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testIsRenderNothingFalseWithHits() throws Exception {
		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			_createCustomFacetDisplayContextBuilder(_AGGREGATION_TYPE_TERMS);

		CustomFacetDisplayContext customFacetDisplayContext =
			customFacetDisplayContextBuilder.aggregationField(
				RandomTestUtil.randomString()
			).totalHits(
				1
			).build();

		Assert.assertFalse(customFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testIsRenderNothingFalseWithSelectedDateRange()
		throws Exception {

		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			_createCustomFacetDisplayContextBuilder(
				_AGGREGATION_TYPE_DATE_RANGE);

		CustomFacetDisplayContext customFacetDisplayContext =
			customFacetDisplayContextBuilder.aggregationField(
				RandomTestUtil.randomString()
			).parameterValue(
				"past-24-hours"
			).totalHits(
				0
			).build();

		Assert.assertFalse(customFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testIsRenderNothingTrueWithNoHits() throws Exception {
		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			_createCustomFacetDisplayContextBuilder(_AGGREGATION_TYPE_TERMS);

		CustomFacetDisplayContext customFacetDisplayContext =
			customFacetDisplayContextBuilder.totalHits(
				0
			).build();

		Assert.assertTrue(customFacetDisplayContext.isRenderNothing());
	}

	@Override
	@Test
	public void testOneTerm() throws Exception {
		String term = createTerm();

		setUpAsset(term);

		String filterValue = getFilterValue(term);

		int frequency = RandomTestUtil.randomInt();

		setUpTermCollectors(
			facetCollector,
			Collections.singletonList(
				createTermCollector(filterValue, frequency)));

		String parameterValue = getFacetDisplayContextParameterValue();

		FacetDisplayContext facetDisplayContext = createFacetDisplayContext(
			parameterValue);

		List<BucketDisplayContext> bucketDisplayContexts =
			facetDisplayContext.getBucketDisplayContexts();

		Assert.assertEquals(
			bucketDisplayContexts.toString(), 1, bucketDisplayContexts.size());

		BucketDisplayContext bucketDisplayContext = bucketDisplayContexts.get(
			0);

		Assert.assertEquals(term, bucketDisplayContext.getBucketText());
		Assert.assertEquals(frequency, bucketDisplayContext.getFrequency());
		Assert.assertTrue(bucketDisplayContext.isFrequencyVisible());
		Assert.assertFalse(bucketDisplayContext.isSelected());

		Assert.assertTrue(facetDisplayContext.isNothingSelected());
		Assert.assertTrue(facetDisplayContext.isRenderNothing());
		Assert.assertEquals(
			parameterValue, facetDisplayContext.getParameterValue());
	}

	@Override
	@Test
	public void testOneTermWithPreviousSelection() throws Exception {
		String term = RandomTestUtil.randomString();

		setUpAsset(term);

		int frequency = RandomTestUtil.randomInt();

		String filterValue = getFilterValue(term);

		setUpTermCollectors(
			facetCollector,
			Collections.singletonList(
				createTermCollector(filterValue, frequency)));

		FacetDisplayContext facetDisplayContext = createFacetDisplayContext(
			filterValue);

		List<BucketDisplayContext> bucketDisplayContexts =
			facetDisplayContext.getBucketDisplayContexts();

		Assert.assertEquals(
			bucketDisplayContexts.toString(), 1, bucketDisplayContexts.size());

		BucketDisplayContext bucketDisplayContext = bucketDisplayContexts.get(
			0);

		Assert.assertEquals(term, bucketDisplayContext.getBucketText());
		Assert.assertEquals(frequency, bucketDisplayContext.getFrequency());
		Assert.assertTrue(bucketDisplayContext.isFrequencyVisible());
		Assert.assertTrue(bucketDisplayContext.isSelected());

		Assert.assertFalse(facetDisplayContext.isNothingSelected());
		Assert.assertFalse(facetDisplayContext.isRenderNothing());
	}

	@Test
	public void testOrderDateRangeByTermFrequencyAscending() throws Exception {
		_testOrderByDateRange(
			new int[] {1, 3, 3, 4},
			new String[] {
				"past-24-hours", "past-month", "past-week", "past-hour"
			},
			new int[] {4, 3, 3, 1}, "count:asc",
			new String[] {
				"[20180515225959 TO 20180515235959]",
				"[20180508235959 TO 20180508235959]",
				"[20180508235959 TO 20180415235959]",
				"[20180508235959 TO 20180514235959]"
			});
	}

	@Test
	public void testOrderDateRangeByTermFrequencyDescending() throws Exception {
		_testOrderByDateRange(
			new int[] {3, 3, 2, 1},
			new String[] {
				"past-24-hours", "past-month", "past-week", "past-hour"
			},
			new int[] {1, 2, 3, 3}, "count:desc",
			new String[] {
				"[20180515225959 TO 20180515235959]",
				"[20180508235959 TO 20180508235959]",
				"[20180508235959 TO 20180415235959]",
				"[20180508235959 TO 20180514235959]"
			});
	}

	@Override
	protected FacetDisplayContext getFacetDisplayContext(Group group)
		throws Exception {

		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			new CustomFacetDisplayContextBuilder(
				_AGGREGATION_TYPE_TERMS, _getHttpServletRequest(group));

		return customFacetDisplayContextBuilder.currentURL(
			"/search"
		).facet(
			facet
		).frequenciesVisible(
			true
		).build();
	}

	@Override
	protected void setUpPortletDisplayStyleGroupExternalReferenceCode(
		String externalReferenceCode) {

		CustomFacetPortletInstanceConfiguration
			customFacetPortletInstanceConfiguration = Mockito.mock(
				CustomFacetPortletInstanceConfiguration.class);

		Mockito.when(
			customFacetPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		configurationProviderUtilMockedStatic.when(
			() -> ConfigurationProviderUtil.getPortletInstanceConfiguration(
				Mockito.any(), Mockito.any())
		).thenReturn(
			customFacetPortletInstanceConfiguration
		);
	}

	@Override
	protected void testOrderBy(
			int[] expectedFrequencies, String[] expectedGroupNames,
			int[] frequencies, String order, String[] groupNames)
		throws Exception {

		setUpTermCollectors(
			facetCollector, getTermCollectors(groupNames, frequencies));

		FacetDisplayContext facetDisplayContext = createFacetDisplayContext(
			StringPool.BLANK, order);

		assertFacetOrder(
			facetDisplayContext.getBucketDisplayContexts(), expectedGroupNames,
			expectedFrequencies);
	}

	private CustomFacetDisplayContextBuilder
			_createCustomFacetDisplayContextBuilder(String aggregationType)
		throws Exception {

		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			new CustomFacetDisplayContextBuilder(
				aggregationType, getHttpServletRequest());

		customFacetDisplayContextBuilder.facet(
			facet
		).order(
			"count:desc"
		);

		ReflectionTestUtil.setFieldValue(
			customFacetDisplayContextBuilder, "_timeZone",
			TimeZoneUtil.getDefault());

		ReflectionTestUtil.setFieldValue(
			customFacetDisplayContextBuilder, "_locale",
			LocaleUtil.getDefault());

		_mockFacetConfiguration();

		_setUpCustomFacetPortletInstanceConfiguration(
			customFacetDisplayContextBuilder);

		return customFacetDisplayContextBuilder;
	}

	private CustomFacetDisplayContextBuilder
			_createCustomFacetDisplayContextBuilder(
				String aggregationType, String order)
		throws Exception {

		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			_createCustomFacetDisplayContextBuilder(aggregationType);

		customFacetDisplayContextBuilder.order(order);

		return customFacetDisplayContextBuilder;
	}

	private JSONArray _createRangesJSONArray(String... labelsAndRanges) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (String labelAndRange : labelsAndRanges) {
			String[] labelAndRangeArray = StringUtil.split(labelAndRange, '=');

			jsonArray.put(
				JSONUtil.put(
					"label", labelAndRangeArray[0]
				).put(
					"range", labelAndRangeArray[1]
				));
		}

		return jsonArray;
	}

	private FacetConfiguration _getFacetConfiguration(
		JSONObject dataJSONObject) {

		FacetConfiguration facetConfiguration = new FacetConfiguration();

		facetConfiguration.setDataJSONObject(dataJSONObject);

		return facetConfiguration;
	}

	private HttpServletRequest _getHttpServletRequest(Group group) {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.doReturn(
			getThemeDisplay(group)
		).when(
			httpServletRequest
		).getAttribute(
			WebKeys.THEME_DISPLAY
		);

		return httpServletRequest;
	}

	private void _mockFacetConfiguration(String... labelsAndRanges) {
		Mockito.doReturn(
			_getFacetConfiguration(
				JSONUtil.put("ranges", _createRangesJSONArray(labelsAndRanges)))
		).when(
			facet
		).getFacetConfiguration();
	}

	private TermCollector _mockTermCollector() {
		TermCollector termCollector = Mockito.mock(TermCollector.class);

		Mockito.doReturn(
			termCollector
		).when(
			facetCollector
		).getTermCollector(
			Mockito.anyString()
		);

		return termCollector;
	}

	private TermCollector _mockTermCollector(String term) {
		TermCollector termCollector = Mockito.mock(TermCollector.class);

		Mockito.doReturn(
			termCollector
		).when(
			facetCollector
		).getTermCollector(
			term
		);

		return termCollector;
	}

	private void _mockTermCollectorFrequency(
		TermCollector termCollector, int frequency) {

		Mockito.doReturn(
			frequency
		).when(
			termCollector
		).getFrequency();
	}

	private void _setUpCustomFacetPortletInstanceConfiguration(
		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder) {

		CustomFacetPortletInstanceConfiguration
			customFacetPortletInstanceConfiguration = Mockito.mock(
				CustomFacetPortletInstanceConfiguration.class);

		Mockito.doReturn(
			0L
		).when(
			customFacetPortletInstanceConfiguration
		).displayStyleGroupId();

		ReflectionTestUtil.setFieldValue(
			customFacetDisplayContextBuilder,
			"_customFacetPortletInstanceConfiguration",
			customFacetPortletInstanceConfiguration);
	}

	private void _testOrderByDateRange(
			int[] expectedFrequencies, String[] expectedTerms,
			int[] frequencies, String order, String[] terms)
		throws Exception {

		setUpTermCollectors(
			facetCollector, getTermCollectors(terms, frequencies));

		CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
			_createCustomFacetDisplayContextBuilder(
				_AGGREGATION_TYPE_DATE_RANGE, order);

		_mockFacetConfiguration(
			"past-hour=[20180515225959 TO 20180515235959]",
			"past-week=[20180508235959 TO 20180508235959]",
			"past-month=[20180508235959 TO 20180415235959]",
			"past-24-hours=[20180508235959 TO 20180514235959]");

		CustomFacetDisplayContext customFacetDisplayContext =
			customFacetDisplayContextBuilder.fromParameterValue(
				"2018-01-01"
			).toParameterValue(
				"2018-01-31"
			).build();

		assertFacetOrder(
			customFacetDisplayContext.getBucketDisplayContexts(), expectedTerms,
			expectedFrequencies);
	}

	private static final String _AGGREGATION_TYPE_DATE_RANGE = "dateRange";

	private static final String _AGGREGATION_TYPE_RANGE = "range";

	private static final String _AGGREGATION_TYPE_TERMS = "terms";

}