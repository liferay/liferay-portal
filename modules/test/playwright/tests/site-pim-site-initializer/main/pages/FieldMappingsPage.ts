/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {DataSetPage} from '../../../site-cms-site-initializer/main/pages/DataSetPage';
import {EditFieldMappingsPage} from './EditFieldMappingsPage';

export class FieldMappingsPage {
	readonly channelField: (channelField: string) => Locator;
	readonly dataSetFragmentPage: DataSetPage;
	readonly moreActionsButton: Locator;
	readonly moreActionsMenuItem: (name: string) => Locator;
	readonly page: Page;
	readonly row: (channelField: string) => Locator;
	readonly sourceAttributes: (channelField: string) => Locator;
	readonly status: (channelField: string) => Locator;

	constructor(page: Page) {
		this.channelField = (channelField) =>
			this.row(channelField).getByRole('link', {
				exact: true,
				name: channelField,
			});
		this.dataSetFragmentPage = new DataSetPage(page);
		this.moreActionsButton = page.getByRole('button', {
			name: 'More Actions',
		});
		this.moreActionsMenuItem = (name) =>
			page.getByRole('menuitem', {exact: true, name});
		this.page = page;
		this.row = (channelField) =>
			this.dataSetFragmentPage.table.bodyRows.filter({
				has: page.getByRole('link', {
					exact: true,
					name: channelField,
				}),
			});
		this.sourceAttributes = (channelField) =>
			this.row(channelField).locator('.cell-sourceAttributes');
		this.status = (channelField) =>
			this.row(channelField).locator('.cell-status .label');
	}

	async clearMapping(channelField: string) {
		this.page.once('dialog', (dialog) => dialog.accept());

		await this.dataSetFragmentPage.execItemAction({
			action: 'Clear',
			filter: channelField,
		});
	}

	async editMapping(channelField: string) {
		await this.dataSetFragmentPage.execItemAction({
			action: 'Edit',
			filter: channelField,
		});
	}

	async executeConnector() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.moreActionsMenuItem('Execute'),
			trigger: this.moreActionsButton,
		});

		await waitForAlert(
			this.page,
			'Execution has started successfully and will continue in the background.'
		);
	}

	async expectClearVisible(channelField: string) {
		await clickAndExpectToBeVisible({
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Clear',
			}),
			trigger: this.row(channelField).getByRole('button', {
				name: `${channelField} Actions`,
			}),
		});

		await this.page.keyboard.press('Escape');
	}

	async expectMoreActionsMenuItems({
		hidden = [],
		visible = [],
	}: {
		hidden?: string[];
		visible?: string[];
	}) {
		await this.moreActionsButton.click();

		for (const name of visible) {
			await expect(this.moreActionsMenuItem(name)).toBeVisible();
		}

		for (const name of hidden) {
			await expect(this.moreActionsMenuItem(name)).toBeHidden();
		}

		await this.page.keyboard.press('Escape');
	}

	async mapRequiredChannelFields() {
		const editFieldMappingsPage = new EditFieldMappingsPage(this.page);

		for (const {channelField, sourceAttribute, value} of [
			{channelField: 'Catalog ID', value: '1'},
			{channelField: 'Name', sourceAttribute: 'Title (title)'},
			{channelField: 'Product Type', value: 'simple'},
			{channelField: 'SKU', sourceAttribute: 'SKU (sku)'},
		]) {
			await this.channelField(channelField).click();

			if (sourceAttribute) {
				await editFieldMappingsPage.mapToSourceAttribute(
					sourceAttribute
				);
			}
			else {
				await editFieldMappingsPage.mapToValue(value!);
			}

			await editFieldMappingsPage.saveButton.click();

			await expect(this.status(channelField)).toHaveText('Mapped');
		}
	}

	async openConnectorSchedule() {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.moreActionsMenuItem('Schedule'),
			trigger: this.moreActionsButton,
		});

		await this.page.waitForURL(/edit_dispatch_trigger/);
	}
}
