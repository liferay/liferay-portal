/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.client.serdes.v1_0;

import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyCategoryProperty;
import com.liferay.headless.admin.taxonomy.client.json.BaseJSONParser;

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
public class TaxonomyCategoryPropertySerDes {

	public static TaxonomyCategoryProperty toDTO(String json) {
		TaxonomyCategoryPropertyJSONParser taxonomyCategoryPropertyJSONParser =
			new TaxonomyCategoryPropertyJSONParser();

		return taxonomyCategoryPropertyJSONParser.parseToDTO(json);
	}

	public static TaxonomyCategoryProperty[] toDTOs(String json) {
		TaxonomyCategoryPropertyJSONParser taxonomyCategoryPropertyJSONParser =
			new TaxonomyCategoryPropertyJSONParser();

		return taxonomyCategoryPropertyJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		TaxonomyCategoryProperty taxonomyCategoryProperty) {

		if (taxonomyCategoryProperty == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (taxonomyCategoryProperty.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(taxonomyCategoryProperty.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (taxonomyCategoryProperty.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyCategoryProperty.getKey()));

			sb.append("\"");
		}

		if (taxonomyCategoryProperty.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyCategoryProperty.getValue()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TaxonomyCategoryPropertyJSONParser taxonomyCategoryPropertyJSONParser =
			new TaxonomyCategoryPropertyJSONParser();

		return taxonomyCategoryPropertyJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		TaxonomyCategoryProperty taxonomyCategoryProperty) {

		if (taxonomyCategoryProperty == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (taxonomyCategoryProperty.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					taxonomyCategoryProperty.getExternalReferenceCode()));
		}

		if (taxonomyCategoryProperty.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(taxonomyCategoryProperty.getKey()));
		}

		if (taxonomyCategoryProperty.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put(
				"value", String.valueOf(taxonomyCategoryProperty.getValue()));
		}

		return map;
	}

	public static class TaxonomyCategoryPropertyJSONParser
		extends BaseJSONParser<TaxonomyCategoryProperty> {

		@Override
		protected TaxonomyCategoryProperty createDTO() {
			return new TaxonomyCategoryProperty();
		}

		@Override
		protected TaxonomyCategoryProperty[] createDTOArray(int size) {
			return new TaxonomyCategoryProperty[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TaxonomyCategoryProperty taxonomyCategoryProperty,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryProperty.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryProperty.setKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryProperty.setValue(
						(String)jsonParserFieldValue);
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