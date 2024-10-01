/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';

export class AssetPublisherPage {
	readonly page: Page;

	readonly configurationIframe: FrameLocator;

	constructor(page: Page) {
		this.page = page;

		this.configurationIframe = this.page.frameLocator(
			'iframe[title*="Asset Publisher"]'
		);
	}

	async changeAssetSelection(type: 'Collection' | 'Dynamic' | 'Manual') {
		await this.configurationIframe.getByLabel(type, {exact: true}).click();

		await waitForAlert(
			this.configurationIframe,
			'Success:You have successfully updated the setup.'
		);
	}

	async createCollectionFromAssetPublisher(collectionName: string) {
		await this.configurationIframe
			.getByRole('button', {name: 'Create a collection from this'})
			.click();

		await this.configurationIframe
			.getByPlaceholder('Title')
			.fill(collectionName);

		await this.configurationIframe
			.getByLabel('Collection Title')
			.getByRole('button', {name: 'Save'})
			.click();

		await waitForAlert(
			this.configurationIframe,
			'Success:The collection was created successfully.'
		);
	}
}
