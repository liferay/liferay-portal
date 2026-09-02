/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {editObjectDefinitionPagesTest} from '../../../fixtures/editObjectDefinitionPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {listTypeDefinitionsPagesTest} from '../../../fixtures/listTypeDefinitionsPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {notificationPagesTest} from '../../../fixtures/notificationPagesTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {generateObjectEntryValues} from '../utils/generateObjectEntry';

const test = mergeTests(
	loginTest(),
	apiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
	}),
	editObjectDefinitionPagesTest,
	listTypeDefinitionsPagesTest,
	notificationPagesTest,
	objectPagesTest
);

test.describe.serial('View Custom Object1 after upgrade', () => {
	test('Can view and edit object action after upgrade', async ({
		editObjectActionPage,
		page,
		viewObjectActionsPage,
	}) => {
		await test.step('View object action', async () => {
			await viewObjectActionsPage.goto('Custom Object1');

			await page.getByRole('link', {name: 'Custom Action'}).click();

			await editObjectActionPage.actionBuilderTab.click();

			await expect(editObjectActionPage.expressionInput).toHaveValue(
				"customFieldText == 'Notification Test'"
			);
		});

		await test.step('Edit object action', async () => {
			await editObjectActionPage.expressionInput.fill(
				"customFieldText == 'New Expression'"
			);

			await editObjectActionPage.basicInfoTab.click();

			await editObjectActionPage.actionLabelInput.fill(
				'Custom Action Updated'
			);

			await editObjectActionPage.saveButton.click();

			await page.locator('.fds-side-panel').waitFor({state: 'hidden'});

			await expect(
				page.getByRole('link', {name: 'Custom Action Updated'})
			).toBeVisible();
		});
	});

	test('Can view and edit object definition after upgrade', async ({
		editObjectDetailsPage,
	}) => {
		await test.step('View object definition', async () => {
			await editObjectDetailsPage.goto('Custom Object1');

			await expect(
				editObjectDetailsPage.allowDraftToggle
			).not.toBeChecked();
			await expect(
				editObjectDetailsPage.entryTitleFieldCombobox
			).toHaveText('Custom Field Text');
			await expect(editObjectDetailsPage.pluralLabelInput).toHaveValue(
				'Custom Objects1'
			);
			await expect(editObjectDetailsPage.scopeCombobox).toHaveText(
				'Site'
			);
			await expect(editObjectDetailsPage.showWidgetToggle).toBeChecked();
		});

		await test.step('Edit object definition details', async () => {
			await editObjectDetailsPage.waitForDetailsFormLoaded();

			await editObjectDetailsPage.labelInput.fill(
				'Custom Object1 Updated'
			);
			await editObjectDetailsPage.pluralLabelInput.fill(
				'Custom Objects1 Updated'
			);

			const {reload} =
				await editObjectDetailsPage.saveObjectDefinitionReturningReload();

			await reload;

			await expect(editObjectDetailsPage.labelInput).toHaveValue(
				'Custom Object1 Updated'
			);
			await expect(editObjectDetailsPage.pluralLabelInput).toHaveValue(
				'Custom Objects1 Updated'
			);
		});
	});

	test('Can view and edit object entry after upgrade', async ({
		apiHelpers,
		page,
		queuePage,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.getObjectDefinitionByName(
				'CustomObject1'
			);

		await test.step('View object entry', async () => {
			await viewObjectEntriesPage.goto(objectDefinition.className);

			await page
				.getByRole('row')
				.filter({hasText: 'Notification Test'})
				.getByRole('link')
				.click();

			await expect(page.getByLabel('Custom Field Text')).toHaveValue(
				'Notification Test'
			);

			await page.getByRole('link', {name: 'Account Tab'}).click();

			await expect(page.getByText('Account Test')).toBeVisible();

			await page.getByRole('link', {name: 'Custom Object2 Tab'}).click();

			await expect(page.getByText('Document_1.jpg')).toBeVisible();
		});

		await test.step('Add new object entry', async () => {
			await viewObjectEntriesPage.goto(objectDefinition.className);

			await viewObjectEntriesPage.clickAddObjectEntry(
				'Custom Object1 Updated'
			);

			await viewObjectEntriesPage.fillObjectEntry({
				objectFieldLabel: 'Custom Field Text',
				objectFieldValue: 'New Expression',
			});

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(viewObjectEntriesPage.successMessage).toBeVisible();
		});

		await test.step('Check new notification in the queue', async () => {
			await page.goto(
				`/group/guest${PORTLET_URLS.notificationQueueEntries}`
			);

			await expect(queuePage.pageTitle).toBeVisible();

			await expect(
				page.getByRole('row').filter({hasText: 'CustomObject1'})
			).toHaveCount(2);
		});
	});

	test('Can view and edit object layout after upgrade', async ({
		objectLayoutsPage,
		page,
	}) => {
		await test.step('View object layout', async () => {
			await objectLayoutsPage.goto('Custom Object1 Updated');

			await page.getByRole('link', {name: 'Custom Layout'}).click();

			await objectLayoutsPage.layoutTab.click();

			for (const objectLayoutFieldName of [
				'Custom Field Text',
				'MTM - Custom Object',
				'OTM - System Object',
			]) {
				await expect(
					objectLayoutsPage.iframeLocator.getByText(
						objectLayoutFieldName
					)
				).toBeVisible();
			}

			for (const objectLayoutTabName of [
				'Account Tab',
				'Custom Object2 Tab',
				'Field Tab',
			]) {
				await expect(
					objectLayoutsPage.iframeLocator.getByRole('heading', {
						name: objectLayoutTabName,
					})
				).toBeVisible();
			}
		});

		await test.step('Edit object layout', async () => {
			await objectLayoutsPage.goto('Custom Object1 Updated');

			await page.getByRole('link', {name: 'Custom Layout'}).click();

			await objectLayoutsPage.iframeLocator
				.locator('input[name="name"]')
				.fill('Custom Layout Updated');

			await objectLayoutsPage.saveUpdateLayoutButton.click();

			await page.locator('.fds-side-panel').waitFor({state: 'hidden'});

			await expect(
				page.getByRole('link', {name: 'Custom Layout Updated'})
			).toBeVisible();
		});
	});

	test('Can view and edit object relationship after upgrade', async ({
		objectRelationshipsPage,
		page,
	}) => {
		const objectRelationships = [
			{
				hierarchy: 'Parent',
				label: 'MTM - Custom Object',
				relatedObject: 'CustomObject2',
				type: 'manyToMany',
			},
			{
				hierarchy: 'Parent',
				label: 'OTM - System Object',
				relatedObject: 'AccountEntry',
				type: 'oneToMany',
			},
		];

		await test.step('View object relationships', async () => {
			await objectRelationshipsPage.goto('Custom Object1 Updated');

			for (const objectRelationship of objectRelationships) {
				const row = page
					.getByRole('row')
					.filter({hasText: objectRelationship.label});

				await expect(row).toContainText(
					objectRelationship.relatedObject
				);
				await expect(row).toContainText(objectRelationship.type);
				await expect(row).toContainText(objectRelationship.hierarchy);
			}
		});

		await test.step('Edit object relationships', async () => {
			for (const objectRelationship of objectRelationships) {
				await page
					.getByRole('link', {name: objectRelationship.label})
					.click();

				await objectRelationshipsPage.labelInput.fill(
					`${objectRelationship.label} Updated`
				);

				await objectRelationshipsPage.saveObjectRelationshipButton.click();

				await page
					.locator('.fds-side-panel')
					.waitFor({state: 'hidden'});
			}

			for (const objectRelationship of objectRelationships) {
				await expect(
					page.getByRole('link', {
						name: `${objectRelationship.label} Updated`,
					})
				).toBeVisible();
			}
		});
	});

	test('Delete object definition after upgrade', async ({
		objectRelationshipsPage,
		viewObjectDefinitionsPage,
	}) => {
		await test.step('Delete object relationships', async () => {
			await objectRelationshipsPage.goto('Custom Object1 Updated');

			await objectRelationshipsPage.deleteObjectRelationship(
				'MTM - Custom Object Updated',
				'mTMCustomObject'
			);
			await objectRelationshipsPage.deleteObjectRelationship(
				'OTM - System Object Updated',
				'oTMSystemObject'
			);
		});

		await test.step('Delete object definition', async () => {
			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.deleteObjectDefinition(
				'Custom Object1 Updated',
				'CustomObject1'
			);

			await expect(
				viewObjectDefinitionsPage.frontendDataSetEntries.filter({
					hasText: 'Custom Object1 Updated',
				})
			).toBeHidden();
		});
	});
});

test.describe.serial('View Custom Object3 after upgrade', () => {
	test('Can view and edit object entry after upgrade', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.getObjectDefinitionByName(
				'CustomObject3'
			);

		await test.step('View object entry', async () => {
			await viewObjectEntriesPage.goto(objectDefinition.className);

			const objectEntryValues = [
				'Yes',
				'Jan 1, 1970',
				'5.5',
				'123456789',
				'1234567890123456',
				'Lorem ipsum dolor sit amet, an senserit appellantur vis, ea usu facilis admodum facilisi. Doctus admodum maiorum duo ex, dignissim reformidans ut mei, at his cibo eloquentiam. Has ei unum legimus constituto, stet labore has et. No audire pertinacia pri, vix possit vocibus facilisi eu.',
				'Picklist Item 1',
				'3.1415',
				'Rich Text Entry',
			];

			for (const objectEntryValue of objectEntryValues) {
				await expect(
					page
						.locator('td')
						.getByText(objectEntryValue, {exact: true})
				).toBeVisible();
			}
		});

		await test.step('Edit object entry', async () => {
			await page
				.getByRole('row')
				.filter({hasText: 'Rich Text Entry'})
				.getByRole('link')
				.click();

			const {items: objectFieldsResponse} =
				await apiHelpers.objectAdmin.getAllObjectDefinitionsFields(
					objectDefinition.id
				);

			const customObjectFields = objectFieldsResponse.filter(
				(objectField: {system: boolean}) => !objectField.system
			);

			const {objectEntry} = await generateObjectEntryValues({
				listTypeEntries: [
					'Picklist Item 1',
					'Picklist Item 2',
					'Picklist Item 3',
				],
				objectEntryFormat: 'UI',
				objectFields: customObjectFields,
			});

			const objectEntryValues =
				await viewObjectEntriesPage.fillObjectFields({
					objectEntry,
					objectFields: customObjectFields,
				});

			await viewObjectEntriesPage.saveObjectEntryButton.click();

			await expect(viewObjectEntriesPage.successMessage).toBeVisible();

			await viewObjectEntriesPage.backButton.click();

			for (const {entry} of objectEntryValues) {
				await expect(
					page.locator('td').getByText(entry, {exact: true})
				).toBeVisible();
			}
		});
	});

	test('Can view and edit object field after upgrade', async ({
		apiHelpers,
		objectFieldsPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = [
			{label: 'Custom Field Boolean', type: 'Boolean'},
			{label: 'Custom Field Date', type: 'Date'},
			{label: 'Custom Field Decimal', type: 'Decimal'},
			{label: 'Custom Field Integer', type: 'Integer'},
			{label: 'Custom Field Long Integer', type: 'Long Integer'},
			{label: 'Custom Field Long Text', type: 'Long Text'},
			{label: 'Custom Field Picklist', type: 'Picklist'},
			{
				label: 'Custom Field Precision Decimal',
				type: 'Precision Decimal',
			},
			{label: 'Custom Field Rich Text', type: 'Rich Text'},
		];

		await test.step('View object fields', async () => {
			await objectFieldsPage.goto('Custom Object3');

			for (const objectField of objectFields) {
				await expect(
					page
						.getByRole('row')
						.filter({hasText: objectField.label})
						.filter({hasText: objectField.type})
				).toBeVisible();
			}
		});

		await test.step('Edit object fields', async () => {
			for (const objectField of objectFields) {
				await objectFieldsPage.openObjectField(objectField.label);

				await objectFieldsPage.iframeLocator
					.getByLabel('LabelMandatory')
					.fill(`${objectField.label} Updated`);

				await objectFieldsPage.saveObjectField();
			}

			const objectDefinition =
				await apiHelpers.objectAdmin.getObjectDefinitionByName(
					'CustomObject3'
				);

			await viewObjectEntriesPage.goto(objectDefinition.className);

			for (const objectField of objectFields) {
				await expect(
					page.getByRole('columnheader', {
						name: `${objectField.label} Updated`,
					})
				).toBeVisible();
			}
		});
	});

	test('Delete object entry after upgrade', async ({
		apiHelpers,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.getObjectDefinitionByName(
				'CustomObject3'
			);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.frontendDatasetActions.last().click();

		await viewObjectEntriesPage.frontendDatasetDeleteAction.click();

		await viewObjectEntriesPage.deletionConfirmationModal
			.getByRole('button', {name: 'Delete'})
			.click();

		await expect(page.getByText('No Results Found')).toBeVisible();
	});
});

test.describe.serial('View Picklist after upgrade', () => {
	test('Can view and edit picklist after upgrade', async ({
		listTypeDefinitionPage,
		page,
	}) => {
		await test.step('View picklist', async () => {
			await listTypeDefinitionPage.goto();

			await expect(
				page.getByRole('row', {name: 'Custom Picklist'})
			).toBeVisible();

			await listTypeDefinitionPage
				.getPicklistLinkLocator('Custom Picklist')
				.click();

			for (const itemNumber of ['1', '2', '3']) {
				await expect(
					listTypeDefinitionPage.frameLocator.getByRole('link', {
						name: `Picklist Item ${itemNumber}`,
					})
				).toBeVisible();
			}
		});

		await test.step('Edit picklist', async () => {
			await listTypeDefinitionPage.sidebarNameInput.fill(
				'Custom Picklist Updated'
			);

			await listTypeDefinitionPage.frameLocator
				.getByLabel('External Reference Code')
				.fill('ERC Updated');

			await listTypeDefinitionPage.sidebarSaveButton.click();

			await page.locator('.fds-side-panel').waitFor({state: 'hidden'});

			await expect(
				page.getByRole('row', {name: 'Custom Picklist Updated'})
			).toBeVisible();
		});

		await test.step('Edit picklist items', async () => {
			await listTypeDefinitionPage
				.getPicklistLinkLocator('Custom Picklist Updated')
				.click();

			for (const itemNumber of ['1', '2', '3']) {
				const itemName = `Picklist Item ${itemNumber}`;

				await listTypeDefinitionPage
					.getPicklistItemLinkLocator(itemName)
					.click();

				await listTypeDefinitionPage.modalNameInput.fill(
					`${itemName} Updated`
				);

				await listTypeDefinitionPage.modalSaveButton.click();

				await listTypeDefinitionPage.modalNameInput.waitFor({
					state: 'hidden',
				});

				await expect(
					listTypeDefinitionPage.frameLocator.getByRole('link', {
						name: `${itemName} Updated`,
					})
				).toBeVisible();
			}
		});

		await test.step('Add new item to picklist', async () => {
			await listTypeDefinitionPage.goto();

			await listTypeDefinitionPage.addPicklistItem(
				'Custom Picklist Updated',
				'New Picklist Item'
			);

			await expect(
				listTypeDefinitionPage.frameLocator.getByRole('link', {
					name: 'New Picklist Item',
				})
			).toBeVisible();
		});
	});

	test('Delete picklist item after upgrade', async ({
		listTypeDefinitionPage,
	}) => {
		await listTypeDefinitionPage.goto();

		await listTypeDefinitionPage
			.getPicklistLinkLocator('Custom Picklist Updated')
			.click();

		await listTypeDefinitionPage.frameLocator
			.getByRole('row', {name: 'Picklist Item 2 Updated'})
			.getByRole('button', {name: 'Actions'})
			.click();

		await listTypeDefinitionPage.deleteActionMenuOption.click();

		await listTypeDefinitionPage.deleteButton.click();

		await expect(
			listTypeDefinitionPage.frameLocator.getByRole('link', {
				name: 'Picklist Item 2 Updated',
			})
		).toBeHidden();
	});

	test('Delete picklist after upgrade', async ({
		listTypeDefinitionPage,
		page,
		viewObjectDefinitionsPage,
	}) => {
		await test.step('Cannot delete picklist used by an object definition', async () => {
			await listTypeDefinitionPage.goto();

			await page
				.getByRole('row', {name: 'Custom Picklist Updated'})
				.getByRole('button', {name: 'Actions'})
				.click();

			await expect(
				page.getByRole('menuitem', {name: 'Delete'})
			).toBeHidden();
		});

		await test.step('Delete CustomObject3', async () => {
			await viewObjectDefinitionsPage.goto();

			await viewObjectDefinitionsPage.deleteObjectDefinition(
				'Custom Object3',
				'CustomObject3'
			);

			await expect(
				viewObjectDefinitionsPage.frontendDataSetEntries.filter({
					hasText: 'Custom Object3',
				})
			).toBeHidden();
		});

		await test.step('Delete picklist', async () => {
			await listTypeDefinitionPage.goto();

			await page
				.getByRole('row', {name: 'Custom Picklist Updated'})
				.getByRole('button', {name: 'Actions'})
				.click();

			await page.getByRole('menuitem', {name: 'Delete'}).click();

			await expect(
				page.getByRole('row', {name: 'Custom Picklist Updated'})
			).toBeHidden();
		});
	});
});
