/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.Fragment;
import com.liferay.headless.delivery.client.dto.v1_0.FragmentSettingsAllowed;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FragmentSettingsAllowedSerDes {

	public static FragmentSettingsAllowed toDTO(String json) {
		FragmentSettingsAllowedJSONParser fragmentSettingsAllowedJSONParser =
			new FragmentSettingsAllowedJSONParser();

		return fragmentSettingsAllowedJSONParser.parseToDTO(json);
	}

	public static FragmentSettingsAllowed[] toDTOs(String json) {
		FragmentSettingsAllowedJSONParser fragmentSettingsAllowedJSONParser =
			new FragmentSettingsAllowedJSONParser();

		return fragmentSettingsAllowedJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		FragmentSettingsAllowed fragmentSettingsAllowed) {

		if (fragmentSettingsAllowed == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (fragmentSettingsAllowed.getAllowedFragments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"allowedFragments\": ");

			sb.append("[");

			for (int i = 0;
				 i < fragmentSettingsAllowed.getAllowedFragments().length;
				 i++) {

				sb.append(
					String.valueOf(
						fragmentSettingsAllowed.getAllowedFragments()[i]));

				if ((i + 1) <
						fragmentSettingsAllowed.getAllowedFragments().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentSettingsAllowedJSONParser fragmentSettingsAllowedJSONParser =
			new FragmentSettingsAllowedJSONParser();

		return fragmentSettingsAllowedJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FragmentSettingsAllowed fragmentSettingsAllowed) {

		if (fragmentSettingsAllowed == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentSettingsAllowed.getAllowedFragments() == null) {
			map.put("allowedFragments", null);
		}
		else {
			map.put(
				"allowedFragments",
				String.valueOf(fragmentSettingsAllowed.getAllowedFragments()));
		}

		return map;
	}

	public static class FragmentSettingsAllowedJSONParser
		extends BaseJSONParser<FragmentSettingsAllowed> {

		@Override
		protected FragmentSettingsAllowed createDTO() {
			return new FragmentSettingsAllowed();
		}

		@Override
		protected FragmentSettingsAllowed[] createDTOArray(int size) {
			return new FragmentSettingsAllowed[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "allowedFragments")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FragmentSettingsAllowed fragmentSettingsAllowed,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "allowedFragments")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Fragment[] allowedFragmentsArray =
						new Fragment[jsonParserFieldValues.length];

					for (int i = 0; i < allowedFragmentsArray.length; i++) {
						allowedFragmentsArray[i] = FragmentSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					fragmentSettingsAllowed.setAllowedFragments(
						allowedFragmentsArray);
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