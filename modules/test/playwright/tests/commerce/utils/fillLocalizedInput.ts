/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator} from '@playwright/test';

import {waitForInputLocalized} from '../../../utils/waitFor';

export async function fillLocalizedInput(input: Locator, value: string) {
	await waitForInputLocalized(input.page(), await input.getAttribute('id'));

	await input.fill(value);
}
