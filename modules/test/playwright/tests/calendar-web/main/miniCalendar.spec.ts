/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {calendarPagesTest} from '../../../fixtures/calendarPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {liferayConfig} from '../../../liferay.config';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import createUserWithPermissions from '../../../utils/createUserWithPermissions';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch} from '../../../utils/performLogin';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

export const test = mergeTests(
	accountSettingsPagesTest,
	apiHelpersTest,
	calendarPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

let layout: Layout;

test.afterEach(async ({page, pageEditorPage, site}) => {
	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page
			.locator('.dropdown-menu')
			.getByRole('menuitem', {name: 'Delete'}),
		trigger: page
			.locator('.control-menu-nav-item')
			.getByLabel('Options', {exact: true}),
	});

	await page.getByRole('button', {name: 'Delete'}).click();
});

test.beforeEach(async ({apiHelpers, page, pageEditorPage, site}) => {
	layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getWidgetDefinition({
				id: getRandomString(),
				widgetName: 'com_liferay_calendar_web_portlet_CalendarPortlet',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	await pageEditorPage.publishPage();

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);
});

test('can move between mini calendar gridcell child buttons using arrow keys', async ({
	calendarWidgetPage,
	page,
}) => {
	await calendarWidgetPage.unhideSidebar();

	await calendarWidgetPage.miniCalendarGrid.focus();

	const gridMovements = [
		{date: '1', key: 'ArrowRight'},
		{date: '2', key: 'ArrowRight'},
		{date: '9', key: 'ArrowDown'},
		{date: '8', key: 'ArrowLeft'},
	];

	for (const {date, key} of gridMovements) {
		await page.keyboard.press(key);

		await expect(
			page.getByRole('button', {exact: true, name: date})
		).toBeFocused();

		await expect(
			page.getByRole('button', {exact: true, name: date})
		).toHaveAttribute('tabIndex', '0');
	}
});

test('ensure that mini calendar accessibility properties are maintained after changing months', async ({
	calendarWidgetPage,
}) => {
	await calendarWidgetPage.unhideSidebar();

	await calendarWidgetPage.miniCalendarNextMonthButton.click();

	await expect(calendarWidgetPage.miniCalendarBase).toHaveAttribute(
		'role',
		'dialog'
	);

	await expect(calendarWidgetPage.miniCalendarHeaderLabel).toHaveAttribute(
		'role',
		'paragraph'
	);

	await expect(calendarWidgetPage.miniCalendarHeaderLabel).toHaveAttribute(
		'aria-live',
		'polite'
	);
});

test('ensure that next and previous buttons acessibility label in the mini calendar are translated to portuguese', async ({
	accountSettingsPage,
	apiHelpers,
	calendarWidgetPage,
	page,
	pageEditorPage,
	site,
}) => {
	let company;

	let defaultUser;

	let user;

	try {
		company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		defaultUser =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'test@liferay.com'
			);

		user = await createUserWithPermissions({
			apiHelpers,
			rolePermissions: [
				{
					actionIds: ['VIEW'],
					primaryKey: company.companyId,
					resourceName: 'com.liferay.portal.kernel.model.Layout',
					scope: 1,
				},
			],
		});

		await performUserSwitch(page, user.alternateName);

		await page.goto(liferayConfig.environment.baseUrl);

		await page.locator('button[data-qa-id="userPersonalMenu"]').click();

		await page.getByRole('menuitem', {name: 'Account Settings'}).click();

		await accountSettingsPage.selectAccountLanguage({languageId: 'pt_BR'});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await expect(
			calendarWidgetPage.miniCalendarNextMonthButton
		).toHaveAccessibleName('Ir para o próximo mês');

		await expect(
			calendarWidgetPage.miniCalendarPastMonthButton
		).toHaveAccessibleName('Ir para o mês anterior');
	}
	finally {
		await page.goto('en');

		await page.locator('button[data-qa-id="userPersonalMenu"]').click();

		await page.getByRole('menuitem', {name: 'Account Settings'}).click();

		await accountSettingsPage.selectAccountLanguage({languageId: 'en_US'});

		await page.goto(liferayConfig.environment.baseUrl);

		await performUserSwitch(page, defaultUser.alternateName);

		await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user.id));
	}
});
