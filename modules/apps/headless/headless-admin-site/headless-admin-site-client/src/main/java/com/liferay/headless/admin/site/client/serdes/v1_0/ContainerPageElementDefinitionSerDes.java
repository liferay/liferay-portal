/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentViewport;
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
public class ContainerPageElementDefinitionSerDes {

	public static ContainerPageElementDefinition toDTO(String json) {
		ContainerPageElementDefinitionJSONParser
			containerPageElementDefinitionJSONParser =
				new ContainerPageElementDefinitionJSONParser();

		return containerPageElementDefinitionJSONParser.parseToDTO(json);
	}

	public static ContainerPageElementDefinition[] toDTOs(String json) {
		ContainerPageElementDefinitionJSONParser
			containerPageElementDefinitionJSONParser =
				new ContainerPageElementDefinitionJSONParser();

		return containerPageElementDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ContainerPageElementDefinition containerPageElementDefinition) {

		if (containerPageElementDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (containerPageElementDefinition.getContentVisibility() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentVisibility\": ");

			sb.append("\"");

			sb.append(containerPageElementDefinition.getContentVisibility());

			sb.append("\"");
		}

		if (containerPageElementDefinition.getCssClasses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0;
				 i < containerPageElementDefinition.getCssClasses().length;
				 i++) {

				sb.append(
					_toJSON(containerPageElementDefinition.getCssClasses()[i]));

				if ((i + 1) <
						containerPageElementDefinition.getCssClasses().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (containerPageElementDefinition.getCustomCSS() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSS\": ");

			sb.append("\"");

			sb.append(_escape(containerPageElementDefinition.getCustomCSS()));

			sb.append("\"");
		}

		if (containerPageElementDefinition.getFragmentLink() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentLink\": ");

			sb.append(
				String.valueOf(
					containerPageElementDefinition.getFragmentLink()));
		}

		if (containerPageElementDefinition.getFragmentStyle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentStyle\": ");

			sb.append(
				String.valueOf(
					containerPageElementDefinition.getFragmentStyle()));
		}

		if (containerPageElementDefinition.getFragmentViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 containerPageElementDefinition.
						 getFragmentViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						containerPageElementDefinition.getFragmentViewports()
							[i]));

				if ((i + 1) < containerPageElementDefinition.
						getFragmentViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (containerPageElementDefinition.getHtmlProperties() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"htmlProperties\": ");

			sb.append(
				String.valueOf(
					containerPageElementDefinition.getHtmlProperties()));
		}

		if (containerPageElementDefinition.getIndexed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(containerPageElementDefinition.getIndexed());
		}

		if (containerPageElementDefinition.getLayout() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"layout\": ");

			sb.append(
				String.valueOf(containerPageElementDefinition.getLayout()));
		}

		if (containerPageElementDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(containerPageElementDefinition.getName()));

			sb.append("\"");
		}

		if (containerPageElementDefinition.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(containerPageElementDefinition.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ContainerPageElementDefinitionJSONParser
			containerPageElementDefinitionJSONParser =
				new ContainerPageElementDefinitionJSONParser();

		return containerPageElementDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ContainerPageElementDefinition containerPageElementDefinition) {

		if (containerPageElementDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (containerPageElementDefinition.getContentVisibility() == null) {
			map.put("contentVisibility", null);
		}
		else {
			map.put(
				"contentVisibility",
				String.valueOf(
					containerPageElementDefinition.getContentVisibility()));
		}

		if (containerPageElementDefinition.getCssClasses() == null) {
			map.put("cssClasses", null);
		}
		else {
			map.put(
				"cssClasses",
				String.valueOf(containerPageElementDefinition.getCssClasses()));
		}

		if (containerPageElementDefinition.getCustomCSS() == null) {
			map.put("customCSS", null);
		}
		else {
			map.put(
				"customCSS",
				String.valueOf(containerPageElementDefinition.getCustomCSS()));
		}

		if (containerPageElementDefinition.getFragmentLink() == null) {
			map.put("fragmentLink", null);
		}
		else {
			map.put(
				"fragmentLink",
				String.valueOf(
					containerPageElementDefinition.getFragmentLink()));
		}

		if (containerPageElementDefinition.getFragmentStyle() == null) {
			map.put("fragmentStyle", null);
		}
		else {
			map.put(
				"fragmentStyle",
				String.valueOf(
					containerPageElementDefinition.getFragmentStyle()));
		}

		if (containerPageElementDefinition.getFragmentViewports() == null) {
			map.put("fragmentViewports", null);
		}
		else {
			map.put(
				"fragmentViewports",
				String.valueOf(
					containerPageElementDefinition.getFragmentViewports()));
		}

		if (containerPageElementDefinition.getHtmlProperties() == null) {
			map.put("htmlProperties", null);
		}
		else {
			map.put(
				"htmlProperties",
				String.valueOf(
					containerPageElementDefinition.getHtmlProperties()));
		}

		if (containerPageElementDefinition.getIndexed() == null) {
			map.put("indexed", null);
		}
		else {
			map.put(
				"indexed",
				String.valueOf(containerPageElementDefinition.getIndexed()));
		}

		if (containerPageElementDefinition.getLayout() == null) {
			map.put("layout", null);
		}
		else {
			map.put(
				"layout",
				String.valueOf(containerPageElementDefinition.getLayout()));
		}

		if (containerPageElementDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(containerPageElementDefinition.getName()));
		}

		if (containerPageElementDefinition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(containerPageElementDefinition.getType()));
		}

		return map;
	}

	public static class ContainerPageElementDefinitionJSONParser
		extends BaseJSONParser<ContainerPageElementDefinition> {

		@Override
		protected ContainerPageElementDefinition createDTO() {
			return new ContainerPageElementDefinition();
		}

		@Override
		protected ContainerPageElementDefinition[] createDTOArray(int size) {
			return new ContainerPageElementDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "contentVisibility")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentLink")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentViewports")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "htmlProperties")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "layout")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ContainerPageElementDefinition containerPageElementDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "contentVisibility")) {
				if (jsonParserFieldValue != null) {
					containerPageElementDefinition.setContentVisibility(
						ContainerPageElementDefinition.ContentVisibility.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				if (jsonParserFieldValue != null) {
					containerPageElementDefinition.setCssClasses(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				if (jsonParserFieldValue != null) {
					containerPageElementDefinition.setCustomCSS(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentLink")) {
				if (jsonParserFieldValue != null) {
					containerPageElementDefinition.setFragmentLink(
						FragmentLinkSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				if (jsonParserFieldValue != null) {
					containerPageElementDefinition.setFragmentStyle(
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

					containerPageElementDefinition.setFragmentViewports(
						fragmentViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "htmlProperties")) {
				if (jsonParserFieldValue != null) {
					containerPageElementDefinition.setHtmlProperties(
						HtmlPropertiesSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				if (jsonParserFieldValue != null) {
					containerPageElementDefinition.setIndexed(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "layout")) {
				if (jsonParserFieldValue != null) {
					containerPageElementDefinition.setLayout(
						LayoutSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					containerPageElementDefinition.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					containerPageElementDefinition.setType(
						ContainerPageElementDefinition.Type.create(
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