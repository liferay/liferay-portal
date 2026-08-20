/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionAPI,
	ObjectFolderAPI,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {collectionsPagesTest} from '../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	performUserSwitch,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import getFormContainerDefinition from '../../layout-content-page-editor-web/main/utils/getFormContainerDefinition';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {localizationPagesTest} from '../../site-admin-web/main/fixtures/localizationPagesTest';
import {generateObjectFields} from '../utils/generateObjectFields';
import {getFreshObjectRelationshipName} from '../utils/getFreshObjectRelationshipName';

const test = mergeTests(
	collectionsPagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	fragmentsPagesTest,
	globalMenuPagesTest,
	isolatedSiteTest,
	localizationPagesTest,
	loginTest(),
	objectPagesTest,
	pageEditorPagesTest,
	productMenuPageTest
);

const cmsTest = mergeTests(
	test,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	})
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

			await modelBuilderLeftSidebarPage.createNewObjectDefinitionButton.click();

			const objectDefinitionResponsePromise =
				modelBuilderDiagramPage.page.waitForResponse((response) =>
					response
						.url()
						.includes(
							'/object-definitions/by-external-reference-code/'
						)
				);

			const objectDefinition =
				await modalAddObjectDefinitionPage.createObjectDefinition(
					objectDefinitionLabel
				);

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await objectDefinitionResponsePromise;

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

		await waitForAlert(
			page,
			`Success:${objectDefinitionLabel} was created successfully.`
		);

		await expect(
			page.getByRole('link', {name: objectDefinitionLabel})
		).toBeVisible();

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
				status: {code: 2},
			});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
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

		await modelBuilderObjectDefinitionNodePage.deleteDraftObjectDefinition();

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

	test(
		'can publish multiple object definitions',
		{tag: '@LPD-16778'},
		async ({apiHelpers, modelBuilderDiagramPage, page}) => {
			await test.step('Create five draft object definitions with fields', async () => {
				for (let i = 0; i < 5; i++) {
					const objectDefinition =
						await apiHelpers.objectAdmin.postRandomObjectDefinition(
							{
								objectFields: generateObjectFields({
									objectFieldBusinessTypes: ['Text'],
								}),
								status: {code: 2},
							}
						);

					apiHelpers.data.push({
						id: objectDefinition.id,
						type: 'objectDefinition',
					});
				}
			});

			await test.step('Go to model builder and click to publish all definitions at once', async () => {
				await modelBuilderDiagramPage.goto({
					objectFolderName: 'Default',
				});

				await page.getByRole('button', {name: 'Publish'}).click();

				await page.getByRole('button', {name: 'Select All'}).click();

				await page
					.getByRole('button', {name: 'Publish Objects'})
					.click();
			});

			await test.step('Verify that definitions were published', async () => {
				await expect(
					page.getByRole('heading', {name: 'Successfully published.'})
				).toBeVisible();

				await expect(page.locator('.lexicon-icon-check')).toHaveCount(
					5
				);
			});
		}
	);

	test(
		'can search for object definition in model builder view',
		{tag: '@LPD-16778'},
		async ({
			apiHelpers,
			modelBuilderDiagramPage,
			modelBuilderLeftSidebarPage,
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

			await test.step('Verify both objects appear in the sidebar before searching', async () => {
				await modelBuilderDiagramPage.goto({
					objectFolderName: 'Default',
				});

				await expect(
					modelBuilderLeftSidebarPage.sidebarItems.filter({
						hasText: objectDefinition1.label['en_US'],
					})
				).toBeVisible();

				await expect(
					modelBuilderLeftSidebarPage.sidebarItems.filter({
						hasText: objectDefinition2.label['en_US'],
					})
				).toBeVisible();
			});

			const searchInput = page.getByRole('textbox', {name: 'Search'});

			await test.step('Search for the first object definition and verify the other is hidden', async () => {
				await searchInput.fill(objectDefinition1.label['en_US']);

				await expect(
					modelBuilderLeftSidebarPage.sidebarItems.filter({
						hasText: objectDefinition1.label['en_US'],
					})
				).toBeVisible();

				await expect(
					modelBuilderLeftSidebarPage.sidebarItems.filter({
						hasText: objectDefinition2.label['en_US'],
					})
				).toBeHidden();
			});

			await test.step('Clear search and verify both definitions appear again', async () => {
				await searchInput.clear();

				await expect(
					modelBuilderLeftSidebarPage.sidebarItems.filter({
						hasText: objectDefinition1.label['en_US'],
					})
				).toBeVisible();

				await expect(
					modelBuilderLeftSidebarPage.sidebarItems.filter({
						hasText: objectDefinition2.label['en_US'],
					})
				).toBeVisible();
			});
		}
	);

	test(
		'cannot publish an invalid definition',
		{tag: '@LPD-16778'},
		async ({apiHelpers, modelBuilderDiagramPage, page}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const objectDefinitionWithField =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 2},
				});

			apiHelpers.data.push({
				id: objectDefinitionWithField.id,
				type: 'objectDefinition',
			});

			const objectDefinitionWithoutField =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: [],
					status: {code: 2},
				});

			apiHelpers.data.push({
				id: objectDefinitionWithoutField.id,
				type: 'objectDefinition',
			});

			await test.step('Attempt to publish all definitions', async () => {
				await modelBuilderDiagramPage.goto({
					objectFolderName: 'Default',
				});

				await page.getByRole('button', {name: 'Publish'}).click();

				await page.getByRole('button', {name: 'Select All'}).click();

				await page
					.getByRole('button', {name: 'Publish Objects'})
					.click();
			});

			await test.step('Only the valid defintion is published and an error message is displayed', async () => {
				await expect(
					page.getByRole('heading', {name: 'Published with Errors'})
				).toBeVisible();

				await expect(
					page.getByText('At least one object field must be added.')
				).toBeVisible();

				await expect(page.locator('.lexicon-icon-check')).toHaveCount(
					1
				);
			});
		}
	);

	test('hidden system object definitions are not displayed', async ({
		modelBuilderDiagramPage,
		page,
	}) => {
		const hiddenObjectDefinitionNames = [
			'FunctionalCookieEntry',
			'NecessaryCookieEntry',
			'PerformanceCookieEntry',
			'PersonalizationCookieEntry',
		];

		const responsePromise = page.waitForResponse((response) =>
			response
				.url()
				.includes(
					'/object-definitions?pageSize=-1&filter=hidden%20eq%20false'
				)
		);

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		const response = await responsePromise;

		const body = await response.json();

		const objectDefinitionNames = body.items.map(
			(item: ObjectDefinition) => item.name
		);

		expect(
			objectDefinitionNames.every(
				(name: string) => !hiddenObjectDefinitionNames.includes(name)
			)
		).toBeTruthy();
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

		const objectFolderAPIClient =
			await apiHelpers.buildRestClient(ObjectFolderAPI);

		await objectFolderAPIClient.deleteObjectFolder(objectFolder.id);
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

	test('object definition remains in its folder after external reference code edit and new object definition creation', async ({
		apiHelpers,
		modalAddObjectDefinitionPage,
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
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition1.id,
			type: 'objectDefinition',
		});

		await modelBuilderDiagramPage.goto({
			objectFolderName: objectFolder.name,
		});

		await modelBuilderObjectDefinitionNodePage.clickObjectDefinitionActionsButton(
			objectDefinition1.label['en-us'],
			modelBuilderDiagramPage.objectDefinitionNodes
		);

		await modelBuilderObjectDefinitionNodePage.editObjectDefinitionExternalReferenceCodeButton.click();

		const objectDefinitionExternalReferenceCode =
			'ObjectDefinition' + getRandomInt();

		await modelBuilderObjectDefinitionNodePage.modalEditObjectDefinitionExternalReferenceCodeInput.fill(
			objectDefinitionExternalReferenceCode
		);

		await page.getByRole('button', {name: 'save'}).click();

		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();

		await modelBuilderLeftSidebarPage.createNewObjectDefinitionButton.click();

		const objectDefinition2 =
			await modalAddObjectDefinitionPage.createObjectDefinition(
				objectDefinitionLabel
			);

		apiHelpers.data.push({
			id: objectDefinition2.id,
			type: 'objectDefinition',
		});

		await waitForAlert(
			page,
			`Success:${objectDefinitionLabel} was created successfully.`
		);

		await page.reload();

		await expect(
			modelBuilderDiagramPage.objectDefinitionNodes.filter({
				hasText: objectDefinition1.label['en_US'],
			})
		).toBeVisible();
	});

	test('see object definition details', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		modelBuilderRightSidebarPage,
		page,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectFieldName = objectFields[0].name;

		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: department} =
			await objectDefinitionAPIClient.postObjectDefinition({
				active: true,
				enableFriendlyURLCustomization: true,
				label: {
					en_US: 'Department',
					pt_BR: 'Departamento',
				},
				name: 'Department',
				objectFields,
				objectFolderExternalReferenceCode:
					objectFolder.externalReferenceCode,
				panelCategoryKey: 'control_panel.object',
				pluralLabel: {
					en_US: 'Departments',
					pt_BR: 'Departamentos',
				},
				scope: 'company',
				status: {code: 0},
				titleObjectFieldName: objectFieldName,
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
				titleObjectFieldName: 'id',
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
				.getByRole('option', {name: 'pt_BR language: Translated'})
				.click();

			await expect(
				modelBuilderRightSidebarPage.sidebarLabelInput
			).toHaveValue(objectDefinition.label['pt_BR']);

			await page.keyboard.press('Escape');

			await expect(
				modelBuilderRightSidebarPage.objectDefinitionPluralLabel
			).toHaveValue(objectDefinition.pluralLabel['pt_BR']);

			await modelBuilderRightSidebarPage.objectDefinitionPluralLabelLocalizationButton.click();

			await page
				.getByRole('option', {name: 'en_US language: Default'})
				.dispatchEvent('click');

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

			// Seo Container

			await expect(
				modelBuilderRightSidebarPage.objectDefinitionSeo
			).toBeChecked({
				checked: objectDefinition.enableFriendlyURLCustomization,
			});
		}
	});
});

test.describe('Manage object definitions through View Object Definitions', () => {
	test('action buttons should be disabled while waiting for API responses', async ({
		apiHelpers,
		editObjectDetailsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 2},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await page.route(
			'**/object-definitions/by-external-reference-code/*',
			async (route) => {
				await new Promise((fulfill) => setTimeout(fulfill, 300));
				await route.continue();
			}
		);

		await editObjectDetailsPage.goto(objectDefinition.label['en_US']);

		const publishButton = page.getByRole('button', {name: 'Publish'});

		await expect(publishButton).toBeDisabled();

		await page.waitForResponse(
			'**/object-definitions/by-external-reference-code/*'
		);

		await expect(publishButton).toBeEnabled();

		await publishButton.click();

		const saveButton = page.getByRole('button', {name: 'save'});

		await expect(publishButton).toBeDisabled();

		await page.waitForResponse('**/object-definitions/*/publish');

		await expect(saveButton).toBeDisabled();

		await page.waitForResponse(
			'**/object-definitions/by-external-reference-code/*'
		);

		await expect(saveButton).toBeEnabled();

		await saveButton.click();

		await expect(saveButton).toBeDisabled();

		await page.waitForResponse(
			'**/object-definitions/by-external-reference-code/*'
		);

		await expect(saveButton).toBeEnabled();
	});

	test('can delete an object definition by FDS action', async ({
		apiHelpers,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 2},
			});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 2},
			});

		const objectDefinition3 =
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
		apiHelpers.data.push({
			id: objectDefinition3.id,
			type: 'objectDefinition',
		});

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.clickObjectDefinitionActionButton(
			objectDefinition2.label['en_US']
		);

		await viewObjectDefinitionsPage.deleteObjectDefinitionOption.click();

		apiHelpers.data.splice(
			apiHelpers.data.findIndex(
				(object) =>
					object.id === objectDefinition2.id &&
					object.type === 'objectDefinition'
			),
			1
		);

		await viewObjectDefinitionsPage.clickObjectDefinitionActionButton(
			objectDefinition3.label!['en_US']
		);

		await viewObjectDefinitionsPage.deleteObjectDefinitionOption.click();

		await page
			.getByPlaceholder('Confirm Object Definition Name')
			.fill(objectDefinition3.name!);

		await page.getByRole('button', {exact: true, name: 'Delete'}).click();

		apiHelpers.data.splice(
			apiHelpers.data.findIndex(
				(object) =>
					object.id === objectDefinition3.id &&
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

		await expect(
			viewObjectDefinitionsPage.frontendDataSetEntries.filter({
				hasText: objectDefinition3.label!['en_US'],
			})
		).toBeHidden();
	});

	test('can restrict a previously created object', async ({
		apiHelpers,
		editObjectDetailsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 2},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await editObjectDetailsPage.goto(objectDefinition.label['en_US']);

		await editObjectDetailsPage.goToDetailsTab();

		await expect(
			editObjectDetailsPage.accountRestrictionToggle
		).toBeDisabled();

		await apiHelpers.headlessAdminUser.postAccount();

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: accountObjectDefinition} =
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				'L_ACCOUNT'
			);

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
					objectDefinitionId1: accountObjectDefinition.id,
					objectDefinitionId2: objectDefinition.id,
					objectDefinitionName2: objectDefinition.name,
					type: 'oneToMany',
				}
			);

		apiHelpers.data.push({
			id: objectRelationship.id,
			type: 'objectRelationship',
		});

		await page.reload();

		await editObjectDetailsPage.goToDetailsTab();

		await editObjectDetailsPage.enableAccountRestriction(
			objectRelationship.label['en_US']
		);

		await editObjectDetailsPage.publishButton.click();

		await waitForAlert(page, 'The object was published successfully');

		await page.reload();

		await editObjectDetailsPage.goToDetailsTab();

		await expect(
			editObjectDetailsPage.accountRestrictionToggle
		).toBeDisabled();
	});

	test('can save changes when publishing', async ({
		apiHelpers,
		editObjectDetailsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 2},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const label = 'UpdatedLabel' + getRandomInt();

		await editObjectDetailsPage.goto(objectDefinition.label['en_US']);

		await editObjectDetailsPage.goToDetailsTab();

		await editObjectDetailsPage.waitForDetailsFormLoaded();

		await editObjectDetailsPage.labelInput.fill(label);

		await editObjectDetailsPage.publishButton.click();

		await waitForAlert(page, 'The object was published successfully');

		await page.reload();

		await editObjectDetailsPage.goToDetailsTab();

		await expect(editObjectDetailsPage.labelInput).toHaveValue(label);
	});

	test('can set a different language value for the label and plural label', async ({
		apiHelpers,
		editObjectDetailsPage,
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

		const label = 'Rótulo em Português';
		const pluralLabel = 'Rótulos em Português';

		await editObjectDetailsPage.goto(objectDefinition.label['en_US']);

		await editObjectDetailsPage.goToDetailsTab();

		await editObjectDetailsPage.waitForDetailsFormLoaded();

		await editObjectDetailsPage.selectLabelLanguage('pt_BR');

		await editObjectDetailsPage.labelInput.fill(label);

		await editObjectDetailsPage.selectPluralLabelLanguage('pt_BR');

		await editObjectDetailsPage.pluralLabelInput.fill(pluralLabel);

		await editObjectDetailsPage.saveObjectDefinition();

		await waitForAlert(page, 'The object was saved successfully');

		await page.reload();

		await editObjectDetailsPage.goToDetailsTab();

		await editObjectDetailsPage.selectLabelLanguage('pt_BR');

		await expect(editObjectDetailsPage.labelInput).toHaveValue(label);

		await editObjectDetailsPage.selectPluralLabelLanguage('pt_BR');

		await expect(editObjectDetailsPage.pluralLabelInput).toHaveValue(
			pluralLabel
		);
	});

	test(
		'can set the external reference code field as the title field',
		{tag: '@LPD-102828'},
		async ({apiHelpers, editObjectDetailsPage, page}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await editObjectDetailsPage.goto(objectDefinition.label['en_US']);

			await editObjectDetailsPage.goToDetailsTab();

			await editObjectDetailsPage.entryTitleField.click();

			await page
				.getByRole('option', {
					exact: true,
					name: 'External Reference Code',
				})
				.click();

			await editObjectDetailsPage.saveObjectDefinition();

			await editObjectDetailsPage.goto(objectDefinition.label['en_US']);

			await editObjectDetailsPage.goToDetailsTab();

			await expect(editObjectDetailsPage.entryTitleField).toContainText(
				'External Reference Code'
			);
		}
	);

	test(
		'can set Title Field for a system object',
		{tag: '@LPS-145393'},
		async ({apiHelpers: _apiHelpers, editObjectDetailsPage, page}) => {
			await editObjectDetailsPage.goto('User');

			await editObjectDetailsPage.goToDetailsTab();

			const originalEntryTitleField =
				(
					await editObjectDetailsPage.entryTitleField.textContent()
				)?.trim() ?? '';

			const entryTitleFieldName =
				originalEntryTitleField === 'Screen Name'
					? 'Email Address'
					: 'Screen Name';

			await editObjectDetailsPage.entryTitleField.click();

			await page
				.getByRole('option', {exact: true, name: entryTitleFieldName})
				.click();

			await editObjectDetailsPage.saveObjectDefinition();

			await editObjectDetailsPage.goto('User');

			await editObjectDetailsPage.goToDetailsTab();

			await expect(editObjectDetailsPage.entryTitleField).toContainText(
				entryTitleFieldName
			);

			await editObjectDetailsPage.entryTitleField.click();

			await page
				.getByRole('option', {
					exact: true,
					name: originalEntryTitleField,
				})
				.click();

			await editObjectDetailsPage.saveObjectDefinition();
		}
	);

	test('can update editable fields and cannot update disabled fields after publishing', async ({
		apiHelpers,
		editObjectDetailsPage,
		page,
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

		const objectFieldLabel = objectFields[0].label!['en_US'];
		const label = 'UpdatedLabel' + getRandomInt();
		const pluralLabel = 'UpdatedPlural' + getRandomInt();

		await editObjectDetailsPage.goto(objectDefinition.label['en_US']);

		await editObjectDetailsPage.goToDetailsTab();

		await editObjectDetailsPage.waitForDetailsFormLoaded();

		await expect(editObjectDetailsPage.nameInput).toBeDisabled();
		await expect(editObjectDetailsPage.scopeCombobox).toBeDisabled();

		await editObjectDetailsPage.labelInput.fill(label);
		await editObjectDetailsPage.pluralLabelInput.fill(pluralLabel);
		await editObjectDetailsPage.selectEntryTitleField(objectFieldLabel);
		await editObjectDetailsPage.selectPanelLink('Object');

		await editObjectDetailsPage.saveObjectDefinition();

		await waitForAlert(page, 'The object was saved successfully');

		await page.reload();

		await editObjectDetailsPage.goToDetailsTab();

		await expect(editObjectDetailsPage.labelInput).toHaveValue(label);
		await expect(editObjectDetailsPage.pluralLabelInput).toHaveValue(
			pluralLabel
		);
		await expect(editObjectDetailsPage.entryTitleFieldCombobox).toHaveText(
			objectFieldLabel
		);
		await expect(editObjectDetailsPage.panelLinkCombobox).toHaveText(
			'Object',
			{ignoreCase: true}
		);
	});

	test('can update fields of a draft object before publishing', async ({
		apiHelpers,
		editObjectDetailsPage,
		page,
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

		const objectFieldLabel = objectFields[0].label!['en_US'];
		const label = 'UpdatedLabel' + getRandomInt();
		const name = 'UpdatedName' + getRandomInt();
		const pluralLabel = 'UpdatedPlural' + getRandomInt();

		await editObjectDetailsPage.goto(objectDefinition.label['en_US']);

		await editObjectDetailsPage.goToDetailsTab();

		await editObjectDetailsPage.waitForDetailsFormLoaded();

		await editObjectDetailsPage.labelInput.fill(label);
		await editObjectDetailsPage.pluralLabelInput.fill(pluralLabel);
		await editObjectDetailsPage.nameInput.fill(name);
		await editObjectDetailsPage.selectEntryTitleField(objectFieldLabel);
		await editObjectDetailsPage.selectScope('Company');

		await editObjectDetailsPage.selectPanelLink('Object');

		await editObjectDetailsPage.saveObjectDefinition();

		await waitForAlert(page, 'The object was saved successfully');

		await page.reload();

		await editObjectDetailsPage.goToDetailsTab();

		await expect(editObjectDetailsPage.labelInput).toHaveValue(label);
		await expect(editObjectDetailsPage.pluralLabelInput).toHaveValue(
			pluralLabel
		);
		await expect(editObjectDetailsPage.nameInput).toHaveValue(name);
		await expect(editObjectDetailsPage.entryTitleFieldCombobox).toHaveText(
			objectFieldLabel
		);
		await expect(editObjectDetailsPage.scopeCombobox).toHaveText(
			'Company',
			{ignoreCase: true}
		);
		await expect(editObjectDetailsPage.panelLinkCombobox).toHaveText(
			'Object',
			{ignoreCase: true}
		);

		await editObjectDetailsPage.selectScope('Site');

		await editObjectDetailsPage.selectPanelLink('Content & Data');

		await editObjectDetailsPage.saveObjectDefinition();

		await waitForAlert(page, 'The object was saved successfully');

		await page.reload();

		await editObjectDetailsPage.goToDetailsTab();

		await expect(editObjectDetailsPage.scopeCombobox).toHaveText('Site', {
			ignoreCase: true,
		});
		await expect(editObjectDetailsPage.panelLinkCombobox).toHaveText(
			'Content & Data',
			{ignoreCase: true}
		);
	});

	test('can view and edit its own object with only the add permission', async ({
		apiHelpers,
		editObjectDetailsPage,
		modalAddObjectDefinitionPage,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'ObjRole' + getRandomInt(),
			rolePermissions: [
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet',
					scope: 1,
				},
				{
					actionIds: ['ADD_OBJECT_DEFINITION'],
					primaryKey: companyId,
					resourceName: 'com.liferay.object',
					scope: 1,
				},
				{
					actionIds: ['UPDATE', 'VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.object.model.ObjectDefinition',
					scope: 1,
				},
				{
					actionIds: ['VIEW_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
			],
		});

		apiHelpers.data.push({id: role.id, type: 'role'});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		apiHelpers.data.push({id: user.id, type: 'userAccount'});

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		await performUserSwitch(page, user.alternateName);

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.createObjectDefinitionButton.click();

		const objectLabel = 'CustomObject' + getRandomInt();

		const objectDefinition =
			await modalAddObjectDefinitionPage.createObjectDefinition(
				objectLabel
			);

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await editObjectDetailsPage.goto(objectDefinition.label['en_US']);

		await editObjectDetailsPage.goToDetailsTab();

		await editObjectDetailsPage.waitForDetailsFormLoaded();

		const label = 'UpdatedLabel' + getRandomInt();

		await editObjectDetailsPage.labelInput.fill(label);

		await editObjectDetailsPage.saveObjectDefinition();

		await waitForAlert(page, 'The object was saved successfully');

		await page.reload();

		await editObjectDetailsPage.goToDetailsTab();

		await expect(editObjectDetailsPage.labelInput).toHaveValue(label);
	});

	test('can view the object management toolbar in different tabs.', async ({
		apiHelpers,
		editObjectDetailsPage,
		page,
	}) => {
		const objectDefinitionExternalReferenceCode =
			'ObjectDefinitionExternalReferenceCode' + getRandomInt();
		const objectDefinitionLabel = 'ObjectDefinitionLabel' + getRandomInt();

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {body: objectDefinition} =
			await objectDefinitionAPIClient.postObjectDefinition({
				enableFormContainer: true,
				externalReferenceCode: objectDefinitionExternalReferenceCode,
				label: {
					en_US: objectDefinitionLabel,
				},
				name: 'ObjectDefinitionName' + getRandomInt(),
				pluralLabel: {
					en_US: 'ObjectDefinitionsLabel' + getRandomInt(),
				},
				scope: 'company',
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await editObjectDetailsPage.goto(objectDefinition.label['en_US']);

		const tabs = [
			'Details',
			'Fields',
			'Relationships',
			'Actions',
			'Views',
			'Validations',
			'State Manager',
		];

		for (const tab of tabs) {
			await page.getByRole('link', {name: tab}).click();

			await expect(
				page.getByRole('heading', {
					level: 3,
					name: objectDefinition.label['en_US'],
				})
			).toBeVisible();

			await expect(
				page.getByLabel('Edit External Reference Code')
			).toBeVisible();

			await expect(
				page.getByText(objectDefinition.externalReferenceCode)
			).toBeVisible();

			await expect(
				page.getByText(objectDefinition.id.toString())
			).toBeVisible();
		}
	});

	test(
		'delete modal shows the number of object entries that will be deleted',
		{tag: '@LPS-150886'},
		async ({apiHelpers, page, viewObjectDefinitionsPage}) => {
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
			const fieldName = objectFields[0].name!;

			await apiHelpers.objectEntry.postObjectEntry(
				{[fieldName]: 'Entry A'},
				applicationName
			);

			await apiHelpers.objectEntry.postObjectEntry(
				{[fieldName]: 'Entry B'},
				applicationName
			);

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.clickObjectDefinitionActionButton(
				objectDefinition.label!['en_US']
			);

			await viewObjectDefinitionsPage.deleteObjectDefinitionOption.click();

			await expect(
				page.getByText('Delete Object Definition')
			).toBeVisible();

			await expect(page.getByText('2 object entries')).toBeVisible();
		}
	);

	test(
		'cannot delete a published object definition with a mismatched confirmation name',
		{tag: '@LPS-150886'},
		async ({apiHelpers, page, viewObjectDefinitionsPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.clickObjectDefinitionActionButton(
				objectDefinition.label!['en_US']
			);

			await viewObjectDefinitionsPage.deleteObjectDefinitionOption.click();

			await page
				.getByPlaceholder('Confirm Object Definition Name', {
					exact: false,
				})
				.fill('WrongValue');

			await expect(page.getByText('Input does not match')).toBeVisible();

			await expect(
				page.getByRole('button', {exact: true, name: 'Delete'})
			).toBeDisabled();
		}
	);

	test('cannot publish an object without the publish permission', async ({
		apiHelpers,
		editObjectDetailsPage,
		page,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 2},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const companyId = await page.evaluate(() => {
			return Liferay.ThemeDisplay.getCompanyId();
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: 'ObjRole' + getRandomInt(),
			rolePermissions: [
				{
					actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
					primaryKey: companyId,
					resourceName:
						'com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet',
					scope: 1,
				},
				{
					actionIds: ['VIEW'],
					primaryKey: companyId,
					resourceName: 'com.liferay.object.model.ObjectDefinition',
					scope: 1,
				},
				{
					actionIds: ['VIEW_CONTROL_PANEL'],
					primaryKey: companyId,
					resourceName: '90',
					scope: 1,
				},
			],
		});

		apiHelpers.data.push({id: role.id, type: 'role'});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		apiHelpers.data.push({id: user.id, type: 'userAccount'});

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		await performUserSwitch(page, user.alternateName);

		await editObjectDetailsPage.goto(objectDefinition.label['en_US']);

		await expect(editObjectDetailsPage.publishButton).toBeDisabled();
		await expect(editObjectDetailsPage.saveButton).toBeDisabled();
	});

	test('cannot publish definition with duplicate friendlyURL prefix', async ({
		apiHelpers,
		editObjectDetailsPage,
		page,
	}) => {
		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
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

		await editObjectDetailsPage.goto(objectDefinition2.label['en_US']);

		await editObjectDetailsPage.friendlyURLSeparator.fill(
			`c_${objectDefinition1.name}`
		);

		await editObjectDetailsPage.publishButton.click();

		await expect(editObjectDetailsPage.publishButton).toBeDisabled();

		await expect(
			page.getByText('Other asset types may use this prefix.', {
				exact: true,
			})
		).toBeVisible();

		await expect(editObjectDetailsPage.publishButton).toBeEnabled();
	});

	test('cannot save an object definition without a translation after changing the default language', async ({
		apiHelpers,
		editObjectDetailsPage,
		localizationInstanceSettingsPage,
		page,
		restoreInstanceDefaultLanguage: _restoreInstanceDefaultLanguage,
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

		await localizationInstanceSettingsPage.goto('Language', false);

		await localizationInstanceSettingsPage.setDefaultLanguage('pt_BR');

		await editObjectDetailsPage.goto(objectDefinition.label['en_US']);

		await editObjectDetailsPage.goToDetailsTab();

		const labelInput = page.getByRole('textbox', {
			exact: true,
			name: 'Label Mandatory',
		});

		await labelInput.fill(getRandomString());

		await editObjectDetailsPage.saveButton.click();

		await expect(page.getByText('Required')).toBeVisible();
	});

	const titleFieldTest = test.extend<{
		restoreAccountEntryTitleFieldAndDefaultLanguage: void;
	}>({
		restoreAccountEntryTitleFieldAndDefaultLanguage: async (
			{editObjectDetailsPage, localizationInstanceSettingsPage, page},
			use
		) => {
			try {
				await use();
			}
			finally {
				await localizationInstanceSettingsPage.goto('Language', false);

				await localizationInstanceSettingsPage.setDefaultLanguage(
					'en_US'
				);

				await editObjectDetailsPage.goto('Account');

				await editObjectDetailsPage.selectEntryTitleField('Name');

				await editObjectDetailsPage.saveObjectDefinition();

				await waitForAlert(page, 'The object was saved successfully.');
			}
		},
	});

	titleFieldTest(
		'verify object entries display the new title field correctly when changing the localization on Instance Settings',
		async ({
			apiHelpers,
			editObjectDetailsPage,
			localizationInstanceSettingsPage,
			page,
			restoreAccountEntryTitleFieldAndDefaultLanguage:
				_restoreAccountEntryTitleFieldAndDefaultLanguage,
			viewObjectEntriesPage,
		}) => {
			let objectDefinition: ObjectDefinition;
			let relationshipLabel: string;
			let relationshipName: string;

			await test.step('Create an Account and a Custom Object with a Relationship 1toM of Account to Custom Object', async () => {
				await apiHelpers.headlessAdminUser.postAccount({
					type: 'business',
				});

				const objectFields = generateObjectFields({
					objectFieldBusinessTypes: ['Text'],
				});

				objectDefinition =
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

				relationshipLabel = 'Relationship';
				relationshipName = await getFreshObjectRelationshipName(
					apiHelpers,
					['L_ACCOUNT', objectDefinition.externalReferenceCode!],
					'relationship'
				);

				await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
					'L_ACCOUNT',
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
			});

			await test.step('Change the default language to Portuguese', async () => {
				await localizationInstanceSettingsPage.goto('Language');

				await localizationInstanceSettingsPage.setDefaultLanguage(
					'pt_BR'
				);
			});

			await test.step('Change the title field in the "Account" system object', async () => {
				await editObjectDetailsPage.goto('Account');

				await editObjectDetailsPage.selectEntryTitleField('Type');

				await editObjectDetailsPage.saveObjectDefinition();

				await waitForAlert(page, 'The object was saved successfully.');
			});

			await test.step('Create an entry using the title field selected', async () => {
				await viewObjectEntriesPage.goto(objectDefinition.className!);

				await viewObjectEntriesPage.clickAddObjectEntry(
					objectDefinition.label!['en_US']
				);

				await page
					.locator('.form-group', {hasText: relationshipLabel})
					.getByPlaceholder('Search')
					.click();

				await page
					.getByRole('menuitem', {name: /business/i})
					.first()
					.click();

				await viewObjectEntriesPage.saveObjectEntryButton.click();

				await waitForAlert(page);
			});

			await test.step('Verify the title field of System Object is present', async () => {
				await viewObjectEntriesPage.backButton.click();

				await expect(
					page.getByRole('cell', {name: /business/i}).first()
				).toBeVisible();

				await page
					.getByRole('cell', {name: /business/i})
					.first()
					.click();

				await expect(page.getByText(/business/i).first()).toBeVisible();
			});
		}
	);

	test('verify it is possible to update Custom Object when changing the localization on Instance Settings', async ({
		apiHelpers,
		editObjectDetailsPage,
		localizationInstanceSettingsPage,
		page,
		restoreInstanceDefaultLanguage: _restoreInstanceDefaultLanguage,
	}) => {
		const newLabel = 'Objeto Personalizado ' + getRandomInt();
		const newPluralLabel = 'Objetos Personalizados ' + getRandomInt();

		let objectDefinition: ObjectDefinition;

		await test.step('Create a custom object', async () => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});
		});

		await test.step('Change default language to Portuguese', async () => {
			await localizationInstanceSettingsPage.goto('Language');

			await localizationInstanceSettingsPage.defaultLanguageSelect.selectOption(
				'pt_BR'
			);

			await localizationInstanceSettingsPage.saveSettings();
		});

		await test.step('Navigate to Object Admin and update the object label', async () => {
			await editObjectDetailsPage.goto(objectDefinition.label['en_US']);

			await editObjectDetailsPage.goToDetailsTab();

			await editObjectDetailsPage.waitForDetailsFormLoaded();

			await editObjectDetailsPage.labelInput.fill(newLabel);
			await editObjectDetailsPage.pluralLabelInput.fill(newPluralLabel);

			await editObjectDetailsPage.saveObjectDefinition();

			await waitForAlert(page, 'The object was saved successfully.');
		});

		await test.step('Assert the object label and plural label were updated', async () => {
			await expect(editObjectDetailsPage.labelInput).toHaveValue(
				newLabel
			);
			await expect(editObjectDetailsPage.pluralLabelInput).toHaveValue(
				newPluralLabel
			);
		});
	});

	test(
		'verify that the Access in Control Panel and View permissions control Object Admin portlet access and object visibility',
		{tag: '@LPS-135390'},
		async ({
			apiHelpers,
			globalMenuPage,
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

			const companyId = await page.evaluate(() => {
				return Liferay.ThemeDisplay.getCompanyId();
			});

			const role1 = await apiHelpers.headlessAdminUser.postRole({
				name: 'role' + getRandomInt(),
				rolePermissions: [
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL'],
						primaryKey: companyId,
						resourceName:
							'com_liferay_object_web_internal_list_type_portlet_portlet_ListTypeDefinitionsPortlet',
						scope: 1,
					},
					{
						actionIds: ['VIEW_CONTROL_PANEL'],
						primaryKey: companyId,
						resourceName: '90',
						scope: 1,
					},
				],
			});

			apiHelpers.data.push({id: role1.id, type: 'role'});

			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			apiHelpers.data.push({id: user.id, type: 'userAccount'});

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			await apiHelpers.headlessAdminUser.assignUserToRole(
				role1.externalReferenceCode,
				user.id
			);

			await performLogout(page);
			await performLoginViaApi({page, screenName: user.alternateName});

			await globalMenuPage.goToControlPanel();

			await expect(
				page.getByRole('menuitem', {exact: true, name: 'Objects'})
			).toBeHidden();

			await viewObjectDefinitionsPage.goto();

			await expect(
				page.getByText(
					'You do not have the roles required to access this portlet.'
				)
			).toBeVisible();

			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			const role2 = await apiHelpers.headlessAdminUser.postRole({
				name: 'role' + getRandomInt(),
				rolePermissions: [
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
						primaryKey: companyId,
						resourceName:
							'com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet',
						scope: 1,
					},
				],
			});

			apiHelpers.data.push({id: role2.id, type: 'role'});

			await apiHelpers.headlessAdminUser.assignUserToRole(
				role2.externalReferenceCode,
				user.id
			);

			await performLogout(page);
			await performLoginViaApi({page, screenName: user.alternateName});

			await globalMenuPage.goToControlPanel();

			await expect(
				page.getByRole('menuitem', {exact: true, name: 'Objects'})
			).toBeVisible();

			await viewObjectDefinitionsPage.goto();

			await expect(
				page.getByText('No Objects Created Yet', {
					exact: true,
				})
			).toBeVisible();

			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			const role3 = await apiHelpers.headlessAdminUser.postRole({
				name: 'role' + getRandomInt(),
				rolePermissions: [
					{
						actionIds: ['VIEW'],
						primaryKey: companyId,
						resourceName:
							'com.liferay.object.model.ObjectDefinition',
						scope: 1,
					},
				],
			});

			apiHelpers.data.push({id: role3.id, type: 'role'});

			await apiHelpers.headlessAdminUser.assignUserToRole(
				role3.externalReferenceCode,
				user.id
			);

			await performLogout(page);
			await performLoginViaApi({page, screenName: user.alternateName});

			await viewObjectDefinitionsPage.goto();

			await expect(
				page.getByRole('link', {
					name: objectDefinition.label['en_US'],
				})
			).toBeVisible();
		}
	);

	test(
		'verify that the Add Object Definition permission controls the ability to create an Object',
		{tag: '@LPS-135390'},
		async ({
			apiHelpers,
			modalAddObjectDefinitionPage,
			page,
			viewObjectDefinitionsPage,
		}) => {
			const companyId = await page.evaluate(() => {
				return Liferay.ThemeDisplay.getCompanyId();
			});

			const role1 = await apiHelpers.headlessAdminUser.postRole({
				name: 'role' + getRandomInt(),
				rolePermissions: [
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL'],
						primaryKey: companyId,
						resourceName:
							'com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet',
						scope: 1,
					},
					{
						actionIds: ['VIEW_CONTROL_PANEL'],
						primaryKey: companyId,
						resourceName: '90',
						scope: 1,
					},
				],
			});

			apiHelpers.data.push({id: role1.id, type: 'role'});

			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			apiHelpers.data.push({id: user.id, type: 'userAccount'});

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			await apiHelpers.headlessAdminUser.assignUserToRole(
				role1.externalReferenceCode,
				user.id
			);

			await performLogout(page);
			await performLoginViaApi({page, screenName: user.alternateName});

			await viewObjectDefinitionsPage.goto();

			await expect(
				viewObjectDefinitionsPage.createObjectDefinitionButton
			).toBeHidden();

			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			const role2 = await apiHelpers.headlessAdminUser.postRole({
				name: 'role' + getRandomInt(),
				rolePermissions: [
					{
						actionIds: ['ADD_OBJECT_DEFINITION'],
						primaryKey: companyId,
						resourceName: 'com.liferay.object',
						scope: 1,
					},
				],
			});

			apiHelpers.data.push({id: role2.id, type: 'role'});

			await apiHelpers.headlessAdminUser.assignUserToRole(
				role2.externalReferenceCode,
				user.id
			);

			await performLogout(page);
			await performLoginViaApi({page, screenName: user.alternateName});

			await viewObjectDefinitionsPage.goto();

			await expect(
				viewObjectDefinitionsPage.createObjectDefinitionButton
			).toBeVisible();

			await viewObjectDefinitionsPage.createObjectDefinitionButton.click();

			const objectDefinition =
				await modalAddObjectDefinitionPage.createObjectDefinition(
					'CustomObject' + getRandomInt()
				);

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await waitForAlert(
				page,
				`Success:${objectDefinition.label['en_US']} was created successfully.`
			);

			await expect(
				page.getByRole('link', {name: objectDefinition.label['en_US']})
			).toBeVisible();
		}
	);

	test(
		'verify that the Delete permission controls the ability to delete an Object',
		{tag: '@LPS-135390'},
		async ({apiHelpers, page, viewObjectDefinitionsPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const companyId = await page.evaluate(() => {
				return Liferay.ThemeDisplay.getCompanyId();
			});

			const role1 = await apiHelpers.headlessAdminUser.postRole({
				name: 'role' + getRandomInt(),
				rolePermissions: [
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
						primaryKey: companyId,
						resourceName:
							'com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet',
						scope: 1,
					},
					{
						actionIds: ['VIEW'],
						primaryKey: companyId,
						resourceName:
							'com.liferay.object.model.ObjectDefinition',
						scope: 1,
					},
					{
						actionIds: ['VIEW_CONTROL_PANEL'],
						primaryKey: companyId,
						resourceName: '90',
						scope: 1,
					},
				],
			});

			apiHelpers.data.push({id: role1.id, type: 'role'});

			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			apiHelpers.data.push({id: user.id, type: 'userAccount'});

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			await apiHelpers.headlessAdminUser.assignUserToRole(
				role1.externalReferenceCode,
				user.id
			);

			await performLogout(page);
			await performLoginViaApi({page, screenName: user.alternateName});

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.clickObjectDefinitionActionButton(
				objectDefinition.label['en_US']
			);

			await expect(
				viewObjectDefinitionsPage.deleteObjectDefinitionOption
			).toBeHidden();

			await performLogout(page);
			await performLoginViaApi({page, screenName: 'test'});

			const role2 = await apiHelpers.headlessAdminUser.postRole({
				name: 'role' + getRandomInt(),
				rolePermissions: [
					{
						actionIds: ['DELETE', 'UPDATE'],
						primaryKey: companyId,
						resourceName:
							'com.liferay.object.model.ObjectDefinition',
						scope: 1,
					},
				],
			});

			apiHelpers.data.push({id: role2.id, type: 'role'});

			await apiHelpers.headlessAdminUser.assignUserToRole(
				role2.externalReferenceCode,
				user.id
			);

			await performLogout(page);
			await performLoginViaApi({page, screenName: user.alternateName});

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.clickObjectDefinitionActionButton(
				objectDefinition.label['en_US']
			);

			await expect(
				viewObjectDefinitionsPage.deleteObjectDefinitionOption
			).toBeVisible();

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.changeObjectActivateStatus(
				objectDefinition.name
			);

			await expect(
				page.getByRole('switch', {name: 'Activate Object'})
			).not.toBeChecked();

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.clickObjectDefinitionActionButton(
				objectDefinition.label['en_US']
			);

			await viewObjectDefinitionsPage.deleteObjectDefinitionOption.click();

			await page
				.getByPlaceholder('Confirm Object Definition Name')
				.fill(objectDefinition.name);

			await page.getByRole('button', {name: 'Delete'}).click();

			apiHelpers.data.splice(
				apiHelpers.data.findIndex(
					(object) =>
						object.id === objectDefinition.id &&
						object.type === 'objectDefinition'
				),
				1
			);

			await waitForAlert(
				page,
				`Success:${objectDefinition.label['en_US']} was deleted successfully.`
			);

			await expect(
				page.getByRole('link', {
					name: objectDefinition.label['en_US'],
				})
			).toBeHidden();
		}
	);

	test(
		'verify that it is not possible to create an Object with a duplicated Object Name',
		{tag: '@LPS-135549'},
		async ({
			apiHelpers,
			modalAddObjectDefinitionPage,
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

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.createObjectDefinitionButton.click();

			await modalAddObjectDefinitionPage.objectLabelInput.fill(
				objectDefinition.label['en_US']
			);
			await modalAddObjectDefinitionPage.objectPluralLabelInput.fill(
				objectDefinition.pluralLabel['en_US']
			);

			await modalAddObjectDefinitionPage.objectDefinitionSaveButton.click();

			await expect(
				page.getByText('This name is already in use. Try another one.')
			).toBeVisible();
		}
	);

	test(
		'verify required field validations and Object Name autofill when creating an Object',
		{tag: '@LPS-135549'},
		async ({
			modalAddObjectDefinitionPage,
			page,
			viewObjectDefinitionsPage,
		}) => {
			const objectDefinitionLabel = 'Object' + getRandomInt();

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.createObjectDefinitionButton.click();

			await modalAddObjectDefinitionPage.objectLabelInput.fill(
				objectDefinitionLabel
			);
			await modalAddObjectDefinitionPage.objectPluralLabelInput.fill(
				objectDefinitionLabel
			);
			await expect(
				modalAddObjectDefinitionPage.objectNameInput
			).toHaveValue(objectDefinitionLabel);

			await modalAddObjectDefinitionPage.objectNameInput.fill('');

			await modalAddObjectDefinitionPage.objectDefinitionSaveButton.click();

			await expect(page.getByText('Required')).toBeVisible();

			await modalAddObjectDefinitionPage.objectNameInput.fill(
				objectDefinitionLabel
			);
			await modalAddObjectDefinitionPage.objectLabelInput.fill('');
			await modalAddObjectDefinitionPage.objectDefinitionSaveButton.click();

			await expect(page.getByText('Required')).toBeVisible();

			await modalAddObjectDefinitionPage.objectLabelInput.fill(
				objectDefinitionLabel
			);

			await modalAddObjectDefinitionPage.objectPluralLabelInput.fill('');
			await modalAddObjectDefinitionPage.objectDefinitionSaveButton.click();

			await expect(page.getByText('Required')).toBeVisible();

			await modalAddObjectDefinitionPage.objectPluralLabelInput.fill(
				objectDefinitionLabel
			);

			await modalAddObjectDefinitionPage.objectDefinitionSaveButton.click();

			await waitForAlert(
				page,
				`Success:${objectDefinitionLabel} was created successfully.`
			);

			await viewObjectDefinitionsPage.clickObjectDefinitionActionButton(
				objectDefinitionLabel
			);

			await viewObjectDefinitionsPage.deleteObjectDefinitionOption.click();

			await waitForAlert(
				page,
				`Success:${objectDefinitionLabel} was deleted successfully.`
			);
		}
	);

	test(
		'verify it is possible to search for Custom and System Objects and verify the empty state when no results are found',
		{tag: '@LPS-135547'},
		async ({apiHelpers, page, viewObjectDefinitionsPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.searchInput.fill(
				objectDefinition.label['en_US']
			);

			await page.keyboard.press('Enter');

			await expect(
				page.getByRole('link', {
					exact: true,
					name: objectDefinition.label['en_US'],
				})
			).toBeVisible();

			await viewObjectDefinitionsPage.searchInput.fill('Account');

			await page.keyboard.press('Enter');

			await expect(
				page.getByRole('link', {exact: true, name: 'Account'})
			).toBeVisible();

			await expect(
				page.getByRole('link', {
					exact: true,
					name: objectDefinition.label['en_US'],
				})
			).toBeHidden();

			await viewObjectDefinitionsPage.searchInput.fill(
				'NonExistentObject' + getRandomInt()
			);

			await page.keyboard.press('Enter');

			await expect(page.getByText('No Results Found')).toBeVisible();
		}
	);

	test(
		'verify that previous filled data is not kept when cancelling the creation of an Object',
		{tag: '@LPS-139418'},
		async ({
			modalAddObjectDefinitionPage,
			page,
			viewObjectDefinitionsPage,
		}) => {
			await viewObjectDefinitionsPage.goto();

			const objectDefinitionLabel = 'CustomObject' + getRandomInt();

			await viewObjectDefinitionsPage.createObjectDefinitionButton.click();

			await modalAddObjectDefinitionPage.objectLabelInput.fill(
				objectDefinitionLabel
			);
			await modalAddObjectDefinitionPage.objectPluralLabelInput.fill(
				objectDefinitionLabel
			);

			await page.getByRole('button', {name: 'Cancel'}).click();

			await expect(page.getByText(objectDefinitionLabel)).toBeHidden();

			await viewObjectDefinitionsPage.createObjectDefinitionButton.click();

			await expect(
				modalAddObjectDefinitionPage.objectLabelInput
			).toHaveValue('');
			await expect(
				modalAddObjectDefinitionPage.objectPluralLabelInput
			).toHaveValue('');
		}
	);

	test(
		'verify that the Object portlet visibility in the Open Menu changes according to activation status',
		{tag: '@LPS-139005'},
		async ({
			apiHelpers,
			globalMenuPage,
			page,
			viewObjectDefinitionsPage,
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

			await page.goto('/');

			await globalMenuPage.goToControlPanel();

			await expect(
				page.getByRole('link', {
					name: objectDefinition.pluralLabel['en_US'],
				})
			).toBeVisible();

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.changeObjectActivateStatus(
				objectDefinition.name
			);

			await page.goto('/');

			await globalMenuPage.goToControlPanel();

			await expect(
				page.getByRole('link', {
					name: objectDefinition.pluralLabel['en_US'],
				})
			).toBeHidden();

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.changeObjectActivateStatus(
				objectDefinition.name
			);

			await page.goto('/');

			await globalMenuPage.goToControlPanel();

			await expect(
				page.getByRole('link', {
					name: objectDefinition.pluralLabel['en_US'],
				})
			).toBeVisible();
		}
	);

	test(
		'verify that the Object portlet visibility in the Site Menu changes according to activation status',
		{tag: '@LPS-139005'},
		async ({
			apiHelpers,
			page,
			productMenuPage,
			viewObjectDefinitionsPage,
		}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					panelCategoryKey: 'site_administration.content',
					scope: 'site',
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			await page.reload();

			await productMenuPage.openProductMenuIfClosed();

			await productMenuPage.contentAndDataButton.click();

			await expect(
				page.getByRole('menuitem', {
					name: objectDefinition.pluralLabel['en_US'],
				})
			).toBeVisible();

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.changeObjectActivateStatus(
				objectDefinition.name
			);

			await page.goto('/');

			await productMenuPage.openProductMenuIfClosed();

			await productMenuPage.contentAndDataButton.click();

			await expect(
				page.getByRole('menuitem', {
					name: objectDefinition.pluralLabel['en_US'],
				})
			).toBeHidden();

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.changeObjectActivateStatus(
				objectDefinition.name
			);

			await page.goto('/');

			await productMenuPage.openProductMenuIfClosed();

			await productMenuPage.contentAndDataButton.click();

			await expect(
				page.getByRole('menuitem', {
					name: objectDefinition.pluralLabel['en_US'],
				})
			).toBeVisible();
		}
	);
});

test.describe('Manage object definitions through Page Templates', () => {
	test(
		'verify that the Object visibility in Collection Providers changes according to activation status',
		{tag: '@LPS-139005'},
		async ({
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

			await collectionsPage.goto(site.friendlyUrlPath);

			await page.goto(
				(await page
					.getByRole('link', {name: 'Collection Providers'})
					.getAttribute('href')) +
					'&_com_liferay_asset_list_web_portlet_AssetListPortlet_delta=200'
			);

			await expect(
				page.getByText(objectDefinition.name).first()
			).toBeVisible();

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.changeObjectActivateStatus(
				objectDefinition.name
			);

			await collectionsPage.goto(site.friendlyUrlPath);

			await page.goto(
				(await page
					.getByRole('link', {name: 'Collection Providers'})
					.getAttribute('href')) +
					'&_com_liferay_asset_list_web_portlet_AssetListPortlet_delta=200'
			);

			await expect(
				page.getByText(objectDefinition.name).first()
			).toBeHidden();

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.changeObjectActivateStatus(
				objectDefinition.name
			);

			await collectionsPage.goto(site.friendlyUrlPath);

			await page.goto(
				(await page
					.getByRole('link', {name: 'Collection Providers'})
					.getAttribute('href')) +
					'&_com_liferay_asset_list_web_portlet_AssetListPortlet_delta=200'
			);

			await expect(
				page.getByText(objectDefinition.name).first()
			).toBeVisible();
		}
	);

	test(
		'verify that the Object visibility in the Page Template subtype changes according to activation status',
		{tag: '@LPS-139005'},
		async ({
			apiHelpers,
			displayPageTemplatesPage,
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

			await displayPageTemplatesPage.goto();

			await displayPageTemplatesPage.newButton.click();

			await page
				.getByRole('menuitem', {name: 'Display Page Template'})
				.click();

			await page.getByRole('button', {name: 'Blank'}).click();

			await expect(page.getByLabel('Content Type')).toContainText(
				objectDefinition.label['en_US']
			);

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.changeObjectActivateStatus(
				objectDefinition.name
			);

			await displayPageTemplatesPage.goto();

			await displayPageTemplatesPage.newButton.click();

			await page
				.getByRole('menuitem', {name: 'Display Page Template'})
				.click();

			await page.getByRole('button', {name: 'Blank'}).click();

			await expect(page.getByLabel('Content Type')).not.toContainText(
				objectDefinition.label['en_US']
			);

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.changeObjectActivateStatus(
				objectDefinition.name
			);

			await displayPageTemplatesPage.goto();

			await page.reload();

			await displayPageTemplatesPage.newButton.click();

			await page
				.getByRole('menuitem', {name: 'Display Page Template'})
				.click();

			await page.getByRole('button', {name: 'Blank'}).click();

			await expect(page.getByLabel('Content Type')).toContainText(
				objectDefinition.label['en_US']
			);
		}
	);

	test(
		'verify that the Object entry visibility in Page fragments changes according to activation status',
		{tag: '@LPS-139005'},
		async ({
			apiHelpers,
			page,
			pageEditorPage,
			site,
			viewObjectDefinitionsPage,
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

			const applicationName =
				'c/' + objectDefinition.name.toLowerCase() + 's';

			await apiHelpers.objectEntry.postObjectEntry(
				{textField: 'Test 1'},
				applicationName
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

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.changeObjectActivateStatus(
				objectDefinition.name
			);

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await page.getByText('Heading Example', {exact: true}).dblclick();

			await page.getByLabel('Select Item').click();

			const selectFrame = page.frameLocator('iframe[title="Select"]');

			await expect(
				selectFrame.getByRole('menuitem', {
					name: objectDefinition.name,
				})
			).toBeHidden();

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.changeObjectActivateStatus(
				objectDefinition.name
			);

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			await page.getByText('Heading Example', {exact: true}).dblclick();

			await page.getByLabel('Select Item').click();

			await expect(
				selectFrame.getByRole('menuitem', {
					name: objectDefinition.name,
				})
			).toBeVisible();

			await selectFrame
				.getByRole('menuitem', {name: objectDefinition.name})
				.click();

			await selectFrame.getByText('Test 1').click();

			await page
				.getByLabel('Field')
				.selectOption('ObjectField_textField');

			await pageEditorPage.publishPage();

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(page.getByText('Test 1')).toBeVisible();

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.changeObjectActivateStatus(
				objectDefinition.name
			);

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(page.getByText('Test 1')).toBeHidden();
			await expect(page.getByText('Heading Example')).toBeVisible();

			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.changeObjectActivateStatus(
				objectDefinition.name
			);

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(page.getByText('Test 1')).toBeVisible();
		}
	);
});

cmsTest.describe('Manage enableFormContainer configuration', () => {
	cmsTest(
		'can see object definition on form container list when configuration is enabled and cannot when it is disabled',
		{tag: ['@LPD-64249']},
		async ({apiHelpers, page, pageEditorPage, site}) => {
			const objectDefinitionAPIClient =
				await apiHelpers.buildRestClient(ObjectDefinitionAPI);

			const objectField = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const {body: objectDefinition} =
				await objectDefinitionAPIClient.postObjectDefinition({
					enableFormContainer: true,
					label: {
						en_US: 'ObjectDefinitionLabel' + getRandomInt(),
					},
					name: 'ObjectDefinitionName' + getRandomInt(),
					objectFields: objectField,
					pluralLabel: {
						en_US: 'ObjectDefinitionsLabel' + getRandomInt(),
					},
					scope: 'company',
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getFormContainerDefinition({
						id: getRandomString(),
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			const formContainerSelect = page.getByLabel('Content Type');

			await formContainerSelect.selectOption(
				objectDefinition.label['en_US']
			);

			await expect(
				page.frameLocator('iframe').getByRole('cell', {
					exact: true,
					name: objectField[0].label['en_US'],
				})
			).toBeVisible();

			await page.getByRole('button', {name: 'Cancel'}).click();

			await objectDefinitionAPIClient.patchObjectDefinition(
				objectDefinition.id,
				{
					enableFormContainer: false,
				}
			);

			await page.reload();

			await expect(
				formContainerSelect.getByRole('option', {
					name: objectDefinition.name,
				})
			).not.toBeAttached();
		}
	);
});
