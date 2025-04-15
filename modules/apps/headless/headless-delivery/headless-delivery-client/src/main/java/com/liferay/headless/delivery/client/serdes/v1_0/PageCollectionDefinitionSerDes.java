/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.CollectionViewport;
import com.liferay.headless.delivery.client.dto.v1_0.FragmentViewport;
import com.liferay.headless.delivery.client.dto.v1_0.PageCollectionDefinition;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

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
public class PageCollectionDefinitionSerDes {

	public static PageCollectionDefinition toDTO(String json) {
		PageCollectionDefinitionJSONParser pageCollectionDefinitionJSONParser =
			new PageCollectionDefinitionJSONParser();

		return pageCollectionDefinitionJSONParser.parseToDTO(json);
	}

	public static PageCollectionDefinition[] toDTOs(String json) {
		PageCollectionDefinitionJSONParser pageCollectionDefinitionJSONParser =
			new PageCollectionDefinitionJSONParser();

		return pageCollectionDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PageCollectionDefinition pageCollectionDefinition) {

		if (pageCollectionDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageCollectionDefinition.getCollectionConfig() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionConfig\": ");

			sb.append(
				String.valueOf(pageCollectionDefinition.getCollectionConfig()));
		}

		if (pageCollectionDefinition.getCollectionViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < pageCollectionDefinition.getCollectionViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						pageCollectionDefinition.getCollectionViewports()[i]));

				if ((i + 1) <
						pageCollectionDefinition.
							getCollectionViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageCollectionDefinition.getDisplayAllItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayAllItems\": ");

			sb.append(pageCollectionDefinition.getDisplayAllItems());
		}

		if (pageCollectionDefinition.getDisplayAllPages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayAllPages\": ");

			sb.append(pageCollectionDefinition.getDisplayAllPages());
		}

		if (pageCollectionDefinition.getEmptyCollectionConfig() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emptyCollectionConfig\": ");

			sb.append(
				String.valueOf(
					pageCollectionDefinition.getEmptyCollectionConfig()));
		}

		if (pageCollectionDefinition.getFragmentStyle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentStyle\": ");

			sb.append(
				String.valueOf(pageCollectionDefinition.getFragmentStyle()));
		}

		if (pageCollectionDefinition.getFragmentViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < pageCollectionDefinition.getFragmentViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						pageCollectionDefinition.getFragmentViewports()[i]));

				if ((i + 1) <
						pageCollectionDefinition.
							getFragmentViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageCollectionDefinition.getLayout() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"layout\": ");

			sb.append(String.valueOf(pageCollectionDefinition.getLayout()));
		}

		if (pageCollectionDefinition.getListItemStyle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listItemStyle\": ");

			sb.append("\"");

			sb.append(_escape(pageCollectionDefinition.getListItemStyle()));

			sb.append("\"");
		}

		if (pageCollectionDefinition.getListStyle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listStyle\": ");

			sb.append("\"");

			sb.append(_escape(pageCollectionDefinition.getListStyle()));

			sb.append("\"");
		}

		if (pageCollectionDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(pageCollectionDefinition.getName()));

			sb.append("\"");
		}

		if (pageCollectionDefinition.getNumberOfColumns() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfColumns\": ");

			sb.append(pageCollectionDefinition.getNumberOfColumns());
		}

		if (pageCollectionDefinition.getNumberOfItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfItems\": ");

			sb.append(pageCollectionDefinition.getNumberOfItems());
		}

		if (pageCollectionDefinition.getNumberOfItemsPerPage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfItemsPerPage\": ");

			sb.append(pageCollectionDefinition.getNumberOfItemsPerPage());
		}

		if (pageCollectionDefinition.getNumberOfPages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfPages\": ");

			sb.append(pageCollectionDefinition.getNumberOfPages());
		}

		if (pageCollectionDefinition.getPaginationType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paginationType\": ");

			sb.append("\"");

			sb.append(pageCollectionDefinition.getPaginationType());

			sb.append("\"");
		}

		if (pageCollectionDefinition.getShowAllItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showAllItems\": ");

			sb.append(pageCollectionDefinition.getShowAllItems());
		}

		if (pageCollectionDefinition.getTemplateKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"templateKey\": ");

			sb.append("\"");

			sb.append(_escape(pageCollectionDefinition.getTemplateKey()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageCollectionDefinitionJSONParser pageCollectionDefinitionJSONParser =
			new PageCollectionDefinitionJSONParser();

		return pageCollectionDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PageCollectionDefinition pageCollectionDefinition) {

		if (pageCollectionDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageCollectionDefinition.getCollectionConfig() == null) {
			map.put("collectionConfig", null);
		}
		else {
			map.put(
				"collectionConfig",
				String.valueOf(pageCollectionDefinition.getCollectionConfig()));
		}

		if (pageCollectionDefinition.getCollectionViewports() == null) {
			map.put("collectionViewports", null);
		}
		else {
			map.put(
				"collectionViewports",
				String.valueOf(
					pageCollectionDefinition.getCollectionViewports()));
		}

		if (pageCollectionDefinition.getDisplayAllItems() == null) {
			map.put("displayAllItems", null);
		}
		else {
			map.put(
				"displayAllItems",
				String.valueOf(pageCollectionDefinition.getDisplayAllItems()));
		}

		if (pageCollectionDefinition.getDisplayAllPages() == null) {
			map.put("displayAllPages", null);
		}
		else {
			map.put(
				"displayAllPages",
				String.valueOf(pageCollectionDefinition.getDisplayAllPages()));
		}

		if (pageCollectionDefinition.getEmptyCollectionConfig() == null) {
			map.put("emptyCollectionConfig", null);
		}
		else {
			map.put(
				"emptyCollectionConfig",
				String.valueOf(
					pageCollectionDefinition.getEmptyCollectionConfig()));
		}

		if (pageCollectionDefinition.getFragmentStyle() == null) {
			map.put("fragmentStyle", null);
		}
		else {
			map.put(
				"fragmentStyle",
				String.valueOf(pageCollectionDefinition.getFragmentStyle()));
		}

		if (pageCollectionDefinition.getFragmentViewports() == null) {
			map.put("fragmentViewports", null);
		}
		else {
			map.put(
				"fragmentViewports",
				String.valueOf(
					pageCollectionDefinition.getFragmentViewports()));
		}

		if (pageCollectionDefinition.getLayout() == null) {
			map.put("layout", null);
		}
		else {
			map.put(
				"layout", String.valueOf(pageCollectionDefinition.getLayout()));
		}

		if (pageCollectionDefinition.getListItemStyle() == null) {
			map.put("listItemStyle", null);
		}
		else {
			map.put(
				"listItemStyle",
				String.valueOf(pageCollectionDefinition.getListItemStyle()));
		}

		if (pageCollectionDefinition.getListStyle() == null) {
			map.put("listStyle", null);
		}
		else {
			map.put(
				"listStyle",
				String.valueOf(pageCollectionDefinition.getListStyle()));
		}

		if (pageCollectionDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(pageCollectionDefinition.getName()));
		}

		if (pageCollectionDefinition.getNumberOfColumns() == null) {
			map.put("numberOfColumns", null);
		}
		else {
			map.put(
				"numberOfColumns",
				String.valueOf(pageCollectionDefinition.getNumberOfColumns()));
		}

		if (pageCollectionDefinition.getNumberOfItems() == null) {
			map.put("numberOfItems", null);
		}
		else {
			map.put(
				"numberOfItems",
				String.valueOf(pageCollectionDefinition.getNumberOfItems()));
		}

		if (pageCollectionDefinition.getNumberOfItemsPerPage() == null) {
			map.put("numberOfItemsPerPage", null);
		}
		else {
			map.put(
				"numberOfItemsPerPage",
				String.valueOf(
					pageCollectionDefinition.getNumberOfItemsPerPage()));
		}

		if (pageCollectionDefinition.getNumberOfPages() == null) {
			map.put("numberOfPages", null);
		}
		else {
			map.put(
				"numberOfPages",
				String.valueOf(pageCollectionDefinition.getNumberOfPages()));
		}

		if (pageCollectionDefinition.getPaginationType() == null) {
			map.put("paginationType", null);
		}
		else {
			map.put(
				"paginationType",
				String.valueOf(pageCollectionDefinition.getPaginationType()));
		}

		if (pageCollectionDefinition.getShowAllItems() == null) {
			map.put("showAllItems", null);
		}
		else {
			map.put(
				"showAllItems",
				String.valueOf(pageCollectionDefinition.getShowAllItems()));
		}

		if (pageCollectionDefinition.getTemplateKey() == null) {
			map.put("templateKey", null);
		}
		else {
			map.put(
				"templateKey",
				String.valueOf(pageCollectionDefinition.getTemplateKey()));
		}

		return map;
	}

	public static class PageCollectionDefinitionJSONParser
		extends BaseJSONParser<PageCollectionDefinition> {

		@Override
		protected PageCollectionDefinition createDTO() {
			return new PageCollectionDefinition();
		}

		@Override
		protected PageCollectionDefinition[] createDTOArray(int size) {
			return new PageCollectionDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "collectionConfig")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "collectionViewports")) {

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
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentViewports")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "layout")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "listItemStyle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "listStyle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfColumns")) {
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
			else if (Objects.equals(jsonParserFieldName, "showAllItems")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "templateKey")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageCollectionDefinition pageCollectionDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "collectionConfig")) {
				if (jsonParserFieldValue != null) {
					pageCollectionDefinition.setCollectionConfig(
						CollectionConfigSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "collectionViewports")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					CollectionViewport[] collectionViewportsArray =
						new CollectionViewport[jsonParserFieldValues.length];

					for (int i = 0; i < collectionViewportsArray.length; i++) {
						collectionViewportsArray[i] =
							CollectionViewportSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					pageCollectionDefinition.setCollectionViewports(
						collectionViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayAllItems")) {
				if (jsonParserFieldValue != null) {
					pageCollectionDefinition.setDisplayAllItems(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayAllPages")) {
				if (jsonParserFieldValue != null) {
					pageCollectionDefinition.setDisplayAllPages(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "emptyCollectionConfig")) {

				if (jsonParserFieldValue != null) {
					pageCollectionDefinition.setEmptyCollectionConfig(
						EmptyCollectionConfigSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				if (jsonParserFieldValue != null) {
					pageCollectionDefinition.setFragmentStyle(
						FragmentStyleSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentViewports")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FragmentViewport[] fragmentViewportsArray =
						new FragmentViewport[jsonParserFieldValues.length];

					for (int i = 0; i < fragmentViewportsArray.length; i++) {
						fragmentViewportsArray[i] =
							FragmentViewportSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					pageCollectionDefinition.setFragmentViewports(
						fragmentViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "layout")) {
				if (jsonParserFieldValue != null) {
					pageCollectionDefinition.setLayout(
						LayoutSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "listItemStyle")) {
				if (jsonParserFieldValue != null) {
					pageCollectionDefinition.setListItemStyle(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "listStyle")) {
				if (jsonParserFieldValue != null) {
					pageCollectionDefinition.setListStyle(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					pageCollectionDefinition.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfColumns")) {
				if (jsonParserFieldValue != null) {
					pageCollectionDefinition.setNumberOfColumns(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfItems")) {
				if (jsonParserFieldValue != null) {
					pageCollectionDefinition.setNumberOfItems(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfItemsPerPage")) {

				if (jsonParserFieldValue != null) {
					pageCollectionDefinition.setNumberOfItemsPerPage(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfPages")) {
				if (jsonParserFieldValue != null) {
					pageCollectionDefinition.setNumberOfPages(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paginationType")) {
				if (jsonParserFieldValue != null) {
					pageCollectionDefinition.setPaginationType(
						PageCollectionDefinition.PaginationType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "showAllItems")) {
				if (jsonParserFieldValue != null) {
					pageCollectionDefinition.setShowAllItems(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "templateKey")) {
				if (jsonParserFieldValue != null) {
					pageCollectionDefinition.setTemplateKey(
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