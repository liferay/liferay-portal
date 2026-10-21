/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {escapeHTML} from 'frontend-js-web';

type ToastMessageOptions = {
	dangerouslySetMessageHTML?: boolean;
};

export function openErrorToast(
	message: string,
	options?: ToastMessageOptions
): void {
	openToast({
		message: toToastMessage(message, options),
		type: 'danger',
	});
}

export function openSuccessToast(
	message: string,
	options?: ToastMessageOptions
): void {
	openToast({
		message: toToastMessage(message, options),
		type: 'success',
	});
}

function toToastMessage(
	message: string,
	options?: ToastMessageOptions
): string {
	if (options?.dangerouslySetMessageHTML) {
		return message;
	}

	return escapeHTML(message);
}
