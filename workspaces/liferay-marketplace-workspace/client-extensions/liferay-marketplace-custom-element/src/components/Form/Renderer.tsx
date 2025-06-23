/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {memo} from 'react';
import {Params} from 'react-router-dom';

import {Operators} from '../../core/SearchBuilder';
import i18n from '../../i18n';
import Form from './index';

type AutoCompleteProps = {
	label?: string;
	onSearch: (keyword: string) => any;
	resource?: ((params: Readonly<Params<string>>) => string) | string;
	transformData?: (item: any) => any;
};

type RenderedFieldOptions = string[] | {label: string; value: string}[];

export type RendererFields = {
	disabled?: boolean;
	isCustomFilter?: boolean;
	label: string;
	name: string;
	operator?: Operators;
	optionalOperator?: Operators;
	options?: RenderedFieldOptions;
	placeholder?: string;
	removeQuoteMark?: boolean;
	requestOperator?: string;
	type:
		| 'autocomplete'
		| 'checkbox'
		| 'date'
		| 'date-range'
		| 'multiselect'
		| 'number'
		| 'select'
		| 'text'
		| 'textarea';
} & Partial<AutoCompleteProps>;

export type Options = {
	label: string;
	value: string;
};

export type FieldOptions = {[key: string]: any[]};

type RendererProps = {
	fieldOptions?: FieldOptions;
	fields: RendererFields[];
	filterSchema: string;
	form: any;
	isLoading?: boolean;
	onApply: () => void;
	onChange: (event: any) => void;
};

const RenderField = ({
	field,
	fieldOptions,
	form,
	isLoading = false,
	onApply,
	onChange,
}: Omit<RendererProps, 'fields'> & {field: RendererFields}) => {
	const {disabled, label, name, options = [], type} = field;

	const currentValue = form[name];

	const isFieldDisabled = () =>
		disabled ?? currentValue.includes(i18n.sub('no-x', field.label));

	const getFieldValue = () =>
		currentValue === i18n.sub('no-x', field.label) ? '' : currentValue;

	const getOptions = () =>
		fieldOptions?.[name] ||
		(options || []).map((option) =>
			typeof option === 'object'
				? option
				: {
						label: option,
						value: option,
					}
		);

	if (type === 'date-range') {
		return (
			<div className="my-1">
				<Form.DateRange
					label={label}
					onChange={(
						event: string,
						setValue: React.Dispatch<React.SetStateAction<string>>
					) => {
						setValue(event);

						onChange({
							target: {
								name,
								type: 'date-range',
								value: event,
							},
						});
					}}
					value={currentValue[0]?.value || currentValue}
				/>
			</div>
		);
	}

	if (['date', 'number', 'text', 'textarea'].includes(type)) {
		return (
			<Form.Input
				disabled={isFieldDisabled()}
				onChange={onChange}
				onKeyDown={(event) => {
					if (event.key === 'Enter' && type !== 'textarea') {
						onApply();
					}
				}}
				value={getFieldValue()}
				{...(field as any)}
			/>
		);
	}

	if (type === 'select') {
		return (
			<Form.Select
				disabled={disabled}
				isLoading={isLoading}
				label={label}
				name={name}
				onChange={onChange}
				options={getOptions()}
				value={currentValue[0]?.value || currentValue}
			/>
		);
	}

	if (type === 'checkbox') {
		const onCheckboxChange = (event: any) => {
			const labelValue = event.target.labels[0].innerText;
			const inputValue = event.target.value;

			const formValue: unknown[] = Array.isArray(form[name])
				? [...form[name]]
				: [];

			const simplifiedFormValue = formValue.map((item: any) =>
				typeof item === 'object' && item !== null ? item.value : item
			);

			const newEntry =
				inputValue !== labelValue
					? {label: labelValue, value: inputValue}
					: inputValue;

			const isSelected = simplifiedFormValue.includes(inputValue);

			const newValue = isSelected
				? formValue.filter((item: any) =>
						typeof item === 'object'
							? item.value !== inputValue
							: item !== inputValue
					)
				: [...formValue, newEntry];

			onChange({
				target: {
					name,
					value: newValue,
				},
			});
		};

		return (
			<div>
				<label>{label}</label>

				{options.map((option, index) => {
					const optionValue =
						typeof option === 'string' ? option : option.value;

					return (
						<Form.Checkbox
							checked={
								Array.isArray(form[name]) &&
								form[name].some((option: Options | string) =>
									typeof option === 'string'
										? option === optionValue
										: option.value === optionValue
								)
							}
							disabled={disabled}
							key={index}
							label={
								typeof option === 'string'
									? option
									: option.label
							}
							name={name}
							onChange={onCheckboxChange}
							value={optionValue}
						/>
					);
				})}
			</div>
		);
	}

	if (type === 'multiselect') {
		return (
			<div className="my-2">
				<label>{label}</label>

				<Form.MultiSelect
					disabled={disabled}
					isLoading={field.resource ? isLoading : false}
					name={name}
					onChange={onChange}
					options={getOptions()}
					value={currentValue}
				/>
			</div>
		);
	}

	return null;
};

const Renderer: React.FC<RendererProps> = ({fields, ...otherProps}) => (
	<div className="form-renderer">
		{fields.map((field, index) => (
			<div className="mb-4" key={index}>
				<RenderField {...otherProps} field={field} />
			</div>
		))}
	</div>
);

export default memo(Renderer);
