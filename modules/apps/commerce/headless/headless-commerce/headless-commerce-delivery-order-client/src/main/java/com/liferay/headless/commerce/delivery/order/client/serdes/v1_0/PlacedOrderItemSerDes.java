/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.PlacedOrderItem;
import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.PlacedOrderItemShipment;
import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.VirtualItem;
import com.liferay.headless.commerce.delivery.order.client.json.BaseJSONParser;

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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class PlacedOrderItemSerDes {

	public static PlacedOrderItem toDTO(String json) {
		PlacedOrderItemJSONParser placedOrderItemJSONParser =
			new PlacedOrderItemJSONParser();

		return placedOrderItemJSONParser.parseToDTO(json);
	}

	public static PlacedOrderItem[] toDTOs(String json) {
		PlacedOrderItemJSONParser placedOrderItemJSONParser =
			new PlacedOrderItemJSONParser();

		return placedOrderItemJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PlacedOrderItem placedOrderItem) {
		if (placedOrderItem == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (placedOrderItem.getAdaptiveMediaImageHTMLTag() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"adaptiveMediaImageHTMLTag\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderItem.getAdaptiveMediaImageHTMLTag()));

			sb.append("\"");
		}

		if (placedOrderItem.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(placedOrderItem.getCustomFields()));
		}

		if (placedOrderItem.getDeliveryGroup() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliveryGroup\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderItem.getDeliveryGroup()));

			sb.append("\"");
		}

		if (placedOrderItem.getDeliveryGroupName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliveryGroupName\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderItem.getDeliveryGroupName()));

			sb.append("\"");
		}

		if (placedOrderItem.getErrorMessages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessages\": ");

			sb.append("[");

			for (int i = 0; i < placedOrderItem.getErrorMessages().length;
				 i++) {

				sb.append(_toJSON(placedOrderItem.getErrorMessages()[i]));

				if ((i + 1) < placedOrderItem.getErrorMessages().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (placedOrderItem.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderItem.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (placedOrderItem.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(placedOrderItem.getId());
		}

		if (placedOrderItem.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderItem.getName()));

			sb.append("\"");
		}

		if (placedOrderItem.getOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"options\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderItem.getOptions()));

			sb.append("\"");
		}

		if (placedOrderItem.getParentOrderItemId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentOrderItemId\": ");

			sb.append(placedOrderItem.getParentOrderItemId());
		}

		if (placedOrderItem.getPlacedOrderItemShipments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderItemShipments\": ");

			sb.append("[");

			for (int i = 0;
				 i < placedOrderItem.getPlacedOrderItemShipments().length;
				 i++) {

				sb.append(
					String.valueOf(
						placedOrderItem.getPlacedOrderItemShipments()[i]));

				if ((i + 1) <
						placedOrderItem.getPlacedOrderItemShipments().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (placedOrderItem.getPlacedOrderItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderItems\": ");

			sb.append("[");

			for (int i = 0; i < placedOrderItem.getPlacedOrderItems().length;
				 i++) {

				sb.append(
					String.valueOf(placedOrderItem.getPlacedOrderItems()[i]));

				if ((i + 1) < placedOrderItem.getPlacedOrderItems().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (placedOrderItem.getPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"price\": ");

			sb.append(String.valueOf(placedOrderItem.getPrice()));
		}

		if (placedOrderItem.getProductId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(placedOrderItem.getProductId());
		}

		if (placedOrderItem.getProductURLs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productURLs\": ");

			sb.append(_toJSON(placedOrderItem.getProductURLs()));
		}

		if (placedOrderItem.getQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"quantity\": ");

			sb.append(placedOrderItem.getQuantity());
		}

		if (placedOrderItem.getReplacedSku() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacedSku\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderItem.getReplacedSku()));

			sb.append("\"");
		}

		if (placedOrderItem.getRequestedDeliveryDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"requestedDeliveryDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					placedOrderItem.getRequestedDeliveryDate()));

			sb.append("\"");
		}

		if (placedOrderItem.getSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"settings\": ");

			sb.append(String.valueOf(placedOrderItem.getSettings()));
		}

		if (placedOrderItem.getShippingAddressExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddressExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					placedOrderItem.getShippingAddressExternalReferenceCode()));

			sb.append("\"");
		}

		if (placedOrderItem.getShippingAddressId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddressId\": ");

			sb.append(placedOrderItem.getShippingAddressId());
		}

		if (placedOrderItem.getSku() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sku\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderItem.getSku()));

			sb.append("\"");
		}

		if (placedOrderItem.getSkuId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuId\": ");

			sb.append(placedOrderItem.getSkuId());
		}

		if (placedOrderItem.getSubscription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscription\": ");

			sb.append(placedOrderItem.getSubscription());
		}

		if (placedOrderItem.getThumbnail() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"thumbnail\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderItem.getThumbnail()));

			sb.append("\"");
		}

		if (placedOrderItem.getUnitOfMeasure() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitOfMeasure\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderItem.getUnitOfMeasure()));

			sb.append("\"");
		}

		if (placedOrderItem.getUnitOfMeasureKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitOfMeasureKey\": ");

			sb.append("\"");

			sb.append(_escape(placedOrderItem.getUnitOfMeasureKey()));

			sb.append("\"");
		}

		if (placedOrderItem.getValid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"valid\": ");

			sb.append(placedOrderItem.getValid());
		}

		if (placedOrderItem.getVirtualItemURLs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"virtualItemURLs\": ");

			sb.append("[");

			for (int i = 0; i < placedOrderItem.getVirtualItemURLs().length;
				 i++) {

				sb.append(_toJSON(placedOrderItem.getVirtualItemURLs()[i]));

				if ((i + 1) < placedOrderItem.getVirtualItemURLs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (placedOrderItem.getVirtualItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"virtualItems\": ");

			sb.append("[");

			for (int i = 0; i < placedOrderItem.getVirtualItems().length; i++) {
				sb.append(String.valueOf(placedOrderItem.getVirtualItems()[i]));

				if ((i + 1) < placedOrderItem.getVirtualItems().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PlacedOrderItemJSONParser placedOrderItemJSONParser =
			new PlacedOrderItemJSONParser();

		return placedOrderItemJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PlacedOrderItem placedOrderItem) {
		if (placedOrderItem == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (placedOrderItem.getAdaptiveMediaImageHTMLTag() == null) {
			map.put("adaptiveMediaImageHTMLTag", null);
		}
		else {
			map.put(
				"adaptiveMediaImageHTMLTag",
				String.valueOf(placedOrderItem.getAdaptiveMediaImageHTMLTag()));
		}

		if (placedOrderItem.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields",
				String.valueOf(placedOrderItem.getCustomFields()));
		}

		if (placedOrderItem.getDeliveryGroup() == null) {
			map.put("deliveryGroup", null);
		}
		else {
			map.put(
				"deliveryGroup",
				String.valueOf(placedOrderItem.getDeliveryGroup()));
		}

		if (placedOrderItem.getDeliveryGroupName() == null) {
			map.put("deliveryGroupName", null);
		}
		else {
			map.put(
				"deliveryGroupName",
				String.valueOf(placedOrderItem.getDeliveryGroupName()));
		}

		if (placedOrderItem.getErrorMessages() == null) {
			map.put("errorMessages", null);
		}
		else {
			map.put(
				"errorMessages",
				String.valueOf(placedOrderItem.getErrorMessages()));
		}

		if (placedOrderItem.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(placedOrderItem.getExternalReferenceCode()));
		}

		if (placedOrderItem.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(placedOrderItem.getId()));
		}

		if (placedOrderItem.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(placedOrderItem.getName()));
		}

		if (placedOrderItem.getOptions() == null) {
			map.put("options", null);
		}
		else {
			map.put("options", String.valueOf(placedOrderItem.getOptions()));
		}

		if (placedOrderItem.getParentOrderItemId() == null) {
			map.put("parentOrderItemId", null);
		}
		else {
			map.put(
				"parentOrderItemId",
				String.valueOf(placedOrderItem.getParentOrderItemId()));
		}

		if (placedOrderItem.getPlacedOrderItemShipments() == null) {
			map.put("placedOrderItemShipments", null);
		}
		else {
			map.put(
				"placedOrderItemShipments",
				String.valueOf(placedOrderItem.getPlacedOrderItemShipments()));
		}

		if (placedOrderItem.getPlacedOrderItems() == null) {
			map.put("placedOrderItems", null);
		}
		else {
			map.put(
				"placedOrderItems",
				String.valueOf(placedOrderItem.getPlacedOrderItems()));
		}

		if (placedOrderItem.getPrice() == null) {
			map.put("price", null);
		}
		else {
			map.put("price", String.valueOf(placedOrderItem.getPrice()));
		}

		if (placedOrderItem.getProductId() == null) {
			map.put("productId", null);
		}
		else {
			map.put(
				"productId", String.valueOf(placedOrderItem.getProductId()));
		}

		if (placedOrderItem.getProductURLs() == null) {
			map.put("productURLs", null);
		}
		else {
			map.put(
				"productURLs",
				String.valueOf(placedOrderItem.getProductURLs()));
		}

		if (placedOrderItem.getQuantity() == null) {
			map.put("quantity", null);
		}
		else {
			map.put("quantity", String.valueOf(placedOrderItem.getQuantity()));
		}

		if (placedOrderItem.getReplacedSku() == null) {
			map.put("replacedSku", null);
		}
		else {
			map.put(
				"replacedSku",
				String.valueOf(placedOrderItem.getReplacedSku()));
		}

		if (placedOrderItem.getRequestedDeliveryDate() == null) {
			map.put("requestedDeliveryDate", null);
		}
		else {
			map.put(
				"requestedDeliveryDate",
				liferayToJSONDateFormat.format(
					placedOrderItem.getRequestedDeliveryDate()));
		}

		if (placedOrderItem.getSettings() == null) {
			map.put("settings", null);
		}
		else {
			map.put("settings", String.valueOf(placedOrderItem.getSettings()));
		}

		if (placedOrderItem.getShippingAddressExternalReferenceCode() == null) {
			map.put("shippingAddressExternalReferenceCode", null);
		}
		else {
			map.put(
				"shippingAddressExternalReferenceCode",
				String.valueOf(
					placedOrderItem.getShippingAddressExternalReferenceCode()));
		}

		if (placedOrderItem.getShippingAddressId() == null) {
			map.put("shippingAddressId", null);
		}
		else {
			map.put(
				"shippingAddressId",
				String.valueOf(placedOrderItem.getShippingAddressId()));
		}

		if (placedOrderItem.getSku() == null) {
			map.put("sku", null);
		}
		else {
			map.put("sku", String.valueOf(placedOrderItem.getSku()));
		}

		if (placedOrderItem.getSkuId() == null) {
			map.put("skuId", null);
		}
		else {
			map.put("skuId", String.valueOf(placedOrderItem.getSkuId()));
		}

		if (placedOrderItem.getSubscription() == null) {
			map.put("subscription", null);
		}
		else {
			map.put(
				"subscription",
				String.valueOf(placedOrderItem.getSubscription()));
		}

		if (placedOrderItem.getThumbnail() == null) {
			map.put("thumbnail", null);
		}
		else {
			map.put(
				"thumbnail", String.valueOf(placedOrderItem.getThumbnail()));
		}

		if (placedOrderItem.getUnitOfMeasure() == null) {
			map.put("unitOfMeasure", null);
		}
		else {
			map.put(
				"unitOfMeasure",
				String.valueOf(placedOrderItem.getUnitOfMeasure()));
		}

		if (placedOrderItem.getUnitOfMeasureKey() == null) {
			map.put("unitOfMeasureKey", null);
		}
		else {
			map.put(
				"unitOfMeasureKey",
				String.valueOf(placedOrderItem.getUnitOfMeasureKey()));
		}

		if (placedOrderItem.getValid() == null) {
			map.put("valid", null);
		}
		else {
			map.put("valid", String.valueOf(placedOrderItem.getValid()));
		}

		if (placedOrderItem.getVirtualItemURLs() == null) {
			map.put("virtualItemURLs", null);
		}
		else {
			map.put(
				"virtualItemURLs",
				String.valueOf(placedOrderItem.getVirtualItemURLs()));
		}

		if (placedOrderItem.getVirtualItems() == null) {
			map.put("virtualItems", null);
		}
		else {
			map.put(
				"virtualItems",
				String.valueOf(placedOrderItem.getVirtualItems()));
		}

		return map;
	}

	public static class PlacedOrderItemJSONParser
		extends BaseJSONParser<PlacedOrderItem> {

		@Override
		protected PlacedOrderItem createDTO() {
			return new PlacedOrderItem();
		}

		@Override
		protected PlacedOrderItem[] createDTOArray(int size) {
			return new PlacedOrderItem[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "adaptiveMediaImageHTMLTag")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "deliveryGroup")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "deliveryGroupName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessages")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "parentOrderItemId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "placedOrderItemShipments")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "placedOrderItems")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "price")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productURLs")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "quantity")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "replacedSku")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "requestedDeliveryDate")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "settings")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingAddressExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAddressId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sku")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "skuId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "subscription")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "thumbnail")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "unitOfMeasure")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "unitOfMeasureKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "valid")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "virtualItemURLs")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "virtualItems")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PlacedOrderItem placedOrderItem, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "adaptiveMediaImageHTMLTag")) {

				if (jsonParserFieldValue != null) {
					placedOrderItem.setAdaptiveMediaImageHTMLTag(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setCustomFields(
						(Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deliveryGroup")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setDeliveryGroup(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deliveryGroupName")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setDeliveryGroupName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessages")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setErrorMessages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					placedOrderItem.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setOptions((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "parentOrderItemId")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setParentOrderItemId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "placedOrderItemShipments")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PlacedOrderItemShipment[] placedOrderItemShipmentsArray =
						new PlacedOrderItemShipment
							[jsonParserFieldValues.length];

					for (int i = 0; i < placedOrderItemShipmentsArray.length;
						 i++) {

						placedOrderItemShipmentsArray[i] =
							PlacedOrderItemShipmentSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					placedOrderItem.setPlacedOrderItemShipments(
						placedOrderItemShipmentsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "placedOrderItems")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PlacedOrderItem[] placedOrderItemsArray =
						new PlacedOrderItem[jsonParserFieldValues.length];

					for (int i = 0; i < placedOrderItemsArray.length; i++) {
						placedOrderItemsArray[i] = PlacedOrderItemSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					placedOrderItem.setPlacedOrderItems(placedOrderItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "price")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setPrice(
						PriceSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setProductId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productURLs")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setProductURLs(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "quantity")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setQuantity(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "replacedSku")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setReplacedSku(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "requestedDeliveryDate")) {

				if (jsonParserFieldValue != null) {
					placedOrderItem.setRequestedDeliveryDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "settings")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setSettings(
						SettingsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingAddressExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					placedOrderItem.setShippingAddressExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAddressId")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setShippingAddressId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sku")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setSku((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skuId")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setSkuId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subscription")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setSubscription(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "thumbnail")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setThumbnail((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "unitOfMeasure")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setUnitOfMeasure(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "unitOfMeasureKey")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setUnitOfMeasureKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "valid")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setValid((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "virtualItemURLs")) {
				if (jsonParserFieldValue != null) {
					placedOrderItem.setVirtualItemURLs(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "virtualItems")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					VirtualItem[] virtualItemsArray =
						new VirtualItem[jsonParserFieldValues.length];

					for (int i = 0; i < virtualItemsArray.length; i++) {
						virtualItemsArray[i] = VirtualItemSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					placedOrderItem.setVirtualItems(virtualItemsArray);
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