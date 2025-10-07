/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelpers} from './ApiHelpers';

export class HeadlessPortalInstanceApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'headless-portal-instances/v1.0';
	}

	async addVirtualInstance(data: {
		domain: string;
		portalInstanceId: string;
		virtualHost: string;
	}): Promise<any> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/portal-instances`,
			{data}
		);
	}

	async deleteVirtualInstance(instanceId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/portal-instances/${instanceId}`
		);
	}
}
