/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.client.serdes.v1_0;

import com.liferay.headless.object.client.dto.v1_0.ObjectEntryFolder;
import com.liferay.headless.object.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alicia García
 * @generated
 */
@Generated("")
public class ObjectEntryFolderSerDes {

	public static ObjectEntryFolder toDTO(String json) {
		ObjectEntryFolderJSONParser objectEntryFolderJSONParser =
			new ObjectEntryFolderJSONParser();

		return objectEntryFolderJSONParser.parseToDTO(json);
	}

	public static ObjectEntryFolder[] toDTOs(String json) {
		ObjectEntryFolderJSONParser objectEntryFolderJSONParser =
			new ObjectEntryFolderJSONParser();

		return objectEntryFolderJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectEntryFolder objectEntryFolder) {
		if (objectEntryFolder == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (objectEntryFolder.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(objectEntryFolder.getActions()));
		}

		if (objectEntryFolder.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(objectEntryFolder.getCreator());
		}

		if (objectEntryFolder.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					objectEntryFolder.getDateCreated()));

			sb.append("\"");
		}

		if (objectEntryFolder.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					objectEntryFolder.getDateModified()));

			sb.append("\"");
		}

		if (objectEntryFolder.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(objectEntryFolder.getDescription()));

			sb.append("\"");
		}

		if (objectEntryFolder.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(objectEntryFolder.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (objectEntryFolder.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(objectEntryFolder.getId());
		}

		if (objectEntryFolder.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(objectEntryFolder.getLabel()));

			sb.append("\"");
		}

		if (objectEntryFolder.getLabel_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label_i18n\": ");

			sb.append(_toJSON(objectEntryFolder.getLabel_i18n()));
		}

		if (objectEntryFolder.getNumberOfObjectEntries() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfObjectEntries\": ");

			sb.append(objectEntryFolder.getNumberOfObjectEntries());
		}

		if (objectEntryFolder.getNumberOfObjectEntryFolders() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfObjectEntryFolders\": ");

			sb.append(objectEntryFolder.getNumberOfObjectEntryFolders());
		}

		if (objectEntryFolder.getParentObjectEntryFolderBrief() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentObjectEntryFolderBrief\": ");

			sb.append(
				String.valueOf(
					objectEntryFolder.getParentObjectEntryFolderBrief()));
		}

		if (objectEntryFolder.
				getParentObjectEntryFolderExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentObjectEntryFolderExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					objectEntryFolder.
						getParentObjectEntryFolderExternalReferenceCode()));

			sb.append("\"");
		}

		if (objectEntryFolder.getParentObjectEntryFolderId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentObjectEntryFolderId\": ");

			sb.append(objectEntryFolder.getParentObjectEntryFolderId());
		}

		if (objectEntryFolder.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < objectEntryFolder.getPermissions().length;
				 i++) {

				sb.append(objectEntryFolder.getPermissions()[i]);

				if ((i + 1) < objectEntryFolder.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (objectEntryFolder.getRemovedBy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"removedBy\": ");

			sb.append(objectEntryFolder.getRemovedBy());
		}

		if (objectEntryFolder.getRemovedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"removedDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					objectEntryFolder.getRemovedDate()));

			sb.append("\"");
		}

		if (objectEntryFolder.getScopeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scopeId\": ");

			sb.append(objectEntryFolder.getScopeId());
		}

		if (objectEntryFolder.getScopeKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scopeKey\": ");

			sb.append("\"");

			sb.append(_escape(objectEntryFolder.getScopeKey()));

			sb.append("\"");
		}

		if (objectEntryFolder.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(String.valueOf(objectEntryFolder.getStatus()));
		}

		if (objectEntryFolder.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(objectEntryFolder.getTitle()));

			sb.append("\"");
		}

		if (objectEntryFolder.getViewableBy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewableBy\": ");

			sb.append("\"");

			sb.append(objectEntryFolder.getViewableBy());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectEntryFolderJSONParser objectEntryFolderJSONParser =
			new ObjectEntryFolderJSONParser();

		return objectEntryFolderJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ObjectEntryFolder objectEntryFolder) {

		if (objectEntryFolder == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (objectEntryFolder.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(objectEntryFolder.getActions()));
		}

		if (objectEntryFolder.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(objectEntryFolder.getCreator()));
		}

		if (objectEntryFolder.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					objectEntryFolder.getDateCreated()));
		}

		if (objectEntryFolder.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					objectEntryFolder.getDateModified()));
		}

		if (objectEntryFolder.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(objectEntryFolder.getDescription()));
		}

		if (objectEntryFolder.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(objectEntryFolder.getExternalReferenceCode()));
		}

		if (objectEntryFolder.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(objectEntryFolder.getId()));
		}

		if (objectEntryFolder.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(objectEntryFolder.getLabel()));
		}

		if (objectEntryFolder.getLabel_i18n() == null) {
			map.put("label_i18n", null);
		}
		else {
			map.put(
				"label_i18n",
				String.valueOf(objectEntryFolder.getLabel_i18n()));
		}

		if (objectEntryFolder.getNumberOfObjectEntries() == null) {
			map.put("numberOfObjectEntries", null);
		}
		else {
			map.put(
				"numberOfObjectEntries",
				String.valueOf(objectEntryFolder.getNumberOfObjectEntries()));
		}

		if (objectEntryFolder.getNumberOfObjectEntryFolders() == null) {
			map.put("numberOfObjectEntryFolders", null);
		}
		else {
			map.put(
				"numberOfObjectEntryFolders",
				String.valueOf(
					objectEntryFolder.getNumberOfObjectEntryFolders()));
		}

		if (objectEntryFolder.getParentObjectEntryFolderBrief() == null) {
			map.put("parentObjectEntryFolderBrief", null);
		}
		else {
			map.put(
				"parentObjectEntryFolderBrief",
				String.valueOf(
					objectEntryFolder.getParentObjectEntryFolderBrief()));
		}

		if (objectEntryFolder.
				getParentObjectEntryFolderExternalReferenceCode() == null) {

			map.put("parentObjectEntryFolderExternalReferenceCode", null);
		}
		else {
			map.put(
				"parentObjectEntryFolderExternalReferenceCode",
				String.valueOf(
					objectEntryFolder.
						getParentObjectEntryFolderExternalReferenceCode()));
		}

		if (objectEntryFolder.getParentObjectEntryFolderId() == null) {
			map.put("parentObjectEntryFolderId", null);
		}
		else {
			map.put(
				"parentObjectEntryFolderId",
				String.valueOf(
					objectEntryFolder.getParentObjectEntryFolderId()));
		}

		if (objectEntryFolder.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(objectEntryFolder.getPermissions()));
		}

		if (objectEntryFolder.getRemovedBy() == null) {
			map.put("removedBy", null);
		}
		else {
			map.put(
				"removedBy", String.valueOf(objectEntryFolder.getRemovedBy()));
		}

		if (objectEntryFolder.getRemovedDate() == null) {
			map.put("removedDate", null);
		}
		else {
			map.put(
				"removedDate",
				liferayToJSONDateFormat.format(
					objectEntryFolder.getRemovedDate()));
		}

		if (objectEntryFolder.getScopeId() == null) {
			map.put("scopeId", null);
		}
		else {
			map.put("scopeId", String.valueOf(objectEntryFolder.getScopeId()));
		}

		if (objectEntryFolder.getScopeKey() == null) {
			map.put("scopeKey", null);
		}
		else {
			map.put(
				"scopeKey", String.valueOf(objectEntryFolder.getScopeKey()));
		}

		if (objectEntryFolder.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(objectEntryFolder.getStatus()));
		}

		if (objectEntryFolder.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(objectEntryFolder.getTitle()));
		}

		if (objectEntryFolder.getViewableBy() == null) {
			map.put("viewableBy", null);
		}
		else {
			map.put(
				"viewableBy",
				String.valueOf(objectEntryFolder.getViewableBy()));
		}

		return map;
	}

	public static class ObjectEntryFolderJSONParser
		extends BaseJSONParser<ObjectEntryFolder> {

		@Override
		protected ObjectEntryFolder createDTO() {
			return new ObjectEntryFolder();
		}

		@Override
		protected ObjectEntryFolder[] createDTOArray(int size) {
			return new ObjectEntryFolder[size];
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
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label_i18n")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfObjectEntries")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfObjectEntryFolders")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentObjectEntryFolderBrief")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentObjectEntryFolderExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentObjectEntryFolderId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "removedBy")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "removedDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scopeId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scopeKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectEntryFolder objectEntryFolder, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					objectEntryFolder.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					objectEntryFolder.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					objectEntryFolder.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					objectEntryFolder.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					objectEntryFolder.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					objectEntryFolder.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					objectEntryFolder.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					objectEntryFolder.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label_i18n")) {
				if (jsonParserFieldValue != null) {
					objectEntryFolder.setLabel_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfObjectEntries")) {

				if (jsonParserFieldValue != null) {
					objectEntryFolder.setNumberOfObjectEntries(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfObjectEntryFolders")) {

				if (jsonParserFieldValue != null) {
					objectEntryFolder.setNumberOfObjectEntryFolders(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentObjectEntryFolderBrief")) {

				if (jsonParserFieldValue != null) {
					objectEntryFolder.setParentObjectEntryFolderBrief(
						ParentObjectEntryFolderBriefSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentObjectEntryFolderExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					objectEntryFolder.
						setParentObjectEntryFolderExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentObjectEntryFolderId")) {

				if (jsonParserFieldValue != null) {
					objectEntryFolder.setParentObjectEntryFolderId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.object.client.permission.Permission[]
						permissionsArray = new
						com.liferay.headless.object.client.permission.Permission
							[jsonParserFieldValues.length];

					for (int i = 0; i < permissionsArray.length; i++) {
						permissionsArray[i] =
							com.liferay.headless.object.client.permission.
								Permission.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					objectEntryFolder.setPermissions(permissionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "removedBy")) {
				if (jsonParserFieldValue != null) {
					objectEntryFolder.setRemovedBy(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "removedDate")) {
				if (jsonParserFieldValue != null) {
					objectEntryFolder.setRemovedDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scopeId")) {
				if (jsonParserFieldValue != null) {
					objectEntryFolder.setScopeId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scopeKey")) {
				if (jsonParserFieldValue != null) {
					objectEntryFolder.setScopeKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					objectEntryFolder.setStatus(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					objectEntryFolder.setTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				if (jsonParserFieldValue != null) {
					objectEntryFolder.setViewableBy(
						ObjectEntryFolder.ViewableBy.create(
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