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
	'Go to the templates page',
	{tag: '@LPD-73189'},
	async ({digitalSalesRoomTemplatesPage, digitalSalesRoomsPage}) => {
		await digitalSalesRoomsPage.goto();

		await expect(
			digitalSalesRoomsPage.digitalSalesRoomsTable.searchInput
		).toBeVisible();

		await expect(digitalSalesRoomsPage.roomsLink).toBeVisible();
		await expect(digitalSalesRoomsPage.templatesLink).toBeVisible();

		await digitalSalesRoomsPage.templatesLink.click();

		await expect(
			digitalSalesRoomTemplatesPage.newDigitalSalesRoomTemplateButton
		).toBeVisible();
		await expect(digitalSalesRoomTemplatesPage.roomsLink).toBeVisible();
		await expect(digitalSalesRoomTemplatesPage.templatesLink).toBeVisible();
	}
);

test.skip(
	'Create a digital sales room template',
	{tag: '@LPD-75031'},
	async ({
		digitalSalesRoomTemplatesPage,
		digitalSalesRoomsPage,
		editDigitalSalesRoomTemplatePage,
	}) => {
		const name = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goto();
		await digitalSalesRoomsPage.templatesLink.click();

		await digitalSalesRoomTemplatesPage.newDigitalSalesRoomTemplateButton.click();

		await editDigitalSalesRoomTemplatePage.addDigitalSalesRoomTemplate({
			banner: path.join(__dirname, '/dependencies/liferay.png'),
			name,
		});

		await digitalSalesRoomsPage.goto();
		await digitalSalesRoomsPage.templatesLink.click();

		await expect(
			digitalSalesRoomTemplatesPage.digitalSalesRoomTemplatesTable.cell(
				name,
				false
			)
		).toBeVisible();
	}
);

test.skip(
	'Delete a digital sales room template',
	{tag: '@LPD-75031'},
	async ({
		digitalSalesRoomTemplatesPage,
		digitalSalesRoomsPage,
		editDigitalSalesRoomTemplatePage,
		page,
	}) => {
		const name = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goto();
		await digitalSalesRoomsPage.templatesLink.click();
		await digitalSalesRoomTemplatesPage.newDigitalSalesRoomTemplateButton.click();

		await editDigitalSalesRoomTemplatePage.addDigitalSalesRoomTemplate({
			banner: path.join(__dirname, '/dependencies/liferay.png'),
			name,
		});

		await digitalSalesRoomsPage.goto();
		await digitalSalesRoomsPage.templatesLink.click();

		await expect(
			digitalSalesRoomTemplatesPage.digitalSalesRoomTemplatesTable.cell(
				name,
				false
			)
		).toBeVisible();

		await expect(async () => {
			await (
				await digitalSalesRoomTemplatesPage.digitalSalesRoomTemplatesTable.rowActions(
					name,
					0,
					false
				)
			).click();
			await expect(
				digitalSalesRoomTemplatesPage.deleteMenuItem
			).toBeVisible({
				timeout: 200,
			});
		}).toPass({timeout: 1000});

		await digitalSalesRoomTemplatesPage.deleteMenuItem.click();

		const modal = page.getByRole('alert');

		await expect(modal).toBeVisible();

		await modal.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(page);

		await expect(
			digitalSalesRoomTemplatesPage.noResultsFoundMessage
		).toBeVisible();
	}
);

test.skip(
	'Duplicate a digital sales room template',
	{tag: '@LPD-73189'},
	async ({
		digitalSalesRoomTemplatesPage,
		digitalSalesRoomsPage,
		editDigitalSalesRoomTemplatePage,
		page,
	}) => {
		const name = `A${getRandomInt()}`;

		await digitalSalesRoomsPage.goto();
		await digitalSalesRoomsPage.templatesLink.click();
		await digitalSalesRoomTemplatesPage.newDigitalSalesRoomTemplateButton.click();

		await editDigitalSalesRoomTemplatePage.addDigitalSalesRoomTemplate({
			banner: path.join(__dirname, '/dependencies/liferay.png'),
			name,
		});

		await digitalSalesRoomsPage.goto();
		await digitalSalesRoomsPage.templatesLink.click();

		await expect(
			digitalSalesRoomTemplatesPage.digitalSalesRoomTemplatesTable.cell(
				name,
				false
			)
		).toBeVisible();

		await expect(async () => {
			await (
				await digitalSalesRoomTemplatesPage.digitalSalesRoomTemplatesTable.rowActions(
					name,
					0,
					false
				)
			).click();
			await expect(
				digitalSalesRoomTemplatesPage.duplicateMenuItem
			).toBeVisible({
				timeout: 200,
			});
		}).toPass({timeout: 1000});

		await digitalSalesRoomTemplatesPage.duplicateMenuItem.click();

		await waitForAlert(page);

		await expect(
			digitalSalesRoomTemplatesPage.digitalSalesRoomTemplatesTable.cell(
				`${name} (Copy)`,
				false
			)
		).toBeVisible();
	}
);

test.skip(
	'Edit menu item for digital sales room template',
	{tag: '@LPD-76329'},
	async ({
		digitalSalesRoomTemplatesPage,
		editDigitalSalesRoomPage,
		editDigitalSalesRoomTemplatePage,
	}) => {
		const name = `A${getRandomInt()}`;

		await digitalSalesRoomTemplatesPage.goto();

		await expect(
			digitalSalesRoomTemplatesPage.digitalSalesRoomTemplatesTable
				.searchInput
		).toBeVisible();

		await expect(async () => {
			await digitalSalesRoomTemplatesPage.newDigitalSalesRoomTemplateButton.click();

			await expect(
				editDigitalSalesRoomTemplatePage.nameInput
			).toBeVisible({timeout: 200});
		}).toPass({timeout: 1000});

		await editDigitalSalesRoomTemplatePage.addDigitalSalesRoomTemplate({
			name,
			primaryColor: 'red',
		});

		await digitalSalesRoomTemplatesPage.goto();

		await expect(
			digitalSalesRoomTemplatesPage.digitalSalesRoomTemplatesTable.cell(
				name
			)
		).toBeVisible();

		await expect(async () => {
			await (
				await digitalSalesRoomTemplatesPage.digitalSalesRoomTemplatesTable.rowActions(
					name,
					0
				)
			).click();
			await expect(
				digitalSalesRoomTemplatesPage.editMenuItem
			).toBeVisible({
				timeout: 200,
			});
		}).toPass({timeout: 1000});

		await digitalSalesRoomTemplatesPage.editMenuItem.click();

		await expect(editDigitalSalesRoomPage.onboardingMenuItem).toBeVisible();
	}
);

test.skip(
	'Settings menu item for digital sales room template',
	{tag: '@LPD-76329'},
	async ({
		digitalSalesRoomTemplateSettingsPage,
		digitalSalesRoomTemplatesPage,
		editDigitalSalesRoomTemplatePage,
		page,
	}) => {
		const name = `A${getRandomInt()}`;

		await digitalSalesRoomTemplatesPage.goto();

		await expect(
			digitalSalesRoomTemplatesPage.digitalSalesRoomTemplatesTable
				.searchInput
		).toBeVisible();

		await expect(async () => {
			await digitalSalesRoomTemplatesPage.newDigitalSalesRoomTemplateButton.click();

			await expect(
				editDigitalSalesRoomTemplatePage.nameInput
			).toBeVisible({timeout: 200});
		}).toPass({timeout: 1000});

		await editDigitalSalesRoomTemplatePage.addDigitalSalesRoomTemplate({
			name,
			primaryColor: 'red',
		});

		await digitalSalesRoomTemplatesPage.goto();

		await expect(
			digitalSalesRoomTemplatesPage.digitalSalesRoomTemplatesTable.cell(
				name
			)
		).toBeVisible();

		await expect(async () => {
			await (
				await digitalSalesRoomTemplatesPage.digitalSalesRoomTemplatesTable.rowActions(
					name,
					0
				)
			).click();
			await expect(
				digitalSalesRoomTemplatesPage.settingsMenuItem
			).toBeVisible({
				timeout: 200,
			});
		}).toPass({timeout: 1000});

		await digitalSalesRoomTemplatesPage.settingsMenuItem.click();

		await expect(
			digitalSalesRoomTemplateSettingsPage.roomNameInput
		).toBeVisible();

		await digitalSalesRoomTemplateSettingsPage.roomNameInput.fill(
			`Edited ${name}`
		);
		await digitalSalesRoomTemplateSettingsPage.descriptionInput.fill(
			'Edited description'
		);
		await digitalSalesRoomTemplateSettingsPage.saveButton.click();

		await waitForAlert(page);

		await page.reload();

		await expect(
			digitalSalesRoomTemplateSettingsPage.roomNameInput
		).toHaveValue(`Edited ${name}`);
		await expect(
			digitalSalesRoomTemplateSettingsPage.descriptionInput
		).toHaveValue('Edited description');

		await digitalSalesRoomTemplateSettingsPage.lookAndFeelLink.click();

		const fileChooserPromise = page.waitForEvent('filechooser');

		await digitalSalesRoomTemplateSettingsPage.clientLogoButton.click();

		await (
			await fileChooserPromise
		).setFiles(path.join(__dirname, '/dependencies/liferay.png'));

		await digitalSalesRoomTemplateSettingsPage.primaryColorInput.fill(
			'blue'
		);
		await digitalSalesRoomTemplateSettingsPage.secondaryColorInput.fill(
			'blue'
		);
		await digitalSalesRoomTemplateSettingsPage.saveButton.click();

		await waitForAlert(page);

		await page.reload();

		await digitalSalesRoomTemplateSettingsPage.lookAndFeelLink.click();

		await expect(
			digitalSalesRoomTemplateSettingsPage.clientLogoSticker
		).toHaveAttribute('src', /data:image;base64/);
		await expect(
			digitalSalesRoomTemplateSettingsPage.primaryColorInput
		).toHaveValue('blue');
		await expect(
			digitalSalesRoomTemplateSettingsPage.secondaryColorInput
		).toHaveValue('blue');
	}
);
