/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForModal} from '../../../utils/waitFor';
import {waitForAlert} from '../../../utils/waitForAlert';
import {checkInZip} from '../../../utils/zip';
import {DefaultPermissionsPage} from '../permissions/pages/DefaultPermissionsPage';
import {PermissionsPage} from '../permissions/pages/PermissionsPage';
import {structureBuilderPagesTest} from '../structure-builder/fixtures/structureBuilderPagesTest';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const imageFileBase64 =
	'iVBORw0KGgoAAAANSUhEUgAAAD0AAAAXCAIAAAA3N9DuAAAAA3NCSVQICAjb4U/gAAAAEHRFWHRTb2Z0d2FyZQBTaHV0dGVyY4LQCQAABX9JREFUWMOVWFuS3EgOA8BUlXpifuYee4u9/3nWdonEfDAzS91+RKw+HOooKUmCAEiZ//3nP7ZJ4jeX7XR+v15VBizSgO2Q+iarAIoMMcv9FgkbBAyMoI0s7yD9EwkDNiJYZRshAihb69Eqg+igJLheV7k66XKVqxPtd668Ml8kg3GOI0QDaZRB8qrKLBtlhEjilSXO2B24k77SWRbRvxIQYVjiDGV4HbLfLTurIkiybAAE+qiyh6jOUlRnTLI7MGIs8Bgc58C36+XGwBhS2SGGomEWmeURFJHlhr9DShB5pUmTrLKkKneRIEIsG4bIxluk55M03ijMbAFU1c7vfpOZmyrllMZfj49DDFFilm280k2ARqtrLlti2VfWBK9w5aytyiJg9yEkYPRpNsiJ9ysL8xxwVtcs8JUWAElfON0ZR8QuQ5z3j3Gex5PElfXKJPDKjFVGh5EaRhxDG6QmdFcIshbBslxLFTN6OctDnKcRV7rFAGAEI6gvisxKAArh95crz/E8H3FEADgivv3IbnRnUGUDIb6yMn2lR9ALrbJhj+BOdyfUD4zgFGhZxMYbt+q0VdgFhALAEu6vr4ijKs/xHEECIzhCTfFGtNEt4zyi/2ySvH1GrCUAACGGeGWrhd9fWSurMmxzNSHTACo9GyryXQC6AP1siLsnZJD6OD4eQ53T5mWVSXT3s/y6agRJRNx8xsDUH0hg8afsso8Rn6AlSYok0ZIAIc+y3g8aBpDVnlir7uKqLRSc9eIxzueYTduV2t7AP4/49iNtvK7qVofocsu3jCuduTGZNkoR7AtdTKO+K1HbwB3O+8Xlkv0Mya6EoO02osc4/36em7sSSdrdQ2R5hNq5bVSrkOBS6tD0uJ5BTYw2l+m5mHNHYmdvW+0/d1f55ey8WeRE13C/UlVS/PV4PoeaBp3QHpDdm86AgFaik4qk4a6n/ZHLOwiQ7ZPommcGPXS6Hz1rCKBe+pS6vyi1n3oPLAmAGOfx8YiYLxiA28WwPDHEgv3WnNuwuyEjKPEqw+b0ykYeLu9zRFRZk453auuoT5zh/uVP5mjbPsbjHNEairZxslqFbR3kPuhtmkY1hYwRcsO7RHyne/fqjXfd3PBO9DtnWK/fyaCqloo44nlE9LwkcGW1XZI0sCcMyRFqF+KyvKZKO49tENIcMS6X3fvJMhnM/Ysk8/vPDti2aB2/ZL9dWxvdlqHjHM8h7r2KxFX+VLMXCTits827DdQAyUz3iWWDcwtoAxAA4p0o49zrivJ/70iebvgHz7nbDqnz+BihKtjIdFZdWe8dkGvxMLD/bZdsmqwFBvd6PcmjBql5Uq50eU1NHX8DDomLlEOB/+c6x3kOHYMGhjRC0wRbW0trIf7IypwDq6dYdeprLRHZm8n0KIF7Tx+KoAj2lnJVAr3Ru9ybHT+p9feV0NeykXN0yB4oBBaPq3xllZ3pI9QEaOPvydKCjlVeD/krTUCe697sdboM3ylu1Oa089tbvr0nVeJXVmOOfX+M8zxOco5MrY1A0wHfHttxehXZnrNJomDvFK8srR2ogkpXCz+rdurBaLcu23p82VUIhiKoPxNG0DnOj0d01LTJxeP+7tLien9kiHMuqpcldltG0Pb5CO0RaDT+L4G9nzRtyiYIWGtO3SVoOKvSRXDb7c9jyzDJ8/h4jngewcasU19LIoCmafvCVuf+eCNxpY+hKy3DdpGCC2Dx0V0biqtyB+51oNuy8rm2LbK+++bsWQnnZyFMLzri+RzPkHr1bZb3l47IH9ccJFWG3Z8dNvZe1VwfncS0IApoRZfITjp9I7dNMF2ihsKMTRjrKdLvOSDwq15FNQSiPo5nEPu7rtlse4i5uFHzvwB4rc60b/aU/Rc6sWizbSKbGQAAAABJRU5ErkJggg==';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPD-17564': {enabled: true},
	}),
	loginTest(),
	structureBuilderPagesTest
);

test.beforeEach(async ({apiHelpers, page}) => {
	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[user.alternateName] = {
		name: user.givenName,
		password: 'test',
		surname: user.familyName,
	};

	const cmsAdminRole =
		await apiHelpers.headlessAdminUser.getRoleByName('CMS Administrator');

	await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
		cmsAdminRole.id,
		Number(user.id)
	);

	await performUserSwitch(page, user.alternateName);
});

test.afterEach(async ({apiHelpers}) => {
	const tasks = await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
		'cms/bulk-action-tasks'
	);

	for (const task of tasks.items) {
		await apiHelpers.objectEntry.deleteObjectEntry(
			'cms/bulk-action-tasks',
			task.id
		);
	}
});

test(
	'Can delete over a Select All expanded selection from a multiple spaces view',
	{tag: '@LPD-87393'},
	async ({apiHelpers, assetsPage, page}) => {
		for (let i = 0; i < 21; i++) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: `title ${getRandomString()}`,
				},
				'cms/basic-web-contents',
				'Default'
			);
		}

		await assetsPage.gotoAll();

		await assetsPage.selectAllItems(true);

		await page
			.getByTestId(/visualization-mode/)
			.getByLabel('Actions')
			.click();

		await page.getByRole('menuitem', {name: 'Delete'}).click();

		await expect(
			page.getByText(
				'You are about to delete the selected items from multiple spaces'
			)
		).toBeVisible();

		await page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(page, 'Info:Delete action started for all assets.', {
			type: 'info',
		});

		await waitForAlert(
			page,
			`Success:All items were successfully deleted.`,
			{first: true}
		);

		await expect(page.getByText('No Assets Yet')).toBeVisible();
	}
);

test(
	'Can delete over a Select All expanded selection from a single space view with Recycle Bin disabled',
	{tag: '@LPD-87393'},
	async ({apiHelpers, assetsPage, page}) => {
		const spaceName = `Space ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
				trashEnabled: false,
			},
			type: 'Space',
		});

		for (let i = 0; i < 21; i++) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: `title ${getRandomString()}`,
				},
				'cms/basic-web-contents',
				spaceName
			);
		}

		await assetsPage.gotoContents(spaceName);

		await assetsPage.selectAllItems(true);

		await page
			.getByTestId(/visualization-mode/)
			.getByLabel('Actions')
			.click();

		await page.getByRole('menuitem', {name: 'Delete'}).click();

		await expect(
			page.getByText('You are about to permanently delete all items')
		).toBeVisible();

		await page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(page, 'Info:Delete action started for all assets.', {
			type: 'info',
		});

		await waitForAlert(
			page,
			`Success:All items were successfully deleted.`,
			{first: true}
		);

		await expect(page.getByText('No Content Yet')).toBeVisible();
	}
);

test(
	'Can delete over a Select All expanded selection from a single space view with Recycle Bin enabled',
	{tag: '@LPD-87393'},
	async ({apiHelpers, assetsPage, page}) => {
		const spaceName = `Space ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
				trashEnabled: true,
			},
			type: 'Space',
		});

		for (let i = 0; i < 21; i++) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: `title ${getRandomString()}`,
				},
				'cms/basic-web-contents',
				spaceName
			);
		}

		await assetsPage.gotoContents(spaceName);

		await assetsPage.selectAllItems(true);

		await page
			.getByTestId(/visualization-mode/)
			.getByLabel('Actions')
			.click();

		await page.getByRole('menuitem', {name: 'Delete'}).click();

		await waitForAlert(page, 'Info:Delete action started for all assets.', {
			type: 'info',
		});

		await waitForAlert(
			page,
			`Success:All items were successfully deleted.`,
			{first: true}
		);

		await expect(page.getByText('No Content Yet')).toBeVisible();
	}
);

test(
	'Can delete multiple contents across spaces with and without recycle bin enabled',
	{tag: '@LPD-62787'},
	async ({apiHelpers, assetsPage, page, recycleBinPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceNameWithRecycleBin = `Space ${getRandomString()}`;
		const spaceNameWithoutRecycleBin = `Space ${getRandomString()}`;
		const file1Title = `title ${getRandomString()}`;
		const file2Title = `title ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceNameWithRecycleBin,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
				trashEnabled: true,
			},
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceNameWithoutRecycleBin,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
				trashEnabled: false,
			},
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file1Title,
			},
			applicationName,
			spaceNameWithRecycleBin
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file2Title,
			},
			applicationName,
			spaceNameWithoutRecycleBin
		);

		await assetsPage.gotoAll();

		await assetsPage.selectItems([file1Title, file2Title]);

		await page
			.getByTestId(/visualization-mode/)
			.getByLabel('Actions')
			.click();

		await page.getByRole('menuitem', {name: 'Delete'}).click();

		await expect(
			page.getByText(
				'You are about to delete the selected items from multiple spaces'
			)
		).toBeVisible();

		await page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(page, 'Info:Delete action started for 2 assets.', {
			type: 'info',
		});

		await expect(
			page.getByRole('cell', {exact: true, name: file1Title})
		).not.toBeVisible();
		await expect(
			page.getByRole('cell', {exact: true, name: file2Title})
		).not.toBeVisible();

		await recycleBinPage.goto();

		await expect(
			page.getByRole('cell', {exact: true, name: file1Title})
		).toBeVisible();
	}
);

test(
	'Can delete multiple contents in a space with recycle bin disabled',
	{tag: '@LPD-62787'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = `Space ${getRandomString()}`;
		const file1Title = `title ${getRandomString()}`;
		const file2Title = `title ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
				trashEnabled: false,
			},
			type: 'Space',
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file1Title,
			},
			applicationName,
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file2Title,
			},
			applicationName,
			spaceName
		);

		await assetsPage.gotoAll();

		await assetsPage.selectItems([file1Title, file2Title]);

		await page
			.getByTestId(/visualization-mode/)
			.getByLabel('Actions')
			.click();

		await page.getByRole('menuitem', {name: 'Delete'}).click();

		await expect(
			page.getByText('You are about to permanently')
		).toBeVisible();

		await page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(page, 'Info:Delete action started for 2 assets.', {
			type: 'info',
		});

		await expect(
			page.getByRole('cell', {exact: true, name: file1Title})
		).not.toBeVisible();
		await expect(
			page.getByRole('cell', {exact: true, name: file2Title})
		).not.toBeVisible();
	}
);

test(
	'Can delete multiple contents in a space with recycle bin enabled',
	{tag: '@LPD-62787'},
	async ({apiHelpers, assetsPage, page, recycleBinPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = `Space ${getRandomString()}`;
		const file1Title = `title ${getRandomString()}`;
		const file2Title = `title ${getRandomString()}`;

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
				title: file1Title,
			},
			applicationName,
			spaceName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: file2Title,
			},
			applicationName,
			spaceName
		);

		await assetsPage.gotoAll();

		await assetsPage.selectItems([file1Title, file2Title]);

		await page
			.getByTestId('visualization-mode-table')
			.getByLabel('Actions')
			.click();

		await page.getByRole('menuitem', {name: 'Delete'}).click();

		await waitForAlert(page, 'Info:Delete action started for 2 assets.', {
			type: 'info',
		});

		await expect(
			page.getByRole('cell', {exact: true, name: file1Title})
		).not.toBeVisible();
		await expect(
			page.getByRole('cell', {exact: true, name: file2Title})
		).not.toBeVisible();

		await recycleBinPage.goto();

		await expect(
			page.getByRole('cell', {exact: true, name: file1Title})
		).toBeVisible();
		await expect(
			page.getByRole('cell', {exact: true, name: file2Title})
		).toBeVisible();
	}
);

test(
	'Bulk Actions Monitor component',
	{tag: ['@LPD-57835', '@LPD-74095', '@LPD-74096']},
	async ({apiHelpers, assetsPage, page}) => {
		const basicWebContent = 'cms/basic-web-contents';
		const bulkActionTasks = 'cms/bulk-action-tasks';
		const spaceName = 'Default';

		const filesNames = [
			getRandomString(),
			getRandomString(),
			getRandomString(),
		];
		let tasks;

		for (const fileName of filesNames) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: fileName,
				},
				basicWebContent,
				spaceName
			);
		}

		await test.step('Select 1 asset and delete it using the Bulk Action', async () => {
			await assetsPage.gotoAll();

			await assetsPage
				.getItem(filesNames[0])
				.locator('input[title="Select Item"]')
				.check();
			await assetsPage.execBulkItemAction('Delete');

			await waitForAlert(
				page,
				`Info:Delete action started for ${filesNames[0]} asset.`,
				{
					autoClose: true,
					type: 'info',
				}
			);
		});

		await test.step('Check that the processingTask button appear and click on it', async () => {
			await expect(assetsPage.processingTasksButton).toBeVisible();

			await assetsPage.processingTasksButton.click();
		});

		await test.step('After the click, the dropdown component is shown and 1 task with details is visible', async () => {
			await expect(
				assetsPage
					.taskStatusDropdownItemButton('Assets Deletion')
					.nth(0)
			).toBeVisible();

			await expect(assetsPage.taskStatusDropdownList).toContainText(
				'1 Items'
			);
			await expect(assetsPage.taskStatusDropdownList).toContainText(
				'a few seconds ago'
			);
			await expect(assetsPage.taskStatusDropdownList).toContainText(
				'Completed'
			);

			await expect(assetsPage.viewAllTasksLink).toBeVisible();
		});

		await test.step('Go to View All Task redirect to the Task Report page and check that Result column show the correct results', async () => {
			tasks =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					bulkActionTasks
				);

			await assetsPage.viewAllTasksLink.click();

			await expect(
				page.getByRole('heading', {
					exact: true,
					name: 'Task Report',
				})
			).toBeVisible();

			const row = page.getByRole('row').filter({
				hasText: tasks.items[0].id,
			});

			await expect(row).toBeVisible();
			await expect(
				row.getByRole('cell', {
					name: 'All Successful',
				})
			).toBeVisible();
			await expect(
				row
					.getByRole('cell', {
						name: 'All Successful',
					})
					.locator('.lexicon-icon-check-circle-full')
			).toBeVisible();

			await apiHelpers.objectEntry.patchObjectEntry(
				{
					executionStatus: {
						key: 'failed',
						name: 'Failed',
					},
					numberOfFailedItems: 3,
					numberOfSuccessfulItems: 3,
				},
				bulkActionTasks,
				tasks.items[0].id
			);

			await page.reload();

			await expect(
				page.getByRole('cell', {
					exact: true,
					name: '3 Successful 3 Failed',
				})
			).toBeVisible();
			await expect(
				page
					.getByRole('cell', {
						exact: true,
						name: '3 Successful 3 Failed',
					})
					.locator('.lexicon-icon-check-circle-full')
			).toBeVisible();
			await expect(
				page
					.getByRole('cell', {
						exact: true,
						name: '3 Successful 3 Failed',
					})
					.locator('.lexicon-icon-times-circle-full')
			).toBeVisible();

			await apiHelpers.objectEntry.patchObjectEntry(
				{
					executionStatus: {
						key: 'started',
						name: 'Started',
					},
					numberOfFailedItems: 0,
					numberOfSuccessfulItems: 0,
				},
				bulkActionTasks,
				tasks.items[0].id
			);

			await page.reload();

			await expect(
				page.getByRole('cell', {exact: true, name: 'Processing'})
			).toBeVisible();
			await expect(
				page
					.getByRole('cell', {exact: true, name: 'Processing'})
					.locator('.lexicon-icon-time')
			).toBeVisible();

			await apiHelpers.objectEntry.patchObjectEntry(
				{
					executionStatus: {
						key: 'failed',
						name: 'Failed',
					},
					numberOfFailedItems: 3,
					numberOfSuccessfulItems: 0,
				},
				bulkActionTasks,
				tasks.items[0].id
			);

			await expect(
				page.getByRole('cell', {exact: true, name: 'All Failed'})
			).toBeVisible();
			await expect(
				page
					.getByRole('cell', {exact: true, name: 'All Failed'})
					.locator('.lexicon-icon-times-circle-full')
			).toBeVisible();

			await assetsPage.gotoAll();
		});

		await test.step('Update the task status to Started', async () => {
			await apiHelpers.objectEntry.patchObjectEntry(
				{
					executionStatus: {
						key: 'started',
						name: 'Started',
					},
					numberOfFailedItems: 0,
					numberOfSuccessfulItems: 1,
				},
				bulkActionTasks,
				tasks.items[0].id
			);

			expect.poll(
				async () => {
					await expect(
						assetsPage.taskStatusFormsButton
					).toBeVisible();
				},
				{
					timeout: 5000,
				}
			);

			await assetsPage.taskStatusFormsButton.click();

			await expect(assetsPage.taskStatusDropdownList).toContainText(
				'Processing'
			);
		});

		await test.step('Select 2 assets and delete them using the Bulk Action', async () => {
			await expect(
				assetsPage
					.getItem(filesNames[0])
					.locator('input[title="Select Item"]')
			).not.toBeVisible();

			await assetsPage
				.getItem(filesNames[1])
				.locator('input[title="Select Item"]')
				.check();
			await assetsPage
				.getItem(filesNames[2])
				.locator('input[title="Select Item"]')
				.check();
			await assetsPage.execBulkItemAction('Delete');

			await waitForAlert(
				page,
				'Info:Delete action started for 2 assets.',
				{
					autoClose: true,
					type: 'info',
				}
			);

			await expect(assetsPage.processingTasksButton).toBeVisible();

			await assetsPage.processingTasksButton.click();

			await expect(assetsPage.taskStatusDropdownList).toContainText(
				'2 Items'
			);
			await expect(assetsPage.taskStatusDropdownList).toContainText(
				'a few seconds ago'
			);

			await expect(
				assetsPage
					.taskStatusDropdownItemButton('Assets Deletion')
					.nth(0)
			).toBeVisible();
			await expect(
				assetsPage
					.taskStatusDropdownItemButton('Assets Deletion')
					.nth(1)
			).toBeVisible();
		});

		await test.step('Update the status of the task to Failed', async () => {
			const processingTasks =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					bulkActionTasks,
					new URLSearchParams({
						filter: `executionStatus eq 'started'`,
					})
				);

			await apiHelpers.objectEntry.patchObjectEntry(
				{
					executionStatus: {
						key: 'failed',
						name: 'Failed',
					},
				},
				bulkActionTasks,
				processingTasks.items[0].id
			);

			expect.poll(
				async () => {
					await expect(
						assetsPage.taskStatusDropdownList
					).toContainText('Failed');
				},
				{
					timeout: 5000,
				}
			);
		});
	}
);

test(
	'Edit Categories in bulk',
	{tag: '@LPD-57835'},
	async ({apiHelpers, assetsPage, infoPanelPage, page}) => {
		const vocabularyName = getRandomString();

		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const vocabularyId = await apiHelpers.headlessAdminTaxonomy
			.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1}],
				assetTypes: [
					{
						required: false,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name: vocabularyName,
				siteId,
				visibilityType: 'PUBLIC',
			})
			.then((response) => response.id);

		const categoryName = getRandomString();

		await apiHelpers.headlessAdminTaxonomy
			.postTaxonomyVocabularyTaxonomyCategory({
				name: categoryName,
				vocabularyId,
			})
			.then((response) => response.id);

		apiHelpers.data.push({
			id: vocabularyId,
			type: 'taxonomyVocabulary',
		});

		const basicWebContent = 'cms/basic-web-contents';

		const fileNames = [
			getRandomString(),
			getRandomString(),
			getRandomString(),
		];

		for (const fileName of fileNames) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: fileName,
				},
				basicWebContent,
				'Default'
			);
		}

		await test.step('Select 3 assets and bulk edit their categories', async () => {
			await assetsPage.gotoAll();

			await assetsPage
				.getItem(fileNames[0])
				.locator('input[title="Select Item"]')
				.check();
			await assetsPage
				.getItem(fileNames[1])
				.locator('input[title="Select Item"]')
				.check();
			await assetsPage
				.getItem(fileNames[2])
				.locator('input[title="Select Item"]')
				.check();

			await assetsPage.execBulkItemAction('Edit Categories');

			await waitForModal({
				page,
			});
		});

		await test.step('Add a new category to the selected assets', async () => {
			const categoriesAutocomplete =
				page.getByPlaceholder('Add category');

			await categoriesAutocomplete.fill(categoryName);

			const option = page.getByRole('option', {name: categoryName});

			await option.waitFor();
			await option.click();

			const categoryLabel = page.locator('.label-item', {
				hasText: categoryName,
			});

			await expect(categoryLabel).toBeAttached();

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(
				page,
				'Info:Categories update action started for 3 assets.',
				{
					autoClose: true,
					type: 'info',
				}
			);
		});

		await test.step('Check that the "Processing Task" button appears and click on it', async () => {
			await expect(assetsPage.processingTasksButton).toBeVisible();

			await assetsPage.processingTasksButton.click();

			expect.poll(
				async () => {
					await expect(assetsPage.processingTasksButton).toBeHidden();
				},
				{
					timeout: 5000,
				}
			);

			await expect(assetsPage.taskStatusDropdownList).toContainText(
				'Completed'
			);
		});

		await test.step('After the click, the dropdown component is shown and 1 task with details is visible', async () => {
			await expect(
				assetsPage
					.taskStatusDropdownItemButton('Assets Categorization')
					.nth(0)
			).toBeVisible();

			await expect(assetsPage.taskStatusDropdownList).toContainText(
				'3 Items'
			);
		});

		await test.step('Verify the category has been correctly applied.', async () => {
			await page.reload();

			await assetsPage.execItemAction({
				action: 'Show Details',
				filter: fileNames[0],
			});

			await expect(
				page.getByRole('heading', {name: fileNames[0]})
			).toBeVisible();

			await infoPanelPage.selectTab('Categorization').click();

			await expect(
				page.getByText(categoryName, {exact: true})
			).toBeVisible();
		});
	}
);

test(
	'Edit Categories in bulk for different assets',
	{tag: '@LPD-76507'},
	async ({apiHelpers, assetsPage, infoPanelPage, page}) => {
		const vocabularyName = getRandomString();
		const fileNameImg = `file_${getRandomString()}.png`;
		const titleImg = `title ${getRandomString()}`;

		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const vocabularyId = await apiHelpers.headlessAdminTaxonomy
			.postSiteTaxonomyVocabulary({
				assetLibraries: [{id: -1}],
				assetTypes: [
					{
						required: false,
						subtype: 'AllAssetSubtypes',
						type: 'AllAssetTypes',
					},
				],
				name: vocabularyName,
				siteId,
				visibilityType: 'PUBLIC',
			})
			.then((response) => response.id);

		const categories = [
			getRandomString(),
			getRandomString(),
			getRandomString(),
		];

		for (const category of categories) {
			await apiHelpers.headlessAdminTaxonomy
				.postTaxonomyVocabularyTaxonomyCategory({
					name: category,
					vocabularyId,
				})
				.then((response) => response.id);

			apiHelpers.data.push({
				id: vocabularyId,
				type: 'taxonomyVocabulary',
			});
		}

		const basicWebContent = 'cms/basic-web-contents';
		const webContentNames = [getRandomString(), getRandomString()];

		for (const fileName of webContentNames) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: fileName,
				},
				basicWebContent,
				'Default'
			);
		}

		const folderName = `Folder ${getRandomInt()}`;

		const folder = await apiHelpers.objectFolder.createObjectEntryFolder({
			parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			scopeKey: 'Default',
			title: folderName,
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageFileBase64,
					name: fileNameImg,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: titleImg,
			},
			'cms/basic-documents',
			'Default'
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: getRandomString(),
				},
				objectEntryFolderExternalReferenceCode:
					folder.externalReferenceCode,
				title: `Content ${getRandomInt()}`,
			},
			basicWebContent,
			'Default'
		);

		await test.step('Select 3 assets including a folder and bulk edit their categories', async () => {
			await assetsPage.gotoContents();

			await assetsPage
				.getItem(webContentNames[0])
				.locator('input[title="Select Item"]')
				.check();
			await assetsPage
				.getItem(webContentNames[1])
				.locator('input[title="Select Item"]')
				.check();
			await assetsPage
				.getItem(folderName)
				.locator('input[title="Select Item"]')
				.check();

			await assetsPage.execBulkItemAction('Edit Categories');

			await waitForModal({
				page,
			});
		});

		await test.step('Add categories to the selected assets', async () => {
			const categoriesAutocomplete =
				page.getByPlaceholder('Add category');

			for (const category of categories) {
				await categoriesAutocomplete.fill(category);

				const option = page.getByRole('option', {name: category});

				await option.waitFor();
				await option.click();

				const categoryLabel = page.locator('.label-item', {
					hasText: category,
				});

				await expect(categoryLabel).toBeAttached();
			}

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(
				page,
				'Info:Categories update action started for 2 assets.',
				{
					autoClose: true,
					type: 'info',
				}
			);
		});

		await test.step('Check that the "Processing Task" button appears and click on it', async () => {
			await expect(assetsPage.processingTasksButton).toBeVisible();

			await assetsPage.processingTasksButton.click();

			expect.poll(
				async () => {
					await expect(assetsPage.processingTasksButton).toBeHidden();
				},
				{
					timeout: 5000,
				}
			);

			await expect(assetsPage.taskStatusDropdownList).toContainText(
				'Completed'
			);
		});

		await test.step('After the click, the dropdown component is shown and 1 task with details is visible', async () => {
			await expect(
				assetsPage
					.taskStatusDropdownItemButton('Assets Categorization')
					.nth(0)
			).toBeVisible();

			await expect(assetsPage.taskStatusDropdownList).toContainText(
				'2 Items'
			);
		});

		await test.step('Verify the categories has been correctly applied.', async () => {
			for (const webContentName of webContentNames) {
				await page.reload();

				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: webContentName,
				});

				await expect(
					page.getByRole('heading', {name: webContentName})
				).toBeVisible();

				await infoPanelPage.selectTab('Categorization').click();

				for (const category of categories) {
					await expect(
						page.getByText(category, {exact: true})
					).toBeVisible();
				}
			}
		});

		await test.step('Delete more than one category', async () => {
			await page.reload();

			await assetsPage
				.getItem(webContentNames[1])
				.locator('input[title="Select Item"]')
				.check();

			await assetsPage.execBulkItemAction('Edit Categories');

			await waitForModal({
				page,
			});

			await page
				.locator('span', {hasText: categories[0]})
				.getByRole('button', {name: 'Close'})
				.click();
			await page
				.locator('span', {hasText: categories[1]})
				.getByRole('button', {name: 'Close'})
				.click();

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(
				page,
				'Info:Categories update action started for one asset.',
				{
					autoClose: true,
					type: 'info',
				}
			);
		});

		await test.step('Verify the categories has been correctly removed.', async () => {
			await page.reload();

			await assetsPage.execItemAction({
				action: 'Show Details',
				filter: webContentNames[1],
			});

			await expect(
				page.getByRole('heading', {name: webContentNames[1]})
			).toBeVisible();

			await infoPanelPage.selectTab('Categorization').click();

			await expect(
				page.getByText(categories[2], {exact: true})
			).toBeVisible();
		});

		await test.step('Select 3 mixed assets and bulk edit their categories', async () => {
			await assetsPage.gotoAll();

			await assetsPage
				.getItem(webContentNames[0])
				.locator('input[title="Select Item"]')
				.check();
			await assetsPage
				.getItem(webContentNames[1])
				.locator('input[title="Select Item"]')
				.check();
			await assetsPage
				.getItem(titleImg)
				.locator('input[title="Select Item"]')
				.check();

			await assetsPage.execBulkItemAction('Edit Categories');

			await waitForModal({
				page,
			});
		});

		await test.step('Add and remove categories to the selected assets', async () => {
			const categoriesAutocomplete =
				page.getByPlaceholder('Add category');

			for (const category of categories) {
				await categoriesAutocomplete.fill(category);

				const option = page.getByRole('option', {name: category});

				await option.waitFor();
				await option.click();

				const categoryLabel = page.locator('.label-item', {
					hasText: category,
				});

				await expect(categoryLabel).toBeAttached();
			}

			await page
				.locator('span', {hasText: categories[1]})
				.getByRole('button', {name: 'Close'})
				.click();
			await page
				.locator('span', {hasText: categories[2]})
				.getByRole('button', {name: 'Close'})
				.click();

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(
				page,
				'Info:Categories update action started for 3 assets.',
				{
					autoClose: true,
					type: 'info',
				}
			);
		});

		await test.step('Verify the categories has been correctly edited.', async () => {
			await page.reload();

			for (const webContentName of webContentNames) {
				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: webContentName,
				});

				await expect(
					page.getByRole('heading', {name: webContentName})
				).toBeVisible();

				await infoPanelPage.selectTab('Categorization').click();

				await expect(
					page.getByText(categories[0], {exact: true})
				).toBeVisible();

				await assetsPage
					.getItem(webContentName)
					.locator('input[title="Select Item"]')
					.uncheck();
			}

			await assetsPage.execItemAction({
				action: 'Show Details',
				filter: titleImg,
			});

			await expect(
				page.getByRole('heading', {name: titleImg})
			).toBeVisible();

			await infoPanelPage.selectTab('Categorization').click();

			await expect(
				page.getByText(categories[0], {exact: true})
			).toBeVisible();
		});
	}
);

test(
	'Edit Tags in bulk for different assets',
	{tag: '@LPD-76507'},
	async ({apiHelpers, assetsPage, infoPanelPage, page}) => {
		const fileNameImg = `file_${getRandomString()}.png`;
		const titleImg = `title ${getRandomString()}`;

		const siteId = await apiHelpers.headlessAdminUser
			.getSiteByFriendlyUrlPath('cms')
			.then((response) => response.id);

		const basicWebContent = 'cms/basic-web-contents';
		const webContentNames = [getRandomString(), getRandomString()];

		for (const fileName of webContentNames) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: fileName,
				},
				basicWebContent,
				'Default'
			);
		}

		const folderName = `Folder ${getRandomInt()}`;

		const folder = await apiHelpers.objectFolder.createObjectEntryFolder({
			parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			scopeKey: 'Default',
			title: folderName,
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageFileBase64,
					name: fileNameImg,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: titleImg,
			},
			'cms/basic-documents',
			'Default'
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: getRandomString(),
				},
				objectEntryFolderExternalReferenceCode:
					folder.externalReferenceCode,
				title: `Content ${getRandomInt()}`,
			},
			basicWebContent,
			'Default'
		);

		const tagNames = [
			'Tag' + getRandomInt(),
			'Tag' + getRandomInt(),
			'Tag' + getRandomInt(),
		];

		for (const tagName of tagNames) {
			const tag = await apiHelpers.headlessAdminTaxonomy.postSiteKeyword({
				name: tagName,
				siteId,
			});

			apiHelpers.data.push({
				id: tag.id,
				type: 'keyword',
			});
		}

		await test.step('Select 3 assets including a folder and bulk edit their tags', async () => {
			await assetsPage.gotoContents();

			await assetsPage
				.getItem(webContentNames[0])
				.locator('input[title="Select Item"]')
				.check();
			await assetsPage
				.getItem(webContentNames[1])
				.locator('input[title="Select Item"]')
				.check();
			await assetsPage
				.getItem(folderName)
				.locator('input[title="Select Item"]')
				.check();

			await assetsPage.execBulkItemAction('Edit Tags');

			await waitForModal({
				page,
			});
		});

		await test.step('Add tags to the selected assets', async () => {
			const categoriesAutocomplete = page.getByPlaceholder('Add tag');

			for (const tagName of tagNames) {
				await categoriesAutocomplete.fill(tagName);

				const option = page.getByRole('option', {
					exact: true,
					name: tagName,
				});

				await option.waitFor();
				await option.click();

				const categoryLabel = page.locator('.label-item', {
					hasText: tagName,
				});

				await expect(categoryLabel).toBeAttached();
			}

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(
				page,
				'Info:Tags update action started for 2 assets.',
				{
					autoClose: true,
					type: 'info',
				}
			);
		});

		await test.step('Check that the "Processing Task" button appears and click on it', async () => {
			await expect(assetsPage.processingTasksButton).toBeVisible();

			await assetsPage.processingTasksButton.click();

			expect.poll(
				async () => {
					await expect(assetsPage.processingTasksButton).toBeHidden();
				},
				{
					timeout: 5000,
				}
			);

			await expect(assetsPage.taskStatusDropdownList).toContainText(
				'Completed'
			);
		});

		await test.step('After the click, the dropdown component is shown and 1 task with details is visible', async () => {
			await expect(
				assetsPage.taskStatusDropdownItemButton('Assets Tagging').nth(0)
			).toBeVisible();

			await expect(assetsPage.taskStatusDropdownList).toContainText(
				'2 Items'
			);
		});

		await test.step('Verify the tags has been correctly applied.', async () => {
			for (const webContentName of webContentNames) {
				await page.reload();

				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: webContentName,
				});

				await expect(
					page.getByRole('heading', {name: webContentName})
				).toBeVisible();

				await infoPanelPage.selectTab('Categorization').click();

				for (const tagName of tagNames) {
					await expect(
						page.getByText(tagName, {exact: true})
					).toBeVisible();
				}
			}
		});

		await test.step('Delete more than one tag', async () => {
			await page.reload();

			await assetsPage
				.getItem(webContentNames[1])
				.locator('input[title="Select Item"]')
				.check();

			await assetsPage.execBulkItemAction('Edit Tags');

			await waitForModal({
				page,
			});

			await page
				.locator('span', {hasText: tagNames[0]})
				.getByRole('button', {name: 'Close'})
				.click();
			await page
				.locator('span', {hasText: tagNames[1]})
				.getByRole('button', {name: 'Close'})
				.click();

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(
				page,
				'Info:Tags update action started for one asset.',
				{
					autoClose: true,
					type: 'info',
				}
			);
		});

		await test.step('Verify the tags has been correctly removed.', async () => {
			await page.reload();

			await assetsPage.execItemAction({
				action: 'Show Details',
				filter: webContentNames[1],
			});

			await expect(
				page.getByRole('heading', {name: webContentNames[1]})
			).toBeVisible();

			await infoPanelPage.selectTab('Categorization').click();

			await expect(
				page.getByText(tagNames[2], {exact: true})
			).toBeVisible();
		});

		await test.step('Select 3 mixed assets and bulk edit their tags', async () => {
			await assetsPage.gotoAll();

			await assetsPage
				.getItem(webContentNames[0])
				.locator('input[title="Select Item"]')
				.check();
			await assetsPage
				.getItem(webContentNames[1])
				.locator('input[title="Select Item"]')
				.check();
			await assetsPage
				.getItem(titleImg)
				.locator('input[title="Select Item"]')
				.check();

			await assetsPage.execBulkItemAction('Edit Tags');

			await waitForModal({
				page,
			});
		});

		await test.step('Add and remove tags to the selected assets', async () => {
			const categoriesAutocomplete = page.getByPlaceholder('Add tag');

			for (const tagName of tagNames) {
				await categoriesAutocomplete.fill(tagName);

				const option = page.getByRole('option', {
					exact: true,
					name: tagName,
				});

				await option.waitFor();
				await option.click();

				const categoryLabel = page.locator('.label-item', {
					hasText: tagName,
				});

				await expect(categoryLabel).toBeAttached();
			}

			await page
				.locator('span', {hasText: tagNames[1]})
				.getByRole('button', {name: 'Close'})
				.click();
			await page
				.locator('span', {hasText: tagNames[2]})
				.getByRole('button', {name: 'Close'})
				.click();

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(
				page,
				'Info:Tags update action started for 3 assets.',
				{
					autoClose: true,
					type: 'info',
				}
			);
		});

		await test.step('Verify the tags has been correctly edited.', async () => {
			await page.reload();

			for (const webContentName of webContentNames) {
				await assetsPage.execItemAction({
					action: 'Show Details',
					filter: webContentName,
				});

				await expect(
					page.getByRole('heading', {name: webContentName})
				).toBeVisible();

				await infoPanelPage.selectTab('Categorization').click();

				await expect(
					page.getByText(tagNames[0], {exact: true})
				).toBeVisible();

				await assetsPage
					.getItem(webContentName)
					.locator('input[title="Select Item"]')
					.uncheck();
			}

			await assetsPage.execItemAction({
				action: 'Show Details',
				filter: titleImg,
			});

			await expect(
				page.getByRole('heading', {name: titleImg})
			).toBeVisible();

			await infoPanelPage.selectTab('Categorization').click();

			await expect(
				page.getByText(tagNames[0], {exact: true})
			).toBeVisible();
		});
	}
);

test(
	'Bulk action download assets',
	{tag: '@LPD-62554'},
	async ({apiHelpers, assetsPage, page}) => {
		const contentApplicationName = 'cms/basic-web-contents';
		const fileApplicationName = 'cms/basic-documents';
		const spaceName = 'Default';

		const content1 = `title ${getRandomString()}`;
		const fileAssetTitle1 = `title ${getRandomString()}`;
		const fileAssetTitle2 = `title ${getRandomString()}`;
		const fileNameImg = `file_${getRandomString()}.png`;

		const contentObjectEntry1 =
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: content1,
				},
				contentApplicationName,
				spaceName
			);

		const fileObjectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageFileBase64,
					name: fileNameImg,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileAssetTitle1,
			},
			fileApplicationName,
			'Default'
		);

		const fileObjectEntry2 = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: imageFileBase64,
					name: fileNameImg,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileAssetTitle2,
			},
			fileApplicationName,
			'Default'
		);

		await assetsPage.gotoAll();

		await test.step('Download bulk action is hidden when a content asset is selected', async () => {
			await assetsPage.selectItems([content1]);

			await assetsPage.expectBulkItemActionHidden('Download');

			await assetsPage
				.getItem(content1)
				.locator('input[title="Select Item"]')
				.uncheck();
		});

		await test.step('Download a file asset from the bulk action', async () => {
			await assetsPage.selectItems([fileAssetTitle1]);

			const downloadPromise = page.waitForEvent('download');

			await assetsPage.execBulkItemAction('Download');

			await waitForAlert(
				page,
				'Warning:The download of 1 file is being prepared. Please do not close this window or navigate to another section.',
				{
					type: 'warning',
				}
			);

			await waitForAlert(page, 'Success:The download will begin shortly');

			const download = await downloadPromise;

			expect(download.suggestedFilename()).toBeDefined();
		});

		await test.step('Download bulk action is hidden when a content asset and a file are selected together', async () => {
			await assetsPage.selectItems([content1, fileAssetTitle1]);

			await assetsPage.expectBulkItemActionHidden('Download');

			await assetsPage
				.getItem(content1)
				.locator('input[title="Select Item"]')
				.uncheck();
		});

		await test.step('Download multiple file assets from the bulk action', async () => {
			await assetsPage.selectItems([fileAssetTitle1, fileAssetTitle2]);

			const downloadPromise = page.waitForEvent('download');

			await assetsPage.execBulkItemAction('Download');

			await waitForAlert(
				page,
				'Warning:The download of 2 files is being prepared. Please do not close this window or navigate to another section.',
				{
					type: 'warning',
				}
			);

			await waitForAlert(page, 'Success:The download will begin shortly');

			const download = await downloadPromise;

			expect(download.suggestedFilename()).toBeDefined();
		});

		await test.step('Remove generated files', async () => {
			await apiHelpers.objectEntry.deleteObjectEntry(
				contentApplicationName,
				String(contentObjectEntry1.id)
			);
			await apiHelpers.objectEntry.deleteObjectEntry(
				fileApplicationName,
				String(fileObjectEntry1.id)
			);
			await apiHelpers.objectEntry.deleteObjectEntry(
				fileApplicationName,
				String(fileObjectEntry2.id)
			);
		});
	}
);

test(
	'Delete Asset Versions in bulk',
	{tag: '@LPD-67234'},
	async ({apiHelpers, assetsPage, page}) => {
		const contentApplicationName = 'cms/basic-web-contents';
		const spaceName = 'Default';

		const webContentNames = [
			getRandomString(),
			getRandomString(),
			getRandomString(),
		];

		let contentObjectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: webContentNames[0],
			},
			contentApplicationName,
			spaceName
		);

		await test.step('Edit object entry to generate more versions', async () => {
			await apiHelpers.objectEntry.patchObjectEntry(
				{
					title_i18n: {
						en_US: webContentNames[1],
					},
				},
				contentApplicationName,
				contentObjectEntry.id
			);

			contentObjectEntry = await apiHelpers.objectEntry.patchObjectEntry(
				{
					title_i18n: {
						en_US: webContentNames[2],
					},
				},
				contentApplicationName,
				contentObjectEntry.id
			);
		});

		await assetsPage.gotoAll();

		await assetsPage.execItemAction({
			action: 'View History',
			filter: webContentNames[2],
		});

		await test.step('Bulk Delete is hidden when the current version is included in the selection', async () => {
			for (const webContentName of webContentNames) {
				await assetsPage
					.getItem(webContentName)
					.locator('input[title="Select Item"]')
					.check();
			}

			await assetsPage.expectBulkItemActionHidden('Delete');

			await assetsPage
				.getItem(webContentNames[2])
				.locator('input[title="Select Item"]')
				.uncheck();
		});

		await test.step('Bulk delete the non-current versions', async () => {
			await assetsPage.execBulkItemAction('Delete');

			await waitForModal({
				page,
			});

			await page
				.locator('.modal')
				.getByRole('button', {name: 'Delete'})
				.click();

			await waitForAlert(
				page,
				'Info:Delete asset versions action started for 2 versions.',
				{
					autoClose: true,
					type: 'info',
				}
			);
		});

		await test.step('Deleted versions are removed and the current version remains', async () => {
			await page.reload();

			await expect(
				assetsPage.getItem(webContentNames[0])
			).not.toBeVisible();

			await expect(
				assetsPage.getItem(webContentNames[1])
			).not.toBeVisible();

			await expect(assetsPage.getItem(webContentNames[2])).toBeVisible();
		});

		await test.step('Bulk Delete is hidden when only the current version is selected', async () => {
			await assetsPage
				.getItem(webContentNames[2])
				.locator('input[title="Select Item"]')
				.check();

			await assetsPage.expectBulkItemActionHidden('Delete');
		});
	}
);

test(
	'Expire CMS assets in bulk',
	{tag: '@LPD-85361'},
	async ({apiHelpers, assetsPage, page}) => {
		const basicDocumentTitle = `Basic Document ${getRandomString()}`;
		const basicWebContentTitle = `Basic Web Content ${getRandomString()}`;
		const blogTitle = `Blog ${getRandomString()}`;
		const spaceName = 'Default';

		await test.step('Create CMS assets', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: imageFileBase64,
						name: `file_${getRandomString()}.png`,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: basicDocumentTitle,
				},
				'cms/basic-documents',
				spaceName
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: basicWebContentTitle,
				},
				'cms/basic-web-contents',
				spaceName
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: blogTitle,
				},
				'cms/blogs',
				spaceName
			);
		});

		await test.step('Expire one asset in bulk', async () => {
			await assetsPage.gotoAll();

			const row = page.getByRole('row', {name: basicDocumentTitle});

			await expect(
				row.getByRole('cell', {name: 'Approved'})
			).toBeVisible();

			await assetsPage.selectItems([basicDocumentTitle]);

			await assetsPage.execBulkItemAction('Expire');

			await waitForModal({page});

			await page
				.locator('.modal')
				.getByRole('button', {name: 'Expire'})
				.click();

			await waitForAlert(
				page,
				`Info:Expire action started for ${basicDocumentTitle} asset.`,
				{
					type: 'info',
				}
			);

			await waitForAlert(
				page,
				`Success:${basicDocumentTitle} was successfully expired.`,
				{first: true}
			);

			await expect(
				row.getByRole('cell', {name: 'Expired'})
			).toBeVisible();
		});

		await test.step('Expire two assets in bulk', async () => {
			await assetsPage.gotoAll();

			const rows = [
				page.getByRole('row', {name: basicWebContentTitle}),
				page.getByRole('row', {name: blogTitle}),
			];

			for (const row of rows) {
				await expect(
					row.getByRole('cell', {name: 'Approved'})
				).toBeVisible();
			}

			await assetsPage.selectItems([basicWebContentTitle, blogTitle]);

			await assetsPage.execBulkItemAction('Expire');

			await waitForModal({page});

			await page
				.locator('.modal')
				.getByRole('button', {name: 'Expire'})
				.click();

			await waitForAlert(
				page,
				'Info:Expire action started for 2 assets.',
				{
					type: 'info',
				}
			);

			await waitForAlert(
				page,
				'Success:2 assets were successfully expired.',
				{first: true}
			);

			for (const row of rows) {
				await expect(
					row.getByRole('cell', {name: 'Expired'})
				).toBeVisible();
			}
		});
	}
);

test(
	'Bulk Expire over a Select All expanded selection forwards the section filter',
	{tag: '@LPD-88977'},
	async ({apiHelpers, assetsPage, page}) => {
		const fileCount = 21;
		const titlePrefix = `BulkSelectAll ${getRandomString()}`;

		await test.step(`Create ${fileCount} files in the Default space`, async () => {
			for (let i = 0; i < fileCount; i++) {
				const entry = await apiHelpers.objectEntry.postObjectEntry(
					{
						file: {
							fileBase64: imageFileBase64,
							name: `${titlePrefix}_${i}.gif`,
						},
						objectEntryFolderExternalReferenceCode: 'L_FILES',
						title: `${titlePrefix}_${i}`,
					},
					'cms/basic-documents',
					'Default'
				);

				apiHelpers.data.push({
					id: entry.file.id,
					type: 'document',
				});
			}
		});

		await assetsPage.gotoFiles();
		await assetsPage.changeVisualizationMode('Table');

		await page.getByTitle('Select Items').click();

		const selectAllLink = page.getByRole('button', {
			exact: true,
			name: 'Select All',
		});

		await expect(selectAllLink).toBeVisible();

		await selectAllLink.click();

		await expect(page.getByText('All Selected')).toBeVisible();

		await assetsPage.execBulkItemAction('Expire');

		await waitForModal({page});

		await page
			.locator('.modal')
			.getByRole('button', {name: 'Expire'})
			.click();

		await waitForAlert(page, 'Info:Expire action started', {
			type: 'info',
		});

		await expect(page.getByText('Filter is null')).toHaveCount(0);
	}
);

test(
	'Bulk Delete over a Select All expanded selection forwards the section filter',
	{tag: '@LPD-89162'},
	async ({apiHelpers, assetsPage, page}) => {
		const fileCount = 21;
		const titlePrefix = `BulkDeleteSelectAll ${getRandomString()}`;

		await test.step(`Create ${fileCount} files in the Default space`, async () => {
			for (let i = 0; i < fileCount; i++) {
				const entry = await apiHelpers.objectEntry.postObjectEntry(
					{
						file: {
							fileBase64: imageFileBase64,
							name: `${titlePrefix}_${i}.gif`,
						},
						objectEntryFolderExternalReferenceCode: 'L_FILES',
						title: `${titlePrefix}_${i}`,
					},
					'cms/basic-documents',
					'Default'
				);

				apiHelpers.data.push({
					id: entry.file.id,
					type: 'document',
				});
			}
		});

		await assetsPage.gotoFiles();
		await assetsPage.changeVisualizationMode('Table');

		await page.getByTitle('Select Items').click();

		const selectAllLink = page.getByRole('button', {
			exact: true,
			name: 'Select All',
		});

		await expect(selectAllLink).toBeVisible();

		await selectAllLink.click();

		await expect(page.getByText('All Selected')).toBeVisible();

		await assetsPage.execBulkItemAction('Delete');

		await waitForModal({page});

		await page
			.locator('.modal')
			.getByRole('button', {name: 'Delete'})
			.click();

		await waitForAlert(page, 'Info:Delete action started', {
			type: 'info',
		});

		await expect(page.getByText('Filter is null')).toHaveCount(0);
	}
);

test(
	'Export for Translation CMS assets in bulk',
	{tag: '@LPD-85361'},
	async ({apiHelpers, assetsPage, page}) => {
		const basicDocumentTitle = `Basic Document ${getRandomString()}`;
		const basicWebContentTitle1 = `Basic Web Content ${getRandomString()}`;
		const basicWebContentTitle2 = `Basic Web Content ${getRandomString()}`;
		const blogTitle = `Blog ${getRandomString()}`;
		const spaceName = 'Default';

		await test.step('Create CMS assets', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: imageFileBase64,
						name: `file_${getRandomString()}.png`,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: basicDocumentTitle,
				},
				'cms/basic-documents',
				spaceName
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: basicWebContentTitle1,
				},
				'cms/basic-web-contents',
				spaceName
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: basicWebContentTitle2,
				},
				'cms/basic-web-contents',
				spaceName
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: blogTitle,
				},
				'cms/blogs',
				spaceName
			);
		});

		await test.step('Exporting for Translation a single file type object entry is not allowed', async () => {
			await assetsPage.gotoAll();

			await assetsPage.selectItems([basicDocumentTitle]);

			await assetsPage.execBulkItemAction('Export for Translation');

			await waitForModal({
				page,
			});

			await expect(
				page.getByText(
					'This action is not available for the item you have selected.'
				)
			).toBeVisible();

			await page
				.locator('.modal')
				.getByRole('button', {name: 'OK'})
				.click();
		});

		await test.step('Exporting for Translation file and content type object entries together is not allowed', async () => {
			await assetsPage.gotoAll();

			await assetsPage.selectItems([
				basicDocumentTitle,
				basicWebContentTitle1,
			]);

			await assetsPage.execBulkItemAction('Export for Translation');

			await waitForModal({
				page,
			});

			await expect(
				page.getByText(
					'This action is not available for the item you have selected.'
				)
			).toBeVisible();

			await page
				.locator('.modal')
				.getByRole('button', {name: 'OK'})
				.click();
		});

		await test.step('Exporting for Translation a single content type object entry to a single target language', async () => {
			await assetsPage.gotoAll();

			await assetsPage.selectItems([basicWebContentTitle1]);

			await assetsPage.execBulkItemAction('Export for Translation');

			await waitForModal({
				page,
			});

			await expect(
				page
					.locator('.modal-header')
					.getByText('Export for Translation')
			).toBeVisible();

			const filePath = await assetsPage.exportForTranslation(true, [
				'Spanish (Spain)',
			]);

			await expect(
				checkInZip(filePath, `${basicWebContentTitle1}-en_US-es_ES.xlf`)
			).resolves.toBe(true);
		});

		await test.step('Exporting for Translation a single content type object entry to a multiple target languages', async () => {
			await assetsPage.gotoAll();

			await assetsPage.selectItems([basicWebContentTitle1]);

			await assetsPage.execBulkItemAction('Export for Translation');

			await waitForModal({
				page,
			});

			await expect(
				page
					.locator('.modal-header')
					.getByText('Export for Translation')
			).toBeVisible();

			const filePath = await assetsPage.exportForTranslation(true, [
				'Chinese (China)',
				'Spanish (Spain)',
			]);

			await expect(
				checkInZip(filePath, `${basicWebContentTitle1}-en_US-es_ES.xlf`)
			).resolves.toBe(true);

			await expect(
				checkInZip(filePath, `${basicWebContentTitle1}-en_US-zh_CN.xlf`)
			).resolves.toBe(true);
		});

		await test.step('Exporting for Translation two equal content type object entries', async () => {
			await assetsPage.gotoAll();

			await assetsPage.selectItems([
				basicWebContentTitle1,
				basicWebContentTitle2,
			]);

			await assetsPage.execBulkItemAction('Export for Translation');

			await waitForModal({
				page,
			});

			await expect(
				page
					.locator('.modal-header')
					.getByText('Export for Translation')
			).toBeVisible();

			const filePath = await assetsPage.exportForTranslation(true, [
				'Spanish (Spain)',
			]);

			await expect(
				checkInZip(filePath, `${basicWebContentTitle1}-en_US-es_ES.xlf`)
			).resolves.toBe(true);

			await expect(
				checkInZip(filePath, `${basicWebContentTitle2}-en_US-es_ES.xlf`)
			).resolves.toBe(true);
		});

		await test.step('Exporting for Translation two different content type object entries', async () => {
			await assetsPage.gotoAll();

			await assetsPage.selectItems([basicWebContentTitle1, blogTitle]);

			await assetsPage.execBulkItemAction('Export for Translation');

			await waitForModal({
				page,
			});

			await expect(
				page
					.locator('.modal-header')
					.getByText('Export for Translation')
			).toBeVisible();

			const filePath = await assetsPage.exportForTranslation(true, [
				'Spanish (Spain)',
			]);

			await expect(
				checkInZip(filePath, `${basicWebContentTitle1}-en_US.zip`)
			).resolves.toBe(true);

			await expect(
				checkInZip(filePath, `${blogTitle}-en_US.zip`)
			).resolves.toBe(true);
		});

		await test.step('Exporting for Translation multiple object entries of two different content types', async () => {
			await assetsPage.gotoAll();

			await assetsPage.selectItems([
				basicWebContentTitle1,
				basicWebContentTitle2,
				blogTitle,
			]);

			await assetsPage.execBulkItemAction('Export for Translation');

			await waitForModal({
				page,
			});

			await expect(
				page
					.locator('.modal-header')
					.getByText('Export for Translation')
			).toBeVisible();

			const filePath = await assetsPage.exportForTranslation(true, [
				'Spanish (Spain)',
			]);

			await expect(
				checkInZip(filePath, `Basic Web Content Translations-en_US.zip`)
			).resolves.toBe(true);

			await expect(
				checkInZip(filePath, `${blogTitle}-en_US.zip`)
			).resolves.toBe(true);
		});
	}
);

test(
	'Can move multiple contents to a folder in a Space',
	{tag: '@LPD-86776'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = `Space ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const contentTitles = [
			`Content ${getRandomString()}`,
			`Content ${getRandomString()}`,
			`Content ${getRandomString()}`,
		];

		await test.step('Create a new Space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});
		});

		let destinationFolderId: number;

		await test.step('Create a destination folder in the Space', async () => {
			const folder =
				await apiHelpers.objectFolder.createObjectEntryFolder({
					parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					scopeKey: spaceName,
					title: destinationFolderName,
				});

			destinationFolderId = folder.id;
		});

		await test.step('Create three contents in the Space', async () => {
			for (const title of contentTitles) {
				await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						title,
					},
					applicationName,
					spaceName
				);
			}
		});

		await test.step('Select the three contents and move them to the destination folder', async () => {
			await assetsPage.gotoAll();

			await assetsPage.selectItems(contentTitles);

			await assetsPage.bulkMoveTo({
				destinationFolder: destinationFolderName,
				destinationSpace: spaceName,
			});
		});

		await test.step('Info alert for the bulk move is displayed', async () => {
			await waitForAlert(
				page,
				`Info:Moving 3 assets to ${destinationFolderName}.`,
				{type: 'info'}
			);
		});

		await test.step('Success alert for the bulk move is displayed', async () => {
			await waitForAlert(
				page,
				`Success:3 assets were successfully moved to ${destinationFolderName}.`,
				{first: true}
			);
		});

		await test.step('The three contents are in the destination folder', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(spaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const movedItems = response.items.filter(
				(item: {objectEntryFolderId: number}) =>
					item.objectEntryFolderId === destinationFolderId
			);

			expect(movedItems).toHaveLength(3);
		});
	}
);

test(
	'Can copy multiple files across Spaces',
	{tag: '@LPD-86776'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-documents';
		const sourceSpaceName = `Space ${getRandomString()}`;
		const destinationSpaceName = `Space ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const fileTitles = [
			`File ${getRandomString()}`,
			`File ${getRandomString()}`,
			`File ${getRandomString()}`,
		];

		await test.step('Create source and destination Spaces', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: sourceSpaceName,
				settings: {},
				type: 'Space',
			});

			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: destinationSpaceName,
				settings: {},
				type: 'Space',
			});
		});

		let destinationFolderId: number;

		await test.step('Create a destination folder in the destination Space', async () => {
			const folder =
				await apiHelpers.objectFolder.createObjectEntryFolder({
					parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
					scopeKey: destinationSpaceName,
					title: destinationFolderName,
				});

			destinationFolderId = folder.id;
		});

		await test.step('Create three files in the source Space', async () => {
			for (const title of fileTitles) {
				await apiHelpers.objectEntry.postObjectEntry(
					{
						file: {
							fileBase64: imageFileBase64,
							name: `file_${getRandomString()}.png`,
						},
						objectEntryFolderExternalReferenceCode: 'L_FILES',
						title,
					},
					applicationName,
					sourceSpaceName
				);
			}
		});

		await test.step('Select the three files and copy them to the destination folder', async () => {
			await assetsPage.gotoAll();

			await assetsPage.selectItems(fileTitles);

			await assetsPage.bulkCopyTo({
				destinationFolder: destinationFolderName,
				destinationSpace: destinationSpaceName,
			});
		});

		await test.step('Info alert for the bulk copy is displayed', async () => {
			await waitForAlert(
				page,
				`Info:Copying 3 assets to ${destinationFolderName}.`,
				{type: 'info'}
			);
		});

		await test.step('Success alert for the bulk copy is displayed', async () => {
			await waitForAlert(
				page,
				`Success:3 assets were successfully copied to ${destinationFolderName}.`,
				{first: true}
			);
		});

		await test.step('The three files are in the destination folder in the destination Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(destinationSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const copiedItems = response.items.filter(
				(item: {objectEntryFolderId: number}) =>
					item.objectEntryFolderId === destinationFolderId
			);

			expect(copiedItems).toHaveLength(3);
		});

		await test.step('The three files are still present in the source Space', async () => {
			const response =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					encodeURIComponent(sourceSpaceName),
					new URLSearchParams({pageSize: '100'})
				);

			const sourceItems = response.items.filter(
				(item: {objectEntryFolderExternalReferenceCode: string}) =>
					item.objectEntryFolderExternalReferenceCode === 'L_FILES'
			);

			expect(sourceItems).toHaveLength(3);
		});
	}
);

test(
	'Warn users about limitations when bulk moving or copying',
	{tag: '@LPD-86776'},
	async ({apiHelpers, assetsPage, page}) => {
		const spaceName = `Space ${getRandomString()}`;
		const contentTitles = [
			`Content ${getRandomString()}`,
			`Content ${getRandomString()}`,
		];
		const fileTitle = `File ${getRandomString()}`;

		await test.step('Create a Space with two contents and one file', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});

			for (const title of contentTitles) {
				await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						title,
					},
					'cms/basic-web-contents',
					spaceName
				);
			}

			await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: imageFileBase64,
						name: `file_${getRandomString()}.png`,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title: fileTitle,
				},
				'cms/basic-documents',
				spaceName
			);
		});

		await test.step('Select the two contents', async () => {
			await assetsPage.gotoAll();

			await assetsPage.selectItems(contentTitles);
		});

		const pickerDialog = assetsPage.getCopyOrMoveDestinationDialog();
		const notAllowedDialog = page.getByRole('dialog', {
			name: 'Action not allowed',
		});

		await test.step('Move To destination picker shows the warning for same-type selection', async () => {
			await page
				.getByRole('button', {exact: true, name: 'Move To'})
				.click();

			await expect(
				pickerDialog.getByText(/will be retained/i)
			).toBeVisible();

			await pickerDialog
				.getByRole('button', {exact: true, name: 'Cancel'})
				.click();

			await expect(pickerDialog).toBeHidden();
		});

		await test.step('Copy To destination picker shows the warning for same-type selection', async () => {
			await page
				.getByRole('button', {exact: true, name: 'Copy To'})
				.click();

			await expect(
				pickerDialog.getByText(/will be copied/i)
			).toBeVisible();

			await pickerDialog
				.getByRole('button', {exact: true, name: 'Cancel'})
				.click();

			await expect(pickerDialog).toBeHidden();
		});

		await test.step('Also select the file for a mixed-type selection', async () => {
			await assetsPage.selectItems([fileTitle]);
		});

		await test.step('Move To blocks a mixed-type selection with a not-allowed modal', async () => {
			await page
				.getByRole('button', {exact: true, name: 'Move To'})
				.click();

			await expect(
				notAllowedDialog.getByText(/cannot be moved together/i)
			).toBeVisible();

			await notAllowedDialog
				.getByRole('button', {exact: true, name: 'OK'})
				.click();

			await expect(notAllowedDialog).toBeHidden();
		});

		await test.step('Copy To blocks a mixed-type selection with a not-allowed modal', async () => {
			await page
				.getByRole('button', {exact: true, name: 'Copy To'})
				.click();

			await expect(
				notAllowedDialog.getByText(/cannot be copied together/i)
			).toBeVisible();

			await notAllowedDialog
				.getByRole('button', {exact: true, name: 'OK'})
				.click();

			await expect(notAllowedDialog).toBeHidden();
		});
	}
);

test(
	'Selected folders are hidden from the destination picker on bulk move',
	{tag: '@LPD-86776'},
	async ({apiHelpers, assetsPage, page}) => {
		const spaceName = `Space ${getRandomString()}`;
		const folderAName = `Folder A ${getRandomString()}`;
		const folderBName = `Folder B ${getRandomString()}`;

		await test.step('Create a Space with two content folders', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});

			for (const title of [folderAName, folderBName]) {
				await apiHelpers.objectFolder.createObjectEntryFolder({
					parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					scopeKey: spaceName,
					title,
				});
			}
		});

		await test.step("Navigate to the Space's Contents", async () => {
			await assetsPage.gotoSpaceContents(spaceName);
		});

		await test.step('Select both folders and open Move To', async () => {
			await assetsPage.selectItems([folderAName, folderBName]);

			await page
				.getByRole('button', {exact: true, name: 'Move To'})
				.click();
		});

		await test.step('Selected folders are hidden in the destination picker', async () => {
			const dialog = assetsPage.getCopyOrMoveDestinationDialog();

			await dialog.getByLabel(spaceName).click();

			await expect(
				dialog.getByRole('radio', {
					exact: true,
					name: `Select ${folderAName}`,
				})
			).toBeHidden();

			await expect(
				dialog.getByRole('radio', {
					exact: true,
					name: `Select ${folderBName}`,
				})
			).toBeHidden();
		});
	}
);

test(
	'Bulk move shows an error when the destination Space lacks the content structure',
	{tag: '@LPD-86776'},
	async ({apiHelpers, assetsPage, page}) => {
		const sourceSpaceName = `Space ${getRandomString()}`;
		const destinationSpaceName = `Space ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const contentTitles = [
			`Content ${getRandomString()}`,
			`Content ${getRandomString()}`,
		];

		let sourceSpaceERC: string;

		await test.step('Create source and destination Spaces', async () => {
			const sourceSpace =
				await apiHelpers.headlessAssetLibrary.createAssetLibrary({
					name: sourceSpaceName,
					settings: {},
					type: 'Space',
				});

			sourceSpaceERC = sourceSpace.externalReferenceCode;

			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: destinationSpaceName,
				settings: {},
				type: 'Space',
			});
		});

		await test.step('Create a destination folder in the destination Space', async () => {
			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: destinationSpaceName,
				title: destinationFolderName,
			});
		});

		let applicationName: string;

		await test.step('Create a content structure available only in the source Space', async () => {
			const definition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectDefinitionSettings: [
						{
							name: 'acceptedGroupExternalReferenceCodes',
							value: sourceSpaceERC as unknown as object,
						},
					],
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: getRandomString(),
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: 'en_US',
							label: {en_US: 'Title'},
							localized: true,
							name: 'title',
							required: true,
						},
					],
					objectFolderExternalReferenceCode:
						'L_CMS_CONTENT_STRUCTURES',
					scope: 'depot',
					status: {code: 0},
					titleObjectFieldName: 'title',
				});

			expect(
				definition.id,
				'object definition was created'
			).toBeDefined();
			expect(
				definition.restContextPath,
				'object definition has a REST context path'
			).toBeDefined();

			apiHelpers.data.push({
				id: definition.id as number,
				type: 'objectDefinition',
			});

			applicationName = (definition.restContextPath as string).replace(
				/^\/o\//,
				''
			);
		});

		await test.step('Seed two contents in the source Space', async () => {
			for (const title of contentTitles) {
				await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						title,
					},
					applicationName,
					sourceSpaceName
				);
			}
		});

		await test.step('Try to bulk move the contents to the destination Space', async () => {
			await assetsPage.gotoAll();

			await assetsPage.selectItems(contentTitles);

			await assetsPage.bulkMoveTo({
				destinationFolder: destinationFolderName,
				destinationSpace: destinationSpaceName,
			});
		});

		await test.step('Error toast informs the asset cannot be moved', async () => {
			await waitForAlert(
				page,
				'Error:The asset cannot be moved because its content type is not available in the destination space.',
				{type: 'danger'}
			);
		});
	}
);

test(
	'Destination picker shows folder hierarchy via drill-down',
	{tag: '@LPD-86776'},
	async ({apiHelpers, assetsPage, page}) => {
		const sourceSpaceName = `Source ${getRandomString()}`;
		const destinationSpaceName = `Destination ${getRandomString()}`;
		const folder1Name = `Folder1 ${getRandomString()}`;
		const folder2Name = `Folder2 ${getRandomString()}`;
		const contentTitle = `Content ${getRandomString()}`;

		await test.step('Create source and destination Spaces', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: sourceSpaceName,
				settings: {},
				type: 'Space',
			});

			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: destinationSpaceName,
				settings: {},
				type: 'Space',
			});
		});

		await test.step('Create a nested folder hierarchy in the destination Space', async () => {
			const folder1 =
				await apiHelpers.objectFolder.createObjectEntryFolder({
					parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					scopeKey: destinationSpaceName,
					title: folder1Name,
				});

			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode:
					folder1.externalReferenceCode,
				scopeKey: destinationSpaceName,
				title: folder2Name,
			});
		});

		await test.step('Create a content in the source Space', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentTitle,
				},
				'cms/basic-web-contents',
				sourceSpaceName
			);
		});

		await test.step('Open the Move To picker', async () => {
			await assetsPage.gotoAll();

			await assetsPage.selectItems([contentTitle]);

			await page
				.getByRole('button', {exact: true, name: 'Move To'})
				.click();
		});

		const dialog = assetsPage.getCopyOrMoveDestinationDialog();

		await test.step('Inside the destination Space the picker only shows the top-level folder', async () => {
			await dialog.getByLabel(destinationSpaceName).click();

			await expect(
				dialog.getByRole('radio', {
					exact: true,
					name: `Select ${folder1Name}`,
				})
			).toBeVisible();

			await expect(
				dialog.getByRole('radio', {
					exact: true,
					name: `Select ${folder2Name}`,
				})
			).toBeHidden();
		});

		await test.step('Drilling into the top-level folder reveals the nested folder and updates breadcrumbs', async () => {
			await dialog
				.getByRole('link', {exact: true, name: folder1Name})
				.click();

			await expect(
				dialog
					.getByRole('navigation', {name: 'Breadcrumb'})
					.getByText(folder1Name)
			).toBeVisible();

			await expect(
				dialog.getByRole('radio', {
					exact: true,
					name: `Select ${folder2Name}`,
				})
			).toBeVisible();
		});
	}
);

test(
	'Bulk move shows an error when the destination already has a same-named content',
	{tag: '@LPD-86776'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const sourceSpaceName = `Source ${getRandomString()}`;
		const destinationSpaceName = `Destination ${getRandomString()}`;
		const destinationFolderName = `Destination ${getRandomString()}`;
		const sharedTitle = `Content ${getRandomString()}`;
		const otherTitle = `Content ${getRandomString()}`;

		await test.step('Create source and destination Spaces', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: sourceSpaceName,
				settings: {},
				type: 'Space',
			});

			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: destinationSpaceName,
				settings: {},
				type: 'Space',
			});
		});

		let destinationFolderERC: string;

		await test.step('Create a destination folder in the destination Space', async () => {
			const folder =
				await apiHelpers.objectFolder.createObjectEntryFolder({
					parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					scopeKey: destinationSpaceName,
					title: destinationFolderName,
				});

			destinationFolderERC = folder.externalReferenceCode;
		});

		await test.step('Seed contents in both Spaces with one shared title', async () => {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: sharedTitle,
				},
				applicationName,
				sourceSpaceName
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: otherTitle,
				},
				applicationName,
				sourceSpaceName
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode:
						destinationFolderERC,
					title: sharedTitle,
				},
				applicationName,
				destinationSpaceName
			);
		});

		await test.step('Try to bulk move the source contents to the destination folder', async () => {
			await assetsPage.gotoSpaceContents(sourceSpaceName);

			await assetsPage.selectItems([sharedTitle, otherTitle]);

			await assetsPage.bulkMoveTo({
				destinationFolder: destinationFolderName,
				destinationSpace: destinationSpaceName,
			});
		});

		await test.step('Error toast informs the assets could not be moved', async () => {
			await waitForAlert(
				page,
				'Error:Assets could not be moved. Please ensure the name is unique in the destination.',
				{type: 'danger'}
			);
		});
	}
);

test(
	'Bulk Duplicate creates draft copies in the same Space',
	{tag: '@LPD-88656'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const contentTitles = [
			`Content ${getRandomString()}`,
			`Content ${getRandomString()}`,
		];
		const spaceName = `Space ${getRandomString()}`;

		await test.step('Create a Space with two contents', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});

			for (const title of contentTitles) {
				await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						title,
					},
					applicationName,
					spaceName
				);
			}
		});

		await test.step('Bulk duplicate both contents', async () => {
			await assetsPage.gotoSpaceContents(spaceName);

			await assetsPage.selectItems(contentTitles);

			await assetsPage.execBulkItemAction('Duplicate');

			await waitForModal({page});

			await page
				.locator('.modal')
				.getByRole('button', {name: 'Duplicate'})
				.click();
		});

		await test.step('Info alert for the bulk duplicate is displayed', async () => {
			await waitForAlert(
				page,
				'Info:Duplicate action started for 2 assets.',
				{type: 'info'}
			);
		});

		await test.step('Success alert for the bulk duplicate is displayed', async () => {
			await waitForAlert(
				page,
				'Success:2 assets were successfully duplicated.',
				{first: true}
			);
		});

		await test.step('Both copies appear with (Copy) suffix in Draft state', async () => {
			for (const originalTitle of contentTitles) {
				await expect(
					page.getByRole('link', {
						exact: true,
						name: `${originalTitle} (Copy)`,
					})
				).toBeVisible();
			}

			await expect(
				assetsPage.table.bodyRows
					.filter({
						has: page.getByRole('link', {
							exact: true,
							name: `${contentTitles[0]} (Copy)`,
						}),
					})
					.getByText('Draft')
			).toBeVisible();
		});
	}
);

test(
	'Bulk Duplicate handles a mixed entry and folder selection in the same Space',
	{tag: '@LPD-88656'},
	async ({apiHelpers, assetsPage, page}) => {
		const contentTitle = `Content ${getRandomString()}`;
		const folderTitle = `Folder ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;

		await test.step('Create a Space with one content and one folder', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentTitle,
				},
				'cms/basic-web-contents',
				spaceName
			);

			await apiHelpers.objectFolder.createObjectEntryFolder({
				parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				scopeKey: spaceName,
				title: folderTitle,
			});
		});

		await test.step('Bulk duplicate the content and folder', async () => {
			await assetsPage.gotoSpaceContents(spaceName);

			await assetsPage.selectItems([contentTitle, folderTitle]);

			await assetsPage.execBulkItemAction('Duplicate');

			await waitForModal({page});

			await page
				.locator('.modal')
				.getByRole('button', {name: 'Duplicate'})
				.click();
		});

		await test.step('Info alert for the bulk duplicate is displayed', async () => {
			await waitForAlert(
				page,
				'Info:Duplicate action started for 2 assets.',
				{type: 'info'}
			);
		});

		await test.step('Success alert for the bulk duplicate is displayed', async () => {
			await waitForAlert(
				page,
				'Success:2 assets were successfully duplicated.',
				{first: true}
			);
		});

		await test.step('Both copies appear with (Copy) suffix', async () => {
			for (const title of [contentTitle, folderTitle]) {
				await expect(
					page.getByRole('link', {
						exact: true,
						name: `${title} (Copy)`,
					})
				).toBeVisible();
			}
		});
	}
);

test(
	'Permissions can be reset to defaults in bulk from the All section',
	{tag: '@LPD-85553'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';

		const contentNames = [getRandomString(), getRandomString()];

		const spaceName = `Space ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		for (const contentName of contentNames) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentName,
				},
				applicationName,
				spaceName
			);
		}

		const permissionsPage = new PermissionsPage(page);

		const overriddenPermissions = [
			{action: 'DELETE_DISCUSSION', checked: true, role: 'Power User'},
			{action: 'UPDATE_DISCUSSION', checked: true, role: 'User'},
		];

		await test.step('Override permissions on each asset individually', async () => {
			await assetsPage.gotoAll();

			for (const contentName of contentNames) {
				await page
					.getByRole('button', {name: `${contentName} Actions`})
					.click();

				await page
					.getByRole('menuitem', {exact: true, name: 'Permissions'})
					.click();

				await page
					.getByRole('menuitem', {exact: true, name: 'Permissions'})
					.last()
					.click();

				await permissionsPage.checkPermissionsAndSave(
					overriddenPermissions
				);
			}
		});

		await test.step('Bulk reset permissions to parent defaults', async () => {
			await assetsPage.selectItems(contentNames);

			await page
				.getByTestId('visualization-mode-table')
				.getByLabel('Actions')
				.click();

			await page
				.getByRole('menuitem', {
					exact: true,
					name: 'Reset to Default Permissions',
				})
				.click();

			await page.getByRole('button', {name: 'Confirm'}).click();

			await waitForAlert(
				page,
				`Info:Reset permissions action started for ${contentNames.length} assets.`,
				{type: 'info'}
			);
		});

		await test.step('Verify the overridden permissions were cleared', async () => {
			for (const contentName of contentNames) {
				await page
					.getByRole('button', {name: `${contentName} Actions`})
					.click();

				await page
					.getByRole('menuitem', {exact: true, name: 'Permissions'})
					.click();

				await page
					.getByRole('menuitem', {exact: true, name: 'Permissions'})
					.last()
					.click();

				await permissionsPage.verifyPermissions([
					{
						action: 'DELETE_DISCUSSION',
						checked: false,
						role: 'Power User',
					},
					{
						action: 'UPDATE_DISCUSSION',
						checked: false,
						role: 'User',
					},
				]);
			}
		});
	}
);

test(
	'Permissions can be edited by role in bulk from the All section',
	{tag: '@LPD-85553'},
	async ({apiHelpers, assetsPage, page}) => {
		const applicationName = 'cms/basic-web-contents';

		const contentNames = [getRandomString(), getRandomString()];

		const spaceName = `Space ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {},
			type: 'Space',
		});

		for (const contentName of contentNames) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentName,
				},
				applicationName,
				spaceName
			);
		}

		const defaultPermissionsPage = new DefaultPermissionsPage(page);
		const permissionsPage = new PermissionsPage(page);

		await test.step('Bulk edit Permissions by Role for the selected assets', async () => {
			await assetsPage.gotoAll();

			await assetsPage.selectItems(contentNames);

			await page
				.getByTestId('visualization-mode-table')
				.getByLabel('Actions')
				.click();

			await page
				.getByRole('menuitem', {
					exact: true,
					name: 'Edit Permissions by Role',
				})
				.click();

			await expect(defaultPermissionsPage.permissionsModal).toBeVisible();

			await defaultPermissionsPage.permissionsModalSelectRole.selectOption(
				'Power User'
			);

			await defaultPermissionsPage.permissionsModal
				.getByTestId('row-checkbox-Power User_VIEW')
				.check();

			await defaultPermissionsPage.permissionsModalSaveButton.click();

			await waitForAlert(
				page,
				`update action started for ${contentNames.length} assets`,
				{type: 'info'}
			);

			await defaultPermissionsPage.permissionsModalCancelButton.click();
		});

		await test.step('Verify the selected assets received the new permissions', async () => {
			const expected = [
				{action: 'VIEW', checked: true, role: 'Power User'},
			];

			for (const contentName of contentNames) {
				await page
					.getByRole('button', {name: `${contentName} Actions`})
					.click();

				await page
					.getByRole('menuitem', {exact: true, name: 'Permissions'})
					.click();

				await page
					.getByRole('menuitem', {exact: true, name: 'Permissions'})
					.last()
					.click();

				await permissionsPage.verifyPermissions(expected);
			}
		});
	}
);
