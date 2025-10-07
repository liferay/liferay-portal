/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {masterPagesPagesTest} from '../../../fixtures/masterPagesPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageSelectorPagesTest} from '../../../fixtures/pageSelectorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {systemSettingsPageTest} from '../../../fixtures/systemSettingsPageTest';
import {liferayConfig} from '../../../liferay.config';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';
import {selectAndExpectToHaveValue} from '../../../utils/selectAndExpectToHaveValue';
import {waitForAlert} from '../../../utils/waitForAlert';
import {pagesPagesTest} from './fixtures/pagesPagesTest';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	masterPagesPagesTest,
	pageEditorPagesTest,
	pageSelectorPagesTest,
	pagesAdminPagesTest,
	pagesPagesTest,
	systemSettingsPageTest
);

const deleteClientExtension = async (apiHelpers, clientExtension) => {
	const {clientExtensionEntryId} =
		await apiHelpers.jsonWebServicesClientExtension.deleteClientExtension(
			clientExtension.clientExtensionEntryId
		);

	expect(clientExtensionEntryId).not.toBeNull();
};

test.describe('General configuration', () => {
	test('Checks the accessibility of the General page configuration', async ({
		page,
	}) => {
		await page.goto('/');

		await page.getByLabel('Configure Page', {exact: true}).click();

		await expect(page).toHaveURL(/edit_layout/);

		await checkAccessibility({
			page,
			selectors: ['.input-container[aria-label="General"]'],
		});
	});

	test('Can edit the page name and layout template via pages administration', async ({
		apiHelpers,
		page,
		pageConfigurationPage,
		pagesAdminPage,
		site,
	}) => {

		// Create page and go to page configuration

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: 'Test Page Title',
		});

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pageConfigurationPage.goToSection('Test Page Title', 'General');

		// Fill name and change layout to 1 column

		await pageConfigurationPage.fillName('Test Page Title Edit');

		await page.getByTitle('1 Column', {exact: true}).click();

		// Check card is selected and save

		const card = page.locator('.card.card-interactive').first();

		await expect(card).toHaveClass(/active/);

		await pageConfigurationPage.save();

		// Go to view mode of page and check layout

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await expect(
			page.getByRole('heading', {name: 'Test Page Title Edit'})
		).toBeVisible();

		await expect(page.locator('#layout-column_column-1')).toBeAttached();
	});

	test('Can not select pages from other sites for Link to a Page', async ({
		apiHelpers,
		page,
		pageConfigurationPage,
		pageSelectorPage,
		pagesAdminPage,
		site,
	}) => {

		// Create a widget page and a link to layout page

		const name = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {
				type: 'link_to_layout',
			},
			title: name,
		});

		// Try to select linked page and check Sites and Libraries
		// section is not shown

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pageConfigurationPage.goToSection(name, 'General');

		await clickAndExpectToBeVisible({
			target: page.locator('.modal-dialog'),
			trigger: page
				.locator('.layout-type')
				.getByRole('button', {name: 'Select'}),
		});

		const modal = await pageSelectorPage.getModal();
		await modal.locator('.treeview').waitFor();

		await expect(modal.getByText('Sites and Libraries')).not.toBeVisible();
	});

	test(
		'Custom CSS should be hide if a master page is selected',
		{
			tag: '@LPD-41497',
		},
		async ({
			apiHelpers,
			page,
			pageConfigurationPage,
			pagesAdminPage,
			site,
		}) => {

			// Add master page

			const layoutPageTemplateEntryName = getRandomString();

			const masterPage =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
					{
						groupId: site.id,
						name: layoutPageTemplateEntryName,
						type: 'master-layout',
					}
				);

			// Create a layout

			const layoutTitle = getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				masterLayoutPlid: masterPage.plid,
				title: layoutTitle,
			});

			// Assert custom css is hide

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pageConfigurationPage.goToSection(layoutTitle, 'Design');

			await expect(
				page.getByRole('textbox', {exact: true, name: 'CSS'})
			).not.toBeVisible();

			// Change master to blank and assert custom css is show

			await pageConfigurationPage.selectMasterLayout('Blank');

			await expect(page.getByLabel('Master', {exact: true})).toHaveValue(
				'Blank'
			);

			await expect(
				page.getByRole('textbox', {exact: true, name: 'CSS'})
			).toBeVisible();
		}
	);

	test(
		'Layout type is escaped',
		{
			tag: '@LPD-54782',
		},
		async ({
			apiHelpers,
			page,
			pageConfigurationPage,
			pagesAdminPage,
			site,
		}) => {

			// Create a layout

			const title = getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				options: {
					type: 'contenttho8o<script>alert(12345)</script>l421oja33oz',
				},
				title,
			});

			// Go to page configuration

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pageConfigurationPage.goToSection(title, 'General');

			// Verify no alert is shown

			page.on('dialog', async (dialog) => {
				await dialog.accept();

				expect(dialog.message(), '12345').toBeNull();
			});
		}
	);
});

test.describe('Design configuration', () => {
	test(
		'Add CSS extension to page',
		{
			tag: '@LPS-153656',
		},
		async ({apiHelpers, page, pagesAdminPage, site}) => {

			// Create a new CSS client extension with a script element attribute

			const clientExtensionName = getRandomString();

			const clientExtension =
				await apiHelpers.jsonWebServicesClientExtension.addClientExtension(
					{
						name: clientExtensionName,
						type: 'globalCSS',
						url: 'https://www.example.com/style.css',
					}
				);

			// Create a layout

			const layoutTitle = getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: layoutTitle,
			});

			// Apply CSS client extension to page

			await pagesAdminPage.selectClientExtension({
				clientExtensionName,
				layoutTitle,
				siteUrl: site.friendlyUrlPath,
				type: 'globalCSS',
			});

			// Check CSS is attached to the page in view mode

			await page.goto(`/web${site.friendlyUrlPath}/${layoutTitle}`);

			await expect(
				page.locator('link[href="https://www.example.com/style.css"]')
			).toBeAttached();

			// Clean up

			await deleteClientExtension(apiHelpers, clientExtension);
		}
	);

	test(
		'Inherited CSS extensions from master pages should be read-only mode',
		{
			tag: '@LPS-153656',
		},
		async ({
			apiHelpers,
			masterPagesPage,
			page,
			pageConfigurationPage,
			pagesAdminPage,
			site,
		}) => {

			// Create a new CSS client extension with a script element attribute

			const clientExtensionName = getRandomString();

			const clientExtension =
				await apiHelpers.jsonWebServicesClientExtension.addClientExtension(
					{
						name: clientExtensionName,
						type: 'globalCSS',
						url: 'https://www.example.com/style.css',
					}
				);

			// Add master page

			const layoutPageTemplateEntryName = getRandomString();

			const masterPage =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
					{
						groupId: site.id,
						name: layoutPageTemplateEntryName,
						type: 'master-layout',
					}
				);

			// Apply CSS client extension to master page

			await masterPagesPage.selectClientExtension({
				clientExtensionName,
				layoutTitle: layoutPageTemplateEntryName,
				siteUrl: site.friendlyUrlPath,
				type: 'globalCSS',
			});

			// Publish master page

			await masterPagesPage.goto(site.friendlyUrlPath);
			await masterPagesPage.publishMaster(layoutPageTemplateEntryName);

			// Create a layout

			const layoutTitle = getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				masterLayoutPlid: masterPage.plid,
				title: layoutTitle,
			});

			// Check inherited CSS client extension is read-only

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pageConfigurationPage.goToSection(layoutTitle, 'Design');

			await expect(
				page.locator('.global-css-cets-configuration tr', {
					has: page.getByText('From Master'),
				})
			).toHaveClass(/disabled/);

			// Clean up

			await deleteClientExtension(apiHelpers, clientExtension);
		}
	);

	test(
		'Inherited CSS extensions from pages should be read-only mode',
		{
			tag: '@LPS-153658',
		},
		async ({
			apiHelpers,
			page,
			pageConfigurationPage,
			pagesAdminPage,
			site,
		}) => {

			// Create a new CSS client extension with a style element attribute

			const clientExtensionName = getRandomString();

			const clientExtension =
				await apiHelpers.jsonWebServicesClientExtension.addClientExtension(
					{
						name: clientExtensionName,
						type: 'globalCSS',
						url: 'https://www.example.com/style.css',
					}
				);

			// Apply CSS client extension to all pages

			await pagesAdminPage.selectClientExtension({
				clientExtensionName,
				siteUrl: site.friendlyUrlPath,
				type: 'globalCSS',
			});

			// Create a layout

			const layoutTitle = getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: layoutTitle,
			});

			// Check inherited CSS client extension is read-only

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pageConfigurationPage.goToSection(layoutTitle, 'Design');

			await expect(
				page.locator('.global-css-cets-configuration tr', {
					has: page.getByText('From Pages'),
				})
			).toHaveClass(/disabled/);

			// Clean up

			await deleteClientExtension(apiHelpers, clientExtension);
		}
	);

	test(
		'Add JS extension to page',
		{
			tag: '@LPS-153658',
		},
		async ({apiHelpers, page, pagesAdminPage, site}) => {

			// Create a new JS client extension with a script element attribute

			const clientExtensionName = getRandomString();

			const clientExtension =
				await apiHelpers.jsonWebServicesClientExtension.addClientExtension(
					{
						name: clientExtensionName,
						type: 'globalJS',
						url: 'https://www.example.com/script.js',
					}
				);

			// Create a layout

			const layoutTitle = getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: layoutTitle,
			});

			// Apply JS client extension to page

			await pagesAdminPage.selectClientExtension({
				clientExtensionName,
				layoutTitle,
				siteUrl: site.friendlyUrlPath,
				type: 'globalJS',
			});

			// Check JS is attached to the page in view mode

			await page.goto(`/web${site.friendlyUrlPath}/${layoutTitle}`);

			await expect(
				page.locator(`script[src="https://www.example.com/script.js"]`)
			).toBeAttached();

			// Clean up

			await deleteClientExtension(apiHelpers, clientExtension);
		}
	);

	test(
		'Inherited JS extensions from master pages should be read-only mode',
		{
			tag: '@LPS-153658',
		},
		async ({
			apiHelpers,
			masterPagesPage,
			page,
			pageConfigurationPage,
			pagesAdminPage,
			site,
		}) => {

			// Create a new JS client extension with a script element attribute

			const clientExtensionName = getRandomString();

			const clientExtension =
				await apiHelpers.jsonWebServicesClientExtension.addClientExtension(
					{
						name: clientExtensionName,
						type: 'globalJS',
						url: 'https://www.example.com/script.js',
					}
				);

			// Add master page

			const layoutPageTemplateEntryName = getRandomString();

			const masterPage =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
					{
						groupId: site.id,
						name: layoutPageTemplateEntryName,
						type: 'master-layout',
					}
				);

			// Apply JS client extension to master page

			await masterPagesPage.selectClientExtension({
				clientExtensionName,
				layoutTitle: layoutPageTemplateEntryName,
				siteUrl: site.friendlyUrlPath,
				type: 'globalJS',
			});

			// Publish master page

			await masterPagesPage.goto(site.friendlyUrlPath);
			await masterPagesPage.publishMaster(layoutPageTemplateEntryName);

			// Create a layout

			const layoutTitle = getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				masterLayoutPlid: masterPage.plid,
				title: layoutTitle,
			});

			// Check inherited JS client extension is read-only

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pageConfigurationPage.goToSection(layoutTitle, 'Design');

			await pagesAdminPage.clickOnJavaScriptClientExtensionsTab();

			await expect(
				page.locator('.global-js-cets-configuration tr', {
					has: page.getByText('From Master'),
				})
			).toHaveClass(/disabled/);

			// Clean up

			await deleteClientExtension(apiHelpers, clientExtension);
		}
	);

	test(
		'Inherited JS extensions from pages should be read-only mode',
		{
			tag: '@LPS-153658',
		},
		async ({
			apiHelpers,
			page,
			pageConfigurationPage,
			pagesAdminPage,
			site,
		}) => {

			// Create a new JS client extension with a script element attribute

			const clientExtensionName = getRandomString();

			const clientExtension =
				await apiHelpers.jsonWebServicesClientExtension.addClientExtension(
					{
						name: clientExtensionName,
						type: 'globalJS',
						url: 'https://www.example.com/script.js',
					}
				);

			// Apply JS client extension to all pages

			await pagesAdminPage.selectClientExtension({
				clientExtensionName,
				siteUrl: site.friendlyUrlPath,
				type: 'globalJS',
			});

			// Create a layout

			const layoutTitle = getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: layoutTitle,
			});

			// Check inherited JS client extension is read-only

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pageConfigurationPage.goToSection(layoutTitle, 'Design');

			await pagesAdminPage.clickOnJavaScriptClientExtensionsTab();

			await expect(
				page.locator('.global-js-cets-configuration tr', {
					has: page.getByText('From Pages'),
				})
			).toHaveClass(/disabled/);

			// Clean up

			await deleteClientExtension(apiHelpers, clientExtension);
		}
	);

	test(
		'Add theme favicon to page',
		{
			tag: ['@LPS-153654', '@LPS-153903'],
		},
		async ({apiHelpers, page, pagesAdminPage, site}) => {

			// Create a new theme favicon client extension

			const clientExtensionName = getRandomString();

			const clientExtension =
				await apiHelpers.jsonWebServicesClientExtension.addClientExtension(
					{
						name: clientExtensionName,
						type: 'themeFavicon',
						url: 'https://www.google.com/favicon.ico',
					}
				);

			// Create a layout

			const layoutTitle = getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: layoutTitle,
			});

			// Assert default theme favicon in view mode

			await page.goto(`/web${site.friendlyUrlPath}/${layoutTitle}`);

			await expect(
				page.locator(
					'link[href*="classic-theme/images/favicon.ico"][rel="icon"]'
				)
			).toBeAttached();

			// Apply theme favicon client extension to page

			await pagesAdminPage.changeFavicon(
				layoutTitle,
				path.join(__dirname, '/dependencies/thumbnail.jpg'),
				site.friendlyUrlPath
			);

			// Assert custom theme favicon in view mode

			await page.goto(`/web${site.friendlyUrlPath}/${layoutTitle}`);

			await expect(
				page.locator('link[href*="thumbnail.jpg"][rel="icon"]')
			).toBeAttached();

			// Clear theme favicon

			await pagesAdminPage.clearThemeFaviconClientExtension({
				layoutTitle,
				siteUrl: site.friendlyUrlPath,
			});

			// Assert default theme favicon in view mode

			await page.goto(`/web${site.friendlyUrlPath}/${layoutTitle}`);

			await expect(
				page.locator(
					'link[href*="classic-theme/images/favicon.ico"][rel="icon"]'
				)
			).toBeAttached();

			// Clean up

			await deleteClientExtension(apiHelpers, clientExtension);
		}
	);

	test(
		'Add theme favicon extension client extension to pages',
		{
			tag: '@LPS-153654',
		},
		async ({apiHelpers, page, pagesAdminPage, site}) => {

			// Create a new theme favicon client extension

			const clientExtensionName = getRandomString();

			const clientExtension =
				await apiHelpers.jsonWebServicesClientExtension.addClientExtension(
					{
						name: clientExtensionName,
						type: 'themeFavicon',
						url: 'https://www.google.com/favicon.ico',
					}
				);

			// Create a layout

			const layoutTitle = getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: layoutTitle,
			});

			// Assert default theme favicon in view mode

			await page.goto(`/web${site.friendlyUrlPath}/${layoutTitle}`);

			await expect(
				page.locator(
					'link[href*="classic-theme/images/favicon.ico"][rel="icon"]'
				)
			).toBeAttached();

			// Apply theme favicon client extension to page

			await pagesAdminPage.selectClientExtension({
				clientExtensionName,
				siteUrl: site.friendlyUrlPath,
				type: 'themeFavicon',
			});

			// Assert custom theme favicon in view mode

			await page.goto(`/web${site.friendlyUrlPath}/${layoutTitle}`);

			await expect(
				page.locator(
					'link[href*="https://www.google.com/favicon.ico"][rel="icon"]'
				)
			).toBeAttached();

			// Clear theme favicon

			await pagesAdminPage.clearThemeFaviconClientExtension({
				siteUrl: site.friendlyUrlPath,
			});

			// Assert default theme favicon in view mode

			await page.goto(`/web${site.friendlyUrlPath}/${layoutTitle}`);

			await expect(
				page.locator(
					'link[href*="classic-theme/images/favicon.ico"][rel="icon"]'
				)
			).toBeAttached();

			// Clean up

			await deleteClientExtension(apiHelpers, clientExtension);
		}
	);

	test(
		'Inherited theme favicon client extension from master pages',
		{
			tag: '@LPS-153654',
		},
		async ({apiHelpers, masterPagesPage, page, pagesAdminPage, site}) => {

			// Create a new theme favicon client extension

			const clientExtensionName1 = getRandomString();

			const clientExtension1 =
				await apiHelpers.jsonWebServicesClientExtension.addClientExtension(
					{
						name: clientExtensionName1,
						type: 'themeFavicon',
						url: 'https://www.google.com/favicon.ico',
					}
				);

			const clientExtensionName2 = getRandomString();

			const clientExtension2 =
				await apiHelpers.jsonWebServicesClientExtension.addClientExtension(
					{
						name: clientExtensionName2,
						type: 'themeFavicon',
						url: 'https://www.nba.com/favicon.ico',
					}
				);

			// Add master page

			const layoutPageTemplateEntryName = getRandomString();

			const masterPage =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
					{
						groupId: site.id,
						name: layoutPageTemplateEntryName,
						type: 'master-layout',
					}
				);

			// Apply theme favicon client extension to master page

			await masterPagesPage.selectClientExtension({
				clientExtensionName: clientExtensionName1,
				layoutTitle: layoutPageTemplateEntryName,
				siteUrl: site.friendlyUrlPath,
				type: 'themeFavicon',
			});

			// Publish master page

			await masterPagesPage.goto(site.friendlyUrlPath);
			await masterPagesPage.publishMaster(layoutPageTemplateEntryName);

			// Create a layout

			const layoutTitle = getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				masterLayoutPlid: masterPage.plid,
				title: layoutTitle,
			});

			// Assert custom master theme favicon in view mode

			await page.goto(`/web${site.friendlyUrlPath}/${layoutTitle}`);

			await expect(
				page.locator(
					'link[href*="https://www.google.com/favicon.ico"][rel="icon"]'
				)
			).toBeAttached();

			// Apply theme favicon client extension to page

			await pagesAdminPage.selectClientExtension({
				clientExtensionName: clientExtensionName2,
				layoutTitle,
				siteUrl: site.friendlyUrlPath,
				type: 'themeFavicon',
			});

			// Assert custom page theme favicon in view mode

			await page.goto(`/web${site.friendlyUrlPath}/${layoutTitle}`);

			await expect(
				page.locator(
					'link[href*="https://www.nba.com/favicon.ico"][rel="icon"]'
				)
			).toBeAttached();

			// Clean up

			await deleteClientExtension(apiHelpers, clientExtension1);

			await deleteClientExtension(apiHelpers, clientExtension2);
		}
	);

	test(
		'Check draft alert is shown when it applies',
		{tag: ['@LPS-175136']},
		async ({
			apiHelpers,
			page,
			pageConfigurationPage,
			pageEditorPage,
			pagesAdminPage,
			site,
		}) => {

			// Create page

			const pageName = getRandomString();

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				siteId: site.id,
				title: pageName,
			});

			// Go to Design configuration and change something

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pageConfigurationPage.goToSection(pageName, 'Design');

			await expect(async () => {
				await page
					.getByText('Define a custom theme for this page')
					.click();

				await expect(
					page.getByRole('checkbox', {name: 'Show Footer'})
				).toBeEnabled({
					timeout: 1000,
				});
			}).toPass();

			// Save and check alert is displayed

			await pageConfigurationPage.save();

			await expect(
				page.getByText(
					'These design configurations are now saved in a draft'
				)
			).toBeVisible();

			// Publish page via page editor and check alert is not displayed

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await pageEditorPage.publishPage();

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pageConfigurationPage.goToSection(pageName, 'Design');

			await page
				.getByText('Define a custom theme for this page')
				.waitFor();

			await expect(
				page.getByText(
					'These design configurations are now saved in a draft'
				)
			).not.toBeVisible();
		}
	);

	test(
		'Check error message for invalid javascript',
		{
			tag: ['@LPD-45835'],
		},
		async ({
			apiHelpers,
			page,
			pageConfigurationPage,
			pagesAdminPage,
			site,
		}) => {

			// Create page

			const pageName = getRandomString();

			await apiHelpers.headlessDelivery.createSitePage({
				siteId: site.id,
				title: pageName,
			});

			// Go to Design configuration

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pageConfigurationPage.goToSection(pageName, 'Design');

			await pagesAdminPage.clickOnJavaScriptClientExtensionsTab();

			await page
				.getByPlaceholder('JavaScript')
				.fill('<script>console.log()</script>');

			// Save configuration

			await pageConfigurationPage.saveButton.click();

			// Assert error message

			await waitForAlert(page, 'Error:Your request failed to complete.', {
				type: 'danger',
			});

			await expect(
				page.getByText(
					'Error:Scripts tags are not supported inside custom JavaScript.'
				)
			).toBeVisible();
		}
	);
});

test.describe('SEO configuration', () => {
	test('Can disable open graph', async ({
		apiHelpers,
		page,
		pagesAdminPage,
		site,
		systemSettingsPage,
	}) => {

		// Create page

		const pageName = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: pageName,
		});

		// Disable open graph

		await systemSettingsPage.goToSystemSetting('Pages', 'SEO');

		const openGraphCheckInput = page.getByLabel('Enable Open Graph');

		await openGraphCheckInput.uncheck();

		await page.locator('.btn-primary').click();

		await waitForAlert(page);

		// Assert open graph section is not present

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.clickOnAction('Configure', pageName);

		await expect(
			page.locator('nav.menubar', {
				has: page.getByText('Open Graph'),
			})
		).not.toBeAttached();

		// Enable open graph

		await systemSettingsPage.goToSystemSetting('Pages', 'SEO');

		await openGraphCheckInput.check();

		await page.locator('.btn-primary').click();

		await waitForAlert(page);

		// Assert open graph section is present

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.clickOnAction('Configure', pageName);

		await expect(
			page.locator('nav.menubar', {
				has: page.getByText('Open Graph'),
			})
		).toBeAttached();
	});

	test('Checks page SEO HTML title is not shown in edit mode', async ({
		apiHelpers,
		page,
		pageConfigurationPage,
		pageEditorPage,
		pagesAdminPage,
		site,
	}) => {

		// Create page

		const pageName = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: pageName,
		});

		// Change SEO HTML title

		await pagesAdminPage.goto(site.friendlyUrlPath);
		await pageConfigurationPage.goToSection(pageName, 'SEO');

		const HTMLTitle = getRandomString();

		await pageConfigurationPage.setHTMLTitle(HTMLTitle);

		// Check SEO HTML title is shown in view mode

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		expect(await page.title()).toBe(
			`${HTMLTitle} - ${site.name} - Liferay DXP`
		);

		// Check SEO HTML title is not shown in view mode

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		expect(await page.title()).toBe(
			`${pageName} - ${site.name} - Liferay DXP (Editing)`
		);
	});

	test('SEO preview', async ({
		apiHelpers,
		page,
		pageConfigurationPage,
		pagesAdminPage,
		site,
	}) => {

		// Create page and go to SEO

		const pageName = getRandomString();

		await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: pageName,
		});

		await pagesAdminPage.goto(site.friendlyUrlPath);
		await pageConfigurationPage.goToSection(pageName, 'SEO');

		// Change SEO HTML title and description in default language

		const defaultLanguageHTMLTitle = getRandomString();

		await page.getByLabel('HTML Title').fill(defaultLanguageHTMLTitle);

		const defaultLanguageDescription = getRandomString();

		await page.getByLabel('Description').fill(defaultLanguageDescription);

		// Assert preview

		await expect(page.locator('.preview-seo-title')).toContainText(
			`${defaultLanguageHTMLTitle} - ${site.name}`
		);

		await expect(page.locator('.preview-seo-description')).toContainText(
			defaultLanguageDescription
		);

		// Switch language

		await page
			.getByRole('button')
			.filter({hasText: 'en-US'})
			.first()
			.click();

		await page.getByRole('menuitem').filter({hasText: 'es-ES'}).click();

		// Change SEO HTML title and description in spanish

		const spanishLanguageHTMLTitle = getRandomString();

		await page.getByLabel('HTML Title').fill(spanishLanguageHTMLTitle);

		const spanishLanguageDescription = getRandomString();

		await page.getByLabel('Description').fill(spanishLanguageDescription);

		// Assert preview

		await expect(page.locator('.preview-seo-title')).toContainText(
			`${spanishLanguageHTMLTitle} - ${site.name}`
		);

		await expect(page.locator('.preview-seo-description')).toContainText(
			spanishLanguageDescription
		);
	});

	test('User can customize canonical url', async ({
		apiHelpers,
		page,
		pageConfigurationPage,
		pagesAdminPage,
		site,
	}) => {

		// Create page

		const pageName = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: pageName,
		});

		// Assert canonical url

		await performLogout(page);

		await page.goto(`/web${site.friendlyUrlPath}/${pageName}`);

		expect(
			await page.locator('link[rel="canonical"]').getAttribute('href')
		).toBe(
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}`
		);

		await performLoginViaApi({page, screenName: 'test'});

		// Go to SEO

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pageConfigurationPage.goToSection(pageName, 'SEO');

		// Configure custom canonical url

		await page.getByLabel('Use Custom Canonical URL').check();

		await expect(
			page.getByText('Due to the customization of the canonical URL')
		).toBeAttached();

		const canonicalURL = page.getByLabel('Canonical URL', {exact: true});

		await expect(canonicalURL).toHaveValue(
			`${liferayConfig.environment.baseUrl}/web${site.friendlyUrlPath}`
		);

		await canonicalURL.fill('http://mycustomizedurl.com/mydesiredurl');

		// Save configuration

		await pageConfigurationPage.save();

		// Assert custom canonical url

		await performLogout(page);

		await page.goto(`/web${site.friendlyUrlPath}/${pageName}`);

		expect(
			await page.locator('link[rel="canonical"]').getAttribute('href')
		).toBe('http://mycustomizedurl.com/mydesiredurl');
	});

	test('User can customize custom meta tags', async ({
		apiHelpers,
		page,
		pageConfigurationPage,
		pagesAdminPage,
		site,
	}) => {

		// Create page

		const pageName = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: pageName,
		});

		// Configure custom meta tags

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pageConfigurationPage.goToSection(pageName, 'Custom Meta Tags');

		// Edit custom meta tags

		await page.getByRole('button', {name: 'Add'}).waitFor();

		const property1 = page.getByLabel('Property', {exact: true}).nth(0);

		await property1.waitFor();

		const property1Name = getRandomString();

		await property1.fill(property1Name);

		const content1 = page.getByLabel('Content', {exact: true}).nth(0);

		const content1DefaultValue = getRandomString();

		await content1.fill(content1DefaultValue);

		// Add new custom meta tag

		await page.getByRole('button', {name: 'Add'}).click();

		// Edit new custom meta tag

		const property2 = page.getByLabel('Property', {exact: true}).nth(1);

		await property2.waitFor();

		const property2Name = getRandomString();

		await property2.fill(property2Name);

		const content2 = page.getByLabel('Content', {exact: true}).nth(1);

		const content2DefaultValue = getRandomString();

		await content2.fill(content2DefaultValue);

		// Wait a bit until the multivalues component register the value

		await page.waitForTimeout(1000);

		// Switch language

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'Not translated into Spanish.',
			}),
			trigger: page
				.getByRole('button')
				.filter({hasText: 'en-US'})
				.first(),
		});

		// Translate custom meta tags

		const content1SpanishValue = getRandomString();

		await content1.fill(content1SpanishValue);

		const content2SpanishValue = getRandomString();

		await content2.fill(content2SpanishValue);

		// Save configuration

		await pageConfigurationPage.save();

		// Assert spanish language custom meta tags

		await performLogout(page);

		await page.goto(`/es/web${site.friendlyUrlPath}/${pageName}`);

		await expect(
			page.locator(
				`meta[property="${property1Name}"][content="${content1SpanishValue}"]`
			)
		).toBeAttached();

		await expect(
			page.locator(
				`meta[property="${property2Name}"][content="${content2SpanishValue}"]`
			)
		).toBeAttached();

		// Assert default language custom meta tags

		await page.goto(`/en/web${site.friendlyUrlPath}/${pageName}`);

		await expect(
			page.locator(
				`meta[property="${property1Name}"][content="${content1DefaultValue}"]`
			)
		).toBeAttached();

		await expect(
			page.locator(
				`meta[property="${property2Name}"][content="${content2DefaultValue}"]`
			)
		).toBeAttached();
	});

	test(
		'User can customize open graph tags',
		{
			tag: '@LPS-134658',
		},
		async ({
			apiHelpers,
			page,
			pageConfigurationPage,
			pagesAdminPage,
			site,
		}) => {

			// Create page

			const pageName = getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: pageName,
			});

			// Configure open graph tags

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pageConfigurationPage.goToSection(pageName, 'Open Graph');

			// Configure image

			const fileChooserPromise = page.waitForEvent('filechooser');

			const iframe = page.frameLocator('iframe[title="Select Image"]');

			await clickAndExpectToBeVisible({
				target: iframe.getByText(
					'Drag & Drop Your Images or Browse to Upload'
				),
				timeout: 2000,
				trigger: page.getByLabel('Select Image', {exact: true}),
			});

			await iframe
				.getByText('Drag & Drop Your Images or Browse to Upload')
				.click();

			const fileChooser = await fileChooserPromise;

			await fileChooser.setFiles(
				path.join(__dirname, '/dependencies/thumbnail.jpg')
			);

			await iframe
				.getByRole('button', {exact: true, name: 'Add'})
				.click();

			// Configure image alt description

			const imageAltDescription = getRandomString();

			await page
				.getByLabel('Image Alt Description')
				.fill(imageAltDescription);

			// Configure title

			await page.getByLabel('Use Custom Title').check();

			const title = getRandomString();

			await page.getByPlaceholder('Title', {exact: true}).fill(title);

			// Configure description

			await page.getByLabel('Use Custom Description').check();

			const description = getRandomString();

			await page
				.getByPlaceholder('Description', {exact: true})
				.fill(description);

			await pageConfigurationPage.save();

			// Assert open graph tags

			await performLogout(page);

			await page.goto(`/web${site.friendlyUrlPath}/${pageName}`);

			await expect(
				page.locator(`meta[property="og:title"][content="${title}"]`)
			).toBeAttached();

			await expect(
				page.locator(
					`meta[property="og:description"][content="${description}"]`
				)
			).toBeAttached();

			await expect(
				page.locator(
					`meta[property="og:image"][content*="thumbnail.jpg"]`
				)
			).toBeAttached();

			await expect(
				page.locator(
					`meta[property="og:image:alt"][content="${imageAltDescription}"]`
				)
			).toBeAttached();
		}
	);
});

test.describe('Page types configuration', () => {
	test('Can configure an embedded page', async ({
		apiHelpers,
		page,
		pageConfigurationPage,
		pagesAdminPage,
		site,
	}) => {
		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {
				type: 'embedded',
			},
			title: 'Embedded',
		});

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pageConfigurationPage.setInputValueAndSave(
			page.getByLabel('URL').first(),
			'Embedded',
			'General',
			'https://www.google.com'
		);

		// Check URL was updated

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pageConfigurationPage.goToSection('Embedded', 'General');

		await expect(page.getByLabel('URL').first()).toHaveValue(
			'https://www.google.com'
		);
	});

	test('Can configure a full page application', async ({
		apiHelpers,
		page,
		pageConfigurationPage,
		pagesAdminPage,
		site,
	}) => {
		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {
				type: 'full_page_application',
			},
			title: 'Full Page Application',
		});

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pageConfigurationPage.goToSection(
			'Full Page Application',
			'General'
		);

		await selectAndExpectToHaveValue({
			optionLabel: 'Wiki',
			select: page.getByLabel('Full Page Application'),
		});

		await pageConfigurationPage.save();

		// Go to view mode of page

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await expect(page.getByRole('heading', {name: 'Wiki'})).toBeVisible();
	});

	test('Can configure a panel page', async ({
		apiHelpers,
		page,
		pageConfigurationPage,
		pagesAdminPage,
		site,
	}) => {

		// Create page and go to General configuration

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {
				type: 'panel',
			},
			title: 'Panel',
		});

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pageConfigurationPage.goToSection('Panel', 'General');

		// Select Collaboration application

		await page
			.locator('.treeview-link[data-id*="collaboration"]')
			.getByRole('checkbox')
			.check();

		await pageConfigurationPage.save();

		// Go to view mode of page

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await expect(
			page.getByRole('link', {exact: true, name: 'Blogs'})
		).toBeVisible();
	});

	test(
		'Can configure a link to page',
		{
			tag: '@LPS-159631',
		},
		async ({
			apiHelpers,
			page,
			pageConfigurationPage,
			pagesAdminPage,
			site,
		}) => {

			// Create widget page

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: getRandomString(),
			});

			// Create link to URL page

			const linkToURLTitle = getRandomString();

			await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				options: {
					type: 'url',
				},
				title: linkToURLTitle,
			});

			// Update URL

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pageConfigurationPage.setInputValueAndSave(
				page.getByLabel('URL').first(),
				linkToURLTitle,
				'General',
				'https://www.google.com'
			);

			// Navigate to page

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			// Check target is not set

			await expect(
				page.getByRole('menuitem', {name: linkToURLTitle})
			).not.toHaveAttribute('target');

			// Update specific target to _blank

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pageConfigurationPage.setInputValueAndSave(
				page.getByLabel('Target', {exact: true}),
				linkToURLTitle,
				'General',
				'_blank'
			);

			// Navigate to page

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			// Check target is set to _blank

			await expect(
				page.getByRole('menuitem', {name: linkToURLTitle})
			).toHaveAttribute('target', '_blank');

			// Update target type to new tab

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pageConfigurationPage.goToSection(linkToURLTitle, 'General');

			await selectAndExpectToHaveValue({
				optionLabel: 'New Tab',
				select: page.getByLabel('Target type'),
			});

			await pageConfigurationPage.save();

			// Navigate to page

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			// Check target is set to _blank

			await expect(
				page.getByRole('menuitem', {name: linkToURLTitle})
			).toHaveAttribute('target', '_blank');
		}
	);
});

test.describe('Utility Page', () => {
	test(
		'Asserts the Utility Pages configuration view',
		{
			tag: '@LPD-4459',
		},
		async ({
			page,
			pageEditorPage,
			site,
			utilityPageConfigurationPage,
			utilityPagesPage,
		}) => {
			await page.goto('/');

			// The configuration action must be available from the card
			// The configuration view should only allow setting the htmlTitle and htmlDescription SEO fields

			await utilityPagesPage.goto(site.friendlyUrlPath);
			await utilityPageConfigurationPage.setUtilityPageConfiguration(
				getRandomString(),
				getRandomString(),
				'404 Error'
			);

			// During editing the "More Page Design Options" link should not be available

			await utilityPagesPage.goto(site.friendlyUrlPath);
			await utilityPagesPage.goToEdit('404 Error');
			await pageEditorPage.goToSidebarTab('Page Design Options');

			await expect(page.getByText('Master', {exact: true})).toBeVisible();
			expect(
				await page.getByTitle('More Page Design Options').count()
			).toEqual(0);
		}
	);
});
