/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LanguagePicker, Provider} from '@clayui/core';
import React from 'react';

import {AvailableLanguages, config} from '../config';

interface Props {
	availableLanguages: AvailableLanguages;
	onChange: (languageId: Liferay.Language.Locale) => void;
	selectedLanguageId: Liferay.Language.Locale;
}

export default function LocaleSelector({
	availableLanguages,
	onChange,
	selectedLanguageId,
}: Props) {
	const locales = Object.entries(availableLanguages).map(
		([id, language]) => ({
			id,
			label: language?.w3cLanguageId ?? id,
			symbol: language?.languageIcon ?? '',
		})
	);

	if (!locales.length) {
		return null;
	}

	const spritemap = `${Liferay.ThemeDisplay.getPathThemeImages()}/clay/icons.svg`;

	return (
		<Provider spritemap={spritemap}>
			<LanguagePicker
				defaultLocaleId={config.defaultLanguageId}
				hideTriggerText
				locales={locales}
				onSelectedLocaleChange={(key) =>
					onChange(key as Liferay.Language.Locale)
				}
				selectedLocaleId={selectedLanguageId}
				small
				spritemap={spritemap}
			/>
		</Provider>
	);
}
