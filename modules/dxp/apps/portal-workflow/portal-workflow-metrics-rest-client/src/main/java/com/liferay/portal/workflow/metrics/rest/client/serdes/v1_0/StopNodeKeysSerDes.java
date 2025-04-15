/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.NodeKey;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.StopNodeKeys;
import com.liferay.portal.workflow.metrics.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class StopNodeKeysSerDes {

	public static StopNodeKeys toDTO(String json) {
		StopNodeKeysJSONParser stopNodeKeysJSONParser =
			new StopNodeKeysJSONParser();

		return stopNodeKeysJSONParser.parseToDTO(json);
	}

	public static StopNodeKeys[] toDTOs(String json) {
		StopNodeKeysJSONParser stopNodeKeysJSONParser =
			new StopNodeKeysJSONParser();

		return stopNodeKeysJSONParser.parseToDTOs(json);
	}

	public static String toJSON(StopNodeKeys stopNodeKeys) {
		if (stopNodeKeys == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (stopNodeKeys.getNodeKeys() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nodeKeys\": ");

			sb.append("[");

			for (int i = 0; i < stopNodeKeys.getNodeKeys().length; i++) {
				sb.append(String.valueOf(stopNodeKeys.getNodeKeys()[i]));

				if ((i + 1) < stopNodeKeys.getNodeKeys().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (stopNodeKeys.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(stopNodeKeys.getStatus());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		StopNodeKeysJSONParser stopNodeKeysJSONParser =
			new StopNodeKeysJSONParser();

		return stopNodeKeysJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(StopNodeKeys stopNodeKeys) {
		if (stopNodeKeys == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (stopNodeKeys.getNodeKeys() == null) {
			map.put("nodeKeys", null);
		}
		else {
			map.put("nodeKeys", String.valueOf(stopNodeKeys.getNodeKeys()));
		}

		if (stopNodeKeys.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(stopNodeKeys.getStatus()));
		}

		return map;
	}

	public static class StopNodeKeysJSONParser
		extends BaseJSONParser<StopNodeKeys> {

		@Override
		protected StopNodeKeys createDTO() {
			return new StopNodeKeys();
		}

		@Override
		protected StopNodeKeys[] createDTOArray(int size) {
			return new StopNodeKeys[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "nodeKeys")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			StopNodeKeys stopNodeKeys, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "nodeKeys")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					NodeKey[] nodeKeysArray =
						new NodeKey[jsonParserFieldValues.length];

					for (int i = 0; i < nodeKeysArray.length; i++) {
						nodeKeysArray[i] = NodeKeySerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					stopNodeKeys.setNodeKeys(nodeKeysArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					stopNodeKeys.setStatus(
						Integer.valueOf((String)jsonParserFieldValue));
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