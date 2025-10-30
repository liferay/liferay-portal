/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.CollectionDisplayPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionDisplayViewport;
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
public class CollectionDisplayPageElementDefinitionSerDes {

	public static CollectionDisplayPageElementDefinition toDTO(String json) {
		CollectionDisplayPageElementDefinitionJSONParser
			collectionDisplayPageElementDefinitionJSONParser =
				new CollectionDisplayPageElementDefinitionJSONParser();

		return collectionDisplayPageElementDefinitionJSONParser.parseToDTO(
			json);
	}

	public static CollectionDisplayPageElementDefinition[] toDTOs(String json) {
		CollectionDisplayPageElementDefinitionJSONParser
			collectionDisplayPageElementDefinitionJSONParser =
				new CollectionDisplayPageElementDefinitionJSONParser();

		return collectionDisplayPageElementDefinitionJSONParser.parseToDTOs(
			json);
	}

	public static String toJSON(
		CollectionDisplayPageElementDefinition
			collectionDisplayPageElementDefinition) {

		if (collectionDisplayPageElementDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (collectionDisplayPageElementDefinition.
				getCollectionDisplayListStyle() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionDisplayListStyle\": ");

			sb.append(
				String.valueOf(
					collectionDisplayPageElementDefinition.
						getCollectionDisplayListStyle()));
		}

		if (collectionDisplayPageElementDefinition.
				getCollectionDisplayViewports() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionDisplayViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < collectionDisplayPageElementDefinition.
					 getCollectionDisplayViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						collectionDisplayPageElementDefinition.
							getCollectionDisplayViewports()[i]));

				if ((i + 1) < collectionDisplayPageElementDefinition.
						getCollectionDisplayViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (collectionDisplayPageElementDefinition.getCollectionSettings() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionSettings\": ");

			sb.append(
				String.valueOf(
					collectionDisplayPageElementDefinition.
						getCollectionSettings()));
		}

		if (collectionDisplayPageElementDefinition.getDisplayAllItems() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayAllItems\": ");

			sb.append(
				collectionDisplayPageElementDefinition.getDisplayAllItems());
		}

		if (collectionDisplayPageElementDefinition.getDisplayAllPages() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayAllPages\": ");

			sb.append(
				collectionDisplayPageElementDefinition.getDisplayAllPages());
		}

		if (collectionDisplayPageElementDefinition.getEmptyCollectionConfig() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emptyCollectionConfig\": ");

			sb.append(
				String.valueOf(
					collectionDisplayPageElementDefinition.
						getEmptyCollectionConfig()));
		}

		if (collectionDisplayPageElementDefinition.getHidden() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hidden\": ");

			sb.append(collectionDisplayPageElementDefinition.getHidden());
		}

		if (collectionDisplayPageElementDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(
				_escape(collectionDisplayPageElementDefinition.getName()));

			sb.append("\"");
		}

		if (collectionDisplayPageElementDefinition.getNumberOfItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfItems\": ");

			sb.append(
				collectionDisplayPageElementDefinition.getNumberOfItems());
		}

		if (collectionDisplayPageElementDefinition.getNumberOfItemsPerPage() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfItemsPerPage\": ");

			sb.append(
				collectionDisplayPageElementDefinition.
					getNumberOfItemsPerPage());
		}

		if (collectionDisplayPageElementDefinition.getNumberOfPages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfPages\": ");

			sb.append(
				collectionDisplayPageElementDefinition.getNumberOfPages());
		}

		if (collectionDisplayPageElementDefinition.getPaginationType() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paginationType\": ");

			sb.append("\"");
			sb.append(
				collectionDisplayPageElementDefinition.getPaginationType());
			sb.append("\"");
		}

		if (collectionDisplayPageElementDefinition.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(collectionDisplayPageElementDefinition.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CollectionDisplayPageElementDefinitionJSONParser
			collectionDisplayPageElementDefinitionJSONParser =
				new CollectionDisplayPageElementDefinitionJSONParser();

		return collectionDisplayPageElementDefinitionJSONParser.parseToMap(
			json);
	}

	public static Map<String, String> toMap(
		CollectionDisplayPageElementDefinition
			collectionDisplayPageElementDefinition) {

		if (collectionDisplayPageElementDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (collectionDisplayPageElementDefinition.
				getCollectionDisplayListStyle() == null) {

			map.put("collectionDisplayListStyle", null);
		}
		else {
			map.put(
				"collectionDisplayListStyle",
				String.valueOf(
					collectionDisplayPageElementDefinition.
						getCollectionDisplayListStyle()));
		}

		if (collectionDisplayPageElementDefinition.
				getCollectionDisplayViewports() == null) {

			map.put("collectionDisplayViewports", null);
		}
		else {
			map.put(
				"collectionDisplayViewports",
				String.valueOf(
					collectionDisplayPageElementDefinition.
						getCollectionDisplayViewports()));
		}

		if (collectionDisplayPageElementDefinition.getCollectionSettings() ==
				null) {

			map.put("collectionSettings", null);
		}
		else {
			map.put(
				"collectionSettings",
				String.valueOf(
					collectionDisplayPageElementDefinition.
						getCollectionSettings()));
		}

		if (collectionDisplayPageElementDefinition.getDisplayAllItems() ==
				null) {

			map.put("displayAllItems", null);
		}
		else {
			map.put(
				"displayAllItems",
				String.valueOf(
					collectionDisplayPageElementDefinition.
						getDisplayAllItems()));
		}

		if (collectionDisplayPageElementDefinition.getDisplayAllPages() ==
				null) {

			map.put("displayAllPages", null);
		}
		else {
			map.put(
				"displayAllPages",
				String.valueOf(
					collectionDisplayPageElementDefinition.
						getDisplayAllPages()));
		}

		if (collectionDisplayPageElementDefinition.getEmptyCollectionConfig() ==
				null) {

			map.put("emptyCollectionConfig", null);
		}
		else {
			map.put(
				"emptyCollectionConfig",
				String.valueOf(
					collectionDisplayPageElementDefinition.
						getEmptyCollectionConfig()));
		}

		if (collectionDisplayPageElementDefinition.getHidden() == null) {
			map.put("hidden", null);
		}
		else {
			map.put(
				"hidden",
				String.valueOf(
					collectionDisplayPageElementDefinition.getHidden()));
		}

		if (collectionDisplayPageElementDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(
					collectionDisplayPageElementDefinition.getName()));
		}

		if (collectionDisplayPageElementDefinition.getNumberOfItems() == null) {
			map.put("numberOfItems", null);
		}
		else {
			map.put(
				"numberOfItems",
				String.valueOf(
					collectionDisplayPageElementDefinition.getNumberOfItems()));
		}

		if (collectionDisplayPageElementDefinition.getNumberOfItemsPerPage() ==
				null) {

			map.put("numberOfItemsPerPage", null);
		}
		else {
			map.put(
				"numberOfItemsPerPage",
				String.valueOf(
					collectionDisplayPageElementDefinition.
						getNumberOfItemsPerPage()));
		}

		if (collectionDisplayPageElementDefinition.getNumberOfPages() == null) {
			map.put("numberOfPages", null);
		}
		else {
			map.put(
				"numberOfPages",
				String.valueOf(
					collectionDisplayPageElementDefinition.getNumberOfPages()));
		}

		if (collectionDisplayPageElementDefinition.getPaginationType() ==
				null) {

			map.put("paginationType", null);
		}
		else {
			map.put(
				"paginationType",
				String.valueOf(
					collectionDisplayPageElementDefinition.
						getPaginationType()));
		}

		if (collectionDisplayPageElementDefinition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(
					collectionDisplayPageElementDefinition.getType()));
		}

		return map;
	}

	public static class CollectionDisplayPageElementDefinitionJSONParser
		extends BaseJSONParser<CollectionDisplayPageElementDefinition> {

		@Override
		protected CollectionDisplayPageElementDefinition createDTO() {
			return new CollectionDisplayPageElementDefinition();
		}

		@Override
		protected CollectionDisplayPageElementDefinition[] createDTOArray(
			int size) {

			return new CollectionDisplayPageElementDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "collectionDisplayListStyle")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "collectionDisplayViewports")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "collectionSettings")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "displayAllItems")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "displayAllPages")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "emptyCollectionConfig")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "hidden")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfItems")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfItemsPerPage")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfPages")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "paginationType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CollectionDisplayPageElementDefinition
				collectionDisplayPageElementDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "collectionDisplayListStyle")) {

				if (jsonParserFieldValue != null) {
					collectionDisplayPageElementDefinition.
						setCollectionDisplayListStyle(
							CollectionDisplayListStyleSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "collectionDisplayViewports")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					CollectionDisplayViewport[]
						collectionDisplayViewportsArray =
							new CollectionDisplayViewport
								[jsonParserFieldValues.length];

					for (int i = 0; i < collectionDisplayViewportsArray.length;
						 i++) {

						collectionDisplayViewportsArray[i] =
							CollectionDisplayViewportSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					collectionDisplayPageElementDefinition.
						setCollectionDisplayViewports(
							collectionDisplayViewportsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "collectionSettings")) {

				if (jsonParserFieldValue != null) {
					collectionDisplayPageElementDefinition.
						setCollectionSettings(
							CollectionSettingsSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayAllItems")) {
				if (jsonParserFieldValue != null) {
					collectionDisplayPageElementDefinition.setDisplayAllItems(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayAllPages")) {
				if (jsonParserFieldValue != null) {
					collectionDisplayPageElementDefinition.setDisplayAllPages(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "emptyCollectionConfig")) {

				if (jsonParserFieldValue != null) {
					collectionDisplayPageElementDefinition.
						setEmptyCollectionConfig(
							EmptyCollectionConfigSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "hidden")) {
				if (jsonParserFieldValue != null) {
					collectionDisplayPageElementDefinition.setHidden(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					collectionDisplayPageElementDefinition.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfItems")) {
				if (jsonParserFieldValue != null) {
					collectionDisplayPageElementDefinition.setNumberOfItems(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfItemsPerPage")) {

				if (jsonParserFieldValue != null) {
					collectionDisplayPageElementDefinition.
						setNumberOfItemsPerPage(
							Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfPages")) {
				if (jsonParserFieldValue != null) {
					collectionDisplayPageElementDefinition.setNumberOfPages(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paginationType")) {
				if (jsonParserFieldValue != null) {
					collectionDisplayPageElementDefinition.setPaginationType(
						CollectionDisplayPageElementDefinition.PaginationType.
							create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					collectionDisplayPageElementDefinition.setType(
						CollectionDisplayPageElementDefinition.Type.create(
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