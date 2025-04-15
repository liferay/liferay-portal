/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.CustomCSSViewport;
import com.liferay.headless.delivery.client.dto.v1_0.FragmentViewport;
import com.liferay.headless.delivery.client.dto.v1_0.PageRowDefinition;
import com.liferay.headless.delivery.client.dto.v1_0.RowViewport;
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
public class PageRowDefinitionSerDes {

	public static PageRowDefinition toDTO(String json) {
		PageRowDefinitionJSONParser pageRowDefinitionJSONParser =
			new PageRowDefinitionJSONParser();

		return pageRowDefinitionJSONParser.parseToDTO(json);
	}

	public static PageRowDefinition[] toDTOs(String json) {
		PageRowDefinitionJSONParser pageRowDefinitionJSONParser =
			new PageRowDefinitionJSONParser();

		return pageRowDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PageRowDefinition pageRowDefinition) {
		if (pageRowDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageRowDefinition.getCssClasses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0; i < pageRowDefinition.getCssClasses().length; i++) {
				sb.append(_toJSON(pageRowDefinition.getCssClasses()[i]));

				if ((i + 1) < pageRowDefinition.getCssClasses().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageRowDefinition.getCustomCSS() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSS\": ");

			sb.append("\"");

			sb.append(_escape(pageRowDefinition.getCustomCSS()));

			sb.append("\"");
		}

		if (pageRowDefinition.getCustomCSSViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSSViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < pageRowDefinition.getCustomCSSViewports().length; i++) {

				sb.append(
					String.valueOf(
						pageRowDefinition.getCustomCSSViewports()[i]));

				if ((i + 1) <
						pageRowDefinition.getCustomCSSViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageRowDefinition.getFragmentStyle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentStyle\": ");

			sb.append(String.valueOf(pageRowDefinition.getFragmentStyle()));
		}

		if (pageRowDefinition.getFragmentViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0; i < pageRowDefinition.getFragmentViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						pageRowDefinition.getFragmentViewports()[i]));

				if ((i + 1) < pageRowDefinition.getFragmentViewports().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageRowDefinition.getGutters() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"gutters\": ");

			sb.append(pageRowDefinition.getGutters());
		}

		if (pageRowDefinition.getIndexed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(pageRowDefinition.getIndexed());
		}

		if (pageRowDefinition.getModulesPerRow() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modulesPerRow\": ");

			sb.append(pageRowDefinition.getModulesPerRow());
		}

		if (pageRowDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(pageRowDefinition.getName()));

			sb.append("\"");
		}

		if (pageRowDefinition.getNumberOfColumns() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfColumns\": ");

			sb.append(pageRowDefinition.getNumberOfColumns());
		}

		if (pageRowDefinition.getReverseOrder() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reverseOrder\": ");

			sb.append(pageRowDefinition.getReverseOrder());
		}

		if (pageRowDefinition.getRowViewportConfig() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rowViewportConfig\": ");

			sb.append(String.valueOf(pageRowDefinition.getRowViewportConfig()));
		}

		if (pageRowDefinition.getRowViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rowViewports\": ");

			sb.append("[");

			for (int i = 0; i < pageRowDefinition.getRowViewports().length;
				 i++) {

				sb.append(
					String.valueOf(pageRowDefinition.getRowViewports()[i]));

				if ((i + 1) < pageRowDefinition.getRowViewports().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageRowDefinition.getVerticalAlignment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"verticalAlignment\": ");

			sb.append("\"");

			sb.append(_escape(pageRowDefinition.getVerticalAlignment()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageRowDefinitionJSONParser pageRowDefinitionJSONParser =
			new PageRowDefinitionJSONParser();

		return pageRowDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PageRowDefinition pageRowDefinition) {

		if (pageRowDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageRowDefinition.getCssClasses() == null) {
			map.put("cssClasses", null);
		}
		else {
			map.put(
				"cssClasses",
				String.valueOf(pageRowDefinition.getCssClasses()));
		}

		if (pageRowDefinition.getCustomCSS() == null) {
			map.put("customCSS", null);
		}
		else {
			map.put(
				"customCSS", String.valueOf(pageRowDefinition.getCustomCSS()));
		}

		if (pageRowDefinition.getCustomCSSViewports() == null) {
			map.put("customCSSViewports", null);
		}
		else {
			map.put(
				"customCSSViewports",
				String.valueOf(pageRowDefinition.getCustomCSSViewports()));
		}

		if (pageRowDefinition.getFragmentStyle() == null) {
			map.put("fragmentStyle", null);
		}
		else {
			map.put(
				"fragmentStyle",
				String.valueOf(pageRowDefinition.getFragmentStyle()));
		}

		if (pageRowDefinition.getFragmentViewports() == null) {
			map.put("fragmentViewports", null);
		}
		else {
			map.put(
				"fragmentViewports",
				String.valueOf(pageRowDefinition.getFragmentViewports()));
		}

		if (pageRowDefinition.getGutters() == null) {
			map.put("gutters", null);
		}
		else {
			map.put("gutters", String.valueOf(pageRowDefinition.getGutters()));
		}

		if (pageRowDefinition.getIndexed() == null) {
			map.put("indexed", null);
		}
		else {
			map.put("indexed", String.valueOf(pageRowDefinition.getIndexed()));
		}

		if (pageRowDefinition.getModulesPerRow() == null) {
			map.put("modulesPerRow", null);
		}
		else {
			map.put(
				"modulesPerRow",
				String.valueOf(pageRowDefinition.getModulesPerRow()));
		}

		if (pageRowDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(pageRowDefinition.getName()));
		}

		if (pageRowDefinition.getNumberOfColumns() == null) {
			map.put("numberOfColumns", null);
		}
		else {
			map.put(
				"numberOfColumns",
				String.valueOf(pageRowDefinition.getNumberOfColumns()));
		}

		if (pageRowDefinition.getReverseOrder() == null) {
			map.put("reverseOrder", null);
		}
		else {
			map.put(
				"reverseOrder",
				String.valueOf(pageRowDefinition.getReverseOrder()));
		}

		if (pageRowDefinition.getRowViewportConfig() == null) {
			map.put("rowViewportConfig", null);
		}
		else {
			map.put(
				"rowViewportConfig",
				String.valueOf(pageRowDefinition.getRowViewportConfig()));
		}

		if (pageRowDefinition.getRowViewports() == null) {
			map.put("rowViewports", null);
		}
		else {
			map.put(
				"rowViewports",
				String.valueOf(pageRowDefinition.getRowViewports()));
		}

		if (pageRowDefinition.getVerticalAlignment() == null) {
			map.put("verticalAlignment", null);
		}
		else {
			map.put(
				"verticalAlignment",
				String.valueOf(pageRowDefinition.getVerticalAlignment()));
		}

		return map;
	}

	public static class PageRowDefinitionJSONParser
		extends BaseJSONParser<PageRowDefinition> {

		@Override
		protected PageRowDefinition createDTO() {
			return new PageRowDefinition();
		}

		@Override
		protected PageRowDefinition[] createDTOArray(int size) {
			return new PageRowDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "customCSSViewports")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentViewports")) {
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
			else if (Objects.equals(jsonParserFieldName, "numberOfColumns")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "reverseOrder")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "rowViewportConfig")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "rowViewports")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "verticalAlignment")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageRowDefinition pageRowDefinition, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				if (jsonParserFieldValue != null) {
					pageRowDefinition.setCssClasses(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				if (jsonParserFieldValue != null) {
					pageRowDefinition.setCustomCSS(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "customCSSViewports")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					CustomCSSViewport[] customCSSViewportsArray =
						new CustomCSSViewport[jsonParserFieldValues.length];

					for (int i = 0; i < customCSSViewportsArray.length; i++) {
						customCSSViewportsArray[i] =
							CustomCSSViewportSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					pageRowDefinition.setCustomCSSViewports(
						customCSSViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				if (jsonParserFieldValue != null) {
					pageRowDefinition.setFragmentStyle(
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

					pageRowDefinition.setFragmentViewports(
						fragmentViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "gutters")) {
				if (jsonParserFieldValue != null) {
					pageRowDefinition.setGutters((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				if (jsonParserFieldValue != null) {
					pageRowDefinition.setIndexed((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modulesPerRow")) {
				if (jsonParserFieldValue != null) {
					pageRowDefinition.setModulesPerRow(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					pageRowDefinition.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfColumns")) {
				if (jsonParserFieldValue != null) {
					pageRowDefinition.setNumberOfColumns(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "reverseOrder")) {
				if (jsonParserFieldValue != null) {
					pageRowDefinition.setReverseOrder(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "rowViewportConfig")) {
				if (jsonParserFieldValue != null) {
					pageRowDefinition.setRowViewportConfig(
						RowViewportConfigSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "rowViewports")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					RowViewport[] rowViewportsArray =
						new RowViewport[jsonParserFieldValues.length];

					for (int i = 0; i < rowViewportsArray.length; i++) {
						rowViewportsArray[i] = RowViewportSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					pageRowDefinition.setRowViewports(rowViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "verticalAlignment")) {
				if (jsonParserFieldValue != null) {
					pageRowDefinition.setVerticalAlignment(
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