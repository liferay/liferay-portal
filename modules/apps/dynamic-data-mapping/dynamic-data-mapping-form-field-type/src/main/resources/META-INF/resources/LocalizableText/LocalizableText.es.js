/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {useFormState} from 'data-engine-js-components-web';
import React, {useEffect, useState} from 'react';

import FieldBase from '../FieldBase/ReactFieldBase.es';
import LocalesDropdown from '../util/localizable/LocalesDropdown';
import {
	convertValueToJSON,
	getEditingValue,
	getInitialInternalValue,
	normalizeLocaleId,
	transformAvailableLocales,
	transformAvailableLocalesAndValue,
	transformEditingLocale,
} from '../util/localizable/transform.es';
import InputComponent from './InputComponent.es';

const INITIAL_DEFAULT_LOCALE = {
	icon: themeDisplay.getDefaultLanguageId(),
	localeId: themeDisplay.getDefaultLanguageId(),
};
const INITIAL_EDITING_LOCALE = {
	icon: normalizeLocaleId(themeDisplay.getDefaultLanguageId()),
	localeId: themeDisplay.getDefaultLanguageId(),
};

const LocalizableText = ({
	availableLocales = [],
	defaultLocale = INITIAL_DEFAULT_LOCALE,
	displayStyle = 'singleline',
	editingLocale = INITIAL_EDITING_LOCALE,
	fieldName,
	id,
	label,
	name,
	onFieldBlurred,
	onFieldChanged = () => {},
	onFieldFocused,
	placeholder = '',
	placeholdersSubmitLabel = [],
	predefinedValue = '',
	readOnly,
	value,
}) => {
	const [currentAvailableLocales, setCurrentAvailableLocales] =
		useState(availableLocales);

	const [currentEditingLocale, setCurrentEditingLocale] =
		useState(editingLocale);

	const [currentValue, setCurrentValue] = useState(value);

	const [currentInternalValue, setCurrentInternalValue] = useState(
		getInitialInternalValue({editingLocale: currentEditingLocale, value})
	);

	const getPlaceholder = (currentEditingLocale) => {
		if (fieldName !== 'submitLabel') {
			return placeholder;
		}

		return placeholdersSubmitLabel.find(
			({localeId}) => localeId === currentEditingLocale.localeId
		).placeholderSubmitLabel;
	};

	const inputValue = currentInternalValue
		? currentInternalValue
		: predefinedValue;

	useEffect(() => {
		const translationManager = Liferay.component('translationManager');

		if (!translationManager) {
			return;
		}

		const newAvailableLocales = translationManager.get('availableLocales');

		const {availableLocales} = {
			...transformAvailableLocales(
				[...newAvailableLocales],
				defaultLocale,
				currentValue
			),
		};

		const newEditingLocale = transformEditingLocale({
			defaultLocale,
			editingLocale: newAvailableLocales.get(
				translationManager.get('editingLocale')
			),
			value: currentValue,
		});

		setCurrentAvailableLocales(availableLocales);

		setCurrentEditingLocale(newEditingLocale);

		setCurrentInternalValue(
			getEditingValue({
				defaultLocale,
				editingLocale: newEditingLocale,
				fieldName,
				value: currentValue,
			})
		);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [defaultLocale, fieldName]);

	useEffect(() => {
		if (!Object.hasOwn(currentValue, editingLocale)) {
			const newEditingLocale = currentAvailableLocales.find(
				(availableLocale) =>
					availableLocale.localeId === editingLocale.localeId
			);

			setCurrentValue(convertValueToJSON(value));

			setCurrentEditingLocale({
				...newEditingLocale,
				icon: normalizeLocaleId(newEditingLocale.localeId),
			});

			setCurrentInternalValue(
				convertValueToJSON(value)[editingLocale.localeId]
			);
		}
	}, [currentAvailableLocales, currentValue, editingLocale, value]);

	return (
		<ClayInput.Group>
			<InputComponent
				dir={Liferay.Language.direction[currentEditingLocale.localeId]}
				displayStyle={displayStyle}
				fieldName={fieldName}
				id={id}
				inputValue={inputValue}
				label={label}
				name={name}
				onFieldBlurred={onFieldBlurred}
				onFieldChanged={(event) => {
					const {target} = event;
					const valueJSON = convertValueToJSON(currentValue);

					const newValue = JSON.stringify({
						...valueJSON,
						[currentEditingLocale.localeId]: target.value,
					});

					setCurrentValue(newValue);
					setCurrentInternalValue(target.value);

					const {availableLocales} = {
						...transformAvailableLocalesAndValue({
							availableLocales: currentAvailableLocales,
							defaultLocale,
							value: newValue,
						}),
					};

					setCurrentAvailableLocales(availableLocales);

					onFieldChanged({event, value: newValue});
				}}
				onFieldFocused={onFieldFocused}
				placeholder={getPlaceholder(currentEditingLocale)}
				readOnly={readOnly}
			/>

			<input
				id={id}
				name={name}
				type="hidden"
				value={currentValue || ''}
			/>

			<ClayInput.GroupItem
				className="liferay-ddm-form-field-localizable-text"
				shrink
			>
				<LocalesDropdown
					availableLocales={currentAvailableLocales}
					fieldName={fieldName}
					value={convertValueToJSON(value)}
				/>
			</ClayInput.GroupItem>
		</ClayInput.Group>
	);
};

const Main = ({
	availableLocales,
	defaultLocale,
	displayStyle,
	editingLocale,
	fieldName,
	id,
	label,
	localizedObjectField,
	name,
	onBlur,
	onChange,
	onFocus,
	placeholder,
	placeholdersSubmitLabel,
	predefinedValue,
	readOnly,
	value = {},
	...otherProps
}) => {
	const {defaultLanguageId, editingLanguageId} = useFormState();

	return (
		<FieldBase
			{...otherProps}
			id={id}
			label={label}
			name={name}
			readOnly={readOnly}
		>
			<LocalizableText
				{...transformAvailableLocalesAndValue({
					availableLocales,
					defaultLocale,
					value,
				})}
				defaultLocale={
					localizedObjectField
						? {localeId: defaultLanguageId}
						: defaultLocale
				}
				displayStyle={displayStyle}
				editingLocale={
					localizedObjectField
						? {localeId: editingLanguageId}
						: editingLocale
				}
				fieldName={fieldName}
				id={id}
				label={label}
				name={name}
				onFieldBlurred={onBlur}
				onFieldChanged={({event, value}) => onChange(event, value)}
				onFieldFocused={onFocus}
				placeholder={placeholder}
				placeholdersSubmitLabel={placeholdersSubmitLabel}
				predefinedValue={predefinedValue}
				readOnly={readOnly}
			/>
		</FieldBase>
	);
};

Main.displayName = 'LocalizableText';

export default Main;
