/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {liferayConfig} from '../../liferay.config';
import getRandomString from '../../utils/getRandomString';
import {ApiHelpers} from '../ApiHelpers';

export enum BLOGS_WORKFLOW_ACTION {
	PUBLISH = 1,
	SAVE_DRAFT = 2,
}

type TBlogsEntry = {
	entryId: string;
	status: number;
	title: string;
};

export class JSONWebServicesBlogsApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/api/jsonws/blogs.blogsentry';
	}

	async addEntry({
		content = getRandomString(),
		displayDate = new Date(),
		groupId,
		title = getRandomString(),
		workflowAction = BLOGS_WORKFLOW_ACTION.PUBLISH,
	}: {
		content?: string;
		displayDate?: Date;
		groupId: number | string;
		title?: string;
		workflowAction?: BLOGS_WORKFLOW_ACTION;
	}): Promise<TBlogsEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('externalReferenceCode', '');
		urlSearchParams.append('title', title);
		urlSearchParams.append('subtitle', '');
		urlSearchParams.append('urlTitle', '');
		urlSearchParams.append('description', '');
		urlSearchParams.append('content', `<p>${content}</p>`);
		urlSearchParams.append(
			'displayDateMonth',
			String(displayDate.getMonth())
		);
		urlSearchParams.append('displayDateDay', String(displayDate.getDate()));
		urlSearchParams.append(
			'displayDateYear',
			String(displayDate.getFullYear())
		);
		urlSearchParams.append(
			'displayDateHour',
			String(displayDate.getHours())
		);
		urlSearchParams.append(
			'displayDateMinute',
			String(displayDate.getMinutes())
		);
		urlSearchParams.append('allowPingbacks', 'false');
		urlSearchParams.append('allowTrackbacks', 'false');
		urlSearchParams.append('trackbacks', '');
		urlSearchParams.append('coverImageCaption', '');
		urlSearchParams.append('-coverImageImageSelector', '');
		urlSearchParams.append('-smallImageImageSelector', '');
		urlSearchParams.append(
			'serviceContext',
			JSON.stringify({
				addGroupPermissions: true,
				addGuestPermissions: true,
				scopeGroupId: groupId,
				workflowAction,
			})
		);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/add-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
