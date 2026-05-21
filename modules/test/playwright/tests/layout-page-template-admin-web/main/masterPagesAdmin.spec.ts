/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {masterPagesPagesTest} from '../../../fixtures/masterPagesPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import {MasterPagesPage} from '../../../pages/layout-page-template-admin-web/MasterPagesPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {hoverAndExpectToBeVisible} from '../../../utils/hoverAndExpectToBeVisible';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	pagesAdminPagesTest,
	isolatedSiteTest,
	featureFlagsTest({
		'LPD-76864': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	masterPagesPagesTest,
	pageEditorPagesTest
);

async function addMasterPage(
	apiHelpers: ApiHelpers,
	masterPageName: string,
	masterPagesPage: MasterPagesPage,
	pageEditorPage: PageEditorPage,
	site: Site
) {

	// Add master page

	const masterPage =
		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
			{
				groupId: site.id,
				name: masterPageName,
				type: 'master-layout',
			}
		);

	// Edit master page

	await masterPagesPage.goto(site.friendlyUrlPath);

	await masterPagesPage.editMaster(masterPageName);

	await pageEditorPage.addFragment('Basic Components', 'Heading');

	const headingId = await pageEditorPage.getFragmentId('Heading');

	await pageEditorPage.editTextEditable(
		headingId,
		'element-text',
		`Master Page: ${masterPageName}`
	);

	await pageEditorPage.publishPage();

	return masterPage;
}

test('Validate if the Blank page template can not be edited and deleted', async ({
	masterPagesPage,
	site,
}) => {

	// Go to master pages administration

	await masterPagesPage.goto(site.friendlyUrlPath);

	// Check Blank can not be edited or deleted

	const templateCard = masterPagesPage.getMasterCard('Blank');

	await expect(templateCard).toBeVisible();

	await expect(templateCard.getByLabel('More actions')).not.toBeVisible();

	await expect(
		templateCard.locator('.custom-control.custom-checkbox')
	).not.toBeVisible();
});

test(
	'Add a page based on custom master',
	{
		tag: ['@LPS-102566', '@LPS-140318'],
	},
	async ({masterPagesPage, page, pageEditorPage, pagesAdminPage, site}) => {
		const masterName = getRandomString();

		let buttonId: string;

		await test.step('Create and publish new custom master page and edit it', async () => {
			await masterPagesPage.goto(site.friendlyUrlPath);

			await masterPagesPage.createNewMaster(masterName);

			await masterPagesPage.editMaster(masterName);
		});

		await test.step('Add and configure a Button fragment on master page', async () => {
			await pageEditorPage.addFragment('Basic Components', 'Button');

			buttonId = await pageEditorPage.getFragmentId('Button');

			expect(
				await pageEditorPage.getFragmentStyle({
					fragmentId: buttonId,
					style: 'backgroundColor',
				})
			).toBe('rgba(0, 0, 0, 0)');

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Background Color',
				fragmentId: buttonId,
				tab: 'Styles',
				value: 'Gray 300',
				valueFromStylebook: true,
			});

			expect(
				await pageEditorPage.getFragmentStyle({
					fragmentId: buttonId,
					style: 'backgroundColor',
				})
			).toBe('rgb(231, 231, 237)');

			await pageEditorPage.publishPage();
		});

		const pageName = getRandomString();

		await test.step('Assert custom masters as an option when add a new page', async () => {
			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pagesAdminPage.createNewPage({
				name: pageName,
				template: masterName,
			});
		});

		await test.step('Assert the new page inherits elements from custom masters', async () => {
			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pagesAdminPage.editPage(pageName);

			expect(
				await pageEditorPage.getFragmentStyle({
					fragmentId: buttonId,
					style: 'backgroundColor',
				})
			).toBe('rgb(231, 231, 237)');
		});

		await test.step('Update custom master', async () => {
			await masterPagesPage.goto(site.friendlyUrlPath);

			await masterPagesPage.editMaster(masterName);

			await pageEditorPage.addFragment('Basic Components', 'Heading');

			page.on('dialog', (dialog) => dialog.accept());

			await pageEditorPage.publishPage();
		});

		await test.step('Assert the new page inherits updated elements from custom master', async () => {
			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pagesAdminPage.editPage(pageName);

			await expect(page.getByText('Heading Example')).toBeVisible();
		});
	}
);

test(
	'Assert the content in drop zone is not changed when change master of page',
	{
		tag: '@LPS-102200',
	},
	async ({apiHelpers, masterPagesPage, page, pageEditorPage, site}) => {

		// Add master 2 page templates

		const masterPageName1 = getRandomString();

		const masterPage1 = await addMasterPage(
			apiHelpers,
			masterPageName1,
			masterPagesPage,
			pageEditorPage,
			site
		);

		const masterPageName2 = getRandomString();

		await addMasterPage(
			apiHelpers,
			masterPageName2,
			masterPagesPage,
			pageEditorPage,
			site
		);

		// Create a page based on first master page

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			masterLayoutPageTemplateEntryERC: masterPage1.externalReferenceCode,
			options: {type: 'content'},
			title: layoutTitle,
		});

		// Go to edit mode

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Assert inherited fragment from master page is visible

		await expect(
			page.getByText(`Master Page: ${masterPageName1}`)
		).toBeVisible();
		await expect(
			page.getByText(`Master Page: ${masterPageName2}`)
		).not.toBeVisible();

		// Add a new fragment

		await pageEditorPage.addFragment('Basic Components', 'Button');

		// Change master page to second custom master

		await pageEditorPage.goToSidebarTab('Page Design Options');

		await page.getByLabel(masterPageName2).click();

		await pageEditorPage.waitForChangesSaved();

		// Assert inherited fragment from master page is visible

		await expect(
			page.getByText(`Master Page: ${masterPageName1}`)
		).not.toBeVisible();
		await expect(
			page.getByText(`Master Page: ${masterPageName2}`)
		).toBeVisible();

		await expect(page.getByText('Go Somewhere')).toBeVisible();

		// Change master page to blank master

		await pageEditorPage.goToSidebarTab('Page Design Options');

		await page.getByLabel('Blank').click();

		await pageEditorPage.waitForChangesSaved();

		// Assert inherited fragment from master page is visible

		await expect(
			page.getByText(`Master Page: ${masterPageName1}`)
		).not.toBeVisible();
		await expect(
			page.getByText(`Master Page: ${masterPageName2}`)
		).not.toBeVisible();

		await expect(page.getByText('Go Somewhere')).toBeVisible();
	}
);

test(
	'Can duplicate a master page template',
	{
		tag: '@LPS-102208',
	},
	async ({apiHelpers, masterPagesPage, page, site}) => {

		// Add master page template

		const masterName = getRandomString();

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
			{
				groupId: site.id,
				name: masterName,
				type: 'master-layout',
			}
		);

		// Duplicate master page template

		await masterPagesPage.goto(site.friendlyUrlPath);

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('menuitem', {name: 'Make a Copy'}),
			trigger: page
				.locator('.card-page-item')
				.filter({hasText: masterName})
				.getByLabel('More actions'),
		});

		await hoverAndExpectToBeVisible({
			autoClick: true,
			target: page.getByText('Master Page', {exact: true}).nth(1),
			trigger: page.getByRole('menuitem', {name: 'Make a Copy'}),
		});

		await waitForAlert(page);

		// Assert master page template is duplicated

		await expect(
			page.getByRole('link', {exact: true, name: `${masterName} (Copy)`})
		).toBeVisible();
	}
);

test(
	'Fragments hidden in master pages are hidden in pages that use it and visibility can not be changed',
	{tag: '@LPS-102566'},
	async ({masterPagesPage, page, pageEditorPage, pagesAdminPage, site}) => {
		const masterName = getRandomString();

		let buttonId: string;
		let buttonFragment: Locator;
		let containerId: string;
		let containerFragment: Locator;
		let headingId: string;
		let headingFragment: Locator;

		await test.step('Create and publish new custom master page with one fragment hidden', async () => {
			await masterPagesPage.goto(site.friendlyUrlPath);

			await masterPagesPage.createNewMaster(masterName);
			await masterPagesPage.editMaster(masterName);

			await pageEditorPage.addFragment('Basic Components', 'Button');
			await pageEditorPage.addFragment('Layout Elements', 'Container');
			await pageEditorPage.addFragment(
				'Basic Components',
				'Heading',
				page.locator('[data-name="Container"]')
			);

			buttonId = await pageEditorPage.getFragmentId('Button');
			buttonFragment = pageEditorPage.getFragment(buttonId);
			containerId = await pageEditorPage.getFragmentId('Container');
			containerFragment = pageEditorPage.getFragment(containerId);
			headingId = await pageEditorPage.getFragmentId('Heading');
			headingFragment = pageEditorPage.getFragment(headingId);

			await pageEditorPage.hideFragment(containerId);

			await expect(containerFragment).not.toBeVisible();
			await expect(headingFragment).not.toBeVisible();

			await pageEditorPage.publishPage();
		});

		await test.step('Create and publish new page based on master page and check that the fragment is still hidden', async () => {
			await pagesAdminPage.goto(site.friendlyUrlPath);

			const pageName = getRandomString();

			await pagesAdminPage.createNewPage({
				name: pageName,
				template: masterName,
			});

			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pagesAdminPage.editPage(pageName);

			await expect(buttonFragment).toBeVisible();
			expect(buttonFragment.getAttribute('inert')).toBeDefined();

			await expect(headingFragment).not.toBeVisible();
			expect(headingFragment.getAttribute('inert')).toBeDefined();
		});
	}
);

test(
	'Importing master page templates',
	{
		tag: '@LPS-173150',
	},
	async ({masterPagesPage, page, site}) => {

		// Go to master page administration

		await masterPagesPage.goto(site.friendlyUrlPath);

		// Open import view

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Import'}),
			trigger: page
				.locator('.control-menu-nav-item')
				.getByLabel('Options', {exact: true}),
		});

		// Assert import view

		await expect(
			page.getByRole('heading', {name: 'Import File'})
		).toBeVisible();

		await expect(
			page.getByText(
				'Select a ZIP file containing one or multiple entries.Read more about exporting and importing page templates.'
			)
		).toBeVisible();

		expect(
			await page
				.getByText(
					'Read more about exporting and importing page templates.'
				)
				.getAttribute('href')
		).toBe(
			'https://learn.liferay.com/en/w/dxp/site-building/creating-pages/adding-pages/exporting-and-importing-page-templates'
		);

		// Import master page

		await masterPagesPage.importFile(
			'master-page-with-fragments.zip',
			path.join(__dirname, '/dependencies/master-page-with-fragments.zip')
		);

		// Assert import message

		await expect(
			page.getByRole('button', {name: '1 item was imported.'})
		).toBeVisible();

		// Upload another file

		await page.getByRole('button', {name: 'Upload Another File'}).click();

		await masterPagesPage.importFile(
			'master-page-with-widgets.zip',
			path.join(__dirname, '/dependencies/master-page-with-widgets.zip')
		);

		await expect(
			page.getByRole('button', {name: '1 item was imported.'})
		).toBeVisible();

		// Assert imported entries

		await masterPagesPage.goto(site.friendlyUrlPath);

		await expect(
			page.getByRole('link', {name: 'Master Page With Fragments'})
		).toBeVisible();

		await expect(
			page.getByRole('link', {name: 'Master Page With Widgets'})
		).toBeVisible();
	}
);

test(
	'Importing master page templates with overwrite existing entries',
	{
		tag: '@LPS-102207',
	},
	async ({masterPagesPage, page, site}) => {

		// Go to master page administration

		await masterPagesPage.goto(site.friendlyUrlPath);

		// Open import view

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Import'}),
			trigger: page
				.locator('.control-menu-nav-item')
				.getByLabel('Options', {exact: true}),
		});

		// Import master page

		await masterPagesPage.importFile(
			'master-page-with-fragments.zip',
			path.join(__dirname, '/dependencies/master-page-with-fragments.zip')
		);

		await expect(
			page.getByRole('button', {name: '1 item was imported.'})
		).toBeVisible();

		// Change thumbnail

		await masterPagesPage.goto(site.friendlyUrlPath);

		const fileChooserPromise = page.waitForEvent('filechooser');

		const pageTemplateName = 'Master Page With Fragments';

		await masterPagesPage.clickAction('Change Thumbnail', pageTemplateName);

		const iframe = page.frameLocator(
			'iframe[title="Master Page Thumbnail"]'
		);

		await expect(
			iframe.getByText('Drag & Drop Your Files or Browse to Upload')
		).toBeVisible();

		await iframe
			.getByText('Drag & Drop Your Files or Browse to Upload')
			.click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(
			path.join(__dirname, '/dependencies/image.jpg')
		);

		await iframe.getByRole('button', {exact: true, name: 'Add'}).click();

		await expect(
			page
				.locator('.card-type-asset')
				.filter({hasText: pageTemplateName})
				.locator('img')
		).toBeAttached();

		// Open import view

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.locator('.dropdown-menu')
				.getByRole('menuitem', {name: 'Import'}),
			trigger: page
				.locator('.control-menu-nav-item')
				.getByLabel('Options', {exact: true}),
		});

		// Import master page and override

		await masterPagesPage.importFile(
			'master-page-with-fragments.zip',
			path.join(__dirname, '/dependencies/master-page-with-fragments.zip')
		);

		const dialog = page.locator('.modal-dialog');

		await dialog.getByLabel('Overwrite Existing Items').check();

		await dialog.getByRole('button', {name: 'Save'}).click();

		await expect(dialog).not.toBeVisible();

		await page.getByRole('button', {name: 'Import'}).click();

		await expect(
			page.getByRole('button', {name: '1 item was imported.'})
		).toBeVisible();

		// Assert thumbnail is not present

		await masterPagesPage.goto(site.friendlyUrlPath);

		await expect(
			page
				.locator('.card-type-asset')
				.filter({hasText: pageTemplateName})
				.locator('img')
		).not.toBeAttached();
	}
);

test.describe('Allowed fragments Configuration', () => {
	test(
		'Only allowed fragments are shown in the sidebar when creating a page based on the master',
		{tag: '@LPS-102194'},
		async ({apiHelpers, masterPagesPage, page, pageEditorPage, site}) => {

			// Create a master page

			const layoutPageTemplateEntryName = getRandomString();

			const masterPage =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
					{
						groupId: site.id,
						name: layoutPageTemplateEntryName,
						type: 'master-layout',
					}
				);

			await masterPagesPage.goto(site.friendlyUrlPath);

			await masterPagesPage.editMaster(layoutPageTemplateEntryName);

			// Configure allowed fragments

			await masterPagesPage.configureAllowedFragments({
				fragmentNames: ['Button', 'Heading'],
				mode: 'select',
				prefilter: 'Basic Components',
			});

			await pageEditorPage.publishPage();

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				masterLayoutPageTemplateEntryERC:
					masterPage.externalReferenceCode,
				options: {type: 'content'},
				title: getRandomString(),
			});

			// Check that only 3 fragments are shown, Container, grid and heading

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			expect(
				page.locator('.page-editor__fragments-widgets__tab-list-item')
			).toHaveCount(4);

			expect(
				page
					.locator('.page-editor__fragments-widgets__tab-list-item')
					.nth(2)
			).toHaveText('Button');

			expect(
				page
					.locator('.page-editor__fragments-widgets__tab-list-item')
					.nth(3)
			).toHaveText('Heading');
		}
	);

	test(
		'Disabling Select New Fragments Automatically option limits the fragments that can be added to the page',
		{tag: '@LPS-102194'},
		async ({apiHelpers, masterPagesPage, page, pageEditorPage, site}) => {

			// Create a master page

			const layoutPageTemplateEntryName1 = getRandomString();

			const masterPage1 =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
					{
						groupId: site.id,
						name: layoutPageTemplateEntryName1,
						type: 'master-layout',
					}
				);

			await masterPagesPage.goto(site.friendlyUrlPath);

			await masterPagesPage.editMaster(layoutPageTemplateEntryName1);

			// Configure allowed fragments

			await masterPagesPage.configureAllowedFragments({
				fragmentNames: ['Button', 'Heading'],
				mode: 'select',
				selectNewFragmentsAutomatically: true,
			});

			await pageEditorPage.publishPage();

			// Create a master page

			const layoutPageTemplateEntryName2 = getRandomString();

			const masterPage2 =
				await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addLayoutPageTemplateEntry(
					{
						groupId: site.id,
						name: layoutPageTemplateEntryName2,
						type: 'master-layout',
					}
				);

			await masterPagesPage.goto(site.friendlyUrlPath);

			await masterPagesPage.editMaster(layoutPageTemplateEntryName2);

			// Configure allowed fragments

			await masterPagesPage.configureAllowedFragments({
				fragmentNames: ['Button', 'Heading'],
				mode: 'select',
				selectNewFragmentsAutomatically: false,
			});

			await pageEditorPage.publishPage();

			// Create a new fragment collection and fragment entry

			const fragmentCollectionName = getRandomString();

			const {fragmentCollectionId} =
				await apiHelpers.jsonWebServicesFragmentCollection.addFragmentCollection(
					{
						groupId: site.id,
						name: fragmentCollectionName,
					}
				);

			// Create custom basic fragment

			const basicFragmentEntryName = getRandomString();

			await apiHelpers.jsonWebServicesFragmentEntry.addFragmentEntry({
				fragmentCollectionId,
				groupId: site.id,
				html: '<div class="fragment-name">Fragment Example</div>',
				name: basicFragmentEntryName,
			});

			// Create a new page based on the first master page

			const layout1 = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				masterLayoutPageTemplateEntryERC:
					masterPage1.externalReferenceCode,
				options: {type: 'content'},
				title: getRandomString(),
			});

			// Check that the fragment is shown

			await pageEditorPage.goto(layout1, site.friendlyUrlPath);

			await page.getByRole('tabpanel', {name: 'Components'}).waitFor();

			expect(page.getByText(basicFragmentEntryName)).toBeVisible();

			// Create a new page based on the second master page

			const layout2 = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				masterLayoutPageTemplateEntryERC:
					masterPage2.externalReferenceCode,
				options: {type: 'content'},
				title: getRandomString(),
			});

			// Check that the fragment is shown

			await pageEditorPage.goto(layout2, site.friendlyUrlPath);

			await page.getByRole('tabpanel', {name: 'Components'}).waitFor();

			expect(page.getByText(basicFragmentEntryName)).not.toBeVisible();
		}
	);
});
