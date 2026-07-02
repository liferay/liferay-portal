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
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import dragAndDropElement from '../../../utils/dragAndDropElement';
import getRandomString from '../../../utils/getRandomString';
import {selectAndExpectToHaveValue} from '../../../utils/selectAndExpectToHaveValue';
import {waitForAlert} from '../../../utils/waitForAlert';
import {pagesPagesTest} from '../../layout-admin-web/main/fixtures/pagesPagesTest';
import {faroConfig} from '../main/faro.config';
import {
	addAttributeFilter,
	addBooleanFilter,
	addBreakdown,
	addCustomEvent,
	addDateFilter,
	addFilter,
	createAndSaveEventAnalysis,
	removeAttribute,
	setEventAnalysisName,
} from '../main/utils/events';
import {
	ACPage,
	navigateToACPageViaURL,
	navigateToACSettingsViaURL,
} from '../main/utils/navigation';
import {signInToAnalyticsCloud} from '../main/utils/signInToAnalyticsCloud';
import {changeTimeFilter} from '../main/utils/time-filter';
import {
	selectPaginationItemsPerPage,
	selectPaginationPageNumber,
} from '../main/utils/utils';

export const test = mergeTests(
	apiHelpersTest,
	assetPublisherPagesTest,
	pageEditorPagesTest,
	pagesPagesTest,
	pagesAdminPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedChannelTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

const randomString = getRandomString();

const pageTitle = 'My Page';

test(
	'Change data type with event already in use',
	{
		tag: '@LRAC-6280',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await test.step('Send a custom event', async () => {
			const date = new Date();

			await apiHelpers.jsonWebServicesOSBAsah.createEvents([
				{
					applicationId: 'CustomEvent',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date.toISOString(),
					eventId: 'customEvent',
					properties: [
						{
							name: 'birthdate',
							value: '2021-11-25T14:36:30.685Z',
						},
						{
							name: 'category',
							value: 'wetsuit',
						},
						{
							name: 'duration',
							value: '3600000',
						},
						{
							name: 'like',
							value: 'true',
						},
						{
							name: 'price',
							value: '259.95',
						},
						{
							name: 'temp',
							value: '11',
						},
					],
					title: 'Liferay',
					userId: '1',
				},
			]);

			await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
				{
					applicationId: 'CustomEvent',
					displayName: 'customEvent',
					eventAttributeDefinitions: [
						{
							dataType: 'DATE',
							displayName: 'birthdate',
							name: 'birthdate',
							type: 'LOCAL',
						},
						{
							dataType: 'STRING',
							displayName: 'category',
							name: 'category',
							type: 'LOCAL',
						},
						{
							dataType: 'DURATION',
							displayName: 'duration',
							name: 'duration',
							type: 'LOCAL',
						},
						{
							dataType: 'BOOLEAN',
							displayName: 'like',
							name: 'like',
							type: 'LOCAL',
						},
						{
							dataType: 'NUMBER',
							displayName: 'price',
							name: 'price',
							type: 'LOCAL',
						},
						{
							dataType: 'NUMBER',
							displayName: 'temp',
							name: 'temp',
							type: 'LOCAL',
						},
					],
					name: 'customEvent',
					type: 'CUSTOM',
				},
			]);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.eventAnalysisPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});
		});

		await test.step('Add the custom event, create an analysis and add a brekdown', async () => {
			await page.getByRole('link', {name: 'Create Analysis'}).click();
		});

		await test.step('Add a name to the segment', async () => {
			await setEventAnalysisName({
				eventAnalysisName: `Event Analysis ${randomString}`,
				page,
			});
		});

		await test.step('Add the custom event to the analysis', async () => {
			await addCustomEvent({
				customEventName: 'customEvent',
				page,
			});
		});

		const attributeNameList = ['category', 'duration', 'temp'];

		await test.step('Add a breakdown to the analysis', async () => {
			for (const attributeName of attributeNameList) {
				await addBreakdown({
					breakdownName: attributeName,
					page,
					tab: 'Event',
				});
			}
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Add a filter to the analysis', async () => {
			await addFilter({
				filterName: 'temp',
				input: '10',
				operator: 'is greater than',
				page,
			});
		});

		await test.step('Check the analysis result appears and save the analysis', async () => {
			await page.setViewportSize({height: 1080, width: 1920});

			for (const attributeName of attributeNameList) {
				await expect(
					page.getByRole('button', {
						exact: true,
						name: `Event ${attributeName}`,
					})
				).toBeVisible();
			}

			const tableInfo = page.getByRole('cell');

			await expect(tableInfo.getByText('wetsuit')).toBeVisible();
			await expect(
				tableInfo.getByText('Between 01:00:00 - 01:00:59')
			).toBeVisible();
			await expect(tableInfo.getByText('10 - 19')).toBeVisible();
			await expect(tableInfo.getByText('100%')).toBeVisible();

			await page.getByText('Save Analysis').click();
		});

		await test.step('Check the analysis result appears and save the', async () => {
			await expect(
				page.getByText(`Event Analysis ${randomString}`)
			).toBeVisible();
		});

		await test.step('Navigation to attributes tab', async () => {
			await navigateToACSettingsViaURL({
				acPage: ACPage.eventAttributesPage,
				page,
				projectID: project.groupId,
			});

			await page
				.getByRole('link', {exact: true, name: 'Attributes'})
				.click();
		});

		await test.step('Edit the new attribute', async () => {
			await page.getByPlaceholder('Search').fill('temp');

			await page.keyboard.press('Enter');

			await expect(page.getByText('NUMBER')).toBeVisible();

			await page.getByRole('link', {name: 'temp'}).click();

			await page.getByRole('button', {name: 'Edit'}).click();

			await page.getByLabel('Default Data Typecast').click();

			await selectAndExpectToHaveValue({
				optionValue: 'DATE',
				select: page.getByLabel('Default Data Typecast'),
			});

			await page.getByRole('button', {name: 'Save'}).click();
		});

		await test.step('Check that the type of the attribute has been changed', async () => {
			await navigateToACSettingsViaURL({
				acPage: ACPage.eventAttributesPage,
				page,
				projectID: project.groupId,
			});

			await page
				.getByRole('link', {exact: true, name: 'Attributes'})
				.click();

			await page.getByPlaceholder('Search').fill('temp');

			await page.keyboard.press('Enter');

			await expect(page.getByText('DATE')).toBeVisible();
		});

		await test.step('test', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.eventAnalysisPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await page
				.getByRole('link', {exact: false, name: 'Event Analysis'})
				.click();
		});

		await test.step('Check the analysis result appears', async () => {
			for (const attributeName of attributeNameList) {
				await expect(
					page.getByRole('button', {
						exact: true,
						name: `Event ${attributeName}`,
					})
				).toBeVisible();
			}

			const tableInfo = page.getByRole('cell');

			await expect(tableInfo.getByText('wetsuit')).toBeVisible();
			await expect(
				tableInfo.getByText('Between 01:00:00 - 01:00:59')
			).toBeVisible();
			await expect(tableInfo.getByText('10 - 19')).toBeVisible();
			await expect(tableInfo.getByText('100%')).toBeVisible();
		});

		await test.step('Check that the attributes used have not had the attribute type changed', async () => {
			await expect(
				page.getByRole('button', {name: 'Event | temp is greater than'})
			).toBeVisible();
		});
	}
);

test(
	'Event Analysis breakdown filter provide auto complete suggestions for "contain" condition',
	{
		tag: '@LRAC-9481',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await test.step('Send a custom event', async () => {
			const date = new Date();

			await apiHelpers.jsonWebServicesOSBAsah.createEvents([
				{
					applicationId: 'CustomEvent',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date.toISOString(),
					eventId: 'customEvent',
					properties: [
						{
							name: 'city',
							value: 'rio de janeiro',
						},
					],
					title: 'Liferay',
					userId: '1',
				},
			]);

			await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
				{
					applicationId: 'CustomEvent',
					displayName: 'customEvent',
					eventAttributeDefinitions: [
						{
							dataType: 'STRING',
							displayName: 'city',
							name: 'city',
							type: 'LOCAL',
						},
					],
					name: 'customEvent',
					type: 'CUSTOM',
				},
			]);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.eventAnalysisPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});
		});

		await test.step('Add the custom event and create an analysis', async () => {
			await page.getByRole('link', {name: 'Create Analysis'}).click();
		});

		await test.step('Add a name to the analysis', async () => {
			await setEventAnalysisName({
				eventAnalysisName: `Event Analysis ${randomString}`,
				page,
			});
		});

		await test.step('Add the custom event to the analysis', async () => {
			await addCustomEvent({
				customEventName: 'customEvent',
				page,
			});
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Add a filter to the analysis', async () => {
			await page
				.locator('.attribute-filter-section-root')
				.getByRole('button')
				.click();

			await page
				.getByRole('menuitem', {exact: true, name: 'city'})
				.click();
		});

		await test.step('Select the contain condition', async () => {
			await page.getByLabel('Condition').click();

			await selectAndExpectToHaveValue({
				optionLabel: 'contains',
				select: page.getByLabel('Condition'),
			});

			await page.waitForTimeout(1000);
		});

		await test.step('Check the auto complete filter', async () => {
			await page
				.locator(
					"xpath=//div[contains(@class,'event-analysis-editor-attribute-dropdown-root show')]//input"
				)
				.first()
				.fill('rio');

			await page.getByRole('option', {name: 'rio de janeiro'}).click();

			await page.keyboard.press('Enter');
		});

		await test.step('Check that the analysis result appears', async () => {
			await expect(
				page
					.getByRole('row', {name: 'customEvent'})
					.locator('div')
					.nth(1)
			).toBeVisible();

			await page
				.locator('div')
				.filter({
					hasText: /^FilterEvent \| citycontains "rio de janeiro"$/,
				})
				.getByLabel('Close')
				.click();

			await expect(
				page.locator('div').filter({
					hasText: /^FilterEvent \| citycontains "rio de janeiro"$/,
				})
			).toHaveCount(0);
		});
	}
);

test(
	'Event Analysis breakdown filter provide auto complete suggestions for "not contain" condition',
	{
		tag: '@LRAC-9481',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await test.step('Send a custom event', async () => {
			const date = new Date();

			await apiHelpers.jsonWebServicesOSBAsah.createEvents([
				{
					applicationId: 'CustomEvent',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date.toISOString(),
					eventId: 'customEvent',
					properties: [
						{
							name: 'city',
							value: 'rio de janeiro',
						},
					],
					title: 'Liferay',
					userId: '1',
				},
			]);

			await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
				{
					applicationId: 'CustomEvent',
					displayName: 'customEvent',
					eventAttributeDefinitions: [
						{
							dataType: 'STRING',
							displayName: 'city',
							name: 'city',
							type: 'LOCAL',
						},
					],
					name: 'customEvent',
					type: 'CUSTOM',
				},
			]);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.eventAnalysisPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});
		});

		await test.step('Add the custom event and create an analysis', async () => {
			await page.getByRole('link', {name: 'Create Analysis'}).click();
		});

		await test.step('Add a name to the Analysis', async () => {
			await setEventAnalysisName({
				eventAnalysisName: `Event Analysis ${randomString}`,
				page,
			});
		});

		await test.step('Add the custom event to the analysis', async () => {
			await addCustomEvent({
				customEventName: 'customEvent',
				page,
			});
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Add a filter to the analysis', async () => {
			await page
				.locator('.attribute-filter-section-root')
				.getByRole('button')
				.click();

			await page
				.getByRole('menuitem', {exact: true, name: 'city'})
				.click();
		});

		await test.step('Select the not contain condition', async () => {
			await page.getByLabel('Condition').click();

			await selectAndExpectToHaveValue({
				optionLabel: 'does not contain',
				select: page.getByLabel('Condition'),
			});

			await page.waitForTimeout(1000);
		});

		await test.step('Check the auto complete filter', async () => {
			await page
				.locator(
					"xpath=//div[contains(@class,'event-analysis-editor-attribute-dropdown-root show')]//input"
				)
				.first()
				.fill('rio');

			await page.getByRole('option', {name: 'rio de janeiro'}).click();

			await page.keyboard.press('Enter');
		});

		await test.step('Check that the analysis result appears', async () => {
			await expect(
				page
					.getByRole('row', {name: 'customEvent'})
					.locator('div')
					.first()
			).toBeVisible();

			await page
				.locator('div')
				.filter({
					hasText:
						/^FilterEvent \| citydoes not contain "rio de janeiro"$/,
				})
				.getByLabel('Close')
				.click();

			await expect(
				page.locator('div').filter({
					hasText:
						/^FilterEvent \| citydoes not contain "rio de janeiro"$/,
				})
			).toHaveCount(0);
		});
	}
);

test(
	'Event Analysis breakdown filter provide auto complete suggestions for "is" condition',
	{
		tag: '@LRAC-9481',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await test.step('Send a custom event', async () => {
			const date = new Date();

			await apiHelpers.jsonWebServicesOSBAsah.createEvents([
				{
					applicationId: 'CustomEvent',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date.toISOString(),
					eventId: 'customEvent',
					properties: [
						{
							name: 'city',
							value: 'rio de janeiro',
						},
					],
					title: 'Liferay',
					userId: '1',
				},
			]);

			await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
				{
					applicationId: 'CustomEvent',
					displayName: 'customEvent',
					eventAttributeDefinitions: [
						{
							dataType: 'STRING',
							displayName: 'city',
							name: 'city',
							type: 'LOCAL',
						},
					],
					name: 'customEvent',
					type: 'CUSTOM',
				},
			]);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.eventAnalysisPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});
		});

		await test.step('Add the custom event and create an analysis', async () => {
			await page.getByRole('link', {name: 'Create Analysis'}).click();
		});

		await test.step('Add a name to the analysis', async () => {
			await setEventAnalysisName({
				eventAnalysisName: `Event Analysis ${randomString}`,
				page,
			});
		});

		await test.step('Add the custom event to the analysis', async () => {
			await addCustomEvent({
				customEventName: 'customEvent',
				page,
			});
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Add a filter to the analysis', async () => {
			await page
				.locator('.attribute-filter-section-root')
				.getByRole('button')
				.click();

			await page
				.getByRole('menuitem', {exact: true, name: 'city'})
				.click();
		});

		await test.step('Select the not contain condition', async () => {
			await page.getByLabel('Condition').click();

			await selectAndExpectToHaveValue({
				optionLabel: 'is',
				select: page.getByLabel('Condition'),
			});

			await page.waitForTimeout(1000);
		});

		await test.step('Check the auto complete filter', async () => {
			await page
				.locator(
					"xpath=//div[contains(@class,'event-analysis-editor-attribute-dropdown-root show')]//input"
				)
				.first()
				.fill('rio');

			await page.getByRole('option', {name: 'rio de janeiro'}).click();

			await page.keyboard.press('Enter');
		});

		await test.step('Check that the analysis result appears', async () => {
			await expect(
				page
					.getByRole('row', {name: 'customEvent'})
					.locator('div')
					.nth(1)
			).toBeVisible();

			await page
				.locator('div')
				.filter({hasText: /^FilterEvent \| cityis "rio de janeiro"$/})
				.getByLabel('Close')
				.click();

			await expect(
				page.locator('div').filter({
					hasText: /^FilterEvent \| cityis "rio de janeiro"$/,
				})
			).toHaveCount(0);
		});
	}
);

test(
	'Event Analysis breakdown filter provide auto complete suggestions for "is not" condition',
	{
		tag: '@LRAC-9481',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await test.step('Send a custom event', async () => {
			const date = new Date();

			await apiHelpers.jsonWebServicesOSBAsah.createEvents([
				{
					applicationId: 'CustomEvent',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date.toISOString(),
					eventId: 'customEvent',
					properties: [
						{
							name: 'city',
							value: 'rio de janeiro',
						},
					],
					title: 'Liferay',
					userId: '1',
				},
			]);

			await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
				{
					applicationId: 'CustomEvent',
					displayName: 'customEvent',
					eventAttributeDefinitions: [
						{
							dataType: 'STRING',
							displayName: 'city',
							name: 'city',
							type: 'LOCAL',
						},
					],
					name: 'customEvent',
					type: 'CUSTOM',
				},
			]);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.eventAnalysisPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});
		});

		await test.step('Add the custom event and create an analysis', async () => {
			await page.getByRole('link', {name: 'Create Analysis'}).click();
		});

		await test.step('Add a name to the analysis', async () => {
			await setEventAnalysisName({
				eventAnalysisName: `Event Analysis ${randomString}`,
				page,
			});
		});

		await test.step('Add the custom event to the analysis', async () => {
			await addCustomEvent({
				customEventName: 'customEvent',
				page,
			});
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Add a filter to the analysis', async () => {
			await page
				.locator('.attribute-filter-section-root')
				.getByRole('button')
				.click();

			await page
				.getByRole('menuitem', {exact: true, name: 'city'})
				.click();
		});

		await test.step('Select the not contain condition', async () => {
			await page.getByLabel('Condition').click();

			await selectAndExpectToHaveValue({
				optionLabel: 'is not',
				select: page.getByLabel('Condition'),
			});

			await page.waitForTimeout(1000);
		});

		await test.step('Check the auto complete filter', async () => {
			await page
				.locator(
					"xpath=//div[contains(@class,'event-analysis-editor-attribute-dropdown-root show')]//input"
				)
				.first()
				.fill('rio');

			await page.getByRole('option', {name: 'rio de janeiro'}).click();

			await page.keyboard.press('Enter');
		});

		await test.step('Check that the analysis result appears', async () => {
			await expect(
				page
					.getByRole('row', {name: 'customEvent'})
					.locator('div')
					.first()
			).toBeVisible();

			await page
				.locator('div')
				.filter({
					hasText: /^FilterEvent \| cityis not "rio de janeiro"$/,
				})
				.getByLabel('Close')
				.click();

			await expect(
				page.locator('div').filter({
					hasText: /^FilterEvent \| cityis not "rio de janeiro"$/,
				})
			).toHaveCount(0);
		});
	}
);

test(
	'Event Analysis creation with Filtered Attribute (String) and (Contains/Does not Contains) condition',
	{
		tag: '@LRAC-7868',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await test.step('Send a custom event', async () => {
			const date = new Date();

			await apiHelpers.jsonWebServicesOSBAsah.createEvents([
				{
					applicationId: 'CustomEvent',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date.toISOString(),
					eventId: 'customEvent',
					properties: [
						{
							name: 'city',
							value: 'rio de janeiro',
						},
					],
					title: 'Liferay',
					userId: '1',
				},
			]);

			await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
				{
					applicationId: 'CustomEvent',
					displayName: 'customEvent',
					eventAttributeDefinitions: [
						{
							dataType: 'STRING',
							displayName: 'city',
							name: 'city',
							type: 'LOCAL',
						},
					],
					name: 'customEvent',
					type: 'CUSTOM',
				},
			]);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.eventAnalysisPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});
		});

		await test.step('Add the custom event and create an analysis', async () => {
			await page.getByRole('link', {name: 'Create Analysis'}).click();
		});

		await test.step('Add a name to the analysis', async () => {
			await setEventAnalysisName({
				eventAnalysisName: `Event Analysis ${randomString}`,
				page,
			});
		});

		await test.step('Add the custom event to the analysis', async () => {
			await addCustomEvent({
				customEventName: 'customEvent',
				page,
			});
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Add a filter to the analysis', async () => {
			await page
				.locator('.attribute-filter-section-root')
				.getByRole('button')
				.click();

			await page
				.getByRole('menuitem', {exact: true, name: 'city'})
				.click();
		});

		await test.step('Select the contain condition', async () => {
			await page.getByLabel('Condition').click();

			await selectAndExpectToHaveValue({
				optionLabel: 'contains',
				select: page.getByLabel('Condition'),
			});

			await page.waitForTimeout(1000);
		});

		await test.step('Check the auto complete filter', async () => {
			await page
				.locator(
					"xpath=//div[contains(@class,'event-analysis-editor-attribute-dropdown-root show')]//input"
				)
				.first()
				.fill('rio');

			await page.getByRole('option', {name: 'rio de janeiro'}).click();

			await page.keyboard.press('Enter');
		});

		await test.step('Check that the analysis result appears', async () => {
			await expect(
				page
					.getByRole('row', {name: 'customEvent'})
					.locator('div')
					.nth(1)
			).toBeVisible();

			await page
				.locator('div')
				.filter({
					hasText: /^FilterEvent \| citycontains "rio de janeiro"$/,
				})
				.getByLabel('Close')
				.click();

			await expect(
				page.locator('div').filter({
					hasText: /^FilterEvent \| citycontains "rio de janeiro"$/,
				})
			).toHaveCount(0);
		});

		await test.step('Add a filter to the analysis', async () => {
			await page
				.locator('.attribute-filter-section-root')
				.getByRole('button')
				.click();

			await page
				.getByRole('menuitem', {exact: true, name: 'city'})
				.click();
		});

		await test.step('Select the not contain condition', async () => {
			await page.getByLabel('Condition').click();

			await selectAndExpectToHaveValue({
				optionLabel: 'does not contain',
				select: page.getByLabel('Condition'),
			});

			await page.waitForTimeout(1000);
		});

		await test.step('Check the auto complete filter', async () => {
			await page
				.locator(
					"xpath=//div[contains(@class,'event-analysis-editor-attribute-dropdown-root show')]//input"
				)
				.first()
				.fill('rio');

			await page.getByRole('option', {name: 'rio de janeiro'}).click();

			await page.keyboard.press('Enter');
		});

		await test.step('Check that the analysis result appears', async () => {
			await expect(
				page
					.getByRole('row', {name: 'customEvent'})
					.locator('div')
					.first()
			).toBeVisible();

			await page
				.locator('div')
				.filter({
					hasText:
						/^FilterEvent \| citydoes not contain "rio de janeiro"$/,
				})
				.getByLabel('Close')
				.click();

			await expect(
				page.locator('div').filter({
					hasText:
						/^FilterEvent \| citydoes not contain "rio de janeiro"$/,
				})
			).toHaveCount(0);
		});
	}
);

test(
	'The analysis result should not appear if any of the attribute conditions are not contained within the result',
	{
		tag: '@LRAC-11746',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await test.step('Send a custom event', async () => {
			const date = new Date();

			await apiHelpers.jsonWebServicesOSBAsah.createEvents([
				{
					applicationId: 'CustomEvent',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date.toISOString(),
					eventId: 'pageTitleEvent',
					properties: [
						{
							name: 'pageTitleEvent',
							value: pageTitle,
						},
					],
					title: pageTitle,
					userId: '1',
				},
			]);

			await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
				{
					applicationId: 'CustomEvent',
					displayName: 'pageTitleEvent',
					eventAttributeDefinitions: [
						{
							dataType: 'STRING',
							displayName: 'pageTitleEvent',
							name: 'pageTitleEvent',
							type: 'LOCAL',
						},
						{
							dataType: 'STRING',
							displayName: 'pageTitle',
							name: 'pageTitle',
							type: 'GLOBAL',
						},
					],
					name: 'pageTitleEvent',
					type: 'CUSTOM',
				},
			]);
		});

		await test.step('Go to Analytics Cloud and Switch the property', async () => {
			await navigateToACPageViaURL({
				acPage: ACPage.eventAnalysisPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});
		});

		await test.step('Add the custom event, create an analysis and add a brekdown', async () => {
			await page.getByRole('link', {name: 'Create Analysis'}).click();
		});

		await test.step('Add a name to the analysis', async () => {
			await setEventAnalysisName({
				eventAnalysisName: `Event Analysis ${randomString}`,
				page,
			});
		});

		await test.step('Add the custom event to the analysis', async () => {
			await addCustomEvent({
				customEventName: 'pageTitleEvent',
				page,
			});
		});

		await test.step('Add a breakdown to the analysis', async () => {
			await addBreakdown({
				breakdownName: 'pageTitle',
				page,
				tab: 'Event',
			});
		});

		await test.step('Change the time filter to Last 24 hours', async () => {
			await changeTimeFilter({
				page,
				timeFilterPeriod: 'Last 24 hours',
			});
		});

		await test.step('Add a filter to the analysis', async () => {
			await addFilter({
				filterName: 'pageTitle',
				input: pageTitle,
				operator: 'contains',
				page,
			});
		});

		await test.step('View the information displayed', async () => {
			await expect(
				page.getByTestId(pageTitle.toLowerCase())
			).toBeVisible();
			await expect(
				page.getByRole('cell', {exact: true, name: 'pageTitleEvent'})
			).toBeVisible();
			await expect(page.locator('.percentage-column')).toContainText(
				'100%'
			);
		});

		await test.step('Add another attribute in the filter and use not contains condition', async () => {
			await removeAttribute({
				page,
				section: 'Filter',
			});

			await addFilter({
				filterName: 'pageTitle',
				input: pageTitle,
				operator: 'does not contain',
				page,
			});
		});

		await test.step('View the information displayed and see that there are no results', async () => {
			await expect(
				page.getByRole('cell', {name: 'No Results'}).first()
			).toBeVisible();
			await expect(
				page.getByRole('cell', {name: 'No Results'}).nth(1)
			).toBeVisible();
		});
	}
);

async function sendCustomEventWithAttributes({
	apiHelpers,
	channelId,
}: {
	apiHelpers: any;
	channelId: string;
}) {
	const date = new Date();

	await apiHelpers.jsonWebServicesOSBAsah.createEvents([
		{
			applicationId: 'CustomEvent',
			canonicalUrl: 'https://www.liferay.com',
			channelId,
			eventDate: date.toISOString(),
			eventId: 'customEvent',
			properties: [
				{name: 'category', value: 'wetsuit'},
				{name: 'pageTitle', value: 'My Page'},
				{name: 'url', value: 'https://www.liferay.com'},
			],
			title: 'Liferay',
			userId: '1',
		},
	]);

	await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
		{
			applicationId: 'CustomEvent',
			displayName: 'customEvent',
			eventAttributeDefinitions: ['category', 'pageTitle', 'url'].map(
				(name) => ({
					dataType: 'STRING',
					displayName: name,
					name,
					type: 'LOCAL',
				})
			),
			name: 'customEvent',
			type: 'CUSTOM',
		},
	]);
}

test(
	'Event Analysis keeps the event, breakdown and filter and clears them when the event is removed',
	{
		tag: '@LRAC-10266',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await sendCustomEventWithAttributes({
			apiHelpers,
			channelId: channel.id,
		});

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await addCustomEvent({customEventName: 'customEvent', page});

		await addBreakdown({breakdownName: 'pageTitle', page, tab: 'Event'});

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		// The event, its breakdown and the result are all present

		await expect(
			page.locator('.event-container').filter({hasText: 'customEvent'})
		).toBeVisible();

		await expect(
			page.locator('.attribute-breakdown-section-root').filter({
				hasText: 'pageTitle',
			})
		).toBeVisible();

		// Removing the event clears the analysis

		await removeAttribute({page, section: 'Event'});

		await expect(
			page.locator('.event-container').filter({hasText: 'customEvent'})
		).toHaveCount(0);

		await expect(
			page.locator('.attribute-breakdown-section-root').filter({
				hasText: 'pageTitle',
			})
		).toHaveCount(0);
	}
);

test(
	'The custom events list can be searched by name',
	{
		tag: '@LRAC-10007',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const date = new Date();

		const customEventNames = [
			'searchA' + getRandomString(),
			'searchB' + getRandomString(),
		];

		for (const eventName of customEventNames) {
			await apiHelpers.jsonWebServicesOSBAsah.createEvents([
				{
					applicationId: 'CustomEvent',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: date.toISOString(),
					eventId: eventName,
					title: 'Liferay',
					userId: '1',
				},
			]);

			await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
				{
					applicationId: 'CustomEvent',
					displayName: eventName,
					eventAttributeDefinitions: [],
					name: eventName,
					type: 'CUSTOM',
				},
			]);
		}

		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsEventsCustomPage,
			page,
			projectID: project.groupId,
		});

		// Searching each event name returns it

		for (const eventName of customEventNames) {
			await page.getByPlaceholder('Search').first().fill(eventName);

			await page.keyboard.press('Enter');

			await expect(
				page.getByRole('link', {name: eventName})
			).toBeVisible();
		}

		// Searching a name that does not exist shows the empty message

		await page
			.getByPlaceholder('Search')
			.first()
			.fill('missing' + getRandomString());

		await page.keyboard.press('Enter');

		await expect(
			page.getByText('There are no results found.')
		).toBeVisible();
	}
);

test(
	'A custom event stays listed when searching and changing pagination',
	{
		tag: '@LRAC-10262',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const customEventName = 'paginate' + getRandomString();

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'CustomEvent',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: new Date().toISOString(),
				eventId: customEventName,
				title: 'Liferay',
				userId: '1',
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'CustomEvent',
				displayName: customEventName,
				eventAttributeDefinitions: [],
				name: customEventName,
				type: 'CUSTOM',
			},
		]);

		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsEventsCustomPage,
			page,
			projectID: project.groupId,
		});

		const customEventLink = page.getByRole('link', {name: customEventName});

		await expect(customEventLink).toBeVisible();

		// The event is found when searching for it

		await page.getByPlaceholder('Search').first().fill(customEventName);

		await page.keyboard.press('Enter');

		await expect(customEventLink).toBeVisible();

		// The event stays listed across pagination sizes

		await selectPaginationItemsPerPage({itemsPerPage: '40', page});

		await expect(customEventLink).toBeVisible();

		await selectPaginationItemsPerPage({itemsPerPage: '20', page});

		await expect(customEventLink).toBeVisible();
	}
);

test(
	'Event Analysis can filter a number attribute by greater than and less than',
	{
		tag: '@LRAC-10277',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'CustomEvent',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: new Date().toISOString(),
				eventId: 'customEvent',
				properties: [{name: 'price', value: '259.95'}],
				title: 'Liferay',
				userId: '1',
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'CustomEvent',
				displayName: 'customEvent',
				eventAttributeDefinitions: [
					{
						dataType: 'NUMBER',
						displayName: 'price',
						name: 'price',
						type: 'LOCAL',
					},
				],
				name: 'customEvent',
				type: 'CUSTOM',
			},
		]);

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await addCustomEvent({customEventName: 'customEvent', page});

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		// The event matches when the price is greater than 250

		await addFilter({
			filterName: 'price',
			input: '250',
			operator: 'is greater than',
			page,
		});

		await expect(
			page.getByRole('row', {name: 'customEvent'})
		).toBeVisible();

		// The event does not match when the price is less than 250

		await removeAttribute({page, section: 'Filter'});

		await addFilter({
			filterName: 'price',
			input: '250',
			operator: 'is less than',
			page,
		});

		await expect(page.getByRole('row', {name: 'customEvent'})).toHaveCount(
			0
		);
	}
);

test(
	'A new Event Analysis offers an event add control but no breakdown or filter until an event is added',
	{
		tag: '@LRAC-10314',
	},
	async ({analyticsChannel: channel, page, project}) => {
		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		// The event section offers an add control

		await expect(
			page.locator('.event-section-root').getByLabel('Add')
		).toBeVisible();

		// The breakdown and filter sections do not offer an add control yet

		await expect(
			page.locator('.attribute-breakdown-section-root').getByLabel('Add')
		).toHaveCount(0);

		await expect(
			page.locator('.attribute-filter-section-root').getByLabel('Add')
		).toHaveCount(0);
	}
);

test(
	'The event picker lists events under the All, Default and Custom tabs',
	{
		tag: '@LRAC-10295',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const customEventName = 'tabs' + getRandomString();

		const date = new Date();

		// A default event (pageViewed) and a custom event

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date.toISOString(),
				eventId: 'pageViewed',
				title: 'My Page',
				userId: '1',
			},
			{
				applicationId: 'CustomEvent',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date.toISOString(),
				eventId: customEventName,
				title: 'Liferay',
				userId: '1',
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'CustomEvent',
				displayName: customEventName,
				eventAttributeDefinitions: [],
				name: customEventName,
				type: 'CUSTOM',
			},
		]);

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await page.getByLabel('Add').click();

		const defaultEvent = page.getByRole('menuitem', {name: 'pageViewed'});

		const customEvent = page.getByRole('menuitem', {name: customEventName});

		// The All tab lists both the default and the custom event

		await page
			.locator('.card-tab')
			.filter({hasText: 'All'})
			.first()
			.click();

		await expect(defaultEvent).toBeVisible();

		await expect(customEvent).toBeVisible();

		// The Default tab lists only the default event

		await page
			.locator('.card-tab')
			.filter({hasText: 'Default'})
			.first()
			.click();

		await expect(defaultEvent).toBeVisible();

		await expect(customEvent).toHaveCount(0);

		// The Custom tab lists only the custom event

		await page
			.locator('.card-tab')
			.filter({hasText: 'Custom'})
			.first()
			.click();

		await expect(customEvent).toBeVisible();

		await expect(defaultEvent).toHaveCount(0);
	}
);

test(
	'The breakdown picker Event tab lists the default and custom attributes',
	{
		tag: '@LRAC-10301',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await sendCustomEventWithAttributes({
			apiHelpers,
			channelId: channel.id,
		});

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await addCustomEvent({customEventName: 'customEvent', page});

		// Open the breakdown picker on the Event tab

		await page
			.locator('.attribute-breakdown-section-root')
			.getByLabel('Add')
			.click();

		await page
			.locator('.card-tab')
			.filter({hasText: 'Event'})
			.first()
			.click();

		// The default and custom attributes are listed

		await expect(
			page.getByRole('menuitem', {exact: true, name: 'pageTitle'})
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {exact: true, name: 'category'})
		).toBeVisible();
	}
);

test(
	'The event attributes list can be searched by name',
	{
		tag: '@LRAC-10017',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const attributes = [
			{dataType: 'STRING', name: 'category', value: 'wetsuit'},
			{dataType: 'NUMBER', name: 'price', value: '259.95'},
			{dataType: 'NUMBER', name: 'temp', value: '11'},
			{
				dataType: 'DATE',
				name: 'birthdate',
				value: '2021-11-25T14:36:30.685Z',
			},
			{dataType: 'BOOLEAN', name: 'like', value: 'true'},
			{dataType: 'DURATION', name: 'duration', value: '3600000'},
		];

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'CustomEvent',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: new Date().toISOString(),
				eventId: 'customEvent',
				properties: attributes.map(({name, value}) => ({name, value})),
				title: 'Liferay',
				userId: '1',
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'CustomEvent',
				displayName: 'customEvent',
				eventAttributeDefinitions: attributes.map(
					({dataType, name}) => ({
						dataType,
						displayName: name,
						name,
						type: 'LOCAL',
					})
				),
				name: 'customEvent',
				type: 'CUSTOM',
			},
		]);

		await navigateToACSettingsViaURL({
			acPage: ACPage.eventAttributesPage,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {exact: true, name: 'Attributes'}).click();

		// Each attribute is found when searching for it

		for (const {name} of attributes) {
			await page.getByPlaceholder('Search').fill(name);

			await page.keyboard.press('Enter');

			await expect(
				page.getByRole('link', {exact: true, name})
			).toBeVisible();
		}

		// A name that does not exist shows the empty message

		await page
			.getByPlaceholder('Search')
			.fill('missing' + getRandomString());

		await page.keyboard.press('Enter');

		await expect(
			page.getByText('There are no results found.')
		).toBeVisible();
	}
);

test(
	'Event Analysis can filter a string attribute by is and is not',
	{
		tag: '@LRAC-10275',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await sendCustomEventWithAttributes({
			apiHelpers,
			channelId: channel.id,
		});

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await addCustomEvent({customEventName: 'customEvent', page});

		await addBreakdown({breakdownName: 'pageTitle', page, tab: 'Event'});

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		// The event matches when the category is wetsuit

		await addAttributeFilter({
			attributeName: 'category',
			condition: 'is',
			page,
			value: 'wetsuit',
		});

		await expect(
			page.getByRole('row', {exact: true, name: 'customEvent 1'})
		).toBeVisible();

		// The event does not match when the category is not wetsuit

		await removeAttribute({page, section: 'Filter'});

		await addAttributeFilter({
			attributeName: 'category',
			condition: 'is not',
			page,
			value: 'wetsuit',
		});

		await expect(
			page.getByRole('row', {exact: true, name: 'customEvent 1'})
		).toHaveCount(0);
	}
);

test(
	'Event Analysis allows adding the same filter twice',
	{
		tag: '@LRAC-10316',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await sendCustomEventWithAttributes({
			apiHelpers,
			channelId: channel.id,
		});

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await addCustomEvent({customEventName: 'customEvent', page});

		await addBreakdown({breakdownName: 'pageTitle', page, tab: 'Event'});

		// Add the same category filter twice

		await addAttributeFilter({
			attributeName: 'category',
			condition: 'is',
			page,
			value: 'wetsuit',
		});

		await addAttributeFilter({
			attributeName: 'category',
			condition: 'is',
			page,
			value: 'wetsuit',
		});

		// Both equal filters are present in the dashboard

		await expect(
			page.locator('.attribute-filter-section-root').getByText('wetsuit')
		).toHaveCount(2);

		// The analysis result still appears

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		await expect(
			page.getByRole('row', {exact: true, name: 'customEvent 1'})
		).toBeVisible();
	}
);

test(
	'Event Analysis creation with a breakdown and a filtered attribute',
	{
		tag: '@LRAC-10264',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'CustomEvent',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: new Date().toISOString(),
				eventId: 'customEvent',
				properties: [
					{name: 'pageTitle', value: 'My Page'},
					{name: 'url', value: 'https://www.liferay.com/web/site'},
				],
				title: 'Liferay',
				userId: '1',
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'CustomEvent',
				displayName: 'customEvent',
				eventAttributeDefinitions: ['pageTitle', 'url'].map((name) => ({
					dataType: 'STRING',
					displayName: name,
					name,
					type: 'LOCAL',
				})),
				name: 'customEvent',
				type: 'CUSTOM',
			},
		]);

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await addCustomEvent({customEventName: 'customEvent', page});

		// Only one event can be analyzed, so the event add control is gone

		await expect(
			page.locator('.event-section-root').getByLabel('Add')
		).toHaveCount(0);

		await addBreakdown({breakdownName: 'pageTitle', page, tab: 'Event'});

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		await addFilter({
			filterName: 'url',
			input: 'site',
			operator: 'contains',
			page,
		});

		// The dashboard shows the event, the breakdown, the filter and a result

		await expect(
			page
				.locator('.attribute-breakdown-section-root')
				.getByText('pageTitle')
		).toBeVisible();

		await expect(
			page.locator('.attribute-filter-section-root').getByText('site')
		).toBeVisible();

		await expect(
			page.getByRole('row', {exact: true, name: 'customEvent 1'})
		).toBeVisible();
	}
);

test(
	'Event Analysis allows a maximum of five breakdowns',
	{
		tag: '@LRAC-10265',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const attributeNames = [
			'category',
			'color',
			'pageTitle',
			'size',
			'url',
		];

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'CustomEvent',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: new Date().toISOString(),
				eventId: 'customEvent',
				properties: attributeNames.map((name) => ({name, value: name})),
				title: 'Liferay',
				userId: '1',
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'CustomEvent',
				displayName: 'customEvent',
				eventAttributeDefinitions: attributeNames.map((name) => ({
					dataType: 'STRING',
					displayName: name,
					name,
					type: 'LOCAL',
				})),
				name: 'customEvent',
				type: 'CUSTOM',
			},
		]);

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await addCustomEvent({customEventName: 'customEvent', page});

		const breakdownAddButton = page
			.locator('.attribute-breakdown-section-root')
			.getByLabel('Add');

		// The add control is still offered after four breakdowns

		for (const breakdownName of attributeNames.slice(0, 4)) {
			await addBreakdown({breakdownName, page, tab: 'Event'});
		}

		await expect(breakdownAddButton).toBeVisible();

		// The add control is gone once the fifth breakdown is added

		await addBreakdown({
			breakdownName: attributeNames[4],
			page,
			tab: 'Event',
		});

		await expect(breakdownAddButton).toHaveCount(0);
	}
);

test(
	'Event Analysis keeps the event result after removing the breakdown',
	{
		tag: '@LRAC-10306',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await sendCustomEventWithAttributes({
			apiHelpers,
			channelId: channel.id,
		});

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await addCustomEvent({customEventName: 'customEvent', page});

		await addBreakdown({breakdownName: 'pageTitle', page, tab: 'Event'});

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		// The breakdown is part of the analysis

		await expect(
			page
				.locator('.attribute-breakdown-section-root')
				.getByText('pageTitle')
		).toBeVisible();

		// Removing the breakdown clears it from the analysis

		await removeAttribute({page, section: 'Breakdown'});

		await expect(
			page
				.locator('.attribute-breakdown-section-root')
				.getByText('pageTitle')
		).toHaveCount(0);

		// The event result still appears without the breakdown

		await expect(
			page.getByRole('row', {name: 'customEvent'})
		).toBeVisible();
	}
);

test(
	'Event Analysis can filter a boolean attribute by true and false',
	{
		tag: '@LRAC-10286',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'CustomEvent',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: new Date().toISOString(),
				eventId: 'customEvent',
				properties: [
					{name: 'dislike', value: 'false'},
					{name: 'like', value: 'true'},
					{name: 'pageTitle', value: 'My Page'},
				],
				title: 'Liferay',
				userId: '1',
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'CustomEvent',
				displayName: 'customEvent',
				eventAttributeDefinitions: [
					{
						dataType: 'BOOLEAN',
						displayName: 'dislike',
						name: 'dislike',
						type: 'LOCAL',
					},
					{
						dataType: 'BOOLEAN',
						displayName: 'like',
						name: 'like',
						type: 'LOCAL',
					},
					{
						dataType: 'STRING',
						displayName: 'pageTitle',
						name: 'pageTitle',
						type: 'LOCAL',
					},
				],
				name: 'customEvent',
				type: 'CUSTOM',
			},
		]);

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await addCustomEvent({customEventName: 'customEvent', page});

		await addBreakdown({breakdownName: 'pageTitle', page, tab: 'Event'});

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		const resultRow = page.getByRole('row', {
			exact: true,
			name: 'customEvent 1',
		});

		// like is true, so filtering like by true matches and by false does not

		await addBooleanFilter({attributeName: 'like', page, value: 'true'});

		await expect(resultRow).toBeVisible();

		await removeAttribute({page, section: 'Filter'});

		await addBooleanFilter({attributeName: 'like', page, value: 'false'});

		await expect(resultRow).toHaveCount(0);

		// dislike is false, so filtering dislike by false matches and by true does not

		await removeAttribute({page, section: 'Filter'});

		await addBooleanFilter({
			attributeName: 'dislike',
			page,
			value: 'false',
		});

		await expect(resultRow).toBeVisible();

		await removeAttribute({page, section: 'Filter'});

		await addBooleanFilter({attributeName: 'dislike', page, value: 'true'});

		await expect(resultRow).toHaveCount(0);
	}
);

test(
	'Event Analysis can filter a date attribute by is, before, after and between',
	{
		tag: '@LRAC-10280',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {

		// Anchor the dates to the previous month so the date range calendar is
		// always one "previous month" click away and the dates stay in the past

		const now = new Date();

		const previousMonth = new Date(
			now.getFullYear(),
			now.getMonth() - 1,
			1
		);

		const pad = (value: number) => String(value).padStart(2, '0');

		const toYMD = (day: number) =>
			`${previousMonth.getFullYear()}-${pad(
				previousMonth.getMonth() + 1
			)}-${pad(day)}`;

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'CustomEvent',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: now.toISOString(),
				eventId: 'customEvent',
				properties: [
					{name: 'birthdate', value: `${toYMD(15)}T12:00:00.000Z`},
					{name: 'pageTitle', value: 'My Page'},
				],
				title: 'Liferay',
				userId: '1',
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'CustomEvent',
				displayName: 'customEvent',
				eventAttributeDefinitions: [
					{
						dataType: 'DATE',
						displayName: 'birthdate',
						name: 'birthdate',
						type: 'LOCAL',
					},
					{
						dataType: 'STRING',
						displayName: 'pageTitle',
						name: 'pageTitle',
						type: 'LOCAL',
					},
				],
				name: 'customEvent',
				type: 'CUSTOM',
			},
		]);

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await addCustomEvent({customEventName: 'customEvent', page});

		await addBreakdown({breakdownName: 'pageTitle', page, tab: 'Event'});

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		const resultRow = page.getByRole('row', {
			exact: true,
			name: 'customEvent 1',
		});

		// birthdate is the 15th, so "is" that date matches

		await addDateFilter({
			attributeName: 'birthdate',
			operator: 'is',
			page,
			value: toYMD(15),
		});

		await expect(resultRow).toBeVisible();

		// before the 14th does not match

		await removeAttribute({page, section: 'Filter'});

		await addDateFilter({
			attributeName: 'birthdate',
			operator: 'before',
			page,
			value: toYMD(14),
		});

		await expect(resultRow).toHaveCount(0);

		// after the 14th matches

		await removeAttribute({page, section: 'Filter'});

		await addDateFilter({
			attributeName: 'birthdate',
			operator: 'after',
			page,
			value: toYMD(14),
		});

		await expect(resultRow).toBeVisible();

		// between the 14th and the 16th matches

		await removeAttribute({page, section: 'Filter'});

		await page
			.locator('.attribute-filter-section-root')
			.getByLabel('Add')
			.click();

		await page
			.getByRole('menuitem', {exact: true, name: 'birthdate'})
			.click();

		await selectAndExpectToHaveValue({
			optionLabel: 'is between',
			select: page.getByLabel('Condition'),
		});

		await page.getByTestId('date-range-input').click();

		await page.getByTestId('previous-month').click();

		const calendar = page.locator('.calendar-root');

		await calendar
			.locator('.day-root:not(.outside-month)')
			.filter({hasText: /^14$/})
			.click();

		await calendar
			.locator('.day-root:not(.outside-month)')
			.filter({hasText: /^16$/})
			.click();

		await page.getByRole('button', {name: 'Apply'}).click();

		await expect(resultRow).toBeVisible();
	}
);

test(
	'Event Analysis reorders breakdowns and restores the original order',
	{
		tag: '@LRAC-10267',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await sendCustomEventWithAttributes({
			apiHelpers,
			channelId: channel.id,
		});

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await addCustomEvent({customEventName: 'customEvent', page});

		for (const breakdownName of ['category', 'pageTitle', 'url']) {
			await addBreakdown({breakdownName, page, tab: 'Event'});
		}

		const breakdownChips = page.locator(
			'.attribute-breakdown-section-root .attribute-chip-container'
		);

		const expectOrder = async (order: string[]) => {
			for (let index = 0; index < order.length; index++) {
				await expect(breakdownChips.nth(index)).toContainText(
					order[index]
				);
			}
		};

		// The breakdowns keep the order in which they were added

		await expectOrder(['category', 'pageTitle', 'url']);

		// Moving the second breakdown before the first reorders them

		await dragAndDropElement({
			dragTarget: breakdownChips.nth(1).locator('.drag-handle'),
			dropTarget: breakdownChips.nth(0),
		});

		await expectOrder(['pageTitle', 'category', 'url']);

		// Moving it back restores the original order

		await dragAndDropElement({
			dragTarget: breakdownChips.nth(1).locator('.drag-handle'),
			dropTarget: breakdownChips.nth(0),
		});

		await expectOrder(['category', 'pageTitle', 'url']);
	}
);

test(
	'Event Analysis can edit an attribute display name from the breakdown picker',
	{
		tag: '@LRAC-10304',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'CustomEvent',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: new Date().toISOString(),
				eventId: 'customEvent',
				properties: [{name: 'color', value: 'red'}],
				title: 'Liferay',
				userId: '1',
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'CustomEvent',
				displayName: 'customEvent',
				eventAttributeDefinitions: [
					{
						dataType: 'STRING',
						displayName: 'color',
						name: 'color',
						type: 'LOCAL',
					},
				],
				name: 'customEvent',
				type: 'CUSTOM',
			},
		]);

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await addCustomEvent({customEventName: 'customEvent', page});

		// Open the breakdown picker and edit the color attribute display name

		await page
			.locator('.attribute-breakdown-section-root')
			.getByLabel('Add')
			.click();

		await page.getByRole('menuitem', {exact: true, name: 'color'}).hover();

		await page.getByRole('button', {name: 'edit'}).click();

		await page.getByLabel('Display Name').fill('color Display Name');

		await page.getByRole('button', {exact: true, name: 'Save'}).click();

		// Reopen the picker and confirm the new display name is listed

		await page
			.locator('.attribute-breakdown-section-root')
			.getByLabel('Add')
			.click();

		await expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'color Display Name',
			})
		).toBeVisible();
	}
);

test(
	'Event Analysis shows an empty state when no analysis is saved',
	{
		tag: '@LRAC-10566',
	},
	async ({analyticsChannel: channel, page, project}) => {
		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await expect(
			page.getByText('Create an analysis to get started.')
		).toBeVisible();

		await expect(
			page.getByRole('link', {
				name: 'Access our documentation to learn more.',
			})
		).toBeVisible();
	}
);

test(
	'Event Analysis saved reports can be searched by name',
	{
		tag: '@LRAC-10563',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await sendCustomEventWithAttributes({
			apiHelpers,
			channelId: channel.id,
		});

		const names = ['Save Analysis 1', 'Save Analysis 2'];

		for (const name of names) {
			await createAndSaveEventAnalysis({
				channelId: channel.id,
				eventName: 'customEvent',
				name,
				page,
				projectId: project.groupId,
			});
		}

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		const listSearch = page
			.locator('.event-analysis-list-root')
			.getByPlaceholder('Search');

		// Each saved analysis is found when searching its name

		for (const name of names) {
			await listSearch.fill(name);

			await page.keyboard.press('Enter');

			await expect(page.getByRole('link', {name})).toBeVisible();
		}

		// A name that does not exist is not listed

		await listSearch.fill('ac dxp');

		await page.keyboard.press('Enter');

		await expect(page.getByRole('link', {name: 'ac dxp'})).toHaveCount(0);
	}
);

test(
	'Event Analysis report persists its event, breakdowns and filter when reopened',
	{
		tag: '@LRAC-10558',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'CustomEvent',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: new Date().toISOString(),
				eventId: 'customEvent',
				properties: [
					{name: 'category', value: 'wetsuit'},
					{name: 'like', value: 'true'},
					{name: 'url', value: 'https://www.liferay.com'},
				],
				title: 'Liferay',
				userId: '1',
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'CustomEvent',
				displayName: 'customEvent',
				eventAttributeDefinitions: [
					{
						dataType: 'STRING',
						displayName: 'category',
						name: 'category',
						type: 'LOCAL',
					},
					{
						dataType: 'BOOLEAN',
						displayName: 'like',
						name: 'like',
						type: 'LOCAL',
					},
					{
						dataType: 'STRING',
						displayName: 'url',
						name: 'url',
						type: 'LOCAL',
					},
				],
				name: 'customEvent',
				type: 'CUSTOM',
			},
		]);

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({eventAnalysisName: 'Save Analysis', page});

		await addCustomEvent({customEventName: 'customEvent', page});

		await addBreakdown({breakdownName: 'category', page, tab: 'Event'});

		await addBreakdown({breakdownName: 'url', page, tab: 'Event'});

		await addBooleanFilter({attributeName: 'like', page, value: 'true'});

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		await page
			.locator('.event-analysis-toolbar-right-content')
			.getByRole('button', {name: 'Save Analysis'})
			.click();

		// The saved analysis is listed; reopen it

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await expect(
			page.getByRole('link', {name: 'Save Analysis'})
		).toBeVisible();

		await page.getByRole('link', {name: 'Save Analysis'}).click();

		// The event, breakdowns, filter and result all persisted

		for (const breakdownName of ['category', 'url']) {
			await expect(
				page
					.locator('.attribute-breakdown-section-root')
					.getByText(breakdownName)
			).toBeVisible();
		}

		await expect(
			page.locator('.attribute-filter-section-root').getByText('like')
		).toBeVisible();

		await expect(page.getByRole('cell').getByText('wetsuit')).toBeVisible();
	}
);

test(
	'Event Analysis report accepts special characters in its name',
	{
		tag: '@LRAC-10567',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await sendCustomEventWithAttributes({
			apiHelpers,
			channelId: channel.id,
		});

		const name = '@#$%*&_>Save+Analysis<!?';

		await createAndSaveEventAnalysis({
			channelId: channel.id,
			eventName: 'customEvent',
			name,
			page,
			projectId: project.groupId,
		});

		// The saved analysis with the special-character name is listed and reopens

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await expect(page.getByRole('link', {name})).toBeVisible();

		await page.getByRole('link', {name}).click();

		await expect(
			page.locator('.event-container').filter({hasText: 'customEvent'})
		).toBeVisible();
	}
);

test(
	'Event Analysis warns when saving a report with an existing name',
	{
		tag: '@LRAC-10561',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await sendCustomEventWithAttributes({
			apiHelpers,
			channelId: channel.id,
		});

		await createAndSaveEventAnalysis({
			channelId: channel.id,
			eventName: 'customEvent',
			name: 'Save Analysis 1',
			page,
			projectId: project.groupId,
		});

		// Build a second analysis and try to save it with the same name

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: 'Save Analysis 1',
			page,
		});

		await addCustomEvent({customEventName: 'customEvent', page});

		const saveButton = page
			.locator('.event-analysis-toolbar-right-content')
			.getByRole('button', {name: 'Save Analysis'});

		await saveButton.click();

		// The duplicate name is rejected with a warning

		await expect(
			page.getByText(
				'This analysis name is currently in use. Please try a different one.'
			)
		).toBeVisible();

		// Renaming to a unique name saves successfully

		await setEventAnalysisName({
			eventAnalysisName: 'Save Analysis 2',
			page,
		});

		await saveButton.click();

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await expect(
			page.getByRole('link', {name: 'Save Analysis 1'})
		).toBeVisible();

		await expect(
			page.getByRole('link', {name: 'Save Analysis 2'})
		).toBeVisible();
	}
);

test(
	'Event Analysis reports can be deleted individually and in bulk',
	{
		tag: '@LRAC-10560',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await sendCustomEventWithAttributes({
			apiHelpers,
			channelId: channel.id,
		});

		const names = ['Save Analysis 1', 'Save Analysis 2', 'Save Analysis 3'];

		for (const name of names) {
			await createAndSaveEventAnalysis({
				channelId: channel.id,
				eventName: 'customEvent',
				name,
				page,
				projectId: project.groupId,
			});
		}

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		// Capture the URL of the first analysis, then return to the list

		await page.getByRole('link', {name: 'Save Analysis 1'}).click();

		const deletedAnalysisURL = page.url();

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		// Delete the first analysis from its row action

		const firstRow = page.getByRole('row', {name: 'Save Analysis 1'});

		await firstRow.hover();

		await firstRow.getByRole('button', {name: 'Delete'}).click();

		await page
			.locator('.modal')
			.getByRole('button', {name: 'Delete'})
			.click();

		await expect(
			page.getByRole('link', {name: 'Save Analysis 1'})
		).toHaveCount(0);

		// The deleted analysis URL shows the not-found page

		await page.goto(deletedAnalysisURL);

		await expect(page.getByText('Analysis Not Found')).toBeVisible();

		// Bulk delete the remaining analyses

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page
			.locator('.event-analysis-list-root')
			.getByRole('checkbox')
			.first()
			.check();

		await page.getByRole('button', {name: 'Delete'}).click();

		await page
			.locator('.modal')
			.getByRole('button', {name: 'Delete'})
			.click();

		await expect(
			page.getByRole('link', {name: 'Save Analysis 2'})
		).toHaveCount(0);

		await expect(
			page.getByRole('link', {name: 'Save Analysis 3'})
		).toHaveCount(0);
	}
);

test(
	'Event Analysis report can be renamed and have a breakdown added when edited',
	{
		tag: '@LRAC-10559',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'CustomEvent',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: new Date().toISOString(),
				eventId: 'customEvent',
				properties: [
					{name: 'like', value: 'true'},
					{name: 'price', value: '259.95'},
				],
				title: 'Liferay',
				userId: '1',
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'CustomEvent',
				displayName: 'customEvent',
				eventAttributeDefinitions: [
					{
						dataType: 'BOOLEAN',
						displayName: 'like',
						name: 'like',
						type: 'LOCAL',
					},
					{
						dataType: 'NUMBER',
						displayName: 'price',
						name: 'price',
						type: 'LOCAL',
					},
				],
				name: 'customEvent',
				type: 'CUSTOM',
			},
		]);

		const saveButton = page
			.locator('.event-analysis-toolbar-right-content')
			.getByRole('button', {name: 'Save Analysis'});

		// Create and save an analysis with a price breakdown

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({eventAnalysisName: 'Save Analysis', page});

		await addCustomEvent({customEventName: 'customEvent', page});

		await addBreakdown({breakdownName: 'price', page, tab: 'Event'});

		await saveButton.click();

		// Reopen it, rename it and add a second breakdown

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Save Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: 'Edited Analysis',
			page,
		});

		await addBreakdown({breakdownName: 'like', page, tab: 'Event'});

		await saveButton.click();

		// The edit persists: the renamed report keeps both breakdowns

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await expect(
			page.getByRole('link', {name: 'Save Analysis'})
		).toHaveCount(0);

		await page.getByRole('link', {name: 'Edited Analysis'}).click();

		for (const breakdownName of ['price', 'like']) {
			await expect(
				page
					.locator('.attribute-breakdown-section-root')
					.getByText(breakdownName)
			).toBeVisible();
		}
	}
);

test(
	'Event Analysis reports can be sorted by name',
	{
		tag: '@LRAC-10564',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await sendCustomEventWithAttributes({
			apiHelpers,
			channelId: channel.id,
		});

		const names = ['Save Analysis 1', 'Save Analysis 2', 'Save Analysis 3'];

		for (const name of names) {
			await createAndSaveEventAnalysis({
				channelId: channel.id,
				eventName: 'customEvent',
				name,
				page,
				projectId: project.groupId,
			});
		}

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		const rows = page.locator('.event-analysis-list-root tbody tr');

		const expectOrder = async (order: string[]) => {
			for (let index = 0; index < order.length; index++) {
				await expect(rows.nth(index)).toContainText(order[index]);
			}
		};

		const nameColumnHeader = page
			.locator('.event-analysis-list-root')
			.getByText('Name', {exact: true});

		// Sorting by name toggles between descending and ascending order

		await nameColumnHeader.click();

		await expectOrder([...names].reverse());

		await nameColumnHeader.click();

		await expectOrder(names);
	}
);

test(
	'Event Analysis reports list paginates',
	{
		tag: '@LRAC-10562',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await sendCustomEventWithAttributes({
			apiHelpers,
			channelId: channel.id,
		});

		for (let index = 0; index < 8; index++) {
			await createAndSaveEventAnalysis({
				channelId: channel.id,
				eventName: 'customEvent',
				name: `Save Analysis ${index}`,
				page,
				projectId: project.groupId,
			});
		}

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		// All eight reports fit on a single page by default

		await expect(
			page.getByText('Showing 1 to 8 of 8 entries.')
		).toBeVisible();

		// Four per page splits them across two pages

		await selectPaginationItemsPerPage({itemsPerPage: '4', page});

		await expect(
			page.getByText('Showing 1 to 4 of 8 entries.')
		).toBeVisible();

		await selectPaginationPageNumber({page, paginationPageNumber: '2'});

		await expect(
			page.getByText('Showing 5 to 8 of 8 entries.')
		).toBeVisible();
	}
);

test(
	'An event analysis can be built from the All, Default and Custom event picker tabs',
	{tag: '@LRAC-10292'},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const date = new Date().toISOString();

		// A default event (pageViewed) and a custom event, both with a pageTitle
		// attribute so a breakdown produces a result row

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'Page',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date,
				eventId: 'pageViewed',
				properties: [{name: 'pageTitle', value: 'My Page'}],
				title: 'My Page',
				userId: '1',
			},
			{
				applicationId: 'CustomEvent',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: date,
				eventId: 'customEvent',
				properties: [{name: 'pageTitle', value: 'My Page'}],
				title: 'Liferay',
				userId: '1',
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'CustomEvent',
				displayName: 'customEvent',
				eventAttributeDefinitions: [
					{
						dataType: 'STRING',
						displayName: 'pageTitle',
						name: 'pageTitle',
						type: 'LOCAL',
					},
				],
				name: 'customEvent',
				type: 'CUSTOM',
			},
		]);

		async function addEventFromTab(eventName: string, tab: string) {
			await page.getByLabel('Add').click();

			await page
				.locator('.card-tab')
				.filter({hasText: tab})
				.first()
				.click();

			await page
				.getByRole('menuitem', {exact: true, name: eventName})
				.click();

			await expect(
				page.locator('.event-container').filter({hasText: eventName})
			).toBeVisible();
		}

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		// The custom event added from the All tab produces a result

		await addEventFromTab('customEvent', 'All');

		await addBreakdown({breakdownName: 'pageTitle', page, tab: 'Event'});

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		await expect(
			page.getByRole('row', {exact: true, name: 'customEvent 1'})
		).toBeVisible();

		// The default event can be added from the Default tab

		await removeAttribute({page, section: 'Event'});

		await addEventFromTab('pageViewed', 'Default');

		// The custom event can be added from the Custom tab

		await removeAttribute({page, section: 'Event'});

		await addEventFromTab('customEvent', 'Custom');
	}
);

async function sendSortCustomEvents({apiHelpers, channelId}) {
	const date = new Date().toISOString();

	// Counts are chosen so the event-count order differs from the alphabetical
	// order of the page titles

	const countsByPageTitle = {'AAA Sort': 2, 'BBB Sort': 3, 'CCC Sort': 1};

	const events = [];

	let userId = 1;

	for (const [pageTitle, count] of Object.entries(countsByPageTitle)) {
		for (let i = 0; i < count; i++) {
			events.push({
				applicationId: 'CustomEvent',
				canonicalUrl: 'https://www.liferay.com',
				channelId,
				eventDate: date,
				eventId: 'customEvent',
				properties: [{name: 'pageTitle', value: pageTitle}],
				title: 'Liferay',
				userId: String(userId++),
			});
		}
	}

	await apiHelpers.jsonWebServicesOSBAsah.createEvents(events);

	await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
		{
			applicationId: 'CustomEvent',
			displayName: 'customEvent',
			eventAttributeDefinitions: [
				{
					dataType: 'STRING',
					displayName: 'pageTitle',
					name: 'pageTitle',
					type: 'LOCAL',
				},
			],
			name: 'customEvent',
			type: 'CUSTOM',
		},
	]);
}

test(
	'Attribute breakdowns are sorted by the highest event count by default',
	{
		tag: '@LRAC-10269',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await sendSortCustomEvents({apiHelpers, channelId: channel.id});

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await addCustomEvent({customEventName: 'customEvent', page});

		await addBreakdown({breakdownName: 'pageTitle', page, tab: 'Event'});

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		// The breakdown rows default to highest event count first

		await expect(page.getByRole('cell', {name: 'Sort'})).toHaveText([
			'bbb sort',
			'aaa sort',
			'ccc sort',
		]);
	}
);

test(
	'The analysis result can be sorted ascending and descending',
	{
		tag: '@LRAC-10270',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await sendSortCustomEvents({apiHelpers, channelId: channel.id});

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await addCustomEvent({customEventName: 'customEvent', page});

		await addBreakdown({breakdownName: 'pageTitle', page, tab: 'Event'});

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		// Default is event count descending

		await expect(page.getByRole('cell', {name: 'Sort'})).toHaveText([
			'bbb sort',
			'aaa sort',
			'ccc sort',
		]);

		// Toggle to ascending

		await page.locator('.table-head-title button.component-action').click();

		await expect(page.getByRole('cell', {name: 'Sort'})).toHaveText([
			'ccc sort',
			'aaa sort',
			'bbb sort',
		]);

		// Toggle back to descending

		await page.locator('.table-head-title button.component-action').click();

		await expect(page.getByRole('cell', {name: 'Sort'})).toHaveText([
			'bbb sort',
			'aaa sort',
			'ccc sort',
		]);
	}
);

test(
	'The Total, Unique and Average tabs each show the breakdown chart data',
	{
		tag: '@LRAC-10307',
	},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		await sendSortCustomEvents({apiHelpers, channelId: channel.id});

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await addCustomEvent({customEventName: 'customEvent', page});

		await addBreakdown({breakdownName: 'pageTitle', page, tab: 'Event'});

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		// Each metric tab shows the three breakdown rows

		for (const tab of ['Total', 'Unique', 'Average']) {
			await page.getByRole('button', {exact: true, name: tab}).click();

			await expect(page.getByRole('cell', {name: 'Sort'})).toHaveCount(3);
		}
	}
);

test(
	'Vote and comment events appear as default events in Event Analysis',
	{
		tag: '@LRAC-11541',
	},
	async ({analyticsChannel: channel, page, project}) => {
		const defaultEventNames = ['VOTE', 'posted'];

		// VOTE (ratings) and posted (comments) are default events that ship
		// hidden, so show them before checking the Event Analysis picker

		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsEventsDefaultPage,
			page,
			projectID: project.groupId,
		});

		try {
			for (const defaultEventName of defaultEventNames) {
				await page
					.getByPlaceholder('Search')
					.first()
					.fill(defaultEventName);

				await page.keyboard.press('Enter');

				const row = page.getByRole('row', {name: defaultEventName});

				await row.hover();

				await row.getByRole('button', {name: 'Set to Show'}).click();

				await waitForAlert(
					page,
					`Success:${defaultEventName} has been set to show.`,
					{autoClose: false}
				);
			}

			await navigateToACPageViaURL({
				acPage: ACPage.eventAnalysisPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await page.getByRole('link', {name: 'Create Analysis'}).click();

			await setEventAnalysisName({
				eventAnalysisName: `Event Analysis ${getRandomString()}`,
				page,
			});

			await page.locator('.event-section-root').getByLabel('Add').click();

			// They appear under the Default tab

			await page
				.locator('.card-tab')
				.filter({hasText: 'Default'})
				.first()
				.click();

			for (const defaultEventName of defaultEventNames) {
				await expect(
					page.getByRole('menuitem', {
						exact: true,
						name: defaultEventName,
					})
				).toBeVisible();
			}

			// They are not custom events

			await page
				.locator('.card-tab')
				.filter({hasText: 'Custom'})
				.first()
				.click();

			for (const defaultEventName of defaultEventNames) {
				await expect(
					page.getByRole('menuitem', {
						exact: true,
						name: defaultEventName,
					})
				).toHaveCount(0);
			}
		}
		finally {

			// Restore the hidden state on the shared project

			await navigateToACSettingsViaURL({
				acPage: ACPage.definitionsEventsDefaultPage,
				page,
				projectID: project.groupId,
			});

			for (const defaultEventName of defaultEventNames) {
				await page
					.getByPlaceholder('Search')
					.first()
					.fill(defaultEventName);

				await page.keyboard.press('Enter');

				const row = page.getByRole('row', {name: defaultEventName});

				await row.hover();

				await row.getByRole('button', {name: 'Set to Hide'}).click();

				await page
					.locator('.confirmation-modal-root')
					.getByRole('button', {exact: true, name: 'Hide'})
					.click();

				await waitForAlert(
					page,
					`Success:${defaultEventName} set to hide.`,
					{autoClose: false}
				);
			}
		}
	}
);

test(
	'A member can save an analysis and open one saved by another user',
	{tag: '@LRAC-10569'},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {

		// Seed a custom event with a string and a boolean attribute

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'CustomEvent',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: new Date().toISOString(),
				eventId: 'customEvent',
				properties: [
					{name: 'category', value: 'wetsuit'},
					{name: 'like', value: 'true'},
				],
				title: 'Liferay',
				userId: '1',
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'CustomEvent',
				displayName: 'customEvent',
				eventAttributeDefinitions: [
					{
						dataType: 'STRING',
						displayName: 'category',
						name: 'category',
						type: 'LOCAL',
					},
					{
						dataType: 'BOOLEAN',
						displayName: 'like',
						name: 'like',
						type: 'LOCAL',
					},
				],
				name: 'customEvent',
				type: 'CUSTOM',
			},
		]);

		// As the admin, save an analysis carrying a breakdown and a filter

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({eventAnalysisName: 'Admin Analysis', page});

		await addCustomEvent({customEventName: 'customEvent', page});

		await addBreakdown({breakdownName: 'category', page, tab: 'Event'});

		await addBooleanFilter({attributeName: 'like', page, value: 'true'});

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		await page
			.locator('.event-analysis-toolbar-right-content')
			.getByRole('button', {name: 'Save Analysis'})
			.click();

		// Sign in as a member (non-admin) user

		await signInToAnalyticsCloud(page, 'corbin.murakami@faro.io');

		try {

			// The member can create and save their own analysis

			await createAndSaveEventAnalysis({
				channelId: channel.id,
				eventName: 'customEvent',
				name: 'Member Analysis',
				page,
				projectId: project.groupId,
			});

			// The member can open the analysis the admin saved

			await navigateToACPageViaURL({
				acPage: ACPage.eventAnalysisPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await page.getByRole('link', {name: 'Admin Analysis'}).click();

			// The admin's breakdown, filter and result all render for the member

			await expect(
				page
					.locator('.attribute-breakdown-section-root')
					.getByText('category')
			).toBeVisible();

			await expect(
				page.locator('.attribute-filter-section-root').getByText('like')
			).toBeVisible();

			await expect(
				page.getByRole('cell').getByText('wetsuit')
			).toBeVisible();
		}
		finally {
			await signInToAnalyticsCloud(page, faroConfig.user.login);
		}
	}
);

test(
	'A hidden custom event is removed from the Event Analysis event picker',
	{tag: '@LRAC-10228'},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const eventName = 'hidden' + getRandomString();

		await apiHelpers.jsonWebServicesOSBAsah.createEvents([
			{
				applicationId: 'CustomEvent',
				canonicalUrl: 'https://www.liferay.com',
				channelId: channel.id,
				eventDate: new Date().toISOString(),
				eventId: eventName,
				title: 'Liferay',
				userId: '1',
			},
		]);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'CustomEvent',
				displayName: eventName,
				name: eventName,
				type: 'CUSTOM',
			},
		]);

		const customEvent = page.getByRole('menuitem', {
			exact: true,
			name: eventName,
		});

		async function openCustomEventPicker() {
			await navigateToACPageViaURL({
				acPage: ACPage.eventAnalysisPage,
				channelID: channel.id,
				page,
				projectID: project.groupId,
			});

			await page.getByRole('link', {name: 'Create Analysis'}).click();

			await page.getByLabel('Add').click();

			await page
				.locator('.card-tab')
				.filter({hasText: 'Custom'})
				.first()
				.click();
		}

		// The custom event is offered in the event picker

		await openCustomEventPicker();

		await expect(customEvent).toBeVisible();

		// Hide the custom event from the settings list

		await navigateToACSettingsViaURL({
			acPage: ACPage.definitionsEventsCustomPage,
			page,
			projectID: project.groupId,
		});

		await page.getByPlaceholder('Search').fill(eventName);

		await page.keyboard.press('Enter');

		await page.getByRole('row', {name: eventName}).hover();

		await page
			.getByRole('row', {name: eventName})
			.getByRole('button', {name: 'Set to Hide'})
			.click();

		await page
			.locator('.confirmation-modal-root')
			.getByRole('button', {exact: true, name: 'Hide'})
			.click();

		await page.getByRole('row', {name: eventName}).hover();

		await expect(
			page
				.getByRole('row', {name: eventName})
				.getByRole('button', {name: 'Set to Show'})
		).toBeVisible();

		// The hidden custom event is no longer offered in the picker

		await openCustomEventPicker();

		await expect(customEvent).toHaveCount(0);
	}
);

test(
	'Custom events are listed in alphabetical order in the Event Analysis picker',
	{tag: '@LRAC-10311'},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const token = getRandomString();

		// Seed in an order that differs from the alphabetical order

		const names = [`m${token}`, `a${token}`, `z${token}`];

		for (const name of names) {
			await apiHelpers.jsonWebServicesOSBAsah.createEvents([
				{
					applicationId: 'CustomEvent',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: new Date().toISOString(),
					eventId: name,
					title: 'Liferay',
					userId: '1',
				},
			]);

			await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
				{
					applicationId: 'CustomEvent',
					displayName: name,
					name,
					type: 'CUSTOM',
				},
			]);
		}

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await page.getByLabel('Add').click();

		await page
			.locator('.card-tab')
			.filter({hasText: 'Custom'})
			.first()
			.click();

		// The three seeded events render alphabetically regardless of seed order

		const seededMenuItems = page
			.getByRole('menuitem')
			.filter({hasText: token});

		await expect(seededMenuItems).toHaveText([
			`a${token}`,
			`m${token}`,
			`z${token}`,
		]);
	}
);

test(
	'Reordering the breakdowns re-sorts the Event Analysis result rows',
	{tag: '@LRAC-10272'},
	async ({analyticsChannel: channel, apiHelpers, page, project}) => {
		const token = getRandomString();

		const eventId = 'reorder' + token;
		const pageAttribute = 'page' + token;
		const colorAttribute = 'color' + token;

		// Counts give clean percentages (total 10) and group totals with no
		// ties, so the row order is deterministic for both breakdown orders

		const countsByCombo: Record<string, number> = {
			'P1|C1': 4,
			'P1|C2': 3,
			'P2|C1': 2,
			'P2|C2': 1,
		};

		const events = [];

		let userId = 1;

		for (const [combo, count] of Object.entries(countsByCombo)) {
			const [pageValue, colorValue] = combo.split('|');

			for (let i = 0; i < count; i++) {
				events.push({
					applicationId: 'CustomEvent',
					canonicalUrl: 'https://www.liferay.com',
					channelId: channel.id,
					eventDate: new Date().toISOString(),
					eventId,
					properties: [
						{name: pageAttribute, value: pageValue},
						{name: colorAttribute, value: colorValue},
					],
					title: 'Liferay',
					userId: String(userId++),
				});
			}
		}

		await apiHelpers.jsonWebServicesOSBAsah.createEvents(events);

		await apiHelpers.jsonWebServicesOSBAsah.createEventDefinition([
			{
				applicationId: 'CustomEvent',
				displayName: eventId,
				eventAttributeDefinitions: [pageAttribute, colorAttribute].map(
					(name) => ({
						dataType: 'STRING',
						displayName: name,
						name,
						type: 'LOCAL',
					})
				),
				name: eventId,
				type: 'CUSTOM',
			},
		]);

		await navigateToACPageViaURL({
			acPage: ACPage.eventAnalysisPage,
			channelID: channel.id,
			page,
			projectID: project.groupId,
		});

		await page.getByRole('link', {name: 'Create Analysis'}).click();

		await setEventAnalysisName({
			eventAnalysisName: `Event Analysis ${getRandomString()}`,
			page,
		});

		await addCustomEvent({customEventName: eventId, page});

		await addBreakdown({breakdownName: pageAttribute, page, tab: 'Event'});

		await addBreakdown({breakdownName: colorAttribute, page, tab: 'Event'});

		await changeTimeFilter({page, timeFilterPeriod: 'Last 24 hours'});

		const percentSequence = async () =>
			(await page.getByRole('cell').allInnerTexts())
				.map((text) => text.trim())
				.filter((text) => /^\d+%$/.test(text));

		// Grouped by page then color (page totals P1=7, P2=3)

		await expect
			.poll(percentSequence)
			.toEqual(['40%', '30%', '20%', '10%']);

		// Move the color breakdown ahead of the page breakdown

		const breakdownChips = page.locator(
			'.attribute-breakdown-section-root .attribute-chip-container'
		);

		await dragAndDropElement({
			dragTarget: breakdownChips.nth(1).locator('.drag-handle'),
			dropTarget: breakdownChips.nth(0),
		});

		// Grouped by color then page (color totals C1=6, C2=4): the rows re-sort

		await expect
			.poll(percentSequence)
			.toEqual(['40%', '20%', '30%', '10%']);
	}
);
