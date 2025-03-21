/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ParentTaxonomyVocabulary;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ParentTaxonomyVocabularySerDes {

	public static ParentTaxonomyVocabulary toDTO(String json) {
		ParentTaxonomyVocabularyJSONParser parentTaxonomyVocabularyJSONParser =
			new ParentTaxonomyVocabularyJSONParser();

		return parentTaxonomyVocabularyJSONParser.parseToDTO(json);
	}

	public static ParentTaxonomyVocabulary[] toDTOs(String json) {
		ParentTaxonomyVocabularyJSONParser parentTaxonomyVocabularyJSONParser =
			new ParentTaxonomyVocabularyJSONParser();

		return parentTaxonomyVocabularyJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ParentTaxonomyVocabulary parentTaxonomyVocabulary) {

		if (parentTaxonomyVocabulary == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (parentTaxonomyVocabulary.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(parentTaxonomyVocabulary.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (parentTaxonomyVocabulary.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(parentTaxonomyVocabulary.getId());
		}

		if (parentTaxonomyVocabulary.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(parentTaxonomyVocabulary.getName()));

			sb.append("\"");
		}

		if (parentTaxonomyVocabulary.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(parentTaxonomyVocabulary.getName_i18n()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ParentTaxonomyVocabularyJSONParser parentTaxonomyVocabularyJSONParser =
			new ParentTaxonomyVocabularyJSONParser();

		return parentTaxonomyVocabularyJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ParentTaxonomyVocabulary parentTaxonomyVocabulary) {

		if (parentTaxonomyVocabulary == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (parentTaxonomyVocabulary.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					parentTaxonomyVocabulary.getExternalReferenceCode()));
		}

		if (parentTaxonomyVocabulary.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(parentTaxonomyVocabulary.getId()));
		}

		if (parentTaxonomyVocabulary.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(parentTaxonomyVocabulary.getName()));
		}

		if (parentTaxonomyVocabulary.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put(
				"name_i18n",
				String.valueOf(parentTaxonomyVocabulary.getName_i18n()));
		}

		return map;
	}

	public static class ParentTaxonomyVocabularyJSONParser
		extends BaseJSONParser<ParentTaxonomyVocabulary> {

		@Override
		protected ParentTaxonomyVocabulary createDTO() {
			return new ParentTaxonomyVocabulary();
		}

		@Override
		protected ParentTaxonomyVocabulary[] createDTOArray(int size) {
			return new ParentTaxonomyVocabulary[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			ParentTaxonomyVocabulary parentTaxonomyVocabulary,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					parentTaxonomyVocabulary.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					parentTaxonomyVocabulary.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					parentTaxonomyVocabulary.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					parentTaxonomyVocabulary.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
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