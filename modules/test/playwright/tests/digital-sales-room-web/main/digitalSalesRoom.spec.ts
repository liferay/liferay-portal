/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {digitalSalesRoomPagesTest} from '../../../fixtures/digitalSalesRoomPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	dataApiHelpersTest,
	digitalSalesRoomPagesTest,
	featureFlagsTest({
		'LPD-66359': {enabled: true},
	}),
	loginTest()
);

test.afterEach(async ({apiHelpers}) => {
	const digitalSalesRooms =
		await apiHelpers.headlessDigitalSalesRoom.getDigitalSalesRooms();

	for (const digitalSalesRoom of digitalSalesRooms.items) {
		await apiHelpers.headlessSite.deleteSite(digitalSalesRoom.id);
	}

	const digitalSalesRoomTemplates =
		await apiHelpers.headlessDigitalSalesRoom.getDigitalSalesRoomTemplates();

	for (const digitalSalesRoomTemplate of digitalSalesRoomTemplates.items) {
		await apiHelpers.headlessSite.deleteSite(digitalSalesRoomTemplate.id);
	}
});

test.skip(
	'Create a digital sales room',
	{tag: '@LPD-69509'},
	async ({digitalSalesRoomsPage, editDigitalSalesRoomPage}) => {
		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			banner: path.join(__dirname, '/dependencies/liferay.png'),
			roomName,
		});

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.cell(roomName)
		).toBeVisible();
	}
);

test.skip(
	'Create a digital sales room with account and channel',
	{tag: '@LPD-73190'},
	async ({apiHelpers, digitalSalesRoomsPage, editDigitalSalesRoomPage}) => {
		const roomName = `A${getRandomInt()}`;

		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});
		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({});

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			channelName: channel.name,
			roomName,
		});

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.cell(channel.name)
		).toBeVisible();
		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.cell(roomName)
		).toBeVisible();
	}
);

test.skip(
	'Create a digital sales room with users',
	{tag: '@LPD-69528'},
	async ({apiHelpers, digitalSalesRoomsPage, editDigitalSalesRoomPage}) => {
		const roomName = `A${getRandomInt()}`;

		const user1 = await apiHelpers.headlessAdminUser.postUserAccount();
		const user2 = await apiHelpers.headlessAdminUser.postUserAccount();

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			roomName,
			usersEmailAddresses: [user1.emailAddress, user2.emailAddress],
		});

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.cell(roomName)
		).toBeVisible();

		const digitalSalesRoom = (
			await apiHelpers.headlessDigitalSalesRoom.getDigitalSalesRooms()
		).items.pop();

		expect(digitalSalesRoom.userAccountBriefs.length).toEqual(3);
	}
);

test.skip(
	'Delete a digital sales room',
	{tag: '@LPD-73577'},
	async ({digitalSalesRoomsPage, editDigitalSalesRoomPage, page}) => {
		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goto();
		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			banner: path.join(__dirname, '/dependencies/liferay.png'),
			roomName,
		});

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.cell(roomName)
		).toBeVisible();

		await expect(async () => {
			await (
				await digitalSalesRoomsPage.digitalSalesRoomsTable.rowActions(
					roomName,
					0
				)
			).click();
			await expect(digitalSalesRoomsPage.deleteMenuItem).toBeVisible({
				timeout: 200,
			});
		}).toPass({timeout: 1000});

		await digitalSalesRoomsPage.deleteMenuItem.click();

		const modal = page.getByRole('alert');

		await expect(modal).toBeVisible();

		await modal.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(page);

		await expect(digitalSalesRoomsPage.noResultsFoundMessage).toBeVisible();
	}
);

test.skip(
	'Save a digital sales room as template',
	{tag: '@LPD-73189'},
	async ({
		digitalSalesRoomSaveAsTemplatePage,
		digitalSalesRoomTemplatesPage,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
	}) => {
		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goto();
		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			banner: path.join(__dirname, '/dependencies/liferay.png'),
			roomName,
		});

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.cell(roomName)
		).toBeVisible();

		await expect(async () => {
			await (
				await digitalSalesRoomsPage.digitalSalesRoomsTable.rowActions(
					roomName,
					0
				)
			).click();
			await expect(
				digitalSalesRoomsPage.saveAsTemplateMenuItem
			).toBeVisible({timeout: 200});
		}).toPass({timeout: 1000});

		await digitalSalesRoomsPage.saveAsTemplateMenuItem.click();

		const template = {
			description: `Description ${getRandomInt()}`,
			roomName: `T${getRandomInt()}`,
		};

		await digitalSalesRoomSaveAsTemplatePage.saveAsTemplate(template);

		await digitalSalesRoomTemplatesPage.goto();

		await expect(
			digitalSalesRoomTemplatesPage.digitalSalesRoomTemplatesTable.cell(
				template.roomName,
				false
			)
		).toBeVisible();
		await expect(
			digitalSalesRoomTemplatesPage.digitalSalesRoomTemplatesTable.cell(
				template.description,
				false
			)
		).toBeVisible();
	}
);

test.skip(
	'Create a digital sales room from template',
	{tag: '@LPD-73189'},
	async ({
		digitalSalesRoomTemplatesPage,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		editDigitalSalesRoomTemplatePage,
		page,
	}) => {
		const name = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goto();
		await digitalSalesRoomsPage.templatesLink.click();

		await expect(async () => {
			await digitalSalesRoomTemplatesPage.newDigitalSalesRoomTemplateButton.click();

			await expect(
				editDigitalSalesRoomTemplatePage.nameInput
			).toBeVisible({timeout: 500});
		}).toPass({timeout: 5000});

		await editDigitalSalesRoomTemplatePage.addDigitalSalesRoomTemplate({
			name,
			primaryColor: 'red',
		});

		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await expect(async () => {
			await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

			await expect(
				digitalSalesRoomsPage.startFromScratchButton
			).toBeVisible({timeout: 200});
		}).toPass({timeout: 2000});

		await digitalSalesRoomsPage.startFromScratchButton.click();

		await expect(editDigitalSalesRoomPage.clientNameInput).toBeVisible();

		await editDigitalSalesRoomPage.cancelButton.click();

		await expect(async () => {
			await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

			await expect(
				digitalSalesRoomsPage.startFromTemplateButton
			).toBeVisible({timeout: 200});
		}).toPass({timeout: 2000});

		await digitalSalesRoomsPage.startFromTemplateButton.click();

		await page.getByText(name).click();

		await expect(
			editDigitalSalesRoomPage.templatePreviewFrame.getByRole(
				'menuitem',
				{name: 'Onboarding'}
			)
		).toBeVisible();

		await editDigitalSalesRoomPage.nextButton.click();

		await expect(editDigitalSalesRoomPage.clientNameInput).toBeVisible();
		await expect(editDigitalSalesRoomPage.primaryColorInput).toHaveValue(
			'red'
		);

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			primaryColor: 'red',
			roomName,
		});

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.cell(roomName)
		).toBeVisible();
	}
);

test.skip(
	'Test edit menuitem for digital sales room',
	{tag: ['@LPD-69570', '@LPD-76329']},
	async ({digitalSalesRoomsPage, editDigitalSalesRoomPage, page}) => {
		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			banner: path.join(__dirname, '/dependencies/liferay.png'),
			roomName,
		});

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.cell(roomName)
		).toBeVisible();

		await expect(async () => {
			await (
				await digitalSalesRoomsPage.digitalSalesRoomsTable.rowActions(
					roomName,
					0
				)
			).click();
			await expect(digitalSalesRoomsPage.editMenuItem).toBeVisible({
				timeout: 200,
			});
		}).toPass({timeout: 1000});

		await digitalSalesRoomsPage.editMenuItem.click();

		await expect(editDigitalSalesRoomPage.onboardingMenuItem).toBeVisible();

		test.step('verify welcome block', async () => {
			await expect(
				page.getByRole('heading', {name: 'WELCOME'})
			).toBeVisible();
			await expect(page.getByText('Andry Ford')).toBeVisible();
		});
	}
);

test.skip(
	'Test settings menuitem for digital sales room',
	{tag: '@LPD-76329'},
	async ({
		apiHelpers,
		digitalSalesRoomSettingsPage,
		digitalSalesRoomsPage,
		editDigitalSalesRoomPage,
		page,
	}) => {
		const roomName = `A${getRandomInt()}`;

		const account = await apiHelpers.headlessAdminUser.postAccount({
			type: 'business',
		});
		const channel =
			await apiHelpers.headlessCommerceAdminChannel.postChannel({});

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			accountName: account.name,
			channelName: channel.name,
			roomName,
		});

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.cell(roomName)
		).toBeVisible();

		await expect(async () => {
			await (
				await digitalSalesRoomsPage.digitalSalesRoomsTable.rowActions(
					roomName,
					0
				)
			).click();
			await expect(digitalSalesRoomsPage.settingsMenuItem).toBeVisible({
				timeout: 200,
			});
		}).toPass({timeout: 1000});

		await digitalSalesRoomsPage.settingsMenuItem.click();

		await expect(
			digitalSalesRoomSettingsPage.clientNameInput
		).toBeVisible();

		const clientName = `Edited ${getRandomInt()}`;

		await digitalSalesRoomSettingsPage.clientNameInput.fill(clientName);
		await digitalSalesRoomSettingsPage.roomNameInput.fill(
			`Edited ${roomName}`
		);
		await digitalSalesRoomSettingsPage.saveButton.click();

		await waitForAlert(digitalSalesRoomSettingsPage.page);

		await page.reload();

		await expect(digitalSalesRoomSettingsPage.clientNameInput).toHaveValue(
			clientName
		);
		await expect(digitalSalesRoomSettingsPage.roomNameInput).toHaveValue(
			`Edited ${roomName}`
		);

		await digitalSalesRoomSettingsPage.settingsLink.click();

		await expect(
			digitalSalesRoomSettingsPage.selectChannelInput
		).toHaveValue(channel.name);
		await expect(
			digitalSalesRoomSettingsPage.selectAccountInput
		).toHaveValue(account.name);
	}
);

test.skip(
	'Add comment',
	{tag: '@LPD-76076'},
	async ({digitalSalesRoomsPage, editDigitalSalesRoomPage, page}) => {
		const roomName = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await digitalSalesRoomsPage.digitalSalesRoomsTable.newButton.click();

		await editDigitalSalesRoomPage.addDigitalSalesRoom({
			banner: path.join(__dirname, '/dependencies/liferay.png'),
			roomName,
		});

		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.cell(roomName)
		).toBeVisible();
		await expect(async () => {
			await (
				await digitalSalesRoomsPage.digitalSalesRoomsTable.rowActions(
					roomName,
					0
				)
			).click();
			await expect(digitalSalesRoomsPage.editMenuItem).toBeVisible({
				timeout: 200,
			});
		}).toPass({timeout: 1000});

		await digitalSalesRoomsPage.editMenuItem.click();

		await expect(editDigitalSalesRoomPage.onboardingMenuItem).toBeVisible();

		await editDigitalSalesRoomPage.commentsButton.click();

		await expect(editDigitalSalesRoomPage.roomCommentsText).toBeVisible();
		await expect(editDigitalSalesRoomPage.commentSaveButton).toBeDisabled();

		const comment = getRandomString();

		await editDigitalSalesRoomPage.commentTextAreaPlaceholder.fill(comment);

		await expect(
			editDigitalSalesRoomPage.commentSaveButton
		).not.toBeDisabled();

		await editDigitalSalesRoomPage.commentSaveButton.click();

		await waitForAlert(page);

		await expect(
			editDigitalSalesRoomPage.commentTextAreaPlaceholder
		).toBeVisible();
		await expect(page.getByText('Test Test')).toBeVisible();
		await expect(page.getByText(comment)).toBeVisible();
	}
);
