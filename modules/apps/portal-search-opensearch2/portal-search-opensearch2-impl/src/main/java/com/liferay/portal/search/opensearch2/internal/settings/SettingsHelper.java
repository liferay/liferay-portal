/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.search.opensearch2.internal.util.IndexUtil;

import org.apache.commons.lang.StringUtils;

/**
 * @author Bryan Engler
 */
public class SettingsHelper {

	public String get(String key) {
		String[] keyParts = key.split("\\.");

		if (keyParts.length > 1) {
			JSONObject settingJSONObject = _settingsJSONObject;

			for (int i = 0; i < (keyParts.length - 1); i++) {
				settingJSONObject = settingJSONObject.getJSONObject(
					keyParts[i]);
			}

			return settingJSONObject.getString(keyParts[keyParts.length - 1]);
		}

		return _settingsJSONObject.getString(key, null);
	}

	public JSONObject getSettingsJSONObject() {
		return _settingsJSONObject;
	}

	public void loadFromSource(String source) {
		if (StringUtils.isBlank(source)) {
			return;
		}

		source = source.trim();

		if (source.charAt(0) == CharPool.OPEN_CURLY_BRACE) {
			IndexUtil.mergeToJSONObject(
				_settingsJSONObject, _createJSONObject(source));
		}
		else {
			String[] lines = source.split(StringPool.NEW_LINE);

			StringBundler sb = new StringBundler(lines.length);

			for (String line : lines) {
				if (line.contains(StringPool.PERIOD)) {
					String[] parts = line.split(StringPool.COLON);

					String value = parts[1];

					put(parts[0], value.trim());
				}
				else {
					sb.append(line);
					sb.append(StringPool.NEW_LINE);
				}
			}

			if (sb.index() > 0) {
				sb.setIndex(sb.index() - 1);

				ObjectMapper yamlObjectMapper = new ObjectMapper(
					new YAMLFactory());

				try {
					Object object = yamlObjectMapper.readValue(
						sb.toString(), Object.class);

					ObjectMapper objectMapper = new ObjectMapper();

					IndexUtil.mergeToJSONObject(
						_settingsJSONObject,
						_createJSONObject(
							objectMapper.writeValueAsString(object)));
				}
				catch (Exception exception) {
					throw new RuntimeException(
						"Failed to load settings from [" + sb.toString() + "]",
						exception);
				}
			}
		}
	}

	public void put(String key, boolean value) {
		put(key, String.valueOf(value));
	}

	public void put(String key, String value) {
		if (!StringUtils.isBlank(value)) {
			JSONObject settingJSONObject = JSONFactoryUtil.createJSONObject();

			String[] settingParts = key.split("\\.");

			for (int i = settingParts.length - 1; i >= 0; i--) {
				if (i == (settingParts.length - 1)) {
					settingJSONObject.put(settingParts[i], value);
				}
				else {
					settingJSONObject = JSONUtil.put(
						settingParts[i], settingJSONObject);
				}
			}

			IndexUtil.mergeToJSONObject(_settingsJSONObject, settingJSONObject);
		}
	}

	private JSONObject _createJSONObject(String jsonString) {
		try {
			return JSONFactoryUtil.createJSONObject(jsonString);
		}
		catch (JSONException jsonException) {
			throw new RuntimeException(jsonException);
		}
	}

	private final JSONObject _settingsJSONObject =
		JSONFactoryUtil.createJSONObject();

}