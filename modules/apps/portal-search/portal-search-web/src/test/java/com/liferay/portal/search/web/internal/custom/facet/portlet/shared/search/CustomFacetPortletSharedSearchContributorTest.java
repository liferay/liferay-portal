/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.custom.facet.portlet.shared.search;

import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.language.LanguageImpl;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.bucket.DateRangeAggregation;
import com.liferay.portal.search.aggregation.bucket.RangeAggregation;
import com.liferay.portal.search.facet.custom.CustomFacetSearchContributor;
import com.liferay.portal.search.facet.date.range.DateRangeFacetSearchContributor;
import com.liferay.portal.search.facet.nested.NestedFacet;
import com.liferay.portal.search.facet.nested.NestedFacetSearchContributor;
import com.liferay.portal.search.facet.range.RangeFacetSearchContributor;
import com.liferay.portal.search.internal.aggregation.AggregationsImpl;
import com.liferay.portal.search.internal.facet.DateRangeFacetImpl;
import com.liferay.portal.search.internal.facet.FacetImpl;
import com.liferay.portal.search.internal.facet.RangeFacetImpl;
import com.liferay.portal.search.internal.facet.custom.CustomFacetFactoryImpl;
import com.liferay.portal.search.internal.facet.custom.CustomFacetSearchContributorImpl;
import com.liferay.portal.search.internal.facet.date.range.DateRangeFacetFactoryImpl;
import com.liferay.portal.search.internal.facet.date.range.DateRangeFacetSearchContributorImpl;
import com.liferay.portal.search.internal.facet.nested.NestedFacetFactoryImpl;
import com.liferay.portal.search.internal.facet.nested.NestedFacetSearchContributorImpl;
import com.liferay.portal.search.internal.facet.range.RangeFacetFactoryImpl;
import com.liferay.portal.search.internal.facet.range.RangeFacetSearchContributorImpl;
import com.liferay.portal.search.internal.filter.FilterBuildersImpl;
import com.liferay.portal.search.internal.legacy.searcher.SearchRequestBuilderImpl;
import com.liferay.portal.search.internal.query.QueriesImpl;
import com.liferay.portal.search.internal.searcher.SearchRequestBuilderFactoryImpl;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Petteri Karttunen
 */
public class CustomFacetPortletSharedSearchContributorTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testContributeDateRangeAggregation() throws Exception {
		String aggregationField = RandomTestUtil.randomString();

		String range1 = _createRange();
		String range2 = _createRange();

		String[] parameterValues = {range1, range2};

		JSONArray rangesJSONArray = _createRangesJSONArray(range1, range2);

		PortletSharedSearchSettings portletSharedSearchSettings =
			_createPortletSharedSearchSettings(
				aggregationField, _AGGREGATION_TYPE_DATE_RANGE, parameterValues,
				rangesJSONArray);

		_contribute(portletSharedSearchSettings);

		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(null);

		DateRangeFacetImpl dateRangeFacetImpl =
			(DateRangeFacetImpl)searchRequestBuilder.withFacetContextGet(
				facetContext -> facetContext.getFacet(
					portletSharedSearchSettings.getPortletId()));

		Assert.assertEquals(
			_PORTLET_ID, dateRangeFacetImpl.getAggregationName());
		Assert.assertEquals(
			aggregationField, dateRangeFacetImpl.getFieldName());
		Assert.assertArrayEquals(
			parameterValues, dateRangeFacetImpl.getSelections());

		FacetConfiguration facetConfiguration =
			dateRangeFacetImpl.getFacetConfiguration();

		JSONObject jsonObject = facetConfiguration.getData();

		Assert.assertEquals(
			rangesJSONArray.toString(),
			String.valueOf(jsonObject.getJSONArray("ranges")));
	}

	@Test
	public void testContributeDateRangeAggregationWithDDMFieldArraySyntax()
		throws Exception {

		String aggregationField = RandomTestUtil.randomString();
		String filterValue = RandomTestUtil.randomString();
		String range1 = _createRange();
		String range2 = _createRange();

		_assertNestedAggregation(
			_AGGREGATION_TYPE_DATE_RANGE,
			StringBundler.concat(
				DDMIndexer.DDM_FIELD_ARRAY, StringPool.PERIOD, filterValue,
				StringPool.PERIOD, aggregationField),
			StringBundler.concat(
				DDMIndexer.DDM_FIELD_ARRAY, StringPool.PERIOD,
				aggregationField),
			StringBundler.concat(
				DDMIndexer.DDM_FIELD_ARRAY, StringPool.PERIOD,
				DDMIndexer.DDM_FIELD_NAME),
			filterValue, DDMIndexer.DDM_FIELD_ARRAY,
			new String[] {range1, range2},
			_createRangesJSONArray(range1, range2));
	}

	@Test
	public void testContributeDateRangeAggregationWithNestedFieldArraySyntax()
		throws Exception {

		String aggregationField = RandomTestUtil.randomString();
		String filterValue = RandomTestUtil.randomString();

		String range1 = _createRange();
		String range2 = _createRange();

		_assertNestedAggregation(
			_AGGREGATION_TYPE_DATE_RANGE,
			StringBundler.concat(
				_NESTED_FIELD_ARRAY, StringPool.PERIOD, filterValue,
				StringPool.PERIOD, aggregationField),
			StringBundler.concat(
				_NESTED_FIELD_ARRAY, StringPool.PERIOD, aggregationField),
			_NESTED_FIELD_ARRAY + ".fieldName", filterValue,
			_NESTED_FIELD_ARRAY, new String[] {range1, range2},
			_createRangesJSONArray(range1, range2));
	}

	@Test
	public void testContributeRangeAggregation() throws Exception {
		String aggregationField = RandomTestUtil.randomString();

		String range1 = _createRange();
		String range2 = _createRange();

		String[] parameterValues = {range1, range2};

		JSONArray rangesJSONArray = _createRangesJSONArray(range1, range2);

		PortletSharedSearchSettings portletSharedSearchSettings =
			_createPortletSharedSearchSettings(
				aggregationField, _AGGREGATION_TYPE_RANGE, parameterValues,
				rangesJSONArray);

		_contribute(portletSharedSearchSettings);

		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(null);

		RangeFacetImpl rangeFacetImpl =
			(RangeFacetImpl)searchRequestBuilder.withFacetContextGet(
				facetContext -> facetContext.getFacet(
					portletSharedSearchSettings.getPortletId()));

		Assert.assertEquals(_PORTLET_ID, rangeFacetImpl.getAggregationName());
		Assert.assertEquals(aggregationField, rangeFacetImpl.getFieldName());
		Assert.assertArrayEquals(
			parameterValues, rangeFacetImpl.getSelections());

		FacetConfiguration facetConfiguration =
			rangeFacetImpl.getFacetConfiguration();

		JSONObject jsonObject = facetConfiguration.getData();

		Assert.assertEquals(
			rangesJSONArray.toString(),
			String.valueOf(jsonObject.getJSONArray("ranges")));
	}

	@Test
	public void testContributeRangeAggregationWithDDMFieldArraySyntax()
		throws Exception {

		String aggregationField = RandomTestUtil.randomString();
		String filterValue = RandomTestUtil.randomString();
		String range1 = _createRange();
		String range2 = _createRange();

		_assertNestedAggregation(
			_AGGREGATION_TYPE_RANGE,
			StringBundler.concat(
				DDMIndexer.DDM_FIELD_ARRAY, StringPool.PERIOD, filterValue,
				StringPool.PERIOD, aggregationField),
			StringBundler.concat(
				DDMIndexer.DDM_FIELD_ARRAY, StringPool.PERIOD,
				aggregationField),
			StringBundler.concat(
				DDMIndexer.DDM_FIELD_ARRAY, StringPool.PERIOD,
				DDMIndexer.DDM_FIELD_NAME),
			filterValue, DDMIndexer.DDM_FIELD_ARRAY,
			new String[] {range1, range2},
			_createRangesJSONArray(range1, range2));
	}

	@Test
	public void testContributeRangeAggregationWithNestedArraySyntax()
		throws Exception {

		String aggregationField = RandomTestUtil.randomString();
		String filterValue = RandomTestUtil.randomString();
		String range1 = _createRange();
		String range2 = _createRange();

		_assertNestedAggregation(
			_AGGREGATION_TYPE_RANGE,
			StringBundler.concat(
				_NESTED_FIELD_ARRAY, StringPool.PERIOD, filterValue,
				StringPool.PERIOD, aggregationField),
			StringBundler.concat(
				_NESTED_FIELD_ARRAY, StringPool.PERIOD, aggregationField),
			_NESTED_FIELD_ARRAY + ".fieldName", filterValue,
			_NESTED_FIELD_ARRAY, new String[] {range1, range2},
			_createRangesJSONArray(range1, range2));
	}

	@Test
	public void testContributeTermsAggregation() throws Exception {
		String aggregationField = RandomTestUtil.randomString();

		String[] parameterValues = {
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		};

		PortletSharedSearchSettings portletSharedSearchSettings =
			_createPortletSharedSearchSettings(
				aggregationField, _AGGREGATION_TYPE_TERMS, parameterValues,
				null);

		_contribute(portletSharedSearchSettings);

		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(null);

		FacetImpl facetImpl =
			(FacetImpl)searchRequestBuilder.withFacetContextGet(
				facetContext -> facetContext.getFacet(
					portletSharedSearchSettings.getPortletId()));

		Assert.assertEquals(_PORTLET_ID, facetImpl.getAggregationName());
		Assert.assertEquals(aggregationField, facetImpl.getFieldName());
		Assert.assertArrayEquals(parameterValues, facetImpl.getSelections());
	}

	@Test
	public void testContributeTermsAggregationWithDDMFieldArraySyntax()
		throws Exception {

		String aggregationField = RandomTestUtil.randomString();
		String filterValue = RandomTestUtil.randomString();

		_assertNestedAggregation(
			_AGGREGATION_TYPE_TERMS,
			StringBundler.concat(
				DDMIndexer.DDM_FIELD_ARRAY, StringPool.PERIOD, filterValue,
				StringPool.PERIOD, aggregationField),
			StringBundler.concat(
				DDMIndexer.DDM_FIELD_ARRAY, StringPool.PERIOD,
				aggregationField),
			StringBundler.concat(
				DDMIndexer.DDM_FIELD_ARRAY, StringPool.PERIOD,
				DDMIndexer.DDM_FIELD_NAME),
			filterValue, DDMIndexer.DDM_FIELD_ARRAY, new String[0], null);
	}

	@Test
	public void testContributeTermsAggregationWithDDMFieldArraySyntaxWithKeywordLowercaseField()
		throws Exception {

		String aggregationField =
			RandomTestUtil.randomString() + ".keyword_lowercase";
		String filterValue = RandomTestUtil.randomString();

		_assertNestedAggregation(
			_AGGREGATION_TYPE_TERMS,
			StringBundler.concat(
				DDMIndexer.DDM_FIELD_ARRAY, StringPool.PERIOD, filterValue,
				StringPool.PERIOD, aggregationField),
			StringBundler.concat(
				DDMIndexer.DDM_FIELD_ARRAY, StringPool.PERIOD,
				aggregationField),
			StringBundler.concat(
				DDMIndexer.DDM_FIELD_ARRAY, StringPool.PERIOD,
				DDMIndexer.DDM_FIELD_NAME),
			filterValue, DDMIndexer.DDM_FIELD_ARRAY, new String[0], null);
	}

	@Test
	public void testContributeTermsAggregationWithDDMFieldSyntax()
		throws Exception {

		String aggregationField = "ddmFieldValueKeyword_en_US";
		String filterValue = StringBundler.concat(
			"ddm__keyword__", RandomTestUtil.randomLong(),
			StringPool.DOUBLE_UNDERLINE, RandomTestUtil.randomString(),
			"_en_US");

		_assertNestedAggregation(
			_AGGREGATION_TYPE_TERMS, filterValue,
			StringBundler.concat(
				DDMIndexer.DDM_FIELD_ARRAY, StringPool.PERIOD,
				aggregationField),
			StringBundler.concat(
				DDMIndexer.DDM_FIELD_ARRAY, StringPool.PERIOD,
				DDMIndexer.DDM_FIELD_NAME),
			filterValue, DDMIndexer.DDM_FIELD_ARRAY, new String[0], null);
	}

	@Test
	public void testContributeTermsAggregationWithNestedFieldArraySyntax()
		throws Exception {

		String aggregationField = RandomTestUtil.randomString();
		String filterValue = RandomTestUtil.randomString();

		_assertNestedAggregation(
			_AGGREGATION_TYPE_TERMS,
			StringBundler.concat(
				_NESTED_FIELD_ARRAY, StringPool.PERIOD, filterValue,
				StringPool.PERIOD, aggregationField),
			StringBundler.concat(
				_NESTED_FIELD_ARRAY, StringPool.PERIOD, aggregationField),
			_NESTED_FIELD_ARRAY + ".fieldName", filterValue,
			_NESTED_FIELD_ARRAY, new String[0], null);
	}

	private void _assertNestedAggregation(
		String aggregationType, String configurationField,
		String expectedAggregationField, String expectedFilterField,
		String expectedFilterValue, String expectedPath,
		String[] parameterValues, JSONArray rangesJSONArray) {

		PortletSharedSearchSettings portletSharedSearchSettings =
			_createPortletSharedSearchSettings(
				configurationField, aggregationType, parameterValues,
				rangesJSONArray);

		_contribute(false, portletSharedSearchSettings);

		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(null);

		NestedFacet nestedFacet =
			(NestedFacet)searchRequestBuilder.withFacetContextGet(
				facetContext -> facetContext.getFacet(
					portletSharedSearchSettings.getPortletId()));

		Assert.assertEquals(_PORTLET_ID, nestedFacet.getAggregationName());
		Assert.assertEquals(
			expectedAggregationField, nestedFacet.getFieldName());
		Assert.assertEquals(expectedFilterField, nestedFacet.getFilterField());
		Assert.assertEquals(expectedFilterValue, nestedFacet.getFilterValue());
		Assert.assertEquals(expectedPath, nestedFacet.getPath());
		Assert.assertArrayEquals(parameterValues, nestedFacet.getSelections());

		if (aggregationType.equals(_AGGREGATION_TYPE_DATE_RANGE)) {
			Aggregation childAggregation = nestedFacet.getChildAggregation();

			Assert.assertTrue(childAggregation instanceof DateRangeAggregation);
		}
		else if (aggregationType.equals(_AGGREGATION_TYPE_RANGE)) {
			Aggregation childAggregation = nestedFacet.getChildAggregation();

			Assert.assertTrue(childAggregation instanceof RangeAggregation);
		}
	}

	private void _contribute(
		boolean legacyDDMIndexFieldsEnabled,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		CustomFacetPortletSharedSearchContributor
			customFacetPortletSharedSearchContributor =
				_createCustomFacetPortletSharedSearchContributor(
					legacyDDMIndexFieldsEnabled);

		customFacetPortletSharedSearchContributor.contribute(
			portletSharedSearchSettings);
	}

	private void _contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		CustomFacetPortletSharedSearchContributor
			customFacetPortletSharedSearchContributor =
				_createCustomFacetPortletSharedSearchContributor(false);

		customFacetPortletSharedSearchContributor.contribute(
			portletSharedSearchSettings);
	}

	private CustomFacetPortletSharedSearchContributor
		_createCustomFacetPortletSharedSearchContributor(
			boolean legacyDDMIndexFieldsEnabled) {

		CustomFacetPortletSharedSearchContributor
			customFacetPortletSharedSearchContributor =
				new CustomFacetPortletSharedSearchContributor();

		_setUpDDMIndexer(
			customFacetPortletSharedSearchContributor,
			legacyDDMIndexFieldsEnabled);
		_setUpCustomFacetSearchContributor(
			customFacetPortletSharedSearchContributor);
		_setUpDateRangeFacetSearchContributor(
			customFacetPortletSharedSearchContributor);
		_setUpNestedFacetSearchContributor(
			customFacetPortletSharedSearchContributor);
		_setUpRangeFacetSearchContributor(
			customFacetPortletSharedSearchContributor);

		ReflectionTestUtil.setFieldValue(
			customFacetPortletSharedSearchContributor, "_aggregations",
			new AggregationsImpl());
		ReflectionTestUtil.setFieldValue(
			customFacetPortletSharedSearchContributor, "_filterBuilders",
			new FilterBuildersImpl());
		ReflectionTestUtil.setFieldValue(
			customFacetPortletSharedSearchContributor, "_language",
			new LanguageImpl());

		return customFacetPortletSharedSearchContributor;
	}

	private PortletPreferences _createPortletPreferences(
		String aggregationField, String aggregationType,
		JSONArray rangesJSONArray) {

		PortletPreferences portletPreferences = Mockito.mock(
			PortletPreferences.class);

		Mockito.doReturn(
			aggregationField
		).when(
			portletPreferences
		).getValue(
			"aggregationField", StringPool.BLANK
		);

		Mockito.doReturn(
			aggregationType
		).when(
			portletPreferences
		).getValue(
			"aggregationType", StringPool.BLANK
		);

		String ranges = null;

		if (rangesJSONArray != null) {
			ranges = rangesJSONArray.toString();
		}

		Mockito.doReturn(
			ranges
		).when(
			portletPreferences
		).getValue(
			"ranges", StringPool.BLANK
		);

		return portletPreferences;
	}

	private PortletSharedSearchSettings _createPortletSharedSearchSettings(
		String aggregationField, String aggregationType,
		String[] parameterValues, JSONArray rangesJSONArray) {

		PortletPreferences portletPreferences = _createPortletPreferences(
			aggregationField, aggregationType, rangesJSONArray);

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
			parameterValues
		).when(
			portletSharedSearchSettings
		).getParameterValues(
			Mockito.anyString()
		);

		Mockito.doReturn(
			_PORTLET_ID
		).when(
			portletSharedSearchSettings
		).getPortletId();

		Mockito.doReturn(
			portletPreferences
		).when(
			portletSharedSearchSettings
		).getPortletPreferences();

		return portletSharedSearchSettings;
	}

	private String _createRange() {
		return StringBundler.concat(
			StringPool.OPEN_BRACKET, RandomTestUtil.randomLong(), " TO ",
			RandomTestUtil.randomLong(), StringPool.CLOSE_BRACKET);
	}

	private JSONArray _createRangesJSONArray(String... ranges) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (String range : ranges) {
			jsonArray.put(
				JSONUtil.put(
					"label", range
				).put(
					"range", range
				));
		}

		return jsonArray;
	}

	private void _setUpCustomFacetSearchContributor(
		CustomFacetPortletSharedSearchContributor
			customFacetPortletSharedSearchContributor) {

		CustomFacetSearchContributor customFacetSearchContributor =
			new CustomFacetSearchContributorImpl();

		ReflectionTestUtil.setFieldValue(
			customFacetSearchContributor, "_customFacetFactory",
			new CustomFacetFactoryImpl());

		ReflectionTestUtil.setFieldValue(
			customFacetPortletSharedSearchContributor,
			"_customFacetSearchContributor", customFacetSearchContributor);
	}

	private void _setUpDateRangeFacetSearchContributor(
		CustomFacetPortletSharedSearchContributor
			customFacetPortletSharedSearchContributor) {

		DateRangeFacetSearchContributor dateRangeFacetSearchContributor =
			new DateRangeFacetSearchContributorImpl();

		ReflectionTestUtil.setFieldValue(
			dateRangeFacetSearchContributor, "_dateRangeFacetFactory",
			new DateRangeFacetFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			dateRangeFacetSearchContributor, "_jsonFactory",
			new JSONFactoryImpl());

		ReflectionTestUtil.setFieldValue(
			customFacetPortletSharedSearchContributor,
			"_dateRangeFacetSearchContributor",
			dateRangeFacetSearchContributor);
	}

	private void _setUpDDMIndexer(
		CustomFacetPortletSharedSearchContributor
			customFacetPortletSharedSearchContributor,
		boolean legacyDDMIndexFieldsEnabled) {

		DDMIndexer ddmIndexer = Mockito.mock(DDMIndexer.class);

		Mockito.doReturn(
			legacyDDMIndexFieldsEnabled
		).when(
			ddmIndexer
		).isLegacyDDMIndexFieldsEnabled();

		Mockito.doReturn(
			"ddmFieldValueKeyword_en_US"
		).when(
			ddmIndexer
		).getValueFieldName(
			Mockito.anyString(), Mockito.any()
		);

		ReflectionTestUtil.setFieldValue(
			customFacetPortletSharedSearchContributor, "_ddmIndexer",
			ddmIndexer);
	}

	private void _setUpNestedFacetSearchContributor(
		CustomFacetPortletSharedSearchContributor
			customFacetPortletSharedSearchContributor) {

		NestedFacetSearchContributor nestedFacetSearchContributor =
			new NestedFacetSearchContributorImpl();

		ReflectionTestUtil.setFieldValue(
			nestedFacetSearchContributor, "aggregations",
			new AggregationsImpl());
		ReflectionTestUtil.setFieldValue(
			nestedFacetSearchContributor, "nestedFacetFactory",
			new NestedFacetFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			nestedFacetSearchContributor, "queries", new QueriesImpl());

		ReflectionTestUtil.setFieldValue(
			customFacetPortletSharedSearchContributor,
			"_nestedFacetSearchContributor", nestedFacetSearchContributor);
	}

	private void _setUpRangeFacetSearchContributor(
		CustomFacetPortletSharedSearchContributor
			customFacetPortletSharedSearchContributor) {

		RangeFacetSearchContributor rangeFacetSearchContributor =
			new RangeFacetSearchContributorImpl();

		ReflectionTestUtil.setFieldValue(
			rangeFacetSearchContributor, "_jsonFactory", new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			rangeFacetSearchContributor, "_rangeFacetFactory",
			new RangeFacetFactoryImpl());

		ReflectionTestUtil.setFieldValue(
			customFacetPortletSharedSearchContributor,
			"_rangeFacetSearchContributor", rangeFacetSearchContributor);
	}

	private static final String _AGGREGATION_TYPE_DATE_RANGE = "dateRange";

	private static final String _AGGREGATION_TYPE_RANGE = "range";

	private static final String _AGGREGATION_TYPE_TERMS = "terms";

	private static final String _NESTED_FIELD_ARRAY = "nestedFieldArray";

	private static final String _PORTLET_ID = RandomTestUtil.randomString();

}