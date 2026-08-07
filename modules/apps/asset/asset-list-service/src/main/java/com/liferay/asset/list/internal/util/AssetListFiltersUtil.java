/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.util;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectField;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.MatchAllQuery;
import com.liferay.portal.kernel.search.MatchQuery;
import com.liferay.portal.kernel.search.NestedQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.text.Format;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Joshua Cords
 */
public class AssetListFiltersUtil {

	public static BooleanClause[] getFiltersBooleanClauses(
		long companyId, JSONArray filtersJSONArray, Locale locale) {

		if (JSONUtil.isEmpty(filtersJSONArray)) {
			return new BooleanClause[0];
		}

		BooleanQuery booleanQuery = new BooleanQuery();

		boolean hasMustClause = false;

		for (int i = 0; i < filtersJSONArray.length(); i++) {
			JSONObject jsonObject = filtersJSONArray.getJSONObject(i);

			Query query = _toQuery(companyId, jsonObject, locale);

			if (query == null) {
				continue;
			}

			if (_isNegatedOperator(
					jsonObject.getString("operatorName", "contains"))) {

				booleanQuery.add(query, BooleanClauseOccur.MUST_NOT);
			}
			else {
				booleanQuery.add(query, BooleanClauseOccur.MUST);

				hasMustClause = true;
			}
		}

		if (!booleanQuery.hasClauses()) {
			return new BooleanClause[0];
		}

		if (!hasMustClause) {
			booleanQuery.add(new MatchAllQuery(), BooleanClauseOccur.MUST);
		}

		return new BooleanClause[] {
			new BooleanClause<>(booleanQuery, BooleanClauseOccur.MUST)
		};
	}

	private static String _getCommonFieldName(
		Locale locale, String propertyName) {

		if (!_commonFieldTypesMap.containsKey(propertyName)) {
			return null;
		}

		if (_localizedCommonFieldNames.contains(propertyName)) {
			return Field.getLocalizedName(locale, "localized_" + propertyName);
		}

		return propertyName;
	}

	private static String _getCommonFieldType(String propertyName) {
		return _commonFieldTypesMap.get(propertyName);
	}

	private static boolean _isCommonFieldRow(JSONObject jsonObject) {
		if ((jsonObject.getLong("classNameId") <= 0) &&
			(jsonObject.getLong("classTypeId") <= 0)) {

			return true;
		}

		return false;
	}

	private static boolean _isDateTimeField(ObjectField objectField) {
		return ObjectFieldConstants.DB_TYPE_DATE_TIME.equals(
			objectField.getDBType());
	}

	private static boolean _isNegatedOperator(String operatorName) {
		if (operatorName.equals("not-contains") ||
			operatorName.equals("not-eq")) {

			return true;
		}

		return false;
	}

	private static String _resolveRelativeDateValue(String value) {
		if (!_relativeDateValues.contains(value)) {
			return null;
		}

		Format format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddHHmmss");

		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		if (value.equals("last-year") || value.equals("past-year")) {
			calendar.add(Calendar.YEAR, -1);
		}
		else if (value.equals("next-month")) {
			calendar.add(Calendar.MONTH, 1);
		}
		else if (value.equals("past-24-hours") || value.equals("past-day")) {
			calendar.add(Calendar.DAY_OF_MONTH, -1);
		}
		else if (value.equals("past-month")) {
			calendar.add(Calendar.MONTH, -1);
		}
		else if (value.equals("past-week")) {
			calendar.add(Calendar.DAY_OF_MONTH, -7);
		}

		return format.format(calendar.getTime());
	}

	private static Query _toCommonFieldQuery(
		JSONObject jsonObject, Locale locale, String propertyName) {

		if (Validator.isNull(propertyName)) {
			return null;
		}

		String commonFieldName = _getCommonFieldName(locale, propertyName);
		String commonFieldType = _getCommonFieldType(propertyName);

		if ((commonFieldName == null) || (commonFieldType == null)) {
			return null;
		}

		String operatorName = GetterUtil.getString(
			jsonObject.getString("operatorName"), "contains");

		return _toCommonFieldValueQuery(
			commonFieldName, jsonObject,
			_localizedCommonFieldNames.contains(propertyName), operatorName,
			commonFieldType);
	}

	private static Query _toCommonFieldValueQuery(
		String field, JSONObject jsonObject, boolean localized,
		String operatorName, String type) {

		if (operatorName.equals("between") || operatorName.equals("ge") ||
			operatorName.equals("gt") || operatorName.equals("le") ||
			operatorName.equals("lt")) {

			return _toTermRangeQuery(
				type.equals(_TYPE_DATE), false, field, jsonObject,
				operatorName);
		}

		String value = jsonObject.getString("value");

		if (Validator.isNull(value)) {
			return null;
		}

		if (type.equals(_TYPE_DATE) &&
			(operatorName.equals("eq") || operatorName.equals("not-eq"))) {

			return new TermRangeQuery(
				field, _toDateTerm(false, false, value),
				_toDateTerm(false, true, value), true, true);
		}

		if (type.equals(_TYPE_DECIMAL) || type.equals(_TYPE_INTEGER)) {
			return new TermQuery(field, value);
		}

		if (localized) {
			return new MatchQuery(field, value);
		}

		if (Objects.equals(field, Field.USER_NAME)) {
			value = StringUtil.toLowerCase(value);
		}

		if (operatorName.equals("contains") ||
			operatorName.equals("not-contains")) {

			return new WildcardQuery(
				field, StringPool.STAR + value + StringPool.STAR);
		}

		return new TermQuery(field, value);
	}

	private static String _toDateTerm(
		boolean dateTimeField, boolean upperBound, String value) {

		if (Validator.isNull(value)) {
			return null;
		}

		String relativeDateValue = _resolveRelativeDateValue(value);

		if (relativeDateValue != null) {
			value = relativeDateValue;
		}

		String digits = value.replaceAll("[^0-9]", "");

		if (dateTimeField) {
			String padded = digits + "000000000000";

			return padded.substring(0, 12) + (upperBound ? "59" : "00");
		}

		String padded = digits + "00000000";

		return padded.substring(0, 8) + (upperBound ? "235959" : "000000");
	}

	private static NestedQuery _toNestedQuery(
		long companyId, JSONObject jsonObject, Locale locale) {

		String propertyName = jsonObject.getString("propertyName");
		String value = jsonObject.getString("value");

		if (Validator.isNull(propertyName) || Validator.isNull(value)) {
			return null;
		}

		ObjectField objectField = AssetListObjectFieldUtil.fetchObjectField(
			jsonObject.getLong("classNameId"), companyId, propertyName);

		if (objectField == null) {
			return null;
		}

		String operatorName = jsonObject.getString("operatorName", "contains");

		String subfield = AssetListObjectFieldUtil.getFilterSubfield(
			locale, objectField);

		Query query = _toValueQuery(
			jsonObject, objectField, operatorName, subfield, value);

		if (query == null) {
			return null;
		}

		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(
			new TermQuery("nestedFieldArray.fieldName", propertyName),
			BooleanClauseOccur.MUST);
		booleanQuery.add(
			new TermQuery(
				"nestedFieldArray.valueFieldName",
				subfield.substring(subfield.indexOf(CharPool.PERIOD) + 1)),
			BooleanClauseOccur.MUST);
		booleanQuery.add(query, BooleanClauseOccur.MUST);

		return new NestedQuery("nestedFieldArray", booleanQuery);
	}

	private static Query _toPicklistQuery(
		JSONObject filterJSONObject, String subfield) {

		JSONArray valueJSONArray = filterJSONObject.getJSONArray("value");

		if (JSONUtil.isEmpty(valueJSONArray)) {
			return null;
		}

		BooleanQuery booleanQuery = new BooleanQuery();

		BooleanClauseOccur booleanClauseOccur = BooleanClauseOccur.SHOULD;

		String quantifier = filterJSONObject.getString("quantifier");

		if (Objects.equals(quantifier, "all")) {
			booleanClauseOccur = BooleanClauseOccur.MUST;
		}

		for (int i = 0; i < valueJSONArray.length(); i++) {
			JSONObject itemJSONObject = valueJSONArray.getJSONObject(i);

			String value = StringUtil.toLowerCase(
				itemJSONObject.getString("value"));

			booleanQuery.add(
				new TermQuery(subfield, value), booleanClauseOccur);
		}

		return booleanQuery;
	}

	private static Query _toQuery(
		long companyId, JSONObject jsonObject, Locale locale) {

		if (jsonObject == null) {
			return null;
		}

		if (_isCommonFieldRow(jsonObject)) {
			return _toCommonFieldQuery(
				jsonObject, locale, jsonObject.getString("propertyName"));
		}

		return _toNestedQuery(companyId, jsonObject, locale);
	}

	private static Query _toRangeQuery(
		JSONObject filterJSONObject, ObjectField objectField,
		String operatorName, String subfield) {

		boolean dateField = subfield.endsWith(".value_date");

		return _toTermRangeQuery(
			dateField, dateField && _isDateTimeField(objectField), subfield,
			filterJSONObject, operatorName);
	}

	private static Query _toTermRangeQuery(
		boolean dateField, boolean dateTime, String field,
		JSONObject jsonObject, String operatorName) {

		if (operatorName.equals("between")) {
			JSONArray valueJSONArray = jsonObject.getJSONArray("value");

			if ((valueJSONArray == null) || (valueJSONArray.length() < 2)) {
				return null;
			}

			String lowerTerm = GetterUtil.getString(
				valueJSONArray.getString(0), null);
			String upperTerm = GetterUtil.getString(
				valueJSONArray.getString(1), null);

			if (dateField) {
				lowerTerm = _toDateTerm(dateTime, false, lowerTerm);
				upperTerm = _toDateTerm(dateTime, true, upperTerm);
			}

			return new TermRangeQuery(field, lowerTerm, upperTerm, true, true);
		}

		String value = jsonObject.getString("value");

		if (Validator.isNull(value)) {
			return null;
		}

		if (operatorName.equals("ge")) {
			String lowerTerm =
				dateField ? _toDateTerm(dateTime, false, value) : value;

			return new TermRangeQuery(field, lowerTerm, null, true, false);
		}

		if (operatorName.equals("gt")) {
			String lowerTerm =
				dateField ? _toDateTerm(dateTime, true, value) : value;

			return new TermRangeQuery(field, lowerTerm, null, false, false);
		}

		if (operatorName.equals("le")) {
			String upperTerm =
				dateField ? _toDateTerm(dateTime, true, value) : value;

			return new TermRangeQuery(field, null, upperTerm, false, true);
		}

		if (operatorName.equals("lt")) {
			String upperTerm =
				dateField ? _toDateTerm(dateTime, false, value) : value;

			return new TermRangeQuery(field, null, upperTerm, false, false);
		}

		return null;
	}

	private static Query _toValueQuery(
		JSONObject filterJSONObject, ObjectField objectField,
		String operatorName, String subfield, String value) {

		if (operatorName.equals("contains") ||
			operatorName.equals("not-contains")) {

			if (objectField.getListTypeDefinitionId() != 0) {
				return _toPicklistQuery(filterJSONObject, subfield);
			}

			if (subfield.endsWith(".value_keyword")) {
				return new WildcardQuery(
					subfield,
					StringPool.STAR + StringUtil.toLowerCase(value) +
						StringPool.STAR);
			}
		}

		if (operatorName.equals("between") || operatorName.equals("ge") ||
			operatorName.equals("gt") || operatorName.equals("le") ||
			operatorName.equals("lt")) {

			return _toRangeQuery(
				filterJSONObject, objectField, operatorName, subfield);
		}

		if ((operatorName.equals("eq") || operatorName.equals("not-eq")) &&
			subfield.endsWith(".value_date")) {

			boolean dateTimeField = _isDateTimeField(objectField);

			return new TermRangeQuery(
				subfield, _toDateTerm(dateTimeField, false, value),
				_toDateTerm(dateTimeField, true, value), true, true);
		}

		if (subfield.endsWith(".value_keyword")) {
			return new TermQuery(subfield, StringUtil.toLowerCase(value));
		}

		if (operatorName.equals("eq") || operatorName.equals("not-eq") ||
			subfield.endsWith(".value_boolean") ||
			subfield.endsWith(".value_double") ||
			subfield.endsWith(".value_integer") ||
			subfield.endsWith(".value_long")) {

			return new TermQuery(subfield, value);
		}

		return new MatchQuery(subfield, value);
	}

	private static final String _TYPE_DATE = "date";

	private static final String _TYPE_DECIMAL = "decimal";

	private static final String _TYPE_INTEGER = "integer";

	private static final String _TYPE_TEXT = "text";

	private static final Map<String, String> _commonFieldTypesMap =
		HashMapBuilder.put(
			Field.CREATE_DATE, _TYPE_DATE
		).put(
			Field.DISPLAY_DATE, _TYPE_DATE
		).put(
			Field.EXPIRATION_DATE, _TYPE_DATE
		).put(
			Field.MODIFIED_DATE, _TYPE_DATE
		).put(
			Field.PRIORITY, _TYPE_DECIMAL
		).put(
			Field.PUBLISH_DATE, _TYPE_DATE
		).put(
			Field.REVIEW_DATE, _TYPE_DATE
		).put(
			Field.STATUS, _TYPE_INTEGER
		).put(
			Field.TITLE, _TYPE_TEXT
		).put(
			Field.USER_NAME, _TYPE_TEXT
		).put(
			"externalReferenceCode", _TYPE_TEXT
		).put(
			"viewCount", _TYPE_INTEGER
		).build();
	private static final Set<String> _localizedCommonFieldNames =
		SetUtil.fromArray(Field.TITLE);
	private static final Set<String> _relativeDateValues = SetUtil.fromArray(
		"last-year", "next-month", "now", "past-24-hours", "past-day",
		"past-month", "past-week", "past-year");

}