/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ModulePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.ModuleViewport;
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
public class ModulePageElementDefinitionSerDes {

	public static ModulePageElementDefinition toDTO(String json) {
		ModulePageElementDefinitionJSONParser
			modulePageElementDefinitionJSONParser =
				new ModulePageElementDefinitionJSONParser();

		return modulePageElementDefinitionJSONParser.parseToDTO(json);
	}

	public static ModulePageElementDefinition[] toDTOs(String json) {
		ModulePageElementDefinitionJSONParser
			modulePageElementDefinitionJSONParser =
				new ModulePageElementDefinitionJSONParser();

		return modulePageElementDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ModulePageElementDefinition modulePageElementDefinition) {

		if (modulePageElementDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (modulePageElementDefinition.getModuleViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"moduleViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < modulePageElementDefinition.getModuleViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						modulePageElementDefinition.getModuleViewports()[i]));

				if ((i + 1) <
						modulePageElementDefinition.
							getModuleViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (modulePageElementDefinition.getSize() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"size\": ");

			sb.append(modulePageElementDefinition.getSize());
		}

		if (modulePageElementDefinition.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(modulePageElementDefinition.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ModulePageElementDefinitionJSONParser
			modulePageElementDefinitionJSONParser =
				new ModulePageElementDefinitionJSONParser();

		return modulePageElementDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ModulePageElementDefinition modulePageElementDefinition) {

		if (modulePageElementDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (modulePageElementDefinition.getModuleViewports() == null) {
			map.put("moduleViewports", null);
		}
		else {
			map.put(
				"moduleViewports",
				String.valueOf(
					modulePageElementDefinition.getModuleViewports()));
		}

		if (modulePageElementDefinition.getSize() == null) {
			map.put("size", null);
		}
		else {
			map.put(
				"size", String.valueOf(modulePageElementDefinition.getSize()));
		}

		if (modulePageElementDefinition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type", String.valueOf(modulePageElementDefinition.getType()));
		}

		return map;
	}

	public static class ModulePageElementDefinitionJSONParser
		extends BaseJSONParser<ModulePageElementDefinition> {

		@Override
		protected ModulePageElementDefinition createDTO() {
			return new ModulePageElementDefinition();
		}

		@Override
		protected ModulePageElementDefinition[] createDTOArray(int size) {
			return new ModulePageElementDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "moduleViewports")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "size")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ModulePageElementDefinition modulePageElementDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "moduleViewports")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ModuleViewport[] moduleViewportsArray =
						new ModuleViewport[jsonParserFieldValues.length];

					for (int i = 0; i < moduleViewportsArray.length; i++) {
						moduleViewportsArray[i] = ModuleViewportSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					modulePageElementDefinition.setModuleViewports(
						moduleViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "size")) {
				if (jsonParserFieldValue != null) {
					modulePageElementDefinition.setSize(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					modulePageElementDefinition.setType(
						ModulePageElementDefinition.Type.create(
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