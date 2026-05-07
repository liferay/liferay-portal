/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.serdes.v1_0;

import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.SiteVisitorBehaviorMetric;
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
public class SiteVisitorBehaviorMetricSerDes {

	public static SiteVisitorBehaviorMetric toDTO(String json) {
		SiteVisitorBehaviorMetricJSONParser
			siteVisitorBehaviorMetricJSONParser =
				new SiteVisitorBehaviorMetricJSONParser();

		return siteVisitorBehaviorMetricJSONParser.parseToDTO(json);
	}

	public static SiteVisitorBehaviorMetric[] toDTOs(String json) {
		SiteVisitorBehaviorMetricJSONParser
			siteVisitorBehaviorMetricJSONParser =
				new SiteVisitorBehaviorMetricJSONParser();

		return siteVisitorBehaviorMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		SiteVisitorBehaviorMetric siteVisitorBehaviorMetric) {

		if (siteVisitorBehaviorMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (siteVisitorBehaviorMetric.getAverageSessionDuration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"averageSessionDuration\": ");

			sb.append(siteVisitorBehaviorMetric.getAverageSessionDuration());
		}

		if (siteVisitorBehaviorMetric.getKnownVisitors() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"knownVisitors\": ");

			sb.append(siteVisitorBehaviorMetric.getKnownVisitors());
		}

		if (siteVisitorBehaviorMetric.getTotalSessionDuration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalSessionDuration\": ");

			sb.append(siteVisitorBehaviorMetric.getTotalSessionDuration());
		}

		if (siteVisitorBehaviorMetric.getVisitors() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visitors\": ");

			sb.append(siteVisitorBehaviorMetric.getVisitors());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SiteVisitorBehaviorMetricJSONParser
			siteVisitorBehaviorMetricJSONParser =
				new SiteVisitorBehaviorMetricJSONParser();

		return siteVisitorBehaviorMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SiteVisitorBehaviorMetric siteVisitorBehaviorMetric) {

		if (siteVisitorBehaviorMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (siteVisitorBehaviorMetric.getAverageSessionDuration() == null) {
			map.put("averageSessionDuration", null);
		}
		else {
			map.put(
				"averageSessionDuration",
				String.valueOf(
					siteVisitorBehaviorMetric.getAverageSessionDuration()));
		}

		if (siteVisitorBehaviorMetric.getKnownVisitors() == null) {
			map.put("knownVisitors", null);
		}
		else {
			map.put(
				"knownVisitors",
				String.valueOf(siteVisitorBehaviorMetric.getKnownVisitors()));
		}

		if (siteVisitorBehaviorMetric.getTotalSessionDuration() == null) {
			map.put("totalSessionDuration", null);
		}
		else {
			map.put(
				"totalSessionDuration",
				String.valueOf(
					siteVisitorBehaviorMetric.getTotalSessionDuration()));
		}

		if (siteVisitorBehaviorMetric.getVisitors() == null) {
			map.put("visitors", null);
		}
		else {
			map.put(
				"visitors",
				String.valueOf(siteVisitorBehaviorMetric.getVisitors()));
		}

		return map;
	}

	public static class SiteVisitorBehaviorMetricJSONParser
		extends BaseJSONParser<SiteVisitorBehaviorMetric> {

		@Override
		protected SiteVisitorBehaviorMetric createDTO() {
			return new SiteVisitorBehaviorMetric();
		}

		@Override
		protected SiteVisitorBehaviorMetric[] createDTOArray(int size) {
			return new SiteVisitorBehaviorMetric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "averageSessionDuration")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "knownVisitors")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalSessionDuration")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "visitors")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SiteVisitorBehaviorMetric siteVisitorBehaviorMetric,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "averageSessionDuration")) {
				if (jsonParserFieldValue != null) {
					siteVisitorBehaviorMetric.setAverageSessionDuration(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "knownVisitors")) {
				if (jsonParserFieldValue != null) {
					siteVisitorBehaviorMetric.setKnownVisitors(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalSessionDuration")) {

				if (jsonParserFieldValue != null) {
					siteVisitorBehaviorMetric.setTotalSessionDuration(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "visitors")) {
				if (jsonParserFieldValue != null) {
					siteVisitorBehaviorMetric.setVisitors(
						Long.valueOf((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:18617334