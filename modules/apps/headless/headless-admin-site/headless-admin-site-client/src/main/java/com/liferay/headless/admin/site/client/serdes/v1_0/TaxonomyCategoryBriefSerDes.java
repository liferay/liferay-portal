/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
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
public class TaxonomyCategoryBriefSerDes {

	public static TaxonomyCategoryBrief toDTO(String json) {
		TaxonomyCategoryBriefJSONParser taxonomyCategoryBriefJSONParser =
			new TaxonomyCategoryBriefJSONParser();

		return taxonomyCategoryBriefJSONParser.parseToDTO(json);
	}

	public static TaxonomyCategoryBrief[] toDTOs(String json) {
		TaxonomyCategoryBriefJSONParser taxonomyCategoryBriefJSONParser =
			new TaxonomyCategoryBriefJSONParser();

		return taxonomyCategoryBriefJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TaxonomyCategoryBrief taxonomyCategoryBrief) {
		if (taxonomyCategoryBrief == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (taxonomyCategoryBrief.getParentTaxonomyCategory() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentTaxonomyCategory\": ");

			sb.append(
				String.valueOf(
					taxonomyCategoryBrief.getParentTaxonomyCategory()));
		}

		if (taxonomyCategoryBrief.getParentTaxonomyVocabulary() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentTaxonomyVocabulary\": ");

			sb.append(
				String.valueOf(
					taxonomyCategoryBrief.getParentTaxonomyVocabulary()));
		}

		if (taxonomyCategoryBrief.getScope() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scope\": ");

			sb.append(taxonomyCategoryBrief.getScope());
		}

		if (taxonomyCategoryBrief.getTaxonomyCategoryExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					taxonomyCategoryBrief.
						getTaxonomyCategoryExternalReferenceCode()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TaxonomyCategoryBriefJSONParser taxonomyCategoryBriefJSONParser =
			new TaxonomyCategoryBriefJSONParser();

		return taxonomyCategoryBriefJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		TaxonomyCategoryBrief taxonomyCategoryBrief) {

		if (taxonomyCategoryBrief == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (taxonomyCategoryBrief.getParentTaxonomyCategory() == null) {
			map.put("parentTaxonomyCategory", null);
		}
		else {
			map.put(
				"parentTaxonomyCategory",
				String.valueOf(
					taxonomyCategoryBrief.getParentTaxonomyCategory()));
		}

		if (taxonomyCategoryBrief.getParentTaxonomyVocabulary() == null) {
			map.put("parentTaxonomyVocabulary", null);
		}
		else {
			map.put(
				"parentTaxonomyVocabulary",
				String.valueOf(
					taxonomyCategoryBrief.getParentTaxonomyVocabulary()));
		}

		if (taxonomyCategoryBrief.getScope() == null) {
			map.put("scope", null);
		}
		else {
			map.put("scope", String.valueOf(taxonomyCategoryBrief.getScope()));
		}

		if (taxonomyCategoryBrief.getTaxonomyCategoryExternalReferenceCode() ==
				null) {

			map.put("taxonomyCategoryExternalReferenceCode", null);
		}
		else {
			map.put(
				"taxonomyCategoryExternalReferenceCode",
				String.valueOf(
					taxonomyCategoryBrief.
						getTaxonomyCategoryExternalReferenceCode()));
		}

		return map;
	}

	public static class TaxonomyCategoryBriefJSONParser
		extends BaseJSONParser<TaxonomyCategoryBrief> {

		@Override
		protected TaxonomyCategoryBrief createDTO() {
			return new TaxonomyCategoryBrief();
		}

		@Override
		protected TaxonomyCategoryBrief[] createDTOArray(int size) {
			return new TaxonomyCategoryBrief[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "parentTaxonomyCategory")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentTaxonomyVocabulary")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scope")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"taxonomyCategoryExternalReferenceCode")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TaxonomyCategoryBrief taxonomyCategoryBrief,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "parentTaxonomyCategory")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryBrief.setParentTaxonomyCategory(
						ParentTaxonomyCategorySerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentTaxonomyVocabulary")) {

				if (jsonParserFieldValue != null) {
					taxonomyCategoryBrief.setParentTaxonomyVocabulary(
						ParentTaxonomyVocabularySerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scope")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryBrief.setScope(
						com.liferay.headless.admin.site.client.scope.Scope.
							toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"taxonomyCategoryExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					taxonomyCategoryBrief.
						setTaxonomyCategoryExternalReferenceCode(
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:-1873669091