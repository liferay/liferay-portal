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
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FastDateFormatFactoryImpl;

import java.text.Format;

import java.util.Arrays;
import java.util.Date;
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
	public void testFilterQueriesWithCommonFields() {
		String priority = String.valueOf(RandomTestUtil.randomDouble());

		_assertTermRangeQuery(
			Field.PRIORITY, false, false, priority, null,
			_assertCommonFieldQuery(
				BooleanClauseOccur.MUST,
				_getCommonFieldFilterJSONObject(
					"gt", Field.PRIORITY, priority)));

		String status = String.valueOf(RandomTestUtil.randomInt());

		_assertTermQuery(
			Field.STATUS, status,
			_assertCommonFieldQuery(
				BooleanClauseOccur.MUST_NOT,
				_getCommonFieldFilterJSONObject(
					"not-eq", Field.STATUS, status)));

		String title = RandomTestUtil.randomString();

		_assertMatchQuery(
			"localized_title_en_US", title,
			_assertCommonFieldQuery(
				BooleanClauseOccur.MUST,
				_getCommonFieldFilterJSONObject("eq", Field.TITLE, title)));
		_assertMatchQuery(
			"localized_title_en_US", title,
			_assertCommonFieldQuery(
				BooleanClauseOccur.MUST,
				_getCommonFieldFilterJSONObject(
					"contains", Field.TITLE, title)));

		String userName = RandomTestUtil.randomString();

		_assertTermQuery(
			Field.USER_NAME, StringUtil.toLowerCase(userName),
			_assertCommonFieldQuery(
				BooleanClauseOccur.MUST,
				_getCommonFieldFilterJSONObject(
					"eq", Field.USER_NAME, userName)));
		_assertWildcardQuery(
			Field.USER_NAME,
			StringBundler.concat("*", StringUtil.toLowerCase(userName), "*"),
			_assertCommonFieldQuery(
				BooleanClauseOccur.MUST_NOT,
				_getCommonFieldFilterJSONObject(
					"not-contains", Field.USER_NAME, userName)));

		String viewCount = String.valueOf(RandomTestUtil.randomInt());

		_assertTermQuery(
			"viewCount", viewCount,
			_assertCommonFieldQuery(
				BooleanClauseOccur.MUST,
				_getCommonFieldFilterJSONObject("eq", "viewCount", viewCount)));

		_assertTermRangeQuery(
			Field.CREATE_DATE, false, false, "20260115235959", null,
			_assertCommonFieldQuery(
				BooleanClauseOccur.MUST,
				_getCommonFieldFilterJSONObject(
					"gt", Field.CREATE_DATE, "2026-01-15")));
		_assertTermRangeQuery(
			Field.MODIFIED_DATE, true, true, "20260115000000", "20260120235959",
			_assertCommonFieldQuery(
				BooleanClauseOccur.MUST,
				_getCommonFieldFilterJSONObject(
					"between", Field.MODIFIED_DATE,
					JSONUtil.putAll("2026-01-15", "2026-01-20"))));
		_assertTermRangeQuery(
			Field.MODIFIED_DATE, true, true, "20260115000000", "20260115235959",
			_assertCommonFieldQuery(
				BooleanClauseOccur.MUST,
				_getCommonFieldFilterJSONObject(
					"eq", Field.MODIFIED_DATE, "2026-01-15")));
		_assertTermRangeQuery(
			Field.MODIFIED_DATE, true, true, "20260115000000", "20260115235959",
			_assertCommonFieldQuery(
				BooleanClauseOccur.MUST_NOT,
				_getCommonFieldFilterJSONObject(
					"not-eq", Field.MODIFIED_DATE, "2026-01-15")));

		BooleanClause[] booleanClauses =
			AssetListFiltersUtil.getFiltersBooleanClauses(
				_COMPANY_ID,
				JSONUtil.putAll(
					_getCommonFieldFilterJSONObject(
						"eq", RandomTestUtil.randomString(),
						RandomTestUtil.randomString())),
				LocaleUtil.US);

		Assert.assertEquals(
			Arrays.toString(booleanClauses), 0, booleanClauses.length);
	}

	@Test
	public void testFilterQueriesWithDateAndDateTimeOperators() {
		String dateFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_DATE,
			ObjectFieldConstants.DB_TYPE_DATE, dateFieldName);

		_assertTermRangeQuery(
			"nestedFieldArray.value_date", true, true, "20260115000000",
			"20260120235959",
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject(
					"between", dateFieldName,
					JSONUtil.putAll("2026-01-15", "2026-01-20")),
				dateFieldName));
		_assertTermRangeQuery(
			"nestedFieldArray.value_date", true, true, "20260115000000",
			"20260115235959",
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("eq", dateFieldName, "2026-01-15"),
				dateFieldName));
		_assertTermRangeQuery(
			"nestedFieldArray.value_date", true, false, "20260115000000", null,
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("ge", dateFieldName, "2026-01-15"),
				dateFieldName));
		_assertTermRangeQuery(
			"nestedFieldArray.value_date", false, false, "20260115235959", null,
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("gt", dateFieldName, "2026-01-15"),
				dateFieldName));
		_assertTermRangeQuery(
			"nestedFieldArray.value_date", false, true, null, "20260115235959",
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("le", dateFieldName, "2026-01-15"),
				dateFieldName));
		_assertTermRangeQuery(
			"nestedFieldArray.value_date", false, false, null, "20260115000000",
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("lt", dateFieldName, "2026-01-15"),
				dateFieldName));

		String dateTimeFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME,
			ObjectFieldConstants.DB_TYPE_DATE_TIME, dateTimeFieldName);

		_assertTermRangeQuery(
			"nestedFieldArray.value_date", true, true, "20260115103000",
			"20260115103059",
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject(
					"eq", dateTimeFieldName, "2026-01-15 10:30"),
				dateTimeFieldName));
	}

	@Test
	public void testFilterQueriesWithEqualityOperators() {
		String booleanFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
			ObjectFieldConstants.DB_TYPE_BOOLEAN, booleanFieldName);

		_assertTermQuery(
			"nestedFieldArray.value_boolean", "true",
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("eq", booleanFieldName, "true"),
				booleanFieldName));

		String doubleFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_DECIMAL,
			ObjectFieldConstants.DB_TYPE_DOUBLE, doubleFieldName);

		String doubleFieldValue = String.valueOf(RandomTestUtil.randomDouble());

		_assertTermQuery(
			"nestedFieldArray.value_double", doubleFieldValue,
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("eq", doubleFieldName, doubleFieldValue),
				doubleFieldName));

		String integerFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
			ObjectFieldConstants.DB_TYPE_INTEGER, integerFieldName);

		String integerFieldValue = String.valueOf(RandomTestUtil.randomInt());

		_assertTermQuery(
			"nestedFieldArray.value_integer", integerFieldValue,
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("eq", integerFieldName, integerFieldValue),
				integerFieldName));
		_assertTermQuery(
			"nestedFieldArray.value_integer", integerFieldValue,
			_assertNestedQuery(
				BooleanClauseOccur.MUST_NOT,
				_getFilterJSONObject(
					"not-eq", integerFieldName, integerFieldValue),
				integerFieldName));

		String keywordTextFieldName = RandomTestUtil.randomString();

		_setUpKeywordTextObjectField(keywordTextFieldName);

		_assertTermQuery(
			"nestedFieldArray.value_keyword", "alpha",
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("eq", keywordTextFieldName, "Alpha"),
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
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject(
					"eq", localizedTextFieldName, localizedTextFieldValue),
				localizedTextFieldName));

		String longIntegerFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
			ObjectFieldConstants.DB_TYPE_LONG, longIntegerFieldName);

		String longIntegerFieldValue = String.valueOf(
			RandomTestUtil.randomLong());

		_assertTermQuery(
			"nestedFieldArray.value_long", longIntegerFieldValue,
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject(
					"eq", longIntegerFieldName, longIntegerFieldValue),
				longIntegerFieldName));

		String textFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, textFieldName);

		String textFieldValue = RandomTestUtil.randomString();

		_assertTermQuery(
			"nestedFieldArray.value_text", textFieldValue,
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("eq", textFieldName, textFieldValue),
				textFieldName));
		_assertTermQuery(
			"nestedFieldArray.value_text", textFieldValue,
			_assertNestedQuery(
				BooleanClauseOccur.MUST_NOT,
				_getFilterJSONObject("not-eq", textFieldName, textFieldValue),
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

		_setUpKeywordTextObjectField(keywordTextFieldName);

		_assertWildcardQuery(
			"nestedFieldArray.value_keyword", "*alpha*",
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("contains", keywordTextFieldName, "Alpha"),
				keywordTextFieldName));
		_assertWildcardQuery(
			"nestedFieldArray.value_keyword", "*alpha*",
			_assertNestedQuery(
				BooleanClauseOccur.MUST_NOT,
				_getFilterJSONObject(
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
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject(
					"between", doubleFieldName,
					JSONUtil.putAll(
						doubleLowerFieldValue, doubleUpperFieldValue)),
				doubleFieldName));

		String doubleFieldValue = String.valueOf(RandomTestUtil.randomDouble());

		_assertTermRangeQuery(
			"nestedFieldArray.value_double", false, false, doubleFieldValue,
			null,
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("gt", doubleFieldName, doubleFieldValue),
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
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject(
					"between", integerFieldName,
					JSONUtil.putAll(
						integerLowerFieldValue, integerUpperFieldValue)),
				integerFieldName));

		String integerFieldValue = String.valueOf(RandomTestUtil.randomInt());

		_assertTermRangeQuery(
			"nestedFieldArray.value_integer", true, false, integerFieldValue,
			null,
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("ge", integerFieldName, integerFieldValue),
				integerFieldName));
		_assertTermRangeQuery(
			"nestedFieldArray.value_integer", false, false, integerFieldValue,
			null,
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("gt", integerFieldName, integerFieldValue),
				integerFieldName));
		_assertTermRangeQuery(
			"nestedFieldArray.value_integer", false, true, null,
			integerFieldValue,
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("le", integerFieldName, integerFieldValue),
				integerFieldName));
		_assertTermRangeQuery(
			"nestedFieldArray.value_integer", false, false, null,
			integerFieldValue,
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("lt", integerFieldName, integerFieldValue),
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
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject(
					"between", longIntegerFieldName,
					JSONUtil.putAll(longLowerFieldValue, longUpperFieldValue)),
				longIntegerFieldName));
	}

	@Test
	public void testFilterQueriesWithPicklistMultiValueOperators() {
		String picklistFieldName = RandomTestUtil.randomString();

		_setUpPicklistObjectField(picklistFieldName);

		_assertPicklistBooleanQuery(
			BooleanClauseOccur.MUST,
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject(
					"contains", picklistFieldName,
					JSONUtil.putAll(
						_getPicklistValueJSONObject("approved"),
						_getPicklistValueJSONObject("draft"))
				).put(
					"quantifier", "all"
				),
				picklistFieldName),
			"approved", "draft");
		_assertPicklistBooleanQuery(
			BooleanClauseOccur.SHOULD,
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject(
					"contains", picklistFieldName,
					JSONUtil.putAll(_getPicklistValueJSONObject("Approved"))
				).put(
					"quantifier", "any"
				),
				picklistFieldName),
			"approved");
		_assertPicklistBooleanQuery(
			BooleanClauseOccur.SHOULD,
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject(
					"contains", picklistFieldName,
					JSONUtil.putAll(_getPicklistValueJSONObject("approved"))),
				picklistFieldName),
			"approved");
		_assertPicklistBooleanQuery(
			BooleanClauseOccur.SHOULD,
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject(
					"contains", picklistFieldName,
					JSONUtil.putAll(
						_getPicklistValueJSONObject("approved"),
						_getPicklistValueJSONObject("draft"))
				).put(
					"quantifier", "any"
				),
				picklistFieldName),
			"approved", "draft");
		_assertPicklistBooleanQuery(
			BooleanClauseOccur.SHOULD,
			_assertNestedQuery(
				BooleanClauseOccur.MUST_NOT,
				_getFilterJSONObject(
					"not-contains", picklistFieldName,
					JSONUtil.putAll(
						_getPicklistValueJSONObject("approved"),
						_getPicklistValueJSONObject("draft"))
				).put(
					"quantifier", "any"
				),
				picklistFieldName),
			"approved", "draft");

		BooleanClause[] booleanClauses =
			AssetListFiltersUtil.getFiltersBooleanClauses(
				_COMPANY_ID,
				JSONUtil.putAll(
					_getFilterJSONObject(
						"contains", picklistFieldName,
						JSONFactoryUtil.createJSONArray()
					).put(
						"quantifier", "any"
					)),
				LocaleUtil.US);

		Assert.assertEquals(
			Arrays.toString(booleanClauses), 0, booleanClauses.length);
	}

	@Test
	public void testFilterQueriesWithRelativeDateOperators() {
		String dateFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_DATE,
			ObjectFieldConstants.DB_TYPE_DATE, dateFieldName);

		String lastYearLowerTerm = _resolveRelativeDateLowerTerm(
			dateFieldName, "last-year");
		String nextMonthLowerTerm = _resolveRelativeDateLowerTerm(
			dateFieldName, "next-month");
		String nowLowerTerm = _resolveRelativeDateLowerTerm(
			dateFieldName, "now");
		String past24HoursLowerTerm = _resolveRelativeDateLowerTerm(
			dateFieldName, "past-24-hours");
		String pastDayLowerTerm = _resolveRelativeDateLowerTerm(
			dateFieldName, "past-day");
		String pastMonthLowerTerm = _resolveRelativeDateLowerTerm(
			dateFieldName, "past-month");
		String pastWeekLowerTerm = _resolveRelativeDateLowerTerm(
			dateFieldName, "past-week");
		String pastYearLowerTerm = _resolveRelativeDateLowerTerm(
			dateFieldName, "past-year");

		for (String lowerTerm :
				new String[] {
					lastYearLowerTerm, nextMonthLowerTerm, nowLowerTerm,
					past24HoursLowerTerm, pastDayLowerTerm, pastMonthLowerTerm,
					pastWeekLowerTerm, pastYearLowerTerm
				}) {

			Assert.assertEquals(lowerTerm, 14, lowerTerm.length());
			Assert.assertTrue(lowerTerm, lowerTerm.endsWith("000000"));
		}

		Format format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMdd");

		Assert.assertEquals(format.format(new Date()) + "000000", nowLowerTerm);

		Assert.assertEquals(lastYearLowerTerm, pastYearLowerTerm);
		Assert.assertEquals(past24HoursLowerTerm, pastDayLowerTerm);
		Assert.assertTrue(nowLowerTerm.compareTo(nextMonthLowerTerm) < 0);
		Assert.assertTrue(pastDayLowerTerm.compareTo(nowLowerTerm) < 0);
		Assert.assertTrue(pastMonthLowerTerm.compareTo(pastWeekLowerTerm) < 0);
		Assert.assertTrue(pastWeekLowerTerm.compareTo(pastDayLowerTerm) < 0);
		Assert.assertTrue(pastYearLowerTerm.compareTo(pastMonthLowerTerm) < 0);

		_assertTermRangeQuery(
			"nestedFieldArray.value_date", false, true, null,
			format.format(new Date()) + "235959",
			_assertNestedQuery(
				BooleanClauseOccur.MUST,
				_getFilterJSONObject("le", dateFieldName, "now"),
				dateFieldName));
	}

	@Test
	public void testFilterQueriesWithTextContainsOperators() {
		String textFieldName = RandomTestUtil.randomString();

		_setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, textFieldName);

		String textFieldValue = RandomTestUtil.randomString();

		Query containsQuery = _assertNestedQuery(
			BooleanClauseOccur.MUST,
			_getFilterJSONObject("contains", textFieldName, textFieldValue),
			textFieldName);

		Assert.assertTrue(
			containsQuery.toString(), containsQuery instanceof MatchQuery);

		Query containsWithQuantifierQuery = _assertNestedQuery(
			BooleanClauseOccur.MUST,
			_getFilterJSONObject(
				"contains", textFieldName, textFieldValue
			).put(
				"quantifier", "any"
			),
			textFieldName);

		Assert.assertTrue(
			containsWithQuantifierQuery.toString(),
			containsWithQuantifierQuery instanceof MatchQuery);

		Query notContainsQuery = _assertNestedQuery(
			BooleanClauseOccur.MUST_NOT,
			_getFilterJSONObject("not-contains", textFieldName, textFieldValue),
			textFieldName);

		Assert.assertTrue(
			notContainsQuery.toString(),
			notContainsQuery instanceof MatchQuery);
	}

	private Query _assertCommonFieldQuery(
		BooleanClauseOccur expectedBooleanClauseOccur,
		JSONObject filterJSONObject) {

		BooleanClause[] booleanClauses =
			AssetListFiltersUtil.getFiltersBooleanClauses(
				_COMPANY_ID, JSONUtil.putAll(filterJSONObject), LocaleUtil.US);

		Assert.assertEquals(
			Arrays.toString(booleanClauses), 1, booleanClauses.length);

		BooleanClause<?> filtersBooleanClause = booleanClauses[0];

		Assert.assertEquals(
			BooleanClauseOccur.MUST,
			filtersBooleanClause.getBooleanClauseOccur());

		BooleanQuery filtersBooleanQuery =
			(BooleanQuery)filtersBooleanClause.getClause();

		List<BooleanClause<Query>> filterBooleanClauses =
			filtersBooleanQuery.clauses();

		BooleanClause<Query> filterBooleanClause = filterBooleanClauses.get(0);

		Assert.assertEquals(
			expectedBooleanClauseOccur,
			filterBooleanClause.getBooleanClauseOccur());

		Query query = filterBooleanClause.getClause();

		Assert.assertFalse(query.toString(), query instanceof NestedQuery);

		return query;
	}

	private void _assertMatchQuery(
		String expectedField, String expectedValue, Query query) {

		Assert.assertTrue(query.toString(), query instanceof MatchQuery);

		MatchQuery matchQuery = (MatchQuery)query;

		Assert.assertEquals(expectedField, matchQuery.getField());
		Assert.assertEquals(expectedValue, matchQuery.getValue());
	}

	private QueryTerm _assertNestedFieldQueryTerm(
		BooleanClause<Query> booleanClause, String expectedField) {

		Assert.assertEquals(
			BooleanClauseOccur.MUST, booleanClause.getBooleanClauseOccur());

		TermQuery termQuery = (TermQuery)booleanClause.getClause();

		QueryTerm queryTerm = termQuery.getQueryTerm();

		Assert.assertEquals(expectedField, queryTerm.getField());

		return queryTerm;
	}

	private Query _assertNestedQuery(
		BooleanClauseOccur expectedBooleanClauseOccur,
		JSONObject filterJSONObject, String propertyName) {

		BooleanClause[] booleanClauses =
			AssetListFiltersUtil.getFiltersBooleanClauses(
				_COMPANY_ID, JSONUtil.putAll(filterJSONObject), LocaleUtil.US);

		Assert.assertEquals(
			Arrays.toString(booleanClauses), 1, booleanClauses.length);

		BooleanClause<?> filtersBooleanClause = booleanClauses[0];

		Assert.assertEquals(
			BooleanClauseOccur.MUST,
			filtersBooleanClause.getBooleanClauseOccur());

		BooleanQuery filtersBooleanQuery =
			(BooleanQuery)filtersBooleanClause.getClause();

		List<BooleanClause<Query>> filterBooleanClauses =
			filtersBooleanQuery.clauses();

		BooleanClause<Query> filterBooleanClause = filterBooleanClauses.get(0);

		Assert.assertEquals(
			expectedBooleanClauseOccur,
			filterBooleanClause.getBooleanClauseOccur());

		NestedQuery nestedQuery = (NestedQuery)filterBooleanClause.getClause();

		Assert.assertEquals("nestedFieldArray", nestedQuery.getPath());

		BooleanQuery nestedFieldBooleanQuery =
			(BooleanQuery)nestedQuery.getQuery();

		List<BooleanClause<Query>> nestedFieldBooleanClauses =
			nestedFieldBooleanQuery.clauses();

		Assert.assertEquals(
			nestedFieldBooleanClauses.toString(), 3,
			nestedFieldBooleanClauses.size());

		QueryTerm nestedFieldQueryTerm = _assertNestedFieldQueryTerm(
			nestedFieldBooleanClauses.get(0), "nestedFieldArray.fieldName");

		Assert.assertEquals(propertyName, nestedFieldQueryTerm.getValue());

		_assertNestedFieldQueryTerm(
			nestedFieldBooleanClauses.get(1),
			"nestedFieldArray.valueFieldName");

		BooleanClause<Query> nestedFieldBooleanClause =
			nestedFieldBooleanClauses.get(2);

		Assert.assertEquals(
			BooleanClauseOccur.MUST,
			nestedFieldBooleanClause.getBooleanClauseOccur());

		return nestedFieldBooleanClause.getClause();
	}

	private void _assertPicklistBooleanQuery(
		BooleanClauseOccur expectedBooleanClauseOccur, Query query,
		String... expectedValues) {

		Assert.assertTrue(query.toString(), query instanceof BooleanQuery);

		BooleanQuery booleanQuery = (BooleanQuery)query;

		List<BooleanClause<Query>> nestedFieldBooleanClauses =
			booleanQuery.clauses();

		Assert.assertEquals(
			nestedFieldBooleanClauses.toString(), expectedValues.length,
			nestedFieldBooleanClauses.size());

		for (int i = 0; i < expectedValues.length; i++) {
			BooleanClause<Query> booleanClause = nestedFieldBooleanClauses.get(
				i);

			Assert.assertEquals(
				expectedBooleanClauseOccur,
				booleanClause.getBooleanClauseOccur());

			_assertTermQuery(
				"nestedFieldArray.value_keyword", expectedValues[i],
				booleanClause.getClause());
		}
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
		Assert.assertEquals(expectedLowerTerm, termRangeQuery.getLowerTerm());
		Assert.assertEquals(expectedUpperTerm, termRangeQuery.getUpperTerm());
		Assert.assertEquals(
			expectedIncludesLower, termRangeQuery.includesLower());
		Assert.assertEquals(
			expectedIncludesUpper, termRangeQuery.includesUpper());
	}

	private void _assertWildcardQuery(
		String expectedField, String expectedValue, Query query) {

		Assert.assertTrue(query.toString(), query instanceof WildcardQuery);

		WildcardQuery wildcardQuery = (WildcardQuery)query;

		QueryTerm queryTerm = wildcardQuery.getQueryTerm();

		Assert.assertEquals(expectedField, queryTerm.getField());
		Assert.assertEquals(expectedValue, queryTerm.getValue());
	}

	private JSONObject _getCommonFieldFilterJSONObject(
		String operatorName, String propertyName, JSONArray valueJSONArray) {

		return JSONUtil.put(
			"operatorName", operatorName
		).put(
			"propertyName", propertyName
		).put(
			"value", valueJSONArray
		);
	}

	private JSONObject _getCommonFieldFilterJSONObject(
		String operatorName, String propertyName, String value) {

		return JSONUtil.put(
			"operatorName", operatorName
		).put(
			"propertyName", propertyName
		).put(
			"value", value
		);
	}

	private JSONObject _getFilterJSONObject(
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

	private JSONObject _getFilterJSONObject(
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

	private JSONObject _getPicklistValueJSONObject(String value) {
		return JSONUtil.put(
			"label", value
		).put(
			"value", value
		);
	}

	private String _resolveRelativeDateLowerTerm(
		String propertyName, String value) {

		Query query = _assertNestedQuery(
			BooleanClauseOccur.MUST,
			_getFilterJSONObject("ge", propertyName, value), propertyName);

		Assert.assertTrue(query.toString(), query instanceof TermRangeQuery);

		TermRangeQuery termRangeQuery = (TermRangeQuery)query;

		return termRangeQuery.getLowerTerm();
	}

	private void _setUpKeywordTextObjectField(String name) {
		ObjectField objectField = _setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, name);

		Mockito.when(
			objectField.isIndexedAsKeyword()
		).thenReturn(
			true
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

	private void _setUpPicklistObjectField(String fieldName) {
		ObjectField objectField = _setUpObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_PICKLIST,
			ObjectFieldConstants.DB_TYPE_STRING, fieldName);

		Mockito.when(
			objectField.getListTypeDefinitionId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			objectField.isIndexedAsKeyword()
		).thenReturn(
			true
		);
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