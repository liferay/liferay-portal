/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {addCMSAdministrator} from '../../../utils/addCMSAdministrator';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLoginViaApi,
	performLogout,
	performUserSwitch,
	performUserSwitchViaApi,
	userData,
} from '../../../utils/performLogin';
import {SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE} from '../../setup/site-cms-site/constants/space';
import {structureBuilderPagesTest} from '../structure-builder/fixtures/structureBuilderPagesTest';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {DataSetPage} from './pages/DataSetPage';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
	}),
	loginTest(),
	structureBuilderPagesTest,
	workflowPagesTest
);

let cmsAdminUser: TUserAccount;
let setupData: Array<{id: number | string; type: string}>;
let spaceAdminUser: TUserAccount;
let spaceUser: TUserAccount;

test.beforeAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	cmsAdminUser = await addCMSAdministrator(apiHelpers);

	apiHelpers.data.push({id: cmsAdminUser.id, type: 'userAccount'});

	spaceAdminUser = await apiHelpers.headlessAdminUser.postUserAccount();

	apiHelpers.data.push({id: spaceAdminUser.id, type: 'userAccount'});

	userData[spaceAdminUser.alternateName] = {
		name: spaceAdminUser.givenName,
		password: 'test',
		surname: spaceAdminUser.familyName,
	};

	spaceUser = await apiHelpers.headlessAdminUser.postUserAccount();

	apiHelpers.data.push({id: spaceUser.id, type: 'userAccount'});

	userData[spaceUser.alternateName] = {
		name: spaceUser.givenName,
		password: 'test',
		surname: spaceUser.familyName,
	};

	await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
		SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE,
		spaceAdminUser.externalReferenceCode
	);

	await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
		SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE,
		spaceAdminUser.externalReferenceCode,
		['Asset Library Administrator']
	);

	await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
		SITE_CMS_SPACE_EXTERNAL_REFERENCE_CODE,
		spaceUser.externalReferenceCode
	);

	setupData = [...apiHelpers.data];

	await page.close();
});

test.afterAll(async ({browser}) => {
	const page = await browser.newPage();

	await performLoginViaApi({page, screenName: 'test'});

	const apiHelpers = new DataApiHelpers(page);

	apiHelpers.setData(setupData);

	await apiHelpers.clearData();

	await page.close();
});

const BASIC_WEB_CONTENT_APPLICATION_NAME = 'cms/basic-web-contents';

async function addBasicWebContent(
	apiHelpers: DataApiHelpers,
	spaceName: string,
	title: string
) {
	return apiHelpers.objectEntry.postObjectEntry(
		{
			objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			title,
		},
		BASIC_WEB_CONTENT_APPLICATION_NAME,
		spaceName
	);
}

async function addSpaceWithWorkflow(apiHelpers: DataApiHelpers) {
	const objectDefinition =
		await apiHelpers.objectAdmin.getObjectDefinitionByName(
			'CMSBasicWebContent'
		);

	const workflowDefinition =
		await apiHelpers.headlessAdminWorkflow.getWorkflowDefinitionByName(
			'Single Approver'
		);

	const space = await apiHelpers.headlessAssetLibrary.createSpace();

	await apiHelpers.headlessAdminWorkflow.postWorkflowDefinitionLink(
		objectDefinition.className,
		space.siteId,
		workflowDefinition.id,
		workflowDefinition.name,
		Number(workflowDefinition.version)
	);

	return space;
}

async function assignWorkflowTaskToMe(
	apiHelpers: DataApiHelpers,
	assetClassName: string,
	assetPrimaryKey: number
) {
	const userAccount = await apiHelpers.headlessAdminUser.getMyUserAccount();

	let workflowTask;

	await expect(async () => {
		workflowTask =
			await apiHelpers.headlessAdminWorkflow.getWorkflowTaskByAsset(
				assetClassName,
				String(assetPrimaryKey)
			);

		expect(workflowTask).toBeTruthy();
	}).toPass();

	await apiHelpers.headlessAdminWorkflow.postAssignTaskToUser(
		workflowTask.id,
		userAccount.id
	);
}

async function addBasicWebContentAssignedToMe(
	apiHelpers: DataApiHelpers,
	spaceName: string,
	title: string
) {
	const objectDefinition =
		await apiHelpers.objectAdmin.getObjectDefinitionByName(
			'CMSBasicWebContent'
		);

	const objectEntry = await addBasicWebContent(apiHelpers, spaceName, title);

	await assignWorkflowTaskToMe(
		apiHelpers,
		objectDefinition.className,
		objectEntry.id
	);

	return objectEntry;
}

function getMyTasksSection(page: Page) {
	return page.locator('div.container-fluid-max').filter({
		has: page.getByRole('heading', {exact: true, name: 'My Tasks'}),
	});
}

function getMyTasksRows(page: Page) {
	return getMyTasksSection(page)
		.getByRole('row')
		.filter({hasText: /sent you/i});
}

test(
	'My Tasks "View All" opens the pending tasks assigned to the user',
	{tag: ['@LPD-78912', '@LPD-116311', '@LPD-92859', '@LPD-112143']},
	async ({apiHelpers, homePage, page}) => {
		const space = await addSpaceWithWorkflow(apiHelpers);

		const myTaskTitle = `My task ${getRandomString()}`;
		const roleTaskTitle = `Role task ${getRandomString()}`;

		await addBasicWebContent(apiHelpers, space.name, roleTaskTitle);
		await addBasicWebContentAssignedToMe(
			apiHelpers,
			space.name,
			myTaskTitle
		);

		await homePage.goto();

		await page.getByRole('link', {name: 'View All My Tasks'}).click();

		await expect(page).toHaveURL(/\/tasks\?/);

		const assigneeFilter = page
			.getByRole('group')
			.filter({hasText: 'Assignee:'});

		await expect(assigneeFilter).toBeVisible();
		await expect(
			page.getByRole('group').filter({hasText: 'Status: Pending'})
		).toBeVisible();

		const myTaskRow = page.getByRole('row').filter({hasText: myTaskTitle});
		const roleTaskRow = page
			.getByRole('row')
			.filter({hasText: roleTaskTitle});

		await expect(myTaskRow).toBeVisible();
		await expect(roleTaskRow).toBeHidden();

		await assigneeFilter
			.getByRole('button', {name: 'Remove Filter'})
			.click();

		await expect(roleTaskRow).toBeVisible();
	}
);

test(
	'My Tasks lists the 8 tasks most recently assigned to the user',
	{tag: '@LPD-116311'},
	async ({apiHelpers, homePage, page}) => {
		const space = await addSpaceWithWorkflow(apiHelpers);

		const roleTaskTitle = `Role task ${getRandomString()}`;

		await addBasicWebContent(apiHelpers, space.name, roleTaskTitle);

		const titles = Array.from(
			{length: 9},
			(_, index) => `My task ${index} ${getRandomString()}`
		);

		for (const title of titles) {
			await addBasicWebContentAssignedToMe(apiHelpers, space.name, title);
		}

		await homePage.goto();

		const rows = getMyTasksRows(page);

		await expect(rows).toHaveCount(8);

		await expect(rows.first()).toContainText(titles[8]);

		await expect(rows.filter({hasText: titles[0]})).toBeHidden();
		await expect(rows.filter({hasText: roleTaskTitle})).toBeHidden();
	}
);

test(
	'My Tasks shows an empty state when the user has no assigned tasks',
	{tag: '@LPD-116311'},
	async ({homePage, page}) => {
		try {
			await performUserSwitch(page, spaceUser.alternateName);

			await homePage.goto();

			const myTasksSection = getMyTasksSection(page);

			await expect(
				myTasksSection.getByText('No Tasks Yet')
			).toBeVisible();
			await expect(
				myTasksSection.getByText('There are no tasks assigned to you.')
			).toBeVisible();
		}
		finally {
			await performUserSwitch(page, 'test');
		}
	}
);

test(
	'Can manage my workflow tasks',
	{tag: ['@LPD-58790', '@LPD-116311']},
	async ({apiHelpers, homePage, page}) => {
		const space = await addSpaceWithWorkflow(apiHelpers);

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			space.externalReferenceCode,
			spaceAdminUser.externalReferenceCode
		);

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			space.externalReferenceCode,
			spaceAdminUser.externalReferenceCode,
			['Asset Library Administrator']
		);

		const objectEntry1 = await addBasicWebContentAssignedToMe(
			apiHelpers,
			space.name,
			getRandomString()
		);
		const objectEntry2 = await addBasicWebContentAssignedToMe(
			apiHelpers,
			space.name,
			getRandomString()
		);
		const objectEntry3 = await addBasicWebContentAssignedToMe(
			apiHelpers,
			space.name,
			getRandomString()
		);

		await homePage.goto();

		await test.step('Verify workflow task transition action', async () => {
			const workflowTaskRow = getMyTasksRows(page).filter({
				hasText: objectEntry3.title,
			});

			await workflowTaskRow.getByRole('button').click();

			await page.getByRole('menuitem', {name: 'Approve'}).click();

			await page.getByRole('button', {name: 'Save'}).click();

			await expect(workflowTaskRow).toBeHidden();
		});

		await test.step('Verify workflow task assign to... action', async () => {
			const workflowTaskRow = getMyTasksRows(page).filter({
				hasText: objectEntry1.title,
			});

			await expect(workflowTaskRow).toBeVisible();

			await homePage.assignTo(objectEntry1.title);

			await expect(workflowTaskRow).toBeHidden();
		});

		await test.step('Verify workflow task update due date action', async () => {
			const dueDate = `${new Date().getFullYear() + 1}-01-01`;

			await homePage.updateDueDate(dueDate, objectEntry2.title);

			await getMyTasksRows(page)
				.filter({hasText: objectEntry2.title})
				.getByRole('button')
				.click();

			await page.getByRole('menuitem', {name: 'Update Due Date'}).click();

			await expect(page.locator('input[type="date"]')).toHaveValue(
				dueDate
			);

			await page.getByRole('button', {name: 'Cancel'}).click();
		});
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

		try {
			await assignWorkflowTaskToMe(
				apiHelpers,
				'com.liferay.account.model.AccountEntry',
				account.id
			);

			const objectEntry = await addBasicWebContentAssignedToMe(
				apiHelpers,
				'Default',
				getRandomString()
			);

			await homePage.goto();

			const myTasksSection = getMyTasksSection(page);

			await expect(
				myTasksSection.getByRole('link', {name: objectEntry.title})
			).toBeVisible();

			await expect(myTasksSection.getByText(account.name)).toBeHidden();
		}
		finally {

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

		const file1Title = `title ${getRandomString()}`;

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
				title: `some content ${getRandomString()}`,
			},
			applicationName,
			spaceName
		);

		await homePage.goto();

		const dataSetFragmentPage: DataSetPage = new DataSetPage(page);

		const row = dataSetFragmentPage.table.bodyRows.getByLabel(file1Title);

		await expect(row.getByText(file1Title)).toBeVisible();
	}
);

test(
	'Can go to All page from Recent Assets button',
	{tag: '@LPD-83675'},
	async ({apiHelpers, homePage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = 'Default';

		const file1Title = `title ${getRandomString()}`;

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
				title: `some content ${getRandomString()}`,
			},
			applicationName,
			spaceName
		);

		await homePage.goto();

		await homePage.viewAllButton.click();

		await expect(page.getByRole('heading', {name: 'All'})).toBeVisible();
	}
);

test(
	'Recent Assets shows at most the 16 most recent items',
	{tag: '@LPD-95795'},
	async ({apiHelpers, homePage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = 'Default';

		const oldestAssetTitle = `recent asset ${getRandomString()}`;

		for (let index = 0; index < 17; index++) {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title:
						index === 0
							? oldestAssetTitle
							: `recent asset ${getRandomString()}`,
				},
				applicationName,
				spaceName
			);
		}

		await homePage.goto();

		const recentAssets = page.locator('.recent-assets-fds');

		await expect(recentAssets.locator('tbody tr')).toHaveCount(16);
		await expect(
			recentAssets.locator('.pagination-bar')
		).not.toBeAttached();

		await expect(
			recentAssets.getByText(oldestAssetTitle, {exact: true})
		).toHaveCount(0);
	}
);

test(
	'Can only see Recent Assets the user has VIEW permission on',
	{tag: '@LPD-87568'},
	async ({apiHelpers, homePage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const restrictedTitle = `restricted ${getRandomString()}`;
		const visibleTitle = `visible ${getRandomString()}`;

		const restrictedSpace =
			await apiHelpers.headlessAssetLibrary.createSpace({
				settings: {
					logoColor: 'outline-3',
					sharingEnabled: true,
				},
			});

		try {
			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: restrictedTitle,
				},
				applicationName,
				restrictedSpace.name
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: visibleTitle,
				},
				applicationName,
				'Default'
			);

			const dataSetFragmentPage: DataSetPage = new DataSetPage(page);

			await test.step('Default admin can see both assets in Recent Assets', async () => {
				await homePage.goto();

				await expect(
					dataSetFragmentPage.getRow(visibleTitle)
				).toBeVisible();

				await expect(
					dataSetFragmentPage.getRow(restrictedTitle)
				).toBeVisible();
			});

			await test.step('Space User cannot see the restricted asset in Recent Assets', async () => {
				await performUserSwitch(page, spaceUser.alternateName);

				await homePage.goto();

				await expect(
					dataSetFragmentPage.getRow(visibleTitle)
				).toBeVisible();

				await expect(
					dataSetFragmentPage.getRow(restrictedTitle)
				).toBeHidden();
			});
		}
		finally {
			await performUserSwitch(page, 'test');
		}
	}
);

test(
	'Can perform asset actions on Recent Assets rows',
	{tag: '@LPD-87568'},
	async ({apiHelpers, homePage, page}) => {
		const contentApplicationName = 'cms/basic-web-contents';
		const contentTitle = `content ${getRandomString()}`;
		const fileApplicationName = 'cms/basic-documents';
		const fileName = `file_${getRandomString()}.png`;
		const fileTitle = `file ${getRandomString()}`;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			contentApplicationName,
			'Default'
		);

		const fileEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				file: {
					fileBase64: 'R0lGODlhAQABAAAAACw=',
					name: fileName,
				},
				objectEntryFolderExternalReferenceCode: 'L_FILES',
				title: fileTitle,
			},
			fileApplicationName,
			'Default'
		);

		apiHelpers.data.push({
			id: fileEntry.file.id,
			type: 'document',
		});

		const dataSetFragmentPage: DataSetPage = new DataSetPage(page);

		await homePage.goto();

		await test.step('Recent Assets shows the content and file rows', async () => {
			await expect(
				dataSetFragmentPage.getRow(contentTitle)
			).toBeVisible();

			await expect(dataSetFragmentPage.getRow(fileTitle)).toBeVisible();
		});

		await test.step('File row action menu shows Download and inherited actions', async () => {
			await dataSetFragmentPage
				.getRow(fileTitle)
				.getByRole('button', {name: `${fileTitle} Actions`})
				.click();

			for (const action of [
				'Delete',
				'Download',
				'Edit',
				'Permissions',
				'Share',
				'View History',
			]) {
				await expect(
					page.getByRole('menuitem', {
						exact: true,
						name: action,
					})
				).toBeVisible();
			}

			await page.keyboard.press('Escape');
		});

		await test.step('Content row action menu shows inherited actions but no Download', async () => {
			await dataSetFragmentPage
				.getRow(contentTitle)
				.getByRole('button', {
					name: `${contentTitle} Actions`,
				})
				.click();

			for (const action of [
				'Delete',
				'Edit',
				'Permissions',
				'Share',
				'View History',
			]) {
				await expect(
					page.getByRole('menuitem', {
						exact: true,
						name: action,
					})
				).toBeVisible();
			}

			await expect(
				page.getByRole('menu').getByRole('menuitem', {
					exact: true,
					name: 'Download',
				})
			).toBeHidden();

			await page.keyboard.press('Escape');
		});

		await test.step('Can download a file asset from Recent Assets', async () => {
			const downloadPromise = page.waitForEvent('download');

			await dataSetFragmentPage.execItemAction({
				action: 'Download',
				filter: fileTitle,
			});

			const download = await downloadPromise;

			expect(download.suggestedFilename()).toBe(fileName);
		});
	}
);

test(
	'Can view an asset from Recent Assets',
	{tag: '@LPD-93228'},
	async ({apiHelpers, homePage, page}) => {
		const contentApplicationName = 'cms/basic-web-contents';
		const contentTitle = `content ${getRandomString()}`;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			contentApplicationName,
			'Default'
		);

		const dataSetFragmentPage: DataSetPage = new DataSetPage(page);

		await homePage.goto();

		await test.step('Can open the asset navigation modal with the View action', async () => {
			await dataSetFragmentPage.execItemAction({
				action: 'View',
				filter: contentTitle,
			});

			await expect(page.getByTestId('modal-header-name')).toHaveText(
				contentTitle
			);
		});

		await test.step('Details panel shows the metadata with the location breadcrumb', async () => {
			await page.getByRole('button', {name: 'Show Details'}).click();

			const spaceBreadcrumb = page.locator(
				'.asset-metadata .space-breadcrumb'
			);

			await expect(spaceBreadcrumb).toBeVisible();
		});
	}
);

test(
	'Can use Quick Actions to create new content',
	{tag: '@LPD-58793'},
	async ({apiHelpers, homePage, page}) => {
		await apiHelpers.headlessAssetLibrary.createSpace({
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
			},
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

test(
	'Can see a custom Content Structure as a quick action',
	{tag: '@LPD-87559'},
	async ({homePage, page, structureBuilderPage}) => {
		const structureLabel = `Custom${getRandomInt()}`;

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			page: structureBuilderPage,
			spaces: ['Default'],
		});

		const verifyCustomStructureQuickAction = async () => {
			await homePage.goto();

			const customStructureButton = page.getByRole('button', {
				name: structureLabel,
			});

			await expect(customStructureButton).toBeVisible();

			await customStructureButton.click();

			await expect(
				page.getByPlaceholder(`New ${structureLabel}`)
			).toBeVisible();
		};

		await test.step(
			'Default admin can use the custom Content Structure quick action',
			verifyCustomStructureQuickAction
		);

		await test.step('CMS Administrator can use the custom Content Structure quick action', async () => {
			await performUserSwitch(page, cmsAdminUser.alternateName);

			await verifyCustomStructureQuickAction();
		});

		await test.step('Space Administrator can use the custom Content Structure quick action', async () => {
			await performUserSwitch(page, spaceAdminUser.alternateName);

			await verifyCustomStructureQuickAction();
		});

		await test.step('Space User does not see the Quick Actions section', async () => {
			await performUserSwitch(page, spaceUser.alternateName);

			await homePage.goto();

			await expect(
				page.getByRole('heading', {name: 'Quick Actions'})
			).not.toBeVisible();
		});
	}
);

test(
	'Can use Search Bar to search for content',
	{tag: ['@LPD-61220', '@LPD-89781']},
	async ({apiHelpers, assetsPage, homePage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const spaceName = 'Default';

		const file1Title = `title ${getRandomString()}`;

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
				title: `some content ${getRandomString()}`,
			},
			applicationName,
			spaceName
		);

		await homePage.goto();

		const searchInput = await page.getByPlaceholder('Search');

		await searchInput.fill('title');

		await searchInput.press('Enter');

		await test.step('Verify URL uses the FDS pretty format', async () => {
			await expect(page).toHaveURL(/_fdsConfig=\(q:title\)(?:&|$)/);
		});

		const row = assetsPage.table.bodyRows.filter({hasText: file1Title});

		await expect(row.getByRole('link', {name: file1Title})).toBeVisible();

		await test.step('Verify search input contains the search value', async () => {
			const searchInput = page.getByRole('searchbox', {name: 'Search'});

			await expect(searchInput).toHaveValue('title');
		});
	}
);

test(
	'Can access the CMS when assigned to a Space via user group',
	{tag: '@LPD-83640'},
	async ({apiHelpers, page}) => {
		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUsersToUserGroup(
			userGroup.id,
			[user.id]
		);

		const space = await apiHelpers.headlessAssetLibrary.createSpace({
			settings: {
				logoColor: 'outline-3',
				sharingEnabled: true,
			},
		});

		await apiHelpers.jsonWebServicesUserGroup.assignUserGroupsToGroup(
			String(space.siteId),
			String(userGroup.id)
		);

		await performLogout(page);
		await performLogin(page, user.alternateName);

		await page.goto('/web/cms');

		await expect(
			page.getByRole('heading', {name: `Welcome, ${user.givenName}!`})
		).toBeVisible();
	}
);

test(
	'Recent Assets shows the editor as "Modified by" after another user moves the content',
	{tag: '@LPD-89977'},
	async ({apiHelpers, assetsPage, homePage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const contentTitle = `Content ${getRandomString()}`;
		const destinationFolderName = `Folder ${getRandomString()}`;
		const destinationSpaceName = `Destination ${getRandomString()}`;

		const dataSetFragmentPage: DataSetPage = new DataSetPage(page);
		const editorFullName = `${spaceAdminUser.givenName} ${spaceAdminUser.familyName}`;

		const destinationSpace =
			await apiHelpers.headlessAssetLibrary.createSpace({
				name: destinationSpaceName,
			});

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
			destinationSpace.externalReferenceCode,
			spaceAdminUser.externalReferenceCode
		);

		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			destinationSpace.externalReferenceCode,
			spaceAdminUser.externalReferenceCode,
			['Asset Library Administrator']
		);

		await apiHelpers.objectFolder.createObjectEntryFolder({
			parentObjectEntryFolderExternalReferenceCode: 'L_CONTENTS',
			scopeKey: destinationSpaceName,
			title: destinationFolderName,
		});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			applicationName,
			'Default'
		);

		await test.step('Sign in as the Space Administrator and move the content to the destination Space', async () => {
			await performUserSwitchViaApi(page, spaceAdminUser.alternateName);

			await assetsPage.gotoAll();

			await assetsPage.moveTo({
				destinationFolder: destinationFolderName,
				destinationSpace: destinationSpaceName,
				itemTitle: contentTitle,
			});
		});

		await test.step('Recent Assets attributes the modification to the Space Administrator', async () => {
			await homePage.goto();

			const row = dataSetFragmentPage.getRow(contentTitle);

			await expect(
				row.getByText(new RegExp(`by ${editorFullName}$`))
			).toBeVisible();
		});

		await performUserSwitchViaApi(page, 'test');
	}
);

test(
	'Recent Assets shows the editor as "Modified by" after another user edits the content',
	{tag: '@LPD-89977'},
	async ({apiHelpers, homePage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const contentTitle = `Content ${getRandomString()}`;
		const updatedTitle = `Updated ${getRandomString()}`;

		const dataSetFragmentPage: DataSetPage = new DataSetPage(page);
		const editorFullName = `${spaceAdminUser.givenName} ${spaceAdminUser.familyName}`;

		const contentEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			applicationName,
			'Default'
		);

		await performUserSwitchViaApi(page, spaceAdminUser.alternateName);

		await apiHelpers.objectEntry.patchObjectEntry(
			{
				title_i18n: {
					en_US: updatedTitle,
				},
			},
			applicationName,
			contentEntry.id
		);

		await homePage.goto();

		const row = dataSetFragmentPage.getRow(updatedTitle);

		await expect(
			row.getByText(new RegExp(`by ${editorFullName}$`))
		).toBeVisible();

		await performUserSwitchViaApi(page, 'test');
	}
);
