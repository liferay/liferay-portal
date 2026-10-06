/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {clientExtensionsPageTest} from '../fixtures/clientExtensionsPageTest';
import {WaitAction} from '../pages/EditClientExtensionsPage';
import {editFDSVisualizationModePageTest} from './fixtures/editFDSVisualizationModePageTest';
import {EditFDSVisualizationModePage} from './pages/EditFDSVisualizationModePage';

const test = mergeTests(
	clientExtensionsPageTest,
	editFDSVisualizationModePageTest,
	featureFlagsTest({
		'LPD-43793': {enabled: true},
	}),
	loginTest()
);

test(
	'Verify that it is possible to create a visualization mode',
	{tag: '@LPD-107931'},
	async ({clientExtensionsPage, editFDSVisualizationModePage, page}) => {
		await clientExtensionsPage.goto();

		await clientExtensionsPage.addNewClientExtensionButton.click();

		await page
			.getByRole('menuitem', {
				name: 'Add Frontend Data Set Visualization Mode',
			})
			.click();

		await editFDSVisualizationModePage.waitFor();

		const name = getRandomString();

		await editFDSVisualizationModePage.nameInput.fill(name);
		await editFDSVisualizationModePage.javaScriptURLInput.fill(
			'http://www.myplace.com/myvisualizationmode.js'
		);
		await editFDSVisualizationModePage.selectIcon('cards2');
		await editFDSVisualizationModePage.publish(WaitAction.SUCCESS);

		await clientExtensionsPage.goto();

		await expect(clientExtensionsPage.getRowByText(name)).toBeVisible();

		const editedFDSVisualizationModePage =
			await clientExtensionsPage.editClientExtension(
				name,
				EditFDSVisualizationModePage
			);

		await expect(editedFDSVisualizationModePage.iconInput).toHaveValue(
			'cards2'
		);

		await test.step('Cleanup', async () => {
			await clientExtensionsPage.goto();

			await clientExtensionsPage.deleteClientExtension(name);
		});
	}
);

test(
	'Verify that a visualization mode is marked as beta',
	{tag: '@LPD-107931'},
	async ({editFDSVisualizationModePage, page}) => {
		await editFDSVisualizationModePage.goto();

		await expect(page.getByText('Beta', {exact: true})).toBeVisible();
	}
);

test(
	'Verify that it is not possible to publish when the icon is empty',
	{tag: '@LPD-107931'},
	async ({editFDSVisualizationModePage, page}) => {
		await editFDSVisualizationModePage.goto();

		await editFDSVisualizationModePage.nameInput.fill(getRandomString());
		await editFDSVisualizationModePage.javaScriptURLInput.fill(
			'http://www.myplace.com/myvisualizationmode.js'
		);
		await editFDSVisualizationModePage.publish(WaitAction.NONE);

		await expect(page.getByText('Please select an icon.')).toBeVisible();
	}
);
