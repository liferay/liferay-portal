/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';
import path from 'path';

import {ApplicationsMenuPage} from '../../../../pages/product-navigation-applications-menu/ApplicationsMenuPage';
import {DateOptions} from '../types/dateOptions';
import {ExportImportPage} from './ExportImportPage';

export class CompanyExportImportPage {
	readonly page: Page;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly deletionsLabel: Locator;
	readonly exportImportPage: ExportImportPage;
	readonly rangeDateRangeEndDate: Locator;
	readonly rangeDateRangeEndTime: Locator;
	readonly rangeDateRangeRadioButton: Locator;
	readonly rangeDateRangeStartDate: Locator;
	readonly rangeDateRangeStartTime: Locator;
	readonly rangeLast: Locator;
	readonly rangeLastRadioButton: Locator;

	constructor(page: Page) {
		this.page = page;
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.deletionsLabel = page
			.getByLabel('Deletions', {exact: true})
			.locator('label');
		this.exportImportPage = new ExportImportPage(page);
		this.rangeDateRangeEndDate = page.locator(
			'[id="_com_liferay_exportimport_web_portlet_CompanyExportPortlet_endDate"]'
		);
		this.rangeDateRangeEndTime = page.locator(
			'[id="_com_liferay_exportimport_web_portlet_CompanyExportPortlet_endTime"]'
		);
		this.rangeDateRangeRadioButton = page.getByRole('radio', {
			name: 'Date Range',
		});
		this.rangeDateRangeStartDate = page.locator(
			'[id="_com_liferay_exportimport_web_portlet_CompanyExportPortlet_startDate"]'
		);
		this.rangeDateRangeStartTime = page.locator(
			'[id="_com_liferay_exportimport_web_portlet_CompanyExportPortlet_startTime"]'
		);
		this.rangeLast = page.locator(
			'[id="_com_liferay_exportimport_web_portlet_CompanyExportPortlet_last"]'
		);
		this.rangeLastRadioButton = page.getByRole('radio', {name: 'Last'});
	}

	async export(
		itemLabel: string[],
		includePermissions: boolean = false,
		dateOptions?: DateOptions,
		taskName?: string
	): Promise<string> {
		await this.applicationsMenuPage.goToExport();

		await this.page.getByTestId('creationMenuNewButton').nth(1).click();

		for (const label of itemLabel) {
			await this.page.getByLabel(label, {exact: true}).click();
		}

		taskName
			? await this.exportImportPage.title.fill(taskName)
			: (taskName = 'Export');

		if (includePermissions) {
			await this.exportImportPage.exportPermissionsButton.click();
		}

		if (dateOptions?.endDate || dateOptions?.startDate) {
			await this.rangeDateRangeRadioButton.check();

			if (dateOptions.endDate) {
				await this.rangeDateRangeEndDate.fill(dateOptions.endDate);
			}

			if (dateOptions.endTime) {
				await this.rangeDateRangeEndTime.fill(dateOptions.endTime);
			}

			if (dateOptions.startDate) {
				await this.rangeDateRangeStartDate.fill(dateOptions.startDate);
			}

			if (dateOptions.startTime) {
				await this.rangeDateRangeStartTime.fill(dateOptions.startTime);
			}
		}
		else if (dateOptions?.rangeLast) {
			await this.rangeLastRadioButton.check();

			await this.rangeLast.selectOption(dateOptions.rangeLast);
		}

		await this.exportImportPage.exportButton.click();

		await this.page
			.locator('//h2[span[normalize-space()="' + taskName + '"]]')
			.first()
			.locator('../..')
			.getByText('Successful')
			.waitFor();

		return await this.exportImportPage.downloadExportProcess(taskName);
	}

	async goToImportOptions(filePath: string) {
		await this.applicationsMenuPage.goToImport();

		await this.exportImportPage.newImportButton.click();

		await this.page.locator('input[type="file"]').setInputFiles(filePath);

		await this.exportImportPage.continueButton.click();
	}

	async import(
		filePath: string,
		includePermissions: boolean = false,
		expectedUploadErrorMessage?: string,
		useCurrentUser: boolean = false
	): Promise<void> {
		await this.applicationsMenuPage.goToImport();

		await this.exportImportPage.newImportButton.click();

		await this.page.locator('input[type="file"]').setInputFiles(filePath);

		if (expectedUploadErrorMessage) {
			await expect(
				this.page.getByText(expectedUploadErrorMessage)
			).toBeVisible();

			return;
		}

		await this.exportImportPage.continueButton.click();

		if (includePermissions) {
			await this.exportImportPage.importPermissionsButton.click();
		}

		if (useCurrentUser) {
			if (
				!(await this.exportImportPage.useCurrentUserAsAuthorCheckbox.isVisible())
			) {
				await this.page
					.getByRole('button', {name: 'Authorship of the Content'})
					.click();

				await this.exportImportPage.useCurrentUserAsAuthorCheckbox.waitFor(
					{state: 'visible'}
				);
			}

			await this.exportImportPage.useCurrentUserAsAuthorCheckbox.check();
		}

		await this.exportImportPage.importButton.click();

		const fileName = path.basename(filePath);
		await this.page
			.getByText(fileName)
			.locator('../../../..')
			.getByText('Successful')
			.waitFor();
	}
}
