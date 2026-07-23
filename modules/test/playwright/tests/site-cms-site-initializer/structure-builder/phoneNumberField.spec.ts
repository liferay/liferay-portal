/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from '../main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from './fixtures/structureBuilderPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	loginTest(),
	pageEditorPagesTest,
	structureBuilderPagesTest
);

test(
	'Phone field disambiguates +1 (US vs CA), supports fixed prefix, and respects customized fragment configuration',
	{tag: ['@LPD-91059', '@LPD-91575']},
	async ({contentsPage, page, pageEditorPage, structureBuilderPage}) => {
		const structureLabel = `Structure${getRandomInt()}`;
		const contentTitle = getRandomString();

		// Create structure with a Phone Number field

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			name: structureLabel,
			page: structureBuilderPage,
		});

		await structureBuilderPage.addField('Phone Number');

		await structureBuilderPage.changeFieldSettings({label: 'Phone'});

		const structureId = await structureBuilderPage.publishStructure();

		// Create content selecting United States and an unambiguous US area
		// code (212 = NYC)

		await contentsPage.goto();

		await contentsPage.createContent(structureLabel);

		await page.getByLabel('Title').fill(contentTitle);

		const phoneFragmentTrigger = page.getByLabel('Country Code');

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: /United States/}),
			trigger: phoneFragmentTrigger,
		});

		const phoneInput = page.locator('input[type="tel"]');

		// Typing letters is rejected — only digits, spaces, dashes,
		// parentheses and dots survive

		await phoneInput.pressSequentially('abc212-555 (1234)def');

		await expect(phoneInput).toHaveValue('212-555 (1234)');

		await phoneInput.fill('2125551234');

		await contentsPage.saveContent();

		// Edit content again — libphonenumber-js should resolve +12125551234
		// to US (not CA, which shares +1)

		await contentsPage.editContent(contentTitle);

		await expect(
			phoneFragmentTrigger.locator('.lexicon-icon-en-us')
		).toBeVisible();

		await contentsPage.saveContent();

		// While the user picks the country, the editor does not offer the "Show
		// Prefix" configuration because the prefix picker is mandatory

		await structureBuilderPage.editStructure(structureId);

		await structureBuilderPage.customizeEditor();

		await pageEditorPage.selectFragment(
			await pageEditorPage.getFragmentId('Phone Number')
		);

		await pageEditorPage.goToConfigurationTab('General');

		await expect(
			page
				.getByRole('tabpanel', {name: 'General'})
				.getByLabel('Show Country Flag', {exact: true})
		).toBeVisible();

		await expect(
			page
				.getByRole('tabpanel', {name: 'General'})
				.getByLabel('Show Prefix', {exact: true})
		).toBeHidden();

		await pageEditorPage.publishPage();

		// Switch the structure prefix to fixed +44 (UK)

		await structureBuilderPage.editStructure(structureId);

		await structureBuilderPage.selectFields([{label: 'Phone'}]);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'Fixed'}),
			trigger: page.getByLabel('Country Source'),
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: /United Kingdom/}),
			trigger: page.locator('.form-group-autofit button'),
		});

		await structureBuilderPage.publishStructure();

		// Edit content — the dropdown is replaced with a static +44 prefix
		// because the structure now enforces a fixed prefix

		await contentsPage.goto();

		await contentsPage.editContent(contentTitle);

		await expect(phoneFragmentTrigger).not.toBeVisible();

		await expect(page.getByText('+44', {exact: true})).toBeVisible();

		// Customize the editor — now that the prefix is fixed, the "Show Prefix"
		// configuration is offered; turning it off hides the static prefix

		await structureBuilderPage.editStructure(structureId);

		await structureBuilderPage.customizeEditor();

		const fragmentId = await pageEditorPage.getFragmentId('Phone Number');

		await pageEditorPage.changeFragmentConfiguration({
			fieldLabel: 'Show Prefix',
			fragmentId,
			tab: 'General',
			value: false,
		});

		await pageEditorPage.publishPage();

		// Edit content — the prefix is no longer rendered, only the bare phone
		// input remains

		await contentsPage.goto();

		await contentsPage.editContent(contentTitle);

		await expect(phoneFragmentTrigger).not.toBeVisible();

		await expect(page.getByText('+44', {exact: true})).not.toBeVisible();

		await expect(phoneInput).toBeVisible();

		// Cleanup

		await contentsPage.goto();

		await contentsPage.deleteContent(contentTitle);
	}
);

test(
	'Publishing a content with an invalid phone number shows a descriptive error, keeps the untouched field empty, and raises no success toast',
	{tag: '@LPD-94363'},
	async ({contentsPage, page, structureBuilderPage}) => {
		const structureLabel = `Structure${getRandomInt()}`;

		// Create a structure with two Phone Number fields

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			name: structureLabel,
			page: structureBuilderPage,
		});

		await structureBuilderPage.addField('Phone Number');

		await structureBuilderPage.addField('Phone Number');

		await structureBuilderPage.publishStructure();

		// Create a content, enter an invalid number in the first phone field and
		// leave the second one untouched

		await contentsPage.goto();

		await contentsPage.createContent(structureLabel);

		await page.getByLabel('Title').fill(getRandomString());

		const phoneInputs = page.locator('input[type="tel"]');

		await phoneInputs.first().fill('123');

		// Publish and check the error is descriptive instead of generic

		await clickAndExpectToBeVisible({
			target: page.getByText('Please enter a valid phone number.'),
			trigger: contentsPage.publishButton,
		});

		// The failed publish must not raise the optimistic success toast

		await expect(page.locator('.alert-success')).not.toBeVisible();

		// The untouched second field must stay empty instead of rendering "null"

		await expect(phoneInputs.nth(1)).toHaveValue('');
	}
);

test(
	'Phone prefix dropdown is not clipped when the form is nested in a grid',
	{tag: '@LPD-93856'},
	async ({contentsPage, page, pageEditorPage, structureBuilderPage}) => {
		const structureLabel = `Structure${getRandomInt()}`;

		// Create a structure with a Phone Number field

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			name: structureLabel,
			page: structureBuilderPage,
		});

		await structureBuilderPage.addField('Phone Number');

		const structureId = await structureBuilderPage.publishStructure();

		// Customize the editor, add a grid and move the Form Container into one
		// of its modules

		await structureBuilderPage.editStructure(structureId);

		await structureBuilderPage.customizeEditor();

		await pageEditorPage.addFragment('Layout Elements', 'Grid');

		// Move the Form Container into the first grid module

		await pageEditorPage.dragAndDropFragment({
			dragTarget: page.locator(
				'.page-editor__topper[data-name="Form Container"]'
			),
			dropTarget: page
				.locator(
					'.page-editor__topper[data-name="Grid"] .page-editor__col'
				)
				.first(),
			page,
		});

		await pageEditorPage.waitForChangesSaved();

		await expect(
			page
				.locator('.page-editor__topper[data-name="Grid"]')
				.locator('.page-editor__topper[data-name="Form Container"]')
		).toBeVisible();

		// Publish, confirming the form errors dialog raised by the unmapped
		// metadata fields of the default form

		await clickAndExpectToBeVisible({
			target: page.locator('.modal-footer', {hasText: 'Publish'}),
			timeout: 3000,
			trigger: pageEditorPage.publishButton,
		});

		await page.locator('.modal-footer').getByText('Publish').click();

		await waitForAlert(
			page,
			'Success:The editor was updated successfully.'
		);

		// Create content and open the country prefix dropdown

		await contentsPage.goto();

		await contentsPage.createContent(structureLabel);

		const prefixMenu = page.locator('.phone-number-input-prefix-menu');

		await expect(async () => {
			await page.getByLabel('Country Code').click({timeout: 3000});

			await expect(prefixMenu).toHaveClass(/show/, {timeout: 3000});
		}).toPass();

		// The dropdown must escape the grid's overflow clipping: every corner of
		// its bounding box has to resolve to the menu itself. Sampling only the
		// center would miss a menu clipped at its edges, which is exactly how the
		// overflow cut it off

		await expect(async () => {
			const fullyVisible = await prefixMenu.evaluate((menu) => {
				const rect = menu.getBoundingClientRect();
				const inset = 4;

				const corners = [
					[rect.left + inset, rect.top + inset],
					[rect.right - inset, rect.top + inset],
					[rect.left + inset, rect.bottom - inset],
					[rect.right - inset, rect.bottom - inset],
				];

				return corners.every(([x, y]) => {
					const point = document.elementFromPoint(
						Math.round(x),
						Math.round(y)
					);

					return Boolean(point) && menu.contains(point);
				});
			});

			expect(fullyVisible).toBe(true);
		}).toPass();
	}
);
