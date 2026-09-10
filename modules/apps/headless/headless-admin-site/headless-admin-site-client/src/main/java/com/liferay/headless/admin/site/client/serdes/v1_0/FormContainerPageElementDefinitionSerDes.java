/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentViewport;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

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
public class FormContainerPageElementDefinitionSerDes {

	public static FormContainerPageElementDefinition toDTO(String json) {
		FormContainerPageElementDefinitionJSONParser
			formContainerPageElementDefinitionJSONParser =
				new FormContainerPageElementDefinitionJSONParser();

		return formContainerPageElementDefinitionJSONParser.parseToDTO(json);
	}

	public static FormContainerPageElementDefinition[] toDTOs(String json) {
		FormContainerPageElementDefinitionJSONParser
			formContainerPageElementDefinitionJSONParser =
				new FormContainerPageElementDefinitionJSONParser();

		return formContainerPageElementDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		FormContainerPageElementDefinition formContainerPageElementDefinition) {

		if (formContainerPageElementDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (formContainerPageElementDefinition.getBackgroundImageValue() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"backgroundImageValue\": ");

			sb.append(
				String.valueOf(
					formContainerPageElementDefinition.
						getBackgroundImageValue()));
		}

		if (formContainerPageElementDefinition.getCssClasses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0;
				 i < formContainerPageElementDefinition.getCssClasses().length;
				 i++) {

				sb.append(
					_toJSON(
						formContainerPageElementDefinition.getCssClasses()[i]));

				if ((i + 1) <
						formContainerPageElementDefinition.
							getCssClasses().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (formContainerPageElementDefinition.getFormContainerConfig() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formContainerConfig\": ");

			sb.append(
				String.valueOf(
					formContainerPageElementDefinition.
						getFormContainerConfig()));
		}

		if (formContainerPageElementDefinition.getFragmentViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < formContainerPageElementDefinition.
					 getFragmentViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						formContainerPageElementDefinition.
							getFragmentViewports()[i]));

				if ((i + 1) < formContainerPageElementDefinition.
						getFragmentViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (formContainerPageElementDefinition.getIndexed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(formContainerPageElementDefinition.getIndexed());
		}

		if (formContainerPageElementDefinition.getLayout() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"layout\": ");

			sb.append(
				String.valueOf(formContainerPageElementDefinition.getLayout()));
		}

		if (formContainerPageElementDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(formContainerPageElementDefinition.getName()));

			sb.append("\"");
		}

		if (formContainerPageElementDefinition.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(formContainerPageElementDefinition.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FormContainerPageElementDefinitionJSONParser
			formContainerPageElementDefinitionJSONParser =
				new FormContainerPageElementDefinitionJSONParser();

		return formContainerPageElementDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FormContainerPageElementDefinition formContainerPageElementDefinition) {

		if (formContainerPageElementDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (formContainerPageElementDefinition.getBackgroundImageValue() ==
				null) {

			map.put("backgroundImageValue", null);
		}
		else {
			map.put(
				"backgroundImageValue",
				String.valueOf(
					formContainerPageElementDefinition.
						getBackgroundImageValue()));
		}

		if (formContainerPageElementDefinition.getCssClasses() == null) {
			map.put("cssClasses", null);
		}
		else {
			map.put(
				"cssClasses",
				String.valueOf(
					formContainerPageElementDefinition.getCssClasses()));
		}

		if (formContainerPageElementDefinition.getFormContainerConfig() ==
				null) {

			map.put("formContainerConfig", null);
		}
		else {
			map.put(
				"formContainerConfig",
				String.valueOf(
					formContainerPageElementDefinition.
						getFormContainerConfig()));
		}

		if (formContainerPageElementDefinition.getFragmentViewports() == null) {
			map.put("fragmentViewports", null);
		}
		else {
			map.put(
				"fragmentViewports",
				String.valueOf(
					formContainerPageElementDefinition.getFragmentViewports()));
		}

		if (formContainerPageElementDefinition.getIndexed() == null) {
			map.put("indexed", null);
		}
		else {
			map.put(
				"indexed",
				String.valueOf(
					formContainerPageElementDefinition.getIndexed()));
		}

		if (formContainerPageElementDefinition.getLayout() == null) {
			map.put("layout", null);
		}
		else {
			map.put(
				"layout",
				String.valueOf(formContainerPageElementDefinition.getLayout()));
		}

		if (formContainerPageElementDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(formContainerPageElementDefinition.getName()));
		}

		if (formContainerPageElementDefinition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(formContainerPageElementDefinition.getType()));
		}

		return map;
	}

	public static class FormContainerPageElementDefinitionJSONParser
		extends BaseJSONParser<FormContainerPageElementDefinition> {

		@Override
		protected FormContainerPageElementDefinition createDTO() {
			return new FormContainerPageElementDefinition();
		}

		@Override
		protected FormContainerPageElementDefinition[] createDTOArray(
			int size) {

			return new FormContainerPageElementDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "backgroundImageValue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "formContainerConfig")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentViewports")) {
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
			FormContainerPageElementDefinition
				formContainerPageElementDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "backgroundImageValue")) {
				if (jsonParserFieldValue != null) {
					formContainerPageElementDefinition.setBackgroundImageValue(
						BackgroundImageValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				if (jsonParserFieldValue != null) {
					formContainerPageElementDefinition.setCssClasses(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "formContainerConfig")) {

				if (jsonParserFieldValue != null) {
					formContainerPageElementDefinition.setFormContainerConfig(
						FormContainerConfigSerDes.toDTO(
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

					formContainerPageElementDefinition.setFragmentViewports(
						fragmentViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				if (jsonParserFieldValue != null) {
					formContainerPageElementDefinition.setIndexed(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "layout")) {
				if (jsonParserFieldValue != null) {
					formContainerPageElementDefinition.setLayout(
						LayoutSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					formContainerPageElementDefinition.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					formContainerPageElementDefinition.setType(
						FormContainerPageElementDefinition.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-1266812945