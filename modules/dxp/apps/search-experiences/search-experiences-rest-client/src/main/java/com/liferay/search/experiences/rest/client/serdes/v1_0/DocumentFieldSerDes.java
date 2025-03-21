/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.DocumentField;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class DocumentFieldSerDes {

	public static DocumentField toDTO(String json) {
		DocumentFieldJSONParser documentFieldJSONParser =
			new DocumentFieldJSONParser();

		return documentFieldJSONParser.parseToDTO(json);
	}

	public static DocumentField[] toDTOs(String json) {
		DocumentFieldJSONParser documentFieldJSONParser =
			new DocumentFieldJSONParser();

		return documentFieldJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DocumentField documentField) {
		if (documentField == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (documentField.getValues() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"values\": ");

			sb.append("[");

			for (int i = 0; i < documentField.getValues().length; i++) {
				sb.append(_toJSON(documentField.getValues()[i]));

				if ((i + 1) < documentField.getValues().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DocumentFieldJSONParser documentFieldJSONParser =
			new DocumentFieldJSONParser();

		return documentFieldJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DocumentField documentField) {
		if (documentField == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (documentField.getValues() == null) {
			map.put("values", null);
		}
		else {
			map.put("values", String.valueOf(documentField.getValues()));
		}

		return map;
	}

	public static class DocumentFieldJSONParser
		extends BaseJSONParser<DocumentField> {

		@Override
		protected DocumentField createDTO() {
			return new DocumentField();
		}

		@Override
		protected DocumentField[] createDTOArray(int size) {
			return new DocumentField[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "values")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			DocumentField documentField, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "values")) {
				if (jsonParserFieldValue != null) {
					documentField.setValues((Object[])jsonParserFieldValue);
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