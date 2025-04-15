/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.WarehouseOrderType;
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
public class WarehouseOrderTypeSerDes {

	public static WarehouseOrderType toDTO(String json) {
		WarehouseOrderTypeJSONParser warehouseOrderTypeJSONParser =
			new WarehouseOrderTypeJSONParser();

		return warehouseOrderTypeJSONParser.parseToDTO(json);
	}

	public static WarehouseOrderType[] toDTOs(String json) {
		WarehouseOrderTypeJSONParser warehouseOrderTypeJSONParser =
			new WarehouseOrderTypeJSONParser();

		return warehouseOrderTypeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WarehouseOrderType warehouseOrderType) {
		if (warehouseOrderType == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (warehouseOrderType.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(warehouseOrderType.getActions()));
		}

		if (warehouseOrderType.getOrderType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderType\": ");

			sb.append(String.valueOf(warehouseOrderType.getOrderType()));
		}

		if (warehouseOrderType.getOrderTypeExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					warehouseOrderType.getOrderTypeExternalReferenceCode()));

			sb.append("\"");
		}

		if (warehouseOrderType.getOrderTypeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeId\": ");

			sb.append(warehouseOrderType.getOrderTypeId());
		}

		if (warehouseOrderType.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(warehouseOrderType.getPriority());
		}

		if (warehouseOrderType.getWarehouseExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					warehouseOrderType.getWarehouseExternalReferenceCode()));

			sb.append("\"");
		}

		if (warehouseOrderType.getWarehouseId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseId\": ");

			sb.append(warehouseOrderType.getWarehouseId());
		}

		if (warehouseOrderType.getWarehouseOrderTypeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseOrderTypeId\": ");

			sb.append(warehouseOrderType.getWarehouseOrderTypeId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WarehouseOrderTypeJSONParser warehouseOrderTypeJSONParser =
			new WarehouseOrderTypeJSONParser();

		return warehouseOrderTypeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WarehouseOrderType warehouseOrderType) {

		if (warehouseOrderType == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (warehouseOrderType.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(warehouseOrderType.getActions()));
		}

		if (warehouseOrderType.getOrderType() == null) {
			map.put("orderType", null);
		}
		else {
			map.put(
				"orderType", String.valueOf(warehouseOrderType.getOrderType()));
		}

		if (warehouseOrderType.getOrderTypeExternalReferenceCode() == null) {
			map.put("orderTypeExternalReferenceCode", null);
		}
		else {
			map.put(
				"orderTypeExternalReferenceCode",
				String.valueOf(
					warehouseOrderType.getOrderTypeExternalReferenceCode()));
		}

		if (warehouseOrderType.getOrderTypeId() == null) {
			map.put("orderTypeId", null);
		}
		else {
			map.put(
				"orderTypeId",
				String.valueOf(warehouseOrderType.getOrderTypeId()));
		}

		if (warehouseOrderType.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put(
				"priority", String.valueOf(warehouseOrderType.getPriority()));
		}

		if (warehouseOrderType.getWarehouseExternalReferenceCode() == null) {
			map.put("warehouseExternalReferenceCode", null);
		}
		else {
			map.put(
				"warehouseExternalReferenceCode",
				String.valueOf(
					warehouseOrderType.getWarehouseExternalReferenceCode()));
		}

		if (warehouseOrderType.getWarehouseId() == null) {
			map.put("warehouseId", null);
		}
		else {
			map.put(
				"warehouseId",
				String.valueOf(warehouseOrderType.getWarehouseId()));
		}

		if (warehouseOrderType.getWarehouseOrderTypeId() == null) {
			map.put("warehouseOrderTypeId", null);
		}
		else {
			map.put(
				"warehouseOrderTypeId",
				String.valueOf(warehouseOrderType.getWarehouseOrderTypeId()));
		}

		return map;
	}

	public static class WarehouseOrderTypeJSONParser
		extends BaseJSONParser<WarehouseOrderType> {

		@Override
		protected WarehouseOrderType createDTO() {
			return new WarehouseOrderType();
		}

		@Override
		protected WarehouseOrderType[] createDTOArray(int size) {
			return new WarehouseOrderType[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "orderType")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"orderTypeExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderTypeId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"warehouseExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "warehouseId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "warehouseOrderTypeId")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WarehouseOrderType warehouseOrderType, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					warehouseOrderType.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderType")) {
				if (jsonParserFieldValue != null) {
					warehouseOrderType.setOrderType(
						OrderTypeSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"orderTypeExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					warehouseOrderType.setOrderTypeExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderTypeId")) {
				if (jsonParserFieldValue != null) {
					warehouseOrderType.setOrderTypeId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					warehouseOrderType.setPriority(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"warehouseExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					warehouseOrderType.setWarehouseExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "warehouseId")) {
				if (jsonParserFieldValue != null) {
					warehouseOrderType.setWarehouseId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "warehouseOrderTypeId")) {

				if (jsonParserFieldValue != null) {
					warehouseOrderType.setWarehouseOrderTypeId(
						Long.valueOf((String)jsonParserFieldValue));
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