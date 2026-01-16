/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import useSWR, {SWRConfiguration} from 'swr';

import HeadlessAdminUser from '../services/rest/HeadlessAdminUser';
import HeadlessCommerceAdminOrder from '../services/rest/HeadlessCommerceAdminOrder';
import HeadlessCommerceDeliveryOrder from '../services/rest/HeadlessCommerceDeliveryOrder';
import PublisherSalesSummary from '../services/rest/PublisherSalesSummary';

type ExtendedOrderItems = OrderItem & {
	account: {
		id: number;
		name: string;
		taxId: string;
		type: string;
	};
	currencyCode: string;
	finalPrice?: number;
	finalPriceWithTaxAmount?: number;
};

type ExtendedPlacedOrderItems = PlacedOrderItems & {
	author: string;
};

const usePublisherSalesSummaryObject = (
	entryId: string,
	swrOptions?: SWRConfiguration
) => {
	return useSWR(
		`/publisher-sales-summary/${entryId}`,
		async () => {
			const publisherSalesSummary =
				await PublisherSalesSummary.getPublisherSalesSummaryById(
					entryId,
					new URLSearchParams({
						nestedFields: 'publisherToCommerceOrder',
					})
				);

			const [publisherAccount, postalAddresses, placedOrders, orders] =
				await Promise.all([
					HeadlessAdminUser.getAccount(
						publisherSalesSummary.r_accountToPublisher_accountEntryId
					),
					HeadlessAdminUser.getAccountPostalAddresses(
						publisherSalesSummary.r_accountToPublisher_accountEntryId
					),
					Promise.all(
						publisherSalesSummary.publisherToCommerceOrder.map(
							(order) =>
								HeadlessCommerceDeliveryOrder.getPlacedOrder(
									order.id
								)
						)
					),
					Promise.all(
						publisherSalesSummary.publisherToCommerceOrder.map(
							(order) =>
								HeadlessCommerceAdminOrder.getOrder(
									order.id,
									new URLSearchParams({
										nestedFields: 'account,orderItems',
									})
								)
						)
					),
				]);

			const orderItems: ExtendedOrderItems[] = [];
			const placedOrderItems: ExtendedPlacedOrderItems[] = [];

			orders.forEach((order) =>
				order.orderItems.forEach((orderItem) =>
					orderItems.push({
						...orderItem,
						account: order.account,
						currencyCode: order.currencyCode,
					})
				)
			);

			placedOrders.forEach((placedOrder) =>
				placedOrder.placedOrderItems.forEach((placedOrderItem) =>
					placedOrderItems.push({
						...placedOrderItem,
						author: placedOrder.author,
					})
				)
			);

			const completeOrderItems = orderItems.map((orderItem, index) => ({
				orderItem,
				placedOrderItem: placedOrderItems[index],
			}));

			return {
				account: publisherAccount,
				completeOrderItems,
				postalAddresses,
				publisherSalesSummary,
			};
		},
		swrOptions
	);
};

export default usePublisherSalesSummaryObject;
