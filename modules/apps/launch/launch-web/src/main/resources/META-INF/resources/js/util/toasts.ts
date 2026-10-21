/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {escapeHTML} from 'frontend-js-web';

export function openErrorToast(message: string): void {
	openToast({
		message: escapeHTML(message),
		type: 'danger',
	});
}

export function openSuccessToast(message: string): void {
	openToast({
		message: escapeHTML(message),
		type: 'success',
	});
}
