/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {liferayConfig} from '../../liferay.config';
import {LAYOUT_PAGE_TEMPLATE_ENTRY_TYPES} from '../../utils/layoutPageTemplateEntryTypes';
import {ApiHelpers} from '../ApiHelpers';

export class JSONWebServicesLayoutPageTemplateEntryApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/api/jsonws/layout.layoutpagetemplateentry';
	}

	async addDisplayPageLayoutPageTemplateEntry({
		classNameId,
		classTypeKey = '',
		externalReferenceCode = '',
		groupId,
		layoutPageTemplateEntryKey = '',
		name,
	}: {
		classNameId: string;
		classTypeKey?: string;
		externalReferenceCode?: string;
		groupId: string;
		layoutPageTemplateEntryKey?: string;
		name: string;
		type?: LayoutPageTemplateEntryType;
	}): Promise<LayoutPageTemplateEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('classNameId', classNameId);
		urlSearchParams.append('classTypeKey', classTypeKey);
		urlSearchParams.append('externalReferenceCode', externalReferenceCode);
		urlSearchParams.append('groupId', groupId);
		urlSearchParams.append('layoutPageTemplateCollectionId', '0');
		urlSearchParams.append(
			'layoutPageTemplateEntryKey',
			layoutPageTemplateEntryKey
		);
		urlSearchParams.append('masterLayoutPlid', '0');
		urlSearchParams.append('name', name);
		urlSearchParams.append(
			'type',
			LAYOUT_PAGE_TEMPLATE_ENTRY_TYPES['display-page']
		);
		urlSearchParams.append('status', '0');
		urlSearchParams.append('serviceContext', JSON.stringify({}));

		return await this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/add-layout-page-template-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async addLayoutPageTemplateEntry({
		externalReferenceCode = '',
		groupId,
		layoutPageTemplateCollectionId = '0',
		layoutPageTemplateEntryKey = '',
		name,
		type = 'basic',
	}: {
		externalReferenceCode?: string;
		groupId: string;
		layoutPageTemplateCollectionId?: string;
		layoutPageTemplateEntryKey?: string;
		name: string;
		type?: LayoutPageTemplateEntryType;
	}): Promise<LayoutPageTemplateEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('externalReferenceCode', externalReferenceCode);
		urlSearchParams.append('groupId', groupId);
		urlSearchParams.append(
			'layoutPageTemplateCollectionId',
			layoutPageTemplateCollectionId
		);
		urlSearchParams.append(
			'layoutPageTemplateEntryKey',
			layoutPageTemplateEntryKey
		);
		urlSearchParams.append('name', name);
		urlSearchParams.append('type', LAYOUT_PAGE_TEMPLATE_ENTRY_TYPES[type]);
		urlSearchParams.append('masterLayoutPlid', '0');
		urlSearchParams.append('status', '0');
		urlSearchParams.append('serviceContext', JSON.stringify({}));

		return await this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/add-layout-page-template-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async deleteLayoutPageTemplateEntry({
		layoutPageTemplateEntryId,
	}: {
		layoutPageTemplateEntryId: string;
	}): Promise<LayoutPageTemplateEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append(
			'layoutPageTemplateEntryId',
			layoutPageTemplateEntryId
		);

		return await this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/delete-layout-page-template-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async fetchLayoutPageTemplateEntry({
		groupId,
		name,
		type = 'basic',
	}: {
		groupId: string;
		name: string;
		type?: LayoutPageTemplateEntryType;
	}): Promise<LayoutPageTemplateEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('groupId', groupId);
		urlSearchParams.append('layoutPageTemplateCollectionId', '0');
		urlSearchParams.append('name', name);
		urlSearchParams.append('type', LAYOUT_PAGE_TEMPLATE_ENTRY_TYPES[type]);

		return await this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/fetch-layout-page-template-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async markAsDefaultDisplayPageLayoutPageTemplateEntry({
		layoutPageTemplateEntryId,
	}: {
		layoutPageTemplateEntryId: string;
	}): Promise<LayoutPageTemplateEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append(
			'layoutPageTemplateEntryId',
			layoutPageTemplateEntryId
		);
		urlSearchParams.append('defaultTemplate', 'true');

		return await this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/update-layout-page-template-entry`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
