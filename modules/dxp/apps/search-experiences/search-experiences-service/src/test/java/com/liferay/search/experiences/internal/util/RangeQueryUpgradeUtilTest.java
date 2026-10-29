/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rodrigo Guedes de Souza
 */
public class RangeQueryUpgradeUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testUpgradeRangeQueries() {
		JSONObject fieldJSONObject = JSONUtil.put(
			"format", "yyyyMMddHHmmss"
		).put(
			"from", "${configuration.start_date}"
		).put(
			"include_lower", true
		).put(
			"include_upper", true
		).put(
			"time_zone", "UTC"
		).put(
			"to", "${configuration.end_date}"
		);

		Assert.assertTrue(
			RangeQueryUpgradeUtil.upgradeRangeQueries(
				_createRangeQueryJSONObject(fieldJSONObject)));

		_assertRangeQueryParameters(
			JSONUtil.put(
				"format", "yyyyMMddHHmmss"
			).put(
				"gte", "${configuration.start_date}"
			).put(
				"lte", "${configuration.end_date}"
			).put(
				"time_zone", "UTC"
			),
			fieldJSONObject);
	}

	@Test
	public void testUpgradeRangeQueriesWithExclusiveBounds() {
		JSONObject fieldJSONObject = JSONUtil.put(
			"from", 10
		).put(
			"include_lower", false
		).put(
			"include_upper", false
		).put(
			"to", 20
		);

		Assert.assertTrue(
			RangeQueryUpgradeUtil.upgradeRangeQueries(
				_createRangeQueryJSONObject(fieldJSONObject)));

		_assertRangeQueryParameters(
			JSONUtil.put(
				"gt", 10
			).put(
				"lt", 20
			),
			fieldJSONObject);
	}

	@Test
	public void testUpgradeRangeQueriesWithExistingOperators() {
		JSONObject fieldJSONObject = JSONUtil.put(
			"from", "20100101000000"
		).put(
			"gt", "20200101000000"
		).put(
			"lte", "20300101000000"
		).put(
			"to", "20400101000000"
		);

		Assert.assertTrue(
			RangeQueryUpgradeUtil.upgradeRangeQueries(
				_createRangeQueryJSONObject(fieldJSONObject)));

		_assertRangeQueryParameters(
			JSONUtil.put(
				"gt", "20200101000000"
			).put(
				"lte", "20300101000000"
			),
			fieldJSONObject);
	}

	@Test
	public void testUpgradeRangeQueriesWithJSONString() throws Exception {
		JSONObject jsonObject = JSONUtil.put(
			"occur", "${configuration.occur}"
		).put(
			"queries",
			JSONUtil.putAll(
				_createRangeQueryJSONObject(JSONUtil.put("from", 1))
			).toString()
		).put(
			"query",
			JSONUtil.put(
				"range",
				JSONUtil.put(
					"createDate",
					JSONUtil.put(
						"from", 1
					).put(
						"to", 2
					))
			).toString()
		).put(
			"text", "{not json"
		);

		Assert.assertTrue(
			RangeQueryUpgradeUtil.upgradeRangeQueries(jsonObject));

		Assert.assertEquals(
			"${configuration.occur}", jsonObject.getString("occur"));
		Assert.assertEquals(
			JSONUtil.putAll(
				_createRangeQueryJSONObject(JSONUtil.put("gte", 1))
			).toString(),
			jsonObject.getString("queries"));

		_assertRangeQueryParameters(
			JSONUtil.put(
				"gte", 1
			).put(
				"lte", 2
			),
			JSONUtil.getValueAsJSONObject(
				JSONFactoryUtil.createJSONObject(jsonObject.getString("query")),
				"JSONObject/range", "JSONObject/createDate"));

		Assert.assertEquals("{not json", jsonObject.getString("text"));
	}

	@Test
	public void testUpgradeRangeQueriesWithNestedRangeQueries() {
		JSONObject fieldJSONObject1 = JSONUtil.put(
			"from", "-9223372036854775808"
		).put(
			"include_lower", true
		).put(
			"include_upper", true
		).put(
			"to", "${time.current_date|date_format=timestamp}"
		);
		JSONObject fieldJSONObject2 = JSONUtil.put(
			"from", "${time.current_date|date_format=timestamp}"
		).put(
			"include_lower", true
		).put(
			"include_upper", true
		).put(
			"to", "9223372036854775807"
		);

		Assert.assertTrue(
			RangeQueryUpgradeUtil.upgradeRangeQueries(
				JSONUtil.put(
					"bool",
					JSONUtil.put(
						"should",
						JSONUtil.putAll(
							JSONUtil.put(
								"bool",
								JSONUtil.put(
									"must",
									JSONUtil.putAll(
										JSONUtil.put(
											"range",
											JSONUtil.put(
												"displayDate_sortable",
												fieldJSONObject1)),
										JSONUtil.put(
											"range",
											JSONUtil.put(
												"expirationDate_sortable",
												fieldJSONObject2))))))))));

		_assertRangeQueryParameters(
			JSONUtil.put(
				"gte", "${time.current_date|date_format=timestamp}"
			).put(
				"lte", "9223372036854775807"
			),
			fieldJSONObject2);

		_assertRangeQueryParameters(
			JSONUtil.put(
				"gte", "-9223372036854775808"
			).put(
				"lte", "${time.current_date|date_format=timestamp}"
			),
			fieldJSONObject1);
	}

	@Test
	public void testUpgradeRangeQueriesWithNullBound() throws Exception {
		JSONObject fieldJSONObject = JSONFactoryUtil.createJSONObject(
			"{\"from\": null, \"to\": \"20300101000000\"}");

		Assert.assertTrue(
			RangeQueryUpgradeUtil.upgradeRangeQueries(
				_createRangeQueryJSONObject(fieldJSONObject)));

		_assertRangeQueryParameters(
			JSONUtil.put("lte", "20300101000000"), fieldJSONObject);
	}

	@Test
	public void testUpgradeRangeQueriesWithString() {
		Assert.assertEquals(
			JSONUtil.putAll(
				_createRangeQueryJSONObject(JSONUtil.put("gt", 1))
			).toString(),
			RangeQueryUpgradeUtil.upgradeRangeQueries(
				JSONUtil.putAll(
					_createRangeQueryJSONObject(
						JSONUtil.put(
							"from", 1
						).put(
							"include_lower", false
						))
				).toString()));
		Assert.assertEquals(
			_createRangeQueryJSONObject(
				JSONUtil.put(
					"gte", 1
				).put(
					"lte", 2
				)
			).toString(),
			RangeQueryUpgradeUtil.upgradeRangeQueries(
				_createRangeQueryJSONObject(
					JSONUtil.put(
						"from", 1
					).put(
						"to", 2
					)
				).toString()));

		String json = _createRangeQueryJSONObject(
			JSONUtil.put("gte", 1)
		).toString();

		Assert.assertSame(
			json, RangeQueryUpgradeUtil.upgradeRangeQueries(json));

		json = StringUtil.removeLast(
			_createRangeQueryJSONObject(
				JSONUtil.put("from", 1)
			).toString(),
			StringPool.CLOSE_CURLY_BRACE);

		Assert.assertSame(
			json, RangeQueryUpgradeUtil.upgradeRangeQueries(json));

		json = StringUtil.removeLast(
			JSONUtil.putAll(
				_createRangeQueryJSONObject(JSONUtil.put("from", 1))
			).toString(),
			StringPool.CLOSE_BRACKET);

		Assert.assertSame(
			json, RangeQueryUpgradeUtil.upgradeRangeQueries(json));

		json = RandomTestUtil.randomString();

		Assert.assertSame(
			json, RangeQueryUpgradeUtil.upgradeRangeQueries(json));

		Assert.assertNull(
			RangeQueryUpgradeUtil.upgradeRangeQueries((String)null));
		Assert.assertSame(
			StringPool.BLANK,
			RangeQueryUpgradeUtil.upgradeRangeQueries(StringPool.BLANK));
	}

	@Test
	public void testUpgradeRangeQueriesWithoutLegacyParameters() {
		JSONObject fieldJSONObject = JSONUtil.put(
			"gte", "20200101000000"
		).put(
			"lt", "20300101000000"
		);

		Assert.assertFalse(
			RangeQueryUpgradeUtil.upgradeRangeQueries(
				JSONUtil.putAll(_createRangeQueryJSONObject(fieldJSONObject))));

		_assertRangeQueryParameters(
			JSONUtil.put(
				"gte", "20200101000000"
			).put(
				"lt", "20300101000000"
			),
			fieldJSONObject);

		JSONArray rangesJSONArray = JSONUtil.putAll(
			JSONUtil.put(
				"from", 0
			).put(
				"to", 100
			),
			JSONUtil.put("from", 100));

		Assert.assertFalse(
			RangeQueryUpgradeUtil.upgradeRangeQueries(
				JSONUtil.put(
					"price_ranges",
					JSONUtil.put(
						"range",
						JSONUtil.put(
							"field", "price"
						).put(
							"ranges", rangesJSONArray
						)))));

		_assertRangeQueryParameters(
			JSONUtil.put(
				"from", 0
			).put(
				"to", 100
			),
			rangesJSONArray.getJSONObject(0));
	}

	private void _assertRangeQueryParameters(
		JSONObject expectedJSONObject, JSONObject fieldJSONObject) {

		Assert.assertEquals(
			fieldJSONObject.toString(), expectedJSONObject.keySet(),
			fieldJSONObject.keySet());

		for (String key : expectedJSONObject.keySet()) {
			Assert.assertEquals(
				key, expectedJSONObject.get(key), fieldJSONObject.get(key));
		}
	}

	private JSONObject _createRangeQueryJSONObject(JSONObject fieldJSONObject) {
		return JSONUtil.put(
			"range", JSONUtil.put("createDate", fieldJSONObject));
	}

}