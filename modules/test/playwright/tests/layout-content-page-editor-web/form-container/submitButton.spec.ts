/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import fillAndClickOutside from '../../../utils/fillAndClickOutside';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {getObjectERC} from '../../setup/page-management-site/main/utils/getObjectERC';
import getFormContainerDefinition from '../main/utils/getFormContainerDefinition';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
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

test(
	"Cannot save a value as draft in the object when 'Allow Users to Save Entries as Draft' option is not enabled",
	{tag: '@LPS-191474'},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a Content page with a form

		const formId = getRandomString();

		const formDefinition = getFormContainerDefinition({
			id: formId,
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([formDefinition]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		// Go to edit mode and map the form to Lemon Weight field

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		await pageEditorPage.mapFormFragment(formId, 'Lemon', ['Lemon Weight']);

		// Change the "Submitted Entry Status" configuration to Draft

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Submitted Entry Status',
			fragmentId: await pageEditorPage.getFragmentId('Form Button'),
			tab: 'General',
			value: 'Draft',
		});

		// Publish with a draft submit button

		await page.getByLabel('Publish', {exact: true}).click();

		await expect(
			page.getByText(
				'form does not allow creating entries as draft. Review the button configuration and set it to approved to generate valid entries.'
			)
		).toBeVisible();

		await page
			.locator('.modal')
			.getByText('Publish', {exact: true})
			.click();

		await waitForAlert(
			page,
			'Success:The page was published successfully.'
		);

		// Go to view mode and check that the value cannot be saved

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		await page.getByRole('spinbutton', {name: 'Lemon Weight'}).fill('200');

		await page.getByText('Submit', {exact: true}).click();

		await expect(
			page.getByText(
				'An error occurred while sending the form information.'
			)
		).toBeVisible();
	}
);

test(
	'It is not possible to change an object from approved status to draft status',
	{tag: '@LPS-191474'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		pageManagementSite,
	}) => {
		const getObjectEntry = async () => {
			const {items} =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					'c/drafts'
				);

			const item = items[0];

			return item;
		};

		// Create object definition

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableLocalization: true,
				enableObjectEntryDraft: true,
				externalReferenceCode: 'draftERC',
				label: {
					en_US: 'Draft',
				},
				name: 'Draft',
				objectFields: [
					{
						DBType: 'String',
						businessType: 'Text',
						externalReferenceCode: 'textERC',
						indexed: true,
						indexedAsKeyword: false,
						label: {
							en_US: 'Text',
						},
						localized: true,
						name: 'text',
						required: false,
					},
				],
				pluralLabel: {
					en_US: 'Drafts',
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

		const displayPageTemplateName = getRandomString();

		await test.step('Create a Display Page Template with a Form container mapped to Lemon object and two buttons, one to save as Draft and other to save as Approved', async () => {

			// Create a Display page for the Lemon object

			await displayPageTemplatesPage.goto(
				pageManagementSite.friendlyUrlPath
			);

			await displayPageTemplatesPage.createTemplate({
				contentType: 'Draft',
				name: displayPageTemplateName,
			});

			await displayPageTemplatesPage.editTemplate(
				displayPageTemplateName
			);

			// Add a Form Container and map it to Lemon Weight field

			await pageEditorPage.addFragment(
				'Form Components',
				'Form Container'
			);

			const fragmentId =
				await pageEditorPage.getFragmentId('Form Container');

			await pageEditorPage.mapFormFragment(fragmentId, 'Draft (Default)');

			// Add another submit button with the "Submitted Entry Status" configuration as Draft

			const dptSubmitButtonId =
				await pageEditorPage.getFragmentId('Form Button');

			await pageEditorPage.clickFragmentOption(
				dptSubmitButtonId,
				'Duplicate'
			);

			await page.locator('#banner.page-editor__disabled-area').click();

			await pageEditorPage.editTextEditable(
				dptSubmitButtonId,
				'submit-button-text',
				'Submit as draft'
			);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Submitted Entry Status',
				fragmentId: dptSubmitButtonId,
				tab: 'General',
				value: 'Draft',
			});

			await displayPageTemplatesPage.publishTemplate();
		});

		const headingId = getRandomString();
		const formId = getRandomString();
		let layout = null;

		await test.step('Create a Content page with a Form fragment mapped to Lemon object with a draft Submit Button and a Heading fragment', async () => {

			// Create a Content page

			const formDefinition = getFormContainerDefinition({
				id: formId,
			});

			const headingDefinition = getFragmentDefinition({
				id: headingId,
				key: 'BASIC_COMPONENT-heading',
			});

			layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					formDefinition,
					headingDefinition,
				]),
				siteId: pageManagementSite.id,
				title: getRandomString(),
			});

			// Go to edit mode

			await pageEditorPage.goto(
				layout,
				pageManagementSite.friendlyUrlPath
			);

			// Map the form to Draft object

			await pageEditorPage.mapFormFragment(formId, 'Draft');

			// Change the "Submitted Entry Status" configuration to Draft

			const submitButtonId =
				await pageEditorPage.getFragmentId('Form Button');

			await pageEditorPage.editTextEditable(
				submitButtonId,
				'submit-button-text',
				'Submit as draft'
			);

			await pageEditorPage.changeFragmentConfiguration({
				fieldLabel: 'Submitted Entry Status',
				fragmentId: submitButtonId,
				tab: 'General',
				value: 'Draft',
			});

			await pageEditorPage.publishPage();
		});

		const input = page.getByRole('textbox', {name: 'Text'});
		const submitDraftButton = page.getByText('Submit as draft', {
			exact: true,
		});

		await test.step('Go to view mode and save the Text field value as draft', async () => {
			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await fillAndClickOutside(page, input, '100');

			await submitDraftButton.click();

			await page
				.getByText(
					'Thank you. Your information was successfully received.'
				)
				.waitFor();
		});

		// Check the saved value

		const objectEntry = await getObjectEntry();

		expect(objectEntry.text).toBe('100');
		expect(objectEntry.status.label).toBe('draft');

		// Go to display page

		const {className: objectDefinitionClassName} = (
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				getObjectERC('Lemon')
			)
		).body;

		const className =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				objectDefinitionClassName
			);

		await test.step('Go to object display page and save the field value as Draft', async () => {
			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${objectEntry.id}`
			);

			// Set new value and submit as draft

			await fillAndClickOutside(page, input, '200');

			await submitDraftButton.click();

			await page
				.getByText(
					'Thank you. Your information was successfully received.'
				)
				.waitFor();

			// Check the saved value

			const updatedObjectEntry = await getObjectEntry();

			expect(updatedObjectEntry.text).toBe('200');
			expect(updatedObjectEntry.status.label).toBe('draft');
		});

		await test.step('Go to object display page and save the field value as Approved', async () => {
			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${objectEntry.id}`
			);

			// Set new value and submit as approved

			await fillAndClickOutside(page, input, '300');

			await page.getByText('Submit', {exact: true}).click();

			await page
				.getByText(
					'Thank you. Your information was successfully received.'
				)
				.waitFor();

			// Check the saved value

			const updatedObjectEntry = await getObjectEntry();

			expect(updatedObjectEntry.text).toBe('300');
			expect(updatedObjectEntry.status.label).toBe('approved');
		});

		await test.step('Go to view mode, click in the Heading and try to save the field value as Draft again', async () => {
			await page.goto(
				`/web${pageManagementSite.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${objectEntry.id}`
			);

			// Set new value and submit as draft

			await fillAndClickOutside(page, input, '400');

			await submitDraftButton.click();

			await expect(
				page.getByText(
					'An error occurred while sending the form information.'
				)
			).toBeVisible();

			// Check the saved value

			const updatedObjectEntry = await getObjectEntry();

			expect(updatedObjectEntry.text).toBe('300');
			expect(updatedObjectEntry.status.label).toBe('approved');
		});
	}
);
