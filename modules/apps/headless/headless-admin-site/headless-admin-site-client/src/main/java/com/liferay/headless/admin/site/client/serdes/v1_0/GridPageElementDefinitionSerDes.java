/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.GridPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.GridViewport;
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
public class GridPageElementDefinitionSerDes {

	public static GridPageElementDefinition toDTO(String json) {
		GridPageElementDefinitionJSONParser
			gridPageElementDefinitionJSONParser =
				new GridPageElementDefinitionJSONParser();

		return gridPageElementDefinitionJSONParser.parseToDTO(json);
	}

	public static GridPageElementDefinition[] toDTOs(String json) {
		GridPageElementDefinitionJSONParser
			gridPageElementDefinitionJSONParser =
				new GridPageElementDefinitionJSONParser();

		return gridPageElementDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		GridPageElementDefinition gridPageElementDefinition) {

		if (gridPageElementDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (gridPageElementDefinition.getCssClasses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0;
				 i < gridPageElementDefinition.getCssClasses().length; i++) {

				sb.append(
					_toJSON(gridPageElementDefinition.getCssClasses()[i]));

				if ((i + 1) <
						gridPageElementDefinition.getCssClasses().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (gridPageElementDefinition.getCustomCSS() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSS\": ");

			sb.append("\"");

			sb.append(_escape(gridPageElementDefinition.getCustomCSS()));

			sb.append("\"");
		}

		if (gridPageElementDefinition.getFragmentStyle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentStyle\": ");

			sb.append(
				String.valueOf(gridPageElementDefinition.getFragmentStyle()));
		}

		if (gridPageElementDefinition.getGridViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"gridViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < gridPageElementDefinition.getGridViewports().length; i++) {

				sb.append(
					String.valueOf(
						gridPageElementDefinition.getGridViewports()[i]));

				if ((i + 1) <
						gridPageElementDefinition.getGridViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (gridPageElementDefinition.getGutters() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"gutters\": ");

			sb.append(gridPageElementDefinition.getGutters());
		}

		if (gridPageElementDefinition.getIndexed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(gridPageElementDefinition.getIndexed());
		}

		if (gridPageElementDefinition.getModulesPerRow() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modulesPerRow\": ");

			sb.append(gridPageElementDefinition.getModulesPerRow());
		}

		if (gridPageElementDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(gridPageElementDefinition.getName()));

			sb.append("\"");
		}

		if (gridPageElementDefinition.getNumberOfModules() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfModules\": ");

			sb.append(gridPageElementDefinition.getNumberOfModules());
		}

		if (gridPageElementDefinition.getReverseOrder() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reverseOrder\": ");

			sb.append(gridPageElementDefinition.getReverseOrder());
		}

		if (gridPageElementDefinition.getVerticalAlignment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"verticalAlignment\": ");

			sb.append("\"");
			sb.append(gridPageElementDefinition.getVerticalAlignment());
			sb.append("\"");
		}

		if (gridPageElementDefinition.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(gridPageElementDefinition.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		GridPageElementDefinitionJSONParser
			gridPageElementDefinitionJSONParser =
				new GridPageElementDefinitionJSONParser();

		return gridPageElementDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		GridPageElementDefinition gridPageElementDefinition) {

		if (gridPageElementDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (gridPageElementDefinition.getCssClasses() == null) {
			map.put("cssClasses", null);
		}
		else {
			map.put(
				"cssClasses",
				String.valueOf(gridPageElementDefinition.getCssClasses()));
		}

		if (gridPageElementDefinition.getCustomCSS() == null) {
			map.put("customCSS", null);
		}
		else {
			map.put(
				"customCSS",
				String.valueOf(gridPageElementDefinition.getCustomCSS()));
		}

		if (gridPageElementDefinition.getFragmentStyle() == null) {
			map.put("fragmentStyle", null);
		}
		else {
			map.put(
				"fragmentStyle",
				String.valueOf(gridPageElementDefinition.getFragmentStyle()));
		}

		if (gridPageElementDefinition.getGridViewports() == null) {
			map.put("gridViewports", null);
		}
		else {
			map.put(
				"gridViewports",
				String.valueOf(gridPageElementDefinition.getGridViewports()));
		}

		if (gridPageElementDefinition.getGutters() == null) {
			map.put("gutters", null);
		}
		else {
			map.put(
				"gutters",
				String.valueOf(gridPageElementDefinition.getGutters()));
		}

		if (gridPageElementDefinition.getIndexed() == null) {
			map.put("indexed", null);
		}
		else {
			map.put(
				"indexed",
				String.valueOf(gridPageElementDefinition.getIndexed()));
		}

		if (gridPageElementDefinition.getModulesPerRow() == null) {
			map.put("modulesPerRow", null);
		}
		else {
			map.put(
				"modulesPerRow",
				String.valueOf(gridPageElementDefinition.getModulesPerRow()));
		}

		if (gridPageElementDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(gridPageElementDefinition.getName()));
		}

		if (gridPageElementDefinition.getNumberOfModules() == null) {
			map.put("numberOfModules", null);
		}
		else {
			map.put(
				"numberOfModules",
				String.valueOf(gridPageElementDefinition.getNumberOfModules()));
		}

		if (gridPageElementDefinition.getReverseOrder() == null) {
			map.put("reverseOrder", null);
		}
		else {
			map.put(
				"reverseOrder",
				String.valueOf(gridPageElementDefinition.getReverseOrder()));
		}

		if (gridPageElementDefinition.getVerticalAlignment() == null) {
			map.put("verticalAlignment", null);
		}
		else {
			map.put(
				"verticalAlignment",
				String.valueOf(
					gridPageElementDefinition.getVerticalAlignment()));
		}

		if (gridPageElementDefinition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type", String.valueOf(gridPageElementDefinition.getType()));
		}

		return map;
	}

	public static class GridPageElementDefinitionJSONParser
		extends BaseJSONParser<GridPageElementDefinition> {

		@Override
		protected GridPageElementDefinition createDTO() {
			return new GridPageElementDefinition();
		}

		@Override
		protected GridPageElementDefinition[] createDTOArray(int size) {
			return new GridPageElementDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "gridViewports")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "gutters")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "modulesPerRow")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfModules")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "reverseOrder")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "verticalAlignment")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			GridPageElementDefinition gridPageElementDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				if (jsonParserFieldValue != null) {
					gridPageElementDefinition.setCssClasses(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				if (jsonParserFieldValue != null) {
					gridPageElementDefinition.setCustomCSS(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				if (jsonParserFieldValue != null) {
					gridPageElementDefinition.setFragmentStyle(
						FragmentStyleSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "gridViewports")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					GridViewport[] gridViewportsArray =
						new GridViewport[jsonParserFieldValues.length];

					for (int i = 0; i < gridViewportsArray.length; i++) {
						gridViewportsArray[i] = GridViewportSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					gridPageElementDefinition.setGridViewports(
						gridViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "gutters")) {
				if (jsonParserFieldValue != null) {
					gridPageElementDefinition.setGutters(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				if (jsonParserFieldValue != null) {
					gridPageElementDefinition.setIndexed(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modulesPerRow")) {
				if (jsonParserFieldValue != null) {
					gridPageElementDefinition.setModulesPerRow(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					gridPageElementDefinition.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfModules")) {
				if (jsonParserFieldValue != null) {
					gridPageElementDefinition.setNumberOfModules(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "reverseOrder")) {
				if (jsonParserFieldValue != null) {
					gridPageElementDefinition.setReverseOrder(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "verticalAlignment")) {
				if (jsonParserFieldValue != null) {
					gridPageElementDefinition.setVerticalAlignment(
						GridPageElementDefinition.VerticalAlignment.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					gridPageElementDefinition.setType(
						GridPageElementDefinition.Type.create(
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