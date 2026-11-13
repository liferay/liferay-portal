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
	readonly attachmentActionsButton: (title: string) => Locator;
	readonly creationMenuNewButton: Locator;
	readonly deleteMenuItem: Locator;
	readonly mediaAttachmentsTable: Locator;
	readonly mediaForm: Locator;
	readonly mediaImagesTable: Locator;
	readonly page: Page;
	readonly selectFileButton: Locator;
	readonly selectFileFrame: FrameLocator;
	readonly selectFileIframe: Locator;
	readonly selectFileItem: (title: string) => Locator;
	readonly selectFileSearchInput: Locator;
	readonly sidePanel: Locator;
	readonly sidePanelCurrentImage: Locator;
	readonly sidePanelCustomFieldInput: (fieldName: string) => Locator;
	readonly sidePanelDoneButton: Locator;
	readonly sidePanelFrame: FrameLocator;
	readonly sidePanelOptionSelect: (optionName: string) => Locator;
	readonly sidePanelSaveAsDraftButton: Locator;
	readonly sidePanelTag: (tagName: string) => Locator;
	readonly sidePanelTagOption: (tagName: string) => Locator;
	readonly sidePanelTagRemoveButton: (tagName: string) => Locator;
	readonly sidePanelTagsClearAllButton: Locator;
	readonly sidePanelTagsInput: Locator;
	readonly sidePanelTagsSelectButton: Locator;
	readonly sidePanelTagsSelectCheckbox: (tagName: string) => Locator;
	readonly sidePanelTagsSelectFrame: FrameLocator;
	readonly sidePanelTagsSelectIframe: Locator;
	readonly sidePanelTitleInput: Locator;
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
		this.attachmentActionsButton = (title: string) =>
			this.mediaForm
				.getByRole('row')
				.filter({hasText: title})
				.getByRole('button', {name: 'Actions'});
		this.deleteMenuItem = page.getByRole('menuitem', {name: 'Delete'});
		this.page = page;
		this.sidePanel = page.locator('.fds-side-panel.is-visible');
		this.sidePanelFrame = page.frameLocator('.is-visible iframe');
		this.selectFileButton = this.sidePanelFrame.getByRole('button', {
			exact: true,
			name: 'Select File',
		});
		this.selectFileIframe = this.sidePanelFrame.locator(
			'iframe[title="Select File"]'
		);
		this.selectFileFrame = this.selectFileIframe.contentFrame();
		this.selectFileItem = (title: string) =>
			this.selectFileFrame.getByText(title, {exact: true});
		this.selectFileSearchInput =
			this.selectFileFrame.getByPlaceholder('Search for');
		this.sidePanelCurrentImage = this.sidePanelFrame.getByRole('img', {
			name: 'Current Image',
		});
		this.sidePanelCustomFieldInput = (fieldName: string) =>
			this.sidePanelFrame.locator(
				`input[name*="ExpandoAttribute--${fieldName}--"]`
			);
		this.sidePanelDoneButton = this.sidePanelFrame.getByRole('button', {
			exact: true,
			name: 'Done',
		});
		this.sidePanelOptionSelect = (optionName: string) =>
			this.sidePanelFrame.getByLabel(optionName, {exact: true});
		this.sidePanelSaveAsDraftButton = this.sidePanelFrame.getByRole(
			'button',
			{exact: true, name: 'Save as Draft'}
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
		this.sidePanelTagRemoveButton = (tagName: string) =>
			this.sidePanelTag(tagName).getByRole('button');
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
		this.sidePanelTagsSelectCheckbox = (tagName: string) =>
			this.sidePanelTagsSelectFrame
				.getByRole('row')
				.filter({hasText: tagName})
				.getByRole('checkbox');
		this.sidePanelTitleInput = this.sidePanelFrame.getByLabel('Title');
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
			target: this.sidePanelTitleInput,
			trigger: this.mediaForm.getByText(title, {exact: true}),
		});

		await expect(this.sidePanel).not.toHaveClass(/is-loading|is-moving/);
		await expect(this.sidePanelTitleInput).toBeVisible();
	}

	async publishAttachment() {
		await this.sidePanelTitleInput.click();

		await expect(this.sidePanelFrame.getByRole('listbox')).toBeHidden();

		await this.sidePanelFrame
			.getByRole('button', {exact: true, name: 'Publish'})
			.click();

		await waitForAlert(this.sidePanelFrame);
	}
}
