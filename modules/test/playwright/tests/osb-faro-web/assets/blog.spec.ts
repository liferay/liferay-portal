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
import {Nanites, runNanites} from '../main/utils/nanites';
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

test('View all blogs in the property in assets', async ({
	analyticsChannel: channel,
	apiHelpers,
	page,
	project,
}) => {
	const blogTitles = ['My Blog 1', 'My Blog 2'];

	// Seed a view for each blog within the Last 24 hours period

	const date = new Date();

	await apiHelpers.jsonWebServicesOSBAsah.createEvents(
		blogTitles.map((blogTitle, index) => ({
			applicationId: 'Blog',
			assetId: String(index + 1),
			assetTitle: blogTitle,
			canonicalUrl: 'https://www.liferay.com',
			channelId: channel.id,
			eventDate: date.toISOString(),
			eventId: 'blogViewed',
			title: pageTitle,
			userId: '1',
		}))
	);

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

	for (const blogTitle of blogTitles) {
		await expect(
			page.getByRole('link', {exact: true, name: blogTitle})
		).toBeVisible();
	}
});

test(
	'Blog overview surfaces appears-on, cards, comments, and audience metrics',
	{
		tag: [
			'@LRAC-8387',
			'@LRAC-8374',
			'@LRAC-8113',
			'@LRAC-12347',
			'@LRAC-8351',
		],
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

		// Four blogViewed events from three distinct viewers (views = 4) plus one blogCommented (comments = 1)

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Blog',
				assetId: '1',
				assetTitle: 'Blogs AC Title',
				canonicalUrl: '/web/site-name/ac-page/-/blogs/blogs-ac-title',
				channelId: channel.id,
				dataSourceId: 0,
				eventDate: date.toISOString(),
				eventId: 'blogViewed',
				title: pageTitle,
				userId: knownIndividualA.id,
			},
			{
				applicationId: 'Blog',
				assetId: '1',
				assetTitle: 'Blogs AC Title',
				canonicalUrl: '/web/site-name/ac-page/-/blogs/blogs-ac-title',
				channelId: channel.id,
				dataSourceId: 0,
				eventDate: date.toISOString(),
				eventId: 'blogViewed',
				title: pageTitle,
				userId: knownIndividualB.id,
			},
			{
				applicationId: 'Blog',
				assetId: '1',
				assetTitle: 'Blogs AC Title',
				canonicalUrl: '/web/site-name/ac-page/-/blogs/blogs-ac-title',
				channelId: channel.id,
				dataSourceId: 0,
				eventDate: date.toISOString(),
				eventId: 'blogViewed',
				title: pageTitle,
				userId: anonymousIdentityId,
			},
			{
				applicationId: 'Blog',
				assetId: '1',
				assetTitle: 'Blogs AC Title',
				canonicalUrl: '/web/site-name/ac-page/-/blogs/blogs-ac-title',
				channelId: channel.id,
				dataSourceId: 0,
				eventDate: date.toISOString(),
				eventId: 'blogViewed',
				title: pageTitle,
				userId: anonymousIdentityId,
			},
			{
				applicationId: 'Comment',
				assetId: '1',
				assetTitle: 'Blogs AC Title',
				canonicalUrl: '/web/site-name/ac-page/-/blogs/blogs-ac-title',
				channelId: channel.id,
				dataSourceId: 0,
				eventDate: date.toISOString(),
				eventId: 'posted',
				eventProperties:
					'{"className":"com.liferay.blogs.model.BlogsEntry"}',
				properties: [
					{
						name: 'className',
						value: 'com.liferay.blogs.model.BlogsEntry',
					},
				],
				title: pageTitle,
				userId: knownIndividualA.id,
			},
		]);

		// Create a batch segment that includes only knownIndividualA so the audience card second donut splits 1 segmented / 1 unsegmented (50% / 50%)

		await apiHelpers.jsonWebServicesOSBFaro.createIndividualSegment({
			channelId: channel.id,
			filter: `(firstName eq 'ac')`,
			groupId: project.groupId,
			name: 'Audience Segment ' + getRandomString(),
		});

		// Run the membership nanite so the new batch segment picks up its only matching individual

		await runNanites({
			apiHelpers,
			naniteNames: [Nanites.UpdateMembershipsNanite],
			page,
		});

		// Open the blog overview

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
			.getByRole('link', {exact: true, name: 'Blogs AC Title'})
			.click();

		// The overview lists the five expected cards

		for (const cardTitle of [
			'Visitors Behavior',
			'Audience',
			'Views by Location',
			'Views by Technology',
			'Asset Appears On',
		]) {
			await expect(page.getByText(cardTitle)).toBeVisible();
		}

		// Visitors behavior shows views = 4 and comments = 1

		await expect(
			page
				.locator('.analytics-metrics-tabs .card-tab')
				.filter({hasText: 'Views'})
				.locator('.metric-value')
		).toHaveText('4');

		await expect(
			page
				.locator('.analytics-metrics-tabs .card-tab')
				.filter({hasText: 'Comments'})
				.locator('.metric-value')
		).toHaveText('1');

		// Asset appears on lists the seeded blog page URL

		await expect(
			page.getByRole('cell', {
				name: '/web/site-name/ac-page/-/blogs/blogs-ac-title',
			})
		).toBeVisible();

		// Audience card first donut splits 66.7% known / 33.3% anonymous

		await expect(page.getByText('66.7%')).toBeVisible();
		await expect(page.getByText('33.3%')).toBeVisible();

		// Audience card second donut splits 50% segmented / 50% unsegmented

		await expect(
			page.locator('.audience-report-chart-donut').nth(1).getByText('50%')
		).toHaveCount(2);
	}
);
