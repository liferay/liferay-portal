/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {liferayConfig} from '../../liferay.config';
import getRandomString from '../../utils/getRandomString';
import {ApiHelpers} from '../ApiHelpers';

export enum KB_WORKFLOW_ACTION {
	PUBLISH = 1,
	SAVE_DRAFT = 2,
}

type TKBArticle = {
	kbArticleId: string;
	resourcePrimKey: string;
	status: number;
	title: string;
};

export class JSONWebServicesKnowledgeBaseApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/api/jsonws/kb.kbarticle';
	}

	async addKBArticle({
		content = getRandomString(),
		expirationDate,
		groupId,
		reviewDate,
		title = getRandomString(),
		workflowAction = KB_WORKFLOW_ACTION.PUBLISH,
	}: {
		content?: string;
		expirationDate?: Date;
		groupId: number | string;
		reviewDate?: Date;
		title?: string;
		workflowAction?: KB_WORKFLOW_ACTION;
	}): Promise<TKBArticle> {
		const className =
			await this.apiHelpers.jsonWebServicesClassName.fetchClassName(
				'com.liferay.knowledge.base.model.KBFolder'
			);

		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('externalReferenceCode', '');
		urlSearchParams.append(
			'portletId',
			'com_liferay_knowledge_base_web_portlet_AdminPortlet'
		);
		urlSearchParams.append(
			'parentResourceClassNameId',
			className.classNameId
		);
		urlSearchParams.append('parentResourcePrimKey', '0');
		urlSearchParams.append('title', title);
		urlSearchParams.append('urlTitle', '');
		urlSearchParams.append('content', `<p>${content}</p>`);
		urlSearchParams.append('description', '');
		urlSearchParams.append('-sections', '');
		urlSearchParams.append('sourceURL', '');
		urlSearchParams.append('displayDate', String(Date.now()));

		if (expirationDate) {
			urlSearchParams.append(
				'expirationDate',
				String(expirationDate.getTime())
			);
		}
		else {
			urlSearchParams.append('-expirationDate', '');
		}

		if (reviewDate) {
			urlSearchParams.append('reviewDate', String(reviewDate.getTime()));
		}
		else {
			urlSearchParams.append('-reviewDate', '');
		}

		urlSearchParams.append('-selectedFileNames', '');
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
			`${liferayConfig.environment.baseUrl}${this.basePath}/add-kb-article`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
