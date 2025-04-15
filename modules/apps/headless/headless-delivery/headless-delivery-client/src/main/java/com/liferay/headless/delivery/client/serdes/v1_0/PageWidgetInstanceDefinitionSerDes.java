/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.CustomCSSViewport;
import com.liferay.headless.delivery.client.dto.v1_0.FragmentViewport;
import com.liferay.headless.delivery.client.dto.v1_0.PageWidgetInstanceDefinition;
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
public class PageWidgetInstanceDefinitionSerDes {

	public static PageWidgetInstanceDefinition toDTO(String json) {
		PageWidgetInstanceDefinitionJSONParser
			pageWidgetInstanceDefinitionJSONParser =
				new PageWidgetInstanceDefinitionJSONParser();

		return pageWidgetInstanceDefinitionJSONParser.parseToDTO(json);
	}

	public static PageWidgetInstanceDefinition[] toDTOs(String json) {
		PageWidgetInstanceDefinitionJSONParser
			pageWidgetInstanceDefinitionJSONParser =
				new PageWidgetInstanceDefinitionJSONParser();

		return pageWidgetInstanceDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PageWidgetInstanceDefinition pageWidgetInstanceDefinition) {

		if (pageWidgetInstanceDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageWidgetInstanceDefinition.getCssClasses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0;
				 i < pageWidgetInstanceDefinition.getCssClasses().length; i++) {

				sb.append(
					_toJSON(pageWidgetInstanceDefinition.getCssClasses()[i]));

				if ((i + 1) <
						pageWidgetInstanceDefinition.getCssClasses().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageWidgetInstanceDefinition.getCustomCSS() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSS\": ");

			sb.append("\"");

			sb.append(_escape(pageWidgetInstanceDefinition.getCustomCSS()));

			sb.append("\"");
		}

		if (pageWidgetInstanceDefinition.getCustomCSSViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSSViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 pageWidgetInstanceDefinition.
						 getCustomCSSViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						pageWidgetInstanceDefinition.getCustomCSSViewports()
							[i]));

				if ((i + 1) < pageWidgetInstanceDefinition.
						getCustomCSSViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageWidgetInstanceDefinition.getFragmentStyle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentStyle\": ");

			sb.append(
				String.valueOf(
					pageWidgetInstanceDefinition.getFragmentStyle()));
		}

		if (pageWidgetInstanceDefinition.getFragmentViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < pageWidgetInstanceDefinition.getFragmentViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						pageWidgetInstanceDefinition.getFragmentViewports()
							[i]));

				if ((i + 1) < pageWidgetInstanceDefinition.
						getFragmentViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageWidgetInstanceDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(pageWidgetInstanceDefinition.getName()));

			sb.append("\"");
		}

		if (pageWidgetInstanceDefinition.getWidgetInstance() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetInstance\": ");

			sb.append(
				String.valueOf(
					pageWidgetInstanceDefinition.getWidgetInstance()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageWidgetInstanceDefinitionJSONParser
			pageWidgetInstanceDefinitionJSONParser =
				new PageWidgetInstanceDefinitionJSONParser();

		return pageWidgetInstanceDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PageWidgetInstanceDefinition pageWidgetInstanceDefinition) {

		if (pageWidgetInstanceDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageWidgetInstanceDefinition.getCssClasses() == null) {
			map.put("cssClasses", null);
		}
		else {
			map.put(
				"cssClasses",
				String.valueOf(pageWidgetInstanceDefinition.getCssClasses()));
		}

		if (pageWidgetInstanceDefinition.getCustomCSS() == null) {
			map.put("customCSS", null);
		}
		else {
			map.put(
				"customCSS",
				String.valueOf(pageWidgetInstanceDefinition.getCustomCSS()));
		}

		if (pageWidgetInstanceDefinition.getCustomCSSViewports() == null) {
			map.put("customCSSViewports", null);
		}
		else {
			map.put(
				"customCSSViewports",
				String.valueOf(
					pageWidgetInstanceDefinition.getCustomCSSViewports()));
		}

		if (pageWidgetInstanceDefinition.getFragmentStyle() == null) {
			map.put("fragmentStyle", null);
		}
		else {
			map.put(
				"fragmentStyle",
				String.valueOf(
					pageWidgetInstanceDefinition.getFragmentStyle()));
		}

		if (pageWidgetInstanceDefinition.getFragmentViewports() == null) {
			map.put("fragmentViewports", null);
		}
		else {
			map.put(
				"fragmentViewports",
				String.valueOf(
					pageWidgetInstanceDefinition.getFragmentViewports()));
		}

		if (pageWidgetInstanceDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(pageWidgetInstanceDefinition.getName()));
		}

		if (pageWidgetInstanceDefinition.getWidgetInstance() == null) {
			map.put("widgetInstance", null);
		}
		else {
			map.put(
				"widgetInstance",
				String.valueOf(
					pageWidgetInstanceDefinition.getWidgetInstance()));
		}

		return map;
	}

	public static class PageWidgetInstanceDefinitionJSONParser
		extends BaseJSONParser<PageWidgetInstanceDefinition> {

		@Override
		protected PageWidgetInstanceDefinition createDTO() {
			return new PageWidgetInstanceDefinition();
		}

		@Override
		protected PageWidgetInstanceDefinition[] createDTOArray(int size) {
			return new PageWidgetInstanceDefinition[size];
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
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "widgetInstance")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageWidgetInstanceDefinition pageWidgetInstanceDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				if (jsonParserFieldValue != null) {
					pageWidgetInstanceDefinition.setCssClasses(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				if (jsonParserFieldValue != null) {
					pageWidgetInstanceDefinition.setCustomCSS(
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

					pageWidgetInstanceDefinition.setCustomCSSViewports(
						customCSSViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				if (jsonParserFieldValue != null) {
					pageWidgetInstanceDefinition.setFragmentStyle(
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

					pageWidgetInstanceDefinition.setFragmentViewports(
						fragmentViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					pageWidgetInstanceDefinition.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widgetInstance")) {
				if (jsonParserFieldValue != null) {
					pageWidgetInstanceDefinition.setWidgetInstance(
						WidgetInstanceSerDes.toDTO(
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