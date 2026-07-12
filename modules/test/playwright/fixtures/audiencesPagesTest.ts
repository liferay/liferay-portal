/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {AudiencesPage} from '../pages/audiences-web/AudiencesPage';

const audiencesPagesTest = test.extend<{
	audiencesPage: AudiencesPage;
}>({
	audiencesPage: async ({page}, use) => {
		await use(new AudiencesPage(page));
	},
});

export {audiencesPagesTest};
