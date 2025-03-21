/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.ModelPrefilterContributor;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class ModelPrefilterContributorSerDes {

	public static ModelPrefilterContributor toDTO(String json) {
		ModelPrefilterContributorJSONParser
			modelPrefilterContributorJSONParser =
				new ModelPrefilterContributorJSONParser();

		return modelPrefilterContributorJSONParser.parseToDTO(json);
	}

	public static ModelPrefilterContributor[] toDTOs(String json) {
		ModelPrefilterContributorJSONParser
			modelPrefilterContributorJSONParser =
				new ModelPrefilterContributorJSONParser();

		return modelPrefilterContributorJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ModelPrefilterContributor modelPrefilterContributor) {

		if (modelPrefilterContributor == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (modelPrefilterContributor.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(modelPrefilterContributor.getClassName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ModelPrefilterContributorJSONParser
			modelPrefilterContributorJSONParser =
				new ModelPrefilterContributorJSONParser();

		return modelPrefilterContributorJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ModelPrefilterContributor modelPrefilterContributor) {

		if (modelPrefilterContributor == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (modelPrefilterContributor.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put(
				"className",
				String.valueOf(modelPrefilterContributor.getClassName()));
		}

		return map;
	}

	public static class ModelPrefilterContributorJSONParser
		extends BaseJSONParser<ModelPrefilterContributor> {

		@Override
		protected ModelPrefilterContributor createDTO() {
			return new ModelPrefilterContributor();
		}

		@Override
		protected ModelPrefilterContributor[] createDTOArray(int size) {
			return new ModelPrefilterContributor[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ModelPrefilterContributor modelPrefilterContributor,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					modelPrefilterContributor.setClassName(
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