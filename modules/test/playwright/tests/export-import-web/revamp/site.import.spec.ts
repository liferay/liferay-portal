/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import * as path from 'path';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {exportImportPagesTest} from './fixtures/exportImportPagesTest';

export const test = mergeTests(
	exportImportPagesTest,
	featureFlagsTest({
		'LPD-57655': {enabled: true},
	}),
	loginTest(),
	productMenuPageTest
);

test('Can upload valid lar', async ({
	exportImportPage,
	page,
	productMenuPage,
}) => {
	await productMenuPage.openProductMenuIfClosed();

	await productMenuPage.goToPublishingImport();

	await exportImportPage.newImport.click();

	await exportImportPage.selectFile(
		path.join(__dirname, '../main/dependencies', 'site.lar')
	);

	await exportImportPage.completedLabel.waitFor();

	await expect(page.getByText('site.lar', {exact: true})).toBeVisible();

	await expect(exportImportPage.continueButton).toBeEnabled();
});

test('Auto-fills the Name field with the uploaded file name', async ({
	exportImportPage,
	productMenuPage,
}) => {
	await productMenuPage.openProductMenuIfClosed();

	await productMenuPage.goToPublishingImport();

	await exportImportPage.newImport.click();

	await expect(exportImportPage.nameInput).toHaveValue('');

	await exportImportPage.selectFile(
		path.join(__dirname, '../main/dependencies', 'site.lar')
	);

	await exportImportPage.completedLabel.waitFor();

	await expect(exportImportPage.nameInput).toHaveValue('site.lar');
});

test('Preserves the user-provided Name when a file is uploaded', async ({
	exportImportPage,
	productMenuPage,
}) => {
	await productMenuPage.openProductMenuIfClosed();

	await productMenuPage.goToPublishingImport();

	await exportImportPage.newImport.click();

	await exportImportPage.nameInput.fill('My custom import');

	await exportImportPage.selectFile(
		path.join(__dirname, '../main/dependencies', 'site.lar')
	);

	await exportImportPage.completedLabel.waitFor();

	await expect(exportImportPage.nameInput).toHaveValue('My custom import');
});

test('Shows a required error when Name is left empty on blur', async ({
	exportImportPage,
	page,
	productMenuPage,
}) => {
	await productMenuPage.openProductMenuIfClosed();

	await productMenuPage.goToPublishingImport();

	await exportImportPage.newImport.click();

	await exportImportPage.nameInput.focus();

	await exportImportPage.nameInput.blur();

	await expect(
		page.getByText('This field is required.', {exact: true})
	).toBeVisible();
});

test('Should show error on invalid lar upload (extension .lar)', async ({
	exportImportPage,
	productMenuPage,
}) => {
	await productMenuPage.openProductMenuIfClosed();

	await productMenuPage.goToPublishingImport();

	await exportImportPage.newImport.click();

	await exportImportPage.import(
		path.join(__dirname, '../main/dependencies', 'folder.portlet.lar'),
		'Uploaded LAR file type Portlet does not match layout-prototype, layout-set, layout-set-prototype.'
	);
	await expect(exportImportPage.continueButton).toBeDisabled();
});

test('Should show error on invalid lar upload (extension not .lar)', async ({
	exportImportPage,
	productMenuPage,
}) => {
	await productMenuPage.openProductMenuIfClosed();

	await productMenuPage.goToPublishingImport();

	await exportImportPage.newImport.click();

	await exportImportPage.import(
		path.join(__dirname, '../main/dependencies', 'Document.jpg'),
		'File type must be .lar'
	);

	await expect(exportImportPage.continueButton).toBeDisabled();
});
