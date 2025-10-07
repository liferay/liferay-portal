/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {masterPagesPagesTest} from '../../../fixtures/masterPagesPagesTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import getRandomString from '../../../utils/getRandomString';
import {getObjectERC} from '../../setup/page-management-site/main/utils/getObjectERC';
import getFormContainerDefinition from '../main/utils/getFormContainerDefinition';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	documentLibraryPagesTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-17564': {enabled: true},
		'LPD-21926': {enabled: true},
		'LPD-32050': {enabled: true},
		'LPD-60546': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	fragmentsPagesTest,
	isolatedSiteTest,
	loginTest(),
	masterPagesPagesTest,
	objectPagesTest,
	pageEditorPagesTest,
	pageManagementSiteTest
);

test(
	'Can see more than 10 options on dropdown menu of select from list',
	{
		tag: '@LPD-194759',
	},
	async ({apiHelpers, page, pageManagementSite}) => {

		// Create list type

		const listTypeDefinition =
			await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

		const countries = [
			'Argentina',
			'Brasil',
			'Canada',
			'France',
			'Germany',
			'Hungary',
			'Italy',
			'India',
			'Portugal',
			'Rusia',
			'Spain',
		];

		for (const country of countries) {
			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: country,
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				name_i18n: {en_US: country},
			});
		}

		// Create object definition

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				externalReferenceCode: 'plantERC',
				label: {
					en_US: 'Plant',
				},
				name: 'Plant',
				objectFields: [
					{
						DBType: 'String',
						businessType: 'Picklist',
						externalReferenceCode: 'countryERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Country',
						},
						listTypeDefinitionExternalReferenceCode:
							listTypeDefinition.externalReferenceCode,
						listTypeDefinitionId: listTypeDefinition.id,
						localized: false,
						name: 'country',
						required: false,
					},
				],
				pluralLabel: {
					en_US: 'Plants',
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		// Create a content page with form container

		const picklistId = getRandomString();

		const picklistDefinition = getFragmentDefinition({
			fragmentConfig: {
				inputFieldId: 'ObjectField_country',
			},
			id: picklistId,
			key: 'INPUTS-select-from-list',
		});

		const submitFragmentDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'INPUTS-submit-button',
		});

		const formDefinition = getFormContainerDefinition({
			id: getRandomString(),
			objectDefinitionClassName: objectDefinition.className,
			pageElements: [picklistDefinition, submitFragmentDefinition],
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Go to view mode and assert select options

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await page.getByPlaceholder('Choose an Option').click();

		for (const country of countries) {
			await expect(
				page.getByRole('option', {name: country})
			).toBeVisible();
		}
	}
);

test('Shows correct options in picklist field selected as title in related object', async ({
	apiHelpers,
	page,
	pageEditorPage,
	pageManagementSite,
}) => {

	// Create a page with a Form fragment

	const formId = getRandomString();

	const formDefinition = getFormContainerDefinition({
		id: formId,
	});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([formDefinition]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	// Go to edit mode

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	// Map the form to Lemon Basket object, select fields and publish

	await pageEditorPage.mapFormFragment(formId, 'Lemon', [
		'Lemon Size',
		'Lemon Basket to Lemons',
	]);

	await pageEditorPage.publishPage();

	// Go to view mode and check picklist values

	await page.goto(
		`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
	);

	await page.getByRole('combobox', {name: 'Lemon Basket to Lemons'}).click();

	await expect(page.getByText('Plastic', {exact: true})).toBeVisible();
	await expect(page.getByText('Carton', {exact: true})).toBeVisible();
});

test(
	'Checks changing the option when one default is selected set the value correctly',
	{
		tag: '@LPD-31856',
	},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a page with a Form fragment

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Go to edit mode and map it to the object

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.mapFormFragment(formId, 'Lemon Basket', [
			'Lemon Dimensions',
			'Material',
		]);

		// Publish and go to view mode

		await pageEditorPage.publishPage();

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Check that the value after selecting the item is correct

		const materialField = page.getByRole('combobox', {
			name: 'Material',
		});

		await materialField.click();

		await page.getByText('carton').click();

		await expect(materialField).toHaveValue('Carton');
	}
);

test(
	'The page designer map the Select from List fragment to objects fields on content pages',
	{
		tag: ['@LPS-151159', '@LPS-182728'],
	},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a page with a Form fragment

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {className: objectDefinitionClassName} = (
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				getObjectERC('Lemon Basket')
			)
		).body;

		const picklistId = getRandomString();

		const picklistDefinition = getFragmentDefinition({
			fragmentConfig: {
				inputFieldId: 'ObjectField_material',
			},
			id: picklistId,
			key: 'INPUTS-select-from-list',
		});

		const multiselectPicklistDefinition = getFragmentDefinition({
			fragmentConfig: {
				inputFieldId: 'ObjectField_lemonDimensions',
			},
			id: getRandomString(),
			key: 'INPUTS-select-from-list',
		});

		const submitFragmentDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'INPUTS-submit-button',
		});

		const formDefinition = getFormContainerDefinition({
			id: getRandomString(),
			objectDefinitionClassName,
			pageElements: [
				picklistDefinition,
				multiselectPicklistDefinition,
				submitFragmentDefinition,
			],
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Go to edit mode and map it to the object

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Change label and help text

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Label',
			fragmentId: picklistId,
			tab: 'General',
			value: 'Select your material',
		});

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Help Text',
			fragmentId: picklistId,
			tab: 'General',
			value: 'Just one material can be selected',
		});

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Show Help Text',
			fragmentId: picklistId,
			tab: 'General',
			value: true,
		});

		// Mark field as required

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Mark as Required',
			fragmentId: picklistId,
			tab: 'General',
			value: true,
		});

		// Publish and go to view mode

		await pageEditorPage.publishPage();

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Assert help text and label

		await expect(page.getByText('Select your material')).toBeVisible();

		await expect(
			page.getByText('Just one material can be selected')
		).toBeVisible();
	}
);

test(
	'Page designer can define number of options of multiselect fragment',
	{tag: ['@LPS-169936', '@LPS-182728']},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a page with a Form fragment

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Go to edit mode

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Map the form to object and select fields

		await pageEditorPage.mapFormFragment(formId, 'Lemon Basket', [
			'Lemon Dimensions',
		]);

		// Assert help text

		await expect(page.getByText('Lemon Dimensions')).toBeVisible();

		const inputId = await pageEditorPage.getFragmentId('Multiselect');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Show Help Text',
			fragmentId: inputId,
			tab: 'General',
			value: true,
		});

		await expect(page.getByText('Add your help text here.')).toBeVisible();

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Help Text',
			fragmentId: inputId,
			tab: 'General',
			value: 'Preferences',
		});

		await expect(page.getByText('Preferences')).toBeVisible();

		// Assert mandatory symbol

		await expect(
			page.locator('.custom-checkbox .lexicon-icon-asterisk')
		).toBeVisible();

		// Change number of options configuration

		await pageEditorPage.selectFragment(inputId);

		await expect(
			page.getByLabel('Number of Options', {exact: true})
		).toHaveValue('5');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Number of Options',
			fragmentId: inputId,
			tab: 'General',
			value: '2',
		});

		// Assert options in edit mode

		const multiselect = page.locator(
			'.custom-checkbox .custom-control-label'
		);

		await expect(multiselect.nth(0).getByText('Large')).toBeVisible();
		await expect(multiselect.nth(1).getByText('Medium')).toBeVisible();
		await expect(multiselect.nth(2).getByText('Small')).not.toBeVisible();

		await pageEditorPage.publishPage();

		// Assert options in view mode

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(multiselect.nth(0).getByText('Large')).toBeVisible();
		await expect(multiselect.nth(1).getByText('Medium')).toBeVisible();
		await expect(multiselect.nth(2).getByText('Small')).not.toBeVisible();

		// Click show all

		await page.getByRole('button', {name: 'Show All'}).click();

		await expect(multiselect.nth(0).getByText('Large')).toBeVisible();
		await expect(multiselect.nth(1).getByText('Medium')).toBeVisible();
		await expect(multiselect.nth(2).getByText('Small')).toBeVisible();

		// Go to edit mode and update show all options configuration

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Show All Options',
			fragmentId: inputId,
			tab: 'General',
			value: true,
		});

		// Assert options in edit mode

		await expect(multiselect.nth(0).getByText('Large')).toBeVisible();
		await expect(multiselect.nth(1).getByText('Medium')).toBeVisible();
		await expect(multiselect.nth(2).getByText('Small')).toBeVisible();

		await pageEditorPage.publishPage();

		// Assert options in view mode

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await expect(multiselect.nth(0).getByText('Large')).toBeVisible();
		await expect(multiselect.nth(1).getByText('Medium')).toBeVisible();
		await expect(multiselect.nth(2).getByText('Small')).toBeVisible();
	}
);

test.describe('Dropdown behavior with several object entries', () => {
	let lemonBaskets = [];

	test.beforeEach(() => {
		lemonBaskets = [];
	});

	test.afterEach(async ({apiHelpers}) => {
		for (const lemonBasket of lemonBaskets) {
			await apiHelpers.objectEntry.deleteObjectEntry(
				'c/lemonbaskets',
				lemonBasket.id
			);
		}
	});

	test(
		'Dropdown shows all results when there is no filter applied',
		{tag: ['@LPD-64860']},
		async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

			// Create lemon baskets

			for (let i = 0; i < 19; i++) {
				const lemonBasket =
					await apiHelpers.objectEntry.postObjectEntry(
						{
							lemonDimensions: ['large'],
							material: 'plastic',
						},
						'c/lemonbaskets',
						pageManagementSite.key
					);

				lemonBaskets.push(lemonBasket);
			}

			// Create a page with a Form fragment

			const formId = getRandomString();

			const formDefinition = getFormContainerDefinition({
				id: formId,
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([formDefinition]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Map the form to Lemon, select Lemon Basket to Lemons and publish

			await pageEditorPage.mapFormFragment(formId, 'Lemon', [
				'Lemon Basket to Lemons',
			]);

			await pageEditorPage.publishPage();

			// Go to view mode and check values

			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await page.getByPlaceholder('Choose an Option').click();

			await expect(page.getByRole('option')).toHaveCount(21);
		}
	);
});
