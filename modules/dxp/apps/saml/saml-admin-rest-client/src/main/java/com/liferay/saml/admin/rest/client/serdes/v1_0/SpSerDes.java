/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.admin.rest.client.serdes.v1_0;

import com.liferay.saml.admin.rest.client.dto.v1_0.Sp;
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
public class SpSerDes {

	public static Sp toDTO(String json) {
		SpJSONParser spJSONParser = new SpJSONParser();

		return spJSONParser.parseToDTO(json);
	}

	public static Sp[] toDTOs(String json) {
		SpJSONParser spJSONParser = new SpJSONParser();

		return spJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Sp sp) {
		if (sp == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (sp.getAllowShowingTheLoginPortlet() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"allowShowingTheLoginPortlet\": ");

			sb.append(sp.getAllowShowingTheLoginPortlet());
		}

		if (sp.getAssertionSignatureRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assertionSignatureRequired\": ");

			sb.append(sp.getAssertionSignatureRequired());
		}

		if (sp.getClockSkew() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clockSkew\": ");

			sb.append(sp.getClockSkew());
		}

		if (sp.getKeyStoreEncryptionCredentialPassword() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keyStoreEncryptionCredentialPassword\": ");

			sb.append("\"");

			sb.append(_escape(sp.getKeyStoreEncryptionCredentialPassword()));

			sb.append("\"");
		}

		if (sp.getLdapImportEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ldapImportEnabled\": ");

			sb.append(sp.getLdapImportEnabled());
		}

		if (sp.getSignAuthnRequest() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"signAuthnRequest\": ");

			sb.append(sp.getSignAuthnRequest());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SpJSONParser spJSONParser = new SpJSONParser();

		return spJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Sp sp) {
		if (sp == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (sp.getAllowShowingTheLoginPortlet() == null) {
			map.put("allowShowingTheLoginPortlet", null);
		}
		else {
			map.put(
				"allowShowingTheLoginPortlet",
				String.valueOf(sp.getAllowShowingTheLoginPortlet()));
		}

		if (sp.getAssertionSignatureRequired() == null) {
			map.put("assertionSignatureRequired", null);
		}
		else {
			map.put(
				"assertionSignatureRequired",
				String.valueOf(sp.getAssertionSignatureRequired()));
		}

		if (sp.getClockSkew() == null) {
			map.put("clockSkew", null);
		}
		else {
			map.put("clockSkew", String.valueOf(sp.getClockSkew()));
		}

		if (sp.getKeyStoreEncryptionCredentialPassword() == null) {
			map.put("keyStoreEncryptionCredentialPassword", null);
		}
		else {
			map.put(
				"keyStoreEncryptionCredentialPassword",
				String.valueOf(sp.getKeyStoreEncryptionCredentialPassword()));
		}

		if (sp.getLdapImportEnabled() == null) {
			map.put("ldapImportEnabled", null);
		}
		else {
			map.put(
				"ldapImportEnabled", String.valueOf(sp.getLdapImportEnabled()));
		}

		if (sp.getSignAuthnRequest() == null) {
			map.put("signAuthnRequest", null);
		}
		else {
			map.put(
				"signAuthnRequest", String.valueOf(sp.getSignAuthnRequest()));
		}

		return map;
	}

	public static class SpJSONParser extends BaseJSONParser<Sp> {

		@Override
		protected Sp createDTO() {
			return new Sp();
		}

		@Override
		protected Sp[] createDTOArray(int size) {
			return new Sp[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "allowShowingTheLoginPortlet")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "assertionSignatureRequired")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "clockSkew")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"keyStoreEncryptionCredentialPassword")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "ldapImportEnabled")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "signAuthnRequest")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Sp sp, String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "allowShowingTheLoginPortlet")) {

				if (jsonParserFieldValue != null) {
					sp.setAllowShowingTheLoginPortlet(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "assertionSignatureRequired")) {

				if (jsonParserFieldValue != null) {
					sp.setAssertionSignatureRequired(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "clockSkew")) {
				if (jsonParserFieldValue != null) {
					sp.setClockSkew(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"keyStoreEncryptionCredentialPassword")) {

				if (jsonParserFieldValue != null) {
					sp.setKeyStoreEncryptionCredentialPassword(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ldapImportEnabled")) {
				if (jsonParserFieldValue != null) {
					sp.setLdapImportEnabled((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "signAuthnRequest")) {
				if (jsonParserFieldValue != null) {
					sp.setSignAuthnRequest((Boolean)jsonParserFieldValue);
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