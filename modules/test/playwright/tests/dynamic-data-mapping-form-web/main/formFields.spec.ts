/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import performLoginViaApi, {performLogout} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {deleteItems} from './utils/deleteItems';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
	}),
	loginTest(),
	formsPagesTest
);

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

	test('Assert that it is possible to delete the decimal separator of a numeric field without removing the character before it', async ({
		formBuilderPage,
		formBuilderSidePanelPage,
		page,
	}) => {
		await formBuilderPage.goToNew();

		await formBuilderPage.fillFormTitle('Form' + getRandomInt());

		await formBuilderSidePanelPage.addFieldByDoubleClick('Numeric');

		await formBuilderSidePanelPage.numericTypeDecimal.check();

		await expect(formBuilderSidePanelPage.numericTypeDecimal).toBeChecked();

		await formBuilderSidePanelPage.advancedTab.click();

		await formBuilderSidePanelPage.inputMaskToggle.check();

		await expect(page.getByLabel('Thousands Separator')).toBeVisible();

		await formBuilderPage.saveButton.click();

		await waitForAlert(page);

		const newTabPage = await formBuilderPage.openPreviewForm();

		const numericInput = newTabPage.getByLabel('Numeric');

		await numericInput.fill('22.');

		await numericInput.click();

		await newTabPage.keyboard.press('Backspace');

		await expect(numericInput).toHaveValue('22');
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

	test('Duplicating fieldset with required fields only takes one click', async ({
		formBuilderPage,
		formBuilderSidePanelPage,
		page,
	}) => {
		await formBuilderPage.goToNew();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.requiredFieldToggleSwitch.click();

		await formBuilderSidePanelPage.clickAdvancedTab();

		const textFieldReference1 =
			await formBuilderSidePanelPage.getFieldReference();

		await formBuilderSidePanelPage.backButton.click();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.requiredFieldToggleSwitch.click();

		await formBuilderSidePanelPage.clickAdvancedTab();

		const textFieldReference2 =
			await formBuilderSidePanelPage.getFieldReference();

		await formBuilderSidePanelPage.dragAndDropField(
			textFieldReference2,
			textFieldReference1
		);

		await page
			.locator('label.text-uppercase', {hasText: 'Fields Group'})
			.click();

		await formBuilderSidePanelPage.clickBasicTab();

		await formBuilderSidePanelPage.repeatableFieldToggleSwitch.click();

		const newTabPage = await formBuilderPage.openPreviewForm();

		await newTabPage.getByRole('textbox').last().click();

		await newTabPage
			.getByRole('button', {
				name: 'Add Duplicate Field Fields Group',
			})
			.click();

		await expect(
			newTabPage.getByText('This field is required.')
		).toBeVisible();

		await expect(
			newTabPage.getByLabel('Fields Group', {exact: true})
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
	test('Assert actions on a fields group', async ({
		formBuilderPage,
		formBuilderSidePanelPage,
		page,
	}) => {
		await test.step('fields group can be created', async () => {
			await formBuilderPage.goToNew();

			await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

			await formBuilderSidePanelPage.backButton.click();

			await formBuilderSidePanelPage.addFieldToFieldGroup('Numeric', 0);

			await expect(
				page.getByLabel('Fields Group', {exact: true})
			).toBeVisible();
		});

		await test.step('fields group can be deleted', async () => {
			await page.getByLabel('Fields Group').first().click({force: true});

			await page.getByLabel('Actions').nth(0).click();

			await page.getByRole('menuitem', {name: 'Delete'}).click();

			await expect(
				page.getByLabel('Fields Group', {exact: true})
			).not.toBeVisible();
		});

		await test.step('recreate fields group', async () => {
			await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

			await formBuilderSidePanelPage.backButton.click();

			await formBuilderSidePanelPage.addFieldToFieldGroup('Numeric', 0);

			await expect(
				page.getByLabel('Fields Group', {exact: true})
			).toBeVisible();
		});

		await test.step('fields in a fieldGroup can be reordered', async () => {
			await formBuilderPage.openFieldSettings('Text');

			const textFieldReference =
				await formBuilderSidePanelPage.getFieldReference();

			const textContainer = page.locator(
				`.col-ddm:has(> .ddm-field-container[data-field-name="${textFieldReference}"])`
			);

			await formBuilderSidePanelPage.backButton.click();

			await formBuilderPage.openFieldSettings('Numeric');

			const numericFieldReference =
				await formBuilderSidePanelPage.getFieldReference();

			const numericContainer = page.locator(
				`.col-ddm:has(> .ddm-field-container[data-field-name="${numericFieldReference}"])`
			);

			await expect(numericContainer).toHaveAttribute(
				'data-ddm-field-row',
				'1'
			);
			await expect(textContainer).toHaveAttribute(
				'data-ddm-field-row',
				'0'
			);

			await page
				.locator('.ddm-drag')
				.nth(2)
				.dragTo(page.locator('.ddm-target').nth(2));

			await expect(numericContainer).toHaveAttribute(
				'data-ddm-field-row',
				'0'
			);
			await expect(textContainer).toHaveAttribute(
				'data-ddm-field-row',
				'1'
			);
		});

		await test.step('fieldGroup can be previewed', async () => {
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

			await newTabPage.close();
		});
	});

	test('Assert edition of a rich text field predefined value that contains a rule', async ({
		formBuilderPage,
		formBuilderSidePanelPage,
		page,
		rulesBuilderPage,
	}) => {
		let richTextPredefinedValueIframe: FrameLocator;

		await test.step('create a new form with a richText field and set a predefined value for it', async () => {
			await formBuilderPage.goToNew();

			await formBuilderSidePanelPage.addFieldByDoubleClick('Rich Text');

			await formBuilderPage.openFieldSettings('Rich Text');

			await formBuilderSidePanelPage.advancedTab.click();

			richTextPredefinedValueIframe = page
				.locator('[data-field-name="predefinedValue"]')
				.frameLocator('iframe');

			await richTextPredefinedValueIframe.getByRole('paragraph').click();

			await page.keyboard.type('Rich text predefined value');
		});

		await test.step('create a rule involving richText field', async () => {
			await rulesBuilderPage.rulesTab.click();

			await rulesBuilderPage.addElementsButton.click();

			await rulesBuilderPage.selectConditionLeftFormField('Rich Text');

			await rulesBuilderPage.selectConditionOperator('Is Empty');

			await rulesBuilderPage.selectAction('Require');

			await page.getByTitle('Choose an Option').click();

			await page.getByRole('option', {name: 'Rich Text'}).click();

			await rulesBuilderPage.saveButton.click();
		});

		await test.step('edit previous predefined value after adding the rule', async () => {
			await formBuilderPage.formTab.click();

			await expect(
				page.locator('.ddm-field .form-group label.ddm-label', {
					hasText: 'Rich Text',
				})
			).toBeVisible();

			await formBuilderPage.openFieldSettings('Rich Text');

			await formBuilderSidePanelPage.advancedTab.click();

			await richTextPredefinedValueIframe
				.getByText('Rich text predefined value')
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

	test(
		'Validates field group automatic deletion when the last field is dragged out, covering deletion of nested child groups and top-level groups',
		{tag: ['@LPD-70472', '@LPD-71315']},
		async ({formBuilderPage, formBuilderSidePanelPage, page}) => {
			let textFieldReference1;

			let textFieldReference2;

			let textFieldReference3;

			await test.step('go to form builder and create 3 text fields', async () => {
				await formBuilderPage.goToNew();

				await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

				await formBuilderPage.openFieldSettings('Text', 0);

				textFieldReference1 =
					await formBuilderSidePanelPage.getFieldReference();

				await formBuilderSidePanelPage.backButton.click();

				await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

				await formBuilderPage.openFieldSettings('Text', 1);

				textFieldReference2 =
					await formBuilderSidePanelPage.getFieldReference();

				await formBuilderSidePanelPage.backButton.click();

				await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

				await formBuilderPage.openFieldSettings('Text', 2);

				textFieldReference3 =
					await formBuilderSidePanelPage.getFieldReference();

				await formBuilderSidePanelPage.backButton.click();
			});

			await test.step('create the three-level nested field group structure', async () => {
				await formBuilderSidePanelPage.dragAndDropField(
					textFieldReference1,
					textFieldReference2
				);

				await formBuilderSidePanelPage.dragAndDropField(
					textFieldReference3,
					textFieldReference1
				);

				await formBuilderSidePanelPage.dragAndDropField(
					textFieldReference1,
					textFieldReference2
				);
			});

			await test.step('test case 1: drag the innermost field (textFieldReference3) to a field in a higher level to delete its parent group', async () => {
				await formBuilderSidePanelPage.dragAndDropField(
					textFieldReference3,
					textFieldReference2
				);

				const dropZoneTargets = page.locator('.ddm-target');

				await expect(dropZoneTargets).toHaveCount(16);
			});

			await test.step('test case 2: clean up field structure and drag the last remaining field out of its group to validate group deletion', async () => {
				await formBuilderPage.deleteField(textFieldReference1);

				await formBuilderPage.deleteField(textFieldReference2);

				const lastFieldsGroupLocator = page
					.locator(
						'.ddm-field-container[data-field-name^="Fieldset"]'
					)
					.last();

				const lastFieldsGroupName =
					await lastFieldsGroupLocator.evaluate(
						(element) => element.dataset.fieldName
					);

				await formBuilderSidePanelPage.dragAndDropField(
					textFieldReference3,
					4
				);

				await expect(
					page.locator(
						`.ddm-field-container[data-field-name="${lastFieldsGroupName}"]`
					)
				).toHaveCount(0);

				await expect(
					page.locator(
						`.ddm-field-container[data-field-name="${textFieldReference3}"]`
					)
				).toBeVisible();
			});
		}
	);
});
