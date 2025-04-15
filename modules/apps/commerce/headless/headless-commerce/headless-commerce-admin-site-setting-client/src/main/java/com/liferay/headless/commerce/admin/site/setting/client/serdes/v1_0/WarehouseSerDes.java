/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.site.setting.client.dto.v1_0.Warehouse;
import com.liferay.headless.commerce.admin.site.setting.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Zoltán Takács
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

		if (warehouse.getCommerceCountryId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"commerceCountryId\": ");

			sb.append(warehouse.getCommerceCountryId());
		}

		if (warehouse.getCommerceRegionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"commerceRegionId\": ");

			sb.append(warehouse.getCommerceRegionId());
		}

		if (warehouse.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(warehouse.getDescription()));
		}

		if (warehouse.getGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"groupId\": ");

			sb.append(warehouse.getGroupId());
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

		if (warehouse.getMvccVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mvccVersion\": ");

			sb.append(warehouse.getMvccVersion());
		}

		if (warehouse.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(warehouse.getName()));
		}

		if (warehouse.getPrimary() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"primary\": ");

			sb.append(warehouse.getPrimary());
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

		if (warehouse.getCommerceCountryId() == null) {
			map.put("commerceCountryId", null);
		}
		else {
			map.put(
				"commerceCountryId",
				String.valueOf(warehouse.getCommerceCountryId()));
		}

		if (warehouse.getCommerceRegionId() == null) {
			map.put("commerceRegionId", null);
		}
		else {
			map.put(
				"commerceRegionId",
				String.valueOf(warehouse.getCommerceRegionId()));
		}

		if (warehouse.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(warehouse.getDescription()));
		}

		if (warehouse.getGroupId() == null) {
			map.put("groupId", null);
		}
		else {
			map.put("groupId", String.valueOf(warehouse.getGroupId()));
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

		if (warehouse.getMvccVersion() == null) {
			map.put("mvccVersion", null);
		}
		else {
			map.put("mvccVersion", String.valueOf(warehouse.getMvccVersion()));
		}

		if (warehouse.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(warehouse.getName()));
		}

		if (warehouse.getPrimary() == null) {
			map.put("primary", null);
		}
		else {
			map.put("primary", String.valueOf(warehouse.getPrimary()));
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
			if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "city")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "commerceCountryId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "commerceRegionId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "groupId")) {
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
			else if (Objects.equals(jsonParserFieldName, "mvccVersion")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
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
			else if (Objects.equals(jsonParserFieldName, "zip")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Warehouse warehouse, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					warehouse.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "city")) {
				if (jsonParserFieldValue != null) {
					warehouse.setCity((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "commerceCountryId")) {
				if (jsonParserFieldValue != null) {
					warehouse.setCommerceCountryId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "commerceRegionId")) {
				if (jsonParserFieldValue != null) {
					warehouse.setCommerceRegionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					warehouse.setDescription(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "groupId")) {
				if (jsonParserFieldValue != null) {
					warehouse.setGroupId(
						Long.valueOf((String)jsonParserFieldValue));
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
			else if (Objects.equals(jsonParserFieldName, "mvccVersion")) {
				if (jsonParserFieldValue != null) {
					warehouse.setMvccVersion(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					warehouse.setName(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
				if (jsonParserFieldValue != null) {
					warehouse.setPrimary((Boolean)jsonParserFieldValue);
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