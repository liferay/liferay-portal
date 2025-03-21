/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.client.serdes.v1_0;

import com.liferay.analytics.reports.rest.client.dto.v1_0.AssetAppearsOnHistogram;
import com.liferay.analytics.reports.rest.client.dto.v1_0.AssetAppearsOnHistogramMetric;
import com.liferay.analytics.reports.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
public class AssetAppearsOnHistogramMetricSerDes {

	public static AssetAppearsOnHistogramMetric toDTO(String json) {
		AssetAppearsOnHistogramMetricJSONParser
			assetAppearsOnHistogramMetricJSONParser =
				new AssetAppearsOnHistogramMetricJSONParser();

		return assetAppearsOnHistogramMetricJSONParser.parseToDTO(json);
	}

	public static AssetAppearsOnHistogramMetric[] toDTOs(String json) {
		AssetAppearsOnHistogramMetricJSONParser
			assetAppearsOnHistogramMetricJSONParser =
				new AssetAppearsOnHistogramMetricJSONParser();

		return assetAppearsOnHistogramMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		AssetAppearsOnHistogramMetric assetAppearsOnHistogramMetric) {

		if (assetAppearsOnHistogramMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (assetAppearsOnHistogramMetric.getAssetAppearsOnHistograms() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetAppearsOnHistograms\": ");

			sb.append("[");

			for (int i = 0;
				 i < assetAppearsOnHistogramMetric.
					 getAssetAppearsOnHistograms().length;
				 i++) {

				sb.append(
					String.valueOf(
						assetAppearsOnHistogramMetric.
							getAssetAppearsOnHistograms()[i]));

				if ((i + 1) < assetAppearsOnHistogramMetric.
						getAssetAppearsOnHistograms().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssetAppearsOnHistogramMetricJSONParser
			assetAppearsOnHistogramMetricJSONParser =
				new AssetAppearsOnHistogramMetricJSONParser();

		return assetAppearsOnHistogramMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AssetAppearsOnHistogramMetric assetAppearsOnHistogramMetric) {

		if (assetAppearsOnHistogramMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (assetAppearsOnHistogramMetric.getAssetAppearsOnHistograms() ==
				null) {

			map.put("assetAppearsOnHistograms", null);
		}
		else {
			map.put(
				"assetAppearsOnHistograms",
				String.valueOf(
					assetAppearsOnHistogramMetric.
						getAssetAppearsOnHistograms()));
		}

		return map;
	}

	public static class AssetAppearsOnHistogramMetricJSONParser
		extends BaseJSONParser<AssetAppearsOnHistogramMetric> {

		@Override
		protected AssetAppearsOnHistogramMetric createDTO() {
			return new AssetAppearsOnHistogramMetric();
		}

		@Override
		protected AssetAppearsOnHistogramMetric[] createDTOArray(int size) {
			return new AssetAppearsOnHistogramMetric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "assetAppearsOnHistograms")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AssetAppearsOnHistogramMetric assetAppearsOnHistogramMetric,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "assetAppearsOnHistograms")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					AssetAppearsOnHistogram[] assetAppearsOnHistogramsArray =
						new AssetAppearsOnHistogram
							[jsonParserFieldValues.length];

					for (int i = 0; i < assetAppearsOnHistogramsArray.length;
						 i++) {

						assetAppearsOnHistogramsArray[i] =
							AssetAppearsOnHistogramSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					assetAppearsOnHistogramMetric.setAssetAppearsOnHistograms(
						assetAppearsOnHistogramsArray);
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