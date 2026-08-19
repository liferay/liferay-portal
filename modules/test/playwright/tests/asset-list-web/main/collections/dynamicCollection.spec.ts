/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {collectionsPagesTest} from '../../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {CollectionsPage} from '../../../../pages/asset-list-web/CollectionsPage';
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

const testWithEnhancedFiltering = mergeTests(
	loginTest(),
	featureFlagsTest({
		'LPD-74731': {enabled: true}, // Enhanced Filtering
		'LPS-178052': {enabled: true}, // CMS Objects
	}),
	isolatedSiteTest,
	dataApiHelpersTest,
	collectionsPagesTest
);

const CMS_BASIC_WEB_CONTENT = 'Basic Web Content';
const CMS_BLOG = 'Blog';
const CMS_WEB_CONTENT_APPLICATION = 'cms/basic-web-contents';

/**
 * Reopens the collection and returns the frame listing the items it resolves
 * to. Every check starts from a fresh page load so no modal state carries over
 * between retries.
 */
async function gotoViewItems(
	collectionsPage: CollectionsPage,
	site: {friendlyUrlPath: string},
	collectionName: string
) {
	await collectionsPage.goto(site.friendlyUrlPath);

	await collectionsPage.openCollection(collectionName);

	return await collectionsPage.openViewItems();
}

testWithEnhancedFiltering.describe(
	'Enhanced Filtering with CMS Objects',
	() => {
		testWithEnhancedFiltering(
			"Applies a filter for CMS Object based on type's own field",
			{tag: '@LPD-88609'},
			async ({apiHelpers, collectionsPage, site}) => {
				testWithEnhancedFiltering.setTimeout(240000);

				const collectionName = getRandomString();
				const titles = [
					`Alpha ${getRandomString()}`,
					`Beta ${getRandomString()}`,
				];

				let spaceName: string;

				await testWithEnhancedFiltering.step(
					'Create a Space holding two basic web contents',
					async () => {
						const space =
							await apiHelpers.headlessAssetLibrary.createAssetLibrary(
								{
									name: `Space ${getRandomString()}`,
									settings: {},
									type: 'Space',
								}
							);

						spaceName = space.name;

						for (const title of titles) {
							await apiHelpers.objectEntry.postObjectEntry(
								{
									content: `<p>${title} body</p>`,
									objectEntryFolderExternalReferenceCode:
										'L_CONTENTS',
									title,
								},
								CMS_WEB_CONTENT_APPLICATION,
								space.name
							);
						}

						await apiHelpers.headlessAssetLibrary.connectSite(
							space.externalReferenceCode,
							site.externalReferenceCode,
							{searchable: true}
						);
					}
				);

				await testWithEnhancedFiltering.step(
					'Create a dynamic collection scoped to the Space',
					async () => {
						await collectionsPage.goto(site.friendlyUrlPath);

						await collectionsPage.addNewDynamicCollection(
							collectionName
						);

						await collectionsPage.configureSourceItemType({
							itemType: CMS_BASIC_WEB_CONTENT + ' (CMS)',
						});

						await collectionsPage.scopeToSpace(spaceName);

						await collectionsPage.save();
					}
				);

				await testWithEnhancedFiltering.step(
					'Both basic web contents are available before any filter is added',
					async () => {
						const viewItemsFrame = await gotoViewItems(
							collectionsPage,
							site,
							collectionName
						);

						for (const title of titles) {
							await expect(
								viewItemsFrame.getByText(title)
							).toBeVisible({timeout: 5000});
						}
					}
				);

				await testWithEnhancedFiltering.step(
					'Filter the collection by title',
					async () => {
						await collectionsPage.goto(site.friendlyUrlPath);

						await collectionsPage.openCollection(collectionName);

						await collectionsPage.addFilterConditions([
							{
								field: 'Title',
								fieldGroup: CMS_BASIC_WEB_CONTENT,
								operator: 'Contains',
								quantifier: 'Any of the Following',
								value: 'Nothing',
							},
						]);

						await collectionsPage.save();
					}
				);

				await testWithEnhancedFiltering.step(
					'Neither basic web content is available while the filter applies',
					async () => {
						const viewItemsFrame = await gotoViewItems(
							collectionsPage,
							site,
							collectionName
						);

						for (const title of titles) {
							await expect(
								viewItemsFrame.getByText(title)
							).toHaveCount(0);
						}
					}
				);
			}
		);

		testWithEnhancedFiltering(
			'Resets the ordering column that the selected item type does not support',
			{tag: '@LPD-102710'},
			async ({collectionsPage, page, site}) => {
				const collectionName = getRandomString();

				const getOrderByColumn = async (index: number) =>
					await page
						.locator(
							`input[name$="TypeSettingsProperties--orderByColumn${index}--"]`
						)
						.inputValue();

				await testWithEnhancedFiltering.step(
					'Order a collection by a field of its item type, then by a common field',
					async () => {
						await collectionsPage.goto(site.friendlyUrlPath);

						await collectionsPage.addNewDynamicCollection(
							collectionName
						);

						await collectionsPage.configureSourceItemType({
							itemType: CMS_BASIC_WEB_CONTENT + ' (CMS)',
						});

						await collectionsPage.setOrderByColumn({
							column: 'Order By',
							field: 'Title',
							fieldGroup: CMS_BASIC_WEB_CONTENT,
						});

						await collectionsPage.setOrderByColumn({
							column: 'And Then By',
							field: 'Created Date',
							fieldGroup: 'Common Fields',
						});

						await collectionsPage.save();
					}
				);

				await testWithEnhancedFiltering.step(
					'Both orderings are saved',
					async () => {
						await collectionsPage.goto(site.friendlyUrlPath);

						await collectionsPage.openCollection(collectionName);

						// A column on a field of the item type is saved as JSON
						// carrying the propertyName, classNameId, classTypeId.

						expect(await getOrderByColumn(1)).toContain(
							'"propertyName":"title"'
						);
						expect(await getOrderByColumn(2)).toBe('createDate');
					}
				);

				await testWithEnhancedFiltering.step(
					'Change the item type',
					async () => {
						await collectionsPage.configureSourceItemType({
							itemSubtype: 'Basic Web Content',
							itemType: 'Web Content Article',
						});

						await collectionsPage.save();
					}
				);

				await testWithEnhancedFiltering.step(
					'The item type specific ordering falls back to the default and the common field is kept',
					async () => {
						await collectionsPage.goto(site.friendlyUrlPath);

						await collectionsPage.openCollection(collectionName);

						expect(await getOrderByColumn(1)).toBe('modifiedDate');
						expect(await getOrderByColumn(2)).toBe('createDate');
					}
				);
			}
		);

		testWithEnhancedFiltering(
			'Discards a filter that the selected item type no longer displays',
			{tag: '@LPD-102710'},
			async ({collectionsPage, page, site}) => {
				const collectionName = getRandomString();

				const getFilters = async () =>
					JSON.parse(
						await page
							.locator(
								`input[name$="TypeSettingsProperties--filters--"]`
							)
							.inputValue()
					);

				await testWithEnhancedFiltering.step(
					'Filter a collection by a field of its item type and by a common field',
					async () => {
						await collectionsPage.goto(site.friendlyUrlPath);

						await collectionsPage.addNewDynamicCollection(
							collectionName
						);

						await collectionsPage.configureSourceItemType({
							itemType: CMS_BASIC_WEB_CONTENT + ' (CMS)',
						});

						await collectionsPage.addFilterConditions([
							{
								field: 'Title',
								fieldGroup: CMS_BASIC_WEB_CONTENT,
								operator: 'Contains',
								quantifier: 'Any of the Following',
								value: 'nothing',
							},
							{
								field: 'Author Name',
								fieldGroup: 'Common Fields',
								operator: 'Contains',
								quantifier: 'Any of the Following',
								value: 'nobody',
							},
						]);

						await collectionsPage.save();
					}
				);

				await testWithEnhancedFiltering.step(
					'Both filters are saved while they are displayed',
					async () => {
						await collectionsPage.goto(site.friendlyUrlPath);

						await collectionsPage.openCollection(collectionName);

						expect(await getFilters()).toHaveLength(2);
					}
				);

				await testWithEnhancedFiltering.step(
					'Change the item type to another CMS object',
					async () => {
						await collectionsPage.configureSourceItemType({
							itemType: CMS_BLOG + ' (CMS)',
						});

						await collectionsPage.save();
					}
				);

				await testWithEnhancedFiltering.step(
					'Only the filter on the previous item type field is discarded',
					async () => {
						await collectionsPage.goto(site.friendlyUrlPath);

						await collectionsPage.openCollection(collectionName);

						const filters = await getFilters();

						expect(filters).toHaveLength(1);
						expect(filters[0].propertyName).toBe('userName');
						expect(filters[0].classNameId).toBeUndefined();
					}
				);

				await testWithEnhancedFiltering.step(
					'Change the item type to one that does not display the enhanced filter',
					async () => {
						await collectionsPage.configureSourceItemType({
							itemSubtype: 'Basic Web Content',
							itemType: 'Web Content Article',
						});

						await collectionsPage.save();
					}
				);

				await testWithEnhancedFiltering.step(
					'The filter on the common field is discarded too',
					async () => {
						await collectionsPage.goto(site.friendlyUrlPath);

						await collectionsPage.openCollection(collectionName);

						expect(await getFilters()).toEqual([]);
					}
				);
			}
		);
	}
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

				await collectionsPage.save();
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
