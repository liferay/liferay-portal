/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.util;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.util.FrontendTokenDefinitionUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.StyleBookEntryProviderUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		_stagingUtilMockedStatic.when(
			() -> StagingUtil.getLiveGroupId(Mockito.anyLong())
		).thenAnswer(
			(Answer<Long>)invocationOnMock -> invocationOnMock.getArgument(
				0, Long.class)
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_groupLocalServiceUtilMockedStatic.close();
		_stagingUtilMockedStatic.close();
		_styleBookEntryProviderUtilMockedStatic.close();
	}

	@Test
	public void testGetFrontendTokensValues() throws Exception {
		_testGetFrontendTokensValuesWithCustomDefinition();
		_testGetFrontendTokensValuesWithDefaultDefinition();
	}

	@Test
	@TestInfo("LPD-89205")
	public void testGetStyleBookEntryMaps() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		long groupId = RandomTestUtil.randomLong();

		StyleBookEntry currentScopeStyleBookEntry = _mockStyleBookEntry(
			groupId);

		StyleBookEntry orphanScopeStyleBookEntry = _mockStyleBookEntry(
			RandomTestUtil.randomLong());

		Group otherScopeGroup = _mockGroup();

		StyleBookEntry otherScopeStyleBookEntry1 = _mockStyleBookEntry(
			otherScopeGroup.getGroupId());
		StyleBookEntry otherScopeStyleBookEntry2 = _mockStyleBookEntry(
			otherScopeGroup.getGroupId());

		_styleBookEntryProviderUtilMockedStatic.when(
			() -> StyleBookEntryProviderUtil.getStyleBookEntries(
				companyId, groupId, _THEME_ID)
		).thenReturn(
			Arrays.asList(
				currentScopeStyleBookEntry, orphanScopeStyleBookEntry,
				otherScopeStyleBookEntry1, otherScopeStyleBookEntry2)
		);

		FrontendTokenDefinition frontendTokenDefinition =
			_mockFrontendTokenDefinition(_THEME_ID);
		Layout layout = _mockLayout(companyId, groupId);
		ThemeDisplay themeDisplay = _mockThemeDisplay();

		for (boolean includeFrontendTokensValues :
				new boolean[] {false, true}) {

			List<Map<String, Object>> styleBookEntryMaps =
				StyleBookEntryUtil.getStyleBookEntryMaps(
					frontendTokenDefinition, includeFrontendTokensValues,
					layout, themeDisplay);

			Assert.assertEquals(
				styleBookEntryMaps.toString(), 4, styleBookEntryMaps.size());

			_assertStyleBookEntryMap(
				currentScopeStyleBookEntry.getExternalReferenceCode(),
				includeFrontendTokensValues, null, styleBookEntryMaps.get(0));
			_assertStyleBookEntryMap(
				orphanScopeStyleBookEntry.getExternalReferenceCode(),
				includeFrontendTokensValues, null, styleBookEntryMaps.get(1));
			_assertStyleBookEntryMap(
				otherScopeStyleBookEntry1.getExternalReferenceCode(),
				includeFrontendTokensValues, otherScopeGroup,
				styleBookEntryMaps.get(2));
			_assertStyleBookEntryMap(
				otherScopeStyleBookEntry2.getExternalReferenceCode(),
				includeFrontendTokensValues, otherScopeGroup,
				styleBookEntryMaps.get(3));

			_groupLocalServiceUtilMockedStatic.verify(
				() -> GroupLocalServiceUtil.fetchGroup(
					otherScopeGroup.getGroupId()),
				Mockito.times(1));

			_groupLocalServiceUtilMockedStatic.clearInvocations();
		}
	}

	private void _assertFrontendTokenValue(
			String expectedValue,
			FrontendTokenDefinition frontendTokenDefinition, String name,
			StyleBookEntry styleBookEntry)
		throws Exception {

		Assert.assertEquals(
			expectedValue,
			_getFrontendTokenValue(
				StyleBookEntryUtil.getFrontendTokensValues(
					frontendTokenDefinition, LocaleUtil.getDefault(),
					styleBookEntry),
				name));
	}

	private void _assertStyleBookEntryMap(
			String externalReferenceCode, boolean includeFrontendTokensValues,
			Group scopeGroup, Map<String, Object> styleBookEntryMap)
		throws Exception {

		Assert.assertEquals(
			externalReferenceCode, styleBookEntryMap.get("styleBookEntryERC"));

		if (scopeGroup == null) {
			Assert.assertFalse(
				styleBookEntryMap.toString(),
				styleBookEntryMap.containsKey("styleBookEntryScopeERC"));
			Assert.assertFalse(
				styleBookEntryMap.toString(),
				styleBookEntryMap.containsKey("subtitle"));
		}
		else {
			Assert.assertEquals(
				scopeGroup.getExternalReferenceCode(),
				styleBookEntryMap.get("styleBookEntryScopeERC"));
			Assert.assertEquals(
				scopeGroup.getDescriptiveName(LocaleUtil.getDefault()),
				styleBookEntryMap.get("subtitle"));
		}

		Assert.assertEquals(
			includeFrontendTokensValues,
			styleBookEntryMap.containsKey("tokenValues"));
	}

	private JSONObject _createFrontendTokenDefinitionJSONObject(
		JSONArray frontendTokensJSONArray) {

		return JSONUtil.put(
			"frontendTokenCategories",
			JSONUtil.putAll(
				JSONUtil.put(
					"frontendTokenSets",
					JSONUtil.putAll(
						JSONUtil.put(
							"frontendTokens", frontendTokensJSONArray
						).put(
							"label", "Theme Colors"
						).put(
							"name", "themeColors"
						))
				).put(
					"label", "Color System"
				).put(
					"name", "colorSystem"
				)));
	}

	private JSONObject _createFrontendTokenJSONObject(
		String defaultValue, String label, String name) {

		return JSONUtil.put(
			"defaultValue", defaultValue
		).put(
			"editorType", "ColorPicker"
		).put(
			"label", label
		).put(
			"mappings",
			JSONUtil.putAll(
				JSONUtil.put(
					"type", "cssVariable"
				).put(
					"value", label
				))
		).put(
			"name", name
		);
	}

	private Object _getFrontendTokenValue(
		Map<String, Object> frontendTokensValues, String name) {

		Map<?, ?> frontendTokenValue = (Map<?, ?>)frontendTokensValues.get(
			name);

		return frontendTokenValue.get("value");
	}

	private FrontendTokenDefinition _mockFrontendTokenDefinition(String themeId)
		throws Exception {

		FrontendTokenDefinition frontendTokenDefinition = Mockito.mock(
			FrontendTokenDefinition.class);

		Mockito.when(
			frontendTokenDefinition.getThemeId()
		).thenReturn(
			themeId
		);

		Mockito.when(
			frontendTokenDefinition.getJSONObject(Mockito.any(Locale.class))
		).thenReturn(
			_createFrontendTokenDefinitionJSONObject(
				JSONUtil.putAll(
					_createFrontendTokenJSONObject(
						_SUCCESS_COLOR_DEFAULT_VALUE,
						RandomTestUtil.randomString(),
						_SUCCESS_COLOR_TOKEN_NAME)))
		);

		return frontendTokenDefinition;
	}

	private Group _mockGroup() throws Exception {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getDescriptiveName(LocaleUtil.getDefault())
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			group.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroup(group.getGroupId())
		).thenReturn(
			group
		);

		return group;
	}

	private Layout _mockLayout(long companyId, long groupId) {
		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			layout.getGroupId()
		).thenReturn(
			groupId
		);

		return layout;
	}

	private StyleBookEntry _mockStyleBookEntry(
		JSONObject frontendTokensValuesJSONObject) {

		StyleBookEntry styleBookEntry = Mockito.mock(StyleBookEntry.class);

		Mockito.when(
			styleBookEntry.getFrontendTokensValues()
		).thenReturn(
			frontendTokensValuesJSONObject.toString()
		);

		return styleBookEntry;
	}

	private StyleBookEntry _mockStyleBookEntry(
		JSONObject frontendTokensValuesJSONObject,
		String overrideFrontendTokenDefinitionJSON, String themeId) {

		StyleBookEntry styleBookEntry = _mockStyleBookEntry(
			frontendTokensValuesJSONObject);

		Mockito.when(
			styleBookEntry.getFrontendTokenDefinition()
		).thenReturn(
			overrideFrontendTokenDefinitionJSON
		);

		Mockito.when(
			styleBookEntry.getThemeId()
		).thenReturn(
			themeId
		);

		return styleBookEntry;
	}

	private StyleBookEntry _mockStyleBookEntry(long groupId) {
		StyleBookEntry styleBookEntry = Mockito.mock(StyleBookEntry.class);

		Mockito.when(
			styleBookEntry.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			styleBookEntry.getFrontendTokensValues()
		).thenReturn(
			"{}"
		);

		Mockito.when(
			styleBookEntry.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			styleBookEntry.getImagePreviewURL(Mockito.any(ThemeDisplay.class))
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			styleBookEntry.getName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return styleBookEntry;
	}

	private ThemeDisplay _mockThemeDisplay() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.getDefault()
		);

		return themeDisplay;
	}

	private void _testGetFrontendTokensValuesWithCustomDefinition()
		throws Exception {

		FrontendTokenDefinition frontendTokenDefinition =
			_mockFrontendTokenDefinition(_THEME_ID);

		JSONObject customFrontendTokenDefinitionJSONObject =
			_createFrontendTokenDefinitionJSONObject(
				JSONUtil.putAll(
					_createFrontendTokenJSONObject(
						"#CUSTOM1", RandomTestUtil.randomString(),
						_SUCCESS_COLOR_TOKEN_NAME),
					_createFrontendTokenJSONObject(
						"#CUSTOM2", RandomTestUtil.randomString(),
						_WARNING_COLOR_TOKEN_NAME)));

		Map<String, Object> frontendTokensValues =
			StyleBookEntryUtil.getFrontendTokensValues(
				frontendTokenDefinition, LocaleUtil.getDefault(),
				_mockStyleBookEntry(
					JSONFactoryUtil.createJSONObject(),
					customFrontendTokenDefinitionJSONObject.toString(),
					_THEME_ID));

		Assert.assertEquals(
			frontendTokensValues.toString(), 2, frontendTokensValues.size());
		Assert.assertEquals(
			"#CUSTOM1",
			_getFrontendTokenValue(
				frontendTokensValues, _SUCCESS_COLOR_TOKEN_NAME));
		Assert.assertEquals(
			"#CUSTOM2",
			_getFrontendTokenValue(
				frontendTokensValues, _WARNING_COLOR_TOKEN_NAME));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				FrontendTokenDefinitionUtil.class.getName(),
				LoggerTestUtil.WARN)) {

			for (StyleBookEntry styleBookEntry :
					Arrays.asList(
						_mockStyleBookEntry(
							JSONFactoryUtil.createJSONObject(),
							customFrontendTokenDefinitionJSONObject.toString(),
							RandomTestUtil.randomString()),
						_mockStyleBookEntry(
							JSONFactoryUtil.createJSONObject(), null,
							_THEME_ID),
						_mockStyleBookEntry(
							JSONFactoryUtil.createJSONObject(),
							"{not valid json", _THEME_ID))) {

				frontendTokensValues =
					StyleBookEntryUtil.getFrontendTokensValues(
						frontendTokenDefinition, LocaleUtil.getDefault(),
						styleBookEntry);

				Assert.assertEquals(
					frontendTokensValues.toString(), 1,
					frontendTokensValues.size());
				Assert.assertEquals(
					_SUCCESS_COLOR_DEFAULT_VALUE,
					_getFrontendTokenValue(
						frontendTokensValues, _SUCCESS_COLOR_TOKEN_NAME));
			}

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());
		}
	}

	private void _testGetFrontendTokensValuesWithDefaultDefinition()
		throws Exception {

		FrontendTokenDefinition frontendTokenDefinition =
			_mockFrontendTokenDefinition(_THEME_ID);

		_assertFrontendTokenValue(
			_SUCCESS_COLOR_DEFAULT_VALUE, frontendTokenDefinition,
			_SUCCESS_COLOR_TOKEN_NAME, null);
		_assertFrontendTokenValue(
			_SUCCESS_COLOR_DEFAULT_VALUE, frontendTokenDefinition,
			_SUCCESS_COLOR_TOKEN_NAME,
			_mockStyleBookEntry(JSONFactoryUtil.createJSONObject()));
		_assertFrontendTokenValue(
			"#34F787", frontendTokenDefinition, _SUCCESS_COLOR_TOKEN_NAME,
			_mockStyleBookEntry(
				JSONUtil.put(
					_SUCCESS_COLOR_TOKEN_NAME,
					JSONUtil.put("value", "#34F787"))));

		_assertFrontendTokenValue(
			"#34F787", _mockFrontendTokenDefinition(null),
			_SUCCESS_COLOR_TOKEN_NAME,
			_mockStyleBookEntry(
				JSONUtil.put(
					_SUCCESS_COLOR_TOKEN_NAME,
					JSONUtil.put("value", "#34F787"))));
		_assertFrontendTokenValue(
			"#34F787", frontendTokenDefinition, _SUCCESS_COLOR_TOKEN_NAME,
			_mockStyleBookEntry(
				JSONUtil.put(
					_THEME_ID + ":" + _SUCCESS_COLOR_TOKEN_NAME,
					JSONUtil.put("value", "#34F787"))));
		_assertFrontendTokenValue(
			"#NEWVAL", frontendTokenDefinition, _SUCCESS_COLOR_TOKEN_NAME,
			_mockStyleBookEntry(
				JSONUtil.put(
					_SUCCESS_COLOR_TOKEN_NAME, JSONUtil.put("value", "#OLDVAL")
				).put(
					_THEME_ID + ":" + _SUCCESS_COLOR_TOKEN_NAME,
					JSONUtil.put("value", "#NEWVAL")
				)));

		Map<String, Object> frontendTokensValues =
			StyleBookEntryUtil.getFrontendTokensValues(
				null, LocaleUtil.getDefault(),
				_mockStyleBookEntry(JSONFactoryUtil.createJSONObject()));

		Assert.assertTrue(frontendTokensValues.isEmpty());
	}

	private static final String _SUCCESS_COLOR_DEFAULT_VALUE = "#287d3c";

	private static final String _SUCCESS_COLOR_TOKEN_NAME = "successColor";

	private static final String _THEME_ID = "classic_WAR_classictheme";

	private static final String _WARNING_COLOR_TOKEN_NAME = "warningColor";

	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);
	private static final MockedStatic<StagingUtil> _stagingUtilMockedStatic =
		Mockito.mockStatic(StagingUtil.class);
	private static final MockedStatic<StyleBookEntryProviderUtil>
		_styleBookEntryProviderUtilMockedStatic = Mockito.mockStatic(
			StyleBookEntryProviderUtil.class);

}