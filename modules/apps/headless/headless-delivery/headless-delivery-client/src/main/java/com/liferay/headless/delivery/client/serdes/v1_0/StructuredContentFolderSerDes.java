/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.StructuredContentFolder;
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
public class StructuredContentFolderSerDes {

	public static StructuredContentFolder toDTO(String json) {
		StructuredContentFolderJSONParser structuredContentFolderJSONParser =
			new StructuredContentFolderJSONParser();

		return structuredContentFolderJSONParser.parseToDTO(json);
	}

	public static StructuredContentFolder[] toDTOs(String json) {
		StructuredContentFolderJSONParser structuredContentFolderJSONParser =
			new StructuredContentFolderJSONParser();

		return structuredContentFolderJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		StructuredContentFolder structuredContentFolder) {

		if (structuredContentFolder == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (structuredContentFolder.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(structuredContentFolder.getActions()));
		}

		if (structuredContentFolder.getAssetLibraryKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraryKey\": ");

			sb.append("\"");

			sb.append(_escape(structuredContentFolder.getAssetLibraryKey()));

			sb.append("\"");
		}

		if (structuredContentFolder.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(structuredContentFolder.getCreator()));
		}

		if (structuredContentFolder.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0;
				 i < structuredContentFolder.getCustomFields().length; i++) {

				sb.append(structuredContentFolder.getCustomFields()[i]);

				if ((i + 1) <
						structuredContentFolder.getCustomFields().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (structuredContentFolder.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					structuredContentFolder.getDateCreated()));

			sb.append("\"");
		}

		if (structuredContentFolder.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					structuredContentFolder.getDateModified()));

			sb.append("\"");
		}

		if (structuredContentFolder.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(structuredContentFolder.getDescription()));

			sb.append("\"");
		}

		if (structuredContentFolder.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(structuredContentFolder.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (structuredContentFolder.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(structuredContentFolder.getId());
		}

		if (structuredContentFolder.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(structuredContentFolder.getName()));

			sb.append("\"");
		}

		if (structuredContentFolder.getNumberOfStructuredContentFolders() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfStructuredContentFolders\": ");

			sb.append(
				structuredContentFolder.getNumberOfStructuredContentFolders());
		}

		if (structuredContentFolder.getNumberOfStructuredContents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfStructuredContents\": ");

			sb.append(structuredContentFolder.getNumberOfStructuredContents());
		}

		if (structuredContentFolder.getParentStructuredContentFolderId() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentStructuredContentFolderId\": ");

			sb.append(
				structuredContentFolder.getParentStructuredContentFolderId());
		}

		if (structuredContentFolder.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(structuredContentFolder.getSiteId());
		}

		if (structuredContentFolder.getSubscribed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscribed\": ");

			sb.append(structuredContentFolder.getSubscribed());
		}

		if (structuredContentFolder.getViewableBy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewableBy\": ");

			sb.append("\"");

			sb.append(structuredContentFolder.getViewableBy());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		StructuredContentFolderJSONParser structuredContentFolderJSONParser =
			new StructuredContentFolderJSONParser();

		return structuredContentFolderJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		StructuredContentFolder structuredContentFolder) {

		if (structuredContentFolder == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (structuredContentFolder.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions",
				String.valueOf(structuredContentFolder.getActions()));
		}

		if (structuredContentFolder.getAssetLibraryKey() == null) {
			map.put("assetLibraryKey", null);
		}
		else {
			map.put(
				"assetLibraryKey",
				String.valueOf(structuredContentFolder.getAssetLibraryKey()));
		}

		if (structuredContentFolder.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put(
				"creator",
				String.valueOf(structuredContentFolder.getCreator()));
		}

		if (structuredContentFolder.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields",
				String.valueOf(structuredContentFolder.getCustomFields()));
		}

		if (structuredContentFolder.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					structuredContentFolder.getDateCreated()));
		}

		if (structuredContentFolder.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					structuredContentFolder.getDateModified()));
		}

		if (structuredContentFolder.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(structuredContentFolder.getDescription()));
		}

		if (structuredContentFolder.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					structuredContentFolder.getExternalReferenceCode()));
		}

		if (structuredContentFolder.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(structuredContentFolder.getId()));
		}

		if (structuredContentFolder.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(structuredContentFolder.getName()));
		}

		if (structuredContentFolder.getNumberOfStructuredContentFolders() ==
				null) {

			map.put("numberOfStructuredContentFolders", null);
		}
		else {
			map.put(
				"numberOfStructuredContentFolders",
				String.valueOf(
					structuredContentFolder.
						getNumberOfStructuredContentFolders()));
		}

		if (structuredContentFolder.getNumberOfStructuredContents() == null) {
			map.put("numberOfStructuredContents", null);
		}
		else {
			map.put(
				"numberOfStructuredContents",
				String.valueOf(
					structuredContentFolder.getNumberOfStructuredContents()));
		}

		if (structuredContentFolder.getParentStructuredContentFolderId() ==
				null) {

			map.put("parentStructuredContentFolderId", null);
		}
		else {
			map.put(
				"parentStructuredContentFolderId",
				String.valueOf(
					structuredContentFolder.
						getParentStructuredContentFolderId()));
		}

		if (structuredContentFolder.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put(
				"siteId", String.valueOf(structuredContentFolder.getSiteId()));
		}

		if (structuredContentFolder.getSubscribed() == null) {
			map.put("subscribed", null);
		}
		else {
			map.put(
				"subscribed",
				String.valueOf(structuredContentFolder.getSubscribed()));
		}

		if (structuredContentFolder.getViewableBy() == null) {
			map.put("viewableBy", null);
		}
		else {
			map.put(
				"viewableBy",
				String.valueOf(structuredContentFolder.getViewableBy()));
		}

		return map;
	}

	public static class StructuredContentFolderJSONParser
		extends BaseJSONParser<StructuredContentFolder> {

		@Override
		protected StructuredContentFolder createDTO() {
			return new StructuredContentFolder();
		}

		@Override
		protected StructuredContentFolder[] createDTOArray(int size) {
			return new StructuredContentFolder[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
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
			else if (Objects.equals(
						jsonParserFieldName,
						"numberOfStructuredContentFolders")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfStructuredContents")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentStructuredContentFolderId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "subscribed")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			StructuredContentFolder structuredContentFolder,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					structuredContentFolder.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				if (jsonParserFieldValue != null) {
					structuredContentFolder.setAssetLibraryKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					structuredContentFolder.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.delivery.client.custom.field.
						CustomField[] customFieldsArray = new
						com.liferay.headless.delivery.client.custom.field.
							CustomField[jsonParserFieldValues.length];

					for (int i = 0; i < customFieldsArray.length; i++) {
						customFieldsArray[i] =
							com.liferay.headless.delivery.client.custom.field.
								CustomField.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					structuredContentFolder.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					structuredContentFolder.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					structuredContentFolder.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					structuredContentFolder.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					structuredContentFolder.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					structuredContentFolder.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					structuredContentFolder.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"numberOfStructuredContentFolders")) {

				if (jsonParserFieldValue != null) {
					structuredContentFolder.setNumberOfStructuredContentFolders(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfStructuredContents")) {

				if (jsonParserFieldValue != null) {
					structuredContentFolder.setNumberOfStructuredContents(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentStructuredContentFolderId")) {

				if (jsonParserFieldValue != null) {
					structuredContentFolder.setParentStructuredContentFolderId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					structuredContentFolder.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subscribed")) {
				if (jsonParserFieldValue != null) {
					structuredContentFolder.setSubscribed(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				if (jsonParserFieldValue != null) {
					structuredContentFolder.setViewableBy(
						StructuredContentFolder.ViewableBy.create(
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