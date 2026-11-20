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
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {syncAnalyticsCloudViaAPI} from '../../analytics-settings-web/main/utils/analytics-settings';
import {faroConfig} from '../main/faro.config';
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
} from '../main/utils/navigation';
import {
	addNestedSegmentField,
	addSegmentField,
	createBatchSegment,
	editCriteriaAttributeValue,
	editCriteriaConjunction,
	editSegment,
	includeAnonymousToggle,
	saveSegment,
	selectAsset,
	selectBehaviorAsset,
	selectBehaviorPage,
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

test(
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

		const pageName = getRandomString();

		await test.step('Create blogViewed and posted events for known and anonymous individuals', async () => {
			const blogId = String(getRandomInt());

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

			await createBatchSegment(page);

			await addSegmentField({
				criterionName: 'Comment',
				criterionType: 'Events',
				page,
			});

			await selectBehaviorAsset({
				assetName: pageName,
				assetType: 'Blogs',
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

		await test.step('Check the segment member count in the membership', async () => {
			await navigateTo({
				page,
				pageName: 'Membership',
			});

			await expect(async () => {
				await page.reload();

				await expect(
					page
						.locator('li')
						.filter({hasText: 'Total Members:'})
						.locator('b')
				).toHaveText('2', {timeout: 5000});
			}).toPass({timeout: 60000});

			await viewSegmentMembershipCount({
				anonymousMemberCount: '1',
				knownMemberCount: '1',
				page,
				totalMemberCount: '2',
			});
		});

		await test.step('Check that the correct known member appears in the membership tab', async () => {
			await viewNameOnTableList({
				itemNames: `${knownIndividualName} Smith`,
				page,
			});
		});
	}
);

test(
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
					attributeValue: '@liferay.com',
					page,
				});

				await page.keyboard.press('Escape');
			});

			await test.step('Add jobTitle criteria and fill in', async () => {
				await addSegmentField({
					criterionName: 'Job Title',
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

				await page.keyboard.press('Escape');
			});

			await test.step('Add the familyName criteria as a nested criteria of the jobTitle and fill in', async () => {
				await addNestedSegmentField({
					criterionName: 'Last Name',
					criterionType: 'Individual',
					nestedSegmentField: 'Job Title',
					page,
				});

				await editCriteriaAttributeValue({
					attributeValue: 'Smith',
					index: 2,
					page,
				});

				await page.keyboard.press('Escape');

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
				criteriaRowValue:
					'Individual Email Address contains "@liferay.com"',
				page,
			});

			await viewSegmentCriteriaCard({
				criteriaRowIndex: 0,
				criteriaRowValue:
					'Individual Job Title does not contain "engineer"',
				page,
				parent: page.locator('.criteria-group').nth(1),
			});

			await viewSegmentCriteriaCard({
				criteriaRowIndex: 1,
				criteriaRowValue: 'Individual Last Name is "Smith"',
				page,
				parent: page.locator('.criteria-group').nth(1),
			});
		});
	}
);

test(
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

			await createBatchSegment(page);

			await addSegmentField({
				criterionName: 'Organization',
				criterionType: 'Organization',
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

test(
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

			await createBatchSegment(page);

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

			await expect(page.getByText('No Results Were Found')).toBeVisible();

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

		await expect(page.getByText('No segments were found.')).toBeVisible();
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
			criterionName: 'View',
			criterionType: 'Events',
			page,
		});

		await selectBehaviorPage({page, pageName: pageTitle});

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

			await page.getByRole('button', {name: 'Save Segment'}).click();

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

			await page.getByRole('button', {name: 'Save Segment'}).click();

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
			criterionName: 'View',
			criterionType: 'Events',
			page,
		});

		await selectBehaviorAsset({
			assetName: assetTitle,
			assetType: 'Blogs',
			page,
		});

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

		await expect(page.getByText('No segments were found.')).toBeVisible();
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
			criterionName: 'View',
			criterionType: 'Events',
			page,
		});

		await selectBehaviorAsset({
			assetName: 'DM AC Title',
			assetType: 'Documents and Media',
			page,
		});

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
			criterionName: 'View',
			criterionType: 'Events',
			page,
		});

		await selectBehaviorAsset({
			assetName: assetTitle,
			assetType: 'Web Content',
			page,
		});

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
			criterionName: 'View',
			criterionType: 'Events',
			page,
		});

		await selectBehaviorAsset({
			assetName: assetTitle,
			assetType: 'Blogs',
			page,
		});

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

		await expect(page.getByText('No segments were found.')).toBeVisible();
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
