/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.PlacedOrderAddress;
import com.liferay.headless.commerce.delivery.order.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class PlacedOrderAddressSerDes {

	public static PlacedOrderAddress toDTO(String json) {
		PlacedOrderAddressJSONParser placedOrderAddressJSONParser =
			new PlacedOrderAddressJSONParser();

		return placedOrderAddressJSONParser.parseToDTO(json);
	}

	public static PlacedOrderAddress[] toDTOs(String json) {
		PlacedOrderAddressJSONParser placedOrderAddressJSONParser =
			new PlacedOrderAddressJSONParser();

		return placedOrderAddressJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PlacedOrderAddress placedOrderAddress) {
		if (placedOrderAddress == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (placedOrderAddress.getCity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"city\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderAddress.getCity()));

			sb.append("\"");
		}

		if (placedOrderAddress.getCountry() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"country\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderAddress.getCountry()));

			sb.append("\"");
		}

		if (placedOrderAddress.getCountryISOCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"countryISOCode\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderAddress.getCountryISOCode()));

			sb.append("\"");
		}

		if (placedOrderAddress.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderAddress.getDescription()));

			sb.append("\"");
		}

		if (placedOrderAddress.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderAddress.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (placedOrderAddress.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(placedOrderAddress.getId());
		}

		if (placedOrderAddress.getLatitude() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"latitude\": ");

			sb.append(placedOrderAddress.getLatitude());
		}

		if (placedOrderAddress.getLongitude() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"longitude\": ");

			sb.append(placedOrderAddress.getLongitude());
		}

		if (placedOrderAddress.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderAddress.getName()));

			sb.append("\"");
		}

		if (placedOrderAddress.getPhoneNumber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"phoneNumber\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderAddress.getPhoneNumber()));

			sb.append("\"");
		}

		if (placedOrderAddress.getRegion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"region\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderAddress.getRegion()));

			sb.append("\"");
		}

		if (placedOrderAddress.getRegionISOCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"regionISOCode\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderAddress.getRegionISOCode()));

			sb.append("\"");
		}

		if (placedOrderAddress.getStreet1() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street1\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderAddress.getStreet1()));

			sb.append("\"");
		}

		if (placedOrderAddress.getStreet2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street2\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderAddress.getStreet2()));

			sb.append("\"");
		}

		if (placedOrderAddress.getStreet3() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street3\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderAddress.getStreet3()));

			sb.append("\"");
		}

		if (placedOrderAddress.getSubtype() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtype\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderAddress.getSubtype()));

			sb.append("\"");
		}

		if (placedOrderAddress.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderAddress.getType()));

			sb.append("\"");
		}

		if (placedOrderAddress.getTypeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"typeId\": ");

			sb.append(placedOrderAddress.getTypeId());
		}

		if (placedOrderAddress.getVatNumber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"vatNumber\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderAddress.getVatNumber()));

			sb.append("\"");
		}

		if (placedOrderAddress.getZip() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"zip\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderAddress.getZip()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PlacedOrderAddressJSONParser placedOrderAddressJSONParser =
			new PlacedOrderAddressJSONParser();

		return placedOrderAddressJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PlacedOrderAddress placedOrderAddress) {

		if (placedOrderAddress == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (placedOrderAddress.getCity() == null) {
			map.put("city", null);
		}
		else {
			map.put("city", String.valueOf(placedOrderAddress.getCity()));
		}

		if (placedOrderAddress.getCountry() == null) {
			map.put("country", null);
		}
		else {
			map.put("country", String.valueOf(placedOrderAddress.getCountry()));
		}

		if (placedOrderAddress.getCountryISOCode() == null) {
			map.put("countryISOCode", null);
		}
		else {
			map.put(
				"countryISOCode",
				String.valueOf(placedOrderAddress.getCountryISOCode()));
		}

		if (placedOrderAddress.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(placedOrderAddress.getDescription()));
		}

		if (placedOrderAddress.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(placedOrderAddress.getExternalReferenceCode()));
		}

		if (placedOrderAddress.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(placedOrderAddress.getId()));
		}

		if (placedOrderAddress.getLatitude() == null) {
			map.put("latitude", null);
		}
		else {
			map.put(
				"latitude", String.valueOf(placedOrderAddress.getLatitude()));
		}

		if (placedOrderAddress.getLongitude() == null) {
			map.put("longitude", null);
		}
		else {
			map.put(
				"longitude", String.valueOf(placedOrderAddress.getLongitude()));
		}

		if (placedOrderAddress.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(placedOrderAddress.getName()));
		}

		if (placedOrderAddress.getPhoneNumber() == null) {
			map.put("phoneNumber", null);
		}
		else {
			map.put(
				"phoneNumber",
				String.valueOf(placedOrderAddress.getPhoneNumber()));
		}

		if (placedOrderAddress.getRegion() == null) {
			map.put("region", null);
		}
		else {
			map.put("region", String.valueOf(placedOrderAddress.getRegion()));
		}

		if (placedOrderAddress.getRegionISOCode() == null) {
			map.put("regionISOCode", null);
		}
		else {
			map.put(
				"regionISOCode",
				String.valueOf(placedOrderAddress.getRegionISOCode()));
		}

		if (placedOrderAddress.getStreet1() == null) {
			map.put("street1", null);
		}
		else {
			map.put("street1", String.valueOf(placedOrderAddress.getStreet1()));
		}

		if (placedOrderAddress.getStreet2() == null) {
			map.put("street2", null);
		}
		else {
			map.put("street2", String.valueOf(placedOrderAddress.getStreet2()));
		}

		if (placedOrderAddress.getStreet3() == null) {
			map.put("street3", null);
		}
		else {
			map.put("street3", String.valueOf(placedOrderAddress.getStreet3()));
		}

		if (placedOrderAddress.getSubtype() == null) {
			map.put("subtype", null);
		}
		else {
			map.put("subtype", String.valueOf(placedOrderAddress.getSubtype()));
		}

		if (placedOrderAddress.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(placedOrderAddress.getType()));
		}

		if (placedOrderAddress.getTypeId() == null) {
			map.put("typeId", null);
		}
		else {
			map.put("typeId", String.valueOf(placedOrderAddress.getTypeId()));
		}

		if (placedOrderAddress.getVatNumber() == null) {
			map.put("vatNumber", null);
		}
		else {
			map.put(
				"vatNumber", String.valueOf(placedOrderAddress.getVatNumber()));
		}

		if (placedOrderAddress.getZip() == null) {
			map.put("zip", null);
		}
		else {
			map.put("zip", String.valueOf(placedOrderAddress.getZip()));
		}

		return map;
	}

	public static class PlacedOrderAddressJSONParser
		extends BaseJSONParser<PlacedOrderAddress> {

		@Override
		protected PlacedOrderAddress createDTO() {
			return new PlacedOrderAddress();
		}

		@Override
		protected PlacedOrderAddress[] createDTOArray(int size) {
			return new PlacedOrderAddress[size];
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
			PlacedOrderAddress placedOrderAddress, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "city")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setCity((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "country")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setCountry((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "countryISOCode")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setCountryISOCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					placedOrderAddress.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "latitude")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setLatitude(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "longitude")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setLongitude(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "phoneNumber")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setPhoneNumber(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "region")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setRegion((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "regionISOCode")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setRegionISOCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "street1")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setStreet1((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "street2")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setStreet2((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "street3")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setStreet3((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subtype")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setSubtype((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "typeId")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setTypeId(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "vatNumber")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setVatNumber(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "zip")) {
				if (jsonParserFieldValue != null) {
					placedOrderAddress.setZip((String)jsonParserFieldValue);
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