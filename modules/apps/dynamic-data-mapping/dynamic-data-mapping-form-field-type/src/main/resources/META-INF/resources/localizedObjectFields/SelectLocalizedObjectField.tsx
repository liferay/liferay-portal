/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {stringUtils} from '@liferay/object-js-components-web';
import React, {useEffect, useMemo, useState} from 'react';

import FieldBase from '../FieldBase/ReactFieldBase.es';
import SingleSelectBase from '../Select/SingleSelectBase';
import {useNormalizedOptionsMemo} from '../Select/hooks';
import {SelectMainProps} from '../Select/select.d';
import {LocalizedValue} from '../types';
import {isEmptyObject} from '../util/basicJsUtils';
import LocalesDropdown, {
	AvailableLocale,
	EditingLocale,
} from '../util/localizable/LocalesDropdown';
import {getEditingLocales, getLocale} from './util/locales';

import './SelectLocalizedObjectField.scss';

import type {Locale} from '../types';

type valueTypes = {} | LocalizedValue<string>;

export interface SelectLocalizedObjectFieldProps
	extends Omit<SelectMainProps, 'value'> {
	availableLocales: AvailableLocale[];
	defaultLocale: AvailableLocale;
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
	defaultLanguageId,
	defaultLocale,
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
	const values = normalizeValues(defaultLanguageId, predefinedValue, value);

	const [editingLocales, setEditingLocales] = useState<EditingLocale[]>(
		getEditingLocales(availableLocales, defaultLocale, values)
	);

	const [currentEditingLocale, setCurrentEditingLocale] =
		useState<EditingLocale>({
			...getLocale(editingLocales, defaultLocale, defaultLanguageId),
		});

	const [localizedValues, setLocalizedValues] =
		useState<LocalizedValue<string>>(values);

	const normalizedOptions = useNormalizedOptionsMemo({
		editingLanguageId: currentEditingLocale.localeId,
		fixedOptions,
		multiple: false,
		options,
		showEmptyOption,
		valueArray: [localizedValues[currentEditingLocale.localeId]!],
	});

	const localizedOptions = useMemo(() => {
		return normalizedOptions.map((option) => ({
			...option,
			label: stringUtils.getLocalizableLabel({
				labels: option.labelMap,
				preferredLanguageId: currentEditingLocale.localeId,
				...(!option.labelMap &&
					option.value === 'chooseAnOption' && {
						fallbackLabel: option.label,
					}),
			}),
		}));
	}, [normalizedOptions, currentEditingLocale.localeId]);

	// If value from the outside state has a property pointing to an array,
	// ensure it uses the normalized values of localizedValues.

	useEffect(() => {
		if (
			!isEmptyObject(value) &&
			Object.values(value).some((arrayItem) => Array.isArray(arrayItem))
		) {
			onChange({}, {...localizedValues});
		}
	}, [localizedValues, onChange, value, values]);

	const updateLocalizedValues = (localeId: Locale, newValues: React.Key) => {
		const newLocalizedValues = {
			...localizedValues,
			[localeId]: newValues === 'chooseAnOption' ? '' : newValues,
		};

		setLocalizedValues(newLocalizedValues);

		onChange({}, newLocalizedValues);
	};

	const handleChange = (newValues: React.Key) => {
		updateLocalizedValues(currentEditingLocale.localeId, newValues);
	};

	const handleTranslationChange = (localeId: Liferay.Language.Locale) => {
		const currentLocale = getLocale(
			editingLocales,
			defaultLocale,
			localeId
		);

		const updatedLocale = {...currentLocale, isTranslated: true};

		setEditingLocales((previous) =>
			previous.map((locale) =>
				locale.localeId === localeId ? updatedLocale : locale
			)
		);

		setCurrentEditingLocale(updatedLocale);
	};

	return (
		<FieldBase
			label={label}
			localizedValue={localizedValues}
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
					selectedKey={localizedValues[currentEditingLocale.localeId]}
					showEmptyOption={showEmptyOption}
				/>

				<ClayInput.GroupItem shrink>
					<LocalesDropdown
						availableLocales={editingLocales}
						editingLocale={currentEditingLocale}
						fieldName={fieldName}
						onLanguageClicked={handleTranslationChange}
					/>
				</ClayInput.GroupItem>
			</ClayInput.Group>
		</FieldBase>
	);
}
