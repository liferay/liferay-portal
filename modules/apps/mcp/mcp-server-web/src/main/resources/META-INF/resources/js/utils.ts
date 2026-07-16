/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';

import {DataMask} from './types';

type ToastMessageOptions = {
	dangerouslySetMessageHTML?: boolean;
};

export function isSystemMask(dataMask: DataMask | null): boolean {
	return dataMask?.maskType?.key === 'system';
}

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

export function required(value: string): string | undefined {
	return value?.trim()
		? undefined
		: Liferay.Language.get('this-field-is-required');
}

export function toODataStringLiteral(value: string): string {
	return `'${value.replace(/'/g, "''")}'`;
}

function toToastMessage(
	message: string,
	options?: ToastMessageOptions
): string {
	if (options?.dangerouslySetMessageHTML) {
		return message;
	}

	return Liferay.Util.escapeHTML(message);
}
