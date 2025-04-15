/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.TaxonomyCategory;
import com.liferay.bulk.rest.client.dto.v1_0.TaxonomyVocabulary;
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
public class TaxonomyVocabularySerDes {

	public static TaxonomyVocabulary toDTO(String json) {
		TaxonomyVocabularyJSONParser taxonomyVocabularyJSONParser =
			new TaxonomyVocabularyJSONParser();

		return taxonomyVocabularyJSONParser.parseToDTO(json);
	}

	public static TaxonomyVocabulary[] toDTOs(String json) {
		TaxonomyVocabularyJSONParser taxonomyVocabularyJSONParser =
			new TaxonomyVocabularyJSONParser();

		return taxonomyVocabularyJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TaxonomyVocabulary taxonomyVocabulary) {
		if (taxonomyVocabulary == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (taxonomyVocabulary.getMultiValued() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"multiValued\": ");

			sb.append(taxonomyVocabulary.getMultiValued());
		}

		if (taxonomyVocabulary.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyVocabulary.getName()));

			sb.append("\"");
		}

		if (taxonomyVocabulary.getRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(taxonomyVocabulary.getRequired());
		}

		if (taxonomyVocabulary.getTaxonomyCategories() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategories\": ");

			sb.append("[");

			for (int i = 0;
				 i < taxonomyVocabulary.getTaxonomyCategories().length; i++) {

				sb.append(
					String.valueOf(
						taxonomyVocabulary.getTaxonomyCategories()[i]));

				if ((i + 1) <
						taxonomyVocabulary.getTaxonomyCategories().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (taxonomyVocabulary.getTaxonomyVocabularyId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyVocabularyId\": ");

			sb.append(taxonomyVocabulary.getTaxonomyVocabularyId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TaxonomyVocabularyJSONParser taxonomyVocabularyJSONParser =
			new TaxonomyVocabularyJSONParser();

		return taxonomyVocabularyJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		TaxonomyVocabulary taxonomyVocabulary) {

		if (taxonomyVocabulary == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (taxonomyVocabulary.getMultiValued() == null) {
			map.put("multiValued", null);
		}
		else {
			map.put(
				"multiValued",
				String.valueOf(taxonomyVocabulary.getMultiValued()));
		}

		if (taxonomyVocabulary.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(taxonomyVocabulary.getName()));
		}

		if (taxonomyVocabulary.getRequired() == null) {
			map.put("required", null);
		}
		else {
			map.put(
				"required", String.valueOf(taxonomyVocabulary.getRequired()));
		}

		if (taxonomyVocabulary.getTaxonomyCategories() == null) {
			map.put("taxonomyCategories", null);
		}
		else {
			map.put(
				"taxonomyCategories",
				String.valueOf(taxonomyVocabulary.getTaxonomyCategories()));
		}

		if (taxonomyVocabulary.getTaxonomyVocabularyId() == null) {
			map.put("taxonomyVocabularyId", null);
		}
		else {
			map.put(
				"taxonomyVocabularyId",
				String.valueOf(taxonomyVocabulary.getTaxonomyVocabularyId()));
		}

		return map;
	}

	public static class TaxonomyVocabularyJSONParser
		extends BaseJSONParser<TaxonomyVocabulary> {

		@Override
		protected TaxonomyVocabulary createDTO() {
			return new TaxonomyVocabulary();
		}

		@Override
		protected TaxonomyVocabulary[] createDTOArray(int size) {
			return new TaxonomyVocabulary[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "multiValued")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategories")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyVocabularyId")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TaxonomyVocabulary taxonomyVocabulary, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "multiValued")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setMultiValued(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setRequired(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategories")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					TaxonomyCategory[] taxonomyCategoriesArray =
						new TaxonomyCategory[jsonParserFieldValues.length];

					for (int i = 0; i < taxonomyCategoriesArray.length; i++) {
						taxonomyCategoriesArray[i] =
							TaxonomyCategorySerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					taxonomyVocabulary.setTaxonomyCategories(
						taxonomyCategoriesArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyVocabularyId")) {

				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setTaxonomyVocabularyId(
						Long.valueOf((String)jsonParserFieldValue));
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