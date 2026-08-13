/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers} from './ApiHelpers';

interface DataSourceLiferayAnalyticsURL {
	liferayAnalyticsURL: string;
}

interface Channel {
	channelId: string;
	dataSources?: {
		dataSourceId?: string;
		siteIds?: number[];
	}[];
	name: string;
}

export class AnalyticsSettingsRestApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'analytics-settings-rest/v1.0';
	}

	async deleteDataSource() {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/data-sources`
		);
	}

	async postDataSource(
		token: string
	): Promise<DataSourceLiferayAnalyticsURL> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/data-sources`,
			{
				data: {token},
			}
		);
	}

	async putContactsConfiguration(configuration: {
		syncAllAccounts?: boolean;
		syncAllContacts?: boolean;
		syncedAccountGroupIds?: number[];
		syncedOrganizationIds?: number[];
		syncedUserGroupIds?: number[];
	}) {
		return this.apiHelpers.page.request.put(
			`${this.apiHelpers.baseUrl}${this.basePath}/contacts/configuration`,
			{
				data: configuration,
				headers: {
					'Content-Type': 'application/json',
					...(await this.apiHelpers.getCSRFTokenHeader()),
				},
			}
		);
	}

	async syncSitesToChannel(
		channelId: string,
		siteIds: number[]
	): Promise<Channel> {
		return this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}/channels`,
			{
				channelId,
				dataSources: [{siteIds}],
			}
		);
	}
}
