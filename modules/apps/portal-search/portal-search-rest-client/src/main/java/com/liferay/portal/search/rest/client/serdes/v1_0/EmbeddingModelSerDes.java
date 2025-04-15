/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.client.serdes.v1_0;

import com.liferay.portal.search.rest.client.dto.v1_0.EmbeddingModel;
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
public class EmbeddingModelSerDes {

	public static EmbeddingModel toDTO(String json) {
		EmbeddingModelJSONParser embeddingModelJSONParser =
			new EmbeddingModelJSONParser();

		return embeddingModelJSONParser.parseToDTO(json);
	}

	public static EmbeddingModel[] toDTOs(String json) {
		EmbeddingModelJSONParser embeddingModelJSONParser =
			new EmbeddingModelJSONParser();

		return embeddingModelJSONParser.parseToDTOs(json);
	}

	public static String toJSON(EmbeddingModel embeddingModel) {
		if (embeddingModel == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (embeddingModel.getModelId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modelId\": ");

			sb.append("\"");

			sb.append(_escape(embeddingModel.getModelId()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		EmbeddingModelJSONParser embeddingModelJSONParser =
			new EmbeddingModelJSONParser();

		return embeddingModelJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(EmbeddingModel embeddingModel) {
		if (embeddingModel == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (embeddingModel.getModelId() == null) {
			map.put("modelId", null);
		}
		else {
			map.put("modelId", String.valueOf(embeddingModel.getModelId()));
		}

		return map;
	}

	public static class EmbeddingModelJSONParser
		extends BaseJSONParser<EmbeddingModel> {

		@Override
		protected EmbeddingModel createDTO() {
			return new EmbeddingModel();
		}

		@Override
		protected EmbeddingModel[] createDTOArray(int size) {
			return new EmbeddingModel[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "modelId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			EmbeddingModel embeddingModel, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "modelId")) {
				if (jsonParserFieldValue != null) {
					embeddingModel.setModelId((String)jsonParserFieldValue);
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