/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.constants;

/**
 * @author Riccardo Ferrari
 */
public class FieldOrderConstants {

	public static final String[] FIELD_ORDER_ITEM_NAMES = {
		"cpDefinitionId", "createDate", "customFields", "externalReferenceCode",
		"finalPrice", "id", "modifiedDate", "name", "options", "orderId",
		"parentOrderItemId", "quantity", "sku", "subscription", "unitOfMeasure",
		"unitPrice", "userId"
	};

	public static final String[] FIELD_ORDER_NAMES = {
		"accountExternalReferenceCode", "accountId", "channelId", "createDate",
		"currencyCode", "customFields", "externalReferenceCode", "id",
		"modifiedDate", "orderDate", "orderItems", "orderStatus",
		"orderTypeExternalReferenceCode", "orderTypeId", "paymentMethod",
		"paymentStatus", "status", "total", "userId"
	};

}