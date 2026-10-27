/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {syncAnalyticsCloudViaAPI} from '../../analytics-settings-web/main/utils/analytics-settings';
import {captureAnalyticsEvents} from './utils/captureAnalyticsEvents';

const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-76864': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedChannelTest,
	isolatedSiteTest,
	loginAnalyticsCloudTest(),
	loginTest(),
	pageViewModePagesTest
);

test(
	'Document interaction events fire with the expected file entry properties and request common properties',
	{
		tag: ['@LRAC-8944', '@LRAC-8969'],
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		page,
		project,
		site,
		widgetPagePage,
	}) => {
		const documentTitle = 'AC Document ' + getRandomString();

		const document = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/Document.jpg')
			),
			{
				fileName: 'Document.jpg',
				title: documentTitle,
			}
		);

		// Use a widget page with the Documents and Media portlet

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'portlet'},
			title: getRandomString(),
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await widgetPagePage.addPortlet('Documents and Media');

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel,
			project,
			siteId: Number(site.id),
		});

		const capturedEvents = captureAnalyticsEvents(page);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		// Fire documentDownloaded from the list Info Panel

		await page.getByRole('button', {name: 'Toggle Info Panel'}).click();

		await page.locator('.card', {hasText: documentTitle}).first().hover();

		await page
			.locator('.card', {hasText: documentTitle})
			.first()
			.locator('input[type="checkbox"]')
			.check();

		await page
			.locator('[data-analytics-file-entry-id]')
			.getByText('Download', {exact: true})
			.click();

		await expect
			.poll(() =>
				capturedEvents.find(
					(event) => event.eventId === 'documentDownloaded'
				)
			)
			.toBeTruthy();

		const documentDownloaded = capturedEvents.find(
			(event) => event.eventId === 'documentDownloaded'
		);

		expect(documentDownloaded.properties.fileEntryId).toBe(
			String(document.id)
		);
		expect(documentDownloaded.properties.title).toBe(documentTitle);
		expect(documentDownloaded.properties.version).toBeTruthy();
		expect(documentDownloaded.properties.groupId).toBe(String(site.id));
		expect(documentDownloaded.channelId).toBe(channel.id);
		expect(documentDownloaded.dataSourceId).toBeTruthy();

		// Close the Info Panel so it does not cover the document link

		await page.getByRole('button', {name: 'Toggle Info Panel'}).click();

		await expect(
			page.getByRole('tabpanel', {name: 'Info Panel'})
		).toBeHidden();

		// Open the document to fire documentPreviewed (the file entry view fires the event on load).

		await page
			.getByRole('link', {exact: true, name: documentTitle})
			.first()
			.click();

		await expect
			.poll(() =>
				capturedEvents.find(
					(event) => event.eventId === 'documentPreviewed'
				)
			)
			.toBeTruthy();

		const documentPreviewed = capturedEvents.find(
			(event) => event.eventId === 'documentPreviewed'
		);

		expect(documentPreviewed.properties.fileEntryId).toBe(
			String(document.id)
		);
		expect(documentPreviewed.properties.title).toBe(documentTitle);
		expect(documentPreviewed.properties.version).toBeTruthy();
		expect(documentPreviewed.properties.groupId).toBe(String(site.id));
		expect(documentPreviewed.channelId).toBe(channel.id);
		expect(documentPreviewed.dataSourceId).toBeTruthy();
	}
);

test(
	'Document download event fires from the row actions menu',
	{
		tag: '@LRAC-10673',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		page,
		project,
		site,
		widgetPagePage,
	}) => {
		const documentTitle = 'AC Document ' + getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/Document.jpg')
			),
			{
				fileName: 'Document.jpg',
				title: documentTitle,
			}
		);

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'portlet'},
			title: getRandomString(),
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await widgetPagePage.addPortlet('Documents and Media');

		// The row actions menu is hidden by default; enable "Show Actions" in
		// the portlet configuration

		await widgetPagePage.clickOnAction(
			'Documents and Media',
			'Configuration'
		);

		await page
			.frameLocator('iframe[title*="Documents and Media"]')
			.getByLabel('Show Actions')
			.check();

		await widgetPagePage.saveAndClose('Documents and Media');

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel,
			project,
			siteId: Number(site.id),
		});

		const capturedEvents = captureAnalyticsEvents(page);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		// Download the document from its row actions (three dots) menu

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Download'}),
			trigger: page
				.locator('.card', {hasText: documentTitle})
				.getByLabel('Actions'),
		});

		await expect
			.poll(() =>
				capturedEvents.find(
					(event) => event.eventId === 'documentDownloaded'
				)
			)
			.toBeTruthy();
	}
);

test(
	'Document download event fires from the selection management bar',
	{
		tag: '@LRAC-10672',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		page,
		project,
		site,
		widgetPagePage,
	}) => {
		const documentTitle = 'AC Document ' + getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(
				path.join(__dirname, '/dependencies/Document.jpg')
			),
			{
				fileName: 'Document.jpg',
				title: documentTitle,
			}
		);

		const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {type: 'portlet'},
			title: getRandomString(),
		});

		await widgetPagePage.goto(layout, site.friendlyUrlPath);

		await widgetPagePage.addPortlet('Documents and Media');

		// The selection actions are hidden by default; enable "Show Actions" in
		// the portlet configuration

		await widgetPagePage.clickOnAction(
			'Documents and Media',
			'Configuration'
		);

		await page
			.frameLocator('iframe[title*="Documents and Media"]')
			.getByLabel('Show Actions')
			.check();

		await widgetPagePage.saveAndClose('Documents and Media');

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel,
			project,
			siteId: Number(site.id),
		});

		const capturedEvents = captureAnalyticsEvents(page);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

		// Select the document, then download it from the management bar

		await page.locator('.card', {hasText: documentTitle}).first().hover();

		await page
			.locator('.card', {hasText: documentTitle})
			.first()
			.locator('input[type="checkbox"]')
			.check();

		await page
			.locator('.management-bar')
			.getByRole('button', {name: 'Download'})
			.click();

		await expect
			.poll(() =>
				capturedEvents.find(
					(event) => event.eventId === 'documentDownloaded'
				)
			)
			.toBeTruthy();
	}
);
