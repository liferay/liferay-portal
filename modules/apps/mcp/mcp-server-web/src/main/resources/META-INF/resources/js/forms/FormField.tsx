/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {FieldValidator, useField} from 'formik';
import {FieldBase} from 'frontend-js-components-web';
import React from 'react';

import {required} from '../utils';

interface FormFieldProps {
	component?: 'input' | 'textarea';
	disabled?: boolean;
	helpMessage?: string;
	id: string;
	label: string;
	name: string;
	required?: boolean;
	validate?: FieldValidator;
}

export function FormField({
	component = 'input',
	disabled,
	helpMessage,
	id,
	label,
	name,
	required: isRequired,
	validate,
}: FormFieldProps) {
	const [field, meta] = useField({
		name,
		validate: validate ?? (isRequired ? required : undefined),
	});

	return (
		<FieldBase
			disabled={disabled}
			errorMessage={meta.touched ? meta.error : undefined}
			helpMessage={helpMessage}
			id={id}
			label={label}
			required={isRequired}
		>
			<ClayInput
				{...field}
				component={component}
				disabled={disabled}
				id={id}
				required={isRequired}
				type="text"
			/>
		</FieldBase>
	);
}
