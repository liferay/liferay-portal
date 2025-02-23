/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {liferayConfig} from '../../liferay.config';
import {ApiHelpers} from '../ApiHelpers';

export class JSONWebServicesAssetDisplayPageEntryApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/api/jsonws/asset.assetdisplaypageentry';
	}

	async addAssetDisplayPageEntry({
		classNameId,
		classPK,
		groupId,
		layoutPageTemplateEntryId,
	}: {
		classNameId: string;
		classPK: string;
		groupId: string;
		layoutPageTemplateEntryId: string;
	}): Promise<AssetDisplayPageEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('classNameId', classNameId);
		urlSearchParams.append('classPK', classPK);
		urlSearchParams.append('groupId', groupId);
		urlSearchParams.append(
			'layoutPageTemplateEntryId',
			layoutPageTemplateEntryId
		);
		urlSearchParams.append('serviceContext', JSON.stringify({}));

		return await this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/add-asset-display-page-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
