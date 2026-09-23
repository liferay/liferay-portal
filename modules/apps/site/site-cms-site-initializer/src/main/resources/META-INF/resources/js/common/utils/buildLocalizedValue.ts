/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	DefaultLanguageLabels,
	getDefaultLanguageLabel,
} from './defaultLanguageLabels';

export default function buildLocalizedValue({
	defaultLanguageLabels,
	key,
}: {
	defaultLanguageLabels: DefaultLanguageLabels;
	key: string;
}): Liferay.Language.LocalizedValue<string> {
	return {
		[Liferay.ThemeDisplay.getDefaultLanguageId()]: getDefaultLanguageLabel({
			defaultLanguageLabels,
			key,
		}),
		[Liferay.ThemeDisplay.getLanguageId()]: Liferay.Language.get(key),
	};
}
