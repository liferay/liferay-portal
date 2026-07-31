/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {DataSetPage} from '../../../site-cms-site-initializer/main/pages/DataSetPage';

export class ConnectorsPage {
	readonly dataSetFragmentPage: DataSetPage;
	readonly emptyStateTitle: Locator;
	readonly newConnectorButton: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.dataSetFragmentPage = new DataSetPage(page);
		this.emptyStateTitle = page.getByText('No Connectors Yet', {
			exact: true,
		});
		this.newConnectorButton = page.getByTestId('fdsCreationActionButton');
		this.page = page;
	}

	async deleteConnector(name: string) {
		this.page.once('dialog', (dialog) => dialog.accept());

		await this.dataSetFragmentPage.execItemAction({
			action: 'Delete',
			filter: name,
		});

		await this.getConnector(name).waitFor({state: 'hidden'});
	}

	getConnector(name: string) {
		return this.dataSetFragmentPage.getRow(name).getByRole('link', {name});
	}

	async goto() {
		await this.page.goto(PORTLET_URLS.pimConnectors);

		await this.newConnectorButton.waitFor({state: 'visible'});
	}
}
