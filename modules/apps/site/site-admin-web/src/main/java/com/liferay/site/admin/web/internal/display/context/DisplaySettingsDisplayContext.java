/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.display.context;

import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Pablo Molina
 * @author Jürgen Kappler
 */
public class DisplaySettingsDisplayContext {

	public DisplaySettingsDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public long getLiveGroupId() {
		Group liveGroup = _getLiveGroup();

		return liveGroup.getGroupId();
	}

	public Map<String, Object> getPropsMap() {
		Group liveGroup = _getLiveGroup();

		return HashMapBuilder.<String, Object>put(
			"availableLanguages", _getAvailableLanguagesJSONArray()
		).put(
			"currentLanguages", _getCurrentLanguagesJSONArray()
		).put(
			"defaultLanguageId",
			() -> LocaleUtil.toLanguageId(
				PortalUtil.getSiteDefaultLocale(liveGroup.getGroupId()))
		).put(
			"inheritLocales",
			() -> {
				UnicodeProperties typeSettingsUnicodeProperties =
					_getTypeSettingsUnicodeProperties();

				return GetterUtil.getBoolean(
					typeSettingsUnicodeProperties.getProperty(
						GroupConstants.TYPE_SETTINGS_KEY_INHERIT_LOCALES),
					true);
			}
		).put(
			"liveGroupIsGuest", liveGroup.isGuest()
		).put(
			"liveGroupIsOrganization", liveGroup.isOrganization()
		).put(
			"portletNamespace", _liferayPortletResponse.getNamespace()
		).build();
	}

	private JSONArray _getAvailableLanguagesJSONArray() {
		JSONArray availableLanguagesJSONArray =
			JSONFactoryUtil.createJSONArray();

		Set<JSONObject> availableLanguagesJSONObjects = new TreeSet<>(
			(jsonObject1, jsonObject2) -> {
				String value1 = jsonObject1.getString("value");
				String value2 = jsonObject2.getString("value");

				return value1.compareTo(value2);
			});

		List<Locale> siteCurrentLocales = _getCurrentLocales();

		for (Locale availableLocale : LanguageUtil.getAvailableLocales()) {
			if (!siteCurrentLocales.contains(availableLocale)) {
				availableLanguagesJSONObjects.add(
					JSONUtil.put(
						"label",
						availableLocale.getDisplayName(
							_themeDisplay.getLocale())
					).put(
						"value", LocaleUtil.toLanguageId(availableLocale)
					));
			}
		}

		for (JSONObject availableLanguageJSONObject :
				availableLanguagesJSONObjects) {

			availableLanguagesJSONArray.put(availableLanguageJSONObject);
		}

		return availableLanguagesJSONArray;
	}

	private JSONArray _getCurrentLanguagesJSONArray() {
		JSONArray currentLanguagesJSONArray = JSONFactoryUtil.createJSONArray();

		for (Locale currentLocale : _getCurrentLocales()) {
			currentLanguagesJSONArray.put(
				JSONUtil.put(
					"label",
					currentLocale.getDisplayName(_themeDisplay.getLocale())
				).put(
					"value", LanguageUtil.getLanguageId(currentLocale)
				));
		}

		return currentLanguagesJSONArray;
	}

	private List<Locale> _getCurrentLocales() {
		List<Locale> currentLocales = new ArrayList<>();

		UnicodeProperties typeSettingsUnicodeProperties =
			_getTypeSettingsUnicodeProperties();

		String groupLanguageIds = typeSettingsUnicodeProperties.getProperty(
			PropsKeys.LOCALES);

		if (groupLanguageIds != null) {
			Collections.addAll(
				currentLocales,
				LocaleUtil.fromLanguageIds(StringUtil.split(groupLanguageIds)));
		}
		else {
			currentLocales.addAll(_getSiteAvailableLocales());
		}

		return currentLocales;
	}

	private Group _getLiveGroup() {
		if (_liveGroup != null) {
			return _liveGroup;
		}

		Group siteGroup = _themeDisplay.getSiteGroup();

		if (siteGroup.isStagingGroup()) {
			_liveGroup = siteGroup.getLiveGroup();
		}
		else {
			_liveGroup = siteGroup;
		}

		return _liveGroup;
	}

	private Set<Locale> _getSiteAvailableLocales() {
		if (_siteAvailableLocales != null) {
			return _siteAvailableLocales;
		}

		Group liveGroup = _getLiveGroup();

		_siteAvailableLocales = LanguageUtil.getAvailableLocales(
			liveGroup.getGroupId());

		return _siteAvailableLocales;
	}

	private UnicodeProperties _getTypeSettingsUnicodeProperties() {
		if (_typeSettingsUnicodeProperties != null) {
			return _typeSettingsUnicodeProperties;
		}

		UnicodeProperties typeSettingsUnicodeProperties = null;

		Group liveGroup = _getLiveGroup();

		if (liveGroup != null) {
			typeSettingsUnicodeProperties =
				liveGroup.getTypeSettingsProperties();
		}
		else {
			typeSettingsUnicodeProperties = new UnicodeProperties();
		}

		_typeSettingsUnicodeProperties = typeSettingsUnicodeProperties;

		return _typeSettingsUnicodeProperties;
	}

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private Group _liveGroup;
	private Set<Locale> _siteAvailableLocales;
	private final ThemeDisplay _themeDisplay;
	private UnicodeProperties _typeSettingsUnicodeProperties;

}