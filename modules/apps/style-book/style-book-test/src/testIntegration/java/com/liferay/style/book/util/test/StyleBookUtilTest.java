/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.constants.FrontendTokenDefinitionConstants;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.util.StyleBookUtil;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class StyleBookUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test
	public void testGetStyleFromThemeStyleBookEntry() throws Exception {
		StyleBookEntry styleFromThemeStyleBookEntry =
			StyleBookUtil.getStyleFromThemeStyleBookEntry(
				null, _group.getGroupId(), null);

		Assert.assertEquals(-1, styleFromThemeStyleBookEntry.getHeadId());
		Assert.assertEquals(
			0, styleFromThemeStyleBookEntry.getStyleBookEntryId());
		Assert.assertEquals(
			StringPool.BLANK, styleFromThemeStyleBookEntry.getThemeId());
		Assert.assertFalse(
			styleFromThemeStyleBookEntry.isDefaultStyleBookEntry());

		FrontendTokenDefinition frontendTokenDefinition =
			_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
				_layout);

		styleFromThemeStyleBookEntry =
			StyleBookUtil.getStyleFromThemeStyleBookEntry(
				frontendTokenDefinition, _group.getGroupId(), null);

		Assert.assertEquals(
			"styles-from-x", styleFromThemeStyleBookEntry.getName());
		Assert.assertEquals(
			frontendTokenDefinition.getThemeId(),
			styleFromThemeStyleBookEntry.getThemeId());
		Assert.assertTrue(
			styleFromThemeStyleBookEntry.isDefaultStyleBookEntry());

		_styleBookEntryLocalService.addStyleBookEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), true, null, null,
			RandomTestUtil.randomString(), null,
			frontendTokenDefinition.getThemeId(), null);

		styleFromThemeStyleBookEntry =
			StyleBookUtil.getStyleFromThemeStyleBookEntry(
				frontendTokenDefinition, _group.getGroupId(), LocaleUtil.US);

		Assert.assertEquals(
			frontendTokenDefinition.getThemeId(),
			styleFromThemeStyleBookEntry.getThemeId());
		Assert.assertFalse(
			styleFromThemeStyleBookEntry.isDefaultStyleBookEntry());
	}

	@Test
	public void testGetThemeName() {
		Locale locale = LocaleUtil.US;
		FrontendTokenDefinition frontendTokenDefinition =
			_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
				_layout);

		_testGetThemeName(
			LanguageUtil.format(
				locale, "x-theme",
				frontendTokenDefinition.getThemeName(locale)),
			frontendTokenDefinition, locale,
			FrontendTokenDefinitionConstants.THEME_TYPE_BUNDLE);
		_testGetThemeName(
			frontendTokenDefinition.getThemeName(locale),
			frontendTokenDefinition, locale,
			FrontendTokenDefinitionConstants.THEME_TYPE_GLOBAL);
		_testGetThemeName(
			LanguageUtil.format(
				locale, "x-theme-css-client-extension",
				frontendTokenDefinition.getThemeName(locale)),
			frontendTokenDefinition, locale,
			FrontendTokenDefinitionConstants.THEME_TYPE_THEME_CSS_CET);
	}

	@Test
	public void testIsThemeInactive() throws PortalException {
		Assert.assertTrue(
			StyleBookUtil.isThemeInactive(
				_layout.getCompanyId(), RandomTestUtil.randomString()));

		Theme theme = _layout.getTheme();

		Assert.assertFalse(
			StyleBookUtil.isThemeInactive(
				_layout.getCompanyId(), theme.getThemeId()));
	}

	private void _testGetThemeName(
		String expectedThemeName,
		FrontendTokenDefinition frontendTokenDefinition, Locale locale,
		String themeType) {

		String initialThemeType = frontendTokenDefinition.getThemeType();

		try {
			ReflectionTestUtil.setFieldValue(
				frontendTokenDefinition, "_themeType", themeType);

			Assert.assertEquals(
				expectedThemeName,
				ReflectionTestUtil.<String>invoke(
					StyleBookUtil.class, "_getThemeName",
					new Class<?>[] {
						FrontendTokenDefinition.class, Locale.class
					},
					frontendTokenDefinition, locale));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				frontendTokenDefinition, "_themeType", initialThemeType);
		}
	}

	@Inject
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	private Group _group;
	private Layout _layout;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}