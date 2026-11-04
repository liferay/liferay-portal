/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../utils/waitForAlert';
import {searchTableRowByValue} from '../commerceDNDTablePage';

export class CommerceAdminProductDetailsMediaPage {
	readonly addAttachmentButton: Locator;
	readonly addImageButton: Locator;
	readonly creationMenuNewButton: Locator;
	readonly mediaAttachmentsTable: Locator;
	readonly mediaForm: Locator;
	readonly mediaImagesTable: Locator;
	readonly page: Page;
	readonly selectFileButton: Locator;
	readonly selectFileFrame: FrameLocator;
	readonly selectFileIframe: Locator;
	readonly sidePanelCustomFieldInput: (fieldName: string) => Locator;
	readonly sidePanelFrame: FrameLocator;
	readonly sidePanelTag: (tagName: string) => Locator;
	readonly sidePanelTagOption: (tagName: string) => Locator;
	readonly sidePanelTagsClearAllButton: Locator;
	readonly sidePanelTagsInput: Locator;
	readonly sidePanelTagsSelectButton: Locator;
	readonly sidePanelTagsSelectFrame: FrameLocator;
	readonly sidePanelTagsSelectIframe: Locator;
	readonly tableRow: (
		tableName: Locator,
		colPosition: number,
		value: number | string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly tableRowModifiedDateField: (
		rowValue: string,
		table: Locator
	) => Promise<Locator>;

	constructor(page: Page) {
		this.addAttachmentButton = page.getByLabel('Add Attachment');
		this.addImageButton = page.getByLabel('Add Image');
		this.mediaForm = page.locator(
			'#_com_liferay_commerce_product_definitions_web_internal_portlet_CPDefinitionsPortlet_fm'
		);
		this.mediaAttachmentsTable = this.mediaForm
			.locator('.fds table')
			.nth(1);
		this.mediaImagesTable = this.mediaForm.locator('table').nth(0);
		this.page = page;
		this.sidePanelFrame = page.frameLocator('.is-visible iframe');
		this.selectFileButton = this.sidePanelFrame.getByRole('button', {
			exact: true,
			name: 'Select File',
		});
		this.selectFileIframe = this.sidePanelFrame.locator(
			'iframe[title="Select File"]'
		);
		this.selectFileFrame = this.selectFileIframe.contentFrame();
		this.sidePanelCustomFieldInput = (fieldName: string) =>
			this.sidePanelFrame.locator(
				`input[name*="ExpandoAttribute--${fieldName}--"]`
			);
		this.sidePanelTag = (tagName: string) =>
			this.sidePanelFrame
				.locator('.label')
				.filter({hasText: new RegExp(`^${tagName}$`)});
		this.sidePanelTagOption = (tagName: string) =>
			this.sidePanelFrame.getByRole('option', {
				exact: true,
				name: tagName,
			});
		this.sidePanelTagsClearAllButton = this.sidePanelFrame.getByRole(
			'button',
			{name: 'Clear All'}
		);
		this.sidePanelTagsInput = this.sidePanelFrame.locator(
			"div[id*='assetTagsSelector'] input.form-control-inset"
		);
		this.sidePanelTagsSelectButton = this.sidePanelFrame.getByRole(
			'button',
			{exact: true, name: 'Select Tags'}
		);
		this.sidePanelTagsSelectIframe = this.sidePanelFrame.locator(
			'iframe[title="Tags"]'
		);
		this.sidePanelTagsSelectFrame =
			this.sidePanelTagsSelectIframe.contentFrame();
		this.tableRow = async (
			table: Locator,
			colPosition: number,
			value: number | string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				table,
				colPosition,
				String(value),
				strictEqual
			);
		};
		this.tableRowModifiedDateField = async (rowValue, table) => {
			const tableRow = await this.tableRow(table, 1, rowValue, true);

			if (tableRow && tableRow.row) {
				return tableRow.row.locator('.cell-modifiedDate');
			}

			throw new Error(`Cannot locate row with rowValue: ${rowValue}`);
		};
	}

	async openAttachment(title: string) {
		await this.page.reload();

		await clickAndExpectToBeVisible({
			target: this.sidePanelFrame.getByLabel('Title'),
			trigger: this.mediaForm.getByText(title, {exact: true}),
		});
	}

	async publishAttachment() {

		// The tags autocomplete covers the Publish button, so close it first

		await this.sidePanelFrame.getByLabel('Title').click();

		await expect(this.sidePanelFrame.getByRole('listbox')).toBeHidden();

		await this.sidePanelFrame
			.getByRole('button', {exact: true, name: 'Publish'})
			.click();

		await waitForAlert(this.sidePanelFrame);
	}
}
