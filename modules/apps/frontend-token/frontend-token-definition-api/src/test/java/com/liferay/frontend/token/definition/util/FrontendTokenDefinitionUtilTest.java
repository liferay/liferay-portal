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
		_testMergeFrontendTokenDefinitionJSONObjectWithBlankOverrideCategories();
		_testMergeFrontendTokenDefinitionJSONObjectWithReplacedToken();
		_testMergeFrontendTokenDefinitionJSONObjectWithUnknownCategoryName();
		_testMergeFrontendTokenDefinitionJSONObjectWithUnknownSetName();
	}

	private void _assertNames(JSONArray jsonArray, String... names) {
		Assert.assertEquals(
			jsonArray.toString(), names.length, jsonArray.length());

		for (int i = 0; i < names.length; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Assert.assertEquals(names[i], jsonObject.getString("name"));
		}
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

		JSONObject frontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				JSONUtil.putAll(
					_createFrontendTokenCategoryJSONObject(
						frontendTokenSetsJSONArray,
						RandomTestUtil.randomString())));

		return frontendTokenDefinitionJSONObject.toString();
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
						_createFrontendTokenSetJSONObject(
							_createFrontendTokenJSONObject(frontendTokenName),
							RandomTestUtil.randomString()),
						_createFrontendTokenSetJSONObject(
							_createFrontendTokenJSONObject(frontendTokenName),
							RandomTestUtil.randomString()))));

		Assert.assertEquals(
			frontendTokenNames.toString(), 2, frontendTokenNames.size());
		Assert.assertEquals(frontendTokenName, frontendTokenNames.get(0));
		Assert.assertEquals(frontendTokenName, frontendTokenNames.get(1));
	}

	private void _testGetFrontendTokenNamesWithInvalidCategories() {
		JSONObject frontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				JSONUtil.putAll(RandomTestUtil.randomString()));

		List<String> frontendTokenNames =
			FrontendTokenDefinitionUtil.getFrontendTokenNames(
				frontendTokenDefinitionJSONObject.toString());

		Assert.assertTrue(frontendTokenNames.isEmpty());
	}

	private void _testGetFrontendTokenNamesWithInvalidSetsAndTokens() {
		List<String> frontendTokenNames =
			FrontendTokenDefinitionUtil.getFrontendTokenNames(
				_createFrontendTokenDefinitionJSON(
					JSONUtil.putAll(
						RandomTestUtil.randomString(),
						_createFrontendTokenSetJSONObject(
							JSONUtil.putAll(
								RandomTestUtil.randomString(),
								_createFrontendTokenJSONObject(
									"customPrimaryColor")),
							RandomTestUtil.randomString()))));

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
						_createFrontendTokenSetJSONObject(
							JSONUtil.putAll(
								_createFrontendTokenJSONObject(
									"customPrimaryColor"),
								_createFrontendTokenJSONObject(
									"customSecondaryColor")),
							RandomTestUtil.randomString()),
						_createFrontendTokenSetJSONObject(
							_createFrontendTokenJSONObject(
								"customHeadingColor"),
							RandomTestUtil.randomString()))));

		Assert.assertEquals(
			frontendTokenNames.toString(), 3, frontendTokenNames.size());
		Assert.assertTrue(frontendTokenNames.contains("customPrimaryColor"));
		Assert.assertTrue(frontendTokenNames.contains("customSecondaryColor"));
		Assert.assertTrue(frontendTokenNames.contains("customHeadingColor"));
	}

	private void _testMergeFrontendTokenDefinitionJSONObjectWithBlankOverrideCategories() {
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

	private void _testMergeFrontendTokenDefinitionJSONObjectWithInvalidBaseCategory() {
		String baseTokenName = RandomTestUtil.randomString();
		String categoryName = RandomTestUtil.randomString();
		String overrideTokenName = RandomTestUtil.randomString();
		String setName = RandomTestUtil.randomString();

		JSONObject frontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				JSONUtil.putAll(
					RandomTestUtil.randomString(),
					_createFrontendTokenCategoryJSONObject(
						categoryName,
						_createFrontendTokenJSONObject(baseTokenName),
						setName)));

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				categoryName, _createFrontendTokenJSONObject(overrideTokenName),
				setName);

		JSONObject mergedFrontendTokenDefinitionJSONObject =
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject);

		_assertNames(
			_getFrontendTokensJSONArray(
				1, mergedFrontendTokenDefinitionJSONObject, 0),
			baseTokenName, overrideTokenName);
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
									RandomTestUtil.randomString(),
									_createFrontendTokenJSONObject(
										overrideTokenName)),
								setName)),
						categoryName)));

		JSONObject mergedFrontendTokenDefinitionJSONObject =
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject);

		_assertNames(
			_getFrontendTokensJSONArray(
				0, mergedFrontendTokenDefinitionJSONObject, 0),
			baseTokenName, overrideTokenName);
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

		JSONObject mergedFrontendTokenDefinitionJSONObject =
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject);

		_assertNames(
			_getFrontendTokensJSONArray(
				0, mergedFrontendTokenDefinitionJSONObject, 0),
			baseTokenName, overrideTokenName);

		JSONArray originalFrontendTokensJSONArray = _getFrontendTokensJSONArray(
			0, frontendTokenDefinitionJSONObject, 0);

		_assertNames(originalFrontendTokensJSONArray, baseTokenName);
	}

	private void _testMergeFrontendTokenDefinitionJSONObjectWithReplacedToken() {
		String baseType = RandomTestUtil.randomString();
		String categoryName = RandomTestUtil.randomString();
		String otherTokenName = RandomTestUtil.randomString();
		String overrideType = RandomTestUtil.randomString();
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

		JSONObject mergedFrontendTokenDefinitionJSONObject =
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject);

		JSONArray frontendTokensJSONArray = _getFrontendTokensJSONArray(
			0, mergedFrontendTokenDefinitionJSONObject, 0);

		_assertNames(frontendTokensJSONArray, otherTokenName, targetTokenName);

		JSONObject targetFrontendTokenJSONObject =
			frontendTokensJSONArray.getJSONObject(1);

		Assert.assertEquals(
			overrideType, targetFrontendTokenJSONObject.getString("type"));

		JSONArray originalFrontendTokensJSONArray = _getFrontendTokensJSONArray(
			0, frontendTokenDefinitionJSONObject, 0);

		_assertNames(
			originalFrontendTokensJSONArray, otherTokenName, targetTokenName);

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

		_assertNames(
			frontendTokenCategoriesJSONArray, baseCategoryName,
			overrideCategoryName);

		_assertNames(
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

		JSONObject mergedFrontendTokenDefinitionJSONObject =
			FrontendTokenDefinitionUtil.mergeFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject);

		JSONArray frontendTokenSetsJSONArray = _getFrontendTokenSetsJSONArray(
			0, mergedFrontendTokenDefinitionJSONObject);

		_assertNames(frontendTokenSetsJSONArray, baseSetName, overrideSetName);

		JSONObject baseFrontendTokenSetJSONObject =
			frontendTokenSetsJSONArray.getJSONObject(0);

		_assertNames(
			baseFrontendTokenSetJSONObject.getJSONArray("frontendTokens"),
			baseTokenName);

		JSONObject overrideFrontendTokenSetJSONObject =
			frontendTokenSetsJSONArray.getJSONObject(1);

		_assertNames(
			overrideFrontendTokenSetJSONObject.getJSONArray("frontendTokens"),
			overrideTokenName);
	}

}