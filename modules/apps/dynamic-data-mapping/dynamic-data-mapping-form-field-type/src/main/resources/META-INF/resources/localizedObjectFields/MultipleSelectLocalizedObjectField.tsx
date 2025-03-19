/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {stringUtils} from '@liferay/object-js-components-web';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import {MultipleSelectBase} from '../Select/MultipleSelectBase';
import {MultipleSelectBaseProps} from '../Select/select.d';
import {LocalizedValue} from '../types';
import LocalesDropdown, {
	AvailableLocale,
	EditingLocale,
} from '../util/localizable/LocalesDropdown';
import {getEditingLocales, getLocale} from './util/locales';

import type {Locale} from '../types';

type valueTypes = string[] | LocalizedValue<string[]>;

export interface MultipleSelectLocalizedObjectFieldProps
	extends MultipleSelectBaseProps<string[] | LocalizedValue<string[]>> {
	availableLocales: AvailableLocale[];
	defaultLocale: EditingLocale;
}

function getDefaultValue(locale: Locale, values: valueTypes) {
	return Array.isArray(values) ? {[locale]: values} : values;
}

export default function MultipleSelectLocalizedObjectField({
	availableLocales,
	defaultLanguageId,
	defaultLocale,
	errorMessage,
	fieldName,
	id,
	label,
	name,
	onChange,
	options,
	readOnly,
	required,
	tip,
	value: values,
}: MultipleSelectLocalizedObjectFieldProps) {
	const [editingLocales, setEditingLocales] = useState<EditingLocale[]>(
		getEditingLocales(
			availableLocales,
			defaultLocale,
			getDefaultValue(defaultLanguageId, values)
		)
	);

	const [currentEditingLocale, setCurrentEditingLocale] =
		useState<EditingLocale>({
			...getLocale(editingLocales, defaultLocale, defaultLocale.localeId),
		});

	const currentEditingLocaleIdRef = useRef<Locale>(
		currentEditingLocale.localeId
	);

	const [localizedValues, setLocalizedValues] = useState<
		LocalizedValue<string[]>
	>(getDefaultValue(currentEditingLocale.localeId, values));

	useEffect(() => {
		currentEditingLocaleIdRef.current = currentEditingLocale.localeId;
	}, [currentEditingLocale]);

	const updateLocalizedValues = (localeId: Locale, items: string[]) => {
		const newLocalizedValues = {
			...localizedValues,
			[localeId]: items,
		};
		setLocalizedValues(newLocalizedValues);

		onChange({}, newLocalizedValues);
	};

	const handleChange = (_: object, uniqueItems: string[]) => {
		updateLocalizedValues(currentEditingLocaleIdRef.current, uniqueItems);
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

	const localizedOptions = useMemo(() => {
		return options.map((option) => ({
			...option,
			label: stringUtils.getLocalizableLabel({
				labels: option.labelMap,
				preferredLanguageId: currentEditingLocale.localeId,
			}),
		}));
	}, [options, currentEditingLocale.localeId]);

	const handleAsyncOptions = useCallback(() => {
		return new Promise((resolve) => {
			resolve(localizedOptions);
		});
	}, [localizedOptions]);

	return (
		<ClayInput.Group>
			<MultipleSelectBase
				defaultLanguageId={defaultLanguageId}
				errorMessage={errorMessage}
				fieldName={fieldName}
				id={id}
				label={label}
				name={name}
				onChange={handleChange}
				onLoadMore={handleAsyncOptions}
				options={localizedOptions}
				readOnly={readOnly}
				required={required}
				tip={tip}
				value={localizedValues[currentEditingLocale.localeId] ?? ['']}
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
	);
}
