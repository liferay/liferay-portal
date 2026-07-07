/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../utils/getRandomInt';
import {waitForAlert} from '../../utils/waitForAlert';

export class EditDigitalSalesRoomPage {
	readonly cancelButton: Locator;
	readonly commentActionsButton: Locator;
	readonly commentDeleteButton: Locator;
	readonly commentEditButton: Locator;
	readonly commentEditSaveButton: Locator;
	readonly commentSaveButton;
	readonly commentsButton: Locator;
	readonly commentTextarea: Locator;
	readonly contributorRoleInputButton: Locator;
	readonly contributorRoleMenuItemButton: Locator;
	readonly documentCard: (documentName: string) => Locator;
	readonly documentGalleryCard: Locator;
	readonly documentGalleryCardBadge: Locator;
	readonly documentGalleryCardIcon: Locator;
	readonly documentGalleryCardTitle: Locator;
	readonly documentsMenuItem: Locator;
	readonly editCommentTextarea: Locator;
	readonly fileUploadButton: Locator;
	readonly fragmentImage: Locator;
	readonly friendlyURLInput: Locator;
	readonly newButton: Locator;
	readonly nextButton: Locator;
	readonly noDocumentsMessage: Locator;
	readonly onboardingMenuItem: Locator;
	readonly page: Page;
	readonly pageEditor: Locator;
	readonly publishButton: Locator;
	readonly replyButton: Locator;
	readonly roleKeyButton: Locator;
	readonly roomCollaboratorRoleInputButton: Locator;
	readonly roomCommentsText: Locator;
	readonly roomNameInput: Locator;
	readonly saveButton: Locator;
	readonly selectAccountInput: Locator;
	readonly selectFileButton: Locator;
	readonly selectImageAddButton: Locator;
	readonly selectImageButton: Locator;
	readonly selectImageFileInput: Locator;
	readonly selectImageFrame: FrameLocator;
	readonly selectOption: (value: string) => Locator;
	readonly templatePreviewFrame: FrameLocator;
	readonly viewerRoleInputButton: Locator;

	constructor(page: Page) {
		this.cancelButton = page.getByRole('button', {
			exact: true,
			name: 'Cancel',
		});
		this.commentActionsButton = page.getByRole('button', {name: 'Actions'});
		this.commentDeleteButton = page.getByRole('menuitem', {name: 'Delete'});
		this.commentEditButton = page.getByRole('menuitem', {name: 'Edit'});
		this.commentEditSaveButton = page
			.getByRole('button', {name: 'Save'})
			.nth(1);
		this.commentSaveButton = page.getByRole('button', {name: 'Save'});
		this.commentsButton = page.getByLabel('Comments', {exact: true});
		this.commentTextarea = page.getByRole('textbox', {
			name: 'Add comment.',
		});
		this.contributorRoleInputButton = page.locator(
			'[data-testid="roleKeyItem_Content Contributor"]'
		);
		this.contributorRoleMenuItemButton = page.locator(
			'[data-testid="memberRoleKeyItem_Content Contributor"]'
		);
		this.documentCard = (documentName: string) =>
			page.locator('.card-title', {hasText: documentName});
		this.documentGalleryCard = page.locator('.dsr-document-card');
		this.documentGalleryCardBadge = this.documentGalleryCard.locator(
			'.dsr-document-badge'
		);
		this.documentGalleryCardIcon = this.documentGalleryCard.locator(
			'.dsr-document-icon svg'
		);
		this.documentGalleryCardTitle = this.documentGalleryCard.locator(
			'.dsr-document-title'
		);
		this.documentsMenuItem = page.getByRole('menuitem', {
			name: 'Documents',
		});
		this.editCommentTextarea = page
			.getByRole('textbox', {name: 'Add comment.'})
			.nth(1);
		this.fileUploadButton = page.getByRole('menuitem', {
			name: 'File Upload',
		});
		this.fragmentImage = page.locator('#page-editor img').first();
		this.friendlyURLInput = page.getByLabel('Friendly URL');
		this.newButton = page.getByRole('button', {name: 'New'});
		this.nextButton = page.getByRole('button', {name: 'Next'});
		this.noDocumentsMessage = page.getByText(
			'There are no documents or media files in this folder.'
		);
		this.onboardingMenuItem = page.getByRole('menuitem', {
			name: 'Onboarding',
		});
		this.page = page;
		this.pageEditor = page.locator('#page-editor');
		this.publishButton = page.getByRole('button', {name: 'Publish'});
		this.replyButton = page.getByRole('button', {name: 'reply'});
		this.roleKeyButton = page.locator('[data-testid="roleKeyButton"]');
		this.roomCollaboratorRoleInputButton = page.locator(
			'[data-testid="roleKeyItem_Room Collaborator"]'
		);
		this.roomCommentsText = page.getByText('Room Comments');
		this.roomNameInput = page.getByLabel('Room Name');
		this.saveButton = page.getByRole('button', {name: 'Save'});
		this.selectAccountInput = page.getByRole('combobox', {
			name: 'Select Account',
		});
		this.selectFileButton = page.getByRole('button', {name: 'Select File'});
		this.selectImageFrame = page.frameLocator('iframe[title="Select"]');
		this.selectImageAddButton = this.selectImageFrame.getByRole('button', {
			name: 'Add',
		});
		this.selectImageButton = page.getByRole('button', {
			name: 'Select Image',
		});
		this.selectImageFileInput =
			this.selectImageFrame.locator('input[type="file"]');
		this.selectOption = (value: string) =>
			page.getByRole('option', {name: value});
		this.templatePreviewFrame = page
			.getByLabel('Create New Digital Sales Room')
			.frameLocator('iframe');
		this.viewerRoleInputButton = page.locator(
			'[data-testid="roleKeyItem_Viewer"]'
		);
	}

	async uploadDocument(filePath: string) {
		await clickAndExpectToBeVisible({
			target: this.newButton,
			trigger: this.documentsMenuItem,
		});
		await clickAndExpectToBeVisible({
			target: this.fileUploadButton,
			trigger: this.newButton,
		});
		await clickAndExpectToBeVisible({
			target: this.selectFileButton,
			trigger: this.fileUploadButton,
		});

		const fileChooserPromise = this.page.waitForEvent('filechooser');

		await this.selectFileButton.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(filePath);

		await this.publishButton.click();

		await waitForAlert(this.page);
	}

	async uploadFragmentImage(filePath: string) {
		await this.fragmentImage.click();
		await this.fragmentImage.click();

		await this.selectImageButton.click();

		await this.selectImageFileInput.setInputFiles(filePath);

		await this.selectImageAddButton.click();

		await this.publishButton.click();
	}

	async addDigitalSalesRoom({
		accountName,
		friendlyURL = '',
		roomName = `A${getRandomInt()}`,
		templateName = 'DSR',
	}: {
		accountName?: string;
		friendlyURL?: string;
		roomName?: string;
		templateName?: string;
	}) {
		await expect(this.selectAccountInput).toBeEnabled();

		await this.selectAccountInput.click();
		await this.selectOption(accountName).click();

		await expect(this.selectAccountInput).toHaveValue(accountName);

		await this.nextButton.click();

		await this.page.getByText(templateName, {exact: true}).click();

		await expect(
			this.templatePreviewFrame.getByRole('heading', {name: 'WELCOME'})
		).toBeVisible();

		await this.nextButton.click();

		await this.roomNameInput.fill(roomName);
		await this.friendlyURLInput.fill(friendlyURL);

		await this.saveButton.click();

		await expect(
			this.page.getByRole('heading', {name: 'Onboarding'})
		).toBeVisible();
	}

	async addDigitalSalesRoomComment(comment: string) {
		await this.commentsButton.click();

		await expect(this.roomCommentsText).toBeVisible();

		await this.commentTextarea.fill(comment);

		await expect(this.commentSaveButton).not.toBeDisabled();

		await this.commentSaveButton.click();

		await waitForAlert(this.page, 'Success:Your comment has been posted.');
	}
}
