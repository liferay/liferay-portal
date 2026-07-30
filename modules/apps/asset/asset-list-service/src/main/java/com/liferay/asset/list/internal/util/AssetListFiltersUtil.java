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
import com.liferay.portal.kernel.search.MatchQuery;
import com.liferay.portal.kernel.search.NestedQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;

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

		for (int i = 0; i < filtersJSONArray.length(); i++) {
			NestedQuery nestedQuery = _toNestedQuery(
				companyId, filtersJSONArray.getJSONObject(i), locale);

			if (nestedQuery == null) {
				continue;
			}

			booleanQuery.add(nestedQuery, BooleanClauseOccur.MUST);
		}

		if (!booleanQuery.hasClauses()) {
			return new BooleanClause[0];
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

	private static boolean _isNegatedOperator(String operatorName) {
		if (operatorName.equals("not-contains") ||
			operatorName.equals("not-eq")) {

			return true;
		}

		return false;
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

		Query query = _toValueQuery(operatorName, subfield, value);

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
		booleanQuery.add(
			query,
			_isNegatedOperator(operatorName) ? BooleanClauseOccur.MUST_NOT :
				BooleanClauseOccur.MUST);

		return new NestedQuery("nestedFieldArray", booleanQuery);
	}

	private static Query _toValueQuery(
		String operatorName, String subfield, String value) {

		if ((operatorName.equals("contains") ||
			 operatorName.equals("not-contains")) &&
			subfield.endsWith(".value_keyword")) {

			return new WildcardQuery(
				subfield,
				StringPool.STAR + StringUtil.toLowerCase(value) +
					StringPool.STAR);
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

}