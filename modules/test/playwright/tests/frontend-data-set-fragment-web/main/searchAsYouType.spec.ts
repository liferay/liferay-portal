/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataSetManagerApiHelpersTest} from '../../../fixtures/dataSetManagerApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {dataSetFragmentPageTest} from './fixtures/dataSetFragmentPageTest';

let dataSetERC: string;
let dataSetLabel: string;

export const test = mergeTests(
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedLayoutTest({publish: false}),
	loginTest(),
	dataSetFragmentPageTest
);

test.beforeEach(async ({dataSetManagerApiHelpers}) => {
	dataSetERC = getRandomString();
	dataSetLabel = getRandomString();

	await dataSetManagerApiHelpers.createDataSet({
		erc: dataSetERC,
		label: dataSetLabel,
		restEndpoint: `/by-external-reference-code/${dataSetERC}/dataSetToDataSetTableSections`,
		restSchema: 'DataSetTableSection',
	});

	await dataSetManagerApiHelpers.createDataSetTableSection({
		dataSetERC,
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({erc: dataSetERC});
});

function countSearchRequests(page: Page) {
	const searches: Array<string> = [];

	page.on('request', (request) => {
		const url = new URL(request.url());

		if (url.pathname.includes('/o/data-set-admin/data-sets')) {
			const search = url.searchParams.get('search');

			if (search !== null) {
				searches.push(search);
			}
		}
	});

	return searches;
}

test(
	'Searches once for a burst of keystrokes when search as you type is enabled',
	{tag: '@LPD-89786'},
	async ({dataSetFragmentPage, dataSetManagerApiHelpers, layout, page}) => {
		await test.step('Enable search as you type', async () => {
			await dataSetManagerApiHelpers.updateDataSet({
				erc: dataSetERC,
				searchAsYouType: true,
			});
		});

		await test.step('Configure Data Set in the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		const searches = countSearchRequests(page);

		await test.step('Type without pressing Enter', async () => {
			await dataSetFragmentPage.searchInput.pressSequentially('abc', {
				delay: 50,
			});
		});

		await test.step('Only the final keyword is requested', async () => {
			await expect.poll(() => searches).toEqual(['abc']);
		});
	}
);

test(
	'Searches only on Enter when search as you type is disabled',
	{tag: '@LPD-89786'},
	async ({dataSetFragmentPage, layout, page}) => {
		await test.step('Configure Data Set in the page', async () => {
			await dataSetFragmentPage.configureDataSetFragment({
				dataSetLabel,
				layout,
			});
		});

		const searches = countSearchRequests(page);

		await test.step('Typing alone requests nothing', async () => {
			await dataSetFragmentPage.searchInput.pressSequentially('abc', {
				delay: 50,
			});

			await page.waitForTimeout(1000);

			expect(searches).toEqual([]);
		});

		await test.step('Enter requests the keyword', async () => {
			await dataSetFragmentPage.searchInput.press('Enter');

			await expect.poll(() => searches).toEqual(['abc']);
		});
	}
);
