/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';
import * as fs from 'fs';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {getTempDir} from '../../../../utils/temp';
import {zipFolder} from '../../../../utils/zip';

export type taskStatus = 'completedWithErrors' | 'success';

export class ExportImportPage {
	readonly actionsButton: (name: string) => Locator;
	readonly addFilterButton: Locator;
	readonly clearMenuItem: Locator;
	readonly clearSearchButton: Locator;
	readonly completedLabel: Locator;
	readonly continueButton: Locator;
	readonly downloadMenuItem: Locator;
	readonly excludeSwitch: Locator;
	readonly exportButton: Locator;
	readonly exportIndividualDeletionsCheckbox: Locator;
	readonly exportMenuItem: Locator;
	readonly exportReportEntriesMenuItem: Locator;
	readonly exportReportEntriesModal: Locator;
	readonly exportReportEntriesModalDownloadButton: Locator;
	readonly exportReportEntriesModalProgressbar: Locator;
	readonly fileSelector: Locator;
	readonly filterBackButton: Locator;
	readonly filterButton: Locator;
	readonly filterContentBySelect: Locator;
	readonly fromDateInput: Locator;
	readonly importButton: Locator;
	readonly importMenuItem: Locator;
	readonly nameInput: Locator;
	readonly newButton: Locator;
	readonly page: Page;
	readonly relaunchMenuItem: Locator;
	readonly removeFilterButton: Locator;
	readonly replicateSelectedDeletionsCheckbox: Locator;
	readonly searchButton: Locator;
	readonly searchInput: Locator;
	readonly showResultsButton: Locator;
	readonly taskStatusLabel: (
		taskName: string,
		taskStatus?: taskStatus
	) => Locator;
	readonly toDateInput: Locator;
	readonly viewReportEntriesMenuItem: Locator;

	constructor(page: Page) {
		this.actionsButton = (name) =>
			page.getByRole('button', {name: `${name} Actions`});
		this.addFilterButton = page.getByRole('button', {name: 'Add Filter'});
		this.clearMenuItem = page.getByRole('menuitem', {name: 'Clear'});
		this.clearSearchButton = page.getByRole('button', {
			exact: true,
			name: 'Clear',
		});
		this.completedLabel = page.getByText('completed');
		this.continueButton = page.getByRole('button', {name: 'Continue'});
		this.downloadMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Download',
		});
		this.excludeSwitch = page.getByRole('switch', {name: 'Exclude'});
		this.exportButton = page.getByRole('button', {name: 'Export'});
		this.exportIndividualDeletionsCheckbox = page.getByRole('checkbox', {
			name: 'Export Individual Deletions',
		});
		this.exportMenuItem = page.getByRole('menuitem', {
			name: 'Export',
		});
		this.exportReportEntriesMenuItem = page.getByRole('menuitem', {
			name: 'Export Report Entries',
		});
		this.exportReportEntriesModal = page.getByRole('dialog', {
			name: 'Export Report Entries',
		});
		this.exportReportEntriesModalDownloadButton =
			this.exportReportEntriesModal.getByRole('button', {
				name: 'Download',
			});
		this.exportReportEntriesModalProgressbar =
			this.exportReportEntriesModal.getByRole('progressbar');
		this.fileSelector = page.getByText('Select Files');
		this.filterBackButton = page.getByRole('button', {name: 'Back'});
		this.filterButton = page
			.getByTestId('managementToolbar')
			.getByRole('button', {name: 'Filter'});
		this.filterContentBySelect = page.getByLabel('Filter Content By');
		this.fromDateInput = page.getByLabel('From', {exact: true});
		this.importButton = page.getByRole('button', {name: 'Import'});
		this.importMenuItem = page.getByRole('menuitem', {
			name: 'Import',
		});
		this.nameInput = page.getByRole('textbox', {name: 'Name'});
		this.newButton = page
			.getByRole('button', {exact: true, name: 'New'})
			.first();
		this.page = page;
		this.relaunchMenuItem = page.getByRole('menuitem', {name: 'Relaunch'});
		this.removeFilterButton = page.getByLabel('Remove Filter');
		this.replicateSelectedDeletionsCheckbox = page.getByRole('checkbox', {
			name: 'Replicate Selected Deletions',
		});
		this.searchButton = page.getByRole('button', {name: 'Search'});
		this.searchInput = page.getByRole('searchbox', {name: 'Search'});
		this.showResultsButton = page.getByRole('button', {
			name: 'Show Results',
		});
		this.taskStatusLabel = (taskName, taskStatus = 'success') => {
			const taskStatusTexts: Record<taskStatus, string> = {
				completedWithErrors: 'Completed With Errors',
				success: 'Successful',
			};

			return this.page
				.locator('tr', {hasText: taskName})
				.locator('.cell-status')
				.getByText(taskStatusTexts[taskStatus], {exact: true});
		};
		this.toDateInput = page.getByLabel('To', {exact: true});
		this.viewReportEntriesMenuItem = page.getByRole('menuitem', {
			name: 'View Report Entries',
		});
	}

	async clearReportSearch() {
		await this.clearSearchButton.click();
		await this.page.waitForLoadState('networkidle');
	}

	async clickNew() {
		await clickAndExpectToBeVisible({
			target: this.nameInput,
			trigger: this.newButton,
		});
	}

	async download(name: string): Promise<string> {
		const downloadPromise = this.page.waitForEvent('download');

		await clickAndExpectToBeVisible({
			target: this.downloadMenuItem,
			trigger: this.actionsButton(name),
		});

		await this.downloadMenuItem.click();

		const download = await downloadPromise;

		const filePath = getTempDir() + download.suggestedFilename();

		await download.saveAs(filePath);

		return filePath;
	}

	async excludeReportFilter() {
		await this.filterButton.click();
		await this.excludeSwitch.check();
		await this.showResultsButton.click();

		const responsePromise = this.page.waitForResponse(
			(response) =>
				response.url().includes('report-entries') &&
				response.status() === 200
		);

		await responsePromise;
	}

	async expectUploadError(folderPath: string, message: string) {
		await this.selectFile(folderPath);

		await expect(this.page.getByText(message)).toBeVisible();
	}

	async export(name: string) {
		await this.clickNew();

		await this.nameInput.fill(name);

		await this.exportButton.click();
	}

	async filterByDateRange(fromDate: string, toDate: string) {
		await this.filterContentBySelect.selectOption('dateRange');

		await this.fromDateInput.fill(fromDate);

		await this.toDateInput.fill(toDate);

		await this.showResultsButton.click();
	}

	async filterByModifiedLast() {
		await this.filterContentBySelect.selectOption('last');

		await this.showResultsButton.click();
	}

	async filterReportBy(category: string, value: string) {
		await this.filterButton.click();

		if (await this.filterBackButton.isVisible()) {
			await this.filterBackButton.click();
		}

		await this.page
			.getByRole('menuitem', {exact: true, name: category})
			.click();

		await this.page.getByRole('checkbox', {name: value}).check();

		await this.addFilterButton.click();
		await this.page.waitForLoadState('networkidle');
	}

	async getColumnValues(headerName: string): Promise<string[]> {
		const header = this.page.getByRole('columnheader', {
			exact: true,
			name: headerName,
		});

		const index = await header.evaluate((node) => {
			return (
				Array.from(
					(node as HTMLElement).parentElement!.children
				).indexOf(node as HTMLElement) + 1
			);
		});

		return this.page
			.locator(`tbody tr td:nth-child(${index})`)
			.allTextContents();
	}

	async goToExport(siteFriendlyUrlPath: string) {
		await this.page.goto(
			`/group${siteFriendlyUrlPath}${PORTLET_URLS.export}`
		);
	}

	async goToImport(siteFriendlyUrlPath: string) {
		await this.page.goto(
			`/group${siteFriendlyUrlPath}${PORTLET_URLS.import}`
		);
	}

	async goToImportDetails(name: string) {
		await clickAndExpectToBeVisible({
			target: this.viewReportEntriesMenuItem,
			trigger: this.actionsButton(name),
		});

		await this.viewReportEntriesMenuItem.click();
	}

	async import({
		folderPath,
		includeDeletions = false,
		name,
		taskStatus = 'success',
	}: {
		folderPath: string;
		includeDeletions?: boolean;
		name: string;
		taskStatus?: taskStatus;
	}) {
		await this.nameInput.fill(name);

		await this.selectFile(folderPath);

		await this.completedLabel.waitFor();

		await this.continueButton.click();

		if (includeDeletions) {
			await this.replicateSelectedDeletionsCheckbox.check();
		}

		await this.continueButton.click();

		await this.importButton.waitFor();

		await this.importButton.click();

		await this.taskStatusLabel(name, taskStatus).waitFor();
	}

	async openExportReportEntriesModal(name: string) {
		await clickAndExpectToBeVisible({
			target: this.exportReportEntriesMenuItem,
			trigger: this.actionsButton(name),
		});

		await this.exportReportEntriesMenuItem.click();

		await this.exportReportEntriesModal.waitFor();
	}

	async removeReportFilter() {
		await this.removeFilterButton.click();
		await this.page.waitForLoadState('networkidle');
	}

	async searchReportEntries(searchTerm: string) {
		await this.searchInput.fill(searchTerm);
		await this.searchButton.click();

		await expect(
			this.page.getByRole('button', {name: 'Clear Search'})
		).toBeVisible({timeout: 2000});

		await this.page.waitForLoadState('networkidle');
	}

	async selectFile(folderPath: string) {
		const fileChooserPromise = this.page.waitForEvent('filechooser');

		await this.fileSelector.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			fs.statSync(folderPath).isDirectory()
				? await zipFolder(folderPath)
				: folderPath
		);
	}

	async sortBy(headerName: string) {
		await this.page
			.getByRole('columnheader', {exact: true, name: headerName})
			.getByRole('button')
			.click();

		await this.page.waitForLoadState('networkidle');
	}
}
