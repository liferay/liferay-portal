/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.BackgroundImageValue;
import com.liferay.headless.admin.site.client.dto.v1_0.DirectBackgroundImageValue;
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
public class BackgroundImageValueSerDes {

	public static BackgroundImageValue toDTO(String json) {
		BackgroundImageValueJSONParser backgroundImageValueJSONParser =
			new BackgroundImageValueJSONParser();

		return backgroundImageValueJSONParser.parseToDTO(json);
	}

	public static BackgroundImageValue[] toDTOs(String json) {
		BackgroundImageValueJSONParser backgroundImageValueJSONParser =
			new BackgroundImageValueJSONParser();

		return backgroundImageValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(BackgroundImageValue backgroundImageValue) {
		if (backgroundImageValue == null) {
			return "null";
		}

		BackgroundImageValue.Type type = backgroundImageValue.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("Direct")) {
				return DirectBackgroundImageValueSerDes.toJSON(
					(DirectBackgroundImageValue)backgroundImageValue);
			}

			if (typeString.equals("Mapped")) {
				return MappedBackgroundImageValueSerDes.toJSON(
					(MappedBackgroundImageValue)backgroundImageValue);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		BackgroundImageValueJSONParser backgroundImageValueJSONParser =
			new BackgroundImageValueJSONParser();

		return backgroundImageValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		BackgroundImageValue backgroundImageValue) {

		if (backgroundImageValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (backgroundImageValue.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(backgroundImageValue.getType()));
		}

		return map;
	}

	public static class BackgroundImageValueJSONParser
		extends BaseJSONParser<BackgroundImageValue> {

		@Override
		protected BackgroundImageValue createDTO() {
			return null;
		}

		@Override
		protected BackgroundImageValue[] createDTOArray(int size) {
			return new BackgroundImageValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		public BackgroundImageValue parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("Direct")) {
					return DirectBackgroundImageValue.toDTO(json);
				}

				if (typeString.equals("Mapped")) {
					return MappedBackgroundImageValue.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown type " + typeString);
			}
			else {
				throw new IllegalArgumentException("Missing type parameter");
			}
		}

		@Override
		protected void setField(
			BackgroundImageValue backgroundImageValue,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					backgroundImageValue.setType(
						BackgroundImageValue.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-222839254