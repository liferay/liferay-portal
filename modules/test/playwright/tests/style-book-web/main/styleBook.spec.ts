/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {masterPagesPagesTest} from '../../../fixtures/masterPagesPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {styleBookPageTest} from '../../../fixtures/styleBookPageTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {PagesAdminPage} from '../../../pages/layout-admin-web/PagesAdminPage';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import {MasterPagesPage} from '../../../pages/layout-page-template-admin-web/MasterPagesPage';
import {StyleBooksPage} from '../../../pages/style-book-web/StyleBooksPage';
import getRandomString from '../../../utils/getRandomString';
import {reloadUntilVisible} from '../../../utils/reloadUntilVisible';
import {enableLocalStaging} from '../../../utils/staging';
import {portletPublishToLivePageTest} from '../../staging-configuration-web/main/fixtures/portletPublishToLivePageTest';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	masterPagesPagesTest,
	pageEditorPagesTest,
	pagesAdminPagesTest,
	styleBookPageTest
);

test('Assert that the style books in the page editor are based on the applied theme', async ({
	page,
	pageEditorPage,
	pagesAdminPage,
	site,
	styleBooksPage,
}) => {
	const styleBookName = getRandomString();

	await test.step('Create a style book', async () => {
		await styleBooksPage.goto(site.friendlyUrlPath);

		await styleBooksPage.create(styleBookName, 'Classic Theme');

		await styleBooksPage.publish();
	});

	const pageName = getRandomString();

	await test.step('Create a content page and assert the Classic style book is applied', async () => {
		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.createNewPage({
			draft: true,
			name: pageName,
		});

		await pageEditorPage.goToSidebarTab('Page Design Options');

		await pageEditorPage.goToConfigurationTab('Style Book');

		await expect(page.getByText('Styles from Classic Theme')).toBeVisible();

		await expect(page.getByText(styleBookName)).toBeVisible();
	});

	await test.step('Apply the CMS theme and assert the CMS style book is applied', async () => {
		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.goToDesignTabConfiguration(pageName);

		await pagesAdminPage.changeTheme('CMS');

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.editPage(pageName);

		await pageEditorPage.goToSidebarTab('Page Design Options');

		await pageEditorPage.goToConfigurationTab('Style Book');

		await expect(page.getByText('Styles from CMS Theme')).toBeVisible();
		await expect(page.getByText(styleBookName)).toBeHidden();
	});
});

test('Assert that the style books in the page editor are based on the applied theme CSS client extension', async ({
	page,
	pageEditorPage,
	pagesAdminPage,
	site,
}) => {
	const pageName = getRandomString();

	await test.step('Create a content page and apply the "Liferay Sample Theme CSS 3" client extension', async () => {
		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.createNewPage({
			draft: true,
			name: pageName,
		});

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.goToDesignTabConfiguration(pageName);

		await pagesAdminPage.selectThemeCSSClientExtension(
			'Liferay Sample Theme CSS 3'
		);
	});

	await test.step('Assert that the style books not supported message is visible', async () => {
		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.editPage(pageName);

		await pageEditorPage.goToSidebarTab('Page Design Options');

		await pageEditorPage.goToConfigurationTab('Style Book');

		await expect(
			page.getByText(
				'The current theme does not support style books. To use this feature, you must change the selected theme.'
			)
		).toBeVisible();
	});

	await test.step('Apply the "Liferay Sample Theme CSS 4" client extension to a content page', async () => {
		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.goToDesignTabConfiguration(pageName);

		await pagesAdminPage.selectThemeCSSClientExtension(
			'Liferay Sample Theme CSS 4'
		);
	});

	await test.step('Assert that the applied style book is the one provided by "Liferay Sample Theme CSS 4" client extension', async () => {
		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.editPage(pageName);

		await pageEditorPage.goToSidebarTab('Page Design Options');

		await pageEditorPage.goToConfigurationTab('Style Book');

		await expect(
			page.getByText(
				'Styles from Liferay Sample Theme CSS 4 Theme CSS Client Extension'
			)
		).toBeVisible();
	});
});

test.describe('Style books applied to master pages', async () => {
	async function setUp({
		apiHelpers,
		markStyleBookAsDefault,
		masterPagesPage,
		pageEditorPage,
		pagesAdminPage,
		selectStyleBookForMasterPage,
		site,
		styleBooksPage,
	}: {
		apiHelpers: ApiHelpers;
		markStyleBookAsDefault?: boolean;
		masterPagesPage: MasterPagesPage;
		page: Page;
		pageEditorPage: PageEditorPage;
		pagesAdminPage: PagesAdminPage;
		selectStyleBookForMasterPage?: boolean;
		site: Site;
		styleBooksPage: StyleBooksPage;
	}) {
		const styleBookName = getRandomString();

		await styleBooksPage.goto(site.friendlyUrlPath);

		await styleBooksPage.create(styleBookName, 'Classic Theme');

		await styleBooksPage.publish();

		if (markStyleBookAsDefault) {
			await styleBooksPage.markAsDefault(styleBookName);
		}

		const masterPageName = getRandomString();

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
			{
				groupId: site.id,
				name: masterPageName,
				type: 'master-layout',
			}
		);

		if (selectStyleBookForMasterPage) {
			await masterPagesPage.goto(site.friendlyUrlPath);

			await masterPagesPage.editMaster(masterPageName);

			await pageEditorPage.selectStyleBook(styleBookName);

			await pageEditorPage.publishPage();
		}

		const pageName = getRandomString();

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.createNewPage({
			draft: true,
			name: pageName,
			template: masterPageName,
		});
	}

	test('Without selected style book and without default style book for the theme', async ({
		apiHelpers,
		masterPagesPage,
		page,
		pageEditorPage,
		pagesAdminPage,
		site,
		styleBooksPage,
	}) => {
		await setUp({
			apiHelpers,
			markStyleBookAsDefault: false,
			masterPagesPage,
			page,
			pageEditorPage,
			pagesAdminPage,
			selectStyleBookForMasterPage: false,
			site,
			styleBooksPage,
		});

		await test.step('Assert that the style book "styles from classic theme" is applied to the child page', async () => {
			await pageEditorPage.goToSidebarTab('Page Design Options');

			await pageEditorPage.goToConfigurationTab('Style Book');

			await expect(
				page.getByText('Styles from Classic Theme')
			).toBeVisible();
		});
	});

	test('With selected style book and without default style book for the theme', async ({
		apiHelpers,
		masterPagesPage,
		page,
		pageEditorPage,
		pagesAdminPage,
		site,
		styleBooksPage,
	}) => {
		await setUp({
			apiHelpers,
			markStyleBookAsDefault: false,
			masterPagesPage,
			page,
			pageEditorPage,
			pagesAdminPage,
			selectStyleBookForMasterPage: true,
			site,
			styleBooksPage,
		});

		await test.step('Assert that the selected style book from master is applied to the child page', async () => {
			await pageEditorPage.goToSidebarTab('Page Design Options');

			await pageEditorPage.goToConfigurationTab('Style Book');

			await expect(page.getByText('Styles from master')).toBeVisible();
		});
	});

	test('Without selected style book and with default style book for the theme', async ({
		apiHelpers,
		masterPagesPage,
		page,
		pageEditorPage,
		pagesAdminPage,
		site,
		styleBooksPage,
	}) => {
		await setUp({
			apiHelpers,
			markStyleBookAsDefault: true,
			masterPagesPage,
			page,
			pageEditorPage,
			pagesAdminPage,
			selectStyleBookForMasterPage: false,
			site,
			styleBooksPage,
		});

		await test.step('Assert that the selected style book from master is applied to the child page', async () => {
			await pageEditorPage.goToSidebarTab('Page Design Options');

			await pageEditorPage.goToConfigurationTab('Style Book');

			await expect(page.getByText('Styles by default')).toBeVisible();
		});
	});
});

test('Style book is incompatible with the applied theme', async ({
	page,
	pageEditorPage,
	pagesAdminPage,
	site,
	styleBooksPage,
}) => {
	const classicStyleBookName = getRandomString();

	await test.step('Create a style book for Classic theme', async () => {
		await styleBooksPage.goto(site.friendlyUrlPath);

		await styleBooksPage.create(classicStyleBookName, 'Classic Theme');

		await styleBooksPage.publish();
	});

	const cmsStyleBookName = getRandomString();

	await test.step('Create a style book for CMS theme and mark it as default', async () => {
		await styleBooksPage.goto(site.friendlyUrlPath);

		await styleBooksPage.create(cmsStyleBookName, 'CMS Theme');

		await styleBooksPage.publish();

		await styleBooksPage.markAsDefault(cmsStyleBookName);
	});

	const pageName = getRandomString();

	await test.step('Create a content page and apply the new style book', async () => {
		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.createNewPage({
			draft: true,
			name: pageName,
		});

		await pageEditorPage.selectStyleBook(classicStyleBookName);

		await pageEditorPage.publishPage();
	});

	await test.step('Change the theme to CMS', async () => {
		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.goToDesignTabConfiguration(pageName);

		await pagesAdminPage.changeTheme('CMS');
	});

	await test.step('Assert that the applied style book is the default one from the CMS theme', async () => {
		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.editPage(pageName);

		await pageEditorPage.goToSidebarTab('Page Design Options');

		await pageEditorPage.goToConfigurationTab('Style Book');

		await page
			.locator(
				`.page-editor__sidebar__design-options__tab-card--active:has-text("${cmsStyleBookName}"):has-text("Styles by Default")`
			)
			.waitFor();
	});
});

const stagingTest = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	masterPagesPagesTest,
	pageEditorPagesTest,
	pagesAdminPagesTest,
	portletPublishToLivePageTest,
	styleBookPageTest
);

stagingTest(
	'Assert that style books work when staging is enabled',
	async ({
		apiHelpers,
		masterPagesPage,
		page,
		pageEditorPage,
		pagesAdminPage,
		portletPublishToLivePage,
		site,
		styleBooksPage,
	}) => {
		async function createStyleBook(color: string, styleBookName: string) {
			await test.step('Create style book while live site is selected and edit Brand Color 1 token', async () => {
				await styleBooksPage.goto(site.friendlyUrlPath);

				await styleBooksPage.create(styleBookName, 'Classic Theme');

				await styleBooksPage.updateTokenInputColor(
					'Brand Color 1',
					color,
					'Brand Colors'
				);

				await styleBooksPage.waitForAutoSave();

				await styleBooksPage.publish();
			});
		}

		const stagingSite = await stagingTest.step(
			'Enable local staging',
			async () => {
				const layout = await apiHelpers.jsonWebServicesLayout.addLayout(
					{
						groupId: site.id,
						options: {type: 'content'},
						title: getRandomString(),
					}
				);

				await enableLocalStaging(apiHelpers, page, site);

				const stagingSite =
					await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
						`${site.friendlyUrlPath}-staging`
					);

				await page.goto(
					`/web${stagingSite.friendlyUrlPath}${layout.friendlyURL}`
				);

				await reloadUntilVisible({
					myLocator: portletPublishToLivePage.publishToLiveButton,
					page,
				});

				await portletPublishToLivePage.publishToLiveButton.click();

				return stagingSite;
			}
		);

		const styleBookName1 = getRandomString();

		await createStyleBook('#FF0000', styleBookName1);

		const styleBookName2 = getRandomString();

		await createStyleBook('#00ccffff', styleBookName2);

		const masterPageName = getRandomString();

		await stagingTest.step(
			'Create a master page and assign Brand Color 1 token to a Heading fragment',
			async () => {
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
					{
						groupId: stagingSite.id,
						name: masterPageName,
						type: 'master-layout',
					}
				);

				await masterPagesPage.goto(stagingSite.friendlyUrlPath);

				await masterPagesPage.editMaster(masterPageName);

				await pageEditorPage.addFragment('Basic Components', 'Heading');

				await pageEditorPage.goToConfigurationTab('Styles');

				await page
					.getByLabel('Text Color', {exact: true})
					.getByLabel('Detach Style')
					.click();

				await pageEditorPage.changeFragmentConfiguration({
					fieldLabel: 'Text Color',
					fragmentId: await pageEditorPage.getFragmentId('Heading'),
					tab: 'Styles',
					value: 'Brand Color 1',
					valueFromStylebook: true,
				});
			}
		);

		const styleBook1BrandColor1 = 'rgb(255, 0, 0)';

		await stagingTest.step('Assert Heading fragment color', async () => {
			await expect(page.locator('.component-heading')).toHaveCSS(
				'color',
				'rgb(11, 95, 255)'
			);

			await pageEditorPage.selectStyleBook(styleBookName1);

			await expect(page.locator('.component-heading')).toHaveCSS(
				'color',
				styleBook1BrandColor1
			);

			await pageEditorPage.publishMasterButton.click();
		});

		await test.step('Create a child page based on master page', async () => {
			const pageName = getRandomString();

			await pagesAdminPage.goto(stagingSite.friendlyUrlPath);

			await pagesAdminPage.createNewPage({
				draft: true,
				name: pageName,
				template: masterPageName,
			});
		});

		await test.step('Assert that a child page can inherit the master page style book selection', async () => {
			await pageEditorPage.goToSidebarTab('Page Design Options');

			await pageEditorPage.goToConfigurationTab('Style Book');

			await expect(page.getByText('Styles from master')).toBeVisible();

			await expect(page.locator('.component-heading')).toHaveCSS(
				'color',
				styleBook1BrandColor1
			);
		});

		await test.step('Assert that a child page can override the master page style book selection', async () => {
			await pageEditorPage.selectStyleBook(styleBookName2);

			await expect(page.locator('.component-heading')).toHaveCSS(
				'color',
				'rgb(0, 204, 255)'
			);
		});
	}
);
