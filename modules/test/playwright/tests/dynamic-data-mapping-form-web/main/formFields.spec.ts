/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import performLoginViaApi, {performLogout} from '../../../utils/performLogin';
import {deleteItems} from './utils/deleteItems';

export const test = mergeTests(dataApiHelpersTest, loginTest(), formsPagesTest);

test.afterEach(async ({formsPage}) => {
	await formsPage.goTo();

	await deleteItems(formsPage);
});

test.describe('Manage fields through Form Preview page', () => {
	test('Assert that it is possible to delete the predefined value of a text field', async ({
		formBuilderPage,
		formBuilderSidePanelPage,
	}) => {
		await formBuilderPage.goToNew();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.advancedTab.click();

		await formBuilderSidePanelPage.predefinedValueField.fill(
			'predefined value for text field.'
		);

		const newTabPage = await formBuilderPage.openPreviewForm();

		await newTabPage.getByLabel('Text').click();

		await newTabPage.keyboard.press('Control+A');

		await newTabPage.keyboard.press('Backspace');

		// Wait a little bit before doing the assertion since useSyncValue hook takes a few miliseconds to set the value on the text field
		// Otherwise the test would always pass, even with the bug still present

		await newTabPage.waitForTimeout(1000);

		await expect(newTabPage.getByLabel('Text')).toHaveValue('');

		await newTabPage.close();
	});

	test('Duplicating field with evaluation rules has correct behavior', async ({
		formBuilderPage,
		formBuilderSidePanelPage,
		page,
	}) => {
		await formBuilderPage.goToNew();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.label.fill('Text Field');

		await formBuilderSidePanelPage.requiredFieldToggleSwitch.click();

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.repeatableFieldToggleSwitch.click();

		await page.getByLabel('Add Duplicate Field').waitFor();

		const newTabPage = await formBuilderPage.openPreviewForm();

		await newTabPage
			.getByRole('textbox')
			.and(newTabPage.getByLabel('Text Field', {exact: true}))
			.click();

		await newTabPage
			.getByRole('button', {
				name: 'Add Duplicate Field Text Field',
			})
			.click();

		await expect(
			newTabPage.getByText('This field is required.')
		).toBeVisible();

		await expect(
			newTabPage.getByLabel('Text Field', {exact: true})
		).toHaveCount(2);
	});

	test(
		'HTML autocomplete attribute is rendered and has the configured value limited to 20 non-special characters in Date, Numeric and Text field types',
		{tag: ['@LPD-12824']},
		async ({formBuilderPage, formBuilderSidePanelPage}) => {
			const testData: {
				expectedValue: string;
				fieldTitle: FormFieldTypeTitle;
				inputValue: string;
			}[] = [
				{
					expectedValue: 'bday',
					fieldTitle: 'Date',
					inputValue: '+)(*&^%$#@ bday$__%  ',
				},
				{
					expectedValue: 'one-time-code',
					fieldTitle: 'Numeric',
					inputValue: '****[][one-time-code&&#()',
				},
				{
					expectedValue: 'transaction-currency',
					fieldTitle: 'Text',
					inputValue: 'transaction-currencyextracharacters',
				},
			];

			await formBuilderPage.goToNew();

			await expect(formBuilderPage.newFormHeading).toBeVisible();

			await formBuilderPage.fillFormTitle('Form' + getRandomInt());

			for (const data of testData) {
				await formBuilderSidePanelPage.addFieldByDoubleClick(
					data.fieldTitle
				);

				await formBuilderSidePanelPage.clickAdvancedTab();

				await expect(
					formBuilderSidePanelPage.htmlAutocompleteAttributeField
				).toBeVisible();

				await formBuilderSidePanelPage.htmlAutocompleteAttributeField.fill(
					data.inputValue
				);

				await formBuilderSidePanelPage.clickBackButton();
			}

			const newTabPage = await formBuilderPage.openPreviewForm();

			for (const data of testData) {
				if (data.fieldTitle === 'Date') {
					await expect(
						newTabPage.getByPlaceholder('__/__/____')
					).toHaveAttribute('autocomplete', data.expectedValue);

					continue;
				}

				await expect(
					newTabPage.getByLabel(data.fieldTitle)
				).toHaveAttribute('autocomplete', data.expectedValue);
			}

			await newTabPage.close();
		}
	);

	test('Make sure the aria-labelledby reference is present in the captcha form view', async ({
		formBuilderPage,
		formBuilderSidePanelPage,
	}) => {
		await formBuilderPage.goToNew();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderPage.formSettingsButton.click();

		await formBuilderPage.requireCaptchaToggle.click();

		await formBuilderPage.formSettingsDoneButton.click();

		const newTabPage = await formBuilderPage.openPreviewForm();

		const captchaContainer = newTabPage.locator(
			"[data-field-reference='_CAPTCHA_']"
		);

		await expect(captchaContainer).toBeVisible();

		const captchaContainerAriaLabelledby =
			await captchaContainer.getAttribute('aria-labelledby');

		const screenReaderOnlyCaptchaSpan = newTabPage.locator(
			`span[id='${captchaContainerAriaLabelledby}']`
		);

		await expect(screenReaderOnlyCaptchaSpan).toHaveClass('sr-only');

		await expect(screenReaderOnlyCaptchaSpan).toContainText('captcha');

		await newTabPage.close();
	});

	test('Verify boolean field aria-labelledby is only created when there is corresponding label rendered', async ({
		formBuilderPage,
		formBuilderSidePanelPage,
	}) => {
		await formBuilderPage.goToNew();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderSidePanelPage.addFieldByDoubleClick('Boolean');

		await formBuilderSidePanelPage.label.fill('Boolean without helptext');

		await formBuilderSidePanelPage.backButton.click();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Boolean');

		await formBuilderSidePanelPage.label.fill('Boolean with helptext');

		await formBuilderSidePanelPage.helpText.fill('Help text');

		await formBuilderSidePanelPage.backButton.click();

		const newTabPage = await formBuilderPage.openPreviewForm();

		const elementWithoutHelpText = newTabPage
			.locator('.form-group')
			.first();

		await expect(elementWithoutHelpText).not.toHaveAttribute(
			'aria-labelledby'
		);

		const elementWithHelpText = newTabPage.locator('.form-group').last();

		await expect(elementWithHelpText).toHaveAttribute('aria-labelledby');

		const helpTextLabelId =
			await elementWithHelpText.getAttribute('aria-labelledby');

		await expect(
			newTabPage.locator(`[id="${helpTextLabelId}"]`)
		).toBeVisible();

		await newTabPage.close();
	});

	test('Verify if temporary files are removed', async ({
		apiHelpers,
		formBuilderPage,
		formBuilderSidePanelPage,
		formViewPage,
		page,
	}) => {
		await formBuilderPage.goToNew();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderSidePanelPage.addFieldByDoubleClick('Upload');

		await formBuilderSidePanelPage.allowGuestUsersToggle.click();

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await performLogout(page);

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		await page.waitForLoadState('domcontentloaded');

		// Verify that the first file is removed after the second file is uploaded

		await formViewPage.uploadFile(page, __dirname, 'sampleFile.txt');

		await expect(formViewPage.uploadInput).toHaveValue('sampleFile.txt');

		const firstFileEntryId = await formViewPage.getFileEntryId(page);

		const getDocumentUnauthenticated = async (documentId: string) => {
			const {Authorization} =
				await apiHelpers.getJSONWebServicesHeaders();

			return apiHelpers.get(
				`${apiHelpers.baseUrl}headless-delivery/v1.0/documents/${documentId}`,
				false,
				{Authorization}
			);
		};

		expect(await getDocumentUnauthenticated(firstFileEntryId)).toEqual(
			expect.objectContaining({
				id: Number(firstFileEntryId),
			})
		);

		await formViewPage.uploadFile(page, __dirname, 'loremIpsum.txt');

		await expect(formViewPage.uploadInput).toHaveValue('loremIpsum.txt');

		expect(await getDocumentUnauthenticated(firstFileEntryId)).toEqual({
			status: 'NOT_FOUND',
		});

		// Verify that the file is removed when reloading the page

		const secondFileEntryId = await formViewPage.getFileEntryId(page);

		expect(await getDocumentUnauthenticated(secondFileEntryId)).toEqual(
			expect.objectContaining({
				id: Number(secondFileEntryId),
			})
		);

		await page.reload();

		expect(await getDocumentUnauthenticated(secondFileEntryId)).toEqual({
			status: 'NOT_FOUND',
		});

		// Verify that the file is removed when clearing the upload field

		await formViewPage.uploadFile(page, __dirname, 'sampleFile.txt');

		await expect(formViewPage.uploadInput).toHaveValue('sampleFile.txt');

		const thirdFileEntryId = await formViewPage.getFileEntryId(page);

		expect(await getDocumentUnauthenticated(thirdFileEntryId)).toEqual(
			expect.objectContaining({
				id: Number(thirdFileEntryId),
			})
		);

		await formViewPage.unselectFile.click();

		expect(await getDocumentUnauthenticated(thirdFileEntryId)).toEqual({
			status: 'NOT_FOUND',
		});

		await performLoginViaApi(page, 'test');
	});
});

test.describe('Manage fields through Form Builder page', () => {
	test('Assert edition of a rich text field predefined value that contains a rule', async ({
		formBuilderPage,
		formsPage,
		page,
	}) => {
		await formsPage.goTo();

		await formsPage.importForm(
			path.join(
				__dirname,
				'dependencies',
				'form-with-rich-text.portlet.lar'
			)
		);

		await formsPage.openForm('Form with rich text field');

		await expect(
			page.getByRole('textbox', {name: 'Rich Text'})
		).toBeVisible();

		await formBuilderPage.openFieldSettings('Rich Text');

		await formBuilderPage.settingsAdvancedTab.click();

		const richTextPredefinedValueIframe = page
			.getByRole('textbox', {name: 'Predefined Value'})
			.frameLocator('iframe');

		await richTextPredefinedValueIframe
			.getByText("Rich's text predefined value")
			.click();

		await page.keyboard.press('Control+A');

		await page.keyboard.press('Backspace');

		await page.keyboard.type(
			'Typing a new predefined value for the rich text field.'
		);

		await expect(
			richTextPredefinedValueIframe.getByText(
				'Typing a new predefined value for the rich text field.'
			)
		).toBeVisible();
	});

	test('Assert that a date field can be previewed', async ({
		formBuilderPage,
		formBuilderSidePanelPage,
	}) => {
		await formBuilderPage.goToNew();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Date');

		const newTabPage = await formBuilderPage.openPreviewForm();

		await expect(
			newTabPage.getByLabel('Date', {exact: true})
		).toBeVisible();

		await newTabPage.getByRole('button', {name: 'Select Date'}).click();

		await newTabPage.getByLabel('Select Current Date').click();

		await newTabPage.keyboard.press('Escape');

		const currentDate = new Date();

		const formattedDate = new Intl.DateTimeFormat('en-US', {
			day: '2-digit',
			month: '2-digit',
			year: 'numeric',
		}).format(currentDate);

		await expect(newTabPage.getByText(formattedDate)).toBeVisible();
	});

	test('Assert that a fields group can be previewed', async ({
		formBuilderPage,
		formBuilderSidePanelPage,
	}) => {
		await formBuilderPage.goToNew();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.backButton.click();

		await formBuilderSidePanelPage.addFieldToFieldGroup('Numeric', 0);

		const newTabPage = await formBuilderPage.openPreviewForm();

		await expect(
			newTabPage.getByLabel('Fields Group', {exact: true})
		).toBeVisible();

		await expect(
			newTabPage.getByLabel('Text', {exact: true})
		).toBeVisible();

		await expect(
			newTabPage.getByLabel('Numeric', {exact: true})
		).toBeVisible();
	});

	test('Can move the last field of a child group into the parent group field', async ({
		formBuilderPage,
		formBuilderSidePanelPage,
		page,
	}) => {
		await test.step('go to form builder and create structure with two levels of nesting and one field in each', async () => {
			await formBuilderPage.goToNew();

			await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

			await formBuilderSidePanelPage.backButton.click();

			await formBuilderSidePanelPage.addFieldToFieldGroup('Text', 0);

			await formBuilderSidePanelPage.backButton.click();

			await formBuilderSidePanelPage.addFieldToFieldGroup('Text', 2);

			await page.getByLabel('Actions').nth(4).click();

			await page.getByRole('menuitem', {name: 'Delete'}).click();
		});

		await test.step('drag field from child into the parent one to create new fieldGroup', async () => {
			await page
				.locator('.ddm-drag')
				.nth(3)
				.dragTo(page.locator('.ddm-drag').nth(1));
		});

		await expect(
			page.getByLabel('Fields Group', {exact: true})
		).toHaveCount(2);
	});

	test('Fields group can be translated and collapsed', async ({
		formBuilderPage,
		formBuilderSidePanelPage,
		page,
	}) => {
		let newTabPage: Page;
		const fieldsGroupLabels = {
			en_US: 'Contact Info',
			pt_BR: 'Informações de contato',
		};

		const numericFieldLabels = {
			en_US: 'Phone Number',
			pt_BR: 'Número de telefone',
		};

		const textFieldLabels = {
			en_US: 'Address',
			pt_BR: 'Endereço',
		};

		await test.step('Create a fields group with a numeric and a text field, changing their labels', async () => {
			await formBuilderPage.goToNew();

			await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

			await formBuilderSidePanelPage.label.fill(textFieldLabels['en_US']);

			await formBuilderSidePanelPage.backButton.click();

			await formBuilderSidePanelPage.addFieldToFieldGroup('Numeric', 0);

			await formBuilderSidePanelPage.label.fill(
				numericFieldLabels['en_US']
			);

			await page
				.locator('label')
				.filter({hasText: 'Fields Group'})
				.click();

			await formBuilderSidePanelPage.label.fill(
				fieldsGroupLabels['en_US']
			);
		});

		await test.step('Add pt-BR labels to the fields and make the fields group collapsible', async () => {
			await formBuilderPage.changeFormBuilderLanguage(
				'Portuguese (Brazil)'
			);

			await formBuilderSidePanelPage.label.fill(
				fieldsGroupLabels['pt_BR']
			);

			await formBuilderSidePanelPage.collapsibleToggleSwitch.check();

			await page.getByText(`Text${textFieldLabels['en_US']}`).click();

			await formBuilderSidePanelPage.label.fill(textFieldLabels['pt_BR']);

			await page
				.getByText(`Numeric${numericFieldLabels['en_US']}`)
				.click();

			await formBuilderSidePanelPage.label.fill(
				numericFieldLabels['pt_BR']
			);
		});

		await test.step('Go to the preview form tab', async () => {
			newTabPage = await formBuilderPage.openPreviewForm();
		});

		await test.step('Assert that the values for the default language labels are visible', async () => {
			await expect(
				newTabPage.getByLabel(fieldsGroupLabels['en_US'], {exact: true})
			).toBeVisible();

			await expect(
				newTabPage.getByLabel(textFieldLabels['en_US'], {exact: true})
			).toBeVisible();

			await expect(
				newTabPage.getByLabel(numericFieldLabels['en_US'], {
					exact: true,
				})
			).toBeVisible();
		});

		await test.step('Assert that the values for the default language labels are not visible after the fields group is collapsed', async () => {
			await newTabPage
				.getByRole('button', {name: fieldsGroupLabels['en_US']})
				.click();

			await expect(
				newTabPage.getByLabel(fieldsGroupLabels['en_US'], {exact: true})
			).toBeVisible();

			await expect(
				newTabPage.getByLabel(textFieldLabels['en_US'], {exact: true})
			).not.toBeVisible();

			await expect(
				newTabPage.getByLabel(numericFieldLabels['en_US'], {
					exact: true,
				})
			).not.toBeVisible();
		});

		await test.step('Assert that the values for the pt_BR labels are visible after changing the language', async () => {
			await newTabPage
				.getByRole('button', {name: 'Select a language, current'})
				.click();

			await newTabPage
				.getByRole('link', {name: 'português-Brasil'})
				.click();

			await expect(
				newTabPage.getByLabel(fieldsGroupLabels['pt_BR'], {exact: true})
			).toBeVisible();

			await expect(
				newTabPage.getByLabel(textFieldLabels['pt_BR'], {exact: true})
			).toBeVisible();

			await expect(
				newTabPage.getByLabel(numericFieldLabels['pt_BR'], {
					exact: true,
				})
			).toBeVisible();
		});

		await test.step('Assert that the values for the pt_BR labels are not visible after the fields group is collapsed', async () => {
			await newTabPage
				.getByRole('button', {name: fieldsGroupLabels['pt_BR']})
				.click();

			await expect(
				newTabPage.getByLabel(fieldsGroupLabels['pt_BR'], {exact: true})
			).toBeVisible();

			await expect(
				newTabPage.getByLabel(textFieldLabels['pt_BR'], {exact: true})
			).not.toBeVisible();

			await expect(
				newTabPage.getByLabel(numericFieldLabels['pt_BR'], {
					exact: true,
				})
			).not.toBeVisible();
		});
	});
});
