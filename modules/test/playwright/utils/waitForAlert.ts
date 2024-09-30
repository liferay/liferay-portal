import {FrameLocator, Page} from '@playwright/test';

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

interface waitForAlert {
	autoClose?: boolean;
	displayType?:
		| '.alert-success'
		| '.alert-info'
		| '.alert-warning'
		| '.alert-danger';
	page: Page | FrameLocator;
	text?: string;
}

export async function waitForAlert({
	autoClose = true,
	displayType = '.alert-success',
	page,
	text = 'Success:Your request completed successfully.',
}: waitForAlert) {
	const alert = page.locator(displayType, {
		hasText: text,
	});

	await alert.waitFor();

	if (autoClose) {
		await alert.getByLabel('Close').click();

		await alert.waitFor({state: 'hidden'});
	}
}
