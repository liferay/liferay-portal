/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';

export const FIELD_TYPES = [
	'Text',
	'Long Text',
	'Rich Text',
	'Decimal',
	'Single Select',
	'Multiselect',
	'Numeric',
	'Date',
	'Date and Time',
	'Boolean',
	'Upload',
] as const;

export type FieldType = (typeof FIELD_TYPES)[number];

type Field = {label: string; nth?: number};

type StructureType = 'content' | 'file';

export class StructureBuilderPage {
	readonly page: Page;

	readonly dataApiHelpers: DataApiHelpers;

	private readonly clearAllSpacesButton: Locator;
	private readonly customizeExperienceButton: Locator;
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
		this.customizeExperienceButton = this.page.getByRole('button', {
			name: 'Customize Experience',
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
			await this.page.goto(url);

			await this.page
				.locator('.component-tbar')
				.getByText('Publish')
				.waitFor({timeout: 2000});
		}).toPass();
	}

	async addField(type: FieldType) {
		const hasFields = !(await this.page
			.getByText('No Fields Yet')
			.isVisible());

		let trigger: Locator;

		if (hasFields) {
			trigger = this.page.getByTitle('Add Field');
		}
		else {
			trigger = this.page.getByText('Add Field');
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

	async changeFieldSettings({
		erc,
		label,
		localizable,
		mandatory,
		name,
		picklist,
		requestFile,
	}: {
		erc?: string;
		label?: string;
		localizable?: boolean;
		mandatory?: boolean;
		name?: string;
		picklist?: string;
		requestFile?: 'computer' | 'document-library';
	}) {
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

		const mandatoryToggle = this.page.getByLabel('Mandatory');

		if (mandatory !== undefined && !(await mandatoryToggle.isChecked())) {
			await this.page.getByLabel('Mandatory').click();
		}

		if (requestFile !== undefined) {
			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.page.getByRole('option', {
					name:
						requestFile === 'computer'
							? 'Computer'
							: 'Documents and Media',
				}),
				trigger: this.page.getByLabel('Request Files'),
			});
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

	async clickFieldAction(field: Field, action: string) {
		await this.selectFields([field]);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				name: action,
			}),
			trigger: this.page.getByRole('button', {name: 'Field Options'}),
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
		erc = getRandomString(),
		label,
		name = `StructureName${getRandomInt()}`,
		page,
		publish = true,
	}: {
		erc?: string;
		label: string;
		name?: string;
		page: StructureBuilderPage;
		publish?: boolean;
	}) {
		await page.goToCreateStructure();

		await page.enableForAllSpaces();

		await page.changeStructureSettings({
			erc,
			label,
			name,
		});

		const id = await page.saveStructure();

		if (publish) {
			await page.publishStructure();
		}

		return id;
	}

	async customizeExperience() {
		await expect(async () => {
			if (await this.customizeExperienceButton.isVisible()) {
				await this.customizeExperienceButton.click({timeout: 2000});
			}

			await expect(
				this.page.getByText('Select a Page Element', {exact: true})
			).toBeVisible({
				timeout: 3500,
			});

			await this.waitForExperienceCustomizerModal();
		}).toPass();
	}

	async deleteFields(fields: Field[]) {

		// Deleting one field

		if (fields.length === 1) {
			const [field] = fields;

			const treeItems = this.page.getByRole('treeitem', {
				name: field.label,
			});

			const treeItem = treeItems.nth(field.nth || 0);

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
		const treeItem = this.page
			.locator('.treeview-item')
			.getByLabel(field.label, {exact: true})
			.nth(field.nth || 0);

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
		const publish = async () => {
			await this.publishButton.click();

			await waitForAlert(this.page, 'published successfully', {
				timeout: 5000,
			});
		};

		const [response] = await Promise.all([
			this.page.waitForResponse(
				(response) =>
					response.url().includes('object-definitions') &&
					response.status() === 200,
				{timeout: 5000}
			),
			await publish(),
		]);

		return await response.json();
	}

	async saveStructure() {
		const save = async () => {
			await this.saveButton.click();

			await waitForAlert(this.page, 'successfully', {timeout: 5000});
		};

		const [response] = await Promise.all([
			this.page.waitForResponse(
				(response) =>
					response.url().includes('object-definitions') &&
					response.status() === 200,
				{timeout: 5000}
			),
			await save(),
		]);

		const {id} = await response.json();

		// Add ids to ApiHelpers data so structures are cleaned after each test

		this.dataApiHelpers.data.push({
			id,
			type: 'objectDefinition',
		});

		return id;
	}

	async selectFields(fields: Field[]) {
		for (const [i, field] of fields.entries()) {
			const treeItem = this.page
				.getByRole('treeitem', {
					name: field.label,
				})
				.nth(field.nth || 0);

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
				).toBeVisible();
			}).toPass();
		}
	}

	async selectStructure() {
		const treeItem = this.page.getByRole('treeitem').first();

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

	async waitForExperienceCustomizerModal() {
		await this.page.waitForTimeout(4000);

		const gotItButton = this.page.getByText('Got It');

		if (await gotItButton.isVisible()) {
			await clickAndExpectToBeHidden({
				target: gotItButton,
				trigger: gotItButton,
			});
		}
	}
}
