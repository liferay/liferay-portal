/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {serverAdministrationPageTest} from '../../../fixtures/serverAdministrationPageTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {openProductMenu} from '../../../utils/productMenu';
import {pagesPagesTest} from './fixtures/pagesPagesTest';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-76864': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	globalMenuPagesTest,
	isolatedSiteTest,
	loginTest(),
	pagesAdminPagesTest,
	pagesPagesTest,
	serverAdministrationPageTest
);

const getGroovyScript = (companyId, pageName, siteId, userId) => `
	import com.liferay.petra.lang.SafeCloseable
	import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal
	import com.liferay.portal.kernel.model.LayoutConstants
	import com.liferay.portal.kernel.service.LayoutLocalServiceUtil
	import com.liferay.portal.kernel.service.ServiceContext

	def serviceContext = new ServiceContext()
	serviceContext.setScopeGroupId(${siteId})
	serviceContext.setCompanyId(${companyId})
	serviceContext.setAttribute("layout.instanceable.allowed", Boolean.TRUE)

	def safeCloseable =
		LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)

	try {
		out.println(
			LayoutLocalServiceUtil.addLayout(
				null, ${userId}, ${siteId}, false,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
				"${pageName}", "${pageName}", "",
				LayoutConstants.TYPE_EMPTY, true, "", serviceContext))
	}
	finally {
		safeCloseable.close()
	}
`;

test('Empty pages show correct label in UI and correct alert in view mode', async ({
	apiHelpers,
	globalMenuPage,
	page,
	pageTreePage,
	pagesAdminPage,
	serverAdministrationPage,
	site,
}) => {

	// Create a page of type Empty using a Groovy script that enables
	// LazyReferencingThreadLocal (required since LPD-78297)

	await globalMenuPage.goToControlPanel('Server Administration');

	const companyId = await page.evaluate(() => {
		return Liferay.ThemeDisplay.getCompanyId();
	});

	const user =
		await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
			'test@liferay.com'
		);

	const layoutName = getRandomString();

	await serverAdministrationPage.executeScript(
		getGroovyScript(companyId, layoutName, site.id, user.id)
	);

	await expect(page.getByText('"type": "empty"')).toBeVisible();

	const output = await serverAdministrationPage.getScriptOutput();

	const layout = JSON.parse(output);

	await page.goto(`/web/${site.name}`);

	// Assert label is in Control Menu Bar

	await expect(
		page.locator('.control-menu-nav-item').getByText('Empty')
	).toBeVisible();

	// Assert label is in Product Menu's Page Tree

	await openProductMenu(page);

	await pageTreePage.open();

	const pageTreeItem = page.getByRole('treeitem', {name: layoutName});

	await expect(pageTreeItem).toBeVisible();

	await expect(
		page.locator('.treeview-item').getByText('Empty').nth(0)
	).toBeVisible();

	await pageTreeItem.hover();

	await pageTreeItem.locator('button.dropdown-toggle').click();

	await expect(
		page.getByRole('menuitem', {name: 'Add Child Page'})
	).toBeVisible();
	await expect(page.getByRole('menuitem', {name: 'Delete'})).toBeVisible();
	await expect(page.getByRole('menuitem', {name: 'Configure'})).toBeHidden();
	await expect(
		page.getByRole('menuitem', {name: 'Permissions'})
	).toBeHidden();

	// Assert label is in Group Pages Portlet Miller Columns

	await pagesAdminPage.goto(site.friendlyUrlPath);

	await expect(
		page.locator('.miller-columns-item').getByText('Empty').nth(0)
	).toBeVisible();

	// Check it's a dummy page with an alert in view mode

	await page.goto(`/web/${site.name}${layout.friendlyURL}`);

	await expect(
		page.getByText(
			'This page was automatically generated during the import process to ensure the correct hierarchy of imported elements. Edit the page to configure.'
		)
	).toBeVisible();

	// Assert that the edit button in the dummy page's alert banner goes to the select layout template page

	await clickAndExpectToBeVisible({
		target: page.locator('.control-menu').getByText('Select Template'),
		trigger: page.locator('.alert').getByText('Edit Page'),
	});

	// Click first in Global templates and then come back to Basic templates

	await clickAndExpectToBeVisible({
		target: page.locator('.sheet').getByText('Global Templates'),
		timeout: 3000,
		trigger: page.getByRole('menuitem', {
			name: 'Global Templates',
		}),
	});

	await clickAndExpectToBeVisible({
		target: page.locator('.sheet').getByText('Basic Templates'),
		timeout: 3000,
		trigger: page.getByRole('menuitem', {
			name: 'Basic Templates',
		}),
	});

	// Assert that templates that should be hidden when editing an empty page are not present

	await expect(
		page.locator('.card-page-item', {hasText: 'Embedded'})
	).toBeHidden();

	await expect(
		page.locator('.card-page-item', {hasText: 'Link to URL'})
	).toBeHidden();
});
