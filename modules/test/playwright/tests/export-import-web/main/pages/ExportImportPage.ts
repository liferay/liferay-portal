/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';
import path from 'path';

import {ProductMenuPage} from '../../../../pages/product-navigation-control-menu-web/ProductMenuPage';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {getTempDir} from '../../../../utils/temp';

type DateFilter = {
	endDate?: string;
	endTime?: string;
	rangeLast?: string;
	startDate?: string;
	startTime?: string;
};

export type taskStatus = 'success' | 'completedWithErrors';

export class ExportImportPage {
	readonly addFilterButton: Locator;
	readonly cancelButton: Locator;
	readonly clearMenuItem: Locator;
	readonly clearSearchButton: Locator;
	readonly continueButton: Locator;
	readonly deletionsLabel: Locator;
	readonly downloadButton: Locator;
	readonly exportButton: Locator;
	readonly excludeSwitch: Locator;
	readonly exportPermissionsButton: Locator;
	readonly exportReportEntriesMenuItem: Locator;
	readonly exportReportEntriesModal: Locator;
	readonly exportReportEntriesModalDownloadButton: Locator;
	readonly exportReportEntriesModalProgressbar: Locator;
	readonly fileSelector: Locator;
	readonly filterBackButton: Locator;
	readonly filterButton: Locator;
	readonly importButton: Locator;
	readonly importPermissionsCheckbox: Locator;
	readonly newExportButton: Locator;
	readonly newImportButton: Locator;
	readonly page: Page;
	readonly pagesFieldset: Locator;
	readonly portletListContainer: Locator;
	readonly productMenuPage: ProductMenuPage;
	readonly rangeDateRangeEndDate: Locator;
	readonly rangeDateRangeEndTime: Locator;
	readonly rangeDateRangeRadioButton: Locator;
	readonly rangeDateRangeStartDate: Locator;
	readonly rangeDateRangeStartTime: Locator;
	readonly rangeLast: Locator;
	readonly rangeLastRadioButton: Locator;
	readonly refreshCountsLink: Locator;
	readonly removeFilterButton: Locator;
	readonly searchButton: Locator;
	readonly searchInput: Locator;
	readonly showResultsButton: Locator;
	readonly taskActionsMenu: (taskName: string) => Locator;
	readonly taskRow: (taskName: string) => Locator;
	readonly taskStatusLabel: (
		taskName: string,
		taskStatus?: taskStatus
	) => Locator;
	readonly title: Locator;
	readonly useCurrentUserAsAuthorCheckbox: Locator;
	readonly viewReportEntriesMenuItem: Locator;

	constructor(page: Page) {
		this.addFilterButton = page.getByRole('button', {name: 'Add Filter'});
		this.cancelButton = page.getByRole('button', {name: 'Cancel'});
		this.clearMenuItem = page.getByRole('link', {name: 'Clear'});
		this.clearSearchButton = page.getByRole('button', {
			exact: true,
			name: 'Clear',
		});
		this.continueButton = page.getByRole('button', {name: 'Continue'});
		this.deletionsLabel = page
			.getByLabel('Deletions', {exact: true})
			.locator('label');
		this.downloadButton = page.getByRole('button', {name: 'Download'});
		this.exportButton = page.getByRole('button', {name: 'Export'});
		this.exportReportEntriesMenuItem = page.getByRole('menuitem', {
			name: 'Export Report Entries',
		});
		this.excludeSwitch = page.getByRole('switch', {name: 'Exclude'});
		this.exportPermissionsButton = page.getByLabel('Export Permissions');
		this.exportReportEntriesModal = page.getByRole('dialog', {
			name: 'Export Report Entries',
		});
		this.exportReportEntriesModalDownloadButton =
			this.exportReportEntriesModal.getByRole('button', {
				name: 'Download',
			});
		this.exportReportEntriesModalProgressbar =
			this.exportReportEntriesModal.getByRole('progressbar');
		this.fileSelector = page.getByRole('button', {name: 'Select File'});
		this.filterBackButton = page.getByRole('button', {name: 'Back'});
		this.filterButton = page
			.getByTestId('managementToolbar')
			.getByRole('button', {name: 'Filter'});
		this.importButton = page.getByRole('button', {name: 'Import'});
		this.importPermissionsCheckbox = page.getByLabel('Import Permissions');
		this.newExportButton = page.getByRole('link', {name: 'Custom Export'});
		this.newImportButton = page.getByRole('link', {name: 'Import'});
		this.page = page;
		this.pagesFieldset = page.locator('#pages-fieldset');
		this.portletListContainer = page
			.locator(
				'#_com_liferay_exportimport_web_portlet_ExportPortlet_selectContents .portlet-list'
			)
			.or(
				page.locator(
					'#_com_liferay_exportimport_web_portlet_CompanyExportPortlet_selectContents .portlet-list'
				)
			);
		this.productMenuPage = new ProductMenuPage(page);
		this.rangeDateRangeEndDate = page
			.locator(
				'[id="_com_liferay_exportimport_web_portlet_CompanyExportPortlet_endDate"]'
			)
			.or(
				page.locator(
					'[id="_com_liferay_exportimport_web_portlet_ExportPortlet_endDate"]'
				)
			);
		this.rangeDateRangeEndTime = page
			.locator(
				'[id="_com_liferay_exportimport_web_portlet_CompanyExportPortlet_endTime"]'
			)
			.or(
				page.locator(
					'[id="_com_liferay_exportimport_web_portlet_ExportPortlet_endTime"]'
				)
			);
		this.rangeDateRangeRadioButton = page.getByRole('radio', {
			name: 'Date Range',
		});
		this.rangeDateRangeStartDate = page
			.locator(
				'[id="_com_liferay_exportimport_web_portlet_CompanyExportPortlet_startDate"]'
			)
			.or(
				page.locator(
					'[id="_com_liferay_exportimport_web_portlet_ExportPortlet_startDate"]'
				)
			);
		this.rangeDateRangeStartTime = page
			.locator(
				'[id="_com_liferay_exportimport_web_portlet_CompanyExportPortlet_startTime"]'
			)
			.or(
				page.locator(
					'[id="_com_liferay_exportimport_web_portlet_ExportPortlet_startTime"]'
				)
			);
		this.rangeLast = page.locator(
			'[id="_com_liferay_exportimport_web_portlet_CompanyExportPortlet_last"]'
		);
		this.rangeLastRadioButton = page.getByRole('radio', {name: 'Last'});
		this.refreshCountsLink = page.getByRole('link', {
			name: 'Refresh Counts',
		});
		this.removeFilterButton = page.getByLabel('Remove Filter');
		this.searchButton = page.getByRole('button', {name: 'Search'});
		this.searchInput = page.getByRole('searchbox', {name: 'Search'});
		this.showResultsButton = page.getByRole('button', {
			name: 'Show Results',
		});
		this.taskActionsMenu = (taskName) =>
			this.taskRow(taskName).getByRole('button');
		this.taskRow = (taskName) =>
			page.locator('[data-qa-id="row"]', {
				hasText: taskName,
			});
		this.taskStatusLabel = (taskName, taskStatus = 'success') => {
			const taskStatusTexts: Record<taskStatus, string> = {
				completedWithErrors: 'Completed With Errors',
				success: 'Successful',
			};

			return this.taskRow(taskName)
				.first()
				.getByText(taskStatusTexts[taskStatus]);
		};
		this.title = page.getByPlaceholder('Enter the name of the process');
		this.useCurrentUserAsAuthorCheckbox = page.getByLabel(
			'Use the Current User as Author: Assign the current user as the author of all'
		);
		this.viewReportEntriesMenuItem = page.getByRole('menuitem', {
			name: 'View Report Entries',
		});
	}

	async checkAllPortlets() {
		const portletListContainer = this.portletListContainer;

		await portletListContainer.waitFor({state: 'attached'});

		const checkBoxes = portletListContainer.locator(
			'input[type="checkbox"]:visible'
		);

		for (const checkbox of await checkBoxes.all()) {
			await checkbox.check();
		}
	}

	async expectPortletCounts(
		label: string | RegExp,
		{
			counts = {},
			portletId,
			registrations,
		}: {
			counts?: {deletions?: number; items?: number};
			portletId?: string;
			registrations?: Array<{
				counts: {deletions?: number; items?: number};
				label: string | RegExp;
			}>;
		} = {}
	) {
		if (portletId) {
			if (registrations) {
				await this._expandPortlet(portletId);
			}

			await this._assertLabelCounts(
				this._portletLabel(portletId),
				counts
			);

			const contentLocator = this.page.locator(
				`[id$="_content_${portletId}"]`
			);

			for (const registration of registrations ?? []) {
				await this._assertLabelCounts(
					this._filterLabels(
						contentLocator.locator('label'),
						registration.label
					),
					registration.counts
				);
			}

			return;
		}

		if (registrations) {
			if (typeof label === 'string') {
				await this.page
					.locator(
						`button.content-link[data-portlettitle="${label}"]`
					)
					.click();
			}
			else {
				const buttons = this.page.locator(
					'button.content-link[data-portlettitle]'
				);

				await buttons.first().waitFor();

				for (const button of await buttons.all()) {
					const title = await button.evaluate(
						(element) => element.dataset.portlettitle
					);

					if (title && label.test(title)) {
						await button.click();
						break;
					}
				}
			}
		}

		for (const entry of [{counts, label}, ...(registrations ?? [])]) {
			await this._assertPortletEntryCounts(entry.label, entry.counts);
		}
	}

	async expectPortletAbsent(label: string | RegExp) {
		const filter =
			typeof label === 'string'
				? {has: this.page.locator(`:text-is("${label}")`)}
				: {hasText: label};

		await expect(this.page.locator('label').filter(filter)).toHaveCount(0);
	}

	async expectPortletDeletionsHidden(label: string | RegExp) {
		await this._assertPortletEntryCounts(label, {deletions: 'hidden'});
	}

	private _filterLabels(labels: Locator, label: string | RegExp): Locator {
		return labels.filter(
			typeof label === 'string'
				? {has: this.page.locator(`:text-is("${label}")`)}
				: {hasText: label}
		);
	}

	private _portletLabel(portletId: string): Locator {
		return this.page.locator('label').filter({
			has: this.page.locator(`input[name$="_PORTLET_DATA_${portletId}"]`),
		});
	}

	private async _expandPortlet(portletId: string) {
		const dataCheckbox = this.page.locator(
			`input[name$="_PORTLET_DATA_${portletId}"]`
		);

		if (!(await dataCheckbox.isChecked())) {
			await dataCheckbox.check();
		}

		await this.page
			.locator(`button.content-link[data-portletid="${portletId}"]`)
			.click();
	}

	private async _assertPortletEntryCounts(
		label: string | RegExp,
		counts: {
			deletions?: 'absent' | 'hidden' | number;
			items?: 'absent' | number;
		}
	) {
		await this._assertLabelCounts(
			this._filterLabels(this.page.locator('label'), label),
			counts
		);
	}

	private async _assertLabelCounts(
		labelLocator: Locator,
		counts: {
			deletions?: 'absent' | 'hidden' | number;
			items?: 'absent' | number;
		}
	) {
		const {deletions, items} = counts;

		if (items !== undefined) {
			const itemsLocator = labelLocator.locator(
				'.staging-taglib-checkbox-items'
			);

			if (items === 'absent') {
				await expect(itemsLocator).toHaveCount(0);
			}
			else {
				await expect(itemsLocator).toBeVisible();
				await expect(itemsLocator).toHaveText(`${items} Items`);
			}
		}

		if (deletions !== undefined) {
			const deletionsLocator = labelLocator.locator(
				'.staging-taglib-checkbox-deletions'
			);

			if (deletions === 'absent') {
				await expect(deletionsLocator).toHaveCount(0);
			}
			else if (deletions === 'hidden') {
				await expect(deletionsLocator).toBeHidden();
			}
			else {
				await expect(deletionsLocator).toBeVisible();
				await expect(deletionsLocator).toHaveText(
					`${deletions} Deletions`
				);
			}
		}
	}

	async uncheckPageSettings() {
		await this.pagesFieldset.waitFor({state: 'attached'});

		await this.pagesFieldset.evaluate((fieldset) => {
			fieldset
				.querySelectorAll<HTMLInputElement>(
					'input[type="checkbox"]:checked'
				)
				.forEach((input) => input.click());
		});
	}

	async uncheckPortlets() {
		const portletListContainer = this.portletListContainer;

		await portletListContainer.waitFor({state: 'attached'});

		const checkBoxes = portletListContainer.locator(
			'input[type="checkbox"]:visible'
		);

		for (const checkbox of await checkBoxes.all()) {
			await checkbox.uncheck();
		}
	}

	async export({
		dateFilter,
		exportAllPortlets = false,
		includePageSettings = true,
		includePermissions = false,
		portletLabels,
		taskName = `Export-${getRandomString()}`,
	}: {
		dateFilter?: DateFilter;
		exportAllPortlets?: boolean;
		includePageSettings?: boolean;
		includePermissions?: boolean;
		portletLabels?: string[];
		taskName?: string;
	} = {}): Promise<string> {
		await this.newExportButton.click();

		await this.title.fill(taskName);

		if (!includePageSettings) {
			await this.uncheckPageSettings();
		}

		if (exportAllPortlets) {
			await this.checkAllPortlets();
		}
		else if (portletLabels) {
			await this.uncheckPortlets();

			for (const portletLabel of portletLabels) {
				await this.page.getByLabel(portletLabel).check();
			}
		}

		if (includePermissions) {
			await this.exportPermissionsButton.check();
		}

		if (dateFilter?.endDate || dateFilter?.startDate) {
			await this.rangeDateRangeRadioButton.check();

			if (dateFilter.endDate) {
				await this.rangeDateRangeEndDate.fill(dateFilter.endDate);
			}

			if (dateFilter.endTime) {
				await this.rangeDateRangeEndTime.fill(dateFilter.endTime);
			}

			if (dateFilter.startDate) {
				await this.rangeDateRangeStartDate.fill(dateFilter.startDate);
			}

			if (dateFilter.startTime) {
				await this.rangeDateRangeStartTime.fill(dateFilter.startTime);
			}
		}
		else if (dateFilter?.rangeLast) {
			await this.rangeLastRadioButton.check();

			await this.rangeLast.selectOption(dateFilter.rangeLast);
		}

		await this.exportButton.click();

		await this.taskStatusLabel(taskName).waitFor();

		return await this.downloadExportProcess(taskName);
	}

	async clickTaskAction(
		taskName: string,
		action: 'Clear' | 'View Report Entries' | 'Export Report Entries'
	) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: action}),
			trigger: this.taskActionsMenu(taskName),
		});
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

	async import({
		expectedUploadErrorMessage,
		filePath,
		taskStatus = 'success',
		timeout,
	}: {
		expectedUploadErrorMessage?: string;
		filePath: string;
		taskStatus?: taskStatus;
		timeout?: number;
	}) {
		await this.selectImportFile({
			expectedUploadErrorMessage,
			filePath,
		});

		if (expectedUploadErrorMessage) {
			return;
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

		const fileName = path.basename(filePath);
		await expect(this.taskStatusLabel(fileName, taskStatus)).toBeVisible({
			timeout,
		});
	}

	async getExportableItems() {
		await this.newExportButton.click();

		const portletListContainer = this.portletListContainer;

		await portletListContainer.waitFor({state: 'attached'});

		const itemsLocator = portletListContainer.locator(
			'.custom-control-label-text:has(strong)'
		);

		const itemsMap = new Map();

		for (const itemLocator of await itemsLocator.all()) {
			const title = await itemLocator.locator('strong').textContent();
			const countLocator = itemLocator.locator(
				'.staging-taglib-checkbox-items'
			);

			if ((await countLocator.count()) === 0) {
				continue;
			}

			const countText = await countLocator.textContent();

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
		await this.clickTaskAction(exportName, 'View Report Entries');
	}

	async goToImportReportEntryDetails(externalReferenceCode: string) {
		await this.page
			.getByRole('row', {name: externalReferenceCode})
			.getByLabel('view')
			.click();

		expect(
			this.page.getByText('Report Entry Details').first()
		).toBeVisible();
	}

	async clearReportSearch() {
		await this.clearSearchButton.click();
		await this.page.waitForLoadState('networkidle');
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

	async getReportColumnValues(headerName: string): Promise<string[]> {
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

	async openExportReportEntriesModal(exportName) {
		await this.clickTaskAction(exportName, 'Export Report Entries');

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

	async sortReportBy(headerName: string) {
		await this.page
			.getByRole('columnheader', {exact: true, name: headerName})
			.getByRole('button')
			.click();
		await this.page.waitForLoadState('networkidle');
	}

	async selectImportFile({
		expectedUploadErrorMessage,
		filePath,
	}: {
		expectedUploadErrorMessage?: string;
		filePath: string;
	}): Promise<void> {
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
	}
}
