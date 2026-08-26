/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLocalizedInput from '@clayui/localized-input';
import classNames from 'classnames';
import React, {FocusEventHandler, useEffect, useMemo, useState} from 'react';

import FieldBase from '../common/FieldBase';

import './InputLocalized.scss';

interface InputLocalizedProps {
	className?: string;
	component?: 'input' | 'textarea';
	disabled?: boolean;
	error?: string;
	helpMessage?: string;
	id?: string;
	label: string;
	name?: string;
	onBlur?: FocusEventHandler<HTMLInputElement>;
	onChange: (
		value: Liferay.Language.LocalizedValue<string>,
		locale: LocaleItem
	) => void;
	onSelectedLocaleChange?: (locale: Liferay.Language.Locale) => void;
	placeholder?: string;
	required?: boolean;
	resultFormatter?: (value: string) => React.ReactNode;
	selectedLocale?: Liferay.Language.Locale;
	tooltip?: string;
	translations: Liferay.Language.LocalizedValue<string> &
		Partial<{
			zh_Hans_CN: string;
			zh_Hant_TW: string;
		}>;
}

interface LocaleItem {
	label: Liferay.Language.Locale;
	symbol: string;
}

export function translationsNormalizer(
	translations: Liferay.Language.LocalizedValue<string>
): Liferay.Language.LocalizedValue<string> {
	const {zh_Hans_CN, zh_Hant_TW, ...normalizedTranslations} = translations;

	if (zh_Hans_CN) {
		normalizedTranslations['zh_CN'] = zh_Hans_CN;
	}

	if (zh_Hant_TW) {
		normalizedTranslations['zh_TW'] = zh_Hant_TW;
	}

	return normalizedTranslations;
}

export default function InputLocalized({
	className,
	disabled,
	error,
	helpMessage,
	id,
	label,
	name,
	onBlur,
	onChange,
	onSelectedLocaleChange,
	placeholder,
	required,
	resultFormatter = () => null,
	selectedLocale,
	tooltip,
	translations,
	...otherProps
}: InputLocalizedProps) {
	const availableLocales = useMemo(() => {
		return Object.keys(Liferay.Language.available)
			.sort((languageId: string) =>
				languageId === Liferay.ThemeDisplay.getDefaultLanguageId()
					? -1
					: 1
			)
			.map((languageId: string) => ({
				label: languageId as Liferay.Language.Locale,
				symbol: languageId.replace(/_/g, '-').toLowerCase(),
			}));
	}, []);

	const [selectedLocaleItem, setSelectedLocaleItem] = useState<LocaleItem>(
		availableLocales[0]
	);

	const normalizedTranslations = translationsNormalizer(translations);

	useEffect(() => {
		setSelectedLocaleItem(
			availableLocales.find(({label}) => label === selectedLocale)! ??
				availableLocales[0]
		);
	}, [availableLocales, selectedLocale]);

	return (
		<FieldBase
			className="input-localized"
			disabled={disabled}
			errorMessage={error}
			helpMessage={helpMessage}
			id={id}
			label={label}
			required={required}
			tooltip={tooltip}
		>
			<ClayLocalizedInput
				{...otherProps}
				className={classNames(className, {
					'input-localized--rtl':
						Liferay.Language.direction[selectedLocaleItem.label] ===
						'rtl',
				})}
				disabled={disabled}
				id={id}
				label=""
				locales={availableLocales}
				name={name}
				onBlur={onBlur}
				onSelectedLocaleChange={(newLocale) => {
					setSelectedLocaleItem(newLocale as LocaleItem);

					onChange(normalizedTranslations, newLocale as LocaleItem);

					if (onSelectedLocaleChange) {
						onSelectedLocaleChange((newLocale as LocaleItem).label);
					}
				}}
				onTranslationsChange={(newTranslations) => {
					onChange(newTranslations, selectedLocaleItem);
				}}
				placeholder={placeholder}
				resultFormatter={resultFormatter}
				selectedLocale={selectedLocaleItem}
				translations={normalizedTranslations}
			/>
		</FieldBase>
	);
}
