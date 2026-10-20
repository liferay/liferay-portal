/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {setItemsPerPage} from '../../../utils/pagination';
import {waitForAlert} from '../../../utils/waitForAlert';

const test = mergeTests(
	apiHelpersTest,
	documentLibraryPagesTest,
	isolatedSiteTest,
	loginTest()
);

test(
	'File Entry History is ordered correctly',
	{tag: '@LPD-52628'},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const fileEntryTitle =
			await test.step('Create a new File Entry with multiple versions', async () => {
				const fileEntry =
					await apiHelpers.headlessDelivery.postDocument(
						site.id,
						createReadStream(
							path.join(__dirname, '/dependencies/image1.jpeg')
						)
					);

				for (let i = 0; i < 20; i++) {
					await apiHelpers.headlessDelivery.patchDocument({
						document: {
							description: '' + i,
						},
						documentId: fileEntry.id,
					});
				}

				return fileEntry.title;
			});

		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.goToViewHistoryFileEntry(fileEntryTitle);

		await setItemsPerPage(page, 20);

		await expect(
			page.getByText('Showing 1 to 20 of 21 entries.', {exact: true})
		).toBeVisible();

		await expect(page.locator('td.lfr-version-column').nth(0)).toHaveText(
			'1.20'
		);
	}
);

test(
	'File Entry History lists the version information, orders the versions and offers the version actions',
	{tag: '@LPS-164334'},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const fileEntry = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(path.join(__dirname, '/dependencies/image1.jpeg'))
		);

		for (let i = 0; i < 11; i++) {
			await apiHelpers.headlessDelivery.patchDocument({
				document: {description: String(i)},
				documentId: fileEntry.id,
			});
		}

		const singleVersionFileEntry =
			await apiHelpers.headlessDelivery.postDocument(
				site.id,
				createReadStream(
					path.join(__dirname, '/dependencies/image1.jpeg')
				)
			);

		const getVersionRow = (version: string) =>
			page.getByRole('row').filter({
				has: page.locator('td.lfr-version-column', {
					hasText: new RegExp(
						`^\\s*${version.replace('.', '\\.')}\\s*$`
					),
				}),
			});

		const openVersionActions = (row: Locator) =>
			clickAndExpectToBeVisible({
				target: page.getByRole('menuitem', {name: 'View'}),
				trigger: row.getByRole('button', {name: 'Show Actions'}),
			});

		await documentLibraryPage.goto(site.friendlyUrlPath);
		await documentLibraryPage.goToViewHistoryFileEntry(fileEntry.title);

		await test.step('DocumentsVersionHistoryInformation', async () => {
			for (const columnHeader of [
				'Author',
				'ID',
				'Modified Date',
				'Status',
				'Title',
				'Version',
			]) {
				await expect(
					page.locator('thead').getByRole('cell', {
						exact: true,
						name: columnHeader,
					})
				).toBeVisible();
			}
		});

		await test.step('DocumentsVersionHistoryOrdered', async () => {
			await expect(page.locator('td.lfr-version-column')).toHaveText([
				'1.11',
				'1.10',
				'1.9',
				'1.8',
				'1.7',
				'1.6',
				'1.5',
				'1.4',
				'1.3',
				'1.2',
				'1.1',
				'1.0',
			]);
		});

		await test.step('DocumentVersionHistoryKebabOptions', async () => {
			for (const {actionNames, missingActionNames, version} of [
				{
					actionNames: ['Delete', 'Download', 'View'],
					missingActionNames: ['Revert'],
					version: '1.11',
				},
				{
					actionNames: ['Delete', 'Download', 'Revert', 'View'],
					missingActionNames: [],
					version: '1.0',
				},
			]) {
				await documentLibraryPage.goto(site.friendlyUrlPath);
				await documentLibraryPage.goToViewHistoryFileEntry(
					fileEntry.title
				);

				await openVersionActions(getVersionRow(version));

				for (const actionName of actionNames) {
					await expect(
						page.getByRole('menuitem', {name: actionName})
					).toBeVisible();
				}

				for (const actionName of missingActionNames) {
					await expect(
						page.getByRole('menuitem', {name: actionName})
					).toHaveCount(0);
				}
			}

			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.goToViewHistoryFileEntry(
				singleVersionFileEntry.title
			);

			await openVersionActions(getVersionRow('1.0'));

			for (const actionName of ['Download', 'View']) {
				await expect(
					page.getByRole('menuitem', {name: actionName})
				).toBeVisible();
			}
		});

		await test.step('DocumentsVersionHistoryDeleteVersion', async () => {
			await documentLibraryPage.goto(site.friendlyUrlPath);
			await documentLibraryPage.goToViewHistoryFileEntry(fileEntry.title);

			page.once('dialog', (dialog) => dialog.accept());

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: 'Delete'}),
				trigger: getVersionRow('1.2').getByRole('button', {
					name: 'Actions',
				}),
			});

			await waitForAlert(page);

			await expect(getVersionRow('1.2')).toHaveCount(0);

			await expect(getVersionRow('1.1')).toBeVisible();
		});
	}
);
