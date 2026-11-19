/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.AccountLifecycle;
import com.liferay.osb.faro.rest.client.dto.v1_0.AccountLifecycleStage;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class AccountLifecycleSerDes {

	public static AccountLifecycle toDTO(String json) {
		AccountLifecycleJSONParser accountLifecycleJSONParser =
			new AccountLifecycleJSONParser();

		return accountLifecycleJSONParser.parseToDTO(json);
	}

	public static AccountLifecycle[] toDTOs(String json) {
		AccountLifecycleJSONParser accountLifecycleJSONParser =
			new AccountLifecycleJSONParser();

		return accountLifecycleJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AccountLifecycle accountLifecycle) {
		if (accountLifecycle == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (accountLifecycle.getAccountLifecycleStages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountLifecycleStages\": ");

			sb.append("[");

			for (int i = 0;
				 i < accountLifecycle.getAccountLifecycleStages().length; i++) {

				sb.append(
					String.valueOf(
						accountLifecycle.getAccountLifecycleStages()[i]));

				if ((i + 1) <
						accountLifecycle.getAccountLifecycleStages().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (accountLifecycle.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					accountLifecycle.getDateModified()));

			sb.append("\"");
		}

		if (accountLifecycle.getDefaultAccountLifecycle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultAccountLifecycle\": ");

			sb.append(accountLifecycle.getDefaultAccountLifecycle());
		}

		if (accountLifecycle.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(accountLifecycle.getDescription()));

			sb.append("\"");
		}

		if (accountLifecycle.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(accountLifecycle.getId()));

			sb.append("\"");
		}

		if (accountLifecycle.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(accountLifecycle.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AccountLifecycleJSONParser accountLifecycleJSONParser =
			new AccountLifecycleJSONParser();

		return accountLifecycleJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AccountLifecycle accountLifecycle) {
		if (accountLifecycle == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (accountLifecycle.getAccountLifecycleStages() == null) {
			map.put("accountLifecycleStages", null);
		}
		else {
			map.put(
				"accountLifecycleStages",
				String.valueOf(accountLifecycle.getAccountLifecycleStages()));
		}

		if (accountLifecycle.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					accountLifecycle.getDateModified()));
		}

		if (accountLifecycle.getDefaultAccountLifecycle() == null) {
			map.put("defaultAccountLifecycle", null);
		}
		else {
			map.put(
				"defaultAccountLifecycle",
				String.valueOf(accountLifecycle.getDefaultAccountLifecycle()));
		}

		if (accountLifecycle.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(accountLifecycle.getDescription()));
		}

		if (accountLifecycle.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(accountLifecycle.getId()));
		}

		if (accountLifecycle.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(accountLifecycle.getName()));
		}

		return map;
	}

	public static class AccountLifecycleJSONParser
		extends BaseJSONParser<AccountLifecycle> {

		@Override
		protected AccountLifecycle createDTO() {
			return new AccountLifecycle();
		}

		@Override
		protected AccountLifecycle[] createDTOArray(int size) {
			return new AccountLifecycle[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accountLifecycleStages")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "defaultAccountLifecycle")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
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
			AccountLifecycle accountLifecycle, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountLifecycleStages")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					AccountLifecycleStage[] accountLifecycleStagesArray =
						new AccountLifecycleStage[jsonParserFieldValues.length];

					for (int i = 0; i < accountLifecycleStagesArray.length;
						 i++) {

						accountLifecycleStagesArray[i] =
							AccountLifecycleStageSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					accountLifecycle.setAccountLifecycleStages(
						accountLifecycleStagesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					accountLifecycle.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "defaultAccountLifecycle")) {

				if (jsonParserFieldValue != null) {
					accountLifecycle.setDefaultAccountLifecycle(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					accountLifecycle.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					accountLifecycle.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					accountLifecycle.setName((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-604602142