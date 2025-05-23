/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	featureFlagsTest({
		'LPD-11232': {enabled: true},
		'LPD-17564': {enabled: true},
	}),
	loginTest()
);

test('Add a new tag', {tag: '@LPD-51250'}, async ({tagsPage}) => {
	const tagName = await tagsPage.createTag();

	const tag = tagsPage.getItem(tagName);
	await expect(tag).toBeVisible();
});

test(
	'Save and add another tag',
	{tag: '@LPD-51250'},
	async ({page, tagsPage}) => {
		await tagsPage.goto();

		const name1 = `Tag${getRandomInt()}`;
		const name2 = `Tag${getRandomInt()}`;

		await tagsPage.newTagButton.click();

		await page.getByLabel('NameRequired').fill(name1);

		await tagsPage.saveAndAddAnotherButton.click();

		await page.getByLabel('NameRequired').fill(name2);

		await tagsPage.saveButton.click();

		const tag1 = tagsPage.getItem(name1);
		await expect(tag1).toBeVisible();

		const tag2 = tagsPage.getItem(name2);
		await expect(tag2).toBeVisible();
	}
);

test('Delete a tag', {tag: '@LPD-51252'}, async ({page, tagsPage}) => {
	const tagName = await tagsPage.createTag();

	await tagsPage.execItemAction({
		action: 'Delete',
		filter: tagName,
	});

	await expect(page.getByRole('heading', {name: `Delete Tag`})).toBeVisible();

	await clickAndExpectToBeVisible({
		target: page.getByText(`Success:${tagName} was deleted successfully.`),
		trigger: page.getByRole('button', {name: 'Delete'}),
	});

	await expect(tagsPage.getItem(tagName)).not.toBeVisible();
});

test('Edit an existing tag', {tag: '@LPD-52395'}, async ({page, tagsPage}) => {
	const tagName = await tagsPage.createTag();

	await tagsPage.execItemAction({
		action: 'Edit',
		filter: tagName,
	});

	await expect(page.getByText(`Edit "${tagName}"`)).toBeVisible();

	await expect(tagsPage.saveAndAddAnotherButton).not.toBeVisible();

	const newName = `Tag${getRandomInt()}`;

	await page.getByLabel('NameRequired').fill(newName);

	await clickAndExpectToBeVisible({
		target: page.getByText(`Success:${tagName} was updated successfully.`),
		trigger: tagsPage.saveButton,
	});

	const tag = tagsPage.getItem(newName);
	await expect(tag).toBeVisible();
});

test(
	'Create a new tag in a specific space',
	{tag: '@LPD-53874'},
	async ({page, tagsPage}) => {
		await tagsPage.goto();

		const name = `Tag${getRandomInt()}`;

		await tagsPage.newTagButton.click();

		await page.getByLabel('NameRequired').fill(name);

		await tagsPage.spaceCheckbox.uncheck();

		await page.getByLabel('Space Selector').click();

		await page.getByLabel('Default').last().click();

		await clickAndExpectToBeVisible({
			target: page.getByText(`Success:${name} was created successfully.`),
			trigger: tagsPage.saveButton,
		});

		const tag = tagsPage.getItem(name);
		await expect(tag).toBeVisible();

		await expect(
			page
				.locator('[data-testid="visualization-mode-table"]')
				.getByText('Default')
		).toBeVisible();
	}
);

test('Bulk Merge tags', {tag: '@LPD-43388'}, async ({page, tagsPage}) => {
	const tagName1 = await tagsPage.createTag();
	const tagName2 = await tagsPage.createTag();

	const tag1 = tagsPage.getItem(tagName1);
	const tag2 = tagsPage.getItem(tagName2);

	await expect(tag1).toBeVisible();
	await expect(tag2).toBeVisible();

	page.reload();

	await tagsPage.execItemAction({
		action: 'Merge',
		filter: tagName1,
	});

	await expect(page.getByText('Merge Tags')).toBeVisible();

	await clickAndExpectToBeVisible({
		target: page.getByText('Please choose at least 2 tags.'),
		trigger: tagsPage.saveButton,
	});

	await page.getByRole('button', {name: 'OK'}).click();

	await page.getByLabel('Select').click();

	await expect(
		page
			.locator('.fds table')
			.locator('tbody tr')
			.filter({hasText: tagName1})
	).toBeVisible();
	await expect(
		page
			.locator('.fds table')
			.locator('tbody tr')
			.filter({hasText: tagName2})
	).toBeVisible();

	await page.getByRole('row', {name: tagName1}).getByLabel('').click();
	await page.getByRole('row', {name: tagName2}).getByLabel('').click();

	await page.getByRole('button', {name: 'Done'}).click();

	await clickAndExpectToBeVisible({
		target: page.getByRole('heading', {name: 'Confirm Merge Tags'}),
		trigger: tagsPage.saveButton,
	});

	await page.getByRole('button', {name: 'OK'}).click();

	await expect(tag1).toBeVisible();
	await expect(tag2).not.toBeVisible();
});

test('Merge tags', {tag: '@LPD-43388'}, async ({page, tagsPage}) => {
	const tagName1 = await tagsPage.createTag();
	const tagName2 = await tagsPage.createTag();

	const tag1 = tagsPage.getItem(tagName1);
	const tag2 = tagsPage.getItem(tagName2);

	await expect(tag1).toBeVisible();
	await expect(tag2).toBeVisible();

	page.reload();

	await tagsPage.execItemAction({
		action: 'Merge',
		filter: tagName1,
	});

	await expect(page.getByText('Merge Tags')).toBeVisible();

	await expect(
		page.getByRole('gridcell', {exact: true, name: tagName1})
	).toBeVisible();

	await page.getByLabel('Merge Tags').getByRole('combobox').click();

	await page.getByRole('option', {name: tagName2}).click();

	await clickAndExpectToBeVisible({
		target: page.getByRole('heading', {name: 'Confirm Merge Tags'}),
		trigger: tagsPage.saveButton,
	});

	await clickAndExpectToBeVisible({
		target: page.getByText(
			`Success:${tagName2} and ${tagName1} have been successfully merged.`
		),
		trigger: page.getByRole('button', {name: 'OK'}),
	});

	await expect(tag1).toBeVisible();
	await expect(tag2).not.toBeVisible();
});
