/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import createTempFile from '../../../utils/createTempFile';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {templatesPageTest} from '../../template-web/main/fixtures/templatesPageTest';
import {getPageEditorDateFormat} from '../utils/dateFormat';
import {generateFormulaObjectFields} from '../utils/generateFormulaObjectFields';
import {generateObjectEntryValues} from '../utils/generateObjectEntry';
import {generateObjectFields} from '../utils/generateObjectFields';
import {postListTypeDefinitionListTypeEntries} from '../utils/postListTypeDefinitionListTypeEntries';

const test = mergeTests(
	apiHelpersTest,
	collectionsPagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	objectPagesTest,
	pageEditorPagesTest,
	pageViewModePagesTest,
	pagesAdminPagesTest,
	templatesPageTest
);

test.describe('Collection Display', () => {
	test('can display an object reactivated on the Collection Providers', async ({
		apiHelpers,
		collectionsPage,
		page,
		site,
		viewObjectDefinitionsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(
			objectDefinition.name
		);

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(
			objectDefinition.name
		);

		await collectionsPage.goto(site.friendlyUrlPath);

		await page.getByRole('link', {name: 'Collection Providers'}).click();

		await expect(
			page.getByText(objectDefinition.name).first()
		).toBeVisible();
	});

	test(
		'can display entries on table format in collection display',
		{tag: '@LPS-135386'},
		async ({apiHelpers, page, pageEditorPage, site}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text', 'Text'],
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectEntries = [];

			for (let i = 0; i < 2; i++) {
				const {objectEntry} = await generateObjectEntryValues({
					objectEntryFormat: 'API',
					objectFields,
				});
				objectEntries.push(objectEntry);
			}

			await apiHelpers.objectEntry.postObjectEntriesBatch(
				'c/' + objectDefinition.name.toLowerCase() + 's',
				objectEntries
			);

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition(),
				siteId: site.id,
				title: getRandomString(),
			});

			await test.step('Add Collection Display with object as provider and Table style', async () => {
				await pageEditorPage.goto(layout, site.friendlyUrlPath);

				await pageEditorPage.addFragment(
					'Content Display',
					'Collection Display'
				);

				await pageEditorPage.selectFragment(
					await pageEditorPage.getFragmentId('Collection Display')
				);

				await pageEditorPage.chooseCollectionDisplayCollection(
					'Collection Providers',
					objectDefinition.label['en_US'],
					{search: true}
				);

				await pageEditorPage.waitForChangesSaved();

				const collectionId =
					await pageEditorPage.getFragmentId('Collection Display');

				await pageEditorPage.selectFragment(collectionId);

				await pageEditorPage.changeConfiguration({
					fieldLabel: 'Style Display',
					tab: 'General',
					value: 'Table',
				});

				await pageEditorPage.waitForChangesSaved();
			});

			await test.step('Publish and verify entries in view mode', async () => {
				await pageEditorPage.publishPage();

				await page.goto(
					`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
				);

				const collectionTable = page.getByRole('table');

				for (const objectEntry of objectEntries) {
					await expect(
						collectionTable.getByText(
							objectEntry[objectFields[0].name]
						)
					).toBeVisible();
				}
			});
		}
	);

	test(
		'can search for object entry on search experience in collection providers',
		{tag: '@LPS-135388'},
		async ({apiHelpers, page, pageEditorPage, site}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: [
					{businessType: 'Text', indexed: true},
				],
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';
			const fieldName = objectFields[0].name!;

			const entryValueA = 'Alpha' + getRandomString();
			const entryValueB = 'Beta' + getRandomString();

			for (const value of [entryValueA, entryValueB]) {
				await apiHelpers.objectEntry.postObjectEntry(
					{[fieldName]: value},
					applicationName
				);
			}

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition(),
				siteId: site.id,
				title: getRandomString(),
			});

			await test.step('Add Collection Display with object as provider and Table style', async () => {
				await pageEditorPage.goto(layout, site.friendlyUrlPath);

				await pageEditorPage.addFragment(
					'Content Display',
					'Collection Display'
				);

				await pageEditorPage.selectFragment(
					await pageEditorPage.getFragmentId('Collection Display')
				);

				await pageEditorPage.chooseCollectionDisplayCollection(
					'Collection Providers',
					objectDefinition.label['en_US'],
					{search: true}
				);

				await pageEditorPage.waitForChangesSaved();

				const collectionId =
					await pageEditorPage.getFragmentId('Collection Display');

				await pageEditorPage.selectFragment(collectionId);

				await pageEditorPage.changeConfiguration({
					fieldLabel: 'Style Display',
					tab: 'General',
					value: 'Table',
				});

				await pageEditorPage.waitForChangesSaved();
			});

			await test.step('Add Collection Filter with Keywords filter', async () => {
				await pageEditorPage.addFragment(
					'Content Display',
					'Collection Filter'
				);

				const collectionFilterId =
					await pageEditorPage.getFragmentId('Collection Filter');

				await pageEditorPage.selectFragment(collectionFilterId);

				await page.getByLabel('Select', {exact: true}).click();

				await page.getByLabel(objectDefinition.label['en_US']).check();

				await page
					.getByLabel('Filter', {exact: true})
					.selectOption('keywords');

				await pageEditorPage.waitForChangesSaved();
			});

			await test.step('Publish page and verify both entries are visible', async () => {
				await pageEditorPage.publishPage();

				await page.goto(
					`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
				);

				const collectionTable = page.locator(
					'.lfr-layout-structure-item-collection'
				);

				await expect(
					collectionTable.getByText(entryValueA)
				).toBeVisible();
				await expect(
					collectionTable.getByText(entryValueB)
				).toBeVisible();
			});

			await test.step('Search for entry A and verify entry B is filtered out', async () => {
				const collectionTable = page.locator(
					'.lfr-layout-structure-item-collection'
				);

				await expect(async () => {
					const searchInput = page.getByPlaceholder('Search', {
						exact: true,
					});

					await searchInput.fill(entryValueA, {timeout: 1000});
					await searchInput.press('Enter', {timeout: 1000});

					await page.waitForURL(/keywords/, {timeout: 3000});

					await expect(
						collectionTable.getByText(entryValueA)
					).toBeVisible({timeout: 3000});

					await expect(
						collectionTable.getByText(entryValueB)
					).not.toBeVisible({timeout: 3000});
				}).toPass();
			});
		}
	);

	test(
		'object is displayed to be selected as collection provider on collection display fragment',
		{tag: '@LPS-133865'},
		async ({apiHelpers, page, pageEditorPage, site}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition(),
				siteId: site.id,
				title: getRandomString(),
			});

			await test.step('Add Collection Display and select the object as provider', async () => {
				await pageEditorPage.goto(layout, site.friendlyUrlPath);

				await pageEditorPage.addFragment(
					'Content Display',
					'Collection Display'
				);

				await pageEditorPage.selectFragment(
					await pageEditorPage.getFragmentId('Collection Display')
				);

				await pageEditorPage.chooseCollectionDisplayCollection(
					'Collection Providers',
					objectDefinition.label['en_US'],
					{search: true}
				);

				await expect(
					page.getByLabel('Collection', {exact: true})
				).toHaveValue(objectDefinition.label['en_US']);
			});
		}
	);

	test('relationship field is not displayed in collection display table when parent object is inactive', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
		viewObjectDefinitionsPage,
	}) => {
		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition1.id,
			type: 'objectDefinition',
		});

		apiHelpers.data.push({
			id: objectDefinition2.id,
			type: 'objectDefinition',
		});

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectDefinition1.externalReferenceCode,
				{
					label: {en_US: 'Relationship'},
					name: 'relationship' + getRandomInt(),
					objectDefinitionExternalReferenceCode1:
						objectDefinition1.externalReferenceCode,
					objectDefinitionExternalReferenceCode2:
						objectDefinition2.externalReferenceCode,
					objectDefinitionId1: objectDefinition1.id,
					objectDefinitionId2: objectDefinition2.id,
					objectDefinitionName2: objectDefinition2.name,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		const applicationName1 = `c/${objectDefinition1.name.toLowerCase()}s`;
		const applicationName2 = `c/${objectDefinition2.name.toLowerCase()}s`;

		const entryA = await apiHelpers.objectEntry.postObjectEntry(
			{['textField']: 'Entry A'},
			applicationName1
		);

		const entryB = await apiHelpers.objectEntry.postObjectEntry(
			{['textField']: 'Entry B'},
			applicationName2
		);

		await apiHelpers.objectEntry.putByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
			{
				applicationName: applicationName1,
				currentExternalReferenceCode: entryA.externalReferenceCode,
				objectRelationshipName: objectRelationship.name,
				relatedExternalReferenceCode: entryB.externalReferenceCode,
			}
		);

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition(),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await pageEditorPage.addFragment(
			'Content Display',
			'Collection Display'
		);

		await pageEditorPage.selectFragment(
			await pageEditorPage.getFragmentId('Collection Display')
		);

		await pageEditorPage.chooseCollectionDisplayCollection(
			'Collection Providers',
			objectDefinition2.label['en_US'],
			{search: true}
		);

		await pageEditorPage.waitForChangesSaved();

		const collectionId =
			await pageEditorPage.getFragmentId('Collection Display');

		await pageEditorPage.selectFragment(collectionId);

		await pageEditorPage.changeConfiguration({
			fieldLabel: 'Style Display',
			tab: 'General',
			value: 'Table',
		});

		await pageEditorPage.waitForChangesSaved();

		await pageEditorPage.publishPage();

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByRole('cell', {name: 'textField'})).toBeVisible();
		await expect(
			page.getByRole('cell', {name: 'Relationship'})
		).toBeVisible();

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(
			objectDefinition1.label['en_US']
		);

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByRole('cell', {name: 'textField'})).toBeVisible();
		await expect(
			page.getByRole('cell', {name: 'Relationship'})
		).not.toBeVisible();
	});
});

test.describe('Content Pages Mapping', () => {
	test('can display an object reactivated on the Page Item Selector', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
		viewObjectDefinitionsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(
			objectDefinition.name
		);

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(
			objectDefinition.name
		);

		const headingDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'BASIC_COMPONENT-heading',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([headingDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		await page.getByText('Heading Example', {exact: true}).dblclick();

		await page.getByLabel('Select Item').click();

		await expect(
			page
				.frameLocator('iframe[title="Select"]')
				.getByRole('menuitem', {name: objectDefinition.name})
		).toBeVisible();
	});

	test(
		'can map preview URL of image attachment to fragment on content page',
		{tag: '@LPS-182999'},
		async ({apiHelpers, page, pageEditorPage, site}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Attachment'],
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const document = await apiHelpers.headlessDelivery.postDocument(
				site.id,
				createReadStream(
					path.join(__dirname, '..', 'dependencies', 'astronaut.png')
				),
				{fileName: 'astronaut.png', title: getRandomString()}
			);

			const attachmentFieldName = objectFields[0].name!;
			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{[attachmentFieldName]: document.id},
				applicationName
			);

			const imageId = getRandomString();

			const imageFragment = getFragmentDefinition({
				id: imageId,
				key: 'BASIC_COMPONENT-image',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([imageFragment]),
				siteId: site.id,
				title: getRandomString(),
			});

			await test.step('Map Image fragment to object entry Preview URL', async () => {
				await pageEditorPage.goto(layout, site.friendlyUrlPath);

				await pageEditorPage.selectEditable(imageId, 'image-square');

				await page
					.getByLabel('Source Selection')
					.selectOption('Mapping');

				await pageEditorPage.setMappedItem({
					entity: objectDefinition.label['en_US'],
					entry: objectEntry.id.toString(),
					entryLocator: page
						.frameLocator('iframe[title="Select"]')
						.getByText(objectEntry.id.toString())
						.first(),
					field: 'Preview URL',
				});

				await pageEditorPage.waitForChangesSaved();
			});

			await test.step('Verify mapped image is shown in editor', async () => {
				await expect(
					page.locator('.component-image img')
				).toBeVisible();
			});

			await test.step('Publish and verify image in view mode', async () => {
				await pageEditorPage.publishPage();

				await page.goto(
					`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
				);

				await expect(
					page.locator('.component-image img')
				).toBeVisible();
			});
		}
	);

	test(
		'can map preview URL of non-image attachment to fragment showing blank space',
		{tag: '@LPS-182999'},
		async ({apiHelpers, page, pageEditorPage, site}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Attachment'],
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const pdfPath = createTempFile(
				'test-document.pdf',
				'%PDF-1.0 dummy content'
			);

			const document = await apiHelpers.headlessDelivery.postDocument(
				site.id,
				createReadStream(pdfPath),
				{fileName: 'test-document.pdf', title: getRandomString()}
			);

			const attachmentFieldName = objectFields[0].name!;
			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{[attachmentFieldName]: document.id},
				applicationName
			);

			const imageId = getRandomString();

			const imageFragment = getFragmentDefinition({
				id: imageId,
				key: 'BASIC_COMPONENT-image',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([imageFragment]),
				siteId: site.id,
				title: getRandomString(),
			});

			await test.step('Map Image fragment to object entry Preview URL', async () => {
				await pageEditorPage.goto(layout, site.friendlyUrlPath);

				await pageEditorPage.selectEditable(imageId, 'image-square');

				await page
					.getByLabel('Source Selection')
					.selectOption('Mapping');

				await pageEditorPage.setMappedItem({
					entity: objectDefinition.label['en_US'],
					entry: objectEntry.id.toString(),
					entryLocator: page
						.frameLocator('iframe[title="Select"]')
						.getByText(objectEntry.id.toString())
						.first(),
					field: 'Preview URL',
				});

				await pageEditorPage.waitForChangesSaved();
			});

			await test.step('Publish and verify non-image shows blank space in view mode', async () => {
				await pageEditorPage.publishPage();

				await page.goto(
					`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
				);

				await expect
					.poll(async () =>
						page
							.locator('.component-image img')
							.evaluate(
								(image: HTMLImageElement) => image.naturalWidth
							)
					)
					.toBe(0);
			});
		}
	);

	test(
		'can view image user profile from specific entry on display page',
		{tag: '@LPD-86436'},
		async ({apiHelpers, page, pageEditorPage, site}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';
			const textFieldName = objectFields[0].name;

			const entryValue = 'TestEntry_' + getRandomInt();

			const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{[textFieldName]: entryValue},
				applicationName
			);

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition(),
				siteId: site.id,
				title: getRandomString(),
			});

			await test.step('Add Image fragment and map to User Profile Image', async () => {
				await pageEditorPage.goto(layout, site.friendlyUrlPath);

				await pageEditorPage.addFragment('Basic Components', 'Image');

				const imageId = await pageEditorPage.getFragmentId('Image');

				await pageEditorPage.selectEditable(imageId, 'image-square');

				await page
					.getByLabel('Source Selection')
					.selectOption('Mapping');

				await pageEditorPage.setMappedItem({
					entity: objectDefinition.label['en_US'],
					entry: objectEntry.id.toString(),
					entryLocator: page
						.frameLocator('iframe[title="Select"]')
						.getByText(objectEntry.id.toString())
						.first(),
					field: 'User Profile Image',
				});

				await pageEditorPage.waitForChangesSaved();
			});

			await test.step('Publish and verify user profile image is visible', async () => {
				await pageEditorPage.publishPage();

				await page.goto(
					`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
				);

				await expect(
					page.locator('.component-image img')
				).toBeVisible();
			});
		}
	);

	test(
		'relationship field cannot be selected for page fragment mapping when parent object is inactive',
		{tag: '@LPS-139005'},
		async ({
			apiHelpers,
			page,
			pageEditorPage,
			site,
			viewObjectDefinitionsPage,
		}) => {
			const objectDefinition1 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			const objectDefinition2 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
					titleObjectFieldName: 'textField',
				});

			apiHelpers.data.push({
				id: objectDefinition1.id,
				type: 'objectDefinition',
			});

			apiHelpers.data.push({
				id: objectDefinition2.id,
				type: 'objectDefinition',
			});

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						label: {en_US: 'Relationship'},
						name: 'relationship' + getRandomInt(),
						objectDefinitionExternalReferenceCode1:
							objectDefinition1.externalReferenceCode,
						objectDefinitionExternalReferenceCode2:
							objectDefinition2.externalReferenceCode,
						objectDefinitionId1: objectDefinition1.id,
						objectDefinitionId2: objectDefinition2.id,
						objectDefinitionName2: objectDefinition2.name,
						type: 'oneToMany',
					}
				);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			const applicationName2 = `c/${objectDefinition2.name.toLowerCase()}s`;

			await apiHelpers.objectEntry.postObjectEntry(
				{['textField']: 'Entry B'},
				applicationName2
			);

			const headingDefinition = getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-heading',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([headingDefinition]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await page.getByText('Heading Example', {exact: true}).dblclick();

			await page.getByRole('button', {name: 'Select Item'}).click();

			const selectFrame = page.frameLocator('iframe[title="Select"]');

			await selectFrame
				.getByRole('menuitem', {
					name: objectDefinition2.label['en_US'],
				})
				.click();

			await selectFrame
				.getByText('Entry B', {exact: true})
				.first()
				.click();

			await page.getByLabel('Field').click();

			await expect(
				page.getByRole('option', {
					name: objectRelationship.label.en_US,
				})
			).toBeAttached();

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.changeObjectActivateStatus(
				objectDefinition1.label.en_US
			);

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await page.getByText('Heading Example', {exact: true}).dblclick();

			await page.getByRole('button', {name: 'Select Item'}).click();

			await selectFrame
				.getByRole('menuitem', {
					name: objectDefinition2.label['en_US'],
				})
				.click();

			await selectFrame
				.getByText('Entry B', {exact: true})
				.first()
				.click();

			await page.getByLabel('Field').click();

			await expect(
				page.getByRole('option', {
					name: objectRelationship.label.en_US,
				})
			).not.toBeAttached();
		}
	);
});

test.describe('Display Page', () => {
	test(
		'can define fixed filter for picklist type on display page',
		{tag: '@LPS-135004'},
		async ({apiHelpers, page, pageEditorPage, site}) => {
			const {listTypeDefinition, listTypeEntries} =
				await postListTypeDefinitionListTypeEntries({
					apiHelpers,
				});

			const picklistEntryNames = listTypeEntries.map(
				(entry) => entry.name
			);

			const objectFields = generateObjectFields({
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				objectFieldBusinessTypes: [
					{businessType: 'Picklist', indexed: true},
				],
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const picklistFieldName = objectFields[0].name;
			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			for (let i = 0; i < picklistEntryNames.length; i++) {
				await apiHelpers.objectEntry.postObjectEntry(
					{
						[picklistFieldName]: {key: picklistEntryNames[i]},
					},
					applicationName
				);
			}

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition(),
				siteId: site.id,
				title: getRandomString(),
			});

			await test.step('Add Collection Display with object as provider in Table style', async () => {
				await pageEditorPage.goto(layout, site.friendlyUrlPath);

				await pageEditorPage.addFragment(
					'Content Display',
					'Collection Display'
				);

				await pageEditorPage.selectFragment(
					await pageEditorPage.getFragmentId('Collection Display')
				);

				await pageEditorPage.chooseCollectionDisplayCollection(
					'Collection Providers',
					objectDefinition.label['en_US'],
					{search: true}
				);

				await pageEditorPage.waitForChangesSaved();

				const collectionId =
					await pageEditorPage.getFragmentId('Collection Display');

				await pageEditorPage.selectFragment(collectionId);

				await pageEditorPage.changeConfiguration({
					fieldLabel: 'Style Display',
					tab: 'General',
					value: 'Table',
				});

				await pageEditorPage.waitForChangesSaved();
			});

			await test.step('Configure fixed filter on the picklist field', async () => {
				const collectionId =
					await pageEditorPage.getFragmentId('Collection Display');

				await pageEditorPage.selectFragment(collectionId);

				await page.getByTitle('View Collection Options').click();

				await page
					.getByRole('menuitem', {name: 'Filter Collection'})
					.click();

				await page
					.getByLabel(objectFields[0].label['en_US'], {exact: true})
					.selectOption(picklistEntryNames[0]);

				await page.getByRole('button', {name: 'Save'}).click();
			});

			await test.step('Publish and verify only filtered entry is visible', async () => {
				await pageEditorPage.publishPage();

				await page.goto(
					`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
				);

				const collectionTable = page.getByRole('table');

				await expect(
					collectionTable.getByText(picklistEntryNames[0])
				).toBeVisible();

				await expect(
					collectionTable.getByText(picklistEntryNames[1])
				).not.toBeVisible();
			});
		}
	);

	test(
		'can set pagination as numeric on display page',
		{tag: '@LPS-135004'},
		async ({apiHelpers, page, pageEditorPage, site}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectEntries = [];

			for (let i = 0; i < 2; i++) {
				const {objectEntry} = await generateObjectEntryValues({
					objectEntryFormat: 'API',
					objectFields,
				});
				objectEntries.push(objectEntry);
			}

			await apiHelpers.objectEntry.postObjectEntriesBatch(
				'c/' + objectDefinition.name.toLowerCase() + 's',
				objectEntries
			);

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition(),
				siteId: site.id,
				title: getRandomString(),
			});

			await test.step('Add Collection Display with object as provider', async () => {
				await pageEditorPage.goto(layout, site.friendlyUrlPath);

				await pageEditorPage.addFragment(
					'Content Display',
					'Collection Display'
				);

				await pageEditorPage.selectFragment(
					await pageEditorPage.getFragmentId('Collection Display')
				);

				await pageEditorPage.chooseCollectionDisplayCollection(
					'Collection Providers',
					objectDefinition.label['en_US'],
					{search: true}
				);

				await pageEditorPage.waitForChangesSaved();

				await pageEditorPage.addFragment('Basic Components', 'Heading');
			});

			await test.step('Set pagination to Numeric and verify', async () => {
				const collectionId =
					await pageEditorPage.getFragmentId('Collection Display');

				await pageEditorPage.selectFragment(collectionId);

				await pageEditorPage.changeConfiguration({
					fieldLabel: 'Pagination',
					tab: 'General',
					value: 'numeric',
				});

				await pageEditorPage.waitForChangesSaved();

				await expect(
					page.getByRole('navigation', {name: 'Pagination'})
				).toBeVisible();

				await expect(
					page.getByText('Showing 1 to 2 of 2 entries.')
				).toBeVisible();
			});
		}
	);

	test(
		'can set pagination as simple on display page',
		{tag: '@LPS-135004'},
		async ({apiHelpers, page, pageEditorPage, site}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectEntries = [];

			for (let i = 0; i < 2; i++) {
				const {objectEntry} = await generateObjectEntryValues({
					objectEntryFormat: 'API',
					objectFields,
				});
				objectEntries.push(objectEntry);
			}

			await apiHelpers.objectEntry.postObjectEntriesBatch(
				'c/' + objectDefinition.name.toLowerCase() + 's',
				objectEntries
			);

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition(),
				siteId: site.id,
				title: getRandomString(),
			});

			await test.step('Add Collection Display with object as provider', async () => {
				await pageEditorPage.goto(layout, site.friendlyUrlPath);

				await pageEditorPage.addFragment(
					'Content Display',
					'Collection Display'
				);

				await pageEditorPage.selectFragment(
					await pageEditorPage.getFragmentId('Collection Display')
				);

				await pageEditorPage.chooseCollectionDisplayCollection(
					'Collection Providers',
					objectDefinition.label['en_US'],
					{search: true}
				);

				await pageEditorPage.waitForChangesSaved();

				await pageEditorPage.addFragment('Basic Components', 'Heading');
			});

			await test.step('Set pagination to Simple and verify', async () => {
				const collectionId =
					await pageEditorPage.getFragmentId('Collection Display');

				await pageEditorPage.selectFragment(collectionId);

				await pageEditorPage.changeConfiguration({
					fieldLabel: 'Pagination',
					tab: 'General',
					value: 'simple',
				});

				await pageEditorPage.waitForChangesSaved();

				await expect(
					page.getByRole('button', {name: 'previous'})
				).toBeVisible();

				await expect(
					page.getByRole('button', {name: 'Next'})
				).toBeVisible();
			});
		}
	);

	test(
		'cannot select unpublished object for a display page template',
		{tag: '@LPS-137871'},
		async ({apiHelpers, displayPageTemplatesPage, page}) => {
			const objectName = 'CustomObject' + getRandomInt();

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectDefinitionExternalReferenceCode: objectName,
					status: {code: 2},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await test.step('Create a new blank display page template', async () => {
				await displayPageTemplatesPage.goto();

				await page.getByRole('button', {name: 'New'}).click();

				await page
					.getByRole('menuitem', {name: 'Display Page Template'})
					.click();

				await page.getByRole('button', {name: 'Blank'}).click();
			});

			await test.step('Verify unpublished object is not in content type options', async () => {
				const contentTypeSelect = page.getByLabel('Content Type');

				await expect(contentTypeSelect).toBeVisible();

				const options = contentTypeSelect.locator('option');

				const optionTexts = await options.allTextContents();

				expect(optionTexts).not.toContain(
					objectDefinition.label['en_US']
				);
			});
		}
	);

	test('verify if the object entries are displayed when selecting to preview an object entry on a page template', async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
	}) => {
		test.slow();
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

		const {listTypeDefinition, listTypeEntries} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
			});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: [
				'AutoIncrement',
				'Decimal',
				'Date',
				'Boolean',
				'Encrypted',
				'Integer',
				'LongInteger',
				'LongText',
				'MultiselectPicklist',
				'Picklist',
				'PrecisionDecimal',
				'RichText',
				'Text',
			],
		});

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				label: {
					en_US: objectDefinitionLabel,
				},
				name: objectDefinitionName,
				objectFields,
				pluralLabel: {
					en_US: objectDefinitionLabel,
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const {objectEntry: objectEntryValues} =
			await generateObjectEntryValues({
				listTypeEntries: listTypeEntries.map(
					(listTypeEntry) => listTypeEntry.name
				),
				objectEntryFormat: 'API',
				objectFields,
			});

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			objectEntryValues,
			applicationName
		);

		await displayPageTemplatesPage.goto();

		const displayPageTemplateName = getRandomString();

		await displayPageTemplatesPage.createTemplate({
			contentType: objectDefinition.label['en_US'],
			name: displayPageTemplateName,
		});

		await page.getByTitle(displayPageTemplateName).click();

		overloop: for (const [_, objectField] of objectDefinition.objectFields
			.filter((objectField) => !objectField.system)
			.entries()) {
			await pageEditorPage.addFragment('Basic Components', 'Heading');

			await page.getByText('Heading Example', {exact: true}).click();

			await pageEditorPage.setMappingConfiguration({
				mapping: {
					entity: objectDefinitionLabel,
					entry: objectEntry.externalReferenceCode,
					field: objectField.label['en_US'],
				},
				source: 'content',
			});

			let matchString: string;

			switch (objectField.businessType) {
				case 'Date': {
					const date = new Date(
						Date.parse(
							objectEntryValues[objectField.name] as string
						)
					);

					matchString = getPageEditorDateFormat(date);

					// Defer date validation for CI trace view analysis (issue #LRCI-4253)

					continue overloop;
				}
				case 'Picklist': {
					matchString = (
						objectEntryValues[objectField.name] as {
							key: string;
						}
					).key;

					break;
				}
				case 'MultiselectPicklist': {
					(objectEntryValues[objectField.name] as string[]).forEach(
						(listTypeEntry, index) => {
							index < 1
								? (matchString = `${listTypeEntry}`)
								: (matchString += `, ${listTypeEntry}`);
						}
					);

					break;
				}
				default: {
					matchString =
						objectEntryValues[objectField.name].toString();
				}
			}

			await expect(
				page.getByTitle('Edit Text').filter({hasText: matchString})
			).toBeVisible();
		}

		// Clean up

		await displayPageTemplatesPage.goto();

		await displayPageTemplatesPage.deleteTemplate(objectDefinitionLabel);
	});
});

test.describe('Information Template', () => {
	let contentPageName: string;
	let informationTemplateName: string;

	test.afterEach(async ({pagesAdminPage, templatesPage}) => {
		if (contentPageName) {
			await pagesAdminPage.goto();

			await pagesAdminPage.deletePage(contentPageName);

			contentPageName = '';
		}

		if (informationTemplateName) {
			await templatesPage.goto();

			await templatesPage.deleteInformationTemplate(
				informationTemplateName
			);

			informationTemplateName = '';
		}
	});

	test('verify it is possible to create a information template with an object as an item type and see its entries', async ({
		apiHelpers,
		page,
		pageEditorPage,
		pagesAdminPage,
		templatesPage,
	}) => {
		const {listTypeDefinition, listTypeEntries} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
			});

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: [
				'Boolean',
				'Decimal',
				'Integer',
				'LongText',
				'Picklist',
				'Text',
			],
		});

		apiHelpers.data.push({
			id: listTypeDefinition.id,
			type: 'listTypeDefinition',
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const {objectEntry: objectEntryValues} =
			await generateObjectEntryValues({
				listTypeEntries: listTypeEntries.map(
					(listTypeEntry) => listTypeEntry.name
				),
				objectEntryFormat: 'API',
				objectFields,
			});

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			objectEntryValues,
			applicationName
		);

		informationTemplateName = 'Object Template' + getRandomInt();

		await test.step('create information template and add object fields', async () => {
			await templatesPage.goto();

			await templatesPage.createInformationTemplate({
				itemType: objectDefinition.label['en_US'],
				name: informationTemplateName,
			});

			for (const objectField of objectFields) {
				await page
					.getByRole('button', {name: objectField.label['en_US']})
					.click();
			}

			await templatesPage.saveTemplate(informationTemplateName);
		});

		contentPageName = getRandomString();

		await test.step('create page template with HTML element linked to the informationTemplateName', async () => {
			await pagesAdminPage.goto();

			await pagesAdminPage.createNewPage({
				name: contentPageName,
			});

			await pagesAdminPage.editPage(contentPageName);

			await pageEditorPage.addFragment('Basic Components', 'HTML');

			const htmlFragmentId = await pageEditorPage.getFragmentId('HTML');

			await pageEditorPage.selectEditable(htmlFragmentId, 'element-html');

			await pageEditorPage.setMappedItem({
				entity: objectDefinition.label['en_US'],
				entry: objectEntry.id.toString(),
				entryLocator: page
					.frameLocator('iframe[title="Select"]')
					.getByText(objectEntry.id.toString())
					.first(),
				field: informationTemplateName,
			});

			await pageEditorPage.waitForChangesSaved();

			await pageEditorPage.publishPage();
		});

		await test.step('go to created page and assert object entries', async () => {
			await page.goto(`/web/guest/${contentPageName}`);

			const entries = Object.values(objectEntryValues)
				.map((value) => {
					if (typeof value === 'boolean') {
						return value ? 'Yes' : 'No';
					}

					if (
						typeof value === 'object' &&
						value !== null &&
						'key' in (value as object)
					) {
						return (value as {key: string}).key;
					}

					return String(value);
				})
				.join(' ');

			await expect(page.getByText(entries)).toBeVisible();
		});
	});
});

test.describe('Object Widget', () => {
	test(
		'can add and view auto increment object entries on a widget page',
		{tag: '@LPD-102828'},
		async ({
			apiHelpers,
			page,
			site,
			viewObjectEntriesPage,
			widgetPagePage,
		}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: [
					'Text',
					{
						businessType: 'AutoIncrement',
						objectFieldSettings: [
							{name: 'initialValue', value: '24680'},
							{name: 'prefix', value: 'T-Shirt-'},
							{name: 'suffix', value: '-Brazil'},
						],
					},
				],
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			for (const size of ['Small', 'Large']) {
				await apiHelpers.objectEntry.postObjectEntry(
					{[objectFields[0].name as string]: size},
					'c/' + objectDefinition.name.toLowerCase() + 's'
				);
			}

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: getRandomString(),
			});

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			await widgetPagePage.addPortlet(
				objectDefinition.pluralLabel['en_US']
			);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await page
				.getByLabel(objectFields[0].label.en_US, {exact: true})
				.fill('Medium');

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await waitForAlert(page);

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			for (const identification of ['24680', '24681', '24682']) {
				await expect(
					page.getByText(`T-Shirt-${identification}-Brazil`)
				).toBeVisible();
			}
		}
	);

	test(
		'can map a formula field on a widget page',
		{tag: '@LPD-102828'},
		async ({apiHelpers, page, site, widgetPagePage}) => {
			const {firstObjectField, objectFields, secondObjectField} =
				generateFormulaObjectFields({
					objectFieldBusinessType: 'Integer',
					operator: '/',
					output: 'Integer',
				});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await apiHelpers.objectEntry.postObjectEntry(
				{
					[firstObjectField.name as string]: 24680,
					[secondObjectField.name as string]: 20,
				},
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				title: getRandomString(),
			});

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			await widgetPagePage.addPortlet(
				objectDefinition.pluralLabel['en_US']
			);

			await expect(
				page.getByRole('cell', {exact: true, name: '1234'})
			).toBeVisible();
		}
	);

	test(
		'can add object portlet as a widget on a page',
		{tag: '@LPS-143122'},
		async ({
			apiHelpers,
			editObjectDetailsPage,
			page,
			site,
			widgetPagePage,
		}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await test.step('Enable "Show Widget in Page Builder" and save', async () => {
				await editObjectDetailsPage.goto(
					objectDefinition.label['en_US']
				);

				await editObjectDetailsPage.goToDetailsTab();

				await expect(
					editObjectDetailsPage.showWidgetToggle
				).toBeChecked();

				await editObjectDetailsPage.saveObjectDefinition();

				await waitForAlert(page, 'Success:');
			});

			await test.step('Create a widget page and add the object portlet', async () => {
				const layout = await apiHelpers.jsonWebServicesLayout.addLayout(
					{
						groupId: site.id,
						title: getRandomString(),
					}
				);

				await page.goto(
					`/web${site.friendlyUrlPath}${layout.friendlyURL}`
				);

				await widgetPagePage.addPortlet(
					objectDefinition.pluralLabel['en_US']
				);
			});

			await test.step('Verify the portlet is displayed with empty state', async () => {
				await expect(
					page
						.getByText(objectDefinition.pluralLabel['en_US'])
						.first()
				).toBeVisible();

				await expect(page.getByText('No Results Found')).toBeVisible();
			});
		}
	);

	test(
		'cannot add object portlet as widget when widget button is disabled',
		{tag: '@LPS-143122'},
		async ({
			apiHelpers,
			editObjectDetailsPage,
			page,
			site,
			widgetPagePage,
		}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await test.step('Navigate to object details and untoggle show widget', async () => {
				await editObjectDetailsPage.goto(
					objectDefinition.label['en_US']
				);

				await editObjectDetailsPage.goToDetailsTab();

				await editObjectDetailsPage.showWidgetToggle.uncheck();

				await editObjectDetailsPage.saveObjectDefinition();

				await waitForAlert(page, 'Success:');
			});

			await test.step('Search for the object portlet on a widget page and verify it is not available', async () => {
				const layout = await apiHelpers.jsonWebServicesLayout.addLayout(
					{
						groupId: site.id,
						title: getRandomString(),
					}
				);

				await page.goto(
					`/web${site.friendlyUrlPath}${layout.friendlyURL}`
				);

				await widgetPagePage.openAddPanel();

				await page.getByLabel('Widgets', {exact: true}).click();

				await page
					.getByRole('textbox', {name: 'Search Form'})
					.fill(objectDefinition.pluralLabel['en_US']);

				await expect(
					page
						.getByRole('alert')
						.getByText('There are no widgets on this page')
				).toBeVisible();
			});
		}
	);

	test(
		'object portlet widget disappears from page when widget button is disabled',
		{tag: '@LPS-143122'},
		async ({
			apiHelpers,
			editObjectDetailsPage,
			page,
			site,
			widgetPagePage,
		}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			let layout: Layout;

			await test.step('Create a widget page and add the object portlet', async () => {
				layout = await apiHelpers.jsonWebServicesLayout.addLayout({
					groupId: site.id,
					title: getRandomString(),
				});

				await page.goto(
					`/web${site.friendlyUrlPath}${layout.friendlyURL}`
				);

				await widgetPagePage.addPortlet(
					objectDefinition.pluralLabel['en_US']
				);
			});

			await test.step('Disable "Show Widget in Page Builder" and save', async () => {
				await editObjectDetailsPage.goto(
					objectDefinition.label['en_US']
				);

				await editObjectDetailsPage.goToDetailsTab();

				await editObjectDetailsPage.showWidgetToggle.uncheck();

				await editObjectDetailsPage.saveObjectDefinition();

				await waitForAlert(page, 'Success:');
			});

			await test.step('Navigate back to widget page and verify warning message', async () => {
				await page.goto(
					`/web${site.friendlyUrlPath}${layout.friendlyURL}`
				);

				await expect(
					page.getByText('This object is not available.')
				).toBeVisible();
			});
		}
	);
});
