/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator} from '@playwright/test';

export default async function gotoDepotInItemSelector(
	itemSelector: FrameLocator,
	depotName: string
) {

	// The item selector opens in the current site

	await itemSelector
		.getByRole('link', {exact: true, name: 'Sites and Libraries'})
		.click();

	await itemSelector.getByText('Asset Library', {exact: true}).click();

	await itemSelector.getByText(depotName, {exact: true}).click();
}
