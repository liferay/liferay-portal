/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.util;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.portal.json.JSONFactoryImpl;
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
		FrontendTokenDefinition frontendTokenDefinition =
			_mockFrontendTokenDefinition(_THEME_ID);

		Locale locale = LocaleUtil.getDefault();

		Assert.assertEquals(
			_DEFAULT_VALUE,
			_getFrontendTokenValue(
				"successColor",
				StyleBookEntryUtil.getFrontendTokensValues(
					frontendTokenDefinition, locale, null)));
		Assert.assertEquals(
			_DEFAULT_VALUE,
			_getFrontendTokenValue(
				"successColor",
				StyleBookEntryUtil.getFrontendTokensValues(
					frontendTokenDefinition, locale,
					_mockStyleBookEntry(JSONFactoryUtil.createJSONObject()))));
		Assert.assertEquals(
			"#34F787",
			_getFrontendTokenValue(
				"successColor",
				StyleBookEntryUtil.getFrontendTokensValues(
					frontendTokenDefinition, locale,
					_mockStyleBookEntry(
						JSONUtil.put(
							"successColor",
							JSONUtil.put("value", "#34F787"))))));
		Assert.assertEquals(
			"#34F787",
			_getFrontendTokenValue(
				"successColor",
				StyleBookEntryUtil.getFrontendTokensValues(
					_mockFrontendTokenDefinition(null), locale,
					_mockStyleBookEntry(
						JSONUtil.put(
							"successColor",
							JSONUtil.put("value", "#34F787"))))));
		Assert.assertEquals(
			"#34F787",
			_getFrontendTokenValue(
				"successColor",
				StyleBookEntryUtil.getFrontendTokensValues(
					frontendTokenDefinition, locale,
					_mockStyleBookEntry(
						JSONUtil.put(
							_THEME_ID + ":successColor",
							JSONUtil.put("value", "#34F787"))))));
		Assert.assertEquals(
			"#NEWVAL",
			_getFrontendTokenValue(
				"successColor",
				StyleBookEntryUtil.getFrontendTokensValues(
					frontendTokenDefinition, locale,
					_mockStyleBookEntry(
						JSONUtil.put(
							_THEME_ID + ":successColor",
							JSONUtil.put("value", "#NEWVAL")
						).put(
							"successColor", JSONUtil.put("value", "#OLDVAL")
						)))));

		Map<String, Object> frontendTokensValues =
			StyleBookEntryUtil.getFrontendTokensValues(
				null, locale,
				_mockStyleBookEntry(JSONFactoryUtil.createJSONObject()));

		Assert.assertTrue(frontendTokensValues.isEmpty());
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

		Group otherScopeGroup = _getGroup();

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

		List<Map<String, Object>> styleBookEntryMaps =
			StyleBookEntryUtil.getStyleBookEntryMaps(
				frontendTokenDefinition, false, layout, themeDisplay);

		_assertStyleBookEntryMap(
			currentScopeStyleBookEntry.getExternalReferenceCode(), null, false,
			styleBookEntryMaps.get(0));
		_assertStyleBookEntryMap(
			orphanScopeStyleBookEntry.getExternalReferenceCode(), null, false,
			styleBookEntryMaps.get(1));
		_assertStyleBookEntryMap(
			otherScopeStyleBookEntry1.getExternalReferenceCode(),
			otherScopeGroup, false, styleBookEntryMaps.get(2));
		_assertStyleBookEntryMap(
			otherScopeStyleBookEntry2.getExternalReferenceCode(),
			otherScopeGroup, false, styleBookEntryMaps.get(3));

		Assert.assertEquals(
			styleBookEntryMaps.toString(), 4, styleBookEntryMaps.size());

		_groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroup(
				otherScopeGroup.getGroupId()),
			Mockito.times(1));

		_groupLocalServiceUtilMockedStatic.clearInvocations();

		styleBookEntryMaps = StyleBookEntryUtil.getStyleBookEntryMaps(
			frontendTokenDefinition, true, layout, themeDisplay);

		_assertStyleBookEntryMap(
			currentScopeStyleBookEntry.getExternalReferenceCode(), null, true,
			styleBookEntryMaps.get(0));
		_assertStyleBookEntryMap(
			orphanScopeStyleBookEntry.getExternalReferenceCode(), null, true,
			styleBookEntryMaps.get(1));
		_assertStyleBookEntryMap(
			otherScopeStyleBookEntry1.getExternalReferenceCode(),
			otherScopeGroup, true, styleBookEntryMaps.get(2));
		_assertStyleBookEntryMap(
			otherScopeStyleBookEntry2.getExternalReferenceCode(),
			otherScopeGroup, true, styleBookEntryMaps.get(3));

		Assert.assertEquals(
			styleBookEntryMaps.toString(), 4, styleBookEntryMaps.size());

		_groupLocalServiceUtilMockedStatic.verify(
			() -> GroupLocalServiceUtil.fetchGroup(
				otherScopeGroup.getGroupId()),
			Mockito.times(1));
	}

	private void _assertStyleBookEntryMap(
			String externalReferenceCode, Group group,
			boolean includeFrontendTokenValues,
			Map<String, Object> styleBookEntryMap)
		throws Exception {

		Assert.assertEquals(
			externalReferenceCode, styleBookEntryMap.get("styleBookEntryERC"));

		if (group == null) {
			Assert.assertFalse(
				styleBookEntryMap.toString(),
				styleBookEntryMap.containsKey("styleBookEntryScopeERC"));
			Assert.assertFalse(
				styleBookEntryMap.toString(),
				styleBookEntryMap.containsKey("subtitle"));
		}
		else {
			Assert.assertEquals(
				group.getExternalReferenceCode(),
				styleBookEntryMap.get("styleBookEntryScopeERC"));
			Assert.assertEquals(
				group.getDescriptiveName(LocaleUtil.getDefault()),
				styleBookEntryMap.get("subtitle"));
		}

		Assert.assertEquals(
			includeFrontendTokenValues,
			styleBookEntryMap.containsKey("tokenValues"));
	}

	private Object _getFrontendTokenValue(
		String name, Map<String, Object> frontendTokensValues) {

		Map<?, ?> frontendTokenValue = (Map<?, ?>)frontendTokensValues.get(
			name);

		return frontendTokenValue.get("value");
	}

	private Group _getGroup() throws Exception {
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

	private FrontendTokenDefinition _mockFrontendTokenDefinition(String themeId)
		throws Exception {

		FrontendTokenDefinition frontendTokenDefinition = Mockito.mock(
			FrontendTokenDefinition.class);

		Mockito.when(
			frontendTokenDefinition.getThemeId()
		).thenReturn(
			themeId
		);

		JSONObject frontendTokenDefinitionJSONObject = JSONUtil.put(
			"frontendTokenCategories",
			JSONUtil.putAll(
				JSONUtil.put(
					"frontendTokenSets",
					JSONUtil.putAll(
						JSONUtil.put(
							"frontendTokens",
							JSONUtil.putAll(
								JSONUtil.put(
									"defaultValue", _DEFAULT_VALUE
								).put(
									"editorType", "ColorPicker"
								).put(
									"label", "success"
								).put(
									"mappings",
									JSONUtil.putAll(
										JSONUtil.put(
											"type", "cssVariable"
										).put(
											"value", "success"
										))
								).put(
									"name", "successColor"
								))
						).put(
							"label", "Theme Colors"
						))
				).put(
					"label", "Color System"
				)));

		Mockito.when(
			frontendTokenDefinition.getJSONObject(Mockito.any(Locale.class))
		).thenReturn(
			frontendTokenDefinitionJSONObject
		);

		return frontendTokenDefinition;
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

	private static final String _DEFAULT_VALUE = "#287d3c";

	private static final String _THEME_ID = "classic_WAR_classictheme";

	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);
	private static final MockedStatic<StagingUtil> _stagingUtilMockedStatic =
		Mockito.mockStatic(StagingUtil.class);
	private static final MockedStatic<StyleBookEntryProviderUtil>
		_styleBookEntryProviderUtilMockedStatic = Mockito.mockStatic(
			StyleBookEntryProviderUtil.class);

}