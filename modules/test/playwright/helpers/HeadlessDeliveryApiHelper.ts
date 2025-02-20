/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import * as fs from 'fs';

import getRandomString from '../utils/getRandomString';
import {ApiHelpers} from './ApiHelpers';

interface createSitePageProps {
	pageDefinition?: PageDefinition;
	pagePermissions?: PagePermission[];
	parentSitePage?: {friendlyUrlPath: string};
	siteId: string;
	title: string;
}

type TDocument = {
	contentUrl?: string;
	description?: string;
	documentFolderId?: number;
	externalReferenceCode?: string;
	fileName?: string;
	id?: number;
	keywords?: string[];
	taxonomyCategoryIds?: number[];
	title?: string;
	viewableBy?: string;
};

type TDocumentFolder = {
	externalReferenceCode?: string;
	id?: number;
	name?: string;
	parentDocumentFolderId?: number;
	viewableBy?: string;
};

type TWikiNode = {
	description?: string;
	externalReferenceCode?: string;
	id?: number;
	name?: string;
	viewableBy?: string;
};

type TWikiPage = {
	content?: string;
	description?: string;
	encodingFormat?: string;
	externalReferenceCode?: string;
	headline?: string;
	id?: number;
	viewableBy?: string;
};

export class HeadlessDeliveryApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'headless-delivery/v1.0';
	}

	/**
	 * This method requires the feature flag LPS-178052 to be enabled,
	 * please enable it in your test if using it.
	 *
	 * It allows creating a page inside a site.
	 *
	 * @param siteId the id of the site in which the page will be created
	 * @param title the title of the page
	 * @param pageDefinition the definition of the page in case that we want
	 * to specify some content for it, for example some fragments+
	 */
	async createSitePage({
		pageDefinition,
		pagePermissions,
		parentSitePage,
		siteId,
		title,
	}: createSitePageProps): Promise<Layout> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/site-pages`,
			{data: {pageDefinition, pagePermissions, parentSitePage, title}}
		);
	}

	async deleteBlog(blogId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/blog-postings/${blogId}`
		);
	}

	async deleteDocument(documentId: string) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/documents/${documentId}`
		);
	}

	async deleteSiteDocumentsFolderByExternalReferenceCode(
		externalReferenceCode: string
	) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/Guest/documents-folder/by-external-reference-code/${externalReferenceCode}`
		);
	}

	async getSiteDocumentsPage(siteId: string, sort: string = 'id') {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/documents?sort=${sort}`
		);
	}

	async getSitePage(friendlyUrlPath: string, siteId: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/site-pages/${friendlyUrlPath}`
		);
	}

	async getSitePages(siteId: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/site-pages`
		);
	}

	async postBlog(
		siteId: number | string,
		blog?: {
			articleBody?: string;
			headline?: string;
			keywords?: string[];
		}
	): Promise<any> {
		blog = {
			articleBody: getRandomString(),
			headline: getRandomString(),
			...(blog || {}),
		};

		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/blog-postings`,
			{
				data: blog,
				failOnStatusCode: true,
			}
		);
	}

	async postSiteKnowledgeBaseArticle({
		articleBody,
		siteId,
		title,
	}: {
		articleBody: string;
		siteId: string;
		title: string;
	}): Promise<KnowledgeBaseArticle> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/knowledge-base-articles`,
			{
				data: {
					articleBody,
					title,
				},
				failOnStatusCode: true,
			}
		);
	}

	async postMessageBoardThread({
		articleBody,
		headline,
		siteId,
	}: {
		articleBody: string;
		headline: string;
		siteId: string;
	}): Promise<MessageBoardThread> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/message-board-threads`,
			{
				data: {
					articleBody,
					headline,
				},
				failOnStatusCode: true,
			}
		);
	}

	async postMessageBoardMessage({
		articleBody,
		messageBoardThreadId,
	}: {
		articleBody: string;
		messageBoardThreadId: string;
	}): Promise<MessageBoardMessage> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/message-board-threads/${messageBoardThreadId}/message-board-messages`,
			{
				data: {
					articleBody,
				},
				failOnStatusCode: true,
			}
		);
	}

	async postStructuredContent({
		categoryIds,
		contentStructureId,
		datePublished,
		description = '',
		relatedContents,
		siteId,
		tags,
		title,
		viewableBy = '',
	}: {
		categoryIds?: number[];
		contentStructureId: number;
		datePublished: string;
		description?: string;
		relatedContents?: {contentType: string; id: number; title: string}[];
		siteId: string;
		tags?: string[];
		title: string;
		viewableBy?: string;
	}): Promise<StructuredContent> {
		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/structured-contents`,
			{
				data: {
					contentStructureId,
					datePublished,
					description,
					keywords: tags,
					relatedContents,
					taxonomyCategoryIds: categoryIds,
					title,
					viewableBy,
				},
				failOnStatusCode: true,
			}
		);
	}

	async getStructuredContentByKey(
		siteId: string,
		key: string
	): Promise<StructuredContent> {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/structured-contents/by-key/${key}`,
			true
		);
	}

	async postWikiNode(
		siteId: number | string,
		wikiNode?: TWikiNode
	): Promise<TWikiNode> {
		wikiNode = {
			description: getRandomString(),
			externalReferenceCode: getRandomString(),
			name: getRandomString(),
			viewableBy: 'Anyone',
			...(wikiNode || {}),
		};

		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/wiki-nodes`,
			{
				data: wikiNode,
				failOnStatusCode: true,
			}
		);
	}

	async postWikiPage(
		wikiNodeId: number | string,
		wikiPage?: TWikiPage
	): Promise<TWikiPage> {
		wikiPage = {
			content: getRandomString(),
			description: getRandomString(),
			encodingFormat: 'plain_text',
			externalReferenceCode: getRandomString(),
			headline: getRandomString(),
			viewableBy: 'Anyone',
			...(wikiPage || {}),
		};

		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/wiki-nodes/${wikiNodeId}/wiki-pages`,
			{
				data: wikiPage,
				failOnStatusCode: true,
			}
		);
	}

	async postDocument(
		siteId: number | string,
		file: fs.ReadStream,
		document?: TDocument
	) {
		document = {
			description: getRandomString(),
			externalReferenceCode: getRandomString(),
			fileName: getRandomString(),
			title: getRandomString(),
			viewableBy: 'Anyone',
			...(document || {}),
		};

		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/documents`,
			{
				failOnStatusCode: true,
				headers: {
					...(await this.apiHelpers.getCSRFTokenHeader()),
				},
				multipart: {
					document: JSON.stringify(document),
					file,
				},
			}
		);
	}

	async postDocumentFolder(
		siteId: number | string,
		documentFolder?: TDocumentFolder
	) {
		documentFolder = {
			externalReferenceCode: getRandomString(),
			name: getRandomString(),
			viewableBy: 'Anyone',
			...(documentFolder || {}),
		};

		return this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/sites/${siteId}/document-folders`,
			{
				data: documentFolder,
				failOnStatusCode: true,
				headers: {
					...(await this.apiHelpers.getCSRFTokenHeader()),
				},
			}
		);
	}

	async patchDocument({
		document,
		documentId,
		file,
	}: {
		document?: TDocument;
		documentId: number;
		file?: fs.ReadStream;
	}) {
		return this.apiHelpers.patchRequestOptions(
			`${this.apiHelpers.baseUrl}${this.basePath}/documents/${documentId}`,
			{
				failOnStatusCode: true,
				headers: {
					...(await this.apiHelpers.getCSRFTokenHeader()),
				},
				multipart: {
					document: JSON.stringify(document),
					file,
				},
			}
		);
	}

	async putBlog(
		blogPostingId: number | string,
		blog?: {
			articleBody?: string;
			headline?: string;
		}
	): Promise<any> {
		blog = {
			articleBody: getRandomString(),
			headline: getRandomString(),
			...(blog || {}),
		};

		return this.apiHelpers.put(
			`${this.apiHelpers.baseUrl}${this.basePath}/blog-postings/${blogPostingId}`,
			{
				data: blog,
				failOnStatusCode: true,
			}
		);
	}
}
