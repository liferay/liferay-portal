/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {createCategories} from '../../../helpers/CreateCategories';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
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

test(
	'Edit external reference code of an existing vocabulary',
	{tag: '@LPD-90008'},
	async ({
		apiHelpers,
		assetCategoriesAdminPage,
		page,
		site,
		vocabulariesEditPage,
	}) => {
		const updatedExternalReferenceCode = getRandomString();
		const vocabularyName = getRandomString();

		const [vocabulary1, vocabulary2] = await Promise.all([
			apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
				name: vocabularyName,
				siteId: site.id,
			}),
			apiHelpers.headlessAdminTaxonomy.postSiteTaxonomyVocabulary({
				name: getRandomString(),
				siteId: site.id,
			}),
		]);

		await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

		await test.step('Edit the external reference code', async () => {
			await vocabulariesEditPage.goto(vocabularyName);

			await expect(
				vocabulariesEditPage.externalReferenceCodeInput
			).toBeEditable();

			await expect(
				vocabulariesEditPage.externalReferenceCodeInput
			).toHaveValue(vocabulary1.externalReferenceCode);

			await vocabulariesEditPage.fillExternalReferenceCode(
				updatedExternalReferenceCode
			);

			await vocabulariesEditPage.saveButton.click();

			await waitForAlert(page);

			await vocabulariesEditPage.goto(vocabularyName);

			await expect(
				vocabulariesEditPage.externalReferenceCodeInput
			).toHaveValue(updatedExternalReferenceCode);
		});

		await test.step('Reject duplicate external reference code on edit', async () => {
			await vocabulariesEditPage.fillExternalReferenceCode(
				vocabulary2.externalReferenceCode
			);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: vocabulariesEditPage.saveButton,
				trigger: page.getByText(
					'Please enter a unique external reference code.',
					{exact: true}
				),
			});
		});
	}
);

test(
	'Edit external reference code of an existing category',
	{tag: '@LPD-90008'},
	async ({
		apiHelpers,
		assetCategoriesAdminPage,
		assetCategoriesEditPage,
		page,
		site,
	}) => {
		const categoryName = getRandomString();
		const updatedExternalReferenceCode = getRandomString();
		const vocabularyName = getRandomString();

		const [category1, category2] = await createCategories({
			apiHelpers,
			categoryNames: [{name: categoryName}, {name: getRandomString()}],
			siteId: site.id,
			vocabularyName,
		});

		await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

		await test.step('Edit the external reference code', async () => {
			await assetCategoriesEditPage.goto(categoryName);

			await expect(
				assetCategoriesEditPage.externalReferenceCodeInput
			).toBeEditable();

			await expect(
				assetCategoriesEditPage.externalReferenceCodeInput
			).toHaveValue(category1.externalReferenceCode);

			await assetCategoriesEditPage.fillExternalReferenceCode(
				updatedExternalReferenceCode
			);

			await assetCategoriesEditPage.save(`Success:${categoryName}`);

			await assetCategoriesEditPage.goto(categoryName);

			await expect(
				assetCategoriesEditPage.externalReferenceCodeInput
			).toHaveValue(updatedExternalReferenceCode);
		});

		await test.step('Reject duplicate external reference code on edit', async () => {
			await assetCategoriesEditPage.fillExternalReferenceCode(
				category2.externalReferenceCode
			);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: assetCategoriesEditPage.saveButton,
				trigger: page.getByText(
					'Please enter a unique external reference code.',
					{exact: true}
				),
			});
		});
	}
);

test('Add, edit and delete a vocabulary', async ({
	assetCategoriesAdminPage,
	page,
	site,
	vocabulariesEditPage,
}) => {
	await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

	const vocabularyDescription = getRandomString();
	const vocabularyExternalReferenceCode = getRandomString();
	const vocabularyName = getRandomString();

	await test.step('Add a vocabulary with description', async () => {
		await assetCategoriesAdminPage.newVocabularyButton.click();

		await vocabulariesEditPage.add({
			description: vocabularyDescription,
			externalReferenceCode: vocabularyExternalReferenceCode,
			name: vocabularyName,
		});

		await expect(
			page.getByRole('heading', {name: vocabularyName})
		).toBeVisible();

		await expect(page.getByText(vocabularyDescription)).toBeVisible();
	});

	const newVocabularyName = getRandomString();

	await test.step('Edit the vocabulary', async () => {
		await vocabulariesEditPage.goto(vocabularyName);

		await vocabulariesEditPage.fillName(newVocabularyName);

		await expect(
			vocabulariesEditPage.externalReferenceCodeInput
		).toHaveValue(vocabularyExternalReferenceCode);

		await vocabulariesEditPage.saveButton.click();

		await waitForAlert(page);

		await expect(
			page.getByRole('heading', {name: newVocabularyName})
		).toBeVisible();
	});

	await test.step('Add a vocabulary with existing external reference code', async () => {
		await assetCategoriesAdminPage.newVocabularyButton.click();

		await vocabulariesEditPage.fillName(getRandomString());
		await vocabulariesEditPage.fillExternalReferenceCode(
			vocabularyExternalReferenceCode
		);

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: vocabulariesEditPage.saveButton,
			trigger: page.getByText(
				'Please enter a unique external reference code.',
				{
					exact: true,
				}
			),
		});

		await assetCategoriesAdminPage.goto(site.friendlyUrlPath);
	});

	await test.step('Delete the vocabulary', async () => {
		await vocabulariesEditPage.delete(newVocabularyName);

		await waitForAlert(page);

		await expect(
			page.getByRole('heading', {name: newVocabularyName})
		).not.toBeVisible();
	});
});

assetTypes.forEach(async (assetType) => {
	test(`Add a vocabulary for ${assetType}`, async ({
		assetCategoriesAdminPage,
		page,
		site,
		vocabulariesEditPage,
	}) => {
		await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

		const vocabularyName = getRandomString();

		await assetCategoriesAdminPage.newVocabularyButton.click();

		await vocabulariesEditPage.add({
			assetTypes: [assetType],
			name: vocabularyName,
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

	const vocabularyName = getRandomString();
	const assetTypes = ['Blogs Entry', 'Object Entry Folder'];

	await test.step('Add a vocabulary with 2 asset types', async () => {
		await assetCategoriesAdminPage.newVocabularyButton.click();

		await vocabulariesEditPage.add({
			assetTypes,
			name: vocabularyName,
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
			categoryNames: [
				{name: getRandomString()},
				{name: getRandomString()},
			],
			siteId: site.id,
			vocabularyName: getRandomString(),
		});
	});

	await assetCategoriesAdminPage.goto(site.friendlyUrlPath);

	await test.step('Delete all categories', async () => {
		await assetCategoriesAdminPage.deleteAllCategories();

		await waitForAlert(page);

		await expect(page.getByText('There are no categories')).toBeVisible();
	});
});
