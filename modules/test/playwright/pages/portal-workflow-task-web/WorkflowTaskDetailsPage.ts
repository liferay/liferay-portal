/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page} from '@playwright/test';

import {waitForAlert} from '../../utils/waitForAlert';
import {WorkflowTasksPage} from './WorkflowTasksPage';

export class WorkflowTaskDetailsPage {
	readonly activitiesButton: Locator;
	readonly approveMenuItem: Locator;
	readonly assignToDialogIFRAME: FrameLocator;
	readonly assignToMenuItem: Locator;
	readonly assignToSingleSelect: Locator;
	readonly assigneeDoneButton: Locator;
	readonly cancelButton: Locator;
	readonly commentsButton: Locator;
	readonly commentsTextbox: Locator;
	readonly detailsMessage: Locator;
	readonly doneButton: Locator;
	readonly editAssetButton: Locator;
	readonly page: Page;
	readonly previewMessageBoards: Locator;
	readonly rejectMenuItem: Locator;
	readonly replyButton: Locator;
	readonly reviewActionMenu: Locator;
	readonly reviewComment: Locator;
	readonly subscribeButton: Locator;
	readonly workflowTasksPage: WorkflowTasksPage;

	constructor(page: Page) {
		this.activitiesButton = page.getByRole('button', {name: 'Activities'});
		this.approveMenuItem = page.getByRole('menuitem', {name: 'approve'});
		this.assignToDialogIFRAME = page.frameLocator(
			'iframe[name="_com_liferay_portal_workflow_task_web_portlet_MyWorkflowTaskPortlet_assignToDialog_iframe_"]'
		);
		this.assignToMenuItem = page.getByRole('link', {name: 'Assign to...'});
		this.assignToSingleSelect =
			this.assignToDialogIFRAME.getByLabel('Assign to');
		this.assigneeDoneButton = this.assignToDialogIFRAME.getByRole(
			'button',
			{name: 'Done'}
		);
		this.commentsButton = page.getByRole('button', {name: 'Comments'});
		this.cancelButton = page.getByRole('button', {name: 'Cancel'});
		this.detailsMessage = page.getByLabel(
			'Ask a user to work on the item.'
		);
		this.doneButton = page.getByRole('button', {name: 'Done'});
		this.editAssetButton = page.locator('[title="Edit"]');
		this.page = page;
		this.previewMessageBoards = page.getByRole('button', {
			name: 'Preview of Message Boards',
		});
		this.rejectMenuItem = page.getByRole('menuitem', {name: 'reject'});
		this.replyButton = page.getByRole('button', {name: 'Reply'});
		this.reviewActionMenu = page.locator(
			'[id="_com_liferay_portal_workflow_task_web_portlet_MyWorkflowTaskPortlet_kldx___menu"]'
		);
		this.reviewComment = page.getByRole('textbox', {name: 'Comment'});
		this.subscribeButton = page.getByLabel('Subscribe to Comments');
		this.commentsTextbox = page.frameLocator('iframe').getByRole('textbox');
		this.workflowTasksPage = new WorkflowTasksPage(page);
	}

	async addComment(comment: string) {
		await this.commentsButton.click();
		await this.commentsTextbox.fill(comment);
		await this.replyButton.click();
	}

	async clickDoneButton() {
		await this.doneButton.click();

		await waitForAlert(this.page);
	}

	async goTo(assetTitle: string) {
		await this.workflowTasksPage.goto();

		await this.selectAsset(assetTitle);
	}

	async selectAsset(assetTitle: string) {
		const assetLink = this.page.getByRole('link', {name: assetTitle});
		await assetLink.click({force: true});
	}

	async selectAssignee(assignee: string) {
		await this.assignToSingleSelect.selectOption(assignee);
	}

	async writeTaskComment(threadTitle: string, comment: string) {
		await this.selectAsset(threadTitle);

		await this.addComment(comment);
	}
}
