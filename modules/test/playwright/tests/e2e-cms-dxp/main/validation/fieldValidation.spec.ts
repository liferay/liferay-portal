/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from '../../../site-cms-site-initializer/structure-builder/fixtures/structureBuilderPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-17564': {enabled: true},
	}),
	loginTest(),
	structureBuilderPagesTest
);

test(
	'Publishing content is blocked when a mandatory structure field is left empty',
	{tag: ['@LPD-95548', '@LPD-95548/TC-23.a']},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		page,
		structureBuilderPage,
	}) => {
		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		const structureLabel = `Structure${getRandomString()}`;

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			page: structureBuilderPage,
			publish: false,
			spaces: [space.name],
		});

		await structureBuilderPage.addField('Text');

		await structureBuilderPage.selectFields([{label: 'Text'}]);

		await structureBuilderPage.changeFieldSettings({
			label: 'Subtitle',
			mandatory: true,
		});

		await structureBuilderPage.publishStructure();

		await test.step('Create a content leaving the mandatory Subtitle field empty and attempt to publish', async () => {
			await assetsPage.gotoContents(space.name);

			await contentsPage.createContent(structureLabel, space.name);

			await page
				.getByRole('textbox', {exact: true, name: 'Title'})
				.fill(getRandomString());

			await contentsPage.publishButton.click();
		});

		await test.step('A validation error appears on the empty mandatory field and the content is not published', async () => {
			const subtitleField = page.getByRole('textbox', {
				exact: true,
				name: 'Subtitle',
			});

			await expect(subtitleField).toBeFocused();

			expect(
				await subtitleField.evaluate(
					(element: HTMLInputElement) => element.validity.valueMissing
				)
			).toBe(true);

			await expect(
				page.getByRole('heading', {name: 'New'})
			).toBeVisible();
		});
	}
);

test(
	'Saving a Structured Content entry with a non-numeric value in a number field is blocked',
	{tag: ['@LPD-95548', '@LPD-95548/TC-23.c']},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		page,
		structureBuilderPage,
	}) => {
		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: `Space ${getRandomString()}`,
			type: 'Space',
		});

		const structureLabel = `Structure${getRandomString()}`;

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			page: structureBuilderPage,
			publish: false,
			spaces: [space.name],
		});

		await structureBuilderPage.addField('Numeric');

		await structureBuilderPage.selectFields([{label: 'Numeric'}]);

		await structureBuilderPage.changeFieldSettings({label: 'Quantity'});

		await structureBuilderPage.publishStructure();

		const quantityField = page.getByLabel('Quantity');

		await test.step('Create a content and enter a non-numeric value in the Quantity field', async () => {
			await assetsPage.gotoContents(space.name);

			await contentsPage.createContent(structureLabel, space.name);

			await page
				.getByRole('textbox', {exact: true, name: 'Title'})
				.fill(getRandomString());

			await quantityField.click();

			await page.keyboard.type('abc');

			await quantityField.blur();
		});

		await test.step('The field rejects the non-numeric input, so no invalid value can be saved', async () => {
			await expect(quantityField).toHaveValue('');

			expect(
				await quantityField.evaluate(
					(element: HTMLInputElement) => element.validity.badInput
				)
			).toBe(false);
		});
	}
);
