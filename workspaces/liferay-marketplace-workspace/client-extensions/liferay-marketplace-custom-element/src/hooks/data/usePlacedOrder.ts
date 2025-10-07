/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import useSWR from 'swr';

import {Liferay} from '../../liferay/liferay';
import HeadlessCommerceDeliveryOrder from '../../services/rest/HeadlessCommerceDeliveryOrder';

const channelId = Liferay.CommerceContext.commerceChannelId;

type Props = {
	accountId: number | string;
	filter?: string;
	orderTypeExternalReferenceCodes?: string[];
	page: number;
	pageSize: number;
	shouldFetch?: boolean;
};

const usePlacedOrder = (orderId: number | string) =>
	useSWR(`/placed-order/${orderId}`, () =>
		HeadlessCommerceDeliveryOrder.getPlacedOrder(orderId)
	);

const usePlacedOrders = ({
	accountId,
	filter,
	orderTypeExternalReferenceCodes,
	page,
	pageSize,
	shouldFetch = true,
}: Props) =>
	useSWR(
		shouldFetch ? `/placed-orders/${accountId}/${page}/${pageSize}` : null,
		async () => {
			const response =
				await HeadlessCommerceDeliveryOrder.getPlacedOrders(
					channelId,
					accountId,
					new URLSearchParams({
						...(filter && {filter}),
						nestedFields: 'placedOrderItems',
						page: page.toString(),
						pageSize: pageSize.toString(),
						sort: 'createDate:desc',
					})
				);

			return {
				...response,
				items: response.items.filter(
					({orderTypeExternalReferenceCode}) =>
						orderTypeExternalReferenceCodes?.length
							? orderTypeExternalReferenceCodes.includes(
									orderTypeExternalReferenceCode
								)
							: true
				),
			};
		}
	);

export {usePlacedOrder, usePlacedOrders};
