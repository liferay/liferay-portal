/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers, DataApiHelpers} from './ApiHelpers';

type TCommerceReturn = {
	channelId?: number;
	commerceReturnToCommerceReturnItems?: TCommerceReturnItem[];
	id?: number;
	r_accountToCommerceReturns_accountEntryId?: number;
	r_commerceOrderToCommerceReturns_commerceOrderId?: number;
	returnStatus?: TCommerceReturnStatus;
};

type TCommerceReturnItem = {
	amount?: number;
	authorized: number;
	quantity?: number;
	r_accountToCommerceReturnItems_accountEntryId?: number;
	r_commerceOrderItemToCommerceReturnItems_commerceOrderItemId?: number;
	r_commerceOrderToCommerceReturns_commerceOrderId?: number;
	received?: number;
	returnItemStatus?: TCommerceReturnItemStatus;
	returnReason?: TCommerceReturnReason;
	returnResolutionMethod?: TCommerceReturnResolutionMethod;
};

type TCommerceReturnReason = {
	key: string;
};

type TCommerceReturnStatus = {
	key: string;
};

type TCommerceReturnItemStatus = {
	key: string;
};

type TCommerceReturnResolutionMethod = {
	key: string;
};

export class HeadlessCommerceReturnApiHelper {
	readonly apiHelpers: ApiHelpers | DataApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers | DataApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'commerce/returns/';
	}

	async deleteCommerceReturn(returnId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}${returnId}`
		);
	}

	async postCommerceReturn(
		commerceReturn: TCommerceReturn
	): Promise<TCommerceReturn> {
		commerceReturn = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}`,
			{
				data: commerceReturn,
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: commerceReturn.id,
				type: 'commerceReturn',
			});
		}

		return commerceReturn;
	}
}
