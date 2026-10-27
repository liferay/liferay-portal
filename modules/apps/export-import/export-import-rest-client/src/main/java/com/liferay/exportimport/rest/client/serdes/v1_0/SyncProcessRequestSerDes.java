/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.SyncProcessRequest;
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
public class SyncProcessRequestSerDes {

	public static SyncProcessRequest toDTO(String json) {
		SyncProcessRequestJSONParser syncProcessRequestJSONParser =
			new SyncProcessRequestJSONParser();

		return syncProcessRequestJSONParser.parseToDTO(json);
	}

	public static SyncProcessRequest[] toDTOs(String json) {
		SyncProcessRequestJSONParser syncProcessRequestJSONParser =
			new SyncProcessRequestJSONParser();

		return syncProcessRequestJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SyncProcessRequest syncProcessRequest) {
		if (syncProcessRequest == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (syncProcessRequest.getComments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comments\": ");

			sb.append(syncProcessRequest.getComments());
		}

		if (syncProcessRequest.getPortletDataHandlers() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"portletDataHandlers\": ");

			sb.append("[");

			for (int i = 0;
				 i < syncProcessRequest.getPortletDataHandlers().length; i++) {

				sb.append(
					String.valueOf(
						syncProcessRequest.getPortletDataHandlers()[i]));

				if ((i + 1) <
						syncProcessRequest.getPortletDataHandlers().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (syncProcessRequest.getRatings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratings\": ");

			sb.append(syncProcessRequest.getRatings());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SyncProcessRequestJSONParser syncProcessRequestJSONParser =
			new SyncProcessRequestJSONParser();

		return syncProcessRequestJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SyncProcessRequest syncProcessRequest) {

		if (syncProcessRequest == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (syncProcessRequest.getComments() == null) {
			map.put("comments", null);
		}
		else {
			map.put(
				"comments", String.valueOf(syncProcessRequest.getComments()));
		}

		if (syncProcessRequest.getPortletDataHandlers() == null) {
			map.put("portletDataHandlers", null);
		}
		else {
			map.put(
				"portletDataHandlers",
				String.valueOf(syncProcessRequest.getPortletDataHandlers()));
		}

		if (syncProcessRequest.getRatings() == null) {
			map.put("ratings", null);
		}
		else {
			map.put("ratings", String.valueOf(syncProcessRequest.getRatings()));
		}

		return map;
	}

	public static class SyncProcessRequestJSONParser
		extends BaseJSONParser<SyncProcessRequest> {

		@Override
		protected SyncProcessRequest createDTO() {
			return new SyncProcessRequest();
		}

		@Override
		protected SyncProcessRequest[] createDTOArray(int size) {
			return new SyncProcessRequest[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "comments")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "portletDataHandlers")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "ratings")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SyncProcessRequest syncProcessRequest, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "comments")) {
				if (jsonParserFieldValue != null) {
					syncProcessRequest.setComments(
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

					syncProcessRequest.setPortletDataHandlers(
						portletDataHandlersArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ratings")) {
				if (jsonParserFieldValue != null) {
					syncProcessRequest.setRatings(
						(Boolean)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:1276084550