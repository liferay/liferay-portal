/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {openFieldset} from '../../../../utils/openFieldset';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {JournalPage} from './JournalPage';

const JOURNAL_NAMESPACE = '_com_liferay_journal_web_portlet_JournalPortlet_';

export class JournalEditFolderPage {
	readonly defaultWorkflowRadio: Locator;
	readonly journalPage: JournalPage;
	readonly page: Page;
	readonly parentFolderWorkflowRadio: Locator;
	readonly saveButton: Locator;
	readonly structureRestricions: Locator;
	readonly structureWorkflowRadio: Locator;
	readonly title: Locator;

	constructor(page: Page) {
		this.defaultWorkflowRadio = page.getByLabel(
			"Set the default workflow for the folder's content"
		);
		this.journalPage = new JournalPage(page);
		this.page = page;
		this.parentFolderWorkflowRadio = page.getByLabel(
			'Inherit allowed structures and workflows from the parent folder'
		);
		this.saveButton = page.getByRole('button', {exact: true, name: 'Save'});
		this.structureRestricions = page.getByRole('button', {
			name: 'Structure Restrictions and',
		});
		this.structureWorkflowRadio = page.getByLabel(
			"Set the allowed structures and workflows for the folder's content"
		);
		this.title = page.getByLabel('Name Required');
	}

	async goto({
		folderId,
		groupId,
		siteUrl,
	}: {
		folderId: number;
		groupId: number;
		siteUrl: Site['friendlyUrlPath'];
	}) {
		await this.page.goto(
			`/group${siteUrl}${PORTLET_URLS.journal}&${JOURNAL_NAMESPACE}mvcPath=%2Fedit_folder.jsp&${JOURNAL_NAMESPACE}folderId=${folderId}&${JOURNAL_NAMESPACE}groupId=${groupId}`
		);

		await openFieldset(this.page, 'Structure Restrictions and Workflow');
	}

	async editFolder(title: string) {
		await this.journalPage.goToJournalFolderAction('Edit', title);

		await this.page.waitForURL(/folderId/);
	}

	async gotToPermission(title: string) {
		await this.journalPage.goToJournalFolderAction('Permissions', title);
	}

	async save() {
		await this.saveButton.click();

		await waitForAlert(this.page);
	}

	async setWorkflow(workflowName: string) {
		await this.defaultWorkflowRadio.check();

		await this.page
			.getByRole('group', {name: 'Structure Restrictions and Workflow'})
			.getByRole('combobox')
			.selectOption({label: workflowName});

		await this.save();
	}

	async setStructureWorkflow(structureName: string, workflowName: string) {
		await this.structureWorkflowRadio.check();

		await this.page.getByRole('button', {name: 'Choose Structure'}).click();

		await this.page
			.frameLocator('iframe[title="Structures"]')
			.getByRole('cell', {exact: true, name: structureName})
			.click();

		await this.page
			.getByRole('row')
			.filter({hasText: structureName})
			.getByRole('combobox')
			.selectOption({label: workflowName});

		await this.save();
	}

	async useParentFolderWorkflow() {
		await this.parentFolderWorkflowRadio.check();

		await this.save();
	}
}
