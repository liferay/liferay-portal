/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.serdes.v1_0;

import com.liferay.analytics.cms.rest.client.dto.v1_0.Metric;
import com.liferay.analytics.cms.rest.client.dto.v1_0.ObjectEntryMetric;
import com.liferay.analytics.cms.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
public class ObjectEntryMetricSerDes {

	public static ObjectEntryMetric toDTO(String json) {
		ObjectEntryMetricJSONParser objectEntryMetricJSONParser =
			new ObjectEntryMetricJSONParser();

		return objectEntryMetricJSONParser.parseToDTO(json);
	}

	public static ObjectEntryMetric[] toDTOs(String json) {
		ObjectEntryMetricJSONParser objectEntryMetricJSONParser =
			new ObjectEntryMetricJSONParser();

		return objectEntryMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectEntryMetric objectEntryMetric) {
		if (objectEntryMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (objectEntryMetric.getDataSourceId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataSourceId\": ");

			sb.append("\"");

			sb.append(_escape(objectEntryMetric.getDataSourceId()));

			sb.append("\"");
		}

		if (objectEntryMetric.getDefaultMetric() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultMetric\": ");

			sb.append(String.valueOf(objectEntryMetric.getDefaultMetric()));
		}

		if (objectEntryMetric.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(objectEntryMetric.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (objectEntryMetric.getSelectedMetrics() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectedMetrics\": ");

			sb.append("[");

			for (int i = 0; i < objectEntryMetric.getSelectedMetrics().length;
				 i++) {

				sb.append(
					String.valueOf(objectEntryMetric.getSelectedMetrics()[i]));

				if ((i + 1) < objectEntryMetric.getSelectedMetrics().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectEntryMetricJSONParser objectEntryMetricJSONParser =
			new ObjectEntryMetricJSONParser();

		return objectEntryMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ObjectEntryMetric objectEntryMetric) {

		if (objectEntryMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (objectEntryMetric.getDataSourceId() == null) {
			map.put("dataSourceId", null);
		}
		else {
			map.put(
				"dataSourceId",
				String.valueOf(objectEntryMetric.getDataSourceId()));
		}

		if (objectEntryMetric.getDefaultMetric() == null) {
			map.put("defaultMetric", null);
		}
		else {
			map.put(
				"defaultMetric",
				String.valueOf(objectEntryMetric.getDefaultMetric()));
		}

		if (objectEntryMetric.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(objectEntryMetric.getExternalReferenceCode()));
		}

		if (objectEntryMetric.getSelectedMetrics() == null) {
			map.put("selectedMetrics", null);
		}
		else {
			map.put(
				"selectedMetrics",
				String.valueOf(objectEntryMetric.getSelectedMetrics()));
		}

		return map;
	}

	public static class ObjectEntryMetricJSONParser
		extends BaseJSONParser<ObjectEntryMetric> {

		@Override
		protected ObjectEntryMetric createDTO() {
			return new ObjectEntryMetric();
		}

		@Override
		protected ObjectEntryMetric[] createDTOArray(int size) {
			return new ObjectEntryMetric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dataSourceId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultMetric")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "selectedMetrics")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectEntryMetric objectEntryMetric, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dataSourceId")) {
				if (jsonParserFieldValue != null) {
					objectEntryMetric.setDataSourceId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultMetric")) {
				if (jsonParserFieldValue != null) {
					objectEntryMetric.setDefaultMetric(
						MetricSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					objectEntryMetric.setExternalReferenceCode(
						(String)jsonParserFieldValue);
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

					objectEntryMetric.setSelectedMetrics(selectedMetricsArray);
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