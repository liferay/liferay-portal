/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests, test} from '@playwright/test';

import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {ConnectorsPage} from '../pages/ConnectorsPage';
import {EditConnectorPage} from '../pages/EditConnectorPage';
import {ProductPage} from '../pages/ProductPage';
import {ProductsPage} from '../pages/ProductsPage';

const pimPages = test.extend<{
	connectorsPage: ConnectorsPage;
	editConnectorPage: EditConnectorPage;
	pimSetup;
	productPage: ProductPage;
	productsPage: ProductsPage;
}>({
	connectorsPage: async ({page}, use) => {
		await use(new ConnectorsPage(page));
	},
	editConnectorPage: async ({page}, use) => {
		await use(new EditConnectorPage(page));
	},
	pimSetup: [
		async ({page}, use) => {
			const apiHelpers = new ApiHelpers(page);

			await apiHelpers.objectAdmin.waitForObjectDefinition('PIMBaseSku');
			await apiHelpers.objectAdmin.waitForObjectDefinition(
				'PIMConnector'
			);

			await use();
		},
		{auto: true},
	],
	productPage: async ({page}, use) => {
		await use(new ProductPage(page));
	},
	productsPage: async ({page}, use) => {
		await use(new ProductsPage(page));
	},
});

const pimPagesTest = mergeTests(
	loginTest(),
	featureFlagsTest({
		'LPD-96666': {enabled: true},
	}),
	pimPages
);

export {pimPagesTest};
