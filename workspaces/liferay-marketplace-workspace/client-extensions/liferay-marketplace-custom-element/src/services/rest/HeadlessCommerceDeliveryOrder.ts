/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetcher from '../fetcher';

export default class HeadlessCommerceDeliveryOrder {
	static getPlacedOrders(
		channelId: number | string,
		accountId: number | string,
		params = new URLSearchParams()
	) {
		return fetcher<APIResponse<PlacedOrder>>(
			`/o/headless-commerce-delivery-order/v1.0/channels/${channelId}/accounts/${accountId}/placed-orders?${params}`
		);
	}

	static async getPlacedOrder(orderId: number | string) {
		return fetcher<PlacedOrder>(
			`o/headless-commerce-delivery-order/v1.0/placed-orders/${orderId}?nestedFields=placedOrderItems`
		);
	}

	static async getPlacedOrderBillingAddress(orderId: string) {
		return fetcher(
			`o/headless-commerce-delivery-order/v1.0/placed-orders/${orderId}/placed-order-billing-address`
		);
	}
}
