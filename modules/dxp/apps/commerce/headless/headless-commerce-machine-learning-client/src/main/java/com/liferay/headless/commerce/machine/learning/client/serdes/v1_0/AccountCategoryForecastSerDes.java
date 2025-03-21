/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.client.serdes.v1_0;

import com.liferay.headless.commerce.machine.learning.client.dto.v1_0.AccountCategoryForecast;
import com.liferay.headless.commerce.machine.learning.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class AccountCategoryForecastSerDes {

	public static AccountCategoryForecast toDTO(String json) {
		AccountCategoryForecastJSONParser accountCategoryForecastJSONParser =
			new AccountCategoryForecastJSONParser();

		return accountCategoryForecastJSONParser.parseToDTO(json);
	}

	public static AccountCategoryForecast[] toDTOs(String json) {
		AccountCategoryForecastJSONParser accountCategoryForecastJSONParser =
			new AccountCategoryForecastJSONParser();

		return accountCategoryForecastJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		AccountCategoryForecast accountCategoryForecast) {

		if (accountCategoryForecast == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (accountCategoryForecast.getAccount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"account\": ");

			sb.append(accountCategoryForecast.getAccount());
		}

		if (accountCategoryForecast.getActual() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actual\": ");

			sb.append(accountCategoryForecast.getActual());
		}

		if (accountCategoryForecast.getCategory() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"category\": ");

			sb.append(accountCategoryForecast.getCategory());
		}

		if (accountCategoryForecast.getCategoryTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"categoryTitle\": ");

			sb.append("\"");

			sb.append(_escape(accountCategoryForecast.getCategoryTitle()));

			sb.append("\"");
		}

		if (accountCategoryForecast.getForecast() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"forecast\": ");

			sb.append(accountCategoryForecast.getForecast());
		}

		if (accountCategoryForecast.getForecastLowerBound() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"forecastLowerBound\": ");

			sb.append(accountCategoryForecast.getForecastLowerBound());
		}

		if (accountCategoryForecast.getForecastUpperBound() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"forecastUpperBound\": ");

			sb.append(accountCategoryForecast.getForecastUpperBound());
		}

		if (accountCategoryForecast.getTimestamp() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"timestamp\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					accountCategoryForecast.getTimestamp()));

			sb.append("\"");
		}

		if (accountCategoryForecast.getUnit() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unit\": ");

			sb.append("\"");

			sb.append(_escape(accountCategoryForecast.getUnit()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AccountCategoryForecastJSONParser accountCategoryForecastJSONParser =
			new AccountCategoryForecastJSONParser();

		return accountCategoryForecastJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AccountCategoryForecast accountCategoryForecast) {

		if (accountCategoryForecast == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (accountCategoryForecast.getAccount() == null) {
			map.put("account", null);
		}
		else {
			map.put(
				"account",
				String.valueOf(accountCategoryForecast.getAccount()));
		}

		if (accountCategoryForecast.getActual() == null) {
			map.put("actual", null);
		}
		else {
			map.put(
				"actual", String.valueOf(accountCategoryForecast.getActual()));
		}

		if (accountCategoryForecast.getCategory() == null) {
			map.put("category", null);
		}
		else {
			map.put(
				"category",
				String.valueOf(accountCategoryForecast.getCategory()));
		}

		if (accountCategoryForecast.getCategoryTitle() == null) {
			map.put("categoryTitle", null);
		}
		else {
			map.put(
				"categoryTitle",
				String.valueOf(accountCategoryForecast.getCategoryTitle()));
		}

		if (accountCategoryForecast.getForecast() == null) {
			map.put("forecast", null);
		}
		else {
			map.put(
				"forecast",
				String.valueOf(accountCategoryForecast.getForecast()));
		}

		if (accountCategoryForecast.getForecastLowerBound() == null) {
			map.put("forecastLowerBound", null);
		}
		else {
			map.put(
				"forecastLowerBound",
				String.valueOf(
					accountCategoryForecast.getForecastLowerBound()));
		}

		if (accountCategoryForecast.getForecastUpperBound() == null) {
			map.put("forecastUpperBound", null);
		}
		else {
			map.put(
				"forecastUpperBound",
				String.valueOf(
					accountCategoryForecast.getForecastUpperBound()));
		}

		if (accountCategoryForecast.getTimestamp() == null) {
			map.put("timestamp", null);
		}
		else {
			map.put(
				"timestamp",
				liferayToJSONDateFormat.format(
					accountCategoryForecast.getTimestamp()));
		}

		if (accountCategoryForecast.getUnit() == null) {
			map.put("unit", null);
		}
		else {
			map.put("unit", String.valueOf(accountCategoryForecast.getUnit()));
		}

		return map;
	}

	public static class AccountCategoryForecastJSONParser
		extends BaseJSONParser<AccountCategoryForecast> {

		@Override
		protected AccountCategoryForecast createDTO() {
			return new AccountCategoryForecast();
		}

		@Override
		protected AccountCategoryForecast[] createDTOArray(int size) {
			return new AccountCategoryForecast[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "account")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "actual")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "category")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "categoryTitle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "forecast")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "forecastLowerBound")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "forecastUpperBound")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "timestamp")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "unit")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AccountCategoryForecast accountCategoryForecast,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "account")) {
				if (jsonParserFieldValue != null) {
					accountCategoryForecast.setAccount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actual")) {
				if (jsonParserFieldValue != null) {
					accountCategoryForecast.setActual(
						Float.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "category")) {
				if (jsonParserFieldValue != null) {
					accountCategoryForecast.setCategory(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "categoryTitle")) {
				if (jsonParserFieldValue != null) {
					accountCategoryForecast.setCategoryTitle(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "forecast")) {
				if (jsonParserFieldValue != null) {
					accountCategoryForecast.setForecast(
						Float.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "forecastLowerBound")) {

				if (jsonParserFieldValue != null) {
					accountCategoryForecast.setForecastLowerBound(
						Float.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "forecastUpperBound")) {

				if (jsonParserFieldValue != null) {
					accountCategoryForecast.setForecastUpperBound(
						Float.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "timestamp")) {
				if (jsonParserFieldValue != null) {
					accountCategoryForecast.setTimestamp(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "unit")) {
				if (jsonParserFieldValue != null) {
					accountCategoryForecast.setUnit(
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