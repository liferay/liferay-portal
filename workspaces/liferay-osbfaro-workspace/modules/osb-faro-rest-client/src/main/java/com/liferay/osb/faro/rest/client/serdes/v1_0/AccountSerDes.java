/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.Account;
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
public class AccountSerDes {

	public static Account toDTO(String json) {
		AccountJSONParser accountJSONParser = new AccountJSONParser();

		return accountJSONParser.parseToDTO(json);
	}

	public static Account[] toDTOs(String json) {
		AccountJSONParser accountJSONParser = new AccountJSONParser();

		return accountJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Account account) {
		if (account == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (account.getAccountName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountName\": ");

			sb.append("\"");

			sb.append(_escape(account.getAccountName()));

			sb.append("\"");
		}

		if (account.getAccountType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountType\": ");

			sb.append("\"");

			sb.append(_escape(account.getAccountType()));

			sb.append("\"");
		}

		if (account.getActivitiesCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"activitiesCount\": ");

			sb.append(account.getActivitiesCount());
		}

		if (account.getAnnualRevenue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"annualRevenue\": ");

			sb.append(account.getAnnualRevenue());
		}

		if (account.getCountry() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"country\": ");

			sb.append("\"");

			sb.append(_escape(account.getCountry()));

			sb.append("\"");
		}

		if (account.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(account.getDateModified()));

			sb.append("\"");
		}

		if (account.getFirstActivityDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"firstActivityDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(account.getFirstActivityDate()));

			sb.append("\"");
		}

		if (account.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(account.getId()));

			sb.append("\"");
		}

		if (account.getIndustry() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"industry\": ");

			sb.append("\"");

			sb.append(_escape(account.getIndustry()));

			sb.append("\"");
		}

		if (account.getLastActivityDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastActivityDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(account.getLastActivityDate()));

			sb.append("\"");
		}

		if (account.getLifecycleStage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lifecycleStage\": ");

			sb.append("\"");

			sb.append(_escape(account.getLifecycleStage()));

			sb.append("\"");
		}

		if (account.getNumberOfEmployees() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfEmployees\": ");

			sb.append(account.getNumberOfEmployees());
		}

		if (account.getWebsite() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"website\": ");

			sb.append("\"");

			sb.append(_escape(account.getWebsite()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AccountJSONParser accountJSONParser = new AccountJSONParser();

		return accountJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Account account) {
		if (account == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (account.getAccountName() == null) {
			map.put("accountName", null);
		}
		else {
			map.put("accountName", String.valueOf(account.getAccountName()));
		}

		if (account.getAccountType() == null) {
			map.put("accountType", null);
		}
		else {
			map.put("accountType", String.valueOf(account.getAccountType()));
		}

		if (account.getActivitiesCount() == null) {
			map.put("activitiesCount", null);
		}
		else {
			map.put(
				"activitiesCount",
				String.valueOf(account.getActivitiesCount()));
		}

		if (account.getAnnualRevenue() == null) {
			map.put("annualRevenue", null);
		}
		else {
			map.put(
				"annualRevenue", String.valueOf(account.getAnnualRevenue()));
		}

		if (account.getCountry() == null) {
			map.put("country", null);
		}
		else {
			map.put("country", String.valueOf(account.getCountry()));
		}

		if (account.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(account.getDateModified()));
		}

		if (account.getFirstActivityDate() == null) {
			map.put("firstActivityDate", null);
		}
		else {
			map.put(
				"firstActivityDate",
				liferayToJSONDateFormat.format(account.getFirstActivityDate()));
		}

		if (account.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(account.getId()));
		}

		if (account.getIndustry() == null) {
			map.put("industry", null);
		}
		else {
			map.put("industry", String.valueOf(account.getIndustry()));
		}

		if (account.getLastActivityDate() == null) {
			map.put("lastActivityDate", null);
		}
		else {
			map.put(
				"lastActivityDate",
				liferayToJSONDateFormat.format(account.getLastActivityDate()));
		}

		if (account.getLifecycleStage() == null) {
			map.put("lifecycleStage", null);
		}
		else {
			map.put(
				"lifecycleStage", String.valueOf(account.getLifecycleStage()));
		}

		if (account.getNumberOfEmployees() == null) {
			map.put("numberOfEmployees", null);
		}
		else {
			map.put(
				"numberOfEmployees",
				String.valueOf(account.getNumberOfEmployees()));
		}

		if (account.getWebsite() == null) {
			map.put("website", null);
		}
		else {
			map.put("website", String.valueOf(account.getWebsite()));
		}

		return map;
	}

	public static class AccountJSONParser extends BaseJSONParser<Account> {

		@Override
		protected Account createDTO() {
			return new Account();
		}

		@Override
		protected Account[] createDTOArray(int size) {
			return new Account[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accountName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "accountType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "activitiesCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "annualRevenue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "country")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "firstActivityDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "industry")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lastActivityDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lifecycleStage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfEmployees")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "website")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Account account, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountName")) {
				if (jsonParserFieldValue != null) {
					account.setAccountName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "accountType")) {
				if (jsonParserFieldValue != null) {
					account.setAccountType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "activitiesCount")) {
				if (jsonParserFieldValue != null) {
					account.setActivitiesCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "annualRevenue")) {
				if (jsonParserFieldValue != null) {
					account.setAnnualRevenue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "country")) {
				if (jsonParserFieldValue != null) {
					account.setCountry((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					account.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "firstActivityDate")) {
				if (jsonParserFieldValue != null) {
					account.setFirstActivityDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					account.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "industry")) {
				if (jsonParserFieldValue != null) {
					account.setIndustry((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lastActivityDate")) {
				if (jsonParserFieldValue != null) {
					account.setLastActivityDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lifecycleStage")) {
				if (jsonParserFieldValue != null) {
					account.setLifecycleStage((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfEmployees")) {
				if (jsonParserFieldValue != null) {
					account.setNumberOfEmployees(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "website")) {
				if (jsonParserFieldValue != null) {
					account.setWebsite((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:1480950229