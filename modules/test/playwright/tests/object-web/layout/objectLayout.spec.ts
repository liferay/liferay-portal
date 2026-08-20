/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionAPI,
	ObjectField,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {editObjectDefinitionPagesTest} from '../../../fixtures/editObjectDefinitionPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {generateFormulaObjectFields} from '../utils/generateFormulaObjectFields';
import {generateObjectEntryValues} from '../utils/generateObjectEntry';
import {generateObjectFields} from '../utils/generateObjectFields';
import {getFreshObjectRelationshipName} from '../utils/getFreshObjectRelationshipName';
import getRandomObjectFieldText from '../utils/getRandomObjectFieldText';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	editObjectDefinitionPagesTest,
	loginTest(),
	objectPagesTest
);

test.describe('Manage custom layouts through object layout tab', () => {
	test('can add a block to a layout', async ({
		apiHelpers,
		objectLayoutsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectLayoutsPage.goto(objectDefinition.name);

		const objectLayoutName = getRandomString();
		const blockName = getRandomString();

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await objectLayoutsPage.openObjectLayoutConfiguration(objectLayoutName);

		await objectLayoutsPage.createObjectLayoutTab(getRandomString());

		await objectLayoutsPage.createObjectLayoutBlock({
			objectLayoutRegularBlockName: blockName,
		});

		await expect(
			objectLayoutsPage.iframeLocator.getByText(blockName)
		).toBeVisible();
	});

	test(
		'can add a field to the second column of a block',
		{tag: '@LPD-102828'},
		async ({apiHelpers, objectLayoutsPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await objectLayoutsPage.goto(objectDefinition.name);

			const objectLayoutName = getRandomString();

			await objectLayoutsPage.createObjectLayout(objectLayoutName);

			await objectLayoutsPage.openObjectLayoutConfiguration(
				objectLayoutName
			);

			await objectLayoutsPage.createObjectLayoutTab(getRandomString());

			await objectLayoutsPage.createObjectLayoutBlock({
				objectLayoutRegularBlockName: getRandomString(),
			});

			await objectLayoutsPage.openObjectLayoutObjectField();

			await objectLayoutsPage.addObjectLayoutObjectField('textField', 2);

			await expect(
				objectLayoutsPage.layoutTabPanel.getByText('textField')
			).toBeVisible();
		}
	);

	test('can add a field to the third column of a block', async ({
		apiHelpers,
		objectLayoutsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectLayoutsPage.goto(objectDefinition.name);

		const objectLayoutName = getRandomString();

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await objectLayoutsPage.openObjectLayoutConfiguration(objectLayoutName);

		await objectLayoutsPage.createObjectLayoutTab(getRandomString());

		await objectLayoutsPage.createObjectLayoutBlock({
			objectLayoutRegularBlockName: getRandomString(),
		});

		await objectLayoutsPage.openObjectLayoutObjectField();

		await objectLayoutsPage.addObjectLayoutObjectField('textField', 3);

		await expect(
			objectLayoutsPage.layoutTabPanel.getByText('textField')
		).toBeVisible();
	});

	test('can add a layout', async ({apiHelpers, objectLayoutsPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectLayoutsPage.goto(objectDefinition.name);

		const objectLayoutName = getRandomString();

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await expect(
			page.getByRole('cell', {exact: true, name: objectLayoutName})
		).toBeVisible();
	});

	test('can add a tab of type Fields', async ({
		apiHelpers,
		objectLayoutsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectLayoutsPage.goto(objectDefinition.name);

		const objectLayoutName = getRandomString();
		const tabName = getRandomString();

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await objectLayoutsPage.openObjectLayoutConfiguration(objectLayoutName);

		await objectLayoutsPage.createObjectLayoutTab(tabName);

		await expect(
			objectLayoutsPage.iframeLocator.getByText(tabName)
		).toBeVisible();
	});

	test('can add seo block when creating its layout', async ({
		apiHelpers,
		objectLayoutsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				enableFriendlyURLCustomization: true,
				status: {code: 0},
				titleObjectFieldName: 'textField',
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await test.step('create layout with SEO block and set it as default', async () => {
			await objectLayoutsPage.goto(objectDefinition.name);

			const objectLayoutName = getRandomString();

			await objectLayoutsPage.createObjectLayout(objectLayoutName);

			await objectLayoutsPage.createObjectLayoutContent({
				hasSeoBlock: true,
				objectFieldNames: ['textField'],
				objectLayoutName,
				objectLayoutRegularBlockName: getRandomString(),
				objectLayoutTabName: getRandomString(),
			});

			await objectLayoutsPage.setObjectLayoutAsDefault();

			await page
				.frameLocator('iframe')
				.getByRole('button', {name: 'Save'})
				.first()
				.click();
		});

		await test.step('add object entry with custom friendly URL', async () => {
			await viewObjectEntriesPage.goto(objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await viewObjectEntriesPage.fillObjectEntry({
				objectFieldBusinessType: 'Text',
				objectFieldLabel: 'textField',
				objectFieldValue: 'Entry A',
			});

			await viewObjectEntriesPage.friendlyUrlInput.fill(
				'Entry A friendlyURL'
			);

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await waitForAlert(page);

			await expect(page.getByLabel('Friendly URL')).toHaveValue(
				'entry-a-friendlyurl'
			);
		});
	});

	test(
		'can add tabs for self Many-to-Many relationship',
		{tag: '@LPS-163656'},
		async ({apiHelpers, objectLayoutsPage}) => {
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

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const objectRelationshipName1 =
				await getFreshObjectRelationshipName(apiHelpers, [
					objectDefinition.externalReferenceCode!,
				]);
			const objectRelationshipName2 =
				await getFreshObjectRelationshipName(apiHelpers, [
					objectDefinition.externalReferenceCode!,
				]);

			const {body: objectRelationship1} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition.externalReferenceCode,
					{
						label: {en_US: 'Relationship'},
						name: objectRelationshipName1,
						objectDefinitionExternalReferenceCode1:
							objectDefinition.externalReferenceCode,
						objectDefinitionExternalReferenceCode2:
							objectDefinition.externalReferenceCode,
						objectDefinitionId1: objectDefinition.id,
						objectDefinitionId2: objectDefinition.id,
						objectDefinitionName2: objectDefinition.name,
						type: 'manyToMany',
					}
				);

			apiHelpers.data.push({
				id: objectRelationship1.id,
				type: 'objectRelationship',
			});

			const {body: objectRelationship2} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition.externalReferenceCode,
					{
						label: {en_US: 'Relationship 2'},
						name: objectRelationshipName2,
						objectDefinitionExternalReferenceCode1:
							objectDefinition.externalReferenceCode,
						objectDefinitionExternalReferenceCode2:
							objectDefinition.externalReferenceCode,
						objectDefinitionId1: objectDefinition.id,
						objectDefinitionId2: objectDefinition.id,
						objectDefinitionName2: objectDefinition.name,
						type: 'manyToMany',
					}
				);

			apiHelpers.data.push({
				id: objectRelationship2.id,
				type: 'objectRelationship',
			});

			await objectLayoutsPage.goto(objectDefinition.label['en_US']);

			const layoutName = 'Layout' + getRandomInt();

			await objectLayoutsPage.createObjectLayout(layoutName);

			await objectLayoutsPage.openObjectLayoutConfiguration(layoutName);

			await objectLayoutsPage.layoutTab.click();

			await objectLayoutsPage.createObjectLayoutTab('Field Tab');

			const tabs = [
				{optionIndex: 0, tabLabel: 'Relationship Tab A'},
				{optionIndex: 1, tabLabel: 'Relationship Tab B'},
			];

			for (const {optionIndex, tabLabel} of tabs) {
				await objectLayoutsPage.addTab.click();

				const addTabDialog =
					objectLayoutsPage.iframeLocator.getByLabel('Add Tab');

				await addTabDialog.getByLabel('Label').fill(tabLabel);

				await addTabDialog
					.getByText('Relationships', {exact: true})
					.click();

				await addTabDialog
					.getByRole('combobox', {name: 'Relationship'})
					.click();

				await objectLayoutsPage.iframeLocator
					.getByRole('option')
					.nth(optionIndex)
					.click();

				await objectLayoutsPage.saveTabButton.click();

				await objectLayoutsPage.saveUpdateLayoutButton.click();
			}

			await expect(
				objectLayoutsPage.iframeLocator.getByText('Relationship Tab A')
			).toBeVisible();

			await expect(
				objectLayoutsPage.iframeLocator.getByText('Relationship Tab B')
			).toBeVisible();
		}
	);

	test('can cancel an in-progress layout update', async ({
		apiHelpers,
		objectLayoutsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectLayoutsPage.goto(objectDefinition.name);

		const objectLayoutName = getRandomString();

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await page.getByRole('link', {name: objectLayoutName}).click();

		await objectLayoutsPage.iframeLocator
			.getByRole('tab', {name: 'Info'})
			.click();

		await objectLayoutsPage.iframeLocator
			.getByRole('textbox')
			.fill('Layout Updated');

		await objectLayoutsPage.iframeLocator
			.getByRole('button', {name: 'Cancel'})
			.first()
			.click();

		await page.reload();

		await expect(page.getByText(objectLayoutName).first()).toBeVisible();
		await expect(page.getByText('Layout Updated')).toBeHidden();
	});

	test('can create, read, update and delete relationship child entry when selected', async ({
		apiHelpers,
		objectLayoutsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinitionLabel1 = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName1 = 'ObjectDefinitionName' + getRandomInt();

		const objectDefinitionLabel2 = 'ObjectDefinitionLabel' + getRandomInt();
		const objectDefinitionName2 = 'ObjectDefinitionName' + getRandomInt();

		const objectFields1: Partial<ObjectField>[] = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const {objectEntry: objectEntry1} = await generateObjectEntryValues({
			objectEntryFormat: 'UI',
			objectFields: objectFields1,
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				label: {
					en_US: objectDefinitionLabel1,
				},
				name: objectDefinitionName1,
				objectFields: objectFields1,
				pluralLabel: {
					en_US: objectDefinitionLabel1,
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

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		const parentObjectEntry = await apiHelpers.objectEntry.postObjectEntry(
			objectEntry1,
			applicationName
		);

		const objectFields2: Partial<ObjectField>[] = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const {objectEntry: objectEntry2} = await generateObjectEntryValues({
			objectEntryFormat: 'UI',
			objectFields: objectFields2,
		});

		const {body: objectDefinition2} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				label: {
					en_US: objectDefinitionLabel2,
				},
				name: objectDefinitionName2,
				objectFields: objectFields2,
				pluralLabel: {
					en_US: objectDefinitionLabel2,
				},
				portlet: true,
				scope: 'company',
				status: {
					code: 0,
				},
			});

		apiHelpers.data.push({
			id: objectDefinition2.id,
			type: 'objectDefinition',
		});

		const applicationName2 =
			'c/' + objectDefinition2.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			objectEntry2,
			applicationName2
		);

		const objectRelationshipApiClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipApiClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectDefinition.externalReferenceCode,
				{
					label: {
						en_US: 'objectRelationshipLabel' + getRandomInt(),
					},
					name: await getFreshObjectRelationshipName(apiHelpers, [
						objectDefinition.externalReferenceCode!,
						objectDefinition2.externalReferenceCode!,
					]),
					objectDefinitionExternalReferenceCode1:
						objectDefinition.externalReferenceCode,
					objectDefinitionExternalReferenceCode2:
						objectDefinition2.externalReferenceCode,
					objectDefinitionId1: objectDefinition.id,
					objectDefinitionId2: objectDefinition2.id,
					objectDefinitionName2: objectDefinition2.name,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await objectLayoutsPage.goto(objectDefinitionLabel1);

		const objectLayoutName = getRandomString();

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await objectLayoutsPage.createObjectLayoutContent({
			objectFieldNames: [objectFields1[0].label.en_US],
			objectLayoutName,
			objectLayoutRegularBlockName: getRandomString(),
			objectLayoutTabName: getRandomString(),
		});

		await objectLayoutsPage.setObjectLayoutAsDefault();

		const objectLayoutRelTabName = getRandomString();

		const {reload} = await objectLayoutsPage.createObjectRelationshipTab(
			objectLayoutName,
			objectLayoutRelTabName,
			objectRelationship.label.en_US
		);

		await waitForAlert(
			page,
			'Success:The object layout was updated successfully'
		);

		await reload;

		const objectChildEntry = 'ChildEntry' + getRandomInt();

		await test.step('Create relationship entry', async () => {
			await viewObjectEntriesPage.goto(objectDefinition.className);

			await page
				.getByRole('link', {name: parentObjectEntry.id.toString()})
				.click();

			await page
				.getByRole('link')
				.filter({hasText: objectLayoutRelTabName})
				.click();

			await page.getByRole('button', {name: 'New'}).first().click();

			await page.getByRole('menuitem', {name: 'Create New'}).click();

			await page
				.getByLabel(objectFields2[0].label.en_US)
				.fill(objectChildEntry);

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(page);
		});

		const relationshipInput = page.getByPlaceholder('Search');

		await test.step('Read relationship input value', async () => {
			await viewObjectEntriesPage.backButton.click();

			await page.getByRole('cell').getByRole('link').click();

			await page
				.getByLabel(objectFields2[0].label.en_US)
				.waitFor({state: 'visible'});

			await expect(relationshipInput).toHaveValue(
				parentObjectEntry.externalReferenceCode
			);

			await expect(
				page.getByLabel(objectFields2[0].label.en_US)
			).toHaveValue(objectChildEntry);
		});

		await test.step('Update relationship input value', async () => {
			const newEntry = await generateObjectEntryValues({
				objectEntryFormat: 'UI',
				objectFields: objectFields1,
			});

			const newRelationshipEntry =
				await apiHelpers.objectEntry.postObjectEntry(
					newEntry,
					applicationName
				);

			await page.reload();

			await relationshipInput.click();

			await page
				.getByRole('menuitem', {
					name: newRelationshipEntry.externalReferenceCode,
				})
				.click();

			await page.getByRole('button', {name: 'Save'}).click();

			await expect(relationshipInput).toHaveValue(
				newRelationshipEntry.externalReferenceCode
			);
		});

		await test.step('Delete relationship input value', async () => {
			await relationshipInput.fill('');

			await page.keyboard.press('Enter');

			await page.getByRole('button', {name: 'Save'}).click();

			await expect(relationshipInput).toHaveValue('');
		});
	});

	test('can delete a field from a layout', async ({
		apiHelpers,
		objectLayoutsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectLayoutsPage.goto(objectDefinition.name);

		const objectLayoutName = getRandomString();

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await objectLayoutsPage.createObjectLayoutContent({
			objectFieldNames: ['textField'],
			objectLayoutName,
			objectLayoutRegularBlockName: getRandomString(),
			objectLayoutTabName: getRandomString(),
		});

		await objectLayoutsPage.iframeLocator.getByText('textField').hover();

		await page
			.frameLocator('iframe')
			.getByLabel('More Actions')
			.nth(2)
			.click();

		await page
			.frameLocator('iframe')
			.getByRole('menuitem', {name: 'Delete'})
			.click();

		await expect(
			objectLayoutsPage.iframeLocator.getByText('textField')
		).toBeHidden();
	});

	test('can delete a layout', async ({
		apiHelpers,
		objectLayoutsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectLayoutsPage.goto(objectDefinition.name);

		const objectLayoutName = getRandomString();

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await page
			.getByRole('row')
			.filter({hasText: objectLayoutName})
			.locator('.dropdown-toggle')
			.click();

		await page.getByRole('menuitem', {name: 'Delete'}).click();

		await waitForAlert(page);

		await expect(page.getByText(objectLayoutName)).toBeHidden();
	});

	test(
		'can see label when creating Relationship Tab',
		{tag: '@LPS-163660'},
		async ({
			addNewObjectRelationshipModalPage,
			apiHelpers,
			objectLayoutsPage,
			objectRelationshipsPage,
			page: _page,
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

			await objectRelationshipsPage.goto(objectDefinition.label['en_US']);

			await objectRelationshipsPage.addObjectRelationshipButton.click();

			const objectRelationship =
				await addNewObjectRelationshipModalPage.handleForm({
					manyRecordsOf: objectDefinition.label['en_US'],
					objectRelationshipLabel: 'Relationship',
					type: 'Many to Many',
				});

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await objectLayoutsPage.goto(objectDefinition.label['en_US']);

			const layoutName = 'Layout' + getRandomInt();

			await objectLayoutsPage.createObjectLayout(layoutName);

			await objectLayoutsPage.openObjectLayoutConfiguration(layoutName);

			await objectLayoutsPage.setObjectLayoutAsDefault();

			await objectLayoutsPage.layoutTab.click();

			await objectLayoutsPage.createObjectLayoutTab('Field Tab');

			await objectLayoutsPage.addTab.click();

			await objectLayoutsPage.relationshipType.click();

			await objectLayoutsPage.fieldList.click();

			await expect(
				objectLayoutsPage.iframeLocator.getByText('Parent')
			).toBeVisible();

			await expect(
				objectLayoutsPage.iframeLocator.getByText('Child')
			).toBeVisible();
		}
	);

	test(
		"can update a layout's name",
		{tag: '@LPD-102828'},
		async ({apiHelpers, objectLayoutsPage, page}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await objectLayoutsPage.goto(objectDefinition.name);

			const objectLayoutName = getRandomString();

			await objectLayoutsPage.createObjectLayout(objectLayoutName);

			await objectLayoutsPage.createObjectLayoutContent({
				objectFieldNames: ['textField'],
				objectLayoutName,
				objectLayoutRegularBlockName: getRandomString(),
				objectLayoutTabName: getRandomString(),
			});

			await objectLayoutsPage.setObjectLayoutAsDefault();

			await objectLayoutsPage.iframeLocator
				.getByRole('button', {name: 'Save'})
				.first()
				.click();

			await waitForAlert(
				page,
				'The object layout was updated successfully'
			);

			await page.getByRole('link', {name: objectLayoutName}).click();

			await objectLayoutsPage.iframeLocator
				.getByRole('tab', {name: 'Info'})
				.click();

			const updatedObjectLayoutName = getRandomString();

			await objectLayoutsPage.layoutInfoNameInput.fill(
				updatedObjectLayoutName
			);

			await objectLayoutsPage.iframeLocator
				.getByRole('button', {name: 'Save'})
				.first()
				.click();

			await expect(
				page.getByText(updatedObjectLayoutName).first()
			).toBeVisible();

			await expect(page.getByText(objectLayoutName)).toHaveCount(0);
		}
	);

	test('can update an entry on the relationship tab with update permission', async ({
		apiHelpers,
		objectLayoutsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				panelCategoryKey: 'control_panel.object',
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
				objectDefinition.externalReferenceCode,
				{
					label: {
						en_US: 'Relationship' + getRandomInt(),
					},
					name: await getFreshObjectRelationshipName(
						apiHelpers,
						[objectDefinition.externalReferenceCode!],
						'relationship'
					),
					objectDefinitionExternalReferenceCode1:
						objectDefinition.externalReferenceCode,
					objectDefinitionExternalReferenceCode2:
						objectDefinition.externalReferenceCode,
					objectDefinitionId1: objectDefinition.id,
					objectDefinitionId2: objectDefinition.id,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		const objectLayoutName = getRandomString();

		await objectLayoutsPage.goto(objectDefinition.name);

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await page.getByRole('link', {name: objectLayoutName}).click();

		await objectLayoutsPage.markAsDefaultButton.check();

		await objectLayoutsPage.createObjectLayoutContent({
			objectFieldNames: ['textField'],
			objectLayoutName,
			objectLayoutRegularBlockName: 'Block 1',
			objectLayoutTabName: 'Field Tab',
		});

		const {reload} = await objectLayoutsPage.createObjectRelationshipTab(
			objectLayoutName,
			'Relationship Tab',
			objectRelationship.label['en_US']
		);

		await waitForAlert(
			page,
			'Success:The object layout was updated successfully'
		);

		await reload;

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Entry Test'},
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await page
			.getByRole('row', {name: 'Entry Test'})
			.getByRole('link')
			.click();

		await page.getByRole('link', {name: 'Relationship Tab'}).click();

		await page.getByRole('button', {name: 'New'}).first().click();

		await page.getByRole('menuitem', {name: 'Create New'}).click();

		await page.getByLabel('textField').fill('New entry related');

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'role' + getRandomInt(),
			rolePermissions: [
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: String(company.companyId),
					resourceName: `com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_${objectDefinition.className.split('#')[1]}`,
					scope: 1,
				},
				{
					actionIds: ['VIEW', 'UPDATE'],
					primaryKey: String(company.companyId),
					resourceName: objectDefinition.className,
					scope: 1,
				},
			],
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		await performUserSwitch(page, user.alternateName);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.frontendDatasetItems.first().click();

		await page.getByRole('link', {name: 'Relationship Tab'}).click();

		await viewObjectEntriesPage.frontendDatasetItems.first().click();

		await page.getByLabel('textField').fill('Entry Updated');

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page.getByRole('row', {name: 'Entry Updated'})
		).toBeVisible();
	});

	test(
		'can view a formula field value on a custom layout',
		{tag: '@LPD-102828'},
		async ({
			apiHelpers,
			objectLayoutsPage,
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
				operator: '+',
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

			await objectLayoutsPage.goto(objectDefinition.name);

			const objectLayoutName = getRandomString();

			await objectLayoutsPage.createObjectLayout(objectLayoutName);

			await objectLayoutsPage.openObjectLayoutConfiguration(
				objectLayoutName
			);

			await objectLayoutsPage.setObjectLayoutAsDefault();

			await objectLayoutsPage.layoutTab.click();

			await objectLayoutsPage.createObjectLayoutTab(getRandomString());

			await objectLayoutsPage.createObjectLayoutBlock({
				objectLayoutRegularBlockName: getRandomString(),
			});

			for (const {label} of objectFields) {
				await objectLayoutsPage.openObjectLayoutObjectField();

				await objectLayoutsPage.addObjectLayoutObjectField(label.en_US);
			}

			await objectLayoutsPage.saveUpdateLayoutButton.click();

			const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					[firstObjectField.name as string]: 1234,
					[secondObjectField.name as string]: 4321,
				},
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

			await viewObjectEntriesPage.goto(objectDefinition.className);

			await page
				.getByRole('link', {name: String(objectEntry.id)})
				.click();

			await expect(
				page.getByLabel(formulaObjectField.label.en_US)
			).toHaveValue('5555');
		}
	);

	test('can view all fields of an object when creating its layout', async ({
		apiHelpers,
		objectLayoutsPage,
		page,
	}) => {
		const objectFields = getRandomObjectFieldText({
			objectFieldsQuantity: 20,
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		const objectLayoutName = getRandomString();

		await objectLayoutsPage.goto(objectDefinition.name);

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await objectLayoutsPage.openObjectLayoutConfiguration(objectLayoutName);

		await objectLayoutsPage.createObjectLayoutTab(getRandomString());

		await objectLayoutsPage.createObjectLayoutBlock({
			objectLayoutRegularBlockName: getRandomString(),
		});

		await objectLayoutsPage.openObjectLayoutObjectField();

		objectFields.forEach(({label}) => {
			expect(
				page
					.frameLocator('iframe')
					.getByRole('option', {name: label.en_US})
			).toBeVisible();
		});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		await objectDefinitionAPIClient.deleteObjectDefinition(
			objectDefinition.id
		);
	});

	test('can view entries with custom layout created', async ({
		apiHelpers,
		objectLayoutsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
				titleObjectFieldName: 'textField',
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const blockName = getRandomString();

		await test.step('create layout and set it as default', async () => {
			await objectLayoutsPage.goto(objectDefinition.name);

			const objectLayoutName = getRandomString();

			await objectLayoutsPage.createObjectLayout(objectLayoutName);

			await objectLayoutsPage.createObjectLayoutContent({
				objectFieldNames: ['textField'],
				objectLayoutName,
				objectLayoutRegularBlockName: blockName,
				objectLayoutTabName: getRandomString(),
			});

			await objectLayoutsPage.setObjectLayoutAsDefault();

			await page
				.frameLocator('iframe')
				.getByRole('button', {name: 'Save'})
				.first()
				.click();
		});

		await test.step('add object entry and assert that blockname is visible', async () => {
			await viewObjectEntriesPage.goto(objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await viewObjectEntriesPage.fillObjectEntry({
				objectFieldBusinessType: 'Text',
				objectFieldLabel: 'textField',
				objectFieldValue: 'Entry A',
			});

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await waitForAlert(page);

			await viewObjectEntriesPage.backButton.click();

			await page.waitForLoadState('networkidle');

			await page
				.getByRole('row', {name: 'Entry A'})
				.getByRole('link')
				.click();

			await expect(
				page.locator('label').filter({hasText: blockName})
			).toBeVisible();

			await expect(page.getByLabel('textField')).toBeVisible();
		});
	});

	test(
		'cannot add a field to a block when no field is selected',
		{tag: '@LPD-102828'},
		async ({apiHelpers, objectLayoutsPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await objectLayoutsPage.goto(objectDefinition.name);

			const objectLayoutName = getRandomString();

			await objectLayoutsPage.createObjectLayout(objectLayoutName);

			await objectLayoutsPage.openObjectLayoutConfiguration(
				objectLayoutName
			);

			await objectLayoutsPage.createObjectLayoutTab(getRandomString());

			await objectLayoutsPage.createObjectLayoutBlock({
				objectLayoutRegularBlockName: getRandomString(),
			});

			await objectLayoutsPage.addField.click();

			await objectLayoutsPage.saveAddFieldButton.click();

			await expect(
				objectLayoutsPage.iframeLocator.getByText('Required')
			).toBeVisible();
		}
	);

	test('cannot add a Relationship tab as the first tab', async ({
		apiHelpers,
		objectLayoutsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectLayoutsPage.goto(objectDefinition.name);

		const objectLayoutName = getRandomString();

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await objectLayoutsPage.openObjectLayoutConfiguration(objectLayoutName);

		await objectLayoutsPage.addTab.click();

		await expect(
			objectLayoutsPage.iframeLocator.getByRole('menuitem', {
				name: 'Relationship Tab',
			})
		).toBeHidden();
	});

	test('cannot add a Relationship tab to an object without relationships', async ({
		apiHelpers,
		objectLayoutsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectLayoutsPage.goto(objectDefinition.name);

		const objectLayoutName = getRandomString();

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await objectLayoutsPage.openObjectLayoutConfiguration(objectLayoutName);

		await objectLayoutsPage.createObjectLayoutTab(getRandomString());

		await objectLayoutsPage.addTab.click();

		await expect(
			objectLayoutsPage.iframeLocator.getByRole('menuitem', {
				name: 'Relationship Tab',
			})
		).toBeHidden();
	});

	test(
		'cannot save layout as default when other is already set',
		{tag: ['@LPS-165850']},
		async ({apiHelpers, objectLayoutsPage, page}) => {
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

			await objectLayoutsPage.goto(objectDefinition.name);

			await objectLayoutsPage.goto(objectDefinition.label.en_US);

			const objectLayout1Name = getRandomString();

			await objectLayoutsPage.createObjectLayout(objectLayout1Name);

			await objectLayoutsPage.createObjectLayoutContent({
				objectFieldNames: [objectFields[0].label.en_US],
				objectLayoutName: objectLayout1Name,
				objectLayoutRegularBlockName: getRandomString(),
				objectLayoutTabName: getRandomString(),
			});

			await objectLayoutsPage.setObjectLayoutAsDefault();

			await page
				.frameLocator('iframe')
				.getByRole('button', {name: 'Save'})
				.first()
				.click();

			const objectLayout2Name = getRandomString();

			await objectLayoutsPage.createObjectLayout(objectLayout2Name);

			await objectLayoutsPage.createObjectLayoutContent({
				objectFieldNames: [objectFields[0].label.en_US],
				objectLayoutName: objectLayout2Name,
				objectLayoutRegularBlockName: getRandomString(),
				objectLayoutTabName: getRandomString(),
			});

			await objectLayoutsPage.setObjectLayoutAsDefault();

			await page
				.frameLocator('iframe')
				.getByRole('button', {name: 'Save'})
				.first()
				.click();

			await waitForAlert(
				page,
				'Error:There can only be one default object layout',
				{type: 'danger'}
			);
		}
	);

	test('cannot set a layout as default without the required fields on the first tab', async ({
		apiHelpers,
		objectLayoutsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await objectLayoutsPage.goto(objectDefinition.name);

		const objectLayoutName = getRandomString();

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await objectLayoutsPage.openObjectLayoutConfiguration(objectLayoutName);

		await objectLayoutsPage.createObjectLayoutTab(getRandomString());

		await objectLayoutsPage.createObjectLayoutBlock({
			objectLayoutRegularBlockName: getRandomString(),
		});

		await objectLayoutsPage.setObjectLayoutAsDefault();

		await objectLayoutsPage.iframeLocator
			.getByRole('button', {name: 'Save'})
			.first()
			.click();

		await expect(
			page.getByText('Error:Please add at least one field')
		).toBeVisible();
	});

	test('cannot update an entry on the relationship tab without update permission', async ({
		apiHelpers,
		objectLayoutsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				panelCategoryKey: 'control_panel.object',
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
				objectDefinition.externalReferenceCode,
				{
					label: {
						en_US: 'Relationship' + getRandomInt(),
					},
					name: await getFreshObjectRelationshipName(
						apiHelpers,
						[objectDefinition.externalReferenceCode!],
						'relationship'
					),
					objectDefinitionExternalReferenceCode1:
						objectDefinition.externalReferenceCode,
					objectDefinitionExternalReferenceCode2:
						objectDefinition.externalReferenceCode,
					objectDefinitionId1: objectDefinition.id,
					objectDefinitionId2: objectDefinition.id,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		const objectLayoutName = getRandomString();

		await objectLayoutsPage.goto(objectDefinition.name);

		await objectLayoutsPage.createObjectLayout(objectLayoutName);

		await page.getByRole('link', {name: objectLayoutName}).click();

		await objectLayoutsPage.markAsDefaultButton.check();

		await objectLayoutsPage.createObjectLayoutContent({
			objectFieldNames: ['textField'],
			objectLayoutName,
			objectLayoutRegularBlockName: 'Block 1',
			objectLayoutTabName: 'Field Tab',
		});

		const {reload} = await objectLayoutsPage.createObjectRelationshipTab(
			objectLayoutName,
			'Relationship Tab',
			objectRelationship.label['en_US']
		);

		await waitForAlert(
			page,
			'Success:The object layout was updated successfully'
		);

		await reload;

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: 'Entry Test'},
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await page
			.getByRole('row', {name: 'Entry Test'})
			.getByRole('link')
			.click();

		await page.getByRole('link', {name: 'Relationship Tab'}).click();

		await page.getByRole('button', {name: 'New'}).first().click();

		await page.getByRole('menuitem', {name: 'Create New'}).click();

		await page.getByLabel('textField').fill('New entry related');

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'role' + getRandomInt(),
			rolePermissions: [
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL'],
					primaryKey: String(company.companyId),
					resourceName: `com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_${objectDefinition.className.split('#')[1]}`,
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: String(company.companyId),
					resourceName: objectDefinition.className,
					scope: 1,
				},
			],
		});

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		await performUserSwitch(page, user.alternateName);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.frontendDatasetItems.first().click();

		await expect(page.getByLabel('textField')).toBeDisabled();

		await expect(viewObjectEntriesPage.saveObjectEntryButton).toBeHidden();

		await page.getByRole('link', {name: 'Relationship Tab'}).click();

		await viewObjectEntriesPage.frontendDatasetItems.first().click();

		await expect(page.getByLabel('textField')).toBeDisabled();

		await expect(viewObjectEntriesPage.saveObjectEntryButton).toBeHidden();
	});

	test('seo and categorization blocks can be added and removed from layout', async ({
		apiHelpers,
		objectLayoutsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				enableFriendlyURLCustomization: true,
				status: {code: 0},
				titleObjectFieldName: 'textField',
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectLayoutName = getRandomString();

		await test.step('create layout with SEO and Categorization collapsible blocks and set it as default', async () => {
			await objectLayoutsPage.goto(objectDefinition.name);

			await objectLayoutsPage.createObjectLayout(objectLayoutName);

			await objectLayoutsPage.createObjectLayoutContent({
				hasCategorizationBlock: true,
				hasSeoBlock: true,
				objectFieldNames: ['textField'],
				objectLayoutName,
				objectLayoutRegularBlockName: getRandomString(),
				objectLayoutTabName: getRandomString(),
			});

			await objectLayoutsPage.toggleCollapsible('Categorization');

			await objectLayoutsPage.toggleCollapsible('SEO');

			await objectLayoutsPage.setObjectLayoutAsDefault();

			await page
				.frameLocator('iframe')
				.getByRole('button', {name: 'Save'})
				.first()
				.click();
		});

		await test.step('verify SEO and Categorization blocks are visible and collapsible', async () => {
			await viewObjectEntriesPage.goto(objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await expect(
				page.getByRole('button', {name: 'Categorization'})
			).toBeVisible();

			await expect(page.getByRole('button', {name: 'SEO'})).toBeVisible();
		});

		await test.step('edit layout and remove SEO and Categorization blocks', async () => {
			await objectLayoutsPage.goto(objectDefinition.name);

			await page.getByRole('link', {name: objectLayoutName}).click();

			await objectLayoutsPage.layoutTab.click();

			const frame = page.locator('iframe').contentFrame();
			const itemsToDelete = 2;

			for (let i = 0; i < itemsToDelete; i++) {
				await frame
					.getByRole('button', {name: 'More Actions'})
					.last()
					.click();
				await frame.getByRole('menuitem', {name: 'Delete'}).click();
			}

			await objectLayoutsPage.saveUpdateLayoutButton.click();
		});

		await test.step('verify that blocks are not visible anymore', async () => {
			await viewObjectEntriesPage.goto(objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				objectDefinition.label['en_US']
			);

			await expect(
				page.getByRole('button', {name: 'Categorization'})
			).not.toBeVisible();

			await expect(
				page.getByRole('button', {name: 'SEO'})
			).not.toBeVisible();
		});
	});
});
