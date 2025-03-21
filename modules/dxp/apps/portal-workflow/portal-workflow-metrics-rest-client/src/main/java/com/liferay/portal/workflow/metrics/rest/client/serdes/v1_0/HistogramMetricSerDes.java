/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Histogram;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.HistogramMetric;
import com.liferay.portal.workflow.metrics.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class HistogramMetricSerDes {

	public static HistogramMetric toDTO(String json) {
		HistogramMetricJSONParser histogramMetricJSONParser =
			new HistogramMetricJSONParser();

		return histogramMetricJSONParser.parseToDTO(json);
	}

	public static HistogramMetric[] toDTOs(String json) {
		HistogramMetricJSONParser histogramMetricJSONParser =
			new HistogramMetricJSONParser();

		return histogramMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(HistogramMetric histogramMetric) {
		if (histogramMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (histogramMetric.getHistograms() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"histograms\": ");

			sb.append("[");

			for (int i = 0; i < histogramMetric.getHistograms().length; i++) {
				sb.append(String.valueOf(histogramMetric.getHistograms()[i]));

				if ((i + 1) < histogramMetric.getHistograms().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (histogramMetric.getUnit() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unit\": ");

			sb.append("\"");

			sb.append(histogramMetric.getUnit());

			sb.append("\"");
		}

		if (histogramMetric.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append(histogramMetric.getValue());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		HistogramMetricJSONParser histogramMetricJSONParser =
			new HistogramMetricJSONParser();

		return histogramMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(HistogramMetric histogramMetric) {
		if (histogramMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (histogramMetric.getHistograms() == null) {
			map.put("histograms", null);
		}
		else {
			map.put(
				"histograms", String.valueOf(histogramMetric.getHistograms()));
		}

		if (histogramMetric.getUnit() == null) {
			map.put("unit", null);
		}
		else {
			map.put("unit", String.valueOf(histogramMetric.getUnit()));
		}

		if (histogramMetric.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(histogramMetric.getValue()));
		}

		return map;
	}

	public static class HistogramMetricJSONParser
		extends BaseJSONParser<HistogramMetric> {

		@Override
		protected HistogramMetric createDTO() {
			return new HistogramMetric();
		}

		@Override
		protected HistogramMetric[] createDTOArray(int size) {
			return new HistogramMetric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "histograms")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "unit")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			HistogramMetric histogramMetric, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "histograms")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Histogram[] histogramsArray =
						new Histogram[jsonParserFieldValues.length];

					for (int i = 0; i < histogramsArray.length; i++) {
						histogramsArray[i] = HistogramSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					histogramMetric.setHistograms(histogramsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "unit")) {
				if (jsonParserFieldValue != null) {
					histogramMetric.setUnit(
						HistogramMetric.Unit.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					histogramMetric.setValue(
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