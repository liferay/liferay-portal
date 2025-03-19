/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import React, {useState} from 'react';
import {flushSync} from 'react-dom';

import CheckboxBase, {ICheckboxBaseProps} from '../Checkbox/CheckboxBase';
import LocalesDropdown, {
	EditingLocale,
} from '../util/localizable/LocalesDropdown';
import {getEditingLocales, getLocale} from './util/locales';

import type {FieldChangeEventHandler, LocalizedValue} from '../types';

export default function CheckboxLocalizedObjectField(props: IProps) {
	const {availableLocales, defaultLocale, fieldName, onChange, value} = props;

	const initialEditingLocales = getEditingLocales(
		availableLocales,
		defaultLocale,
		value
	);

	const [editingLocales, setEditingLocales] = useState<EditingLocale[]>(
		initialEditingLocales
	);

	const [currentEditingLocale, setCurrentEditingLocale] = useState({
		...getLocale(editingLocales, defaultLocale, defaultLocale.localeId),
	});

	const checked = !!value[currentEditingLocale.localeId];

	const handleCheckboxToggle: FieldChangeEventHandler<
		LocalizedValue<boolean>
	> = (event) => {
		const eventValue = event.target.value;

		const newValue = {
			...value,
			[currentEditingLocale.localeId]: eventValue,
		};

		onChange({target: {value: newValue}});
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
		<ClayInput.Group>
			<ClayInput.GroupItem>
				<CheckboxBase
					{...props}
					checked={checked}
					onChange={handleCheckboxToggle}
				/>
			</ClayInput.GroupItem>

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

export interface IProps extends ICheckboxBaseProps {
	availableLocales: EditingLocale[];
	defaultLocale: EditingLocale;
	fieldName: string;
	onChange: FieldChangeEventHandler<LocalizedValue<boolean>>;
	systemSettingsURL: string;
	value: LocalizedValue<boolean>;
}
