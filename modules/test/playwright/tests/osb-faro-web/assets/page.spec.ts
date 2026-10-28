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
import getRandomString from '../../../utils/getRandomString';
import {switchChannel} from '../main/utils/channel';
import {createIndividuals, generateIndividual} from '../main/utils/individuals';
import {Nanites, runNanites} from '../main/utils/nanites';
import {
	ACPage,
	navigateTo,
	navigateToACPageViaURL,
	navigateToACWorkspace,
} from '../main/utils/navigation';
import {
	addSegmentField,
	addStaticMember,
	createDynamicSegment,
	createStaticSegment,
	deleteSegment,
	saveSegment,
	selectOperator,
	setSegmentName,
} from '../main/utils/segments';
import {CardSelectors, SegmentConditions} from '../main/utils/selectors';
import {changeTimeFilter} from '../main/utils/time-filter';
import {
	searchByTerm,
	viewNameNotPresentOnTableList,
	viewNameOnTableList,
} from '../main/utils/utils';

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

const randomString = getRandomString();

const pageTitle = 'My Page';

test(
	'Assert clicking on a page in the pages lists navigates to the page profile',
	{
		tag: '@LRAC-8112 Legacy',
	},
	async ({analyticsChannel: channel, apiHelpers, page}) => {
		const individualName = 'user1';

		const individuals = [
			generateIndividual({
				name: individualName,
			}),
		];

		await test.step('Create an Individual directly in the AC database', async () => {
			await createIndividuals({
				apiHelpers,
				individuals,
			});
		});

		const date1 = new Date();

		await test.step('Create an event for the individual to appear within the Last 24 hours period in AC', async () => {
			const events = individuals.map((individual) => ({
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date1.toISOString(),
				eventId: 'pageViewed',
				title: pageTitle,
				userId: individual.id,
			}));

			await apiHelpers.jsonWebServicesOSBAsah.createEvents(events);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACWorkspace({page});
			await switchChannel({
				channelName: channel.name,
				page,
			});
		});

		await test.step('Go to Pages Tab', async () => {
			await navigateTo({
				page,
				pageName: 'Pages',
			});
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Access one of the pages on the list', async () => {
			await navigateTo({
				page,
				pageName: pageTitle,
			});
		});

		await test.step('Assert Page Profile', async () => {
			await expect(page.locator('h1.title')).toContainText(pageTitle);

			const cardsNames = [
				'Unique Visitors',
				'Views',
				'Bounce Rate',
				'Time On Page',
				'Entrances',
				'Exit Rate',
			];

			for (const card of cardsNames) {
				await expect(
					page.getByRole('button').getByText(card, {exact: true})
				).toBeVisible();
			}
		});

		await test.step('View the time filter is Last 24 Hours', async () => {
			await expect(
				page.getByRole('button', {name: 'Last 24 hours'})
			).toBeVisible();
		});

		await test.step('Switch to Overview tab and view the time filter is Last 24 Hours', async () => {
			await navigateTo({
				page,
				pageName: 'Overview',
			});

			await expect(
				page.getByRole('button', {name: 'Last 24 hours'})
			).toBeVisible();
		});
	}
);

test(
	'Assert page view accuracy between cards Visitor Behavior, Audience, and Page List number',
	{
		tag: '@LRAC-14813',
	},
	async ({analyticsChannel: channel, apiHelpers, page}) => {
		const pageTitle = 'My Page ' + randomString;

		const individualName = 'user1';

		const individuals = [
			generateIndividual({
				name: individualName,
			}),
		];

		await test.step('Create an Individual directly in the AC database', async () => {
			await createIndividuals({
				apiHelpers,
				individuals,
			});
		});

		const date1 = new Date();

		await test.step('Create an event for the individual to appear within the Last 24 hours period in AC', async () => {
			const events = individuals.map((individual) => ({
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date1.toISOString(),
				eventId: 'pageViewed',
				title: pageTitle,
				userId: individual.id,
			}));

			await apiHelpers.jsonWebServicesOSBAsah.createEvents(events);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACWorkspace({page});
			await switchChannel({
				channelName: channel.name,
				page,
			});
		});

		await test.step('Go to Pages Tab', async () => {
			await navigateTo({
				page,
				pageName: 'Pages',
			});
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});

			await page.reload();
		});

		await test.step('Access one of the pages on the list', async () => {
			await navigateTo({
				page,
				pageName: pageTitle,
			});
		});

		await test.step('View Unique Visitors metric value in Visitors Behavior card', async () => {
			await expect(
				page.getByRole('button', {name: 'Unique Visitors'})
			).toContainText('1');
		});

		await test.step('View Visitors metric value in Audience card', async () => {
			await expect(
				page
					.locator('[id="container\\.report\\.audienceCard"]')
					.getByText('1')
					.nth(1)
			).toBeVisible();
		});
	}
);

test(
	'Assert the pages list shows a list of pages',
	{
		tag: '@Legacy',
	},
	async ({analyticsChannel: channel, apiHelpers, page}) => {
		const pageTitle1 = 'My Page 1';
		const pageTitle2 = 'My Page 2';
		const pageTitle3 = 'My Page 3';

		const individualName = 'user1';

		const individuals = [
			generateIndividual({
				name: individualName,
			}),
		];

		await test.step('Create an Individual directly in the AC database', async () => {
			await createIndividuals({
				apiHelpers,
				individuals,
			});
		});

		const date1 = new Date();

		await test.step('Create an event for the individual to appear within the Last 24 hours period in AC', async () => {
			const events = individuals
				.map((individual) => ({
					applicationId: 'Page',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date1.toISOString(),
					eventId: 'pageViewed',
					title: pageTitle1,
					userId: individual.id,
				}))
				.concat(
					individuals.map((individual) => ({
						applicationId: 'Page',
						canonicalUrl: 'https://www.liferay.com',
						channelId: channel.id,
						eventDate: date1.toISOString(),
						eventId: 'pageViewed',
						title: pageTitle2,
						userId: individual.id,
					}))
				)
				.concat(
					individuals.map((individual) => ({
						applicationId: 'Page',
						canonicalUrl: 'https://www.liferay.com',
						channelId: channel.id,
						eventDate: date1.toISOString(),
						eventId: 'pageViewed',
						title: pageTitle3,
						userId: individual.id,
					}))
				);

			await apiHelpers.jsonWebServicesOSBAsah.createEvents(events);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACWorkspace({page});
			await switchChannel({
				channelName: channel.name,
				page,
			});
		});

		await test.step('Go to Pages Tab', async () => {
			await navigateTo({
				page,
				pageName: 'Pages',
			});
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Assert the page list', async () => {
			await expect(
				page.getByRole('link', {name: `${pageTitle1}`})
			).toBeVisible();
			await expect(
				page.getByRole('link', {name: `${pageTitle2}`})
			).toBeVisible();
			await expect(
				page.getByRole('link', {name: `${pageTitle3}`})
			).toBeVisible();
		});
	}
);

test(
	'Check that the Dynamic Segment does not continue to appear in the audience card after the segment is deleted',
	{
		tag: '@LPD-27586',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const individualName = 'user1';
		const individuals = [
			generateIndividual({
				name: individualName,
			}),
		];

		await test.step('Create an Individual directly in the AC database', async () => {
			await createIndividuals({
				apiHelpers,
				individuals,
			});
		});

		const date1 = new Date();

		await test.step('Create an event for the individual to appear within the Last 24 hours period in AC', async () => {
			const events = individuals.map((individual) => ({
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date1.toISOString(),
				eventId: 'pageViewed',
				title: pageTitle,
				userId: individual.id,
			}));

			await apiHelpers.jsonWebServicesOSBAsah.createEvents(events);
		});

		await test.step('Create Individual Session within the Last 24 hours period in AC', async () => {
			const sessions = individuals.map((individual) => ({
				channelId: channel.id,
				id: individual.id,
				sessionEnd: date1.toISOString(),
				sessionStart: date1.toISOString(),
				userId: individual.id,
			}));

			await apiHelpers.jsonWebServicesOSBAsah.createSessions(sessions);
		});

		const date2 = new Date();
		date2.setDate(date2.getDate() - 5);

		await test.step('Create an event for the individual to appear in periods different than the Last 24 hours in AC', async () => {
			const pageDaily = individuals.map((individual) => ({
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date2.toISOString(),
				title: pageTitle,
				userId: individual.id,
				views: 1,
			}));

			await apiHelpers.jsonWebServicesOSBAsah.createPagesDaily(pageDaily);
		});

		await test.step('Create Individual Session in periods different than the Last 24 hours in AC', async () => {
			const sessions = individuals.map((individual) => ({
				channelId: channel.id,
				id: individual.id,
				sessionEnd: date2.toISOString(),
				sessionStart: date2.toISOString(),
				userId: individual.id,
			}));

			await apiHelpers.jsonWebServicesOSBAsah.createSessions(sessions);
		});

		await test.step('Go to Segments', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.segmentPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});
		});

		const dynamicSegmentName = 'Test Dynamic Segment';

		await test.step('Create dynamic segment', async () => {
			await createDynamicSegment(page);

			await addSegmentField({
				criterionName: 'email',
				criterionType: 'Individual',
				page,
			});

			await selectOperator({
				operator: 'is known',
				operatorField: SegmentConditions.criteriaCondition,
				page,
			});

			await setSegmentName({
				page,
				segmentName: dynamicSegmentName,
			});

			await saveSegment(page);
		});

		await test.step('Go to Segments', async () => {
			await navigateTo({
				page,
				pageName: 'Segments',
			});
		});

		const staticSegmentName = 'Test Static Segment';

		await test.step('Create static segment', async () => {
			await createStaticSegment(page);

			await setSegmentName({
				page,
				segmentName: staticSegmentName,
			});

			await addStaticMember({
				memberNames: individualName,
				page,
			});

			await saveSegment(page);
		});

		await test.step('Run the Segment Nanite', async () => {
			await runNanites({
				apiHelpers,
				naniteNames: [Nanites.UpdateMembershipsNanite],
				page,
			});
		});

		await test.step('Go to Sites > Go to Pages Tab', async () => {
			await navigateTo({
				page,
				pageName: 'Sites',
			});

			await navigateTo({
				page,
				pageName: 'Pages',
			});
		});

		await test.step('Access one of the pages on the list', async () => {
			await navigateTo({
				page,
				pageName: pageTitle,
			});
		});

		const segmentsName = [dynamicSegmentName, staticSegmentName];

		await test.step('Check that the created segment appears on the Audience card', async () => {
			for (const itemName of segmentsName) {
				await expect(page.getByText(itemName)).toBeVisible({
					timeout: 100 * 1000,
				});
			}
		});

		await test.step('Change the time filter of the Audience card to Last 24 hours', async () => {
			await changeTimeFilter({
				cardSelector: CardSelectors.Audience,
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Check that the created segment appears on the Audience card', async () => {
			for (const itemName of segmentsName) {
				await expect(page.getByText(itemName)).toBeVisible({
					timeout: 100 * 1000,
				});
			}
		});

		await test.step('Go to Segments', async () => {
			await navigateTo({
				page,
				pageName: 'Segments',
			});
		});

		await test.step('Delete the segments', async () => {
			await deleteSegment({
				page,
				segmentName: dynamicSegmentName,
			});

			await deleteSegment({
				page,
				segmentName: staticSegmentName,
			});
		});

		await test.step('Reload the page to clear the Audience card cache', async () => {
			await page.reload();
		});

		await test.step('Go to Sites > Go to Pages Tab', async () => {
			await navigateTo({
				page,
				pageName: 'Sites',
			});

			await navigateTo({
				page,
				pageName: 'Pages',
			});
		});

		await test.step('Access one of the pages on the list', async () => {
			await navigateTo({
				page,
				pageName: pageTitle,
			});
		});

		await test.step('Check that no segments appear on the Audience card', async () => {
			await expect(
				page.locator('.audience-report-chart-bar li')
			).toBeHidden();
		});

		await test.step('Change the time filter of the Audience card to Last 24 hours', async () => {
			await changeTimeFilter({
				cardSelector: CardSelectors.Audience,
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Check that no segments appear on the Audience card', async () => {
			await expect(
				page.locator('.audience-report-chart-bar li')
			).toBeHidden();
		});
	}
);

test.skip(
	'Create the page name with special characters makes the correct name appear in the path part of AC pages',
	{
		tag: '@LRAC-8988',
	},
	async ({analyticsChannel: channel, apiHelpers, page}) => {
		const pageTitle = 'Snúið Vinsælar þú';

		const individualName = 'user1';
		const individuals = [
			generateIndividual({
				name: individualName,
			}),
		];

		await test.step('Create an Individual directly in the AC database', async () => {
			await createIndividuals({
				apiHelpers,
				individuals,
			});
		});

		const date1 = new Date();

		await test.step('Create an event for the individual to appear within the Last 24 hours period in AC', async () => {
			const events = individuals.map((individual) => ({
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date1.toISOString(),
				eventId: 'pageViewed',
				title: pageTitle,
				userId: individual.id,
			}));

			await apiHelpers.jsonWebServicesOSBAsah.createEvents(events);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACWorkspace({page});
			await switchChannel({
				channelName: channel.name,
				page,
			});
		});

		await test.step('Go to Pages Tab', async () => {
			await navigateTo({
				page,
				pageName: 'Pages',
			});
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Access one of the pages on the list > Go to Path Tab', async () => {
			await navigateTo({
				page,
				pageName: pageTitle,
			});
			await navigateTo({
				page,
				pageName: 'Path',
			});
		});

		await test.step('Check that Snúið Vinsælar þú Page appear as referral page', async () => {
			await expect(
				page.getByText('Snúið Vinsælar ...', {exact: true}).first()
			).toBeVisible();
		});
	}
);

test(
	'Page Profile known individuals list shows individuals who have viewed the page',
	{
		tag: '@Legacy',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const firstIndividual = 'user1';
		const secondIndividual = 'user2';
		const thirdIndividual = 'user3';

		const individuals = [
			generateIndividual({
				name: firstIndividual,
			}),
			generateIndividual({
				name: secondIndividual,
			}),
			generateIndividual({
				name: thirdIndividual,
			}),
		];

		await test.step('Create 3 Individuals directly in the AC database', async () => {
			await createIndividuals({
				apiHelpers,
				individuals,
			});
		});

		const date1 = new Date();

		await test.step('Create events for two of the individuals to appear within the Last 24 hours period in AC', async () => {
			await apiHelpers.jsonWebServicesOSBAsah.createEvents(
				individuals.slice(0, 2).map((individual) => ({
					applicationId: 'Page',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date1.toISOString(),
					eventId: 'pageViewed',
					title: 'Liferay',
					userId: individual.id,
				}))
			);
		});

		await test.step('Create events for one of the individuals to appear in periods different than the Last 24 hours in AC', async () => {
			const date2 = new Date();
			date2.setDate(date2.getDate() - 5);

			await apiHelpers.jsonWebServicesOSBAsah.createPagesDaily(
				individuals.slice(2, 3).map((individual) => ({
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date2.toISOString(),
					title: 'Liferay',
					userId: individual.id,
					views: 1,
				}))
			);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.sitePage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});
		});

		await test.step('Go to Pages Tab', async () => {
			await navigateTo({
				page,
				pageName: 'Pages',
			});
		});

		await test.step('Access one of the pages on the list > Go to Known Individuals Tab', async () => {
			await navigateTo({
				page,
				pageName: 'Liferay',
			});
			await navigateTo({
				page,
				pageName: 'Known Individuals',
			});
		});

		const individualPresentIn30Days = [thirdIndividual];

		await test.step('Check that User3 User3 is appearing in the list', async () => {
			await viewNameOnTableList({
				itemNames: individualPresentIn30Days,
				page,
			});
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		const individualsPresentIn24Hours = [firstIndividual, secondIndividual];

		await test.step('Check that User1 User1 and User2 User2 are appearing in the list', async () => {
			await viewNameOnTableList({
				itemNames: individualsPresentIn24Hours,
				page,
			});
		});

		await test.step('Check that User3 User3 is appearing in the list', async () => {
			await viewNameNotPresentOnTableList({
				itemNames: individualPresentIn30Days,
				page,
			});
		});
	}
);

test(
	'Page profile views by technology shows which browsers and devices are being used',
	{
		tag: '@Legacy',
	},
	async ({analyticsChannel: channel, apiHelpers, page}) => {
		const individualName = 'user1';
		const individuals = [
			generateIndividual({
				name: individualName,
			}),
		];

		await test.step('Create an Individual directly in the AC database', async () => {
			await createIndividuals({
				apiHelpers,
				individuals,
			});
		});

		const date1 = new Date();

		await test.step('Create an event for the individual to appear within the Last 24 hours period in AC', async () => {
			const events = individuals.map((individual) => ({
				applicationId: 'Page',
				browserName: 'Chrome',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				deviceType: 'Desktop',
				eventDate: date1.toISOString(),
				eventId: 'pageViewed',
				title: pageTitle,
				userId: individual.id,
			}));

			await apiHelpers.jsonWebServicesOSBAsah.createEvents(events);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACWorkspace({page});
			await switchChannel({
				channelName: channel.name,
				page,
			});
		});

		await test.step('Go to Pages Tab', async () => {
			await navigateTo({
				page,
				pageName: 'Pages',
			});
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Access one of the pages on the list', async () => {
			await navigateTo({
				page,
				pageName: pageTitle,
			});
		});

		await test.step('View Technology Browsers Metrics', async () => {
			await page
				.getByText('Views by Technology')
				.scrollIntoViewIfNeeded();

			await page.getByRole('button', {name: 'Browsers'}).click();

			await expect(page.getByText('Views by Technology')).toBeVisible();

			await expect(page.getByText('Chrome')).toBeVisible();

			await expect(page.locator('.legend-percentage')).toContainText(
				'100%'
			);
		});

		await test.step('View Technology Devices Metrics', async () => {
			await page.getByRole('button', {name: 'Devices'}).click();

			await expect(
				page
					.locator('.analytics-operating-system-chart')
					.getByText('Desktop')
			).toBeVisible();
		});
	}
);

test.skip(
	'Path analysis shows the outside pages that were interacted with in DXP',
	{
		tag: '@LRAC-14827',
	},
	async ({analyticsChannel: channel, apiHelpers, page}) => {
		const individualName = 'user1';
		const individuals = [
			generateIndividual({
				name: individualName,
			}),
		];

		await test.step('Create an Individual directly in the AC database', async () => {
			await createIndividuals({
				apiHelpers,
				individuals,
			});
		});

		const date1 = new Date();

		await test.step('Create an event for the individual to appear within the Last 24 hours period in AC', async () => {
			const events = individuals.map((individual) => ({
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date1.toISOString(),
				eventId: 'pageViewed',
				referrer: 'https://www.google.com',
				title: pageTitle,
				userId: individual.id,
			}));

			await apiHelpers.jsonWebServicesOSBAsah.createEvents(events);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACWorkspace({page});
			await switchChannel({
				channelName: channel.name,
				page,
			});
		});

		await test.step('Go to Pages Tab', async () => {
			await navigateTo({
				page,
				pageName: 'Pages',
			});
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Access one of the pages on the list > Go to Path Tab', async () => {
			await navigateTo({
				page,
				pageName: pageTitle,
			});
			await navigateTo({
				page,
				pageName: 'Path',
			});
		});

		await test.step('Check that Google Page appears the referral pages and the number of views', async () => {
			await expect(page.getByText('https://www.goo...')).toBeVisible();

			await expect(
				page.getByText('1', {exact: true}).first()
			).toBeVisible();
		});

		await test.step('Check that Home Page appears with one view', async () => {
			await expect(
				page.getByText('1', {exact: true}).nth(1)
			).toBeVisible();
		});

		await test.step('Check that My Page appears as exit pages and the number of views', async () => {
			await expect(page.getByTitle('Go to Dashboard Page')).toContainText(
				pageTitle
			);

			await expect(
				page.getByText('1', {exact: true}).nth(2)
			).toBeVisible();
		});
	}
);

test.skip(
	'Path Analysis shows the pages that were interacted with in DXP',
	{
		tag: '@LRAC-14827',
	},
	async ({analyticsChannel: channel, apiHelpers, page}) => {
		const pageTitle1 = 'My Page 1';
		const pageTitle2 = 'My Page 2';
		const pageURL1 = 'https://www.liferay.com/page1';
		const pageURL2 = 'https://www.liferay.com/page2';

		const individualName = 'user1';
		const individuals = [
			generateIndividual({
				name: individualName,
			}),
		];

		await test.step('Create an Individual directly in the AC database', async () => {
			await createIndividuals({
				apiHelpers,
				individuals,
			});
		});

		const date1 = new Date();

		await test.step('Create an event for the individual to appear within the Last 24 hours period in AC', async () => {
			const events = individuals
				.map((individual) => ({
					applicationId: 'Page',
					canonicalUrl: pageURL1,
					channelId: channel.id,
					eventDate: date1.toISOString(),
					eventId: 'pageViewed',
					title: pageTitle1,
					userId: individual.id,
				}))
				.concat(
					individuals.map((individual) => ({
						applicationId: 'Page',
						canonicalUrl: pageURL2,
						channelId: channel.id,
						eventDate: date1.toISOString(),
						eventId: 'pageViewed',
						referrer: pageURL1,
						title: pageTitle2,
						userId: individual.id,
					}))
				)
				.concat(
					individuals.map((individual) => ({
						applicationId: 'Page',
						canonicalUrl: pageURL1,
						channelId: channel.id,
						eventDate: date1.toISOString(),
						eventId: 'pageViewed',
						referrer: pageURL2,
						title: pageTitle1,
						userId: individual.id,
					}))
				);

			await apiHelpers.jsonWebServicesOSBAsah.createEvents(events);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACWorkspace({page});
			await switchChannel({
				channelName: channel.name,
				page,
			});
		});

		await test.step('Go to Pages Tab', async () => {
			await navigateTo({
				page,
				pageName: 'Pages',
			});
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Access one of the pages on the list > Go to Path Tab', async () => {
			await navigateTo({
				page,
				pageName: pageTitle1,
			});
			await navigateTo({
				page,
				pageName: 'Path',
			});
		});

		await test.step('Check that My Page 2 and Direct Traffic appear as referral pages', async () => {
			await expect(page.getByText(pageTitle2).first()).toBeVisible();

			await expect(page.getByText('Direct Traffic')).toBeVisible();
		});

		await test.step('Check that My Page 2 and Drop Offs appear as exit pages', async () => {
			await expect(page.getByText(pageTitle2).first()).toBeVisible();

			await expect(page.getByText('Drop Offs')).toBeVisible();
		});
	}
);

test.skip(
	'Remove segments from the filter',
	{
		tag: '@LRAC-14836',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const individualName = 'user1';
		const individuals = [
			generateIndividual({
				name: individualName,
			}),
		];

		await test.step('Create an Individual directly in the AC database', async () => {
			await createIndividuals({
				apiHelpers,
				individuals,
			});
		});

		const date1 = new Date();

		await test.step('Create an event for the individual to appear within the Last 24 hours period in AC', async () => {
			const events = individuals.map((individual) => ({
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date1.toISOString(),
				eventId: 'pageViewed',
				referrer: 'https://www.google.com',
				title: pageTitle,
				userId: individual.id,
			}));

			await apiHelpers.jsonWebServicesOSBAsah.createEvents(events);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACWorkspace({page});
			await switchChannel({
				channelName: channel.name,
				page,
			});
		});

		await test.step('Go to Segments Dashboard and create a Static Segment', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.segmentPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await createStaticSegment(page);

			await setSegmentName({page, segmentName: 'Test Static Segment'});
		});

		await test.step('Add static member and save segment', async () => {
			await addStaticMember({
				memberNames: [`user1`],
				page,
			});

			await saveSegment(page);
		});

		await test.step('Go to Pages Tab', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.sitePage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await navigateTo({
				page,
				pageName: 'Pages',
			});
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Access one of the pages on the list > Go to Path Tab', async () => {
			await navigateTo({
				page,
				pageName: pageTitle,
			});
			await navigateTo({
				page,
				pageName: 'Path',
			});
		});

		const filterLabel = page
			.locator('span')
			.filter({hasText: 'Test Static Segment'})
			.first();

		await test.step('Add filter with created segment', async () => {
			await page.getByRole('button', {name: 'Filter'}).click();

			await page
				.getByRole('menuitem', {name: 'Test Static Segment'})
				.click();

			await expect(filterLabel).toBeVisible();
		});

		const numberOfViews = page.getByText('1', {exact: true}).first();

		await test.step('Check the number of views with the filter', async () => {
			await expect(numberOfViews).toBeVisible();
		});

		await test.step('Remove segment from filter', async () => {
			await page.getByLabel('Close').click();

			await expect(filterLabel).not.toBeVisible();
		});

		await test.step('Check the number of views without the filter', async () => {
			await expect(numberOfViews).toBeVisible();
		});
	}
);

test(
	'Search for a Page in Pages list by its canonical URL',
	{
		tag: '@LRAC-14794',
	},
	async ({analyticsChannel: channel, apiHelpers, page}) => {
		const individualName = 'user1';
		const individuals = [
			generateIndividual({
				name: individualName,
			}),
		];

		await test.step('Create an Individual directly in the AC database', async () => {
			await createIndividuals({
				apiHelpers,
				individuals,
			});
		});

		const date1 = new Date();

		await test.step('Create an event for the individual to appear within the Last 24 hours period in AC', async () => {
			const events = individuals.map((individual) => ({
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date1.toISOString(),
				eventId: 'pageViewed',
				title: pageTitle,
				userId: individual.id,
			}));

			await apiHelpers.jsonWebServicesOSBAsah.createEvents(events);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACWorkspace({page});
			await switchChannel({
				channelName: channel.name,
				page,
			});
		});

		await test.step('Go to Pages Tab', async () => {
			await navigateTo({
				page,
				pageName: 'Pages',
			});
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Assert the page list', async () => {
			await searchByTerm({page, searchTerm: 'https://www.liferay.com'});

			await expect(
				page.getByRole('link', {name: `${pageTitle}`})
			).toBeVisible();
		});
	}
);

test(
	'Path filter segment dropdown lists existing segments and reflects deletions',
	{
		tag: ['@LRAC-15060', '@LRAC-15061'],
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const individuals = [generateIndividual({name: 'user1'})];

		await createIndividuals({apiHelpers, individuals});

		const date = new Date();

		await apiHelpers.jsonWebServicesOSBAsah.createEvents(
			individuals.map((individual) => ({
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date.toISOString(),
				eventId: 'pageViewed',
				title: pageTitle,
				userId: individual.id,
			}))
		);

		const segmentNames = ['1', '2', '3', '4'].map(
			(n) => `Path Filter Segment ${n}`
		);

		const segments: Array<{id: string}> = [];

		for (const name of segmentNames) {
			const segment =
				await apiHelpers.jsonWebServicesOSBFaro.createIndividualSegment(
					{
						channelId: channel.id,
						groupId: project.groupId,
						name,
					}
				);

			segments.push(segment);
		}

		await navigateToACPageViaURL({
			acPage: ACPage.sitePage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await navigateTo({page, pageName: 'Pages'});

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		await navigateTo({page, pageName: pageTitle});

		await navigateTo({page, pageName: 'Path'});

		// Open the filter and assert all 4 segments are listed

		await page.getByRole('button', {name: 'Filter'}).click();

		for (const name of segmentNames) {
			await expect(page.getByRole('menuitem', {name})).toBeVisible();
		}

		await page.keyboard.press('Escape');

		// Delete the last segment via API and reload the path view

		await apiHelpers.jsonWebServicesOSBFaro.deleteIndividualSegments(
			`[${segments[3].id}]`,
			project.groupId
		);

		await page.reload();

		await navigateTo({page, pageName: 'Pages'});

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		await navigateTo({page, pageName: pageTitle});

		await navigateTo({page, pageName: 'Path'});

		await page.getByRole('button', {name: 'Filter'}).click();

		for (const name of segmentNames.slice(0, 3)) {
			await expect(page.getByRole('menuitem', {name})).toBeVisible();
		}

		// Search for the deleted segment and assert it is no longer listed

		await page.getByPlaceholder('Search').last().fill(segmentNames[3]);

		await expect(
			page.getByRole('menuitem', {name: segmentNames[3]})
		).toHaveCount(0);
	}
);
