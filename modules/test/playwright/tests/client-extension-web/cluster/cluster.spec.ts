/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {liferayConfig} from '../../../liferay.config';
import getRandomString from '../../../utils/getRandomString';
import {editJSImportMapsPageTest} from '../main/fixtures/editJSImportMapsExtensionPageTest';
import {WaitAction} from '../pages/EditClientExtensionsPage';

export const test = mergeTests(editJSImportMapsPageTest, loginTest());

test('Client extension is deployed in all cluster nodes', async ({
	editJSImportMapsPage,
	page,
}) => {
	await editJSImportMapsPage.goto();

	const name = '_' + getRandomString().replace(/[^a-zA-Z0-9]/g, '_');
	const url = `http://example.com/${name}.js`;

	await editJSImportMapsPage.nameInput.fill(name);
	await editJSImportMapsPage.bareSpecifierInput.fill(name);
	await editJSImportMapsPage.javaScriptURLInput.fill(url);

	await editJSImportMapsPage.publish(WaitAction.SUCCESS);

	const clusterNodeURLs = [
		liferayConfig.environment.baseUrl,
		liferayConfig.environment.baseUrl.replace('8080', '9080'),
	];

	for (const clusterNodeURL of clusterNodeURLs) {
		const response = await page.goto(clusterNodeURL);

		const body = await response.body();

		expect(body.toString()).toContain(`"${name}":"${url}"`);
	}
});
