/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import POM from '../../../../utils/POM';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';

export enum TabName {
	FIELDSET = 'Fieldset',
	ICON_MENU = 'Icon Menu',
	INPUT_LOCALIZED = 'Input Localized',
	LOGO_SELECTOR = 'Logo Selector',
	SEARCH_ITERATOR = 'Search Iterator',
	SEARCH_PAGINATOR = 'Search Paginator',
}

export class SamplePage extends POM {
	readonly apiHelpers: ApiHelpers;
	readonly linkList: Locator;

	constructor(page: Page, url: string) {
		super(page, url);

		this.apiHelpers = new ApiHelpers(page);
		this.linkList = page.getByRole('link');
	}

	async selectTab(tabName: TabName) {
		const linkHeading = this.linkList.getByText(tabName);

		const target: Locator | undefined = {
			[TabName.FIELDSET]: this.page.getByRole('button', {
				name: 'Help Text Help Text',
			}),

			[TabName.ICON_MENU]: this.page.getByRole('button', {
				name: 'Sample Add',
			}),

			[TabName.INPUT_LOCALIZED]: this.page.getByText('Sample label'),

			[TabName.LOGO_SELECTOR]: this.page.getByText('First Logo'),

			[TabName.SEARCH_ITERATOR]: this.page.getByRole('cell', {
				exact: true,
				name: 'Name',
			}),

			[TabName.SEARCH_PAGINATOR]: this.page.getByLabel('Items per Page'),
		}[tabName];

		if (target === undefined) {
			throw new Error(`Unknown tab name ${tabName}`);
		}

		await clickAndExpectToBeVisible({
			autoClick: false,
			target,
			trigger: linkHeading,
		});
	}

	async waitFor() {
		await this.page
			.getByRole('link', {name: 'Fieldset'})
			.waitFor({state: 'visible'});
	}
}
