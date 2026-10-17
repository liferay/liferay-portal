/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {openFieldset} from '../../utils/openFieldset';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';

const DL_ADMIN_NAMESPACE =
	'_com_liferay_document_library_web_portlet_DLAdminPortlet_';

export class DocumentLibraryEditFolderPage {
	readonly page: Page;

	readonly defaultWorkflowRadio: Locator;
	readonly documentTypeWorkflowRadio: Locator;
	readonly parentFolderWorkflowRadio: Locator;
	readonly saveButton: Locator;
	readonly title: Locator;

	constructor(page: Page) {
		this.page = page;

		this.defaultWorkflowRadio = page.getByLabel(
			"Set the default workflow for the folder's content"
		);
		this.documentTypeWorkflowRadio = page.getByLabel(
			'Define Specific Document Type Restrictions and Workflow'
		);
		this.parentFolderWorkflowRadio = page.getByLabel(
			'Use Document Type Restrictions and Workflow of the Parent Folder'
		);
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.title = page.getByLabel('Name Required');
	}

	async goto({
		folderId,
		repositoryId,
		siteUrl,
	}: {
		folderId: number;
		repositoryId: number;
		siteUrl: Site['friendlyUrlPath'];
	}) {
		await this.page.goto(
			`/group${siteUrl}${PORTLET_URLS.documentLibrary}&${DL_ADMIN_NAMESPACE}mvcRenderCommandName=%2Fdocument_library%2Fedit_folder&${DL_ADMIN_NAMESPACE}folderId=${folderId}&${DL_ADMIN_NAMESPACE}repositoryId=${repositoryId}`
		);

		await this._openWorkflowFieldset();
	}

	async fillTitle(name: string) {
		await this.title.fill(name);
	}

	async publishNewFolder(name: string) {
		await this.fillTitle(name);
		await this.saveButton.click();
	}

	async save() {
		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async setDocumentTypeWorkflow(
		documentTypeName: string,
		workflowName: string
	) {
		await this._openWorkflowFieldset();

		await this.documentTypeWorkflowRadio.check();

		await this.page
			.getByRole('link', {name: 'Select Document Type'})
			.click();

		await this.page
			.frameLocator('iframe[title="Document Types"]')
			.getByRole('definition')
			.filter({hasText: documentTypeName})
			.getByRole('button', {name: 'Choose'})
			.click();

		await this.page
			.getByRole('row')
			.filter({hasText: documentTypeName})
			.getByRole('combobox')
			.selectOption({label: workflowName});

		await Promise.all([
			this.page.waitForEvent('dialog').then((dialog) => dialog.accept()),
			this.save(),
		]);
	}

	async setWorkflow(workflowName: string) {
		await this._openWorkflowFieldset();

		await this.defaultWorkflowRadio.check();

		await this.page
			.getByLabel('Default Workflow for all Document Types')
			.selectOption({label: workflowName});

		await this.save();
	}

	async useParentFolderWorkflow() {
		await this._openWorkflowFieldset();

		await this.parentFolderWorkflowRadio.check();

		await this.save();
	}

	private async _openWorkflowFieldset() {
		await openFieldset(
			this.page,
			'Document Type Restrictions and Workflow'
		);
	}
}
