/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.site.setting.client.dto.v1_0.AvailabilityEstimate;
import com.liferay.headless.commerce.admin.site.setting.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class AvailabilityEstimateSerDes {

	public static AvailabilityEstimate toDTO(String json) {
		AvailabilityEstimateJSONParser availabilityEstimateJSONParser =
			new AvailabilityEstimateJSONParser();

		return availabilityEstimateJSONParser.parseToDTO(json);
	}

	public static AvailabilityEstimate[] toDTOs(String json) {
		AvailabilityEstimateJSONParser availabilityEstimateJSONParser =
			new AvailabilityEstimateJSONParser();

		return availabilityEstimateJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AvailabilityEstimate availabilityEstimate) {
		if (availabilityEstimate == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (availabilityEstimate.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(availabilityEstimate.getCreator()));
		}

		if (availabilityEstimate.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					availabilityEstimate.getDateCreated()));

			sb.append("\"");
		}

		if (availabilityEstimate.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					availabilityEstimate.getDateModified()));

			sb.append("\"");
		}

		if (availabilityEstimate.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(availabilityEstimate.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (availabilityEstimate.getGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"groupId\": ");

			sb.append(availabilityEstimate.getGroupId());
		}

		if (availabilityEstimate.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(availabilityEstimate.getId());
		}

		if (availabilityEstimate.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < availabilityEstimate.getPermissions().length;
				 i++) {

				sb.append(availabilityEstimate.getPermissions()[i]);

				if ((i + 1) < availabilityEstimate.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (availabilityEstimate.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(availabilityEstimate.getPriority());
		}

		if (availabilityEstimate.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append(_toJSON(availabilityEstimate.getTitle()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AvailabilityEstimateJSONParser availabilityEstimateJSONParser =
			new AvailabilityEstimateJSONParser();

		return availabilityEstimateJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AvailabilityEstimate availabilityEstimate) {

		if (availabilityEstimate == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (availabilityEstimate.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put(
				"creator", String.valueOf(availabilityEstimate.getCreator()));
		}

		if (availabilityEstimate.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					availabilityEstimate.getDateCreated()));
		}

		if (availabilityEstimate.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					availabilityEstimate.getDateModified()));
		}

		if (availabilityEstimate.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					availabilityEstimate.getExternalReferenceCode()));
		}

		if (availabilityEstimate.getGroupId() == null) {
			map.put("groupId", null);
		}
		else {
			map.put(
				"groupId", String.valueOf(availabilityEstimate.getGroupId()));
		}

		if (availabilityEstimate.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(availabilityEstimate.getId()));
		}

		if (availabilityEstimate.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(availabilityEstimate.getPermissions()));
		}

		if (availabilityEstimate.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put(
				"priority", String.valueOf(availabilityEstimate.getPriority()));
		}

		if (availabilityEstimate.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(availabilityEstimate.getTitle()));
		}

		return map;
	}

	public static class AvailabilityEstimateJSONParser
		extends BaseJSONParser<AvailabilityEstimate> {

		@Override
		protected AvailabilityEstimate createDTO() {
			return new AvailabilityEstimate();
		}

		@Override
		protected AvailabilityEstimate[] createDTOArray(int size) {
			return new AvailabilityEstimate[size];
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
			else if (Objects.equals(jsonParserFieldName, "groupId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			AvailabilityEstimate availabilityEstimate,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					availabilityEstimate.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					availabilityEstimate.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					availabilityEstimate.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					availabilityEstimate.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "groupId")) {
				if (jsonParserFieldValue != null) {
					availabilityEstimate.setGroupId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					availabilityEstimate.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.commerce.admin.site.setting.client.
						permission.Permission[] permissionsArray = new
						com.liferay.headless.commerce.admin.site.setting.client.
							permission.Permission[jsonParserFieldValues.length];

					for (int i = 0; i < permissionsArray.length; i++) {
						permissionsArray[i] =
							com.liferay.headless.commerce.admin.site.setting.
								client.permission.Permission.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					availabilityEstimate.setPermissions(permissionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					availabilityEstimate.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					availabilityEstimate.setTitle(
						(Map<String, String>)jsonParserFieldValue);
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:-881096953