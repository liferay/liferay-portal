/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers} from './ApiHelpers';

export class ApiBuilderHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'headless-builder';
	}

	async getEndpointPage(endpointURI: string) {
		return this.apiHelpers.getResponse(
			`${this.apiHelpers.baseUrl}${endpointURI}`
		);
	}

	async postApiResource(data: DataObject, url: String) {
		return this.apiHelpers.postResponse(
			`${this.apiHelpers.baseUrl}${this.basePath}/${url}`,
			{data}
		);
	}

	async postApiApplication(data: DataObject) {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/applications`,
			{data}
		);
	}

	async putResponse(uri: any) {
		return this.apiHelpers.putResponse(
			`${this.apiHelpers.baseUrl}${this.basePath}/${uri}`
		);
	}

	async postAPIFilter(data: DataObject) {
		return this.apiHelpers.postResponse(
			`${this.apiHelpers.baseUrl}${this.basePath}/filters`,
			{data}
		);
	}

	async deleteApiResource(id: number, url: String) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/${url}/${id}`
		);
	}

	async deleteApiApplication(apiApplicationId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/applications/${apiApplicationId}`
		);
	}
}
