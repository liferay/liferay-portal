/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers} from './ApiHelpers';

export class DataEngineApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'data-engine/v2.0';
	}

	/**
	 * It allows creating a structure inside a site.
	 *
	 * @param siteId the id of the site in which the structure will be created
	 * @param dataDefinition the definition of the structure to specify
	 * the fields for it.
	 */

	async createStructure(
		siteId: string,
		dataDefinition: DataDefinition
	): Promise<DataDefinition> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/data-definitions/by-content-type/journal`,
			{data: dataDefinition}
		);
	}

	async createDocumentMetadataSet(
		siteId: number | string,
		dataDefinition: DataDefinition
	): Promise<DataDefinition> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/data-definitions/by-content-type/document-library`,
			{data: dataDefinition}
		);
	}

	async getStructure(id: string): Promise<DataDefinition> {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/data-definitions/${id}`
		);
	}

	/**
	 * It allows updating an existing structure, for example to add a field to
	 * it.
	 *
	 * @param id the id of the structure to update
	 * @param dataDefinition the full updated definition of the structure
	 */

	async updateStructure(
		id: string,
		dataDefinition: DataDefinition
	): Promise<DataDefinition> {
		return this.apiHelpers.put(
			`${this.apiHelpers.baseUrl}${this.basePath}/data-definitions/${id}`,
			{data: dataDefinition}
		);
	}
}
