/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.ReplenishmentItem;
import com.liferay.headless.commerce.admin.inventory.client.json.BaseJSONParser;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class ReplenishmentItemSerDes {

	public static ReplenishmentItem toDTO(String json) {
		ReplenishmentItemJSONParser replenishmentItemJSONParser =
			new ReplenishmentItemJSONParser();

		return replenishmentItemJSONParser.parseToDTO(json);
	}

	public static ReplenishmentItem[] toDTOs(String json) {
		ReplenishmentItemJSONParser replenishmentItemJSONParser =
			new ReplenishmentItemJSONParser();

		return replenishmentItemJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ReplenishmentItem replenishmentItem) {
		if (replenishmentItem == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (replenishmentItem.getAvailabilityDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availabilityDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					replenishmentItem.getAvailabilityDate()));

			sb.append("\"");
		}

		if (replenishmentItem.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(replenishmentItem.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (replenishmentItem.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(replenishmentItem.getId());
		}

		if (replenishmentItem.getQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"quantity\": ");

			sb.append(replenishmentItem.getQuantity());
		}

		if (replenishmentItem.getSku() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sku\": ");

			sb.append("\"");

			sb.append(_escape(replenishmentItem.getSku()));

			sb.append("\"");
		}

		if (replenishmentItem.getUnitOfMeasureKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitOfMeasureKey\": ");

			sb.append("\"");

			sb.append(_escape(replenishmentItem.getUnitOfMeasureKey()));

			sb.append("\"");
		}

		if (replenishmentItem.getWarehouseId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseId\": ");

			sb.append(replenishmentItem.getWarehouseId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ReplenishmentItemJSONParser replenishmentItemJSONParser =
			new ReplenishmentItemJSONParser();

		return replenishmentItemJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ReplenishmentItem replenishmentItem) {

		if (replenishmentItem == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (replenishmentItem.getAvailabilityDate() == null) {
			map.put("availabilityDate", null);
		}
		else {
			map.put(
				"availabilityDate",
				liferayToJSONDateFormat.format(
					replenishmentItem.getAvailabilityDate()));
		}

		if (replenishmentItem.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(replenishmentItem.getExternalReferenceCode()));
		}

		if (replenishmentItem.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(replenishmentItem.getId()));
		}

		if (replenishmentItem.getQuantity() == null) {
			map.put("quantity", null);
		}
		else {
			map.put(
				"quantity", String.valueOf(replenishmentItem.getQuantity()));
		}

		if (replenishmentItem.getSku() == null) {
			map.put("sku", null);
		}
		else {
			map.put("sku", String.valueOf(replenishmentItem.getSku()));
		}

		if (replenishmentItem.getUnitOfMeasureKey() == null) {
			map.put("unitOfMeasureKey", null);
		}
		else {
			map.put(
				"unitOfMeasureKey",
				String.valueOf(replenishmentItem.getUnitOfMeasureKey()));
		}

		if (replenishmentItem.getWarehouseId() == null) {
			map.put("warehouseId", null);
		}
		else {
			map.put(
				"warehouseId",
				String.valueOf(replenishmentItem.getWarehouseId()));
		}

		return map;
	}

	public static class ReplenishmentItemJSONParser
		extends BaseJSONParser<ReplenishmentItem> {

		@Override
		protected ReplenishmentItem createDTO() {
			return new ReplenishmentItem();
		}

		@Override
		protected ReplenishmentItem[] createDTOArray(int size) {
			return new ReplenishmentItem[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "availabilityDate")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "quantity")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sku")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "unitOfMeasureKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "warehouseId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ReplenishmentItem replenishmentItem, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "availabilityDate")) {
				if (jsonParserFieldValue != null) {
					replenishmentItem.setAvailabilityDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					replenishmentItem.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					replenishmentItem.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "quantity")) {
				if (jsonParserFieldValue != null) {
					replenishmentItem.setQuantity(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sku")) {
				if (jsonParserFieldValue != null) {
					replenishmentItem.setSku((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "unitOfMeasureKey")) {
				if (jsonParserFieldValue != null) {
					replenishmentItem.setUnitOfMeasureKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "warehouseId")) {
				if (jsonParserFieldValue != null) {
					replenishmentItem.setWarehouseId(
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