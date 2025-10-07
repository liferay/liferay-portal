/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {formsPagesTest} from '../../../fixtures/formsPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {rolesPagesTest} from '../../../fixtures/rolesPagesTest';
import {FormFieldsPage} from '../../../pages/dynamic-data-mapping-form-web/FormFieldsPage';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {performLogout} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {generateObjectFields} from '../../object-web/main/utils/generateObjectFields';
import {deleteItems} from './utils/deleteItems';

export const test = mergeTests(
	dataApiHelpersTest,
	formsPagesTest,
	isolatedSiteTest,
	loginTest(),
	rolesPagesTest
);

let formPreviewPage: Page;

test.afterEach(async ({formsPage}) => {
	if (formPreviewPage) {
		await formPreviewPage.close();

		formPreviewPage = null;
	}

	await formsPage.goTo();

	await deleteItems(formsPage);
});

test.beforeEach(({page}) => {
	page.setViewportSize({height: 1080, width: 1920});
});

test.describe('Accessibility', () => {
	test('aria-describedby is applied for invalid multiple selection', async ({
		formBuilderPage,
		formBuilderSidePanelPage,
		formsPage,
		page,
	}) => {
		await formsPage.goTo();

		await formsPage.clickManagementToolbarNewButton();

		await formBuilderSidePanelPage.addMultipleSelectionButton.dblclick();

		await formBuilderSidePanelPage.requiredFieldToggleSwitch.click();

		await page.waitForTimeout(1000);

		await formBuilderPage.clickPublishFormButton();

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		await page.getByRole('button', {name: 'Submit'}).click();

		const singleSelect = page.getByRole('checkbox');

		await singleSelect.waitFor();

		const fieldFeedbackElement = page.locator('.field-feedback');

		await expect(fieldFeedbackElement).toBeVisible();

		await expect(fieldFeedbackElement).toHaveText(
			'This field is required.'
		);

		const fieldFeedbackId = await fieldFeedbackElement.getAttribute('id');

		await expect(singleSelect).toHaveAttribute(
			'aria-describedby',
			fieldFeedbackId
		);
	});
});

test('can interact with a large list of fields on the form entries page', async ({
	formBuilderPage,
	formBuilderSidePanelPage,
	formsPage,
	page,
}) => {
	test.slow();

	await formBuilderPage.goToNew();

	for (const index of Array.from(Array(30).keys())) {
		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.label.fill(`Text ${index}`);

		await formBuilderSidePanelPage.clickBackButton();
	}

	await formBuilderPage.clickPublishFormButton();

	const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

	await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

	const formEntry = getRandomString();

	await page.getByLabel('Text 29').fill(formEntry);

	await page.getByRole('button', {name: 'Submit'}).click();

	await expect(
		page.getByText(
			'Your information was successfully received. Thank you for filling out the form.'
		)
	).toBeVisible();

	await formsPage.goTo();

	await formsPage.openForm('Untitled Form');

	await formBuilderPage.entriesTab.click();

	await page.locator('a').filter({hasText: 'Text 29'}).click();

	await expect(page.getByText(formEntry)).toBeVisible();
});

test('can interact with Single Selection options using only keys', async ({
	formBuilderFieldSettingsSidePanelPage,
	formBuilderPage,
	formBuilderSidePanelPage,
	formsPage,
	page,
}) => {
	await formsPage.goTo();

	await formsPage.clickManagementToolbarNewButton();

	for (let index = 0; index < 2; index++) {
		await formBuilderSidePanelPage.addSingleSelectionButton.dblclick();

		await formBuilderFieldSettingsSidePanelPage.addOptions(3);

		await formBuilderFieldSettingsSidePanelPage.advancedTabButton.click();

		await formBuilderFieldSettingsSidePanelPage.inlineToggle.click();

		await formBuilderSidePanelPage.backButton.click();
	}

	const formPreviewPagePromise = page.waitForEvent('popup');

	await formBuilderPage.previewButton.click();

	formPreviewPage = await formPreviewPagePromise;

	// We need to ensure option elements are enabled before tabbing through them.

	await expect(formPreviewPage.getByLabel('Option0').first()).toBeEnabled();

	for (let index = 0; index < 2; index++) {

		// On the second loop iteration, pressing Tab should focus the second group.

		await formPreviewPage.keyboard.press('Tab');

		await expect(
			formPreviewPage.getByLabel('Option0').nth(index)
		).toBeChecked();

		await formPreviewPage.keyboard.press('ArrowDown');

		await expect(
			formPreviewPage.getByLabel('Option1').nth(index)
		).toBeChecked();

		await formPreviewPage.keyboard.press('ArrowDown');

		await expect(
			formPreviewPage.getByLabel('Option2').nth(index)
		).toBeChecked();

		await formPreviewPage.keyboard.press('ArrowDown');

		// After pressing Arrow Down while being in the last option,
		// we should have the first option selected again.

		await expect(
			formPreviewPage.getByLabel('Option0').nth(index)
		).toBeChecked();

		// Then, pressing Arrow Up twice should select the middle option.

		await formPreviewPage.keyboard.press('ArrowUp');

		await formPreviewPage.keyboard.press('ArrowUp');

		await expect(
			formPreviewPage.getByLabel('Option1').nth(index)
		).toBeChecked();
	}
});

test('can add image to repeated Rich Text field', async ({
	formBuilderFieldSettingsSidePanelPage,
	formBuilderPage,
	formBuilderSidePanelPage,
	formsPage,
	page,
}) => {
	await formsPage.goTo();

	await formsPage.clickManagementToolbarNewButton();

	await formBuilderSidePanelPage.addFieldByDoubleClick('Rich Text');

	await formBuilderFieldSettingsSidePanelPage.advancedTabButton.click();

	await formBuilderFieldSettingsSidePanelPage.repeatableToggle.click();

	const formPreviewPagePromise = page.waitForEvent('popup');

	await formBuilderPage.previewButton.click();

	formPreviewPage = await formPreviewPagePromise;

	const formFieldsPage = new FormFieldsPage(formPreviewPage);

	const editorContentFrame = formPreviewPage.frameLocator(
		'iframe[title="editor"]'
	);

	await formFieldsPage.repeatFieldButton.click();

	await formFieldsPage.richTextAddImageButton.nth(1).click();

	await formFieldsPage.richTextselectImage('planet.png');

	const textBox = editorContentFrame.nth(1).getByRole('textbox');

	await expect(
		textBox.locator('img[src="/documents/d/guest/planet-png"]')
	).toBeVisible();
});

test('can submit txt file as guest user in a mapped form', async ({
	apiHelpers,
	formBuilderPage,
	formBuilderSidePanelPage,
	formSettingsModalPage,
	formViewPage,
	page,
	roleDefinePermissionsPage,
	rolePage,
	rolesPage,
	site,
}) => {
	const objectFields = generateObjectFields({
		objectFieldBusinessTypes: [
			{
				businessType: 'Attachment',
				objectFieldSettings: [
					{
						name: 'acceptedFileExtensions',
						value: 'jpeg, jpg, pdf, png, txt',
					},
					{
						name: 'fileSource',
						value: 'userComputer',
					},
					{
						name: 'maximumFileSize',
						value: 0,
					},
				],
			},
		],
	});

	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFields,
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	await test.step('define permissions to add and view object entry for guest user', async () => {
		await rolesPage.goto();

		await rolesPage.rolesTable.search('Guest');

		await page.getByRole('link', {name: 'Guest'}).click();

		await rolePage.definePermissionsLink.click();

		await roleDefinePermissionsPage.searchInput.fill(objectDefinition.name);

		await expect(
			roleDefinePermissionsPage.menuItem(objectDefinition.name)
		).toBeVisible();

		await roleDefinePermissionsPage.menuItem(objectDefinition.name).click();

		await roleDefinePermissionsPage
			.selectAllCheckbox('Application Permissions')
			.click();

		await roleDefinePermissionsPage
			.selectAllCheckbox(objectDefinition.name)
			.first()
			.click();

		await roleDefinePermissionsPage
			.selectAllCheckbox(objectDefinition.name)
			.nth(1)
			.click();

		await roleDefinePermissionsPage.saveButton.click();

		await waitForAlert(page, 'Success:The role permissions were updated.');
	});

	let formSubmissionURL: string;
	let formTitle: string;

	await test.step('create new form with object definition as storage type and mapped to attachment field', async () => {
		await formBuilderPage.goToNew(site.friendlyUrlPath);

		await expect(formBuilderPage.newFormHeading).toBeVisible();

		formTitle = 'Form' + getRandomInt();

		await formBuilderPage.fillFormTitle(formTitle);

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Upload');

		await formBuilderSidePanelPage.allowGuestUsersToggle.check();

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(objectFields[0].name);

		await formBuilderPage.clickPublishFormButton();

		formSubmissionURL = await formBuilderPage.getFormSubmissionURL();
	});

	await test.step('logout and assert that it is possible to add txt file and that object entry was created', async () => {
		await performLogout(page);

		await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

		await formViewPage.uploadFile(page, __dirname, 'sampleFile.txt');

		await expect(
			page.getByText('Please enter a file with a valid extension')
		).not.toBeVisible();

		await expect(page.getByLabel('Upload')).toHaveValue('sampleFile.txt');

		await page.getByRole('button', {name: 'Save'}).click();

		await expect(
			page.getByText(
				'Your information was successfully received. Thank you for filling out the form.'
			)
		).toBeVisible();

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

		const item = items[0];

		const attachmentField = Object.keys(item).find((key) =>
			key.startsWith('attachment')
		);

		expect(item[attachmentField]?.name).toEqual('sampleFile.txt');
	});

	await performLogin(page, 'test');
});

test(
	'should delete only the entries returned by the search when "Select All Items on the Page" is checked',
	{tag: ['@LPD-58613']},
	async ({formBuilderPage, formBuilderSidePanelPage, formsPage, page}) => {
		const formTitle = 'Form' + getRandomInt();

		await test.step('publish form with a single text field', async () => {
			await formsPage.goTo();

			await formsPage.clickManagementToolbarNewButton();

			await formBuilderSidePanelPage.addTextButton.dblclick();

			await formBuilderPage.formTitle.fill(formTitle);

			await formBuilderPage.clickPublishFormButton();
		});

		await test.step('create two entries: one bad, one good', async () => {
			const formSubmissionURL =
				await formBuilderPage.getFormSubmissionURL();

			await page.goto(formSubmissionURL, {waitUntil: 'networkidle'});

			await page.getByLabel('Text').fill('Bad entry');

			await page.getByRole('button', {name: 'Submit'}).click();

			await waitForAlert(page);

			await page.getByRole('button', {name: 'Submit Again'}).click();

			await page.getByLabel('Text').fill('Good entry');

			await page.getByRole('button', {name: 'Submit'}).click();

			await waitForAlert(page);
		});

		await test.step('assert that only the entries returned by the search are deleted', async () => {
			await formsPage.goTo();

			await page
				.getByRole('row', {name: `Select ${formTitle}`})
				.getByLabel('Show Actions')
				.click();

			await page.getByRole('menuitem', {name: 'View Entries'}).click();

			await page.waitForTimeout(1000);

			await page.getByPlaceholder('Search for').fill('Bad');

			await page.getByLabel('Search for', {exact: true}).click();

			const badEntryLocator = page.getByRole('cell', {
				exact: true,
				name: 'Bad entry',
			});

			const goodEntryLocator = page.getByRole('cell', {
				exact: true,
				name: 'Good entry',
			});

			await expect(badEntryLocator).toBeVisible();

			await expect(goodEntryLocator).not.toBeVisible();

			await page.getByLabel('Select All Items on the Page').click();

			page.once('dialog', (dialog) => dialog.accept());

			await page.getByRole('button', {name: 'Delete'}).click();

			await page.getByLabel('Clear 0 Results for Bad').click();

			await expect(badEntryLocator).not.toBeVisible();

			await expect(goodEntryLocator).toBeVisible();
		});
	}
);
