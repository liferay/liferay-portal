/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.serdes.v1_0;

import com.liferay.headless.admin.fragment.client.dto.v1_0.CollectionSelectorTypeOptions;
import com.liferay.headless.admin.fragment.client.dto.v1_0.Dependency;
import com.liferay.headless.admin.fragment.client.json.BaseJSONParser;

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
public class CollectionSelectorTypeOptionsSerDes {

	public static CollectionSelectorTypeOptions toDTO(String json) {
		CollectionSelectorTypeOptionsJSONParser
			collectionSelectorTypeOptionsJSONParser =
				new CollectionSelectorTypeOptionsJSONParser();

		return collectionSelectorTypeOptionsJSONParser.parseToDTO(json);
	}

	public static CollectionSelectorTypeOptions[] toDTOs(String json) {
		CollectionSelectorTypeOptionsJSONParser
			collectionSelectorTypeOptionsJSONParser =
				new CollectionSelectorTypeOptionsJSONParser();

		return collectionSelectorTypeOptionsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		CollectionSelectorTypeOptions collectionSelectorTypeOptions) {

		if (collectionSelectorTypeOptions == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (collectionSelectorTypeOptions.getDependency() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dependency\": ");

			sb.append(_toJSON(collectionSelectorTypeOptions.getDependency()));
		}

		if (collectionSelectorTypeOptions.getItemSubtype() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemSubtype\": ");

			sb.append("\"");

			sb.append(_escape(collectionSelectorTypeOptions.getItemSubtype()));

			sb.append("\"");
		}

		if (collectionSelectorTypeOptions.getItemType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemType\": ");

			sb.append("\"");

			sb.append(_escape(collectionSelectorTypeOptions.getItemType()));

			sb.append("\"");
		}

		if (collectionSelectorTypeOptions.getNumberOfItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfItems\": ");

			sb.append(collectionSelectorTypeOptions.getNumberOfItems());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CollectionSelectorTypeOptionsJSONParser
			collectionSelectorTypeOptionsJSONParser =
				new CollectionSelectorTypeOptionsJSONParser();

		return collectionSelectorTypeOptionsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		CollectionSelectorTypeOptions collectionSelectorTypeOptions) {

		if (collectionSelectorTypeOptions == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (collectionSelectorTypeOptions.getDependency() == null) {
			map.put("dependency", null);
		}
		else {
			map.put(
				"dependency",
				String.valueOf(collectionSelectorTypeOptions.getDependency()));
		}

		if (collectionSelectorTypeOptions.getItemSubtype() == null) {
			map.put("itemSubtype", null);
		}
		else {
			map.put(
				"itemSubtype",
				String.valueOf(collectionSelectorTypeOptions.getItemSubtype()));
		}

		if (collectionSelectorTypeOptions.getItemType() == null) {
			map.put("itemType", null);
		}
		else {
			map.put(
				"itemType",
				String.valueOf(collectionSelectorTypeOptions.getItemType()));
		}

		if (collectionSelectorTypeOptions.getNumberOfItems() == null) {
			map.put("numberOfItems", null);
		}
		else {
			map.put(
				"numberOfItems",
				String.valueOf(
					collectionSelectorTypeOptions.getNumberOfItems()));
		}

		return map;
	}

	public static class CollectionSelectorTypeOptionsJSONParser
		extends BaseJSONParser<CollectionSelectorTypeOptions> {

		@Override
		protected CollectionSelectorTypeOptions createDTO() {
			return new CollectionSelectorTypeOptions();
		}

		@Override
		protected CollectionSelectorTypeOptions[] createDTOArray(int size) {
			return new CollectionSelectorTypeOptions[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dependency")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "itemSubtype")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "itemType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfItems")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CollectionSelectorTypeOptions collectionSelectorTypeOptions,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dependency")) {
				if (jsonParserFieldValue != null) {
					collectionSelectorTypeOptions.setDependency(
						(Map<String, Dependency>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "itemSubtype")) {
				if (jsonParserFieldValue != null) {
					collectionSelectorTypeOptions.setItemSubtype(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "itemType")) {
				if (jsonParserFieldValue != null) {
					collectionSelectorTypeOptions.setItemType(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfItems")) {
				if (jsonParserFieldValue != null) {
					collectionSelectorTypeOptions.setNumberOfItems(
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
// LIFERAY-REST-BUILDER-HASH:-984059749