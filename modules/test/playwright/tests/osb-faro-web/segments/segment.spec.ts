/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {liferayConfig} from '../../../liferay.config';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {syncAnalyticsCloudViaAPI} from '../../analytics-settings-web/main/utils/analytics-settings';
import {faroConfig} from '../main/faro.config';
import {switchChannel} from '../main/utils/channel';
import {
	addBreakdownByAttribute,
	goToDistributionTabAndSelectAttribute,
	viewBreakdownRechartsData,
} from '../main/utils/distribution';
import {changeEventDisplayName} from '../main/utils/event-definitions';
import {
	Individual,
	createIndividuals,
	generateIndividual,
} from '../main/utils/individuals';
import {waitForLoading} from '../main/utils/loading';
import {Nanites, runNanites} from '../main/utils/nanites';
import {
	ACPage,
	navigateTo,
	navigateToACPageViaURL,
	navigateToACWorkspace,
} from '../main/utils/navigation';
import {
	addNestedSegmentField,
	addSegmentField,
	addStaticMember,
	createBatchSegment,
	createDynamicSegment,
	createStaticSegment,
	editCriteriaAttributeValue,
	editCriteriaConjunction,
	editSegment,
	includeAnonymousToggle,
	saveSegment,
	selectAsset,
	selectOperator,
	setSegmentName,
	viewSegmentCriteriaCard,
	viewSegmentMembershipCount,
} from '../main/utils/segments';
import {SegmentConditions} from '../main/utils/selectors';
import {
	searchByTerm,
	selectPaginationItemsPerPage,
	selectPaginationPageNumber,
	viewNameListInOrder,
	viewNameNotPresentOnTableList,
	viewNameOnTableList,
	viewPaginationResults,
} from '../main/utils/utils';

export const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	instanceSettingsPagesTest,
	isolatedChannelTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

test(
	'Add a Batch segment using an individual property',
	{
		tag: '@LRAC-11460',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const individualName = 'ac';
		const individuals = [
			generateIndividual({
				name: individualName,
			}),
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

		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await createBatchSegment(page);

		await test.step('Add email criteria and fill in', async () => {
			await addSegmentField({
				criterionName: 'Email Address',
				criterionType: 'Individual',
				page,
			});

			await selectOperator({
				operator: 'contains',
				operatorField: SegmentConditions.criteriaCondition,
				page,
			});

			await editCriteriaAttributeValue({
				attributeValue: individualName,
				page,
			});

			const dropdownItems = page.locator(
				'.dropdown-menu.dropdown-menu-select.show .dropdown-item'
			);

			await expect(dropdownItems).toHaveCount(1);
			await expect(dropdownItems).toHaveText(
				`${individualName}@liferay.com`
			);
		});
	}
);

test.skip(
	'Check events criteria shows which data source data came from',
	{
		tag: '@LRAC-8233',
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
		const pageTitle = 'AC Page';

		await test.step('Create an event for the individual to appear within the Last 24 hours period in AC', async () => {
			const events = individuals.map((individual) => ({
				applicationId: 'Page',
				assetId: 'https://www.liferay.com/ac-page',
				assetTitle: pageTitle,
				canonicalUrl: 'https://www.liferay.com/ac-page',
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

		await test.step('Access the dynamic segment creation page > Add the Viewed Page criteria', async () => {
			await navigateTo({
				page,
				pageName: 'Segments',
			});

			await createDynamicSegment(page);

			await addSegmentField({
				criterionName: 'Viewed Page',
				criterionType: 'Events',
				page,
			});
		});

		await test.step('Click on the Select button of the Viewed Page criteria', async () => {
			await page.getByRole('button', {name: 'Select'}).click();
		});

		await test.step('Check that the modal displays the page that was interacted and the data source that originated the data', async () => {
			await viewNameOnTableList({
				itemNames: pageTitle,
				page,
			});

			await expect(
				page.locator(`tr:has-text("${pageTitle}")`)
			).toBeVisible();
		});
	}
);

test.skip(
	'Check if updated custom event displayName is shown on segment criteria card',
	{
		tag: '@LPD-27065',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const customEventName = 'CustomEvent' + new Date().getTime();

		await test.step('Send a custom event', async () => {
			const eventAttributeName = 'propString';
			const date = new Date();

			await apiHelpers.jsonWebServicesOSBAsah.createEvents([
				{
					applicationId: 'CustomEvent',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date.toISOString(),
					eventId: customEventName,
					properties: [
						{
							name: eventAttributeName,
							value: 'testAttribute',
						},
					],
					title: 'Liferay',
					userId: '1',
				},
			]);

			await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
				{
					applicationId: 'CustomEvent',
					displayName: customEventName,
					eventAttributeDefinitions: [
						{
							dataType: 'STRING',
							displayName: eventAttributeName,
							name: eventAttributeName,
							type: 'LOCAL',
						},
					],
					name: customEventName,
					type: 'CUSTOM',
				},
			]);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.sitePage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});
		});

		await test.step('Go to Settings > Go to Events > Go to Custom Events Tab', async () => {
			await navigateTo({
				page,
				pageName: 'Settings',
			});
			await navigateTo({
				page,
				pageName: 'Definitions',
			});
			await navigateTo({
				page,
				pageName: 'Events',
			});
			await navigateTo({
				page,
				pageName: 'Custom Events',
			});
		});

		const newCustomEventName = `${customEventName}EV`;

		await test.step('Change the display name of the event', async () => {
			await changeEventDisplayName({
				eventName: customEventName,
				newEventName: newCustomEventName,
				page,
			});

			await expect(
				page.getByText(newCustomEventName).nth(1)
			).toBeVisible();
			await page.locator('button.close').click();
		});

		await test.step('Go to Segments', async () => {
			await navigateTo({
				page,
				pageName: 'Exit Settings',
			});
			await navigateTo({
				page,
				pageName: 'Segments',
			});
		});

		await test.step('Create dynamic segment', async () => {
			await createDynamicSegment(page);
		});

		await test.step('Check that the custom event with the updated name appears in the list of criteria', async () => {
			await expect(page.getByText(newCustomEventName)).toBeVisible();
		});

		await test.step('Add the custom event criteria to the segment', async () => {
			await addSegmentField({
				criterionName: newCustomEventName,
				criterionType: 'Events',
				page,
			});
		});

		await test.step('Check that the added criteria is using the name of the updated custom event', async () => {
			expect(
				page
					.locator('div')
					.filter({hasText: `/^${newCustomEventName}$/`})
			).toBeTruthy();
		});

		await test.step('Add a value to the attribute value field', async () => {
			await editCriteriaAttributeValue({
				attributeValue: 'testAttribute',
				page,
			});
		});

		await test.step('Add a name to the segment', async () => {
			await setSegmentName({
				page,
				segmentName: 'Test Dynamic Segment',
			});
		});

		await test.step('Save the segment', async () => {
			await saveSegment(page);
		});

		await test.step('Check that the Segment Criteria card is displaying the segment rule with the name of the updated custom event', async () => {
			expect(
				page.getByRole('heading', {name: 'Segment Criteria'})
			).toBeTruthy();
			expect(page.getByText(newCustomEventName)).toBeTruthy();
		});

		await test.step('Edit the segment', async () => {
			await editSegment(page);
		});

		await test.step('Check that the list of criteria and the criteria being used in the segment are both using the name of the updated custom event', async () => {
			expect(
				page
					.locator('div')
					.filter({hasText: `/^${newCustomEventName}$/`})
			).toBeTruthy();

			expect(
				page
					.locator('li')
					.filter({hasText: `/^${newCustomEventName}$/`})
			).toBeTruthy();
		});
	}
);

test.skip(
	'Create a segment with behavior of commenting on a blog',
	{
		tag: '@Legacy',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const knownIndividualName = 'ac';
		const knownIndividual = [
			generateIndividual({
				name: knownIndividualName,
			}),
		];

		await test.step('Create the known individuals directly in the AC database', async () => {
			await createIndividuals({
				apiHelpers,
				individuals: knownIndividual,
			});
		});

		const anonymousIdentityID = '87';
		const date = new Date();

		await test.step('Create an identity for an anonymous individual directly in the AC database', async () => {
			await apiHelpers.jsonWebServicesOSBAsah.createIdentities([
				{
					createDate: date.toISOString(),
					id: anonymousIdentityID,
				},
			]);
		});

		const pageName = 'Liferay Blog - AC Page';

		await test.step('Create blogViewed and posted events for known and anonymous individuals', async () => {
			const blogId = '1905';

			await apiHelpers.jsonWebServicesOSBAsah.createEvents(
				knownIndividual.map((individual) => ({
					applicationId: 'Blog',
					assetId: blogId,
					assetTitle: pageName,
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					dataSourceId: 0,
					eventDate: date.toISOString(),
					eventId: 'blogViewed',
					title: pageName,
					userId: individual.id,
				}))
			);

			await apiHelpers.jsonWebServicesOSBAsah.createEvents(
				knownIndividual.map((individual) => ({
					applicationId: 'Comment',
					assetId: blogId,
					assetTitle: pageName,
					canonicalUrl: 'https://www.liferay.com',
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
					title: pageName,
					userId: individual.id,
				}))
			);

			await apiHelpers.jsonWebServicesOSBAsah.createEvents([
				{
					applicationId: 'Comment',
					assetId: blogId,
					assetTitle: pageName,
					canonicalUrl: 'https://www.liferay.com',
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
					title: pageName,
					userId: anonymousIdentityID,
				},
			]);
		});

		await test.step('Create a session for the known individual', async () => {
			await apiHelpers.jsonWebServicesOSBAsah.createSessions(
				knownIndividual.map((individual) => ({
					channelId: channel.id,
					id: individual.id,
					sessionEnd: date.toISOString(),
					sessionStart: date.toISOString(),
					userId: individual.id,
				}))
			);
		});

		await test.step('Create dynamic segment', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.segmentPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await createDynamicSegment(page);

			await addSegmentField({
				criterionName: 'Commented on Blog',
				criterionType: 'Events',
				page,
			});

			await selectAsset({
				assetName: pageName,
				page,
			});

			await includeAnonymousToggle({
				enable: true,
				page,
			});

			await setSegmentName({
				page,
				segmentName: 'Test Commented on Blog Segment',
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

		await test.step('Reload the segment page to clear the cache', async () => {
			await waitForLoading(page);

			await page.reload();

			await waitForLoading(page);
		});

		await test.step('Check the segment member count in the membership', async () => {
			await navigateTo({
				page,
				pageName: 'Membership',
			});

			await expect(
				page
					.locator('li')
					.filter({hasText: 'Known Members:'})
					.locator('b')
			).toHaveText('1');

			await expect(
				page
					.locator('li')
					.filter({hasText: 'Anonymous Members:'})
					.locator('b')
			).toHaveText('1');

			await expect(
				page
					.locator('li')
					.filter({hasText: 'Total Members:'})
					.locator('b')
			).toHaveText('2');
		});

		await test.step('Check that the correct known member appears in the membership tab', async () => {
			await viewNameOnTableList({
				itemNames: `${knownIndividualName} Smith`,
				page,
			});
		});
	}
);

test.skip(
	'Search the Segment Profile Distribution',
	{
		tag: '@Legacy',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const firstIndividualsName = 'ac';
		const secondIndividualsName = 'dxp';
		const knownIndividuals = [
			generateIndividual({
				name: firstIndividualsName,
			}),
			generateIndividual({
				name: secondIndividualsName,
			}),
		];

		await test.step('Create 2 individuals directly in the AC database', async () => {
			await createIndividuals({
				apiHelpers,
				individuals: knownIndividuals,
			});
		});

		const date = new Date();

		await test.step('Create the first and second Individuals Events', async () => {
			await apiHelpers.jsonWebServicesOSBAsah.createEvents(
				knownIndividuals.map((individual) => ({
					applicationId: 'Page',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date.toISOString(),
					eventId: 'pageViewed',
					title: 'Liferay',
					userId: individual.id,
				}))
			);
		});

		await test.step('Create the first and second Individual Session', async () => {
			await apiHelpers.jsonWebServicesOSBAsah.createSessions(
				knownIndividuals.map((individual) => ({
					channelId: channel.id,
					id: individual.id,
					sessionEnd: date.toISOString(),
					sessionStart: date.toISOString(),
					userId: individual.id,
				}))
			);
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
				memberNames: [
					`${firstIndividualsName}@liferay.com`,
					`${secondIndividualsName}@liferay.com`,
				],
				page,
			});

			await saveSegment(page);
		});

		await test.step('Click on distribution tab and select familyName attribute', async () => {
			await goToDistributionTabAndSelectAttribute({
				attributeName: 'familyName',
				page,
			});
		});

		await test.step('Click on attribute result row', async () => {
			await page
				.locator('g.recharts-layer .recharts-bar-rectangle')
				.click();
		});

		await test.step('Check on side modal if a individual matches the attribute selected', async () => {
			await searchByTerm({
				page,
				searchTerm: `${firstIndividualsName} Smith`,
			});

			await viewNameOnTableList({
				itemNames: `${firstIndividualsName} Smith`,
				page,
			});
		});

		await test.step('Check on side modal if second individual is not visible after search', async () => {
			await viewNameNotPresentOnTableList({
				itemNames: `${secondIndividualsName} Smith`,
				page,
			});
		});

		await test.step('Do a search with random user and assert there are no results found', async () => {
			await searchByTerm({page, searchTerm: 'lorem'});

			await expect(
				page.getByText(
					'There are no results found.Please try a different search term.'
				)
			).toBeVisible();
		});
	}
);

test.skip(
	'Segment Composition shows Active and Known individuals',
	{
		tag: '@Legacy',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const knownIndividualName = 'ac';
		const knownIndividual = [
			generateIndividual({
				name: knownIndividualName,
			}),
		];

		await test.step('Create the known individuals directly in the AC database', async () => {
			await createIndividuals({
				apiHelpers,
				individuals: knownIndividual,
			});
		});

		const anonymousIdentityID = '87';
		const date = new Date();

		await test.step('Create an identity for an anonymous directly in the AC database', async () => {
			await apiHelpers.jsonWebServicesOSBAsah.createIdentities([
				{
					createDate: date.toISOString(),
					id: anonymousIdentityID,
				},
			]);
		});

		const pageName = 'Liferay - AC Page';

		await test.step('Create events for the anonymous and known individual to appear in AC', async () => {
			await apiHelpers.jsonWebServicesOSBAsah.createEvents(
				knownIndividual.map((individual) => ({
					applicationId: 'Page',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date.toISOString(),
					eventId: 'pageViewed',
					title: pageName,
					userId: individual.id,
				}))
			);

			await apiHelpers.jsonWebServicesOSBAsah.createEvents([
				{
					applicationId: 'Page',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date.toISOString(),
					eventId: 'pageViewed',
					title: pageName,
					userId: anonymousIdentityID,
				},
			]);
		});

		await test.step('Create a session for the known individual', async () => {
			await apiHelpers.jsonWebServicesOSBAsah.createSessions(
				knownIndividual.map((individual) => ({
					channelId: channel.id,
					id: individual.id,
					sessionEnd: date.toISOString(),
					sessionStart: date.toISOString(),
					userId: individual.id,
				}))
			);
		});

		const dynamicSegmentName = 'Test Dynamic Segment';

		await test.step('Create dynamic segment', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.segmentPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

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

			await addSegmentField({
				criterionName: 'email',
				criterionType: 'Individual',
				page,
			});

			await selectOperator({
				index: 1,
				operator: 'is unknown',
				operatorField: SegmentConditions.criteriaCondition,
				page,
			});

			await editCriteriaConjunction({page});

			await includeAnonymousToggle({
				enable: true,
				page,
			});

			await setSegmentName({
				page,
				segmentName: dynamicSegmentName,
			});

			await saveSegment(page);
		});

		await test.step('Create static segment', async () => {
			await navigateTo({
				page,
				pageName: 'Segments',
			});

			await createStaticSegment(page);

			await setSegmentName({
				page,
				segmentName: 'Test Static Segment',
			});

			await addStaticMember({
				memberNames: knownIndividualName,
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

		await test.step('Check the Segment Composition card data for the Static Segment', async () => {
			let activeCount = page
				.locator('li')
				.filter({hasText: 'Active Last 30 Days'})
				.getByTestId('active-count');

			await expect(activeCount).toHaveText('1');

			let activePorcentage = page
				.locator('li')
				.filter({hasText: 'Active Last 30 Days'})
				.getByTestId('active-porcentage');

			await expect(activePorcentage).toHaveText('100%');

			activeCount = page
				.locator('li')
				.filter({hasText: 'Known Members'})
				.getByTestId('active-count');

			await expect(activeCount).toHaveText('1');

			activePorcentage = page
				.locator('li')
				.filter({hasText: 'Known Members'})
				.getByTestId('active-porcentage');

			await expect(activePorcentage).toHaveText('100%');
		});

		await test.step('Go to Segments > Access the Dynamic Segment', async () => {
			await navigateTo({
				page,
				pageName: 'Segments',
			});

			await navigateTo({
				page,
				pageName: dynamicSegmentName,
			});
		});

		await test.step('Reload the segment page to clear the cache', async () => {
			await page.reload();

			await waitForLoading(page);
		});

		await test.step('Check the Segment Composition card data for the Dynamic Segment', async () => {
			let activeCount = page
				.locator('li')
				.filter({hasText: 'Active Last 30 Days'})
				.getByTestId('active-count');

			await expect(activeCount).toHaveText('2');

			let activePorcentage = page
				.locator('li')
				.filter({hasText: 'Active Last 30 Days'})
				.getByTestId('active-porcentage');

			await expect(activePorcentage).toHaveText('100%');

			activeCount = page
				.locator('li')
				.filter({hasText: 'Known Members'})
				.getByTestId('active-count');

			await expect(activeCount).toHaveText('1');

			activePorcentage = page
				.locator('li')
				.filter({hasText: 'Known Members'})
				.getByTestId('active-porcentage');

			await expect(activePorcentage).toHaveText('50%');
		});
	}
);

test.skip(
	'Segment criterias nest correctly in the criteria card',
	{
		tag: '@Legacy',
	},
	async ({analyticsChannel: channel, page, project}) => {
		await test.step('Create dynamic segment with a nested criterion', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.segmentPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await createDynamicSegment(page);

			await test.step('Add email criteria and fill in', async () => {
				await addSegmentField({
					criterionName: 'email',
					criterionType: 'Individual',
					page,
				});

				await selectOperator({
					operator: 'contains',
					operatorField: SegmentConditions.criteriaCondition,
					page,
				});

				await editCriteriaAttributeValue({
					attributeValue: '@liferay.com',
					page,
				});
			});

			await test.step('Add jobTitle criteria and fill in', async () => {
				await addSegmentField({
					criterionName: 'jobTitle',
					criterionType: 'Individual',
					page,
				});

				await selectOperator({
					index: 1,
					operator: 'does not contain',
					operatorField: SegmentConditions.criteriaCondition,
					page,
				});

				await editCriteriaAttributeValue({
					attributeValue: 'engineer',
					index: 1,
					page,
				});
			});

			await test.step('Add the familyName criteria as a nested criteria of the jobTitle and fill in', async () => {
				await addNestedSegmentField({
					criterionName: 'familyName',
					criterionType: 'Individual',
					nestedSegmentField: 'jobTitle',
					page,
				});

				await editCriteriaAttributeValue({
					attributeValue: 'Smith',
					index: 2,
					page,
				});

				await editCriteriaConjunction({
					index: 1,
					page,
				});
			});

			await setSegmentName({
				page,
				segmentName: 'Test Dynamic Segment',
			});

			await saveSegment(page);
		});

		await test.step('Check the criteria in the Segment Criteria card and verify if two of the criteria are nested', async () => {
			await viewSegmentCriteriaCard({
				criteriaRowIndex: 0,
				criteriaRowValue: 'Individual email contains "@liferay.com"',
				page,
			});

			await viewSegmentCriteriaCard({
				criteriaRowIndex: 0,
				criteriaRowValue:
					'Individual jobTitle does not contain "engineer"',
				page,
				parent: page.locator('.criteria-group').nth(1),
			});

			await viewSegmentCriteriaCard({
				criteriaRowIndex: 1,
				criteriaRowValue: 'Individual familyName is "Smith"',
				page,
				parent: page.locator('.criteria-group').nth(1),
			});
		});
	}
);

test.skip(
	'Segment distribution can be filtered by date',
	{
		tag: '@Legacy',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const firstIndividualName = 'ac';
		const secondndividualName = 'dxp';
		const individuals = [
			generateIndividual({
				name: firstIndividualName,
			}),
			generateIndividual({
				name: secondndividualName,
			}),
		];

		const birthDateFirstIndividual = '2008-06-11';
		const updatedIndividuals = [
			{
				...individuals[0],
				birthDate: `${birthDateFirstIndividual}T00:00:00.000Z`,
			},
			...individuals.slice(1),
		];

		await test.step('Create the 2 individuals directly in the AC database', async () => {
			await createIndividuals({
				apiHelpers,
				individuals: updatedIndividuals,
			});
		});

		const date = new Date();

		await test.step('Create an event for the individuals to appear in AC', async () => {
			await apiHelpers.jsonWebServicesOSBAsah.createEvents(
				updatedIndividuals.map((individual) => ({
					applicationId: 'Page',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date.toISOString(),
					eventId: 'pageViewed',
					title: 'Liferay',
					userId: individual.id,
				}))
			);
		});

		await test.step('Create a session for the known individuals', async () => {
			await apiHelpers.jsonWebServicesOSBAsah.createSessions(
				updatedIndividuals.map((individual) => ({
					channelId: channel.id,
					id: individual.id,
					sessionEnd: date.toISOString(),
					sessionStart: date.toISOString(),
					userId: individual.id,
				}))
			);
		});

		await test.step('Go to Segments > Create a Static Segment', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.segmentPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await createStaticSegment(page);

			await setSegmentName({page, segmentName: 'Test Static Segment'});

			await addStaticMember({
				memberNames: [firstIndividualName, secondndividualName],
				page,
			});

			await saveSegment(page);
		});

		await test.step('Add a new breakdown by birthDate attribute', async () => {
			await addBreakdownByAttribute({
				attributeName: 'birthDate',
				page,
			});
		});

		await test.step('Check if the correct results appear (birthDates and maximum counts)', async () => {
			await viewBreakdownRechartsData({
				attributeValue: '1970-01-01',
				maxCount: '1',
				page,
			});

			await viewBreakdownRechartsData({
				attributeValue: birthDateFirstIndividual,
				maxCount: '1',
				page,
			});
		});

		await test.step('Click on distribution tab and select birthDate attribute', async () => {
			await goToDistributionTabAndSelectAttribute({
				attributeName: 'birthDate',
				page,
			});
		});

		await test.step('Check if the correct results appear (birthDates and maximum counts)', async () => {
			await viewBreakdownRechartsData({
				attributeValue: '1970-01-01',
				maxCount: '1',
				page,
			});

			await viewBreakdownRechartsData({
				attributeValue: birthDateFirstIndividual,
				maxCount: '1',
				page,
			});
		});
	}
);

test.skip(
	'Segment overview distribution filtered by text',
	{
		tag: '@Legacy',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const knownIndividualName = 'ac';
		const knownIndividual = [
			generateIndividual({
				name: knownIndividualName,
			}),
		];

		await test.step('Create the known individual directly in the AC database', async () => {
			await createIndividuals({
				apiHelpers,
				individuals: knownIndividual,
			});
		});

		const date = new Date();

		await test.step('Create an event for the individual to appear in AC', async () => {
			await apiHelpers.jsonWebServicesOSBAsah.createEvents(
				knownIndividual.map((individual) => ({
					applicationId: 'Page',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date.toISOString(),
					eventId: 'pageViewed',
					title: 'Liferay',
					userId: individual.id,
				}))
			);
		});

		await test.step('Create a session for the known individual', async () => {
			await apiHelpers.jsonWebServicesOSBAsah.createSessions(
				knownIndividual.map((individual) => ({
					channelId: channel.id,
					id: individual.id,
					sessionEnd: date.toISOString(),
					sessionStart: date.toISOString(),
					userId: individual.id,
				}))
			);
		});

		await test.step('Go to Segments > Create a Static Segment', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.segmentPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await createStaticSegment(page);

			await setSegmentName({page, segmentName: 'Test Static Segment'});

			await addStaticMember({
				memberNames: knownIndividualName,
				page,
			});

			await saveSegment(page);
		});

		await test.step('Add a new breakdown by familyName attribute', async () => {
			await addBreakdownByAttribute({
				attributeName: 'familyName',
				page,
			});
		});

		await test.step('Check if the correct results appear (familyName and maximum count)', async () => {
			await viewBreakdownRechartsData({
				attributeValue: 'smith',
				maxCount: '1',
				page,
			});
		});
	}
);

test.skip(
	'Add segment using an organization property "organization"',
	{
		tag: '@Legacy',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {

		// Remove the .skip from the test after it has been analyzed and fixed

		const organization =
			await apiHelpers.headlessAdminUser.postOrganization();
		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await test.step('Add the new user to the organization', async () => {
			await apiHelpers.headlessAdminUser.assignUserToOrganizationByEmailAddress(
				organization.id,
				user.emailAddress
			);
		});

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel,
			project,
			syncedOrganizationIds: [Number(organization.id)],
		});

		await test.step('Interact with the user that is not part of the organization', async () => {
			await page.goto(liferayConfig.environment.baseUrl);

			await page.waitForTimeout(10000);
		});

		await test.step('Interact with the user that is part of the organization', async () => {
			await performLogout(page);

			await performLogin(page, user.alternateName);

			await page.goto(liferayConfig.environment.baseUrl);

			await page.waitForTimeout(10000);
		});

		await test.step('Create dynamic segment using the organization criteria', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.segmentPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await createDynamicSegment(page);

			await addSegmentField({
				criterionName: 'Organization',
				criterionType: 'Organization Attributes',
				page,
			});

			await selectAsset({
				assetName: organization.name,
				page,
			});

			await setSegmentName({
				page,
				segmentName: 'Test Organization Segment',
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

		await test.step('Reload the segment page to clear the cache', async () => {
			await waitForLoading(page);

			await page.reload();

			await waitForLoading(page);
		});

		await test.step('Check the segment member count in the membership', async () => {
			await navigateTo({
				page,
				pageName: 'Membership',
			});

			await viewSegmentMembershipCount({
				anonymousMemberCount: '0',
				knownMemberCount: '1',
				page,
				totalMemberCount: '1',
			});
		});

		await test.step('Check that the user that is part of organization appears in the membership list', async () => {
			await viewNameOnTableList({
				itemNames: user.givenName,
				page,
			});
		});

		await test.step('Check that the user that is not part of the organization will not appears in the membership list', async () => {
			await viewNameNotPresentOnTableList({
				itemNames: 'Test',
				page,
			});
		});
	}
);

test.skip(
	'Add segment using an individual property "user group"',
	{
		tag: '@Legacy',
	},
	async ({
		analyticsChannel: channel,
		apiHelpers,
		defaultUserAssociationsPage,
		instanceSettingsPage,
		page,
		project,
	}) => {

		// Remove the .skip from the test after it has been analyzed and fixed

		const userGroup = await apiHelpers.headlessAdminUser.postUserGroup();

		await test.step('Enable to assign new users to the created user group', async () => {
			await instanceSettingsPage.goToInstanceSetting(
				'Users',
				'Default User Associations'
			);

			await defaultUserAssociationsPage.userGroupsInput.fill(
				userGroup.name
			);
			await defaultUserAssociationsPage.saveButton.click();

			await waitForAlert(page);
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await syncAnalyticsCloudViaAPI({
			apiHelpers,
			channel,
			project,
			syncedUserGroupIds: [Number(userGroup.id)],
		});

		await test.step('Interact with the user that is not part of the user group', async () => {
			await page.goto(liferayConfig.environment.baseUrl);

			await page.waitForTimeout(10000);
		});

		await test.step('Interact with the user that is part of the user group', async () => {
			await performLogout(page);

			await performLogin(page, user.alternateName);

			await page.goto(liferayConfig.environment.baseUrl);

			await page.waitForTimeout(10000);
		});

		await test.step('Create dynamic segment using the user group criteria', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.segmentPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await createDynamicSegment(page);

			await addSegmentField({
				criterionName: 'User Group',
				criterionType: 'Individual',
				page,
			});

			await selectAsset({
				assetName: userGroup.name,
				page,
			});

			await setSegmentName({
				page,
				segmentName: 'Test User Group Segment',
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

		await test.step('Reload the segment page to clear the cache', async () => {
			await waitForLoading(page);

			await page.reload();

			await waitForLoading(page);
		});

		await test.step('Check the segment member count in the membership', async () => {
			await navigateTo({
				page,
				pageName: 'Membership',
			});

			await viewSegmentMembershipCount({
				anonymousMemberCount: '0',
				knownMemberCount: '1',
				page,
				totalMemberCount: '1',
			});
		});

		await test.step('Check that the user that is part of user group appears in the membership list', async () => {
			await viewNameOnTableList({
				itemNames: user.givenName,
				page,
			});
		});

		await test.step('Check that the user that is not part of the user group will not appears in the membership list', async () => {
			await viewNameNotPresentOnTableList({
				itemNames: 'Test',
				page,
			});
		});
	}
);

test('Segment criteria card lists every criterion when the segment has many', async ({
	analyticsChannel: channel,
	page,
	project,
}) => {
	const duplicateCount = 20;

	await navigateToACPageViaURL({
		acPage: ACPage.segmentPage,
		channelID: channel.id,
		page,
		projectID: project.groupId,
	});

	await createBatchSegment(page);

	// Add a date of birth criterion and duplicate it twenty times

	await addSegmentField({
		criterionName: 'Date of Birth',
		criterionType: 'Individual',
		page,
	});

	for (let i = 0; i < duplicateCount; i++) {
		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Duplicate'}),
			trigger: page
				.locator('.criterion')
				.filter({hasText: 'Date of Birth'})
				.first()
				.locator('button.dropdown-toggle'),
		});
	}

	await setSegmentName({
		page,
		segmentName: 'Dynamic Segment Test',
	});

	await saveSegment(page);

	// Verify every criterion is listed in the saved segment

	await expect(
		page.locator('.criteria-row').filter({hasText: 'Date of Birth'})
	).toHaveCount(duplicateCount + 1);
});

test(
	'Segment list supports default view, search, sort, and pagination',
	{
		tag: '@LPD-89756',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const segmentIds: string[] = [];

		const segmentNames = [
			'Dynamic Segment Test1',
			'Dynamic Segment Test2',
			'Dynamic Segment Test3',
			'Dynamic Segment Test4',
			'Dynamic Segment Test5',
		];

		try {

			// Create five batch segments through the Faro API

			for (const name of segmentNames) {
				const segment =
					await apiHelpers.jsonWebServicesOSBFaro.createIndividualSegment(
						{
							channelId: channel.id,
							groupId: project.groupId,
							name,
						}
					);

				segmentIds.push(segment.id);
			}

			// Default state lists the five segments with the 20 items per page selector

			await navigateToACPageViaURL({
				acPage: ACPage.segmentPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await viewNameOnTableList({
				itemNames: segmentNames,
				page,
			});

			await expect(
				page.locator(
					'.pagination-items-per-page button.dropdown-toggle'
				)
			).toHaveText('20 Items');

			await viewPaginationResults({
				page,
				paginationResults: 'Showing 1 to 5 of 5 entries.',
			});

			// Search filters the list and shows the empty state when nothing matches

			await searchByTerm({
				page,
				searchTerm: 'Test1',
			});

			await viewNameOnTableList({
				itemNames: 'Dynamic Segment Test1',
				page,
			});

			await viewNameNotPresentOnTableList({
				itemNames: ['Dynamic Segment Test2', 'Dynamic Segment Test5'],
				page,
			});

			await searchByTerm({
				page,
				searchTerm: 'NonExistentSegment',
			});

			await expect(
				page.getByText('There are no results found.')
			).toBeVisible();

			// Sort by Name ascending then toggle to descending

			await navigateToACPageViaURL({
				acPage: ACPage.segmentPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: 'Name'}),
				trigger: page.getByRole('button', {name: 'Order'}),
			});

			await viewNameListInOrder({
				itemNames: segmentNames,
				page,
			});

			// Paginate with four items per page; the fifth segment lives on page two

			await selectPaginationItemsPerPage({
				itemsPerPage: '4',
				page,
			});

			await viewPaginationResults({
				page,
				paginationResults: 'Showing 1 to 4 of 5 entries.',
			});

			await viewNameListInOrder({
				itemNames: segmentNames.slice(0, 4),
				page,
			});

			await selectPaginationPageNumber({
				page,
				paginationPageNumber: '2',
			});

			await viewPaginationResults({
				page,
				paginationResults: 'Showing 5 to 5 of 5 entries.',
			});

			await viewNameOnTableList({
				itemNames: 'Dynamic Segment Test5',
				page,
			});
		}
		finally {
			if (segmentIds.length) {
				await apiHelpers.jsonWebServicesOSBFaro.deleteIndividualSegments(
					`[${segmentIds.join(',')}]`,
					project.groupId
				);
			}
		}
	}
);

test(
	'Cancelling the batch segment editor discards the in-progress segment',
	{
		tag: '@LRAC-8476',
	},
	async ({analyticsChannel: channel, page, project}) => {
		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await createBatchSegment(page);

		await setSegmentName({
			page,
			segmentName: getRandomString(),
		});

		await addSegmentField({
			criterionName: 'Email Address',
			criterionType: 'Individual',
			page,
		});

		await editCriteriaAttributeValue({
			attributeValue: 'userea@liferay.com',
			page,
		});

		// Dismiss the value autocomplete so it does not intercept the cancel click

		await page.keyboard.press('Escape');

		// Cancel and confirm leave page

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('button', {name: 'Leave Page'}),
			trigger: page.getByRole('link', {name: 'Cancel'}),
		});

		// The cancelled segment is not persisted to the list

		await expect(
			page.getByText('There are no segments found.')
		).toBeVisible();
	}
);

test(
	'Create a batch segment with an email criterion and see it listed',
	{
		tag: '@LPD-89756',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const individualName = 'user' + getRandomString();
		const individuals = [generateIndividual({name: individualName})];

		await createIndividuals({apiHelpers, individuals});

		const date = new Date();

		await apiHelpers.jsonWebServicesOSBAsah.createSessions(
			individuals.map((individual) => ({
				channelId: channel.id,
				id: individual.id,
				sessionEnd: date.toISOString(),
				sessionStart: date.toISOString(),
				userId: individual.id,
			}))
		);

		// Open the editor and configure an email criterion

		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await createBatchSegment(page);

		const segmentName = getRandomString();

		await setSegmentName({page, segmentName});

		await addSegmentField({
			criterionName: 'Email Address',
			criterionType: 'Individual ',
			page,
		});

		await editCriteriaAttributeValue({
			attributeValue: `${individualName}@liferay.com`,
			page,
		});

		// Dismiss the value autocomplete so it does not intercept the cancel click

		await page.keyboard.press('Escape');

		// Preview the membership and verify the individual appears

		await clickAndExpectToBeVisible({
			target: page.getByText('Known Segment Members'),
			trigger: page.getByRole('button', {name: 'View Members'}),
		});

		await viewNameOnTableList({
			itemNames: `${individualName} Smith`,
			page,
		});

		await page.getByRole('button', {name: 'Done'}).click();

		// Save the segment and verify it appears in the segments list

		await saveSegment(page);

		await navigateTo({page, pageName: 'Segments'});

		await viewNameOnTableList({
			itemNames: segmentName,
			page,
		});
	}
);

test(
	'Create a batch segment that includes anonymous individuals',
	{
		tag: '@LRAC-7962',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const anonymousIdentityIDs = [
			getRandomString(),
			getRandomString(),
			getRandomString(),
		];
		const date = new Date();

		// Create three anonymous identities with a pageViewed event on a per-run unique page and a session each

		await apiHelpers.jsonWebServicesOSBAsah.createIdentities(
			anonymousIdentityIDs.map((id) => ({
				createDate: date.toISOString(),
				id,
			}))
		);

		const pageTitle = getRandomString();
		const pageURL = `https://www.liferay.com/${pageTitle}`;

		await apiHelpers.jsonWebServicesOSBAsah.createEvents(
			anonymousIdentityIDs.map((id) => ({
				applicationId: 'Page',
				assetId: pageURL,
				assetTitle: pageTitle,
				canonicalUrl: pageURL,
				channelId: channel.id,
				dataSourceId: 0,
				eventDate: date.toISOString(),
				eventId: 'pageViewed',
				title: pageTitle,
				userId: id,
			}))
		);

		await apiHelpers.jsonWebServicesOSBAsah.createSessions(
			anonymousIdentityIDs.map((id) => ({
				channelId: channel.id,
				id,
				sessionEnd: date.toISOString(),
				sessionStart: date.toISOString(),
				userId: id,
			}))
		);

		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		// Create a batch segment that narrows to anonymous individuals who viewed the per-run page, with include-anonymous on

		await createBatchSegment(page);

		await setSegmentName({page, segmentName: getRandomString()});

		await addSegmentField({
			criterionName: 'First Name',
			criterionType: 'Individual',
			page,
		});

		await selectOperator({
			operator: 'is unknown',
			operatorField: SegmentConditions.criteriaCondition,
			page,
		});

		await addSegmentField({
			criterionName: 'Viewed Page',
			criterionType: 'Events',
			page,
		});

		await selectAsset({assetName: pageTitle, page});

		await includeAnonymousToggle({enable: true, page});

		await saveSegment(page);

		// The saved segment shows the includes anonymous individuals label

		await expect(
			page.getByText('Includes Anonymous Individuals')
		).toBeVisible();

		// Reopen the editor and verify the preview reflects the three anonymous individuals seeded for this run

		await editSegment(page);

		await expect(page.getByText(/Total Members: 3/)).toBeVisible();
	}
);

test(
	'Duplicate a criterion in the batch segment editor and save with distinct values',
	{
		tag: '@LRAC-8474',
	},
	async ({analyticsChannel: channel, page, project}) => {
		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await createBatchSegment(page);

		const segmentName = getRandomString();

		await setSegmentName({page, segmentName});

		// Add a job title criterion and duplicate it

		await addSegmentField({
			criterionName: 'Job Title',
			criterionType: 'Individual',
			page,
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Duplicate'}),
			trigger: page
				.locator('.criterion')
				.filter({hasText: 'Job Title'})
				.first()
				.locator('button.dropdown-toggle'),
		});

		await expect(
			page.locator('.criterion').filter({hasText: 'Job Title'})
		).toHaveCount(2);

		// Fill the first criterion: contains "web developer"

		await selectOperator({
			operator: 'contains',
			operatorField: SegmentConditions.criteriaCondition,
			page,
		});

		await editCriteriaAttributeValue({
			attributeValue: 'web developer',
			page,
		});

		// Fill the second criterion: contains "lawyer"

		await selectOperator({
			index: 1,
			operator: 'contains',
			operatorField: SegmentConditions.criteriaCondition,
			page,
		});

		await editCriteriaAttributeValue({
			attributeValue: 'lawyer',
			index: 1,
			page,
		});

		// Dismiss the value autocomplete before submitting

		await page.keyboard.press('Escape');

		await saveSegment(page);

		// Both criteria appear in the saved Segment Criteria card

		await viewSegmentCriteriaCard({
			criteriaRowIndex: 0,
			criteriaRowValue: 'Individual Job Title contains "web developer"',
			page,
		});

		await viewSegmentCriteriaCard({
			criteriaRowIndex: 1,
			criteriaRowValue: 'Individual Job Title contains "lawyer"',
			page,
		});
	}
);

test(
	'Delete a criterion from the batch segment editor',
	{
		tag: '@LRAC-8595',
	},
	async ({analyticsChannel: channel, page, project}) => {
		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await createBatchSegment(page);

		await setSegmentName({page, segmentName: getRandomString()});

		// Add a job title criterion and a last name criterion

		await addSegmentField({
			criterionName: 'Job Title',
			criterionType: 'Individual',
			page,
		});

		await selectOperator({
			operator: 'contains',
			operatorField: SegmentConditions.criteriaCondition,
			page,
		});

		await editCriteriaAttributeValue({
			attributeValue: 'engineer',
			page,
		});

		await page.keyboard.press('Escape');

		await addSegmentField({
			criterionName: 'Last Name',
			criterionType: 'Individual ',
			page,
		});

		await selectOperator({
			index: 1,
			operator: 'contains',
			operatorField: SegmentConditions.criteriaCondition,
			page,
		});

		await editCriteriaAttributeValue({
			attributeValue: 'Smith',
			index: 1,
			page,
		});

		await expect(page.locator('.criterion')).toHaveCount(2);

		await page.keyboard.press('Escape');

		await saveSegment(page);

		// Delete the Job Title criterion via its dropdown menuitem Delete

		await editSegment(page);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Delete'}),
			trigger: page
				.locator('.criterion')
				.filter({hasText: 'Job Title'})
				.first()
				.getByLabel('Menu', {exact: true}),
		});

		// Only the Last Name criterion remains

		await expect(page.locator('.criterion')).toHaveCount(1);
		await expect(page.locator('.criterion')).toContainText('Last Name');

		await saveSegment(page);

		await viewSegmentCriteriaCard({
			criteriaRowIndex: 0,
			criteriaRowValue: 'Individual Last Name contains "Smith"',
			page,
		});
	}
);

test(
	'Batch segment preview reflects criteria changes in real time',
	{
		tag: '@LRAC-8475',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const name = getRandomString();
		const firstFullName = `userfn${name} Smith`;
		const secondFullName = `user1${name} Smith`;

		const individuals: Individual[] = [
			generateIndividual({name: `userfn${name}`}),
			generateIndividual({name: `user1${name}`}),
		];

		await createIndividuals({apiHelpers, individuals});

		const date = new Date();

		await apiHelpers.jsonWebServicesOSBAsah.createSessions(
			individuals.map((individual) => ({
				channelId: channel.id,
				id: individual.id,
				sessionEnd: date.toISOString(),
				sessionStart: date.toISOString(),
				userId: individual.id,
			}))
		);

		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await createBatchSegment(page);

		const segmentName = getRandomString();

		await setSegmentName({page, segmentName});

		// Add an email criterion scoped to this run to match both individuals

		await addSegmentField({
			criterionName: 'Email Address',
			criterionType: 'Individual',
			page,
		});

		await selectOperator({
			operator: 'contains',
			operatorField: SegmentConditions.criteriaCondition,
			page,
		});

		await editCriteriaAttributeValue({
			attributeValue: `${name}@liferay.com`,
			page,
		});

		// The preview lists both individuals

		await clickAndExpectToBeVisible({
			target: page.getByText('Known Segment Members'),
			trigger: page.getByRole('button', {name: 'View Members'}),
		});

		await viewNameOnTableList({
			itemNames: [firstFullName, secondFullName],
			page,
		});

		await page.getByRole('button', {name: 'Done'}).click();

		// Add a first name criterion narrowing the segment to the second individual

		await addSegmentField({
			criterionName: 'First Name',
			criterionType: 'Individual',
			page,
		});

		await selectOperator({
			index: 1,
			operator: 'contains',
			operatorField: SegmentConditions.criteriaCondition,
			page,
		});

		await editCriteriaAttributeValue({
			attributeValue: `user1${name}`,
			index: 1,
			page,
		});

		// The preview now lists only the second individual

		await clickAndExpectToBeVisible({
			target: page.getByText('Known Segment Members'),
			trigger: page.getByRole('button', {name: 'View Members'}),
		});

		await viewNameOnTableList({
			itemNames: secondFullName,
			page,
		});

		await viewNameNotPresentOnTableList({
			itemNames: firstFullName,
			page,
		});
	}
);

test(
	'Segment name uniqueness is enforced on create and on edit',
	{
		tag: ['@LRAC-14041', '@LRAC-14043'],
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const segmentIds: string[] = [];

		try {
			const firstSegmentName = 'First Segment Name';

			const firstSegment =
				await apiHelpers.jsonWebServicesOSBFaro.createIndividualSegment(
					{
						channelId: channel.id,
						groupId: project.groupId,
						name: firstSegmentName,
					}
				);

			segmentIds.push(firstSegment.id);

			await navigateToACPageViaURL({
				acPage: ACPage.segmentPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			// Open the editor and try to save a case-insensitive duplicate name

			await createBatchSegment(page);

			await setSegmentName({
				page,
				segmentName: firstSegmentName,
			});

			await addSegmentField({
				criterionName: 'Email Address',
				criterionType: 'Individual',
				page,
			});

			await editCriteriaAttributeValue({
				attributeValue: 'userea@liferay.com',
				page,
			});

			await page.keyboard.press('Escape');

			await page.locator('button[type="submit"]').click();

			await waitForAlert(page, 'This segment name is currently in use', {
				autoClose: false,
				type: 'warning',
			});

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('button', {name: 'Leave Page'}),
				trigger: page.getByRole('link', {name: 'Cancel'}),
			});

			await viewPaginationResults({
				page,
				paginationResults: 'Showing 1 to 1 of 1 entry.',
			});

			// Create a second segment via API for the edit collision test

			const secondSegmentName = 'Second Segment Name';

			const secondSegment =
				await apiHelpers.jsonWebServicesOSBFaro.createIndividualSegment(
					{
						channelId: channel.id,
						groupId: project.groupId,
						name: secondSegmentName,
					}
				);

			segmentIds.push(secondSegment.id);

			await page.reload();

			// Open the first segment and try to rename it to the second segment's name

			await page.goto(
				`${faroConfig.environment.baseUrl}/workspace/${project.groupId}/${channel.id}/contacts/segments/${firstSegment.id}/edit`
			);

			await expect(
				page.getByRole('button', {name: 'Save Segment'})
			).toBeVisible();

			// Add a criterion so the form is valid and the rename can be submitted

			await addSegmentField({
				criterionName: 'Email Address',
				criterionType: 'Individual',
				page,
			});

			await editCriteriaAttributeValue({
				attributeValue: 'userea@liferay.com',
				page,
			});

			await page.keyboard.press('Escape');

			await setSegmentName({
				page,
				segmentName: secondSegmentName,
			});

			await page.locator('button[type="submit"]').click();

			await waitForAlert(page, 'This segment name is currently in use', {
				autoClose: false,
				type: 'warning',
			});

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('button', {name: 'Leave Page'}),
				trigger: page.getByRole('link', {name: 'Cancel'}),
			});

			// Both segments retain their original names

			await navigateToACPageViaURL({
				acPage: ACPage.segmentPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await viewPaginationResults({
				page,
				paginationResults: 'Showing 1 to 2 of 2 entries.',
			});

			await expect(
				page.getByRole('link', {name: firstSegmentName})
			).toBeAttached();

			await expect(
				page.getByRole('link', {name: secondSegmentName})
			).toBeAttached();
		}
		finally {
			if (segmentIds.length) {
				await apiHelpers.jsonWebServicesOSBFaro.deleteIndividualSegments(
					`[${segmentIds.join(',')}]`,
					project.groupId
				);
			}
		}
	}
);

test(
	'Save, edit, and delete a batch segment with a Viewed Blog Web Behavior criterion',
	{
		tag: ['@LRAC-8588', '@LRAC-8597', '@LRAC-8598'],
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {

		// Seed an individual and a blogViewed event so the viewed blog criterion has matching data

		const assetTitle = 'Blogs AC Title';

		const individual = generateIndividual({
			name: 'blogreader' + getRandomString(),
		});

		await createIndividuals({apiHelpers, individuals: [individual]});

		const date = new Date();

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Blog',
				assetId: '1905',
				assetTitle,
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId: 0,
				eventDate: date.toISOString(),
				eventId: 'blogViewed',
				title: assetTitle,
				userId: individual.id,
			},
		]);

		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		// Create a batch segment with a viewed blog criterion at least 4 times

		await createBatchSegment(page);

		const segmentName = getRandomString();

		await setSegmentName({page, segmentName});

		await addSegmentField({
			criterionName: 'Viewed Blog',
			criterionType: 'Events',
			page,
		});

		await selectAsset({assetName: assetTitle, page});

		await page.locator('input[type="number"]').fill('4');

		await saveSegment(page);

		// The saved segment shows the criterion with the matching asset and count

		await expect(page.locator('.criteria-card-root')).toContainText(
			assetTitle
		);

		await expect(page.locator('.criteria-card-root')).toContainText('4');

		// Navigate back to the segments list and assert the kebab offers Edit and Delete

		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Edit'}),
			trigger: page.locator('.dropdown-action'),
		});

		// Edit via the kebab Edit action, change the count to 5, and rename

		await expect(
			page.getByRole('button', {name: 'Save Segment'})
		).toBeVisible();

		await page.locator('input[type="number"]').fill('5');

		const renamedSegment = getRandomString();

		await setSegmentName({page, segmentName: renamedSegment});

		await saveSegment(page);

		// The edited segment shows the updated count and the renamed name in the list

		await expect(page.locator('.criteria-card-root')).toContainText('5');

		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await viewNameOnTableList({itemNames: renamedSegment, page});

		// Delete via the kebab delete action and confirm

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Delete'}),
			trigger: page.locator('.dropdown-action'),
		});

		await page
			.getByRole('dialog')
			.getByRole('button', {name: 'Delete'})
			.click();

		await waitForAlert(page, 'Success:The segment has been deleted.', {
			autoClose: false,
		});

		await expect(
			page.getByText('There are no segments found.')
		).toBeVisible();
	}
);

test(
	`Save a batch segment with a Viewed Document & Media Web Behavior criterion`,
	{
		tag: '@LRAC-9224',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const individual = generateIndividual({
			name: 'webbehavior' + getRandomString(),
		});

		await createIndividuals({apiHelpers, individuals: [individual]});

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Document',
				assetId: '1',
				assetTitle: 'DM AC Title',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId: 0,
				eventDate: new Date().toISOString(),
				eventId: 'documentPreviewed',
				title: 'DM AC Title',
				userId: individual.id,
			},
		]);

		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await createBatchSegment(page);

		const segmentName = getRandomString();

		await setSegmentName({page, segmentName});

		await addSegmentField({
			criterionName: 'Viewed Document & Media',
			criterionType: 'Events',
			page,
		});

		await selectAsset({assetName: 'DM AC Title', page});

		await saveSegment(page);

		await expect(page.locator('.criteria-card-root')).toContainText(
			'DM AC Title'
		);

		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await viewNameOnTableList({itemNames: segmentName, page});
	}
);

test(
	'Save a batch segment with a Viewed Web Content criterion filtered to a Last 7 days time period',
	{
		tag: '@LRAC-9222',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const assetTitle = 'Web Content AC Title';

		const individual = generateIndividual({
			name: 'wcviewer' + getRandomString(),
		});

		await createIndividuals({apiHelpers, individuals: [individual]});

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'WebContent',
				assetId: '1',
				assetTitle,
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId: 0,
				eventDate: new Date().toISOString(),
				eventId: 'webContentViewed',
				title: assetTitle,
				userId: individual.id,
			},
		]);

		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await createBatchSegment(page);

		const segmentName = getRandomString();

		await setSegmentName({page, segmentName});

		await addSegmentField({
			criterionName: 'Viewed Web Content',
			criterionType: 'Events',
			page,
		});

		await selectAsset({assetName: assetTitle, page});

		// Switch the time period dropdown to Last 7 days

		await page
			.locator('button[data-testid="clay-select"]', {
				hasText: 'Last 24 hours',
			})
			.click();

		await page.getByText('Last 7 days').click();

		await saveSegment(page);

		await expect(page.locator('.criteria-card-root')).toContainText(
			'Last 7 days'
		);
	}
);

test(
	'Save a batch segment with a Viewed Blog Has Not Web Behavior criterion',
	{
		tag: '@LRAC-Legacy-HasNot',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const assetTitle = 'Blogs AC Title';

		const individual = generateIndividual({
			name: 'blognothad' + getRandomString(),
		});

		await createIndividuals({apiHelpers, individuals: [individual]});

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Blog',
				assetId: '1',
				assetTitle,
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				dataSourceId: 0,
				eventDate: new Date().toISOString(),
				eventId: 'blogViewed',
				title: assetTitle,
				userId: individual.id,
			},
		]);

		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await createBatchSegment(page);

		const segmentName = getRandomString();

		await setSegmentName({page, segmentName});

		await addSegmentField({
			criterionName: 'Viewed Blog',
			criterionType: 'Events',
			page,
		});

		await selectAsset({assetName: assetTitle, page});

		// Switch the "has" operator to "has not"

		await page
			.locator('.criteria-statement button.form-control')
			.first()
			.click();

		await page
			.locator('button.dropdown-item', {hasText: /^has not$/})
			.click();

		await saveSegment(page);

		await expect(page.locator('.criteria-card-root')).toContainText(
			'has not'
		);
	}
);

test(
	'Delete a batch segment via the in-editor Delete Segment button',
	{
		tag: ['@LPD-90772', '@LRAC-11536'],
	},
	async ({analyticsChannel: channel, page, project}) => {
		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await createBatchSegment(page);

		await setSegmentName({page, segmentName: getRandomString()});

		await addSegmentField({
			criterionName: 'Job Title',
			criterionType: 'Individual',
			page,
		});

		await selectOperator({
			operator: 'is unknown',
			operatorField: SegmentConditions.criteriaCondition,
			page,
		});

		await saveSegment(page);

		// Re-enter the editor from the segments list and delete the segment via the in-editor Delete Segment button

		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Edit'}),
			trigger: page.locator('.dropdown-action'),
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page
				.getByRole('dialog')
				.getByRole('button', {name: 'Delete'}),
			trigger: page.getByRole('button', {name: 'Delete Segment'}),
		});

		await waitForAlert(page, 'Success:The segment has been deleted.', {
			autoClose: false,
		});

		await expect(
			page.getByText('There are no segments found.')
		).toBeVisible();
	}
);

test(
	'Preview the membership of a batch segment with a First Name criterion',
	{
		tag: '@LRAC-8490',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const runId = getRandomString();
		const baseName = `user${runId}`;
		const individualNames = [
			`${baseName}1`,
			`${baseName}2`,
			`${baseName}3`,
		];

		const individuals: Individual[] = individualNames.map((name) =>
			generateIndividual({name})
		);

		await createIndividuals({apiHelpers, individuals});

		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		// Create a batch segment with `First Name contains <runId>` to match the three seeded individuals

		await createBatchSegment(page);

		await setSegmentName({
			page,
			segmentName: `Preview Membership ${runId}`,
		});

		await addSegmentField({
			criterionName: 'First Name',
			criterionType: 'Individual',
			page,
		});

		await selectOperator({
			operator: 'contains',
			operatorField: SegmentConditions.criteriaCondition,
			page,
		});

		await editCriteriaAttributeValue({
			attributeValue: runId,
			page,
		});

		// Open the in-editor View Members preview and assert the three seeded individuals appear

		await clickAndExpectToBeVisible({
			target: page.getByText('Known Segment Members'),
			trigger: page.getByRole('button', {name: 'View Members'}),
		});

		await viewNameOnTableList({
			itemNames: individualNames.map((name) => `${name} Smith`),
			page,
		});
	}
);

test.skip(

	// Re-enable when the Membership tab reflects batch segment membership in the local AC env.
	// Currently the UpdateMembershipsNanite + page reload still leaves Known Members at 0 even
	// with seeded individuals + pageViewed events + sessions, the same blocker that keeps the
	// other Membership-tab tests in this file under test.skip.

	'Segment Membership tab lists all known individuals, shows the legend counts, and supports search',
	{
		tag: ['@LRAC-8510', '@LRAC-8512', '@LRAC-8523'],
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const runId = getRandomString();
		const baseName = `user${runId}`;
		const individualNames = [
			`${baseName}1`,
			`${baseName}2`,
			`${baseName}3`,
		];
		const fullNames = individualNames.map((name) => `${name} Smith`);

		const individuals: Individual[] = individualNames.map((name) =>
			generateIndividual({name})
		);

		await createIndividuals({apiHelpers, individuals});

		const date = new Date();

		await apiHelpers.jsonWebServicesOSBAsah.createEvents(
			individuals.map((individual) => ({
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date.toISOString(),
				eventId: 'pageViewed',
				title: 'Liferay',
				userId: individual.id,
			}))
		);

		await apiHelpers.jsonWebServicesOSBAsah.createSessions(
			individuals.map((individual) => ({
				channelId: channel.id,
				id: individual.id,
				sessionEnd: date.toISOString(),
				sessionStart: date.toISOString(),
				userId: individual.id,
			}))
		);

		await navigateToACPageViaURL({
			acPage: ACPage.segmentPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		// Create a batch segment that matches only the three seeded individuals via a per-run First Name token

		await createBatchSegment(page);

		await setSegmentName({page, segmentName: getRandomString()});

		await addSegmentField({
			criterionName: 'First Name',
			criterionType: 'Individual',
			page,
		});

		await selectOperator({
			operator: 'contains',
			operatorField: SegmentConditions.criteriaCondition,
			page,
		});

		await editCriteriaAttributeValue({
			attributeValue: runId,
			page,
		});

		await page.keyboard.press('Escape');

		await saveSegment(page);

		// Run the membership nanite so the batch segment picks up the seeded individuals

		await runNanites({
			apiHelpers,
			naniteNames: [Nanites.UpdateMembershipsNanite],
			page,
		});

		await waitForLoading(page);

		await page.reload();

		await waitForLoading(page);

		// The Membership tab legend reflects three known members and zero anonymous

		await navigateTo({page, pageName: 'Membership'});

		await viewSegmentMembershipCount({
			anonymousMemberCount: '0',
			knownMemberCount: '3',
			page,
			totalMemberCount: '3',
		});

		// All three seeded individuals are listed

		await viewNameOnTableList({itemNames: fullNames, page});

		// Searching narrows the list to a single individual

		await searchByTerm({page, searchTerm: `${baseName}2`});

		await viewNameOnTableList({itemNames: `${baseName}2 Smith`, page});

		await viewNameNotPresentOnTableList({
			itemNames: [`${baseName}1 Smith`, `${baseName}3 Smith`],
			page,
		});
	}
);
