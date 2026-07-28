/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.client.serdes.v1_0;

import com.liferay.portal.security.fips.rest.client.dto.v1_0.HealthVerification;
import com.liferay.portal.security.fips.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Lucas Miranda
 * @generated
 */
@Generated("")
public class HealthVerificationSerDes {

	public static HealthVerification toDTO(String json) {
		HealthVerificationJSONParser healthVerificationJSONParser =
			new HealthVerificationJSONParser();

		return healthVerificationJSONParser.parseToDTO(json);
	}

	public static HealthVerification[] toDTOs(String json) {
		HealthVerificationJSONParser healthVerificationJSONParser =
			new HealthVerificationJSONParser();

		return healthVerificationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(HealthVerification healthVerification) {
		if (healthVerification == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (healthVerification.getDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"date\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(healthVerification.getDate()));

			sb.append("\"");
		}

		if (healthVerification.getFailedTest() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"failedTest\": ");

			sb.append("\"");

			sb.append(_escape(healthVerification.getFailedTest()));

			sb.append("\"");
		}

		if (healthVerification.getFipsState() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fipsState\": ");

			sb.append("\"");

			sb.append(_escape(healthVerification.getFipsState()));

			sb.append("\"");
		}

		if (healthVerification.getProviderMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"providerMessage\": ");

			sb.append("\"");

			sb.append(_escape(healthVerification.getProviderMessage()));

			sb.append("\"");
		}

		if (healthVerification.getProviderName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"providerName\": ");

			sb.append("\"");

			sb.append(_escape(healthVerification.getProviderName()));

			sb.append("\"");
		}

		if (healthVerification.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");
			sb.append(healthVerification.getStatus());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		HealthVerificationJSONParser healthVerificationJSONParser =
			new HealthVerificationJSONParser();

		return healthVerificationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		HealthVerification healthVerification) {

		if (healthVerification == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (healthVerification.getDate() == null) {
			map.put("date", null);
		}
		else {
			map.put(
				"date",
				liferayToJSONDateFormat.format(healthVerification.getDate()));
		}

		if (healthVerification.getFailedTest() == null) {
			map.put("failedTest", null);
		}
		else {
			map.put(
				"failedTest",
				String.valueOf(healthVerification.getFailedTest()));
		}

		if (healthVerification.getFipsState() == null) {
			map.put("fipsState", null);
		}
		else {
			map.put(
				"fipsState", String.valueOf(healthVerification.getFipsState()));
		}

		if (healthVerification.getProviderMessage() == null) {
			map.put("providerMessage", null);
		}
		else {
			map.put(
				"providerMessage",
				String.valueOf(healthVerification.getProviderMessage()));
		}

		if (healthVerification.getProviderName() == null) {
			map.put("providerName", null);
		}
		else {
			map.put(
				"providerName",
				String.valueOf(healthVerification.getProviderName()));
		}

		if (healthVerification.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(healthVerification.getStatus()));
		}

		return map;
	}

	public static class HealthVerificationJSONParser
		extends BaseJSONParser<HealthVerification> {

		@Override
		protected HealthVerification createDTO() {
			return new HealthVerification();
		}

		@Override
		protected HealthVerification[] createDTOArray(int size) {
			return new HealthVerification[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "date")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "failedTest")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fipsState")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "providerMessage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "providerName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			HealthVerification healthVerification, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "date")) {
				if (jsonParserFieldValue != null) {
					healthVerification.setDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "failedTest")) {
				if (jsonParserFieldValue != null) {
					healthVerification.setFailedTest(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fipsState")) {
				if (jsonParserFieldValue != null) {
					healthVerification.setFipsState(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "providerMessage")) {
				if (jsonParserFieldValue != null) {
					healthVerification.setProviderMessage(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "providerName")) {
				if (jsonParserFieldValue != null) {
					healthVerification.setProviderName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					healthVerification.setStatus(
						HealthVerification.Status.create(
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
// LIFERAY-REST-BUILDER-HASH:-792861771