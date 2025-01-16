/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinitionApi,
	ObjectFolderApi,
	ObjectRelationship,
	ObjectRelationshipApi,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {collectionsPagesTest} from '../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../fixtures/fragmentPagesTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import {loginTest} from '../../fixtures/loginTest';
import {objectPagesTest} from '../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../fixtures/pageEditorPagesTest';
import {getRandomInt} from '../../utils/getRandomInt';
import getRandomString from '../../utils/getRandomString';
import getFragmentDefinition from '../layout-content-page-editor-web/utils/getFragmentDefinition';
import getPageDefinition from '../layout-content-page-editor-web/utils/getPageDefinition';
import {createObjectFields} from './utils/mockObjectFields';

export const test = mergeTests(
	collectionsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	fragmentsPagesTest,
	isolatedSiteTest,
	loginTest(),
	objectPagesTest,
	pageEditorPagesTest
);

test.describe('Manage object definitions through Model Builder', () => {
	test.beforeEach(({page}) => {
		page.setViewportSize({height: 1080, width: 1920});
	});

	test('assert presence of selected node style on click and its transition after dragging an unselected one', async ({
		apiHelpers,
		modelBuilderDiagramPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'default',
				status: {code: 0},
			});

		const commerceOrderItemLabel = 'Commerce Order Item';

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		await modelBuilderDiagramPage.toggleSidebarsButton.click();

		await modelBuilderDiagramPage.fitViewButton.click();

		await modelBuilderDiagramPage.objectDefinitionNodes
			.filter({hasText: commerceOrderItemLabel})
			.click();

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes.filter({
				hasText: commerceOrderItemLabel,
			})
		).toHaveClass(/selected/);

		await modelBuilderDiagramPage.dragNodeThroughDiagram(
			objectDefinition.label['en_US'],
			1400,
			940
		);

		await modelBuilderDiagramPage.fitViewButton.click();

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes.filter({
				hasText: commerceOrderItemLabel,
			})
		).not.toHaveClass(/selected/);

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes.filter({
				hasText: objectDefinition.label['en_US'],
			})
		).toHaveClass(/selected/);
	});

	test('can create an object definition by model builder', async ({
		apiHelpers,
		modalAddObjectDefinitionPage,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderRightSidebarPage,
	}) => {
		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		for (let i = 0; i <= 3; i++) {
			const objectDefinitionLabel =
				'ObjectDefinitionLabel' + getRandomInt();

			modelBuilderLeftSidebarPage.createNewObjectDefinitionButton.click();

			const objectDefinition =
				await modalAddObjectDefinitionPage.createObjectDefinition(
					objectDefinitionLabel
				);

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const rightSidebar =
				modelBuilderRightSidebarPage.getRightSidebarLocator(
					modelBuilderLeftSidebarPage.createNewObjectDefinitionButton
				);

			await expect(
				rightSidebar.getByTitle(objectDefinitionLabel + ' Details')
			).toBeVisible();

			await modelBuilderLeftSidebarPage.sidebarItems
				.filter({hasText: objectDefinition.label['en_US']})
				.click();

			await expect(
				modelBuilderDiagramPage.objectDefinitionNodes.filter({
					hasText: objectDefinition.label['en_US'],
				})
			).toBeVisible();
		}
	});

	test('can create an object definition inside a folder and see if it renders correctly in the model builder', async ({
		apiHelpers,
		modalAddObjectDefinitionPage,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		page,
		viewObjectDefinitionsPage,
	}) => {
		await viewObjectDefinitionsPage.goto();

		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();

		viewObjectDefinitionsPage.createObjectDefinitionButton.click();

		const objectDefinition =
			await modalAddObjectDefinitionPage.createObjectDefinition(
				objectDefinitionLabel
			);

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		expect(page.getByText(objectDefinitionLabel)).toBeVisible();

		await viewObjectDefinitionsPage.viewInModelBuilderButton.click();

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes.filter({
				hasText: objectDefinition.label['en_US'],
			})
		).toBeVisible();

		await expect(
			modelBuilderLeftSidebarPage.sidebarItems.filter({
				hasText: objectDefinition.label['en_US'],
			})
		).toBeVisible();
	});

	test('can delete an object definition by model builder leftsidebar', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
	}) => {
		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'default',
				status: {code: 2},
			});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'default',
				status: {code: 2},
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

		await modelBuilderLeftSidebarPage.clickSideBarItem(
			objectDefinition1.label['en_US']
		);

		await modelBuilderLeftSidebarPage.clickObjectDefinitionActionsButtonInSidebar(
			objectDefinition1.label['en_US']
		);

		await modelBuilderObjectDefinitionNodePage.deleteObjectDefinitionOption.click();

		apiHelpers.data.splice(
			apiHelpers.data.findIndex(
				(object) =>
					object.id === objectDefinition1.id &&
					object.type === 'objectDefinition'
			),
			1
		);

		await expect(
			modelBuilderLeftSidebarPage.sidebarItems.filter({
				hasText: objectDefinition2.label['en_US'],
			})
		).toBeVisible();

		await expect(
			modelBuilderLeftSidebarPage.sidebarItems.filter({
				hasText: objectDefinition1.label['en_US'],
			})
		).toBeHidden();
	});

	test('can delete an published object definition by model builder', async ({
		apiHelpers,
		modalAddObjectDefinitionPage,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
	}) => {
		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'default',
				status: {code: 0},
			});

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		await modelBuilderLeftSidebarPage.createNewObjectDefinitionButton.click();

		const objectDefinition2 =
			await modalAddObjectDefinitionPage.createObjectDefinition(
				'ObjectDefinition' + getRandomInt()
			);

		apiHelpers.data.push({
			id: objectDefinition1.id,
			type: 'objectDefinition',
		});
		apiHelpers.data.push({
			id: objectDefinition2.id,
			type: 'objectDefinition',
		});

		await modelBuilderDiagramPage.toggleSidebarsButton.click();

		await modelBuilderDiagramPage.fitViewButton.click();

		await modelBuilderObjectDefinitionNodePage.clickObjectDefinitionActionsButton(
			objectDefinition1.label['en_US'],
			modelBuilderDiagramPage.objectDefinitionNodes
		);

		await modelBuilderObjectDefinitionNodePage.deleteObjectDefinition(
			objectDefinition1.name
		);

		apiHelpers.data.splice(
			apiHelpers.data.findIndex(
				(object) =>
					object.id === objectDefinition1.id &&
					object.type === 'objectDefinition'
			),
			1
		);

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes.filter({
				hasText: objectDefinition2.label['en_US'],
			})
		).toBeVisible();

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes.filter({
				hasText: objectDefinition1.label['en_US'],
			})
		).toBeHidden();
	});

	test('linked object definitions are created when object definitions are related and put into different folders', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderObjectDefinitionNodePage,
	}) => {
		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				status: {code: 0},
			});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'default',
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

		const objectRelationshipApiClient = await apiHelpers.buildRestClient(
			ObjectRelationshipApi
		);

		await objectRelationshipApiClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
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
				type: ObjectRelationship.TypeEnum.OneToMany,
			}
		);

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		await expect(
			modelBuilderObjectDefinitionNodePage.getLinkedObjectDefinitionIconLocator(
				objectDefinition1.label['en_US'],
				modelBuilderDiagramPage.objectDefinitionNodes
			)
		).toBeVisible();

		await expect(
			modelBuilderObjectDefinitionNodePage.getLinkedObjectDefinitionIconLocator(
				objectDefinition2.label['en_US'],
				modelBuilderDiagramPage.objectDefinitionNodes
			)
		).toBeHidden();

		await modelBuilderLeftSidebarPage.sidebarItems
			.filter({hasText: objectFolder.name})
			.hover();

		await modelBuilderLeftSidebarPage.goToFolderButton.click();

		await expect(
			modelBuilderObjectDefinitionNodePage.getLinkedObjectDefinitionIconLocator(
				objectDefinition1.label['en_US'],
				modelBuilderDiagramPage.objectDefinitionNodes
			)
		).toBeHidden();

		await expect(
			modelBuilderObjectDefinitionNodePage.getLinkedObjectDefinitionIconLocator(
				objectDefinition2.label['en_US'],
				modelBuilderDiagramPage.objectDefinitionNodes
			)
		).toBeVisible();

		// Clean up

		const objectFolderApiClient =
			await apiHelpers.buildRestClient(ObjectFolderApi);

		await objectFolderApiClient.deleteObjectFolder(objectFolder.id);
	});

	test('navigate to edit object definition page', async ({
		context,
		modelBuilderDiagramPage,
		modelBuilderObjectDefinitionNodePage,
	}) => {
		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		await modelBuilderDiagramPage.toggleSidebarsButton.click();

		await modelBuilderObjectDefinitionNodePage.clickObjectDefinitionActionsButton(
			'organization',
			modelBuilderDiagramPage.objectDefinitionNodes
		);

		await modelBuilderDiagramPage.editInPageViewOption.click();

		const pagePromise = context.waitForEvent('page');

		await modelBuilderDiagramPage.openPageViewButton.click();

		const editObjectDefinitionPage = await pagePromise;

		await expect(
			editObjectDefinitionPage.getByText('ERC:L_ORGANIZATION')
		).toBeVisible();
	});

	test('see object definition details', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderRightSidebarPage,
		page,
	}) => {
		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionApi);

		const {body: department} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				label: {
					en_US: 'Department',
					pt_BR: 'Departamento',
				},
				name: 'Department',
				objectFields: createObjectFields('text', [{
						label: 'Name',
						name: 'name',
					}]),
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				panelCategoryKey: 'control_panel.object',
				pluralLabel: {
					en_US: 'Departments',
					pt_BR: 'Departamentos',
				},
				scope: 'company',
				status: {code: 0},
				titleObjectFieldName: 'id',
			});

		apiHelpers.data.push({id: department.id, type: 'objectDefinition'});

		const {body: employee} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: false,
				label: {
					en_US: 'Employee',
					pt_BR: 'Funcionario',
				},
				name: 'Employee',
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				panelCategoryKey: 'site_administration.design',
				pluralLabel: {
					en_US: 'Employees',
					pt_BR: 'Funcionarios',
				},
				scope: 'site',
				status: {code: 1},
				titleObjectFieldName: 'name',
			});

		apiHelpers.data.push({id: employee.id, type: 'objectDefinition'});

		await modelBuilderDiagramPage.goto({
			objectFolderName: objectFolder.name,
		});

		for (const objectDefinition of [department, employee]) {
			await modelBuilderLeftSidebarPage.sidebarItems
				.filter({hasText: objectDefinition.label['en_US']})
				.click();

			const rightSidebar =
				modelBuilderRightSidebarPage.getRightSidebarLocator(
					modelBuilderLeftSidebarPage.createNewObjectDefinitionButton
				);

			await expect(
				rightSidebar.getByTitle(
					`${objectDefinition.label['en_US']} Details`
				)
			).toBeVisible();

			// Object Data Container

			await expect(
				modelBuilderRightSidebarPage.sidebarLabelInput
			).toHaveValue(objectDefinition.label['en_US']);

			await modelBuilderRightSidebarPage.objectDefinitionLabelLocalizationButton.click();

			await page
				.getByRole('menuitem', {name: 'pt_BR Translated'})
				.click();

			await expect(
				modelBuilderRightSidebarPage.sidebarLabelInput
			).toHaveValue(objectDefinition.label['pt_BR']);

			await page.keyboard.press('Escape');

			await expect(
				modelBuilderRightSidebarPage.objectDefinitionPluralLabel
			).toHaveValue(objectDefinition.pluralLabel['pt_BR']);

			await modelBuilderRightSidebarPage.objectDefinitionPluralLabelLocalizationButton.click();

			await page.getByRole('menuitem', {name: 'en_US Default'}).click();

			await expect(
				modelBuilderRightSidebarPage.objectDefinitionPluralLabel
			).toHaveValue(objectDefinition.pluralLabel['en_US']);

			await page.keyboard.press('Escape');

			await expect(
				modelBuilderRightSidebarPage.objectDefinitionActivateObject
			).toBeChecked({checked: objectDefinition.active});

			// Entry Display Container

			await expect(
				modelBuilderRightSidebarPage.objectDefinitionEntryTitleField
			).toHaveText(objectDefinition.titleObjectFieldName, {
				ignoreCase: true,
			});

			// Scope Container

			await expect(
				modelBuilderRightSidebarPage.objectDefinitionScope
			).toHaveText(objectDefinition.scope, {ignoreCase: true});

			const [_, panelLink] = objectDefinition.panelCategoryKey.split('.');

			await expect(
				modelBuilderRightSidebarPage.objectDefinitionPanelLink
			).toHaveText(panelLink, {ignoreCase: true});
		}
	});
});

test.describe('Manage object definitions through View Object Definitions', () => {
	test('can delete an object definition by FDS action', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'default',
				status: {code: 2},
			});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'default',
				status: {code: 2},
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

		await page.locator('.cell-item-actions').first().waitFor();

		await page
			.locator('.cell-item-actions')
			.last()
			.locator('.dropdown-toggle')
			.click();

		await viewObjectDefinitionsPage.deleteObjectDefinitionOption.click();

		apiHelpers.data.splice(
			apiHelpers.data.findIndex(
				(object) =>
					object.id === objectDefinition2.id &&
					object.type === 'objectDefinition'
			),
			1
		);

		await expect(
			viewObjectDefinitionsPage.frontendDataSetEntries.filter({
				hasText: objectDefinition1.label['en_US'],
			})
		).toBeVisible();

		await expect(
			viewObjectDefinitionsPage.frontendDataSetEntries.filter({
				hasText: objectDefinition2.label['en_US'],
			})
		).toBeHidden();
	});
});

test.describe('Manage object definitions through a Page', () => {
	test('can display an object reactivated on the Collection Providers', async ({
		apiHelpers,
		collectionsPage,
		page,
		site,
		viewObjectDefinitionsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'default',
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

	test('can display an object reactivated on the Page Item Selector', async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
		viewObjectDefinitionsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'default',
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
});
