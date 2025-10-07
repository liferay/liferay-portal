/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers, DataApiHelpers} from './ApiHelpers';

export class HeadlessAssetLibraryApiHelper {
	apiHelpers: ApiHelpers;
	basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'headless-asset-library/v1.0';
	}

	async createAssetLibrary({
		description,
		name,
		settings = {},
		type = 'Space',
	}: {
		description?: string;
		name: string;
		settings: any;
		type: string;
	}) {
		const data = JSON.stringify({
			description,
			name,
			settings,
			type,
		});

		const assetLibrary = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/asset-libraries`,
			{data}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: assetLibrary.id,
				type: 'assetLibrary',
			});
		}

		return assetLibrary;
	}

	async getAssetLibrariesPage(filter?: string) {
		const response = await this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/asset-libraries${filter ? `?filter=${encodeURIComponent(filter)}` : ''}`
		);

		return response?.items;
	}

	async deleteAssetLibrary(assetLibraryId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/asset-libraries/${assetLibraryId}`
		);
	}
}
