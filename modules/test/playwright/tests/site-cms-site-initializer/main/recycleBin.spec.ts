/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {addCMSAdministrator} from '../../../utils/addCMSAdministrator';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performUserSwitchViaApi,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {RecycleBinPage} from './pages/RecycleBinPage';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

test.beforeAll(async ({browser}) => {
	const newPage = await browser.newPage();

	const recycleBinPage = new RecycleBinPage(newPage);

	await performLoginViaApi({page: newPage, screenName: 'test'});

	await recycleBinPage.goto();

	const emptyRecycleBinMessage = newPage.getByText(
		'The Recycle Bin is empty.'
	);

	await Promise.race([
		emptyRecycleBinMessage.waitFor({state: 'visible'}),
		recycleBinPage.selectAllItemsCheckbox.waitFor({state: 'visible'}),
	]);

	if (await emptyRecycleBinMessage.isVisible()) {
		await newPage.close();

		return;
	}

	await recycleBinPage.selectAllItemsCheckbox.check();

	await recycleBinPage.tableActions.click();

	await newPage.getByRole('menuitem', {name: 'Delete'}).click();

	await recycleBinPage.modalDeleteEntriesButton.click();

	expect.poll(
		async () => {
			await newPage.reload();

			await expect(emptyRecycleBinMessage).toBeVisible();
		},
		{
			intervals: [1000],
			timeout: 10_000,
		}
	);

	await newPage.close();
});

test(
	'Can delete a draft content from Recycle Bin',
	{tag: '@LPD-83737'},
	async ({apiHelpers, contentsPage, page, recycleBinPage}) => {
		const spaceName = getRandomString();

		await test.step('Create a new Space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});
		});

		await test.step('Create draft content', async () => {
			await contentsPage.goto();

			await contentsPage.createContent('Basic Web Content', spaceName);

			await page.getByTitle('Back').click();
		});

		await test.step('Delete the created content so it goes into the Recycle Bin', async () => {
			await contentsPage.deleteContent('Untitled Asset');
		});

		await test.step('Go to the Recycle Bin and delete the content permanently', async () => {
			await recycleBinPage.goto();

			await page
				.getByRole('row', {name: 'Untitled Asset'})
				.getByRole('button')
				.click();

			await page.getByRole('menuitem', {name: 'Delete'}).click();

			await page.getByLabel('Delete').waitFor({state: 'visible'});

			await expect(
				recycleBinPage.deleteItemConfirmationText
			).toBeVisible();

			await recycleBinPage.deleteButton.last().click();

			await waitForAlert(
				page,
				`Success:Untitled Asset has been permanently deleted.`
			);
		});
	}
);

test(
	'Can delete a single content from Recycle Bin',
	{tag: '@LPD-55831'},
	async ({apiHelpers, contentsPage, page, recycleBinPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const contentName = getRandomString();
		const spaceName = getRandomString();

		await test.step('Create a new Space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentName,
			},
			applicationName,
			spaceName
		);

		await test.step('Delete the created content so it goes into the Recycle Bin', async () => {
			await contentsPage.goto();

			await contentsPage.deleteContent(contentName);
		});

		await test.step('Go to the Recycle Bin and delete the content permanently', async () => {
			await recycleBinPage.goto();

			await page
				.getByRole('row', {name: contentName})
				.getByRole('button')
				.click();

			await page.getByRole('menuitem', {name: 'Delete'}).click();

			await page.getByLabel('Delete').waitFor({state: 'visible'});

			await checkAccessibility({
				page,
				selectors: ['.modal-content'],
			});

			await expect(
				recycleBinPage.deleteItemConfirmationText
			).toBeVisible();

			await recycleBinPage.deleteButton.last().click();

			await waitForAlert(
				page,
				`Success:${contentName} has been permanently deleted.`
			);
		});
	}
);

test(
	'Can delete permanently multiple contents from Recycle Bin',
	{tag: '@LPD-62787'},
	async ({apiHelpers, contentsPage, page, recycleBinPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const contentName1 = getRandomString();
		const contentName2 = getRandomString();
		const spaceName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
				trashEnabled: true,
			},
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentName1,
			},
			applicationName,
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentName2,
			},
			applicationName,
			spaceName
		);

		await test.step('Login as CMS Administrator', async () => {
			const user = await addCMSAdministrator(apiHelpers);

			await performUserSwitchViaApi(page, user.alternateName);
		});

		await test.step('Delete the contents so they can go into the Recycle Bin', async () => {
			await contentsPage.goto();

			await contentsPage.deleteContent(contentName1);

			await contentsPage.deleteContent(contentName2);
		});

		await test.step('Go to the Recycle Bin and delete the contents permanently', async () => {
			await recycleBinPage.goto();

			await recycleBinPage.selectItems([contentName1, contentName2]);

			await page
				.getByTestId('visualization-mode-table')
				.getByLabel('Actions')
				.click();

			await page.getByRole('menuitem', {name: 'Delete'}).click();

			await expect(
				page.getByText('You are about to permanently delete 2 items.')
			).toBeVisible();

			await checkAccessibility({
				page,
				selectors: ['.modal-content'],
			});

			await page.getByRole('button', {name: 'Delete'}).click();

			await waitForAlert(
				page,
				'Info:Delete action started for 2 assets.',
				{
					type: 'info',
				}
			);

			await waitForAlert(
				page,
				`Success:2 assets were successfully deleted.`,
				{first: true}
			);

			await expect(
				page.getByRole('cell', {name: contentName1})
			).toBeHidden();
			await expect(
				page.getByRole('cell', {name: contentName2})
			).toBeHidden();
		});
	}
);

test(
	'Can empty the Recycle Bin',
	{tag: '@LPD-62787'},
	async ({apiHelpers, contentsPage, page, recycleBinPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const contentName1 = getRandomString();
		const contentName2 = getRandomString();
		const spaceName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
				trashEnabled: true,
			},
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentName1,
			},
			applicationName,
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentName2,
			},
			applicationName,
			spaceName
		);

		await test.step('Delete the contents so they can go into the Recycle Bin', async () => {
			await contentsPage.goto();

			await contentsPage.deleteContent(contentName1);

			await contentsPage.deleteContent(contentName2);
		});

		await test.step('Go to the Recycle Bin and empty it', async () => {
			await recycleBinPage.goto();

			await page.getByLabel('More Actions').click();

			await recycleBinPage.emptyRecycleBinButton.waitFor({
				state: 'visible',
			});

			await recycleBinPage.emptyRecycleBinButton.click();

			await expect(
				page.getByText(
					'This will permanently delete all items in the recycle bin.'
				)
			).toBeVisible();

			await page.getByRole('button', {name: 'Empty Bin'}).click();

			await waitForAlert(
				page,
				'Info:Delete action started for all assets.',
				{type: 'info'}
			);

			await page.reload();

			await expect(
				page.getByRole('cell', {name: contentName1})
			).toBeHidden();
			await expect(
				page.getByRole('cell', {name: contentName2})
			).toBeHidden();
		});
	}
);

test(
	'Can restore a single content from Recycle Bin',
	{tag: '@LPD-55830'},
	async ({apiHelpers, contentsPage, page, recycleBinPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const contentName = getRandomString();
		const spaceName = getRandomString();

		await test.step('Create a new Space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentName,
			},
			applicationName,
			spaceName
		);

		await test.step('Delete the created content so it goes into the Recycle Bin', async () => {
			await contentsPage.goto();

			await contentsPage.deleteContent(contentName);
		});

		await test.step('Go to the Recycle Bin and restore the item ', async () => {
			await recycleBinPage.goto();

			await page
				.getByRole('row', {name: contentName})
				.getByRole('button')
				.click();

			await page.getByRole('menuitem', {name: 'Restore'}).click();

			await waitForAlert(
				page,
				`Success:${contentName} was restored to Contents.`,
				{autoClose: false}
			);

			await checkAccessibility({
				page,
				selectors: ['.alert-success'],
			});
		});

		await test.step('Assert item is restored', async () => {
			await page.getByRole('link', {name: 'Contents'}).click();

			await expect(
				page.getByRole('row', {name: contentName})
			).toBeVisible();
		});

		await test.step('Clean up', async () => {
			await contentsPage.deleteContent(contentName);

			await recycleBinPage.goto();

			await page
				.getByRole('row', {name: contentName})
				.getByRole('button')
				.click();

			await page.getByRole('menuitem', {name: 'Delete'}).click();

			await expect(
				recycleBinPage.deleteItemConfirmationText
			).toBeVisible();

			await recycleBinPage.deleteButton.last().click();

			await waitForAlert(
				page,
				`Success:${contentName} has been permanently deleted.`
			);
		});
	}
);

test(
	'Can restore multiple contents from the Recycle Bin in bulk',
	{tag: '@LPD-87118'},
	async ({apiHelpers, contentsPage, page, recycleBinPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const contentName1 = getRandomString();
		const contentName2 = getRandomString();
		const spaceName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
				trashEnabled: true,
			},
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentName1,
			},
			applicationName,
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentName2,
			},
			applicationName,
			spaceName
		);

		await test.step('Delete the contents so they can go into the Recycle Bin', async () => {
			await contentsPage.goto();

			await contentsPage.deleteContent(contentName1);

			await contentsPage.deleteContent(contentName2);
		});

		await test.step('Bulk restore both contents from the Recycle Bin toolbar', async () => {
			await recycleBinPage.goto();

			await recycleBinPage.selectItems([contentName1, contentName2]);

			await page
				.getByTestId('visualization-mode-table')
				.getByLabel('Actions')
				.click();

			await page.getByRole('menuitem', {name: 'Restore'}).click();

			await waitForAlert(
				page,
				'Success:2 items were restored to their original locations.',
				{autoClose: false}
			);

			await checkAccessibility({
				page,
				selectors: ['.alert-success'],
			});
		});

		await test.step('Assert both items are back in the Contents folder', async () => {
			await contentsPage.goto();

			await expect(
				page.getByRole('row', {name: contentName1})
			).toBeVisible();
			await expect(
				page.getByRole('row', {name: contentName2})
			).toBeVisible();
		});

		await test.step('Clean up', async () => {
			await contentsPage.deleteContent(contentName1);
			await contentsPage.deleteContent(contentName2);
		});
	}
);

test(
	'Can restore a single content from the Recycle Bin bulk toolbar with a destination link in the toast',
	{tag: '@LPD-87118'},
	async ({apiHelpers, contentsPage, page, recycleBinPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const contentName = getRandomString();
		const spaceName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentName,
			},
			applicationName,
			spaceName
		);

		await test.step('Delete the content so it goes into the Recycle Bin', async () => {
			await contentsPage.goto();

			await contentsPage.deleteContent(contentName);
		});

		await test.step('Restore a single selected row through the bulk toolbar', async () => {
			await recycleBinPage.goto();

			await recycleBinPage.selectItems([contentName]);

			await page
				.getByTestId('visualization-mode-table')
				.getByLabel('Actions')
				.click();

			await page.getByRole('menuitem', {name: 'Restore'}).click();

			await waitForAlert(
				page,
				`Success:${contentName} was restored to Contents.`,
				{autoClose: false}
			);
		});

		await test.step('Destination link in the toast navigates to the Contents folder', async () => {
			await page
				.locator('.alert-success')
				.getByRole('link', {name: 'Contents'})
				.click();

			await expect(
				page.getByRole('row', {name: contentName})
			).toBeVisible();
		});

		await test.step('Clean up', async () => {
			await contentsPage.deleteContent(contentName);
		});
	}
);

test(
	'Can use the success toast options of undo and redirect to Recycle Bin after deleting content',
	{tag: '@LPD-53983'},
	async ({apiHelpers, contentsPage, page, recycleBinPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const contentName = getRandomString();
		const spaceName = getRandomString();

		await test.step('Create a new Space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentName,
			},
			applicationName,
			spaceName
		);

		await test.step('Delete the created content so it goes into the Recycle Bin', async () => {
			await contentsPage.goto();

			await contentsPage.deleteContent(contentName);

			await checkAccessibility({
				page,
				selectors: ['.alert-success'],
			});
		});

		await test.step('Restore the content using the undo button from the success toast', async () => {
			await page.getByRole('button', {name: 'Undo'}).click();

			await expect(
				page.getByText(`Success:${contentName} was restored`)
			).toBeVisible();
		});

		await test.step('Delete the content again and go to the Recycle Bin using the toast link', async () => {
			await contentsPage.deleteContent(contentName);

			await page.getByRole('link', {name: 'Recycle Bin'}).click();
		});

		await test.step('Delete the content from the Recycle Bin', async () => {
			await expect(
				page.getByRole('row', {name: contentName})
			).toBeVisible();

			await recycleBinPage.execItemAction({
				action: 'Delete',
				filter: contentName,
			});

			await expect(
				recycleBinPage.deleteItemConfirmationText
			).toBeVisible();

			await recycleBinPage.deleteButton.last().click();

			await waitForAlert(
				page,
				`Success:${contentName} has been permanently deleted.`
			);

			await expect(
				page.getByText(`Success:${contentName} was moved to the`)
			).toBeHidden();
		});
	}
);

test(
	'Default user can trash a CMS content and empty the Recycle Bin',
	{tag: '@LPD-83226'},
	async ({apiHelpers, contentsPage, page, recycleBinPage}) => {
		const contentName = getRandomString();
		const spaceName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {trashEnabled: true},
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentName,
			},
			'cms/basic-web-contents',
			spaceName
		);

		await test.step('Send the content to the Recycle Bin', async () => {
			await contentsPage.goto();

			await contentsPage.deleteContent(contentName);
		});

		await test.step('The content appears in the Recycle Bin', async () => {
			await recycleBinPage.goto();

			await expect(
				page.getByRole('row', {name: contentName})
			).toBeVisible();
		});

		await test.step('The Empty Recycle Bin option is available', async () => {
			await page.getByLabel('More Actions').click();

			await expect(recycleBinPage.emptyRecycleBinButton).toBeVisible();
		});

		await test.step('Empty the Recycle Bin and the content is gone', async () => {
			await recycleBinPage.emptyRecycleBinButton.click();

			await page.getByRole('button', {name: 'Empty Bin'}).click();

			await waitForAlert(
				page,
				'Info:Delete action started for all assets.',
				{type: 'info'}
			);

			await expect(async () => {
				await page.reload();

				await expect(
					page.getByRole('row', {name: contentName})
				).toBeHidden();
			}).toPass();
		});
	}
);

test(
	'Space member with delete permission sees trashed content and can empty the Recycle Bin',
	{tag: '@LPD-83226'},
	async ({apiHelpers, contentsPage, page, recycleBinPage}) => {
		const contentName = getRandomString();
		const spaceName = getRandomString();

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {trashEnabled: true},
			type: 'Space',
		});

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentName,
			},
			'cms/basic-web-contents',
			spaceName
		);

		const objectDefinition = await apiHelpers.get(
			`${apiHelpers.baseUrl}object-admin/v1.0/object-definitions/by-external-reference-code/L_CMS_BASIC_WEB_CONTENT`
		);

		const assetLibraryMemberRole =
			await apiHelpers.headlessAdminUser.getRoleByName(
				'Asset Library Member'
			);

		const companyId = await page.evaluate(() =>
			Liferay.ThemeDisplay.getCompanyId()
		);

		await test.step('Grant delete permission to Asset Library Member on the content', async () => {
			await apiHelpers.jsonWebServicesResourcePermissionApiHelper.setIndividualResourcePermissions(
				['DELETE', 'VIEW'],
				String(companyId),
				String(space.siteId),
				objectDefinition.className,
				String(objectEntry.id),
				String(assetLibraryMemberRole.id)
			);
		});

		await test.step('As the default user, trash the content', async () => {
			await contentsPage.goto();

			await contentsPage.deleteContent(contentName);
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await test.step('Create a user and add as space member', async () => {
			await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);

			await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

			await apiHelpers.jsonWebServicesUser.addGroupUsers(space.siteId, [
				user.id,
			]);
		});

		await test.step('Log in as the space member', async () => {
			await performUserSwitchViaApi(page, user.alternateName);
		});

		await test.step('The space member sees the trashed content', async () => {
			await recycleBinPage.goto();

			await expect(
				page.getByRole('row', {name: contentName})
			).toBeVisible();
		});

		await test.step('Empty the Recycle Bin and the content is gone', async () => {
			await page.getByLabel('More Actions').click();

			await recycleBinPage.emptyRecycleBinButton.click();

			await page.getByRole('button', {name: 'Empty Bin'}).click();

			await waitForAlert(
				page,
				'Info:Delete action started for all assets.',
				{type: 'info'}
			);

			await expect(async () => {
				await page.reload();

				await expect(
					page.getByRole('row', {name: contentName})
				).toBeHidden();
			}).toPass();
		});
	}
);

test(
	'Space member sees assets trashed by another user in their Space',
	{tag: '@LPD-89591'},
	async ({apiHelpers, contentsPage, page, recycleBinPage}) => {
		const contentName = getRandomString();
		const folderName = getRandomString();
		const spaceName = getRandomString();

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {trashEnabled: true},
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentName,
			},
			'cms/basic-web-contents',
			spaceName
		);

		await apiHelpers.objectFolder.createObjectEntryFolder({
			parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			scopeKey: spaceName,
			title: folderName,
		});

		await test.step('As the default user, trash the content', async () => {
			await contentsPage.goto();

			await contentsPage.deleteContent(contentName);
		});

		await test.step('As the default user, trash the folder', async () => {
			await contentsPage.goto();

			await contentsPage.deleteFolder(folderName);
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await test.step('Create a user and add as space member', async () => {
			await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);

			await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

			await apiHelpers.jsonWebServicesUser.addGroupUsers(space.siteId, [
				user.id,
			]);
		});

		await test.step('Log in as the space member', async () => {
			await performUserSwitchViaApi(page, user.alternateName);
		});

		await test.step('The space member sees the trashed content', async () => {
			await recycleBinPage.goto();

			await expect(
				page.getByRole('row', {name: contentName})
			).toBeVisible();
		});

		await test.step('The space member sees the trashed folder', async () => {
			await recycleBinPage.goto();

			await expect(
				page.getByRole('row', {name: folderName})
			).toBeVisible();
		});

		await test.step('The bulk actions toolbar is not available without delete permission', async () => {
			await recycleBinPage.selectItems([contentName]);

			await expect(
				page
					.getByTestId('visualization-mode-table')
					.getByLabel('Actions')
			).toBeHidden();
		});
	}
);

test(
	'Recycle Bin row shows Space, Content Type, Removed By, and Removed Date',
	{tag: '@LPD-89104'},
	async ({apiHelpers, contentsPage, page, recycleBinPage}) => {
		const contentName = getRandomString();
		const spaceName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {trashEnabled: true},
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentName,
			},
			'cms/basic-web-contents',
			spaceName
		);

		await test.step('Trash the content', async () => {
			await contentsPage.goto();

			await contentsPage.deleteContent(contentName);
		});

		await test.step('Row exposes the four metadata columns', async () => {
			await recycleBinPage.goto();

			const row = page.getByRole('row', {name: contentName});

			await expect(row).toContainText(spaceName);
			await expect(row).toContainText('Basic Web Content');
			await expect(row).toContainText('Test Test');

			await expect(row).toContainText(String(new Date().getFullYear()));
		});
	}
);

test(
	'Filtering the Recycle Bin by Space narrows results to that space',
	{tag: '@LPD-89104'},
	async ({apiHelpers, contentsPage, page, recycleBinPage}) => {
		const spaceName1 = getRandomString();
		const spaceName2 = getRandomString();
		const contentName1 = getRandomString();
		const contentName2 = getRandomString();

		const createdSpaces: Record<string, any> = {};

		for (const name of [spaceName1, spaceName2]) {
			createdSpaces[name] =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name,
					settings: {trashEnabled: true},
					type: 'Space',
				});
		}

		for (const [space, content] of [
			[spaceName1, contentName1],
			[spaceName2, contentName2],
		]) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: content,
				},
				'cms/basic-web-contents',
				space
			);
		}

		await test.step('Trash one content per space', async () => {
			for (const content of [contentName1, contentName2]) {
				await contentsPage.goto();

				await contentsPage.deleteContent(content);
			}
		});

		await test.step('Both rows are visible with no filter applied', async () => {
			await recycleBinPage.goto();

			await expect(
				page.getByRole('row', {name: contentName1})
			).toBeVisible();
			await expect(
				page.getByRole('row', {name: contentName2})
			).toBeVisible();
		});

		await test.step('Apply a Space filter limited to Space A', async () => {
			await page.getByRole('button', {name: 'Filter'}).click();

			await page.getByRole('menuitem', {name: 'Space'}).click();

			await page
				.getByRole('checkbox', {
					name: createdSpaces[spaceName1].assetLibraryKey,
				})
				.check();

			await page.getByRole('button', {name: 'Add Filter'}).click();
		});

		await test.step('Only Space A content remains visible', async () => {
			await expect(
				page.getByRole('row', {name: contentName1})
			).toBeVisible();
			await expect(
				page.getByRole('row', {name: contentName2})
			).toBeHidden();
		});
	}
);

test(
	'The Recycle Bin filter menu does not offer the extension filter',
	{tag: '@LPD-95408'},
	async ({apiHelpers, contentsPage, page, recycleBinPage}) => {
		const contentName = getRandomString();
		const spaceName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {trashEnabled: true},
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentName,
			},
			'cms/basic-web-contents',
			spaceName
		);

		await test.step('Trash the content', async () => {
			await contentsPage.goto();

			await contentsPage.deleteContent(contentName);
		});

		await test.step('The filter menu offers Space but not Extension', async () => {
			await recycleBinPage.goto();

			await expect(
				page.getByRole('row', {name: contentName})
			).toBeVisible();

			await page.getByRole('button', {name: 'Filter'}).click();

			await expect(
				page.getByRole('menuitem', {name: 'Space'})
			).toBeVisible();

			await expect(
				page.getByRole('menuitem', {name: 'Extension'})
			).toBeHidden();
		});
	}
);

test(
	'The Recycle Bin filter menu groups the filters by type',
	{tag: '@LPD-95407'},
	async ({apiHelpers, contentsPage, page, recycleBinPage}) => {
		const contentName = getRandomString();
		const spaceName = getRandomString();

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {trashEnabled: true},
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentName,
			},
			'cms/basic-web-contents',
			spaceName
		);

		await test.step('Trash the content', async () => {
			await contentsPage.goto();

			await contentsPage.deleteContent(contentName);
		});

		await test.step('The filter menu shows the filters grouped', async () => {
			await recycleBinPage.goto();

			await expect(
				page.getByRole('row', {name: contentName})
			).toBeVisible();

			await page.getByRole('button', {name: 'Filter'}).click();

			await expect(
				page
					.getByRole('group', {exact: true, name: 'Filter By'})
					.getByRole('menuitem', {name: 'Space'})
			).toBeVisible();

			await expect(
				page
					.getByRole('group', {name: 'Filter by Date'})
					.getByRole('menuitem', {name: 'Create Date'})
			).toBeVisible();
		});
	}
);

test(
	'Space General Settings Recycle Bin panel honors trashEnabled and max age validation',
	{tag: '@LPD-89104'},
	async ({apiHelpers, page}) => {
		const spaceName = getRandomString();

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		const {classNameId} =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				'com.liferay.depot.model.DepotEntry'
			);

		await page.goto(`/web/cms/e/space-settings/${classNameId}/${space.id}`);

		const enableCheckbox = page.getByRole('checkbox', {
			name: 'Enable Recycle Bin',
		});
		const maxAgeField = page.getByRole('spinbutton', {
			name: 'Trash Entries Max Age',
		});
		const saveButton = page.getByRole('button', {name: 'Save'});

		await test.step('Panel renders with the checkbox checked and the max age field visible by default', async () => {
			await expect(enableCheckbox).toBeChecked();
			await expect(maxAgeField).toBeVisible();
		});

		await test.step('Disabling the bin hides the max age field', async () => {
			await enableCheckbox.uncheck();
			await expect(maxAgeField).toBeHidden();
		});

		await test.step('Re-enabling the bin shows the max age field again', async () => {
			await enableCheckbox.check();
			await expect(maxAgeField).toBeVisible();
		});

		await test.step('Max age is required when the bin is enabled', async () => {
			await maxAgeField.fill('');
			await saveButton.click();
			await expect(
				page.getByText('This field is required.')
			).toBeVisible();
		});

		await test.step('Save succeeds when the bin is disabled with no max age', async () => {
			await enableCheckbox.uncheck();
			await expect(maxAgeField).toBeHidden();
			await saveButton.click();
			await waitForAlert(
				page,
				`Success:${spaceName} was saved successfully`
			);
		});
	}
);
