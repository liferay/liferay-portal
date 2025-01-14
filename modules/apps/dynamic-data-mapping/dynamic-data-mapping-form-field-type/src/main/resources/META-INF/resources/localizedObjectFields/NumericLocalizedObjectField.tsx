/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import React, {useState} from 'react';
import {flushSync} from 'react-dom';

import {NumericProps} from '../Numeric/Numeric';
import NumericBase from '../Numeric/NumericBase';
import LocalesDropdown, {
	EditingLocale,
} from '../util/localizable/LocalesDropdown';
import {getEditingLocales, getLocale} from './util/locales';

export default function NumericLocalizedObjectField({
	availableLocales,
	dataType,
	defaultLanguageId,
	defaultLocale,
	fieldName,
	onChange,
	value,
	...otherProps
}: NumericProps) {
	const initialEditingLocales = getEditingLocales(
		availableLocales,
		defaultLocale,
		value ?? {['en_US']: ''}
	);

	const [editingLocales, setEditingLocales] = useState<EditingLocale[]>(
		initialEditingLocales
	);

	const [currentEditingLocale, setCurrentEditingLocale] = useState({
		...getLocale(editingLocales, defaultLocale, defaultLocale.localeId),
	});

	const handleTranslationChange = (localeId: Liferay.Language.Locale) => {
		if (typeof value === 'object' && !Object.hasOwn(value, localeId)) {
			flushSync(() => {
				onChange({
					target: {
						value: {
							...value,
							[localeId]: value[defaultLanguageId],
						},
					},
				});
			});
		}

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
		<>
			<NumericBase
				{...otherProps}
				dataType={dataType}
				defaultLanguageId={defaultLanguageId}
				defaultLocale={defaultLocale}
				editingLocale={currentEditingLocale.localeId}
				fieldName={fieldName}
				onChange={onChange}
				value={value}
			/>

			<ClayInput.GroupItem shrink>
				<LocalesDropdown
					availableLocales={editingLocales}
					editingLocale={currentEditingLocale}
					fieldName={fieldName}
					onLanguageClicked={handleTranslationChange}
				/>
			</ClayInput.GroupItem>
		</>
	);
}
