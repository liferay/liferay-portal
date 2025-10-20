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
import createUserWithPermissions from '../../../utils/createUserWithPermissions';
import getRandomString from '../../../utils/getRandomString';
import {hoverAndExpectToBeVisible} from '../../../utils/hoverAndExpectToBeVisible';
import {performLogout, performUserSwitch} from '../../../utils/performLogin';
import getContainerDefinition from './utils/getContainerDefinition';
import getFragmentDefinition from './utils/getFragmentDefinition';
import getPageDefinition from './utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageManagementSiteTest,
	pageEditorPagesTest
);

test(
	'User with Update - Basic and Update - Advanced Options permission cannot define custom css of fragment',
	{
		tag: ['@LPS-147787', '@LPS-136411'],
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Add new user with 'Update - Advanced Options' and 'Update - Basic' permissions

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const user = await createUserWithPermissions({
			apiHelpers,
			rolePermissions: [
				{
					actionIds: ['UPDATE_LAYOUT_ADVANCED_OPTIONS'],
					primaryKey: company.companyId,
					resourceName: 'com.liferay.portal.kernel.model.Layout',
					scope: 1,
				},
				{
					actionIds: ['UPDATE_LAYOUT_BASIC'],
					primaryKey: company.companyId,
					resourceName: 'com.liferay.portal.kernel.model.Layout',
					scope: 1,
				},
			],
		});

		// Create page

		const containerId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getContainerDefinition({id: containerId}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Go to edit mode

		await performUserSwitch(page, user.alternateName);

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Assert configuration is present in general tab

		await pageEditorPage.selectFragment(containerId);

		await expect(page.getByLabel('Width', {exact: true})).toBeAttached();

		// Assert color is present in styles tab

		await pageEditorPage.goToConfigurationTab('Styles');

		await expect(page.getByText('Background')).not.toBeAttached();
		await expect(page.getByText('Borders')).not.toBeAttached();
		await expect(page.getByText('Effects')).not.toBeAttached();
		await expect(page.getByText('Text')).not.toBeAttached();

		// Assert in advanced tab

		await pageEditorPage.goToConfigurationTab('Advanced');

		await expect(page.getByLabel('HTML Tag', {exact: true})).toBeAttached();

		await expect(
			page.getByLabel('Custom CSS', {exact: true})
		).not.toBeAttached();

		await performLogout(page);
	}
);

test(
	'User with Update - Limited permission does not have access to advance tab',
	{
		tag: '@LPS-147787',
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Add new user with 'Update - Limited' permission

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const user = await createUserWithPermissions({
			apiHelpers,
			rolePermissions: [
				{
					actionIds: ['UPDATE_LAYOUT_LIMITED'],
					primaryKey: company.companyId,
					resourceName: 'com.liferay.portal.kernel.model.Layout',
					scope: 1,
				},
			],
		});

		// Create page

		const containerId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getContainerDefinition({id: containerId}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		// Go to edit mode

		await performUserSwitch(page, user.alternateName);

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Assert configuration is present in general tab

		await pageEditorPage.selectFragment(containerId);

		await expect(page.getByLabel('Width', {exact: true})).toBeAttached();

		// Assert color is present in styles tab

		await pageEditorPage.goToConfigurationTab('Styles');

		await expect(
			page
				.getByLabel('Background ColorImage')
				.getByLabel('Color', {exact: true})
		).toBeAttached();

		// Assert advanced tab is not present

		await expect(
			page.getByRole('tab', {exact: true, name: 'Advanced'})
		).not.toBeAttached();

		await performLogout(page);
	}
);

test(
	'User without permissions for Journal can not access the web content editor from page editor',
	{
		tag: '@LPS-96795',
	},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create user with correct permissions

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

		// Create a page and go to edit mode as new user

		const headingId = getRandomString();

		const headingFragment = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([headingFragment]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await performUserSwitch(page, user.alternateName);

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Map the editable

		await pageEditorPage.selectEditable(headingId, 'element-text');

		await pageEditorPage.setMappingConfiguration({
			mapping: {
				entity: 'Web Content',
				entry: 'Animal 01 - Dogs and Cats categories',
				field: 'Title',
				folder: 'Animals',
			},
		});

		// Go to contents panel and check user can not edit the web content

		const checkWebContentEdition = async (allowed: boolean) => {
			await pageEditorPage.goToSidebarTab('Page Content');

			const panel = page.getByLabel('Page Content Panel');

			const content = panel.locator(
				'.page-editor__page-contents__page-content'
			);

			await hoverAndExpectToBeVisible({
				autoClick: true,
				target: content.getByTitle('Open Actions Menu'),
				trigger: content,
			});

			await expect(
				page.getByRole('menuitem', {name: 'View Usages'})
			).toBeVisible();

			if (allowed) {
				await expect(
					page.getByRole('menuitem', {name: 'Edit'})
				).toBeVisible();
			}
			else {
				await expect(
					page.getByRole('menuitem', {name: 'Edit'})
				).not.toBeVisible();
			}
		};

		await checkWebContentEdition(false);

		await pageEditorPage.publishPage();

		await performUserSwitch(page, 'test');

		// Do it as user with permissions

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await checkWebContentEdition(true);

		await performLogout(page);
	}
);
