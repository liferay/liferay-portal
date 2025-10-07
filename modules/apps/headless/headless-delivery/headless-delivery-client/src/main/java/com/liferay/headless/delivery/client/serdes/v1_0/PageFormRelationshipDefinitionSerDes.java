/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.CustomCSSViewport;
import com.liferay.headless.delivery.client.dto.v1_0.FragmentViewport;
import com.liferay.headless.delivery.client.dto.v1_0.PageFormRelationshipDefinition;
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
public class PageFormRelationshipDefinitionSerDes {

	public static PageFormRelationshipDefinition toDTO(String json) {
		PageFormRelationshipDefinitionJSONParser
			pageFormRelationshipDefinitionJSONParser =
				new PageFormRelationshipDefinitionJSONParser();

		return pageFormRelationshipDefinitionJSONParser.parseToDTO(json);
	}

	public static PageFormRelationshipDefinition[] toDTOs(String json) {
		PageFormRelationshipDefinitionJSONParser
			pageFormRelationshipDefinitionJSONParser =
				new PageFormRelationshipDefinitionJSONParser();

		return pageFormRelationshipDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PageFormRelationshipDefinition pageFormRelationshipDefinition) {

		if (pageFormRelationshipDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageFormRelationshipDefinition.getButtonLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"buttonLabel\": ");

			sb.append(
				String.valueOf(
					pageFormRelationshipDefinition.getButtonLabel()));
		}

		if (pageFormRelationshipDefinition.getContentType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentType\": ");

			sb.append("\"");

			sb.append(_escape(pageFormRelationshipDefinition.getContentType()));

			sb.append("\"");
		}

		if (pageFormRelationshipDefinition.getCssClasses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0;
				 i < pageFormRelationshipDefinition.getCssClasses().length;
				 i++) {

				sb.append(
					_toJSON(pageFormRelationshipDefinition.getCssClasses()[i]));

				if ((i + 1) <
						pageFormRelationshipDefinition.getCssClasses().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageFormRelationshipDefinition.getCustomCSS() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSS\": ");

			sb.append("\"");

			sb.append(_escape(pageFormRelationshipDefinition.getCustomCSS()));

			sb.append("\"");
		}

		if (pageFormRelationshipDefinition.getCustomCSSViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSSViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 pageFormRelationshipDefinition.
						 getCustomCSSViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						pageFormRelationshipDefinition.getCustomCSSViewports()
							[i]));

				if ((i + 1) < pageFormRelationshipDefinition.
						getCustomCSSViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageFormRelationshipDefinition.getFragmentStyle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentStyle\": ");

			sb.append(
				String.valueOf(
					pageFormRelationshipDefinition.getFragmentStyle()));
		}

		if (pageFormRelationshipDefinition.getFragmentViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 pageFormRelationshipDefinition.
						 getFragmentViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						pageFormRelationshipDefinition.getFragmentViewports()
							[i]));

				if ((i + 1) < pageFormRelationshipDefinition.
						getFragmentViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageFormRelationshipDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(pageFormRelationshipDefinition.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageFormRelationshipDefinitionJSONParser
			pageFormRelationshipDefinitionJSONParser =
				new PageFormRelationshipDefinitionJSONParser();

		return pageFormRelationshipDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PageFormRelationshipDefinition pageFormRelationshipDefinition) {

		if (pageFormRelationshipDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageFormRelationshipDefinition.getButtonLabel() == null) {
			map.put("buttonLabel", null);
		}
		else {
			map.put(
				"buttonLabel",
				String.valueOf(
					pageFormRelationshipDefinition.getButtonLabel()));
		}

		if (pageFormRelationshipDefinition.getContentType() == null) {
			map.put("contentType", null);
		}
		else {
			map.put(
				"contentType",
				String.valueOf(
					pageFormRelationshipDefinition.getContentType()));
		}

		if (pageFormRelationshipDefinition.getCssClasses() == null) {
			map.put("cssClasses", null);
		}
		else {
			map.put(
				"cssClasses",
				String.valueOf(pageFormRelationshipDefinition.getCssClasses()));
		}

		if (pageFormRelationshipDefinition.getCustomCSS() == null) {
			map.put("customCSS", null);
		}
		else {
			map.put(
				"customCSS",
				String.valueOf(pageFormRelationshipDefinition.getCustomCSS()));
		}

		if (pageFormRelationshipDefinition.getCustomCSSViewports() == null) {
			map.put("customCSSViewports", null);
		}
		else {
			map.put(
				"customCSSViewports",
				String.valueOf(
					pageFormRelationshipDefinition.getCustomCSSViewports()));
		}

		if (pageFormRelationshipDefinition.getFragmentStyle() == null) {
			map.put("fragmentStyle", null);
		}
		else {
			map.put(
				"fragmentStyle",
				String.valueOf(
					pageFormRelationshipDefinition.getFragmentStyle()));
		}

		if (pageFormRelationshipDefinition.getFragmentViewports() == null) {
			map.put("fragmentViewports", null);
		}
		else {
			map.put(
				"fragmentViewports",
				String.valueOf(
					pageFormRelationshipDefinition.getFragmentViewports()));
		}

		if (pageFormRelationshipDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(pageFormRelationshipDefinition.getName()));
		}

		return map;
	}

	public static class PageFormRelationshipDefinitionJSONParser
		extends BaseJSONParser<PageFormRelationshipDefinition> {

		@Override
		protected PageFormRelationshipDefinition createDTO() {
			return new PageFormRelationshipDefinition();
		}

		@Override
		protected PageFormRelationshipDefinition[] createDTOArray(int size) {
			return new PageFormRelationshipDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "buttonLabel")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contentType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "cssClasses")) {
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

			return false;
		}

		@Override
		protected void setField(
			PageFormRelationshipDefinition pageFormRelationshipDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "buttonLabel")) {
				if (jsonParserFieldValue != null) {
					pageFormRelationshipDefinition.setButtonLabel(
						FragmentInlineValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentType")) {
				if (jsonParserFieldValue != null) {
					pageFormRelationshipDefinition.setContentType(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				if (jsonParserFieldValue != null) {
					pageFormRelationshipDefinition.setCssClasses(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				if (jsonParserFieldValue != null) {
					pageFormRelationshipDefinition.setCustomCSS(
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

					pageFormRelationshipDefinition.setCustomCSSViewports(
						customCSSViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				if (jsonParserFieldValue != null) {
					pageFormRelationshipDefinition.setFragmentStyle(
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

					pageFormRelationshipDefinition.setFragmentViewports(
						fragmentViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					pageFormRelationshipDefinition.setName(
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