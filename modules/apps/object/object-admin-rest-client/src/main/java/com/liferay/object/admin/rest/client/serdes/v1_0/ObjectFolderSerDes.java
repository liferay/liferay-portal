/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.serdes.v1_0;

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectFolder;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectFolderItem;
import com.liferay.object.admin.rest.client.json.BaseJSONParser;

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
public class ObjectFolderSerDes {

	public static ObjectFolder toDTO(String json) {
		ObjectFolderJSONParser objectFolderJSONParser =
			new ObjectFolderJSONParser();

		return objectFolderJSONParser.parseToDTO(json);
	}

	public static ObjectFolder[] toDTOs(String json) {
		ObjectFolderJSONParser objectFolderJSONParser =
			new ObjectFolderJSONParser();

		return objectFolderJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectFolder objectFolder) {
		if (objectFolder == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (objectFolder.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(objectFolder.getActions()));
		}

		if (objectFolder.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(objectFolder.getDateCreated()));

			sb.append("\"");
		}

		if (objectFolder.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(objectFolder.getDateModified()));

			sb.append("\"");
		}

		if (objectFolder.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(objectFolder.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (objectFolder.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(objectFolder.getId());
		}

		if (objectFolder.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append(_toJSON(objectFolder.getLabel()));
		}

		if (objectFolder.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(objectFolder.getName()));

			sb.append("\"");
		}

		if (objectFolder.getObjectFolderItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectFolderItems\": ");

			sb.append("[");

			for (int i = 0; i < objectFolder.getObjectFolderItems().length;
				 i++) {

				sb.append(
					String.valueOf(objectFolder.getObjectFolderItems()[i]));

				if ((i + 1) < objectFolder.getObjectFolderItems().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectFolderJSONParser objectFolderJSONParser =
			new ObjectFolderJSONParser();

		return objectFolderJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ObjectFolder objectFolder) {
		if (objectFolder == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (objectFolder.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(objectFolder.getActions()));
		}

		if (objectFolder.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(objectFolder.getDateCreated()));
		}

		if (objectFolder.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(objectFolder.getDateModified()));
		}

		if (objectFolder.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(objectFolder.getExternalReferenceCode()));
		}

		if (objectFolder.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(objectFolder.getId()));
		}

		if (objectFolder.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(objectFolder.getLabel()));
		}

		if (objectFolder.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(objectFolder.getName()));
		}

		if (objectFolder.getObjectFolderItems() == null) {
			map.put("objectFolderItems", null);
		}
		else {
			map.put(
				"objectFolderItems",
				String.valueOf(objectFolder.getObjectFolderItems()));
		}

		return map;
	}

	public static class ObjectFolderJSONParser
		extends BaseJSONParser<ObjectFolder> {

		@Override
		protected ObjectFolder createDTO() {
			return new ObjectFolder();
		}

		@Override
		protected ObjectFolder[] createDTOArray(int size) {
			return new ObjectFolder[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
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
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "objectFolderItems")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectFolder objectFolder, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					objectFolder.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					objectFolder.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					objectFolder.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					objectFolder.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					objectFolder.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					objectFolder.setLabel(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					objectFolder.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "objectFolderItems")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ObjectFolderItem[] objectFolderItemsArray =
						new ObjectFolderItem[jsonParserFieldValues.length];

					for (int i = 0; i < objectFolderItemsArray.length; i++) {
						objectFolderItemsArray[i] =
							ObjectFolderItemSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					objectFolder.setObjectFolderItems(objectFolderItemsArray);
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