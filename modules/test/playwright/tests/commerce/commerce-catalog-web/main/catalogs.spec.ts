/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {commercePagesTest} from '../../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import createTempFile from '../../../../utils/createTempFile';
import getRandomString from '../../../../utils/getRandomString';
import {waitForAlert} from '../../../../utils/waitForAlert';

export const test = mergeTests(
	commercePagesTest,
	dataApiHelpersTest,
	loginTest()
);

test(
	'Catalogs can be created, edited and deleted',
	{tag: ['@COMMERCE-6272', '@COMMERCE-6273', '@LPD-106358']},
	async ({
		apiHelpers,
		commerceAdminCatalogDetailsPage,
		commerceAdminCatalogsPage,
		page,
	}) => {
		const catalogName = getRandomString();
		const editedCatalogName = `${catalogName} Edited`;

		let catalogId: string;

		await test.step('Create a catalog', async () => {
			await commerceAdminCatalogsPage.goto();
			await commerceAdminCatalogsPage.addCatalogsButton.click();

			await expect(
				commerceAdminCatalogsPage.modalFieldName
			).toBeVisible();

			await commerceAdminCatalogsPage.modalFieldName.fill(catalogName);
			await commerceAdminCatalogsPage.modalLanguageSelect.selectOption({
				label: 'English (United States)',
			});
			await commerceAdminCatalogsPage.modalCurrencySelect.selectOption({
				label: 'US Dollar',
			});
			await commerceAdminCatalogsPage.modalSubmitButton.click();

			await expect(commerceAdminCatalogsPage.catalogId).toBeVisible();

			catalogId = await commerceAdminCatalogsPage.catalogId.textContent();

			apiHelpers.data.push({id: catalogId, type: 'catalog'});

			await commerceAdminCatalogsPage.goto();
			await commerceAdminCatalogsPage.search(catalogName);

			await expect(
				commerceAdminCatalogsPage.catalogLink(catalogName)
			).toBeVisible();

			await commerceAdminCatalogsPage.catalogLink(catalogName).click();

			await expect(commerceAdminCatalogDetailsPage.nameInput).toHaveValue(
				catalogName
			);
			await expect(
				commerceAdminCatalogDetailsPage.languageSelect
			).toHaveValue('en_US');
			await expect(
				commerceAdminCatalogDetailsPage.currencySelect
			).toHaveValue('USD');
		});

		await test.step('Edit the catalog', async () => {
			await commerceAdminCatalogDetailsPage.nameInput.fill(
				editedCatalogName
			);
			await commerceAdminCatalogDetailsPage.currencySelect.selectOption({
				label: 'Euro',
			});
			await commerceAdminCatalogDetailsPage.saveButton.click();

			await waitForAlert(page);

			await commerceAdminCatalogsPage.goto();
			await commerceAdminCatalogsPage.search(editedCatalogName);
			await commerceAdminCatalogsPage
				.catalogLink(editedCatalogName)
				.click();

			await expect(commerceAdminCatalogDetailsPage.nameInput).toHaveValue(
				editedCatalogName
			);
			await expect(
				commerceAdminCatalogDetailsPage.currencySelect
			).toHaveValue('EUR');
		});

		await test.step('Delete the catalog', async () => {
			await commerceAdminCatalogsPage.goto();
			await commerceAdminCatalogsPage.search(editedCatalogName);
			await commerceAdminCatalogsPage
				.catalogActionsButton(editedCatalogName)
				.click();
			await commerceAdminCatalogsPage.deleteMenuItem.click();

			await waitForAlert(page);

			await expect(
				commerceAdminCatalogsPage.catalogLink(editedCatalogName)
			).toHaveCount(0);

			apiHelpers.setData(
				apiHelpers.data.filter((item) => item.id !== catalogId)
			);
		});
	}
);

test(
	'A default image can be added to a catalog and removed',
	{tag: ['@COMMERCE-6278', '@LPD-106358']},
	async ({
		apiHelpers,
		commerceAdminCatalogDetailsPage,
		commerceAdminCatalogsPage,
		page,
	}) => {
		const imageName = `${getRandomString()}.png`;
		const imageUrlPattern = new RegExp(imageName.replace('.', '[.-]'));
		const imagePath = createTempFile(
			imageName,
			readFileSync(path.join(__dirname, '/dependencies/liferay.png'))
		);

		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		await commerceAdminCatalogsPage.goto();
		await commerceAdminCatalogsPage.search(catalog.name);
		await commerceAdminCatalogsPage.catalogLink(catalog.name).click();

		await test.step('Add the default catalog image', async () => {
			await commerceAdminCatalogDetailsPage.uploadDefaultImage(imagePath);

			await expect(
				commerceAdminCatalogDetailsPage.defaultImage
			).toHaveAttribute('src', imageUrlPattern);

			apiHelpers.data.push({
				id: await commerceAdminCatalogDetailsPage.fileEntryIdInput.inputValue(),
				type: 'document',
			});

			await commerceAdminCatalogDetailsPage.saveButton.click();

			await waitForAlert(page);

			await commerceAdminCatalogsPage.goto();
			await commerceAdminCatalogsPage.search(catalog.name);
			await commerceAdminCatalogsPage.catalogLink(catalog.name).click();

			await expect(
				commerceAdminCatalogDetailsPage.defaultImage
			).toHaveAttribute('src', imageUrlPattern);
		});

		await test.step('Remove the default catalog image', async () => {
			await commerceAdminCatalogDetailsPage.removeImageButton.click();

			await expect(
				commerceAdminCatalogDetailsPage.defaultImage
			).toHaveCount(0);

			await commerceAdminCatalogDetailsPage.saveButton.click();

			await waitForAlert(page);

			await commerceAdminCatalogsPage.goto();
			await commerceAdminCatalogsPage.search(catalog.name);
			await commerceAdminCatalogsPage.catalogLink(catalog.name).click();

			await expect(
				commerceAdminCatalogDetailsPage.defaultImage
			).toHaveCount(0);
		});
	}
);

test(
	'The Master catalog is created by default and cannot be deleted',
	{tag: '@LPD-106358'},
	async ({commerceAdminCatalogsPage}) => {
		await commerceAdminCatalogsPage.goto();
		await commerceAdminCatalogsPage.search('Master');

		await expect(
			commerceAdminCatalogsPage.catalogLink('Master')
		).toBeVisible();

		await commerceAdminCatalogsPage.catalogActionsButton('Master').click();
		await commerceAdminCatalogsPage.deleteMenuItem.click();

		await expect(commerceAdminCatalogsPage.deleteMenuItem).toBeHidden();

		await commerceAdminCatalogsPage.goto();
		await commerceAdminCatalogsPage.search('Master');

		await expect(
			commerceAdminCatalogsPage.catalogLink('Master')
		).toBeVisible();
	}
);

test(
	'A catalog that has products cannot be deleted',
	{tag: '@LPD-106358'},
	async ({apiHelpers, commerceAdminCatalogsPage, page}) => {
		const catalog =
			await apiHelpers.headlessCommerceAdminCatalog.postCatalog();

		await apiHelpers.headlessCommerceAdminCatalog.postProduct({
			catalogId: catalog.id,
		});

		await commerceAdminCatalogsPage.goto();
		await commerceAdminCatalogsPage.search(catalog.name);

		await commerceAdminCatalogsPage
			.catalogActionsButton(catalog.name)
			.click();
		await commerceAdminCatalogsPage.deleteMenuItem.click();

		await waitForAlert(
			page,
			'You cannot delete catalogs that have products.',
			{autoClose: false, type: 'danger'}
		);

		await commerceAdminCatalogsPage.goto();
		await commerceAdminCatalogsPage.search(catalog.name);

		await expect(
			commerceAdminCatalogsPage.catalogLink(catalog.name)
		).toBeVisible();
	}
);
