/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.order.client.dto.v1_0.Order;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderItem;
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
public class OrderSerDes {

	public static Order toDTO(String json) {
		OrderJSONParser orderJSONParser = new OrderJSONParser();

		return orderJSONParser.parseToDTO(json);
	}

	public static Order[] toDTOs(String json) {
		OrderJSONParser orderJSONParser = new OrderJSONParser();

		return orderJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Order order) {
		if (order == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (order.getAccount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"account\": ");

			sb.append(String.valueOf(order.getAccount()));
		}

		if (order.getAccountExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(order.getAccountExternalReferenceCode()));

			sb.append("\"");
		}

		if (order.getAccountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountId\": ");

			sb.append(order.getAccountId());
		}

		if (order.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(order.getActions()));
		}

		if (order.getAdvanceStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"advanceStatus\": ");

			sb.append("\"");

			sb.append(_escape(order.getAdvanceStatus()));

			sb.append("\"");
		}

		if (order.getBillingAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"billingAddress\": ");

			sb.append(String.valueOf(order.getBillingAddress()));
		}

		if (order.getBillingAddressExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"billingAddressExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(order.getBillingAddressExternalReferenceCode()));

			sb.append("\"");
		}

		if (order.getBillingAddressId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"billingAddressId\": ");

			sb.append(order.getBillingAddressId());
		}

		if (order.getChannel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channel\": ");

			sb.append(String.valueOf(order.getChannel()));
		}

		if (order.getChannelExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(order.getChannelExternalReferenceCode()));

			sb.append("\"");
		}

		if (order.getChannelId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelId\": ");

			sb.append(order.getChannelId());
		}

		if (order.getCouponCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"couponCode\": ");

			sb.append("\"");

			sb.append(_escape(order.getCouponCode()));

			sb.append("\"");
		}

		if (order.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(order.getCreateDate()));

			sb.append("\"");
		}

		if (order.getCreatorEmailAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creatorEmailAddress\": ");

			sb.append("\"");

			sb.append(_escape(order.getCreatorEmailAddress()));

			sb.append("\"");
		}

		if (order.getCurrencyCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyCode\": ");

			sb.append("\"");

			sb.append(_escape(order.getCurrencyCode()));

			sb.append("\"");
		}

		if (order.getCurrencyExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(order.getCurrencyExternalReferenceCode()));

			sb.append("\"");
		}

		if (order.getCurrencyId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyId\": ");

			sb.append(order.getCurrencyId());
		}

		if (order.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(order.getCustomFields()));
		}

		if (order.getDeliveryTermDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliveryTermDescription\": ");

			sb.append("\"");

			sb.append(_escape(order.getDeliveryTermDescription()));

			sb.append("\"");
		}

		if (order.getDeliveryTermExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliveryTermExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(order.getDeliveryTermExternalReferenceCode()));

			sb.append("\"");
		}

		if (order.getDeliveryTermId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliveryTermId\": ");

			sb.append(order.getDeliveryTermId());
		}

		if (order.getDeliveryTermName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deliveryTermName\": ");

			sb.append("\"");

			sb.append(_escape(order.getDeliveryTermName()));

			sb.append("\"");
		}

		if (order.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(order.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (order.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(order.getId());
		}

		if (order.getLastPriceUpdateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastPriceUpdateDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(order.getLastPriceUpdateDate()));

			sb.append("\"");
		}

		if (order.getModifiedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifiedDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(order.getModifiedDate()));

			sb.append("\"");
		}

		if (order.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(order.getName()));

			sb.append("\"");
		}

		if (order.getOrderDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(order.getOrderDate()));

			sb.append("\"");
		}

		if (order.getOrderItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderItems\": ");

			sb.append("[");

			for (int i = 0; i < order.getOrderItems().length; i++) {
				sb.append(String.valueOf(order.getOrderItems()[i]));

				if ((i + 1) < order.getOrderItems().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (order.getOrderStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderStatus\": ");

			sb.append(order.getOrderStatus());
		}

		if (order.getOrderStatusInfo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderStatusInfo\": ");

			sb.append(String.valueOf(order.getOrderStatusInfo()));
		}

		if (order.getOrderTypeExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(order.getOrderTypeExternalReferenceCode()));

			sb.append("\"");
		}

		if (order.getOrderTypeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeId\": ");

			sb.append(order.getOrderTypeId());
		}

		if (order.getPaymentMethod() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentMethod\": ");

			sb.append("\"");

			sb.append(_escape(order.getPaymentMethod()));

			sb.append("\"");
		}

		if (order.getPaymentStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentStatus\": ");

			sb.append(order.getPaymentStatus());
		}

		if (order.getPaymentStatusInfo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentStatusInfo\": ");

			sb.append(String.valueOf(order.getPaymentStatusInfo()));
		}

		if (order.getPaymentTermDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentTermDescription\": ");

			sb.append("\"");

			sb.append(_escape(order.getPaymentTermDescription()));

			sb.append("\"");
		}

		if (order.getPaymentTermExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentTermExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(order.getPaymentTermExternalReferenceCode()));

			sb.append("\"");
		}

		if (order.getPaymentTermId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentTermId\": ");

			sb.append(order.getPaymentTermId());
		}

		if (order.getPaymentTermName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentTermName\": ");

			sb.append("\"");

			sb.append(_escape(order.getPaymentTermName()));

			sb.append("\"");
		}

		if (order.getPrintedNote() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"printedNote\": ");

			sb.append("\"");

			sb.append(_escape(order.getPrintedNote()));

			sb.append("\"");
		}

		if (order.getPurchaseOrderNumber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"purchaseOrderNumber\": ");

			sb.append("\"");

			sb.append(_escape(order.getPurchaseOrderNumber()));

			sb.append("\"");
		}

		if (order.getRequestedDeliveryDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"requestedDeliveryDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					order.getRequestedDeliveryDate()));

			sb.append("\"");
		}

		if (order.getShippable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippable\": ");

			sb.append(order.getShippable());
		}

		if (order.getShippingAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddress\": ");

			sb.append(String.valueOf(order.getShippingAddress()));
		}

		if (order.getShippingAddressExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddressExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(order.getShippingAddressExternalReferenceCode()));

			sb.append("\"");
		}

		if (order.getShippingAddressId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAddressId\": ");

			sb.append(order.getShippingAddressId());
		}

		if (order.getShippingAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAmount\": ");

			sb.append(order.getShippingAmount());
		}

		if (order.getShippingAmountFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(order.getShippingAmountFormatted()));

			sb.append("\"");
		}

		if (order.getShippingAmountValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingAmountValue\": ");

			sb.append(order.getShippingAmountValue());
		}

		if (order.getShippingDiscountAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountAmount\": ");

			sb.append(order.getShippingDiscountAmount());
		}

		if (order.getShippingDiscountAmountFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(order.getShippingDiscountAmountFormatted()));

			sb.append("\"");
		}

		if (order.getShippingDiscountAmountValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountAmountValue\": ");

			sb.append(order.getShippingDiscountAmountValue());
		}

		if (order.getShippingDiscountPercentageLevel1() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentageLevel1\": ");

			sb.append(order.getShippingDiscountPercentageLevel1());
		}

		if (order.getShippingDiscountPercentageLevel1WithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentageLevel1WithTaxAmount\": ");

			sb.append(order.getShippingDiscountPercentageLevel1WithTaxAmount());
		}

		if (order.getShippingDiscountPercentageLevel2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentageLevel2\": ");

			sb.append(order.getShippingDiscountPercentageLevel2());
		}

		if (order.getShippingDiscountPercentageLevel2WithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentageLevel2WithTaxAmount\": ");

			sb.append(order.getShippingDiscountPercentageLevel2WithTaxAmount());
		}

		if (order.getShippingDiscountPercentageLevel3() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentageLevel3\": ");

			sb.append(order.getShippingDiscountPercentageLevel3());
		}

		if (order.getShippingDiscountPercentageLevel3WithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentageLevel3WithTaxAmount\": ");

			sb.append(order.getShippingDiscountPercentageLevel3WithTaxAmount());
		}

		if (order.getShippingDiscountPercentageLevel4() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentageLevel4\": ");

			sb.append(order.getShippingDiscountPercentageLevel4());
		}

		if (order.getShippingDiscountPercentageLevel4WithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentageLevel4WithTaxAmount\": ");

			sb.append(order.getShippingDiscountPercentageLevel4WithTaxAmount());
		}

		if (order.getShippingDiscountWithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountWithTaxAmount\": ");

			sb.append(order.getShippingDiscountWithTaxAmount());
		}

		if (order.getShippingDiscountWithTaxAmountFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountWithTaxAmountFormatted\": ");

			sb.append("\"");

			sb.append(
				_escape(order.getShippingDiscountWithTaxAmountFormatted()));

			sb.append("\"");
		}

		if (order.getShippingMethod() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingMethod\": ");

			sb.append("\"");

			sb.append(_escape(order.getShippingMethod()));

			sb.append("\"");
		}

		if (order.getShippingOption() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingOption\": ");

			sb.append("\"");

			sb.append(_escape(order.getShippingOption()));

			sb.append("\"");
		}

		if (order.getShippingWithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingWithTaxAmount\": ");

			sb.append(order.getShippingWithTaxAmount());
		}

		if (order.getShippingWithTaxAmountFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingWithTaxAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(order.getShippingWithTaxAmountFormatted()));

			sb.append("\"");
		}

		if (order.getShippingWithTaxAmountValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingWithTaxAmountValue\": ");

			sb.append(order.getShippingWithTaxAmountValue());
		}

		if (order.getSubtotal() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotal\": ");

			sb.append(order.getSubtotal());
		}

		if (order.getSubtotalAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalAmount\": ");

			sb.append(order.getSubtotalAmount());
		}

		if (order.getSubtotalDiscountAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountAmount\": ");

			sb.append(order.getSubtotalDiscountAmount());
		}

		if (order.getSubtotalDiscountAmountFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(order.getSubtotalDiscountAmountFormatted()));

			sb.append("\"");
		}

		if (order.getSubtotalDiscountPercentageLevel1() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentageLevel1\": ");

			sb.append(order.getSubtotalDiscountPercentageLevel1());
		}

		if (order.getSubtotalDiscountPercentageLevel1WithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentageLevel1WithTaxAmount\": ");

			sb.append(order.getSubtotalDiscountPercentageLevel1WithTaxAmount());
		}

		if (order.getSubtotalDiscountPercentageLevel2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentageLevel2\": ");

			sb.append(order.getSubtotalDiscountPercentageLevel2());
		}

		if (order.getSubtotalDiscountPercentageLevel2WithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentageLevel2WithTaxAmount\": ");

			sb.append(order.getSubtotalDiscountPercentageLevel2WithTaxAmount());
		}

		if (order.getSubtotalDiscountPercentageLevel3() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentageLevel3\": ");

			sb.append(order.getSubtotalDiscountPercentageLevel3());
		}

		if (order.getSubtotalDiscountPercentageLevel3WithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentageLevel3WithTaxAmount\": ");

			sb.append(order.getSubtotalDiscountPercentageLevel3WithTaxAmount());
		}

		if (order.getSubtotalDiscountPercentageLevel4() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentageLevel4\": ");

			sb.append(order.getSubtotalDiscountPercentageLevel4());
		}

		if (order.getSubtotalDiscountPercentageLevel4WithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentageLevel4WithTaxAmount\": ");

			sb.append(order.getSubtotalDiscountPercentageLevel4WithTaxAmount());
		}

		if (order.getSubtotalDiscountWithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountWithTaxAmount\": ");

			sb.append(order.getSubtotalDiscountWithTaxAmount());
		}

		if (order.getSubtotalDiscountWithTaxAmountFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountWithTaxAmountFormatted\": ");

			sb.append("\"");

			sb.append(
				_escape(order.getSubtotalDiscountWithTaxAmountFormatted()));

			sb.append("\"");
		}

		if (order.getSubtotalFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalFormatted\": ");

			sb.append("\"");

			sb.append(_escape(order.getSubtotalFormatted()));

			sb.append("\"");
		}

		if (order.getSubtotalWithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalWithTaxAmount\": ");

			sb.append(order.getSubtotalWithTaxAmount());
		}

		if (order.getSubtotalWithTaxAmountFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalWithTaxAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(order.getSubtotalWithTaxAmountFormatted()));

			sb.append("\"");
		}

		if (order.getSubtotalWithTaxAmountValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalWithTaxAmountValue\": ");

			sb.append(order.getSubtotalWithTaxAmountValue());
		}

		if (order.getTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxAmount\": ");

			sb.append(order.getTaxAmount());
		}

		if (order.getTaxAmountFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(order.getTaxAmountFormatted()));

			sb.append("\"");
		}

		if (order.getTaxAmountValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxAmountValue\": ");

			sb.append(order.getTaxAmountValue());
		}

		if (order.getTotal() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"total\": ");

			sb.append(order.getTotal());
		}

		if (order.getTotalAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalAmount\": ");

			sb.append(order.getTotalAmount());
		}

		if (order.getTotalDiscountAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountAmount\": ");

			sb.append(order.getTotalDiscountAmount());
		}

		if (order.getTotalDiscountAmountFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(order.getTotalDiscountAmountFormatted()));

			sb.append("\"");
		}

		if (order.getTotalDiscountAmountValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountAmountValue\": ");

			sb.append(order.getTotalDiscountAmountValue());
		}

		if (order.getTotalDiscountPercentageLevel1() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentageLevel1\": ");

			sb.append(order.getTotalDiscountPercentageLevel1());
		}

		if (order.getTotalDiscountPercentageLevel1WithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentageLevel1WithTaxAmount\": ");

			sb.append(order.getTotalDiscountPercentageLevel1WithTaxAmount());
		}

		if (order.getTotalDiscountPercentageLevel2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentageLevel2\": ");

			sb.append(order.getTotalDiscountPercentageLevel2());
		}

		if (order.getTotalDiscountPercentageLevel2WithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentageLevel2WithTaxAmount\": ");

			sb.append(order.getTotalDiscountPercentageLevel2WithTaxAmount());
		}

		if (order.getTotalDiscountPercentageLevel3() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentageLevel3\": ");

			sb.append(order.getTotalDiscountPercentageLevel3());
		}

		if (order.getTotalDiscountPercentageLevel3WithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentageLevel3WithTaxAmount\": ");

			sb.append(order.getTotalDiscountPercentageLevel3WithTaxAmount());
		}

		if (order.getTotalDiscountPercentageLevel4() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentageLevel4\": ");

			sb.append(order.getTotalDiscountPercentageLevel4());
		}

		if (order.getTotalDiscountPercentageLevel4WithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentageLevel4WithTaxAmount\": ");

			sb.append(order.getTotalDiscountPercentageLevel4WithTaxAmount());
		}

		if (order.getTotalDiscountWithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountWithTaxAmount\": ");

			sb.append(order.getTotalDiscountWithTaxAmount());
		}

		if (order.getTotalDiscountWithTaxAmountFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountWithTaxAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(order.getTotalDiscountWithTaxAmountFormatted()));

			sb.append("\"");
		}

		if (order.getTotalDiscountWithTaxAmountValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountWithTaxAmountValue\": ");

			sb.append(order.getTotalDiscountWithTaxAmountValue());
		}

		if (order.getTotalFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalFormatted\": ");

			sb.append("\"");

			sb.append(_escape(order.getTotalFormatted()));

			sb.append("\"");
		}

		if (order.getTotalWithTaxAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalWithTaxAmount\": ");

			sb.append(order.getTotalWithTaxAmount());
		}

		if (order.getTotalWithTaxAmountFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalWithTaxAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(order.getTotalWithTaxAmountFormatted()));

			sb.append("\"");
		}

		if (order.getTotalWithTaxAmountValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalWithTaxAmountValue\": ");

			sb.append(order.getTotalWithTaxAmountValue());
		}

		if (order.getTransactionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"transactionId\": ");

			sb.append("\"");

			sb.append(_escape(order.getTransactionId()));

			sb.append("\"");
		}

		if (order.getWorkflowStatusInfo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowStatusInfo\": ");

			sb.append(String.valueOf(order.getWorkflowStatusInfo()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OrderJSONParser orderJSONParser = new OrderJSONParser();

		return orderJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Order order) {
		if (order == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (order.getAccount() == null) {
			map.put("account", null);
		}
		else {
			map.put("account", String.valueOf(order.getAccount()));
		}

		if (order.getAccountExternalReferenceCode() == null) {
			map.put("accountExternalReferenceCode", null);
		}
		else {
			map.put(
				"accountExternalReferenceCode",
				String.valueOf(order.getAccountExternalReferenceCode()));
		}

		if (order.getAccountId() == null) {
			map.put("accountId", null);
		}
		else {
			map.put("accountId", String.valueOf(order.getAccountId()));
		}

		if (order.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(order.getActions()));
		}

		if (order.getAdvanceStatus() == null) {
			map.put("advanceStatus", null);
		}
		else {
			map.put("advanceStatus", String.valueOf(order.getAdvanceStatus()));
		}

		if (order.getBillingAddress() == null) {
			map.put("billingAddress", null);
		}
		else {
			map.put(
				"billingAddress", String.valueOf(order.getBillingAddress()));
		}

		if (order.getBillingAddressExternalReferenceCode() == null) {
			map.put("billingAddressExternalReferenceCode", null);
		}
		else {
			map.put(
				"billingAddressExternalReferenceCode",
				String.valueOf(order.getBillingAddressExternalReferenceCode()));
		}

		if (order.getBillingAddressId() == null) {
			map.put("billingAddressId", null);
		}
		else {
			map.put(
				"billingAddressId",
				String.valueOf(order.getBillingAddressId()));
		}

		if (order.getChannel() == null) {
			map.put("channel", null);
		}
		else {
			map.put("channel", String.valueOf(order.getChannel()));
		}

		if (order.getChannelExternalReferenceCode() == null) {
			map.put("channelExternalReferenceCode", null);
		}
		else {
			map.put(
				"channelExternalReferenceCode",
				String.valueOf(order.getChannelExternalReferenceCode()));
		}

		if (order.getChannelId() == null) {
			map.put("channelId", null);
		}
		else {
			map.put("channelId", String.valueOf(order.getChannelId()));
		}

		if (order.getCouponCode() == null) {
			map.put("couponCode", null);
		}
		else {
			map.put("couponCode", String.valueOf(order.getCouponCode()));
		}

		if (order.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put(
				"createDate",
				liferayToJSONDateFormat.format(order.getCreateDate()));
		}

		if (order.getCreatorEmailAddress() == null) {
			map.put("creatorEmailAddress", null);
		}
		else {
			map.put(
				"creatorEmailAddress",
				String.valueOf(order.getCreatorEmailAddress()));
		}

		if (order.getCurrencyCode() == null) {
			map.put("currencyCode", null);
		}
		else {
			map.put("currencyCode", String.valueOf(order.getCurrencyCode()));
		}

		if (order.getCurrencyExternalReferenceCode() == null) {
			map.put("currencyExternalReferenceCode", null);
		}
		else {
			map.put(
				"currencyExternalReferenceCode",
				String.valueOf(order.getCurrencyExternalReferenceCode()));
		}

		if (order.getCurrencyId() == null) {
			map.put("currencyId", null);
		}
		else {
			map.put("currencyId", String.valueOf(order.getCurrencyId()));
		}

		if (order.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put("customFields", String.valueOf(order.getCustomFields()));
		}

		if (order.getDeliveryTermDescription() == null) {
			map.put("deliveryTermDescription", null);
		}
		else {
			map.put(
				"deliveryTermDescription",
				String.valueOf(order.getDeliveryTermDescription()));
		}

		if (order.getDeliveryTermExternalReferenceCode() == null) {
			map.put("deliveryTermExternalReferenceCode", null);
		}
		else {
			map.put(
				"deliveryTermExternalReferenceCode",
				String.valueOf(order.getDeliveryTermExternalReferenceCode()));
		}

		if (order.getDeliveryTermId() == null) {
			map.put("deliveryTermId", null);
		}
		else {
			map.put(
				"deliveryTermId", String.valueOf(order.getDeliveryTermId()));
		}

		if (order.getDeliveryTermName() == null) {
			map.put("deliveryTermName", null);
		}
		else {
			map.put(
				"deliveryTermName",
				String.valueOf(order.getDeliveryTermName()));
		}

		if (order.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(order.getExternalReferenceCode()));
		}

		if (order.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(order.getId()));
		}

		if (order.getLastPriceUpdateDate() == null) {
			map.put("lastPriceUpdateDate", null);
		}
		else {
			map.put(
				"lastPriceUpdateDate",
				liferayToJSONDateFormat.format(order.getLastPriceUpdateDate()));
		}

		if (order.getModifiedDate() == null) {
			map.put("modifiedDate", null);
		}
		else {
			map.put(
				"modifiedDate",
				liferayToJSONDateFormat.format(order.getModifiedDate()));
		}

		if (order.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(order.getName()));
		}

		if (order.getOrderDate() == null) {
			map.put("orderDate", null);
		}
		else {
			map.put(
				"orderDate",
				liferayToJSONDateFormat.format(order.getOrderDate()));
		}

		if (order.getOrderItems() == null) {
			map.put("orderItems", null);
		}
		else {
			map.put("orderItems", String.valueOf(order.getOrderItems()));
		}

		if (order.getOrderStatus() == null) {
			map.put("orderStatus", null);
		}
		else {
			map.put("orderStatus", String.valueOf(order.getOrderStatus()));
		}

		if (order.getOrderStatusInfo() == null) {
			map.put("orderStatusInfo", null);
		}
		else {
			map.put(
				"orderStatusInfo", String.valueOf(order.getOrderStatusInfo()));
		}

		if (order.getOrderTypeExternalReferenceCode() == null) {
			map.put("orderTypeExternalReferenceCode", null);
		}
		else {
			map.put(
				"orderTypeExternalReferenceCode",
				String.valueOf(order.getOrderTypeExternalReferenceCode()));
		}

		if (order.getOrderTypeId() == null) {
			map.put("orderTypeId", null);
		}
		else {
			map.put("orderTypeId", String.valueOf(order.getOrderTypeId()));
		}

		if (order.getPaymentMethod() == null) {
			map.put("paymentMethod", null);
		}
		else {
			map.put("paymentMethod", String.valueOf(order.getPaymentMethod()));
		}

		if (order.getPaymentStatus() == null) {
			map.put("paymentStatus", null);
		}
		else {
			map.put("paymentStatus", String.valueOf(order.getPaymentStatus()));
		}

		if (order.getPaymentStatusInfo() == null) {
			map.put("paymentStatusInfo", null);
		}
		else {
			map.put(
				"paymentStatusInfo",
				String.valueOf(order.getPaymentStatusInfo()));
		}

		if (order.getPaymentTermDescription() == null) {
			map.put("paymentTermDescription", null);
		}
		else {
			map.put(
				"paymentTermDescription",
				String.valueOf(order.getPaymentTermDescription()));
		}

		if (order.getPaymentTermExternalReferenceCode() == null) {
			map.put("paymentTermExternalReferenceCode", null);
		}
		else {
			map.put(
				"paymentTermExternalReferenceCode",
				String.valueOf(order.getPaymentTermExternalReferenceCode()));
		}

		if (order.getPaymentTermId() == null) {
			map.put("paymentTermId", null);
		}
		else {
			map.put("paymentTermId", String.valueOf(order.getPaymentTermId()));
		}

		if (order.getPaymentTermName() == null) {
			map.put("paymentTermName", null);
		}
		else {
			map.put(
				"paymentTermName", String.valueOf(order.getPaymentTermName()));
		}

		if (order.getPrintedNote() == null) {
			map.put("printedNote", null);
		}
		else {
			map.put("printedNote", String.valueOf(order.getPrintedNote()));
		}

		if (order.getPurchaseOrderNumber() == null) {
			map.put("purchaseOrderNumber", null);
		}
		else {
			map.put(
				"purchaseOrderNumber",
				String.valueOf(order.getPurchaseOrderNumber()));
		}

		if (order.getRequestedDeliveryDate() == null) {
			map.put("requestedDeliveryDate", null);
		}
		else {
			map.put(
				"requestedDeliveryDate",
				liferayToJSONDateFormat.format(
					order.getRequestedDeliveryDate()));
		}

		if (order.getShippable() == null) {
			map.put("shippable", null);
		}
		else {
			map.put("shippable", String.valueOf(order.getShippable()));
		}

		if (order.getShippingAddress() == null) {
			map.put("shippingAddress", null);
		}
		else {
			map.put(
				"shippingAddress", String.valueOf(order.getShippingAddress()));
		}

		if (order.getShippingAddressExternalReferenceCode() == null) {
			map.put("shippingAddressExternalReferenceCode", null);
		}
		else {
			map.put(
				"shippingAddressExternalReferenceCode",
				String.valueOf(
					order.getShippingAddressExternalReferenceCode()));
		}

		if (order.getShippingAddressId() == null) {
			map.put("shippingAddressId", null);
		}
		else {
			map.put(
				"shippingAddressId",
				String.valueOf(order.getShippingAddressId()));
		}

		if (order.getShippingAmount() == null) {
			map.put("shippingAmount", null);
		}
		else {
			map.put(
				"shippingAmount", String.valueOf(order.getShippingAmount()));
		}

		if (order.getShippingAmountFormatted() == null) {
			map.put("shippingAmountFormatted", null);
		}
		else {
			map.put(
				"shippingAmountFormatted",
				String.valueOf(order.getShippingAmountFormatted()));
		}

		if (order.getShippingAmountValue() == null) {
			map.put("shippingAmountValue", null);
		}
		else {
			map.put(
				"shippingAmountValue",
				String.valueOf(order.getShippingAmountValue()));
		}

		if (order.getShippingDiscountAmount() == null) {
			map.put("shippingDiscountAmount", null);
		}
		else {
			map.put(
				"shippingDiscountAmount",
				String.valueOf(order.getShippingDiscountAmount()));
		}

		if (order.getShippingDiscountAmountFormatted() == null) {
			map.put("shippingDiscountAmountFormatted", null);
		}
		else {
			map.put(
				"shippingDiscountAmountFormatted",
				String.valueOf(order.getShippingDiscountAmountFormatted()));
		}

		if (order.getShippingDiscountAmountValue() == null) {
			map.put("shippingDiscountAmountValue", null);
		}
		else {
			map.put(
				"shippingDiscountAmountValue",
				String.valueOf(order.getShippingDiscountAmountValue()));
		}

		if (order.getShippingDiscountPercentageLevel1() == null) {
			map.put("shippingDiscountPercentageLevel1", null);
		}
		else {
			map.put(
				"shippingDiscountPercentageLevel1",
				String.valueOf(order.getShippingDiscountPercentageLevel1()));
		}

		if (order.getShippingDiscountPercentageLevel1WithTaxAmount() == null) {
			map.put("shippingDiscountPercentageLevel1WithTaxAmount", null);
		}
		else {
			map.put(
				"shippingDiscountPercentageLevel1WithTaxAmount",
				String.valueOf(
					order.getShippingDiscountPercentageLevel1WithTaxAmount()));
		}

		if (order.getShippingDiscountPercentageLevel2() == null) {
			map.put("shippingDiscountPercentageLevel2", null);
		}
		else {
			map.put(
				"shippingDiscountPercentageLevel2",
				String.valueOf(order.getShippingDiscountPercentageLevel2()));
		}

		if (order.getShippingDiscountPercentageLevel2WithTaxAmount() == null) {
			map.put("shippingDiscountPercentageLevel2WithTaxAmount", null);
		}
		else {
			map.put(
				"shippingDiscountPercentageLevel2WithTaxAmount",
				String.valueOf(
					order.getShippingDiscountPercentageLevel2WithTaxAmount()));
		}

		if (order.getShippingDiscountPercentageLevel3() == null) {
			map.put("shippingDiscountPercentageLevel3", null);
		}
		else {
			map.put(
				"shippingDiscountPercentageLevel3",
				String.valueOf(order.getShippingDiscountPercentageLevel3()));
		}

		if (order.getShippingDiscountPercentageLevel3WithTaxAmount() == null) {
			map.put("shippingDiscountPercentageLevel3WithTaxAmount", null);
		}
		else {
			map.put(
				"shippingDiscountPercentageLevel3WithTaxAmount",
				String.valueOf(
					order.getShippingDiscountPercentageLevel3WithTaxAmount()));
		}

		if (order.getShippingDiscountPercentageLevel4() == null) {
			map.put("shippingDiscountPercentageLevel4", null);
		}
		else {
			map.put(
				"shippingDiscountPercentageLevel4",
				String.valueOf(order.getShippingDiscountPercentageLevel4()));
		}

		if (order.getShippingDiscountPercentageLevel4WithTaxAmount() == null) {
			map.put("shippingDiscountPercentageLevel4WithTaxAmount", null);
		}
		else {
			map.put(
				"shippingDiscountPercentageLevel4WithTaxAmount",
				String.valueOf(
					order.getShippingDiscountPercentageLevel4WithTaxAmount()));
		}

		if (order.getShippingDiscountWithTaxAmount() == null) {
			map.put("shippingDiscountWithTaxAmount", null);
		}
		else {
			map.put(
				"shippingDiscountWithTaxAmount",
				String.valueOf(order.getShippingDiscountWithTaxAmount()));
		}

		if (order.getShippingDiscountWithTaxAmountFormatted() == null) {
			map.put("shippingDiscountWithTaxAmountFormatted", null);
		}
		else {
			map.put(
				"shippingDiscountWithTaxAmountFormatted",
				String.valueOf(
					order.getShippingDiscountWithTaxAmountFormatted()));
		}

		if (order.getShippingMethod() == null) {
			map.put("shippingMethod", null);
		}
		else {
			map.put(
				"shippingMethod", String.valueOf(order.getShippingMethod()));
		}

		if (order.getShippingOption() == null) {
			map.put("shippingOption", null);
		}
		else {
			map.put(
				"shippingOption", String.valueOf(order.getShippingOption()));
		}

		if (order.getShippingWithTaxAmount() == null) {
			map.put("shippingWithTaxAmount", null);
		}
		else {
			map.put(
				"shippingWithTaxAmount",
				String.valueOf(order.getShippingWithTaxAmount()));
		}

		if (order.getShippingWithTaxAmountFormatted() == null) {
			map.put("shippingWithTaxAmountFormatted", null);
		}
		else {
			map.put(
				"shippingWithTaxAmountFormatted",
				String.valueOf(order.getShippingWithTaxAmountFormatted()));
		}

		if (order.getShippingWithTaxAmountValue() == null) {
			map.put("shippingWithTaxAmountValue", null);
		}
		else {
			map.put(
				"shippingWithTaxAmountValue",
				String.valueOf(order.getShippingWithTaxAmountValue()));
		}

		if (order.getSubtotal() == null) {
			map.put("subtotal", null);
		}
		else {
			map.put("subtotal", String.valueOf(order.getSubtotal()));
		}

		if (order.getSubtotalAmount() == null) {
			map.put("subtotalAmount", null);
		}
		else {
			map.put(
				"subtotalAmount", String.valueOf(order.getSubtotalAmount()));
		}

		if (order.getSubtotalDiscountAmount() == null) {
			map.put("subtotalDiscountAmount", null);
		}
		else {
			map.put(
				"subtotalDiscountAmount",
				String.valueOf(order.getSubtotalDiscountAmount()));
		}

		if (order.getSubtotalDiscountAmountFormatted() == null) {
			map.put("subtotalDiscountAmountFormatted", null);
		}
		else {
			map.put(
				"subtotalDiscountAmountFormatted",
				String.valueOf(order.getSubtotalDiscountAmountFormatted()));
		}

		if (order.getSubtotalDiscountPercentageLevel1() == null) {
			map.put("subtotalDiscountPercentageLevel1", null);
		}
		else {
			map.put(
				"subtotalDiscountPercentageLevel1",
				String.valueOf(order.getSubtotalDiscountPercentageLevel1()));
		}

		if (order.getSubtotalDiscountPercentageLevel1WithTaxAmount() == null) {
			map.put("subtotalDiscountPercentageLevel1WithTaxAmount", null);
		}
		else {
			map.put(
				"subtotalDiscountPercentageLevel1WithTaxAmount",
				String.valueOf(
					order.getSubtotalDiscountPercentageLevel1WithTaxAmount()));
		}

		if (order.getSubtotalDiscountPercentageLevel2() == null) {
			map.put("subtotalDiscountPercentageLevel2", null);
		}
		else {
			map.put(
				"subtotalDiscountPercentageLevel2",
				String.valueOf(order.getSubtotalDiscountPercentageLevel2()));
		}

		if (order.getSubtotalDiscountPercentageLevel2WithTaxAmount() == null) {
			map.put("subtotalDiscountPercentageLevel2WithTaxAmount", null);
		}
		else {
			map.put(
				"subtotalDiscountPercentageLevel2WithTaxAmount",
				String.valueOf(
					order.getSubtotalDiscountPercentageLevel2WithTaxAmount()));
		}

		if (order.getSubtotalDiscountPercentageLevel3() == null) {
			map.put("subtotalDiscountPercentageLevel3", null);
		}
		else {
			map.put(
				"subtotalDiscountPercentageLevel3",
				String.valueOf(order.getSubtotalDiscountPercentageLevel3()));
		}

		if (order.getSubtotalDiscountPercentageLevel3WithTaxAmount() == null) {
			map.put("subtotalDiscountPercentageLevel3WithTaxAmount", null);
		}
		else {
			map.put(
				"subtotalDiscountPercentageLevel3WithTaxAmount",
				String.valueOf(
					order.getSubtotalDiscountPercentageLevel3WithTaxAmount()));
		}

		if (order.getSubtotalDiscountPercentageLevel4() == null) {
			map.put("subtotalDiscountPercentageLevel4", null);
		}
		else {
			map.put(
				"subtotalDiscountPercentageLevel4",
				String.valueOf(order.getSubtotalDiscountPercentageLevel4()));
		}

		if (order.getSubtotalDiscountPercentageLevel4WithTaxAmount() == null) {
			map.put("subtotalDiscountPercentageLevel4WithTaxAmount", null);
		}
		else {
			map.put(
				"subtotalDiscountPercentageLevel4WithTaxAmount",
				String.valueOf(
					order.getSubtotalDiscountPercentageLevel4WithTaxAmount()));
		}

		if (order.getSubtotalDiscountWithTaxAmount() == null) {
			map.put("subtotalDiscountWithTaxAmount", null);
		}
		else {
			map.put(
				"subtotalDiscountWithTaxAmount",
				String.valueOf(order.getSubtotalDiscountWithTaxAmount()));
		}

		if (order.getSubtotalDiscountWithTaxAmountFormatted() == null) {
			map.put("subtotalDiscountWithTaxAmountFormatted", null);
		}
		else {
			map.put(
				"subtotalDiscountWithTaxAmountFormatted",
				String.valueOf(
					order.getSubtotalDiscountWithTaxAmountFormatted()));
		}

		if (order.getSubtotalFormatted() == null) {
			map.put("subtotalFormatted", null);
		}
		else {
			map.put(
				"subtotalFormatted",
				String.valueOf(order.getSubtotalFormatted()));
		}

		if (order.getSubtotalWithTaxAmount() == null) {
			map.put("subtotalWithTaxAmount", null);
		}
		else {
			map.put(
				"subtotalWithTaxAmount",
				String.valueOf(order.getSubtotalWithTaxAmount()));
		}

		if (order.getSubtotalWithTaxAmountFormatted() == null) {
			map.put("subtotalWithTaxAmountFormatted", null);
		}
		else {
			map.put(
				"subtotalWithTaxAmountFormatted",
				String.valueOf(order.getSubtotalWithTaxAmountFormatted()));
		}

		if (order.getSubtotalWithTaxAmountValue() == null) {
			map.put("subtotalWithTaxAmountValue", null);
		}
		else {
			map.put(
				"subtotalWithTaxAmountValue",
				String.valueOf(order.getSubtotalWithTaxAmountValue()));
		}

		if (order.getTaxAmount() == null) {
			map.put("taxAmount", null);
		}
		else {
			map.put("taxAmount", String.valueOf(order.getTaxAmount()));
		}

		if (order.getTaxAmountFormatted() == null) {
			map.put("taxAmountFormatted", null);
		}
		else {
			map.put(
				"taxAmountFormatted",
				String.valueOf(order.getTaxAmountFormatted()));
		}

		if (order.getTaxAmountValue() == null) {
			map.put("taxAmountValue", null);
		}
		else {
			map.put(
				"taxAmountValue", String.valueOf(order.getTaxAmountValue()));
		}

		if (order.getTotal() == null) {
			map.put("total", null);
		}
		else {
			map.put("total", String.valueOf(order.getTotal()));
		}

		if (order.getTotalAmount() == null) {
			map.put("totalAmount", null);
		}
		else {
			map.put("totalAmount", String.valueOf(order.getTotalAmount()));
		}

		if (order.getTotalDiscountAmount() == null) {
			map.put("totalDiscountAmount", null);
		}
		else {
			map.put(
				"totalDiscountAmount",
				String.valueOf(order.getTotalDiscountAmount()));
		}

		if (order.getTotalDiscountAmountFormatted() == null) {
			map.put("totalDiscountAmountFormatted", null);
		}
		else {
			map.put(
				"totalDiscountAmountFormatted",
				String.valueOf(order.getTotalDiscountAmountFormatted()));
		}

		if (order.getTotalDiscountAmountValue() == null) {
			map.put("totalDiscountAmountValue", null);
		}
		else {
			map.put(
				"totalDiscountAmountValue",
				String.valueOf(order.getTotalDiscountAmountValue()));
		}

		if (order.getTotalDiscountPercentageLevel1() == null) {
			map.put("totalDiscountPercentageLevel1", null);
		}
		else {
			map.put(
				"totalDiscountPercentageLevel1",
				String.valueOf(order.getTotalDiscountPercentageLevel1()));
		}

		if (order.getTotalDiscountPercentageLevel1WithTaxAmount() == null) {
			map.put("totalDiscountPercentageLevel1WithTaxAmount", null);
		}
		else {
			map.put(
				"totalDiscountPercentageLevel1WithTaxAmount",
				String.valueOf(
					order.getTotalDiscountPercentageLevel1WithTaxAmount()));
		}

		if (order.getTotalDiscountPercentageLevel2() == null) {
			map.put("totalDiscountPercentageLevel2", null);
		}
		else {
			map.put(
				"totalDiscountPercentageLevel2",
				String.valueOf(order.getTotalDiscountPercentageLevel2()));
		}

		if (order.getTotalDiscountPercentageLevel2WithTaxAmount() == null) {
			map.put("totalDiscountPercentageLevel2WithTaxAmount", null);
		}
		else {
			map.put(
				"totalDiscountPercentageLevel2WithTaxAmount",
				String.valueOf(
					order.getTotalDiscountPercentageLevel2WithTaxAmount()));
		}

		if (order.getTotalDiscountPercentageLevel3() == null) {
			map.put("totalDiscountPercentageLevel3", null);
		}
		else {
			map.put(
				"totalDiscountPercentageLevel3",
				String.valueOf(order.getTotalDiscountPercentageLevel3()));
		}

		if (order.getTotalDiscountPercentageLevel3WithTaxAmount() == null) {
			map.put("totalDiscountPercentageLevel3WithTaxAmount", null);
		}
		else {
			map.put(
				"totalDiscountPercentageLevel3WithTaxAmount",
				String.valueOf(
					order.getTotalDiscountPercentageLevel3WithTaxAmount()));
		}

		if (order.getTotalDiscountPercentageLevel4() == null) {
			map.put("totalDiscountPercentageLevel4", null);
		}
		else {
			map.put(
				"totalDiscountPercentageLevel4",
				String.valueOf(order.getTotalDiscountPercentageLevel4()));
		}

		if (order.getTotalDiscountPercentageLevel4WithTaxAmount() == null) {
			map.put("totalDiscountPercentageLevel4WithTaxAmount", null);
		}
		else {
			map.put(
				"totalDiscountPercentageLevel4WithTaxAmount",
				String.valueOf(
					order.getTotalDiscountPercentageLevel4WithTaxAmount()));
		}

		if (order.getTotalDiscountWithTaxAmount() == null) {
			map.put("totalDiscountWithTaxAmount", null);
		}
		else {
			map.put(
				"totalDiscountWithTaxAmount",
				String.valueOf(order.getTotalDiscountWithTaxAmount()));
		}

		if (order.getTotalDiscountWithTaxAmountFormatted() == null) {
			map.put("totalDiscountWithTaxAmountFormatted", null);
		}
		else {
			map.put(
				"totalDiscountWithTaxAmountFormatted",
				String.valueOf(order.getTotalDiscountWithTaxAmountFormatted()));
		}

		if (order.getTotalDiscountWithTaxAmountValue() == null) {
			map.put("totalDiscountWithTaxAmountValue", null);
		}
		else {
			map.put(
				"totalDiscountWithTaxAmountValue",
				String.valueOf(order.getTotalDiscountWithTaxAmountValue()));
		}

		if (order.getTotalFormatted() == null) {
			map.put("totalFormatted", null);
		}
		else {
			map.put(
				"totalFormatted", String.valueOf(order.getTotalFormatted()));
		}

		if (order.getTotalWithTaxAmount() == null) {
			map.put("totalWithTaxAmount", null);
		}
		else {
			map.put(
				"totalWithTaxAmount",
				String.valueOf(order.getTotalWithTaxAmount()));
		}

		if (order.getTotalWithTaxAmountFormatted() == null) {
			map.put("totalWithTaxAmountFormatted", null);
		}
		else {
			map.put(
				"totalWithTaxAmountFormatted",
				String.valueOf(order.getTotalWithTaxAmountFormatted()));
		}

		if (order.getTotalWithTaxAmountValue() == null) {
			map.put("totalWithTaxAmountValue", null);
		}
		else {
			map.put(
				"totalWithTaxAmountValue",
				String.valueOf(order.getTotalWithTaxAmountValue()));
		}

		if (order.getTransactionId() == null) {
			map.put("transactionId", null);
		}
		else {
			map.put("transactionId", String.valueOf(order.getTransactionId()));
		}

		if (order.getWorkflowStatusInfo() == null) {
			map.put("workflowStatusInfo", null);
		}
		else {
			map.put(
				"workflowStatusInfo",
				String.valueOf(order.getWorkflowStatusInfo()));
		}

		return map;
	}

	public static class OrderJSONParser extends BaseJSONParser<Order> {

		@Override
		protected Order createDTO() {
			return new Order();
		}

		@Override
		protected Order[] createDTOArray(int size) {
			return new Order[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "account")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "accountExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "accountId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "advanceStatus")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "billingAddress")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"billingAddressExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "billingAddressId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "channel")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "channelExternalReferenceCode")) {

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
			else if (Objects.equals(
						jsonParserFieldName, "creatorEmailAddress")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "currencyCode")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "currencyExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "currencyId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "deliveryTermDescription")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"deliveryTermExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "deliveryTermId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "deliveryTermName")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

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
			else if (Objects.equals(jsonParserFieldName, "orderDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderItems")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderStatus")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderStatusInfo")) {
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
			else if (Objects.equals(jsonParserFieldName, "paymentMethod")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "paymentStatus")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "paymentStatusInfo")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "paymentTermDescription")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"paymentTermExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "paymentTermId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "paymentTermName")) {
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
			else if (Objects.equals(jsonParserFieldName, "shippable")) {
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
			else if (Objects.equals(jsonParserFieldName, "shippingAmount")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingAmountFormatted")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingAmountValue")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingDiscountAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountAmountFormatted")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingDiscountAmountValue")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountPercentageLevel1")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountPercentageLevel1WithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountPercentageLevel2")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountPercentageLevel2WithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountPercentageLevel3")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountPercentageLevel3WithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountPercentageLevel4")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountPercentageLevel4WithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingDiscountWithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountWithTaxAmountFormatted")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shippingMethod")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shippingOption")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingWithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingWithTaxAmountFormatted")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingWithTaxAmountValue")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "subtotal")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "subtotalAmount")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "subtotalDiscountAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountAmountFormatted")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountPercentageLevel1")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountPercentageLevel1WithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountPercentageLevel2")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountPercentageLevel2WithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountPercentageLevel3")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountPercentageLevel3WithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountPercentageLevel4")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountPercentageLevel4WithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "subtotalDiscountWithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountWithTaxAmountFormatted")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "subtotalFormatted")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "subtotalWithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalWithTaxAmountFormatted")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "subtotalWithTaxAmountValue")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "taxAmount")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxAmountFormatted")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "taxAmountValue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "total")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "totalAmount")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountAmountFormatted")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountAmountValue")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountPercentageLevel1")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"totalDiscountPercentageLevel1WithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountPercentageLevel2")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"totalDiscountPercentageLevel2WithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountPercentageLevel3")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"totalDiscountPercentageLevel3WithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountPercentageLevel4")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"totalDiscountPercentageLevel4WithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountWithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"totalDiscountWithTaxAmountFormatted")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"totalDiscountWithTaxAmountValue")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "totalFormatted")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalWithTaxAmount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalWithTaxAmountFormatted")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalWithTaxAmountValue")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "transactionId")) {
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
			Order order, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "account")) {
				if (jsonParserFieldValue != null) {
					order.setAccount(
						AccountSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "accountExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					order.setAccountExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "accountId")) {
				if (jsonParserFieldValue != null) {
					order.setAccountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					order.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "advanceStatus")) {
				if (jsonParserFieldValue != null) {
					order.setAdvanceStatus((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "billingAddress")) {
				if (jsonParserFieldValue != null) {
					order.setBillingAddress(
						BillingAddressSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"billingAddressExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					order.setBillingAddressExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "billingAddressId")) {
				if (jsonParserFieldValue != null) {
					order.setBillingAddressId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "channel")) {
				if (jsonParserFieldValue != null) {
					order.setChannel(
						ChannelSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "channelExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					order.setChannelExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "channelId")) {
				if (jsonParserFieldValue != null) {
					order.setChannelId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "couponCode")) {
				if (jsonParserFieldValue != null) {
					order.setCouponCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					order.setCreateDate(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "creatorEmailAddress")) {

				if (jsonParserFieldValue != null) {
					order.setCreatorEmailAddress((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "currencyCode")) {
				if (jsonParserFieldValue != null) {
					order.setCurrencyCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "currencyExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					order.setCurrencyExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "currencyId")) {
				if (jsonParserFieldValue != null) {
					order.setCurrencyId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					order.setCustomFields((Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "deliveryTermDescription")) {

				if (jsonParserFieldValue != null) {
					order.setDeliveryTermDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"deliveryTermExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					order.setDeliveryTermExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deliveryTermId")) {
				if (jsonParserFieldValue != null) {
					order.setDeliveryTermId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deliveryTermName")) {
				if (jsonParserFieldValue != null) {
					order.setDeliveryTermName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					order.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					order.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "lastPriceUpdateDate")) {

				if (jsonParserFieldValue != null) {
					order.setLastPriceUpdateDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				if (jsonParserFieldValue != null) {
					order.setModifiedDate(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					order.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderDate")) {
				if (jsonParserFieldValue != null) {
					order.setOrderDate(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderItems")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					OrderItem[] orderItemsArray =
						new OrderItem[jsonParserFieldValues.length];

					for (int i = 0; i < orderItemsArray.length; i++) {
						orderItemsArray[i] = OrderItemSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					order.setOrderItems(orderItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderStatus")) {
				if (jsonParserFieldValue != null) {
					order.setOrderStatus(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderStatusInfo")) {
				if (jsonParserFieldValue != null) {
					order.setOrderStatusInfo(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"orderTypeExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					order.setOrderTypeExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderTypeId")) {
				if (jsonParserFieldValue != null) {
					order.setOrderTypeId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paymentMethod")) {
				if (jsonParserFieldValue != null) {
					order.setPaymentMethod((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paymentStatus")) {
				if (jsonParserFieldValue != null) {
					order.setPaymentStatus(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paymentStatusInfo")) {
				if (jsonParserFieldValue != null) {
					order.setPaymentStatusInfo(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "paymentTermDescription")) {

				if (jsonParserFieldValue != null) {
					order.setPaymentTermDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"paymentTermExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					order.setPaymentTermExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paymentTermId")) {
				if (jsonParserFieldValue != null) {
					order.setPaymentTermId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paymentTermName")) {
				if (jsonParserFieldValue != null) {
					order.setPaymentTermName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "printedNote")) {
				if (jsonParserFieldValue != null) {
					order.setPrintedNote((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "purchaseOrderNumber")) {

				if (jsonParserFieldValue != null) {
					order.setPurchaseOrderNumber((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "requestedDeliveryDate")) {

				if (jsonParserFieldValue != null) {
					order.setRequestedDeliveryDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippable")) {
				if (jsonParserFieldValue != null) {
					order.setShippable((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAddress")) {
				if (jsonParserFieldValue != null) {
					order.setShippingAddress(
						ShippingAddressSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingAddressExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					order.setShippingAddressExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAddressId")) {
				if (jsonParserFieldValue != null) {
					order.setShippingAddressId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingAmount")) {
				if (jsonParserFieldValue != null) {
					order.setShippingAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingAmountFormatted")) {

				if (jsonParserFieldValue != null) {
					order.setShippingAmountFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingAmountValue")) {

				if (jsonParserFieldValue != null) {
					order.setShippingAmountValue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingDiscountAmount")) {

				if (jsonParserFieldValue != null) {
					order.setShippingDiscountAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountAmountFormatted")) {

				if (jsonParserFieldValue != null) {
					order.setShippingDiscountAmountFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingDiscountAmountValue")) {

				if (jsonParserFieldValue != null) {
					order.setShippingDiscountAmountValue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountPercentageLevel1")) {

				if (jsonParserFieldValue != null) {
					order.setShippingDiscountPercentageLevel1(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountPercentageLevel1WithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setShippingDiscountPercentageLevel1WithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountPercentageLevel2")) {

				if (jsonParserFieldValue != null) {
					order.setShippingDiscountPercentageLevel2(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountPercentageLevel2WithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setShippingDiscountPercentageLevel2WithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountPercentageLevel3")) {

				if (jsonParserFieldValue != null) {
					order.setShippingDiscountPercentageLevel3(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountPercentageLevel3WithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setShippingDiscountPercentageLevel3WithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountPercentageLevel4")) {

				if (jsonParserFieldValue != null) {
					order.setShippingDiscountPercentageLevel4(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountPercentageLevel4WithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setShippingDiscountPercentageLevel4WithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingDiscountWithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setShippingDiscountWithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingDiscountWithTaxAmountFormatted")) {

				if (jsonParserFieldValue != null) {
					order.setShippingDiscountWithTaxAmountFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingMethod")) {
				if (jsonParserFieldValue != null) {
					order.setShippingMethod((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shippingOption")) {
				if (jsonParserFieldValue != null) {
					order.setShippingOption((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingWithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setShippingWithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingWithTaxAmountFormatted")) {

				if (jsonParserFieldValue != null) {
					order.setShippingWithTaxAmountFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingWithTaxAmountValue")) {

				if (jsonParserFieldValue != null) {
					order.setShippingWithTaxAmountValue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subtotal")) {
				if (jsonParserFieldValue != null) {
					order.setSubtotal(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subtotalAmount")) {
				if (jsonParserFieldValue != null) {
					order.setSubtotalAmount(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "subtotalDiscountAmount")) {

				if (jsonParserFieldValue != null) {
					order.setSubtotalDiscountAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountAmountFormatted")) {

				if (jsonParserFieldValue != null) {
					order.setSubtotalDiscountAmountFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountPercentageLevel1")) {

				if (jsonParserFieldValue != null) {
					order.setSubtotalDiscountPercentageLevel1(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountPercentageLevel1WithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setSubtotalDiscountPercentageLevel1WithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountPercentageLevel2")) {

				if (jsonParserFieldValue != null) {
					order.setSubtotalDiscountPercentageLevel2(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountPercentageLevel2WithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setSubtotalDiscountPercentageLevel2WithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountPercentageLevel3")) {

				if (jsonParserFieldValue != null) {
					order.setSubtotalDiscountPercentageLevel3(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountPercentageLevel3WithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setSubtotalDiscountPercentageLevel3WithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountPercentageLevel4")) {

				if (jsonParserFieldValue != null) {
					order.setSubtotalDiscountPercentageLevel4(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountPercentageLevel4WithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setSubtotalDiscountPercentageLevel4WithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "subtotalDiscountWithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setSubtotalDiscountWithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalDiscountWithTaxAmountFormatted")) {

				if (jsonParserFieldValue != null) {
					order.setSubtotalDiscountWithTaxAmountFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subtotalFormatted")) {
				if (jsonParserFieldValue != null) {
					order.setSubtotalFormatted((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "subtotalWithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setSubtotalWithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"subtotalWithTaxAmountFormatted")) {

				if (jsonParserFieldValue != null) {
					order.setSubtotalWithTaxAmountFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "subtotalWithTaxAmountValue")) {

				if (jsonParserFieldValue != null) {
					order.setSubtotalWithTaxAmountValue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "taxAmount")) {
				if (jsonParserFieldValue != null) {
					order.setTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxAmountFormatted")) {

				if (jsonParserFieldValue != null) {
					order.setTaxAmountFormatted((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "taxAmountValue")) {
				if (jsonParserFieldValue != null) {
					order.setTaxAmountValue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "total")) {
				if (jsonParserFieldValue != null) {
					order.setTotal(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "totalAmount")) {
				if (jsonParserFieldValue != null) {
					order.setTotalAmount(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountAmount")) {

				if (jsonParserFieldValue != null) {
					order.setTotalDiscountAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountAmountFormatted")) {

				if (jsonParserFieldValue != null) {
					order.setTotalDiscountAmountFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountAmountValue")) {

				if (jsonParserFieldValue != null) {
					order.setTotalDiscountAmountValue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountPercentageLevel1")) {

				if (jsonParserFieldValue != null) {
					order.setTotalDiscountPercentageLevel1(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"totalDiscountPercentageLevel1WithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setTotalDiscountPercentageLevel1WithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountPercentageLevel2")) {

				if (jsonParserFieldValue != null) {
					order.setTotalDiscountPercentageLevel2(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"totalDiscountPercentageLevel2WithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setTotalDiscountPercentageLevel2WithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountPercentageLevel3")) {

				if (jsonParserFieldValue != null) {
					order.setTotalDiscountPercentageLevel3(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"totalDiscountPercentageLevel3WithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setTotalDiscountPercentageLevel3WithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountPercentageLevel4")) {

				if (jsonParserFieldValue != null) {
					order.setTotalDiscountPercentageLevel4(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"totalDiscountPercentageLevel4WithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setTotalDiscountPercentageLevel4WithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalDiscountWithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setTotalDiscountWithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"totalDiscountWithTaxAmountFormatted")) {

				if (jsonParserFieldValue != null) {
					order.setTotalDiscountWithTaxAmountFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"totalDiscountWithTaxAmountValue")) {

				if (jsonParserFieldValue != null) {
					order.setTotalDiscountWithTaxAmountValue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "totalFormatted")) {
				if (jsonParserFieldValue != null) {
					order.setTotalFormatted((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalWithTaxAmount")) {

				if (jsonParserFieldValue != null) {
					order.setTotalWithTaxAmount(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalWithTaxAmountFormatted")) {

				if (jsonParserFieldValue != null) {
					order.setTotalWithTaxAmountFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalWithTaxAmountValue")) {

				if (jsonParserFieldValue != null) {
					order.setTotalWithTaxAmountValue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "transactionId")) {
				if (jsonParserFieldValue != null) {
					order.setTransactionId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowStatusInfo")) {

				if (jsonParserFieldValue != null) {
					order.setWorkflowStatusInfo(
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