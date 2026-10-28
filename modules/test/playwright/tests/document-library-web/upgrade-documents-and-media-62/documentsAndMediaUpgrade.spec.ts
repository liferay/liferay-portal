/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {searchAdminPageTest} from '../../../fixtures/searchAdminPageTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {reindexAllSearchIndexes} from '../../portal-db-infrastructure/utils/reindexAllSearchIndexes';
import {viewUpgradedDocument} from '../../portal-db-infrastructure/utils/viewUpgradedDocument';

const test = mergeTests(loginTest(), searchAdminPageTest);

test(
	'Can view a document with its comment and extracted metadata after upgrade',
	{tag: '@LPD-112520'},
	async ({page, searchAdminPage}) => {
		await reindexAllSearchIndexes({searchAdminPage});

		await viewUpgradedDocument({
			documentPageURL: '/web/guest/document',
			page,
			title: 'Document1',
		});

		await page
			.getByRole('button', {name: 'Automatically Extracted Metadata'})
			.click();

		const extractedMetadata = page
			.getByRole('region')
			.filter({hasText: 'Content Type'});

		await expect(
			extractedMetadata
				.getByText('Content Type', {exact: true})
				.locator('..')
		).toContainText('application/msword');

		await expect(
			extractedMetadata.getByText('Creator', {exact: true}).locator('..')
		).toContainText('Liferay');

		await clickAndExpectToBeHidden({
			target: page
				.locator('.sidebar-section')
				.getByRole('link', {name: 'Download'}),
			trigger: page.locator('a[href*=infoPanel]'),
		});

		await expect(
			page.getByRole('article').getByText('Document Comment')
		).toBeVisible();
	}
);
