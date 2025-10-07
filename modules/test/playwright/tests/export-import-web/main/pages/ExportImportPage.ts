/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {ProductMenuPage} from '../../../../pages/product-navigation-control-menu-web/ProductMenuPage';
import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {getTempDir} from '../../../../utils/temp';

export class ExportImportPage {
	readonly cancelButton: Locator;
	readonly continueButton: Locator;
	readonly copyAsNewRadioButton: Locator;
	readonly deleteApplicationDataAlert: Locator;
	readonly deleteApplicationDataCheckbox: Locator;
	readonly deleteApplicationDataBeforeImportingWarningLabel: Locator;
	readonly deletionsLabel: Locator;
	readonly downloadButton: Locator;
	readonly exportButton: Locator;
	readonly exportPermissionsButton: Locator;
	readonly fileSelector: Locator;
	readonly importButton: Locator;
	readonly importModalButton: Locator;
	readonly importPermissionsButton: Locator;
	readonly mirrorWithOverwritingRadioButton: Locator;
	readonly newExportButton: Locator;
	readonly newImportButton: Locator;
	readonly page: Page;
	readonly productMenuPage: ProductMenuPage;
	readonly taskMenu: (taskName: string) => Locator;
	readonly taskSuccessLabel: (taskName: string) => Locator;
	readonly title: Locator;
	readonly updateDataAlert: Locator;
	readonly updateDataMirrorWarningLabel: Locator;
	readonly useCurrentUserAsAuthorCheckbox: Locator;
	readonly viewDetails: Locator;
	readonly warningHeader: Locator;

	constructor(page: Page) {
		this.cancelButton = page.getByRole('button', {name: 'Cancel'});
		this.continueButton = page.getByRole('button', {name: 'Continue'});
		this.copyAsNewRadioButton = page.getByLabel('Copy as new');
		this.deleteApplicationDataAlert = page.locator('[role="alert"]', {
			hasText: 'This option does not apply to object entries.',
		});
		this.deleteApplicationDataCheckbox = page.getByLabel(
			'Delete Application Data'
		);
		this.deleteApplicationDataBeforeImportingWarningLabel = page
			.getByLabel('Important Info About Your Import')
			.getByText(
				'Delete Application Data Before Importing: This option does not apply to object'
			);
		this.deletionsLabel = page
			.getByLabel('Deletions', {exact: true})
			.locator('label');
		this.downloadButton = page.getByRole('button', {name: 'Download'});
		this.exportButton = page.getByRole('button', {name: 'Export'});
		this.exportPermissionsButton = page.getByLabel('Export Permissions');
		this.fileSelector = page.getByRole('button', {name: 'Select File'});
		this.importButton = page.getByRole('button', {name: 'Import'});
		this.importModalButton = page
			.getByLabel('Important Info About Your Import')
			.getByRole('button', {name: 'Import'});
		this.importPermissionsButton = page.getByLabel('Import Permissions');
		this.mirrorWithOverwritingRadioButton = page.getByLabel(
			'Mirror with overwriting'
		);
		this.newExportButton = page.getByRole('link', {name: 'Custom Export'});
		this.newImportButton = page.getByRole('link', {name: 'Import'});
		this.page = page;
		this.productMenuPage = new ProductMenuPage(page);
		this.taskMenu = (taskName: string) =>
			this.page
				.locator('[data-qa-id="row"]', {
					hasText: taskName,
				})
				.getByRole('button');
		this.taskSuccessLabel = (taskName: string) =>
			this.page
				.locator('[data-qa-id="row"]', {
					hasText: taskName,
				})
				.getByText('Successful');
		this.title = page.getByPlaceholder('Enter the name of the process');
		this.updateDataAlert = page.locator('[role="alert"]', {
			hasText:
				'Objects entries are always mirrored regardless of the selection.',
		});
		this.updateDataMirrorWarningLabel = page
			.getByLabel('Important Info About Your Import')
			.getByText(
				'Update Data (Mirror): Objects entries are always mirrored regardless of the selection.'
			);
		this.useCurrentUserAsAuthorCheckbox = page.getByLabel(
			'Use the Current User as Author: Assign the current user as the author of all'
		);
		this.viewDetails = page.getByRole('menuitem', {name: 'View Details'});
		this.warningHeader = page.getByRole('heading', {
			name: 'Important Info About Your Import',
		});
	}

	async export(title: string, itemLabel?: string) {
		await this.newExportButton.click();

		await this.title.fill(title);

		if (itemLabel) {
			await this.page.getByLabel(itemLabel, {exact: true}).click();
		}

		await this.exportButton.click();
	}

	async exportAll(title: string, itemLabel?: string) {
		await this.newExportButton.click();

		await this.title.fill(title);

		if (itemLabel) {
			await this.page.getByLabel(itemLabel, {exact: true}).click();
		}

		const portletListContainer = this.page.locator(
			'#_com_liferay_exportimport_web_portlet_ExportPortlet_selectContents .portlet-list'
		);

		await portletListContainer.waitFor();

		const checkBoxes = portletListContainer.locator(
			'input[type="checkbox"]'
		);

		for (const checkbox of await checkBoxes.all()) {
			await checkbox.check();
		}

		await this.exportButton.click();
	}

	async checkItemInNewlyCreatedImportProcess(
		folderPath: string,
		itemToCheck: string
	) {
		await this.newImportButton.click();

		const fileChooserPromise = this.page.waitForEvent('filechooser');

		await this.fileSelector.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(folderPath);

		await this.continueButton.click();

		await this.page.waitForLoadState('domcontentloaded');
		await this.page.waitForTimeout(1000);

		const wikiLabelCount = await this.page.getByLabel(itemToCheck).count();
		expect(wikiLabelCount).toBe(0);
	}

	async import(filePath: string, expectedUploadErrorMessage?: string) {
		await this.newImportButton.click();

		const fileChooserPromise = this.page.waitForEvent('filechooser');

		await this.fileSelector.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(filePath);

		if (expectedUploadErrorMessage) {
			await expect(
				this.page.getByText(expectedUploadErrorMessage)
			).toBeVisible();

			return;
		}

		await this.continueButton.click();

		await this.page.waitForLoadState('domcontentloaded');
		await this.page.waitForTimeout(1000);

		await this.page
			.locator(
				'[id="_com_liferay_exportimport_web_portlet_ImportPortlet_contentLink_com_liferay_layout_admin_web_portlet_GroupPagesPortlet"]'
			)
			.click();

		const utilityPages = this.page
			.locator('#PagesContent')
			.getByText('Utility Pages');

		if (await utilityPages.isVisible()) {
			await utilityPages.click();
		}

		await this.page
			.locator(
				'[id="_com_liferay_exportimport_web_portlet_ImportPortlet_contentOptionsLink"]'
			)
			.click();

		await this.page
			.locator(
				'[id="_com_liferay_exportimport_web_portlet_ImportPortlet_contentOptions"]'
			)
			.getByText('Comments')
			.click();

		await this.page
			.locator(
				'[id="_com_liferay_exportimport_web_portlet_ImportPortlet_contentOptions"]'
			)
			.getByText('Ratings')
			.click();

		await this.importButton.click();
	}

	async getExportableItems() {
		await this.newExportButton.click();

		const portletListContainer = this.page.locator(
			'#_com_liferay_exportimport_web_portlet_ExportPortlet_selectContents .portlet-list'
		);

		await portletListContainer.waitFor();

		const itemsLocator = portletListContainer.locator(
			'.custom-control-label-text:has(strong)'
		);

		const itemsMap = new Map();

		for (const itemLocator of await itemsLocator.all()) {
			const title = await itemLocator.locator('strong').textContent();
			const countText = await itemLocator
				.locator('.staging-taglib-checkbox-items')
				.textContent();

			const countMatch = countText ? countText.match(/\d+/) : null;

			if (title && countMatch) {
				const countAsNumber = parseInt(countMatch[0], 10);

				itemsMap.set(title.trim(), countAsNumber);
			}
		}

		await this.cancelButton.click();

		return itemsMap;
	}

	async downloadExportProcess(name: string) {
		const downloadPromise = this.page.waitForEvent('download');

		await this.page
			.locator('//h2[span[normalize-space()="' + name + '"]]/span/a')
			.first()
			.click();

		const download = await downloadPromise;
		const filePath = getTempDir() + download.suggestedFilename();

		await download.saveAs(filePath);

		return filePath;
	}

	async goToExport(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.export}`
		);
	}

	async goToImport(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.import}`
		);
	}

	async goToImportDetails(exportName: string) {
		await expect(this.taskSuccessLabel(exportName)).toBeVisible();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.viewDetails,
			trigger: this.taskMenu(exportName),
		});
	}

	async goToImportOptions(
		folderPath: string,
		siteUrl?: Site['friendlyUrlPath']
	) {
		await this.goToImport(siteUrl);
		await this.newImportButton.click();
		await this.page.getByRole('button', {name: 'Select File'}).waitFor();

		const previousFileAlert = this.page.getByText(
			'Warning:This file was previously uploaded'
		);
		if (await previousFileAlert.isVisible()) {
			await clickAndExpectToBeHidden({
				target: previousFileAlert,
				trigger: this.page.getByRole('link', {
					name: 'Delete File',
				}),
			});
		}

		const fileChooserPromise = this.page.waitForEvent('filechooser');
		await this.fileSelector.click();
		const fileChooser = await fileChooserPromise;
		await fileChooser.setFiles(folderPath);

		await this.continueButton.click();
		this.page.getByText('File Summary');
	}

	async goToImportErrorDetails(externalReferenceCode: string) {
		await this.page
			.getByRole('row', {name: externalReferenceCode})
			.getByLabel('view')
			.click();

		expect(this.page.getByText('Error Details').first()).toBeVisible();
	}
}
