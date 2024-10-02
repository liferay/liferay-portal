/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {liferayConfig} from '../../../../liferay.config';
import getRandomString from '../../../../utils/getRandomString';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import {EN_BASE_URL, ES_BASE_URL, PT_BASE_URL} from '../../utils/constants';
import saveFromModal from '../../utils/saveFromModal';
import {dataSetManagerSetupTest} from './fixtures/dataSetManagerSetupTest';
import {visualizationModesPageTest} from './fixtures/visualizationModesPageTest';
import clickActionInRow from '../../utils/clickActionInRow';

export const test = mergeTests(
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-164563': true,
	}),
	visualizationModesPageTest,
	loginTest(),
	dataSetManagerSetupTest
);

const LABEL_COLUMN_INDEX = 2;
const RENDERER_COLUMN_INDEX = 4;
const SORTABLE_COLUMN_INDEX = 5;
const TYPE_COLUMN_INDEX = 3;

let dataSetERC: string;

const dataSetLabel: string = getRandomString();

test.beforeEach(async ({dataSetManagerApiHelpers}) => {
	dataSetERC = getRandomString();

	await dataSetManagerApiHelpers.createDataSet({
		erc: dataSetERC,
		label: dataSetLabel,
	});
});

test.afterEach(async ({dataSetManagerApiHelpers}) => {
	await dataSetManagerApiHelpers.deleteDataSet({
		erc: dataSetERC,
	});
});

test.describe('Visualization Modes in Data Set Manager', () => {
	test('Configure cards visualization mode @LPD-10735', async ({
		page,
		visualizationModesPage,
	}) => {
		await test.step('Navigate to cards visualization mode page', async () => {
			await visualizationModesPage.goto({
				dataSetLabel,
			});

			await visualizationModesPage.selectTab('Cards');

			await expect(
				visualizationModesPage.cardsVisualizationModeContainer
			).toBeVisible();
		});

		await test.step('Check if cards sections are correct', async () => {
			await expect(
				visualizationModesPage.cardsVisualizationModeContainer.locator(
					'.cards-section-label'
				)
			).toHaveText([
				'Card Element',
				'Title',
				'Description',
				'Image',
				'Symbol',
			]);
		});

		await test.step('Assign a field to title section', async () => {
			const fieldName = 'name';
			const sectionLabel = 'Title';

			const container =
				visualizationModesPage.cardsVisualizationModeContainer;

			await visualizationModesPage.openAssignDataSourceFieldsModal({
				container,
				sectionLabel,
			});

			await visualizationModesPage.selectField({fieldName});

			await saveFromModal({
				page,
			});

			const assignedFieldLocator =
				await visualizationModesPage.getAssignedFieldLocator({
					container,
					sectionLabel,
				});

			await expect(assignedFieldLocator).toHaveText(fieldName);
		});

		await test.step('Edit field to title section', async () => {
			const newFieldName = 'rendererType';
			const oldFieldName = 'name';
			const sectionLabel = 'Title';

			const container =
				visualizationModesPage.cardsVisualizationModeContainer;

			await visualizationModesPage.openChangeDataSourceFieldsModal({
				container,
				sectionLabel,
			});

			const oldCheckbox =
				visualizationModesPage.getFieldCheckboxByLabel(oldFieldName);

			await expect(oldCheckbox).toBeChecked();

			await visualizationModesPage.selectField({fieldName: newFieldName});

			await expect(oldCheckbox).not.toBeChecked();

			await saveFromModal({
				page,
			});

			const assignedFieldLocator =
				await visualizationModesPage.getAssignedFieldLocator({
					container,
					sectionLabel,
				});

			await expect(assignedFieldLocator).toHaveText(newFieldName);
		});

		await test.step('Assign a field typing the name manually to Description section @LPD-25242', async () => {
			const fieldName = 'name';
			const sectionLabel = 'Description';

			const container =
				visualizationModesPage.cardsVisualizationModeContainer;

			await visualizationModesPage.openAssignCustomFieldModal({
				container,
				sectionLabel,
			});

			await visualizationModesPage.addCustomFieldInput.waitFor();

			await visualizationModesPage.addCustomFieldInput.fill(fieldName);

			await saveFromModal({
				page,
				saveText: 'Add',
			});

			const assignedFieldLocator =
				await visualizationModesPage.getAssignedFieldLocator({
					container,
					sectionLabel,
				});

			await expect(assignedFieldLocator).toHaveText(fieldName);
		});

		await test.step('Edit field to Description section @LPD-25242', async () => {
			const newFieldName = 'rendererType';
			const sectionLabel = 'Description';

			const container =
				visualizationModesPage.cardsVisualizationModeContainer;

			await visualizationModesPage.openChangeCustomFieldModal({
				container,
				sectionLabel,
			});

			await visualizationModesPage.addCustomFieldInput.waitFor();

			await visualizationModesPage.addCustomFieldInput.fill(newFieldName);

			await saveFromModal({
				page,
				saveText: 'Add',
			});

			const assignedFieldLocator =
				await visualizationModesPage.getAssignedFieldLocator({
					container,
					sectionLabel,
				});

			await expect(assignedFieldLocator).toHaveText(newFieldName);
		});
	});

	test('Configure list visualization mode @LPD-10735', async ({
		page,
		visualizationModesPage,
	}) => {
		await test.step('Navigate to list visualization mode page', async () => {
			await visualizationModesPage.goto({
				dataSetLabel,
			});

			await visualizationModesPage.selectTab('List');

			await expect(
				visualizationModesPage.listVisualizationModeContainer
			).toBeVisible();
		});

		await test.step('Check if list sections are correct', async () => {
			await expect(
				visualizationModesPage.listVisualizationModeContainer.locator(
					'.list-section-label'
				)
			).toHaveText([
				'List Element',
				'Title',
				'Description',
				'Image',
				'Symbol',
			]);
		});

		await test.step('Assign a field to title section', async () => {
			const fieldName = 'name';
			const sectionLabel = 'Title';

			const container =
				visualizationModesPage.listVisualizationModeContainer;

			await visualizationModesPage.openAssignDataSourceFieldsModal({
				container,
				sectionLabel,
			});

			await visualizationModesPage.selectField({fieldName});

			await saveFromModal({
				page,
			});

			const assignedFieldLocator =
				await visualizationModesPage.getAssignedFieldLocator({
					container,
					sectionLabel,
				});

			await expect(assignedFieldLocator).toHaveText(fieldName);
		});

		await test.step('Edit field to title section', async () => {
			const newFieldName = 'rendererType';
			const oldFieldName = 'name';
			const sectionLabel = 'Title';

			const container =
				visualizationModesPage.listVisualizationModeContainer;

			await visualizationModesPage.openChangeDataSourceFieldsModal({
				container,
				sectionLabel,
			});

			const oldCheckbox =
				visualizationModesPage.getFieldCheckboxByLabel(oldFieldName);

			await expect(oldCheckbox).toBeChecked();

			await visualizationModesPage.selectField({fieldName: newFieldName});

			await expect(oldCheckbox).not.toBeChecked();

			await saveFromModal({
				page,
			});

			const assignedFieldLocator =
				await visualizationModesPage.getAssignedFieldLocator({
					container,
					sectionLabel,
				});

			await expect(assignedFieldLocator).toHaveText(newFieldName);
		});

		await test.step('Assign a field typing the name manually to Description section @LPD-25242', async () => {
			const fieldName = 'name';
			const sectionLabel = 'Description';

			const container =
				visualizationModesPage.listVisualizationModeContainer;

			await visualizationModesPage.openAssignCustomFieldModal({
				container,
				sectionLabel,
			});

			await visualizationModesPage.addCustomFieldInput.waitFor();

			await visualizationModesPage.addCustomFieldInput.fill(fieldName);

			await saveFromModal({
				page,
				saveText: 'Add',
			});

			const assignedFieldLocator =
				await visualizationModesPage.getAssignedFieldLocator({
					container,
					sectionLabel,
				});

			await expect(assignedFieldLocator).toHaveText(fieldName);
		});

		await test.step('Edit field to Description section @LPD-25242', async () => {
			const newFieldName = 'rendererType';
			const sectionLabel = 'Description';

			const container =
				visualizationModesPage.listVisualizationModeContainer;

			await visualizationModesPage.openChangeCustomFieldModal({
				container,
				sectionLabel,
			});

			await visualizationModesPage.addCustomFieldInput.waitFor();

			await visualizationModesPage.addCustomFieldInput.fill(newFieldName);

			await saveFromModal({
				page,
				saveText: 'Add',
			});

			const assignedFieldLocator =
				await visualizationModesPage.getAssignedFieldLocator({
					container,
					sectionLabel,
				});

			await expect(assignedFieldLocator).toHaveText(newFieldName);
		});
	});

	test('Configure table visualization mode @LPD-11049', async ({
		page,
		visualizationModesPage,
	}) => {
		const sampleScalarField = 'id';
		const sampleScalarFieldName = 'label';
		const sampleObjectField = 'fdsViewFDSFieldRelationship';
		const sampleObjectChildField = 'id';

		await test.step('Navigate to table visualization mode page', async () => {
			await visualizationModesPage.goto({
				dataSetLabel,
			});

			await visualizationModesPage.selectTab('Table');

			await expect(
				visualizationModesPage.tableVisualizationModeContainer
			).toBeVisible();
		});

		await test.step('Add fields from field selection tree', async () => {
			await visualizationModesPage.openAddDataSourceFieldsModal();

			await visualizationModesPage.selectField({
				fieldName: sampleScalarField,
			});

			await visualizationModesPage.selectField({
				dataId: `${sampleObjectField}.*`,
				fieldName: sampleObjectField,
			});

			await visualizationModesPage.selectField({
				dataId: `${sampleObjectField}.${sampleObjectChildField}`,
				fieldName: sampleObjectChildField,
			});

			await saveFromModal({
				page,
			});
		});

		await test.step('Add fields from text input', async () => {
			await visualizationModesPage.openAddCustomFieldModal();

			await visualizationModesPage.addCustomFieldInput.waitFor();

			await visualizationModesPage.addCustomFieldInput.fill(
				sampleScalarFieldName
			);

			await saveFromModal({
				page,
				saveText: 'Add',
			});
		});

		await test.step('Check if field defaults are correct', async () => {
			await expect(
				visualizationModesPage
					.getRowByText(sampleScalarField)
					.locator('td')
					.nth(SORTABLE_COLUMN_INDEX)
			).toHaveText('true');

			await expect(
				visualizationModesPage
					.getRowByText(sampleScalarFieldName)
					.locator('td')
					.nth(SORTABLE_COLUMN_INDEX)
			).toHaveText('true');

			await expect(
				visualizationModesPage
					.getRowByText(`${sampleObjectField}.*`)
					.locator('td')
					.nth(SORTABLE_COLUMN_INDEX)
			).toHaveText('false');

			await expect(
				visualizationModesPage
					.getRowByText(
						`${sampleObjectField}.${sampleObjectChildField}`
					)
					.locator('td')
					.nth(SORTABLE_COLUMN_INDEX)
			).toHaveText('false');
		});

		await test.step('Edit a field', async () => {
			await clickActionInRow({
				actionName: 'Edit',
				rowName: sampleScalarField,
				visualizationModesPage,
			});

			const sortableInput =
				visualizationModesPage.page.getByLabel('Sortable');

			await expect(sortableInput).toBeInViewport();
			await expect(sortableInput).toBeEnabled();
			await expect(sortableInput).toBeChecked();

			await sortableInput.click();

			await expect(sortableInput).not.toBeChecked();

			await saveFromModal({
				page,
			});

			await expect(
				visualizationModesPage
					.getRowByText(sampleScalarField)
					.locator('td')
					.nth(SORTABLE_COLUMN_INDEX)
			).toHaveText('false');
		});

		await test.step('Check if object field has disabled sortable option', async () => {
			await clickActionInRow({
				actionName: 'Edit',
				rowName: `${sampleObjectField}.*`,
				visualizationModesPage,
			});

			const sortableLabel =
				visualizationModesPage.page.getByLabel('Sortable');

			await expect(sortableLabel).toBeInViewport();

			await expect(sortableLabel).toBeDisabled();

			await visualizationModesPage.cancelAddFieldsModal();
		});
	});

	test('Add a field and assert its added to the last position in the table and assert fields can be reordered using a keyboard', async ({
		page,
		visualizationModesPage,
	}) => {
		const SAMPLE_FIELD = 'name';
		const sampleScalarField = 'id';
		const sampleObjectField = 'fdsViewFDSFieldRelationship';
		const sampleObjectChildField = 'id';

		await test.step('Navigate to table visualization mode page', async () => {
			await visualizationModesPage.goto({
				dataSetLabel,
			});

			await visualizationModesPage.selectTab('Table');

			await expect(
				visualizationModesPage.tableVisualizationModeContainer
			).toBeVisible();
		});

		await test.step('Add fields', async () => {
			await visualizationModesPage.openAddDataSourceFieldsModal();

			await visualizationModesPage.selectField({
				fieldName: sampleScalarField,
			});

			await visualizationModesPage.selectField({
				dataId: `${sampleObjectField}.*`,
				fieldName: sampleObjectField,
			});

			await visualizationModesPage.selectField({
				dataId: `${sampleObjectField}.${sampleObjectChildField}`,
				fieldName: sampleObjectChildField,
			});

			await saveFromModal({
				page,
			});
		});

		await test.step('Add a new field', async () => {
			await visualizationModesPage.openAddDataSourceFieldsModal();

			await visualizationModesPage.selectField({
				fieldName: SAMPLE_FIELD,
			});

			await saveFromModal({
				page,
			});
		});

		await test.step('Check if field is added to the last position', async () => {
			const lastTableRow = visualizationModesPage.page.locator(
				'table.orderable-table > tbody tr:last-child'
			);

			await expect(lastTableRow.locator('td').nth(1)).toHaveText(
				SAMPLE_FIELD
			);

			await visualizationModesPage.assertTableFieldRowCount(4);
		});

		await test.step('Focus the last field', async () => {
			const lastTableRow = visualizationModesPage.page.locator(
				'table.orderable-table > tbody tr:last-child'
			);

			await expect(lastTableRow).toBeVisible();

			const firstCell = lastTableRow.locator('td > button').first();

			await expect(firstCell).toBeVisible();

			await firstCell.focus();

			await expect(firstCell).toBeFocused();
		});

		await test.step('Move the field one place up', async () => {
			await page.keyboard.press('Enter');

			await page.keyboard.press('ArrowUp');

			await page.keyboard.press('Enter');
		});

		await test.step('Assert that the field has moved one place up', async () => {
			const tableRows = visualizationModesPage.page.locator(
				'table.orderable-table > tbody tr'
			);

			const tableRowsCount = await tableRows.count();

			expect(tableRowsCount).toEqual(4);

			const expectedTexts = [
				sampleScalarField,
				`${sampleObjectField}.*`,
				SAMPLE_FIELD,
				`${sampleObjectField}.${sampleObjectChildField}`,
			];

			for (let i = 0; i < expectedTexts.length; i++) {
				const row = tableRows.nth(i);

				await expect(row).toBeVisible();

				const secondColumn = row.locator('td').nth(1);

				await expect(secondColumn).toBeVisible();

				const text = await secondColumn.innerText();

				expect(text).toBe(expectedTexts[i]);
			}
		});
	});

	test('Configure table visualization mode using search with array fields @LPD-11769, @LPS-185231, LPS-185227', async ({
		page,
		visualizationModesPage,
	}) => {
		const SAMPLE_COMPLEX_ARRAY_FIELD = 'auditEvents[]*';
		const SAMPLE_COMPLEX_ARRAY_CHILD_FIELD = 'auditEvents[]creator.name';
		const SAMPLE_SCALAR_ARRAY_FIELD = 'keywords';
		const SAMPLE_FULL_COMPLEX_FIELD = 'creator.*';
		const SAMPLE_COMPLEX_OBJECT_CHILD_FIELD = 'creator.givenName';

		await test.step('Navigate to table visualization mode page', async () => {
			await visualizationModesPage.goto({
				dataSetLabel,
			});

			await visualizationModesPage.selectTab('Table');

			await expect(
				visualizationModesPage.tableVisualizationModeContainer
			).toBeVisible();
		});

		await test.step('Add scalar array field', async () => {
			await visualizationModesPage.openAddDataSourceFieldsModal();

			await visualizationModesPage.searchAndSelectField(
				SAMPLE_SCALAR_ARRAY_FIELD
			);
			await visualizationModesPage.searchAndSelectField(
				SAMPLE_COMPLEX_ARRAY_FIELD
			);
			await visualizationModesPage.searchAndSelectField(
				SAMPLE_COMPLEX_ARRAY_CHILD_FIELD
			);
			await visualizationModesPage.searchAndSelectField(
				SAMPLE_COMPLEX_OBJECT_CHILD_FIELD
			);
			await visualizationModesPage.searchAndSelectField(
				SAMPLE_FULL_COMPLEX_FIELD
			);

			await saveFromModal({
				page,
			});
		});

		await test.step('Check if fields show the correct type', async () => {
			await expect(
				visualizationModesPage
					.getRowByText(SAMPLE_SCALAR_ARRAY_FIELD)
					.locator('td')
					.nth(TYPE_COLUMN_INDEX)
			).toHaveText('array');

			await expect(
				visualizationModesPage
					.getRowByText(SAMPLE_SCALAR_ARRAY_FIELD)
					.locator('td')
					.nth(SORTABLE_COLUMN_INDEX)
			).toHaveText('false');

			await expect(
				visualizationModesPage
					.getRowByText(SAMPLE_COMPLEX_ARRAY_FIELD)
					.locator('td')
					.nth(TYPE_COLUMN_INDEX)
			).toHaveText('array');

			await expect(
				visualizationModesPage
					.getRowByText(SAMPLE_COMPLEX_ARRAY_FIELD)
					.locator('td')
					.nth(SORTABLE_COLUMN_INDEX)
			).toHaveText('false');

			await expect(
				visualizationModesPage
					.getRowByText(SAMPLE_COMPLEX_OBJECT_CHILD_FIELD)
					.locator('td')
					.nth(TYPE_COLUMN_INDEX)
			).toHaveText('string');

			await expect(
				visualizationModesPage
					.getRowByText(SAMPLE_FULL_COMPLEX_FIELD)
					.locator('td')
					.nth(TYPE_COLUMN_INDEX)
			).toHaveText('object');
		});
	});

	test('Check cancel in table visualization mode', async ({
		page,
		visualizationModesPage,
	}) => {
		const sampleScalarField = 'id';
		const sampleObjectField = 'fdsViewFDSFieldRelationship';

		await test.step('Navigate to table visualization mode page', async () => {
			await visualizationModesPage.goto({
				dataSetLabel,
			});

			await visualizationModesPage.selectTab('Table');

			await expect(
				visualizationModesPage.tableVisualizationModeContainer
			).toBeVisible();
		});

		await test.step('Add one field, but cancel the operation @LPS-185230', async () => {
			await visualizationModesPage.openAddDataSourceFieldsModal();

			await visualizationModesPage.selectField({
				fieldName: sampleScalarField,
			});

			await visualizationModesPage.cancelAddFieldsModal();

			await visualizationModesPage.assertTableFieldRowCount(0);
		});

		await test.step('Add one field, save', async () => {
			await visualizationModesPage.openAddDataSourceFieldsModal();

			await visualizationModesPage.selectField({
				fieldName: sampleScalarField,
			});

			await saveFromModal({
				page,
			});
		});

		await test.step('Unselect selected field. Select another. Cancel @LPS-185230', async () => {
			await visualizationModesPage.openAddDataSourceFieldsModal();

			await visualizationModesPage.unSelectField({
				fieldName: sampleScalarField,
			});

			await visualizationModesPage.selectField({
				dataId: `${sampleObjectField}.*`,
				fieldName: sampleObjectField,
			});

			await visualizationModesPage.cancelAddFieldsModal();
		});

		await test.step('Check there is one field and is the one just added', async () => {
			await expect(
				visualizationModesPage
					.getRowByText(sampleScalarField)
					.locator('td')
					.nth(LABEL_COLUMN_INDEX)
			).toHaveText(sampleScalarField);

			await visualizationModesPage.assertTableFieldRowCount(1);
		});

		await test.step('Edit a field, change its label, cancel @LPS-176051 @LPS-178736 @LPS-179151', async () => {
			await clickActionInRow({
				actionName: 'Edit',
				rowName: sampleScalarField,
				visualizationModesPage,
			});

			const labelInput = visualizationModesPage.page.getByLabel('Label');

			await expect(labelInput).toBeInViewport();

			await expect(labelInput).toBeEnabled();

			await labelInput.fill('New label for field');

			await visualizationModesPage.cancelAddFieldsModal();
		});

		await test.step('Check there is one field and is the one just added', async () => {
			await expect(
				visualizationModesPage
					.getRowByText(sampleScalarField)
					.locator('td')
					.nth(LABEL_COLUMN_INDEX)
			).toHaveText(sampleScalarField);

			await visualizationModesPage.assertTableFieldRowCount(1);
		});

		await test.step('Delete a field, cancel @LPS-185500', async () => {
			await clickActionInRow({
				actionName: 'Delete',
				rowName: sampleScalarField,
				visualizationModesPage,
			});

			await visualizationModesPage.cancelAddFieldsModal();
		});

		await test.step('Check there is one field and is the one just added', async () => {
			await expect(
				visualizationModesPage
					.getRowByText(sampleScalarField)
					.locator('td')
					.nth(LABEL_COLUMN_INDEX)
			).toHaveText(sampleScalarField);

			await visualizationModesPage.assertTableFieldRowCount(1);
		});
	});

	test('Check field edition in table visualization mode @LPS-176051, @LPS-178736', async ({
		page,
		visualizationModesPage,
	}) => {
		const sampleScalarField = 'id';
		const SAMPLE_FIELD = 'name';

		await test.step('Navigate to table visualization mode page', async () => {
			await visualizationModesPage.goto({
				dataSetLabel,
			});

			await visualizationModesPage.selectTab('Table');

			await expect(
				visualizationModesPage.tableVisualizationModeContainer
			).toBeVisible();
		});

		await test.step('Add one field, save', async () => {
			await visualizationModesPage.openAddDataSourceFieldsModal();

			await visualizationModesPage.selectField({
				fieldName: sampleScalarField,
			});

			await saveFromModal({
				page,
			});
		});

		await test.step('Check there is one field and is the one just added', async () => {
			await expect(
				visualizationModesPage
					.getRowByText(sampleScalarField)
					.locator('td')
					.nth(LABEL_COLUMN_INDEX)
			).toHaveText(sampleScalarField);

			await visualizationModesPage.assertTableFieldRowCount(1);
		});

		await test.step('Add another field, save', async () => {
			await visualizationModesPage.openAddDataSourceFieldsModal();

			await visualizationModesPage.selectField({
				fieldName: SAMPLE_FIELD,
			});

			await saveFromModal({
				page,
			});
		});

		await test.step('Check there are two fields', async () => {
			await expect(
				visualizationModesPage
					.getRowByText(sampleScalarField)
					.locator('td')
					.nth(LABEL_COLUMN_INDEX)
			).toHaveText(sampleScalarField);

			await expect(
				visualizationModesPage
					.getRowByText(SAMPLE_FIELD)
					.locator('td')
					.nth(LABEL_COLUMN_INDEX)
			).toHaveText(SAMPLE_FIELD);

			await visualizationModesPage.assertTableFieldRowCount(2);
		});

		await test.step('Delete field', async () => {
			await clickActionInRow({
				actionName: 'Delete',
				rowName: SAMPLE_FIELD,
				visualizationModesPage,
			});

			const deleteModal =
				await visualizationModesPage.page.getByRole('dialog');

			await expect(deleteModal).toContainText(
				'Are you sure you want to delete this field? It will be removed immediately. Fragments using it will be affected. This action cannot be undone.'
			);

			await deleteModal.getByRole('button', {name: 'Delete'}).click();

			const toastContainer = page.locator('.alert-container');

			await expect(toastContainer.getByText('Success')).toBeInViewport();

			await toastContainer
				.getByRole('button', {
					name: 'Close',
				})
				.click();
		});

		await test.step('Check that there is only one field', async () => {
			await expect(
				visualizationModesPage
					.getRowByText(sampleScalarField)
					.locator('td')
					.nth(LABEL_COLUMN_INDEX)
			).toHaveText(sampleScalarField);

			await visualizationModesPage.assertTableFieldRowCount(1);
		});

		await test.step('Open field edition modal, check that name field is not editable', async () => {
			await clickActionInRow({
				actionName: 'Edit',
				rowName: sampleScalarField,
				visualizationModesPage,
			});

			const editModal =
				await visualizationModesPage.page.getByRole('dialog');

			await expect(editModal.getByRole('heading')).toContainText(
				`Edit ${sampleScalarField}`
			);

			const nameInput = visualizationModesPage.page.getByLabel('Name');

			await expect(nameInput).toBeInViewport();

			await expect(nameInput).toBeDisabled();

			await visualizationModesPage.cancelAddFieldsModal();
		});

		await test.step('Open field edition modal, check that the user can change the renderer', async () => {
			await expect(
				visualizationModesPage
					.getRowByText(sampleScalarField)
					.locator('td')
					.nth(RENDERER_COLUMN_INDEX)
			).toHaveText('Default');

			await clickActionInRow({
				actionName: 'Edit',
				rowName: sampleScalarField,
				visualizationModesPage,
			});

			const rendererButton = page.getByRole('button', {name: 'Default'});
			await expect(rendererButton).toBeInViewport();

			const rendererDropdownId = await rendererButton.evaluate((node) => {
				return node.getAttribute('aria-controls');
			});
			await rendererButton.click();

			await page.locator(`#${rendererDropdownId}`).waitFor();

			const availbleRenderersCount = await page
				.locator(`#${rendererDropdownId}`)
				.getByRole('option')
				.count();
			await expect(availbleRenderersCount).toBeGreaterThanOrEqual(10);

			await page
				.locator(`#${rendererDropdownId}`)
				.getByRole('option', {name: 'Boolean'})
				.click();

			await saveFromModal({
				page,
			});

			await expect(
				visualizationModesPage
					.getRowByText(sampleScalarField)
					.locator('td')
					.nth(RENDERER_COLUMN_INDEX)
			).toHaveText('Boolean');
		});
	});

	test(
		'Check that users can translate labels in table visualization mode.',
		{tag: '@LPS-176516'},
		async ({page, visualizationModesPage}) => {
			const SAMPLE_FIELD = 'name';
			const SAMPLE_FIELD_EN_US = 'Name';
			const SAMPLE_FIELD_ES_ES = 'Nombre';
			const SAMPLE_FIELD_PT_BR = 'Nome';

			await test.step('Navigate to table visualization mode page', async () => {
				await visualizationModesPage.goto({
					dataSetLabel,
				});

				await visualizationModesPage.selectTab('Table');

				await expect(
					visualizationModesPage.tableVisualizationModeContainer
				).toBeVisible();
			});

			await test.step('Add field', async () => {
				await visualizationModesPage.openAddDataSourceFieldsModal();

				await visualizationModesPage.selectField({
					fieldName: SAMPLE_FIELD,
				});

				await saveFromModal({
					page,
				});
			});

			await test.step('Check there is one field and is the one just added', async () => {
				await expect(
					visualizationModesPage
						.getRowByText(SAMPLE_FIELD)
						.locator('td')
						.nth(LABEL_COLUMN_INDEX)
				).toHaveText(SAMPLE_FIELD);

				await visualizationModesPage.assertTableFieldRowCount(1);
			});

			await test.step('Edit a field, change its label using the default language (en_US)', async () => {
				await clickActionInRow({
					actionName: 'Edit',
					rowName: SAMPLE_FIELD,
					visualizationModesPage,
				});

				const labelInput =
					visualizationModesPage.page.getByLabel('Label');

				await expect(labelInput).toBeInViewport();

				await expect(labelInput).toBeEnabled();

				await labelInput.fill(SAMPLE_FIELD_EN_US);

				await saveFromModal({page});
			});

			await test.step('Check there is one field and the label shows the translated value', async () => {
				await expect(
					visualizationModesPage
						.getRowByText(SAMPLE_FIELD)
						.locator('td')
						.nth(LABEL_COLUMN_INDEX)
				).toHaveText(SAMPLE_FIELD_EN_US);

				await visualizationModesPage.assertTableFieldRowCount(1);
			});

			await test.step('Edit a field, update the label using the pt_BR and es_ES languages', async () => {
				await clickActionInRow({
					actionName: 'Edit',
					rowName: SAMPLE_FIELD,
					visualizationModesPage,
				});

				const labelInput =
					visualizationModesPage.page.getByLabel('Label');

				await expect(labelInput).toBeInViewport();

				const localizationButton = await page
					.locator('.input-localized')
					.getByRole('button');
				const languageDropdownId = await localizationButton.evaluate(
					(node) => node.getAttribute('aria-controls')
				);

				await localizationButton.click();

				await page.locator(`#${languageDropdownId}`).waitFor();

				await expect(
					page
						.locator(`#${languageDropdownId}`)
						.getByRole('menuitem', {name: 'en_US'})
						.locator('.label-item')
				).toContainText('Default');

				await expect(
					page
						.locator(`#${languageDropdownId}`)
						.getByRole('menuitem', {name: 'es_ES'})
						.locator('.label-item')
				).toContainText('Untranslated');

				await expect(
					page
						.locator(`#${languageDropdownId}`)
						.getByRole('menuitem', {name: 'pt_BR'})
						.locator('.label-item')
				).toContainText('Untranslated');

				await page
					.locator(`#${languageDropdownId}`)
					.getByRole('menuitem', {name: 'pt_BR'})
					.click();

				await labelInput.fill(SAMPLE_FIELD_PT_BR);

				await localizationButton.click();

				await page.locator(`#${languageDropdownId}`).waitFor();
				await page
					.locator(`#${languageDropdownId}`)
					.getByRole('menuitem', {name: 'es_ES'})
					.click();

				await labelInput.fill(SAMPLE_FIELD_ES_ES);

				await saveFromModal({page});
			});

			await test.step('Check that the language dropdown shows the updated language as Translated', async () => {
				await clickActionInRow({
					actionName: 'Edit',
					rowName: SAMPLE_FIELD,
					visualizationModesPage,
				});

				const localizationButton = await page
					.locator('.input-localized')
					.getByRole('button');
				const languageDropdownId = await localizationButton.evaluate(
					(node) => node.getAttribute('aria-controls')
				);

				await localizationButton.click();

				await page.locator(`#${languageDropdownId}`).waitFor();

				await expect(
					page
						.locator(`#${languageDropdownId}`)
						.getByRole('menuitem', {name: 'en_US'})
						.locator('.label-item')
				).toContainText('Default');

				await expect(
					page
						.locator(`#${languageDropdownId}`)
						.getByRole('menuitem', {name: 'es_ES'})
						.locator('.label-item')
				).toContainText('Translated');

				await expect(
					page
						.locator(`#${languageDropdownId}`)
						.getByRole('menuitem', {name: 'pt_BR'})
						.locator('.label-item')
				).toContainText('Translated');

				await page
					.locator(`#${languageDropdownId}`)
					.getByRole('menuitem', {name: 'pt_BR'})
					.click();

				await page.keyboard.press('Escape');
				await visualizationModesPage.cancelAddFieldsModal();
			});

			await test.step('Confirm that the translation works when the page is loaded with es_ES locale', async () => {
				const currentUrl = page.url();
				const updatedUrl = currentUrl.replace(
					liferayConfig.environment.baseUrl,
					ES_BASE_URL
				);

				await page.goto(updatedUrl);

				await visualizationModesPage.dataSetPage.selectTab(
					'Modos de visualización'
				);

				await expect(
					visualizationModesPage
						.getRowByText(SAMPLE_FIELD)
						.locator('td')
						.nth(LABEL_COLUMN_INDEX)
				).toHaveText(SAMPLE_FIELD_ES_ES);

				await visualizationModesPage.assertTableFieldRowCount(1);
			});

			await test.step('Confirm that the translation works when the page is loaded with pt_BR locale', async () => {
				const currentUrl = page.url();
				const updatedUrl = currentUrl.replace(ES_BASE_URL, PT_BASE_URL);

				await page.goto(updatedUrl);

				await visualizationModesPage.dataSetPage.selectTab(
					'Modos de exibição'
				);

				await expect(
					visualizationModesPage
						.getRowByText(SAMPLE_FIELD)
						.locator('td')
						.nth(LABEL_COLUMN_INDEX)
				).toHaveText(SAMPLE_FIELD_PT_BR);

				await visualizationModesPage.assertTableFieldRowCount(1);
			});

			await test.step('Restore EN locale', async () => {
				await page.goto(EN_BASE_URL);
			});
		}
	);

	test('Check modal field selection allows check and uncheck fields @LPS-174141, @LPS-185228, @LPS-179282', async ({
		page,
		visualizationModesPage,
	}) => {
		const sampleScalarField = 'externalReferenceCode';
		const SAMPLE_FIELD = 'name';

		await test.step('Navigate to table visualization mode page', async () => {
			await visualizationModesPage.goto({
				dataSetLabel,
			});

			await visualizationModesPage.selectTab('Table');

			await expect(
				visualizationModesPage.tableVisualizationModeContainer
			).toBeVisible();
		});

		await test.step('Can check and uncheck fields in the field selection modal', async () => {
			await visualizationModesPage.openAddDataSourceFieldsModal();

			await visualizationModesPage.selectField({fieldName: SAMPLE_FIELD});

			const checkbox =
				visualizationModesPage.getFieldCheckboxByLabel(SAMPLE_FIELD);

			await expect(checkbox).toBeChecked();

			await visualizationModesPage.unSelectField({
				fieldName: SAMPLE_FIELD,
			});

			await expect(checkbox).not.toBeChecked();

			await saveFromModal({
				page,
			});
		});

		await test.step('Can check some fields and uncheck all selected fields using Deselect All button', async () => {
			await visualizationModesPage.openAddDataSourceFieldsModal();

			await visualizationModesPage.selectField({fieldName: SAMPLE_FIELD});

			const sampleFieldCheckbox =
				visualizationModesPage.getFieldCheckboxByLabel(SAMPLE_FIELD);

			await expect(sampleFieldCheckbox).toBeChecked();

			await visualizationModesPage.selectField({
				fieldName: sampleScalarField,
			});

			const sampleScalarFieldCheckbox =
				visualizationModesPage.getFieldCheckboxByLabel(
					sampleScalarField
				);

			await expect(sampleScalarFieldCheckbox).toBeChecked();

			await visualizationModesPage.unSelectSelectedFields();

			await expect(sampleFieldCheckbox).not.toBeChecked();
			await expect(sampleScalarFieldCheckbox).not.toBeChecked();

			await saveFromModal({
				page,
			});
		});

		await test.step('Check there is no field added', async () => {
			await visualizationModesPage.assertTableFieldRowCount(0);
		});
	});
});
