/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.DataDefinitionField;
import com.liferay.headless.delivery.client.dto.v1_0.DocumentMetadataSet;
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
public class DocumentMetadataSetSerDes {

	public static DocumentMetadataSet toDTO(String json) {
		DocumentMetadataSetJSONParser documentMetadataSetJSONParser =
			new DocumentMetadataSetJSONParser();

		return documentMetadataSetJSONParser.parseToDTO(json);
	}

	public static DocumentMetadataSet[] toDTOs(String json) {
		DocumentMetadataSetJSONParser documentMetadataSetJSONParser =
			new DocumentMetadataSetJSONParser();

		return documentMetadataSetJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DocumentMetadataSet documentMetadataSet) {
		if (documentMetadataSet == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (documentMetadataSet.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(documentMetadataSet.getActions()));
		}

		if (documentMetadataSet.getAssetLibraryKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraryKey\": ");

			sb.append("\"");

			sb.append(_escape(documentMetadataSet.getAssetLibraryKey()));

			sb.append("\"");
		}

		if (documentMetadataSet.getAvailableLanguages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguages\": ");

			sb.append("[");

			for (int i = 0;
				 i < documentMetadataSet.getAvailableLanguages().length; i++) {

				sb.append(
					_toJSON(documentMetadataSet.getAvailableLanguages()[i]));

				if ((i + 1) <
						documentMetadataSet.getAvailableLanguages().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (documentMetadataSet.getDataDefinitionFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataDefinitionFields\": ");

			sb.append("[");

			for (int i = 0;
				 i < documentMetadataSet.getDataDefinitionFields().length;
				 i++) {

				sb.append(documentMetadataSet.getDataDefinitionFields()[i]);

				if ((i + 1) <
						documentMetadataSet.getDataDefinitionFields().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (documentMetadataSet.getDataLayout() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataLayout\": ");

			sb.append(documentMetadataSet.getDataLayout());
		}

		if (documentMetadataSet.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					documentMetadataSet.getDateCreated()));

			sb.append("\"");
		}

		if (documentMetadataSet.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					documentMetadataSet.getDateModified()));

			sb.append("\"");
		}

		if (documentMetadataSet.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(documentMetadataSet.getDescription()));

			sb.append("\"");
		}

		if (documentMetadataSet.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(documentMetadataSet.getDescription_i18n()));
		}

		if (documentMetadataSet.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(documentMetadataSet.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (documentMetadataSet.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(documentMetadataSet.getId());
		}

		if (documentMetadataSet.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(documentMetadataSet.getName()));

			sb.append("\"");
		}

		if (documentMetadataSet.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(documentMetadataSet.getName_i18n()));
		}

		if (documentMetadataSet.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(documentMetadataSet.getSiteId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DocumentMetadataSetJSONParser documentMetadataSetJSONParser =
			new DocumentMetadataSetJSONParser();

		return documentMetadataSetJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DocumentMetadataSet documentMetadataSet) {

		if (documentMetadataSet == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (documentMetadataSet.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions", String.valueOf(documentMetadataSet.getActions()));
		}

		if (documentMetadataSet.getAssetLibraryKey() == null) {
			map.put("assetLibraryKey", null);
		}
		else {
			map.put(
				"assetLibraryKey",
				String.valueOf(documentMetadataSet.getAssetLibraryKey()));
		}

		if (documentMetadataSet.getAvailableLanguages() == null) {
			map.put("availableLanguages", null);
		}
		else {
			map.put(
				"availableLanguages",
				String.valueOf(documentMetadataSet.getAvailableLanguages()));
		}

		if (documentMetadataSet.getDataDefinitionFields() == null) {
			map.put("dataDefinitionFields", null);
		}
		else {
			map.put(
				"dataDefinitionFields",
				String.valueOf(documentMetadataSet.getDataDefinitionFields()));
		}

		if (documentMetadataSet.getDataLayout() == null) {
			map.put("dataLayout", null);
		}
		else {
			map.put(
				"dataLayout",
				String.valueOf(documentMetadataSet.getDataLayout()));
		}

		if (documentMetadataSet.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					documentMetadataSet.getDateCreated()));
		}

		if (documentMetadataSet.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					documentMetadataSet.getDateModified()));
		}

		if (documentMetadataSet.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(documentMetadataSet.getDescription()));
		}

		if (documentMetadataSet.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(documentMetadataSet.getDescription_i18n()));
		}

		if (documentMetadataSet.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(documentMetadataSet.getExternalReferenceCode()));
		}

		if (documentMetadataSet.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(documentMetadataSet.getId()));
		}

		if (documentMetadataSet.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(documentMetadataSet.getName()));
		}

		if (documentMetadataSet.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put(
				"name_i18n",
				String.valueOf(documentMetadataSet.getName_i18n()));
		}

		if (documentMetadataSet.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(documentMetadataSet.getSiteId()));
		}

		return map;
	}

	public static class DocumentMetadataSetJSONParser
		extends BaseJSONParser<DocumentMetadataSet> {

		@Override
		protected DocumentMetadataSet createDTO() {
			return new DocumentMetadataSet();
		}

		@Override
		protected DocumentMetadataSet[] createDTOArray(int size) {
			return new DocumentMetadataSet[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguages")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "dataDefinitionFields")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dataLayout")) {
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
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

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
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DocumentMetadataSet documentMetadataSet, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					documentMetadataSet.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				if (jsonParserFieldValue != null) {
					documentMetadataSet.setAssetLibraryKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguages")) {

				if (jsonParserFieldValue != null) {
					documentMetadataSet.setAvailableLanguages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "dataDefinitionFields")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DataDefinitionField[] dataDefinitionFieldsArray =
						new DataDefinitionField[jsonParserFieldValues.length];

					for (int i = 0; i < dataDefinitionFieldsArray.length; i++) {
						dataDefinitionFieldsArray[i] =
							DataDefinitionFieldSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					documentMetadataSet.setDataDefinitionFields(
						dataDefinitionFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataLayout")) {
				if (jsonParserFieldValue != null) {
					documentMetadataSet.setDataLayout(
						DataLayoutSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					documentMetadataSet.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					documentMetadataSet.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					documentMetadataSet.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					documentMetadataSet.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					documentMetadataSet.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					documentMetadataSet.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					documentMetadataSet.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					documentMetadataSet.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					documentMetadataSet.setSiteId(
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