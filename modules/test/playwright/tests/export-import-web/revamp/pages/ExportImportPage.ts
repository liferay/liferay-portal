/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {getTempDir} from '../../../../utils/temp';
import {zipFolder} from '../../../../utils/zip';

export type taskStatus = 'completedWithErrors' | 'success';

export class ExportImportPage {
	readonly actionsButton: (name: string) => Locator;
	readonly completedLabel: Locator;
	readonly continueButton: Locator;
	readonly downloadMenuItem: Locator;
	readonly exportButton: Locator;
	readonly exportMenuItem: Locator;
	readonly fileSelector: Locator;
	readonly importButton: Locator;
	readonly importMenuItem: Locator;
	readonly nameInput: Locator;
	readonly newButton: Locator;
	readonly page: Page;
	readonly taskStatusLabel: (
		taskName: string,
		taskStatus?: taskStatus
	) => Locator;

	constructor(page: Page) {
		this.actionsButton = (name) =>
			page.getByRole('button', {name: `${name} Actions`});
		this.completedLabel = page.getByText('completed');
		this.continueButton = page.getByRole('button', {name: 'Continue'});
		this.downloadMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Download',
		});
		this.exportButton = page.getByRole('button', {name: 'Export'});
		this.exportMenuItem = page.getByRole('menuitem', {
			name: 'Export',
		});
		this.fileSelector = page.getByText('Select Files');
		this.importButton = page.getByRole('button', {name: 'Import'});
		this.importMenuItem = page.getByRole('menuitem', {
			name: 'Import',
		});
		this.nameInput = page.getByRole('textbox', {name: 'Name'});
		this.newButton = page
			.getByRole('button', {exact: true, name: 'New'})
			.first();
		this.page = page;
		this.taskStatusLabel = (taskName, taskStatus = 'success') => {
			const taskStatusTexts: Record<taskStatus, string> = {
				completedWithErrors: 'Completed with errors',
				success: 'Successful',
			};

			return this.page
				.locator('tr', {hasText: taskName})
				.locator('.cell-status')
				.getByText(taskStatusTexts[taskStatus], {exact: true});
		};
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

	async export(name: string) {
		await this.clickNew();

		await this.nameInput.fill(name);

		await this.exportButton.click();
	}

	async import(folderPath: string, name: string) {
		await this.nameInput.fill(name);

		await this.selectFile(folderPath);

		await this.completedLabel.waitFor();

		await this.continueButton.click();

		await this.continueButton.click();

		await this.importButton.waitFor();

		await this.importButton.click();

		await this.taskStatusLabel(name).waitFor();
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
}
