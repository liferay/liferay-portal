/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.CampaignMetric;
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
public class CampaignMetricSerDes {

	public static CampaignMetric toDTO(String json) {
		CampaignMetricJSONParser campaignMetricJSONParser =
			new CampaignMetricJSONParser();

		return campaignMetricJSONParser.parseToDTO(json);
	}

	public static CampaignMetric[] toDTOs(String json) {
		CampaignMetricJSONParser campaignMetricJSONParser =
			new CampaignMetricJSONParser();

		return campaignMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CampaignMetric campaignMetric) {
		if (campaignMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (campaignMetric.getMetricType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metricType\": ");

			sb.append("\"");

			sb.append(_escape(campaignMetric.getMetricType()));

			sb.append("\"");
		}

		if (campaignMetric.getPreviousValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"previousValue\": ");

			sb.append(campaignMetric.getPreviousValue());
		}

		if (campaignMetric.getTrendClassification() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trendClassification\": ");

			sb.append("\"");

			sb.append(_escape(campaignMetric.getTrendClassification()));

			sb.append("\"");
		}

		if (campaignMetric.getTrendPercentage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trendPercentage\": ");

			sb.append(campaignMetric.getTrendPercentage());
		}

		if (campaignMetric.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append(campaignMetric.getValue());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CampaignMetricJSONParser campaignMetricJSONParser =
			new CampaignMetricJSONParser();

		return campaignMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(CampaignMetric campaignMetric) {
		if (campaignMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (campaignMetric.getMetricType() == null) {
			map.put("metricType", null);
		}
		else {
			map.put(
				"metricType", String.valueOf(campaignMetric.getMetricType()));
		}

		if (campaignMetric.getPreviousValue() == null) {
			map.put("previousValue", null);
		}
		else {
			map.put(
				"previousValue",
				String.valueOf(campaignMetric.getPreviousValue()));
		}

		if (campaignMetric.getTrendClassification() == null) {
			map.put("trendClassification", null);
		}
		else {
			map.put(
				"trendClassification",
				String.valueOf(campaignMetric.getTrendClassification()));
		}

		if (campaignMetric.getTrendPercentage() == null) {
			map.put("trendPercentage", null);
		}
		else {
			map.put(
				"trendPercentage",
				String.valueOf(campaignMetric.getTrendPercentage()));
		}

		if (campaignMetric.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(campaignMetric.getValue()));
		}

		return map;
	}

	public static class CampaignMetricJSONParser
		extends BaseJSONParser<CampaignMetric> {

		@Override
		protected CampaignMetric createDTO() {
			return new CampaignMetric();
		}

		@Override
		protected CampaignMetric[] createDTOArray(int size) {
			return new CampaignMetric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "metricType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "previousValue")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "trendClassification")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "trendPercentage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CampaignMetric campaignMetric, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "metricType")) {
				if (jsonParserFieldValue != null) {
					campaignMetric.setMetricType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "previousValue")) {
				if (jsonParserFieldValue != null) {
					campaignMetric.setPreviousValue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "trendClassification")) {

				if (jsonParserFieldValue != null) {
					campaignMetric.setTrendClassification(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "trendPercentage")) {
				if (jsonParserFieldValue != null) {
					campaignMetric.setTrendPercentage(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					campaignMetric.setValue(
						Double.valueOf((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:-1600833210