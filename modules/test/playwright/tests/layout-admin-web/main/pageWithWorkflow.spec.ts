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
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {WorkflowPage} from '../../../pages/portal-workflow-web/WorkflowPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import createUserWithPermissions from '../../../utils/createUserWithPermissions';
import getRandomString from '../../../utils/getRandomString';
import {performLogout, performUserSwitch} from '../../../utils/performLogin';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pagesAdminPagesTest,
	pageEditorPagesTest,
	workflowPagesTest
);

async function enableSingleApproverWorkflow(
	site: Site,
	workflowPage: WorkflowPage
) {
	await workflowPage.goto(site.friendlyUrlPath);

	await workflowPage.changeWorkflow('Content Page', 'Single Approver');
}

async function createPageEditorUser(apiHelpers: ApiHelpers) {
	const company =
		await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
			'liferay.com'
		);

	return await createUserWithPermissions({
		apiHelpers,
		rolePermissions: [
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
				primaryKey: company.companyId,
				resourceName:
					'com_liferay_layout_admin_web_portlet_GroupPagesPortlet',
				scope: 1,
			},
			{
				actionIds: ['UPDATE'],
				primaryKey: company.companyId,
				resourceName: 'com.liferay.portal.kernel.model.Layout',
				scope: 1,
			},
			{
				actionIds: ['VIEW_SITE_ADMINISTRATION'],
				primaryKey: company.companyId,
				resourceName: 'com.liferay.portal.kernel.model.Group',
				scope: 1,
			},
		],
	});
}

test(
	'Page author cannot edit again after submit',
	{
		tag: '@LPS-98384',
	},
	async ({
		apiHelpers,
		page,
		pageEditorPage,
		pagesAdminPage,
		site,
		workflowPage,
		workflowTasksPage,
	}) => {

		// Create a content page

		const headingId = getRandomString();

		const headingDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([headingDefinition]),
			siteId: site.id,
			title: layoutTitle,
		});

		// Enable Single Approver workflow for Content Pages

		await enableSingleApproverWorkflow(site, workflowPage);

		// Creates a new user and login

		const user = await createPageEditorUser(apiHelpers);

		// Edit content page and submit for workflow as new user

		await performUserSwitch(page, user.alternateName);

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.editTextEditable(
			headingId,
			'element-text',
			'New editable fragment text'
		);

		await pageEditorPage.publishPage();

		// Go to view mode as new user

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByText('Heading Example')).toBeVisible();

		// Page author cannot edit page after submit as new user

		await expect(
			page.locator('.control-menu-nav-item').getByTitle('Edit', {
				exact: true,
			})
		).not.toBeVisible();

		// Assert status label as new user

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await expect(
			page.locator('.miller-columns-item').filter({hasText: layoutTitle})
		).toHaveText(/Pending/);

		// Assert edit action is not present as new user

		await page
			.locator('li', {has: page.getByText(layoutTitle)})
			.getByRole('button', {name: 'Open Page Options Menu'})
			.click();

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('menuitem', {
				exact: true,
				name: 'Preview',
			}),
			trigger: page
				.locator('li', {has: page.getByText(layoutTitle)})
				.getByRole('button', {name: 'Open Page Options Menu'}),
		});

		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'Edit',
			})
		).not.toBeVisible();

		// Preview page and assert edited text

		await performUserSwitch(page, 'test');

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.clickOnAction('Preview', layoutTitle);

		await expect(
			page.getByText('New editable fragment text')
		).toBeVisible();

		// Edit fragment

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.editTextEditable(
			headingId,
			'element-text',
			'Approved text'
		);

		// Preview page and assert edited text

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.clickOnAction('Preview', layoutTitle);

		await expect(page.getByText('Approved text')).toBeVisible();

		// Approve content page

		await workflowTasksPage.goToAssignedToMyRoles(site.friendlyUrlPath);

		await workflowTasksPage.assignToMe(layoutTitle);

		await workflowTasksPage.approve(layoutTitle);

		// Assert status label

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await expect(
			page.locator('.miller-columns-item').filter({hasText: layoutTitle})
		).not.toHaveText(/Pending/);

		// Go to view mode

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByText('Approved text')).toBeVisible();

		await performLogout(page);
	}
);

test(
	'Page author can edit content page after reject',
	{
		tag: '@LPS-98384',
	},
	async ({
		apiHelpers,
		page,
		pageEditorPage,
		pagesAdminPage,
		site,
		workflowPage,
		workflowTasksPage,
	}) => {

		// Create a content page

		const headingId = getRandomString();

		const headingDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([headingDefinition]),
			siteId: site.id,
			title: layoutTitle,
		});

		// Enable Single Approver workflow for Content Pages

		await enableSingleApproverWorkflow(site, workflowPage);

		// Creates a new user

		const user = await createPageEditorUser(apiHelpers);

		await performUserSwitch(page, user.alternateName);

		// Edit content page and submit for workflow as new user

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.editTextEditable(
			headingId,
			'element-text',
			'Rejected text'
		);

		await pageEditorPage.publishPage();

		// Reject content page

		await performUserSwitch(page, 'test');

		await workflowTasksPage.goToAssignedToMyRoles(site.friendlyUrlPath);

		await workflowTasksPage.assignToMe(layoutTitle);

		await workflowTasksPage.reject(layoutTitle);

		// Edit content page and submit for workflow

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.editTextEditable(
			headingId,
			'element-text',
			'Modified text'
		);

		// Go to view mode and assert default text

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByText('Heading Example')).toBeVisible();

		// Preview page and assert modified text

		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.clickOnAction('Preview', layoutTitle);

		await expect(page.getByText('Modified text')).toBeVisible();
		await expect(page.getByText('Rejected text')).not.toBeVisible();

		await performLogout(page);
	}
);
