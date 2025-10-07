/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';

// Sample object definitions to test nesting (Food > Fruit Basket > Fruit)

const FRUIT_ERC = getRandomString();
const FRUIT_BASKET_ERC = getRandomString();
const FOOD_ERC = getRandomString();

const FRUIT_DEFINITION: ObjectDefinition = {
	enableFriendlyURLCustomization: true,
	enableIndexSearch: true,
	enableLocalization: true,
	enableObjectEntryDraft: true,
	enableObjectEntrySchedule: true,
	enableObjectEntryVersioning: true,
	externalReferenceCode: FRUIT_ERC,
	label: {
		en_US: 'Fruit',
	},
	name: `Fruit${getRandomInt()}`,
	objectFields: [
		{
			DBType: 'String',
			businessType: 'Text',
			externalReferenceCode: getRandomString(),
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: 'en_US',
			label: {
				en_US: 'Fruit Title',
			},
			localized: true,
			name: 'fruitTitle',
			objectFieldSettings: [],
			required: true,
		},
	],
	objectRelationships: [],
	pluralLabel: {
		en_US: 'Fruit',
	},
	scope: 'company',
	status: {
		code: 0,
	},
	titleObjectFieldName: 'title',
};

const FRUIT_BASKET_DEFINITION: ObjectDefinition = {
	enableFriendlyURLCustomization: true,
	enableIndexSearch: true,
	enableLocalization: true,
	enableObjectEntryDraft: true,
	enableObjectEntrySchedule: true,
	enableObjectEntryVersioning: true,
	externalReferenceCode: FRUIT_BASKET_ERC,
	label: {
		en_US: 'Fruit Basket',
	},
	name: `FruitBasket${getRandomInt()}`,
	objectFields: [
		{
			DBType: 'String',
			businessType: 'Text',
			externalReferenceCode: getRandomString(),
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: 'en_US',
			label: {
				en_US: 'Fruit Basket Title',
			},
			localized: true,
			name: 'fruitBasketTitle',
			objectFieldSettings: [],
			required: true,
		},
	],
	objectRelationships: [
		{
			deletionType: 'cascade',
			label: {
				en_US: 'Fruit',
			},
			name: `name${getRandomInt()}`,
			objectDefinitionExternalReferenceCode1: FRUIT_BASKET_ERC,
			objectDefinitionExternalReferenceCode2: FRUIT_ERC,
			type: 'oneToMany',
		},
	],
	pluralLabel: {
		en_US: 'Fruit Basket',
	},
	scope: 'company',
	status: {
		code: 0,
	},
	titleObjectFieldName: 'title',
};

const FOOD_DEFINITION: ObjectDefinition = {
	enableFriendlyURLCustomization: true,
	enableIndexSearch: true,
	enableLocalization: true,
	enableObjectEntryDraft: true,
	enableObjectEntrySchedule: true,
	enableObjectEntryVersioning: true,
	externalReferenceCode: FOOD_ERC,
	label: {
		en_US: 'Food',
	},
	name: `Food${getRandomInt()}`,
	objectFields: [
		{
			DBType: 'String',
			businessType: 'Text',
			externalReferenceCode: getRandomString(),
			indexed: true,
			indexedAsKeyword: false,
			indexedLanguageId: 'en_US',
			label: {
				en_US: 'Food Title',
			},
			localized: true,
			name: 'foodTitle',
			objectFieldSettings: [],
			required: true,
		},
	],
	objectRelationships: [
		{
			deletionType: 'cascade',
			label: {
				en_US: 'FruitBasket',
			},
			name: `name${getRandomInt()}`,
			objectDefinitionExternalReferenceCode1: FOOD_ERC,
			objectDefinitionExternalReferenceCode2: FRUIT_BASKET_ERC,
			type: 'oneToMany',
		},
	],
	pluralLabel: {
		en_US: 'Food',
	},
	scope: 'company',
	status: {
		code: 0,
	},
	titleObjectFieldName: 'title',
};

const test = mergeTests(
	featureFlagsTest({
		'LPD-32050': {enabled: true},
		'LPD-50377': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	cmsPagesTest,
	dataApiHelpersTest,
	loginTest(),
	pageEditorPagesTest,
	isolatedLayoutTest({publish: false})
);

test(
	'Can use and configure a Form Relationship fragment',
	{tag: '@LPD-54414'},
	async ({apiHelpers, layout, page, pageEditorPage}) => {

		// Post required object definitions

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		for (const objectDefition of [
			FRUIT_DEFINITION,
			FRUIT_BASKET_DEFINITION,
			FOOD_DEFINITION,
		]) {
			const {body} =
				await objectDefinitionAPIClient.postObjectDefinition(
					objectDefition
				);

			apiHelpers.data.push({id: body.id, type: 'objectDefinition'});
		}

		// Create a page and go to edit mode

		await pageEditorPage.goto(layout);

		// Check Form Relationship fragment can't be added to Root

		await expect(async () => {
			await pageEditorPage.addFragment(
				'Form Components',
				'Form Relationship',
				page.getByText('Drag and drop fragments or widgets here')
			);
		}).not.toPass();

		// Add a Form Container, map it to Food and add a Form Relationship inside

		await pageEditorPage.addFragment('Form Components', 'Form Container');

		const formId = await pageEditorPage.getFragmentId('Form Container');

		await pageEditorPage.mapFormFragment(formId, 'Food', []);

		await pageEditorPage.addFragment(
			'Form Components',
			'Form Relationship',
			page.getByText('Drag and drop fragments or widgets here')
		);

		let formRelationshipId =
			await pageEditorPage.getFragmentId('Form Relationship');

		// Check button label config does not appear if fragment is not mapped

		await pageEditorPage.selectFragment(formRelationshipId);

		await expect(page.getByText('Form Relationship Options')).toBeVisible();

		await expect(page.getByText('Add Button Label')).not.toBeVisible();

		// Check Fruit Basket is the only option to map

		let select = page.getByLabel('Select a content type');
		let options = select.locator('option');

		await expect(options).toHaveCount(2);

		await expect(options.first()).toContainText('None');
		await expect(options.nth(1)).toContainText('Fruit Basket');

		await pageEditorPage.mapFormRelationshipFragment(
			formRelationshipId,
			'FruitBasket (Fruit Basket)'
		);

		// Add an input inside and check it can only be mapped to Fruit Basket Title

		await pageEditorPage.addFragment(
			'Form Components',
			'Inline Text',
			page.getByText('Drag and drop fragments or widgets here')
		);

		select = page.getByLabel('Field');
		options = select.locator('option');

		await expect(options).toHaveCount(2);

		await expect(options.first()).toContainText('Unmapped');
		await expect(options.nth(1)).toContainText('Fruit Basket Title');

		const inputId = await pageEditorPage.getFragmentId('Inline Text');

		await pageEditorPage.deleteFragment(inputId);

		// Now add another Form Relationship and check it can only be mapped to Fruit

		await pageEditorPage.addFragment(
			'Form Components',
			'Form Relationship',
			page.getByText('Drag and drop fragments or widgets here')
		);

		formRelationshipId = await pageEditorPage.getFragmentId(
			'Form Relationship',
			1
		);

		select = page.getByLabel('Select a content type');
		options = select.locator('option');

		await expect(options).toHaveCount(2);

		await expect(options.first()).toContainText('None');
		await expect(options.nth(1)).toHaveText('Fruit (Fruit)');

		await pageEditorPage.mapFormRelationshipFragment(
			formRelationshipId,
			'Fruit (Fruit)'
		);

		// Check correct value is shown in general panel select

		select = page
			.locator('.page-editor__item-configuration-sidebar')
			.getByLabel('Content Type', {exact: true});

		const selectedOption = select.locator('option:checked');

		await expect(selectedOption).toHaveText('Fruit (Fruit)');

		// Add an input inside and check it can only be mapped to Fruit Title

		await pageEditorPage.addFragment(
			'Form Components',
			'Inline Text',
			page.getByText('Drag and drop fragments or widgets here')
		);

		select = page.getByLabel('Field');
		options = select.locator('option');

		await expect(options).toHaveCount(2);

		await expect(options.first()).toContainText('Unmapped');
		await expect(options.nth(1)).toContainText('Fruit Title');
	}
);
