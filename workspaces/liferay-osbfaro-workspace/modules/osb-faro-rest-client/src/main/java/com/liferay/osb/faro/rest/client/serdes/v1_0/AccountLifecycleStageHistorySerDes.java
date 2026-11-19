/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.AccountLifecycleStageHistory;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

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
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class AccountLifecycleStageHistorySerDes {

	public static AccountLifecycleStageHistory toDTO(String json) {
		AccountLifecycleStageHistoryJSONParser
			accountLifecycleStageHistoryJSONParser =
				new AccountLifecycleStageHistoryJSONParser();

		return accountLifecycleStageHistoryJSONParser.parseToDTO(json);
	}

	public static AccountLifecycleStageHistory[] toDTOs(String json) {
		AccountLifecycleStageHistoryJSONParser
			accountLifecycleStageHistoryJSONParser =
				new AccountLifecycleStageHistoryJSONParser();

		return accountLifecycleStageHistoryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		AccountLifecycleStageHistory accountLifecycleStageHistory) {

		if (accountLifecycleStageHistory == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (accountLifecycleStageHistory.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(accountLifecycleStageHistory.getDescription()));

			sb.append("\"");
		}

		if (accountLifecycleStageHistory.getEndDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"endDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					accountLifecycleStageHistory.getEndDate()));

			sb.append("\"");
		}

		if (accountLifecycleStageHistory.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(accountLifecycleStageHistory.getId()));

			sb.append("\"");
		}

		if (accountLifecycleStageHistory.getMaxDuration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxDuration\": ");

			sb.append(accountLifecycleStageHistory.getMaxDuration());
		}

		if (accountLifecycleStageHistory.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(accountLifecycleStageHistory.getName()));

			sb.append("\"");
		}

		if (accountLifecycleStageHistory.getPosition() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"position\": ");

			sb.append(accountLifecycleStageHistory.getPosition());
		}

		if (accountLifecycleStageHistory.getStartDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"startDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					accountLifecycleStageHistory.getStartDate()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AccountLifecycleStageHistoryJSONParser
			accountLifecycleStageHistoryJSONParser =
				new AccountLifecycleStageHistoryJSONParser();

		return accountLifecycleStageHistoryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AccountLifecycleStageHistory accountLifecycleStageHistory) {

		if (accountLifecycleStageHistory == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (accountLifecycleStageHistory.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(accountLifecycleStageHistory.getDescription()));
		}

		if (accountLifecycleStageHistory.getEndDate() == null) {
			map.put("endDate", null);
		}
		else {
			map.put(
				"endDate",
				liferayToJSONDateFormat.format(
					accountLifecycleStageHistory.getEndDate()));
		}

		if (accountLifecycleStageHistory.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(accountLifecycleStageHistory.getId()));
		}

		if (accountLifecycleStageHistory.getMaxDuration() == null) {
			map.put("maxDuration", null);
		}
		else {
			map.put(
				"maxDuration",
				String.valueOf(accountLifecycleStageHistory.getMaxDuration()));
		}

		if (accountLifecycleStageHistory.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(accountLifecycleStageHistory.getName()));
		}

		if (accountLifecycleStageHistory.getPosition() == null) {
			map.put("position", null);
		}
		else {
			map.put(
				"position",
				String.valueOf(accountLifecycleStageHistory.getPosition()));
		}

		if (accountLifecycleStageHistory.getStartDate() == null) {
			map.put("startDate", null);
		}
		else {
			map.put(
				"startDate",
				liferayToJSONDateFormat.format(
					accountLifecycleStageHistory.getStartDate()));
		}

		return map;
	}

	public static class AccountLifecycleStageHistoryJSONParser
		extends BaseJSONParser<AccountLifecycleStageHistory> {

		@Override
		protected AccountLifecycleStageHistory createDTO() {
			return new AccountLifecycleStageHistory();
		}

		@Override
		protected AccountLifecycleStageHistory[] createDTOArray(int size) {
			return new AccountLifecycleStageHistory[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "endDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "maxDuration")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "position")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "startDate")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AccountLifecycleStageHistory accountLifecycleStageHistory,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStageHistory.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "endDate")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStageHistory.setEndDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStageHistory.setId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "maxDuration")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStageHistory.setMaxDuration(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStageHistory.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "position")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStageHistory.setPosition(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "startDate")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStageHistory.setStartDate(
						toDate((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:-1740214522