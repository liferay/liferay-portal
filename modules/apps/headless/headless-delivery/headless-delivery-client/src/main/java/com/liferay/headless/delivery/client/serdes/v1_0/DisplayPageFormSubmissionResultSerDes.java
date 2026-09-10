/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.DisplayPageFormSubmissionResult;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class DisplayPageFormSubmissionResultSerDes {

	public static DisplayPageFormSubmissionResult toDTO(String json) {
		DisplayPageFormSubmissionResultJSONParser
			displayPageFormSubmissionResultJSONParser =
				new DisplayPageFormSubmissionResultJSONParser();

		return displayPageFormSubmissionResultJSONParser.parseToDTO(json);
	}

	public static DisplayPageFormSubmissionResult[] toDTOs(String json) {
		DisplayPageFormSubmissionResultJSONParser
			displayPageFormSubmissionResultJSONParser =
				new DisplayPageFormSubmissionResultJSONParser();

		return displayPageFormSubmissionResultJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		DisplayPageFormSubmissionResult displayPageFormSubmissionResult) {

		if (displayPageFormSubmissionResult == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (displayPageFormSubmissionResult.getDefaultDisplayPage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultDisplayPage\": ");

			sb.append(displayPageFormSubmissionResult.getDefaultDisplayPage());
		}

		if (displayPageFormSubmissionResult.getMapping() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mapping\": ");

			sb.append(
				String.valueOf(displayPageFormSubmissionResult.getMapping()));
		}

		if (displayPageFormSubmissionResult.
				getNotificationTextFragmentInlineValue() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"notificationTextFragmentInlineValue\": ");

			sb.append(
				String.valueOf(
					displayPageFormSubmissionResult.
						getNotificationTextFragmentInlineValue()));
		}

		if (displayPageFormSubmissionResult.getShowNotification() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showNotification\": ");

			sb.append(displayPageFormSubmissionResult.getShowNotification());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DisplayPageFormSubmissionResultJSONParser
			displayPageFormSubmissionResultJSONParser =
				new DisplayPageFormSubmissionResultJSONParser();

		return displayPageFormSubmissionResultJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DisplayPageFormSubmissionResult displayPageFormSubmissionResult) {

		if (displayPageFormSubmissionResult == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (displayPageFormSubmissionResult.getDefaultDisplayPage() == null) {
			map.put("defaultDisplayPage", null);
		}
		else {
			map.put(
				"defaultDisplayPage",
				String.valueOf(
					displayPageFormSubmissionResult.getDefaultDisplayPage()));
		}

		if (displayPageFormSubmissionResult.getMapping() == null) {
			map.put("mapping", null);
		}
		else {
			map.put(
				"mapping",
				String.valueOf(displayPageFormSubmissionResult.getMapping()));
		}

		if (displayPageFormSubmissionResult.
				getNotificationTextFragmentInlineValue() == null) {

			map.put("notificationTextFragmentInlineValue", null);
		}
		else {
			map.put(
				"notificationTextFragmentInlineValue",
				String.valueOf(
					displayPageFormSubmissionResult.
						getNotificationTextFragmentInlineValue()));
		}

		if (displayPageFormSubmissionResult.getShowNotification() == null) {
			map.put("showNotification", null);
		}
		else {
			map.put(
				"showNotification",
				String.valueOf(
					displayPageFormSubmissionResult.getShowNotification()));
		}

		return map;
	}

	public static class DisplayPageFormSubmissionResultJSONParser
		extends BaseJSONParser<DisplayPageFormSubmissionResult> {

		@Override
		protected DisplayPageFormSubmissionResult createDTO() {
			return new DisplayPageFormSubmissionResult();
		}

		@Override
		protected DisplayPageFormSubmissionResult[] createDTOArray(int size) {
			return new DisplayPageFormSubmissionResult[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "defaultDisplayPage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "mapping")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"notificationTextFragmentInlineValue")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "showNotification")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DisplayPageFormSubmissionResult displayPageFormSubmissionResult,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "defaultDisplayPage")) {
				if (jsonParserFieldValue != null) {
					displayPageFormSubmissionResult.setDefaultDisplayPage(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "mapping")) {
				if (jsonParserFieldValue != null) {
					displayPageFormSubmissionResult.setMapping(
						MappingSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"notificationTextFragmentInlineValue")) {

				if (jsonParserFieldValue != null) {
					displayPageFormSubmissionResult.
						setNotificationTextFragmentInlineValue(
							FragmentInlineValueSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "showNotification")) {
				if (jsonParserFieldValue != null) {
					displayPageFormSubmissionResult.setShowNotification(
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
// LIFERAY-REST-BUILDER-HASH:-540544879