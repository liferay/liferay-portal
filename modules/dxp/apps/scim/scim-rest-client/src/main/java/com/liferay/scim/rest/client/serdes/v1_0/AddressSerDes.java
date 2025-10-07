/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.Address;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class AddressSerDes {

	public static Address toDTO(String json) {
		AddressJSONParser addressJSONParser = new AddressJSONParser();

		return addressJSONParser.parseToDTO(json);
	}

	public static Address[] toDTOs(String json) {
		AddressJSONParser addressJSONParser = new AddressJSONParser();

		return addressJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Address address) {
		if (address == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (address.getCountry() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"country\": ");

			sb.append("\"");

			sb.append(_escape(address.getCountry()));

			sb.append("\"");
		}

		if (address.getFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formatted\": ");

			sb.append("\"");

			sb.append(_escape(address.getFormatted()));

			sb.append("\"");
		}

		if (address.getLocality() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"locality\": ");

			sb.append("\"");

			sb.append(_escape(address.getLocality()));

			sb.append("\"");
		}

		if (address.getPostalCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"postalCode\": ");

			sb.append("\"");

			sb.append(_escape(address.getPostalCode()));

			sb.append("\"");
		}

		if (address.getPrimary() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"primary\": ");

			sb.append(address.getPrimary());
		}

		if (address.getRegion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"region\": ");

			sb.append("\"");

			sb.append(_escape(address.getRegion()));

			sb.append("\"");
		}

		if (address.getStreetAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"streetAddress\": ");

			sb.append("\"");

			sb.append(_escape(address.getStreetAddress()));

			sb.append("\"");
		}

		if (address.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(address.getType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AddressJSONParser addressJSONParser = new AddressJSONParser();

		return addressJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Address address) {
		if (address == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (address.getCountry() == null) {
			map.put("country", null);
		}
		else {
			map.put("country", String.valueOf(address.getCountry()));
		}

		if (address.getFormatted() == null) {
			map.put("formatted", null);
		}
		else {
			map.put("formatted", String.valueOf(address.getFormatted()));
		}

		if (address.getLocality() == null) {
			map.put("locality", null);
		}
		else {
			map.put("locality", String.valueOf(address.getLocality()));
		}

		if (address.getPostalCode() == null) {
			map.put("postalCode", null);
		}
		else {
			map.put("postalCode", String.valueOf(address.getPostalCode()));
		}

		if (address.getPrimary() == null) {
			map.put("primary", null);
		}
		else {
			map.put("primary", String.valueOf(address.getPrimary()));
		}

		if (address.getRegion() == null) {
			map.put("region", null);
		}
		else {
			map.put("region", String.valueOf(address.getRegion()));
		}

		if (address.getStreetAddress() == null) {
			map.put("streetAddress", null);
		}
		else {
			map.put(
				"streetAddress", String.valueOf(address.getStreetAddress()));
		}

		if (address.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(address.getType()));
		}

		return map;
	}

	public static class AddressJSONParser extends BaseJSONParser<Address> {

		@Override
		protected Address createDTO() {
			return new Address();
		}

		@Override
		protected Address[] createDTOArray(int size) {
			return new Address[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "country")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "formatted")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "locality")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "postalCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "region")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "streetAddress")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Address address, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "country")) {
				if (jsonParserFieldValue != null) {
					address.setCountry((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "formatted")) {
				if (jsonParserFieldValue != null) {
					address.setFormatted((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "locality")) {
				if (jsonParserFieldValue != null) {
					address.setLocality((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "postalCode")) {
				if (jsonParserFieldValue != null) {
					address.setPostalCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
				if (jsonParserFieldValue != null) {
					address.setPrimary((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "region")) {
				if (jsonParserFieldValue != null) {
					address.setRegion((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "streetAddress")) {
				if (jsonParserFieldValue != null) {
					address.setStreetAddress((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					address.setType((String)jsonParserFieldValue);
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