/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.RequestPortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.RequestPortletDataHandlerControl;
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
public class RequestPortletDataHandlerSerDes {

	public static RequestPortletDataHandler toDTO(String json) {
		RequestPortletDataHandlerJSONParser
			requestPortletDataHandlerJSONParser =
				new RequestPortletDataHandlerJSONParser();

		return requestPortletDataHandlerJSONParser.parseToDTO(json);
	}

	public static RequestPortletDataHandler[] toDTOs(String json) {
		RequestPortletDataHandlerJSONParser
			requestPortletDataHandlerJSONParser =
				new RequestPortletDataHandlerJSONParser();

		return requestPortletDataHandlerJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		RequestPortletDataHandler requestPortletDataHandler) {

		if (requestPortletDataHandler == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (requestPortletDataHandler.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(requestPortletDataHandler.getName()));

			sb.append("\"");
		}

		if (requestPortletDataHandler.getRequestPortletDataHandlerControls() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"requestPortletDataHandlerControls\": ");

			sb.append("[");

			for (int i = 0;
				 i < requestPortletDataHandler.
					 getRequestPortletDataHandlerControls().length;
				 i++) {

				sb.append(
					String.valueOf(
						requestPortletDataHandler.
							getRequestPortletDataHandlerControls()[i]));

				if ((i + 1) < requestPortletDataHandler.
						getRequestPortletDataHandlerControls().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		RequestPortletDataHandlerJSONParser
			requestPortletDataHandlerJSONParser =
				new RequestPortletDataHandlerJSONParser();

		return requestPortletDataHandlerJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		RequestPortletDataHandler requestPortletDataHandler) {

		if (requestPortletDataHandler == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (requestPortletDataHandler.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(requestPortletDataHandler.getName()));
		}

		if (requestPortletDataHandler.getRequestPortletDataHandlerControls() ==
				null) {

			map.put("requestPortletDataHandlerControls", null);
		}
		else {
			map.put(
				"requestPortletDataHandlerControls",
				String.valueOf(
					requestPortletDataHandler.
						getRequestPortletDataHandlerControls()));
		}

		return map;
	}

	public static class RequestPortletDataHandlerJSONParser
		extends BaseJSONParser<RequestPortletDataHandler> {

		@Override
		protected RequestPortletDataHandler createDTO() {
			return new RequestPortletDataHandler();
		}

		@Override
		protected RequestPortletDataHandler[] createDTOArray(int size) {
			return new RequestPortletDataHandler[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"requestPortletDataHandlerControls")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			RequestPortletDataHandler requestPortletDataHandler,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					requestPortletDataHandler.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"requestPortletDataHandlerControls")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					RequestPortletDataHandlerControl[]
						requestPortletDataHandlerControlsArray =
							new RequestPortletDataHandlerControl
								[jsonParserFieldValues.length];

					for (int i = 0;
						 i < requestPortletDataHandlerControlsArray.length;
						 i++) {

						requestPortletDataHandlerControlsArray[i] =
							RequestPortletDataHandlerControlSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					requestPortletDataHandler.
						setRequestPortletDataHandlerControls(
							requestPortletDataHandlerControlsArray);
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
// LIFERAY-REST-BUILDER-HASH:335036725