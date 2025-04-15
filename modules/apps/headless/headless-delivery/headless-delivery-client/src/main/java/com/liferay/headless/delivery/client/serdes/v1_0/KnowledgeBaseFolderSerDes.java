/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.KnowledgeBaseFolder;
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
public class KnowledgeBaseFolderSerDes {

	public static KnowledgeBaseFolder toDTO(String json) {
		KnowledgeBaseFolderJSONParser knowledgeBaseFolderJSONParser =
			new KnowledgeBaseFolderJSONParser();

		return knowledgeBaseFolderJSONParser.parseToDTO(json);
	}

	public static KnowledgeBaseFolder[] toDTOs(String json) {
		KnowledgeBaseFolderJSONParser knowledgeBaseFolderJSONParser =
			new KnowledgeBaseFolderJSONParser();

		return knowledgeBaseFolderJSONParser.parseToDTOs(json);
	}

	public static String toJSON(KnowledgeBaseFolder knowledgeBaseFolder) {
		if (knowledgeBaseFolder == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (knowledgeBaseFolder.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(knowledgeBaseFolder.getActions()));
		}

		if (knowledgeBaseFolder.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(knowledgeBaseFolder.getCreator()));
		}

		if (knowledgeBaseFolder.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < knowledgeBaseFolder.getCustomFields().length;
				 i++) {

				sb.append(knowledgeBaseFolder.getCustomFields()[i]);

				if ((i + 1) < knowledgeBaseFolder.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (knowledgeBaseFolder.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					knowledgeBaseFolder.getDateCreated()));

			sb.append("\"");
		}

		if (knowledgeBaseFolder.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					knowledgeBaseFolder.getDateModified()));

			sb.append("\"");
		}

		if (knowledgeBaseFolder.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(knowledgeBaseFolder.getDescription()));

			sb.append("\"");
		}

		if (knowledgeBaseFolder.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(knowledgeBaseFolder.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (knowledgeBaseFolder.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(knowledgeBaseFolder.getId());
		}

		if (knowledgeBaseFolder.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(knowledgeBaseFolder.getName()));

			sb.append("\"");
		}

		if (knowledgeBaseFolder.getNumberOfKnowledgeBaseArticles() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfKnowledgeBaseArticles\": ");

			sb.append(knowledgeBaseFolder.getNumberOfKnowledgeBaseArticles());
		}

		if (knowledgeBaseFolder.getNumberOfKnowledgeBaseFolders() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfKnowledgeBaseFolders\": ");

			sb.append(knowledgeBaseFolder.getNumberOfKnowledgeBaseFolders());
		}

		if (knowledgeBaseFolder.getParentKnowledgeBaseFolder() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentKnowledgeBaseFolder\": ");

			sb.append(
				String.valueOf(
					knowledgeBaseFolder.getParentKnowledgeBaseFolder()));
		}

		if (knowledgeBaseFolder.getParentKnowledgeBaseFolderId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentKnowledgeBaseFolderId\": ");

			sb.append(knowledgeBaseFolder.getParentKnowledgeBaseFolderId());
		}

		if (knowledgeBaseFolder.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(knowledgeBaseFolder.getSiteId());
		}

		if (knowledgeBaseFolder.getViewableBy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewableBy\": ");

			sb.append("\"");

			sb.append(knowledgeBaseFolder.getViewableBy());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		KnowledgeBaseFolderJSONParser knowledgeBaseFolderJSONParser =
			new KnowledgeBaseFolderJSONParser();

		return knowledgeBaseFolderJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		KnowledgeBaseFolder knowledgeBaseFolder) {

		if (knowledgeBaseFolder == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (knowledgeBaseFolder.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions", String.valueOf(knowledgeBaseFolder.getActions()));
		}

		if (knowledgeBaseFolder.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put(
				"creator", String.valueOf(knowledgeBaseFolder.getCreator()));
		}

		if (knowledgeBaseFolder.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields",
				String.valueOf(knowledgeBaseFolder.getCustomFields()));
		}

		if (knowledgeBaseFolder.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					knowledgeBaseFolder.getDateCreated()));
		}

		if (knowledgeBaseFolder.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					knowledgeBaseFolder.getDateModified()));
		}

		if (knowledgeBaseFolder.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(knowledgeBaseFolder.getDescription()));
		}

		if (knowledgeBaseFolder.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(knowledgeBaseFolder.getExternalReferenceCode()));
		}

		if (knowledgeBaseFolder.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(knowledgeBaseFolder.getId()));
		}

		if (knowledgeBaseFolder.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(knowledgeBaseFolder.getName()));
		}

		if (knowledgeBaseFolder.getNumberOfKnowledgeBaseArticles() == null) {
			map.put("numberOfKnowledgeBaseArticles", null);
		}
		else {
			map.put(
				"numberOfKnowledgeBaseArticles",
				String.valueOf(
					knowledgeBaseFolder.getNumberOfKnowledgeBaseArticles()));
		}

		if (knowledgeBaseFolder.getNumberOfKnowledgeBaseFolders() == null) {
			map.put("numberOfKnowledgeBaseFolders", null);
		}
		else {
			map.put(
				"numberOfKnowledgeBaseFolders",
				String.valueOf(
					knowledgeBaseFolder.getNumberOfKnowledgeBaseFolders()));
		}

		if (knowledgeBaseFolder.getParentKnowledgeBaseFolder() == null) {
			map.put("parentKnowledgeBaseFolder", null);
		}
		else {
			map.put(
				"parentKnowledgeBaseFolder",
				String.valueOf(
					knowledgeBaseFolder.getParentKnowledgeBaseFolder()));
		}

		if (knowledgeBaseFolder.getParentKnowledgeBaseFolderId() == null) {
			map.put("parentKnowledgeBaseFolderId", null);
		}
		else {
			map.put(
				"parentKnowledgeBaseFolderId",
				String.valueOf(
					knowledgeBaseFolder.getParentKnowledgeBaseFolderId()));
		}

		if (knowledgeBaseFolder.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(knowledgeBaseFolder.getSiteId()));
		}

		if (knowledgeBaseFolder.getViewableBy() == null) {
			map.put("viewableBy", null);
		}
		else {
			map.put(
				"viewableBy",
				String.valueOf(knowledgeBaseFolder.getViewableBy()));
		}

		return map;
	}

	public static class KnowledgeBaseFolderJSONParser
		extends BaseJSONParser<KnowledgeBaseFolder> {

		@Override
		protected KnowledgeBaseFolder createDTO() {
			return new KnowledgeBaseFolder();
		}

		@Override
		protected KnowledgeBaseFolder[] createDTOArray(int size) {
			return new KnowledgeBaseFolder[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
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
						jsonParserFieldName, "numberOfKnowledgeBaseArticles")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfKnowledgeBaseFolders")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentKnowledgeBaseFolder")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentKnowledgeBaseFolderId")) {

				return false;
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
			KnowledgeBaseFolder knowledgeBaseFolder, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					knowledgeBaseFolder.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					knowledgeBaseFolder.setCreator(
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

					knowledgeBaseFolder.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					knowledgeBaseFolder.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					knowledgeBaseFolder.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					knowledgeBaseFolder.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					knowledgeBaseFolder.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					knowledgeBaseFolder.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					knowledgeBaseFolder.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfKnowledgeBaseArticles")) {

				if (jsonParserFieldValue != null) {
					knowledgeBaseFolder.setNumberOfKnowledgeBaseArticles(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfKnowledgeBaseFolders")) {

				if (jsonParserFieldValue != null) {
					knowledgeBaseFolder.setNumberOfKnowledgeBaseFolders(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentKnowledgeBaseFolder")) {

				if (jsonParserFieldValue != null) {
					knowledgeBaseFolder.setParentKnowledgeBaseFolder(
						ParentKnowledgeBaseFolderSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentKnowledgeBaseFolderId")) {

				if (jsonParserFieldValue != null) {
					knowledgeBaseFolder.setParentKnowledgeBaseFolderId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					knowledgeBaseFolder.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				if (jsonParserFieldValue != null) {
					knowledgeBaseFolder.setViewableBy(
						KnowledgeBaseFolder.ViewableBy.create(
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