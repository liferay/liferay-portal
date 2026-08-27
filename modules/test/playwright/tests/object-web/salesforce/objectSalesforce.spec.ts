/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectViewAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {instanceSettingsPagesTest} from '../../../fixtures/instanceSettingsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
import {waitForAlert} from '../../../utils/waitForAlert';
import getFormContainerDefinition from '../../layout-content-page-editor-web/main/utils/getFormContainerDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {generateObjectFields} from '../utils/generateObjectFields';
import {salesforceConfig} from './salesforce.config';

const EXTERNAL_REFERENCE_CODE = 'Playwright_Test__c';

const OBJECT_DEFINITION_NAME = 'PlaywrightTest';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-135430': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	instanceSettingsPagesTest,
	isolatedSiteTest,
	loginTest(),
	objectPagesTest,
	pageEditorPagesTest
);

const createdSalesforceObjectEntries = [] as {
	applicationName: string;
	objectFieldValues: string[];
}[];

test.afterEach(async ({apiHelpers}) => {
	for (const {
		applicationName,
		objectFieldValues,
	} of createdSalesforceObjectEntries) {
		try {
			const {items} =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					applicationName,
					new URLSearchParams({
						filter: objectFieldValues
							.map(
								(objectFieldValue) =>
									`title eq '${objectFieldValue}'`
							)
							.join(' or '),
					})
				);

			for (const {externalReferenceCode} of items ?? []) {
				await apiHelpers.delete(
					`${apiHelpers.baseUrl}${applicationName}/by-external-reference-code/${externalReferenceCode}`
				);
			}
		}
		catch (error) {
			console.error(
				`Unable to delete the Salesforce object entries: ${error}`
			);
		}
	}

	createdSalesforceObjectEntries.length = 0;
});

test.beforeEach(async ({apiHelpers, instanceSettingsPage, page}) => {
	test.skip(
		!salesforceConfig.salesforceLoginURL ||
			!salesforceConfig.salesforceConsumerKey ||
			!salesforceConfig.salesforceConsumerSecret ||
			!salesforceConfig.salesforceUsername ||
			!salesforceConfig.salesforcePassword,
		'Requires Salesforce environment variables.'
	);

	page.setViewportSize({height: 1080, width: 1920});

	const leftoverObjectDefinition =
		await apiHelpers.objectAdmin.getObjectDefinitionByName(
			OBJECT_DEFINITION_NAME
		);

	if (leftoverObjectDefinition) {
		await apiHelpers.deleteObjectDefinition(leftoverObjectDefinition.id);
	}

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

test('Assert CRUD with created custom object using Salesforce storage type', async ({
	apiHelpers,
	page,
	viewObjectEntriesPage,
}) => {
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
			externalReferenceCode: EXTERNAL_REFERENCE_CODE,
			label: {en_US: 'Playwright Test'},
			name: OBJECT_DEFINITION_NAME,
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

	const objectViewAPIClient = await apiHelpers.buildRestClient(ObjectViewAPI);

	await objectViewAPIClient.postObjectDefinitionObjectView(
		objectDefinition.id,
		{
			defaultObjectView: true,
			name: {en_US: getRandomString()},
			objectViewColumns: [
				{objectFieldName: objectFields[0].name, priority: 0},
				{objectFieldName: 'createDate', priority: 1},
			],
			objectViewSortColumns: [
				{objectFieldName: 'createDate', priority: 0, sortOrder: 'desc'},
			],
		}
	);

	const objectFieldValue = getRandomString();
	const objectFieldUpdatedValue = getRandomString();

	createdSalesforceObjectEntries.push({
		applicationName: normalizeRestPath(objectDefinition.restContextPath!),
		objectFieldValues: [objectFieldValue, objectFieldUpdatedValue],
	});

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
	});

	await test.step('Read Object Entry', async () => {
		await clickAndExpectToBeVisible({
			target: page.getByRole('cell', {
				exact: true,
				name: objectFieldValue,
			}),
			trigger: viewObjectEntriesPage.backButton,
		});
	});

	await test.step('Update Object Entry', async () => {
		await page
			.getByRole('row', {name: objectFieldValue})
			.getByRole('button', {name: 'Actions'})
			.click();
		await page.getByRole('menuitem', {name: 'View'}).click();

		await viewObjectEntriesPage.fillObjectEntry({
			objectFieldBusinessType: 'Text',
			objectFieldLabel: 'Title',
			objectFieldValue: objectFieldUpdatedValue,
		});

		await viewObjectEntriesPage.saveObjectEntryButton.click();
		await expect(viewObjectEntriesPage.successMessage).toBeVisible();

		await clickAndExpectToBeVisible({
			target: page.getByRole('cell', {
				exact: true,
				name: objectFieldUpdatedValue,
			}),
			trigger: viewObjectEntriesPage.backButton,
		});
	});

	await test.step('Delete Object Entry', async () => {
		await page
			.getByRole('row', {name: objectFieldUpdatedValue})
			.getByRole('button', {name: 'Actions'})
			.click();
		await viewObjectEntriesPage.frontendDatasetDeleteAction.click();
		await viewObjectEntriesPage.deletionConfirmationModal
			.getByRole('button', {name: 'Delete'})
			.click();

		await expect(
			page.getByRole('cell', {exact: true, name: objectFieldUpdatedValue})
		).toBeAttached({attached: false});
	});
});

test('Assert CRUD with created custom object using Salesforce storage type in form container', async ({
	apiHelpers,
	page,
	pageEditorPage,
	site,
	viewObjectEntriesPage,
}) => {
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
			externalReferenceCode: EXTERNAL_REFERENCE_CODE,
			label: {en_US: 'Playwright Test'},
			name: OBJECT_DEFINITION_NAME,
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

	const objectViewAPIClient = await apiHelpers.buildRestClient(ObjectViewAPI);

	await objectViewAPIClient.postObjectDefinitionObjectView(
		objectDefinition.id,
		{
			defaultObjectView: true,
			name: {en_US: getRandomString()},
			objectViewColumns: [
				{objectFieldName: objectFields[0].name, priority: 0},
				{objectFieldName: 'createDate', priority: 1},
			],
			objectViewSortColumns: [
				{objectFieldName: 'createDate', priority: 0, sortOrder: 'desc'},
			],
		}
	);

	const formId = getRandomString();

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFormContainerDefinition({id: formId}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await test.step('Map the form container to the Salesforce object', async () => {
		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.mapFormFragment(
			formId,
			objectDefinition.label['en_US'],
			['Title']
		);

		await pageEditorPage.publishPage();
	});

	const entryValue = getRandomString();

	createdSalesforceObjectEntries.push({
		applicationName: normalizeRestPath(objectDefinition.restContextPath!),
		objectFieldValues: [entryValue],
	});

	await test.step('Submit an entry via the published form', async () => {
		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await page.getByRole('textbox', {name: 'Title'}).fill(entryValue);

		await page.getByRole('button', {name: 'Submit'}).click();

		await expect(
			page.getByText(
				'Thank you. Your information was successfully received.'
			)
		).toBeVisible();
	});

	await test.step('Read Object Entry in object admin', async () => {
		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page.getByRole('cell', {exact: true, name: entryValue})
		).toBeVisible();
	});

	await test.step('Delete Object Entry', async () => {
		await page
			.getByRole('row', {name: entryValue})
			.getByRole('button', {name: 'Actions'})
			.click();
		await viewObjectEntriesPage.frontendDatasetDeleteAction.click();
		await viewObjectEntriesPage.deletionConfirmationModal
			.getByRole('button', {name: 'Delete'})
			.click();

		await expect(
			page.getByRole('cell', {exact: true, name: entryValue})
		).toBeAttached({
			attached: false,
		});
	});
});
