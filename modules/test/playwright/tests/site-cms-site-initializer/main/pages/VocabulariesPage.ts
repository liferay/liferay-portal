/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {DataSetPage} from './DataSetPage';

export class VocabulariesPage {
	readonly page: Page;
	readonly dataSetFragmentPage: DataSetPage;

	constructor(page: Page) {
		this.page = page;
		this.dataSetFragmentPage = new DataSetPage(page);
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.cmsVocabularies);
		await this.page.getByRole('heading', {name: 'Vocabularies'}).waitFor();
	}

	getItem(filter: string) {
		return this.dataSetFragmentPage.getRow(filter);
	}

	async deleteVocabulary(name: string) {
		await this.execItemAction({
			action: 'Delete',
			filter: name,
		});

		await expect(this.page.getByRole('heading', {name})).toBeVisible();

		await clickAndExpectToBeVisible({
			target: this.page.getByText(
				'Success:Your request completed successfully.'
			),
			trigger: this.page.getByRole('button', {name: 'Delete'}),
		});

		await expect(this.getItem(name)).not.toBeVisible();
	}

	async execItemAction({action, filter}: {action: string; filter: string}) {
		await this.dataSetFragmentPage.execItemAction({
			action,
			filter,
		});
	}
}
