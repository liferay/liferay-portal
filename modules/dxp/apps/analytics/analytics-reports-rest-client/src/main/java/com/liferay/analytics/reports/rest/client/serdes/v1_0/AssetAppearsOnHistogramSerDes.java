/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.client.serdes.v1_0;

import com.liferay.analytics.reports.rest.client.dto.v1_0.AppearsOnHistogram;
import com.liferay.analytics.reports.rest.client.dto.v1_0.AssetAppearsOnHistogram;
import com.liferay.analytics.reports.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
public class AssetAppearsOnHistogramSerDes {

	public static AssetAppearsOnHistogram toDTO(String json) {
		AssetAppearsOnHistogramJSONParser assetAppearsOnHistogramJSONParser =
			new AssetAppearsOnHistogramJSONParser();

		return assetAppearsOnHistogramJSONParser.parseToDTO(json);
	}

	public static AssetAppearsOnHistogram[] toDTOs(String json) {
		AssetAppearsOnHistogramJSONParser assetAppearsOnHistogramJSONParser =
			new AssetAppearsOnHistogramJSONParser();

		return assetAppearsOnHistogramJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		AssetAppearsOnHistogram assetAppearsOnHistogram) {

		if (assetAppearsOnHistogram == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (assetAppearsOnHistogram.getAppearsOnHistograms() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"appearsOnHistograms\": ");

			sb.append("[");

			for (int i = 0;
				 i < assetAppearsOnHistogram.getAppearsOnHistograms().length;
				 i++) {

				sb.append(
					String.valueOf(
						assetAppearsOnHistogram.getAppearsOnHistograms()[i]));

				if ((i + 1) <
						assetAppearsOnHistogram.
							getAppearsOnHistograms().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (assetAppearsOnHistogram.getMetricName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metricName\": ");

			sb.append("\"");

			sb.append(_escape(assetAppearsOnHistogram.getMetricName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssetAppearsOnHistogramJSONParser assetAppearsOnHistogramJSONParser =
			new AssetAppearsOnHistogramJSONParser();

		return assetAppearsOnHistogramJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AssetAppearsOnHistogram assetAppearsOnHistogram) {

		if (assetAppearsOnHistogram == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (assetAppearsOnHistogram.getAppearsOnHistograms() == null) {
			map.put("appearsOnHistograms", null);
		}
		else {
			map.put(
				"appearsOnHistograms",
				String.valueOf(
					assetAppearsOnHistogram.getAppearsOnHistograms()));
		}

		if (assetAppearsOnHistogram.getMetricName() == null) {
			map.put("metricName", null);
		}
		else {
			map.put(
				"metricName",
				String.valueOf(assetAppearsOnHistogram.getMetricName()));
		}

		return map;
	}

	public static class AssetAppearsOnHistogramJSONParser
		extends BaseJSONParser<AssetAppearsOnHistogram> {

		@Override
		protected AssetAppearsOnHistogram createDTO() {
			return new AssetAppearsOnHistogram();
		}

		@Override
		protected AssetAppearsOnHistogram[] createDTOArray(int size) {
			return new AssetAppearsOnHistogram[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "appearsOnHistograms")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "metricName")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AssetAppearsOnHistogram assetAppearsOnHistogram,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "appearsOnHistograms")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					AppearsOnHistogram[] appearsOnHistogramsArray =
						new AppearsOnHistogram[jsonParserFieldValues.length];

					for (int i = 0; i < appearsOnHistogramsArray.length; i++) {
						appearsOnHistogramsArray[i] =
							AppearsOnHistogramSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					assetAppearsOnHistogram.setAppearsOnHistograms(
						appearsOnHistogramsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "metricName")) {
				if (jsonParserFieldValue != null) {
					assetAppearsOnHistogram.setMetricName(
						(String)jsonParserFieldValue);
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