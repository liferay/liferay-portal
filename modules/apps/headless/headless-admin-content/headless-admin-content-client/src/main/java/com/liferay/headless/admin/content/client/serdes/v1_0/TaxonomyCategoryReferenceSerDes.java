/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.client.serdes.v1_0;

import com.liferay.headless.admin.content.client.dto.v1_0.TaxonomyCategoryReference;
import com.liferay.headless.admin.content.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class TaxonomyCategoryReferenceSerDes {

	public static TaxonomyCategoryReference toDTO(String json) {
		TaxonomyCategoryReferenceJSONParser
			taxonomyCategoryReferenceJSONParser =
				new TaxonomyCategoryReferenceJSONParser();

		return taxonomyCategoryReferenceJSONParser.parseToDTO(json);
	}

	public static TaxonomyCategoryReference[] toDTOs(String json) {
		TaxonomyCategoryReferenceJSONParser
			taxonomyCategoryReferenceJSONParser =
				new TaxonomyCategoryReferenceJSONParser();

		return taxonomyCategoryReferenceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		TaxonomyCategoryReference taxonomyCategoryReference) {

		if (taxonomyCategoryReference == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (taxonomyCategoryReference.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(taxonomyCategoryReference.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (taxonomyCategoryReference.getSiteKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteKey\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyCategoryReference.getSiteKey()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TaxonomyCategoryReferenceJSONParser
			taxonomyCategoryReferenceJSONParser =
				new TaxonomyCategoryReferenceJSONParser();

		return taxonomyCategoryReferenceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		TaxonomyCategoryReference taxonomyCategoryReference) {

		if (taxonomyCategoryReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (taxonomyCategoryReference.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					taxonomyCategoryReference.getExternalReferenceCode()));
		}

		if (taxonomyCategoryReference.getSiteKey() == null) {
			map.put("siteKey", null);
		}
		else {
			map.put(
				"siteKey",
				String.valueOf(taxonomyCategoryReference.getSiteKey()));
		}

		return map;
	}

	public static class TaxonomyCategoryReferenceJSONParser
		extends BaseJSONParser<TaxonomyCategoryReference> {

		@Override
		protected TaxonomyCategoryReference createDTO() {
			return new TaxonomyCategoryReference();
		}

		@Override
		protected TaxonomyCategoryReference[] createDTOArray(int size) {
			return new TaxonomyCategoryReference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteKey")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TaxonomyCategoryReference taxonomyCategoryReference,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryReference.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteKey")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryReference.setSiteKey(
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