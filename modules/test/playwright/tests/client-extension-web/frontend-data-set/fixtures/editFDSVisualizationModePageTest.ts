/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {EditFDSVisualizationModePage} from '../pages/EditFDSVisualizationModePage';

const editFDSVisualizationModePageTest = test.extend<{
	editFDSVisualizationModePage: EditFDSVisualizationModePage;
}>({
	editFDSVisualizationModePage: async ({page}, use) => {
		await use(new EditFDSVisualizationModePage(page));
	},
});

export {editFDSVisualizationModePageTest};
