/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {applyFDSSelectionFilter} from '../../../../utils/applyFDSSelectionFilter';
import {checkAccessibility} from '../../../../utils/checkAccessibility';
import {clickAndExpectToBeHidden} from '../../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

test('Add a new tag', {tag: '@LPD-51250'}, async ({page, tagsPage}) => {
	const tagName = await tagsPage.createTag();

	const tag = tagsPage.getItem(tagName);

	await expect(tag).toBeVisible();

	// Check accessibility

	await checkAccessibility({
		page,
		selectors: ['.cms-section'],
	});

	await tagsPage.deleteTag(tagName);
});

test(
	'Save and add another tag',
	{tag: '@LPD-51250'},
	async ({page, tagsPage}) => {
		await page.emulateMedia({reducedMotion: 'reduce'});

		await tagsPage.goto();

		const name1 = `Tag${getRandomInt()}`;
		const name2 = `Tag${getRandomInt()}`;

		await clickAndExpectToBeVisible({
			target: page.locator('.modal-title', {
				hasText: 'New Tag',
			}),
			timeout: 2000,
			trigger: tagsPage.newTagButton,
		});

		// Check the accessibility of the modal

		await checkAccessibility({
			page,
			selectors: ['.modal-content'],
		});

		await page.getByLabel('NameRequired').fill(name1);

		await expect(async () => {
			await tagsPage.saveAndAddAnotherButton.click({timeout: 1000});

			await expect(page.getByLabel('NameRequired')).toBeEmpty({
				timeout: 1000,
			});
		}).toPass();

		await page.getByLabel('NameRequired').fill(name2);

		await clickAndExpectToBeHidden({
			target: page.locator('.modal-title', {
				hasText: 'New Tag',
			}),
			timeout: 2000,
			trigger: tagsPage.saveButton,
		});

		const tag1 = tagsPage.getItem(name1);

		await expect(tag1).toBeVisible();

		const tag2 = tagsPage.getItem(name2);

		await expect(tag2).toBeVisible();

		await tagsPage.deleteTag(name1);

		await tagsPage.deleteTag(name2);
	}
);

test('Delete a tag', {tag: '@LPD-51252'}, async ({tagsPage}) => {
	const tagName = await tagsPage.createTag();

	await tagsPage.deleteTag(tagName);
});

test('Edit an existing tag', {tag: '@LPD-52395'}, async ({page, tagsPage}) => {
	await page.emulateMedia({reducedMotion: 'reduce'});

	const tagName = await tagsPage.createTag();

	await tagsPage.execItemAction({
		action: 'Edit',
		filter: tagName,
	});

	await expect(page.getByText(`Edit "${tagName}"`)).toBeVisible();

	await expect(tagsPage.saveAndAddAnotherButton).not.toBeVisible();

	// Check the accessibility of the modal

	await checkAccessibility({
		page,
		selectors: ['.modal-content'],
	});

	const newName = `Tag${getRandomInt()}`;

	await page.getByLabel('NameRequired').fill(newName);

	await clickAndExpectToBeVisible({
		target: page.getByText(`Success:${tagName} was updated successfully.`),
		trigger: tagsPage.saveButton,
	});

	const tag = tagsPage.getItem(newName);

	await expect(tag).toBeVisible();

	await tagsPage.deleteTag(newName);
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

		await page.getByRole('option', {name: 'D Default'}).click();

		await clickAndExpectToBeVisible({
			target: page.getByText(`Success:${name} was created successfully.`),
			trigger: tagsPage.saveButton,
		});

		const tag = tagsPage.getItem(name);

		await expect(tag).toBeVisible();

		const tagRow = page
			.locator('.fds tbody tr')
			.filter({has: page.getByText(name)});

		await expect(tagRow.getByRole('cell', {name: 'Default'})).toBeVisible();

		await tagsPage.deleteTag(name);
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

	await expect(
		page
			.locator('.liferay-modal', {
				hasText: 'Please choose at least 2 tags.',
			})
			.locator('.modal-dialog')
	).toHaveClass(/modal-dialog-centered/);

	await page.getByRole('button', {name: 'OK'}).click();

	await page.getByLabel('Select', {exact: true}).click();

	await expect(
		page
			.locator('.modal')
			.locator('.fds table')
			.locator('tbody tr')
			.filter({hasText: tagName1})
	).toBeVisible();

	await expect(
		page
			.locator('.modal')
			.locator('.fds table')
			.locator('tbody tr')
			.filter({hasText: tagName2})
	).toBeVisible();

	await page
		.locator('.fds table')
		.getByRole('row', {name: tagName1})
		.getByLabel('')
		.click();
	await page
		.locator('.fds table')
		.getByRole('row', {name: tagName2})
		.getByLabel('')
		.click();

	await page.locator('.modal').getByRole('button', {name: 'Done'}).click();

	const targetTagSelect = page
		.locator('.form-group', {hasText: 'Into This Tag:'})
		.locator('select');

	await expect(
		targetTagSelect.locator('option', {hasText: tagName1})
	).toHaveCount(1);

	await expect(
		targetTagSelect.locator('option', {hasText: tagName2})
	).toHaveCount(1);

	await clickAndExpectToBeVisible({
		target: page.getByRole('heading', {name: 'Confirm Merge Tags'}),
		trigger: tagsPage.saveButton,
	});

	await page.getByRole('button', {name: 'Save'}).click();

	await expect(tag1).toBeVisible();
	await expect(tag2).not.toBeVisible();

	await tagsPage.deleteTag(tagName1);
});

test('Merge tags', {tag: '@LPD-43388'}, async ({page, tagsPage}) => {
	await page.emulateMedia({reducedMotion: 'reduce'});

	const tagName1 = await tagsPage.createTag();
	const tagName2 = await tagsPage.createTag();

	const tag1 = tagsPage.getItem(tagName1);
	const tag2 = tagsPage.getItem(tagName2);

	await expect(tag1).toBeVisible();
	await expect(tag2).toBeVisible();

	await tagsPage.execItemAction({
		action: 'Merge',
		filter: tagName1,
	});

	await expect(page.getByText('Merge Tags')).toBeVisible();

	// Check accessibility

	await checkAccessibility({
		page,
		selectors: ['.modal-content'],
	});

	await expect(
		page.getByRole('gridcell', {exact: true, name: tagName1})
	).toBeVisible();

	await page.getByLabel('Merge Tags').getByRole('combobox').nth(0).click();

	await expect(async () => {
		await page.getByRole('option', {name: tagName2}).click();

		await expect(
			page.locator('.label-secondary', {hasText: tagName2})
		).toBeVisible();
	}).toPass();

	const targetTagSelect = page
		.locator('.form-group', {hasText: 'Into This Tag:'})
		.locator('select');

	await expect(
		targetTagSelect.locator('option', {hasText: tagName1})
	).toHaveCount(1);

	await expect(
		targetTagSelect.locator('option', {hasText: tagName2})
	).toHaveCount(1);

	await clickAndExpectToBeVisible({
		target: page.getByRole('heading', {name: 'Confirm Merge Tags'}),
		trigger: tagsPage.saveButton,
	});

	await expect(
		page
			.locator('.liferay-modal', {
				has: page.getByRole('heading', {name: 'Confirm Merge Tags'}),
			})
			.locator('.modal-dialog')
	).toHaveClass(/modal-dialog-centered/);

	await clickAndExpectToBeVisible({
		target: page.getByText(
			`Success:${tagName2} and ${tagName1} have been successfully merged.`
		),
		trigger: page.getByRole('button', {name: 'Save'}),
	});

	await expect(tag1).toBeVisible();
	await expect(tag2).not.toBeVisible();

	await tagsPage.deleteTag(tagName1);
});

test(
	'Validate that a UI error appears when attempting to create or edit a tag with an existing name',
	{tag: ['@LPD-57497', '@LPD-92349']},
	async ({page, tagsPage}) => {
		const name1 = await tagsPage.createTag();

		const tag1 = tagsPage.getItem(name1);

		await expect(tag1).toBeVisible();

		await tagsPage.newTagButton.click();

		await page.getByLabel('NameRequired').fill(name1);

		await clickAndExpectToBeVisible({
			target: page
				.locator('.modal-body')
				.getByText(
					'Please enter a unique name. This one is already in use.'
				),
			trigger: tagsPage.saveButton,
		});

		await clickAndExpectToBeHidden({
			target: page
				.locator('.modal-body')
				.getByText(
					'Please enter a unique name. This one is already in use.'
				),
			trigger: page.getByText('Cancel'),
		});

		// Repeat test for attempting to edit tag since the edit and create modals are separate components

		const name2 = await tagsPage.createTag();

		const tag2 = tagsPage.getItem(name2);
		await expect(tag2).toBeVisible();

		await tagsPage.execItemAction({
			action: 'Edit',
			filter: name2,
		});

		await expect(page.getByText(`Edit "${name2}"`)).toBeVisible();

		await expect(tagsPage.saveAndAddAnotherButton).not.toBeVisible();

		await page.getByLabel('NameRequired').fill(name1);

		await clickAndExpectToBeVisible({
			target: page
				.locator('.modal-body')
				.getByText(
					'Please enter a unique name. This one is already in use.'
				),
			trigger: tagsPage.saveButton,
		});

		await clickAndExpectToBeHidden({
			target: page
				.locator('.modal-body')
				.getByText(
					'Please enter a unique name. This one is already in use.'
				),
			trigger: page.getByText('Cancel'),
		});

		await tagsPage.deleteTag(name1);

		await tagsPage.deleteTag(name2);
	}
);

test(
	'UI error appears when attempting to create a tag with an invalid character',
	{tag: '@LPD-69332'},
	async ({page, tagsPage}) => {
		await tagsPage.goto();

		await tagsPage.newTagButton.click();

		await page.getByLabel('NameRequired').fill('<Tag>');

		await clickAndExpectToBeVisible({
			target: page.getByText(
				'Name cannot contain the following invalid characters:'
			),
			trigger: tagsPage.saveButton,
		});

		await clickAndExpectToBeHidden({
			target: page
				.locator('.modal-body')
				.getByText(
					'Name cannot contain the following invalid characters:'
				),
			trigger: page.getByText('Cancel'),
		});

		await expect(tagsPage.getItem('<Tag>')).not.toBeVisible();
	}
);

test(
	"View a Tag's usages",
	{tag: '@LPD-89713'},
	async ({apiHelpers, dataSetPage, page, tagsPage}) => {
		const {id: siteId} =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('cms');

		const tagName = getRandomString();

		await apiHelpers.headlessAdminTaxonomy.postSiteKeyword({
			name: tagName,
			siteId,
		});

		await tagsPage.goto();

		await tagsPage.execItemAction({
			action: 'View Usages',
			filter: tagName,
		});

		await expect(page.getByText('No Results Found')).toBeVisible();

		const basicWebContentTitle = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{
				keywords: [tagName],
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: basicWebContentTitle,
			},
			'cms/basic-web-contents/scopes/Default'
		);

		await tagsPage.goto();

		await tagsPage.execItemAction({
			action: 'View Usages',
			filter: tagName,
		});

		await checkAccessibility({
			page,
			selectors: ['.content'],
			selectorsToExclude: [
				'.control-menu-container',
				'.sidebar-container',
				'.top-bar',
			],
		});

		await expect(dataSetPage.getRow(basicWebContentTitle)).toBeVisible();
	}
);

test('Validate tag inputs', {tag: ['@LPD-69687']}, async ({page, tagsPage}) => {
	await tagsPage.goto();

	await clickAndExpectToBeVisible({
		target: page.locator('.modal-title', {
			hasText: 'New Tag',
		}),
		timeout: 2000,
		trigger: tagsPage.newTagButton,
	});

	// Check we can't publish an empty name

	await expect(tagsPage.saveButton).toBeDisabled();

	await expect(tagsPage.saveAndAddAnotherButton).toBeDisabled();

	await page.getByLabel('NameRequired').fill('');

	await clickAndExpectToBeVisible({
		target: page.getByText('This field is required'),
		trigger: page.locator('.modal-body'),
	});

	await page.getByLabel('NameRequired').fill(`Tag${getRandomInt()}`);

	// Check we can't publish without selecting a space

	await tagsPage.spaceCheckbox.uncheck();

	await page.getByLabel('Space Selector').focus();

	await page.keyboard.press('Tab');

	await expect(page.getByText('The Space field is required')).toBeVisible();

	await expect(tagsPage.saveButton).toBeDisabled();
});

test(
	'Tags with the same name can be created',
	{tag: ['@LPD-69204', '@LPD-92491']},
	async ({apiHelpers, assetsPage, infoPanelPage, page}) => {
		const applicationName = 'cms/basic-web-contents';
		const contentTitle = `title ${getRandomString()}`;
		let objectEntry: ObjectEntry;
		const tagNameBase = getRandomString().substring(0, 7);
		const tagName1 = `A${tagNameBase}`;
		const tagName2 = `a${tagNameBase}`;

		try {
			objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title: contentTitle,
				},
				applicationName,
				'Default'
			);

			await assetsPage.gotoAll();

			await assetsPage.execItemAction({
				action: 'Show Details',
				filter: contentTitle,
			});

			await expect(
				page.getByRole('heading', {name: contentTitle})
			).toBeVisible();

			await infoPanelPage.selectTab('Categorization').click();

			await page.getByPlaceholder('Add tag').fill(tagName1);

			const newTagOption = page.getByRole('option', {
				name: 'Create New Tag:',
			});

			await newTagOption.waitFor();
			await newTagOption.click();

			await expect(page.getByText(tagName1, {exact: true})).toBeVisible();

			await expect(async () => {
				await page.getByPlaceholder('Add tag').fill(tagName2);

				await newTagOption.waitFor();
				await newTagOption.click();

				await expect(
					page.getByText(tagName2, {exact: true})
				).toBeVisible();
			}).toPass({timeout: 5000});
		}
		finally {
			if (objectEntry) {
				await apiHelpers.objectEntry.deleteObjectEntry(
					applicationName,
					String(objectEntry.id)
				);
			}
		}
	}
);

test(
	'Filtering tags by Space shows the tags scoped to that Space',
	{tag: '@LPD-89720'},
	async ({apiHelpers, page, tagsPage}) => {
		const {id: siteId} =
			await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('cms');

		// Create two Spaces, with one tag scoped to each one

		const spaceName = getRandomString();

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			type: 'Space',
		});

		const anotherSpace =
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: getRandomString(),
				type: 'Space',
			});

		const tagName = getRandomString();

		await apiHelpers.headlessAdminTaxonomy.postSiteKeyword({
			assetLibraries: [
				{externalReferenceCode: space.externalReferenceCode},
			],
			name: tagName,
			siteId,
		});

		const anotherTagName = getRandomString();

		await apiHelpers.headlessAdminTaxonomy.postSiteKeyword({
			assetLibraries: [
				{externalReferenceCode: anotherSpace.externalReferenceCode},
			],
			name: anotherTagName,
			siteId,
		});

		// Both tags are listed before filtering

		await tagsPage.goto();

		await expect(tagsPage.getItem(tagName)).toBeVisible();
		await expect(tagsPage.getItem(anotherTagName)).toBeVisible();

		// Filtering by a Space keeps the tag scoped to it and hides the rest

		await applyFDSSelectionFilter(page, {
			filter: 'Space',
			value: spaceName,
		});

		await expect(tagsPage.getItem(tagName)).toBeVisible();
		await expect(tagsPage.getItem(anotherTagName)).toBeHidden();
	}
);
