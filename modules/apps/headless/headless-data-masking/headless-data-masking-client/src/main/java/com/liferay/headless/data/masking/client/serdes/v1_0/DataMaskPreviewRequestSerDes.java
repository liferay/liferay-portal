/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.client.serdes.v1_0;

import com.liferay.headless.data.masking.client.dto.v1_0.DataMaskPreviewRequest;
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
public class DataMaskPreviewRequestSerDes {

	public static DataMaskPreviewRequest toDTO(String json) {
		DataMaskPreviewRequestJSONParser dataMaskPreviewRequestJSONParser =
			new DataMaskPreviewRequestJSONParser();

		return dataMaskPreviewRequestJSONParser.parseToDTO(json);
	}

	public static DataMaskPreviewRequest[] toDTOs(String json) {
		DataMaskPreviewRequestJSONParser dataMaskPreviewRequestJSONParser =
			new DataMaskPreviewRequestJSONParser();

		return dataMaskPreviewRequestJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DataMaskPreviewRequest dataMaskPreviewRequest) {
		if (dataMaskPreviewRequest == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (dataMaskPreviewRequest.getDetectionRegex() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"detectionRegex\": ");

			sb.append("\"");

			sb.append(_escape(dataMaskPreviewRequest.getDetectionRegex()));

			sb.append("\"");
		}

		if (dataMaskPreviewRequest.getReplacementRegex() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacementRegex\": ");

			sb.append("\"");

			sb.append(_escape(dataMaskPreviewRequest.getReplacementRegex()));

			sb.append("\"");
		}

		if (dataMaskPreviewRequest.getReplacementValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacementValue\": ");

			sb.append("\"");

			sb.append(_escape(dataMaskPreviewRequest.getReplacementValue()));

			sb.append("\"");
		}

		if (dataMaskPreviewRequest.getSampleText() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sampleText\": ");

			sb.append("\"");

			sb.append(_escape(dataMaskPreviewRequest.getSampleText()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DataMaskPreviewRequestJSONParser dataMaskPreviewRequestJSONParser =
			new DataMaskPreviewRequestJSONParser();

		return dataMaskPreviewRequestJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DataMaskPreviewRequest dataMaskPreviewRequest) {

		if (dataMaskPreviewRequest == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dataMaskPreviewRequest.getDetectionRegex() == null) {
			map.put("detectionRegex", null);
		}
		else {
			map.put(
				"detectionRegex",
				String.valueOf(dataMaskPreviewRequest.getDetectionRegex()));
		}

		if (dataMaskPreviewRequest.getReplacementRegex() == null) {
			map.put("replacementRegex", null);
		}
		else {
			map.put(
				"replacementRegex",
				String.valueOf(dataMaskPreviewRequest.getReplacementRegex()));
		}

		if (dataMaskPreviewRequest.getReplacementValue() == null) {
			map.put("replacementValue", null);
		}
		else {
			map.put(
				"replacementValue",
				String.valueOf(dataMaskPreviewRequest.getReplacementValue()));
		}

		if (dataMaskPreviewRequest.getSampleText() == null) {
			map.put("sampleText", null);
		}
		else {
			map.put(
				"sampleText",
				String.valueOf(dataMaskPreviewRequest.getSampleText()));
		}

		return map;
	}

	public static class DataMaskPreviewRequestJSONParser
		extends BaseJSONParser<DataMaskPreviewRequest> {

		@Override
		protected DataMaskPreviewRequest createDTO() {
			return new DataMaskPreviewRequest();
		}

		@Override
		protected DataMaskPreviewRequest[] createDTOArray(int size) {
			return new DataMaskPreviewRequest[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "detectionRegex")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "replacementRegex")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "replacementValue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sampleText")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DataMaskPreviewRequest dataMaskPreviewRequest,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "detectionRegex")) {
				if (jsonParserFieldValue != null) {
					dataMaskPreviewRequest.setDetectionRegex(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "replacementRegex")) {
				if (jsonParserFieldValue != null) {
					dataMaskPreviewRequest.setReplacementRegex(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "replacementValue")) {
				if (jsonParserFieldValue != null) {
					dataMaskPreviewRequest.setReplacementValue(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sampleText")) {
				if (jsonParserFieldValue != null) {
					dataMaskPreviewRequest.setSampleText(
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
// LIFERAY-REST-BUILDER-HASH:-81479315