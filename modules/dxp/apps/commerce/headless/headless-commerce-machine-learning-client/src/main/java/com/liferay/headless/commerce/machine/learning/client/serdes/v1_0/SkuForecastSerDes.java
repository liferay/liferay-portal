/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.client.serdes.v1_0;

import com.liferay.headless.commerce.machine.learning.client.dto.v1_0.SkuForecast;
import com.liferay.headless.commerce.machine.learning.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class SkuForecastSerDes {

	public static SkuForecast toDTO(String json) {
		SkuForecastJSONParser skuForecastJSONParser =
			new SkuForecastJSONParser();

		return skuForecastJSONParser.parseToDTO(json);
	}

	public static SkuForecast[] toDTOs(String json) {
		SkuForecastJSONParser skuForecastJSONParser =
			new SkuForecastJSONParser();

		return skuForecastJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SkuForecast skuForecast) {
		if (skuForecast == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (skuForecast.getActual() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actual\": ");

			sb.append(skuForecast.getActual());
		}

		if (skuForecast.getForecast() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"forecast\": ");

			sb.append(skuForecast.getForecast());
		}

		if (skuForecast.getForecastLowerBound() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"forecastLowerBound\": ");

			sb.append(skuForecast.getForecastLowerBound());
		}

		if (skuForecast.getForecastUpperBound() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"forecastUpperBound\": ");

			sb.append(skuForecast.getForecastUpperBound());
		}

		if (skuForecast.getSku() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sku\": ");

			sb.append("\"");

			sb.append(_escape(skuForecast.getSku()));

			sb.append("\"");
		}

		if (skuForecast.getTimestamp() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"timestamp\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(skuForecast.getTimestamp()));

			sb.append("\"");
		}

		if (skuForecast.getUnit() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unit\": ");

			sb.append("\"");

			sb.append(_escape(skuForecast.getUnit()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SkuForecastJSONParser skuForecastJSONParser =
			new SkuForecastJSONParser();

		return skuForecastJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SkuForecast skuForecast) {
		if (skuForecast == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (skuForecast.getActual() == null) {
			map.put("actual", null);
		}
		else {
			map.put("actual", String.valueOf(skuForecast.getActual()));
		}

		if (skuForecast.getForecast() == null) {
			map.put("forecast", null);
		}
		else {
			map.put("forecast", String.valueOf(skuForecast.getForecast()));
		}

		if (skuForecast.getForecastLowerBound() == null) {
			map.put("forecastLowerBound", null);
		}
		else {
			map.put(
				"forecastLowerBound",
				String.valueOf(skuForecast.getForecastLowerBound()));
		}

		if (skuForecast.getForecastUpperBound() == null) {
			map.put("forecastUpperBound", null);
		}
		else {
			map.put(
				"forecastUpperBound",
				String.valueOf(skuForecast.getForecastUpperBound()));
		}

		if (skuForecast.getSku() == null) {
			map.put("sku", null);
		}
		else {
			map.put("sku", String.valueOf(skuForecast.getSku()));
		}

		if (skuForecast.getTimestamp() == null) {
			map.put("timestamp", null);
		}
		else {
			map.put(
				"timestamp",
				liferayToJSONDateFormat.format(skuForecast.getTimestamp()));
		}

		if (skuForecast.getUnit() == null) {
			map.put("unit", null);
		}
		else {
			map.put("unit", String.valueOf(skuForecast.getUnit()));
		}

		return map;
	}

	public static class SkuForecastJSONParser
		extends BaseJSONParser<SkuForecast> {

		@Override
		protected SkuForecast createDTO() {
			return new SkuForecast();
		}

		@Override
		protected SkuForecast[] createDTOArray(int size) {
			return new SkuForecast[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actual")) {
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
			else if (Objects.equals(jsonParserFieldName, "sku")) {
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
			SkuForecast skuForecast, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actual")) {
				if (jsonParserFieldValue != null) {
					skuForecast.setActual(
						Float.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "forecast")) {
				if (jsonParserFieldValue != null) {
					skuForecast.setForecast(
						Float.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "forecastLowerBound")) {

				if (jsonParserFieldValue != null) {
					skuForecast.setForecastLowerBound(
						Float.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "forecastUpperBound")) {

				if (jsonParserFieldValue != null) {
					skuForecast.setForecastUpperBound(
						Float.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sku")) {
				if (jsonParserFieldValue != null) {
					skuForecast.setSku((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "timestamp")) {
				if (jsonParserFieldValue != null) {
					skuForecast.setTimestamp(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "unit")) {
				if (jsonParserFieldValue != null) {
					skuForecast.setUnit((String)jsonParserFieldValue);
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