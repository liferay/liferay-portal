/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.WarehouseAccountGroup;
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
public class WarehouseAccountGroupSerDes {

	public static WarehouseAccountGroup toDTO(String json) {
		WarehouseAccountGroupJSONParser warehouseAccountGroupJSONParser =
			new WarehouseAccountGroupJSONParser();

		return warehouseAccountGroupJSONParser.parseToDTO(json);
	}

	public static WarehouseAccountGroup[] toDTOs(String json) {
		WarehouseAccountGroupJSONParser warehouseAccountGroupJSONParser =
			new WarehouseAccountGroupJSONParser();

		return warehouseAccountGroupJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WarehouseAccountGroup warehouseAccountGroup) {
		if (warehouseAccountGroup == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (warehouseAccountGroup.getAccountGroup() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroup\": ");

			sb.append(String.valueOf(warehouseAccountGroup.getAccountGroup()));
		}

		if (warehouseAccountGroup.getAccountGroupExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroupExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					warehouseAccountGroup.
						getAccountGroupExternalReferenceCode()));

			sb.append("\"");
		}

		if (warehouseAccountGroup.getAccountGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroupId\": ");

			sb.append(warehouseAccountGroup.getAccountGroupId());
		}

		if (warehouseAccountGroup.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(warehouseAccountGroup.getActions()));
		}

		if (warehouseAccountGroup.getWarehouseAccountGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseAccountGroupId\": ");

			sb.append(warehouseAccountGroup.getWarehouseAccountGroupId());
		}

		if (warehouseAccountGroup.getWarehouseExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					warehouseAccountGroup.getWarehouseExternalReferenceCode()));

			sb.append("\"");
		}

		if (warehouseAccountGroup.getWarehouseId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseId\": ");

			sb.append(warehouseAccountGroup.getWarehouseId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WarehouseAccountGroupJSONParser warehouseAccountGroupJSONParser =
			new WarehouseAccountGroupJSONParser();

		return warehouseAccountGroupJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WarehouseAccountGroup warehouseAccountGroup) {

		if (warehouseAccountGroup == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (warehouseAccountGroup.getAccountGroup() == null) {
			map.put("accountGroup", null);
		}
		else {
			map.put(
				"accountGroup",
				String.valueOf(warehouseAccountGroup.getAccountGroup()));
		}

		if (warehouseAccountGroup.getAccountGroupExternalReferenceCode() ==
				null) {

			map.put("accountGroupExternalReferenceCode", null);
		}
		else {
			map.put(
				"accountGroupExternalReferenceCode",
				String.valueOf(
					warehouseAccountGroup.
						getAccountGroupExternalReferenceCode()));
		}

		if (warehouseAccountGroup.getAccountGroupId() == null) {
			map.put("accountGroupId", null);
		}
		else {
			map.put(
				"accountGroupId",
				String.valueOf(warehouseAccountGroup.getAccountGroupId()));
		}

		if (warehouseAccountGroup.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions", String.valueOf(warehouseAccountGroup.getActions()));
		}

		if (warehouseAccountGroup.getWarehouseAccountGroupId() == null) {
			map.put("warehouseAccountGroupId", null);
		}
		else {
			map.put(
				"warehouseAccountGroupId",
				String.valueOf(
					warehouseAccountGroup.getWarehouseAccountGroupId()));
		}

		if (warehouseAccountGroup.getWarehouseExternalReferenceCode() == null) {
			map.put("warehouseExternalReferenceCode", null);
		}
		else {
			map.put(
				"warehouseExternalReferenceCode",
				String.valueOf(
					warehouseAccountGroup.getWarehouseExternalReferenceCode()));
		}

		if (warehouseAccountGroup.getWarehouseId() == null) {
			map.put("warehouseId", null);
		}
		else {
			map.put(
				"warehouseId",
				String.valueOf(warehouseAccountGroup.getWarehouseId()));
		}

		return map;
	}

	public static class WarehouseAccountGroupJSONParser
		extends BaseJSONParser<WarehouseAccountGroup> {

		@Override
		protected WarehouseAccountGroup createDTO() {
			return new WarehouseAccountGroup();
		}

		@Override
		protected WarehouseAccountGroup[] createDTOArray(int size) {
			return new WarehouseAccountGroup[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accountGroup")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"accountGroupExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "accountGroupId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "warehouseAccountGroupId")) {

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

			return false;
		}

		@Override
		protected void setField(
			WarehouseAccountGroup warehouseAccountGroup,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountGroup")) {
				if (jsonParserFieldValue != null) {
					warehouseAccountGroup.setAccountGroup(
						AccountGroupSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"accountGroupExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					warehouseAccountGroup.setAccountGroupExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "accountGroupId")) {
				if (jsonParserFieldValue != null) {
					warehouseAccountGroup.setAccountGroupId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					warehouseAccountGroup.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "warehouseAccountGroupId")) {

				if (jsonParserFieldValue != null) {
					warehouseAccountGroup.setWarehouseAccountGroupId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"warehouseExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					warehouseAccountGroup.setWarehouseExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "warehouseId")) {
				if (jsonParserFieldValue != null) {
					warehouseAccountGroup.setWarehouseId(
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