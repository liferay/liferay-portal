/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.serdes.v1_0;

import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.DocumentMetric;
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
public class DocumentMetricSerDes {

	public static DocumentMetric toDTO(String json) {
		DocumentMetricJSONParser documentMetricJSONParser =
			new DocumentMetricJSONParser();

		return documentMetricJSONParser.parseToDTO(json);
	}

	public static DocumentMetric[] toDTOs(String json) {
		DocumentMetricJSONParser documentMetricJSONParser =
			new DocumentMetricJSONParser();

		return documentMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DocumentMetric documentMetric) {
		if (documentMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (documentMetric.getAssetId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetId\": ");

			sb.append("\"");

			sb.append(_escape(documentMetric.getAssetId()));

			sb.append("\"");
		}

		if (documentMetric.getAssetTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle\": ");

			sb.append("\"");

			sb.append(_escape(documentMetric.getAssetTitle()));

			sb.append("\"");
		}

		if (documentMetric.getCommentsMetric() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"commentsMetric\": ");

			sb.append(String.valueOf(documentMetric.getCommentsMetric()));
		}

		if (documentMetric.getDownloadsMetric() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"downloadsMetric\": ");

			sb.append(String.valueOf(documentMetric.getDownloadsMetric()));
		}

		if (documentMetric.getImpressionMadeMetric() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"impressionMadeMetric\": ");

			sb.append(String.valueOf(documentMetric.getImpressionMadeMetric()));
		}

		if (documentMetric.getLastViewedMetric() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastViewedMetric\": ");

			sb.append(String.valueOf(documentMetric.getLastViewedMetric()));
		}

		if (documentMetric.getRatingsMetric() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratingsMetric\": ");

			sb.append(String.valueOf(documentMetric.getRatingsMetric()));
		}

		if (documentMetric.getUrls() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"urls\": ");

			sb.append("[");

			for (int i = 0; i < documentMetric.getUrls().length; i++) {
				sb.append(_toJSON(documentMetric.getUrls()[i]));

				if ((i + 1) < documentMetric.getUrls().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (documentMetric.getUsersInvolvedMetric() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"usersInvolvedMetric\": ");

			sb.append(String.valueOf(documentMetric.getUsersInvolvedMetric()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DocumentMetricJSONParser documentMetricJSONParser =
			new DocumentMetricJSONParser();

		return documentMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DocumentMetric documentMetric) {
		if (documentMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (documentMetric.getAssetId() == null) {
			map.put("assetId", null);
		}
		else {
			map.put("assetId", String.valueOf(documentMetric.getAssetId()));
		}

		if (documentMetric.getAssetTitle() == null) {
			map.put("assetTitle", null);
		}
		else {
			map.put(
				"assetTitle", String.valueOf(documentMetric.getAssetTitle()));
		}

		if (documentMetric.getCommentsMetric() == null) {
			map.put("commentsMetric", null);
		}
		else {
			map.put(
				"commentsMetric",
				String.valueOf(documentMetric.getCommentsMetric()));
		}

		if (documentMetric.getDownloadsMetric() == null) {
			map.put("downloadsMetric", null);
		}
		else {
			map.put(
				"downloadsMetric",
				String.valueOf(documentMetric.getDownloadsMetric()));
		}

		if (documentMetric.getImpressionMadeMetric() == null) {
			map.put("impressionMadeMetric", null);
		}
		else {
			map.put(
				"impressionMadeMetric",
				String.valueOf(documentMetric.getImpressionMadeMetric()));
		}

		if (documentMetric.getLastViewedMetric() == null) {
			map.put("lastViewedMetric", null);
		}
		else {
			map.put(
				"lastViewedMetric",
				String.valueOf(documentMetric.getLastViewedMetric()));
		}

		if (documentMetric.getRatingsMetric() == null) {
			map.put("ratingsMetric", null);
		}
		else {
			map.put(
				"ratingsMetric",
				String.valueOf(documentMetric.getRatingsMetric()));
		}

		if (documentMetric.getUrls() == null) {
			map.put("urls", null);
		}
		else {
			map.put("urls", String.valueOf(documentMetric.getUrls()));
		}

		if (documentMetric.getUsersInvolvedMetric() == null) {
			map.put("usersInvolvedMetric", null);
		}
		else {
			map.put(
				"usersInvolvedMetric",
				String.valueOf(documentMetric.getUsersInvolvedMetric()));
		}

		return map;
	}

	public static class DocumentMetricJSONParser
		extends BaseJSONParser<DocumentMetric> {

		@Override
		protected DocumentMetric createDTO() {
			return new DocumentMetric();
		}

		@Override
		protected DocumentMetric[] createDTOArray(int size) {
			return new DocumentMetric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "assetId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "commentsMetric")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "downloadsMetric")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "impressionMadeMetric")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lastViewedMetric")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "ratingsMetric")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "urls")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "usersInvolvedMetric")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DocumentMetric documentMetric, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "assetId")) {
				if (jsonParserFieldValue != null) {
					documentMetric.setAssetId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				if (jsonParserFieldValue != null) {
					documentMetric.setAssetTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "commentsMetric")) {
				if (jsonParserFieldValue != null) {
					documentMetric.setCommentsMetric(
						MetricValueSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "downloadsMetric")) {
				if (jsonParserFieldValue != null) {
					documentMetric.setDownloadsMetric(
						MetricValueSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "impressionMadeMetric")) {

				if (jsonParserFieldValue != null) {
					documentMetric.setImpressionMadeMetric(
						MetricValueSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lastViewedMetric")) {
				if (jsonParserFieldValue != null) {
					documentMetric.setLastViewedMetric(
						MetricValueSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ratingsMetric")) {
				if (jsonParserFieldValue != null) {
					documentMetric.setRatingsMetric(
						MetricValueSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "urls")) {
				if (jsonParserFieldValue != null) {
					documentMetric.setUrls(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "usersInvolvedMetric")) {

				if (jsonParserFieldValue != null) {
					documentMetric.setUsersInvolvedMetric(
						MetricValueSerDes.toDTO((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:1804321695