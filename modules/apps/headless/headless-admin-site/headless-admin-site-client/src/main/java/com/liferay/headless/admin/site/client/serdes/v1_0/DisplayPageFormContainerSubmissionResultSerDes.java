/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageFormContainerSubmissionResult;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
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
public class DisplayPageFormContainerSubmissionResultSerDes {

	public static DisplayPageFormContainerSubmissionResult toDTO(String json) {
		DisplayPageFormContainerSubmissionResultJSONParser
			displayPageFormContainerSubmissionResultJSONParser =
				new DisplayPageFormContainerSubmissionResultJSONParser();

		return displayPageFormContainerSubmissionResultJSONParser.parseToDTO(
			json);
	}

	public static DisplayPageFormContainerSubmissionResult[] toDTOs(
		String json) {

		DisplayPageFormContainerSubmissionResultJSONParser
			displayPageFormContainerSubmissionResultJSONParser =
				new DisplayPageFormContainerSubmissionResultJSONParser();

		return displayPageFormContainerSubmissionResultJSONParser.parseToDTOs(
			json);
	}

	public static String toJSON(
		DisplayPageFormContainerSubmissionResult
			displayPageFormContainerSubmissionResult) {

		if (displayPageFormContainerSubmissionResult == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (displayPageFormContainerSubmissionResult.getDefaultDisplayPage() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultDisplayPage\": ");

			sb.append(
				displayPageFormContainerSubmissionResult.
					getDefaultDisplayPage());
		}

		if (displayPageFormContainerSubmissionResult.
				getItemExternalReference() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemExternalReference\": ");

			sb.append(
				String.valueOf(
					displayPageFormContainerSubmissionResult.
						getItemExternalReference()));
		}

		if (displayPageFormContainerSubmissionResult.
				getSuccessNotificationMessage() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"successNotificationMessage\": ");

			sb.append(
				String.valueOf(
					displayPageFormContainerSubmissionResult.
						getSuccessNotificationMessage()));
		}

		if (displayPageFormContainerSubmissionResult.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(displayPageFormContainerSubmissionResult.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DisplayPageFormContainerSubmissionResultJSONParser
			displayPageFormContainerSubmissionResultJSONParser =
				new DisplayPageFormContainerSubmissionResultJSONParser();

		return displayPageFormContainerSubmissionResultJSONParser.parseToMap(
			json);
	}

	public static Map<String, String> toMap(
		DisplayPageFormContainerSubmissionResult
			displayPageFormContainerSubmissionResult) {

		if (displayPageFormContainerSubmissionResult == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (displayPageFormContainerSubmissionResult.getDefaultDisplayPage() ==
				null) {

			map.put("defaultDisplayPage", null);
		}
		else {
			map.put(
				"defaultDisplayPage",
				String.valueOf(
					displayPageFormContainerSubmissionResult.
						getDefaultDisplayPage()));
		}

		if (displayPageFormContainerSubmissionResult.
				getItemExternalReference() == null) {

			map.put("itemExternalReference", null);
		}
		else {
			map.put(
				"itemExternalReference",
				String.valueOf(
					displayPageFormContainerSubmissionResult.
						getItemExternalReference()));
		}

		if (displayPageFormContainerSubmissionResult.
				getSuccessNotificationMessage() == null) {

			map.put("successNotificationMessage", null);
		}
		else {
			map.put(
				"successNotificationMessage",
				String.valueOf(
					displayPageFormContainerSubmissionResult.
						getSuccessNotificationMessage()));
		}

		if (displayPageFormContainerSubmissionResult.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(
					displayPageFormContainerSubmissionResult.getType()));
		}

		return map;
	}

	public static class DisplayPageFormContainerSubmissionResultJSONParser
		extends BaseJSONParser<DisplayPageFormContainerSubmissionResult> {

		@Override
		protected DisplayPageFormContainerSubmissionResult createDTO() {
			return new DisplayPageFormContainerSubmissionResult();
		}

		@Override
		protected DisplayPageFormContainerSubmissionResult[] createDTOArray(
			int size) {

			return new DisplayPageFormContainerSubmissionResult[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "defaultDisplayPage")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "itemExternalReference")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "successNotificationMessage")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DisplayPageFormContainerSubmissionResult
				displayPageFormContainerSubmissionResult,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "defaultDisplayPage")) {
				if (jsonParserFieldValue != null) {
					displayPageFormContainerSubmissionResult.
						setDefaultDisplayPage((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "itemExternalReference")) {

				if (jsonParserFieldValue != null) {
					displayPageFormContainerSubmissionResult.
						setItemExternalReference(
							ItemExternalReferenceSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "successNotificationMessage")) {

				if (jsonParserFieldValue != null) {
					displayPageFormContainerSubmissionResult.
						setSuccessNotificationMessage(
							SuccessNotificationMessageSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					displayPageFormContainerSubmissionResult.setType(
						DisplayPageFormContainerSubmissionResult.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:256962505