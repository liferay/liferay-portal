/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionAPI,
	ObjectField,
	ObjectFieldAPI,
	ObjectRelationship,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {ObjectRelationshipFormPage} from '../../../pages/object-web/object-relationship/ObjectRelationshipFormPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {generateObjectFields} from '../utils/generateObjectFields';
import {getFreshObjectRelationshipName} from '../utils/getFreshObjectRelationshipName';

export const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	objectPagesTest
);

test.beforeEach(({page}) => {
	page.setViewportSize({height: 1080, width: 1920});
});

test.describe('Manage object relationships through Model Builder', () => {
	test(
		'allows to create relationship with a custom object named Address',
		{tag: '@LPD-51156'},
		async ({
			addNewObjectRelationshipModalPage,
			apiHelpers,
			modelBuilderDiagramPage,
			page,
		}) => {
			const objectFolder =
				await apiHelpers.objectAdmin.postRandomObjectFolder();

			apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body: customPostalAddressDefinition} =
				await objectDefinitionAPIClient.postObjectDefinition({
					active: true,
					externalReferenceCode:
						'CustomPostalAddress' + getRandomInt(),
					label: {
						en_US: 'Custom Postal Address',
					},
					name: 'Address',
					objectFields: [],
					objectFolderExternalReferenceCode:
						objectFolder.externalReferenceCode,
					pluralLabel: {
						en_US: 'Custom Postal Addresses',
					},
					portlet: true,
					scope: 'company',
					status: {
						code: 1,
					},
				});

			apiHelpers.data.push({
				id: customPostalAddressDefinition.id,
				type: 'objectDefinition',
			});

			const partnerObjectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode:
						objectFolder.externalReferenceCode,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: partnerObjectDefinition.id,
				type: 'objectDefinition',
			});

			await test.step('navigate to model builder and display all nodes', async () => {
				await modelBuilderDiagramPage.goto({
					objectFolderName: objectFolder.name,
				});

				await modelBuilderDiagramPage.toggleSidebarsButton.click();

				await modelBuilderDiagramPage.fitViewButton.click();
			});

			await test.step('assert that creating a relationship with a custom object named Address as the source node is allowed', async () => {
				await modelBuilderDiagramPage.connectObjectDefinitionsNodeHandles(
					customPostalAddressDefinition.id,
					partnerObjectDefinition.id
				);

				await expect(
					addNewObjectRelationshipModalPage.modalHeader
				).toBeVisible();
			});

			await test.step('assert that creating a relationship with a custom object named Address as the target node is allowed', async () => {
				await page.getByRole('button', {name: 'Cancel'}).click();

				await modelBuilderDiagramPage.connectObjectDefinitionsNodeHandles(
					partnerObjectDefinition.id,
					customPostalAddressDefinition.id
				);

				await expect(
					addNewObjectRelationshipModalPage.modalHeader
				).toBeVisible();
			});
		}
	);

	test('assert that relationship Many records of field shows correct definition label', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		page,
	}) => {
		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				status: {code: 0},
			});
		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
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

		const objectRelationshipData: Partial<ObjectRelationship> = {
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
		};

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectDefinition1.externalReferenceCode,
				objectRelationshipData
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await modelBuilderDiagramPage.goto({
			objectFolderName: objectFolder.name,
		});

		await modelBuilderDiagramPage.clickObjectRelationshipEdge(
			objectRelationship.label['en_US']
		);

		const objectRelationshipFormPage = new ObjectRelationshipFormPage(
			page,
			'.form-group'
		);

		await expect(
			objectRelationshipFormPage.manyRecordsOfSelect
		).toHaveValue(objectDefinition2.label['en_US']);
	});

	test('can create multiple object relationships between the same objects', async ({
		addNewObjectRelationshipModalPage,
		apiHelpers,
		modelBuilderDiagramPage,
		page,
	}) => {
		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				status: {code: 0},
			});
		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
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

		await modelBuilderDiagramPage.goto({
			objectFolderName: objectFolder.name,
		});

		await modelBuilderDiagramPage.connectObjectDefinitionsNodeHandles(
			objectDefinition1.id,
			objectDefinition2.id
		);

		const objectRelationship1Label = 'objectRelationship' + getRandomInt();

		await addNewObjectRelationshipModalPage.handleForm({
			objectRelationshipLabel: objectRelationship1Label,
			type: 'One to Many',
		});

		await modelBuilderDiagramPage.connectObjectDefinitionsNodeHandles(
			objectDefinition2.id,
			objectDefinition1.id,
			['top', 'bottom']
		);

		const objectRelationship2Label = 'objectRelationship' + getRandomInt();

		await addNewObjectRelationshipModalPage.handleForm({
			objectRelationshipLabel: objectRelationship2Label,
			type: 'One to Many',
		});

		const manyRelationshipsEdge =
			modelBuilderDiagramPage.objectRelationshipEdges.filter({
				has: page.getByText('2', {exact: true}),
			});

		await expect(manyRelationshipsEdge).toBeVisible();

		await manyRelationshipsEdge.click();

		await expect(
			page.getByRole('menuitem', {name: objectRelationship1Label})
		).toBeVisible();

		await expect(
			page.getByRole('menuitem', {name: objectRelationship2Label})
		).toBeVisible();
	});

	test('can create object relationship to linked object definition by drag and drop', async ({
		addNewObjectRelationshipModalPage,
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderObjectDefinitionNodePage,
		page,
		viewObjectDefinitionsPage,
	}) => {
		await page.goto('/');

		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				status: {code: 0},
			});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		const objectDefinition3 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
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
		apiHelpers.data.push({
			id: objectDefinition3.id,
			type: 'objectDefinition',
		});

		const objectRelationshipLabel =
			'objectRelationshipLabel' + getRandomInt();
		const objectRelationshipName = await getFreshObjectRelationshipName(
			apiHelpers,
			[
				objectDefinition1.externalReferenceCode!,
				objectDefinition2.externalReferenceCode!,
			]
		);

		const objectRelationshipData: Partial<ObjectRelationship> = {
			label: {
				en_US: objectRelationshipLabel,
			},
			name: objectRelationshipName,
			objectDefinitionExternalReferenceCode1:
				objectDefinition1.externalReferenceCode,
			objectDefinitionExternalReferenceCode2:
				objectDefinition2.externalReferenceCode,
			objectDefinitionId1: objectDefinition1.id,
			objectDefinitionId2: objectDefinition2.id,
			objectDefinitionName2: objectDefinition2.name,
			type: 'oneToMany',
		};

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectDefinition1.externalReferenceCode,
				objectRelationshipData
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.openObjectFolder(
			objectFolder.label['en_US']
		);

		await viewObjectDefinitionsPage.viewInModelBuilderButton.click();

		await modelBuilderDiagramPage.toggleSidebarsButton.click();

		await modelBuilderDiagramPage.fitViewButton.click();

		await modelBuilderDiagramPage.connectObjectDefinitionsNodeHandles(
			objectDefinition3.id,
			objectDefinition2.id
		);

		const objectRelationshipLabel2 = 'objectRelationship' + getRandomInt();

		const objectRelationship2 =
			await addNewObjectRelationshipModalPage.handleForm({
				objectRelationshipLabel: objectRelationshipLabel2,
				type: 'One to Many',
			});

		apiHelpers.data.push({
			id: objectRelationship2.id,
			type: 'objectRelationship',
		});

		await expect(
			modelBuilderDiagramPage.objectRelationshipEdges.filter({
				hasText: objectRelationshipLabel2,
			})
		).toBeVisible();

		await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
			objectDefinition2.label['en_US'],
			modelBuilderDiagramPage.objectDefinitionNodes
		);

		await modelBuilderDiagramPage.fitViewButton.click();

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes
				.filter({hasText: objectDefinition2.label['en_US']})
				.getByText(objectRelationshipLabel2)
		).toBeVisible();
	});

	test('can create one to many relationship with object field by dragging node handles', async ({
		addNewObjectRelationshipModalPage,
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderObjectDefinitionNodePage,
		viewObjectDefinitionsPage,
	}) => {
		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				status: {code: 0},
			});
		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
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

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.openObjectFolder(
			objectFolder.label['en_US']
		);

		await viewObjectDefinitionsPage.viewInModelBuilderButton.click();

		await modelBuilderDiagramPage.toggleSidebarsButton.click();

		await modelBuilderDiagramPage.fitViewButton.click();

		await modelBuilderDiagramPage.connectObjectDefinitionsNodeHandles(
			objectDefinition1.id,
			objectDefinition2.id
		);

		const objectRelationshipLabel = 'objectRelationship' + getRandomInt();

		const objectRelationship =
			await addNewObjectRelationshipModalPage.handleForm({
				objectRelationshipLabel,
				type: 'One to Many',
			});

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await expect(
			modelBuilderDiagramPage.objectRelationshipEdges.filter({
				hasText: objectRelationshipLabel,
			})
		).toBeVisible();

		await modelBuilderObjectDefinitionNodePage.clickShowAllFieldsButton(
			objectDefinition2.label['en_US'],
			modelBuilderDiagramPage.objectDefinitionNodes
		);

		await modelBuilderDiagramPage.fitViewButton.click();

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes
				.filter({hasText: objectDefinition2.label['en_US']})
				.getByText(objectRelationshipLabel)
		).toBeVisible();
	});

	test('can create relationship between definitions from different folders', async ({
		addNewObjectRelationshipModalPage,
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
		page,
	}) => {
		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
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

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		await modelBuilderDiagramPage.toggleSidebarsButton.click();

		await modelBuilderDiagramPage.fitViewButton.click();

		await modelBuilderObjectDefinitionNodePage.openAddNewObjectFieldOrRelationshipModal(
			objectDefinition1.label['en_US'],
			modelBuilderDiagramPage.objectDefinitionNodes,
			modelBuilderObjectDefinitionNodePage.addObjectRelationshipButton
		);

		const objectRelationship =
			await addNewObjectRelationshipModalPage.handleForm({
				manyRecordsOf: objectDefinition2.label['en_US'],
				objectRelationshipLabel: 'objectRelationship' + getRandomInt(),
				type: 'One to Many',
			});

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await expect(
			modelBuilderDiagramPage.objectRelationshipEdges.filter({
				hasText: objectRelationship.label['en_US'],
			})
		).toBeVisible();

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes.filter({
				hasText: objectDefinition2.label['en_US'],
			})
		).toBeVisible();

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes
				.filter({
					hasText: objectDefinition2.label['en_US'],
				})
				.locator('.lfr-objects__model-builder-node-container')
		).toHaveClass(/link/);

		await modelBuilderDiagramPage.toggleSidebarsButton.click();

		await modelBuilderLeftSidebarPage.collapseOtherFoldersButton.click();

		await expect(
			page
				.getByRole('group', {name: 'Default'})
				.getByLabel(objectDefinition2.label['en_US'])
		).toBeVisible();
	});

	test('can create relationship by using add relationship button', async ({
		addNewObjectRelationshipModalPage,
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderObjectDefinitionNodePage,
		viewObjectDefinitionsPage,
	}) => {
		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				status: {code: 0},
			});
		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
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

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.openObjectFolder(
			objectFolder.label['en_US']
		);

		await viewObjectDefinitionsPage.viewInModelBuilderButton.click();

		const objectRelationshipLabel = 'objectRelationship' + getRandomInt();

		await modelBuilderObjectDefinitionNodePage.openAddNewObjectFieldOrRelationshipModal(
			objectDefinition2.label['en_US'],
			modelBuilderDiagramPage.objectDefinitionNodes,
			modelBuilderObjectDefinitionNodePage.addObjectRelationshipButton
		);

		const objectRelationship =
			await addNewObjectRelationshipModalPage.handleForm({
				manyRecordsOf: objectDefinition1.name,
				objectRelationshipLabel,
				type: 'One to Many',
			});

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await expect(
			modelBuilderDiagramPage.objectRelationshipEdges.filter({
				hasText: objectRelationshipLabel,
			})
		).toBeVisible();
	});

	test('can create two self object relationship', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectRelationshipDetails: {
			label: string;
			type: 'oneToMany' | 'oneToOne' | 'manyToMany';
		}[] = [
			{
				label: 'objectRelationshipLabel' + getRandomInt(),
				type: 'oneToMany',
			},
			{
				label: 'objectRelationshipLabel' + getRandomInt(),
				type: 'manyToMany',
			},
		];

		for (const {label, type} of objectRelationshipDetails) {
			const objectRelationshipName = await getFreshObjectRelationshipName(
				apiHelpers,
				[objectDefinition.externalReferenceCode!]
			);
			const objectRelationshipData: Partial<ObjectRelationship> = {
				label: {
					en_US: label,
				},
				name: objectRelationshipName,
				objectDefinitionExternalReferenceCode1:
					objectDefinition.externalReferenceCode,
				objectDefinitionExternalReferenceCode2:
					objectDefinition.externalReferenceCode,
				objectDefinitionId1: objectDefinition.id,
				objectDefinitionId2: objectDefinition.id,
				objectDefinitionName2: objectDefinition.name,
				type,
			};

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectRelationshipData.objectDefinitionExternalReferenceCode1,
					objectRelationshipData
				);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});
		}

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.openObjectFolder(
			objectFolder.label['en_US']
		);

		await viewObjectDefinitionsPage.viewInModelBuilderButton.click();

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes.filter({
				hasText: objectDefinition.label['en_US'],
			})
		).toBeVisible();

		await page
			.locator('svg')
			.filter({hasText: '2'})
			.locator('rect')
			.click();

		for (const {label, type} of objectRelationshipDetails) {
			await expect(
				page.getByRole('menuitem', {
					name: label,
				})
			).toBeVisible();
			await expect(
				page.getByRole('menuitem', {
					name: type as any,
				})
			).toBeVisible();
		}
	});

	test('can delete object relationship from different folders', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderRightSidebarPage,
		page,
		viewObjectDefinitionsPage,
	}) => {
		await page.goto('/');

		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
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

		const objectRelationshipLabel =
			'objectRelationshipLabel' + getRandomInt();
		const objectRelationshipName = await getFreshObjectRelationshipName(
			apiHelpers,
			[
				objectDefinition1.externalReferenceCode!,
				objectDefinition2.externalReferenceCode!,
			]
		);

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectDefinition1.externalReferenceCode,
				{
					label: {
						en_US: objectRelationshipLabel,
					},
					name: objectRelationshipName,
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

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.openObjectFolder(
			objectFolder.label['en_US']
		);

		await viewObjectDefinitionsPage.viewInModelBuilderButton.click();

		await expect(
			modelBuilderDiagramPage.objectRelationshipEdges.filter({
				hasText: objectRelationshipLabel,
			})
		).toBeVisible();

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes.filter({
				hasText: objectDefinition2.label['en_US'],
			})
		).toBeVisible();

		await modelBuilderDiagramPage.clickObjectRelationshipEdge(
			objectRelationshipLabel
		);

		await modelBuilderRightSidebarPage.deleteObjectRelationship(
			objectRelationshipName
		);

		apiHelpers.data.splice(
			apiHelpers.data.findIndex(
				(object) =>
					object.id === objectRelationship.id &&
					object.type === 'objectRelationship'
			),
			1
		);

		await expect(
			modelBuilderDiagramPage.objectRelationshipEdges.filter({
				hasText: objectRelationshipLabel,
			})
		).not.toBeVisible();

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes.filter({
				hasText: objectDefinition2.label['en_US'],
			})
		).not.toBeVisible();
	});

	test('can edit object relationship details in Right Sidebar', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderRightSidebarPage,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				status: {code: 0},
			});
		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
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

		const objectRelationshipLabel =
			'objectRelationshipLabel' + getRandomInt();
		const objectRelationshipName = await getFreshObjectRelationshipName(
			apiHelpers,
			[
				objectDefinition1.externalReferenceCode!,
				objectDefinition2.externalReferenceCode!,
			]
		);

		const objectRelationshipData: Partial<ObjectRelationship> = {
			label: {
				en_US: objectRelationshipLabel,
			},
			name: objectRelationshipName,
			objectDefinitionExternalReferenceCode1:
				objectDefinition1.externalReferenceCode,
			objectDefinitionExternalReferenceCode2:
				objectDefinition2.externalReferenceCode,
			objectDefinitionId1: objectDefinition1.id,
			objectDefinitionId2: objectDefinition2.id,
			objectDefinitionName2: objectDefinition2.name,
			type: 'oneToMany',
		};

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectRelationshipData.objectDefinitionExternalReferenceCode1,
				objectRelationshipData
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.openObjectFolder(
			objectFolder.label['en_US']
		);

		await viewObjectDefinitionsPage.viewInModelBuilderButton.click();

		await modelBuilderDiagramPage.clickObjectRelationshipEdge(
			objectRelationshipLabel
		);

		await modelBuilderRightSidebarPage.sidebarLabelInput.fill('Value Test');

		await modelBuilderRightSidebarPage.objectRelationshipDeletionType.click();
		await page.getByRole('option', {name: 'Cascade'}).click();

		await modelBuilderLeftSidebarPage.sidebarItems
			.filter({hasText: objectDefinition1.label['en_US']})
			.click();

		await modelBuilderDiagramPage.clickObjectRelationshipEdge('Value Test');

		await expect(
			modelBuilderRightSidebarPage.sidebarLabelInput
		).toHaveValue('Value Test');
		await expect(
			modelBuilderRightSidebarPage.objectRelationshipDeletionType
		).toContainText('Cascade');
	});

	test(
		'can search for object definition with relationship in model builder view',
		{tag: '@LPS-185676'},
		async ({
			apiHelpers,
			modelBuilderDiagramPage,
			modelBuilderLeftSidebarPage,
			page,
		}) => {
			const objectFolder =
				await apiHelpers.objectAdmin.postRandomObjectFolder();

			apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

			const objectDefinition1 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			const objectDefinition2 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFolderExternalReferenceCode:
						objectFolder.externalReferenceCode,
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

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

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

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await test.step('Search for the object definition that is on the other folder', async () => {
				await modelBuilderDiagramPage.goto({
					objectFolderName: 'Default',
				});

				const searchInput = page.getByRole('textbox', {name: 'Search'});

				await searchInput.fill(objectDefinition2.label.en_US);
			});

			await test.step('Verify both folders are present and the linked object appears twice on the sidebar', async () => {
				await expect(
					modelBuilderLeftSidebarPage.sidebarItems.filter({
						hasText: 'Default',
					})
				).toBeVisible();

				await expect(
					modelBuilderLeftSidebarPage.sidebarItems.filter({
						hasText: objectFolder.label.en_US,
					})
				).toBeVisible();

				await expect(
					modelBuilderLeftSidebarPage.sidebarItems.filter({
						hasText: objectDefinition2.label.en_US,
					})
				).toHaveCount(2);
			});

			await test.step('Verify the searched object and the linked one are visible in the diagram', async () => {
				await expect(
					modelBuilderDiagramPage.objectDefinitionNodes.filter({
						hasText: objectDefinition1.label.en_US,
					})
				).toBeVisible();

				await expect(
					modelBuilderDiagramPage.objectDefinitionNodes.filter({
						hasText: objectDefinition2.label.en_US,
					})
				).toBeVisible();
			});
		}
	);

	test(
		'can view nodes cards after relationship deletion',
		{tag: '@LPS-185676'},
		async ({apiHelpers, modelBuilderDiagramPage, page}) => {
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

			const objectRelationshipLabel =
				'objectRelationshipLabel' + getRandomInt();
			const objectRelationshipName = await getFreshObjectRelationshipName(
				apiHelpers,
				[
					objectDefinition1.externalReferenceCode!,
					objectDefinition2.externalReferenceCode!,
				]
			);

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						label: {
							en_US: objectRelationshipLabel,
						},
						name: objectRelationshipName,
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

			await test.step('Verify both nodes and the relationship edge are visible', async () => {
				await modelBuilderDiagramPage.goto({
					objectFolderName: 'Default',
				});

				await expect(
					modelBuilderDiagramPage.objectDefinitionNodes.filter({
						hasText: objectDefinition1.label['en_US'],
					})
				).toBeVisible();

				await expect(
					modelBuilderDiagramPage.objectDefinitionNodes.filter({
						hasText: objectDefinition2.label['en_US'],
					})
				).toBeVisible();

				await expect(
					modelBuilderDiagramPage.objectRelationshipEdges.filter({
						hasText: objectRelationshipLabel,
					})
				).toBeVisible();
			});

			await test.step('Delete the relationship via API', async () => {
				await objectRelationshipAPIClient.deleteObjectRelationship(
					objectRelationship.id
				);
			});

			await test.step('Reload page to reflect the deletion and assert it', async () => {
				await page.reload();

				await expect(
					modelBuilderDiagramPage.objectRelationshipEdges.filter({
						hasText: objectRelationshipLabel,
					})
				).not.toBeVisible();

				await expect(
					modelBuilderDiagramPage.objectDefinitionNodes.filter({
						hasText: objectDefinition1.label['en_US'],
					})
				).toBeVisible();

				await expect(
					modelBuilderDiagramPage.objectDefinitionNodes.filter({
						hasText: objectDefinition2.label['en_US'],
					})
				).toBeVisible();
			});
		}
	);

	test('cannot delete the object relationship that is the only custom object field from the published object definition', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderRightSidebarPage,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				status: {code: 0},
			});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [],
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				status: {code: 1},
			});

		apiHelpers.data.push({
			id: objectDefinition1.id,
			type: 'objectDefinition',
		});
		apiHelpers.data.push({
			id: objectDefinition2.id,
			type: 'objectDefinition',
		});

		const objectRelationshipLabel =
			'objectRelationshipLabel' + getRandomInt();
		const objectRelationshipName = await getFreshObjectRelationshipName(
			apiHelpers,
			[
				objectDefinition1.externalReferenceCode!,
				objectDefinition2.externalReferenceCode!,
			]
		);

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectDefinition1.externalReferenceCode,
				{
					label: {
						en_US: objectRelationshipLabel,
					},
					name: objectRelationshipName,
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

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: publishedObjectDefinition2} =
			await objectDefinitionAPIClient.postObjectDefinitionPublish(
				objectDefinition2.id
			);

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.openObjectFolder(
			objectFolder.label['en_US']
		);

		await viewObjectDefinitionsPage.viewInModelBuilderButton.click();

		await modelBuilderDiagramPage.fitViewButton.click();

		await modelBuilderDiagramPage.clickObjectRelationshipEdge(
			objectRelationshipLabel
		);

		await modelBuilderRightSidebarPage.deleteObjectRelationship(
			objectRelationship.name
		);

		await expect(modelBuilderDiagramPage.deletionNotAllowed).toBeVisible();

		const objectFieldObjectRelationship =
			publishedObjectDefinition2.objectFields.find(
				(objectField: ObjectField) =>
					objectField.businessType === 'Relationship'
			);

		await expect(
			page.getByText(
				`The object field "${objectFieldObjectRelationship.name}" cannot be deleted because it is the only custom object field of the published object definition "${objectDefinition2.name}". Add at least one object field to the object definition.`
			)
		).toBeVisible();

		const objectFieldAPIClient =
			await apiHelpers.buildRestClient(ObjectFieldAPI);
		await objectFieldAPIClient.postObjectDefinitionByExternalReferenceCodeObjectField(
			publishedObjectDefinition2.externalReferenceCode,
			{
				DBType: 'String',
				businessType: 'Text',
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: '',
				label: {en_US: 'textField'},
				listTypeDefinitionId: 0,
				localized: false,
				name: 'textField',
				readOnly: 'false',
				required: false,
				state: false,
				system: false,
				unique: false,
			}
		);
	});

	test(
		'does not allow to create relationship between Postal Address system object and objects without an one-to-many relationship with Account object',
		{tag: '@LPD-26481'},
		async ({
			apiHelpers,
			modelBuilderDiagramPage,
			page,
			viewObjectDefinitionsPage,
		}) => {
			const objectDefinition1 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const {body: postalAddress} =
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					'L_POSTAL_ADDRESS'
				);

			apiHelpers.data.push({
				id: objectDefinition1.id,
				type: 'objectDefinition',
			});

			await test.step('navigate to model builder and display all nodes', async () => {
				await viewObjectDefinitionsPage.goto();

				await viewObjectDefinitionsPage.openObjectFolder('Default');

				await viewObjectDefinitionsPage.viewInModelBuilderButton.click();

				await modelBuilderDiagramPage.toggleSidebarsButton.click();

				await modelBuilderDiagramPage.fitViewButton.click();
			});

			await test.step('assert that a warning is displayed when the user drags a connection between the nodes', async () => {
				await modelBuilderDiagramPage.connectObjectDefinitionsNodeHandles(
					postalAddress.id,
					objectDefinition1.id
				);

				await expect(
					modelBuilderDiagramPage.postalAddressObjectRelationshipWarning
				).toBeVisible();
			});

			await test.step('assert that a "Learn more" link is displayed in the warning toast and leads to a learn recource', async () => {
				await expect(
					page.getByRole('link', {name: 'Learn more.'})
				).toHaveAttribute(
					'href',
					/accessing-accounts-data-from-custom-objects/
				);
			});
		}
	);
});

test.describe('Manage object relationships through Objects Admin UI', () => {
	test(
		'account restriction is disabled when the linked account relationship is deleted',
		{tag: '@LPS-156824'},
		async ({apiHelpers, editObjectDetailsPage, page}) => {
			let objectDefinition: ObjectDefinition;
			let objectRelationship: ObjectRelationship;

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			await test.step('create object definition and link it to Account with a one-to-many relationship', async () => {
				objectDefinition =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						status: {code: 0},
					});

				apiHelpers.data.push({
					id: objectDefinition.id,
					type: 'objectDefinition',
				});

				({body: objectRelationship} =
					await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
						'L_ACCOUNT',
						{
							label: {en_US: 'Relationship Account'},
							name: await getFreshObjectRelationshipName(
								apiHelpers,
								[
									'L_ACCOUNT',
									objectDefinition.externalReferenceCode!,
								],
								'relationshipAccount'
							),
							objectDefinitionExternalReferenceCode1: 'L_ACCOUNT',
							objectDefinitionExternalReferenceCode2:
								objectDefinition.externalReferenceCode,
							objectDefinitionId2: objectDefinition.id,
							objectDefinitionName2: objectDefinition.name,
							type: 'oneToMany',
						}
					));
			});

			await test.step('enable account restriction and assert it is active', async () => {
				await editObjectDetailsPage.goto(
					objectDefinition.label['en_US']
				);

				await editObjectDetailsPage.enableAccountRestriction(
					'Relationship Account'
				);

				await editObjectDetailsPage.saveObjectDefinition();

				await expect(
					editObjectDetailsPage.accountRestrictionToggle
				).toBeChecked();

				await waitForAlert(
					page,
					'Success:The object was saved successfully.'
				);
			});

			await test.step('delete the relationship and assert account restriction is disabled', async () => {
				await objectRelationshipAPIClient.deleteObjectRelationship(
					objectRelationship.id
				);

				await editObjectDetailsPage.goto(
					objectDefinition.label['en_US']
				);

				await expect(
					editObjectDetailsPage.accountRestrictionToggle
				).not.toBeChecked();

				await expect(
					editObjectDetailsPage.accountRestrictionToggle
				).toBeDisabled();
			});
		}
	);

	test(
		'can cancel relationship creation',
		{tag: '@LPS-135400'},
		async ({apiHelpers, objectRelationshipsPage, page}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await objectRelationshipsPage.goto(objectDefinition.label['en_US']);

			await objectRelationshipsPage.addObjectRelationshipButton.click();

			const objectRelationshipFormPage = new ObjectRelationshipFormPage(
				page,
				'role=dialog'
			);

			await objectRelationshipFormPage.labelInput.fill(
				`Relationship${getRandomInt()}`
			);
			await objectRelationshipFormPage.selectType('One to Many');
			await objectRelationshipFormPage.selectManyRecordsOf(
				objectDefinition.label['en_US']
			);

			await page
				.getByRole('dialog')
				.getByRole('button', {name: 'Cancel'})
				.click();

			await expect(page.getByText('No Results Found')).toBeVisible();
		}
	);

	test(
		'can create many to many relationship',
		{tag: '@LPS-135401'},
		async ({
			addNewObjectRelationshipModalPage,
			apiHelpers,
			objectRelationshipsPage,
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

			apiHelpers.data.push({
				id: objectDefinition1.id,
				type: 'objectDefinition',
			});

			apiHelpers.data.push({
				id: objectDefinition2.id,
				type: 'objectDefinition',
			});

			await objectRelationshipsPage.goto(
				objectDefinition1.label['en_US']
			);

			await objectRelationshipsPage.addObjectRelationshipButton.click();

			const relationshipLabel = `Relationship${getRandomInt()}`;

			const objectRelationship =
				await addNewObjectRelationshipModalPage.handleForm({
					manyRecordsOf: objectDefinition2.label['en_US'],
					objectRelationshipLabel: relationshipLabel,
					type: 'Many to Many',
				});

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await waitForAlert(
				page,
				'Success:Relationship was created successfully'
			);

			await expect(
				page.getByRole('link', {name: relationshipLabel})
			).toBeVisible();
		}
	);

	test('can create object relationship with parameter', async ({
		addNewObjectRelationshipModalPage,
		apiHelpers,
		objectRelationshipsPage,
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

		const objectRelationshipData: Partial<ObjectRelationship> = {
			label: {
				en_US: 'objectRelationshipLabel' + getRandomInt(),
			},
			name: await getFreshObjectRelationshipName(apiHelpers, [
				'L_ACCOUNT',
				objectDefinition.externalReferenceCode!,
			]),
			objectDefinitionExternalReferenceCode2:
				objectDefinition.externalReferenceCode,
			type: 'oneToMany',
		};

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship1} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				'L_ACCOUNT',
				objectRelationshipData
			);

		apiHelpers.data.push({
			id: objectRelationship1.id,
			type: 'objectRelationship',
		});

		await objectRelationshipsPage.goto('Postal Address');

		await objectRelationshipsPage.addObjectRelationshipButton.click();

		const objectRelationship2 =
			await addNewObjectRelationshipModalPage.handleForm({
				manyRecordsOf: objectDefinition.name,
				objectRelationshipLabel:
					'objectRelationshipWithParameter' + getRandomInt(),
				parameter: objectRelationship1.label['en_US'],
				type: 'One to Many',
			});

		apiHelpers.data.push({
			id: objectRelationship2.id,
			type: 'objectRelationship',
		});

		await waitForAlert(
			page,
			'Success:Relationship was created successfully.'
		);

		const objectRelationshipLink = page.getByRole('link', {
			exact: true,
			name: objectRelationship2.label['en_US'],
		});

		await expect(objectRelationshipLink).toBeVisible();

		await objectRelationshipLink.click();

		await expect(
			page.frameLocator('iframe').getByLabel('ParameterMandatory')
		).toHaveText(objectRelationship1.label['en_US']);
	});

	test(
		'can create one to many relationship',
		{tag: '@LPS-135400'},
		async ({
			addNewObjectRelationshipModalPage,
			apiHelpers,
			objectRelationshipsPage,
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

			apiHelpers.data.push({
				id: objectDefinition1.id,
				type: 'objectDefinition',
			});

			apiHelpers.data.push({
				id: objectDefinition2.id,
				type: 'objectDefinition',
			});

			await objectRelationshipsPage.goto(
				objectDefinition1.label['en_US']
			);

			await objectRelationshipsPage.addObjectRelationshipButton.click();

			const relationshipLabel = `Relationship${getRandomInt()}`;

			const objectRelationship =
				await addNewObjectRelationshipModalPage.handleForm({
					manyRecordsOf: objectDefinition2.label['en_US'],
					objectRelationshipLabel: relationshipLabel,
					type: 'One to Many',
				});

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await waitForAlert(
				page,
				'Success:Relationship was created successfully'
			);

			await expect(
				page.getByRole('link', {name: relationshipLabel})
			).toBeVisible();
		}
	);

	test(
		'can delete relationship',
		{tag: '@LPS-135400'},
		async ({apiHelpers, objectRelationshipsPage, page}) => {
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

			const relationshipLabel = `Relationship${getRandomInt()}`;
			const relationshipName = await getFreshObjectRelationshipName(
				apiHelpers,
				[
					objectDefinition1.externalReferenceCode!,
					objectDefinition2.externalReferenceCode!,
				],
				'relationship'
			);

			await (
				await apiHelpers.buildRestClient(ObjectRelationshipAPI)
			).postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectDefinition1.externalReferenceCode,
				{
					label: {en_US: relationshipLabel},
					name: relationshipName,
					objectDefinitionExternalReferenceCode2:
						objectDefinition2.externalReferenceCode,
					objectDefinitionId2: objectDefinition2.id,
					objectDefinitionName2: objectDefinition2.name,
					type: 'oneToMany',
				}
			);

			await objectRelationshipsPage.goto(
				objectDefinition1.label['en_US']
			);

			await objectRelationshipsPage.actionsButton.click();

			await objectRelationshipsPage.deleteObjectRelationshipOption.click();

			await page
				.getByPlaceholder('Confirm relationship name', {exact: false})
				.fill(relationshipName);

			await page.getByRole('button', {name: 'Delete'}).click();

			await expect(page.getByText('No Results Found')).toBeVisible();
		}
	);

	test(
		'can update relationship',
		{tag: '@LPS-135400'},
		async ({apiHelpers, objectRelationshipsPage, page}) => {
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

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const relationshipLabel = `Relationship${getRandomInt()}`;

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						label: {en_US: relationshipLabel},
						name: await getFreshObjectRelationshipName(
							apiHelpers,
							[
								objectDefinition1.externalReferenceCode!,
								objectDefinition2.externalReferenceCode!,
							],
							'relationship'
						),
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

			await objectRelationshipsPage.goto(
				objectDefinition1.label['en_US']
			);

			await page.getByRole('link', {name: relationshipLabel}).click();

			const newLabel = `UpdatedRelationship${getRandomInt()}`;
			const iframe = page.frameLocator('iframe');

			await iframe.getByLabel('LabelMandatory').fill(newLabel);
			await objectRelationshipsPage.saveObjectRelationshipButton.click();

			await waitForAlert(
				page,
				'Success:The object relationship was updated successfully.'
			);

			await expect(
				page.getByRole('link', {name: newLabel})
			).toBeVisible();
		}
	);

	test(
		'can view relationship',
		{tag: '@LPS-135400'},
		async ({apiHelpers, objectRelationshipsPage, page}) => {
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

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const relationshipLabel = `ViewRelationship${getRandomInt()}`;

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						label: {en_US: relationshipLabel},
						name: await getFreshObjectRelationshipName(
							apiHelpers,
							[
								objectDefinition1.externalReferenceCode!,
								objectDefinition2.externalReferenceCode!,
							],
							'viewRelationship'
						),
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

			await objectRelationshipsPage.goto(
				objectDefinition1.label['en_US']
			);

			await expect(
				page.getByRole('link', {name: relationshipLabel})
			).toBeVisible();
		}
	);

	test(
		'cannot create duplicated relationship name',
		{tag: '@LPS-135400'},
		async ({apiHelpers, objectRelationshipsPage, page}) => {
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

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const relationshipLabel = `Relationship${getRandomInt()}`;
			const relationshipName = await getFreshObjectRelationshipName(
				apiHelpers,
				[
					objectDefinition1.externalReferenceCode!,
					objectDefinition2.externalReferenceCode!,
				],
				'relationship'
			);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						label: {en_US: relationshipLabel},
						name: relationshipName,
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

			await objectRelationshipsPage.goto(
				objectDefinition1.label['en_US']
			);

			await objectRelationshipsPage.addObjectRelationshipButton.click();

			const objectRelationshipFormPage = new ObjectRelationshipFormPage(
				page,
				'.modal-content'
			);

			await objectRelationshipFormPage.labelInput.fill(relationshipLabel);

			await objectRelationshipFormPage.nameInput.fill(relationshipName);

			await objectRelationshipFormPage.selectType('One to Many');

			await objectRelationshipFormPage.selectManyRecordsOf(
				objectDefinition2.label['en_US']
			);

			await objectRelationshipFormPage.saveButton.click();

			await waitForAlert(
				page,
				`Error:There is already an object relationship with this name in the object definition "${objectDefinition1.name}"`,
				{autoClose: false, type: 'danger'}
			);
		}
	);

	test(
		'cannot leave relationship fields blank',
		{tag: '@LPS-135400'},
		async ({apiHelpers, objectRelationshipsPage, page}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await objectRelationshipsPage.goto(objectDefinition.label['en_US']);

			await objectRelationshipsPage.addObjectRelationshipButton.click();

			const objectRelationshipFormPage = new ObjectRelationshipFormPage(
				page,
				'.modal-content'
			);

			await objectRelationshipFormPage.saveButton.click();

			await expect(page.getByText('Required')).toHaveCount(3);
		}
	);

	test(
		'cannot submit entry with invalid value on relationship field',
		{tag: '@LPS-135400'},
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
					titleObjectFieldName: 'textField',
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition.externalReferenceCode,
					{
						label: {en_US: `Relationship${getRandomInt()}`},
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

			const applicationName =
				'c/' + objectDefinition.name.toLowerCase() + 's';

			const entryA = await apiHelpers.objectEntry.postObjectEntry(
				{textField: 'Entry A'},
				applicationName
			);

			await viewObjectEntriesPage.goto(objectDefinition.className);

			await page.getByRole('link', {name: entryA.id.toString()}).click();

			await page
				.locator('#editObjectEntry')
				.getByPlaceholder('Search')
				.fill('!@#$%');

			await page
				.locator('.sheet-footer button.btn-primary')
				.click({force: true});

			await expect(
				page
					.locator('.form-feedback-item')
					.filter({hasText: 'The field value is invalid.'})
			).toBeVisible();
		}
	);

	test(
		'creates relationship on both objects for many to many',
		{tag: '@LPS-135401'},
		async ({
			addNewObjectRelationshipModalPage,
			apiHelpers,
			objectRelationshipsPage,
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

			apiHelpers.data.push({
				id: objectDefinition1.id,
				type: 'objectDefinition',
			});

			apiHelpers.data.push({
				id: objectDefinition2.id,
				type: 'objectDefinition',
			});

			await objectRelationshipsPage.goto(
				objectDefinition1.label['en_US']
			);

			await objectRelationshipsPage.addObjectRelationshipButton.click();

			const relationshipLabel = `Relationship${getRandomInt()}`;

			const objectRelationship =
				await addNewObjectRelationshipModalPage.handleForm({
					manyRecordsOf: objectDefinition2.label['en_US'],
					objectRelationshipLabel: relationshipLabel,
					type: 'Many to Many',
				});

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await waitForAlert(
				page,
				'Success:Relationship was created successfully'
			);

			await objectRelationshipsPage.goto(
				objectDefinition2.label['en_US']
			);

			await expect(
				page.getByRole('link', {name: relationshipLabel})
			).toBeVisible();
		}
	);

	test(
		'does not create relationship field on many to many',
		{tag: '@LPS-135401'},
		async ({
			addNewObjectRelationshipModalPage,
			apiHelpers,
			objectRelationshipsPage,
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

			apiHelpers.data.push({
				id: objectDefinition1.id,
				type: 'objectDefinition',
			});

			apiHelpers.data.push({
				id: objectDefinition2.id,
				type: 'objectDefinition',
			});

			await objectRelationshipsPage.goto(
				objectDefinition1.label['en_US']
			);

			await objectRelationshipsPage.addObjectRelationshipButton.click();

			const relationshipLabel = `Relationship${getRandomInt()}`;

			const objectRelationship =
				await addNewObjectRelationshipModalPage.handleForm({
					manyRecordsOf: objectDefinition2.label['en_US'],
					objectRelationshipLabel: relationshipLabel,
					type: 'Many to Many',
				});

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await waitForAlert(
				page,
				'Success:Relationship was created successfully'
			);

			await page.getByRole('link', {name: 'Fields'}).click();

			await expect(
				page.getByRole('link', {name: relationshipLabel})
			).not.toBeVisible();
		}
	);

	test(
		'does not delete other fields when relationship field is deleted',
		{tag: '@LPS-135400'},
		async ({
			apiHelpers,
			objectFieldsPage,
			objectRelationshipsPage,
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

			const relationshipName = await getFreshObjectRelationshipName(
				apiHelpers,
				[
					objectDefinition1.externalReferenceCode!,
					objectDefinition2.externalReferenceCode!,
				],
				'relationship'
			);

			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectDefinition1.externalReferenceCode,
				{
					label: {en_US: `Relationship${getRandomInt()}`},
					name: relationshipName,
					objectDefinitionExternalReferenceCode2:
						objectDefinition2.externalReferenceCode,
					objectDefinitionId2: objectDefinition2.id,
					objectDefinitionName2: objectDefinition2.name,
					type: 'oneToMany',
				}
			);

			await objectRelationshipsPage.goto(
				objectDefinition1.label['en_US']
			);

			await objectRelationshipsPage.actionsButton.click();

			await objectRelationshipsPage.deleteObjectRelationshipOption.click();

			await page
				.getByPlaceholder('Confirm relationship name', {exact: false})
				.fill(relationshipName);

			await page.getByRole('button', {name: 'Delete'}).click();

			await objectFieldsPage.goto(objectDefinition2.label['en_US']);

			await expect(
				page.getByRole('link', {name: 'textField'})
			).toBeVisible();
		}
	);

	test(
		'has prevent deletion type selected by default',
		{tag: '@LPS-135400'},
		async ({
			addNewObjectRelationshipModalPage,
			apiHelpers,
			objectRelationshipsPage,
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

			apiHelpers.data.push({
				id: objectDefinition1.id,
				type: 'objectDefinition',
			});

			apiHelpers.data.push({
				id: objectDefinition2.id,
				type: 'objectDefinition',
			});

			await objectRelationshipsPage.goto(
				objectDefinition1.label['en_US']
			);

			await objectRelationshipsPage.addObjectRelationshipButton.click();

			const relationshipLabel = `Relationship${getRandomInt()}`;

			const objectRelationship =
				await addNewObjectRelationshipModalPage.handleForm({
					manyRecordsOf: objectDefinition2.label['en_US'],
					objectRelationshipLabel: relationshipLabel,
					type: 'One to Many',
				});

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await waitForAlert(
				page,
				'Success:Relationship was created successfully'
			);

			await page.getByRole('link', {name: relationshipLabel}).click();

			await expect(
				page.frameLocator('iframe').getByLabel('Deletion Type')
			).toContainText('Prevent');
		}
	);

	test('object relationship autocomplete field loads more object definitions on scroll', async ({
		apiHelpers,
		objectRelationshipsPage,
		page,
	}) => {
		const objectDefinitionERCPrefix = `ERC${getRandomString().replace(/-/g, '').slice(0, 17)}`;

		for (let i = 1; i <= 21; i++) {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectDefinitionExternalReferenceCode:
						objectDefinitionERCPrefix + i,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});
		}

		await objectRelationshipsPage.goto('Account');

		await objectRelationshipsPage.addObjectRelationshipButton.click();

		const objectRelationshipFormPage = new ObjectRelationshipFormPage(
			page,
			'.modal-content'
		);

		await objectRelationshipFormPage.labelInput.fill(
			'objectRelationShipLabel' + getRandomInt()
		);

		await objectRelationshipFormPage.selectType('One to Many');

		const manyRecordsInput = page.getByRole('combobox', {
			name: 'Many Records Of',
		});

		await manyRecordsInput.fill(objectDefinitionERCPrefix);

		const lastObjectDefinitionOption = page.getByRole('option', {
			name: objectDefinitionERCPrefix + '9',
		});

		await expect(async () => {
			await page.getByRole('listbox').evaluate((listbox) => {
				const menu = listbox.parentElement as HTMLElement;

				menu.scrollTo(0, menu.scrollHeight);
			});

			await expect(lastObjectDefinitionOption).toBeVisible({
				timeout: 2000,
			});
		}).toPass();

		await lastObjectDefinitionOption.click();

		await expect(manyRecordsInput).toHaveValue(
			objectDefinitionERCPrefix + '9'
		);

		await objectRelationshipFormPage.reverseOrderButton.click();

		await expect(page.getByLabel('Many Records Of')).toHaveValue('Account');

		await objectRelationshipFormPage.saveButton.click();

		await waitForAlert(
			page,
			'Success:Relationship was created successfully.'
		);
	});

	test(
		'prevent deletion type blocks parent entry deletion',
		{tag: '@LPS-135401'},
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const objectDefinition1 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
					titleObjectFieldName: 'textField',
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
						label: {en_US: `Relationship${getRandomInt()}`},
						name: await getFreshObjectRelationshipName(
							apiHelpers,
							[
								objectDefinition1.externalReferenceCode!,
								objectDefinition2.externalReferenceCode!,
							],
							'relationship'
						),
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

			const applicationNameA =
				'c/' + objectDefinition1.name.toLowerCase() + 's';

			const applicationNameB =
				'c/' + objectDefinition2.name.toLowerCase() + 's';

			const entryA = await apiHelpers.objectEntry.postObjectEntry(
				{textField: 'Entry A'},
				applicationNameA
			);

			const entryB = await apiHelpers.objectEntry.postObjectEntry(
				{textField: 'Entry B'},
				applicationNameB
			);

			await viewObjectEntriesPage.goto(objectDefinition2.className);

			await page.getByRole('link', {name: entryB.id.toString()}).click();

			await page
				.locator('#editObjectEntry')
				.getByPlaceholder('Search')
				.click();

			await page.getByRole('menuitem', {name: 'Entry A'}).click();

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await waitForAlert(page);

			await viewObjectEntriesPage.goto(objectDefinition1.className);

			await expect(
				page.getByRole('link', {name: entryA.id.toString()})
			).toBeVisible();

			await viewObjectEntriesPage.frontendDatasetActions.click();

			await viewObjectEntriesPage.frontendDatasetDeleteAction.click();

			await page
				.getByRole('dialog', {name: 'Delete Entry'})
				.getByRole('button', {name: 'Delete'})
				.click();

			await expect(page.getByText('Deletion Not Possible')).toBeVisible();
		}
	);

	test(
		'shows empty state when there is no relationship',
		{tag: '@LPS-135400'},
		async ({apiHelpers, objectRelationshipsPage, page}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await objectRelationshipsPage.goto(objectDefinition.label['en_US']);

			await expect(page.getByText('No Results Found')).toBeVisible();
		}
	);

	test(
		'shows name of related object definition on relationship tab',
		{tag: '@LPS-135400'},
		async ({apiHelpers, objectRelationshipsPage, page}) => {
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

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						label: {en_US: `Relationship${getRandomInt()}`},
						name: await getFreshObjectRelationshipName(
							apiHelpers,
							[
								objectDefinition1.externalReferenceCode!,
								objectDefinition2.externalReferenceCode!,
							],
							'relationship'
						),
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

			await objectRelationshipsPage.goto(
				objectDefinition1.label['en_US']
			);

			await expect(
				page.getByText(objectDefinition2.label['en_US'])
			).toBeVisible();
		}
	);

	test(
		'can delete an object after deleting the relationship',
		{tag: '@LPS-150886'},
		async ({
			apiHelpers,
			objectRelationshipsPage,
			page,
			viewObjectDefinitionsPage,
		}) => {
			let objectDefinition1: ObjectDefinition;
			let objectDefinition2: ObjectDefinition;
			let relationshipName: string;

			await test.step('Create object definitions', async () => {
				const objectFields = generateObjectFields({
					objectFieldBusinessTypes: ['Text'],
				});

				objectDefinition1 =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						objectFields,
						status: {code: 0},
					});

				apiHelpers.data.push({
					id: objectDefinition1.id,
					type: 'objectDefinition',
				});

				objectDefinition2 =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						objectFields: generateObjectFields({
							objectFieldBusinessTypes: ['Text'],
						}),
						status: {code: 0},
					});

				apiHelpers.data.push({
					id: objectDefinition2.id,
					type: 'objectDefinition',
				});
			});

			await test.step('Create object relationship', async () => {
				const objectRelationshipAPIClient =
					await apiHelpers.buildRestClient(ObjectRelationshipAPI);

				relationshipName = await getFreshObjectRelationshipName(
					apiHelpers,
					[
						objectDefinition1.externalReferenceCode!,
						objectDefinition2.externalReferenceCode!,
					],
					'relationship'
				);

				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode!,
					{
						label: {en_US: 'Relationship'},
						name: relationshipName,
						objectDefinitionExternalReferenceCode2:
							objectDefinition2.externalReferenceCode,
						objectDefinitionId2: objectDefinition2.id,
						objectDefinitionName2: objectDefinition2.name,
						type: 'oneToMany',
					}
				);
			});

			await test.step('Verify that an object that has a relationship cannot be deleted', async () => {
				await viewObjectDefinitionsPage.goto();

				await viewObjectDefinitionsPage.clickObjectDefinitionActionButton(
					objectDefinition1.label['en_US']
				);

				await viewObjectDefinitionsPage.deleteObjectDefinitionOption.click();

				await expect(
					page.getByText('Deletion Not Allowed')
				).toBeVisible();

				await page.getByRole('button', {name: 'Done'}).click();
			});

			await test.step('Delete the relationship first', async () => {
				await objectRelationshipsPage.goto(
					objectDefinition1.label['en_US']
				);

				await objectRelationshipsPage.actionsButton.click();

				await objectRelationshipsPage.deleteObjectRelationshipOption.click();

				await page
					.getByPlaceholder('Confirm relationship name', {
						exact: false,
					})
					.fill(relationshipName);

				await page.getByRole('button', {name: 'Delete'}).click();

				await expect(page.getByText('No Results Found')).toBeVisible({
					timeout: 15000,
				});
			});

			await test.step('Now delete the object definition', async () => {
				await viewObjectDefinitionsPage.goto();

				await viewObjectDefinitionsPage.clickObjectDefinitionActionButton(
					objectDefinition1.label['en_US']
				);

				await viewObjectDefinitionsPage.deleteObjectDefinitionOption.click();

				await page
					.getByPlaceholder('Confirm Object Definition Name')
					.fill(objectDefinition1.name);

				await page
					.getByRole('button', {exact: true, name: 'Delete'})
					.click();

				apiHelpers.data.splice(
					apiHelpers.data.findIndex(
						(object) =>
							object.id === objectDefinition1.id &&
							object.type === 'objectDefinition'
					),
					1
				);

				await expect(
					viewObjectDefinitionsPage.frontendDataSetEntries.filter({
						hasText: objectDefinition1.label['en_US'],
					})
				).toBeHidden();
			});
		}
	);
});

test.describe('Manage object relationships with system objects', () => {
	test(
		'can delete Many-to-Many relationship between Custom Object entry and System Object entry',
		{tag: '@LPS-146754'},
		async ({
			apiHelpers,
			objectLayoutsPage,
			page,
			viewObjectEntriesPage,
		}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 2},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const userAccount1 =
				await apiHelpers.headlessAdminUser.postUserAccount();

			const userAccount2 =
				await apiHelpers.headlessAdminUser.postUserAccount();

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const objectRelationshipName = await getFreshObjectRelationshipName(
				apiHelpers,
				[objectDefinition.externalReferenceCode!, 'L_USER']
			);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition.externalReferenceCode,
					{
						label: {en_US: 'Relationship'},
						name: objectRelationshipName,
						objectDefinitionExternalReferenceCode1:
							objectDefinition.externalReferenceCode,
						objectDefinitionExternalReferenceCode2: 'L_USER',
						objectDefinitionId1: objectDefinition.id,
						objectDefinitionName2: 'User',
						type: 'manyToMany',
					}
				);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			await objectDefinitionAPIClient.postObjectDefinitionPublish(
				objectDefinition.id
			);

			const {body: publishedObjectDefinition} =
				await objectDefinitionAPIClient.getObjectDefinition(
					objectDefinition.id
				);

			const objectEntriesClassName = publishedObjectDefinition.className;

			const textFieldName = objectFields[0].name;

			await objectLayoutsPage.goto(objectDefinition.label['en_US']);

			const layoutName = 'Layout' + getRandomInt();

			await objectLayoutsPage.createObjectLayout(layoutName);

			await objectLayoutsPage.createObjectLayoutContent({
				objectFieldNames: [objectFields[0].label['en_US']],
				objectLayoutName: layoutName,
				objectLayoutRegularBlockName: 'Block 1',
				objectLayoutTabName: 'Field Tab',
			});

			await objectLayoutsPage.addRelationshipTab(
				'Relationship Tab',
				'Relationship'
			);
			await objectLayoutsPage.setObjectLayoutAsDefault();

			const {reload} =
				await objectLayoutsPage.saveObjectLayoutReturningReload();

			const restPath = `c/${objectDefinition.name.toLowerCase()}s`;

			await apiHelpers.objectEntry.postObjectEntry(
				{[textFieldName]: 'Entry A'},
				restPath
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{[textFieldName]: 'Entry B'},
				restPath
			);

			await reload;

			const relateEntryToBothUsers = async (entryLabel: string) => {
				await viewObjectEntriesPage.goto(objectEntriesClassName);

				await page
					.getByRole('row', {name: new RegExp(entryLabel)})
					.getByRole('button', {name: 'Actions'})
					.click();
				await page.getByRole('menuitem', {name: 'View'}).click();

				await page.getByText('Relationship Tab', {exact: true}).click();

				await page.getByLabel('Select Existing One').first().click();
				await page
					.frameLocator('iframe[title="Select"]')
					.getByText(String(userAccount1.id), {exact: true})
					.first()
					.click();

				await page.reload();

				await page
					.getByRole('link', {exact: true, name: 'Relationship Tab'})
					.click();

				await page.getByLabel('Select Existing One').first().click();
				await page
					.frameLocator('iframe[title="Select"]')
					.getByText(String(userAccount2.id), {exact: true})
					.click();
			};

			const deleteAllRelationsFromEntry = async (entryLabel: string) => {
				await viewObjectEntriesPage.goto(objectEntriesClassName);

				await page
					.getByRole('row', {name: new RegExp(entryLabel)})
					.getByRole('button', {name: 'Actions'})
					.click();
				await page.getByRole('menuitem', {name: 'View'}).click();

				await page.getByText('Relationship Tab', {exact: true}).click();

				const rowActions = page.getByRole('button', {name: 'Actions'});

				const initialCount = await rowActions.count();

				for (let i = 0; i < initialCount; i++) {
					await rowActions.first().click();
					await page.getByRole('menuitem', {name: 'Delete'}).click();
					await page.getByRole('button', {name: 'Delete'}).click();

					await expect(rowActions).toHaveCount(initialCount - i - 1);
				}
			};

			await test.step('relate Entry A to both users', () =>
				relateEntryToBothUsers('Entry A'));

			await test.step('relate Entry B to both users', () =>
				relateEntryToBothUsers('Entry B'));

			await test.step('delete all relations from Entry A', () =>
				deleteAllRelationsFromEntry('Entry A'));

			await test.step('delete all relations from Entry B', () =>
				deleteAllRelationsFromEntry('Entry B'));

			await expect(
				page.getByText(userAccount1.givenName.toLowerCase())
			).toBeHidden();
			await expect(
				page.getByText(userAccount2.givenName.toLowerCase())
			).toBeHidden();
		}
	);

	test(
		'can delete relationship on system object',
		{tag: '@LPS-135406'},
		async ({
			apiHelpers,
			objectRelationshipsPage,
			page,
			viewObjectDefinitionsPage,
		}) => {
			let userObjectRelationship: ObjectRelationship;
			let relationshipLabel: string;
			let relationshipName: string;

			await test.step('Create object definition and relationship', async () => {
				const objectDefinition =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						status: {code: 0},
					});

				apiHelpers.data.push({
					id: objectDefinition.id,
					type: 'objectDefinition',
				});

				const objectRelationshipAPIClient =
					await apiHelpers.buildRestClient(ObjectRelationshipAPI);

				relationshipLabel = 'Relationship' + getRandomInt();
				relationshipName = await getFreshObjectRelationshipName(
					apiHelpers,
					['L_USER', objectDefinition.externalReferenceCode!],
					'relationship'
				);

				const {body: objectRelationship} =
					await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
						'L_USER',
						{
							label: {en_US: relationshipLabel},
							name: relationshipName,
							objectDefinitionExternalReferenceCode2:
								objectDefinition.externalReferenceCode,
							objectDefinitionId2: objectDefinition.id,
							objectDefinitionName2: objectDefinition.name,
							type: 'oneToMany',
						}
					);

				userObjectRelationship = objectRelationship;

				apiHelpers.data.push({
					id: objectRelationship.id,
					type: 'objectRelationship',
				});
			});

			await test.step("Navigate to the User system object's Relationships tab", async () => {
				await viewObjectDefinitionsPage.goto();

				await viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
					'User'
				);

				await objectRelationshipsPage.relationshipTabItem.click();
			});

			await test.step('Delete the relationship', async () => {
				await page
					.getByRole('row', {name: relationshipLabel})
					.getByRole('button')
					.click();

				await objectRelationshipsPage.deleteObjectRelationshipOption.click();

				await page
					.getByPlaceholder('Confirm relationship name', {
						exact: false,
					})
					.fill(relationshipName);

				await page.getByRole('button', {name: 'Delete'}).click();

				apiHelpers.data.splice(
					apiHelpers.data.findIndex(
						(object) =>
							object.id === userObjectRelationship.id &&
							object.type === 'objectRelationship'
					),
					1
				);
			});

			await test.step('Check that the relationship is deleted', async () => {
				await expect(
					page.getByRole('link', {name: relationshipName})
				).not.toBeVisible();
			});
		}
	);

	test(
		'can relate Many-to-Many Custom Object entry with System Object entries',
		{tag: '@LPS-146754'},
		async ({
			apiHelpers,
			objectLayoutsPage,
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

			const userAccount1 =
				await apiHelpers.headlessAdminUser.postUserAccount();

			const userAccount2 =
				await apiHelpers.headlessAdminUser.postUserAccount();

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const objectRelationshipName = await getFreshObjectRelationshipName(
				apiHelpers,
				[objectDefinition.externalReferenceCode!, 'L_USER']
			);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition.externalReferenceCode,
					{
						label: {en_US: 'Relationship'},
						name: objectRelationshipName,
						objectDefinitionExternalReferenceCode1:
							objectDefinition.externalReferenceCode,
						objectDefinitionExternalReferenceCode2: 'L_USER',
						objectDefinitionId1: objectDefinition.id,
						objectDefinitionName2: 'User',
						type: 'manyToMany',
					}
				);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			const textFieldName = objectFields[0].name;

			await objectLayoutsPage.goto(objectDefinition.label['en_US']);

			const layoutName = 'Layout' + getRandomInt();

			await objectLayoutsPage.createObjectLayout(layoutName);

			await objectLayoutsPage.createObjectLayoutContent({
				objectFieldNames: [objectFields[0].label['en_US']],
				objectLayoutName: layoutName,
				objectLayoutRegularBlockName: 'Block 1',
				objectLayoutTabName: 'Field Tab',
			});

			await objectLayoutsPage.addRelationshipTab(
				'Relationship Tab',
				'Relationship'
			);
			await objectLayoutsPage.setObjectLayoutAsDefault();

			const saveButton = page
				.frameLocator('iframe')
				.getByRole('button', {name: 'Save'})
				.first();

			await expect(saveButton).toBeVisible();
			await saveButton.click();
			await page.waitForLoadState('domcontentloaded');

			const restPath = `c/${objectDefinition.name.toLowerCase()}s`;

			await apiHelpers.objectEntry.postObjectEntry(
				{[textFieldName]: 'Entry A'},
				restPath
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{[textFieldName]: 'Entry B'},
				restPath
			);

			const openEntryRelationshipTab = async (entryLabel: string) => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				const entryRow = page.getByRole('row', {
					name: new RegExp(entryLabel),
				});

				await expect(entryRow).toBeVisible();

				await entryRow.getByRole('button', {name: 'Actions'}).click();
				await page.getByRole('menuitem', {name: 'View'}).click();

				await page.waitForLoadState('domcontentloaded');

				const relationshipTab = page.getByRole('link', {
					exact: true,
					name: 'Relationship Tab',
				});

				await expect(relationshipTab).toBeVisible();
				await relationshipTab.click();
			};

			const getRelatedUserRow = (userAccount: TUserAccount) =>
				page
					.getByRole('row')
					.filter({
						hasText: new RegExp(
							`${userAccount.givenName}|${userAccount.id}`,
							'i'
						),
					})
					.first();

			const selectExistingRelationshipEntry = async (
				userAccount: TUserAccount
			) => {
				await page.getByLabel('Select Existing One').first().click();

				const relationshipEntry = page
					.frameLocator('iframe[title="Select"]')
					.getByText(String(userAccount.id), {exact: true})
					.first();

				await expect(relationshipEntry).toBeVisible();
				await relationshipEntry.click();

				await expect(
					page.locator('iframe[title="Select"]')
				).toBeHidden();

				await expect(getRelatedUserRow(userAccount)).toBeVisible();
			};

			for (const entryLabel of ['Entry A', 'Entry B']) {
				await openEntryRelationshipTab(entryLabel);

				await selectExistingRelationshipEntry(userAccount1);

				await openEntryRelationshipTab(entryLabel);

				await expect(getRelatedUserRow(userAccount1)).toBeVisible();

				await selectExistingRelationshipEntry(userAccount2);

				await openEntryRelationshipTab(entryLabel);

				await expect(getRelatedUserRow(userAccount1)).toBeVisible();
				await expect(getRelatedUserRow(userAccount2)).toBeVisible();
			}
		}
	);

	test(
		'can update relationship label on system object',
		{tag: '@LPS-135406'},
		async ({
			apiHelpers,
			objectRelationshipsPage,
			page,
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

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const relationshipLabel = 'Relationship' + getRandomInt();

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					'L_USER',
					{
						label: {en_US: relationshipLabel},
						name: await getFreshObjectRelationshipName(
							apiHelpers,
							['L_USER', objectDefinition.externalReferenceCode!],
							'relationship'
						),
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

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.clickEditObjectDefinitionLink(
				'User'
			);

			await objectRelationshipsPage.relationshipTabItem.click();

			await page.getByRole('link', {name: relationshipLabel}).click();

			const newLabel = 'New Relationship' + getRandomInt();

			await objectRelationshipsPage.labelInput.fill(newLabel);

			await objectRelationshipsPage.saveObjectRelationship();

			await expect(
				page.getByRole('link', {name: newLabel})
			).toBeVisible();
		}
	);
});

test.describe('Manage object relationship entries', () => {
	test(
		'already-related entry is kept on both objects when associated with another entry',
		{tag: '@LPS-135401'},
		async ({
			apiHelpers,
			objectLayoutsPage,
			page,
			viewObjectEntriesPage,
		}) => {
			let objectDefinition1: ObjectDefinition;
			let objectDefinition2: ObjectDefinition;

			await test.step('add two custom objects with a M:M relationship', async () => {
				objectDefinition1 =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						status: {code: 0},
						titleObjectFieldName: 'textField',
					});

				objectDefinition2 =
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
							name: await getFreshObjectRelationshipName(
								apiHelpers,
								[
									objectDefinition1.externalReferenceCode!,
									objectDefinition2.externalReferenceCode!,
								],
								'relationship'
							),
							objectDefinitionExternalReferenceCode1:
								objectDefinition1.externalReferenceCode,
							objectDefinitionExternalReferenceCode2:
								objectDefinition2.externalReferenceCode,
							objectDefinitionId1: objectDefinition1.id,
							objectDefinitionId2: objectDefinition2.id,
							objectDefinitionName2: objectDefinition2.name,
							type: 'manyToMany',
						}
					);

				apiHelpers.data.push({
					id: objectRelationship.id,
					type: 'objectRelationship',
				});
			});

			await test.step('add a layout with a relationship tab', async () => {
				const setupLayout = async (
					objectDefinition: ObjectDefinition,
					fieldLabel: string
				) => {
					const layoutName = 'Layout' + getRandomInt();

					await objectLayoutsPage.goto(
						objectDefinition.label['en_US']
					);

					await objectLayoutsPage.createObjectLayout(layoutName);

					await objectLayoutsPage.createObjectLayoutContent({
						objectFieldNames: [fieldLabel],
						objectLayoutName: layoutName,
						objectLayoutRegularBlockName: 'Block 1',
						objectLayoutTabName: 'Field Tab',
					});

					await objectLayoutsPage.openObjectLayoutConfiguration(
						layoutName
					);

					await objectLayoutsPage.setObjectLayoutAsDefault();

					await objectLayoutsPage.layoutTab.click();

					await objectLayoutsPage.addRelationshipTab(
						'Relationship Tab',
						'Relationship'
					);

					const {reload} =
						await objectLayoutsPage.saveObjectLayoutReturningReload();

					await reload;
				};

				await setupLayout(objectDefinition1, 'textField');

				await setupLayout(objectDefinition2, 'textField');
			});

			await test.step('create entries A1 and A2 for Object A and entry B for Object B ', async () => {
				const applicationName1 = `c/${objectDefinition1.name.toLowerCase()}s`;
				const applicationName2 = `c/${objectDefinition2.name.toLowerCase()}s`;

				for (const letter of ['A1', 'A2']) {
					await apiHelpers.objectEntry.postObjectEntry(
						{['textField']: `Entry ${letter}`},
						applicationName1
					);
				}

				await apiHelpers.objectEntry.postObjectEntry(
					{['textField']: 'Entry B'},
					applicationName2
				);
			});

			const openRelationshipTab = async (
				className: string,
				entryLabel: string
			) => {
				await viewObjectEntriesPage.goto(className);

				const entryRow = page.getByRole('row', {
					name: new RegExp(entryLabel),
				});

				await expect(entryRow).toBeVisible();

				await entryRow.getByRole('button', {name: 'Actions'}).click();

				await page.getByRole('menuitem', {name: 'View'}).click();

				await page.waitForLoadState('domcontentloaded');

				const relationshipTab = page.getByRole('link', {
					exact: true,
					name: 'Relationship Tab',
				});

				await expect(relationshipTab).toBeVisible();

				await relationshipTab.click();
			};

			await test.step('relate Entry B to both Entry A1 and Entry A2 from Object A', async () => {
				for (const letter of ['A1', 'A2']) {
					await openRelationshipTab(
						objectDefinition1.className,
						`Entry ${letter}`
					);

					await page
						.getByLabel('Select Existing One')
						.first()
						.click();

					const selectFrame = page.frameLocator(
						'iframe[title="Select"]'
					);

					const entryBItem = selectFrame
						.getByText('Entry B', {exact: true})
						.first();

					await expect(entryBItem).toBeVisible();

					await entryBItem.click();

					await expect(
						page.locator('iframe[title="Select"]')
					).toBeHidden();

					await expect(
						page
							.getByRole('row')
							.filter({hasText: 'Entry B'})
							.first()
					).toBeVisible();
				}
			});

			await test.step('assert Entry A1 relationship tab still shows Entry B', async () => {
				await openRelationshipTab(
					objectDefinition1.className,
					'Entry A1'
				);

				await expect(
					page.getByRole('row').filter({hasText: 'Entry B'}).first()
				).toBeVisible();
			});
		}
	);

	test(
		'can edit Many-to-Many relationship of Custom Object entries',
		{tag: '@LPS-157229'},
		async ({
			apiHelpers,
			objectLayoutsPage,
			page,
			viewObjectEntriesPage,
		}) => {
			const objectFields1 = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const objectFields2 = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const textFieldName1 = objectFields1[0].name;
			const textFieldName2 = objectFields2[0].name;

			const objectDefinition1 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: objectFields1,
					status: {code: 0},
					titleObjectFieldName: textFieldName1,
				});

			const objectDefinition2 =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: objectFields2,
					status: {code: 0},
					titleObjectFieldName: textFieldName2,
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

			const objectRelationshipName = await getFreshObjectRelationshipName(
				apiHelpers,
				[
					objectDefinition1.externalReferenceCode!,
					objectDefinition2.externalReferenceCode!,
				]
			);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						label: {en_US: 'Relationship'},
						name: objectRelationshipName,
						objectDefinitionExternalReferenceCode1:
							objectDefinition1.externalReferenceCode,
						objectDefinitionExternalReferenceCode2:
							objectDefinition2.externalReferenceCode,
						objectDefinitionId1: objectDefinition1.id,
						objectDefinitionId2: objectDefinition2.id,
						objectDefinitionName2: objectDefinition2.name,
						type: 'manyToMany',
					}
				);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await objectLayoutsPage.goto(objectDefinition1.label['en_US']);

			const layoutName = 'Layout' + getRandomInt();

			await objectLayoutsPage.createObjectLayout(layoutName);

			await objectLayoutsPage.createObjectLayoutContent({
				objectFieldNames: [objectFields1[0].label['en_US']],
				objectLayoutName: layoutName,
				objectLayoutRegularBlockName: 'Block 1',
				objectLayoutTabName: 'Field Tab',
			});

			await objectLayoutsPage.openObjectLayoutConfiguration(layoutName);
			await objectLayoutsPage.setObjectLayoutAsDefault();
			await objectLayoutsPage.layoutTab.click();

			await objectLayoutsPage.addRelationshipTab(
				'Relationship Tab',
				'Relationship'
			);

			const {reload} =
				await objectLayoutsPage.saveObjectLayoutReturningReload();

			const restPath1 = `c/${objectDefinition1.name.toLowerCase()}s`;
			const restPath2 = `c/${objectDefinition2.name.toLowerCase()}s`;

			const entryA = await apiHelpers.objectEntry.postObjectEntry(
				{[textFieldName1]: 'Entry A'},
				restPath1
			);

			const entryB = await apiHelpers.objectEntry.postObjectEntry(
				{[textFieldName2]: 'Entry B'},
				restPath2
			);

			await apiHelpers.objectEntry.putByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				{
					applicationName: restPath1,
					currentExternalReferenceCode: entryA.externalReferenceCode,
					objectRelationshipName,
					relatedExternalReferenceCode: entryB.externalReferenceCode,
				}
			);

			await reload;

			const openObjectEntry = async (
				className: string,
				entryLabel: string
			) => {
				await viewObjectEntriesPage.goto(className);

				const entryRow = page.getByRole('row', {
					name: new RegExp(entryLabel),
				});

				await expect(entryRow).toBeVisible();

				await entryRow.getByRole('button', {name: 'Actions'}).click();
				await page.getByRole('menuitem', {name: 'View'}).click();

				await page.waitForLoadState('domcontentloaded');
			};

			const openRelationshipTab = async () => {
				const relationshipTab = page.getByRole('link', {
					exact: true,
					name: 'Relationship Tab',
				});

				await expect(relationshipTab).toBeVisible();
				await relationshipTab.click();
			};

			await openObjectEntry(objectDefinition1.className, 'Entry A');
			await openRelationshipTab();
			await expect(
				page.getByRole('row').filter({hasText: 'Entry B'}).first()
			).toBeVisible();

			await openObjectEntry(objectDefinition2.className, 'Entry B');
			await page
				.getByLabel(objectFields2[0].label['en_US'], {exact: true})
				.fill('Entry C');

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await waitForAlert(page);

			await openObjectEntry(objectDefinition1.className, 'Entry A');
			await openRelationshipTab();

			await expect(
				page.getByRole('row').filter({hasText: 'Entry C'}).first()
			).toBeVisible();
		}
	);

	test(
		'can relate two entries bidirectionally in a self-referencing One-to-Many relationship',
		{tag: '@LPS-147906'},
		async ({
			apiHelpers,
			objectLayoutsPage,
			page,
			viewObjectEntriesPage,
		}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const textFieldName = objectFields[0].name;

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
					titleObjectFieldName: textFieldName,
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const objectRelationshipName = await getFreshObjectRelationshipName(
				apiHelpers,
				[objectDefinition.externalReferenceCode!]
			);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition.externalReferenceCode,
					{
						label: {en_US: 'Relationship'},
						name: objectRelationshipName,
						objectDefinitionExternalReferenceCode1:
							objectDefinition.externalReferenceCode,
						objectDefinitionExternalReferenceCode2:
							objectDefinition.externalReferenceCode,
						objectDefinitionId1: objectDefinition.id,
						objectDefinitionId2: objectDefinition.id,
						objectDefinitionName2: objectDefinition.name,
						type: 'oneToMany',
					}
				);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await objectLayoutsPage.goto(objectDefinition.label['en_US']);

			const layoutName = 'Layout' + getRandomInt();

			await objectLayoutsPage.createObjectLayout(layoutName);

			await objectLayoutsPage.createObjectLayoutContent({
				objectFieldNames: [
					objectFields[0].label['en_US'],
					'Relationship',
				],
				objectLayoutName: layoutName,
				objectLayoutRegularBlockName: 'Block 1',
				objectLayoutTabName: 'Field Tab',
			});

			await objectLayoutsPage.openObjectLayoutConfiguration(layoutName);
			await objectLayoutsPage.setObjectLayoutAsDefault();
			await objectLayoutsPage.layoutTab.click();

			await objectLayoutsPage.addRelationshipTab(
				'Relationship Tab',
				'Relationship'
			);

			const {reload} =
				await objectLayoutsPage.saveObjectLayoutReturningReload();

			const restPath = `c/${objectDefinition.name.toLowerCase()}s`;

			await apiHelpers.objectEntry.postObjectEntry(
				{[textFieldName]: 'Entry A'},
				restPath
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{[textFieldName]: 'Entry B'},
				restPath
			);

			await reload;

			const openEntryRelationshipTab = async (entryLabel: string) => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				const entryRow = page.getByRole('row', {
					name: new RegExp(entryLabel),
				});

				await expect(entryRow).toBeVisible();

				await entryRow.getByRole('button', {name: 'Actions'}).click();
				await page.getByRole('menuitem', {name: 'View'}).click();

				await page.waitForLoadState('domcontentloaded');

				const relationshipTab = page.getByRole('link', {
					exact: true,
					name: 'Relationship Tab',
				});

				await expect(relationshipTab).toBeVisible();
				await relationshipTab.click();
			};

			const selectExistingRelationshipEntry = async (
				entryLabel: string
			) => {
				await page.getByRole('button', {name: 'New'}).first().click();
				await page
					.getByRole('menuitem', {name: 'Select Existing One'})
					.click();

				const relationshipEntry = page
					.frameLocator('iframe[title="Select"]')
					.getByText(entryLabel, {exact: true})
					.first();

				await expect(relationshipEntry).toBeVisible();
				await relationshipEntry.click();

				await expect(
					page.locator('iframe[title="Select"]')
				).toBeHidden();

				await expect(
					page.getByRole('row').filter({hasText: entryLabel}).first()
				).toBeVisible();
			};

			await openEntryRelationshipTab('Entry A');

			await selectExistingRelationshipEntry('Entry B');

			await openEntryRelationshipTab('Entry B');

			await selectExistingRelationshipEntry('Entry A');

			await viewObjectEntriesPage.goto(objectDefinition.className);

			const tableBody = page.locator('tbody');

			await expect(tableBody.locator('tr')).toHaveCount(2);
			await expect(
				tableBody.getByText('Entry A', {exact: true})
			).toHaveCount(2);
			await expect(
				tableBody.getByText('Entry B', {exact: true})
			).toHaveCount(2);
		}
	);

	test(
		'can see related entries on Relationship tab',
		{tag: '@LPS-158478'},
		async ({
			apiHelpers,
			objectLayoutsPage,
			page,
			viewObjectEntriesPage,
		}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const textFieldName = objectFields[0].name;

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
					titleObjectFieldName: textFieldName,
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const objectRelationshipName = await getFreshObjectRelationshipName(
				apiHelpers,
				[objectDefinition.externalReferenceCode!]
			);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition.externalReferenceCode,
					{
						label: {en_US: 'Relationship'},
						name: objectRelationshipName,
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
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await objectLayoutsPage.goto(objectDefinition.label['en_US']);

			const layoutName = 'Layout' + getRandomInt();

			await objectLayoutsPage.createObjectLayout(layoutName);

			await objectLayoutsPage.createObjectLayoutContent({
				objectFieldNames: [objectFields[0].label['en_US']],
				objectLayoutName: layoutName,
				objectLayoutRegularBlockName: 'Block 1',
				objectLayoutTabName: 'Field Tab',
			});

			await objectLayoutsPage.addRelationshipTab(
				'Relationship Tab',
				'Relationship'
			);
			await objectLayoutsPage.setObjectLayoutAsDefault();

			const {reload} =
				await objectLayoutsPage.saveObjectLayoutReturningReload();

			const restPath = `c/${objectDefinition.name.toLowerCase()}s`;

			await apiHelpers.objectEntry.postObjectEntry(
				{[textFieldName]: 'Entry Test A'},
				restPath
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{[textFieldName]: 'Entry Test B'},
				restPath
			);

			const openEntryRelationshipTab = async (entryLabel: string) => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				const entryRow = page.getByRole('row', {
					name: new RegExp(entryLabel),
				});

				await expect(entryRow).toBeVisible();

				await entryRow.getByRole('button', {name: 'Actions'}).click();
				await page.getByRole('menuitem', {name: 'View'}).click();

				await page.waitForLoadState('domcontentloaded');

				const relationshipTab = page.getByRole('link', {
					exact: true,
					name: 'Relationship Tab',
				});

				await expect(relationshipTab).toBeVisible();
				await relationshipTab.click();
			};

			await reload;

			await openEntryRelationshipTab('Entry Test A');

			await page.getByLabel('Select Existing One').first().click();

			const relationshipEntry = page
				.frameLocator('iframe[title="Select"]')
				.getByText('Entry Test B', {exact: true})
				.first();

			await expect(relationshipEntry).toBeVisible();
			await relationshipEntry.click();

			await expect(page.locator('iframe[title="Select"]')).toBeHidden();

			await expect(
				page.getByRole('row').filter({hasText: 'Entry Test B'}).first()
			).toBeVisible();

			await openEntryRelationshipTab('Entry Test A');

			await expect(
				page.getByRole('row').filter({hasText: 'Entry Test B'}).first()
			).toBeVisible();
		}
	);

	test(
		'can view object entry title on relationship field',
		{tag: '@LPS-139803'},
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			let objectDefinition: ObjectDefinition;

			await test.step('create object definition with a one-to-many self-relationship and an entry', async () => {
				objectDefinition =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						status: {code: 0},
						titleObjectFieldName: 'textField',
					});

				apiHelpers.data.push({
					id: objectDefinition.id,
					type: 'objectDefinition',
				});

				const objectRelationshipAPIClient =
					await apiHelpers.buildRestClient(ObjectRelationshipAPI);

				const {body: objectRelationship} =
					await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
						objectDefinition.externalReferenceCode,
						{
							label: {en_US: 'Relationship'},
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
							objectDefinitionName2: objectDefinition.name,
							type: 'oneToMany',
						}
					);

				apiHelpers.data.push({
					id: objectRelationship.id,
					type: 'objectRelationship',
				});

				const restPath = `c/${objectDefinition.name.toLowerCase()}s`;

				await apiHelpers.objectEntry.postObjectEntry(
					{['textField']: 'Entry Test'},
					restPath
				);
			});

			await test.step('assert entry title is visible in the relationship field', async () => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				await page
					.getByRole('button', {name: 'Add ' + objectDefinition.name})
					.click();

				await clickAndExpectToBeVisible({
					target: page.getByRole('menuitem', {name: 'Entry Test'}),
					trigger: page.getByPlaceholder('Search'),
				});
			});
		}
	);

	test(
		'can view object entry title in the relationship tab for self relationship',
		{tag: '@LPS-139803'},
		async ({
			apiHelpers,
			objectLayoutsPage,
			page,
			viewObjectEntriesPage,
		}) => {
			let objectDefinition: ObjectDefinition;
			let objectRelationship: ObjectRelationship;
			let restPath: string;

			await test.step('create object definition with a one-to-many self-relationship and a layout with a relationship tab', async () => {
				objectDefinition =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						status: {code: 0},
						titleObjectFieldName: 'textField',
					});

				apiHelpers.data.push({
					id: objectDefinition.id,
					type: 'objectDefinition',
				});

				const objectRelationshipAPIClient =
					await apiHelpers.buildRestClient(ObjectRelationshipAPI);

				({body: objectRelationship} =
					await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
						objectDefinition.externalReferenceCode,
						{
							label: {en_US: 'Relationship'},
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
							objectDefinitionName2: objectDefinition.name,
							type: 'oneToMany',
						}
					));

				apiHelpers.data.push({
					id: objectRelationship.id,
					type: 'objectRelationship',
				});

				const layoutName = 'Layout' + getRandomInt();

				await objectLayoutsPage.goto(objectDefinition.label['en_US']);

				await objectLayoutsPage.createObjectLayout(layoutName);

				await objectLayoutsPage.createObjectLayoutContent({
					objectFieldNames: ['textField', 'Relationship'],
					objectLayoutName: layoutName,
					objectLayoutRegularBlockName: 'Block 1',
					objectLayoutTabName: 'Field Tab',
				});

				await objectLayoutsPage.openObjectLayoutConfiguration(
					layoutName
				);

				await objectLayoutsPage.setObjectLayoutAsDefault();

				await objectLayoutsPage.layoutTab.click();

				await objectLayoutsPage.addRelationshipTab(
					'Relationship Tab',
					'Relationship'
				);

				const {reload} =
					await objectLayoutsPage.saveObjectLayoutReturningReload();

				await reload;

				restPath = `c/${objectDefinition.name.toLowerCase()}s`;
			});

			await test.step('create entries A and B', async () => {
				await apiHelpers.objectEntry.postObjectEntry(
					{['textField']: 'Entry A'},
					restPath
				);

				await apiHelpers.objectEntry.postObjectEntry(
					{['textField']: 'Entry B'},
					restPath
				);
			});

			await test.step('relate Entry B to Entry A and assert it is visible in the relationship tab', async () => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				const entryARow = page.getByRole('row', {name: /Entry A/});

				await expect(entryARow).toBeVisible();

				await entryARow.getByRole('button', {name: 'Actions'}).click();

				await page.getByRole('menuitem', {name: 'View'}).click();

				await page.waitForLoadState('domcontentloaded');

				const relationshipTab = page.getByRole('link', {
					exact: true,
					name: 'Relationship Tab',
				});

				await expect(relationshipTab).toBeVisible();

				await relationshipTab.click();

				await page.getByRole('button', {name: 'New'}).first().click();

				await page
					.getByRole('menuitem', {name: 'Select Existing One'})
					.click();

				const selectFrame = page.frameLocator('iframe[title="Select"]');

				await expect(
					selectFrame.getByText('Entry B', {exact: true}).first()
				).toBeVisible();

				await selectFrame
					.getByText('Entry B', {exact: true})
					.first()
					.click();

				await expect(
					page.locator('iframe[title="Select"]')
				).toBeHidden();

				await expect(
					page.getByRole('row').filter({hasText: 'Entry B'}).first()
				).toBeVisible();

				await viewObjectEntriesPage.goto(objectDefinition.className);

				const entryARowRefreshed = page.getByRole('row', {
					name: /Entry A/,
				});

				await entryARowRefreshed
					.getByRole('button', {name: 'Actions'})
					.nth(0)
					.click();

				await page.getByRole('menuitem', {name: 'View'}).click();

				await page.waitForLoadState('domcontentloaded');

				await page
					.getByRole('link', {exact: true, name: 'Relationship Tab'})
					.click();

				await expect(
					page.getByRole('row').filter({hasText: 'Entry B'}).first()
				).toBeVisible();
			});
		}
	);

	test(
		'cannot relate an entry with itself',
		{tag: '@LPS-163658'},
		async ({
			addNewObjectRelationshipModalPage,
			apiHelpers,
			objectLayoutsPage,
			objectRelationshipsPage,
			page,
			viewObjectEntriesPage,
		}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const textFieldName = objectFields[0].name;

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
					titleObjectFieldName: textFieldName,
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

			await objectLayoutsPage.createObjectLayoutContent({
				objectFieldNames: [objectFields[0].label['en_US']],
				objectLayoutName: layoutName,
				objectLayoutRegularBlockName: 'Block 1',
				objectLayoutTabName: 'Field Tab',
			});

			await objectLayoutsPage.openObjectLayoutConfiguration(layoutName);
			await objectLayoutsPage.setObjectLayoutAsDefault();
			await objectLayoutsPage.layoutTab.click();

			await objectLayoutsPage.addRelationshipTab(
				'Relationship Tab',
				'Relationship'
			);

			const {reload} =
				await objectLayoutsPage.saveObjectLayoutReturningReload();

			const restPath = `c/${objectDefinition.name.toLowerCase()}s`;

			await apiHelpers.objectEntry.postObjectEntry(
				{[textFieldName]: 'Entry Test'},
				restPath
			);

			await reload;

			const openEntryRelationshipTab = async (entryLabel: string) => {
				await viewObjectEntriesPage.goto(objectDefinition.className);

				const entryRow = page.getByRole('row', {
					name: new RegExp(entryLabel),
				});

				await expect(entryRow).toBeVisible();

				await entryRow.getByRole('button', {name: 'Actions'}).click();
				await page.getByRole('menuitem', {name: 'View'}).click();

				await page.waitForLoadState('domcontentloaded');

				const relationshipTab = page.getByRole('link', {
					exact: true,
					name: 'Relationship Tab',
				});

				await expect(relationshipTab).toBeVisible();
				await relationshipTab.click();
			};

			await openEntryRelationshipTab('Entry Test');

			await page.getByLabel('Select Existing One').first().click();

			const selectFrame = page.frameLocator('iframe[title="Select"]');

			await expect(
				selectFrame.getByText('Entry Test', {exact: true})
			).toBeHidden();
		}
	);

	test(
		'cannot select Relationship field for Object Entry Title',
		{tag: '@LPS-139803'},
		async ({apiHelpers, editObjectDetailsPage, page}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const objectRelationshipName = await getFreshObjectRelationshipName(
				apiHelpers,
				[objectDefinition.externalReferenceCode!]
			);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition.externalReferenceCode,
					{
						label: {en_US: 'Relationship'},
						name: objectRelationshipName,
						objectDefinitionExternalReferenceCode1:
							objectDefinition.externalReferenceCode,
						objectDefinitionExternalReferenceCode2:
							objectDefinition.externalReferenceCode,
						objectDefinitionId1: objectDefinition.id,
						objectDefinitionId2: objectDefinition.id,
						objectDefinitionName2: objectDefinition.name,
						type: 'oneToMany',
					}
				);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await editObjectDetailsPage.goto(objectDefinition.label['en_US']);

			await editObjectDetailsPage.goToDetailsTab();

			await editObjectDetailsPage.entryTitleField.click();

			const titleFieldOptions = page.getByRole('listbox');

			await expect(
				titleFieldOptions
					.getByRole('option')
					.filter({hasText: /^Relationship\b/})
			).toHaveCount(0);
		}
	);

	test(
		'disassociate deletion allows child entry to be deleted in one-to-many',
		{tag: '@LPS-135401'},
		async ({
			apiHelpers,
			objectLayoutsPage,
			page,
			viewObjectEntriesPage,
		}) => {
			let objectDefinition1: ObjectDefinition;
			let objectDefinition2: ObjectDefinition;
			let objectRelationship: ObjectRelationship;

			await test.step('create objects A and B with a disassociate deletion one-to-many relationship and a layout with a relationship tab for Object A', async () => {
				objectDefinition1 =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						status: {code: 0},
						titleObjectFieldName: 'textField',
					});

				objectDefinition2 =
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

				({body: objectRelationship} =
					await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
						objectDefinition1.externalReferenceCode,
						{
							deletionType: 'disassociate',
							label: {en_US: 'Relationship'},
							name: await getFreshObjectRelationshipName(
								apiHelpers,
								[
									objectDefinition1.externalReferenceCode!,
									objectDefinition2.externalReferenceCode!,
								],
								'relationship'
							),
							objectDefinitionExternalReferenceCode1:
								objectDefinition1.externalReferenceCode,
							objectDefinitionExternalReferenceCode2:
								objectDefinition2.externalReferenceCode,
							objectDefinitionId1: objectDefinition1.id,
							objectDefinitionId2: objectDefinition2.id,
							objectDefinitionName2: objectDefinition2.name,
							type: 'oneToMany',
						}
					));

				apiHelpers.data.push({
					id: objectRelationship.id,
					type: 'objectRelationship',
				});

				const layoutName = 'Layout' + getRandomInt();

				await objectLayoutsPage.goto(objectDefinition1.label['en_US']);

				await objectLayoutsPage.createObjectLayout(layoutName);

				await objectLayoutsPage.createObjectLayoutContent({
					objectFieldNames: ['textField'],
					objectLayoutName: layoutName,
					objectLayoutRegularBlockName: 'Block 1',
					objectLayoutTabName: 'Field Tab',
				});

				await objectLayoutsPage.openObjectLayoutConfiguration(
					layoutName
				);

				await objectLayoutsPage.setObjectLayoutAsDefault();

				await objectLayoutsPage.layoutTab.click();

				await objectLayoutsPage.addRelationshipTab(
					'Relationship Tab',
					'Relationship'
				);

				const {reload} =
					await objectLayoutsPage.saveObjectLayoutReturningReload();

				await reload;
			});

			await test.step('create entries A and B and associate entry B to entry A', async () => {
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
						currentExternalReferenceCode:
							entryA.externalReferenceCode,
						objectRelationshipName: objectRelationship.name,
						relatedExternalReferenceCode:
							entryB.externalReferenceCode,
					}
				);
			});

			await test.step('delete Entry B and assert it is removed from both objects', async () => {
				await viewObjectEntriesPage.goto(objectDefinition2.className);

				const entryBRow = page.getByRole('row', {name: /Entry B/});

				await expect(entryBRow).toBeVisible();

				await entryBRow.getByRole('button', {name: 'Actions'}).click();

				await page.getByRole('menuitem', {name: 'Delete'}).click();

				await viewObjectEntriesPage.deletionConfirmationModal
					.getByRole('button', {name: 'Delete'})
					.click();

				await expect(
					page.getByRole('row', {name: /Entry B/})
				).not.toBeVisible();

				await viewObjectEntriesPage.goto(objectDefinition1.className);

				const entryARow = page.getByRole('row', {name: /Entry A/});

				await expect(entryARow).toBeVisible();

				await entryARow.getByRole('button', {name: 'Actions'}).click();

				await page.getByRole('menuitem', {name: 'View'}).click();

				await page.waitForLoadState('domcontentloaded');

				await page
					.getByRole('link', {exact: true, name: 'Relationship Tab'})
					.click();

				await expect(
					page.getByRole('row', {name: /Entry B/})
				).not.toBeVisible();
			});
		}
	);

	test(
		'relates many entries from both objects in a many-to-many relationship',
		{tag: '@LPS-135401'},
		async ({
			apiHelpers,
			objectLayoutsPage,
			page,
			viewObjectEntriesPage,
		}) => {
			let objectDefinition1: ObjectDefinition;
			let objectDefinition2: ObjectDefinition;

			await test.step('add two custom objects with a M:M relationship', async () => {
				objectDefinition1 =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						status: {code: 0},
						titleObjectFieldName: 'textField',
					});

				objectDefinition2 =
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
							label: {en_US: 'Relationship' + getRandomInt()},
							name: await getFreshObjectRelationshipName(
								apiHelpers,
								[
									objectDefinition1.externalReferenceCode!,
									objectDefinition2.externalReferenceCode!,
								],
								'relationship'
							),
							objectDefinitionExternalReferenceCode1:
								objectDefinition1.externalReferenceCode,
							objectDefinitionExternalReferenceCode2:
								objectDefinition2.externalReferenceCode,
							objectDefinitionId1: objectDefinition1.id,
							objectDefinitionId2: objectDefinition2.id,
							objectDefinitionName2: objectDefinition2.name,
							type: 'manyToMany',
						}
					);

				apiHelpers.data.push({
					id: objectRelationship.id,
					type: 'objectRelationship',
				});
			});

			await test.step('multiple entries are created for both objects', async () => {
				const applicationName1 = `c/${objectDefinition1.name.toLowerCase()}s`;
				const applicationName2 = `c/${objectDefinition2.name.toLowerCase()}s`;

				for (const letter of ['A', 'B', 'C']) {
					await apiHelpers.objectEntry.postObjectEntry(
						{['textField']: `Entry ${letter}`},
						applicationName1
					);
				}

				for (const letter of ['D', 'E', 'F']) {
					await apiHelpers.objectEntry.postObjectEntry(
						{['textField']: `Entry ${letter}`},
						applicationName2
					);
				}
			});

			await test.step('add layouts with a relationship tab for each definition', async () => {
				const setupLayout = async (
					objectDefinition: ObjectDefinition,
					fieldName: string
				) => {
					const layoutName = 'Layout' + getRandomInt();

					await objectLayoutsPage.goto(
						objectDefinition.label['en_US']
					);

					await objectLayoutsPage.createObjectLayout(layoutName);

					await objectLayoutsPage.createObjectLayoutContent({
						objectFieldNames: [fieldName],
						objectLayoutName: layoutName,
						objectLayoutRegularBlockName: 'Block 1',
						objectLayoutTabName: 'Field Tab',
					});

					await objectLayoutsPage.openObjectLayoutConfiguration(
						layoutName
					);

					await objectLayoutsPage.setObjectLayoutAsDefault();

					await objectLayoutsPage.layoutTab.click();

					await objectLayoutsPage.addRelationshipTab(
						'Relationship Tab',
						'Relationship'
					);

					const {reload} =
						await objectLayoutsPage.saveObjectLayoutReturningReload();

					await reload;
				};

				await setupLayout(objectDefinition1, 'textField');

				await setupLayout(objectDefinition2, 'textField');
			});

			await test.step('many entries from Object A can be related to many entries from Object B', async () => {
				const openRelationshipTab = async (
					className: string,
					entryLabel: string
				) => {
					await viewObjectEntriesPage.goto(className);

					const entryRow = page.getByRole('row', {
						name: new RegExp(entryLabel),
					});

					await expect(entryRow).toBeVisible();

					await entryRow
						.getByRole('button', {name: 'Actions'})
						.click();

					await page.getByRole('menuitem', {name: 'View'}).click();

					await page.waitForLoadState('domcontentloaded');

					const relationshipTab = page.getByRole('link', {
						exact: true,
						name: 'Relationship Tab',
					});

					await expect(relationshipTab).toBeVisible();

					await relationshipTab.click();
				};

				const selectRelationshipEntry = async (entryLabel: string) => {
					await page
						.getByLabel('Select Existing One')
						.first()
						.click();

					const selectFrame = page.frameLocator(
						'iframe[title="Select"]'
					);

					const entry = selectFrame
						.getByText(entryLabel, {exact: true})
						.first();

					await expect(entry).toBeVisible();

					await entry.click();

					await expect(
						page.locator('iframe[title="Select"]')
					).toBeHidden();

					await expect(
						page
							.getByRole('row')
							.filter({hasText: entryLabel})
							.first()
					).toBeVisible();
				};

				await openRelationshipTab(
					objectDefinition1.className,
					'Entry A'
				);

				await selectRelationshipEntry('Entry D');

				await openRelationshipTab(
					objectDefinition1.className,
					'Entry A'
				);

				await expect(
					page.getByRole('row').filter({hasText: 'Entry D'}).first()
				).toBeVisible();

				await selectRelationshipEntry('Entry E');

				await openRelationshipTab(
					objectDefinition1.className,
					'Entry A'
				);

				await expect(
					page.getByRole('row').filter({hasText: 'Entry D'}).first()
				).toBeVisible();

				await expect(
					page.getByRole('row').filter({hasText: 'Entry E'}).first()
				).toBeVisible();

				await openRelationshipTab(
					objectDefinition2.className,
					'Entry F'
				);

				await selectRelationshipEntry('Entry B');

				await openRelationshipTab(
					objectDefinition2.className,
					'Entry F'
				);

				await expect(
					page.getByRole('row').filter({hasText: 'Entry B'}).first()
				).toBeVisible();

				await selectRelationshipEntry('Entry C');

				await openRelationshipTab(
					objectDefinition2.className,
					'Entry F'
				);

				await expect(
					page.getByRole('row').filter({hasText: 'Entry B'}).first()
				).toBeVisible();

				await expect(
					page.getByRole('row').filter({hasText: 'Entry C'}).first()
				).toBeVisible();
			});
		}
	);

	test(
		'relationship field visibility changes according to active state on parent definition in one-to-many',
		{tag: '@LPS-139005'},
		async ({
			apiHelpers,
			page,
			viewObjectDefinitionsPage,
			viewObjectEntriesPage,
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

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						label: {en_US: 'Relationship' + getRandomInt()},
						name: await getFreshObjectRelationshipName(
							apiHelpers,
							[
								objectDefinition1.externalReferenceCode!,
								objectDefinition2.externalReferenceCode!,
							],
							'relationship'
						),
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
				{['textField']: 'Entry 1'},
				applicationName2
			);

			await test.step('inactivate Object A and assert relationship field is hidden for Object B entry form', async () => {
				await viewObjectDefinitionsPage.goto();

				await viewObjectDefinitionsPage.changeObjectActivateStatus(
					objectDefinition1.label['en_US']
				);

				await viewObjectEntriesPage.goto(objectDefinition2.className);

				await page
					.getByRole('button', {
						name: 'Add ' + objectDefinition2.name,
					})
					.click();

				await expect(
					page.getByLabel(objectRelationship.label.en_US, {
						exact: true,
					})
				).not.toBeVisible();
			});

			await test.step('reactivate Object A and assert relationship field is visible for Object B entry form', async () => {
				await viewObjectDefinitionsPage.goto();

				await viewObjectDefinitionsPage.changeObjectActivateStatus(
					objectDefinition1.label.en_US
				);

				await viewObjectEntriesPage.goto(objectDefinition2.className);

				await page
					.getByRole('button', {
						name: 'Add ' + objectDefinition2.name,
					})
					.click();

				await expect(
					page.getByLabel(objectRelationship.label.en_US, {
						exact: true,
					})
				).toBeVisible();
			});
		}
	);

	test(
		'relationship tab visibility changes according to active state in many-to-many',
		{tag: '@LPS-139005'},
		async ({
			apiHelpers,
			objectLayoutsPage,
			page,
			viewObjectDefinitionsPage,
			viewObjectEntriesPage,
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

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						label: {en_US: 'Relationship'},
						name: await getFreshObjectRelationshipName(
							apiHelpers,
							[
								objectDefinition1.externalReferenceCode!,
								objectDefinition2.externalReferenceCode!,
							],
							'relationship'
						),
						objectDefinitionExternalReferenceCode1:
							objectDefinition1.externalReferenceCode,
						objectDefinitionExternalReferenceCode2:
							objectDefinition2.externalReferenceCode,
						objectDefinitionId1: objectDefinition1.id,
						objectDefinitionId2: objectDefinition2.id,
						objectDefinitionName2: objectDefinition2.name,
						type: 'manyToMany',
					}
				);

			apiHelpers.data.push({
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			const setupLayout = async (
				objectDefinition: typeof objectDefinition1,
				fieldLabel: string
			) => {
				const layoutName = 'Layout' + getRandomInt();

				await objectLayoutsPage.goto(objectDefinition.label['en_US']);

				await objectLayoutsPage.createObjectLayout(layoutName);

				await objectLayoutsPage.createObjectLayoutContent({
					objectFieldNames: [fieldLabel],
					objectLayoutName: layoutName,
					objectLayoutRegularBlockName: 'Block 1',
					objectLayoutTabName: 'Field Tab',
				});

				await objectLayoutsPage.openObjectLayoutConfiguration(
					layoutName
				);

				await objectLayoutsPage.setObjectLayoutAsDefault();

				await objectLayoutsPage.layoutTab.click();

				await objectLayoutsPage.addRelationshipTab(
					'Relationship Tab',
					'Relationship'
				);

				const {reload} =
					await objectLayoutsPage.saveObjectLayoutReturningReload();

				await reload;
			};

			await setupLayout(objectDefinition1, 'textField');

			await setupLayout(objectDefinition2, 'textField');

			const applicationName1 = `c/${objectDefinition1.name.toLowerCase()}s`;
			const applicationName2 = `c/${objectDefinition2.name.toLowerCase()}s`;

			await apiHelpers.objectEntry.postObjectEntry(
				{['textField']: 'Entry A'},
				applicationName1
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{['textField']: 'Entry B'},
				applicationName2
			);

			const openEntryDetails = async (
				className: string,
				entryLabel: string
			) => {
				await viewObjectEntriesPage.goto(className);

				const entryRow = page.getByRole('row', {
					name: new RegExp(entryLabel),
				});

				await expect(entryRow).toBeVisible();

				await entryRow.getByRole('button', {name: 'Actions'}).click();

				await page.getByRole('menuitem', {name: 'View'}).click();

				await page.waitForLoadState('domcontentloaded');
			};

			await test.step('inactivate Object B and assert relationship tab is hidden on Entry A', async () => {
				await viewObjectDefinitionsPage.goto();

				await viewObjectDefinitionsPage.changeObjectActivateStatus(
					objectDefinition2.label['en_US']
				);

				await openEntryDetails(objectDefinition1.className, 'Entry A');

				await expect(
					page.getByRole('link', {
						exact: true,
						name: 'Relationship Tab',
					})
				).not.toBeVisible();
			});

			await test.step('reactivate Object B and assert relationship tab is visible on Entry A', async () => {
				await viewObjectDefinitionsPage.goto();

				await viewObjectDefinitionsPage.changeObjectActivateStatus(
					objectDefinition2.label['en_US']
				);

				await openEntryDetails(objectDefinition1.className, 'Entry A');

				await expect(
					page.getByRole('link', {
						exact: true,
						name: 'Relationship Tab',
					})
				).toBeVisible();
			});
		}
	);

	test(
		'relationship tab visibility changes according to active state in one-to-many',
		{tag: '@LPS-139005'},
		async ({
			apiHelpers,
			objectLayoutsPage,
			page,
			viewObjectDefinitionsPage,
			viewObjectEntriesPage,
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

			const objectRelationshipAPIClient =
				await apiHelpers.buildRestClient(ObjectRelationshipAPI);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition1.externalReferenceCode,
					{
						label: {en_US: 'Relationship'},
						name: await getFreshObjectRelationshipName(
							apiHelpers,
							[
								objectDefinition1.externalReferenceCode!,
								objectDefinition2.externalReferenceCode!,
							],
							'relationship'
						),
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

			const layoutName = 'Layout' + getRandomInt();

			await objectLayoutsPage.goto(objectDefinition1.label['en_US']);

			await objectLayoutsPage.createObjectLayout(layoutName);

			await objectLayoutsPage.createObjectLayoutContent({
				objectFieldNames: ['textField'],
				objectLayoutName: layoutName,
				objectLayoutRegularBlockName: 'Block 1',
				objectLayoutTabName: 'Field Tab',
			});

			await objectLayoutsPage.openObjectLayoutConfiguration(layoutName);

			await objectLayoutsPage.setObjectLayoutAsDefault();

			await objectLayoutsPage.layoutTab.click();

			await objectLayoutsPage.addRelationshipTab(
				'Relationship Tab',
				'Relationship'
			);

			const {reload} =
				await objectLayoutsPage.saveObjectLayoutReturningReload();

			const applicationName1 = `c/${objectDefinition1.name.toLowerCase()}s`;

			await apiHelpers.objectEntry.postObjectEntry(
				{['textField']: 'Entry 1'},
				applicationName1
			);

			await reload;

			const openEntry1Details = async () => {
				await viewObjectEntriesPage.goto(objectDefinition1.className);

				const entryRow = page.getByRole('row', {name: /Entry 1/});

				await expect(entryRow).toBeVisible();

				await entryRow.getByRole('button', {name: 'Actions'}).click();

				await page.getByRole('menuitem', {name: 'View'}).click();

				await page.waitForLoadState('domcontentloaded');
			};

			await test.step('inactivate Object B and assert relationship tab is hidden on Entry 1', async () => {
				await viewObjectDefinitionsPage.goto();

				await viewObjectDefinitionsPage.changeObjectActivateStatus(
					objectDefinition2.label['en_US']
				);

				await openEntry1Details();

				await expect(
					page.getByRole('link', {
						exact: true,
						name: 'Relationship Tab',
					})
				).not.toBeVisible();
			});

			await test.step('reactivate Object B and assert relationship tab is visible on Entry 1', async () => {
				await viewObjectDefinitionsPage.goto();

				await viewObjectDefinitionsPage.changeObjectActivateStatus(
					objectDefinition2.label['en_US']
				);

				await openEntry1Details();

				await expect(
					page.getByRole('link', {
						exact: true,
						name: 'Relationship Tab',
					})
				).toBeVisible();
			});
		}
	);
});

test.describe('View relationship hierarchy labels', () => {
	test(
		'can switch relationship order between parent and child',
		{tag: '@LPS-193697'},
		async ({
			addNewObjectRelationshipModalPage,
			apiHelpers,
			objectRelationshipsPage,
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

			apiHelpers.data.push({
				id: objectDefinition1.id,
				type: 'objectDefinition',
			});

			apiHelpers.data.push({
				id: objectDefinition2.id,
				type: 'objectDefinition',
			});

			await objectRelationshipsPage.goto(
				objectDefinition1.label['en_US']
			);

			await objectRelationshipsPage.addObjectRelationshipButton.click();

			await expect(
				addNewObjectRelationshipModalPage.modalHeader
			).toBeVisible();

			await addNewObjectRelationshipModalPage.objectRelationshipFormPage.labelInput.fill(
				'Relationship'
			);

			await addNewObjectRelationshipModalPage.objectRelationshipFormPage.selectType(
				'One to Many'
			);

			await addNewObjectRelationshipModalPage.objectRelationshipFormPage.selectManyRecordsOf(
				objectDefinition2.label['en_US']
			);

			await addNewObjectRelationshipModalPage.objectRelationshipFormPage.reverseOrderButton.click();

			const modal = page.getByRole('dialog');

			await expect(modal.getByLabel('One Record Of')).toHaveValue(
				objectDefinition2.label['en_US']
			);

			await expect(modal.getByLabel('Many Records Of')).toHaveValue(
				objectDefinition1.label['en_US']
			);

			const objectRelationship =
				await addNewObjectRelationshipModalPage.objectRelationshipFormPage.saveButton
					.click()
					.then(async () => {
						const response = await page.waitForResponse(
							'**/object-relationships'
						);

						return response.json();
					});

			if (objectRelationship?.id) {
				apiHelpers.data.push({
					id: objectRelationship.id,
					type: 'objectRelationship',
				});
			}
		}
	);

	test(
		'shows Parent and Child labels and disables fields on parent form for self relationships',
		{tag: ['@LPS-163654', '@LPS-163655']},
		async ({apiHelpers, objectRelationshipsPage, page}) => {
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

			const objectRelationshipName = await getFreshObjectRelationshipName(
				apiHelpers,
				[objectDefinition.externalReferenceCode!]
			);

			const {body: objectRelationship} =
				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					objectDefinition.externalReferenceCode,
					{
						label: {en_US: 'Relationship'},
						name: objectRelationshipName,
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
				id: objectRelationship.id,
				type: 'objectRelationship',
			});

			await objectRelationshipsPage.goto(objectDefinition.label['en_US']);

			await expect(page.getByText('Parent')).toBeVisible();
			await expect(page.getByText('Child')).toBeVisible();

			const iframe = page.frameLocator('iframe');

			await page
				.getByRole('row')
				.filter({hasText: 'Parent'})
				.getByRole('link', {exact: true, name: 'Relationship'})
				.click();

			await expect(iframe.getByText('Parent')).toBeVisible();

			await expect(
				iframe.getByLabel('NameMandatory', {exact: true})
			).toBeDisabled();
			await expect(
				iframe.getByLabel('TypeMandatory', {exact: true})
			).toBeDisabled();
			await expect(
				iframe.getByLabel('Many Records OfMandatory', {exact: true})
			).toBeDisabled();

			await objectRelationshipsPage.cancelButton.click();

			await page
				.getByRole('row')
				.filter({hasText: 'Child'})
				.getByRole('link', {exact: true, name: 'Relationship'})
				.click();

			await expect(iframe.getByText('Child')).toBeVisible();
		}
	);
});
