/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from '../main/fixtures/cmsPagesTest';
import {structureBuilderPagesTest} from './fixtures/structureBuilderPagesTest';

const test = mergeTests(
	cmsPagesTest,
	loginTest(),
	pageEditorPagesTest,
	structureBuilderPagesTest
);

test(
	'Alerts are displayed when trying to customize the editor without publishing the structure',
	{
		tag: '@LPD-50370',
	},
	async ({page, structureBuilderPage}) => {

		// Create structure

		await structureBuilderPage.createStructureFromData({
			label: `StructureName${getRandomInt()}`,
			page: structureBuilderPage,
		});

		// Add two Text fields

		await structureBuilderPage.addField('Text');

		await structureBuilderPage.changeFieldSettings({
			label: 'Field 1',
		});

		await structureBuilderPage.addField('Text');

		await structureBuilderPage.changeFieldSettings({
			label: 'Field 2',
		});

		// Try to customize the editor without publishing the structure

		await page.getByRole('button', {name: 'Customize Editor'}).click();

		// Check the warning is shown

		await expect(
			page.getByText(
				'To customize the editor you need to publish the content structure first.'
			)
		).toBeAttached();

		// Publish the structure

		await page
			.getByRole('dialog', {
				name: 'Publish to Customize Editor',
			})
			.getByRole('button', {name: 'Publish'})
			.click();

		await waitForAlert(
			page,
			'Remember to review the customized editor if needed.',
			{autoClose: false}
		);

		// Check the customized editor

		await page
			.getByRole('alert')
			.getByRole('button', {name: 'Customize Editor'})
			.click();

		await structureBuilderPage.waitForEditorCustomizerModal();

		await expect(page.getByLabel('Field 1 (Read Only)')).toBeVisible();

		// Go back to the structure builder

		await page
			.locator('.management-bar')
			.getByRole('link', {name: 'Back'})
			.click();

		// Delete the field and try to customize the editor again

		await structureBuilderPage.deleteFields([{label: 'Field 1'}]);

		await page.getByRole('button', {name: 'Customize Editor'}).click();

		// Check the warning is shown

		await expect(
			page.getByText(
				'To customize the editor you need to publish the content structure first. You have made changes to the content structure that may impact existing stored data once published.'
			)
		).toBeAttached();

		await page
			.getByRole('dialog', {
				name: 'Publish to Customize Editor',
			})
			.getByRole('button', {name: 'Publish'})
			.click();

		await page
			.getByRole('alert')
			.getByRole('button', {name: 'Customize Editor'})
			.click();

		await structureBuilderPage.waitForEditorCustomizerModal();

		// Check the editor is regenerated removing the deleted field

		await expect(page.getByLabel('Field 1 (Read Only)')).not.toBeVisible();
		await expect(page.getByLabel('Field 2 (Read Only)')).toBeVisible();
	}
);

test(
	'Edit editor link is shown every time we publish if it is customized',
	{
		tag: '@LPD-50370',
	},
	async ({page, pageEditorPage, structureBuilderPage}) => {

		// Create structure

		const label = `StructureName${getRandomInt()}`;

		await structureBuilderPage.createStructureFromData({
			label,
			page: structureBuilderPage,
		});

		// Add two Text fields

		await structureBuilderPage.addField('Text');

		await structureBuilderPage.changeFieldSettings({
			label: 'Field 1',
		});

		await structureBuilderPage.addField('Text');

		await structureBuilderPage.changeFieldSettings({
			label: 'Field 2',
		});

		// Publish the structure and check standard toast is shown

		await expect(async () => {
			await structureBuilderPage.publishButton.click({timeout: 1000});

			await waitForAlert(
				page,
				`Success:${label} was published successfully.`,
				{exact: true, timeout: 2000}
			);
		}).toPass();

		// Customize the editor

		await structureBuilderPage.customizeEditor();

		const fragmentId = await pageEditorPage.getFragmentId('Text', 0);

		await pageEditorPage.deleteFragment(fragmentId);

		// Go back to structure builder

		await clickAndExpectToBeVisible({
			target: page.getByText('Structure Fields'),
			trigger: page
				.locator('.management-bar')
				.getByRole('link', {name: 'Back'}),
		});

		// Publish again and check edit editor link is show in toast

		await expect(async () => {
			await structureBuilderPage.publishButton.click({timeout: 1000});

			await waitForAlert(
				page,
				'Remember to review the customized editor if needed'
			);
		}).toPass();
	}
);

test(
	'Can autogenerate default editor after customizing it',
	{
		tag: '@LPD-50376',
	},
	async ({page, pageEditorPage, structureBuilderPage}) => {

		// Create structure

		await structureBuilderPage.createStructureFromData({
			label: `StructureName${getRandomInt()}`,
			page: structureBuilderPage,
		});

		// Customize the editor and add a fragment

		await structureBuilderPage.customizeEditor();

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		// Regenerate Display Page and check the Heading is not present

		await pageEditorPage.regenerateDisplayPage();

		await page.getByText('Select a Page Element', {exact: true}).waitFor();

		await expect(
			page.locator('.lfr-layout-structure-item-basic-component-heading')
		).not.toBeVisible();
	}
);

test(
	'Control menu is not shown in the page editor when customizing the editor',
	{
		tag: '@LPD-69912',
	},
	async ({page, structureBuilderPage}) => {

		// Create structure

		await structureBuilderPage.createStructureFromData({
			label: `StructureName${getRandomInt()}`,
			page: structureBuilderPage,
		});

		// Customize the editor

		await structureBuilderPage.customizeEditor();

		// Check the control menu is not shown

		await expect(page.locator('.control-menu')).not.toBeVisible();
	}
);
