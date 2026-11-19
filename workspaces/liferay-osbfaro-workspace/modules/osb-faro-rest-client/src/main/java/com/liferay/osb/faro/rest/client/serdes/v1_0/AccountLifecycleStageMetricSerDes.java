/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.AccountLifecycleStageMetric;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class AccountLifecycleStageMetricSerDes {

	public static AccountLifecycleStageMetric toDTO(String json) {
		AccountLifecycleStageMetricJSONParser
			accountLifecycleStageMetricJSONParser =
				new AccountLifecycleStageMetricJSONParser();

		return accountLifecycleStageMetricJSONParser.parseToDTO(json);
	}

	public static AccountLifecycleStageMetric[] toDTOs(String json) {
		AccountLifecycleStageMetricJSONParser
			accountLifecycleStageMetricJSONParser =
				new AccountLifecycleStageMetricJSONParser();

		return accountLifecycleStageMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		AccountLifecycleStageMetric accountLifecycleStageMetric) {

		if (accountLifecycleStageMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (accountLifecycleStageMetric.getAccountsCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountsCount\": ");

			sb.append(accountLifecycleStageMetric.getAccountsCount());
		}

		if (accountLifecycleStageMetric.getAverageStageDuration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"averageStageDuration\": ");

			sb.append(accountLifecycleStageMetric.getAverageStageDuration());
		}

		if (accountLifecycleStageMetric.getConversionRateToNextStage() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"conversionRateToNextStage\": ");

			sb.append(
				accountLifecycleStageMetric.getConversionRateToNextStage());
		}

		if (accountLifecycleStageMetric.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(accountLifecycleStageMetric.getDescription()));

			sb.append("\"");
		}

		if (accountLifecycleStageMetric.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(accountLifecycleStageMetric.getName()));

			sb.append("\"");
		}

		if (accountLifecycleStageMetric.getPercentage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"percentage\": ");

			sb.append(accountLifecycleStageMetric.getPercentage());
		}

		if (accountLifecycleStageMetric.getPosition() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"position\": ");

			sb.append(accountLifecycleStageMetric.getPosition());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AccountLifecycleStageMetricJSONParser
			accountLifecycleStageMetricJSONParser =
				new AccountLifecycleStageMetricJSONParser();

		return accountLifecycleStageMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AccountLifecycleStageMetric accountLifecycleStageMetric) {

		if (accountLifecycleStageMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (accountLifecycleStageMetric.getAccountsCount() == null) {
			map.put("accountsCount", null);
		}
		else {
			map.put(
				"accountsCount",
				String.valueOf(accountLifecycleStageMetric.getAccountsCount()));
		}

		if (accountLifecycleStageMetric.getAverageStageDuration() == null) {
			map.put("averageStageDuration", null);
		}
		else {
			map.put(
				"averageStageDuration",
				String.valueOf(
					accountLifecycleStageMetric.getAverageStageDuration()));
		}

		if (accountLifecycleStageMetric.getConversionRateToNextStage() ==
				null) {

			map.put("conversionRateToNextStage", null);
		}
		else {
			map.put(
				"conversionRateToNextStage",
				String.valueOf(
					accountLifecycleStageMetric.
						getConversionRateToNextStage()));
		}

		if (accountLifecycleStageMetric.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(accountLifecycleStageMetric.getDescription()));
		}

		if (accountLifecycleStageMetric.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(accountLifecycleStageMetric.getName()));
		}

		if (accountLifecycleStageMetric.getPercentage() == null) {
			map.put("percentage", null);
		}
		else {
			map.put(
				"percentage",
				String.valueOf(accountLifecycleStageMetric.getPercentage()));
		}

		if (accountLifecycleStageMetric.getPosition() == null) {
			map.put("position", null);
		}
		else {
			map.put(
				"position",
				String.valueOf(accountLifecycleStageMetric.getPosition()));
		}

		return map;
	}

	public static class AccountLifecycleStageMetricJSONParser
		extends BaseJSONParser<AccountLifecycleStageMetric> {

		@Override
		protected AccountLifecycleStageMetric createDTO() {
			return new AccountLifecycleStageMetric();
		}

		@Override
		protected AccountLifecycleStageMetric[] createDTOArray(int size) {
			return new AccountLifecycleStageMetric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accountsCount")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "averageStageDuration")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "conversionRateToNextStage")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "percentage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "position")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AccountLifecycleStageMetric accountLifecycleStageMetric,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountsCount")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStageMetric.setAccountsCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "averageStageDuration")) {

				if (jsonParserFieldValue != null) {
					accountLifecycleStageMetric.setAverageStageDuration(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "conversionRateToNextStage")) {

				if (jsonParserFieldValue != null) {
					accountLifecycleStageMetric.setConversionRateToNextStage(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStageMetric.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStageMetric.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "percentage")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStageMetric.setPercentage(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "position")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStageMetric.setPosition(
						Integer.valueOf((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:374604125