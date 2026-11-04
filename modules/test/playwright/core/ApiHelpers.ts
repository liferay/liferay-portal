/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {ObjectAdminApiHelper} from './ObjectAdminApiHelper';
import {ObjectEntryApiHelper} from './ObjectEntryApiHelper';
import {clearAuthToken, getCSRFTokenHeader, getHeader} from './authToken';
import {liferayConfig} from './liferayConfig';

export type TDataApiHelpersData = {
	applicationName?: string;
	id: any;
	type: string;
};

export interface RequestOptions<T> {
	data?: T;
	failOnStatusCode?: boolean;
	headers?: {[key: string]: string};
	multipart?: {[key: string]: any};
}

interface HeadlessClientWithHeaders {
	defaultHeaders: Record<string, string>;
}

export class BaseApiHelpers {
	readonly baseUrl: string;
	readonly data: TDataApiHelpersData[];
	readonly objectAdmin: ObjectAdminApiHelper;
	readonly objectEntry: ObjectEntryApiHelper;
	readonly page: Page;

	protected static readonly _authorization = `Basic ${btoa(
		`test@liferay.com:${liferayConfig.environment.password}`
	)}`;

	constructor(page: Page, baseUrl?: string) {
		this.baseUrl = baseUrl
			? baseUrl + '/o/'
			: liferayConfig.environment.baseUrl + '/o/';
		this.data = [];
		this.objectAdmin = new ObjectAdminApiHelper(this);
		this.objectEntry = new ObjectEntryApiHelper(this);
		this.page = page;
	}

	async buildRestClient<
		T extends new (
			baseUrl: string
		) => InstanceType<T> & HeadlessClientWithHeaders,
	>(ApiClientClass: T): Promise<InstanceType<T>> {
		const apiInstance = new ApiClientClass(
			liferayConfig.environment.baseUrl + '/o'
		);

		apiInstance.defaultHeaders = {
			Cookie: `JSESSIONID=${await this.getJSessionId()};`,
			...(await getCSRFTokenHeader(this.page)),
		};

		return apiInstance;
	}

	async clearData() {
		const failures: string[] = [];

		for await (const item of this.data.reverse()) {
			try {
				await this.deleteData(item);
			}
			catch (error) {
				failures.push(`${item.type} ${item.id}: ${error.message}`);
			}
		}

		if (failures.length) {
			console.warn(
				`Unable to delete ${failures.length} created entities:\n${failures.join('\n')}`
			);
		}
	}

	async collectObjectDefinitionIds(
		objectDefinitionId: number,
		objectDefinitionIds: number[],
		visitedObjectDefinitionIds: Set<number>
	) {
		if (visitedObjectDefinitionIds.has(objectDefinitionId)) {
			return;
		}

		visitedObjectDefinitionIds.add(objectDefinitionId);

		const objectDefinition = await this.get(
			`${this.baseUrl}object-admin/v1.0/object-definitions/${objectDefinitionId}`
		);

		const objectRelationships = await this.get(
			`${this.baseUrl}object-admin/v1.0/object-definitions/by-external-reference-code/${objectDefinition.externalReferenceCode}/object-relationships`
		);

		const isCMSObjectDefinition =
			objectDefinition.objectFolderExternalReferenceCode?.startsWith(
				'L_CMS'
			);

		for (const objectRelationship of objectRelationships.items) {
			if (!objectRelationship.edge) {
				continue;
			}

			if (
				isCMSObjectDefinition &&
				objectRelationship.objectDefinitionId2
			) {
				const relatedObjectDefinition = await this.get(
					`${this.baseUrl}object-admin/v1.0/object-definitions/${objectRelationship.objectDefinitionId2}`
				);

				if (
					relatedObjectDefinition.objectFolderExternalReferenceCode ===
					'L_CMS_STRUCTURE_REPEATABLE_GROUPS'
				) {
					await this.collectObjectDefinitionIds(
						objectRelationship.objectDefinitionId2,
						objectDefinitionIds,
						visitedObjectDefinitionIds
					);
				}
			}

			await this.put(
				`${this.baseUrl}object-admin/v1.0/object-relationships/${objectRelationship.id}`,
				{
					data: {
						...objectRelationship,
						edge: false,
					},
				}
			);
		}

		objectDefinitionIds.push(objectDefinitionId);
	}

	async delete<T>(
		url: string,
		{data, failOnStatusCode, headers}: RequestOptions<T> = {}
	) {
		return this._sendRequest(
			'delete',
			url,
			{data, failOnStatusCode: failOnStatusCode || false},
			undefined,
			headers
		);
	}

	async deleteObjectDefinition(objectDefinitionId: number) {
		const objectDefinitionIds: number[] = [];

		await this.collectObjectDefinitionIds(
			objectDefinitionId,
			objectDefinitionIds,
			new Set()
		);

		for (const id of objectDefinitionIds) {
			await this.delete(
				`${this.baseUrl}object-admin/v1.0/object-definitions/${id}`
			);
		}
	}

	async get(
		url: string,
		failOnStatusCode?: boolean,
		headers?: {[key: string]: string}
	) {
		const response = await this.getResponse(url, failOnStatusCode, headers);

		return response.json();
	}

	getAuthorizationHeader() {
		return BaseApiHelpers._authorization;
	}

	async getCSRFTokenHeader() {
		return getCSRFTokenHeader(this.page);
	}

	async getJSONWebServicesHeaders() {
		return {
			'Authorization': BaseApiHelpers._authorization,
			'Content-Type': 'application/x-www-form-urlencoded',
			...(await getCSRFTokenHeader(this.page)),
		};
	}

	async getJSessionId() {
		const cookies = await this.page.context().cookies();

		return cookies.find((cookie) => cookie.name === 'JSESSIONID').value;
	}

	async getResponse(
		url: string,
		failOnStatusCode?: boolean,
		headers?: {[key: string]: string}
	) {
		return await this._sendRequest(
			'get',
			url,
			{failOnStatusCode: failOnStatusCode || false},
			headers
		);
	}

	async patch<T>(url: string, data: T) {
		const response = await this._sendRequest('patch', url, {data});

		const text = await response.text();

		if (!text) {
			return response;
		}

		return response.json();
	}

	async patchRequestOptions<T>(url: string, options: RequestOptions<T> = {}) {
		const response = await this._sendRequest(
			'patch',
			url,
			{
				data: options.data,
				failOnStatusCode: options.failOnStatusCode || false,
				multipart: options.multipart,
			},
			options.headers
		);

		const text = await response.text();

		if (!text) {
			return response;
		}

		return response.json();
	}

	async post<T>(url: string, options: RequestOptions<T> = {}) {
		const response = await this.postResponse(url, options);
		const status = response.status();

		if (!response.ok()) {
			const error = await response.text();

			throw new Error(
				`POST request to ${url} failed with code ${status}:\n\n${error}`
			);
		}

		if (status === 204) {
			return;
		}

		return response.json();
	}

	async postResponse<T>(
		url: string,
		{data, failOnStatusCode, headers, multipart}: RequestOptions<T> = {}
	) {
		return await this._sendRequest(
			'post',
			url,
			{data, failOnStatusCode: failOnStatusCode || false, multipart},
			headers
		);
	}

	async put<T>(url: string, options: RequestOptions<T> = {}) {
		const response = await this.putResponse(url, options);

		if (response.status() === 204) {
			return;
		}

		return response.json();
	}

	async putResponse<T>(
		url: string,
		{data, failOnStatusCode, headers, multipart}: RequestOptions<T> = {}
	) {
		return await this._sendRequest(
			'put',
			url,
			{data, failOnStatusCode: failOnStatusCode || false, multipart},
			headers
		);
	}

	setData(data: TDataApiHelpersData[]) {
		this.data.length = 0;

		while (data.length) {
			this.data.unshift(data.pop());
		}
	}

	protected async deleteData(item: TDataApiHelpersData) {
		if (item.type === 'objectAction') {
			await this.delete(
				`${this.baseUrl}object-admin/v1.0/object-actions/${item.id}`
			);
		}
		else if (item.type === 'objectDefinition') {
			await this.deleteObjectDefinition(item.id);
		}
		else if (item.type === 'objectEntry') {
			await this.objectEntry.deleteObjectEntry(
				item.applicationName!,
				item.id
			);
		}
		else if (item.type === 'objectFolder') {
			await this.delete(
				`${this.baseUrl}object-admin/v1.0/object-folders/${item.id}`
			);
		}
		else if (item.type === 'objectRelationship') {
			await this.delete(
				`${this.baseUrl}object-admin/v1.0/object-relationships/${item.id}`
			);
		}
	}

	protected async _sendRequest(
		method: 'delete' | 'get' | 'patch' | 'post' | 'put',
		url: string,
		options: {[key: string]: unknown} = {},
		headers?: {[key: string]: string},
		extraHeaders?: {[key: string]: string}
	) {
		const buildHeaders = async () =>
			headers || {
				...(await getHeader(this.page)),
				...(extraHeaders || {}),
			};

		const response = await this.page.request[method](url, {
			...options,
			headers: await buildHeaders(),
		});

		if (headers || response.status() !== 403) {
			return response;
		}

		clearAuthToken(this.page);

		return await this.page.request[method](url, {
			...options,
			headers: await buildHeaders(),
		});
	}
}
