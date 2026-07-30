/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {waitForModal} from '../../../utils/waitFor';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {cmpPagesTest} from './fixtures/cmpPagesTest';

const test = mergeTests(
	cmpPagesTest,
	cmsPagesTest,
	dataApiHelpersTest,
	loginTest()
);

const BASIC_WEB_CONTENTS = 'cms/basic-web-contents';
const CMP_PROJECTS = 'cmp/projects';

test(
	'Keeps a linked project after reopening the content editor',
	{tag: ['@LPD-98901']},
	async ({apiHelpers, contentsPage, page}) => {
		const contentTitle = `Content ${getRandomString()}`;
		const projectTitle = `Project ${getRandomString()}`;

		let contentEntry;
		let projectEntry;

		try {
			await test.step('Create a project and a content item', async () => {
				projectEntry = await apiHelpers.objectEntry.postObjectEntry(
					{title: projectTitle},
					CMP_PROJECTS
				);

				contentEntry = await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						title: contentTitle,
					},
					BASIC_WEB_CONTENTS,
					'Default'
				);
			});

			await test.step('Link the project from the Projects panel', async () => {
				await contentsPage.goto();

				await contentsPage.editContent(contentTitle);

				await contentsPage.openSidePanel('Projects');

				await page.getByRole('combobox', {name: 'Projects'}).click();

				await page.getByRole('option', {name: projectTitle}).click();

				await expect(
					page.getByRole('link', {name: projectTitle})
				).toBeVisible();
			});

			await test.step('Reopen the content and assert the link persisted', async () => {
				await contentsPage.goto();

				await contentsPage.editContent(contentTitle);

				await contentsPage.openSidePanel('Projects');

				await expect(
					page.getByRole('link', {name: projectTitle})
				).toBeVisible();
			});
		}
		finally {
			if (contentEntry) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					BASIC_WEB_CONTENTS,
					String(contentEntry.id)
				);
			}

			if (projectEntry) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					CMP_PROJECTS,
					String(projectEntry.id)
				);
			}
		}
	}
);

test(
	'Links and unlinks a project from the content editor',
	{tag: ['@LPD-98901']},
	async ({apiHelpers, contentsPage, page}) => {
		const contentTitle = `Content ${getRandomString()}`;
		const projectTitle = `Project ${getRandomString()}`;

		let contentEntry;
		let projectEntry;

		try {
			await test.step('Create a past-due project and a content item', async () => {
				projectEntry = await apiHelpers.objectEntry.postObjectEntry(
					{dueDate: '2020-01-01', title: projectTitle},
					CMP_PROJECTS
				);

				contentEntry = await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						title: contentTitle,
					},
					BASIC_WEB_CONTENTS,
					'Default'
				);
			});

			await test.step('Link the project from the Projects panel', async () => {
				await contentsPage.goto();

				await contentsPage.editContent(contentTitle);

				await contentsPage.openSidePanel('Projects');

				await page.getByRole('combobox', {name: 'Projects'}).click();

				await page.getByRole('option', {name: projectTitle}).click();
			});

			await test.step('Assert the linked project card and overdue badge', async () => {
				await expect(
					page.getByRole('link', {name: projectTitle})
				).toBeVisible();

				await expect(page.getByText('Overdue')).toBeVisible();
			});

			await test.step('Assert the project cannot be linked twice', async () => {
				await page.getByRole('combobox', {name: 'Projects'}).click();

				await expect(
					page.getByRole('option', {name: projectTitle})
				).toBeHidden();

				await page.keyboard.press('Escape');
			});

			await test.step('Remove the linked project', async () => {
				await page
					.locator('.cms-linked-projects')
					.getByLabel('Remove', {exact: true})
					.click();

				await expect(
					page.getByRole('link', {name: projectTitle})
				).toBeHidden();
			});
		}
		finally {
			if (contentEntry) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					BASIC_WEB_CONTENTS,
					String(contentEntry.id)
				);
			}

			if (projectEntry) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					CMP_PROJECTS,
					String(projectEntry.id)
				);
			}
		}
	}
);

test(
	'Links and unlinks a project from the content list info panel',
	{tag: ['@LPD-98942']},
	async ({apiHelpers, contentsPage, infoPanelPage, page}) => {
		const contentTitle = `Content ${getRandomString()}`;
		const projectTitle = `Project ${getRandomString()}`;

		let contentEntry;
		let projectEntry;

		try {
			await test.step('Create a past-due project and a content item', async () => {
				projectEntry = await apiHelpers.objectEntry.postObjectEntry(
					{dueDate: '2020-01-01', title: projectTitle},
					CMP_PROJECTS
				);

				contentEntry = await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						title: contentTitle,
					},
					BASIC_WEB_CONTENTS,
					'Default'
				);
			});

			await test.step('Open the info panel Projects tab', async () => {
				await contentsPage.goto();

				await contentsPage.viewShowDetails(contentTitle);

				await infoPanelPage.selectTab('Projects').click();
			});

			await test.step('Link the project and assert the card', async () => {
				await page.getByRole('combobox', {name: 'Projects'}).click();

				await page.getByRole('option', {name: projectTitle}).click();

				await expect(
					page.getByRole('link', {name: projectTitle})
				).toBeVisible();

				await expect(page.getByText('Overdue')).toBeVisible();
			});

			await test.step('Remove the linked project', async () => {
				await page
					.locator('.cms-linked-projects')
					.getByLabel('Remove', {exact: true})
					.click();

				await expect(
					page.getByRole('link', {name: projectTitle})
				).toBeHidden();
			});
		}
		finally {
			if (contentEntry) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					BASIC_WEB_CONTENTS,
					String(contentEntry.id)
				);
			}

			if (projectEntry) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					CMP_PROJECTS,
					String(projectEntry.id)
				);
			}
		}
	}
);

test(
	'Links multiple assets to projects in bulk from the content list',
	{tag: ['@LPD-99127']},
	async ({apiHelpers, assetsPage, contentsPage, infoPanelPage, page}) => {
		const contentTitles = [
			`Content ${getRandomString()}`,
			`Content ${getRandomString()}`,
		];
		const projectTitles = [
			`Project ${getRandomString()}`,
			`Project ${getRandomString()}`,
		];

		await test.step('Create two projects and two content items', async () => {
			for (const projectTitle of projectTitles) {
				const projectEntry =
					await apiHelpers.objectEntry.postObjectEntry(
						{title: projectTitle},
						CMP_PROJECTS
					);

				apiHelpers.data.push({
					applicationName: CMP_PROJECTS,
					id: projectEntry.id,
					type: 'objectEntry',
				});
			}

			for (const contentTitle of contentTitles) {
				const contentEntry =
					await apiHelpers.objectEntry.postObjectEntry(
						{
							objectEntryFolderExternalReferenceCode:
								'L_CONTENTS',
							title: contentTitle,
						},
						BASIC_WEB_CONTENTS,
						'Default'
					);

				apiHelpers.data.push({
					applicationName: BASIC_WEB_CONTENTS,
					id: contentEntry.id,
					type: 'objectEntry',
				});
			}
		});

		await test.step('Select both assets and open the bulk action', async () => {
			await assetsPage.gotoAll();

			for (const contentTitle of contentTitles) {
				await assetsPage
					.getItem(contentTitle)
					.locator('input[title="Select Item"]')
					.check();
			}

			await assetsPage.execBulkItemAction('Add Assets to Project');

			await waitForModal({page});
		});

		await test.step('Pick both projects and confirm', async () => {
			for (const projectTitle of projectTitles) {
				await page
					.getByRole('combobox', {name: 'Select Project'})
					.click();

				await page.getByRole('option', {name: projectTitle}).click();
			}

			await expect(
				page.getByRole('link', {name: projectTitles[0]})
			).toBeVisible();
			await expect(
				page.getByRole('link', {name: projectTitles[1]})
			).toBeVisible();

			await page
				.getByRole('button', {exact: true, name: 'Confirm'})
				.click();
		});

		await test.step('Assert the completion toast names the projects', async () => {
			await expect(
				page.getByText(
					`added to ${projectTitles[0]}, ${projectTitles[1]}`
				)
			).toBeVisible({timeout: 60000});
		});

		await test.step('Assert one asset shows both projects in the info panel', async () => {
			await contentsPage.goto();

			await contentsPage.viewShowDetails(contentTitles[0]);

			await infoPanelPage.selectTab('Projects').click();

			await expect(
				page.getByRole('link', {name: projectTitles[0]})
			).toBeVisible();
			await expect(
				page.getByRole('link', {name: projectTitles[1]})
			).toBeVisible();
		});
	}
);
