/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import classNames from 'classnames';
import {FieldFeedback, useId} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useRef, useState} from 'react';

type Feedback = {
	fields?: Record<string, string>;
	message?: string;
};

type Field = {
	defaultValue: string;
	handleChange?: (value: string) => void;
	handleOnBlur?: (event: React.FocusEvent<HTMLInputElement, Element>) => void;
	helpText?: string;
	label: string;
	name: string;
	value: string;
};

type FieldsProps = {
	disabled?: boolean;
	errors?: Feedback;
	fields: Field[];
	hideReset?: boolean;
	url: string;
	warnings?: Feedback;
};

export default function SeparatorFields({
	disabled,
	errors,
	fields,
	hideReset,
	url,
	warnings,
}: FieldsProps) {
	return (
		<>
			{fields.map((field) => (
				<Field
					disabled={disabled}
					errors={errors}
					field={field}
					hideReset={hideReset}
					key={field.name}
					url={url}
					warnings={warnings}
				/>
			))}
		</>
	);
}

type FieldProps = {
	disabled?: boolean;
	errors?: Feedback;
	field: Field;
	hideReset?: boolean;
	url: string;
	warnings?: Feedback;
};

function Field({
	disabled,
	errors,
	field,
	hideReset,
	url,
	warnings,
}: FieldProps) {
	const descriptionId = useId();
	const ref = useRef<HTMLInputElement>(null);

	const {defaultValue, handleChange, handleOnBlur, helpText, label, name} =
		field;

	const error = errors?.fields?.[name];
	const warning = warnings?.fields?.[name];

	const [value, setValue] = useState(field.value);

	return (
		<ClayForm.Group
			className={classNames({
				'has-error': error,
				'has-warning': warning && !error,
			})}
			key={name}
		>
			<label className="mb-0" htmlFor={name}>
				{label}
			</label>

			<p className="mb-1 small text-secondary">{url}</p>

			<p className="sr-only" id={descriptionId}>
				{sub(
					Liferay.Language.get('this-will-work-as-a-suffix-for-x'),
					url
				)}
			</p>

			<ClayInput.Group>
				<ClayInput.GroupItem prepend shrink>
					<ClayInput.GroupText aria-hidden="true">
						/
					</ClayInput.GroupText>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem append>
					<ClayInput
						aria-describedby={descriptionId}
						disabled={disabled}
						id={name}
						name={name}
						onBlur={(event) => {
							if (handleOnBlur) {
								handleOnBlur(event);
							}
						}}
						onChange={(event) => {
							setValue(event.target.value);

							if (handleChange) {
								handleChange(event.target.value);
							}
						}}
						ref={ref}
						value={value}
					/>
				</ClayInput.GroupItem>

				{value !== defaultValue && !hideReset ? (
					<ClayInput.GroupItem shrink>
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get(
								'reset-to-default-value'
							)}
							displayType="secondary"
							onClick={() => {
								setValue(defaultValue);

								ref.current?.focus();
							}}
							symbol="restore"
							title={Liferay.Language.get(
								'reset-to-default-value'
							)}
						/>
					</ClayInput.GroupItem>
				) : null}
			</ClayInput.Group>

			<FieldFeedback
				errorMessage={error}
				helpMessage={helpText}
				id={`${name}_fieldFeedback`}
				warningMessage={warning}
			/>
		</ClayForm.Group>
	);
}
