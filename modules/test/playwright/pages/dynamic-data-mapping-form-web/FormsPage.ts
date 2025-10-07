/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {PORTLET_URLS} from '../../utils/portletUrls';
import {zipFolder} from '../../utils/zip';

export class FormsPage {
	readonly closeButton: Locator;
	readonly continueButton: Locator;
	readonly dataProvidersTab: Locator;
	readonly emptyResultNewFormButton: Locator;
	readonly exportImportIframe: FrameLocator;
	readonly formsHeader: Locator;
	readonly formsTab: Locator;
	readonly exportImportOption: Locator;
	readonly formsOptionsKebab: Locator;
	readonly importButton: Locator;
	readonly importTab: Locator;
	readonly managementToolbarDeleteButton: Locator;
	readonly managementToolbarNewButton: Locator;
	readonly managementToolbarSearchForButton: Locator;
	readonly managementToolbarSelectAllItems: Locator;
	readonly newFormButton: Locator;
	readonly page: Page;
	readonly selectFileButton: Locator;

	constructor(page: Page) {
		this.exportImportIframe = page.frameLocator(
			'iframe[title="Export \\/ Import"]'
		);
		this.closeButton = page.getByLabel('Close', {exact: true});
		this.continueButton = this.exportImportIframe.getByRole('button', {
			name: 'Continue',
		});
		this.dataProvidersTab = page.getByRole('link', {
			name: 'Data Providers',
		});
		this.emptyResultNewFormButton = page.getByText('New Form', {
			exact: true,
		});
		this.exportImportOption = page.getByRole('menuitem', {
			name: 'Export / Import',
		});
		this.formsHeader = page.getByRole('heading', {
			exact: true,
			name: 'Forms',
		});
		this.formsOptionsKebab = page.getByLabel('Options');
		this.formsTab = page.getByRole('link', {name: 'Forms'});
		this.importButton = this.exportImportIframe.getByRole('button', {
			name: 'Import',
		});
		this.importTab = this.exportImportIframe.getByRole('link', {
			name: 'Import',
		});
		this.managementToolbarDeleteButton = page.getByRole('button', {
			name: 'Delete',
		});
		this.managementToolbarNewButton = page.getByText('New', {exact: true});
		this.managementToolbarSearchForButton = page.getByRole('button', {
			name: 'Search for',
		});
		this.managementToolbarSelectAllItems = page.getByLabel(
			'Select All Items on the Page'
		);
		this.newFormButton = page.getByRole('link', {name: 'New Form'});
		this.page = page;
		this.selectFileButton = this.exportImportIframe.getByRole('button', {
			name: 'Select File',
		});
	}

	async clearImportHistory() {
		await this.exportImportIframe
			.getByRole('button', {name: 'Actions'})
			.click();

		this.page.once('dialog', async (dialog) => {
			await dialog.accept();
		});

		await this.exportImportIframe
			.getByRole('link', {name: 'Clear'})
			.click();
	}

	async clickEmptyResultNewFormButton() {
		await this.emptyResultNewFormButton.click();
	}

	async clickFormTitle(formTitle: string) {
		await this.page.getByText(formTitle).click();
	}

	async clickManagementToolbarNewButton() {
		await this.managementToolbarSearchForButton.isEnabled();
		await this.managementToolbarNewButton.click();
	}

	async goTo(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.forms}`,
			{waitUntil: 'networkidle'}
		);
	}

	async importForm(folderPath: string) {
		await this.openExportImporFormModal();

		await this.importTab.click();

		await this.selectFile(folderPath);

		await this.continueButton.click();

		await this.importButton.click();

		await expect(
			this.exportImportIframe.locator('td.lfr-status-column')
		).toHaveText('Successful');

		await this.clearImportHistory();

		await this.closeButton.click();
	}

	async openExportImporFormModal() {
		await this.formsOptionsKebab.click();

		await this.exportImportOption.click();
	}

	async openForm(formLabel: string) {
		await this.page
			.getByRole('link', {exact: true, name: formLabel})
			.click();
	}

	async selectFile(folderPath: string) {
		const fileChooserPromise = this.page.waitForEvent('filechooser');

		await this.selectFileButton.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(await zipFolder(folderPath));

		await expect(
			this.exportImportIframe.locator('.upload-file')
		).toHaveClass(/upload-complete/);
	}
}
