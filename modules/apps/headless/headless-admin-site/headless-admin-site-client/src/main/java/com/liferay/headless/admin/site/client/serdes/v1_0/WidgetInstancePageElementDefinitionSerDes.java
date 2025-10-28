/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FragmentViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetInstancePageElementDefinition;
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
public class WidgetInstancePageElementDefinitionSerDes {

	public static WidgetInstancePageElementDefinition toDTO(String json) {
		WidgetInstancePageElementDefinitionJSONParser
			widgetInstancePageElementDefinitionJSONParser =
				new WidgetInstancePageElementDefinitionJSONParser();

		return widgetInstancePageElementDefinitionJSONParser.parseToDTO(json);
	}

	public static WidgetInstancePageElementDefinition[] toDTOs(String json) {
		WidgetInstancePageElementDefinitionJSONParser
			widgetInstancePageElementDefinitionJSONParser =
				new WidgetInstancePageElementDefinitionJSONParser();

		return widgetInstancePageElementDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		WidgetInstancePageElementDefinition
			widgetInstancePageElementDefinition) {

		if (widgetInstancePageElementDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (widgetInstancePageElementDefinition.getCssClasses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0;
				 i < widgetInstancePageElementDefinition.getCssClasses().length;
				 i++) {

				sb.append(
					_toJSON(
						widgetInstancePageElementDefinition.getCssClasses()
							[i]));

				if ((i + 1) < widgetInstancePageElementDefinition.
						getCssClasses().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (widgetInstancePageElementDefinition.getCustomCSS() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSS\": ");

			sb.append("\"");

			sb.append(
				_escape(widgetInstancePageElementDefinition.getCustomCSS()));

			sb.append("\"");
		}

		if (widgetInstancePageElementDefinition.
				getDraftWidgetInstanceExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"draftWidgetInstanceExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					widgetInstancePageElementDefinition.
						getDraftWidgetInstanceExternalReferenceCode()));

			sb.append("\"");
		}

		if (widgetInstancePageElementDefinition.getFragmentStyle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentStyle\": ");

			sb.append(
				String.valueOf(
					widgetInstancePageElementDefinition.getFragmentStyle()));
		}

		if (widgetInstancePageElementDefinition.getFragmentViewports() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < widgetInstancePageElementDefinition.
					 getFragmentViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						widgetInstancePageElementDefinition.
							getFragmentViewports()[i]));

				if ((i + 1) < widgetInstancePageElementDefinition.
						getFragmentViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (widgetInstancePageElementDefinition.getIndexed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(widgetInstancePageElementDefinition.getIndexed());
		}

		if (widgetInstancePageElementDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(widgetInstancePageElementDefinition.getName()));

			sb.append("\"");
		}

		if (widgetInstancePageElementDefinition.getWidgetInstance() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetInstance\": ");

			sb.append(
				String.valueOf(
					widgetInstancePageElementDefinition.getWidgetInstance()));
		}

		if (widgetInstancePageElementDefinition.
				getWidgetInstanceExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetInstanceExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					widgetInstancePageElementDefinition.
						getWidgetInstanceExternalReferenceCode()));

			sb.append("\"");
		}

		if (widgetInstancePageElementDefinition.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(widgetInstancePageElementDefinition.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WidgetInstancePageElementDefinitionJSONParser
			widgetInstancePageElementDefinitionJSONParser =
				new WidgetInstancePageElementDefinitionJSONParser();

		return widgetInstancePageElementDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WidgetInstancePageElementDefinition
			widgetInstancePageElementDefinition) {

		if (widgetInstancePageElementDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (widgetInstancePageElementDefinition.getCssClasses() == null) {
			map.put("cssClasses", null);
		}
		else {
			map.put(
				"cssClasses",
				String.valueOf(
					widgetInstancePageElementDefinition.getCssClasses()));
		}

		if (widgetInstancePageElementDefinition.getCustomCSS() == null) {
			map.put("customCSS", null);
		}
		else {
			map.put(
				"customCSS",
				String.valueOf(
					widgetInstancePageElementDefinition.getCustomCSS()));
		}

		if (widgetInstancePageElementDefinition.
				getDraftWidgetInstanceExternalReferenceCode() == null) {

			map.put("draftWidgetInstanceExternalReferenceCode", null);
		}
		else {
			map.put(
				"draftWidgetInstanceExternalReferenceCode",
				String.valueOf(
					widgetInstancePageElementDefinition.
						getDraftWidgetInstanceExternalReferenceCode()));
		}

		if (widgetInstancePageElementDefinition.getFragmentStyle() == null) {
			map.put("fragmentStyle", null);
		}
		else {
			map.put(
				"fragmentStyle",
				String.valueOf(
					widgetInstancePageElementDefinition.getFragmentStyle()));
		}

		if (widgetInstancePageElementDefinition.getFragmentViewports() ==
				null) {

			map.put("fragmentViewports", null);
		}
		else {
			map.put(
				"fragmentViewports",
				String.valueOf(
					widgetInstancePageElementDefinition.
						getFragmentViewports()));
		}

		if (widgetInstancePageElementDefinition.getIndexed() == null) {
			map.put("indexed", null);
		}
		else {
			map.put(
				"indexed",
				String.valueOf(
					widgetInstancePageElementDefinition.getIndexed()));
		}

		if (widgetInstancePageElementDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(widgetInstancePageElementDefinition.getName()));
		}

		if (widgetInstancePageElementDefinition.getWidgetInstance() == null) {
			map.put("widgetInstance", null);
		}
		else {
			map.put(
				"widgetInstance",
				String.valueOf(
					widgetInstancePageElementDefinition.getWidgetInstance()));
		}

		if (widgetInstancePageElementDefinition.
				getWidgetInstanceExternalReferenceCode() == null) {

			map.put("widgetInstanceExternalReferenceCode", null);
		}
		else {
			map.put(
				"widgetInstanceExternalReferenceCode",
				String.valueOf(
					widgetInstancePageElementDefinition.
						getWidgetInstanceExternalReferenceCode()));
		}

		if (widgetInstancePageElementDefinition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(widgetInstancePageElementDefinition.getType()));
		}

		return map;
	}

	public static class WidgetInstancePageElementDefinitionJSONParser
		extends BaseJSONParser<WidgetInstancePageElementDefinition> {

		@Override
		protected WidgetInstancePageElementDefinition createDTO() {
			return new WidgetInstancePageElementDefinition();
		}

		@Override
		protected WidgetInstancePageElementDefinition[] createDTOArray(
			int size) {

			return new WidgetInstancePageElementDefinition[size];
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
						jsonParserFieldName,
						"draftWidgetInstanceExternalReferenceCode")) {

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
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "widgetInstance")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"widgetInstanceExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WidgetInstancePageElementDefinition
				widgetInstancePageElementDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				if (jsonParserFieldValue != null) {
					widgetInstancePageElementDefinition.setCssClasses(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				if (jsonParserFieldValue != null) {
					widgetInstancePageElementDefinition.setCustomCSS(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"draftWidgetInstanceExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					widgetInstancePageElementDefinition.
						setDraftWidgetInstanceExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				if (jsonParserFieldValue != null) {
					widgetInstancePageElementDefinition.setFragmentStyle(
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

					widgetInstancePageElementDefinition.setFragmentViewports(
						fragmentViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				if (jsonParserFieldValue != null) {
					widgetInstancePageElementDefinition.setIndexed(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					widgetInstancePageElementDefinition.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widgetInstance")) {
				if (jsonParserFieldValue != null) {
					widgetInstancePageElementDefinition.setWidgetInstance(
						WidgetInstanceSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"widgetInstanceExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					widgetInstancePageElementDefinition.
						setWidgetInstanceExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					widgetInstancePageElementDefinition.setType(
						WidgetInstancePageElementDefinition.Type.create(
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