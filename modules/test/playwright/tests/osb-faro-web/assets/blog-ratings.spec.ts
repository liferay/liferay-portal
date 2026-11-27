/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {syncAnalyticsCloudViaAPI} from '../../analytics-settings-web/main/utils/analytics-settings';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';
import {ACPage, navigateToACPageViaURL} from '../main/utils/navigation';
import {closeSessions} from '../main/utils/sessions';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-76864': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedChannelTest,
	isolatedSiteTest,
	instanceSettingsPagesTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

test(
	'Blog rating in AC returns to 0.00/10 after the only vote is revoked',
	{
		tag: ['@LRAC-10601', '@LRAC-7848'],
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		instanceSettingsPage,
		page,
		project,
		site,
	}) => {
		test.setTimeout(120000);

		// Switch the instance-wide blogs rating type to stars (Instance Settings > Community Tools > Ratings)

		await instanceSettingsPage.goToInstanceSetting(
			'Community Tools',
			'Ratings'
		);

		await page
			.getByTitle('com.liferay.blogs.model.-blogs-entry_-ratings-type', {
				exact: true,
			})
			.selectOption('stars');

		page.once('dialog', (dialog) => dialog.accept());

		await page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(page);

		const blogsEntryName = 'Blogs AC Title';

		await apiHelpers.headlessDelivery.postBlog(site.id, {
			articleBody: 'Body',
			headline: blogsEntryName,
		});

		// Build a content page with the Blogs portlet so the entry shows up at a known URL

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getWidgetDefinition({
					id: getRandomString(),
					widgetName: 'com_liferay_blogs_web_portlet_BlogsPortlet',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel,
			project,
			siteId: Number(site.id),
		});

		await page.goto(
			`/web/${site.name}/${layout.friendlyUrlPath}/-/blogs/blogs-ac-title`
		);

		// Vote 4 stars (DXP scale 0-5 shows 4.0; AC scale 0-10 shows 8/10)

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: '4'}),
			trigger: page.locator('.ratings-stars-dropdown-toggle'),
		});

		await expect(page.locator('.ratings-stars-average-text')).toContainText(
			'4.0'
		);

		// Flush the AC ingestion pipeline so the blogRated event is processed

		await closeSessions(apiHelpers, page);

		await page.waitForTimeout(30000);

		// AC asset blog overview reflects the 8/10 rating

		await navigateToACPageViaURL({
			acPage: ACPage.assetPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Last 24 hours'}),
			trigger: page.getByRole('button', {name: 'Last 30 days'}),
		});

		await clickAndExpectToBeVisible({
			target: page.getByText('8/10'),
			trigger: page.getByRole('link', {
				exact: true,
				name: blogsEntryName,
			}),
		});

		// Revoke the vote (DXP shows -, AC scale should return to 0/10)

		await page.goto(
			`/web/${site.name}/${layout.friendlyUrlPath}/-/blogs/blogs-ac-title`
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'Delete'}),
			trigger: page.locator('.ratings-stars-dropdown-toggle'),
		});

		await expect(page.locator('.ratings-stars-average-text')).toContainText(
			'0.0'
		);

		// Flush the AC ingestion pipeline so the rating delete is processed

		await closeSessions(apiHelpers, page);

		await page.waitForTimeout(30000);

		await navigateToACPageViaURL({
			acPage: ACPage.assetPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Last 24 hours'}),
			trigger: page.getByRole('button', {name: 'Last 30 days'}),
		});

		await clickAndExpectToBeVisible({
			target: page.getByText('0/10'),
			trigger: page.getByRole('link', {
				exact: true,
				name: blogsEntryName,
			}),
		});
	}
);
