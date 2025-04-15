/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.ContentField;
import com.liferay.headless.delivery.client.dto.v1_0.DocumentType;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class DocumentTypeSerDes {

	public static DocumentType toDTO(String json) {
		DocumentTypeJSONParser documentTypeJSONParser =
			new DocumentTypeJSONParser();

		return documentTypeJSONParser.parseToDTO(json);
	}

	public static DocumentType[] toDTOs(String json) {
		DocumentTypeJSONParser documentTypeJSONParser =
			new DocumentTypeJSONParser();

		return documentTypeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DocumentType documentType) {
		if (documentType == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (documentType.getAvailableLanguages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguages\": ");

			sb.append("[");

			for (int i = 0; i < documentType.getAvailableLanguages().length;
				 i++) {

				sb.append(_toJSON(documentType.getAvailableLanguages()[i]));

				if ((i + 1) < documentType.getAvailableLanguages().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (documentType.getContentFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentFields\": ");

			sb.append("[");

			for (int i = 0; i < documentType.getContentFields().length; i++) {
				sb.append(String.valueOf(documentType.getContentFields()[i]));

				if ((i + 1) < documentType.getContentFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (documentType.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(documentType.getDescription()));

			sb.append("\"");
		}

		if (documentType.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(documentType.getDescription_i18n()));
		}

		if (documentType.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(documentType.getName()));

			sb.append("\"");
		}

		if (documentType.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(documentType.getName_i18n()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DocumentTypeJSONParser documentTypeJSONParser =
			new DocumentTypeJSONParser();

		return documentTypeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DocumentType documentType) {
		if (documentType == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (documentType.getAvailableLanguages() == null) {
			map.put("availableLanguages", null);
		}
		else {
			map.put(
				"availableLanguages",
				String.valueOf(documentType.getAvailableLanguages()));
		}

		if (documentType.getContentFields() == null) {
			map.put("contentFields", null);
		}
		else {
			map.put(
				"contentFields",
				String.valueOf(documentType.getContentFields()));
		}

		if (documentType.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(documentType.getDescription()));
		}

		if (documentType.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(documentType.getDescription_i18n()));
		}

		if (documentType.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(documentType.getName()));
		}

		if (documentType.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put("name_i18n", String.valueOf(documentType.getName_i18n()));
		}

		return map;
	}

	public static class DocumentTypeJSONParser
		extends BaseJSONParser<DocumentType> {

		@Override
		protected DocumentType createDTO() {
			return new DocumentType();
		}

		@Override
		protected DocumentType[] createDTOArray(int size) {
			return new DocumentType[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "availableLanguages")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contentFields")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				return true;
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
			DocumentType documentType, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "availableLanguages")) {
				if (jsonParserFieldValue != null) {
					documentType.setAvailableLanguages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ContentField[] contentFieldsArray =
						new ContentField[jsonParserFieldValues.length];

					for (int i = 0; i < contentFieldsArray.length; i++) {
						contentFieldsArray[i] = ContentFieldSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					documentType.setContentFields(contentFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					documentType.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					documentType.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					documentType.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					documentType.setName_i18n(
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