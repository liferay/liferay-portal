/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.language.override.client.serdes.v1_0;

import com.liferay.headless.admin.language.override.client.dto.v1_0.LanguageOverride;
import com.liferay.headless.admin.language.override.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Thiago Buarque
 * @generated
 */
@Generated("")
public class LanguageOverrideSerDes {

	public static LanguageOverride toDTO(String json) {
		LanguageOverrideJSONParser languageOverrideJSONParser =
			new LanguageOverrideJSONParser();

		return languageOverrideJSONParser.parseToDTO(json);
	}

	public static LanguageOverride[] toDTOs(String json) {
		LanguageOverrideJSONParser languageOverrideJSONParser =
			new LanguageOverrideJSONParser();

		return languageOverrideJSONParser.parseToDTOs(json);
	}

	public static String toJSON(LanguageOverride languageOverride) {
		if (languageOverride == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (languageOverride.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(languageOverride.getCreator()));
		}

		if (languageOverride.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					languageOverride.getDateCreated()));

			sb.append("\"");
		}

		if (languageOverride.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					languageOverride.getDateModified()));

			sb.append("\"");
		}

		if (languageOverride.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(languageOverride.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (languageOverride.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(languageOverride.getId());
		}

		if (languageOverride.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(languageOverride.getKey()));

			sb.append("\"");
		}

		if (languageOverride.getLanguageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"languageId\": ");

			sb.append("\"");

			sb.append(_escape(languageOverride.getLanguageId()));

			sb.append("\"");
		}

		if (languageOverride.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append("\"");

			sb.append(_escape(languageOverride.getValue()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		LanguageOverrideJSONParser languageOverrideJSONParser =
			new LanguageOverrideJSONParser();

		return languageOverrideJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(LanguageOverride languageOverride) {
		if (languageOverride == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (languageOverride.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(languageOverride.getCreator()));
		}

		if (languageOverride.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					languageOverride.getDateCreated()));
		}

		if (languageOverride.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					languageOverride.getDateModified()));
		}

		if (languageOverride.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(languageOverride.getExternalReferenceCode()));
		}

		if (languageOverride.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(languageOverride.getId()));
		}

		if (languageOverride.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(languageOverride.getKey()));
		}

		if (languageOverride.getLanguageId() == null) {
			map.put("languageId", null);
		}
		else {
			map.put(
				"languageId", String.valueOf(languageOverride.getLanguageId()));
		}

		if (languageOverride.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(languageOverride.getValue()));
		}

		return map;
	}

	public static class LanguageOverrideJSONParser
		extends BaseJSONParser<LanguageOverride> {

		@Override
		protected LanguageOverride createDTO() {
			return new LanguageOverride();
		}

		@Override
		protected LanguageOverride[] createDTOArray(int size) {
			return new LanguageOverride[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "languageId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			LanguageOverride languageOverride, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					languageOverride.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					languageOverride.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					languageOverride.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					languageOverride.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					languageOverride.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					languageOverride.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "languageId")) {
				if (jsonParserFieldValue != null) {
					languageOverride.setLanguageId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					languageOverride.setValue((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:727077235