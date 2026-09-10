/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.StayInPageFormContainerSubmissionResult;
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
public class StayInPageFormContainerSubmissionResultSerDes {

	public static StayInPageFormContainerSubmissionResult toDTO(String json) {
		StayInPageFormContainerSubmissionResultJSONParser
			stayInPageFormContainerSubmissionResultJSONParser =
				new StayInPageFormContainerSubmissionResultJSONParser();

		return stayInPageFormContainerSubmissionResultJSONParser.parseToDTO(
			json);
	}

	public static StayInPageFormContainerSubmissionResult[] toDTOs(
		String json) {

		StayInPageFormContainerSubmissionResultJSONParser
			stayInPageFormContainerSubmissionResultJSONParser =
				new StayInPageFormContainerSubmissionResultJSONParser();

		return stayInPageFormContainerSubmissionResultJSONParser.parseToDTOs(
			json);
	}

	public static String toJSON(
		StayInPageFormContainerSubmissionResult
			stayInPageFormContainerSubmissionResult) {

		if (stayInPageFormContainerSubmissionResult == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (stayInPageFormContainerSubmissionResult.
				getSuccessNotificationMessage() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"successNotificationMessage\": ");

			sb.append(
				String.valueOf(
					stayInPageFormContainerSubmissionResult.
						getSuccessNotificationMessage()));
		}

		if (stayInPageFormContainerSubmissionResult.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(stayInPageFormContainerSubmissionResult.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		StayInPageFormContainerSubmissionResultJSONParser
			stayInPageFormContainerSubmissionResultJSONParser =
				new StayInPageFormContainerSubmissionResultJSONParser();

		return stayInPageFormContainerSubmissionResultJSONParser.parseToMap(
			json);
	}

	public static Map<String, String> toMap(
		StayInPageFormContainerSubmissionResult
			stayInPageFormContainerSubmissionResult) {

		if (stayInPageFormContainerSubmissionResult == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (stayInPageFormContainerSubmissionResult.
				getSuccessNotificationMessage() == null) {

			map.put("successNotificationMessage", null);
		}
		else {
			map.put(
				"successNotificationMessage",
				String.valueOf(
					stayInPageFormContainerSubmissionResult.
						getSuccessNotificationMessage()));
		}

		if (stayInPageFormContainerSubmissionResult.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(
					stayInPageFormContainerSubmissionResult.getType()));
		}

		return map;
	}

	public static class StayInPageFormContainerSubmissionResultJSONParser
		extends BaseJSONParser<StayInPageFormContainerSubmissionResult> {

		@Override
		protected StayInPageFormContainerSubmissionResult createDTO() {
			return new StayInPageFormContainerSubmissionResult();
		}

		@Override
		protected StayInPageFormContainerSubmissionResult[] createDTOArray(
			int size) {

			return new StayInPageFormContainerSubmissionResult[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
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
			StayInPageFormContainerSubmissionResult
				stayInPageFormContainerSubmissionResult,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "successNotificationMessage")) {

				if (jsonParserFieldValue != null) {
					stayInPageFormContainerSubmissionResult.
						setSuccessNotificationMessage(
							SuccessNotificationMessageSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					stayInPageFormContainerSubmissionResult.setType(
						StayInPageFormContainerSubmissionResult.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:1086253342