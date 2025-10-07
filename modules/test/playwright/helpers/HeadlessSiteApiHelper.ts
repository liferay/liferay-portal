/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createReadStream} from 'fs';

import {zipFolder} from '../utils/zip';
import {ApiHelpers} from './ApiHelpers';

type TSite = {
	externalReferenceCode?: string;
	id?: number;
	name: string;
	parentSiteKey?: string;
	templateKey?: string;
	templateType?: string;
};

export class HeadlessSiteApiHelper {
	apiHelpers: ApiHelpers;
	basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'headless-site/v1.0';
	}

	async createSite(site: TSite): Promise<Site> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites`,
			{data: site}
		);
	}

	async createSiteFromZip(
		site: TSite,
		siteInitializerPath: string
	): Promise<Site> {
		const zip = await zipFolder(siteInitializerPath, {
			destPath: 'site-initializer/',
		});

		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/site-initializer`,
			{
				headers: {
					...(await this.apiHelpers.getCSRFTokenHeader()),
				},
				multipart: {
					file: createReadStream(zip),
					site: JSON.stringify(site),
				},
			}
		);
	}

	async getSiteByERC(externalReferenceCode: string): Promise<Site> {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/by-external-reference-code/${externalReferenceCode}`
		);
	}

	async deleteSite(siteId: string) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}`
		);
	}

	async deleteSiteByERC(externalReferenceCode: string) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/by-external-reference-code/${externalReferenceCode}`
		);
	}
}
