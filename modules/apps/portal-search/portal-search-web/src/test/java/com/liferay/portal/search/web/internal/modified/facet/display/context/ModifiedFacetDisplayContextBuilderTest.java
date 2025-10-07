/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.modified.facet.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.internal.BaseFacetDisplayContextTestCase;
import com.liferay.portal.search.web.internal.facet.display.context.BucketDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.FacetDisplayContext;
import com.liferay.portal.search.web.internal.modified.facet.configuration.ModifiedFacetPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.modified.facet.display.context.builder.ModifiedFacetDisplayContextBuilder;
import com.liferay.portal.search.web.internal.util.DateRangeFactoryUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adam Brandizzi
 */
public class ModifiedFacetDisplayContextBuilderTest
	extends BaseFacetDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		_defaultLocale = LocaleThreadLocal.getDefaultLocale();
		_jsonFactoryImpl = new JSONFactoryImpl();

		_setUpPortalUtil();

		Mockito.doReturn(
			_facetCollector
		).when(
			_facet
		).getFacetCollector();

		Mockito.doReturn(
			getFacetConfiguration()
		).when(
			_facet
		).getFacetConfiguration();

		Mockito.doReturn(
			new SearchContext()
		).when(
			_facet
		).getSearchContext();
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setDefaultLocale(_defaultLocale);
	}

	@Test
	public void testBucketDisplayContexts() {
		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			createDisplayContextBuilder();

		_mockFacetConfiguration(
			"past-hour=[20180515225959 TO 20180515235959]",
			"some-time-ago=[20180508235959 TO 20180514235959]");

		ModifiedFacetDisplayContext modifiedFacetDisplayContext =
			modifiedFacetDisplayContextBuilder.build();

		List<BucketDisplayContext> bucketDisplayContexts =
			modifiedFacetDisplayContext.getBucketDisplayContexts();

		Assert.assertEquals(
			bucketDisplayContexts.toString(), 2, bucketDisplayContexts.size());

		BucketDisplayContext bucketDisplayContext = bucketDisplayContexts.get(
			0);

		Assert.assertEquals("past-hour", bucketDisplayContext.getBucketText());

		bucketDisplayContext = bucketDisplayContexts.get(1);

		Assert.assertEquals(
			"some-time-ago", bucketDisplayContext.getBucketText());
	}

	@Test
	public void testCustomRangeHasFrequency() {
		String from = "2018-01-01";
		String to = "2018-01-31";

		TermCollector termCollector = _mockTermCollector(
			DateRangeFactoryUtil.getRangeString(from, to, TimeZoneUtil.GMT));

		int frequency = RandomTestUtil.randomInt();

		_mockTermCollectorFrequency(termCollector, frequency);

		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			createDisplayContextBuilder();

		modifiedFacetDisplayContextBuilder.setFromParameterValue(from);
		modifiedFacetDisplayContextBuilder.setToParameterValue(to);

		ModifiedFacetDisplayContext modifiedFacetDisplayContext =
			modifiedFacetDisplayContextBuilder.build();

		BucketDisplayContext bucketDisplayContext =
			modifiedFacetDisplayContext.getCustomRangeBucketDisplayContext();

		Assert.assertEquals(frequency, bucketDisplayContext.getFrequency());
	}

	@Test
	public void testCustomRangeHasTermCollectorFrequency() {
		int frequency = RandomTestUtil.randomInt();
		TermCollector termCollector = _mockTermCollector();

		_mockTermCollectorFrequency(termCollector, frequency);

		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			createDisplayContextBuilder();

		modifiedFacetDisplayContextBuilder.setFromParameterValue("2018-01-01");
		modifiedFacetDisplayContextBuilder.setToParameterValue("2018-01-31");

		ModifiedFacetDisplayContext modifiedFacetDisplayContext =
			modifiedFacetDisplayContextBuilder.build();

		BucketDisplayContext bucketDisplayContext =
			modifiedFacetDisplayContext.getCustomRangeBucketDisplayContext();

		Assert.assertEquals(frequency, bucketDisplayContext.getFrequency());
	}

	@Test
	public void testCustomRangeUsesStandardDateFormat() {
		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			createDisplayContextBuilder();

		modifiedFacetDisplayContextBuilder.setCurrentURL(
			"/?modifiedFrom=2018-01-01&modifiedTo=2018-01-31");

		for (Locale locale : List.of(LocaleUtil.US, new Locale("ar", "SA"))) {
			LocaleThreadLocal.setDefaultLocale(locale);

			ModifiedFacetDisplayContext modifiedFacetDisplayContext =
				modifiedFacetDisplayContextBuilder.build();

			BucketDisplayContext bucketDisplayContext =
				modifiedFacetDisplayContext.
					getCustomRangeBucketDisplayContext();

			String modifiedFrom = HttpComponentsUtil.getParameter(
				bucketDisplayContext.getFilterValue(), "modifiedFrom", false);

			Assert.assertNotEquals("2018-01-01", modifiedFrom);

			String modifiedTo = HttpComponentsUtil.getParameter(
				bucketDisplayContext.getFilterValue(), "modifiedTo", false);

			Assert.assertNotEquals("2018-01-31", modifiedTo);

			Matcher matcher = _pattern.matcher(modifiedFrom);

			Assert.assertTrue(modifiedFrom, matcher.matches());

			matcher = _pattern.matcher(modifiedTo);

			Assert.assertTrue(modifiedTo, matcher.matches());
		}
	}

	@Override
	@Test
	public void testEmptySearchResults() throws Exception {
	}

	@Override
	@Test
	public void testEmptySearchResultsWithPreviousSelection() throws Exception {
	}

	@Override
	@Test
	public void testGetDisplayStyleGroup() throws Exception {
		setUpGroupLocalServiceUtil(getGroup());
		setUpPortletDisplayStyleGroupExternalReferenceCode(null);

		_assertDisplayContext(getGroup());

		groupLocalServiceUtilMockedStatic.verifyNoInteractions();
	}

	@Override
	@Test
	public void testGetDisplayStyleGroupWithConfiguration() throws Exception {
		Group group = getGroup();

		setUpGroupLocalServiceUtil(group);
		setUpPortletDisplayStyleGroupExternalReferenceCode(
			group.getExternalReferenceCode());

		_assertDisplayContext(group);

		groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				group.getExternalReferenceCode(), 0L),
			Mockito.times(1));
	}

	@Test
	public void testIsNothingSelected() {
		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			createDisplayContextBuilder();

		ModifiedFacetDisplayContext modifiedFacetDisplayContext =
			modifiedFacetDisplayContextBuilder.build();

		Assert.assertTrue(modifiedFacetDisplayContext.isNothingSelected());
	}

	@Test
	public void testIsNothingSelectedWithFromAndToAttributes() {
		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			createDisplayContextBuilder();

		modifiedFacetDisplayContextBuilder.setFromParameterValue("2018-01-01");
		modifiedFacetDisplayContextBuilder.setToParameterValue("2018-01-31");

		ModifiedFacetDisplayContext modifiedFacetDisplayContext =
			modifiedFacetDisplayContextBuilder.build();

		Assert.assertFalse(modifiedFacetDisplayContext.isNothingSelected());
	}

	@Test
	public void testIsNothingSelectedWithSelectedRange() {
		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			createDisplayContextBuilder();

		modifiedFacetDisplayContextBuilder.setParameterValues("past-24-hours");

		ModifiedFacetDisplayContext modifiedFacetDisplayContext =
			modifiedFacetDisplayContextBuilder.build();

		Assert.assertFalse(modifiedFacetDisplayContext.isNothingSelected());
	}

	@Test
	public void testIsRenderNothingFalseWithFromAndTo() {
		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			createDisplayContextBuilder();

		modifiedFacetDisplayContextBuilder.setFromParameterValue("2018-01-01");
		modifiedFacetDisplayContextBuilder.setToParameterValue("2018-01-31");
		modifiedFacetDisplayContextBuilder.setTotalHits(0);

		ModifiedFacetDisplayContext modifiedFacetDisplayContext =
			modifiedFacetDisplayContextBuilder.build();

		Assert.assertFalse(modifiedFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testIsRenderNothingFalseWithHits() {
		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			createDisplayContextBuilder();

		modifiedFacetDisplayContextBuilder.setTotalHits(1);

		ModifiedFacetDisplayContext modifiedFacetDisplayContext =
			modifiedFacetDisplayContextBuilder.build();

		Assert.assertFalse(modifiedFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testIsRenderNothingFalseWithSelectedRange() {
		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			createDisplayContextBuilder();

		modifiedFacetDisplayContextBuilder.setParameterValues("past-24-hours");
		modifiedFacetDisplayContextBuilder.setTotalHits(0);

		ModifiedFacetDisplayContext modifiedFacetDisplayContext =
			modifiedFacetDisplayContextBuilder.build();

		Assert.assertFalse(modifiedFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testIsRenderNothingTrueWithNoHits() {
		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			createDisplayContextBuilder();

		modifiedFacetDisplayContextBuilder.setTotalHits(0);

		ModifiedFacetDisplayContext modifiedFacetDisplayContext =
			modifiedFacetDisplayContextBuilder.build();

		Assert.assertTrue(modifiedFacetDisplayContext.isRenderNothing());
	}

	@Test
	public void testMissingFromAndToParameters() {
		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			createDisplayContextBuilder();

		modifiedFacetDisplayContextBuilder.setCurrentURL(
			"/?modifiedFrom=2018-01-01&modifiedTo=2018-01-31");

		ModifiedFacetDisplayContext modifiedFacetDisplayContext =
			modifiedFacetDisplayContextBuilder.build();

		for (BucketDisplayContext bucketDisplayContext :
				modifiedFacetDisplayContext.getBucketDisplayContexts()) {

			String label = bucketDisplayContext.getBucketText();

			if (label.equals("custom-range")) {
				continue;
			}

			String rangeURL = bucketDisplayContext.getFilterValue();

			Assert.assertTrue(
				Validator.isNotNull(
					HttpComponentsUtil.getParameter(
						rangeURL, "modified", false)));
			Assert.assertTrue(
				Validator.isNull(
					HttpComponentsUtil.getParameter(
						rangeURL, "modifiedFrom", false)));
			Assert.assertTrue(
				Validator.isNull(
					HttpComponentsUtil.getParameter(
						rangeURL, "modifiedTo", false)));
		}
	}

	@Override
	@Test
	public void testOneTerm() throws Exception {
	}

	@Override
	@Test
	public void testOneTermWithPreviousSelection() throws Exception {
	}

	@Override
	@Test
	public void testOrderByTermFrequencyAscending() throws Exception {
		testOrderBy(
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

	@Override
	@Test
	public void testOrderByTermFrequencyDescending() throws Exception {
		testOrderBy(
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
	@Test
	public void testOrderByTermValueAscending() throws Exception {
	}

	@Override
	@Test
	public void testOrderByTermValueDescending() throws Exception {
	}

	protected ModifiedFacetDisplayContextBuilder createDisplayContextBuilder() {
		return createDisplayContextBuilder("rangesConfiguration");
	}

	protected ModifiedFacetDisplayContextBuilder createDisplayContextBuilder(
		String order) {

		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			_createModifiedFacetDisplayContextBuilder();

		_mockFacetConfiguration();

		modifiedFacetDisplayContextBuilder.setFacet(_facet);
		modifiedFacetDisplayContextBuilder.setLocale(LocaleUtil.getDefault());
		modifiedFacetDisplayContextBuilder.setOrder(order);
		modifiedFacetDisplayContextBuilder.setTimeZone(
			TimeZoneUtil.getDefault());

		return modifiedFacetDisplayContextBuilder;
	}

	protected FacetConfiguration getFacetConfiguration() {
		JSONObject jsonObject = _jsonFactoryImpl.createJSONObject();

		return getFacetConfiguration(jsonObject);
	}

	protected FacetConfiguration getFacetConfiguration(
		JSONObject dataJSONObject) {

		FacetConfiguration facetConfiguration = new FacetConfiguration();

		facetConfiguration.setDataJSONObject(dataJSONObject);

		return facetConfiguration;
	}

	@Override
	protected FacetDisplayContext getFacetDisplayContext(Group group)
		throws Exception {

		return null;
	}

	@Override
	protected void setUpPortletDisplayStyleGroupExternalReferenceCode(
		String externalReferenceCode) {

		ModifiedFacetPortletInstanceConfiguration
			modifiedFacetPortletInstanceConfiguration = Mockito.mock(
				ModifiedFacetPortletInstanceConfiguration.class);

		Mockito.when(
			modifiedFacetPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode()
		).thenReturn(
			externalReferenceCode
		);

		configurationProviderUtilMockedStatic.when(
			() -> ConfigurationProviderUtil.getPortletInstanceConfiguration(
				Mockito.any(), Mockito.any())
		).thenReturn(
			modifiedFacetPortletInstanceConfiguration
		);
	}

	@Override
	protected void testOrderBy(
			int[] expectedFrequencies, String[] expectedTerms,
			int[] frequencies, String order, String[] terms)
		throws Exception {

		setUpTermCollectors(
			_facetCollector, getTermCollectors(terms, frequencies));

		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			createDisplayContextBuilder(order);

		_mockFacetConfiguration(
			"past-hour=[20180515225959 TO 20180515235959]",
			"past-week=[20180508235959 TO 20180508235959]",
			"past-month=[20180508235959 TO 20180415235959]",
			"past-24-hours=[20180508235959 TO 20180514235959]");

		modifiedFacetDisplayContextBuilder.setFromParameterValue("2018-01-01");
		modifiedFacetDisplayContextBuilder.setToParameterValue("2018-01-31");

		ModifiedFacetDisplayContext modifiedFacetDisplayContext =
			modifiedFacetDisplayContextBuilder.build();

		assertFacetOrder(
			modifiedFacetDisplayContext.getBucketDisplayContexts(),
			expectedTerms, expectedFrequencies);
	}

	protected Portal portal = Mockito.mock(Portal.class);

	private void _addRangeJSONObject(
		JSONArray jsonArray, String label, String range) {

		JSONObject jsonObject = _jsonFactoryImpl.createJSONObject();

		jsonObject.put(
			"label", label
		).put(
			"range", range
		);

		jsonArray.put(jsonObject);
	}

	private void _assertDisplayContext(Group group) throws Exception {
		ModifiedFacetDisplayContextBuilder modifiedFacetDisplayContextBuilder =
			new ModifiedFacetDisplayContextBuilder(getRenderRequest(group));

		modifiedFacetDisplayContextBuilder.setFacet(_facet);
		modifiedFacetDisplayContextBuilder.setFromParameterValue("2018-01-01");
		modifiedFacetDisplayContextBuilder.setTimeZone(
			TimeZoneUtil.getDefault());
		modifiedFacetDisplayContextBuilder.setToParameterValue("2018-01-31");

		ModifiedFacetDisplayContext modifiedFacetDisplayContext =
			modifiedFacetDisplayContextBuilder.build();

		Assert.assertEquals(
			group.getGroupId(),
			modifiedFacetDisplayContext.getDisplayStyleGroupId());
	}

	private JSONObject _createDataJSONObject(String... labelsAndRanges) {
		JSONObject dataJSONObject = _jsonFactoryImpl.createJSONObject();

		dataJSONObject.put("ranges", _createRangesJSONArray(labelsAndRanges));

		return dataJSONObject;
	}

	private ModifiedFacetDisplayContextBuilder
		_createModifiedFacetDisplayContextBuilder() {

		configurationProviderUtilMockedStatic.when(
			() -> ConfigurationProviderUtil.getPortletInstanceConfiguration(
				Mockito.any(), Mockito.any())
		).thenReturn(
			Mockito.mock(ModifiedFacetPortletInstanceConfiguration.class)
		);

		try {
			return new ModifiedFacetDisplayContextBuilder(getRenderRequest());
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	private JSONArray _createRangesJSONArray(String... labelsAndRanges) {
		JSONArray jsonArray = _jsonFactoryImpl.createJSONArray();

		for (String labelAndRange : labelsAndRanges) {
			String[] labelAndRangeArray = StringUtil.split(labelAndRange, '=');

			_addRangeJSONObject(
				jsonArray, labelAndRangeArray[0], labelAndRangeArray[1]);
		}

		return jsonArray;
	}

	private void _mockFacetConfiguration(String... labelsAndRanges) {
		Mockito.doReturn(
			getFacetConfiguration(_createDataJSONObject(labelsAndRanges))
		).when(
			_facet
		).getFacetConfiguration();
	}

	private TermCollector _mockTermCollector() {
		TermCollector termCollector = Mockito.mock(TermCollector.class);

		Mockito.doReturn(
			termCollector
		).when(
			_facetCollector
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
			_facetCollector
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

	private void _setUpPortalUtil() {
		Mockito.doAnswer(
			invocation -> new String[] {
				invocation.getArgument(0, String.class), StringPool.BLANK
			}
		).when(
			portal
		).stripURLAnchor(
			Mockito.anyString(), Mockito.anyString()
		);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(portal);
	}

	private static final Pattern _pattern = Pattern.compile(
		"\\d{4}-\\d{2}-\\d{2}");

	private Locale _defaultLocale;
	private final Facet _facet = Mockito.mock(Facet.class);
	private final FacetCollector _facetCollector = Mockito.mock(
		FacetCollector.class);
	private JSONFactoryImpl _jsonFactoryImpl;

}