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
		externalReferenceCode,
		name,
		settings = {},
		type = 'Space',
	}: {
		description?: string;
		externalReferenceCode?: string;
		name: string;
		settings?: any;
		type?: string;
	}) {
		const data = JSON.stringify({
			description,
			externalReferenceCode,
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
				id: assetLibrary.externalReferenceCode,
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

	async deleteAssetLibrary(externalReferenceCode: string) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/asset-libraries/${externalReferenceCode}`
		);
	}

	async getAssetLibrary(externalReferenceCode: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/asset-libraries/${externalReferenceCode}`
		);
	}

	async patchAssetLibrary(
		externalReferenceCode: string,
		body: Record<string, any>
	) {
		return this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}/asset-libraries/${externalReferenceCode}`,
			body
		);
	}

	async patchAssetLibraryWithProblem(
		externalReferenceCode: string,
		body: Record<string, any>
	) {
		return this.apiHelpers.patchRequestOptions(
			`${this.apiHelpers.baseUrl}${this.basePath}/asset-libraries/${externalReferenceCode}`,
			{data: body, failOnStatusCode: false}
		);
	}

	async connectSite(
		assetLibraryExternalReferenceCode: string,
		connectedSiteExternalReferenceCode: string,
		body: Record<string, any> = {searchable: true}
	) {
		return this.apiHelpers.put(
			`${this.apiHelpers.baseUrl}${this.basePath}/asset-libraries/${assetLibraryExternalReferenceCode}/connected-sites/${connectedSiteExternalReferenceCode}`,
			{data: body}
		);
	}

	async disconnectSite(
		assetLibraryExternalReferenceCode: string,
		connectedSiteExternalReferenceCode: string
	) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/asset-libraries/${assetLibraryExternalReferenceCode}/connected-sites/${connectedSiteExternalReferenceCode}`
		);
	}

	async putAssetLibraryUserAccount(
		assetLibraryExternalReferenceCode: string,
		userAccountExternalReferenceCode: string
	) {
		return this.apiHelpers.put(
			`${this.apiHelpers.baseUrl}${this.basePath}/asset-libraries/${assetLibraryExternalReferenceCode}/user-accounts/${userAccountExternalReferenceCode}`
		);
	}

	async putAssetLibraryUserAccountRoles(
		assetLibraryExternalReferenceCode: string,
		userAccountExternalReferenceCode: string,
		roleNames: string[]
	) {
		return this.apiHelpers.put(
			`${this.apiHelpers.baseUrl}${this.basePath}/asset-libraries/${assetLibraryExternalReferenceCode}/user-accounts/${userAccountExternalReferenceCode}/roles`,
			{data: roleNames.map((name) => ({name}))}
		);
	}
}
