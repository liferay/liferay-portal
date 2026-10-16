/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {performLogout} from '../../../utils/performLogin';
import createSiteTemplateWithMessageBoards from './utils/createSiteTemplateWithMessageBoards';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-105225': {enabled: true},
	}),
	loginTest(),
	pageEditorPagesTest
);

const DEFAULT_WIDGET_PERMISSIONS = [
	{
		addToPage: false,
		configuration: false,
		permissions: false,
		role: 'guest',
		view: true,
	},
	{
		addToPage: true,
		configuration: true,
		permissions: true,
		role: 'owner',
		view: true,
	},
	{
		addToPage: true,
		configuration: false,
		permissions: false,
		role: 'power-user',
		view: false,
	},
	{
		addToPage: false,
		configuration: false,
		permissions: false,
		role: 'site-member',
		view: true,
	},
	{
		addToPage: true,
		configuration: false,
		permissions: false,
		role: 'user',
		view: false,
	},
];

async function assertDefaultWidgetPermissions(
	page: Page,
	pageEditorPage: PageEditorPage,
	widgetId: string
) {
	await pageEditorPage.selectFragment(widgetId);

	await pageEditorPage.clickFragmentOption(widgetId, 'Permissions');

	const permissionsFrame = page.frameLocator('iframe[title="Permissions"]');

	for (const {
		addToPage,
		configuration,
		permissions,
		role,
		view,
	} of DEFAULT_WIDGET_PERMISSIONS) {
		for (const [actionId, checked] of [
			['ADD_TO_PAGE', addToPage],
			['CONFIGURATION', configuration],
			['PERMISSIONS', permissions],
			['VIEW', view],
		] as const) {
			const checkbox = permissionsFrame.locator(
				`#${role}_ACTION_${actionId}, #${role}_PRESELECTED_${actionId}`
			);

			if (checked) {
				await expect(checkbox).toBeChecked();
			}
			else {
				await expect(checkbox).not.toBeChecked();
			}
		}
	}

	await page
		.locator('.modal-header')
		.getByLabel('Close', {exact: true})
		.click();
}

test(
	'Widget permissions inherited from a Site Template are applied in the Site',
	{tag: '@LPS-121422'},
	async ({apiHelpers, page, pageEditorPage}) => {
		test.slow();

		// Create a Site from a Site Template with a Message Boards widget

		const {layout, layoutSetPrototype} =
			await createSiteTemplateWithMessageBoards({
				apiHelpers,
				page,
				pageEditorPage,
			});

		const site = await apiHelpers.headlessAdminSite.postSite({
			name: 'Site-' + getRandomString(),
			templateKey: String(layoutSetPrototype.layoutSetPrototypeId),
			templateType: 'site-template',
		});

		const pageURL = `/web${site.friendlyUrlPath}${layout.friendlyURL}`;

		await expect(async () => {
			await page.goto(pageURL);

			await expect(page.locator('.portlet-message-boards')).toBeVisible({
				timeout: 2000,
			});
		}).toPass({timeout: 30000});

		// The Message Boards widget keeps its default permissions

		await page.goto(`${pageURL}?p_l_mode=edit`);

		const widgetId = await pageEditorPage.getFragmentId('Message Boards');

		await assertDefaultWidgetPermissions(page, pageEditorPage, widgetId);

		// A guest can view a thread in the Message Boards widget

		const articleBody = getRandomString();
		const headline = 'Thread-' + getRandomString();

		await apiHelpers.headlessDelivery.postMessageBoardThread({
			articleBody,
			headline,
			siteId: site.id,
			viewableBy: 'Anyone',
		});

		await performLogout(page);

		await page.goto(pageURL);

		await clickAndExpectToBeVisible({
			target: page.getByText(articleBody),
			trigger: page.getByRole('link', {name: headline}).first(),
		});

		// Remove the view permission of the Message Boards widget from the Guest role

		await performLogin(page, 'test');

		await page.goto(`${pageURL}?p_l_mode=edit`);

		await pageEditorPage.changeWidgetPermission(
			widgetId,
			'#guest_ACTION_VIEW',
			false
		);

		await pageEditorPage.publishPage();

		// A guest cannot access the Message Boards widget

		await performLogout(page);

		await page.goto(pageURL);

		await expect(
			page.getByText(
				'You do not have the roles required to access this portlet.'
			)
		).toBeVisible();
	}
);
