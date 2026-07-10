/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {liferayConfig} from '../../liferay.config';
import getRandomString from '../../utils/getRandomString';
import {ApiHelpers} from '../ApiHelpers';

export class JSONWebServicesSegmentsEntryApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/api/jsonws/segments.segmentsentry';
	}

	async addSegmentsEntry({
		criteria,
		groupId,
		name,
		source,
	}: {
		criteria: Segment;
		groupId: string;
		name: string;
		source?: string;
	}): Promise<SegmentsEntry> {
		const user =
			await this.apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'test@liferay.com'
			);

		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('active', 'true');
		urlSearchParams.append('criteria', JSON.stringify(criteria));
		urlSearchParams.append(
			'descriptionMap',
			JSON.stringify({en_US: getRandomString()})
		);
		urlSearchParams.append('nameMap', JSON.stringify({en_US: name}));
		urlSearchParams.append('segmentsEntryKey', '');
		urlSearchParams.append(
			'serviceContext',
			JSON.stringify({scopeGroupId: groupId, userId: user.userId})
		);

		if (source) {
			urlSearchParams.append('source', source);
		}

		return await this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/add-segments-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async deleteSegmentsEntry(segmentsEntryId: string): Promise<void> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('segmentsEntryId', segmentsEntryId);

		return await this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/delete-segments-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
