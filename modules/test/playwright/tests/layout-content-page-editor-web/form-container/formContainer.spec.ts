/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectFieldAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {getObjectERC} from '../../setup/page-management-site/main/utils/getObjectERC';
import getFormContainerDefinition from '../main/utils/getFormContainerDefinition';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';
import getWidgetDefinition from '../main/utils/getWidgetDefinition';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-17564': {enabled: true},
		'LPD-21926': {enabled: true},
		'LPD-32050': {enabled: true},
		'LPD-60546': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest
);

test('Allow selecting fields from main object and relationships in fields modal', async ({
	apiHelpers,
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

	// Map the form to Lemon object and select fields

	await pageEditorPage.mapFormFragment(formId, 'Lemon', [
		'Lemon Size',
		'Lemon Basket Color',
	]);

	const form = pageEditorPage.getFragment(formId);

	await expect(form.getByText('Lemon Size')).toBeVisible();
	await expect(form.getByText('Lemon Basket Color')).toBeVisible();
});

test('Input fragments show correct label, help text and placeholder when switching language', async ({
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

	const languageSelectorDefinition = getWidgetDefinition({
		id: getRandomString(),
		widgetName:
			'com_liferay_site_navigation_language_web_portlet_SiteNavigationLanguagePortlet',
	});

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			formDefinition,
			languageSelectorDefinition,
		]),
		siteId: pageManagementSite.id,
		title: getRandomString(),
	});

	// Go to edit mode

	await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

	await pageEditorPage.mapFormFragment(formId, 'Lemon', ['Lemon Size']);

	const fragmentId = await pageEditorPage.getFragmentId('Text');

	// Add translations to label, help text and placeholder

	await pageEditorPage.changeFragmentConfiguration({
		fieldLabel: 'Label',
		fragmentId,
		tab: 'General',
		value: 'English Label',
	});

	await pageEditorPage.changeFragmentConfiguration({
		fieldLabel: 'Help Text',
		fragmentId,
		tab: 'General',
		value: 'English Help Text',
	});

	await pageEditorPage.changeFragmentConfiguration({
		fieldLabel: 'Placeholder',
		fragmentId,
		tab: 'General',
		value: 'English Placeholder',
	});

	await pageEditorPage.switchLanguage('es-ES');

	await pageEditorPage.changeFragmentConfiguration({
		fieldLabel: 'Label',
		fragmentId,
		tab: 'General',
		value: 'Spanish Label',
	});

	await pageEditorPage.changeFragmentConfiguration({
		fieldLabel: 'Show Help Text',
		fragmentId,
		tab: 'General',
		value: true,
	});

	await pageEditorPage.changeFragmentConfiguration({
		fieldLabel: 'Help Text',
		fragmentId,
		tab: 'General',
		value: 'Spanish Help Text',
	});

	await pageEditorPage.changeFragmentConfiguration({
		fieldLabel: 'Placeholder',
		fragmentId,
		tab: 'General',
		value: 'Spanish Placeholder',
	});

	// Check that the translations are correctly displayed

	await pageEditorPage.switchLanguage('en-US');

	const englishLabel = page.getByText('English Label');
	const englishHelpText = page.getByText('English Help Text');
	const englishPlaceholder = page.getByPlaceholder('English Placeholder');

	const spanishLabel = page.getByText('Spanish Label');
	const spanishHelpText = page.getByText('Spanish Help Text');
	const spanishPlaceholder = page.getByPlaceholder('Spanish Placeholder');

	await expect(englishLabel).toBeVisible();
	await expect(englishHelpText).toBeVisible();
	await expect(englishPlaceholder).toBeVisible();

	await pageEditorPage.switchLanguage('es-ES');

	await expect(spanishLabel).toBeVisible();
	await expect(spanishHelpText).toBeVisible();
	await expect(spanishPlaceholder).toBeVisible();

	// Check the translations in the view mode

	await pageEditorPage.publishPage();

	await page.goto(
		`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
	);

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {name: 'español-España'}),
		trigger: page.getByTitle('Select a Language', {exact: true}),
	});

	await expect(spanishLabel).toBeVisible();
	await expect(spanishHelpText).toBeVisible();
	await expect(spanishPlaceholder).toBeVisible();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {name: 'english-United States'}),
		trigger: page.getByTitle('Seleccionar un idioma', {exact: true}),
	});

	await expect(englishLabel).toBeVisible();
	await expect(englishHelpText).toBeVisible();
	await expect(englishPlaceholder).toBeVisible();
});

test(
	'Check read-only fields',
	{tag: ['@LPD-44528']},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a new object definition with all fields

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				externalReferenceCode: 'readonly-object-erc',
				label: {
					en_US: 'Read Only Object',
				},
				name: 'ReadOnlyObject',
				objectFields: [
					{
						DBType: 'Boolean',
						externalReferenceCode: 'boolean-erc',
						indexed: true,
						indexedAsKeyword: true,
						label: {
							en_US: 'Boolean',
						},
						name: 'boolean',
					},
					{
						DBType: 'DateTime',
						externalReferenceCode: 'date-time-erc',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Date And Time',
						},
						name: 'dateAndTime',
						objectFieldSettings: [
							{
								name: 'timeStorage',
								value: {},
							},
						],
					},
					{
						DBType: 'Date',
						externalReferenceCode: 'date-erc',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Date',
						},
						name: 'date',
					},
					{
						DBType: 'Clob',
						externalReferenceCode: 'long-text-erc',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Long Text',
						},
						name: 'longText',
					},
					{
						DBType: 'Long',
						businessType: 'Attachment',
						externalReferenceCode: 'dl-file-upload-erc',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'DL File',
						},
						localized: false,
						name: 'dlFileUpload',
						objectFieldSettings: [
							{
								name: 'acceptedFileExtensions',
								value: 'pdf',
							},
							{
								name: 'maximumFileSize',
								value: 100,
							},
							{
								name: 'fileSource',
								value: 'documentsAndMedia',
							},
						] as any,
						type: 'Long',
					},
					{
						DBType: 'String',
						externalReferenceCode: 'text-erc',
						indexed: true,
						indexedAsKeyword: true,
						label: {
							en_US: 'Text',
						},
						name: 'text',
					},
					{
						DBType: 'Clob',
						businessType: 'RichText',
						externalReferenceCode: 'rich-text-erc',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Rich Text',
						},
						name: 'richText',
					},
					{
						DBType: 'String',
						businessType: 'Picklist',
						externalReferenceCode: 'picklist-erc',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Picklist',
						},
						listTypeDefinitionExternalReferenceCode:
							'lemon-dimensions-picklist-erc',
						name: 'picklist',
					},
					{
						DBType: 'String',
						businessType: 'MultiselectPicklist',
						externalReferenceCode: 'multiselect-picklist-erc',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'MultiSelect Picklist',
						},
						listTypeDefinitionExternalReferenceCode:
							'lemon-dimensions-picklist-erc',
						name: 'multiSelectPicklist',
					},
					{
						DBType: 'Integer',
						externalReferenceCode: 'numeric-erc',
						indexed: true,
						indexedAsKeyword: false,
						indexedLanguageId: '',
						label: {
							en_US: 'Numeric',
						},
						name: 'numeric',
					},
				],
				pluralLabel: {
					en_US: 'ReadOnlyObjects',
				},
				scope: 'company',
				status: {
					code: 0,
				},
			});

		// Set readOnly to true for all fields

		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);

		for (const objectField of objectDefinition.objectFields) {
			await objectFieldAPIClient.putObjectField(objectField.id, {
				...objectField,
				readOnly: 'true',
			});
		}

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		// Create a page with a form mapped to 'Read Only Object'

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.mapFormFragment(formId, 'Read Only Object');

		await pageEditorPage.publishPage();

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Check that all fields have the corresponding attribute and label

		const richTextToolbarButton = page
			.locator('.rich-text-input', {
				hasText: 'Rich Text (Read Only)',
			})
			.locator('.ck-button')
			.last();

		await expect(richTextToolbarButton).toBeDisabled();

		await expect(
			page.locator('.rich-text-input--disabled')
		).not.toBeAttached();

		[
			'Boolean (Read Only)',
			'Date (Read Only)',
			'Date And Time (Read Only)',
			'Long Text (Read Only)',
			'DL File (Read Only)',
			'Text (Read Only)',
			'Picklist (Read Only)',
			'Numeric (Read Only)',
		].forEach(async (label) => {
			await expect(page.getByLabel(label, {exact: true})).toHaveAttribute(
				'readonly',
				''
			);
		});

		(
			await page
				.getByLabel('MultiSelect Picklist (Read Only)')
				.locator('input')
				.all()
		).forEach(async (input) => {
			await expect(input).toHaveAttribute('readonly', '');
		});
	}
);

test(
	'Submitted entry status configuration is only visible if the form button is submit',
	{tag: '@LPD-37217'},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {
		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {className: objectDefinitionClassName} = (
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				getObjectERC('Lemon')
			)
		).body;

		const formId = getRandomString();

		const submitFragmentId = getRandomString();

		const submitFragmentDefinition = getFragmentDefinition({
			id: submitFragmentId,
			key: 'INPUTS-submit-button',
		});

		const formDefinition = getFormContainerDefinition({
			id: formId,
			objectDefinitionClassName,
			pageElements: [submitFragmentDefinition],
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.selectFragment(submitFragmentId);

		await expect(page.getByLabel('Type', {exact: true})).toHaveValue(
			'submit'
		);

		await expect(
			page.getByLabel('Submitted Entry Status', {exact: true})
		).toBeVisible();

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Type',
			fragmentId: submitFragmentId,
			tab: 'General',
			value: 'Next',
		});

		await expect(
			page.getByLabel('Submitted Entry Status')
		).not.toBeVisible();
	}
);
