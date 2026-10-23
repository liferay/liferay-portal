/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {
	ClayCheckbox,
	ClayInput,
	ClaySelectWithOption,
	ClayToggle,
} from '@clayui/form';
import {RequiredMark} from '@liferay/site-cms-site-initializer';
import React from 'react';

export interface IPIMConnectorSettingsField {
	businessType: string;
	label: string;
	name: string;
	options?: {label: string; value: string}[] | null;
	required: boolean;
}

export type PIMConnectorSettingsValue = boolean | number | string | string[];

interface IProps {
	field: IPIMConnectorSettingsField;
	onChange: (value: PIMConnectorSettingsValue) => void;
	value?: PIMConnectorSettingsValue | null;
}

const INPUT_TYPES: {[key: string]: string} = {
	Date: 'date',
	DateTime: 'datetime-local',
	Decimal: 'number',
	Encrypted: 'password',
	Integer: 'number',
	LongInteger: 'number',
	PrecisionDecimal: 'number',
	Text: 'text',
};

export default function PIMConnectorSettingsField({
	field,
	onChange,
	value,
}: IProps) {
	const id = `pimConnectorSettings_${field.name}`;

	if (field.businessType === 'Boolean') {
		return (
			<ClayForm.Group className="mb-0">
				<ClayToggle
					id={id}
					label={field.label}
					onToggle={onChange}
					toggled={Boolean(value)}
				/>
			</ClayForm.Group>
		);
	}

	const label = (
		<label htmlFor={id}>
			{field.label}

			{field.required && <RequiredMark />}
		</label>
	);

	if (field.businessType === 'MultiselectPicklist') {
		const values = Array.isArray(value) ? value : [];

		return (
			<ClayForm.Group className="mb-0" role="group">
				{label}

				{(field.options ?? []).map((option) => (
					<ClayCheckbox
						checked={values.includes(option.value)}
						key={option.value}
						label={option.label}
						onChange={(event) =>
							onChange(
								event.target.checked
									? [...values, option.value]
									: values.filter(
											(curValue) =>
												curValue !== option.value
										)
							)
						}
					/>
				))}
			</ClayForm.Group>
		);
	}

	if (field.businessType === 'Picklist') {
		return (
			<ClayForm.Group className="mb-0">
				{label}

				<ClaySelectWithOption
					id={id}
					onChange={(event) => onChange(event.target.value)}
					options={[
						{
							label: Liferay.Language.get('select-an-option'),
							value: '',
						},
						...(field.options ?? []),
					]}
					required={field.required}
					value={String(value ?? '')}
				/>
			</ClayForm.Group>
		);
	}

	if (
		field.businessType === 'LongText' ||
		field.businessType === 'RichText'
	) {
		return (
			<ClayForm.Group className="mb-0">
				{label}

				<ClayInput
					component="textarea"
					id={id}
					onChange={(event) => onChange(event.target.value)}
					required={field.required}
					value={String(value ?? '')}
				/>
			</ClayForm.Group>
		);
	}

	return (
		<ClayForm.Group className="mb-0">
			{label}

			<ClayInput
				id={id}
				onChange={(event) => onChange(event.target.value)}
				required={field.required}
				type={INPUT_TYPES[field.businessType] ?? 'text'}
				value={String(value ?? '')}
			/>
		</ClayForm.Group>
	);
}
