/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import z from 'zod';

import CreateFilters from '../../core/CreateFilters';
import {
	FilterSchemaOption,
	filterSchema as filterSchemas,
} from '../../schema/filters';
import zodSchema from '../../schema/zod';
import {downloadFile} from '../../utils/file';
import {MarketplaceSpringBootOAuth2} from './OAuth2Client';

class MarketplaceOAuth2 extends MarketplaceSpringBootOAuth2 {
	async createAccount(
		account: z.infer<typeof zodSchema.accountForm>
	): Promise<Account> {
		const formData = new FormData();

		if (account.accountImage) {
			const blob = new Blob([account.accountImage]);
			formData.append('file', blob, account.accountImage.name);
		}

		const data = {
			customFields: [
				{
					customValue: {
						data: account.emailAddress,
					},
					name: 'Contact Email',
				},
			],
			name: account.accountName,
			postalAddresses: [
				{
					addressCountry: account.billingAddress.country,
					addressLocality: account.billingAddress.city,
					addressRegion: account.billingAddress.regionISOCode ?? '',
					name: account.billingAddress.name,
					phoneNumber: account.billingAddress.phoneNumber,
					postalCode: account.billingAddress.zip,
					primary: true,
					streetAddressLine1: account.billingAddress.street1,
					streetAddressLine2: account.billingAddress.street2 ?? '',
				},
			],
			taxId: account.taxNumber,
			type: account.accountType,
		};

		formData.append('account', JSON.stringify(data));

		const newAccount = await this.post<Account>('/account', formData, {
			earlyReturn: true,
		});

		return newAccount;
	}

	async downloadOrderReport(
		filter: {
			[key: string]: string;
		},
		filterSchema?: FilterSchemaOption
	) {
		const searchBuilder = CreateFilters.createFilter({
			appliedFilter: filter,
			filterSchema: (filterSchemas as any)[filterSchema ?? ''],
		});

		const response = await this.get<Response>(
			`/orders/export?filters=${searchBuilder}`,
			{earlyReturn: true}
		);

		await downloadFile('orders.csv', response);
	}

	async taxCalculate(orderId: number): Promise<Order> {
		const order = await this.post<Order>(
			`/tax-calculate/${orderId}`,
			{},
			{earlyReturn: true}
		);

		return order;
	}
}

const marketplaceOAuth2 = new MarketplaceOAuth2('/marketplace');

export default marketplaceOAuth2;
