/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.Meta;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class MetaSerDes {

	public static Meta toDTO(String json) {
		MetaJSONParser metaJSONParser = new MetaJSONParser();

		return metaJSONParser.parseToDTO(json);
	}

	public static Meta[] toDTOs(String json) {
		MetaJSONParser metaJSONParser = new MetaJSONParser();

		return metaJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Meta meta) {
		if (meta == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (meta.getCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"created\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(meta.getCreated()));

			sb.append("\"");
		}

		if (meta.getLastModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(meta.getLastModified()));

			sb.append("\"");
		}

		if (meta.getLocation() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"location\": ");

			sb.append("\"");

			sb.append(_escape(meta.getLocation()));

			sb.append("\"");
		}

		if (meta.getResourceType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"resourceType\": ");

			sb.append("\"");

			sb.append(_escape(meta.getResourceType()));

			sb.append("\"");
		}

		if (meta.getVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"version\": ");

			sb.append("\"");

			sb.append(_escape(meta.getVersion()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MetaJSONParser metaJSONParser = new MetaJSONParser();

		return metaJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Meta meta) {
		if (meta == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (meta.getCreated() == null) {
			map.put("created", null);
		}
		else {
			map.put(
				"created", liferayToJSONDateFormat.format(meta.getCreated()));
		}

		if (meta.getLastModified() == null) {
			map.put("lastModified", null);
		}
		else {
			map.put(
				"lastModified",
				liferayToJSONDateFormat.format(meta.getLastModified()));
		}

		if (meta.getLocation() == null) {
			map.put("location", null);
		}
		else {
			map.put("location", String.valueOf(meta.getLocation()));
		}

		if (meta.getResourceType() == null) {
			map.put("resourceType", null);
		}
		else {
			map.put("resourceType", String.valueOf(meta.getResourceType()));
		}

		if (meta.getVersion() == null) {
			map.put("version", null);
		}
		else {
			map.put("version", String.valueOf(meta.getVersion()));
		}

		return map;
	}

	public static class MetaJSONParser extends BaseJSONParser<Meta> {

		@Override
		protected Meta createDTO() {
			return new Meta();
		}

		@Override
		protected Meta[] createDTOArray(int size) {
			return new Meta[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "created")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lastModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "location")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "resourceType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Meta meta, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "created")) {
				if (jsonParserFieldValue != null) {
					meta.setCreated(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lastModified")) {
				if (jsonParserFieldValue != null) {
					meta.setLastModified(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "location")) {
				if (jsonParserFieldValue != null) {
					meta.setLocation((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "resourceType")) {
				if (jsonParserFieldValue != null) {
					meta.setResourceType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				if (jsonParserFieldValue != null) {
					meta.setVersion((String)jsonParserFieldValue);
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