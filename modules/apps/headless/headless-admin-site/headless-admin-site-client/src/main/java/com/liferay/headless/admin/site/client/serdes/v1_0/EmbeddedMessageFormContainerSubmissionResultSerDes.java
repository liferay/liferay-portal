/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.EmbeddedMessageFormContainerSubmissionResult;
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
public class EmbeddedMessageFormContainerSubmissionResultSerDes {

	public static EmbeddedMessageFormContainerSubmissionResult toDTO(
		String json) {

		EmbeddedMessageFormContainerSubmissionResultJSONParser
			embeddedMessageFormContainerSubmissionResultJSONParser =
				new EmbeddedMessageFormContainerSubmissionResultJSONParser();

		return embeddedMessageFormContainerSubmissionResultJSONParser.
			parseToDTO(json);
	}

	public static EmbeddedMessageFormContainerSubmissionResult[] toDTOs(
		String json) {

		EmbeddedMessageFormContainerSubmissionResultJSONParser
			embeddedMessageFormContainerSubmissionResultJSONParser =
				new EmbeddedMessageFormContainerSubmissionResultJSONParser();

		return embeddedMessageFormContainerSubmissionResultJSONParser.
			parseToDTOs(json);
	}

	public static String toJSON(
		EmbeddedMessageFormContainerSubmissionResult
			embeddedMessageFormContainerSubmissionResult) {

		if (embeddedMessageFormContainerSubmissionResult == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (embeddedMessageFormContainerSubmissionResult.getMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"message\": ");

			sb.append(
				String.valueOf(
					embeddedMessageFormContainerSubmissionResult.getMessage()));
		}

		if (embeddedMessageFormContainerSubmissionResult.
				getSuccessNotificationMessage() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"successNotificationMessage\": ");

			sb.append(
				String.valueOf(
					embeddedMessageFormContainerSubmissionResult.
						getSuccessNotificationMessage()));
		}

		if (embeddedMessageFormContainerSubmissionResult.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(embeddedMessageFormContainerSubmissionResult.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		EmbeddedMessageFormContainerSubmissionResultJSONParser
			embeddedMessageFormContainerSubmissionResultJSONParser =
				new EmbeddedMessageFormContainerSubmissionResultJSONParser();

		return embeddedMessageFormContainerSubmissionResultJSONParser.
			parseToMap(json);
	}

	public static Map<String, String> toMap(
		EmbeddedMessageFormContainerSubmissionResult
			embeddedMessageFormContainerSubmissionResult) {

		if (embeddedMessageFormContainerSubmissionResult == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (embeddedMessageFormContainerSubmissionResult.getMessage() == null) {
			map.put("message", null);
		}
		else {
			map.put(
				"message",
				String.valueOf(
					embeddedMessageFormContainerSubmissionResult.getMessage()));
		}

		if (embeddedMessageFormContainerSubmissionResult.
				getSuccessNotificationMessage() == null) {

			map.put("successNotificationMessage", null);
		}
		else {
			map.put(
				"successNotificationMessage",
				String.valueOf(
					embeddedMessageFormContainerSubmissionResult.
						getSuccessNotificationMessage()));
		}

		if (embeddedMessageFormContainerSubmissionResult.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(
					embeddedMessageFormContainerSubmissionResult.getType()));
		}

		return map;
	}

	public static class EmbeddedMessageFormContainerSubmissionResultJSONParser
		extends BaseJSONParser<EmbeddedMessageFormContainerSubmissionResult> {

		@Override
		protected EmbeddedMessageFormContainerSubmissionResult createDTO() {
			return new EmbeddedMessageFormContainerSubmissionResult();
		}

		@Override
		protected EmbeddedMessageFormContainerSubmissionResult[] createDTOArray(
			int size) {

			return new EmbeddedMessageFormContainerSubmissionResult[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "message")) {
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
			EmbeddedMessageFormContainerSubmissionResult
				embeddedMessageFormContainerSubmissionResult,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "message")) {
				if (jsonParserFieldValue != null) {
					embeddedMessageFormContainerSubmissionResult.setMessage(
						FragmentInlineValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "successNotificationMessage")) {

				if (jsonParserFieldValue != null) {
					embeddedMessageFormContainerSubmissionResult.
						setSuccessNotificationMessage(
							SuccessNotificationMessageSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					embeddedMessageFormContainerSubmissionResult.setType(
						EmbeddedMessageFormContainerSubmissionResult.Type.
							create((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:-1489640652