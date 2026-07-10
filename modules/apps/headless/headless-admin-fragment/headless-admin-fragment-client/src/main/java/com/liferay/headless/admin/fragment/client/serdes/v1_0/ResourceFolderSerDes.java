/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.serdes.v1_0;

import com.liferay.headless.admin.fragment.client.dto.v1_0.ResourceFolder;
import com.liferay.headless.admin.fragment.client.json.BaseJSONParser;

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
public class ResourceFolderSerDes {

	public static ResourceFolder toDTO(String json) {
		ResourceFolderJSONParser resourceFolderJSONParser =
			new ResourceFolderJSONParser();

		return resourceFolderJSONParser.parseToDTO(json);
	}

	public static ResourceFolder[] toDTOs(String json) {
		ResourceFolderJSONParser resourceFolderJSONParser =
			new ResourceFolderJSONParser();

		return resourceFolderJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ResourceFolder resourceFolder) {
		if (resourceFolder == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (resourceFolder.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(resourceFolder.getCreator());
		}

		if (resourceFolder.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					resourceFolder.getDateCreated()));

			sb.append("\"");
		}

		if (resourceFolder.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					resourceFolder.getDateModified()));

			sb.append("\"");
		}

		if (resourceFolder.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(resourceFolder.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (resourceFolder.getFragmentSet() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentSet\": ");

			sb.append(String.valueOf(resourceFolder.getFragmentSet()));
		}

		if (resourceFolder.getFragmentSetExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentSetExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(resourceFolder.getFragmentSetExternalReferenceCode()));

			sb.append("\"");
		}

		if (resourceFolder.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(resourceFolder.getName()));

			sb.append("\"");
		}

		if (resourceFolder.getParentResourceFolder() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentResourceFolder\": ");

			sb.append(String.valueOf(resourceFolder.getParentResourceFolder()));
		}

		if (resourceFolder.getParentResourceFolderExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentResourceFolderExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					resourceFolder.
						getParentResourceFolderExternalReferenceCode()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ResourceFolderJSONParser resourceFolderJSONParser =
			new ResourceFolderJSONParser();

		return resourceFolderJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ResourceFolder resourceFolder) {
		if (resourceFolder == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (resourceFolder.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(resourceFolder.getCreator()));
		}

		if (resourceFolder.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					resourceFolder.getDateCreated()));
		}

		if (resourceFolder.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					resourceFolder.getDateModified()));
		}

		if (resourceFolder.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(resourceFolder.getExternalReferenceCode()));
		}

		if (resourceFolder.getFragmentSet() == null) {
			map.put("fragmentSet", null);
		}
		else {
			map.put(
				"fragmentSet", String.valueOf(resourceFolder.getFragmentSet()));
		}

		if (resourceFolder.getFragmentSetExternalReferenceCode() == null) {
			map.put("fragmentSetExternalReferenceCode", null);
		}
		else {
			map.put(
				"fragmentSetExternalReferenceCode",
				String.valueOf(
					resourceFolder.getFragmentSetExternalReferenceCode()));
		}

		if (resourceFolder.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(resourceFolder.getName()));
		}

		if (resourceFolder.getParentResourceFolder() == null) {
			map.put("parentResourceFolder", null);
		}
		else {
			map.put(
				"parentResourceFolder",
				String.valueOf(resourceFolder.getParentResourceFolder()));
		}

		if (resourceFolder.getParentResourceFolderExternalReferenceCode() ==
				null) {

			map.put("parentResourceFolderExternalReferenceCode", null);
		}
		else {
			map.put(
				"parentResourceFolderExternalReferenceCode",
				String.valueOf(
					resourceFolder.
						getParentResourceFolderExternalReferenceCode()));
		}

		return map;
	}

	public static class ResourceFolderJSONParser
		extends BaseJSONParser<ResourceFolder> {

		@Override
		protected ResourceFolder createDTO() {
			return new ResourceFolder();
		}

		@Override
		protected ResourceFolder[] createDTOArray(int size) {
			return new ResourceFolder[size];
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
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentSet")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"fragmentSetExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentResourceFolder")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentResourceFolderExternalReferenceCode")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ResourceFolder resourceFolder, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					resourceFolder.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					resourceFolder.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					resourceFolder.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					resourceFolder.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentSet")) {
				if (jsonParserFieldValue != null) {
					resourceFolder.setFragmentSet(
						FragmentSetSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"fragmentSetExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					resourceFolder.setFragmentSetExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					resourceFolder.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentResourceFolder")) {

				if (jsonParserFieldValue != null) {
					resourceFolder.setParentResourceFolder(
						ResourceFolderSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentResourceFolderExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					resourceFolder.setParentResourceFolderExternalReferenceCode(
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
// LIFERAY-REST-BUILDER-HASH:1833199742