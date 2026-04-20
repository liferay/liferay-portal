/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {zipFolder} from '../../../../utils/zip';

import type {taskStatus} from '../../main/pages/ExportImportPage';

export class ExportImportPage {
	readonly completedLabel: Locator;
	readonly continueButton: Locator;
	readonly currentAndPreviousTab: Locator;
	readonly exportButton: Locator;
	readonly exportMenuItem: Locator;
	readonly fileSelector: Locator;
	readonly importButton: Locator;
	readonly importMenuItem: Locator;
	readonly nameInput: Locator;
	readonly newExportButton: Locator;
	readonly newExportTab: Locator;
	readonly newImport: Locator;
	readonly newImportTab: Locator;
	readonly page: Page;
	readonly title: Locator;
	readonly taskRow: (taskName: string) => Locator;
	readonly taskStatusLabel: (
		taskName: string,
		taskStatus?: taskStatus
	) => Locator;

	constructor(page: Page) {
		this.completedLabel = page.getByText('completed');
		this.continueButton = page.getByRole('button', {name: 'Continue'});
		this.currentAndPreviousTab = page.getByRole('link', {
			name: 'Current and Previous',
		});
		this.exportButton = page.getByRole('button', {name: 'Export'});
		this.exportMenuItem = page.getByRole('menuitem', {
			name: 'Export',
		});
		this.fileSelector = page.getByText('Select file');
		this.importButton = page.getByRole('button', {name: 'Import'});
		this.importMenuItem = page.getByRole('menuitem', {
			name: 'Import',
		});
		this.nameInput = page.getByRole('textbox', {name: 'Name'});
		this.newExportButton = page.getByRole('link', {name: 'Custom Export'});
		this.newExportTab = page.getByRole('link', {
			name: 'New Export Process',
		});
		this.newImport = page.getByRole('link', {name: 'Import'});
		this.newImportTab = page.getByRole('link', {
			name: 'New Import Process',
		});
		this.page = page;
		this.title = page.getByLabel('Export the selected data to');
		this.taskRow = (taskName) =>
			this.page.locator('[data-qa-id="row"]', {
				hasText: taskName,
			});
		this.taskStatusLabel = (taskName, taskStatus = 'success') => {
			const taskStatusTexts: Record<taskStatus, string> = {
				completedWithErrors: 'Completed with errors',
				success: 'Successful',
			};

			return this.taskRow(taskName).getByText(
				taskStatusTexts[taskStatus]
			);
		};
	}

	async export(title: string) {
		await this.title.fill(title);
		await this.exportButton.click();
	}

	async selectFile(folderPath: string) {
		const fileChooserPromise = this.page.waitForEvent('filechooser');

		await this.fileSelector.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			folderPath.endsWith('.lar')
				? await zipFolder(folderPath)
				: folderPath
		);
	}

	async import(folderPath: string, expectedUploadErrorMessage?: string) {
		await this.fileSelector.click();

		await this.selectFile(folderPath);

		if (expectedUploadErrorMessage) {
			await this.page
				.getByText(expectedUploadErrorMessage, {exact: true})
				.waitFor();

			return;
		}

		await this.continueButton.click();

		await this.importButton.waitFor();

		await this.importButton.click();

		await this.page
			.locator(
				'[data-qa-id=row]:nth-of-type(1) .background-task-status-successful'
			)
			.waitFor();
	}
}
