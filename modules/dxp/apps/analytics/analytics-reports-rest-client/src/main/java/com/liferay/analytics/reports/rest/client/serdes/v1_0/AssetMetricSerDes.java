/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.client.serdes.v1_0;

import com.liferay.analytics.reports.rest.client.dto.v1_0.AssetMetric;
import com.liferay.analytics.reports.rest.client.dto.v1_0.Metric;
import com.liferay.analytics.reports.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
public class AssetMetricSerDes {

	public static AssetMetric toDTO(String json) {
		AssetMetricJSONParser assetMetricJSONParser =
			new AssetMetricJSONParser();

		return assetMetricJSONParser.parseToDTO(json);
	}

	public static AssetMetric[] toDTOs(String json) {
		AssetMetricJSONParser assetMetricJSONParser =
			new AssetMetricJSONParser();

		return assetMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AssetMetric assetMetric) {
		if (assetMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (assetMetric.getAssetId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetId\": ");

			sb.append("\"");

			sb.append(_escape(assetMetric.getAssetId()));

			sb.append("\"");
		}

		if (assetMetric.getAssetTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle\": ");

			sb.append("\"");

			sb.append(_escape(assetMetric.getAssetTitle()));

			sb.append("\"");
		}

		if (assetMetric.getAssetType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetType\": ");

			sb.append("\"");

			sb.append(_escape(assetMetric.getAssetType()));

			sb.append("\"");
		}

		if (assetMetric.getDataSourceId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataSourceId\": ");

			sb.append("\"");

			sb.append(_escape(assetMetric.getDataSourceId()));

			sb.append("\"");
		}

		if (assetMetric.getDefaultMetric() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultMetric\": ");

			sb.append(String.valueOf(assetMetric.getDefaultMetric()));
		}

		if (assetMetric.getSelectedMetrics() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectedMetrics\": ");

			sb.append("[");

			for (int i = 0; i < assetMetric.getSelectedMetrics().length; i++) {
				sb.append(String.valueOf(assetMetric.getSelectedMetrics()[i]));

				if ((i + 1) < assetMetric.getSelectedMetrics().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssetMetricJSONParser assetMetricJSONParser =
			new AssetMetricJSONParser();

		return assetMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AssetMetric assetMetric) {
		if (assetMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (assetMetric.getAssetId() == null) {
			map.put("assetId", null);
		}
		else {
			map.put("assetId", String.valueOf(assetMetric.getAssetId()));
		}

		if (assetMetric.getAssetTitle() == null) {
			map.put("assetTitle", null);
		}
		else {
			map.put("assetTitle", String.valueOf(assetMetric.getAssetTitle()));
		}

		if (assetMetric.getAssetType() == null) {
			map.put("assetType", null);
		}
		else {
			map.put("assetType", String.valueOf(assetMetric.getAssetType()));
		}

		if (assetMetric.getDataSourceId() == null) {
			map.put("dataSourceId", null);
		}
		else {
			map.put(
				"dataSourceId", String.valueOf(assetMetric.getDataSourceId()));
		}

		if (assetMetric.getDefaultMetric() == null) {
			map.put("defaultMetric", null);
		}
		else {
			map.put(
				"defaultMetric",
				String.valueOf(assetMetric.getDefaultMetric()));
		}

		if (assetMetric.getSelectedMetrics() == null) {
			map.put("selectedMetrics", null);
		}
		else {
			map.put(
				"selectedMetrics",
				String.valueOf(assetMetric.getSelectedMetrics()));
		}

		return map;
	}

	public static class AssetMetricJSONParser
		extends BaseJSONParser<AssetMetric> {

		@Override
		protected AssetMetric createDTO() {
			return new AssetMetric();
		}

		@Override
		protected AssetMetric[] createDTOArray(int size) {
			return new AssetMetric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "assetId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assetType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dataSourceId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultMetric")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "selectedMetrics")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AssetMetric assetMetric, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "assetId")) {
				if (jsonParserFieldValue != null) {
					assetMetric.setAssetId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				if (jsonParserFieldValue != null) {
					assetMetric.setAssetTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetType")) {
				if (jsonParserFieldValue != null) {
					assetMetric.setAssetType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataSourceId")) {
				if (jsonParserFieldValue != null) {
					assetMetric.setDataSourceId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultMetric")) {
				if (jsonParserFieldValue != null) {
					assetMetric.setDefaultMetric(
						MetricSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selectedMetrics")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Metric[] selectedMetricsArray =
						new Metric[jsonParserFieldValues.length];

					for (int i = 0; i < selectedMetricsArray.length; i++) {
						selectedMetricsArray[i] = MetricSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					assetMetric.setSelectedMetrics(selectedMetricsArray);
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