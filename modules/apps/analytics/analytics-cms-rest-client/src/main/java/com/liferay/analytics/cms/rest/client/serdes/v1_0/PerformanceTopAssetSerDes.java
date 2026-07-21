/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.serdes.v1_0;

import com.liferay.analytics.cms.rest.client.dto.v1_0.PerformanceTopAsset;
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
public class PerformanceTopAssetSerDes {

	public static PerformanceTopAsset toDTO(String json) {
		PerformanceTopAssetJSONParser performanceTopAssetJSONParser =
			new PerformanceTopAssetJSONParser();

		return performanceTopAssetJSONParser.parseToDTO(json);
	}

	public static PerformanceTopAsset[] toDTOs(String json) {
		PerformanceTopAssetJSONParser performanceTopAssetJSONParser =
			new PerformanceTopAssetJSONParser();

		return performanceTopAssetJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PerformanceTopAsset performanceTopAsset) {
		if (performanceTopAsset == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (performanceTopAsset.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(performanceTopAsset.getClassName()));

			sb.append("\"");
		}

		if (performanceTopAsset.getDownloads() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"downloads\": ");

			sb.append(performanceTopAsset.getDownloads());
		}

		if (performanceTopAsset.getEmbedded() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"embedded\": ");

			if (performanceTopAsset.getEmbedded() instanceof String) {
				sb.append("\"");
				sb.append((String)performanceTopAsset.getEmbedded());
				sb.append("\"");
			}
			else {
				sb.append(performanceTopAsset.getEmbedded());
			}
		}

		if (performanceTopAsset.getEngagement() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"engagement\": ");

			sb.append(performanceTopAsset.getEngagement());
		}

		if (performanceTopAsset.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(performanceTopAsset.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (performanceTopAsset.getImpressions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"impressions\": ");

			sb.append(performanceTopAsset.getImpressions());
		}

		if (performanceTopAsset.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(performanceTopAsset.getTitle()));

			sb.append("\"");
		}

		if (performanceTopAsset.getTrend() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trend\": ");

			sb.append(String.valueOf(performanceTopAsset.getTrend()));
		}

		if (performanceTopAsset.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(performanceTopAsset.getType()));

			sb.append("\"");
		}

		if (performanceTopAsset.getViews() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"views\": ");

			sb.append(performanceTopAsset.getViews());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PerformanceTopAssetJSONParser performanceTopAssetJSONParser =
			new PerformanceTopAssetJSONParser();

		return performanceTopAssetJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PerformanceTopAsset performanceTopAsset) {

		if (performanceTopAsset == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (performanceTopAsset.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put(
				"className",
				String.valueOf(performanceTopAsset.getClassName()));
		}

		if (performanceTopAsset.getDownloads() == null) {
			map.put("downloads", null);
		}
		else {
			map.put(
				"downloads",
				String.valueOf(performanceTopAsset.getDownloads()));
		}

		if (performanceTopAsset.getEmbedded() == null) {
			map.put("embedded", null);
		}
		else {
			map.put(
				"embedded", String.valueOf(performanceTopAsset.getEmbedded()));
		}

		if (performanceTopAsset.getEngagement() == null) {
			map.put("engagement", null);
		}
		else {
			map.put(
				"engagement",
				String.valueOf(performanceTopAsset.getEngagement()));
		}

		if (performanceTopAsset.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(performanceTopAsset.getExternalReferenceCode()));
		}

		if (performanceTopAsset.getImpressions() == null) {
			map.put("impressions", null);
		}
		else {
			map.put(
				"impressions",
				String.valueOf(performanceTopAsset.getImpressions()));
		}

		if (performanceTopAsset.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(performanceTopAsset.getTitle()));
		}

		if (performanceTopAsset.getTrend() == null) {
			map.put("trend", null);
		}
		else {
			map.put("trend", String.valueOf(performanceTopAsset.getTrend()));
		}

		if (performanceTopAsset.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(performanceTopAsset.getType()));
		}

		if (performanceTopAsset.getViews() == null) {
			map.put("views", null);
		}
		else {
			map.put("views", String.valueOf(performanceTopAsset.getViews()));
		}

		return map;
	}

	public static class PerformanceTopAssetJSONParser
		extends BaseJSONParser<PerformanceTopAsset> {

		@Override
		protected PerformanceTopAsset createDTO() {
			return new PerformanceTopAsset();
		}

		@Override
		protected PerformanceTopAsset[] createDTOArray(int size) {
			return new PerformanceTopAsset[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "downloads")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "embedded")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "engagement")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "impressions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "trend")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "views")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PerformanceTopAsset performanceTopAsset, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					performanceTopAsset.setClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "downloads")) {
				if (jsonParserFieldValue != null) {
					performanceTopAsset.setDownloads(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "embedded")) {
				if (jsonParserFieldValue != null) {
					performanceTopAsset.setEmbedded(
						(Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "engagement")) {
				if (jsonParserFieldValue != null) {
					performanceTopAsset.setEngagement(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					performanceTopAsset.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "impressions")) {
				if (jsonParserFieldValue != null) {
					performanceTopAsset.setImpressions(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					performanceTopAsset.setTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "trend")) {
				if (jsonParserFieldValue != null) {
					performanceTopAsset.setTrend(
						TrendSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					performanceTopAsset.setType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "views")) {
				if (jsonParserFieldValue != null) {
					performanceTopAsset.setViews(
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
// LIFERAY-REST-BUILDER-HASH:289929672