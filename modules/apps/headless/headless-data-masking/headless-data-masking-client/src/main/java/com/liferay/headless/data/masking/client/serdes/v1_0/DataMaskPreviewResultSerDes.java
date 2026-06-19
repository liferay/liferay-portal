/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.client.serdes.v1_0;

import com.liferay.headless.data.masking.client.dto.v1_0.DataMaskPreviewResult;
import com.liferay.headless.data.masking.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Jose Luis Navarro
 * @generated
 */
@Generated("")
public class DataMaskPreviewResultSerDes {

	public static DataMaskPreviewResult toDTO(String json) {
		DataMaskPreviewResultJSONParser dataMaskPreviewResultJSONParser =
			new DataMaskPreviewResultJSONParser();

		return dataMaskPreviewResultJSONParser.parseToDTO(json);
	}

	public static DataMaskPreviewResult[] toDTOs(String json) {
		DataMaskPreviewResultJSONParser dataMaskPreviewResultJSONParser =
			new DataMaskPreviewResultJSONParser();

		return dataMaskPreviewResultJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DataMaskPreviewResult dataMaskPreviewResult) {
		if (dataMaskPreviewResult == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (dataMaskPreviewResult.getError() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"error\": ");

			sb.append("\"");

			sb.append(_escape(dataMaskPreviewResult.getError()));

			sb.append("\"");
		}

		if (dataMaskPreviewResult.getOutput() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"output\": ");

			sb.append("\"");

			sb.append(_escape(dataMaskPreviewResult.getOutput()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DataMaskPreviewResultJSONParser dataMaskPreviewResultJSONParser =
			new DataMaskPreviewResultJSONParser();

		return dataMaskPreviewResultJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DataMaskPreviewResult dataMaskPreviewResult) {

		if (dataMaskPreviewResult == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dataMaskPreviewResult.getError() == null) {
			map.put("error", null);
		}
		else {
			map.put("error", String.valueOf(dataMaskPreviewResult.getError()));
		}

		if (dataMaskPreviewResult.getOutput() == null) {
			map.put("output", null);
		}
		else {
			map.put(
				"output", String.valueOf(dataMaskPreviewResult.getOutput()));
		}

		return map;
	}

	public static class DataMaskPreviewResultJSONParser
		extends BaseJSONParser<DataMaskPreviewResult> {

		@Override
		protected DataMaskPreviewResult createDTO() {
			return new DataMaskPreviewResult();
		}

		@Override
		protected DataMaskPreviewResult[] createDTOArray(int size) {
			return new DataMaskPreviewResult[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "error")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "output")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DataMaskPreviewResult dataMaskPreviewResult,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "error")) {
				if (jsonParserFieldValue != null) {
					dataMaskPreviewResult.setError(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "output")) {
				if (jsonParserFieldValue != null) {
					dataMaskPreviewResult.setOutput(
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
// LIFERAY-REST-BUILDER-HASH:-460564855