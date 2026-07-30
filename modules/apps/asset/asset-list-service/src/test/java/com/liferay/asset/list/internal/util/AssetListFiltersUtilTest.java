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
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.MatchQuery;
import com.liferay.portal.kernel.search.NestedQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryTerm;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FastDateFormatFactoryImpl;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Felipe Lorenz
 */
public class AssetListFiltersUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		ReflectionTestUtil.setFieldValue(
			FastDateFormatFactoryUtil.class, "_fastDateFormatFactory",
			new FastDateFormatFactoryImpl());
	}

	@AfterClass
	public static void tearDownClass() {
		_objectDefinitionLocalServiceUtilMockedStatic.close();
		_objectFieldLocalServiceUtilMockedStatic.close();
		_portalUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_objectDefinitionLocalServiceUtilMockedStatic.reset();
		_objectFieldLocalServiceUtilMockedStatic.reset();
		_portalUtilMockedStatic.reset();

		_setUpLocalizationUtil();
	}

	@Test
	public void testFilterQueriesWithEqualityOperators() {
		String booleanFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
			ObjectFieldConstants.DB_TYPE_BOOLEAN, booleanFieldName);

		_assertTermQuery(
			"nestedFieldArray.value_boolean", "true",
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST,
				_buildFilterJSONObject("eq", booleanFieldName, "true"),
				booleanFieldName));

		String doubleFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_DECIMAL,
			ObjectFieldConstants.DB_TYPE_DOUBLE, doubleFieldName);

		String doubleFieldValue = String.valueOf(RandomTestUtil.randomDouble());

		_assertTermQuery(
			"nestedFieldArray.value_double", doubleFieldValue,
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST,
				_buildFilterJSONObject("eq", doubleFieldName, doubleFieldValue),
				doubleFieldName));

		String integerFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
			ObjectFieldConstants.DB_TYPE_INTEGER, integerFieldName);

		String integerFieldValue = String.valueOf(RandomTestUtil.randomInt());

		_assertTermQuery(
			"nestedFieldArray.value_integer", integerFieldValue,
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST,
				_buildFilterJSONObject(
					"eq", integerFieldName, integerFieldValue),
				integerFieldName));
		_assertTermQuery(
			"nestedFieldArray.value_integer", integerFieldValue,
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST_NOT,
				_buildFilterJSONObject(
					"not-eq", integerFieldName, integerFieldValue),
				integerFieldName));

		String keywordTextFieldName = RandomTestUtil.randomString();

		ObjectField keywordObjectField = _setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, keywordTextFieldName);

		Mockito.when(
			keywordObjectField.isIndexedAsKeyword()
		).thenReturn(
			true
		);

		_assertTermQuery(
			"nestedFieldArray.value_keyword", "alpha",
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST,
				_buildFilterJSONObject("eq", keywordTextFieldName, "Alpha"),
				keywordTextFieldName));

		String localizedTextFieldName = RandomTestUtil.randomString();

		ObjectField localizedObjectField = _setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, localizedTextFieldName);

		Mockito.when(
			localizedObjectField.isLocalized()
		).thenReturn(
			true
		);

		String localizedTextFieldValue = RandomTestUtil.randomString();

		_assertTermQuery(
			"nestedFieldArray.value_en_US", localizedTextFieldValue,
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST,
				_buildFilterJSONObject(
					"eq", localizedTextFieldName, localizedTextFieldValue),
				localizedTextFieldName));

		String longIntegerFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
			ObjectFieldConstants.DB_TYPE_LONG, longIntegerFieldName);

		String longFieldValue = String.valueOf(RandomTestUtil.randomLong());

		_assertTermQuery(
			"nestedFieldArray.value_long", longFieldValue,
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST,
				_buildFilterJSONObject(
					"eq", longIntegerFieldName, longFieldValue),
				longIntegerFieldName));

		String textFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, textFieldName);

		String textFieldValue = RandomTestUtil.randomString();

		_assertTermQuery(
			"nestedFieldArray.value_text", textFieldValue,
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST,
				_buildFilterJSONObject("eq", textFieldName, textFieldValue),
				textFieldName));
		_assertTermQuery(
			"nestedFieldArray.value_text", textFieldValue,
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST_NOT,
				_buildFilterJSONObject("not-eq", textFieldName, textFieldValue),
				textFieldName));
	}

	@Test
	public void testFilterQueriesWithInvalidInput() {
		BooleanClause[] booleanClauses =
			AssetListFiltersUtil.getFiltersBooleanClauses(
				_COMPANY_ID, null, LocaleUtil.US);

		Assert.assertEquals(
			Arrays.toString(booleanClauses), 0, booleanClauses.length);

		booleanClauses = AssetListFiltersUtil.getFiltersBooleanClauses(
			_COMPANY_ID, JSONFactoryUtil.createJSONArray(), LocaleUtil.US);

		Assert.assertEquals(
			Arrays.toString(booleanClauses), 0, booleanClauses.length);
	}

	@Test
	public void testFilterQueriesWithKeywordTextContainsOperators() {
		String keywordTextFieldName = RandomTestUtil.randomString();

		ObjectField keywordObjectField = _setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, keywordTextFieldName);

		Mockito.when(
			keywordObjectField.isIndexedAsKeyword()
		).thenReturn(
			true
		);

		_assertWildcardQuery(
			"nestedFieldArray.value_keyword", "*alpha*",
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST,
				_buildFilterJSONObject(
					"contains", keywordTextFieldName, "Alpha"),
				keywordTextFieldName));
		_assertWildcardQuery(
			"nestedFieldArray.value_keyword", "*alpha*",
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST_NOT,
				_buildFilterJSONObject(
					"not-contains", keywordTextFieldName, "Alpha"),
				keywordTextFieldName));
	}

	@Test
	public void testFilterQueriesWithNumericRangeOperators() {
		String doubleFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_DECIMAL,
			ObjectFieldConstants.DB_TYPE_DOUBLE, doubleFieldName);

		String doubleLowerFieldValue = "1.5";
		String doubleUpperFieldValue = "2.5";

		_assertTermRangeQuery(
			"nestedFieldArray.value_double", true, true, doubleLowerFieldValue,
			doubleUpperFieldValue,
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST,
				_buildFilterJSONObject(
					"between", doubleFieldName,
					JSONUtil.putAll(
						doubleLowerFieldValue, doubleUpperFieldValue)),
				doubleFieldName));

		String doubleFieldValue = String.valueOf(RandomTestUtil.randomDouble());

		_assertTermRangeQuery(
			"nestedFieldArray.value_double", false, false, doubleFieldValue,
			null,
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST,
				_buildFilterJSONObject("gt", doubleFieldName, doubleFieldValue),
				doubleFieldName));

		String integerFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
			ObjectFieldConstants.DB_TYPE_INTEGER, integerFieldName);

		String integerLowerFieldValue = "1";
		String integerUpperFieldValue = "2";

		_assertTermRangeQuery(
			"nestedFieldArray.value_integer", true, true,
			integerLowerFieldValue, integerUpperFieldValue,
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST,
				_buildFilterJSONObject(
					"between", integerFieldName,
					JSONUtil.putAll(
						integerLowerFieldValue, integerUpperFieldValue)),
				integerFieldName));

		String integerFieldValue = String.valueOf(RandomTestUtil.randomInt());

		_assertTermRangeQuery(
			"nestedFieldArray.value_integer", true, false, integerFieldValue,
			null,
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST,
				_buildFilterJSONObject(
					"ge", integerFieldName, integerFieldValue),
				integerFieldName));
		_assertTermRangeQuery(
			"nestedFieldArray.value_integer", false, false, integerFieldValue,
			null,
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST,
				_buildFilterJSONObject(
					"gt", integerFieldName, integerFieldValue),
				integerFieldName));
		_assertTermRangeQuery(
			"nestedFieldArray.value_integer", false, true, null,
			integerFieldValue,
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST,
				_buildFilterJSONObject(
					"le", integerFieldName, integerFieldValue),
				integerFieldName));
		_assertTermRangeQuery(
			"nestedFieldArray.value_integer", false, false, null,
			integerFieldValue,
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST,
				_buildFilterJSONObject(
					"lt", integerFieldName, integerFieldValue),
				integerFieldName));

		String longIntegerFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
			ObjectFieldConstants.DB_TYPE_LONG, longIntegerFieldName);

		String longLowerFieldValue = "1";
		String longUpperFieldValue = "2";

		_assertTermRangeQuery(
			"nestedFieldArray.value_long", true, true, longLowerFieldValue,
			longUpperFieldValue,
			_assertNestedRowAndGetQuery(
				BooleanClauseOccur.MUST,
				_buildFilterJSONObject(
					"between", longIntegerFieldName,
					JSONUtil.putAll(longLowerFieldValue, longUpperFieldValue)),
				longIntegerFieldName));
	}

	@Test
	public void testFilterQueriesWithTextContainsOperators() {
		String textFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, textFieldName);

		String textFieldValue = RandomTestUtil.randomString();

		Query containsQuery = _assertNestedRowAndGetQuery(
			BooleanClauseOccur.MUST,
			_buildFilterJSONObject("contains", textFieldName, textFieldValue),
			textFieldName);

		Assert.assertTrue(
			containsQuery.toString(), containsQuery instanceof MatchQuery);

		Query containsWithQuantifierQuery = _assertNestedRowAndGetQuery(
			BooleanClauseOccur.MUST,
			_buildFilterJSONObject(
				"contains", textFieldName, textFieldValue
			).put(
				"quantifier", "any"
			),
			textFieldName);

		Assert.assertTrue(
			containsWithQuantifierQuery.toString(),
			containsWithQuantifierQuery instanceof MatchQuery);

		Query notContainsQuery = _assertNestedRowAndGetQuery(
			BooleanClauseOccur.MUST_NOT,
			_buildFilterJSONObject(
				"not-contains", textFieldName, textFieldValue),
			textFieldName);

		Assert.assertTrue(
			notContainsQuery.toString(),
			notContainsQuery instanceof MatchQuery);
	}

	private Query _assertNestedRowAndGetQuery(
		BooleanClauseOccur expectedBooleanClauseOccur,
		JSONObject filterJSONObject, String propertyName) {

		BooleanClause[] booleanClauses =
			AssetListFiltersUtil.getFiltersBooleanClauses(
				_COMPANY_ID, JSONUtil.putAll(filterJSONObject), LocaleUtil.US);

		Assert.assertEquals(
			Arrays.toString(booleanClauses), 1, booleanClauses.length);

		BooleanClause<?> outerBooleanClause = booleanClauses[0];

		Assert.assertEquals(
			BooleanClauseOccur.MUST,
			outerBooleanClause.getBooleanClauseOccur());

		BooleanQuery outerBooleanQuery =
			(BooleanQuery)outerBooleanClause.getClause();

		List<BooleanClause<Query>> rowBooleanClauses =
			outerBooleanQuery.clauses();

		BooleanClause<Query> rowBooleanClause = rowBooleanClauses.get(0);

		NestedQuery nestedQuery = (NestedQuery)rowBooleanClause.getClause();

		Assert.assertEquals("nestedFieldArray", nestedQuery.getPath());

		BooleanQuery innerBooleanQuery = (BooleanQuery)nestedQuery.getQuery();

		List<BooleanClause<Query>> innerBooleanClauses =
			innerBooleanQuery.clauses();

		Assert.assertEquals(
			innerBooleanClauses.toString(), 3, innerBooleanClauses.size());

		BooleanClause<Query> fieldNameBooleanClause = innerBooleanClauses.get(
			0);

		TermQuery fieldNameTermQuery =
			(TermQuery)fieldNameBooleanClause.getClause();

		QueryTerm fieldNameQueryTerm = fieldNameTermQuery.getQueryTerm();

		Assert.assertEquals(
			"nestedFieldArray.fieldName", fieldNameQueryTerm.getField());
		Assert.assertEquals(propertyName, fieldNameQueryTerm.getValue());

		Assert.assertEquals(
			BooleanClauseOccur.MUST,
			fieldNameBooleanClause.getBooleanClauseOccur());

		BooleanClause<Query> valueFieldNameBooleanClause =
			innerBooleanClauses.get(1);

		TermQuery valueFieldNameTermQuery =
			(TermQuery)valueFieldNameBooleanClause.getClause();

		QueryTerm valueFieldNameQueryTerm =
			valueFieldNameTermQuery.getQueryTerm();

		Assert.assertEquals(
			"nestedFieldArray.valueFieldName",
			valueFieldNameQueryTerm.getField());

		Assert.assertEquals(
			BooleanClauseOccur.MUST,
			valueFieldNameBooleanClause.getBooleanClauseOccur());

		BooleanClause<Query> valueBooleanClause = innerBooleanClauses.get(2);

		Assert.assertEquals(
			expectedBooleanClauseOccur,
			valueBooleanClause.getBooleanClauseOccur());

		return valueBooleanClause.getClause();
	}

	private void _assertTermQuery(
		String expectedField, String expectedValue, Query query) {

		Assert.assertTrue(query.toString(), query instanceof TermQuery);

		TermQuery termQuery = (TermQuery)query;

		QueryTerm queryTerm = termQuery.getQueryTerm();

		Assert.assertEquals(expectedField, queryTerm.getField());
		Assert.assertEquals(expectedValue, queryTerm.getValue());
	}

	private void _assertTermRangeQuery(
		String expectedField, boolean expectedIncludesLower,
		boolean expectedIncludesUpper, String expectedLowerTerm,
		String expectedUpperTerm, Query query) {

		Assert.assertTrue(query.toString(), query instanceof TermRangeQuery);

		TermRangeQuery termRangeQuery = (TermRangeQuery)query;

		Assert.assertEquals(expectedField, termRangeQuery.getField());
		Assert.assertEquals(
			expectedIncludesLower, termRangeQuery.includesLower());
		Assert.assertEquals(
			expectedIncludesUpper, termRangeQuery.includesUpper());
		Assert.assertEquals(expectedLowerTerm, termRangeQuery.getLowerTerm());
		Assert.assertEquals(expectedUpperTerm, termRangeQuery.getUpperTerm());
	}

	private void _assertWildcardQuery(
		String expectedField, String expectedValue, Query query) {

		Assert.assertTrue(query.toString(), query instanceof WildcardQuery);

		WildcardQuery wildcardQuery = (WildcardQuery)query;

		QueryTerm queryTerm = wildcardQuery.getQueryTerm();

		Assert.assertEquals(expectedField, queryTerm.getField());
		Assert.assertEquals(expectedValue, queryTerm.getValue());
	}

	private JSONObject _buildFilterJSONObject(
		String operatorName, String propertyName, JSONArray valueJSONArray) {

		return JSONUtil.put(
			"classNameId", _CLASS_NAME_ID
		).put(
			"classTypeId", _CLASS_TYPE_ID
		).put(
			"operatorName", operatorName
		).put(
			"propertyName", propertyName
		).put(
			"value", valueJSONArray
		);
	}

	private JSONObject _buildFilterJSONObject(
		String operatorName, String propertyName, String value) {

		return JSONUtil.put(
			"classNameId", _CLASS_NAME_ID
		).put(
			"classTypeId", _CLASS_TYPE_ID
		).put(
			"operatorName", operatorName
		).put(
			"propertyName", propertyName
		).put(
			"value", value
		);
	}

	private void _setUpLocalizationUtil() {
		LocalizationUtil localizationUtil = new LocalizationUtil();

		Localization localization = Mockito.mock(Localization.class);

		Mockito.when(
			localization.getLocalizedName(
				Mockito.anyString(), Mockito.anyString())
		).thenAnswer(
			invocation ->
				invocation.getArgument(0) + "_" + invocation.getArgument(1)
		);

		localizationUtil.setLocalization(localization);
	}

	private ObjectField _setUpObjectField(
		String businessType, String dbType, String fieldName) {

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getObjectDefinitionId()
		).thenReturn(
			_CLASS_TYPE_ID
		);

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getBusinessType()
		).thenReturn(
			businessType
		);

		Mockito.when(
			objectField.getDBType()
		).thenReturn(
			dbType
		);

		Mockito.when(
			objectField.getName()
		).thenReturn(
			fieldName
		);

		_objectDefinitionLocalServiceUtilMockedStatic.when(
			() ->
				ObjectDefinitionLocalServiceUtil.
					fetchObjectDefinitionByClassName(
						_COMPANY_ID, "com.liferay.test.Class" + _CLASS_NAME_ID)
		).thenReturn(
			objectDefinition
		);

		_objectFieldLocalServiceUtilMockedStatic.when(
			() -> ObjectFieldLocalServiceUtil.fetchObjectField(
				_CLASS_TYPE_ID, fieldName)
		).thenReturn(
			objectField
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getClassName(_CLASS_NAME_ID)
		).thenReturn(
			"com.liferay.test.Class" + _CLASS_NAME_ID
		);

		return objectField;
	}

	private static final long _CLASS_NAME_ID = RandomTestUtil.randomLong();

	private static final long _CLASS_TYPE_ID = RandomTestUtil.randomLong();

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final MockedStatic<ObjectDefinitionLocalServiceUtil>
		_objectDefinitionLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectDefinitionLocalServiceUtil.class);
	private static final MockedStatic<ObjectFieldLocalServiceUtil>
		_objectFieldLocalServiceUtilMockedStatic = Mockito.mockStatic(
			ObjectFieldLocalServiceUtil.class);
	private static final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);

}