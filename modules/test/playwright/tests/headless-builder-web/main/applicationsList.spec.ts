/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {applyFDSSelectionFilter} from '../../../utils/applyFDSSelectionFilter';
import getRandomString from '../../../utils/getRandomString';
import {setFDSItemsPerPage} from '../../../utils/setFDSItemsPerPage';
import {headlessBuilderPagesTest} from './fixtures/headlessBuilderPagesTest';

const test = mergeTests(
	dataApiHelpersTest,
	headlessBuilderPagesTest({}),
	loginTest()
);

test(
	'can list API applications, sort them and filter them by status',
	{tag: '@LPD-106934'},
	async ({apiHelpers, headlessBuilderPage, page}) => {
		const applications = [];

		for (const applicationStatus of [
			'published',
			'unpublished',
			'unpublished',
		]) {
			const application = await apiHelpers.objectEntry.postObjectEntry(
				{
					applicationStatus,
					baseURL: `test-${getRandomString()}`,
					title: getRandomString(),
				},
				'headless-builder/applications'
			);

			apiHelpers.data.push({id: application.id, type: 'apiApplication'});

			applications.push(application);
		}

		const failedResponses: string[] = [];

		page.on('response', (response) => {
			if (response.status() >= 400 && response.url().includes('/o/')) {
				failedResponses.push(`${response.status()} ${response.url()}`);
			}
		});

		await headlessBuilderPage.goto();

		await setFDSItemsPerPage(page, 50);

		const [publishedApplication, ...unpublishedApplications] = applications;

		await expect(
			page.locator('.fds tbody tr', {
				hasText: publishedApplication.title,
			})
		).toContainText('Published');

		for (const unpublishedApplication of unpublishedApplications) {
			await expect(
				page.locator('.fds tbody tr', {
					hasText: unpublishedApplication.title,
				})
			).toContainText('Unpublished');
		}

		const statusColumnHeader = page
			.getByRole('columnheader')
			.getByText('Status');

		for (const publishedFirst of [true, false]) {
			await statusColumnHeader.click();

			await expect(async () => {
				const rowTexts = await page
					.locator('.fds tbody tr')
					.allInnerTexts();

				const getRowIndex = (title: string) =>
					rowTexts.findIndex((rowText) => rowText.includes(title));

				const publishedRowIndex = getRowIndex(
					publishedApplication.title
				);

				expect(publishedRowIndex).toBeGreaterThan(-1);

				for (const unpublishedApplication of unpublishedApplications) {
					const unpublishedRowIndex = getRowIndex(
						unpublishedApplication.title
					);

					expect(unpublishedRowIndex).toBeGreaterThan(-1);

					if (publishedFirst) {
						expect(publishedRowIndex).toBeLessThan(
							unpublishedRowIndex
						);
					}
					else {
						expect(publishedRowIndex).toBeGreaterThan(
							unpublishedRowIndex
						);
					}
				}
			}).toPass({timeout: 15000});
		}

		await applyFDSSelectionFilter(page, {
			exclude: true,
			filter: 'Status',
			multiple: false,
			value: 'Published',
		});

		for (const unpublishedApplication of unpublishedApplications) {
			await expect(
				page.locator('.fds tbody tr', {
					hasText: unpublishedApplication.title,
				})
			).toContainText('Unpublished');
		}

		await expect(
			page.locator('.fds tbody tr', {
				hasText: publishedApplication.title,
			})
		).toHaveCount(0);

		expect(failedResponses).toEqual([]);
	}
);
