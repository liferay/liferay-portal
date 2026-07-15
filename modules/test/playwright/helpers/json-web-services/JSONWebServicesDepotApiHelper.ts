/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {liferayConfig} from '../../liferay.config';
import getRandomString from '../../utils/getRandomString';
import {ApiHelpers} from '../ApiHelpers';

type DepotEntry = {
	depotEntryId: string;
	groupId: number;
};

enum DepotType {
	ANY = -1,
	ASSET_LIBRARY = 0,
	DESIGN_LIBRARY = 3,
	PROJECT = 2,
	SPACE = 1,
}

export class JSONWebServicesDepotApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;
	readonly depotType: typeof DepotType;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/api/jsonws/depot.depotentry';
		this.depotType = DepotType;
	}

	async addDepotEntry(
		depotName: string,
		options: {type: DepotType} = {type: 0}
	): Promise<DepotEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('nameMap', JSON.stringify({en_US: depotName}));
		urlSearchParams.append(
			'descriptionMap',
			JSON.stringify({en_US: getRandomString()})
		);
		urlSearchParams.append('type', String(options.type));

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
