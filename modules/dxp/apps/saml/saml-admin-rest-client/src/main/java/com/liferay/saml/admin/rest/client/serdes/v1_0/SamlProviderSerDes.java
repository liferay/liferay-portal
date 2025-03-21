/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.admin.rest.client.serdes.v1_0;

import com.liferay.saml.admin.rest.client.dto.v1_0.SamlProvider;
import com.liferay.saml.admin.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Generated("")
public class SamlProviderSerDes {

	public static SamlProvider toDTO(String json) {
		SamlProviderJSONParser samlProviderJSONParser =
			new SamlProviderJSONParser();

		return samlProviderJSONParser.parseToDTO(json);
	}

	public static SamlProvider[] toDTOs(String json) {
		SamlProviderJSONParser samlProviderJSONParser =
			new SamlProviderJSONParser();

		return samlProviderJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SamlProvider samlProvider) {
		if (samlProvider == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (samlProvider.getEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enabled\": ");

			sb.append(samlProvider.getEnabled());
		}

		if (samlProvider.getEntityId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entityId\": ");

			sb.append("\"");

			sb.append(_escape(samlProvider.getEntityId()));

			sb.append("\"");
		}

		if (samlProvider.getIdp() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"idp\": ");

			sb.append(String.valueOf(samlProvider.getIdp()));
		}

		if (samlProvider.getKeyStoreCredentialPassword() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keyStoreCredentialPassword\": ");

			sb.append("\"");

			sb.append(_escape(samlProvider.getKeyStoreCredentialPassword()));

			sb.append("\"");
		}

		if (samlProvider.getRole() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"role\": ");

			sb.append("\"");

			sb.append(samlProvider.getRole());

			sb.append("\"");
		}

		if (samlProvider.getSignMetadata() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"signMetadata\": ");

			sb.append(samlProvider.getSignMetadata());
		}

		if (samlProvider.getSp() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sp\": ");

			sb.append(String.valueOf(samlProvider.getSp()));
		}

		if (samlProvider.getSslRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sslRequired\": ");

			sb.append(samlProvider.getSslRequired());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SamlProviderJSONParser samlProviderJSONParser =
			new SamlProviderJSONParser();

		return samlProviderJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SamlProvider samlProvider) {
		if (samlProvider == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (samlProvider.getEnabled() == null) {
			map.put("enabled", null);
		}
		else {
			map.put("enabled", String.valueOf(samlProvider.getEnabled()));
		}

		if (samlProvider.getEntityId() == null) {
			map.put("entityId", null);
		}
		else {
			map.put("entityId", String.valueOf(samlProvider.getEntityId()));
		}

		if (samlProvider.getIdp() == null) {
			map.put("idp", null);
		}
		else {
			map.put("idp", String.valueOf(samlProvider.getIdp()));
		}

		if (samlProvider.getKeyStoreCredentialPassword() == null) {
			map.put("keyStoreCredentialPassword", null);
		}
		else {
			map.put(
				"keyStoreCredentialPassword",
				String.valueOf(samlProvider.getKeyStoreCredentialPassword()));
		}

		if (samlProvider.getRole() == null) {
			map.put("role", null);
		}
		else {
			map.put("role", String.valueOf(samlProvider.getRole()));
		}

		if (samlProvider.getSignMetadata() == null) {
			map.put("signMetadata", null);
		}
		else {
			map.put(
				"signMetadata", String.valueOf(samlProvider.getSignMetadata()));
		}

		if (samlProvider.getSp() == null) {
			map.put("sp", null);
		}
		else {
			map.put("sp", String.valueOf(samlProvider.getSp()));
		}

		if (samlProvider.getSslRequired() == null) {
			map.put("sslRequired", null);
		}
		else {
			map.put(
				"sslRequired", String.valueOf(samlProvider.getSslRequired()));
		}

		return map;
	}

	public static class SamlProviderJSONParser
		extends BaseJSONParser<SamlProvider> {

		@Override
		protected SamlProvider createDTO() {
			return new SamlProvider();
		}

		@Override
		protected SamlProvider[] createDTOArray(int size) {
			return new SamlProvider[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "enabled")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "entityId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "idp")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "keyStoreCredentialPassword")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "role")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "signMetadata")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sp")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sslRequired")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SamlProvider samlProvider, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "enabled")) {
				if (jsonParserFieldValue != null) {
					samlProvider.setEnabled((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "entityId")) {
				if (jsonParserFieldValue != null) {
					samlProvider.setEntityId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "idp")) {
				if (jsonParserFieldValue != null) {
					samlProvider.setIdp(
						IdpSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "keyStoreCredentialPassword")) {

				if (jsonParserFieldValue != null) {
					samlProvider.setKeyStoreCredentialPassword(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "role")) {
				if (jsonParserFieldValue != null) {
					samlProvider.setRole(
						SamlProvider.Role.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "signMetadata")) {
				if (jsonParserFieldValue != null) {
					samlProvider.setSignMetadata((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sp")) {
				if (jsonParserFieldValue != null) {
					samlProvider.setSp(
						SpSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sslRequired")) {
				if (jsonParserFieldValue != null) {
					samlProvider.setSslRequired((Boolean)jsonParserFieldValue);
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