/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {FrontendSPAInfrastructureConfigurationPage} from '../pages/frontend-spa-web/FrontendSPAInfrastructureConfigurationPage';

const frontendSPAInfrastructureConfigurationTest = test.extend<{
	frontendSPAInfrastructureConfigurationPage: FrontendSPAInfrastructureConfigurationPage;
}>({
	frontendSPAInfrastructureConfigurationPage: async ({page}, use) => {
		await use(new FrontendSPAInfrastructureConfigurationPage(page));
	},
});

export {frontendSPAInfrastructureConfigurationTest};
