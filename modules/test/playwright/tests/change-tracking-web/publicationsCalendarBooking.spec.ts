/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {calendarPagesTest} from '../../fixtures/calendarPagesTest';
import {changeTrackingPagesTest} from '../../fixtures/changeTrackingPagesTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../fixtures/pagesAdminPagesTest';
import {getRandomInt} from '../../utils/getRandomInt';
import getRandomString from '../../utils/getRandomString';
import getPageDefinition from '../layout-content-page-editor-web/utils/getPageDefinition';
import getWidgetDefinition from '../layout-content-page-editor-web/utils/getWidgetDefinition';

export const test = mergeTests(
	apiHelpersTest,
	changeTrackingPagesTest,
	calendarPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	pagesAdminPagesTest,
	pageEditorPagesTest,
	isolatedSiteTest,
	loginTest()
);

test('Can publish a Publication containing an added Calendar Event', async ({
	apiHelpers,
	calendarWidgetPage,
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
	site,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	const layout = await apiHelpers.headlessDelivery.createSitePage({
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

	await calendarWidgetPage.setCalendarWidgetConfiguration(
		'Europe/Paris',
		false
	);

	await pageEditorPage.publishPage();

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

	const title = getRandomInt().toString();

	await calendarWidgetPage.addEvent({
		allDay: true,
		publishEvent: true,
		title,
	});

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await page.reload();

	await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

	await calendarWidgetPage.clickEvent(title);

	await expect(calendarWidgetPage.calendarWidget).toContainText(title);
});
