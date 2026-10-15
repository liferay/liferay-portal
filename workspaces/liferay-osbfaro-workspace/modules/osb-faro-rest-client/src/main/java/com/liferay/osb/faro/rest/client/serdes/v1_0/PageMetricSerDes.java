/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.PageMetric;
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
public class PageMetricSerDes {

	public static PageMetric toDTO(String json) {
		PageMetricJSONParser pageMetricJSONParser = new PageMetricJSONParser();

		return pageMetricJSONParser.parseToDTO(json);
	}

	public static PageMetric[] toDTOs(String json) {
		PageMetricJSONParser pageMetricJSONParser = new PageMetricJSONParser();

		return pageMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PageMetric pageMetric) {
		if (pageMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageMetric.getAssetId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetId\": ");

			sb.append("\"");

			sb.append(_escape(pageMetric.getAssetId()));

			sb.append("\"");
		}

		if (pageMetric.getAssetTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle\": ");

			sb.append("\"");

			sb.append(_escape(pageMetric.getAssetTitle()));

			sb.append("\"");
		}

		if (pageMetric.getAvgTimeOnPage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"avgTimeOnPage\": ");

			sb.append(pageMetric.getAvgTimeOnPage());
		}

		if (pageMetric.getBounceRate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bounceRate\": ");

			sb.append(pageMetric.getBounceRate());
		}

		if (pageMetric.getDataSourceId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataSourceId\": ");

			sb.append("\"");

			sb.append(_escape(pageMetric.getDataSourceId()));

			sb.append("\"");
		}

		if (pageMetric.getDirectAccess() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"directAccess\": ");

			sb.append(pageMetric.getDirectAccess());
		}

		if (pageMetric.getEntrances() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entrances\": ");

			sb.append(pageMetric.getEntrances());
		}

		if (pageMetric.getExitRate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"exitRate\": ");

			sb.append(pageMetric.getExitRate());
		}

		if (pageMetric.getIndirectAccess() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indirectAccess\": ");

			sb.append(pageMetric.getIndirectAccess());
		}

		if (pageMetric.getUrls() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"urls\": ");

			sb.append("[");

			for (int i = 0; i < pageMetric.getUrls().length; i++) {
				sb.append(_toJSON(pageMetric.getUrls()[i]));

				if ((i + 1) < pageMetric.getUrls().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageMetric.getViews() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"views\": ");

			sb.append(pageMetric.getViews());
		}

		if (pageMetric.getViewsTrendPercentage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewsTrendPercentage\": ");

			sb.append(pageMetric.getViewsTrendPercentage());
		}

		if (pageMetric.getVisitors() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visitors\": ");

			sb.append(pageMetric.getVisitors());
		}

		if (pageMetric.getVisitorsTrendPercentage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visitorsTrendPercentage\": ");

			sb.append(pageMetric.getVisitorsTrendPercentage());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageMetricJSONParser pageMetricJSONParser = new PageMetricJSONParser();

		return pageMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PageMetric pageMetric) {
		if (pageMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageMetric.getAssetId() == null) {
			map.put("assetId", null);
		}
		else {
			map.put("assetId", String.valueOf(pageMetric.getAssetId()));
		}

		if (pageMetric.getAssetTitle() == null) {
			map.put("assetTitle", null);
		}
		else {
			map.put("assetTitle", String.valueOf(pageMetric.getAssetTitle()));
		}

		if (pageMetric.getAvgTimeOnPage() == null) {
			map.put("avgTimeOnPage", null);
		}
		else {
			map.put(
				"avgTimeOnPage", String.valueOf(pageMetric.getAvgTimeOnPage()));
		}

		if (pageMetric.getBounceRate() == null) {
			map.put("bounceRate", null);
		}
		else {
			map.put("bounceRate", String.valueOf(pageMetric.getBounceRate()));
		}

		if (pageMetric.getDataSourceId() == null) {
			map.put("dataSourceId", null);
		}
		else {
			map.put(
				"dataSourceId", String.valueOf(pageMetric.getDataSourceId()));
		}

		if (pageMetric.getDirectAccess() == null) {
			map.put("directAccess", null);
		}
		else {
			map.put(
				"directAccess", String.valueOf(pageMetric.getDirectAccess()));
		}

		if (pageMetric.getEntrances() == null) {
			map.put("entrances", null);
		}
		else {
			map.put("entrances", String.valueOf(pageMetric.getEntrances()));
		}

		if (pageMetric.getExitRate() == null) {
			map.put("exitRate", null);
		}
		else {
			map.put("exitRate", String.valueOf(pageMetric.getExitRate()));
		}

		if (pageMetric.getIndirectAccess() == null) {
			map.put("indirectAccess", null);
		}
		else {
			map.put(
				"indirectAccess",
				String.valueOf(pageMetric.getIndirectAccess()));
		}

		if (pageMetric.getUrls() == null) {
			map.put("urls", null);
		}
		else {
			map.put("urls", String.valueOf(pageMetric.getUrls()));
		}

		if (pageMetric.getViews() == null) {
			map.put("views", null);
		}
		else {
			map.put("views", String.valueOf(pageMetric.getViews()));
		}

		if (pageMetric.getViewsTrendPercentage() == null) {
			map.put("viewsTrendPercentage", null);
		}
		else {
			map.put(
				"viewsTrendPercentage",
				String.valueOf(pageMetric.getViewsTrendPercentage()));
		}

		if (pageMetric.getVisitors() == null) {
			map.put("visitors", null);
		}
		else {
			map.put("visitors", String.valueOf(pageMetric.getVisitors()));
		}

		if (pageMetric.getVisitorsTrendPercentage() == null) {
			map.put("visitorsTrendPercentage", null);
		}
		else {
			map.put(
				"visitorsTrendPercentage",
				String.valueOf(pageMetric.getVisitorsTrendPercentage()));
		}

		return map;
	}

	public static class PageMetricJSONParser
		extends BaseJSONParser<PageMetric> {

		@Override
		protected PageMetric createDTO() {
			return new PageMetric();
		}

		@Override
		protected PageMetric[] createDTOArray(int size) {
			return new PageMetric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "assetId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "avgTimeOnPage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "bounceRate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dataSourceId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "directAccess")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "entrances")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "exitRate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "indirectAccess")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "urls")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "views")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "viewsTrendPercentage")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "visitors")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "visitorsTrendPercentage")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageMetric pageMetric, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "assetId")) {
				if (jsonParserFieldValue != null) {
					pageMetric.setAssetId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				if (jsonParserFieldValue != null) {
					pageMetric.setAssetTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "avgTimeOnPage")) {
				if (jsonParserFieldValue != null) {
					pageMetric.setAvgTimeOnPage(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "bounceRate")) {
				if (jsonParserFieldValue != null) {
					pageMetric.setBounceRate(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataSourceId")) {
				if (jsonParserFieldValue != null) {
					pageMetric.setDataSourceId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "directAccess")) {
				if (jsonParserFieldValue != null) {
					pageMetric.setDirectAccess(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "entrances")) {
				if (jsonParserFieldValue != null) {
					pageMetric.setEntrances(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "exitRate")) {
				if (jsonParserFieldValue != null) {
					pageMetric.setExitRate(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indirectAccess")) {
				if (jsonParserFieldValue != null) {
					pageMetric.setIndirectAccess(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "urls")) {
				if (jsonParserFieldValue != null) {
					pageMetric.setUrls(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "views")) {
				if (jsonParserFieldValue != null) {
					pageMetric.setViews(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "viewsTrendPercentage")) {

				if (jsonParserFieldValue != null) {
					pageMetric.setViewsTrendPercentage(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "visitors")) {
				if (jsonParserFieldValue != null) {
					pageMetric.setVisitors(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "visitorsTrendPercentage")) {

				if (jsonParserFieldValue != null) {
					pageMetric.setVisitorsTrendPercentage(
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
// LIFERAY-REST-BUILDER-HASH:368364050