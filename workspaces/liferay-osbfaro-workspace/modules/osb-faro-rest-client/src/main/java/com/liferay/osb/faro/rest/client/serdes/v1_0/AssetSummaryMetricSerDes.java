/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.AssetSummaryMetric;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class AssetSummaryMetricSerDes {

	public static AssetSummaryMetric toDTO(String json) {
		AssetSummaryMetricJSONParser assetSummaryMetricJSONParser =
			new AssetSummaryMetricJSONParser();

		return assetSummaryMetricJSONParser.parseToDTO(json);
	}

	public static AssetSummaryMetric[] toDTOs(String json) {
		AssetSummaryMetricJSONParser assetSummaryMetricJSONParser =
			new AssetSummaryMetricJSONParser();

		return assetSummaryMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AssetSummaryMetric assetSummaryMetric) {
		if (assetSummaryMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (assetSummaryMetric.getAssetId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetId\": ");

			sb.append("\"");

			sb.append(_escape(assetSummaryMetric.getAssetId()));

			sb.append("\"");
		}

		if (assetSummaryMetric.getAssetTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle\": ");

			sb.append("\"");

			sb.append(_escape(assetSummaryMetric.getAssetTitle()));

			sb.append("\"");
		}

		if (assetSummaryMetric.getAssetType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetType\": ");

			sb.append("\"");

			sb.append(_escape(assetSummaryMetric.getAssetType()));

			sb.append("\"");
		}

		if (assetSummaryMetric.getDownloads() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"downloads\": ");

			sb.append(assetSummaryMetric.getDownloads());
		}

		if (assetSummaryMetric.getDownloadsTrendPercentage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"downloadsTrendPercentage\": ");

			sb.append(assetSummaryMetric.getDownloadsTrendPercentage());
		}

		if (assetSummaryMetric.getImpressions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"impressions\": ");

			sb.append(assetSummaryMetric.getImpressions());
		}

		if (assetSummaryMetric.getImpressionsTrendPercentage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"impressionsTrendPercentage\": ");

			sb.append(assetSummaryMetric.getImpressionsTrendPercentage());
		}

		if (assetSummaryMetric.getReads() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reads\": ");

			sb.append(assetSummaryMetric.getReads());
		}

		if (assetSummaryMetric.getReadsTrendPercentage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readsTrendPercentage\": ");

			sb.append(assetSummaryMetric.getReadsTrendPercentage());
		}

		if (assetSummaryMetric.getViews() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"views\": ");

			sb.append(assetSummaryMetric.getViews());
		}

		if (assetSummaryMetric.getViewsTrendPercentage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewsTrendPercentage\": ");

			sb.append(assetSummaryMetric.getViewsTrendPercentage());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssetSummaryMetricJSONParser assetSummaryMetricJSONParser =
			new AssetSummaryMetricJSONParser();

		return assetSummaryMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AssetSummaryMetric assetSummaryMetric) {

		if (assetSummaryMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (assetSummaryMetric.getAssetId() == null) {
			map.put("assetId", null);
		}
		else {
			map.put("assetId", String.valueOf(assetSummaryMetric.getAssetId()));
		}

		if (assetSummaryMetric.getAssetTitle() == null) {
			map.put("assetTitle", null);
		}
		else {
			map.put(
				"assetTitle",
				String.valueOf(assetSummaryMetric.getAssetTitle()));
		}

		if (assetSummaryMetric.getAssetType() == null) {
			map.put("assetType", null);
		}
		else {
			map.put(
				"assetType", String.valueOf(assetSummaryMetric.getAssetType()));
		}

		if (assetSummaryMetric.getDownloads() == null) {
			map.put("downloads", null);
		}
		else {
			map.put(
				"downloads", String.valueOf(assetSummaryMetric.getDownloads()));
		}

		if (assetSummaryMetric.getDownloadsTrendPercentage() == null) {
			map.put("downloadsTrendPercentage", null);
		}
		else {
			map.put(
				"downloadsTrendPercentage",
				String.valueOf(
					assetSummaryMetric.getDownloadsTrendPercentage()));
		}

		if (assetSummaryMetric.getImpressions() == null) {
			map.put("impressions", null);
		}
		else {
			map.put(
				"impressions",
				String.valueOf(assetSummaryMetric.getImpressions()));
		}

		if (assetSummaryMetric.getImpressionsTrendPercentage() == null) {
			map.put("impressionsTrendPercentage", null);
		}
		else {
			map.put(
				"impressionsTrendPercentage",
				String.valueOf(
					assetSummaryMetric.getImpressionsTrendPercentage()));
		}

		if (assetSummaryMetric.getReads() == null) {
			map.put("reads", null);
		}
		else {
			map.put("reads", String.valueOf(assetSummaryMetric.getReads()));
		}

		if (assetSummaryMetric.getReadsTrendPercentage() == null) {
			map.put("readsTrendPercentage", null);
		}
		else {
			map.put(
				"readsTrendPercentage",
				String.valueOf(assetSummaryMetric.getReadsTrendPercentage()));
		}

		if (assetSummaryMetric.getViews() == null) {
			map.put("views", null);
		}
		else {
			map.put("views", String.valueOf(assetSummaryMetric.getViews()));
		}

		if (assetSummaryMetric.getViewsTrendPercentage() == null) {
			map.put("viewsTrendPercentage", null);
		}
		else {
			map.put(
				"viewsTrendPercentage",
				String.valueOf(assetSummaryMetric.getViewsTrendPercentage()));
		}

		return map;
	}

	public static class AssetSummaryMetricJSONParser
		extends BaseJSONParser<AssetSummaryMetric> {

		@Override
		protected AssetSummaryMetric createDTO() {
			return new AssetSummaryMetric();
		}

		@Override
		protected AssetSummaryMetric[] createDTOArray(int size) {
			return new AssetSummaryMetric[size];
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
			else if (Objects.equals(jsonParserFieldName, "downloads")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "downloadsTrendPercentage")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "impressions")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "impressionsTrendPercentage")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "reads")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "readsTrendPercentage")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "views")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "viewsTrendPercentage")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AssetSummaryMetric assetSummaryMetric, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "assetId")) {
				if (jsonParserFieldValue != null) {
					assetSummaryMetric.setAssetId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				if (jsonParserFieldValue != null) {
					assetSummaryMetric.setAssetTitle(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetType")) {
				if (jsonParserFieldValue != null) {
					assetSummaryMetric.setAssetType(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "downloads")) {
				if (jsonParserFieldValue != null) {
					assetSummaryMetric.setDownloads(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "downloadsTrendPercentage")) {

				if (jsonParserFieldValue != null) {
					assetSummaryMetric.setDownloadsTrendPercentage(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "impressions")) {
				if (jsonParserFieldValue != null) {
					assetSummaryMetric.setImpressions(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "impressionsTrendPercentage")) {

				if (jsonParserFieldValue != null) {
					assetSummaryMetric.setImpressionsTrendPercentage(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "reads")) {
				if (jsonParserFieldValue != null) {
					assetSummaryMetric.setReads(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "readsTrendPercentage")) {

				if (jsonParserFieldValue != null) {
					assetSummaryMetric.setReadsTrendPercentage(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "views")) {
				if (jsonParserFieldValue != null) {
					assetSummaryMetric.setViews(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "viewsTrendPercentage")) {

				if (jsonParserFieldValue != null) {
					assetSummaryMetric.setViewsTrendPercentage(
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
// LIFERAY-REST-BUILDER-HASH:-853501188