/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../liferay.config';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {openProductMenu} from '../../../utils/productMenu';
import {pagesPagesTest} from './fixtures/pagesPagesTest';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pagesAdminPagesTest,
	pagesPagesTest
);

async function addEmptyPage({
	apiHelpers,
	name,
	siteId,
}: {
	apiHelpers: ApiHelpers;
	name: string;
	siteId: string;
}): Promise<Layout> {
	const serviceContext = {
		attributes: {
			'layout.instanceable.allowed': true,
		},
		scopeGroupId: siteId,
	};

	const urlSearchParams = new URLSearchParams();

	urlSearchParams.append('externalReferenceCode', null);
	urlSearchParams.append('groupId', siteId);
	urlSearchParams.append('privateLayout', 'false');
	urlSearchParams.append('parentLayoutId', '0');
	urlSearchParams.append('name', name);
	urlSearchParams.append('title', name);
	urlSearchParams.append('description', '');
	urlSearchParams.append('type', 'empty');
	urlSearchParams.append('hidden', 'true');
	urlSearchParams.append('friendlyURL', '');
	urlSearchParams.append('serviceContext', JSON.stringify(serviceContext));

	const layout = await apiHelpers.post(
		`${liferayConfig.environment.baseUrl}/api/jsonws/layout/add-layout`,
		{
			data: urlSearchParams.toString(),
			failOnStatusCode: true,
			headers: await apiHelpers.getJSONWebServicesHeaders(),
		}
	);

	return layout;
}

test('Empty pages show correct label in UI and correct alert in view mode', async ({
	apiHelpers,
	page,
	pageTreePage,
	pagesAdminPage,
	site,
}) => {

	// Create a page of type Empty

	const layoutName = getRandomString();

	const layout = await addEmptyPage({
		apiHelpers,
		name: layoutName,
		siteId: site.id,
	});

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
