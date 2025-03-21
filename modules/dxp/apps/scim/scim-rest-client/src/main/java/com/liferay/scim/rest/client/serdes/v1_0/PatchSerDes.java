/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.Patch;
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
public class PatchSerDes {

	public static Patch toDTO(String json) {
		PatchJSONParser patchJSONParser = new PatchJSONParser();

		return patchJSONParser.parseToDTO(json);
	}

	public static Patch[] toDTOs(String json) {
		PatchJSONParser patchJSONParser = new PatchJSONParser();

		return patchJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Patch patch) {
		if (patch == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (patch.getSupported() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"supported\": ");

			sb.append(patch.getSupported());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PatchJSONParser patchJSONParser = new PatchJSONParser();

		return patchJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Patch patch) {
		if (patch == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (patch.getSupported() == null) {
			map.put("supported", null);
		}
		else {
			map.put("supported", String.valueOf(patch.getSupported()));
		}

		return map;
	}

	public static class PatchJSONParser extends BaseJSONParser<Patch> {

		@Override
		protected Patch createDTO() {
			return new Patch();
		}

		@Override
		protected Patch[] createDTOArray(int size) {
			return new Patch[size];
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
			Patch patch, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "supported")) {
				if (jsonParserFieldValue != null) {
					patch.setSupported((Boolean)jsonParserFieldValue);
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