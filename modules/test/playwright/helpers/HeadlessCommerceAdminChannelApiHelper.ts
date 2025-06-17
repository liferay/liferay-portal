/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getRandomInt} from '../utils/getRandomInt';
import {ApiHelpers, DataApiHelpers} from './ApiHelpers';

type TChannel = {
	accountId?: number;
	currencyCode?: string;
	id?: number;
	name?: string;
	siteGroupId?: number | string;
	type?: string;
};

export class HeadlessCommerceAdminChannelApiHelper {
	readonly apiHelpers: ApiHelpers | DataApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers | DataApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'headless-commerce-admin-channel/v1.0/';
	}

	async deleteChannel(channelId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/channels/${channelId}`
		);
	}

	async getChannel(channelId: number): Promise<TChannel> {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/channels/${channelId}`
		);
	}

	async getChannelsPage(search: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/channels?search=${search}`
		);
	}

	async getTaxCategories() {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/tax-categories`
		);
	}

	async patchChannelWithAccountId(accountId: number, channel: TChannel) {
		await this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}/channels/${channel.id}`,
			{
				accountId,
			}
		);
	}

	async postChannel(channel: TChannel): Promise<TChannel> {
		channel = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/channels`,
			{
				data: {
					accountId: 0,
					currencyCode: 'USD',
					name: 'Channel' + getRandomInt(),
					siteGroupId: 0,
					type: 'site',
					...channel,
				},
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({id: channel.id, type: 'channel'});
		}

		return channel;
	}

	async putChannel(channelId: number, channel: TChannel) {
		await this.apiHelpers.put(
			`${this.apiHelpers.baseUrl}${this.basePath}/channels/${channelId}`,
			{
				data: {
					accountId: 0,
					currencyCode: 'USD',
					name: 'Channel' + getRandomInt(),
					siteGroupId: 0,
					type: 'site',
					...channel,
				},
			}
		);
	}
}
