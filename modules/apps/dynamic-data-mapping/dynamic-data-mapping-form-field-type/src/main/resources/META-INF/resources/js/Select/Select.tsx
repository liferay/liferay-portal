/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useFormState} from 'data-engine-js-components-web';
import React, {useMemo} from 'react';

import {ReactFieldBase as FieldBase} from '../api/api';
import SelectLocalizedObjectField, {
	SelectLocalizedObjectFieldProps,
} from '../localizedObjectFields/SelectLocalizedObjectField';
import {normalizeValue} from '../util/options';
import MultipleSelection, {MultipleSelectionProps} from './MultipleSelect';
import SingleSelectBase from './SingleSelectBase';
import {useNormalizedOptionsMemo} from './hooks';
import {SelectMainProps} from './select.d';
import {toArray} from './selectOperations';

import type {Locale} from '../types';

const Select = ({
	defaultLanguageId,
	displayErrors,
	fixedOptions = [],
	label,
	localizedValue = {},
	localizedValueEdited,
	multiple = false,
	name,
	onChange,
	id,
	onSelectionChange,
	options = [],
	placeholder = Liferay.Language.get('choose-an-option'),
	predefinedValue = [],
	readOnly = false,
	showEmptyOption = true,
	valid,
	value,
	selectedKey,
	...otherProps
}: SelectMainProps) => {
	const {editingLanguageId}: {editingLanguageId: Locale} = useFormState();
	const predefinedValueArray = toArray(predefinedValue);
	const valueArray = toArray(value as string | string[]);
	const {viewMode} = useFormState();

	const normalizedOptions = useNormalizedOptionsMemo({
		editingLanguageId,
		fixedOptions,
		multiple,
		options,
		showEmptyOption,
		valueArray,
	});

	const multipleSelectValues = useMemo(
		() =>
			normalizeValue({
				localizedValueEdited,
				multiple,
				normalizedOptions,
				predefinedValueArray,
				valueArray,
			}) as string[],
		[
			localizedValueEdited,
			multiple,
			normalizedOptions,
			predefinedValueArray,
			valueArray,
		]
	);

	let newValue: string | string[] | undefined = valueArray;
	let newPredefinedValue = predefinedValueArray;

	if (!multiple) {
		if (
			normalizedOptions.length &&
			newValue?.[0] &&
			!normalizedOptions.find((option) => option.value === newValue?.[0])
		) {
			newValue = undefined;
		}

		if (
			normalizedOptions.length &&
			predefinedValueArray[0] &&
			!normalizedOptions.find(
				(option) => option.value === predefinedValueArray[0]
			)
		) {
			newPredefinedValue = [];
		}
	}

	const MultipleSelectionComponent =
		MultipleSelection as React.FC<MultipleSelectionProps>;

	return (
		<FieldBase
			displayErrors={displayErrors}
			label={label}
			localizedValue={localizedValue}
			name={name}
			readOnly={readOnly}
			valid={valid}
			{...otherProps}
		>
			{multiple ? (
				<MultipleSelectionComponent
					defaultLanguageId={defaultLanguageId}
					displayErrors={displayErrors}
					fixedOptions={[]}
					label={label}
					name={name}
					onChange={onChange}
					options={normalizedOptions}
					predefinedValue={predefinedValueArray}
					readOnly={readOnly}
					valid={valid}
					value={
						viewMode || !!multipleSelectValues.length
							? multipleSelectValues
							: (predefinedValue as string[])
					}
					{...otherProps}
				/>
			) : (
				<SingleSelectBase
					defaultLanguageId={defaultLanguageId}
					displayErrors={displayErrors}
					fixedOptions={fixedOptions}
					id={id}
					label={label}
					multiple={multiple}
					name={name}
					onSelectionChange={(itemKey: React.Key) => {
						let newItemKey: React.Key | null = itemKey;

						if ((itemKey as string)?.includes('$.')) {
							newItemKey = '.';
						}

						const field = normalizedOptions.find(
							({value}) => value === newItemKey
						);

						if (field && field.value === 'chooseAnOption') {
							onChange({}, []);
						}
						else {
							onChange({}, [field ? field.value : itemKey]);
						}

						if (onSelectionChange) {
							onSelectionChange(itemKey);
						}
					}}
					options={normalizedOptions}
					placeholder={placeholder}
					predefinedValue={newPredefinedValue}
					readOnly={readOnly}
					selectedKey={selectedKey ?? newValue}
					showEmptyOption={showEmptyOption}
					valid={valid}
					viewMode={viewMode}
					{...otherProps}
				/>
			)}

			<input
				name={name}
				type="hidden"
				value={
					multiple
						? JSON.stringify(newValue)
						: newValue?.[0] === 'chooseAnOption'
							? undefined
							: JSON.stringify(newValue)
				}
			/>
		</FieldBase>
	);
};

const Main = (props: SelectMainProps | SelectLocalizedObjectFieldProps) => {
	const isLocalizedObjectField: boolean =
		Liferay.FeatureFlags['LPD-32050'] && !!props.localizedObjectField;

	return !isLocalizedObjectField ? (
		<Select {...(props as SelectMainProps)} />
	) : (
		<SelectLocalizedObjectField
			{...(props as SelectLocalizedObjectFieldProps)}
		/>
	);
};

export default Main;
