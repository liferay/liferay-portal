/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {PermissionsPage} from '../permissions/pages/PermissionsPage';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

test(
	'Can edit a folder',
	{tag: '@LPD-42841'},
	async ({apiHelpers, assetsPage, page}) => {
		const folderTitle = getRandomString();

		const folderData =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				scopeKey: 'Default',
				title: folderTitle,
			});

		await assetsPage.gotoFiles();

		await assetsPage.execCardItemAction({
			action: 'Edit',
			filter: folderTitle,
		});

		const newFolderTitle = getRandomString();

		const nameInput = page.getByLabel('Name');

		await expect(nameInput).toHaveValue(folderTitle);

		await nameInput.fill(newFolderTitle);
		await page.getByLabel('Description').fill('folder description');
		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			page,
			`Success:${newFolderTitle} was updated successfully.`
		);

		await expect(
			page.getByLabel(newFolderTitle, {exact: true})
		).toBeVisible();

		await apiHelpers.objectFolder.deleteObjectEntryFolder(folderData.id);
	}
);

test(
	'Folders should not show status',
	{tag: ['@LPD-78615', '@LPD-92355']},
	async ({apiHelpers, assetsPage, page}) => {
		const folderTitle = getRandomString();

		await apiHelpers.objectFolder.createObjectEntryFolder({
			parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			scopeKey: 'Default',
			title: folderTitle,
		});

		await assetsPage.gotoContents();

		const row = page
			.getByRole('row')
			.filter({has: page.getByRole('link', {name: folderTitle})});

		await expect(row.locator('.cell-embedded-status')).toHaveText('--');
	}
);

test(
	'Folders have View Folder action, but not View',
	{tag: '@LPD-58720'},
	async ({apiHelpers, assetsPage, page}) => {
		const folderTitle = getRandomString();

		const folderData =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				scopeKey: 'Default',
				title: folderTitle,
			});

		await assetsPage.gotoFiles();

		assetsPage
			.getCardItem(folderTitle)
			.getByLabel(`${folderTitle} Actions`)
			.click();

		expect(
			page.getByRole('menuitem', {exact: true, name: 'View'})
		).toBeHidden();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'View Folder'})
		).toBeVisible();

		await apiHelpers.objectFolder.deleteObjectEntryFolder(folderData.id);
	}
);

test(
	'Info panel shows correct number of assets in folder',
	{tag: '@LPD-69166'},
	async ({apiHelpers, assetsPage, page}) => {
		const folderName = `Folder ${getRandomInt()}`;

		const folder = await apiHelpers.objectFolder.createObjectEntryFolder({
			parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
			scopeKey: 'Default',
			title: folderName,
		});

		try {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: 'R0lGODlhAQABAAAAACw=',
						name: `file_${getRandomString()}.png`,
					},
					objectEntryFolderExternalReferenceCode:
						folder.externalReferenceCode,
					title: `Content ${getRandomInt()}`,
				},
				'cms/basic-documents',
				'Default'
			);

			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode:
					folder.externalReferenceCode,
				scopeKey: 'Default',
				title: `Subfolder ${getRandomInt()}`,
			});

			await assetsPage.gotoFiles();

			await page.getByLabel(/View Selected/i).click();
			await page.getByRole('option', {name: 'Table'}).click();

			await page
				.getByRole('row', {name: folderName})
				.getByRole('checkbox')
				.check();

			await page.getByRole('button', {name: 'Show Info Panel'}).click();

			expect(
				await page.getByTestId('number-of-assets').textContent()
			).toContain('2');
		}
		finally {
			await apiHelpers.objectFolder.deleteObjectEntryFolder(folder.id);
		}
	}
);

test(
	'Duplicating a folder creates a copy in the same parent',
	{tag: '@LPD-88657'},
	async ({apiHelpers, assetsPage, page}) => {
		const folderName = `Folder ${getRandomString()}`;
		const spaceName = 'Default';

		await test.step('Create a folder in the Space', async () => {
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: spaceName,
				title: folderName,
			});
		});

		await test.step('Duplicate the folder', async () => {
			await assetsPage.gotoSpaceContents(spaceName);

			await assetsPage.execItemAction({
				action: 'Duplicate',
				filter: folderName,
				parentAction: 'Copy',
			});

			await expect(
				page.getByRole('link', {
					exact: true,
					name: `${folderName} (Copy)`,
				})
			).toBeVisible();
		});

		await test.step('Duplicate the original again and check the suffix increments', async () => {
			await assetsPage.execItemAction({
				action: 'Duplicate',
				filter: folderName,
				parentAction: 'Copy',
			});

			await expect(
				page.getByRole('link', {
					exact: true,
					name: `${folderName} (Copy 1)`,
				})
			).toBeVisible();
		});
	}
);

test(
	'Shared folder shows a shared icon in the Files section only for the recipient',
	{tag: '@LPD-66045'},
	async ({apiHelpers, assetsPage, page}) => {
		const folderTitle1 = `Folder ${getRandomString()}`;
		const folderTitle2 = `Folder ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		const objectEntryFolder1 =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
				scopeKey: space.assetLibraryKey,
				title: folderTitle1,
			});

		const objectEntryFolder2 =
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
				scopeKey: space.assetLibraryKey,
				title: folderTitle2,
			});

		try {
			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			const cmsAdminRole =
				await apiHelpers.headlessAdminUser.getRoleByName(
					'CMS Administrator'
				);

			await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
				cmsAdminRole.id,
				Number(user.id)
			);

			await apiHelpers.objectFolder.postObjectEntryFolderCollaborators(
				[
					{
						actionIds: ['VIEW'],
						id: user.id,
						share: true,
						type: 'User',
					},
				],
				objectEntryFolder1.id
			);

			await performUserSwitch(page, user.alternateName);

			await assetsPage.gotoFiles();

			await assetsPage.changeVisualizationMode('Table');

			const folderRow1 = page
				.getByRole('row')
				.filter({has: page.getByRole('link', {name: folderTitle1})});

			await expect(folderRow1).toBeVisible();

			await expect(
				folderRow1.locator('.lexicon-icon-users').first()
			).toBeVisible();

			const folderRow2 = page
				.getByRole('row')
				.filter({has: page.getByRole('link', {name: folderTitle2})});

			await expect(folderRow2).toBeVisible();

			await expect(folderRow2.locator('.lexicon-icon-users')).toHaveCount(
				0
			);
		}
		finally {
			await apiHelpers.objectFolder.deleteObjectEntryFolder(
				objectEntryFolder1.id
			);

			await apiHelpers.objectFolder.deleteObjectEntryFolder(
				objectEntryFolder2.id
			);
		}
	}
);

test(
	'Can add a folder in the Files section',
	{tag: '@LPD-92348'},
	async ({apiHelpers, assetsPage, folderPage, page}) => {
		const folderTitle = getRandomString();

		await assetsPage.gotoFiles();

		const [response] = await Promise.all([
			page.waitForResponse(
				(response) =>
					response.url().includes('/object-entry-folders') &&
					response.request().method() === 'POST'
			),
			folderPage.createFolder(folderTitle),
		]);

		const {id} = await response.json();

		try {
			await expect(
				page.getByLabel(folderTitle, {exact: true})
			).toBeVisible();
		}
		finally {
			await apiHelpers.objectFolder.deleteObjectEntryFolder(id);
		}
	}
);

test(
	'Can delete a folder in the Files section',
	{tag: '@LPD-92348'},
	async ({apiHelpers, assetsPage, page}) => {
		const folderTitle = getRandomString();

		const folder = await apiHelpers.objectFolder.createObjectEntryFolder({
			parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
			scopeKey: 'Default',
			title: folderTitle,
		});

		try {
			await assetsPage.gotoFiles();

			await assetsPage.changeVisualizationMode('Table');

			await assetsPage.execItemAction({
				action: 'Delete',
				filter: folderTitle,
			});

			await waitForAlert(page, `Success:${folderTitle} was moved`, {
				autoClose: false,
			});

			await expect(
				page.getByRole('link', {name: folderTitle})
			).toBeHidden();
		}
		finally {
			await apiHelpers.objectFolder.deleteObjectEntryFolder(folder.id);
		}
	}
);

test(
	'Can delete a folder in a Space',
	{tag: '@LPD-92348'},
	async ({apiHelpers, assetsPage, page}) => {
		const folderTitle = getRandomString();
		const spaceName = `Space ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		await apiHelpers.objectFolder.createObjectEntryFolder({
			parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			scopeKey: space.assetLibraryKey,
			title: folderTitle,
		});

		await assetsPage.gotoSpaceContents(spaceName);

		await assetsPage.execItemAction({
			action: 'Delete',
			filter: folderTitle,
		});

		await waitForAlert(page, `Success:${folderTitle} was moved`, {
			autoClose: false,
		});

		await expect(page.getByRole('link', {name: folderTitle})).toBeHidden();
	}
);

test(
	'Can edit a folder in the Contents section',
	{tag: '@LPD-92348'},
	async ({apiHelpers, assetsPage, page}) => {
		const folderTitle = getRandomString();

		const folder = await apiHelpers.objectFolder.createObjectEntryFolder({
			parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			scopeKey: 'Default',
			title: folderTitle,
		});

		try {
			await assetsPage.gotoContents();

			await assetsPage.execItemAction({
				action: 'Edit',
				filter: folderTitle,
			});

			const newFolderTitle = getRandomString();

			const nameInput = page.getByLabel('Name');

			await expect(nameInput).toHaveValue(folderTitle);

			await nameInput.fill(newFolderTitle);

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(
				page,
				`Success:${newFolderTitle} was updated successfully.`
			);

			await expect(
				page.getByRole('link', {name: newFolderTitle})
			).toBeVisible();
		}
		finally {
			await apiHelpers.objectFolder.deleteObjectEntryFolder(folder.id);
		}
	}
);

test(
	'Can edit a folder in a Space',
	{tag: '@LPD-92348'},
	async ({apiHelpers, assetsPage, page}) => {
		const folderTitle = getRandomString();
		const spaceName = `Space ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		await apiHelpers.objectFolder.createObjectEntryFolder({
			parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			scopeKey: space.assetLibraryKey,
			title: folderTitle,
		});

		await assetsPage.gotoSpaceContents(spaceName);

		await assetsPage.execItemAction({
			action: 'Edit',
			filter: folderTitle,
		});

		const newFolderTitle = getRandomString();

		const nameInput = page.getByLabel('Name');

		await expect(nameInput).toHaveValue(folderTitle);

		await nameInput.fill(newFolderTitle);

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			page,
			`Success:${newFolderTitle} was updated successfully.`
		);

		await expect(
			page.getByRole('link', {name: newFolderTitle})
		).toBeVisible();
	}
);

test(
	'Can grant a folder permission to a Space role',
	{tag: '@LPD-92348'},
	async ({apiHelpers, assetsPage, page}) => {
		const folderTitle = getRandomString();
		const spaceName = `Space ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		await apiHelpers.objectFolder.createObjectEntryFolder({
			parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			scopeKey: space.assetLibraryKey,
			title: folderTitle,
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
		await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			space.externalReferenceCode,
			user.externalReferenceCode
		);

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			space.externalReferenceCode,
			user.externalReferenceCode,
			['Asset Library Member']
		);

		// Grant the Space member role permission to update the folder

		await assetsPage.gotoSpaceContents(spaceName);

		const permissionsMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Permissions',
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: permissionsMenuItem.first(),
			trigger: page.getByRole('button', {
				name: `${folderTitle} Actions`,
			}),
		});

		await permissionsMenuItem.first().hover();

		await expect(permissionsMenuItem).toHaveCount(2);

		await permissionsMenuItem.last().click();

		const permissionsPage = new PermissionsPage(page);

		await permissionsPage.checkPermissionsAndSave([
			{action: 'UPDATE', role: 'Asset Library Member'},
		]);

		// Switch to the Space member and verify the granted permission

		await performUserSwitch(page, user.alternateName);

		await assetsPage.gotoSpaceContents(spaceName);

		await clickAndExpectToBeVisible({
			target: page.getByRole('menuitem', {
				exact: true,
				name: 'View Folder',
			}),
			trigger: page.getByRole('button', {
				name: `${folderTitle} Actions`,
			}),
		});

		await expect(
			page.getByRole('menuitem', {exact: true, name: 'Edit'})
		).toBeVisible();
	}
);

test(
	'The All section creation menu does not offer a New Folder option',
	{tag: '@LPD-92348'},
	async ({assetsPage, page}) => {
		await assetsPage.gotoAll();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Basic Web Content'}),
			trigger: assetsPage.newButton,
		});

		await expect(
			page.getByRole('menuitem', {exact: true, name: 'Folder'})
		).toBeHidden();
	}
);
