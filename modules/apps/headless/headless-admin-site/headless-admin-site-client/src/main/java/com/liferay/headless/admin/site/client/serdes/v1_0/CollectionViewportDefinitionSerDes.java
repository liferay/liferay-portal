/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.CollectionViewportDefinition;
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
public class CollectionViewportDefinitionSerDes {

	public static CollectionViewportDefinition toDTO(String json) {
		CollectionViewportDefinitionJSONParser
			collectionViewportDefinitionJSONParser =
				new CollectionViewportDefinitionJSONParser();

		return collectionViewportDefinitionJSONParser.parseToDTO(json);
	}

	public static CollectionViewportDefinition[] toDTOs(String json) {
		CollectionViewportDefinitionJSONParser
			collectionViewportDefinitionJSONParser =
				new CollectionViewportDefinitionJSONParser();

		return collectionViewportDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		CollectionViewportDefinition collectionViewportDefinition) {

		if (collectionViewportDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (collectionViewportDefinition.getNumberOfColumns() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfColumns\": ");

			sb.append(collectionViewportDefinition.getNumberOfColumns());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CollectionViewportDefinitionJSONParser
			collectionViewportDefinitionJSONParser =
				new CollectionViewportDefinitionJSONParser();

		return collectionViewportDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		CollectionViewportDefinition collectionViewportDefinition) {

		if (collectionViewportDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (collectionViewportDefinition.getNumberOfColumns() == null) {
			map.put("numberOfColumns", null);
		}
		else {
			map.put(
				"numberOfColumns",
				String.valueOf(
					collectionViewportDefinition.getNumberOfColumns()));
		}

		return map;
	}

	public static class CollectionViewportDefinitionJSONParser
		extends BaseJSONParser<CollectionViewportDefinition> {

		@Override
		protected CollectionViewportDefinition createDTO() {
			return new CollectionViewportDefinition();
		}

		@Override
		protected CollectionViewportDefinition[] createDTOArray(int size) {
			return new CollectionViewportDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "numberOfColumns")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CollectionViewportDefinition collectionViewportDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "numberOfColumns")) {
				if (jsonParserFieldValue != null) {
					collectionViewportDefinition.setNumberOfColumns(
						Integer.valueOf((String)jsonParserFieldValue));
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