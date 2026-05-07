/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.serdes.v1_0;

import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.SiteHistogramMetric;
import com.liferay.site.dsr.analytics.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Gianmarco Brunialti
 * @generated
 */
@Generated("")
public class SiteHistogramMetricSerDes {

	public static SiteHistogramMetric toDTO(String json) {
		SiteHistogramMetricJSONParser siteHistogramMetricJSONParser =
			new SiteHistogramMetricJSONParser();

		return siteHistogramMetricJSONParser.parseToDTO(json);
	}

	public static SiteHistogramMetric[] toDTOs(String json) {
		SiteHistogramMetricJSONParser siteHistogramMetricJSONParser =
			new SiteHistogramMetricJSONParser();

		return siteHistogramMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SiteHistogramMetric siteHistogramMetric) {
		if (siteHistogramMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (siteHistogramMetric.getHistogram() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"histogram\": ");

			sb.append(String.valueOf(siteHistogramMetric.getHistogram()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SiteHistogramMetricJSONParser siteHistogramMetricJSONParser =
			new SiteHistogramMetricJSONParser();

		return siteHistogramMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SiteHistogramMetric siteHistogramMetric) {

		if (siteHistogramMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (siteHistogramMetric.getHistogram() == null) {
			map.put("histogram", null);
		}
		else {
			map.put(
				"histogram",
				String.valueOf(siteHistogramMetric.getHistogram()));
		}

		return map;
	}

	public static class SiteHistogramMetricJSONParser
		extends BaseJSONParser<SiteHistogramMetric> {

		@Override
		protected SiteHistogramMetric createDTO() {
			return new SiteHistogramMetric();
		}

		@Override
		protected SiteHistogramMetric[] createDTOArray(int size) {
			return new SiteHistogramMetric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "histogram")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SiteHistogramMetric siteHistogramMetric, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "histogram")) {
				if (jsonParserFieldValue != null) {
					siteHistogramMetric.setHistogram(
						HistogramSerDes.toDTO((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:1439543644