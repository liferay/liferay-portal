/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {openProductMenu} from '../../../utils/productMenu';
import addApprovedStructuredContent from '../../../utils/structured-content/addApprovedStructuredContent';
import addDraftStructuredContent from '../../../utils/structured-content/addDraftStructuredContent';
import addExpiredStructuredContent from '../../../utils/structured-content/addExpiredStructuredContent';
import addInTrashStructuredContent from '../../../utils/structured-content/addInTrashStructuredContent';
import addScheduledStructuredContent from '../../../utils/structured-content/addScheduledStructuredContent';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	pagesAdminPagesTest,
	pageViewModePagesTest
);

test(
	'Drag handler is shown only in non-static widgets',
	{tag: ['@LPD-33348']},
	async ({apiHelpers, page, site, widgetPagePage}) => {

		// Create widget page and add a widget

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {
				type: 'portlet',
			},
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await widgetPagePage.addPortlet('Web Content Display');

		// Check drag handler is shown in topper

		await page
			.locator('.portlet-content')
			.getByText('Web Content Display')
			.hover();

		await expect(
			page
				.locator('.portlet-topper', {hasText: 'Web Content Display'})
				.locator('.lexicon-icon-drag')
		).toBeVisible();

		// Check drag handler is not shown for static widgets

		await page.locator('.portlet-content').getByText('Search Bar').hover();

		await expect(
			page
				.locator('.portlet-topper', {hasText: 'Search Bar'})
				.locator('.portlet-name-text')
		).toBeVisible();

		await expect(
			page
				.locator('.portlet-topper', {hasText: 'Search Bar'})
				.locator('.lexicon-icon-drag')
		).not.toBeVisible();
	}
);

test.describe('Content tab add panel', () => {
	test(
		'Check correct web contents are displayed in Content tab of the Add panel',
		{
			tag: '@LPD-15256',
		},
		async ({apiHelpers, page, site, widgetPagePage}) => {

			// Add required basic web contents

			const approvedWebContentTitle = getRandomString();
			const draftWebContentTitle = getRandomString();
			const expiredWebContentTitle = getRandomString();
			const inTrashWebContentTitle = getRandomString();
			const scheduledWebContentTitle = getRandomString();

			const contentStructureId =
				await getBasicWebContentStructureId(apiHelpers);

			await addApprovedStructuredContent({
				apiHelpers,
				contentStructureId,
				siteId: site.id,
				title: approvedWebContentTitle,
			});

			await addDraftStructuredContent({
				apiHelpers,
				contentStructureId,
				siteId: site.id,
				title: draftWebContentTitle,
			});

			await addExpiredStructuredContent(
				apiHelpers,
				site.id,
				contentStructureId,
				expiredWebContentTitle
			);

			await addInTrashStructuredContent(
				apiHelpers,
				site.id,
				contentStructureId,
				inTrashWebContentTitle
			);

			await addScheduledStructuredContent(
				apiHelpers,
				site.id,
				contentStructureId,
				scheduledWebContentTitle
			);

			// Method to verify correct web contents are visible
			// Approved and scheduled web contents should be displayed,
			// whereas draft, expired and in-trash web contents should not

			async function verifyVisibleWebContents() {
				await expect(
					page.getByText(approvedWebContentTitle)
				).toBeVisible();
				await expect(
					page.getByText(draftWebContentTitle)
				).not.toBeVisible();
				await expect(
					page.getByText(expiredWebContentTitle)
				).not.toBeVisible();
				await expect(
					page.getByText(inTrashWebContentTitle)
				).not.toBeVisible();
				await expect(
					page.getByText(scheduledWebContentTitle)
				).toBeVisible();
			}

			// Create page, go to view mode and open Contents panel

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: getRandomString(),
			});

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			await widgetPagePage.openAddPanel();

			await widgetPagePage.contentTab.click();

			// Verify correct web contents are displayed

			await verifyVisibleWebContents();

			await page.getByLabel('Select Label').selectOption('8');

			await verifyVisibleWebContents();

			await page.getByRole('button', {name: 'Display Style'}).click();

			await verifyVisibleWebContents();
		}
	);

	test('View web content is shown in Web Content Display after be added via content panel', async ({
		apiHelpers,
		page,
		site,
		widgetPagePage,
	}) => {

		// Add required web content

		const webContentTitle = getRandomString();

		const contentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await addApprovedStructuredContent({
			apiHelpers,
			contentStructureId,
			siteId: site.id,
			title: webContentTitle,
		});

		// Create page and go to view mode

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		// Check item has correct title

		await widgetPagePage.openAddPanel();

		await widgetPagePage.contentTab.click();

		await expect(page.getByTitle(webContentTitle)).toBeVisible();

		// Add content and check it's displayed inside a Web Content Display

		await widgetPagePage.addContent(webContentTitle);

		await expect(
			page.locator('.portlet-journal-content').getByText(webContentTitle)
		).toBeVisible();
	});
});

test.describe('Customization settings', () => {
	test('Can customize page as site member', async ({
		apiHelpers,
		page,
		pagesAdminPage,
		site,
		widgetPagePage,
	}) => {

		// Create page

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			title: layoutTitle,
		});

		// Enable customization

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.clickOnAction('Configure', layoutTitle);

		await page.getByLabel('Customizable', {exact: true}).check();

		await page.getByTitle('column-1-customizable', {exact: true}).check();

		await pagesAdminPage.saveConfiguration();

		// Go to view mode and assert info customize message

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		await expect(
			page.getByText('You can customize this page.')
		).toBeVisible();

		// Add non instanceable blog portlet to customizable column

		await widgetPagePage.addPortlet('Blogs');

		const column1 = page.locator('#layout-column_column-1');

		await expect(
			column1.getByRole('heading', {name: 'Blogs'})
		).toBeVisible();

		// Add new site member user and login

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		const siteRole =
			await apiHelpers.headlessAdminUser.getRoleByName('Site Member');

		await apiHelpers.headlessAdminUser.assignUserToSite(
			siteRole.id,
			site.id,
			user.id
		);

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		// Go to view mode

		await performUserSwitch(page, user.alternateName);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		// Assert new user can add non instanceable blog portlet to customizable column

		await expect(
			column1.getByRole('heading', {name: 'Blogs'})
		).not.toBeVisible();

		await widgetPagePage.addPortlet('Blogs');

		await expect(
			column1.getByRole('heading', {name: 'Blogs'})
		).toBeVisible();

		// Add web content display portlet to customizable column

		await widgetPagePage.addPortlet('Web Content Display');

		await expect(
			column1.getByRole('heading', {name: 'Web Content Display'})
		).toBeVisible();

		// Delete web content display portlet from customizable column

		await widgetPagePage.deletePortlet('Web Content Display');

		await expect(
			column1.getByRole('heading', {name: 'Web Content Display'})
		).not.toBeVisible();

		// Click on view page without my customizations and assert blogs is not visible

		await page
			.locator('.sidebar')
			.getByRole('button', {name: 'Close'})
			.click();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'View Page without my customizations',
			}),
			trigger: page.getByRole('button', {name: 'Show Actions'}),
		});

		await expect(
			column1.getByRole('heading', {name: 'Blogs'})
		).not.toBeVisible();

		// Click on view my customized page and assert blogs is visible

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'View My Customized Page',
			}),
			trigger: page.getByRole('button', {name: 'Show Actions'}),
		});

		await expect(
			column1.getByRole('heading', {name: 'Blogs'})
		).toBeVisible();

		// Click on reset my customizations and assert blogs is not visible

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {
				name: 'Reset My Customizations',
			}),
			trigger: page.getByRole('button', {name: 'Show Actions'}),
		});

		await expect(
			column1.getByRole('heading', {name: 'Blogs'})
		).not.toBeVisible();
	});
});

test.describe('Toggle controls', () => {
	test(
		'Can hide and show portlet header of existing visible portlets on widget page via switch Toggle Controls',
		{
			tag: '@LPS-108216',
		},
		async ({apiHelpers, page, site, widgetPagePage}) => {

			// Create page and go to view mode

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: getRandomString(),
			});

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			await widgetPagePage.addPortlet('Blogs Aggregator');

			const blogsWidget = page.locator('.portlet-blogs');

			// Make sure controls are visible and check topper is shown

			await widgetPagePage.toggleControls('visible');

			const topper = page
				.locator('.portlet-topper')
				.getByText('Blogs Aggregator');

			await blogsWidget.hover();

			await expect(topper).toBeVisible();

			// Toggle controls and check topper is not shown

			await widgetPagePage.toggleControls('hidden');

			await blogsWidget.hover();

			await expect(topper).not.toBeVisible();

			// Recover original state

			await widgetPagePage.toggleControls('visible');

			// Delete Web Content Display and check it's not displayed

			await widgetPagePage.deletePortlet('Blogs Aggregator');

			await expect(
				page.locator('.portlet-topper', {hasText: 'Blogs Aggregator'})
			).not.toBeVisible();
		}
	);
});

test.describe('XSS', () => {
	test(
		'View the XSS is escaped when store it in widget page name',
		{
			tag: '@LPS-178476',
		},
		async ({apiHelpers, page, site}) => {

			// Add listener with expect so it fails when a browser dialog is shown

			page.on('dialog', async (dialog) => {
				dialog.accept();

				expect(
					dialog.message(),
					'This alert should not be shown'
				).toBeNull();
			});

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: '<script>alert(123);</script>',
			});

			// Go to view mode of page

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			// Open the Product Menu

			await openProductMenu(page);
		}
	);
});
