/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.CollectionDisplayViewportDefinition;
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
public class CollectionDisplayViewportDefinitionSerDes {

	public static CollectionDisplayViewportDefinition toDTO(String json) {
		CollectionDisplayViewportDefinitionJSONParser
			collectionDisplayViewportDefinitionJSONParser =
				new CollectionDisplayViewportDefinitionJSONParser();

		return collectionDisplayViewportDefinitionJSONParser.parseToDTO(json);
	}

	public static CollectionDisplayViewportDefinition[] toDTOs(String json) {
		CollectionDisplayViewportDefinitionJSONParser
			collectionDisplayViewportDefinitionJSONParser =
				new CollectionDisplayViewportDefinitionJSONParser();

		return collectionDisplayViewportDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		CollectionDisplayViewportDefinition
			collectionDisplayViewportDefinition) {

		if (collectionDisplayViewportDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (collectionDisplayViewportDefinition.getAlign() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"align\": ");

			sb.append("\"");
			sb.append(collectionDisplayViewportDefinition.getAlign());
			sb.append("\"");
		}

		if (collectionDisplayViewportDefinition.getFlexWrap() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"flexWrap\": ");

			sb.append("\"");
			sb.append(collectionDisplayViewportDefinition.getFlexWrap());
			sb.append("\"");
		}

		if (collectionDisplayViewportDefinition.getHidden() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hidden\": ");

			sb.append(collectionDisplayViewportDefinition.getHidden());
		}

		if (collectionDisplayViewportDefinition.getJustify() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"justify\": ");

			sb.append("\"");
			sb.append(collectionDisplayViewportDefinition.getJustify());
			sb.append("\"");
		}

		if (collectionDisplayViewportDefinition.getNumberOfColumns() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfColumns\": ");

			sb.append(collectionDisplayViewportDefinition.getNumberOfColumns());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CollectionDisplayViewportDefinitionJSONParser
			collectionDisplayViewportDefinitionJSONParser =
				new CollectionDisplayViewportDefinitionJSONParser();

		return collectionDisplayViewportDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		CollectionDisplayViewportDefinition
			collectionDisplayViewportDefinition) {

		if (collectionDisplayViewportDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (collectionDisplayViewportDefinition.getAlign() == null) {
			map.put("align", null);
		}
		else {
			map.put(
				"align",
				String.valueOf(collectionDisplayViewportDefinition.getAlign()));
		}

		if (collectionDisplayViewportDefinition.getFlexWrap() == null) {
			map.put("flexWrap", null);
		}
		else {
			map.put(
				"flexWrap",
				String.valueOf(
					collectionDisplayViewportDefinition.getFlexWrap()));
		}

		if (collectionDisplayViewportDefinition.getHidden() == null) {
			map.put("hidden", null);
		}
		else {
			map.put(
				"hidden",
				String.valueOf(
					collectionDisplayViewportDefinition.getHidden()));
		}

		if (collectionDisplayViewportDefinition.getJustify() == null) {
			map.put("justify", null);
		}
		else {
			map.put(
				"justify",
				String.valueOf(
					collectionDisplayViewportDefinition.getJustify()));
		}

		if (collectionDisplayViewportDefinition.getNumberOfColumns() == null) {
			map.put("numberOfColumns", null);
		}
		else {
			map.put(
				"numberOfColumns",
				String.valueOf(
					collectionDisplayViewportDefinition.getNumberOfColumns()));
		}

		return map;
	}

	public static class CollectionDisplayViewportDefinitionJSONParser
		extends BaseJSONParser<CollectionDisplayViewportDefinition> {

		@Override
		protected CollectionDisplayViewportDefinition createDTO() {
			return new CollectionDisplayViewportDefinition();
		}

		@Override
		protected CollectionDisplayViewportDefinition[] createDTOArray(
			int size) {

			return new CollectionDisplayViewportDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "align")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "flexWrap")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "hidden")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "justify")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfColumns")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CollectionDisplayViewportDefinition
				collectionDisplayViewportDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "align")) {
				if (jsonParserFieldValue != null) {
					collectionDisplayViewportDefinition.setAlign(
						CollectionDisplayViewportDefinition.Align.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "flexWrap")) {
				if (jsonParserFieldValue != null) {
					collectionDisplayViewportDefinition.setFlexWrap(
						CollectionDisplayViewportDefinition.FlexWrap.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "hidden")) {
				if (jsonParserFieldValue != null) {
					collectionDisplayViewportDefinition.setHidden(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "justify")) {
				if (jsonParserFieldValue != null) {
					collectionDisplayViewportDefinition.setJustify(
						CollectionDisplayViewportDefinition.Justify.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfColumns")) {
				if (jsonParserFieldValue != null) {
					collectionDisplayViewportDefinition.setNumberOfColumns(
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