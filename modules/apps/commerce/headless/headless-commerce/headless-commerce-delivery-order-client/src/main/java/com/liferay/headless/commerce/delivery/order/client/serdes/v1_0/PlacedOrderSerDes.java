/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.Attachment;
import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.PlacedOrder;
import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.PlacedOrderComment;
import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.PlacedOrderItem;
import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.Step;
import com.liferay.headless.commerce.delivery.order.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class PlacedOrderSerDes {

	public static PlacedOrder toDTO(String json) {
		PlacedOrderJSONParser placedOrderJSONParser =
			new PlacedOrderJSONParser();

		return placedOrderJSONParser.parseToDTO(json);
	}

	public static PlacedOrder[] toDTOs(String json) {
		PlacedOrderJSONParser placedOrderJSONParser =
			new PlacedOrderJSONParser();

		return placedOrderJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PlacedOrder placedOrder) {
		if (placedOrder == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (placedOrder.getAccount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"account\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getAccount()));

			sb.append("\"");
		}

		if (placedOrder.getAccountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountId\": ");

			sb.append(placedOrder.getAccountId());
		}

		if (placedOrder.getAttachments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attachments\": ");

			sb.append("[");

			for (int i = 0; i < placedOrder.getAttachments().length; i++) {
				sb.append(String.valueOf(placedOrder.getAttachments()[i]));

				if ((i + 1) < placedOrder.getAttachments().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (placedOrder.getAuthor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"author\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getAuthor()));

			sb.append("\"");
		}

		if (placedOrder.getAuthorId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"authorId\": ");

			sb.append(placedOrder.getAuthorId());
		}

		if (placedOrder.getChannelId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelId\": ");

			sb.append(placedOrder.getChannelId());
		}

		if (placedOrder.getCouponCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"couponCode\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getCouponCode()));

			sb.append("\"");
		}

		if (placedOrder.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(placedOrder.getCreateDate()));

			sb.append("\"");
		}

		if (placedOrder.getCurrencyCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyCode\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getCurrencyCode()));

			sb.append("\"");
		}

		if (placedOrder.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(placedOrder.getCustomFields()));
		}

		if (placedOrder.getErrorMessages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessages\": ");

			sb.append("[");

			for (int i = 0; i < placedOrder.getErrorMessages().length; i++) {
				sb.append(_toJSON(placedOrder.getErrorMessages()[i]));

				if ((i + 1) < placedOrder.getErrorMessages().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (placedOrder.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (placedOrder.getFriendlyURLSeparator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyURLSeparator\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getFriendlyURLSeparator()));

			sb.append("\"");
		}

		if (placedOrder.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(placedOrder.getId());
		}

		if (placedOrder.getLastPriceUpdateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastPriceUpdateDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					placedOrder.getLastPriceUpdateDate()));

			sb.append("\"");
		}

		if (placedOrder.getModifiedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifiedDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(placedOrder.getModifiedDate()));

			sb.append("\"");
		}

		if (placedOrder.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getName()));

			sb.append("\"");
		}

		if (placedOrder.getOrderStatusInfo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderStatusInfo\": ");

			sb.append(String.valueOf(placedOrder.getOrderStatusInfo()));
		}

		if (placedOrder.getOrderType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderType\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getOrderType()));

			sb.append("\"");
		}

		if (placedOrder.getOrderTypeExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getOrderTypeExternalReferenceCode()));

			sb.append("\"");
		}

		if (placedOrder.getOrderTypeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeId\": ");

			sb.append(placedOrder.getOrderTypeId());
		}

		if (placedOrder.getOrderUUID() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderUUID\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getOrderUUID()));

			sb.append("\"");
		}

		if (placedOrder.getPaymentMethod() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentMethod\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getPaymentMethod()));

			sb.append("\"");
		}

		if (placedOrder.getPaymentMethodLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentMethodLabel\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getPaymentMethodLabel()));

			sb.append("\"");
		}

		if (placedOrder.getPaymentStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentStatus\": ");

			sb.append(placedOrder.getPaymentStatus());
		}

		if (placedOrder.getPaymentStatusInfo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentStatusInfo\": ");

			sb.append(String.valueOf(placedOrder.getPaymentStatusInfo()));
		}

		if (placedOrder.getPaymentStatusLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentStatusLabel\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getPaymentStatusLabel()));

			sb.append("\"");
		}

		if (placedOrder.getPlacedOrderBillingAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderBillingAddress\": ");

			sb.append(
				String.valueOf(placedOrder.getPlacedOrderBillingAddress()));
		}

		if (placedOrder.getPlacedOrderBillingAddressId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderBillingAddressId\": ");

			sb.append(placedOrder.getPlacedOrderBillingAddressId());
		}

		if (placedOrder.getPlacedOrderComments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderComments\": ");

			sb.append("[");

			for (int i = 0; i < placedOrder.getPlacedOrderComments().length;
				 i++) {

				sb.append(
					String.valueOf(placedOrder.getPlacedOrderComments()[i]));

				if ((i + 1) < placedOrder.getPlacedOrderComments().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (placedOrder.getPlacedOrderItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderItems\": ");

			sb.append("[");

			for (int i = 0; i < placedOrder.getPlacedOrderItems().length; i++) {
				sb.append(String.valueOf(placedOrder.getPlacedOrderItems()[i]));

				if ((i + 1) < placedOrder.getPlacedOrderItems().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (placedOrder.getPlacedOrderShippingAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderShippingAddress\": ");

			sb.append(
				String.valueOf(placedOrder.getPlacedOrderShippingAddress()));
		}

		if (placedOrder.getPlacedOrderShippingAddressId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderShippingAddressId\": ");

			sb.append(placedOrder.getPlacedOrderShippingAddressId());
		}

		if (placedOrder.getPrintedNote() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"printedNote\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getPrintedNote()));

			sb.append("\"");
		}

		if (placedOrder.getPurchaseOrderNumber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"purchaseOrderNumber\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getPurchaseOrderNumber()));

			sb.append("\"");
		}

		if (placedOrder.getRequestedDeliveryDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"requestedDeliveryDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					placedOrder.getRequestedDeliveryDate()));

			sb.append("\"");
		}

		if (placedOrder.getShipments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shipments\": ");

			sb.append(String.valueOf(placedOrder.getShipments()));
		}

		if (placedOrder.getShippingMethod() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingMethod\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getShippingMethod()));

			sb.append("\"");
		}

		if (placedOrder.getShippingOption() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingOption\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getShippingOption()));

			sb.append("\"");
		}

		if (placedOrder.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(_escape(placedOrder.getStatus()));

			sb.append("\"");
		}

		if (placedOrder.getSteps() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"steps\": ");

			sb.append("[");

			for (int i = 0; i < placedOrder.getSteps().length; i++) {
				sb.append(String.valueOf(placedOrder.getSteps()[i]));

				if ((i + 1) < placedOrder.getSteps().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (placedOrder.getSummary() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"summary\": ");

			sb.append(String.valueOf(placedOrder.getSummary()));
		}

		if (placedOrder.getUseAsBilling() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"useAsBilling\": ");

			sb.append(placedOrder.getUseAsBilling());
		}

		if (placedOrder.getValid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"valid\": ");

			sb.append(placedOrder.getValid());
		}

		if (placedOrder.getWorkflowStatusInfo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowStatusInfo\": ");

			sb.append(String.valueOf(placedOrder.getWorkflowStatusInfo()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PlacedOrderJSONParser placedOrderJSONParser =
			new PlacedOrderJSONParser();

		return placedOrderJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PlacedOrder placedOrder) {
		if (placedOrder == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (placedOrder.getAccount() == null) {
			map.put("account", null);
		}
		else {
			map.put("account", String.valueOf(placedOrder.getAccount()));
		}

		if (placedOrder.getAccountId() == null) {
			map.put("accountId", null);
		}
		else {
			map.put("accountId", String.valueOf(placedOrder.getAccountId()));
		}

		if (placedOrder.getAttachments() == null) {
			map.put("attachments", null);
		}
		else {
			map.put(
				"attachments", String.valueOf(placedOrder.getAttachments()));
		}

		if (placedOrder.getAuthor() == null) {
			map.put("author", null);
		}
		else {
			map.put("author", String.valueOf(placedOrder.getAuthor()));
		}

		if (placedOrder.getAuthorId() == null) {
			map.put("authorId", null);
		}
		else {
			map.put("authorId", String.valueOf(placedOrder.getAuthorId()));
		}

		if (placedOrder.getChannelId() == null) {
			map.put("channelId", null);
		}
		else {
			map.put("channelId", String.valueOf(placedOrder.getChannelId()));
		}

		if (placedOrder.getCouponCode() == null) {
			map.put("couponCode", null);
		}
		else {
			map.put("couponCode", String.valueOf(placedOrder.getCouponCode()));
		}

		if (placedOrder.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put(
				"createDate",
				liferayToJSONDateFormat.format(placedOrder.getCreateDate()));
		}

		if (placedOrder.getCurrencyCode() == null) {
			map.put("currencyCode", null);
		}
		else {
			map.put(
				"currencyCode", String.valueOf(placedOrder.getCurrencyCode()));
		}

		if (placedOrder.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields", String.valueOf(placedOrder.getCustomFields()));
		}

		if (placedOrder.getErrorMessages() == null) {
			map.put("errorMessages", null);
		}
		else {
			map.put(
				"errorMessages",
				String.valueOf(placedOrder.getErrorMessages()));
		}

		if (placedOrder.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(placedOrder.getExternalReferenceCode()));
		}

		if (placedOrder.getFriendlyURLSeparator() == null) {
			map.put("friendlyURLSeparator", null);
		}
		else {
			map.put(
				"friendlyURLSeparator",
				String.valueOf(placedOrder.getFriendlyURLSeparator()));
		}

		if (placedOrder.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(placedOrder.getId()));
		}

		if (placedOrder.getLastPriceUpdateDate() == null) {
			map.put("lastPriceUpdateDate", null);
		}
		else {
			map.put(
				"lastPriceUpdateDate",
				liferayToJSONDateFormat.format(
					placedOrder.getLastPriceUpdateDate()));
		}

		if (placedOrder.getModifiedDate() == null) {
			map.put("modifiedDate", null);
		}
		else {
			map.put(
				"modifiedDate",
				liferayToJSONDateFormat.format(placedOrder.getModifiedDate()));
		}

		if (placedOrder.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(placedOrder.getName()));
		}

		if (placedOrder.getOrderStatusInfo() == null) {
			map.put("orderStatusInfo", null);
		}
		else {
			map.put(
				"orderStatusInfo",
				String.valueOf(placedOrder.getOrderStatusInfo()));
		}

		if (placedOrder.getOrderType() == null) {
			map.put("orderType", null);
		}
		else {
			map.put("orderType", String.valueOf(placedOrder.getOrderType()));
		}

		if (placedOrder.getOrderTypeExternalReferenceCode() == null) {
			map.put("orderTypeExternalReferenceCode", null);
		}
		else {
			map.put(
				"orderTypeExternalReferenceCode",
				String.valueOf(
					placedOrder.getOrderTypeExternalReferenceCode()));
		}

		if (placedOrder.getOrderTypeId() == null) {
			map.put("orderTypeId", null);
		}
		else {
			map.put(
				"orderTypeId", String.valueOf(placedOrder.getOrderTypeId()));
		}

		if (placedOrder.getOrderUUID() == null) {
			map.put("orderUUID", null);
		}
		else {
			map.put("orderUUID", String.valueOf(placedOrder.getOrderUUID()));
		}

		if (placedOrder.getPaymentMethod() == null) {
			map.put("paymentMethod", null);
		}
		else {
			map.put(
				"paymentMethod",
				String.valueOf(placedOrder.getPaymentMethod()));
		}

		if (placedOrder.getPaymentMethodLabel() == null) {
			map.put("paymentMethodLabel", null);
		}
		else {
			map.put(
				"paymentMethodLabel",
				String.valueOf(placedOrder.getPaymentMethodLabel()));
		}

		if (placedOrder.getPaymentStatus() == null) {
			map.put("paymentStatus", null);
		}
		else {
			map.put(
				"paymentStatus",
				String.valueOf(placedOrder.getPaymentStatus()));
		}

		if (placedOrder.getPaymentStatusInfo() == null) {
			map.put("paymentStatusInfo", null);
		}
		else {
			map.put(
				"paymentStatusInfo",
				String.valueOf(placedOrder.getPaymentStatusInfo()));
		}

		if (placedOrder.getPaymentStatusLabel() == null) {
			map.put("paymentStatusLabel", null);
		}
		else {
			map.put(
				"paymentStatusLabel",
				String.valueOf(placedOrder.getPaymentStatusLabel()));
		}

		if (placedOrder.getPlacedOrderBillingAddress() == null) {
			map.put("placedOrderBillingAddress", null);
		}
		else {
			map.put(
				"placedOrderBillingAddress",
				String.valueOf(placedOrder.getPlacedOrderBillingAddress()));
		}

		if (placedOrder.getPlacedOrderBillingAddressId() == null) {
			map.put("placedOrderBillingAddressId", null);
		}
		else {
			map.put(
				"placedOrderBillingAddressId",
				String.valueOf(placedOrder.getPlacedOrderBillingAddressId()));
		}

		if (placedOrder.getPlacedOrderComments() == null) {
			map.put("placedOrderComments", null);
		}
		else {
			map.put(
				"placedOrderComments",
				String.valueOf(placedOrder.getPlacedOrderComments()));
		}

		if (placedOrder.getPlacedOrderItems() == null) {
			map.put("placedOrderItems", null);
		}
		else {
			map.put(
				"placedOrderItems",
				String.valueOf(placedOrder.getPlacedOrderItems()));
		}

		if (placedOrder.getPlacedOrderShippingAddress() == null) {
			map.put("placedOrderShippingAddress", null);
		}
		else {
			map.put(
				"placedOrderShippingAddress",
				String.valueOf(placedOrder.getPlacedOrderShippingAddress()));
		}

		if (placedOrder.getPlacedOrderShippingAddressId() == null) {
			map.put("placedOrderShippingAddressId", null);
		}
		else {
			map.put(
				"placedOrderShippingAddressId",
				String.valueOf(placedOrder.getPlacedOrderShippingAddressId()));
		}

		if (placedOrder.getPrintedNote() == null) {
			map.put("printedNote", null);
		}
		else {
			map.put(
				"printedNote", String.valueOf(placedOrder.getPrintedNote()));
		}

		if (placedOrder.getPurchaseOrderNumber() == null) {
			map.put("purchaseOrderNumber", null);
		}
		else {
			map.put(
				"purchaseOrderNumber",
				String.valueOf(placedOrder.getPurchaseOrderNumber()));
		}

		if (placedOrder.getRequestedDeliveryDate() == null) {
			map.put("requestedDeliveryDate", null);
		}
		else {
			map.put(
				"requestedDeliveryDate",
				liferayToJSONDateFormat.format(
					placedOrder.getRequestedDeliveryDate()));
		}

		if (placedOrder.getShipments() == null) {
			map.put("shipments", null);
		}
		else {
			map.put("shipments", String.valueOf(placedOrder.getShipments()));
		}

		if (placedOrder.getShippingMethod() == null) {
			map.put("shippingMethod", null);
		}
		else {
			map.put(
				"shippingMethod",
				String.valueOf(placedOrder.getShippingMethod()));
		}

		if (placedOrder.getShippingOption() == null) {
			map.put("shippingOption", null);
		}
		else {
			map.put(
				"shippingOption",
				String.valueOf(placedOrder.getShippingOption()));
		}

		if (placedOrder.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(placedOrder.getStatus()));
		}

		if (placedOrder.getSteps() == null) {
			map.put("steps", null);
		}
		else {
			map.put("steps", String.valueOf(placedOrder.getSteps()));
		}

		if (placedOrder.getSummary() == null) {
			map.put("summary", null);
		}
		else {
			map.put("summary", String.valueOf(placedOrder.getSummary()));
		}

		if (placedOrder.getUseAsBilling() == null) {
			map.put("useAsBilling", null);
		}
		else {
			map.put(
				"useAsBilling", String.valueOf(placedOrder.getUseAsBilling()));
		}

		if (placedOrder.getValid() == null) {
			map.put("valid", null);
		}
		else {
			map.put("valid", String.valueOf(placedOrder.getValid()));
		}

		if (placedOrder.getWorkflowStatusInfo() == null) {
			map.put("workflowStatusInfo", null);
		}
		else {
			map.put(
				"workflowStatusInfo",
				String.valueOf(placedOrder.getWorkflowStatusInfo()));
		}

		return map;
	}

	public static class PlacedOrderJSONParser
		extends BaseJSONParser<PlacedOrder> {

		@Override
		protected PlacedOrder createDTO() {
			return new PlacedOrder();
		}

		@Override
		protected PlacedOrder[] createDTOArray(int size) {
			return new PlacedOrder[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "account")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "accountId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "attachments")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "author")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "authorId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "channelId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "couponCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "currencyCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessages")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "friendlyURLSeparator")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "lastPriceUpdateDate")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderStatusInfo")) {
				return false;
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
			else if (Objects.equals(jsonParserFieldName, "orderUUID")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "paymentMethod")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "paymentMethodLabel")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "paymentStatus")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "paymentStatusInfo")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "paymentStatusLabel")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "placedOrderBillingAddress")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "placedOrderBillingAddressId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "placedOrderComments")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "placedOrderItems")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "placedOrderShippingAddress")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "placedOrderShippingAddressId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "printedNote")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "purchaseOrderNumber")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "requestedDeliveryDate")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shipments")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shippingMethod")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shippingOption")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "steps")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "summary")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "useAsBilling")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "valid")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowStatusInfo")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PlacedOrder placedOrder, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "account")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setAccount((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "accountId")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setAccountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "attachments")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Attachment[] attachmentsArray =
						new Attachment[jsonParserFieldValues.length];

					for (int i = 0; i < attachmentsArray.length; i++) {
						attachmentsArray[i] = AttachmentSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					placedOrder.setAttachments(attachmentsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "author")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setAuthor((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "authorId")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setAuthorId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "channelId")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setChannelId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "couponCode")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setCouponCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setCreateDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "currencyCode")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setCurrencyCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setCustomFields(
						(Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessages")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setErrorMessages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					placedOrder.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "friendlyURLSeparator")) {

				if (jsonParserFieldValue != null) {
					placedOrder.setFriendlyURLSeparator(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "lastPriceUpdateDate")) {

				if (jsonParserFieldValue != null) {
					placedOrder.setLastPriceUpdateDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setModifiedDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderStatusInfo")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setOrderStatusInfo(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderType")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setOrderType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"orderTypeExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					placedOrder.setOrderTypeExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderTypeId")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setOrderTypeId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderUUID")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setOrderUUID((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paymentMethod")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setPaymentMethod((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "paymentMethodLabel")) {

				if (jsonParserFieldValue != null) {
					placedOrder.setPaymentMethodLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paymentStatus")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setPaymentStatus(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paymentStatusInfo")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setPaymentStatusInfo(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "paymentStatusLabel")) {

				if (jsonParserFieldValue != null) {
					placedOrder.setPaymentStatusLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "placedOrderBillingAddress")) {

				if (jsonParserFieldValue != null) {
					placedOrder.setPlacedOrderBillingAddress(
						PlacedOrderAddressSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "placedOrderBillingAddressId")) {

				if (jsonParserFieldValue != null) {
					placedOrder.setPlacedOrderBillingAddressId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "placedOrderComments")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PlacedOrderComment[] placedOrderCommentsArray =
						new PlacedOrderComment[jsonParserFieldValues.length];

					for (int i = 0; i < placedOrderCommentsArray.length; i++) {
						placedOrderCommentsArray[i] =
							PlacedOrderCommentSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					placedOrder.setPlacedOrderComments(
						placedOrderCommentsArray);
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

					placedOrder.setPlacedOrderItems(placedOrderItemsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "placedOrderShippingAddress")) {

				if (jsonParserFieldValue != null) {
					placedOrder.setPlacedOrderShippingAddress(
						PlacedOrderAddressSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "placedOrderShippingAddressId")) {

				if (jsonParserFieldValue != null) {
					placedOrder.setPlacedOrderShippingAddressId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "printedNote")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setPrintedNote((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "purchaseOrderNumber")) {

				if (jsonParserFieldValue != null) {
					placedOrder.setPurchaseOrderNumber(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "requestedDeliveryDate")) {

				if (jsonParserFieldValue != null) {
					placedOrder.setRequestedDeliveryDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shipments")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setShipments(
						ShipmentSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingMethod")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setShippingMethod((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingOption")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setShippingOption((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setStatus((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "steps")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Step[] stepsArray = new Step[jsonParserFieldValues.length];

					for (int i = 0; i < stepsArray.length; i++) {
						stepsArray[i] = StepSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					placedOrder.setSteps(stepsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "summary")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setSummary(
						SummarySerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "useAsBilling")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setUseAsBilling((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "valid")) {
				if (jsonParserFieldValue != null) {
					placedOrder.setValid((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowStatusInfo")) {

				if (jsonParserFieldValue != null) {
					placedOrder.setWorkflowStatusInfo(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
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