/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectRelationshipAPI,
	ObjectViewAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {formatDateForUI} from '../../../utils/applyFDSDateTimeRangeFilter';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {generateFormulaObjectFields} from '../utils/generateFormulaObjectFields';
import {generateObjectEntryValues} from '../utils/generateObjectEntry';
import {generateObjectFields} from '../utils/generateObjectFields';
import {getFreshObjectRelationshipName} from '../utils/getFreshObjectRelationshipName';
import {postListTypeDefinitionListTypeEntries} from '../utils/postListTypeDefinitionListTypeEntries';

export const test = mergeTests(
	globalMenuPagesTest,
	dataApiHelpersTest,
	loginTest(),
	objectPagesTest,
	workflowPagesTest
);

test(
	'assert empty states for the object view tables',
	{tag: '@LPS-135394'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		await expect(page.getByText('No Results Found')).toBeVisible();

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.viewBuilderTab.click();

		const sidePanel = editObjectViewPage.sidePanel;

		await expect(
			sidePanel.getByText('No columns added yet.')
		).toBeVisible();

		await expect(
			sidePanel.getByText('Add columns to start creating a view.')
		).toBeVisible();
	}
);

test(
	'assert no result message when searching for a custom view',
	{tag: '@LPS-135394'},
	async ({apiHelpers, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page
			.getByRole('search')
			.getByRole('searchbox', {name: 'Search'})
			.fill('NonExistentView' + getRandomInt());

		await page.keyboard.press('Enter');

		await expect(page.getByText('No Results Found')).toBeVisible();
	}
);

test('assert that the user is able to use the ERC field in Sort, on the Custom Views tab', async ({
	apiHelpers,
	page,
	viewObjectEntriesPage,
}) => {
	const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();
	const objectDefinitionName = 'ObjectDefinitionName' + getRandomInt();

	const objectFields = generateObjectFields({
		objectFieldBusinessTypes: ['Text'],
	});

	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	const {body: objectDefinition} =
		await objectDefinitionAPIClient.postObjectDefinition({
			active: true,
			enableLocalization: true,
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
			titleObjectFieldName: objectFields[0].name,
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	const objectViewAPIClient = await apiHelpers.buildRestClient(ObjectViewAPI);

	await objectViewAPIClient.postObjectDefinitionObjectView(
		objectDefinition.id,
		{
			defaultObjectView: true,
			name: {en_US: getRandomString()},
			objectViewColumns: [
				{
					objectFieldName: objectFields[0].name,
					priority: 0,
				},
				{
					objectFieldName: 'externalReferenceCode',
					priority: 1,
				},
			],
			objectViewSortColumns: [
				{
					objectFieldName: 'externalReferenceCode',
					priority: 0,
					sortOrder: 'asc',
				},
			],
		}
	);

	const {objectEntry} = await generateObjectEntryValues({
		objectEntryFormat: 'API',
		objectFields,
	});

	const applicationName = 'c/' + objectDefinition.name.toLowerCase() + 's';
	const entry1 = 'Entry A';
	const entry2 = 'Entry B';

	await apiHelpers.objectEntry.postObjectEntry(
		{...objectEntry, externalReferenceCode: entry1},
		applicationName
	);

	await apiHelpers.objectEntry.postObjectEntry(
		{...objectEntry, externalReferenceCode: entry2},
		applicationName
	);

	await viewObjectEntriesPage.goto(objectDefinition.className);

	await expect(page.locator('.cell-externalReferenceCode').nth(1)).toHaveText(
		entry1
	);
	await expect(page.locator('.cell-externalReferenceCode').nth(2)).toHaveText(
		entry2
	);

	await page.getByTitle('Sortable Column').click();

	await expect(page.locator('.cell-externalReferenceCode').nth(1)).toHaveText(
		entry2
	);
	await expect(page.locator('.cell-externalReferenceCode').nth(2)).toHaveText(
		entry1
	);
});

test(
	'can add a column to the custom view',
	{tag: '@LPS-135394'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.selectObjectFields(['Author', 'Create Date']);

		await expect(
			editObjectViewPage.sidePanel.getByText('Author').first()
		).toBeVisible();

		await expect(
			editObjectViewPage.sidePanel.getByText('Create Date').first()
		).toBeVisible();
	}
);

test('can add and remove new object fields from object view while maintaining correct logic order', async ({
	apiHelpers,
	editObjectViewPage,
	objectViewPage,
	page,
}) => {
	const listTypeDefinition =
		await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

	apiHelpers.data.push({
		id: listTypeDefinition.id,
		type: 'listTypeDefinition',
	});

	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFields: [
				{
					DBType: 'String',
					businessType: 'MultiselectPicklist',
					externalReferenceCode: 'customPicklist',
					indexed: true,
					indexedAsKeyword: false,
					indexedLanguageId: 'en_US',
					label: {
						en_US: 'customPicklist',
					},
					listTypeDefinitionExternalReferenceCode:
						listTypeDefinition.externalReferenceCode,
					name: 'customPicklist',
					required: false,
					state: false,
				},
			],
			status: {code: 0},
		});

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	const objectViewName = getRandomString();

	await objectViewPage.goto(objectDefinition.label['en_US']);

	await objectViewPage.createObjectView(objectViewName);

	await page.getByRole('link', {name: objectViewName}).click();

	await editObjectViewPage.selectObjectFields(['Status', 'Create Date']);

	const objectFields = editObjectViewPage.sidePanel.locator(
		'.lfr-object__object-custom-view-builder-item'
	);

	await expect(objectFields).toHaveCount(2);

	await expect(objectFields.nth(0)).toContainText('Status');

	await expect(objectFields.nth(1)).toContainText('Create Date');

	await editObjectViewPage.selectObjectFields(['ID', 'customPicklist']);

	await editObjectViewPage.unselectObjectFields(['Status', 'ID']);

	await editObjectViewPage.selectObjectFields(['External Reference Code']);

	await expect(objectFields).toHaveCount(3);

	await expect(objectFields.nth(0)).toContainText('Create Date');

	await expect(objectFields.nth(1)).toContainText('customPicklist');

	await expect(objectFields.nth(2)).toContainText('External Reference Code');
});

test(
	'can add metadata columns to default sort',
	{tag: '@LPS-144472'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		const metadataColumns = [
			'Author',
			'Create Date',
			'External Reference Code',
			'Modified Date',
			'Status',
			'ID',
		];

		await editObjectViewPage.selectObjectFields(metadataColumns);

		const defaultSortTab = sidePanel.getByRole('tab', {
			name: 'Default Sort',
		});

		await defaultSortTab.click();

		await expect(
			sidePanel.getByRole('heading', {name: 'Default Sort'})
		).toBeVisible({timeout: 3000});

		for (const columnName of metadataColumns) {
			await editObjectViewPage.addDefaultSort(columnName, 'Ascending');
		}

		for (const columnName of metadataColumns) {
			await expect(
				sidePanel.getByText(columnName, {exact: true}).last()
			).toBeVisible();
		}
	}
);

test(
	'can add translation to column label in custom view',
	{tag: '@LPS-147792'},
	async ({
		apiHelpers,
		editObjectViewPage,
		objectViewPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [
					{
						DBType: 'String',
						businessType: 'Text',
						label: {
							en_US: 'Custom Field',
							pt_BR: 'Campo Customizado',
						},
						name: 'customField',
					},
				],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.selectObjectFields(['Custom Field']);

		const defaultSortTab = sidePanel.getByRole('tab', {
			name: 'Default Sort',
		});

		await defaultSortTab.click();

		await expect(
			sidePanel.getByRole('heading', {name: 'Default Sort'})
		).toBeVisible({timeout: 3000});

		await editObjectViewPage.addDefaultSort('Custom Field', 'Ascending');

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{customField: 'Entry Test'},
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition.className, 'pt');

		await expect(page.getByText('Campo Customizado')).toBeVisible();
	}
);

test(
	'can cancel column addition in custom view',
	{tag: '@LPS-135394'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.viewBuilderTab.click();

		const addButton = editObjectViewPage.addColumnButton.or(
			editObjectViewPage.addButton
		);

		await addButton.click();

		await editObjectViewPage.addColumnsModal
			.getByRole('button', {name: 'Cancel'})
			.click();

		await expect(editObjectViewPage.addColumnsModal).toBeHidden();

		await expect(
			editObjectViewPage.sidePanel.getByText('No columns added yet.')
		).toBeVisible();
	}
);

test(
	'can cancel rename of a column label in custom view',
	{tag: '@LPS-147792'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.selectObjectFields(['Author']);

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByRole('button', {name: 'More'}).click();

		await sidePanel.getByRole('menuitem', {name: 'Edit'}).click();

		const editModal = sidePanel
			.getByRole('dialog')
			.filter({hasText: 'Label'});

		await editModal.getByLabel('Label').clear();
		await editModal.getByLabel('Label').fill('Publishing House');

		await editModal.getByRole('button', {name: 'Cancel'}).click();

		await expect(sidePanel.getByText('Author').first()).toBeVisible();
	}
);

test(
	'can cancel the creation of a custom view',
	{tag: '@LPS-135394'},
	async ({apiHelpers, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label.en_US);

		await objectViewPage.addObjectViewButton.click();

		await page.getByRole('button', {name: 'Cancel'}).click();

		await expect(page.getByText('No Results Found')).toBeVisible();
	}
);

test(
	'can create a custom view',
	{tag: '@LPS-135394'},
	async ({apiHelpers, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await expect(page.getByRole('link', {name: viewName})).toBeVisible();
	}
);

test(
	'can create a default sort with ascending or descending order',
	{tag: '@LPS-144472'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.selectObjectFields(['Author']);

		const defaultSortTab = sidePanel.getByRole('tab', {
			name: 'Default Sort',
		});

		await defaultSortTab.click();

		await expect(
			sidePanel.getByRole('heading', {name: 'Default Sort'})
		).toBeVisible({timeout: 3000});

		await editObjectViewPage.addDefaultSort('Author', 'Ascending');

		await expect(
			sidePanel.getByText('Author', {exact: true}).last()
		).toBeVisible();

		await expect(
			sidePanel.getByText('Ascending', {exact: true}).last()
		).toBeVisible();

		await sidePanel.getByRole('button', {name: 'More'}).click();

		await sidePanel.getByRole('menuitem', {name: 'Edit'}).click();

		await sidePanel
			.getByLabel('Edit Default Sort')
			.getByText('Ascending')
			.click();

		await sidePanel.getByRole('option', {name: 'Descending'}).click();

		await sidePanel
			.getByLabel('Edit Default Sort')
			.getByRole('button', {name: 'Save'})
			.click();

		await expect(
			sidePanel.getByText('Descending', {exact: true}).last()
		).toBeVisible();
	}
);

test(
	'can create a filter by relationship column from system object',
	{tag: '@LPS-170529'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				'L_ACCOUNT',
				{
					label: {
						en_US: 'objectRelationshipLabel' + getRandomInt(),
					},
					name: await getFreshObjectRelationshipName(apiHelpers, [
						'L_ACCOUNT',
						objectDefinition.externalReferenceCode!,
					]),
					objectDefinitionExternalReferenceCode1: 'L_ACCOUNT',
					objectDefinitionExternalReferenceCode2:
						objectDefinition.externalReferenceCode,
					objectDefinitionId2: objectDefinition.id,
					objectDefinitionName2: objectDefinition.name,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.selectObjectFields([
			objectRelationship.label.en_US,
		]);

		await editObjectViewPage.filtersTab.click();

		await editObjectViewPage.newFilterButton.click();

		await editObjectViewPage.filterBy.click();

		await sidePanel
			.getByRole('option', {name: objectRelationship.label.en_US})
			.click();

		await editObjectViewPage.saveFilter.click();

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await page.reload();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.filtersTab.click();

		await expect(
			sidePanel.getByText(objectRelationship.label.en_US).last()
		).toBeVisible();
	}
);

test(
	'can create a filter using the relationship field of the object',
	{tag: '@LPS-166585'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
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
					label: {
						en_US: 'objectRelationshipLabel' + getRandomInt(),
					},
					name: await getFreshObjectRelationshipName(apiHelpers, [
						objectDefinition1.externalReferenceCode!,
						objectDefinition2.externalReferenceCode!,
					]),
					objectDefinitionExternalReferenceCode1:
						objectDefinition1.externalReferenceCode,
					objectDefinitionExternalReferenceCode2:
						objectDefinition2.externalReferenceCode,
					objectDefinitionId2: objectDefinition2.id,
					objectDefinitionName2: objectDefinition2.name,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await objectViewPage.goto(objectDefinition2.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.filtersTab.click();

		await editObjectViewPage.newFilterButton.click();

		await editObjectViewPage.filterBy.click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel
			.getByRole('option', {name: objectRelationship.label.en_US})
			.click();

		await editObjectViewPage.saveFilter.click();

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await page.reload();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.filtersTab.click();

		await expect(
			sidePanel.getByText(objectRelationship.label.en_US).first()
		).toBeVisible();
	}
);

test('can create an object custom view using object relationship entry', async ({
	apiHelpers,
	editObjectViewPage,
	objectViewPage,
	page,
}) => {
	const objectDefinition1 =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			status: {code: 0},
		});

	const objectDefinition2 =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			status: {code: 0},
		});

	apiHelpers.data.push({id: objectDefinition1.id, type: 'objectDefinition'});

	apiHelpers.data.push({id: objectDefinition2.id, type: 'objectDefinition'});

	const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
		ObjectRelationshipAPI
	);

	const {body: objectRelationship} =
		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			objectDefinition1.externalReferenceCode,
			{
				label: {
					en_US: 'objectRelationshipLabel' + getRandomInt(),
				},
				name: await getFreshObjectRelationshipName(apiHelpers, [
					objectDefinition1.externalReferenceCode!,
					objectDefinition2.externalReferenceCode!,
				]),
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

	const applicationName = 'c/' + objectDefinition1.name.toLowerCase() + 's';

	const textObjectEntry = {
		textField: 'entry',
	};

	const objectEntryResponse = await apiHelpers.objectEntry.postObjectEntry(
		textObjectEntry,
		applicationName
	);

	const objectViewName = getRandomString();

	await objectViewPage.goto(objectDefinition2.label['en_US']);

	await objectViewPage.createObjectView(objectViewName);

	await page.getByRole('link', {name: objectViewName}).click();

	await editObjectViewPage.createFilter(
		objectRelationship.label.en_US,
		'Includes',
		`${objectEntryResponse.id}`
	);

	await expect(
		editObjectViewPage.sidePanel
			.locator('.lfr-object__object-builder-list-item-first-column')
			.getByText(`${objectRelationship.label.en_US}`)
	).toBeVisible();

	await expect(
		editObjectViewPage.sidePanel
			.locator('.lfr-object__object-builder-list-item-second-column')
			.getByText('Relationship', {exact: true})
	).toBeVisible();

	await expect(
		editObjectViewPage.sidePanel
			.locator('.lfr-object__object-builder-list-item-third-column')
			.getByText(`${objectEntryResponse.id}`)
	).toBeVisible();
});

test(
	'can delete a column through the delete button in custom view',
	{tag: '@LPS-135394'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await editObjectViewPage.selectObjectFields(['Author']);

		await expect(sidePanel.getByText('Author').first()).toBeVisible();

		await sidePanel.getByRole('button', {name: 'More'}).click();

		await sidePanel.getByRole('menuitem', {name: 'Delete'}).click();

		await expect(
			sidePanel.getByText('No columns added yet.')
		).toBeVisible();
	}
);

test(
	'can delete a column through unselecting it in custom view',
	{tag: '@LPS-135394'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.selectObjectFields(['Author']);

		const sidePanel = editObjectViewPage.sidePanel;

		await expect(sidePanel.getByText('Author').first()).toBeVisible();

		await editObjectViewPage.unselectObjectFields(['Author']);

		await expect(
			sidePanel.getByText('No columns added yet.')
		).toBeVisible();
	}
);

test(
	'can delete a column with relationship field filter in custom view',
	{tag: '@LPS-166588'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				'L_USER',
				{
					label: {
						en_US: 'objectRelationshipLabel' + getRandomInt(),
					},
					name: await getFreshObjectRelationshipName(apiHelpers, [
						'L_USER',
						objectDefinition.externalReferenceCode!,
					]),
					objectDefinitionExternalReferenceCode1: 'L_USER',
					objectDefinitionExternalReferenceCode2:
						objectDefinition.externalReferenceCode,
					objectDefinitionId2: objectDefinition.id,
					objectDefinitionName2: objectDefinition.name,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.selectObjectFields([
			objectRelationship.label.en_US,
		]);

		await expect(
			sidePanel.getByText(objectRelationship.label.en_US).first()
		).toBeVisible();

		await sidePanel.getByRole('button', {name: 'More'}).click();

		await sidePanel.getByRole('menuitem', {name: 'Delete'}).click();

		await expect(
			sidePanel.getByText('No columns added yet.')
		).toBeVisible();

		await editObjectViewPage.filtersTab.click();

		await editObjectViewPage.newFilterButton.click();

		await editObjectViewPage.filterBy.click();

		await expect(
			sidePanel.getByRole('option', {
				name: objectRelationship.label.en_US,
			})
		).toBeVisible();
	}
);

test(
	'can delete a custom view',
	{tag: '@LPS-135394'},
	async ({apiHelpers, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page
			.getByRole('row', {name: viewName})
			.getByRole('button', {name: 'Actions'})
			.click();

		await page.getByRole('menuitem', {name: 'Delete'}).click();

		await expect(page.getByRole('link', {name: viewName})).toBeHidden();
	}
);

test(
	'can delete a filter in custom view',
	{tag: '@LPS-144957'},
	async ({
		apiHelpers,
		editObjectViewPage,
		objectViewPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.selectObjectFields(['textField']);

		await editObjectViewPage.filtersTab.click();

		await editObjectViewPage.newFilterButton.click();

		await editObjectViewPage.filterBy.click();

		await sidePanel.getByRole('option', {name: 'Create Date'}).click();

		await editObjectViewPage.saveFilter.click();

		await sidePanel.getByRole('button', {name: 'Add'}).click();

		await editObjectViewPage.filterBy.click();

		await sidePanel.getByRole('option', {name: 'Modified Date'}).click();

		await editObjectViewPage.saveFilter.click();

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await page.reload();

		await page.getByRole('button', {name: 'Filter'}).click();

		await expect(
			page.getByRole('menuitem', {name: 'Create Date'})
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {name: 'Modified Date'})
		).toBeVisible();

		await objectViewPage.goto(objectDefinition.label['en_US']);

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.filtersTab.click();

		await sidePanel.getByRole('button', {name: 'More'}).last().click();

		await sidePanel.getByRole('menuitem', {name: 'Delete'}).click();

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await page.reload();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.filtersTab.click();

		await expect(sidePanel.getByText('Relationship').first()).toBeHidden();

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await page.getByRole('button', {name: 'Filter'}).click();

		await expect(
			page.getByRole('menuitem', {name: 'Create Date'})
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {name: 'Modified Date'})
		).not.toBeVisible();
	}
);

test(
	'can delete a filter with relationship field in custom view',
	{tag: '@LPS-166590'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				'L_USER',
				{
					label: {
						en_US: 'objectRelationshipLabel' + getRandomInt(),
					},
					name: await getFreshObjectRelationshipName(apiHelpers, [
						'L_USER',
						objectDefinition.externalReferenceCode!,
					]),
					objectDefinitionExternalReferenceCode1: 'L_USER',
					objectDefinitionExternalReferenceCode2:
						objectDefinition.externalReferenceCode,
					objectDefinitionId2: objectDefinition.id,
					objectDefinitionName2: objectDefinition.name,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.selectObjectFields([
			objectRelationship.label.en_US,
		]);

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await page.reload();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.filtersTab.click();

		await editObjectViewPage.newFilterButton.click();

		await editObjectViewPage.filterBy.click();

		await sidePanel
			.getByRole('option', {name: objectRelationship.label.en_US})
			.click();

		await editObjectViewPage.saveFilter.click();

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await page.reload();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.filtersTab.click();

		await sidePanel.getByRole('button', {name: 'More'}).click();

		await sidePanel.getByRole('menuitem', {name: 'Delete'}).click();

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await page.reload();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.filtersTab.click();

		await expect(
			sidePanel.getByText(objectRelationship.label.en_US).first()
		).toBeHidden();
	}
);

test(
	'can drag columns in custom view builder',
	{tag: '@LPS-135394'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.selectObjectFields([
			'Author',
			'Create Date',
			'Modified Date',
		]);

		const sidePanel = editObjectViewPage.sidePanel;

		const columns = sidePanel.locator(
			'.lfr-object__object-custom-view-builder-item'
		);

		await expect(columns).toContainText([
			'Author',
			'Create Date',
			'Modified Date',
		]);

		const sourceRow = columns.nth(0);
		const targetRow = columns.nth(2);

		const sourceBox = await sourceRow.boundingBox();
		const targetBox = await targetRow.boundingBox();

		const startX = sourceBox.x + sourceBox.width / 2;
		const startY = sourceBox.y + sourceBox.height / 2;

		const endX = targetBox.x + targetBox.width / 2;
		const endY = targetBox.y + targetBox.height / 2 + 10;

		const dataTransfer = await sourceRow.evaluateHandle(
			() => new DataTransfer()
		);

		await sourceRow.dispatchEvent('dragstart', {
			clientX: startX,
			clientY: startY,
			dataTransfer,
		});
		await targetRow.dispatchEvent('dragenter', {
			clientX: endX,
			clientY: endY,
			dataTransfer,
		});
		await targetRow.dispatchEvent('dragover', {
			clientX: endX,
			clientY: endY,
			dataTransfer,
		});

		await page.waitForTimeout(100);

		await targetRow.dispatchEvent('drop', {
			clientX: endX,
			clientY: endY,
			dataTransfer,
		});
		await sourceRow.dispatchEvent('dragend', {
			clientX: endX,
			clientY: endY,
			dataTransfer,
		});

		const reorderedColumns = sidePanel.locator(
			'.lfr-object__object-custom-view-builder-item'
		);

		await expect(reorderedColumns).toContainText([
			'Create Date',
			'Modified Date',
			'Author',
		]);
	}
);

test(
	'can duplicate an object view',
	{tag: '@LPS-146028'},
	async ({apiHelpers, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page
			.getByRole('row', {name: viewName})
			.getByRole('button', {name: 'Actions'})
			.click();

		await page.getByRole('menuitem', {name: 'Duplicate'}).click();

		await page.getByText('(Copy)').first().waitFor();

		await expect(
			page.getByRole('link', {name: `${viewName} (Copy)`})
		).toBeVisible();
	}
);

test(
	'can edit a filter in custom view',
	{tag: '@LPS-144957'},
	async ({
		apiHelpers,
		configurationTabPage,
		editObjectViewPage,
		globalMenuPage,
		objectViewPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		const approvedEntry = await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Entry Test'},
			applicationName
		);

		await globalMenuPage.goToApplications('Process Builder');

		await configurationTabPage.configurationTabLink.click();

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			objectDefinition.label['en_US']
		);

		const pendingEntry = await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Entry Test 2'},
			applicationName
		);

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.selectObjectFields(['textField', 'Status']);

		await editObjectViewPage.createFilter('Status', 'Includes', 'Pending');

		await sidePanel.getByRole('button', {name: 'Save'}).last().click();

		await page.waitForLoadState('networkidle');

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await page.reload();

		await expect(
			page.getByText(approvedEntry.textField, {exact: true})
		).not.toBeVisible();

		await expect(
			page.getByText(pendingEntry.textField, {exact: true})
		).toBeVisible();

		await objectViewPage.goto(objectDefinition.label['en_US']);

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.filtersTab.click();

		await sidePanel.getByRole('button', {name: 'More'}).click();

		await sidePanel.getByRole('menuitem', {name: 'Edit'}).click();

		await sidePanel
			.locator('div')
			.filter({hasText: /^Pending$/})
			.nth(2)
			.click();

		await sidePanel.getByRole('checkbox', {name: 'Approved'}).check();

		await sidePanel.getByText('New Filter').click();

		await sidePanel
			.getByLabel('New Filter')
			.getByRole('button', {name: 'Save'})
			.click();

		await sidePanel.getByRole('button', {name: 'Save'}).last().click();

		await page.waitForLoadState('networkidle');

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await page.reload();

		await expect(
			page.getByText(approvedEntry.textField, {exact: true})
		).toBeVisible();

		await expect(
			page.getByText(pendingEntry.textField, {exact: true})
		).toBeVisible();
	}
);

test(
	'can edit a filter label in custom view',
	{tag: '@LPS-166587'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				'L_USER',
				{
					label: {
						en_US: 'objectRelationshipLabel' + getRandomInt(),
					},
					name: await getFreshObjectRelationshipName(apiHelpers, [
						'L_USER',
						objectDefinition.externalReferenceCode!,
					]),
					objectDefinitionExternalReferenceCode1: 'L_USER',
					objectDefinitionExternalReferenceCode2:
						objectDefinition.externalReferenceCode,
					objectDefinitionId2: objectDefinition.id,
					objectDefinitionName2: objectDefinition.name,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.selectObjectFields([
			objectRelationship.label.en_US,
		]);

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await page.reload();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.viewBuilderTab.click();

		await sidePanel.getByRole('button', {name: 'More'}).click();

		await sidePanel.getByRole('menuitem', {name: 'Edit'}).click();

		const editModal = sidePanel
			.getByRole('dialog')
			.filter({hasText: 'Label'});

		await editModal.getByLabel('Label').clear();

		await editModal.getByLabel('Label').fill('Relationship Edit');

		await editModal.getByRole('button', {name: 'Edit'}).click();

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await page.reload();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.viewBuilderTab.click();

		await expect(sidePanel.getByText('Relationship Edit')).toBeVisible();
	}
);

test(
	'can filter a picklist column with the excludes operator in custom view',
	{tag: '@LPD-102828'},
	async ({
		apiHelpers,
		editObjectViewPage,
		objectViewPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const {listTypeDefinition, listTypeEntries} =
			await postListTypeDefinitionListTypeEntries({
				apiHelpers,
				listTypeEntriesLength: 2,
			});

		const [excludedListTypeEntry, listTypeEntry] = listTypeEntries;

		const objectFields = generateObjectFields({
			listTypeDefinitionExternalReferenceCode:
				listTypeDefinition.externalReferenceCode,
			objectFieldBusinessTypes: ['Picklist'],
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
			'c/' + objectDefinition.name.toLowerCase() + 's';

		for (const listTypeEntryKey of [
			excludedListTypeEntry.key,
			listTypeEntry.key,
		]) {
			await apiHelpers.objectEntry.postObjectEntry(
				{[objectFields[0].name as string]: listTypeEntryKey},
				applicationName
			);
		}

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.markAsDefaultButton.check();

		await editObjectViewPage.selectObjectFields([
			objectFields[0].label.en_US,
		]);

		await editObjectViewPage.createFilter(
			objectFields[0].label.en_US,
			'Excludes',
			excludedListTypeEntry.key
		);

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page.getByRole('row').filter({hasText: listTypeEntry.key})
		).toBeVisible();

		await expect(
			page.getByRole('row').filter({hasText: excludedListTypeEntry.key})
		).toHaveCount(0);
	}
);

test(
	'can filter entries by create date in custom view',
	{tag: ['@LPD-102828', '@LPS-169019']},
	async ({
		apiHelpers,
		editObjectViewPage,
		objectViewPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [
					{
						DBType: 'String',
						businessType: 'Text',
						label: {en_US: 'Custom Field'},
						name: 'customField',
					},
				],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{customField: 'Entry Test'},
			applicationName
		);

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.selectObjectFields([
			'Custom Field',
			'Create Date',
		]);

		await editObjectViewPage.filtersTab.click();

		await editObjectViewPage.newFilterButton.click();

		await editObjectViewPage.filterBy.click();

		await sidePanel.getByRole('option', {name: 'Create Date'}).click();

		await editObjectViewPage.saveFilter.click();

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(page.getByText('Entry Test').first()).toBeVisible();

		await page.getByRole('button', {name: 'Filter'}).click();

		await page.getByRole('menuitem', {name: 'Create Date'}).click();

		const today = new Date();
		const yesterday = new Date(today);

		yesterday.setDate(yesterday.getDate() - 1);

		await page
			.getByRole('textbox', {name: 'From'})
			.fill(formatDateForUI(yesterday));

		await page
			.getByRole('textbox', {name: 'To'})
			.fill(formatDateForUI(today));

		await page.getByRole('button', {name: 'Add Filter'}).click();

		await expect(page.getByText('1 Result Found for:')).toBeVisible();

		await expect(
			page.getByRole('button', {
				name: `Create Date: ${formatDateForUI(yesterday)} - ${formatDateForUI(today)}`,
			})
		).toBeVisible();

		await expect(page.getByText('Entry Test').first()).toBeVisible();
	}
);

test(
	'can filter entries by modified date in custom view',
	{tag: '@LPS-169018'},
	async ({
		apiHelpers,
		editObjectViewPage,
		objectViewPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [
					{
						DBType: 'String',
						businessType: 'Text',
						label: {en_US: 'Custom Field'},
						name: 'customField',
					},
				],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{customField: 'Entry Test'},
			applicationName
		);

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.selectObjectFields([
			'Custom Field',
			'Modified Date',
		]);

		await editObjectViewPage.filtersTab.click();

		await editObjectViewPage.newFilterButton.click();

		await editObjectViewPage.filterBy.click();

		await sidePanel.getByRole('option', {name: 'Modified Date'}).click();

		await editObjectViewPage.saveFilter.click();

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(page.getByText('Entry Test').first()).toBeVisible();

		await page.getByRole('button', {name: 'Filter'}).click();

		await page.getByRole('menuitem', {name: 'Modified Date'}).click();

		const today = new Date();
		const yesterday = new Date(today);

		yesterday.setDate(yesterday.getDate() - 1);

		await page
			.getByRole('textbox', {name: 'From'})
			.fill(formatDateForUI(yesterday));

		await page
			.getByRole('textbox', {name: 'To'})
			.fill(formatDateForUI(today));

		await page.getByRole('button', {name: 'Add Filter'}).click();

		await expect(page.getByText('1 Result Found for:')).toBeVisible();

		await expect(
			page.getByRole('button', {
				name: `Modified Date: ${formatDateForUI(yesterday)} - ${formatDateForUI(today)}`,
			})
		).toBeVisible();

		await expect(page.getByText('Entry Test').first()).toBeVisible();
	}
);

test(
	'can filter entries by status in custom view',
	{tag: '@LPS-169016'},
	async ({
		apiHelpers,
		configurationTabPage,
		editObjectViewPage,
		globalMenuPage,
		objectViewPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		const approvedEntry = await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Entry Test'},
			applicationName
		);

		await globalMenuPage.goToApplications('Process Builder');

		await configurationTabPage.configurationTabLink.click();

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			objectDefinition.label['en_US']
		);

		const pendingEntry = await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Entry Test 2'},
			applicationName
		);

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.selectObjectFields(['textField', 'Status']);

		await editObjectViewPage.createFilter(
			'Status',
			'Includes',
			'Approved, Denied, Draft, Expired, Inactive, Incomplete, In Recycle Bin, Scheduled'
		);

		await sidePanel.getByRole('button', {name: 'Save'}).last().click();

		await page.waitForLoadState('networkidle');

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await page.reload();

		await expect(
			page.getByText(approvedEntry.textField, {exact: true})
		).toBeVisible();

		await expect(
			page.getByText(pendingEntry.textField, {exact: true})
		).not.toBeVisible();
	}
);

test(
	'can filter entries using relationship field in custom view',
	{tag: '@LPS-169016'},
	async ({
		apiHelpers,
		editObjectViewPage,
		objectViewPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionA =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		const objectDefinitionB =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinitionA.id,
			type: 'objectDefinition',
		});

		apiHelpers.data.push({
			id: objectDefinitionB.id,
			type: 'objectDefinition',
		});

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationshipAC} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectDefinitionA.externalReferenceCode,
				{
					label: {
						en_US: 'objectRelationshipLabel' + getRandomInt(),
					},
					name: await getFreshObjectRelationshipName(apiHelpers, [
						objectDefinitionA.externalReferenceCode!,
						objectDefinitionB.externalReferenceCode!,
					]),
					objectDefinitionExternalReferenceCode1:
						objectDefinitionA.externalReferenceCode,
					objectDefinitionExternalReferenceCode2:
						objectDefinitionB.externalReferenceCode,
					objectDefinitionId2: objectDefinitionB.id,
					objectDefinitionName2: objectDefinitionB.name,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationshipAC.id,
			type: 'objectRelationship',
		});

		const objectEntryA1 = await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'entryA1'},
			'c/' + objectDefinitionA.name.toLowerCase() + 's'
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				[`r_${objectRelationshipAC.name}_c_${objectDefinitionA.name[0].toLowerCase() + objectDefinitionA.name.substring(1)}Id`]:
					objectEntryA1.id.toString(),
				textField: 'entryA1',
			},
			'c/' + objectDefinitionB.name.toLowerCase() + 's'
		);

		await objectViewPage.goto(objectDefinitionB.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.selectObjectFields([
			'textField',
			objectRelationshipAC.label.en_US,
		]);

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await page.reload();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.filtersTab.click();

		await editObjectViewPage.newFilterButton.click();

		await editObjectViewPage.filterBy.click();

		await sidePanel
			.getByRole('option', {name: objectRelationshipAC.label.en_US})
			.click();

		await editObjectViewPage.filterType.click();

		await sidePanel.getByRole('option', {name: 'Excludes'}).click();

		await sidePanel
			.getByRole('dialog', {name: 'New Filter'})
			.locator('input[type="text"]')
			.click();

		await sidePanel
			.getByRole('checkbox', {name: String(objectEntryA1.id)})
			.check();

		await sidePanel
			.getByLabel('New Filter')
			.getByText('New Filter')
			.click();

		await expect(
			sidePanel.getByRole('row', {name: `Remove ${objectEntryA1.id}`})
		).toBeVisible();

		await editObjectViewPage.saveFilter.click();

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		const objectEntryA2 = await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'entryA2'},
			'c/' + objectDefinitionA.name.toLowerCase() + 's'
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{
				[`r_${objectRelationshipAC.name}_c_${objectDefinitionA.name[0].toLowerCase() + objectDefinitionA.name.substring(1)}Id`]:
					objectEntryA2.id.toString(),
				textField: 'entryA2',
			},
			'c/' + objectDefinitionB.name.toLowerCase() + 's'
		);

		await viewObjectEntriesPage.goto(objectDefinitionB.className);

		await expect(
			page.getByRole('cell', {name: String(objectEntryA2.id)})
		).toBeVisible();

		await expect(
			page.getByRole('cell', {name: String(objectEntryA1.id)})
		).not.toBeVisible();

		await page.getByRole('button', {name: 'Remove Filter'}).click();

		await expect(
			page.getByRole('cell', {name: String(objectEntryA1.id)})
		).toBeVisible();
	}
);

test(
	'can search for a column on the add columns modal',
	{tag: '@LPS-135394'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.viewBuilderTab.click();

		const addButton = editObjectViewPage.addColumnButton.or(
			editObjectViewPage.addButton
		);

		await addButton.click();

		const modal = editObjectViewPage.addColumnsModal;

		await modal.getByPlaceholder('Search').fill('Author');

		await expect(modal.getByText('Author')).toBeVisible();
		await expect(modal.getByText('Create Date')).toBeHidden();
	}
);

test(
	'can search for a column on the view builder tab',
	{tag: '@LPS-135394'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.selectObjectFields(['Author', 'Create Date']);

		const sidePanel = editObjectViewPage.sidePanel;

		const searchInput = sidePanel.getByPlaceholder('Search');

		await searchInput.fill('Author');

		await expect(
			sidePanel
				.locator('.lfr-object__object-custom-view-builder-item')
				.filter({hasText: 'Author'})
		).toBeVisible();

		await expect(
			sidePanel
				.locator('.lfr-object__object-custom-view-builder-item')
				.filter({hasText: 'Create Date'})
		).toBeHidden();
	}
);

test(
	'can search for a custom view',
	{tag: '@LPS-135394'},
	async ({apiHelpers, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewNameA = 'ViewA' + getRandomInt();
		const viewNameB = 'ViewB' + getRandomInt();

		await objectViewPage.createObjectView(viewNameA);

		await page.getByRole('link', {name: viewNameA}).waitFor();

		await objectViewPage.createObjectView(viewNameB);

		await page.getByRole('link', {name: viewNameB}).waitFor();

		await page
			.getByRole('search')
			.getByRole('searchbox', {name: 'Search'})
			.fill(viewNameA);

		await page.keyboard.press('Enter');

		await expect(page.getByRole('link', {name: viewNameA})).toBeVisible();

		await expect(page.getByRole('link', {name: viewNameB})).toBeHidden();
	}
);

test(
	'can search for columns in default sort',
	{tag: '@LPS-144472'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		const metadataColumns = [
			'Author',
			'Create Date',
			'External Reference Code',
			'Modified Date',
			'Status',
			'ID',
		];

		await editObjectViewPage.selectObjectFields(metadataColumns);

		const defaultSortTab = sidePanel.getByRole('tab', {
			name: 'Default Sort',
		});

		await defaultSortTab.click();

		await expect(
			sidePanel.getByRole('heading', {name: 'Default Sort'})
		).toBeVisible({timeout: 3000});

		for (const columnName of metadataColumns) {
			await editObjectViewPage.addDefaultSort(columnName, 'Ascending');
		}

		for (const columnName of metadataColumns) {
			const searchInput = sidePanel.getByPlaceholder('Search').last();

			await searchInput.fill(columnName);

			await expect(
				sidePanel.getByText(columnName, {exact: true}).last()
			).toBeVisible();

			await searchInput.clear();
		}
	}
);

test(
	'can see entries with default return in custom view',
	{tag: '@LPS-144472'},
	async ({apiHelpers, page, viewObjectEntriesPage}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		for (const letter of ['A', 'B', 'Z']) {
			await apiHelpers.objectEntry.postObjectEntry(
				{textField: `Entry ${letter}`},
				applicationName
			);
		}

		const objectViewAPIClient =
			await apiHelpers.buildRestClient(ObjectViewAPI);

		await objectViewAPIClient.postObjectDefinitionObjectView(
			objectDefinition.id,
			{
				defaultObjectView: true,
				name: {en_US: 'CustomView' + getRandomInt()},
				objectViewColumns: [
					{
						objectFieldName: 'textField',
						priority: 0,
					},
				],
				objectViewSortColumns: [
					{
						objectFieldName: 'textField',
						priority: 0,
						sortOrder: 'desc',
					},
				],
			}
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		const cells = page.getByRole('cell');

		await expect(cells).toContainText(['Entry Z', 'Entry B', 'Entry A']);
	}
);

test(
	'can see renamed column name on object view entries list',
	{tag: '@LPS-147792'},
	async ({
		apiHelpers,
		editObjectViewPage,
		objectViewPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.selectObjectFields(['textField']);

		await sidePanel.getByRole('button', {name: 'More'}).click();

		await sidePanel.getByRole('menuitem', {name: 'Edit'}).click();

		const editModal = sidePanel
			.getByRole('dialog')
			.filter({hasText: 'Label'});

		await editModal.getByLabel('Label').clear();

		await editModal.getByLabel('Label').fill('Column Label Renamed');

		await editModal.getByRole('button', {name: 'Edit'}).click();

		await expect(sidePanel.getByText('Column Label Renamed')).toBeVisible();

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Entry Test'},
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page.getByRole('columnheader', {name: 'Column Label Renamed'})
		).toBeVisible();
	}
);

test(
	'can sort column entries as ascending or descending',
	{tag: '@LPD-78504'},
	async ({apiHelpers, page, viewObjectEntriesPage}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		for (const letter of ['A', 'B', 'C']) {
			await apiHelpers.objectEntry.postObjectEntry(
				{textField: `Entry ${letter}`},
				applicationName
			);
		}

		const objectViewAPIClient =
			await apiHelpers.buildRestClient(ObjectViewAPI);

		await objectViewAPIClient.postObjectDefinitionObjectView(
			objectDefinition.id,
			{
				defaultObjectView: true,
				name: {en_US: 'CustomView' + getRandomInt()},
				objectViewColumns: [
					{
						objectFieldName: 'textField',
						priority: 0,
					},
				],
				objectViewSortColumns: [
					{
						objectFieldName: 'textField',
						priority: 0,
						sortOrder: 'asc',
					},
				],
			}
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		const ascendingCells = page.getByRole('cell');

		await expect(ascendingCells).toContainText([
			'Entry A',
			'Entry B',
			'Entry C',
		]);

		await page
			.getByRole('columnheader', {name: 'textField'})
			.getByRole('button')
			.click();

		const descendingCells = page.getByRole('cell');

		await expect(descendingCells).toContainText([
			'Entry C',
			'Entry B',
			'Entry A',
		]);
	}
);

test(
	'can update a custom view name',
	{tag: '@LPS-135394'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		const nameInput = sidePanel.locator('input[type="text"]').first();

		await nameInput.clear();
		await nameInput.fill('New Custom Views');

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await page.reload();

		await expect(
			page.getByRole('link', {name: 'New Custom Views'})
		).toBeVisible();
	}
);

test('can use external reference code field in view column', async ({
	apiHelpers,
	objectViewPage,
	page,
	viewObjectEntriesPage,
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

	const applicationName = 'c/' + objectDefinition.name!.toLowerCase() + 's';

	await apiHelpers.objectEntry.postObjectEntry(
		{[objectFields[0].name]: 'Entry Test'},
		applicationName
	);

	await objectViewPage.goto(objectDefinition.label['en_US']);

	const viewName = 'Custom Views';

	await objectViewPage.createObjectView(viewName);

	await page.getByRole('link', {name: viewName}).click();

	await objectViewPage.markAsDefault.check();

	await objectViewPage.viewBuilderTab.click();

	await objectViewPage.iframe
		.getByRole('button', {name: 'Add Column'})
		.click({timeout: 500});

	await page.getByRole('checkbox', {name: 'External Reference Code'}).check();

	await page.getByRole('checkbox', {name: objectFields[0].name}).check();

	await page.getByRole('button', {name: 'Save'}).click();

	await page
		.locator('iframe')
		.contentFrame()
		.getByRole('button', {name: 'Save'})
		.click();

	await viewObjectEntriesPage.goto(objectDefinition.className!);

	await expect(page.getByRole('cell', {name: 'Entry Test'})).toBeVisible();

	await expect(
		page.getByRole('columnheader', {name: 'External Reference Code'})
	).toBeVisible();
});

test(
	'can view a formula field value on a custom view',
	{tag: '@LPD-102828'},
	async ({
		apiHelpers,
		editObjectViewPage,
		objectViewPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const {
			firstObjectField,
			formulaObjectField,
			objectFields,
			secondObjectField,
		} = generateFormulaObjectFields({
			objectFieldBusinessType: 'Decimal',
			operator: '*',
			output: 'Decimal',
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

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.markAsDefaultButton.check();

		await editObjectViewPage.selectObjectFields([
			formulaObjectField.label.en_US,
		]);

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await apiHelpers.objectEntry.postObjectEntry(
			{
				[firstObjectField.name as string]: 2468,
				[secondObjectField.name as string]: 5,
			},
			'c/' + objectDefinition.name.toLowerCase() + 's'
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page.getByRole('cell', {exact: true, name: '12340'})
		).toBeVisible();
	}
);

test(
	'can view entries of object in a table view defined as default',
	{tag: '@LPS-144902'},
	async ({apiHelpers, page, viewObjectEntriesPage}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectViewAPIClient =
			await apiHelpers.buildRestClient(ObjectViewAPI);

		await objectViewAPIClient.postObjectDefinitionObjectView(
			objectDefinition.id,
			{
				defaultObjectView: true,
				name: {en_US: 'CustomView' + getRandomInt()},
				objectViewColumns: [
					{
						objectFieldName: 'textField',
						priority: 0,
					},
				],
			}
		);

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Entry Test'},
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(page.getByText('Entry Test').first()).toBeVisible();
	}
);

test(
	'can view entries of object with default sort defined',
	{tag: '@LPS-144472'},
	async ({apiHelpers, page, viewObjectEntriesPage}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		for (const letter of ['A', 'B']) {
			await apiHelpers.objectEntry.postObjectEntry(
				{textField: `Entry ${letter}`},
				applicationName
			);
		}

		const objectViewAPIClient =
			await apiHelpers.buildRestClient(ObjectViewAPI);

		await objectViewAPIClient.postObjectDefinitionObjectView(
			objectDefinition.id,
			{
				defaultObjectView: true,
				name: {en_US: 'CustomView' + getRandomInt()},
				objectViewColumns: [
					{
						objectFieldName: 'textField',
						priority: 0,
					},
				],
				objectViewSortColumns: [
					{
						objectFieldName: 'textField',
						priority: 0,
						sortOrder: 'asc',
					},
				],
			}
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		const cells = page.locator('table tbody tr td:nth-child(2)');

		await expect(cells.first()).toHaveText('Entry A');
		await expect(cells.last()).toHaveText('Entry B');
	}
);

test(
	'can view metadata values correctly displayed on a custom view',
	{tag: '@LPS-143190'},
	async ({apiHelpers, page, viewObjectEntriesPage}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Entry Test'},
			applicationName
		);

		const objectViewAPIClient =
			await apiHelpers.buildRestClient(ObjectViewAPI);

		await objectViewAPIClient.postObjectDefinitionObjectView(
			objectDefinition.id,
			{
				defaultObjectView: true,
				name: {en_US: 'CustomView' + getRandomInt()},
				objectViewColumns: [
					{objectFieldName: 'creator', priority: 0},
					{objectFieldName: 'createDate', priority: 1},
					{objectFieldName: 'modifiedDate', priority: 2},
					{objectFieldName: 'status', priority: 3},
					{objectFieldName: 'id', priority: 4},
				],
			}
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		for (const columnName of [
			'Author',
			'Create Date',
			'Modified Date',
			'Status',
			'ID',
		]) {
			await expect(
				page.getByRole('columnheader', {name: columnName})
			).toBeVisible();
		}
	}
);

test(
	'can view only selected columns on custom view',
	{tag: '@LPS-144902'},
	async ({apiHelpers, page, viewObjectEntriesPage}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectViewAPIClient =
			await apiHelpers.buildRestClient(ObjectViewAPI);

		await objectViewAPIClient.postObjectDefinitionObjectView(
			objectDefinition.id,
			{
				defaultObjectView: true,
				name: {en_US: 'CustomView' + getRandomInt()},
				objectViewColumns: [
					{objectFieldName: 'creator', priority: 0},
					{objectFieldName: 'createDate', priority: 1},
					{objectFieldName: 'textField', priority: 2},
				],
			}
		);

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Entry Test'},
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		for (const columnName of ['Author', 'Create Date', 'textField']) {
			await expect(
				page.getByRole('columnheader', {name: columnName})
			).toBeVisible();
		}

		for (const columnName of [
			'External Reference Code',
			'Modified Date',
			'Status',
			'ID',
		]) {
			await expect(
				page.getByRole('columnheader', {name: columnName})
			).toBeHidden();
		}
	}
);

test(
	'can view values of two or more relationship fields for the same object',
	{tag: '@LPS-148955'},
	async ({apiHelpers, page, viewObjectEntriesPage}) => {
		const objectDefinitionA =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinitionA.id,
			type: 'objectDefinition',
		});

		const objectDefinitionB =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinitionB.id,
			type: 'objectDefinition',
		});

		const objectDefinitionC =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinitionC.id,
			type: 'objectDefinition',
		});

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationshipAC} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectDefinitionA.externalReferenceCode,
				{
					label: {en_US: 'Relationship A' + getRandomInt()},
					name: await getFreshObjectRelationshipName(
						apiHelpers,
						[
							objectDefinitionA.externalReferenceCode!,
							objectDefinitionC.externalReferenceCode!,
						],
						'relationshipA'
					),
					objectDefinitionExternalReferenceCode1:
						objectDefinitionA.externalReferenceCode,
					objectDefinitionExternalReferenceCode2:
						objectDefinitionC.externalReferenceCode,
					objectDefinitionId1: objectDefinitionA.id,
					objectDefinitionId2: objectDefinitionC.id,
					objectDefinitionName2: objectDefinitionC.name,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationshipAC.id,
			type: 'objectRelationship',
		});

		const {body: objectRelationshipBC} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectDefinitionB.externalReferenceCode,
				{
					label: {en_US: 'Relationship B' + getRandomInt()},
					name: await getFreshObjectRelationshipName(
						apiHelpers,
						[
							objectDefinitionB.externalReferenceCode!,
							objectDefinitionC.externalReferenceCode!,
						],
						'relationshipB'
					),
					objectDefinitionExternalReferenceCode1:
						objectDefinitionB.externalReferenceCode,
					objectDefinitionExternalReferenceCode2:
						objectDefinitionC.externalReferenceCode,
					objectDefinitionId1: objectDefinitionB.id,
					objectDefinitionId2: objectDefinitionC.id,
					objectDefinitionName2: objectDefinitionC.name,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationshipBC.id,
			type: 'objectRelationship',
		});

		const objectEntryA = await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Entry A'},
			'c/' + objectDefinitionA.name.toLowerCase() + 's'
		);

		const objectEntryB = await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Entry B'},
			'c/' + objectDefinitionB.name.toLowerCase() + 's'
		);

		const relationshipFieldNameAC = `r_${objectRelationshipAC.name}_c_${objectDefinitionA.name[0].toLowerCase() + objectDefinitionA.name.substring(1)}Id`;

		const relationshipFieldNameBC = `r_${objectRelationshipBC.name}_c_${objectDefinitionB.name[0].toLowerCase() + objectDefinitionB.name.substring(1)}Id`;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				[relationshipFieldNameAC]: objectEntryA.id.toString(),
				[relationshipFieldNameBC]: objectEntryB.id.toString(),
				textField: 'Entry C',
			},
			'c/' + objectDefinitionC.name.toLowerCase() + 's'
		);

		const objectViewAPIClient =
			await apiHelpers.buildRestClient(ObjectViewAPI);

		await objectViewAPIClient.postObjectDefinitionObjectView(
			objectDefinitionC.id,
			{
				defaultObjectView: true,
				name: {en_US: 'CustomView' + getRandomInt()},
				objectViewColumns: [
					{
						objectFieldName: relationshipFieldNameAC,
						priority: 0,
					},
					{
						objectFieldName: relationshipFieldNameBC,
						priority: 1,
					},
				],
			}
		);

		await viewObjectEntriesPage.goto(objectDefinitionC.className);

		await expect(
			page.getByRole('cell', {exact: true, name: String(objectEntryA.id)})
		).toBeVisible();

		await expect(
			page.getByRole('cell', {exact: true, name: String(objectEntryB.id)})
		).toBeVisible();
	}
);

test('cannot create an object custom view using empty multiselectpicklist entry', async ({
	apiHelpers,
	editObjectViewPage,
	objectViewPage,
	page,
}) => {
	const listTypeDefinition =
		await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

	apiHelpers.data.push({
		id: listTypeDefinition.id,
		type: 'listTypeDefinition',
	});

	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFields: [
				{
					DBType: 'String',
					businessType: 'MultiselectPicklist',
					externalReferenceCode: 'customPicklist',
					label: {
						en_US: 'customPicklist',
					},
					listTypeDefinitionExternalReferenceCode:
						listTypeDefinition.externalReferenceCode,
					name: 'customPicklist',
					state: false,
				},
			],
			status: {code: 0},
		});

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	const objectViewName = getRandomString();

	await objectViewPage.goto(objectDefinition.label['en_US']);

	await objectViewPage.createObjectView(objectViewName);

	await page.getByRole('link', {name: objectViewName}).click();

	await editObjectViewPage.createFilter('customPicklist', 'Includes');

	await expect(
		page.frameLocator('iframe').getByText('Required')
	).toBeVisible();
});

test(
	'cannot filter by external reference code field in custom view',
	{tag: '@LPD-102828'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.filtersTab.click();

		await editObjectViewPage.newFilterButton.click();

		await editObjectViewPage.filterBy.click();

		await expect(
			editObjectViewPage.sidePanel.getByRole('option', {
				exact: true,
				name: 'Status',
			})
		).toBeVisible();

		await expect(
			editObjectViewPage.sidePanel.getByRole('option', {
				exact: true,
				name: 'External Reference Code',
			})
		).toHaveCount(0);
	}
);

test(
	'cannot leave name field empty when creating a custom view',
	{tag: '@LPS-135394'},
	async ({apiHelpers, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		await objectViewPage.addObjectViewButton.click();

		await objectViewPage.objectViewNameSaveModalButton.click();

		await expect(page.getByText('Required')).toBeVisible();
	}
);

test(
	'cannot save a view as default when one is already set',
	{tag: '@LPS-135394'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewNameA = 'CustomViewA' + getRandomInt();

		await objectViewPage.createObjectView(viewNameA);

		await page.getByRole('link', {name: viewNameA}).waitFor();

		await page.getByRole('link', {name: viewNameA}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.selectObjectFields(['Status']);

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await page.reload();

		const viewNameB = 'CustomViewB' + getRandomInt();

		await objectViewPage.createObjectView(viewNameB);

		await page.getByRole('link', {name: viewNameB}).waitFor();

		await page.getByRole('link', {name: viewNameB}).click();

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.selectObjectFields(['Author']);

		await editObjectViewPage.saveButton.last().click();

		await waitForAlert(page, 'There can only be one default object view', {
			type: 'danger',
		});
	}
);

test(
	'cannot save a view set as default when there are no columns selected',
	{tag: '@LPS-135394'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.saveButton.last().click();

		await waitForAlert(
			page,
			'Default view must have at least one column.',
			{type: 'danger'}
		);
	}
);

test(
	'columns are ordered following the predefined order on custom view',
	{tag: '@LPS-144902'},
	async ({apiHelpers, page, viewObjectEntriesPage}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectViewAPIClient =
			await apiHelpers.buildRestClient(ObjectViewAPI);

		await objectViewAPIClient.postObjectDefinitionObjectView(
			objectDefinition.id,
			{
				defaultObjectView: true,
				name: {en_US: 'CustomView' + getRandomInt()},
				objectViewColumns: [
					{objectFieldName: 'createDate', priority: 0},
					{objectFieldName: 'creator', priority: 1},
					{objectFieldName: 'id', priority: 2},
				],
			}
		);

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{customField: 'Entry Test'},
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		const descendingCells = page.getByRole('columnheader');

		await expect(descendingCells).toContainText([
			'Create Date',
			'Author',
			'ID',
		]);
	}
);

test(
	'duplicated object view columns are correctly ordered',
	{tag: '@LPS-146028'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		await sidePanel.getByLabel('Mark as Default').check();

		await editObjectViewPage.selectObjectFields([
			'Author',
			'Create Date',
			'Modified Date',
			'Status',
			'ID',
			'textField',
		]);

		await editObjectViewPage.saveButton.last().click();

		await page.waitForLoadState('networkidle');

		await page.reload();

		await page
			.getByRole('row', {name: viewName})
			.getByRole('button', {name: 'Actions'})
			.click();

		await page.getByRole('menuitem', {name: 'Duplicate'}).click();

		await page.getByText('(Copy)').first().waitFor();

		await page.getByRole('link', {name: `${viewName} (Copy)`}).click();

		await editObjectViewPage.viewBuilderTab.click();

		const columns = sidePanel.locator(
			'.lfr-object__object-custom-view-builder-item'
		);

		await expect(columns.nth(0)).toContainText('Author');
		await expect(columns.nth(1)).toContainText('Create Date');
		await expect(columns.nth(2)).toContainText('Modified Date');
		await expect(columns.nth(3)).toContainText('Status');
		await expect(columns.nth(4)).toContainText('ID');
		await expect(columns.nth(5)).toContainText('textField');
	}
);

test(
	'duplicated view has same original name with copy suffix',
	{tag: '@LPS-146028'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page
			.getByRole('row', {name: viewName})
			.getByRole('button', {name: 'Actions'})
			.click();

		await page.getByRole('menuitem', {name: 'Duplicate'}).click();

		await page.getByText('(Copy)').first().waitFor();

		await page.getByRole('link', {name: `${viewName} (Copy)`}).click();

		const sidePanel = editObjectViewPage.sidePanel;

		const nameInput = sidePanel.locator('input[type="text"]').first();

		await expect(nameInput).toHaveValue(`${viewName} (Copy)`);
	}
);

test(
	'metadata columns are displayed for selection in custom view',
	{tag: '@LPS-135394'},
	async ({apiHelpers, editObjectViewPage, objectViewPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectViewPage.goto(objectDefinition.label['en_US']);

		const viewName = 'CustomView' + getRandomInt();

		await objectViewPage.createObjectView(viewName);

		await page.getByRole('link', {name: viewName}).waitFor();

		await page.getByRole('link', {name: viewName}).click();

		await editObjectViewPage.viewBuilderTab.click();

		const addButton = editObjectViewPage.addColumnButton.or(
			editObjectViewPage.addButton
		);

		await addButton.click();

		const modal = editObjectViewPage.addColumnsModal;

		for (const columnOption of [
			'Author',
			'Create Date',
			'External Reference Code',
			'Modified Date',
			'Status',
			'ID',
		]) {
			await expect(modal.getByText(columnOption)).toBeVisible();
		}
	}
);
