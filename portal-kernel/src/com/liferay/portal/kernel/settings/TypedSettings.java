/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;

import java.util.Collection;
import java.util.Locale;

/**
 * @author Iván Zaera
 * @author Jorge Ferrer
 */
public class TypedSettings {

	public TypedSettings(Settings settings) {
		this(settings, LanguageUtil.getAvailableLocales());
	}

	public TypedSettings(
		Settings settings, Collection<Locale> availableLocales) {

		_settings = settings;
		_availableLocales = availableLocales;
	}

	public boolean getBooleanValue(String key) {
		return getBooleanValue(key, false);
	}

	public boolean getBooleanValue(String key, boolean defaultValue) {
		return GetterUtil.getBoolean(getValue(key, null), defaultValue);
	}

	public double getDoubleValue(String key) {
		return getDoubleValue(key, 0);
	}

	public double getDoubleValue(String key, double defaultValue) {
		return GetterUtil.getDouble(getValue(key, null), defaultValue);
	}

	public float getFloatValue(String key) {
		return getFloatValue(key, 0);
	}

	public float getFloatValue(String key, float defaultValue) {
		return GetterUtil.getFloat(getValue(key, null), defaultValue);
	}

	public int getIntegerValue(String key) {
		return getIntegerValue(key, 0);
	}

	public int getIntegerValue(String key, int defaultValue) {
		return GetterUtil.getInteger(getValue(key, null), defaultValue);
	}

	public LocalizedValuesMap getLocalizedValuesMap(String key) {
		String value = getValue(key, null);

		if (JSONUtil.isJSONObject(value)) {
			try {
				return _toLocalizedValuesMap(
					JSONFactoryUtil.createJSONObject(value));
			}
			catch (JSONException jsonException) {
				_log.error(jsonException);
			}
		}

		return _toLocalizedValuesMap(key, value);
	}

	public long getLongValue(String key) {
		return getLongValue(key, 0);
	}

	public long getLongValue(String key, long defaultValue) {
		return GetterUtil.getLong(getValue(key, null), defaultValue);
	}

	public String getValue(String key) {
		return getValue(key, StringPool.BLANK);
	}

	public String getValue(String key, String defaultValue) {
		return _settings.getValue(key, defaultValue);
	}

	public String[] getValues(String key) {
		return getValues(key, StringPool.EMPTY_ARRAY);
	}

	public String[] getValues(String key, String[] defaultValue) {
		return _settings.getValues(key, defaultValue);
	}

	public Settings getWrappedSettings() {
		return _settings;
	}

	private LocalizedValuesMap _toLocalizedValuesMap(JSONObject jsonObject) {
		String defaultValue = jsonObject.getString(
			LocaleUtil.toLanguageId(LocaleThreadLocal.getDefaultLocale()));

		LocalizedValuesMap localizedValuesMap = new LocalizedValuesMap(
			defaultValue);

		for (Locale locale : _availableLocales) {
			localizedValuesMap.put(
				locale,
				jsonObject.getString(LocaleUtil.toLanguageId(locale), null));
		}

		return localizedValuesMap;
	}

	private LocalizedValuesMap _toLocalizedValuesMap(
		String key, String defaultValue) {

		LocalizedValuesMap localizedValuesMap = new LocalizedValuesMap(
			defaultValue);

		for (Locale locale : _availableLocales) {
			String localizedPreference = LocalizationUtil.getLocalizedName(
				key, LocaleUtil.toLanguageId(locale));

			localizedValuesMap.put(locale, getValue(localizedPreference, null));
		}

		return localizedValuesMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(TypedSettings.class);

	private final Collection<Locale> _availableLocales;
	private final Settings _settings;

}