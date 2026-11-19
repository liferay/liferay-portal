/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.AccountLifecycleHistory;
import com.liferay.osb.faro.rest.client.dto.v1_0.AccountLifecycleStageHistory;
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
public class AccountLifecycleHistorySerDes {

	public static AccountLifecycleHistory toDTO(String json) {
		AccountLifecycleHistoryJSONParser accountLifecycleHistoryJSONParser =
			new AccountLifecycleHistoryJSONParser();

		return accountLifecycleHistoryJSONParser.parseToDTO(json);
	}

	public static AccountLifecycleHistory[] toDTOs(String json) {
		AccountLifecycleHistoryJSONParser accountLifecycleHistoryJSONParser =
			new AccountLifecycleHistoryJSONParser();

		return accountLifecycleHistoryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		AccountLifecycleHistory accountLifecycleHistory) {

		if (accountLifecycleHistory == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (accountLifecycleHistory.getAccountLifecycleStageHistories() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountLifecycleStageHistories\": ");

			sb.append("[");

			for (int i = 0;
				 i < accountLifecycleHistory.
					 getAccountLifecycleStageHistories().length;
				 i++) {

				sb.append(
					String.valueOf(
						accountLifecycleHistory.
							getAccountLifecycleStageHistories()[i]));

				if ((i + 1) < accountLifecycleHistory.
						getAccountLifecycleStageHistories().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (accountLifecycleHistory.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(accountLifecycleHistory.getId()));

			sb.append("\"");
		}

		if (accountLifecycleHistory.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(accountLifecycleHistory.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AccountLifecycleHistoryJSONParser accountLifecycleHistoryJSONParser =
			new AccountLifecycleHistoryJSONParser();

		return accountLifecycleHistoryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AccountLifecycleHistory accountLifecycleHistory) {

		if (accountLifecycleHistory == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (accountLifecycleHistory.getAccountLifecycleStageHistories() ==
				null) {

			map.put("accountLifecycleStageHistories", null);
		}
		else {
			map.put(
				"accountLifecycleStageHistories",
				String.valueOf(
					accountLifecycleHistory.
						getAccountLifecycleStageHistories()));
		}

		if (accountLifecycleHistory.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(accountLifecycleHistory.getId()));
		}

		if (accountLifecycleHistory.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(accountLifecycleHistory.getName()));
		}

		return map;
	}

	public static class AccountLifecycleHistoryJSONParser
		extends BaseJSONParser<AccountLifecycleHistory> {

		@Override
		protected AccountLifecycleHistory createDTO() {
			return new AccountLifecycleHistory();
		}

		@Override
		protected AccountLifecycleHistory[] createDTOArray(int size) {
			return new AccountLifecycleHistory[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "accountLifecycleStageHistories")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AccountLifecycleHistory accountLifecycleHistory,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "accountLifecycleStageHistories")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					AccountLifecycleStageHistory[]
						accountLifecycleStageHistoriesArray =
							new AccountLifecycleStageHistory
								[jsonParserFieldValues.length];

					for (int i = 0;
						 i < accountLifecycleStageHistoriesArray.length; i++) {

						accountLifecycleStageHistoriesArray[i] =
							AccountLifecycleStageHistorySerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					accountLifecycleHistory.setAccountLifecycleStageHistories(
						accountLifecycleStageHistoriesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleHistory.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleHistory.setName(
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
// LIFERAY-REST-BUILDER-HASH:-1793325114