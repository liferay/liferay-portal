/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.CustomCSSViewport;
import com.liferay.headless.delivery.client.dto.v1_0.FragmentViewport;
import com.liferay.headless.delivery.client.dto.v1_0.PageFormStepContainerDefinition;
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
public class PageFormStepContainerDefinitionSerDes {

	public static PageFormStepContainerDefinition toDTO(String json) {
		PageFormStepContainerDefinitionJSONParser
			pageFormStepContainerDefinitionJSONParser =
				new PageFormStepContainerDefinitionJSONParser();

		return pageFormStepContainerDefinitionJSONParser.parseToDTO(json);
	}

	public static PageFormStepContainerDefinition[] toDTOs(String json) {
		PageFormStepContainerDefinitionJSONParser
			pageFormStepContainerDefinitionJSONParser =
				new PageFormStepContainerDefinitionJSONParser();

		return pageFormStepContainerDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PageFormStepContainerDefinition pageFormStepContainerDefinition) {

		if (pageFormStepContainerDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageFormStepContainerDefinition.getCssClasses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0;
				 i < pageFormStepContainerDefinition.getCssClasses().length;
				 i++) {

				sb.append(
					_toJSON(
						pageFormStepContainerDefinition.getCssClasses()[i]));

				if ((i + 1) <
						pageFormStepContainerDefinition.
							getCssClasses().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageFormStepContainerDefinition.getCustomCSS() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSS\": ");

			sb.append("\"");

			sb.append(_escape(pageFormStepContainerDefinition.getCustomCSS()));

			sb.append("\"");
		}

		if (pageFormStepContainerDefinition.getCustomCSSViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSSViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < pageFormStepContainerDefinition.
					 getCustomCSSViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						pageFormStepContainerDefinition.getCustomCSSViewports()
							[i]));

				if ((i + 1) < pageFormStepContainerDefinition.
						getCustomCSSViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageFormStepContainerDefinition.getFragmentStyle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentStyle\": ");

			sb.append(
				String.valueOf(
					pageFormStepContainerDefinition.getFragmentStyle()));
		}

		if (pageFormStepContainerDefinition.getFragmentViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 pageFormStepContainerDefinition.
						 getFragmentViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						pageFormStepContainerDefinition.getFragmentViewports()
							[i]));

				if ((i + 1) < pageFormStepContainerDefinition.
						getFragmentViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageFormStepContainerDefinition.getIndexed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(pageFormStepContainerDefinition.getIndexed());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageFormStepContainerDefinitionJSONParser
			pageFormStepContainerDefinitionJSONParser =
				new PageFormStepContainerDefinitionJSONParser();

		return pageFormStepContainerDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PageFormStepContainerDefinition pageFormStepContainerDefinition) {

		if (pageFormStepContainerDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageFormStepContainerDefinition.getCssClasses() == null) {
			map.put("cssClasses", null);
		}
		else {
			map.put(
				"cssClasses",
				String.valueOf(
					pageFormStepContainerDefinition.getCssClasses()));
		}

		if (pageFormStepContainerDefinition.getCustomCSS() == null) {
			map.put("customCSS", null);
		}
		else {
			map.put(
				"customCSS",
				String.valueOf(pageFormStepContainerDefinition.getCustomCSS()));
		}

		if (pageFormStepContainerDefinition.getCustomCSSViewports() == null) {
			map.put("customCSSViewports", null);
		}
		else {
			map.put(
				"customCSSViewports",
				String.valueOf(
					pageFormStepContainerDefinition.getCustomCSSViewports()));
		}

		if (pageFormStepContainerDefinition.getFragmentStyle() == null) {
			map.put("fragmentStyle", null);
		}
		else {
			map.put(
				"fragmentStyle",
				String.valueOf(
					pageFormStepContainerDefinition.getFragmentStyle()));
		}

		if (pageFormStepContainerDefinition.getFragmentViewports() == null) {
			map.put("fragmentViewports", null);
		}
		else {
			map.put(
				"fragmentViewports",
				String.valueOf(
					pageFormStepContainerDefinition.getFragmentViewports()));
		}

		if (pageFormStepContainerDefinition.getIndexed() == null) {
			map.put("indexed", null);
		}
		else {
			map.put(
				"indexed",
				String.valueOf(pageFormStepContainerDefinition.getIndexed()));
		}

		return map;
	}

	public static class PageFormStepContainerDefinitionJSONParser
		extends BaseJSONParser<PageFormStepContainerDefinition> {

		@Override
		protected PageFormStepContainerDefinition createDTO() {
			return new PageFormStepContainerDefinition();
		}

		@Override
		protected PageFormStepContainerDefinition[] createDTOArray(int size) {
			return new PageFormStepContainerDefinition[size];
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
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageFormStepContainerDefinition pageFormStepContainerDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				if (jsonParserFieldValue != null) {
					pageFormStepContainerDefinition.setCssClasses(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				if (jsonParserFieldValue != null) {
					pageFormStepContainerDefinition.setCustomCSS(
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

					pageFormStepContainerDefinition.setCustomCSSViewports(
						customCSSViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				if (jsonParserFieldValue != null) {
					pageFormStepContainerDefinition.setFragmentStyle(
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

					pageFormStepContainerDefinition.setFragmentViewports(
						fragmentViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				if (jsonParserFieldValue != null) {
					pageFormStepContainerDefinition.setIndexed(
						(Boolean)jsonParserFieldValue);
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