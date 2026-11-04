/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeHidden} from './clickAndExpectToBeHidden';

type Options = {
	autoClose?: boolean;
	closeText?: string;
	exact?: boolean;
	first?: boolean;
	timeout?: number;
	type?: 'success' | 'info' | 'warning' | 'danger';
};

const CSS_CLASSES = {
	danger: '.alert-danger',
	info: '.alert-info',
	success: '.alert-success',
	warning: '.alert-warning',
};

export async function waitForAlert(
	parent: Page | FrameLocator,
	text = 'Success:Your request completed successfully.',
	{
		autoClose = true,
		closeText = 'Close',
		exact = false,
		first = false,
		timeout,
		type = 'success',
	}: Options = {
		autoClose: true,
		closeText: 'Close',
		exact: false,
		first: false,
		type: 'success',
	}
) {
	const matchingAlerts = parent.locator(CSS_CLASSES[type], {
		hasText: text,
	});

	const alert = first ? matchingAlerts.first() : matchingAlerts;

	if (timeout) {
		await alert.waitFor({timeout});
	}
	else {
		await alert.waitFor();
	}

	if (exact) {
		await expect(alert).toHaveText(text);
	}

	if (autoClose) {
		await clickAndExpectToBeHidden({
			target: alert,
			trigger: alert.getByLabel(closeText),
		});
	}
}
