/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';

export type OrderImportSource = 'CSV' | 'Orders' | 'Wish Lists';

export async function getTableRowCells(
	table: Locator,
	rowText: string
): Promise<Record<string, string>> {
	const labels = await table.locator('thead th').allInnerTexts();

	const values = await table
		.locator('tbody tr')
		.filter({hasText: rowText})
		.first()
		.locator('td')
		.allInnerTexts();

	if (!values.length) {
		throw new Error(`Cannot locate table row with value ${rowText}`);
	}

	return Object.fromEntries(
		labels.map((label, index) => [
			label.trim().toUpperCase(),
			(values[index] ?? '').trim(),
		])
	);
}

export class OrderImportPage {
	readonly cancelButton: (source?: OrderImportSource) => Locator;
	readonly importButton: (source?: OrderImportSource) => Locator;
	readonly importedRowsAlert: (count: number) => Locator;
	readonly notImportedRowsAlert: (count: number) => Locator;
	readonly orderActionsButton: Locator;
	readonly page: Page;
	readonly previewRows: (source?: OrderImportSource) => Locator;
	readonly previewTable: (source?: OrderImportSource) => Locator;
	readonly sourceLink: (name: string, source?: OrderImportSource) => Locator;
	readonly sourceMenuItem: (source: OrderImportSource) => Locator;
	readonly sourceTable: (source?: OrderImportSource) => Locator;

	constructor(page: Page) {
		this.cancelButton = (source = 'Wish Lists') =>
			this.frame(source).getByRole('button', {
				exact: true,
				name: 'Cancel',
			});
		this.importButton = (source = 'Wish Lists') =>
			this.frame(source).getByRole('button', {
				exact: true,
				name: 'Import',
			});
		this.importedRowsAlert = (count: number) =>
			page.getByText(
				count > 1
					? `${count} rows were imported successfully`
					: '1 row was imported successfully'
			);
		this.notImportedRowsAlert = (count: number) =>
			page.getByText(
				count > 1
					? `${count} rows were not imported`
					: '1 row was not imported'
			);
		this.orderActionsButton = page.locator('.thumb-menu');
		this.page = page;
		this.previewRows = (source = 'Wish Lists') =>
			this.previewTable(source).locator('tbody tr');
		this.previewTable = (source = 'Wish Lists') =>
			this.frame(source).locator('.fds table');
		this.sourceLink = (name: string, source = 'Wish Lists') =>
			this.sourceTable(source).getByRole('link', {name});
		this.sourceMenuItem = (source: OrderImportSource) =>
			page.getByRole('menuitem', {name: `Import from ${source}`});
		this.sourceTable = (source = 'Wish Lists') =>
			this.frame(source).locator('.fds table');
	}

	frame(source: OrderImportSource = 'Wish Lists'): FrameLocator {
		return this.page.frameLocator(`iframe[title="Import from ${source}"]`);
	}

	async openImportModal(source: OrderImportSource = 'Wish Lists') {
		await clickAndExpectToBeVisible({
			target: this.sourceMenuItem(source),
			trigger: this.orderActionsButton,
		});

		await this.sourceMenuItem(source).click();
	}

	async previewRowCells(
		productName: string,
		source: OrderImportSource = 'Wish Lists'
	) {
		return getTableRowCells(this.previewTable(source), productName);
	}

	async selectSource(name: string, source: OrderImportSource = 'Wish Lists') {
		await this.sourceLink(name, source).click();
	}
}
