/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {masterPagesPagesTest} from '../../../fixtures/masterPagesPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {styleBookPageTest} from '../../../fixtures/styleBookPageTest';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import {StyleBooksPage} from '../../../pages/style-book-web/StyleBooksPage';
import getRandomString from '../../../utils/getRandomString';
import {
	disableSystemFeatureFlag,
	enableSystemFeatureFlag,
} from '../../../utils/systemFeatureFlag';

const test = mergeTests(
	apiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	masterPagesPagesTest,
	pageEditorPagesTest,
	pagesAdminPagesTest,
	productMenuPageTest,
	styleBookPageTest
);

test('Checks the correct label for restricted pages in the preview selector', async ({
	apiHelpers,
	page,
	site,
	styleBooksPage,
}) => {

	// Create a content page with only one permission

	const pageName = getRandomString();

	await apiHelpers.headlessDelivery.createSitePage({
		pagePermissions: [
			{
				actionKeys: ['VIEW'],
				roleKey: 'Owner',
			},
		],
		siteId: site.id,
		title: pageName,
	});

	// Create a stylebook and edit it

	const styleBookName = getRandomString();

	await styleBooksPage.goto(site.friendlyUrlPath);

	await styleBooksPage.create(styleBookName);

	// Check the restricted page label in the preview selector

	await page.getByRole('button', {name: pageName}).click();

	await expect(
		page.getByRole('menuitem', {name: `${pageName} Restricted Page`})
	).toBeVisible();
});

test(
	'Preview StyleBook when edit StyleBook',
	{tag: '@LPD-35561'},
	async ({
		page,
		pageEditorPage,
		pagesAdminPage,
		productMenuPage,
		site,
		styleBooksPage,
	}) => {
		await test.step('Enable feature flag', async () => {
			await enableSystemFeatureFlag({
				page,
				title: 'Featured Content Fragment Set',
				type: 'Deprecation',
			});
		});

		await test.step('Add a content page', async () => {
			await styleBooksPage.goto(site.friendlyUrlPath);

			await productMenuPage.goToPages();

			await pagesAdminPage.createNewPage({
				draft: true,
				name: 'Test Page Name',
				template: 'Blank',
			});
		});

		await test.step('Add a Banner Center to page', async () => {
			await pageEditorPage.goToSidebarTab('Components');

			await pageEditorPage.addFragment(
				'Featured Content Deprecated',
				'Banner Center',
				page.locator('div.page-editor__root')
			);
		});

		await test.step('Change the background color of Paragraph and publish the page', async () => {
			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Background Color',
				fragmentId: await pageEditorPage.getFragmentId('Paragraph'),
				tab: 'Styles',
				value: 'Danger',
				valueFromStylebook: true,
			});

			await pageEditorPage.publishPage();
		});

		const styleBookName = getRandomString();

		await test.step('Add a style book', async () => {
			await styleBooksPage.goto(site.friendlyUrlPath);

			await styleBooksPage.create(styleBookName);
		});

		await test.step('Assert the content page is shown in preview iframe', async () => {
			const previewIframe = page.frameLocator(
				'iframe.style-book-editor__page-preview-frame'
			);

			expect(
				previewIframe.getByRole('heading', {
					name: 'Banner Title Example',
				})
			).toBeVisible();
		});

		await test.step('Edit Background Color in Button Primary section', async () => {
			await styleBooksPage.selectTokenCategory('Buttons');

			await styleBooksPage.updateTokenInputColor(
				'Background Color',
				'#FF0000',
				'Button Primary'
			);

			await styleBooksPage.waitForAutoSave();
		});

		await test.step('Select Typography in sidebar', async () => {
			await styleBooksPage.selectTokenCategory('Typography');
		});

		await test.step('Edit Heading 1 Font Size in Headings section', async () => {
			await styleBooksPage.updateTokenInput(
				'Heading 1 Font Size',
				'2',
				'Headings'
			);

			await styleBooksPage.waitForAutoSave();
		});

		await test.step('Select Color System in sidebar', async () => {
			await styleBooksPage.selectTokenCategory('Color System');
		});

		await test.step('Edit the Danger in Theme Colors section', async () => {
			await styleBooksPage.updateTokenInputColor(
				'Brand Color 1',
				'danger',
				'Brand Colors'
			);

			await styleBooksPage.waitForAutoSave();
		});

		await test.step('Preview the effect in page preview iframe', async () => {
			const previewIframe = page.frameLocator(
				'iframe.style-book-editor__page-preview-frame'
			);

			await expect(
				previewIframe.locator(
					'.lfr-layout-structure-item-basic-component-button .btn-primary'
				)
			).toHaveCSS('background-color', 'rgb(255, 0, 0)');

			await expect(
				previewIframe
					.locator(
						'.lfr-layout-structure-item-basic-component-heading'
					)
					.getByText('Banner Title Example')
			).toHaveCSS('font-size', '32px');

			await expect(
				previewIframe.locator(
					'.lfr-layout-structure-item-basic-component-paragraph'
				)
			).toHaveCSS('background-color', 'rgb(218, 20, 20)');

			await styleBooksPage.publish();
		});

		await test.step('Assert the new style book in Style Books admin', async () => {
			await expect(
				page.getByRole('link', {name: styleBookName})
			).toBeVisible();
		});

		await test.step('Disable feature flag', async () => {
			await disableSystemFeatureFlag({
				page,
				title: 'Featured Content Fragment Set',
				type: 'Deprecation',
			});
		});
	}
);

test(
	'Preview style book on pages',
	{tag: '@LPD-35560'},
	async ({
		page,
		pageEditorPage,
		pagesAdminPage,
		productMenuPage,
		site,
		styleBooksPage,
	}) => {
		const pageName = getRandomString();

		await test.step('Add a content page with a paragraph and a button', async () => {
			await styleBooksPage.goto(site.friendlyUrlPath);

			await productMenuPage.goToPages();

			await pagesAdminPage.createNewPage({
				draft: true,
				name: pageName,
				template: 'Blank',
			});

			await pageEditorPage.addFragment('Basic Components', 'Paragraph');

			await pageEditorPage.addFragment('Basic Components', 'Button');

			await pageEditorPage.publishPage();
		});

		await test.step('Add a style book', async () => {
			await styleBooksPage.goto(site.friendlyUrlPath);

			await styleBooksPage.create(getRandomString());
		});

		await test.step('Change Body Color in the General frontend token category', async () => {
			await styleBooksPage.selectTokenCategory('General');

			await styleBooksPage.updateTokenInputColor(
				'Body Color',
				'#227777',
				'Body'
			);

			await styleBooksPage.waitForAutoSave();
		});

		await test.step('Change color of Button Primary in the Buttons frontend token category', async () => {
			await styleBooksPage.selectTokenCategory('Buttons');

			await styleBooksPage.updateTokenInputColor(
				'Color',
				'#880022',
				'Button Primary'
			);

			await styleBooksPage.waitForAutoSave();
		});

		await test.step('Change Font Family Base in the Typography frontend token category', async () => {
			await styleBooksPage.selectTokenCategory('Typography');

			await styleBooksPage.updateTokenInput(
				'Font Family Base',
				'times',
				'Font Family'
			);

			await styleBooksPage.waitForAutoSave();
		});

		const previewIframe = page.frameLocator(
			'iframe.style-book-editor__page-preview-frame'
		);

		await test.step('Preview body color changes', async () => {
			await expect(previewIframe.locator('body')).toHaveCSS(
				'color',
				'rgb(34, 119, 119)'
			);
		});

		await test.step('Preview button color changes', async () => {
			await expect(previewIframe.locator('.btn-primary')).toHaveCSS(
				'color',
				'rgb(136, 0, 34)'
			);
		});

		await test.step('Preview typography changes', async () => {
			await expect(
				previewIframe.getByRole('menuitem', {name: pageName})
			).toHaveCSS('font-family', 'times');
		});
	}
);

test.describe('Cannot preview style book', () => {
	async function addHeadingAndPublishChanges(pageEditorPage: PageEditorPage) {
		await test.step('Add a default heading component and publish the changes', async () => {
			await pageEditorPage.addFragment('Basic Components', 'Heading');

			await pageEditorPage.publishPage();
		});
	}

	async function addStyleBook(site: Site, styleBooksPage: StyleBooksPage) {
		await test.step('Add a style book', async () => {
			await styleBooksPage.goto(site.friendlyUrlPath);

			await styleBooksPage.create(getRandomString());
		});
	}

	async function previewAndAssertDefaultHeading(
		invalidPreviewTypes: string[],
		page: Page,
		previewType: string
	) {
		await page.getByRole('button', {name: previewType}).click();

		expect(page.getByRole('menuitem', {name: 'Fragments'})).toBeVisible();

		for (const invalidPreviewType of invalidPreviewTypes) {
			expect(
				page.getByRole('menuitem', {name: invalidPreviewType})
			).not.toBeVisible();
		}

		await page.getByRole('menuitem', {name: previewType}).click();

		const previewIframe = page.frameLocator(
			'iframe.style-book-editor__page-preview-frame'
		);

		const heading = previewIframe
			.getByRole('heading', {name: 'Heading Example'})
			.first();

		await heading.waitFor();
	}

	async function updateHeadingContentWithoutPublish(
		pageEditorPage: PageEditorPage
	) {
		await test.step('Update the heading content but does not publish the changes', async () => {
			await pageEditorPage.editTextEditable(
				await pageEditorPage.getFragmentId('Heading'),
				'element-text',
				getRandomString()
			);

			await pageEditorPage.waitForChangesSaved();
		});
	}

	test('On draft master pages', async ({
		apiHelpers,
		masterPagesPage,
		page,
		pageEditorPage,
		site,
		styleBooksPage,
	}) => {
		const name = getRandomString();

		await test.step('Add Heading fragment to draft master page', async () => {
			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
				{
					groupId: site.id,
					name,
					type: 'master-layout',
				}
			);

			await masterPagesPage.goto(site.friendlyUrlPath);
		});

		await masterPagesPage.editMaster(name);

		await addHeadingAndPublishChanges(pageEditorPage);

		await masterPagesPage.editMaster(name);

		await updateHeadingContentWithoutPublish(pageEditorPage);

		await addStyleBook(site, styleBooksPage);

		await test.step("Assert only 'Fragments' and 'Masters' are available", async () => {
			await previewAndAssertDefaultHeading(
				['Display Page Templates', 'Pages', 'Page Templates'],
				page,
				'Masters'
			);
		});
	});

	test('On draft content pages', async ({
		page,
		pageEditorPage,
		pagesAdminPage,
		site,
		styleBooksPage,
	}) => {
		const name = getRandomString();

		await test.step('Add Heading fragment to draft content page', async () => {
			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pagesAdminPage.createNewPage({
				draft: true,
				name,
				template: 'Blank',
			});
		});

		await addHeadingAndPublishChanges(pageEditorPage);

		await pagesAdminPage.editPage(name);

		await updateHeadingContentWithoutPublish(pageEditorPage);

		await addStyleBook(site, styleBooksPage);

		await test.step("Assert that only 'Fragments' and 'Pages' are available", async () => {
			await previewAndAssertDefaultHeading(
				['Display Page Templates', 'Masters', 'Page Templates'],
				page,
				'Pages'
			);
		});
	});

	test('On draft display page templates', async ({
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
		styleBooksPage,
	}) => {
		const pageName = getRandomString();

		await test.step('Create a display page template', async () => {
			await displayPageTemplatesPage.goto(site.friendlyUrlPath);

			await displayPageTemplatesPage.createTemplate({
				contentSubtype: 'Basic Document',
				contentType: 'Document',
				name: pageName,
			});
		});

		await displayPageTemplatesPage.editTemplate(pageName);

		await test.step('Add a default heading component and publish the changes', async () => {
			await pageEditorPage.addFragment('Basic Components', 'Heading');

			await displayPageTemplatesPage.publishTemplate();
		});

		await displayPageTemplatesPage.editTemplate(pageName);

		await updateHeadingContentWithoutPublish(pageEditorPage);

		await addStyleBook(site, styleBooksPage);

		await test.step("Assert that only 'Display Page Templates' and 'Fragments' are available", async () => {
			await previewAndAssertDefaultHeading(
				['Masters', 'Pages', 'Page Templates'],
				page,
				'Display Page Templates'
			);
		});
	});
});

const themeScopedTest = mergeTests(
	featureFlagsTest({
		'LPD-30204': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	styleBookPageTest
);

themeScopedTest(
	'Fragment collection preview applies the theme in which the style book is based on',
	async ({page, site, styleBooksPage}) => {
		await test.step('Create a style book based on the Dialect theme', async () => {
			await styleBooksPage.goto(site.friendlyUrlPath);

			await styleBooksPage.create('New style book', 'Dialect Theme');
		});

		await test.step("Assert that the tokens applied to the preview page of the 'Basic Components' fragment collection are from the dialect theme", async () => {
			await styleBooksPage.previewFragmentCollection('Basic Components');

			const previewIframe = page.frameLocator(
				'iframe.style-book-editor__page-preview-frame'
			);

			const firstButton = previewIframe
				.getByRole('link', {
					name: 'Go Somewhere',
				})
				.first();

			await firstButton.waitFor();

			expect(firstButton).toHaveCSS(
				'background-color',
				'rgb(89, 36, 235)'
			);
		});
	}
);
