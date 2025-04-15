/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.account.client.dto.v1_0.AccountAddress;
import com.liferay.headless.commerce.admin.account.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class AccountAddressSerDes {

	public static AccountAddress toDTO(String json) {
		AccountAddressJSONParser accountAddressJSONParser =
			new AccountAddressJSONParser();

		return accountAddressJSONParser.parseToDTO(json);
	}

	public static AccountAddress[] toDTOs(String json) {
		AccountAddressJSONParser accountAddressJSONParser =
			new AccountAddressJSONParser();

		return accountAddressJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AccountAddress accountAddress) {
		if (accountAddress == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (accountAddress.getCity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"city\": ");

			sb.append("\"");

			sb.append(_escape(accountAddress.getCity()));

			sb.append("\"");
		}

		if (accountAddress.getCountryISOCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"countryISOCode\": ");

			sb.append("\"");

			sb.append(_escape(accountAddress.getCountryISOCode()));

			sb.append("\"");
		}

		if (accountAddress.getDefaultBilling() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultBilling\": ");

			sb.append(accountAddress.getDefaultBilling());
		}

		if (accountAddress.getDefaultShipping() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultShipping\": ");

			sb.append(accountAddress.getDefaultShipping());
		}

		if (accountAddress.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(accountAddress.getDescription()));

			sb.append("\"");
		}

		if (accountAddress.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(accountAddress.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (accountAddress.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(accountAddress.getId());
		}

		if (accountAddress.getLatitude() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"latitude\": ");

			sb.append(accountAddress.getLatitude());
		}

		if (accountAddress.getLongitude() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"longitude\": ");

			sb.append(accountAddress.getLongitude());
		}

		if (accountAddress.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(accountAddress.getName()));

			sb.append("\"");
		}

		if (accountAddress.getPhoneNumber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"phoneNumber\": ");

			sb.append("\"");

			sb.append(_escape(accountAddress.getPhoneNumber()));

			sb.append("\"");
		}

		if (accountAddress.getRegionISOCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"regionISOCode\": ");

			sb.append("\"");

			sb.append(_escape(accountAddress.getRegionISOCode()));

			sb.append("\"");
		}

		if (accountAddress.getStreet1() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street1\": ");

			sb.append("\"");

			sb.append(_escape(accountAddress.getStreet1()));

			sb.append("\"");
		}

		if (accountAddress.getStreet2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street2\": ");

			sb.append("\"");

			sb.append(_escape(accountAddress.getStreet2()));

			sb.append("\"");
		}

		if (accountAddress.getStreet3() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street3\": ");

			sb.append("\"");

			sb.append(_escape(accountAddress.getStreet3()));

			sb.append("\"");
		}

		if (accountAddress.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append(accountAddress.getType());
		}

		if (accountAddress.getZip() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"zip\": ");

			sb.append("\"");

			sb.append(_escape(accountAddress.getZip()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AccountAddressJSONParser accountAddressJSONParser =
			new AccountAddressJSONParser();

		return accountAddressJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AccountAddress accountAddress) {
		if (accountAddress == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (accountAddress.getCity() == null) {
			map.put("city", null);
		}
		else {
			map.put("city", String.valueOf(accountAddress.getCity()));
		}

		if (accountAddress.getCountryISOCode() == null) {
			map.put("countryISOCode", null);
		}
		else {
			map.put(
				"countryISOCode",
				String.valueOf(accountAddress.getCountryISOCode()));
		}

		if (accountAddress.getDefaultBilling() == null) {
			map.put("defaultBilling", null);
		}
		else {
			map.put(
				"defaultBilling",
				String.valueOf(accountAddress.getDefaultBilling()));
		}

		if (accountAddress.getDefaultShipping() == null) {
			map.put("defaultShipping", null);
		}
		else {
			map.put(
				"defaultShipping",
				String.valueOf(accountAddress.getDefaultShipping()));
		}

		if (accountAddress.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(accountAddress.getDescription()));
		}

		if (accountAddress.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(accountAddress.getExternalReferenceCode()));
		}

		if (accountAddress.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(accountAddress.getId()));
		}

		if (accountAddress.getLatitude() == null) {
			map.put("latitude", null);
		}
		else {
			map.put("latitude", String.valueOf(accountAddress.getLatitude()));
		}

		if (accountAddress.getLongitude() == null) {
			map.put("longitude", null);
		}
		else {
			map.put("longitude", String.valueOf(accountAddress.getLongitude()));
		}

		if (accountAddress.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(accountAddress.getName()));
		}

		if (accountAddress.getPhoneNumber() == null) {
			map.put("phoneNumber", null);
		}
		else {
			map.put(
				"phoneNumber", String.valueOf(accountAddress.getPhoneNumber()));
		}

		if (accountAddress.getRegionISOCode() == null) {
			map.put("regionISOCode", null);
		}
		else {
			map.put(
				"regionISOCode",
				String.valueOf(accountAddress.getRegionISOCode()));
		}

		if (accountAddress.getStreet1() == null) {
			map.put("street1", null);
		}
		else {
			map.put("street1", String.valueOf(accountAddress.getStreet1()));
		}

		if (accountAddress.getStreet2() == null) {
			map.put("street2", null);
		}
		else {
			map.put("street2", String.valueOf(accountAddress.getStreet2()));
		}

		if (accountAddress.getStreet3() == null) {
			map.put("street3", null);
		}
		else {
			map.put("street3", String.valueOf(accountAddress.getStreet3()));
		}

		if (accountAddress.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(accountAddress.getType()));
		}

		if (accountAddress.getZip() == null) {
			map.put("zip", null);
		}
		else {
			map.put("zip", String.valueOf(accountAddress.getZip()));
		}

		return map;
	}

	public static class AccountAddressJSONParser
		extends BaseJSONParser<AccountAddress> {

		@Override
		protected AccountAddress createDTO() {
			return new AccountAddress();
		}

		@Override
		protected AccountAddress[] createDTOArray(int size) {
			return new AccountAddress[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "city")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "countryISOCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultBilling")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultShipping")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "latitude")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "longitude")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "phoneNumber")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "regionISOCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "street1")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "street2")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "street3")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "zip")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AccountAddress accountAddress, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "city")) {
				if (jsonParserFieldValue != null) {
					accountAddress.setCity((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "countryISOCode")) {
				if (jsonParserFieldValue != null) {
					accountAddress.setCountryISOCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultBilling")) {
				if (jsonParserFieldValue != null) {
					accountAddress.setDefaultBilling(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultShipping")) {
				if (jsonParserFieldValue != null) {
					accountAddress.setDefaultShipping(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					accountAddress.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					accountAddress.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					accountAddress.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "latitude")) {
				if (jsonParserFieldValue != null) {
					accountAddress.setLatitude(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "longitude")) {
				if (jsonParserFieldValue != null) {
					accountAddress.setLongitude(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					accountAddress.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "phoneNumber")) {
				if (jsonParserFieldValue != null) {
					accountAddress.setPhoneNumber((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "regionISOCode")) {
				if (jsonParserFieldValue != null) {
					accountAddress.setRegionISOCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "street1")) {
				if (jsonParserFieldValue != null) {
					accountAddress.setStreet1((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "street2")) {
				if (jsonParserFieldValue != null) {
					accountAddress.setStreet2((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "street3")) {
				if (jsonParserFieldValue != null) {
					accountAddress.setStreet3((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					accountAddress.setType(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "zip")) {
				if (jsonParserFieldValue != null) {
					accountAddress.setZip((String)jsonParserFieldValue);
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