/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';
import {statSync} from 'fs';

import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getTempDir} from '../../../utils/temp';

/**
 * Asserts that a document restored by a legacy database upgrade opens on the
 * page that lists it, reports the metadata the archive recorded for it, and
 * downloads with the bytes the store holds for it. Pass expectedSize for an
 * archive whose byte count is known; without it the download must still
 * produce a non-empty file.
 */
export async function viewUpgradedDocument({
	documentPageURL,
	expectedSize,
	page,
	title,
}: {
	documentPageURL: string;
	expectedSize?: number;
	page: Page;
	title: string;
}) {
	await page.goto(documentPageURL);

	await page.getByRole('link', {name: title}).click();

	const downloadButton = page
		.locator('.sidebar-section')
		.getByRole('link', {name: 'Download'});

	await clickAndExpectToBeVisible({
		target: downloadButton,
		timeout: 5000,
		trigger: page.locator('a[href*=infoPanel]'),
	});

	const username = page.locator('.sidebar-body .username');

	await expect(username).toBeVisible();

	await expect(username).toHaveText('Test Test');

	const version = page.locator('.sidebar-header .label-item');

	await expect(version).toBeVisible();

	await expect(version).toHaveText('Version 1.0');

	const workflowStatus = page.locator('.sidebar-header .workflow-status');

	await expect(workflowStatus).toBeVisible();

	await expect(workflowStatus).toHaveText('Approved');

	const downloadPromise = page.waitForEvent('download');

	await downloadButton.click();

	const download = await downloadPromise;

	const filePath = getTempDir() + download.suggestedFilename();

	await download.saveAs(filePath);

	const {size} = statSync(filePath);

	if (expectedSize !== undefined) {
		expect(size).toBe(expectedSize);
	}
	else {
		expect(size).toBeGreaterThan(0);
	}
}
