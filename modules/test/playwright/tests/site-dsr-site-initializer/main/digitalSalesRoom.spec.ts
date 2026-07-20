/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {digitalSalesRoomPagesTest} from '../../../fixtures/digitalSalesRoomPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {liferayConfig} from '../../../liferay.config';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	dataApiHelpersTest,
	digitalSalesRoomPagesTest,
	featureFlagsTest({
		'LPD-35443': {enabled: true},
		'LPD-66359': {enabled: true},
	}),
	loginTest()
);

test.afterEach(async ({apiHelpers}) => {
	const rooms = await apiHelpers.headlessDigitalSalesRoom.getRooms();

	for (const room of rooms.items) {
		await apiHelpers.headlessDigitalSalesRoom.deleteRoom(room.id);
	}
});

test(
	'Create a digital sales room',
	{tag: '@LPD-69509'},
	async ({apiHelpers, digitalSalesRoomsPage, editDigitalSalesRoomPage}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.cell(roomName, false)
		).toBeVisible();

		await expect(digitalSalesRoomsPage.roomLink(roomName)).toHaveAttribute(
			'href',
			/view_room/
		);

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.cell(roomName, false)
		).toBeVisible();

		await expect(digitalSalesRoomsPage.roomLink(roomName)).toHaveAttribute(
			'href',
			/view_room/
		);
	}
);

test(
	'View a digital sales room',
	{tag: '@LPD-69528'},
	async ({
		apiHelpers,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		await digitalSalesRoomsPage.clickRowActionsMenuItem(
			roomName,
			digitalSalesRoomsPage.viewMenuItem
		);

		await expect(page.locator('.page-editor__sidebar')).not.toBeVisible();
	}
);

test(
	'Update a digital sales room name, ERC and friendly URL from settings',
	{tag: ['@LPD-94454', '@LPD-97477']},
	async ({
		apiHelpers,
		digitalSalesRoomSettingsPage,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const roomName = `A${getRandomInt()}`;
		const updatedExternalReferenceCode = `erc${getRandomInt()}`;
		const updatedFriendlyURL = `furl${getRandomInt()}`;
		const updatedName = `B${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		await digitalSalesRoomsPage.clickRowActionsMenuItem(
			roomName,
			digitalSalesRoomsPage.settingsMenuItem
		);

		await expect(digitalSalesRoomSettingsPage.nameInput).toBeVisible();

		await expect(
			digitalSalesRoomSettingsPage.externalReferenceCodeInput
		).toHaveAttribute('required');
		await expect(
			digitalSalesRoomSettingsPage.friendlyURLInput
		).toHaveAttribute('required');

		await digitalSalesRoomSettingsPage.updateRoomSettings({
			externalReferenceCode: updatedExternalReferenceCode,
			friendlyURL: updatedFriendlyURL,
			name: updatedName,
		});

		await waitForAlert(page);

		await digitalSalesRoomsPage.clickRowActionsMenuItem(
			updatedName,
			digitalSalesRoomsPage.settingsMenuItem
		);

		await expect(
			digitalSalesRoomSettingsPage.externalReferenceCodeInput
		).toHaveValue(updatedExternalReferenceCode);
		await expect(digitalSalesRoomSettingsPage.friendlyURLInput).toHaveValue(
			updatedFriendlyURL
		);
		await expect(digitalSalesRoomSettingsPage.nameInput).toHaveValue(
			updatedName
		);
		await expect(digitalSalesRoomSettingsPage.siteIdValue).toBeVisible();
	}
);

test(
	'Edit a digital sales room',
	{tag: '@LPD-69528'},
	async ({
		apiHelpers,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		await digitalSalesRoomsPage.clickRowActionsMenuItem(
			roomName,
			digitalSalesRoomsPage.editMenuItem
		);

		await expect(page.locator('.page-editor__sidebar')).toBeVisible();
	}
);

test(
	'Add Document Gallery Block and configure a document card',
	{tag: '@LPD-92373'},
	async ({
		apiHelpers,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const documentTitle = `Doc${getRandomInt()}`;
		const fileName = `${documentTitle}.png`;
		const filePath = path.join(__dirname, 'dependencies', 'document1.png');
		const roomName = `A${getRandomInt()}`;

		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		const pageEditorPage = new PageEditorPage(page);

		await page.goto(`/web/${roomName}/onboarding?p_l_mode=edit`);

		const groupId = await page.evaluate(() =>
			Liferay.ThemeDisplay.getScopeGroupId()
		);

		await apiHelpers.headlessDelivery.postDocument(
			groupId,
			createReadStream(filePath),
			{fileName, title: documentTitle}
		);

		await pageEditorPage.addFragment(
			'Digital Sales Room',
			'Document Gallery Block'
		);

		const fragmentId = await pageEditorPage.getFragmentId(
			'Document Gallery Block'
		);

		await pageEditorPage.selectFragment(fragmentId);
		await pageEditorPage.goToConfigurationTab('General');

		const generalPanel = page.getByRole('tabpanel', {name: 'General'});

		await generalPanel
			.getByRole('button', {name: 'Select Document 1'})
			.click();
		const selectFromModalItem = page.getByRole('menuitem', {
			name: 'Select Document 1',
		});

		await selectFromModalItem.click({timeout: 2000}).catch(() => {});
		await page
			.frameLocator('iframe[title="Select"]')
			.getByText(documentTitle, {exact: true})
			.click();

		await expect(
			generalPanel.getByRole('textbox', {name: 'Document 1'})
		).toHaveValue(documentTitle);

		await pageEditorPage.publishPage();

		await page.goto(`/web/${roomName}/onboarding`);

		await expect(
			editDigitalSalesRoomPage.documentGalleryCard
		).toBeVisible();
		await expect(
			editDigitalSalesRoomPage.documentGalleryCardBadge
		).toHaveText('PNG');
		await expect(
			editDigitalSalesRoomPage.documentGalleryCardIcon
		).toBeVisible();
		await expect(
			editDigitalSalesRoomPage.documentGalleryCardTitle
		).toHaveText(documentTitle);

		const response = await page.request.get(
			(await editDigitalSalesRoomPage.documentGalleryCard.getAttribute(
				'href'
			)) ?? ''
		);

		expect(response.status()).toBe(200);
		expect(response.headers()['content-type']).toContain('image');
	}
);

test(
	'Delete a digital sales room',
	{tag: ['@LPD-73577', '@LPD-97748']},
	async ({
		apiHelpers,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomActionsMenu(roomName);

		await digitalSalesRoomsPage.deleteMenuItem.click();

		await expect(
			digitalSalesRoomsPage.deleteConfirmationModal
		).toBeVisible();

		await digitalSalesRoomsPage.deleteButton.click();

		await waitForAlert(page);

		await expect(digitalSalesRoomsPage.noResultsFoundMessage).toBeVisible();
	}
);

test(
	'Room archival and deletion should both be available simultaneously to a user with the appropriate permissions',
	{tag: '@LPD-97748'},
	async ({apiHelpers, digitalSalesRoomsPage, page}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const roomName = `A${getRandomInt()}`;

		const room = await apiHelpers.headlessDigitalSalesRoom.addRoom({
			accountEntryId: account.id,
			name: roomName,
		});

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			'L_DSR_SELLER',
			userAccount.id
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[userAccount.emailAddress]
		);

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: `DSR Room Viewer ${getRandomInt()}`,
		});

		await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
			role.id,
			Number(userAccount.id)
		);

		apiHelpers.data.push({
			id: `${role.id}_${userAccount.id}`,
			type: 'roleUserAccountAssociation',
		});

		const grantRoomPermissions = (actionIds: string[]) =>
			apiHelpers.objectEntry.putObjectEntryPermissions(
				'digital-sales-room/rooms',
				room.id,
				[{actionIds, roleName: role.name}]
			);

		await grantRoomPermissions(['VIEW']);

		await performUserSwitch(page, userAccount.alternateName);

		await digitalSalesRoomsPage.goToRoomActionsMenu(roomName, true);

		await performUserSwitch(page, 'test');

		await grantRoomPermissions(['UPDATE', 'VIEW']);

		await performUserSwitch(page, userAccount.alternateName);

		await digitalSalesRoomsPage.goToRoomActionsMenu(roomName);

		await expect(digitalSalesRoomsPage.archiveMenuItem).toBeVisible();
		await expect(digitalSalesRoomsPage.deleteMenuItem).toBeHidden();
		await expect(digitalSalesRoomsPage.viewMenuItem).toBeVisible();

		await performUserSwitch(page, 'test');

		await grantRoomPermissions(['DELETE', 'UPDATE', 'VIEW']);

		await performUserSwitch(page, userAccount.alternateName);

		await digitalSalesRoomsPage.goToRoomActionsMenu(roomName);

		await expect(digitalSalesRoomsPage.archiveMenuItem).toBeVisible();
		await expect(digitalSalesRoomsPage.deleteMenuItem).toBeVisible();
		await expect(digitalSalesRoomsPage.viewMenuItem).toBeVisible();

		await performUserSwitch(page, 'test');
	}
);

test(
	'Invite external user and verify pending status then remove user',
	{tag: '@LPD-66359'},
	async ({
		apiHelpers,
		digitalSalesRoomUsersPage,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const accountName = `B${getRandomInt()}`;
		const email = `invited-${getRandomInt()}@liferay.com`;
		const roomName = `A${getRandomInt()}`;

		await apiHelpers.headlessAdminUser.postAccount({
			name: accountName,
			type: 'business',
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.cell(roomName, false)
		).toBeVisible();

		await digitalSalesRoomsPage.clickRowActionsMenuItem(
			roomName,
			digitalSalesRoomsPage.shareMenuItem
		);

		await expect(
			digitalSalesRoomUsersPage.userEmailAddressesInput
		).toBeVisible();

		await digitalSalesRoomUsersPage.userEmailAddressesInput.fill(email);
		await digitalSalesRoomUsersPage.userEmailAddressesInput.press('Enter');

		await digitalSalesRoomUsersPage.inviteButton.click();

		await waitForAlert(page, 'Success:User was invited successfully.');

		await expect(digitalSalesRoomUsersPage.userRow(email)).toBeVisible();
		await expect(
			digitalSalesRoomUsersPage.roleText(email, 'Viewer')
		).toBeVisible();

		await digitalSalesRoomUsersPage.removeUserButton(email).click();

		await waitForAlert(page, 'Success:User was removed successfully.');

		await expect(
			digitalSalesRoomUsersPage.userRow(email)
		).not.toBeVisible();
	}
);

test(
	'Share room via header share button',
	{tag: '@LPD-66359'},
	async ({
		apiHelpers,
		digitalSalesRoomUsersPage,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const accountName = `B${getRandomInt()}`;
		const roomName = `A${getRandomInt()}`;

		await apiHelpers.headlessAdminUser.postAccount({
			name: accountName,
			type: 'business',
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName,
			roomName,
		});

		await page.getByRole('button', {name: 'Publish'}).click();

		await expect(digitalSalesRoomUsersPage.shareButton).toBeVisible();

		await digitalSalesRoomUsersPage.shareButton.click();

		await expect(digitalSalesRoomUsersPage.shareModalHeading).toBeVisible();

		await expect(
			digitalSalesRoomUsersPage.shareModalEmailInput
		).toBeVisible();
	}
);

test(
	'Add comment',
	{tag: '@LPD-76076'},
	async ({
		apiHelpers,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		await digitalSalesRoomsPage.clickRowActionsMenuItem(
			roomName,
			digitalSalesRoomsPage.viewMenuItem
		);

		await expect(page.locator('.page-editor__sidebar')).not.toBeVisible();

		const comment = getRandomString();

		await editDigitalSalesRoomPage.addDigitalSalesRoomComment(comment);

		await expect(editDigitalSalesRoomPage.commentTextarea).toBeVisible();
		await expect(page.getByText('Test Test')).toBeVisible();
		await expect(page.getByText(comment)).toBeVisible();
	}
);

test(
	'Delete comment',
	{tag: '@LPD-76076'},
	async ({
		apiHelpers,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		await digitalSalesRoomsPage.clickRowActionsMenuItem(
			roomName,
			digitalSalesRoomsPage.viewMenuItem
		);

		const comment = getRandomString();

		await editDigitalSalesRoomPage.addDigitalSalesRoomComment(comment);

		await expect(
			editDigitalSalesRoomPage.commentActionsButton
		).toBeVisible();

		await editDigitalSalesRoomPage.commentActionsButton.click();
		await editDigitalSalesRoomPage.commentDeleteButton.click();

		await waitForAlert(page, 'Success:Your comment has been deleted.');

		await expect(page.getByText(comment)).not.toBeVisible();
	}
);

test(
	'Edit comment',
	{tag: '@LPD-76076'},
	async ({
		apiHelpers,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		await digitalSalesRoomsPage.clickRowActionsMenuItem(
			roomName,
			digitalSalesRoomsPage.viewMenuItem
		);

		const comment = getRandomString();

		await editDigitalSalesRoomPage.addDigitalSalesRoomComment(comment);

		await editDigitalSalesRoomPage.commentActionsButton.click();
		await editDigitalSalesRoomPage.commentEditButton.click();

		const comment2 = getRandomString();

		await editDigitalSalesRoomPage.editCommentTextarea.fill(comment2);
		await editDigitalSalesRoomPage.commentEditSaveButton.click();

		await waitForAlert(page, 'Success:Your comment has been edited.');

		await expect(page.getByText(comment)).not.toBeVisible();
		await expect(page.getByText(comment2)).toBeVisible();
	}
);

test(
	'Add reply to a comment',
	{tag: '@LPD-76076'},
	async ({
		apiHelpers,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		await digitalSalesRoomsPage.clickRowActionsMenuItem(
			roomName,
			digitalSalesRoomsPage.viewMenuItem
		);

		const comment = getRandomString();

		await editDigitalSalesRoomPage.addDigitalSalesRoomComment(comment);

		await editDigitalSalesRoomPage.replyButton.click();

		const commentReply = getRandomString();

		await editDigitalSalesRoomPage.editCommentTextarea.fill(commentReply);
		await editDigitalSalesRoomPage.commentEditSaveButton.click();

		await waitForAlert(page, 'Success:Your comment has been posted.');

		await expect(page.getByText(commentReply)).toBeVisible();
		await expect(page.getByText(comment)).toBeVisible();
	}
);

test(
	'Can upload file to a digital sales room',
	{tag: '@LPD-87116'},
	async ({apiHelpers, digitalSalesRoomsPage, editDigitalSalesRoomPage}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();
		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		await digitalSalesRoomsPage.clickRowActionsMenuItem(
			roomName,
			digitalSalesRoomsPage.viewMenuItem
		);

		await editDigitalSalesRoomPage.uploadDocument(
			path.join(__dirname, 'dependencies', 'liferay.png')
		);

		await expect(
			editDigitalSalesRoomPage.noDocumentsMessage
		).not.toBeVisible();
	}
);

test(
	'A viewer cannot upload files nor share but can make comments',
	{tag: ['@LPD-87116', '@LPD-96701']},
	async ({
		apiHelpers,
		digitalSalesRoomUsersPage,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();
		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		await digitalSalesRoomsPage.clickRowActionsMenuItem(
			roomName,
			digitalSalesRoomsPage.shareMenuItem
		);

		await expect(
			digitalSalesRoomUsersPage.userEmailAddressesInput
		).toBeVisible();

		await digitalSalesRoomUsersPage.userEmailAddressesInput.fill(
			userAccount.emailAddress
		);
		await digitalSalesRoomUsersPage.userEmailAddressesInput.press('Enter');
		await digitalSalesRoomUsersPage.inviteButton.click();

		await waitForAlert(page, 'Success:User was invited successfully.');

		await performUserSwitch(page, userAccount.alternateName);

		await page.goto(`/web/${roomName}`);

		await expect(digitalSalesRoomUsersPage.shareButton).not.toBeVisible();

		const comment = getRandomString();

		await editDigitalSalesRoomPage.addDigitalSalesRoomComment(comment);

		await expect(editDigitalSalesRoomPage.commentTextarea).toBeVisible();
		await expect(page.getByText(userAccount.name)).toBeVisible();
		await expect(page.getByText(comment)).toBeVisible();

		await editDigitalSalesRoomPage.documentsMenuItem.click();

		await expect(editDigitalSalesRoomPage.noDocumentsMessage).toBeVisible();
		await expect(editDigitalSalesRoomPage.newButton).not.toBeVisible();

		await performUserSwitch(page, 'test');
	}
);

test(
	'An invited non-owner cannot browse an archived room',
	{tag: '@LPD-92367'},
	async ({
		apiHelpers,
		digitalSalesRoomUsersPage,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();
		await digitalSalesRoomsPage.clickRowActionsMenuItem(
			roomName,
			digitalSalesRoomsPage.shareMenuItem
		);

		await expect(
			digitalSalesRoomUsersPage.userEmailAddressesInput
		).toBeVisible();

		await digitalSalesRoomUsersPage.userEmailAddressesInput.fill(
			userAccount.emailAddress
		);
		await digitalSalesRoomUsersPage.userEmailAddressesInput.press('Enter');
		await digitalSalesRoomUsersPage.inviteButton.click();

		await waitForAlert(page, 'Success:User was invited successfully.');

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(async () => {
			await (
				await digitalSalesRoomsPage.digitalSalesRoomsTable.rowActions(
					roomName,
					0,
					false
				)
			).click({timeout: 1000});
		}).toPass();

		await expect(digitalSalesRoomsPage.archiveMenuItem).toBeVisible();

		await page.keyboard.press('Escape');

		await digitalSalesRoomsPage.archiveRoom(roomName);
		await digitalSalesRoomsPage.showArchivedRooms();

		await (
			await digitalSalesRoomsPage.digitalSalesRoomsTable.rowActions(
				roomName,
				0,
				false
			)
		).click();

		await expect(digitalSalesRoomsPage.restoreMenuItem).toBeVisible();

		await page.keyboard.press('Escape');

		await performUserSwitch(page, userAccount.alternateName);

		await page.goto(`/web/${roomName}`);

		await expect(page).not.toHaveURL(new RegExp(roomName, 'i'));

		await performUserSwitch(page, 'test');

		await digitalSalesRoomsPage.goToRoomsPage();
		await digitalSalesRoomsPage.showArchivedRooms();
		await digitalSalesRoomsPage.restoreRoom(roomName);

		await performUserSwitch(page, userAccount.alternateName);

		await page.goto(`/web/${roomName}`);

		await expect(page).toHaveURL(new RegExp(roomName, 'i'));

		await performUserSwitch(page, 'test');
	}
);

test(
	'An owner does not see the upload button in an archived room',
	{tag: '@LPD-92367'},
	async ({
		apiHelpers,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		const rooms = await apiHelpers.headlessDigitalSalesRoom.getRooms();

		const room = rooms.items.find(
			(item: {name: string}) => item.name === roomName
		);

		const siteOwnerRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Owner');

		const urlSearchParams = new URLSearchParams();

		urlSearchParams.append('userId', String(userAccount.id));
		urlSearchParams.append('groupId', String(room.siteId));
		urlSearchParams.append('roleIds', JSON.stringify([siteOwnerRole.id]));

		await apiHelpers.post(
			`${liferayConfig.environment.baseUrl}/api/jsonws/usergrouprole/add-user-group-roles`,
			{
				data: urlSearchParams.toString(),
				failOnStatusCode: true,
				headers: await apiHelpers.getJSONWebServicesHeaders(),
			}
		);

		await digitalSalesRoomsPage.archiveRoom(roomName);

		await performUserSwitch(page, userAccount.alternateName);

		await page.goto(`/web/${roomName}`);

		await expect(page).toHaveURL(new RegExp(roomName, 'i'));

		await editDigitalSalesRoomPage.documentsMenuItem.click();

		await expect(page.getByRole('searchbox')).toBeVisible();
		await expect(
			page.getByTestId('creationMenuNewButton').filter({hasText: 'New'})
		).not.toBeVisible();

		await performUserSwitch(page, 'test');
	}
);

test(
	'A contributor can upload documents and make comments',
	{tag: '@LPD-87116'},
	async ({
		apiHelpers,
		digitalSalesRoomUsersPage,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();
		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		await digitalSalesRoomsPage.clickRowActionsMenuItem(
			roomName,
			digitalSalesRoomsPage.shareMenuItem
		);

		await expect(
			digitalSalesRoomUsersPage.userEmailAddressesInput
		).toBeVisible();

		await digitalSalesRoomUsersPage.userEmailAddressesInput.fill(
			userAccount.emailAddress
		);
		await digitalSalesRoomUsersPage.userEmailAddressesInput.press('Enter');
		await editDigitalSalesRoomPage.roleKeyButton.click();
		await editDigitalSalesRoomPage.contributorRoleInputButton.click();
		await digitalSalesRoomUsersPage.inviteButton.click();

		await waitForAlert(page, 'Success:User was invited successfully.');

		await performUserSwitch(page, userAccount.alternateName);

		await page.goto(`/web/${roomName}`);

		const comment = getRandomString();

		await editDigitalSalesRoomPage.addDigitalSalesRoomComment(comment);

		await expect(editDigitalSalesRoomPage.commentTextarea).toBeVisible();
		await expect(page.getByText(userAccount.name)).toBeVisible();
		await expect(page.getByText(comment)).toBeVisible();

		await editDigitalSalesRoomPage.uploadDocument(
			path.join(__dirname, 'dependencies', 'liferay.png')
		);

		await expect(
			editDigitalSalesRoomPage.noDocumentsMessage
		).not.toBeVisible();

		await performUserSwitch(page, 'test');
	}
);

test(
	'A contributor can update created invitations',
	{tag: '@LPD-87116'},
	async ({
		apiHelpers,
		digitalSalesRoomUsersPage,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		const email = `invited-${getRandomInt()}@liferay.com`;
		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();
		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();
		await digitalSalesRoomsPage.clickRowActionsMenuItem(
			roomName,
			digitalSalesRoomsPage.shareMenuItem
		);

		await expect(
			digitalSalesRoomUsersPage.userEmailAddressesInput
		).toBeVisible();

		await digitalSalesRoomUsersPage.userEmailAddressesInput.fill(
			userAccount.emailAddress
		);
		await digitalSalesRoomUsersPage.userEmailAddressesInput.press('Enter');
		await editDigitalSalesRoomPage.roleKeyButton.click();

		await expect(
			editDigitalSalesRoomPage.contributorRoleInputButton
		).toBeVisible();

		await editDigitalSalesRoomPage.contributorRoleInputButton.click();
		await digitalSalesRoomUsersPage.inviteButton.click();

		await waitForAlert(page, 'Success:User was invited successfully.');

		await performUserSwitch(page, userAccount.alternateName);

		await page.goto(`/web/${roomName}`);

		await digitalSalesRoomUsersPage.shareButton.click();
		await digitalSalesRoomUsersPage.userEmailAddressesInput.fill(email);
		await digitalSalesRoomUsersPage.userEmailAddressesInput.press('Enter');
		await digitalSalesRoomUsersPage.inviteButton.click();

		await waitForAlert(page, 'Success:User was invited successfully.');

		await expect(
			digitalSalesRoomUsersPage.roleText(email, 'Viewer')
		).toBeVisible();

		await digitalSalesRoomUsersPage.roleDropdown(email).click();
		await editDigitalSalesRoomPage.contributorRoleMenuItemButton.click();

		await expect(
			digitalSalesRoomUsersPage.roleText(email, 'Content Contributor')
		).toBeVisible();

		await performUserSwitch(page, 'test');
	}
);

test(
	'A viewer can see the comment and uploaded file made by the contributor',
	{tag: '@LPD-87116'},
	async ({
		apiHelpers,
		digitalSalesRoomUsersPage,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const contributor =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[contributor.alternateName] = {
			name: contributor.givenName,
			password: 'test',
			surname: contributor.familyName,
		};

		const viewer = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[viewer.alternateName] = {
			name: viewer.givenName,
			password: 'test',
			surname: viewer.familyName,
		};

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();
		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();
		await digitalSalesRoomsPage.clickRowActionsMenuItem(
			roomName,
			digitalSalesRoomsPage.shareMenuItem
		);

		await expect(
			digitalSalesRoomUsersPage.userEmailAddressesInput
		).toBeVisible();

		await digitalSalesRoomUsersPage.userEmailAddressesInput.fill(
			viewer.emailAddress
		);
		await digitalSalesRoomUsersPage.userEmailAddressesInput.press('Enter');
		await digitalSalesRoomUsersPage.inviteButton.click();

		await waitForAlert(page, 'Success:User was invited successfully.');

		await digitalSalesRoomUsersPage.userEmailAddressesInput.fill(
			contributor.emailAddress
		);
		await digitalSalesRoomUsersPage.userEmailAddressesInput.press('Enter');
		await editDigitalSalesRoomPage.roleKeyButton.click();

		await expect(
			editDigitalSalesRoomPage.contributorRoleInputButton
		).toBeVisible();

		await editDigitalSalesRoomPage.contributorRoleInputButton.click();
		await digitalSalesRoomUsersPage.inviteButton.click();

		await waitForAlert(page, 'Success:User was invited successfully.');

		await performUserSwitch(page, contributor.alternateName);

		await page.goto(`/web/${roomName}`);

		const comment = getRandomString();

		await editDigitalSalesRoomPage.addDigitalSalesRoomComment(comment);

		await expect(editDigitalSalesRoomPage.commentTextarea).toBeVisible();
		await expect(page.getByText(contributor.name)).toBeVisible();
		await expect(page.getByText(comment)).toBeVisible();

		await editDigitalSalesRoomPage.uploadDocument(
			path.join(__dirname, 'dependencies', 'liferay.png')
		);

		await expect(
			editDigitalSalesRoomPage.noDocumentsMessage
		).not.toBeVisible();

		await performUserSwitch(page, viewer.alternateName);

		await page.goto(`/web/${roomName}`);

		const comment2 = getRandomString();

		await editDigitalSalesRoomPage.addDigitalSalesRoomComment(comment2);

		await expect(editDigitalSalesRoomPage.commentTextarea).toBeVisible();
		await expect(page.getByText(viewer.name)).toBeVisible();
		await expect(page.getByText(comment2)).toBeVisible();
		await expect(page.getByText(contributor.name)).toBeVisible();
		await expect(page.getByText(comment)).toBeVisible();

		await editDigitalSalesRoomPage.documentsMenuItem.click();

		await expect(page.getByText('Approved')).toBeVisible();
		await expect(
			editDigitalSalesRoomPage.noDocumentsMessage
		).not.toBeVisible();

		await performUserSwitch(page, 'test');
	}
);

test(
	'An image uploaded from a page fragment is not listed in the Documents widget',
	{tag: '@LPD-92365'},
	async ({
		apiHelpers,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const filePath = path.join(__dirname, 'dependencies', 'liferay.png');

		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await page.goto(`/web/${roomName}/onboarding?p_l_mode=edit`);

		await editDigitalSalesRoomPage.uploadFragmentImage(filePath);

		await page.goto(`/web/${roomName}`);

		await editDigitalSalesRoomPage.documentsMenuItem.click();

		await expect(editDigitalSalesRoomPage.noDocumentsMessage).toBeVisible();

		const fileChooserPromise = page.waitForEvent('filechooser');

		await editDigitalSalesRoomPage.newButton.click();
		await editDigitalSalesRoomPage.fileUploadButton.click();
		await editDigitalSalesRoomPage.selectFileButton.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(filePath);

		await editDigitalSalesRoomPage.publishButton.click();

		await waitForAlert(page);

		await expect(
			editDigitalSalesRoomPage.noDocumentsMessage
		).not.toBeVisible();
	}
);

test(
	'A seller can duplicate a room copying only the selected documents',
	{tag: '@LPD-92370'},
	async ({
		apiHelpers,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});
		const roomName = `A${getRandomInt()}`;
		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			'L_DSR_SELLER',
			userAccount.id
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[userAccount.emailAddress]
		);

		await performUserSwitch(page, userAccount.alternateName);

		await test.step('Create a room with two documents', async () => {
			await digitalSalesRoomsPage.goToRoomsPageAsSeller();

			await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

			await editDigitalSalesRoomPage.addDigitalSalesRoom({
				accountName: account.name,
				roomName,
			});

			await digitalSalesRoomsPage.goToRoomsPageAsSeller();

			await digitalSalesRoomsPage.clickRowActionsMenuItem(
				roomName,
				digitalSalesRoomsPage.viewMenuItem
			);

			await editDigitalSalesRoomPage.uploadDocument(
				path.join(__dirname, 'dependencies', 'document1.png')
			);
			await editDigitalSalesRoomPage.uploadDocument(
				path.join(__dirname, 'dependencies', 'liferay.png')
			);
		});

		await test.step('Duplicate the room selecting only one document', async () => {
			await digitalSalesRoomsPage.goToRoomsPageAsSeller();

			await digitalSalesRoomsPage.clickRowActionsMenuItem(
				roomName,
				digitalSalesRoomsPage.duplicateMenuItem
			);

			await expect(
				digitalSalesRoomsPage.duplicateModalHeading
			).toBeVisible();
			await expect(
				digitalSalesRoomsPage.documentRow('document1')
			).toBeVisible();
			await expect(
				digitalSalesRoomsPage.documentRow('liferay')
			).toBeVisible();

			await digitalSalesRoomsPage.documentRowCheckbox('liferay').check();
			await digitalSalesRoomsPage.duplicateButton.click();

			await expect(digitalSalesRoomsPage.duplicateModal).not.toBeVisible({
				timeout: 60000,
			});
		});

		await test.step('Verify the duplicated room contains only the selected document', async () => {
			await digitalSalesRoomsPage.goToRoomsPageAsSeller();

			await digitalSalesRoomsPage.clickRowActionsMenuItem(
				`${roomName} (Copy)`,
				digitalSalesRoomsPage.viewMenuItem
			);

			await editDigitalSalesRoomPage.documentsMenuItem.click();

			await expect(
				editDigitalSalesRoomPage.documentCard('document1')
			).not.toBeVisible();
			await expect(
				editDigitalSalesRoomPage.documentCard('liferay')
			).toBeVisible();
		});
	}
);

test(
	'Duplicating an archived room creates an active room',
	{tag: '@LPD-97749'},
	async ({apiHelpers, digitalSalesRoomsPage, editDigitalSalesRoomPage}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const roomName = `A${getRandomInt()}`;

		await test.step('Create and archive a room', async () => {
			await digitalSalesRoomsPage.goToRoomsPage();

			await expect(
				digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
			).toBeVisible();

			await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

			await editDigitalSalesRoomPage.addDigitalSalesRoom({
				accountName: account.name,
				roomName,
			});

			await digitalSalesRoomsPage.goToRoomsPage();

			await digitalSalesRoomsPage.archiveRoom(roomName);
		});

		await test.step('Duplicate the archived room', async () => {
			await digitalSalesRoomsPage.showArchivedRooms();

			await digitalSalesRoomsPage.clickRowActionsMenuItem(
				roomName,
				digitalSalesRoomsPage.duplicateMenuItem
			);

			await expect(
				digitalSalesRoomsPage.duplicateModalHeading
			).toBeVisible();

			await digitalSalesRoomsPage.duplicateButton.click();

			await expect(digitalSalesRoomsPage.duplicateModal).not.toBeVisible({
				timeout: 30000,
			});
		});

		await test.step('Verify the duplicated room is active', async () => {
			await digitalSalesRoomsPage.goToRoomsPage();

			const duplicatedRow =
				digitalSalesRoomsPage.digitalSalesRoomsTable.table
					.getByRole('row')
					.filter({hasText: `${roomName} (Copy)`});

			await expect(duplicatedRow).toContainText('Active');
		});
	}
);

test(
	'The room collaborator can manage pages and documents, add room comments, and share the room',
	{tag: '@LPD-92366'},
	async ({
		apiHelpers,
		digitalSalesRoomUsersPage,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const collaborator =
			await apiHelpers.headlessAdminUser.postUserAccount();
		const member = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[collaborator.alternateName] = {
			name: collaborator.givenName,
			password: 'test',
			surname: collaborator.familyName,
		};

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();
		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();
		await digitalSalesRoomsPage.clickRowActionsMenuItem(
			roomName,
			digitalSalesRoomsPage.shareMenuItem
		);

		await expect(
			digitalSalesRoomUsersPage.userEmailAddressesInput
		).toBeVisible();

		await digitalSalesRoomUsersPage.userEmailAddressesInput.fill(
			collaborator.emailAddress
		);
		await digitalSalesRoomUsersPage.userEmailAddressesInput.press('Enter');
		await editDigitalSalesRoomPage.roleKeyButton.click();

		await expect(
			editDigitalSalesRoomPage.roomCollaboratorRoleInputButton
		).toBeVisible();

		await editDigitalSalesRoomPage.roomCollaboratorRoleInputButton.click();
		await digitalSalesRoomUsersPage.inviteButton.click();

		await waitForAlert(page, 'Success:User was invited successfully.');

		await performUserSwitch(page, collaborator.alternateName);

		await page.goto(`/web/${roomName}/onboarding?p_l_mode=edit`);

		await expect(editDigitalSalesRoomPage.pageEditor).toBeVisible();

		await page.goto(`/web/${roomName}`);

		await digitalSalesRoomUsersPage.shareButton.click();
		await digitalSalesRoomUsersPage.userEmailAddressesInput.fill(
			member.emailAddress
		);
		await digitalSalesRoomUsersPage.userEmailAddressesInput.press('Enter');
		await editDigitalSalesRoomPage.roleKeyButton.click();

		await expect(
			editDigitalSalesRoomPage.contributorRoleInputButton
		).toBeVisible();
		await expect(
			editDigitalSalesRoomPage.viewerRoleInputButton
		).toBeVisible();
		await expect(
			editDigitalSalesRoomPage.roomCollaboratorRoleInputButton
		).not.toBeVisible();

		await editDigitalSalesRoomPage.viewerRoleInputButton.click();
		await digitalSalesRoomUsersPage.inviteButton.click();

		await waitForAlert(page, 'Success:User was invited successfully.');

		await expect(
			digitalSalesRoomUsersPage.roleText(member.name, 'Viewer')
		).toBeVisible();

		await digitalSalesRoomUsersPage.roleDropdown(member.name).click();

		await expect(
			editDigitalSalesRoomPage.contributorRoleMenuItemButton
		).toBeVisible();

		await editDigitalSalesRoomPage.contributorRoleMenuItemButton.click();

		await expect(
			digitalSalesRoomUsersPage.roleText(
				member.name,
				'Content Contributor'
			)
		).toBeVisible();

		await page.goto(`/web/${roomName}`);

		const comment = getRandomString();

		await editDigitalSalesRoomPage.addDigitalSalesRoomComment(comment);

		await expect(page.getByText(comment)).toBeVisible();

		await editDigitalSalesRoomPage.uploadDocument(
			path.join(__dirname, 'dependencies', 'liferay.png')
		);

		await expect(
			editDigitalSalesRoomPage.noDocumentsMessage
		).not.toBeVisible();

		await performUserSwitch(page, 'test');
	}
);

test(
	'Set, update, and remove the access expiration for internal and invited users',
	{tag: '@LPD-92369'},
	async ({
		apiHelpers,
		digitalSalesRoomUsersPage,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const accountName = `B${getRandomInt()}`;
		const invitedEmail = `invited-${getRandomInt()}@liferay.com`;
		const roomName = `A${getRandomInt()}`;

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		await apiHelpers.headlessAdminUser.postAccount({
			name: accountName,
			type: 'business',
		});

		await test.step('Create the room', async () => {
			await digitalSalesRoomsPage.goToRoomsPage();

			await expect(
				digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
			).toBeVisible();

			await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

			await editDigitalSalesRoomPage.addDigitalSalesRoom({
				accountName,
				roomName,
			});
		});

		await test.step('Open the share view', async () => {
			await digitalSalesRoomsPage.goToRoomsPage();

			await expect(
				digitalSalesRoomsPage.digitalSalesRoomsTable.cell(
					roomName,
					false
				)
			).toBeVisible();

			await digitalSalesRoomsPage.clickRowActionsMenuItem(
				roomName,
				digitalSalesRoomsPage.shareMenuItem
			);

			await expect(
				digitalSalesRoomUsersPage.userEmailAddressesInput
			).toBeVisible();
		});

		const verifyExpirationLifecycle = async (
			emailAddress: string,
			rowText: string
		) => {
			await test.step('Add the user with an access expiration date', async () => {
				await digitalSalesRoomUsersPage.userEmailAddressesInput.fill(
					emailAddress
				);
				await digitalSalesRoomUsersPage.userEmailAddressesInput.press(
					'Enter'
				);
				await digitalSalesRoomUsersPage.inviteExpirationDateInput.fill(
					'2030-07-15'
				);
				await digitalSalesRoomUsersPage.inviteButton.click();

				await waitForAlert(
					page,
					'Success:User was invited successfully.'
				);

				await expect(
					digitalSalesRoomUsersPage.expirationLabel(rowText)
				).toContainText('Jul 15, 2030');
			});

			await test.step('Update the access expiration date', async () => {
				await digitalSalesRoomUsersPage
					.editExpirationButton(rowText)
					.click();
				await digitalSalesRoomUsersPage
					.rowExpirationDateInput(rowText)
					.fill('2031-08-20');
				await digitalSalesRoomUsersPage
					.confirmExpirationButton(rowText)
					.click();

				await waitForAlert(page);

				await expect(
					digitalSalesRoomUsersPage.expirationLabel(rowText)
				).toContainText('Aug 20, 2031');
			});

			await test.step('Remove the access expiration date', async () => {
				await digitalSalesRoomUsersPage
					.editExpirationButton(rowText)
					.click();
				await digitalSalesRoomUsersPage
					.rowExpirationDateInput(rowText)
					.fill('');
				await digitalSalesRoomUsersPage
					.confirmExpirationButton(rowText)
					.click();

				await waitForAlert(page);

				await expect(
					digitalSalesRoomUsersPage.userRow(rowText)
				).toContainText('No Expiration');
			});
		};

		await test.step('Manage the access expiration for an internal member', () =>
			verifyExpirationLifecycle(
				userAccount.emailAddress,
				userAccount.alternateName
			));

		await test.step('Manage the access expiration for a pending invitation', () =>
			verifyExpirationLifecycle(invitedEmail, invitedEmail));
	}
);

test(
	'The room settings action and page use the room-settings labels',
	{tag: '@LPD-97482'},
	async ({
		apiHelpers,
		digitalSalesRoomSettingsPage,
		digitalSalesRoomsPage,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const roomName = `A${getRandomInt()}`;

		await apiHelpers.headlessDigitalSalesRoom.addRoom({
			accountEntryId: account.id,
			name: roomName,
		});

		await digitalSalesRoomsPage.goToRoomsPage();

		await expect(async () => {
			await (
				await digitalSalesRoomsPage.digitalSalesRoomsTable.rowActions(
					roomName,
					0,
					false
				)
			).click();

			await expect(digitalSalesRoomsPage.settingsMenuItem).toBeVisible({
				timeout: 1000,
			});
		}).toPass({timeout: 10000});

		await digitalSalesRoomsPage.settingsMenuItem.click();

		await expect(digitalSalesRoomSettingsPage.headerTitle).toHaveText(
			`${roomName} Settings`
		);
	}
);

test(
	'When a room is archived, there is a warning on the site',
	{tag: '@LPD-97849'},
	async ({
		apiHelpers,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});

		const roomName = `A${getRandomInt()}`;

		const userAccount =
			await apiHelpers.headlessAdminUser.postUserAccount();

		userData[userAccount.alternateName] = {
			name: userAccount.givenName,
			password: 'test',
			surname: userAccount.familyName,
		};

		await apiHelpers.headlessAdminUser.postRoleByExternalReferenceCodeUserAccountAssociation(
			'L_DSR_SELLER',
			userAccount.id
		);

		await apiHelpers.headlessAdminUser.assignUserToAccountByEmailAddress(
			account.id,
			[userAccount.emailAddress]
		);

		await performUserSwitch(page, userAccount.alternateName);

		await test.step('The seller creates and archives a room', async () => {
			await digitalSalesRoomsPage.goToRoomsPageAsSeller();

			await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

			await editDigitalSalesRoomPage.addDigitalSalesRoom({
				accountName: account.name,
				roomName,
			});

			await digitalSalesRoomsPage.goToRoomsPageAsSeller();

			await digitalSalesRoomsPage.archiveRoom(roomName);
		});

		await test.step('The archived room shows the warning to the seller', async () => {
			await digitalSalesRoomsPage.showArchivedRooms();

			await digitalSalesRoomsPage.clickRowActionsMenuItem(
				roomName,
				digitalSalesRoomsPage.viewMenuItem
			);

			await expect(
				digitalSalesRoomsPage.archivedRoomWarning
			).toBeVisible();

			await expect(
				digitalSalesRoomsPage.archivedRoomWarning
			).toContainText(
				'This Digital Sales Room is archived. New comments cannot be added, and it can no longer be shared.'
			);
		});
	}
);
