/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {pimPagesTest} from './fixtures/pimPagesTest';

const test = mergeTests(loginTest(), pimPagesTest);

test(
	'Hide the search bar in the connectors empty state',
	{tag: ['@LPD-98441']},
	async ({connectorsPage}) => {
		await connectorsPage.goto();

		await expect(connectorsPage.emptyStateTitle).toBeVisible();

		await expect(
			connectorsPage.dataSetFragmentPage.searchInput
		).toBeHidden();

		await expect(connectorsPage.newConnectorButton).toBeVisible();
	}
);

test(
	'Create a connector',
	{tag: ['@LPD-98441']},
	async ({connectorsPage, editConnectorPage}) => {
		const connectorName = getRandomString();

		try {
			await connectorsPage.goto();

			await connectorsPage.newConnectorButton.click();

			await editConnectorPage.createConnector({
				connector: 'Liferay Commerce',
				name: connectorName,
			});

			await expect(
				connectorsPage.getConnector(connectorName)
			).toBeVisible();
		}
		finally {
			await connectorsPage.goto();

			await connectorsPage.deleteConnector(connectorName);
		}
	}
);
