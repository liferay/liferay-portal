/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.StyleBook;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class StyleBookSerDes {

	public static StyleBook toDTO(String json) {
		StyleBookJSONParser styleBookJSONParser = new StyleBookJSONParser();

		return styleBookJSONParser.parseToDTO(json);
	}

	public static StyleBook[] toDTOs(String json) {
		StyleBookJSONParser styleBookJSONParser = new StyleBookJSONParser();

		return styleBookJSONParser.parseToDTOs(json);
	}

	public static String toJSON(StyleBook styleBook) {
		if (styleBook == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (styleBook.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(styleBook.getActions()));
		}

		if (styleBook.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(styleBook.getCreator());
		}

		if (styleBook.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(styleBook.getDateCreated()));

			sb.append("\"");
		}

		if (styleBook.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(styleBook.getDateModified()));

			sb.append("\"");
		}

		if (styleBook.getDefaultStyleBook() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultStyleBook\": ");

			sb.append(styleBook.getDefaultStyleBook());
		}

		if (styleBook.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(styleBook.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (styleBook.getFrontendTokensValues() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"frontendTokensValues\": ");

			sb.append("\"");

			sb.append(_escape(styleBook.getFrontendTokensValues()));

			sb.append("\"");
		}

		if (styleBook.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(styleBook.getId());
		}

		if (styleBook.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(styleBook.getKey()));

			sb.append("\"");
		}

		if (styleBook.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(styleBook.getName()));

			sb.append("\"");
		}

		if (styleBook.getPreviewFileEntryExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"previewFileEntryExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(styleBook.getPreviewFileEntryExternalReferenceCode()));

			sb.append("\"");
		}

		if (styleBook.getThemeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"themeId\": ");

			sb.append("\"");

			sb.append(_escape(styleBook.getThemeId()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		StyleBookJSONParser styleBookJSONParser = new StyleBookJSONParser();

		return styleBookJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(StyleBook styleBook) {
		if (styleBook == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (styleBook.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(styleBook.getActions()));
		}

		if (styleBook.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(styleBook.getCreator()));
		}

		if (styleBook.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(styleBook.getDateCreated()));
		}

		if (styleBook.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(styleBook.getDateModified()));
		}

		if (styleBook.getDefaultStyleBook() == null) {
			map.put("defaultStyleBook", null);
		}
		else {
			map.put(
				"defaultStyleBook",
				String.valueOf(styleBook.getDefaultStyleBook()));
		}

		if (styleBook.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(styleBook.getExternalReferenceCode()));
		}

		if (styleBook.getFrontendTokensValues() == null) {
			map.put("frontendTokensValues", null);
		}
		else {
			map.put(
				"frontendTokensValues",
				String.valueOf(styleBook.getFrontendTokensValues()));
		}

		if (styleBook.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(styleBook.getId()));
		}

		if (styleBook.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(styleBook.getKey()));
		}

		if (styleBook.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(styleBook.getName()));
		}

		if (styleBook.getPreviewFileEntryExternalReferenceCode() == null) {
			map.put("previewFileEntryExternalReferenceCode", null);
		}
		else {
			map.put(
				"previewFileEntryExternalReferenceCode",
				String.valueOf(
					styleBook.getPreviewFileEntryExternalReferenceCode()));
		}

		if (styleBook.getThemeId() == null) {
			map.put("themeId", null);
		}
		else {
			map.put("themeId", String.valueOf(styleBook.getThemeId()));
		}

		return map;
	}

	public static class StyleBookJSONParser extends BaseJSONParser<StyleBook> {

		@Override
		protected StyleBook createDTO() {
			return new StyleBook();
		}

		@Override
		protected StyleBook[] createDTOArray(int size) {
			return new StyleBook[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultStyleBook")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "frontendTokensValues")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"previewFileEntryExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "themeId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			StyleBook styleBook, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					styleBook.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					styleBook.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					styleBook.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					styleBook.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultStyleBook")) {
				if (jsonParserFieldValue != null) {
					styleBook.setDefaultStyleBook(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					styleBook.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "frontendTokensValues")) {

				if (jsonParserFieldValue != null) {
					styleBook.setFrontendTokensValues(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					styleBook.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					styleBook.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					styleBook.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"previewFileEntryExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					styleBook.setPreviewFileEntryExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "themeId")) {
				if (jsonParserFieldValue != null) {
					styleBook.setThemeId((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-841545486