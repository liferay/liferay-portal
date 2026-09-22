/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator} from '@playwright/test';

export async function selectOptionContaining(
	select: Locator,
	optionLabel: string
) {
	const optionLabels = await select.locator('option').allTextContents();

	await select.selectOption({
		index: optionLabels.findIndex((currentOptionLabel) =>
			currentOptionLabel.includes(optionLabel)
		),
	});
}
