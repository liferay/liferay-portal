/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.internal.facet;

import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.facet.util.RangeParserUtil;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.DateRangeAggregation;
import com.liferay.portal.search.aggregation.bucket.Range;
import com.liferay.portal.search.facet.category.CategoryFacetSearchContributor;
import com.liferay.portal.search.facet.custom.CustomFacetSearchContributor;
import com.liferay.portal.search.facet.date.range.DateRangeFacetSearchContributor;
import com.liferay.portal.search.facet.folder.FolderFacetSearchContributor;
import com.liferay.portal.search.facet.nested.NestedFacetSearchContributor;
import com.liferay.portal.search.facet.site.SiteFacetSearchContributor;
import com.liferay.portal.search.facet.tag.TagFacetSearchContributor;
import com.liferay.portal.search.facet.type.TypeFacetSearchContributor;
import com.liferay.portal.search.facet.user.UserFacetSearchContributor;
import com.liferay.portal.search.filter.DateRangeFilterBuilder;
import com.liferay.portal.search.filter.FilterBuilders;
import com.liferay.portal.search.rest.dto.v1_0.FacetConfiguration;
import com.liferay.portal.search.searcher.SearchRequestBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(service = FacetRequestContributor.class)
public class FacetRequestContributor {

	public void contribute(
		FacetConfiguration[] facetConfigurations,
		SearchRequestBuilder searchRequestBuilder) {

		for (FacetConfiguration facetConfiguration : facetConfigurations) {
			_setProperties(facetConfiguration);

			if (StringUtil.equals("category", facetConfiguration.getName()) ||
				StringUtil.equals("vocabulary", facetConfiguration.getName())) {

				_contributeCategoryFacet(
					facetConfiguration, searchRequestBuilder);
			}
			else if (StringUtil.equals(
						"custom", facetConfiguration.getName())) {

				_contributeCustomFacet(
					facetConfiguration, searchRequestBuilder);
			}
			else if (StringUtil.equals(
						"date-range", facetConfiguration.getName())) {

				_contributeDateRangeFacet(
					facetConfiguration, searchRequestBuilder);
			}
			else if (StringUtil.equals(
						"folder", facetConfiguration.getName())) {

				_contributeFolderFacet(
					facetConfiguration, searchRequestBuilder);
			}
			else if (StringUtil.equals(
						"nested", facetConfiguration.getName())) {

				_contributeNestedFacet(
					facetConfiguration, searchRequestBuilder);
			}
			else if (StringUtil.equals("site", facetConfiguration.getName())) {
				_contributeSiteFacet(facetConfiguration, searchRequestBuilder);
			}
			else if (StringUtil.equals("tag", facetConfiguration.getName())) {
				_contributeTagFacet(facetConfiguration, searchRequestBuilder);
			}
			else if (StringUtil.equals("type", facetConfiguration.getName())) {
				_contributeTypeFacet(facetConfiguration, searchRequestBuilder);
			}
			else if (StringUtil.equals("user", facetConfiguration.getName())) {
				_contributeUserFacet(facetConfiguration, searchRequestBuilder);
			}
		}
	}

	private void _contributeCategoryFacet(
		FacetConfiguration facetConfiguration,
		SearchRequestBuilder searchRequestBuilder) {

		_categoryFacetSearchContributor.contribute(
			searchRequestBuilder,
			categoryFacetBuilder -> categoryFacetBuilder.aggregationName(
				facetConfiguration.getAggregationName()
			).frequencyThreshold(
				facetConfiguration.getFrequencyThreshold()
			).maxTerms(
				facetConfiguration.getMaxTerms()
			).selectedCategoryIds(
				_toLongArray(facetConfiguration.getValues())
			).vocabularyIds(
				_getVocabularyIdsAttribute(facetConfiguration)
			));
	}

	private void _contributeCustomFacet(
		FacetConfiguration facetConfiguration,
		SearchRequestBuilder searchRequestBuilder) {

		if (!_hasAttributes(facetConfiguration, "field")) {
			return;
		}

		_customFacetSearchContributor.contribute(
			searchRequestBuilder,
			customFacetBuilder -> customFacetBuilder.aggregationName(
				facetConfiguration.getAggregationName()
			).fieldToAggregate(
				GetterUtil.getString(_getAttribute(facetConfiguration, "field"))
			).frequencyThreshold(
				facetConfiguration.getFrequencyThreshold()
			).maxTerms(
				facetConfiguration.getMaxTerms()
			).selectedValues(
				_toStringArray(facetConfiguration.getValues())
			));
	}

	private void _contributeDateRangeFacet(
		FacetConfiguration facetConfiguration,
		SearchRequestBuilder searchRequestBuilder) {

		if (!_hasAttributes(facetConfiguration, "field", "format", "ranges")) {
			return;
		}

		String field = GetterUtil.getString(
			_getAttribute(facetConfiguration, "field"));

		if (!_ddmIndexer.isLegacyDDMIndexFieldsEnabled() &&
			field.startsWith(DDMIndexer.DDM_FIELD_ARRAY)) {

			_contributeDateRangeFacetWithDDMFieldArray(
				facetConfiguration, field, searchRequestBuilder);
		}
		else if (!_ddmIndexer.isLegacyDDMIndexFieldsEnabled() &&
				 field.startsWith(DDMIndexer.DDM_FIELD_PREFIX)) {

			_contributeDateRangeFacetWithDDMField(
				facetConfiguration, field, searchRequestBuilder);
		}
		else if (field.startsWith("nestedFieldArray")) {
			_contributeDateRangeFacetWithNestedFieldArray(
				facetConfiguration, field, searchRequestBuilder);
		}
		else {
			_contributeDateRangeFacet(
				facetConfiguration, field, searchRequestBuilder);
		}
	}

	private void _contributeDateRangeFacet(
		FacetConfiguration facetConfiguration, String field,
		SearchRequestBuilder searchRequestBuilder) {

		_dateRangeFacetSearchContributor.contribute(
			searchRequestBuilder,
			dateRangeFacetBuilder -> dateRangeFacetBuilder.aggregationName(
				facetConfiguration.getAggregationName()
			).field(
				field
			).format(
				GetterUtil.getString(
					_getAttribute(facetConfiguration, "format"))
			).frequencyThreshold(
				facetConfiguration.getFrequencyThreshold()
			).rangesJSONArray(
				_jsonFactory.createJSONArray(
					(List<Map<String, Object>>)_getAttribute(
						facetConfiguration, "ranges"))
			).selectedRanges(
				_toStringArray(facetConfiguration.getValues())
			));
	}

	private void _contributeDateRangeFacetWithDDMField(
		FacetConfiguration facetConfiguration, String field,
		SearchRequestBuilder searchRequestBuilder) {

		String[] ddmFieldParts = StringUtil.split(
			field, DDMIndexer.DDM_FIELD_SEPARATOR);

		if ((ddmFieldParts.length != 4) &&
			!ddmFieldParts[3].startsWith("Date")) {

			return;
		}

		_contributeDateRangeFacetWithNestedField(
			facetConfiguration,
			_getDDMDateValueFieldName(ddmFieldParts[1], ddmFieldParts[3]),
			DDMIndexer.DDM_FIELD_NAME, field, DDMIndexer.DDM_FIELD_ARRAY,
			searchRequestBuilder);
	}

	private void _contributeDateRangeFacetWithDDMFieldArray(
		FacetConfiguration facetConfiguration, String field,
		SearchRequestBuilder searchRequestBuilder) {

		String[] fieldParts = StringUtil.split(field, StringPool.PERIOD);

		if (fieldParts.length != 3) {
			return;
		}

		_contributeDateRangeFacetWithNestedField(
			facetConfiguration, fieldParts[2], DDMIndexer.DDM_FIELD_NAME,
			fieldParts[1], DDMIndexer.DDM_FIELD_ARRAY, searchRequestBuilder);
	}

	private void _contributeDateRangeFacetWithNestedField(
		FacetConfiguration facetConfiguration, String fieldToAggregate,
		String filterField, String filterValue, String path,
		SearchRequestBuilder searchRequestBuilder) {

		String fieldToAggregateWithPath = StringBundler.concat(
			path, StringPool.PERIOD, fieldToAggregate);

		JSONArray rangesJSONArray = _jsonFactory.createJSONArray(
			(List<Map<String, Object>>)_getAttribute(
				facetConfiguration, "ranges"));

		String[] selectedValues = _toStringArray(
			facetConfiguration.getValues());

		_nestedFacetSearchContributor.contribute(
			searchRequestBuilder,
			nestedFacetBuilder -> nestedFacetBuilder.aggregationName(
				facetConfiguration.getAggregationName()
			).additionalFacetConfigurationData(
				JSONUtil.put("ranges", rangesJSONArray)
			).childAggregation(
				_getDateRangeChildAggregation(
					facetConfiguration, fieldToAggregateWithPath,
					rangesJSONArray)
			).childAggregationValuesFilter(
				_getDateRangeChildAggregationFilter(
					facetConfiguration, fieldToAggregateWithPath,
					Arrays.asList(selectedValues))
			).fieldToAggregate(
				fieldToAggregateWithPath
			).filterField(
				StringBundler.concat(path, StringPool.PERIOD, filterField)
			).filterValue(
				filterValue
			).frequencyThreshold(
				facetConfiguration.getFrequencyThreshold()
			).path(
				path
			).selectedValues(
				_toStringArray(facetConfiguration.getValues())
			));
	}

	private void _contributeDateRangeFacetWithNestedFieldArray(
		FacetConfiguration facetConfiguration, String field,
		SearchRequestBuilder searchRequestBuilder) {

		String[] fieldParts = StringUtil.split(field, StringPool.PERIOD);

		if (fieldParts.length != 3) {
			return;
		}

		_contributeDateRangeFacetWithNestedField(
			facetConfiguration, fieldParts[2], "fieldName", fieldParts[1],
			"nestedFieldArray", searchRequestBuilder);
	}

	private void _contributeFolderFacet(
		FacetConfiguration facetConfiguration,
		SearchRequestBuilder searchRequestBuilder) {

		_folderFacetSearchContributor.contribute(
			searchRequestBuilder,
			folderFacetBuilder -> folderFacetBuilder.aggregationName(
				facetConfiguration.getAggregationName()
			).frequencyThreshold(
				facetConfiguration.getFrequencyThreshold()
			).maxTerms(
				facetConfiguration.getMaxTerms()
			).selectedFolderIds(
				_toLongArray(facetConfiguration.getValues())
			));
	}

	private void _contributeNestedFacet(
		FacetConfiguration facetConfiguration,
		SearchRequestBuilder searchRequestBuilder) {

		if (!_hasAttributes(
				facetConfiguration, "field", "filterField", "filterValue",
				"path")) {

			return;
		}

		_nestedFacetSearchContributor.contribute(
			searchRequestBuilder,
			nestedFacetBuilder -> nestedFacetBuilder.aggregationName(
				facetConfiguration.getAggregationName()
			).fieldToAggregate(
				GetterUtil.getString(_getAttribute(facetConfiguration, "field"))
			).filterField(
				GetterUtil.getString(
					_getAttribute(facetConfiguration, "filterField"))
			).filterValue(
				GetterUtil.getString(
					_getAttribute(facetConfiguration, "filterValue"))
			).frequencyThreshold(
				facetConfiguration.getFrequencyThreshold()
			).maxTerms(
				facetConfiguration.getMaxTerms()
			).path(
				GetterUtil.getString(_getAttribute(facetConfiguration, "path"))
			).selectedValues(
				_toStringArray(facetConfiguration.getValues())
			));
	}

	private void _contributeSiteFacet(
		FacetConfiguration facetConfiguration,
		SearchRequestBuilder searchRequestBuilder) {

		_siteFacetSearchContributor.contribute(
			searchRequestBuilder,
			siteFacetBuilder -> siteFacetBuilder.aggregationName(
				facetConfiguration.getAggregationName()
			).frequencyThreshold(
				facetConfiguration.getFrequencyThreshold()
			).maxTerms(
				facetConfiguration.getMaxTerms()
			).selectedGroupIds(
				_toStringArray(facetConfiguration.getValues())
			));
	}

	private void _contributeTagFacet(
		FacetConfiguration facetConfiguration,
		SearchRequestBuilder searchRequestBuilder) {

		_tagFacetSearchContributor.contribute(
			searchRequestBuilder,
			tagFacetBuilder -> tagFacetBuilder.aggregationName(
				facetConfiguration.getAggregationName()
			).frequencyThreshold(
				facetConfiguration.getFrequencyThreshold()
			).maxTerms(
				facetConfiguration.getMaxTerms()
			).selectedTagNames(
				_toStringArray(facetConfiguration.getValues())
			));
	}

	private void _contributeTypeFacet(
		FacetConfiguration facetConfiguration,
		SearchRequestBuilder searchRequestBuilder) {

		_typeFacetSearchContributor.contribute(
			searchRequestBuilder,
			typeFacetBuilder -> typeFacetBuilder.aggregationName(
				facetConfiguration.getAggregationName()
			).frequencyThreshold(
				facetConfiguration.getFrequencyThreshold()
			).selectedEntryClassNames(
				_toStringArray(facetConfiguration.getValues())
			));
	}

	private void _contributeUserFacet(
		FacetConfiguration facetConfiguration,
		SearchRequestBuilder searchRequestBuilder) {

		_userFacetSearchContributor.contribute(
			searchRequestBuilder,
			userFacetBuilder -> userFacetBuilder.aggregationName(
				facetConfiguration.getAggregationName()
			).frequencyThreshold(
				facetConfiguration.getFrequencyThreshold()
			).maxTerms(
				facetConfiguration.getMaxTerms()
			).selectedUserIds(
				_toLongArray(facetConfiguration.getValues())
			));
	}

	private Object _getAttribute(
		FacetConfiguration facetConfiguration, String key) {

		Map<String, Object> attributes = facetConfiguration.getAttributes();

		return attributes.get(key);
	}

	private String _getDDMDateValueFieldName(String indexType, String suffix) {
		String valueFieldName = _ddmIndexer.getValueFieldName(
			indexType, _getLocaleFromSuffix(suffix));

		return valueFieldName + "_date";
	}

	private Aggregation _getDateRangeChildAggregation(
		FacetConfiguration facetConfiguration, String fieldToAggregate,
		JSONArray rangesJSONArray) {

		DateRangeAggregation dateRangeAggregation = _aggregations.dateRange(
			facetConfiguration.getAggregationName(), fieldToAggregate);

		dateRangeAggregation.setFormat(
			GetterUtil.getString(
				_getAttribute(facetConfiguration, "format"), null));

		for (int i = 0; i < rangesJSONArray.length(); i++) {
			JSONObject rangeJSONObject = rangesJSONArray.getJSONObject(i);

			String range = rangeJSONObject.getString("range");

			String[] rangeParts = RangeParserUtil.parserRange(range);

			dateRangeAggregation.addRange(
				new Range(range, rangeParts[0], rangeParts[1]));
		}

		return dateRangeAggregation;
	}

	private Filter _getDateRangeChildAggregationFilter(
		FacetConfiguration facetConfiguration, String fieldName,
		List<String> selectedRangeStrings) {

		if (selectedRangeStrings.isEmpty()) {
			return null;
		}

		BooleanFilter booleanFilter = new BooleanFilter();

		for (String selection : selectedRangeStrings) {
			String[] rangeParts = RangeParserUtil.parserRange(selection);

			String from = rangeParts[0];
			String to = rangeParts[1];

			if (Validator.isNull(from) && Validator.isNull(to)) {
				continue;
			}

			DateRangeFilterBuilder dateRangeFilterBuilder =
				_filterBuilders.dateRangeFilterBuilder();

			dateRangeFilterBuilder.setFieldName(fieldName);
			dateRangeFilterBuilder.setFormat(
				GetterUtil.getString(
					_getAttribute(facetConfiguration, "format"), null));
			dateRangeFilterBuilder.setFrom(from);
			dateRangeFilterBuilder.setIncludeLower(true);
			dateRangeFilterBuilder.setIncludeUpper(true);
			dateRangeFilterBuilder.setTo(to);

			booleanFilter.add(
				dateRangeFilterBuilder.build(), BooleanClauseOccur.SHOULD);
		}

		return booleanFilter;
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

	private String[] _getVocabularyIdsAttribute(
		FacetConfiguration facetConfiguration) {

		if (!_hasAttributes(facetConfiguration, "vocabularyIds")) {
			return new String[0];
		}

		Map<String, Object> attributes = facetConfiguration.getAttributes();

		List<String> vocabularyIds = (List)attributes.get("vocabularyIds");

		return vocabularyIds.toArray(new String[0]);
	}

	private boolean _hasAttributes(
		FacetConfiguration facetConfiguration, String... keys) {

		Map<String, Object> attributes = facetConfiguration.getAttributes();

		if (MapUtil.isEmpty(attributes)) {
			return false;
		}

		for (String key : keys) {
			if (!attributes.containsKey(key)) {
				return false;
			}
		}

		return true;
	}

	private void _setProperties(FacetConfiguration facetConfiguration) {
		if (Validator.isBlank(facetConfiguration.getAggregationName())) {
			facetConfiguration.setAggregationName(facetConfiguration::getName);
		}

		Integer frequencyThreshold = facetConfiguration.getFrequencyThreshold();

		facetConfiguration.setFrequencyThreshold(
			() -> _toInt(1, frequencyThreshold, 0));

		Integer maxTerms = facetConfiguration.getMaxTerms();

		facetConfiguration.setMaxTerms(() -> _toInt(10, maxTerms, 0));
	}

	private int _toInt(int defaultValue, Integer value, int minValue) {
		if ((value == null) || (value < minValue)) {
			return defaultValue;
		}

		return value;
	}

	private long[] _toLongArray(Object[] values) {
		if (ArrayUtil.isNotEmpty(values)) {
			return ListUtil.toLongArray(
				Arrays.asList(values), GetterUtil::getLong);
		}

		return new long[0];
	}

	private String[] _toStringArray(Object[] values) {
		if (ArrayUtil.isNotEmpty(values)) {
			return ArrayUtil.toStringArray(values);
		}

		return new String[0];
	}

	@Reference
	private Aggregations _aggregations;

	@Reference
	private CategoryFacetSearchContributor _categoryFacetSearchContributor;

	@Reference
	private CustomFacetSearchContributor _customFacetSearchContributor;

	@Reference
	private DateRangeFacetSearchContributor _dateRangeFacetSearchContributor;

	@Reference
	private DDMIndexer _ddmIndexer;

	@Reference
	private FilterBuilders _filterBuilders;

	@Reference
	private FolderFacetSearchContributor _folderFacetSearchContributor;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private NestedFacetSearchContributor _nestedFacetSearchContributor;

	@Reference
	private SiteFacetSearchContributor _siteFacetSearchContributor;

	@Reference
	private TagFacetSearchContributor _tagFacetSearchContributor;

	@Reference
	private TypeFacetSearchContributor _typeFacetSearchContributor;

	@Reference
	private UserFacetSearchContributor _userFacetSearchContributor;

}