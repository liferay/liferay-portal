/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.ContentStructure;
import com.liferay.headless.delivery.client.dto.v1_0.ContentStructureField;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class ContentStructureSerDes {

	public static ContentStructure toDTO(String json) {
		ContentStructureJSONParser contentStructureJSONParser =
			new ContentStructureJSONParser();

		return contentStructureJSONParser.parseToDTO(json);
	}

	public static ContentStructure[] toDTOs(String json) {
		ContentStructureJSONParser contentStructureJSONParser =
			new ContentStructureJSONParser();

		return contentStructureJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ContentStructure contentStructure) {
		if (contentStructure == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (contentStructure.getAssetLibraryKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraryKey\": ");

			sb.append("\"");

			sb.append(_escape(contentStructure.getAssetLibraryKey()));

			sb.append("\"");
		}

		if (contentStructure.getAvailableLanguages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguages\": ");

			sb.append("[");

			for (int i = 0; i < contentStructure.getAvailableLanguages().length;
				 i++) {

				sb.append(_toJSON(contentStructure.getAvailableLanguages()[i]));

				if ((i + 1) < contentStructure.getAvailableLanguages().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (contentStructure.getContentStructureFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentStructureFields\": ");

			sb.append("[");

			for (int i = 0;
				 i < contentStructure.getContentStructureFields().length; i++) {

				sb.append(
					String.valueOf(
						contentStructure.getContentStructureFields()[i]));

				if ((i + 1) <
						contentStructure.getContentStructureFields().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (contentStructure.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(contentStructure.getCreator()));
		}

		if (contentStructure.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					contentStructure.getDateCreated()));

			sb.append("\"");
		}

		if (contentStructure.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					contentStructure.getDateModified()));

			sb.append("\"");
		}

		if (contentStructure.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(contentStructure.getDescription()));

			sb.append("\"");
		}

		if (contentStructure.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(contentStructure.getDescription_i18n()));
		}

		if (contentStructure.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(contentStructure.getId());
		}

		if (contentStructure.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(contentStructure.getName()));

			sb.append("\"");
		}

		if (contentStructure.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(contentStructure.getName_i18n()));
		}

		if (contentStructure.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(contentStructure.getSiteId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ContentStructureJSONParser contentStructureJSONParser =
			new ContentStructureJSONParser();

		return contentStructureJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ContentStructure contentStructure) {
		if (contentStructure == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (contentStructure.getAssetLibraryKey() == null) {
			map.put("assetLibraryKey", null);
		}
		else {
			map.put(
				"assetLibraryKey",
				String.valueOf(contentStructure.getAssetLibraryKey()));
		}

		if (contentStructure.getAvailableLanguages() == null) {
			map.put("availableLanguages", null);
		}
		else {
			map.put(
				"availableLanguages",
				String.valueOf(contentStructure.getAvailableLanguages()));
		}

		if (contentStructure.getContentStructureFields() == null) {
			map.put("contentStructureFields", null);
		}
		else {
			map.put(
				"contentStructureFields",
				String.valueOf(contentStructure.getContentStructureFields()));
		}

		if (contentStructure.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(contentStructure.getCreator()));
		}

		if (contentStructure.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					contentStructure.getDateCreated()));
		}

		if (contentStructure.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					contentStructure.getDateModified()));
		}

		if (contentStructure.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(contentStructure.getDescription()));
		}

		if (contentStructure.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(contentStructure.getDescription_i18n()));
		}

		if (contentStructure.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(contentStructure.getId()));
		}

		if (contentStructure.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(contentStructure.getName()));
		}

		if (contentStructure.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put(
				"name_i18n", String.valueOf(contentStructure.getName_i18n()));
		}

		if (contentStructure.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(contentStructure.getSiteId()));
		}

		return map;
	}

	public static class ContentStructureJSONParser
		extends BaseJSONParser<ContentStructure> {

		@Override
		protected ContentStructure createDTO() {
			return new ContentStructure();
		}

		@Override
		protected ContentStructure[] createDTOArray(int size) {
			return new ContentStructure[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguages")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "contentStructureFields")) {

				return false;
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
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				return true;
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
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ContentStructure contentStructure, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				if (jsonParserFieldValue != null) {
					contentStructure.setAssetLibraryKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguages")) {

				if (jsonParserFieldValue != null) {
					contentStructure.setAvailableLanguages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "contentStructureFields")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ContentStructureField[] contentStructureFieldsArray =
						new ContentStructureField[jsonParserFieldValues.length];

					for (int i = 0; i < contentStructureFieldsArray.length;
						 i++) {

						contentStructureFieldsArray[i] =
							ContentStructureFieldSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					contentStructure.setContentStructureFields(
						contentStructureFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					contentStructure.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					contentStructure.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					contentStructure.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					contentStructure.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					contentStructure.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					contentStructure.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					contentStructure.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					contentStructure.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					contentStructure.setSiteId(
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