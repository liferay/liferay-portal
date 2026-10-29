/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.AccountLifecycleStage;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class AccountLifecycleStageSerDes {

	public static AccountLifecycleStage toDTO(String json) {
		AccountLifecycleStageJSONParser accountLifecycleStageJSONParser =
			new AccountLifecycleStageJSONParser();

		return accountLifecycleStageJSONParser.parseToDTO(json);
	}

	public static AccountLifecycleStage[] toDTOs(String json) {
		AccountLifecycleStageJSONParser accountLifecycleStageJSONParser =
			new AccountLifecycleStageJSONParser();

		return accountLifecycleStageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AccountLifecycleStage accountLifecycleStage) {
		if (accountLifecycleStage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (accountLifecycleStage.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(accountLifecycleStage.getDescription()));

			sb.append("\"");
		}

		if (accountLifecycleStage.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(accountLifecycleStage.getId()));

			sb.append("\"");
		}

		if (accountLifecycleStage.getMaxDuration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxDuration\": ");

			sb.append(accountLifecycleStage.getMaxDuration());
		}

		if (accountLifecycleStage.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(accountLifecycleStage.getName()));

			sb.append("\"");
		}

		if (accountLifecycleStage.getPosition() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"position\": ");

			sb.append(accountLifecycleStage.getPosition());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AccountLifecycleStageJSONParser accountLifecycleStageJSONParser =
			new AccountLifecycleStageJSONParser();

		return accountLifecycleStageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AccountLifecycleStage accountLifecycleStage) {

		if (accountLifecycleStage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (accountLifecycleStage.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(accountLifecycleStage.getDescription()));
		}

		if (accountLifecycleStage.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(accountLifecycleStage.getId()));
		}

		if (accountLifecycleStage.getMaxDuration() == null) {
			map.put("maxDuration", null);
		}
		else {
			map.put(
				"maxDuration",
				String.valueOf(accountLifecycleStage.getMaxDuration()));
		}

		if (accountLifecycleStage.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(accountLifecycleStage.getName()));
		}

		if (accountLifecycleStage.getPosition() == null) {
			map.put("position", null);
		}
		else {
			map.put(
				"position",
				String.valueOf(accountLifecycleStage.getPosition()));
		}

		return map;
	}

	public static class AccountLifecycleStageJSONParser
		extends BaseJSONParser<AccountLifecycleStage> {

		@Override
		protected AccountLifecycleStage createDTO() {
			return new AccountLifecycleStage();
		}

		@Override
		protected AccountLifecycleStage[] createDTOArray(int size) {
			return new AccountLifecycleStage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "maxDuration")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "position")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AccountLifecycleStage accountLifecycleStage,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStage.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStage.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "maxDuration")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStage.setMaxDuration(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStage.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "position")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStage.setPosition(
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
// LIFERAY-REST-BUILDER-HASH:-2062420481