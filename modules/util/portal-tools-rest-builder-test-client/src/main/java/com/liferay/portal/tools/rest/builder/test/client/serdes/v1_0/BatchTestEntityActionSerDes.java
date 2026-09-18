/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.BatchTestEntityAction;
import com.liferay.portal.tools.rest.builder.test.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class BatchTestEntityActionSerDes {

	public static BatchTestEntityAction toDTO(String json) {
		BatchTestEntityActionJSONParser batchTestEntityActionJSONParser =
			new BatchTestEntityActionJSONParser();

		return batchTestEntityActionJSONParser.parseToDTO(json);
	}

	public static BatchTestEntityAction[] toDTOs(String json) {
		BatchTestEntityActionJSONParser batchTestEntityActionJSONParser =
			new BatchTestEntityActionJSONParser();

		return batchTestEntityActionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(BatchTestEntityAction batchTestEntityAction) {
		if (batchTestEntityAction == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (batchTestEntityAction.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(batchTestEntityAction.getName()));

			sb.append("\"");
		}

		if (batchTestEntityAction.getSourceBatchTestEntityId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sourceBatchTestEntityId\": ");

			sb.append(batchTestEntityAction.getSourceBatchTestEntityId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		BatchTestEntityActionJSONParser batchTestEntityActionJSONParser =
			new BatchTestEntityActionJSONParser();

		return batchTestEntityActionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		BatchTestEntityAction batchTestEntityAction) {

		if (batchTestEntityAction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (batchTestEntityAction.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(batchTestEntityAction.getName()));
		}

		if (batchTestEntityAction.getSourceBatchTestEntityId() == null) {
			map.put("sourceBatchTestEntityId", null);
		}
		else {
			map.put(
				"sourceBatchTestEntityId",
				String.valueOf(
					batchTestEntityAction.getSourceBatchTestEntityId()));
		}

		return map;
	}

	public static class BatchTestEntityActionJSONParser
		extends BaseJSONParser<BatchTestEntityAction> {

		@Override
		protected BatchTestEntityAction createDTO() {
			return new BatchTestEntityAction();
		}

		@Override
		protected BatchTestEntityAction[] createDTOArray(int size) {
			return new BatchTestEntityAction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "sourceBatchTestEntityId")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			BatchTestEntityAction batchTestEntityAction,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					batchTestEntityAction.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "sourceBatchTestEntityId")) {

				if (jsonParserFieldValue != null) {
					batchTestEntityAction.setSourceBatchTestEntityId(
						Long.valueOf((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:1354105892