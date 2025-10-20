/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClaySelect} from '@clayui/form';
import React from 'react';

import FieldWrapper from './FieldWrapper';

type ClaySelectProps = {
	shrink?: boolean;
	sizing?: 'lg' | 'sm';
} & React.SelectHTMLAttributes<HTMLSelectElement>;

const FieldSelect = ({
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
} & ClaySelectProps) => {
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
			<ClaySelect
				{...restProps}
				aria-describedby={(errorMessage || helpMessage) ?? feedbackId}
				aria-label={placeholder}
				disabled={disabled}
				id={fieldId}
				name={name}
			>
				{items.map(({label, value}) => (
					<ClaySelect.Option
						key={value}
						label={label}
						value={value}
					/>
				))}
			</ClaySelect>
		</FieldWrapper>
	);
};

export default FieldSelect;
