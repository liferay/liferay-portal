/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {styleBookPageTest} from '../../../fixtures/styleBookPageTest';
import {PagesAdminPage} from '../../../pages/layout-admin-web/PagesAdminPage';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pagesAdminPagesTest,
	styleBookPageTest
);

const INVALID_TOKEN_MESSAGE = 'Tokens cannot reference itself.';
const STYLEBOOK_BRAND_COLOR_1 = 'Brand Color 1';
const STYLEBOOK_BRAND_COLOR_2 = 'Brand Color 2';
const STYLEBOOK_BRAND_COLORS_SECTION = 'Brand Colors';
const STYLEBOOK_NAME = getRandomString();

async function createPageAndAddContainer({
	friendlyUrlPath,
	pageEditorPage,
	pageName,
	pagesAdminPage,
}: {
	friendlyUrlPath: string;
	pageEditorPage: PageEditorPage;
	pageName?: string;
	pagesAdminPage: PagesAdminPage;
}) {
	await pagesAdminPage.goto(friendlyUrlPath);

	await pagesAdminPage.createNewPage({
		draft: true,
		name: pageName || getRandomString(),
	});

	await pageEditorPage.addFragment('Layout Elements', 'Container');
}

function getBackgroundColorInputLocator(page: Page) {
	const backgroundColorSection = page.getByLabel('Background Color', {
		exact: true,
	});

	return backgroundColorSection.getByRole('textbox');
}

async function waitForInvalidTokenMessage(page: Page, message: string) {
	await expect(page.getByText(message)).toBeVisible();
}

test.describe('Fragment Style Configuration and Token Detachment', () => {
	test.beforeEach(async ({pageEditorPage, pagesAdminPage, site}) => {
		await test.step('Create a content page and add a container', async () => {
			await createPageAndAddContainer({
				friendlyUrlPath: site.friendlyUrlPath,
				pageEditorPage,
				pagesAdminPage,
			});
		});
	});

	test(
		'Assert the reference will be autocompleted as 6 digit characters, if the user fill out 3 digit characters when clicking on the tab, intro or outside the output.',
		{tag: '@LPS-141568'},
		async ({page, pageEditorPage}) => {
			await test.step('Change the background color and type a 3 digits color reference', async () => {
				await pageEditorPage.goToConfigurationTab('Styles');

				await getBackgroundColorInputLocator(page).fill('03C');
			});

			await test.step('Check if the color reference is autocompleted after trigger auto save', async () => {
				await pageEditorPage.goToConfigurationTab('Styles');

				await pageEditorPage.waitForChangesSaved();

				await expect(getBackgroundColorInputLocator(page)).toHaveValue(
					'0033CC'
				);
			});

			await test.step('Assert the background color of the container', async () => {
				await expect(
					pageEditorPage.getFragment(
						await pageEditorPage.getFragmentId('Container')
					)
				).toHaveCSS('background-color', 'rgb(0, 51, 204)');
			});
		}
	);

	test(
		'Assert that the user could detach the linked token.',
		{tag: '@LPS-136199'},
		async ({page, pageEditorPage}) => {
			await test.step('Change the background color to success', async () => {
				await pageEditorPage.changeFragmentConfiguration({
					fieldLabel: 'Background Color',
					fragmentId: await pageEditorPage.getFragmentId('Container'),
					tab: 'Styles',
					value: 'Success',
					valueFromStylebook: true,
				});
			});

			await test.step('Detach the linked token and assert that the color reference is shown', async () => {
				await pageEditorPage.goToConfigurationTab('Styles');

				await page
					.getByLabel('Background Color', {exact: true})
					.getByLabel('Detach Style')
					.click();

				await expect(getBackgroundColorInputLocator(page)).toHaveValue(
					'287D3C'
				);

				await expect(
					pageEditorPage.getFragment(
						await pageEditorPage.getFragmentId('Container')
					)
				).toHaveCSS('background-color', 'rgb(40, 125, 60)');
			});
		}
	);
});

test.describe('Style Book Token Validation and Publication Workflow', () => {
	test.beforeEach(async ({site, styleBooksPage}) => {
		await test.step('Create a style book', async () => {
			await styleBooksPage.goto(site.friendlyUrlPath);

			await styleBooksPage.create(STYLEBOOK_NAME);
		});
	});

	test(
		'Check if the user could cancel publish process in style book editor if link invalid color token.',
		{tag: '@LPS-145650'},
		async ({page, styleBooksHelper, styleBooksPage}) => {
			const STYLEBOOK_CATEGORY_VALUE = '00CCBB';

			await test.step('Change the Brand Color 1 to an invalid color token', async () => {
				await styleBooksPage.updateTokenInputColor(
					STYLEBOOK_BRAND_COLOR_1,
					STYLEBOOK_BRAND_COLOR_1,
					STYLEBOOK_BRAND_COLORS_SECTION
				);

				await waitForInvalidTokenMessage(page, INVALID_TOKEN_MESSAGE);
			});

			await test.step('Cancel the publish process', async () => {
				await styleBooksPage.cancelPublish();
			});

			await test.step('View the invalid color token is still shown in Brand Color 1 field', async () => {
				await waitForInvalidTokenMessage(page, INVALID_TOKEN_MESSAGE);
			});

			await test.step('Insert an valid color token in Brand Color 1 field then publish', async () => {
				await styleBooksPage.updateTokenInputColor(
					STYLEBOOK_BRAND_COLOR_1,
					STYLEBOOK_CATEGORY_VALUE,
					STYLEBOOK_BRAND_COLORS_SECTION
				);

				await styleBooksPage.waitForAutoSave();

				await styleBooksPage.publish();
			});

			await test.step('View the Brand Color 1 is new color token', async () => {
				await styleBooksPage.edit(STYLEBOOK_NAME);

				await styleBooksHelper.assertTokenInputValue({
					label: STYLEBOOK_BRAND_COLOR_1,
					section: STYLEBOOK_BRAND_COLORS_SECTION,
					value: STYLEBOOK_CATEGORY_VALUE,
				});
			});
		}
	);

	test(
		'Check that the user cannot refer two tokens mutually.',
		{tag: '@LPS-141568'},
		async ({page, styleBooksPage}) => {
			await test.step('Change the Brand Color 1 to Brand Color 2', async () => {
				await styleBooksPage.updateTokenInputColor(
					STYLEBOOK_BRAND_COLOR_2,
					STYLEBOOK_BRAND_COLOR_1,
					STYLEBOOK_BRAND_COLORS_SECTION
				);

				await styleBooksPage.waitForAutoSave();
			});

			await test.step('Assert that is not possible to refer Brand Color 2 to Brand Color 1', async () => {
				await styleBooksPage.updateTokenInputColor(
					STYLEBOOK_BRAND_COLOR_1,
					STYLEBOOK_BRAND_COLOR_2,
					STYLEBOOK_BRAND_COLORS_SECTION
				);

				await waitForInvalidTokenMessage(
					page,
					'Tokens cannot be mutually referenced.'
				);
			});
		}
	);

	test(
		'Check if the user could continue publish process in style book editor if link invalid color token. The tokens groups should be collapsed by default except the first one.',
		{tag: '@LPS-145650'},
		async ({page, styleBooksHelper, styleBooksPage}) => {
			async function assertSectionIsExpanded(
				section: string,
				expanded: boolean = true
			) {
				expect(
					page
						.locator('.panel')
						.getByRole('button', {expanded, name: section})
				).toBeDefined();
			}

			await test.step('View only the first tokens group is expanded by default', async () => {
				await assertSectionIsExpanded(STYLEBOOK_BRAND_COLORS_SECTION);
				await assertSectionIsExpanded('Grays', false);
				await assertSectionIsExpanded('Theme Colors', false);
			});

			await test.step('Change the Brand Color 1 to an invalid token', async () => {
				await styleBooksPage.updateTokenInputColor(
					STYLEBOOK_BRAND_COLOR_1,
					STYLEBOOK_BRAND_COLOR_1,
					STYLEBOOK_BRAND_COLORS_SECTION
				);

				await waitForInvalidTokenMessage(page, INVALID_TOKEN_MESSAGE);
			});

			await test.step('Continue the publish process', async () => {
				await styleBooksPage.continuePublish();
			});

			await test.step('View the Brand Color 1 is new color token', async () => {
				await styleBooksPage.edit(STYLEBOOK_NAME);

				await styleBooksHelper.assertTokenInputValue({
					label: STYLEBOOK_BRAND_COLOR_1,
					section: STYLEBOOK_BRAND_COLORS_SECTION,
					value: '0B5FFF',
				});
			});
		}
	);

	test(
		'Assert that the token changes from style book should be applied to the usages.',
		{tag: '@LPS-136199'},
		async ({pageEditorPage, pagesAdminPage, site, styleBooksPage}) => {
			const pageName = getRandomString();

			await test.step('Create a content page and add a heading', async () => {
				await createPageAndAddContainer({
					friendlyUrlPath: site.friendlyUrlPath,
					pageEditorPage,
					pageName,
					pagesAdminPage,
				});
			});

			await test.step('Change the background color to success', async () => {
				await pageEditorPage.changeFragmentConfiguration({
					fieldLabel: 'Background Color',
					fragmentId: await pageEditorPage.getFragmentId('Container'),
					tab: 'Styles',
					value: 'Success',
					valueFromStylebook: true,
				});
			});

			await test.step('Assert that the color reference is shown', async () => {
				await expect(
					pageEditorPage.getFragment(
						await pageEditorPage.getFragmentId('Container')
					)
				).toHaveCSS('background-color', 'rgb(40, 125, 60)');
			});

			await test.step('Change Success to a custom token and publish', async () => {
				await styleBooksPage.goto(site.friendlyUrlPath);

				await styleBooksPage.edit(STYLEBOOK_NAME);

				await styleBooksPage.updateTokenInput(
					'Success',
					'34f787',
					'Theme Colors'
				);

				await styleBooksPage.waitForAutoSave();

				await styleBooksPage.publish();
			});

			await test.step('Apply the new style book to the page and assert that the custom token is being applied', async () => {
				await pagesAdminPage.goto(site.friendlyUrlPath);

				await pagesAdminPage.editPage(pageName);

				await pageEditorPage.selectStyleBook(STYLEBOOK_NAME);

				await expect(
					pageEditorPage.getFragment(
						await pageEditorPage.getFragmentId('Container')
					)
				).toHaveCSS('background-color', 'rgb(52, 247, 135)');
			});
		}
	);
});
