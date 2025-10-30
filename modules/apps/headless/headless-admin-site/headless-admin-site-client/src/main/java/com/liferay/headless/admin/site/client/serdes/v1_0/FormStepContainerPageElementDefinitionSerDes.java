/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.CustomCSSViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.FormStepContainerPageElementDefinition;
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
public class FormStepContainerPageElementDefinitionSerDes {

	public static FormStepContainerPageElementDefinition toDTO(String json) {
		FormStepContainerPageElementDefinitionJSONParser
			formStepContainerPageElementDefinitionJSONParser =
				new FormStepContainerPageElementDefinitionJSONParser();

		return formStepContainerPageElementDefinitionJSONParser.parseToDTO(
			json);
	}

	public static FormStepContainerPageElementDefinition[] toDTOs(String json) {
		FormStepContainerPageElementDefinitionJSONParser
			formStepContainerPageElementDefinitionJSONParser =
				new FormStepContainerPageElementDefinitionJSONParser();

		return formStepContainerPageElementDefinitionJSONParser.parseToDTOs(
			json);
	}

	public static String toJSON(
		FormStepContainerPageElementDefinition
			formStepContainerPageElementDefinition) {

		if (formStepContainerPageElementDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (formStepContainerPageElementDefinition.getCssClasses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 formStepContainerPageElementDefinition.
						 getCssClasses().length;
				 i++) {

				sb.append(
					_toJSON(
						formStepContainerPageElementDefinition.getCssClasses()
							[i]));

				if ((i + 1) < formStepContainerPageElementDefinition.
						getCssClasses().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (formStepContainerPageElementDefinition.getCustomCSS() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSS\": ");

			sb.append("\"");

			sb.append(
				_escape(formStepContainerPageElementDefinition.getCustomCSS()));

			sb.append("\"");
		}

		if (formStepContainerPageElementDefinition.getCustomCSSViewports() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSSViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < formStepContainerPageElementDefinition.
					 getCustomCSSViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						formStepContainerPageElementDefinition.
							getCustomCSSViewports()[i]));

				if ((i + 1) < formStepContainerPageElementDefinition.
						getCustomCSSViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (formStepContainerPageElementDefinition.getFragmentStyle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentStyle\": ");

			sb.append(
				String.valueOf(
					formStepContainerPageElementDefinition.getFragmentStyle()));
		}

		if (formStepContainerPageElementDefinition.getFragmentViewports() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < formStepContainerPageElementDefinition.
					 getFragmentViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						formStepContainerPageElementDefinition.
							getFragmentViewports()[i]));

				if ((i + 1) < formStepContainerPageElementDefinition.
						getFragmentViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (formStepContainerPageElementDefinition.getLayout() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"layout\": ");

			sb.append(
				String.valueOf(
					formStepContainerPageElementDefinition.getLayout()));
		}

		if (formStepContainerPageElementDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(
				_escape(formStepContainerPageElementDefinition.getName()));

			sb.append("\"");
		}

		if (formStepContainerPageElementDefinition.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(formStepContainerPageElementDefinition.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FormStepContainerPageElementDefinitionJSONParser
			formStepContainerPageElementDefinitionJSONParser =
				new FormStepContainerPageElementDefinitionJSONParser();

		return formStepContainerPageElementDefinitionJSONParser.parseToMap(
			json);
	}

	public static Map<String, String> toMap(
		FormStepContainerPageElementDefinition
			formStepContainerPageElementDefinition) {

		if (formStepContainerPageElementDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (formStepContainerPageElementDefinition.getCssClasses() == null) {
			map.put("cssClasses", null);
		}
		else {
			map.put(
				"cssClasses",
				String.valueOf(
					formStepContainerPageElementDefinition.getCssClasses()));
		}

		if (formStepContainerPageElementDefinition.getCustomCSS() == null) {
			map.put("customCSS", null);
		}
		else {
			map.put(
				"customCSS",
				String.valueOf(
					formStepContainerPageElementDefinition.getCustomCSS()));
		}

		if (formStepContainerPageElementDefinition.getCustomCSSViewports() ==
				null) {

			map.put("customCSSViewports", null);
		}
		else {
			map.put(
				"customCSSViewports",
				String.valueOf(
					formStepContainerPageElementDefinition.
						getCustomCSSViewports()));
		}

		if (formStepContainerPageElementDefinition.getFragmentStyle() == null) {
			map.put("fragmentStyle", null);
		}
		else {
			map.put(
				"fragmentStyle",
				String.valueOf(
					formStepContainerPageElementDefinition.getFragmentStyle()));
		}

		if (formStepContainerPageElementDefinition.getFragmentViewports() ==
				null) {

			map.put("fragmentViewports", null);
		}
		else {
			map.put(
				"fragmentViewports",
				String.valueOf(
					formStepContainerPageElementDefinition.
						getFragmentViewports()));
		}

		if (formStepContainerPageElementDefinition.getLayout() == null) {
			map.put("layout", null);
		}
		else {
			map.put(
				"layout",
				String.valueOf(
					formStepContainerPageElementDefinition.getLayout()));
		}

		if (formStepContainerPageElementDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(
					formStepContainerPageElementDefinition.getName()));
		}

		if (formStepContainerPageElementDefinition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(
					formStepContainerPageElementDefinition.getType()));
		}

		return map;
	}

	public static class FormStepContainerPageElementDefinitionJSONParser
		extends BaseJSONParser<FormStepContainerPageElementDefinition> {

		@Override
		protected FormStepContainerPageElementDefinition createDTO() {
			return new FormStepContainerPageElementDefinition();
		}

		@Override
		protected FormStepContainerPageElementDefinition[] createDTOArray(
			int size) {

			return new FormStepContainerPageElementDefinition[size];
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
			FormStepContainerPageElementDefinition
				formStepContainerPageElementDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				if (jsonParserFieldValue != null) {
					formStepContainerPageElementDefinition.setCssClasses(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				if (jsonParserFieldValue != null) {
					formStepContainerPageElementDefinition.setCustomCSS(
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

					formStepContainerPageElementDefinition.
						setCustomCSSViewports(customCSSViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				if (jsonParserFieldValue != null) {
					formStepContainerPageElementDefinition.setFragmentStyle(
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

					formStepContainerPageElementDefinition.setFragmentViewports(
						fragmentViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "layout")) {
				if (jsonParserFieldValue != null) {
					formStepContainerPageElementDefinition.setLayout(
						LayoutSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					formStepContainerPageElementDefinition.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					formStepContainerPageElementDefinition.setType(
						FormStepContainerPageElementDefinition.Type.create(
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