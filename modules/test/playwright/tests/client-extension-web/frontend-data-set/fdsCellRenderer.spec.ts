/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {clientExtensionsPageTest} from '../fixtures/clientExtensionsPageTest';
import {WaitAction} from '../pages/EditClientExtensionsPage';
import {editFDSCellRendererPageTest} from './fixtures/editFDSCellRendererPageTest';

const test = mergeTests(
	clientExtensionsPageTest,
	editFDSCellRendererPageTest,
	loginTest()
);

test(
	`Verify that changes are not saved when clicks on Cancel button`,
	{tag: '@LPS-175155'},
	async ({editFDSCellRendererPage}) => {
		const name = getRandomString();

		await editFDSCellRendererPage.goto();

		await editFDSCellRendererPage.nameInput.fill(name);
		await editFDSCellRendererPage.javaScriptURLInput.fill(
			'http://www.myplace.com/mycellrenderer.js'
		);
		const clientExtensionsPage = await editFDSCellRendererPage.cancel();

		await clientExtensionsPage.waitFor();

		await expect(clientExtensionsPage.getRowByText(name)).toHaveCount(0);
	}
);

test(
	`Verify that it is possible to change the language`,
	{tag: '@LPS-175155'},
	async ({editFDSCellRendererPage}) => {
		await editFDSCellRendererPage.goto();

		await editFDSCellRendererPage.changeNameLanguage('es_ES');
		await editFDSCellRendererPage.fillName(
			'es_ES',
			'Cambiar formato de fecha'
		);
		await editFDSCellRendererPage.changeNameLanguage('en_US');
		await editFDSCellRendererPage.changeNameLanguage('es_ES');

		await expect(editFDSCellRendererPage.nameInput).toHaveValue(
			'Cambiar formato de fecha'
		);
	}
);

test(
	`Verify that it is possible to create a cell renderer`,
	{tag: '@LPS-175155'},
	async ({clientExtensionsPage, editFDSCellRendererPage}) => {
		const name = getRandomString();

		await editFDSCellRendererPage.goto();

		await editFDSCellRendererPage.nameInput.fill(name);
		await editFDSCellRendererPage.javaScriptURLInput.fill(
			'http://www.myplace.com/mycellrenderer.js'
		);
		await editFDSCellRendererPage.publish(WaitAction.SUCCESS);

		await clientExtensionsPage.goto();

		await expect(clientExtensionsPage.getRowByText(name)).toBeVisible();

		await test.step('Cleanup', async () => {
			await clientExtensionsPage.deleteClientExtension(name);
		});
	}
);

test(
	`Verify that the Additional Resources group can be hidden`,
	{tag: '@LPS-175155'},
	async ({editFDSCellRendererPage, page}) => {
		await editFDSCellRendererPage.goto();

		await page.getByRole('button', {name: 'Additional Resources'}).click();

		await page
			.getByText('Source Code URL')
			.waitFor({state: 'hidden', timeout: 1000});
	}
);

test(
	`Verify that the Content group can be hidden`,
	{tag: '@LPS-175155'},
	async ({editFDSCellRendererPage, page}) => {
		await editFDSCellRendererPage.goto();

		await page.getByRole('button', {exact: true, name: 'Content'}).click();

		await page
			.getByText('JavaScript URL')
			.waitFor({state: 'hidden', timeout: 1000});
	}
);

test(
	`Verify that the Identity group can be hidden`,
	{tag: '@LPS-175155'},
	async ({editFDSCellRendererPage, page}) => {
		await editFDSCellRendererPage.goto();

		await page.getByRole('button', {name: 'Identity'}).click();

		await page.getByText('Name').waitFor({state: 'hidden', timeout: 1000});
		await page
			.getByText('Description')
			.waitFor({state: 'hidden', timeout: 1000});
	}
);

test(
	`Verify that it is not possible to publish when the Name field is empty`,
	{tag: '@LPS-175155'},
	async ({editFDSCellRendererPage}) => {
		await editFDSCellRendererPage.goto();

		await editFDSCellRendererPage.javaScriptURLInput.fill(
			'http://www.myplace.com/mycellrenderer.js'
		);

		await editFDSCellRendererPage.publish(WaitAction.ERROR);
	}
);

test(
	`Verify that it is not possible to publish when the JavaScript URL field is empty`,
	{tag: '@LPS-175155'},
	async ({editFDSCellRendererPage, page}) => {
		const name = getRandomString();

		await editFDSCellRendererPage.goto();

		await editFDSCellRendererPage.nameInput.fill(name);
		await editFDSCellRendererPage.publish(WaitAction.NONE);

		await expect(
			page.getByText('The JavaScript URL field is required.')
		).toBeVisible();
	}
);
