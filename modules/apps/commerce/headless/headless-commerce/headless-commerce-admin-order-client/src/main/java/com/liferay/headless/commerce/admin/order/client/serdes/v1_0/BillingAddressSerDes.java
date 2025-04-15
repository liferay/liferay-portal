/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.order.client.dto.v1_0.BillingAddress;
import com.liferay.headless.commerce.admin.order.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class BillingAddressSerDes {

	public static BillingAddress toDTO(String json) {
		BillingAddressJSONParser billingAddressJSONParser =
			new BillingAddressJSONParser();

		return billingAddressJSONParser.parseToDTO(json);
	}

	public static BillingAddress[] toDTOs(String json) {
		BillingAddressJSONParser billingAddressJSONParser =
			new BillingAddressJSONParser();

		return billingAddressJSONParser.parseToDTOs(json);
	}

	public static String toJSON(BillingAddress billingAddress) {
		if (billingAddress == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (billingAddress.getCity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"city\": ");

			sb.append("\"");

			sb.append(_escape(billingAddress.getCity()));

			sb.append("\"");
		}

		if (billingAddress.getCountryISOCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"countryISOCode\": ");

			sb.append("\"");

			sb.append(_escape(billingAddress.getCountryISOCode()));

			sb.append("\"");
		}

		if (billingAddress.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(billingAddress.getDescription()));

			sb.append("\"");
		}

		if (billingAddress.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(billingAddress.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (billingAddress.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(billingAddress.getId());
		}

		if (billingAddress.getLatitude() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"latitude\": ");

			sb.append(billingAddress.getLatitude());
		}

		if (billingAddress.getLongitude() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"longitude\": ");

			sb.append(billingAddress.getLongitude());
		}

		if (billingAddress.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(billingAddress.getName()));

			sb.append("\"");
		}

		if (billingAddress.getPhoneNumber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"phoneNumber\": ");

			sb.append("\"");

			sb.append(_escape(billingAddress.getPhoneNumber()));

			sb.append("\"");
		}

		if (billingAddress.getRegionISOCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"regionISOCode\": ");

			sb.append("\"");

			sb.append(_escape(billingAddress.getRegionISOCode()));

			sb.append("\"");
		}

		if (billingAddress.getStreet1() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street1\": ");

			sb.append("\"");

			sb.append(_escape(billingAddress.getStreet1()));

			sb.append("\"");
		}

		if (billingAddress.getStreet2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street2\": ");

			sb.append("\"");

			sb.append(_escape(billingAddress.getStreet2()));

			sb.append("\"");
		}

		if (billingAddress.getStreet3() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street3\": ");

			sb.append("\"");

			sb.append(_escape(billingAddress.getStreet3()));

			sb.append("\"");
		}

		if (billingAddress.getSubtype() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtype\": ");

			sb.append("\"");

			sb.append(_escape(billingAddress.getSubtype()));

			sb.append("\"");
		}

		if (billingAddress.getVatNumber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"vatNumber\": ");

			sb.append("\"");

			sb.append(_escape(billingAddress.getVatNumber()));

			sb.append("\"");
		}

		if (billingAddress.getZip() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"zip\": ");

			sb.append("\"");

			sb.append(_escape(billingAddress.getZip()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		BillingAddressJSONParser billingAddressJSONParser =
			new BillingAddressJSONParser();

		return billingAddressJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(BillingAddress billingAddress) {
		if (billingAddress == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (billingAddress.getCity() == null) {
			map.put("city", null);
		}
		else {
			map.put("city", String.valueOf(billingAddress.getCity()));
		}

		if (billingAddress.getCountryISOCode() == null) {
			map.put("countryISOCode", null);
		}
		else {
			map.put(
				"countryISOCode",
				String.valueOf(billingAddress.getCountryISOCode()));
		}

		if (billingAddress.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(billingAddress.getDescription()));
		}

		if (billingAddress.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(billingAddress.getExternalReferenceCode()));
		}

		if (billingAddress.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(billingAddress.getId()));
		}

		if (billingAddress.getLatitude() == null) {
			map.put("latitude", null);
		}
		else {
			map.put("latitude", String.valueOf(billingAddress.getLatitude()));
		}

		if (billingAddress.getLongitude() == null) {
			map.put("longitude", null);
		}
		else {
			map.put("longitude", String.valueOf(billingAddress.getLongitude()));
		}

		if (billingAddress.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(billingAddress.getName()));
		}

		if (billingAddress.getPhoneNumber() == null) {
			map.put("phoneNumber", null);
		}
		else {
			map.put(
				"phoneNumber", String.valueOf(billingAddress.getPhoneNumber()));
		}

		if (billingAddress.getRegionISOCode() == null) {
			map.put("regionISOCode", null);
		}
		else {
			map.put(
				"regionISOCode",
				String.valueOf(billingAddress.getRegionISOCode()));
		}

		if (billingAddress.getStreet1() == null) {
			map.put("street1", null);
		}
		else {
			map.put("street1", String.valueOf(billingAddress.getStreet1()));
		}

		if (billingAddress.getStreet2() == null) {
			map.put("street2", null);
		}
		else {
			map.put("street2", String.valueOf(billingAddress.getStreet2()));
		}

		if (billingAddress.getStreet3() == null) {
			map.put("street3", null);
		}
		else {
			map.put("street3", String.valueOf(billingAddress.getStreet3()));
		}

		if (billingAddress.getSubtype() == null) {
			map.put("subtype", null);
		}
		else {
			map.put("subtype", String.valueOf(billingAddress.getSubtype()));
		}

		if (billingAddress.getVatNumber() == null) {
			map.put("vatNumber", null);
		}
		else {
			map.put("vatNumber", String.valueOf(billingAddress.getVatNumber()));
		}

		if (billingAddress.getZip() == null) {
			map.put("zip", null);
		}
		else {
			map.put("zip", String.valueOf(billingAddress.getZip()));
		}

		return map;
	}

	public static class BillingAddressJSONParser
		extends BaseJSONParser<BillingAddress> {

		@Override
		protected BillingAddress createDTO() {
			return new BillingAddress();
		}

		@Override
		protected BillingAddress[] createDTOArray(int size) {
			return new BillingAddress[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "city")) {
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
			BillingAddress billingAddress, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "city")) {
				if (jsonParserFieldValue != null) {
					billingAddress.setCity((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "countryISOCode")) {
				if (jsonParserFieldValue != null) {
					billingAddress.setCountryISOCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					billingAddress.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					billingAddress.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					billingAddress.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "latitude")) {
				if (jsonParserFieldValue != null) {
					billingAddress.setLatitude(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "longitude")) {
				if (jsonParserFieldValue != null) {
					billingAddress.setLongitude(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					billingAddress.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "phoneNumber")) {
				if (jsonParserFieldValue != null) {
					billingAddress.setPhoneNumber((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "regionISOCode")) {
				if (jsonParserFieldValue != null) {
					billingAddress.setRegionISOCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "street1")) {
				if (jsonParserFieldValue != null) {
					billingAddress.setStreet1((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "street2")) {
				if (jsonParserFieldValue != null) {
					billingAddress.setStreet2((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "street3")) {
				if (jsonParserFieldValue != null) {
					billingAddress.setStreet3((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subtype")) {
				if (jsonParserFieldValue != null) {
					billingAddress.setSubtype((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "vatNumber")) {
				if (jsonParserFieldValue != null) {
					billingAddress.setVatNumber((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "zip")) {
				if (jsonParserFieldValue != null) {
					billingAddress.setZip((String)jsonParserFieldValue);
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