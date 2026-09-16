/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.AccountLifecycleStageTransition;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class AccountLifecycleStageTransitionSerDes {

	public static AccountLifecycleStageTransition toDTO(String json) {
		AccountLifecycleStageTransitionJSONParser
			accountLifecycleStageTransitionJSONParser =
				new AccountLifecycleStageTransitionJSONParser();

		return accountLifecycleStageTransitionJSONParser.parseToDTO(json);
	}

	public static AccountLifecycleStageTransition[] toDTOs(String json) {
		AccountLifecycleStageTransitionJSONParser
			accountLifecycleStageTransitionJSONParser =
				new AccountLifecycleStageTransitionJSONParser();

		return accountLifecycleStageTransitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		AccountLifecycleStageTransition accountLifecycleStageTransition) {

		if (accountLifecycleStageTransition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (accountLifecycleStageTransition.getAccountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountId\": ");

			sb.append("\"");

			sb.append(_escape(accountLifecycleStageTransition.getAccountId()));

			sb.append("\"");
		}

		if (accountLifecycleStageTransition.getAccountName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountName\": ");

			sb.append("\"");

			sb.append(
				_escape(accountLifecycleStageTransition.getAccountName()));

			sb.append("\"");
		}

		if (accountLifecycleStageTransition.getFromAccountLifecycleStage() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fromAccountLifecycleStage\": ");

			sb.append(
				String.valueOf(
					accountLifecycleStageTransition.
						getFromAccountLifecycleStage()));
		}

		if (accountLifecycleStageTransition.getToAccountLifecycleStage() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"toAccountLifecycleStage\": ");

			sb.append(
				String.valueOf(
					accountLifecycleStageTransition.
						getToAccountLifecycleStage()));
		}

		if (accountLifecycleStageTransition.getTransitionDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"transitionDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					accountLifecycleStageTransition.getTransitionDate()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AccountLifecycleStageTransitionJSONParser
			accountLifecycleStageTransitionJSONParser =
				new AccountLifecycleStageTransitionJSONParser();

		return accountLifecycleStageTransitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AccountLifecycleStageTransition accountLifecycleStageTransition) {

		if (accountLifecycleStageTransition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (accountLifecycleStageTransition.getAccountId() == null) {
			map.put("accountId", null);
		}
		else {
			map.put(
				"accountId",
				String.valueOf(accountLifecycleStageTransition.getAccountId()));
		}

		if (accountLifecycleStageTransition.getAccountName() == null) {
			map.put("accountName", null);
		}
		else {
			map.put(
				"accountName",
				String.valueOf(
					accountLifecycleStageTransition.getAccountName()));
		}

		if (accountLifecycleStageTransition.getFromAccountLifecycleStage() ==
				null) {

			map.put("fromAccountLifecycleStage", null);
		}
		else {
			map.put(
				"fromAccountLifecycleStage",
				String.valueOf(
					accountLifecycleStageTransition.
						getFromAccountLifecycleStage()));
		}

		if (accountLifecycleStageTransition.getToAccountLifecycleStage() ==
				null) {

			map.put("toAccountLifecycleStage", null);
		}
		else {
			map.put(
				"toAccountLifecycleStage",
				String.valueOf(
					accountLifecycleStageTransition.
						getToAccountLifecycleStage()));
		}

		if (accountLifecycleStageTransition.getTransitionDate() == null) {
			map.put("transitionDate", null);
		}
		else {
			map.put(
				"transitionDate",
				liferayToJSONDateFormat.format(
					accountLifecycleStageTransition.getTransitionDate()));
		}

		return map;
	}

	public static class AccountLifecycleStageTransitionJSONParser
		extends BaseJSONParser<AccountLifecycleStageTransition> {

		@Override
		protected AccountLifecycleStageTransition createDTO() {
			return new AccountLifecycleStageTransition();
		}

		@Override
		protected AccountLifecycleStageTransition[] createDTOArray(int size) {
			return new AccountLifecycleStageTransition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accountId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "accountName")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "fromAccountLifecycleStage")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "toAccountLifecycleStage")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "transitionDate")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AccountLifecycleStageTransition accountLifecycleStageTransition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountId")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStageTransition.setAccountId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "accountName")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStageTransition.setAccountName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "fromAccountLifecycleStage")) {

				if (jsonParserFieldValue != null) {
					accountLifecycleStageTransition.
						setFromAccountLifecycleStage(
							AccountLifecycleStageSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "toAccountLifecycleStage")) {

				if (jsonParserFieldValue != null) {
					accountLifecycleStageTransition.setToAccountLifecycleStage(
						AccountLifecycleStageSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "transitionDate")) {
				if (jsonParserFieldValue != null) {
					accountLifecycleStageTransition.setTransitionDate(
						toDate((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:1334858808