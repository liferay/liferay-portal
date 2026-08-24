/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {ApiHelpers} from '../../helpers/ApiHelpers';
import {clickAndExpectToBeHidden} from '../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../utils/getRandomString';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';
import {PageEditorPage} from '../layout-content-page-editor-web/PageEditorPage';

export class DisplayPageTemplatesPage {
	readonly page: Page;

	readonly apiHelpers: ApiHelpers;
	readonly newButton: Locator;
	readonly publishButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.apiHelpers = new ApiHelpers(page);
		this.newButton = page.getByText('New', {exact: true});
		this.publishButton = page.getByLabel('Publish', {exact: true});
	}

	/**
	 * Creates the default display page template of an item type and maps a
	 * Heading fragment to one of its fields, so an item of that type renders
	 * its own value at its friendly URL.
	 *
	 * The template is created and marked as default through the API. Only the
	 * fragment mapping goes through the page editor, because no API exposes it.
	 */
	async createDefaultTemplate({
		className,
		mappedField = 'Title',
		pageEditorPage,
		site,
	}: {
		className: string;
		mappedField?: string;
		pageEditorPage: PageEditorPage;
		site: Site;
	}) {
		const name = `DPT ${getRandomString()}`;

		const {classNameId} =
			await this.apiHelpers.jsonWebServicesClassName.fetchClassName(
				className
			);

		const {layoutPageTemplateEntryId} =
			await this.apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
				{
					classNameId,
					groupId: String(site.id),
					name,
				}
			);

		await this.apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
			{layoutPageTemplateEntryId}
		);

		await this.goto(site.friendlyUrlPath);

		await this.editTemplate(name);

		await pageEditorPage.addFragment(
			'Basic Components',
			'Heading',
			this.page.getByText('Drag and drop fragments or widgets here')
		);

		const headingId = await pageEditorPage.getFragmentId('Heading');

		await pageEditorPage.selectEditable(headingId, 'element-text');

		await pageEditorPage.changeConfiguration({
			fieldLabel: 'Field',
			tab: 'Mapping',
			value: mappedField,
		});

		await pageEditorPage.publishPage();

		return name;
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.displayPageTemplates}`
		);
	}

	async clickMoreActions(name: string, actionName: string) {
		await this.page
			.locator('.card-page-item')
			.filter({hasText: name})
			.getByLabel('More actions')
			.click();

		await this.page
			.getByRole('menuitem', {
				exact: true,
				name: actionName,
			})
			.click();
	}

	async copyTemplate(name: string) {
		await clickAndExpectToBeVisible({
			autoClick: false,
			target: this.page.getByRole('menuitem', {name: 'Make a Copy'}),
			trigger: this.page
				.locator('.card-page-item')
				.filter({hasText: name})
				.getByLabel('More actions'),
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page
				.getByText('Display Page', {exact: true})
				.filter({visible: true}),
			timeout: 5000,
			trigger: this.page.getByRole('menuitem', {name: 'Make a Copy'}),
		});

		await waitForAlert(this.page);
	}

	async deleteTemplate(name: string) {
		await this.clickMoreActions(name, 'Delete');

		await this.page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(this.page, 'Success:');
	}

	async deleteAllDisplayPageTemplates() {
		await expect(
			this.page.getByLabel('Search for', {exact: true})
		).toBeEnabled();

		await this.page
			.getByLabel('Select All Items on the Page')
			.setChecked(true);

		await this.page.getByRole('button', {name: 'Delete'}).click();

		const deleteEntriesModal = this.page.getByRole('dialog', {
			name: 'Delete Entries',
		});

		await deleteEntriesModal.waitFor();

		await deleteEntriesModal.getByRole('button', {name: 'Delete'}).click();
	}

	async editTemplate(name: string) {
		await this.clickMoreActions(name, 'Edit');

		await this.page
			.getByText('Select a Page Element', {exact: true})
			.waitFor();
	}

	async viewUsages(name: string) {
		await this.clickMoreActions(name, 'View Usages');

		await clickAndExpectToBeVisible({
			target: this.page.getByRole('row').getByRole('checkbox').first(),

			trigger: this.page.getByRole('menuitem', {
				exact: true,
				name: 'View Usages',
			}),
		});
	}

	async mapConfiguration({
		field,
		mappingField,
	}: {
		field: string;
		mappingField: string;
	}) {
		await this.page
			.locator('.form-group')
			.filter({has: this.page.getByLabel(field, {exact: true})})
			.getByTitle('Map', {exact: true})
			.click();

		await this.page
			.getByLabel('Field', {exact: true})
			.selectOption(mappingField);

		await this.page
			.locator('.dpt-mapping-panel')
			.getByRole('button')
			.click();

		await this.saveConfiguration();
	}

	async markAsDefault(name: string) {
		this.page.once('dialog', (dialog) => {
			dialog.accept().catch(() => {});
		});

		await this.clickMoreActions(name, 'Mark as Default');

		await waitForAlert(this.page);
	}

	async renameTemplate(newName: string, oldName: string) {
		await this.clickMoreActions(oldName, 'Rename');

		await this.page.getByLabel('Name', {exact: true}).fill(newName);

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(this.page);
	}

	async saveConfiguration() {
		await this.page
			.getByRole('button', {exact: true, name: 'Save'})
			.click();

		await waitForAlert(
			this.page,
			'Success:The page was updated successfully.'
		);
	}

	async changePreviewItem(title: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {
				name: 'Select Other Item',
			}),
			trigger: this.page.getByLabel('Preview With'),
		});

		const folderCard = this.page
			.frameLocator('iframe[title="Select"]')
			.getByRole('link', {name: 'Animals'});

		const articleCard = this.page
			.frameLocator('iframe[title="Select"]')
			.getByText(title, {exact: false});

		await clickAndExpectToBeVisible({
			target: articleCard,
			trigger: folderCard,
		});

		await clickAndExpectToBeHidden({
			target: this.page.locator('.modal-dialog'),
			trigger: articleCard,
		});
	}

	async createFolder(name: string, description?: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Folder'}),
			trigger: this.newButton,
		});

		await this.page.locator('.modal-body').waitFor();

		await this.page.getByLabel('Name').fill(name);

		if (description) {
			await this.page.getByLabel('Description').fill(description);
		}

		await this.page.getByRole('button', {name: 'Create'}).click();
		await waitForAlert(this.page);
	}

	async copyFolderTo(sourceName: string, targetName: string) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: this.page.getByRole('menuitem', {name: 'Copy To'}),
			trigger: this.page
				.locator('.card-page-item')
				.filter({hasText: sourceName})
				.getByLabel('More actions'),
		});

		const frameLocator = this.page.frameLocator('iframe');

		await frameLocator
			.getByRole('treeitem', {name: 'Home'})
			.locator('.component-expander')
			.click();

		await frameLocator.getByRole('treeitem', {name: targetName}).click();
	}

	async createTemplate({
		contentSubtype,
		contentType,
		folderName,
		name,
	}: {
		contentSubtype?: string;
		contentType: string;
		folderName?: string;
		name: string;
	}) {
		if (folderName) {
			await this.page.getByRole('link', {name: folderName}).click();

			await this.page.getByText(folderName, {exact: true}).waitFor();

			await clickAndExpectToBeVisible({
				target: this.page
					.locator('.breadcrumb-item')
					.getByText(folderName),
				trigger: this.page.getByRole('link', {name: folderName}),
			});
		}

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: this.page.getByRole('menuitem', {
				name: 'Display Page Template',
			}),
			trigger: this.newButton,
		});

		await this.page
			.getByRole('menuitem', {
				name: 'Display Page Template',
			})
			.click();

		await this.page.getByRole('button', {name: 'Blank'}).click();
		await this.page.getByLabel('Name', {exact: true}).fill(name);

		await this.page
			.getByLabel('Content Type')
			.selectOption({label: contentType});

		if (contentSubtype) {
			await this.page
				.getByLabel('Subtype')
				.selectOption({label: contentSubtype});
		}

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			this.page,
			'Success:The display page template was created successfully.'
		);

		await this.publishTemplate();
	}

	async publishTemplate() {
		await this.publishButton.waitFor();
		await this.publishButton.click();

		await waitForAlert(
			this.page,
			'Success:The display page template was published successfully.'
		);
	}
}
