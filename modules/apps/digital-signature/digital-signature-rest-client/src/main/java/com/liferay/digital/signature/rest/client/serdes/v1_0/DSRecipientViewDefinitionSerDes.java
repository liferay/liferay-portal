/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.client.serdes.v1_0;

import com.liferay.digital.signature.rest.client.dto.v1_0.DSRecipientViewDefinition;
import com.liferay.digital.signature.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author José Abelenda
 * @generated
 */
@Generated("")
public class DSRecipientViewDefinitionSerDes {

	public static DSRecipientViewDefinition toDTO(String json) {
		DSRecipientViewDefinitionJSONParser
			dsRecipientViewDefinitionJSONParser =
				new DSRecipientViewDefinitionJSONParser();

		return dsRecipientViewDefinitionJSONParser.parseToDTO(json);
	}

	public static DSRecipientViewDefinition[] toDTOs(String json) {
		DSRecipientViewDefinitionJSONParser
			dsRecipientViewDefinitionJSONParser =
				new DSRecipientViewDefinitionJSONParser();

		return dsRecipientViewDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		DSRecipientViewDefinition dsRecipientViewDefinition) {

		if (dsRecipientViewDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (dsRecipientViewDefinition.getAuthenticationMethod() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"authenticationMethod\": ");

			sb.append("\"");

			sb.append(
				_escape(dsRecipientViewDefinition.getAuthenticationMethod()));

			sb.append("\"");
		}

		if (dsRecipientViewDefinition.getDsClientUserId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dsClientUserId\": ");

			sb.append("\"");

			sb.append(_escape(dsRecipientViewDefinition.getDsClientUserId()));

			sb.append("\"");
		}

		if (dsRecipientViewDefinition.getEmailAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddress\": ");

			sb.append("\"");

			sb.append(_escape(dsRecipientViewDefinition.getEmailAddress()));

			sb.append("\"");
		}

		if (dsRecipientViewDefinition.getReturnURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"returnURL\": ");

			sb.append("\"");

			sb.append(_escape(dsRecipientViewDefinition.getReturnURL()));

			sb.append("\"");
		}

		if (dsRecipientViewDefinition.getUserName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userName\": ");

			sb.append("\"");

			sb.append(_escape(dsRecipientViewDefinition.getUserName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DSRecipientViewDefinitionJSONParser
			dsRecipientViewDefinitionJSONParser =
				new DSRecipientViewDefinitionJSONParser();

		return dsRecipientViewDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DSRecipientViewDefinition dsRecipientViewDefinition) {

		if (dsRecipientViewDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dsRecipientViewDefinition.getAuthenticationMethod() == null) {
			map.put("authenticationMethod", null);
		}
		else {
			map.put(
				"authenticationMethod",
				String.valueOf(
					dsRecipientViewDefinition.getAuthenticationMethod()));
		}

		if (dsRecipientViewDefinition.getDsClientUserId() == null) {
			map.put("dsClientUserId", null);
		}
		else {
			map.put(
				"dsClientUserId",
				String.valueOf(dsRecipientViewDefinition.getDsClientUserId()));
		}

		if (dsRecipientViewDefinition.getEmailAddress() == null) {
			map.put("emailAddress", null);
		}
		else {
			map.put(
				"emailAddress",
				String.valueOf(dsRecipientViewDefinition.getEmailAddress()));
		}

		if (dsRecipientViewDefinition.getReturnURL() == null) {
			map.put("returnURL", null);
		}
		else {
			map.put(
				"returnURL",
				String.valueOf(dsRecipientViewDefinition.getReturnURL()));
		}

		if (dsRecipientViewDefinition.getUserName() == null) {
			map.put("userName", null);
		}
		else {
			map.put(
				"userName",
				String.valueOf(dsRecipientViewDefinition.getUserName()));
		}

		return map;
	}

	public static class DSRecipientViewDefinitionJSONParser
		extends BaseJSONParser<DSRecipientViewDefinition> {

		@Override
		protected DSRecipientViewDefinition createDTO() {
			return new DSRecipientViewDefinition();
		}

		@Override
		protected DSRecipientViewDefinition[] createDTOArray(int size) {
			return new DSRecipientViewDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "authenticationMethod")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dsClientUserId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "returnURL")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userName")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DSRecipientViewDefinition dsRecipientViewDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "authenticationMethod")) {
				if (jsonParserFieldValue != null) {
					dsRecipientViewDefinition.setAuthenticationMethod(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dsClientUserId")) {
				if (jsonParserFieldValue != null) {
					dsRecipientViewDefinition.setDsClientUserId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				if (jsonParserFieldValue != null) {
					dsRecipientViewDefinition.setEmailAddress(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "returnURL")) {
				if (jsonParserFieldValue != null) {
					dsRecipientViewDefinition.setReturnURL(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userName")) {
				if (jsonParserFieldValue != null) {
					dsRecipientViewDefinition.setUserName(
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