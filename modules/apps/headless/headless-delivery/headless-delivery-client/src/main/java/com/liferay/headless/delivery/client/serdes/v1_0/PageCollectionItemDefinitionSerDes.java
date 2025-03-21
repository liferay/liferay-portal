/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.PageCollectionItemDefinition;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class PageCollectionItemDefinitionSerDes {

	public static PageCollectionItemDefinition toDTO(String json) {
		PageCollectionItemDefinitionJSONParser
			pageCollectionItemDefinitionJSONParser =
				new PageCollectionItemDefinitionJSONParser();

		return pageCollectionItemDefinitionJSONParser.parseToDTO(json);
	}

	public static PageCollectionItemDefinition[] toDTOs(String json) {
		PageCollectionItemDefinitionJSONParser
			pageCollectionItemDefinitionJSONParser =
				new PageCollectionItemDefinitionJSONParser();

		return pageCollectionItemDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PageCollectionItemDefinition pageCollectionItemDefinition) {

		if (pageCollectionItemDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageCollectionItemDefinition.getCollectionItemConfig() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionItemConfig\": ");

			if (
					pageCollectionItemDefinition.
						getCollectionItemConfig() instanceof String) {

				sb.append("\"");
				sb.append(
					(String)
						pageCollectionItemDefinition.getCollectionItemConfig());
				sb.append("\"");
			}
			else {
				sb.append(
					pageCollectionItemDefinition.getCollectionItemConfig());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageCollectionItemDefinitionJSONParser
			pageCollectionItemDefinitionJSONParser =
				new PageCollectionItemDefinitionJSONParser();

		return pageCollectionItemDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PageCollectionItemDefinition pageCollectionItemDefinition) {

		if (pageCollectionItemDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageCollectionItemDefinition.getCollectionItemConfig() == null) {
			map.put("collectionItemConfig", null);
		}
		else {
			map.put(
				"collectionItemConfig",
				String.valueOf(
					pageCollectionItemDefinition.getCollectionItemConfig()));
		}

		return map;
	}

	public static class PageCollectionItemDefinitionJSONParser
		extends BaseJSONParser<PageCollectionItemDefinition> {

		@Override
		protected PageCollectionItemDefinition createDTO() {
			return new PageCollectionItemDefinition();
		}

		@Override
		protected PageCollectionItemDefinition[] createDTOArray(int size) {
			return new PageCollectionItemDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "collectionItemConfig")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageCollectionItemDefinition pageCollectionItemDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "collectionItemConfig")) {
				if (jsonParserFieldValue != null) {
					pageCollectionItemDefinition.setCollectionItemConfig(
						(Object)jsonParserFieldValue);
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