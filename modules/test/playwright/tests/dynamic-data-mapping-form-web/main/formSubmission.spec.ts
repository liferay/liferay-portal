/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'path';

import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {virtualInstancesPagesTest} from '../../../fixtures/virtualInstancesPagesTest';
import {FormBuilderPage} from '../../../pages/dynamic-data-mapping-form-web/FormBuilderPage';
import {FormsPage} from '../../../pages/dynamic-data-mapping-form-web/FormsPage';
import {getRandomInt} from '../../../utils/getRandomInt';
import performLogin from '../../../utils/performLogin';
import {deleteItems} from './utils/deleteItems';

export const test = mergeTests(
	loginTest(),
	formsPagesTest,
	virtualInstancesPagesTest
);

const DEFAULT_VIRTUAL_INSTANCE_NAME = 'www.able.com';

const deleteAfterTestVirtualInstances = new Set<string>();
let hasDataProvider: boolean = false;

test.afterEach(async ({formsPage, virtualInstancesPage}) => {
	await formsPage.goTo();

	await deleteItems(formsPage);

	if (hasDataProvider) {
		await formsPage.page.waitForLoadState();

		await formsPage.dataProvidersTab.click();

		await deleteItems(formsPage);

		hasDataProvider = false;
	}

	for (const virtualInstanceName of deleteAfterTestVirtualInstances) {
		await virtualInstancesPage.deleteVirtualInstance(virtualInstanceName);

		deleteAfterTestVirtualInstances.delete(virtualInstanceName);
	}
});

test.describe('Manage forms through submission page', () => {
	test('assert that data provider works on virtual instance', async ({
		browser,
		virtualInstancesPage,
	}) => {
		test.slow();

		await virtualInstancesPage.addNewVirtualInstance(
			DEFAULT_VIRTUAL_INSTANCE_NAME
		);

		deleteAfterTestVirtualInstances.add(DEFAULT_VIRTUAL_INSTANCE_NAME);

		const virtualInstancePage = await browser.newPage({
			baseURL: `http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:8080`,
		});

		await performLogin(
			virtualInstancePage,
			'test',
			'?p_p_id=com_liferay_login_web_portlet_LoginPortlet&' +
				'p_p_state=maximized',
			`@${DEFAULT_VIRTUAL_INSTANCE_NAME}.com`
		);

		const virtualInstanceFormsPage = new FormsPage(virtualInstancePage);

		await virtualInstanceFormsPage.goTo();

		hasDataProvider = true;

		await virtualInstanceFormsPage.importForm(
			path.join(
				__dirname,
				'dependencies',
				'form-with-data-provider.portlet.lar'
			)
		);

		await virtualInstanceFormsPage.openForm('Form with data provider');

		const virtualInstanceFormBuilderPage = new FormBuilderPage(
			virtualInstancePage
		);

		await virtualInstanceFormBuilderPage.clickPublishFormButton();

		const formSubmissionURL =
			await virtualInstanceFormBuilderPage.getFormSubmissionURL();

		await virtualInstancePage.goto(formSubmissionURL, {
			waitUntil: 'networkidle',
		});

		await expect(virtualInstancePage.getByLabel('Country')).toHaveValue(
			'Brazil'
		);

		await virtualInstancePage.getByRole('button', {name: 'Submit'}).click();

		await expect(
			virtualInstancePage.getByText(
				'Your information was successfully received. Thank you for filling out the form.'
			)
		).toBeVisible();

		await virtualInstanceFormsPage.goTo();

		await virtualInstanceFormsPage.openForm('Form with data provider');

		await virtualInstanceFormBuilderPage.entriesTab.click();

		await expect(
			virtualInstancePage.getByText('Brazil', {exact: true})
		).toBeVisible();

		await virtualInstancePage.close();
	});

	test('can submit manual entry while using data provider autofill rule', async ({
		formBuilderPage,
		formsPage,
		page,
	}) => {
		hasDataProvider = true;

		await formsPage.goTo();

		await formsPage.importForm(
			path.join(
				__dirname,
				'dependencies',
				'form-with-data-provider.portlet.lar'
			)
		);

		await formsPage.openForm('Form with data provider');

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		await page.getByLabel('Country').fill('123456');

		await page.getByRole('button', {name: 'Submit'}).click();

		await expect(
			page.getByText(
				'Your information was successfully received. Thank you for filling out the form.'
			)
		).toBeVisible();

		await formsPage.goTo();

		await formsPage.openForm('Form with data provider');

		await formBuilderPage.entriesTab.click();

		await expect(page.getByText('123456')).toBeVisible();
	});

	test('verify that a Form can require CAPTCHA before being accessed', async ({
		formBuilderPage,
		formBuilderSidePanelPage,
		formsPage,
		page,
	}) => {
		await formsPage.goTo();

		await test.step('create a form containing a text field and CAPTCHA validation', async () => {
			await formsPage.newFormButton.first().click();

			await formBuilderPage.fillFormTitle('Form' + getRandomInt());

			await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

			await formBuilderPage.formSettingsButton.click();

			await formBuilderPage.requireCaptchaToggle.click();

			await formBuilderPage.formSettingsDoneButton.click();

			await formBuilderPage.clickPublishFormButton();
		});

		await test.step('navigate to the form page and assert that CAPTCHA is reiquired', async () => {
			const formSubmissionURL =
				await formBuilderPage.getFormSubmissionURL();

			await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

			await page.getByLabel('Text').fill('Text field value');

			await page.getByRole('textbox').last().fill('1');

			await page.getByRole('button', {name: 'Submit'}).click();

			await expect(
				page.getByText('Close Error:Text verification')
			).toBeVisible();
		});
	});
});
