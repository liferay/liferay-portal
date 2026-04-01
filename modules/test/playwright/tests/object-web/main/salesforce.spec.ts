/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {salesforceConfig} from './salesforce.config';
import {generateObjectFields} from './utils/generateObjectFields';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-135430': {enabled: true},
	}),
	instanceSettingsPagesTest,
	isolatedSiteTest,
	loginTest(),
	objectPagesTest
);

test.beforeEach(async ({instanceSettingsPage, page}) => {
	test.skip(
		!salesforceConfig.salesforceLoginURL ||
			!salesforceConfig.salesforceConsumerKey ||
			!salesforceConfig.salesforceConsumerSecret ||
			!salesforceConfig.salesforceUsername ||
			!salesforceConfig.salesforcePassword,
		'Requires Salesforce environment variables.'
	);

	page.setViewportSize({height: 1080, width: 1920});

	await test.step('Setup Salesforce Instance Settings', async () => {
		await instanceSettingsPage.goToInstanceSetting(
			'Third Party',
			'Salesforce Integration'
		);

		await page
			.getByLabel('Login URL')
			.fill(salesforceConfig.salesforceLoginURL!);
		await page
			.getByLabel('Consumer Key')
			.fill(salesforceConfig.salesforceConsumerKey!);
		await page
			.getByLabel('Consumer Secret')
			.fill(salesforceConfig.salesforceConsumerSecret!);
		await page
			.getByLabel('Username')
			.fill(salesforceConfig.salesforceUsername!);

		await page
			.locator('input[name*="password"]')
			.filter({visible: true})
			.fill(salesforceConfig.salesforcePassword!);

		await instanceSettingsPage.saveAndWaitForAlert();
	});
});

test(
	'LPS-162131 Assert CRUD with created custom object using Salesforce storage type',
	{tag: '@LPS-162131'},
	async ({apiHelpers, page, viewObjectEntriesPage}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: [
				{
					businessType: 'Text',
					externalReferenceCode: 'Title__c',
					label: {en_US: 'Title'},
					name: 'title',
				},
			],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				externalReferenceCode: 'Playwright_Test__c',
				label: {en_US: 'Playwright Test'},
				name: 'PlaywrightTest',
				objectFields,
				panelCategoryKey: 'control_panel.object',
				pluralLabel: {en_US: 'Playwright Tests'},
				portlet: true,
				scope: 'company',
				status: {code: 0},
				storageType: 'salesforce',
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectFieldValue = getRandomString();
		const objectFieldUpdatedValue = getRandomString();

		await test.step('Create Object Entry', async () => {
			await viewObjectEntriesPage.goto(objectDefinition.className);
			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await viewObjectEntriesPage.fillObjectEntry({
				objectFieldBusinessType: 'Text',
				objectFieldLabel: 'Title',
				objectFieldValue,
			});

			await viewObjectEntriesPage.saveObjectEntryButton.click();
			await waitForAlert(page);
			await viewObjectEntriesPage.backButton.click();
		});

		await test.step('Read Object Entry', async () => {
			await expect(
				page.getByRole('cell', {name: objectFieldValue})
			).toBeVisible();
		});

		await test.step('Update Object Entry', async () => {
			await page.getByRole('button', {name: 'Actions'}).last().click();
			await page.getByRole('menuitem', {name: 'View'}).click();

			await viewObjectEntriesPage.fillObjectEntry({
				objectFieldBusinessType: 'Text',
				objectFieldLabel: 'Title',
				objectFieldValue: objectFieldUpdatedValue,
			});

			await viewObjectEntriesPage.saveObjectEntryButton.click();
			await expect(viewObjectEntriesPage.successMessage).toBeVisible();
			await viewObjectEntriesPage.backButton.click();

			await expect(
				page.getByRole('cell', {name: objectFieldUpdatedValue})
			).toBeVisible();
		});

		await test.step('Delete Object Entry', async () => {
			await viewObjectEntriesPage.frontendDatasetActions.last().click();
			await viewObjectEntriesPage.frontendDatasetDeleteAction.click();
			await viewObjectEntriesPage.deletionConfirmationModal
				.getByRole('button', {name: 'Delete'})
				.click();

			await expect(
				page.getByRole('cell', {name: objectFieldUpdatedValue})
			).toBeAttached({attached: false});
		});
	}
);
