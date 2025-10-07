/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {StructureBuilderPage} from '../pages/StructureBuilderPage';

const structureBuilderPagesTest = dataApiHelpersTest.extend<{
	structureBuilderPage: StructureBuilderPage;
}>({
	structureBuilderPage: async ({apiHelpers, page}, use) => {
		await use(new StructureBuilderPage(page, apiHelpers));
	},
});

export {structureBuilderPagesTest};
