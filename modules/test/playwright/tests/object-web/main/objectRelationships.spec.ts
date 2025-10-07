/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
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
import {getRandomInt} from '../../../utils/getRandomInt';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	dataApiHelpersTest,
	loginTest(),
	objectPagesTest
);

test.beforeEach(({page}) => {
	page.setViewportSize({height: 1080, width: 1920});
});

test.describe('Manage object relationships through Model Builder', () => {
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
			name: 'objectRelationshipName' + Math.floor(Math.random() * 99),
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

		await expect(objectRelationshipFormPage.manyRecordsOfInput).toHaveText(
			objectDefinition2.label['en_US']
		);
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

		await page.waitForTimeout(500);

		await modelBuilderDiagramPage.clickObjectRelationshipEdge('2');

		expect(
			page.getByRole('menuitem', {name: objectRelationship1Label})
		).toBeVisible();

		expect(
			page.getByRole('menuitem', {name: objectRelationship2Label})
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
		const objectRelationshipName =
			'objectRelationshipName' + Math.floor(Math.random() * 99);

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
			const objectRelationshipName =
				'objectRelationshipName' + Math.floor(Math.random() * 99);
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
		const objectRelationshipName =
			'objectRelationshipName' + Math.floor(Math.random() * 99);

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
		const objectRelationshipName =
			'objectRelationshipName' + Math.floor(Math.random() * 99);

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
				const pagePromise = page.waitForEvent('popup');

				await page.getByRole('link', {name: 'Learn more.'}).click();

				const liferayLearnPage = await pagePromise;

				await liferayLearnPage.waitForLoadState();

				await expect(
					liferayLearnPage.getByRole('heading', {
						name: 'Accessing Accounts Data from Custom Object',
					})
				).toBeVisible();
			});
		}
	);

	test(
		'allows to create relationship with a custom object named Address',
		{tag: '@LPD-51156'},
		async ({
			addNewObjectRelationshipModalPage,
			apiHelpers,
			modelBuilderDiagramPage,
			page,
			viewObjectDefinitionsPage,
		}) => {
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

			const {body: commerceOrderDefinition} =
				await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
					'L_COMMERCE_ORDER'
				);

			await test.step('navigate to model builder and display all nodes', async () => {
				await viewObjectDefinitionsPage.goto();

				await viewObjectDefinitionsPage.openObjectFolder('Default');

				await viewObjectDefinitionsPage.viewInModelBuilderButton.click();

				await modelBuilderDiagramPage.toggleSidebarsButton.click();

				await modelBuilderDiagramPage.fitViewButton.click();
			});

			await test.step('assert that creating a relationship with a custom object named Address as the source node is allowed', async () => {
				await modelBuilderDiagramPage.connectObjectDefinitionsNodeHandles(
					customPostalAddressDefinition.id,
					commerceOrderDefinition.id
				);

				expect(
					addNewObjectRelationshipModalPage.modalHeader
				).toBeVisible();
			});

			await test.step('assert that creating a relationship with a custom object named Address as the target node is allowed', async () => {
				await page.getByRole('button', {name: 'Cancel'}).click();

				await modelBuilderDiagramPage.connectObjectDefinitionsNodeHandles(
					commerceOrderDefinition.id,
					customPostalAddressDefinition.id
				);

				expect(
					addNewObjectRelationshipModalPage.modalHeader
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
		const objectRelationshipName =
			'objectRelationshipName' + Math.floor(Math.random() * 99);

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
});

test.describe('Manage object relationships through Objects Admin UI', () => {
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
			name: 'objectRelationshipName' + Math.floor(Math.random() * 99),
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

		await expect(
			page.getByText(objectRelationship2.label['en_US'])
		).toBeVisible();

		await page
			.getByRole('link', {
				exact: true,
				name: objectRelationship2.label['en_US'],
			})
			.click();

		await expect(
			page.frameLocator('iframe').getByLabel('ParameterMandatory')
		).toHaveText(objectRelationship1.label['en_US']);
	});
});
