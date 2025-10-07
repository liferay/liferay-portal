/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {deleteItems} from './utils/deleteItems';

export const test = mergeTests(
	applicationsMenuPageTest,
	dataApiHelpersTest,
	formsPagesTest,
	loginTest()
);

test.afterEach(async ({formsPage}) => {
	await formsPage.goTo();

	await deleteItems(formsPage);
});

test.describe('FormView when form storage type is object', () => {
	test.beforeEach(({page}) => {
		page.setViewportSize({height: 1080, width: 1920});
	});

	test('make sure the button submit label is Submit to workflow when the object definition has a linked workflow and Save when it does not', async ({
		apiHelpers,
		applicationsMenuPage,
		configurationTabPage,
		formBuilderPage,
		formBuilderSidePanelPage,
		formSettingsModalPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		const formTitle = 'Form' + getRandomInt();

		await formBuilderPage.fillFormTitle(formTitle);

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField('textField');

		await expect(formBuilderSidePanelPage.objectFieldSelect).toBeVisible();

		await apiHelpers.dynamicDataMapping.waitForDDMEvaluate(page);

		await formBuilderPage.clickSaveButton();

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		await expect(
			page.getByRole('button', {
				name: 'Save',
			})
		).toBeVisible();

		await page.goto('/');

		await applicationsMenuPage.goToProcessBuilder();

		await configurationTabPage.configurationTabLink.click();

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			objectDefinition.label['en_US']
		);

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		await expect(
			page.getByRole('button', {
				name: 'Submit for Workflow',
			})
		).toBeVisible();
	});
});
