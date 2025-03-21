/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ClientExtension;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ClientExtensionSerDes {

	public static ClientExtension toDTO(String json) {
		ClientExtensionJSONParser clientExtensionJSONParser =
			new ClientExtensionJSONParser();

		return clientExtensionJSONParser.parseToDTO(json);
	}

	public static ClientExtension[] toDTOs(String json) {
		ClientExtensionJSONParser clientExtensionJSONParser =
			new ClientExtensionJSONParser();

		return clientExtensionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ClientExtension clientExtension) {
		if (clientExtension == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (clientExtension.getClientExtensionConfig() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clientExtensionConfig\": ");

			sb.append(_toJSON(clientExtension.getClientExtensionConfig()));
		}

		if (clientExtension.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(clientExtension.getExternalReferenceCode()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ClientExtensionJSONParser clientExtensionJSONParser =
			new ClientExtensionJSONParser();

		return clientExtensionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ClientExtension clientExtension) {
		if (clientExtension == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (clientExtension.getClientExtensionConfig() == null) {
			map.put("clientExtensionConfig", null);
		}
		else {
			map.put(
				"clientExtensionConfig",
				String.valueOf(clientExtension.getClientExtensionConfig()));
		}

		if (clientExtension.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(clientExtension.getExternalReferenceCode()));
		}

		return map;
	}

	public static class ClientExtensionJSONParser
		extends BaseJSONParser<ClientExtension> {

		@Override
		protected ClientExtension createDTO() {
			return new ClientExtension();
		}

		@Override
		protected ClientExtension[] createDTOArray(int size) {
			return new ClientExtension[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "clientExtensionConfig")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ClientExtension clientExtension, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "clientExtensionConfig")) {
				if (jsonParserFieldValue != null) {
					clientExtension.setClientExtensionConfig(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					clientExtension.setExternalReferenceCode(
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