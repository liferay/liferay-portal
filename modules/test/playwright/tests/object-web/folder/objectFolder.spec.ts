/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectFolder,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	loginTest(),
	objectPagesTest
);

test.describe('manage object definitions through model builder', () => {
	test('can edit object folder label and ERC by Model Builder', async ({
		apiHelpers,
		modalEditObjectFolderPage,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
	}) => {
		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		await modelBuilderDiagramPage.goto({
			objectFolderName: objectFolder.name,
		});

		await modelBuilderDiagramPage.editObjectFolderDetailsButton.click();

		const newObjectFolderLabel = 'objectFolderLabel' + getRandomInt();
		const newObjectFolderERC = 'objectFolderERC' + getRandomInt();

		await modalEditObjectFolderPage.editObjectFolderDetails(
			newObjectFolderERC,
			newObjectFolderLabel
		);

		expect(
			modelBuilderDiagramPage.getObjectFolderLabelHeaderLocator(
				newObjectFolderLabel
			)
		).toBeVisible();

		expect(modelBuilderLeftSidebarPage.selectedObjectFolder).toBeVisible();

		expect(
			modelBuilderDiagramPage.getObjectFolderERCHeaderLocator(
				newObjectFolderERC
			)
		).toBeVisible();
	});

	test('ensure the back url button redirects to the correct folder', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
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

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.viewInModelBuilderButton.click();

		const otherObjectFolderLocator =
			modelBuilderLeftSidebarPage.getOtherObjectFolderLocator(
				objectFolder.label['en_US']
			);

		await otherObjectFolderLocator.hover();

		await otherObjectFolderLocator
			.getByRole('button', {exact: true, name: 'Go to Folder'})
			.click();

		await modelBuilderDiagramPage.toggleSidebarsButton.click();

		await page.getByTitle('User Profile Menu').click();

		await page
			.getByRole('menuitem', {
				name: 'Notifications',
			})
			.click();

		await expect(
			page.getByRole('heading', {level: 1, name: 'Notifications'})
		).toBeVisible();

		await page.getByTitle('Back', {exact: true}).click();

		await expect(
			viewObjectDefinitionsPage.getObjectFolderCardHeaderERC(
				objectFolder.externalReferenceCode
			)
		).toBeVisible();

		await viewObjectDefinitionsPage.frontendDataSetEntries
			.filter({
				hasText: objectDefinition.label['en_US'],
			})
			.click();

		await page.getByTitle('Back', {exact: true}).click();

		await expect(
			viewObjectDefinitionsPage.getObjectFolderCardHeaderERC(
				objectFolder.externalReferenceCode
			)
		).toBeVisible();
	});

	test('navigate between object folders on model builder page', async ({
		apiHelpers,
		modelBuilderDiagramPage,
		modelBuilderLeftSidebarPage,
		page,
	}) => {
		const objectFolders: ObjectFolder[] = await Promise.all(
			Array.apply(null, Array(3)).map(async () => {
				const objectFolder =
					await apiHelpers.objectAdmin.postRandomObjectFolder();

				apiHelpers.data.push({
					id: objectFolder.id,
					type: 'objectFolder',
				});

				return objectFolder;
			})
		);

		await modelBuilderDiagramPage.goto({objectFolderName: 'Default'});

		for (const objectFolder of objectFolders) {
			await expect(
				modelBuilderLeftSidebarPage.otherObjectFolders
			).toBeVisible();

			const otherObjectFolderLocator =
				modelBuilderLeftSidebarPage.getOtherObjectFolderLocator(
					objectFolder.label['en_US']
				);

			await otherObjectFolderLocator.hover();

			await otherObjectFolderLocator
				.getByRole('button', {name: 'Go to Folder'})
				.click();

			await expect(otherObjectFolderLocator).toBeHidden();

			await expect(
				modelBuilderDiagramPage.getObjectFolderLabelHeaderLocator(
					objectFolder.label['en_US']
				)
			).toBeVisible();

			await page.waitForTimeout(1000);
		}
	});
});

test.describe('manage object definitions through view object definitions', () => {
	test('can edit object folder label and ERC in the view object definitions page', async ({
		apiHelpers,
		modalEditObjectFolderPage,
		viewObjectDefinitionsPage,
	}) => {
		const objectFolder =
			await apiHelpers.objectAdmin.postRandomObjectFolder();

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.openObjectFolder(
			objectFolder.label['en_US']
		);

		await viewObjectDefinitionsPage.objectFolderActions.click();

		await viewObjectDefinitionsPage.objectFolderEditLabelAndERCOption.click();

		const newObjectFolderLabel = 'objectFolderLabel' + getRandomInt();
		const newObjectFolderERC = 'objectFolderERC' + getRandomInt();

		await modalEditObjectFolderPage.editObjectFolderDetails(
			newObjectFolderERC,
			newObjectFolderLabel
		);

		expect(
			viewObjectDefinitionsPage.objectFolders.getByText(
				newObjectFolderLabel
			)
		).toBeVisible();

		expect(
			viewObjectDefinitionsPage.getObjectFolderCardHeaderLabel(
				newObjectFolderLabel
			)
		).toBeVisible();

		expect(
			viewObjectDefinitionsPage.getObjectFolderCardHeaderERC(
				newObjectFolderERC
			)
		).toBeVisible();
	});

	test('created object folders are on the left side bar', async ({
		apiHelpers,
		viewObjectDefinitionsPage,
	}) => {
		await viewObjectDefinitionsPage.goto();

		const objectFolderExternalReferenceCode =
			'objectFolder' + getRandomInt();

		const objectFolder = await viewObjectDefinitionsPage.createObjectFolder(
			objectFolderExternalReferenceCode
		);

		apiHelpers.data.push({id: objectFolder.id, type: 'objectFolder'});

		await expect(
			viewObjectDefinitionsPage.page
				.locator('li')
				.filter({hasText: objectFolder.label['en_US']})
		).toBeVisible();
	});

	test('default folder does not contains delete and edit options', async ({
		viewObjectDefinitionsPage,
	}) => {
		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.defaultObjectFolder.click();

		await viewObjectDefinitionsPage.objectFolderActions.click();

		await expect(
			viewObjectDefinitionsPage.objectFolderDeleteFolderOption
		).toBeHidden();

		await expect(
			viewObjectDefinitionsPage.objectFolderEditLabelAndERCOption
		).toBeHidden();
	});

	test('navigate between object folders on view object definitions page', async ({
		apiHelpers,
		viewObjectDefinitionsPage,
	}) => {
		const objectFolders: ObjectFolder[] = await Promise.all(
			Array.apply(null, Array(5)).map(async () => {
				const objectFolder =
					await apiHelpers.objectAdmin.postRandomObjectFolder();

				apiHelpers.data.push({
					id: objectFolder.id,
					type: 'objectFolder',
				});

				return objectFolder;
			})
		);

		await viewObjectDefinitionsPage.goto();

		for (const objectFolder of objectFolders) {
			await expect(viewObjectDefinitionsPage.objectFolders).toBeVisible();

			viewObjectDefinitionsPage.openObjectFolder(
				objectFolder.label['en_US']
			);

			expect(
				viewObjectDefinitionsPage.getObjectFolderCardHeaderLabel(
					objectFolder.label['en']
				)
			).toBeVisible();
		}
	});

	test('object definitions from a deleted folder are moved to the default folder', async ({
		apiHelpers,
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

		apiHelpers.data.push(
			{id: objectDefinition1.id, type: 'objectDefinition'},
			{id: objectDefinition2.id, type: 'objectDefinition'}
		);

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.openObjectFolder(
			objectFolder.externalReferenceCode
		);

		await viewObjectDefinitionsPage.objectFolderActions.click();

		await viewObjectDefinitionsPage.deleteObjectFolder(objectFolder.name);

		apiHelpers.data.splice(
			apiHelpers.data.findIndex(
				(object) =>
					object.id === objectFolder.id &&
					object.type === 'objectFolder'
			),
			1
		);

		await viewObjectDefinitionsPage.defaultObjectFolder.click();

		await expect(
			viewObjectDefinitionsPage.frontendDataSetEntries.filter({
				hasText: objectDefinition1.label['en_US'],
			})
		).toBeVisible();

		await expect(
			viewObjectDefinitionsPage.frontendDataSetEntries.filter({
				hasText: objectDefinition2.label['en_US'],
			})
		).toBeVisible();
	});

	test(
		'object definitions can be moved to the current folder when the user is in the model builder view',
		{tag: '@LPS-185681'},
		async ({
			apiHelpers,
			modelBuilderDiagramPage,
			modelBuilderLeftSidebarPage,
			page,
			viewObjectDefinitionsPage: _viewObjectDefinitionsPage,
		}) => {
			let folderA: ObjectFolder;
			let folderB: ObjectFolder;
			let objectDefinition: ObjectDefinition;

			await test.step('Create folders and object definition', async () => {
				folderA = await apiHelpers.objectAdmin.postRandomObjectFolder();

				apiHelpers.data.push({id: folderA.id, type: 'objectFolder'});

				folderB = await apiHelpers.objectAdmin.postRandomObjectFolder();

				apiHelpers.data.push({id: folderB.id, type: 'objectFolder'});

				objectDefinition =
					await apiHelpers.objectAdmin.postRandomObjectDefinition({
						objectFolderExternalReferenceCode:
							folderA.externalReferenceCode,
						status: {code: 0},
					});

				apiHelpers.data.push({
					id: objectDefinition.id,
					type: 'objectDefinition',
				});
			});

			await test.step('Navigate to Folder B via Model Builder sidebar', async () => {

				// Navigate to Folder A in Model Builder

				await modelBuilderDiagramPage.goto({
					objectFolderName: folderA.name,
				});

				// Navigate to Folder B via left sidebar

				const folderBLocator =
					modelBuilderLeftSidebarPage.getOtherObjectFolderLocator(
						folderB.label['en_US']
					);

				await folderBLocator.hover();

				await folderBLocator.getByTitle('Go to Folder').click();
			});

			await test.step('Move object definition to current folder', async () => {
				await modelBuilderLeftSidebarPage.clickObjectDefinitionActionsButtonInSidebar(
					objectDefinition.label['en_US']
				);

				await page
					.getByRole('menuitem', {name: 'Move to Current Folder'})
					.click();

				await expect(
					page.locator('#ToastAlertContainer .alert-success')
				).toBeVisible();
			});

			await test.step('Check that the object definition is visible in Folder B sidebar', async () => {
				await expect(
					modelBuilderLeftSidebarPage.sidebarItems.filter({
						hasText: objectDefinition.label['en_US'],
					})
				).toBeVisible();
			});
		}
	);
});
