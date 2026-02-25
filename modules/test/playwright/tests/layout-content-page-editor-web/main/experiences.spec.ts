/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import createUserWithPermissions from '../../../utils/createUserWithPermissions';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch} from '../../../utils/performLogin';
import getFragmentDefinition from './utils/getFragmentDefinition';
import getPageDefinition from './utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-78863': {enabled: true, system: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest
);

test('Allows renaming an experience', async ({
	apiHelpers,
	page,
	pageEditorPage,
	site,
}) => {

	// Create page and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	// Create experience and rename it

	await pageEditorPage.createExperience('E1');

	await expect(page.getByLabel('Experience: E1')).toBeVisible();

	await pageEditorPage.editExperienceName('E1', 'E1 edited');

	await expect(page.getByLabel('Experience: E1 edited')).toBeVisible();
});

test('Allows changing the segment of an existing experience', async ({
	apiHelpers,
	page,
	pageEditorPage,
	site,
}) => {

	// Create page and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	// Create experience and rename it

	await pageEditorPage.createExperience('E1');

	await expect(page.getByLabel('Experience: E1')).toBeVisible();

	await pageEditorPage.editExperienceSegment('E1', 'S1');

	await page.locator('.page-editor__experience-selector').click();

	const row = page.locator('.dropdown-menu__experience', {hasText: 'E1'});

	await expect(row).toContainText('AudienceS1');
});

test('Creates new experiences as expected', async ({
	apiHelpers,
	page,
	pageEditorPage,
	site,
}) => {

	// Create a page with a Heading fragment and go to edit mode

	const headingId = getRandomString();
	const headingDefinition = getFragmentDefinition({
		id: headingId,
		key: 'BASIC_COMPONENT-heading',
	});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([headingDefinition]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	// Create new experience and check it's the last one and inactive

	await pageEditorPage.createExperience('E1');

	await expect(page.getByLabel('Experience: E1')).toBeVisible();

	await pageEditorPage.openExperienceSelector();

	const row = page.locator('.dropdown-menu__experience').last();

	await expect(row).toContainText('E1');
	await expect(row).toContainText('Inactive');

	await pageEditorPage.closeExperienceSelector();

	// Edit heading text in E1 experience

	await pageEditorPage.editTextEditable(headingId, 'element-text', 'E1 Text');

	await pageEditorPage.publishPage();

	// Go to view mode of page and check it displays the Default experience text

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

	await expect(page.getByText('E1 Text')).not.toBeAttached();

	await expect(page.getByText('Heading Example')).toBeVisible();
});

test('Keeps modal open when canceling segment creation', async ({
	apiHelpers,
	page,
	pageEditorPage,
	site,
}) => {

	// Create page and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	// Open experience creation modal and go to create new segment

	await pageEditorPage.experienceSelector.click();

	await page.getByText('Select Experience').waitFor();
	await page.getByLabel('New Experience').click();

	await page.getByText('New Segment').waitFor();
	await page.getByText('New Segment').click();

	// Cancel segment creation and check we are back to page editor

	await page.getByText('No Conditions yet').waitFor();

	await page.getByText('Cancel', {exact: true}).click();

	await expect(
		page.locator('.modal-title', {hasText: 'New Experience'})
	).toBeVisible();
});

test('Styles changes affect to current experience only', async ({
	apiHelpers,
	pageEditorPage,
	site,
}) => {

	// Create a page with a Heading fragment and go to edit mode

	const headingId = getRandomString();
	const headingDefinition = getFragmentDefinition({
		id: headingId,
		key: 'BASIC_COMPONENT-heading',
	});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([headingDefinition]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	// Change heading margin top

	await pageEditorPage.changeFragmentSpacing(headingId, 'Margin Top', '2');

	expect(
		await pageEditorPage.getFragmentStyle({
			fragmentId: headingId,
			isTopperStyle: true,
			style: 'marginTop',
		})
	).toBe('8px');

	// Create new experience and change margin top again

	await pageEditorPage.createExperience('E1');

	await pageEditorPage.changeFragmentSpacing(
		headingId,
		'Margin Top',
		'5',
		'px'
	);

	expect(
		await pageEditorPage.getFragmentStyle({
			fragmentId: headingId,
			isTopperStyle: true,
			style: 'marginTop',
		})
	).toBe('5px');

	// Change to Default experience again and check previous margin

	await pageEditorPage.switchExperience('Default');

	expect(
		await pageEditorPage.getFragmentStyle({
			fragmentId: headingId,
			isTopperStyle: true,
			style: 'marginTop',
		})
	).toBe('8px');
});

test('Allows duplicating an experience', async ({
	apiHelpers,
	page,
	pageEditorPage,
	site,
}) => {

	// Create a page and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	// Create new experience and duplicate it

	await pageEditorPage.createExperience('E1');

	await pageEditorPage.duplicateExperience('E1');

	await expect(page.getByLabel('Experience: Copy of E1')).toBeVisible();

	await pageEditorPage.openExperienceSelector();

	const row = page.locator('.dropdown-menu__experience').last();

	await expect(row).toContainText('Copy of E1');
	await expect(row).toContainText('Inactive');
});

test(
	'Allows creating experiences with different fragments',
	{
		tag: '@LPS-86285',
	},
	async ({apiHelpers, pageEditorPage, site}) => {

		// Create a page with a Heading fragment and go to edit mode

		const headingId = getRandomString();
		const headingDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([headingDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Create new experience and remove the fragment

		await pageEditorPage.createExperience('E1');

		await pageEditorPage.removeFragment(headingId);

		// Change to Default experience again and check the fragment is present

		await pageEditorPage.switchExperience('Default');

		await expect(pageEditorPage.getFragment(headingId)).toBeVisible();
	}
);

test(
	'Allows creating experiences with different fragments mapped to different content',
	{
		tag: ['@LPS-113248', '@LPS-142290'],
	},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a page and go to edit mode

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition(),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Create new experience, add heading fragment and map it

		await pageEditorPage.createExperience('E1');

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		const headingId1 = await pageEditorPage.getFragmentId('Heading');

		await pageEditorPage.selectEditable(headingId1, 'element-text');

		await pageEditorPage.setMappingConfiguration({
			mapping: {
				entity: 'Web Content',
				entry: 'Animal 01 - Dogs and Cats categories',
				field: 'Title',
				folder: 'Animals',
			},
		});

		// Change to Default experience, add heading fragment and map it

		await pageEditorPage.switchExperience('Default');

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		const headingId2 = await pageEditorPage.getFragmentId('Heading');

		await pageEditorPage.selectEditable(headingId2, 'element-text');

		await pageEditorPage.setMappingConfiguration({
			mapping: {
				entity: 'Web Content',
				entry: 'Animal 02 - Dogs category',
				field: 'Title',
				folder: 'Animals',
			},
		});

		// Publish and go to view mode

		await pageEditorPage.publishPage();

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Assert it displays the Default experience mapping

		await expect(
			page.getByText('Animal 01 - Dogs and Cats categories')
		).not.toBeVisible();

		await expect(
			page.getByText('Animal 02 - Dogs category')
		).toBeAttached();

		// Assert it displays the new experience mapping

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {
				name: 'E1 Segment: Anyone Inactive',
			}),
			trigger: page.getByLabel('Experience Selector'),
		});

		await expect(
			page.getByText('Animal 01 - Dogs and Cats categories')
		).toBeVisible();

		await expect(
			page.getByText('Animal 02 - Dogs category')
		).not.toBeAttached();
	}
);

test(
	'Allows creating experiences with the same non-instantiable widget',
	{
		tag: '@LPS-96828',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Create new experience and add the fragment

		await pageEditorPage.createExperience('E1');

		await pageEditorPage.addWidget('Tools', 'Sign In');

		await expect(page.getByText('You are signed in')).toBeVisible();

		// Change to Default experience again and check we can add the fragment again

		await pageEditorPage.switchExperience('Default');

		await pageEditorPage.addWidget('Tools', 'Sign In');

		await expect(page.getByText('You are signed in')).toBeVisible();
	}
);

test(
	'Allows editing and deleting an experience',
	{
		tag: '@LPS-90586',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page and go to edit mode

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Create new experience

		await pageEditorPage.createExperience('E1');

		// Edit it

		await pageEditorPage.editExperienceName('E1', 'E2');

		// Delete it

		await pageEditorPage.deleteExperience('E2');

		await pageEditorPage.openExperienceSelector();

		await expect(
			page.locator('.dropdown-menu__experience', {
				hasText: 'E2',
			})
		).not.toBeVisible();

		// Validate if all experiences were deleted, only the Default experience will appear

		await expect(
			page.locator('.dropdown-menu__experience', {
				hasText: 'Default',
			})
		).not.toBeVisible();
	}
);

test(
	'Allows prioritize and deprioritize experiences',
	{
		tag: '@LPS-90585',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a page and go to edit mode

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Create new experience

		await pageEditorPage.createExperience('E1');

		// Prioritize priority

		await pageEditorPage.openExperienceSelector();

		const defaultExperience = page.locator('.dropdown-menu__experience', {
			hasText: 'Default',
		});

		const experience = page.locator('.dropdown-menu__experience', {
			hasText: 'E1',
		});

		const listItems = page.locator('ul.list-group');

		await experience
			.getByLabel('Prioritize Experience', {exact: true})
			.click();

		await expect(listItems.locator('li').last()).toContainText('Default');

		await expect(defaultExperience).toContainText('Inactive');

		await expect(listItems.locator('li').first()).toContainText('E1');

		await expect(experience).toContainText('Active');

		// Decrease priority

		await experience
			.getByLabel('Deprioritize Experience', {exact: true})
			.click();

		await expect(listItems.locator('li').first()).toContainText('Default');

		await expect(defaultExperience).toContainText('Active');

		await expect(listItems.locator('li').last()).toContainText('E1');

		await expect(experience).toContainText('Inactive');
	}
);

test(
	'Preview current experience in a new tab',
	{
		tag: '@LPS-153367',
	},
	async ({apiHelpers, context, page, pageEditorPage, site}) => {

		// Create a page with a Heading fragment and go to edit mode

		const headingId = getRandomString();
		const headingDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([headingDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Create new experience and check it's the last one and inactive

		await pageEditorPage.createExperience('E1');

		// Edit heading text in E1 experience

		await pageEditorPage.editTextEditable(
			headingId,
			'element-text',
			'E1 Text'
		);

		// Preview experience in a new tab

		await expect(async () => {
			const pagePromise = context.waitForEvent('page');

			await clickAndExpectToBeVisible({
				target: page.getByRole('menuitem', {
					name: 'Preview in a New Tab',
				}),
				trigger: page
					.locator('.control-menu-nav-item')
					.getByLabel('Options', {exact: true}),
			});

			await page
				.getByRole('menuitem', {
					name: 'Preview in a New Tab',
				})
				.click({timeout: 1000});

			const newTab = await pagePromise;

			await newTab.waitForLoadState();

			await expect(newTab.getByText('E1 Text')).toBeVisible({
				timeout: 1000,
			});
		}).toPass({timeout: 5000});
	}
);

test(
	'Users without edit segments entry permissions cannot create new segments',
	{
		tag: '@LPS-90588',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create a new page

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			siteId: site.id,
			title: getRandomString(),
		});

		// Go to edit and assert New Segment button is visible

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.openExperienceSelector();

		await page.getByLabel('New Experience').click();

		await expect(
			page.getByRole('button', {name: 'New Segment'})
		).toBeVisible();

		// Switch to a new user with update page permissions and without edit segments entry permissions

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const user = await createUserWithPermissions({
			apiHelpers,
			rolePermissions: [
				{
					actionIds: ['UPDATE'],
					primaryKey: company.companyId,
					resourceName: 'com.liferay.portal.kernel.model.Layout',
					scope: 1,
				},
			],
		});

		await performUserSwitch(page, user.alternateName);

		// Go to edit and assert New Segment button is not visible

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.openExperienceSelector();

		await page.getByLabel('New Experience').click();

		await expect(
			page.getByRole('button', {name: 'New Segment'})
		).not.toBeVisible();
	}
);
