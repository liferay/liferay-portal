/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.serdes.v1_0;

import com.liferay.analytics.cms.rest.client.dto.v1_0.Histogram;
import com.liferay.analytics.cms.rest.client.dto.v1_0.ObjectEntryHistogramMetric;
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
public class ObjectEntryHistogramMetricSerDes {

	public static ObjectEntryHistogramMetric toDTO(String json) {
		ObjectEntryHistogramMetricJSONParser
			objectEntryHistogramMetricJSONParser =
				new ObjectEntryHistogramMetricJSONParser();

		return objectEntryHistogramMetricJSONParser.parseToDTO(json);
	}

	public static ObjectEntryHistogramMetric[] toDTOs(String json) {
		ObjectEntryHistogramMetricJSONParser
			objectEntryHistogramMetricJSONParser =
				new ObjectEntryHistogramMetricJSONParser();

		return objectEntryHistogramMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ObjectEntryHistogramMetric objectEntryHistogramMetric) {

		if (objectEntryHistogramMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (objectEntryHistogramMetric.getHistograms() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"histograms\": ");

			sb.append("[");

			for (int i = 0;
				 i < objectEntryHistogramMetric.getHistograms().length; i++) {

				sb.append(
					String.valueOf(
						objectEntryHistogramMetric.getHistograms()[i]));

				if ((i + 1) <
						objectEntryHistogramMetric.getHistograms().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectEntryHistogramMetricJSONParser
			objectEntryHistogramMetricJSONParser =
				new ObjectEntryHistogramMetricJSONParser();

		return objectEntryHistogramMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ObjectEntryHistogramMetric objectEntryHistogramMetric) {

		if (objectEntryHistogramMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (objectEntryHistogramMetric.getHistograms() == null) {
			map.put("histograms", null);
		}
		else {
			map.put(
				"histograms",
				String.valueOf(objectEntryHistogramMetric.getHistograms()));
		}

		return map;
	}

	public static class ObjectEntryHistogramMetricJSONParser
		extends BaseJSONParser<ObjectEntryHistogramMetric> {

		@Override
		protected ObjectEntryHistogramMetric createDTO() {
			return new ObjectEntryHistogramMetric();
		}

		@Override
		protected ObjectEntryHistogramMetric[] createDTOArray(int size) {
			return new ObjectEntryHistogramMetric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "histograms")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectEntryHistogramMetric objectEntryHistogramMetric,
			String jsonParserFieldName, Object jsonParserFieldValue) {

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

					objectEntryHistogramMetric.setHistograms(histogramsArray);
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