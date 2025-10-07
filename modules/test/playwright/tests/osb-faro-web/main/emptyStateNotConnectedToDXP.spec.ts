/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {faroConfig} from './faro.config';
import {clickOnLink} from './utils/actions';
import {ACPage, navigateToACSettingsViaURL} from './utils/navigation';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	loginAnalyticsCloudTest(),
	loginTest()
);

let project;

const randomString = getRandomString();

test.beforeEach(async ({apiHelpers}) => {
	project = await apiHelpers.jsonWebServicesOSBFaro.createProject(
		'My Project ' + randomString
	);
});

test.afterEach(async ({apiHelpers}) => {
	await apiHelpers.jsonWebServicesOSBFaro.deleteProject(project.groupId);
});

test(
	'Empty state of pages with no data source and no property.',

	{
		tag: '@LRAC-9248',
	},

	async ({page}) => {
		await test.step('Go to Data Sources, check the empty page status message', async () => {
			await navigateToACSettingsViaURL({
				acPage: ACPage.dataSourcePage,
				page,
				projectID: project.groupId,
			});

			await expect(
				page.getByText('No Data Sources Connected')
			).toBeVisible();
			await expect(
				page.getByText('Add a data source to get started.')
			).toBeVisible();

			await expect(
				page.getByText('Access our documentation to learn more.')
			).toBeVisible();

			await clickOnLink({
				baseUrl: faroConfig.environment.baseUrl,
				name: 'Access our documentation to learn more.',
				page,
			});

			await expect(page).toHaveTitle(
				'Managing Data Sources - Liferay Official Documentation - Liferay Learn'
			);
			await expect(
				page
					.locator('a#managing-data-sources')
					.getByText('Managing Data Sources')
			).toBeVisible();
		});

		await test.step('Go to Properties and then check the empty page status message then check the if the hyperlink is taking the user to the documentation', async () => {
			await navigateToACSettingsViaURL({
				acPage: ACPage.propertiesPage,
				page,
				projectID: project.groupId,
			});

			await expect(page.getByText('No Properties Found')).toBeVisible();
			await expect(
				page.getByText('Create a property to get started.')
			).toBeVisible();
			await expect(
				page.getByText('Access our documentation to learn more.')
			).toBeVisible();

			await clickOnLink({
				baseUrl: faroConfig.environment.baseUrl,
				name: 'Access our documentation to learn more.',
				page,
			});

			await expect(page).toHaveTitle(
				'Scoping Sites and Individuals Using Properties - Liferay Official Documentation - Liferay Learn'
			);
			await expect(
				page
					.locator('a#scoping-sites-and-individuals-using-properties')
					.getByText('Scoping Sites and Individuals Using Properties')
			).toBeVisible();
		});

		await test.step('Go to Definitions then go to Individuals and check the list of field mapping attributes', async () => {
			await navigateToACSettingsViaURL({
				acPage: ACPage.definitionsIndividualAttributesPage,
				page,
				projectID: project.groupId,
			});

			await expect(page.getByText('additionalName')).toBeVisible();
			await expect(page.getByText('birthDate')).toBeVisible();
			await expect(page.getByText('email')).toBeVisible();
			await expect(page.getByText('familyName')).toBeVisible();
			await expect(page.getByText('givenName')).toBeVisible();
			await expect(page.getByText('jobTitle')).toBeVisible();
			await expect(page.getByText('languageId')).toBeVisible();
			await expect(page.getByText('modifiedDate')).toBeVisible();
			await expect(page.getByText('screenName')).toBeVisible();
		});

		await test.step('Go back to Definitions and go to Event Attributes and check the empty page status message then check the if the hyperlink is taking the user to the documentation', async () => {
			await navigateToACSettingsViaURL({
				acPage: ACPage.definitionsEventAttributesGlobalPage,
				page,
				projectID: project.groupId,
			});

			const tableBodyElement = await page.locator('table tbody');

			await expect(tableBodyElement).toBeVisible();

			const elements = await tableBodyElement
				.locator('td .table-title a')
				.all();

			const href = await elements[0].getAttribute('href');

			await page.goto(`${faroConfig.environment.baseUrl}${href}`);

			await expect(page.getByText('No Sample Data Found')).toBeVisible();
			await expect(
				page.getByText(
					'You can come back later and check if there is any data received from your events.Learn more about event tracking'
				)
			).toBeVisible();
			await expect(
				page.getByText('Learn more about event tracking.')
			).toBeVisible();

			await clickOnLink({
				baseUrl: faroConfig.environment.baseUrl,
				name: 'Learn more about event tracking.',
				page,
			});

			await expect(page).toHaveTitle(
				'Definitions - Liferay Official Documentation - Liferay Learn'
			);

			const header = await page.locator('h2#event-attributes');

			await expect(header.getByText('Event Attributes')).toBeVisible();
		});

		await test.step('Go to Definitions, then go to Events, select the Custom Events tab and check the empty page status message then check the if the hyperlink is taking the user to the documentation', async () => {
			await navigateToACSettingsViaURL({
				acPage: ACPage.definitionsEventsCustomPage,
				page,
				projectID: project.groupId,
			});

			await expect(
				page.getByText('No Custom Events Found')
			).toBeVisible();
			await expect(
				page.getByText('Create some custom events to get started.')
			).toBeVisible();
			await expect(
				page.getByText('Learn how to add custom events on your site.')
			).toBeVisible();

			await clickOnLink({
				baseUrl: faroConfig.environment.baseUrl,
				name: 'Learn how to add custom events on your site.',
				page,
			});

			await expect(page).toHaveTitle(
				'Tracking Events - Liferay Official Documentation - Liferay Learn'
			);
			await expect(
				page.locator('a#tracking-events').getByText('Tracking Events')
			).toBeVisible();
		});

		await test.step('Still in the Custom Events tab go to block list and check the empty page status message then check the if the hyperlink is taking the user to the documentation', async () => {
			await navigateToACSettingsViaURL({
				acPage: ACPage.definitionsEventsBlockListPage,
				page,
				projectID: project.groupId,
			});

			await expect(
				page.getByText('There are no events blocked.')
			).toBeVisible();
			await expect(
				page.getByText(
					'Access our documentation to learn how to manage custom events.'
				)
			).toBeVisible();

			await clickOnLink({
				baseUrl: faroConfig.environment.baseUrl,
				name: 'Access our documentation to learn how to manage custom events.',
				page,
			});

			await expect(page).toHaveTitle(
				'Definitions - Liferay Official Documentation - Liferay Learn'
			);
			await expect(
				page.locator('a#custom-events').getByText('Custom Events')
			).toBeVisible();
		});

		await test.step('Go to Data Control and Privacy, then go to Request Log > Manage and check the empty page status message', async () => {
			await navigateToACSettingsViaURL({
				acPage: ACPage.dataPrivacyRequestLogPage,
				page,
				projectID: project.groupId,
			});

			await expect(page.getByText('No Requests Found')).toBeVisible();
			await expect(
				page.getByText('Create a request to get started.')
			).toBeVisible();
			await expect(
				page.getByText('Access our documentation to learn more.')
			).toBeVisible();
		});

		await test.step('Go back to Data Control and Privacy, then go to Suppressed Users > Manage, and check the empty page status message then check the if the hyperlink is taking the user to the documentation', async () => {
			await navigateToACSettingsViaURL({
				acPage: ACPage.dataPrivacySupressedUsersPage,
				page,
				projectID: project.groupId,
			});

			await expect(
				page.getByText(
					'To suppress a user, go to data control & privacy under settings and create a new request on the request log.'
				)
			).toBeVisible();
			await expect(
				page.getByText('Access our documentation to learn more.')
			).toBeVisible();

			await clickOnLink({
				baseUrl: faroConfig.environment.baseUrl,
				name: 'Access our documentation to learn more.',
				page,
			});

			await expect(page).toHaveTitle(
				'Data Control and Privacy - Liferay Official Documentation - Liferay Learn'
			);

			const element = await page.locator('a#data-control-and-privacy');

			await expect(
				element.getByText('Data Control and Privacy')
			).toBeVisible();
		});
	}
);
