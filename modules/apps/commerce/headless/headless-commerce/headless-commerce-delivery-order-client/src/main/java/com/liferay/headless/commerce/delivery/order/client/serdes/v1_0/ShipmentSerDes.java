/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.Shipment;
import com.liferay.headless.commerce.delivery.order.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class ShipmentSerDes {

	public static Shipment toDTO(String json) {
		ShipmentJSONParser shipmentJSONParser = new ShipmentJSONParser();

		return shipmentJSONParser.parseToDTO(json);
	}

	public static Shipment[] toDTOs(String json) {
		ShipmentJSONParser shipmentJSONParser = new ShipmentJSONParser();

		return shipmentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Shipment shipment) {
		if (shipment == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (shipment.getAccountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountId\": ");

			sb.append(shipment.getAccountId());
		}

		if (shipment.getCarrier() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"carrier\": ");

			sb.append("\"");

			sb.append(_escape(shipment.getCarrier()));

			sb.append("\"");
		}

		if (shipment.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(shipment.getCreateDate()));

			sb.append("\"");
		}

		if (shipment.getExpectedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expectedDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(shipment.getExpectedDate()));

			sb.append("\"");
		}

		if (shipment.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(shipment.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (shipment.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(shipment.getId());
		}

		if (shipment.getItemsCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemsCount\": ");

			sb.append(shipment.getItemsCount());
		}

		if (shipment.getModifiedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifiedDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(shipment.getModifiedDate()));

			sb.append("\"");
		}

		if (shipment.getOneLineAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"oneLineAddress\": ");

			sb.append("\"");

			sb.append(_escape(shipment.getOneLineAddress()));

			sb.append("\"");
		}

		if (shipment.getOrderExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(shipment.getOrderExternalReferenceCode()));

			sb.append("\"");
		}

		if (shipment.getOrderId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderId\": ");

			sb.append(shipment.getOrderId());
		}

		if (shipment.getShippingAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddress\": ");

			sb.append(String.valueOf(shipment.getShippingAddress()));
		}

		if (shipment.getShippingAddressId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddressId\": ");

			sb.append(shipment.getShippingAddressId());
		}

		if (shipment.getShippingDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(shipment.getShippingDate()));

			sb.append("\"");
		}

		if (shipment.getShippingMethodId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingMethodId\": ");

			sb.append(shipment.getShippingMethodId());
		}

		if (shipment.getShippingOptionName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingOptionName\": ");

			sb.append("\"");

			sb.append(_escape(shipment.getShippingOptionName()));

			sb.append("\"");
		}

		if (shipment.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(String.valueOf(shipment.getStatus()));
		}

		if (shipment.getTrackingNumber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trackingNumber\": ");

			sb.append("\"");

			sb.append(_escape(shipment.getTrackingNumber()));

			sb.append("\"");
		}

		if (shipment.getTrackingURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trackingURL\": ");

			sb.append("\"");

			sb.append(_escape(shipment.getTrackingURL()));

			sb.append("\"");
		}

		if (shipment.getUserName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userName\": ");

			sb.append("\"");

			sb.append(_escape(shipment.getUserName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ShipmentJSONParser shipmentJSONParser = new ShipmentJSONParser();

		return shipmentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Shipment shipment) {
		if (shipment == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (shipment.getAccountId() == null) {
			map.put("accountId", null);
		}
		else {
			map.put("accountId", String.valueOf(shipment.getAccountId()));
		}

		if (shipment.getCarrier() == null) {
			map.put("carrier", null);
		}
		else {
			map.put("carrier", String.valueOf(shipment.getCarrier()));
		}

		if (shipment.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put(
				"createDate",
				liferayToJSONDateFormat.format(shipment.getCreateDate()));
		}

		if (shipment.getExpectedDate() == null) {
			map.put("expectedDate", null);
		}
		else {
			map.put(
				"expectedDate",
				liferayToJSONDateFormat.format(shipment.getExpectedDate()));
		}

		if (shipment.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(shipment.getExternalReferenceCode()));
		}

		if (shipment.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(shipment.getId()));
		}

		if (shipment.getItemsCount() == null) {
			map.put("itemsCount", null);
		}
		else {
			map.put("itemsCount", String.valueOf(shipment.getItemsCount()));
		}

		if (shipment.getModifiedDate() == null) {
			map.put("modifiedDate", null);
		}
		else {
			map.put(
				"modifiedDate",
				liferayToJSONDateFormat.format(shipment.getModifiedDate()));
		}

		if (shipment.getOneLineAddress() == null) {
			map.put("oneLineAddress", null);
		}
		else {
			map.put(
				"oneLineAddress", String.valueOf(shipment.getOneLineAddress()));
		}

		if (shipment.getOrderExternalReferenceCode() == null) {
			map.put("orderExternalReferenceCode", null);
		}
		else {
			map.put(
				"orderExternalReferenceCode",
				String.valueOf(shipment.getOrderExternalReferenceCode()));
		}

		if (shipment.getOrderId() == null) {
			map.put("orderId", null);
		}
		else {
			map.put("orderId", String.valueOf(shipment.getOrderId()));
		}

		if (shipment.getShippingAddress() == null) {
			map.put("shippingAddress", null);
		}
		else {
			map.put(
				"shippingAddress",
				String.valueOf(shipment.getShippingAddress()));
		}

		if (shipment.getShippingAddressId() == null) {
			map.put("shippingAddressId", null);
		}
		else {
			map.put(
				"shippingAddressId",
				String.valueOf(shipment.getShippingAddressId()));
		}

		if (shipment.getShippingDate() == null) {
			map.put("shippingDate", null);
		}
		else {
			map.put(
				"shippingDate",
				liferayToJSONDateFormat.format(shipment.getShippingDate()));
		}

		if (shipment.getShippingMethodId() == null) {
			map.put("shippingMethodId", null);
		}
		else {
			map.put(
				"shippingMethodId",
				String.valueOf(shipment.getShippingMethodId()));
		}

		if (shipment.getShippingOptionName() == null) {
			map.put("shippingOptionName", null);
		}
		else {
			map.put(
				"shippingOptionName",
				String.valueOf(shipment.getShippingOptionName()));
		}

		if (shipment.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(shipment.getStatus()));
		}

		if (shipment.getTrackingNumber() == null) {
			map.put("trackingNumber", null);
		}
		else {
			map.put(
				"trackingNumber", String.valueOf(shipment.getTrackingNumber()));
		}

		if (shipment.getTrackingURL() == null) {
			map.put("trackingURL", null);
		}
		else {
			map.put("trackingURL", String.valueOf(shipment.getTrackingURL()));
		}

		if (shipment.getUserName() == null) {
			map.put("userName", null);
		}
		else {
			map.put("userName", String.valueOf(shipment.getUserName()));
		}

		return map;
	}

	public static class ShipmentJSONParser extends BaseJSONParser<Shipment> {

		@Override
		protected Shipment createDTO() {
			return new Shipment();
		}

		@Override
		protected Shipment[] createDTOArray(int size) {
			return new Shipment[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accountId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "carrier")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "expectedDate")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "itemsCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "oneLineAddress")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "orderExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAddress")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAddressId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shippingDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shippingMethodId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingOptionName")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "trackingNumber")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "trackingURL")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userName")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Shipment shipment, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountId")) {
				if (jsonParserFieldValue != null) {
					shipment.setAccountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "carrier")) {
				if (jsonParserFieldValue != null) {
					shipment.setCarrier((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					shipment.setCreateDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expectedDate")) {
				if (jsonParserFieldValue != null) {
					shipment.setExpectedDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					shipment.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					shipment.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "itemsCount")) {
				if (jsonParserFieldValue != null) {
					shipment.setItemsCount(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				if (jsonParserFieldValue != null) {
					shipment.setModifiedDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "oneLineAddress")) {
				if (jsonParserFieldValue != null) {
					shipment.setOneLineAddress((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "orderExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					shipment.setOrderExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderId")) {
				if (jsonParserFieldValue != null) {
					shipment.setOrderId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAddress")) {
				if (jsonParserFieldValue != null) {
					shipment.setShippingAddress(
						ShippingAddressSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAddressId")) {
				if (jsonParserFieldValue != null) {
					shipment.setShippingAddressId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingDate")) {
				if (jsonParserFieldValue != null) {
					shipment.setShippingDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingMethodId")) {
				if (jsonParserFieldValue != null) {
					shipment.setShippingMethodId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingOptionName")) {

				if (jsonParserFieldValue != null) {
					shipment.setShippingOptionName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					shipment.setStatus(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "trackingNumber")) {
				if (jsonParserFieldValue != null) {
					shipment.setTrackingNumber((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "trackingURL")) {
				if (jsonParserFieldValue != null) {
					shipment.setTrackingURL((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userName")) {
				if (jsonParserFieldValue != null) {
					shipment.setUserName((String)jsonParserFieldValue);
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