/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FormRelationshipPageElementDefinition;
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
public class FormRelationshipPageElementDefinitionSerDes {

	public static FormRelationshipPageElementDefinition toDTO(String json) {
		FormRelationshipPageElementDefinitionJSONParser
			formRelationshipPageElementDefinitionJSONParser =
				new FormRelationshipPageElementDefinitionJSONParser();

		return formRelationshipPageElementDefinitionJSONParser.parseToDTO(json);
	}

	public static FormRelationshipPageElementDefinition[] toDTOs(String json) {
		FormRelationshipPageElementDefinitionJSONParser
			formRelationshipPageElementDefinitionJSONParser =
				new FormRelationshipPageElementDefinitionJSONParser();

		return formRelationshipPageElementDefinitionJSONParser.parseToDTOs(
			json);
	}

	public static String toJSON(
		FormRelationshipPageElementDefinition
			formRelationshipPageElementDefinition) {

		if (formRelationshipPageElementDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (formRelationshipPageElementDefinition.getBackgroundImageValue() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"backgroundImageValue\": ");

			sb.append(
				String.valueOf(
					formRelationshipPageElementDefinition.
						getBackgroundImageValue()));
		}

		if (formRelationshipPageElementDefinition.getCssClasses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 formRelationshipPageElementDefinition.
						 getCssClasses().length;
				 i++) {

				sb.append(
					_toJSON(
						formRelationshipPageElementDefinition.getCssClasses()
							[i]));

				if ((i + 1) < formRelationshipPageElementDefinition.
						getCssClasses().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (formRelationshipPageElementDefinition.getFormRelationshipConfig() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formRelationshipConfig\": ");

			sb.append(
				String.valueOf(
					formRelationshipPageElementDefinition.
						getFormRelationshipConfig()));
		}

		if (formRelationshipPageElementDefinition.getFragmentViewports() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < formRelationshipPageElementDefinition.
					 getFragmentViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						formRelationshipPageElementDefinition.
							getFragmentViewports()[i]));

				if ((i + 1) < formRelationshipPageElementDefinition.
						getFragmentViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (formRelationshipPageElementDefinition.getIndexed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(formRelationshipPageElementDefinition.getIndexed());
		}

		if (formRelationshipPageElementDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(formRelationshipPageElementDefinition.getName()));

			sb.append("\"");
		}

		if (formRelationshipPageElementDefinition.getRepeatable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"repeatable\": ");

			sb.append(formRelationshipPageElementDefinition.getRepeatable());
		}

		if (formRelationshipPageElementDefinition.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(formRelationshipPageElementDefinition.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FormRelationshipPageElementDefinitionJSONParser
			formRelationshipPageElementDefinitionJSONParser =
				new FormRelationshipPageElementDefinitionJSONParser();

		return formRelationshipPageElementDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FormRelationshipPageElementDefinition
			formRelationshipPageElementDefinition) {

		if (formRelationshipPageElementDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (formRelationshipPageElementDefinition.getBackgroundImageValue() ==
				null) {

			map.put("backgroundImageValue", null);
		}
		else {
			map.put(
				"backgroundImageValue",
				String.valueOf(
					formRelationshipPageElementDefinition.
						getBackgroundImageValue()));
		}

		if (formRelationshipPageElementDefinition.getCssClasses() == null) {
			map.put("cssClasses", null);
		}
		else {
			map.put(
				"cssClasses",
				String.valueOf(
					formRelationshipPageElementDefinition.getCssClasses()));
		}

		if (formRelationshipPageElementDefinition.getFormRelationshipConfig() ==
				null) {

			map.put("formRelationshipConfig", null);
		}
		else {
			map.put(
				"formRelationshipConfig",
				String.valueOf(
					formRelationshipPageElementDefinition.
						getFormRelationshipConfig()));
		}

		if (formRelationshipPageElementDefinition.getFragmentViewports() ==
				null) {

			map.put("fragmentViewports", null);
		}
		else {
			map.put(
				"fragmentViewports",
				String.valueOf(
					formRelationshipPageElementDefinition.
						getFragmentViewports()));
		}

		if (formRelationshipPageElementDefinition.getIndexed() == null) {
			map.put("indexed", null);
		}
		else {
			map.put(
				"indexed",
				String.valueOf(
					formRelationshipPageElementDefinition.getIndexed()));
		}

		if (formRelationshipPageElementDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(
					formRelationshipPageElementDefinition.getName()));
		}

		if (formRelationshipPageElementDefinition.getRepeatable() == null) {
			map.put("repeatable", null);
		}
		else {
			map.put(
				"repeatable",
				String.valueOf(
					formRelationshipPageElementDefinition.getRepeatable()));
		}

		if (formRelationshipPageElementDefinition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(
					formRelationshipPageElementDefinition.getType()));
		}

		return map;
	}

	public static class FormRelationshipPageElementDefinitionJSONParser
		extends BaseJSONParser<FormRelationshipPageElementDefinition> {

		@Override
		protected FormRelationshipPageElementDefinition createDTO() {
			return new FormRelationshipPageElementDefinition();
		}

		@Override
		protected FormRelationshipPageElementDefinition[] createDTOArray(
			int size) {

			return new FormRelationshipPageElementDefinition[size];
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
						jsonParserFieldName, "formRelationshipConfig")) {

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
			else if (Objects.equals(jsonParserFieldName, "repeatable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FormRelationshipPageElementDefinition
				formRelationshipPageElementDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "backgroundImageValue")) {
				if (jsonParserFieldValue != null) {
					formRelationshipPageElementDefinition.
						setBackgroundImageValue(
							BackgroundImageValueSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				if (jsonParserFieldValue != null) {
					formRelationshipPageElementDefinition.setCssClasses(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "formRelationshipConfig")) {

				if (jsonParserFieldValue != null) {
					formRelationshipPageElementDefinition.
						setFormRelationshipConfig(
							FormRelationshipConfigSerDes.toDTO(
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

					formRelationshipPageElementDefinition.setFragmentViewports(
						fragmentViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				if (jsonParserFieldValue != null) {
					formRelationshipPageElementDefinition.setIndexed(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					formRelationshipPageElementDefinition.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "repeatable")) {
				if (jsonParserFieldValue != null) {
					formRelationshipPageElementDefinition.setRepeatable(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					formRelationshipPageElementDefinition.setType(
						FormRelationshipPageElementDefinition.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:1134975348