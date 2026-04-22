/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {FieldBase} from 'frontend-js-components-web';
import React from 'react';

export type FieldTextProps = {
	component?: 'textarea' | 'input';
	disabled?: boolean;
	errorMessage?: string;
	formGroupProps?: {className: string};
	helpMessage?: string;
	id?: string;
	label: string;
	name: string;
	required?: boolean;
	type?: 'text' | 'number';
	value?: string;
} & React.ComponentProps<typeof ClayInput>;

const FieldText = ({
	'aria-describedby': ariaDescribedBy,
	component = 'input',
	disabled,
	errorMessage,
	formGroupProps,
	helpMessage,
	id,
	label,
	name,
	required,
	type = 'text',
	value = '',
	...restProps
}: FieldTextProps) => {
	const fieldId = id ?? name;
	const feedbackId =
		errorMessage || helpMessage ? `${fieldId}fieldFeedback` : undefined;
	const describedBy =
		[ariaDescribedBy, feedbackId].filter(Boolean).join(' ') || undefined;

	return (
		<FieldBase
			className={formGroupProps?.className}
			disabled={disabled}
			errorMessage={errorMessage}
			helpMessage={helpMessage}
			id={fieldId}
			label={label}
			required={required}
		>
			<ClayInput
				{...restProps}
				aria-describedby={describedBy}
				aria-invalid={errorMessage ? true : undefined}
				component={component}
				disabled={disabled}
				id={fieldId}
				name={name}
				required={required}
				type={type}
				value={value}
			/>
		</FieldBase>
	);
};

export default FieldText;
