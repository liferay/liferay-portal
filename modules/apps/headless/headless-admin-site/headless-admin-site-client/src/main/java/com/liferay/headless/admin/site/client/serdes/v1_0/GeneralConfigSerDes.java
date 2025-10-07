/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.GeneralConfig;
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
public class GeneralConfigSerDes {

	public static GeneralConfig toDTO(String json) {
		GeneralConfigJSONParser generalConfigJSONParser =
			new GeneralConfigJSONParser();

		return generalConfigJSONParser.parseToDTO(json);
	}

	public static GeneralConfig[] toDTOs(String json) {
		GeneralConfigJSONParser generalConfigJSONParser =
			new GeneralConfigJSONParser();

		return generalConfigJSONParser.parseToDTOs(json);
	}

	public static String toJSON(GeneralConfig generalConfig) {
		if (generalConfig == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (generalConfig.getApplicationDecorator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"applicationDecorator\": ");

			sb.append("\"");

			sb.append(generalConfig.getApplicationDecorator());

			sb.append("\"");
		}

		if (generalConfig.getCustomTitle_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customTitle_i18n\": ");

			sb.append(_toJSON(generalConfig.getCustomTitle_i18n()));
		}

		if (generalConfig.getUseCustomTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"useCustomTitle\": ");

			sb.append(generalConfig.getUseCustomTitle());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		GeneralConfigJSONParser generalConfigJSONParser =
			new GeneralConfigJSONParser();

		return generalConfigJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(GeneralConfig generalConfig) {
		if (generalConfig == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (generalConfig.getApplicationDecorator() == null) {
			map.put("applicationDecorator", null);
		}
		else {
			map.put(
				"applicationDecorator",
				String.valueOf(generalConfig.getApplicationDecorator()));
		}

		if (generalConfig.getCustomTitle_i18n() == null) {
			map.put("customTitle_i18n", null);
		}
		else {
			map.put(
				"customTitle_i18n",
				String.valueOf(generalConfig.getCustomTitle_i18n()));
		}

		if (generalConfig.getUseCustomTitle() == null) {
			map.put("useCustomTitle", null);
		}
		else {
			map.put(
				"useCustomTitle",
				String.valueOf(generalConfig.getUseCustomTitle()));
		}

		return map;
	}

	public static class GeneralConfigJSONParser
		extends BaseJSONParser<GeneralConfig> {

		@Override
		protected GeneralConfig createDTO() {
			return new GeneralConfig();
		}

		@Override
		protected GeneralConfig[] createDTOArray(int size) {
			return new GeneralConfig[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "applicationDecorator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customTitle_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "useCustomTitle")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			GeneralConfig generalConfig, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "applicationDecorator")) {
				if (jsonParserFieldValue != null) {
					generalConfig.setApplicationDecorator(
						GeneralConfig.ApplicationDecorator.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customTitle_i18n")) {
				if (jsonParserFieldValue != null) {
					generalConfig.setCustomTitle_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "useCustomTitle")) {
				if (jsonParserFieldValue != null) {
					generalConfig.setUseCustomTitle(
						(Boolean)jsonParserFieldValue);
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