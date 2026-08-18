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

const baseTest = mergeTests(
	featureFlagsTest({
		'LPD-11235': {enabled: true},
	}),
	formsPagesTest,
	loginTest()
);

const xssBypassTest = mergeTests(
	baseTest,
	featureFlagsTest({
		'LPD-31212': {enabled: true},
	})
);

const xssDisabledTest = mergeTests(
	baseTest,
	featureFlagsTest({
		'LPD-31212': {enabled: false},
	})
);

[xssBypassTest, xssDisabledTest].forEach((testSuite) => {
	testSuite.afterEach(async ({formsPage}) => {
		await formsPage.goTo();

		await deleteItems(formsPage);
	});
});

baseTest(
	'Cannot see source button nor interact with the editor content when rich text field is read only',
	{tag: ['@LPD-55278']},
	async ({formBuilderPage, formBuilderSidePanelPage, formFieldsPage}) => {

		// Create and enter a new form

		await formBuilderPage.goToNew();

		// Add a rich text field

		await formBuilderSidePanelPage.addFieldByDoubleClick('Rich Text');

		// Rich Text Field Source Button Not Visible nor Editable

		await expect(formFieldsPage.richTextToolbar).toBeVisible();

		await expect(formFieldsPage.richTextSourceButton).toBeHidden();

		await expect(
			formFieldsPage.richTextFrame.locator('body')
		).toHaveAttribute('contenteditable', 'false');
	}
);

const content = '<script>alert("Hello! I am an alert box!");</script>';
const sanitizedContent = '<script>;</script>';

const assertRichTextContent = async (content, expected, newTabPage) => {
	const sourceButton = newTabPage.getByTitle('Source');

	await expect(sourceButton).toBeVisible();

	await sourceButton.click();

	const codeMirror = newTabPage.locator('.CodeMirror-scroll');

	await codeMirror.click();

	const textArea = newTabPage
		.getByLabel('Rich Text, help text')
		.getByRole('textbox');

	await textArea.fill(content);

	const input = newTabPage
		.locator(
			'xpath=//input[starts-with(@name, "_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_ddm$$RichText") and @type="hidden"]'
		)
		.first();

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

	const formEntryPagePromisse = new Promise<Page>((resolve) =>
		formBuilderPage.page.once('popup', resolve)
	);

	await formBuilderPage.previewButton.click();

	const formEntryPage = await formEntryPagePromisse;

	await formEntryPage.waitForLoadState('domcontentloaded');

	return formEntryPage;
};

baseTest(
	'Is focused in form submission when empty and required',
	{tag: ['@LPD-76497']},
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

		const richTextEditor = page
			.frameLocator('.cke_wysiwyg_frame')
			.getByRole('textbox');

		await expect(richTextEditor).toBeFocused();

		await expect(page.getByText('This field is required.')).toBeVisible();
	}
);

baseTest(
	'Names and marks each editor after its own field when a page has two of them',
	{tag: ['@LPD-34688']},
	async ({formBuilderPage, formBuilderSidePanelPage, page}) => {
		const optionalLabel = getRandomString();
		const requiredLabel = getRandomString();

		await formBuilderPage.goToNew();

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		// Add an optional rich text field, then a required one below it

		await formBuilderSidePanelPage.addFieldByDoubleClick('Rich Text');

		await formBuilderSidePanelPage.label.fill(optionalLabel);

		await formBuilderSidePanelPage.backButton.click();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Rich Text');

		await formBuilderSidePanelPage.label.fill(requiredLabel);

		await formBuilderSidePanelPage.requiredFieldToggleSwitch.click();

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL);

		// Each editable is named after the field it edits, so a wrong order
		// fails these lookups rather than passing silently

		const richTextFrames = page.frameLocator('iframe.cke_wysiwyg_frame');

		const optionalEditable = richTextFrames
			.first()
			.getByRole('textbox', {name: optionalLabel});

		const requiredEditable = richTextFrames
			.last()
			.getByRole('textbox', {name: requiredLabel});

		// Only the required field's own editable is announced as required

		await expect(requiredEditable).toHaveAttribute('aria-required', 'true');

		await expect(optionalEditable).not.toHaveAttribute('aria-required');
	}
);

xssBypassTest(
	'Can add scripts to the rich text field @LPD-31212',
	async ({formBuilderPage, formBuilderSidePanelPage}) => {
		const formEntryPage = await createRichText(
			formBuilderPage,
			formBuilderSidePanelPage
		);

		await assertRichTextContent(content, content, formEntryPage);
	}
);

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
