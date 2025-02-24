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
		classTypeId = '0',
		externalReferenceCode = '',
		groupId,
		name,
	}: {
		classNameId: string;
		classTypeId?: string;
		externalReferenceCode?: string;
		groupId: string;
		name: string;
		type?: LayoutPageTemplateEntryType;
	}): Promise<LayoutPageTemplateEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('classNameId', classNameId);
		urlSearchParams.append('classTypeId', classTypeId);
		urlSearchParams.append('externalReferenceCode', externalReferenceCode);
		urlSearchParams.append('groupId', groupId);
		urlSearchParams.append('layoutPageTemplateCollectionId', '0');
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
		name,
		type = 'basic',
	}: {
		externalReferenceCode?: string;
		groupId: string;
		name: string;
		type?: LayoutPageTemplateEntryType;
	}): Promise<LayoutPageTemplateEntry> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('externalReferenceCode', externalReferenceCode);
		urlSearchParams.append('groupId', groupId);
		urlSearchParams.append('layoutPageTemplateCollectionId', '0');
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
