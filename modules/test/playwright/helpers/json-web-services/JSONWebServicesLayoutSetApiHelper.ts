/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createReadStream} from 'fs';

import {liferayConfig} from '../../liferay.config';
import {ApiHelpers} from '../ApiHelpers';

export class JSONWebServicesLayoutSetApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/api/jsonws/layoutset';
	}

	async updateLayoutSetPrototypeLinkEnabled({
		groupId,
		layoutSetPrototypeLinkEnabled,
		layoutSetPrototypeUuid,
	}: {
		groupId: string;
		layoutSetPrototypeLinkEnabled: boolean;
		layoutSetPrototypeUuid: string;
	}) {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('groupId', groupId);
		urlSearchParams.append(
			'layoutSetPrototypeLinkEnabled',
			layoutSetPrototypeLinkEnabled.toString()
		);
		urlSearchParams.append(
			'layoutSetPrototypeUuid',
			layoutSetPrototypeUuid
		);
		urlSearchParams.append('privateLayout', false.toString());

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/update-layout-set-prototype-link-enabled`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async updateLogo({
		filePath,
		groupId,
		privateLayout = false,
	}: {
		filePath: string;
		groupId: string;
		privateLayout?: boolean;
	}) {
		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/update-logo`,
			{
				failOnStatusCode: true,
				headers: {
					Authorization: this.apiHelpers.getAuthorizationHeader(),
					...(await this.apiHelpers.getCSRFTokenHeader()),
				},
				multipart: {
					file: createReadStream(filePath),
					groupId,
					hasLogo: true,
					privateLayout,
				},
			}
		);
	}

	async updateVirtualHosts({
		groupId,
		virtualHostname,
	}: {
		groupId: string;
		virtualHostname: string;
	}) {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('groupId', groupId);
		urlSearchParams.append('privateLayout', false.toString());
		urlSearchParams.append(
			'virtualHostnames',
			JSON.stringify({[virtualHostname]: 'en_US'})
		);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/update-virtual-hosts`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
