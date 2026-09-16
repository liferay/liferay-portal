/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.AccountLifecycleStage;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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

		if (accountLifecycleStage.getDisplayOrder() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayOrder\": ");

			sb.append(accountLifecycleStage.getDisplayOrder());
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

		if (accountLifecycleStage.getStageType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"stageType\": ");

			sb.append("\"");

			sb.append(_escape(accountLifecycleStage.getStageType()));

			sb.append("\"");
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

		if (accountLifecycleStage.getDisplayOrder() == null) {
			map.put("displayOrder", null);
		}
		else {
			map.put(
				"displayOrder",
				String.valueOf(accountLifecycleStage.getDisplayOrder()));
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

		if (accountLifecycleStage.getStageType() == null) {
			map.put("stageType", null);
		}
		else {
			map.put(
				"stageType",
				String.valueOf(accountLifecycleStage.getStageType()));
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
			else if (Objects.equals(jsonParserFieldName, "displayOrder")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "maxDuration")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "stageType")) {
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
			else if (Objects.equals(jsonParserFieldName, "displayOrder")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStage.setDisplayOrder(
						Integer.valueOf((String)jsonParserFieldValue));
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
			else if (Objects.equals(jsonParserFieldName, "stageType")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStage.setStageType(
						(String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:1087458209