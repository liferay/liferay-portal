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
import {EditFieldMappingsPage} from '../pages/EditFieldMappingsPage';
import {FieldMappingsPage} from '../pages/FieldMappingsPage';
import {ProductPage} from '../pages/ProductPage';
import {ProductStructuresPage} from '../pages/ProductStructuresPage';
import {ProductsPage} from '../pages/ProductsPage';
import {SpaceSelectorPage} from '../pages/SpaceSelectorPage';

const pimPages = test.extend<{
	connectorsPage: ConnectorsPage;
	editConnectorPage: EditConnectorPage;
	editFieldMappingsPage: EditFieldMappingsPage;
	fieldMappingsPage: FieldMappingsPage;
	pimSetup;
	productPage: ProductPage;
	productStructuresPage: ProductStructuresPage;
	productsPage: ProductsPage;
	spaceSelectorPage: SpaceSelectorPage;
}>({
	connectorsPage: async ({page}, use) => {
		await use(new ConnectorsPage(page));
	},
	editConnectorPage: async ({page}, use) => {
		await use(new EditConnectorPage(page));
	},
	editFieldMappingsPage: async ({page}, use) => {
		await use(new EditFieldMappingsPage(page));
	},
	fieldMappingsPage: async ({page}, use) => {
		await use(new FieldMappingsPage(page));
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
	productStructuresPage: async ({page}, use) => {
		await use(new ProductStructuresPage(page));
	},
	productsPage: async ({page}, use) => {
		await use(new ProductsPage(page));
	},
	spaceSelectorPage: async ({page}, use) => {
		await use(new SpaceSelectorPage(page));
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
