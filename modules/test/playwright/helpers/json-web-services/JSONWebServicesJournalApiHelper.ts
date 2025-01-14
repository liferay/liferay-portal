/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {liferayConfig} from '../../liferay.config';
import getRandomString from '../../utils/getRandomString';
import {ApiHelpers} from '../ApiHelpers';

type TFolder = {
	description?: string;
	externalReferenceCode?: string;
	folderId?: number;
	groupId: number | string;
	name?: string;
	parentFolderId?: number;
};

type TWebContent = {
	articleId?: string;
	content?: string;
	ddmStructureId: number | string;
	ddmTemplateKey?: string;
	descriptionMap?: any;
	externalReferenceCode?: string;
	folderId?: number | string;
	groupId: number | string;
	serviceContext?: any;
	titleMap?: any;
};

export class JSONWebServicesJournalApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly baseFolderPath: string;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.baseFolderPath = '/api/jsonws/journal.journalfolder';
		this.basePath = '/api/jsonws/journal.journalarticle';
	}

	async addFolder(folder?: TFolder): Promise<TFolder> {
		const urlSearchParams = new URLSearchParams();

		folder = {
			description: getRandomString(),
			externalReferenceCode: getRandomString(),
			groupId: 0,
			name: getRandomString(),
			parentFolderId: 0,
			...(folder || {}),
		};

		urlSearchParams.append(
			'externalReferenceCode',
			folder.externalReferenceCode
		);
		urlSearchParams.append('groupId', String(folder.groupId));
		urlSearchParams.append('parentFolderId', String(folder.parentFolderId));
		urlSearchParams.append('name', folder.name);
		urlSearchParams.append('description', folder.description);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.baseFolderPath}/add-folder`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async addWebContent(webContent?: TWebContent): Promise<TWebContent> {
		const urlSearchParams = new URLSearchParams();

		webContent = {
			content: getRandomString(),
			ddmStructureId: 0,
			ddmTemplateKey: 'BASIC-WEB-CONTENT',
			descriptionMap: {en_US: getRandomString()},
			externalReferenceCode: getRandomString(),
			folderId: 0,
			groupId: 0,
			serviceContext: {},
			titleMap: {en_US: getRandomString()},
			...(webContent || {}),
		};

		urlSearchParams.append(
			'externalReferenceCode',
			webContent.externalReferenceCode
		);
		urlSearchParams.append('groupId', String(webContent.groupId));
		urlSearchParams.append('folderId', String(webContent.folderId));
		urlSearchParams.append('titleMap', JSON.stringify(webContent.titleMap));
		urlSearchParams.append(
			'descriptionMap',
			JSON.stringify(webContent.descriptionMap)
		);
		
		urlSearchParams.append('content', `<root>
				<dynamic-element field-reference="content" index-type="text" name="content" type="rich_text">
				<dynamic-content><![CDATA[<p>${webContent.content}</p>]]></dynamic-content>
				</dynamic-element>
				</root>`);

		urlSearchParams.append(
			'ddmStructureId',
			String(webContent.ddmStructureId)
		);
		urlSearchParams.append('ddmTemplateKey', webContent.ddmTemplateKey);
		urlSearchParams.append(
			'serviceContext',
			JSON.stringify(webContent.serviceContext)
		);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/add-article`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async expireArticle(siteId: string, articleId: string): Promise<void> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('groupId', siteId);
		urlSearchParams.append('articleId', articleId);
		urlSearchParams.append('articleURL', '');

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/expire-article`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async moveArticleToTrash(siteId: string, articleId: string): Promise<void> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('groupId', siteId);
		urlSearchParams.append('articleId', articleId);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/move-article-to-trash`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
