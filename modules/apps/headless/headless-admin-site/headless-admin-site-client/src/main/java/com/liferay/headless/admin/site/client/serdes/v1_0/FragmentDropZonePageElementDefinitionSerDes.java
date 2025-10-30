/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FragmentDropZonePageElementDefinition;
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
public class FragmentDropZonePageElementDefinitionSerDes {

	public static FragmentDropZonePageElementDefinition toDTO(String json) {
		FragmentDropZonePageElementDefinitionJSONParser
			fragmentDropZonePageElementDefinitionJSONParser =
				new FragmentDropZonePageElementDefinitionJSONParser();

		return fragmentDropZonePageElementDefinitionJSONParser.parseToDTO(json);
	}

	public static FragmentDropZonePageElementDefinition[] toDTOs(String json) {
		FragmentDropZonePageElementDefinitionJSONParser
			fragmentDropZonePageElementDefinitionJSONParser =
				new FragmentDropZonePageElementDefinitionJSONParser();

		return fragmentDropZonePageElementDefinitionJSONParser.parseToDTOs(
			json);
	}

	public static String toJSON(
		FragmentDropZonePageElementDefinition
			fragmentDropZonePageElementDefinition) {

		if (fragmentDropZonePageElementDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (fragmentDropZonePageElementDefinition.getFragmentDropZoneId() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentDropZoneId\": ");

			sb.append("\"");

			sb.append(
				_escape(
					fragmentDropZonePageElementDefinition.
						getFragmentDropZoneId()));

			sb.append("\"");
		}

		if (fragmentDropZonePageElementDefinition.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(fragmentDropZonePageElementDefinition.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentDropZonePageElementDefinitionJSONParser
			fragmentDropZonePageElementDefinitionJSONParser =
				new FragmentDropZonePageElementDefinitionJSONParser();

		return fragmentDropZonePageElementDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FragmentDropZonePageElementDefinition
			fragmentDropZonePageElementDefinition) {

		if (fragmentDropZonePageElementDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentDropZonePageElementDefinition.getFragmentDropZoneId() ==
				null) {

			map.put("fragmentDropZoneId", null);
		}
		else {
			map.put(
				"fragmentDropZoneId",
				String.valueOf(
					fragmentDropZonePageElementDefinition.
						getFragmentDropZoneId()));
		}

		if (fragmentDropZonePageElementDefinition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(
					fragmentDropZonePageElementDefinition.getType()));
		}

		return map;
	}

	public static class FragmentDropZonePageElementDefinitionJSONParser
		extends BaseJSONParser<FragmentDropZonePageElementDefinition> {

		@Override
		protected FragmentDropZonePageElementDefinition createDTO() {
			return new FragmentDropZonePageElementDefinition();
		}

		@Override
		protected FragmentDropZonePageElementDefinition[] createDTOArray(
			int size) {

			return new FragmentDropZonePageElementDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "fragmentDropZoneId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FragmentDropZonePageElementDefinition
				fragmentDropZonePageElementDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "fragmentDropZoneId")) {
				if (jsonParserFieldValue != null) {
					fragmentDropZonePageElementDefinition.setFragmentDropZoneId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					fragmentDropZonePageElementDefinition.setType(
						FragmentDropZonePageElementDefinition.Type.create(
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