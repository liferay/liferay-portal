/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.custom.facet.portlet.shared.search;

import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.facet.util.RangeParserUtil;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.DateRangeAggregation;
import com.liferay.portal.search.aggregation.bucket.Range;
import com.liferay.portal.search.aggregation.bucket.RangeAggregation;
import com.liferay.portal.search.facet.custom.CustomFacetSearchContributor;
import com.liferay.portal.search.facet.date.range.DateRangeFacetSearchContributor;
import com.liferay.portal.search.facet.nested.NestedFacetSearchContributor;
import com.liferay.portal.search.facet.range.RangeFacetSearchContributor;
import com.liferay.portal.search.filter.DateRangeFilterBuilder;
import com.liferay.portal.search.filter.FilterBuilders;
import com.liferay.portal.search.filter.RangeFilterBuilder;
import com.liferay.portal.search.web.internal.custom.facet.constants.CustomFacetPortletKeys;
import com.liferay.portal.search.web.internal.custom.facet.portlet.CustomFacetPortletPreferences;
import com.liferay.portal.search.web.internal.custom.facet.portlet.CustomFacetPortletPreferencesImpl;
import com.liferay.portal.search.web.internal.custom.facet.util.CustomFacetUtil;
import com.liferay.portal.search.web.internal.range.BaseRangeFacetPortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 * @author Petteri Karttunen
 */
@Component(
	property = "jakarta.portlet.name=" + CustomFacetPortletKeys.CUSTOM_FACET,
	service = PortletSharedSearchContributor.class
)
public class CustomFacetPortletSharedSearchContributor
	extends BaseRangeFacetPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		CustomFacetPortletPreferences customFacetPortletPreferences =
			new CustomFacetPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		String aggregationField =
			customFacetPortletPreferences.getAggregationField();

		String aggregationType =
			customFacetPortletPreferences.getAggregationType();

		if (Validator.isNull(aggregationField) ||
			Validator.isNull(aggregationType)) {

			return;
		}

		if (CustomFacetUtil.isRangeAggregation(aggregationType)) {
			_contributeRangeAggregation(
				aggregationField, aggregationType,
				customFacetPortletPreferences, portletSharedSearchSettings);
		}
		else if (aggregationType.equals("terms")) {
			_contribute(
				aggregationField, aggregationType,
				customFacetPortletPreferences, portletSharedSearchSettings,
				null, null);
		}
	}

	private void _addCustomRange(
		String aggregationType,
		CustomFacetPortletPreferences customFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings,
		JSONArray rangesJSONArray, List<String> selectedRanges) {

		String selectedCustomRangeString = getSelectedCustomRangeString(
			aggregationType,
			CustomFacetUtil.getParameterName(customFacetPortletPreferences),
			portletSharedSearchSettings);

		if (!Validator.isBlank(selectedCustomRangeString)) {
			addCustomRange(
				rangesJSONArray, selectedCustomRangeString, selectedRanges);
		}
	}

	private void _contribute(
		String aggregationField, String aggregationType,
		CustomFacetPortletPreferences customFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings,
		JSONArray rangesJSONArray, List<String> selectedRanges) {

		if (!_ddmIndexer.isLegacyDDMIndexFieldsEnabled() &&
			aggregationField.startsWith(DDMIndexer.DDM_FIELD_ARRAY)) {

			_contributeWithDDMFieldArray(
				aggregationField, aggregationType,
				customFacetPortletPreferences, portletSharedSearchSettings,
				rangesJSONArray, selectedRanges);
		}
		else if (!_ddmIndexer.isLegacyDDMIndexFieldsEnabled() &&
				 aggregationField.startsWith(DDMIndexer.DDM_FIELD_PREFIX)) {

			_contributeWithDDMField(
				aggregationField, aggregationType,
				customFacetPortletPreferences, portletSharedSearchSettings,
				rangesJSONArray, selectedRanges);
		}
		else if (aggregationField.startsWith("nestedFieldArray")) {
			_contributeWithNestedFieldArray(
				aggregationField, aggregationType,
				customFacetPortletPreferences, portletSharedSearchSettings,
				rangesJSONArray, selectedRanges);
		}
		else if (aggregationType.equals("dateRange")) {
			_contributeWithDateRangeFacet(
				customFacetPortletPreferences, aggregationField,
				portletSharedSearchSettings, rangesJSONArray,
				selectedRanges.toArray(new String[0]));
		}
		else if (aggregationType.equals("range")) {
			_contributeWithRangeFacet(
				customFacetPortletPreferences, aggregationField,
				portletSharedSearchSettings, rangesJSONArray,
				selectedRanges.toArray(new String[0]));
		}
		else {
			_contributeWithCustomFacet(
				customFacetPortletPreferences, aggregationField,
				portletSharedSearchSettings);
		}
	}

	private void _contributeRangeAggregation(
		String aggregationField, String aggregationType,
		CustomFacetPortletPreferences customFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		JSONArray rangesJSONArray = _getRangesJSONArray(
			aggregationType, customFacetPortletPreferences);

		List<String> selectedRanges = _getSelectedRanges(
			aggregationType, customFacetPortletPreferences,
			portletSharedSearchSettings, rangesJSONArray);

		_addCustomRange(
			aggregationType, customFacetPortletPreferences,
			portletSharedSearchSettings, rangesJSONArray, selectedRanges);

		if (JSONUtil.isEmpty(rangesJSONArray)) {
			return;
		}

		_contribute(
			aggregationField, aggregationType, customFacetPortletPreferences,
			portletSharedSearchSettings, rangesJSONArray, selectedRanges);
	}

	private void _contributeWithCustomFacet(
		CustomFacetPortletPreferences customFacetPortletPreferences,
		String fieldToAggregate,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		_customFacetSearchContributor.contribute(
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(
				customFacetPortletPreferences.getFederatedSearchKey()),
			customFacetBuilder -> customFacetBuilder.aggregationName(
				portletSharedSearchSettings.getPortletId()
			).fieldToAggregate(
				fieldToAggregate
			).frequencyThreshold(
				customFacetPortletPreferences.getFrequencyThreshold()
			).maxTerms(
				customFacetPortletPreferences.getMaxTerms()
			).selectedValues(
				portletSharedSearchSettings.getParameterValues(
					CustomFacetUtil.getParameterName(
						customFacetPortletPreferences))
			));
	}

	private void _contributeWithDateRangeFacet(
		CustomFacetPortletPreferences customFacetPortletPreferences,
		String fieldToAggregate,
		PortletSharedSearchSettings portletSharedSearchSettings,
		JSONArray rangesJSONArray, String[] selectedRanges) {

		_dateRangeFacetSearchContributor.contribute(
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(
				customFacetPortletPreferences.getFederatedSearchKey()),
			dateRangeFacetBuilder -> dateRangeFacetBuilder.aggregationName(
				portletSharedSearchSettings.getPortletId()
			).field(
				fieldToAggregate
			).format(
				"yyyyMMddHHmmss"
			).frequencyThreshold(
				customFacetPortletPreferences.getFrequencyThreshold()
			).order(
				customFacetPortletPreferences.getOrder()
			).rangesJSONArray(
				rangesJSONArray
			).selectedRanges(
				selectedRanges
			));
	}

	private void _contributeWithDDMField(
		String aggregationField, String aggregationType,
		CustomFacetPortletPreferences customFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings,
		JSONArray rangesJSONArray, List<String> selectedRanges) {

		String[] ddmFieldParts = StringUtil.split(
			customFacetPortletPreferences.getAggregationField(),
			DDMIndexer.DDM_FIELD_SEPARATOR);

		if (ddmFieldParts.length != 4) {
			return;
		}

		String fieldToAggregate = _withNestedPath(
			_ddmIndexer.getValueFieldName(
				ddmFieldParts[1], _getLocaleFromSuffix(ddmFieldParts[3])),
			DDMIndexer.DDM_FIELD_ARRAY);

		_contributeWithNestedFacet(
			_getAdditionalFacetConfigurationDataJSONObject(
				aggregationType, rangesJSONArray),
			_getChildAggregation(
				fieldToAggregate, aggregationType, portletSharedSearchSettings,
				rangesJSONArray),
			_getChildAggregationValuesFilter(
				fieldToAggregate, aggregationType, selectedRanges),
			customFacetPortletPreferences, fieldToAggregate,
			_withNestedPath(
				DDMIndexer.DDM_FIELD_NAME, DDMIndexer.DDM_FIELD_ARRAY),
			aggregationField, DDMIndexer.DDM_FIELD_ARRAY,
			portletSharedSearchSettings,
			_getSelectedValues(
				aggregationType, customFacetPortletPreferences,
				portletSharedSearchSettings, selectedRanges));
	}

	private void _contributeWithDDMFieldArray(
		String aggregationField, String aggregationType,
		CustomFacetPortletPreferences customFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings,
		JSONArray rangesJSONArray, List<String> selectedRanges) {

		String[] ddmFieldArrayParts = StringUtil.split(
			aggregationField, StringPool.PERIOD);

		String fieldToAggregate = _getDDMFieldArrayFieldToAggregate(
			ddmFieldArrayParts);

		if (fieldToAggregate == null) {
			return;
		}

		_contributeWithNestedFacet(
			_getAdditionalFacetConfigurationDataJSONObject(
				aggregationType, rangesJSONArray),
			_getChildAggregation(
				fieldToAggregate, aggregationType, portletSharedSearchSettings,
				rangesJSONArray),
			_getChildAggregationValuesFilter(
				fieldToAggregate, aggregationType, selectedRanges),
			customFacetPortletPreferences, fieldToAggregate,
			_withNestedPath(
				DDMIndexer.DDM_FIELD_NAME, DDMIndexer.DDM_FIELD_ARRAY),
			ddmFieldArrayParts[1], DDMIndexer.DDM_FIELD_ARRAY,
			portletSharedSearchSettings,
			_getSelectedValues(
				aggregationType, customFacetPortletPreferences,
				portletSharedSearchSettings, selectedRanges));
	}

	private void _contributeWithNestedFacet(
		JSONObject additionalFacetConfigurationDataJSONObject,
		Aggregation childAggregation, Filter childAggregationValuesFilter,
		CustomFacetPortletPreferences customFacetPortletPreferences,
		String fieldToAggregate, String filterField, String filterValue,
		String path, PortletSharedSearchSettings portletSharedSearchSettings,
		String[] selectedValues) {

		_nestedFacetSearchContributor.contribute(
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(
				customFacetPortletPreferences.getFederatedSearchKey()),
			nestedFacetBuilder -> nestedFacetBuilder.aggregationName(
				portletSharedSearchSettings.getPortletId()
			).additionalFacetConfigurationData(
				additionalFacetConfigurationDataJSONObject
			).childAggregation(
				childAggregation
			).childAggregationValuesFilter(
				childAggregationValuesFilter
			).fieldToAggregate(
				fieldToAggregate
			).filterField(
				filterField
			).filterValue(
				filterValue
			).frequencyThreshold(
				customFacetPortletPreferences.getFrequencyThreshold()
			).path(
				path
			).selectedValues(
				selectedValues
			));
	}

	private void _contributeWithNestedFieldArray(
		String aggregationField, String aggregationType,
		CustomFacetPortletPreferences customFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings,
		JSONArray rangesJSONArray, List<String> selectedRanges) {

		String[] nestedFieldArrayParts = StringUtil.split(
			aggregationField, StringPool.PERIOD);

		if (nestedFieldArrayParts.length != 3) {
			return;
		}

		String fieldToAggregate = _withNestedPath(
			nestedFieldArrayParts[2], "nestedFieldArray");

		_contributeWithNestedFacet(
			_getAdditionalFacetConfigurationDataJSONObject(
				aggregationType, rangesJSONArray),
			_getChildAggregation(
				fieldToAggregate, aggregationType, portletSharedSearchSettings,
				rangesJSONArray),
			_getChildAggregationValuesFilter(
				fieldToAggregate, aggregationType, selectedRanges),
			customFacetPortletPreferences, fieldToAggregate,
			_withNestedPath("fieldName", "nestedFieldArray"),
			nestedFieldArrayParts[1], "nestedFieldArray",
			portletSharedSearchSettings,
			_getSelectedValues(
				aggregationType, customFacetPortletPreferences,
				portletSharedSearchSettings, selectedRanges));
	}

	private void _contributeWithRangeFacet(
		CustomFacetPortletPreferences customFacetPortletPreferences,
		String fieldToAggregate,
		PortletSharedSearchSettings portletSharedSearchSettings,
		JSONArray rangesJSONArray, String[] selectedRanges) {

		_rangeFacetSearchContributor.contribute(
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(
				customFacetPortletPreferences.getFederatedSearchKey()),
			rangeFacetBuilder -> rangeFacetBuilder.aggregationName(
				portletSharedSearchSettings.getPortletId()
			).field(
				fieldToAggregate
			).frequencyThreshold(
				customFacetPortletPreferences.getFrequencyThreshold()
			).order(
				customFacetPortletPreferences.getOrder()
			).rangesJSONArray(
				rangesJSONArray
			).selectedRanges(
				selectedRanges
			));
	}

	private JSONObject _getAdditionalFacetConfigurationDataJSONObject(
		String aggregationType, JSONArray rangesJSONArray) {

		if (CustomFacetUtil.isRangeAggregation(aggregationType) &&
			(rangesJSONArray != null)) {

			return JSONUtil.put("ranges", rangesJSONArray);
		}

		return null;
	}

	private Aggregation _getChildAggregation(
		String aggregationField, String aggregationType,
		PortletSharedSearchSettings portletSharedSearchSettings,
		JSONArray rangesJSONArray) {

		if (aggregationType.equals("dateRange")) {
			DateRangeAggregation dateRangeAggregation = _aggregations.dateRange(
				portletSharedSearchSettings.getPortletId(), aggregationField);

			dateRangeAggregation.setFormat("yyyyMMddHHmmss");

			return _withRanges(dateRangeAggregation, rangesJSONArray);
		}
		else if (aggregationType.equals("range")) {
			return _withRanges(
				_aggregations.range(
					portletSharedSearchSettings.getPortletId(),
					aggregationField),
				rangesJSONArray);
		}

		return null;
	}

	private Filter _getChildAggregationValuesFilter(
		String aggregationField, String aggregationType,
		List<String> selectedRanges) {

		if (!CustomFacetUtil.isRangeAggregation(aggregationType) ||
			ListUtil.isEmpty(selectedRanges)) {

			return null;
		}

		if (aggregationType.equals("dateRange")) {
			return _getDateRangeChildAggregationFilter(
				aggregationField, selectedRanges);
		}
		else if (aggregationType.equals("range")) {
			return _getRangeChildAggregationFilter(
				aggregationField, selectedRanges);
		}

		return null;
	}

	private Filter _getDateRangeChildAggregationFilter(
		String aggregationField, List<String> selectedRanges) {

		BooleanFilter booleanFilter = new BooleanFilter();

		for (String selectedRange : selectedRanges) {
			String[] rangeParts = RangeParserUtil.parserRange(selectedRange);

			String from = rangeParts[0];
			String to = rangeParts[1];

			if (Validator.isNull(from) && Validator.isNull(to)) {
				continue;
			}

			DateRangeFilterBuilder dateRangeFilterBuilder =
				_filterBuilders.dateRangeFilterBuilder();

			dateRangeFilterBuilder.setFieldName(aggregationField);
			dateRangeFilterBuilder.setFrom(from);
			dateRangeFilterBuilder.setIncludeLower(true);
			dateRangeFilterBuilder.setIncludeUpper(true);
			dateRangeFilterBuilder.setTo(to);

			booleanFilter.add(
				dateRangeFilterBuilder.build(), BooleanClauseOccur.SHOULD);
		}

		return booleanFilter;
	}

	private String _getDDMFieldArrayFieldToAggregate(
		String[] ddmFieldArrayParts) {

		if ((ddmFieldArrayParts.length == 4) &&
			ddmFieldArrayParts[3].equals("keyword_lowercase")) {

			return _withNestedPath(
				StringBundler.concat(
					ddmFieldArrayParts[2], StringPool.PERIOD,
					ddmFieldArrayParts[3]),
				DDMIndexer.DDM_FIELD_ARRAY);
		}
		else if (ddmFieldArrayParts.length == 3) {
			return _withNestedPath(
				ddmFieldArrayParts[2], DDMIndexer.DDM_FIELD_ARRAY);
		}

		return null;
	}

	private Locale _getLocaleFromSuffix(String string) {
		for (Locale availableLocale : _language.getAvailableLocales()) {
			String availableLanguageId = _language.getLanguageId(
				availableLocale);

			if (string.endsWith(availableLanguageId)) {
				return availableLocale;
			}
		}

		return null;
	}

	private Filter _getRangeChildAggregationFilter(
		String fieldName, List<String> selectedRanges) {

		BooleanFilter booleanFilter = new BooleanFilter();

		for (String selectedRange : selectedRanges) {
			String[] rangeParts = RangeParserUtil.parserRange(selectedRange);

			String from = rangeParts[0];
			String to = rangeParts[1];

			if (Validator.isNull(from) && Validator.isNull(to)) {
				continue;
			}

			RangeFilterBuilder rangeFilterBuilder =
				_filterBuilders.rangeFilterBuilder();

			rangeFilterBuilder.setFieldName(fieldName);
			rangeFilterBuilder.setFrom(from);
			rangeFilterBuilder.setIncludeLower(true);
			rangeFilterBuilder.setIncludeUpper(true);
			rangeFilterBuilder.setTo(to);

			booleanFilter.add(
				rangeFilterBuilder.build(), BooleanClauseOccur.SHOULD);
		}

		return booleanFilter;
	}

	private JSONArray _getRangesJSONArray(
		String aggregationType,
		CustomFacetPortletPreferences customFacetPortletPreferences) {

		if (aggregationType.equals("dateRange")) {
			return getDateRangesJSONArray(
				CalendarFactoryUtil.getCalendar(),
				customFacetPortletPreferences.getRangesJSONArray());
		}

		return customFacetPortletPreferences.getRangesJSONArray();
	}

	private List<String> _getSelectedRanges(
		String aggregationType,
		CustomFacetPortletPreferences customFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings,
		JSONArray rangesJSONArray) {

		return getSelectedRangeStrings(
			aggregationType,
			CustomFacetUtil.getParameterName(customFacetPortletPreferences),
			portletSharedSearchSettings, rangesJSONArray);
	}

	private String[] _getSelectedValues(
		String aggregationType,
		CustomFacetPortletPreferences customFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings,
		List<String> selectedRanges) {

		if (CustomFacetUtil.isRangeAggregation(aggregationType)) {
			return selectedRanges.toArray(new String[0]);
		}

		return portletSharedSearchSettings.getParameterValues(
			CustomFacetUtil.getParameterName(customFacetPortletPreferences));
	}

	private String _withNestedPath(String field, String path) {
		return StringBundler.concat(path, StringPool.PERIOD, field);
	}

	private RangeAggregation _withRanges(
		RangeAggregation rangeAggregation, JSONArray rangesJSONArray) {

		for (int i = 0; i < rangesJSONArray.length(); i++) {
			JSONObject rangeJSONObject = rangesJSONArray.getJSONObject(i);

			String range = rangeJSONObject.getString("range");

			String[] rangeParts = RangeParserUtil.parserRange(range);

			rangeAggregation.addRange(
				new Range(range, rangeParts[0], rangeParts[1]));
		}

		return rangeAggregation;
	}

	@Reference
	private Aggregations _aggregations;

	@Reference
	private CustomFacetSearchContributor _customFacetSearchContributor;

	@Reference
	private DateRangeFacetSearchContributor _dateRangeFacetSearchContributor;

	@Reference
	private DDMIndexer _ddmIndexer;

	@Reference
	private FilterBuilders _filterBuilders;

	@Reference
	private Language _language;

	@Reference
	private NestedFacetSearchContributor _nestedFacetSearchContributor;

	@Reference
	private RangeFacetSearchContributor _rangeFacetSearchContributor;

}