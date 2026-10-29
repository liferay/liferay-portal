/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createHash} from 'crypto';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedDXPSyncedChannelTest} from '../../../fixtures/isolatedDXPSyncedChannelTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {syncAnalyticsCloudViaAPI} from '../../analytics-settings-web/main/utils/analytics-settings';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {
	addBreakdownByAttribute,
	viewBreakdownRechartsData,
} from '../main/utils/distribution';
import {createIndividuals, generateIndividual} from '../main/utils/individuals';
import {waitForLoading} from '../main/utils/loading';
import {Nanites, runNanites} from '../main/utils/nanites';
import {
	ACPage,
	navigateToACPageViaURL,
	openIndividualProfileViaURL,
} from '../main/utils/navigation';
import {changeTimeFilter} from '../main/utils/time-filter';
import {
	selectPaginationItemsPerPage,
	selectPaginationPageNumber,
	viewNameListInOrder,
	viewPaginationResults,
} from '../main/utils/utils';

export const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedDXPSyncedChannelTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

test.beforeEach(async ({analyticsChannel, apiHelpers, project, site}) => {
	await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-heading',
			}),
		]),
		siteId: site.id,
		title: 'My Page',
	});

	await syncAnalyticsCloudViaAPI({
		apiHelpers,
		channel: analyticsChannel,
		project,
		siteId: Number(site.id),
	});
});

test(
	'Add a new breakdown by an attribute and assert that correct results appear',
	{
		tag: '@Legacy',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		dxpSyncedAnalyticsChannel,
		page,
		project,
	}) => {
		const {dataSourceId} = dxpSyncedAnalyticsChannel;

		const individualName = 'ac';
		const individuals = [
			{
				...generateIndividual({
					name: individualName,
				}),
				dataSourceId,
			},
		];

		await test.step('Create new Individual', async () => {
			await createIndividuals({
				apiHelpers,
				individuals,
			});
		});

		const date = new Date();
		await test.step('Create Individual Event', async () => {
			const events = individuals.map((individual) => ({
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId,
				eventDate: date.toISOString(),
				eventId: 'pageViewed',
				title: 'Liferay',
				userId: individual.id,
			}));

			await apiHelpers.jsonWebServicesOSBAsah.createEvents(events);
		});

		await test.step('Create Individual Session', async () => {
			const sessions = individuals.map((individual) => ({
				channelId: channel.id,
				id: individual.id,
				sessionEnd: date.toISOString(),
				sessionStart: date.toISOString(),
				userId: individual.id,
			}));

			await apiHelpers.jsonWebServicesOSBAsah.createSessions(sessions);
		});

		await test.step('Go to Individuals Dashboard', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.individualPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});
		});

		await test.step('Add a new breakdown', async () => {
			await addBreakdownByAttribute({
				attributeName: 'email',
				page,
			});
		});

		await test.step('Check if the correct results appear (email and maximum count)', async () => {
			await viewBreakdownRechartsData({
				attributeValue: `${individualName}@liferay.com`,
				maxCount: '1',
				page,
			});
		});

		await test.step('Close breakdown tab', async () => {
			await page.getByLabel('Close').click();
		});
	}
);

test(
	'Enriched Profiles count increases by one when an anonymous individual is later created as known',
	{
		tag: '@LRAC-8911',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		dxpSyncedAnalyticsChannel,
		page,
		project,
	}) => {
		const {dataSourceId} = dxpSyncedAnalyticsChannel;

		const individualId = getRandomString();
		const individualName = 'enriched' + getRandomString();

		const date = new Date();

		await test.step('Create an anonymous identity and page event', async () => {
			await apiHelpers.jsonWebServicesOSBAsah.createIdentities([
				{createDate: date.toISOString(), id: individualId},
			]);

			await apiHelpers.jsonWebServicesOSBAsah.createEvents([
				{
					applicationId: 'Page',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					dataSourceId,
					eventDate: date.toISOString(),
					eventId: 'pageViewed',
					title: 'My Page',
					userId: individualId,
				},
			]);

			await apiHelpers.jsonWebServicesOSBAsah.createSessions([
				{
					channelId: channel.id,
					id: individualId,
					sessionEnd: date.toISOString(),
					sessionStart: date.toISOString(),
					userId: individualId,
				},
			]);
		});

		await navigateToACPageViaURL({
			acPage: ACPage.individualPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await test.step('Verify Enriched Profiles count is zero before enrichment', async () => {
			await expect(
				page.locator('.enriched-profiles-card-root')
			).toContainText('0 Profiles');
		});

		await test.step('Create a known individual record for the anonymous identity', async () => {
			await createIndividuals({
				apiHelpers,
				individuals: [
					{dataSourceId, id: individualId, name: individualName},
				],
			});

			await apiHelpers.jsonWebServicesOSBAsah.createEvents([
				{
					applicationId: 'Page',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					dataSourceId,
					eventDate: new Date().toISOString(),
					eventId: 'pageViewed',
					title: 'My Page',
					userId: individualId,
				},
			]);
		});

		await page.reload();

		await test.step('Verify Enriched Profiles count became one after enrichment', async () => {
			await expect(
				page.locator('.enriched-profiles-card-root')
			).toContainText('1 Profiles');
		});
	}
);

test(
	'Individual events card shows the empty state when no events match the active time filter',
	{
		tag: '@LRAC-10513',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		dxpSyncedAnalyticsChannel,
		page,
		project,
	}) => {
		const {dataSourceId} = dxpSyncedAnalyticsChannel;

		const individual = {
			...generateIndividual({name: 'empty' + getRandomString()}),
			dataSourceId,
		};

		await createIndividuals({apiHelpers, individuals: [individual]});

		// Create an old event outside the Last 24 hours window so the user appears in the list but the events card is empty under that filter

		const oldDate = new Date();

		oldDate.setDate(oldDate.getDate() - 10);

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId,
				emailAddressHashed: createHash('sha256')
					.update(`${individual.name}@liferay.com`)
					.digest('hex'),
				eventDate: oldDate.toISOString(),
				eventId: 'pageViewed',
				sessionId: individual.id,
				title: 'My Page',
				userId: individual.id,
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createSessions([
			{
				channelId: channel.id,
				id: individual.id,
				sessionEnd: oldDate.toISOString(),
				sessionStart: oldDate.toISOString(),
				userId: individual.id,
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createIdentityActivitiesSummary(
			[
				{
					activitiesCount: 1,
					channelId: channel.id,
					dataSourceId,
					eventId: 'pageViewed',
					firstActivityDate: oldDate.toISOString(),
					identityId: individual.id,
					individualId: individual.id,
					lastActivityDate: oldDate.toISOString(),
				},
			]
		);

		await openIndividualProfileViaURL({
			channelID: channel.id,
			individualId: individual.id,
			page,
			projectID: project.groupId,
		});

		await expect(
			page.getByRole('button', {name: 'pageViewed'})
		).toBeVisible();

		// Switch the Individual Events card time filter to Last 24 hours to exclude the seeded event

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		await expect(
			page.getByText('There are no events found.').first()
		).toBeVisible();
	}
);

test(
	'Individual activities feed shows only that individuals own events',
	{
		tag: '@LRAC-10509',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		dxpSyncedAnalyticsChannel,
		page,
		project,
	}) => {
		const {dataSourceId} = dxpSyncedAnalyticsChannel;

		const runId = getRandomString();

		const individualA = {
			...generateIndividual({name: 'userA' + runId}),
			dataSourceId,
		};
		const individualB = {
			...generateIndividual({name: 'userB' + runId}),
			dataSourceId,
		};

		await createIndividuals({
			apiHelpers,
			individuals: [individualA, individualB],
		});

		const customEventA = 'customEventA-' + runId;
		const customEventB = 'customEventB-' + runId;

		const date = new Date();

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Custom',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId,
				eventDate: date.toISOString(),
				eventId: customEventA,
				title: customEventA,
				userId: individualA.id,
			},
			{
				applicationId: 'Custom',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId,
				eventDate: date.toISOString(),
				eventId: customEventB,
				title: customEventB,
				userId: individualB.id,
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createSessions([
			{
				channelId: channel.id,
				id: individualA.id,
				sessionEnd: date.toISOString(),
				sessionStart: date.toISOString(),
				userId: individualA.id,
			},
			{
				channelId: channel.id,
				id: individualB.id,
				sessionEnd: date.toISOString(),
				sessionStart: date.toISOString(),
				userId: individualB.id,
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'Custom',
				displayName: customEventA,
				name: customEventA,
				type: 'CUSTOM',
			},
			{
				applicationId: 'Custom',
				displayName: customEventB,
				name: customEventB,
				type: 'CUSTOM',
			},
		]);

		await test.step('Open user A and assert only their custom event is listed', async () => {
			await openIndividualProfileViaURL({
				channelID: channel.id,
				individualId: individualA.id,
				page,
				projectID: project.groupId,
			});

			// Switch the individual activities chart to the Last 24 hours view

			await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

			await expect(page.getByText(customEventA).first()).toBeVisible();

			await expect(page.getByText(customEventB)).not.toBeVisible();
		});

		await test.step('Open user B and assert only their custom event is listed', async () => {
			await openIndividualProfileViaURL({
				channelID: channel.id,
				individualId: individualB.id,
				page,
				projectID: project.groupId,
			});

			// Switch the individual activities chart to the Last 24 hours view

			await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

			await expect(page.getByText(customEventB).first()).toBeVisible();

			await expect(page.getByText(customEventA)).not.toBeVisible();
		});
	}
);

test(
	'Individual activities list shows todays activities when switched to the 24 hour view',
	{
		tag: '@LRAC-8151',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		dxpSyncedAnalyticsChannel,
		page,
		project,
	}) => {
		const {dataSourceId} = dxpSyncedAnalyticsChannel;

		const individual = {
			...generateIndividual({name: 'activities' + getRandomString()}),
			dataSourceId,
		};

		const date = new Date();

		await createIndividuals({apiHelpers, individuals: [individual]});

		// Seed several activities for today so they fall inside the Last 24
		// hours window

		await apiHelpers.jsonWebServicesOSBAsah.createEvents(
			['a', 'b', 'c'].map((suffix) => ({
				applicationId: 'Document',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId,
				emailAddressHashed: createHash('sha256')
					.update(`${individual.name}@liferay.com`)
					.digest('hex'),
				eventDate: date.toISOString(),
				eventId: 'documentPreviewed',
				sessionId: individual.id,
				title: `documentPreviewed-${suffix}`,
				userId: individual.id,
			}))
		);

		await apiHelpers.jsonWebServicesOSBAsah.createSessions([
			{
				channelId: channel.id,
				id: individual.id,
				sessionEnd: date.toISOString(),
				sessionStart: date.toISOString(),
				userId: individual.id,
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createIdentityActivitiesSummary(
			[
				{
					activitiesCount: 3,
					channelId: channel.id,
					dataSourceId,
					eventId: 'documentPreviewed',
					firstActivityDate: date.toISOString(),
					identityId: individual.id,
					individualId: individual.id,
					lastActivityDate: date.toISOString(),
				},
			]
		);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'Document',
				displayName: 'documentPreviewed',
				name: 'documentPreviewed',
				type: 'DEFAULT',
			},
		]);

		await openIndividualProfileViaURL({
			channelID: channel.id,
			individualId: individual.id,
			page,
			projectID: project.groupId,
		});

		// Switch the individual activities chart to the Last 24 hours view

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		// The seeded events show up in the individual activities feed

		await expect(
			page.getByRole('button', {name: 'documentPreviewed'})
		).toHaveCount(3);

		// The activities chart still renders bars for todays seeded events

		await expect(
			page
				.locator(
					'.individuals-activities-chart .recharts-bar-rectangle'
				)
				.first()
		).toBeAttached();
	}
);

test(
	'Total Individuals count reflects a newly ingested known individual',
	{
		tag: '@LRAC-8832',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		dxpSyncedAnalyticsChannel,
		page,
		project,
	}) => {
		const {dataSourceId} = dxpSyncedAnalyticsChannel;

		const totalIndividuals = page
			.locator('.trend-item-root')
			.filter({hasText: 'Total Individuals'})
			.locator('.total');

		await navigateToACPageViaURL({
			acPage: ACPage.individualPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		// The freshly synced channel has no ingested individuals yet

		await expect(totalIndividuals).toHaveText('0');

		// Ingest a single known individual with activity

		const individual = {
			...generateIndividual({name: 'total' + getRandomString()}),
			dataSourceId,
		};

		const date = new Date();

		await createIndividuals({apiHelpers, individuals: [individual]});

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId,
				eventDate: date.toISOString(),
				eventId: 'pageViewed',
				title: 'My Page',
				userId: individual.id,
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createSessions([
			{
				channelId: channel.id,
				id: individual.id,
				sessionEnd: date.toISOString(),
				sessionStart: date.toISOString(),
				userId: individual.id,
			},
		]);

		// Close sessions so the individual's activity is processed into the count

		await apiHelpers.jsonWebServicesOSBAsah.closeSessions();

		// The Total Individuals trend counts the individual once the closed
		// session is processed

		await expect(async () => {
			await page.reload();

			await expect(totalIndividuals).toHaveText('1', {timeout: 5000});
		}).toPass({timeout: 60000});
	}
);

test(
	'The Individuals dashboard overview shows all of its summary cards',
	{
		tag: '@LRAC-8903',
	},
	async ({analyticsChannel: channel, page, project}) => {
		await navigateToACPageViaURL({
			acPage: ACPage.individualPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		// The Total Individuals, Known and Anonymous trend summaries

		for (const trendName of ['Total Individuals', 'Known', 'Anonymous']) {
			await expect(
				page.locator('.trend-item-root').filter({hasText: trendName})
			).toBeVisible();
		}

		// The remaining overview cards

		for (const cardTitle of [
			'Enriched Profiles',
			'Active Individuals',
			'Top Interests',
		]) {
			await expect(page.getByText(cardTitle).first()).toBeVisible();
		}
	}
);

test('Active Individuals card is empty for a custom range with no activity', async ({
	analyticsChannel: channel,
	apiHelpers,
	dxpSyncedAnalyticsChannel,
	page,
	project,
}) => {
	const {dataSourceId} = dxpSyncedAnalyticsChannel;

	const individual = {
		...generateIndividual({name: 'empty' + getRandomString()}),
		dataSourceId,
	};

	const date = new Date();

	await createIndividuals({apiHelpers, individuals: [individual]});

	await apiHelpers.jsonWebServicesOSBAsah.createEvents([
		{
			applicationId: 'Page',
			canonicalUrl: 'https://www.liferay.com',
			channelId: channel.id,
			dataSourceId,
			eventDate: date.toISOString(),
			eventId: 'pageViewed',
			title: 'My Page',
			userId: individual.id,
		},
	]);

	await navigateToACPageViaURL({
		acPage: ACPage.individualPage,
		channelID: channel.id,
		page,
		projectID: project.groupId,
	});

	// Pick a custom range in the previous month, which holds no seeded activity

	await changeTimeFilter({page, timeFilterPeriod: 'Custom Range'});

	await page.getByTestId('previous-month').first().click();

	await page.getByRole('button', {exact: true, name: '10'}).first().click();

	await page.getByRole('button', {exact: true, name: '15'}).first().click();

	// The Active Individuals card shows its empty state instead of a chart

	await expect(
		page.getByText('There is no data for active individuals.')
	).toBeVisible();
});

test(
	'Individual profile Overview tab shows all of its summary cards',
	{
		tag: '@LRAC-8902',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		dxpSyncedAnalyticsChannel,
		page,
		project,
	}) => {
		const {dataSourceId} = dxpSyncedAnalyticsChannel;

		const individual = {
			...generateIndividual({name: 'cards' + getRandomString()}),
			dataSourceId,
		};

		const date = new Date();

		await createIndividuals({apiHelpers, individuals: [individual]});

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId,
				eventDate: date.toISOString(),
				eventId: 'pageViewed',
				title: 'My Page',
				userId: individual.id,
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createSessions([
			{
				channelId: channel.id,
				id: individual.id,
				sessionEnd: date.toISOString(),
				sessionStart: date.toISOString(),
				userId: individual.id,
			},
		]);

		// Open the individual profile from the Known Individuals list

		await openIndividualProfileViaURL({
			channelID: channel.id,
			individualId: individual.id,
			page,
			projectID: project.groupId,
		});

		// The Activities tab shows the Individual Events card

		await expect(page.getByText('Individual Events').first()).toBeVisible();

		// The Interests tab shows the interests list

		await page.getByRole('link', {exact: true, name: 'Interests'}).click();

		await expect(page.getByText('No interests were found.')).toBeVisible();

		// The Segments tab shows the Associated Segments card

		await page.getByRole('link', {exact: true, name: 'Segments'}).click();

		await expect(
			page.getByText('Associated Segments').first()
		).toBeVisible();
	}
);

test(
	'Individual associated segments list paginates',
	{
		tag: '@LRAC-9003',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		dxpSyncedAnalyticsChannel,
		page,
		project,
	}) => {
		const {dataSourceId} = dxpSyncedAnalyticsChannel;

		const runId = getRandomString();

		const individual = {
			...generateIndividual({name: 'seg' + runId}),
			dataSourceId,
		};

		const date = new Date();

		await createIndividuals({apiHelpers, individuals: [individual]});

		await apiHelpers.jsonWebServicesOSBAsah.createSessions([
			{
				channelId: channel.id,
				id: individual.id,
				sessionEnd: date.toISOString(),
				sessionStart: date.toISOString(),
				userId: individual.id,
			},
		]);

		// The individual belongs to six dynamic segments matching its given name

		const segmentIds = [];

		for (let i = 1; i <= 6; i++) {
			const segment =
				await apiHelpers.jsonWebServicesOSBFaro.createIndividualSegment(
					{
						channelId: channel.id,
						filter: `(firstName eq '${individual.name}')`,
						groupId: project.groupId,
						name: `Segment ${i} ${runId}`,
					}
				);

			segmentIds.push(segment.id);
		}

		try {
			await runNanites({
				apiHelpers,
				naniteNames: [Nanites.UpdateMembershipsNanite],
				page,
			});

			// Open the individual profile Segments tab

			await openIndividualProfileViaURL({
				channelID: channel.id,
				individualId: individual.id,
				page,
				projectID: project.groupId,
			});

			await page.getByRole('link', {name: 'Segments'}).click();

			// All six segments fit on the default page

			await viewPaginationResults({
				page,
				paginationResults: 'Showing 1 to 6 of 6 entries.',
			});

			// Four per page splits the six segments across two pages

			await selectPaginationItemsPerPage({itemsPerPage: '4', page});

			await viewPaginationResults({
				page,
				paginationResults: 'Showing 1 to 4 of 6 entries.',
			});

			await selectPaginationPageNumber({page, paginationPageNumber: '2'});

			await viewPaginationResults({
				page,
				paginationResults: 'Showing 5 to 6 of 6 entries.',
			});
		}
		finally {
			await apiHelpers.jsonWebServicesOSBFaro.deleteIndividualSegments(
				`[${segmentIds.join(',')}]`,
				project.groupId
			);
		}
	}
);

test(
	'Individual associated segments list can be ordered by name',
	{
		tag: '@LRAC-8991',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		dxpSyncedAnalyticsChannel,
		page,
		project,
	}) => {
		const {dataSourceId} = dxpSyncedAnalyticsChannel;

		const runId = getRandomString();

		const individual = {
			...generateIndividual({name: 'seg' + runId}),
			dataSourceId,
		};

		const date = new Date();

		await createIndividuals({apiHelpers, individuals: [individual]});

		await apiHelpers.jsonWebServicesOSBAsah.createSessions([
			{
				channelId: channel.id,
				id: individual.id,
				sessionEnd: date.toISOString(),
				sessionStart: date.toISOString(),
				userId: individual.id,
			},
		]);

		// Three dynamic segments matching the individual, named to sort A, B, C

		const segmentNames = [
			`Segment ${runId} A`,
			`Segment ${runId} B`,
			`Segment ${runId} C`,
		];

		const segmentIds = [];

		for (const name of segmentNames) {
			const segment =
				await apiHelpers.jsonWebServicesOSBFaro.createIndividualSegment(
					{
						channelId: channel.id,
						filter: `(firstName eq '${individual.name}')`,
						groupId: project.groupId,
						name,
					}
				);

			segmentIds.push(segment.id);
		}

		try {
			await runNanites({
				apiHelpers,
				naniteNames: [Nanites.UpdateMembershipsNanite],
				page,
			});

			// Open the individual profile Segments tab

			await openIndividualProfileViaURL({
				channelID: channel.id,
				individualId: individual.id,
				page,
				projectID: project.groupId,
			});

			await page.getByRole('link', {name: 'Segments'}).click();

			// Ordering by name lists the segments alphabetically

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: 'Name'}),
				trigger: page.getByRole('button', {name: 'Order'}),
			});

			await viewNameListInOrder({itemNames: segmentNames, page});
		}
		finally {
			await apiHelpers.jsonWebServicesOSBFaro.deleteIndividualSegments(
				`[${segmentIds.join(',')}]`,
				project.groupId
			);
		}
	}
);

test(
	'Individual associated segment can be selected to view its membership',
	{
		tag: '@LRAC-8993',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		dxpSyncedAnalyticsChannel,
		page,
		project,
	}) => {
		const {dataSourceId} = dxpSyncedAnalyticsChannel;

		const runId = getRandomString();

		const individual = {
			...generateIndividual({name: 'seg' + runId}),
			dataSourceId,
		};

		const date = new Date();

		await createIndividuals({apiHelpers, individuals: [individual]});

		await apiHelpers.jsonWebServicesOSBAsah.createSessions([
			{
				channelId: channel.id,
				id: individual.id,
				sessionEnd: date.toISOString(),
				sessionStart: date.toISOString(),
				userId: individual.id,
			},
		]);

		const segmentNames = [`Segment ${runId} A`, `Segment ${runId} B`];

		const segmentIds = [];

		for (const name of segmentNames) {
			const segment =
				await apiHelpers.jsonWebServicesOSBFaro.createIndividualSegment(
					{
						channelId: channel.id,
						filter: `(firstName eq '${individual.name}')`,
						groupId: project.groupId,
						name,
					}
				);

			segmentIds.push(segment.id);
		}

		try {
			await runNanites({
				apiHelpers,
				naniteNames: [Nanites.UpdateMembershipsNanite],
				page,
			});

			// Open the individual profile Segments tab

			await openIndividualProfileViaURL({
				channelID: channel.id,
				individualId: individual.id,
				page,
				projectID: project.groupId,
			});

			await page.getByRole('link', {name: 'Segments'}).click();

			// Select an associated segment and open its membership

			await page.getByRole('link', {name: segmentNames[1]}).click();

			await page.getByRole('link', {name: 'Membership'}).click();

			await expect(
				page.getByText(`${individual.name} Smith`).first()
			).toBeVisible();
		}
		finally {
			await apiHelpers.jsonWebServicesOSBFaro.deleteIndividualSegments(
				`[${segmentIds.join(',')}]`,
				project.groupId
			);
		}
	}
);

test(
	'Individual Events activity can be expanded to view its event attributes',
	{
		tag: '@LRAC-8907',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		dxpSyncedAnalyticsChannel,
		page,
		project,
	}) => {
		const {dataSourceId} = dxpSyncedAnalyticsChannel;

		const individual = {
			...generateIndividual({name: 'act' + getRandomString()}),
			dataSourceId,
		};

		const date = new Date();

		const pastDate = new Date(date);

		pastDate.setDate(pastDate.getDate() - 1);

		await createIndividuals({apiHelpers, individuals: [individual]});

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId,
				emailAddressHashed: createHash('sha256')
					.update(`${individual.name}@liferay.com`)
					.digest('hex'),
				eventDate: pastDate.toISOString(),
				eventId: 'pageViewed',
				sessionId: individual.id,
				title: 'pageViewed',
				userId: individual.id,
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createSessions([
			{
				channelId: channel.id,
				id: individual.id,
				sessionEnd: date.toISOString(),
				sessionStart: date.toISOString(),
				userId: individual.id,
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createIdentityActivitiesSummary(
			[
				{
					activitiesCount: 1,
					channelId: channel.id,
					dataSourceId,
					eventId: 'pageViewed',
					firstActivityDate: date.toISOString(),
					identityId: individual.id,
					individualId: individual.id,
					lastActivityDate: date.toISOString(),
				},
			]
		);

		// Open the individual profile

		await openIndividualProfileViaURL({
			channelID: channel.id,
			individualId: individual.id,
			page,
			projectID: project.groupId,
		});

		// Expand the activity in the Individual Events timeline

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		await page.getByText('pageViewed').first().click();

		await expect(page.getByText('"applicationId": "Page"')).toBeVisible();
		await expect(page.getByText('"eventId": "pageViewed"')).toBeVisible();
	}
);

test(
	'A selected individual activity point can clear its date selection',
	{
		tag: '@LRAC-8916',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		dxpSyncedAnalyticsChannel,
		page,
		project,
	}) => {
		const {dataSourceId} = dxpSyncedAnalyticsChannel;

		const individual = {
			...generateIndividual({name: 'act' + getRandomString()}),
			dataSourceId,
		};

		const date = new Date();

		const pastDate = new Date(date);

		pastDate.setDate(pastDate.getDate() - 1);

		await createIndividuals({apiHelpers, individuals: [individual]});

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId,
				emailAddressHashed: createHash('sha256')
					.update(`${individual.name}@liferay.com`)
					.digest('hex'),
				eventDate: pastDate.toISOString(),
				eventId: 'pageViewed',
				sessionId: individual.id,
				title: 'pageViewed',
				userId: individual.id,
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createSessions([
			{
				channelId: channel.id,
				id: individual.id,
				sessionEnd: date.toISOString(),
				sessionStart: date.toISOString(),
				userId: individual.id,
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.closeSessions();

		await apiHelpers.jsonWebServicesOSBAsah.createIdentityActivitiesSummary(
			[
				{
					activitiesCount: 1,
					channelId: channel.id,
					dataSourceId,
					eventId: 'pageViewed',
					firstActivityDate: date.toISOString(),
					identityId: individual.id,
					individualId: individual.id,
					lastActivityDate: date.toISOString(),
				},
			]
		);

		// Open the individual profile

		await openIndividualProfileViaURL({
			channelID: channel.id,
			individualId: individual.id,
			page,
			projectID: project.groupId,
		});

		// Select a point on the activity chart, then clear the date selection

		await page.locator('.recharts-bar-rectangle path').first().click();

		await expect(
			page.getByRole('button', {name: 'Clear Date Selection'})
		).toBeVisible();

		await page.getByRole('button', {name: 'Clear Date Selection'}).click();

		await expect(
			page.getByRole('button', {name: 'Clear Date Selection'})
		).toHaveCount(0);
	}
);

test(
	'All seeded individual attributes appear in the Details list',
	{
		tag: '@LRAC-8938',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		dxpSyncedAnalyticsChannel,
		page,
		project,
	}) => {
		const {dataSourceId} = dxpSyncedAnalyticsChannel;

		const individual = {
			...generateIndividual({name: 'det' + getRandomString()}),
			dataSourceId,
			jobTitle: 'lawyer',
		};

		const date = new Date();

		await createIndividuals({apiHelpers, individuals: [individual]});

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId,
				emailAddressHashed: createHash('sha256')
					.update(`${individual.name}@liferay.com`)
					.digest('hex'),
				eventDate: date.toISOString(),
				eventId: 'pageViewed',
				sessionId: individual.id,
				title: 'pageViewed',
				userId: individual.id,
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createSessions([
			{
				channelId: channel.id,
				id: individual.id,
				sessionEnd: date.toISOString(),
				sessionStart: date.toISOString(),
				userId: individual.id,
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createIdentityActivitiesSummary(
			[
				{
					activitiesCount: 1,
					channelId: channel.id,
					dataSourceId,
					eventId: 'pageViewed',
					firstActivityDate: date.toISOString(),
					identityId: individual.id,
					individualId: individual.id,
					lastActivityDate: date.toISOString(),
				},
			]
		);

		// Open the individual profile Details tab

		await openIndividualProfileViaURL({
			channelID: channel.id,
			individualId: individual.id,
			page,
			projectID: project.groupId,
		});

		await waitForLoading(page);

		await page.getByRole('link', {exact: true, name: 'Profile'}).click();

		// The seeded attributes appear in the Details list

		for (const attributeName of [
			'birthDate',
			'email',
			'familyName',
			'givenName',
			'jobTitle',
		]) {
			await expect(
				page.getByRole('row').filter({
					has: page.getByText(attributeName, {exact: true}),
				})
			).toBeVisible();
		}
	}
);

test(
	'Synced contacts appear in the Known Individuals list',
	{
		tag: '@LRAC-12659',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		dxpSyncedAnalyticsChannel,
		page,
		project,
	}) => {
		const {dataSourceId} = dxpSyncedAnalyticsChannel;

		const runId = getRandomString();

		const individuals = [
			{...generateIndividual({name: 'dxp' + runId}), dataSourceId},
			{...generateIndividual({name: 'lxc' + runId}), dataSourceId},
		];

		const date = new Date();

		date.setDate(date.getDate() - 1);

		await createIndividuals({apiHelpers, individuals});

		await apiHelpers.jsonWebServicesOSBAsah.createEvents(
			individuals.map((individual) => ({
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId,
				eventDate: date.toISOString(),
				eventId: 'pageViewed',
				title: 'pageViewed',
				userId: individual.id,
			}))
		);

		await apiHelpers.jsonWebServicesOSBAsah.createIdentityActivitiesSummary(
			individuals.map((individual) => ({
				activitiesCount: 1,
				channelId: channel.id,
				dataSourceId,
				eventId: 'pageViewed',
				firstActivityDate: date.toISOString(),
				identityId: individual.id,
				individualId: individual.id,
				lastActivityDate: date.toISOString(),
			}))
		);

		await navigateToACPageViaURL({
			acPage: ACPage.individualPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		// Both synced contacts surface in the Known Individuals list

		for (const individual of individuals) {
			await expect(async () => {
				await page.reload();

				await page
					.getByPlaceholder('Search')
					.first()
					.fill(individual.name);

				await expect(
					page.getByRole('link', {name: individual.name}).first()
				).toBeVisible({timeout: 3000});
			}).toPass({timeout: 30000});
		}
	}
);

test(
	'Contacts synced by user group appear in the Known Individuals list',
	{
		tag: '@LRAC-12665',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		dxpSyncedAnalyticsChannel,
		page,
		project,
	}) => {
		const {dataSourceId} = dxpSyncedAnalyticsChannel;

		const individual = {
			...generateIndividual({name: 'grp' + getRandomString()}),
			dataSourceId,
		};

		const date = new Date();

		date.setDate(date.getDate() - 1);

		await createIndividuals({apiHelpers, individuals: [individual]});

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId,
				eventDate: date.toISOString(),
				eventId: 'pageViewed',
				title: 'pageViewed',
				userId: individual.id,
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createIdentityActivitiesSummary(
			[
				{
					activitiesCount: 1,
					channelId: channel.id,
					dataSourceId,
					eventId: 'pageViewed',
					firstActivityDate: date.toISOString(),
					identityId: individual.id,
					individualId: individual.id,
					lastActivityDate: date.toISOString(),
				},
			]
		);

		await navigateToACPageViaURL({
			acPage: ACPage.individualPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await expect(async () => {
			await page.reload();

			await page.getByPlaceholder('Search').first().fill(individual.name);

			await expect(
				page.getByRole('link', {name: individual.name}).first()
			).toBeVisible({timeout: 3000});
		}).toPass({timeout: 30000});
	}
);

test(
	'Contacts synced by organization appear in the Known Individuals list',
	{
		tag: '@LRAC-12662',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		dxpSyncedAnalyticsChannel,
		page,
		project,
	}) => {
		const {dataSourceId} = dxpSyncedAnalyticsChannel;

		const individual = {
			...generateIndividual({name: 'org' + getRandomString()}),
			dataSourceId,
		};

		const date = new Date();

		date.setDate(date.getDate() - 1);

		await createIndividuals({apiHelpers, individuals: [individual]});

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId,
				eventDate: date.toISOString(),
				eventId: 'pageViewed',
				title: 'pageViewed',
				userId: individual.id,
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createIdentityActivitiesSummary(
			[
				{
					activitiesCount: 1,
					channelId: channel.id,
					dataSourceId,
					eventId: 'pageViewed',
					firstActivityDate: date.toISOString(),
					identityId: individual.id,
					individualId: individual.id,
					lastActivityDate: date.toISOString(),
				},
			]
		);

		await navigateToACPageViaURL({
			acPage: ACPage.individualPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await expect(async () => {
			await page.reload();

			await page.getByPlaceholder('Search').first().fill(individual.name);

			await expect(
				page.getByRole('link', {name: individual.name}).first()
			).toBeVisible({timeout: 3000});
		}).toPass({timeout: 30000});
	}
);
