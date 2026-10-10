/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.accessibility.menu.web.internal.util;

import com.liferay.accessibility.menu.web.internal.constants.AccessibilitySettingConstants;
import com.liferay.accessibility.menu.web.internal.model.AccessibilitySetting;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.language.LanguageImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Evan Thibodeau
 */
public class AccessibilitySettingsUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_httpServletRequest = new MockHttpServletRequest();

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(new LanguageImpl());
	}

	@Test
	public void testGetAccessibilitySettings() {
		List<AccessibilitySetting> accessibilitySettings =
			AccessibilitySettingsUtil.getAccessibilitySettings(
				_httpServletRequest);

		for (AccessibilitySetting accessibilitySetting :
				accessibilitySettings) {

			Assert.assertEquals(
				null, accessibilitySetting.getSessionClicksValue());
		}

		Assert.assertTrue(
			ListUtil.exists(
				accessibilitySettings,
				accessibilitySetting ->
					Objects.equals(
						accessibilitySetting.getCssClass(),
						"c-prefers-persistent-alerts") &&
					Objects.equals(
						accessibilitySetting.getKey(),
						AccessibilitySettingConstants.
							ACCESSIBILITY_SETTING_PERSISTENT_NOTIFICATIONS)));
	}

	@Test
	public void testGetAccessibilitySettingsHighContrast() {
		List<AccessibilitySetting> accessibilitySettings = ListUtil.filter(
			AccessibilitySettingsUtil.getAccessibilitySettings(
				_httpServletRequest),
			accessibilitySetting -> Objects.equals(
				accessibilitySetting.getKey(),
				AccessibilitySettingConstants.
					ACCESSIBILITY_SETTING_HIGH_CONTRAST));

		Assert.assertEquals(
			accessibilitySettings.toString(), 1, accessibilitySettings.size());

		AccessibilitySetting accessibilitySetting = accessibilitySettings.get(
			0);

		Assert.assertEquals(
			"data-prefers-contrast", accessibilitySetting.getAttributeName());
		Assert.assertEquals("more", accessibilitySetting.getAttributeValue());
		Assert.assertNull(accessibilitySetting.getCssClass());
		Assert.assertFalse(accessibilitySetting.getDefaultValue());
	}

	private HttpServletRequest _httpServletRequest;

}