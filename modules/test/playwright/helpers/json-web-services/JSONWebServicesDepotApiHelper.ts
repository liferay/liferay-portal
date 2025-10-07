/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {liferayConfig} from '../../liferay.config';
import getRandomString from '../../utils/getRandomString';
import {ApiHelpers} from '../ApiHelpers';

type DepotEntry = {
	depotEntryId: string;
};

export class JSONWebServicesDepotApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/api/jsonws/depot.depotentry';
	}

	async addDepotEntry(depotName: string): Promise<DepotEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('nameMap', JSON.stringify({en_US: depotName}));
		urlSearchParams.append(
			'descriptionMap',
			JSON.stringify({en_US: getRandomString()})
		);
		urlSearchParams.append('type', '0');

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/add-depot-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async deleteDepotEntry(depotEntryId: string): Promise<DepotEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('depotEntryId', depotEntryId);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/delete-depot-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
