/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from '@liferay/object-js-components-web';

export interface ErrorMessage {
	fieldName: keyof ObjectAction;
	message?: string;
	messages?: ErrorMessage[];
}

export interface Error {
	[key: string]: string | Error;
}

export function parseError(details: ErrorMessage[], errors: Error) {
	details.forEach(({fieldName, message, messages}) => {
		if (message) {
			errors[fieldName] = message;
		}
		else {
			errors[fieldName] = {};
			parseError(messages as ErrorMessage[], errors[fieldName] as Error);
		}
	});
}

export function getErrorMessage(errors: Error, errorMessages: Set<string>) {
	Object.values(errors).forEach((value) => {
		if (typeof value === 'string') {
			if (!errorMessages.has(value)) {
				errorMessages.add(value);
			}
		}
		else {
			getErrorMessage(value, errorMessages);
		}
	});
}

export function handleErrors(
	{detail, message, title}: Error,
	setErrors: (value: Error) => void
) {
	if (detail) {
		const details = JSON.parse(detail as string);
		const newErrors: Error = {};

		parseError(details, newErrors);

		setErrors(newErrors);

		const errorMessages = new Set<string>();

		if (newErrors) {
			getErrorMessage(newErrors, errorMessages);
			errorMessages.forEach((message) => {
				openToast({
					message,
					type: 'danger',
				});
			});
		}
	}
	else if (title || message) {
		openToast({
			message: (title || message) as string,
			type: 'danger',
		});
	}
}
