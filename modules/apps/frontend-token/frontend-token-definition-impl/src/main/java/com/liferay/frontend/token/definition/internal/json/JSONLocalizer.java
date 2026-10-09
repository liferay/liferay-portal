/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.internal.json;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Iván Zaera
 * @review
 */
public class JSONLocalizer {

	public JSONLocalizer(
		String json, JSONFactory jsonFactory,
		ResourceBundleLoader resourceBundleLoader, String themeId) {

		try {
			JSONObject jsonObject = jsonFactory.createJSONObject(json);

			_jsonMap = jsonObject.toMap();
		}
		catch (JSONException jsonException) {
			throw new RuntimeException(jsonException);
		}

		_jsonFactory = jsonFactory;
		_resourceBundleLoader = resourceBundleLoader;
		_themeId = themeId;
	}

	/**
	 * Get a JSONObject
	 * @param locale the target locale or null to get the default JSONObject
	 * @return the JSONObject that created with the translated JSON
	 */
	public JSONObject getJSONObject(Locale locale) {
		if ((_resourceBundleLoader == null) || (locale == null)) {
			return _jsonFactory.createJSONObject(_jsonMap);
		}

		Map<?, ?> jsonMap = _jsonMaps.computeIfAbsent(
			locale,
			key -> {
				JSONObject newJSONObject = _jsonFactory.createJSONObject(
					_jsonMap);

				_localize(newJSONObject, locale);

				return newJSONObject.toMap();
			});

		return _jsonFactory.createJSONObject(jsonMap);
	}

	private void _localize(JSONObject jsonObject, Locale locale) {
		if (locale == null) {
			return;
		}

		ResourceBundle resourceBundle =
			_resourceBundleLoader.loadResourceBundle(locale);

		if (resourceBundle == null) {
			return;
		}

		Map<String, String> map = new HashMap<>();

		for (String key : jsonObject.keySet()) {
			if (_localizableKeys.contains(key)) {
				String value = jsonObject.getString(key);

				if (Validator.isNotNull(value)) {
					try {
						map.put(
							key,
							GetterUtil.getString(
								ResourceBundleUtil.getString(
									resourceBundle, value),
								value));
					}
					catch (MissingResourceException missingResourceException) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								StringBundler.concat(
									"Unable to find key ", key, " in theme ",
									_themeId),
								missingResourceException);
						}
					}
				}
			}
			else {
				Object object = jsonObject.get(key);

				if (object instanceof JSONObject) {
					_localize((JSONObject)object, locale);
				}
				else if (object instanceof JSONArray) {
					JSONArray jsonArray = (JSONArray)object;

					for (int i = 0; i < jsonArray.length(); i++) {
						Object currentObject = jsonArray.get(i);

						if (!(currentObject instanceof JSONObject)) {
							continue;
						}

						_localize((JSONObject)currentObject, locale);
					}
				}
			}
		}

		for (Map.Entry<String, String> entry : map.entrySet()) {
			jsonObject.put(entry.getKey(), entry.getValue());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(JSONLocalizer.class);

	private static final Set<String> _localizableKeys = new HashSet<>(
		Arrays.asList("label"));

	private final JSONFactory _jsonFactory;
	private final Map<?, ?> _jsonMap;
	private final Map<Locale, Map<?, ?>> _jsonMaps = new ConcurrentHashMap<>();
	private final ResourceBundleLoader _resourceBundleLoader;
	private final String _themeId;

}