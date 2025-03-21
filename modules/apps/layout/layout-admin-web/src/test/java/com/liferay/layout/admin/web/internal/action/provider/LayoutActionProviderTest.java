/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.action.provider;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.AdditionalAnswers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class LayoutActionProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpGroup();
		_setUpHtml();
		_setUpHttpServletRequest();
		_setUpLanguageUtil();
		_setUpThemeDisplay();
	}

	@After
	public void tearDown() {
		_htmlUtilMockedStatic.close();
	}

	@Test
	public void testGetMessageKeyWhenDeleteLayoutWithChildLayout()
		throws PortalException {

		_setUpLayout(true, false);

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			new LayoutActionProvider(
				null, _httpServletRequest, _language, null, null),
			"_getDeleteJSONObject", new Class<?>[] {Layout.class, Layout.class},
			null, _layout);

		Map<String, Object> map = (Map<String, Object>)jsonObject.get("data");

		Assert.assertEquals(
			"are-you-sure-you-want-to-delete-the-page-x.-this-page-contains-" +
				"child-pages-that-will-also-be-removed",
			map.get("message"));
	}

	@Test
	public void testGetMessageKeyWhenDeleteLayoutWithChildLayoutAndScopedContent()
		throws PortalException {

		_setUpLayout(true, true);

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			new LayoutActionProvider(
				null, _httpServletRequest, _language, null, null),
			"_getDeleteJSONObject", new Class<?>[] {Layout.class, Layout.class},
			null, _layout);

		Map<String, Object> map = (Map<String, Object>)jsonObject.get("data");

		Assert.assertEquals(
			"are-you-sure-you-want-to-delete-the-page-x.-this-page-serves-as-" +
				"a-scope-for-content-and-also-contains-child-pages",
			map.get("message"));
	}

	@Test
	public void testGetMessageKeyWhenDeleteLayoutWithScopedContent()
		throws PortalException {

		_setUpLayout(false, true);

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			new LayoutActionProvider(
				null, _httpServletRequest, _language, null, null),
			"_getDeleteJSONObject", new Class<?>[] {Layout.class, Layout.class},
			null, _layout);

		Map<String, Object> map = (Map<String, Object>)jsonObject.get("data");

		Assert.assertEquals(
			"are-you-sure-you-want-to-delete-the-page-x.-this-page-serves-as-" +
				"a-scope-for-content",
			map.get("message"));
	}

	private void _setUpGroup() {
		Mockito.when(
			_group.isStaged()
		).thenReturn(
			true
		);
	}

	private void _setUpHtml() {
		_htmlUtilMockedStatic.when(
			() -> HtmlUtil.escape(Mockito.anyString())
		).thenAnswer(
			AdditionalAnswers.returnsFirstArg()
		);
	}

	private void _setUpHttpServletRequest() {
		Mockito.when(
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		Mockito.when(
			_language.format(
				Mockito.any(HttpServletRequest.class), Mockito.anyString(),
				Mockito.anyString())
		).thenAnswer(
			AdditionalAnswers.returnsSecondArg()
		);

		languageUtil.setLanguage(_language);
	}

	private void _setUpLayout(boolean hasChildren, boolean hasScopeGroup)
		throws PortalException {

		Mockito.when(
			_layout.getName(_themeDisplay.getLocale())
		).thenReturn(
			"test"
		);

		Mockito.when(
			_layout.hasChildren()
		).thenReturn(
			hasChildren
		);

		Mockito.when(
			_layout.hasScopeGroup()
		).thenReturn(
			hasScopeGroup
		);
	}

	private void _setUpThemeDisplay() {
		Mockito.when(
			_themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);
	}

	private final Group _group = Mockito.mock(Group.class);
	private final MockedStatic<HtmlUtil> _htmlUtilMockedStatic =
		Mockito.mockStatic(HtmlUtil.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Language _language = Mockito.mock(Language.class);
	private final Layout _layout = Mockito.mock(Layout.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}