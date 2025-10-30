/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.CustomCSSViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.FormPageElementDefinition;
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
public class FormPageElementDefinitionSerDes {

	public static FormPageElementDefinition toDTO(String json) {
		FormPageElementDefinitionJSONParser
			formPageElementDefinitionJSONParser =
				new FormPageElementDefinitionJSONParser();

		return formPageElementDefinitionJSONParser.parseToDTO(json);
	}

	public static FormPageElementDefinition[] toDTOs(String json) {
		FormPageElementDefinitionJSONParser
			formPageElementDefinitionJSONParser =
				new FormPageElementDefinitionJSONParser();

		return formPageElementDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		FormPageElementDefinition formPageElementDefinition) {

		if (formPageElementDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (formPageElementDefinition.getCssClasses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0;
				 i < formPageElementDefinition.getCssClasses().length; i++) {

				sb.append(
					_toJSON(formPageElementDefinition.getCssClasses()[i]));

				if ((i + 1) <
						formPageElementDefinition.getCssClasses().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (formPageElementDefinition.getCustomCSS() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSS\": ");

			sb.append("\"");

			sb.append(_escape(formPageElementDefinition.getCustomCSS()));

			sb.append("\"");
		}

		if (formPageElementDefinition.getCustomCSSViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSSViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < formPageElementDefinition.getCustomCSSViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						formPageElementDefinition.getCustomCSSViewports()[i]));

				if ((i + 1) <
						formPageElementDefinition.
							getCustomCSSViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (formPageElementDefinition.getFormConfig() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formConfig\": ");

			sb.append(
				String.valueOf(formPageElementDefinition.getFormConfig()));
		}

		if (formPageElementDefinition.getFragmentStyle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentStyle\": ");

			sb.append(
				String.valueOf(formPageElementDefinition.getFragmentStyle()));
		}

		if (formPageElementDefinition.getFragmentViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < formPageElementDefinition.getFragmentViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						formPageElementDefinition.getFragmentViewports()[i]));

				if ((i + 1) <
						formPageElementDefinition.
							getFragmentViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (formPageElementDefinition.getIndexed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(formPageElementDefinition.getIndexed());
		}

		if (formPageElementDefinition.getLayout() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"layout\": ");

			sb.append(String.valueOf(formPageElementDefinition.getLayout()));
		}

		if (formPageElementDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(formPageElementDefinition.getName()));

			sb.append("\"");
		}

		if (formPageElementDefinition.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(formPageElementDefinition.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FormPageElementDefinitionJSONParser
			formPageElementDefinitionJSONParser =
				new FormPageElementDefinitionJSONParser();

		return formPageElementDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FormPageElementDefinition formPageElementDefinition) {

		if (formPageElementDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (formPageElementDefinition.getCssClasses() == null) {
			map.put("cssClasses", null);
		}
		else {
			map.put(
				"cssClasses",
				String.valueOf(formPageElementDefinition.getCssClasses()));
		}

		if (formPageElementDefinition.getCustomCSS() == null) {
			map.put("customCSS", null);
		}
		else {
			map.put(
				"customCSS",
				String.valueOf(formPageElementDefinition.getCustomCSS()));
		}

		if (formPageElementDefinition.getCustomCSSViewports() == null) {
			map.put("customCSSViewports", null);
		}
		else {
			map.put(
				"customCSSViewports",
				String.valueOf(
					formPageElementDefinition.getCustomCSSViewports()));
		}

		if (formPageElementDefinition.getFormConfig() == null) {
			map.put("formConfig", null);
		}
		else {
			map.put(
				"formConfig",
				String.valueOf(formPageElementDefinition.getFormConfig()));
		}

		if (formPageElementDefinition.getFragmentStyle() == null) {
			map.put("fragmentStyle", null);
		}
		else {
			map.put(
				"fragmentStyle",
				String.valueOf(formPageElementDefinition.getFragmentStyle()));
		}

		if (formPageElementDefinition.getFragmentViewports() == null) {
			map.put("fragmentViewports", null);
		}
		else {
			map.put(
				"fragmentViewports",
				String.valueOf(
					formPageElementDefinition.getFragmentViewports()));
		}

		if (formPageElementDefinition.getIndexed() == null) {
			map.put("indexed", null);
		}
		else {
			map.put(
				"indexed",
				String.valueOf(formPageElementDefinition.getIndexed()));
		}

		if (formPageElementDefinition.getLayout() == null) {
			map.put("layout", null);
		}
		else {
			map.put(
				"layout",
				String.valueOf(formPageElementDefinition.getLayout()));
		}

		if (formPageElementDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(formPageElementDefinition.getName()));
		}

		if (formPageElementDefinition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type", String.valueOf(formPageElementDefinition.getType()));
		}

		return map;
	}

	public static class FormPageElementDefinitionJSONParser
		extends BaseJSONParser<FormPageElementDefinition> {

		@Override
		protected FormPageElementDefinition createDTO() {
			return new FormPageElementDefinition();
		}

		@Override
		protected FormPageElementDefinition[] createDTOArray(int size) {
			return new FormPageElementDefinition[size];
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
			else if (Objects.equals(jsonParserFieldName, "formConfig")) {
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
			FormPageElementDefinition formPageElementDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				if (jsonParserFieldValue != null) {
					formPageElementDefinition.setCssClasses(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				if (jsonParserFieldValue != null) {
					formPageElementDefinition.setCustomCSS(
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

					formPageElementDefinition.setCustomCSSViewports(
						customCSSViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "formConfig")) {
				if (jsonParserFieldValue != null) {
					formPageElementDefinition.setFormConfig(
						FormConfigSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				if (jsonParserFieldValue != null) {
					formPageElementDefinition.setFragmentStyle(
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

					formPageElementDefinition.setFragmentViewports(
						fragmentViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				if (jsonParserFieldValue != null) {
					formPageElementDefinition.setIndexed(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "layout")) {
				if (jsonParserFieldValue != null) {
					formPageElementDefinition.setLayout(
						LayoutSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					formPageElementDefinition.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					formPageElementDefinition.setType(
						FormPageElementDefinition.Type.create(
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