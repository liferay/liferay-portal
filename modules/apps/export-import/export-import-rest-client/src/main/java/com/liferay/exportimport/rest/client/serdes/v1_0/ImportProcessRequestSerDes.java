/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.ImportProcessRequest;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class ImportProcessRequestSerDes {

	public static ImportProcessRequest toDTO(String json) {
		ImportProcessRequestJSONParser importProcessRequestJSONParser =
			new ImportProcessRequestJSONParser();

		return importProcessRequestJSONParser.parseToDTO(json);
	}

	public static ImportProcessRequest[] toDTOs(String json) {
		ImportProcessRequestJSONParser importProcessRequestJSONParser =
			new ImportProcessRequestJSONParser();

		return importProcessRequestJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ImportProcessRequest importProcessRequest) {
		if (importProcessRequest == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (importProcessRequest.getComments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comments\": ");

			sb.append(importProcessRequest.getComments());
		}

		if (importProcessRequest.getDataStrategy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataStrategy\": ");

			sb.append("\"");
			sb.append(importProcessRequest.getDataStrategy());
			sb.append("\"");
		}

		if (importProcessRequest.getDeletions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletions\": ");

			sb.append(importProcessRequest.getDeletions());
		}

		if (importProcessRequest.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(importProcessRequest.getName()));

			sb.append("\"");
		}

		if (importProcessRequest.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append(importProcessRequest.getPermissions());
		}

		if (importProcessRequest.getPortletDataHandlers() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"portletDataHandlers\": ");

			sb.append("[");

			for (int i = 0;
				 i < importProcessRequest.getPortletDataHandlers().length;
				 i++) {

				sb.append(
					String.valueOf(
						importProcessRequest.getPortletDataHandlers()[i]));

				if ((i + 1) <
						importProcessRequest.getPortletDataHandlers().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (importProcessRequest.getRatings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratings\": ");

			sb.append(importProcessRequest.getRatings());
		}

		if (importProcessRequest.getUserIdStrategy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userIdStrategy\": ");

			sb.append("\"");
			sb.append(importProcessRequest.getUserIdStrategy());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ImportProcessRequestJSONParser importProcessRequestJSONParser =
			new ImportProcessRequestJSONParser();

		return importProcessRequestJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ImportProcessRequest importProcessRequest) {

		if (importProcessRequest == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (importProcessRequest.getComments() == null) {
			map.put("comments", null);
		}
		else {
			map.put(
				"comments", String.valueOf(importProcessRequest.getComments()));
		}

		if (importProcessRequest.getDataStrategy() == null) {
			map.put("dataStrategy", null);
		}
		else {
			map.put(
				"dataStrategy",
				String.valueOf(importProcessRequest.getDataStrategy()));
		}

		if (importProcessRequest.getDeletions() == null) {
			map.put("deletions", null);
		}
		else {
			map.put(
				"deletions",
				String.valueOf(importProcessRequest.getDeletions()));
		}

		if (importProcessRequest.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(importProcessRequest.getName()));
		}

		if (importProcessRequest.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(importProcessRequest.getPermissions()));
		}

		if (importProcessRequest.getPortletDataHandlers() == null) {
			map.put("portletDataHandlers", null);
		}
		else {
			map.put(
				"portletDataHandlers",
				String.valueOf(importProcessRequest.getPortletDataHandlers()));
		}

		if (importProcessRequest.getRatings() == null) {
			map.put("ratings", null);
		}
		else {
			map.put(
				"ratings", String.valueOf(importProcessRequest.getRatings()));
		}

		if (importProcessRequest.getUserIdStrategy() == null) {
			map.put("userIdStrategy", null);
		}
		else {
			map.put(
				"userIdStrategy",
				String.valueOf(importProcessRequest.getUserIdStrategy()));
		}

		return map;
	}

	public static class ImportProcessRequestJSONParser
		extends BaseJSONParser<ImportProcessRequest> {

		@Override
		protected ImportProcessRequest createDTO() {
			return new ImportProcessRequest();
		}

		@Override
		protected ImportProcessRequest[] createDTOArray(int size) {
			return new ImportProcessRequest[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "comments")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dataStrategy")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "deletions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "portletDataHandlers")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "ratings")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userIdStrategy")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ImportProcessRequest importProcessRequest,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "comments")) {
				if (jsonParserFieldValue != null) {
					importProcessRequest.setComments(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataStrategy")) {
				if (jsonParserFieldValue != null) {
					importProcessRequest.setDataStrategy(
						ImportProcessRequest.DataStrategy.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deletions")) {
				if (jsonParserFieldValue != null) {
					importProcessRequest.setDeletions(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					importProcessRequest.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					importProcessRequest.setPermissions(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "portletDataHandlers")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PortletDataHandler[] portletDataHandlersArray =
						new PortletDataHandler[jsonParserFieldValues.length];

					for (int i = 0; i < portletDataHandlersArray.length; i++) {
						portletDataHandlersArray[i] =
							PortletDataHandlerSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					importProcessRequest.setPortletDataHandlers(
						portletDataHandlersArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ratings")) {
				if (jsonParserFieldValue != null) {
					importProcessRequest.setRatings(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userIdStrategy")) {
				if (jsonParserFieldValue != null) {
					importProcessRequest.setUserIdStrategy(
						ImportProcessRequest.UserIdStrategy.create(
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
// LIFERAY-REST-BUILDER-HASH:-1822998027