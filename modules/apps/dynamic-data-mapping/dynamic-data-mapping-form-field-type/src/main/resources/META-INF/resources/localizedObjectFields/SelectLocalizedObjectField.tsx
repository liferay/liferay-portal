/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {stringUtils} from '@liferay/object-js-components-web';
import {useFormState} from 'data-engine-js-components-web';
import React, {useEffect, useMemo} from 'react';

import FieldBase from '../FieldBase/ReactFieldBase.es';
import SingleSelectBase from '../Select/SingleSelectBase';
import {useNormalizedOptionsMemo} from '../Select/hooks';
import {SelectMainProps} from '../Select/select.d';
import {LocalizedValue} from '../types';
import {isEmptyObject} from '../util/basicJsUtils';
import LocalesDropdown, {
	AvailableLocale,
} from '../util/localizable/LocalesDropdown';

import './SelectLocalizedObjectField.scss';

import type {Locale} from '../types';

type valueTypes = {} | LocalizedValue<string>;

export interface SelectLocalizedObjectFieldProps
	extends Omit<SelectMainProps, 'value'> {
	availableLocales: AvailableLocale[];
	value: valueTypes;
}

function normalizeValues(
	defaultLocaleId: Locale,
	predefinedValue: string | string[] | undefined,
	values: LocalizedValue<string[] | string>
): LocalizedValue<string> {
	if (isEmptyObject(values)) {
		if (Array.isArray(predefinedValue) && predefinedValue.length) {
			return {
				[defaultLocaleId]: predefinedValue[0],
			};
		}

		return {
			[defaultLocaleId]:
				typeof predefinedValue === 'string' ? predefinedValue : '',
		};
	}

	const normalizedValues: LocalizedValue<string> = {};

	for (const key in values) {
		const localeKey = key as Locale;
		const localeValue = values[localeKey] as string[] | string;

		normalizedValues[localeKey] = Array.isArray(localeValue)
			? localeValue[0]
			: localeValue;
	}

	return normalizedValues;
}

export default function SelectLocalizedObjectField({
	availableLocales,
	fieldName,
	fixedOptions = [],
	id,
	label,
	name,
	onChange,
	options,
	placeholder = Liferay.Language.get('choose-an-option'),
	predefinedValue,
	readOnly,
	showEmptyOption = true,
	value,
	...otherProps
}: SelectLocalizedObjectFieldProps) {
	const {
		defaultLanguageId,
		editingLanguageId,
	}: {defaultLanguageId: Locale; editingLanguageId: Locale} = useFormState();

	const normalizedOptions = useNormalizedOptionsMemo({
		editingLanguageId,
		fixedOptions,
		multiple: false,
		options,
		showEmptyOption,
		valueArray: [(value as LocalizedValue<string>)[editingLanguageId]!],
	});

	const localizedOptions = useMemo(() => {
		return normalizedOptions.map((option) => ({
			...option,
			label: stringUtils.getLocalizableLabel({
				labels: option.labelMap,
				preferredLanguageId: editingLanguageId,
				...(!option.labelMap &&
					option.value === 'chooseAnOption' && {
						fallbackLabel: option.label,
					}),
			}),
		}));
	}, [editingLanguageId, normalizedOptions]);

	// If value from the outside state has a property pointing to an array,
	// ensure it uses the normalized values of localizedValues.

	useEffect(() => {
		if (
			!isEmptyObject(value) &&
			Object.values(value).some((arrayItem) => Array.isArray(arrayItem))
		) {
			onChange(
				{},
				{...normalizeValues(defaultLanguageId, predefinedValue, value)}
			);
		}
	}, [defaultLanguageId, onChange, predefinedValue, value]);

	const updateLocalizedValues = (localeId: Locale, newValues: React.Key) => {
		onChange(
			{},
			{
				...value,
				[localeId]: newValues === 'chooseAnOption' ? '' : newValues,
			}
		);
	};

	const handleChange = (newValues: React.Key) => {
		updateLocalizedValues(editingLanguageId, newValues);
	};

	return (
		<FieldBase
			label={label}
			name={name}
			readOnly={readOnly}
			{...otherProps}
		>
			<ClayInput.Group>
				<SingleSelectBase
					{...otherProps}
					className="ddm-object-field-single-select-localized"
					defaultLanguageId={defaultLanguageId}
					fieldName={fieldName}
					id={id}
					label={label}
					name={name}
					onSelectionChange={handleChange}
					options={localizedOptions}
					placeholder={placeholder}
					readOnly={readOnly}
					selectedKey={
						normalizeValues(
							defaultLanguageId,
							predefinedValue,
							value
						)[editingLanguageId]
					}
					showEmptyOption={showEmptyOption}
				/>

				<ClayInput.GroupItem shrink>
					<LocalesDropdown
						availableLocales={availableLocales}
						fieldName={fieldName}
						value={value}
					/>
				</ClayInput.GroupItem>
			</ClayInput.Group>
		</FieldBase>
	);
}
