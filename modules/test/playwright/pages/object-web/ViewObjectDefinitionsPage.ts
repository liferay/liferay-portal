/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectFolder} from '@liferay/object-admin-rest-client-js';
import {Locator, Page, Request, Response, expect} from '@playwright/test';
import {readFile} from 'fs/promises';
import path from 'path';

import {gotoWithRetry} from '../../utils/gotoWithRetry';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {getTempDir} from '../../utils/temp';
import {waitForFDS} from '../../utils/waitFor';
import {waitForSearchToBeReady} from '../../utils/waitForSearchToBeReady';

export class ViewObjectDefinitionsPage {
	readonly actionsButton: Locator;
	readonly addObjectFolderButton: Locator;
	readonly createObjectDefinitionButton: Locator;
	readonly createObjectFolderButton: Locator;
	readonly confirmObjectFolderNameInput: Locator;
	readonly defaultObjectFolder: Locator;
	readonly deleteObjectDefinitionOption: Locator;
	readonly deleteObjectFolderButton: Locator;
	readonly exportObjectDefinitionOption: Locator;
	readonly frontendDataSetEntries: Locator;
	readonly hiddenFileInput: Locator;
	readonly importObjectDefinitionOption: Locator;
	readonly objectFolderActions: Locator;
	readonly objectFolderCardHeader: Locator;
	readonly objectFolderDeleteFolderOption: Locator;
	readonly objectFolderEditLabelAndERCOption: Locator;
	readonly objectFolders: Locator;
	readonly objectFolderLabelInput: Locator;
	readonly page: Page;
	readonly searchInput: Locator;
	readonly viewInModelBuilderButton: Locator;

	constructor(page: Page) {
		this.actionsButton = page.getByRole('button', {name: 'Actions'});
		this.addObjectFolderButton = page.getByLabel('Add Object Folder');
		this.confirmObjectFolderNameInput = page.locator(
			'input[placeholder="Confirm Folder Name"]'
		);
		this.createObjectDefinitionButton =
			page.getByLabel('Create New Object');
		this.createObjectFolderButton = page.getByRole('button', {
			name: 'Create Folder',
		});
		this.defaultObjectFolder = page
			.getByRole('listitem')
			.filter({hasText: 'Default'});
		this.deleteObjectDefinitionOption = page.getByRole('menuitem', {
			name: 'Delete',
		});
		this.deleteObjectFolderButton = page.getByRole('button', {
			name: 'Delete',
		});
		this.exportObjectDefinitionOption = page.getByRole('menuitem', {
			name: 'Export Object Definition',
		});
		this.frontendDataSetEntries = page.locator('div.table-list-title a');
		this.hiddenFileInput = page.locator('input[type="file"]');
		this.importObjectDefinitionOption = page.getByRole('menuitem', {
			name: 'Import Object Definition',
		});
		this.objectFolders = page
			.getByRole('list')
			.filter({hasText: 'Default'});
		this.objectFolderActions = page
			.locator('div.lfr__object-web-view-object-definitions-title-kebab')
			.getByLabel('Object Folder Actions');
		this.objectFolderCardHeader = page.locator(
			'div.lfr-objects__card-header'
		);
		this.objectFolderDeleteFolderOption = page.getByRole('menuitem', {
			name: 'Delete Object Folder',
		});
		this.objectFolderEditLabelAndERCOption = page.getByRole('menuitem', {
			name: 'Edit Label and ERC',
		});
		this.objectFolderLabelInput = page.locator('input[name="label"]');
		this.page = page;
		this.searchInput = page
			.getByTestId('managementToolbar')
			.getByRole('searchbox', {name: 'Search'});
		this.viewInModelBuilderButton = page.getByLabel(
			'View in Model Builder'
		);
	}

	async changeObjectActivateStatus(objectDefinitionName: string) {
		await this.clickEditObjectDefinitionLink(objectDefinitionName);

		const saveButton = this.page.getByRole('button', {name: 'Save'});

		const toggle = this.page.getByRole('switch', {
			name: 'Activate Object',
		});

		await expect(saveButton).toBeEnabled();

		const toggled = await toggle.isChecked();

		await toggle.click();

		await expect(toggle).toBeChecked({checked: !toggled});

		const saveResponse = this.page.waitForResponse(
			(response) =>
				response
					.url()
					.includes(
						'/o/object-admin/v1.0/object-definitions/by-external-reference-code/'
					) && response.request().method() === 'PUT'
		);

		const reload = this.page.waitForEvent('load', {timeout: 10000});

		await saveButton.click();

		await saveResponse;

		await reload;
	}

	async clickEditObjectDefinitionLink(
		objectDefinitionLabel: string,
		placeholder?: string
	) {
		const input = this.page
			.locator('.management-bar')
			.getByRole('searchbox', {
				name: placeholder ?? 'Search',
			});

		await input.fill(objectDefinitionLabel);

		await waitForSearchToBeReady(this.page);

		await this._submitSearch(objectDefinitionLabel);

		await this.page
			.getByRole('link', {exact: true, name: objectDefinitionLabel})
			.click();
	}

	async clickObjectDefinitionActionButton(objectDefinitionLabel: string) {
		await this.page
			.getByRole('row', {name: objectDefinitionLabel})
			.getByRole('button')
			.click();
	}

	async createObjectFolder(objectFolderLabel: string): Promise<ObjectFolder> {
		await this.addObjectFolderButton.click();
		await this.objectFolderLabelInput.click();
		await this.objectFolderLabelInput.fill(objectFolderLabel);

		const responsePromise = this.page.waitForResponse('**/object-folders');
		await this.createObjectFolderButton.click();
		const response = await responsePromise;

		return response.json();
	}

	async deleteObjectDefinition(label: string, name: string) {
		await this.clickObjectDefinitionActionButton(label);

		await this.deleteObjectDefinitionOption.click();

		const modal = this.page.getByRole('dialog');

		await modal.getByRole('textbox').fill(name);

		const reloadResponse = this.page.waitForResponse(
			(response) =>
				response
					.url()
					.includes('/o/object-admin/v1.0/object-definitions?') &&
				response.request().method() === 'GET' &&
				response.status() === 200
		);

		await modal.getByRole('button', {exact: true, name: 'Delete'}).click();

		const response = await reloadResponse;

		await response.finished();
	}

	async deleteObjectFolder(objectFolderName: string) {
		await this.objectFolderDeleteFolderOption.click();
		await this.confirmObjectFolderNameInput.click();
		await this.confirmObjectFolderNameInput.fill(objectFolderName);
		await this.deleteObjectFolderButton.click();
	}

	async exportObjectDefinition(objectDefinitionLabel: string) {
		await this.goto();

		await this.searchInput.fill(objectDefinitionLabel);

		await waitForSearchToBeReady(this.page);

		await this._submitSearch(objectDefinitionLabel);

		const downloadPromise = this.page.waitForEvent('download');

		const row = this.page.getByRole('row', {name: objectDefinitionLabel});

		await row.getByRole('button', {name: 'Actions'}).click();

		await this.exportObjectDefinitionOption.click();

		const download = await downloadPromise;

		const filePath = path.join(getTempDir(), download.suggestedFilename());

		await download.saveAs(filePath);

		const content = await readFile(filePath, 'utf-8');

		return {content, filePath, jsonContent: JSON.parse(content)};
	}

	getObjectFolderCardHeaderERC = (objectFolderERC: string) => {
		return this.objectFolderCardHeader
			.getByRole('strong')
			.filter({hasText: objectFolderERC});
	};

	getObjectFolderCardHeaderLabel = (objectFolderLabel: string) => {
		return this.objectFolderCardHeader
			.locator('span')
			.filter({hasText: objectFolderLabel})
			.first();
	};

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await gotoWithRetry(
			this.page,
			`/group${siteUrl || '/guest'}${PORTLET_URLS.objects}`,
			{waitUntil: 'load'}
		);
	}

	async importObjectDefinition(
		filePath: string,
		objectName: string
	): Promise<number> {
		await this.goto();

		await this.objectFolderActions.click();

		await this.importObjectDefinitionOption.click();

		const internalName = objectName.replace(/ /g, '');

		await this.page.getByLabel('Name').fill(internalName);

		await this.hiddenFileInput.setInputFiles(filePath);

		const responsePromise = this.page.waitForResponse(
			(response: Response) =>
				response
					.url()
					.includes('/o/object-admin/v1.0/object-definitions') &&
				response.request().method() === 'GET' &&
				response.status() === 200
		);

		await this.page
			.getByRole('button', {exact: true, name: 'Import'})
			.click();

		const response = await responsePromise;

		await this.page
			.locator('.modal-body')
			.waitFor({state: 'hidden', timeout: 10000});

		await expect(
			this.page.locator('.alert-danger', {
				hasText: 'The object definition failed to import.',
			})
		).toBeHidden();

		const {items} = await response.json();

		const imported = items.find(
			(item: {id: number; name: string}) => item.name === internalName
		);

		return imported?.id;
	}

	async openObjectFolder(
		objectFolderLabel: string,
		options: {timeout?: number} = {}
	) {
		await this.page
			.getByRole('listitem')
			.filter({hasText: objectFolderLabel})
			.click({timeout: options?.timeout});
	}

	private async _submitSearch(objectDefinitionLabel: string) {
		await waitForFDS({page: this.page});

		const searchRequestSettled = new Promise<void>((resolve) => {
			const settle = (request: Request) => {
				const url = new URL(request.url());

				if (
					request.method() !== 'GET' ||
					!url.pathname.endsWith(
						'/o/object-admin/v1.0/object-definitions'
					) ||
					url.searchParams.get('search') !== objectDefinitionLabel
				) {
					return;
				}

				this.page.off('requestfailed', settle);
				this.page.off('requestfinished', settle);

				resolve();
			};

			this.page.on('requestfailed', settle);
			this.page.on('requestfinished', settle);
		});

		await this.page.keyboard.press('Enter');

		await searchRequestSettled;
	}
}
