/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {deleteItems} from './utils/deleteItems';

const test = mergeTests(
	featureFlagsTest({
		'LPD-11235': {enabled: false},
	}),
	formsPagesTest,
	loginTest()
);

const xssDisabledTest = mergeTests(
	test,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPD-31212': {enabled: false},
	})
);

[test, xssDisabledTest].forEach((testSuite) => {
	testSuite.afterEach(async ({formsPage}) => {
		await formsPage.goTo();

		await deleteItems(formsPage);
	});
});

test(
	'Added "Rich Text" field includes preview of editor and is disabled',
	{
		tag: ['@LPD-11235'],
	},
	async ({formBuilderPage, formBuilderSidePanelPage, page}) => {
		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Rich Text');

		const editable = formBuilderSidePanelPage.page.getByRole('textbox', {
			name: 'Rich Text Editor',
		});

		await expect(editable).toBeVisible();
		await expect(editable).toHaveAttribute('contenteditable', 'false');

		const richTextField = page.locator('.ddm-field-container', {
			has: page.getByText('Rich Text', {exact: true}),
		});

		const ckEditor5Toolbar = richTextField.getByRole('toolbar', {
			name: 'Editor toolbar',
		});

		await expect(ckEditor5Toolbar).toBeVisible();

		const ckEditor5SourceButton = richTextField.getByRole('button', {
			name: 'Source',
		});

		await expect(ckEditor5SourceButton).toBeDisabled();
	}
);

test(
	'Added "Rich Text" field is focused when required and is empty on form submission',
	{
		tag: ['@LPD-76497'],
	},
	async ({formBuilderPage, formBuilderSidePanelPage, page}) => {
		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Rich Text');

		await formBuilderSidePanelPage.requiredFieldToggleSwitch.click();

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL);

		await page.getByRole('button', {name: 'Submit'}).click();

		const richTextEditor = page.getByRole('textbox', {
			name: 'Rich Text Editor. Editing area: main',
		});

		await expect(richTextEditor).toBeFocused();

		await expect(page.getByText('This field is required.')).toBeVisible();
	}
);

test(
	'"Rich Text" field does not replace focused field when changing language',
	{
		tag: ['@LPD-68018'],
	},
	async ({formBuilderPage, formBuilderSidePanelPage, page}) => {
		await test.step('Create new form', async () => {
			await formBuilderPage.goToNew();

			await expect(formBuilderPage.newFormHeading).toBeVisible();
		});

		await test.step('Add a "Text" field and "Rich Text" field', async () => {
			await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

			await formBuilderSidePanelPage.backButton.click();

			await formBuilderSidePanelPage.addFieldByDoubleClick('Rich Text');
		});

		await test.step('Focus on "Rich Text" field', async () => {
			await formBuilderPage.openFieldSettings('Rich Text');
		});

		await test.step('Go to settings advanced tab', async () => {
			await formBuilderSidePanelPage.advancedTab.click();
		});

		await test.step('Add a predefined value "english"', async () => {
			const richTextEditor = page
				.locator('[data-qa-id="predefinedValue"]')
				.locator('.ck-editor__editable');

			await richTextEditor.fill('english');
		});

		await test.step('Add new language "Spanish (Spain)"', async () => {
			await formBuilderPage.changeFormBuilderLanguage('Spanish (Spain)');
		});

		await test.step('Add a predefined value "spanish" to the "Rich Text" field', async () => {
			const richTextEditor = page
				.locator('[data-qa-id="predefinedValue"]')
				.locator('.ck-editor__editable');

			await richTextEditor.fill('spanish');
		});

		await test.step('Change language to "English"', async () => {
			await formBuilderPage.changeFormBuilderLanguage(
				'English (United States)'
			);
		});

		await test.step('Change focus to "Text" field', async () => {
			await formBuilderPage.openFieldSettings('Text');
		});

		await test.step('Change language to "Spanish (Spain)"', async () => {
			await formBuilderPage.changeFormBuilderLanguage('Spanish (Spain)');
		});

		await test.step('Check that "Text" field is still visible and "Rich Text" field appears once', async () => {
			await expect(
				page.locator('.ddm-field-container', {
					has: page.getByText('Text', {exact: true}),
				})
			).toBeVisible();

			await expect(
				page.locator('.ddm-field-container', {
					hasText: 'Rich Text',
				})
			).toHaveCount(1);
		});

		await test.step('Change language to "English (United States)"', async () => {
			await formBuilderPage.changeFormBuilderLanguage(
				'English (United States)'
			);
		});

		await test.step('Check that "Text" field is still visible and "Rich Text" field appears once', async () => {
			await expect(
				page.locator('.ddm-field-container', {
					has: page.getByText('Text', {exact: true}),
				})
			).toBeVisible();

			await expect(
				page.locator('.ddm-field-container', {
					hasText: 'Rich Text',
				})
			).toHaveCount(1);
		});
	}
);

test(
	'Repeated "Rich Text" field keeps the content of every instance on form submission',
	{
		tag: ['@LPD-115982'],
	},
	async ({formBuilderPage, formBuilderSidePanelPage, formsPage, page}) => {
		const firstContent = getRandomString();
		const formTitle = 'Form' + getRandomInt();
		const secondContent = getRandomString();

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		await formBuilderPage.fillFormTitle(formTitle);

		await formBuilderSidePanelPage.addFieldByDoubleClick('Rich Text');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.repeatableFieldToggleSwitch.click();

		await page.getByLabel('Add Duplicate Field').waitFor();

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL);

		const richTextEditors = page.getByRole('textbox', {
			name: 'Rich Text Editor. Editing area: main',
		});

		await richTextEditors.first().fill(firstContent);

		await page
			.getByRole('button', {name: 'Add Duplicate Field Rich Text'})
			.click();

		await expect(richTextEditors).toHaveCount(2);

		await richTextEditors.nth(1).fill(secondContent);

		await page.getByRole('button', {name: 'Submit'}).click();

		await expect(
			page.getByText(
				'Your information was successfully received. Thank you for filling out the form.'
			)
		).toBeVisible();

		await formsPage.goTo();

		await page
			.getByRole('row', {name: `Select ${formTitle}`})
			.getByLabel('Show Actions')
			.click();

		await page.getByRole('menuitem', {name: 'View Entries'}).click();

		await expect(
			page.getByRole('cell', {
				exact: true,
				name: `${firstContent}, ${secondContent}`,
			})
		).toBeVisible();
	}
);

const content = '<script>alert("Hello! I am an alert box!");</script>';
const sanitizedContent = '<p>alert("Hello! I am an alert box!");</p>';

const assertRichTextContent = async (content, expected, newTabPage) => {
	const sourceButton = newTabPage.getByRole('button', {
		name: 'Source',
	});

	await expect(sourceButton).toBeVisible();

	await sourceButton.click();

	await newTabPage.getByLabel('Source code editing area').fill(content);

	const input = newTabPage
		.locator(
			'xpath=//input[starts-with(@name, "_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_ddm$$RichText") and @type="hidden"]'
		)
		.first();

	await sourceButton.click();

	await expect(input).toHaveValue(expected);

	await newTabPage.close();
};

const createRichText = async (
	formBuilderPage,
	formBuilderSidePanelPage,
	helpText = 'help text'
) => {
	await formBuilderPage.goToNew();

	await expect(formBuilderPage.newFormHeading).toBeVisible();

	await formBuilderPage.fillFormTitle('Form' + getRandomInt());

	await formBuilderSidePanelPage.addFieldByDoubleClick('Rich Text');

	await formBuilderPage.helpText.fill(helpText);

	const formEntryPagePromise = new Promise<Page>((resolve) =>
		formBuilderPage.page.once('popup', resolve)
	);

	await formBuilderPage.previewButton.click();

	const formEntryPage = await formEntryPagePromise;

	await formEntryPage.waitForLoadState('domcontentloaded');

	return formEntryPage;
};

xssDisabledTest(
	'Can add help text to the rich text field @LPD-52535',
	async ({formBuilderPage, formBuilderSidePanelPage}) => {
		const helpText = getRandomString();

		const formEntryPage = await createRichText(
			formBuilderPage,
			formBuilderSidePanelPage,
			helpText
		);

		expect(formEntryPage.getByText(helpText, {exact: true})).toBeVisible();
	}
);

xssDisabledTest(
	'Can not add scripts to the rich text field @LPD-31212',
	async ({formBuilderPage, formBuilderSidePanelPage}) => {
		const formEntryPage = await createRichText(
			formBuilderPage,
			formBuilderSidePanelPage
		);

		await assertRichTextContent(content, sanitizedContent, formEntryPage);
	}
);
