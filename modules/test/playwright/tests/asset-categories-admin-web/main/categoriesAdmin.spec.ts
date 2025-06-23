/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {createCategories} from '../../../helpers/CreateCategories';
import {waitForAlert} from '../../../utils/waitForAlert';
import {assetCategoriesPagesTest} from './fixtures/assetCategoriesAdminPagesTest';

const test = mergeTests(
	apiHelpersTest,
	assetCategoriesPagesTest,
	isolatedSiteTest,
	loginTest()
);

const assetTypes = [
	'All Asset Types',
	'Blogs Entry',
	'Document',
	'User',
	'Web Content Article',
];

test('Add, edit and delete a vocabulary', async ({
	assetCategoriesAdminPage,
	page,
	site,
	vocabulariesEditPage,
}) => {
	await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

	const vocabularyDescription = 'Vocabulary Description';
	const vocabularyName = 'Vocabulary 1';

	await test.step('Add a vocabulary with description', async () => {
		await assetCategoriesAdminPage.newVocabularyButton.click();

		await vocabulariesEditPage.add({
			name: vocabularyName,
			description: vocabularyDescription,
		});

		await expect(
			page.getByRole('heading', {name: vocabularyName})
		).toBeVisible();

		await expect(page.getByText(vocabularyDescription)).toBeVisible();
	});

	const newVocabularyName = 'Vocabulary Changed';

	await test.step('Edit the vocabulary', async () => {
		await vocabulariesEditPage.goto(vocabularyName);

		await vocabulariesEditPage.fillName(newVocabularyName);

		await vocabulariesEditPage.saveButton.click();

		await waitForAlert(page);

		await expect(
			page.getByRole('heading', {name: newVocabularyName})
		).toBeVisible();
	});

	await test.step('Delete the vocabulary', async () => {
		await vocabulariesEditPage.delete(newVocabularyName);

		await waitForAlert(page);

		await expect(
			page.getByRole('heading', {name: newVocabularyName})
		).not.toBeVisible();
	});
});

assetTypes.forEach(async (assetType, index) => {
	test(`Add a vocabulary for ${assetType}`, async ({
		assetCategoriesAdminPage,
		page,
		site,
		vocabulariesEditPage,
	}) => {
		await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

		const vocabularyName = `Vocabulary ${index + 1}`;

		await assetCategoriesAdminPage.newVocabularyButton.click();

		await vocabulariesEditPage.add({
			name: vocabularyName,
			assetTypes: [assetType],
		});

		await expect(
			page.getByRole('heading', {name: vocabularyName})
		).toBeVisible();

		await expect(page.getByText(assetType, {exact: true})).toBeVisible();
	});
});

test('Delete an associated asset type of vocabulary', async ({
	assetCategoriesAdminPage,
	page,
	site,
	vocabulariesEditPage,
}) => {
	await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

	const vocabularyName = 'Vocabulary 1';
	const assetTypes = ['Basic Web Content', 'Object Entry Folder'];

	await test.step('Add a vocabulary with 2 asset types', async () => {
		await assetCategoriesAdminPage.newVocabularyButton.click();

		await vocabulariesEditPage.add({
			name: vocabularyName,
			assetTypes,
		});

		await expect(
			page.getByRole('heading', {name: vocabularyName})
		).toBeVisible();

		await expect(
			page.getByText(assetTypes.join(', '), {exact: true})
		).toBeVisible();
	});

	await test.step('Remove last associated asset type', async () => {
		await vocabulariesEditPage.goto(vocabularyName);

		await vocabulariesEditPage.removeLastAssociatedAssetType();

		await vocabulariesEditPage.saveButton.click();

		await waitForAlert(page);

		await expect(
			page.getByText('Object Entry Folder', {exact: true})
		).not.toBeVisible();
	});
});

test('Delete all categories of a vocabulary', async ({
	apiHelpers,
	assetCategoriesAdminPage,
	page,
	site,
}) => {
	await test.step('Add a vocabulary with 2 categories', async () => {
		await createCategories({
			apiHelpers,
			categoryNames: [{name: 'Category 1'}, {name: 'Category 2'}],
			siteId: site.id,
			vocabularyName: 'Vocabulary 1',
		});
	});

	await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

	await test.step('Delete all categories', async () => {
		await assetCategoriesAdminPage.deleteAllCategories();

		await waitForAlert(page);

		await expect(page.getByText('There are no categories')).toBeVisible();
	});
});
