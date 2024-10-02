/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {dataSetManagerApiHelpersTest} from '../../fixtures/dataSetManagerApiHelpersTest';
import clickActionInRow from '../../utils/clickActionInRow';
import saveFromModal from '../../utils/saveFromModal';
import {dataSetManagerSetupTest} from './fixtures/dataSetManagerSetupTest';
import {visualizationModesPageTest} from './fixtures/visualizationModesPageTest';

export const test = mergeTests(
	dataSetManagerApiHelpersTest,
	featureFlagsTest({
		'LPS-164563': true,
	}),
	visualizationModesPageTest,
	loginTest(),
	dataSetManagerSetupTest
);

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

test('Assert the CellRenderer is displayed', async ({
	page,
	visualizationModesPage,
}) => {
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

	await test.step('Confirm that the cell renderer CX option is present when editing a field', async () => {
		await clickActionInRow({
			actionName: 'Edit',
			rowName: sampleScalarField,
			visualizationModesPage,
		});

		const editFieldModal = page.locator('.modal');

		await expect(editFieldModal).toBeInViewport();

		const rendererSelect = editFieldModal
			.locator('button', {
				hasText: 'Default',
			})
			.first();

		await expect(rendererSelect).toBeInViewport();

		await rendererSelect.click();

		const cellRendererOption = editFieldModal.locator('li', {
			hasText: 'Liferay Sample Frontend Data Set Cell Renderer',
		});

		await expect(cellRendererOption).toBeInViewport();
	});
});
