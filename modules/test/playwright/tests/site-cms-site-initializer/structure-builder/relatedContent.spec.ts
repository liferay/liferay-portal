/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from './fixtures/structureBuilderPagesTest';

const test = mergeTests(
	cmsPagesTest,
	loginTest(),
	pageEditorPagesTest,
	structureBuilderPagesTest
);

test(
	'CMS Content Structure can have multiple related content fields',
	{
		tag: '@LPD-85625',
	},
	async ({page, structureBuilderPage}) => {
		const relatedContentLabel1 = getRandomString();
		const relatedContentLabel2 = getRandomString();

		await test.step('Create Content Structure', async () => {
			const label1 = getRandomString();
			const name1 = `StructureName${getRandomInt()}`;

			await structureBuilderPage.createStructureFromData({
				label: label1,
				name: name1,
				page: structureBuilderPage,
			});
		});

		await test.step('Add 2 Related Content field', async () => {
			await structureBuilderPage.addRelatedContent(
				relatedContentLabel1,
				'Basic Document'
			);
			await structureBuilderPage.addRelatedContent(
				relatedContentLabel2,
				'Basic Web Content'
			);
		});

		await test.step('Publish the Content Structure, reload the page and check that 2 Related Content fields are visible', async () => {
			await structureBuilderPage.publishStructure();

			await page.reload();

			await expect(
				page.getByRole('treeitem', {
					exact: true,
					name: relatedContentLabel1,
				})
			).toBeVisible();
			await expect(
				page.getByRole('treeitem', {
					exact: true,
					name: relatedContentLabel2,
				})
			).toBeVisible();
		});
	}
);
