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

	private void _assertName(int index, JSONArray jsonArray, String name) {
		JSONObject jsonObject = jsonArray.getJSONObject(index);

		Assert.assertEquals(name, jsonObject.getString("name"));
	}

	private JSONObject _createFrontendTokenCategoryJSONObject(
		JSONArray frontendTokenSetsJSONArray, String name) {

		return JSONUtil.put(
			"frontendTokenSets", frontendTokenSetsJSONArray
		).put(
			"name", name
		);
	}

	private JSONObject _createFrontendTokenCategoryJSONObject(
		String categoryName, JSONObject frontendTokenJSONObject,
		String setName) {

		return _createFrontendTokenCategoryJSONObject(
			JSONUtil.putAll(
				_createFrontendTokenSetJSONObject(
					frontendTokenJSONObject, setName)),
			categoryName);
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
		String categoryName, JSONObject frontendTokenJSONObject,
		String setName) {

		return _createFrontendTokenDefinitionJSONObject(
			JSONUtil.putAll(
				_createFrontendTokenCategoryJSONObject(
					categoryName, frontendTokenJSONObject, setName)));
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
		JSONArray frontendTokensJSONArray, String name) {

		return JSONUtil.put(
			"frontendTokens", frontendTokensJSONArray
		).put(
			"name", name
		);
	}

	private JSONObject _createFrontendTokenSetJSONObject(
		JSONObject frontendTokenJSONObject, String name) {

		return _createFrontendTokenSetJSONObject(
			JSONUtil.putAll(frontendTokenJSONObject), name);
	}

	private JSONArray _getFrontendTokenSetsJSONArray(
		int categoryIndex, JSONObject frontendTokenDefinitionJSONObject) {

		JSONArray frontendTokenCategoriesJSONArray =
			frontendTokenDefinitionJSONObject.getJSONArray(
				"frontendTokenCategories");

		JSONObject frontendTokenCategoryJSONObject =
			frontendTokenCategoriesJSONArray.getJSONObject(categoryIndex);

		return frontendTokenCategoryJSONObject.getJSONArray(
			"frontendTokenSets");
	}

	private JSONArray _getFrontendTokensJSONArray(
		int categoryIndex, JSONObject frontendTokenDefinitionJSONObject,
		int setIndex) {

		JSONArray frontendTokenSetsJSONArray = _getFrontendTokenSetsJSONArray(
			categoryIndex, frontendTokenDefinitionJSONObject);

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
						categoryName,
						_createFrontendTokenJSONObject(baseTokenName),
						setName)));

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				categoryName, _createFrontendTokenJSONObject(overrideTokenName),
				setName);

		JSONArray frontendTokensJSONArray = _getFrontendTokensJSONArray(
			1,
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject),
			0);

		Assert.assertEquals(
			frontendTokensJSONArray.toString(), 2,
			frontendTokensJSONArray.length());

		_assertName(1, frontendTokensJSONArray, overrideTokenName);
	}

	private void _testMergeFrontendTokenDefinitionJSONObjectWithInvalidOverrideToken() {
		String baseTokenName = RandomTestUtil.randomString();
		String categoryName = RandomTestUtil.randomString();
		String overrideTokenName = RandomTestUtil.randomString();
		String setName = RandomTestUtil.randomString();

		JSONObject frontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				categoryName, _createFrontendTokenJSONObject(baseTokenName),
				setName);

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				JSONUtil.putAll(
					_createFrontendTokenCategoryJSONObject(
						JSONUtil.putAll(
							_createFrontendTokenSetJSONObject(
								JSONUtil.putAll(
									_INVALID_JSON_OBJECT,
									_createFrontendTokenJSONObject(
										overrideTokenName)),
								setName)),
						categoryName)));

		JSONArray frontendTokensJSONArray = _getFrontendTokensJSONArray(
			0,
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject),
			0);

		Assert.assertEquals(
			frontendTokensJSONArray.toString(), 2,
			frontendTokensJSONArray.length());

		_assertName(0, frontendTokensJSONArray, baseTokenName);

		_assertName(1, frontendTokensJSONArray, overrideTokenName);
	}

	private void _testMergeFrontendTokenDefinitionJSONObjectWithNewToken() {
		String baseTokenName = RandomTestUtil.randomString();
		String categoryName = RandomTestUtil.randomString();
		String overrideTokenName = RandomTestUtil.randomString();
		String setName = RandomTestUtil.randomString();

		JSONObject frontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				categoryName, _createFrontendTokenJSONObject(baseTokenName),
				setName);

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				categoryName, _createFrontendTokenJSONObject(overrideTokenName),
				setName);

		JSONArray frontendTokensJSONArray = _getFrontendTokensJSONArray(
			0,
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject),
			0);

		Assert.assertEquals(
			frontendTokensJSONArray.toString(), 2,
			frontendTokensJSONArray.length());

		_assertName(0, frontendTokensJSONArray, baseTokenName);

		_assertName(1, frontendTokensJSONArray, overrideTokenName);

		JSONArray originalFrontendTokensJSONArray = _getFrontendTokensJSONArray(
			0, frontendTokenDefinitionJSONObject, 0);

		Assert.assertEquals(
			originalFrontendTokensJSONArray.toString(), 1,
			originalFrontendTokensJSONArray.length());
	}

	private void _testMergeFrontendTokenDefinitionJSONObjectWithNullOverrideDefinition() {
		JSONObject frontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				RandomTestUtil.randomString(),
				_createFrontendTokenJSONObject(RandomTestUtil.randomString()),
				RandomTestUtil.randomString());

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
						JSONUtil.putAll(
							_createFrontendTokenSetJSONObject(
								JSONUtil.putAll(
									_createFrontendTokenJSONObject(
										otherTokenName),
									_createFrontendTokenJSONObject(
										targetTokenName, baseType)),
								setName)),
						categoryName)));

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				categoryName,
				_createFrontendTokenJSONObject(targetTokenName, overrideType),
				setName);

		JSONArray frontendTokensJSONArray = _getFrontendTokensJSONArray(
			0,
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject),
			0);

		Assert.assertEquals(
			frontendTokensJSONArray.toString(), 2,
			frontendTokensJSONArray.length());

		_assertName(0, frontendTokensJSONArray, otherTokenName);

		_assertName(1, frontendTokensJSONArray, targetTokenName);

		JSONObject targetFrontendTokenJSONObject =
			frontendTokensJSONArray.getJSONObject(1);

		Assert.assertEquals(
			overrideType, targetFrontendTokenJSONObject.getString("type"));

		JSONArray originalFrontendTokensJSONArray = _getFrontendTokensJSONArray(
			0, frontendTokenDefinitionJSONObject, 0);

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
				baseCategoryName, _createFrontendTokenJSONObject(baseTokenName),
				RandomTestUtil.randomString());

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				overrideCategoryName,
				_createFrontendTokenJSONObject(overrideTokenName),
				RandomTestUtil.randomString());

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

		_assertName(0, frontendTokenCategoriesJSONArray, baseCategoryName);

		_assertName(1, frontendTokenCategoriesJSONArray, overrideCategoryName);

		_assertName(
			0,
			_getFrontendTokensJSONArray(
				1, mergedFrontendTokenDefinitionJSONObject, 0),
			overrideTokenName);
	}

	private void _testMergeFrontendTokenDefinitionJSONObjectWithUnknownSetName() {
		String baseSetName = RandomTestUtil.randomString();
		String baseTokenName = RandomTestUtil.randomString();
		String categoryName = RandomTestUtil.randomString();
		String overrideSetName = RandomTestUtil.randomString();
		String overrideTokenName = RandomTestUtil.randomString();

		JSONObject frontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				categoryName, _createFrontendTokenJSONObject(baseTokenName),
				baseSetName);

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				categoryName, _createFrontendTokenJSONObject(overrideTokenName),
				overrideSetName);

		JSONArray frontendTokenSetsJSONArray = _getFrontendTokenSetsJSONArray(
			0,
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject));

		Assert.assertEquals(
			frontendTokenSetsJSONArray.toString(), 2,
			frontendTokenSetsJSONArray.length());

		JSONObject baseFrontendTokenSetJSONObject =
			frontendTokenSetsJSONArray.getJSONObject(0);

		_assertName(0, frontendTokenSetsJSONArray, baseSetName);

		_assertName(
			0, baseFrontendTokenSetJSONObject.getJSONArray("frontendTokens"),
			baseTokenName);

		JSONObject overrideFrontendTokenSetJSONObject =
			frontendTokenSetsJSONArray.getJSONObject(1);

		_assertName(1, frontendTokenSetsJSONArray, overrideSetName);

		_assertName(
			0,
			overrideFrontendTokenSetJSONObject.getJSONArray("frontendTokens"),
			overrideTokenName);
	}

	private static final String _INVALID_JSON_OBJECT = "not-an-object";

}