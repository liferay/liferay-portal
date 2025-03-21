/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.LocalizationConfig;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class LocalizationConfigSerDes {

	public static LocalizationConfig toDTO(String json) {
		LocalizationConfigJSONParser localizationConfigJSONParser =
			new LocalizationConfigJSONParser();

		return localizationConfigJSONParser.parseToDTO(json);
	}

	public static LocalizationConfig[] toDTOs(String json) {
		LocalizationConfigJSONParser localizationConfigJSONParser =
			new LocalizationConfigJSONParser();

		return localizationConfigJSONParser.parseToDTOs(json);
	}

	public static String toJSON(LocalizationConfig localizationConfig) {
		if (localizationConfig == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (localizationConfig.getUnlocalizedFieldsMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unlocalizedFieldsMessage\": ");

			sb.append(
				String.valueOf(
					localizationConfig.getUnlocalizedFieldsMessage()));
		}

		if (localizationConfig.getUnlocalizedFieldsState() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unlocalizedFieldsState\": ");

			sb.append("\"");

			sb.append(localizationConfig.getUnlocalizedFieldsState());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		LocalizationConfigJSONParser localizationConfigJSONParser =
			new LocalizationConfigJSONParser();

		return localizationConfigJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		LocalizationConfig localizationConfig) {

		if (localizationConfig == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (localizationConfig.getUnlocalizedFieldsMessage() == null) {
			map.put("unlocalizedFieldsMessage", null);
		}
		else {
			map.put(
				"unlocalizedFieldsMessage",
				String.valueOf(
					localizationConfig.getUnlocalizedFieldsMessage()));
		}

		if (localizationConfig.getUnlocalizedFieldsState() == null) {
			map.put("unlocalizedFieldsState", null);
		}
		else {
			map.put(
				"unlocalizedFieldsState",
				String.valueOf(localizationConfig.getUnlocalizedFieldsState()));
		}

		return map;
	}

	public static class LocalizationConfigJSONParser
		extends BaseJSONParser<LocalizationConfig> {

		@Override
		protected LocalizationConfig createDTO() {
			return new LocalizationConfig();
		}

		@Override
		protected LocalizationConfig[] createDTOArray(int size) {
			return new LocalizationConfig[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "unlocalizedFieldsMessage")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "unlocalizedFieldsState")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			LocalizationConfig localizationConfig, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "unlocalizedFieldsMessage")) {

				if (jsonParserFieldValue != null) {
					localizationConfig.setUnlocalizedFieldsMessage(
						FragmentInlineValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "unlocalizedFieldsState")) {

				if (jsonParserFieldValue != null) {
					localizationConfig.setUnlocalizedFieldsState(
						LocalizationConfig.UnlocalizedFieldsState.create(
							(String)jsonParserFieldValue));
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