/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import {FieldValidator, useField} from 'formik';
import {FieldBase} from 'frontend-js-components-web';
import React from 'react';

import required from '../../utils/required';

interface FormFieldProps {
	appendContent?: React.ReactNode;
	component?: 'input' | 'textarea';
	disabled?: boolean;
	helpMessage?: string;
	id: string;
	label: string;
	name: string;
	onChange?: React.ChangeEventHandler<HTMLInputElement | HTMLTextAreaElement>;
	prefixHelpMessage?: string;
	prependContent?: string;
	required?: boolean;
	validate?: FieldValidator;
}

export function FormField({
	appendContent,
	component = 'input',
	disabled,
	helpMessage,
	id,
	label,
	name,
	onChange,
	prefixHelpMessage,
	prependContent,
	required: isRequired,
	validate,
}: FormFieldProps) {
	const [field, meta] = useField({
		name,
		validate: validate ?? (isRequired ? required : undefined),
	});

	const prefixHelpMessageId = `${id}prefixHelpMessage`;

	const input = (
		<ClayInput
			{...field}
			aria-describedby={
				prefixHelpMessage
					? `${prefixHelpMessageId} ${id}fieldFeedback`
					: `${id}fieldFeedback`
			}
			aria-invalid={meta.touched && meta.error ? true : undefined}
			component={component}
			disabled={disabled}
			id={id}
			onChange={(event) => {
				field.onChange(event);

				onChange?.(event);
			}}
			required={isRequired}
			type="text"
		/>
	);

	return (
		<FieldBase
			disabled={disabled}
			errorMessage={meta.touched ? meta.error : undefined}
			helpMessage={helpMessage}
			id={id}
			label={label}
			required={isRequired}
		>
			{prefixHelpMessage && (
				<ClayForm.FeedbackGroup>
					<ClayForm.HelpText id={prefixHelpMessageId}>
						{prefixHelpMessage}
					</ClayForm.HelpText>
				</ClayForm.FeedbackGroup>
			)}

			{prependContent || appendContent ? (
				<ClayInput.Group>
					{prependContent && (
						<ClayInput.GroupItem prepend shrink>
							<ClayInput.GroupText>
								{prependContent}
							</ClayInput.GroupText>
						</ClayInput.GroupItem>
					)}

					<ClayInput.GroupItem
						append={!!prependContent}
						prepend={!!appendContent}
					>
						{input}
					</ClayInput.GroupItem>

					{appendContent && (
						<ClayInput.GroupItem append shrink>
							{appendContent}
						</ClayInput.GroupItem>
					)}
				</ClayInput.Group>
			) : (
				input
			)}
		</FieldBase>
	);
}
