/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

type ValidationFunction = (value: any) => string | undefined;
type ValidationSchema = Record<string, ValidationFunction[]>;

const alphanumeric: ValidationFunction = (value) =>
	/^[\w-]+$/.test(value)
		? undefined
		: Liferay.Language.get(
				'please-enter-only-alphanumeric-characters-dashes-or-underscores'
			);

const invalidCharacters =
	(chars: string[]): ValidationFunction =>
	(value) => {
		if (value && chars.some((char) => value.includes(char))) {
			return sub(
				Liferay.Language.get(
					'name-cannot-contain-the-following-invalid-characters-x'
				),
				chars.join(', ')
			);
		}
	};

const maxLength =
	(max: number): ValidationFunction =>
	(value) => {
		if (value && value.length > max) {
			return sub(
				Liferay.Language.get('please-enter-no-more-than-x-characters'),
				max
			);
		}
	};

const notNull: ValidationFunction = (value) => {
	if (value === 'null') {
		return Liferay.Language.get('name-cannot-be-null');
	}
};

const nonNumeric: ValidationFunction = (value) => {
	if (value && !isNaN(Number(value))) {
		return Liferay.Language.get('please-enter-a-nonnumeric-name');
	}
};

const required: ValidationFunction = (value) => {
	if (!value) {
		return Liferay.Language.get('this-field-is-required');
	}
};

const validate = (
	fields: ValidationSchema,
	values: Record<string, any>
): Record<string, string> => {
	const errors: Record<string, string> = {};

	Object.entries(fields).forEach(([inputName, validations]) => {
		validations.some((validation) => {
			const error = validation(values[inputName]);

			if (error) {
				errors[inputName] = error;
			}

			return Boolean(error);
		});
	});

	return errors;
};

export {
	alphanumeric,
	invalidCharacters,
	maxLength,
	notNull,
	nonNumeric,
	required,
	validate,
};
