/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import classNames from 'classnames';
import {InputLocalized} from 'frontend-js-components-web';
import React, {useState} from 'react';

type Translations = Liferay.Language.LocalizedValue<string>;

export function LocalizedInput({
	className,
	disabled,
	formGroupClassName,
	id,
	label = '',
	onSave,
	required,
	translations: initialTranslations,
	...otherProps
}: {
	className?: string;
	disabled?: boolean;
	formGroupClassName?: string;
	id?: string;
	label?: string;
	onSave: (translations: Translations) => void;
	required?: boolean;
	translations: Translations;
}) {
	const [translations, setTranslations] =
		useState<Translations>(initialTranslations);

	const [locale, setLocale] = useState(
		Liferay.ThemeDisplay.getDefaultLanguageId()
	);

	const hasError =
		required && locale in translations && !translations[locale];

	return (
		<ClayForm.Group
			className={classNames(formGroupClassName, {'has-error': hasError})}
		>
			<InputLocalized
				{...otherProps}
				className={className}
				disabled={disabled}
				error={
					hasError
						? Liferay.Language.get('this-field-is-required')
						: ''
				}
				id={id}
				label={label}
				onBlur={() => onSave(translations)}
				onChange={(nextTranslations, {label: nextLocale}) => {
					let translations: Translations = nextTranslations;

					// Clean empty translations and save if switching locale

					if (nextLocale !== locale) {
						translations = normalizeTranslations(translations);

						setLocale(nextLocale);
						onSave(translations);
					}

					// Save and call onChange

					setTranslations(translations);
				}}
				required={required}
				translations={translations}
			/>
		</ClayForm.Group>
	);
}

function normalizeTranslations(translations: Translations) {
	return Object.fromEntries(
		Object.entries(translations).filter(
			([locale, value]) =>
				locale === Liferay.ThemeDisplay.getDefaultLanguageId() || value
		)
	);
}
