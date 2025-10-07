/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.serdes.v1_0;

import com.liferay.analytics.cms.rest.client.dto.v1_0.TopPage;
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
public class TopPageSerDes {

	public static TopPage toDTO(String json) {
		TopPageJSONParser topPageJSONParser = new TopPageJSONParser();

		return topPageJSONParser.parseToDTO(json);
	}

	public static TopPage[] toDTOs(String json) {
		TopPageJSONParser topPageJSONParser = new TopPageJSONParser();

		return topPageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TopPage topPage) {
		if (topPage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (topPage.getCanonicalUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"canonicalUrl\": ");

			sb.append("\"");

			sb.append(_escape(topPage.getCanonicalUrl()));

			sb.append("\"");
		}

		if (topPage.getDefaultMetric() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultMetric\": ");

			sb.append(String.valueOf(topPage.getDefaultMetric()));
		}

		if (topPage.getPageTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageTitle\": ");

			sb.append("\"");

			sb.append(_escape(topPage.getPageTitle()));

			sb.append("\"");
		}

		if (topPage.getSiteName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteName\": ");

			sb.append("\"");

			sb.append(_escape(topPage.getSiteName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TopPageJSONParser topPageJSONParser = new TopPageJSONParser();

		return topPageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(TopPage topPage) {
		if (topPage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (topPage.getCanonicalUrl() == null) {
			map.put("canonicalUrl", null);
		}
		else {
			map.put("canonicalUrl", String.valueOf(topPage.getCanonicalUrl()));
		}

		if (topPage.getDefaultMetric() == null) {
			map.put("defaultMetric", null);
		}
		else {
			map.put(
				"defaultMetric", String.valueOf(topPage.getDefaultMetric()));
		}

		if (topPage.getPageTitle() == null) {
			map.put("pageTitle", null);
		}
		else {
			map.put("pageTitle", String.valueOf(topPage.getPageTitle()));
		}

		if (topPage.getSiteName() == null) {
			map.put("siteName", null);
		}
		else {
			map.put("siteName", String.valueOf(topPage.getSiteName()));
		}

		return map;
	}

	public static class TopPageJSONParser extends BaseJSONParser<TopPage> {

		@Override
		protected TopPage createDTO() {
			return new TopPage();
		}

		@Override
		protected TopPage[] createDTOArray(int size) {
			return new TopPage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "canonicalUrl")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultMetric")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageTitle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteName")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TopPage topPage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "canonicalUrl")) {
				if (jsonParserFieldValue != null) {
					topPage.setCanonicalUrl((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultMetric")) {
				if (jsonParserFieldValue != null) {
					topPage.setDefaultMetric(
						MetricSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageTitle")) {
				if (jsonParserFieldValue != null) {
					topPage.setPageTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteName")) {
				if (jsonParserFieldValue != null) {
					topPage.setSiteName((String)jsonParserFieldValue);
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