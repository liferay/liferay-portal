/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ApplicationsMenuPage} from '../../../../pages/product-navigation-applications-menu/ApplicationsMenuPage';
import {readCSVFile} from '../../../../utils/fileReader';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {getTempDir} from '../../../../utils/temp';
import {unzipFile} from '../../../../utils/zip';

export class DataMigrationCenterPage {
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly page: Page;
	readonly newButton: Locator;
	readonly entityTypeSelector: Locator;
	readonly importFileMenuItem: Locator;
	readonly exportFileMenuItem: Locator;
	readonly exportFileFormatSelector: Locator;
	readonly exportButton: Locator;
	readonly downloadButton: Locator;
	readonly downloadSampleButton: Locator;
	readonly attributeCodeCheckBox: Locator;
	readonly importStrategySelector: Locator;
	readonly fileSelector: Locator;
	readonly nextButton: Locator;
	readonly scopeSelector: Locator;
	readonly startImportButton: () => Promise<Locator>;
	readonly updateStrategySelector: Locator;

	constructor(page: Page) {
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.page = page;
		this.newButton = page.getByRole('button', {name: 'New'});
		this.entityTypeSelector = page.getByLabel('Entity Type');
		this.exportFileFormatSelector = page.getByLabel('Export File Format');
		this.importFileMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Import File',
		});
		this.exportFileMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Export File',
		});
		this.exportButton = page.getByRole('button', {name: 'Export'});
		this.downloadButton = page.getByRole('button', {name: 'Download'});
		this.downloadSampleButton = page.getByRole('link', {name: 'Download'});
		this.attributeCodeCheckBox = page
			.getByRole('row', {name: 'Attribute Code'})
			.getByLabel('');
		this.importStrategySelector = page.getByLabel('Import Strategy');
		this.fileSelector = page.locator(
			'#_com_liferay_batch_planner_web_internal_portlet_BatchPlannerPortlet_importFile'
		);
		this.nextButton = page.getByRole('button', {name: 'Next'});
		this.scopeSelector = page.getByLabel('Scope', {exact: true});
		this.updateStrategySelector = page.getByLabel('Update Strategy');
		this.startImportButton = async (): Promise<Locator> => {
			await this.page.waitForSelector(
				'button[data-testid="start-import"]'
			);
			const button = this.page.locator(
				'button[data-testid="start-import"]'
			);

			return button;
		};
	}

	async gotoPage(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.batchExportImport}`
		);
	}

	async goto() {
		await this.applicationsMenuPage.goToDataMigrationCenter();
	}

	async goToImportFile() {
		await this.newButton.click();
		await this.importFileMenuItem.click();
	}

	async goToExportFile() {
		await this.newButton.click();
		await this.exportFileMenuItem.click();
	}

	async downloadSampleFile(entitType: string) {
		await this.selectEntityType(entitType);

		const downloadPromise = this.page.waitForEvent('download');
		await this.downloadSampleButton.click();
		const download = await downloadPromise;
		const filePath = getTempDir() + download.suggestedFilename();
		await download.saveAs(filePath);

		return readCSVFile(filePath);
	}

	async importFile(
		entitType: string,
		filePath: string,
		importStrategy: string,
		updateStrategy: string
	) {
		await this.selectFile(filePath);
		await this.selectEntityType(entitType);
		await this.importStrategySelector.selectOption(importStrategy);
		await this.updateStrategySelector.selectOption(updateStrategy);

		if ((await this.scopeSelector.all()).length) {
			this.scopeSelector.selectOption(
				await this.page
					.locator('option', {hasText: /^Liferay( DXP)?$/})
					.textContent()
			);
		}

		await this.nextButton.click();
		(await this.startImportButton()).click();
	}

	async selectEntityType(entityTypeName: string) {
		await this.entityTypeSelector.selectOption(entityTypeName);
	}

	async selectExportFileFormat(exportFileFormatName: string) {
		await this.exportFileFormatSelector.selectOption(exportFileFormatName);
	}

	async selectFile(filePath: string) {
		const fileChooserPromise = this.page.waitForEvent('filechooser');
		await this.fileSelector.click();
		const fileChooser = await fileChooserPromise;
		await fileChooser.setFiles(filePath);
	}

	async exportFile(
		exportFileFormat: string,
		entitType: string,
		checkedFields: Array<String> = null
	) {
		await this.goto();
		await this.goToExportFile();

		await this.selectExportFileFormat(exportFileFormat);
		await this.selectEntityType(entitType);

		if (checkedFields !== null) {
			await this.attributeCodeCheckBox.click();

			for (const fieldName of checkedFields) {
				await this.page.getByLabel(`Select ${fieldName}`).click();
			}
		}

		await this.exportButton.click();

		const downloadPromise = this.page.waitForEvent('download');
		await this.downloadButton.click();
		const download = await downloadPromise;
		const filePath = getTempDir() + download.suggestedFilename();
		await download.saveAs(filePath);

		return unzipFile(filePath);
	}
}
