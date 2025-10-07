/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetcher from '../fetcher';

export default class HeadlessCommerceAdminOrder {
	static deleteOrder(orderId: number | string) {
		return fetcher.delete(
			`o/headless-commerce-admin-order/v1.0/orders/${orderId}`
		);
	}

	static getOrder(
		orderId: number | string,
		searchParams = new URLSearchParams()
	) {
		return fetcher<Order>(
			`o/headless-commerce-admin-order/v1.0/orders/${orderId}?${searchParams.toString()}`
		);
	}

	static getOrders(searchParams = new URLSearchParams()) {
		return fetcher<APIResponse>(
			`o/headless-commerce-admin-order/v1.0/orders?${searchParams.toString()}`
		);
	}

	static patchOrder(orderId: number | string, order: Partial<Order>) {
		return fetcher.patch<Order>(
			`o/headless-commerce-admin-order/v1.0/orders/${orderId}`,
			order
		);
	}
}
