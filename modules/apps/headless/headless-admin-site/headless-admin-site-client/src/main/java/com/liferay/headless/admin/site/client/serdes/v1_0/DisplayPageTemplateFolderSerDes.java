/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplateFolder;
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
public class DisplayPageTemplateFolderSerDes {

	public static DisplayPageTemplateFolder toDTO(String json) {
		DisplayPageTemplateFolderJSONParser
			displayPageTemplateFolderJSONParser =
				new DisplayPageTemplateFolderJSONParser();

		return displayPageTemplateFolderJSONParser.parseToDTO(json);
	}

	public static DisplayPageTemplateFolder[] toDTOs(String json) {
		DisplayPageTemplateFolderJSONParser
			displayPageTemplateFolderJSONParser =
				new DisplayPageTemplateFolderJSONParser();

		return displayPageTemplateFolderJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		DisplayPageTemplateFolder displayPageTemplateFolder) {

		if (displayPageTemplateFolder == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (displayPageTemplateFolder.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(displayPageTemplateFolder.getCreator());
		}

		if (displayPageTemplateFolder.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					displayPageTemplateFolder.getDateCreated()));

			sb.append("\"");
		}

		if (displayPageTemplateFolder.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					displayPageTemplateFolder.getDateModified()));

			sb.append("\"");
		}

		if (displayPageTemplateFolder.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(displayPageTemplateFolder.getDescription()));

			sb.append("\"");
		}

		if (displayPageTemplateFolder.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(displayPageTemplateFolder.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (displayPageTemplateFolder.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(displayPageTemplateFolder.getKey()));

			sb.append("\"");
		}

		if (displayPageTemplateFolder.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(displayPageTemplateFolder.getName()));

			sb.append("\"");
		}

		if (displayPageTemplateFolder.getParentDisplayPageTemplateFolder() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentDisplayPageTemplateFolder\": ");

			sb.append(
				String.valueOf(
					displayPageTemplateFolder.
						getParentDisplayPageTemplateFolder()));
		}

		if (displayPageTemplateFolder.
				getParentDisplayPageTemplateFolderExternalReferenceCode() !=
					null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(
				"\"parentDisplayPageTemplateFolderExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					displayPageTemplateFolder.
						getParentDisplayPageTemplateFolderExternalReferenceCode()));

			sb.append("\"");
		}

		if (displayPageTemplateFolder.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0;
				 i < displayPageTemplateFolder.getPermissions().length; i++) {

				sb.append(displayPageTemplateFolder.getPermissions()[i]);

				if ((i + 1) <
						displayPageTemplateFolder.getPermissions().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (displayPageTemplateFolder.getUuid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uuid\": ");

			sb.append("\"");

			sb.append(_escape(displayPageTemplateFolder.getUuid()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DisplayPageTemplateFolderJSONParser
			displayPageTemplateFolderJSONParser =
				new DisplayPageTemplateFolderJSONParser();

		return displayPageTemplateFolderJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DisplayPageTemplateFolder displayPageTemplateFolder) {

		if (displayPageTemplateFolder == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (displayPageTemplateFolder.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put(
				"creator",
				String.valueOf(displayPageTemplateFolder.getCreator()));
		}

		if (displayPageTemplateFolder.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					displayPageTemplateFolder.getDateCreated()));
		}

		if (displayPageTemplateFolder.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					displayPageTemplateFolder.getDateModified()));
		}

		if (displayPageTemplateFolder.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(displayPageTemplateFolder.getDescription()));
		}

		if (displayPageTemplateFolder.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					displayPageTemplateFolder.getExternalReferenceCode()));
		}

		if (displayPageTemplateFolder.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(displayPageTemplateFolder.getKey()));
		}

		if (displayPageTemplateFolder.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(displayPageTemplateFolder.getName()));
		}

		if (displayPageTemplateFolder.getParentDisplayPageTemplateFolder() ==
				null) {

			map.put("parentDisplayPageTemplateFolder", null);
		}
		else {
			map.put(
				"parentDisplayPageTemplateFolder",
				String.valueOf(
					displayPageTemplateFolder.
						getParentDisplayPageTemplateFolder()));
		}

		if (displayPageTemplateFolder.
				getParentDisplayPageTemplateFolderExternalReferenceCode() ==
					null) {

			map.put(
				"parentDisplayPageTemplateFolderExternalReferenceCode", null);
		}
		else {
			map.put(
				"parentDisplayPageTemplateFolderExternalReferenceCode",
				String.valueOf(
					displayPageTemplateFolder.
						getParentDisplayPageTemplateFolderExternalReferenceCode()));
		}

		if (displayPageTemplateFolder.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(displayPageTemplateFolder.getPermissions()));
		}

		if (displayPageTemplateFolder.getUuid() == null) {
			map.put("uuid", null);
		}
		else {
			map.put(
				"uuid", String.valueOf(displayPageTemplateFolder.getUuid()));
		}

		return map;
	}

	public static class DisplayPageTemplateFolderJSONParser
		extends BaseJSONParser<DisplayPageTemplateFolder> {

		@Override
		protected DisplayPageTemplateFolder createDTO() {
			return new DisplayPageTemplateFolder();
		}

		@Override
		protected DisplayPageTemplateFolder[] createDTOArray(int size) {
			return new DisplayPageTemplateFolder[size];
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
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

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
						"parentDisplayPageTemplateFolder")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentDisplayPageTemplateFolderExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DisplayPageTemplateFolder displayPageTemplateFolder,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateFolder.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateFolder.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateFolder.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateFolder.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					displayPageTemplateFolder.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateFolder.setKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateFolder.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentDisplayPageTemplateFolder")) {

				if (jsonParserFieldValue != null) {
					displayPageTemplateFolder.
						setParentDisplayPageTemplateFolder(
							DisplayPageTemplateFolderSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentDisplayPageTemplateFolderExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					displayPageTemplateFolder.
						setParentDisplayPageTemplateFolderExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.admin.site.client.permission.
						Permission[] permissionsArray = new
						com.liferay.headless.admin.site.client.permission.
							Permission[jsonParserFieldValues.length];

					for (int i = 0; i < permissionsArray.length; i++) {
						permissionsArray[i] =
							com.liferay.headless.admin.site.client.permission.
								Permission.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					displayPageTemplateFolder.setPermissions(permissionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateFolder.setUuid(
						(String)jsonParserFieldValue);
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