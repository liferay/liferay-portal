/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

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
public class ModuleViewportSerDes {

	public static ModuleViewport toDTO(String json) {
		ModuleViewportJSONParser moduleViewportJSONParser =
			new ModuleViewportJSONParser();

		return moduleViewportJSONParser.parseToDTO(json);
	}

	public static ModuleViewport[] toDTOs(String json) {
		ModuleViewportJSONParser moduleViewportJSONParser =
			new ModuleViewportJSONParser();

		return moduleViewportJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ModuleViewport moduleViewport) {
		if (moduleViewport == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (moduleViewport.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");
			sb.append(moduleViewport.getId());
			sb.append("\"");
		}

		if (moduleViewport.getModuleViewportDefinition() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"moduleViewportDefinition\": ");

			sb.append(
				String.valueOf(moduleViewport.getModuleViewportDefinition()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ModuleViewportJSONParser moduleViewportJSONParser =
			new ModuleViewportJSONParser();

		return moduleViewportJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ModuleViewport moduleViewport) {
		if (moduleViewport == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (moduleViewport.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(moduleViewport.getId()));
		}

		if (moduleViewport.getModuleViewportDefinition() == null) {
			map.put("moduleViewportDefinition", null);
		}
		else {
			map.put(
				"moduleViewportDefinition",
				String.valueOf(moduleViewport.getModuleViewportDefinition()));
		}

		return map;
	}

	public static class ModuleViewportJSONParser
		extends BaseJSONParser<ModuleViewport> {

		@Override
		protected ModuleViewport createDTO() {
			return new ModuleViewport();
		}

		@Override
		protected ModuleViewport[] createDTOArray(int size) {
			return new ModuleViewport[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "moduleViewportDefinition")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ModuleViewport moduleViewport, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					moduleViewport.setId(
						ModuleViewport.Id.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "moduleViewportDefinition")) {

				if (jsonParserFieldValue != null) {
					moduleViewport.setModuleViewportDefinition(
						ModuleViewportDefinitionSerDes.toDTO(
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