/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.serdes.v1_0;

import com.liferay.headless.admin.fragment.client.dto.v1_0.ResourceFile;
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
public class ResourceFileSerDes {

	public static ResourceFile toDTO(String json) {
		ResourceFileJSONParser resourceFileJSONParser =
			new ResourceFileJSONParser();

		return resourceFileJSONParser.parseToDTO(json);
	}

	public static ResourceFile[] toDTOs(String json) {
		ResourceFileJSONParser resourceFileJSONParser =
			new ResourceFileJSONParser();

		return resourceFileJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ResourceFile resourceFile) {
		if (resourceFile == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (resourceFile.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(resourceFile.getCreator());
		}

		if (resourceFile.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(resourceFile.getDateCreated()));

			sb.append("\"");
		}

		if (resourceFile.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(resourceFile.getDateModified()));

			sb.append("\"");
		}

		if (resourceFile.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(resourceFile.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (resourceFile.getFileURLReference() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileURLReference\": ");

			sb.append(String.valueOf(resourceFile.getFileURLReference()));
		}

		if (resourceFile.getFragmentSet() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentSet\": ");

			sb.append(String.valueOf(resourceFile.getFragmentSet()));
		}

		if (resourceFile.getFragmentSetExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentSetExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(resourceFile.getFragmentSetExternalReferenceCode()));

			sb.append("\"");
		}

		if (resourceFile.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(resourceFile.getName()));

			sb.append("\"");
		}

		if (resourceFile.getResourceFolder() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"resourceFolder\": ");

			sb.append(String.valueOf(resourceFile.getResourceFolder()));
		}

		if (resourceFile.getResourceFolderExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"resourceFolderExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(resourceFile.getResourceFolderExternalReferenceCode()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ResourceFileJSONParser resourceFileJSONParser =
			new ResourceFileJSONParser();

		return resourceFileJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ResourceFile resourceFile) {
		if (resourceFile == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (resourceFile.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(resourceFile.getCreator()));
		}

		if (resourceFile.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(resourceFile.getDateCreated()));
		}

		if (resourceFile.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(resourceFile.getDateModified()));
		}

		if (resourceFile.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(resourceFile.getExternalReferenceCode()));
		}

		if (resourceFile.getFileURLReference() == null) {
			map.put("fileURLReference", null);
		}
		else {
			map.put(
				"fileURLReference",
				String.valueOf(resourceFile.getFileURLReference()));
		}

		if (resourceFile.getFragmentSet() == null) {
			map.put("fragmentSet", null);
		}
		else {
			map.put(
				"fragmentSet", String.valueOf(resourceFile.getFragmentSet()));
		}

		if (resourceFile.getFragmentSetExternalReferenceCode() == null) {
			map.put("fragmentSetExternalReferenceCode", null);
		}
		else {
			map.put(
				"fragmentSetExternalReferenceCode",
				String.valueOf(
					resourceFile.getFragmentSetExternalReferenceCode()));
		}

		if (resourceFile.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(resourceFile.getName()));
		}

		if (resourceFile.getResourceFolder() == null) {
			map.put("resourceFolder", null);
		}
		else {
			map.put(
				"resourceFolder",
				String.valueOf(resourceFile.getResourceFolder()));
		}

		if (resourceFile.getResourceFolderExternalReferenceCode() == null) {
			map.put("resourceFolderExternalReferenceCode", null);
		}
		else {
			map.put(
				"resourceFolderExternalReferenceCode",
				String.valueOf(
					resourceFile.getResourceFolderExternalReferenceCode()));
		}

		return map;
	}

	public static class ResourceFileJSONParser
		extends BaseJSONParser<ResourceFile> {

		@Override
		protected ResourceFile createDTO() {
			return new ResourceFile();
		}

		@Override
		protected ResourceFile[] createDTOArray(int size) {
			return new ResourceFile[size];
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
			else if (Objects.equals(jsonParserFieldName, "fileURLReference")) {
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
			else if (Objects.equals(jsonParserFieldName, "resourceFolder")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"resourceFolderExternalReferenceCode")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ResourceFile resourceFile, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					resourceFile.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					resourceFile.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					resourceFile.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					resourceFile.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fileURLReference")) {
				if (jsonParserFieldValue != null) {
					resourceFile.setFileURLReference(
						FileURLReferenceSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentSet")) {
				if (jsonParserFieldValue != null) {
					resourceFile.setFragmentSet(
						FragmentSetSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"fragmentSetExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					resourceFile.setFragmentSetExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					resourceFile.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "resourceFolder")) {
				if (jsonParserFieldValue != null) {
					resourceFile.setResourceFolder(
						ResourceFolderSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"resourceFolderExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					resourceFile.setResourceFolderExternalReferenceCode(
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
// LIFERAY-REST-BUILDER-HASH:-1985227709