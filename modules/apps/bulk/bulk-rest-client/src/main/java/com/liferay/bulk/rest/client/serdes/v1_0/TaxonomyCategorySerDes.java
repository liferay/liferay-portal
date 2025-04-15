/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.TaxonomyCategory;
import com.liferay.bulk.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class TaxonomyCategorySerDes {

	public static TaxonomyCategory toDTO(String json) {
		TaxonomyCategoryJSONParser taxonomyCategoryJSONParser =
			new TaxonomyCategoryJSONParser();

		return taxonomyCategoryJSONParser.parseToDTO(json);
	}

	public static TaxonomyCategory[] toDTOs(String json) {
		TaxonomyCategoryJSONParser taxonomyCategoryJSONParser =
			new TaxonomyCategoryJSONParser();

		return taxonomyCategoryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TaxonomyCategory taxonomyCategory) {
		if (taxonomyCategory == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (taxonomyCategory.getTaxonomyCategoryId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryId\": ");

			sb.append(taxonomyCategory.getTaxonomyCategoryId());
		}

		if (taxonomyCategory.getTaxonomyCategoryName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryName\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyCategory.getTaxonomyCategoryName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TaxonomyCategoryJSONParser taxonomyCategoryJSONParser =
			new TaxonomyCategoryJSONParser();

		return taxonomyCategoryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(TaxonomyCategory taxonomyCategory) {
		if (taxonomyCategory == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (taxonomyCategory.getTaxonomyCategoryId() == null) {
			map.put("taxonomyCategoryId", null);
		}
		else {
			map.put(
				"taxonomyCategoryId",
				String.valueOf(taxonomyCategory.getTaxonomyCategoryId()));
		}

		if (taxonomyCategory.getTaxonomyCategoryName() == null) {
			map.put("taxonomyCategoryName", null);
		}
		else {
			map.put(
				"taxonomyCategoryName",
				String.valueOf(taxonomyCategory.getTaxonomyCategoryName()));
		}

		return map;
	}

	public static class TaxonomyCategoryJSONParser
		extends BaseJSONParser<TaxonomyCategory> {

		@Override
		protected TaxonomyCategory createDTO() {
			return new TaxonomyCategory();
		}

		@Override
		protected TaxonomyCategory[] createDTOArray(int size) {
			return new TaxonomyCategory[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "taxonomyCategoryId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryName")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TaxonomyCategory taxonomyCategory, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "taxonomyCategoryId")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategory.setTaxonomyCategoryId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryName")) {

				if (jsonParserFieldValue != null) {
					taxonomyCategory.setTaxonomyCategoryName(
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