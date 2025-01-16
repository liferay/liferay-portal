/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionApi,
	ObjectField,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {accountSettingsPagesTest} from '../../fixtures/accountSettingsPagesTest';
import {applicationsMenuPageTest} from '../../fixtures/applicationsMenuPageTest';
import {collectionsPagesTest} from '../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../fixtures/dataApiHelpersTest';
import {editObjectDefinitionPagesTest} from '../../fixtures/editObjectDefinitionPagesTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import {objectPagesTest} from '../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../fixtures/pageEditorPagesTest';
import {workflowPagesTest} from '../../fixtures/workflowPagesTest';
import {getRandomDouble} from '../../utils/getRandomDouble';
import {getRandomInt} from '../../utils/getRandomInt';
import {journalPagesTest} from '../journal-web/fixtures/journalPagesTest';
import {mockObjectFields} from './utils/mockObjectFields';

export const test = mergeTests(
	accountSettingsPagesTest,
	applicationsMenuPageTest,
	collectionsPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	editObjectDefinitionPagesTest,
	featureFlagsTest({
		'LPD-32050': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	journalPagesTest,
	loginTest(),
	objectPagesTest,
	pageEditorPagesTest,
	workflowPagesTest
);

let siteLanguage = 'en';

test.afterEach(async ({page}) => {
	if (siteLanguage !== 'en') {
		await page.goto('en');

		siteLanguage = 'en';
	}
});

test.describe('Localized object entries are saved correctly', () => {
	test('Boolean fields', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const {objectFields, titleObjectFieldName} = await mockObjectFields({
			apiHelpers,
			localizeAllLocalizable: true,
			objectEntryReturn: {format: 'API'},
			objectFieldBusinessTypes: ['boolean'],
			titleObjectFieldName: 'boolean',
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionApi);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				label: {
					en_US: objectDefinitionLabel,
				},
				name: objectDefinitionName,
				objectFields,
				pluralLabel: {
					en_US: objectDefinitionLabel,
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
				titleObjectFieldName,
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.addObjectEntryButton.click();

		const checkBox = page.getByRole('checkbox', {
			name: objectFields[0].label['en_US'],
		});

		await checkBox.check();

		const translationsDropdownTriggerButton = page
			.getByTestId('triggerButton')
			.first();

		await translationsDropdownTriggerButton.click();

		const catalanOption = page.getByTestId('availableLocalesDropdownca_ES');

		await catalanOption.click();

		await expect(checkBox).toBeChecked();

		await checkBox.uncheck();

		await translationsDropdownTriggerButton.click();

		await expect(catalanOption.locator('.label-item-expand')).toHaveText(
			'translated',
			{ignoreCase: true}
		);

		const englishOption = page.getByTestId('availableLocalesDropdownen_US');

		await expect(englishOption.locator('.label-item-expand')).toHaveText(
			'default',
			{ignoreCase: true}
		);

		await englishOption.click();

		await expect(checkBox).toBeChecked();

		await translationsDropdownTriggerButton.click();

		await catalanOption.click();

		await expect(checkBox).not.toBeChecked();

		const responsePromise = page.waitForResponse(
			`**${objectDefinition.restContextPath}`
		);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		const response = await responsePromise;

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		await page.getByRole('link', {name: 'Back'}).click();

		const responseBody = await response.json();

		const entryLink = page.getByRole('link', {name: responseBody.id});

		await entryLink.click();

		await expect(checkBox).toBeChecked();

		await translationsDropdownTriggerButton.click();

		await catalanOption.click();

		await expect(checkBox).not.toBeChecked();
	});

	test('Numeric fields', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const {objectFields, titleObjectFieldName} = await mockObjectFields({
			apiHelpers,
			localizeAllLocalizable: true,
			objectFieldBusinessTypes: [
				'decimal',
				'integer',
				'longInteger',
				'precisionDecimal',
			],
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionApi);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				label: {
					en_US: objectDefinitionLabel,
				},
				name: objectDefinitionName,
				objectFields,
				pluralLabel: {
					en_US: objectDefinitionLabel,
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
				titleObjectFieldName,
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.addObjectEntryButton.click();

		let englishValues: {[key: string]: string} = {};

		for (const {businessType, label, name} of objectFields) {
			if (
				businessType === ObjectField.BusinessTypeEnum.Decimal ||
				businessType === ObjectField.BusinessTypeEnum.PrecisionDecimal
			) {
				englishValues = {
					...englishValues,
					[`${name}`]: String(getRandomDouble()),
				};

				await page.getByLabel(label['en_US']).fill(englishValues[name]);
			}
			else {
				englishValues = {
					...englishValues,
					[`${name}`]: String(getRandomInt()).substring(0, 5),
				};

				await page.getByLabel(label['en_US']).fill(englishValues[name]);
			}
		}

		const translationsDropdownTriggerButton = page
			.getByTestId('triggerButton')
			.first();

		await translationsDropdownTriggerButton.click();

		const catalanOption = page.getByTestId('availableLocalesDropdownca_ES');

		await catalanOption.first().click();

		const responsePromise = page.waitForResponse(
			`**${objectDefinition.restContextPath}`
		);

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		const response = await responsePromise;

		await expect(
			page.getByText('Success:Your request completed successfully.')
		).toBeVisible();

		await page.getByRole('link', {name: 'Back'}).click();

		const responseBody = await response.json();

		const entryLink = page.getByRole('link', {name: responseBody.id});

		await entryLink.click();

		await translationsDropdownTriggerButton.click();

		await catalanOption.first().click();

		let catalanValues: {[key: string]: string} = {};

		for (const {businessType, label, name} of objectFields) {
			const input = page.getByLabel(label['en_US']);

			expect(
				(await input.inputValue()) ===
					englishValues[name].replace('.', ',')
			).toBeTruthy();

			if (
				businessType === ObjectField.BusinessTypeEnum.Decimal ||
				businessType === ObjectField.BusinessTypeEnum.PrecisionDecimal
			) {
				catalanValues = {
					...catalanValues,
					[`${name}`]: String(getRandomDouble()).replace('.', ','),
				};

				await input.fill(catalanValues[name]);
			}
			else {
				catalanValues = {
					...catalanValues,
					[`${name}`]: String(getRandomInt()).substring(0, 5),
				};

				await page.getByLabel(label['en_US']).fill(catalanValues[name]);
			}
		}

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await page.waitForTimeout(1000);

		await page.getByRole('link', {name: 'Back'}).click();

		await entryLink.click();

		for (const {label, name} of objectFields) {
			const input = page.getByLabel(label['en_US']);

			expect(
				(await input.inputValue()) === englishValues[name]
			).toBeTruthy();
		}

		await translationsDropdownTriggerButton.click();

		await catalanOption.first().click();

		for (const {label, name} of objectFields) {
			const inputValue = await page
				.getByLabel(label['en_US'])
				.inputValue();

			expect(inputValue === catalanValues[name]).toBeTruthy();
		}
	});
});
