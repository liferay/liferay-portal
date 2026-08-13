/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import dragAndDropElement from '../../../../utils/dragAndDropElement';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

export const FIELD_TYPES = [
	'Text',
	'Long Text',
	'Rich Text',
	'Decimal',
	'Select from List',
	'Numeric',
	'Date',
	'Date and Time',
	'Boolean',
	'Upload',
	'Phone Number',
	'Select Related Content',
] as const;

export type FieldType = (typeof FIELD_TYPES)[number];

type Field = {label: string; nth?: number};

type StructureType = 'content' | 'file';

export class StructureBuilderPage {
	readonly page: Page;

	readonly dataApiHelpers: DataApiHelpers;

	private readonly clearAllSpacesButton: Locator;
	private readonly customizeEditorButton: Locator;
	private readonly labelInput: Locator;
	private readonly nameInput: Locator;

	readonly publishButton: Locator;
	readonly saveButton: Locator;
	readonly spaceCheckbox: Locator;
	readonly spaceSelector: Locator;

	constructor(page: Page, dataApiHelpers: DataApiHelpers) {
		this.page = page;

		this.dataApiHelpers = dataApiHelpers;

		this.clearAllSpacesButton = this.page.getByLabel('Clear All');
		this.customizeEditorButton = this.page.getByRole('button', {
			name: 'Customize Editor',
		});
		this.labelInput = this.page.getByLabel('Content Structure Label');
		this.nameInput = this.page.getByLabel('Content Structure Name');
		this.publishButton = this.page.getByRole('button', {name: 'Publish'});
		this.saveButton = this.page.getByRole('button', {name: 'Save'});
		this.spaceCheckbox = this.page.getByLabel(
			'Make this content structure'
		);
		this.spaceSelector = this.page.getByLabel('Spaces', {exact: true});
	}

	private async goto(props: {id: number} | {type: StructureType}) {
		let url = PORTLET_URLS.cmsStructureBuilder;

		if ('id' in props) {
			url = url + `?objectDefinitionId=${props.id}`;
		}
		else if ('type' in props) {
			const folderERC =
				props.type === 'content'
					? 'L_CMS_CONTENT_STRUCTURES'
					: 'L_CMS_FILE_TYPES';

			url = url + `?objectFolderExternalReferenceCode=${folderERC}`;
		}

		await expect(async () => {
			await this.page.goto(url, {waitUntil: 'networkidle'});

			await expect(
				this.page.locator('.component-tbar').getByText('Publish')
			).toBeVisible({timeout: 5000});
		}).toPass({timeout: 30000});
	}

	getTreeItem({
		field,
		parent = this.page,
	}: {
		field: Field;
		parent?: Locator | Page;
	}) {
		return parent
			.locator('.treeview-link', {hasText: field.label})
			.nth(field.nth || 0);
	}

	async addField(type: FieldType, parent?: Field) {
		let trigger: Locator;

		if (parent) {
			await this.selectFields([parent]);

			const treeItem = this.getTreeItem({field: parent});

			trigger = treeItem.getByTitle('Add Field');
		}
		else {
			trigger = this.page.getByTitle('Add Field').first();
		}

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {exact: true, name: type}),
			trigger,
		});
	}

	async addReferencedStructures(names: string[]) {
		const hasFields = !(await this.page
			.getByText('No Fields Yet')
			.isVisible());

		let trigger: Locator;

		if (hasFields) {
			trigger = this.page.getByLabel('Add Field');
		}
		else {
			trigger = this.page.getByText('Add Field');
		}

		await clickAndExpectToBeVisible({
			target: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Referenced Content Structure',
			}),
			trigger,
		});

		await clickAndExpectToBeVisible({
			target: this.page.locator('.modal-title', {
				hasText: 'Referenced Content Structure',
			}),
			timeout: 2000,
			trigger: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Referenced Content Structure',
			}),
		});

		for (const name of names) {
			await expect(async () => {
				await this.page
					.getByLabel('Content Structures')
					.click({timeout: 1000});

				await this.page
					.getByRole('option', {name})
					.click({timeout: 1000});

				await expect(
					this.page.locator('.label-secondary', {hasText: name})
				).toBeVisible({timeout: 1000});
			}).toPass();
		}

		await clickAndExpectToBeHidden({
			target: this.page.locator('.modal-title', {
				hasText: 'Referenced Content Structure',
			}),
			trigger: this.page.locator('.modal-footer').getByText('Add'),
		});
	}

	async addRelatedContent(name: string, relatedContent: string) {
		const hasFields = !(await this.page
			.getByText('No Fields Yet')
			.isVisible());

		let trigger: Locator;

		if (hasFields) {
			trigger = this.page.getByLabel('Add Field');
		}
		else {
			trigger = this.page.getByText('Add Field');
		}

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				name: 'Select Related Content',
			}),
			trigger,
		});

		await this.page
			.getByRole('textbox', {exact: true, name: 'Label Mandatory'})
			.fill(name);

		await expect(async () => {
			await this.page
				.getByRole('combobox', {exact: true, name: 'Related Content'})
				.click();

			await expect(
				this.page.getByRole('option', {
					exact: true,
					name: relatedContent,
				})
			).toBeVisible();

			await this.page
				.getByRole('option', {exact: true, name: relatedContent})
				.click();
		}).toPass();
	}

	async changeFieldSettings({
		acceptedFileExtensions,
		erc,
		label,
		localizable,
		mandatory,
		maximumFileSize,
		multiselection,
		name,
		picklist,
		relatedContent,
		requestFile,
		showFilesInLibrary,
	}: {
		acceptedFileExtensions?: string;
		erc?: string;
		label?: string;
		localizable?: boolean;
		mandatory?: boolean;
		maximumFileSize?: number;
		multiselection?: boolean;
		name?: string;
		picklist?: string;
		relatedContent?: string;
		requestFile?: 'computer' | 'document-library';
		showFilesInLibrary?: boolean;
	}) {
		if (acceptedFileExtensions !== undefined) {
			const acceptedFileExtensionsInput = this.page.getByLabel(
				'Accepted File Extensions'
			);

			await acceptedFileExtensionsInput.fill(acceptedFileExtensions);
			await acceptedFileExtensionsInput.blur();
		}

		if (erc !== undefined) {
			const ercInput = this.page.getByLabel('ERC');

			await ercInput.fill(erc);
			await ercInput.blur();
		}

		if (name !== undefined) {
			const fieldNameInput = this.page.getByLabel('Field Name');

			await fieldNameInput.fill(name);
			await fieldNameInput.blur();
		}

		if (label !== undefined) {
			const labelInput = this.page.getByLabel('Label');

			await labelInput.fill(label);
			await labelInput.blur();
		}

		if (picklist !== undefined) {
			const labelInput = this.page.getByLabel('Picklist');

			await labelInput.click();

			const option = this.page.getByRole('option', {name: picklist});

			await option.waitFor();

			await option.click();
		}

		const localizableToggle = this.page.getByLabel('Localizable');

		if (
			localizable !== undefined &&
			!(await localizableToggle.isChecked())
		) {
			await this.page.getByLabel('Localizable').click();
		}

		const mandatoryToggle = this.page.getByRole('checkbox', {
			name: 'Mandatory',
		});

		if (mandatory !== undefined && !(await mandatoryToggle.isChecked())) {
			await mandatoryToggle.click();
		}

		const multiselectionToggle = this.page.getByRole('checkbox', {
			name: 'Multiselection',
		});

		if (
			multiselection !== undefined &&
			!(await multiselectionToggle.isChecked())
		) {
			await multiselectionToggle.click();
		}

		if (relatedContent !== undefined) {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.page.getByRole('option', {
					exact: true,
					name: relatedContent,
				}),
				trigger: this.page.getByRole('combobox', {
					name: 'Related Content',
				}),
			});
		}

		if (requestFile !== undefined) {
			const option = this.page.getByRole('option', {
				name: requestFile === 'computer' ? 'Computer' : 'Item Selector',
			});
			const trigger = this.page.getByLabel('Request Files');

			await clickAndExpectToBeVisible({target: option, trigger});

			// The CMS theme compiles clay's atlas-custom-properties flavor,
			// which sets pointer-events: none on .dropdown-item.active, so the
			// selected option cannot be clicked. Remove this guard once that
			// divergence is resolved.

			if ((await option.getAttribute('aria-selected')) === 'true') {
				await clickAndExpectToBeHidden({target: option, trigger});
			}
			else {
				await option.click();
			}
		}

		if (maximumFileSize !== undefined) {
			const maxFileSizeInput = this.page.getByLabel('Maximum File Size');

			await maxFileSizeInput.fill(String(maximumFileSize));
			await maxFileSizeInput.blur();
		}

		if (showFilesInLibrary !== undefined) {
			const showFilesInLibraryToggle = this.page.getByRole('checkbox', {
				name: 'Show Files in CMS Library',
			});

			if (
				(await showFilesInLibraryToggle.isChecked()) !==
				showFilesInLibrary
			) {
				await showFilesInLibraryToggle.click();
			}
		}
	}

	async changeStructureSettings({
		erc,
		label,
		name,
	}: {
		erc?: string;
		label?: string;
		name?: string;
	}) {
		if (erc !== undefined) {
			const ercInput = this.page.getByLabel('ERC');
			await ercInput.fill(erc);
			await ercInput.blur();
		}

		if (label !== undefined) {
			await this.labelInput.fill(label);
			await this.labelInput.blur();
		}

		if (name !== undefined) {
			await this.nameInput.fill(name);
			await this.nameInput.blur();
		}
	}

	async checkIsParent({child, parent}) {
		const parentContainer = this.page.locator('.treeview-item', {
			has: this.getTreeItem({field: parent}),
		});

		await expect(
			this.getTreeItem({
				field: child,
				parent: parentContainer,
			})
		).toBeVisible();
	}

	async clickFieldAction(field: Field, action: string) {
		await this.selectFields([field]);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				name: action,
			}),
			trigger: this.page
				.getByRole('treeitem', {name: field.label})
				.getByRole('button', {name: 'Field Options'}),
		});
	}

	async createRepeatableGroup({
		fields,
		label,
	}: {
		fields: Field[];
		label?: string;
	}) {
		await this.selectFields(fields);

		if (fields.length > 1) {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.page.getByRole('menuitem', {
					name: 'Create Repeatable Group',
				}),
				trigger: this.page.getByLabel('Selection Options'),
			});
		}
		else {
			await this.clickFieldAction(fields[0], 'Create Repeatable Group');
		}

		await this.page
			.locator('.label-item', {hasText: 'Repeatable Group'})
			.waitFor();

		if (label) {
			const labelInput = this.page.getByLabel('Label');

			await labelInput.fill(label);
			await labelInput.blur();

			await expect(
				this.page.locator('.treeview-link', {hasText: label})
			).toBeVisible();
		}
	}

	async createStructureFromData({
		autoDelete = true,
		erc = getRandomString(),
		label,
		name = `StructureName${getRandomInt()}`,
		page,
		publish = true,
		spaces,
		type = 'content',
	}: {
		autoDelete?: boolean;
		erc?: string;
		label: string;
		name?: string;
		page: StructureBuilderPage;
		publish?: boolean;
		spaces?: string[];
		type?: StructureType;
	}) {
		await page.goToCreateStructure(type);

		if (spaces) {
			await this.selectSpaces(spaces);
		}

		await page.changeStructureSettings({
			erc,
			label,
			name,
		});

		const id = await page.saveStructure({autoDelete});

		if (publish) {
			await page.publishStructure();
		}

		return id;
	}

	async customizeEditor() {
		await expect(async () => {
			if (await this.customizeEditorButton.isVisible()) {
				await this.customizeEditorButton.click({timeout: 2000});
			}

			await expect(
				this.page.getByText('Select a Page Element', {exact: true})
			).toBeVisible({
				timeout: 3500,
			});

			await this.waitForEditorCustomizerModal();
		}).toPass();
	}

	async deleteFields(
		fields: Field[],
		{confirm}: {confirm?: boolean} = {confirm: true}
	) {

		// Deleting one field

		if (fields.length === 1) {
			const [field] = fields;

			const treeItem = this.getTreeItem({field});

			await treeItem.waitFor({state: 'visible'});

			await this.selectFields([field]);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.page.getByRole('menuitem', {name: 'Delete'}),
				trigger: treeItem.getByLabel('Field Options'),
			});
		}

		// Deleting multiple fields

		else {
			await this.selectFields(fields);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.page.getByRole('menuitem', {name: 'Delete'}),
				trigger: this.page.getByLabel('Selection Options'),
			});
		}

		// Wait some time in case deletion modal is shown

		await this.page.waitForTimeout(2500);

		const modal = this.page.locator('.modal-content', {
			hasText: 'Delete Fields',
		});

		if ((await modal.isVisible()) && confirm) {
			await clickAndExpectToBeHidden({
				target: modal,
				trigger: modal.getByText('Delete', {exact: true}),
			});
		}
	}

	async dragItem({
		item,
		target,
		verify = true,
	}: {
		item: Field;
		target: Field;
		verify?: boolean;
	}) {
		const dragItem = this.getTreeItem({
			field: item,
		});

		const targetItem = this.getTreeItem({
			field: target,
		});

		await dragAndDropElement({
			dragTarget: dragItem,
			dropTarget: targetItem,
		});

		if (verify) {
			await this.checkIsParent({child: item, parent: target});
		}
	}

	async editStructure(id: number) {
		await this.goto({id});
	}

	async enableForAllSpaces() {
		if (
			(await this.spaceCheckbox.isChecked()) &&
			this.spaceSelector.isDisabled()
		) {
			return;
		}

		await expect(async () => {
			await this.page
				.getByText('Content Structure Fields')
				.click({timeout: 500});

			await this.spaceCheckbox.click({timeout: 500});

			await expect(this.spaceSelector).toBeDisabled({timeout: 500});
		}).toPass();
	}

	async expandField(field: Field) {
		const treeItem = this.getTreeItem({field});

		await expect(async () => {
			await treeItem.locator('.component-expander').click({timeout: 500});

			await expect(treeItem).toHaveAttribute('aria-expanded', 'true', {
				timeout: 2000,
			});
		}).toPass();
	}

	async goToCreateStructure(type: StructureType = 'content') {
		await this.goto({type});
	}

	async publishStructure() {
		await this.publishButton.click();

		// Publishing a change that may impact stored data, such as removing a
		// field, raises a confirmation first

		const confirmDialog = this.page.getByRole('dialog', {
			name: 'Publish Content Structure Changes',
		});

		const successAlert = this.page
			.locator('.alert-success')
			.filter({hasText: 'published successfully'});

		await expect(confirmDialog.or(successAlert)).toBeVisible({
			timeout: 10000,
		});

		if (await confirmDialog.isVisible()) {
			await confirmDialog.getByRole('button', {name: 'Publish'}).click();
		}

		await waitForAlert(this.page, 'published successfully', {
			timeout: 10000,
		});

		const url = new URL(this.page.url());

		const objectDefinitionId = url.searchParams.get('objectDefinitionId');

		return objectDefinitionId ? Number(objectDefinitionId) : undefined;
	}

	async saveStructure(
		{autoDelete}: {autoDelete?: boolean} = {autoDelete: true}
	) {
		const save = async () => {
			await this.saveButton.click();

			await waitForAlert(this.page, 'successfully', {timeout: 5000});
		};

		const [response] = await Promise.all([
			this.page.waitForResponse(
				(response) =>
					response.url().includes('object-definitions') &&
					response.status() === 200,
				{timeout: 10000}
			),
			await save(),
		]);

		const {id} = await response.json();

		// Add ids to ApiHelpers data so structures are cleaned after each test

		if (autoDelete) {
			this.dataApiHelpers.data.push({
				id,
				type: 'objectDefinition',
			});
		}

		return id;
	}

	async selectFields(fields: Field[]) {
		for (const [i, field] of fields.entries()) {
			const treeItem = this.getTreeItem({field});

			await expect(async () => {
				await treeItem.click({
					modifiers: i === 0 ? [] : ['ControlOrMeta'],
					timeout: 500,
				});

				await expect(treeItem).toHaveClass(/active/, {timeout: 500});
			}).toPass();
		}

		if (fields.length > 1) {
			await expect(
				this.page.getByText(`${fields.length} Items Selected`)
			).toBeVisible();
		}
	}

	async selectSpaces(spaces: string[]) {
		if (await this.spaceCheckbox.isChecked()) {
			await this.spaceCheckbox.uncheck();
		}
		else if (await this.clearAllSpacesButton.isVisible()) {
			await this.clearAllSpacesButton.click();
		}

		for (const space of spaces) {
			await expect(async () => {
				await this.spaceSelector.click({timeout: 1000});

				await this.page
					.getByRole('option', {name: space})
					.click({timeout: 1000});

				await expect(
					this.page.locator('.label-secondary', {hasText: space})
				).toBeVisible({timeout: 5000});
			}).toPass({timeout: 20000});
		}
	}

	async selectStructure() {
		const treeItem = this.page.locator('.treeview-link').first();

		await expect(async () => {
			await treeItem.click({
				timeout: 500,
			});

			await expect(treeItem).toHaveClass(/active/, {timeout: 500});

			await expect(
				this.page.getByLabel('Content Structure Name')
			).toBeVisible();
		}).toPass();
	}

	async setWorkflows(workflows: {space: string; workflow: string}[]) {
		for (const {space, workflow} of workflows) {
			if (!space) {
				await this.page
					.getByLabel('Default Workflow')
					.selectOption(workflow);
			}
			else {
				const row = this.page.locator('tr', {hasText: space});

				await row.getByLabel('Select Workflow').selectOption(workflow);
			}
		}
	}

	async switchLanguage(languageId: string) {
		const trigger = this.page.getByLabel('Open Localizations');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.locator('.dropdown-item', {hasText: languageId}),
			trigger,
		});

		await expect(trigger).toHaveAttribute('title', languageId);
	}

	async switchTab(name: 'General' | 'Search' | 'Workflow') {
		const target =
			name === 'General'
				? this.page.getByLabel('ERC')
				: name === 'Search'
					? this.page.getByText('Searchable')
					: this.page.getByText(
							'Set the default workflow for entries'
						);

		await clickAndExpectToBeVisible({
			target,
			trigger: this.page.getByRole('tab', {name}),
		});
	}

	async waitForEditorCustomizerModal() {
		await this.page.waitForTimeout(4000);

		const gotItButton = this.page.getByText('Got It');
		const tryItButton = this.page.getByText('Try It');

		const button = (await gotItButton.isVisible())
			? gotItButton
			: (await tryItButton.isVisible())
				? tryItButton
				: null;

		if (button) {
			await clickAndExpectToBeHidden({
				target: button,
				trigger: button,
			});
		}
	}
}
