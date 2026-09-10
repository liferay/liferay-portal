/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.configuration.client.serdes.v1_0;

import com.liferay.headless.admin.configuration.client.dto.v1_0.SystemConfiguration;
import com.liferay.headless.admin.configuration.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Thiago Buarque
 * @generated
 */
@Generated("")
public class SystemConfigurationSerDes {

	public static SystemConfiguration toDTO(String json) {
		SystemConfigurationJSONParser systemConfigurationJSONParser =
			new SystemConfigurationJSONParser();

		return systemConfigurationJSONParser.parseToDTO(json);
	}

	public static SystemConfiguration[] toDTOs(String json) {
		SystemConfigurationJSONParser systemConfigurationJSONParser =
			new SystemConfigurationJSONParser();

		return systemConfigurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SystemConfiguration systemConfiguration) {
		if (systemConfiguration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (systemConfiguration.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(systemConfiguration.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (systemConfiguration.getProperties() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"properties\": ");

			sb.append(_toJSON(systemConfiguration.getProperties()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SystemConfigurationJSONParser systemConfigurationJSONParser =
			new SystemConfigurationJSONParser();

		return systemConfigurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SystemConfiguration systemConfiguration) {

		if (systemConfiguration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (systemConfiguration.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(systemConfiguration.getExternalReferenceCode()));
		}

		if (systemConfiguration.getProperties() == null) {
			map.put("properties", null);
		}
		else {
			map.put(
				"properties",
				String.valueOf(systemConfiguration.getProperties()));
		}

		return map;
	}

	public static class SystemConfigurationJSONParser
		extends BaseJSONParser<SystemConfiguration> {

		@Override
		protected SystemConfiguration createDTO() {
			return new SystemConfiguration();
		}

		@Override
		protected SystemConfiguration[] createDTOArray(int size) {
			return new SystemConfiguration[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "properties")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			SystemConfiguration systemConfiguration, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					systemConfiguration.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "properties")) {
				if (jsonParserFieldValue != null) {
					systemConfiguration.setProperties(
						(Map<String, Object>)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-1760335642