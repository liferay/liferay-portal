/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from './clickAndExpectToBeVisible';

export async function changeManagementToolbarView(
	parent: Page | FrameLocator,
	viewName: string
) {
	const trigger = parent.getByLabel('Select View, Currently Selected: ');

	await trigger.waitFor({state: 'visible'});

	const currentViewLabel = parent.getByLabel(
		`Select View, Currently Selected: ${viewName}`
	);

	if (await currentViewLabel.isVisible()) {
		return;
	}

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: parent.getByRole('menuitem', {name: viewName}),
		trigger,
	});

	await expect(currentViewLabel).toBeVisible();
}
