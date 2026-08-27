/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.util;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Gabriel Lima
 */
public class FrontendTokenDefinitionUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetFrontendTokenNames() {
		_testGetFrontendTokenNamesWithBlankDefinition();
		_testGetFrontendTokenNamesWithDuplicateName();
		_testGetFrontendTokenNamesWithInvalidCategories();
		_testGetFrontendTokenNamesWithInvalidSetsAndTokens();
		_testGetFrontendTokenNamesWithMalformedDefinition();
		_testGetFrontendTokenNamesWithValidDefinition();
	}

	@Test
	public void testMergeFrontendTokenDefinitionJSONObject() {
		_testMergeFrontendTokenDefinitionJSONObjectWithInvalidBaseCategory();
		_testMergeFrontendTokenDefinitionJSONObjectWithInvalidOverrideToken();
		_testMergeFrontendTokenDefinitionJSONObjectWithNewToken();
		_testMergeFrontendTokenDefinitionJSONObjectWithNullOverrideDefinition();
		_testMergeFrontendTokenDefinitionJSONObjectWithReplacedToken();
		_testMergeFrontendTokenDefinitionJSONObjectWithUnknownCategoryName();
		_testMergeFrontendTokenDefinitionJSONObjectWithUnknownSetName();
	}

	private void _assertName(String name, JSONArray jsonArray, int index) {
		JSONObject jsonObject = jsonArray.getJSONObject(index);

		Assert.assertEquals(name, jsonObject.getString("name"));
	}

	private JSONObject _createFrontendTokenCategoryJSONObject(
		String name, JSONArray frontendTokenSetsJSONArray) {

		return JSONUtil.put(
			"frontendTokenSets", frontendTokenSetsJSONArray
		).put(
			"name", name
		);
	}

	private JSONObject _createFrontendTokenCategoryJSONObject(
		String categoryName, String setName,
		JSONObject frontendTokenJSONObject) {

		return _createFrontendTokenCategoryJSONObject(
			categoryName,
			JSONUtil.putAll(
				_createFrontendTokenSetJSONObject(
					setName, frontendTokenJSONObject)));
	}

	private String _createFrontendTokenDefinitionJSON(
		JSONArray frontendTokenSetsJSONArray) {

		return JSONUtil.put(
			"frontendTokenCategories",
			JSONUtil.putAll(
				JSONUtil.put(
					"frontendTokenSets", frontendTokenSetsJSONArray
				).put(
					"name", RandomTestUtil.randomString()
				))
		).toString();
	}

	private JSONObject _createFrontendTokenDefinitionJSONObject(
		JSONArray frontendTokenCategoriesJSONArray) {

		return JSONUtil.put(
			"frontendTokenCategories", frontendTokenCategoriesJSONArray);
	}

	private JSONObject _createFrontendTokenDefinitionJSONObject(
		String categoryName, String setName,
		JSONObject frontendTokenJSONObject) {

		return _createFrontendTokenDefinitionJSONObject(
			JSONUtil.putAll(
				_createFrontendTokenCategoryJSONObject(
					categoryName, setName, frontendTokenJSONObject)));
	}

	private JSONObject _createFrontendTokenJSONObject(String name) {
		return _createFrontendTokenJSONObject(
			name, RandomTestUtil.randomString());
	}

	private JSONObject _createFrontendTokenJSONObject(
		String name, String type) {

		return JSONUtil.put(
			"name", name
		).put(
			"type", type
		);
	}

	private JSONObject _createFrontendTokenSetJSONObject(
		String name, JSONArray frontendTokensJSONArray) {

		return JSONUtil.put(
			"frontendTokens", frontendTokensJSONArray
		).put(
			"name", name
		);
	}

	private JSONObject _createFrontendTokenSetJSONObject(
		String name, JSONObject frontendTokenJSONObject) {

		return _createFrontendTokenSetJSONObject(
			name, JSONUtil.putAll(frontendTokenJSONObject));
	}

	private JSONArray _getFrontendTokenSetsJSONArray(
		JSONObject frontendTokenDefinitionJSONObject, int categoryIndex) {

		JSONArray frontendTokenCategoriesJSONArray =
			frontendTokenDefinitionJSONObject.getJSONArray(
				"frontendTokenCategories");

		JSONObject frontendTokenCategoryJSONObject =
			frontendTokenCategoriesJSONArray.getJSONObject(categoryIndex);

		return frontendTokenCategoryJSONObject.getJSONArray(
			"frontendTokenSets");
	}

	private JSONArray _getFrontendTokensJSONArray(
		JSONObject frontendTokenDefinitionJSONObject, int categoryIndex,
		int setIndex) {

		JSONArray frontendTokenSetsJSONArray = _getFrontendTokenSetsJSONArray(
			frontendTokenDefinitionJSONObject, categoryIndex);

		JSONObject frontendTokenSetJSONObject =
			frontendTokenSetsJSONArray.getJSONObject(setIndex);

		return frontendTokenSetJSONObject.getJSONArray("frontendTokens");
	}

	private void _testGetFrontendTokenNamesWithBlankDefinition() {
		List<String> frontendTokenNames =
			FrontendTokenDefinitionUtil.getFrontendTokenNames(null);

		Assert.assertTrue(frontendTokenNames.isEmpty());

		frontendTokenNames = FrontendTokenDefinitionUtil.getFrontendTokenNames(
			"");

		Assert.assertTrue(frontendTokenNames.isEmpty());
	}

	private void _testGetFrontendTokenNamesWithDuplicateName() {
		String frontendTokenName = "customPrimaryColor";

		List<String> frontendTokenNames =
			FrontendTokenDefinitionUtil.getFrontendTokenNames(
				_createFrontendTokenDefinitionJSON(
					JSONUtil.putAll(
						JSONUtil.put(
							"frontendTokens",
							JSONUtil.putAll(
								JSONUtil.put("name", frontendTokenName))
						).put(
							"name", RandomTestUtil.randomString()
						),
						JSONUtil.put(
							"frontendTokens",
							JSONUtil.putAll(
								JSONUtil.put("name", frontendTokenName))
						).put(
							"name", RandomTestUtil.randomString()
						))));

		Assert.assertEquals(
			frontendTokenNames.toString(), 2, frontendTokenNames.size());
		Assert.assertEquals(frontendTokenName, frontendTokenNames.get(0));
		Assert.assertEquals(frontendTokenName, frontendTokenNames.get(1));
	}

	private void _testGetFrontendTokenNamesWithInvalidCategories() {
		List<String> frontendTokenNames =
			FrontendTokenDefinitionUtil.getFrontendTokenNames(
				JSONUtil.put(
					"frontendTokenCategories",
					JSONUtil.putAll(_INVALID_JSON_OBJECT)
				).toString());

		Assert.assertTrue(frontendTokenNames.isEmpty());
	}

	private void _testGetFrontendTokenNamesWithInvalidSetsAndTokens() {
		List<String> frontendTokenNames =
			FrontendTokenDefinitionUtil.getFrontendTokenNames(
				_createFrontendTokenDefinitionJSON(
					JSONUtil.putAll(
						_INVALID_JSON_OBJECT,
						JSONUtil.put(
							"frontendTokens",
							JSONUtil.putAll(
								_INVALID_JSON_OBJECT,
								JSONUtil.put("name", "customPrimaryColor"))
						).put(
							"name", RandomTestUtil.randomString()
						))));

		Assert.assertEquals(
			frontendTokenNames.toString(), 1, frontendTokenNames.size());
		Assert.assertTrue(frontendTokenNames.contains("customPrimaryColor"));
	}

	private void _testGetFrontendTokenNamesWithMalformedDefinition() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				FrontendTokenDefinitionUtil.class.getName(),
				LoggerTestUtil.WARN)) {

			List<String> frontendTokenNames =
				FrontendTokenDefinitionUtil.getFrontendTokenNames(
					"{not valid json");

			Assert.assertTrue(frontendTokenNames.isEmpty());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to parse frontend token definition",
				logEntry.getMessage());
		}
	}

	private void _testGetFrontendTokenNamesWithValidDefinition() {
		List<String> frontendTokenNames =
			FrontendTokenDefinitionUtil.getFrontendTokenNames(
				_createFrontendTokenDefinitionJSON(
					JSONUtil.putAll(
						JSONUtil.put(
							"frontendTokens",
							JSONUtil.putAll(
								JSONUtil.put("name", "customPrimaryColor"),
								JSONUtil.put("name", "customSecondaryColor"))
						).put(
							"name", RandomTestUtil.randomString()
						),
						JSONUtil.put(
							"frontendTokens",
							JSONUtil.putAll(
								JSONUtil.put("name", "customHeadingColor"))
						).put(
							"name", RandomTestUtil.randomString()
						))));

		Assert.assertTrue(frontendTokenNames.contains("customPrimaryColor"));
		Assert.assertTrue(frontendTokenNames.contains("customSecondaryColor"));
		Assert.assertTrue(frontendTokenNames.contains("customHeadingColor"));
		Assert.assertEquals(
			frontendTokenNames.toString(), 3, frontendTokenNames.size());
	}

	private void _testMergeFrontendTokenDefinitionJSONObjectWithInvalidBaseCategory() {
		String baseTokenName = RandomTestUtil.randomString();
		String categoryName = RandomTestUtil.randomString();
		String overrideTokenName = RandomTestUtil.randomString();
		String setName = RandomTestUtil.randomString();

		JSONObject frontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				JSONUtil.putAll(
					_INVALID_JSON_OBJECT,
					_createFrontendTokenCategoryJSONObject(
						categoryName, setName,
						_createFrontendTokenJSONObject(baseTokenName))));

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				categoryName, setName,
				_createFrontendTokenJSONObject(overrideTokenName));

		JSONArray frontendTokensJSONArray = _getFrontendTokensJSONArray(
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject),
			1, 0);

		Assert.assertEquals(
			frontendTokensJSONArray.toString(), 2,
			frontendTokensJSONArray.length());

		_assertName(overrideTokenName, frontendTokensJSONArray, 1);
	}

	private void _testMergeFrontendTokenDefinitionJSONObjectWithInvalidOverrideToken() {
		String baseTokenName = RandomTestUtil.randomString();
		String categoryName = RandomTestUtil.randomString();
		String overrideTokenName = RandomTestUtil.randomString();
		String setName = RandomTestUtil.randomString();

		JSONObject frontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				categoryName, setName,
				_createFrontendTokenJSONObject(baseTokenName));

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				JSONUtil.putAll(
					_createFrontendTokenCategoryJSONObject(
						categoryName,
						JSONUtil.putAll(
							_createFrontendTokenSetJSONObject(
								setName,
								JSONUtil.putAll(
									_INVALID_JSON_OBJECT,
									_createFrontendTokenJSONObject(
										overrideTokenName)))))));

		JSONArray frontendTokensJSONArray = _getFrontendTokensJSONArray(
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject),
			0, 0);

		Assert.assertEquals(
			frontendTokensJSONArray.toString(), 2,
			frontendTokensJSONArray.length());

		_assertName(baseTokenName, frontendTokensJSONArray, 0);

		_assertName(overrideTokenName, frontendTokensJSONArray, 1);
	}

	private void _testMergeFrontendTokenDefinitionJSONObjectWithNewToken() {
		String baseTokenName = RandomTestUtil.randomString();
		String categoryName = RandomTestUtil.randomString();
		String overrideTokenName = RandomTestUtil.randomString();
		String setName = RandomTestUtil.randomString();

		JSONObject frontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				categoryName, setName,
				_createFrontendTokenJSONObject(baseTokenName));

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				categoryName, setName,
				_createFrontendTokenJSONObject(overrideTokenName));

		JSONArray frontendTokensJSONArray = _getFrontendTokensJSONArray(
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject),
			0, 0);

		Assert.assertEquals(
			frontendTokensJSONArray.toString(), 2,
			frontendTokensJSONArray.length());

		_assertName(baseTokenName, frontendTokensJSONArray, 0);

		_assertName(overrideTokenName, frontendTokensJSONArray, 1);

		JSONArray originalFrontendTokensJSONArray = _getFrontendTokensJSONArray(
			frontendTokenDefinitionJSONObject, 0, 0);

		Assert.assertEquals(
			originalFrontendTokensJSONArray.toString(), 1,
			originalFrontendTokensJSONArray.length());
	}

	private void _testMergeFrontendTokenDefinitionJSONObjectWithNullOverrideDefinition() {
		JSONObject frontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_createFrontendTokenJSONObject(RandomTestUtil.randomString()));

		for (JSONObject overrideFrontendTokenDefinitionJSONObject :
				Arrays.asList(
					null, JSONUtil.put("name", RandomTestUtil.randomString()),
					_createFrontendTokenDefinitionJSONObject(
						JSONUtil.putAll()))) {

			Assert.assertSame(
				frontendTokenDefinitionJSONObject,
				FrontendTokenDefinitionUtil.
					mergeFrontendTokenDefinitionJSONObject(
						frontendTokenDefinitionJSONObject,
						overrideFrontendTokenDefinitionJSONObject));
		}
	}

	private void _testMergeFrontendTokenDefinitionJSONObjectWithReplacedToken() {
		String baseType = RandomTestUtil.randomString();
		String categoryName = RandomTestUtil.randomString();
		String overrideType = RandomTestUtil.randomString();
		String otherTokenName = RandomTestUtil.randomString();
		String setName = RandomTestUtil.randomString();
		String targetTokenName = RandomTestUtil.randomString();

		JSONObject frontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				JSONUtil.putAll(
					_createFrontendTokenCategoryJSONObject(
						categoryName,
						JSONUtil.putAll(
							_createFrontendTokenSetJSONObject(
								setName,
								JSONUtil.putAll(
									_createFrontendTokenJSONObject(
										otherTokenName),
									_createFrontendTokenJSONObject(
										targetTokenName, baseType)))))));

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				categoryName, setName,
				_createFrontendTokenJSONObject(targetTokenName, overrideType));

		JSONArray frontendTokensJSONArray = _getFrontendTokensJSONArray(
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject),
			0, 0);

		Assert.assertEquals(
			frontendTokensJSONArray.toString(), 2,
			frontendTokensJSONArray.length());

		_assertName(otherTokenName, frontendTokensJSONArray, 0);

		_assertName(targetTokenName, frontendTokensJSONArray, 1);

		JSONObject targetFrontendTokenJSONObject =
			frontendTokensJSONArray.getJSONObject(1);

		Assert.assertEquals(
			overrideType, targetFrontendTokenJSONObject.getString("type"));

		JSONArray originalFrontendTokensJSONArray = _getFrontendTokensJSONArray(
			frontendTokenDefinitionJSONObject, 0, 0);

		Assert.assertEquals(
			originalFrontendTokensJSONArray.toString(), 2,
			originalFrontendTokensJSONArray.length());

		JSONObject originalTargetFrontendTokenJSONObject =
			originalFrontendTokensJSONArray.getJSONObject(1);

		Assert.assertEquals(
			baseType, originalTargetFrontendTokenJSONObject.getString("type"));
	}

	private void _testMergeFrontendTokenDefinitionJSONObjectWithUnknownCategoryName() {
		String baseCategoryName = RandomTestUtil.randomString();
		String baseTokenName = RandomTestUtil.randomString();
		String overrideCategoryName = RandomTestUtil.randomString();
		String overrideTokenName = RandomTestUtil.randomString();

		JSONObject frontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				baseCategoryName, RandomTestUtil.randomString(),
				_createFrontendTokenJSONObject(baseTokenName));

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				overrideCategoryName, RandomTestUtil.randomString(),
				_createFrontendTokenJSONObject(overrideTokenName));

		JSONObject mergedFrontendTokenDefinitionJSONObject =
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject);

		JSONArray frontendTokenCategoriesJSONArray =
			mergedFrontendTokenDefinitionJSONObject.getJSONArray(
				"frontendTokenCategories");

		Assert.assertEquals(
			frontendTokenCategoriesJSONArray.toString(), 2,
			frontendTokenCategoriesJSONArray.length());

		_assertName(baseCategoryName, frontendTokenCategoriesJSONArray, 0);

		_assertName(overrideCategoryName, frontendTokenCategoriesJSONArray, 1);

		_assertName(
			overrideTokenName,
			_getFrontendTokensJSONArray(
				mergedFrontendTokenDefinitionJSONObject, 1, 0),
			0);
	}

	private void _testMergeFrontendTokenDefinitionJSONObjectWithUnknownSetName() {
		String baseSetName = RandomTestUtil.randomString();
		String baseTokenName = RandomTestUtil.randomString();
		String categoryName = RandomTestUtil.randomString();
		String overrideSetName = RandomTestUtil.randomString();
		String overrideTokenName = RandomTestUtil.randomString();

		JSONObject frontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				categoryName, baseSetName,
				_createFrontendTokenJSONObject(baseTokenName));

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				categoryName, overrideSetName,
				_createFrontendTokenJSONObject(overrideTokenName));

		JSONArray frontendTokenSetsJSONArray = _getFrontendTokenSetsJSONArray(
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject),
			0);

		Assert.assertEquals(
			frontendTokenSetsJSONArray.toString(), 2,
			frontendTokenSetsJSONArray.length());

		JSONObject baseFrontendTokenSetJSONObject =
			frontendTokenSetsJSONArray.getJSONObject(0);

		_assertName(baseSetName, frontendTokenSetsJSONArray, 0);

		_assertName(
			baseTokenName,
			baseFrontendTokenSetJSONObject.getJSONArray("frontendTokens"), 0);

		JSONObject overrideFrontendTokenSetJSONObject =
			frontendTokenSetsJSONArray.getJSONObject(1);

		_assertName(overrideSetName, frontendTokenSetsJSONArray, 1);

		_assertName(
			overrideTokenName,
			overrideFrontendTokenSetJSONObject.getJSONArray("frontendTokens"),
			0);
	}

	private static final String _INVALID_JSON_OBJECT = "not-an-object";

}