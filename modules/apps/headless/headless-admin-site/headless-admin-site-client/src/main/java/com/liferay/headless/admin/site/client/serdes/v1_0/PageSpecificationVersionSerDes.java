/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecificationVersion;
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
public class PageSpecificationVersionSerDes {

	public static PageSpecificationVersion toDTO(String json) {
		PageSpecificationVersionJSONParser pageSpecificationVersionJSONParser =
			new PageSpecificationVersionJSONParser();

		return pageSpecificationVersionJSONParser.parseToDTO(json);
	}

	public static PageSpecificationVersion[] toDTOs(String json) {
		PageSpecificationVersionJSONParser pageSpecificationVersionJSONParser =
			new PageSpecificationVersionJSONParser();

		return pageSpecificationVersionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PageSpecificationVersion pageSpecificationVersion) {

		if (pageSpecificationVersion == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (pageSpecificationVersion.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(pageSpecificationVersion.getCreator());
		}

		if (pageSpecificationVersion.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					pageSpecificationVersion.getDateCreated()));

			sb.append("\"");
		}

		if (pageSpecificationVersion.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					pageSpecificationVersion.getDateModified()));

			sb.append("\"");
		}

		if (pageSpecificationVersion.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(pageSpecificationVersion.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (pageSpecificationVersion.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(pageSpecificationVersion.getName()));

			sb.append("\"");
		}

		if (pageSpecificationVersion.getPageSpecification() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSpecification\": ");

			sb.append(
				String.valueOf(
					pageSpecificationVersion.getPageSpecification()));
		}

		if (pageSpecificationVersion.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");
			sb.append(pageSpecificationVersion.getStatus());
			sb.append("\"");
		}

		if (pageSpecificationVersion.getStatusDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"statusDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					pageSpecificationVersion.getStatusDate()));

			sb.append("\"");
		}

		if (pageSpecificationVersion.getVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"version\": ");

			sb.append(pageSpecificationVersion.getVersion());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageSpecificationVersionJSONParser pageSpecificationVersionJSONParser =
			new PageSpecificationVersionJSONParser();

		return pageSpecificationVersionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PageSpecificationVersion pageSpecificationVersion) {

		if (pageSpecificationVersion == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (pageSpecificationVersion.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put(
				"creator",
				String.valueOf(pageSpecificationVersion.getCreator()));
		}

		if (pageSpecificationVersion.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					pageSpecificationVersion.getDateCreated()));
		}

		if (pageSpecificationVersion.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					pageSpecificationVersion.getDateModified()));
		}

		if (pageSpecificationVersion.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					pageSpecificationVersion.getExternalReferenceCode()));
		}

		if (pageSpecificationVersion.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(pageSpecificationVersion.getName()));
		}

		if (pageSpecificationVersion.getPageSpecification() == null) {
			map.put("pageSpecification", null);
		}
		else {
			map.put(
				"pageSpecification",
				String.valueOf(
					pageSpecificationVersion.getPageSpecification()));
		}

		if (pageSpecificationVersion.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put(
				"status", String.valueOf(pageSpecificationVersion.getStatus()));
		}

		if (pageSpecificationVersion.getStatusDate() == null) {
			map.put("statusDate", null);
		}
		else {
			map.put(
				"statusDate",
				liferayToJSONDateFormat.format(
					pageSpecificationVersion.getStatusDate()));
		}

		if (pageSpecificationVersion.getVersion() == null) {
			map.put("version", null);
		}
		else {
			map.put(
				"version",
				String.valueOf(pageSpecificationVersion.getVersion()));
		}

		return map;
	}

	public static class PageSpecificationVersionJSONParser
		extends BaseJSONParser<PageSpecificationVersion> {

		@Override
		protected PageSpecificationVersion createDTO() {
			return new PageSpecificationVersion();
		}

		@Override
		protected PageSpecificationVersion[] createDTOArray(int size) {
			return new PageSpecificationVersion[size];
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
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageSpecification")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "statusDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageSpecificationVersion pageSpecificationVersion,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					pageSpecificationVersion.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					pageSpecificationVersion.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					pageSpecificationVersion.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					pageSpecificationVersion.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					pageSpecificationVersion.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageSpecification")) {
				if (jsonParserFieldValue != null) {
					pageSpecificationVersion.setPageSpecification(
						PageSpecificationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					pageSpecificationVersion.setStatus(
						PageSpecificationVersion.Status.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "statusDate")) {
				if (jsonParserFieldValue != null) {
					pageSpecificationVersion.setStatusDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				if (jsonParserFieldValue != null) {
					pageSpecificationVersion.setVersion(
						Integer.valueOf((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:759938507