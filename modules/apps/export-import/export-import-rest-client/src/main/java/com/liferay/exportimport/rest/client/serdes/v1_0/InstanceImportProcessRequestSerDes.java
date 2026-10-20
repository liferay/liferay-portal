/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.InstanceImportProcessRequest;
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
public class InstanceImportProcessRequestSerDes {

	public static InstanceImportProcessRequest toDTO(String json) {
		InstanceImportProcessRequestJSONParser
			instanceImportProcessRequestJSONParser =
				new InstanceImportProcessRequestJSONParser();

		return instanceImportProcessRequestJSONParser.parseToDTO(json);
	}

	public static InstanceImportProcessRequest[] toDTOs(String json) {
		InstanceImportProcessRequestJSONParser
			instanceImportProcessRequestJSONParser =
				new InstanceImportProcessRequestJSONParser();

		return instanceImportProcessRequestJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		InstanceImportProcessRequest instanceImportProcessRequest) {

		if (instanceImportProcessRequest == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (instanceImportProcessRequest.getComments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comments\": ");

			sb.append(instanceImportProcessRequest.getComments());
		}

		if (instanceImportProcessRequest.getDataStrategy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataStrategy\": ");

			sb.append("\"");
			sb.append(instanceImportProcessRequest.getDataStrategy());
			sb.append("\"");
		}

		if (instanceImportProcessRequest.getDeletions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletions\": ");

			sb.append(instanceImportProcessRequest.getDeletions());
		}

		if (instanceImportProcessRequest.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(instanceImportProcessRequest.getName()));

			sb.append("\"");
		}

		if (instanceImportProcessRequest.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append(instanceImportProcessRequest.getPermissions());
		}

		if (instanceImportProcessRequest.getPortletDataHandlers() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"portletDataHandlers\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 instanceImportProcessRequest.
						 getPortletDataHandlers().length;
				 i++) {

				sb.append(
					String.valueOf(
						instanceImportProcessRequest.getPortletDataHandlers()
							[i]));

				if ((i + 1) < instanceImportProcessRequest.
						getPortletDataHandlers().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (instanceImportProcessRequest.getRatings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratings\": ");

			sb.append(instanceImportProcessRequest.getRatings());
		}

		if (instanceImportProcessRequest.getSites() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sites\": ");

			sb.append("[");

			for (int i = 0; i < instanceImportProcessRequest.getSites().length;
				 i++) {

				sb.append(
					String.valueOf(instanceImportProcessRequest.getSites()[i]));

				if ((i + 1) < instanceImportProcessRequest.getSites().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (instanceImportProcessRequest.getUserIdStrategy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userIdStrategy\": ");

			sb.append("\"");
			sb.append(instanceImportProcessRequest.getUserIdStrategy());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		InstanceImportProcessRequestJSONParser
			instanceImportProcessRequestJSONParser =
				new InstanceImportProcessRequestJSONParser();

		return instanceImportProcessRequestJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		InstanceImportProcessRequest instanceImportProcessRequest) {

		if (instanceImportProcessRequest == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (instanceImportProcessRequest.getComments() == null) {
			map.put("comments", null);
		}
		else {
			map.put(
				"comments",
				String.valueOf(instanceImportProcessRequest.getComments()));
		}

		if (instanceImportProcessRequest.getDataStrategy() == null) {
			map.put("dataStrategy", null);
		}
		else {
			map.put(
				"dataStrategy",
				String.valueOf(instanceImportProcessRequest.getDataStrategy()));
		}

		if (instanceImportProcessRequest.getDeletions() == null) {
			map.put("deletions", null);
		}
		else {
			map.put(
				"deletions",
				String.valueOf(instanceImportProcessRequest.getDeletions()));
		}

		if (instanceImportProcessRequest.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(instanceImportProcessRequest.getName()));
		}

		if (instanceImportProcessRequest.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(instanceImportProcessRequest.getPermissions()));
		}

		if (instanceImportProcessRequest.getPortletDataHandlers() == null) {
			map.put("portletDataHandlers", null);
		}
		else {
			map.put(
				"portletDataHandlers",
				String.valueOf(
					instanceImportProcessRequest.getPortletDataHandlers()));
		}

		if (instanceImportProcessRequest.getRatings() == null) {
			map.put("ratings", null);
		}
		else {
			map.put(
				"ratings",
				String.valueOf(instanceImportProcessRequest.getRatings()));
		}

		if (instanceImportProcessRequest.getSites() == null) {
			map.put("sites", null);
		}
		else {
			map.put(
				"sites",
				String.valueOf(instanceImportProcessRequest.getSites()));
		}

		if (instanceImportProcessRequest.getUserIdStrategy() == null) {
			map.put("userIdStrategy", null);
		}
		else {
			map.put(
				"userIdStrategy",
				String.valueOf(
					instanceImportProcessRequest.getUserIdStrategy()));
		}

		return map;
	}

	public static class InstanceImportProcessRequestJSONParser
		extends BaseJSONParser<InstanceImportProcessRequest> {

		@Override
		protected InstanceImportProcessRequest createDTO() {
			return new InstanceImportProcessRequest();
		}

		@Override
		protected InstanceImportProcessRequest[] createDTOArray(int size) {
			return new InstanceImportProcessRequest[size];
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
			else if (Objects.equals(jsonParserFieldName, "sites")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userIdStrategy")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			InstanceImportProcessRequest instanceImportProcessRequest,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "comments")) {
				if (jsonParserFieldValue != null) {
					instanceImportProcessRequest.setComments(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataStrategy")) {
				if (jsonParserFieldValue != null) {
					instanceImportProcessRequest.setDataStrategy(
						InstanceImportProcessRequest.DataStrategy.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deletions")) {
				if (jsonParserFieldValue != null) {
					instanceImportProcessRequest.setDeletions(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					instanceImportProcessRequest.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					instanceImportProcessRequest.setPermissions(
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

					instanceImportProcessRequest.setPortletDataHandlers(
						portletDataHandlersArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ratings")) {
				if (jsonParserFieldValue != null) {
					instanceImportProcessRequest.setRatings(
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

					instanceImportProcessRequest.setSites(sitesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userIdStrategy")) {
				if (jsonParserFieldValue != null) {
					instanceImportProcessRequest.setUserIdStrategy(
						InstanceImportProcessRequest.UserIdStrategy.create(
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
// LIFERAY-REST-BUILDER-HASH:3081001