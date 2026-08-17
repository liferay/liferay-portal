/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

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
import {pagesPagesTest} from './fixtures/pagesPagesTest';

const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	loginTest(),
	pagesAdminPagesTest,
	pagesPagesTest,
	pageViewModePagesTest
);

const CLAY_PORTLET_NAME = 'Clay Sample';

function getPortletByName({
	page,
	portletName,
}: {
	page: Page;
	portletName: string;
}) {
	return page.getByRole('heading', {
		name: portletName,
	});
}

async function expectPortletInColumn({
	columnNumber,
	page,
	portletName,
}: {
	columnNumber: number;
	page: Page;
	portletName: string;
}) {
	await expect(
		page
			.getByRole('main')
			.locator('.portlet-column')
			.filter({has: getPortletByName({page, portletName})})
	).toHaveId(`column-${columnNumber}`);
}

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

	test(
		'Content item stacks the subtitle below the title and centers the add button on the right',
		{
			tag: '@LPD-94654',
		},
		async ({apiHelpers, page, site, widgetPagePage}) => {

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

			// Create page, go to view mode and open the Content panel

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: getRandomString(),
			});

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			await widgetPagePage.openAddPanel();

			await widgetPagePage.contentTab.click();

			// Wait for the content item to render

			const item = page
				.locator('.sidebar-body__add-panel__tab-item')
				.filter({hasText: webContentTitle})
				.first();

			await expect(item).toBeVisible();

			// Measure the content item layout, retrying until it settles

			await expect(async () => {
				await item.hover({timeout: 2000});

				const itemBox = await item.boundingBox({timeout: 2000});
				const titleBox = await item
					.locator('.title')
					.boundingBox({timeout: 2000});
				const subtitleBox = await item
					.locator('.subtitle')
					.boundingBox({timeout: 2000});
				const addButtonBox = await item
					.getByRole('button', {name: 'Add Content'})
					.boundingBox({timeout: 2000});

				if (!itemBox || !titleBox || !subtitleBox || !addButtonBox) {
					throw new Error(
						'Expected the content item layout to be measurable'
					);
				}

				// The subtitle sits below the title and is left-aligned with it

				expect(subtitleBox.y).toBeGreaterThanOrEqual(
					titleBox.y + titleBox.height - 2
				);
				expect(
					Math.abs(subtitleBox.x - titleBox.x)
				).toBeLessThanOrEqual(2);

				// The add button is vertically centered within the row

				expect(
					Math.abs(
						addButtonBox.y +
							addButtonBox.height / 2 -
							(itemBox.y + itemBox.height / 2)
					)
				).toBeLessThanOrEqual(3);

				// The add button is anchored to the right edge of the row

				expect(
					itemBox.x +
						itemBox.width -
						(addButtonBox.x + addButtonBox.width)
				).toBeLessThanOrEqual(12);
			}).toPass({timeout: 10000});
		}
	);
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

test.describe('Three columns layout', () => {
	test.beforeEach(
		async ({
			apiHelpers,
			page,
			pageConfigurationPage,
			pagesAdminPage,
			site,
		}) => {
			const layoutTitle = getRandomString();

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: layoutTitle,
			});

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pageConfigurationPage.goToSection(layoutTitle, 'General');

			await page.getByTitle('3 Columns', {exact: true}).click();

			const card = page.locator('.card.card-interactive').first();

			await expect(card).toHaveClass(/active/);

			await pageConfigurationPage.save();

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);
		}
	);

	test('Portlet can be dragged and dropped', async ({
		page,
		widgetPagePage,
	}) => {
		await test.step('Add portlet to the first column in the page layout', async () => {
			await widgetPagePage.addPortlet(CLAY_PORTLET_NAME);

			await expectPortletInColumn({
				columnNumber: 1,
				page,
				portletName: CLAY_PORTLET_NAME,
			});
		});

		await test.step('Drag and drop portlet to another column in page', async () => {
			await widgetPagePage.dragPortlet({
				portletName: CLAY_PORTLET_NAME,
				target: page
					.getByRole('main')
					.locator('.portlet-column .portlet-dropzone.empty')
					.first(),
				topperSelector: '.portlet .portlet-topper',
			});

			await expectPortletInColumn({
				columnNumber: 2,
				page,
				portletName: CLAY_PORTLET_NAME,
			});
		});
	});

	test('Portlet can be removed', async ({page, widgetPagePage}) => {
		await test.step('Add portlet to the page layout', async () => {
			await widgetPagePage.addPortlet(CLAY_PORTLET_NAME);

			expect(
				getPortletByName({page, portletName: CLAY_PORTLET_NAME})
			).toBeVisible();
		});

		await test.step('Delete portlet from the page', async () => {
			await widgetPagePage.deletePortlet(CLAY_PORTLET_NAME);

			expect(
				getPortletByName({page, portletName: CLAY_PORTLET_NAME})
			).toBeHidden();
		});
	});

	test('Portlets have defined limits in the 3-column page layout.', async ({
		page,
		widgetPagePage,
	}) => {
		const MESSAGE_BOARDS_PORTLET_NAME = 'Message Boards';
		const DOCUMENTS_AND_MEDIA_PORTLET_NAME = 'Documents and Media';

		await test.step('Add portlet to the third column in the page layout', async () => {
			await widgetPagePage.addPortlet(DOCUMENTS_AND_MEDIA_PORTLET_NAME);

			await widgetPagePage.dragPortlet({
				portletName: DOCUMENTS_AND_MEDIA_PORTLET_NAME,
				target: page.locator('#layout-column_column-3'),
				topperSelector: '.portlet .portlet-topper',
			});
		});

		await test.step('Add portlet to the second column', async () => {
			await widgetPagePage.addPortlet(MESSAGE_BOARDS_PORTLET_NAME);

			await widgetPagePage.dragPortlet({
				portletName: MESSAGE_BOARDS_PORTLET_NAME,
				target: page.locator('#layout-column_column-2'),
				topperSelector: '.portlet .portlet-topper',
			});
		});

		await test.step('Add portlet to the first column', async () => {
			await widgetPagePage.addPortlet(CLAY_PORTLET_NAME);
		});

		await test.step('Verify that all portlets are in the correct column', async () => {
			await expectPortletInColumn({
				columnNumber: 1,
				page,
				portletName: CLAY_PORTLET_NAME,
			});

			await expectPortletInColumn({
				columnNumber: 2,
				page,
				portletName: MESSAGE_BOARDS_PORTLET_NAME,
			});

			await expectPortletInColumn({
				columnNumber: 3,
				page,
				portletName: DOCUMENTS_AND_MEDIA_PORTLET_NAME,
			});
		});
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
