/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.MappedBackgroundImageValue;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
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
public class MappedBackgroundImageValueSerDes {

	public static MappedBackgroundImageValue toDTO(String json) {
		MappedBackgroundImageValueJSONParser
			mappedBackgroundImageValueJSONParser =
				new MappedBackgroundImageValueJSONParser();

		return mappedBackgroundImageValueJSONParser.parseToDTO(json);
	}

	public static MappedBackgroundImageValue[] toDTOs(String json) {
		MappedBackgroundImageValueJSONParser
			mappedBackgroundImageValueJSONParser =
				new MappedBackgroundImageValueJSONParser();

		return mappedBackgroundImageValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		MappedBackgroundImageValue mappedBackgroundImageValue) {

		if (mappedBackgroundImageValue == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (mappedBackgroundImageValue.getFragmentMappedValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentMappedValue\": ");

			sb.append(
				String.valueOf(
					mappedBackgroundImageValue.getFragmentMappedValue()));
		}

		if (mappedBackgroundImageValue.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(mappedBackgroundImageValue.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MappedBackgroundImageValueJSONParser
			mappedBackgroundImageValueJSONParser =
				new MappedBackgroundImageValueJSONParser();

		return mappedBackgroundImageValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		MappedBackgroundImageValue mappedBackgroundImageValue) {

		if (mappedBackgroundImageValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (mappedBackgroundImageValue.getFragmentMappedValue() == null) {
			map.put("fragmentMappedValue", null);
		}
		else {
			map.put(
				"fragmentMappedValue",
				String.valueOf(
					mappedBackgroundImageValue.getFragmentMappedValue()));
		}

		if (mappedBackgroundImageValue.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type", String.valueOf(mappedBackgroundImageValue.getType()));
		}

		return map;
	}

	public static class MappedBackgroundImageValueJSONParser
		extends BaseJSONParser<MappedBackgroundImageValue> {

		@Override
		protected MappedBackgroundImageValue createDTO() {
			return new MappedBackgroundImageValue();
		}

		@Override
		protected MappedBackgroundImageValue[] createDTOArray(int size) {
			return new MappedBackgroundImageValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "fragmentMappedValue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			MappedBackgroundImageValue mappedBackgroundImageValue,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "fragmentMappedValue")) {
				if (jsonParserFieldValue != null) {
					mappedBackgroundImageValue.setFragmentMappedValue(
						FragmentMappedValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					mappedBackgroundImageValue.setType(
						MappedBackgroundImageValue.Type.create(
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:800114812