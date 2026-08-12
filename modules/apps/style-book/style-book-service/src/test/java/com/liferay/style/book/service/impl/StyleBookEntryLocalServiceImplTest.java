/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.impl;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.style.book.exception.DuplicateStyleBookEntryFrontendTokenException;
import com.liferay.style.book.exception.StyleBookEntryFrontendTokenDefinitionException;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.service.persistence.StyleBookEntryPersistence;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

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
	public void setUp() {
		MockitoAnnotations.initMocks(this);
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

	private String _createFrontendTokenDefinition(
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

	private JSONObject _createFrontendTokenJSONObject(
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

	private JSONObject _createFrontendTokenSetJSONObject(
		String name, JSONObject... frontendTokenJSONObjects) {

		return JSONUtil.put(
			"frontendTokens", JSONUtil.putAll(frontendTokenJSONObjects)
		).put(
			"label", name
		).put(
			"name", name
		);
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
			styleBookEntry.isHead()
		).thenReturn(
			true
		);

		Mockito.when(
			_styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId)
		).thenReturn(
			styleBookEntry
		);

		return styleBookEntry;
	}

	private void
			_testUpdateFrontendTokenDefinitionClearsFrontendTokenDefinition(
				String frontendTokenDefinition)
		throws Exception {

		long styleBookEntryId = RandomTestUtil.randomLong();

		StyleBookEntry styleBookEntry = _mockStyleBookEntry(styleBookEntryId);

		_styleBookEntryLocalService.updateFrontendTokenDefinition(
			styleBookEntryId, frontendTokenDefinition);

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

		String frontendTokenDefinition = _createFrontendTokenDefinition(
			_createFrontendTokenSetJSONObject(
				RandomTestUtil.randomString(),
				_createFrontendTokenJSONObject(
					RandomTestUtil.randomString(), "primaryColor")),
			_createFrontendTokenSetJSONObject(
				RandomTestUtil.randomString(),
				_createFrontendTokenJSONObject(
					RandomTestUtil.randomString(), "primaryColor")));

		AssertUtils.assertFailure(
			DuplicateStyleBookEntryFrontendTokenException.class,
			"Frontend token \"primaryColor\" is defined more than once",
			() -> _styleBookEntryLocalService.updateFrontendTokenDefinition(
				styleBookEntryId, frontendTokenDefinition));
	}

	private void _testUpdateFrontendTokenDefinitionWithInvalidJSON()
		throws Exception {

		long styleBookEntryId = RandomTestUtil.randomLong();

		_mockStyleBookEntry(styleBookEntryId);

		AssertUtils.assertFailure(
			StyleBookEntryFrontendTokenDefinitionException.class,
			"Unable to parse frontend token definition",
			() -> _styleBookEntryLocalService.updateFrontendTokenDefinition(
				styleBookEntryId, "{not valid json"));
	}

	private void _testUpdateFrontendTokenDefinitionWithInvalidJSONSchema()
		throws Exception {

		long styleBookEntryId = RandomTestUtil.randomLong();

		_mockStyleBookEntry(styleBookEntryId);

		JSONObject frontendTokenJSONObject = _createFrontendTokenJSONObject(
			RandomTestUtil.randomString(), "primaryColor");

		frontendTokenJSONObject.put("type", "NotAValidType");

		String frontendTokenDefinition = _createFrontendTokenDefinition(
			_createFrontendTokenSetJSONObject(
				RandomTestUtil.randomString(), frontendTokenJSONObject));

		AssertUtils.assertFailure(
			StyleBookEntryFrontendTokenDefinitionException.class,
			"Unable to parse frontend token definition",
			() -> _styleBookEntryLocalService.updateFrontendTokenDefinition(
				styleBookEntryId, frontendTokenDefinition));
	}

	private void _testUpdateFrontendTokenDefinitionWithValidFrontendTokenDefinition()
		throws Exception {

		long styleBookEntryId = RandomTestUtil.randomLong();

		StyleBookEntry styleBookEntry = _mockStyleBookEntry(styleBookEntryId);

		Mockito.when(
			_styleBookEntryPersistence.update(styleBookEntry)
		).thenReturn(
			styleBookEntry
		);

		String frontendTokenDefinition = _createFrontendTokenDefinition(
			_createFrontendTokenSetJSONObject(
				RandomTestUtil.randomString(),
				_createFrontendTokenJSONObject(
					RandomTestUtil.randomString(), "primaryColor")));

		StyleBookEntry updatedStyleBookEntry =
			_styleBookEntryLocalService.updateFrontendTokenDefinition(
				styleBookEntryId, frontendTokenDefinition);

		Assert.assertEquals(styleBookEntry, updatedStyleBookEntry);

		Mockito.verify(
			styleBookEntry
		).setFrontendTokenDefinition(
			frontendTokenDefinition
		);
	}

	@InjectMocks
	private StyleBookEntryLocalService _styleBookEntryLocalService =
		new StyleBookEntryLocalServiceImpl();

	@Mock
	private StyleBookEntryPersistence _styleBookEntryPersistence;

}