/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.util;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
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
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.text.Format;

import java.util.Calendar;
import java.util.Locale;
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

			NestedQuery nestedQuery = _toNestedQuery(
				companyId, jsonObject, locale);

			if (nestedQuery == null) {
				continue;
			}

			if (_isNegatedOperator(
					jsonObject.getString("operatorName", "contains"))) {

				booleanQuery.add(nestedQuery, BooleanClauseOccur.MUST_NOT);
			}
			else {
				booleanQuery.add(nestedQuery, BooleanClauseOccur.MUST);

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

	private static ObjectDefinition _fetchObjectDefinition(
		long classNameId, long companyId) {

		if (classNameId <= 0) {
			return null;
		}

		return ObjectDefinitionLocalServiceUtil.
			fetchObjectDefinitionByClassName(
				companyId, PortalUtil.getClassName(classNameId));
	}

	private static ObjectField _fetchObjectField(
		long classNameId, long companyId, String name) {

		ObjectDefinition objectDefinition = _fetchObjectDefinition(
			classNameId, companyId);

		if (objectDefinition == null) {
			return null;
		}

		return ObjectFieldLocalServiceUtil.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), name);
	}

	private static String _getSubfield(Locale locale, ObjectField objectField) {
		if (objectField.isIndexedAsKeyword()) {
			return "nestedFieldArray.value_keyword";
		}

		String dbType = objectField.getDBType();

		if (ObjectFieldConstants.DB_TYPE_BIG_DECIMAL.equals(dbType) ||
			ObjectFieldConstants.DB_TYPE_DOUBLE.equals(dbType)) {

			return "nestedFieldArray.value_double";
		}

		if (ObjectFieldConstants.DB_TYPE_BOOLEAN.equals(dbType)) {
			return "nestedFieldArray.value_boolean";
		}

		if (ObjectFieldConstants.DB_TYPE_DATE.equals(dbType) ||
			ObjectFieldConstants.DB_TYPE_DATE_TIME.equals(dbType)) {

			return "nestedFieldArray.value_date";
		}

		if (ObjectFieldConstants.DB_TYPE_INTEGER.equals(dbType)) {
			return "nestedFieldArray.value_integer";
		}

		if (ObjectFieldConstants.DB_TYPE_LONG.equals(dbType)) {
			return "nestedFieldArray.value_long";
		}

		if (objectField.isLocalized()) {
			return Field.getLocalizedName(locale, "nestedFieldArray.value");
		}

		String indexedLanguageId = objectField.getIndexedLanguageId();

		if (Validator.isNotNull(indexedLanguageId)) {
			return "nestedFieldArray.value_" + indexedLanguageId;
		}

		return "nestedFieldArray.value_text";
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

		if (jsonObject == null) {
			return null;
		}

		String propertyName = jsonObject.getString("propertyName");
		String value = jsonObject.getString("value");

		if (Validator.isNull(propertyName) || Validator.isNull(value)) {
			return null;
		}

		ObjectField objectField = _fetchObjectField(
			jsonObject.getLong("classNameId"), companyId, propertyName);

		if (objectField == null) {
			return null;
		}

		String operatorName = jsonObject.getString("operatorName", "contains");

		String subfield = _getSubfield(locale, objectField);

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

		BooleanClauseOccur booleanClauseOccur = BooleanClauseOccur.SHOULD;

		String quantifier = filterJSONObject.getString("quantifier");

		if (Objects.equals(quantifier, "all")) {
			booleanClauseOccur = BooleanClauseOccur.MUST;
		}

		BooleanQuery booleanQuery = new BooleanQuery();

		for (int i = 0; i < valueJSONArray.length(); i++) {
			JSONObject itemJSONObject = valueJSONArray.getJSONObject(i);

			String value = StringUtil.toLowerCase(
				itemJSONObject.getString("value"));

			booleanQuery.add(
				new TermQuery(subfield, value), booleanClauseOccur);
		}

		return booleanQuery;
	}

	private static Query _toRangeQuery(
		JSONObject filterJSONObject, ObjectField objectField,
		String operatorName, String subfield) {

		boolean dateField = subfield.endsWith(".value_date");

		boolean dateTimeField = false;

		if (dateField && _isDateTimeField(objectField)) {
			dateTimeField = true;
		}

		if (operatorName.equals("between")) {
			JSONArray valueJSONArray = filterJSONObject.getJSONArray("value");

			if ((valueJSONArray == null) || (valueJSONArray.length() < 2)) {
				return null;
			}

			String lowerTerm = GetterUtil.getString(
				valueJSONArray.getString(0), null);
			String upperTerm = GetterUtil.getString(
				valueJSONArray.getString(1), null);

			if (dateField) {
				lowerTerm = _toDateTerm(dateTimeField, false, lowerTerm);
				upperTerm = _toDateTerm(dateTimeField, true, upperTerm);
			}

			return new TermRangeQuery(
				subfield, lowerTerm, upperTerm, true, true);
		}

		String value = filterJSONObject.getString("value");

		if (Validator.isNull(value)) {
			return null;
		}

		if (operatorName.equals("ge")) {
			String lowerTerm =
				dateField ? _toDateTerm(dateTimeField, false, value) : value;

			return new TermRangeQuery(subfield, lowerTerm, null, true, false);
		}

		if (operatorName.equals("gt")) {
			String lowerTerm =
				dateField ? _toDateTerm(dateTimeField, true, value) : value;

			return new TermRangeQuery(subfield, lowerTerm, null, false, false);
		}

		if (operatorName.equals("le")) {
			String upperTerm =
				dateField ? _toDateTerm(dateTimeField, true, value) : value;

			return new TermRangeQuery(subfield, null, upperTerm, false, true);
		}

		if (operatorName.equals("lt")) {
			String upperTerm =
				dateField ? _toDateTerm(dateTimeField, false, value) : value;

			return new TermRangeQuery(subfield, null, upperTerm, false, false);
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

	private static final Set<String> _relativeDateValues = SetUtil.fromArray(
		"last-year", "next-month", "now", "past-24-hours", "past-day",
		"past-month", "past-week", "past-year");

}