/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.InstanceExportProcessRequest;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.Site;
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
public class InstanceExportProcessRequestSerDes {

	public static InstanceExportProcessRequest toDTO(String json) {
		InstanceExportProcessRequestJSONParser
			instanceExportProcessRequestJSONParser =
				new InstanceExportProcessRequestJSONParser();

		return instanceExportProcessRequestJSONParser.parseToDTO(json);
	}

	public static InstanceExportProcessRequest[] toDTOs(String json) {
		InstanceExportProcessRequestJSONParser
			instanceExportProcessRequestJSONParser =
				new InstanceExportProcessRequestJSONParser();

		return instanceExportProcessRequestJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		InstanceExportProcessRequest instanceExportProcessRequest) {

		if (instanceExportProcessRequest == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (instanceExportProcessRequest.getComments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comments\": ");

			sb.append(instanceExportProcessRequest.getComments());
		}

		if (instanceExportProcessRequest.getDateFilter() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateFilter\": ");

			sb.append(
				String.valueOf(instanceExportProcessRequest.getDateFilter()));
		}

		if (instanceExportProcessRequest.getDeletions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletions\": ");

			sb.append(instanceExportProcessRequest.getDeletions());
		}

		if (instanceExportProcessRequest.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(instanceExportProcessRequest.getName()));

			sb.append("\"");
		}

		if (instanceExportProcessRequest.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append(instanceExportProcessRequest.getPermissions());
		}

		if (instanceExportProcessRequest.getPortletDataHandlers() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"portletDataHandlers\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 instanceExportProcessRequest.
						 getPortletDataHandlers().length;
				 i++) {

				sb.append(
					String.valueOf(
						instanceExportProcessRequest.getPortletDataHandlers()
							[i]));

				if ((i + 1) < instanceExportProcessRequest.
						getPortletDataHandlers().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (instanceExportProcessRequest.getRatings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratings\": ");

			sb.append(instanceExportProcessRequest.getRatings());
		}

		if (instanceExportProcessRequest.getSites() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sites\": ");

			sb.append("[");

			for (int i = 0; i < instanceExportProcessRequest.getSites().length;
				 i++) {

				sb.append(
					String.valueOf(instanceExportProcessRequest.getSites()[i]));

				if ((i + 1) < instanceExportProcessRequest.getSites().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		InstanceExportProcessRequestJSONParser
			instanceExportProcessRequestJSONParser =
				new InstanceExportProcessRequestJSONParser();

		return instanceExportProcessRequestJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		InstanceExportProcessRequest instanceExportProcessRequest) {

		if (instanceExportProcessRequest == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (instanceExportProcessRequest.getComments() == null) {
			map.put("comments", null);
		}
		else {
			map.put(
				"comments",
				String.valueOf(instanceExportProcessRequest.getComments()));
		}

		if (instanceExportProcessRequest.getDateFilter() == null) {
			map.put("dateFilter", null);
		}
		else {
			map.put(
				"dateFilter",
				String.valueOf(instanceExportProcessRequest.getDateFilter()));
		}

		if (instanceExportProcessRequest.getDeletions() == null) {
			map.put("deletions", null);
		}
		else {
			map.put(
				"deletions",
				String.valueOf(instanceExportProcessRequest.getDeletions()));
		}

		if (instanceExportProcessRequest.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(instanceExportProcessRequest.getName()));
		}

		if (instanceExportProcessRequest.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(instanceExportProcessRequest.getPermissions()));
		}

		if (instanceExportProcessRequest.getPortletDataHandlers() == null) {
			map.put("portletDataHandlers", null);
		}
		else {
			map.put(
				"portletDataHandlers",
				String.valueOf(
					instanceExportProcessRequest.getPortletDataHandlers()));
		}

		if (instanceExportProcessRequest.getRatings() == null) {
			map.put("ratings", null);
		}
		else {
			map.put(
				"ratings",
				String.valueOf(instanceExportProcessRequest.getRatings()));
		}

		if (instanceExportProcessRequest.getSites() == null) {
			map.put("sites", null);
		}
		else {
			map.put(
				"sites",
				String.valueOf(instanceExportProcessRequest.getSites()));
		}

		return map;
	}

	public static class InstanceExportProcessRequestJSONParser
		extends BaseJSONParser<InstanceExportProcessRequest> {

		@Override
		protected InstanceExportProcessRequest createDTO() {
			return new InstanceExportProcessRequest();
		}

		@Override
		protected InstanceExportProcessRequest[] createDTOArray(int size) {
			return new InstanceExportProcessRequest[size];
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
			else if (Objects.equals(jsonParserFieldName, "sites")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			InstanceExportProcessRequest instanceExportProcessRequest,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "comments")) {
				if (jsonParserFieldValue != null) {
					instanceExportProcessRequest.setComments(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateFilter")) {
				if (jsonParserFieldValue != null) {
					instanceExportProcessRequest.setDateFilter(
						DateFilterSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deletions")) {
				if (jsonParserFieldValue != null) {
					instanceExportProcessRequest.setDeletions(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					instanceExportProcessRequest.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					instanceExportProcessRequest.setPermissions(
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

					instanceExportProcessRequest.setPortletDataHandlers(
						portletDataHandlersArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ratings")) {
				if (jsonParserFieldValue != null) {
					instanceExportProcessRequest.setRatings(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sites")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Site[] sitesArray = new Site[jsonParserFieldValues.length];

					for (int i = 0; i < sitesArray.length; i++) {
						sitesArray[i] = SiteSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					instanceExportProcessRequest.setSites(sitesArray);
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
// LIFERAY-REST-BUILDER-HASH:-694412064