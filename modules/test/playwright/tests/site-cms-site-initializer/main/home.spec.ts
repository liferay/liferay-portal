/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {DataSetPage} from './pages/DataSetPage';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
		'LPS-179669': {enabled: true},
	}),
	loginTest(),
	workflowPagesTest
);

test(
	'Can manage my workflow tasks',
	{tag: '@LPD-58790'},
	async ({
		apiHelpers,
		configurationTabPage,
		homePage,
		page,
		processBuilderPage,
	}) => {
		await processBuilderPage.goto('/test');
		await configurationTabPage.configurationTabLink.waitFor({
			state: 'visible',
		});
		await configurationTabPage.configurationTabLink.click({force: true});
		await configurationTabPage.page.waitForURL((url) =>
			url.href.includes('=configuration')
		);

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			'Basic Web Content'
		);

		let objectEntry1;
		let objectEntry2;
		let objectEntry3;

		const applicationName = 'cms/basic-web-contents';

		try {
			const contentName1 = getRandomString();
			const contentName2 = getRandomString();
			const contentName3 = getRandomString();

			objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentName1,
				},
				applicationName,
				'Default'
			);

			objectEntry2 = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentName2,
				},
				applicationName,
				'Default'
			);

			objectEntry3 = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentName3,
				},
				applicationName,
				'Default'
			);

			await homePage.goto();

			await test.step('Verify workflow task assign to me action', async () => {
				await homePage.workflowTaskFilterButton.click();
				await homePage.assignedToMyRolesMenuItem.click();

				await expect(page.getByText(objectEntry1.title)).toBeVisible();
				await homePage.assignToMe(objectEntry1.title);
				await expect(page.getByText(objectEntry1.title)).toBeHidden();

				await homePage.workflowTaskFilterButton.click();
				await homePage.assignedToMeMenuItem.click();

				await expect(page.getByText(objectEntry1.title)).toBeVisible();
			});

			await test.step('Verify workflow task assign to... action', async () => {
				await homePage.workflowTaskFilterButton.click();
				await homePage.assignedToMyRolesMenuItem.click();

				await expect(page.getByText(objectEntry2.title)).toBeVisible();
				await homePage.assignTo(objectEntry2.title);
				await expect(page.getByText(objectEntry2.title)).toBeHidden();

				await homePage.workflowTaskFilterButton.click();
				await homePage.assignedToMeMenuItem.click();

				await expect(page.getByText(objectEntry2.title)).toBeVisible();
			});

			await test.step('Verify workflow task approve action', async () => {
				await expect(page.getByText(objectEntry1.title)).toBeVisible();
				await homePage.approveWorkflowTask(objectEntry1.title);
				await expect(page.getByText(objectEntry1.title)).toBeHidden();
			});

			await test.step('Verify workflow task reject action', async () => {
				await expect(page.getByText(objectEntry2.title)).toBeVisible();
				await homePage.rejectWorkflowTask(objectEntry2.title);
				await expect(page.getByText(objectEntry2.title)).toBeHidden();
			});

			await test.step('Verify workflow task update due date action', async () => {
				await homePage.workflowTaskFilterButton.click();
				await homePage.assignedToMyRolesMenuItem.click();

				await expect(page.getByText(objectEntry3.title)).toBeVisible();
				await homePage.assignToMe(objectEntry3.title);
				await expect(page.getByText(objectEntry3.title)).toBeHidden();

				await homePage.workflowTaskFilterButton.click();
				await homePage.assignedToMeMenuItem.click();

				await expect(page.getByText(objectEntry3.title)).toBeVisible();

				const now = new Date();

				const nextYear = now.getFullYear() + 1;

				const dueDate = nextYear + '-01-01';

				await homePage.updateDueDate(dueDate, objectEntry3.title);

				const workflowTaskRow = page.getByRole('row', {
					name: objectEntry3.title,
				});
				await workflowTaskRow.getByRole('button').click();
				await page
					.getByRole('menuitem', {name: 'Update Due Date'})
					.click();

				await expect(page.locator('input[type="date"]')).toHaveValue(
					dueDate
				);
			});
		}
		finally {
			if (objectEntry1) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					applicationName,
					String(objectEntry1.id)
				);
			}

			if (objectEntry2) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					applicationName,
					String(objectEntry2.id)
				);
			}

			if (objectEntry3) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					applicationName,
					String(objectEntry3.id)
				);
			}

			// Teardown for created web content workflow

			await processBuilderPage.goto('/test');
			await configurationTabPage.configurationTabLink.waitFor({
				state: 'visible',
			});
			await configurationTabPage.configurationTabLink.click({
				force: true,
			});
			await configurationTabPage.page.waitForURL((url) =>
				url.href.includes('=configuration')
			);

			await configurationTabPage.unassignWorkflowFromAssetType(
				'Basic Web Content'
			);
		}
	}
);

test(
	'Can only see valid asset types for workflow task',
	{tag: '@LPD-66218'},
	async ({
		apiHelpers,
		configurationTabPage,
		homePage,
		page,
		processBuilderPage,
	}) => {
		await processBuilderPage.goto('/test');
		await configurationTabPage.configurationTabLink.waitFor({
			state: 'visible',
		});
		await configurationTabPage.configurationTabLink.click({force: true});
		await configurationTabPage.page.waitForURL((url) =>
			url.href.includes('=configuration')
		);

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			'Account'
		);

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			'Basic Web Content'
		);

		const account = await apiHelpers.headlessAdminUser.postAccount({
			name: getRandomString(),
			type: 'business',
		});

		let objectEntry;

		const applicationName = 'cms/basic-web-contents';

		try {
			const contentName = getRandomString();

			objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentName,
				},
				applicationName,
				'Default'
			);

			await homePage.goto();

			await homePage.workflowTaskFilterButton.click();
			await homePage.assignedToMyRolesMenuItem.click();

			await expect(page.getByText(account.name)).toBeHidden();
			await expect(page.getByText(objectEntry.title)).toBeVisible();
		}
		finally {
			if (objectEntry) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					applicationName,
					String(objectEntry.id)
				);
			}

			// Teardown for created web content workflow

			await processBuilderPage.goto('/test');
			await configurationTabPage.configurationTabLink.waitFor({
				state: 'visible',
			});
			await configurationTabPage.configurationTabLink.click({
				force: true,
			});
			await configurationTabPage.page.waitForURL((url) =>
				url.href.includes('=configuration')
			);

			await configurationTabPage.unassignWorkflowFromAssetType(
				'Basic Web Content'
			);

			await page.reload();

			await configurationTabPage.unassignWorkflowFromAssetType('Account');
		}
	}
);

test(
	'Can see Recent Assets',
	{tag: '@LPD-58792'},
	async ({apiHelpers, homePage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = 'Default';
		let objectEntry1;
		let objectEntry2;

		const file1Title = `title ${getRandomString()}`;

		try {
			objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: file1Title,
				},
				applicationName,
				spaceName
			);

			objectEntry2 = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: `some content ${getRandomString()}`,
				},
				applicationName,
				spaceName
			);

			await homePage.goto();

			const dataSetFragmentPage: DataSetPage = new DataSetPage(page);

			const row = dataSetFragmentPage.table.bodyRows.filter({
				hasText: file1Title,
			});

			await expect(row.getByText(file1Title)).toBeVisible();
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry1.id)
			);
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry2.id)
			);
		}
	}
);

test(
	'Can use Quick Actions to create new content',
	{tag: '@LPD-58793'},
	async ({apiHelpers, homePage, page}) => {
		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
			},
			type: 'Space',
		});

		await test.step('Check redirection after clicking New Basic Web Content button', async () => {
			await homePage.goto();

			await homePage.basicWebContentButton.click();

			await homePage.selectSpace('Default');

			await expect(
				page.getByPlaceholder('New Basic Web Content')
			).toBeVisible();
		});

		await test.step('Check redirection after clicking Blog button', async () => {
			await homePage.goto();

			await homePage.blogButton.click();

			await homePage.selectSpace('Default');

			await expect(page.getByPlaceholder('New Blog')).toBeVisible();
		});

		await test.step('Check redirection after clicking Basic Document button', async () => {
			await homePage.goto();

			await homePage.basicDocumentButton.click();

			await homePage.selectSpace('Default');

			await expect(
				page.getByPlaceholder('New Basic Document')
			).toBeVisible();
		});

		await test.step('Check redirection after clicking Vocabulary button', async () => {
			await homePage.goto();

			await homePage.vocabularyButton.click();

			await expect(page.getByText('Basic Info')).toBeVisible();
		});
	}
);

test.skip(
	'Can use Search Bar to search for content',
	{tag: '@LPD-61220'},
	async ({apiHelpers, assetsPage, homePage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = 'Default';
		let objectEntry1;
		let objectEntry2;

		const file1Title = `title ${getRandomString()}`;

		try {
			objectEntry1 = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: file1Title,
				},
				applicationName,
				spaceName
			);

			objectEntry2 = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: `some content ${getRandomString()}`,
				},
				applicationName,
				spaceName
			);

			await homePage.goto();

			const searchInput = await page.getByPlaceholder('Search');

			await searchInput.fill('title');

			await searchInput.press('Enter');

			const row = assetsPage.table.bodyRows.filter({hasText: file1Title});

			await expect(row.getByText(file1Title)).toBeVisible();
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry1.id)
			);
			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(objectEntry2.id)
			);
		}
	}
);
