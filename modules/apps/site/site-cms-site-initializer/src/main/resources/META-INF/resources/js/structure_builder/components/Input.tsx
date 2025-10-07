/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {FieldFeedback, useId} from 'frontend-js-components-web';
import React, {useState} from 'react';

export default function Input({
	className,
	disabled = false,
	error,
	helpMessage,
	inputProps,
	label,
	onValueChange,
	placeholder,
	required = false,
	value: initialValue,
}: {
	className?: string;
	disabled?: boolean;
	error?: string;
	helpMessage?: string;
	inputProps?: React.InputHTMLAttributes<HTMLInputElement>;
	label: string;
	onValueChange: (value: string) => void;
	placeholder?: string;
	required?: boolean;
	value: string;
}) {
	const id = useId();
	const helpMessageId = useId();
	const [value, setValue] = useState(initialValue);

	const hasError = error || (required && !value);

	return (
		<ClayForm.Group
			className={classNames(className, {'has-error': hasError})}
		>
			<label htmlFor={id}>
				{label}

				{required ? (
					<ClayIcon
						className="ml-1 reference-mark"
						focusable="false"
						role="presentation"
						symbol="asterisk"
					/>
				) : null}
			</label>

			<ClayInput
				aria-describedby={helpMessageId}
				disabled={disabled}
				id={id}
				onBlur={() => onValueChange(value)}
				onChange={(event) => setValue(event.target.value)}
				placeholder={placeholder}
				required={required}
				type="text"
				value={value}
				{...inputProps}
			/>

			{hasError ? (
				<FieldFeedback
					errorMessage={
						error || Liferay.Language.get('this-field-is-required')
					}
				/>
			) : null}

			{helpMessage ? (
				<FieldFeedback helpMessage={helpMessage} id={helpMessageId} />
			) : null}
		</ClayForm.Group>
	);
}
