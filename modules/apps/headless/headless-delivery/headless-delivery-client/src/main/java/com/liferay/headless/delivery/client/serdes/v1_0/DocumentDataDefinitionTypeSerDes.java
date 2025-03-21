/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.DataDefinitionField;
import com.liferay.headless.delivery.client.dto.v1_0.DocumentDataDefinitionType;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class DocumentDataDefinitionTypeSerDes {

	public static DocumentDataDefinitionType toDTO(String json) {
		DocumentDataDefinitionTypeJSONParser
			documentDataDefinitionTypeJSONParser =
				new DocumentDataDefinitionTypeJSONParser();

		return documentDataDefinitionTypeJSONParser.parseToDTO(json);
	}

	public static DocumentDataDefinitionType[] toDTOs(String json) {
		DocumentDataDefinitionTypeJSONParser
			documentDataDefinitionTypeJSONParser =
				new DocumentDataDefinitionTypeJSONParser();

		return documentDataDefinitionTypeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		DocumentDataDefinitionType documentDataDefinitionType) {

		if (documentDataDefinitionType == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (documentDataDefinitionType.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(documentDataDefinitionType.getActions()));
		}

		if (documentDataDefinitionType.getAssetLibraryKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraryKey\": ");

			sb.append("\"");

			sb.append(_escape(documentDataDefinitionType.getAssetLibraryKey()));

			sb.append("\"");
		}

		if (documentDataDefinitionType.getAvailableLanguages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguages\": ");

			sb.append("[");

			for (int i = 0;
				 i < documentDataDefinitionType.getAvailableLanguages().length;
				 i++) {

				sb.append(
					_toJSON(
						documentDataDefinitionType.getAvailableLanguages()[i]));

				if ((i + 1) <
						documentDataDefinitionType.
							getAvailableLanguages().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (documentDataDefinitionType.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(documentDataDefinitionType.getCreator()));
		}

		if (documentDataDefinitionType.getDataDefinitionFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataDefinitionFields\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 documentDataDefinitionType.
						 getDataDefinitionFields().length;
				 i++) {

				sb.append(
					documentDataDefinitionType.getDataDefinitionFields()[i]);

				if ((i + 1) < documentDataDefinitionType.
						getDataDefinitionFields().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (documentDataDefinitionType.getDataLayout() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataLayout\": ");

			sb.append(documentDataDefinitionType.getDataLayout());
		}

		if (documentDataDefinitionType.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					documentDataDefinitionType.getDateCreated()));

			sb.append("\"");
		}

		if (documentDataDefinitionType.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					documentDataDefinitionType.getDateModified()));

			sb.append("\"");
		}

		if (documentDataDefinitionType.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(documentDataDefinitionType.getDescription()));

			sb.append("\"");
		}

		if (documentDataDefinitionType.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(
				_toJSON(documentDataDefinitionType.getDescription_i18n()));
		}

		if (documentDataDefinitionType.getDocumentMetadataSetIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentMetadataSetIds\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 documentDataDefinitionType.
						 getDocumentMetadataSetIds().length;
				 i++) {

				sb.append(
					documentDataDefinitionType.getDocumentMetadataSetIds()[i]);

				if ((i + 1) < documentDataDefinitionType.
						getDocumentMetadataSetIds().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (documentDataDefinitionType.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(documentDataDefinitionType.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (documentDataDefinitionType.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(documentDataDefinitionType.getId());
		}

		if (documentDataDefinitionType.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(documentDataDefinitionType.getName()));

			sb.append("\"");
		}

		if (documentDataDefinitionType.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(documentDataDefinitionType.getName_i18n()));
		}

		if (documentDataDefinitionType.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(documentDataDefinitionType.getSiteId());
		}

		if (documentDataDefinitionType.getViewableBy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewableBy\": ");

			sb.append("\"");

			sb.append(documentDataDefinitionType.getViewableBy());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DocumentDataDefinitionTypeJSONParser
			documentDataDefinitionTypeJSONParser =
				new DocumentDataDefinitionTypeJSONParser();

		return documentDataDefinitionTypeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DocumentDataDefinitionType documentDataDefinitionType) {

		if (documentDataDefinitionType == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (documentDataDefinitionType.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions",
				String.valueOf(documentDataDefinitionType.getActions()));
		}

		if (documentDataDefinitionType.getAssetLibraryKey() == null) {
			map.put("assetLibraryKey", null);
		}
		else {
			map.put(
				"assetLibraryKey",
				String.valueOf(
					documentDataDefinitionType.getAssetLibraryKey()));
		}

		if (documentDataDefinitionType.getAvailableLanguages() == null) {
			map.put("availableLanguages", null);
		}
		else {
			map.put(
				"availableLanguages",
				String.valueOf(
					documentDataDefinitionType.getAvailableLanguages()));
		}

		if (documentDataDefinitionType.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put(
				"creator",
				String.valueOf(documentDataDefinitionType.getCreator()));
		}

		if (documentDataDefinitionType.getDataDefinitionFields() == null) {
			map.put("dataDefinitionFields", null);
		}
		else {
			map.put(
				"dataDefinitionFields",
				String.valueOf(
					documentDataDefinitionType.getDataDefinitionFields()));
		}

		if (documentDataDefinitionType.getDataLayout() == null) {
			map.put("dataLayout", null);
		}
		else {
			map.put(
				"dataLayout",
				String.valueOf(documentDataDefinitionType.getDataLayout()));
		}

		if (documentDataDefinitionType.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					documentDataDefinitionType.getDateCreated()));
		}

		if (documentDataDefinitionType.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					documentDataDefinitionType.getDateModified()));
		}

		if (documentDataDefinitionType.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(documentDataDefinitionType.getDescription()));
		}

		if (documentDataDefinitionType.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(
					documentDataDefinitionType.getDescription_i18n()));
		}

		if (documentDataDefinitionType.getDocumentMetadataSetIds() == null) {
			map.put("documentMetadataSetIds", null);
		}
		else {
			map.put(
				"documentMetadataSetIds",
				String.valueOf(
					documentDataDefinitionType.getDocumentMetadataSetIds()));
		}

		if (documentDataDefinitionType.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					documentDataDefinitionType.getExternalReferenceCode()));
		}

		if (documentDataDefinitionType.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(documentDataDefinitionType.getId()));
		}

		if (documentDataDefinitionType.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(documentDataDefinitionType.getName()));
		}

		if (documentDataDefinitionType.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put(
				"name_i18n",
				String.valueOf(documentDataDefinitionType.getName_i18n()));
		}

		if (documentDataDefinitionType.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put(
				"siteId",
				String.valueOf(documentDataDefinitionType.getSiteId()));
		}

		if (documentDataDefinitionType.getViewableBy() == null) {
			map.put("viewableBy", null);
		}
		else {
			map.put(
				"viewableBy",
				String.valueOf(documentDataDefinitionType.getViewableBy()));
		}

		return map;
	}

	public static class DocumentDataDefinitionTypeJSONParser
		extends BaseJSONParser<DocumentDataDefinitionType> {

		@Override
		protected DocumentDataDefinitionType createDTO() {
			return new DocumentDataDefinitionType();
		}

		@Override
		protected DocumentDataDefinitionType[] createDTOArray(int size) {
			return new DocumentDataDefinitionType[size];
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
			else if (Objects.equals(jsonParserFieldName, "creator")) {
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
						jsonParserFieldName, "documentMetadataSetIds")) {

				return false;
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
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DocumentDataDefinitionType documentDataDefinitionType,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					documentDataDefinitionType.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				if (jsonParserFieldValue != null) {
					documentDataDefinitionType.setAssetLibraryKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguages")) {

				if (jsonParserFieldValue != null) {
					documentDataDefinitionType.setAvailableLanguages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					documentDataDefinitionType.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
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

					documentDataDefinitionType.setDataDefinitionFields(
						dataDefinitionFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataLayout")) {
				if (jsonParserFieldValue != null) {
					documentDataDefinitionType.setDataLayout(
						DataLayoutSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					documentDataDefinitionType.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					documentDataDefinitionType.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					documentDataDefinitionType.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					documentDataDefinitionType.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "documentMetadataSetIds")) {

				if (jsonParserFieldValue != null) {
					documentDataDefinitionType.setDocumentMetadataSetIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					documentDataDefinitionType.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					documentDataDefinitionType.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					documentDataDefinitionType.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					documentDataDefinitionType.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					documentDataDefinitionType.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				if (jsonParserFieldValue != null) {
					documentDataDefinitionType.setViewableBy(
						DocumentDataDefinitionType.ViewableBy.create(
							(String)jsonParserFieldValue));
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