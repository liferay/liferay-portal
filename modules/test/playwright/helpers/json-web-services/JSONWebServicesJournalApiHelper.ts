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
	description?: string;
	descriptionMap?: any;
	externalReferenceCode?: string;
	folderId?: number | string;
	groupId: number | string;
	layoutUuid?: string;
	resourcePrimKey?: number | string;
	serviceContext?: any;
	title?: string;
	titleMap?: any;
	userId?: number | string;
	version?: number | string;
};

type TWebContentDetailed = TWebContent & {
	articleURL?: string;
	autoArticleId?: boolean;
	classNameId?: number;
	classPK?: number;
	displayDateDay?: number;
	displayDateHour?: number;
	displayDateMinute?: number;
	displayDateMonth?: number;
	displayDateYear?: number;
	expirationDateDay?: number;
	expirationDateHour?: number;
	expirationDateMinute?: number;
	expirationDateMonth?: number;
	expirationDateYear?: number;
	friendlyURLMap?: Record<string, string>;
	images?: Record<string, any>;
	indexable?: boolean;
	layoutUuid?: string;
	neverExpire?: boolean;
	neverReview?: boolean;
	reviewDateDay?: number;
	reviewDateHour?: number;
	reviewDateMinute?: number;
	reviewDateMonth?: number;
	reviewDateYear?: number;
	smallFile?: any;
	smallImage?: boolean;
	smallImageId?: number;
	smallImageSource?: number;
	smallImageURL?: string;
	titleMap?: Record<string, string>;
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
			'content',
			`<root>
				<dynamic-element field-reference="content" index-type="text" name="content" type="rich_text">
				<dynamic-content><![CDATA[<p>${webContent.content}</p>]]></dynamic-content>
				</dynamic-element>
				</root>`
		);

		urlSearchParams.append(
			'descriptionMap',
			JSON.stringify(webContent.descriptionMap)
		);

		urlSearchParams.append(
			'ddmStructureId',
			String(webContent.ddmStructureId)
		);

		urlSearchParams.append('ddmTemplateKey', webContent.ddmTemplateKey);
		urlSearchParams.append(
			'externalReferenceCode',
			webContent.externalReferenceCode
		);
		urlSearchParams.append('folderId', String(webContent.folderId));
		urlSearchParams.append('groupId', String(webContent.groupId));
		urlSearchParams.append('titleMap', JSON.stringify(webContent.titleMap));
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

	async addWebContentDetailed(
		webContent: TWebContentDetailed
	): Promise<TWebContentDetailed> {
		const urlSearchParams = new URLSearchParams();

		webContent = {
			articleId: '',
			articleURL: '',
			autoArticleId: true,
			classNameId: 0,
			classPK: 0,
			content: getRandomString(),
			ddmStructureId: 0,
			ddmTemplateKey: 'BASIC-WEB-CONTENT',
			descriptionMap: {en_US: getRandomString()},
			displayDateDay: 0,
			displayDateHour: 0,
			displayDateMinute: 0,
			displayDateMonth: 0,
			displayDateYear: 0,
			expirationDateDay: 0,
			expirationDateHour: 0,
			expirationDateMinute: 0,
			expirationDateMonth: 0,
			expirationDateYear: 0,
			externalReferenceCode: getRandomString(),
			folderId: 0,
			friendlyURLMap: {en_US: getRandomString()},
			groupId: 0,
			images: {},
			indexable: false,
			layoutUuid: '',
			neverExpire: true,
			neverReview: true,
			reviewDateDay: 0,
			reviewDateHour: 0,
			reviewDateMinute: 0,
			reviewDateMonth: 0,
			reviewDateYear: 0,
			serviceContext: {},
			smallFile: new Blob(['']),
			smallImage: false,
			smallImageId: 0,
			smallImageSource: 0,
			smallImageURL: '',
			titleMap: {en_US: getRandomString()},
			...webContent,
		};

		Object.entries(webContent).map(([key, value]) => {
			urlSearchParams.append(
				key,
				typeof value === 'object' ? JSON.stringify(value) : value
			);
		});
		urlSearchParams.set(
			'content',
			`<root>
				<dynamic-element field-reference="content" index-type="text" name="content" type="rich_text">
				<dynamic-content><![CDATA[<p>${webContent.content}</p>]]></dynamic-content>
				</dynamic-element>
			</root>`
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

	async editWebContent(
		contentEdit,
		groupId,
		webContent?: TWebContent
	): Promise<TWebContent> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('userId', String(webContent.userId));
		urlSearchParams.append('groupId', String(groupId));

		if (Object.prototype.hasOwnProperty.call(contentEdit, 'folderId')) {
			urlSearchParams.append('folderId', contentEdit.folderId);
		}
		else {
			urlSearchParams.append('folderId', String(webContent.folderId));
		}

		urlSearchParams.append('articleId', webContent.articleId);
		urlSearchParams.append('version', String(webContent.version));

		if (Object.prototype.hasOwnProperty.call(contentEdit, 'title')) {
			urlSearchParams.append(
				'titleMap',
				JSON.stringify({en_US: contentEdit.title})
			);
		}
		else {
			urlSearchParams.append(
				'titleMap',
				JSON.stringify({en_US: webContent.title})
			);
		}

		if (Object.prototype.hasOwnProperty.call(contentEdit, 'description')) {
			urlSearchParams.append(
				'descriptionMap',
				JSON.stringify({en_US: contentEdit.description})
			);
		}
		else {
			urlSearchParams.append(
				'descriptionMap',
				JSON.stringify({en_US: webContent.description})
			);
		}

		if (Object.prototype.hasOwnProperty.call(contentEdit, 'content')) {
			urlSearchParams.append(
				'content',
				`<root>
					<dynamic-element field-reference="content" index-type="text" name="content" type="rich_text">
					<dynamic-content><![CDATA[<p>${contentEdit.content}</p>]]></dynamic-content>
					</dynamic-element>
					</root>`
			);
		}
		else {
			urlSearchParams.append('content', webContent.content);
		}

		urlSearchParams.append('layoutUuid', String(webContent.layoutUuid));
		urlSearchParams.append(
			'serviceContext',
			JSON.stringify({
				scopeGroupId: webContent.groupId,
				userId: webContent.userId,
			})
		);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/update-article`,
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

	async getArticleByUrlTitle(
		siteId: string,
		urlTitle: string
	): Promise<TWebContent> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('groupId', siteId);
		urlSearchParams.append('urlTitle', urlTitle);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/get-article-by-url-title`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
