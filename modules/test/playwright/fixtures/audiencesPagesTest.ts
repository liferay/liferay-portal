/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AudiencesPage} from '../pages/audiences-web/AudiencesPage';
import {dataApiHelpersTest} from './dataApiHelpersTest';

const audiencesPagesTest = dataApiHelpersTest.extend<{
	audiencesPage: AudiencesPage;
}>({
	audiencesPage: async ({apiHelpers, page}, use) => {
		await use(new AudiencesPage(page, apiHelpers));
	},
});

export {audiencesPagesTest};
