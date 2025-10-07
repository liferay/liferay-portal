/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getRandomInt} from '../utils/getRandomInt';
import {ApiHelpers} from './ApiHelpers';

export class ListTypeAdminApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'headless-admin-list-type/v1.0';
	}

	async deleteListTypeDefinition(listTypeDefinitionId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/list-type-definitions/${listTypeDefinitionId}`
		);
	}

	async getFilteredListTypeDefinition(
		filterParamKey: string,
		filterParamValue: string
	): Promise<ListTypeDefinition[]> {
		const response: ListTypeDefinitions = await this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/list-type-definitions?filter=${filterParamKey} eq '${filterParamValue}'`
		);

		return response.items;
	}

	async getListTypeDefinitions(): Promise<ListTypeDefinitions> {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/list-type-definitions`
		);
	}

	async postListTypeEntry({
		key,
		listTypeDefinitionExternalReferenceCode,
		name_i18n,
	}: {
		key: string;
		listTypeDefinitionExternalReferenceCode: string;
		name_i18n: LocalizedValue<string>;
	}): Promise<ListTypeDefinition> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/list-type-definitions/by-external-reference-code/${listTypeDefinitionExternalReferenceCode}/list-type-entries`,
			{
				data: {
					key: key.toLowerCase(),
					name_i18n,
				},
			}
		);
	}

	async postRandomListTypeDefinition(): Promise<ListTypeDefinition> {
		const listTypeDefinitionExternalReferenceCode =
			'ListTypeDefinition' + getRandomInt();

		const requestBody = {
			externalReferenceCode: listTypeDefinitionExternalReferenceCode,
			name: listTypeDefinitionExternalReferenceCode,
			name_i18n: {
				en_US: listTypeDefinitionExternalReferenceCode,
			},
		};

		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/list-type-definitions`,
			{data: requestBody}
		);
	}
}
