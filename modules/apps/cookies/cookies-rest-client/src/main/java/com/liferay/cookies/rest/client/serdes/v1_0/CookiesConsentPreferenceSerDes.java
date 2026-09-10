/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.rest.client.serdes.v1_0;

import com.liferay.cookies.rest.client.dto.v1_0.CookiesConsentPreference;
import com.liferay.cookies.rest.client.json.BaseJSONParser;

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
 * @author Christopher Kian
 * @generated
 */
@Generated("")
public class CookiesConsentPreferenceSerDes {

	public static CookiesConsentPreference toDTO(String json) {
		CookiesConsentPreferenceJSONParser cookiesConsentPreferenceJSONParser =
			new CookiesConsentPreferenceJSONParser();

		return cookiesConsentPreferenceJSONParser.parseToDTO(json);
	}

	public static CookiesConsentPreference[] toDTOs(String json) {
		CookiesConsentPreferenceJSONParser cookiesConsentPreferenceJSONParser =
			new CookiesConsentPreferenceJSONParser();

		return cookiesConsentPreferenceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		CookiesConsentPreference cookiesConsentPreference) {

		if (cookiesConsentPreference == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (cookiesConsentPreference.getDomain() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"domain\": ");

			sb.append("\"");

			sb.append(_escape(cookiesConsentPreference.getDomain()));

			sb.append("\"");
		}

		if (cookiesConsentPreference.getExpirationDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expirationDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					cookiesConsentPreference.getExpirationDate()));

			sb.append("\"");
		}

		if (cookiesConsentPreference.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(cookiesConsentPreference.getId());
		}

		if (cookiesConsentPreference.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(cookiesConsentPreference.getName()));

			sb.append("\"");
		}

		if (cookiesConsentPreference.getUserId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userId\": ");

			sb.append(cookiesConsentPreference.getUserId());
		}

		if (cookiesConsentPreference.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append("\"");

			sb.append(_escape(cookiesConsentPreference.getValue()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CookiesConsentPreferenceJSONParser cookiesConsentPreferenceJSONParser =
			new CookiesConsentPreferenceJSONParser();

		return cookiesConsentPreferenceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		CookiesConsentPreference cookiesConsentPreference) {

		if (cookiesConsentPreference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (cookiesConsentPreference.getDomain() == null) {
			map.put("domain", null);
		}
		else {
			map.put(
				"domain", String.valueOf(cookiesConsentPreference.getDomain()));
		}

		if (cookiesConsentPreference.getExpirationDate() == null) {
			map.put("expirationDate", null);
		}
		else {
			map.put(
				"expirationDate",
				liferayToJSONDateFormat.format(
					cookiesConsentPreference.getExpirationDate()));
		}

		if (cookiesConsentPreference.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(cookiesConsentPreference.getId()));
		}

		if (cookiesConsentPreference.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(cookiesConsentPreference.getName()));
		}

		if (cookiesConsentPreference.getUserId() == null) {
			map.put("userId", null);
		}
		else {
			map.put(
				"userId", String.valueOf(cookiesConsentPreference.getUserId()));
		}

		if (cookiesConsentPreference.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put(
				"value", String.valueOf(cookiesConsentPreference.getValue()));
		}

		return map;
	}

	public static class CookiesConsentPreferenceJSONParser
		extends BaseJSONParser<CookiesConsentPreference> {

		@Override
		protected CookiesConsentPreference createDTO() {
			return new CookiesConsentPreference();
		}

		@Override
		protected CookiesConsentPreference[] createDTOArray(int size) {
			return new CookiesConsentPreference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "domain")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CookiesConsentPreference cookiesConsentPreference,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "domain")) {
				if (jsonParserFieldValue != null) {
					cookiesConsentPreference.setDomain(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				if (jsonParserFieldValue != null) {
					cookiesConsentPreference.setExpirationDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					cookiesConsentPreference.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					cookiesConsentPreference.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				if (jsonParserFieldValue != null) {
					cookiesConsentPreference.setUserId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					cookiesConsentPreference.setValue(
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
// LIFERAY-REST-BUILDER-HASH:1014711766