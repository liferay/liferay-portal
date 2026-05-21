/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {masterPagesPagesTest} from '../../../fixtures/masterPagesPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {styleBookPageTest} from '../../../fixtures/styleBookPageTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {doAndGoBack} from '../../../utils/doAndGoBack';
import fillAndClickOutside from '../../../utils/fillAndClickOutside';
import getRandomString from '../../../utils/getRandomString';

const STYLE_BOOK_NAME = getRandomString();

const TEST_COLOR = 'rgb(255, 0, 0)';

async function expectStyleBookToBeSelected({
	page,
	pageFriendlyUrlPath,
	siteFriendlyUrlPath,
}: {
	page: Page;
	pageFriendlyUrlPath: string;
	siteFriendlyUrlPath: string;
}) {
	await doAndGoBack(page, async () => {
		await page.goto(`/web${siteFriendlyUrlPath}${pageFriendlyUrlPath}`);

		await expect(page.locator('footer')).toHaveCSS(
			'background-color',
			TEST_COLOR
		);
	});
}

const test = mergeTests(
	featureFlagsTest({
		'LPD-76864': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	masterPagesPagesTest,
	pageEditorPagesTest,
	pagesAdminPagesTest,
	styleBookPageTest
);

test.beforeEach(async ({page, site, styleBooksPage}) => {
	await styleBooksPage.goto(site.friendlyUrlPath);

	await styleBooksPage.create(STYLE_BOOK_NAME);

	await fillAndClickOutside(
		page,
		page.getByLabel('Brand Color 4', {exact: true}).getByRole('textbox'),
		TEST_COLOR
	);

	await styleBooksPage.waitForAutoSave();

	await styleBooksPage.publish();
});

test(
	'The selected style book does not change when configuring the page',
	{tag: '@LPD-66976'},
	async ({page, pageEditorPage, pagesAdminPage, site}) => {
		const originalPageName = getRandomString();

		await test.step('Create a content page and select the created style book', async () => {
			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pagesAdminPage.createNewPage({
				draft: true,
				name: originalPageName,
			});

			await pageEditorPage.selectStyleBook(STYLE_BOOK_NAME);

			await pageEditorPage.publishPage();

			await expectStyleBookToBeSelected({
				page,
				pageFriendlyUrlPath: `/${originalPageName}`,
				siteFriendlyUrlPath: site.friendlyUrlPath,
			});
		});

		const modifiedPageName = getRandomString();

		const modifiedPageFriendlyUrlPath = `/${modifiedPageName}`;

		await test.step('Modify the page configuration', async () => {
			await pagesAdminPage.clickOnAction('Configure', originalPageName);

			await page
				.getByRole('textbox', {name: 'Name'})
				.fill(modifiedPageName);

			await page
				.getByRole('textbox', {name: 'Friendly URL'})
				.fill(modifiedPageFriendlyUrlPath);

			await pagesAdminPage.saveConfiguration();
		});

		await test.step('Assert the style book is still selected', async () => {
			await expectStyleBookToBeSelected({
				page,
				pageFriendlyUrlPath: modifiedPageFriendlyUrlPath,
				siteFriendlyUrlPath: site.friendlyUrlPath,
			});
		});
	}
);

test(
	'A user can select the style book of a page via Look and Feel settings',
	{tag: '@LPD-66976'},
	async ({page, pageEditorPage, pagesAdminPage, site}) => {
		const testCases = [
			{requiresPagePublish: true, template: 'Blank'},
			{requiresPagePublish: false, template: 'Widget Page'},
		];

		for (const {requiresPagePublish, template} of testCases) {
			const pageName = getRandomString();

			await test.step(`Create a "${template}" page`, async () => {
				await pagesAdminPage.goto(site.friendlyUrlPath);

				await pagesAdminPage.createNewPage({
					draft: true,
					name: pageName,
					template,
				});
			});

			await test.step('Select the created style book via Look and Feel settings', async () => {
				await pagesAdminPage.goto(site.friendlyUrlPath);

				await pagesAdminPage.goToDesignTabConfiguration(pageName);

				const styleBookTextbox = page.getByRole('textbox', {
					name: 'Style Book',
				});

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page
						.frameLocator('iframe[title="Select Style Book"]')
						.getByRole('button', {
							name: `Select ${STYLE_BOOK_NAME}`,
						}),
					trigger: styleBookTextbox,
				});

				await pagesAdminPage.saveConfiguration();

				await expect(styleBookTextbox).toHaveValue(STYLE_BOOK_NAME);
			});

			await test.step('Assert the style book is applied to the page', async () => {
				if (requiresPagePublish) {
					await pagesAdminPage.goto(site.friendlyUrlPath);

					await pagesAdminPage.editPage(pageName);

					await pageEditorPage.publishPage();
				}

				await expectStyleBookToBeSelected({
					page,
					pageFriendlyUrlPath: `/${pageName}`,
					siteFriendlyUrlPath: site.friendlyUrlPath,
				});
			});
		}
	}
);

test(
	'A user can select the style book of a master page based on the current selected theme',
	{tag: '@LPD-66976'},
	async ({masterPagesPage, page, pagesAdminPage, site}) => {
		const masterPageName = getRandomString();

		await test.step('Create a master page and go to design tab', async () => {
			await masterPagesPage.goto(site.friendlyUrlPath);

			await masterPagesPage.createNewMaster(masterPageName);

			await masterPagesPage.gotoConfiguration(masterPageName);

			await pagesAdminPage.clickOnTab('Design');
		});

		const frame = page
			.frameLocator('iframe[title="Select Style Book"]')
			.getByRole('main');

		const textbox = page.getByRole('textbox', {name: 'Style Book'});

		await test.step('Assert only Dialect style books are available when it is the selected theme', async () => {
			await pagesAdminPage.changeTheme('Dialect');

			await clickAndExpectToBeVisible({target: frame, trigger: textbox});

			await expect(frame.getByRole('button')).toHaveCount(1);
			await expect(textbox).toHaveValue('Styles from Dialect Theme');

			await page.getByRole('button', {name: 'Close'}).click();
		});

		await test.step('Assert only Classic style books are available when it is the selected theme', async () => {
			await pagesAdminPage.changeTheme('Classic');

			await clickAndExpectToBeVisible({target: frame, trigger: textbox});

			await expect(frame.getByRole('button')).toHaveCount(2);
			await expect(textbox).toHaveValue('Styles from Classic Theme');

			await frame
				.getByRole('button', {name: `Select ${STYLE_BOOK_NAME}`})
				.click();

			await pagesAdminPage.saveConfiguration();

			await masterPagesPage.goto(site.friendlyUrlPath);

			await masterPagesPage.publishMaster(masterPageName);
		});

		await test.step('Test style book with a content page', async () => {
			await pagesAdminPage.goto(site.friendlyUrlPath);

			const pageName = getRandomString();

			await pagesAdminPage.createNewPage({
				name: pageName,
				template: masterPageName,
			});

			await expectStyleBookToBeSelected({
				page,
				pageFriendlyUrlPath: `/${pageName}`,
				siteFriendlyUrlPath: site.friendlyUrlPath,
			});
		});
	}
);
