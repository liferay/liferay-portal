/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.accessibility.menu.web.internal.util;

import com.liferay.accessibility.menu.web.internal.configuration.AccessibilityMenuConfiguration;
import com.liferay.accessibility.menu.web.internal.constants.AccessibilitySettingConstants;
import com.liferay.accessibility.menu.web.internal.model.AccessibilitySetting;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Evan Thibodeau
 */
public class AccessibilitySettingsUtil {

	public static List<AccessibilitySetting> getAccessibilitySettings(
		HttpServletRequest httpServletRequest) {

		return ListUtil.fromArray(
			new AccessibilitySetting(
				"c-prefers-link-underline", false,
				LanguageUtil.get(
					httpServletRequest, "underlined-links-description"),
				AccessibilitySettingConstants.
					ACCESSIBILITY_SETTING_UNDERLINED_LINKS,
				LanguageUtil.get(httpServletRequest, "underlined-links"),
				_getSessionClicksValue(
					httpServletRequest,
					AccessibilitySettingConstants.
						ACCESSIBILITY_SETTING_UNDERLINED_LINKS)),
			new AccessibilitySetting(
				"c-prefers-letter-spacing-1", false,
				LanguageUtil.get(
					httpServletRequest, "increased-text-spacing-description"),
				AccessibilitySettingConstants.
					ACCESSIBILITY_SETTING_INCREASED_TEXT_SPACING,
				LanguageUtil.get(httpServletRequest, "increased-text-spacing"),
				_getSessionClicksValue(
					httpServletRequest,
					AccessibilitySettingConstants.
						ACCESSIBILITY_SETTING_INCREASED_TEXT_SPACING)),
			new AccessibilitySetting(
				"c-prefers-expanded-text", false,
				LanguageUtil.get(
					httpServletRequest, "expanded-text-description"),
				AccessibilitySettingConstants.
					ACCESSIBILITY_SETTING_EXPANDED_TEXT,
				LanguageUtil.get(httpServletRequest, "expanded-text"),
				_getSessionClicksValue(
					httpServletRequest,
					AccessibilitySettingConstants.
						ACCESSIBILITY_SETTING_EXPANDED_TEXT)),
			new AccessibilitySetting(
				"c-prefers-reduced-motion", false,
				LanguageUtil.get(
					httpServletRequest, "reduced-motion-description"),
				AccessibilitySettingConstants.
					ACCESSIBILITY_SETTING_REDUCED_MOTION,
				LanguageUtil.get(httpServletRequest, "reduced-motion"),
				_getSessionClicksValue(
					httpServletRequest,
					AccessibilitySettingConstants.
						ACCESSIBILITY_SETTING_REDUCED_MOTION)));
	}

	public static boolean isAccessibilityMenuEnabled(
		HttpServletRequest httpServletRequest,
		ConfigurationProvider configurationProvider) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			AccessibilityMenuConfiguration accessibilityMenuConfiguration =
				configurationProvider.getGroupConfiguration(
					AccessibilityMenuConfiguration.class,
					themeDisplay.getScopeGroupId());

			return accessibilityMenuConfiguration.enableAccessibilityMenu();
		}
		catch (ConfigurationException configurationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(configurationException);
			}
		}

		return false;
	}

	private static Boolean _getSessionClicksValue(
		HttpServletRequest httpServletRequest, String accessibilitySettingKey) {

		String sessionClicksValueString = GetterUtil.getString(
			SessionClicks.get(
				httpServletRequest, accessibilitySettingKey, null));

		if (Validator.isNull(sessionClicksValueString)) {
			return null;
		}

		return GetterUtil.getBoolean(sessionClicksValueString);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccessibilitySettingsUtil.class);

}