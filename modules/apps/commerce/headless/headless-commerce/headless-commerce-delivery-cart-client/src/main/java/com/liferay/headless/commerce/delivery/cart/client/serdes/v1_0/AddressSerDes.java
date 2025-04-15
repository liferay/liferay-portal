/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.Address;
import com.liferay.headless.commerce.delivery.cart.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Andrea Sbarra
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

		if (address.getCity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"city\": ");

			sb.append("\"");

			sb.append(_escape(address.getCity()));

			sb.append("\"");
		}

		if (address.getCountry() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"country\": ");

			sb.append("\"");

			sb.append(_escape(address.getCountry()));

			sb.append("\"");
		}

		if (address.getCountryISOCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"countryISOCode\": ");

			sb.append("\"");

			sb.append(_escape(address.getCountryISOCode()));

			sb.append("\"");
		}

		if (address.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(address.getDescription()));

			sb.append("\"");
		}

		if (address.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(address.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (address.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(address.getId());
		}

		if (address.getLatitude() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"latitude\": ");

			sb.append(address.getLatitude());
		}

		if (address.getLongitude() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"longitude\": ");

			sb.append(address.getLongitude());
		}

		if (address.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(address.getName()));

			sb.append("\"");
		}

		if (address.getPhoneNumber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"phoneNumber\": ");

			sb.append("\"");

			sb.append(_escape(address.getPhoneNumber()));

			sb.append("\"");
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

		if (address.getRegionISOCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"regionISOCode\": ");

			sb.append("\"");

			sb.append(_escape(address.getRegionISOCode()));

			sb.append("\"");
		}

		if (address.getStreet1() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street1\": ");

			sb.append("\"");

			sb.append(_escape(address.getStreet1()));

			sb.append("\"");
		}

		if (address.getStreet2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street2\": ");

			sb.append("\"");

			sb.append(_escape(address.getStreet2()));

			sb.append("\"");
		}

		if (address.getStreet3() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street3\": ");

			sb.append("\"");

			sb.append(_escape(address.getStreet3()));

			sb.append("\"");
		}

		if (address.getSubtype() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtype\": ");

			sb.append("\"");

			sb.append(_escape(address.getSubtype()));

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

		if (address.getTypeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"typeId\": ");

			sb.append(address.getTypeId());
		}

		if (address.getVatNumber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"vatNumber\": ");

			sb.append("\"");

			sb.append(_escape(address.getVatNumber()));

			sb.append("\"");
		}

		if (address.getZip() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"zip\": ");

			sb.append("\"");

			sb.append(_escape(address.getZip()));

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

		if (address.getCity() == null) {
			map.put("city", null);
		}
		else {
			map.put("city", String.valueOf(address.getCity()));
		}

		if (address.getCountry() == null) {
			map.put("country", null);
		}
		else {
			map.put("country", String.valueOf(address.getCountry()));
		}

		if (address.getCountryISOCode() == null) {
			map.put("countryISOCode", null);
		}
		else {
			map.put(
				"countryISOCode", String.valueOf(address.getCountryISOCode()));
		}

		if (address.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(address.getDescription()));
		}

		if (address.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(address.getExternalReferenceCode()));
		}

		if (address.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(address.getId()));
		}

		if (address.getLatitude() == null) {
			map.put("latitude", null);
		}
		else {
			map.put("latitude", String.valueOf(address.getLatitude()));
		}

		if (address.getLongitude() == null) {
			map.put("longitude", null);
		}
		else {
			map.put("longitude", String.valueOf(address.getLongitude()));
		}

		if (address.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(address.getName()));
		}

		if (address.getPhoneNumber() == null) {
			map.put("phoneNumber", null);
		}
		else {
			map.put("phoneNumber", String.valueOf(address.getPhoneNumber()));
		}

		if (address.getRegion() == null) {
			map.put("region", null);
		}
		else {
			map.put("region", String.valueOf(address.getRegion()));
		}

		if (address.getRegionISOCode() == null) {
			map.put("regionISOCode", null);
		}
		else {
			map.put(
				"regionISOCode", String.valueOf(address.getRegionISOCode()));
		}

		if (address.getStreet1() == null) {
			map.put("street1", null);
		}
		else {
			map.put("street1", String.valueOf(address.getStreet1()));
		}

		if (address.getStreet2() == null) {
			map.put("street2", null);
		}
		else {
			map.put("street2", String.valueOf(address.getStreet2()));
		}

		if (address.getStreet3() == null) {
			map.put("street3", null);
		}
		else {
			map.put("street3", String.valueOf(address.getStreet3()));
		}

		if (address.getSubtype() == null) {
			map.put("subtype", null);
		}
		else {
			map.put("subtype", String.valueOf(address.getSubtype()));
		}

		if (address.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(address.getType()));
		}

		if (address.getTypeId() == null) {
			map.put("typeId", null);
		}
		else {
			map.put("typeId", String.valueOf(address.getTypeId()));
		}

		if (address.getVatNumber() == null) {
			map.put("vatNumber", null);
		}
		else {
			map.put("vatNumber", String.valueOf(address.getVatNumber()));
		}

		if (address.getZip() == null) {
			map.put("zip", null);
		}
		else {
			map.put("zip", String.valueOf(address.getZip()));
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
			if (Objects.equals(jsonParserFieldName, "city")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "country")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "countryISOCode")) {
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
			else if (Objects.equals(jsonParserFieldName, "region")) {
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
			else if (Objects.equals(jsonParserFieldName, "subtype")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "typeId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "vatNumber")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "zip")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Address address, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "city")) {
				if (jsonParserFieldValue != null) {
					address.setCity((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "country")) {
				if (jsonParserFieldValue != null) {
					address.setCountry((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "countryISOCode")) {
				if (jsonParserFieldValue != null) {
					address.setCountryISOCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					address.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					address.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					address.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "latitude")) {
				if (jsonParserFieldValue != null) {
					address.setLatitude(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "longitude")) {
				if (jsonParserFieldValue != null) {
					address.setLongitude(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					address.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "phoneNumber")) {
				if (jsonParserFieldValue != null) {
					address.setPhoneNumber((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "region")) {
				if (jsonParserFieldValue != null) {
					address.setRegion((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "regionISOCode")) {
				if (jsonParserFieldValue != null) {
					address.setRegionISOCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "street1")) {
				if (jsonParserFieldValue != null) {
					address.setStreet1((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "street2")) {
				if (jsonParserFieldValue != null) {
					address.setStreet2((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "street3")) {
				if (jsonParserFieldValue != null) {
					address.setStreet3((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subtype")) {
				if (jsonParserFieldValue != null) {
					address.setSubtype((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					address.setType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "typeId")) {
				if (jsonParserFieldValue != null) {
					address.setTypeId(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "vatNumber")) {
				if (jsonParserFieldValue != null) {
					address.setVatNumber((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "zip")) {
				if (jsonParserFieldValue != null) {
					address.setZip((String)jsonParserFieldValue);
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