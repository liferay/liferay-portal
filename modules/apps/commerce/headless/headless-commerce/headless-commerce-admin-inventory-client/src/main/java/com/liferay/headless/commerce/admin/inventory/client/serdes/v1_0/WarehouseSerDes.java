/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.Warehouse;
import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.WarehouseItem;
import com.liferay.headless.commerce.admin.inventory.client.json.BaseJSONParser;

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
public class WarehouseSerDes {

	public static Warehouse toDTO(String json) {
		WarehouseJSONParser warehouseJSONParser = new WarehouseJSONParser();

		return warehouseJSONParser.parseToDTO(json);
	}

	public static Warehouse[] toDTOs(String json) {
		WarehouseJSONParser warehouseJSONParser = new WarehouseJSONParser();

		return warehouseJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Warehouse warehouse) {
		if (warehouse == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (warehouse.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(warehouse.getActions()));
		}

		if (warehouse.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(warehouse.getActive());
		}

		if (warehouse.getCity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"city\": ");

			sb.append("\"");

			sb.append(_escape(warehouse.getCity()));

			sb.append("\"");
		}

		if (warehouse.getCountryISOCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"countryISOCode\": ");

			sb.append("\"");

			sb.append(_escape(warehouse.getCountryISOCode()));

			sb.append("\"");
		}

		if (warehouse.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(warehouse.getDescription()));
		}

		if (warehouse.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(warehouse.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (warehouse.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(warehouse.getId());
		}

		if (warehouse.getLatitude() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"latitude\": ");

			sb.append(warehouse.getLatitude());
		}

		if (warehouse.getLongitude() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"longitude\": ");

			sb.append(warehouse.getLongitude());
		}

		if (warehouse.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(warehouse.getName()));
		}

		if (warehouse.getRegionISOCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"regionISOCode\": ");

			sb.append("\"");

			sb.append(_escape(warehouse.getRegionISOCode()));

			sb.append("\"");
		}

		if (warehouse.getStreet1() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street1\": ");

			sb.append("\"");

			sb.append(_escape(warehouse.getStreet1()));

			sb.append("\"");
		}

		if (warehouse.getStreet2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street2\": ");

			sb.append("\"");

			sb.append(_escape(warehouse.getStreet2()));

			sb.append("\"");
		}

		if (warehouse.getStreet3() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"street3\": ");

			sb.append("\"");

			sb.append(_escape(warehouse.getStreet3()));

			sb.append("\"");
		}

		if (warehouse.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(warehouse.getType()));

			sb.append("\"");
		}

		if (warehouse.getWarehouseItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseItems\": ");

			sb.append("[");

			for (int i = 0; i < warehouse.getWarehouseItems().length; i++) {
				sb.append(String.valueOf(warehouse.getWarehouseItems()[i]));

				if ((i + 1) < warehouse.getWarehouseItems().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (warehouse.getZip() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"zip\": ");

			sb.append("\"");

			sb.append(_escape(warehouse.getZip()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WarehouseJSONParser warehouseJSONParser = new WarehouseJSONParser();

		return warehouseJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Warehouse warehouse) {
		if (warehouse == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (warehouse.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(warehouse.getActions()));
		}

		if (warehouse.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(warehouse.getActive()));
		}

		if (warehouse.getCity() == null) {
			map.put("city", null);
		}
		else {
			map.put("city", String.valueOf(warehouse.getCity()));
		}

		if (warehouse.getCountryISOCode() == null) {
			map.put("countryISOCode", null);
		}
		else {
			map.put(
				"countryISOCode",
				String.valueOf(warehouse.getCountryISOCode()));
		}

		if (warehouse.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(warehouse.getDescription()));
		}

		if (warehouse.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(warehouse.getExternalReferenceCode()));
		}

		if (warehouse.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(warehouse.getId()));
		}

		if (warehouse.getLatitude() == null) {
			map.put("latitude", null);
		}
		else {
			map.put("latitude", String.valueOf(warehouse.getLatitude()));
		}

		if (warehouse.getLongitude() == null) {
			map.put("longitude", null);
		}
		else {
			map.put("longitude", String.valueOf(warehouse.getLongitude()));
		}

		if (warehouse.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(warehouse.getName()));
		}

		if (warehouse.getRegionISOCode() == null) {
			map.put("regionISOCode", null);
		}
		else {
			map.put(
				"regionISOCode", String.valueOf(warehouse.getRegionISOCode()));
		}

		if (warehouse.getStreet1() == null) {
			map.put("street1", null);
		}
		else {
			map.put("street1", String.valueOf(warehouse.getStreet1()));
		}

		if (warehouse.getStreet2() == null) {
			map.put("street2", null);
		}
		else {
			map.put("street2", String.valueOf(warehouse.getStreet2()));
		}

		if (warehouse.getStreet3() == null) {
			map.put("street3", null);
		}
		else {
			map.put("street3", String.valueOf(warehouse.getStreet3()));
		}

		if (warehouse.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(warehouse.getType()));
		}

		if (warehouse.getWarehouseItems() == null) {
			map.put("warehouseItems", null);
		}
		else {
			map.put(
				"warehouseItems",
				String.valueOf(warehouse.getWarehouseItems()));
		}

		if (warehouse.getZip() == null) {
			map.put("zip", null);
		}
		else {
			map.put("zip", String.valueOf(warehouse.getZip()));
		}

		return map;
	}

	public static class WarehouseJSONParser extends BaseJSONParser<Warehouse> {

		@Override
		protected Warehouse createDTO() {
			return new Warehouse();
		}

		@Override
		protected Warehouse[] createDTOArray(int size) {
			return new Warehouse[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "city")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "countryISOCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return true;
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
				return true;
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
			else if (Objects.equals(jsonParserFieldName, "warehouseItems")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "zip")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Warehouse warehouse, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					warehouse.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					warehouse.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "city")) {
				if (jsonParserFieldValue != null) {
					warehouse.setCity((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "countryISOCode")) {
				if (jsonParserFieldValue != null) {
					warehouse.setCountryISOCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					warehouse.setDescription(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					warehouse.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					warehouse.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "latitude")) {
				if (jsonParserFieldValue != null) {
					warehouse.setLatitude(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "longitude")) {
				if (jsonParserFieldValue != null) {
					warehouse.setLongitude(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					warehouse.setName(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "regionISOCode")) {
				if (jsonParserFieldValue != null) {
					warehouse.setRegionISOCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "street1")) {
				if (jsonParserFieldValue != null) {
					warehouse.setStreet1((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "street2")) {
				if (jsonParserFieldValue != null) {
					warehouse.setStreet2((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "street3")) {
				if (jsonParserFieldValue != null) {
					warehouse.setStreet3((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					warehouse.setType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "warehouseItems")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					WarehouseItem[] warehouseItemsArray =
						new WarehouseItem[jsonParserFieldValues.length];

					for (int i = 0; i < warehouseItemsArray.length; i++) {
						warehouseItemsArray[i] = WarehouseItemSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					warehouse.setWarehouseItems(warehouseItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "zip")) {
				if (jsonParserFieldValue != null) {
					warehouse.setZip((String)jsonParserFieldValue);
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