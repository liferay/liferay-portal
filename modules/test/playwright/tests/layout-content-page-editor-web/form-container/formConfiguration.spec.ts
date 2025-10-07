/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {getObjectERC} from '../../setup/page-management-site/main/utils/getObjectERC';
import getFormContainerDefinition from '../main/utils/getFormContainerDefinition';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';
import getWidgetDefinition from '../main/utils/getWidgetDefinition';

const test = mergeTests(
	apiHelpersTest,
	pageEditorPagesTest,
	pageManagementSiteTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
		'LPD-17564': {enabled: true},
		'LPD-21926': {enabled: true},
		'LPD-32050': {enabled: true},
		'LPD-60546': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	loginTest()
);

test(
	'Can add custom translations for success message',
	{
		tag: ['@LPS-155529', '@LPS-188036'],
	},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a page with a Form fragment and a widget

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Go to edit mode and change form configuration

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.mapFormFragment(formId, 'Lemon', [
			'Lemon Size',
			'Lemon Basket to Lemons',
		]);

		await pageEditorPage.selectFragment(formId);

		await page
			.locator('.panel', {hasText: 'Actions After Submit'})
			.getByLabel('Success Action', {exact: true})
			.selectOption({label: 'Show Embedded Message'});

		await pageEditorPage.switchLanguage('es-ES');

		await page
			.getByLabel('Embedded Message', {exact: true})
			.fill('Estamos muy agradecidos de recibir su formulario.');

		// Preview message

		await page.getByLabel('Preview Success Message', {exact: true}).click();

		await expect(
			page.getByText('Estamos muy agradecidos de recibir su formulario.')
		).toBeVisible();

		await pageEditorPage.publishPage();

		// Go to view mode in spanish language

		await page.goto(
			`/es-es/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Submit form

		await page.getByRole('button', {name: 'Submit'}).click();

		// Assert spanish success message

		await page
			.getByText('Estamos muy agradecidos de recibir su formulario.')
			.waitFor();

		// Go to view mode in english language

		await page.goto(
			`/en/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Submit form

		await page.getByRole('button', {name: 'Submit'}).click();

		// Assert english success message

		await page
			.getByText('Thank you. Your information was successfully received.')
			.waitFor();
	}
);

test(
	'Show success message only one time',
	{
		tag: ['@LPD-37435', '@LPS-188036'],
	},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a page with a Form fragment and a widget

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const widgetId = getRandomString();

		const widgetDefinition = getWidgetDefinition({
			id: widgetId,
			widgetName:
				'com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				formDefinition,
				widgetDefinition,
			]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Go to edit mode and change form configuration

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.mapFormFragment(formId, 'Lemon', [
			'Lemon Size',
			'Lemon Basket to Lemons',
			'Lemon Weight',
		]);

		await pageEditorPage.selectFragment(formId);

		await page
			.locator('.panel', {hasText: 'Actions After Submit'})
			.getByLabel('Success Action', {exact: true})
			.selectOption({label: 'Stay in Page'});

		await page
			.getByLabel('Show Notification After Submit', {exact: true})
			.check();

		await page
			.getByLabel('Success Notification Text', {exact: true})
			.fill('Request received correctly');

		// add a Tabs fragment that includes drop-zone

		await pageEditorPage.addFragment('Basic Components', 'Tabs');

		await pageEditorPage.publishPage();

		// Go to view mode and check picklist values

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		const lemonWeightInput = page.getByRole('spinbutton', {
			name: 'Lemon Weight',
		});

		await lemonWeightInput.fill('100');

		// Submit form

		await page.getByRole('button', {name: 'Submit'}).click();

		// Wait for the first alert

		await page.getByText('Request received correctly').waitFor();

		// Verify that the first alert disappears without any more alerts being displayed

		let moreAlertsAppear = false;
		let firstAlertDisappears = false;

		await expect(async () => {
			const alerts = await page
				.getByText('Request received correctly')
				.all();

			if (alerts.length > 1) {
				moreAlertsAppear = true;
			}
			else if (!alerts.length) {
				firstAlertDisappears = true;
			}

			expect(firstAlertDisappears).toBe(true);
			expect(moreAlertsAppear).toBe(false);
		}).toPass();
	}
);

test(
	'Success notification with toast message must be compatible with go to entry display page redirect option',
	{
		tag: '@LPS-188036',
	},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a default display page for lemon object

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {className: objectDefinitionClassName} = (
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				getObjectERC('Lemon')
			)
		).body;

		const className =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				objectDefinitionClassName
			);

		const displayPage =
			await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
				{
					classNameId: className.classNameId,
					groupId: pageManagementSite.id,
					name: getRandomString(),
				}
			);

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.markAsDefaultDisplayPageLayoutPageTemplateEntry(
			{
				layoutPageTemplateEntryId:
					displayPage.layoutPageTemplateEntryId,
			}
		);

		// Create a page with a form fragment

		const formId = getRandomString();

		const textDefinition = getFragmentDefinition({
			fragmentConfig: {
				inputFieldId: 'ObjectField_lemonSize',
			},
			id: getRandomString(),
			key: 'INPUTS-text-input',
		});

		const submitFragmentDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'INPUTS-submit-button',
		});

		const formDefinition = getFormContainerDefinition({
			id: formId,
			objectDefinitionClassName,
			pageElements: [textDefinition, submitFragmentDefinition],
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Go to edit mode and change form configuration

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Success Action',
			fragmentId: formId,
			tab: 'General',
			value: 'Go to Entry Display Page',
		});

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Display Page',
			fragmentId: formId,
			tab: 'General',
			value: 'Default',
		});

		await page
			.getByLabel('Show Notification After Submit', {exact: true})
			.check();

		await page
			.getByLabel('Success Notification Text', {exact: true})
			.fill('Request received correctly');

		await pageEditorPage.publishPage();

		// Go to view mode

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Assert form is not redirected if there are validation errors

		const input = page.getByRole('textbox', {name: 'Lemon Size'});

		await input.click();

		await page.keyboard.type('a'.repeat(290));

		await page.getByText('Submit', {exact: true}).click();

		await expect(
			page.getByText(
				'Value exceeds maximum length of 280 for field Lemon Size.'
			)
		).toBeVisible();

		// Clear input and assert success notification

		await input.clear();

		await page.getByText('Submit', {exact: true}).click();

		await waitForAlert(page, 'Request received correctly');

		// Delete the display page

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.deleteLayoutPageTemplateEntry(
			{
				layoutPageTemplateEntryId:
					displayPage.layoutPageTemplateEntryId,
			}
		);
	}
);

test(
	'Form Fragment redirect to correct success page',
	{tag: '@LPS-155529'},
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

		// Create a success page with a heading fragment

		const successLayoutTitle = getRandomString();

		const succesLayout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					fragmentFields: [
						{
							id: 'element-text',
							value: {
								text: {
									value_i18n: {
										en_US: 'Success Page',
									},
								},
							},
						},
					],
					id: getRandomString(),
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: pageManagementSite.id,
			title: successLayoutTitle,
		});

		// Go to edit mode and map the form to Lemon object

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.mapFormFragment(formId, 'Lemon', ['Lemon Weight']);

		// Change the success action to go to the success page

		await pageEditorPage.selectFragment(formId);

		await pageEditorPage.changeConfiguration({
			fieldLabel: 'Success Action',
			tab: 'General',
			value: 'Go to Page',
		});

		const layoutTreeItem = page
			.frameLocator('iframe[title="Select"]')
			.getByLabel(successLayoutTitle);

		await clickAndExpectToBeVisible({
			target: layoutTreeItem,
			timeout: 3000,
			trigger: page.getByLabel('Select Page', {exact: true}),
		});

		await clickAndExpectToBeHidden({
			target: page.locator('.modal-dialog'),
			trigger: layoutTreeItem,
		});

		await pageEditorPage.publishPage();

		// Go to view mode and submit form

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		const lemonWeightInput = page.getByRole('spinbutton', {
			name: 'Lemon Weight',
		});

		await lemonWeightInput.fill('100');

		await page.getByText('Submit', {exact: true}).click();

		// Assert that the success page is displayed

		await expect(page.getByText('Success Page')).toBeVisible();

		// Delete the success page

		await apiHelpers.jsonWebServicesLayout.deleteLayout(succesLayout.id);

		// Publish the form again and check that the default message is displayed

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await lemonWeightInput.fill('100');

		await page.getByText('Submit', {exact: true}).click();

		await expect(
			page.getByText(
				'Thank you. Your information was successfully received.'
			)
		).toBeVisible();
	}
);
