/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js';
import {Locator, Page, expect} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
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

type FieldType = (typeof FIELD_TYPES)[number];

type Field = {label: string; nth?: number};

type StructureType = 'content' | 'file';

export class StructureBuilderPage {
	readonly page: Page;

	private readonly customizeExperienceButton: Locator;
	private readonly labelInput: Locator;
	private readonly nameInput: Locator;
	private readonly spaceCheckbox: Locator;

	readonly publishButton: Locator;
	readonly saveButton: Locator;
	readonly spaceSelector: Locator;

	constructor(page: Page) {
		this.page = page;

		this.customizeExperienceButton = this.page.getByRole('button', {
			name: 'Customize Experience',
		});
		this.labelInput = this.page.getByLabel('Structure Label');
		this.nameInput = this.page.getByLabel('Structure Name');
		this.publishButton = this.page.getByRole('button', {name: 'Publish'});
		this.saveButton = this.page.getByRole('button', {name: 'Save'});
		this.spaceCheckbox = this.page.getByRole('checkbox', {
			name: 'Make this structure available in all spaces',
		});
		this.spaceSelector = this.page.getByLabel('Spaces', {exact: true});
	}

	private async goto(props: {id: string} | {type: StructureType}) {
		let url = PORTLET_URLS.cmsStructureBuilder;

		if ('id' in props) {
			url = url + `?objectDefinitionId=${props.id}`;
		}
		else if ('type' in props) {
			const erc =
				props.type === 'content'
					? 'L_CMS_CONTENT_STRUCTURES'
					: 'L_CMS_FILE_TYPES';

			url = url + `?objectFolderExternalReferenceCode=${erc}`;
		}

		await this.page.goto(url);

		await this.page
			.locator('.management-bar')
			.getByText('Publish')
			.waitFor();
	}

	async addField(type: FieldType) {
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
				name: 'Referenced Structure',
			}),
			trigger,
		});

		await clickAndExpectToBeVisible({
			target: this.page.locator('.modal-title', {
				hasText: 'Referenced Structure',
			}),
			timeout: 2000,
			trigger: this.page.getByRole('menuitem', {
				exact: true,
				name: 'Referenced Structure',
			}),
		});

		for (const name of names) {
			await expect(async () => {
				await this.page.getByLabel('Structures').click({timeout: 1000});

				await this.page
					.getByRole('option', {name})
					.click({timeout: 1000});

				await expect(
					this.page.locator('.label-secondary', {hasText: name})
				).toBeVisible();
			}).toPass();
		}

		await clickAndExpectToBeHidden({
			target: this.page.locator('.modal-title', {
				hasText: 'Referenced Structure',
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
	}: {
		erc?: string;
		label?: string;
		localizable?: boolean;
		mandatory?: boolean;
		name?: string;
		picklist?: string;
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

	async createStructure(type: StructureType = 'content') {
		await this.goto({type});
	}

	async customizeExperience() {
		await expect(async () => {
			await this.customizeExperienceButton.click();

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

			const treeItems = this.page
				.locator('.treeview-item')
				.getByLabel(field.label, {exact: true});

			await treeItems.waitFor({state: 'visible'});

			const count = await treeItems.count();

			const treeItem = treeItems.nth(field.nth || 0);

			await this.selectFields([field]);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.page.getByRole('menuitem', {name: 'Delete'}),
				trigger: treeItem.getByLabel('Field Options'),
			});

			await expect(treeItems).toHaveCount(count - 1);
		}

		// Deleting multiple fields

		else {
			const count = await this.page
				.locator('.treeview-item')
				.first()
				.locator('.treeview-group > .treeview-item')
				.count();

			await this.selectFields(fields);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: this.page.getByRole('menuitem', {name: 'Delete'}),
				trigger: this.page.getByLabel('Selection Options'),
			});

			await expect(
				this.page
					.locator('.treeview-item')
					.first()
					.locator('.treeview-group > .treeview-item')
			).toHaveCount(count - fields.length);
		}
	}

	async deleteStructure(id: number) {
		const apiHelpers = new ApiHelpers(this.page);

		const APIClient = await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {
			response: {status},
		} = await APIClient.deleteObjectDefinition(id);

		expect(status).toBe(204);
	}

	async editStructure(id: string) {
		await this.goto({id});
	}

	async enableForAllSpaces() {
		await expect(async () => {
			await this.page.getByText('Structure Fields').click({timeout: 500});

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

		return await response.json();
	}

	async selectFields(fields: Field[]) {
		for (const [i, field] of fields.entries()) {
			const treeItem = this.page
				.locator('.treeview-item')
				.getByLabel(field.label, {exact: true})
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
