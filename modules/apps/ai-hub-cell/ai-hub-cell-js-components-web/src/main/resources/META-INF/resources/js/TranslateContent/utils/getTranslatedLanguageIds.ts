/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AUTO_TRANSLATABLE_TYPES} from './constants';

export function getTranslatedLanguageIds(languageIds: string[]): string[] {
	return languageIds.filter((languageId) =>
		AUTO_TRANSLATABLE_TYPES.some(({type}) =>
			Array.from(
				document.querySelectorAll<HTMLInputElement>(
					`[data-localizable="true"][data-field-type="${type}"] [type="hidden"][name$="_${languageId}"]`
				)
			).some((input) => Boolean(input.value))
		)
	);
}
