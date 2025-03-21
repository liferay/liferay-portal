/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.Config;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ConfigSerDes {

	public static Config toDTO(String json) {
		ConfigJSONParser configJSONParser = new ConfigJSONParser();

		return configJSONParser.parseToDTO(json);
	}

	public static Config[] toDTOs(String json) {
		ConfigJSONParser configJSONParser = new ConfigJSONParser();

		return configJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Config config) {
		if (config == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (config.getLandscapeMobile() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"landscapeMobile\": ");

			sb.append("\"");

			sb.append(_escape(config.getLandscapeMobile()));

			sb.append("\"");
		}

		if (config.getPortraitMobile() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"portraitMobile\": ");

			sb.append("\"");

			sb.append(_escape(config.getPortraitMobile()));

			sb.append("\"");
		}

		if (config.getTablet() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tablet\": ");

			sb.append("\"");

			sb.append(_escape(config.getTablet()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ConfigJSONParser configJSONParser = new ConfigJSONParser();

		return configJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Config config) {
		if (config == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (config.getLandscapeMobile() == null) {
			map.put("landscapeMobile", null);
		}
		else {
			map.put(
				"landscapeMobile", String.valueOf(config.getLandscapeMobile()));
		}

		if (config.getPortraitMobile() == null) {
			map.put("portraitMobile", null);
		}
		else {
			map.put(
				"portraitMobile", String.valueOf(config.getPortraitMobile()));
		}

		if (config.getTablet() == null) {
			map.put("tablet", null);
		}
		else {
			map.put("tablet", String.valueOf(config.getTablet()));
		}

		return map;
	}

	public static class ConfigJSONParser extends BaseJSONParser<Config> {

		@Override
		protected Config createDTO() {
			return new Config();
		}

		@Override
		protected Config[] createDTOArray(int size) {
			return new Config[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "landscapeMobile")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "portraitMobile")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "tablet")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Config config, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "landscapeMobile")) {
				if (jsonParserFieldValue != null) {
					config.setLandscapeMobile((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "portraitMobile")) {
				if (jsonParserFieldValue != null) {
					config.setPortraitMobile((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "tablet")) {
				if (jsonParserFieldValue != null) {
					config.setTablet((String)jsonParserFieldValue);
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			sb.append(_toJSON(value));

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static String _toJSON(Object value) {
		if (value == null) {
			return "null";
		}

		if (value instanceof Map) {
			return _toJSON((Map)value);
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			StringBuilder sb = new StringBuilder("[");

			Object[] values = (Object[])value;

			for (int i = 0; i < values.length; i++) {
				sb.append(_toJSON(values[i]));

				if ((i + 1) < values.length) {
					sb.append(", ");
				}
			}

			sb.append("]");

			return sb.toString();
		}

		if (value instanceof String) {
			return "\"" + _escape(value) + "\"";
		}

		return String.valueOf(value);
	}

}