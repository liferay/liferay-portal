/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {assetPublisherPagesTest} from '../../../fixtures/assetPublisherPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {createIndividuals, generateIndividual} from '../main/utils/individuals';
import {ACPage, navigateToACPageViaURL} from '../main/utils/navigation';

export const test = mergeTests(
	apiHelpersTest,
	assetPublisherPagesTest,
	pageEditorPagesTest,
	featureFlagsTest({
		'LPD-39304': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedChannelTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

const pageTitle = 'My Page';

test('Documents visitor behavior card shows expected amount of views', async ({
	analyticsChannel: channel,
	apiHelpers,
	page,
	project,
}) => {
	await test.step('Create document events to appear within the Last 24 hours period in AC', async () => {
		const date1 = new Date();

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Document',
				assetId: '1',
				assetTitle: 'My Document 1',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date1.toISOString(),
				eventId: 'documentPreviewed',
				title: pageTitle,
				userId: '1',
			},
			{
				applicationId: 'Document',
				assetId: '1',
				assetTitle: 'My Document 1',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date1.toISOString(),
				eventId: 'documentPreviewed',
				title: pageTitle,
				userId: '1',
			},
			{
				applicationId: 'Document',
				assetId: '1',
				assetTitle: 'My Document 1',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date1.toISOString(),
				eventId: 'documentDownloaded',
				title: pageTitle,
				userId: '1',
			},
			{
				applicationId: 'Document',
				assetId: '1',
				assetTitle: 'My Document 1',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date1.toISOString(),
				eventId: 'documentDownloaded',
				title: pageTitle,
				userId: '1',
			},
			{
				applicationId: 'Document',
				assetId: '1',
				assetTitle: 'My Document 1',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date1.toISOString(),
				eventId: 'documentDownloaded',
				title: pageTitle,
				userId: '1',
			},
		]);
	});

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

	// Open the document overview from the asset list

	await page.getByRole('link', {exact: true, name: 'My Document 1'}).click();

	await expect(page.getByText('Visitors Behavior')).toBeVisible();

	await expect(
		page
			.locator('.analytics-metrics-tabs .card-tab')
			.filter({hasText: 'Downloads'})
			.locator('.metric-value')
	).toHaveText('3');

	await expect(
		page
			.locator('.analytics-metrics-tabs .card-tab')
			.filter({hasText: 'Impressions'})
			.locator('.metric-value')
	).toHaveText('2');
});

test(
	'Document overview surfaces appears-on, technology, and audience metrics',
	{
		tag: ['@LRAC-8419', '@LRAC-8403', '@LRAC-8414'],
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {

		// Seed two known individuals and one anonymous identity so the audience card splits 2 known / 1 anonymous (66.7% / 33.3%)

		const knownIndividualA = generateIndividual({name: 'ac'});
		const knownIndividualB = generateIndividual({name: 'liferay'});

		await createIndividuals({
			apiHelpers,
			individuals: [knownIndividualA, knownIndividualB],
		});

		const date = new Date();
		const anonymousIdentityId = getRandomString();

		await apiHelpers.jsonWebServicesOSBAsah.createIdentities([
			{createDate: date.toISOString(), id: anonymousIdentityId},
		]);

		// Three documentDownloaded events from three distinct viewers (Downloads = 3, all Desktop)

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Document',
				assetId: '1',
				assetTitle: 'DM AC Title',
				browserName: 'Chrome',
				canonicalUrl:
					'/web/site-name/ac-page/-/document_library/view_file/1',
				channelId: channel.id,
				dataSourceId: 0,
				deviceType: 'Desktop',
				eventDate: date.toISOString(),
				eventId: 'documentDownloaded',
				title: 'DM AC Title',
				userId: knownIndividualA.id,
			},
			{
				applicationId: 'Document',
				assetId: '1',
				assetTitle: 'DM AC Title',
				browserName: 'Chrome',
				canonicalUrl:
					'/web/site-name/ac-page/-/document_library/view_file/1',
				channelId: channel.id,
				dataSourceId: 0,
				deviceType: 'Desktop',
				eventDate: date.toISOString(),
				eventId: 'documentDownloaded',
				title: 'DM AC Title',
				userId: knownIndividualB.id,
			},
			{
				applicationId: 'Document',
				assetId: '1',
				assetTitle: 'DM AC Title',
				browserName: 'Chrome',
				canonicalUrl:
					'/web/site-name/ac-page/-/document_library/view_file/1',
				channelId: channel.id,
				dataSourceId: 0,
				deviceType: 'Desktop',
				eventDate: date.toISOString(),
				eventId: 'documentDownloaded',
				title: 'DM AC Title',
				userId: anonymousIdentityId,
			},
		]);

		// Open the document overview

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

		await page
			.getByRole('link', {exact: true, name: 'DM AC Title'})
			.click();

		// Visitors Behavior shows Downloads = 3

		await expect(
			page
				.locator('.analytics-metrics-tabs .card-tab')
				.filter({hasText: 'Downloads'})
				.locator('.metric-value')
		).toHaveText('3');

		// Asset Appears On lists the seeded document page URL

		await expect(
			page.getByRole('cell', {
				name: '/web/site-name/ac-page/-/document_library/view_file/1',
			})
		).toBeVisible();

		// Downloads by Technology surfaces the Desktop device

		await expect(
			page
				.locator('.card-root')
				.filter({hasText: 'Downloads by Technology'})
				.getByText('Desktop')
		).toBeVisible();

		// Audience card splits 66.7% known / 33.3% anonymous

		await expect(page.getByText('66.7%')).toBeVisible();
		await expect(page.getByText('33.3%')).toBeVisible();
	}
);

test(
	'Document downloads are tracked on the Documents and Media asset',
	{
		tag: ['@LRAC-10671', '@LRAC-10672', '@LRAC-10673'],
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const date = new Date();

		const documentDownloadedEvent = {
			applicationId: 'Document',
			assetId: '1',
			assetTitle: 'DM AC Title',
			canonicalUrl:
				'/web/site-name/ac-page/-/document_library/view_file/1',
			channelId: channel.id,
			eventDate: date.toISOString(),
			eventId: 'documentDownloaded',
			title: 'DM AC Title',
			userId: '1',
		};

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			documentDownloadedEvent,
			documentDownloadedEvent,
		]);

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

		await page
			.getByRole('link', {exact: true, name: 'DM AC Title'})
			.click();

		// The Visitors Behavior card reflects the two downloads

		await expect(
			page
				.locator('.analytics-metrics-tabs .card-tab')
				.filter({hasText: 'Downloads'})
				.locator('.metric-value')
		).toHaveText('2');
	}
);

test(
	'Document rating is shown on the Documents and Media asset',
	{
		tag: ['@LRAC-11450', '@LRAC-11509'],
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const documentTitle = 'DM AC Title';
		const canonicalUrl =
			'/web/site-name/ac-page/-/document_library/view_file/1';

		const date = new Date();

		// Seed a download so the document asset appears in the list, plus a
		// four-star (8.00/10) rating vote on the same document

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Document',
				assetId: '1',
				assetTitle: documentTitle,
				canonicalUrl,
				channelId: channel.id,
				eventDate: date.toISOString(),
				eventId: 'documentDownloaded',
				title: documentTitle,
				userId: '1',
			},
			{
				applicationId: 'Ratings',
				assetId: '1',
				canonicalUrl,
				channelId: channel.id,
				eventDate: date.toISOString(),
				eventId: 'VOTE',
				properties: [
					{
						name: 'className',
						value: 'com.liferay.document.library.kernel.model.DLFileEntry',
					},
					{name: 'ratingType', value: 'stars'},
					{name: 'score', value: '0.8'},
				],
				title: documentTitle,
				userId: '1',
			},
		]);

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

		await page
			.getByRole('link', {exact: true, name: documentTitle})
			.click();

		// The Visitors Behavior Rating metric reflects the four-star vote

		await expect(
			page
				.locator('.analytics-metrics-tabs .card-tab')
				.filter({hasText: 'Rating'})
				.locator('.metric-value')
		).toContainText('8/10');
	}
);
