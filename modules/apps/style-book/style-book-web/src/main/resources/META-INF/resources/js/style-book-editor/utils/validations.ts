/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FormikErrors} from 'formik';

type ValidationFunction = (value: string) => string | undefined;

const required: ValidationFunction = (value) => {
	if (!value.trim()) {
		return Liferay.Language.get('this-field-is-required');
	}
};

const unique =
	(existingValues: string[], errorMessage: string): ValidationFunction =>
	(value) => {
		const normalizedValue = value.trim().toLowerCase();

		if (
			existingValues.some(
				(existingValue) =>
					existingValue.toLowerCase() === normalizedValue
			)
		) {
			return errorMessage;
		}
	};

const validate = <Values extends {[FieldName in keyof Values]: string}>(
	fields: Partial<Record<keyof Values, ValidationFunction[]>>,
	values: Values
) => {
	return (Object.keys(fields) as (keyof Values)[]).reduce<
		FormikErrors<Values>
	>((errors, fieldName) => {
		const error = fields[fieldName]
			?.map((validation) => validation(values[fieldName]))
			.find(Boolean);

		if (error) {
			return {
				...errors,
				[fieldName]: error,
			};
		}

		return errors;
	}, {});
};

export {required, unique, validate};
