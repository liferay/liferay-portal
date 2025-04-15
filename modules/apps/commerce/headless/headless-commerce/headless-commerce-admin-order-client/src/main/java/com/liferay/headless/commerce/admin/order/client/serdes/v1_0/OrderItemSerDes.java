/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderItem;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.VirtualItem;
import com.liferay.headless.commerce.admin.order.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class OrderItemSerDes {

	public static OrderItem toDTO(String json) {
		OrderItemJSONParser orderItemJSONParser = new OrderItemJSONParser();

		return orderItemJSONParser.parseToDTO(json);
	}

	public static OrderItem[] toDTOs(String json) {
		OrderItemJSONParser orderItemJSONParser = new OrderItemJSONParser();

		return orderItemJSONParser.parseToDTOs(json);
	}

	public static String toJSON(OrderItem orderItem) {
		if (orderItem == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (orderItem.getBookedQuantityId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bookedQuantityId\": ");

			sb.append(orderItem.getBookedQuantityId());
		}

		if (orderItem.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < orderItem.getCustomFields().length; i++) {
				sb.append(orderItem.getCustomFields()[i]);

				if ((i + 1) < orderItem.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (orderItem.getDecimalQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"decimalQuantity\": ");

			sb.append(orderItem.getDecimalQuantity());
		}

		if (orderItem.getDeliveryGroup() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliveryGroup\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getDeliveryGroup()));

			sb.append("\"");
		}

		if (orderItem.getDeliveryGroupName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliveryGroupName\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getDeliveryGroupName()));

			sb.append("\"");
		}

		if (orderItem.getDiscountAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountAmount\": ");

			sb.append(orderItem.getDiscountAmount());
		}

		if (orderItem.getDiscountManuallyAdjusted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountManuallyAdjusted\": ");

			sb.append(orderItem.getDiscountManuallyAdjusted());
		}

		if (orderItem.getDiscountPercentageLevel1() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel1\": ");

			sb.append(orderItem.getDiscountPercentageLevel1());
		}

		if (orderItem.getDiscountPercentageLevel1WithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel1WithTaxAmount\": ");

			sb.append(orderItem.getDiscountPercentageLevel1WithTaxAmount());
		}

		if (orderItem.getDiscountPercentageLevel2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel2\": ");

			sb.append(orderItem.getDiscountPercentageLevel2());
		}

		if (orderItem.getDiscountPercentageLevel2WithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel2WithTaxAmount\": ");

			sb.append(orderItem.getDiscountPercentageLevel2WithTaxAmount());
		}

		if (orderItem.getDiscountPercentageLevel3() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel3\": ");

			sb.append(orderItem.getDiscountPercentageLevel3());
		}

		if (orderItem.getDiscountPercentageLevel3WithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel3WithTaxAmount\": ");

			sb.append(orderItem.getDiscountPercentageLevel3WithTaxAmount());
		}

		if (orderItem.getDiscountPercentageLevel4() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel4\": ");

			sb.append(orderItem.getDiscountPercentageLevel4());
		}

		if (orderItem.getDiscountPercentageLevel4WithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel4WithTaxAmount\": ");

			sb.append(orderItem.getDiscountPercentageLevel4WithTaxAmount());
		}

		if (orderItem.getDiscountWithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountWithTaxAmount\": ");

			sb.append(orderItem.getDiscountWithTaxAmount());
		}

		if (orderItem.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (orderItem.getFinalPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"finalPrice\": ");

			sb.append(orderItem.getFinalPrice());
		}

		if (orderItem.getFinalPriceWithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"finalPriceWithTaxAmount\": ");

			sb.append(orderItem.getFinalPriceWithTaxAmount());
		}

		if (orderItem.getFormattedQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formattedQuantity\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getFormattedQuantity()));

			sb.append("\"");
		}

		if (orderItem.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(orderItem.getId());
		}

		if (orderItem.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(orderItem.getName()));
		}

		if (orderItem.getOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"options\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getOptions()));

			sb.append("\"");
		}

		if (orderItem.getOrderExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getOrderExternalReferenceCode()));

			sb.append("\"");
		}

		if (orderItem.getOrderId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderId\": ");

			sb.append(orderItem.getOrderId());
		}

		if (orderItem.getPriceManuallyAdjusted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceManuallyAdjusted\": ");

			sb.append(orderItem.getPriceManuallyAdjusted());
		}

		if (orderItem.getPrintedNote() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"printedNote\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getPrintedNote()));

			sb.append("\"");
		}

		if (orderItem.getPromoPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"promoPrice\": ");

			sb.append(orderItem.getPromoPrice());
		}

		if (orderItem.getPromoPriceWithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"promoPriceWithTaxAmount\": ");

			sb.append(orderItem.getPromoPriceWithTaxAmount());
		}

		if (orderItem.getQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"quantity\": ");

			sb.append(orderItem.getQuantity());
		}

		if (orderItem.getReplacedSku() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacedSku\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getReplacedSku()));

			sb.append("\"");
		}

		if (orderItem.getReplacedSkuExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacedSkuExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getReplacedSkuExternalReferenceCode()));

			sb.append("\"");
		}

		if (orderItem.getReplacedSkuId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacedSkuId\": ");

			sb.append(orderItem.getReplacedSkuId());
		}

		if (orderItem.getRequestedDeliveryDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"requestedDeliveryDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					orderItem.getRequestedDeliveryDate()));

			sb.append("\"");
		}

		if (orderItem.getShippable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippable\": ");

			sb.append(orderItem.getShippable());
		}

		if (orderItem.getShippedQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippedQuantity\": ");

			sb.append(orderItem.getShippedQuantity());
		}

		if (orderItem.getShippingAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddress\": ");

			sb.append(String.valueOf(orderItem.getShippingAddress()));
		}

		if (orderItem.getShippingAddressExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddressExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(orderItem.getShippingAddressExternalReferenceCode()));

			sb.append("\"");
		}

		if (orderItem.getShippingAddressId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddressId\": ");

			sb.append(orderItem.getShippingAddressId());
		}

		if (orderItem.getSku() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sku\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getSku()));

			sb.append("\"");
		}

		if (orderItem.getSkuExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getSkuExternalReferenceCode()));

			sb.append("\"");
		}

		if (orderItem.getSkuId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuId\": ");

			sb.append(orderItem.getSkuId());
		}

		if (orderItem.getSubscription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscription\": ");

			sb.append(orderItem.getSubscription());
		}

		if (orderItem.getUnitOfMeasure() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitOfMeasure\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getUnitOfMeasure()));

			sb.append("\"");
		}

		if (orderItem.getUnitOfMeasureKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitOfMeasureKey\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getUnitOfMeasureKey()));

			sb.append("\"");
		}

		if (orderItem.getUnitPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitPrice\": ");

			sb.append(orderItem.getUnitPrice());
		}

		if (orderItem.getUnitPriceWithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitPriceWithTaxAmount\": ");

			sb.append(orderItem.getUnitPriceWithTaxAmount());
		}

		if (orderItem.getVirtualItemURLs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"virtualItemURLs\": ");

			sb.append("[");

			for (int i = 0; i < orderItem.getVirtualItemURLs().length; i++) {
				sb.append(_toJSON(orderItem.getVirtualItemURLs()[i]));

				if ((i + 1) < orderItem.getVirtualItemURLs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (orderItem.getVirtualItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"virtualItems\": ");

			sb.append("[");

			for (int i = 0; i < orderItem.getVirtualItems().length; i++) {
				sb.append(String.valueOf(orderItem.getVirtualItems()[i]));

				if ((i + 1) < orderItem.getVirtualItems().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OrderItemJSONParser orderItemJSONParser = new OrderItemJSONParser();

		return orderItemJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(OrderItem orderItem) {
		if (orderItem == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (orderItem.getBookedQuantityId() == null) {
			map.put("bookedQuantityId", null);
		}
		else {
			map.put(
				"bookedQuantityId",
				String.valueOf(orderItem.getBookedQuantityId()));
		}

		if (orderItem.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields", String.valueOf(orderItem.getCustomFields()));
		}

		if (orderItem.getDecimalQuantity() == null) {
			map.put("decimalQuantity", null);
		}
		else {
			map.put(
				"decimalQuantity",
				String.valueOf(orderItem.getDecimalQuantity()));
		}

		if (orderItem.getDeliveryGroup() == null) {
			map.put("deliveryGroup", null);
		}
		else {
			map.put(
				"deliveryGroup", String.valueOf(orderItem.getDeliveryGroup()));
		}

		if (orderItem.getDeliveryGroupName() == null) {
			map.put("deliveryGroupName", null);
		}
		else {
			map.put(
				"deliveryGroupName",
				String.valueOf(orderItem.getDeliveryGroupName()));
		}

		if (orderItem.getDiscountAmount() == null) {
			map.put("discountAmount", null);
		}
		else {
			map.put(
				"discountAmount",
				String.valueOf(orderItem.getDiscountAmount()));
		}

		if (orderItem.getDiscountManuallyAdjusted() == null) {
			map.put("discountManuallyAdjusted", null);
		}
		else {
			map.put(
				"discountManuallyAdjusted",
				String.valueOf(orderItem.getDiscountManuallyAdjusted()));
		}

		if (orderItem.getDiscountPercentageLevel1() == null) {
			map.put("discountPercentageLevel1", null);
		}
		else {
			map.put(
				"discountPercentageLevel1",
				String.valueOf(orderItem.getDiscountPercentageLevel1()));
		}

		if (orderItem.getDiscountPercentageLevel1WithTaxAmount() == null) {
			map.put("discountPercentageLevel1WithTaxAmount", null);
		}
		else {
			map.put(
				"discountPercentageLevel1WithTaxAmount",
				String.valueOf(
					orderItem.getDiscountPercentageLevel1WithTaxAmount()));
		}

		if (orderItem.getDiscountPercentageLevel2() == null) {
			map.put("discountPercentageLevel2", null);
		}
		else {
			map.put(
				"discountPercentageLevel2",
				String.valueOf(orderItem.getDiscountPercentageLevel2()));
		}

		if (orderItem.getDiscountPercentageLevel2WithTaxAmount() == null) {
			map.put("discountPercentageLevel2WithTaxAmount", null);
		}
		else {
			map.put(
				"discountPercentageLevel2WithTaxAmount",
				String.valueOf(
					orderItem.getDiscountPercentageLevel2WithTaxAmount()));
		}

		if (orderItem.getDiscountPercentageLevel3() == null) {
			map.put("discountPercentageLevel3", null);
		}
		else {
			map.put(
				"discountPercentageLevel3",
				String.valueOf(orderItem.getDiscountPercentageLevel3()));
		}

		if (orderItem.getDiscountPercentageLevel3WithTaxAmount() == null) {
			map.put("discountPercentageLevel3WithTaxAmount", null);
		}
		else {
			map.put(
				"discountPercentageLevel3WithTaxAmount",
				String.valueOf(
					orderItem.getDiscountPercentageLevel3WithTaxAmount()));
		}

		if (orderItem.getDiscountPercentageLevel4() == null) {
			map.put("discountPercentageLevel4", null);
		}
		else {
			map.put(
				"discountPercentageLevel4",
				String.valueOf(orderItem.getDiscountPercentageLevel4()));
		}

		if (orderItem.getDiscountPercentageLevel4WithTaxAmount() == null) {
			map.put("discountPercentageLevel4WithTaxAmount", null);
		}
		else {
			map.put(
				"discountPercentageLevel4WithTaxAmount",
				String.valueOf(
					orderItem.getDiscountPercentageLevel4WithTaxAmount()));
		}

		if (orderItem.getDiscountWithTaxAmount() == null) {
			map.put("discountWithTaxAmount", null);
		}
		else {
			map.put(
				"discountWithTaxAmount",
				String.valueOf(orderItem.getDiscountWithTaxAmount()));
		}

		if (orderItem.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(orderItem.getExternalReferenceCode()));
		}

		if (orderItem.getFinalPrice() == null) {
			map.put("finalPrice", null);
		}
		else {
			map.put("finalPrice", String.valueOf(orderItem.getFinalPrice()));
		}

		if (orderItem.getFinalPriceWithTaxAmount() == null) {
			map.put("finalPriceWithTaxAmount", null);
		}
		else {
			map.put(
				"finalPriceWithTaxAmount",
				String.valueOf(orderItem.getFinalPriceWithTaxAmount()));
		}

		if (orderItem.getFormattedQuantity() == null) {
			map.put("formattedQuantity", null);
		}
		else {
			map.put(
				"formattedQuantity",
				String.valueOf(orderItem.getFormattedQuantity()));
		}

		if (orderItem.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(orderItem.getId()));
		}

		if (orderItem.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(orderItem.getName()));
		}

		if (orderItem.getOptions() == null) {
			map.put("options", null);
		}
		else {
			map.put("options", String.valueOf(orderItem.getOptions()));
		}

		if (orderItem.getOrderExternalReferenceCode() == null) {
			map.put("orderExternalReferenceCode", null);
		}
		else {
			map.put(
				"orderExternalReferenceCode",
				String.valueOf(orderItem.getOrderExternalReferenceCode()));
		}

		if (orderItem.getOrderId() == null) {
			map.put("orderId", null);
		}
		else {
			map.put("orderId", String.valueOf(orderItem.getOrderId()));
		}

		if (orderItem.getPriceManuallyAdjusted() == null) {
			map.put("priceManuallyAdjusted", null);
		}
		else {
			map.put(
				"priceManuallyAdjusted",
				String.valueOf(orderItem.getPriceManuallyAdjusted()));
		}

		if (orderItem.getPrintedNote() == null) {
			map.put("printedNote", null);
		}
		else {
			map.put("printedNote", String.valueOf(orderItem.getPrintedNote()));
		}

		if (orderItem.getPromoPrice() == null) {
			map.put("promoPrice", null);
		}
		else {
			map.put("promoPrice", String.valueOf(orderItem.getPromoPrice()));
		}

		if (orderItem.getPromoPriceWithTaxAmount() == null) {
			map.put("promoPriceWithTaxAmount", null);
		}
		else {
			map.put(
				"promoPriceWithTaxAmount",
				String.valueOf(orderItem.getPromoPriceWithTaxAmount()));
		}

		if (orderItem.getQuantity() == null) {
			map.put("quantity", null);
		}
		else {
			map.put("quantity", String.valueOf(orderItem.getQuantity()));
		}

		if (orderItem.getReplacedSku() == null) {
			map.put("replacedSku", null);
		}
		else {
			map.put("replacedSku", String.valueOf(orderItem.getReplacedSku()));
		}

		if (orderItem.getReplacedSkuExternalReferenceCode() == null) {
			map.put("replacedSkuExternalReferenceCode", null);
		}
		else {
			map.put(
				"replacedSkuExternalReferenceCode",
				String.valueOf(
					orderItem.getReplacedSkuExternalReferenceCode()));
		}

		if (orderItem.getReplacedSkuId() == null) {
			map.put("replacedSkuId", null);
		}
		else {
			map.put(
				"replacedSkuId", String.valueOf(orderItem.getReplacedSkuId()));
		}

		if (orderItem.getRequestedDeliveryDate() == null) {
			map.put("requestedDeliveryDate", null);
		}
		else {
			map.put(
				"requestedDeliveryDate",
				liferayToJSONDateFormat.format(
					orderItem.getRequestedDeliveryDate()));
		}

		if (orderItem.getShippable() == null) {
			map.put("shippable", null);
		}
		else {
			map.put("shippable", String.valueOf(orderItem.getShippable()));
		}

		if (orderItem.getShippedQuantity() == null) {
			map.put("shippedQuantity", null);
		}
		else {
			map.put(
				"shippedQuantity",
				String.valueOf(orderItem.getShippedQuantity()));
		}

		if (orderItem.getShippingAddress() == null) {
			map.put("shippingAddress", null);
		}
		else {
			map.put(
				"shippingAddress",
				String.valueOf(orderItem.getShippingAddress()));
		}

		if (orderItem.getShippingAddressExternalReferenceCode() == null) {
			map.put("shippingAddressExternalReferenceCode", null);
		}
		else {
			map.put(
				"shippingAddressExternalReferenceCode",
				String.valueOf(
					orderItem.getShippingAddressExternalReferenceCode()));
		}

		if (orderItem.getShippingAddressId() == null) {
			map.put("shippingAddressId", null);
		}
		else {
			map.put(
				"shippingAddressId",
				String.valueOf(orderItem.getShippingAddressId()));
		}

		if (orderItem.getSku() == null) {
			map.put("sku", null);
		}
		else {
			map.put("sku", String.valueOf(orderItem.getSku()));
		}

		if (orderItem.getSkuExternalReferenceCode() == null) {
			map.put("skuExternalReferenceCode", null);
		}
		else {
			map.put(
				"skuExternalReferenceCode",
				String.valueOf(orderItem.getSkuExternalReferenceCode()));
		}

		if (orderItem.getSkuId() == null) {
			map.put("skuId", null);
		}
		else {
			map.put("skuId", String.valueOf(orderItem.getSkuId()));
		}

		if (orderItem.getSubscription() == null) {
			map.put("subscription", null);
		}
		else {
			map.put(
				"subscription", String.valueOf(orderItem.getSubscription()));
		}

		if (orderItem.getUnitOfMeasure() == null) {
			map.put("unitOfMeasure", null);
		}
		else {
			map.put(
				"unitOfMeasure", String.valueOf(orderItem.getUnitOfMeasure()));
		}

		if (orderItem.getUnitOfMeasureKey() == null) {
			map.put("unitOfMeasureKey", null);
		}
		else {
			map.put(
				"unitOfMeasureKey",
				String.valueOf(orderItem.getUnitOfMeasureKey()));
		}

		if (orderItem.getUnitPrice() == null) {
			map.put("unitPrice", null);
		}
		else {
			map.put("unitPrice", String.valueOf(orderItem.getUnitPrice()));
		}

		if (orderItem.getUnitPriceWithTaxAmount() == null) {
			map.put("unitPriceWithTaxAmount", null);
		}
		else {
			map.put(
				"unitPriceWithTaxAmount",
				String.valueOf(orderItem.getUnitPriceWithTaxAmount()));
		}

		if (orderItem.getVirtualItemURLs() == null) {
			map.put("virtualItemURLs", null);
		}
		else {
			map.put(
				"virtualItemURLs",
				String.valueOf(orderItem.getVirtualItemURLs()));
		}

		if (orderItem.getVirtualItems() == null) {
			map.put("virtualItems", null);
		}
		else {
			map.put(
				"virtualItems", String.valueOf(orderItem.getVirtualItems()));
		}

		return map;
	}

	public static class OrderItemJSONParser extends BaseJSONParser<OrderItem> {

		@Override
		protected OrderItem createDTO() {
			return new OrderItem();
		}

		@Override
		protected OrderItem[] createDTOArray(int size) {
			return new OrderItem[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "bookedQuantityId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "decimalQuantity")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "deliveryGroup")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "deliveryGroupName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "discountAmount")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountManuallyAdjusted")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountPercentageLevel1")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"discountPercentageLevel1WithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountPercentageLevel2")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"discountPercentageLevel2WithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountPercentageLevel3")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"discountPercentageLevel3WithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountPercentageLevel4")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"discountPercentageLevel4WithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountWithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "finalPrice")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "finalPriceWithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "formattedQuantity")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "orderExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceManuallyAdjusted")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "printedNote")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "promoPrice")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "promoPriceWithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "quantity")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "replacedSku")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"replacedSkuExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "replacedSkuId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "requestedDeliveryDate")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shippable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shippedQuantity")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAddress")) {
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
			else if (Objects.equals(
						jsonParserFieldName, "skuExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "skuId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "subscription")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "unitOfMeasure")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "unitOfMeasureKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "unitPrice")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "unitPriceWithTaxAmount")) {

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
			OrderItem orderItem, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "bookedQuantityId")) {
				if (jsonParserFieldValue != null) {
					orderItem.setBookedQuantityId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.commerce.admin.order.client.custom.
						field.CustomField[] customFieldsArray = new
						com.liferay.headless.commerce.admin.order.client.custom.
							field.CustomField[jsonParserFieldValues.length];

					for (int i = 0; i < customFieldsArray.length; i++) {
						customFieldsArray[i] =
							com.liferay.headless.commerce.admin.order.client.
								custom.field.CustomField.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					orderItem.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "decimalQuantity")) {
				if (jsonParserFieldValue != null) {
					orderItem.setDecimalQuantity(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deliveryGroup")) {
				if (jsonParserFieldValue != null) {
					orderItem.setDeliveryGroup((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deliveryGroupName")) {
				if (jsonParserFieldValue != null) {
					orderItem.setDeliveryGroupName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "discountAmount")) {
				if (jsonParserFieldValue != null) {
					orderItem.setDiscountAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountManuallyAdjusted")) {

				if (jsonParserFieldValue != null) {
					orderItem.setDiscountManuallyAdjusted(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountPercentageLevel1")) {

				if (jsonParserFieldValue != null) {
					orderItem.setDiscountPercentageLevel1(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"discountPercentageLevel1WithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					orderItem.setDiscountPercentageLevel1WithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountPercentageLevel2")) {

				if (jsonParserFieldValue != null) {
					orderItem.setDiscountPercentageLevel2(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"discountPercentageLevel2WithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					orderItem.setDiscountPercentageLevel2WithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountPercentageLevel3")) {

				if (jsonParserFieldValue != null) {
					orderItem.setDiscountPercentageLevel3(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"discountPercentageLevel3WithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					orderItem.setDiscountPercentageLevel3WithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountPercentageLevel4")) {

				if (jsonParserFieldValue != null) {
					orderItem.setDiscountPercentageLevel4(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"discountPercentageLevel4WithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					orderItem.setDiscountPercentageLevel4WithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountWithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					orderItem.setDiscountWithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					orderItem.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "finalPrice")) {
				if (jsonParserFieldValue != null) {
					orderItem.setFinalPrice(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "finalPriceWithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					orderItem.setFinalPriceWithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "formattedQuantity")) {
				if (jsonParserFieldValue != null) {
					orderItem.setFormattedQuantity(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					orderItem.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					orderItem.setName(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				if (jsonParserFieldValue != null) {
					orderItem.setOptions((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "orderExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					orderItem.setOrderExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderId")) {
				if (jsonParserFieldValue != null) {
					orderItem.setOrderId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceManuallyAdjusted")) {

				if (jsonParserFieldValue != null) {
					orderItem.setPriceManuallyAdjusted(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "printedNote")) {
				if (jsonParserFieldValue != null) {
					orderItem.setPrintedNote((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "promoPrice")) {
				if (jsonParserFieldValue != null) {
					orderItem.setPromoPrice(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "promoPriceWithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					orderItem.setPromoPriceWithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "quantity")) {
				if (jsonParserFieldValue != null) {
					orderItem.setQuantity(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "replacedSku")) {
				if (jsonParserFieldValue != null) {
					orderItem.setReplacedSku((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"replacedSkuExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					orderItem.setReplacedSkuExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "replacedSkuId")) {
				if (jsonParserFieldValue != null) {
					orderItem.setReplacedSkuId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "requestedDeliveryDate")) {

				if (jsonParserFieldValue != null) {
					orderItem.setRequestedDeliveryDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippable")) {
				if (jsonParserFieldValue != null) {
					orderItem.setShippable((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippedQuantity")) {
				if (jsonParserFieldValue != null) {
					orderItem.setShippedQuantity(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAddress")) {
				if (jsonParserFieldValue != null) {
					orderItem.setShippingAddress(
						ShippingAddressSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingAddressExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					orderItem.setShippingAddressExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAddressId")) {
				if (jsonParserFieldValue != null) {
					orderItem.setShippingAddressId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sku")) {
				if (jsonParserFieldValue != null) {
					orderItem.setSku((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "skuExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					orderItem.setSkuExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skuId")) {
				if (jsonParserFieldValue != null) {
					orderItem.setSkuId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subscription")) {
				if (jsonParserFieldValue != null) {
					orderItem.setSubscription((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "unitOfMeasure")) {
				if (jsonParserFieldValue != null) {
					orderItem.setUnitOfMeasure((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "unitOfMeasureKey")) {
				if (jsonParserFieldValue != null) {
					orderItem.setUnitOfMeasureKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "unitPrice")) {
				if (jsonParserFieldValue != null) {
					orderItem.setUnitPrice(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "unitPriceWithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					orderItem.setUnitPriceWithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "virtualItemURLs")) {
				if (jsonParserFieldValue != null) {
					orderItem.setVirtualItemURLs(
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

					orderItem.setVirtualItems(virtualItemsArray);
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