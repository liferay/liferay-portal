/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.NodeKey;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.StartNodeKeys;
import com.liferay.portal.workflow.metrics.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class StartNodeKeysSerDes {

	public static StartNodeKeys toDTO(String json) {
		StartNodeKeysJSONParser startNodeKeysJSONParser =
			new StartNodeKeysJSONParser();

		return startNodeKeysJSONParser.parseToDTO(json);
	}

	public static StartNodeKeys[] toDTOs(String json) {
		StartNodeKeysJSONParser startNodeKeysJSONParser =
			new StartNodeKeysJSONParser();

		return startNodeKeysJSONParser.parseToDTOs(json);
	}

	public static String toJSON(StartNodeKeys startNodeKeys) {
		if (startNodeKeys == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (startNodeKeys.getNodeKeys() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nodeKeys\": ");

			sb.append("[");

			for (int i = 0; i < startNodeKeys.getNodeKeys().length; i++) {
				sb.append(String.valueOf(startNodeKeys.getNodeKeys()[i]));

				if ((i + 1) < startNodeKeys.getNodeKeys().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (startNodeKeys.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(startNodeKeys.getStatus());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		StartNodeKeysJSONParser startNodeKeysJSONParser =
			new StartNodeKeysJSONParser();

		return startNodeKeysJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(StartNodeKeys startNodeKeys) {
		if (startNodeKeys == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (startNodeKeys.getNodeKeys() == null) {
			map.put("nodeKeys", null);
		}
		else {
			map.put("nodeKeys", String.valueOf(startNodeKeys.getNodeKeys()));
		}

		if (startNodeKeys.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(startNodeKeys.getStatus()));
		}

		return map;
	}

	public static class StartNodeKeysJSONParser
		extends BaseJSONParser<StartNodeKeys> {

		@Override
		protected StartNodeKeys createDTO() {
			return new StartNodeKeys();
		}

		@Override
		protected StartNodeKeys[] createDTOArray(int size) {
			return new StartNodeKeys[size];
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
			StartNodeKeys startNodeKeys, String jsonParserFieldName,
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

					startNodeKeys.setNodeKeys(nodeKeysArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					startNodeKeys.setStatus(
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