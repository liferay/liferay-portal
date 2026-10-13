/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.client.serdes.v1_0;

import com.liferay.digital.signature.rest.client.dto.v1_0.SignatureRequestRecipient;
import com.liferay.digital.signature.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author José Abelenda
 * @generated
 */
@Generated("")
public class SignatureRequestRecipientSerDes {

	public static SignatureRequestRecipient toDTO(String json) {
		SignatureRequestRecipientJSONParser
			signatureRequestRecipientJSONParser =
				new SignatureRequestRecipientJSONParser();

		return signatureRequestRecipientJSONParser.parseToDTO(json);
	}

	public static SignatureRequestRecipient[] toDTOs(String json) {
		SignatureRequestRecipientJSONParser
			signatureRequestRecipientJSONParser =
				new SignatureRequestRecipientJSONParser();

		return signatureRequestRecipientJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		SignatureRequestRecipient signatureRequestRecipient) {

		if (signatureRequestRecipient == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (signatureRequestRecipient.getEmailAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddress\": ");

			sb.append("\"");

			sb.append(_escape(signatureRequestRecipient.getEmailAddress()));

			sb.append("\"");
		}

		if (signatureRequestRecipient.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(signatureRequestRecipient.getName()));

			sb.append("\"");
		}

		if (signatureRequestRecipient.getSentDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sentDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					signatureRequestRecipient.getSentDate()));

			sb.append("\"");
		}

		if (signatureRequestRecipient.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(_escape(signatureRequestRecipient.getStatus()));

			sb.append("\"");
		}

		if (signatureRequestRecipient.getStatusDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"statusDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					signatureRequestRecipient.getStatusDate()));

			sb.append("\"");
		}

		if (signatureRequestRecipient.getUserId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userId\": ");

			sb.append(signatureRequestRecipient.getUserId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SignatureRequestRecipientJSONParser
			signatureRequestRecipientJSONParser =
				new SignatureRequestRecipientJSONParser();

		return signatureRequestRecipientJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SignatureRequestRecipient signatureRequestRecipient) {

		if (signatureRequestRecipient == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (signatureRequestRecipient.getEmailAddress() == null) {
			map.put("emailAddress", null);
		}
		else {
			map.put(
				"emailAddress",
				String.valueOf(signatureRequestRecipient.getEmailAddress()));
		}

		if (signatureRequestRecipient.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(signatureRequestRecipient.getName()));
		}

		if (signatureRequestRecipient.getSentDate() == null) {
			map.put("sentDate", null);
		}
		else {
			map.put(
				"sentDate",
				liferayToJSONDateFormat.format(
					signatureRequestRecipient.getSentDate()));
		}

		if (signatureRequestRecipient.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put(
				"status",
				String.valueOf(signatureRequestRecipient.getStatus()));
		}

		if (signatureRequestRecipient.getStatusDate() == null) {
			map.put("statusDate", null);
		}
		else {
			map.put(
				"statusDate",
				liferayToJSONDateFormat.format(
					signatureRequestRecipient.getStatusDate()));
		}

		if (signatureRequestRecipient.getUserId() == null) {
			map.put("userId", null);
		}
		else {
			map.put(
				"userId",
				String.valueOf(signatureRequestRecipient.getUserId()));
		}

		return map;
	}

	public static class SignatureRequestRecipientJSONParser
		extends BaseJSONParser<SignatureRequestRecipient> {

		@Override
		protected SignatureRequestRecipient createDTO() {
			return new SignatureRequestRecipient();
		}

		@Override
		protected SignatureRequestRecipient[] createDTOArray(int size) {
			return new SignatureRequestRecipient[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sentDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "statusDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SignatureRequestRecipient signatureRequestRecipient,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				if (jsonParserFieldValue != null) {
					signatureRequestRecipient.setEmailAddress(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					signatureRequestRecipient.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sentDate")) {
				if (jsonParserFieldValue != null) {
					signatureRequestRecipient.setSentDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					signatureRequestRecipient.setStatus(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "statusDate")) {
				if (jsonParserFieldValue != null) {
					signatureRequestRecipient.setStatusDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				if (jsonParserFieldValue != null) {
					signatureRequestRecipient.setUserId(
						Long.valueOf((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:-173248146