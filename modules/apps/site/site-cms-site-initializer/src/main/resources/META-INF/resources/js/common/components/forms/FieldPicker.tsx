/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option, Picker} from '@clayui/core';
import React from 'react';

import FieldWrapper from './FieldWrapper';

const FieldPicker = ({
	disabled,
	errorMessage,
	helpMessage,
	id,
	items,
	label,
	name,
	placeholder,
	required,
	...restProps
}: {
	disabled?: boolean;
	errorMessage?: string;
	helpMessage?: string;
	id?: string;
	items: any[];
	label: string;
	name: string;
	placeholder?: string;
	required?: boolean;
} & Omit<React.ComponentProps<typeof Picker>, 'children'>) => {
	const fieldId = id ?? name;
	const feedbackId = `feedback-${fieldId}`;

	return (
		<FieldWrapper
			disabled={disabled}
			errorMessage={errorMessage}
			feedbackId={feedbackId}
			fieldId={fieldId}
			helpMessage={helpMessage}
			label={label}
			required={required}
		>
			<Picker
				{...restProps}
				aria-describedby={(errorMessage || helpMessage) ?? feedbackId}
				disabled={disabled}
				id={fieldId}
				items={items}
				name={name}
				placeholder={placeholder}
			>
				{({label, value}) => <Option key={value}>{label}</Option>}
			</Picker>
		</FieldWrapper>
	);
};

export default FieldPicker;
