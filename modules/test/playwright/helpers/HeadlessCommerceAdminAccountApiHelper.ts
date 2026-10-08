/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getRandomString from '../utils/getRandomString';
import {ApiHelpers, DataApiHelpers} from './ApiHelpers';

type TAccountChannelEntry = {
	channelId?: number;
	classPK?: number;
	id?: number;
};

type TAddress = {
	city?: string;
	countryISOCode?: string;
	defaultBilling?: boolean;
	defaultShipping?: boolean;
	description?: string;
	id?: number;
	latitude?: number;
	longitude?: number;
	name?: string;
	phoneNumber?: string;
	regionISOCode?: string;
	street1?: string;
	street2?: string;
	street3?: string;
	type?: number;
	zip?: string;
};

export class HeadlessCommerceAdminAccountApiHelper {
	readonly apiHelpers: ApiHelpers | DataApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers | DataApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'headless-commerce-admin-account/v1.0';
	}

	async postAccountChannelBillingAddress(
		accountId: number,
		accountChannelEntry: TAccountChannelEntry
	): Promise<TAccountChannelEntry> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/accounts/${accountId}/account-channel-billing-addresses`,
			{
				data: accountChannelEntry,
			}
		);
	}

	async postAccountChannelShippingAddress(
		accountId: number,
		accountChannelEntry: TAccountChannelEntry
	): Promise<TAccountChannelEntry> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/accounts/${accountId}/account-channel-shipping-addresses`,
			{
				data: accountChannelEntry,
			}
		);
	}

	async postAddress(accountEntryId: number, address?: TAddress) {
		address = {
			city: getRandomString(),
			countryISOCode: 'US',
			defaultBilling: true,
			defaultShipping: true,
			description: getRandomString(),
			latitude: 0,
			longitude: 0,
			name: getRandomString(),
			regionISOCode: '',
			street1: getRandomString(),
			street2: getRandomString(),
			street3: getRandomString(),
			type: 2,
			zip: getRandomString(),
			...(address || {}),
		};

		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/accounts/${accountEntryId}/accountAddresses`,
			{
				data: address,
				failOnStatusCode: true,
			}
		);
	}
}
