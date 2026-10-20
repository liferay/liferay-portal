/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.ExportProcessRequest;
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
public class ExportProcessRequestSerDes {

	public static ExportProcessRequest toDTO(String json) {
		ExportProcessRequestJSONParser exportProcessRequestJSONParser =
			new ExportProcessRequestJSONParser();

		return exportProcessRequestJSONParser.parseToDTO(json);
	}

	public static ExportProcessRequest[] toDTOs(String json) {
		ExportProcessRequestJSONParser exportProcessRequestJSONParser =
			new ExportProcessRequestJSONParser();

		return exportProcessRequestJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ExportProcessRequest exportProcessRequest) {
		if (exportProcessRequest == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (exportProcessRequest.getComments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comments\": ");

			sb.append(exportProcessRequest.getComments());
		}

		if (exportProcessRequest.getDateFilter() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateFilter\": ");

			sb.append(String.valueOf(exportProcessRequest.getDateFilter()));
		}

		if (exportProcessRequest.getDeletions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletions\": ");

			sb.append(exportProcessRequest.getDeletions());
		}

		if (exportProcessRequest.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(exportProcessRequest.getName()));

			sb.append("\"");
		}

		if (exportProcessRequest.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append(exportProcessRequest.getPermissions());
		}

		if (exportProcessRequest.getPortletDataHandlers() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"portletDataHandlers\": ");

			sb.append("[");

			for (int i = 0;
				 i < exportProcessRequest.getPortletDataHandlers().length;
				 i++) {

				sb.append(
					String.valueOf(
						exportProcessRequest.getPortletDataHandlers()[i]));

				if ((i + 1) <
						exportProcessRequest.getPortletDataHandlers().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (exportProcessRequest.getRatings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratings\": ");

			sb.append(exportProcessRequest.getRatings());
		}

		if (exportProcessRequest.getSiteExternalReferenceCodes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteExternalReferenceCodes\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 exportProcessRequest.
						 getSiteExternalReferenceCodes().length;
				 i++) {

				sb.append(
					_toJSON(
						exportProcessRequest.getSiteExternalReferenceCodes()
							[i]));

				if ((i + 1) < exportProcessRequest.
						getSiteExternalReferenceCodes().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ExportProcessRequestJSONParser exportProcessRequestJSONParser =
			new ExportProcessRequestJSONParser();

		return exportProcessRequestJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ExportProcessRequest exportProcessRequest) {

		if (exportProcessRequest == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (exportProcessRequest.getComments() == null) {
			map.put("comments", null);
		}
		else {
			map.put(
				"comments", String.valueOf(exportProcessRequest.getComments()));
		}

		if (exportProcessRequest.getDateFilter() == null) {
			map.put("dateFilter", null);
		}
		else {
			map.put(
				"dateFilter",
				String.valueOf(exportProcessRequest.getDateFilter()));
		}

		if (exportProcessRequest.getDeletions() == null) {
			map.put("deletions", null);
		}
		else {
			map.put(
				"deletions",
				String.valueOf(exportProcessRequest.getDeletions()));
		}

		if (exportProcessRequest.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(exportProcessRequest.getName()));
		}

		if (exportProcessRequest.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(exportProcessRequest.getPermissions()));
		}

		if (exportProcessRequest.getPortletDataHandlers() == null) {
			map.put("portletDataHandlers", null);
		}
		else {
			map.put(
				"portletDataHandlers",
				String.valueOf(exportProcessRequest.getPortletDataHandlers()));
		}

		if (exportProcessRequest.getRatings() == null) {
			map.put("ratings", null);
		}
		else {
			map.put(
				"ratings", String.valueOf(exportProcessRequest.getRatings()));
		}

		if (exportProcessRequest.getSiteExternalReferenceCodes() == null) {
			map.put("siteExternalReferenceCodes", null);
		}
		else {
			map.put(
				"siteExternalReferenceCodes",
				String.valueOf(
					exportProcessRequest.getSiteExternalReferenceCodes()));
		}

		return map;
	}

	public static class ExportProcessRequestJSONParser
		extends BaseJSONParser<ExportProcessRequest> {

		@Override
		protected ExportProcessRequest createDTO() {
			return new ExportProcessRequest();
		}

		@Override
		protected ExportProcessRequest[] createDTOArray(int size) {
			return new ExportProcessRequest[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "comments")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateFilter")) {
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
			else if (Objects.equals(
						jsonParserFieldName, "siteExternalReferenceCodes")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ExportProcessRequest exportProcessRequest,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "comments")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setComments(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateFilter")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setDateFilter(
						DateFilterSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deletions")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setDeletions(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setPermissions(
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

					exportProcessRequest.setPortletDataHandlers(
						portletDataHandlersArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ratings")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setRatings(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "siteExternalReferenceCodes")) {

				if (jsonParserFieldValue != null) {
					exportProcessRequest.setSiteExternalReferenceCodes(
						toStrings((Object[])jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:1823777578