/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {ExportImportDataSelectionPage} from '../pages/ExportImportDataSelectionPage';
import {ExportImportPage} from '../pages/ExportImportPage';

export async function assertImportWizardControls({
	contentLabel,
	exportImportDataSelectionPage,
	exportImportPage,
	folderPath,
	hasCommentsAndRatings,
	hasMirrorWithOverwriting,
	hasSiteBuilder,
	name,
	page,
}: {
	contentLabel?: string;
	exportImportDataSelectionPage: ExportImportDataSelectionPage;
	exportImportPage: ExportImportPage;
	folderPath: string;
	hasCommentsAndRatings: boolean;
	hasMirrorWithOverwriting: boolean;
	hasSiteBuilder: boolean;
	name: string;
	page: Page;
}) {

	// Upload the LAR on the import wizard setup step

	await exportImportPage.clickNew();

	await exportImportPage.goToImportDataSelection({folderPath, name});

	// Data selection step

	await expect(
		page.getByRole('checkbox', {name: 'Import Permissions'})
	).toBeVisible();

	await expect(
		exportImportPage.replicateSelectedDeletionsCheckbox
	).toBeHidden();

	if (contentLabel) {
		await exportImportDataSelectionPage.expandSection('Objects');

		await expect(
			page.getByRole('checkbox', {name: contentLabel})
		).toBeVisible();
	}

	if (hasSiteBuilder) {
		await exportImportDataSelectionPage.expandSection('Site Builder');

		await expect(
			page.getByRole('checkbox', {name: 'Look and Feel'})
		).toBeVisible();
	}
	else {
		await expect(page.getByText('Site Builder')).toBeHidden();
	}

	if (hasCommentsAndRatings) {
		await exportImportDataSelectionPage.expandSection('Content & Data');

		await expect(page.getByText('Comments and Ratings')).toBeVisible();
	}
	else {
		await expect(page.getByText('Comments and Ratings')).toBeHidden();
	}

	await exportImportPage.continueButton.click();

	// Settings step

	await expect(
		page.getByRole('radio', {name: 'Use the Original Author'})
	).toBeVisible();

	await expect(
		page.getByRole('radio', {name: 'Use the Current User as Author'})
	).toBeVisible();

	await expect(
		page.getByText(
			'All data and content inside the imported LAR is created as new the first time while maintaining a reference to the source. Subsequent imports from the same source update the entries instead of creating new entries.'
		)
	).toBeVisible();

	if (hasMirrorWithOverwriting) {
		await expect(
			page.getByRole('radio', {exact: true, name: 'Mirror'})
		).toBeVisible();

		await expect(
			page.getByRole('radio', {name: 'Mirror with overwriting'})
		).toBeVisible();
	}
	else {
		await expect(page.getByText('Mirror with overwriting')).toBeHidden();
	}
}
