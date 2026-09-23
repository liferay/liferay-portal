/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.impl;

import com.liferay.frontend.token.definition.FrontendToken;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.style.book.exception.DuplicateStyleBookEntryFrontendTokenException;
import com.liferay.style.book.exception.StyleBookEntryFrontendTokenDefinitionException;
import com.liferay.style.book.exception.StyleBookEntryFrontendTokenException;
import com.liferay.style.book.exception.StyleBookEntryFrontendTokensValuesException;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.service.persistence.StyleBookEntryPersistence;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Anderson Luiz
 * @author Gabriel Lima
 * @author Thiago Buarque
 */
public class StyleBookEntryLocalServiceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		ReflectionTestUtil.setFieldValue(
			_styleBookEntryLocalService, "_jsonFactory", new JSONFactoryImpl());
	}

	@Test
	public void testAddFrontendToken() throws Exception {
		_testAddFrontendTokenWithBlankRequiredField();
		_testAddFrontendTokenWithDuplicateName();
		_testAddFrontendTokenWithExistingUnrelatedValue();
		_testAddFrontendTokenWithInvalidType();
		_testAddFrontendTokenWithMalformedFrontendTokenDefinition();
		_testAddFrontendTokenWithType();
	}

	@Test
	public void testGetStyleBookEntries() {
		_styleBookEntryLocalService.getStyleBookEntries(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString());

		Mockito.verify(
			_styleBookEntryPersistence
		).findByG_T_Head(
			Mockito.anyLong(), Mockito.anyString(), Mockito.eq(true)
		);
	}

	@Test
	public void testUpdateFrontendTokenDefinition() throws Exception {
		_testUpdateFrontendTokenDefinitionClearsFrontendTokenDefinition(null);
		_testUpdateFrontendTokenDefinitionClearsFrontendTokenDefinition(
			StringPool.BLANK);
		_testUpdateFrontendTokenDefinitionWithDuplicateFrontendTokenInPayload();
		_testUpdateFrontendTokenDefinitionWithInvalidJSON();
		_testUpdateFrontendTokenDefinitionWithInvalidJSONSchema();
		_testUpdateFrontendTokenDefinitionWithValidFrontendTokenDefinition();
	}

	@Test
	public void testUpdateFrontendTokensValues() throws Exception {
		_testUpdateFrontendTokensValues(StringPool.BLANK);
		_testUpdateFrontendTokensValues(
			_getFrontendTokensValues(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString()));
		_testUpdateFrontendTokensValues(null);
		_testUpdateFrontendTokensValuesWithInvalidCharacters(
			RandomTestUtil.randomString() + StringPool.LESS_THAN +
				RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
		_testUpdateFrontendTokensValuesWithInvalidCharacters(
			RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + StringPool.LESS_THAN +
				RandomTestUtil.randomString());
		_testUpdateFrontendTokensValuesWithInvalidJSON();
		_testUpdateFrontendTokensValuesWithSameValue();
	}

	private void _assertAddFrontendTokenFailure(
		Class<? extends Throwable> exceptionClass, String message,
		long styleBookEntryId) {

		AssertUtils.assertFailure(
			exceptionClass, message,
			() -> _styleBookEntryLocalService.addFrontendToken(
				styleBookEntryId, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_PRIMARY_COLOR_TOKEN_NAME, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				FrontendToken.Type.STRING.getValue(), new ServiceContext()));
	}

	private String _getFrontendTokenDefinition(
		JSONObject... frontendTokenSetJSONObjects) {

		JSONObject jsonObject = JSONUtil.put(
			"frontendTokenCategories",
			JSONUtil.putAll(
				JSONUtil.put(
					"frontendTokenSets",
					JSONUtil.putAll(frontendTokenSetJSONObjects)
				).put(
					"name", RandomTestUtil.randomString()
				)));

		return jsonObject.toString();
	}

	private JSONObject _getFrontendTokenJSONObject(
		String defaultValue, String name) {

		return JSONUtil.put(
			"defaultValue", defaultValue
		).put(
			"editorType", "ColorPicker"
		).put(
			"label", name
		).put(
			"mappings",
			JSONUtil.putAll(
				JSONUtil.put(
					"type", "cssVariable"
				).put(
					"value", name
				))
		).put(
			"name", name
		).put(
			"type", "String"
		);
	}

	private JSONObject _getFrontendTokenSetJSONObject(
		String name, JSONObject... frontendTokenJSONObjects) {

		return JSONUtil.put(
			"frontendTokens", JSONUtil.putAll(frontendTokenJSONObjects)
		).put(
			"label", name
		).put(
			"name", name
		);
	}

	private String _getFrontendTokensValues(
		String cssVariableMapping, String key, String value) {

		return JSONUtil.put(
			key,
			JSONUtil.put(
				"cssVariableMapping", cssVariableMapping
			).put(
				"tokenDefinitionId", RandomTestUtil.randomString()
			).put(
				"value", value
			)
		).toString();
	}

	private StyleBookEntry _mockStyleBookEntry(long styleBookEntryId)
		throws Exception {

		StyleBookEntry styleBookEntry = Mockito.mock(StyleBookEntry.class);

		Mockito.when(
			styleBookEntry.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			styleBookEntry.getThemeId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			styleBookEntry.getStyleBookEntryId()
		).thenReturn(
			styleBookEntryId
		);

		Mockito.when(
			styleBookEntry.isHead()
		).thenReturn(
			true
		);

		Mockito.when(
			_styleBookEntryPersistence.fetchByHeadId(styleBookEntryId)
		).thenReturn(
			styleBookEntry
		);

		Mockito.when(
			_styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId)
		).thenReturn(
			styleBookEntry
		);

		return styleBookEntry;
	}

	private void _testAddFrontendTokenWithBlankRequiredField()
		throws Exception {

		long styleBookEntryId = RandomTestUtil.randomLong();

		_mockStyleBookEntry(styleBookEntryId);

		String[] fieldNames = {
			"CSS variable mapping", "category label", "category name",
			"default value", "label", "name", "set label", "set name", "type"
		};

		for (int i = 0; i < fieldNames.length; i++) {
			String[] values = {
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				FrontendToken.Type.STRING.getValue()
			};

			values[i] = StringPool.BLANK;

			AssertUtils.assertFailure(
				StyleBookEntryFrontendTokenException.MustNotBeNull.class,
				StringBundler.concat(
					"Frontend token ", fieldNames[i], " must not be null"),
				() -> _styleBookEntryLocalService.addFrontendToken(
					styleBookEntryId, values[0], values[3], null, values[1],
					values[2], RandomTestUtil.randomString(), values[4],
					values[5], RandomTestUtil.randomString(), values[6],
					values[7], values[8], new ServiceContext()));
		}
	}

	private void _testAddFrontendTokenWithDuplicateName() throws Exception {
		long styleBookEntryId = RandomTestUtil.randomLong();

		StyleBookEntry styleBookEntry = _mockStyleBookEntry(styleBookEntryId);

		String frontendTokenDefinition = _getFrontendTokenDefinition(
			_getFrontendTokenSetJSONObject(
				RandomTestUtil.randomString(),
				_getFrontendTokenJSONObject(
					RandomTestUtil.randomString(), _PRIMARY_COLOR_TOKEN_NAME)));

		Mockito.when(
			styleBookEntry.getFrontendTokenDefinition()
		).thenReturn(
			frontendTokenDefinition
		);

		_assertAddFrontendTokenFailure(
			DuplicateStyleBookEntryFrontendTokenException.class,
			"Frontend token \"" + _PRIMARY_COLOR_TOKEN_NAME +
				"\" already exists",
			styleBookEntryId);
	}

	private void _testAddFrontendTokenWithExistingUnrelatedValue()
		throws Exception {

		long styleBookEntryId = RandomTestUtil.randomLong();

		StyleBookEntry styleBookEntry = _mockStyleBookEntry(styleBookEntryId);

		Mockito.when(
			styleBookEntry.getFrontendTokenDefinition()
		).thenReturn(
			"{}"
		);

		String unrelatedName = RandomTestUtil.randomString();
		String unrelatedValue = RandomTestUtil.randomString();

		Mockito.when(
			styleBookEntry.getFrontendTokensValues()
		).thenReturn(
			JSONUtil.put(
				unrelatedName, JSONUtil.put("value", unrelatedValue)
			).toString()
		);

		ServiceContext serviceContext = new ServiceContext();

		Mockito.when(
			_styleBookEntryPersistence.update(styleBookEntry, serviceContext)
		).thenReturn(
			styleBookEntry
		);

		Mockito.when(
			_styleBookEntryPersistence.update(styleBookEntry)
		).thenReturn(
			styleBookEntry
		);

		String value = "null";

		_styleBookEntryLocalService.addFrontendToken(
			styleBookEntryId, RandomTestUtil.randomString(), value, null,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_PRIMARY_COLOR_TOKEN_NAME, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			FrontendToken.Type.STRING.getValue(), serviceContext);

		Mockito.verify(
			styleBookEntry
		).setFrontendTokenDefinition(
			Mockito.anyString()
		);

		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
			String.class);

		Mockito.verify(
			styleBookEntry
		).setFrontendTokensValues(
			argumentCaptor.capture()
		);

		JSONObject frontendTokensValuesJSONObject =
			JSONFactoryUtil.createJSONObject(argumentCaptor.getValue());

		Assert.assertEquals(
			unrelatedValue,
			frontendTokensValuesJSONObject.getJSONObject(
				unrelatedName
			).getString(
				"value"
			));

		JSONObject frontendTokenValueJSONObject =
			frontendTokensValuesJSONObject.getJSONObject(
				"custom:" + _PRIMARY_COLOR_TOKEN_NAME);

		Assert.assertEquals(
			value, frontendTokenValueJSONObject.getString("value"));
	}

	private void _testAddFrontendTokenWithInvalidType() throws Exception {
		long styleBookEntryId = RandomTestUtil.randomLong();

		StyleBookEntry styleBookEntry = _mockStyleBookEntry(styleBookEntryId);

		Mockito.when(
			styleBookEntry.getFrontendTokenDefinition()
		).thenReturn(
			"{}"
		);

		String frontendTokenType = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			StyleBookEntryFrontendTokenException.MustHaveValidType.class,
			StringBundler.concat(
				"Frontend token type \"", frontendTokenType,
				"\" is not supported"),
			() -> _styleBookEntryLocalService.addFrontendToken(
				styleBookEntryId, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_PRIMARY_COLOR_TOKEN_NAME, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				frontendTokenType, new ServiceContext()));
	}

	private void _testAddFrontendTokenWithMalformedFrontendTokenDefinition()
		throws Exception {

		long styleBookEntryId = RandomTestUtil.randomLong();

		StyleBookEntry styleBookEntry = _mockStyleBookEntry(styleBookEntryId);

		Mockito.when(
			styleBookEntry.getFrontendTokenDefinition()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		_assertAddFrontendTokenFailure(
			StyleBookEntryFrontendTokenDefinitionException.class,
			"Unable to parse frontend token definition", styleBookEntryId);
	}

	private void _testAddFrontendTokenWithType() throws Exception {
		for (FrontendToken.Type type : FrontendToken.Type.values()) {
			long styleBookEntryId = RandomTestUtil.randomLong();

			StyleBookEntry styleBookEntry = _mockStyleBookEntry(
				styleBookEntryId);

			Mockito.when(
				styleBookEntry.getFrontendTokenDefinition()
			).thenReturn(
				"{}"
			);

			Mockito.when(
				styleBookEntry.getFrontendTokensValues()
			).thenReturn(
				"{}"
			);

			ServiceContext serviceContext = new ServiceContext();

			Mockito.when(
				_styleBookEntryPersistence.update(
					styleBookEntry, serviceContext)
			).thenReturn(
				styleBookEntry
			);

			Mockito.when(
				_styleBookEntryPersistence.update(styleBookEntry)
			).thenReturn(
				styleBookEntry
			);

			_styleBookEntryLocalService.addFrontendToken(
				styleBookEntryId, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				_PRIMARY_COLOR_TOKEN_NAME, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				type.getValue(), serviceContext);

			ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
				String.class);

			Mockito.verify(
				styleBookEntry
			).setFrontendTokenDefinition(
				argumentCaptor.capture()
			);

			JSONObject frontendTokenDefinitionJSONObject =
				JSONFactoryUtil.createJSONObject(argumentCaptor.getValue());

			JSONObject frontendTokenJSONObject =
				frontendTokenDefinitionJSONObject.getJSONArray(
					"frontendTokenCategories"
				).getJSONObject(
					0
				).getJSONArray(
					"frontendTokenSets"
				).getJSONObject(
					0
				).getJSONArray(
					"frontendTokens"
				).getJSONObject(
					0
				);

			Assert.assertEquals(
				type.getValue(), frontendTokenJSONObject.getString("type"));
		}
	}

	private void
			_testUpdateFrontendTokenDefinitionClearsFrontendTokenDefinition(
				String frontendTokenDefinition)
		throws Exception {

		long styleBookEntryId = RandomTestUtil.randomLong();

		StyleBookEntry styleBookEntry = _mockStyleBookEntry(styleBookEntryId);

		_styleBookEntryLocalService.updateFrontendTokenDefinition(
			styleBookEntryId, frontendTokenDefinition, new ServiceContext());

		Mockito.verify(
			styleBookEntry
		).setFrontendTokenDefinition(
			frontendTokenDefinition
		);
	}

	private void _testUpdateFrontendTokenDefinitionWithDuplicateFrontendTokenInPayload()
		throws Exception {

		long styleBookEntryId = RandomTestUtil.randomLong();

		_mockStyleBookEntry(styleBookEntryId);

		String frontendTokenDefinition = _getFrontendTokenDefinition(
			_getFrontendTokenSetJSONObject(
				RandomTestUtil.randomString(),
				_getFrontendTokenJSONObject(
					RandomTestUtil.randomString(), "primaryColor")),
			_getFrontendTokenSetJSONObject(
				RandomTestUtil.randomString(),
				_getFrontendTokenJSONObject(
					RandomTestUtil.randomString(), "primaryColor")));

		AssertUtils.assertFailure(
			DuplicateStyleBookEntryFrontendTokenException.class,
			"Frontend token \"primaryColor\" is defined more than once",
			() -> _styleBookEntryLocalService.updateFrontendTokenDefinition(
				styleBookEntryId, frontendTokenDefinition,
				new ServiceContext()));
	}

	private void _testUpdateFrontendTokenDefinitionWithInvalidJSON()
		throws Exception {

		long styleBookEntryId = RandomTestUtil.randomLong();

		_mockStyleBookEntry(styleBookEntryId);

		AssertUtils.assertFailure(
			StyleBookEntryFrontendTokenDefinitionException.class,
			"Unable to parse frontend token definition",
			() -> _styleBookEntryLocalService.updateFrontendTokenDefinition(
				styleBookEntryId, RandomTestUtil.randomString(),
				new ServiceContext()));
	}

	private void _testUpdateFrontendTokenDefinitionWithInvalidJSONSchema()
		throws Exception {

		long styleBookEntryId = RandomTestUtil.randomLong();

		_mockStyleBookEntry(styleBookEntryId);

		JSONObject frontendTokenJSONObject = _getFrontendTokenJSONObject(
			RandomTestUtil.randomString(), "primaryColor");

		frontendTokenJSONObject.put("type", "NotAValidType");

		String frontendTokenDefinition = _getFrontendTokenDefinition(
			_getFrontendTokenSetJSONObject(
				RandomTestUtil.randomString(), frontendTokenJSONObject));

		AssertUtils.assertFailure(
			StyleBookEntryFrontendTokenDefinitionException.class,
			"Unable to parse frontend token definition",
			() -> _styleBookEntryLocalService.updateFrontendTokenDefinition(
				styleBookEntryId, frontendTokenDefinition,
				new ServiceContext()));
	}

	private void _testUpdateFrontendTokenDefinitionWithValidFrontendTokenDefinition()
		throws Exception {

		long styleBookEntryId = RandomTestUtil.randomLong();

		StyleBookEntry styleBookEntry = _mockStyleBookEntry(styleBookEntryId);

		Mockito.when(
			_styleBookEntryPersistence.update(
				Mockito.eq(styleBookEntry), Mockito.any(ServiceContext.class))
		).thenReturn(
			styleBookEntry
		);

		String frontendTokenDefinition = _getFrontendTokenDefinition(
			_getFrontendTokenSetJSONObject(
				RandomTestUtil.randomString(),
				_getFrontendTokenJSONObject(
					RandomTestUtil.randomString(), "primaryColor")));

		StyleBookEntry updatedStyleBookEntry =
			_styleBookEntryLocalService.updateFrontendTokenDefinition(
				styleBookEntryId, frontendTokenDefinition,
				new ServiceContext());

		Assert.assertEquals(styleBookEntry, updatedStyleBookEntry);

		Mockito.verify(
			styleBookEntry
		).setFrontendTokenDefinition(
			frontendTokenDefinition
		);
	}

	private void _testUpdateFrontendTokensValues(String frontendTokensValues)
		throws Exception {

		long styleBookEntryId = RandomTestUtil.randomLong();

		StyleBookEntry styleBookEntry = _mockStyleBookEntry(styleBookEntryId);

		_styleBookEntryLocalService.updateFrontendTokensValues(
			styleBookEntryId, frontendTokensValues);

		Mockito.verify(
			styleBookEntry
		).setFrontendTokensValues(
			frontendTokensValues
		);
	}

	private void _testUpdateFrontendTokensValuesWithInvalidCharacters(
			String cssVariableMapping, String value)
		throws Exception {

		long styleBookEntryId = RandomTestUtil.randomLong();

		_mockStyleBookEntry(styleBookEntryId);

		String key = RandomTestUtil.randomString();

		String frontendTokensValues = _getFrontendTokensValues(
			cssVariableMapping, key, value);

		AssertUtils.assertFailure(
			StyleBookEntryFrontendTokensValuesException.
				MustNotContainInvalidCharacters.class,
			StringBundler.concat(
				"Frontend token value \"", key,
				"\" contains invalid characters"),
			() -> _styleBookEntryLocalService.updateFrontendTokensValues(
				styleBookEntryId, frontendTokensValues));
	}

	private void _testUpdateFrontendTokensValuesWithInvalidJSON()
		throws Exception {

		long styleBookEntryId = RandomTestUtil.randomLong();

		_mockStyleBookEntry(styleBookEntryId);

		AssertUtils.assertFailure(
			StyleBookEntryFrontendTokensValuesException.MustBeValidJSON.class,
			"Unable to parse frontend tokens values",
			() -> _styleBookEntryLocalService.updateFrontendTokensValues(
				styleBookEntryId, "{not valid json"));
	}

	private void _testUpdateFrontendTokensValuesWithSameValue()
		throws Exception {

		long styleBookEntryId = RandomTestUtil.randomLong();

		StyleBookEntry styleBookEntry = _mockStyleBookEntry(styleBookEntryId);

		String frontendTokensValues = _getFrontendTokensValues(
			RandomTestUtil.randomString() + StringPool.LESS_THAN +
				RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Mockito.when(
			styleBookEntry.getFrontendTokensValues()
		).thenReturn(
			frontendTokensValues
		);

		_styleBookEntryLocalService.updateFrontendTokensValues(
			styleBookEntryId, frontendTokensValues);

		Mockito.verify(
			styleBookEntry
		).setFrontendTokensValues(
			frontendTokensValues
		);
	}

	private static final String _PRIMARY_COLOR_TOKEN_NAME = "primaryColor";

	@InjectMocks
	private StyleBookEntryLocalService _styleBookEntryLocalService =
		new StyleBookEntryLocalServiceImpl();

	@Mock
	private StyleBookEntryPersistence _styleBookEntryPersistence;

}