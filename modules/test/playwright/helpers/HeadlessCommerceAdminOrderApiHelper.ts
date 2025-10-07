/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getRandomInt} from '../utils/getRandomInt';
import getRandomString from '../utils/getRandomString';
import {ApiHelpers, DataApiHelpers} from './ApiHelpers';

type TOrder = {
	accountId?: number;
	billingAddressId?: string;
	channelId?: number;
	createDate?: string;
	currencyCode?: string;
	id?: number;
	name?: string;
	orderItems?: TOrderItem[];
	orderStatus?: string;
	orderStatusInfo?: number;
	orderTypeExternalReferenceCode?: string;
	paymentMethod?: string;
	paymentStatus?: string;
	paymentStatusInfo?: number;
	shippingAddressId?: string;
	shippingAmount?: number;
	shippingMethod?: string;
	shippingOption?: string;
	total?: number;
};

type TOrderItem = {
	decimalQuantity?: number;
	id?: number;
	productId?: number;
	quantity: number;
	skuId?: string;
	unitOfMeasureKey?: number;
	unitPrice?: number;
};

type TOrderNote = {
	author?: string;
	content?: string;
	externalReferenceCode?: string;
	id?: number;
	orderExternalReferenceCode?: string;
	orderId?: number;
	restricted?: boolean;
};

type TOrderRule = {
	active?: boolean;
	id?: number;
	name?: string;
	priority?: number;
	type: string;
	typeSettings?: string;
};

type TOrderType = {
	active?: boolean;
	id?: number;
	name?: {
		[key: string]: string;
	};
};

type TTerm = {
	active?: boolean;
	description?: {
		[key: string]: string;
	};
	id?: number;
	label?: {
		[key: string]: string;
	};
	name?: string;
	priority?: number;
	type: string;
};

export class HeadlessCommerceAdminOrderApiHelper {
	readonly apiHelpers: ApiHelpers | DataApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers | DataApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'headless-commerce-admin-order/v1.0/';
	}

	async deleteOrder(orderId: number) {
		const shipments =
			await this.apiHelpers.headlessCommerceAdminShipment.getShipments();

		for (const shipment of shipments.items) {
			await this.apiHelpers.headlessCommerceAdminShipment.deleteShipment(
				shipment.id
			);
		}

		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/orders/${orderId}`
		);
	}

	async deleteOrderRules(orderRuleId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/order-rules/${orderRuleId}`
		);
	}

	async deleteOrderTypes(orderTypeId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/order-types/${orderTypeId}`
		);
	}

	async deleteTerms(termsId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/terms/${termsId}`
		);
	}

	async getOrder(orderId: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/orders/${orderId}`
		);
	}

	async getOrdersPage() {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/orders`
		);
	}

	async patchOrder(id: number, order: TOrder) {
		await this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}orders/${id}?nestedFields=orderItems`,
			order
		);

		const patchOrder = await this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}orders/${id}`
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			if (
				!this.apiHelpers.data.find(
					(element) => element.id === patchOrder.id
				)
			) {
				this.apiHelpers.data.push({
					id: patchOrder.id,
					type: 'order',
				});
			}
		}

		return patchOrder;
	}

	async postOrder(order: TOrder): Promise<TOrder> {
		const postOrder = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/orders?nestedFields=orderItems`,
			{
				data: {currencyCode: 'USD', ...order},
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: postOrder.id,
				type: 'order',
			});
		}

		return postOrder;
	}

	async postOrderIdOrderNote(
		orderId: number,
		orderNote: TOrderNote
	): Promise<TOrderNote> {
		orderNote = {
			author: getRandomString(),
			content: getRandomString(),
			externalReferenceCode: getRandomString(),
			...(orderNote || {}),
		};

		const postOrder = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/orders/${orderId}/orderNotes`,
			{
				data: orderNote,
				failOnStatusCode: true,
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: postOrder.id,
				type: 'order',
			});
		}

		return postOrder;
	}

	async postOrderRule(orderRule: TOrderRule) {
		orderRule = {
			active: true,
			name: getRandomString(),
			priority: getRandomInt(),
			type: '',
			typeSettings: '',
			...(orderRule || {}),
		};

		orderRule = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/order-rules`,
			{
				data: orderRule,
				failOnStatusCode: true,
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({id: orderRule.id, type: 'orderRule'});
		}

		return orderRule;
	}

	async postOrderType(orderType: TOrderType) {
		orderType = {
			active: orderType.active,
			name: {
				en_US: getRandomString(),
			},
			...orderType,
		};

		orderType = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/order-types`,
			{
				data: orderType,
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({id: orderType.id, type: 'orderType'});
		}

		return orderType;
	}

	async postTerm(terms: TTerm) {
		terms = {
			active: true,
			description: {
				en_US: getRandomString(),
			},
			label: {
				en_US: getRandomString(),
			},
			name: getRandomString(),
			priority: getRandomInt(),
			type: '',
			...(terms || {}),
		};

		terms = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/terms`,
			{
				data: terms,
				failOnStatusCode: true,
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({id: terms.id, type: 'terms'});
		}

		return terms;
	}
}
