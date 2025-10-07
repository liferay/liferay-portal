/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import useSWR, {SWRConfiguration} from 'swr';

import SearchBuilder from '../core/SearchBuilder';
import HeadlessCommerceAdminCatalog from '../services/rest/HeadlessCommerceAdminCatalog';
import HeadlessCommerceAdminOrder from '../services/rest/HeadlessCommerceAdminOrder';
import HeadlessCommerceAdminPayment from '../services/rest/HeadlessCommerceAdminPayment';

const useAdminOrderProduct = (
	orderId: string,
	swrOptions?: SWRConfiguration
) => {
	return useSWR(
		`/admin-order/${orderId}`,
		async () => {
			const [order, payments] = await Promise.all([
				HeadlessCommerceAdminOrder.getOrder(
					orderId,
					new URLSearchParams({
						nestedFields: 'account,billingAddress,orderItems',
					})
				),
				HeadlessCommerceAdminPayment.getPayment(
					new URLSearchParams({
						filter: new SearchBuilder()
							.eq('relatedItemId', orderId, {unquote: true})
							.build(),
					})
				),
			]);

			const sku = await HeadlessCommerceAdminCatalog.getSku(
				order.orderItems[0].skuId
			);

			const product = await HeadlessCommerceAdminCatalog.getProduct(
				sku.productId,
				new URLSearchParams({nestedFields: 'productSpecifications'})
			);

			return {
				order,
				payments,
				product,
			};
		},
		swrOptions
	);
};

export default useAdminOrderProduct;
