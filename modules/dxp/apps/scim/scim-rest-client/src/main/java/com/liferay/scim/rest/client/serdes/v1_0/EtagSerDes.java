/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.Etag;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class EtagSerDes {

	public static Etag toDTO(String json) {
		EtagJSONParser etagJSONParser = new EtagJSONParser();

		return etagJSONParser.parseToDTO(json);
	}

	public static Etag[] toDTOs(String json) {
		EtagJSONParser etagJSONParser = new EtagJSONParser();

		return etagJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Etag etag) {
		if (etag == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (etag.getSupported() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"supported\": ");

			sb.append(etag.getSupported());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		EtagJSONParser etagJSONParser = new EtagJSONParser();

		return etagJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Etag etag) {
		if (etag == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (etag.getSupported() == null) {
			map.put("supported", null);
		}
		else {
			map.put("supported", String.valueOf(etag.getSupported()));
		}

		return map;
	}

	public static class EtagJSONParser extends BaseJSONParser<Etag> {

		@Override
		protected Etag createDTO() {
			return new Etag();
		}

		@Override
		protected Etag[] createDTOArray(int size) {
			return new Etag[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "supported")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Etag etag, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "supported")) {
				if (jsonParserFieldValue != null) {
					etag.setSupported((Boolean)jsonParserFieldValue);
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