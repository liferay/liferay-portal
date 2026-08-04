/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {collectionsPagesTest} from '../../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getGlobalSite from '../../../../utils/getGlobalSite';
import getRandomString from '../../../../utils/getRandomString';
import {
	performUserSwitchViaApi,
	userData,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';

const test = mergeTests(
	loginTest(),
	isolatedSiteTest,
	dataApiHelpersTest,
	collectionsPagesTest
);

test.describe('Source', () => {
	test(
		'Renders every section when the selected item subtype is not viewable',
		{tag: '@LPD-100415'},
		async ({apiHelpers, collectionsPage, page, site}) => {
			const collectionName = getRandomString();
			const documentTypeName = getRandomString();

			let documentTypeId: string;
			let screenName: string;

			await test.step('Create a document type in the Global site', async () => {
				const globalSite = await getGlobalSite(apiHelpers);

				const documentType =
					await apiHelpers.headlessDelivery.postSiteDocumentDataDefinitionType(
						globalSite.groupId,
						documentTypeName
					);

				documentTypeId = String(documentType.id);
			});

			await test.step('Create a dynamic collection filtered by that document type', async () => {
				await collectionsPage.goto(site.friendlyUrlPath);

				await collectionsPage.addNewDynamicCollection(collectionName);

				await collectionsPage.configureSourceItemType({
					itemSubtype: documentTypeName,
					itemType: 'Document',
				});
			});

			await test.step('Create a user who administers the site', async () => {
				const user =
					await apiHelpers.headlessAdminUser.postUserAccount();

				screenName = user.alternateName || '';

				userData[screenName] = {
					name: user.givenName,
					password: 'test',
					surname: user.familyName,
				};

				await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);

				await apiHelpers.jsonWebServicesUser.answerReminderQuery(
					user.id
				);

				const role =
					await apiHelpers.headlessAdminUser.getRoleByName(
						'Site Administrator'
					);

				await apiHelpers.headlessAdminUser.assignUserToSite(
					role.id,
					site.id,
					user.id
				);
			});

			await test.step('Open the collection as that user', async () => {
				await performUserSwitchViaApi(page, screenName);

				await collectionsPage.goto(site.friendlyUrlPath);

				await collectionsPage.openCollection(collectionName);
			});

			await test.step('Every section is rendered', async () => {
				for (const section of [
					'Source',
					'Scope',
					'Filter',
					'Ordering',
				]) {
					await expect(
						page.getByRole('button', {name: section})
					).toBeVisible();
				}
			});

			await test.step('Save the collection', async () => {
				await page.getByRole('button', {name: 'Save'}).click();

				await waitForAlert(page);
			});

			await test.step('The item subtype is visible in the save', async () => {
				await performUserSwitchViaApi(page, 'test');

				await collectionsPage.goto(site.friendlyUrlPath);

				await collectionsPage.openCollection(collectionName);

				await expect(
					page
						.locator('.asset-subtype:not(.hide)')
						.getByLabel('Item Subtype')
				).toHaveValue(documentTypeId);
			});
		}
	);
});
