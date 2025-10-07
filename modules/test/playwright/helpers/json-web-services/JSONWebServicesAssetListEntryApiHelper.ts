/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {liferayConfig} from '../../liferay.config';
import {ApiHelpers} from '../ApiHelpers';

export class JSONWebServicesAssetListEntryApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/api/jsonws/assetlist.assetlistentry';
	}

	async addDynamicAssetListEntry({
		groupId,
		title,
		typeSettings = '',
	}: {
		groupId: string;
		title: string;
		typeSettings?: string;
	}): Promise<AssetListEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('externalReferenceCode', '');
		urlSearchParams.append('groupId', groupId);
		urlSearchParams.append('title', title);
		urlSearchParams.append('typeSettings', typeSettings);
		urlSearchParams.append('serviceContext', JSON.stringify({}));

		return await this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/add-dynamic-asset-list-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async addManualAssetListEntry({
		groupId,
		title,
		type = '1',
	}: {
		groupId: string;
		title: string;
		type?: string;
	}): Promise<AssetListEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('externalReferenceCode', '');
		urlSearchParams.append('groupId', groupId);
		urlSearchParams.append('title', title);
		urlSearchParams.append('type', type);

		return await this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/add-asset-list-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async updateAssetListEntry({
		assetListEntryId,
		groupId,
		segmentsEntryId = '0',
		typeSettings = '',
	}: {
		assetListEntryId: string;
		groupId: string;
		segmentsEntryId?: string;
		typeSettings?: string;
	}): Promise<AssetListEntry> {
		const user =
			await this.apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'test@liferay.com'
			);

		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('assetListEntryId', assetListEntryId);
		urlSearchParams.append('segmentsEntryId', segmentsEntryId);
		urlSearchParams.append('typeSettings', typeSettings);
		urlSearchParams.append(
			'serviceContext',
			JSON.stringify({scopeGroupId: groupId, userId: user.userId})
		);

		return await this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/update-asset-list-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
