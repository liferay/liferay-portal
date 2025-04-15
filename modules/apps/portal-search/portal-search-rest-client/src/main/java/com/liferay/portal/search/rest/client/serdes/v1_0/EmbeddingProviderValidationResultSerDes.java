/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.client.serdes.v1_0;

import com.liferay.portal.search.rest.client.dto.v1_0.EmbeddingProviderValidationResult;
import com.liferay.portal.search.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class EmbeddingProviderValidationResultSerDes {

	public static EmbeddingProviderValidationResult toDTO(String json) {
		EmbeddingProviderValidationResultJSONParser
			embeddingProviderValidationResultJSONParser =
				new EmbeddingProviderValidationResultJSONParser();

		return embeddingProviderValidationResultJSONParser.parseToDTO(json);
	}

	public static EmbeddingProviderValidationResult[] toDTOs(String json) {
		EmbeddingProviderValidationResultJSONParser
			embeddingProviderValidationResultJSONParser =
				new EmbeddingProviderValidationResultJSONParser();

		return embeddingProviderValidationResultJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		EmbeddingProviderValidationResult embeddingProviderValidationResult) {

		if (embeddingProviderValidationResult == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (embeddingProviderValidationResult.getErrorMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessage\": ");

			sb.append("\"");

			sb.append(
				_escape(embeddingProviderValidationResult.getErrorMessage()));

			sb.append("\"");
		}

		if (embeddingProviderValidationResult.getExpectedDimensions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expectedDimensions\": ");

			sb.append(
				embeddingProviderValidationResult.getExpectedDimensions());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		EmbeddingProviderValidationResultJSONParser
			embeddingProviderValidationResultJSONParser =
				new EmbeddingProviderValidationResultJSONParser();

		return embeddingProviderValidationResultJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		EmbeddingProviderValidationResult embeddingProviderValidationResult) {

		if (embeddingProviderValidationResult == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (embeddingProviderValidationResult.getErrorMessage() == null) {
			map.put("errorMessage", null);
		}
		else {
			map.put(
				"errorMessage",
				String.valueOf(
					embeddingProviderValidationResult.getErrorMessage()));
		}

		if (embeddingProviderValidationResult.getExpectedDimensions() == null) {
			map.put("expectedDimensions", null);
		}
		else {
			map.put(
				"expectedDimensions",
				String.valueOf(
					embeddingProviderValidationResult.getExpectedDimensions()));
		}

		return map;
	}

	public static class EmbeddingProviderValidationResultJSONParser
		extends BaseJSONParser<EmbeddingProviderValidationResult> {

		@Override
		protected EmbeddingProviderValidationResult createDTO() {
			return new EmbeddingProviderValidationResult();
		}

		@Override
		protected EmbeddingProviderValidationResult[] createDTOArray(int size) {
			return new EmbeddingProviderValidationResult[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "errorMessage")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "expectedDimensions")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			EmbeddingProviderValidationResult embeddingProviderValidationResult,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "errorMessage")) {
				if (jsonParserFieldValue != null) {
					embeddingProviderValidationResult.setErrorMessage(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "expectedDimensions")) {

				if (jsonParserFieldValue != null) {
					embeddingProviderValidationResult.setExpectedDimensions(
						Integer.valueOf((String)jsonParserFieldValue));
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