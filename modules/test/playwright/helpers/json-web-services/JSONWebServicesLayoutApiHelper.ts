/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect} from '@playwright/test';

import {liferayConfig} from '../../liferay.config';
import getRandomString from '../../utils/getRandomString';
import {ApiHelpers} from '../ApiHelpers';

export class JSONWebServicesLayoutApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = '/api/jsonws/layout';
	}

	/**
	 * Creates a visible, public, root level layout in the given group with the given name.
	 *
	 * @param title
	 * Used to set the title, name and friendlyURL
	 *
	 * @param options.publish
	 * Whether to publish the layout or leave it as draft. It can only be used with 'content' type
	 * layouts: if you provide a value other than `undefined` for any other type of layout the
	 * method will fail with an exception.
	 *
	 * @param options.type
	 * The layout type (eg: 'portlet' or 'content')
	 */
	async addLayout({
		externalReferenceCode = '',
		masterLayoutPageTemplateEntryERC = '',
		groupId,
		options = {type: 'portlet'},
		parentLayoutId = '0',
		privateLayout = 'false',
		title,
		typeSettings = '',
	}: {
		externalReferenceCode?: string;
		groupId: string;
		masterLayoutPageTemplateEntryERC?: string;
		options?: {publish?: boolean; type?: string};
		parentLayoutId?: string;
		privateLayout?: string;
		title: string;
		typeSettings?: string;
	}): Promise<Layout> {
		if (options.publish && options.type !== 'content') {
			throw new TypeError(
				`Publish parameter can only be 'undefined' for non content layouts`
			);
		}

		const name = title;

		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('externalReferenceCode', externalReferenceCode);
		urlSearchParams.append('groupId', groupId);
		urlSearchParams.append('privateLayout', privateLayout);
		urlSearchParams.append('parentLayoutId', parentLayoutId);
		urlSearchParams.append('localeNamesMap', JSON.stringify({en_US: name}));
		urlSearchParams.append(
			'localeTitlesMap',
			JSON.stringify({en_US: title})
		);
		urlSearchParams.append(
			'descriptionMap',
			JSON.stringify({en_US: getRandomString()})
		);
		urlSearchParams.append('keywordsMap', JSON.stringify({en_US: ''}));
		urlSearchParams.append('robotsMap', JSON.stringify({en_US: ''}));
		urlSearchParams.append('type', options.type);
		urlSearchParams.append('typeSettings', typeSettings);
		urlSearchParams.append('hidden', 'false');
		urlSearchParams.append(
			'friendlyURLMap',
			JSON.stringify({en_US: `/${title}`})
		);
		urlSearchParams.append(
			'masterLayoutPageTemplateEntryERC',
			masterLayoutPageTemplateEntryERC
		);

		urlSearchParams.append('serviceContext', JSON.stringify({}));

		const layout = await this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/add-layout`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);

		// Content layouts are edited through their draft layout

		if (options.type === 'content') {
			layout.draftLayout = await this.getDraftLayout(layout);
		}

		// Publish content layouts using UI (since there's no headless method available yet)

		if (options.publish && options.type === 'content') {
			const page = this.apiHelpers.page;

			await page.goto(
				`${liferayConfig.environment.baseUrl}/group/guest/~/control_panel/manage` +
					'?p_p_id=com_liferay_layout_admin_web_portlet_GroupPagesPortlet'
			);

			await page.getByLabel(name, {exact: true}).click();
			await page.getByLabel('Publish', {exact: true}).click();

			expect(page.getByRole('heading', {name: 'Pages'})).toBeAttached();
		}

		return layout;
	}

	async deleteLayout(plid: string): Promise<void> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('plid', plid);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/delete-layout`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	/**
	 * Returns the draft layout of the given content layout, which is the one
	 * serving the page editor. The draft layout carries the external reference
	 * code of its live layout with a '-draft' suffix.
	 */
	async getDraftLayout(layout: Layout): Promise<Layout> {
		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append(
			'externalReferenceCode',
			`${layout.externalReferenceCode}-draft`
		);
		urlSearchParams.append('groupId', layout.groupId);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/get-layout-by-external-reference-code`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}

	async getLayoutsCount(
		groupId: number,
		privateLayout: boolean
	): Promise<number> {
		const urlSearchParams = new URLSearchParams();

		// @ts-ignore

		urlSearchParams.append('groupId', groupId);

		// @ts-ignore

		urlSearchParams.append('privateLayout', privateLayout);

		return this.apiHelpers.post(
			`${liferayConfig.environment.baseUrl}${this.basePath}/get-layouts-count`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await this.apiHelpers.getJSONWebServicesHeaders(),
			}
		);
	}
}
